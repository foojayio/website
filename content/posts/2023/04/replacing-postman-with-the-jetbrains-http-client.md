---
title: "Replacing Postman with the Jetbrains HTTP Client"
slug: "replacing-postman-with-the-jetbrains-http-client"
date: "2023-04-13T06:59:28+00:00"
lastmod: "2023-04-13T07:09:10+00:00"
description: "How to use the JetBrains HTTP Client to replace Postman, test third party APIs, share requests and run them in CI all of that within minutes!"
canonical: "https://lengrand.fr/replacing-postman-in-seconds-with-the-jetbrains-http-client/"
authors:
  - "jlengrand"
image: "/images/posts/2023/04/replacing-postman-with-the-jetbrains-http-client/headpostman.png"
categories:
  - "IntelliJ IDEA"
  - "Kotlin"
  - "Tools"
tags:
related_posts:
  - "getting-started-with-openapi-generators-tips-tricks"
  - "building-command-line-interfaces-with-kotlin-using-picocli"
  - "kover-code-coverage-plugin-for-kotlin"
  - "avoid-stringly-typed-in-kotlin"
enlighterjs: true
frozen: false
---

***TL;DR: I've created the first version of an openapi-generator for the JetBrains HTTP Client, and together with the CLI runner it allows you to play against APIs without ever going out of your terminal and it can even run in your CI/CD pipeline.** [**See repository here**](https://github.com/jlengrand/dotaClient "See repository here")*.

