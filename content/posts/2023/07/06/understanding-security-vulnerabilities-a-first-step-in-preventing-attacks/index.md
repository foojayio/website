---
title: "Understanding Security Vulnerabilities: Preventing Attacks"
date: "2023-07-06T12:49:42+00:00"
lastmod: "2023-07-06T12:50:22+00:00"
description: "What are the common vulnerabilities we need to be aware of? How do they look & how can we better protect ourselves from these common attacks?"
authors:
  - "shai-almog"
image: "Shai_Almog_hacker_with_his_back_to_the_camera_wide_angle_shot_7c4fd868-3eca-43d3-b98e-76bff1a07b49.jpg"
categories:
  - "Security"
  - "Tutorials"
related_posts:
  - "manifold-vs-lombok-enhancing-java-with-property-support"
  - "operator-overloading-in-java"
  - "the-reason-java-is-still-popular"
frozen: false
---

When I was a teenager, our local telephone company introduced a new service - the premium phone calls (AKA 1-900 numbers).

The fun part was that we discovered a workaround to these charges by dialing the sequential local numbers which these 1-900 numbers would redirect to.

If the "support number" for the 1-900 was 555-555 we would dial every number between 555-455 and 555-655 until we hit the jackpot...

Hours were spent dialing these numbers, leading us to make numerous calls for free. This attack is still prevalent today, and it's called Insecure Direct Object References (IDOR).

{{< youtube HTp3cW1Sfq8 >}}

## IDOR

In the digital world, IDOR is similar to our teen exploits. It means trying various ID numbers in sequence until we find the right one. A few years ago, a social network named Parler, which listed users by a sequential numeric ID, fell victim to this type of attack when a user was able to request and download the full list of users on that network.

E.g. their URLs looked like: `https://site.com/viewUser?id=999`

All a person needs to do is loop over valid sequential numbers and send the request to get the user information of everyone on that site. This is trivial and can be accomplished by anyone with relatively low technical skills.

To avoid such an attack, it is advised not to expose guessable or sequential numeric IDs to the end users. While UUID might seem long, it offers a more secure alternative. Additionally, request checking should be implemented. If a user is requesting information about a user they aren't connected to, that request should be blocked. Other effective mitigations include setting request quotas and delays between requests to prevent a massive data grab.

I won't go into these since they are typically implemented in the API gateway layer during provisioning. You can write this in code but it's a challenging task as you might have many endpoints with a great deal of complexity. The rule of thumb is to write as little code as you possibly can, more code means more bugs and a wider attack surface for a malicious hacker.

## Vulnerabilities and Exploits

A crucial term in application security is **vulnerability** . It's a weakness or bug that can be likened to a hole in the fence surrounding your house. These vulnerabilities can reside in your code, libraries, Java itself, the operating system, or even physical hardware. However, not every vulnerability is **exploitable**. Just like a hole in your fence may not necessarily grant access to your house, vulnerabilities don't always mean your code can be hacked. Our aim is to plug as many holes as possible to make the task of exploiting our system more difficult.

I know the onion metaphor is tired by now but for security it makes a lot of sense. We need to enforce security at every layer. In the Log4Shell exploit that was exposed last year we had a major zero-day vulnerability. A zero-day vulnerability is a newly discovered vulnerability that no one knew about before, like a new hole in the fence.

The Log4Shell vulnerability relied on people logging information without validating it first. This was a bad practice before the vulnerability was known. If you used a Log4J version that had that vulnerability, but sanitized your data. You would have been safe despite that vulnerability.

## SQL Injection

SQL injection involves building your own queries by concatenating a query string manually. Let's look at vulnerable SQL like this:

```
String sql = "SELECT * from Users WHERE id = " + id;
```

Considering the sample URL we used before we could request a URL like this: `https://site.com/viewUser?id=1 OR true=true`.

This URL would result in an attacker fetching all the users as the condition will become:

```
SELECT * from Users WHERE id = 1 OR true=true
```

Which is always true. This is a relatively tame outcome. SQL statements can be chained to drop tables deleting the entire database. A solution to this is using the prepared statement syntax, where the implementation treats all the content as a string. This prevents the SQL keywords from being exploited e.g.:

```
PreparedStatement sql = connection.prepareStatement("SELECT * from Users WHERE id = ?");
sql.setString(1, id);
```

In this situation when we set the value for the id it will treat it as a string even if there are SQL keywords or special characters. Using APIs like JPA (Spring Data, Hibernate, etc.) will also protect you from SQL injection when using similar APIs.

## Serialization

Java serialization is another common vulnerability. The lesson here is to avoid using serialization or requiring it, and instead running your app with a filter that blocks certain types of serialization.

