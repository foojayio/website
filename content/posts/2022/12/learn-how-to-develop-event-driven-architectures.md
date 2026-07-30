---
title: "Learn How to Develop Event-Driven Architectures"
slug: "learn-how-to-develop-event-driven-architectures"
date: "2022-12-07T15:25:16+00:00"
lastmod: "2022-12-07T16:27:04+00:00"
description: "EDA is a design pattern in which decoupled components (often microservices) can asynchronously publish and subscribe to events."
canonical: "https://chronicle.software/how-to-develop-event-driven-architectures/"
authors:
  - "rob-austin"
image: "https://foojay.io/wp-content/uploads/2022/11/Screen-Shot-2022-12-02-at-11.17.40-AM.png"
categories:
  - "Java Core"
  - "Performance"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Previously, I wrote [an article on Open source Chronicle Wire](https://foojay.io/today/high-performance-java-serialisation/ "an article on Open source Chronicle Wire"), that discusses how we could serialise an application's state into different message formats.

Now in this article, I'm going to look at how we can use Open source [Chronicle Queue](https://chronicle.software/queue/ "Chronicle Queue ")and [Chronicle Wire](https://chronicle.software/wire/ "Chronicle Wire") to structure applications to use Event-Driven Architecture (EDA).

EDA is a design pattern in which decoupled components (often microservices) can asynchronously publish and subscribe to events.

At a high level, event-driven architectures are usually made up of application components connected via an async message system. The events flow as messages between the application components and these components act independently as they don't need to know about other components.

All a component needs to know is how to process incoming messages and how to send messages upon the completion of business logic. In other words, Event-Driven Architectures are basically fire and forget.

Below is an example of an EDA architecture:

![](/images/posts/2022/12/learn-how-to-develop-event-driven-architectures/Screen-Shot-2022-12-02-at-11.17.40-AM-1024x394.png)

Where the orange boxes are java processes, and the blue boxes are message queues.

To connect these components, a broker-based message system is often used. However, introducing a broker introduces a degree of additional latency. Instead, if performance is important to the system design, it is preferable to use a Point-to-Point message bus as this will offer better latency since it removes the additional network hops required by the broker.

Even better, it may be possible to remove the network latency entirely. With modern hardware, we can take advantage of large multi-core machines: applications are able to run multiple processes on a single server, with each component or micro-service bound to a CPU core by utilising thread affinity and CPU isolation. Additionally, we set up a failover server acting for redundancy. By centralising the application in this way, we eliminate the overall network overhead, yielding much better end-to-end application performance.

When It comes to choosing the message bus, if the components are written in Java then the open source library Chronicle Queue can be selected. This is especially beneficial if these components are on the same host, as Chronicle Queue is a point-to-point messaging layer, which works by writing your events to shared off-heap memory.

Chronicle Queue is able to offer messaging performance characteristics that are close to reading and writing from RAM. There is also an extension to Chronicle Queue that allows it to send replicated messages over the network, which is required to support HA/DR and server failover.

Even though I love Java, it is good to know that Chronicle Queue also supports other languages such as Python and C++.

Now, let's take a step back and demonstrate how we can start building up an event-driven solution using Chronicle Wire and Chronicle Queue by looking at a very simple example which demonstrates how we can construct a Java method call.

The basic premise here is that information is exchanged (in the form of a method parameter) but, because the EDA design sends asynchronous \[fire and forget\] events, we must use a void method. For example void print(String message).

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">package net.openhft.chronicle.wire.examples;

public class WireExamples3 {

   interface Printer {
       void print(String message);
   }

   public static void main(String[] args) {
       final Printer consolePrinter = System.out::println;
       consolePrinter.print("hello world");
   }
}</pre>

When run, this code will print:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="dracula">hello world</pre>

In summary, the class above is calling the standard console implementation of the Printer interface. So, in this simple example, we could say that the Java method call is our transport.

Now, what if we were to change this and make Chronicle Wire our transport?

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">package net.openhft.chronicle.wire.examples;

import net.openhft.chronicle.wire.JSONWire;
import net.openhft.chronicle.wire.Wire;

public class WireExamples4 {

   interface Printer {
       void print(String message);
   }

   public static void main(String[] args) {
       Wire wire = new JSONWire();
       final Printer printer = wire.methodWriter(Printer.class);
       printer.print("hello world");

       wire.methodReader((Printer) System.out::println).readOne();
   }
}</pre>

As expected this code prints exactly the same text:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="dracula">hello world</pre>

It takes the method call print("hello world") and serialises it to [JSON](https://en.wikipedia.org/wiki/JSON "JSON"), using the Chronicle Wire methodWriter. This JSON is then subsequently read by the methodReader, then the print method is called. We can inspect the payload of chronicle-wire, if we add the following statement:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="dracula">System.out.println(wire.bytes());</pre>

By changing our code to this:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">package net.openhft.chronicle.wire.examples;

import net.openhft.chronicle.wire.JSONWire;
import net.openhft.chronicle.wire.Wire;

public class WireExamples4 {

   interface Printer {
       void print(String message);
   }

   public static void main(String[] args) {
       Wire wire = new JSONWire();
       final Printer printer = wire.methodWriter(Printer.class);
       printer.print("hello world");
       System.out.println(wire.bytes());
       wire.methodReader((Printer) System.out::println).readOne();
   }
}</pre>

It outputs:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">"print":"hello world"

...

hello world</pre>

Where "print":"hello world" is the serialised form of this method call, it is this information that will be transmitted on our message bus.

So let's introduce a message bus, and make a remote procedure call (RPC). In other words, make a method call from one Java thread or process to another

One way we could do this is to use Chronicle Queue, which is built upon the foundation of Chronicle Wire. Chronicle Queue uses Chronicle Wire as its serializer, but instead of writing the serialised event (in this case "print":"hello world") to JSON and storing it in Java memory, it instead writes this event to a memory-mapped file, which can be shared between Java threads and processes.

Another feature I like is that Chronicle Queue can support multiple processes writing and reading from the same queue and it stores its data in a compact binary format.

You can see the example below looks very similar to the previous example, but each section of code can be run in its own process.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">// first java process

package net.openhft.chronicle.queue.example;

import net.openhft.chronicle.queue.ChronicleQueue;

public class QueueExamples1 {

    public static void main(String[] args) {

        ChronicleQueue queue = ChronicleQueue.single("./myQueueDir");
        Printer printer = queue.methodWriter(Printer.class);
        printer.print("hello world");
    }

    // this interface has to be deployed to both java processes
    interface Printer {
        void print(String message);
    }

}

// second java process

package net.openhft.chronicle.queue.example;

import net.openhft.chronicle.bytes.MethodReader;
import net.openhft.chronicle.queue.ChronicleQueue;

public class QueueExamples2 {

    public static void main(String[] args) {

        ChronicleQueue queue = ChronicleQueue.single("./myQueueDir");
        final MethodReader methodReader = queue.createTailer().methodReader((Printer) System.out::println);

        for (; ; ) {
            final boolean successIfMessageRead = methodReader.readOne();
            Thread.yield();
        }

    }

    // this interface has to be deployed to both java processes
    interface Printer {
        void print(String message);
    }

}</pre>

The only shared configuration between the two processes is the chronicle queue directory "./myQueueDir" and the Printer interface.

Several other libraries are using Chronicle Queue internally such as [Chronicle Services](https://chronicle.software/services/ "Chronicle Services") or [Apache Cassandra](https://cassandra.apache.org/_/index.html "Apache Cassandra").

### Summary {#h3-0-summary}

There is more to an Event-Driven Architecture than setting up a few method calls between a few Java processes. Other important considerations are:

* Service configuration with the messaging layer, perhaps via a configuration file.
* Configurable redundancy.
* Designating which server is running on which core.
* Choose your deployment fabric, either as a series of interoperable micro services or as a monolithic application.
* Being able to regain your component state by replaying in the same events, in the same order, is ideal for back testing and bug fixing.
