---
title: "Small, Signed, and Still Vulnerable? What Really Makes a Java Container Secure"
date: "2026-11-20"
description: "Small, distroless, signed, and scanned clean, yet vulnerable? Six Java container security myths taken apart and gaps closed."
authors:
  - "catherine-edelveis"
image: "iron.jpg"
categories:
  - "Java"
  - "Security"
  - "DevOps"
related_posts:
  - "java-patching-at-incident-speed-lessons-from-the-emudhra-ca-compromise"
  - "free-java-real-risk-why-unsupported-openjdk-is-now-a-security-gamble"
  - "testcontainers-integration-testing-running-your-real-war-in-docker"
  - "beyond-mvn-install-maven-practices"
---

**TLDR**

- An image can be small, distroless, signed, and scanned clean, but still harbor a security flaw.  
- The size of the base image alone tells you nothing about its patching policy.   
- What was secure today might be insecure tomorrow: scan and update images continuously.

Let me tell you a story about one Java container image, whose team did everything right. The image runs a Spring Boot JAR on a distroless JRE base, so there is no shell or a package manager. It is built in a multi-stage Dockerfile, signed with Cosign, and comes with an SBOM attached as an attestation. On 1 December 2021, the team runs it through a vulnerability scanner, and the report is as clean as a report can be.

