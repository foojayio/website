---
title: "Put the structure of your Spring Boot service in a YAML model: architecture changes, AI coding and BI from one source"
date: "2026-01-01"
description: "A YAML domain model that regenerates the Spring Boot code when the architecture or stack changes, keeps AI-written code consistent, and exports an Apache Ossie model for BI."
authors:
  - "richard-kim"
image: "preview.jpg"
categories:
  - "Java"
  - "Spring"
  - "Developer Tools"
  - "AI"
related_posts: []
---

I lead the ElastiCORE project. ElastiCORE is an Apache-2.0 Gradle plugin that reads a YAML model of a business domain and generates a Spring Boot application from it. This article argues one point with a working sample: if the structure of a service lives in a small model instead of in hand-written layers, three hard problems get easier.

1. **The architecture or the stack changes.** Layered to hexagonal, Spring Boot 3 to 4, Java to Kotlin, JPA to MyBatis.
2. **AI assistants write the code.** Quality should not depend on which prompt or which model produced it.
3. **BI and analytics need to know what the data means.** "Revenue" should have one definition, not one per dashboard.

The examples come from the repository's pet store sample. The YAML is real, and every Java excerpt was copied from a fresh build on the main branch just after the 3.0.261119 release, trimmed with `// ...`.

## The model

The pet store's domain is about 180 lines of YAML under `src/main/resources/blueprint/petstore/`. Here is the order (Korean descriptions trimmed):

```yaml
  PurchaseOrder:
    meta: entity @root @expose(20) @event(Placed(total), Shipped, Cancelled) @owner(customerId)
          @access(read=owner, create=user, cancel=owner)
    fields:
      orderId:         string(36) @id @genid
      customer:        Customer @ManyToOne
      # ...
      status:          OrderStatus
      creditCard:      CreditCard! @Embedded
      totalWithoutVat: money(12.2) @default(0) @compute(sum(lines.subTotal))
      # ...
      lines:           List<OrderLine> @OneToMany(mappedBy="order") @dtype(List<OrderLineDTO>) @minsize(1, 'the cart is empty')
      version:         long @jpa:Version
    states:
      status:
        initial: PLACED
        ship:    PLACED -> SHIPPED
        cancel:  PLACED -> CANCELLED
        delete:  CANCELLED
```

One line is one field. The `meta` line says the order is an aggregate root exposed over REST, owned by the customer who placed it, and readable and cancellable only by that owner. `states` is its lifecycle. Nothing here names a framework, a package layout or a persistence API. That is the point.

## 1. Architecture and technology change: regenerate, don't rewrite

Which code the model becomes is chosen in `build.gradle`, not in the model:

```gradle
elasticore {
    architecture = 'hexagonal'    // layered | ddd | hexagonal
    language     = 'java'         // java | kotlin
}
```

- **Spring Boot 3 or 4** is read from the Spring Boot plugin version. The pet store runs on Boot 4.0.8.
- **Java or Kotlin**: JPA output in all three architectures.
- **JPA, MyBatis or MongoDB**: `mode:` in the domain's `env.yml`. MyBatis covers layered, DDD and hexagonal in Java. MongoDB is newer and narrower: layered Java only, checked on Boot 3.

For the pet store, `hexagonal` produced 160 files on one build: entities, DTOs, a mapper, use-case and repository ports, JPA adapters, services, controllers, domain events, an error handler and a TypeScript client. The controller depends on the inbound port, not on a service class:

```java
@RestController
@RequestMapping("/api/petstore/purchaseOrder")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderUseCase purchaseOrderService;
    // ...
    @PostMapping("/{orderId}/ship")
    public PurchaseOrderDTO ship(@PathVariable("orderId") String orderId) {
        return purchaseOrderService.ship(orderId);
    }
```

In a hand-written code base, moving from layered to hexagonal is a rewrite of every module. To see what it costs here, I built a small shop model (a customer and an order with the same `states`, `@owner` and `@access` as above) from the Maven Central release, and changed only settings, never the YAML:

- **`hexagonal` -> `ddd`**: one line in `build.gradle`. The build wrote the new classes and deleted the ones the model no longer produces (`REMOVED(the DSL no longer produces it)` for the use-case ports). It compiled, and the same REST calls gave the same answers: 401 when not signed in, 403 for a customer shipping, 200 on cancel, 409 on cancelling twice.
- **Spring Boot 3.5 -> 4.0**: the Boot plugin version, the runtime artifact (`elasticore-springboot3` -> `-springboot4`), `starter-web` -> `starter-webmvc` and springdoc 2 -> 3. Of 35 generated files, 3 changed (the two enums and the error handler, now written for Jackson 3); the other 32 stayed byte-identical. Same status codes and messages on Boot 4.
- **Java -> Kotlin**: `language = 'kotlin'` plus the Kotlin Gradle plugins. 35 `.kt` files, compiled, same REST answers.

