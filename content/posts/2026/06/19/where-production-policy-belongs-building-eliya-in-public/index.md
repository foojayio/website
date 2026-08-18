---
title: "Where production policy belongs: building Eliya in public"
slug: "where-production-policy-belongs-building-eliya-in-public"
date: "2026-06-19T08:15:28+00:00"
description: "Where does production policy belong in a managed runtime? The thesis behind Eliya, an opinionated OpenJDK 25 distribution, and the one flag it ships. Part 1."
authors:
  - "fahim-farook"
image: "eliya.png"
categories:
  - "Developer Tools"
  - "Java"
  - "OpenJDK Migration"
tags:
related_posts:
  - "boxlang-ai-3-2-0-image-generation-web-search-fluent-audio-agent-registry-mcp-observability"
  - "a-glance-into-jfr-class-and-method-tagging"
  - "where-do-you-get-your-java"
  - "the-files-in-jdk-21"
enlighterjs: true
frozen: false
---

This is an argument about where production intent belongs in a managed runtime, and the OpenJDK distribution we built to act on it - Eliya, an opinionated, compliance-conscious OpenJDK distribution. This is the first article in a series. The later parts are engineering: reproducible builds, the glibc floor, release signing, the one source patch we shipped. This part is the thesis they all serve.
![](https://root.asymm.systems/images/jvm-behaviour-space.png)

Every configurable system has two spaces. The configuration space: every behaviour you can reach by setting the knobs the system already exposes. And there's the implementation space: behaviour that only exists if someone goes in and changes the internals.

A wrapper script can do a lot. Setting flags, launching a `-javaagent`, mounting a volume. That's more than "selecting a flag"; it composes external system behaviour around the JVM. But notice the floor it hits, and the floor is the point. It can't rewrite the heap-dump byte stream as that stream is being written. It can't tell you a flag's origin - whether the value was derived internally or set on the command line. Even a native JVMTI agent - the strongest external case, running in the JVM's own address space - works through the runtime's published extension points. The boundary that matters isn't how many knobs you can reach from outside. It's whether you can change what the JVM's own code does on the inside - and a wrapper, however elaborate, is always on the outside.

Most operational requirements live well outside that boundary, and that's fine. Most requirements never need the JVM's interior. The interesting problems start when one does.

## Policy, mechanism, and a caveat

OS research named this separation fifty years ago (the HYDRA paper). Policy names an intent; mechanism implements it. The JVM exposes mechanisms individually. e.g., -XX:+HeapDumpOnOutOfMemoryError is a mechanism. The JVM has long exposed first-class flags for some intents - like compilation (`-server`, tiered compilation) and memory. What it does not expose is a policy that says "When this process dies, it should leave enough evidence to diagnose why".

The upstream JDK is largely neutral, by design, so it doesn't ship the kind of opinionated defaults that particular groups or industries would want as their baseline. A JDK distribution however can take that opinion and cater to those groups with the policies built in.

Why should such an opinionated distribution exist at all? Defaults are policy, whether anyone intends them to be or not. Johnson and Goldstein measured organ-donor consent at around 12% in Germany and over 99% in Austria ("Do Defaults Save Lives?", *Science* , 2003), two culturally similar countries, and the entire gap came down to which box was pre-ticked.  

People run what ships. What a JDK distribution defaults to becomes, in practice, the configuration of the fleet. That justifies shipping a distribution with production-grade defaults.

Second, it still doesn't justify patching a VM (which we did), because a wrapper could ship defaults too. Here is the caveat. It's easy to overreach, so let's be exact about the limit. Separating policy from mechanism does not mean policy has to live inside the mechanism. Plenty of systems externalise their policy correctly. Kubernetes pushes authorisation to admission controllers, applications delegate access decisions to IAM. So I can't claim "policy belongs in the runtime". The real claim is narrower, and harder to dispute: *some* policies depend on what only the runtime can see or do - a flag's origin, the live object graph, the bytes a dump writer is about to emit. Such policies require runtime intimacy, and externalising them isn't a style preference, it's just not possible. An out-of-the-box JVM often leaves less evidence than operators expect when something goes wrong. No GC log. No heap dump. Crash log written to some ephemeral path that dies with the container. And recording those traces correctly (redacting what's sensitive, signing what has to be tamper-evident, attesting what produced each setting) needs exactly the runtime intimacy from a paragraph ago. That's the thing a wrapper can't ship, and the only reason a patch exists.

