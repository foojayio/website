---
title: "Secure Code Review Best Practices (Part 2)"
date: "2021-03-18T15:14:31+00:00"
lastmod: "2021-03-18T15:14:33+00:00"
description: "Code reviews are hard to do well. Particularly when you’re not entirely sure about the errors you should be looking for!"
canonical: "https://snyk.io/blog/secure-code-review/"
authors:
  - "bmvermeer"
image: "Favicon-3-2.png"
categories:
  - "Security"
related_posts:
frozen: false
---

Code reviews are hard to do well. Particularly when you're not entirely sure about the errors you should be looking for!

In [part 1 of this series](https://foojay.io/today/secure-code-review-best-practices-part-1/), we focused on four best practices, such as **sanitization** , **storing secrets,** and **scanning your application** are explained so take a quick look if you missed the first part.

Let's move on to the second and final part of this topic, tips 5 through 8!

### 5. Enforce the Least Privilege Principle

In addition to authentication comes authorization. They sound similar but are quite different. As we saw in point 4, authentication proves a user or service is indeed who they say they are, while authorization goes further to ensure that person or service is allowed to perform whatever task or action they're trying to perform. We know we need to check for this and ensure those users, services, or processes are running or exist in a role that has the authority to undertake such an action. However, from a coding point of view, it's often all too easy to give more access than is actually required.

The principle of least privilege states that every module (such as a process, a user, or a program, depending on the subject) must be able to access only the information and resources that are necessary for its legitimate purpose. So in essence, give people or processes the bare minimum of privileges and permissions they need to achieve their goal.

A great way to test for this is to ensure you write specific automatic unit and integration tests that not only test the happy path but, more importantly, test the unhappy security related cases. These tests should successfully authenticate, but try to perform operations they're not entitled to perform. These tests should always be added when altering the roles your application runs under or introduces new resources that require you to be in a specific role to perform.

### 6. Handle Sensitive Data with Care

Exposing sensitive data --- like personal information or credit card numbers of your client --- can be harmful. But even a more subtle case than this can be equally harmful. For example, the exposure of unique identifiers in your system is harmful, if that identifier can be used in another call to retrieve additional data.

First of all, you need to look closely at the design of your application and determine if you really need the data. On top of that, make sure that you don't expose sensitive data, perhaps via logging, autocompletion, transmitting data etc.

#### Storing Sensitive Data

If you need to persist sensitive data like Personally Identifiable Information (PII) or financial details, be aware that proper encryption is used. You probably want and need to be GDPR compliant but, first and foremost, you don't want your clients data to be compromised. The encryption should either be a strong 2-way encryption algorithm, if you need to retrieve the data in its original form, or a strong cryptographic hashing algorithm, if you need to store passwords. Don't fall into the trap of writing your own encryption --- find out what encryption you need to use and use a well-vetted library to handle the encryption for you. For instance, use BCrypt for password hashing and encryption algorithms Triple DES, RSA and AES to encrypt the data you need to retrieve. Most importantly, keep reviewing if the algorithms you use are still secure enough. What is perfectly fine today, might be compromised tomorrow.

Also keep in mind that sensitive data can live in memory. If you change a password in your system prevent temporary storage in an immutable data type. For example, if you use a String in Java to store your password in memory, the original value will be in memory until the garbage collector removes it as String is immutable. A byte array would be a better choice in this case.

#### Transporting Sensitive Data

If you need to transfer sensitive data, check if the connection is secure. Sensitive data should only be transferred encrypted and over a TLS. You need to also make sure that the TLS version is up to date. Obviously sending somebody's credit card details as a query parameter or as plain text in the payload over HTTP is not considered safe at all.

#### Session Data

Last but certainly not least, consider session data also as sensitive data. The advice is not to store sensitive data in cookies at all, but rather use a session identifier and store the data in a server-managed session. Also, make sure that cookies are encrypted and have a decent length (eg. 128 bits). Check if attributes like **HttpOnly** , **Secure,** and **SameSite** on the cookie are set correctly and that they expire in a reasonable amount of time. Note that if a user logs out client-side, the session must be invalidated so it cannot be used elsewhere.

### 7. Protect Against Predictable Attacks

We can safely assume that attackers will continue to hack our applications using predictable, well-known, and recognized attack vectors. The general lack of knowledge about common vulnerabilities and how they can be exploited, often leads to duplicating the same security mistakes over and over in future code. Take a look at the [OWASP Top 10](https://owasp.org/www-project-top-ten/) vulnerabilities and understand how these common exploits work. Here are some tips to look out for when trying to avoid some of the most common vulnerability types.

#### XSS

The core premise behind a [Cross-site Scripting (XSS) attack](https://snyk.io/learn/cross-site-scripting/) is when a user injects malicious data to your application which ends up in a context --- such as an HTML document --- that can be executed to share sensitive information or other malicious activity. If you're planning to allow your users to submit data within your site that is displayed on your page, you need to learn how to defensively prevent XSS from happening. There are a number of ways to reduce the chances of XSS attacks, for example, HTML encoding dangerous characters. Such characters include \&, \<, \>, " and '. Disallowing such characters, or sanitizing them, will prevent an attacker from breaking out from the HTML tag and execute malicious code. To make this harder, we can HTML encode any HTML characters that are passed to us, so that they are not represented in the form that can break out of an HTML tag, as follows:

| HTML entity | HTML encoding |
|-------------|---------------|
| \&          | \&amp;        |
| \<          | \&lt;         |
| \>          | \&gt;         |
| "           | \&quot;       |
| '           | '             |

Similarly, we can achieve that with tag attributes, event handlers, or even style properties. It's common to use sanitization libraries to help us whitelist what characters that we should allow. Using existing sanitization libraries also means that we don't have to be security gurus to make sure we pick up every eventuality. This is particularly good for more junior developers.

#### SQL and NoSQL injection

One of the reasons why [SQL injection](https://snyk.io/learn/sql-injection/) is most attractive to an attacker is because it provides them with direct access to the data they inevitably want to gain access to. All too often, a hack is merely just a way for an attacker to learn or gain knowledge about the system that they are trying to breach. This often means that an attacker has to do more work to find out where the data lives and how they can gain access to that data. SQL injection, on the other hand, is a mechanism that, if successfully hacked, can provide an attacker with direct access to sensitive information stored in a database. This does not only hold for SQL database --- many NoSQL databases can be compromised in a similar way.

Both XSS and SQL injection are very common attacks. You should know how they work and how to spot them in your code. The lack of sanitization of input anywhere in the system is a big red flag for XSS. For SQL injection you should check if query parameterization is implemented. You need to make a distinction between the query and the parameters. Binding the parameters to a particular type before making them part of the query (and do proper sanitization), will prevent these kinds of attacks.

There are many other vulnerability types that your application might be susceptible to, some more than others. You should take the time to learn about which ones affect your application most, from the code your teams directly produce through, to the types of libraries your application depends upon.

### 8. Statically Test Source Code Automatically

A static code analysis tool or linter is a very powerful tool for developers. By statically looking at the code you and your team wrote, you can point out a number of things like programming errors, [bugs](https://en.wikipedia.org/wiki/Software_bug), stylistic errors, and suspicious constructs. With this bug and error detection, a linter can, in many cases, also indicate if a security-related bug slipped into your source code. Depending on the static tool you use and the ecosystem you are operating in, a static code analyzer can point out issues like SQL injections and code vulnerabilities.

Linter can be very useful but will produce a lot of false positives. Since all linters are rule-based and not looking at the full context of your code, there will be a bunch of cases where a linter will flag your code as a bug or a security issue while this is not the case. Nevertheless, if you fine-tune the ruleset, a linter can prevent nasty mistakes. Although these tools can be used in a lot of different forms --- for, example, manually with a command-line instruction or as part of an IDE --- the best way is to automate these processes as much as possible. For instance, as part of your build process, or maybe when a new pull request is submitted to your repository.

Either way, automation is key. Well-known static code analyzers you can use are [SonarSource](https://www.sonarsource.com/) (with a free and open source tier) and Veracode. You might also want to take a look at [Snyk Code](https://snyk.io/blog/java-code-analysis-solving-java-security-issues-in-spring-mvc-app/) that does specific static analysis on security issues. However, depending on your context, you might need a more specific one. Check this[list of static code analyses tools](https://en.wikipedia.org/wiki/List_of_tools_for_static_code_analysis)for both language-specific and multi-language solutions that may fit your needs. If you don't have automatic static code analyzer in place, you can either use it during your manual code review or, even better, automate it in your process so people can catch obvious problems even sooner.

### Conclusion

Reviewing someone's code is hard, specifically when you also have to look at security issues.

Combining the 4 best practices from [part 1](https://foojay.io/today/secure-code-review-best-practices-part-1/) with the best practices in this part already gives you 8 pointers to improve your skills.  

Check out all 8 best practices in [the handy one-pager](https://snyk.io/wp-content/uploads/Snyk-Secure-Code-Review-Cheat-Sheet.pdf) we created.  

This article was [originally posted on the Snyk.io blog](https://snyk.io/blog/secure-code-review/) and is used with permission.
