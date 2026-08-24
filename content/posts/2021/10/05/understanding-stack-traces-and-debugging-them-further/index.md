---
title: "Understand Stack Traces and how you can Debug Better With them"
date: "2021-10-05T10:57:28+00:00"
lastmod: "2021-10-05T11:18:49+00:00"
description: "When we get an exception stack, it can often contain the solution for our problem. But in more than one case, it's the edge of a thread..."
canonical: "https://talktotheduck.dev/understanding-stack-traces-and-debugging-them-further"
authors:
  - "shai-almog"
image: "Understanding-Stack-Traces1.jpg"
categories:
  - "Debugging"
related_posts:
  - "eight-debugging-tips-for-intellijidea-users-you-never-knew-existed"
  - "java-logging-what-to-log-what-not-to-log"
  - "embracing-jvm-unified-logging"
frozen: false
---

Recently a junior developer sent me an obfuscated stack trace and was pretty surprised when I instantly knew the problem and pointed him at the necessary change. To be fair, I had the advantage of being the person who put that bug there in the first place… But still the ability to glean information from a stack trace, even an obfuscated one, is a serious skill.

The stack trace in question was a `ClassNotFoundException`, that's typically pretty easy and already tells you everything you need to know. The class isn't there. Why it isn't there is really a matter of what we did wrong. In this case, since the project was obfuscated, the bug was that this class wasn't excluded from obfuscation.

Despite all the hate it got over the years, `NullPointerException` is one of my favorite exceptions. You instantly know what happened and in most cases the stack leads almost directly to the problem. There are some edge cases e.g.:

```java
myList.get(offset).invokeMethod();
```

So which one triggered the `NullPointerException`?

If you're using the latest version of Java it would tell you, which is pretty cool. But if we're still at Java 11 (or 8) there's more than one option. At least 3 or maybe 4 if we cheat a bit:

1. `myList` is the obvious one but it rarely is null and if so you would see it immediately
2. offset can be null. It can be an Integer object in which case it can be `null` due to autoboxing. It's also less likely
3. The most likely culprit in this specific case is the return value from the `get()` method which means one of the list elements is `null`
4. Finally `invokeMethod` itself can throw a `NullPointerException`. But that's a bit of cheating since the stack will be a bit deeper

So without knowing anything about the code we can pretty much guess what failed at a line just by knowing the exception type. But this doesn't lead us directly to the bug in all cases.

## There Was a Null

That `NullPointerException` probably happened due to a null in the list. Assuming you verified that you might still not know how that null got into the list in the first place…

This isn't hard to find out, let's assume that the List is of the `ArrayList` type, in that case just open the `ArrayList` class (which you can do with Control-O in IntelliJ) and place a conditional breakpoint on the `add()` method. You can test if the value is null and that will stop at a breakpoint if someone tries to add null to the list.

