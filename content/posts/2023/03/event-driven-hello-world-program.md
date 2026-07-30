---
title: "Event Driven Hello World Program | Foojay.io Today"
slug: "event-driven-hello-world-program"
date: "2023-03-02T16:34:35+00:00"
lastmod: "2023-03-03T09:08:08+00:00"
description: "Let's use an event-driven Hello World (a paradigm where the flow is determined by events) to step through behaviour-driven development,"
authors:
  - "peter-lawrey"
image: "/images/posts/2023/03/event-driven-hello-world-program/Screen-Shot-2023-02-20-at-4.56.42-PM-1024x241-1.png"
categories:
  - "DevOps"
  - "Microservices"
tags:
related_posts:
  - "how-behaviour-driven-development-works-well-with-event-driven-architectures"
  - "the-evolution-of-apis-from-restful-to-event-driven"
  - "learn-how-to-develop-event-driven-architectures"
  - "event-driven-architecture-and-change-data-capture-made-easy"
enlighterjs: true
frozen: false
---

**Event-driven microservices** can be straightforward to describe before they are implemented, tested and maintained.

They are also highly responsive to new information in real time, with latencies in Java of below 10 microseconds 99.99% of the time depending on the functionality of the small independently deployable microservice.

In this introductory article, we use an example [event-driven](https://chronicle.software/how-to-develop-event-driven-architectures/ "event-driven")[Hello World](https://en.wikipedia.org/wiki/%22Hello,_World!%22_program " Hello World") program (a programming paradigm where the program flow is determined by events) to step through [behaviour-driven development](https://chronicle.software/how-bdd-works-well-with-eda/ "behaviour-driven development"), where we describe the behaviour the business needs first as test data, and writing a very simple [microservice](https://en.wikipedia.org/wiki/Microservices "microservice") which turns input events like this:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">say: Hello World</pre>

Into outputs like this, by adding an exclamation point:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">say: Hello World! # &lt;- adds an exclamation point</pre>

All the code for this example is [available on GitHub](https://github.com/OpenHFT/Chronicle-Queue-Demo/tree/main/hello-world).

When modelling Event-Driven systems, a useful pattern is to have event-driven core systems with gateways connecting to external systems that might not be event driven.

To keep a clear separation of concern, [business logic](https://en.wikipedia.org/wiki/Business_logic) such as making a decision based on market data, or processing an order, is placed in the event-driven microservices, as these are the easiest to test, with the **gateways** connecting to external clients and systems being as thin as possible so they are only concerned with acting as adapters and avoid containing significant business logic.

**Domain-Driven Design** is a focus of determining the requirements from domain experts. Their requirements are further divided into event-driven microservices.

Where the information is passed as a series of events between the micoservices.

The requirements for each internal microservices can be described in YAML, for Behaviour-Driven Development.

![](/images/posts/2023/03/event-driven-hello-world-program/Screen-Shot-2023-02-20-at-4.50.56-PM-1024x225.png)  
*Figure 1- Gateways connect internal services to external systems*

All examples are in the [Chronicle-Queue-Demo/hello-world module](https://github.com/OpenHFT/Chronicle-Queue-Demo/tree/main/hello-world).

### A Simple Event-Driven Contract {#h3-0-a-simple-event-driven-contract}

We model events as asynchronous method calls without arguments, or one-to-many arguments e.g.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public interface Says {
   void say(String words);
}</pre>

This is the simplest Hello World example to get started.

We can add to this interface other event types (methods) with multiple parameters. Parameters don't have to be just primitives, they can also be complex data structures such as Data Transfer Objects.

There is no assumption about how the events produced by the microservice will be processed. It might be record but otherwise ignored for now, processed immediately by a single microservice, or read by multiple downstream microservices some time later.

Thus, it doesn't return a value. Any results will be emitted as events from the respective event handlers. In programming, an event handler is a callback routine that can operate asynchronously.

### External Event Producers and Consumers {#h3-1-external-event-producers-and-consumers}

Often we need to integrate with the client's external systems.

As this is a simple "Hello World" example, let's imagine that instead of external systems connected via gateways we have a simple program that reads input from the console to provide upstream events and another simple program to write to the console, acting as a downstream gateway.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public class SaysInput {
    public static void input(Says says) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        for (String line; ((line = br.readLine()) != null); )
            says.say(line);
    }
}

public class SaysOutput implements Says {
    public void say(String words) {
        System.out.println(words);
    }
}</pre>

These can be integrated easily as the output of one is wired to the input of the other.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public class RecordInputToConsoleMain {
    public static void main(String[] args) throws IOException {
        // Writes text in each call to say(line) to the console
        final Says says = new SaysOutput();
        // Takes each line input and calls say(line) each time
        SaysInput.input(says);
    }
}</pre>

We can also record everything the producer performs to YAML to build tests later.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public class RecordInputAsYamlMain {
    public static void main(String[] args) throws IOException {
        // obtains a proxy that writes to the PrintStream the method calls and their  arguments
        final Says says = Wires.recordAsYaml(Says.class, System.out);
        // Takes each line input and calls say(theLine) each time
        SaysInput.input(says);
    }
}</pre>

Use the following to replay the output from a file.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public class ReplayOutputMain {
    public static void main(String[] args) throws IOException {
        // Reads the content of a Yaml file specified in args[0] and feeds it to SaysOutput.
        Wires.replay(args[0], new SaysOutput());
    }
}</pre>

### Unit Tests for the RecordAsYaml and Replay Methods {#h3-2-unit-tests-for-the-recordasyaml-and-replay-methods}

To test the functionality of recordAsYaml and replay methods in isolation and verify if they work as suggested above, the following unit tests were developed.

Having lots of text in unit tests is cumbersome, and in the next section you can see how this text can be taken from files.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public class WiresTest extends WireTestCommon {
    @Test
    public void recordAsYaml() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        Says says = Wires.recordAsYaml(Says.class, ps);
        says.say("One");
        says.say("Two");
        says.say("Three");

        assertEquals("" +
            "---\n" +
            "say: One\n" +
            "...\n" +
            "---\n" +
            "say: Two\n" +
            "...\n" +
            "---\n" +
            "say: Three\n" +
            "...\n",
            new String(baos.toByteArray(), StandardCharsets.ISO_8859_1));
    }

    @Test
    public void replay() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        Says says = Wires.recordAsYaml(Says.class, ps);
        says.say("zero");
        Wires.replay("=" +
            "---\n" +
            "say: One\n" +
            "...\n" +
            "---\n" +
            "say: Two\n" +
            "...\n" +
            "---\n" +
            "say: Three\n" +
            "...\n",says);  

        assertEquals("" +
            "---\n" +
            "say: zero\n" +
            "...\n" +
            "---\n" +
            "say: One\n" +
            "...\n" +
            "---\n" +
            "say: Two\n" +
            "...\n" +
            "---\n" +
            "say: Three\n" +
            "...\n", new String(baos.toByteArray(), StandardCharsets.ISO_8859_1));
    }

    interface Says {
        void say(String word);
    }
}
</pre>

