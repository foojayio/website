---
title: "Java Version Support Calendar: End-of-Support Dates for Java 8, 11, 17, 21 and 25 by Vendor"
date: "2026-12-07"
description: "End-of-support dates for Java 8, 11, 17, 21 and 25 from Temurin, Corretto, Microsoft, Red Hat, Azul and Oracle, and commands to find which build you run."
authors:
  - "fahri-cap"
image: "cover.jpg"
categories:
  - "OpenJDK"
  - "Java Core"
  - "OpenRewrite"
  - "Security"
related_posts:
  - "should-you-update-java-or-upgrade-and-which-version-should-you-use"
  - "java-patching-at-incident-speed-lessons-from-the-emudhra-ca-compromise"
  - "java4shell-cve-2026-114938-what-it-is-who-is-affected-and-what-to-do-now"
  - "which-java-runtime-should-you-use-in-production-comparing-openjdk-distributions"
  - "free-java-real-risk-why-unsupported-openjdk-is-now-a-security-gamble"
  - "the-java-21-doomsday-clock-countdown"
canonical: "https://www.oreonit.com/java-version-support-calendar-end-of-life-dates/"
---

There is no single end-of-life date for a Java version. Java is built by several vendors from the same OpenJDK source, and each vendor decides how long it will keep publishing security updates for its own builds. Java 17 from one vendor stops receiving updates in 2027; from another it is covered into 2029. What matters is whose build is inside your servers and container images, and whether you pay for support.

This post starts with the commands that tell you which Java version and which vendor's build you run. It then puts the published dates for the five long-term support (LTS) releases in one table, explains what each vendor's date actually means, and covers the upgrade path, including the OpenRewrite recipes for each step. Dates were checked on 25 November 2026 against each vendor's own page, listed at the end. Vendors do extend these dates, so confirm before you plan around one.

## How to find which Java versions you run

Start with an inventory. Most organisations run more Java versions than they think, because each application, build agent and container image carries its own.

### On a server or virtual machine

```bash
# the Java on the PATH (prints version and vendor)
java -version

# every JVM your user can attach to, then the exact version of one of them
jcmd -l
jcmd <pid> VM.version

# every Java process on the host, whoever owns it (includes containers' processes)
ps -eo pid,user,args | grep '[j]ava'

# vendor and runtime name, when the version banner does not name the vendor
java -XshowSettings:properties -version 2>&1 | grep -E 'java\.(vendor|runtime\.name) ='

# installed runtimes on Debian/Ubuntu and RHEL-family systems
update-alternatives --list java
rpm -qa | grep -i -E 'openjdk|java'
```

The vendor line in the `java -version` output matters as much as the number: "Temurin", "Corretto", "Zulu", "Red_Hat", "Microsoft" or "Oracle Corporation" tells you which column of the support table below applies. Some builds do not name the vendor in that output. An Oracle JDK prints "Java(TM) SE Runtime Environment" and Oracle's OpenJDK build prints "OpenJDK Runtime Environment", and both report `java.vendor` as "Oracle Corporation", so read the runtime name as well as the vendor property.

`jcmd` only lists JVMs it can attach to: those running as the same user, and not those inside containers. On a container host, use the process list as well, then check each image with the commands below.

### In container images

```bash
# run the image's own java
docker run --rm --entrypoint java <image> -version

# many official JDK images set the version in an environment variable
docker image inspect <image> | grep -i java_version

# list Java runtimes and libraries found in an image (Syft)
syft <image> | grep -i -E 'jdk|jre|java'

# list the images running in a Kubernetes cluster, to feed the commands above
kubectl get pods -A -o jsonpath='{range .items[*].spec.containers[*]}{.image}{"\n"}{end}' | sort -u

# the same for init containers, which is also where sidecar containers are declared
kubectl get pods -A -o jsonpath='{range .items[*].spec.initContainers[*]}{.image}{"\n"}{end}' | sort -u
```

### In the build

