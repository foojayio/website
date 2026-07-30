---
title: "Building Command Line Interfaces with Kotlin using picoCLI"
slug: "building-command-line-interfaces-with-kotlin-using-picocli"
date: "2021-09-23T07:05:12+00:00"
lastmod: "2021-09-23T07:08:55+00:00"
description: "Use cases where CLIs are a great idea, best practises, and discover one of the most used library for CLIs in the JVM world: picoCLI."
canonical: "https://lengrand.fr/building-command-line-interfaces-with-kotlin-using-picocli/"
authors:
  - "jlengrand"
image: "https://foojay.io/wp-content/uploads/2021/09/image1-700x135.png"
categories:
  - "Kotlin"
tags:
related_posts:
frozen: false
---

*TL;DR : We'll dive into a few interesting bits about CLI applications and picoCLI. But you can directly [see the code here](https://github.com/jlengrand/swacli), or view my related conference talk [here](https://www.youtube.com/watch?v=Rc_D4OTKidU&amp;t=460s).*

As a developer, there is a large chance that you use Command Line Interfaces (CLIs) every day. From Git, to kubectl or Maven, they are everywhere. In this article, we'll look into use cases where CLIs are a great idea. We'll also dive into best practises, and discover one of the most used library for CLIs in the JVM world: **[picoCLI](https://picocli.info/)**.

Why using Command Line interfaces {#h2-0-why-using-command-line-interfaces}
---------------------------------------------------------------------------

There are a couple reasons why a CLI can be a good use case for you and your users.

* In case you create fat jars that you run with input arguments, creating a thin CLI wrapper around it can be of great help for you and your users. Indeed, **it looks cleaner and abstracts the JVM ecosystem away** . This becomes easily clear with an example.

  <br />

<img fetchpriority="high" decoding="async" aria-describedby="caption-attachment-46678" class="size-medium wp-image-46678" src="/images/posts/2021/09/building-command-line-interfaces-with-kotlin-using-picocli/image1-700x135.png" alt="2 commands, one usiong the java -jar expression and the other using a native tool" width="700" height="135">

  Using a native application from the CLI is cleaner than using java -jar{#caption-attachment-46678}

  <br />

* Compared to running in an IDE, or via a GUI, CLIs make it very easy to interface (pipe) with other terminal tools, or scripts. <br />

<img decoding="async" aria-describedby="caption-attachment-46679" class="size-medium wp-image-46679" src="/images/posts/2021/09/building-command-line-interfaces-with-kotlin-using-picocli/image2-700x168.png" alt="2 bash commands, piped into each other" width="700" height="168">

  Usage of piping in Bash{#caption-attachment-46679}

  <br />

* CLIs usually also have a very clear and embedded man / help page. This helps your user use the tool in the right way and avoids having to look at documentation or ask for help. <br />

<img decoding="async" aria-describedby="caption-attachment-46680" class="size-medium wp-image-46680" src="/images/posts/2021/09/building-command-line-interfaces-with-kotlin-using-picocli/image3-700x335.png" alt="The help commands from swacli" width="700" height="335">

  Help is a very expressive way to have information about the tool{#caption-attachment-46680}

  <br />

* Finally, we can perpetuate stereotypes. Everybody knows that real developers only use the command line *DUH*. (Please don't listen to people, and use whatever you prefer / are most productive with)

By now, you should be convinced that a CLI can be a nice addition to your developer's toolbelt. So let's have a deeper look into how to create one using Kotlin, and picoCLI.

About picoCLI {#h2-1-about-picocli}
-----------------------------------

[picoCLI](https://picocli.info/) is one of the many options available to you to create CLIs on the JVM. Other possibilities include [Jakarta Commons CLI](https://commons.apache.org/proper/commons-cli/) or [Clikt](https://ajalt.github.io/clikt/) if you're using Kotlin.  

I personally am a big fan of picoCLI because of its amazingly extensive documentation and [how helpful the main author is online](https://twitter.com/jlengrand/status/1318314418825035782). The library has been downloaded over 2.5 million times the past year, and is used in very large projects such as Junit, Spring, or Apache hadoop. The complete list of available features is way too long to be listed here, but today we'll look into **help generation, color support, annotations and compilation to native images**. To do this, we will be creating a simple CLI to return information about Star Wars characters or planets.

### A sample project {#h3-2-a-sample-project}

The snippet and code used in this article are all directly taken from my swacli demo application. [You can find the code on GitHub](https://github.com/jlengrand/swacli).  

At its core, picoCli is nothing more than a Java library. To add it to your project the only thing you actually are required to do is add it to your Maven or Gradle dependencies. It is also recommended to add support for the annotation processor in your IDE. The annotation processor allows for compile time error checking, instead of runtime but it is also useful to generate GraalVM configuration files later on during native compilation.  

<img loading="lazy" decoding="async" aria-describedby="caption-attachment-46681" class="size-medium wp-image-46681" src="/images/posts/2021/09/building-command-line-interfaces-with-kotlin-using-picocli/image4-662x510.png" alt="A snippet of the kapt related part of my gradle file" width="662" height="510">

how the gradle configuration looks like in Gradle for a Kotlin project.{#caption-attachment-46681}

*Note: Even though I love the annotation processor, I tend to disable it because it has a tendency to slow down my IDE a lot.*

Running Hello World {#h2-3-running-hello-world}
-----------------------------------------------

As usual, the first thing we want to do with a new tool is to run Hello World.  

<img loading="lazy" decoding="async" aria-describedby="caption-attachment-46682" class="size-medium wp-image-46682" src="/images/posts/2021/09/building-command-line-interfaces-with-kotlin-using-picocli/image5-700x312.png" alt="Hello World using picoCLI" width="700" height="312">

Hello World using picoCLI{#caption-attachment-46682}

From this example, we discover a few things :

* *CommandLine* is the core Object of picoCLI. It will take care of exception handling, help, requests, and signal passing for you.
* picoCLI will wrap around any *Callable* or *Runnable*. This means that your core logic should be embedded in a Runnable or Callable.
* We can use the *@Command* annotation for picoCLI to generate things for us. In that case, we provide a name, version, and description to the tool. We also tell it to generate standard help for us. This allows us in a single line of code to do things like `sw --version` or `sw --help` . Here is the output when running the snippet of code above with the `--help` input parameter.

<img loading="lazy" decoding="async" aria-describedby="caption-attachment-46683" class="size-medium wp-image-46683" src="/images/posts/2021/09/building-command-line-interfaces-with-kotlin-using-picocli/image6-700x126.png" alt="Result of the help command of sw" width="700" height="126">

Result of the help command of sw{#caption-attachment-46683}
> "A user interface is like a joke, if you have to explain it it's not that great" - Martin LeBlanc.

*Tip :Make sure to describe all you can when defining your CLI, picoCLI will make sure to generate great help for you and make sure your users are not confused.*

Diving into Options and Arguments {#h2-4-diving-into-options-and-arguments}
---------------------------------------------------------------------------

Without being able to provide any inputs to it, a CLI usage would be very limited. Inputs are usually divided into two main types :

* **Parameters** : (or 'positional parameters') the values that typically come after the tool name. Examples are `cd ~` (used to move to your $HOME folder), or `cat README.md LICENSE.md` (used to read the files listed to standard output sequentially).
* **Options** : flags that usually have a name. They can stand by themselves or require a value behind them. For example `ls -a` (list files, including directories).
* Most commands will contain a mix of Options and Parameters, for example `ffmpeg -i input.mkv -c copy output.m4v`.  
  Because this will be the main interface between our users and the tool, we should take good care of choosing options and arguments that make sense.  
  In our Star Wars use case, we want users to be able to **search for planets OR characters** and **either insert some search item (ex : 'Darth') or nothing** and get a full list of characters back. Let's see how those options and parameters look like :

  <br />

<img loading="lazy" decoding="async" aria-describedby="caption-attachment-46684" class="size-medium wp-image-46684" src="/images/posts/2021/09/building-command-line-interfaces-with-kotlin-using-picocli/image7-700x349.png" alt="Using argument groups with pico CLI. Snippet of code" width="700" height="349">

  Our arguments and commands for to search for StarWars information{#caption-attachment-46684}

  <br />

* We define our search query using the Parameter annotation. Because there is only one parameter, we place it at index 0. And because we allow the search query to be empty, we define an [arity](https://en.wikipedia.org/wiki/Arity) of 0 to 1. Just like for databases, an arity defines the number of arguments the parameter can take (bounded by 0, 1 or n).
* We define two *options*, which define the search mode (planet, or character). We define short, and long names for them
* Because we want users to search for planets OR characters, we define those options as **exclusive** to each other. And to avoid confusing the user, we force him to pick one of them by setting the default option values to false.

Here is what happens when trying to run the code without specifying one option :  

<img loading="lazy" decoding="async" aria-describedby="caption-attachment-46685" class="size-medium wp-image-46685" src="/images/posts/2021/09/building-command-line-interfaces-with-kotlin-using-picocli/image8-700x197.png" alt="The error message when using the tool without argument" width="700" height="197">

Missing required argument when running the example. Good!{#caption-attachment-46685}
> Tip : Don't break conventions that have been there for decades. -i (input) , -o (output), -r (recursive) have typical meanings when using command lines. Make sure to apply those unwritten conventions

SubCommands for a better user experience {#h2-5-subcommands-for-a-better-user-experience}
-----------------------------------------------------------------------------------------

You may already have heard or seen subcommands in CLIs. Subcommands are basically using semantic words to achieve your actions. Typical examples are `kubectl get services`, or `gh repo clone jlengrand/swacli`. SubCommands are perfect in our case because they allow us to get rid of our clunky exclusive options. Let's see how to implement them :  

<img loading="lazy" decoding="async" aria-describedby="caption-attachment-46686" class="size-medium wp-image-46686" src="/images/posts/2021/09/building-command-line-interfaces-with-kotlin-using-picocli/image9-700x435.png" alt="Snippet of code using Subcommands" width="700" height="435">

Same example, using subcommands this time{#caption-attachment-46686}

I omitted all the non-crucial information here. What is important to note is that at their core, **subcommands are literally nothing else than commands themselves** . In this snippet, I created a *PlanetsCommand* as well as a *PeopleCommand* , and defined them as subcommands using the subcommand argument of the annotation. Note the use of *HelpCommand* , which is provided by picoCLI to allow for `sw help` usage.

Using this method, users can now search for `sw planets tatooine` or `sw people Luke`.

Colors and Emojis support for clear information display {#h2-6-colors-and-emojis-support-for-clear-information-display}
-----------------------------------------------------------------------------------------------------------------------

Nowadays, all terminals support color schemes as well as emojis (!!). We can leverage those to present information to our users in the clearest possible way and reduce cognitive load. picoCLI supports a custom markup notation that helps with color usage.  

Picturing a *Response* object that contains a number of results, together with data about a planet, here is a snippet that declares markup. picoCLI declares '@\|' and '\|@' , in between which specific colors and types can be defined such as 'bold,green'. The unicode characters that can be seen represent emojis. Much more can be done with colors in picoCLI, such as palettes. [You can read more about it here](http://https://picocli.info/#_ansi_colors_and_styles "You can read more about it here").  

<img loading="lazy" decoding="async" aria-describedby="caption-attachment-46687" class="size-medium wp-image-46687" src="/images/posts/2021/09/building-command-line-interfaces-with-kotlin-using-picocli/image10-700x250.png" alt="An example of formatted text using the picoCLI format" width="700" height="250">

An example of formatted text using the picoCLI format{#caption-attachment-46687}

Native compilation for a blazing fast experience {#h2-7-native-compilation-for-a-blazing-fast-experience}
---------------------------------------------------------------------------------------------------------

The last thing we can do to help our users enjoy our CLI is to make sure it's blazing fast. This is where native compilation and [GraalVM](https://www.graalvm.org/) come into play.

picoCLI supports GraalVM native compilation by default (unless you added some unsupported library). To generate a native executable, run the following command (with graalVM and its native-image extension enabled on your system).  

<img loading="lazy" decoding="async" aria-describedby="caption-attachment-46688" class="size-medium wp-image-46688" src="/images/posts/2021/09/building-command-line-interfaces-with-kotlin-using-picocli/image11-700x166.png" alt="Compiling a native image for our swacli application" width="700" height="166">

Compiling a native image for our swacli application{#caption-attachment-46688}

**On my machine, the execution time for `sw planets tatooine` goes from around 1.6 seconds to a whooping 0.03 seconds** . A must have, clearly (if you can offer the increased compilation time and the added complexity for multiplatforms usage)! [You can read more about GraalVM's native-compilation here](https://www.graalvm.org/reference-manual/native-image/).

With this article, we barely scratched the surface of what picoCLI can do. Hopefully though, I gave you some useful tips to create nicer user experiences and you now feel like trying it out yourself. Find a small utility that you and your team uses often, spend a couple minutes adding a CLI layer around it and see how you like it!

Looking forward to hearing it from you next time we meet!
