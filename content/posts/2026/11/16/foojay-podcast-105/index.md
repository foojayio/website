---
title: "Foojay Podcast #105: Security Is Your Job: OWASP, AI and Safer Java Dependencies with Jim Manico"
date: "2026-11-16"
description: "OWASP's Jim Manico on modernizing the Java Encoder and HTML Sanitizer with AI, trimming dependencies, and why every Java team needs a security process."
authors:
  - "frankdelporte"
  - "jim-manico"
image: "episode-105-owasp-security.jpg"
categories:
  - "AI"
  - "Dependency Management"
  - "Java"
  - "Podcast"
  - "Security"
related_posts:
  - "owasp-java-security-libraries-please-update"
  - "foojay-podcast-104"
  - "free-java-real-risk-why-unsupported-openjdk-is-now-a-security-gamble"
  - "azul-zulu-october-2026-quarterly-update-and-out-of-cycle-release"
frozen: false
---

How safe is your Java application, and how safe are the dependencies it ships with? If you build software that handles sensitive data and you have no security process, Jim Manico has a blunt word for it: negligent.

In this episode, Jim Manico, founder of Manicode Security, Java Champion and OWASP project leader, explains what OWASP actually is and which of its projects matter to a Java developer. He recently brought three nearly abandoned libraries back to life, the OWASP Java Encoder, the OWASP Java HTML Sanitizer and the OWASP JSON Sanitizer, using AI-assisted workflows, and he shares what that taught him.

We cover when to use the encoder and the sanitizer, why untrusted input must be checked on both the client and the server, how a few kilobytes of nested CSS can cause a denial-of-service, and why small vulnerabilities become a major one when an attacker chains them together. Jim also argues for radically fewer third-party libraries, explains how to keep the ones you have up to date, and ends with what to do on Monday morning if your team has no security process yet.

## YouTube

{{< youtube Q9EIBoek2zc >}}

## Podcast Apps

You can listen and subscribe to the Foojay Podcast on:

* [Spotify](https://open.spotify.com/show/6CpTfgn9LirzJGAtc4ICdQ)
* [Apple Podcasts](https://podcasts.apple.com/be/podcast/foojay-io-the-friends-of-openjdk/id1652281304)
* And most others...

## Guests

* Jim Manico - Founder of Manicode Security, Java Champion and OWASP project leader
  * [LinkedIn](https://www.linkedin.com/in/jmanico/)
  * [Foojay Author Page](https://foojay.io/today/author/jim-manico/)

## Links

* [OWASP Java Security Libraries: Please Update](https://foojay.io/today/owasp-java-security-libraries-please-update/)
* [Foojay Podcast #104: 53 CVEs, Two Emergency Releases with Simon Ritter](https://foojay.io/today/foojay-podcast-104/)
* OWASP Java libraries
  * [OWASP Java Encoder](https://github.com/OWASP/owasp-java-encoder)
  * [OWASP Java HTML Sanitizer](https://github.com/OWASP/java-html-sanitizer)
  * [OWASP JSON Sanitizer](https://github.com/owasp/json-sanitizer)
* OWASP documentation projects led by Jim
  * [OWASP Cheat Sheet Series](https://github.com/OWASP/CheatSheetSeries)
  * [OWASP AISVS (Artificial Intelligence Security Verification Standard)](https://github.com/OWASP/AISVS)
  * [OWASP Proactive Controls](https://github.com/OWASP/www-project-proactive-controls/)
* Open source tools by Jim
  * [grim-prompter: threat modeling prompts](https://github.com/jmanico/grim-prompter/)
  * [trim-reaper: third-party library reduction prompts](https://github.com/jmanico/trim-reaper/)
  * [dread-ledger: security logging prompts](https://github.com/jmanico/dread-ledger/)
  * [dread-forge: Secure SDD Kit](https://github.com/jmanico/dread-forge/)

## Content

* 00:00 Introduction of topic and guest
* 02:11 What is OWASP?
* 04:10 How AI influences the speed of OWASP changes. Who contributes.
* 06:30 Which OWASP projects you should know as a Java developer
* 07:30 How Jim uses AI to evolve his libraries
* 08:28 When to use OWASP Java Encoder and OWASP Java HTML Sanitizer
* 13:58 User input needs to be checked in all steps (UI and backend)
* 16:07 Man-in-the-middle attacks (eMuhdra)
* 17:17 How to keep your systems up-to-date + minimize your dependencies
* 22:34 How CSS can cause a denial-of-service problem
* 24:52 How multiple small security issues can be combined into a major issue
* 26:36 How to handle the growing number of discovered vulnerabilities
* 29:13 The era of coding is coming to an end but you can still be a master developer
* 32:45 How to automate upgrading with Maven, Gradle, and other tools
* 36:42 What Java developers can learn from recent security problems (like PHP and eMuhdra)
* 38:42 You must have a security process! Make your managers aware...
* 43:53 How to get involved in OWASP
* 46:08 What to remember from this podcast: security is your responsibility!
