---
title: "Starting Apache Kafka on Java 8, Win 10"
slug: "starting-apache-kafka-java-producer-consumer-windows-10"
date: "2023-08-07T09:39:39+00:00"
lastmod: "2023-08-07T09:40:48+00:00"
description: "Learn how to configure Kafka topics and create a Java-based Kafka Consumer and Producer, withApache Kafka v3.4 on Windows 10."
authors:
  - "sumith-puri"
image: "techila_shots_kafka_java_001.png"
categories:
  - "Kafka"
  - "Tutorials"
tags:
related_posts:
  - "starting-apache-kafka-on-windows-10-kafka-v3-4"
  - "clean-shutdown-of-spring-boot-applications"
  - "getting-more-mileage-out-of-kafka-openjdk-vs-azul-prime"
  - "micrometer-prometheus-in-spring-boot-kafka-burger-orders"
enlighterjs: true
frozen: false
---

This is part 2 of a two part series on Starting Apache Kafka Server, Configuring Kafka Topic, and Creating a Core Java Based Kafka Consumer, as also a Core Java Based Kafka Producer.

All this is demonstrated in step-by-step examples. All of this is for Java v8.0, Apache Kafka v3.4 on Windows 10. [Part 1](https://foojay.io/today/starting-apache-kafka-on-windows-10-kafka-v3-4/) focused on Kafka Consumer and Kafka Producer from the Command Line. This article focuses on the Core Java counterparts. It is important that the reader reads part 1 and completes the example so that the reader has a basic foundation in Kafka.

This article also provides the Maven Dependencies required to create, build and run Apache Kafka Consumers and Producers. Finally, It shows the code sample to run the Consumer and Producer Thread. The code sample is straightforward for an Intermediate+ Java Developer, hence the explanations is omitted for the code.

#### Pre-Requisites

1. Install Java//JRE (v8.0 is used in this Example)  

2. Install Apache Kafka 3.4.0 from the Given Link  

3. Set Java Classpath\> Set JAVA_HOME Correctly  

4. UnZIP/UnTAR Apache Kafka Downloaded in (2)  

5. Use a Text Editor like \[Notepad++\] for Editing  

6. Eclipse IDE (Or Others) to Create, Run \& Test

Before you begin, [read the first article in this series](https://foojay.io/today/starting-apache-kafka-on-windows-10-kafka-v3-4/).

![](https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEi2nkv25xSy_NUgdrbgmJi7wrcI1qvNHmd4nRDvGoMId_oMFjarwE89SV6KTDeaQ8lbjH5UPb40HGWwN2Cy8I5Cgv7PpI5bRhQDXrivE1tLbHpsiBrEmdfufTABWrRCdmQUMzHM5PITnglwHwrEmu7qLNFbjE9e6SPoxAFVYYvBKr1n2wMF0Oe7zUKbJoDi/w640-h360/techila_shots_kafka_java_001.png)

#### Maven Project (Eclipse) and Dependencies

Create a Simple JAR archetype Maven Project in Eclipse (Or IDE of Your Choice). Add the following dependencies in your pom.xml. Make Sure that your Compiler Version is Java 8.0.

```
<dependencies>  
         <!-- This is the Core Library Containing the Classes We will Use -->  
         <dependency>  
             <groupId>org.apache.kafka</groupId>  
             <artifactId>kafka-clients</artifactId>  
             <version>3.2.1</version>  
         </dependency>  
         <!-- The Kafka Client Libraries use the slf4j Logger, So we Need to Add   
             This as a Dependency so that the Required Classes are Present in Our   
             Classpath for the Kafka Client Libraries to Use -->  
         <dependency>  
             <groupId>org.slf4j</groupId>  
             <artifactId>slf4j-api</artifactId>  
             <version>1.7.36</version>  
         </dependency>  
 </dependencies>
```

#### Developing the Java Producer

1. Make Sure you know the Topic Name  

2. Find out Your Bootstrap - Server Port  

3. Refer Javadoc for KafkaProducer Obj.  

4. Also, for Producer \& ProducerRecord

#### Code for Java Producer (Tested on Kafka v3.4 on Windows 10)

```
/**   
  *    Author @sumith.puri (Addl. Ref: https://www.sohamkamani.com/java/kafka/)  
  */   
 package com.kafka.poc.producer;  

 import java.util.Properties;  

 import org.apache.kafka.clients.producer.KafkaProducer;  
 import org.apache.kafka.clients.producer.Producer;  
 import org.apache.kafka.clients.producer.ProducerRecord;  

 public class KafkaPoCProducer implements Runnable {  

     private static final String TOPIC = "test";  
     private static final String BOOTSTRAP_SERVERS = "localhost:9092";  

     @Override  
     public void run() {  

         produce();  
     }  

     private void produce() {  

         // Create Configuration Options for our Producer and Initialize a New Producer  
         Properties props = new Properties();  
         props.put("bootstrap.servers", BOOTSTRAP_SERVERS);  

         // We Configure the Serializer to Describe the Format in which we Want To  
         // Produce Data into our Kafka Cluster  
         props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");  
         props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");  

         // Since we Need to Close our Producer, We can use the try-with-resources  
         // Statement to create a New Producer  
         try (Producer<String, String> producer = new KafkaProducer<>(props)) {  

             // Here, We Run an Infinite Loop to Send a Message to the Cluster Every Second  
             for (int i = 0;; i++) {  
                 String key = Integer.toString(i);  
                 String message = "Watson, Please Come Over Here " + Integer.toString(i);  

                 producer.send(new ProducerRecord<String, String>(TOPIC, key, message));  

                 // Log a Confirmation Once The Message is Written  
                 System.out.println("Sent Message " + key);  
                 try {  
                     // Sleep for a Second  
                     Thread.sleep(1000);  
                 } catch (Exception e) {  
                     break;  
                 }  
             }  
         } catch (Exception e) {  
             System.out.println("Could not Start Producer Due To: " + e);  
         }  
     }  
 }
```

#### Developing the Java Consumer

1. Make Sure you know the Topic Name  

2. Find out Your Bootstrap - Server Port  

3. Refer Javadoc \> KafkaConsumer Obj.  

4. Also, for Consumer, ConsumerRecord  

5. Refer AutoCommit/Acknowledgement

#### Code for Java Consumer (Tested on Kafka v3.4 on Windows 10)

```
/**   
  *    Author @sumith.puri (Addl. Ref: https://www.sohamkamani.com/java/kafka/)  
  */   
 package com.kafka.poc.consumer;  

 import java.time.Duration;  
 import java.util.Arrays;  
 import java.util.Properties;  

 import org.apache.kafka.clients.consumer.ConsumerRecord;  
 import org.apache.kafka.clients.consumer.ConsumerRecords;  
 import org.apache.kafka.clients.consumer.KafkaConsumer;  

 public class KafkaPoCConsumer implements Runnable {  

     private static final String TOPIC = "test";  
     private static final String BOOTSTRAP_SERVERS = "localhost:9092";  

     @Override  
     public void run() {  

         consume();  
     }  

     private void consume() {  

         // Create Configuration Options for our Consumer  
         Properties props = new Properties();  

         props.setProperty("bootstrap.servers", BOOTSTRAP_SERVERS);  
         // The Group ID is a Unique Identified for Each Consumer Group  
         props.setProperty("group.id", "my-group-id");  

         // Since our Producer uses a String Serializer, We need to use the Corresponding  
         // Deserializer  
         props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");  
         props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");  

         // Every Time We Consume a Message from kafka, We Need to "commit", That Is,  
         // Acknowledge Receipts of the Messages.... We Can Set up an Auto-Commit at  
         // Regular intervals, so that this is Taken Care of in the Background  
         props.setProperty("enable.auto.commit", "true");  
         props.setProperty("auto.commit.interval.ms", "1000");  

         // Since We Need to Close our Consumer, We can Use the try-with-resources  
         // Statement to Create It  
         try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {  

             // Subscribe this Consumer to the Same Topic that we Wrote Messages to Earlier  
             consumer.subscribe(Arrays.asList(TOPIC));  

             // Run an Infinite Loop where we Consume and Print New Messages to the Topic  
             while (true) {  

                 // The consumer.poll Method Checks and Waits..For Any New Messages To Arrive For  
                 // The Subscribed Topic in case there are No Messages for the Duration Specified  
                 // In the Argument (1000 ms In this Case), It returns an Empty List  
                 ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));  
                 for (ConsumerRecord<String, String> record : records) {  
                     System.out.printf("Received Message: %s\n", record.value());  
                 }  
             }  
         }  
     }  

 }
```

#### Create the Java Application to Demo Kafka Producer \& Consumer

```
/**   
  *    Author @sumith.puri (Addl. Ref: https://www.sohamkamani.com/java/kafka/)  
  */   
 package com.kafka.poc.app;  

 import com.kafka.poc.consumer.KafkaPoCConsumer;  
 import com.kafka.poc.producer.KafkaPoCProducer;  

 public class KafkaPoCApp {  

     public static void main(String[] args) {  

         Thread cThread = new Thread(new KafkaPoCConsumer());  
         cThread.start();  

         Thread pThread = new Thread(new KafkaPoCProducer());  
         pThread.start();  

     }  
 }
```

Run the Above Application in your IDE or Command-Line.

#### Typical Output from Running the Kafka Producer Consumer PoC

![](https://blogger.googleusercontent.com/img/a/AVvXsEglbg9GBBUrvZY1epMjYK2RUmaw-bhLSgjQ4aRyZ25CnVPULdIu0OfxFSg9xqgKoy2SMEQHzGrTpLzNn3xSrjNSDDRwGOgxJKL6qlj9yeoYG07r2gp58e8P5si6-BrB0KA2l7j0kaTDfQiOcu4qBakQe7UScmejLrF5DXwtnS_1hqmq_JE8xpcIZ-CZR7rh=s16000)

The next article in this series will be the Spring Boot Kafka Consumer Producer Integration that is planned to be used in a real world Product.

It will be used for Asynchronous Notification Service and will also provide further information on Failure Recovery, Acknowledgment, Ordering, Scalability, Fault Tolerance of Apache Kafka for Enterprise Software Development Needs.
