---
title: "Live (re)compile, (re)load, (re)execute Java code in 100 LoC"
slug: "live-recompile-reload-reexecute-java-code-in-100-loc"
date: "2023-11-15T16:22:05+00:00"
lastmod: "2024-01-09T12:58:25+00:00"
description: "A way to automatically (re)compile, (re)load and (re)execute code on file changed!"
authors:
  - "anthony-goubard"
image: "/images/posts/2023/11/live-recompile-reload-reexecute-java-code-in-100-loc/hot-reload-code-ant-commander-pro-1024x391-1.png"
categories:
  - "Java"
  - "JShell"
tags:
related_posts:
  - "java-21-jep-445-unnamed-classes-and-instance-main-methods"
  - "class-loader-hierarchies"
  - "dissection-of-joeffice-open-source-office-suite-in-java"
enlighterjs: true
frozen: false
---

Writing your first program in Java can have quite a learning curve.  

Java has been trying in recent releases to make it easier to write the [first simple program](https://openjdk.org/projects/amber/design-notes/on-ramp).  

Here are some changes that simplify your first Java program:

* [JEP 222](https://openjdk.org/jeps/222): jshell: The Java Shell (Read-Eval-Print Loop) in JDK 9
* [JEP 330](https://openjdk.org/jeps/330): Launch Single-File Source-Code Programs in JDK 11
* [JEP 445](https://openjdk.org/jeps/445): Unnamed Classes and Instance Main Methods in preview in JDK 21
* [JEP 458](https://openjdk.org/jeps/458): Launch Multi-File Source-Code Programs. Not released yet.

In this article, we'll write a program (**The Reloader**) to easily play with Java code without the need to know how to compile or run the code or print the result.

The code will be automatically (re)compiled, (re)loaded, (re)executed and (re)printed to the output when the .java file is saved.  
![](/images/posts/2023/11/live-recompile-reload-reexecute-java-code-in-100-loc/hot-reload-code-ant-commander-pro-1024x391.png) On the left side, the Java file that I edited and saved. On the right side, the code re-executed.

|------------------------------|------------|--------------|--------------------------------|--------------|
|                              | **JShell** | **Java 11+** | **Java 21 (--enable-preview)** | **Reloader** |
| Execute java files           | ❌(.jsh)    | ✅            | ✅                              | ✅            |
| No class needed              | ✅          | ❌            | **✅**                          | ❌            |
| No complex main method       | **✅**      | ❌            | **✅**                          | **✅**        |
| No System.out.println needed | ❌          | ❌            | ❌                              | **✅**        |
| Re-execute on file changed   | ❌          | ❌            | ❌                              | **✅**        |

What we'll need:

1. Source code to play with
2. Code to detect when the source code has been modified
3. A compiler to recompile the code when the file has changed
4. A way to reload the generated class file after the compilation
5. A method to call when the new code is reloaded

The source code {#h2-0-the-source-code}
---------------------------------------

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PlayWithNumbers implements Supplier&lt;String&gt; {
    @Override
    public String get() {
        IntStream numbers = new Random().ints(20, 0, 100);
        return numbers.sorted().mapToObj(Integer::toString).collect(Collectors.joining(", "));
    }
}</pre>

The Reloader {#h2-1-the-reloader}
---------------------------------

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Live Reload and execution of another java file.
 * The other Java file needs to implements the Supplier interface.
 * Public domain code.
 *
 * @author Anthony Goubard - japplis.com
 */
public class Reloader {
    private String fileName;

    public Reloader(String fileName) {
        this.fileName = fileName;
        try {
            monitorForReload();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void monitorForReload() throws IOException {
        Path file = Path.of(fileName);
        if (Files.notExists(file)) throw new FileNotFoundException(file + " not found");
        if (!file.toString().endsWith(".java")) throw new IllegalArgumentException(file + " is not a .java file");
        Runnable compileAndRun = () -&gt; { // statement executed each time the file is saved
            try {
                boolean compiled = compile(file);
                if (compiled) execute(file);
            } catch (Exception ex) {
                System.err.println(ex.getClass().getSimpleName() + ": " + ex.getMessage());
            }
        };
        compileAndRun.run(); // First execution at launch
        monitorFile(file, compileAndRun);
    }

    private boolean compile(Path file) throws IOException, InterruptedException {
        String javaHome = System.getenv("JAVA_HOME");
        String javaCompiler = javaHome + File.separator + "bin" + File.separator + "javac";
        Process compilation = Runtime.getRuntime().exec(new String[] {javaCompiler, file.toString()});
        compilation.getErrorStream().transferTo(System.err); // Only show compilation errors
        compilation.waitFor(1, TimeUnit.MINUTES); // Wait for max 1 minute for the compilation
        boolean compiled = compilation.exitValue() == 0;
        if (!compiled) System.err.println("Compilation failed");
        return compiled;
    }

    private void execute(Path file) throws Exception {
        // Execution is done in a separate class loader that doesn't use the current class loader as parent
        URL classpath = file.toAbsolutePath().getParent().toUri().toURL();
        URLClassLoader loader = new URLClassLoader(new URL[] { classpath }, ClassLoader.getPlatformClassLoader());
        String supplierClassName = file.toString().substring(file.toString().lastIndexOf(File.separator) + 1, file.toString().lastIndexOf("."));
        // Create a new instance of the supplier with the class loader and call the get method
        Class&lt;?&gt; stringSupplierClass = Class.forName(supplierClassName, true, loader);
        Object stringSupplier = stringSupplierClass.getDeclaredConstructor().newInstance();
        if (stringSupplier instanceof Supplier) {
            Object newResult = ((Supplier) stringSupplier).get();
            System.out.println("&gt; " + newResult);
        }
    }

    public static void monitorFile(Path file, Runnable onFileChanged) throws IOException {
        // Using WatchService was more code and less reliable
        Runnable monitor = () -&gt; {
            try {
                long lastModified = Files.getLastModifiedTime(file).to(TimeUnit.SECONDS);
                while (Files.exists(file)) {
                    long newLastModified = Files.getLastModifiedTime(file).to(TimeUnit.SECONDS);
                    if (lastModified != newLastModified) {
                        lastModified = newLastModified;
                        onFileChanged.run();
                    }
                    Thread.sleep(1_000);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };
        Thread monitorThread = new Thread(monitor, file.toString());
        monitorThread.start();
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Missing Java file to execute argument.");
            System.exit(-1);
        }
        new Reloader(args[0]);
    }
}</pre>

Now start it with ***java Reloader.java PlayWithNumbers.java*** and edit the *PlayWithNumbers.java* file as you wish.  
[![](/images/posts/2023/11/live-recompile-reload-reexecute-java-code-in-100-loc/hot-reload-code-netbeans2-1024x576.png)](/images/posts/2023/11/live-recompile-reload-reexecute-java-code-in-100-loc/hot-reload-code-netbeans2.png) Playing with numbers and stream.

Going further {#h2-2-going-further}
-----------------------------------

In this example, we created a minimal version (100 lines of code) that could easily be extended with the following features:

* Provide multiple java files in the arguments
* Provide a directory instead of a Java file
* Detect if there is a *pom.xml* , *gradle.properties* or *build.xml* and build with Apache Maven, Gradle or Apache Ant instead of javac
* Provide external libraries to the `URLClassLoader`
* Detect the return type of the `Supplier` (`String`, `List`, `Array`, `JComponent`, ...) and show the result accordingly
* Open a JShell file (*.jsh*), create a Java file from it and execute it
* Use [JBang](https://www.jbang.dev/) so that installing and running Java get even easier

Some of these ideas, I've implemented in my [Applet Runner IDE plug-in](https://www.japplis.com/applet-runner/) where I recently added support for local java files for URLs.
