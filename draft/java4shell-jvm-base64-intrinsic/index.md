---
title: "Java4Shell From the JVM Side: Is Your Service Running the Vulnerable Base64 Intrinsic?"
date: "2026-01-01"
description: "Java4Shell (CVE-2026-114938) lives in a HotSpot intrinsic, not a library, so the same jar can be safe on one machine and exploitable on the next. How to tell which one you have, apply the mitigation so it actually takes effect, and confirm the patched runtime."
authors:
  - "garth-patil"
image: "java4shell-jvm.jpg"
categories:
  - "Security"
  - "Java Core"
related_posts: []
---

Java4Shell (CVE-2026-114938), disclosed on November 17, is an unusual Java vulnerability. There is no
dependency to bump and no library to swap out. The bug is in code the JVM writes for you: a
hand-tuned assembly version of `java.util.Base64.Decoder` that HotSpot uses only on some processors.
That means the same application jar, in the same container image, can be safe on one host and
exploitable on the next.

This article is about the JVM side of it: what the intrinsic is, how to tell whether a given process
is running it, how to switch it off so the switch actually takes effect, and how to confirm you have
the patched runtime. I run a company that hosts Keycloak, which was the first application the
researchers attacked, so the examples come from there. Everything applies to any Java service that
decodes Base64 it didn't produce, and that is most of them.

## What an intrinsic is, and why this one matters

`java.util.Base64` is ordinary Java code in the JDK. Because Base64 decoding is so common, HotSpot
also ships an *intrinsic* for it: hand-written machine code that the C2 JIT compiler substitutes for
the Java method once that method is hot. On x86-64 processors with AVX-512 VBMI, HotSpot uses a
vectorized intrinsic that decodes 64 characters at a time.

