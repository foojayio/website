---
title: "The SolarWinds Hack for Java Developers"
slug: "the-solarwinds-hack-for-java-developers"
date: "2021-02-09T07:39:53+00:00"
lastmod: "2021-02-09T12:40:16+00:00"
description: "For Java developers and architects who design, build, and run applications, there are two core take-aways to the SolarWinds hack..."
authors:
  - "erikcostlow"
image: "Favicon-3-2.png"
categories:
  - "JDK Flight Recorder"
  - "Security"
tags:
related_posts:
enlighterjs: true
frozen: false
---

In December 2020, an advanced persistent threat attacked many companies by [injecting malicious code into a vendor application](https://www.fireeye.com/blog/threat-research/2020/12/evasive-attacker-leverages-solarwinds-supply-chain-compromises-with-sunburst-backdoor.html) that belonged to SolarWinds. This technique is called a "supply chain attack," because instead of targeting the victim directly, the attacker damaged something higher up the supply chain and simply waited. The US government uses SolarWinds, so by attacking SolarWinds in a way that would infiltrate the US government, the attacker effectively got their target.

The SolarWinds attack is unique in that the hackers did not exploit a vulnerability in an application, rather they broke into the company and [attacked the development pipeline](https://orangematter.solarwinds.com/2021/01/11/new-findings-from-our-investigation-of-sunburst/). The attackers' implant worked in the build process, injecting new code into SolarWinds Orion as it was built to enable command \& control capabilities on target systems that ran the application.

For Java developers and architects who design, build, and run applications, there are two core take-aways:

1. **Monitoring software** at runtime can catch anomalous behavior.
2. Existing **integrity checks** in the Java platform can identify many code manipulation attempts.

Additional details about the attack and its functionality are available through [FireEye's SUNBURST research](https://www.fireeye.com/blog/threat-research/2020/12/evasive-attacker-leverages-solarwinds-supply-chain-compromises-with-sunburst-backdoor.html) and [follow-up](https://www.fireeye.com/blog/threat-research/2020/12/sunburst-additional-technical-details.html), as well as [Krebs on Security](https://krebsonsecurity.com/2021/01/solarwinds-what-hit-us-could-hit-others/).

### Monitoring Software to Catch Anomalous or Unsafe Behavior {#h3-0-monitoring-software-to-catch-anomalous-or-unsafe-behavior}

One way that the SolarWinds attack was caught came from [its outreach](https://www.fireeye.com/blog/threat-research/2020/12/sunburst-additional-technical-details.html) to a command and control server in its primary domain (avsvmcloud.com). While Java developers recognize that a URL commonly makes its way to a [URLConnection](https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/net/URLConnection.html), there are some primary JDK capabilities that will monitor this API, as well as many others.

#### JDK Flight Recorder

[OpenJDK Flight Recorder](https://github.com/openjdk/jmc) is not a security tool, rather it enables developers to perform deep analysis of a target JVM's performance operation. Originally written by Appeal Virtual Machines for JRockit, it forms the foundation of the [WebLogic Development Framework](https://docs.oracle.com/cd/E13153_01/wlcp/wlss40/operations/wldf.html#:~:text=Overview%20of%20Oracle%20Communications%20Converged%20Application%20Server%20and,about%20a%20WebLogic%20Server%20instance%20and%20its%20applications.), and has been freely available inside OpenJDK-based distributions since Java 11. Several Java 8 distributions have backported the feature, including the free [Azul Zulu](https://www.azul.com/downloads/zulu-community/?package=jdk) with [Zulu Mission Control](https://www.azul.com/products/zulu-mission-control/).

The aspect that makes JDK Flight Recorder effective for security monitoring is that it records all JVM-level I/O operations, identifying situations like: what URLConnections does the application open, what files does it open for read/write, what SQL calls does the application run, and more. While the primary audience of JDK Mission Control is performance engineers, it will also detect if the application reaches out to an unexpected hostname such as the SUNBURST command \& control server. Teams that leverage JDK Flight Recorder may detect various unexpected connections like libraries that phone home, or an application reading remote/local [XML Document Type Definitions](https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html).

Another capability of JDK Mission Control is its [event-streaming](http://hirt.se/blog/?p=1239), which sends these events outside the system to be reviewed elsewhere. The ability to store data local to the JFR black-box file as well as stream to a central server match the common practices of [NIST 800-92](https://csrc.nist.gov/publications/detail/sp/800-92/final) section 3 for log retention and centralized aggregation.

JDK Flight Recorder is intended for use in production environments with low overhead of about 1-2%, making it viable for enterprises to understand what their workloads are doing.

Flight Recorder only offers visibility and does not enable operators to control the action, for example to prohibit a certain URLConnection within the JDK.

#### Instrumented Monitoring like Contrast Security

The benefit of instrumented security monitoring is the ability to see information, build security intelligence, and take action like report or block.

Instrumentation has been part of Java since [JDK 1.5](https://docs.oracle.com/javase/1.5.0/docs/api/java/lang/instrument/package-summary.html) and enables agents to modify bytecode, typically onMethodEntry and onMethodExit, often to watch incoming values or adjust counters. Monitoring method arguments gives them the ability to see I/O values similar to what can be seen with JDK Flight Recorder. A key benefit of agents over the standard Java SecurityManager is their ability to adapt on context: where the SecurityManager can only allow/deny known files, the adaptive instrumented agent can allow files except when a filename is controlled by the user.

Java developers that monitor their applications with [Contrast Community Edition](https://www.contrastsecurity.com/contrast-community-edition) can detect unique security issues to their own application: when the application reads remote data, the agent marks that data as coming from a user (onMethodExit watching the return value). When the application goes to access another asset, such as a File or Runtime.exec, the agent can compare the incoming argument to see if it came from user input (onMethodEntry watching the argument). This would give the ability to detect and correct injection issues, such as [Path Traversal](https://owasp.org/www-community/attacks/Path_Traversal) and [Command Injection](https://owasp.org/www-community/attacks/Command_Injection). It will similarly give a listing of items like what cryptographic algorithms are in use, where hardcoded passwords are, and a fair amount of other security-oriented information.

### Verify Integrity Through Signed JAR Files {#h3-1-verify-integrity-through-signed-jar-files}

Java offers signed JAR files as a way of verifying the integrity of a library. These signatures leverage the public key infrastructure and certificate authorities so that a file can be verified without knowing the original author. Combined with timestamping, developers can know who published a library and when it was signed. In most cases, this integrity verification acts as a safeguard against anyone modifying the library contents either as it goes over the network or by changing a file anywhere in the build pipeline. The SolarWinds case was different, in that attackers targeted the build pipeline before the signature was applied. While this means that the SUNBURST malware would have been signed, signed JAR files represent a net benefit for most developers to validate their software.

The most common process for applying signatures is, an automated tool checks out code and compiles it, packages the class files into a JAR file, then signs the JAR file (or hands it to a signing service). Other users can then verify the signature by [verifying signed JAR files](https://docs.oracle.com/javase/tutorial/deployment/jar/verify.html) even if they do not know the original author. The command for this is:

`jarsigner -verify file.jar -verbose`

This command will inform whether the signature is valid or if anything has been modified. Software alone cannot validate if the signature was by the right party, so it is critical to verify that the output chains to a valid root certificate authority and that the signer's name matches what is expected.

If a JAR file is not signed, one of the ways that it can be modified is:

1. Extract a selected class file or unzip the entire JAR.
2. [Decompile any class](https://github.com/JetBrains/intellij-community/tree/master/plugins/java-decompiler/engine) that you want to target.
3. Recompile that source code file, using the JAR file as the classpath.
4. Repackage the unsigned JAR file with your modification.

Teams that build applications should verify signatures of any libraries that their application uses, and then sign all libraries that they distribute. While this may overwrite the original signature of a library, it authenticates that the library version went with the application itself. It is not feasible for a standard user to understand who made each library and to check that the signature for each library was signed by the correct party.
