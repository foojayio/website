---
title: "Secure Code Review Best Practices (Part 1)"
slug: "secure-code-review-best-practices-part-1"
date: "2021-03-11T16:58:46+00:00"
lastmod: "2021-09-28T08:56:35+00:00"
description: "Code reviews are hard to do well. Particularly when you’re not entirely sure about the errors you should be looking for!"
canonical: "https://snyk.io/blog/secure-code-review/"
authors:
  - "bmvermeer"
image: "Favicon-3-2.png"
categories:
  - "Security"
tags:
related_posts:
  - "5-tips-to-create-secure-docker-images-for-java-applications"
  - "eight-debugging-tips-for-intellijidea-users-you-never-knew-existed"
  - "java-encryption-and-hashing"
frozen: false
---

Code reviews are hard to do well. Particularly when you're not entirely sure about the errors you should be looking for!

Be sure when you're reviewing code to understand that all code isn't written equal! Think also about what lies behind the code that you're reviewing and thus the data and assets you are trying to protect. This working knowledge is something that isn't easy to add into a checklist.

However, using the tips below, alongside your domain knowledge, will assist you in deciding where you should spend more of your time and where you should expect higher risk and different types of attacks.

**Note**: A great way of determining where your highest risk areas exist is by creating attack trees that will show you where to focus your efforts first/most.

### 1. Sanitize \& Validate All Input

Modern web applications have to interact with all sorts of third-party input. Although direct input from an end-user in the browser, for instance, is an obvious one. As developers, we all know that a user will insert unexpected things when this is possible. Making sure that direct input from a user is validated and sanitized accordingly is considered a core best practice to ensure that applications are not vulnerable to content injection. However, direct user input is by far not the only thing you should check. Basically every input that comes from the outside boundaries of your system should be considered and treated as potentially harmful. Think about things like:

* data feeds
* files
* events --- an event-driven system, such as working closely with platforms like Functions as a Service
* data responses from other systems
* cookies

