---
title: "Google Remote Procedure Call (gRPC) Example in Payara Server"
slug: "google-remote-procedure-call-grpc-example-in-payara-server"
date: "2022-08-25T10:09:21+00:00"
lastmod: "2022-08-25T10:35:09+00:00"
description: "Payara has developed a module to support gRPC. It is available in Payara Community GitHub repository. Find out more here!"
canonical: "https://blog.payara.fish/grpc-example-in-payara-server"
authors:
  - "luis-neto"
image: "payara_square_logo.jpg"
categories:
  - "Microservices"
  - "Payara"
  - "Release Notes"
  - "Tutorials"
tags:
related_posts:
  - "7-reasons-to-switch-to-openjdk-17-as-a-jakarta-ee-developer"
  - "a-simple-service-with-spring-boot"
  - "easy-jakarta-ee-integration-testing"
enlighterjs: true
frozen: false
---

Google Remote Procedure Call, or gRPC, is an open source Remote Procedure Call (RPC) framework focused on high performance and is portable to any environment.

It is an alternative to REST, deemed by many as more efficient.

After one of our customers requested it, the extension became available in our April 2022 Payara Community and Enterprise releases: 5.2022.2 and 5.38.0.

After providing a brief overview of the gRPC extension in Payara in our [April release blog,](https://blog.payara.fish/whats-new-in-the-april-2022-payara-platform-release) here we provide more technical details about how to use it in Payara Community, where you will build the module yourself and place it in the modules directory.

How Does gRPC Work?
-------------------

[gRPC](https://grpc.io/) is based on the idea of defining a service by specifying the contract interface. The server implements this interface and runs a gRPC server to handle client requests. The client has a stub which has the same methods as the server on its side.

gRPC was designed for HTTP/2, which provides important performance benefits over HTTP 1.x:

* It uses binary compression and framing. HTTP/2 is more compact and has improved efficiency both sending and receiving.
* It implements multiple HTTP/2 calls over a single TCP connection.

[Protocol Buffers](https://developers.google.com/protocol-buffers) (Protbuf) are used by default to serialize the messages. Protbuf is the Interface Definition Language (IDL) to define the payload messages structure and the service interface. The serialization is very quick on both server and client side.

It generates small payload messages which are suitable for limited bandwidth devices like mobile phones. The picture below shows the communication between a gRPC server implemented in Java and two different clients: a C++ Client and an Android App.
![](https://blog.payara.fish/hubfs/image-png-Apr-12-2022-05-35-20-81-PM.png)

gRPC Support in Payara
----------------------

Payara has developed a module to support gRPC. It is available in Payara Community GitHub repository at: <https://github.com/payara/gRPC>. The user can clone and build the project using Maven or just download the JAR file from: [gRPC Support JAR.](https://github.com/payara/Payara/blob/master/appserver/tests/payara-samples/test-domain-setup/src/main/resources/grpc-support-1.0.0.jar)

The user can manually copy this file to Payara modules:

```
cp grpc-support-1.0.0.jar ${PAYARA_HOME}/glassfish/modules
```


or in case of configuring this copy automatically in a test project see our example available at: [GrpcModuleTest](https://github.com/payara/Payara/blob/master/appserver/tests/payara-samples/test-domain-setup/src/test/java/fish/payara/samples/setuptests/GrpcModuleTest.java).

For both options, Payara Server should be restarted:

```
${PAYARA_HOME}\bin> .\asadmin restart-domain
```


The restarting can also be automatized for testing purposes. See [RestartDomain](https://github.com/payara/Payara/blob/master/appserver/tests/payara-samples/test-domain-setup/src/test/java/fish/payara/samples/RestartDomain.java) and [RestartingDomainTest](https://github.com/payara/Payara/blob/master/appserver/tests/payara-samples/test-domain-setup/src/test/java/fish/payara/samples/setuptests/RestartDomainTest.java).

After restarting Payara Server, the user should run the following commads to make sure HTTP/2 and HTTP Push are activated:

```
./asadmin set configs.config.server-config.network-config.protocols.protocol.http-listener-1.http.http2-push-enabled=true./asadmin set configs.config.server-config.network-config.protocols.protocol.http-listener-1.http.http2-enabled=true
```


In the next sections, we will show an implementation based on the Java gRPC example defined in gRPC official tutorial: <https://grpc.io/docs/languages/java/basics/>

Service Definition
------------------

We use Protbuf to define the gRPC Service with its types for request and response methods. The complete file can be found in[route_guide.proto](https://github.com/payara/Payara-Examples/blob/master/grpc/grpc-stubs/src/main/proto/route_guide.proto).

The first step is to define the service name in .proto file:

```
service RouteGuide {}
```


Now we can define some `rpc` methods into the service definition with their types for request and response. Our service includes four methods that encompass the four possible kinds:

* **rpc** ` GetFeature(Point) `**returns**` (Feature) {}`
* **rpc** ` ListFeatures(Rectangle) `**returns**` (stream Feature) {}`
* **rpc** ` RecordRoute(stream Point) `**returns**` (RouteSummary) {}`
* **rpc** ` RouteChat(stream RouteNote) `**returns**` (stream RouteNote) {}`

`GetFeature` returns the `Feature` at a given `Point`. It acts like a *simple function call*done by the client as a request to the server through the client Stub.

`ListFeatures` represents a type where the client sends a request and *gets* a *`stream`* . It uses the `stream` returned to read a list of messages sequentially.

`RecordRoute` is of type:*client side stream* . The client writes a list of messages in a `stream` that is part of the payload request. After this it waits for the server to read all messages and send a response.

`RouteChat` is a *bidirectional streaming RPC*. Both client and server write messages in its streams. The detail here is that these streams are independent from each other. It means there are no rules that amount to: 'wait for all messages come before sending messages'. It is up to each side to manage simultaneous message streams.

We can also see some message types defined in our `.proto` file. For instance, see the definition of a Rectangle message:

```java
message Rectangle {
  // One corner of the rectangle.
  Point lo = 1;
  // The other corner of the rectangle.
  Point hi = 2;
}
```


At this point, we have the service defined. Then we can create the Stubs and work on Server and Client creations.

Stubs Generation
----------------

Once we have `.proto` file, we will create the gRPC client and server interfaces from it. We can do this using Maven as described here: <https://github.com/grpc/grpc-java/blob/master/README.md>[](https://github.com/grpc/grpc-java/blob/master/README.md)

We created a module in our[gRPC example](https://github.com/payara/Payara-Examples/tree/master/grpc)called grpc-stubs, as shown in picture below. The `.proto` file was copied into `proto` folder.
![](https://blog.payara.fish/hubfs/image-png-Apr-13-2022-10-21-10-20-PM.png)

In `grpc-stubs->pom.xml` we included the gRPC dependencies and protobuf-maven-plugin that can generate the code during the Maven build. If the user clones [Payara-Examples](https://github.com/payara/Payara-Examples/) project, then he just need to run the following Maven command from `grpc-stubs` directory:
![](https://blog.payara.fish/hubfs/image-png-Apr-13-2022-10-22-50-19-PM.png)

The results can be found in `target->generated-sources`. The main files generated by proto-plugin are:

* `RouteGuideGRpc.java` which comprises:
  * *stub* classes used by clients to communicate with a server.
  * a `RouteGuideGrpc.RouteGuideImplBase` abstract class to be implemented by Servers and have the methods defined in `RouteGuide` proto service.
* `Rectangle.java`, `Point.java` and `Feature.java`. There are also other classes that implement a protocol buffer to manipulate request and response message types.

![](https://blog.payara.fish/hubfs/image-png-Apr-14-2022-10-07-13-88-AM.png)

Server Creation
---------------

The RouteGuide server is implemented in our example by class[RouteGuideService.](https://github.com/payara/Payara-Examples/blob/master/grpc/grpc-web/src/main/java/fish/payara/example/grpc/RouteGuideService.java) It overrides the methods defined in `RouteGuideGrpc.RouteGuideImplBase` giving the actual behavior to the service.

In the [official gRPC example](https://github.com/grpc/grpc-java/blob/master/examples/src/main/java/io/grpc/examples/routeguide/RouteGuideServer.java), specific methods are included for running the gRPC Server and responding to requests from clients. These methods are not necessary in the Payara example since we created a web project to be deployed in Payara Server. Therefore, we will now look into our RouteGuide implementation.

### Route Guide Implementation

First of all, our class implements the abstract base class:

```java
@ApplicationScoped
public class RouteGuideService extends RouteGuideGrpc.RouteGuideImplBase {
   ...
}
```


Inside the class, we have the implementation of ALL service methods.

For instance, see `getFeature` method which receives a `Point` and a `StreamObserver` from client. Then it finds the feature in the local database and sends it to the observer:

```java
@Override
public void getFeature(Point request, StreamObserver<Feature> responseObserver) {
    responseObserver.onNext(featureRepository.findFeature(request));
    responseObserver.onCompleted();
}
```


We also have the implementation for other three types of RCP calls: Streaming in Server-Side, Streaming in Client-Side and Bidirectional Streaming.

#### Streaming in Server-Side

The method `listFeatures` is used to send back multiple `Feature`s to the client.

For each `Feature` in the database that is inside the `Rectangle` in the payload, the method sends the `Feature` to the client Observer.

When the loop finishes, it calls method `onCompleted` to tell the Observer that all messages were sent.

```java
@Override
public void listFeatures(Rectangle request, StreamObserver<Feature> responseObserver) {
    int left = min(request.getLo().getLongitude(), request.getHi().getLongitude());
    int right = max(request.getLo().getLongitude(), request.getHi().getLongitude());
    int top = max(request.getLo().getLatitude(), request.getHi().getLatitude());
    int bottom = min(request.getLo().getLatitude(), request.getHi().getLatitude());

    for (Feature feature : featureRepository.getFeatures()) {
        if (!routeGuideUtil.exists(feature)) {
            continue;
        }

        int lat = feature.getLocation().getLatitude();
        int lon = feature.getLocation().getLongitude();
        if (lon >= left && lon <= right && lat >= bottom && lat <= top) {
            responseObserver.onNext(feature);
        }
    }
    responseObserver.onCompleted();
}
```


#### Streaming in Client-Side

The next method we will get into is `recordRoute`.

This method receives a stream of `Point`s from client and returns through the StreamObserver a `RouteSummary`.

```java
@Override
public StreamObserver<Point> recordRoute(final StreamObserver<RouteSummary> responseObserver) {
    return new StreamObserver<Point>() {
        ...

        @Override
        public void onNext(Point point) {
            ...
        }

        ...

        @Override
        public void onCompleted() {
            long seconds = NANOSECONDS.toSeconds(System.nanoTime() - startTime);
            responseObserver.onNext(RouteSummary.newBuilder().setPointCount(pointCount)
                    .setFeatureCount(featureCount).setDistance(distance)
                    .setElapsedTime((int) seconds).build());
            responseObserver.onCompleted();
        }
    };
}
```


Inside the method, the example implements interface `StreamObserver` anonymously by overriding the methods:

* `onNext`: called everytime client writes a Point into stream message.
* `onCompleted`: called when client finishes writing into stream message. Used to build the `RouteSummary` and call `onComplete` over the `responseObserver` to send the results to the client.

#### Bidirectional Streaming

To finish the server creation we examine the bidirectional method `routeChat:`

```java
@Override
public StreamObserver<RouteNote> routeChat(final StreamObserver<RouteNote> responseObserver) {
    return new StreamObserver<RouteNote>() {
        @Override
        public void onNext(RouteNote note) {
            List<RouteNote> notes = featureRepository.getOrCreateNotes(note.getLocation());

            for (RouteNote prevNote : notes.toArray(new RouteNote[0])) {
                responseObserver.onNext(prevNote);
            }

            notes.add(note);
        }

        @Override
        public void onError(Throwable t) {
            logger.log(Level.WARNING, "routeChat cancelled");
        }

        @Override
        public void onCompleted() {
            responseObserver.onCompleted();
        }
    };
}
```


Here we also receive and return a `stream` as in previous method.

The main difference is that the two `stream`s are completely independent.

Server and client can write and read in any order.

Client Creation
---------------

We created our client into the same module: [`grpc-web`](https://github.com/payara/Payara-Examples/tree/master/grpc/grpc-web), but it belongs to the [`test`](https://github.com/payara/Payara-Examples/tree/master/grpc/grpc-web/src/test) source folder. Therefore, we encapulated the grpc client into our tests. As we have a dependency to project grpc-stubs in `grpc-web->pom.xml`:

```xml
<dependency>
    <groupId>fish.payara.grpc</groupId>
    <artifactId>grpc-stubs</artifactId>
    <version>1.0-SNAPSHOT</version>
    <scope>compile</scope>
</dependency>
```


Then the stubs are available to be instantiated by our client: [RouteGuideClient](https://github.com/payara/Payara-Examples/blob/master/grpc/grpc-web/src/test/java/fish/payara/example/grpc/RouteGuideClient.java).

### Stubs Instantiation

The first thing to notice is that into our class constructor we instantiate two stubs:

```java
public RouteGuideClient(Channel channel, String clientPrefix) {
    blockingStub = RouteGuideGrpc.newBlockingStub(channel);
    asyncStub = RouteGuideGrpc.newStub(channel);
    routeGuideUtil = new RouteGuideUtil();
    this.clientPrefix = clientPrefix;
}
```


* `blockingStub`: this a synchronous stub which means the RPC client waits for the response.
* `asyncStub`: it will make non-blocking calls. Therefore the response is asynchronous.

The types of these stubs were defined in project [grpc-stubs](https://github.com/payara/Payara-Examples/tree/master/grpc/grpc-stubs): `RouteGuideGrpc.RouteGuideBlockingStub` and `RouteGuideGrpc.RouteGuideStub`, respectively.

`Channel` is created using `ManagedChannelBuilder` in[`TestGrpc`](https://github.com/payara/Payara-Examples/blob/master/grpc/grpc-web/src/test/java/fish/payara/example/grpc/TestGrpc.java) and passed as parameter to `RouteGuideClient` constructor.

```java
ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
...
RouteGuideClient client = new RouteGuideClient(channel, clientIdPrefix);
```


Inside `RouteGuideClient` constructor the channel is passed twice to create the stubs using methods: `newBlockingStub` and `newStub`.

### Service Methods Invocation

To finish this section let's see how we call our four service methods. The results showed in this subsection were generated by running:
![](https://blog.payara.fish/hubfs/image-png-Apr-15-2022-06-12-06-82-PM.png)

#### Simple Function Call

We did it twice in our [`TestGrpc`](https://github.com/payara/Payara-Examples/blob/master/grpc/grpc-web/src/test/java/fish/payara/example/grpc/TestGrpc.java):

```java
// Looking for a valid feature
client.getFeature(409146138, -746188906);

// Feature missing.
client.getFeature(0, 0);
public void getFeature(int lat, int lon) {
    LogHelper.info(clientPrefix+"*** GetFeature: lat={0} lon={1}", lat, lon);

    Point request = Point.newBuilder().setLatitude(lat).setLongitude(lon).build();

    Feature feature;
    try {
        feature = blockingStub.getFeature(request);

    } catch (StatusRuntimeException e) {
        LogHelper.warning(clientPrefix+"RPC failed: {0}", e.getStatus());

        return;
    }
    if (routeGuideUtil.exists(feature)) {
        LogHelper.info(clientPrefix+"Found feature called \"{0}\" at {1}, {2}",
                feature.getName(),
                routeGuideUtil.getLatitude(feature.getLocation()),
                routeGuideUtil.getLongitude(feature.getLocation()));
    } else {
        LogHelper.info(clientPrefix+"Found no feature at {0}, {1}",
                routeGuideUtil.getLatitude(feature.getLocation()),
                routeGuideUtil.getLongitude(feature.getLocation()));
    }
}
```


It acts like calling a local method. The results after the call: `getFeature(409146138, -746188906)`

```
INFO: [] *** GetFeature: lat=409,146,138 lon=-746,188,906
Apr 18, 2022 8:26:39 AM fish.payara.example.grpc.LogHelper info
INFO: [] Found feature called "Berkshire Valley Management Area Trail, Jefferson, NJ, USA" at 40.915, -74.619
```


And after second call: `getFeature(0, 0)`

```
INFO: [] *** GetFeature: lat=0 lon=0
Apr 18, 2022 8:26:39 AM fish.payara.example.grpc.LogHelper info
INFO: [] Found no feature at 0, 0
```


#### Server-side Streaming Call

Now our client calls `listFeatures` method:

```java
// Looking for features between 40, -75 and 42, -73.
client.listFeatures(400000000, -750000000, 420000000, -730000000);
This time blockingStub will not return just a Feature, but an Iterator which is used to access all Features sent by the server.

Iterator<Feature> features;
try {
    features = blockingStub.listFeatures(request);
    for (int i = 1; features.hasNext(); i++) {
        Feature feature = features.next();
        LogHelper.info(clientPrefix+"Result #" + i + ": {0}", feature);
    }
} catch (StatusRuntimeException e) {
    LogHelper.warning(clientPrefix+"RPC failed: {0}", e.getStatus());
}
```


This time `blockingStub` will not return just a `Feature`, but an `Iterator` which is used to access all Features sent by the server.

```java
Iterator<Feature> features;
try {
    features = blockingStub.listFeatures(request);
    for (int i = 1; features.hasNext(); i++) {
        Feature feature = features.next();
        LogHelper.info(clientPrefix+"Result #" + i + ": {0}", feature);
    }
} catch (StatusRuntimeException e) {
    LogHelper.warning(clientPrefix+"RPC failed: {0}", e.getStatus());
}
```


Bellow you can see part of 64 features printed in tests log:

```
INFO: [] *** ListFeatures: lowLat=400,000,000 lowLon=-750,000,000 hiLat=420,000,000 hiLon=-730,000,000

INFO: [] Result #1: name: "Patriots Path, Mendham, NJ 07945, USA"
location {
latitude: 407838351
longitude: -746143763
}

INFO: [] Result #2: name: "101 New Jersey 10, Whippany, NJ 07981, USA"
location {
latitude: 408122808
longitude: -743999179
}

...

INFO: [] Result #64: name: "3 Hasta Way, Newton, NJ 07860, USA"
location {
latitude: 410248224
longitude: -747127767
}
```


#### Client-side Streaming Call

Next we will test the call to method `recordRoute` passing features list and the number of points we want to send.

```java
// Record a few randomly selected points from the features file.
client.recordRoute(features, 10);
StreamObserver<Point> requestObserver = asyncStub.recordRoute(responseObserver);
try {
    for (int i = 0; i < numPoints; ++i) {
        int index = random.nextInt(features.size());
        Point point = features.get(index).getLocation();
        LogHelper.info(clientPrefix+"Visiting point {0}, {1}", routeGuideUtil.getLatitude(point),
                routeGuideUtil.getLongitude(point));
        requestObserver.onNext(point);

        Thread.sleep(random.nextInt(1000) + 500);
        if (finishLatch.getCount() == 0) {
            // RPC completed or errored before we finished sending.
            // Sending further requests won't error, but they will just be thrown away.
            return;
        }
    }
} catch (RuntimeException e) {
    requestObserver.onError(e);
    throw e;
}
```


Above we see part of `recordRoute` implementation in` RouteGuideClient`. This time we use `asyncStub` in `recordRoute` implementation to send ten random points asynchronously.

In order to print out the `RouteSummary` written by the server, we override `onNext` method:

```java
@Override
public void onNext(RouteSummary summary) {
    LogHelper.info(clientPrefix+"Finished trip with {0} points. Passed {1} features. "
                    + "Travelled {2} meters. It took {3} seconds.", summary.getPointCount(),
            summary.getFeatureCount(), summary.getDistance(), summary.getElapsedTime());

}
```


We also override `onCompleted` method to reduce `CountDownLatch` to zero when the server finishes writing:

```java
@Override
public void onCompleted() {
    LogHelper.info(clientPrefix+"Finished RecordRoute");
    finishLatch.countDown();
}
```


The log resulting from our test execution is the following:

```
INFO: [] *** RecordRoute
INFO: [] Visiting point 41.465, -74.048
INFO: [] Visiting point 40.569, -74.929
INFO: [] Visiting point 41.755, -74.008
INFO: [] Visiting point 41.881, -74.172
...
INFO: [] Visiting point 40.466, -74.482
INFO: [] Finished trip with 10 points. Passed 6 features. Travelled 596,646 meters. It took 10 seconds.
INFO: [] Finished RecordRoute
```


#### Bidirectional Streaming Call

To finish, let's examine `routeChat` bidirectional method.

```java
// Send and receive some notes.
CountDownLatch finishLatch = client.routeChat();
public CountDownLatch routeChat() {
    LogHelper.info(clientPrefix+"*** RouteChat");
    final CountDownLatch finishLatch = new CountDownLatch(1);
    StreamObserver<RouteNote> requestObserver =
            asyncStub.routeChat(new StreamObserver<RouteNote>() {
                @Override
                public void onNext(RouteNote note) {
                    LogHelper.info(clientPrefix+"Got message \"{0}\" at {1}, {2}", note.getMessage(), note.getLocation()
                            .getLatitude(), note.getLocation().getLongitude());

                }

                @Override
                public void onError(Throwable t) {
                    LogHelper.warning(clientPrefix+"RouteChat Failed: {0}", Status.fromThrowable(t));
                    finishLatch.countDown();
                }

                @Override
                public void onCompleted() {
                    LogHelper.info(clientPrefix+"Finished RouteChat");
                    finishLatch.countDown();
                }
            });

    try {
        RouteNote[] requests =
                {newNote("First message", 0, 0), newNote("Second message", 0, 10_000_000),
                        newNote("Third message", 10_000_000, 0), newNote("Fourth message", 10_000_000, 10_000_000)};

        for (RouteNote request : requests) {
            LogHelper.info(clientPrefix+"Sending message \"{0}\" at {1}, {2}", request.getMessage(), request.getLocation()
                    .getLatitude(), request.getLocation().getLongitude());
            requestObserver.onNext(request);
        }
    } catch (RuntimeException e) {
        requestObserver.onError(e);
        throw e;
    }
    requestObserver.onCompleted();

    return finishLatch;
}
```


Method `asyncStub.routeChat` also receives and returns a `StreamObserver` as in `asyncStub.recordRoute` method. Although this time the client sends messages to the stream at the same time that the server writes messages into the other stream and these streams are completely independent from each other.

The results logged in client side are:

```
INFO: [] *** RouteChat
INFO: [] Sending message "First message" at 0, 0
INFO: [] Sending message "Second message" at 0, 10,000,000
INFO: [] Sending message "Third message" at 10,000,000, 0
INFO: [] Sending message "Fourth message" at 10,000,000, 10,000,000
INFO: [] Finished RouteChat
```


Test Environment
----------------

To run the tests in client side, we used the following configuration:

* Operating System: Ubuntu 20.04 LTS
* Maven: v3.8.4
* Java: OpenJDK Zulu8 v1.8.0_322
* Payara Server: v5.2022.2

Summary
-------

We hope you've found this helpful as a way to get started with gRPC and Payara Community. We have also added information on gRPC to our documentation and it is available [here.](https://docs.payara.fish/community/docs/documentation/extensions/grpc/README.html#what-is-grpc)

However, by using Payara Enterprise, you can access gRPC functionality *even more* easily. The iteration of Payara Platform designed for mission critical applications, Payara Enterprise now has a compiled gRPC module ready that customers can download, then place in the modules directory. Request Payara Enterprise [here.](https://www.payara.fish/page/payara-enterprise-downloads/?hsCtaTracking=49670096-d865-451a-bb69-e099fdd559d6%7C7bfb7f13-a491-4f3e-9514-5fc652a1e935)

By adding the modern framework gRPC to Payara Server, we continue to expand your choices when using Jakarta EE APIs. gRPC is totally optional, and having it available as an extension means you can pick and choose when it is used.

Please feel free to provide your feedback and tips as you get to grips with this exciting new feature, by posting on the [Payara Forum.](https://forum.payara.fish/)
