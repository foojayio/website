---
title: "Billions of Messages Per Minute Over TCP/IP"
slug: "billions-of-messages-tcp-ip"
date: "2023-07-05T21:05:08+00:00"
lastmod: "2023-07-05T21:05:09+00:00"
description: "Chronicle Wire has had new features added to permit communication with other components across a TCP/IP network."
authors:
  - "george-ball"
image: "https://foojay.io/wp-content/uploads/2020/04/Favicon-3-2.png"
categories:
  - "Java Core"
  - "JavaFX"
  - "Tutorials"
  - "Use Cases"
tags:
related_posts:
enlighterjs: true
frozen: false
---

### Key Takeaways {#h3-0-key-takeaways}

* Data consistency is critical when communicating between software components on different machines to ensure information remains in tact.
* Low-latency data exchange requires a different approach than common formats.
* Chronicle's open-source Wire library provides a highly efficient means of marshalling and unmarshalling data for transmission to and from Chronicle Queue.
* Recent additions to the library extend its use with TCP/IP communications channels, offering extremely high throughput.
* Using Wire across TCP/IP opens up the possibility of Cloud Native deployment of Chronicle-based applications.

### Introduction {#h3-1-introduction}

One of the most important issues when building distributed applications is that of data representation. We must ensure that data sent by a component to a "remote" component (i.e. one that is part of a different process) is received correctly, with the same values. This may seem straightforward but remember that the communicating components may have been written in completely different languages.

Things are complicated further when we consider that different hardware/system architectures are likely to have different ways of representing the "same" values. Simply copying bytes from one component to another is not enough. Even in Java, where we may consider ourselves "protected" from this kind of situation, there is no requirement that two different JVM implementations or different versions from the same vendor use the same internal representation for objects.

The most common solution to this problem is to define a "canonical" representation of data that is understood between processes -- even between programming languages -- and have data translated into this format before sending and then back into the receiver's own format once received. Several such "wire formats" exist, ranging from text-based standards such as YAML, JSON or XML, to binary options such as [Protobuf](https://www.infoq.com/protocolbuffers/ "Protobuf") that incorporate metadata or are completely raw.

