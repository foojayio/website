---
title: "How to Use Java as a Scripting Language"
slug: "java-for-scripting"
date: "2026-03-23T10:00:19+00:00"
lastmod: "2026-03-23T10:48:35+00:00"
description: "Discover how Java has evolved into a powerful scripting language, eliminating boilerplate and enabling instant execution for automation tasks"
canonical: "https://lomagnette.github.io/posts/javascript-no-not-that-one-modern-automation-with-java/"
authors:
  - "loic-magnette"
image: "java-script-cover-700x467-1.png"
categories:
  - "Java"
  - "Java Core"
  - "JDK 23"
  - "JDK21"
  - "JEPs"
tags:
related_posts:
  - "boxlang-aws-azure-and-google-secrets-manager-module-released"
  - "virtual-thread-pinning-field-guide"
  - "boxlang-1-15-0-released-blazing-fast-strings-runtime-portability-and-much-more"
  - "boxlang-1-14-0-sets-ranges-inner-classes-and-a-runtime-that-talks-back"
frozen: false
---

![](java-script-cover-700x467.png)

## The Scripting Dilemma

If you're like me, as a Java developer, you struggle to remember bash or python syntax for quick scripts.  

You end up "vibe coding" it, then struggle again when you need to adapt it. If only you could write it in Java!

You might say "Java is not for scripting" and "I don't want maven or Gradle".Modern Java has quietly eliminated the traditional barriers that made it unsuitable for scripting.With instant execution, shebang support, and zero-setup automation, Java has evolved into a lean scripting language. Write precise, maintainable code without the "vibe coding" that comes with unfamiliar languages.

In this article, I'll show you how Java became a first-class scripting language. It might just become your new favorite automation tool.

## The Death of Manual Compilation

The first problem if you want to write a Java script is compilation. You probably don't want to run `javac` and manage `.class` files for a simple script. But since Java 11 (via [JEP 330](https://openjdk.org/jeps/330)), you can run source-code programs directly with the `java` launcher.

### Single-File Execution

Let's say you want to rewrite `ls` in Java. You might do something like this:

```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.DecimalFormat;

public class ListFiles {

    static void main(String[] args) throws IOException {
        try (var stream = Files.list(Path.of("."))) {
            stream
                .filter(path -> !path.getFileName().toString().startsWith("."))
                .sorted()
                .forEach(ListFiles::printEntry);
        }
    }

    private static void printEntry(Path path) {
        var isDir = Files.isDirectory(path);
        var name = path.getFileName().toString();
        var display = isDir ? "\u001B[34m" + name + "\u001B[0m" : name;

        System.out.printf(
                "%-12s %-10s %s%n", permissions(path, isDir),
                size(path, isDir),
                display
        );
    }

    private static String size(Path path, boolean isDir) {
        if (isDir) return "-";
        try {
            long bytes = Files.size(path);
            if (bytes == 0) return "0 B";

            var units = new String[]{"B", "KB", "MB", "GB", "TB"};
            int group = (int) (Math.log(bytes) / Math.log(1024));
            return new DecimalFormat("#,##0.#").format(bytes / Math.pow(1024, group)) + " " + units[group];
        } catch (IOException _) {
            return "?";
        }
    }

    private static String permissions(Path path, boolean isDir) {
        try {
            return (isDir ? "d" : "-") + PosixFilePermissions.toString(Files.getPosixFilePermissions(path));
        } catch (Exception _) {
            return (isDir ? "d" : "-") +
                   (Files.isReadable(path) ? "r" : "-") +
                   (Files.isWritable(path) ? "w" : "-") +
                   (Files.isExecutable(path) ? "x" : "-") + "------";
        }
    }
}
```

If you want to run it, no need for `javac`, you can simply do this:

```bash
java ListFiles.java
```

No `javac` needed. No `.class` files cluttering your directory. Just instant execution.

### Multi-File Support

