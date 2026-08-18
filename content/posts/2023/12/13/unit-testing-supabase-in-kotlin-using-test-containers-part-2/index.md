---
title: "[Unit] Testing Supabase in Kotlin using Test Containers - PART 2"
slug: "unit-testing-supabase-in-kotlin-using-test-containers-part-2"
date: "2023-12-13T08:37:47+00:00"
lastmod: "2023-12-13T08:48:41+00:00"
description: "In this article we continue diving into TestContainers and Supabase, and run unit tests against a full local self-hosted Supabase."
authors:
  - "jlengrand"
image: "Screenshot-2023-10-11-at-23.57.36-1.png"
categories:
  - "Kotlin"
  - "Testing"
tags:
related_posts:
  - "unit-testing-supabase-in-kotlin"
  - "api-mocking-essential-and-redundant"
  - "beyond-pass-fail-a-modern-approach-to-java-integration-testing"
  - "faster-integration-tests-with-reusable-testcontainers"
frozen: false
---

*TL;DR : You can run a full Supabase instance inside Test Containers quite easily. [See this repository](https://github.com/jlengrand/supabase-testcontainers-kotlin?ref=lengrand.fr).*

[In my last article](https://foojay.io/today/unit-testing-supabase-in-kotlin/), I was listing a few attempts I had done at running tests against my Kotlin Supabase application. The way the Supabase-Kt library is built makes it hard to mock, and I ended up building a minimal Docker Compose setup that was mimicking a Supabase instance.

In this second part, we're gonna push the madness further and actually run a FULL SUPABASE instance locally, still using [Test Containers](https://testcontainers.com/?ref=lengrand.fr).

Right after I finished pushing my repository last week, I realised that Supabase actually offered [a Docker Compose file to self-host their platform](https://supabase.com/docs/guides/self-hosting/docker?ref=lengrand.fr). So I decided to push the madness further and see how easy it was to use that file inside TestContainers. In short : Relatively easy.

### The setup

The setup isn't actually much different from my homecrafted Docker Compose. version. Here it is in its entirety.

A few notable things:

* I'm relying on a local clone of Supabase, and point a ComposeContainer to the `src/test/resources/supabase/docker/docker-compose.yml` file.
* The setup uses an `.env` file, so I use a `dotenv` implementation to grab the parameters there and make the code slightly dynamic.
* I have to run a database statement to populate and flush my database in between tests. The Docker Compose setup from Supabase comes with persistent volumes, which needs to be accounted for.
* I don't have test for those here, but all services (auth, functions, storage), ... should actually be supported, given that we're running a full local instance.

```
import io.github.cdimascio.dotenv.dotenv
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.testcontainers.containers.ComposeContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File
import java.sql.DriverManager

@Testcontainers
class MainKtTest {

    @Test
    fun testEmptyPersonTable(){
        runBlocking {
            val result = getPerson(supabaseClient)
            assertEquals(0, result.size)
        }
    }

    @Test
    fun testSavePersonAndRetrieve(){
        val randomPersons = listOf(Person("Jan", 30), Person("Jane", 42))

        runBlocking {
            val result = savePerson(randomPersons, supabaseClient)
            assertEquals(2, result.size)
            assertEquals(randomPersons, result.map { it.toPerson() })

            val fetchResult = getPerson(supabaseClient)
            assertEquals(2, fetchResult.size)
            assertEquals(randomPersons, fetchResult.map { it.toPerson() })
        }
    }

    companion object {

        private const val DOCKER_COMPOSE_FILE = "src/test/resources/supabase/docker/docker-compose.yml"
        private const val ENV_LOCATION = "src/test/resources/supabase/docker/.env" // We grab the JWT token from here

        val dotenv = dotenv{
            directory = File(ENV_LOCATION).toString()
        }

        private val jwtToken = dotenv["SERVICE_ROLE_KEY"]
        private val dbPassword = dotenv["POSTGRES_PASSWORD"]
        private val db = dotenv["POSTGRES_DB"]

        private lateinit var supabaseClient: SupabaseClient

        @Container
        var container: ComposeContainer = ComposeContainer(File(DOCKER_COMPOSE_FILE))
            .withExposedService("kong", 8000)
            .withExposedService("db", 5432) // Handy but not required

        @JvmStatic
        @AfterAll
        fun tearDown() {
            val dbUrl = container.getServiceHost("db", 5432) + ":" + container.getServicePort("db", 5432)

            val jdbcUrl = "jdbc:postgresql://$dbUrl/$db"
            val connection = DriverManager.getConnection(jdbcUrl, "postgres", dbPassword)

            try {
                val query = connection.prepareStatement(
                    """
            drop table public.person;
        """
                )

                query.executeQuery()
            } catch (ex: Exception) {
                println(ex)
            }
        }

        @JvmStatic
        @BeforeAll
        fun setUp() {
            val supabaseUrl = container.getServiceHost("kong", 8000) + ":" + container.getServicePort("kong", 8000)
            val dbUrl = container.getServiceHost("db", 5432) + ":" + container.getServicePort("db", 5432)

            supabaseClient = createSupabaseClient(
                supabaseUrl = "http://$supabaseUrl",
                supabaseKey = jwtToken
            ) {
                install(Postgrest)
            }

            val jdbcUrl = "jdbc:postgresql://$dbUrl/$db"
            val connection = DriverManager.getConnection(jdbcUrl, "postgres", dbPassword)

            try {
                val query = connection.prepareStatement(
                    """
                create table
                    public.person (
                                    id bigint generated by default as identity not null,
                                    timestamp timestamp with time zone null default now(),
                                    name character varying null,
                                    age bigint null
                ) tablespace pg_default;
                """
                )

                query.executeQuery()
            } catch (ex: Exception) {
                println("Error is fine here. This should actually run only once")
                println(ex) // Might be fine, this should actually run only once
            }
        }
    }
}
```

To achieve those results, a few manual steps are required. The Docker Compose file provided by Supabase uses `container_name` parameters, [which aren't supported by Test Containers](https://github.com/testcontainers/testcontainers-java/pull/2741?ref=lengrand.fr).

I needed to :

* Clone the Supabase repository locally
* Copy the env file
* Run some magic to remove `container_name`
* Once in a while, the Supabase repository will have to be pulled

### The results

The results are as outrageous as I expected them to be, if not more : **All tests are running fine, though it takes almost 1 minute to run them** . The `ComposeContainer` is starting no less than 12 containers (!!!) so it is to be expected.

Obviously, that setup is not to be used for unit testing. That being said, I find it absolutely freaking cool to be able to recreate your complete environment locally that easily, and I'd definitely consider that an option for bigger integration tests. The confidence I didn't have with my home brewed Docker Compose file is much higher now, given that it's directly provided by Supabase. No network needed to run my tests, pretty cool.

![\[Unit\] Testing Supabase in Kotlin using Test Containers - PART 2](https://lengrand.fr/content/images/2023/10/image.png)  
*Gradle running those massive tests just fine*

### What more

My original complete intent was to build a small layer on top of the Docker Compose file, kinda like AtomoicJar does it with its [modules](https://github.com/testcontainers/testcontainers-java/blob/57ca6ad4f0a6ee8bb5feaf428b5c66c7d25259c4/modules/kafka/src/main/java/org/testcontainers/containers/KafkaContainer.java?ref=lengrand.fr#L28). It would have been cool to have a simple interface for a Supabase instance to start, while providing a locally for starting scripts, user roles, maybe a new set of credentials, ...

Here is how they describe it for NGinx for example. I would have loved to have something similar:

```
@Rule
public NginxContainer<?> nginx = new NginxContainer<>(NGINX_IMAGE)
    .withCopyFileToContainer(MountableFile.forHostPath(tmpDirectory), "/usr/share/nginx/html")
    .waitingFor(new HttpWaitStrategy());
```

All of the implementation I've seen extend from `GenericContainer` though, not `ComposeContainer` so I've decided to hold that off and keep it simple for now.

Could maybe be something for the future, who knows.

### In conclusion

That was a fun experiment, in which I've learnt more about TestContainers 😊. I'm as happy as usual with the way Supabase shows love for their users. Providing a seemless Docker Compose like this allows for a great experience. And I'm also impressed with TestContainers and how they can run such complex flaws without breaking a sweat!

If anything, I'd like them to at least ignore the `container_name` parameter if possible. I've seen many folks being blocked by it, and I can imagine many cases, like this one where people are not in control of their compose file. I don't necessarily ask for support, but an option to ignore without throwing an exception would be great.

That's it folks, till next time!
