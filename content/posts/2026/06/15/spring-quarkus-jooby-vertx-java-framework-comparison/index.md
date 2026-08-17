---
title: "Spring, Quarkus,Jooby, or Vert.x: Java Framework Comparison"
slug: "spring-quarkus-jooby-vertx-java-framework-comparison"
date: "2026-06-15T15:06:50+00:00"
lastmod: "2026-06-15T15:06:52+00:00"
description: "Compare Spring, Quarkus, Jooby, Vert.x on the same Java CRUD app. See startup time, image size, complexity, ecosystem tradeoffs, and best-fit use cases."
authors:
  - "catherine-edelveis"
image: "Favicon-3-2.png"
categories:
  - "Java"
tags:
related_posts:
enlighterjs: true
frozen: false
---

**TLDR**

* Building the same app four times revealed that the fastest framework also needed the most code, and the safest choice isn't always the cheapest one to run.
* "Just use Spring" is rarely wrong. It's also rarely the most interesting question a team can ask about its architecture.
* The real final boss may show up in two years, when developers who inherit the codebase have to figure out a framework nobody else on the team knows.

Every Java project I've seen starts the same way. The question "What framework?" is answered in a matter of seconds with "Spring Boot."

The reflex is by no means irrational. After all, Spring Boot is the de facto enterprise standard, and defaulting to it serves most teams most of the time. For some architectures, though, the right answer is in a framework the team never seriously evaluated.

