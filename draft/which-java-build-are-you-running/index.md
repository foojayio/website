---
title: "Do You Know Which Java Build You Are Running?"
date: "2026-01-01"
description: "Most developers know they run Java 21 or 25, but not which build. After the October and November 2026 security updates, that gap matters more than ever."
authors:
  - "frankdelporte"
image: "header.jpg"
categories:
  - "Java"
  - "Java Core"
  - "Security"
related_posts:
  - "azul-zulu-october-2026-quarterly-update-and-out-of-cycle-release"
  - "java-patching-at-incident-speed-lessons-from-the-emudhra-ca-compromise"
  - "free-java-real-risk-why-unsupported-openjdk-is-now-a-security-gamble"
  - "azul-august-2026-release-javas-first-monthly-cspu"
  - "java4shell-cve-2026-114938-what-it-is-who-is-affected-and-what-to-do-now"
  - "all-azul-zulu-container-images-explained-ca-sa-and-chainguard"
---

At the JFall Conference in November (Ede, The Netherlands) I talked to a lot of Java developers and DevRel colleagues, and I asked everyone the same question: which Java version do you run? Most said 21, and a growing group said 25. That made me happy, because both are recent Long-Term Support (LTS) releases. Until a few years ago, the answer still included a lot of 8 and 11, which are now past their end-of-life. 

Then I asked a second question: which build? Almost nobody could answer. "21" is not an answer. It is a family of builds, and the distance between the oldest and the newest one is a year of security fixes.

## Why the build matters more than the version

Java 21 and Java 25 receive updates every quarter, and since August 2026 Azul also ships monthly security updates. Each one carries a different patch level, such as 21.0.13 or 25.0.5. The last few weeks show why that last digit counts:

