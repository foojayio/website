---
title: "Introducing SAST, DAST, IAST and RASP"
slug: "sast-dast-iast-and-rasp"
date: "2021-08-05T08:28:52+00:00"
lastmod: "2021-08-05T08:29:47+00:00"
description: "In this article, we're going to look at the differences between the various cybersecurity defence techniques!"
authors:
  - "sven-ruppert"
image: "http://img.youtube.com/vi/sW7mTNVIUhE/mqdefault.jpg"
categories:
  - "DevOps"
  - "Security"
tags:
related_posts:
  - "spring-remote-code-execution-vulnerability"
  - "detecting-investigating-and-verifying-fixes-for-security-incidents-and-zero-day-issues-using-lightrun"
  - "psa-the-risks-of-remote-jdwp-debugging"
  - "solarwinds-hack-and-the-executive-order-of-cybersecurity-what-does-this-mean-for-us"
frozen: false
---

In this article, we're going to look at the differences between the various cybersecurity defence techniques. Here you can identify four main groups, which we will go through briefly one after another to illustrate the advantages and disadvantages.

**SAST: Static Application Security Testing**

SAST describes the process in which the components of an application are subjected to a static analysis. This approach not only searches for security gaps but also determines the licenses for the individual elements. In the following, however, I will only deal with the consideration of vulnerabilities.

The term 'static' comes from the fact that only the static context is used for this evaluation. All dynamic or context-related assessments are not possible with this method. SAST can be used to analyze the entire tech stack of an application if access to these components is possible, which is also one of the advantages of SAST compared to other analysis methods. The only exception is the IAST procedure, which we will come back to later.

What exactly happens now with the SAST procedure? SAST is available in two forms. The first that I would like to mention relates to checking your source code. Here the attempt is made to determine whether there are patterns that make a later vulnerability easier or even possible in the first place. For example, it searches for calls to critical libraries or identifies unsafe handling of resources. However, one honestly has to admit at this point that, first of all, your source code will be by far the smallest part of the overall application in the vast majority of projects. Second, the analysis methods in this area are still at a very early stage of development.

