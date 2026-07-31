---
title: "7 Jackson CVEs in One Day: AI-Assisted Security Research"
slug: "7-new-vulnerabilities-in-jackson-in-one-day-this-is-what-ai-assisted-security-research-looks-like"
date: "2026-06-29T11:47:41+00:00"
description: "Seven jackson-databind vulnerabilities, one researcher, one day. Two critical RCEs. This is AI-assisted security research in practice."
authors:
  - "steve-poole"
image: "ChatGPT-Image-Jun-29-2026-12_44_59-PM.png"
categories:
  - "Java"
  - "Security"
tags:
related_posts:
  - "ai-found-the-bugs-whos-patching-your-eol-java-code"
  - "did-ai-just-break-software-security-for-ever"
  - "grails-isnt-done-yet-part-1-inside-the-asf-reboot"
  - "foojay-podcast-95"
frozen: false
---

**Quick version check:** the affected range for all seven is broadly `>=2.10.0 =2.19.0 =3.0.0 <3.1.4` --- with some CVEs affecting narrower ranges. If you're on a supported release, upgrade to 2.18.8, 2.21.4, or 3.1.4. If you're on an EOL line --- 2.13.x, 2.14.x, 2.15.x --- jump to the bottom of the page for more specifics or visit [HeroDevs Jackson Support](https://docs.herodevs.com/jackson?utm_source=devrel&amp;utm_medium=referral&amp;utm_campaign=2026q2_spring-boot-3-5-eol_global)

*** ** * ** ***

### Not a sales pitch {#h3-0-not-a-sales-pitch}

Anyone who knows me knows I dont do that. In this case I'm pointing you at [HeroDevs](https://docs.herodevs.com/jackson?utm_source=devrel&amp;utm_medium=referral&amp;utm_campaign=2026q2_spring-boot-3-5-eol_global) because the Jackson issues are serious, [HeroDevs](https://docs.herodevs.com/jackson?utm_source=devrel&amp;utm_medium=referral&amp;utm_campaign=2026q2_spring-boot-3-5-eol_global) have a solution thats stupidly easy to use and I know the folks behind the fixes. It takes a particular type of engineer to create security fixes and I know they have that skillset. Do your own research.

What Just Happened {#h2-1-what-just-happened}
---------------------------------------------

On 22 June 2026, seven vulnerabilities in `jackson-databind` were published. All fixed in the same June 4th releases, all credited to a single researcher. Two critical-level RCEs. Five further access control and deserialization issues. One research effort, one codebase, seven findings.

Sounds like a fairytale?

In a [recent article](https://foojay.io/today/did-ai-just-break-software-security-for-ever/), I argued that four converging forces were breaking the old software security equilibrium: CVE volume, AI-assisted discovery, an overwhelmed maintainer community, and regulators running out of patience.

This batch is that argument made concrete.

### How findings like this are now getting made {#h3-2-how-findings-like-this-are-now-getting-made}

The short version: give a capable AI model the right context, remove the restrictions that exist to prevent misuse, and point it at a codebase. It reasons about trust boundaries, validator assumptions, and edge cases the way a senior security engineer would. But faster, and without getting bored or distracted.

### This is mainstream {#h3-3-this-is-mainstream}

Both Anthropic and OpenAI now run formal programmes for exactly this. Anthropic's [Cyber Verification Program](https://support.claude.com/en/articles/14604842-real-time-cyber-safeguards-on-claude) unlocks dual-use security capabilities for credentialed professionals. OpenAI's [Trusted Access for Cyber](https://openai.com/index/scaling-trusted-access-for-cyber-defense/) has expanded to thousands of verified defenders. Both are documented, public, and growing.

The result, when it works, is not one finding but many.

FIRST's [mid-year vulnerability forecast](https://www.first.org/newsroom/releases/20260615), published June 15th, revised its 2026 projection upward to approximately 66,000 CVEs. 46% above what was predicted *just four months earlier*, driven in part by AI-assisted discovery.

Seven Vulnerabilities {#h2-4-seven-vulnerabilities}
---------------------------------------------------

All seven are fixed in `jackson-databind` 2.18.8, 2.21.4, and 3.1.4. All seven were published as GHSAs on June 16th. For EOL versions (2.13.x, 2.14.x, 2.15.x), HeroDevs NES fixes are available for the two critical RCEs now, with the remaining five to follow.

### The Critical RCEs {#h3-5-the-critical-rces}

**[CVE-2026-54512](https://www.herodevs.com/vulnerability-directory/cve-2026-54512)** ([GHSA-j3rv-43j4-c7qm](https://github.com/FasterXML/jackson-databind/security/advisories/GHSA-j3rv-43j4-c7qm)) --- CVSS v4 9.2 (Critical), EPSS 44%

The `PolymorphicTypeValidator` allowlist checks the container class name but never validates generic type parameters inside it. Supply `java.util.ArrayList` --- `ArrayList` passes the check, the gadget class rides in unchallenged. With a reachable gadget on the classpath, that's unauthenticated RCE. Affects `>=2.10.0`. Requires polymorphic typing enabled with a PTV allowlist.

**[CVE-2026-54513](https://www.herodevs.com/vulnerability-directory/cve-2026-54513)** ([GHSA-rmj7-2vxq-3g9f](https://github.com/FasterXML/jackson-databind/security/advisories/GHSA-rmj7-2vxq-3g9f)) --- CVSS v4 9.3 (Critical)

`BasicPolymorphicTypeValidator.allowIfSubTypeIsArray()` approves any array based only on `clazz.isArray()` --- it never checks the element type against the allowlist. Supply `EvilType[]` and the validator waves it through. Same class of attack as CVE-2026-54512, different entry point. Affects `>=2.10.0`. Also requires polymorphic typing.

Both score Critical under CVSS v4. The CVSS v3.1 scores are 8.1 (High) due to Attack Complexity: High, reflecting the prerequisite that polymorphic deserialization must be enabled.

### The Access Control and Deserialization Bypasses {#h3-6-the-access-control-and-deserialization-bypasses}

**[CVE-2026-54514](https://www.herodevs.com/vulnerability-directory/cve-2026-54514)** ([GHSA-hgj6-7826-r7m5](https://github.com/FasterXML/jackson-databind/security/advisories/GHSA-hgj6-7826-r7m5)) --- CVSS 5.3 (Medium)

`InetSocketAddress` deserialization triggers an eager DNS lookup at construction time --- before any application-level validation. Attacker-controlled hostname in JSON causes an outbound DNS query. SSRF. Affects `>=2.0.0`. No polymorphic typing required.

**[CVE-2026-54515](https://www.herodevs.com/vulnerability-directory/cve-2026-54515)** ([GHSA-5jmj-h7xm-6q6v](https://github.com/FasterXML/jackson-databind/security/advisories/GHSA-5jmj-h7xm-6q6v)) --- CVSS 5.3 (Medium)

Case-insensitive deserialization (`@JsonFormat(ACCEPT_CASE_INSENSITIVE_PROPERTIES)`) rebuilds `_beanProperties` from the unfiltered map rather than the contextual one, restoring properties that `@JsonIgnoreProperties` had just removed. Mass-assignment bypass. Affects `>=3.1.0` only.

**[CVE-2026-54516](https://www.herodevs.com/vulnerability-directory/cve-2026-54516)** ([GHSA-9fxm-vc8v-hj55](https://github.com/FasterXML/jackson-databind/security/advisories/GHSA-9fxm-vc8v-hj55)) --- CVSS 5.3 (Medium)

A property with `@JsonProperty("renamed")` on the getter and `@JsonIgnore` on the setter gets renamed rather than dropped. With `INFER_PROPERTY_MUTATORS` enabled (default), the private backing field is retained and made writable. Attacker supplies the renamed key, field is set directly, bypassing the `@JsonIgnore`. Property tampering. Affects `>=2.21.0` and `>=3.0.0`.

**[CVE-2026-54517](https://www.herodevs.com/vulnerability-directory/cve-2026-54517)** ([GHSA-5hh8-q8hv-fr38](https://github.com/FasterXML/jackson-databind/security/advisories/GHSA-5hh8-q8hv-fr38)) --- CVSS 5.3 (Medium)

`@JsonView` filter applied only to creator properties in `_deserializeUsingPropertyBased`; the setterless collection path bypasses it entirely. A setterless collection annotated with a restricted `@JsonView` can be written from untrusted JSON. Access control bypass. Affects `>=2.21.0` and `>=3.0.0`.

**[CVE-2026-54518](https://www.herodevs.com/vulnerability-directory/cve-2026-54518)** ([GHSA-rcqc-6cw3-h962](https://github.com/FasterXML/jackson-databind/security/advisories/GHSA-rcqc-6cw3-h962)) --- CVSS 6.5 (Medium)

`UnwrappedPropertyHandler.processUnwrappedCreatorProperties()` replays buffered JSON into creator parameters without checking `prop.visibleInView(activeView)`. Constructor parameters annotated with both `@JsonView` and `@JsonUnwrapped` get populated from attacker JSON even when a restrictive view is active. Access control bypass. Affects `>=2.21.0` and `>=3.0.0`.

*** ** * ** ***

The Validator Is the Vulnerability {#h2-7-the-validator-is-the-vulnerability}
-----------------------------------------------------------------------------

The two critical RCEs are the most serious findings, but all seven share a common thread: Jackson's own annotation-based security mechanisms are the attack surface.

CVE-2026-54512 and CVE-2026-54513 bypass the `PolymorphicTypeValidator` --- introduced after a [well-documented series of high-severity deserialization CVEs](https://www.sonatype.com/blog/jackson-databind-the-end-of-the-blacklist) stretching from 2017 to 2019. The bug in CVE-2026-54512 dates to 2019 --- introduced with the same release, 2.10, that brought the validator itself.

CVE-2026-54515 through CVE-2026-54518 bypass `@JsonIgnoreProperties`, `@JsonIgnore`, and `@JsonView` --- the annotation-based access control mechanisms developers use to keep fields unwritable from untrusted input.

The pattern is the same across all seven: a security boundary that looks closed is open in edge cases the original implementation didn't anticipate. "I added the validator" and "I annotated the field" are the beginnings of a security posture, not the ends of one.

*** ** * ** ***

The Creaking Disclosure Pipeline {#h2-8-the-creaking-disclosure-pipeline}
-------------------------------------------------------------------------

The CVE pipeline , which most people rely on without knowing it, has multiple steps beyond the fix itself. A [CNA](https://www.cve.org/resourcessupport/allresources/cnarules "CNA") assigns an ID, [NVD](https://nvd.nist.gov/ "NVD") enriches it, and security scanners - from Dependabot to Snyk, to your SCA tool all sit downstream.

The OSS fixes shipped on June 4th. GHSAs were published June 16th. [On 23 June, a third party filed an issue on the GitHub Advisory Database](https://github.com/github/advisory-database/issues/8093) noting that none of the seven had propagated to the global database. Since then, at least the two critical CVEs have begun appearing in scanner databases. Snyk at least is now showing both. The pipeline moved, but it took time.

Most modern scanners have moved away from pure NVD dependence. Dependabot pulls from the GitHub Advisory Database directly, as do at least Trivy, Snyk, and Orca.

But NVD enrichment provides something those sources don't always carry and which is vital to you: CVSS scores, CPE matching data, and the context security teams use to prioritise remediation.

[NIST acknowledged in April 2026](https://flashpoint.io/blog/national-vulnerability-database-nvd-shifts-to-selective-enrichment-as-cve-volume-surges/) that CVE submissions have grown 263% since 2020 and NVD can no longer enrich all of them.

Don't assume your scanner's silence, or its alert, tells the whole story. Check your `jackson-databind` version directly.

*** ** * ** ***

### Who's Effected {#h3-9-who-s-effected}

|                          | Supported (2.18.x, 2.21.x, 3.x) | EOL (2.13.x, 2.14.x, 2.15.x) |
|--------------------------|---------------------------------|------------------------------|
| CVE IDs                  | All seven                       | Same CVE IDs                 |
| OSS fix                  | ✅ 2.18.8, 2.21.4, 3.1.4         | ❌ None                       |
| NES fix (critical RCEs)  | ---                             | ✅ 2.13.6, 2.14.4, 2.15.5     |
| NES fix (remaining five) | ---                             | In progress                  |

**If you're on a supported release** --- upgrade to 2.18.8, 2.21.4, or 3.1.4. All seven resolved.

**If you use polymorphic deserialization** --- `activateDefaultTyping()` or `@JsonTypeInfo` --- the two critical RCEs are directly relevant. Patch immediately.

**If you use `@JsonView`, `@JsonIgnore`, or `@JsonIgnoreProperties`** as security boundaries on writable fields, audit whether the five access control CVEs affect your configuration.

### If you are on an EOL stream {#h3-10-if-you-are-on-an-eol-stream}

That's 2.13.x, 2.14.x, or 2.15.x, there is no community fix. The same CVE that prompts a team on 2.18.7 to upgrade in an afternoon sits unfixed for a team on 2.14.x with nowhere to go. Your options are migration, mitigations, or commercial support. NES for Jackson has backported fixes for the two critical RCEs to 2.13.6, 2.14.4, and 2.15.5. Use the [HeroDevs EOL Dataset](https://www.herodevs.com/eol-dataset/overview?utm_source=devrel&amp;utm_medium=referral&amp;utm_campaign=2026q2_spring-boot-3-5-eol_global) or [endoflife.date](https://endoflife.date/jackson-databind) to understand your full exposure.

What This Actually Means {#h2-11-what-this-actually-means}
----------------------------------------------------------

Seven vulnerabilities in `jackson-databind`, one researcher, one day. All fixed before the advisories were published. Some still not visible yet to the full ecosystem.

This is not a one-off. The formal infrastructure for AI-assisted security research now exists at scale. The libraries that haven't been looked at carefully in years are not safe, they're simply unexamined. The absence of CVEs was never evidence of safety. It was evidence of silence.

That silence is ending. It's really time to know your dependencies their versions and their EOL status.

Just do it? {#h2-12-just-do-it}
-------------------------------

You've got a scanner, an SCA tool and in anycase you can just generate an SBOM and use that. It's easy to do - why wait. Use the [HeroDevs EOL Dataset](https://www.herodevs.com/eol-dataset/overview?utm_source=devrel&amp;utm_medium=referral&amp;utm_campaign=2026q2_spring-boot-3-5-eol_global) or [endoflife.date](https://endoflife.date)

*Having said that , I will be back with a EOL tooling / data article to help with getting started.*
