---
title: "How Social Trends Help Me Fix Essential Vulnerabilities"
date: "2021-11-04T14:14:55+00:00"
lastmod: "2021-11-04T14:16:21+00:00"
description: "Our research team found a correlation between socially trending vulnerabilities and the existence of exploits that can harm your application."
canonical: "https://snyk.io/blog/snyk-social-trends-fix-security-vulnerabilities/"
authors:
  - "bmvermeer"
image: "https://snyk.io/wp-content/uploads/blog-social-trends-usage-vuln-1240x363.png"
categories:
  - "Security"
  - "Snyk"
related_posts:
  - "avoid-java-serialization"
  - "fix-java-security-issues-while-coding-in-intellij-idea"
  - "fixing-vulnerabilities-in-maven-projects"
frozen: false
---

Recently, Snyk added [social trends](https://snyk.io/blog/social-media-for-security-intelligence/) to its [vulnerability data](https://snyk.io/product/vulnerability-database/). This new indicator shows you what vulnerabilities are trending so you can better prioritize remediation. Our research team found a strong correlation between socially trending vulnerabilities and the existence of exploits that can actually harm your application.

Following the social trends of security vulnerabilities makes practical sense. When a specific vulnerability is gaining a lot of interest in social — Twitter, for instance — it means a lot of people are aware of the problem. Statistically, this also means more people that want to do you harm. Therefore, it can be important to put some extra focus on the vulnerabilities in your system that are socially trending.

Let's take a look at vulnerability social trends (sentiment analysis) in action.

## A trending example in Java

I created a small Java application based on an outdated Spring Boot version, `2.2.0-RELEASE`. After [connecting the GitHub repo to my Snyk account](https://snyk.io/login), I found the following vulnerability on top of my list because it is currently trending on social media.
![](https://snyk.io/wp-content/uploads/blog-social-trends-usage-vuln-1240x363.png)

It is a remote code execution (RCE) vulnerability in the embedded Apache Tomcat version that ships with the specific Spring Boot starter package I use. By clicking on the trending button, I found many tweets referring to this specific vulnerability and a working exploit that is published. I will come back to this later, but first, let me explain the vulnerability.

## Remote Code Execution Vulnerability CVE-2020-9484 explained

I will briefly explain the dynamics of this RCE vulnerability in Tomcat. This vulnerability exists in all Tomcat versions, including the embedded version supplied with Spring Boot. It is also good to know that a new update is already available for all versions fixing this issue.

Suppose you are using the `PersistentManager` with a `FileStore` in Tomcat. The `PersistentManager` manages the session. A session is used to preserve state between the client requests to the server. By default, Tomcat uses the `StandardManager` that keeps the sessions in memory. However, the `PersistenManager` swaps out sessions to storage, if they are idle for some seconds. This can be a disk using the `FileStore` or a database using the `JDBCStore`.

Using the `FileStore`, Tomcat stores in a predefined location as `<JSESSIONID>.session`. If the new call with a session ID is not in memory, the `PersistentManager` will look at the stored sessions on disk and deserializes the stored session object into memory when found.

But what if my session is something this `JSESSIONID=../../../../../../../foo/mysession`? Because of the path traversal, Tomcat will look for `mysession.session` in the `foo` directory. If I am able to upload a file to this system for some reason, I can upload a serialized session file. Next, I can trigger deserialization by setting my `JSESSIONID` to the corresponding location. Deserialization triggers harmful remote code execution depending on the serialized object.

If you want to know more about why deserialization vulnerabilities are harmful, please read the blog post [Serialization and deserialization in Java: explaining the Java deserialize vulnerability](https://snyk.io/blog/serialization-and-deserialization-in-java/).  

For more information about this vulnerability in Tomcat, a great article on [redtimmy.com](https://www.redtimmy.com/apache-tomcat-rce-by-deserialization-cve-2020-9484-write-up-and-exploit/) explains it in more detail. You can find an exploit proof of concept on this GitHub repository: <https://github.com/masahiro331/CVE-2020-9484>

## How Snyk Social Trends help you focus on the important stuff

You might say that this vulnerability has a bunch of prerequisites before it can actually be exploited.

1. `PersistentManager` needs be enabled using the `FileStore`
2. Attackers need to be able to upload an arbitrary file
3. There needs to be a gadget available on the classpath for the deserialization attack

Some people might say, because of these prerequisites not many cases are actually exploitable and dismiss it. However, because of the large amount of attention on Twitter, I took a good look and saw that it was *not that hard* to change the session manager to the `PersistentManger` with `FileStore`. Also with the amount of dependencies I am using in my application the change of having a gadget in there is quite possible.  

So, if for some reason someone in my team decides to use the `PersistentManger` with `FileStore` the only thing missing is arbitrary file uploads. Given the fact that security breaches are almost always a chain of events that lead to a disaster — and not just a single event or vuln — I don't think you can just dismiss a vuln like this. Especially not if fixes are available.  

More importantly, if a vuln is trending, this means a lot of people know and talk about it — including people with bad intentions. For me, it is a marker that I have to investigate such a vuln even more closely than I already do to see if I am vulnerable now or will be in the future.

Including the trending marker in the Snyk UI not only puts a spotlight on the vuln but also increases the priority score for that reason. Therefore, we believe that searching social media to determine the popularity of a specific vulnerability is a powerful tool so I will not get caught by obvious and well-known security mistakes. So when you see ![](https://snyk.io/wp-content/uploads/blog-social-trends-usage-trending.png), we encourage you to take a second look at a vuln you may have ignored before.

This article was originally posted on the Snyk.io blog: [https://snyk.io/blog/snyk-social-trends-fix-security-vulnerabilities/](https://snyk.io/blog/docker-for-java-developers/) and is used with permission.
