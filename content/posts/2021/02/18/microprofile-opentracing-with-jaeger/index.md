---
title: "Introduction to MicroProfile OpenTracing with Jaeger"
date: "2021-02-18T09:41:19+00:00"
lastmod: "2021-02-18T09:41:38+00:00"
description: "In this short video, Rudy de Busscher demonstrates how to use MicroProfile OpenTracing with Jaeger in combination with Payara Micro."
canonical: "https://blog.payara.fish/microprofile-opentracing-with-jaeger"
authors:
  - "jadon-ortlepp"
image: "Favicon-3-2.png"
categories:
  - "Microservices"
  - "Videos"
related_posts:
  - "get-recognized-for-your-cloud-native-java-development-skills-with-this-new-badge"
  - "creating-cloud-native-java-applications-with-the-12-factor-app-methodology"
  - "run-ai-enabled-jakarta-ee-and-microprofile-applications-with-langchain4j-and-open-liberty"
  - "how-to-bring-your-java-microservices-to-the-cloud"
frozen: false
---

In this short video, Rudy de Busscher demonstrates how to use MicroProfile OpenTracing with Jaeger in combination with Payara Micro.

[The MicroProfile OpenTracing](https://github.com/eclipse/microprofile-opentracing) specification defines behaviours and an API for accessing an OpenTracing compliant Tracer object within your JAX-RS application. The behaviours specify how incoming and outgoing requests will have OpenTracing Spans automatically created. The API defines how to explicitly disable or enable tracing for given endpoints.

[Jaeger](https://www.jaegertracing.io/), inspired by Dapper and OpenZipkin, is a distributed tracing system released as open source by Uber Technologies. It is used for monitoring and troubleshooting microservices-based distributed systems, including:

* Distributed context propagation
* Distributed transaction monitoring
* Root cause analysis Service dependency analysis
* Performance / latency optimization

{{< youtube nPuwrvaV8-U >}}
