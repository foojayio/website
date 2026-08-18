---
title: "Spring4Shell: The zero-day RCE in the Spring Framework explained"
slug: "spring4shell-the-zero-day-rce-in-the-spring-framework-explained-ready"
date: "2022-04-08T04:17:45+00:00"
lastmod: "2022-04-08T04:17:47+00:00"
description: "We’ll explore how remote code execution (RCE) works, explaining Spring4Shell, a zero-day vulnerability in the Spring framework that could allow for RCE"
canonical: "https://snyk.io/blog/spring4shell-zero-day-rce-spring-framework-explained/"
authors:
  - "bmvermeer"
image: "blog-hero-code-vulnerability-warning-1536x384-1.jpeg"
categories:
  - "Security"
  - "Spring"
tags:
related_posts:
  - "log4j-isnt-killing-java"
  - "log4shell-leak4j"
  - "system-logger"
  - "jurassic-jdk-migrate-or-extinct"
enlighterjs: true
frozen: false
---

On March 30, 2022, a critical [remote code execution (RCE) vulnerability](https://snyk.io/vuln/SNYK-JAVA-ORGSPRINGFRAMEWORK-2436751) was found in the Spring Framework. More specifically, it is part of the `spring-beans` package, a transitive dependency in both `spring-webmvc` and `spring-webflux`. This vulnerability is another example of why [securing the software supply chain](https://snyk.io/blog/preventing-malicious-packages-and-supply-chain-attacks-with-snyk/) is important to open source.

Security resources like [Lunasec](https://www.lunasec.io/docs/blog/spring-rce-vulnerabilities/), [Rapid7](https://www.rapid7.com/blog/post/2022/03/30/spring4shell-zero-day-vulnerability-in-spring-framework/) and [Praetorian](https://www.praetorian.com/blog/spring-core-jdk9-rce/) confirmed that the vulnerability is real, and in the meantime, Spring has already released a new version that mitigates this problem, so [we recommend updating](https://snyk.io/blog/is-there-such-a-thing-as-spring4shell/). While **Spring4Shell** does not appear to have the same impact as the recent Log4Shell vulnerability, it should still be evaluated and prioritized by every organization using the Spring Framework. In this post, we'll explore how the RCE works.

## Explaining Spring4Shell

If we have a controller with a request mapping loaded into memory, we are already vulnerable to this issue. Below, you see our `GreetingController` with a `PostMapping` to `/greeting`. When we call our application in, for instance, Tomcat at `https://mydomain/myapp/greeting` it tries to transform the input to a POJO (Plain Old Java Object) which, in our case, is the `Greeting` object.

```
@Controller
public class GreetingController {

  @PostMapping("/greeting")
  public String greetingSubmit(@ModelAttribute Greeting greeting, Model model) {
     model.addAttribute("greeting", greeting);
     return "result";
  }

}
```

However, because Spring uses [serialization](https://snyk.io/blog/serialization-and-deserialization-in-java/) under the hood to map these values to the Java object, it is possible to also set other values. After some exploration, it turns out that you are able to set the properties of a class. This is interesting if you run on Tomcat.

...

[Read the full article and explanation on Snyk.io](https://snyk.io/blog/spring4shell-zero-day-rce-spring-framework-explained/)
