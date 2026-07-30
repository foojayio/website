---
title: "Spring Boot: local development enhancements, let's compose!"
slug: "spring-boot-local-development-enhancements-lets-compose"
date: "2023-08-25T11:16:05+00:00"
lastmod: "2023-08-25T12:07:27+00:00"
description: "How to make use of the new local development enhancements in Spring boot 3.1. Get started here on Foojay.io Today!"
authors:
  - "simon-verhoeven"
image: "https://foojay.io/wp-content/uploads/2021/09/1024px-Spring_Framework_Logo_2018.svg.png"
categories:
  - "Developer Tools"
  - "Spring"
  - "Testcontainers"
  - "Tools"
tags:
related_posts:
  - "a-simple-service-with-spring-boot"
  - "a-faster-way-to-build-react-spring-boot-apps-using-hilla-1-3"
  - "better-error-handling-for-your-spring-boot-rest-apis"
  - "the-new-jdbcclient-introduced-in-spring-framework-6-1"
enlighterjs: true
frozen: false
---

Quite often when we are developing an application we need external services such as rabbitMQ, Kafka, etc.

When you are developing locally, you are quite likely using a docker-compose file to start these up, and I am certainly (hopefully) not the only one that has forgotten at least once to start these instances up.

And maybe you are even already using Testcontainers for your testing.

Luckily, Spring Boot 3.1 introduced some nice improvements to make our lives a bit easier.

