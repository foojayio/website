---
title: "Getting Started with Babel for Java and OpenJDK"
slug: "getting-started-with-babel-for-java"
date: "2020-09-29T07:57:36+00:00"
lastmod: "2021-08-23T12:36:52+00:00"
description: "Introdudcing frgaal, a retrofitting Java compiler, letting you use modern and experimental features, while keeping compatibility with your target runtime."
canonical: "https://medium.com/@toni.epple/use-next-generation-java-today-d3394efb8c24"
authors:
  - "eppleton"
image: "Favicon-3-2.png"
categories:
  - "Uncategorized"
tags:
related_posts:
enlighterjs: true
frozen: false
---

In the JavaScript world, developers have to deal with a multitude of JavaScript engines in different browsers and browser versions, while the language itself is evolving quickly. This is only possible because of clever tools like the retrofitting compiler Babel ([https://babeljs.io](https://babeljs.io/)). Babel backports new features to work on older JavaScript engines, allowing developers to use modern JavaScript language features and still be compatible with the majority of browsers out there.

In the Java world, the situation is much better because Java desktop applications are usually shipped with an embedded JRE and for server applications the JRE version is clearly defined. But while devs usually know the version of Java they need to support, they are often stuck with old versions of the language.

frgaal ([https://frgaal.org](https://frgaal.org/)) is a retrofitting compiler for Java that helps you with these situations. It allows you to use modern and even experimental features while still keeping compatibility with your target runtime.

frgaal: Frugal Czech Cake {#a849}
---------------------------------

frgaal, based [on the name of a Czech cake](http://czechgastronomy.com/frgaly/), and pronounced a bit like "frugal", targets all those developers who are stuck with a runtime that doesn't support modern language features.

Some examples where this might be useful are serverless functions, legacy applications, or simply the wish to try out new and experimental features without installing the latest Java daily builds.

### Serverless Java {#219f}

AWS Lambda is the most popular environment for serverless functions. Currently it only provides runtimes for Java 8 and 11 (<https://docs.aws.amazon.com/lambda/latest/dg/lambda-runtimes.html>).

In order to use newer features, you can add frgaal to your build and happily code away.

Once your target runtime is available, you can simply remove frgaal again, or continue updating your code with even newer features.

### Legacy applications {#67c9}

A very common warmup question at Java conferences is „What version of Java are you using?". The audience is supposed to raise their hand when the speaker calls out their version.

And almost everytime the poor people who still run their code on very old JREs are working at banks or insurance companies.

With frgaal, these developers can brush up their Java and use all these shiny new features without compromising compatibility.

### Trying out new features {#565d}

It's cool to read about new language features in upcoming versions of Java, but it would be nice to have an easy way to try them out.

Usually you'll need to download a JDK daily build or even build it from source. With frgaal it becomes much less painful.

How to use frgaal {#f48b}
-------------------------

The only thing you need to use it in a Maven build is to configure the maven-compiler-plugin.

<pre class="EnlighterJSRAW" data-enlighter-language="xml" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;build&gt;
    &lt;plugins&gt;
        &lt;plugin&gt;
            &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;
            &lt;artifactId&gt;maven-compiler-plugin&lt;/artifactId&gt;
            &lt;version&gt;3.8.1&lt;/version&gt;
            &lt;dependencies&gt;
                &lt;dependency&gt;
                    &lt;groupId&gt;org.frgaal&lt;/groupId&gt;
                    &lt;artifactId&gt;compiler-maven-plugin&lt;/artifactId&gt;
                    &lt;version&gt;14.0.2&lt;/version&gt;
                &lt;/dependency&gt;
            &lt;/dependencies&gt;
            &lt;configuration&gt;
                &lt;compilerId&gt;frgaal&lt;/compilerId&gt;
                &lt;source&gt;14&lt;/source&gt;
                &lt;target&gt;1.8&lt;/target&gt;
                &lt;compilerArgs&gt;
                    &lt;arg&gt;-Xlint:deprecation&lt;/arg&gt;
                    &lt;arg&gt;--enable-safe-preview&lt;/arg&gt;
                &lt;/compilerArgs&gt;
            &lt;/configuration&gt;
        &lt;/plugin&gt;
    &lt;/plugins&gt;
&lt;/build&gt;</pre>

If you would like to try it out right now, the only thing you need to do is clone this Github repository with a demo:

$ *git clone* [*https://github.com/eppleton/frgaal-demo.git*](https://github.com/eppleton/frgaal-demo.git)

Have a look at the demo code, it uses some new features like text blocks and enhanced "instanceof"-Syntax :

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

// Handler value: example.Handler
public class Handler implements RequestHandler&lt;Map&lt;String,String&gt;, String&gt;{
  Gson gson = new GsonBuilder().setPrettyPrinting().create();
  @Override
  public String handleRequest(Map&lt;String,String&gt; event, Context context)
  {
    LambdaLogger logger = context.getLogger();
        String response = """
            200 OK 
            Look a this multiline text block!
            ...""";
    // log execution details
    logger.log("ENVIRONMENT VARIABLES: " + gson.toJson(System.getenv()));
    logger.log("CONTEXT: " + gson.toJson(context));
    // process event
    logger.log("EVENT: " + gson.toJson(event));
    logger.log("EVENT TYPE: " + event.getClass().toString());
    return response;
  }
}</pre>

Building and running the project is no different from a regular Maven project. Assuming you have JDK 8 or newer just run:

*$ mvn clean install*

*$ java -jar target/demo-simple-1.0-SNAPSHOT.jar*

<pre class="EnlighterJSRAW" data-enlighter-language="raw" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">initial list content
is…[6, 1, 3, 5]
after sorting: [1, 3, 5, 6]
ok</pre>

That's it, three lines of code to try out new and experimental Java features without installing a preview JDK.

### I can't believe it's not Java! {#4946}

[frgaal](http://frgaal.org/) is built using the javac compiler from OpenJDK and you can compile valid Java code with it. Nevertheless, technically, it is not Java. This explains some of the odd wording on the project's website speaking of "a Java-like language", even though it only supports valid Java plus the experimental features.

The project patches javac 14 to allow it to generate bytecode for JDK8 and compiles the compiler itself for JDK 8. As a result, the frgaal compiler has all the features of JDK14, but can run on JDK 8 and emit JDK 8 compatible bytecode.

It seems really surprising that frgaal can produce bytecode from Java 14 syntax that is compatible with JDK 8, but it actually isn't, as JDK 9--14 contains no features that would require bytecode changes except modules and records. The `var` keyword is purely syntactic, so the compiled code remains the same. The same applies to """textblocks"""" as well. Switch expressions and "instanceof" do not require any special bytecode either. Most of these features just alter the AST of Java, so it's easy to emit JDK 8 compatible bytecode for the generator.

Summary {#0ada}
---------------

The [frgaal project](http://frgaal.org/) allows you to try or use new Java language features in a very simple and convenient way. It's the quickest way to try out new Java syntax and language features. More important, it's a way to use these features in your real projects and brush up your Java, even if you're limited to an older runtime like in a serverless environment.

The project is available under GPL v2.0 + Classpath Exception, and it's free to use to compile your commercial, private, or public projects. Check out the project site for more information and detailed guides for configuring the compiler or usage on the command line.

Used with permissions and thanks, [originally written by Anton Epple](https://medium.com/@toni.epple/use-next-generation-java-today-d3394efb8c24).
