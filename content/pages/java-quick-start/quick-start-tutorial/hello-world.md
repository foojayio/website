---
title: "Getting Started with Java - Hello World"
linkTitle: "Hello World!"
description: "Write and run your first Java program with a simple Hello World example, including the Java 25 shorthand syntax and the classic class-based approach."
canonical: "https://foojay.io/java-quick-start/quick-start-tutorial/hello-world/"
url: "/java-quick-start/quick-start-tutorial/hello-world/"
jdoodle: true
aliases:
  - "/java-quick-start/quick-start-tutorial/hello-world/"
frozen: false
weight: 2
---

{{< youtube 0w0ddtnMCUk >}}

Traditionally, we start our first experiment with a "Hello World" application.

Create a new file, named "HelloWorld.java" and add the following code:

<div data-pym-src="https://www.jdoodle.com/plugin" data-language="java" data-version-index="6" data-libs="mavenlib1, mavenlib2">
 public class HelloWorld { public static void main (String[] args) { String txt = "Hello World"; System.out.println(txt); } }
</div>

1. **Public Class.** Java requires us to "package" our code in a public class. By convention, this has the same name as the file, which you see in this first line. All our code starts after this line, after which we finish the class with a closing bracket `}`.
2. **Entry Point.** A Java application also needs an "entry point", the main class which is started and can call all other methods, that's the second line, and again we need to close this method with a bracket `}`.
3. **Class Body.** Within the main method, any code can be added. In this case, we create a String variable `txt` that holds "Hello World". Notice each line needs to end with a `;`.
4. **Indentation.** As you may notice, the indentation (empty space on the lines), is used to make the code easy to read as you can link the line that starts a block, with the location of the closing bracket. This is not required in Java but helps you as a developer.

`System.out.println(txt)`is similar to `console.log` in JavaScript or `print` in Python.

Save this file, and now we can execute it in the terminal. We need to start Java with the name of the file we just created as an argument:

```
$ java HelloWorld.java

Hello World
```

And there it is... our first working Java code!

In **Java 25** some new features got introduced that make it easier to have your first Java code up-and-running. The same example from above, can now also be written as:

```
// Java 25 style
void main() {
    IO.println("Hello World");
}
```
