---
title: "Building a declarative API with Spring AOP and SpEL"
slug: "building-a-declarative-api-with-spring-aop-and-spel"
date: "2025-02-12T15:57:11+00:00"
lastmod: "2025-02-12T15:59:07+00:00"
description: "See how to implement a declarative API with Spring AOP and SpEL to perform tasks belonging to cross-cutting concerns like auditing."
canonical: "https://lemikaelf.github.io/blog/posts/spel-arguments/"
authors:
  - "mikael-francoeur"
image: "spel.png"
categories:
  - "Java"
  - "Spring"
  - "Tutorials"
tags:
related_posts:
  - "aspect-oriented-programming-aop"
  - "monkey-patching-in-java"
  - "who-instruments-the-instrumenters"
  - "jurassic-jdk-migrate-or-extinct"
frozen: false
---

**In this article, we'll implement a declarative API to perform tasks belonging to cross-cutting concerns, using auditing as an example. We'll see how SpEL and Spring AOP allow us to easily intercept invocations throughout a code base and perform arbitrary logic in an expressive and non-invasive manner.**

## A story about legacy

Let's pretend that we inherited a legacy system. The system is in a dismal state, with every change always at risk of bringing the whole thing down. One day, there is a business requirement to audit a few dozen methods scattered throughout the code base. After every invocation of these methods, we should notify an external system by sending a client id and an operation (`1234`, `"CREATE_USER"`).

The detailed requirements for our auditing example are:

* there are a couple dozen methods to audit, scattered throughout the code base
* the auditing logic is always the same: send the client id and action to an external system
* the client id is always in one of the method arguments, but is sometimes nested inside one of numerous types
* sometimes, a method receives multiple client ids, all of which need to be audited.