- **October 20, 2026:** the quarterly update fixes [53 CVEs in Azul Zulu](https://docs.azul.com/core/release/october-2026/release-notes), the highest number in a single OpenJDK update in over a decade.
- **October 21, 2026:** a zero-day appears. Attackers hold the private keys of eight eMudhra intermediate certificate authorities, and Java does not check revocation by default. Azul ships an [out-of-cycle update](https://docs.azul.com/core/release/october-2026-respin/release-notes) the same day.
- **October 24, 2026:** a related vulnerability shows up. Azul ships a second [out-of-cycle update](https://docs.azul.com/core/release/october-2026-24-respin/), and anyone who stopped at October 21 needs to update again.
- **November 17, 2026:** the [monthly security update](https://docs.azul.com/core/release-notes) arrives for Java 21 and 25.

Three releases in five days, and a fourth four weeks later. If you installed the October quarterly update and walked away, you run a build that is already outdated.

I wrote about the first two events in [Azul Zulu October 2026 Quarterly Update: 53 CVEs, Then a Zero-Day One Day Later](https://foojay.io/today/azul-zulu-october-2026-quarterly-update-and-out-of-cycle-release/). My colleague Miles Dawborne explains why Java estates lag behind browsers in [Java Patching at Incident Speed](https://foojay.io/today/java-patching-at-incident-speed-lessons-from-the-emudhra-ca-compromise/). And [Java4Shell (CVE-2026-114938)](https://foojay.io/today/java4shell-cve-2026-114938-what-it-is-who-is-affected-and-what-to-do-now/) shows that the next critical flaw does not wait for the next quarter.

## The attacker does not wait for your change window

My colleague Simon Ritter makes the bigger argument in [Free Java, Real Risk: Why Unsupported OpenJDK Is Now a Security Gamble](https://foojay.io/today/free-java-real-risk-why-unsupported-openjdk-is-now-a-security-gamble/). AI agents shorten the time between disclosure and exploit from weeks to hours. He cites the 2026 Verizon DBIR, which puts the median time to fix a known vulnerability at 43 days.

Put those numbers side by side. The attacker needs hours, and the typical organization needs weeks. Closing that gap starts with a simple habit: know exactly which build runs where, and be able to replace it quickly.

{{< note >}}
Azul sponsors Foojay and both Simon and I work for Azul. The advice in this post applies to any OpenJDK build, but the build numbers and release notes I link are from Azul Zulu.
{{< /note >}}

## Read your own java -version

Open a terminal on your laptop, your build server, and one production node. Run:

```bash
java -version
```

The output looks like this on a current Azul Zulu 21 build (the exact strings on your machine differ):

```
openjdk version "21.0.13.1" 2026-10-20 LTS
OpenJDK Runtime Environment Zulu21.54.201-CA (build 21.0.13.1+2-LTS)
OpenJDK 64-Bit Server VM Zulu21.54.201-CA (build 21.0.13.1+2-LTS, mixed mode, sharing)
```

Three parts matter:

- **The version string** (`21.0.13.1`): the OpenJDK patch level. Compare it with the latest release.
- **The vendor build** (`Zulu21.54.201-CA`): the vendor, the Zulu version, and the availability type. CA means Community Availability, SA means Subscriber Availability.
- **The build number** (`+2`): the exact build of that patch level.

If you see 21.0.9 or 25.0.1, you run a build from October 2025. A full year of security fixes is missing.

## Compare with the latest release notes

The [Azul Core release notes](https://docs.azul.com/core/release-notes) list every build. The table below shows the November 17, 2026 monthly update:

| Java | PSU-based build (CA and SA) | OpenJDK version | CPU-based build (SA) | OpenJDK version |
|------|-----------------------------|-----------------|----------------------|-----------------|
| 21   | 21.54.201 (CA), 21.54.202 (SA) | 21.0.13.1 | 21.53.202 | 21.0.12.1 |
| 25   | 25.38.201 (CA), 25.38.202 (SA) | 25.0.5.1  | 25.37.202 | 25.0.4.1  |

CPU stands for Critical Patch Update and contains security fixes only. PSU stands for Patch Set Update and adds bug fixes and enhancements. Free Community builds follow the PSU line. If you need the security-only line for production, that is part of a subscription. The [August 2026 CSPU post](https://foojay.io/today/azul-august-2026-release-javas-first-monthly-cspu/) explains both lines in detail.

## See what your build is missing

A table of the latest builds tells you where you should be. It does not tell you how far behind you are. The [CVE History tool in the Azul Core docs](https://docs.azul.com/core/cve-history) answers that question.

Pick an LTS version (6, 7, 8, 11, 17, 21 or 25) and then the OpenJDK release you run. The tool shows three views:

- **Accumulated Vulnerability Exposure:** a chart that adds up the CVEs fixed in every release after yours.
- **CVE Fixes per Release:** a chart with the number of CVEs fixed in each release, so you see which quarters hurt most.
- **CVE list per release:** the individual CVEs behind the numbers.

You can also download the number of fixed CVEs per release as a CSV file, for example to add to a report for your security team.

<!-- TODO (Frank): add 2 or 3 screenshots of the CVE History tool, saved in this folder:
  1. cve-history-21-accumulated.png: Accumulated Vulnerability Exposure for Java 21, starting from 21.0.9 (the October 2025 build), to show how much a "just 21" install misses.
  2. cve-history-25-fixes-per-release.png: CVE Fixes per Release for Java 25, to show the October 2026 peak of 53.
  3. (optional) cve-history-21-vs-old-release.png: the same chart for Java 21 starting from an older release such as 21.0.5, to show the longer duration.
  Then add them here with the gallery shortcode (see template/post.md, section "Image gallery"), one "filename | description" line per image.
  Also add a sentence with the concrete numbers you read from the charts (for example "a 21.0.9 build misses N CVEs"). -->

The tool only counts CVEs fixed in OpenJDK builds. It leaves out CVEs that other distributions fixed and that are known not to affect Zulu. Figures for older releases can differ slightly from numbers other OpenJDK vendors report.

## Be ready to update with the push of a button

Knowing the build is step one. Step two is making the update boring. If replacing a JDK takes a bridge call and a week of coordination, you cannot move at incident speed.

### Docker images

Containers carry their own JDK and their own trust store. Patching the host changes nothing inside the image. Azul publishes the [Docker Official Image](https://docs.azul.com/core/docker-image-tags) `azul-zulu`, so you can use it without a namespace. Choose between two tag styles:

```dockerfile
# Floating tag: moves with every Azul release
FROM azul-zulu:21

# Pinned tag: Zulu version, then OpenJDK version, then variant
FROM azul-zulu:21.54-21.0.13-jre-headless-alpine3.23
```

Floating tags give you the newest build on every rebuild. Pinned tags give you reproducible builds, but you must bump them. Let a bot do the bumping. Dependabot watches Dockerfiles and opens a pull request when a newer tag exists:

```yaml
# .github/dependabot.yml
version: 2
updates:
  - package-ecosystem: "docker"
    directory: "/"
    schedule:
      interval: "daily"
```

Then make sure the pipeline rebuilds without cached layers, and check the result before you ship:

```bash
docker build --pull --no-cache -t myapp:latest .
docker run --rm myapp:latest java -version
```

The second command takes a second and tells you what actually runs. Add it as a step in your CI pipeline and fail the build when the version falls behind. For a full overview of the available images, see [All Azul Zulu Container Images Explained](https://foojay.io/today/all-azul-zulu-container-images-explained-ca-sa-and-chainguard/).

### Servers and virtual machines

If you install the JDK with a package manager, the update is one command and a restart. Put it in your configuration management tool (Ansible, Puppet, Chef) so every server gets the same build on the same day. A JDK you installed by hand from a tarball two years ago is the one nobody remembers. Replace it with a managed package.

### Inventory first

You cannot patch what you cannot find. Ask three questions about every application:

1. Which JDK version and vendor does it run?
2. Who owns it?
3. How quickly can the team deploy a new build?

A simple spreadsheet beats nothing, and a scripted `java -version` over SSH beats the spreadsheet. The goal is to answer in minutes, not days.

## Which line should you pick?

Install the CPU line quickly, after a short test, because it only contains security fixes and carries little regression risk. Test the PSU line over a longer period and move to it before the next quarterly release. That way a zero-day like the eMudhra case becomes a rebuild and a redeploy, not a regression test campaign.

## Check your build today

Pick one of your Java 21 or 25 applications right now and do two things:

1. Run `java -version` and write down the exact output.
2. Compare it with the table in the [latest Azul Core release notes](https://docs.azul.com/core/release-notes).

If the numbers match, ask yourself how fast your team would have reached them after the October 24 release. If they do not match, you now know what to fix first.

The next critical vulnerability does not announce itself. The teams that handle it calmly are the ones that already answered the question "which build do we run?" before it arrived.
