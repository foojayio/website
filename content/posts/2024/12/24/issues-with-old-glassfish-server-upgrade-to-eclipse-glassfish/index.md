---
title: "Issues with old GlassFish server? Upgrade to Eclipse GlassFish!"
slug: "issues-with-old-glassfish-server-upgrade-to-eclipse-glassfish"
date: "2024-12-24T09:05:26+00:00"
lastmod: "2024-12-24T09:05:28+00:00"
description: "If you haven’t explored Eclipse GlassFish since it joined the Eclipse Foundation, now’s the time to see how its new capabilities can modernize and future-proof your Jakarta EE applications."
canonical: "https://omnifish.ee/2024/12/11/issues-with-old-glassfish/"
authors:
  - "ondro-mihalyi"
image: "GlassFish-embedded-e1698251860666.png"
categories:
  - "Jakarta EE"
  - "Java"
tags:
related_posts:
  - "glassfish-is-rolling-forward-whats-new"
  - "ejb-support-in-piranha-via-cdi"
  - "glassfish-embedded-a-simple-way-to-run-jakarta-ee-apps"
  - "reflections-on-2024-a-remarkable-year-for-omnifish-glassfish-piranha-and-jakarta-ee"
frozen: false
---

> **Rely on hardened and production-ready Eclipse GlassFish 7 or newer. Benefit from key feature updates and Jakarta EE advancements, brought to you by OmniFish and the amazing community of opensource GlassFish contributors.**

The advancements made in Eclipse GlassFish under the Eclipse Foundation have transformed it into a more developer-friendly, cloud-native, and secure application server compared to its legacy predecessors from before 2020.

Since version 7, Eclipse GlassFish provides a lot of benefits not available with old GlassFish versions, for example:
> * Support for the **latest Java version** as well as for older versions like **Java** **11**
> * The **latest Jakarta** **EE** version, the new generation of Java EE
> * MicroProfile APIs suitable for **building microservices** and cloud-native applications
> * Support for **modern security and** **HTTP protocols**
> * GlassFish Embedded version optimized for **running microservices**
> * Official **Docker images** tuned for cloud deployments and Kubernetes
> * Updated **developers tools** (IDE plugins, Maven plugins, etc.)
> * **Regular monthly releases** with fixes, improvements and new features
> * **Commercial support** provided by OmniFish -- experts behind GlassFish