1. [Docker compose support](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#features.docker-compose) which allows us to make use of our `compose.yml` file to start these up and create the service connections for supported containers
2. [Testcontainers at development time](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#features.testing.testcontainers.at-development-time)

Both of these functionalities are built atop the [ConnectionDetails abstraction](https://spring.io/blog/2023/06/19/spring-boot-31-connectiondetails-abstraction), so if you are unfamiliar with this. I recommend checking out this article.

**Note:** the `docker compose` CLI application needs to be on your path for these to work properly.

Feel free to clone [the demo repository](https://github.com/SimonVerhoeven/sbldi), to run the samples!

Docker compose support {#_docker_compose_support}
-------------------------------------------------

This method allows us to leverage our existing `docker-compose.yml` files, with some extra quality of live functionality.

We just need to add a dependency on `spring-boot-docker-compose`

Maven:

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;dependencies&gt;
    &lt;dependency&gt;
        &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
        &lt;artifactId&gt;spring-boot-docker-compose&lt;/artifactId&gt;
        &lt;optional&gt;true&lt;/optional&gt;
    &lt;/dependency&gt;
&lt;/dependencies&gt;</pre>

Gradle:

<pre class="EnlighterJSRAW" data-enlighter-language="groovy">dependencies {
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
}</pre>

**note** : the docker-compose support is limited at the moment (such as no Kafka) when we're using `spring-boot-testcontainers` we can use any container with the programmatic API.

### How it works {#_how_it_works}

When we then start our application Spring boot will:

* look for common filenames (`compose.yml` \| `compose.yaml` \| `docker-compose.yml` \| `docker-compose.yaml`)
* start the defined containers/services using `docker compose up`
* create the service connection beans for supported containers

And when the application stops, the defined containers/services are shut down using `docker compose down`

### Configuration {#_configuration}

There are a slew of configuration options, but some useful ones to know:

* specifying a specific compose file: `spring.docker.compose.file`
* managing the docker-compose lifecycle can be done using `spring.docker.compose.lifecycle-management` to configure it as:
  * none: do not start nor stop
  * start-only
  * start-and-stop
* making use of spring profile-specific docker compose files (`docker--compose-{profile}.yaml`) can be done using: `spring.docker.compose.profiles.active`

Testcontainers at development time {#_testcontainers_at_development_time}
-------------------------------------------------------------------------

### The setup {#_the_setup}

We just need to add a dependency on `spring-boot-testcontainers`

Maven:

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;dependencies&gt;
    &lt;dependency&gt;
        &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
        &lt;artifactId&gt;spring-boot-testcontainers&lt;/artifactId&gt;
        &lt;optional&gt;true&lt;/optional&gt;
    &lt;/dependency&gt;
&lt;/dependencies&gt;</pre>

Gradle:

<pre class="EnlighterJSRAW" data-enlighter-language="groovy">dependencies {
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
}</pre>

The application itself is a very simple one that allows us to [get a die result](http://localhost:8080/rollDie) which is then stored in our `Redis` instance, and to [retrieve all these rolls](http://localhost:8080/listRolls)

Now rather than having to install a Redis instance locally, or using a `docker.yaml` file, we're making use of the new Testcontainers functionality.

As you can see in `DemoConfiguration` we are making use of the new `ServiceConnection` to define our Redis instance making use of a Testcontainer.

<pre class="EnlighterJSRAW" data-enlighter-language="java">@Bean
@ServiceConnection(name = "redis")
GenericContainer&lt;?&gt; redisContainer() {
    return new GenericContainer&lt;&gt;(DockerImageName.parse("redis:latest")).withExposedPorts(6379);
}</pre>

Now in `TestTestcontainersDemoApplication` you'll see that we are making use of the new `SpringApplication.from` method to delegate to our actual application, and we are passing in our Test configuration.

<pre class="EnlighterJSRAW" data-enlighter-language="java">public static void main(String[] args) {
    SpringApplication.from(TestcontainersDemoApplication::main)
            .with(DemoConfiguration.class)
            .run(args);
}</pre>

This way we can run our application for development purposes.  

Alternatively, we can make use of: `./gradlew bootTestRun` or `./mvnw spring-boot:test-run`.

After this, we can see that our application has started up ***including*** our Testcontainers.

#### What if my desired container does not have a ServiceConnection yet? {#_what_if_my_desired_container_does_not_have_a_serviceconnection_yet}

Using `@ServiceConnection` is recommended, but not all technologies support this method yet.

If this is the case, then you can inject your `@Bean` definition of the container with `DynamicPropetyRegistry` to contribute the dynamic properties at development time.

This works akin to the `@DnamicPropertySource` annotation from tests and allows us to add properties that become available once the container has started.

For example, let's say we want to send out e-mails from our application and we want to use make use of `MailHog` which does not have a service connection factory provided yet in `spring-boot-testcontainers` we can do:

<pre class="EnlighterJSRAW" data-enlighter-language="java">@Bean
public GenericContainer mailhogContainer(DynamicPropertyRegistry registry) {
   GenericContainer container = new GenericContainer("mailhog/mailhog")
                                        .withExposedPorts(1025);
   registry.add("spring.mail.host", container::getHost);
   registry.add("spring.mail.port", container::getFirstMappedPort);
   return container;
}</pre>

To provide the required information at development time.

### Keeping our data {#_keeping_our_data}

You will notice that when your application stops, the containers are also stopped.  

This does mean that you'll also lose your data.

There are two options to work around this in case you want to keep your data.

#### Reusable testcontainers (experimental) {#_reusable_testcontainers_experimental}

The first option, [Reusable Testcontainers](https://java.testcontainers.org/features/reuse/) is an experimental feature that can be used by adding `.withReuse(true)`.  

These containers are not stopped when your application stops!

<pre class="EnlighterJSRAW" data-enlighter-language="java">@Bean
@ServiceConnection(name = "redis")
GenericContainer&lt;?&gt; redisContainer() {
    return new GenericContainer&lt;&gt;(DockerImageName.parse("redis:latest"))
            .withExposedPorts(6379)
            .withReuse(true);
}</pre>

```
Given the experimental state there are still some limitations which you will have to keep in mind which are document in the
```

[announcement post](https://newsletter.testcontainers.com/announcements/enable-reusable-containers-with-a-single-click)

```
.
```

#### Spring Boot devtools with @RestartScope {#_spring_boot_devtools_with_restartscope}

The second option requires you to annotate the desired containers with `@RestartScope`, and to have devtools set up.  

After which they're no longer restarted when devtools restarts your application.

For devtools we'll need to add this to our pom.xml file:

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;dependency&gt;
    &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
    &lt;artifactId&gt;spring-boot-devtools&lt;/artifactId&gt;
    &lt;optional&gt;true&lt;/optional&gt;
&lt;/dependency&gt;</pre>

or our Gradle build file:

<pre class="EnlighterJSRAW" data-enlighter-language="groovy">dependencies {
    developmentOnly("org.springframework.boot:spring-boot-devtools")
}</pre>

and then we just need to annotate our container(s)

<pre class="EnlighterJSRAW" data-enlighter-language="java">@Bean
@ServiceConnection(name = "redis")
@RestartScope
GenericContainer&lt;?&gt; redisContainer() {
    return new GenericContainer&lt;&gt;(DockerImageName.parse("redis:latest"))
            .withExposedPorts(6379);
}</pre>

**Testcontainers desktop app** {#_testcontainers_cloud_desktop_client}
----------------------------------------------------------------------

This software is not needed, but it's still a nice extra utility to get even more mileage out of your testcontainer usage.

It was recently ([donated to the community](https://twitter.com/bsideup/status/1682091750561554457)) as a free testcontainers desktop application, and can be downloaded from <https://testcontainers.com/desktop/> and is available for Windows, Mac \& Linux.

There are some quite useful features in there such as:

* proxying a service to a fixed port to facilitate debugging
* tracking of used images \& test parallelization
* functionality to switch local runtime for (cloud based) testcontainers
* tweak Testcontainer behaviour such as freezing containers on shutdown/enable reusable testcontainers
* ...

Wrap up {#_wrap_up}
-------------------

I hope this brief showcase was helpful and offered some new insights as to how to ease local development.

In case of any questions, feel free to reach out.

The people at the [testcontainers slack](https://slack.testcontainers.org/) are also very kind, and always willing to help out.

References {#_references}
-------------------------

* [Testcontainers](https://testcontainers.com/): the official Testcontainers website
* [Testcontainers in the cloud](https://testcontainers.com/cloud/)
* [spring-boot-devtools](https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.devtools)
* [Provided service connections](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#features.testing.testcontainers.service-connections)