On top of all this, input that seems under your control at first sight, might be harmful. Think about it --- when a malicious user is able to connect to your database directly, there is a backdoor to insert, for example, [malicious code](https://snyk.io/learn/malicious-code/) that will be executed in your system. The same principle holds for:

* command-line parameters
* environment variables
* system properties
* data storage

All input, even the input that seems to be controlled by you, should be validated and sanitized. Check if the input makes sense. Using the type system in a type-safe language, can help you a lot. In addition, check on the format, range, size, file type, file name and take nothing for granted. User input should be sanitized, preferably using a well-vetted library, before it will be stored or used anywhere.

### 2. Never Store Secrets As Code/Config

It's all too easy to store credentials, tokens or other secrets as variables or constants, because hey --- we're just testing it to make sure it's working. But just as easily this code makes its way into your code repository because you forgot to remove it. We urge you to make sure there's nothing sensitive in the code you look through. If you're using a git-based code repository, there are a bunch of great tools available, like [git-secrets](https://github.com/awslabs/git-secrets), that can statically analyze your commits, via a [pre-commit Git Hook](https://githooks.com/), to ensure you're not trying to push any passwords or sensitive information into your repo. Commits are rejected if the tool matches any of the configured regular expression patterns indicating that sensitive information has been stored improperly. This may slow down pushes a tiny bit, but it's well worth it.

Having team-wide rules that prevent credentials from being stored as code is a great way to monitor bad actions in the existing developer workflow. Use tools like [Vault](https://www.vaultproject.io/) to help manage your secrets when in production. Lastly, consider using an identity and user management toolchain, like [Keycloak](https://github.com/keycloak/keycloak) (currently maintained by a number of developers in Red Hat) as well as others.

There are many ways to avoid putting credentials into your repository in the first place and it's best if you tried to implement as many as you can; however, there's always the chance some sensitive information may sneak in. You should also consider regularly auditing your repos, making use of tools like [GitRob](https://github.com/michenriksen/gitrob) or [truffleHog](https://github.com/dxa4481/truffleHog), both of which scan through your codebase, searching for sensitive information via pattern matching.

### 3. Test For New Security Vulnerabilities Introduced by 3rd Party Open Source Dependencies

Modern application development is heavily dependent on third-party libraries. By using package managers like npm, Maven, Gradle PyPI, or any equivalent, we have easy access to publicly available libraries and frameworks. As developers, we want to focus on specific business logic and not so much on creating boilerplate functionality, using frameworks and libraries to do the heavy lifting is an obvious choice..

There's a good chance you don't know how many direct dependencies your application uses. When looking at an average project, the amount of your code can be as little as 1% --- the rest is imported libraries and frameworks. A lot of code that is put into production is simply not ours, but we do depend on it heavily. It's also extremely likely you don't know how many transitive dependencies your application uses. Larger frameworks nowadays are depending on other libraries that also depend on other libraries. By pulling in a single library or framework, chances are that you are pulling in at least a dozen more libraries and/or frameworks, that you are not always aware of. This way dependencies are making up for the majority of your overall application. Attackers target open source dependencies more and more, as their reuse provides a malicious attacker with many victims. For this reason, it's important to ensure there are no known vulnerabilities in the entire dependency tree of your application.

Let's use [Snyk](https://app.snyk.io/) as an example. Snyk statically analyzes your project to find vulnerable dependencies you may be using and helps you fix them. You can test your repos through Snyk's UI to find issues, but also to keep users from adding new vulnerable libraries by testing pull requests and failing the test, if a new vulnerability was introduced. Automated fix PR's are also an option.

Depending on how you like to work, you can choose to connect your repository to the Snyk UI or scan the project on your local machine using the CLI (check the [CLI cheat sheet](https://snyk.io/blog/snyk-cli-cheat-sheet/)), an integration in your build system, or a plugin in your IDE. From left (the developers' local machine), to completely right (your system in production), and every step in between, you should analyze your dependencies automatically to ensure quick feedback.

### 4. Enforce Secure Authentication

Authentication verifies that a user, service, or entity (internal or external) is who they say they are. This could be as simple as a user passing you their credentials, or a server providing you with its TLS certificate to validate it is indeed the server it claims to be. Authentication doesn't tell you what the user or service is allowed to do, but rather that they are indeed that user or service. Let's cover a few authentication best practices you should make note of:

* **Assume they're not who they say they are.** You should work under the principle that they're not who they say they are until they have provided the credentials to prove it. Assuming the user or service shouldn't have access to your data is, of course, the safest way of behaving. Make sure your code reflects that.
* **Enforce password complexity.** In the case of users, consider being more lenient, in terms of the usernames, particularly when using email addresses. For instance, there's little value in distinguishing between [\[email protected\]](/cdn-cgi/l/email-protection) and [\[email protected\]](/cdn-cgi/l/email-protection), whereas it's important to enforce password complexity (at least 1 uppercase character, 1 lowercase character (a-z), 1 digit (0-9) and 1 special character) and length ([NIST SP800-132](https://csrc.nist.gov/publications/nistpubs/800-132/nist-sp800-132.pdf)). I understand, it's difficult to remember all those random --- or not, long, and complicated passwords. But, come on! It's the year 2020 and password managers are here to save you!
* **Re-authenticate before sensitive operations.** Asking users for their credentials --- before transferring monies, or performing sensitive actions --- mitigates potential[Cross-Site request forgery](https://en.wikipedia.org/wiki/Cross-site_request_forgery) (CSRF) and session hijacking attacks. An attacker might perform these sensitive tasks without ever having provided the user's credentials. This security measure, while inconvenient to your users, can protect them in long term.
* **TLS client authentication.** TLS Client Authentication, also known as two-way TLS authentication, requires both the browser and server to authenticate, each sending their TLS certificates in a TLS handshake. This is achieved by a user or service obtaining a client certificate from the server and providing it on subsequent interactions. The user may need to install the certificate if using a browser.

### Coming Up Next

This was part 1 of a two-part series on Secure code review. Check out [part 2](https://foojay.io/today/secure-code-review-best-practices-part-2/)right away.

You can [download the handy one-pager](https://snyk.io/wp-content/uploads/Snyk-Secure-Code-Review-Cheat-Sheet.pdf) with all the tips and hang it above your bed.

You can also take a look at the [original article](https://snyk.io/blog/secure-code-review/) on Snyk.io if you want more information.
