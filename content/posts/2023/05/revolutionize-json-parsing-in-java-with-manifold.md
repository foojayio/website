---
title: "Revolutionize JSON Parsing in Java with Manifold"
slug: "revolutionize-json-parsing-in-java-with-manifold"
date: "2023-05-26T17:39:19+00:00"
lastmod: "2023-05-26T17:39:20+00:00"
description: "Parsing JSON in Java (and other formats) can be as easy as in JS. It can do much more while keeping the type-safety and deep IDE integration."
canonical: "https://debugagent.com/revolutionize-json-parsing-in-java-with-manifold"
authors:
  - "shai-almog"
image: "https://foojay.io/wp-content/uploads/2023/05/thumbnail-18.png"
categories:
  - "Developer Tools"
  - "Tutorials"
tags:
related_posts:
  - "relearning-java-thread-primitives"
  - "boldness-in-refactoring"
  - "devops-for-developers-continuous-integration-github-actions-and-sonar-cloud"
  - "boxlang-1-14-0-navigate-anything-jsonpath-comes-to-boxlangs-datanavigator"
enlighterjs: true
frozen: false
---

Java developers have often envied JavaScript for its ease of parsing JSON.

Although Java offers more robustness, it tends to involve more work and boilerplate code.

Thanks to the Manifold project, Java now has the potential to outshine JavaScript in parsing and processing JSON files.

Manifold is a revolutionary set of language extensions for Java that completely changes the way we handle JSON (and much more...).

{{< youtube AoBnGZ7q6rk >}}

<br />

