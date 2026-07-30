---
title: "SpringBoot and Kafka Streams – Event Routing & Testing"
slug: "spring-boot-kafka-streams-event-routing-testing"
date: "2025-06-19T08:37:11+00:00"
lastmod: "2025-06-19T08:38:07+00:00"
description: "SpringBoot and Kafka Streams – Event Routing & Testing. Build a Kafka Streams app to validate and route Lille city tour!"
authors:
  - "vincent-vauban"
image: "/images/posts/2025/06/spring-boot-kafka-streams-event-routing-testing/kstream.png"
categories:
  - "Java"
  - "Kafka"
  - "Spring"
  - "Streaming"
  - "Videos"
tags:
related_posts:
  - "building-a-real-time-ai-fraud-detection-system-with-spring-kafka-and-mongodb"
  - "building-real-time-applications-to-process-wikimedia-streams-using-kafka-and-hazelcast"
  - "chronicle-services-building-fast-microservices-with-java"
  - "cloud-cost-optimization-is-hard-java-can-help"
enlighterjs: true
frozen: false
---

Welcome to this hands-on guide to building a Spring Boot Kafka Streams application! (SpringBoot and Kafka Streams).

In this article, I'll walk you through a project I built during the first day of a three-day Kafka Streams training. The goal? Validate sightseeing events in Lille based on predefined timetables and route the data accordingly.

Let's explore how Kafka Streams powers a real-time city tour experience! 🧭

{{< youtube s07d3SmoBMI >}}

<br />

*** ** * ** ***

🔵⚪⚪⚪⚪⚪⚪⚪⚪⚪

