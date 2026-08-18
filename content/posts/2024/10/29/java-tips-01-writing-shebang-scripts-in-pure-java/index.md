---
title: "Java Tips # 01 -  Writing Shebang Scripts in Pure Java"
slug: "java-tips-01-writing-shebang-scripts-in-pure-java"
date: "2024-10-29T17:06:37+00:00"
lastmod: "2024-10-30T15:12:43+00:00"
description: "Learn how to write a Java CLI shebang script and run it from the terminal, leveraging Java 11 and beyond to create simple, effective command-line tools for developers."
authors:
  - "bazlur-rahman"
image: "Bazlur_Rahman_a_Swiss_Army_Knife_with_the_word_Optional_on_it_a_fbcd5137-6b73-4ed7-9c61-090471b880f4.png"
categories:
  - "Java"
tags:
related_posts:
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "5-things-you-probably-didnt-know-about-java-concurrency"
  - "a-minor-but-useful-refactoring-technique-that-would-reduce-your-code-footprint-part-1"
  - "boxlang-ai-3-2-0-image-generation-web-search-fluent-audio-agent-registry-mcp-observability"
frozen: false
---

**Did you know you can write a CLI script in Java just as easily as you would in a bash script, and run it directly from the shell?**

This is commonly called a shebang script, though we are mostly familiar with writing them in bash. Bash scripts are great, but they can be obscure to developers who aren't familiar with the syntax. As a Java developer, you'd likely prefer to get things done the Java way. Well, since Java 11, you can do exactly that!

I'll assume Java is already installed on your machine. To confirm, open your terminal and run:

`java --version`  

You should see something like this:

```
java --version
java 21.0.1 2023-10-17 LTS
Java(TM) SE Runtime Environment (build 21.0.1+12-LTS-29)
Java HotSpot(TM) 64-Bit Server VM (build 21.0.1+12-LTS-29, mixed mode, sharing)
```

If you don't see a similar output, it means Java isn't installed. Sorry to make you uncomfortable, but you'll need to install it now! The easiest way is through [SDKMan](https://sdkman.io/).

In one of my previous articles, I explained how to build CLI applications with [PicoCLI](https://bazlur.ca/2024/07/18/creating-a-command-line-tool-with-jbang-and-picocli-to-generate-release-notes/). If you're interested, feel free to check that out. But in this article, we'll keep it simple, using plain Java with no external libraries.

### Getting Started

First, create a new file called `hello.java`:

`touch hello.java`  

Then, paste the following code into the file:  

```java
#!/usr/bin/java --source 21

import java.time.LocalDate;
import java.util.Random;
import java.util.Scanner;

public class HelloCLI {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        System.out.println("Welcome to the Java CLI. Type 'help' for a list of commands or 'exit' to quit.");

        while (true) {
            System.out.print("Command> ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "greet" -> System.out.println("Hello, Java enthusiast!");
                case "date" -> System.out.println("Today's date: " + LocalDate.now());
                case "time" -> System.out.println("Current time: " + java.time.LocalTime.now());
                case "random" -> System.out.println("Random number (1-100): " + (random.nextInt(100) + 1));
                case "add" -> {
                    System.out.print("Enter first number: ");
                    double num1 = scanner.nextDouble();
                    System.out.print("Enter second number: ");
                    double num2 = scanner.nextDouble();
                    scanner.nextLine(); // Consume the newline
                    System.out.println("Result: " + (num1 + num2));
                }
                case "multiply" -> {
                    System.out.print("Enter first number: ");
                    double num1 = scanner.nextDouble();
                    System.out.print("Enter second number: ");
                    double num2 = scanner.nextDouble();
                    scanner.nextLine(); // Consume the newline
                    System.out.println("Result: " + (num1 * num2));
                }
                case "help" -> {
                    System.out.println("""
                        Available commands:
                        - greet: Prints a friendly greeting.
                        - date: Displays today's date.
                        - time: Displays the current time.
                        - random: Generates a random number between 1 and 100.
                        - add: Adds two numbers.
                        - multiply: Multiplies two numbers.
                        - help: Shows this help message.
                        - exit: Exits the program.
                        """);
                }
                case "exit" -> {
                    System.out.println("Exiting... Goodbye!");
                    return; // Terminate the program
                }
                default -> System.out.println("Unknown command: " + command + ". Type 'help' for a list of commands.");
            }
        }
    }
}
```

### Key Point: Shebang Line

Notice the first line: `#!/usr/bin/java --source 21`. This is the crucial part of the file, instructing the shell to use Java to run the script in source form using Java 21.

You can remove the `.java` extension if you want; that's also fine. Just keep the file named `hello`. To rename the file, use the following command:

```
mv hello.java hello
```

### Make It Executable

Now, to make this script executable, run the following command:

```
chmod +x ./hello
```

That's it! You can now run it with:

```
./hello
```

Here's what you should see when you run it:  

```
./hello
Welcome to the Java CLI. Type 'help' for a list of commands or 'exit' to quit.
Command> help
Available commands:
- greet: Prints a friendly greeting.
- date: Displays today's date.
- time: Displays the current time.
- random: Generates a random number between 1 and 100.
- add: Adds two numbers.
- multiply: Multiplies two numbers.
- help: Shows this help message.
- exit: Exits the program.

Command> greet
Hello, Java enthusiast!
Command> date
Today's date: 2024-10-27
Command> random
Random number (1-100): 99
Command> add
Enter first number: 5
Enter second number: 39999
Result: 40004.0
Command> exit
Exiting... Goodbye!
```

### Bonus Tip: Running From Anywhere

If you'd like to run this script from anywhere on your machine, simply move the file to the `/usr/local/bin/` folder:

```
sudo mv ./hello /usr/local/bin/
```

Now, you can invoke it from any directory just by typing `hello` in your terminal.
