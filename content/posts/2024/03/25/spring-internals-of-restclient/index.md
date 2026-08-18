---
title: "Spring Framework – Internals of RestClient"
date: "2024-03-25T11:32:54+00:00"
lastmod: "2024-03-31T01:37:12+00:00"
description: "Experience the ease and efficiency of Spring Framework RestClient. Simplify your RESTful API integration and unlock the full potential of your applications."
authors:
  - "mahendra1413"
image: "RestClient_1-700x394-1.jpg"
categories:
  - "Java"
  - "Microservices"
  - "reactive"
  - "Spring"
tags:
related_posts:
  - "spring-6-1-restclient"
  - "how-to-improve-your-spring-boot-skills"
  - "creating-a-simple-spring-boot-application-in-intellij-idea"
frozen: false
---

**As a developer and architect, my constant pursuit is to achieve simplicity and elegance when constructing resilient and intricate enterprise applications. With my affinity for the Spring Framework, I have witnessed firsthand the simplicity and modernization it brings to the Spring Ecosystem.**

This framework enables the creation of complex enterprise applications in a more streamlined and refined manner, boasting a sophisticated diffusion and transformer architecture.

{{< img src="RestClient_1-700x394.jpg" class="size-medium" alt="Spring RestClient" width="700" height="394" >}}

Spring RestClient

