---
title: "Unsupported OpenJDK Distributions are at Risk of Non-Compliance with DORA"
slug: "unsupported-openjdk-distributions-are-at-risk-of-non-compliance-with-dora"
date: "2024-08-26T12:24:05+00:00"
lastmod: "2024-09-05T06:31:09+00:00"
description: "Read the only logical conclusion of reading DORA in the context of OpenJDK, assuming that it has meaning and intent in the context of OpenJDK or any ICT asset that is vulnerable to the risk that DORA seeks to mitigate."
authors:
  - "geertjan-wielenga"
image: "https://foojay.io/wp-content/uploads/2024/07/dora.png"
categories:
  - "EU DORA Act"
  - "Java Core"
  - "Performance"
  - "Security"
tags:
related_posts:
  - "connecting-resilience-to-performance-in-relation-to-openjdk"
  - "the-impact-of-the-eu-dora-act-on-non-eu-financial-organizations"
  - "consequences-of-dora-on-java-and-openjdk-with-azul"
frozen: false
---

**For the EU Digital Operations Resilience Act (DORA) to have any meaning at all in the context of OpenJDK (and surely it must have application there since it is explicitly focused on "ICT Assets", which it defines as broadly as possible as "a software or hardware asset in the network and information systems used by a financial entity"), it can only be interpreted to, at the very least, very strongly encourage the usage of supported OpenJDK distributions.**

For OpenJDK to be "supported" means that the distribution of OpenJDK in use receives regular updates, security patches, and technical support from a provider or organization.

**Overview**

Regulation (EU) 2022/2554 of the European Parliament and of the Council of 14 December 2022 on digital operational resilience for the financial sector (also known as "DORA"), has the following provisions which point very strongly towards usage of supported OpenJDK distributions by financial insitutions so as to mitigate risk and promote resilience.