Check what the code is compiled for, which is not always what it runs on: `maven.compiler.release`, `source` and `target` in `pom.xml`, the Gradle toolchain or `sourceCompatibility`, and the JDK on your CI agents. An application compiled for Java 8 and running on Java 17 is common, and is the easiest case to finish.

## End-of-support dates, by version and vendor

The columns do not all measure the same thing, so read the header with these notes:

- **Temurin** (Eclipse Temurin): free builds, and the date is an "at least" date.
- **Corretto** (Amazon Corretto): free builds.
- **Microsoft** (Microsoft Build of OpenJDK): free builds, and the date is the earliest end of support.
- **Red Hat** (Red Hat build of OpenJDK): end of full support → end of ELS-1.
- **Azul Core**: end of paid production support.
- **Oracle Java SE**: end of Premier Support → end of Extended Support, both paid.

| Java | Temurin | Corretto | Microsoft | Red Hat | Azul Core | Oracle Java SE |
|------|---------|----------|-----------|---------|-----------|----------------|
| **8** | Dec 2030 | Dec 2030 | Not built (Microsoft uses Temurin) | **30 Nov 2026** → 31 Dec 2032 | Dec 2030 | Mar 2022 → Dec 2030 |
| **11** | Oct 2027 | Jan 2032 | Sept 2027 | 31 Oct 2024 → 31 Jan 2032 | Jan 2032 | Sept 2023 → Jan 2032 |
| **17** | Oct 2027 | Oct 2029 | Sept 2027 | 31 Dec 2027 → 31 Jan 2029 | Sept 2029 | **Sept 2026** → Sept 2029 |
| **21** | Dec 2029 | Oct 2030 | Sept 2028 | 31 Dec 2029 → 30 Sept 2031 | Sept 2031 | Sept 2028 → Sept 2031 |
| **25** | Sept 2031 | Oct 2032 | Sept 2030 | 31 Dec 2030 → 30 Sept 2033 | Sept 2033 | Sept 2030 → Sept 2033 |

How to read it on 25 November 2026:

- **Java 8** still has free updates from Temurin and Corretto until at least December 2030. Red Hat's full support for OpenJDK 8 on RHEL ends on **30 November 2026**; after that it is covered only under Extended Life Support (ELS), which needs a separate entitlement.
- **Java 11 and Java 17** are the next to go on the free builds most teams use: Microsoft in September 2027 and Temurin in October 2027. Corretto keeps both going for longer (Java 11 to January 2032, Java 17 to October 2029).
- **Java 21** is safe on every build until at least September 2028, and **Java 25** until at least September 2030.

**Oracle:** Premier Support for Java 17 ended in September 2026, so Oracle customers on Java 17 are now in Extended Support, which Oracle lists until September 2029. Oracle's Extended Support needs a paid Java SE subscription; see the Oracle section below for how the free licence differs.

## What "end of support" means for each vendor

The vendors use similar words for different things. The main split is between free builds, where the date is when updates stop being published, and paid support, where the date is when you can no longer raise a support case or get fixes under contract.

| Vendor | What the date means | Cost |
|--------|---------------------|------|
| Eclipse Temurin (Adoptium) | The minimum date Adoptium will keep publishing builds. The page says the dates are minimums and that LTS builds continue for as long as the upstream OpenJDK source is maintained. | Free. Commercial support is available from third parties, not from Adoptium. |
| Amazon Corretto | Amazon provides updates "at no cost until at least the specified date", released quarterly. The date in the table is Amazon's end-of-life date; the last planned update comes earlier: October 2030 for Corretto 8, October 2031 for 11, July 2029 for 17, July 2030 for 21 and July 2032 for 25. | Free, open source licence. |
| Microsoft Build of OpenJDK | The "earliest end-of-support date"; Microsoft says it may extend it. Quarterly updates in January, April, July and October. Java 8 is not built by Microsoft; it points Java 8 users to Temurin, and to its own Temurin-based Java 8 container images for Azure. | Updates are free to anyone. Commercial support is only for Azure customers with an Azure support plan, for workloads on Azure, Azure Stack and Azure Arc. |
| Red Hat build of OpenJDK | Two phases: full support, then ELS-1. The table on Red Hat's page covers OpenJDK on RHEL. | Part of a Red Hat subscription; ELS needs a separate entitlement. |
| Azul | The end of the Production Support term for Azul Core and Azul Prime. Azul's page says LTS releases then get two more years beyond that date. The roadmap page does not cover the free Zulu community builds. | Paid support. |
| Oracle Java SE | Premier Support, then Extended Support, for customers with a Java SE subscription. Sustaining Support is listed as indefinite but brings no new fixes. | Paid subscription. Oracle JDK 25 is free for all users under the No-Fee Terms and Conditions licence (details below). |

