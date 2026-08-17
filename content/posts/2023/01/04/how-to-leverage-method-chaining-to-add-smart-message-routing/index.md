---
title: "How to Leverage Method Chaining to Add Smart Message Routing"
slug: "how-to-leverage-method-chaining-to-add-smart-message-routing"
date: "2023-01-04T08:33:43+00:00"
lastmod: "2023-01-04T08:36:30+00:00"
description: "Learn how to use method chaining to add routing information to serialised data structures in a lightweight fashion!"
authors:
  - "rob-austin"
image: "method-chaining-chronicle.png"
categories:
  - "Java Core"
  - "Performance"
tags:
related_posts:
  - "challenges-when-developing-a-gui-for-fix"
  - "chronicle-queue-storing-1tb-in-virtual-memory-on-a-128gb-machine"
  - "creating-terabyte-sized-queues-with-low-latency"
enlighterjs: true
frozen: false
---

This article explores how to use method chaining to add routing information to serialised data structures in a lightweight fashion, where the routing information can be read separately by an intelligent message router.

We are going to use the open-source serialisation library [Chronicle Wire](https://chronicle.software/wire/ "Chronicle Wire"). Let's assume we have some data that we want to send to a particular destination; we don't want to pollute our business data structures with the routing information. In the future, the routing information could be removed or changed based on external factors such as system failover or horizontal scaling.

Having a separation of routing and business messages is nothing new; after all, JMS has been doing it for years with their createObjectMessage (below):

```java
MessageProducer producer = session.createProducer( destination );
ObjectMessage message = session.createObjectMessage( obj );
producer.send( message );
```


However, what is different here, is that the routing information is serialised alongside but separate to the business message and away from the messaging layer, where this data structure can later be read and dispatched.

In this example, we will route a product to a country:

```java
final Routing routing = wire.methodWriter(Routing.class);
routing.to("Italy").product(new Product("Coffee"));
routing.to("France").product(new Product("Cheese"));
routing.to("America").product(new Product("Popcorn"));
```


You can see the full code example here: [net.openhft.chronicle.wire.examples.MessageRoutingExample](https://github.com/OpenHFT/Chronicle-Wire/blob/develop/src/test/java/net/openhft/chronicle/wire/examples/MessageRoutingExample.java "net.openhft.chronicle.wire.examples.MessageRoutingExample")

In the rest of this article, we'll go into this code in a bit more detail; We will start by defining our business message. This business message is a Data-Transfer-Object (DTO); in an Event-Driven-Architecture (EDA), this DTO would be our event; for our simple example, we are going to create an event called Product.

```
class Product extends SelfDescribingMarshallable {
        String name;
        public Product(String name) {
            this.name = name;
        }
 }
```


The Product class extends *net.openhft.chronicle.wire.SelfDescribingMarshallable;* this is the Chronicle-Wires standard base class used for all Data-Transfer-Objects. However, if you don't wish to extend SelfDescribingMarshallable, you could instead implement the [marker interface](https://en.wikipedia.org/wiki/Marker_interface_pattern "marker interface ") *net.openhft.chronicle.wire.Marshallable* , which almost does the same thing as *SelfDescribingMarshallable* apart from providing default *toString() equals()* and *hashCode()* implementations.

Now, let's define the *Routing* interface; this gives us the ability to add destination information; depending on the Product's destination, it will be acted upon by the appropriate handler. You could think of these as regional handlers, each dispatching messages to its target destination.

For example, if the destination required the message to get routed over the network, the *ProductHandler* could use TCP/IP to a target host.

```
@FunctionalInterface
interface Routing {
      ProductHandler to(String destination);
}

@FunctionalInterface
interface ProductHandler {
      void product(Product product);
}
```


To keep our example simple, we will build a *HashMap* , keyed on the destination, and we will then use a lambda expression that outputs a String for the *ProductHandler*.

```java
final Map<String, ProductHandler> destinationMap = new HashMap<>();

// add ProductHandler to handle messages routed to each destination
destinationMap.put("Italy", product -> System.out.println("Sends the product to Italy, product=" + product));  

destinationMap.put("France", product -> System.out.println("Sends the product to France, product=" + product));

destinationMap.put("America", product -> System.out.println("Sends the product to America, product=" + product));
```


Below, we use Chronicle Wire for our serialisation.

```java
private final Wire wire = new TextWire(Bytes.allocateElasticOnHeap());
```


The code above will allow us to serialise our data structure into a human-readable text format. If we were to use Java to write the following:

```java
final Routing routing = wire.methodWriter(Routing.class);
routing.to("Italy").product(new Product("Coffee"));
routing.to("France").product(new Product("Cheese"));
routing.to("America").product(new Product("Popcorn"));
```


and then dump the output of the wire, using:

```
System.out.println(wire);
```


The following will get logged to the console:

```java
to: Italy
product: {
  name: Coffee
}
...
to: France
product: {
  name: Cheese
}
...
to: America
product: {
  name: Popcorn
}
...
```


Above, you can see how easily the data structure can be read and debugged.

```
CLASS_ALIASES.addAlias(Product.class, ProductHandler.class);
```


Using aliases (above) allows Chronicle-Wire serialisation to use the short name of the class rather than the entire pathname. Taking this approach helps us reduce the number of bytes used in encoding these classes; it also has the advantage of producing a more concise, less verbose output when viewed as text.

However, if performance was a concern, we can use a more efficient compact encoding such as:

```
private final Wire wire = new BinaryWire(new HexDumpBytes());
```


Below is a hex-dump of exactly the same data encoded using binary wire; it's more compact but nowhere near as easy to read. This can be done using:

```
System.out.println(wire.bytes().toHexString())
```


Chronicle Wire will automatically add in the #comment on the right-hand side.

```
21 00 00 00                                     # msg-length
b9 02 74 6f                                     # to: (event)
e5 49 74 61 6c 79                               # Italy
b9 07 70 72 6f 64 75 63 74                      # product: (event)
80 0c                                           # Product
   c4 6e 61 6d 65                                  # name:
   e6 43 6f 66 66 65 65                            # Coffee
22 00 00 00                                     # msg-length
b9 02 74 6f                                     # to: (event)
e6 46 72 61 6e 63 65                            # France
b9 07 70 72 6f 64 75 63 74                      # product: (event)
80 0c                                           # Product
   c4 6e 61 6d 65                                  # name:
   e6 43 68 65 65 73 65                            # Cheese
24 00 00 00                                     # msg-length
b9 02 74 6f                                     # to: (event)
e7 41 6d 65 72 69 63 61                         # America
b9 07 70 72 6f 64 75 63 74                      # product: (event)
80 0d                                           # Product
   c4 6e 61 6d 65                                  # name:
   e7 50 6f 70 63 6f 72 6e                         # Popcorn
```


Now that we have written data, this could be sent as a streaming event over an Event-Driven-Architecture, for example by using a Chronicle Queue. Then to read these streaming events, we can use a MethodReader.

```
MethodReader reader = wire.methodReader((Routing) destinationMap::get);
```


To continuously process messages using the current thread, the code would look like this:

```
for (; ; ) {
    // true if a message was read
    boolean success = reader.readOne();
}
```


or if we only want to run until there were no more messages to read, then the code would look like this:

```
boolean success;
do {
    // true if a message was read
    success = reader.readOne();
} while (success);
```


Running the code outputs:

```
Sends the product to Italy, product=!Product {
  name: Coffee
}

Sends the product to France, product=!Product {
  name: Cheese
}

Sends the product to America, product=!Product {
  name: Popcorn
}
```


### Summary {#h3-0-summary}

All of the above has been built using the open-source product Chronicle-Wire.

This article has shown how it is possible to use method chaining to route messages, but this is not the only use-case for method chaining.

This technique can also allow other types of metadata to be associated with business events.

Other uses for method chaining and associating meta-information include setting a message priority for a priority queue or recording access history.

Then, Dispatching events with associated metadata over an event-driven architecture (EDA) framework allows custom lightweight microservices to read and act upon that metadata.

### What else {#h3-1-what-else}

[Chronicle-Services](https://chronicle.software/products/services/ "Chronicle-Services") takes this concept to the next stage, offering an EDA framework; it also comes with a web gateway that uses a similar principle to provide routing of JSON messages (below) to web-based clients using REST and WebSockets:

```
"to":"websocketClient1","product":{"name":"Coffee"}
"to":"websocketClient2","product":{"name":"Cheese"}
"to":"websocketClient3","product":{"name":"Popcorn"}
```

