---
title: "Testing the untestable"
date: "2025-10-19T17:29:20+00:00"
lastmod: "2026-09-21T06:01:17+00:00"
description: "I'm currently working on a software designed more than a decade ago. It offers a plugin architecture: you can develop a plugin whose lifecycle is handled…"
canonical: "https://blog.frankel.ch/testing-untestable/"
authors:
  - "nicolas-frankel"
image: "cover_large-1.jpg"
categories:
  - "Java"
  - "Testing"
related_posts:
frozen: false
---

I'm currently working on a software designed more than a decade ago. It offers a plugin architecture: you can develop a plugin whose lifecycle is handled by the software. The tough part, though, is how you access the platform capabilities: via static methods on singletons.

```java
@Override
public boolean start() {
    var aService = AService.getInstance();
    var anotherService = AnotherService.getInstance();
    // Do something with the services
    var result = ...;
    return result;
}
```

There's no easy way to test the `start()` method. In the old days, Mockito developers had pushed back against this feature, and the only alternative was [PowerMock](https://github.com/powermock/powermock). The decision was reversed in 2020 with the [3.4.0 release](https://github.com/mockito/mockito/blob/release/3.x/doc/release-notes/official.md#340), which introduced static method mocking in Mockito.

I liked the previous situation better. My opinion is that having to mock static methods is a sign of badly designed code. I [wrote about](https://blog.frankel.ch/on-powermock-abuse/) it already ten years ago. With PowerMock, one could mock the static methods, write the test, redesign the code, and then remove PowerMock. With the current situation, one can't look at the dependencies to search for design smells. In any case, the above problem still stands, and I can't change the design. It's forced upon me.

The solution is strangely straightforward, though. Just write a wrapper method around the one:

```
@VisibleForTesting                                                 //1
boolean start(AService aService, AnotherService anotherService) {  //2
    // Do something with the services
    var result = ...;
    return result;
}

@Override
public boolean start() {
    var aService = AService.getInstance();
    var anotherService = AnotherService.getInstance();
    return start(aService, anotherService);                        //3
}
```

1. Method is normally `private`, but since we want to test it, we make it package visible. The `@VisibleForTesting` annotation is for documentation purposes.
2. The testable method has parameters that can be mocked
3. Call the testable method

In this post, I showed how one can test legacy code not built on Dependency Injection.  

This is a pretty straightforward way to test untestable code.

*Originally published at [A Java Geek](https://blog.frankel.ch/testing-untestable/) on October 19th, 2025*