At [Chronicle Software](https://chronicle.software/?utm_source=foojay&amp;utm_medium=article&amp;utm_campaign=george-tcp "Chronicle Software") we have developed a number of libraries to support the building of applications that are optimised for low latency messaging, primarily in the financial services industry. We provide bespoke solution development and consultancy to clients, the majority of whom are in the financial area, from all over the world.

One of these libraries, [Chronicle Wire](https://chronicle.software/wire/?utm_source=foojay&amp;utm_medium=article&amp;utm_campaign=george-tcp "Chronicle Wire"), provides high-performance transformations of state in Java objects between their internal JVM representation and a format that allows that state to be persisted or communicated to another Java process.

Chronicle Wire grew from the [Chronicle Queue](https://chronicle.software/queue/?utm_source=foojay&amp;utm_medium=article&amp;utm_campaign=george-tcp "Chronicle Queue") project, which offers single digit microsecond latencies for messaging between JVMs on the same machine, or stable latencies of tens of microseconds between machines, as throughput scales to millions of messages per second. Wire now forms a key part of most of the software components developed by Chronicle, with uses from serialisation and deserialisation of object state for communication between components to an efficient model for managing the configuration of these components.

As software architectures increasingly follow a distributed, event-based approach, we are looking to expand the space in which Chronicle Wire can be used, to support TCP/IP interconnections between components. This article provides a basic overview of the features that will be available and some simple examples of how they can be used.

We are already seeing some startling performance figures for this basic approach -- in a benchmark described in Peter Lawrey's article[Java is Very Fast, If You Don't Create Many Objects](https://chronicle.software/java-is-very-fast/?utm_source=foojay&amp;utm_medium=article&amp;utm_campaign=george-tcp " Java is Very Fast, If You Don’t Create Many Objects"), for example, which is built upon loopback TCP/IP networking on a single machine, we were able to pass over 4 billion events per minute.

We benchmarked this against similar technologies used for data exchanges, specifically Jackson and BSON. In a test processing 100 byte messages, the 99.99 percentile processing per-message processing time was about 10.5 microseconds with Chronicle Wire compares to 1400 microseconds with Jaskcon/BSON. This is a significant difference.

Here we present an introduction to the key concepts used to realise this. We are, however, designing these features to be flexible as well as performant, and future articles will show some more advanced use cases.

### About Chronicle Wire {#h3-2-about-chronicle-wire}

Chronicle Wire exists as a layer between an application and a byte stream, acting as a source or sink of data. Wire will serialise (marshal) the state of a Java object and store the resulting format to the byte stream, or it will read a sequence of bytes from the byte stream and deserialise (unmarshal) these into a Java object based only on information carried in the message.

Let's look at a simple example. We will simulate the persisting of a Java object by serialising its state to the Wire, and reading it back into a separate object. We'll use a class called Person.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public class Person extends SelfDescribingMarshallable {
   private String name;
   @NanoTime
   private long timestampNS;
   @Base85
   private long userName;
   …
}</pre>

The full code for the class can be found in the [Chronicle Wire Github repo.](https://github.com/OpenHFT/Chronicle-Wire/blob/ea/src/test/java/net/openhft/chronicle/wire/examples/Person.java "Chronicle Wire Github repo.")

The parent type `SelfDescribingMarshallable` contains the necessary functionality to interact with Wire -- it's loosely equivalent to the `java.io.Serializable` tagging interface used with Java serialisation, although it is much more powerful and does not contain security flaws. As the name suggests, a `SelfDescribingMarshallable` object requires no additional facilities to support marshalling and unmarshalling -- such as a schema for XML, or code generator for Protobuf or SBE. Additionally, the interface provides implementations of "core" Java data object methods `equals()`, `hashcode()` and `toString()`.

The annotation `@NanoTime` is used by Chronicle Wire to encode the property value most efficiently as a timestamp, and `@Base85` is used to encode short strings in a space-efficient way. Both annotations also provide conversions form their compact internal representations to friendly String representations for their respective values.

Let's set up an instance of Chronicle Wire that will marshall and unmarshall to/from YAML, using an area of memory allocated on the Java heap.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Wire yWire = Wire.newYamlWireOnHeap();</pre>

To create and initialise an instance of the Person class we would write:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Person p1 = new Person()
       .name("George Ball")
       .timestampNS(CLOCK.currentTimeNanos())
       .userName(Base85.INSTANCE.parse("georgeb"));
System.out.println("p1: " + p1);</pre>

We use overloaded methods and a flow style, rather than `get...()` and `set...()` methods, for accessing and mutating properties. Output from the code shows the initialised state of the `Person` object, demonstrating the `toString()` method from the `SelfDescribingMarshallable` parent type:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">p1: !Person {
  name: George Ball,
  timestampNS: 2022-11-11T10:11:26.1922124,
  userName: georgeb
}</pre>

Now we serialise the object to the Wire. As the Wire has been created to use text/YAML, its contents can easily be displayed:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Wire yWire = Wire.newYamlWireOnHeap();
p1.writeMarshallable(yWire);
System.out.println(yWire);</pre>

We can see the properties serialised appropriately:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">name: George Ball
timestampNS: 2022-11-11T10:11:54.7071341
userName: georgeb</pre>

We can now create an empty instance of the Person class, populate it by reading back from the Wire, and print it out:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Person p2 = new Person();
p2.readMarshallable(yWire);
System.out.println("p2: " + p2);
The output shows that the new object has the correct state:

p2: !Person {
  name: George Ball,
  timestampNS: 2022-11-11T10:13:29.388,
  userName: georgeb
}</pre>

The code that demonstrates this can be found in the [Chronicle Wire Github repo](https://github.com/OpenHFT/Chronicle-Wire/blob/ea/src/test/java/net/openhft/chronicle/wire/examples/Person.java "Chronicle Wire Github repo").

### Method Writers and Readers {#h3-3-method-writers-and-readers}

Normally we would imagine objects that are serialised and deserialised using Wire to hold data of some kind, relevant to our application. When using Chronicle Queue as a message transport, these objects would form the payload of messages, and we call them Data Transfer Objects (DTOs).

However it is possible to look at this functionality from a different perspective. The serialised form of the `Person` object contained the properties of the object in YAML form:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">name: George Ball
timestampNS: 2022-11-11T10:11:54.7071341
userName: georgeb</pre>

If we generalise this further, we have a means of encoding and sending, using Wire, a request to invoke a method with a supplied argument. Due to the unidirectional nature of our message transport, these methods have to be void, i.e. they cannot return a value. To illustrate this, consider an Interface that contains definitions of operations to be performed on `Person` objects. The implementation(s) of the method(s) is not provided at this time:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public interface PersonOps {
   void addPerson(Person p);
}</pre>

Only one method is specified here, for simplicity. It's intended to take a single argument which is of type `Person`, and add it to some collection. Based on the previous example, we can expect an instance of this type to be encoded to a Wire as

<pre class="EnlighterJSRAW" data-enlighter-language="generic">addPerson: {
  name: George Ball,
  timestampNS: 2022-11-11T10:11:54.7071341,
  userName: georgeb
}</pre>

and decoded to a form that can be considered a method invocation:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">personOps.addPerson(
       Marshallable.fromString(Person.class, "" +
               "name: Alice Smithl\n" +
               "timestampNS: 2022-11-11T10:11:54.7071341\n" +
               "userName: alices\n"));</pre>

Chronicle Wire offers the capability to encode and decode method invocations just like this. The sender uses a type called `MethodWriter`, and the receiver uses a type called `MethodReader`.

As an example, for the `PersonOps` type shown above, we can create a method writer:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">final PersonOps personOps = yWire.methodWriter(PersonOps.class);</pre>

The result of this method call is an instance of the interface type that has a stub implementation of the method `addPerson()`, which encodes the request to the Wire. We can invoke this method as

<pre class="EnlighterJSRAW" data-enlighter-language="generic">personOps.addPerson(p1);

personOps.addPerson(new Person()
       .name("Bob Singh")
       .timestampNS(CLOCK.currentTimeNanos())
       .userName(Base85.INSTANCE.parse("bobs")));</pre>

and if we look at the Wire, we will see the invocation request encoded as a message:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">addPerson: {
  name: Alice Smith,
  timestampNS: 2022-11-11T10:11:54.7071341,
  userName: alices
}
...
addPerson: {
  name: George Ball,
  timestampNS: 2022-11-11T10:28:47.466,
  userName: georgeb
}
...
addPerson: {
  name: Bob Singh,
  timestampNS: 2022-11-11T10:28:48.3001121,
  userName: bobs
}
...</pre>

At the receiving side, we can create a `MethodReader` object, providing an implementation of the method that is to be invoked upon decoding:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">MethodReader reader = yWire.methodReader(
       (PersonOps) p -&gt; System.out.println("added " + p));</pre>

When the message is read and decoded, the method will be called:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">for (int i = 0; i &lt; 3; i++)
   reader.readOne();</pre>

As the method is invoked, we will see the output from the call to `System.out.println()`:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">added !Person {
  name: Alice Smith,
  timestampNS: 2022-11-11T10:11:54.7071341,
  userName: alices
}

