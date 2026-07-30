---
title: "New Features in Jakarta EE 11, with Examples"
slug: "new-features-in-jakarta-ee-11-with-examples"
date: "2025-08-11T06:40:56+00:00"
lastmod: "2025-08-11T06:42:31+00:00"
description: "Launched on June 26, 2025, Jakarta EE 11 introduces one brand-new specification and updates 16 existing ones."
canonical: "https://omnifish.ee/new-features-in-jakarta-ee-11-with-examples/"
authors:
  - "ondro-mihalyi"
image: "/images/posts/2025/08/new-features-in-jakarta-ee-11-with-examples/omnifish-logo-turquise-bg-400px-e1663869726664.png"
categories:
  - "Developer Tools"
  - "Jakarta EE"
  - "Tools"
tags:
related_posts:
  - "10-best-practises-for-jakarta-ee-performance-optimization"
  - "are-java-jakarta-ee-application-servers-heavy"
  - "a-simple-service-with-spring-boot"
  - "book-review-persistence-best-practices-for-java-applications"
enlighterjs: true
frozen: false
---

Jakarta EE 11 is a major update in the enterprise Java world. It offers key changes that enhance developer productivity and modernize the platform. {#h2-0-jakarta-ee-11-is-a-major-update-in-the-enterprise-java-world-it-offers-key-changes-that-enhance-developer-productivity-and-modernize-the-platform}
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

This release introduces several new features and improvements. These streamline workflows and integrate the latest Java advancements.

**Key Features of Jakarta EE 11** {#h2-1-key-features-of-jakarta-ee-11}
-----------------------------------------------------------------------

{{< youtube TUc_oOJb2PA >}}

1. **Java 21 Virtual Threads** : This update introduces Virtual Threads from Java 21. They enable lightweight concurrency for handling thousands of simultaneous tasks. This improves performance and scalability. Learn more about [**Java 21 Virtual Threads**](https://omnifish.ee/glassfish-now-runs-on-jdk-21/).
2. **Jakarta Data** : This new specification simplifies database access with the repository pattern, which reduced a lot of boiler plate code. Explore more about [**Jakarta Data**](https://omnifish.ee/jakartaee_logo_compatible/).
3. **New features in existing APIs:** Several APIs were updated to enhance existing features or add new features. For example. Jakarta Persistence 3.2 saw a lot of improvements (see the [long list here](https://jakarta.ee/specifications/persistence/3.2/) or an article [about most of them here](https://www.baeldung.com/jakarta-persistence-3-2). Many other APIs like Concurrency, Security, Faces, Servlet, or CDI received many enhancements as well.
4. **Java 17+ Compatibility**: This release aligns with Java 17 and 21. Besides virtual threads, it also supports Java Records introduced in Java 17. This ensures developers can leverage the latest Java features and run on up-to-date Java versions.
5. **Modernized APIs**: Jakarta EE 11 also replaces outdated Managed Beans with CDI and removes SecurityManager references. This aligns the platform with current best practices and Java features.

These updates focus on streamlining workflows. They improve performance and integrate modern Java features. This makes Jakarta EE 11 a powerful choice for enterprise and cloud-native applications.

[Eclipse GlassFish](https://omnifish.ee/glassfish/) is the first runtime that provide all Jakarta EE features. You can [download the 8.0.0-M12](https://download.eclipse.org/ee4j/glassfish/) or a newer milestone version to try out all these new features, until the final GlassFish 8 version is released. Note that Jakarta Data is not yet supported natively in 8.0.0-M12 and will be supported in the 8.0.0-M13 version soon, with an implementation provided by the OmniFish engineering team. Besides that, the [OmniFish company is leading the whole development of Eclipse GlassFish](https://omnifish.ee/), ensuring that it's reliable, secure and production-ready, and yet bringing joy to developers when they build applications with GlassFish.

**Jakarta Data: Simplified Data Access** {#h2-2-jakarta-data-simplified-data-access}
------------------------------------------------------------------------------------

Jakarta Data is introduced in Jakarta EE 11. It simplifies database operations by providing a streamlined abstraction layer over Jakarta Persistence, Jakarta NoSQL, and other data sources.

### **What is Jakarta Data?** {#h3-3-what-is-jakarta-data}

Jakarta Data introduces standardized repository patterns. These minimize boilerplate code and separate persistence logic from domain models. This separation enhances productivity and code quality while ensuring flexibility across different data storage options.

Key features of Jakarta Data include:

* **Repository Interfaces** : Allows defining database queries via method definitions on Java interfaces. Provides three main repository interfaces---**DataRepository** , **BasicRepository** , and **CrudRepository**---designed for various common scenarios.
* **Compile-Time Generation**: Allows using Java Annotation Processing to generate implementations during compilation. This catches errors early, improves development speed, as well as application startup speed.
* **Pagination Support** : Offers both offset-based and cursor-based pagination through **Page** and **CursoredPage** interfaces. This caters to different performance and consistency needs.

### **Jakarta Data Code Examples** {#h3-4-jakarta-data-code-examples}

Implementing Jakarta Data in a Jakarta EE 11 project is straightforward. Below are some code examples illustrating its usage:

**Simple Repository Setup:**

```

```

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Repository
public interface ProductRepository {
  @Find List&lt;Product&gt; findByCategory(String category);
  @Find Optional&lt;Product&gt; findByName(String name);
  @Delete void deleteByCategory(String category);
}</pre>

The above repository definition will create a repository with 3 methods, which can be called to find or delete entities. For faster bootstrapping, Jakarta Data provides several repositories to add commonly used methods. For example, if you extend from the [BasicRepository](https://jakarta.ee/specifications/platform/11/apidocs/jakarta/data/repository/basicrepository) interface, you get additional methods for finding, deleting, and saving entities out of the box:

```

```

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Repository
public interface ProductRepository extends BasicRepository&lt;Product, Long&gt; {
  @Find List&lt;Product&gt; findByCategory(String category);
  @Find Optional&lt;Product&gt; findByName(String name);
  @Delete void deleteByCategory(String category);
}</pre>

With the above definition, we get a few more methods to call, without defining them in our custom interface. For example, we can find all entities as a Stream result:

```

```

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Inject ProductRepository productRepository;

Stream&lt;Product&gt; products = productRepository.findAll();</pre>

Or we can save a product entity:

```

```

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Inject ProductRepository productRepository;

Product product = new Product();
productRepository.save(product);</pre>

**Entity Definition:**

```

```

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Entity public class Product {
  @Id @GeneratedValue private Long id; 
  private String name; 
  private String category; 
  private BigDecimal price; 
  // Constructors, getters, and setters
}</pre>

**CRUD Operations:**

```

```

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Repository public interface OrderRepository extends CrudRepository&lt;Order, Long&gt; {
  @Insert Order createOrder(Order order);
  @Update Order updateOrder(Order order);
  @Query("SELECT o FROM Order o WHERE o.status = :status AND o.createdDate &gt; :date") List&lt;Order&gt; findRecentOrdersByStatus(String status, LocalDateTime date); 
}</pre>

These example shows how to create a repository that inherits various common CRUD operations from the Crudrepository interface, while adding more domain-specific methods createOrder and updateOrder, and a custom query method defined by a Jakarta Data Query Language (JDQL). This is roughly a subset of the Jakarta Persistence Query Language (JPQL) plus it allows some to omit some parts of the query which can be derived from the method name or the repository definition (e.g. "SELECT" or "FROM" clauses can be omitted if the entity type is defined by the return type.

**Updated APIs and Programming Model** {#h2-5-updated-apis-and-programming-model}
---------------------------------------------------------------------------------

Jakarta EE 11 modernizes enterprise development by transitioning from older patterns to contemporary Java features. This simplifies and strengthens the development process.

### **Moving from Managed Beans to CDI** {#h3-6-moving-from-managed-beans-to-cdi}

Jakarta EE 11 replaces Jakarta Faces Managed Beans with CDI (Contexts and Dependency Injection) beans. This transition unlocks advanced features previously unavailable with Managed Beans. These include interceptors, conversation scopes, events, and type-safe injection.

**Steps for Migration:**

* Add an empty `beans.xml` file to the `WEB-INF` directory.
* Replace JSF `@ManagedBean` annotations with CDI `@Named` annotations.
* Update JSF scope annotations to their CDI counterparts from `jakarta.enterprise.context`.
* Substitute JSF `@ManagedProperty` with CDI `@Inject` annotations.

This migration unifies dependency injection, scope management, and integration across the Jakarta EE platform.

### **Support for Java Records and Pattern Matching** {#h3-7-support-for-java-records-and-pattern-matching}

Jakarta EE 11 aligns with Java 17 and beyond, introducing support for Java Records and Pattern Matching in enterprise development. Records simplify code by reducing boilerplate and ensuring data integrity. Pattern Matching enhances code readability and reliability.

**Example: Using Java Records in Jakarta EE 11**

```

```

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public record CustomerDTO(Long id, String name, String email, LocalDateTime registrationDate ) {}

@Path("/customers")
public class CustomerResource {
  @Inject private CustomerService customerService;

  @GET @Path("/{id}") @Produces(MediaType.APPLICATION_JSON)
  public CustomerDTO getCustomer(@PathParam("id") Long id) {
    Customer customer = customerService.findById(id);
    return new CustomerDTO( customer.getId(), customer.getName(), customer.getEmail(), customer.getRegistrationDate() );
  }
}</pre>

### **Removing SecurityManager References** {#h3-8-removing-securitymanager-references}

Jakarta EE 11 simplifies security by removing all references to the outdated SecurityManager. This aligns with Java's broader ecosystem changes and prepares for removal of Security Manager support in future Java versions. Developers are encouraged to adopt modern security practices. These include container-based sandboxing, OS-level security features, static analysis tools, and secure coding practices.

**New Java Features and Performance Improvements** {#h2-9-new-java-features-and-performance-improvements}
---------------------------------------------------------------------------------------------------------

Jakarta EE 11 integrates advanced Java features, aligning with Java 17 and beyond and introducing enhancements tailored for Java 21.

### **Java 17+ and Java 21 Compatibility** {#h3-10-java-17-and-java-21-compatibility}

Jakarta EE 11 supports Java 17 and higher, incorporating key updates from Java 21, such as Virtual Threads and pattern matching. Both Java 17 and Java 21 are Long-Term Support (LTS) releases. They offer stability and extended support for enterprise environments.

### **Virtual Threads and Better Concurrency** {#h3-11-virtual-threads-and-better-concurrency}

Virtual Threads, introduced through Jakarta Concurrency 3.1, revolutionize concurrency handling in enterprise applications. These lightweight threads enable applications to manage thousands of concurrent tasks with minimal resource overhead. This makes them ideal for high-volume request scenarios.

**Example Configuration for Virtual Threads:**

```

```

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@ManagedExecutorDefinition( name = "java:comp/vtExecutor", maxAsync = 10, context = "java:comp/vtContextService", virtual = true ) @ContextServiceDefinition( name = "java:comp/vtContextService", propagated = {SECURITY, APPLICATION} ) @ManagedThreadFactoryDefinition( name = "java:comp/vtThreadFactory", context = "java:comp/vtContextService", virtual = true ) @ManagedScheduledExecutorDefinition( name = "java:comp/vtScheduleExecutor", context = "java:comp/vtContextService", maxAsync = 10, virtual = true )
@ApplicationScoped public class VirtualThreadAsyncConfig { }</pre>

### **Impact on Modern Development** {#h3-12-impact-on-modern-development}

Virtual Threads enhance scalability and efficiency in cloud-native and serverless environments. Virtual threads improve service-to-service communication in microservices and optimize resource utilization in distributed systems, without introducing reactive programming, which itself brings a lot of issues with debugging, troubleshooting, incompatibilities and maintenance.

**Enhancements in the current APIs** {#h2-13-enhancements-in-the-current-apis}
------------------------------------------------------------------------------

Here's the full list of APIs that received updates:
![](/images/posts/2025/08/new-features-in-jakarta-ee-11-with-examples/image-4-1024x707-1.png)

We'll quickly go through some of the most interesting enhancements.

### **Persistence** (JPA) {#h3-14-persistence-jpa}

Key new features:

* Programmatic configuration -- configure persistence units in code, without persistence.xml
* Schema Manager API -- manage schema programmatically, without scripts referenced in persistence.xml
* Safe run-in-transaction API -- run a Java lambda in a transaction, without automatic commit or rollback on exception
* Streamlined JPQL -- implicit entity references, `this` variable, simplifies the query syntax for simpler usecases
* New functions in queries -- more powerful way to express queries, without deferring to native SQL
* Type-safe options instead of "Stringly" typed hints -- adds type safety to the API when providing optional hints to the entity manager
* Support for new Java types and features -- mapping of `Instant` and `Year` types supported out of the box, Java records can be embedded or used as an `@IdClass`

**Some examples:**

```

```

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// Programmatic configuration
var emf =
        new PersistenceConfiguration()
                .name("MyZoo")
                .nonJtaDataSource("java:global/jdbc/MyZooData")
                .managedClass(Animal.class)
                .property(PersistenceConfiguration.LOCK_TIMEOUT, 1000)
                .createEntityManagerFactory();

// Programmatic schemamanipulation
emf.getSchemaManager().create(true);

// Run in transaction programmatically
var tiger = new Animal("tiger");
emf.runInTransaction(em -&gt; em.persist(tiger));

// Simpler JPQL - find tigers without entity variable, without select clause
var tigers = em.createQuery("from Animal where type = 'tiger'", Animal.class).getResultList();

// Simpler JPQL - use this variable if entity variable not defined
var animalTypes = em.createQuery("select distinct (this.type) from Animal", String.class).getResultList();
</pre>

For more examples and details, refer to [this article](https://in.relation.to/2024/04/01/jakarta-persistence-3/) about Jakarta Persistence 3.2 by one of its project leads, Gavin King.

### **Context and Dependency Injection (CDI)** {#h3-15-context-and-dependency-injection-cdi}

* Method Invokers -- CDI extensions can define an invoker that can be used by the application or a framework to execute methods and automatically resolve bean instance and arguments for the method. Allows frameworks to invoke methods on CDI beans as action handlers.
* @Priority can be specified on producer methods -- to add priority to produced beans in the same way as to beans defined by a class

**Some examples:**

```

```

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// Invoke a method with an instance of HttpServletRequest injected as a method argument
@Dependent
public class MyService {
    public String hello(HttpServletRequest request) {
        return "Hello " + request.getParameter("name") + "!";
    }
}

/* Build an invoker in a CDI extension and then invoke the methodon an injected MyService instance (first argument is null), with HttpServletRequest 
injected as the first argument (the invoker must be configured 
in the CDI extension to inject the argument */
invoker.invoke(null, null)</pre>

### **Security** {#h3-16-security}

* Qualifiers for built-in authenticatoin mechanisms -- allows injecting them and call them programmatically
* new `HttpAuthenticationMechanismHandler` -- to replace the default handler for complete customization
* In-memory identity store -- for early development stages and tests

**Some examples:**

You can ind more information about changes in CDI 4.1 in this blog article.

```

```

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// a custom handler that forwards to one of the injected built-in mechanisms

@Alternative // needs to replace the default handler
@Priority(APPLICATION)  // needed to apply this alternative handler
@SessionScoped // scoped to the session - authentication applied only once per session
public class SelectableAuthenticationMechanismHandler implements HttpAuthenticationMechanismHandler, Serializable {

    @Inject
    @CustomFormAuthenticationMechanism
    HttpAuthenticationMechanism formAuthMechanism;

    @Inject
    @OpenIdAuthenticationMechanism
    HttpAuthenticationMechanism oidcAuthMechanism;

    String previousAuth = "";

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) throws AuthenticationException {
    return switch (authType) {
            case "oidc" -&gt;
                oidcAuthMechanism.validateRequest(request, response, httpMessageContext);
            case "form" -&gt;
                formAuthMechanism.validateRequest(request, response, httpMessageContext);
                ...</pre>

### **Concurrency** {#h3-17-concurrency}

Besides support for virtual threads in concurrency resources, the Concurrency API brings some more enhancements:

* `@Inject` can now be used in place of `@Resource` -- unifies injecting concurrency resources under the CDI programming model
* Schedule repeated executions of a method -- using the `runAt` attribute of the `@Scheduled` annotation on a method
* Integration with Java 9 Flow reactive streams

**Some examples:**

```

```

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// A method that will be executed repeatedly according to a schedule after it's called
@ApplicationScoped
public class Schedules {

  @Asynchronous(runAt = {
    @Schedule(daysOfWeek = { DayOfWeek.MONDAY, DayOfWeek.FRIDAY }, hours = 8),
    @Schedule(daysOfweek = DayOfWeek.SUNDAY, hours = 10, minutes = 30)
  )
  public void databaseCleanup() {
      cleanUp();
  }
}

// Execute the method at startup to trigger the scheduled execution
@ApplicationScoped
public class AtStartup {

  @Inject Schedules schedules;

  public void onStart(@Observes @Initialized(ApplicationScoped.class) Object startEvent) {
    schedules.databaseCleanup();
  }
}</pre>

Check out the following video presentation with more details and demos of new features in Jakarta Concurrency:

{{< youtube jtNqZz-WkDk >}}

### **Faces (JSF)** {#h3-18-faces-jsf}

* Add `rowStatePreserved` attribute to `<ui:repeat>` -- to align it with `<h:dataTable>`, which already supports it
* The current Faces Flow can be injected as a CDI bean -- inject the current flow instead of calling a several methods on the current FacesContext instance
* Default converter for `java.util.UUID` type -- to support bean properties of type UUID out of the box and align with Jakarta Persistence, which introduced support for UUID as entity keys

More info about the changes in Jakarta Faces 4.1 in [this article](https://balusc.omnifaces.org/2024/06/whats-new-in-faces-41.html#uiRepeatRowStatePreserved) by one of the most active members of the Jakarta Faces specification team, Bauke Scholtz.

**Conclusion** {#h2-19-conclusion}
----------------------------------

Launched on June 26, 2025, Jakarta EE 11 introduces one brand-new specification and updates 16 existing ones. It addresses technical challenges and modernizes the platform. Jakarta EE 11 balances innovation with stability. Features like Jakarta Data and Virtual Threads are introduced while maintaining backward compatibility.

This release boosts productivity by cutting down on boilerplate code and improving testing with updated TCKs. Its alignment with Java SE 17 and 21 unlocks modern programming tools. This makes code cleaner and easier to maintain.

For enterprises, Jakarta EE 11 offers flexibility to support both cloud-native microservices and traditional enterprise systems. Its standard APIs guarantee consistency and portability across implementations. This creates a strong foundation for future advancements.

Looking ahead, [**Jakarta EE 12**](https://jakartaee.github.io/platform/jakartaee12/JakartaEE12ReleasePlan)is scheduled for 2026. It promises support for Java SE 21 and 25, and it plans to focus on unification of aspects like Java modules (JPMS) support, and improved consistency between APIs by introducing common specifications for query languages, HTTP protocol to Java bindings, etc. It's also expected that MicroProfile will blend into Jakarta EE, which will bring a common configuration API to Jakarta EE as well as reunite some APIs that naturally fit together and could be better integrated if under the same Jakarta EE roof (e.g. MicroProfile REST Client and Jakarta RESTful WEb Services).

**FAQs** {#h2-20-faqs}
----------------------

### **What new features in Jakarta EE 11 boost developer productivity?** {#h3-21-what-new-features-in-jakarta-ee-11-boost-developer-productivity}

Jakarta EE 11 introduces several features that enhance developer productivity. These include support for modern Java versions (Java 17+), Virtual Threads from Java 21, and the Jakarta Data specification. These features improve scalability, reduce resource usage, and simplify database operations.

### **What are the main differences between traditional threads and Virtual Threads in Jakarta EE 11?** {#h3-22-what-are-the-main-differences-between-traditional-threads-and-virtual-threads-in-jakarta-ee-11}

Virtual Threads, introduced in Jakarta EE 11, are lightweight and managed by the Java runtime. Unlike traditional threads, they don't rely on OS-level management and don't demand significant system resources. Virtual Threads enable massive concurrency. This makes them ideal for server-side applications where scalability and efficient resource usage are critical. Although virtual threads were released in Java 21, they were risky to use in some cases. Since Java 24, most of these risky scenarios were addressed and it's safe to use virtual threads in standard enterprise Java applications.

### **What are the benefits of switching from Managed Beans to CDI in Jakarta EE 11 for enterprise applications?** {#h3-23-what-are-the-benefits-of-switching-from-managed-beans-to-cdi-in-jakarta-ee-11-for-enterprise-applications}

Switching from Managed Beans to CDI in Jakarta EE 11 offers several benefits. These include type-safe dependency injection, better lifecycle management, and a unified model for enterprise applications. CDI enhances modularity, maintainability, and testability. This improves developer efficiency and application stability.

<br />
