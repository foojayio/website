---
title: "How to Use Java DTOs to Stay Secure | Foojay.io Today"
slug: "how-to-use-java-dtos-to-stay-secure"
date: "2022-10-21T07:29:57+00:00"
lastmod: "2022-10-21T07:29:58+00:00"
description: "How DTOs are used in modern Java, how your apps can benefit, and how Java DTOs can help you be more secure."
canonical: "https://snyk.io/blog/how-to-use-java-dtos/"
authors:
  - "bmvermeer"
image: "snyk-logo-2.png"
categories:
  - "Java Core"
  - "Security"
tags:
related_posts:
  - "5-tips-to-create-secure-docker-images-for-java-applications"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "are-java-security-updates-important"
enlighterjs: true
frozen: false
---

~This article was orignally posted on [Snyk.io](https://snyk.io/blog/how-to-use-java-dtos/) and is reused with permission.~

Data Transfer Objects (DTOs) in Java are objects that transport data between subsystems.

It is an enterprise design pattern to aggregate data.

The main purpose is to reduce the number of system calls needed between the subsystems, reducing the amount of overhead created.

In this article, I will explain how DTOs are used in modern Java applications, ways your application can benefit, and how Java DTOs can help you be more secure by preventing accidental data leaks.

What is a POJO, Java Bean, and Value Object {#h2-0-what-is-a-pojo-java-bean-and-value-object}
---------------------------------------------------------------------------------------------

As the name already suggested, a Plain Old Java Object (POJO) is an ordinary Java Object. It can be any class and isn't bound to any specific restrictions other than the ones prescribed by the Java language. They are created for re-usability and increased readability.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">public class CoffeePOJO {

   public String name;
   private List&lt;String&gt; ingredients;

   public CoffeePOJO(String name, List&lt;String&gt; ingredients) {
       this.name = name;
       this.ingredients = ingredients;
   }

   void addIngredient(String ingredient) {
       ingredients.add(ingredient);
   }
}</pre>

A Java Bean is a POJO according to the [JavaBean standard](https://www.oracle.com/java/technologies/javase/javabeans-spec.html). According to this standard, all properties are private, and will be accessed with getter and setter methods. Additionally a no-arg constructor should be present, along with a few more things.

So, this means that while all Java Beans are POJOs, not all POJOs are Java Beans.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">public class CoffeeBEAN implements Serializable {

   private String name;
   private List&lt;String&gt; ingredients;

   public CoffeeBEAN() {
   }

   public String getName() {
       return name;
   }

   public void setName(String name) {
       this.name = name;
   }

   public List&lt;String&gt; getIngredients() {
       return ingredients;
   }

   public void setIngredients(List&lt;String&gt; ingredients) {
       this.ingredients = ingredients;
   }
}</pre>

A Value Object is a small object that represents a simple data entity.

However, a value object doesn't typically have an identity.

Value objects are not currently available in Java, but JDK maintainers are working to add them [as part of JEP 401](https://openjdk.org/jeps/8277163).

For now, we have to create a POJO to do the work for us.

Implementing a Data Transfer Object {#h2-1-implementing-a-data-transfer-object}
-------------------------------------------------------------------------------

A DTO can be implemented as a POJO --- or a Java Bean for that matter. The most important thing is that a DTO separates concerns between entities like the presentation layer and the domain model, for example.

Let's take a small rest service to explain this. Say we have a coffee store with coffees and customers. Both of these are separate domain entities in the system. If I want to know a customer's favorite coffee, I'll create an API that provides the aggregate data represented in the FavoriteCoffeDTO.

![Diagram showing how Java DTOs separate the presentation layer and domain layer in an API.](https://snyk.io/wp-content/uploads/blog-java-dto-diagram.jpg)

The code looks something like this:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">public class Coffee {

   private Long id;
   private String name;
   private List&lt;String&gt; ingredients;
   private String preparation;

}

public class Customer {

   private Long id;
   private String firstName;
   private String lastName;
   private List&lt;Coffee&gt; coffees;

}

public class FavoriteCoffeeDTO {

   private String name;
   private List&lt;String&gt; coffees;

}</pre>

I separated the domain layer from the presentation layer in the implementation above, allowing my controller to now handle mapping the two domain entities to the DTO.

In this example, I made the fields private, meaning I have to create getter and setter methods to access the fields. Users often choose to follow the JavaBean standard either completely or partially for DTO.

This isn't mandatory of course, you're free to choose whatever method suits your needs. Other options include making all fields public and accessing them directly (like the example below), or making the object immutable with an all-args constructor and some getter methods.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">public class FavoriteCoffeeDTO {

   public String name;
   public List&lt;String&gt; coffees;

}

String name = favCoffeeDTO.name;</pre>

Lastly, if you updated to a more recent version of Java, you might want to use Java records for your DTO's. Java Records are simple immutable classes that automatically provide you with an all-args constructor, access methods, toString(), and hashCode() without defining them.

This makes your code less verbose and more readable. Notice that records do not follow the Java Bean specification, since the access methods do not have a `get` or `set` prefix.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">public record FavoriteCoffeeDTO(String name, List&lt;String&gt; coffees) {}

String name = favoriteCoffeeDTO.name();</pre>

What makes a good DTO? {#h2-2-what-makes-a-good-dto}
----------------------------------------------------

The purpose of a DTO is to carry data between processes. Therefore, a good DTO only contains the information needed for that specific part of the system.

In our API example, we only need to return the name of the customer and their favorite coffee order. There is no need to add anything else, such as business logic. The general advice is to keep your DTOs as simple, small, and straightforward as possible.

Also after a DTO is initialized, its state shouldn't change or evolve. This means that an immutable data structure would be a great fit for a DTO. As a DTO only carries data that should be unaltered, a Java Record would be a great fit --- especially because JSON serialization libraries like Jackson support Java Records.

DTO security considerations {#h2-3-dto-security-considerations}
---------------------------------------------------------------

We already noticed that we decouple the Domain model from the presentation layer with this DTO pattern. Simple DTOs that only contain the data needed for this subsystem or API, without any business logic, can also improve your security.

What we see in many proofs of concepts is that domain entries are fully outputted. This can lead to unnecessary data being available outside of the system and potential data leaks.

Say our API has a function to find all customers, but does not use a DTO:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="dracula">@GetMapping("/customers")
public List&lt;Customer&gt; getAllCustomers(){
   return repository.findAll();       
}</pre>

If our customer object is the same as in our previous example, we are already displaying too much information. Do we actually need the `id` or the list of favorite `coffees`?

It gets worse if we decide to attach more information to the customer, like a home address.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">public class Customer {
   private Long id;
   private String firstName;
   private String lastName;
   private List&lt;Coffee&gt; coffees;
   Private String homeAddress;

}</pre>

If we don't filter in our endpoint, we suddenly create a data breach, since providing a full name and home address is considered a privacy breach in many countries.

Decoupling the API from the data model with a DTO would have prevented this because the mapper controller would only populate necessary fields in the DTO.

Even when we decide to add something to our domain model afterwards, using a DTO prevents us from essentially leaking personally identifiable information.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">public class CustomerDTO {
   private String firstName;
   private String lastName;
}</pre>

Recommendations {#h2-4-recommendations}
---------------------------------------

Using DTOs in Java to decouple subsystems is generally a good idea.

From an engineering perspective, it will reduce roundtrips between the different layers and form a security angle it will help prevent accidental data leaks.

In terms of security, I would recommend making your DTOs specific and limiting the amount of reuse.

If you are reusing your DTOs for different functions, you should clearly understand where these DTO's are used before changing them.

In general, keep your DTOs concise, free of business logic if possible, and only provide the data needed for specific functions.

Lastly, I believe that immutability is a natural fit for DTOs, making Java Records --- which was fully released in Java 16 --- a great way to implement DTOs in Java.

Check out the following resources to learn more about Java security:

* [Serialization and deserialization in Java: explaining the Java deserialize vulnerability](https://snyk.io/blog/serialization-and-deserialization-in-java/)
* [10 best practices to build a Java container with Docker](https://snyk.io/blog/best-practices-to-build-java-containers-with-docker/)
* [Best practices for managing Java dependencies](https://snyk.io/blog/best-practices-for-managing-java-dependencies/)
* [10 Java security best practices](https://snyk.io/blog/10-java-security-best-practices/)
* [Java logging: what should you log and what not?](https://snyk.io/blog/java-logging-what-should-you-log-and-what-not/)

Secure your Java code with Snyk {#h2-5-secure-your-java-code-with-snyk}
-----------------------------------------------------------------------

Create a free Snyk account to find and fix vulnerabilities in your Java applications.

[Sign up for free](https://app.snyk.io/login?cta=sign-up&amp;loc=body&amp;page=how-to-use-java-dtos-to-stay-secure)
