---
title: "Jurassic JDK: Migrate or Extinct"
date: "2026-06-26T07:28:00+00:00"
description: "65 million years ago, dinosaurs didn't adapt. They're gone. Your JDK 7 app is giving the same energy. 🫠 I spent a year and a half migrating more than 15 - by Aicha Laafia"
authors:
  - "aicha-laafia"
image: "1_wul-KqE6vXk4ynnh_IljSQ.webp"
categories:
  - "Jakarta EE"
  - "Java"
  - "JDK21"
  - "Maven"
  - "Performance"
  - "Spring"
tags:
related_posts:
  - "foojay-podcast-78"
  - "foojay-podcast-28"
  - "announcing-sustainability-for-java-developers-a-new-collaborative-guide-from-the-foojay-io-community"
  - "are-java-security-updates-important"
frozen: false
---

![](1_wul-KqE6vXk4ynnh_IljSQ-1-1024x562.webp)

65 million years ago, dinosaurs didn't adapt. They're gone. Your JDK 7 app is giving the same energy. 🫠

I spent a year and a half migrating more than 15 projects in production. Full time. Real teams, real deadlines, real 2am breakage. This is everything I wish someone had written before I started.

## Why Are We Still Here? 👀

Walk into any company with a codebase older than 5 years and you'll hear the same thing:
> "We can't migrate; it's too risky."

And honestly? I get it. JDK 7 works. The app runs. Nobody's complaining today. Why touch it?

Here's why. Public support for JDK 7 ended years ago, and most organizations running it today rely on extended vendor support or no support at all. Every dependency you add is a gamble on compatibility. And every junior dev you hire looks at your codebase and quietly updates their LinkedIn.

The risk isn't migrating. The risk is staying. 🦖

## The Mistake Everyone Makes: The Big Jump

When teams finally decide to migrate, they do the worst possible thing.

They try to go from JDK 7 to JDK 21 in one shot.

One giant branch. One massive PR. Three months of work. Then merge day comes and everything explodes. The team burns out. The migration gets abandoned. And now you're back on JDK 7 with even less motivation to try again.

**Don't jump, Hop!** 🐸The Hop Strategy: LTS by LTS

Java releases a new LTS version every two years. That's your roadmap.

**JDK 7 → JDK 8 → JDK 11 → JDK 17 → JDK 21 → JDK 25**

Each hop is manageable. Each hop ships. Each hop gives your team confidence before the next one.

For large legacy systems, migrating one LTS at a time reduces risk and simplifies troubleshooting. Some teams successfully jump 8→17 or 11→21 directly, but the bigger the gap the harder it is to isolate what broke and why. Your call based on your codebase size and test coverage.

## Step 1: Read Your Codebase Before You Touch Anything 🔍

Before you change a single line, you need to know what you're dealing with.

Run a dependency audit. Every library has a maximum supported JDK version. Find the ones that will break first.

```
mvn versions:display-dependency-updates
```

Look for:

* Libraries that haven't been updated since 2015 🚩
* Internal utilities that wrap deprecated JDK APIs
* XML-based configuration that Spring Boot 3+ won't touch anymore
* Reflection-heavy code (Project Jigsaw will have opinions about this)

Document everything you find. This is your migration map. Without it you're just hiking blind into a jungle. 🌿

## Step 2: The Tools That Actually Help (and the Ones That Gaslight You)

### OpenRewrite ✅

This is the real one. OpenRewrite is an automated refactoring tool that handles a huge chunk of migration for you. Updating Spring Boot versions, migrating JUnit 4 to JUnit 5, replacing deprecated APIs. It's not magic but it's close.

There's a recipe for each LTS hop. For Java 21 specifically:

```
// build.gradle
plugins { 
id("org.openrewrite.rewrite")
version("latest.release")
 }

rewrite {
 activeRecipe("org.openrewrite.java.migrate.UpgradeToJava21") 
setExportDatatables(true)
 } 
dependencies { 
rewrite("org.openrewrite.recipe:rewrite-migrate-java:latest.release")
 }
```

Then run:

```
./gradlew rewriteRun # Gradle 
mvn rewrite:run # Maven
```

Then `git diff`. Review every change. Commit. Move on.
> **One important thing OpenRewrite won't touch:** your ` in ``pom.xml`` or ``targetCompatibility`` in ``build.gradle`. You need to update those manually before running compiler checks. Don't forget. 🚩

Run it. Review the diff. Don't blindly commit. Some of it you'll want, some of it you'll leave. Use your judgment.

### jdeprscan ✅

Built into the JDK itself. Scans your code for deprecated API usage before they blow up in your face.

```
jdeprscan --class-path your-app.jar your-app.jar
```

Run this before every hop. Not after. Before.

### Maven Enforcer Plugin ✅

Lock your minimum JDK version so nobody on the team accidentally compiles against the wrong one.

