---
title: "How to Detect Cache Misses Using Observability"
slug: "how-to-detect-cache-misses-using-observability"
date: "2024-04-04T07:44:31+00:00"
lastmod: "2024-04-04T07:49:33+00:00"
description: "In this article, we'll examine cache misses and, in general, learn about the caching concept and how to implement it in Spring Boot."
canonical: "https://digma.ai/how-to-detect-cache-misses-using-observability/"
authors:
  - "saeed-zarinfam"
image: "Spring-Digma.png"
categories:
  - "Developer Tools"
  - "Observability"
tags:
related_posts:
  - "beyond-pass-fail-a-modern-approach-to-java-integration-testing"
  - "boldness-in-refactoring"
  - "book-review-why-programs-fail"
  - "introducing-the-boxlang-spring-boot-starter-dynamic-jvm-templating-for-spring"
enlighterjs: true
frozen: false
---

**All of us know about caching in system design and software architecture, It is applicable everywhere in the computer industry, even in hardware. Caching is a quick and shortcut solution to improve performance, of course, we need to be careful of using the cache, misusing the cache can directly affect the system's consistency.**

In this article, we will learn about cache miss and, in general, about the caching concept and how to implement it in Spring Boot. Eventually, we will see how Digma can help us detect cache misses locally during development.

## The history of caching

The concept of caching can be traced back to the early days of computing when computer systems relied on hierarchical memory architectures. Early computers used different types of memory with varying access speeds, such as registers, caches, and main memory (RAM). Programmers often employ manual caching techniques to optimize performance by storing frequently accessed data in faster memory locations.

In the 1960s and 1970s, with the emergence of database management systems (DBMS), caching became a common technique for improving database performance. DBMSs implemented caching mechanisms to store frequently accessed data pages or query results in memory, reducing the need to retrieve data from disk storage, which was significantly slower.

The history of caching in software development reflects a continuous evolution driven by the need for efficient resource utilization, improved performance, and enhanced user experiences.

## Benefits of caching

When discussing caching, we usually think of situations where we have frequently accessed data that is expensive to compute. Caching mainly boosts availability and reliability, which are its key advantages.

### Key benefits of caching:

* Improved Performance
* Reliability and Availability
* Reduced Latency
* Scalability
* Enhanced User Experience
* Reduced Server Load
* Cost Saving

## What are caching, cache hits, cache evictions, and cache misses?

These are key concepts in caching, Before getting deep into the code, it's a good idea to get familiar with these concepts with a practical example.

### Let's examine cache concepts through a relatable scenario:

Imagine a time when there were no mobile phones and only home landlines were the only way to make phone calls. There was a phone book in every house, and we used to write down the important and frequently used phone numbers in it. Instead of asking others or searching in Yellow Pages, magazines, etc., we used those phone books to call.

In the screenshot below, caching, cache hits, cache evictions, and cache misses scenarios in the phonebook example.