1️⃣ The Use Case: Lille City Tour {#h2-0-1-the-use-case-lille-city-tour}
------------------------------------------------------------------------

### Imagine you're planning a visit through Lille, France. {#h3-1-imagine-you-re-planning-a-visit-through-lille-france}

You want to see:

* Gare Lille Flandres
* St. Maurice Church
* Les Moules Restaurant
* Place du Général de Gaulle
* Opera, and more...

Each sightseeing spot has a specific opening and closing time.  

Visitors submit their visit plans, and we validate whether the visit can be scheduled within the location's allowed timetable.

*** ** * ** ***

🔵🔵⚪⚪⚪⚪⚪⚪⚪⚪

2️⃣ The Goal {#h2-2-2-the-goal}
-------------------------------

### What I want to do! {#h3-3-what-i-want-to-do}

1. Receive event submissions (location + visit time).
2. Check whether the visit is valid.
3. Route events:

* ✅ Valid visits → trip-steps topic.
* ❌ Invalid visits → DLQ topic (dead-letter queue).

*** ** * ** ***

🔵🔵🔵⚪⚪⚪⚪⚪⚪⚪

3️⃣ Tech Stack {#h2-4-3-tech-stack}
-----------------------------------

### What I used for this demo {#h3-5-what-i-used-for-this-demo}

* Apache Kafka
* Kafka Streams
* Kafka UI for topic management
* Kafka Streams Viz to visualize the topology
* Docker for local environment
* Java for stream logic

*** ** * ** ***

🔵🔵🔵🔵⚪⚪⚪⚪⚪⚪

4️⃣ Data Modeling {#h2-6-4-data-modeling}
-----------------------------------------

### What is the model of the visit data {#h3-7-what-is-the-model-of-the-visit-data}

Each location has its own timetable:

<pre class="EnlighterJSRAW" style="position: relative;" data-enlighter-language="json">[
  {
    "location": "Gare Lille Flandres",
    "timeRanges": [
      { "start": "08:00", "end": "12:00" },
      { "start": "14:00", "end": "18:00" }
    ]
  },
  {
    "location": "St. Maurice Church",
    "timeRanges": [
      { "start": "09:00", "end": "17:00" }
    ]
  }
]</pre>

Each event from the visitor looks like:

<pre class="EnlighterJSRAW" style="position: relative;" data-enlighter-language="json">{
  "location": "Beffroi",
  "hour": "13:00"
}</pre>

The system will return:

<pre class="EnlighterJSRAW" style="position: relative;" data-enlighter-language="json">{
  "location": "Beffroi",
  "hour": "13:00",
  "status": "OK"
}</pre>

Or, if the visit falls outside the available range:

<pre class="EnlighterJSRAW" style="position: relative;" data-enlighter-language="json">{
  "location": "Beffroi",
  "hour": "20:00",
  "status": "KO"
}</pre>

*** ** * ** ***

🔵🔵🔵🔵🔵⚪⚪⚪⚪⚪

5️⃣ Kafka Streams Topology {#h2-8-5-kafka-streams-topology}
-----------------------------------------------------------

### 🧠 Concept {#h3-9-concept}

#### Kafka Streams builds real-time processing flows using topologies.

In our case:

**Input: visit-event topic**

**Processing:**

* Deserialize the message
* Validate against ValidTimetableService
* Set status as OK/KO
* Branch stream

**Output:**

* trip-steps for valid events
* DLQ for invalid ones

### 🧾 Key Logic {#h3-10-key-logic}

#### The processors involved:

<pre class="EnlighterJSRAW" style="position: relative;" data-enlighter-language="java">KStream&lt;String, VisitEvent&gt; rawVisits = builder.stream("visit-event");

KStream&lt;String, VisitStatus&gt; validatedVisits = rawVisits
    .mapValues(event -&gt; {
        boolean isValid = validTimetableService.isValid(event.getLocation(), event.getHour());
        return new VisitStatus(event.getLocation(), event.getHour(), isValid ? "OK" : "KO");
    });

validatedVisits.split()
    .branch((key, status) -&gt; "OK".equals(status.getStatus()), Branched.withConsumer(ks -&gt; ks.to("trip-steps")))
    .branch((key, status) -&gt; "KO".equals(status.getStatus()), Branched.withConsumer(ks -&gt; ks.to("DLQ")));</pre>

### 🖥️ Visualization {#h3-11-visualization}

#### Using Kafka Streams Viz:[Kafka Streams Topology Visualizer](http://https://zz85.github.io/kafka-streams-viz/ "Kafka Streams Topology Visualizer"))

I generated this simple topology:  
[![Topology Kafka Stream Viz](/images/posts/2025/06/spring-boot-kafka-streams-event-routing-testing/topology-185x510.png "Topology Kafka Stream Viz")](/images/posts/2025/06/spring-boot-kafka-streams-event-routing-testing/topology-185x510.png "Topology Kafka Stream Viz")  

Or in simple way:  
`[ visit-event ] --> [ validation logic ] --> [ trip-steps / DLQ ] `  

Each branch of the stream is defined clearly, allowing easy debugging and maintainability.

*** ** * ** ***

🔵🔵🔵🔵🔵🔵⚪⚪⚪⚪

6️⃣ Tools in Action {#h2-12-6-tools-in-action}
----------------------------------------------

#### 🔄 Kafka Topics: All messages are pushed and consumed in real time.

<img fetchpriority="high" decoding="async" aria-describedby="caption-attachment-116470" class="size-medium wp-image-116470" src="/images/posts/2025/06/spring-boot-kafka-streams-event-routing-testing/topics-700x208.png" alt="Topics involved: visit-events, trip-steps, trip-dlq" width="700" height="208">

Topics involved: ***visit-events, trip-steps, trip-dlq***{#caption-attachment-116470}

#### 🧰 Kafbat UI: Used to inspect Kafka topics and payloads during development.

<pre class="EnlighterJSRAW" style="position: relative;" data-enlighter-language="yaml">kafbat-ui:
  container_name: kafbat-ui
  image: ghcr.io/kafbat/kafka-ui:latest
  ports:
    - 8080:8080
  environment:
    DYNAMIC_CONFIG_ENABLED: 'true'
    KAFKA_CLUSTERS_0_NAME: local
    KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS: kafka:29092</pre>

#### 🧭 ValidTimetableService: A custom utility that loads all location timetables and verifies visit requests.

<pre class="EnlighterJSRAW" style="position: relative;" data-enlighter-language="java">/**
     * Sends the list of events to the specified Kafka topic.
     * @param bootstrapServers Kafka bootstrap servers
     * @param topic Kafka topic to send messages to
     * @param events List of CSV event lines to send
     */
    public static void produceEvents(String bootstrapServers, String topic, List&lt;String&gt; events) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        try (KafkaProducer&lt;String, String&gt; producer = new KafkaProducer&lt;&gt;(props)) {
            for (String event : events) {
                ProducerRecord&lt;String, String&gt; record = new ProducerRecord&lt;&gt;(topic, null, event);
                producer.send(record, (metadata, exception) -&gt; {
                    if (exception != null) {
                        System.err.println("Failed to send event: " + event);
                        exception.printStackTrace();
                    } else {
                        System.out.printf("Sent: %s to partition %d offset %d%n", event, metadata.partition(), metadata.offset());
                    }
                });
            }
            producer.flush();
        }
    }</pre>

#### 🧪 Unit Tests: Every logic block is testable, ensuring accuracy before production deployment.

<pre class="EnlighterJSRAW" style="position: relative;" data-enlighter-language="java">class VisitStatusTopologyTest {

    private TopologyTestDriver testDriver;
    private TestInputTopic&lt;String, String&gt; inputTopic;
    private TestOutputTopic&lt;String, VisitStatus&gt; okOutputTopic;
    private TestOutputTopic&lt;String, VisitStatus&gt; koOutputTopic;
    private final String inputTopicName = "visit-events";
    private final String okTopicName = "trip-steps";
    private final String koTopicName = "trip-dlq";

    private final Serde&lt;String&gt; stringSerde = Serdes.String();
    private final Serde&lt;VisitStatus&gt; visitStatusSerde = new VisitStatusSerde();

    @BeforeEach
    void setup() {
        VisitStatusTopology topology = new VisitStatusTopology();
        Topology kafkaTopology = topology.build();

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test-visit-status-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        testDriver = new TopologyTestDriver(kafkaTopology, props);

        inputTopic = testDriver.createInputTopic(inputTopicName, stringSerde.serializer(), stringSerde.serializer());
        okOutputTopic = testDriver.createOutputTopic(okTopicName, stringSerde.deserializer(), visitStatusSerde.deserializer());
        koOutputTopic = testDriver.createOutputTopic(koTopicName, stringSerde.deserializer(), visitStatusSerde.deserializer());
    }

    @AfterEach
    void teardown() {
        testDriver.close();
    }

    @Test
    void testValidVisitGoesToOkTopic() {
        // Given a valid visit event within timetable range (e.g. Place Louise de Bettignies is always valid)
        String input = "Place Louise de Bettignies,12:00,OK";

        // When sending input record
        inputTopic.pipeInput(null, input);

        // Then output in OK topic with status "OK"
        assertFalse(okOutputTopic.isEmpty());
        VisitStatus visitStatus = okOutputTopic.readValue();
        assertEquals("Place Louise de Bettignies", visitStatus.location());
        assertEquals("12:00", visitStatus.time());
        assertEquals("OK", visitStatus.status());

        // NOK topic should be empty
        assertTrue(koOutputTopic.isEmpty());
    }
    //...</pre>

#### 👨‍💻Full repsoitory on GitHub: [vinny59200 / kstream-lille-city-tour](https://github.com/vinny59200/kstream-lille-city-tour "vinny59200 / kstream-lille-city-tour")

*** ** * ** ***

🔵🔵🔵🔵🔵🔵🔵⚪⚪⚪

7️⃣ What I Learned {#h2-13-7-what-i-learned}
--------------------------------------------

This project helped solidify my understanding of:

* Stream processing design with Kafka
* Real-time data validation
* Branching and routing event streams
* Working with external services (like timetable checks) inside a stream

And most importantly, building a real-life use case that's both educational and fun!

*** ** * ** ***

🔵🔵🔵🔵🔵🔵🔵🔵⚪⚪

8️⃣ Next Steps {#h2-14-8-next-steps}
------------------------------------

### Going further with SpringBoot and Kafka Streams {#h3-15-going-further-with-springboot-and-kafka-streams}

Here's what could be added next:

* Store validated trips in a database (PostgreSQL or MongoDB)
* Add user context and preferences
* Visualize city tour analytics on a live dashboard
* Expose REST endpoints to submit visits and query status

*** ** * ** ***

🔵🔵🔵🔵🔵🔵🔵🔵🔵⚪

9️⃣ Try It Yourself {#h2-16-9-try-it-yourself}
----------------------------------------------

### Want to explore this yourself? {#h3-17-want-to-explore-this-yourself}

Clone the project ([vinny59200 / kstream-lille-city-tour](https://github.com/vinny59200/kstream-lille-city-tour "vinny59200 / kstream-lille-city-tour")), run the containers, and start submitting events to see the validation in action.

🧪 Tip: Modify the timetable and see how event routing changes instantly!

*** ** * ** ***

🔵🔵🔵🔵🔵🔵🔵🔵🔵🔵

🔟 Conclusion {#h2-18-conclusion}
---------------------------------

### SpringBoot and Kafka Streams -- Event Routing \& Testing {#h3-19-springboot-and-kafka-streams-event-routing-testing}

Kafka Streams is an incredibly powerful tool for building real-time event processing pipelines.  

Through this Lille City Tour demo, we created a tangible use case that demonstrates stream branching, data validation, and error routing with just a few lines of code.

Want the code? [vinny59200 / kstream-lille-city-tour](https://github.com/vinny59200/kstream-lille-city-tour "vinny59200 / kstream-lille-city-tour")

Thanks for joining the tour! 🇫🇷✨

### See also {#h3-20-see-also}

#### Related to SpringBoot and Kafka Streams

📺 <https://youtu.be/s07d3SmoBMI>  

👩‍🏫 <https://developer.confluent.io/courses/kafka-streams/get-started>  

🍃 [Prepare Spring certification](https://www.udemy.com/course/spring-professional-certification-6-full-tests-2v0-7222-a/?referralCode=04B6ED315B27753236AC)

<br />