Hand-written code is where a switch costs work, so ElastiCORE keeps it apart. A class like `PurchaseOrderBase` is regenerated on every build, while `PurchaseOrder extends PurchaseOrderBase`, the services and the controllers are generated once and then belong to you (in the pet store, the order holds the shop's VAT and discount policy). In my run I had added a method to the service and an endpoint to the controller while the project was layered. Switching to hexagonal merged both files - new base class and ports, my code kept - but my endpoint now called the service through the generated `PurchaseOrderUseCase` interface, which does not have my method. That was a one-line fix by hand. The lesson is the usual one: keep hand code small and close to the model.

One more honest caveat. `states` and the owner check are enforced in `ddd` and `hexagonal`; in `layered` the build warns that it does not enforce them. Switching architectures is cheap, but read the build warnings.

### Hand edits survive regeneration

Generated code you can never touch is a trap. ElastiCORE 3.0 merges instead, the way git merges two branches. It keeps what it last wrote in `.elasticore/cache`, committed with the sources, and on each build does a three-way merge: the cached version is the base, your file is one side, the newly generated file the other.

To check this, I generated a one-entity project from Maven Central, added a method to the generated `Product` entity by hand, then added a field to the YAML (`stock: int @default(0)`). The next build printed:

```
    entity       src/main/java/com/acme/shop/domain/model/Product.java  MERGED(your edits kept)
=== merge: 1 merged with your edits / 0 kept as yours / 0 conflict(s) / 0 still stamped (edited, base not confirmed) ===
```

The entity had both the new `stock` column and my method. If the model changes the very line you edited, you get ordinary `<<<<<<< yours` / `>>>>>>> elasticore` markers and the build fails until you resolve them.

## 2. AI coding: the model is the prompt, the generator is the quality bar

Ask an AI assistant for the same CRUD-plus-rules service twice and you get two designs: different exception types, a different paging contract, one with an ownership check and one without. Every change then needs a careful review of code nobody wrote by hand.

With a model, the assistant's job shrinks to editing YAML. The generator is deterministic: the same model and generator version produce the same files, byte for byte. I checked it by generating the shop project in two fresh directories: 38 generated files, every one with the same SHA-256. And after going layered -> hexagonal -> ddd -> layered, the generated files were byte-identical to the first layered build. Whatever assistant or prompt changed the model, the Java that comes out has the same structure, the same error handling and the same security checks. You review a model diff of a few lines, not 160 files.

The rules are generated, so they cannot be forgotten. The `states` block becomes commands on the aggregate, with a protected setter so no service can jump an order from `CANCELLED` back to `PLACED`:

```java
	public void ship() {
	    moveStatus("ship", OrderStatus.SHIPPED, OrderStatus.PLACED);
	    raiseShipped();
	}

	/** Whether ship may be given now: the status is PLACED. */
	public boolean canShip() {
	    return this.status == OrderStatus.PLACED;
	}
```

`@access(read=owner, create=user, cancel=owner)` becomes a policy object and a check in every operation:

```java
    @Transactional
    public PurchaseOrderDTO ship(String orderId) {
        PurchaseOrder entity = repository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("PurchaseOrder " + orderId + " not found"));
        accessWrite(entity, "ship");
        entity.ship();
        entity.derive(entityManager::find);
        return PetstoreMapper.toDTO(readBack(repository.save(entity)));
    }

    private void accessWrite(PurchaseOrder entity, String operation) {
        ACCESS.require(currentUser, operation);
        if (!ACCESS.owns(currentUser, operation, entity.getCustomerId()))
            throw new EntityNotFoundException("PurchaseOrder not found");
    }
```

A customer who tries to cancel someone else's order gets 404, not 403, so the order's existence does not leak. With the sample running, these are the responses I got:

```bash
# Not signed in
curl -i -X POST localhost:18080/api/petstore/purchaseOrder -H 'Content-Type: application/json' -d @order.json
# 401 {"detail":"Sign in to create PurchaseOrder",...,"code":"not-signed-in"}

# Signed in as the customer. order.json sends "unitCost":0.01; the server ignores it
curl -b cookies.txt -X POST localhost:18080/api/petstore/purchaseOrder -H 'Content-Type: application/json' -d @order.json
# {"customerId":2,...,"lines":[{"itemId":1000,"quantity":2,"subTotal":20.00,"unitCost":10.00}],"status":"PLC","total":20.63,...}

curl -i -b cookies.txt -X POST localhost:18080/api/petstore/purchaseOrder/$ID/ship
# 403 {"detail":"ship PurchaseOrder is for the administrator",...}

curl -i -b cookies.txt -X POST localhost:18080/api/petstore/purchaseOrder/$ID/cancel   # 200, "status":"CAN"
curl -i -b cookies.txt -X POST localhost:18080/api/petstore/purchaseOrder/$ID/cancel   # again
# 409 {"detail":"PurchaseOrder cannot cancel when status is CANCELLED; it must be [PLACED]",...}
```

The price comes from `@snapshot(item.unitCost)` on the order line, and the card number, marked `@writeonly`, is never in a response. None of that depends on an assistant remembering to write it.

The [ElastiCORE agent kit](https://github.com/xsko/elasticore-agent-kit) (1.0.0) makes this the default workflow for Claude Code (the skill also works in opencode and Codex CLI). It gives the agent a DSL reference and recipes, and its hooks refuse `-Pforce` (which would overwrite hand edits), refuse edits to files rewritten on every build, and do not let the agent finish until a model change has been generated and compiled. The kit's README reports one small run: Claude Code finished a library-loan API in 2 min 18 s instead of 4 min 41 s, with the same checklist score, by writing a 27-line YAML instead of every layer. Treat that as an anecdote, not a study.

## 3. BI: the meaning of the data comes from the same model

A BI tool or an AI analyst sees tables and columns. It does not know that `status = 'CAN'` means cancelled, which column joins orders to customers, or whether revenue includes cancelled orders. Usually someone writes that down again for every tool, and the copies drift.

The pet store writes its business measures once, next to the entities (three of its thirteen metrics; Korean descriptions and synonyms trimmed or translated):

```yaml
metrics:
  revenue:      sum(PurchaseOrder.total) where status != CANCELLED @terms(sales) -- revenue, cancelled orders excluded
  orderCount:   count(PurchaseOrder) where status != CANCELLED @terms(orders)
  averageOrder: revenue / orderCount @terms(AOV)
```

With `rules = ['ossie-export']`, the build also writes `blueprint/semantic_model.yml` in the format of [Apache Ossie](https://github.com/apache/ossie) (incubating), a vendor-neutral spec for semantic models that BI, analytics and AI tools can read. Datasets, fields, keys and relationships come from the entities; the metrics become SQL, with the enum resolved to its stored code:

```yaml
    metrics:
      - name: revenue
        expression:
          sql: "SUM(CASE WHEN status <> 'CAN' THEN total END)"
        datatype: Decimal
        ai_context:
          synonyms: ["sales", ...]
      - name: averageOrder
        expression:
          sql: "(SUM(CASE WHEN status <> 'CAN' THEN total END)) / NULLIF((COUNT(CASE WHEN status <> 'CAN' THEN 1 END)), 0)"
# ...
relationships:
  - name: PurchaseOrder_customer
    from: PurchaseOrder
    to: Customer
    from_columns: [customer_id]
    to_columns: [customer_id]
```

The status column carries `Codes: PLC = Placed, SHP = Shipped, CAN = Cancelled` in its `ai_context`, so a tool asking for "sales" gets the business definition instead of its own guess. Fields the application never reads back (`@password`, `@writeonly` such as the card number) are left out. The sample's `SemanticModelTest` checks the exported model against the real database: every dataset is a table, every field a column, every relationship a pair of real columns.

Because the export is generated, it changes with the model. Add a field or a state and the next build updates the API, the database scripts and the semantic model together.

## When not to use it

- **UI-first applications.** ElastiCORE generates a typed TypeScript client, not screens.
- **Non-Spring stacks.** Quarkus, Micronaut or Jakarta EE servers are not targets.
- **Highly bespoke persistence.** If your queries are mostly hand-tuned SQL, the model gets in the way.
- **Logic that is not declarative.** Pricing policies, cross-service workflows and calculations stay in Java. The model covers structure, access, lifecycle, derived values and metrics; the rest goes in the files it hands you.

## Try it

The pet store, from source (JDK 17 to 22):

```bash
git clone https://github.com/xsko/ElastiCORE.git && cd ElastiCORE
./gradlew :elasticore-project:elasticore-sample-petstore:run --args="--server.port=18080"
```

Sign in as `user`/`user` or `admin`/`admin`. The semantic model is in `src/main/resources/blueprint/semantic_model.yml` of the sample.

In your own project, the artifacts are on Maven Central under `io.elasticore`, plugin included. With Spring Boot 4 you need Gradle 8.14 or later and `mavenCentral()` in `pluginManagement`:

```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '4.0.8'
    id 'io.spring.dependency-management' version '1.1.7'
    id 'io.elasticore' version '3.0.261119'
}

dependencies {
    implementation 'io.elasticore:elasticore-base:3.0.261119'
    implementation 'io.elasticore.springboot4:elasticore-springboot4:3.0.261119'
    // plus spring-boot-starter-webmvc, -data-jpa, -validation, springdoc, lombok, h2
}
```

The [README's five steps](https://github.com/xsko/ElastiCORE#into-your-project-in-five-steps) have the full files, and the [ElastiCORE site](https://www.elasticore.io) has the guides. I would like to hear where the model was not enough for your domain. That is how the DSL has grown so far.
