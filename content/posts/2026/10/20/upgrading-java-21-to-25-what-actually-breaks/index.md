---
title: "Upgrading from Java 21 to 25: What Actually Breaks"
date: "2026-10-20"
description: "Seven changes between Java 21 and 25 that break builds, startup or behaviour, each reproduced on both JDKs, and how to find them before you upgrade."
authors:
  - "gerard-marimon"
image: "preview-1600x900.jpg"
categories:
  - "OpenJDK Migration"
  - "JEPs"
related_posts: []
---

Most "what's new in Java 25" articles are about what you gain: Scoped Values, flexible constructor bodies, compact source files, Stream Gatherers. This one is about the other side of an LTS-to-LTS upgrade, the things that stop working.

Four years of releases sit between Java 21 and Java 25, and some of what was deprecated in 21 has been removed by 25. Some of it fails loudly, at startup. Some of it fails silently, which is worse: the build is green, the app starts, and the bug shows up a week later.

Every example below was compiled and run on JDK 21.0.7 and JDK 25.0.3, and the output you see is what the two JVMs printed. I only shortened file paths and removed log timestamps. Each section links to the JEP or release note it comes from.

## 1. Your annotation processors stop running, and nothing tells you

This is the one most likely to hit a typical Maven project, because it fails without an error.