added !Person {
  name: George Ball,
  timestampNS: 2022-11-11T10:28:47.466,
  userName: georgeb
}

added !Person {
  name: Bob Jones,
  timestampNS: 2022-11-11T10:28:48.3001121,
  userName: bobj
}</pre>

This is potentially very powerful, as it gives us a highly flexible and efficient means of encoding events or messages, and associating them with handlers. All of the flexibility of Wire encoding is available -- text formats, or highly efficient binary formats -- as are the many different types of underlying transports with which Wire operates.

We'll now look at how the addition of support for TCP/IP based networking communication as a Wire transport can extend the possibilities even further.

### Concepts {#h3-4-concepts}

The new capabilities are based on three abstractions:

#### Channel

A Chronicle Channel is an abstraction over a bidirectional, point-to-point connection between two components. A Channel's type, specified when the Channel is created, defines the underlying transport that is to be used. The initial implementation supports TCP/IP using asynchronous sockets, or internal Channels that connect two endpoints within the same process. It is intended to support additional, higher level transports such as GRPC, REST or Websockets.

A Channel carries Events, packaged as Chronicle Wire messages, between these two components. Channel types may be defined for different transports, although the initial implementation supports TCP/IP or "local" (intra-process) channels.

#### Context

A Context is a management container for Channels, taking care of their configuration and lifecycle.