The vulnerability is in how that vectorized code handles the last few characters of malformed input.
A specific invalid string (64 `A` characters followed by `=`) makes the tail handling write past the
end of the output buffer, on the Java heap, corrupting whatever object happens to sit next to it.
[The researchers at Calif](https://calif.io/java4shell/) turned that into remote code execution.

Two things follow:

- **"Hot" is the normal state.** The intrinsic is only used after C2 compiles the method. On a server
  that decodes Base64 on every request, that happens within moments of startup, so the vulnerable
  code is the code that is running.
- **The attacker supplies the input.** Keycloak decodes Base64 before anyone has logged in: JWTs such
  as client assertions, the `SAMLRequest` parameter, `Authorization: Basic` credentials and cookies.
  Your service probably has its own list. Anything that accepts a token, a signed payload or a
  `data:` URL is decoding attacker-controlled Base64.

## Which JVMs are affected

| | |
|---|---|
| **JDK** | OpenJDK 17.0.6 and later, and builds derived from it: Temurin, Corretto, Red Hat, Zulu, Liberica, Microsoft, Oracle and others, until patched |
| **Also listed by Oracle** | Oracle Java SE 8u511b33-perf, Oracle GraalVM for JDK 17 and 21, and Oracle GraalVM Enterprise Edition 21.3, per [the CVE record](https://www.cve.org/CVERecord?id=CVE-2026-114938) |
| **CPU** | x86-64 with AVX-512 VBMI: Intel Ice Lake or newer, AMD Zen 4 or newer |
| **Not affected** | ARM64 (for example AWS Graviton or Apple Silicon), which uses a separate implementation |
| **Severity** | Oracle rates it CVSS 3.1 **8.1**; the researchers rate it CVSS 4.0 **9.2** |

The CPU scope comes from the researchers. [The official CVE record](https://www.cve.org/CVERecord?id=CVE-2026-114938),
filed by Oracle, lists affected Oracle Java builds and says nothing about architecture. Oracle's fixes
are in its [November 2026 security alert](https://www.oracle.com/security-alerts/cspunov2026.html).

## Is this process running the vulnerable path?

Three things have to be true: an affected JDK, a CPU with the right instructions, and code that
decodes untrusted Base64. The third is almost always true, so check the first two.

**The CPU.** On the Linux host, or inside a container, which sees the host's CPU:

```bash
grep -o -w -E 'avx512f|avx512bw|avx512vbmi|bmi2' /proc/cpuinfo | sort -u
```

If all four flags come back, the JVM can select the vulnerable code. If any is missing, it can't.

**The runtime.** Ask the running process, not the `java` on the path. If the JDK was updated
without a restart, `java -version` reports the new build while the service is still running the old
one. Inside the container:

```bash
jcmd 1 VM.version
```

That prints the build the process actually loaded, for example `JDK 21.0.13.1`. After updating the
runtime, restart the service before you trust the answer.

**Don't use the JVM flag as a test.** It is tempting to check whether intrinsics are on:

```bash
java -XX:+PrintFlagsFinal -version | grep UseBASE64Intrinsics
```

On the Apple Silicon laptop I'm writing this on, with JDK 25.0.1, that prints:

```text
     bool UseBASE64Intrinsics                      = true                                      {product} {default}
```

`true` on an ARM64 machine, which isn't affected. The flag says that *a* Base64 intrinsic is enabled,
not *which* one. ARM64 has its own implementation, and so do x86-64 CPUs without AVX-512 VBMI. The CPU
flags are what decide exposure.

**Mixed fleets are where this bites.** A Kubernetes node pool with more than one instance type, or an
autoscaling group that falls back to another family when capacity is short, can run the same image
safely on one node and unsafely on the next. Many current-generation x86 cloud instance families run
on Ice Lake or Zen 4. Check every node that can run the service, including the ones autoscaling and
failover bring in. A pool can mix CPUs with and without AVX-512 VBMI, so one node doesn't speak for
the pool.

## The mitigation, and making sure it applies

Turn the intrinsic off:

```bash
-XX:-UseBASE64Intrinsics
```

`java.util.Base64` then runs as plain Java. Behavior is unchanged. For Keycloak the cost was
negligible, but if your service is dominated by Base64 work, measure it before rolling out widely.

The researchers also suggest `-XX:UseAVX=2`, which hides AVX-512 from the JVM entirely. That's a
blunter tool, and on ARM64 the JVM refuses to start with it. If one image runs on both
architectures, use the Base64 flag.

The hard part isn't the flag. It's getting it onto the running process.

**Environment variables replace, they don't append.** Many images read extra JVM options from a
variable, such as Keycloak's `JAVA_OPTS_APPEND`. If you bake the flag into the image's value and your
deployment (a Helm `extraEnv`, a Kubernetes `env` block, an ECS task definition, a Compose file) also
sets that variable for an APM agent or heap settings, the deployment's value wins and the flag
silently disappears. Put the flag in the value the deployment sets:

```yaml
extraEnv: |
  - name: JAVA_OPTS_APPEND
    value: "-javaagent:/opt/dd-java-agent.jar -XX:-UseBASE64Intrinsics"
```

`JAVA_TOOL_OPTIONS` is the JVM-level alternative, read by the JVM itself rather than by a start
script, and it announces itself on startup:

```text
$ JAVA_TOOL_OPTIONS=-XX:-UseBASE64Intrinsics java -XX:+PrintFlagsFinal -version | grep -iE 'picked up|base64'
Picked up JAVA_TOOL_OPTIONS: -XX:-UseBASE64Intrinsics
     bool UseBASE64Intrinsics                      = false                                     {product} {environment}
```

It has the same replace-not-append behavior if something else in your stack already sets it.

**Then check the running process, not the config.** After the restart, inside the container:

```text
$ jcmd 1 VM.flags | tr ' ' '\n' | grep -i base64
-XX:-UseBASE64Intrinsics
```

`ps -o args= -p 1` works too when the flag is on the command line, but it won't show a flag that
arrived through `JAVA_TOOL_OPTIONS`. `jcmd` shows the flags the JVM actually applied, whichever way
they arrived.

## The fix: a patched runtime, verified per architecture

The flag is a mitigation. The fix is a JDK with the patch: Oracle's November 2026 releases, and the
corresponding vendor builds. Red Hat's OpenJDK 21 fix is 21.0.13.1.

Three packaging traps we hit, or watched others hit, while rebuilding images:

1. **Pin the runtime, not the alias.** On Wolfi and Chainguard bases, `openjdk-21-default-jvm` is a
   symlink package. Pinning only that left the old runtime in place when we rebuilt on top of an
   existing image. Pin `openjdk-21-jre>=21.0.13.1` itself.
2. **Re-pushed tags don't reach nodes that already have them.** If an image tag is rebuilt in place,
   a node with the old layers cached keeps running the old runtime until it pulls again. Pull or pin
   the new digest, then restart.
3. **Check every architecture you ship.** A multi-arch image is two images:

   ```bash
   docker run --rm --platform linux/amd64 --entrypoint java <image> -version
   docker run --rm --platform linux/arm64 --entrypoint java <image> -version
   ```

## For Keycloak specifically

The official `quay.io/keycloak/keycloak` image was refreshed on November 19 with Red Hat OpenJDK
21.0.13.1. The fixed tags are `latest`, `26.8`, `26.8.2` and `26.8.2-3`. Older streams weren't
refreshed, so keep the flag there. At Phase Two we rebuilt our open Keycloak images
(`quay.io/phasetwo/keycloak` and `quay.io/phasetwo/keycloak-crdb`) on the patched runtime for both
architectures, including the older 26.7, 26.6 and 26.4 lines that upstream doesn't publish images
for. The full tag list is in our
[Java4Shell notes for Keycloak operators](https://phasetwo.io/blog/keycloak-java4shell-cve-2026-114938/).

## The question to take away

Most vulnerability management answers "which version are we running?" Java4Shell asks a different
question: "which runtime, on which CPU?" If you can't answer that for your JVM fleet today, the
`/proc/cpuinfo` check and a `jcmd` on every node that runs a JVM are a good afternoon's work, and they'll matter
again the next time a bug lives in the JIT.
