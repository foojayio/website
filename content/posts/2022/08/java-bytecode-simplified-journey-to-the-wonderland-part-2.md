---
title: "Java Bytecode Simplified: Journey to the Wonderland (Part 2) | Foojay Today"
slug: "java-bytecode-simplified-journey-to-the-wonderland-part-2"
date: "2022-08-16T15:15:22+00:00"
lastmod: "2022-08-16T15:15:24+00:00"
description: "Let’s appreciate the Java Virtual Machine (JVM) even more. Today we'll discuss Constant Pool in quite some detail."
authors:
  - "bazlur-rahman"
image: "/images/posts/2022/08/java-bytecode-simplified-journey-to-the-wonderland-part-2/icon256x256.png"
categories:
  - "Java Core"
tags:
related_posts:
  - "java-bytecode-simplified-journey-to-the-wonderland-part-1"
  - "what-is-jvm-bytecode"
  - "hardware-acceleration-for-java-tornadovm-can-do-it"
  - "java-bytecode-simplified-journey-to-the-wonderland-part-3"
enlighterjs: true
frozen: false
---

Our [previous article](https://foojay.io/today/java-bytecode-simplified-journey-to-the-wonderland-part-1/) introduced Bytecode and discussed what it includes.

This article will delve a bit deeper into ConstantPool.

Highlights:

* Bytecode is a representation that is abstract in nature. They are fictitious codes for a fictitious machine known as the Java Virtual Machine. The Java virtual machine is a piece of software that interprets **Bytecode**.
* The JVM is a stack-based computer. Real CPUs are register-based systems and execute machine code. Java is compiled into Bytecode, an intermediate form, which is then executed by the [Just In Time](https://en.wikipedia.org/wiki/Just-in-time_compilation) (JIT) compiler, which generates machine code.

Before going any further, let's explore `javap`, which is a very handy tool for deconstructing byte code.  

JavaP is a standard tool included in the JDK's bin subdirectory.

An intriguing aspect of `javap`, is that we do not need to deal with Java source code; rather, it just works with the binary file, which is .class extension.

Let's see an example:

<pre class="EnlighterJSRAW" data-enlighter-language="java">package ca.bazlur;

public class Lamp {
    private boolean isOn;

    public void turnOn() {
        this.isOn = true;
        printStatus();
    }

    public void turnOff() {
        this.isOn = false;
        printStatus();
    }

    private void printStatus() {
        System.out.println("Light is turned " + (isOn ? "on" : "off"));
    }

    public static void main(String[] args) {
        var lamp = new Lamp();
        lamp.turnOn();
        lamp.turnOff();
    }
}</pre>

If we compile this code using `javac` we will get a class file, and then we can use `javap` to disassemble the bytecode from the command line as follows:

`javap Lamp`

We will get the following output.

<pre class="EnlighterJSRAW" data-enlighter-language="java">Compiled from "Lamp.java"
public class ca.bazlur.Lamp {
  public ca.bazlur.Lamp();
  public void turnOn();
  public void turnOff();
  public static void main(java.lang.String[]);
}</pre>

Note that it prints only the public, protected, and default methods. Abobe, it did not print private methods. If we also wish to view the private method, we must specify an additional switch `-p`.

`javap -p Lamp`

<pre class="EnlighterJSRAW" data-enlighter-language="java">Compiled from "Lamp.java"
public class ca.bazlur.Lamp {
  private boolean isOn;
  public ca.bazlur.Lamp();
  public void turnOn();
  public void turnOff();
  private void printStatus();
  public static void main(java.lang.String[]);
}</pre>

Nonetheless, this only prints the names of the methods. We would be looking for more information, including the bytecode used in the method body. This requires another switch, which is `-c`.

`javap -c Lamp`

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Compiled from "Lamp.java"
public class ca.bazlur.Lamp {
  public ca.bazlur.Lamp();
    Code:
       0: aload_0
       1: invokespecial #1                  // Method java/lang/Object."&lt;init&gt;":()V
       4: return

  public void turnOn();
    Code:
       0: aload_0
       1: iconst_1
       2: putfield      #7                  // Field isOn:Z
       5: aload_0
       6: invokevirtual #13                 // Method printStatus:()V
       9: return

  public void turnOff();
    Code:
       0: aload_0
       1: iconst_0
       2: putfield      #7                  // Field isOn:Z
       5: aload_0
       6: invokevirtual #13                 // Method printStatus:()V
       9: return

  public static void main(java.lang.String[]);
    Code:
       0: new           #8                  // class ca/bazlur/Lamp
       3: dup
       4: invokespecial #36                 // Method "&lt;init&gt;":()V
       7: astore_1
       8: aload_1
       9: invokevirtual #37                 // Method turnOn:()V
      12: aload_1
      13: invokevirtual #40                 // Method turnOff:()V
      16: return
}</pre>

Now, this becomes significantly more intriguing, and we can observe the presence of all bytecodes. If we examine the first line of the main method, we see the following:

`new #8`

In addition to this, the code has other locations with numbers such as #1, #2, etc. These are the constant pool's reference values. If we wish to view the constant pool, we must use an additional switch, `-v`.

`javap -v Lamp`

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Classfile /bytecode-simplified/src/main/java/ca/bazlur/Lamp.class
  Last modified Aug. 11, 2022; size 1245 bytes
  SHA-256 checksum cf727468acdcc0b2dd0a6a858a313110e437e01a6625cf4e03f1f0fa41910dae
  Compiled from "Lamp.java"
public class ca.bazlur.Lamp
  minor version: 0
  major version: 62
  flags: (0x0021) ACC_PUBLIC, ACC_SUPER
  this_class: #8                          // ca/bazlur/Lamp
  super_class: #2                         // java/lang/Object
  interfaces: 0, fields: 1, methods: 5, attributes: 3
Constant pool:
   #1 = Methodref          #2.#3          // java/lang/Object."&lt;init&gt;":()V
   #2 = Class              #4             // java/lang/Object
   #3 = NameAndType        #5:#6          // "&lt;init&gt;":()V
   #4 = Utf8               java/lang/Object
   #5 = Utf8               &lt;init&gt;
   #6 = Utf8               ()V
   #7 = Fieldref           #8.#9          // ca/bazlur/Lamp.isOn:Z
   #8 = Class              #10            // ca/bazlur/Lamp
   #9 = NameAndType        #11:#12        // isOn:Z
  #10 = Utf8               ca/bazlur/Lamp
  #11 = Utf8               isOn
  #12 = Utf8               Z
  #13 = Methodref          #8.#14         // ca/bazlur/Lamp.printStatus:()V
  #14 = NameAndType        #15:#6         // printStatus:()V
  #15 = Utf8               printStatus
  #16 = Fieldref           #17.#18        // java/lang/System.out:Ljava/io/PrintStream;
  #17 = Class              #19            // java/lang/System
  #18 = NameAndType        #20:#21        // out:Ljava/io/PrintStream;
  #19 = Utf8               java/lang/System
  #20 = Utf8               out
  #21 = Utf8               Ljava/io/PrintStream;
  #22 = String             #23            // on
  #23 = Utf8               on
  #24 = String             #25            // off
  #25 = Utf8               off
  #26 = InvokeDynamic      #0:#27         // #0:makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;
  #27 = NameAndType        #28:#29        // makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;
  #28 = Utf8               makeConcatWithConstants
  #29 = Utf8               (Ljava/lang/String;)Ljava/lang/String;
  #30 = Methodref          #31.#32        // java/io/PrintStream.println:(Ljava/lang/String;)V
  #31 = Class              #33            // java/io/PrintStream
  #32 = NameAndType        #34:#35        // println:(Ljava/lang/String;)V
  #33 = Utf8               java/io/PrintStream
  #34 = Utf8               println
  #35 = Utf8               (Ljava/lang/String;)V
  #36 = Methodref          #8.#3          // ca/bazlur/Lamp."&lt;init&gt;":()V
  #37 = Methodref          #8.#38         // ca/bazlur/Lamp.turnOn:()V
  #38 = NameAndType        #39:#6         // turnOn:()V
  #39 = Utf8               turnOn
  #40 = Methodref          #8.#41         // ca/bazlur/Lamp.turnOff:()V
  #41 = NameAndType        #42:#6         // turnOff:()V
  #42 = Utf8               turnOff
  #43 = Utf8               Code
  #44 = Utf8               LineNumberTable
  #45 = Utf8               StackMapTable
  #46 = Class              #47            // java/lang/String
  #47 = Utf8               java/lang/String
  #48 = Utf8               main
  #49 = Utf8               ([Ljava/lang/String;)V
  #50 = Utf8               SourceFile
  #51 = Utf8               Lamp.java
</pre>

The output is quite large, so only a portion of the code for the constant pool is shown here.

Bytecode starts with minor and major versions. This allows us to determine the version it was compiled from. There are a few other stuff like flags. This flag is `ACC PUBLIC` because this class is a public class. The `ACC SUPER` was implemented to fix a problem with super invocation, but since Java 1.8, it has no effect; perhaps it will be deleted in the future. In reality, a JEP proposal is available to eliminate this: <https://openjdk.org/jeps/8267650>. We will not discuss all of the content of bytecode here, rather let's move on to **ConstantPool**.

It can be considered a multidimensional array. In fact, in the JVM specification, the general format mentioned as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">cp_info {
    u1 tag;
    u1 info[];
}</pre>

It contains numerous elements, including class name, field name, interface name, String, numbers, pointers to classes or methods, type descriptor, etc., and has an index.  

For instance, the first element contains a Methodref, which is composed of elements `#2` and `#3`. In `#2`, the material is `#4`. Similarly, in line `#4`, we have a `UTF-8` value that is essentially a String, namely `java/lang/Object`.

If you use `javap` to unpack the entire bytecode, you will find something known as a descriptor. They are referred to as Type descriptors. These are strings that describe the signatures of Java methods or Java types at other constant pool locations.

| *BaseType* Character |    Type     |                                  Interpretation                                  |
|----------------------|-------------|----------------------------------------------------------------------------------|
| `B`                  | `byte`      | signed byte                                                                      |
| `C`                  | `char`      | Unicode character code point in the Basic Multilingual Plane encoded with UTF-16 |
| `D`                  | `double`    | double-precision floating-point value                                            |
| `F`                  | `float`     | single-precision floating-point value                                            |
| `I`                  | `int`       | integer                                                                          |
| `J`                  | `long`      | long integer                                                                     |
| `L` *ClassName* `;`  | `reference` | an instance of class *ClassName*                                                 |
| `S`                  | `short`     | signed short                                                                     |
| `Z`                  | `boolean`   | `true` or `false`                                                                |
| `[`                  | `reference` | one array dimension                                                              |

Although it appears to be shorter \& concise, particularly for primitive types, we must always use fully qualified names in bytecode for reference types.

Let's see how we read them. e.g.

**`()Ljava/lang/String`**

In the round bracket, nothing between them indicates that this method doesn't require any parameters. The right of the brackets always indicates the return type. So this represents a method signature, which means it takes nothing but the return string, for example- `toString()`.

**`(I)V`**

This one takes integer parameters and returns a `void`. The V doesn't exist in the table, but it means void. The reason it's not present in the table is because `void` is not actually a type. It means the absence of a type.

The constant pool includes all the information required to verify a class during class loading.

If you are interested in knowing more about Constant Pool, I would recommend reading JVM specifications: <https://docs.oracle.com/javase/specs/jvms/se18/html/jvms-4.html#jvms-4.4>

This is all for today. Next, we will discuss the bytecode catalogue and the family of bytecode.
