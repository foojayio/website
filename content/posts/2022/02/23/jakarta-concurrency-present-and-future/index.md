---
title: "Jakarta Concurrency: Present and Future"
slug: "jakarta-concurrency-present-and-future"
date: "2022-02-23T14:15:13+00:00"
lastmod: "2022-02-23T14:25:26+00:00"
description: "Jakarta Concurrency is a small, but fundamental, specification in the Jakarta EE umbrella. As project lead, I provide more information on it."
canonical: "https://blog.payara.fish/jakarta-concurrency-present-and-future"
authors:
  - "steve-millidge"
image: "Favicon-3-2.png"
categories:
  - "Jakarta EE"
  - "Performance"
tags:
related_posts:
  - "are-java-jakarta-ee-application-servers-heavy"
  - "do-java-jakarta-ee-standards-matter"
  - "evolution-of-microservices"
  - "ejb-support-in-piranha-via-cdi"
enlighterjs: true
frozen: false
---

[Jakarta EE](https://jakarta.ee/), previously Java EE, is a set of specifications that enables the world wide community of Java developers to work on cloud native Java enterprise applications. It is an open source project maintained by the [Eclipse Foundation.](https://www.eclipse.org/org/)

Jakarta Concurrency is a small, but fundamental, specification under the Jakarta EE umbrella. As project lead, I provide more information on what it is, its future and how to be involved.

Why Do We Need Concurrency? {#h2-0-why-do-we-need-concurrency}
--------------------------------------------------------------

When programming in Java, you need to take into consideration the level of context for your application when you move between different threads. For example, if you've logged into a REST API and then need to create a task on a new thread, you need to retain the security context. To do this in [Java SE](https://www.oracle.com/java/technologies/downloads/), you use concurrency primitives: units of code related to concurrency, multithreading, and parallelism.  

If you spawn a new thread in Java SE, the Jakarta EE runtime is unaware of the thread, and would struggle to establish its security (or classloader, or CDI, etc.) context.  

This is where Jakarta Concurrency comes in. It updates Java SE concurrency primitives for use in a Jakarta EE environment. Java SE Executor Service becomes Managed Executor Service, for example, which has the same API. These analogous concurrency primitives allow you to use Java SE concurrency measures in your application server.  

Jakarta EE concurrency provides consistency between the Java SE and Jakarta EE platforms, for a simple migration path from SE to EE. It also allows you to easily design new Jakarta EE applications using concurrency design principles, and add concurrency to existing applications in a Jakarta EE application server.

Jakarta EE aims to allow developers to concentrate on business logic code, taking away infrastructural and operational tasks. One of these tasks is thread management. Jakarta Concurrency, therefore, allows you to access these in a managed Jakarta EE runtime.

### Use Case 1: Adding an Asynchronous Task in Your Application {#h3-1-use-case-1-adding-an-asynchronous-task-in-your-application}

Before Jakarta Concurrency, a Java EE developer would need to use JMS to build an asynchronous task into the application. A HTTP request coming in to a servlet or REST endpoint, and needing a long-running action in response, would require packaging the request info and pushing it into a JMS queue using a message driven bean. This is a heavyweight process.  

Jakarta Concurrency makes this much simpler. If you have a HTTP request, you can use the same component you would in Java SE, but in a Java EE version: Managed Executor Service. You'd submit a job to this, and this would run in a managed thread to replace your long-running action. This thread will be managed by the Jakarta runtime, and apply the same context in the REST request to the action. It's therefore much more lightweight.  

In code form, this would look like this, providing a vastly more simple code than using JMS:

```java
@Path("concurrency")
@RequestScoped
public class GenericResource {

   @Context
   private UriInfo uriInfo;

   @Resource
   private ManagedExecutorService managedExecutor;

   @GET
   @Path("simpleJob")
   @Produces(MediaType.TEXT_PLAIN)
   public String getText() {
      managedExecutor.submit(() -> {
         System.out.println("Job running");
      });
      return "Job Submitted";
   }
}
```


### Use Case 2: Running Tasks in Parallel {#h3-2-use-case-2-running-tasks-in-parallel}

You may have a REST request coming in, and want to run two tasks in parallel, merge the result and return it to a user. With Jakarta Concurrency you can inject the Managed Executor Service into your REST endpoint, and then you can use the Managed Executor API to submit two jobs at once. The method returns immediately and you get back an instance of Future class. You can get the outcome of the job by calling the *get()* method and merge it and then return to the user.  

Previously, this would have been incredibly difficult to do, needing JMS, with a need to create correlation IDs. With Jakarta Concurrency, the resulting code would look something like this:

```java
@GET
@Path("parallelJob")
@Produces(MediaType.TEXT_PLAIN)
public String getParallelJob() throws ExecutionException, InterruptedException {
   Future future1 = managedExecutor.submit(() -> {
         System.out.println("Job 1 running ...");
         // This takes some while
         System.out.println("Job 1 finished ...");
      });
   Future future2 = managedExecutor.submit(() -> {
         System.out.println("Job 2 running ...");
         // This takes some while
         System.out.println("Job 2 finished ...");
   });
   future1.get(); // Wait for job to finish and get result (optionally)
   future2.get();
   return "Jobs completed";
}
```


What Are The Main Components of Jakarta Concurrency? {#h2-3-what-are-the-main-components-of-jakarta-concurrency}
----------------------------------------------------------------------------------------------------------------

* **Managed Executor Services.**Managed Executor Service in Jakarta EE maps on to Executor Service in Java SE, Managed Scheduled Executor Service maps on to Scheduled Executive Service (as you have seen in the use cases above). The API is the same, but the task is run in the context required for Jakarta EE.

<!-- -->

* **Managed Thread Factory.** Managed Thread Factory in Jakarta EE maps on to Thread Factory in Java SE. This can be used when you have an API that creates its own *threads* but has no knowledge of Jakarta EE. You can pass in a Thread Factory. This allows you to call into APIs or libraries that are not aware of Jakarta Concurrency but do take a Thread Factory as a parameter. With Managed Thread Factory, when a task is created to run on the thread, it sets up the correct context again.

<!-- -->

* **Context Service.** Context Service enables you to wrap your *Runnable*, creating a contextual proxy to submit to any raw thread. It will establish all the context you expect, and therefore is useful for if you are using an API or library that has no knowledge of Jakarta EE but is spawning threads.

Future Outlook of Jakarta Concurrency {#h2-4-future-outlook-of-jakarta-concurrency}
-----------------------------------------------------------------------------------

Jakarta Concurrency arrived in Java EE 7, in 2013. As with other specifications, it was moved into the Jakarta EE namespace with the [Jakarta EE 9](https://blog.payara.fish/jakarta-ee-9-is-here) release, and made compatible with Java SE 11 within [Jakarta EE 9.1](https://blog.payara.fish/jakarta-ee-9.1-launches). Now the transition is complete, it is in the place to start making substantial, functional changes to specifications.  

Some of the ideas in development (available to view in[GitHub](https://github.com/eclipse-ee4j/concurrency-api)) are:

### Deployable Managed Objects {#h3-5-deployable-managed-objects}

Currently all Managed Objects (other than the default ones) must be created by the application server administrator.  

Jakarta EE 9 has deployable application scoped data sources. What is suggested here is a feature whereby you could set up your own application scoped and configured managed executor services. These would be deployed using annotations.  

If you have an application which really needs to have fine-grained control of the threading, concurrency and pooling of threads, this feature will allow you to set these processes up without needing administration consoles.

### New @Asynchronous annotation {#h3-6-new-asynchronous-annotation}

Currently there are annotations in Jakarta EE to indicate asynchronous execution, but they are quite specific to each individual specification. This suggested new annotation could be used with CDI Beans. Adding and being able to configure executor service pools will enable fine grained concurrency management when combined with deployable executor services. You could choose different thread pools for different methods, for example.  

This new feature could eventually result in a single @Asynchronous annotation common to all the specifications. This would simplify the platform and make it more unified.

### Catch up with java.until.concurrent {#h3-7-catch-up-with-java-until-concurrent}

Jakarta Concurrency was released in Java EE 7, so has not been brought up to date to match concurrency primitives brought in with Java SE 9, 11 and 17. Some of these include support for the ForkJoinPool in a standard way, and updated APIs for current Java SE Managed Executor Services.

Get Involved! {#h2-8-get-involved}
----------------------------------

As the project lead of Jakarta Concurrency, my goal is not to define its future, but lead a community group that can drive the specification forwards. In short, I need your help to make the specification happen!  

Each specification needs to produce a specification document, detailing how you use the API; an API JAR, to be coded against as a developer; and a Technology Compatibility Kit (TCK), the test suite that is used to determine whether independent implementations of the API meet its requirements.  

You can get involved in each and every part of the process. For example, if you are interested in testing, you could help build tests or maintain the TCK. You can also help directly on the API, working out what capabilities you'd want as a developer. There is also help needed around the specification document, from submitting PRs for typos to higher level input.

* The whole project is on GitHub, you can make your API changes there:  

  [Jakarta Concurrency GitHub](https://blog.payara.fish/cs/c/?cta_guid=b5b79395-a545-4034-a65f-68bfd79ae830&signature=AAH58kFz-PTWbNlaveKVnRkOBNWMTN_-rg&pageId=57864145848&placement_guid=048bbebf-3f2e-4cee-9e14-4351a347363a&click=49ca1b83-ef54-407c-b6c7-35621bf1a7f2&hsutk=4b97431ee110dccec85898b30b16c5d0&canon=https%3A%2F%2Fblog.payara.fish%2Fjakarta-concurrency-present-and-future&utm_referrer=https%3A%2F%2Fblog.payara.fish%2Fpage%2F3&portal_id=334594&redirect_url=APefjpGg7pK_eAkoPoGCIZZr26ZXkKWHWCmIV5fgVuXU7j3JyuKgPH-dHEx5YmlK7kRKI3sU0MYnq3AblgYWH5CzK3s_1bcz7XeJW8502QCX9oVFOQFN9bXT4rB2eHGQEVYFZjQxejbnYEJ-sFUe6AMZV8Uk9I8CGcD5OjW41cqRFw1GOM3OvFjErT7RVG0i7S5iTil-ogJPVrYNV0c_OSyLoTNsMzJeo0Oh11JJzZH9f40T_xLyIOVJvCrl6oBiLNPsDo373rLAQeqiAyLioLWK2sVXP_Zbu7IfcvdhPj1grLKQDOZtABc&__hstc=229474563.4b97431ee110dccec85898b30b16c5d0.1620394154153.1644593918467.1644834329728.399&__hssc=229474563.7.1644834329728&__hsfp=3570997368&contentType=blog-post)  

<!-- -->

* Find the Eclipse Project here:  

  [Jakarta Concurrency Eclipse Project](https://blog.payara.fish/cs/c/?cta_guid=9c9e841d-0734-406b-95a5-75ae0fc37d90&signature=AAH58kGLAMYr3RmUKvEwtl5qx4Dn3Bv4uw&pageId=57864145848&placement_guid=9fda33e2-d0a9-41fa-a8c7-bbebc1f9ac61&click=5bf9a70c-e73c-4192-bb6b-27847624214d&hsutk=4b97431ee110dccec85898b30b16c5d0&canon=https%3A%2F%2Fblog.payara.fish%2Fjakarta-concurrency-present-and-future&utm_referrer=https%3A%2F%2Fblog.payara.fish%2Fpage%2F3&portal_id=334594&redirect_url=APefjpGOv05HvgaD8gx_inPiNfpsHTvFF7EocrYLErrvywuTFar16Jneq3mdyQ59O4NCrskQ42ZO46fDNoZNaRITxd7gadeOF524wtXqnCm1FfdU0fkWekZX27NfwHE9RG8GMCop7ZWV1FWzN198jfgvHnxJfR8EYRMV2Aumf16GB-NZpMV1bY6dA2IjQxLGQXukNEHGP6ZrYXmhnz6rlI6U2gamB909aeWRZ-Yq7kJkNpUnSSGMoA2gDGiRkEZYYqmkNDQ-cqDTrARofgucYdNlt1JMCOdxaQ&__hstc=229474563.4b97431ee110dccec85898b30b16c5d0.1620394154153.1644593918467.1644834329728.399&__hssc=229474563.7.1644834329728&__hsfp=3570997368&contentType=blog-post)

<!-- -->

* Join the Mailing List:   

  [Jakarta Concurrency Mailing List](https://blog.payara.fish/cs/c/?cta_guid=12c3b36a-816d-42f5-bf72-7b787e3db30f&signature=AAH58kFje5qk-NjiQ5HRRNynMkUk8aChsw&pageId=57864145848&placement_guid=2da34d30-ae36-45f2-a7eb-2da1f8b11bf3&click=ee0f1e2d-5e2e-4ba7-a9ed-faaf4e4c6d2f&hsutk=4b97431ee110dccec85898b30b16c5d0&canon=https%3A%2F%2Fblog.payara.fish%2Fjakarta-concurrency-present-and-future&utm_referrer=https%3A%2F%2Fblog.payara.fish%2Fpage%2F3&portal_id=334594&redirect_url=APefjpFs3hKrsOwK-1COfm6m56XoYHwRCqZcBZHOv-oWWHGTEjkWah4ZZu2jc2HCOEGKosVbwefKIgMHaQf8vv5Y5qHFLPGoTWmKzOWxAHu8mwr4gcok1h3plrVDI-Z2QVOwscGtr88rQD-tC_U_OvYGUNrzjjRIYtS_LBpJK0F3yacIsemDX6uYs6oVKmYSiYUEr5VtgoGy2MejMYE_A1to7bPxeeQGnrBXQ7LK7oU0XrdKbbPZz_07miWMYJOHBN9BlPnryrkWyMExI4xoqO5jKJlr-N02-4_VMEtYcSG0eaEb9W2z5ZE&__hstc=229474563.4b97431ee110dccec85898b30b16c5d0.1620394154153.1644593918467.1644834329728.399&__hssc=229474563.7.1644834329728&__hsfp=3570997368&contentType=blog-post)

<!-- -->

* There is also a compatible implementation on GitHub, used in GlassFish and Payara:  

  [Concurrency Compatible Implementation](https://blog.payara.fish/cs/c/?cta_guid=ad60f698-4df1-4204-ae7b-5b01977905ba&signature=AAH58kFawApAZwMKG7GHYW5Jkmc5DTKnEg&pageId=57864145848&placement_guid=02da90b0-c672-4772-ab5d-f12448e47ad8&click=81f1ea07-6290-4387-a503-a20901d1ad8d&hsutk=4b97431ee110dccec85898b30b16c5d0&canon=https%3A%2F%2Fblog.payara.fish%2Fjakarta-concurrency-present-and-future&utm_referrer=https%3A%2F%2Fblog.payara.fish%2Fpage%2F3&portal_id=334594&redirect_url=APefjpGiIU-XKjwqkptdyJkHHseJ22tgQN1KW8VV-I6sdYekXOde7REYQuSTefQ_uzlnIH83mm-2quO5wzRxf1V6xfkHgi_-Ch3NLMvta7IAcDHUMyyQd4oMKz4_pZqsElAaDaX6PtUzOo3ddUhtO3qg1ejerFrHqD3PUDf2BmM8XMAsQf2oLPbW4341200azC0jyJR5mMHebEOxwa4LblopanbmpTJtZke5MrxVHPQi2KBgYK03WLURTND4UrNfqM74Akxv7bIkgwtwMUP5CoLrrIyI4Lcp6w&__hstc=229474563.4b97431ee110dccec85898b30b16c5d0.1620394154153.1644593918467.1644834329728.399&__hssc=229474563.7.1644834329728&__hsfp=3570997368&contentType=blog-post)

It can seem daunting to become involved with an open source project, but Jakarta Concurrency is actively asking for help of all kinds. If in doubt: get involved!