### Oracle Java SE: two different kinds of date

Oracle's roadmap is the one most often misread, because it publishes more than one kind of date for the same release. There are the support dates for customers with a paid Oracle Java SE subscription (Premier Support, then Extended Support), and separately the period during which Oracle publishes updates for its JDK under the free No-Fee Terms and Conditions (NFTC) licence. The free-licence window is much shorter than the paid support dates, and once it ends, further Oracle JDK updates for that release are under a different licence that is not free for production use.

According to [Oracle's Java SE support roadmap](https://www.oracle.com/java/technologies/java-se-support-roadmap.html), Oracle JDK 25 has been free for all users under the NFTC licence since September 2025, and update releases of JDK 25 after September 2028 are planned to move to the Java SE OTN licence, which is how Oracle already offers updates for Java 8, 11, 17 and 21. In practice: if you run the Oracle JDK for 8, 11, 17 or 21 in production, current updates need a subscription; check which licence each of your installed versions was downloaded under. Oracle also publishes OpenJDK builds under an open source licence on jdk.java.net, with a much shorter update period.

## What happens on the day

Nothing stops working. The JVM does not check a date. What changes is that the next quarterly security release does not include your version, and every quarter after that the list of known, fixed vulnerabilities that you are still exposed to gets longer. Java security updates are a regular event: the [Java4Shell vulnerability patched on 17 November 2026](https://www.oreonit.com/java4shell-cve-2026-114938-who-is-affected-what-to-do/) is a recent example of a flaw that was only fixed in supported builds.

The other effects arrive more slowly, and usually cost more:

- **Security updates stop.** Scanners flag the runtime, and audits, customer security questionnaires and Cyber Essentials-style assessments start asking why it is still there.
- **Frameworks move on without you.** Spring Boot 3.5 and 4.2 need at least Java 17, so an application on Java 8 or 11 cannot move to a supported Spring Boot release without a Java upgrade first. See the framework section below.
- **Container base images change.** Vendors stop publishing new image tags for versions they no longer build. An image built on an old tag keeps running, but it is rebuilt from a base that no longer gets fixes, and the image scanner report grows with every build.
- **Libraries drop old Java.** Drivers, SDKs and libraries raise their minimum Java version over time, so security fixes in your dependencies become harder to take as well.

## Frameworks and libraries that force the Java upgrade

In practice, many Java upgrades are triggered by a framework or library rather than the JDK. This table gives the minimum Java version and the support dates that each project publishes, checked on 26 November 2026 on the projects' own sites. "No published date" means the project gives none, which is normal for volunteer-run libraries and does not mean the line is unsupported. An empty cell means the project publishes nothing for it.

| Framework or library | Minimum Java | End of OSS support | End of commercial support |
|---|---|---|---|
| **Frameworks** | | | |
| Spring Boot 2.7 | Java 8 (compatible up to 21) | 30 Jun 2023 | 30 Jun 2029 |
| Spring Boot 3.5 | Java 17 (compatible up to 25) | 30 Jun 2026 | 30 Jun 2032 |
| Spring Boot 4.0 | Java 17 (compatible up to 27) | 31 Dec 2026 | 31 Dec 2027 |
| Spring Boot 4.2 | Java 17 (compatible up to 27) | 31 Dec 2027 | 31 Dec 2028 |
| Spring Framework 5.3 | Java 8 (up to 21) | 31 Aug 2024 | 30 Jun 2029 |
| Spring Framework 6.2 | Java 17 (up to 25) | 30 Jun 2026 | 30 Jun 2032 |
| Spring Framework 7.1 | Java 17 | 31 Dec 2027 | 31 Dec 2028 |
| Quarkus 3.40 LTS | Java 17 | 30 Sep 2027 | Red Hat build: 30 Sep 2027 |
| Quarkus 4.0 | Java 21 | 27 Jan 2027 (not an LTS release) |  |
| Micronaut 4 | Java 17 | No published date | Oracle, Object Computing (no dates) |
| Micronaut 5 | Java 25 | No published date | Oracle, Object Computing (no dates) |
| Eclipse Vert.x 4.5 | Java 8 | April 2027 |  |
| Apache Camel 4.22 LTS | Java 17 | Aug 2027 |  |
| **Persistence** | | | |
| Hibernate ORM 5.6 | Java 8 | Ended (end of life) | IBM, Red Hat, HeroDevs, TuxCare (no dates) |
| Hibernate ORM 6.6 | Java 11 | Limited support, no date | IBM, Red Hat (no dates) |
| Hibernate ORM 7.4 | Java 17 | Latest stable series, no date | IBM, Red Hat (no dates) |
| **Servers and runtimes** | | | |
| Apache Tomcat 9.0 | Java 8 | No earlier than 31 Mar 2027 | Extended support via a new 9.1.x branch |
| Apache Tomcat 10.1 | Java 11 | No published date |  |
| Apache Tomcat 11.0 | Java 17 | No published date |  |
| Eclipse Jetty 9.4 | Java 8 | Community support ended 1 Jun 2022 | Webtide (no dates) |
| Eclipse Jetty 10 and 11 | Java 11 | Community support ended 1 Jan 2024 | Webtide (no dates) |
| Eclipse Jetty 12 | Java 17 | No published date | Webtide (no dates) |
| **Core libraries** | | | |
| Netty 4.1 | Java 6 | 1 Jul 2027 |  |
| Netty 4.2 | Java 8 | No published date |  |
| Jackson 2.18 LTS | Java 8 | End of Dec 2026 |  |
| Jackson 2.21 LTS | Java 8 | At least until end of Jan 2028 |  |
| Jackson 3.1 LTS | Java 17 | At least until end of Feb 2028 |  |
| Apache Log4j 2 | Java 8 | Only the latest 2.x minor is maintained, no date |  |
| JUnit 6 | Java 17 | No published date |  |
| Apache Kafka clients 4.x | Java 11 (brokers need Java 17) | Last three releases get fixes, no date |  |
| **Build tools (Java needed to run the tool)** | | | |
| Gradle 9 | Java 17 | Current major, no date |  |
| Apache Maven 3.9 | Java 8 | Maintained, no date |  |

How to read it:

- Java 17 is now the minimum for the current line of almost every major framework: Spring Boot 3 and 4, Quarkus 3 LTS, Micronaut 4, Hibernate ORM 7, Tomcat 11, Jetty 12, Jackson 3, JUnit 6 and Apache Camel 4. Two have already moved past it: Quarkus 4.0 needs Java 21 and Micronaut 5 has a Java 25 baseline.
- On Java 8, few lines still get free fixes, and several have dates: Tomcat 9.0 (no earlier than 31 March 2027), Vert.x 4.5 (April 2027), Netty 4.1 (1 July 2027) and Jackson 2.18 (end of December 2026). Spring Boot 2.7, Spring Framework 5.3, Hibernate ORM 5.6 and Jetty 9.4 are already past free support.
- Java 11 is a short stop, not a destination. Its lines are either unmaintained (Jetty 10 and 11) or in reduced maintenance (Hibernate ORM 6.6).
- The build and test chain has moved too: Gradle 9 and JUnit 6 need Java 17 to run. Build tools can still compile for an older Java than the one they run on.
- Many core libraries publish no end date. For those, "supported" means "on the current line", so plan to stay current instead of waiting for a deadline.

Moving from Spring Boot 2.x, Hibernate 5 or Tomcat 9 to the current lines also means moving from the `javax.*` to the `jakarta.*` package names, which is often a bigger change than the JDK upgrade itself.

## The upgrade path, in order

Move from one LTS release to the next, with a release and a few weeks in production between steps. Each step has its own well-documented set of changes, and skipping releases makes it much harder to tell which change broke what.

1. **Java 8 to 11.** The hardest step for most applications. Java EE modules such as JAXB, JAX-WS and javax.annotation were removed from the JDK and must be added as dependencies; internal APIs are encapsulated; old build plugins, bytecode libraries and agents need newer versions.
2. **Java 11 to 17.** Strong encapsulation of JDK internals becomes the default, so libraries that reached into internals (often through reflection) need updating. This is also the step that unlocks Spring Boot 3 and 4, with the `jakarta.*` rename that comes with them.
3. **Java 17 to 21, then 25.** Usually smaller steps. Choose 21 or 25 based on your framework and vendor dates in the tables above; both have longer support than 17 on every vendor's build.

### Automating the mechanical work with OpenRewrite

[OpenRewrite](https://docs.openrewrite.org/) automates a large part of the mechanical work. Its `rewrite-migrate-java` module has a recipe for each step:

| Step | Recipe |
|------|--------|
| Java 8 to 11 | `org.openrewrite.java.migrate.Java8toJava11` |
| To Java 17 (covers a migration from Java 8 to 17) | `org.openrewrite.java.migrate.UpgradeToJava17` |
| To Java 21 | `org.openrewrite.java.migrate.UpgradeToJava21` |
| To Java 25 | `org.openrewrite.java.migrate.UpgradeToJava25` |

The OpenRewrite guides for [migrating to Java 17](https://docs.openrewrite.org/running-recipes/popular-recipe-guides/migrate-to-java-17), [Java 21](https://docs.openrewrite.org/running-recipes/popular-recipe-guides/migrate-to-java-21) and [Java 25](https://docs.openrewrite.org/running-recipes/popular-recipe-guides/migrate-to-java-25) give the current Maven and Gradle commands. On 26 November 2026 the Java 17 guide gives this Maven command line, which leaves your `pom.xml` untouched:

```bash
mvn -U org.openrewrite.maven:rewrite-maven-plugin:6.75.0:run \
  --define rewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:RELEASE \
  --define rewrite.activeRecipes=org.openrewrite.java.migrate.UpgradeToJava17
```

Swap the recipe name in the last line for another step. The `run` goal changes your source files and can change `pom.xml` too (the recipe updates the Java version and build plugin settings), so run it on a clean branch. To see the changes first, use the `dryRun` goal in place of `run`: it writes a patch to `target/rewrite/rewrite.patch` and changes nothing. To keep the recipe in the build instead, the same guide configures the plugin in `pom.xml` and runs `mvn rewrite:run`:

```xml
<plugin>
  <groupId>org.openrewrite.maven</groupId>
  <artifactId>rewrite-maven-plugin</artifactId>
  <version>6.75.0</version>
  <configuration>
    <activeRecipes>
      <recipe>org.openrewrite.java.migrate.UpgradeToJava17</recipe>
    </activeRecipes>
  </configuration>
  <dependencies>
    <dependency>
      <groupId>org.openrewrite.recipe</groupId>
      <artifactId>rewrite-migrate-java</artifactId>
      <version>3.71.0</version>
    </dependency>
  </dependencies>
</plugin>
```

{{< note >}}
Read the access notes in the guide before you run either form. The guide describes `rewrite-migrate-java` as a Moderne source-available recipe module, and says OpenRewrite artifacts are distributed through the Code Genome Project repository, which requires authentication, so the repository and your credentials have to be declared in your Maven `settings.xml` first. The guide has the exact `settings.xml` and the Gradle equivalents.
{{< /note >}}

Run a recipe on a branch, review the diff like any other change, and rely on your tests, not the recipe, to tell you the result is correct. If there are not enough tests to trust, writing them is the first step of the upgrade.

### Changing vendor instead of version

Changing vendor within the same Java version, for example from Oracle JDK 8 to Temurin or Corretto 8, is a smaller change than a version upgrade and can buy time while the version upgrade is planned. Test it all the same, particularly anything that depends on fonts, cryptography providers or vendor-specific JVM options.

## What I would do this quarter

- Build the inventory: every application, the Java version and vendor it runs on, the framework versions, and the container base images.
- Mark anything on Red Hat OpenJDK 8 without ELS, on Microsoft or Temurin 11 or 17, or on a Spring Boot release past its OSS support date.
- Move off Java 8 first, then plan 11 and 17 before September and October 2027.
- Rebuild container images on supported base tags on a schedule, not only when the application changes.

For a small number of services this is a few days of work per step. For a large, older application with old frameworks and few tests it is a project, and it belongs in a wider modernisation plan alongside the framework and database upgrades. A longer guide to [modernising legacy Java applications](https://www.oreonit.com/how-to-modernize-legacy-java-applications/) covers the wider options.

## Sources

- [Eclipse Adoptium support](https://adoptium.net/support/) (Temurin release support dates)
- [Amazon Corretto FAQs](https://aws.amazon.com/corretto/faqs/) (support calendar and update cadence)
- [Support roadmap for the Microsoft Build of OpenJDK](https://learn.microsoft.com/java/openjdk/support) (earliest end-of-support dates, Java 8 guidance)
- [OpenJDK life cycle and support policy, Red Hat](https://access.redhat.com/articles/1299013) (full support and ELS-1 dates on RHEL)
- [Azul support roadmap](https://www.azul.com/products/azul-support-roadmap/) (Azul Core and Prime production support dates)
- [Oracle Java SE support roadmap](https://www.oracle.com/java/technologies/java-se-support-roadmap.html) (read 25 Nov 2026)
- [Spring Boot support](https://spring.io/projects/spring-boot#support) and [Spring Framework support](https://spring.io/projects/spring-framework#support) (OSS and commercial support dates)
- [Spring Boot system requirements](https://docs.spring.io/spring-boot/system-requirements.html) (minimum Java versions)
- [Apache Tomcat: which version do I want?](https://tomcat.apache.org/whichversion.html) (Java requirements and Tomcat 9 end of support)
- Framework and library support pages, for the second table: [Quarkus releases](https://quarkus.io/releases/), [Micronaut support](https://micronaut.io/support/), [Vert.x 4.5.36 release notes](https://vertx.io/blog/eclipse-vert-x-4-5-36/), [Apache Camel downloads](https://camel.apache.org/download/), [Hibernate ORM releases](https://hibernate.org/orm/releases/), [Jetty downloads and version history](https://jetty.org/download.html), [Netty 4.1 end-of-life announcement](https://netty.io/news/2026/09/09/4-1-EOL-announcement.html), [Jackson releases](https://github.com/FasterXML/jackson/wiki/Jackson-Releases), [Log4j downloads](https://logging.apache.org/log4j/2.x/download.html), [JUnit](https://docs.junit.org/current/overview.html), [Apache Kafka](https://github.com/apache/kafka), [Gradle compatibility matrix](https://docs.gradle.org/current/userguide/compatibility.html), [Maven release history](https://maven.apache.org/docs/history.html)
- OpenRewrite guides: [migrate to Java 17](https://docs.openrewrite.org/running-recipes/popular-recipe-guides/migrate-to-java-17), [migrate to Java 21](https://docs.openrewrite.org/running-recipes/popular-recipe-guides/migrate-to-java-21) and [migrate to Java 25](https://docs.openrewrite.org/running-recipes/popular-recipe-guides/migrate-to-java-25)

*This article was first published on [oreonit.com](https://www.oreonit.com/java-version-support-calendar-end-of-life-dates/), where I keep the calendar up to date as vendors change their dates.*