One way to change such a system is to identify "seams": "a place where you can alter behavior in your program without editing in that place."[(1)](#ref1) Fortunately for us, our legacy system is built using Spring, so we can rely on this. In Spring, aspect-oriented programming (via Spring AOP) is a good way of exploiting seams, because AOP is by definition non-invasive, in the sense that you don't have to edit the advised code when declaring new aspects.

This screams "cross-cutting concern": something that is scattered throughout the code base, with one unique action applying to many methods. And cross-cutting concerns is just the use case for aspect-oriented programming. Think of the Spring Framework's caching support. It has a single caching logic, but it can vary its cache names, be applied throughout the code base, and deal with nested types:

```java
public record MyPojo(Nested nested) {
}

public record Nested(String value) {
}

@Cacheable(cacheNames = "myCache", key = "#argument.nested.value")
public Entity fetchEntity(MyPojo argument) {
  return client.fetchEntity(argument);
}
```

That's pretty similar to what we want. And the good thing is, Spring exposes all the required tooling to build a similar API for ad-hoc use cases, not just for framework-related logic. Here is the declarative auditing API we are going to build in this article.

```java
@Audit(action = AuditAction.DISABLE_USER, expression = "#requests.![clientId]")
public void disableUsers(List<DisableRequest> requests) {
  // ...
}
```

This annotation declares that the `DISABLE_USER` action will be audited with the client ids that are in the `clientId` property of every `UserDisableRequest` in the argument list. All in a single line that's expressive, concise, and non-invasive. In the rest of this article, we'll see how to implement this (spoiler: it's fairly straightforward)...

## Evaluating SpEL expressions

First, a quick primer on SpEL. The expression in the annotation above (`#requests.![clientId]`) can be a bit mysterious. SpEL (Spring Expression Language) is a DSL that's built into the Spring Framework. Its main use in modern Spring applications is in one-liners that either drill down in a value (like what we did), or that compute a result from variables. And although SpEL is [well documented](https://docs.spring.io/spring-framework/reference/core/expressions/language-ref.html), it's not as well known as some other components of the Spring Framework.

The `#` in `#requests.![clientId]` tells SpEL that the following identifier is a [variable](https://docs.spring.io/spring-framework/reference/core/expressions/language-ref/variables.html). Variables are arbitrary values  

that can be set on an `EvaluationContext`. More on that later. The rest of the expression, `.![clientId]`, is a [collection projection](https://docs.spring.io/spring-framework/reference/core/expressions/language-ref/collection-projection.html). It tells SpEL to extract the `clientId` property from each collection element, just like if we were to do:

```java
requests.stream().map(UserDisableRequest::getClientId).toList()
```

Evaluating a SpEL expression is simple. First, parse the expression. Second, optionally, construct an `EvaluationContext` so that the interpreter can access variables.[(2)](#ref2) Third, evaluate the expression:

```java
@Test
void basicSpelExpression() {
  String expression = "#mylist.size";

  Expression parsedExpression = new SpelExpressionParser()
    .parseExpression(expression);

  StandardEvaluationContext context = new StandardEvaluationContext();
  context.setVariable("mylist", List.of(1, 2, 3));

  assertThat(parsedExpression.getValue(context)).isEqualTo(3);
}
```

Already, this simple example can almost support our auditing use case. We just need to bind the method's parameters to SpEL variables. Luckily, Spring exposes `MethodBasedEvaluationContext`, a subclass of `EvaluationContext` that does just that.

```java
private void doSomething(String myArg) {
}

@Test
void methodParamBinding() throws NoSuchMethodException {
  String expression = "#myArg.length";

  Expression parsedExpression = new SpelExpressionParser()
    .parseExpression(expression);

  MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(
    new Object(),
    getClass().getDeclaredMethod("doSomething", String.class),
    new Object[]{"the argument"},
    new DefaultParameterNameDiscoverer());

  assertThat(parsedExpression.getValue(context))
    .isEqualTo(12);
}
```

This is all we need to bind the arguments of a method invocation to a SpEL evaluation context, parse the expression, and evaluate it. You might wonder what the `new Object()` is. It's the context's root object, but we don't need it for this, so we just pass in a dummy value. One small note, the classes have to be compiled with the [`-parameters` javac argument](https://docs.oracle.com/en/java/javase/23/docs/specs/man/javac.html), so that the parameter names aren't stripped away by the compiler. If you are using Spring Boot and the [Maven plugin](https://docs.spring.io/spring-boot/docs/3.1.3/maven-plugin/reference/htmlsingle/#using) or the [Gradle plugin](https://docs.spring.io/spring-boot/docs/3.2.5/gradle-plugin/reference/htmlsingle/#reacting-to-other-plugins.java), `-parameters` is on by default. Now let's put this into practice by wiring it into an aspect, so that our auditing logic runs after each annotated method.

## Implementing the audit advice

If you've never worked with Spring AOP (Aspect-Oriented Programming), let me catch you up very quickly. AOP is often used to implement cross-cutting functionality, such as metrics, tracing, security, or in our case, auditing. It has a special vocabulary: "aspects" are collections of behaviours called "advices" that you can have Spring run before, after, or around invocation of a bean's methods. The invocations you decide to instrument, or "advise", are called "joinpoints", and you select them with a special expression. This expression, called a "pointcut", is written in a [subset of the AspectJ language](https://docs.spring.io/spring-framework/reference/core/aop/ataspectj/pointcuts.html). This brief overview is sufficient for our needs.

Implementing an advice is simple. First, we declare the `@Audit` annotation, and annotate it with `@Retention(RetentionPolicy.RUNTIME)` so that it's available at runtime. Second, we enable the relevant machinery by adding `@EnableAspectJAutoProxy` on one of your configuration classes. This is optional if you're using Spring Boot, because the built-in `AopAutoConfiguration` will automatically take care of it. Third, we declare an advice by annotating a class with `@Advice` and adding it to the application context (in our case, using `@Component`). Fourth, we define our aspect with `@Around`, `@Before`, `@After`, `@AfterReturning`, or `@AfterThrowing`. And fifth, we define a pointcut using the AspectJ language.

```java
// 1. declare the annotation
@Retention(RetentionPolicy.RUNTIME)
public @interface Audit {
  AuditAction action();

  String expression();
}

// 2. enable the machinery
@Configuration
@EnableAspectJAutoProxy
class AuditConfig {
}

// 3. declare an aspect
@Aspect
@Component
class AuditAspect {

  // 4 and 5. declare the advice and its pointcut
  @AfterReturning("@annotation(auditAnnotation)")
  void audit(JoinPoint joinPoint, Audit auditAnnotation) {
  }
}
```

The pointcut uses the `@annotation` designator to select all methods annotated with `Audit`. Notice that the advice (the `audit()` method) accepts two parameters. The first one is the `JoinPoint`, that will contain information about the advised method. The second one is the `@Audit` annotation that is on the advised method. With these two arguments, we will be able to access the SpEL expression in the `expression` element of the annotation.[(3)](#ref3)

With just these few lines of code, Spring will run our `audit()` method after every successful invocation of a bean method that is annotated with `@Audit` (except for self-invocations, since Spring only intercepts those in `@Configuration` classes). Moreover, our `audit()` method will have access to the names and values of the advised method's argument. Now, let's see how we can use this information to implement our auditing logic.

The `audit()` method will look almost like what we saw before: parsing the expression, using a `MethodBasedEvaluationContext` to make parameters accessible to the SpEL runtime, and finally evaluating the expression and publishing the audit:

```java
// reuse what we can, for performance
private final SpelExpressionParser parser = new SpelExpressionParser();
private final DefaultParameterNameDiscoverer parameterNameDiscoverer
  = new DefaultParameterNameDiscoverer();

private final AuditService auditService; // inject the bean (constructor not shown)

@AfterReturning("@annotation(auditAnnotation)")
void audit(JoinPoint joinPoint, Audit auditAnnotation) {
  MethodSignature signature = (MethodSignature) joinPoint.getSignature();

  Expression expression = parser.parseExpression(auditAnnotation.expression());
  EvaluationContext context = new MethodBasedEvaluationContext(
    new Object(),
    signature.getMethod(),
    joinPoint.getArgs(),
    parameterNameDiscoverer);

  Collection<String> auditableIds = asStringCollection(expression.getValue(context));
  auditableIds.forEach(id -> auditService.audit(auditAnnotation.action(), id));
}
```

There's one finicky part to this: SpEL is a dynamically typed language, so we can't be sure that the return value will be of the correct type. In our case, we want to allow `String`, for a single client id, and `Collection<String>`, in case multiple client ids need to be audited. This is what the `asStringCollection(Object)` method does:

```java
private Collection<String> asStringCollection(Object result) {
  Objects.requireNonNull(result, "expression of @Audit evaluated to null");

  return switch (result) {
    case String string -> List.of(string);
    case Collection<?> collection -> {
      collection.forEach(element -> Assert.isInstanceOf(String.class, element,
        () -> "@Audit expression evaluated to collection with non-string element"));
      yield (Collection<String>) collection;
    }
    default -> throw new RuntimeException(
      "@Audit expression evaluated to non-string type %s"
        .formatted(result.getClass().getName()));
  };
}
```

And voilà! We now have a working declarative auditing API that can handle multiple audit events, as well as one or many client ids coming from arbitrary properties of the audited methods' arguments. This code works, but you shouldn't take my word for it. Instead, I want to show how we can test this with a lightweight test that will run almost as fast as a unit test.

## Testing the audit aspect

Testing aspects involves starting up a Spring context, as well as the aspect-related machinery that we enabled in the application using `@EnableAspectJAutoProxy`. We can do this easily with `@ExtendWith(SpringExtension.class)`, or `@SpringBootTest(classes = AuditAspect.class)`. If you use `@SpringBootTest`, don't forget to specify at least one class in the `classes` element, so that the test doesn't load the whole application.

There's a few ways of testing aspects, but one way I like is to define a new test bean that will be advised. That way, we can more flexibly test the aspect, since it's easy to define more methods and test arbitrary expressions if the annotated class belongs to the test. Here is a minimal setup for such a test:

```java
@ExtendWith(SpringExtension.class) // boot a very minimal Spring context
@Import({
  AuditAspect.class, // make our aspect a bean
  AuditConfig.class, // we check that our config class 
  // correctly enables the aspect machinery
  AuditAspectTest.TestAuditable.class // this is the test bean
})
class AuditAspectTest implements WithAssertions {

  private static final String USER_ID = "userId";

  // this annotation is new to Spring Framework 6.2,
  // it replaces Spring Boot's @MockBean
  @MockitoSpyBean
  private TestAuditable auditable;

  @MockitoBean
  private AuditService auditService;

  @Test
  void itAuditsTheIdWhenTheExpressionEvaluatesToAString() {
    auditable.createUser(USER_ID);

    verify(auditService).audit(AuditAction.CREATE_USER, USER_ID);
  }

  static class TestAuditable {
    // we can now declare any method, with any expression 
    @Audit(action = AuditAction.CREATE_USER, expression = "#userId")
    void createUser(String userId) {
    }
  }
}
```

I'm not showing the full thing here, but such a test should verify that:

* the audit runs after the method completes normally
* the audit doesn't run after the method throws
* the audit throws an exception if the expression evaluates to something other than a `String` or a `Collection<String>`

You can view the full test suite, including these test cases, along with the rest of the code for this article [here](https://github.com/LeMikaelF/spel-auditing-blog).

## Improvements

I've tried to keep this article simple, so I've left out some improvements. First, ideally the aspect would parse the expression strings only once per expression, and cache the result, since parsing can be a costly operation. This can easily be achieved with a `ConcurrentHashMap<String, Expression>`. Second, in the odd case where the expression would be malformed, or wouldn't evaluate to an acceptable type, depending on your requirements, you may want to prevent the execution of the method. In that case, the advice should be an `@Around` method and first evaluate the expression, before invoking the join point. `@Around` advices are a little different, but they're [well documented](https://docs.spring.io/spring-framework/reference/core/aop/ataspectj/advice.html#aop-ataspectj-around-advice).

One other possible improvement is that if the process exits after the advised method has run, but before it has been audited, the audit will never be published. This might be acceptable for you. If it isn't, there are a few solutions. One solution is to wrap the audited behaviour and the auditing code in a transaction, if the architecture allows it. Another solution is to use an [outbox pattern](https://microservices.io/patterns/data/transactional-outbox.html).

The method `asStringCollection` checks the types of all the elements returned by the expression. If you have [Apache Commons Collections](https://commons.apache.org/proper/commons-collections/) in your dependencies, you can use `CollectionUtils.typedCollection(Collection, Class)` to abstract this away.

In this article, I showed how we can leverage SpEL and Spring AOP to create a declarative auditing API that is expressive, flexible, and non-invasive. This pattern can be used for other cross-cutting requirements such as tracing, metrics, logging, or security. In fact, Spring Security already provides `@PreAuthorize`, `@PostAuthorize`, `@PreFilter`, and `@PostFilter` annotations that follow this pattern.

One final thing I would like to stress is that such an API should be tested just as much as anything written in imperative Java. It is easy to make mistakes, such as annotating an interface instead of its implementation (not supported by the AspectJ language), forgetting to enable aspects with `@EnableAspectJAutoProxy` in a non-Boot application, or annotating a method that's not intercepted by a JDK proxy (see the note in  

the [documentation](https://docs.spring.io/spring-framework/reference/core/aop/ataspectj/pointcuts.html#aop-pointcuts-designators)). The good news is, all these cases are easily caught by a test such as the one above.

I have a few other SpEL-related ideas, so if you enjoyed this article and would like to see more, I'd like to hear about it.

*Thank you to my colleagues at Ticketmaster for reviewing an early draft of this article.*

## Footnotes

(1) Michael Feathers, *Working Effectively With Legacy Code* (Prentice Hall Professional, 2004), 36.  
(2) The `EvaluationContext` also allows for more advanced configuration than just variables. For more details, see the [documentation](https://docs.spring.io/spring-framework/reference/core/expressions/evaluation.html#expressions-evaluation-context).  
(3) The methods on an annotation are called "elements", see the [relevant section](https://docs.oracle.com/javase/specs/jls/se8/html/jls-9.html#jls-9.6.1) of the Java Language Specification.
