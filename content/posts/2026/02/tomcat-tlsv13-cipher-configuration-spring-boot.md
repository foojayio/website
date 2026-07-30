---
title: "Tomcat TLSv1.3 cipher configuration"
slug: "tomcat-tlsv13-cipher-configuration-spring-boot"
date: "2026-02-26T18:02:34+00:00"
lastmod: "2026-02-27T09:55:49+00:00"
description: "A Tomcat update splits TLSv1.3 cipher configuration into a new attribute, silently dropping your Spring Boot cipher restrictions. Here's how to fix it."
canonical: "https://www.herodevs.com/blog-posts/tomcats-tls-cipher-change-and-what-it-means-for-spring-boot-apps"
authors:
  - "joe-kuhel"
image: "/images/posts/2026/02/tomcat-tlsv13-cipher-configuration-spring-boot/tomcat.png"
categories:
  - "Apache Tomcat"
  - "Java"
  - "Security"
  - "Spring"
tags:
related_posts:
  - "tiberius-a-security-testing-framework-for-llm-applications-in-java"
  - "crossing-the-river-styx-spring-boot-3-5-and-the-zombie-dependency-problem"
  - "prevent-ldap-injection-in-java-with-springboot"
  - "ai-found-the-bugs-whos-patching-your-eol-java-code"
enlighterjs: true
frozen: false
---

A recent update to Apache Tomcat introduced a subtle but significant change to how TLS cipher suites are configured. If your Spring Boot application explicitly configures TLS ciphers, particularly TLSv1.3 ciphers, and runs on Tomcat 9.0.115+ (Spring Boot 2.x), 10.1.52+ (Spring Boot 3.x), or 11.0.18+ (Spring Boot 4.x), your cipher configuration may be silently ignored.

