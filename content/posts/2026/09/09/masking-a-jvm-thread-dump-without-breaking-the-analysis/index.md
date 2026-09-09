---
title: "Masking a JVM thread dump without breaking the analysis"
date: "2026-09-09T07:32:00+00:00"
description: "A few weeks ago I posted on r/java about detecting virtual thread pinning from a plain thread dump. The technical discussion was fine. The comment that…"
canonical: "https://dev.to/maschiojv/masking-a-jvm-thread-dump-without-breaking-the-analysis-c0b"
authors:
  - "felipe-maschio"
image: "foojay-featured-thread-dump-anonymizer.png"
categories:
  - "Java"
  - "Java Core"
  - "Security"
related_posts:
  - "community-spotlight-boxlang-express-brings-node-style-http-to-the-jvm"
  - "boxlang-1-14-0-sets-ranges-inner-classes-and-a-runtime-that-talks-back"
  - "boxlang-ai-3-2-0-image-generation-web-search-fluent-audio-agent-registry-mcp-observability"
  - "introducing-bx-jwt-enterprise-grade-json-web-tokens-for-boxlang"
frozen: false
---

A few weeks ago I posted on r/java about detecting virtual thread pinning from a plain thread dump. The technical discussion was fine. The comment that stuck with me had nothing to do with pinning: "the problem isn't taking the dump, it's that uploading a production dump to a third-party site is against policy". Not a preference. Policy. And the person saying it was right.

I run ThreadMine, a hosted thread dump analyzer, so that comment describes my own product. This article is about what I built in response: a small open-source CLI that rewrites the dump on your machine before it goes anywhere, in a way that keeps the analysis working. It's called `tm-anon`, it's MIT, and it works with any analyzer, mine included. The interesting part isn't the tool, it's the constraint: you can't just strip everything, because the analyzer needs most of what's in there.

## What a thread dump actually leaks

Take one line from a real dump:

```
at com.acme.payment.LedgerService.applyEntry(LedgerService.java:95)
```

Package, class, method, source file. Multiply by a few hundred frames and you have a map of your codebase: module names, the vendor you integrate with (`com.acme.payment.gateway.AcquirerClient`), which parts of the system talk to which. Thread names are worse, because people put things in them. `pgto-worker-3` is harmless; `sync-tenant-acme-prod` or a thread named after a request path is a customer identifier sitting in a text file.

Two dialects leak a lot more than the classic `jstack` output. An OpenJ9 javacore carries the full command line with its `-D` properties, the complete classpath, local file paths, monitor tables listing your class names, and the loaded-class list. And the JSON dialect of `jcmd Thread.dump_to_file` (JDK 21+) has a `container` field per thread group that holds the `toString()` of the executor or `StructuredTaskScope` owning it, something like `com.acme.batch.LedgerScope@4f2b1a`. It's one of the few places where one of your classes names itself outside a stack frame, and it's easy to miss if you only think in terms of frames.

## Why "mask everything" doesn't work

A thread dump analyzer, any of them, doesn't read your code. It reads structure and public names. The detectors that matter key on things like `java.util.concurrent.ThreadPoolExecutor.getTask` to tell an idle worker from a stuck one, on `http-nio-8080-exec-` and `ForkJoinPool-1-worker-` to group pool threads, on lock addresses to build the deadlock graph, on thread states, and on the `-N` suffix of a pool thread to count how many workers share a prefix. Replace those with opaque tokens and starvation detection, pool grouping and deadlock analysis all go quiet at once.

So the rule that came out of reading the detector code is short: everything the JDK or a known framework put in the dump stays byte for byte, and everything that's yours becomes a token. The list of what stays, the allowlist, is a JSON file in the repository, with the rationale for each family written next to it, so a reviewer can disagree with an entry and rebuild. That list is the whole design; the rest is plumbing.

## How a name becomes a token

Each identifier is hashed with HMAC-SHA256 under a 256-bit key that lives in a local vault file, and the first 40 bits become the token. Same key, same name, same token, in any dump, forever. That property is what keeps multi-dump comparison working after masking: the thread you were watching on Monday has the same token on Tuesday.

Keyed HMAC rather than a plain hash is not a stylistic choice. Java identifiers are guessable. `SHA-256("com.acme.OrderService")` falls to a dictionary attack in seconds, because the attacker can hash candidate names and compare. With a keyed hash, whoever doesn't hold the vault key can't even test a guess. The vault also stores the reverse map, and `tm-anon init --encrypt` seals it with a passphrase (PBKDF2, 600,000 iterations, then AES-256-GCM, both straight from the JDK), so a stolen laptop image is useless without it.

The token grammar has a shape, `p…x…` for a package segment, `C…x…` for a class, `m…x…` for a method, `t…x…` for a thread, and that shape was chosen against the analyzer's own regexes. Each package segment gets its own token so `package.Class.method(` still parses and a flame graph still groups by package. The numeric suffix of a pool thread survives, so `pgto-worker-1` becomes `t426f3xd05a4-1` and the analyzer still sees a pool of N. The `x` in the middle keeps the token from ever looking like a hex address or a request id, which is a real thing one of the detectors checks for.

