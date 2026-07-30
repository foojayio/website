---
title: "Journeys in Java, Level 9: Docker compose all the things"
slug: "journeys-in-java-level-9-docker-compose-all-the-things"
date: "2023-02-26T10:02:15+00:00"
lastmod: "2023-02-26T10:08:04+00:00"
description: "In part 9 of this series, learn how to successfully create an orchestrated microservices system with Docker Compose!"
authors:
  - "jennifer-reif"
image: "https://foojay.io/wp-content/uploads/2023/01/unsplash-bridge-waterfall-scaled.jpeg"
categories:
  - "Databases"
  - "DevOps"
  - "Graph"
  - "Java Core"
  - "Microservices"
  - "Neo4J"
  - "Spring"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Our microservices project contains quite a few pieces now.

We have two databases, three API services, a user-view service for books, and a service to host our configuration.

With so many pieces to manage, it would be nice to have something that orchestrates the individual services into a system, such as Docker Compose.

Back in our [Level 5](https://foojay.io/today/journeys-in-java-level-5-building-an-empire-of-microservices/) rendition, we did exactly this for our smaller version of the project.

Now that we have expanded our services, we need to add those new pieces into the existing Docker Compose management umbrella.

I expected this step of the process to be much quicker, especially since I had done it before.

However, there were several obstacles I encountered that took much longer to solve than anticipated.

I'll do my best to cover those thoroughly here and avoid future problems for both myself and others!

Architecture {#_architecture}
-----------------------------

We built this project from scratch with a [rudimentary scope and functionality](https://foojay.io/today/journeys-in-java-level-1-building-an-empire-of-microservices/) and have slowly expanded the complexity.

In the [most recent step](https://foojay.io/today/journeys-in-java-level-8-add-mongodb-to-spring-cloud-config/), we had a few services managed by Docker Compose and a few managed manually.

This is because we wanted to avoid adding unnecessary complexity with an orchestration layer until we were certain service communication was working without it.

Now that things are operating well independently, we can migrate the working services so that everything is handled by Docker Compose.

Updated architecture:

![microservices level9](/images/posts/2023/02/journeys-in-java-level-9-docker-compose-all-the-things/microservices-level9.png)

There is a small grey border around each service indicating a containerized application. Services are tied to other services through arrows with some boxes to show the type of objects passed back and forth.

The larger grey box encompassing everything represents how Docker Compose is orchestrating all of services as a single unit.

Prepping applications {#_prepping_applications}
-----------------------------------------------

The first step was to integrate the config-service to Docker Compose. This service handles database credentials to both MongoDB and Neo4j.

I expected this step to be the trickiest, but in reality, getting `service1` to interact with the config service proved to be the biggest hurdle, and the one I spent the most time on.

Once that was operating smoothly, adding the rest of the services took very little time.

Let's walk through the steps!

Goodreads-config {#_goodreads_config}
-------------------------------------

First, we need to containerize the application using a Dockerfile. After copying/pasting from another service, I found out that my base [`openjdk` image](https://hub.docker.com/_/openjdk) was deprecated. There were a few suggestions for alternatives, but went with [Azul's image](https://hub.docker.com/u/azul) as the [new base image](https://github.com/JMHReif/microservices-level9/blob/main/config-server/Dockerfile#L3).

Next, we need to package the application from the service's directory using `mvn clean package` and build the Docker image with the containerized application with `docker build -t jmreif/goodreads-config .` at the command line.

Our application is packaged and the Docker image built, so we need to add the service to our `docker-compose.yml` file.

<pre class="EnlighterJSRAW" data-enlighter-language="yaml">version: "3.9"
services:
  #goodreads-db...
  goodreads-config:
    container_name: goodreads-config
    image: jmreif/goodreads-config
    # build: ./config-server
    ports:
      - "8888:8888"
    depends_on:
      - goodreads-db
    environment:
      - SPRING_PROFILES_ACTIVE=native,docker
    volumes:
      - $HOME/Projects/config/microservices-java-config:/config
      - $HOME/Projects/docker/goodreads/config-server/logs:/logs
    networks:
      - goodreads
networks:
  goodreads:</pre>

I temporarily commented out the previous service1, service2, and service3 blocks, so that I could focus on adding each piece individually.

The `goodreads-config` service contains many of the same fields we have used before. I added a commented-out `build` option test and make changes locally, rather than pulling from the remote image as the parameter above it does.

The `depends_on` option specifies that the database container must be started before this service can start. **Note:** This does not mean that the database container is in a "ready" state, only started.

Under the `environment` block, we have a variable set up for a Spring profile. This allows us to use different credentials, depending on whether we are running in a local test environment or in Docker.

The main difference is the use of `localhost` to connect to other services in a local environment (specified by `native` profile), versus container names in a Docker environment (profile: `docker`). The next option for `volumes` sets up the location of the config files (for now, in a local `config` directory) and log files.

*\*Note:\* I played around with moving the profile variable into the config file, but found out that we need it as a variable in the container (either from the application in `application.properties` or in docker-compose.yml). Sourcing it from outside the container didn't cooperate.*

Let's test what we changed so far!

### Round 1: Put it to the test! {#_round_1_put_it_to_the_test}

We can run this much of the system with a single command.

<pre class="EnlighterJSRAW" data-enlighter-language="shell">docker-compose up -d</pre>

*\*Note:\* If you are building local images with the build field in docker-compose.yml, then use the command `docker-compose up -d --build`. This will build the Docker containers each time on startup from the directories.*

Next, we can test our `goodreads-config` service by accessing the configuration file it is hosting. We can do this at the command line with `curl localhost:8888/mongo-client/docker` or using a browser with the URL `localhost:8888/mongo-client/docker`. This should show something like the screenshot below.

![config server test 2](/images/posts/2023/02/journeys-in-java-level-9-docker-compose-all-the-things/config-server-test-2.png)

Figure 1. Config server results

Bring everything back down again with another command.

<pre class="EnlighterJSRAW" data-enlighter-language="shell">docker-compose down</pre>

Goodreads-svc1: Interact with goodreads-config {#_goodreads_svc1_interact_with_goodreads_config}
------------------------------------------------------------------------------------------------

As mentioned above, this was the toughest part to get working, but I learned a few things along the way.

We already had Docker Compose managing this service in the previous [Level 5 version of the code](https://github.com/JMHReif/microservices-level5), so we can use that setup with some adjustments.

<pre class="EnlighterJSRAW" data-enlighter-language="yaml">version: "3.9"
services:
  #goodreads-db...
  #goodreads-config...
  goodreads-svc1:
    container_name: goodreads-svc1
    image: jmreif/goodreads-svc1:lvl9
    # build: ./service1
    ports:
      - "8081:8081"
    depends_on:
      - goodreads-config
    restart: on-failure
    environment:
      - SPRING_APPLICATION_NAME=mongo-client
      - SPRING_CONFIG_IMPORT=configserver:http://goodreads-config:8888
      - SPRING_PROFILES_ACTIVE=docker
networks:
    - goodreads</pre>

The first several options are the same as our previous services, although I added a tag to the image name to keep a separate image with these updates.

The next change is on the `depends_on` option. Instead of waiting directly for the database container, `service1` actually depends on the config service (`goodreads-config`) for the database credentials. The config service then forwards the call with appropriate credentials.

On the next line is a new option - `restart`. This took the longest time for me to debug, but you might remember me mentioning that [`depends_on` only waits for the container to start](https://vsupalov.com/simple-fix-db-not-ready/), not for the service to be ready. It turns out that `service1` was starting too early, so it would fail to find the configuration.

After trying a few different methods, such as building in request retries in the application itself, I discovered that the only working solution was to restart the whole container (or at least the entire application within the container). The most straightforward way to do this was through the [`restart` option in Docker Compose](https://docs.docker.com/compose/compose-file/#restart). This solved the startup and configuration issues I was seeing.

Lastly, the environment variables specify the application name, location of the config server, and Spring profile. The application name and active profile help the application find the appropriate configuration file on the config server.

The `SPRING_CONFIG_IMPORT` variable tells the container where to look for the config server. I also noticed that these properties did not work correctly if I put them in the config file itself. The values must be accessible within the container, or it would not know where to look.

### Service1: Application Changes {#_service1_application_changes}

As I was debugging the restart issues mentioned above, one suggestion to add resiliency to the application and assist with determining errors was to add [Spring Retry](https://docs.spring.io/spring-batch/docs/current/reference/html/retry.html) capabilities.

This allows us to set up guidelines for automatically retrying requests, which is especially helpful when safeguarding against situations like network interruptions.

While it didn't solve the Docker Compose container startup issues, I kept the code to make the application more robust and assist debugging.

There wasn't much code to add, and I followed a colleague's advice, alongside [Baeldung's article](https://www.baeldung.com/spring-retry).

<pre class="EnlighterJSRAW" data-enlighter-language="xml">	org.springframework.retry
	spring-retry

	org.springframework.boot
	spring-boot-starter-aop

	org.springframework.boot
	spring-boot-starter-actuator</pre>

First, we need the retry dependency, alongside the Spring Boot starter for [Aspect-Oriented Programming](https://www.javatpoint.com/spring-boot-aop). I also added Actuator, which will set up endpoints to inspect application health, metrics, and more.

Next, we need to [comment out the local properties](https://github.com/JMHReif/microservices-level9/blob/main/service1/src/main/resources/application.properties) in the `src/main/resources/application.properties` file so that the config server variables don't conflict with ones we are setting in the container (via `docker-compose.yml`).

Otherwise, it would connect to the container config server, but also try to connect to a local config server. It would continue to retry until it failed, causing the application to crash searching for irrelevant, backup property values.

Lesson learned: the retry applied to any properties set, whether in the application or environment variables.

Finally, I need to add a few annotations to the application class.

<pre class="EnlighterJSRAW" data-enlighter-language="java">@SpringBootApplication
@EnableRetry
public class Service1Application {
	....
}

@RestController
@RequestMapping("/db")
@AllArgsConstructor
class BookController {
	....

	@Retryable
	@GetMapping("/books")
	Flux getBooks() { return bookRepository.findAll(); }

	@Retryable
	@GetMapping("/book/{mongoId}")
	Mono getBook(@PathVariable String mongoId) { return bookRepository.findById(mongoId); }
}</pre>

The `@EnableRetry` annotation enables retry functionality in the application. The `@Retryable` annotation on the two methods tells the application which methods should utilize retry logic. Default configuration for retries is set to try the request up to three times with a delay of one second between each retry. However, we can customize the defaults through properties, annotation parameters for each method, or template configuration.

Notice that I did not add retry logic to the [`liveCheck()` method](https://github.com/JMHReif/microservices-level9/blob/main/service1/src/main/java/com/jmhreif/service1/Service1Application.java#L35). This is because we don't interact with other services or have other dependencies that might potentially cause flakiness in the requests. If the `liveCheck()` method does not work, then our application or container is not running.

While I was here, we can upgrade the Spring Boot project to use the latest versions of everything, so I modified the [Spring Boot version](https://github.com/JMHReif/microservices-level9/blob/main/service1/pom.xml#L8), [Java version](https://github.com/JMHReif/microservices-level9/blob/main/service1/pom.xml#L17), and [Spring Cloud version](https://github.com/JMHReif/microservices-level9/blob/main/service1/pom.xml#L18) in the `pom.xml`. I also updated the [Dockerfile's base image](https://github.com/JMHReif/microservices-level9/blob/main/service1/Dockerfile#L3) to the Azul JDK 17, as well.

With those changes in place, we will need to re-package the application with `mvn clean package -DskipTests=true` and rebuild the local Docker container. I also made some tweaks to the config file.

*\*Note:\* the `-DskipTests=true` is necessary because it will look for a config server and fail when/if it doesn't find it.*

### Config file: mongo-client {#_config_file_mongo_client}

In the [mongo-client configuration file](https://github.com/JMHReif/microservices-level9/blob/main/microservices-java-config/mongo-client.yaml) hosted by the config server, I added a couple more properties for opening up Actuator endpoints (for application debugging) and the application name

<pre class="EnlighterJSRAW" data-enlighter-language="text"># Enable all actuator endpoints FOR DEMO PURPOSES ONLY!
management:
  endpoints:
    web:
      exposure:
        include: "*"

spring:
  application:
    name: mongo-client
....</pre>

### Round 2: Put it to the test! {#_round_2_put_it_to_the_test}

Let's run all of the pieces so far - goodreads-db, goodreads-config, goodreads-svc1.

<pre class="EnlighterJSRAW" data-enlighter-language="shell">docker-compose up -d</pre>

*\*Note:\* If you are building local images with the build field in docker-compose.yml, then use the command `docker-compose up -d --build`. This will build the Docker containers each time on startup from the directories.*

Next, we can test our services with a few endpoints.

* Goodreads-config: command line with `curl localhost:8888/mongo-client/docker`.
* Goodreads-svc1: command line with `curl localhost:8081/db`, `curl localhost:8081/db/books`, and `curl localhost:8081/db/book/623a1d969ff4341c13cbcc6b` or web browser with only URL.

![service1 test](/images/posts/2023/02/journeys-in-java-level-9-docker-compose-all-the-things/service1-test.png)

Figure 2. Test service1 for a book

When we are done testing this, we can bring down the system with `docker-compose down`.

Goodreads-svc2: Interact with service1 {#_goodreads_svc2_interact_with_service1}
--------------------------------------------------------------------------------

This service is our user-facing service for interacting with book data. All of the changes made to this service will look familiar because we made the same changes in `service1`!

<pre class="EnlighterJSRAW" data-enlighter-language="yaml">version: "3.9"
services:
  #goodreads-db...
  #goodreads-config...
  #goodreads-svc1...
  goodreads-svc2:
    container_name: goodreads-svc2
    image: jmreif/goodreads-svc2:lvl9
    # build: ./service2
    ports:
      - "8080:8080"
    depends_on:
      - goodreads-svc1
    restart: on-failure
    environment:
      - BACKEND_HOSTNAME=goodreads-svc1
    networks:
      - goodreads
networks:
    - goodreads</pre>

We only add the `restart: on-failure` option here. Because we could have network interruptions that cause services to miss requests or other flaky behavior, I wanted to build the same resiliency into my other applications and containers that I did with `service1`. For a refresher on the `BACKEND_HOSTNAME` environment variable, check out the [Level 5 blog post](https://jmhreif.com/blog/microservices-level5/).

### Service1: Application Changes {#_service1_application_changes_2}

First, we want to add the Spring Retry logic to `service2` by adding the three dependencies in the `pom.xml` ([lines 29-40](https://github.com/JMHReif/microservices-level9/blob/main/service2/pom.xml#L29)). Next, we can add the property to the `application.properties` to [open all Actuator endpoints](https://github.com/JMHReif/microservices-level9/blob/main/service2/src/main/resources/application.properties#L3). **Note:** We only do this in development or local testing - not production! We can comment out that property here for now. Finally, we can add `@EnableRetry` to the [main application class](https://github.com/JMHReif/microservices-level9/blob/main/service2/src/main/java/com/jmhreif/service2/Service2Application.java#L18) and `@Retryable` to each method we want to use retry logic. In our case, only the [`getBooks()` method](https://github.com/JMHReif/microservices-level9/blob/main/service2/src/main/java/com/jmhreif/service2/Service2Application.java#L43).

We have already built in flexibility to `service2` for local or remote testing with the [`hostname` property](https://github.com/JMHReif/microservices-level9/blob/main/service2/src/main/java/com/jmhreif/service2/Service2Application.java#L20). More detail on how and why we did that is in the [Level 5 blog post](https://jmhreif.com/blog/microservices-level5/).

While we are here, we can update the Spring Boot project to use the latest versions of everything for [Spring Boot](https://github.com/JMHReif/microservices-level9/blob/main/service2/pom.xml#L8) and [Java](https://github.com/JMHReif/microservices-level9/blob/main/service2/pom.xml#L17). I also updated the [service's Dockerfile](https://github.com/JMHReif/microservices-level9/blob/main/service2/Dockerfile#L3) to use the Azul JDK 17 as the base image.

We need to re-package the application and build the local container, just as we did in `service1`. Because this service interacts only with `service1`, it doesn't need any ties to the config service or database. Let's test it!

### Round 3: Put it to the test! {#_round_3_put_it_to_the_test}

We'll use the `docker-compose up -d` command, as we did before, to spin up Docker Compose.

Then we test our endpoints.

* Goodreads-config: command line with `curl localhost:8888/mongo-client/docker`.
* Goodreads-svc1: command line with `curl localhost:8081/db`, `curl localhost:8081/db/books`, and `curl localhost:8081/db/book/623a1d969ff4341c13cbcc6b`.
* Goodreads-svc2: command line with `curl localhost:8080/goodreads` and `curl localhost:8080/goodreads/books` or web browser with the URL.

![service2 test](/images/posts/2023/02/journeys-in-java-level-9-docker-compose-all-the-things/service2-test.png)

Figure 3. Test service2 for books

And `docker-compose down` will shut down everything gracefully.

Goodreads-svc3: Backend service for Authors {#_goodreads_svc3_backend_service_for_authors}
------------------------------------------------------------------------------------------

Our third service is a near copy of `service1`, but it hosts author data (rather than books). Pretty much everything we learned before is also applied to this `service3`. We will list the changes for review.

* Docker Compose: [add configuration for `service3`](https://github.com/JMHReif/microservices-level9/blob/main/docker-compose.yml#L60) (values match `service1`).
* Application `pom.xml`: add three depedencies for retry and monitoring ([lines 38-49](https://github.com/JMHReif/microservices-level9/blob/main/service3/pom.xml#L38)). Also upgrade versions for [Spring Boot](https://github.com/JMHReif/microservices-level9/blob/main/service3/pom.xml#L8), [Java](https://github.com/JMHReif/microservices-level9/blob/main/service3/pom.xml#L17), and [Spring Cloud](https://github.com/JMHReif/microservices-level9/blob/main/service3/pom.xml#L18).
* Application `application.properties`: [add Actuator endpoints property and comment out](https://github.com/JMHReif/microservices-level9/blob/main/service3/src/main/resources/application.properties#L2) several (for local testing only).
* Application `Service3Application`: add `@EnableRetry` to [main application class](https://github.com/JMHReif/microservices-level9/blob/main/service3/src/main/java/com/jmhreif/service3/Service3Application.java#L21) and add `@Retryable` to desired methods ([`getAuthors()`](https://github.com/JMHReif/microservices-level9/blob/main/service3/src/main/java/com/jmhreif/service3/Service3Application.java#L39) and [`getAuthor(id)`](https://github.com/JMHReif/microservices-level9/blob/main/service3/src/main/java/com/jmhreif/service3/Service3Application.java#L43)).
* Dockerfile: use [Azul JDK 17 as base image](https://github.com/JMHReif/microservices-level9/blob/main/service3/Dockerfile#L3).
* Application: re-package app with `mvn clean package -DskipTests=true` and build local Docker container
* Config file: no changes because it will use the same values that `service1` uses.

Let's test!

### Round 4: Put it to the test! {#_round_4_put_it_to_the_test}

As usual, use `docker-compose up -d` at the command line to spin up our microservices system and test the endpoints with the commands listed below.

* Goodreads-config: command line with `curl localhost:8888/mongo-client/docker`.
* Goodreads-svc1: command line with `curl localhost:8081/db`, `curl localhost:8081/db/books`, and `curl localhost:8081/db/book/623a1d969ff4341c13cbcc6b`.
* Goodreads-svc2: command line with `curl localhost:8080/goodreads` and `curl localhost:8080/goodreads/books`.
* Goodreads-svc3: `curl localhost:8082/db`, `curl localhost:8082/db/authors`, and `curl localhost:8082/db/author/623a48c1b6575ea3e899b164` or web browser with only URL.

![service3 test](/images/posts/2023/02/journeys-in-java-level-9-docker-compose-all-the-things/service3-test.png)

Figure 4. Test service3 for an author

Close the system with `docker-compose down`.

Goodreads-svc4: Backend service for Reviews {#_goodreads_svc4_backend_service_for_reviews}
------------------------------------------------------------------------------------------

Similar to services one and three, `service4` is a backing service for book review data. However, this service interacts with a graph database in the cloud ([Neo4j](https://dev.neo4j.com/aura-java)). More background on this service is in the [Level 6 blog post](https://jmhreif.com/blog/microservices-level6/). Let's list out our changes to bring `service4` into Docker Compose.

* Docker Compose: [add configuration for `service4`](https://github.com/JMHReif/microservices-level9/blob/main/docker-compose.yml#L75).
* Application `pom.xml`: add three depedencies for retry and monitoring ([lines 38-49](https://github.com/JMHReif/microservices-level9/blob/main/service4/pom.xml#L38)). Upgrade versions for [Spring Boot](https://github.com/JMHReif/microservices-level9/blob/main/service4/pom.xml#L8), [Java](https://github.com/JMHReif/microservices-level9/blob/main/service4/pom.xml#L17), and [Spring Cloud](https://github.com/JMHReif/microservices-level9/blob/main/service4/pom.xml#L18).
* Application `application.properties`: [add Actuator endpoints property and comment out](https://github.com/JMHReif/microservices-level9/blob/main/service4/src/main/resources/application.properties#L2) several (for local testing only).
* Application `Service4Application`: add `@EnableRetry` to [main application class](https://github.com/JMHReif/microservices-level9/blob/main/service4/src/main/java/com/jmhreif/service4/Service4Application.java#L22) and add `@Retryable` to desired methods ([`getReviews()`](https://github.com/JMHReif/microservices-level9/blob/main/service4/src/main/java/com/jmhreif/service4/Service4Application.java#L40) and [`getBookReviews(id)`](https://github.com/JMHReif/microservices-level9/blob/main/service4/src/main/java/com/jmhreif/service4/Service4Application.java#L44)).
* Dockerfile: use [Azul JDK 17 as base image](https://github.com/JMHReif/microservices-level9/blob/main/service4/Dockerfile#L3).
* Application: re-package app with `mvn clean package -DskipTests=true` and build local Docker container
* Config file `neo4j-client`: [add Actuator endpoint property and application name](https://github.com/JMHReif/microservices-level9/blob/main/microservices-java-config/neo4j-client.yaml).

We now have all of our services migrated to Docker Compose. Time to test the entire system!

### Round 5 (final!): Put it to the test! {#_round_5_final_put_it_to_the_test}

We can run our system with the same command we have been using.

<pre class="EnlighterJSRAW" data-enlighter-language="shell">docker-compose up -d</pre>

Next, we can test all of our endpoints.

* Goodreads-config (mongo): command line with `curl localhost:8888/mongo-client/docker`.
* Goodreads-svc1: command line with `curl localhost:8081/db`, `curl localhost:8081/db/books`, and `curl localhost:8081/db/book/623a1d969ff4341c13cbcc6b`.
* Goodreads-svc2: command line with `curl localhost:8080/goodreads` and `curl localhost:8080/goodreads/books`.
* Goodreads-svc3: `curl localhost:8082/db`, `curl localhost:8082/db/authors`, and `curl localhost:8082/db/author/623a48c1b6575ea3e899b164`.
* Goodreads-config (neo4j): command line with `curl localhost:8888/neo4j-client/docker`.
* Neo4j database: ensure [AuraDB instance is running](https://console.neo4j.io/) (free instances are automatically paused after 3 days).
* Goodreads-svc4: `curl localhost:8083/neo`, `curl localhost:8083/neo/reviews`, and `curl localhost:8083/neo/reviews/178186` or web browser with only URL.

![service4 test](/images/posts/2023/02/journeys-in-java-level-9-docker-compose-all-the-things/service4-test.png)

Figure 5. Test service4, reviews for a book

Bring everything back down again with the below command.

<pre class="EnlighterJSRAW" data-enlighter-language="shell">docker-compose down</pre>

Wrapping up! {#_wrapping_up}
----------------------------

We have successfully created an orchestrated microservices system with Docker Compose!

We saw how Docker Compose can throw some tricky environment issues at us related to startup order and dependencies between microservices, but we were able to navigate those with additional configuration options. We also built resiliency into our application services with Spring Retry to help us handle service or network interruptions.

The microservices system now includes a database container (MongoDB), configuration service, three backing services (`service1`, `service3`, and `service4`), and a user-facing service (`service2`). We also connect to an external, cloud-hosted Neo4j database.

We have grown this system, and we hope to continue adding functionality and learning along the way. Happy coding!

Resources {#_resources}
-----------------------

* Github: [microservices-level9](https://github.com/JMHReif/microservices-level9) repository
* Blog post: [Simple Fix If Your Dockerized App Crashes...​](https://vsupalov.com/simple-fix-db-not-ready/)
* Documentation: [Docker Compose - restart](https://docs.docker.com/compose/compose-file/#restart)
* Neo4j AuraDB: [Create a FREE database](https://dev.neo4j.com/aura-java)
