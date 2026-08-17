---
title: "Preventing Cross-Site Scripting (XSS) in Java with Snyk Code"
slug: "preventing-cross-site-scripting-xss-in-java-applications-with-snyk-code"
date: "2023-06-09T15:23:39+00:00"
lastmod: "2023-06-09T15:23:40+00:00"
description: "By taking a proactive approach to XSS prevention and using the right resources and tools, developers can help ensure the security and integrity of their Java web applications. - by Brian Vermeer"
canonical: "https://snyk.io/blog/preventing-xss-snyk-code/"
authors:
  - "bmvermeer"
image: "snykvulnxxcode-1.png"
categories:
  - "Security"
  - "Snyk"
tags:
related_posts:
  - "building-secure-ci-cd-pipelines-with-github-actions-for-your-java-application"
  - "exploring-cve-2022-33980-the-apache-commons-configuration-rce-vulnerability"
  - "foojay-podcast-7"
  - "tiberius-a-security-testing-framework-for-llm-applications-in-java"
enlighterjs: true
frozen: false
---

Java is a powerful backend programming language that can also be used to write HTML pages for web applications. However, developers must know the potential security risks associated with Cross-Site Scripting (XSS) attacks when creating these pages.

With the rise of modern templating frameworks, preventing security attacks through proper input validation and encoding techniques has become easier.

However, when developers choose to create their own HTML pages without using a templating framework, there is an increased risk of introducing vulnerabilities.

Using, for instance, the `HttpServletResponse` object in a Spring MVC application to write content directly to the response can create opportunities for malicious users to inject code into the page, leading to potential XSS attacks.

It is, therefore, essential for developers to take steps to ensure the security of their Java web applications remains high by implementing appropriate measures to prevent XSS vulnerabilities when writing HTML pages.

A solution like the one below is an easy way to implement a server-side rendered page without any fancy framework that normally comes with specific instructions. However, there are obviously some downsides to this method.