When using SAST, it makes a lot more sense to deal with all the other components first. Therefore, all binary files of the entire application environment are meant here. This also includes all elements that play a role in the development, such as the CI server and the other tools used.
> Tip: You can see the Blogpost as Youtube Video here:  
> [![DevSecOps - What is SAST DAST IAST and RASP - english - 4k](http://img.youtube.com/vi/sW7mTNVIUhE/mqdefault.jpg)](https://youtu.be/sW7mTNVIUhE "DevSecOps - What is SAST DAST IAST and RASP - english - 4k")

**DAST: Dynamic Application Security Testing**

The DAST analysis method does precisely the opposite in comparison to the SAST method. Here the application is viewed as a black box. As soon as an executable version of the application is available, it is attacked through automatically executed cyber attacks. In most cases, the general attack patterns are based on the Most Common Vulnerabilities defined by OWASP.

Defining your attack vectors is either not provided for in the tools I know of or requires very detailed knowledge in this area. For developers who do not use these tools daily and intensively, their use is limited to the predefined attack vectors. It is a good thing with this procedure that unknown security gaps can also be identified, as long as they are based on the Most Common Vulnerabilities. Some manufacturers extend this approach with AI or ML techniques. This approach using AI and ML techniques is still in a very early stage of development, and the currently available potential cannot be foreseen.

Unfortunately, this method can only be used sensibly against the production environment, as this is the only way to identify all gaps based on configuration deficiencies. Therefore, in contrast to the SAST process, it makes no sense to use it within the CI route.

**IAST: Interactive Application Security Testing**

IAST uses software tools to evaluate application performance and identify vulnerabilities. IAST takes an "agent-like" approach. Agents and sensors run to continuously analyze application functions during automated tests, manual tests, or a mixture of both.

The process and feedback occur in real-time in the IDE, Continuous Integration (CI) environment, or quality assurance or during production. The sensors have access to:

* All source code
* Data and control flow
* System configuration data
* Web components
* Backend connection data

The main difference between IAST, SAST, and DAST is that IAST runs inside the application. Access to all static components as well as the runtime information enables a very comprehensive picture. IAST is a combination of static and dynamic analysis. However, this part of the dynamic analysis is not a pure black-box test as implemented in DAST.

Reasons for using IAST include potential problems being identified earlier, so that IAST minimizes the cost of eliminating potential costs and delays. This is due to a "shift left" approach, meaning it is carried out in the early stages of the project life cycle. Similar to SAST, the IAST analysis provides complete lines of data-rich code so that security teams can immediately lookout for a specific bug. With the wealth of information that the tool has access to, the source of vulnerabilities can be precisely identified.

Unlike other dynamic software tests, IAST can be easily integrated into Continuous Integration and Deployment (CI / CD) pipelines. The evaluations take place in real-time in the production environment.

On the other hand, IAST tools can slow down the operation of the application. This is based on the fact that the agents change the bytecode themselves. This leads to a lower performance of the overall system. The change itself can, of course, also lead to problems in the production environment. The use of agents naturally also represents a potential source of danger since these agents can also be compromised, see the Solarwinds Hack.

**RASP: Runtime Application Self Protection**

RASP is the approach to secure the application from within. The backup takes place at runtime and generally consists of looking for suspicious commands when they are executed.

What does that mean now? With the RASP approach, you can examine the entire application context on the production machine and real-time. Here all commands that are processed are examined for possible attack patterns. Therefore, this procedure aims to identify existing security gaps and attack patterns and those that are not yet known. Here it goes very clearly into the use of AI and ML techniques.

RASP tools can usually be used in two operating modes. The first operating mode (monitoring) is limited to observing and reporting possible attacks. The second operating mode (protection) then includes implementing defensive measures in real-time and directly on the production environment. RASP aims to fill the gap left by application security testing and network perimeter controls. The two approaches (SAST and DAST) do not have sufficient visibility into real-time data and event flows to prevent vulnerabilities from sliding through the verification process or block new threats that were overlooked during development.

RASP is similar to Interactive Application Security Testing (IAST); the main difference is that IAST focuses on identifying vulnerabilities in the applications, and RASPs focus on protecting against cybersecurity attacks that can exploit these vulnerabilities or other attack vectors.

The RASP technology has the following advantages, first, RASP complements SAST and DAST with an additional layer of protection after the application is started (usually in production). It can be easily applied with faster development cycles. Unexpected entries are checked and checked. It enables you to react quickly to an attack by providing comprehensive analysis and information about the possible vulnerabilities.

However, RASP tools have certain disadvantages, by sitting on the application server, RASP tools can adversely affect application performance. In addition, the technology (RASP) may not be compliant with regulations or internal guidelines, which allows the installation of other software or the automatic blocking of services is permitted. The use of this technology can also lead to the people involved believing themselves to be false security.

The application must also be switched offline until the vulnerability is eliminated. RASP is not a substitute for application security testing because it cannot provide comprehensive protection. While RASP and IAST have similar methods and uses, RASP does not perform extensive scans but instead runs as part of the application to examine traffic and activity. Both report attacks as soon as they occur; with IAST, this happens at the time of the test, while with RASP, it takes place at runtime in production.

**Conclusion**

All approaches result in a wide range of options for arming yourself against known and unknown security gaps. Here it is essential to reconcile your own needs and those of the company for the future direction.

* **RASP:** We have seen that with RASP, there is an approach in which the application can protect itself against attacks at runtime. The permanent monitoring of your activities and the data transferred to the application enable an analysis based on the runtime environment. Here you can choose between pure monitoring or alerting and active self-protection. However, software components are added to the runtime environment with RASP approaches to manipulate the system independently. This has an impact on performance. With this approach, RASP concentrates on the detection and defence of current cyber attacks. So it analyzes the data and user behaviour in order to identify suspicious activities.
* **IAST:** Combines the SAST and DAST approach and is already used within the SDLC, i.e. within the development itself. This means that the IAST tools are already further "to the left" compared to the RASP tools. Another difference to the RASP tools is that IAST consists of static, dynamic and manual tests. Here it also becomes clear that IAST is more in the development phase. The combination of dynamic, static and manual tests promises a comprehensive security solution. However, one should not underestimate the complexity of the manual and dynamic security tests at this point.
* **DAST:** Focuses on how a hacker would approach the system. The overall system is understood like a black box, and the attacks occur without knowing the technologies used. The point here is to harden the production system against the Most Common Vulnerabilities. However, one must not forget at this point that this technology can only be used at the end of the production cycle.
* **SAST:** If you have access to all system components, the SAST approach can be used very effectively against known security gaps and license problems. This procedure is the only guarantee that the entire tech stack can be subjected to direct control. The focus of the SAST approach is on static semantics and, in turn, is completely blind to security holes in the dynamic context. A huge advantage is that this approach can be used with the first line of source code.

My personal opinion on these different approaches is that if you start with DevSecOps or security in IT in general, the SAST approach makes the most sense. This is where the greatest potential threat can be eliminated with minimal effort. It is also a process that can be used in all steps of the production line. Only when all components in the system are secured against known security gaps do the following methods show their highest potential.

After introducing SAST, I would raise the IAST approach and, finally, the RASP approach. This also ensures that the respective teams can grow with the task and that there are no obstacles or delays in production.
