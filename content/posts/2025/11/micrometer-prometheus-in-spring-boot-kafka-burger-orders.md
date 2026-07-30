---
title: "Micrometer & Prometheus in Spring Boot: Kafka Burger Orders"
slug: "micrometer-prometheus-in-spring-boot-kafka-burger-orders"
date: "2025-11-14T10:13:03+00:00"
lastmod: "2025-11-14T10:13:04+00:00"
description: "Learn Micrometer and Prometheus in Spring Boot by building a Kafka Burger Orders app that emits metrics. Step-by-step guide with code and takeaways."
authors:
  - "vincent-vauban"
image: "https://foojay.io/wp-content/uploads/2025/10/Untitled-1024x683.png"
categories:
  - "DevOps"
  - "Java"
  - "JDK Flight Recorder"
  - "Kafka"
  - "Microservices"
  - "Observability"
  - "Spring"
  - "Streaming"
  - "Tutorials"
  - "Videos"
tags:
related_posts:
enlighterjs: true
frozen: false
---

{{< youtube eSreg0xPGqo >}}

<br />

👨‍💻 GitHub: <https://github.com/vinny59200/dukeburger>

*** ** * ** ***

🔵⚪⚪⚪⚪⚪⚪⚪⚪⚪⚪⚪

This guide shows how to use **Micrometer and Prometheus in Spring Boot** to track a custom metric for a Kafka-driven Burger Orders app. You'll post a burger order to a REST endpoint, publish it to Kafka, consume the topic, and increment a counter for all "DukeBurger" orders. Copy the snippets, run, and you'll see your metric on `/actuator/prometheus`.

*** ** * ** ***

🔵🔵⚪⚪⚪⚪⚪⚪⚪⚪⚪⚪

