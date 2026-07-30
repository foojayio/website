---
title: "Project Panama for Newbies (Part 2) | Foojay.io Today"
slug: "project-panama-for-newbies-part-2"
date: "2026-01-02T01:21:00+00:00"
lastmod: "2026-01-02T11:55:15+00:00"
description: "In this part of the Panama series, the goal is to call C function signatures, often defined to accept pointers and structs, from Java."
authors:
  - "carldea"
image: "https://foojay.io/wp-content/uploads/2021/08/panama_part2.png"
categories:
  - "JEPs"
  - "Project Panama"
tags:
related_posts:
enlighterjs: true
frozen: false
---

**Updated December 29, 2025 **(**originally published August 17**, 2021, republished January 2, 2026):**** This article now features Java 25 and the Foreign Function \& Memory (FFM) API, which has been a standard feature since JDK 22 ([JEP-454](https://openjdk.java.net/jeps/454)).

<figure class="wp-block-image size-full is-resized">
 <img fetchpriority="high" decoding="async" width="218" height="407" src="/images/posts/2026/01/project-panama-for-newbies-part-2/panama_part2.png" alt="" class="wp-image-46434" style="width:140px;height:261px">
</figure>

Introduction {#h2-0-introduction}
---------------------------------

Welcome back to Part 2 of Project Panama for Newbies! If you are new to this series, check out [Part 1](https://foojay.io/today/project-panama-for-newbies-part-1/) first.

If you remember from Part 1, we learned how to create C language strings, primitive data types and arrays. We also got a chance to iterate through the data outside of the Java heap and later display items via C's `printf()` and Java's `printf()` method from the `IO.printf()`.

In Part 2, below, we will look at C language's concept of **pointers** and **structs** . Later on in this article, we will use Panama to mimic these concepts. The goal is to call C function signatures that are often defined to accept **pointers** (memory addresses) and **structs**.

For the impatient, check out the source code of [Part 2](https://github.com/carldea/panama4newbies/tree/main/part02) on GitHub.

What is a C pointer? {#h2-1-what-is-a-c-pointer}
------------------------------------------------

Pointers explained according to the *C Programming Language* book by Brian W. Kernighan \& Dennis M. Ritchie:
> C supports the use of pointers, **a type of reference that records the address or location of an object or function in memory**. Pointers can be dereferenced to access data stored at the address pointed to, or to invoke a pointed-to function. Pointers can be manipulated using assignment or pointer arithmetic.
> The C Programming Language book by Brian W. Kernighan \& Dennis M. Ritchie

Before we look at the advantages of using pointers in the C language let's look at how a Java primitive value is stored and used in memory inside the JVM(Java Virtual Machine).  

For example: `int x = 5;`

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public static void main(String[] args) {   
   int x = 5;
   x = doubleIt(x); // x = 10
}

public static int doubleIt(int a) {
   return 2 * a;
}</pre>

In the Java language, there are two places to store *things* in memory, in the JVM **heap** and in the JVM **stack** . The heap is responsible for holding **objects** along with their primitive values. While inside a method the variables declared as primitive data types are stored in **stack** memory.

**Note:** The stack is also capable of storing references (memory addresses) to objects on the heap.

At first glance, the above example seems simple and straightforward, however did you know it takes up more memory when primitives are *passed by value* ? When the variable `x` is declared and assigned in the main() method and subsequently passed into the `doubleIt()` method the value `5` is being copied (stored) which means the value 5 is created **twice** . Having said this, it shows that internally it allocates two addresses (each 64 bit) and two (32bit) space allocations in memory. Wouldn't it be nice to get the address (reference) of the variable x and allow the `doubleIt()` method to access the value at the same location without copying (passed by value)?

In the C the language you can declare variables that allow you to pass primative datatypes **by reference** . A function such as `doubleIt()` would not have to copy the value. Would instead obtain the value at the location referenced in memory. Let's look at a C program rewritten using pointers to be passed into the C function `doubleIt()`.

**Note:** This section is optional to demonstrate the concepts of C pointers helping us use Panama to call functions passed by reference.

A file `pointers.c` contains the code below:

<pre class="EnlighterJSRAW" data-enlighter-language="c" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">#include &lt;stdio.h&gt;

int doubleIt(int *a);

int main () {
   int x = 5;
   int *ptr; // 1. Declare a pointer of type int.
   ptr = &amp;x; // 2. Assign a pointer variable to the address of x.

   // Display locations in memory
   printf("                                    Address of x variable: %x\n", &amp;x );
   printf("                           Address stored in ptr variable: %x\n", ptr );

   // Call doubleIt() by reference
   printf("               Address of the variable x. Call doubleIt(): %d\n", doubleIt(&amp;x) );
   printf("Pointer to the address of the variable x. Call doubleIt(): %d\n", doubleIt(ptr) );

}

/**
 * Returns a value doubled.
 * @param *a pointer to an int
 * @return int doubling of a value.
 */
int doubleIt(int *a) {
   return 2 * (*a); // two times the value at address (of pointer a).
}</pre>

To compile `pointers.c` file use the following:

<pre class="EnlighterJSRAW" data-enlighter-language="shell" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ clang -o pointers_exe pointers.c</pre>

To run the executable file type the following:

<pre class="EnlighterJSRAW" data-enlighter-language="shell" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ ./pointers_exe</pre>

The output is the following:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">                                    Address of x variable: e36584dc
                           Address stored in ptr variable: e36584dc
               Address of the variable x. Call doubleIt(): 10
Pointer to the address of the variable x. Call doubleIt(): 10</pre>

In the example a you will notice the output showing the actual address in memory for `&x` and `ptr`. The last two output lines show how to pass parameters to C functions **by reference** as opposed to **by value**.

### How does it work? {#h3-2-how-does-it-work}

The table below shows the detailed steps of the C program file `pointers.c`.

| Line  |          Code          |                                                                           Description                                                                           |
|-------|------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 7     | `int *ptr;`            | Declare a variable `ptr` pointer of type int. To declare pointers the format is: `<type> *<variable_name>;`                                                     |
| 8     | `ptr = &x;`            | Assign `ptr` to the address of the variable `x`. To assign pointers the format is: `<pointer_var_name>=&<other_variable>` Think '`&`' means **get address of**. |
| 11-12 | Output address         | `printf("%x", &x);` Show hex of the memory address location `printf("%x", ptr);` Same but without \&, ptr contains address.                                     |
| 15-16 | Call `doubleIt()`      | `printf("%d", doubleIt(&x));` Call function by reference (address). `printf("%d", doubleIt(ptr));` Same but without \&, ptr contains address.                   |
| 25    | Function signature     | `int doubleIt(int *a);` Like line 7, the declaration of a pointer.                                                                                              |
| 26    | Get value from address | return 2 \* (\*a); In parenthesis how to obtain the actually value. Think of '`*`' means **get value from**.                                                    |

To keep confusion to a minimum keep the following in mind:

* **Declaring pointers** - Prefix an asterisk `*` symbol to variable.
* **Assigning pointers** - Obtain address by prefixing `&` symbol assigning variable of the same data type.
* **Defining function parameters** - Prefix an asterisk `*` symbol to variable.
* **Accessing Values from pointers** - Obtain a value by prefixing an asterisk \* symbol. Use parens to make it clear. The notion as 'dereferencing a pointer'.

Now that we know how to talk to a C function that accepts a variable by reference let's look at how to perform this in Panama.

C Pointers Panama-fied {#h2-3-c-pointers-panama-fied}
-----------------------------------------------------

Whenever you think of a C pointer think of it as just an **address** location in memory, that stores data (in bytes). Since Pointers point to data in memory, how do you know how much data to retrieve?   
**Answer:** `MemoryLayout` and `ValueLayout`s

At its core Panama is capable of modeling primitives and complex datatypes using the classes `ValueLayout` or `MemoryLayout` respectively. Remember in [Part 1](https://foojay.io/today/project-panama-for-newbies-part-1/) we used the `MemorySession` to allocate a `JAVA_INT` (`ValueLayout`) that further creates a `MemorySegment` instance. To mimic or simulate the concept of a C pointer, you can call the address() method returns the address in memory as a `long`. The listing below shows how to mimic C's concept of pointers in Java.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">try (var arena = Arena.ofConfined()) {
   // int x = 5;
   MemorySegment x = arena.allocateFrom(JAVA_INT, 5);
   // int *ptr;
   long address = x.address();
}</pre>

### Dereferencing a Pointer {#h3-4-dereferencing-a-pointer}

To dereference (*accessing values from pointers*) pointers in C means to retrieve data at an address location in memory. But how do you know how much data to retrieve? A byte? 4 bytes? 8 bytes?

### Base offset (size in bytes) {#h3-5-base-offset-size-in-bytes}

In Panama you will need to specify a base **offset** (size) to properly retrieve the number of bytes at a given address location. When using FFM's `MemorySegment` to get and set data into memory code will need to specify its value layout (how bytes are stored) in C's equivalent data types.

The code below demonstrates the retrieval of an `int` value based on a C primitive `ValueLayout` type (`C_INT`) generated by extract. For example if the variable `x` is of type `int` from `x`'s location in memory the code will grab **4** bytes. If it's of type `long` the code would grab **8** bytes. Below is an example of how to reference and dereference a pointer:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// ptr = &amp;x; the address of x in memory as a long value.
long ptr = address; 

// Create a memory segment at the ptr's address and reinterpret byte size as 4 bytes (interpreted as a pointer to an int).
MemorySegment ptrMemSeg =  MemorySegment.ofAddress(ptr).reinterpret(4);

// (*ptr) known as dereferencing a pointer and retrieve value at address. The second parameter denotes the offset to read from.
int value = x.get(C_INT, 0L);

// value = 5 </pre>

Let's piece things all together.

The listing below explains a full example of mimicking C's concept of pointers. Similar to code snippets above, we can create variables and pointer references. The code will also change the value of the variable `x` and output the value that `ptr` (address) is pointing to (address location of `x`).

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">try (var arena = Arena.ofConfined()) {
        out.println("\nCreating Pointers:");

        // int x = 5;
        var x = arena.allocateFrom(JAVA_INT, 5);

        // int *ptr;
        long address = x.address();             // obtain address

        // ptr = &amp;x;
        long ptr = address;

        // Create a memory segment at ptr's address and reinterpret size as 4 bytes (primitive int).
        MemorySegment ptrMemSeg =  MemorySegment.ofAddress(ptr).reinterpret(4);

        // Output value: x = 5 and ptr's value = 5
        out.printf("           x = %d    address = %x %n", x.get(JAVA_INT, 0), x.address());
        out.printf(" ptr's value = %d    address = %x %n", ptrMemSeg.get(JAVA_INT, 0), ptrMemSeg.address());

         // Change x = 10;
        x.set(JAVA_INT, 0, 10);
        out.printf(" Changing x's value to: %d %n", x.get(JAVA_INT, 0));

        // Output after change
        out.printf("           x = %d    address = %x %n", x.get(JAVA_INT, 0), x.address());
        out.printf(" ptr's value = %d    address = %x %n", ptrMemSeg.get(JAVA_INT, 0), ptrMemSeg.address());
}</pre>

The output of listing above:

<pre class="EnlighterJSRAW" data-enlighter-language="raw" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">           x = 5    address = 7fedece135e0 
 ptr's value = 5    address = 7fedece135e0 
 Changing x's value to: 10 
           x = 10    address = 7fedece135e0 
 ptr's value = 10    address = 7fedece135e0 </pre>

In Java there is no notion of a C pointer but simply thought of as a 64 bit memory address (modern hardware). To obtain the address and value at a specified memory location you will call the `ofAddress(long)` and `reinterpret(byte size)` to generate a `MemorySegement` object.

`MemorySegment.ofAddress(ptr).reinterpret(4);`

To display the long representing the address in memory in hexadecimal we use the `%x` format specifier.

Now that you know how to deal with pointers to primitive types let's look at complex datatypes better known as C's concept of **structs**.

What is a C struct? {#h2-6-what-is-a-c-struct}
----------------------------------------------

To put it simply, this is the ancestor to Java's concept of classes or [records](https://foojay.io/today/records/). If you would like to go deeper into a detailed explanation such as the history of C structs, etc... head over to [Wikipedia](https://en.wikipedia.org/wiki/Struct_(C_programming_language)).

Let's explore C language's `struct`. Below is a simple example of a `struct` `Point` containing `x` and `y `coordinates.

<pre class="EnlighterJSRAW" data-enlighter-language="c" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">#include &lt;stdio.h&gt;

struct Point {
  int x;
  int y;
};

int main () {
   struct Point pt;
   pt.x = 100;
   pt.y = 50;
   printf("Point pt = (%d, %d) \n",  pt.x, pt.y);
}</pre>

The output is the following:

<pre class="EnlighterJSRAW" data-enlighter-language="raw" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">Point pt = (100, 50)</pre>

In the above example you will notice the keyword `struct` is used to define complex datatypes. In this scenario a Point is defined as two `int` variables named `x` and `y`. To declare a variable of type point the keyword is also specified or prefixed i.e. `struct Point pt;`.

To assign values to a struct instance, it is similar to Java, where the dot is used to access the attribute.

<pre class="EnlighterJSRAW" data-enlighter-language="c" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">pt.x = 100;
pt.y = 50;</pre>

An interesting thing to note that in C there isn't the keyword "`new`" like in Java. Actually, in C++ it introduces the keyword `new`.

C Structs Panama-fied {#h2-7-c-structs-panama-fied}
---------------------------------------------------

Now that we know how things work in the C world, let's look at how to mimic C's concept of structs in Java Panama. To create C language's `struct` using Panama, we'll be invoking the static method `MemoryLayout.structLayout()`. This method creates an object of type `GroupLayout`. A `GroupLayout` object will describe a memory layout similar to the `Point` struct defined in C above. The method accepts `ValueLayout` and other `MemoryLayout` instances such as `C_INT` variables used for `x` and `y` coordinates of the `Point` struct. Shown below is how to create one C `Point` struct.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">GroupLayout pointStruct = MemoryLayout.structLayout(
   C_INT.withName("x"),
   C_INT.withName("y")
);

var cPoint = arena.allocate(pointStruct);</pre>

Next, we need to **set** and **get** values from the `cPoint` instance. Below we use the method `varHandle()` to describe the path to the bytes in memory. I will describe it in more detail later, but for now think of it as a way to walk through memory to set and get data based on a memory layout.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">VarHandle VHx = pointStruct.varHandle(MemoryLayout.PathElement.groupElement("x"));
VarHandle VHy = pointStruct.varHandle(MemoryLayout.PathElement.groupElement("y"));

VHx.set(cPoint, 0L, 100); // MemorySegment, base offset, int
VHy.set(cPoint, 0L, 200);

System.out.printf("cPoint = (%d, %d) \n",  VHx.get(cPoint, 0L), VHy.get(cPoint, 0L));</pre>

This will output the following:

<pre class="EnlighterJSRAW" data-enlighter-language="raw" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">cPoint = (100, 200)</pre>

What's a java.lang.invoke.[VarHandle](https://openjdk.java.net/jeps/193)? {#h2-8-what-s-a-java-lang-invoke-varhandle}
---------------------------------------------------------------------------------------------------------------------

According to the Javadoc documentation:
> A `VarHandle` is a dynamically strongly typed reference to a variable, or to a parametrically-defined family of variables, including static fields, non-static fields, array elements, or components of an off-heap data structure. Access to such variables is supported under various *access modes*, including plain read/write access, volatile read/write access, and compare-and-set.
> [Javadoc documentation](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/invoke/VarHandle.html)

In layman's (newbie) terms a `VarHandle` can reference or access to sequences, structs and fields from memory. This API has been used beginning with Java 9. The goal of [VarHandle](https://openjdk.java.net/jeps/193) was to define a standard way to invoke the equivalents of various`java.util.concurrent.atomic` and `sun.misc.Unsafe` operations.

Important Note: the API to get and set values in structs or sequence are enhanced to allow a base offset from the memory address. Prior to the final release of var handles didn't have the second parameter to specify the base offset. This allows the developer to nest structs or retrieve memory at any offset. Since we are simply holding ints for x and y the base offset is 0L (zero long).

`VHx.set(cPoint, 0L, 100);` // set value with a zero offset from memory address.

Getting back to structs, Let's learn how to create a sequence of them!

Sequence of Structs {#h2-9-sequence-of-structs}
-----------------------------------------------

Before we look at how to create an array of structs let's look at how to define them in C. Below is a sequence or an array of 5 `Point` `struct`s. The array variable is named `points`.

<pre class="EnlighterJSRAW" data-enlighter-language="c" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">struct Point {
  int x;
  int y;
} points[5];</pre>

To iterate over an array of structs in C code the following code sets and gets data.

<pre class="EnlighterJSRAW" data-enlighter-language="c" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// sets data
for (int i=0; i&lt;5; i++) {
  points[i].x = 100 + i;
  points[i].y = 200 + i;
}

// gets data
for (int i=0; i&lt;5; i++) {
  printf("Point pt = (%3d, %3d) \n",  points[i].x, points[i].y);
}</pre>

Output is the following:

<pre class="EnlighterJSRAW" data-enlighter-language="raw" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">Point pt = (100, 50) 
Point pt = (100, 200) 
Point pt = (101, 201) 
Point pt = (102, 202) 
Point pt = (103, 203) 
Point pt = (104, 204) </pre>

Now that you know how to declare, create and access an array of structs in C, let's look at how to create a sequence of struct instances in Java Panama. To create a sequence of structs in Panama you will need the handy method `MemoryLayout.sequenceLayout()`.

Again, these methods help you create `MemoryLayout` objects responsible for describing how space should be allocated in memory. The code snipet below creates a memory layout (`SequenceLayout`) ready for the allocator.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">SequenceLayout seqStruct = MemoryLayout.sequenceLayout(5, pointStruct);</pre>

The `seqStruct` describes a memory layout as a sequence of 5 Point structs. Notice that the code reuses the already defined `pointStruct` instance defined earlier (of type `GroupLayout`). Now, let's allocate the space in memory.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">MemorySegment points = arena.allocate(seqStruct);</pre>

Similar to using `VarHandle` and `PathElement`s to access variables in memory (getters/setters) the code below creates a `VarHandle` instance that is able to access the sequence of structs and their x and y fields in memory:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">var VHSeq_x = seqStruct.varHandle(
                MemoryLayout.PathElement.sequenceElement(),
                MemoryLayout.PathElement.groupElement("x"));
var VHSeq_y = seqStruct.varHandle(
                MemoryLayout.PathElement.sequenceElement(),
                MemoryLayout.PathElement.groupElement("y"));</pre>

Now we can iterate through the sequence to set point instances and their x and y coordinates. The code listing below uses a random number generator to supply values to be set for coordinates `(x, y)`.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">Random random = new Random();
for(long i=0; i&lt;seqStruct.elementCount().getAsLong(); i++) {
  VHSeq_x.set(points, 0L, i, random.nextInt(100)); // MemorySegment, base offset, index i, int
  VHSeq_y.set(points, 0L, i, random.nextInt(100));
}</pre>

Above you'll notice the call to `seqStruct.elementCount().getAsLong()`. This allows you to obtain the number of items in the sequence of structs (`SequenceLayout`).

To output the contents of the sequence of `Point` structs the following code will invoke the `VarHandle`'s get method as shown below:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">for(long i=0; i&lt;seqStruct.elementCount().getAsLong(); i++) {
  System.out.printf(" points[%d] = (%2d, %3d) \n", i, VHSeq_x.get(points, 0L, i), VHSeq_y.get(points, 0L, i));
}</pre>

The output is the following:

<pre class="EnlighterJSRAW" data-enlighter-language="raw" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group=""> points[0] = (30,  50) 
 points[1] = (92,  59) 
 points[2] = (44,  31) 
 points[3] = (43,  80) 
 points[4] = (55,  12) </pre>

There you have it, C pointers and C structs in Java!

Conclusion {#h2-10-conclusion}
------------------------------

In Part 2, above, we got a chance to create (mimic) C's concept of pointers.

Next, we learned about memory layouts and how they define a struct using `MemoryLayout.structLayout()`. Returns an object of type `GroupLayout`

After an example of accessing a struct, we examined the important `VarHandle` class since Java 9. Also you learned of the new parameter to specify the base offset into the struct in memory.

Lastly, we were able to create and access a sequence of structs using the method `MemoryLayout.sequenceLayout()`. Returns an object of type `SequenceLayout`.

While this may be elementary to some, it's important to know how to model C's concepts to call into library functions that require variables of type pointers, structs and sequences (arrays).

The next installment ([Part 3](https://foojay.io/today/project-panama-for-newbies-part-3/)), is about using the knowledge we've attained so far to call functions in 3rd party libraries.
