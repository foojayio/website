---
title: "Testing Spring Boot JMS with ActiveMQ Artemis and Testcontainers"
slug: "testing-spring-boot-jms-with-activemq-artemis-and-testcontainers"
date: "2023-12-12T07:55:03+00:00"
lastmod: "2023-12-12T07:55:05+00:00"
description: "Testcontainers is a fantastic way to start resources as containers. Even if there is no pre-made container, you can always use GenericContainer to run virtually any container image."
authors:
  - "simon-martinelli"
image: "https://foojay.io/wp-content/uploads/2023/12/testcontainer-artemis.png"
categories:
  - "JMS"
  - "Spring"
  - "Testcontainers"
  - "Testing"
tags:
related_posts:
enlighterjs: true
frozen: false
---

**Currently, I'm teaching JMS with Spring Boot at the University of Applied Science in Bern, Switzerland. We use [Apache ActiveMQ Artemis](https://activemq.apache.org/components/artemis/) as the JMS message broker. But how can we test our Spring Boot application?**

Testcontainers to the Rescue {#h2-0-testcontainers-to-the-rescue}
-----------------------------------------------------------------

Currently, there is no [Testcontainers Java](https://java.testcontainers.org%24/) module for ActiveMQ Artemis. As you can see in the [Testcontainers GitHub repository](https://github.com/testcontainers/testcontainers-java), there is an active activemq branch that may be released soon. But in the meantime, we need another solution.

There is [GenericContainer](https://java.testcontainers.org/features/creating_container/) that can be used for any container image:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Container
static GenericContainer&lt;?&gt; artemis = new GenericContainer&lt;&gt;(
              DockerImageName.parse("apache/activemq-artemis:latest-alpine"))
        .withEnv("ANONYMOUS_LOGIN", "true")
        .withExposedPorts(61616);</pre>

As you can see, we need some configuration. First, we use the official ActiveMQ Artemis image, which is available in [Docker Hub](https://hub.docker.com/). Then, we set ANONYMOUS_LOGIN to true. Otherwise, we must provide username and password, which is not needed just for testing. And finally, we must expose the default port to which we want to send our message.

Now, the Artemis JMS client needs to know the URL for the connection. We can use @DynamicPropertySource for that purpose and use the information from the Testcontainers container:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@DynamicPropertySource
static void artemisProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.artemis.broker-url", 
        () -&gt; "tcp://%s:%d".formatted(
              artemis.getHost(), artemis.getMappedPort(61616)));
}</pre>

Writing the Test {#h2-1-writing-the-test}
-----------------------------------------

Finally, everything is prepared, and we can write a test that sends and receives a message:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Autowired
private JmsTemplate jmsTemplate;

@Test
void sendMessage() throws JMSException {
    jmsTemplate.convertAndSend("testQueue", "Hello, JMS!");

    Message message = jmsTemplate.receive("testQueue");

    assertThat(message).isInstanceOf(TextMessage.class);
    TextMessage textMessage = (TextMessage) message;
    assertThat(textMessage.getText()).isEqualTo("Hello, JMS");
 }</pre>

Conclusion {#h2-2-conclusion}
-----------------------------

Testcontainers is a fantastic way to start resources as containers. Even if there is no pre-made container, you can always use GenericContainer to run virtually any container image.

Try it out yourself! The source code of the sample project is available on GitHub: <https://github.com/simasch/spring-boot-artemis-testcontainers>
