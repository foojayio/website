---
title: "Securing Symmetric Encryption Algorithms in Java"
slug: "securing-symmetric-encryption-algorithms-in-java"
date: "2023-12-29T05:53:50+00:00"
lastmod: "2023-12-29T05:56:15+00:00"
description: "Encryption is converting readable data or plaintext into unreadable data or ciphertext, ensuring that even if encrypted data is intercepted, it remains inaccessible to unauthorized individuals."
canonical: "https://snyk.io/blog/symmetric-encryption-algorithms-java/"
authors:
  - "bmvermeer"
image: "snyk-logo-2.png"
categories:
  - "Security"
  - "Snyk"
tags:
related_posts:
  - "5-tips-to-create-secure-docker-images-for-java-applications"
  - "8-best-practices-to-prevent-sql-injection-attacks"
  - "avoid-java-serialization"
enlighterjs: true
frozen: false
---

**In our connected world, securing digital data has become an utmost priority. With the wide spread of Java applications in various sectors, from banking to healthcare, we must emphasize the importance of encryption. Encryption is converting readable data or plaintext into unreadable data or ciphertext, ensuring that even if encrypted data is intercepted, it remains inaccessible to unauthorized individuals.**

The first choice you must make as a developer is whether you need encryption. Although this sounds like a strange question, the key principle of encryption is that the ciphertext can be reverted into the original text.