Getting Started with Manifold {#h2-0-getting-started-with-manifold}
-------------------------------------------------------------------

The code for this tutorial can be found on my [GitHub page](https://github.com/shai-almog/java-book/). Manifold is relatively young but already vast in its capabilities. You can learn more about the project on their website and Slack channel.

To begin, you'll need to install the Manifold plugin, which is currently only available for JetBrains IDEs. The project supports LTS releases of Java, including the latest JDK 19.

We can install the plugin from IntelliJ/IDEAs settings UI by navigating to the marketplace and searching for Manifold. The plugin makes sure the IDE doesn't collide with the work done by the Maven/Gradle plugin.

![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/wj7zcz7c66ggh6fcvlu7.png)

Manifold consists of multiple smaller projects, each offering a custom language extension. Today, we'll discuss one such extension, but there's much more to explore.

### Setting Up a Maven Project {#h3-1-setting-up-a-maven-project}

To demonstrate Manifold, we'll use a simple Maven project (it also works with Gradle). We first need to paste the current Manifold version from their website and add the necessary dependencies. The main dependency for JSON is the `manifold-json-rt` dependency. Other dependencies can be added for YAML, XML, and CSV support. We need to add this to the `pom.xml` file in the project.

I'm aware of the irony where the boilerplate reduction for JSON starts with a great deal of configuration in the Maven build script. But this is configuration, not "actual code" and it's mostly copy \& paste. Notice that if you want to reduce this code the Gradle equivalent code is terse by comparison.

This line needs to go into the properties section:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">&lt;manifold.version&gt;2023.1.5&lt;/manifold.version&gt;
</pre>

The dependencies we use are these:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">&lt;dependency&gt;
    &lt;groupId&gt;systems.manifold&lt;/groupId&gt;
    &lt;artifactId&gt;manifold-json-rt&lt;/artifactId&gt;
    &lt;version&gt;${manifold.version}&lt;/version&gt;
&lt;/dependency&gt;</pre>

The compilation plugin is the boilerplate that weaves Manifold into the bytecode and makes it seamless for us. It's the last part of the pom setup:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">&lt;build&gt;
   &lt;plugins&gt;
       &lt;plugin&gt;
           &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;
           &lt;artifactId&gt;maven-compiler-plugin&lt;/artifactId&gt;
           &lt;version&gt;3.8.0&lt;/version&gt;
           &lt;configuration&gt;
               &lt;source&gt;19&lt;/source&gt;
               &lt;target&gt;19&lt;/target&gt;
               &lt;encoding&gt;UTF-8&lt;/encoding&gt;
               &lt;compilerArgs&gt;
                   &lt;!-- Configure manifold plugin--&gt;
                   &lt;arg&gt;-Xplugin:Manifold&lt;/arg&gt;
               &lt;/compilerArgs&gt;

               &lt;!-- Add the processor path for the plugin --&gt;
               &lt;annotationProcessorPaths&gt;
                   &lt;path&gt;
                       &lt;groupId&gt;systems.manifold&lt;/groupId&gt;
                       &lt;artifactId&gt;manifold-json&lt;/artifactId&gt;
                       &lt;version&gt;${manifold.version}&lt;/version&gt;
                   &lt;/path&gt;
               &lt;/annotationProcessorPaths&gt;
           &lt;/configuration&gt;
       &lt;/plugin&gt;
   &lt;/plugins&gt;
&lt;/build&gt;</pre>

With the setup complete, let's dive into the code.

Parsing JSON with Manifold {#h2-2-parsing-json-with-manifold}
-------------------------------------------------------------

We place a sample JSON file in the project directory under the resources hierarchy. I placed this file under `src/main/resources/com/debugagent/json/Test.json`:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">{
  "firstName": "Shai",
  "surname": "Almog",
  "website": "https://debugagent.com/",
  "active": true,
  "details":[
    {"key": "value"}
  ]
}</pre>

In the main class, we refresh the Maven project, and you'll notice a new Test class appears. This class is dynamically created by Manifold based on the JSON file. If you change the JSON and refresh Maven, everything updates seamlessly. It's important to understand that Manifold isn't a code generator. It compiles the JSON we just wrote into bytecode.

The Test class comes with several built-in capabilities, such as a type-safe builder API that lets you construct JSON objects using builder methods. You can also generate nested objects and convert the JSON to a string by using the `write()` and `toJson()` methods.

It means we can now write:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Test test = Test.builder().withFirstName("Someone")
        .withSurname("Surname")
        .withActive(true)
        .withDetails(List.of(
                Test.details.detailsItem.builder().
                        withKey("Value 1").build()
        ))
        .build();</pre>

Which will print out the following JSON:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">{
  "firstName": "Someone",
  "surname": "Surname",
  "active": true,
  "details": [
    {
      "key": "Value 1"
    }
  ]
}</pre>

We can similarly read a JSON file using code such as this:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Test readObject = Test.load().fromJson("""
        {
          "firstName": "Someone",
          "surname": "Surname",
          "active": true,
          "details": [
            {
              "key": "Value 1"
            }
          ]
        }
        """);</pre>

Note the use of Java 15 `TextBlock` syntax for writing a long string. The `load()` method returns an object that includes various APIs for reading the JSON. In this case, it is read from a `String` but there are APIs for reading it from a URL, file, etc.

Manifold supports various formats, including CSV, XML, and YAML, allowing you to generate and parse any of these formats without writing any boilerplate code or sacrificing type safety. In order to add that support we will need to add additional dependencies to the pom.xml file:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">&lt;dependency&gt;
    &lt;groupId&gt;systems.manifold&lt;/groupId&gt;
    &lt;artifactId&gt;manifold-csv-rt&lt;/artifactId&gt;
    &lt;version&gt;${manifold.version}&lt;/version&gt;
&lt;/dependency&gt;
&lt;dependency&gt;
    &lt;groupId&gt;systems.manifold&lt;/groupId&gt;
    &lt;artifactId&gt;manifold-xml-rt&lt;/artifactId&gt;
    &lt;version&gt;${manifold.version}&lt;/version&gt;
&lt;/dependency&gt;
&lt;dependency&gt;
    &lt;groupId&gt;systems.manifold&lt;/groupId&gt;
    &lt;artifactId&gt;manifold-yaml-rt&lt;/artifactId&gt;
    &lt;version&gt;${manifold.version}&lt;/version&gt;
&lt;/dependency&gt;
</pre>

With these additional dependencies, this code will print out the same data as the JSON file... With `test.write().toCsv()` the output would be:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">"firstName","surname","active","details"
"Someone","Surname","true","[manifold.json.rt.api.DataBindings@71070b9c]"</pre>

Notice that the Comma Separated Values (CSV) output doesn't include hierarchy information. That's a limitation of the CSV format and not the fault of Manifold.

With `test.write().toXml()` the output is familiar and surprisingly concise:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">&lt;root_object firstName="Someone" surname="Surname" active="true"&gt;
  &lt;details key="Value 1"/&gt;
&lt;/root_object&gt;</pre>

With `test.write().toYaml()` we again get a familiar printout:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">firstName: Someone
surname: Surname
active: true
details:
- key: Value 1</pre>

Working with JSON Schema {#h2-3-working-with-json-schema}
---------------------------------------------------------

Manifold also works seamlessly with JSON schema, allowing you to enforce strict rules and constraints. This is particularly useful when working with dates and enums. Manifold seamlessly creates/updates byte code that adheres to the schema, making it much easier to work with complex JSON data.

This schema is copied and pasted from the [Manifold github project](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-json):

<pre class="EnlighterJSRAW" data-enlighter-language="generic">{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "$id": "http://example.com/schemas/User.json",
  "type": "object",
  "definitions": {
    "Gender": {
      "type": "string",
      "enum": ["male", "female"]
    }
  },
  "properties": {
    "name": {
      "type": "string",
      "description": "User's full name.",
      "maxLength": 80
    },
    "email": {
      "description": "User's email.",
      "type": "string",
      "format": "email"
    },
    "date_of_birth": {
      "type": "string",
      "description": "Date of uses birth in the one and only date standard: ISO 8601.",
      "format": "date"
    },
    "gender": {
      "$ref" : "#/definitions/Gender"
    }
  },
  "required": ["name", "email"]
}</pre>

It's a relatively simple schema but I'd like to turn your attention to several things here. It defines name and email as required. This is why when we try to create a `User` object using a builder in Manifold, the `build()` method requires both parameters:

```java
User.builder("Name", "/cdn-cgi/l/email-protection")
```

That is just the start... The schema includes a date. Dates are a painful prospect in JSON, the standardization is poor and fraught with issues. The schema also includes a gender field which is effectively an enum. This is all converted to type-safe semantics using common Java classes such as LocalDate:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">User u = User.builder("Name", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="51343c30383d11353e3c30383f7f323e3c">[email&nbsp;protected]</a>")
       .withDate_of_birth(LocalDate.of(1999, 10, 11))
       .withGender(User.Gender.male)
       .build();</pre>

That can be made even shorter with static imports but the gist of the idea is clear. JSON is effectively native to Java in Manifold.

The Tip of The Iceberg {#h2-4-the-tip-of-the-iceberg}
-----------------------------------------------------

Manifold is a powerful and exciting project. It revolutionizes JSON parsing in Java but that's just one tiny portion of what it can do!

We've only scratched the surface of its capabilities in this post. In the next article, we'll dive deeper into Manifold and explore some additional unexpected features.

Please share your experience and thoughts about Manifold in the comments section. If you have any questions, don't hesitate to ask.
