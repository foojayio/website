---
title: "Jakarta Concurrency: Present and Future"
slug: "jakarta-concurrency-present-and-future-2"
date: "2022-07-13T13:38:25+00:00"
lastmod: "2022-07-13T13:39:16+00:00"
description: "Concurrency is a small fundamental spec under the Jakarta EE umbrella. As project lead, here's what it is, its future, and how to be involved!"
canonical: "https://blog.payara.fish/jakarta-concurrency-present-and-future"
authors:
  - "steve-millidge"
image: "concurrency.png"
categories:
  - "Jakarta EE"
tags:
related_posts:
  - "7-reasons-to-switch-to-openjdk-17-as-a-jakarta-ee-developer"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "can-java-jakarta-ee-do-microservices"
  - "issues-with-old-glassfish-server-upgrade-to-eclipse-glassfish"
enlighterjs: true
frozen: false
---

[Jakarta EE](https://jakarta.ee/), previously Java EE, is a set of specifications that enables the world wide community of Java developers to work on cloud native Java enterprise applications.

It is an open source project maintained by the [Eclipse Foundation.](https://www.eclipse.org/org/)

Jakarta Concurrency is a small, but fundamental, specification under the Jakarta EE umbrella.

As project lead, I provide more information on what it is, its future and how to be involved.

Why Do We Need Concurrency? {#h2-0-why-do-we-need-concurrency}
--------------------------------------------------------------

When programming in Java, you need to take into consideration the level of context for your application when you move between different threads. For example, if you've logged into a REST API and then need to create a task on a new thread, you need to retain the security context. To do this in [Java SE](https://www.oracle.com/java/technologies/downloads/), you use concurrency primitives: units of code related to concurrency, multithreading, and parallelism.

If you spawn a new thread in Java SE, the Jakarta EE runtime is unaware of the thread, and would struggle to establish its security (or classloader, or CDI, etc.) context.

This is where Jakarta Concurrency comes in. It updates Java SE concurrency primitives for use in a Jakarta EE environment. Java SE Executor Service becomes Managed Executor Service, for example, which has the same API. These analogous concurrency primitives allow you to use Java SE concurrency measures in your application server.

Jakarta EE concurrency provides consistency between the Java SE and Jakarta EE platforms, for a simple migration path from SE to EE. It also allows you to easily design new Jakarta EE applications using concurrency design principles, and add concurrency to existing applications in a [Jakarta EE application server](https://www.payara.fish/).

Jakarta EE aims to allow developers to concentrate on business logic code, taking away infrastructural and operational tasks. One of these tasks is thread management. Jakarta Concurrency, therefore, allows you to access these in a managed [Jakarta EE runtime](https://www.payara.fish/).

### Use Case 1: Adding an Asynchronous Task in Your Application {#h3-1-use-case-1-adding-an-asynchronous-task-in-your-application}

Before Jakarta Concurrency, a Java EE developer would need to use JMS to build an asynchronous task into the application. A HTTP request coming in to a servlet or REST endpoint, and needing a long-running action in response, would require packaging the request info and pushing it into a JMS queue using a message driven bean. This is a heavyweight process.  

Jakarta Concurrency makes this much simpler. If you have a HTTP request, you can use the same component you would in Java SE, but in a Java EE version: Managed Executor Service. You'd submit a job to this, and this would run in a managed thread to replace your long-running action. This thread will be managed by the Jakarta runtime, and apply the same context in the REST request to the action. It's therefore much more lightweight.  

In code form, this would look like this, providing a vastly more simple code than using JMS:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Path("concurrency")
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
      managedExecutor.submit(() -&gt; {
         System.out.println("Job running");
      });
      return "Job Submitted";
   }
}</pre>

### Use Case 2: Running Tasks in Parallel {#h3-2-use-case-2-running-tasks-in-parallel}

You may have a REST request coming in, and want to run two tasks in parallel, merge the result and return it to a user. With Jakarta Concurrency you can inject the Managed Executor Service into your REST endpoint, and then you can use the Managed Executor API to submit two jobs at once. The method returns immediately and you get back an instance of Future class. You can get the outcome of the job by calling the *get()* method and merge it and then return to the user.  

Previously, this would have been incredibly difficult to do, needing JMS, with a need to create correlation IDs. With Jakarta Concurrency, the resulting code would look something like this:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@GET
@Path("parallelJob")
@Produces(MediaType.TEXT_PLAIN)
public String getParallelJob() throws ExecutionException, InterruptedException {
   Future future1 = managedExecutor.submit(() -&gt; {
         System.out.println("Job 1 running ...");
         // This takes some while
         System.out.println("Job 1 finished ...");
      });
   Future future2 = managedExecutor.submit(() -&gt; {
         System.out.println("Job 2 running ...");
         // This takes some while
         System.out.println("Job 2 finished ...");
   });
   future1.get(); // Wait for job to finish and get result (optionally)
   future2.get();
   return "Jobs completed";
}</pre>

What Are The Main Components of Jakarta Concurrency? {#h2-3-what-are-the-main-components-of-jakarta-concurrency}
----------------------------------------------------------------------------------------------------------------

### 1. Managed Executor Services {#h3-4-1-managed-executor-services}

Managed Executor Service in Jakarta EE maps on to Executor Service in Java SE, Managed Scheduled Executor Service maps on to Scheduled Executive Service (as you have seen in the use cases above). The API is the same, but the task is run in the context required for Jakarta EE.

### 2. Managed Thread Factory {#h3-5-2-managed-thread-factory}

Managed Thread Factory in Jakarta EE maps on to Thread Factory in Java SE. This can be used when you have an API that creates its own *threads* but has no knowledge of Jakarta EE. You can pass in a Thread Factory. This allows you to call into APIs or libraries that are not aware of Jakarta Concurrency but do take a Thread Factory as a parameter. With Managed Thread Factory, when a task is created to run on the thread, it sets up the correct context again.

### 3. Context Service {#h3-6-3-context-service}

Context Service enables you to wrap your *Runnable*, creating a contextual proxy to submit to any raw thread. It will establish all the context you expect, and therefore is useful for if you are using an API or library that has no knowledge of Jakarta EE but is spawning threads.

Future Outlook of Jakarta Concurrency {#h2-7-future-outlook-of-jakarta-concurrency}
-----------------------------------------------------------------------------------

Jakarta Concurrency arrived in Java EE 7, in 2013. As with other specifications, it was moved into the Jakarta EE namespace with the [Jakarta EE 9](https://blog.payara.fish/jakarta-ee-9-is-here) release, and made compatible with Java SE 11 within [Jakarta EE 9.1](https://blog.payara.fish/jakarta-ee-9.1-launches). Now the transition is complete, it is in the place to start making substantial, functional changes to specifications.  

Some of the ideas in development (available to view in[GitHub](https://github.com/eclipse-ee4j/concurrency-api)) are:

### 1. Deployable Managed Objects {#h3-8-1-deployable-managed-objects}

Currently all Managed Objects (other than the default ones) must be created by the application server administrator.  

Jakarta EE 9 has deployable application scoped data sources. What is suggested here is a feature whereby you could set up your own application scoped and configured managed executor services. These would be deployed using annotations.  

If you have an application which really needs to have fine-grained control of the threading, concurrency and pooling of threads, this feature will allow you to set these processes up without needing administration consoles.

### 2. New @Asynchronous annotation {#h3-9-2-new-asynchronous-annotation}

Currently there are annotations in Jakarta EE to indicate asynchronous execution, but they are quite specific to each individual specification. This suggested new annotation could be used with CDI Beans. Adding and being able to configure executor service pools will enable fine grained concurrency management when combined with deployable executor services. You could choose different thread pools for different methods, for example.  

This new feature could eventually result in a single @Asynchronous annotation common to all the specifications. This would simplify the platform and make it more unified.

### 3. Catch up with java.until.concurrent {#h3-10-3-catch-up-with-java-until-concurrent}

Jakarta Concurrency was released in Java EE 7, so has not been brought up to date to match concurrency primitives brought in with Java SE 9, 11 and 17. Some of these include support for the ForkJoinPool in a standard way, and updated APIs for current Java SE Managed Executor Services.

Get Involved! {#h2-11-get-involved}
-----------------------------------

As the project lead of Jakarta Concurrency, my goal is not to define its future, but lead a community group that can drive the specification forwards. In short, I need your help to make the specification happen!  

Each specification needs to produce a specification document, detailing how you use the API; an API JAR, to be coded against as a developer; and a Technology Compatibility Kit (TCK), the test suite that is used to determine whether independent implementations of the API meet its requirements.  

You can get involved in each and every part of the process. For example, if you are interested in testing, you could help build tests or maintain the TCK. You can also help directly on the API, working out what capabilities you'd want as a developer. There is also help needed around the specification document, from submitting PRs for typos to higher level input.

The whole project is on GitHub, you can make your API changes there:

[Jakarta Concurrency GitHub](https://blog.payara.fish/cs/c/?cta_guid=b5b79395-a545-4034-a65f-68bfd79ae830&signature=AAH58kG0vV3O0eRO8EDaTiAoxKFALjOLAQ&pageId=57864145848&placement_guid=048bbebf-3f2e-4cee-9e14-4351a347363a&click=a9ae683e-46fc-4564-a5d5-3a506cb9a322&hsutk=cdd9ba21ef2c55c25f8a5332cfbce980&canon=https%3A%2F%2Fblog.payara.fish%2Fjakarta-concurrency-present-and-future&utm_referrer=https%3A%2F%2Fblog.payara.fish%2Fpage%2F5&portal_id=334594&redirect_url=APefjpHnZm9_Jrpm83Zlh-xwfRpHXN4HuebVdWObUwAUbiIw58R0i13D6DhKWWEs7GBWxTdgq6UpJ7_7KV_ywLF0s3QVXXgZdMTRHbELO2RTpCpCUXwWXRa_wT8vUIb9G7wyATIbVe-ZqDdh8F7NDj34T5eaP9qP87VJ1Kk8pdxuMsOrmJfbwsf4mFRp48td2lQZl305TbA__t6sObbSXVYZ7EPVrk1c7C13PZi52i1Hr6T5Gj08fm2egM_-Ozg1-Uz79X8om0maXAj05xRRlCX4up-2wWr4vh36wsgq_2Zrxi-jmew6rhs&__hstc=229474563.cdd9ba21ef2c55c25f8a5332cfbce980.1646049199202.1657115596720.1657119580530.147&__hssc=229474563.15.1657119580530&__hsfp=812266229&contentType=blog-post)

Find the Eclipse Project here:

[Jakarta Concurrency Eclipse Project](https://blog.payara.fish/cs/c/?cta_guid=9c9e841d-0734-406b-95a5-75ae0fc37d90&signature=AAH58kHtCdMQ4sYkeVKN22WYhuzSGa1Leg&pageId=57864145848&placement_guid=9fda33e2-d0a9-41fa-a8c7-bbebc1f9ac61&click=15719ccc-0ead-43c8-8e8f-e5b9fa4f99c3&hsutk=cdd9ba21ef2c55c25f8a5332cfbce980&canon=https%3A%2F%2Fblog.payara.fish%2Fjakarta-concurrency-present-and-future&utm_referrer=https%3A%2F%2Fblog.payara.fish%2Fpage%2F5&portal_id=334594&redirect_url=APefjpElkNuzHc1iQPCQ4ZpbtWeNJi2sLFdqYOEZUF08M0rgJmYqIdlSX-rsizZv-mhsmOHLVWoe_PHlim07K8uupYcdGd9HH-f8_FBdTApiMC-UEIf7rxSEFue9H7566eMmDhlj_5R2e1RLddV7Wj78Vgo9If4-OoDPeC17DGfPtA5KXRGXhIrJgGDN7CY_JfWcc2unKL1OoRvFQFfGi2Ymmh-A62PzbA128JcW1gK-cJzFggKV0pxjt2Lm_EaLNTTgmQDMxYRrSEYhhLYp7Sm3irqNZpdKiA&__hstc=229474563.cdd9ba21ef2c55c25f8a5332cfbce980.1646049199202.1657115596720.1657119580530.147&__hssc=229474563.15.1657119580530&__hsfp=812266229&contentType=blog-post)

Join the Mailing List:

[Jakarta Concurrency Mailing List](https://blog.payara.fish/cs/c/?cta_guid=12c3b36a-816d-42f5-bf72-7b787e3db30f&signature=AAH58kGCye2vUnmy1wwvBriT3uFkKYzNxA&pageId=57864145848&placement_guid=2da34d30-ae36-45f2-a7eb-2da1f8b11bf3&click=e1d7ac1e-7b01-4d4a-b153-bcee7d4e24a3&hsutk=cdd9ba21ef2c55c25f8a5332cfbce980&canon=https%3A%2F%2Fblog.payara.fish%2Fjakarta-concurrency-present-and-future&utm_referrer=https%3A%2F%2Fblog.payara.fish%2Fpage%2F5&portal_id=334594&redirect_url=APefjpE0rwBLrvv3xbYEz9DgOk4Jyj21uVGQ6LSDhiSQD1Wcre9ZtpifGYba6SRJJ-_rL3k7ItQjpFObPRH8mbL0ywo_MKLSSVhYLG4R0LDtp_S1xRZeiURQanwQYrQXvXRjlqlUJTMe9rnv-ISYNOUWoO-aMwayTt1P8qNuTv96b2T3m2l85jWvw82NMux6wOlDix8Cf0YABvZtj0DDc67PyjnAZjBPA3aLdskiAOcUpGgqBujgA2rFrJX5DTsf0TRdNiGLWrte-cQrrnVB7xAyhDqRCZPmWuvD2KearS8cA4RXUPrXrNc&__hstc=229474563.cdd9ba21ef2c55c25f8a5332cfbce980.1646049199202.1657115596720.1657119580530.147&__hssc=229474563.15.1657119580530&__hsfp=812266229&contentType=blog-post)  

There is also a compatible implementation on GitHub, used in GlassFish and Payara:  
[Concurrency Compatible Implementation](https://blog.payara.fish/cs/c/?cta_guid=ad60f698-4df1-4204-ae7b-5b01977905ba&signature=AAH58kExyLJ6RaMqVhlBvvSTwic9IxWwKg&pageId=57864145848&placement_guid=02da90b0-c672-4772-ab5d-f12448e47ad8&click=23c1d463-7832-4259-ba32-afa40df11e52&hsutk=cdd9ba21ef2c55c25f8a5332cfbce980&canon=https%3A%2F%2Fblog.payara.fish%2Fjakarta-concurrency-present-and-future&utm_referrer=https%3A%2F%2Fblog.payara.fish%2Fpage%2F5&portal_id=334594&redirect_url=APefjpHIsbqBdxoNpBBkde04khz5CXYall1lf-zpgvhZc1TXWaJdsTCBEIk1QsyNktK-f7RCL2r34saeKUY1y00Gg3f_-DRXPR5HmsRBGNx8NL3km5egRXngaumDyKg75kI8qrCtFU_8dStUYtIyvd9aPjihvI5wIplIZYBJVLhRTTOdoreBRvJ10LPr6N3fyplw3tcZ7hGhxSkZDbE3AlTzmd3J1WK1kiBnoxNbUYklHrT7HvLZ1ZtvoA-BpKmQAk2vzE9tqZmSvq30XC-iH2Xhx39xPYUeN108r_kw98pkouyMBZkpNbs&__hstc=229474563.cdd9ba21ef2c55c25f8a5332cfbce980.1646049199202.1657115596720.1657119580530.147&__hssc=229474563.15.1657119580530&__hsfp=812266229&contentType=blog-post)

It can seem daunting to become involved with an open source project, but Jakarta Concurrency is actively asking for help of all kinds. If in doubt - get involved!