[Need help with GlassFish? Ask us!](https://omnifish.ee/contact-us)

Here's an in-depth comparison highlighting the significant new features and improvements that address the limitations found in older GlassFish versions.

### **Jakarta EE Standardization and Flexibility** {#jakarta-ee-standardization-and-flexibility}

In legacy versions, GlassFish supported Java EE, but it was less flexible when implementing new features or APIs. Transitioning to the Eclipse Foundation gave GlassFish a fresh alignment with Jakarta EE, enabling it to incorporate major updates such as **Jakarta EE 10** with a more modular structure and lightweight API usage.

* Jakarta EE 10 showcases flexibility with new features built on the **CDI** dependency injection container. Previously, Java EE relied heavily on EJB components, which combined unrelated features, required boilerplate code, and created heavy components---making it harder to build lightweight applications like microservices. By rebuilding this functionality on CDI, Jakarta EE 10 simplifies things with an annotation-driven model, enabling lightweight components with only the necessary features. This reduces memory usage and speeds up deployments.
* **Better Java SE Compatibility** : Integrating newer Java SE language features into enterprise apps is now simpler with Jakarta EE 10. While legacy versions required workarounds to use Java's latest enhancements, developers can now use Java records, `var` declarations, virtual threads and other language-level improvements directly, bringing GlassFish in line with modern Java development practices.

### **Compatibility with new Java versions** {#compatibility-with-new-java-versions}

Legacy GlassFish runs only on Java 8 or older, while Eclipse GlassFish supports newer versions like Java 11, 17, 21, and 23. This enables applications to run on older Java versions for compatibility or upgrade to benefit from new features, JVM improvements, and security updates.

### **Enhanced Security with Modern Protocols** {#enhanced-security-with-modern-protocols}

Older GlassFish versions used a more monolithic security model with limited integration options, making it cumbersome to configure advanced authentication schemes without custom code. Security was largely based on GlassFish-specific realms and Java EE's standard security APIs, which didn't support OAuth2.0, OpenID Connect, or other modern protocols directly.

* **Jakarta Security 3.0** in Eclipse GlassFish now offers built-in support for token-based authentication systems, including OAuth2.0 and OpenID Connect. This feature is especially important for integrating with identity providers like Okta, Auth0, or AWS Cognito. GlassFish now includes APIs for token validation, multi-tenancy support, and customized login flows, making it easier to adapt to enterprise-grade security needs.
* The **MicroProfile JWT** API simplifies securing RESTful microservices using JWT tokens, a standard format for tokens issued by OAuth 2.0 and OpenID Connect providers. It enables easy sharing of security context between services or from the UI to REST endpoints.
* **Smoother Multi-Tenant Configurations**: Modern applications often serve multiple clients on the same infrastructure. Eclipse GlassFish supports more seamless multi-tenancy by allowing separate authentication configurations per tenant, a feature that older versions did not support effectively without manual setups.
* Regular releases with **Security fixes**: Eclipse GlassFish includes critical security fixes that address vulnerabilities found in older versions. This reduces the risk of security breaches, making it safer for production environments. Regular security updates and regular monthly releases help ensure compliance with industry standards and protect sensitive data.

### **Support for HTTP/2, REST and JSON Enhancements** {#support-for-http-2-rest-and-json-enhancements}

While legacy GlassFish versions provided REST support with JAX-RS, it was limited in scope, focusing on basic RESTful communication without modern features such as HTTP/2 or server-sent events (SSE). JSON handling, likewise, was somewhat rigid, making complex object serialization a manual task.

* **Support for HTTP/2**: Eclipse GlassFish supports HTTP/2, which offers better performance for APIs handling multiple simultaneous HTTP requests by enabling multiplexing, header compression, and reduced latency.
* **Enhanced RESTful Services with JAX-RS 3.1**: The updated JAX-RS implementation now supports multi-part media type (HTML forms), reactive and asynchronous processing, and native integration with JSON Binding. SSE support allows applications to push real-time updates to clients without requiring constant polling, improving user experience in applications like live dashboards.
* **Developer-friendly REST Client API**: The MicroProfile REST Client API in GlassFish makes it easy for developers to call RESTful services, reducing boilerplate code and simplifying setup with support for external configuration.
* **Improved JSON Binding**: Jakarta JSON-B and JSON-P updates in Eclipse GlassFish reduce JSON serialization boilerplate. JSON-B now supports features like bidirectional mapping for complex objects, which legacy GlassFish versions handled less gracefully. This improvement is especially useful in microservices that rely heavily on JSON for data exchange, saving development time and improving maintainability.

### **Cloud-Native Readiness and Containerization** {#cloud-native-readiness-and-containerization}

Older GlassFish versions were designed primarily for traditional data center deployments and required extensive configuration to run efficiently in cloud environments. Running GlassFish on containers such as Docker was possible, but it demanded workarounds to optimize memory, CPU usage, and health monitoring.

* **Enhanced Container and Kubernetes Support**: Eclipse GlassFish has moved towards cloud-native application architecture, streamlining containerization and orchestration with Kubernetes. Pre-configured Docker images, optimized for fast startup and low memory usage, simplify GlassFish deployments. This helps with automating tasks such as scaling, load balancing, and health-checking.
* **Microservices-Friendly Configuration** : GlassFish now supports lightweight deployments with optimized class loaders, reducing startup time and memory usage. The GlassFish Embedded distribution lets you build and run lightweight applications with a single command, without needing to install a server. Additionally, the **MicroProfile Config** API provides a modern, standardized way to manage external configurations, supporting common and custom configuration sources.

### Conclusion {#conclusion}

Eclipse GlassFish has clearly transformed from its legacy versions, evolving with features tailored to today's demands for flexibility, security, observability, and cloud-native capabilities.

The new features and improvements empower developers to build robust, scalable applications with the assurance of Jakarta EE's latest standards.

For developers accustomed to older GlassFish versions, the transition offers a more powerful and agile platform designed for both traditional and cloud-native applications alike.

In short, if you haven't explored Eclipse GlassFish since it joined the Eclipse Foundation, now's the time to see how its new capabilities can modernize and future-proof your Jakarta EE applications.

[more about new features in Eclipse GlassFish](https://omnifish.ee/2024/09/12/glassfish-is-rolling-forward-whats-new/?preview_id=3603&preview_nonce=9953c79df1&preview=true)

[GlassFish support services by OmniFish](https://omnifish.ee/glassfish-support)