![Check for null condition](https://cdn.hashnode.com/res/hashnode/image/upload/v1632981604570/sDVYkrmcU.png)

Now this won't catch all the cases of `null` sneaking into the list. It can do so via the stream API, via `addAll()` and a couple of other methods. The nice thing is that we can grab pretty much any one of those methods:

![Grabbing addAll Calls](https://cdn.hashnode.com/res/hashnode/image/upload/v1632982095589/nDT6BmG5m.png)

Since `addAll()` accepts a `Collection` we can use the standard `contains()` method to check if we have a `null` element in the `Collection` and if so stop.

## What if this is "sometimes" OK?

So we run this code and bingo it stops at a breakpoint… But unfortunately this isn't the right case. That breakpoint is related to a different list which we aren't debugging right now. Apparently in that list a `null` value is OK and expected.

So we press continue and the breakpoint hits again and again and again. Each time for the wrong list… This is often the point where developers start cursing loudly and swearing off debuggers for good old logs.

So there are several ways around this problem. The most ideal one is to avoid that specific list. If you have a way of recognizing that list e.g. a global instance or the first element might be a specific value you can simply augment the original conditional breakpoint e.g. in this case we assume the first element in the `null` is OK list is 77 in which case this condition will workaround the problem:

![Hacking the Exception](https://cdn.hashnode.com/res/hashnode/image/upload/v1632982223058/W16VFVW_0r.png)

This isn't ideal but it works around the problem assuming it's localized enough.

## Nested Stack Traces

So this is one of the "pains" in modern frameworks. The framework catches an exception and propagates it, wrapping it in its own exception. Rinse repeat. You end up with a Matryoshka doll of stack traces.

Sifting through all of those stacks and finding the one that matters is often a huge part of the pain. Especially in frameworks like Spring where the proxy code makes the stacks especially long and hard to read.

E.g.:

```java
javax.ws.rs.ProcessingException: RESTEASY004655: Unable to invoke request: org.apache.http.conn.HttpHostConnectException: Connect to localhost:8443 [localhost/127.0.0.1, localhost/0:0:0:0:0:0:0:1] failed: Connection refused (Connection refused)
    at org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine.invoke(ApacheHttpClient4Engine.java:328)
    at org.jboss.resteasy.client.jaxrs.internal.ClientInvocation.invoke(ClientInvocation.java:443)
    at org.jboss.resteasy.client.jaxrs.internal.proxy.ClientInvoker.invokeSync(ClientInvoker.java:149)
    at org.jboss.resteasy.client.jaxrs.internal.proxy.ClientInvoker.invoke(ClientInvoker.java:112)
    at org.jboss.resteasy.client.jaxrs.internal.proxy.ClientProxy.invoke(ClientProxy.java:76)
    at com.sun.proxy.$Proxy307.grantToken(Unknown Source)
    at org.keycloak.admin.client.token.TokenManager.grantToken(TokenManager.java:90)
    at org.keycloak.admin.client.token.TokenManager.getAccessToken(TokenManager.java:70)
    at org.keycloak.admin.client.token.TokenManager.getAccessTokenString(TokenManager.java:65)
    at org.keycloak.admin.client.resource.BearerAuthFilter.filter(BearerAuthFilter.java:52)
    at org.jboss.resteasy.client.jaxrs.internal.ClientInvocation.filterRequest(ClientInvocation.java:579)
    at org.jboss.resteasy.client.jaxrs.internal.ClientInvocation.invoke(ClientInvocation.java:440)
    at org.jboss.resteasy.client.jaxrs.internal.proxy.ClientInvoker.invokeSync(ClientInvoker.java:149)
    at org.jboss.resteasy.client.jaxrs.internal.proxy.ClientInvoker.invoke(ClientInvoker.java:112)
    at org.jboss.resteasy.client.jaxrs.internal.proxy.ClientProxy.invoke(ClientProxy.java:76)
    at com.sun.proxy.$Proxy315.toRepresentation(Unknown Source)
    at io.athena_tech.server.security.keycloak.KeycloakRealmService.lambda$doesLightrunRealmExist$0(KeycloakRealmService.java:124)
    at io.athena_tech.server.security.keycloak.KeycloakApi.getWithAdmin(KeycloakApi.java:35)
    at io.athena_tech.server.security.keycloak.KeycloakRealmService.doesLightrunRealmExist(KeycloakRealmService.java:122)
    at io.athena_tech.server.security.keycloak.KeycloakRealmService$FastClassBySpringCGLIB$9e16800d.invoke(<generated>)
    at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218)
    at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:771)
    at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)
    at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.proceed(CglibAopProxy.java:749)
    at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:366)
    at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:118)
    at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186)
    at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.proceed(CglibAopProxy.java:749)
    at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:691)
    at io.athena_tech.server.security.keycloak.KeycloakRealmService$EnhancerBySpringCGLIB$7eca0d51.doesLightrunRealmExist(<generated>)
    at io.athena_tech.server.service.client.InitKeycloakService.initDefaultCompanies(InitKeycloakService.java:146)
    at io.athena_tech.server.service.client.InitKeycloakService$FastClassBySpringCGLIB$35f06991.invoke(<generated>)
    at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218)
    at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:771)
    at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)
    at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.proceed(CglibAopProxy.java:749)
    at org.springframework.aop.aspectj.AspectJAfterThrowingAdvice.invoke(AspectJAfterThrowingAdvice.java:62)
    at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186)
    at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.proceed(CglibAopProxy.java:749)
    at org.springframework.aop.interceptor.ExposeInvocationInterceptor.invoke(ExposeInvocationInterceptor.java:95)
    at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186)
    at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.proceed(CglibAopProxy.java:749)
    at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:691)
    at io.athena_tech.server.service.client.InitKeycloakService$EnhancerBySpringCGLIB$5ae1a459.initDefaultCompanies(<generated>)
    at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.base/java.lang.reflect.Method.invoke(Method.java:566)
    at org.springframework.context.event.ApplicationListenerMethodAdapter.doInvoke(ApplicationListenerMethodAdapter.java:305)
    at org.springframework.context.event.ApplicationListenerMethodAdapter.processEvent(ApplicationListenerMethodAdapter.java:190)
    at org.springframework.context.event.ApplicationListenerMethodAdapter.onApplicationEvent(ApplicationListenerMethodAdapter.java:153)
    at org.springframework.context.event.SimpleApplicationEventMulticaster.doInvokeListener(SimpleApplicationEventMulticaster.java:172)
    at org.springframework.context.event.SimpleApplicationEventMulticaster.invokeListener(SimpleApplicationEventMulticaster.java:165)
    at org.springframework.context.event.SimpleApplicationEventMulticaster.multicastEvent(SimpleApplicationEventMulticaster.java:139)
    at org.springframework.context.support.AbstractApplicationContext.publishEvent(AbstractApplicationContext.java:403)
    at org.springframework.context.support.AbstractApplicationContext.publishEvent(AbstractApplicationContext.java:360)
    at org.springframework.boot.context.event.EventPublishingRunListener.running(EventPublishingRunListener.java:103)
    at org.springframework.boot.SpringApplicationRunListeners.running(SpringApplicationRunListeners.java:77)
    at org.springframework.boot.SpringApplication.run(SpringApplication.java:330)
    at io.athena_tech.server.AthenaServerApp.main(AthenaServerApp.java:64)
    at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.base/java.lang.reflect.Method.invoke(Method.java:566)
    at org.springframework.boot.devtools.restart.RestartLauncher.run(RestartLauncher.java:49)
Caused by: org.apache.http.conn.HttpHostConnectException: Connect to localhost:8443 [localhost/127.0.0.1, localhost/0:0:0:0:0:0:0:1] failed: Connection refused (Connection refused)
    at org.apache.http.impl.conn.DefaultHttpClientConnectionOperator.connect(DefaultHttpClientConnectionOperator.java:156)
    at org.apache.http.impl.conn.PoolingHttpClientConnectionManager.connect(PoolingHttpClientConnectionManager.java:376)
    at org.apache.http.impl.execchain.MainClientExec.establishRoute(MainClientExec.java:393)
    at org.apache.http.impl.execchain.MainClientExec.execute(MainClientExec.java:236)
    at org.apache.http.impl.execchain.ProtocolExec.execute(ProtocolExec.java:186)
    at org.apache.http.impl.execchain.RetryExec.execute(RetryExec.java:89)
    at org.apache.http.impl.execchain.RedirectExec.execute(RedirectExec.java:110)
    at org.apache.http.impl.client.InternalHttpClient.doExecute(InternalHttpClient.java:185)
    at org.apache.http.impl.client.CloseableHttpClient.execute(CloseableHttpClient.java:83)
    at org.apache.http.impl.client.CloseableHttpClient.execute(CloseableHttpClient.java:56)
    at org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine.invoke(ApacheHttpClient4Engine.java:323)
    ... 64 common frames omitted
Caused by: java.net.ConnectException: Connection refused (Connection refused)
    at java.base/java.net.PlainSocketImpl.socketConnect(Native Method)
    at java.base/java.net.AbstractPlainSocketImpl.doConnect(AbstractPlainSocketImpl.java:399)
    at java.base/java.net.AbstractPlainSocketImpl.connectToAddress(AbstractPlainSocketImpl.java:242)
    at java.base/java.net.AbstractPlainSocketImpl.connect(AbstractPlainSocketImpl.java:224)
    at java.base/java.net.SocksSocketImpl.connect(SocksSocketImpl.java:403)
    at java.base/java.net.Socket.connect(Socket.java:609)
    at org.apache.http.conn.ssl.SSLConnectionSocketFactory.connectSocket(SSLConnectionSocketFactory.java:368)
    at org.apache.http.impl.conn.DefaultHttpClientConnectionOperator.connect(DefaultHttpClientConnectionOperator.java:142)
    ... 74 common frames omitted
```

Can someone tell me what I did wrong here when trying to run our server locally…

Let's try to break it down starting from the lowest exception which is usually the root cause:

```java
Caused by: java.net.ConnectException: Connection refused (Connection refused)
    at java.base/java.net.PlainSocketImpl.socketConnect(Native Method)
    at java.base/java.net.AbstractPlainSocketImpl.doConnect(AbstractPlainSocketImpl.java:399)
    at java.base/java.net.AbstractPlainSocketImpl.connectToAddress(AbstractPlainSocketImpl.java:242)
    at java.base/java.net.AbstractPlainSocketImpl.connect(AbstractPlainSocketImpl.java:224)
    at java.base/java.net.SocksSocketImpl.connect(SocksSocketImpl.java:403)
    at java.base/java.net.Socket.connect(Socket.java:609)
    at org.apache.http.conn.ssl.SSLConnectionSocketFactory.connectSocket(SSLConnectionSocketFactory.java:368)
    at org.apache.http.impl.conn.DefaultHttpClientConnectionOperator.connect(DefaultHttpClientConnectionOperator.java:142)
```

There's a problem connecting. A connection is refused which means our server is trying to connect to some other server but it's failing.

So this gave us some basic information but nothing else.

Let's proceed one exception upward and look at the second block. Since it's large I'll only focus on the edge of the exception:

```java
Caused by: org.apache.http.conn.HttpHostConnectException: Connect to localhost:8443 [localhost/127.0.0.1, localhost/0:0:0:0:0:0:0:1] failed: Connection refused (Connection refused)
    at org.apache.http.impl.conn.DefaultHttpClientConnectionOperator.connect(DefaultHttpClientConnectionOperator.java:156)
    at org.apache.http.impl.conn.PoolingHttpClientConnectionManager.connect(PoolingHttpClientConnectionManager.java:376)
```

Again nothing here. This is Apache connection code. But no information about who triggered it or why.

So we're back all the way to the top which is less typical and here we can find the answer:

```java
javax.ws.rs.ProcessingException: RESTEASY004655: Unable to invoke request: org.apache.http.conn.HttpHostConnectException: Connect to localhost:8443 [localhost/127.0.0.1, localhost/0:0:0:0:0:0:0:1] failed: Connection refused (Connection refused)
    at org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine.invoke(ApacheHttpClient4Engine.java:328)
    at org.jboss.resteasy.client.jaxrs.internal.ClientInvocation.invoke(ClientInvocation.java:443)
    at org.jboss.resteasy.client.jaxrs.internal.proxy.ClientInvoker.invokeSync(ClientInvoker.java:149)
    at org.jboss.resteasy.client.jaxrs.internal.proxy.ClientInvoker.invoke(ClientInvoker.java:112)
    at org.jboss.resteasy.client.jaxrs.internal.proxy.ClientProxy.invoke(ClientProxy.java:76)
    at com.sun.proxy.$Proxy307.grantToken(Unknown Source)
    at org.keycloak.admin.client.token.TokenManager.grantToken(TokenManager.java:90)
    at org.keycloak.admin.client.token.TokenManager.getAccessToken(TokenManager.java:70)
    at org.keycloak.admin.client.token.TokenManager.getAccessTokenString(TokenManager.java:65)
    at org.keycloak.admin.client.resource.BearerAuthFilter.filter(BearerAuthFilter.java:52)
```

If you'll look a bit below you will see `org.keycloak` packages. This essentially means I forgot to launch keycloak before launching the server. This was well hidden in that stack and required a lot of domain knowledge (we use keycloak) to figure that out.

This sucks. If I was new to the team trying to get stuff to launch (which is exactly when this sort of exception happens), I would have been baffled by the exception. At least it would have taken me a while to figure it out. Unfortunately, I have no silver bullet for that specific problem.

Just keep reading the stack, the answer is usually in the edge (bottom or top). Don't give up if something isn't instantly clear.

## TL;DR

In many cases we can glean the cause of an exception we see in the log or get from the user by just reviewing the stack trace and digging deeper. Obviously, keep in mind nested exceptions and other such issues.

The debugger can still be a great ally when trying to figure out the root cause of an exception stack. We can leverage features like conditional breakpoints to narrow this down. Surprisingly, we didn't touch on exception breakpoints in this post. I think they have their value but when we have a stack we already know (roughly) what happened and where.

We need something that goes beyond that and I tried to cover that in this article.
