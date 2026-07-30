---
title: "Native-image with Micronaut | Foojay.io Today"
slug: "native-image-micronaut"
date: "2021-11-22T10:18:53+00:00"
lastmod: "2021-11-22T10:18:55+00:00"
description: "Last week, I wrote a native web app that queried the Marvel API using Spring Boot. This week, let's do the same with the Micronaut framework."
canonical: "https://blog.frankel.ch/native/micronaut/"
authors:
  - "nicolas-frankel"
image: "https://foojay.io/wp-content/uploads/2021/11/sally.jpeg"
categories:
  - "Kotlin"
  - "Performance"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Last week, I wrote a native web app that queried the Marvel API [using Spring Boot](https://foojay.io/today/native-spring-boot/). This week, I want to do the same with the Micronaut framework.

Creating a new project {#h2-0-creating-a-new-project}
-----------------------------------------------------

Micronaut offers two options to create a new project:

1. A [web UI](https://micronaut.io/launch):

<img fetchpriority="high" decoding="async" class="aligncenter wp-image-50794 size-medium" src="/images/posts/2021/11/native-image-micronaut/micronaut-launch-700x330.jpg" alt="Micronaut Launch Web UI" width="700" height="330">

   As for Spring Initializr, it provides several features:
   * Preview the project before you download it
   * Share the configuration
   * An API

   I do like that you can check the impact that the added features have on the POM.
2. A [Command-Line Interface](https://docs.micronaut.io/1.3.3/guide/index.html#buildCLI):In parallel to the webapp, you can install the on different systems. Then you can use the `mn` command to create new projects.

In both options, you can configure the following parameters:

* The build tool, Maven, Gradle, or Gradle with the Kotlin DSL
* The language, Java, Kotlin, or Groovy
* Micronaut's version
* A couple of metadata
* Dependencies

The application's code is on [GitHub](https://github.com/micronaut-projects/micronaut-starter). You can clone and adapt it, but as far as I know, it's not designed with extension in mind (yet?).

Bean configuration {#h2-1-bean-configuration}
---------------------------------------------

Micronaut's bean configuration relies on [JSR 330](http://javax-inject.github.io/javax-inject/). The JSR defines a couple of annotations, *e.g.* , `@Singleton` and `@Inject`, in the `jakarta.inject` package. Developers use them, and the service provider implements the specification.

`@Singleton` and its sibling `@ApplicationScoped` are meant to be used on our code. Our sample app needs to create an instance of `java.security.MessageDigest`, which cannot be annotated. To solve this problem, JSR 330 provides the `@Factory` annotation:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">@Factory                                                    // 1
class BeanFactory {

    @Singleton                                              // 2
    fun messageDigest() = MessageDigest.getInstance("MD5")  // 3
}</pre>

1. Bean-generating class
2. Regular scope annotation
3. Generate a message digest singleton

Micronaut also provides an automated discovery mechanism. Unfortunately, it doesn't work in Kotlin. You need to point to the package Micronaut explicitly should scan:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">fun main(args: Array&lt;String&gt;) {
    Micronaut.build().args(*args)
             .packages("ch.frankel.blog")
             .start()
}</pre>

Controller configuration {#h2-2-controller-configuration}
---------------------------------------------------------

Micronaut copied the `@Controller` annotation from Spring. You can use it in the same way. Likewise, annotate functions with the relevant HTTP method annotation.

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">@Controller
class MarvelController() {

    @Get
    fun characters() = HttpResponse.accepted&lt;Unit&gt;()
}</pre>

Non-blocking HTTP client {#h2-3-non-blocking-http-client}
---------------------------------------------------------

Micronaut provides two HTTP clients: a declarative one and a low-level one. Both of them are non-blocking.

The declarative client is for simple use-cases, while the low-level is for more complex ones. Passing parameters belongs to the complex category, so I chose the low-level one. Here's a sample of its API:

<img decoding="async" class="aligncenter wp-image-50795 size-medium" src="/images/posts/2021/11/native-image-micronaut/client-api-700x415.png" alt="Micronaut Client API class diagram" width="700" height="415">

<br />

The usage is straightforward:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">val request = HttpRequest.GET&lt;Unit&gt;("https://gateway.marvel.com:443/v1/public/characters")
client.retrieve(request, String::class.java)</pre>

Remember that we should get parameters from the request to the application and propagate them to the request we make to the Marvel API. Micronaut can automatically bind such query parameters to method parameters with the `@QueryValue` annotation for the first part.

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">@Get
fun characters(
    @QueryValue limit: String?,
    @QueryValue offset: String?,
    @QueryValue orderBy: String?
)</pre>

It's not possible to use Kotlin's string interpolation as these parameters are optional. Fortunately, Micronaut provides an `UriBuilder` abstraction, which follows the Builder pattern principles.

<img decoding="async" class="aligncenter wp-image-50796 size-medium" src="/images/posts/2021/11/native-image-micronaut/uribuilder-api-700x455.png" alt="Micronaut URI Builder class diagram" width="700" height="455">

<br />

We can use it like this:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">val uri = UriBuilder
            .of("${properties.serverUrl}/v1/public/characters")
            .queryParamsWith(
                mapOf("limit" to limit, "offset" to offset, "orderBy" to orderBy)
            )
            .build()

fun UriBuilder.queryParamsWith(params: Map&lt;String, String?&gt;) = apply {
    params.entries
        .filter { it.value != null }
        .forEach { queryParam(it.key, it.value) }
}</pre>

Parameterization {#h2-4-parameterization}
-----------------------------------------

Like Spring, Micronaut can bind application properties to Kotlin data classes. In Micronaut, the file is named `application.yml`. The file already exists and contains the `micronaut.application.name` key. We only need to add the additional data. I chose to put it under the same parent key, but there's no such constraint.

<pre class="EnlighterJSRAW" data-enlighter-language="yaml">micronaut:
  application:
    name: nativeMicronaut
    marvel:
      serverUrl: https://gateway.marvel.com:443</pre>

To bind, we need the help of two annotations:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">@ConfigurationProperties("micronaut.application.marvel")       // 1
data class MarvelProperties @ConfigurationInject constructor(  // 2
    val serverUrl: String,
    val apiKey: String,
    val privateKey: String
)</pre>

1. Bind the property class to the property file prefix
2. Allow using a data class. The `@ConfigurationInject` needs to be set on the constructor: it's a sign that the team could improve Kotlin integration in Micronaut.

Testing {#h2-5-testing}
-----------------------

Micronaut tests are based on the `@MicronautTest` annotation.

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">@MicronautTest
class MicronautNativeApplicationTest</pre>

We defined the properties of the above data class as non-nullable strings. Hence, we need to pass the value when the test starts. For that, Micronaut provides the `TestPropertyProvider` interface:

![Test property provider class diagram](/images/posts/2021/11/native-image-micronaut/testpropertyprovider-api.png)

We can leverage it to pass property values:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">@MicronautTest
class MicronautNativeApplicationTest : TestPropertyProvider {

    override fun getProperties() = mapOf(
        "micronaut.application.marvel.apiKey" to "dummy",
        "micronaut.application.marvel.privateKey" to "dummy",
        "micronaut.application.marvel.serverUrl" to "defined-later"
    )
}</pre>

The next step is to set up Testcontainers. Integration is provided out-of-the-box for popular containers, *e.g.*, Postgres, but not with the mock server. We have to write code to handle it.

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">@MicronautTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)             // 1
class MicronautNativeApplicationTest {

    companion object {

        @Container
        val mockServer = MockServerContainer(
            DockerImageName.parse("mockserver/mockserver")
        ).apply { start() }                                 // 2
    }
}</pre>

1. By default, one server is created for each test method. We want one per test class.
2. Don't forget to start it explicitly!

At this point, we can inject both the client and the embedded server:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">@MicronautTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MicronautNativeApplicationTest : TestPropertyProvider {

    @Inject
    private lateinit var client: HttpClient                                    // 1

    @Inject
    private lateinit var server: EmbeddedServer                                // 2

    companion object {

        @Container
        val mockServer = MockServerContainer(
            DockerImageName.parse("mockserver/mockserver")
        ).apply { start() }
    }

    override fun getProperties() = mapOf(
        "micronaut.application.marvel.apiKey" to "dummy",
        "micronaut.application.marvel.privateKey" to "dummy",
        "micronaut.application.marvel.serverUrl" to
            "http://${mockServer.containerIpAddress}:${mockServer.serverPort}" // 3
    )

    @Test
    fun `should deserialize JSON payload from server and serialize it back again`() {
        val mockServerClient = MockServerClient(
            mockServer.containerIpAddress,                                     // 3
            mockServer.serverPort                                              // 3
        )
        val sample = this::class.java.classLoader.getResource("sample.json")
                                                 ?.readText()                  // 4

        mockServerClient.`when`(
            HttpRequest.request()
                .withMethod("GET")
                .withPath("/v1/public/characters")
        ).respond(
            HttpResponse()
                .withStatusCode(200)
                .withHeader("Content-Type", "application/json")
                .withBody(sample)
        )

        // With `retrieve` you just get the body and can assert on it
        val body = client.toBlocking().retrieve(                               // 5
            server.url.toExternalForm(),
            Model::class.java                                                  // 6
        )
        assertEquals(1, body.data.count)
        assertEquals("Anita Blake", body.data.results.first().name)
    }
}</pre>

1. Inject the *reactive* client
2. Inject the embedded server, *i.e.*, the application
3. Retrieve the IP and the port from the mock server
4. Use Kotlin to read the sample file - there's no provided abstraction as in Spring
5. We need to block as the client is reactive
6. There's no JSON assertion API. The easiest path is to deserialize in a `Model` class, and then assert the object's state.

Docker and GraalVM integration {#h2-6-docker-and-graalvm-integration}
---------------------------------------------------------------------

As with Spring, Micronaut provides two ways to create native images:

1. On the local machine.It requires a local GraalVM installation **with** `native-image`.

<pre class="EnlighterJSRAW" data-enlighter-language="bash">mvn package -Dpackaging=native-image</pre>

2. In Docker. It requires a local Docker installation. 

<pre class="EnlighterJSRAW" data-enlighter-language="bash">mvn package -Dpackaging=docker-native</pre>

   Note that if you don't use a GraalVM JDK, you need to activate the \`graalvm\` profile.

<pre class="EnlighterJSRAW" data-enlighter-language="bash">mvn package -Dpackaging=docker-native -Pgraalvm</pre>

With the second approach, the result is the following:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">REPOSITORY             TAG       IMAGE ID         CREATED          SIZE
native-micronaut       latest    898f73fb44b0     33 seconds ago   85.3MB</pre>

The layers are the following:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">┃ ● Layers ┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Cmp   Size  Command
    5.6 MB  FROM e6b8cc5e282829d                                                #1
     12 MB  RUN /bin/sh -c ALPINE_GLIBC_BASE_URL="https://github.com/sgerrand/  #2
    3.5 MB  |1 EXTRA_CMD=apk update &amp;&amp; apk add libstdc++ /bin/sh -c if [[ -n "  #3
     64 MB  #(nop) COPY file:106f24caede12d6d28c6c90d9a3ae33f78485ad71e4157125  #4</pre>

1. Parent image
2. Alpine glibc
3. Additional packages
4. Our native binary

Miscellaneous comments {#h2-7-miscellaneous-comments}
-----------------------------------------------------

I'm pretty familiar with Spring Boot, much less with Micronaut.  

Here are several miscellaneous comments.

* Maven wrapper:When creating a new Maven project, Micronaut also configures the [Maven wrapper](https://github.com/takari/maven-wrapper).
* Documentation matrix:Micronaut guides each offer a configuration matrix. You choose both the language and the build tool, and you'll read the guide in the exact desired flavor.

<img loading="lazy" decoding="async" class="aligncenter size-medium wp-image-50798" src="/images/posts/2021/11/native-image-micronaut/micronaut-guide-700x291.jpg" alt="Micronaut guide choice matrix screenshot" width="700" height="291">

  <br />

  I wish more polyglot multi-platform frameworks' documentation would offer such a feature.
* Configurable packaging:Micronaut parameterizes the Maven's POM `packaging` so you can override it, as in the above native image generation. It's *very* clever!

  It's the first time that I have come upon this approach. I was so surprised when I created the project that I removed it (at first). Keep it.
* Code generation:Last but not least, Micronaut bypasses traditional reflection at runtime. To achieve that, it generates additional code at compile-time. The trade-off is slower build time vs. faster runtime. With Kotlin, I found an additional issue. Micronaut generates the additional code with `kapt`. Unfortunately, `kapt` has been pushed to [maintenance mode](https://kotlinlang.org/docs/kapt.html). Indeed, if you use a JDK with a version above 8, you'll see warnings when compiling.

  Integration of `kapt` with IntelliJ is poor at best. While all guides mention how to configure it, *i.e.*, enable annotation processing, it didn't work for me. I had to rebuild the application using the command line to be able to view the changes. It makes the development lifecycle much slower.

  The team is working toward KSP support, but it's an undergoing effort.

Conclusion {#h2-8-conclusion}
-----------------------------

Micronaut achieves the same result as Spring Boot. The Docker image's size is about 20% smaller. It's also more straightforward, with fewer layers, and based on Linux Alpine.

Kotlin works with Micronaut, but it doesn't feel "natural". If you value Kotlin benefits overall, you'd better choose Spring Boot. Otherwise, keep Micronaut but favor Java to avoid frustration.

Many thanks to [Ivan Lopez](https://twitter.com/ilopmar) for his review of this post.

The complete source code for this post can be found on [GitHub](https://github.com/ajavageek/micronaut-native) in Maven format.

**To go further:**

* [Micronaut Launch](https://micronaut.io/launch)
* [Defining Beans](https://docs.micronaut.io/3.1.3/guide/#beans)
* [Micronaut HTTP client](https://guides.micronaut.io/latest/micronaut-http-client-maven-kotlin.html)
* [Creating your first Micronaut Graal application](https://guides.micronaut.io/latest/micronaut-creating-first-graal-app-maven-kotlin.html)

*Originally published at [A Java Geek](https://blog.frankel.ch/native/micronaut/) on November 21^th^, 2021*

*[CLI]: Command-Line Interface