[Spring Boot 3.2](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.2-Release-Notes#restclient-support "Spring Boot 3.2") launched and introduced a range of captivating functionalities. Notably, the release includes the RestClient feature, which offers a contemporary approach to developing REST endpoints.

Before implementing the RestClient feature, we had several options available for creating REST endpoints, namely:

1. **RestTemplate** facilitated the creation of APIs for synchronous clients using the template driven approach.
2. **WebClient** aided in developing APIs for non-blocking, reactive clients through a fluent API.
3. **HTTP Interface** offered a more detailed approach by utilizing interface-based and dynamic proxy implementation.

But

**Why we use RestClient?**

**RestTemplate** provides numerous overriding methods for each of the HTTP methods, which could be overwhelming at times.

On the other hand, **WebClient** offered a more versatile solution as it supported both synchronous and asynchronous, non-blocking operations with a fluent API. However, even if we were not using it for reactive clients, we still needed to include the web-flux dependency, which became redundant for synchronous calls.

The same applied to the **HTTP Interface** as well.

**What is RestClient?**

**RestClient** provides a sophisticated abstraction layer that is based on the infrastructure of RestTemplate. This layer streamlines the procedure of sending HTTP requests by offering a more user-friendly fluent API and minimizing redundant code.

You can utilize RestClient in various ways namely,

1. You can employ static **create** methods as one approach.

```
     RestClient defaultClient = RestClient.create();
                        (OR)
     var defaultClient = RestClient.create();
```

2. You can also utilize the **builder pattern** , which allows for additional customization. This includes specifying the **HTTP library, message converters, setting the default URI, path variables, request headers, UriBuilderFactory, as well as registering interceptors and initializers**.

```
RestClient customRestClient = RestClient.builder()
  .requestFactory(new SimpleClientHttpRequestFactory()) (1)
  .messageConverters(converters -> converters.add(new MappingJackson2HttpMessageConverter())) (2)
  .baseUrl("https://www.bsmlabs.com") (3)
  .defaultUriVariables(Map.of("article", "restclient")) (4)
  .defaultHeader("client_id", "springrestclient") (5)
  .requestInterceptor(myCustomInterceptor) (6)
  .requestInitializer(myCustomInitializer) (7)
  .build();
```

Let's analyze what each line does:

1. `RestClient.builder()` : This method utilizes the builder pattern to initiate the construction of a new RestClient instance. It allows for configuring the client in a fluent and readable way.
2. `.requestFactory(new SimpleClientHttpRequestFactory())`: This line of code establishes the request factory for the RestClient. In this instance, it uses `SimpleClientHttpRequestFactory`, a fundamental implementation that relies on standard JDK classes to generate HTTP requests. The primary function of this factory is to create HTTP request objects.
3. `.messageConverters(converters -> converters.add(new MappingJackson2HttpMessageConverter())):` We are configuring the RestClient to utilize message converters. These converters convert the bodies of HTTP requests and responses into Java objects. Specifically, we are adding a `MappingJackson2HttpMessageConverter`, which is commonly used to convert JSON data to and from Java objects using the Jackson library.
4. `.baseUrl("https://www.bsmlabs.com")`: This line establishes the base URL for the RestClient, ensuring that all requests made using this client will be relative to this specified URL.
5. `.defaultUriVariables(Map.of("article", "restclient"))`: The RestClient configures default URI variables, which act as placeholders in the URL path and can substitute actual values during request execution. In this case, the variable named "article" is assigned a default value of "restclient".
6. `.defaultHeader("client_id", "springrestclient")`: In this instance, the RestClient sets a default header for all requests, which is a header named "client_id" with the value "springrestclient".
7. `.requestInterceptor(myCustomInterceptor)`: The request interceptor configures the RestClient, enabling the modification of outgoing requests before executing them. We assume that myCustomInterceptor is an implementation of the ClientHttpRequestInterceptor interface.
8. `.requestInitializer(myCustomInitializer)`: The request initializer configures the RestClient and is responsible for performing any necessary initialization of the request before executing it. We assume that myCustomInitializer is an implementation of the RestTemplateRequestInitializer interface.
9. `.build()`: The builder has completed and created the RestClient instance containing all the specified configurations.

In this article, we will connect to retrieve data on universities by providing the country name as an input parameter. And the base Url is <http://universities.hipolabs.com/search>

**Step:1** We have used the following dependencies in **pom.xml**

1. ***spring-boot-starter-web***
2. ***springdoc-openapi-starter-webmvc-api***
3. ***httpclient5***

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.3</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.bsmlabs</groupId>
    <artifactId>spring-rest-client-example</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>spring-rest-client-example</name>
    <description>Demo project for Spring Boot</description>
    <properties>
        <java.version>17</java.version>
        <springdoc-openapi.version>2.3.0</springdoc-openapi.version>
        <httpclient5.version>5.2.1</httpclient5.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-api</artifactId>
            <version>${springdoc-openapi.version}</version>
        </dependency>

        <dependency>
            <groupId>org.apache.httpcomponents.client5</groupId>
            <artifactId>httpclient5</artifactId>
            <version>${httpclient5.version}</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

**Step:2**The RestClient configuration in the spring boot project is as follows.

```
package com.bsmlabs.restclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${universityEndpointUrl}")
    String baseUri;

    /**
     * using RestClient static create method
     */
    @Bean
    RestClient restClient() {
        return RestClient.create(baseUri);
    }

    /**
     * Using RestClient Builder Pattern
     */
    @Bean(name = "builderRestClient")
    RestClient restClientBuilder() {
        return RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .baseUrl(baseUri)
                .build();
    }

}
```

**Step:3** Create Response class as follows using **Record** feature

```
package com.bsmlabs.restclient;

import java.util.List;

public record UniversityDataResponse(String alpha_two_code,
                                     List<String> web_pages,
                                     String state_province,
                                     String name,
                                     List<String> domains,
                                     String country) {
}
```

**Step:4**Create UniversityDataService and its Implementation class

```java
package com.bsmlabs.restclient;

import java.util.List;

public interface UniversityDataService {
    List<UniversityDataResponse> getUniversityDetails(String countryName);

    List<UniversityDataResponse> getUniversityDataWithBuilder(String countryName);
}
```

```java
package com.bsmlabs.restclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class DefaultUniversityDataService implements UniversityDataService {

    @Value("${universityEndpointUrl}")
    String baseUri;

    private final RestClientConfig restClientConfig;

    public DefaultUniversityDataService(RestClientConfig restClientConfig) {
        this.restClientConfig = restClientConfig;
    }

    /**
     * using RestClient static create method
     */
    @Override
    public List<UniversityDataResponse> getUniversityDetails(String countryName) {
        var uri = UriComponentsBuilder.fromHttpUrl(baseUri)
                .queryParam("country", countryName)
                .build()
                .toUri();

        return restClientConfig.restClient().get()
                .uri(uri)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> System.out.println(response.getStatusText()))
                .body(new ParameterizedTypeReference<>() {
                });

    }

    /**
     * Using RestClient Builder Pattern
     */
    @Override
    public List<UniversityDataResponse> getUniversityDataWithBuilder(String countryName) {
        var uri = UriComponentsBuilder.fromHttpUrl(baseUri)
                .queryParam("country", countryName)
                .build()
                .toUri();
        return restClientConfig.restClientBuilder().get()
                .uri(uri)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> System.out.println(response.getStatusText()))
                .body(new ParameterizedTypeReference<>() {
                });
    }
}
```

**Step:5** Run the application and access the URL and it will fetch you the university data based on the country

***http://localhost:8080/api/universities/united%20kingdom***

The `get()` operation is specifically for this; we will execute the `post()`, `put()`, and `delete()` operations in a similar manner.

Using builder pattern, we have a request factories which can be set based on the use case, here are different types of **Client Request Factories** available

1. For Java's HttpClient, we can use `JdkClientHttpRequestFactory`
2. For Apache Http Components `HttpClient`, we can `HttpComponentsClientHttpRequestFactory`
3. For Jetty's HttpClient, we can use `JettyClientHttpRequestFactory`
4. For Reactor Netty's `HttpClient`, we can use `ReactorNettyClientRequestFactory`
5. As a simple default, we can use `SimpleClientHttpRequestFactory`
6. If we do not explicitly specify, the `HttpClient` will default to `Apache or Jetty`, provided that they are present in the classpath.

We are also using the `message converters` which are available [here](https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#rest-message-conversion)

We can also migrate from **RestTemplate** to **RestClient** using the following configuration

```
var restTemplate = new RestTemplate();
var response = RestClient.builder(restTemplate);
```

## Conclusion

`RestClient` is poised to replace `RestTemplate` as it provides a more intuitive and concise method for developing Restful Services, built on top of WebClient.

The complete code can be found [over on Github](https://github.com/bsmahi/spring-rest-client-example)

### References

<https://docs.spring.io/spring-framework/reference/integration/rest-clients.html>
