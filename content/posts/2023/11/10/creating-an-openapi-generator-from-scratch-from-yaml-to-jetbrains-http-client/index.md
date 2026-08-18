---
title: "OpenAPI Generator: From YAML to JetBrains HTTP Client"
date: "2023-11-10T10:41:35+00:00"
lastmod: "2023-11-10T11:03:10+00:00"
description: "In this article, I'll be implementing an openapi generator from scratch so you can too! We'll be creating a very simple generator for the Jetbrains HTTP Client."
authors:
  - "jlengrand"
image: "F9_7HZZW4AEvcHF.jpeg"
categories:
  - "IntelliJ IDEA"
  - "Kotlin"
  - "Testing"
tags:
related_posts:
  - "getting-started-with-openapi-generators-tips-tricks"
  - "book-review-api-design-patterns"
  - "book-review-designing-apis-with-swagger-and-openapi"
  - "replacing-postman-with-the-jetbrains-http-client"
frozen: false
---

### **In this article, I'll be implementing an OpenAPI generator from scratch so you can too! We'll be creating a very simple generator for the Jetbrains HTTP Client.**

![Creating an OpenAPI generator from scratch : From YAML to JetBrains HTTP Client](https://lengrand.fr/content/images/2023/11/F9_7HZZW4AEvcHF.jpeg)

This is the online version of the article with the same name I wrote for the Dutch Java Magazine.

In the previous edition of the magazine, we discussed how the [JetBrains HTTP Client](https://www.jetbrains.com/help/idea/http-client-reference.html?ref=lengrand.fr) could be used to run HTTP Queries, automate them and even use them in your CI/CD pipelines. Just like [Postman](https://www.postman.com/?ref=lengrand.fr), but text based and can be part of your source code. Pretty cool.

For reference, it could look like this.

```
### Github API - Traffic per day

GET https://api.github.com/repos/{{owner}}/{{repo}}/traffic/views?per=day
Accept: application/vnd.github+json
X-GitHub-Api-Version: 2022-11-28
Authorization: Bearer {{github_key}}
```

With an environment file that looks like this:

```
{
  "dev": {
    "github_key": "not_that_easy",
    "owner": "jlengrand",
    "repo": "elm-firebase"
  }}
```

Now, that is very nice, but it requires a lot of manual work. **Wouldn't it be nice to be able to automate this?** Fortunately, most of us developing APIs also generate [OpenAPI](https://www.openapis.org/?ref=lengrand.fr) Specifications for them. When I looked however, there was no OpenAPI generator yet available for the Jetbrains HTTP Client. This is the story of how I've implemented it from scratch, and how you could too if you find yourself in the same situation! We'll use the JetBrains HTTP Client as a practical example, but the knowledge is transferable 🙂.

The OpenAPI generator project contains a core engine, as well as many packages with each a specific generator (Java, Ada, ...). The Jetbrains HTTP Client generator is actually published, and you can find [the merge request](https://github.com/OpenAPITools/openapi-generator/pull/14477/files?ref=lengrand.fr) as well as [the documentation](https://openapi-generator.tech/docs/generators/jetbrains-http-client?ref=lengrand.fr) on GitHub. If you have installed the last available OpenAPI generator release, you can actually try it out in a terminal as such :

    $ openapi-generator generate -i https://api.opendota.com/api -g jetbrains-http-client -o dotaClient

At its core, the idea of the OpenAPI generator is quite simple : It takes a specification file (JSON or YAML), transforms it into a set of objects in memory, and uses those objects to generate code / files using [mustache](https://mustache.github.io/?ref=lengrand.fr) template files.

You can actually find most of that logic in the `DefaultGenerator` source file of the library. There, you can see that actions are separated into 3 groups :

* models (basically data types)
* operations (actual operations)
* supporting files (environments, READMEs, ...).

Each of those is illustrated by a method, and takes separate objects as inputs:

```
…
    void generateModels(List<File> files, List<ModelMap> allModels, List<String> unusedModels) {…}
…
    void generateApis(List<File> files, List<OperationsMap> allOperations, List<ModelMap> allModels) {...}
…
    private void generateSupportingFiles(List<File> files, Map<String, Object> bundle) {...}
…
```

You can find [the actual source file on GitHub](https://github.com/OpenAPITools/openapi-generator/blob/78f3b19b58df699ef883b89a7a44531407377719/modules/openapi-generator/src/main/java/org/openapitools/codegen/DefaultGenerator.java?ref=lengrand.fr#L433). The objects for each of those methods are large `Map` classes that contain the necessary data in a semi-structured format. Here is an example of how `allModels` looks like:

![Creating an OpenAPI generator from scratch : From YAML to JetBrains HTTP Client](https://lh7-us.googleusercontent.com/NVgYf1bDqjbDEuvOkdmDReLlE0pVj2M3A64JpNQncZmFvosFOlA4tGzb1idJWD8cWexTV-tlzd18VJwJ0lgkqHYi80GZFqmj3S-oJtXwa9Y2LrRG1mU-gdlKfBIV0hZsIBm2arP9QlVTQlfIvuwS_Tc)

*A debug view of the allModels object*

As you can see, the object is essentially a lot of key/value pairs that are quite recognisable and directly come from the OpenAPI specification file.

To create our own client, we will take advantage of this nice work. Let's dive into it. We first clone the repository:

```
$ [email protected]:OpenAPITools/openapi-generator.git; cd openapi-generator
```

We can then use the `/new.sh` script to generate a few placeholder files for us. We'll be generating a client, and since we're not creating any bugs we won't be generating test files.

```
$ ./new.sh -n java-magazine-client -c

Creating modules/openapi-generator/src/main/java/org/openapitools/codegen/languages/JavaMagazineClientClientCodegen.java
Creating modules/openapi-generator/src/main/resources/java-magazine-client/README.mustache
Creating modules/openapi-generator/src/main/resources/java-magazine-client/model.mustache
Creating modules/openapi-generator/src/main/resources/java-magazine-client/api.mustache
Creating bin/configs/java-magazine-client-petstore-new.yaml
Finished.
```

The library nicely generates a client generator for us, as well as some template files and even a config so we can test it easily! The config uses the well known [petstore](https://spring-framework-petclinic-qctjpkmzuq-od.a.run.app/?ref=lengrand.fr) by default.

This is how the config file looks like:

```
generatorName: java-magazine-client
outputDir: samples/client/petstore/java/magazine/client
inputSpec: modules/openapi-generator/src/test/resources/3_0/petstore.yaml
templateDir: modules/openapi-generator/src/main/resources/java-magazine-client
additionalProperties:
  hideGenerationTimestamp: "true"
```

It nicely mentions to the OpenAPI generator library which generator to use, which sample OpenAPI file to use as input, where the mustache template files are located and where to store the output.

**Let's run it!**

```
$ ./mvnw clean package # package once to have the generator inside the generated jar
$ ./bin/generate-samples.sh bin/configs/java-magazine-client-petstore-new.yaml
```

Let's see what the generated output looks like:

![Creating an OpenAPI generator from scratch : From YAML to JetBrains HTTP Client](https://lh7-us.googleusercontent.com/QdjBEGu-dpqVoX_EMfZ5DZPG9ND4g3KjY1HR6Yo0LvChQmnRAPQBpBV-MAp6HNteXRbAb4ZOjQkQs9LhxBkj9KiZ7iRFdVKNTwNyNzNjCAQUWoc4RnipISl2kJcsKpauctW-D-Atkq7J5jEOyXcsA98)

*A tree view of the generated client*

We haven't done any work yet, and our generator is already spitting out things! Unfortunately, as we can see, all those files are empty. That's because our mustache template files also are empty. Let's fix that now!

We'll start by customizing the `JavaMagazineClientClientCodegen` to fit our needs. We want a very minimal implementation that fits in this article, so we'll actually decide to NOT implement any supporting files (the README), nor Models and instead focus solely on the API. The way to do this in a custom generator is to extend the `postProcessOperationsWithModels` from the `CodeGenConfig` interface. We change the `.zz` extension into `.http` files that will be recognised by IntelliJ. And because in this specific (simplistic) case, we will not need any alterations to the `OperationsMap` object we can actually only call the super method. Our final class looks like this:

```
package org.openapitools.codegen.languages;
import org.openapitools.codegen.*;
import java.io.File;
import java.util.*;
import org.openapitools.codegen.model.ModelMap;
import org.openapitools.codegen.model.OperationsMap;

public class JavaMagazineClientClientCodegen extends DefaultCodegen implements CodegenConfig {

    public CodegenType getTag() { return CodegenType.CLIENT; }

    public String getName() { return "java-magazine-client"; }

    public String getHelp() { return "Generates a java-magazine-client client."; }

    public JavaMagazineClientClientCodegen() {
        super();

        outputFolder = "generated-code" + File.separator + "java-magazine-client";
        apiTemplateFiles.put("api.mustache", ".http");
        embeddedTemplateDir = templateDir = "java-magazine-client";
        apiPackage = "Apis";
    }

    @Override
    public OperationsMap postProcessOperationsWithModels(OperationsMap objs, List<ModelMap> allModels) {
        return super.postProcessOperationsWithModels(objs, allModels);
    }
}
```

*Note: At first glance, the method and variable names may look a bit like magic. It is because most of the logic comes from DefaultGenerator, and CodeGenConfig. If you feel lost, those two classes are where it's at.*

Now that we have our baseline, what we want to do is work on our mustache files. Those files are basically templates that will be fed into the processing pipeline to generate our `.http` files.

We know we want one file per main API endpoint, with some documentation. We also want the [code\>@name\</code](/cdn-cgi/l/email-protection#b6d5d9d2d390d1c28df6d8d7dbd390dac28d99d5d9d2d3) unique identifier from the Jetbrains HTTP Client to be able [to reference our code](https://www.jetbrains.com/help/idea/exploring-http-syntax.html?ref=lengrand.fr#http_request_names). Finally, we want to add the supported content type for the calls.

If we look at the data object available for operations, we end up with this, where each `{{item}}` notation is the value of the item key inside the object.

```
## {{classname}}
{{#operations}}
{{#operation}}

### {{#summary}}{{summary}}{{/summary}}
# @name {{operationId}}
{{httpMethod}} {{basePath}}{{path}}
{{#consumes}}Content-Type: {{{mediaType}}}
{{/consumes}}
{{/operation}}
{{/operations}}
```

We can see it clearly if we look at the object during processing.

![Creating an OpenAPI generator from scratch : From YAML to JetBrains HTTP Client](https://lh7-us.googleusercontent.com/LW_g2JLAtaj1yq2LJeg0LBb0yTOy4ufSyurPNWW6XjKcdsv5GhVSASr6yWS7vYv3vmEuS9xmbZqzBa4Mqt76dg_Bv47gMoifUjxInC0-z1WkJYJRU3grz4RBApXJAZl4ZCx1irLQ69axWx5CQAe1fM0)  
![Creating an OpenAPI generator from scratch : From YAML to JetBrains HTTP Client](https://lh7-us.googleusercontent.com/HL2UN4ZvPrLgENa7ergdKqrLfcLFEp_N8I46e973y1QsKWO7nzaiqSyjypk-YAdA7_fA_HdjIJR2PwI9zWAym9tVMtSLksXMMZYVmWj6oJbr84V4A90PfMWjqQZ468lQmc9eN8CJEY8h3L4apRAGcgw)

*Note: Unfortunately, to my knowledge the best way to dive into the data model is still to go pause at runtime, I haven't yet found a complete data model documentation online. If you do, let me know!*

Let's rerun the generation and see what we get now:

```
$ ./mvnw package 
$ ./bin/generate-samples.sh bin/configs/java-magazine-client-petstore-new.yaml
```

![Creating an OpenAPI generator from scratch : From YAML to JetBrains HTTP Client](https://lh7-us.googleusercontent.com/hJ-53Q53ibGC0DuCaqu7yuoWpnLJB3d6g9SYUQ26-XeAVLW5JgavLfljBo08hnuyMwUsQo4Hgz5aBthp8L8jqFCpq1RBFWCz-PWFvpdofXDgR4o7QI_iyFKYMz4Afbet38-rEnzAuCXL4aCaL7ZUnZE)

*The result of the generation of our client*

Great! Only API files, and one per API, as wanted. Let's see what they contain!

```
## PetApi

### Add a new pet to the store
# @name addPet
POST http://petstore.swagger.io/v2/pet
Content-Type: application/json
Content-Type: application/xml

### Deletes a pet
# @name deletePet
DELETE http://petstore.swagger.io/v2/pet/{petId}
Looks great to me! Let's try to run one of the calls
```

Looks great to me! Let's try to run one of the calls.

![Creating an OpenAPI generator from scratch : From YAML to JetBrains HTTP Client](https://lh7-us.googleusercontent.com/LrHrgXoLj4y9_lyYCzAbtLGle6eDCqv-eyDWOVQLkCkdsb2LskNOrdhBEO0c0wDMuRb9EHbh3i21TpLEcntMyd_qhHqeYgIsQDzDOD7FCrf6VNsaxe3RY1OzrzB21uqo1SxwzlF7lXZ7oqyno3FAGSo)

*Running one of the calls that's just been generated*

It works just fine, and we get a 200 response as well. Success!

Now, there's only one little issue. The variables! In the Jetbrains HTTP Client format, [variables are written as](https://www.jetbrains.com/help/idea/exploring-http-syntax.html?ref=lengrand.fr#using_request_vars) `{{variable}}`. OpenAPI implementations only have single braces. We need to fix that!

What we'll be doing here is implement [a custom mustache lambda](https://mustache.github.io/mustache.5.html?ref=lengrand.fr) that doubles up the braces when it finds them. The lambda is essentially a string replacement.

```
public static class DoubleMustacheLambda implements Mustache.Lambda {
        @Override
        public void execute(Template.Fragment fragment, Writer writer) throws IOException {
            String text = fragment.execute();
            writer.write(text
                    .replaceAll("\\{", "{{")
                    .replaceAll("}", "}}")
            );
        }
    }
```

In order to make it available in our Generator, the openapi generator library offers the same mechanism as for the rest: We have to override a ready-made method.

```
@Override
protected ImmutableMap.Builder<String, Mustache.Lambda> addMustacheLambdas() {

    return super.addMustacheLambdas()
            .put("doubleMustache", new JavaMagazineClientClientCodegen.DoubleMustacheLambda());
}
```

The code above is added to our `JavaMagazineClientClientCodegen` class.

Next, we also need to modify our mustache template to add that lambda at the right location (around the `path` parameter). If that path is a variable, the braces will then be doubled.

```
## {{classname}}
{{#operations}}
{{#operation}}

### {{#summary}}{{summary}}{{/summary}}
# @name {{operationId}}
{{httpMethod}} {{basePath}}{{#lambda.doubleMustache}}{{path}}{{/lambda.doubleMustache}}
{{#consumes}}Content-Type: {{{mediaType}}}
{{/consumes}}
{{/operation}}
{{/operations}}
```

Et voilà! Running the sample again, we can now use variables as they are meant to be inside IntelliJ!

In the sample below, I'm using the following local environment file:

```
{
  "dev": {
    "petId": 3
  }
}
```

There is still a lot more to do with this generator. READMEs, payload, auth, headers, ... But now it's a matter of updating the mustache files as we want.

I'd love to have a more fleshed out generator, because it'd be an amazing and cheap way together with [the client CLI](https://www.jetbrains.com/help/idea/http-client-cli.html?ref=lengrand.fr) to have a great automated integration tests pipeline and get people running in second with your API.

I hope this article made you feel like trying to create your own OpenAPI generator. The only limit is your imagination! And as you can see, the merging process is actually relatively pleasant, because the volunteers of the project LOVE to see people bringing out new ideas to life.

Happy to hear your thoughts, as always!