#### Handler

A handler is a component that is bound to a Channel and defines how incoming events are processed, and outgoing (result) events are transmitted. This allows various forms of session management to be implemented. Numerous pre-defined handlers are available, and additional handlers can also be defined.

A handler is associated with a channel during the establishment of a connection, normally by the "initiator" of the connection (ie. the client).

### Working with Channels {#h3-5-working-with-channels}

Let's look at some examples of these features in action.

#### Example 1: Hello, World

Following standard practice, the first example is one that simply echoes a "Hello" message. The numbered comments indicate points of interest in the code and correspond to the list below:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public class Channel1ReadWrite {

   private static final String URL = System.getProperty("url", "tcp://:3334");     // ===&gt; (1)

   public static void main(String[] args) {

       try (ChronicleContext context = ChronicleContext.newContext(URL).name("Channel1");     // ===&gt; (2)
           ChronicleChannel channel = context.newChannelSupplier(new EchoHandler()).get()) {

           Jvm.startup().on(Channel1.class, "Channel set up on port: " + channel.channelCfg().port());

           Says says = channel.methodWriter(Says.class);                       // ===&gt; (3)
           says.say("Well hello there");

           StringBuilder eventType = new StringBuilder();                      // ===&gt; (4)
           String text = channel.readOne(eventType, String.class);
           Jvm.startup().on(Channel1.class, "&gt;&gt;&gt;&gt; " + eventType + ": " + text);

       }
   }
}</pre>

1. Critical to the setup of the channel is a URL string. Currently  
   only TCP/IP is available as a transport but more can and will be supported in due course. The semantics of this string as understood by Chronicle Channel setup is summarised in the following table:

![](/images/posts/2023/07/billions-of-messages-tcp-ip/Screenshot-2023-06-27-at-10.11.53-AM-1024x339.png)

2. We use `try-with-resources` to ensure that all necessary components we create are closed appropriately when we are done. First, we create the Context, which will manage the lifecycle and configuration of the channels.

The context provides a factory from which new channels can be created. When requesting a new channel, we specify which handler is to be used to process incoming events. In this example we use `EchoHandler`, which as the name implies simply turns the event around and sends it back to the sender.

All of the necessary work to set up a server-side socket for this connection is done by the factory. The returned channel is available to be used.

3. Remember TCP/IP is a full duplex protocol, so the channel we have is bi-directional. So we can send an event through the channel, using a `MethodWriter` generated from the following type:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public interface Says extends Syncable {
   void say(String say);
}

…

Says says = channel.methodWriter(Says.class);
says.say("Well hello there");
…</pre>

4. We can then use Chronicle Wire to read the echoed event back from the channel and display its details.

When this simple example is run, we can see the output:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">[main] INFO run.chronicle.wire.channel.demo1.Channel1 - Channel set up on port: 3334
[main] INFO run.chronicle.wire.channel.demo1.Channel1 - &gt;&gt;&gt;&gt; say: Well hello there</pre>

#### Example 2: Separate Client and Server

The first example is a little artificial since it combines the client side and server side functionality into a single process. While this may be ideal for testing or debugging purposes, in reality, we want to separate both sides into their own process. Let's have a look at the server following this division:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public class ChannelService {
   static final int PORT = Integer.getInteger("port", 4441);

   public static void main(String[] args) throws IOException {
       System.setProperty("port", "" + PORT); // set if not set.
       ChronicleGatewayMain.main(args);
   }
}</pre>