I got curious. I have decided to compare four major Java frameworks: Spring, Quarkus, Jooby, and Vert.x. For that, I built the same app four times. This is [a quest app](https://github.com/code-with-bellsoft/quest) where heroes take on assignments by difficulty tier and required class: good old CRUD, in other words. Same business logic, same database, four frameworks. And alongside the code, I put together a decision matrix drawn from my own experience, the teams I've worked with, and official documentation. Because code alone is not the indicator.

Here's what I found.

Choosing your character class {#h2-0-choosing-your-character-class}
-------------------------------------------------------------------

I've named my demo app a quest for a reason. I think that selecting a framework is like picking an RPG character at the start of a long campaign. Each comes with a personality, strengths, constraints, and a preferred playstyle that shapes every decision down the road. So, before committing to one, ask yourself these eight questions:

| Criterion                 | Question                                                                                                               | Why it matters                                                                                                                                       |
|:--------------------------|:-----------------------------------------------------------------------------------------------------------------------|:-----------------------------------------------------------------------------------------------------------------------------------------------------|
| **Intended architecture** | What kind of app do we want to build? A simple service, a complex enterprise platform, a reactive event-driven system? | The framework should match the shape of the system.                                                                                                  |
| **Development speed**     | How fast can the team ship and update features?                                                                        | Velocity matters when requirements change. Teams need productive defaults, good tooling, testing support, and clear conventions.                     |
| **Ecosystem**             | How well does the framework integrate with what we need?                                                               | Strong integration support reduces custom glue code, lowers risk, and makes future requirements less painful.                                        |
| **Code complexity**       | How much ceremony, manual plumbing, or hidden magic?                                                                   | Code that looks elegant in a demo can get expensive when debugging production issues. The team must understand the model, not just copy annotations. |
| **Memory footprint**      | How much memory does the app need in production?                                                                       | Smaller images improve pull speed, deployment density, and security posture.                                                                         |
| **Maintenance cost**      | How expensive will this be to maintain over several years?                                                             | The cheapest framework on day one can become the most expensive by year two.                                                                         |
| **Stability**             | Will it still be safe, supported, and painless to upgrade in the future?                                               | Teams need confidence that upgrades won't turn into a quest of their own.                                                                            |
| **Performance model**     | What execution model does the framework expect?                                                                        | This shapes architecture, debugging, scaling, and developer skill requirements.                                                                      |

Let's see if the reflex holds up.

Spring {#h2-1-spring}
---------------------

Spring Boot is the paladin. It is familiar to almost every Java developer, built for sustained enterprise work, and designed to absorb complexity. Its pitch is straightforward: stand-alone, production-grade applications that you can just run, with opinionated starters, auto-configuration, and production features included.

The JPA repository is the clearest example of what that means in practice:

```
public interface QuestRepository extends JpaRepository<Quest, Long> {

    List<Quest> findByDifficulty(Difficulty difficulty);

    List<Quest> findByRequiredClassIgnoreCase(String requiredClass);

    List<Quest> findByDifficultyAndRequiredClassIgnoreCase(
            Difficulty difficulty,
            String requiredClass
    );

}
```


No implementation. Spring can even generate the queries from the method names. Define what you want, and Spring figures out how to fetch it.

The controller follows the same pattern: annotations declare intent, Spring wires everything up:

```
@RestController
@RequestMapping("/quests")
public class QuestController {

    private final QuestService service;

    public QuestController(QuestService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public Quest findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping
    public List<Quest> search(
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(required = false) String requiredClass
    ) {
        return service.search(difficulty, requiredClass);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Quest create(@Valid @RequestBody CreateQuestRequest request) {
        return service.create(request);
    }
}
```


Spring covers far more than what's visible here: Security, JPA, validation, messaging, batch, cloud config, observability, GraphQL. If you need it, there's almost certainly a starter for it.

The tradeoff is startup time and memory. Spring pays for its convenience with abstraction --- 4.9 seconds to start in a container in my tests, 194MB image size provided you choose a lightweight base image. That's manageable, and you can improve it significantly with AOT processing or native images, but it's the price of the warrior's armor.

For hiring, Spring Boot is the default keyword in Java backend job descriptions. You can staff it at every level.

Quarkus {#h2-2-quarkus}
-----------------------

If Spring is a heavily-armed warrior, Quarkus is a ranger: fast and very knowledgeable of the terrain it operates on. Quarkus was designed for containers from the start. Red Hat built it around build-time augmentation: instead of resolving dependencies and wiring beans at runtime, Quarkus does most of that work during the build, which is why the quest app starts in 2.1 seconds in a container.

The structure will feel familiar to Spring developers. Quarkus uses CDI and JAX-RS under the hood rather than Spring's annotation model, but the Panache repository keeps data access clean:

```
@ApplicationScoped
public class QuestRepository implements PanacheRepository<Quest> {

    public List<Quest> findByDifficulty(Difficulty difficulty) {
        return list("difficulty", difficulty);
    }

    public List<Quest> findByRequiredClassIgnoreCase(String requiredClass) {
        return list("lower(requiredClass) = lower(?1)", requiredClass);
    }

    public List<Quest> findByDifficultyAndRequiredClassIgnoreCase(
            Difficulty difficulty,
            String requiredClass
    ) {
        return list(
                "difficulty = ?1 and lower(requiredClass) = lower(?2)",
                difficulty,
                requiredClass
        );
    }
}
```


The JAX-RS resource uses `@Path` and `@Inject` instead of `@RequestMapping` and `@Autowired`, but the shape is the same:

```
@Path("/quests")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class QuestResource {

    private final QuestService service;

    @Inject
    public QuestResource(QuestService service) {
        this.service = service;
    }

    @GET
    @Path("/{id}")
    public Quest findById(@PathParam("id") Long id) {
        return service.findById(id);
    }

    @POST
    public Response create(@Valid CreateQuestRequest request) {
        Quest quest = service.create(request);
        return Response.status(Response.Status.CREATED).entity(quest).build();
    }
}
```


The dev experience is genuinely good. Live coding, where code changes are reflected in the running app automatically, is built in, including remote mode for containerized environments. Continuous testing is triggered on every save.

Some patterns that Spring handles with a single annotation (post-transaction event listeners, for one) require explicit wiring in Quarkus. Build-time augmentation explains both the performance wins and the tradeoffs: some libraries need Quarkus-specific extensions or native-image hints to work with it.

For Kubernetes deployments, Quarkus provides dedicated extensions, health checks, metrics, and OpenTelemetry out of the box. Red Hat's backing is solid and the LTS cadence is clear --- every six months, one year of support, overlap for upgrades.

The talent pool is thinner than Spring's. You can hire Java developers and train them up, but engineers with senior Quarkus production experience are still rare.

Jooby {#h2-3-jooby}
-------------------

Jooby is the monk. It travels light and advocates for minimalism: You pick every piece yourself. No annotation-driven magic to reverse-engineer. Routes are functions:

```
get("/quests/{id}", ctx -> service.findById(ctx.path("id").longValue()));

get("/quests", ctx -> service.search(
        ctx.query("difficulty").toOptional(Difficulty.class).orElse(null),
        ctx.query("requiredClass").toOptional().orElse(null)
));

post("/quests", ctx -> {
    var request = ctx.body(CreateQuestRequest.class);
    ctx.setResponseCode(StatusCode.CREATED);
    return service.create(request);
});
```


Instead of JPA, Jooby offers a JDBI module --- the middle ground between raw JDBC and full ORM. You write SQL, get type-safe results, and see exactly what goes to the database:

```
public Optional<Quest> findById(Long id) {
    return jdbi.withHandle(handle -> handle
            .createQuery("""
                    select id, title, difficulty, reward, required_class, status, assigned_hero
                    from quests
                    where id = :id
                    """)
            .bind("id", id)
            .map(QUEST_MAPPER)
            .findOne());
}
```


The SQL is right there in the method. There are no hidden queries or N+1 surprises waiting for production traffic.

The container image is 149MB and startup is 0.79 seconds. For a focused API service, that's a real win.

The cost shows up when requirements grow. As soon as you need OAuth/OIDC, audit logging, distributed tracing, retry patterns, and platform conventions, you assemble them yourself --- and those decisions become yours to maintain. Jooby is actively maintained with a clear security policy, but the available modules are fewer than what Spring and Quarkus provide out of the box. A confident senior team that values simplicity will thrive with it. If the company expects easy staffing or rotation, Jooby gets harder to justify.

Vert.x {#h2-4-vert-x}
---------------------

Vert.x is often called a framework, but technically, it is a toolkit for building reactive applications on JVM. It was specifically designed for high-volume messages, large event processing, HTTP interactions, and cloud-native apps. It is a reactive sorcerer who can punish hard. The official docs put it plainly: do not block the event loop, or the application can grind to a halt.

That warning tells you everything about Vert.x. It provides building blocks for reactive applications on the JVM (async database clients, event-bus messaging, non-blocking I/O) without imposing an application model on top. Use it right and the results are excellent:

```
public Future<AssignQuestResponse> assign(Long id, AssignQuestRequest request) {
    return repository.assign(id, request.heroName())
            .map(quest -> {
                eventBus.publish("quest.assigned", new JsonObject()
                        .put("questId", quest.id())
                        .put("heroName", request.heroName())
                        .put("heroClass", request.heroClass()));

                return new AssignQuestResponse(
                        quest.id(),
                        request.heroName(),
                        quest.status()
                );
            });
}
```


Clean, composable, genuinely async. The problem is what you write before you get there. The quest app's main verticle runs to nearly 90 lines to get the server started --- config loading, connection pool creation, router setup, health checks, validation, error handling, all explicit, all yours:

```
@Override
public Future<?> start() {
    return loadConfig()
            .map(AppConfig::from)
            .compose(config -> runFlyway(config)
                    .compose(ignored -> startHttpServer(config)));
}
```


That chain of `.compose()` calls bootstraps the entire application. Each step is a separate method you write yourself. Writing the application took 530 lines (the most of any framework in these tests) because every piece of that setup was manual. The container image is 147MB and startup is 0.73 seconds, the leanest numbers here. The problem didn't need that much boilerplate to solve.

For APIs that genuinely need high concurrency, event-bus patterns, reactive database access, or custom protocol handling, Vert.x can be excellent. For standard CRUD, you're paying a complexity tax the system doesn't need.

The numbers {#h2-5-the-numbers}
-------------------------------

All four apps were containerized with multi-stage Docker builds using [BellSoft Hardened Images](https://bell-sw.com/bellsoft-hardened-images/), which keep CVE counts low and image sizes lean by default. No AOT processing, no native images. Just the frameworks running as-is, so the comparison stays honest. The Dockerfiles are included into each framework's module.  

These are baseline numbers without tuning. Spring's startup improves substantially with AOT processing, and native images change the picture further --- but that's a separate experiment.

|             | Lines of Code | Container image size | Build time | Startup time |
|:------------|:-------------:|:--------------------:|:----------:|:------------:|
| **Spring**  |      325      |        194 MB        |   2.7 s    |    4.9 s     |
| **Quarkus** |      401      |        176 MB        |   6.5 s    |    2.1 s     |
| **Jooby**   |      367      |        149 MB        |   3.2 s    |    0.79 s    |
| **Vert.x**  |      530      |        147 MB        |   2.3 s    |    0.73 s    |

Who plays which character {#h2-6-who-plays-which-character}
-----------------------------------------------------------

For most teams making this decision without strong constraints in either direction, Spring is still the right call. It carries the lowest organizational risk: documentation, support lifecycle, mature third-party tooling, straightforward replacement hiring. The LOC count is the lowest of the four, and most Java developers already know the rules.

If the team is Kubernetes-native and cares about container density (startup time in production, pod density, CVE posture), Quarkus deserves serious consideration. The dev experience is excellent, Red Hat's backing makes it a credible enterprise option, and the performance profile is genuinely different from Spring's.

Jooby works best for a small, focused service with a senior team willing to own the toolchain. Accept going in that the hiring pipeline is thinner and you'll be assembling more pieces yourself.

Choose Vert.x specifically because the architecture demands it: high-concurrency APIs, event-driven systems, streaming workloads. For ordinary CRUD, those extra 205 lines over Spring don't disappear after the sprint. They become maintenance debt.

|                         |               Spring               |                  Quarkus                  |           Jooby           |             Vert.x              |
|:-----------------------:|:----------------------------------:|:-----------------------------------------:|:-------------------------:|:-------------------------------:|
|    **Mental model**     | Battle-proven enterprise framework | Build-time optimized enterprise framework | Lightweight web framework |        Reactive toolkit         |
|      **Best fit**       |    Standard enterprise services    |        Cloud-native Java services         |    Small, focused API     | Event-driven/concurrent systems |
| **Enterprise strength** |     Ecosystem, support, hiring     |      Kubernetes, native, containers       |        Simplicity         |      Reactive scalability       |
|      **Main risk**      |       Accidental complexity        |   Build-time magic and extension model    |     Smaller ecosystem     |        Async complexity         |

The quest app's full code, all four implementations, is [on GitHub](https://github.com/code-with-bellsoft/quest). Pick the framework you're considering and read its version of the same logic. That's usually more informative than any benchmark.