By recording and replaying using YAML, our microservices are written, tested and debugged easily without any involvement of the messaging layer.

Let's add a microservice as a data processor as a class that can have one or more event types.

This microservice gets input events as text messages and adds an exclamation mark to them and relays them to the output gateway.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public class AddsExclamation implements Says {
    private final Says out;

    public AddsExclamation(Says out) {
        this.out = out;
    }

    public void say(String words) {
        this.out.say(words + "!");
    }
}</pre>

![](/images/posts/2023/03/event-driven-hello-world-program/Screen-Shot-2023-02-20-at-4.56.42-PM-1024x241.png)  
*Figure 2- A microservice that adds exclamation marks to input messages.*

### A Single-Threaded Event-Driven Process {#h3-3-a-single-threaded-event-driven-process}

We can combine these all stages in one process, one thread.

While this is unlikely to be useful in production, putting microservices into a single thread makes it easier to test and debug.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public class DirectWithExclamationMain {
    public static void main(String[] args) throws IOException {
        SaysInput.input(new AddsExclamation(new SaysOutput()));
    }
}</pre>

### Testing a Single Event-Driven Service {#h3-4-testing-a-single-event-driven-service}

Instead of embedding large amounts of text in a test, we can read resource files.

This makes them easier to read and maintain.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public class AddsExclamationTest {
    @Test
    public void say() throws IOException {
        YamlTester yt = YamlTester.runTest(AddsExclamation.class, "says");
        assertEquals(yt.expected(), yt.actual());
    }
}</pre>

![](/images/posts/2023/03/event-driven-hello-world-program/Screen-Shot-2023-02-20-at-4.58.02-PM-1024x363.png)

Let's update the input to see how easy it is to maintain this test. I will change the second input to Hello World and run the test again.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">src/test/resources/says/in.yaml
---
say: One
...
---
say: Hello World
...
---
say: Three
...</pre>

![](/images/posts/2023/03/event-driven-hello-world-program/Screen-Shot-2023-02-20-at-4.58.42-PM-1024x370.png)

Not only does the test fail, I can click on the differences to see clearly why.

![](/images/posts/2023/03/event-driven-hello-world-program/Screen-Shot-2023-02-20-at-4.59.05-PM.png)

At this point I can either fix the test, or I can accept the change by copying and pasting the actual result over the expected result in the out.yaml file.

In the next post, we will see how to implement more realistic example processing orders, and automate many microservice tests from the configuration.

This provides a basis for creating highly performant, deterministic, redundant microservices

### Conclusion {#h3-5-conclusion}

This article shows the outline of creating and testing a simple microservice, which provides the basis for microservices which are easy to deploy and maintain.