```
<plugin> 
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-enforcer-plugin</artifactId> 
  <configuration>
    <rules>
      <requireJavaVersion>
         <version>[21,)</version> 
       </requireJavaVersion> 
    </rules> 
  </configuration>
 </plugin>
```

### What Gaslights You ❌

Any tool that says it will "fully migrate your project automatically." No it won't. These tools handle the mechanical stuff. The architectural decisions, the library incompatibilities, the Hibernate pain (more on that below 👇) --- that's still on you. Every diff needs a human review. Every automated change needs a test run. Don't skip that. Ever.

## Step 3: The Breaking Changes That Will Ruin Your Day

### JDK 7 → JDK 8

Mostly additive. Lambdas, Streams, Optional, the new Date/Time API. The pain here is usually indirect: libraries that weren't ready for Java 8. Check your dependencies first.

### JDK 8 → JDK 11

This is where people first cry. 😭

The Java EE modules got removed from the JDK. `javax.xml.bind`, `javax.activation`, all of it. Gone. You need to add them back as explicit dependencies.

```
<dependency>
 <groupId>javax.xml.bind</groupId>
 <artifactId>jaxb-api</artifactId>
 <version>2.3.1</version>
</dependency>

<dependency>
 <groupId>com.sun.xml.bind</groupId>
 <artifactId>jaxb-impl</artifactId>
 <version>2.3.1</version>
</dependency>
```

> **Note:** this gets you back to compiling. If you're also targeting Spring Boot 3 eventually, you'll need to migrate to the `jakarta.*` namespace too, but that's a separate decision and a separate hop. Don't mix them.

Also: `sun.*` and `com.sun.*` internal APIs are now strongly encapsulated. If your code or any of your dependencies used those, you'll find out fast.

### JDK 11 → JDK 17

Project Jigsaw is now serious. Reflective access that used to work silently now throws warnings and eventually errors.

Worth knowing: Spring Boot 3 requires Java 17 as a minimum. But you don't have to upgrade to Spring Boot 3 the moment you hit Java 17. Many teams run Java 17 + Spring Boot 2.7 for a while before tackling the Boot 3 migration. These are two separate decisions. Treat them that way. The `javax.*` to `jakarta.*` rename is a Spring Boot 3 concern, not a JDK 17 concern.

### JDK 17 → JDK 21

Virtual threads. Project Loom ships and your thread-per-request apps can scale significantly better with almost no code change. Results vary depending on your drivers and blocking dependencies, so benchmark before declaring victory. But the potential is real. 🎉

This is also where Hibernate starts biting. Hard.

If you're on Hibernate 5, you need to move to [Hibernate 6](https://hibernate.org/orm/documentation/6.0/). And Hibernate 6 is not a minor bump.

**What changed in Hibernate 6:**

* Hibernate 6 moved from reading JDBC `ResultSet` by name to reading by position. That sounds internal but the impact is real: query results that used to map correctly can silently map wrong or throw at runtime. Especially with native queries and multi-table joins.
* The `@Type` annotation got completely reworked. Custom type mappings you had in Hibernate 5 won't compile in Hibernate 6. You need to rewrite them using the new `@JavaType` / `@JdbcType` annotations.
* `@Any` and `@ManyToAny` mappings changed behavior. If you used polymorphic associations, test everything twice. Then test it again. 🧪
* The Criteria API changed enough that some queries that worked before now produce different SQL. Silent behavior changes are the worst kind and Hibernate 6 has a few of them. Check your query results, not just whether the queries run.
* The `hbm.xml` mapping format is deprecated in Hibernate 6 and will be removed beyond 6.x. If you still have legacy XML mappings, start migrating them to annotations now.

### JDK 21 → JDK 25

