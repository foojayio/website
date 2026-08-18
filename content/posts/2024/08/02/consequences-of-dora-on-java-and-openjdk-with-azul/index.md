---
title: "Consequences of DORA on Java and OpenJDK with Azul"
date: "2024-08-02T13:49:44+00:00"
lastmod: "2024-10-18T06:42:44+00:00"
description: "Azul ensures that financial institutions using Java remain compliant with DORA by providing a secure, supported, and stable Java platform, mitigating the risks associated with unsupported OpenJDK distributions."
authors:
  - "geertjan-wielenga"
  - "simonritter"
image: "dora.png"
categories:
  - "EU DORA Act"
  - "Java Core"
  - "Performance"
  - "Security"
tags:
related_posts:
  - "the-impact-of-the-digital-operational-resilience-act-dora-on-java-investment-with-azul"
  - "are-java-security-updates-important"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
frozen: false
---

**The EU's [Digital Operational Resilience Act (DORA)](https://foojay.io/today/the-impact-of-the-digital-operational-resilience-act-dora-on-java-investment-with-azul/) is a regulatory framework aimed at enhancing the digital operational resilience of financial institutions within the European Union.**

Its primary goal is to ensure that financial entities can withstand, respond to, and recover from all types of ICT-related disruptions and threats, such as cyberattacks.

DORA establishes a uniform set of requirements for managing ICT risks across the financial sector, promoting a harmonized approach to digital resilience.

### Key Points of DORA

1. **ICT Risk Management**: Financial institutions must implement comprehensive risk management frameworks to identify, assess, and mitigate ICT-related risks.
2. **Incident Reporting**: Entities must report major ICT-related incidents to the competent authorities within tight deadlines.
3. **Testing and Oversight**: Regular testing of ICT systems, including penetration testing, is required to ensure operational resilience. Additionally, critical third-party ICT service providers will be subject to oversight.
4. **Third-Party Risk Management**: Institutions must carefully manage and monitor risks associated with third-party ICT service providers, including cloud services.
5. **Information Sharing**: DORA encourages financial entities to share information on cyber threats and vulnerabilities to improve collective resilience.

### Five Important Tasks for Compliance

If you are a CISO or are in an IT Compliance-related role in a financial institution in the EU, what exactly do you need to do to ensure your organization is in compliance with DORA, in particular in relation to your Java investment?

Put the five critical tasks below on the list of things that you need to start doing right now, since DORA will apply as of **17 January 2025**, i.e., 5 months from today.

### 1. **Develop and Implement a Comprehensive ICT Risk Management Framework**

* **Chapter** : **DORA, Chapter II: ICT Risk Management**
* **Specific Articles** :**6(1-3), 8(1)**
* **Explanation** : Chapter II mandates a strong ICT risk management framework. As one critical part of that, be aware that using unsupported OpenJDK distributions can expose financial institutions to significant risks, such as unpatched security vulnerabilities and performance issues. [Azul](https://azul.com/) provides a fully supported and secure Java platform, ensuring that Java applications remain resilient and compliant with ICT risk management requirements.

### 2. **Establish an Incident Reporting Mechanism**

* **Chapter** : **DORA, Chapter III: ICT-related Incident Reporting**
* **Specific Articles** :**17(1), 18(1)**
* **Explanation** : Chapter III focuses on timely incident reporting. Unsupported OpenJDK distributions might not receive critical updates or fixes, leading to unreported and unnoticed incidents, which can result in non-compliance. [Azul's](https://azul.com/) Java runtimes come with comprehensive support and monitoring, finetuned for vulnerability and dead code detection of Java code live in production, helping organizations quickly and accurately detect, report, and resolve incidents, ensuring compliance with DORA.

### 3. **Conduct Regular and Rigorous Testing of ICT Systems**

* **Chapter** : **DORA,** **Chapter IV: Digital Operational Resilience Testing**
* **Specific Articles** :**24(1), 24(2), 25(1)**
* **Explanation** : Chapter IV requires regular testing of ICT systems. Using unsupported OpenJDK distributions can undermine these tests, as outdated or vulnerable versions may not accurately reflect production environments, leading to false security assumptions. [Azul](https://azul.com/) provides up-to-date, tested Java distributions, enabling reliable and accurate testing environments for financial institutions.

### 4. **Enhance Third-Party Risk Management Practices**

* **Chapter** : **DORA,** **Chapter V: Management of ICT Third-Party Risk**
* **Specific Article** :**28(2)**
* **Explanation** : Chapter V addresses third-party ICT risks. Relying on unsupported OpenJDK distributions from third parties increases the risk of security breaches and operational failures. [Azul's](https://azul.com/) fully supported Java environments ensure that third-party Java-based applications and services meet the highest security and performance standards, reducing third-party risks.

### 5. **Facilitate Information Sharing on Cyber Threats**

* **Chapter** : **DORA,** **Chapter VI: Information Sharing Arrangements**
* **Specific Article** :**45(1)**
* **Explanation** : Chapter VI encourages sharing information on cyber threats. Unsupported OpenJDK distributions may miss critical updates and patches, making them a weak link in the information-sharing chain. By using [Azul's](https://azul.com/) supported Java distributions, organizations can ensure they are aware of the latest vulnerabilities and can share relevant threat information with other entities to enhance collective cybersecurity.

### Consequences of Using Unsupported OpenJDK Distributions

* **Security Risks**: Unsupported distributions do not receive timely security updates, leaving systems vulnerable to cyberattacks and breaches.
* **Compliance Issues**: Lack of support can lead to non-compliance with regulatory requirements like DORA, potentially resulting in fines and reputational damage.
* **Operational Instability**: Unsupported distributions might not receive performance improvements or critical bug fixes, leading to system outages and degraded performance.
* **Inaccurate Testing**: Outdated Java environments can cause testing environments to be less accurate, leading to vulnerabilities being missed in resilience tests.

By addressing these tasks, financial organizations invested in Java can safely navigate DORA's requirements while strengthening their digital operational resilience. Azul's technologies and expertise are second to none in ensuring that financial institutions using Java can become and remain compliant with DORA by providing a secure, supported, and stable Java platform, mitigating the risks associated with unsupported OpenJDK distributions.
