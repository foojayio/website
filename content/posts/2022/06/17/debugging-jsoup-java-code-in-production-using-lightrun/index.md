---
title: "Debugging jsoup Java Code in Production Using Lightrun"
slug: "debugging-jsoup-java-code-in-production-using-lightrun"
date: "2022-06-17T09:35:06+00:00"
lastmod: "2022-06-17T09:35:07+00:00"
description: "Scraping is a fragile discipline. As a workaround, we often use a server. Debugging these issues is remarkably difficult. Or at least it was!"
canonical: "https://lightrun.com/tutorials/debugging-jsoup-java-code-in-production-using-lightrun/"
authors:
  - "shai-almog"
image: "Debugging-JSOUP-With-Lightrun.jpg"
categories:
  - "IntelliJ IDEA"
  - "Tutorials"
tags:
related_posts:
  - "debugging-java-equals-hashcode-performance-in-production"
  - "debugging-jaxb-production-issues"
  - "debugging-race-conditions-in-production"
  - "get-started-with-allocation-profiling"
enlighterjs: true
frozen: false
---

Scraping websites built for modern browsers is far more challenging than it was a decade ago.

jsoup is a convenient API that makes scraping websites trivial via DOM traversal, CSS Selectors, JQuery-Like methods and more. But it isn't without its caveat. Every scraping API is a ticking time bomb.

Real-world HTML is flaky. It changes without notice since it isn't a documented API. When our Java program fails in scraping, we're suddenly stuck with a ticking time bomb.

In some cases, this is a simple issue that we can reproduce locally and deploy. But some nuanced changes in the DOM tree might be harder to observe in a local test case.

In those cases, we need to understand the problem in the parse tree before pushing an update. Otherwise, we might have a broken product in production.

What is jsoup? The Java HTML Parser
-----------------------------------

Before we go into the nuts and bolts of debugging jsoup let's first answer, the question above and discuss the core concepts behind jsoup.