This is something I [discussed in a previous post](https://debugagent.com/java-serialization-filtering-prevent-0-day-security-vulnerabilities) so there's no point repeating it.

## Cross-site Scripting (XSS)

Cross-site scripting, or XSS, is a complex attack. It involves injecting malicious scripts into websites that then run on every person's browser visiting the page. This can lead to theft of user cookies, which in turn allows the attacker to impersonate users on the website. Protecting against XSS involves validating user-supplied data, treating it as display content, not executable code.

Let's say I have a submit form that accepts user input that is saved to the database. Like the comments section in the blog. I can post in JavaScript code that would submit the user's cookies to a site I control. Then I can steal this information and impersonate a user. This is a very common and surprising attack, it's often performed by encoding the script into a link sent by email.

These are three types of XSS attacks:

* Stored XSS (Persistent) - The attack I described here is a form of stored XSS since the comment I would submit is saved in the database. At this point, every user that looks at the comment is attacked.
* Reflected XSS (Non-persistent) - In this form, the attacker sends a link to a user (or convinces the user to click on a link) that contains the malicious script. When the user clicks the link, the script runs, sending their data to the attacker. The script is embedded in the URL and reflected off the web server. This is usually part of a phishing attack.
* DOM-Based XSS - This type of attack occurs entirely in the victim's browser. The web application's client-side scripts write user-provided data to the Document Object Model. The data is subsequently read from the DOM by the web application and outputted to the browser. If the data was interpreted as JavaScript, it's executed.

Protecting from XSS requires diligent validation of all input. We can protect against these attacks by checking if user-provided data is of the correct form and contains no malicious content. We must ensure any user-supplied content is treated as display content, not executable code.

There are many ways to validate user-submitted data and the Jsoup library contains one such API. Notice that Spring Boot contains XSS protection as part of the security configuration but I'll cover that later.

```java
personName = Jsoup.clean(personName, Whitelist.basic());
```

Notice that validating input is a recurring theme when it comes to security vulnerabilities. As developers we often strive to provide the most generic and flexible tooling, this works against us when it comes to security vulnerabilities. It's important to limit input options even when we don't see a problem.

### Content-Security-Policy (CSP)

One of the ways to carry out an XSS attack is by including foreign code into our own website. One way to block this is using special HTTP headers to define which sites can include our site. This is a rather elaborate process but the nice thing is that Spring Security handles that nicely for us as well.

### HttpOnly Cookies

Cookies can be created in the browser using JavaScript. This is a bad practice. Ideally, cookies should always come from the server and be marked as HTTP only (and HTTPS only). This blocks JavaScript code from accessing the cookie.

That means that even if a script is added somehow or a bad link is clicked, it won't have access to the cookie value. This mitigates XSS attacks so even if your site vulnerable the attack can't steal the cookie. We can enable HttpOnly cookies when we set the cookie in the server code.

## Unvalidated Redirects and Forwards

Another security concern is unvalidated redirects and forwards. Here, an attacker creates a URL that looks like it's coming from your domain, but redirects to another malicious site. The solution lies in validating and restricting included or submitted URLs, and never sending users blindly to third-party sites.

Lets say we have a login page. After we login we're shown a splash screen and then we're sent to the actual destination. This seems simple enough but some people need to go to page X and others need to go to page Y. We want to keep the code generic so we accept the destination URL as an argument. That way the login code can decide where to go next and we don't need to know about all the user types e.g.: `https://bug.com/postLogin?dest=url`.

The problem is that a person can create a URL that looks like it's coming from our domain, but pass in another URL as the last argument. Our users can end up on a malicious site without realizing they were redirected to a new site.

The solution is to validate and restrict included or submitted URLs and never send a user blindly to a third-party site.

## Server Side Request Forgery (SSRF)

SSRF attacks are similar conceptually, in these attacks our server performs a request based on the request we received. Our server can be manipulated to request arbitrary URLs for an attacker. This can serve as the basis for information theft, denial of service attacks, etc.

## Cross-Site Request Forgery (CSRF)

CSRF is another challenging issue where an attacker tricks users into hacking their own account. Typically, we're logged into a website. Our credentials and cookies are already set. If a different website knows we're logged in to a website it can trick us and get us to hack ourselves...

Let's say you visit a website and it has a big button that you can press for your chance to win a million dollars. Would you press it?

What's the harm right?

If that button is a form that submits the request directly to your bank, this can be used to steal currency and more.

The standard solution is to add a server-generated token into the HTML that changes with every request, thus validating that the HTML came from the legitimate site. This is a standard strategy supported by Spring Security.

We can also set our cookies to the SameSite policy which will mean a user won't be logged in if he's on a separate site. Turning this on for your login information is probably a good idea.

## Final Word

In conclusion, while we did not delve into a lot of code writing in this post, the objective was to shed light on common security vulnerabilities and attacks, and how to prevent them. Understanding these concepts is fundamental in building secure applications, and the more we're aware, the better equipped we are to thwart potential threats.

There are many tools for security validation, if you use a decent linter like SonarQube you would be on your way to a more secure app. Snyk also has great tooling that can help catch various vulnerabilities.

This paragraph from the post probably sums up the most important aspects:
> Notice that validating input is a recurring theme when it comes to security vulnerabilities. As developers we often strive to provide the most generic and flexible tooling, this works against us when it comes to security vulnerabilities. It's important to limit input options even when we don't see a problem.