When looking at passwords, for instance, we do not want encryption as we do not want to be able to "decrypt" the original text. Therefore, we rely on [++hashing for passwords++](https://snyk.io/blog/password-hashing-java-applications/) and not encryption.

Encryption in Java {#h2-0-encryption-in-java}
---------------------------------------------

Java applications often handle sensitive data, including customer information, financial details, and transaction records.

As a Java developer, employing robust encryption algorithms is vital to maintaining the integrity and confidentiality of sensitive data. This protects your users and any essential data you have inside your system.

### Symmetric vs asymmetric encryption {#h3-1-symmetric-vs-asymmetric-encryption}

Generally speaking, we can distinguish two types of encryption: symmetric and asymmetric. Either type works fundamentally differently, and either type has different use cases.

Symmetric encryption uses the same key for both encryption and decryption, making it faster but less secure when the key needs to be shared. On the other hand, asymmetric encryption uses a pair of keys (public and private).

The public key is used for encryption, and the private key is for decryption. While symmetric encryption is suited for scenarios where large volumes of data need to be encrypted, asymmetric encryption is ideal for securely sharing keys over a network. Although we distinguish between two types of encryption, in this article, we will **only** focus on symmetric encryption in Java applications.

When using symmetric encryption, it's best to rely on services that leverage hardware security modules (HSMs) for key management and envelope encryption patterns in order to simplify key management, which is the hardest part of the encryption solution.

However, if you're unable to use a service like Amazon Key Management Service (KMS) and still want to do this, the information below will show you how to do so securely. In the remainder of this article, we focus on implementing symmetric encryption in your Java application using the `javax.crypto` packages.

### Understanding outdated encryption algorithms and their risks {#h3-2-understanding-outdated-encryption-algorithms-and-their-risks}

Despite the importance of encryption, not all encryption algorithms are created equal. Some older or "outdated" encryption algorithms, such as DES (Data Encryption Standard) or 3DES, [++have been found to have vulnerabilities that attackers can exploit++](https://security.snyk.io/vuln/SNYK-AMZN201803-JAVA180OPENJDK-1704650). These algorithms were once considered secure, but advances in computing power and cryptanalysis techniques have rendered them unsafe.

Using insecure encryption algorithms in your Java applications can lead to potential risks. An attacker who gains access to your encrypted data may be able to decrypt it if the encryption algorithm is outdated and has known vulnerabilities. This could result in a data breach, with potentially devastating consequences for your users and your organization.

### Recommendations for algorithms and cipher modes {#h3-3-recommendations-for-algorithms-and-cipher-modes}

If we look at symmetric encryption algorithms, the [++OWASP foundation++](https://cheatsheetseries.owasp.org/cheatsheets/Cryptographic_Storage_Cheat_Sheet.html) currently advises AES (Advanced Encryption Standard) with a key that is at least 128 bits but preferably 256 bits. In addition, you should use the algorithm with a secure mode. Also, the [++NIST cryptographic standards and guidelines++](https://csrc.nist.gov/projects/block-cipher-techniques) approve AES encryption as a block cipher technique.

Multiple modes are available with a cipher like AES. These modes have different security and performance characteristics. When it comes to modes, avoid ECB (Electronic Codebook) mode, which [++does not provide serious message confidentiality++](https://cybergibbons.com/reverse-engineering-2/why-is-unauthenticated-encryption-insecure/). Instead, use modes like CCM (Counter with CBC-MAC) or GCM (Galois/Counter Mode). Both of these modes are [++authenticated++](https://www.ubiqsecurity.com/understanding-authenticated-encryption-an-explainer/) modes and guarantee the data's integrity, confidentiality, and authenticity.

Identifying weak encryption algorithms in Java {#h2-4-identifying-weak-encryption-algorithms-in-java}
-----------------------------------------------------------------------------------------------------

It is relatively easy to use the `javax.crypto` APIs for implementing a small encryption service in Java. Below is an example of `EncryptionService` in Java based on the outdated DES algorithm using the ECB mode.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class EncryptionServiceDes {

    private SecretKey secretKey;
    private Cipher cipher;

    public EncryptionServiceDes(String key) throws GeneralSecurityException {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        DESKeySpec desKeySpec = new DESKeySpec(keyBytes);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        secretKey = keyFactory.generateSecret(desKeySpec);
        cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
    }

    public String encrypt(String original) throws GeneralSecurityException {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        // Encrypt the original data
        byte[] encryptedData = cipher.doFinal(original.getBytes(StandardCharsets.UTF_8));
        // Encode the encrypted data in base64 for better handling
        return Base64.getEncoder().encodeToString(encryptedData);
    }
    public String decrypt(String cypher) throws GeneralSecurityException{
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        // Decode the base64-encoded ciphertext
        byte[] encryptedData = Base64.getDecoder().decode(cypher);
        // Decrypt the data
        byte[] decryptedData = cipher.doFinal(encryptedData);
        return new String(decryptedData, StandardCharsets.UTF_8);
    }
}
</pre>

<br />

Now that we've built a known insecure encryption algorithm into a project, we'll use the free Snyk service to detect this insecure use and guide us to a better solution.

First, [++sign up for a free Snyk account++](https://app.snyk.io/login/). You can easily get started using your GitHub, Bitbucket, Azure AD, or Docker account.

I then connected my GitHub repository to Snyk. Snyk Code's static analysis immediately warns me that my application uses a Broken or Risky Cryptographic Algorithm. This is spot on since I have multiple references to DES in the service above. The Snyk Code engine also advises me to consider using AES, which aligns with the advice from OWASP.
![blog-secure-encryption-priotity-score](https://snyk.io/_next/image/?url=https%3A%2F%2Fres.cloudinary.com%2Fsnyk%2Fimage%2Fupload%2Fv1697638240%2Fblog-secure-encryption-priotity-score.jpg&w=2560&q=75)

<br />

Despite using AES in the short snippet below, I am using the CBC mode which is not considered secure.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">...
    public EncryptionServiceAES(String key) throws GeneralSecurityException {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        secretKey = new SecretKeySpec(keyBytes, "AES");
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    }
...</pre>

Snyk will also identify the use of insecure modes, among other things. The screenshot below is taken from the [++Snyk plugin++](https://plugins.jetbrains.com/plugin/10972-snyk-security--code-open-source-container-iac-configurations) that scans my code inside of InteliJ IDEA and provides useful information, like external fix examples.
![blog-secure-encryption-vuln-broken](https://snyk.io/_next/image/?url=https%3A%2F%2Fres.cloudinary.com%2Fsnyk%2Fimage%2Fupload%2Fv1697638242%2Fblog-secure-encryption-vuln-broken.jpg&w=2560&q=75)

Changing the `EncryptionService` to a version that uses `AES/GCM/NoPadding`, like below, is a far better solution than I had before and is in line with the current advice that OWASP provides.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class EncryptionServiceAESGCM {
    private SecretKey secretKey;
    private Cipher cipher;

    public EncryptionServiceAESGCM(String key) throws GeneralSecurityException {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        secretKey = new SecretKeySpec(keyBytes, "AES");
        cipher = Cipher.getInstance("AES/GCM/NoPadding");
    }

    public String encrypt(String original) throws GeneralSecurityException{
        byte[] iv = new byte[16]; // Initialization Vector
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv); // Generate a random IV
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));
        byte[] encryptedData = cipher.doFinal(original.getBytes());
        var encoder = Base64.getEncoder();
        var encrypt64 = encoder.encode(encryptedData);
        var iv64 = encoder.encode(iv);
        return new String(encrypt64) + "#" + new String(iv64);
    }

    public String decrypt(String cypher) throws GeneralSecurityException{
        var split = cypher.split("#");
        var decoder = Base64.getDecoder();
        var cypherText = decoder.decode(split[0]);
        var iv = decoder.decode(split[1]);
        var paraSpec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, paraSpec);
        byte[] decryptedData = cipher.doFinal(cypherText);
        return new String(decryptedData);
    }
}</pre>

<br />

<br />

Continuously reviewing encryption algorithms in your Java applications {#h2-5-continuously-reviewing-encryption-algorithms-in-your-java-applications}
-----------------------------------------------------------------------------------------------------------------------------------------------------

While updating outdated encryption algorithms is crucial, it's equally important to monitor your codebase for new security issues continuously. The encryption algorithms considered safe and secure at the moment of writing will most definitely be out of date in a matter of years.

Proactively scanning and identifying this should, therefore, be a continuous exercise. This does not only count for encryption algorithms but obviously also for other issues in the code you maintain. Adopting and using a static analysis security testing (SAST) tool like Snyk code will help you easily recognize, identify, and mitigate these issues before actual harm is done.

Snyk Code for Java can help you for free {#h2-6-snyk-code-for-java-can-help-you-for-free}
-----------------------------------------------------------------------------------------

Snyk Code is a state-of-the-art static application security testing (SAST) tool that can identify security vulnerabilities within your codebase. It supports several programming languages, including Java, by analyzing code and providing real-time feedback to developers. Snyk Code can be integrated into your CI/CD pipeline to automatically scan your code with each commit, helping you catch and fix security issues early in the development cycle. Additionally, you can connect it to your git repository, use it on the command line using our CLI, and integrate it with all the popular IDEs for Java.

This can catch problems in your code early and often in every step of the development lifecycle. Most importantly, you can use it for free by signing up for Snyk below.

[Register now](https://go.snyk.io/202311-live-hack-exploiting-ai-generated-code.html)

The code examples used in this blog post are published on [++GitHub++](https://github.com/bmvermeer/java-encryption), so you can experiment with it yourself. If you want to have more good resources around security for Java developers. Take a look at:

* [++Using JLink to create smaller Docker images for your Spring Boot Java application++](https://snyk.io/blog/jlink-create-docker-images-spring-boot-java/)
* [++Preventing Cross-Site Scripting (XSS) in Java applications with Snyk Code++](https://snyk.io/blog/preventing-xss-snyk-code/)
* [++Mitigating path traversal vulns in Java with Snyk Code++](https://snyk.io/blog/mitigating-path-traversal-java-snyk-code/)
* [++How to use Java DTOs to stay secure++](https://snyk.io/blog/how-to-use-java-dtos/)
* [++Best practices for managing Java dependencies++](https://snyk.io/blog/best-practices-for-managing-java-dependencies/)
* [++10 best practices to build a Java container with Docker++](https://snyk.io/blog/best-practices-to-build-java-containers-with-docker/)