[Java 25](https://openjdk.org/projects/jdk/25/) is the latest LTS, released in September 2025. If you're reading this before having any idea about what's there, this section is your preview of what you will be dealing with.

The OpenRewrite [`UpgradeToJava25`](https://docs.openrewrite.org/running-recipes/popular-recipe-guides/migrate-to-java-25)recipe covers the mechanical changes. Among them:

* `process.waitFor(5000, TimeUnit.MILLISECONDS)` becomes `process.waitFor(Duration.ofSeconds(5))` ⏱️
* `new java.io.StringReader("x")` becomes `Reader.of("x")`
* `inflater.end()` becomes `inflater.close()` since `Inflater` is now `AutoCloseable`
* `ZipError` replaced by `ZipException`
* Unused lambda params can use `_` (unnamed variables, GA since Java 22 via JEP 456)
* `SecurityManager` is effectively removed as a supported security mechanism. The class still exists but is non-functional. Any code relying on it needs to go.
* Instance main methods are coming (no more `public static void`) but still a preview feature, not GA yet. Don't use in production without `--enable-preview`.

But Hibernate keeps being the main source of pain. 🐛

Now the one that genuinely made my life a nightmare: `IdGeneratorStrategyInterpreter`.

If you had custom ID generation strategies, you were likely using or extending this class. Hibernate deprecated it in 6.0 and fully deleted it in 6.4. No replacement shim. No compatibility layer. Just gone.

Your code won't compile. And if you have custom generators spread across 15 projects, you'll be hunting these down one by one. 🔍

The fix is migrating to the new `@IdGeneratorType` annotation introduced in Hibernate 6. It's cleaner and type-safe. But it's a rewrite, not a find-and-replace.

```
// Before - using the now-deleted IdGeneratorStrategyInterpreter 
public class CustomIdGenerator implements IdentifierGenerator 
{ 
@Override
public Serializable generate(SharedSessionContractImplementor session, Object object) {
 // your logic 
}
}
 // After - Hibernate 6+, using @IdGeneratorType 
@IdGeneratorType(CustomIdGenerator.class) 
@Retention(RUNTIME)
@Target({ FIELD, METHOD }) 
public @interface CustomGenerated {}

public class CustomIdGenerator implements BeforeExecutionGenerator {
 @Override
  public Object generate(SharedSessionContractImplementor session, Object owner, Object currentValue, EventType eventType) {
 // your logic
 } 
}
```

Also: from Hibernate 6.4, if you have `@GeneratedValue` on a non-identifier field, Hibernate now throws an `AnnotationException` at startup. Previous versions silently ignored it. Code that booted fine for years will suddenly refuse to start. Grep your whole codebase for `@GeneratedValue` before this hop. 🚩

The bottom line with Hibernate across these hops: OpenRewrite does the JDK-level stuff. Hibernate is manual work. Budget real time for it.

## Step 4: Build a Migration Plan That Won't Make Your Team Cry 📋

Here's the structure that worked across 15+ projects.

### **Phase 1: Inventory (1 week)**

Run all the scanning tools. Document every dependency version, every deprecated API usage, every Hibernate mapping that might be affected. Don't fix anything yet. Just map the terrain.

### **Phase 2: Dependency cleanup (1--2 weeks)**

Update all dependencies to versions that support your target JDK. Do this before touching the JDK itself. Mixing a JDK upgrade with a dependency upgrade is how you lose a week of debugging.

### **Phase 3: One hop (1--3 weeks depending on codebase size)**

Change the JDK version. Run OpenRewrite. Update your build file `java.version` manually. Fix what OpenRewrite doesn't catch. Run the full test suite. Fix what that surfaces. Ship it. Don't move to the next hop until this one is stable in production.

### **Phase 4: Repeat**

Same process for each LTS hop. The first one is the hardest. By the third one your team is doing it in their sleep.

## The Things Nobody Tells You 🤫

**Your CI/CD pipeline needs updating too.** Forget to update your Docker base image and you're building with the new JDK but running with the old one in production. That's a fun Friday afternoon. Update the pipeline before you ship each hop.

**Test coverage is your best friend and your worst enemy.** High test coverage gives you confidence. Zero test coverage means you find out things are broken from users. If you're in the zero camp, write at least integration tests for your critical paths before starting. You'll thank yourself.

**One project at a time.** If you have multiple services, don't migrate them all simultaneously. Pick the smallest, least critical one first. Learn on it. Then bring those lessons to the rest.

**Communicate with your team constantly.** Migration fatigue is real. People get frustrated when they're blocked by a Hibernate mapping error at 5pm on a Friday. Keep the team in the loop, celebrate each hop that ships, and make it clear this is progress not punishment.

## What's Waiting for You on the Other Side 🏆

After 15+ migrations this is the part that still gets me every time.

The first time a developer on the team writes a `record` instead of a 40-line POJO and looks up with that face. You know the face. "Wait, that's it?"

```
// Before 
public class RaceResult {
 private final int round;
 private final String gp;
 private final int points;

 public RaceResult(int round, String gp, int points) {
 this.round = round;
 this.gp = gp;
 this.points = points;
 }
 // getters, equals, hashCode, toString... 
}

 // After 🎉 
record RaceResult(int round, String gp, int points) {}
```

Virtual threads that let your app handle way more load with far less overhead. Pattern matching that makes your switch statements actually readable. Stream Gatherers that let you batch, slide, and group data without collector headaches.

The language on JDK 21 is not the same language as JDK 7. It's better. Way better. And your team deserves to write it. ✨

## The Real Cost of Staying 💸

Every month you stay on JDK 7:

* Security vulnerabilities pile up with no patches coming
* New hires spend their first week confused by ancient patterns
* Dependencies you want to use require a newer JDK and you can't have them
* The gap between you and modern Java widens and the eventual migration gets harder

The migration isn't free. But staying isn't free either. The bill is just quieter.

## TL;DR 🦕

Don't jump, hop. Scan before you touch anything. Use OpenRewrite but don't trust it blindly. Update your build file manually. Update dependencies before the JDK. Budget real time for Hibernate. One project at a time. Ship each hop before starting the next.

65 million years ago, the dinosaurs didn't have a choice. But you do! 🦖

So please adapt, migrate, and survive!