But since **Java 22** (via [JEP 458](https://openjdk.org/jeps/458), you're not limited to single-file programs. You can now write multi-file programs and run them directly. The Java launcher locates and compiles related source files in subdirectories. For instance if you have something like this:

```java
public class Greet {

    public static void main(String[] args) {
        var message = new Message("Hello", "Folks");
        new MessagingService().sendMessage(message);

    }
}
```

```java
record Message(String welcomingWord, String name) {
}
```

```java
class MessagingService {

    public void sendMessage(Message message){
        System.out.println(message.welcomingWord() + " " + message.name());
    }

}
```

This is just an overcomplicated "Hello World!". But it shows that multi-file programs can run directly:

```bash
java Greet.java  # Automatically finds and compiles dependencies
```

This feature is especially useful for complex scripts that require multiple classes. It's also great for learners who don't want to deal with project setup.

Behind the scenes, the launcher invokes the compiler automatically and stores the compiled result in memory. There's no build cruft, no intermediate files, just pure execution. For scripting purposes, Java now feels as immediate as Python or Ruby.

## Why so much ceremony?

One key thing for a good scripting language is to eliminate boilerplate. Let's be honest, Java can be a bit verbose. The most infamous example is the `public static void main(String[] args)` signature. If you just want a simple script, that's a lot of text that doesn't serve much purpose. Printing "Hello World!" requires at least 5 lines, mostly boilerplate.

```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

### The Java Evolution

Java 25 builds upon previews from Java 21 and 22. It introduces several features that directly tackle verbosity.  
**Compact Source Files** and **Instance Main Methods** eliminate the need for boilerplate signatures.

* Compact Source Files: Write methods and fields at the top level, without a class declaration.
* Instance Main Methods: Simplify `public static void main(String[] args)` down to just `void main()`.

With these features, a "Hello World" can become as simple as this:

```java
void main() {
    System.out.println("Java");
}
```

The JVM automatically chooses the starting point, prioritizing instance `main()` methods when available. This makes your scripts feel more natural and less like traditional enterprise Java.

### Easier console interaction

In the past interacting with the console was a bit cumbersome. First everytime you want to print something to the console, you end up typing `System.out.println("...")`. But it gets worse when you want to read input. You need to go through the gymnastics of using `BufferedReader` or `Scanner`. So you quickly end up with code like this:

```java
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

void main() {
    System.out.println("Please enter your name:");
    String name = "";
    try {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        name = reader.readLine();
    } catch (IOException ioe) {
        ioe.printStackTrace();
    }
    System.out.println("Hello, " + name);
}
```

Thanks to `Compact Source Files`, you can now use the `IO` class instead:

```java
void main() {
    String name = IO.readln("Please enter your name:");
    IO.print("Hello, ");
    IO.println(name);
}
```

The IO class lives in `java.lang`, so it's implicitly imported by every source file.

If you want to learn about all this, I recommend reading the [JEP 512](https://openjdk.org/jeps/512).

## Java as a Native Script: Shebang Support

A defining feature of any script is running it directly, like `./myscript`, without calling an interpreter. Many scripting languages achieve this with a "shebang" line (`#!`). This line at the start of a file tells the OS which program to use.

Since **Java 11** (via [JEP 330](https://openjdk.org/jeps/330)), the `java` launcher supports this convention.

Let's refactor our `ListFiles` example into a proper, executable script using the modern Java features we've discussed:

```java
#!/usr/bin/java --source 25

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.DecimalFormat;

void main(String[] args) throws IOException {
    var dir = Path.of(args.length > 0 ? args[0] : ".");
    if (!Files.exists(dir)) {
        IO.println("Error: Directory '" + dir + "' does not exist.");
        System.exit(1);
    }
    if (!Files.isDirectory(dir)) {
        IO.println("Error: '" + dir + "' is not a directory.");
        System.exit(1);
    }

    try (var stream = Files.list(dir)) {
        stream
            .filter(path -> !path.getFileName().toString().startsWith("."))
            .sorted()
            .forEach(this::printEntry);
    }
}

void printEntry(Path path) {
    var isDir = Files.isDirectory(path);
    var name = path.getFileName().toString();
    var display = isDir ? "\u001B[34m" + name + "\u001B[0m" : name;

    IO.println("%-12s %-10s %s".formatted(
        permissions(path, isDir),
        size(path, isDir),
        display
    ));
}

String size(Path path, boolean isDir) {
    if (isDir) return "-";
    try {
        long bytes = Files.size(path);
        if (bytes == 0) return "0 B";

        var units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int group = (int) (Math.log(bytes) / Math.log(1024));
        return new DecimalFormat("#,##0.#").format(bytes / Math.pow(1024, group)) + " " + units[group];
    } catch (IOException _) {
        return "?";
    }
}

String permissions(Path path, boolean isDir) {
    try {
        return (isDir ? "d" : "-") + PosixFilePermissions.toString(Files.getPosixFilePermissions(path));
    } catch (Exception _) {
        return (isDir ? "d" : "-") +
               (Files.isReadable(path) ? "r" : "-") +
               (Files.isWritable(path) ? "w" : "-") +
               (Files.isExecutable(path) ? "x" : "-") + "------";
    }
}
```

Save the file (e.g., as `ls`) without a `.java` extension and make it executable:

```bash
chmod +x ls
./ls
```

Your Java code is now a first-class CLI command, no different from a Bash or Python script.

**Portability Tip:** Move your script to a directory in your `PATH`, like `/usr/local/bin/`:

```bash
sudo mv ls /usr/local/bin/
ls  # Run from any directory
```

Suddenly, Java feels like a native scripting language.

## Advanced Automation with JBang

These built-in features have made Java a capable scripting language. But [JBang](https://www.jbang.dev/) takes it to the next level by handling setup overhead and enabling advanced features.

### What is JBang?

JBang lets you create, edit, and run self-contained Java programs with unprecedented ease.With JBang, your script can fetch dependencies without you having to dive into Maven or Gradle. It runs on any platform, including Docker and Github Actions. Best of all, JBang automatically downloads required Java versions if they're missing.

You can also use JBang to run any JAR file, whether local or online (via Maven Central).

Running a JBang script is as easy as:

```bash
jbang MyScript.java
```

### Dependency Management

This is where JBang truly shines. Declare dependencies directly in your file using special comments. No Maven or Gradle needed.

Notice the `///` shebang line, which is specific to JBang and allows the script to be executed directly.

```java
///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS com.google.code.gson:gson:2.10.1
//DEPS org.apache.commons:commons-lang3:3.14.0

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

void main() {
    Gson gson = new Gson();
    var person = new Person("John", 30);
    String json = gson.toJson(person);
    IO.println(StringUtils.capitalize(json));
}

record Person(String name, int age) {}
```

JBang automatically downloads and manages these dependencies. No Maven. No Gradle. Just declare and use.

### Awesome Features

JBang offers a lot of advanced features:

**IDE Integration**: JBang can install VSCodium, generate a project structure, and open the script in your IDE:

```bash
jbang edit MyScript.java  # Opens in your IDE with full autocomplete
```

**Native Binaries** : It supports the generation of native image binaries using [GraalVM](https://www.graalvm.org/) for near-instant startup:

```bash
jbang export native MyScript.java
./MyScript  # Blazing fast native execution
```

Be careful with native images, especially regarding reflection. More on this [here](https://www.jbang.dev/documentation/jbang/latest/native-images.html)

**Templates**: JBang comes with a set of templates to help you quickly bootstrap your scripts.

```bash
# Create a CLI app
jbang init --template=cli myapp.java

# Create a web server
jbang init --template=qcli webapp.java

# Create a JavaFX app
jbang init --template=javafx gui.java
```

## Elevating Your Scripts: CLI Richness with Picocli

Simple scripts are great place to start. However, you'll often want options, positional parameters, and help menus for a robust CLI experience. That's where [Picocli](https://picocli.info/) comes in.

### Pro-Grade Tools

Picocli integrates perfectly with JBang for ANSI-colored help messages and strongly-typed argument parsing. Build professional CLIs with auto-generated help, version info, type checking, and beautiful error messages.

```java
///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS info.picocli:picocli:4.7.7
//DEPS info.picocli:picocli-codegen:4.7.7
//NATIVE_OPTIONS --no-fallback -H:+ReportExceptionStackTraces

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "greet", mixinStandardHelpOptions = true, version = "1.0",
         description = "Greets users with style")
class Greet implements Runnable {
    @Option(names = {"-c", "--count"}, description = "Number of greetings")
    int count = 1;

    @Parameters(description = "Name(s) to greet")
    String[] names;

    public void run() {
        for (int i = 0; i < count; i++) {
            for (String name : names) {
                IO.println("Hello, " + name + "!");
            }
        }
    }
}

void main(String[] args) {
    new CommandLine(new Greet()).execute(args);
}
```

Run it:

```bash
jbang greet.java --help
jbang greet.java -c 3 Alice Bob
```

### Zero-Code CLI Experience

Picocli handles the complexity:

* Automatic help generation with `--help`
* Version display with `--version`
* Type conversion and validation
* ANSI colors for better readability
* Subcommands and command hierarchies

Your scripts can rival professionally built CLI tools.

## Java's New Era

I hope you now see that Java is not just about enterprise complexity and verbose boilerplate. You can use it to build powerful automation scripts with ease.

### Why This Matters

**Maintainability**: Java scripts offer types and structure that Bash often lacks. Six months later, strong typing and IDE support make understanding and modifying code much easier.

**Familiarity**: You already know Java. Why context-switch to another language for automation when you can use the expertise you've already built?

**Power**: The entire Java ecosystem is at your fingertips. Millions of libraries and battle-tested frameworks, all in a simple script.

**Performance**: GraalVM native images let your scripts compile to native binaries with instant startup, rivaling Go or Rust.

### The Bottom Line

With shebang support, JBang, and instance main methods, Java is now a lean automation machine. The ceremony is gone. The friction is eliminated. What remains is a powerful, typed, maintainable scripting language that happens to be Java.

Next time you need a quick automation script, don't reach for Bash or Python out of habit. Give Java a try. You might be surprised at how good it feels. Your IDE actually helps you, types catch bugs before runtime, and precision replaces "vibe coding".

Java scripting isn't the future. It's already here. And it's spectacular.