Notice that this is now very short, thanks to our having used the utility class `ChronicleGatewayMain`, which encapsulates the functionality of setting up the server-side (a channel acceptor), removing boilerplate code and using default settings as much as possible.

Code for the client side is shown below, and the numbered comments again illustrate points of interest:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public class ChannelClient {

   private static final String URL = System.getProperty("url", "tcp://localhost:" + ChannelService.PORT);  // ===&gt; (1)

   public static void main(String[] args) {

       try (ChronicleContext context = ChronicleContext.newContext(URL).name("ChannelClient");    // ===&gt; (2)
            ChronicleChannel channel = context.newChannelSupplier(new EchoHandler()).get()) {

           Jvm.startup().on(ChannelClient.class, "Channel set up on port: " + channel.channelCfg().port());
           Says says = channel.methodWriter(Says.class);                            // ===&gt; (3)
           says.say("Well hello there");

           StringBuilder eventType = new StringBuilder();
           String text = channel.readOne(eventType, String.class);

           Jvm.startup().on(ChannelClient.class, "&gt;&gt;&gt;&gt; " + eventType + ": " + text);
       }
   }
}</pre>

1. The URL string contains a hostname and port number, which informs the channel creation logic that we are initiating the setup of the channel from the client side, providing the full address of the acceptor for the service.
2. The Context is set up as an initiator/client, because of the URL string format. When creating a channel from an initiator/client context, we specify which handler to be used at the receiving end. This forms part of the requested channel specification, which is sent to the service during the setup of the channel.

It is necessary for the service to have the necessary code for the handler -- for security reasons no code is sent across the network at any stage -- otherwise channel setup will fail.

3. Once the channel is established, the code is the same as in the first example.

When both client and server applications are run the output is the same as above:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">[main] INFO run.chronicle.wire.channel.demo2.ChannelClient - Channel set up on port: 4441
[main] INFO run.chronicle.wire.channel.demo2.ChannelClient - &gt;&gt;&gt;&gt; say: Well hello there</pre>

#### Example 3: Simple Request/Response Interaction

Earlier we saw how to use Wire's MethodReader and MethodWriter to implement a way of passing requests to request the invocation of methods outside of the current process. Now we can extend this example to demonstrate the ability, using Wire over a TCP/IP Channel, to implement basic Request/Response communication with a service, in a manner that is similar to Remote Procedure Call.

The service itself is simple, providing just a single method -- the intention here is to demonstrate the steps needed to construct the service and access it.

There are four parts to this example:

1. The Service, which implements the business logic in terms of message types for input and for output.
2. The Channel Handler, which connects the Service to the underlying Channel infrastructure.
3. The Service Driver, which acts as an entrypoint to the server side, creating and configuring both service and Channel handler.
4. The Client, a separate application, which creates and sends a request, and receives the response.

**The Service**   

The service is defined using an interface that contains method signatures representing the supported requests. We define the service interface as

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public interface PersonOps {
   void addPerson ( Person p );
}</pre>

The `Person` type is as defined earlier.

Messaging in Chronicle is unidirectional, so service API methods are void. We therefore need to define a second interface that defines the message for used for the response:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public interface ResponseSender {
   void respond(ReqStatus status);
}</pre>

The `ReqStatus` type indicates the success or otherwise of the method, and is defined as:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public enum ReqStatus {
   OK,
   ERROR
}</pre>

The two interfaces are wired together to form a "handler" for incoming requests:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public class PersonOpsProcessor implements PersonOpsHandler {

   private transient ResponseSender responder;                     // ===&gt; (1)

   public PersonOpsProcessor responder(ResponseSender responseSender) {     // ===&gt; (2)
       this.responder = responseSender;
       return this;
   }

   @Override
   public void addPerson(Person p) {                                   // ===&gt; (3)
       responder.respond(ReqStatus.OK);
   }
}
</pre>

1. This field will hold a reference to the output for this service to which response messages are posted.
2. In this example, the ResponseSender is injected using a setter method, this could also be done through a constructor.
3. This is the implementation of the method in the PersonOps interface, which for simplicity sends a successful status response.

