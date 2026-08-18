---
title: "Moving Security into the JVM"
slug: "moving-security-into-the-jvm"
date: "2023-02-17T11:15:35+00:00"
lastmod: "2023-04-27T13:04:54+00:00"
description: "With Azul Vulnerability Detection, running the software and getting security insight become the same action."
canonical: "https://www.azul.com/blog/moving-security-into-the-jvm/"
authors:
  - "erikcostlow"
image: "Screen-Shot-2022-10-25-at-8.33.15-PM.png"
categories:
  - "Release Notes"
  - "Security"
tags:
related_posts:
  - "are-java-security-updates-important"
  - "java-where-the-wild-code-isnt"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
frozen: false
---

The threat model for Java applications is changing, with modern risk coming from the widespread scope and usage of Java and library vulnerabilities.

There are so many different versions of Java (both major and minor versions) and so many systems and libraries that it's complex to know what everything is, where everything is, and if it's what's "out there" poses any security risk.

The new [Azul Vulnerability Detection](https://www.azul.com/products/vulnerability-detection) product by [Azul](https://www.azul.com/) is designed to help organizations deal with this evolving threat and manage the large scope of their Java environments.

For those who attack Java applications, their job has actually become easier. [Exploit payloads](https://www.exploit-db.com/?platform=java) for known vulnerabilities are publicly available and hackers use crawlers that act like search engines, moving around until they find a vulnerable system.

These modern attack payloads target the libraries themselves, not just the JVM. As [applets and client technologies have been deprecated](https://www.testingdocs.com/questions/java-applets-are-depricated/), the JVM's overall attack surface has gone down while the attack surface of libraries has gone up.

Examples of Java library and application vulnerabilities in the last year include [OGNL injection](https://securitynews.sonicwall.com/xmlpost/atlassian-confluence-and-data-center-ognl-injection-vulnerability/#:~:text=An%20OGNL%20injection%20vulnerability%20exists%20in%20Atlassian%20Confluence.,string%20literal%20and%20append%20an%20arbitrary%20OGNL%20expression.) against a major application, [Log4J JNDI injections](https://www.cisa.gov/uscert/apache-log4j-vulnerability-guidance) against many application, and [Psychic Signatures](https://neilmadden.blog/2022/04/19/psychic-signatures-in-java/) attacks against cryptographic trust.
![Graphic: Existing approaches are valuable but leave a critical security in the JVM gap in secure supply chain strategy.](https://www.azul.com/wp-content/uploads/Screen-Shot-2022-10-25-at-8.33.15-PM-1024x516.png) Existing approaches are valuable but leave a critical gap in secure supply chain strategy.

After more than 26 years of Java, there are so many active Java applications that organizations have difficulty tracking them. With the rise of attacks against libraries, teams are being asked to track and inventory the libraries that each application uses.

During the Log4Shell time of last year, many teams spent more than a month tracking down and fixing the flaw. Developers who are familiar see the solution as simple -- you upgrade a library. The problem is that it's complex to know where to look, who's responsible each time, and that you have an accurate inventory. There are some solutions that people are already using to try and address this problem:

* CI/CD scans often create an inventory when the application is built. This can be a basic tool like maven dependency:tree or a third-party tool.
* Agents can be integrated to observe some behavior in JVMs that still contain the [jlinked](https://medium.com/azulsystems/using-jlink-to-build-java-runtimes-for-non-modular-applications-9568c5e70ef4) instrumentation package.
* Some tools can inventory files that are present on a system.

Azul Vulnerability Detection does not replace but rather augments these approaches, offering a defense-in-depth analysis that gives stronger Java-focused insight at production speed.

The speed and ability to operate in production strengthens the CI/CD scans, offering the ability to track where the code went and provide coverage over Java infrastructure software that generally does not go through the CI/CD pipeline.

Most organizations that run tools like Kafka, Cassandra, or Spark just download and run those applications without any build steps. Unlike the agent approach, Azul Vulnerability Detection is integrated into the JVM and operates faster, without the need for external tools. This also avoids production drift, where agents generally run in simulated dev/qa environments.



### There are so many active Java applications that organizations have difficulty tracking them.



The difference with many inventory systems is the ability to go deep into Java applications. Many inventory systems will report based on files or signatures. With Java applications, applications use techniques like shading or flattening.

These change the filename or class package names, causing file scans to miss component detection and report incorrect information. By working deep within Java and being bytecode-aware, Azul Vulnerability Detection can still report information when these Java techniques are in use.

With Azul Vulnerability Detection, running the software and getting security insight become the same action.

**A Revolutionary Approach to Java Application Security**

Read the blog post by Azul CEO and Co-Founder Scott Sellers.

[Read the Blog](https://www.azul.com/blog/revolutionary-approach-to-java-application-security)

Evolution of Java Security
--------------------------

The security landscape has changed around Java's main designs. The original [outdated Java threat model](https://www.infoq.com/news/2021/04/java-security-vote/) dealt with portable code, using the SecurityManager to defend a host computer and sandbox code from an untrusted party.

With usage on backend systems and cloud-native development, the model has changed: the threat isn't remote code, it's the way custom code and libraries work together to safeguard their data.



### With Azul Vulnerability Detection, running the software and getting security insight become the same action.



Today many attacks succeed simply due to known vulnerabilities in existing systems. The presence of CVEs also impacts an organization's cyber insurance policy, with insurers like Chubb offering to share the risk on [neglected software vulnerabilities](https://www.chubb.com/content/dam/chubb-sites/chubb-com/us-en/business-insurance/cyber-enterprise-risk-management-cyber-erm/documents/pdf/2021-10.13_v3_17-01-0295_Widespread_Events_Endorsements.pdf#:~:text=For%20that%20reason%2C%20Chubb%20provides%20policyholders%20with%20a,U.S.%20National%20Institute%20for%20Standards%20and%20Technology%20%28NIST%29) if a CVE is not patched within 45 days.

The policy holder's clock starts when the vulnerability is discovered as a CVE, not when a system with that CVE is identified. As a result organizations benefit by maintaining a continuous inventory of components with known CVEs, and a modern Java security system must be able to provide this inventory.

The ultimate goal is to answer three questions:

1. **What do I have?** This addresses where Java is running and what code is part of that run.
2. **Is it vulnerable?** Based on the knowledge of today (not the time of a scan), does this application contain known vulnerabilities in either the JVM or the application's libraries.
3. **Do I actually use the vulnerable code?** Many Java applications contain unused libraries yet security scans report them at the same severity as code that loads -- they're important but focus on code that loads.

Enabling Azul Vulnerability Detection for security in the JVM
-------------------------------------------------------------

Using Azul Vulnerability Detection is simple -- it's part of the JDK so there's nothing additional to install. You can turn it on locally via command flags or environment variables, or at scale through DNS.

Flags offer direct control, DNS offers scale to engage JVMs that you cannot easily touch, such as vendor applications or existing containers.
![How Azul Vulnerability Detection works to provide security in the JVM.](https://www.azul.com/wp-content/uploads/avd-how-it-works-cropped-1024x706.png)

Each Azul JVM contains a low-overhead worker that communicates JVM-level information to a local connection manager (the Forwarder). The Forwarder helps encrypt all information and offloads cloud-connection overhead and firewall tuning from the JVMs, allowing them to operate at peak speed.

Insight from your JVMs is gathered in your dedicated cloud service for a small UI, steered toward a REST API -- teams do not need another dashboard so the focus is on integration.

This data flows in one direction to the Azul cloud service, and the performance impact is under 1% decreasing the longer the application runs. Performance is measured by the same [open community test suites](https://www.azul.com/blog/benchmarking-renaissance-on-openjdk-and-azul-platform-prime/) that measure JVM performance to unite speed and security.

Reach out to us to use Azul Vulnerability Detection in your environment and deal with the modern threat landscape against your Java applications.