Micrometer is a vendor-neutral metrics facade. Your code records counters, timers, and gauges once; Micrometer ships those to many backends (Prometheus, Datadog, etc.) via simple registries. Prometheus is a time-series database that **pulls** metrics by scraping an HTTP endpoint periodically (Spring exposes `/actuator/prometheus`). [Micrometer Application Observability](https://micrometer.io/docs/)

**Key ideas:**

* Micrometer offers a simple API: `Counter`, `Timer`, `Gauge`.
* Spring Boot Actuator autoconfigures Micrometer and exposes metrics endpoints, including Prometheus format. [See](https://docs.spring.io/spring-boot/reference/actuator/metrics.html)
* Prometheus "scrapes," so your app just exposes a text endpoint---no push needed. [docs.micrometer.io](https://docs.micrometer.io/micrometer/reference/implementations/prometheus.html)

*** ** * ** ***

🔵🔵🔵⚪⚪⚪⚪⚪⚪⚪⚪⚪

1. **Order a burger** via HTTP POST `/orders?burger=DukeBurger`.
2. **Produce** an Avro message to Kafka topic `burger.orders`.
3. **Consume** `burger.orders` with `@KafkaListener`.
4. **Increment** a Micrometer **Counter** named `events_DukeBurger_total` whenever the burger is `"DukeBurger"`.
5. **Expose** metrics at `/actuator/prometheus` for Prometheus to scrape.

This pattern is common: REST → Kafka → Consumer → Metric. Spring Kafka makes producing and consuming concise; Micrometer makes metrics easy. [See](https://docs.spring.io/spring-kafka/reference/kafka/receiving-messages/listener-annotation.html)

*** ** * ** ***

🔵🔵🔵🔵⚪⚪⚪⚪⚪⚪⚪⚪

<pre class="EnlighterJSRAW" style="position: relative" data-enlighter-language="json">{
  "type": "record",
  "name": "BurgerOrder",
  "namespace": "com.vv.burger",
  "fields": [
    { "name": "burger", "type": "string" },
    { "name": "timestamp", "type": "string" }
  ]
}</pre>

**Why:** A tiny schema keeps the demo clear. Avro gives you compact messages and generated classes.

*** ** * ** ***

🔵🔵🔵🔵🔵⚪⚪⚪⚪⚪⚪⚪  

<img fetchpriority="high" decoding="async" aria-describedby="caption-attachment-121484" class="size-medium wp-image-121484" src="/images/posts/2025/11/micrometer-prometheus-in-spring-boot-kafka-burger-orders/spring-init-1-700x404.png" alt="Spring initializer for Micrometer &amp; Prometheus in Spring Boot: Kafka Burger Orders" width="700" height="404">

Spring initializer for Micrometer \& Prometheus in Spring Boot: Kafka Burger Orders{#caption-attachment-121484}

1) Expose a Counter with Tags (Micrometer) {#h2-0-1-expose-a-counter-with-tags-micrometer}
------------------------------------------------------------------------------------------

<pre class="EnlighterJSRAW" style="position: relative" data-enlighter-language="java">package com.vv.burger.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter burgerOrderCounter(MeterRegistry registry) {
        // Common tags for the burger app
        Tags tags = Tags.of(
                Tag.of("app", "burger-service"),
                Tag.of("topic", "burger.orders")
                           );

        return Counter.builder("events_DukeBurger_total")
                      .description("Count of DukeBurger order events processed")
                      .baseUnit("orders")
                      .tags(tags)
                      .register(registry);
    }
}</pre>

*Side note:* We add consistent tags now (app, topic) so you can filter and graph later. [See](https://docs.spring.io/spring-boot/reference/actuator/metrics.html)

*** ** * ** ***

🔵🔵🔵🔵🔵🔵⚪⚪⚪⚪⚪⚪

2) REST Controller → Produce to Kafka {#h2-1-2-rest-controller-produce-to-kafka}
--------------------------------------------------------------------------------

<pre class="EnlighterJSRAW" style="position: relative" data-enlighter-language="java">package com.vv.burger.controller;

import com.vv.burger.BurgerOrder;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping( "/orders" )
public class OrderController {

    private final KafkaTemplate&lt;String, BurgerOrder&gt; kafkaTemplate;
    private final String                             topic;

    public OrderController( KafkaTemplate&lt;String, BurgerOrder&gt; kafkaTemplate,
                            @Value( "${app.kafka.topic}" ) String topic ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @PostMapping
    public String sendOrder( @RequestParam String burger ) {
        // 1. Build the Avro payload (BurgerOrder must be a generated Avro class)
        BurgerOrder order = BurgerOrder.newBuilder()
                                       .setBurger( burger )
                                       .setTimestamp( OffsetDateTime.now()
                                                                    .toString() )
                                       .build();

        // 2. Create CloudEvent metadata as headers
        String id = UUID.randomUUID()
                        .toString();
        OffsetDateTime now = OffsetDateTime.now();

        ProducerRecord&lt;String, BurgerOrder&gt; record = new ProducerRecord&lt;&gt;( topic, order );
        record.headers()
              .add( "ce_id", id.getBytes( StandardCharsets.UTF_8 ) );
        record.headers()
              .add( "ce_type", "BurgerOrder".getBytes( StandardCharsets.UTF_8 ) );
        record.headers()
              .add( "ce_source", "http://localhost/orders".getBytes( StandardCharsets.UTF_8 ) );
        record.headers()
              .add( "ce_specversion", "1.0".getBytes( StandardCharsets.UTF_8 ) );
        record.headers()
              .add( "ce_time", now.toString()
                                  .getBytes( StandardCharsets.UTF_8 ) );
        record.headers()
              .add( "ce_subject", "order".getBytes( StandardCharsets.UTF_8 ) );
        record.headers()
              .add( "ce_datacontenttype", "application/avro".getBytes( StandardCharsets.UTF_8 ) );

        // 3. Send the record
        kafkaTemplate.send( record );

        return "✅ Order sent to Kafka: " + burger;
    }
}</pre>

*Side note:* The headers mimic **CloudEvents** so you can plug into event tooling later. This is optional for the metric. [Cloud Events](https://cloudevents.github.io/sdk-java/)

*** ** * ** ***

🔵🔵🔵🔵🔵🔵🔵⚪⚪⚪⚪⚪

3) Kafka Consumer → Count "DukeBurger" {#h2-2-3-kafka-consumer-count-dukeburger}
--------------------------------------------------------------------------------

<pre class="EnlighterJSRAW" style="position: relative" data-enlighter-language="java">package com.vv.burger.consumer;

import com.vv.burger.BurgerOrder;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.micrometer.core.instrument.Counter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Map;

@Component
public class ConsumerApp {

    // Injects the Counter bean defined in MetricsConfig (events_DukeBurger_total)
    private final Counter burgerOrderCounter;

    public ConsumerApp( final Counter burgerOrderCounter ) {
        this.burgerOrderCounter = burgerOrderCounter;
    }

    @KafkaListener( topics = "burger.orders",
                    groupId = "group1" )
    public void receive( ConsumerRecord&lt;String, BurgerOrder&gt; record ) {
        BurgerOrder order = record.value();

        // Optionally reconstruct CloudEvent from headers
        CloudEvent cloudEvent = CloudEventBuilder.v1()
                                                 .withId( getHeader( record, "ce_id" ) )
                                                 .withType( getHeader( record, "ce_type" ) )
                                                 .withSource( URI.create( getHeader( record, "ce_source" ) ) )
                                                 .withSubject( getHeader( record, "ce_subject" ) )
                                                 .withTime( OffsetDateTime.parse( getHeader( record, "ce_time" ) ) )
                                                 .withDataContentType( getHeader( record, "ce_datacontenttype" ) )
                                                 .withData( "application/avro", order.toString()
                                                                                     .getBytes() ) // optional
                                                 .build();

        System.out.println( "📥 Received order: " + order.getBurger() + " at " + order.getTimestamp() );
        System.out.println( "🧾 CloudEvent type: " + cloudEvent.getType() + ", id: " + cloudEvent.getId() );

        if ( isDukeBurger( order ) ) {
            burgerOrderCounter.increment();
        }
    }

    private boolean isDukeBurger( final BurgerOrder order ) {
        return "DukeBurger".equals( order.getBurger()
                                         .toString() );
    }

    private String getHeader( ConsumerRecord&lt;?, ?&gt; record, String key ) {
        return new String( record.headers()
                                 .lastHeader( key )
                                 .value() );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory&lt;String, BurgerOrder&gt; kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory&lt;String, BurgerOrder&gt; factory =
                new ConcurrentKafkaListenerContainerFactory&lt;&gt;();
        factory.setConsumerFactory( consumerFactory() );
        return factory;
    }

    public ConsumerFactory&lt;String, BurgerOrder&gt; consumerFactory() {
        Map&lt;String, Object&gt; props = Map.of(
                "bootstrap.servers", "kafka:9092",
                "group.id", "group1",
                "key.deserializer", StringDeserializer.class.getName(),
                "value.deserializer", io.confluent.kafka.serializers.KafkaAvroDeserializer.class.getName(),
                "schema.registry.url", "http://schema-registry:8081",
                "specific.avro.reader", true
                                          );

        return new org.springframework.kafka.core.DefaultKafkaConsumerFactory&lt;&gt;( props );
    }
}</pre>

*Side note:* `@KafkaListener` binds the method to the topic with minimal boilerplate. Keep consumer config small for a first run. [See](https://docs.spring.io/spring-kafka/reference/kafka/receiving-messages/listener-annotation.html)

*** ** * ** ***

🔵🔵🔵🔵🔵🔵🔵🔵⚪⚪⚪⚪

4) Avro Bytes → Object (utility) {#h2-3-4-avro-bytes-object-utility}
--------------------------------------------------------------------

<pre class="EnlighterJSRAW" style="position: relative" data-enlighter-language="java">package com.vv.burger.consumer;

import com.vv.burger.BurgerOrder;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;

import java.io.ByteArrayInputStream;

public class AvroUtils {
    public static BurgerOrder fromBytes( byte[] bytes ) {
        try ( ByteArrayInputStream in = new ByteArrayInputStream( bytes ) ) {
            SpecificDatumReader&lt;BurgerOrder&gt; reader = new SpecificDatumReader&lt;&gt;( BurgerOrder.class );
            BinaryDecoder decoder = DecoderFactory.get()
                                                  .binaryDecoder( in, null );
            return reader.read( null, decoder );
        } catch ( Exception e ) {
            throw new RuntimeException( "Failed to deserialize BurgerOrder Avro event", e );
        }
    }
}</pre>

*Side note:* Spring Kafka + Confluent deserializer already returns `BurgerOrder`, so you rarely need this. It's useful in tests or when you manually handle bytes.

*** ** * ** ***

🔵🔵🔵🔵🔵🔵🔵🔵🔵⚪⚪⚪

<pre class="EnlighterJSRAW" style="position: relative" data-enlighter-language="yaml">spring:
  application:
    name: burger-service

# Kafka
app:
  kafka:
    topic: burger.orders

# Actuator + Micrometer Prometheus
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always</pre>

*Side note:* This exposes `/actuator/prometheus` so Prometheus can scrape. [See](https://docs.spring.io/spring-boot/reference/actuator/metrics.html)

*** ** * ** ***

🔵🔵🔵🔵🔵🔵🔵🔵🔵🔵⚪⚪

1. **Build the image** : `docker build -t my-spring-boot-app:latest .`
2. **Run the app** : `docker-compose up -d`
3. **Create the topic:** <http://localhost:8080/ui/clusters/local/all-topics/create-new-topic> named `burger.orders`
4. **Send a few orders:**
   * `curl -X POST "http://localhost:8080/orders?burger=DukeBurger`
   * `curl -X POST "http://localhost:8080/orders?burger=Veggie`
   * `curl -X POST "http://localhost:8080/orders?burger=DukeBurger`
5. **Check metrics:** open `http://localhost:8080/actuator/prometheus` and search for `events_DukeBurger_total`. You should see it increase after each "DukeBurger" consumed.
6. **Check in JMC:** Connect [JMC](https://www.oracle.com/java/technologies/javase/products-jmc9-downloads.html) to your app and In JMC; open **MBean Browser** (left pane); Expand the **`metric `; Navigate to the counter** `events_DukeBurger_total`; Click it → **Attributes** tab → read **`Count`;** You should see it increase after each "DukeBurger" consumed.

<img decoding="async" aria-describedby="caption-attachment-121487" class="size-medium wp-image-121487" src="/images/posts/2025/11/micrometer-prometheus-in-spring-boot-kafka-burger-orders/jmc-1-700x350.png" alt="JMC for Micrometer &amp; Prometheus in Spring Boot: Kafka Burger Orders" width="700" height="350">

JMC for Micrometer \& Prometheus in Spring Boot: Kafka Burger Orders{#caption-attachment-121487}

*** ** * ** ***

🔵🔵🔵🔵🔵🔵🔵🔵🔵🔵🔵⚪

* **Small steps win:** REST → Kafka → Consumer → Metric is a powerful, simple pipeline.
* **Micrometer first:** Write metrics once; swap backends later (Prometheus today, Datadog tomorrow). [See](https://docs.spring.io/spring-boot/reference/actuator/metrics.html)
* **Tags matter:** Add `app` and `topic` tags now. Your future dashboards will thank you.
* **Avro stays lean:** A tiny schema keeps payloads small and generated classes easy to use.
* **CloudEvents optional:** The headers help interoperability but are not required for Micrometer. [Cloud Events](https://cloudevents.github.io/sdk-java/)

*** ** * ** ***

🔵🔵🔵🔵🔵🔵🔵🔵🔵🔵🔵🔵

You just wired **Micrometer and Prometheus in Spring Boot** around a Kafka flow and produced a clean, tagged counter you can graph and alert on. From here, extend the metric set (timers for latency, gauges for queue depth), add dashboards, and create an alert when `events_DukeBurger_total` stalls or spikes.

*** ** * ** ***

* Spring Boot + Kafka Streams testing \& routing:  
  <https://foojay.io/today/spring-boot-kafka-streams-event-routing-testing/>
* Make Spring Batch fly with native image \& GraalVM:  
  <https://foojay.io/today/speed-up-your-spring-batch-with-native-image-and-graalvm/>

*** ** * ** ***

* **Java OCP prep (Udemy):**   
  <https://www.udemy.com/course/ocp-oracle-certified-professional-java-developer-prep/?referralCode=54114F9AD41F127CB99A>
* **Spring Professional -- 6 full tests (Udemy):**   
  <https://www.udemy.com/course/spring-professional-certification-6-full-tests-2v0-7222-a/?referralCode=04B6ED315B27753236AC>
* **Spring Certification Book (Leanpub/ Kindle/ Paperback):**   
  <https://spring-book.mystrikingly.com/>

*** ** * ** ***

References {#h2-4-references}
-----------------------------

* Micrometer docs and Prometheus registry overview. [Micrometer Application Observability+1](https://micrometer.io/docs/)
* Spring Boot Actuator metrics. [See](https://docs.spring.io/spring-boot/reference/actuator/metrics.html)
* `@KafkaListener` reference. [See](https://docs.spring.io/spring-kafka/reference/kafka/receiving-messages/listener-annotation.html)
* CloudEvents Java SDK. [Cloud Events](https://cloudevents.github.io/sdk-java/)
