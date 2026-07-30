---
title: "Journeys in Java, Level 7: Externalize Microservice Configuration"
slug: "journeys-in-java-level-7-externalize-microservice-configuration"
date: "2023-01-30T15:43:16+00:00"
lastmod: "2023-01-30T15:43:18+00:00"
description: "Learn how to set up a service that hosts the Spring Cloud Config server and wire our Neo4j microservice as the config client service."
authors:
  - "jennifer-reif"
image: "/images/posts/2023/01/journeys-in-java-level-7-externalize-microservice-configuration/unsplash-canyon-road-bridge-scaled.jpeg"
categories:
  - "Databases"
  - "Graph"
  - "Java Core"
  - "Microservices"
  - "Neo4J"
  - "Spring"
tags:
related_posts:
  - "journeys-in-java-level-6-build-a-neo4j-microservice"
  - "journeys-in-java-level-5-building-an-empire-of-microservices"
  - "journeys-in-java-level-4-building-an-empire-of-microservices"
  - "journeys-in-java-level-10-service-discovery-with-eureka"
enlighterjs: true
frozen: false
---

Our decided next step for this project takes us in a new direction by adding externalized configuration for our applications.

We already saw how to do something similar with [MongoDB database credentials in Docker Compose](https://jmhreif.com/blog/microservices-level5/) to access a local database container, but what do we do when the database is hosted on public cloud or contains sensitive data?

We probably don't want to publish our credentials anywhere or even prevent accidental publishing.

This is where solutions such as [Spring Cloud Config](https://spring.io/projects/spring-cloud-config) come into play.

Our [Neo4j database free instance](https://dev.neo4j.com/aura-java) is hosted by Neo4j on a public cloud, so we can set the credentials in an external location (such as a local file or other system) use Spring Cloud Config to access those keys and pass them to the approved application.

In this article, we will walk through how to set up a service that hosts the Spring Cloud Config server and how to wire our existing Neo4j microservice (service4) as the config client service to utilize the credentials for accessing Neo4j.

Let's dive in!

Architecture {#_architecture}
-----------------------------

We began this microservices project with minimum functionality explained in the [level 1 blog post](https://jmhreif.com/blog/microservices-level1/). In our last post ([level6](https://jmhreif.com/blog/microservices-level6/)), we added a graph database to our microservices system to take advantage of data relationships between books, authors, and reviews.

With today's next step, we will add a new service to host managing the credentials and incorporate pulling credentials from there into our Neo4j service.

Here is the updated architecture:

![microservices level7](/images/posts/2023/01/journeys-in-java-level-7-externalize-microservice-configuration/microservices-level7.png)

We haven't incorporated our Neo4j service to the rest of our Docker Compose system of services because I felt that the configuration was the next logical step to avoid hard-coding database credentials in our application or compose file. The eventual goal is to add the MongoDB database credentials to Spring Cloud Config, as well, and remove all hard-coded credentials from the applications and Docker Compose.

Until then, our intermediary step is to add the `config-server` service and plug that into our existing `service4` that connects to Neo4j.

Spring Cloud Config {#_spring_cloud_config}
-------------------------------------------

The [project overview for Spring Cloud Config](https://spring.io/projects/spring-cloud-config) has a good explanation of design and usage, but I'll briefly put it into my own words here.

Spring Cloud Config gives us a way to externalize configuration, so that individual services can access only the properties they need to operate. Through a config server, we can set up varying environments so that credentials are organized and managed without manipulating values for testing. The project (by default) is set to use Git for the version-controlled properties files, but it can be set up to use other tools or implementations for managing environments and values.

Without further ado, let's start coding!

Applications - Spring Cloud Config Server {#_applications_spring_cloud_config_server}
-------------------------------------------------------------------------------------

As we have done with our previous services, we will use the Spring Initializr at [start.spring.io](https://start.spring.io/) to set up the outline for our config server application.

On the form, we can leave `Project`, `Language`, and `Spring Boot` version fields defaulted. Under the `Project Metadata` section, I updated the group name for my personal projects, but you are welcome to leave it defaulted. I named the artifact `config-server`, though naming is up to you, as long as we map it properly where needed. All other fields in this section can remain as they are. Under the `Dependencies` section, we need only `Config Server (Spring Cloud Config)`. Finally, the project template is complete, and we can click the `Generate` button at the bottom to download the project.

![springio configserver](/images/posts/2023/01/journeys-in-java-level-7-externalize-microservice-configuration/springio-configserver.png)

Figure 1. Spring Initializr project

Generating will download the project as a zip, so we can unzip it and move it to our project folder with the other services. Open the project in your favorite IDE and let's get coding!

The `pom.xml` contains the dependencies and software versions we set up on the Spring Initializr, so we can move to the `application.properties` file in the `src/main/resources` folder.

<pre class="EnlighterJSRAW" data-enlighter-language="text">server.port=8888
spring.cloud.config.server.git.uri=${HOME}/Projects/config/microservices-java-config</pre>

Just like with our other services, we need to specify a port number for this application to use so that its traffic doesn't conflict with our other services. The default port for Spring Cloud Config server is `8888`, so we will use that. Next, we will use [git](https://git-scm.com/) to version-track our local configuration file, so we need to specify the folder (or public repository URL, if we had that instead) as the value for the second property.

On to the project code!

### Config Server - project code {#_config_server_project_code}

There is very little we need to add, since we are using the default setup. We don't have any big customizations or features required for now. So, there is only one small annotation we need to add to the `ConfigServerApplication.java` class.

<pre class="EnlighterJSRAW" data-enlighter-language="java">@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(ConfigServerApplication.class, args);
	}
}</pre>

The `@EnableConfigServer` annotation notifies Spring that this application needs to operate as the config server.

Now we need to add our configuration file that will contain the database credentials!

Storing config values {#_storing_config_values}
-----------------------------------------------

We need an externalized place for our configuration values to reside, i.e. a file with our database credentials for config server to retrieve. Most of the tutorials you find on Spring Cloud Config recommend starting with a local config folder containing either a `.properties` or `.yaml` file. I went with the YAML file for this project.

A sample of the file is in the `microservices-java-config` folder of the [Github project](https://github.com/JMHReif/microservices-level7).

<pre class="EnlighterJSRAW" data-enlighter-language="text">spring:
  neo4j:
    uri: &lt;insert Neo4j URI here&gt;
    database: &lt;insert Neo4j database here&gt;
    authentication:
      username: &lt;insert Neo4j username here&gt;
      password: &lt;insert Neo4j password here&gt;</pre>

We will need to fill in the values for our Neo4j AuraDB free instance in place of the dummy URL, database, username, and password shown above. **Note:** Database should be `neo4j`, unless you have specifically used commands to change the default. Then, we need to save the file and check it into to [git](https://git-scm.com/) by running the next statements from the command line.

<pre class="EnlighterJSRAW" data-enlighter-language="shell">microservices-java-config % git init
microservices-java-config % git add
microservices-java-config % git commit -am "Initial commit"</pre>

Let's test our config server application!

Test Config Server {#_test_config_server}
-----------------------------------------

Start the application from your IDE or command line. To test, we need to figure out the correct URL to ping. First, we are running the application locally and set the port to `8888`, so the first part of the URL is `localhost:8888`. The rest of the URL is what confused me in most of the tutorials, but once you know what the application is looking for, it isn't as intimidating.

Many tutorials use the `/{application}/{profile}` notation. The `/{application}` piece references the name you give your *client* application. This is an arbitrary name, except that your config file (in our case, yaml) needs to have the same name. Config file naming follows the pattern `{application}-{profile}`. If you do not specify a `-{profile}` (e.g. development, production) on the config file name, it uses the *default* profile.

Since our config file is named `neo4j-client.yaml`, our application name is `neo4j-client`. We do not specify a profile on our config file name, so it will use the default. This means the `/{profile}` piece of the URL is `default`.

Our full URL for testing is then `localhost:8888/neo4j-client/default`!

![config server test](/images/posts/2023/01/journeys-in-java-level-7-externalize-microservice-configuration/config-server-test.png)

Figure 2. Neo4j config results

Next, we need to plug our client service (`service4`) in to use the config server we just set up.

Service4 - modifications {#_service4_modifications}
---------------------------------------------------

To start, we need to add a dependency to `service4` for the Spring Cloud Config client. Open the `pom.xml` and add the following items:

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;properties&gt;
	//java version property
	&lt;spring-cloud.version&gt;2021.0.3&lt;/spring-cloud.version&gt;
&lt;/properties&gt;
&lt;dependencies&gt;
	//other dependencies
	&lt;dependency&gt;
		&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
		&lt;artifactId&gt;spring-cloud-starter-config&lt;/artifactId&gt;
	&lt;/dependency&gt;
&lt;/dependencies&gt;
&lt;dependencyManagement&gt;
	&lt;dependencies&gt;
		&lt;dependency&gt;
			&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
			&lt;artifactId&gt;spring-cloud-dependencies&lt;/artifactId&gt;
			&lt;version&gt;${spring-cloud.version}&lt;/version&gt;
			&lt;type&gt;pom&lt;/type&gt;
			&lt;scope&gt;import&lt;/scope&gt;
		&lt;/dependency&gt;
	&lt;/dependencies&gt;
&lt;/dependencyManagement&gt;</pre>

On the [third line of the above code](https://github.com/JMHReif/microservices-level7/blob/main/service4/pom.xml#L18), we add a property for the Spring Cloud Version. This allows us to source this value anywhere it's needed in the pom. In the dependencies section, we need to add the config client dependency ([seventh line](https://github.com/JMHReif/microservices-level7/blob/main/service4/pom.xml#L34)), making this application a client that will use the config server. Last, but not least, we add a dependency management section ([line twelve](https://github.com/JMHReif/microservices-level7/blob/main/service4/pom.xml#L50)) to handle versioning of Spring Cloud.

Now we need to update the application properties in the `src/main/resources` folder.

<pre class="EnlighterJSRAW" data-enlighter-language="text">server.port=8083

spring.application.name=neo4j-client
spring.config.import=configserver:http://localhost:8888/</pre>

We leave the port property alone, but we can remove the database credential properties because those are being stored in the config server now. The next two properties specify the application name and where the config server is running. Remember that our application name and the name of our config file MUST match. So, that means our `spring.application.name` needs to be `neo4j-client`, as our config file name is `neo4j-client.yaml`. This is also the name that would be referenced between microservices or for service discovery, though we haven't delved into that part of microservices yet. 😉 Our config server is running locally and on the default config server port, so the value of the last property should look familiar.

This actually wraps up all of our changes to the Neo4j service! We don't need to change anything in the `Service4Application.java` class because all of our config values are injected as part of the environment, so everything operates in the background.

Let's test it!

Put it to the test {#_put_it_to_the_test}
-----------------------------------------

As always, we will kick things off from the bottom to the top. First, ensure the Neo4j AuraDB instance is still running. *\*Note:\* AuraDB free tier pauses automatically after 3 days. You can resume with the `play` icon on the instance.*

Next, we need to start our `config-server` application, either through the IDE or command line. Once that is running, we can start the `service4` application through the IDE or command line. Time to test the application using the following commands.

1. Test config server is working: open a browser and go to `localhost:8888/neo4j-client/default` or go to command line with `curl localhost:8888/neo4j-client/default`.
2. Test `service4` is live: open a browser and go to `localhost:8083/neo` or go to command line with `curl localhost:8083/neo`.
3. Test backend reviews api: open a browser and go to `localhost:8083/neo/reviews` or go to command line with `curl localhost:8083/neo/reviews`.
4. Test reviews api for a certain book: open a browser and go to `localhost:8083/neo/reviews/178186` or go to command line with `curl localhost:8083/neo/178186`.

And here is the resulting output from reviews api results!

![microservices lvl7 results](/images/posts/2023/01/journeys-in-java-level-7-externalize-microservice-configuration/microservices-lvl7-results.png)

Figure 3. Find 1000 reviews

![microservices lvl7 results book](/images/posts/2023/01/journeys-in-java-level-7-externalize-microservice-configuration/microservices-lvl7-results-book.png)

Figure 4. Find reviews by book

Wrapping up! {#_wrapping_up}
----------------------------

Today, we incorporated the Spring Cloud Config project to manage and provide database credentials to our Neo4j microservice.

We created a new service to host the config server and set up an external YAML file containing our database credentials.

We saw how to test those pieces and verify the config server was working before moving on to plug in our `service4` application as the client to use the values provided by the config server.

The client service didn't require too many code changes to do this, and then we tested those components (config server, config file, Neo4j microservice) together to ensure we could access our review data as expected.

There are still a few things that we can do on this topic.

* It would be nice to add our MongoDB credentials to the Spring Cloud Config server, as well, and remove the hard-coded values from Docker Compose.
* We also need to incorporate the Neo4j microservice and config service with the rest of our group in Docker Compose.

Happy coding!

Resources {#_resources}
-----------------------

* Github: [microservices-level7](https://github.com/JMHReif/microservices-level7) repository
* Github: [Meta repository for all related content](https://github.com/JMHReif/microservices-java)
* Neo4j AuraDB: [Create a FREE database](https://dev.neo4j.com/aura-java)
* Documentation: [Spring Cloud Config](https://docs.spring.io/spring-cloud-config/docs/current/reference/html/)
* Blog post: [Baeldung's guide to Spring Cloud Config](https://www.baeldung.com/spring-cloud-configuration)
* Video: [JavaBrain's walkthrough](https://www.youtube.com/watch?v=gb1i4WyWNK4)
