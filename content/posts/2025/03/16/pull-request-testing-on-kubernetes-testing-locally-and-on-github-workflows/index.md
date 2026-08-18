---
title: "Pull request testing on Kubernetes: testing locally and on GitHub workflows"
slug: "pull-request-testing-on-kubernetes-testing-locally-and-on-github-workflows"
date: "2025-03-16T17:32:27+00:00"
lastmod: "2025-03-17T08:47:16+00:00"
description: "In this post, we'll lay the ground for a simple app's unit- and integration-testing, leveraging Testcontainers in the local environment."
canonical: "https://blog.frankel.ch/pr-testing-kubernetes/1/"
authors:
  - "nicolas-frankel"
image: "concrete-block.jpeg"
categories:
  - "Kotlin"
  - "Testing"
tags:
related_posts:
  - "my-final-take-on-gradle-vs-maven"
  - "my-first-steps-with-playwright"
  - "pull-request-testing-on-kubernetes-working-with-github-actions-and-gke"
  - "rust-jvm"
enlighterjs: true
frozen: false
---

Imagine an organization with the following practices:

* Commits code on GitHub
* Runs its CI/CD pipelines with GitHub Actions
* Runs its production workload on Kubernetes
* Uses Google Cloud

A new engineer manager arrives and asks for the following:
> On every PR, run integration tests in a Kubernetes cluster similar to the production one.

