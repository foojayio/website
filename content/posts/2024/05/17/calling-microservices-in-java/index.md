---
title: "Calling Microservices in Java: Part 1"
slug: "calling-microservices-in-java"
date: "2024-05-17T19:41:44+00:00"
lastmod: "2024-05-17T19:48:22+00:00"
description: "This article explores various approaches to calling microservices in Java, from simple RESTful APIs to advanced. Learn more!"
authors:
  - "abo-saad-muaath"
image: "https://foojay.io/wp-content/uploads/2024/05/Screenshot-1445-10-05-at-7.23.37 AM.png"
categories:
  - "Java"
  - "Microservices"
tags:
related_posts:
  - "automatically-creating-microservices-architecture-diagrams"
  - "book-review-monolith-to-microservices-part-1"
  - "book-review-quarkus-for-spring-developers"
  - "intro-to-the-boxlang-formatter"
enlighterjs: true
frozen: false
---

**When building applications that need to call other parts of the system ([microservices](https://mezocode.com/microservices-soa-introduction-part1/ "microservices")), Java programmers have access to a variety of tools and techniques that they can use for their development tasks.**

This article guides you through different ways of calling Microservices in Java, from basic methods to more advanced ones, and will share tips on best practices and common mistakes.

### 1. Basic HTTP Requests with HttpURLConnection

The simplest way to calling a microservice in Java is using the `HttpURLConnection` class. This lets your application send HTTP requests and receive responses directly.

**Example**:

```
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SimpleServiceCaller {
    public static String callService(String serviceUrl) throws Exception {
        URL url = new URL(serviceUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString();
        } finally {
            con.disconnect();
        }
    }
}
```

**Pros:**

* It's simple and doesn't need extra software libraries.
* Good for smaller applications or when you don't call services often.

**Cons:**

* It lacks features like connection reuse, automatic retries, and error handling.
* Managing HTTP connections manually can lead to mistakes and messy code.

### 2. Using Apache HttpClient

Apache HttpClient is a more robust tool for making HTTP calls.

It supports many advanced features like connection pooling, which means it can handle many requests efficiently, authentication, and handling proxies.

**Example:**

```
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

public class ApacheHttpClientExample {
    public static String callService(String serviceUrl) throws Exception {

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(serviceUrl);
            return client.execute(request, httpResponse -> 
                EntityUtils.toString(httpResponse.getEntity()));
        }
    }
}
```

**Pros:**

* Handles complex HTTP interactions well.
* Connection pooling improves performance when dealing with many requests.

**Cons:**

* Adds complexity and requires additional library dependencies.
* Still needs manual setup for error handling and resilience.

### 3. Using Spring's RestTemplate and WebClient

For applications built with Spring, `RestTemplate` and `WebClient` are convenient options.

**Example with [RestTemplate](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html "RestTemplate"):**

```
import org.springframework.web.client.RestTemplate;

public class RestTemplateExample {
    private static final RestTemplate restTemplate = new RestTemplate();

    public static String callService(String serviceUrl) {
        return restTemplate.getForObject(serviceUrl, String.class);
    }
}
```

**Example with WebClient:**

```
import org.springframework.web.reactive.function.client.WebClient;

public class WebClientExample {
    private static final WebClient webClient = WebClient.create();

    public static String callService(String serviceUrl) {
        return webClient.get()
                        .uri(serviceUrl)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();  // Waits for the response
    }
}
```

**Pros:**

* Easy to integrate and use within Spring applications.
* `WebClient` supports modern, reactive programming.

**Cons:**

* Tied to Spring, so not suitable if you're not using this framework.
* Spring is moving away from `RestTemplate` to favor `WebClient`.

### 4. Using Feign for Declarative REST Clients

Feign is a tool that lets you write very simple code to make HTTP calls by defining interfaces.

**Example:**

```
@FeignClient(name = "serviceClient", url = "https://jsonplaceholder.typicode.com/todos/1")
public interface ServiceClient {

    @GetMapping("/service")
    String callService();
}
```

**Pros:**

* Simplifies code with its declarative style.
* Integrates well with Spring and other modern Java tools.

**Cons:**

* Less control over the details of HTTP requests.
* Requires adding Feign libraries to your project.

### Best Practices and Common Mistakes

* **Use connection pooling**: It's better for performance.
* **Implement error handling**: Use retries, timeouts, and circuit breakers to make your application more reliable.
* Choose well-supported libraries like Apache HttpClient or WebClient instead of managing HTTP connections yourself.
* **Keep an eye on your HTTP traffic**: Logging requests and responses help in debugging issues.
* **Secure your HTTP requests**: Use HTTPS and proper authentication methods.

### Conclusion

Choosing the right method to connect to microservices in Java depends on your specific needs, such as how big your application is and whether you are using Spring Framework.

While simple methods are fine for small tasks or when you don't need to call services often, more advanced techniques offer better performance and easier maintenance as your application grows.

Follow best practices to ensure your application is robust and performs well.

Happy coding!