I work a lot with APIs, whether for an app I develop myself, or to interact with others. Most people I know tend to use Postman for this. But [I personally don't quite like the product any more](https://twitter.com/jlengrand/status/1620343955127689216 "I personally don't quite like the product any more"), and it forces me to move out of my IntelliJ environment which really kills my productivity.

With a combination of the [JetBrains HTTP Client](https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html "Jetbrains HTTP Client") and [OpenAPI generators](https://openapi-generator.tech/ "OpenAPI generators") we can do (in my opinion) better, in a semi automated way and even reuse our code on our CI/CD. Let's dive into how!

Quick Introduction to the JetBrains HTTP Client {#h2-0-quick-introduction-to-the-jetbrains-http-client}
-------------------------------------------------------------------------------------------------------

A lot of people don't know about the JetBrains HTTP Client, which is a JetBrains proprietary text file format that allows you to run API requests easily.

One thing I love about it is that it's integrated into the IDE, and it can generate those requests for you.

For example, looking at this [Spring Boot Kotlin Sample](https://github.com/spring-guides/tut-spring-boot-kotlin "Spring Boot Kotlin Sample"), next to each Mapping there is a "Open in HTTP Client" option in the gutter.

<img fetchpriority="high" decoding="async" class="size-medium wp-image-65438" src="/images/posts/2023/04/replacing-postman-with-the-jetbrains-http-client/openin-700x197.png" alt="The &quot;Open in HTTP Client&quot; option in the gutter of our IDE" width="700" height="197">

<br />

*The "Open in HTTP Client" option in the gutter of our IDE.*

When clicking it, it will generate a scratch file for this request:

<img decoding="async" class="size-medium wp-image-65439" src="/images/posts/2023/04/replacing-postman-with-the-jetbrains-http-client/generated-700x161.png" alt="The generated request
" width="700" height="161">

<br />

*The generated request.*

You can do many things with those requests, like setting Content-Type, sending body payload and more but [I'll direct you to the documentation](https://www.jetbrains.com/help/idea/exploring-http-syntax.html "I'll direct you to the documentation") for more.

The one thing that I want to show you here is that it defines variables for you that you can fill in using a separate configuration file. Pick an environment, and fill in a value for the variables you need. You can either create a public file, or a private one for, say, API Keys.

<img decoding="async" class="size-medium wp-image-65440" src="/images/posts/2023/04/replacing-postman-with-the-jetbrains-http-client/env-700x241.png" alt="Me creating a Public Environment file
" width="700" height="241">

<br />

*Me creating a Public Environment file.*

Running the request gets me the response, as expected, and it's even saved in a log file for me.

<img loading="lazy" decoding="async" class="size-medium wp-image-65441" src="/images/posts/2023/04/replacing-postman-with-the-jetbrains-http-client/running-471x510.png" alt="Running the request with the slug set to ispum returns a valid response" width="471" height="510">

<br />

*Running the request with the slug set to ispum returns a valid response.*

The best thing about all this is that everything is a text file, so I can put my request file with its config in a folder in my repository, pushes it to my repository together with my source files and I suddenly have an easy way to interact with my API.

I hope that by this time you're convinced of the value of the Client 😊. But wait, we can do much more!

Generating Full API Clients {#h2-1-generating-full-api-clients}
---------------------------------------------------------------

Many of you are probably aware of the OpenAPI specification (formerly swagger files). It is basically a standard to describe your API. If you write a spec file for it, you basically write a "contract" for how your API works.

The nice thing about it is that you can then use this contract to automagically generate a lot of things for your API! That can be clients, in the language of your choice, mock servers, or even documentation. There is a great project for this, conveniently called [OpenAPI Generator](https://openapi-generator.tech/ "OpenAPI Generator").

During the Christmas holidays, I set myself on a quest to create a simple generator for the [JetBrains HTTP Client](https://lengrand.fr/tips-on-getting-started-with-openapi-generators/ "Jetbrains HTTP Client"), which didn't exist yet. As of yesterday, it's released! Let's look through it with an example!

I'm a big DOTA 2 player, and let's imagine I want to start playing with the [Open DOTA API](https://docs.opendota.com/ "Open DOTA API"). Conveniently, it provides us with an [exhaustive OpenAPI specification file](https://api.opendota.com/api "exhaustive OpenAPI specification file").

Let's have a quick look:

<img loading="lazy" decoding="async" class="size-medium wp-image-65442" src="/images/posts/2023/04/replacing-postman-with-the-jetbrains-http-client/spec-510x510.png" alt="The beginning of the OpenAPI Spec file
" width="510" height="510">

<br />

*The beginning of the OpenAPI Spec file.*

As we can see, it describes the location of the API, as well as what kind of HTTP Calls you can make with the associated parameters.

Let's generate a full HTTP Client for it. First, we install the OpenAPI Generator:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">$ brew install openapi-generator</pre>

Then, we generate the client in a new folder

<pre class="EnlighterJSRAW" data-enlighter-language="generic">$ openapi-generator generate -i https://api.opendota.com/api -g jetbrains-http-client -o dotaClient</pre>

✨That's it!✨

When we open the folder in IntelliJ, we're presented with a few very nice things ([here is the GitHub repo if you want to see it for yourself](https://github.com/jlengrand/dotaClient "Here is the GitHub repo if you want to see it for yourself")):

* First, a complete documentation with all available endpoints and how they work in the README. Each call is clickable and link to locations in our source code.

<img loading="lazy" decoding="async" class="size-medium wp-image-65443" src="/images/posts/2023/04/replacing-postman-with-the-jetbrains-http-client/doc-615x510.png" alt="A screenshot of the Dota Client README
" width="615" height="510">

<br />

*A screenshot of the Dota Client README.*

* Then, an Apis folder with all available calls and their related documentation. Here is the generated code for the Heroes endpoints:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">## HeroesApi

### GET /heroes
# @name heroesGet
GET http://api.opendota.com/api/heroes

### GET /heroes/{hero_id}/durations
# @name heroesHeroIdDurationsGet
GET http://api.opendota.com/api/heroes/{{hero_id}}/durations

### GET /heroes/{hero_id}/itemPopularity
# @name heroesHeroIdItemPopularityGet
GET http://api.opendota.com/api/heroes/{{hero_id}}/itemPopularity

### GET /heroes/{hero_id}/matches
# @name heroesHeroIdMatchesGet
GET http://api.opendota.com/api/heroes/{{hero_id}}/matches

### GET /heroes/{hero_id}/matchups
# @name heroesHeroIdMatchupsGet
GET http://api.opendota.com/api/heroes/{{hero_id}}/matchups

### GET /heroes/{hero_id}/players
# @name heroesHeroIdPlayersGet
GET http://api.opendota.com/api/heroes/{{hero_id}}/players</pre>

Now let's create en environment file and run all those calls for a given hero. Let's use the id of my favourite hero: Crystal Maiden.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">{
  "dev": {
    "hero_id": "5"
  }
}</pre>

Just like this, I can start running queries against the API, and for example see which players have the most wins with Crytal Maiden:

<img loading="lazy" decoding="async" class="size-medium wp-image-65444" src="/images/posts/2023/04/replacing-postman-with-the-jetbrains-http-client/request-700x219.png" alt="A screenshot of the results of running the Heroes to Player endpoint
" width="700" height="219">

<br />

*A screenshot of the results of running the Heroes to Player endpoint.*

I can also decide to run the Health endpoint and see whether the DOTA or Steam servers are down. 😊

<img loading="lazy" decoding="async" class="size-medium wp-image-65445" src="/images/posts/2023/04/replacing-postman-with-the-jetbrains-http-client/request2-700x227.png" alt="Results of running a query against the Health endpoint
" width="700" height="227">

<br />

*Results of running a query against the Health endpoint.*

Objective reached! I can play against external APIs locally, in source code, without having to use any tool like Postman and without going out of IntelliJ.

Adding Tests {#h2-2-adding-tests}
---------------------------------

Running endpoints is nice, but we also want to be able to make sure expectations are fulfilled!

Thankfully, there is a way to do this the the HTTP Request client:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">### GET /heroes
# @name heroesGet
GET http://api.opendota.com/api/heroes

&gt; {%
    client.test("Request executed successfully", function() {
        // client.assert(response.status === 200, "Response status is not 200");
        client.assert(response.body.length == 126, "DOTA 2 currently has 123 heroes");
    });
%}
This</pre>

This should fail, as DOTA 2 currently has 123 heroes:

<img loading="lazy" decoding="async" class="size-medium wp-image-65446" src="/images/posts/2023/04/replacing-postman-with-the-jetbrains-http-client/bingo-700x107.png" alt="Bingo!" width="700" height="107">

<br />

*Bingo!*

Going Even Further {#h2-3-going-even-further}
---------------------------------------------

I've always been a bit frustrated at JetBrains for their HTTP Client, because it was completely bound to Intellij. As amazing as it is, I can't spend a lot of time crafting nice custom API calls if I still have to create tests for my CI anyways. At the end of the day, it's double the work for me.

But last month, they announced something that filled me with joy: [A CI runner for the HTTP Request files](https://blog.jetbrains.com/idea/2022/12/http-client-cli-run-requests-and-tests-on-ci/ "A CI runner for the HTTP Request files")! **All of a sudden, all the work we did above became 1000x more useful!**

Let's have a look into it:

* We download the (preview, for now), tool and unzip it:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">$ curl -f -L -o ijhttp.zip "https://jb.gg/ijhttp/latest 
$ unzip ijhttp.zip</pre>

* Finally, we run it against the files we have generated above, with our environment file:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">$ ./ijhttp/ijhttp Apis/HeroesApi.http --env-file Apis/http-client.env.json --env dev</pre>

Et voilà!

<pre class="EnlighterJSRAW" data-enlighter-language="generic">$./ijhttp/ijhttp Apis/HeroesApi.http --env-file Apis/http-client.env.json --env dev
┌─────────────────────────────────────────────────────────────────────────────┐
│                      Running IntelliJ HTTP Client with                      │
├──────────────────────┬──────────────────────────────────────────────────────┤
│        Files         │ HeroesApi.http                                       │
├──────────────────────┼──────────────────────────────────────────────────────┤
│  Public Environment  │ hero_id = 5                                          │
├──────────────────────┼──────────────────────────────────────────────────────┤
│ Private Environment  │                                                      │
└──────────────────────┴──────────────────────────────────────────────────────┘
Request 'heroesGet' GET http://api.opendota.com/api/heroes
Failed HeroesApi.http#0 Request executed successfully
Request 'heroesHeroIdDurationsGet' GET http://api.opendota.com/api/heroes/5/durations
Request 'heroesHeroIdItemPopularityGet' GET http://api.opendota.com/api/heroes/5/itemPopularity
Request 'heroesHeroIdMatchesGet' GET http://api.opendota.com/api/heroes/5/matches
Request 'heroesHeroIdMatchupsGet' GET http://api.opendota.com/api/heroes/5/matchups
Request 'heroesHeroIdPlayersGet' GET http://api.opendota.com/api/heroes/5/players

6 requests completed, 1 have failed tests
RUN FAILED</pre>

We still have our failing test, which will fail our CI. Task failed successfully!

The last step to have a complete setup is to setup a CI action. Let's use GitHub actions for this, fix our failing test, and see what happens:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">name: Run API tests based on JetBrains HTTP Client

on: push

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          distribution: 'temurin'
          java-version: '17'
      - name: Downloads and runs the JetBrains HTTP Client CLI
        run: |
          curl -f -L -o ijhttp.zip "https://jb.gg/ijhttp/latest"
          unzip ijhttp.zip
          ./ijhttp/ijhttp Apis/HeroesApi.http --env-file Apis/http-client.env.json --env dev</pre>

We check out, set the Java version to 17, and run the ijhttp package like we did locally.

[Success](https://github.com/jlengrand/dotaClient/actions/runs/4073382476 "Success")! We now have reliable CI tests using JetBrains HTTP Client requests!

<img loading="lazy" decoding="async" class="size-medium wp-image-65447" src="/images/posts/2023/04/replacing-postman-with-the-jetbrains-http-client/action-700x352.png" alt="GitHub action run results" width="700" height="352">

<br />

*GitHub action run results.*

Next Steps {#h2-4-next-steps}
-----------------------------

There is so much more I could talk about, like the fact that the HTTP Client supports other protocols like GraphQL, handles redirections and more, but I'll keep it to this for now.

Do keep in mind that the [JetBrains HTTP Client generator](https://github.com/OpenAPITools/openapi-generator "Jetbrains HTTP Client generator") is still in a very early phase at the moment. For example, I have no support for authentication or most headers at the moment. It won't break anything, but you might have to add things manually to the generated files.

But hey, I do welcome requests and contributions!

Hope you found the read useful. Feel free to ping me on [Mastodon](https://mastodon.online/@jlengrand "Mastodon") or [Twitter](https://twitter.com/jlengrand "Twitter")!