Writing HTML output in Spring MVC without a templating framework {#h2-0-writing-html-output-in-spring-mvc-without-a-templating-framework}
-----------------------------------------------------------------------------------------------------------------------------------------

Suppose you have a web application that takes a product's name and displays it on a web page using the `HttpServletResponse` object. Here's an example of how you might implement this feature in a Spring MVC controller:

```
1@GetMapping("/direct")
2public void directLink (@RequestParam String param, HttpServletResponse response) throws IOException {
3   Product prod = productService.getProductByName(param);
4
5   response.setContentType("text/html");
6   var writer = response.getWriter();
7   writer.write(head);
8   writer.write("<div class=\"panel-heading\"><h1>"+ param + "</h1></div>");
9
10   String output = "<div class=\"panel-body\">" +
11           "<ul>" +
12           "<li>%s</li>" +
13           "<li>%s</li>" +
14           "<li>%s</li>" +
15           "</ul>" +
16           "</div>";
17
18   writer.write(String.format(output, prod.getDescription(), prod.getProductType(), prod.getPrice()));
19   writer.write(foot);
20
21   response.getWriter().flush();
22}
```


Can you figure out what sorts of security vulnerabilities may be introduced with the above Java code?

Finding XSS with Snyk Code {#h2-1-finding-xss-with-snyk-code}
-------------------------------------------------------------

When closely looking at the function above, you might already recognize at least one XSS vulnerability and maybe even two. When scanning my application with [++Snyk Code++](https://snyk.io/product/snyk-code/) we get notified of two different XSS problems in this method.

There are multiple ways to leverage Snyk Code. Let's take a look at three different examples. The most direct way of getting feedback from Snyk Code to a developer is by installing a plugin in the IDE. We have plugins for many different IDE's available. In the following example, I show how the IntelliJ plugin helps me find XSS problems during development.

Intellij plugin output:
![blog-preventing-xss-product-controller](https://snyk.io/_next/image/?url=https%3A%2F%2Fres.cloudinary.com%2Fsnyk%2Fimage%2Fupload%2Fv1682439081%2Fblog-preventing-xss-product-controller.jpg&w=2560&q=75)

Another option is to run Snyk Code using the Snyk CLI. Running command `snyk code test` from the terminal will give you an output like below. This method is useful on your local machine or as part of your automatic build in a CI/CD pipeline.

CLI output:
![blog-preventing-xss-high-vulns](https://snyk.io/_next/image/?url=https%3A%2F%2Fres.cloudinary.com%2Fsnyk%2Fimage%2Fupload%2Fv1682439081%2Fblog-preventing-xss-high-vulns.jpg&w=2560&q=75)

The third option I want to show you, is the web UI. For this output I used the git integration with Snyk and connected my GitHub repository to the Snyk Web UI using the dashboard at [++https://app.snyk.io++](https://app.snyk.io/). This solution scan's the code that is committed to my repository for security vulnerabilities.

Web UI output:
![blog-preventing-xss-snyk-code-report](https://snyk.io/_next/image/?url=https%3A%2F%2Fres.cloudinary.com%2Fsnyk%2Fimage%2Fupload%2Fv1682439081%2Fblog-preventing-xss-snyk-code-report.jpg&w=2560&q=75)

All three different scanning options show me that there are two distinct XSS security issues I need to address --- with Snyk Code pinpointing their exact location in my code. Let's break them down and see how we can mitigate them.

### Reflective XSS {#h3-2-reflective-xss}

Reflective XSS is a type of XSS attack that occurs when a user injects malicious code into a web application that is then reflected back to the user as part of a response. In the example I provided, if the user input was not properly validated or sanitized before being written to the response, a malicious user could inject a script that would be executed by other users who view the web page. This type of XSS attack is often used to steal user data, modify website content, or perform other malicious actions.

The code above retrieves the user's name from the HTTP request parameter and then writes it directly to the HttpServletResponse object using:

```
1writer.write("<div class=\"panel-heading\"><h1>"+ param + "</h1></div>")
```


This code is vulnerable to XSS attacks because it does not properly validate or sanitize the user input. For example, a malicious user could inject HTML or JavaScript code into the "name" parameter, which would then be executed by other users who view the web page.

For instance: `.../direct?param=<script>alert(document.cookie);</script>` might reveil your personal cookie information. This means we can also send this information to another server without you knowing it.  

Snyk Code caught this mistake for me by pointing out the XSS on line 93.

### Stored XSS {#h3-3-stored-xss}

Stored XSS, on the other hand, is a type of XSS attack where the malicious code is stored on the server and then served to all users who access the affected page. In the example I provided, if the user input was not properly validated or sanitized and was instead stored in a database, a malicious user could inject a script that would be served to all users who view the affected page. This type of XSS attack can be particularly dangerous because it can affect a large number of users and may persist even after the initial injection has been fixed.

The code above retrieves a product from the `ProductService` and then displays them on the fields as part of the output string. However, this code is vulnerable to Stored XSS attacks because it does not properly validate or sanitize the input that comes from the database. Sanitization is particularly important if you aren'tt sure who has permission to write the database. For example, a malicious user could submit a product desciption that includes HTML or JavaScript code, which would be stored in the database and served to all users who visit the product view.

Snyk Code pointed out this potential XSS problem on line 103, where we insert the `product.description` into the output String without validating or sanitizing it.

Mitigating XSS vulnerabilities with Snyk Code {#h2-4-mitigating-xss-vulnerabilities-with-snyk-code}
---------------------------------------------------------------------------------------------------

To prevent XSS vulnerabilities, it is important to properly validate and sanitize user input before writing it to the response. Snyk Code already helps us by pointing out possible solutions. One way to do this is to use a library like [++Apache Commons Text++](https://commons.apache.org/proper/commons-text/)to encode the input and prevent malicious code from being executed.
![blog-preventing-xss-string-path](https://snyk.io/_next/image/?url=https%3A%2F%2Fres.cloudinary.com%2Fsnyk%2Fimage%2Fupload%2Fv1682439081%2Fblog-preventing-xss-string-path.jpg&w=2560&q=75)

Using the `escapeHtml4()` function, we can make sure that code in both reflective and stored XSS is escaped so that it will not be executed when loading the page.  

Obviously, more libraries can perform similar escaping. In addition to [++Apache Commons Text++](https://commons.apache.org/proper/commons-text/), you can look at the [++OWASP Encoder++](https://owasp.org/www-project-java-encoder/). If you work with [++Spring++](https://spring.io/), you can also use Spring's [++HtmlUtils.htmlEscape++](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/util/HtmlUtils.html).

When using Apache Commons text, the properly escaped code could look like this:

```
1@GetMapping("/direct")
2public void directLink (@RequestParam String param, HttpServletResponse response) throws IOException {
3   Product prod = productService.getProductByName(param);
4
5   response.setContentType("text/html");
6   var writer = response.getWriter();
7   writer.write(head);
8   writer.write("<div class=\"panel-heading\"><h1>"+ StringEscapeUtils.escapeHtml4(param) + "</h1></div>");
9
10   String output = "<div class=\"panel-body\">" +
11           "<ul>" +
12           "<li>%s</li>" +
13           "<li>%s</li>" +
14           "<li>%s</li>" +
15           "</ul>" +
16           "</div>";
17
18   writer.write(String.format(output,
19             StringEscapeUtils.escapeHtml4(prod.getDescription()),
20             StringEscapeUtils.escapeHtml4(prod.getProductType()),
21             StringEscapeUtils.escapeHtml4(prod.getPrice())));
22
23   writer.write(foot);
```


### Be careful with templating frameworks {#h3-5-be-careful-with-templating-frameworks}

Templating frameworks like Thymeleaf can help protect agains XSS vulnerabilities. Thymeleaf is a popular templating engine for Java that includes built-in support for HTML escaping, which helps prevent XSS attacks by encoding any user input that is included in the rendered HTML.

However it strongly depends on how you create the template. For example, here's how you might use Thymeleaf to render a product similar to the example before:

```
1<div class="product" th:each="product : ${products}">
2  <p th:text="${product.name}"></p>
3  <p th:text="${product.type}"></p>
4  <p th:text="${product.price}"></p>
5  <p th:utext="${product.descr}"></p>
6</div>
```


In this example, the `th:text` attributes will be escaped, but the `th:utext` attribute won't. This `th:utext` attribute renders the comment text without escaping any HTML tags or special characters and is potentially vulnerable to XSS. When using a particular framework, knowing how certain elements behave is essential.

Catching XSS before you deploy to production {#h2-6-catching-xss-before-you-deploy-to-production}
-------------------------------------------------------------------------------------------------

Preventing XSS attacks is a critical concern for developers working on Java web applications.

Identifying and addressing XSS vulnerabilities as early as possible in the development process is essential.

Although sanitizing user input can effectively mitigate XSS attacks, it may not always be sufficient.

Look at resources like the [++OWASP XSS Cheat Sheet++](https://cheatsheetseries.owasp.org/cheatsheets/Cross_Site_Scripting_Prevention_Cheat_Sheet.html) and the [++Snyk Learn lesson on XSS++](https://learn.snyk.io/lessons/xss/java/) to stay up-to-date with the latest threats and best practices for XSS prevention.

Moreover, it's important to leverage the right tools to catch XSS mistakes and other security issues before they make it to production.

Snyk Code is a valuable free tool for identifying potential security vulnerabilities early in the development cycle.

By taking a proactive approach to XSS prevention and using the right resources and tools, developers can help ensure the security and integrity of their Java web applications.