![Caching, cache hits, cache evictions, and cache misses scenarios in the phonebook example.](https://digma.ai/wp-content/uploads/2024/03/image-2-1024x464.png "Caching, cache hits, cache evictions, and cache misses scenarios in the phonebook example.")

Every time we had a new phone number that we used a lot, we added it to the phone book. We had different ways to keep this phonebook updated, for example, if we called a number and realized that it had changed, we would delete that number, find a new number, and replace it. Let's explore cache-related concepts, including caching cache hits, evictions, and misses, through this imaginary example:

### Scenarios:

1. Cache Hit: If we search for a phone number in our phonebook and find it there (Cached), We call this a Cache Hit.
2. Cache Eviction: If, for any reason, we decide to delete a phone number from our phonebook, We call this Cache Eviction. Cache eviction can be due to many reasons, and it helps us to keep our phonebook updated and ensure we have the latest information.
3. Cache Miss: If we search for a phone number in our phonebook and can not find it there, we call this a Cache Miss.
4. Caching: We call a phone number several times, and each time, we need to search to find the number, which is time-consuming, so we decide to save this number to our phonebook so that we can quickly look up the number when we need it. In fact, the phonebook is our Cache, and the phone number and the person are a Cache Entry.

By exploring cache concepts through navigating a phonebook, including caching, cache hit, cache misses, and cache eviction, we can better understand how caching works in computer systems and the strategies used to optimize performance.

## Caching in Spring Boot

Spring Boot supplies caching support to make your application fast. Spring Boot caching is based on an Abstraction that can easily be enabled in a Spring Boot application. There is a starter package that can easily add to your project dependency to add caching support to your project.

```
<dependency>
 <groupId>org.springframework.boot</groupId>
 <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

The first two things we need to do after adding the library dependency are:

1. Enable caching by adding the @EnableCaching annotation to a configuration class.
2. Add your preferred caching library (e.g., Caffeine, Redis, Hazelcast, or ...). If you don't provide any cache library, Spring Boot will automatically autoconfigure the default in-memory cache based on the ConcurrentHashMap.  

   ### Implementing the Phonebook example using Spring Boot

   Let's continue by implementing the Phonebook example that we discuss in the previous section as a simple Spring Web MVC project with four REST APIs:
3. Get a phone number with a name.
4. Add a phone number with a name.
5. Update a phone number with a name.
6. Delete a phone number with a name.

For the sake of simplicity, we use the default simple in-memory Spring cache implementation, which autoconfigures by default if we don't provide any library, and also use an in-memory map as a Database.

```
@Service
class PhonebookService {
    private final PhonebookRepository phonebookRepository;
    PhonebookService(PhonebookRepository phonebookRepository) {
        this.phonebookRepository = phonebookRepository;
    }
    PhoneNumber findByName(String name) {
        return phonebookRepository.findByName(name);
    }
    PhoneNumber create(PhoneNumber phoneNumber) {
        return phonebookRepository.insert(phoneNumber);
    }
    PhoneNumber update(String name, PhoneNumber phoneNumber) {
        return phonebookRepository.update(name, phoneNumber);
    }
    void delete(String name) {
        phonebookRepository.delete(name);
    }
}
```

We simulate slowness at the repository level by sleeping the current thread for 1 second before each request.

```
@Repository
class PhonebookRepository {
    private final ConcurrentHashMap<String, PhoneNumber> database = new ConcurrentHashMap<>();
    PhoneNumber findByName(String name) {
        return simulateDatabaseInteraction(() -> database.get(name), 1000L);
    }
    PhoneNumber insert(PhoneNumber phoneNumber) {
        return simulateDatabaseInteraction(() -> {
            database.put(phoneNumber.name(), phoneNumber);
            return phoneNumber;
        }, 500L);
    }
    PhoneNumber update(String name, PhoneNumber phoneNumber) {
        return simulateDatabaseInteraction(() -> {
            database.remove(name);
            database.put(phoneNumber.name(), phoneNumber);
            return phoneNumber;
        }, 700L);
    }
    PhoneNumber delete(String name) {
        return simulateDatabaseInteraction(() -> database.remove(name), 200L);
    }
    public List<PhoneNumber> findByNumber(String number) {
        return simulateDatabaseInteraction(() -> database
                .values()
                .stream()
                .filter(phoneNumber -> phoneNumber.number().equals(number))
                .toList(), 2000L);
    }
    private <T> T simulateDatabaseInteraction(Supplier<T> block, long duration) {
        try {
            Thread.sleep(duration);
            return block.get();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
}
```

### Spring Boot annotations for Caching

Spring Boot supports caching concepts by introducing several annotations that can be used at the method level in a declarative way:

**Caching:** Spring Boot supports cashing an item using @CachePut and @Cacheable annotations. By using the @CachePut annotation, we indicate that the result of this method will be cached. It means Spring Framework first calls the method and then saves the result into the cache, and finally returns the result. In a CRUD scenario, the @CachePut annotation is used for Create and Update methods. The @Cacheable annotation acts similarly to the @CachePut annotation if Cashe misses.

**Cache Hit**: Spring supports cache hit by annotating candidate methods with @Cacheable annotation, which indicates that the method result might cached, and if so (cache hit), then instead of calling the method, Spring Framework returns the cached value.

**Cache Eviction**: Spring Boot supports cashing an item using @CachePut and @CacheEvict annotations.

## Adding the cache capability to the Phonebook example

Now we know how to use the cache concept in a Spring Boot. Let us add a cache capability to the Phonebook's service layer.

```
@Service
class PhonebookService {
    private final PhonebookRepository phonebookRepository;
    PhonebookService(PhonebookRepository phonebookRepository) {
        this.phonebookRepository = phonebookRepository;
    }
    @Cacheable(value = "phonebook")
    PhoneNumber findByName(String name) {
        return phonebookRepository.findByName(name);
    }
    @CachePut(value = "phonebook", key = "#phoneNumber.name")
    PhoneNumber create(PhoneNumber phoneNumber) {
        return phonebookRepository.insert(phoneNumber);
    }
    @Caching(put = @CachePut(value = "phonebook", key = "#phoneNumber.name"),
            evict = @CacheEvict(value = "phonebook", key = "#name"))
    PhoneNumber update(String name, PhoneNumber phoneNumber) {
        return phonebookRepository.update(name, phoneNumber);
    }
    @CacheEvict(value = "phonebook")
    void delete(String name) {
        phonebookRepository.delete(name);
    }
}
```

By adding these annotations to the service layer, the Phonebook application performance now is improved.

### More advanced features

There are some advanced features in Spring Boot caching support, like custom Key generation strategies or cashing manually (non-declarative or programmatically) using the CacheManager bean.

## What are the signs of detecting cache misses in observability?

Traditionally, to detect when and where we need to cache data to improve performance, we have to deploy our application and use monitoring tools to see the patterns in the diagrams and charts.

We might even need to load test our applications to understand better how our applications behave under load. This approach is time-consuming and expensive and leads to a long feedback loop.

The [Digma IDE plugin](https://digma.ai/ "Digma IDE plugin") finds bottlenecks, scaling challenges, and query problems lurking in your codebase during development without the need to deploy our project. It brings us a short and Continuous Feedback loop.

### Adding a slow endpoint to the Phonebook project

Let's continue by adding this [endpoint](https://digma.ai/whats-new-feb-2024/ "endpoint") to the Phonebook project:

Get all names for a phone number.

```
//Controller
    @GetMapping("/numbers/{number}")
    List<PhoneNumber> getByNumber(@PathVariable String number) {
        return phonebookService.findByNumber(number);
    }
//Service
    public List<PhoneNumber> findByNumber(String number) {
        return phonebookRepository.findByNumber(number);
    }
//Repository
    public List<PhoneNumber> findByNumber(String number) {
        return simulateDatabaseInteraction(() -> database
                .values()
                .stream()
                .filter(phoneNumber -> phoneNumber.number().equals(number))
                .toList(), 2000L);
    }
```

After implementing the controller, service, and repository methods, we make the repository method slow (2 seconds) to see how Digma helps us detect that.

First, we need to install the Digma [IntelliJ plugin](https://plugins.jetbrains.com/plugin/19470-digma-continuous-feedback "IntelliJ plugin") in our IDE and configure its infrastructure: After that, Let's start to call the Phonebook endpoint using the [HTTPie](https://httpie.io/ "HTTPie") command line tools:

```
http post :8080/api/phonebooks/phones name="saeed" number="+46123"
http :8080/api/phonebooks/phones/saeed
http delete :8080/api/phonebooks/phones/saeed
http post :8080/api/phonebooks/phones name="deli" number="+46345" 
http :8080/api/phonebooks/phones/deli
http put :8080/api/phonebooks/phones/deli name="zarin" number="+46345" 
http :8080/api/phonebooks/phones/numbers/+46123
```

One of the most important features of Digma is Insights, Digma uses OpenTelemetry behind the scenes to collect data (traces, logs, and metrics) about our code when we run it locally and then turn those Observability Data Into Insights by analyzing them.

In our case, Digma sends a notification in the IntelliJ Idea to inform us about a found insight.  
![](https://digma.ai/wp-content/uploads/2024/03/image-3.png)

*Digma insight notification in IntelliJ*  
![](https://digma.ai/wp-content/uploads/2024/03/image-4.png)

*Digma insights in Observability view*

Also, in the Observability view, you can see the insight column and click on the insight to open it. As you can see in the image below, you can read about the founded insight in the Digma Insights view.

![](https://digma.ai/wp-content/uploads/2024/03/image-5.png)

Digma detects that this new endpoint, on average, is slower than other endpoints. In real scenarios, it can be because of a slow database query or forgetting to use cache, cache misses, or ... In this case, we need to use the @Cacheable annotation on the method to fix this issue:

```
@Cacheable(value = "phonebook.number")
public List<PhoneNumber> findByNumber(String number) {
    return phonebookRepository.findByNumber(number);
}
```

And also, we should consider these two:

1. We used a new cache (phonebook.number) for this method because our previous cache was by name, and this one is by phone number.
2. We need more complicated cache eviction strategies for this cache. To handle more complicated cache eviction strategies, we need to inject the @CacheManager bean and implement our eviction strategies programmatically.

The final code for the Phonebook project is accessed at this [GitHub repository](https://github.com/zarinfam/phonebook "GitHub repository").

## Final thoughts

Caching is a powerful technique that can greatly improve the performance, scalability, and efficiency of any system.

By storing **frequently accessed** data in a **fast** and **accessible** location, caching reduces the need for expensive or repetitive operations, such as database queries, network calls, or computations.

However, caching also introduces some challenges and trade-offs, such as cache invalidation, eviction policies, consistency, and capacity.

Therefore, it is important to understand the benefits and drawbacks of different types of caching and to apply them appropriately to the specific needs and goals of each system.

Tools like Digma allow us to find bottlenecks and slowness during development using observability tools.