The [jsoup website](https://jsoup.org/) defines it as:

jsoup is a Java library for working with real-world HTML. It provides a very convenient API for fetching URLs and extracting and manipulating data, using the best of HTML5 DOM methods and CSS selectors.

jsoup implements the [WHATWG HTML5](https://whatwg.org/html) specification and parses HTML to the same DOM as modern browsers do.

With that in mind, let's go directly to a simple sample also from the same website:

```java
Document doc = Jsoup.connect("https://en.wikipedia.org/").get();
log(doc.title());
Elements newsHeadlines = doc.select("#mp-itn b a");
for (Element headline : newsHeadlines) {
  log("%s\n\t%s",
    headline.attr("title"), headline.absUrl("href"));
}
```


This code snippet fetches headlines from wikipedia. In the code above, you can see several interesting features:

* Connection to URL is practically seamless -- just pass a string URL to the connect method
* There are special cases for some element children. E.g. Title is exposed as a simple method that returns a string without selecting from the DOM tree
* However, we can select the entry using pretty elaborate selector syntax

If you're looking at that and thinking "that looks fragile". Yes, it is.

Simple jsoup Test
-----------------

To demonstrate debugging, I created a simple demo that you can download here.

You can use the following Maven dependency to install jsoup into any Java program. Maven will download jsoup jar seamlessly:

```xml
<dependency>
  <groupId>org.jsoup</groupId>
  <artifactId>jsoup</artifactId>
  <version>1.14.3</version>
</dependency>
```


This demo is a trivial Java app that returns a complete list of external links and elements with src attributes in a page. This is based on the code from here, converted to a Spring Boot Java program.

The jsoup applicable code is relatively short:

```
public Set<String> listLinks(String url, boolean includeMedia) throws IOException {
   Document doc = Jsoup.connect(url).get();
   Elements links = doc.select("a[href]");
   Elements imports = doc.select("link[href]");

   Set<String> result = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
   if(includeMedia) {
       Elements media = doc.select("[src]");
       for (Element src : media) {
           result.add(src.absUrl("src"));
           //result.add(src.attr("abs:src"));
       }
   }

   for (Element link : imports) {
       result.add(link.absUrl("abs:href"));
   }

   for (Element link : links) {
       result.add(link.absUrl("abs:href"));
   }

   return result;
}
```


As you can see, we fetch the input String URL. We can also use input streams, but this makes things slightly more complicated when parsing relative URLs (we need a base URL anyway). We then search for links and objects that have an src attribute. The code then adds all of them into a set to keep the entries sorted and unique.

We expose this as a web service using the following code:

```java
@RestController
public class ParseLinksWS {
   private final ParseLinks parseLinks;

   public ParseLinksWS(ParseLinks parseLinks) {
       this.parseLinks = parseLinks;
   }

   @GetMapping("/parseLinks")
   public Set<String> listLinks(@RequestParam String url, @RequestParam(required = false) Boolean includeMedia) throws IOException {
       return parseLinks.listLinks(url, includeMedia == null ? true : includeMedia);
   }
}
```


Once we run the application can the application, we can use it with a simple curl command:

```
curl -H "Content-Type: application/json" "http://localhost:8080/parseLinks?url=https%3A%2F%2Flightrun.com"
```


This prints out the list of URLs referred to in the Lightrun home page.

Debugging Content Failures
--------------------------

Typical string scraping issues occur when an element object changes. E.g. wikipedia can change the structure of their pages and the select method above can suddenly fail. This is often a nuanced failure, e.g. missing DOM element in the Java object hierarchy which can trigger a failure of the select method.

Unfortunately, this can be a subtle failure. Especially when dealing with nested node elements and inter-document dependencies. Most developers solve this by logging a huge amount of data. This can be a problem due to two big reasons:

* Huge logs -- they are both hard to read and very expensive to ingest
* Privacy/GDPR Violations -- a scraped site might include user specific private information. Worse!
* The scraped site might change to include private information after scraping was initially implemented. Logging this private information might violate various laws.

If we don't log enough and can't reproduce the issue locally, things can become difficult. We're stuck in the add logs, build, test, deploy, reproduce -- rinse repeat loop.

Lightrun offers a better way. Just track the specific failure directly in production, verify the problem, and create a fix that will work with one deployment.

**NOTE:** This tutorial assumes you installed Lightrun and understand the basic concepts behind it. If not, please [check out the docs](https://docs.lightrun.com/).

### Finding your way in Browser DOM

Assuming you don't know where to look, a good place to start is inside the jsoup API. This can lead you back to user code. The cool thing is that this works regardless of your code. We can find the right line/file for the snapshot by digging into the API call.

I ctrl-clicked (on Mac use Meta-click) the select method call here:

```java
Elements links = doc.select("a[href]");
```


And it led me to the Element class. In it I ctrl-clicked the Selector "select" method and got to the "interesting" place.

Here, I could place a conditional snapshot to see every case where an "a\[href\]" query is made:

![image1.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1650367462176/h8Wqwl1Yk.png)

This can show me the methods/lines that perform that query:

![image4.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1650465643162/RMkm_mRVG.png)

This can help a lot in narrowing down the general problematic area in the document object hierarchy.

Sometimes, a snapshot might not be enough. We might need to use a log. The advantage of logging is that we can produce a lot of information, but only for a specific case and on-demand.

The value of logs is that they can follow an issue in a way that's very similar to stepping over code. The point where we placed the snapshot is problematic for logs. We know the query sent but we don't have the value that's returned yet. We can solve this easily with logs.

First, we add a log with the following text:

```
"Executing query {query}"
```


![image2.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1650465598568/jjkSGCvVa.png)

Then, to find out how many entries we returned, we just go to the caller (which we know thanks to the stack in the snapshot) and add the following log there:

```
Links query returned {links.size()}
<img decoding="async" src="https://cdn.hashnode.com/res/hashnode/image/upload/v1650465693574/unexIjfhz.png" alt="image5.png">
```


This produces the following log which lets us see that we had 147 `a[href]` links. The beauty of this is that the additional logs are interlaced with the pre-existing logs in-context:

```
Feb 02, 2022 11:25:27 AM org.jsoup.select.Selector select
INFO: LOGPOINT: Executing query a[href]
Feb 02, 2022 11:25:27 AM com.lightrun.demo.jsoupdemo.service.ParseLinks listLinks
INFO: LOGPOINT: Links query returned 147
Feb 02, 2022 11:25:27 AM org.jsoup.select.Selector select
INFO: LOGPOINT: Executing query link[href]
Feb 02, 2022 11:25:27 AM org.jsoup.select.Selector select
INFO: LOGPOINT: Executing query [src]
```


Avoid Security and GDPR Issues
------------------------------

GDPR and security issues can be a problem with leaking user information into the logs. This can be a major problem, and Lightrun helps you reduce that risk significantly.

Lightrun offers two potential solutions that can be used in tandem when applicable.

### Log Piping

The big problem with GDPR is the log ingestion. If you log private user data and then send it to the cloud, it's there for a long time. It's hard to find after the fact and it's very hard to fix.

Lightrun provides the ability to pipe all of Lightrun's injected logging to the IDE directly. This has an advantage of removing noise from other developers who might work with the logs. It can also skip the ingestion (optionally).

To send logs only to the plugin, select the piping mode as "plugin".

![image3.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1650465790348/J8eYWzZC4.png)

### PII Reduction/Blocklists

Personally Identifiable Information (PII) is at the core of GDPR and is also a major security risk. A malicious developer in your organization might want to use Lightrun to siphon user information. Blocklists prevent developers from placing actions in specific files.

PII reduction lets us hide information matching specific patterns from the logs (e.g. credit card format etc). This can be defined in the Lightrun web interface by a manager role.

TL;DR
-----

With Java content scraping, jsoup is the obvious leader. Development with jsoup is far more than string operations or even handling the connection aspects. Besides getting the document object, it also handles complex aspects required for DOM element and scripting.

Scraping is a risky business. It might break in the blink of an eye when a website changes slightly.

Worse, it can break to some users in odd ways that are impossible to reproduce locally.

Thanks to Lightrun, we can debug such failures directly in the production environment and publish a working version swiftly. You can use Lightrun for free by signing up [here](https://lightrun.com/free).