But inside that jar sits `log4j-core-2.14.1.jar`. Nine days later, [CVE-2021-44228](https://nvd.nist.gov/vuln/detail/CVE-2021-44228) goes public under the name Log4Shell, and every attacker-controlled string the application logs becomes a potential way to make the JVM load and run remote code.

Nobody on that team did anything wrong. The image was small, the scan was clean, the base was distroless, the signature was valid. Each of these measures did its job, and each of them guarantees one specific thing. None of them guarantees 100% security.

So let's take six popular assumptions about container security and assess what each measure gives you and what you have to do about the gaps they leave out.

## A smaller image is not necessarily a safer image

Small images have a good reputation. Removing `wget`, `curl`, a package manager, and a compiler from the runtime image is good practice because fewer packages mean fewer CVEs and fewer tools for malicious actors to use if they get in.

But the attack surface is not a sum of all image components. It is a sum of attack vectors, i.e., the exploitable paths someone can exploit. A 4 MB image with a vulnerable library in the execution path is more dangerous than a 20 MB image whose extra megabytes belong to the timezone data.

The popular minimal distributions such as Alpine, Alpaquita, Wolfi, Chiseled Ubuntu, or UBI Micro take between approx. 4 and 10 MB compressed. In terms of size, they are similar. But the size alone tells you little about how fast critical fixes reach the image. What really makes the distributions different is who patches them and how fast. In addition, there's libc. A musl-based image is leaner, but native libraries, JNI code, or Java agents built against glibc may not run on it. So start with the list of minimalist Linux base images described above, but choose the OS by its update model and libc compatibility.

## Zero reported CVEs doesn't mean zero vulnerabilities

A scanner like Trivy or Grype identifies the packages and libraries in your image and matches their names and versions against databases of known vulnerabilities. "Zero CVEs" in this case mean that none of the components the scanner recognized matched the advisories it knew about at that moment.

That gives us two considerations.

Firstly, there’s no vulnerability for a scanner until someone discovers and creates an advisory for it. The Log4Shell code path entered Log4j with version 2.0-beta9 in 2013 and stayed there, invisible to every scanner, until December 2021\. [40,009 CVEs were published](https://jerrygamblin.com/2025/01/05/2024-cve-data-review/) in 2024, and each of them was a "zero" in somebody's report the previous day.

Secondly, a scanner can match only what it can identify. Trivy, for example, identifies a jar by its `pom.properties` or `MANIFEST.MF` file and uses a checksum lookup in its Java database. That is okay for a common Spring Boot fat or exploded jar with its dependencies nested in or attached to it. But when devs use shaded jars, the dependencies are placed into the application's own packages, and their metadata is removed. So, there’s nothing to match for a scanner.

Considering the above, what do you think: How many unknown vulnerabilities are in your image right now? Nobody can tell you. You can’t control what you don’t know — sounds pessimistic, but not quite true in our situation.

What you can control is how quickly a newly disclosed CVE turns into a finding for your affected services. This is where a Software Bill of Materials can help you. Generate an application SBOM at build time from your build tool's dependency graph (for example, with the CycloneDX Maven plugin), so that shaded dependencies are included too, and associate it with the image you ship. Then rescan it continuously. Ideally, have a separate SBOM for the base image (some maintainers provide it out-of-the-box). This will help you to split the accountability clearly: what should be patched by your engineers and what goes to the base image maintainer.

## Distroless and hardened are not synonyms

Scratch, distroless, slim, and hardened base images all fall under the umbrella of minimalistic bases. But they mean different things.

Scratch is an empty image. It might be optimal for a static Go binary, but not for a JVM. Distroless images contain a stripped down distro with only the runtime files an application needs, but no shell or package manager. Slim images are trimmed versions of regular distributions. What was removed depends on the vendor and the tag.

These three concepts describe contents. Image hardening describes a process around the contents. Hardened images are characterized by a limited component set, an SBOM, signed artifacts, documented provenance, a vulnerability-management process, and a maintainer committed to regular updates.

Some hardened images are distroless, but not all distroless images are automatically hardened. This is because "no extra components" tells you nothing about who rebuilds the base when the remaining OS packages get a CVE, or how fast.

The same goes for a shell-free image. A malicious actor with code execution inside your JVM (exploiting Log4Shell, for example) doesn't need a shell because the JVM itself can open network connections, write files, and load classes.

So look for hardened images in the first place, and then choose a flavor – slim or distroless — and a configuration among them that fits your application. 

## A signed image is not necessarily a safe image

A valid signature proves who signed the artifact and that it arrived unaltered. It doesn't prove who built it — for that, you need verified provenance. It says nothing about whether the artifact is safe. In March 2024, the xz-utils compression library shipped versions 5.6.0 and 5.6.1 with a backdoor targeting SSH servers ([CVE-2024-3094](https://nvd.nist.gov/vuln/detail/CVE-2024-3094)). The CVE lived in the official release tarballs, which were published through the project's normal release process by a co-maintainer who had gradually taken over the releases.

It doesn’t mean signatures are useless. But a signature alone is not a proof of safety. You have to also verify provenance, assess the general security of the upstream project, scan artifacts, and control dependency updates. Also, use least-privilege configurations and runtime monitoring of suspicious behavior to limit damage if malicious code gets in.

A signature is useless only in one case — if nobody checks it. So validate the signature of the base image before building on it and sign your own image with its SBOM attached. At deploy, verify the attestations with an admission controller like Kyverno's image verification policies or the Sigstore policy-controller. These solutions can reject an image that isn't signed by the identity you expect.

## A secure base image cannot fix an insecure application

A hardened base can bring the OS and runtime layers close to zero known CVEs. But a Java container has more layers than that. And the rest is your responsibility.

Start with the dependencies. Log4Shell was not a base image issue, it nested in an application dependency. The same goes for Spring, Jackson, and all transitive dependencies pulled in: scan them through the application SBOM and keep them updated.

Then analyze how your containers run. If the Dockerfile doesn't set a user, Docker runs the process as root. Coupled with `--privileged` or extra capabilities, it may help the attacker who escapes the application get to the host.

Here's a multi-stage build that keeps the toolchain in the builder and runs the service as a non-root user on a [BellSoft Hardened Image](https://hub.docker.com/r/bellsoft/hardened-liberica-runtime-container):

```
FROM bellsoft/hardened-liberica-runtime-container:jdk-25-musl AS builder
WORKDIR /app
COPY . /app/app
RUN cd app && ./mvnw package

FROM bellsoft/hardened-liberica-runtime-container:jre-25-nonroot-musl
WORKDIR /app
# Replace app-*.jar with your actual Maven output name (<artifactId>-<version>.jar)
COPY --from=builder /app/app/target/app-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
```

The same structure works with other Java images, such as `eclipse-temurin:25-jdk-alpine` for the builder and `eclipse-temurin:25-jre-alpine` for the runtime. In that case, you have to explicitly set `USER 1234:1234` in the final stage. The `nonroot` tag saved you from having to remember to do that.

Also, never bake secrets into an image\! A database password in an `ENV` instruction or a `COPY`\-ed properties file becomes available to everyone who pulls the image. Fetch the secrets at runtime from a secret manager such as HashiCorp Vault or Kubernetes Secrets.

Finally, take care of the host. Containers share the node's kernel, so a kernel vulnerability puts every container on that node at risk, no matter how clean their images are. Keep the node kernel updated and enable seccomp and AppArmor or SELinux.

## Security at build time doesn't guarantee security next month

Suppose you did everything above. Then a week passes, new CVEs are found in the OS packages, the JRE, and your dependencies, and the image that was clean at build time becomes vulnerable without a single change on your part.

The good news is that reliable vendors publish patched base images constantly. The not-so-good news: when a vendor releases an update for your base image, it won't get updated in your builds auto-magically. A remediation SLA only helps when you rebuild and deploy the patched image.

Getting there starts with pinning. A tag like `jre-25-musl` can drift and point to a different image tomorrow, so pin the base image by digest and keep the tag in the reference: `bellsoft/hardened-liberica-runtime-container:jre-25-musl@sha256:…`. The digest pins the exact image, while the tag tells humans and tools what it is. Then, run the scans and monitor updates regularly. One way of automating this process is to use tools like Renovate or Dependabot, which use the tag to track the image and open a pull request with the new digest whenever the vendor publishes an update.

Implement updates on a regular schedule (weekly, for instance) and keep an emergency lane for actively exploited vulnerabilities. The emergency lane is fast if the regular one has kept everything close to current.

All of this needs an owner. Decide who watches base image releases, who approves the rollout, and who gets the ticket when a critical CVE lands on a Friday evening. If nobody owns the update, the clean image from launch day slowly turns into the 604-vulnerability average [reported by NetRise](https://www.prnewswire.com/news-releases/netrise-releases-supply-chain-visibility--risk-study-edition-2-containers-revealing-significant-visibility-and-risk-challenges-within-common-containers-302326331.html) for popular Docker Hub images.

## Before you call an image secure

What really makes a Java container secure is not one universal method, but a combination of techniques applied consistently.

For every Java image you ship, answer these questions:

- Who patches the OS and runtime layers, and how fast?  
- When was the image last rescanned against current advisories, and who reads the results?  
- Is the root filesystem read-only, and are capabilities dropped?  
- Does the cluster reject an image whose signature or attestations don't verify?  
- What's in your jar, and when did you last update it?  
- Which digest is running in production right now, and how old is its base?

If any answer is "I don't know," that's the next thing to fix.
