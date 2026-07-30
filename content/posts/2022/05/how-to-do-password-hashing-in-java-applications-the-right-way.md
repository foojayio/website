---
title: "How to do password hashing in Java applications the right way"
slug: "how-to-do-password-hashing-in-java-applications-the-right-way"
date: "2022-05-12T15:04:55+00:00"
lastmod: "2022-05-12T15:04:57+00:00"
description: "The first rule of password hashing algorithms is: Don't write your own password hashing algorithm! Let's learn how we do password hashing."
canonical: "https://snyk.io/blog/password-hashing-java-applications/"
authors:
  - "bmvermeer"
image: "https://foojay.io/wp-content/uploads/2020/04/Favicon-3-2.png"
categories:
  - "Security"
tags:
related_posts:
enlighterjs: true
frozen: false
---

There are multiple ways to store sensitive passwords. And while having choices can be great, in the context of password storage, picking wrong can be a security nightmare. With that in mind, let's *hash* out some of your options 🥁🥁.In this article we'll discuss how you should hash passwords in your Java applications. While you can apply these principles to any ecosystem, we'll specifically showcase the best way to handle password hashing in Java.

The first thing you should think through when deciding how to store sensitive passwords is whether or not you even *want* to take care of authentication and authorization yourself. There are many services available that can take care of this for you. Using an authentication and authorization service removes the burden of safely storing passwords in your application. There are many commercial services like [Auth0](https://auth0.com/) and [Okta](https://developer.okta.com/) that make this simple. As long as the provider you choose supports open standards like OpenID Connect, you'll be able to easily integrate them into your Java applications (Spring Boot, for example). Additionally, your provider might be able to support MFA (multi-factor authentication) as well, to further improve your end user security.

If you *do* need to store passwords in your Java application yourself, then this article is right for you.

Hashing passwords for storage {#h2-0-hashing-passwords-for-storage}
-------------------------------------------------------------------

If you store usernames and passwords in your application, whatever you do, **do not** **store** **passwords in plain text** (I know, I know, seem obvious!)! If your system gets breached, an attacker will be able to simply read these plain text passwords and impersonate users.

To avoid the dreaded plain text password, you'll want to store a hashed version of your users' passwords. For that, you'll need a password hashing algorithm.

### What is a password hashing algorithm? {#h3-1-what-is-a-password-hashing-algorithm}

There are different types of hashing algorithms, but password hashing algorithms are one-way functions specifically designed for working with passwords. Password hashing algorithms convert a plain text password into a string of data you can safely store in a database that can never be reversed (you can transform a plain text password into a hash, but it's impossible to transform a hash into a password, hence the name "one-way" function).

### How does password hashing work? {#h3-2-how-does-password-hashing-work}

The way you use password hashing algorithms is during user authentication. Every time a user sends you their password (in plain text), the password is hashed right away. Then the hash is either stored (for registration purposes) or matched against the user's stored hash to verify that the password is correct (authentication).

There are lots of hashing algorithms, but very few of them are good choices for passwords. Many types of hashing functions have specific use cases like checking the integrity of a file, for instance. These hashing algorithms are a poor choice for passwords.

What you need is a **strong password hashing algorithm** for your users' passwords. A good password hashing algorithm consumes a fair amount of computational power and memory. When talking about password hashing algorithms: the more computing resources they use, the better!

Strong password hashing algorithms are slow to run, which makes brute force attacks less effective. There's a lot more depth we could go into here, but the TL;DR of it is this: when it comes to passwords you want to use a strong, resource-intensive password hashing algorithm to provide your users with the most possible safety.

What's the best password hashing algorithm? {#h2-3-what-s-the-best-password-hashing-algorithm}
----------------------------------------------------------------------------------------------

The first rule of password hashing algorithms is: **Don't write your own password hashing algorithm!** The exception is if you are a skilled cryptographer. If you're not, please rely on the standards provided by the industry and choose vetted algorithms and libraries to use within your application.

<br />

According to the [OWASP foundation](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html), only a few algorithms should be considered for modern password hashing. Let's take a look at the recommended password hashing algorithms!

### ARGON2ID {#h3-4-argon2id}

The best algorithm to use at the moment (in 2022) is Argon2id. [Argon2](https://en.wikipedia.org/wiki/Argon2) is a key derivation function that was selected as the winner of the [Password Hashing Competition](https://en.wikipedia.org/wiki/Password_Hashing_Competition). It has three different versions:

* **Argon2d:** maximizes resistance to GPU cracking attacks
* **Argon2i:** optimized to resist side-channel attacks
* **Argon2id:** a hybrid version

The Argon2id version is advised for passwords because of the balanced approach against both side-channel and GPU-based attacks. You can configure the Argon2id algorithm by supplying three parameters: memory size (m), iterations (t), and degree of parallelism (p).

Currently, the advice is to use Argon2id with at least 15 MiB of memory, an iteration count of 2, and 1 degree of parallelism. The algorithm already includes automatic salting for every individual user to prevent pre-computed attacks, so nothing special needs to be done here.

#### Using Spring Security Crypto with Bouncy Castle

The easiest way to implement Arong2id in your Java application is to use the `spring-security-crypto` library. Although it is part of the Spring framework, you do not have to use the rest of the Spring framework.

The `spring-security-crypto` library has a `Argon2PasswordEncoder` that you can use. I personally believe the naming is a bit off, as this is technically not an encoder but a hasher. The Spring library uses `bouncycastle` as a dependency that holds a full Java-based implementation of the Argon2 algorithm. The `spring-security-crypto` library can be considered an intuitive interface on top of `bouncycastle`, making it easier to use. Next to `bouncycastle`, you also need a dependency to `common-logging` for, you might have guessed it, logging.

<pre class="EnlighterJSRAW" data-enlighter-language="xml" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;dependency&gt;
   &lt;groupId&gt;org.springframework.security&lt;/groupId&gt;
   &lt;artifactId&gt;spring-security-crypto&lt;/artifactId&gt;
   &lt;version&gt;5.6.2&lt;/version&gt;
&lt;/dependency&gt;
&lt;dependency&gt;
   &lt;groupId&gt;commons-logging&lt;/groupId&gt;
   &lt;artifactId&gt;commons-logging&lt;/artifactId&gt;
   &lt;version&gt;1.2&lt;/version&gt;
&lt;/dependency&gt;
&lt;dependency&gt;
   &lt;groupId&gt;org.bouncycastle&lt;/groupId&gt;
   &lt;artifactId&gt;bcpkix-jdk15on&lt;/artifactId&gt;
   &lt;version&gt;1.70&lt;/version&gt;
&lt;/dependency&gt;</pre>

The `Argon2PasswordEncoder` uses the Argon2id version by default with m=4MiB, t=3, and p=1. Although the number of iterations is higher than the minimum, you probably still want to consider higher memory consumption. In the Java example below, I use the minimum requirements of m=15Mib, t=2, and p=1. I use the same values as the defaults for the salt and hash length.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">Argon2PasswordEncoder encoder = new Argon2PasswordEncoder(32,64,1,15*1024,2);
var myPassword = "ThisIsMyPassword";

var encodedPassword = encoder.encode(myPassword);
System.out.println(encodedPassword);

var validPassword = encoder.matches(myPassword, encodedPassword);
System.out.println(validPassword);</pre>

The output from this code will be similar to this:

<pre class="EnlighterJSRAW" data-enlighter-language="raw" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$argon2id$v=19$m=15360,t=2,p=1$YpRuuQhW1dHOimAnWD5TRU6Sebitu+fIrmIrenr+YOM$hkEXhhHpu2NUcPwhV4IUQelQdf4I8V+iyFsFiC8BYEisE3oWFv96zYeNA1i/awhaDo1XHz6Pp/1r55SS/I4AIA
True</pre>

This hash contains all the information it needs, including the settings I supplied to the encoder and the salt information. This way you can easily check if the original password matches using `encoder.matches()`. This also works with another instance of the `Argon2PasswordEncoder` with different settings.

#### Using the Argon2 Binding for the JVM

The [Argon2 Binding](https://github.com/phxql/argon2-jvm) for the JVM library is an alternative to the Spring library. This library was created by Moritz Halbritter and uses JNA to call a native C library. The advantage of this approach is that this uses the original implementation of the Argon2 authors and possibly includes a performance advantage. As a result, you need to have access to the underlying C library on your system.

You can choose to install this C library yourself using your package manager or depend on the library that contains a set of pre-compiled Argon2 libraries. The first one is recommended for the obvious reason that it is smaller.

**Without precompiled libraries:**

<pre class="EnlighterJSRAW" data-enlighter-language="xml" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;dependency&gt;
    &lt;groupId&gt;de.mkammerer&lt;/groupId&gt;
    &lt;artifactId&gt;argon2-jvm-nolibs&lt;/artifactId&gt;
    &lt;version&gt;2.11&lt;/version&gt;
&lt;/dependency&gt;</pre>

**With precompiled libraries:**

<pre class="EnlighterJSRAW" data-enlighter-language="xml" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;dependency&gt;
    &lt;groupId&gt;de.mkammerer&lt;/groupId&gt;
    &lt;artifactId&gt;argon2-jvm&lt;/artifactId&gt;
    &lt;version&gt;2.11&lt;/version&gt;
&lt;/dependency&gt;</pre>

The usage of this library is quite straightforward. In the code example below, you can see an example equivalent to the Spring example using the same minimum parameters.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 32, 64);
var myPassword = "ThisIsMyPassword";

var hash = argon2.hash(2,15*1024,1, myPassword.toCharArray());
System.out.println(hash);

var validPassword = argon2.verify(hash, myPassword.toCharArray());
System.out.println(validPassword);</pre>

### SCRYPT {#h3-5-scrypt}

If you're unable to use Argon2id for any reason, [scrypt](https://en.wikipedia.org/wiki/Scrypt) is a good second choice. Similar to argon2id, scrypt is another strong password hashing algorithm that allows you to configure a variety of cost parameters to increase the resources it consumes.

According to the [OWASP Password cheat sheet](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html), you should use a minimum CPU/memory cost parameter (N), a minimum block size (r), and a parallelization parameter (p). The options below are considered the minimum you should use.

* N=2\^16 (64 MiB), r=8 (1024 bytes), p=1
* N=2\^15 (32 MiB), r=8 (1024 bytes), p=2
* N=2\^14 (16 MiB), r=8 (1024 bytes), p=4
* N=2\^13 (8 MiB), r=8 (1024 bytes), p=8
* N=2\^12 (4 MiB), r=8 (1024 bytes), p=15

Please note that the above list of scrypt parameters will all result in equally strong password hashes. The options above are simply showcasing that by specifying different values for the N, r, and p parameters, you can have the scrypt algorithm use more CPU or memory resources, depending on preference. My recommendation below showcases the real-world usage of this that you can simply copy and paste for simplicity.

The `spring-security-crypto` library supports many algorithms including SCrypt. You can use the `SCryptEncoder` in the same way as we did with the `Argon2Encoder`.In the example below I used the middle suggestion to provide a good balance between CPU and memory allocation.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">SCryptPasswordEncoder encoder = new SCryptPasswordEncoder(16384, 8, 4, 32, 64);
var myPassword = "ThisIsMyPassword";

var encodedPassword = encoder.encode(myPassword);
System.out.println(encodedPassword);

var validPassword = encoder.matches(myPassword, encodedPassword);
System.out.println(validPassword);</pre>

**Output:**

<pre class="EnlighterJSRAW" data-enlighter-language="raw" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">e0804$8FQ4x/ntwEz2ZNu8QRyIQJlAXR+gQkiG3WulLMEq/kioVtaFiKE7sZDGgtmqUmwB8OE+f7Eagux9QXG478unLw==$RS1Bz5Uf30dWGxc+vtlkjj7tPnPdgq8YD1V8odhPW4A=
true</pre>

### USING BCRYPT {#h3-6-using-bcrypt}

If Argon2id and scrypt are not available, another strong choice is [BCrypt](https://en.wikipedia.org/wiki/Bcrypt). If you decide to use BCrypt, the work factor parameter should be set to **no less than** **10**.

You can use the `spring-security-crypto` library in a similar way as before. The default workload is set to 10, but we set it to 14 in the following example (a reasonable number in 2022).

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(14);</pre>

The higher you set the work factor, the stronger the hash will be, but it will also take more CPU resources (and time!) to finish running. On my local machine, setting running BCrypt with a workload of 10 costs me about 89ms on average. For a workload of 14, it is 1033ms on average. That is almost 12x as long. This is by no means an accurate benchmark, but it does show the impact of an increased work factor.

Best practices for password hashing {#h2-7-best-practices-for-password-hashing}
-------------------------------------------------------------------------------

Whenever you need to implement password hashing in your application, there are a few best practices you should keep in mind.

1. Never implement password hashing algorithms yourself --- use a well-vetted developer library instead! Cryptography is a complex field, and there are lots of things that can go wrong if you attempt to implement a popular algorithm yourself.
2. As computers get stronger and faster every year, password hashing algorithms (and their parameters) need to be adjusted! Modern password hashing algorithms are reliant upon you (as the developer) to specify how much resources they should use when computing a hash, and as time progresses, you will need to update these parameters. The only way to stay on top of things like this is by keeping up with the latest security news and trends. The [Snyk blog](https://snyk.io/blog/), for example, is a great resource for security information and trends over time.

Make sure that the libraries and techniques you're using are secure by using free tooling like Snyk to proactively identify security issues in your code. Tools like [Snyk](https://snyk.io/) can identify security issues in your code early as well as suggest fixes and provide you with additional information. They are a critical part of maintaining a secure codebase over time as technology advances.

This article was originally posted on the [Snyk.io blog](https://snyk.io/blog/password-hashing-java-applications/) and reused with permission.
