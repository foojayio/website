---
title: "No Database Operations Found: What Writing a Spring Boot Analyzer Taught Me About Spring"
date: "2026-09-03T06:26:00+00:00"
description: "I spent months building a static analyzer that answers one question about a Spring Boot codebase: if I change this endpoint, what does the change reach?"
authors:
  - "braham-shakti"
image: "spring-logo.png"
categories:
  - "Debugging"
  - "Developer Tools"
  - "Java"
  - "Spring"
related_posts:
  - "statically-spilling-your-spring-beans"
  - "transactions-and-threadlocal-in-spring"
  - "a-walk-to-lazy-fetching-with-hibernate-and-spring-data-jpa"
frozen: false
---

I spent months building a static analyzer that answers one question about a Spring Boot codebase: if I change this endpoint, what does the change reach?

Every serious bug I found in it had the same shape.

Not a wrong edge in a graph. Not a missing feature. A **confident absence** — the tool stating, definitively, that an endpoint touched no database, opened no transaction, published no message, when it did all three.

That turned out to be a lesson about Spring more than a lesson about analyzers. Every one of those bugs existed because Spring runs code that nothing in the source visibly calls, and because I had read the code the same way a reviewer reads a pull request: starting at the handler, following the calls.

Here is what that misses. The numbers are from my own runs against public repositories.

## The endpoints are not where the annotations are

The first measurement that stopped me: **Spring PetClinic REST — 10 controllers, 528 methods, one endpoint found.** And that one was a `/` redirect to Swagger.

The application serves around 36 routes. `src/main/java` contains zero method-level mapping annotations.

They live on interfaces. This is what openapi-generator's `spring` generator produces with `interfaceOnly`, and plenty of teams write it by hand for a shared API contract:

```
public interface OwnersApi {
  @RequestMapping(method = RequestMethod.GET, value = "/owners")
  ResponseEntity<List<OwnerDto>> listOwners();
}

@RestController
@RequestMapping("/api")
public class OwnerRestController implements OwnersApi {
  @Override
  public ResponseEntity<List<OwnerDto>> listOwners() { ... }
}
```

The served route is `/api/owners`. Note the asymmetry: **the route comes from the interface, the base path from the implementing class.** Grep the controller and you find nothing.

The second measurement was worse. **halo — 1,349 Java files, 13 endpoints found, against 183 route declarations across 58 production files.**

Those routes are functional:

```
RouterFunctions.route()
  .nest(path("/posts"), () -> RouterFunctions.route()
      .POST(handler::create)
      .GET("/{name}", handler::get)
      .build())
  .build();
```

Three separate traps in that snippet. The whole style is invisible to an annotation scan. `Builder` has a `POST(HandlerFunction)` overload with no path at all — read the first argument as a path and you throw away a route you were holding. And the path lives in the enclosing `nest`, so you need the nest's parenthesis extent, or sibling nests leak prefixes into each other.

A smaller one, same family: Spring treats `@RequestMapping("api")` and `@RequestMapping("/api")` identically. **25 of PetClinic REST's 37 routes use the first form.** Normalise or your index holds `api/pets` while every reader has `/api/pets`.

## Spring runs code nothing calls

This is the category that produces false absences, and it is the one worth internalising as a reviewer.

**`@ModelAttribute` methods run before every handler in the controller.** Measured on Spring PetClinic, after I had fixed every other false negative I knew about: eight endpoints still claimed "Database operations: None found" definitively. Six of them run this before the handler is entered:

```
@ModelAttribute("owner")
public Owner findOwner(@PathVariable Integer ownerId) {
  return this.owners.findById(ownerId);
}
```

The claim was true of the handler and false of the request. There is no call site to find it by. A call-graph walk rooted at the handler can never reach it, however complete it is — and then it reports itself complete.

**Spring Data synthesises methods that exist in no source file.** `interface OwnerRepository extends JpaRepository` declares whatever you wrote and inherits `save`, `findById`, `flush`. A handler calling `owners.save(owner)` names a method that matches no indexed record anywhere.

All five of PetClinic's write endpoints reported no database operations. Six more sites in halo.

**`@Transactional` is usually on the class.** Spring applies it to every public method of the bean, and one annotation above `public class FooServiceImpl` is how most services declare it.

Reading only method annotations, my tool reported no transaction boundary for a fully transactional service — then went on to recommend, confidently, that the writes be wrapped in a single transaction. Which the class already does, at class level.

That is the failure mode in miniature: not silence, but a confident recommendation to do something the code already does.

**Application events have no call site either.** `publishEvent(new OrderPlaced(...))` reaches `@EventListener(OrderPlaced.class)` by declared parameter type — runtime wiring, not a name coincidence. Without joining those halves, the trace stops at "publishes an event" and everything the listener does, which is often the actual database work, is simply absent.

And it runs after the publish, possibly on another thread, outside the caller's transaction.

**Work handed to an executor is a hole.** Found on a real customer project:

```
GET /batchUpdate/factsValues          badged "no database operations"
  → updateFactValuesValueIds
      executorService.invokeAll(tasks)
  → FactValuesUpdateProcess.call()
      dao.updatePrevValueId(...)      two UPDATEs
```