It sounds reasonable.
> Engineering manager: I want #integrationtests to run on the app deployed on #Cloud infra for each #GitHub PR ✅
>
> Me, thinking it's a no-brainer: sure thing! 🤦‍♂️
>
> Me, after 85 runs: I have content for my next blog post/talk 😅
>
> [\[image or embed\]](https://bsky.app/profile/did:plc:lho243ntrkr6h4ohtvk3lr4x/post/3ldq7sml3as2n?ref_src=embed)
>
> --- Nicolas Fränkel 🇺🇦🇬🇪 ([@frankel.ch](https://bsky.app/profile/did:plc:lho243ntrkr6h4ohtvk3lr4x?ref_src=embed)) [December 20, 2024 at 10:52 AM](https://bsky.app/profile/did:plc:lho243ntrkr6h4ohtvk3lr4x/post/3ldq7sml3as2n?ref_src=embed)

In this series of posts, I'll show how you can do it. My plan is the following:

* This blog post focuses on the app, the basic GitHub workflow setup, and testing both locally and during the workflow run
* The second blog post will detail the setup of a Google Kubernetes Engine instance and how to adapt the workflow to use it
* The third and final post will describe how to isolate each run in a [dedicate virtual Kubernetes cluster](https://www.vcluster.com/)

Unit testing vs. integration testing
------------------------------------

I wrote the book [Integration Testing from the Trenches](https://leanpub.com/integrationtest). In there, I defined *Integration testing* as:
> Integration Testing is a strategy to test the collaboration of at least two components.

I translated it in as:
> Integration Testing is a strategy to test the collaboration of at least two classes.

I doubled down on the definition a [couple of years later](https://blog.frankel.ch/unit-test-vs-integration-test/):
> Let's consider the making of a car. Single-class testing is akin to testing each nut and bolt separately. Imagine testing of such components brought no issue to light. Still, it would be very risky to mass manufacture the car without having built a prototype and sent it to a test drive.

However, technology has evolved since that time.

Testcontainers
--------------

I use the word "technology" very generally, but I have [Testcontainers](https://testcontainers.com/) in mind:
> *Unit tests with real dependencies*
>
> Testcontainers is an open source library for providing throwaway, lightweight instances of databases, message brokers, web browsers, or just about anything that can run in a Docker container.

In effect, Testcontainers replaces *mocks* with "real" dependencies-containerized. It's a real game-changer: instead of painfully writing mocking code to stub dependencies, just set them up regularly.

For example, without Testcontainers, you'd need to provide mocks for your *data access objects* in tests; with it, you only need to start a database container, and off you go.

At the time, the cost of having a local Docker daemon in your testing environment offset many benefits. It's not the case anymore, as Docker daemons are available (nearly) everywhere.

My definition of Integration Testing has changed a bit:
> Integration Testing is testing that requires significant setup.

The definition is vague on purpose, as significance has a different meaning depending on the organization, the team, and the individual. Note that Google defines two categories of tests: fast and slow. Their definition is equally vague, meant to adapt to different contexts.

In any case, the golden rule still applies: the closer you are to the final environment, the more risks you cover and the more valuable your tests are. If our target production environment is Kubernetes, we will reap the most benefits from running the app on Kubernetes and testing it as a black box. It doesn't mean that white box testing in a more distant environment is not beneficial; it means that the more significant the gap between the testing environment and the target environment, the fewer issues we will uncover.

For the purposes of this blog post, we will use GitHub as the base testing environment for unit testing and a full-fledged Kubernetes cluster for integration testing. There is no absolute truth regarding what is the best practice™, as contexts vary widely across organizations and even across teams within the same organization. It's up to every engineer to decide within their specific context the ROI of setting up such an environment because the closer you are to production, the more complex and, thus, expensive it will be.

Use-case: application with database
-----------------------------------

Let's jump into how to test an app that uses a database to store its data. I don't want anything fancy, just solid, standard engineering practices. I'll be using a -based app, but most of the following can easily apply to other stacks as well. The following blog posts will involve less language-specific content.

Here are the details:

* Kotlin, because I love the language
* Spring Boot: it's the most widespread framework for JVM-based applications
* Maven-there's nothing else
* Project Reactor and coroutines, because it makes things more interesting
* PostgreSQL-at the moment, it's a very popular database, and it's well-supported by Spring
* [Flyway](https://www.red-gate.com/products/flyway/)

If you don't know Flyway, it allows you to track database schemas and data in a code repository and manage changes, known as migrations, between versions. Each migration has a unique version, *e.g.*, v1.0, v1.1, v2.1.2, etc. Flyway tries to apply migration in order. If it has already applied a migration, it skips it. Flyway stores its data in a dedicated table to track the applied migrations.

This approach is a must-have; [Liquibase](https://www.liquibase.com/) is an alternative that follows the same principles.

Spring Boot fully integrates Flyway and Liquibase. When the app starts, the framework will kickstart them. If a pod is killed and restarted, Flyway will first check the migrations table to apply only the one that didn't run previously.

I don't want to bore you with the app details; you can find the code at [GitHub](https://github.com/ajavageek/vcluster-pipeline).

"Unit" testing
--------------

Per my definition above, unit testing should be easy to set up. With Testcontainers, it is.

The testing code counts the number of items in a table, inserts a new item, and counts the number of items again. It then checks that:

* There's one additional item compared to the initial count
* That the new item is the one we inserted

```kotlin
@SpringBootTest                                                              //1
class VClusterPipelineTest @Autowired constructor(private val repository: ProductRepository) { //2

    @Test
    fun `When inserting a new Product, there should be one more Product in the database and the last inserted Product should be the one inserted`() { //3
        runBlocking {                                                        //4
            val initialCount = repository.count()                            //5
            // The rest of the test
        }
    }
}
```


1. Initialize the Spring context
2. Insert the repository
3. Praise Kotlin to allow for descriptive function names
4. Run non-blocking code in a blocking function
5. Use the repository

We now need a PostgreSQL database; Testcontainers can provide one for us. However, to avoid conflicts, it will choose a random port until it finds an unused one. We need it to connect to the database, run the Flyway migration, and run the testing code.

For this reason, we must write a bit of additional code:

```kotlin
@Profile("local")                                                              //1
class TestContainerConfig {

    companion object {
        val name = "test"
        val userName = "test"
        val pass = "test"
        val postgres = PostgreSQLContainer<Nothing>("postgres:17.2").apply {   //1
            withDatabaseName(name)
            withUsername(userName)
            withPassword(pass)
            start()
        }
    }
}

class TestContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        if (applicationContext.environment.activeProfiles.contains("local")) {
            TestPropertyValues.of(                                             //2
                "spring.r2dbc.url=r2dbc:postgresql://${TestContainerConfig.postgres.host}:${TestContainerConfig.postgres.firstMappedPort}/$name",
                "spring.r2dbc.username=$name",
                "spring.r2dbc.password=$pass",
                "spring.flyway.url=jdbc:postgresql://${TestContainerConfig.postgres.host}:${TestContainerConfig.postgres.firstMappedPort}/$name",
                "spring.flyway.user=$name",
                "spring.flyway.password=$pass"
            ).applyTo(applicationContext.environment)
        }
    }
}
```


1. Start the container, but only if the Spring Boot profile `local` is active
2. Override the configuration values

We need to specify neither the `spring.flyway.user` nor the `spring.flyway.password` if we hacked the `application.yaml` to reuse the R2BC parameters of the same name:

```yaml
spring:
  application:
    name: vcluster-pipeline
  r2dbc:
    username: test
    password: test
    url: r2dbc:postgresql://localhost:8082/flyway-test-db
  flyway:
    user: ${SPRING_R2DBC_USERNAME}                                             #1
    password: ${SPRING_R2DBC_PASSWORD}                                         #1
    url: jdbc:postgresql://localhost:8082/flyway-test-db
```


1. Smart hack to DRY configuration further down

We also annotate the previous test class to use the initializer:

```kotlin
@SpringBootTest
@ContextConfiguration(initializers = [TestContainerInitializer::class])
class VClusterPipelineTest @Autowired constructor(private val repository: ProductRepository) {

    // No change
}
```


Spring Boot offers a couple of options to [activate profiles](https://docs.spring.io/spring-boot/reference/features/external-config.html). For local development, we can use a simple JVM property, *e.g.* , `mvn test -Dspring.profiles.active=local`; in the CI pipeline, we will use environment variables instead.

"Integration" testing
---------------------

I'll also use Flyway to create the database structure for integration testing. In the scope of this example, the System Under Test will be the entire app; hence, I'll test from the HTTP endpoints. It's end-to-end testing for APIs. The code will test the same behavior, albeit treating the as a black box.

```kotlin
class VClusterPipelineIT {

    val logger = LoggerFactory.getLogger(this::class.java)

    @Test
    fun `When inserting a new Product, there should be one more Product in the database and the last inserted Product should be the one inserted`() {

        val baseUrl = System.getenv("APP_BASE_URL") ?: "http://localhost:8080" //1

        logger.info("Using base URL: $baseUrl")

        val client = WebTestClient.bindToServer()                              //2
            .baseUrl(baseUrl)
            .build()

        val initialResponse: EntityExchangeResult<List<Product?>?> = client.get() //3
            .uri("/products")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(Product::class.java)
            .returnResult()

        val initialCount = initialResponse.responseBody?.size?.toLong()        //4

        val now = LocalDateTime.now()
        val product = Product(
            id = UUID.randomUUID(),
            name = "My awesome product",
            description = "Really awesome product",
            price = 100.0,
            createdAt = now
        )

        client.post()                                                          //5
            .uri("/products")
            .bodyValue(product)
            .exchange()
            .expectStatus().isOk
            .expectBody(Product::class.java)

        client.get()                                                           //6
            .uri("/products")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(Product::class.java)
            .hasSize((initialCount!! + 1).toInt())
    }
}
```


1. Get the deployed app URL
2. Create a web client that uses the former
3. Get the initial item list
4. Get the size; we definitely should offer a count function if there are too many items
5. Insert a new item and assert everything works out fine
6. Get the list of items and assert the item count is higher by one

Before going further, let's run the tests in a GitHub workflow.

The GitHub workflow
-------------------

I'll assume you're familiar with [GitHub workflows](https://docs.github.com/en/actions/writing-workflows). If you aren't, a GitHub workflow is a declarative description of an automated job. A job consists of several steps. GitHub offers several triggers: Manual, scheduled, or depending on an event.

We want the workflow to run on each Pull Request to verify that tests run as expected.

```yaml
name: Test on PR                                                               #1

on:
  pull_request:
    branches: [ "master" ]                                                     #2
```


1. Set a descriptive name
2. Trigger on a PR to the master branch

The first steps are pretty standard:

```yaml
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout repository
        uses: actions/checkout@v4
      - name: Install JRE
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 21
          cache: maven                                                         #1
```


1. The `setup-java` action includes a caching option for build tools. Here, it will cache dependencies across runs, speeding up consecutive runs. Unless you have good reasons not to, I recommend using this option.

For the same reason, we should cache our built artifacts. While researching for this post, I learned that GitHub discards them across runs \**and steps in the same run*. Hence, we can speed up the runs by caching them explicitly:

```yaml
      - name: Cache build artifacts
        uses: actions/cache@v4                                                 <1>
        with:
          path: target
          key: ${{ runner.os }}-build-${{ github.sha }}                        <2>
          restore-keys:
            ${{ runner.os }}-build                                             <3>
```


1.
   1. Use the same action that `actions/setup-java` uses under the hood
   2. Compute the cache key. In our case, the `runner.os` should be immutable, but this should be how you run matrices across different operating systems.
   3. Reuse the cache if it's the same OS

```yaml
      - name: Run "unit" tests
        run: ./mvnw -B test
        env:
          SPRING_PROFILES_ACTIVE: local                                        <1>
```


   1. Activate the local profile. The workflow's environment provides a Docker daemon. Hence, Testcontainer successfully downloads and runs the database container.

   At this point, we should run the integration test. Yet, we need the app deployed to run this test. For this, we need available infrastructure.

   Alternative "Unit testing" on GitHub
   ------------------------------------

   The above works perfectly on GitHub, but we can move closer to the deployment setup by leveraging GitHub [service containers](https://docs.github.com/en/actions/use-cases-and-examples/using-containerized-services/about-service-containers). Let's migrate PostgreSQL from Testcontainers to a GitHub service container.

   Removing Testcontainers is pretty straightforward: we do not activate the `local` profile.

   Using GitHub's service container requires an additional section in our workflow:

```yaml
jobs:
  build:
    runs-on: ubuntu-latest
    env:
      GH_PG_USER: testuser                                                     #1
      GH_PG_PASSWORD: testpassword                                             #1
      GH_PG_DB: testdb                                                         #1
    services:
      postgres:
        image: postgres:15
        options: >-                                                            #2
          --health-cmd "pg_isready -U $POSTGRES_USER"
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
            - 5432/tcp                                                         #3
        env:
          POSTGRES_USER: ${{ env.GH_PG_USER }}                                 #4
          POSTGRES_PASSWORD: ${{ env.GH_PG_PASSWORD }}                         #4
          POSTGRES_DB: ${{ env.GH_PG_DB }}                                     #4
```


   1. Define environment variables at the job level to use them across steps. You can use secrets, but in this case, the database instance is not exposed outside the workflow and will be switched off when the latter finishes. Environment variables are good enough to avoid adding unnecessary secrets.
   2. Make sure that PostgreSQL works before going further
   3. Assign a random port and map it to the underlying `5432` port
   4. Use the environment variables

   To run the tests using the above configuration is straightforward.

```yaml
- name: Run "unit" tests
  run: ./mvnw -B test
  env:
    SPRING_FLYWAY_URL: jdbc:postgresql://localhost:${{ job.services.postgres.ports['5432'] }}/${{ env.GH_PG_DB }} #1
    SPRING_R2DBC_URL: r2dbc:postgresql://localhost:${{ job.services.postgres.ports['5432'] }}/${{ env.GH_PG_DB }} #1
    SPRING_R2DBC_USERNAME: ${{ env.GH_PG_USER }}
    SPRING_R2DBC_PASSWORD: ${{ env.GH_PG_PASSWORD }}
```


   1. GitHub runs PostgreSQL on a local Docker, so the host is `localhost`. We can get the random port with the `${{ job.services.postgres.ports['5432'] }}` syntax.

   For more information on `job.services.`, please check the [GitHub documentation](https://docs.github.com/en/actions/writing-workflows/workflow-syntax-for-github-actions#jobsjob_idservices).

   Conclusion
   ----------

   In this article, we laid the ground for a simple app's unit- and integration-testing, leveraging Testcontainers in the local environment. We then proceeded to automate unit testing via a GitHub workflow with the help of GitHub service containers. In the next post, we will prepare the Kubernetes environment on a Cloud provider infrastructure, build the image, and deploy it to the latter.

   The complete source code for this post can be found on [GitHub](https://github.com/ajavageek/vcluster-pipeline).

   **Go further:**
   * [Integration Testing from the Trenches](https://leanpub.com/integrationtest)
   * [Unit tests vs integration tests, why the opposition?](https://blog.frankel.ch/unit-test-vs-integration-test/)
   * [Writing GitHub workflows](https://docs.github.com/en/actions/writing-workflows)
   * [GitHub service containers](https://docs.github.com/en/actions/use-cases-and-examples/using-containerized-services/about-service-containers)
   * [jobs..services](https://docs.github.com/en/actions/writing-workflows/workflow-syntax-for-github-actions#jobsjob_idservices)
   * [vCluster](https://vcluster.com)

   

   *Originally published on [A Java Geek](https://blog.frankel.ch/pr-testing-kubernetes/1/) on February 9^th^, 2025*

*[JVM]: Java Virtual Machine
*[OOP]: Object-Oriented Programming
*[CRUD]: Create Read Update Delete
*[SUT]: System Under Test