**The Channel Handler**   

Recall from the discussion of concepts that the Channel Handler is responsible for processing messages/events that are passed on its associated Channel.

For this example we need to define a class that will dispatch incoming messages on the Channel to the appropriate handler method in the service, and connect the service output to the Channel:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public class PersonSvcHandler extends AbstractHandler&lt;PersonSvcHandler&gt; {   // ===&gt; (1)

   private final PersonOpsHandler personOpsHandler;                         // ===&gt; (2)

   public PersonSvcHandler(PersonOpsHandler personOpsHandler) {            // ===&gt; (3)
       this.personOpsHandler = personOpsHandler;
   }

   public void run(ChronicleContext context, ChronicleChannel channel) {   // ===&gt; (4)
       channel.eventHandlerAsRunnable(
           personOpsHandler.responder(channel.methodWriter(ResponseSender.class))
       ).run();
   }

   @Override
   public ChronicleChannel asInternalChannel(ChronicleContext context,     // ===&gt; (5)
                                                                          ChronicleChannelCfg channelCfg) {
       throw new UnsupportedOperationException("Internal Channel not supported");
   }

}
</pre>

1. The base class is where generic platform functionality is implemented. Our class will supply the necessary specifics for our service.
2. A reference to the implementation of the handler methods.
3. The `PersonOpsHandler` implementation is injected into the handler through the constructor.
4. When a new channel connection is initiated, an instance of our handler is started, with the necessary `MethodReader` and `MethodWriter` objects initialised correctly. This is encapsulated in the `run()` method and happens for every channel connection that is made.
5. In this example class we have explicitly disallowed the creation of an instance of this handler to run with an Internal channel.

**The Service Driver Class**   

Having completed these steps, the driver class for the service is straightforward, and is more or less identical to the previous example, using the utility class `ChronicleGatewayMain` to create the configure the Channel:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public class PersonSvcMain {

   static final int PORT = Integer.getInteger("port", 7771);

   public static void main(String... args) throws IOException {
       System.setProperty("port", "" + PORT);
       ChronicleGatewayMain.main(args);
   }
}</pre>

**The Client**   

We can implement a simple client for our Person service by setting up a Channel and then issuing requests to our service.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public class PersonClient {

   private static final String URL = System.getProperty("url", "tcp://localhost:" + PersonSvcMain.PORT);      // ===&gt; (1)

   public static void main(String[] args) {

       try (ChronicleContext context = ChronicleContext.newContext(URL)) {

           ChronicleChannel channel = context.newChannelSupplier(new PersonSvcHandler(new PersonOpsProcessor()))    // ===&gt; (2)
                                                               .get();

           final PersonOps personOps = channel.methodWriter(PersonOps.class);           // ===&gt; (3)

           Person thePerson = new Person()
                                                   .name("George")
                                                   .timestampNS(SystemTimeProvider.CLOCK.currentTimeNanos())
                                                   .userName(Base85.INSTANCE.parse("georgeb")));

;
           personOps.addPerson(thePerson);

           StringBuilder evtType = new StringBuilder();
           ReqStatus response = channel.readOne(evtType, ReqStatus.class);

           Jvm.startup().on(PersonClient.class, " &gt;&gt;&gt; " + evtType + ": " + response);
       }
   }
}</pre>

1. The URL is by default configured with the port number that was configured in the server.
2. The channel is created and an instance of our custom handler injected.
3. Once created, we can use the channel's MethodWriter method to generate the stub methods that will send the appropriately serialised events to the service.

### Summary {#h3-6-summary}

Chronicle Wire has had new features added to permit communication with other components across a TCP/IP network. This document has described the basic ideas of how this will work within Wire and described some simple examples.

There are many more use cases for this fast and efficient communication within distributed services. Additional examples are available within the [Chronicle Wire GitHub project](https://github.com/OpenHFT/Chronicle-Wire "Chronicle Wire GitHub project")., alongside the [examples](https://github.com/OpenHFT/Chronicle-Wire/tree/ea/src/test/java/net/openhft/chronicle/wire/examples "examples") from this article.