The call graph terminates cleanly and reports itself complete. I chose not to resolve this by guessing which `Callable` was submitted — a wrong body walked is a fabricated finding, which is worse than an admitted gap.

## The names lie in specific, learnable ways

`@Table` is spelled two different ways by two different annotations. `jakarta.persistence.Table` takes `name =` and has no `value`. Spring Data JDBC's `@Table("owners")` is positional. I read `value` and the positional form only, so every JPA entity in every scan carried a null table name — the overwhelmingly common case, silently empty.

JPQL is not SQL, and reading it as SQL invents schema objects. `LEFT JOIN FETCH p.releases r` is an eager-fetch directive; my table-name pattern captured the word `FETCH` itself. The project's data map grew a target called `fetch`, which became **shopizer's largest table at 213 consumers.**

Path expressions do the same thing more quietly: `p.releases` becomes a table `releases` in schema `p` — a name that looks entirely real.

And entity names collide with SQL keywords. shopizer has `@Entity @Table(name = "ORDERS") class Order`, so `select o from Order o` had its only readable name struck out as the keyword `order`. The tiebreak is the project's own `@Entity` set, matched case-sensitively: JPQL entity references are Java class names, and `order` and `ORDER` are neither of them `Order`.

Also worth knowing: a repository stereotype is not the boundary of database access. A `JdbcTemplate` in a `@Service`, an `EntityManager` used for a native query, jOOQ, R2DBC, a raw `PreparedStatement` — none of them are repositories to a component scan. A read/write split names them `jdbcTemplate1` and `jdbcTemplate2`, which is mainstream, not exotic.

## Messaging boundaries are where the joins are wrong

This is the part I expected to know already, having spent years on SQS-heavy services. I was wrong in three specific ways.

**One queue, two spellings.** A producer holds a URL because the SDK wants one; `@SqsListener("orders-q")` takes the bare name; ARNs are a third form. Join on string equality and you report "publishes to a queue nobody consumes" about a queue with a consumer in the same repository.

**Consumers without annotations.** `sqsClient.receiveMessage(...)` inside a `Runnable` started from `@PostConstruct` is the standard shape in codebases predating spring-cloud-aws. An annotation-only scan reports "no consumers" on exactly those projects — the ones most likely to have been running longest.

**RabbitMQ does not join on the destination string at all.** A producer names an *exchange* plus a routing key; a listener binds a *queue*. Equal strings would join an exchange to a queue that happens to share its name. AMQP routes exchange → routing key → queue, and until a declared binding proves the route, a publish and a listener are two honest, unlinked facts.

The exception is the two-argument `convertAndSend` overload, which uses the default exchange — where the routing key *is* the queue name. Arity decides the meaning of the arguments.

And the delay pattern everyone builds on TTL queues means the listener that actually runs is two topology hops from the send: sender → TTL exchange → TTL queue → dead-letters → real exchange → listener.

## The rule I ended up with

Somewhere in this I stopped trying to find more edges and started separating two different kinds of not-knowing.

If the graph cannot choose between three candidate methods and walks all three, every body was read. The set of things that *can* happen is fully covered — a superset, not a sample. What remains unknown is which one runs.

That is an **attribution** question, and it does not invalidate an absence. "Nothing here opens a transaction" is sound if every candidate was inspected, whichever executes. Letting attribution uncertainty veto it produced an answer that declined to say whether database updates existed, because it could not tell three `setVersionNumber` methods apart — none of which does anything.

**Coverage** licenses an absence. Attribution does not.

The most uncomfortable lesson came from a performance change. I raised a per-node edge cap from 40 to 400 and produced 49 new false definitive "no database operations" on one repository. The cap had been the only thing preventing them — accidentally. Completeness had been protected by a limit nobody had chosen for that reason.

So the metric is inverted on purpose. Completeness is not a quality score to optimise; it is a licence to make a definitive claim. Raising it without raising resolution converts silence into confident error.

## What this has to do with reading a diff

I built a machine to answer "what does this change reach," and the machine's every serious failure was a confident *no*.

That is worth sitting with, because a reviewer reading a pull request runs the same scan. You start at the changed method. You follow the calls you can see. You conclude it doesn't touch the database, or doesn't cross a transaction, or doesn't publish anything.

You have the same blind spots my first version had. The `@ModelAttribute` above the handler. The `save` that exists in no file. The `@Transactional` on the class rather than the method. The listener with no call site. The `Callable` handed to an executor.

None of these are hidden — they are all documented, and most readers of this article knew every one of them individually. They are invisible *at the call site*, which is where we make changes and where we review them.

The habit I would offer is small: when a change looks local, treat "this doesn't reach anything" as a claim that needs evidence, not as the default. That is the one sentence my analyzer had to earn the right to say, and it took months.

## Where it still can't see

In the spirit of the above, the gaps I know about.

Self-invocation is the big one. A `@Transactional` or `@Async` method called from another method of the same bean never crosses the proxy, so the annotation silently does nothing — and my analyzer does not detect this at all. It is probably the most-cited Spring gotcha there is, and it is on the list.

`@TransactionalEventListener`'s `phase` attribute is recognised as an annotation and not read. There is no Open-Session-In-View or lazy-loading analysis. Property placeholders match keys exactly, with no relaxed kebab/camel binding.

Stating them is cheaper than being asked.