The whole act [can be found here](https://eur-lex.europa.eu/eli/reg/2022/2554/oj), with quotations below taken directly from it. In the below, the bold and underlinings are by me, to emphasize the key phrases, referred to in the "Connection to Unsupported OpenJDK" sections below each section.

**Note:** In no way is this any kind of official legal opinion, instead, it is a reading by me of the DORA act in the context of OpenJDK and in line with the intention of the act to mitigate risk and promote resilience (those two words come up numerous times in the Act, which has as its name, of course, "Digital Operational Resilience Act"), in the context of financial insitutions in the EU.

My only assumption is that the DORA act is not intended to be meaningless. If it has meaning, which is a reasonable assumption, then it has meaning in the context of OpenJDK, with some of the many of DORA's articles below being difficult to interpret in any other way.

For example, for some context, this is paragraph 1 of the preamble, which clearly sets DORA's scope:

"**In the digital age, information and communication technology (ICT) supports complex systems used for everyday activities. It keeps our economies running in key sectors, including the financial sector, and enhances the functioning of the internal market. Increased digitalisation and interconnectedness also amplify ICT risk, making society as a whole, and the financial system in particular, more vulnerable to cyber threats or ICT disruptions. While the ubiquitous use of ICT systems and high digitalisation and connectivity are today core features of the activities of Union financial entities, their digital resilience has yet to be better addressed and integrated into their broader operational frameworks.**"

Also, as one of many other similar examples of what the DORA is all about, here is paragraph 26 of the preamble:  

**"\[w\]here no ICT testing is required, vulnerabilities remain undetected and result in exposing a financial entity to ICT risk and ultimately create a higher risk to the stability and integrity of the financial sector. Without Union intervention, digital operational resilience testing would continue to be inconsistent and would lack a system of mutual recognition of ICT testing results across different jurisdictions. In addition, as it is unlikely that other financial subsectors would adopt testing schemes on a meaningful scale, they would miss out on the potential benefits of a testing framework, in terms of revealing ICT vulnerabilities and risks, and testing defence capabilities and business continuity, which contributes to increasing the trust of customers, suppliers and business partners. To remedy those overlaps, divergences and gaps, it is necessary to lay down rules for a coordinated testing regime and thereby facilitate the mutual recognition of advanced testing for financial entities meeting the criteria set out in this Regulation."**

There can be no doubt about the fact that the Digital Operational Resilience Act, i.e., DORA, has as its intent to strengthen the resilience of the financial sector against digital operational risk, such as cyber attacks, vulnerabilities, and other ICT-related disruptions---and that therefore DORA needs to be read and interpreted with that intent in mind.

**Details**

The following are the main chapters of DORA, focusing on the key tasks that need to be done for a financial institution to be in compliance with DORA, each of which has relevance in the context of OpenJDK, illustrated by some key quotations copied directly from DORA, with commentary by me in the "Connection to Unsupported OpenJDK" sections.

### 1. Develop and Implement a Comprehensive ICT Risk Management Framework {#h3-0-1-develop-and-implement-a-comprehensive-ict-risk-management-framework}

* **Chapter**: DORA, Chapter II: ICT Risk Management
* **Relevant Articles and Sub-Articles** :
  * **Article 6(1):** "Financial entities shall have **a sound, comprehensive and well-documented ICT risk management framework** as part of their overall risk management system, which enables them **to address ICT risk quickly, efficiently and comprehensively** and **to ensure a high level of digital operational resilience**."
  * **Article 6(2):** "The ICT risk management framework **shall include at least strategies, policies, procedures, ICT protocols and tools that are necessary to duly and adequately protect all information assets and ICT assets** , including **computer software** , hardware, servers, as well as to protect all relevant physical components and infrastructures, such as premises, data centres and sensitive designated areas, **toensure that all information assets and ICT assets are adequately protected from risks**including damage and unauthorised access or usage."
  * **Article 6(3):** "In accordance with their ICT risk management framework, **financial entities shall minimise the impact of ICT risk by deploying appropriate strategies, policies, procedures, ICT protocols and tools** . They shall provide complete and updated information on ICT risk and on their ICT risk management framework to the competent authorities upon their request."
    * **Connection to Unsupported OpenJDK**: Unsupported OpenJDK distributions, which could be outdated OpenJDK versions without some or any of the latest security patches, cannot be a meaningful part of "a sound, comprehensive, and well-documented risk management framework", prescribed in 6(1), since they cannot "duly and adequately protect all information assets and ICT assets", as prescribed in 6(2), nor be part of an "appropriate strateg\[y\]" aimed at "minimis\[ing\] the impact of ICT risk", as prescribed in article 6(3) because they can introduce significant vulnerabilities and operational risk that undermine the resilience of ICT systems. Without regular updates and support, these OpenJDK environments may fail to protect critical functions, leading to non-compliance with Article 6.
  * **Article 8(1):** "As part of the ICT risk management framework referred to in Article 6(1), financial entities **shall identify, classify and adequately document** all ICT supported business functions, roles and responsibilities, **the information assets and ICT assets supporting those functions** , and their roles and dependencies in relation to ICT risk. **Financial entities shall review as needed, and at least yearly, the adequacy of this classification and of any relevant documentation.** "
    * **Connection to Unsupported OpenJDK**: Unsupported OpenJDK distributions may not receive the necessary updates to address vulnerabilities, meaning that they will not be in a state sufficient to satisfy Article 8(1) since outdated OpenJDK distributions will lead to inaccurate or incomplete identification and classification. Thorough and intentional identification, classification, and documentation of ICT assets, in the spirit of DORA, would be undermined if those assets would themselves be at least far more vulnerable than they could have been had they been supported rather than unsupported.

### 2. Establish an Incident Reporting Mechanism {#h3-1-2-establish-an-incident-reporting-mechanism}

* **Chapter**: DORA, Chapter III: ICT-related Incident Reporting
* **Relevant Articles and Sub-Articles** :
  * **Article 17(1):** "Financial entities shall define, establish and implement **an ICT-related incident management process to detect, manage and notify ICT-related incidents** ."
    * **Connection to Unsupported OpenJDK**: Unsupported OpenJDK environments may not include the latest language features, monitoring enhancements, and detection tools necessary for identifying ICT-related incidents, leading to delays or failures in incident reporting. This lack of timely reporting can result in non-compliance with Article 17(1).
  * **Article 18(1):** "**Financial entities shall classify ICT-related incidents and shall determine their impact based on the following criteria** : (a) the number and/or relevance of clients or financial counterparts affected and, where applicable, the amount or number of transactions affected by the ICT-related incident, and whether the ICT-related incident has caused reputational impact; (b) the duration of the ICT-related incident, including the service downtime; (c) the geographical spread with regard to the areas affected by the ICT-related incident, particularly if it affects more than two Member States;**(d) the data losses that the ICT-related incident entails, in relation to availability, authenticity, integrity or confidentiality of data; (e) the criticality of the services affected, including the financial entity's transactions and operations; (f) the economic impact, in particular direct and indirect costs and losses, of the ICT-related incident in both absolute and relative terms.** "
    * **Connection to Unsupported OpenJDK**: The lack of support in older OpenJDK distributions could hinder the classification, identification, and reporting of incidents, or provide incomplete or incorrect data, reducing the level of DORA compliance, delaying notifications to authorities as required by Article 18(1). This delay increases the risk of penalties and non-compliance and is not in the spirit of DORA, which exists to minimize risk and promote resilience.

### 3. Conduct Regular and Rigorous Testing of ICT Systems {#h3-2-3-conduct-regular-and-rigorous-testing-of-ict-systems}

* **Chapter**: DORA, Chapter IV: Digital Operational Resilience Testing
* **Relevant Articles and Sub-Articles** :
  * **Article 24(1):** "For the purpose of assessing preparedness for handling ICT-related incidents, of identifying weaknesses, deficiencies and gaps in digital operational resilience, and of promptly implementing corrective measures, financial entities, other than microenterprises, shall, taking into account the criteria set out in Article 4(2), **establish, maintain and review a sound and comprehensive digital operational resilience testing programme** as an integral part of the ICT risk-management framework referred to in Article 6."
  * **Article 24(2):** "The digital operational resilience testing programme shall **include a range of assessments, tests, methodologies, practices and tools** to be applied in accordance with Articles 25 and 26."
    * **Connection to Unsupported OpenJDK**: Unsupported OpenJDK distributions might not (and very likely don't and can't) reflect the current security and operational standards, leading to inaccurate test results that fail to identify critical weaknesses, resulting in, for example, the opposite to what is provisioned for in Article 24(1): "a sound and comprehensive digital operational resilience testing programme" and the "tests" referred to in Article 24(2) are, whatever the applicable adjective is, certainly assumed to not be "faulty tests". In other words, this could result in non-compliance with the requirements set out in Article 24(1) and 24(2), since there can only be very limited compliance with Article 24 in an inadequate testing environment.
  * **Article 25(1):** "The digital operational resilience testing programme referred to in Article 24 shall provide, in accordance with the criteria set out in Article 4(2), for **the execution of appropriate tests, such as vulnerability assessments and scans, open source analyses, network security assessments, gap analyses, physical security reviews, questionnaires and scanning software solutions, source code reviews where feasible, scenario-based tests, compatibility testing, performance testing, end-to-end testing and penetration testing** ."
    * **Connection to Unsupported OpenJDK**: Using unsupported OpenJDK in testing environments can lead to false security assumptions, which would fail the "appropriate tests" criteria, at least, because outdated systems will not accurately simulate real-world conditions. This can result in a failure to meet the standards set by Article 25(1), compromising the effectiveness of resilience testing.

### 4. Enhance Third-Party Risk Management Practices {#h3-3-4-enhance-third-party-risk-management-practices}

* **Chapter**: DORA, Chapter V: Management of ICT Third-Party Risk
* **Relevant Articles and Sub-Articles** :
  * **Article 28(2):** "As part of their ICT risk management framework, financial entities, other than entities referred to in Article 16(1), first subparagraph, and other than microenterprises, shall adopt, and regularly review, a strategy on ICT third-party risk, taking into account the multi-vendor strategy referred to in Article 6(9), where applicable. The strategy on ICT third-party risk shall include **a policy on the use of ICT services supporting critical or important functions provided by ICT third-party service providers** and shall apply on an individual basis and, where relevant, on a sub-consolidated and consolidated basis. The management body shall, **on the basis of an assessment of the overall risk profile of the financial entity and the scale and complexity of the business services, regularly review the risks identified in respect to contractual arrangements on the use of ICT services supporting critical or important functions** ."
    * **Connection to Unsupported OpenJDK**: If third-party providers rely on unsupported OpenJDK distributions, this poses a significant risk to the financial entity's ICT environment. These risks must be identified and mitigated to comply with Article 28(2), ensuring third-party services meet required security and resilience standards.

### 5. Facilitate Information Sharing on Cyber Threats {#h3-4-5-facilitate-information-sharing-on-cyber-threats}

* **Chapter**: DORA, Chapter VI: Information Sharing Arrangements
* **Relevant Articles and Sub-Articles** :
  * **Article 45(1):** "Financial entities may **exchange amongst themselves cyber threat information and intelligence, including indicators of compromise, tactics, techniques, and procedures, cyber security alerts and configuration tools** "
    * **Connection to Unsupported OpenJDK**: Though the word "may" indicates the above is optional, if faulty information were to be shared, that would be suboptimal and against the principle ideas behind DORA, which is to reduce risk and promote resilience. Unsupported OpenJDK environments may miss critical updates, leading to a lack of awareness about current threats and vulnerabilities. This can hinder effective information sharing, reducing the collective cybersecurity benefits outlined in Article 45(1).

### Summary {#h3-5-summary}

To briefly summarize some of the key points above, unsupported OpenJDK distributions are at risk of not being compliant with DORA for, at least, the following reasons.

1. **Increased Security Vulnerabilities (Article 6(1), 6(2), 6(3))**   
   Unsupported OpenJDK distributions lack necessary security updates, exposing ICT systems to increased risk, which violates Article 6(1) and 6(2). This also undermines the effectiveness of ICT risk management strategies required by Article 6(3).
2. **Inadequate Protection of ICT Assets (Article 6(2))**   
   Without regular updates, unsupported OpenJDK distributions cannot adequately protect ICT assets, leading to potential vulnerabilities and risk, in violation of Article 6(2).
3. **Incomplete Risk Documentation and Assessment (Article 8(1))**   
   Unsupported OpenJDK distributions can result in, or be an indicator of, outdated or inaccurate documentation of ICT-supported functions and assets, failing to meet the requirements of Article 8(1).
4. **Failure in Incident Detection and Reporting (Article 17(1), 18(1))**   
   Unsupported OpenJDK may lack the tools for effective incident detection, leading to delays in reporting ICT-related incidents, which contravenes Article 17(1) and hampers the proper classification and impact assessment required by Article 18(1).
5. **Inaccurate Resilience Testing (Article 24(1), 24(2), 25(1))**   
   Unsupported OpenJDK distributions can lead to inaccurate resilience testing, failing to identify critical weaknesses as required by Article 24(1) and 24(2). This undermines the necessary tests such as vulnerability assessments and penetration testing mandated by Article 25(1).
6. **Compromised Third-Party Risk Management (Article 28(2))**   
   If third-party providers rely on unsupported OpenJDK, this introduces significant risk to the financial entity's ICT environment, violating the requirements for third-party risk management outlined in Article 28(2).
7. **Ineffective Information Sharing (Article 45(1))**   
   Unsupported OpenJDK may lead to incomplete or inaccurate information sharing on cyber threats, which is essential for maintaining collective cybersecurity, as encouraged by Article 45(1).

The above is the only logical conclusion of reading DORA, assuming that it has meaning and intent in the context of OpenJDK or any other ICT asset that is vulnerable to the risk that DORA seeks to mitigate.
