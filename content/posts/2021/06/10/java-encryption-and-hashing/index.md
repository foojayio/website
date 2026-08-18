---
title: "Learn All About Java Encryption and Hashing"
slug: "java-encryption-and-hashing"
date: "2021-06-10T08:04:22+00:00"
lastmod: "2021-06-10T08:17:54+00:00"
description: "What is a strong encryption algorithm today, might be a weak algorithm a year from now. Therefore, encryption needs to be reviewed regularly!"
canonical: "https://snyk.io/blog/10-java-security-best-practices/"
authors:
  - "bmvermeer"
image: "Favicon-3-2.png"
categories:
  - "Security"
tags:
related_posts:
  - "ibm-semeru-java-fips140-3-cryptographic-standard"
  - "vaadin-oauth2-and-keycloak"
  - "how-to-do-password-hashing-in-java-applications-the-right-way"
  - "quick-fire-java-java-after-log4j"
frozen: false
---

If you need to store sensitive data in your system, you have to be sure that you have proper encryption in place. First of all, you need to decide what kind of encryption you need ---for instance, symmetric or asymmetric. Also, you need to choose how secure it needs to be. Stronger encryption takes more time and consumes more CPU. The most important part is that you don't need to implement the encryption algorithms yourself. Encryption is hard and a trusted library solves encryption for you.

If, for instance, we want to encrypt something like credit card details, we probably need a two-way encryption algorithm, because we need to be able to retrieve the original number. Say we use the Advanced Encryption Standard (AES), which is currently the standard symmetric encryption algorithm for US federal organizations. To encrypt and decrypt, there is no reason to deep-dive into low-level Java crypto. We recommend that you use a library that does the heavy lifting for you. For example, [Google Tink](https://github.com/google/tink).

```xml
<dependency>
   <groupId>com.google.crypto.tink</groupId>
   <artifactId>tink</artifactId>
   <version>1.6.0</version>
</dependency>
```

Below, there's a short example of how to use Authenticated Encryption with Associated Data (AEAD) with AES. This allows us to encrypt plaintext and provide associated data that should be authenticated but not encrypted.

```java
AeadConfig.register();
KeysetHandle keysetHandle = KeysetHandle.generateNew(KeyTemplates.get("AES256_GCM"));

String plaintext = "I want to break free!";
String aad = "Queen";

Aead aead = keysetHandle.getPrimitive(Aead.class);
byte[] ciphertext = aead.encrypt(plaintext.getBytes(), aad.getBytes());
String encr = Base64.getEncoder().encodeToString(ciphertext);
System.out.println(encr);

byte[] decrypted = aead.decrypt(Base64.getDecoder().decode(encr), aad.getBytes());
String decr = new String(decrypted);
System.out.println(decr);
```

For passwords, it is safer to use a strong cryptographic hashing algorithm as we don't need to retrieve the original passwords but just match the hashes. Argon2id, BCrypt, and SCrypt are the most suitable for this job according to the [OWASP password storage cheat sheet](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html). All are cryptographic hashes (one-way functions) and computationally difficult algorithms that consume a lot of time. This is exactly what you want, because brute force attacks take ages this way.

Spring security provides excellent support for a wide variety of algorithms. Try using for instance the `BCryptPasswordEncoder` that Spring Security tool 5 provides for the purpose of password hashing.

```xml
<dependency>
   <groupId>org.springframework.security</groupId>
   <artifactId>spring-security-crypto</artifactId>
   <version>5.5.0</version>
</dependency>
```

```java
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); //default strength is 10
String password = "ThisIsMyPassword";

//encode
String hash = encoder.encode(password);

//matching
Boolean match = encoder.matches(password, hash));
```

What is a strong encryption algorithm today, might be a weak algorithm a year from now. Therefore, encryption needs to be reviewed regularly to make sure you use the right algorithm for the job. Use vetted security libraries for these tasks and keep your libraries up to date. Furthermore, make sure to scan your open source libraries for security vulnerabilities often with a tool like [Snyk Open Source](https://snyk.io/product/open-source-security-management/) to prevent unpleasant surprises.

Check out the [10 Java Security Best Practices Cheat Sheet](https://snyk.io/blog/10-java-security-best-practices/)for more Java security tips.
