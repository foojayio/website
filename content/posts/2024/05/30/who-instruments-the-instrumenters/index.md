---
title: "Who instruments the instrumenters?"
date: "2024-05-30T16:25:45+00:00"
lastmod: "2025-02-04T19:16:52+00:00"
description: "Have you ever wondered how libraries like Spring and Mockito modify your code at run-time to implement all their advanced features?"
authors:
  - "johannes-bechberger"
  - "mikael-francoeur"
image: "johannescode.png"
categories:
  - "Developer Tools"
  - "Spring"
related_posts:
  - "ap-loader-a-new-way-to-use-and-embed-async-profiler"
  - "asyncgetcalltrace-reworked-frame-by-frame-with-an-iterative-touch"
  - "class-loader-hierarchies"
  - "ask-a-lille-dev-what-java-developers-really-think-about-quality-frameworks-communities-and-careers"
frozen: false
---

Have you ever wondered how libraries like [Spring](https://spring.io/) and [Mockito](https://site.mockito.org/) modify your code at run-time to implement all their advanced features?

Wouldn't it be cool to get a peek behind the curtains?

This is the premise of my [meta-agent](https://github.com/parttimenerd/meta-agent), a Java agent to instrument instrumenters, to get these insights and what this blog post is about.

This article is a collaboration with [Mikaël Francoeur](https://www.linkedin.com/in/mika%C3%ABl-francoeur/), who had the idea for the meta-agent and wrote most of this post. So it's my first ever post-collaboration. But I start with a short introduction to the agent itself before Mikaël takes over with real-world examples.

## Meta-Agent

The meta-agent ([GitHub](https://github.com/parttimenerd/meta-agent)) is a Java agent that instruments the I`nstrumentation.`[addTransformer](https://docs.oracle.com/en/java/javase/17/docs/api/java.instrument/java/lang/instrument/Instrumentation.html#addTransformer(java.lang.instrument.ClassFileTransformer)) methods agents use to add bytecode transformers and wrap the added transformers to capture bytecode before and after each transformation.

This allows the agent to capture what every instrumenting agent does at run-time. *I covered the basics of writing your own instrumenting agent before in my blog post [Instrumenting Java Code to Find and Handle Unused Classes](https://mostlynerdless.de/blog/2023/04/06/instrumenting-java-code-to-find-and-handle-unused-classes/) and my [related talk](https://www.youtube.com/watch?v=JnJgvcZo7b8). So, I'll skip all the implementation details here.*

But how can you use it? You first have to [download the agent](https://github.com/parttimenerd/meta-agent/releases/download/0.0.1/meta-agent.jar) (or build it from scratch via `mvn package -DskipTests`), then you can just attach it to your JVM at the start:

```bash
java -javaagent:target/meta-agent.jar -jar your-program.jar
```

This will then create a web server at <http://localhost:7071> that allows you to inspect the bytecode modifications of each instrumenter dynamically. For the example from the README <http://localhost:7071/full-diff/class?pattern=java.lang.Iterable> shows you, for example, how Mockito modifies the Iterable class upon mocking:
![](https://mostlynerdless.de/wp-content/uploads/2024/05/image-2000x1144.png)

This uses [Vineflower](https://github.com/Vineflower/vineflower) to decompile the bytecode, but you can, of course, also view raw bytecode diff, as Vineflower might not always produce correct code. Just add [\&mode=javap](http://localhost:7071/full-diff/class?pattern=java.lang.Iterable&mode=javap) to the previous URL:
![](https://mostlynerdless.de/wp-content/uploads/2024/05/image-1-2000x1144.png)

Another nice feature is that the agent allows you to inspect almost all classes, even if they haven't been modified. This enables you to gain insights into code you don't have the source code.

Now I'm handing it over to Mikaël, who actually fixed bugs using my tool:

## Spring and Mockito

Two of the tools I work the most with are Spring and Mockito, and both make liberal use of bytecode generation and modification.

Spring instruments your application classes using proxies generated with [CGLIB](https://github.com/cglib/cglib) or JDK's [Proxy](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/reflect/Proxy.html) class, and Mockito uses the Bytebuddy library to modify classes at run-time. This lets these tools extend Java code in ways that would not otherwise be possible. In this next section, I'll discuss how we can use the meta-agent to gain more observability over these processes.

The first example that comes to mind is proxying classes, a technique frequently used to add arbitrary behavior to an existing object and used extensively by Spring. The JDK contains `java.lang.reflect.Proxy`, a class that lets you implement arbitrary interfaces to generate objects at run-time. For example, here is how you would implement a JDK proxy:

```java
interface Door {
  @Secured
  void open();
  boolean isOpen();
}

Door makeSecure(Door door) {
  return (Door) Proxy.newProxyInstance(
    getClass().getClassLoader(), // classloader
    new Class[] { Door.class }, // interfaces
    (Object proxy, Method method, Object[] args) -> {
      if (method.getAnnotation(Secured.class) != null 
          && !userIsAuthorized()) {
        throw new RuntimeException(
          "user is unauthorized to access method %s"
          .formatted(method.getName())
        );
      }
      return method.invoke(door, args);
    }
  );
}

@Test
void testSecured() {
  Door securedDoor = makeSecure(new SimpleDoor());
  setUserIsAuthorized(false);
  assertThatException().isThrownBy(door::open);
}
```

These few lines of code are enough to implement an annotation that can be reused on arbitrary interfaces. In fact, a lot of what Spring does can be summed up by this code snippet.

But alas, the JDK only supports this for interfaces, not for classes, so you wouldn't be able to use this if `Door`, or any of the types annotated with `@Secured`, was not an interface, even though the code would still make perfect sense from a developer's point of view. CGLIB addresses this gap.

CGLIB is a bytecode generation library that is now abandoned but forked, repackaged, and used extensively by Spring [(1)](#ref1). It has an API that emulates `java.lang.reflect.Proxy`, but that also works with classes. This lets us generify our little security framework to also instrument classes:

```java
<T> T makeSecure(T t) {
  return (T) Enhancer.create(
    t.getClass(), // we can now use classes!
    (MethodInterceptor) (o, method, objects, methodProxy) -> {
      // same intercepting behaviour
    }
  );
}
```

And everything you pass into `makeSecure()` will now have its `@Secured` methods intercepted. This is a powerful mechanism, but have you ever wondered what happens when you invoke [Proxy.newProxyInstance](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/reflect/Proxy.html#newProxyInstance(java.lang.ClassLoader,java.lang.Class%5B%5D,java.lang.reflect.InvocationHandler)), or [Enhancer.create](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/cglib/proxy/Enhancer.html#create())? What kind of trickery takes place? Well, meta-agent lets us see exactly what is happening. Here is part of the decompiled bytecode from the `$Proxy8` class that was dynamically created by the `Proxy` in the first example above:

```java
final class $Proxy8 extends Proxy implements Door {
  private static final Method m5;

  public final void open() {
    try {
      super.h.invoke(this, m5, null);
    } catch (RuntimeException | Error var2) {
      throw var2;
    } catch (Throwable var3) {
      throw new UndeclaredThrowableException(var3);
    }
  }

  static {
    ClassLoader var0 = $Proxy8.class.getClassLoader();
    m5 = Class.forName("me.bechberger.meta.MockitoTest$Door", 
                       false, var0).getMethod("open");
  }
}
```

This is something you could very well write in any Java program. In fact, it's a pretty standard implementation of the decorator design pattern. In a static initializer, the relevant methods are first cached in static fields so that `getMethod()`, a non-trivial operation, only has to happen once. Then, every method is just a simple delegation to the InvocationHandler you provided, surrounded by some error handling. The only peculiar thing about this code is that it was generated at run-time directly in bytecode.

When I first read this, the first thing that struck me was that `Proxy` wraps undeclared unchecked exceptions [(2)](#ref2) into an unchecked `UndeclaredThrowableException`. This is a [documented](https://docs.oracle.com/en%2Fjava%2Fjavase%2F21%2Fdocs%2Fapi%2F%2F/java.base/java/lang/reflect/Proxy.html), albeit little-known, fact and can lead to surprising behavior when using proxy-based frameworks that rely on exception types. More on this later.

Now let's look at what CGLIB does. CGLIB generates a lot of code. The proxy for the `SimpleDoor` class that implements our `Door` interface is over 300 lines long. Here is just the `isOpen()` method. I've renamed the variables for better readability.

```java
public class SimpleDoor$$EnhancerByCGLIB$$b71b2e45 {
  public final boolean isOpen() {
    MethodInterceptor interceptor = this.isOpenCallback;
    if (this.isOpenCallback == null) {
      initCallbacks(this);
      interceptor = this.isOpenCallback;
    }

    if (interceptor != null) {
      Object returnValue = interceptor.intercept(
        this, isOpenMethod, zeroLengthArray, isOpenProxyMethod
      );
      return returnValue == null ? false : (Boolean) returnValue;
    } else {
      return super.isOpen();
    }
  }
}
```

There are two surprising things here. First, this check for `isOpenCallback == null` seems redundant. It turns out that CGLIB allows setting or removing callbacks after the proxy is initialized, so proxies have to do this sanity check for every method call.

This doesn't seem to be used much by the Spring Framework, so there might be room for optimization by making CGLIB proxies immutable. Second, the `returnValue == null ? false` means that if a user-provided callback returns null, CGLIB returns false, instead of throwing a NullPointerException like coercing null to a primitive would typically do. This [mostly undocumented](https://github.com/spring-projects/spring-framework/blob/main/spring-core/src/main/java/org/springframework/cglib/core/CodeEmitter.java#L801) behavior happens for all primitives, and when I learned about it, I dug through the Spring Framework and was able to [identify and fix one bug](https://github.com/spring-projects/spring-framework/pull/32412).

Remember what the JDK `Proxy` did with `UndeclaredThrowableException`? CGLIB proxies in Spring also used to do this, but that changed somewhere along the way. Again, when I learned about it, I opened up the Spring Framework and spotted a regression that affected transaction handling caused by this change. I opened [a PR with the fix](https://github.com/spring-projects/spring-framework/pull/32469) and it will be available in 6.2:
![](https://mostlynerdless.de/wp-content/uploads/2024/05/image-2-2000x1176.png)

I found reading the code generated by the JDK `Proxy` and the CGLIB `Enhancer` classes to be fascinating because it's something I rely on every day as a Spring developer but never had the chance to see [(3)](#ref3). Moreover, it allowed me to spot and contribute fixes for one bug and one regression in Spring.

Another tool I use daily where bytecode generation is used extensively is [Mockito](https://github.com/mockito/mockito), a mocking framework used for testing. The motivation behind mocks is to test collaborators.

For example, in a `UserService` that saves a `User` and then sends a notification based on the results from the database, how would you test that no notification is sent if saving the user fails? The lightest way to do it is using mocks, and anyone who has handwritten mocks before knows how tedious it can be [(4)](#ref4). Mockito makes this easy:

```java
@Test
void givenRepositoryThrowsException_whenSaveUser_thenDoesNotSendNotification() {
  UserRepository userRepository = mock(UserRepository.class);
  NotificationService notificationService = 
    mock(NotificationService.class);
  UserService userService = 
    new UserService(userRepository, notificationService);

  when(userRepository.save(any())).thenThrow(new RuntimeException("nope"));

  assertThatThrownBy(() -> userService.saveUser(new User("Mikaël")))
    .hasMessage("nope");

  verifyNoInteractions(notificationService);
}
```

Where Mockito shines is not only in its API ([mock()](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#mock(T...)), [when()](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#when(T))`.`[thenThrow()](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/stubbing/OngoingStubbing.html#thenThrow(java.lang.Class)), [any()](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/ArgumentMatchers.html#any(java.lang.Class)), and [`verifyNoInteractions()`](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#verifyNoInteractions(java.lang.Object...)) in this example), but also its ability to mock even final or static methods. The proxying techniques we've seen so far used subclassing to generate new objects that implemented or extended existing interfaces or classes. But final and static methods can't be overridden. Mockito gets around this by registering a JVM agent and transforming the existing classes [(5)](#ref5).

I originally planned on showing the decompiled code as an example. Unfortunately, the Vineflower decompiler doesn't output the most readable code; in this case, it gets confused and generates illegal Java.

I had to switch meta-agent to `javap-verbose` mode to confirm with the bytecode, and I fixed and edited the code for readability. Here is the `UserRepository::save` method from the example above, as transformed by Mockito:

```java
class UserRepository {
  // omitting constructor

  User save(User user) {
    MockMethodDispatcher dispatcher =
      MockMethodDispatcher.get("VCcM9ivB", this);

    if (dispatcher != null && dispatcher.isMocked(this)) {
      Method method = UserRepository.class.getDeclaredMethod(
        "save", User.class
      );
      if (!dispatcher.isOverridden(method)) {
        Callable<User> mockCall = dispatcher.handle(
          this, UserRepository.class.getDeclaredMethod(
            "save", User.class
          ), new Object[] { user }
        );
        return mockCall != null ? (User) mockCall.call() : user;
      }
    }

    return user;
  }
}
```

Notice that the name of the `UserRepository` class hasn't changed. Whereas `Proxy` and `CGLIB` generated new classes that are extended or inherited from our existing types, Mockito transforms the existing class, and every method of the transformed class first checks with a static registry (`MockMethodDispatcher`) to see if the current object is a mock.

If it is, then it uses the mocked behavior; if not, it uses the object's natural behavior (in this case, just returning the user). Something that isn't so obvious unless you dig through Mockito's source code is that this transformation is done for every class up the chain of inheritance, up to Object. The meta-agent will readily show the complete list of transformed classes using the `/classes` endpoint.

One potential inefficiency I would like to point out in the code above is that `getDeclaredMethod()`, a non-trivial operation, is actually called twice on every invocation of mocked methods. It can't be cached in a static field like in the Proxy or CGLIB classes, because that would require defining new fields, and most JVMs don't allow adding fields to existing classes [(6)](#ref6).

Storing it in a local variable might result in a performance gain. Still, I wonder if some static cache, like what already exists in MockMethodDispatcher, would translate to faster tests when using Mockito. Finally, it's worth noting that the double invocation of getDeclaredMethod() is almost invisible in Mockito's source code but immediately apparent in the decompiled code.

This is decompiled code, and the Vineflower decompiler backing meta-agent struggles in some cases. For the Mockito mock and some CGLIB proxies that I tried, it generated illegal Java. There's a bit where it just gave up and left a code comment. It also doesn't generate `@Override` annotations consistently, which is weird.

Fortunately, you can also pass `?mode=javap` or `?mode=javap-verbose` to meta-agent, and it will show the actual bytecode and other low-level information like constant pools.

## Conclusion

This was a fun project; there's probably more to uncover by analyzing generated code.

It would also be interesting to use the meta-agent to look at other generated bytecode from popular libraries like [Hibernate](https://hibernate.org), [EclipseLink](https://eclipse.dev/eclipselink/), [AspectJ](https://eclipse.dev/aspectj/), or [Lombok](https://projectlombok.org/).

Or do you have other instrumenters that you want to explore with us?

This project shows how a question during a conference, "How can I inspect code at run-time?", turned into an interesting and eventually helpful project.

(1) CGLIB is now unmaintained, but Spring repackages a patched and updated version of CGLIB. The examples in this text are from that version.{#ref1}

(2) Undeclared checked exceptions can be thrown using Lombok's `@SneakyThrows`, or from a language without checked exceptions, such as Kotlin.{#ref2}

(3) CGLIB does have a `DebuggingClassWriter` that can be used to output generated classes before they are loaded, and to get the same behaviour for `Proxy` you can set the `jdk.proxy.ProxyGenerator.saveGeneratedFiles` system property to `true`, but it is nowhere near as convenient as using meta-agent.{#ref3}

(4) What Mockito calls "mocks" are called "test doubles" in the literature. See the related [discussion](https://github.com/mockito/mockito/issues/2438).{#ref4}

(5) This is actually the behaviour of the "inline mock maker", the default mock maker as of version 5. Mockito also supports generating mocks with subclasses, similar to how the `Proxy` and `Enhancer` classes work.{#ref5}

(6) Rafael Winterhalter's answer to "How to add a field to an existing instance with ByteBuddy?" <https://stackoverflow.com/a/58529716/7096763>.{#ref6}

*This article is part of my work in the [SapMachine](https://sapmachine.io/) team at [SAP](https://sap.com/), making profiling and debugging easier for everyone. This article first appeared on my personal blog [mostlynerdless.de](https://mostlynerdless.de/blog/2024/05/10/who-instruments-the-instrumenters/).*