The argument can be stated more formally:
> **The placement claim.** Let C be the set of external controllers that configure the JVM, and B the set of behaviors those controllers can produce. Every wrapper, Helm chart, or webhook is merely a selector operating within C to pick a point inside B. But certain policies demand behaviors outside B (call them B') because they depend entirely on what only the runtime can see or do. A B' behavior can be obtained only by changing the runtime itself. Therefore, the policy that targets B' is, narrowly, the one that belongs in the VM.

Take an example where a requirement actually falls outside B - the PCI DSS Requirement 3.5.1, which says PAN (Primary Account Number) must be unreadable anywhere it is stored. However, a heap dump of a payment service writes live card numbers to disk in cleartext. A critic is right that you can deal with this from outside the VM by disabling heap dumps, or encrypting the volume. But look at what each one costs you. By disabling dumps you've thrown away the forensic evidence that was the whole reason for running this way, and volume encryption protects the disk while the dump still travels cleartext from memory to the writer inside the trust boundary. Redacting the dump *as the stream is written*, inside the VM, kills the dilemma instead of trading one risk for another. That's a dump-writer problem in HotSpot. You can't implement that behaviour purely by composing existing JVM flags.

## The flag

The whole argument comes down to one flag, which activates a policy group:

```bash
java -XX:EliyaProfile=Production -jar app.jar
```

That flag ships in [Eliya](https://asymm.systems/product/eliya) - an OpenJDK 25 LTS distribution from Asymm Systems, first GA earlier this month, built for compliance-conscious production in regulated industries: telecom, banking and financial services, healthcare, government. `EliyaProfile` is the policy point the thesis calls for: a `ccstr` enum. `Production` - the general set of production-readiness defaults - is the value that ships today (Phase 1); further values are reserved, some on the roadmap, the rest demand-gated.

Quick word on the name. Eliya is short for Nuwara Eliya, the highland tea country of Sri Lanka, a few hours from where I'm writing this. The Sinhala word means *light*. Java took its name from an Indonesian island that grows coffee. Ours comes from highlands that grow tea.

## The Production profile

The `Production` profile ergonomic defaults:

1. **Heap dump on OOM** , written to a structured path under `${ELIYA_DIAGNOSTIC_PATH}/${service}/${replica}/heap-dumps/`
2. **Exit on OOM**, a clean shutdown so orchestration can restart you instead of leaving a zombie JVM
3. **Native Memory Tracking** in `summary` mode
4. **Crash log path** , a predictable `hs_err_pidNNNN.log` location under the same tree
5. **Container support reinforced** : `UseContainerSupport=true` is guaranteed under the profile. Upstream JDK 25 already defaults it on, so today this is a no-op. It's there so the guarantee survives a future upstream changing its mind.
6. **Diagnostic VM options unlocked**, which JFR sampling and profiler attachment need to work accurately

Notice that all six of these are existing HotSpot flags, and the structured path could be resolved by a wrapper script and handed to -XX:HeapDumpPath. Phase 1 ships no behaviour a script couldn't reproduce. Strictly speaking, `Production` is selection from the configuration space. So why patch the VM at all, instead of shipping a wrapper?

Because Phase 1 isn't the capability - it's the boundary. `EliyaProfile` is a named policy point established inside the runtime, and that's where the genuinely runtime-only capabilities of later phases attach.

`Production` is a *fail-safe default* in the Saltzer-Schroeder sense: the VM sets its ergonomics with ergonomic origin, so an operator who explicitly passes a value at the command line wins. Normal JVM precedence (command line beats ergonomic) is respected. Production yields to the operator by design.

Two independent dimensions are getting conflated here. It's worth separating them:

1. **Enforcement Style:** does the profile actively defend an invariant by rejecting a conflicting override, or does it only set a default and step aside? A profile could reject startup when an override conflicts with a constraint it holds, but whether it should depends entirely on the second dimension.
2. **Authority Model:** who owns the invariant, and who has the right to waive it? This is the real difference. `Production`'s constraints are operational - they exist to serve the operator's own goal, diagnosability. An operator who deliberately overrides one is making a call they're entitled to make; they own the goal, so the consequence is theirs to accept, and the profile yields. A compliance value's constraints are external - they exist to satisfy a regulator, not the operator. An SRE saying "I accept card numbers in a world-readable dump" isn't authorised to make that trade; the regulation is. So the profile fails closed at startup regardless of operator intent.

In the old access-control sense - same flag, two authority models. Naturally, mandatory enforcement requires the orchestration plane to seal the profile flag in the deployment manifest and ensure a random operator cannot bypass compliance by removing the flag entirely.

Everything else is upstream OpenJDK 25 unchanged. `java.security` is bit-identical to upstream - TLS 1.0 / 1.1 disabled, weak ciphers blocked, and current minimum key-size requirements are already in place. GC selection is left to JDK 25's ergonomics. Outside the profile, Eliya remains intentionally close to upstream, and EliyaProfile=None preserves upstream behaviour.

## The 3 AM story

None of the theory above is how anyone experiences the problem. Here's the version everyone recognises.

It's 3 AM. The pager goes off - the JVM running your settlement engine just OOM'd and the container restarted. Your first instinct is to pull the GC logs. There aren't any - nobody enabled `-Xlog:gc*`. Fine, the heap dump then. Also missing: nobody set `-XX:+HeapDumpOnOutOfMemoryError`, and nobody set `-XX:HeapDumpPath` either, so even if there were a dump it'd be in whatever ephemeral container path the JVM happened to be running from. The crash log? Same story.

Everybody knows these flags. Most have negligible runtime cost. Yet production systems run without them, because every shop builds its own answer to "what flags should be on?" - usually after its first incident. The configuration-errors literature says this isn't carelessness. i.e. hand humans enough knobs and misconfiguration becomes a dominant failure mode (Yin et al., SOSP 2011; Xu et al., "Hey, You Have Given Me Too Many Knobs!", FSE 2015). The remedy the literature points to is the one the thesis points to: ship the answer in the system, as one intent-level control, instead of re-deriving it per team as a dozen mechanism-level ones.

If you're not in the audience that needs this - you're building an internal tool, or your team has already done the flag work - a neutral upstream build is the right answer, and there are several good ones.

## Where things stand, and what comes next

Phase 1, shipped this month: one opt-in flag; Linux x86_64 and aarch64; `.tar.gz` / `.deb` / `.rpm` / multi-arch GHCR Docker; signed and reproducible; quarterly upstream-CPU refreshes within two weeks of each upstream CPU, through the JDK 25 LTS window (September 2029); GPLv2 with Classpath Exception; corresponding source attached to each release. No JDK 21 build, deliberately. JDK 29 LTS arrives at its GA with a 24-month overlap before Eliya 25 sunsets.

Phase 2 (target H2 2026) builds on the same policy point with continuous JFR, bundled local-only diagnostics tooling, and a FIPS-validated provider variant. It also brings unified GC logging (-Xlog:gc\*) under the profile - deferred from Phase 1 only because HotSpot's internal LogConfiguration initializes too early for a safe ergonomic override without a deeper source patch.

We're going to build the rest of this in public, on Foojay, one piece at a time - scoped to the distribution itself. The parts already queued:

* **Reproducible builds** - every source of non-determinism we had to kill to get byte-for-byte rebuilds.
* **The glibc floor** - why a modern build host silently breaks your binary on enterprise Linux, and the per-arch devkit decision we made about it.
* **Signing and key hygiene** - the easy 80% (the signature) and the hard 20% (everything around the key).
* **The structured diagnostic path** - the one genuine source patch in Phase 1, and why `${service}/${replica}` resolution belongs in the VM rather than a wrapper.

## Verify it yourself

Let's run Eliya and see:

### 1. Pin the bytes

```bash
# Download the artefact + signed checksums:
curl -fsSLO https://github.com/asymmsystems/eliya-jdk/releases/download/eliya-jdk-25.0.3/eliya-jdk-25.0.3-linux-x64.tar.gz
curl -fsSLO https://github.com/asymmsystems/eliya-jdk/releases/download/eliya-jdk-25.0.3/SHA256SUMS.txt
curl -fsSLO https://github.com/asymmsystems/eliya-jdk/releases/download/eliya-jdk-25.0.3/SHA256SUMS.txt.asc

# Fetch the signing key, then cross-check its fingerprint 
# against at least one independent channel before trusting it
gpg --keyserver keys.openpgp.org --recv-keys 076DE547397A5D27EECEE0B307A90689B71A158F
gpg --fingerprint <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="9afff6f3e3fbdafbe9e3f7f7b4e9e3e9eefff7e9">[email protected]</a>
# Expected: 076D E547 397A 5D27 EECE  E0B3 07A9 0689 B71A 158F

# Verify the signature on the checksums file, 
# then verify the checksum on the tarball:
gpg --verify SHA256SUMS.txt.asc SHA256SUMS.txt
sha256sum -c SHA256SUMS.txt --ignore-missing
# Expected: "Good signature from "Eliya Releases (Asymm Systems) <<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="c6a3aaafbfa786a7b5bfababe8b5bfb5b2a3abb5">[email protected]</a>>"" + "OK" on the tarball checksum.
```

The full multi-channel verification ceremony is documented at [verify download](https://asymm.systems/product/eliya/user-guide/verify-download) page.

For Docker users the equivalent is pinning by digest:

```bash
# Get hold of the multi-arch manifest's digest
docker buildx imagetools inspect ghcr.io/asymmsystems/eliya-jdk:25.0.3
# Replace <digest> with the value above
docker pull ghcr.io/asymmsystems/eliya-jdk@sha256:<digest>
```

### 2. Read the build identity

```bash
# Extract and confirm the vendor string:
tar xzf eliya-jdk-25.0.3-linux-x64.tar.gz
./eliya-jdk-25.0.3/bin/java -version
```

### 3. Confirm the profile activated

```bash
./eliya-jdk-25.0.3/bin/java -XX:EliyaProfile=Production -XX:+PrintFlagsFinal -version 2>&1 \
    | grep -E '(HeapDumpOnOutOfMemoryError|ExitOnOutOfMemoryError|NativeMemoryTracking|ErrorFile|UnlockDiagnosticVMOptions) +='

# Each of these shows "{ergonomic}" at the end of the line
# i.e. HotSpot's own origin marker. 
# Re-run with -XX:EliyaProfile=None and see the difference.
```

Pinning Eliya in CI is the next step after verifying the bytes. Learn about the four pinning patterns in the [Eliya Versioning](https://asymm.systems/product/eliya/user-guide/versioning) guide.

References:

* Levin, Cohen, Corwin, Pollack \& Wulf, "Policy/Mechanism Separation in HYDRA", SOSP 1975
* Johnson \& Goldstein, "Do Defaults Save Lives?", Science 302, 2003
* Yin et al., "An Empirical Study on Configuration Errors" SOSP 2011
* Xu et al., "Hey, You Have Given Me Too Many Knobs!", ESEC/FSE 2015
* Saltzer \& Schroeder, "The Protection of Information in Computer Systems" Proc. IEEE 63(9), 1975
* PCI DSS v4.0 Req 3.5.1
