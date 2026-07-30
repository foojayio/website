---
title: "GraphQL, JavaScript, Preprocessor, SQL and more in Manifold"
slug: "graphql-javascript-preprocessor-sql-and-more-in-manifold"
date: "2023-07-17T12:42:59+00:00"
lastmod: "2023-07-17T12:43:03+00:00"
description: "In the final installment of the Manifold series we discuss the final integrations, review the benefits, and discuss using it in a project."
canonical: "https://debugagent.com/graphql-javascript-preprocessor-sql-and-more-in-manifold"
authors:
  - "shai-almog"
image: "/images/posts/2023/07/graphql-javascript-preprocessor-sql-and-more-in-manifold/thumbnail-24.jpg"
categories:
  - "Tutorials"
tags:
related_posts:
  - "manifold-vs-lombok-enhancing-java-with-property-support"
  - "operator-overloading-in-java"
  - "java-string-templates-today"
enlighterjs: true
frozen: false
---

We reached the final installment of our Manifold series but not the end of its remarkable capabilities.

Throughout this series, we have delved into various aspects of Manifold, highlighting its unique features and showcasing how it enhances Java development.

In this article, we will cover some of the remaining features of Manifold, including its support for GraphQL, integration with JavaScript, and the utilization of a preprocessor.

By summarizing these features and reflecting on the knowledge gained throughout the series, I hope to demonstrate the power and versatility of Manifold.

{{< youtube 7PzaVwZ68II >}}

<br />

Expanding Horizons with GraphQL Support {#h2-0-expanding-horizons-with-graphql-support}
---------------------------------------------------------------------------------------

GraphQL, a relatively young technology, has emerged as an alternative to REST APIs. It introduced a specification for requesting and manipulating data between client and server, offering an arguably more efficient and streamlined approach. However, GraphQL can pose challenges for static languages like Java.

Thankfully, Manifold comes to the rescue by mitigating these challenges and making GraphQL accessible and usable within Java projects. By removing the rigidness of Java and providing seamless integration with GraphQL, Manifold empowers developers to leverage this modern API style.

E.g., for this graphql file taken from the Manifold repository:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">query MovieQuery($genre: Genre!, $title: String, $releaseDate: Date) {
    movies(genre: $genre, title: $title, releaseDate: $releaseDate) {
        id
        title
        genre
        releaseDate
    }
}

query ReviewQuery($genre: Genre) {
    reviews(genre: $genre) {
        id
        stars
        comment
        movie {
            id
            title
        }
    }
}

mutation ReviewMutation($movie: ID!, $review: ReviewInput!) {
    createReview(movie: $movie, review: $review) {
        id
        stars
        comment
    }
}

extend type Query {
    reviewsByStars(stars: Int) : [Review!]!
}</pre>

We can write this sort of fluent code:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">var query = MovieQuery.builder(Action).build();
var result = query.request(ENDPOINT).post();
var actionMovies = result.getMovies();
for (var movie : actionMovies) {
  out.println(
    "Title: " + movie.getTitle() + "\n" +
    "Genre: " + movie.getGenre() + "\n" +
    "Year: " + movie.getReleaseDate().getYear() + "\n");
}</pre>

None of these objects need to be declared in advance, all we need are the GraphQL files.

Achieving Code Parity with JavaScript Integration {#h2-1-achieving-code-parity-with-javascript-integration}
-----------------------------------------------------------------------------------------------------------

In some cases, regulatory requirements demand identical algorithms in both client and server code. This is common for cases like interest rate calculations where in the past we used Swing applications to calculate and display the rate. Since both the backend and front-end were in Java it was simple to have a single algorithm.

However, this can be particularly challenging when the client-side implementation relies on JavaScript. Manifold provides a solution by enabling the integration of JavaScript within Java projects. By placing JavaScript files alongside the Java code, developers can invoke JavaScript functions and classes seamlessly using Manifold. Under the hood, Manifold utilizes Rhino to execute JavaScript, ensuring compatibility and code parity across different environments.

E.g., this JavaScript snippet:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">function calculateValue(total, year, rate) {
  var interest = rate / 100 + 1;
  return parseFloat((total * Math.pow(interest, year)).toFixed(4));
}</pre>

Can be invoked from Java as if it was a static method:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">var interest = Calc.calculateValue(4,1999, 5);
System.out.println(interest);</pre>

Preprocessor for Java {#h2-2-preprocessor-for-java}
---------------------------------------------------

While preprocessor-like functionality may seem unnecessary in Java due to its portable nature and JIT compilation, there are scenarios where conditional code becomes essential.

For instance, when building applications that require different behavior in on-premises and cloud environments, configuration alone may not suffice. It would technically work, but it might leave proprietary bytecode in on-site deployments and that isn't something we would want. There are workarounds for this but they are often very heavy-handed for something relatively simple.

Manifold addresses this need by offering a preprocessor-like capability. By defining values in `build.properties` or through environment variables and compiler arguments, developers can conditionally execute specific code paths. This provides flexibility and control without resorting to complex build tricks or platform-specific code.

With Manifold, we can write preprocessor code such as:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">#if SERVER_ON_PREM
    onPremCode();
#elif SERVER_CLOUD
    cloudCode();
#else
    #error “Missing definition: SERVER_ON_PREM or SERVER_CLOUD”
#endif</pre>

Reflecting on Manifold's Power {#h2-3-reflecting-on-manifold-s-power}
---------------------------------------------------------------------

Throughout this series, we have explored the many features of Manifold, including type-safe reflection, extension methods, operator overloading, property support, and more. These features demonstrate Manifold's commitment to enhancing Java development and bridging the gap between Java and modern programming paradigms. By leveraging Manifold, developers can achieve cleaner, more expressive code while maintaining the robustness and type safety of the Java language.

Manifold is an evolving project with many niche features I didn't discuss in this series including the latest one [SQL Support](https://github.com/manifold-systems/manifold/blob/master/manifold-deps-parent/manifold-sql/readme.md).

In a current Spring Boot project that I'm developing, I chose to use Manifold over Lombok. My main reasoning was that this is a startup project so I'm more willing to take risks. Manifold lets me tailor itself to my needs, I don't need many of the manifold features and indeed didn't add all of them.

I will probably need to interact with GraphQL though and this was a big deciding factor when picking Manifold over Lombok. So far I am very pleased with the results and features such as entity beans work splendidly with property annotations.

I do miss the Lombok constructor's annotations though, I hope something like that will eventually make its way into Manifold. Alternatively, if I find the time I might implement this myself.

Final Word {#h2-4-final-word}
-----------------------------

As we conclude this journey through Manifold, it's clear that this library offers a rich set of features that elevate Java development to new heights.

Whether it's simplifying GraphQL integration, ensuring code parity with JavaScript, or enabling conditional compilation through a preprocessor-like approach, Manifold empowers developers to tackle complex challenges with ease.

We hope this series has provided valuable insights and inspired you to explore the possibilities that Manifold brings to your Java projects.

Don't forget to check out the past installments in this series to get the full scope of Manifold's power.
