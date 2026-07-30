---
title: "IBM Semeru Runtimes for Java includes FIPS 140-3 certified cryptography"
slug: "ibm-semeru-java-fips140-3-cryptographic-standard"
date: "2024-08-27T08:55:26+00:00"
lastmod: "2024-08-29T13:36:30+00:00"
description: "IBM Semeru Runtimes for Java 11+ includes FIPS 140-3 cryptography certified by the U.S. National Institute of Standards and Technology (NIST)."
authors:
  - "laura-cowen"
image: "https://foojay.io/wp-content/uploads/2020/04/Favicon-3-2.png"
categories:
  - "Java"
  - "Security"
tags:
related_posts:
  - "where-do-you-get-your-java"
  - "semeru-v11-beyond-oct-2024"
  - "start-using-java-21"
  - "pseudorandom-number-generator"
frozen: false
---

**IBM® Semeru Runtimes™ for Java™ 11, 17, 21+ now includes FIPS 140-3 cryptography ([certified by the U.S. National Institute of Standards and Technology; NIST](https://csrc.nist.gov/projects/cryptographic-module-validation-program/certificate/4755 "certified by the U.S. National Institute of Standards and Technology (NIST)")) and is production-ready for Java deployments. FIPS 140-3 is also available in IBM SDK, Java Technology Edition, V8.**

IBM first made FIPS 140-3 cryptography available as a technology preview more than a year ago, and users responded with helpful feedback on usage and configuration so that the IBM development team could improve its usability. With NIST certification, this capability is now generally available for production deployments on most platforms (with more to come).

What is FIPS 140-3? {#h2-0-what-is-fips-140-3}
----------------------------------------------

FIPS 140-3 certified cryptography is a security standard for U.S. federal agencies to use when [securing computer and telecommunication systems with cryptography](https://csrc.nist.gov/pubs/fips/140-3/final "computer and telecommunication systems"). Software used by those federal agencies, including Java, must comply with FIPS cryptography standards. On May 12, 2022, U.S. President Executive Order 14028 re-enforced the requirement for FIPS-validated encryption and brought significant attention across agencies to the FIPS 140-3 standard which supersedes FIPS 140-2.

FIPS 140-3 in IBM Semeru Runtimes for Java {#h2-1-fips-140-3-in-ibm-semeru-runtimes-for-java}
---------------------------------------------------------------------------------------------

Users of IBM Semeru Runtimes for Java now have an all-in-one solution for FIPS 140-3 cryptography when running their production Java workloads. FIPS 140-3 is available in both [IBM Semeru Runtime Open Edition and Certified Edition](https://www.ibm.com/support/pages/java-sdk-downloads "IBM Semeru Runtime Open Edition and Certified Edition") and provides a broad set of FIPS 140-3 cryptography algorithms and ciphers.

IBM Semeru Runtimes provides multiple security profiles to choose from. Security profiles provide developers and operators with a tool to manage their use of certified cryptography and to confidently upgrade the security posture of their Java workloads over time.

FIPS 140-3 in IBM Semeru Runtimes is built with IBM Crypto for C, a NIST-certified high performance FIPS 140-3 native cryptographic library. IBM Crypto for C is based on OpenSSL and is available as the open source project [OpenCryptographyKitC](https://github.com/IBM/OpenCryptographyKitC "OpenCryptographyKitC").

Configuring FIPS 140-3 in IBM Semeru Runtimes for Java {#h2-2-configuring-fips-140-3-in-ibm-semeru-runtimes-for-java}
---------------------------------------------------------------------------------------------------------------------

To learn more:

For IBM Semeru Runtimes for Java 11+ (starting with 11.0.24.0, 17.0.12.0 and 21.0.4.0), [download the latest Semeru Open and Certified Edition releases](https://ibm.biz/GetSemeru "download the latest Semeru Open and Certified Edition releases") and find out more about [the open source OpenJCEPlusFIPS FIPS 140-3 JCE provider](https://ibm.biz/SemeruFIPS "the open source OpenJCEPlusFIPS FIPS 140-3 JCE provider").

For IBM SDK, Java Technology Edition, V8 (starting with 8.0.8.30), [download the latest IBM SDK release](https://www.ibm.com/support/pages/java-sdk-downloads-version-80 "download the latest IBM SDK release") and find out more about [the IBMJCEPlusFIPS FIPS 140-3 JCE provider](https://ibm.biz/IBMSDK8FIPS "the IBMJCEPlusFIPS FIPS 140-3 JCE provider").

Commercial support for IBM Semeru Runtimes for Java is available (but completely optional) with [IBM Runtimes for Business](https://www.ibm.com/products/runtimes-for-business "IBM Runtimes for Business").

Secure, efficient, stable {#h2-3-secure-efficient-stable}
---------------------------------------------------------

With bundled NIST-certified FIPS 140-3 cryptography, you can adopt IBM Semeru Runtimes (or update your IBM SDK, Java Technology Edition, V8 installation) to run production Java workloads securely, efficiently, and with stability, whether your workloads run on-premises or in the cloud and whether you're a small, medium, large, or huge business.

(See the [original announcement post](https://community.ibm.com/community/user/wasdevops/blogs/paul-arockiam/2024/08/14/ibm-releases-fips-140-3-certified-crypto-for-java?CommunityKey=4a44a543-2f75-4f5c-bdb7-f81c3cf4fce4 "original announcement post") by Paul Arockiam.)