Up to JDK 22, `javac` looked for annotation processors on the class path and ran whatever it found. Since JDK 23 it [no longer does that by default](https://www.oracle.com/java/technologies/javase/23-relnote-issues.html): processors only run if you configure them explicitly (`-processor`, `--processor-path`, `--processor-module-path`) or ask for it with `-proc:full`.

To see it, I built a processor that does nothing except print a note, packaged it as a JAR with its `META-INF/services` entry, and compiled a plain class with that JAR on the class path:

```bash
javac -cp hello-processor.jar -d out App.java
```

On JDK 21, the processor runs and `javac` warns you that this is about to change:

```
Note: Annotation processing is enabled because one or more processors were found
  on the class path. A future release of javac may disable annotation processing
  unless at least one processor is specified by name (-processor), or a search
  path is specified (--processor-path, --processor-module-path), or annotation
  processing is enabled explicitly (-proc:only, -proc:full).
  Use -Xlint:-options to suppress this message.
  Use -proc:none to disable annotation processing.
Note: HelloProcessor ran
```

On JDK 25, the same command prints nothing at all and exits with status 0. The processor simply didn't run.

With Lombok, that means "cannot find symbol" errors for every getter it was supposed to generate, so at least you notice. With a processor that generates something optional, such as metadata files, a service registry or mapper implementations picked up through reflection, you may not notice until runtime.

To fix it, declare your processors. In Maven, list them under `annotationProcessorPaths` in the `maven-compiler-plugin` configuration, or set `<proc>full</proc>` (user property `maven.compiler.proc`) to restore the old behaviour on a plugin version that supports it. Gradle has kept processors on their own `annotationProcessor` configuration [since Gradle 5.0](https://docs.gradle.org/current/userguide/upgrading_version_4.html), so Gradle builds are normally unaffected.

If your JDK 21 build log contains that "Annotation processing is enabled because…" note, you're affected.

## 2. The Security Manager is gone, and one flag stops the JVM

The Security Manager was deprecated for removal in Java 17. In JDK 24, [JEP 486](https://openjdk.org/jeps/486) disabled it permanently: the API is still there, but it can't be turned on.

Take this class:

```java
public class Sm {
    public static void main(String[] args) {
        System.out.println("security manager: " + System.getSecurityManager());
        try {
            System.setSecurityManager(new SecurityManager());
            System.out.println("installed: " + System.getSecurityManager());
        } catch (UnsupportedOperationException e) {
            System.out.println(e);
        }
    }
}
```

On JDK 21 with `-Djava.security.manager=allow`, it installs the Security Manager (with a deprecation warning). On JDK 25, the JVM never gets as far as `main`:

```
$ java -Djava.security.manager=allow Sm
Error occurred during initialization of VM
java.lang.Error: A command line option has attempted to allow or enable the Security Manager. Enabling a Security Manager is not supported.
	at java.lang.System.initPhase3(java.base@25.0.3/System.java:1970)
```

Without the flag, `setSecurityManager` now throws `UnsupportedOperationException: Setting a Security Manager is not supported`.

So search your start scripts, Dockerfiles, systemd units and IDE run configurations for `java.security.manager`. Only `-Djava.security.manager=disallow` is still accepted.

The part that catches people who never used a Security Manager is `Subject.getSubject(AccessControlContext)`. It is still called by older authentication code, Kerberos integrations and some Hadoop-era libraries:

```java
System.out.println(Subject.getSubject(AccessController.getContext()));
```

On JDK 21 this prints `null` when there is no subject. On JDK 25:

```
Exception in thread "main" java.lang.UnsupportedOperationException: getSubject is not supported
	at java.base/javax.security.auth.Subject.getSubject(Subject.java:277)
```

The replacements, `Subject.current()` and `Subject.callAs(...)`, have been available since Java 18, so you can migrate that code while you're still on 21.

## 3. Removed `Thread` methods: your `catch` won't help

`Thread.suspend()`, `Thread.resume()`, `ThreadGroup.suspend()`, `ThreadGroup.resume()` and `ThreadGroup.stop()` [were removed in JDK 23](https://www.oracle.com/java/technologies/javase/23-relnote-issues.html) (and `Thread.countStackFrames()` in JDK 22). On Java 21 they already did nothing useful: they threw `UnsupportedOperationException` unconditionally. So some code ended up looking like this:

```java
Thread worker = new Thread(() -> {});
try {
    worker.suspend();
} catch (UnsupportedOperationException e) {
    System.out.println("caught: " + e);
}
```

Compile it with JDK 21 and run it on both:

```
# JDK 21
caught: java.lang.UnsupportedOperationException

# JDK 25, same class file
Exception in thread "main" java.lang.NoSuchMethodError: 'void java.lang.Thread.suspend()'
	at Legacy.main(Legacy.java:5)
```

The method no longer exists, so the JVM throws `NoSuchMethodError`, which is an `Error`, not the exception the code was ready for. Recompiling your own code on 25 catches this (`cannot find symbol`), but recompiling doesn't help with a dependency JAR built years ago. Section 8 shows how to find those.

## 4. `java.locale.providers=COMPAT` is ignored, with one log line

Since JDK 9, locale data comes from the Unicode CLDR. The old JRE data stayed available as `COMPAT`, and a lot of teams turned it on when CLDR changed their date formats, especially after CLDR 42 (JDK 20) put a narrow no-break space (U+202F) before "AM/PM" in English time formats, which broke parsing of strings typed with a normal space.

[JDK 23 removed the COMPAT data](https://www.oracle.com/java/technologies/javase/23-relnote-issues.html). The flag doesn't fail, it just logs a line and is ignored:

```java
var fmt = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(Locale.US);
String formatted = fmt.format(LocalDateTime.of(2026, 10, 8, 14, 30));
System.out.println("format -> " + formatted.replace(" ", "<U+202F>"));
System.out.println("parse  -> " + LocalDateTime.parse("Oct 8, 2026 2:30:00 PM", fmt));
```

Run with `-Djava.locale.providers=COMPAT,CLDR`:

```
# JDK 21
WARNING: COMPAT locale provider will be removed in a future release
format -> Oct 8, 2026 2:30:00 PM
parse  -> 2026-10-08T14:30

# JDK 25
INFO: Invalid locale provider adapter "COMPAT" ignored.
WARNING: COMPAT locale provider has been removed
format -> Oct 8, 2026, 2:30:00<U+202F>PM
parse  -> DateTimeParseException: Text 'Oct 8, 2026 2:30:00 PM' could not be parsed at index 11
```

With the same flag and the same code, JDK 25 formats the date with an extra comma and an invisible character before `PM`, and the parse fails. The same applies to number formats. For `de-CH`, the grouping separator in my run was `'` with COMPAT and `’` (U+2019) with CLDR.

Don't rely on localized formats for anything a machine reads back. Use an explicit pattern (`DateTimeFormatter.ofPattern("MMM d, yyyy h:mm:ss a", Locale.US)`) or ISO-8601 for data you store, exchange or parse. If you need the old patterns, the [JEP 252](https://openjdk.org/jeps/252) page was updated with migration advice.

For the space before `PM` alone, JDK 23 also added [loose matching of spaces in lenient parsing](https://bugs.openjdk.org/browse/JDK-8324665). `java.time` parsers are strict by default, so you have to ask for it:

```java
var lenient = new DateTimeFormatterBuilder()
        .parseLenient()
        .append(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))
        .toFormatter(Locale.US);

LocalDateTime.parse("Oct 8, 2026, 2:30:00 PM", lenient); // plain space: 2026-10-08T14:30
```

With the strict formatter, that same string fails at index 20 on JDK 25. Lenient parsing doesn't bring back the old pattern, though: the comma after the year is still required.

## 5. New warnings from `sun.misc.Unsafe` and JNI

These don't break anything yet, but they change what your logs look like, and they say what will break next.

**`sun.misc.Unsafe` memory access.** Its memory-access methods were deprecated for removal in JDK 23 ([JEP 471](https://openjdk.org/jeps/471)), and since JDK 24 the first call prints a warning ([JEP 498](https://openjdk.org/jeps/498)). A small program that allocates 8 bytes off-heap, writes a `long` and reads it back:

```
# JDK 21
read back: 42

# JDK 25
WARNING: A terminally deprecated method in sun.misc.Unsafe has been called
WARNING: sun.misc.Unsafe::allocateMemory has been called by OffHeap (file:/.../)
WARNING: Please consider reporting this to the maintainers of class OffHeap
WARNING: sun.misc.Unsafe::allocateMemory will be removed in a future release
read back: 42
```

In real applications the caller is usually a library (serialization, off-heap caches, older networking code), and the warning tells you which one. JEP 498 plans for these calls to throw an exception in a later release, so check whether a newer version of that library has moved to `VarHandle` or the FFM API's `MemorySegment`.

**JNI.** Since JDK 24 ([JEP 472](https://openjdk.org/jeps/472)), loading a native library or binding a `native` method without having enabled native access prints a warning too:

```
WARNING: A restricted method in java.lang.System has been called
WARNING: java.lang.System::loadLibrary has been called by Native in an unnamed module (file:/.../)
WARNING: Use --enable-native-access=ALL-UNNAMED to avoid a warning for callers in this module
WARNING: Restricted methods will be blocked in a future release unless native access is enabled
```

If you depend on JNI (database drivers with native parts, compression, crypto, platform integration), add `--enable-native-access=ALL-UNNAMED` (or name your modules), or put `Enable-Native-Access: ALL-UNNAMED` in the manifest of an executable JAR.

My advice is to run your tests on 25 with both switches set to their strictest value, so the CI fails now rather than the day the default changes:

```
--sun-misc-unsafe-memory-access=deny --illegal-native-access=deny
```

With those two switches, the same programs fail immediately with `UnsupportedOperationException: allocateMemory` and `IllegalCallerException: Illegal native access from an unnamed module`.

## 6. Launch flags that now stop the JVM, or are quietly ignored

JVM options accumulate in scripts for years. These are the ones that behave differently on 25, as I tested them:

| Option | JDK 21 | JDK 25 |
|---|---|---|
| `-Xfuture` | deprecation warning | `Unrecognized option`, JVM doesn't start |
| `-Xnoagent` | accepted | `Unrecognized option`, JVM doesn't start |
| `-XX:-ZGenerational` | non-generational ZGC | `Ignoring option ZGenerational; support was removed in 24.0` |
| `-Xdebug` | accepted | deprecation warning |
| `-noclassgc` | accepted | deprecation warning |

`-t`, `-tm`, `-checksource`, `-cs` and `-noasyncgc` were [removed in JDK 24](https://www.oracle.com/java/technologies/javase/24-relnote-issues.html) along with `-Xfuture`, and they fail the same way. If you used `-Xfuture`, the release note suggests `-Xverify:all` instead.

The ZGC row needs a closer look, because nothing fails. On Java 21, `-XX:+UseZGC` on its own gives you the legacy single-generation mode. Generational ZGC became the default in JDK 23 ([JEP 474](https://openjdk.org/jeps/474)) and the single-generation mode was removed in JDK 24 ([JEP 490](https://openjdk.org/jeps/490)). The GC log shows it:

```
# JDK 21, -XX:+UseZGC
[gc,init] Initializing The Z Garbage Collector
[gc,init] Using legacy single-generation mode

# JDK 25, -XX:+UseZGC
[gc,init] Initializing The Z Garbage Collector
[gc     ] Using The Z Garbage Collector
```

JEP 439, which introduced it, expects generational ZGC to be "a better solution for most use cases", but it behaves differently. If you sized heaps or tuned ZGC on 21, re-check memory use and pause times under load after the upgrade.

## 7. Small changes with sharp edges

A few more from the release notes that are easy to miss.

**`File.delete()` on Windows no longer deletes read-only files** (JDK 25, [JDK-8355954](https://bugs.openjdk.org/browse/JDK-8355954)). It now returns `false` instead of clearing the read-only attribute first:

```java
File f = Files.createTempFile("report", ".txt").toFile();
f.setReadOnly();
System.out.println("deleted: " + f.delete() + ", still exists: " + f.exists());
```

```
# JDK 21 on Windows
deleted: true, still exists: false

# JDK 25 on Windows
deleted: false, still exists: true
```

Cleanup code that ignores the return value of `delete()`, which is most cleanup code, will leave files behind. `-Djdk.io.File.allowDeleteReadOnlyFiles=true` restores the old behaviour while you fix the code (clear the attribute yourself, or use `Files.delete`, which throws instead of returning `false`).

**TLS defaults are stricter.** JDK 24 disabled the `TLS_RSA_*` cipher suites, and JDK 25 disabled SHA-1 in TLS 1.2 handshake signatures. Connections to old internal servers, appliances or legacy partner endpoints that only offer those can now fail with `SSLHandshakeException`. Test those connections before the upgrade. You can re-enable the algorithms in `java.security`, at your own risk.

## 8. Finding all of this before production

A checklist, in the order I'd run it:

1. **Look at your JDK 21 build output.** The annotation-processing note from section 1 is already there if you're affected. Also compile with `-Xlint:deprecation,removal` and read the `[removal]` warnings.
2. **Run `jdeprscan --for-removal` from your current JDK 21** over your application and its dependency JARs. It lists everything you use that is deprecated for removal, including `Thread.suspend`. In my test, the `jdeprscan` from JDK 25 did not report `Thread.suspend`, even with `--release 21`, so run the JDK 21 one before you switch.

   Here it is on the classes from this article, compiled with JDK 21 into `classes/`:

   ```
   $ jdeprscan --for-removal --class-path classes classes
   Directory classes:
   class Legacy uses deprecated method java/lang/Thread::suspend()V (forRemoval=true)
   class Sm uses deprecated class java/lang/SecurityManager (forRemoval=true)
   class Sm uses deprecated method java/lang/System::getSecurityManager()Ljava/lang/SecurityManager; (forRemoval=true)
   class Sm uses deprecated method java/lang/System::setSecurityManager(Ljava/lang/SecurityManager;)V (forRemoval=true)
   class Subj uses deprecated class java/security/AccessController (forRemoval=true)
   class Subj uses deprecated method javax/security/auth/Subject::getSubject(Ljava/security/AccessControlContext;)Ljavax/security/auth/Subject; (forRemoval=true)
   ```

   For a real project, point `--class-path` at your dependencies and pass your JAR or classes directory as the last argument.

3. **Run `jdeps --jdk-internals`** to find who uses `sun.misc.Unsafe` and other internal APIs.
4. **Grep your launch configuration** (scripts, Dockerfiles, Helm charts, `JAVA_TOOL_OPTIONS`, `JDK_JAVA_OPTIONS`) for `java.security.manager`, `java.locale.providers`, `-Xfuture`, `-Xnoagent` and `ZGenerational`.
5. **Run the full test suite on 25** with `--sun-misc-unsafe-memory-access=deny --illegal-native-access=deny`, and read the startup log of a real deployment for `WARNING:` lines you haven't seen before.
6. **Read the "Removed Features and Options" and "Other Notes" sections** of the [22](https://www.oracle.com/java/technologies/javase/22-relnote-issues.html), [23](https://www.oracle.com/java/technologies/javase/23-relnote-issues.html), [24](https://www.oracle.com/java/technologies/javase/24-relnote-issues.html) and [25](https://www.oracle.com/java/technologies/javase/25-relnote-issues.html) release notes. They're long, but the breaking changes are a small part of them.

If you only have time for three, look first for the annotation processors, the COMPAT flag and the read-only files. None of the three stops the build or the app: the processors just don't run, COMPAT leaves a single log line at startup, and `delete()` returns `false`.