## The flow, from the point of view of someone who has never run it

You create a vault once per project:

```
$ tm-anon init
Created vault: /home/you/work/tm-anon-vault.json
```

Then you mask the dump. This writes a new file next to the original:

```
$ tm-anon mask payments-prod.txt
masked  payments-prod.txt -> payments-prod.anon.txt
lines:  78 preserved, 10 tokenized, 22 stripped, 0 redacted
verify: PASS - no identifier survived and the structure is intact.
```

The `verify` line is the part I'd point a reviewer at. Before writing anything, `mask` hands its own output to a separate verifier that re-derives every identifier from the masked text instead of trusting the rewriter, and refuses to write the file if anything that isn't a token, an allowlisted name or pure structure survived. A half-masked dump can't leave the machine by accident, because it never gets written.

This is what the same frame looks like on each side:

```
at com.acme.payment.LedgerService.applyEntry(LedgerService.java:95)
at pb536bxc27ec.pc2564xde165.pd98ecx128d7.Cfcbfdx33dfc.m65719x51697(Cfcbfdx33dfc.java:95)
```

JDK frames, lock addresses, thread states, `cpu=` and `tid=` fields and blank lines are untouched, because blank lines are what delimit threads and an analyzer that loses them loses the thread count. Lines the analyzer ignores anyway but that leak names for free, like `Locked ownable synchronizers`, are removed and replaced by a marker so the structure doesn't shift. A line no rule recognizes is redacted, never passed through.

You upload the `.anon.txt`, run the analysis, download the report, and bring the names back locally:

```
$ tm-anon unmask threadmine-report.html
Wrote threadmine-report.unmasked.html
```

`unmask` treats its input as opaque text and rewrites every token it finds, so it works on an HTML report, an export JSON, a CSV, or a paragraph an LLM wrote about your dump. The tokens come back inside prose too, which is why the grammar is deliberately distinctive: "the bottleneck is Cfcbfdx33dfc" turns back into "the bottleneck is LedgerService".

## Does the analysis survive?

That was the question I couldn't answer when I replied on Reddit, so I measured it. I took the dumps in the test corpus (17 at the time), ran each one through the analyzer twice, original and masked, and compared the output: same set of detected problems, same severities, same health score on every one. Two edge cases behave differently and they're written down in the README rather than hidden, for example an application frame that today matches a detection pattern by accident (a fragment without a package, like `Consumer.receive`) stops matching once tokenized. The honest claim is "equivalent analysis", not "byte-identical".

The corpus is also where the tool caught its own worst bug before a user did. JDK 24 changed the text dialect of `jcmd Thread.dump_to_file`: where older JDKs print a lock as `, JDK 24 prints `. The rule "the contents of angle brackets are an address, keep them verbatim" was correct for every dump I'd looked at and was quietly handing over a class name on the newest JDK. It surfaced while building the test corpus for that dialect, and there's now a test that masks every fixture and runs the verifier over the result, so it can't come back unnoticed. I'd rather tell that story than pretend the first version was right.

## What a security team can actually check

"We promise the tool doesn't upload anything" is worth nothing to the people who blocked the upload in the first place. What they can act on is a claim they can verify in an afternoon, so the repository is built around three of those.

The jar has no network code, and a test enforces it: it scans the production sources, the compiled bytecode constant pools and `pom.xml` for `java.net`, socket channels, `javax.net`, `jdk.net` and any HTTP dependency, and fails the build on the first hit. There is one dependency in the whole project, JUnit, at test scope, so there's no transitive supply chain to audit. And the allowlist that decides what stays verbatim is a readable JSON file, not a heuristic buried in code.

The threat model is written as "here is what this does not protect you from". A masked dump still reveals which frameworks you use, the shape of your thread pools and the depth of your package tree, and dumps masked with the same vault are linkable to each other by design. Whoever holds the vault file can reverse everything. This is pseudonymization reversible by its owner, not irreversible anonymization, and the document says so, because the alternative is a security reviewer discovering it on their own.

## Where it stands

Version 0.4.0 handles the whole HotSpot family (`jstack` from JDK 8 to 25, `jcmd Thread.print`, both text and JSON forms of `Thread.dump_to_file`, ThreadMXBean/VisualVM output, virtual threads), OpenJ9 javacore in a strip mode that removes whole sections rather than masking line by line, Azul Zing and GraalVM native-image dumps. Anything it can't classify is refused rather than half-masked. It ships as a jar that needs Java 21, and as native binaries for Linux, macOS and Windows for people who'd rather not have a JVM on the machine holding the dump.

Disclosure, once more and explicitly: I'm the founder of [ThreadMine](https://threadmine.dev/en/analyze), the analyzer this tool was built to feed, and the page describing how the two fit together is [here](https://threadmine.dev/en/resources/thread-dump-anonymizer). The CLI itself is at [github.com/maschiojv/threadmine-anonymizer](https://github.com/maschiojv/threadmine-anonymizer), MIT, analyzer-agnostic. If your policy still says no even to a masked dump, I'd genuinely like to know what it would take; that thread on r/java is where this started, and it's still the best feedback I've had on the problem.