Prior to the change, Tomcat used a single `ciphers` attribute on the SSL connector to configure cipher suites for all TLS versions. Starting with the versions of Tomcat listed above (Tomcat [commit](https://github.com/apache/tomcat/commit/9abf6bddb2e84ecf1668780bb3150b799f832ccf)), this attribute was split:

* `ciphers`: Now only applies to TLSv1.2 and earlier
* `cipherSuites`: A new attribute specifically for TLSv1.3

The Tomcat team made this change to align with the distinct nature of TLSv1.3 cipher suites, which differ structurally from their TLSv1.2 counterparts. However, the migration path is problematic: TLSv1.3 ciphers placed in the `ciphers` attribute are removed from the configuration, logging only a warning.

What's the risk? {#h2-0-what-s-the-risk}
----------------------------------------

For example, consider an organization with a security policy requiring 256-bit encryption only. Let's say they configure a Spring Boot application as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="yaml">spring:
  ssl:
    bundle:
      jks:
        secure-server:
        # Server running with a self-signed keystore. Other server properties omitted for brevity
          options:
            ciphers:
              - TLS_AES_256_GCM_SHA384
              - TLS_CHACHA20_POLY1305_SHA256
            enabled-protocols:
              - TLSv1.3</pre>

Before the Tomcat change, the server only offered the two ciphers listed above and clients had to negotiate 256-bit encryption or the handshake failed.

After upgrading to an affected Tomcat version, both ciphers are removed from the *explicit* configuration set by the administrator. The only indication of this behavior is in the Tomcat log messages.

<pre class="EnlighterJSRAW" data-enlighter-language="bash">2026-02-22 09:05:06.426 WARN 58919 --- [ main] o.apache.tomcat.util.net.SSLHostConfig : The TLS 1.3 cipher suite [TLS_AES_256_GCM_SHA384] included in the TLS 1.2 and below ciphers list will be ignored<br>2026-02-22 09:05:06.426 WARN 58919 --- [ main] o.apache.tomcat.util.net.SSLHostConfig : The TLS 1.3 cipher suite [TLS_CHACHA20_POLY1305_SHA256] included in the TLS 1.2 and below ciphers list will be ignored</pre>

In this scenario, this does not mean those ciphers are no longer offered. It does mean that the intended cipher restriction is now gone. As a result, Tomcat reverts to offering all default TLSv1.3 ciphers, which includes the 128-bit cipher that was intentionally left out.

An nmap scan of the broken server reveals the problem:

<pre class="EnlighterJSRAW" data-enlighter-language="bash">$ nmap --script ssl-enum-ciphers -p 8443 localhost
Starting Nmap 7.98 ( https://nmap.org ) at 2026-02-22 09:07 -0500
Nmap scan report for localhost (127.0.0.1)
Host is up (0.00011s latency).
Other addresses for localhost (not scanned): ::1

PORT     STATE SERVICE
8443/tcp open  https-alt
| ssl-enum-ciphers:
|   TLSv1.3:
|     ciphers:
|       TLS_AKE_WITH_AES_128_GCM_SHA256 (secp256r1) - A
|       TLS_AKE_WITH_AES_256_GCM_SHA384 (secp256r1) - A
|       TLS_AKE_WITH_CHACHA20_POLY1305_SHA256 (secp256r1) - A
|     cipher preference: client
|_  least strength: A

Nmap done: 1 IP address (1 host up) scanned in 0.08 seconds
</pre>

Despite the explicit exclusion of the `TLS_AES_128_GCM_SHA256` (128-bit) cipher to align with our fictitious organization's security policy, all three default TLSv1.3 ciphers appear as active.

While all standard TLSv1.3 ciphers are cryptographically strong and the default ciphers are not inherently weak, the central issue is the server's silent deviation from the administrator's security policy. Because of this, the mismatch between the intended configuration and the server's actual behavior is a significant concern. In regulated environments (FIPS 140-2, PCI-DSS, internal compliance mandates), this gap between intended and actual configuration is exactly what auditors and penetration testers look for.

Who is Affected {#h2-1-who-is-affected}
---------------------------------------

Any Spring Boot application that:

* Uses embedded Tomcat (the default for web MVC apps)
* Explicitly configures TLSv1.3 cipher suites, either via `server.ssl.ciphers` or via `options.ciphers` in an [SSL Bundle](https://docs.spring.io/spring-boot/reference/features/ssl.html#features.ssl.applying) [applied](https://docs.spring.io/spring-boot/how-to/webserver.html#howto.webserver.configure-ssl.bundles) to the connection with `server.ssl.bundle`
* Runs on Tomcat 9.0.115+, 10.1.52+, or 11.0.18+

The Fix {#h2-2-the-fix}
-----------------------

Spring Boot's OSS releases `v3.5.11` and `v4.0.3` introduced a patch that correctly configures ciphers. This configuration allows for separate cipher settings for TLSv1.2 and older versions, distinct from those used for TLSv1.3.

### How to Verify {#h3-3-how-to-verify}

After upgrading, you can verify the fix is working by checking which ciphers your server offers.

<pre class="EnlighterJSRAW" data-enlighter-language="bash"># Check TLSv1.3 negotiation
openssl s_client -connect localhost:8443 -tls1_3 &lt;/dev/null 2&gt;&amp;1 | grep "Cipher is"

# Get a full list of Ciphers offered by the server
nmap --script ssl-enum-ciphers -p 8443 localhost</pre>

Once the fix is applied, only the explicitly configured ciphers should be offered by the server.

### Recommendation {#h3-4-recommendation}

If your application configures TLSv1.3 cipher suites whether via `server.ssl.ciphers` or via `options.ciphers` in an SSL Bundle, upgrade to a Spring Boot version that includes the fix.

These versions are still under OSS support.

| Spring Boot |                                Patched Version                                | Tomcat Version |    Spring Boot OSS Support    |
|-------------|-------------------------------------------------------------------------------|----------------|-------------------------------|
| 3.5.x       | [3.5.11](https://github.com/spring-projects/spring-boot/releases/tag/v3.5.11) | 10.1.52        | Supported until June 2026     |
| 4.0.x       | [4.0.3](https://github.com/spring-projects/spring-boot/releases/tag/v4.0.3)   | 11.0.18        | Supported until December 2026 |

For applications on Spring Boot versions that have reached the end of OSS support, the fix is not available. HeroDevs NES for Spring also provides a fix for these versions:

| Spring Boot |                                     Patched Version                                      | Tomcat Version | Spring Boot EOL |
|-------------|------------------------------------------------------------------------------------------|----------------|-----------------|
| 2.7.x       | [NES Spring Boot 2.7.35](https://docs.herodevs.com/spring/release-notes/spring-boot-2-7) | 9.0.115        | November 2023   |
| 3.2.x       | [NES Spring Boot 3.2.23](https://docs.herodevs.com/spring/release-notes/spring-boot-3-2) | 10.1.52        | December 2024   |
| 3.3.x       | [NES Spring Boot 3.3.17](https://docs.herodevs.com/spring/release-notes/spring-boot-3-3) | 10.1.52        | June 2025       |
| 3.4.x       | [NES Spring Boot 3.4.15](https://docs.herodevs.com/spring/release-notes/spring-boot-3-4) | 10.1.52        | December 2025   |

*Note: Spring Boot 3.4.13 (the final OSS release) ships with Tomcat 10.1.50, which predates the breaking change. The issue will surface when 3.4.x users independently upgrade Tomcat to 10.1.52+ without also having the Boot-side cipher fix.*

[See here to learn more about the HeroDevs NES for Spring Boot versions.](http://www.herodevs.com/blog-posts/tomcats-tls-cipher-change-and-what-it-means-for-spring-boot-apps)
