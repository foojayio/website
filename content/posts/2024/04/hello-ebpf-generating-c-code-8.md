---
title: "Hello eBPF: Generating C Code (8)"
slug: "hello-ebpf-generating-c-code-8"
date: "2024-04-24T13:11:40+00:00"
lastmod: "2024-04-24T21:01:32+00:00"
description: "This week we generate the C code for struct and map definitions automatically, using annotation processors to simplify writing programs."
authors:
  - "johannes-bechberger"
image: "/images/posts/2024/04/hello-ebpf-generating-c-code-8/ebpf_maps-2000x425-1.png"
categories:
  - "Tools"
tags:
related_posts:
  - "hello-ebpf-developing-ebpf-apps-in-java-1"
  - "hello-ebpf-recording-data-in-basic-ebpf-maps-2"
  - "hello-ebpf-recording-data-in-event-buffers-3"
  - "hello-ebpf-xdp-based-packet-filter-9"
enlighterjs: true
frozen: false
---

**Welcome back to my [series on ebpf](https://mostlynerdless.de/blog/tag/hello-ebpf/). In the last article, we learned how to[auto-layout struct members and auto-generate BPFStructTypes for annotated Java records](https://mostlynerdless.de/blog/2024/03/25/hello-ebpf-auto-layouting-structs-7/). We're going to extend this work today.**

*This is a rather short article, but the implementation and fixing all the bugs took far more time then expected.*

Generating Struct Definitions {#h2-0-generating-struct-definitions}
-------------------------------------------------------------------

We saw in the last article how powerful Java annotation processing is for generating Java code; this week, we'll tackle the generation of C code: In the previous article, we still had to write the C struct and map definitions ourselves, but writing

<pre class="EnlighterJSRAW" data-enlighter-language="c" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">struct event {
  u32 e_pid;
  char e_filename[FILE_NAME_LEN];
  char e_comm[TASK_COMM_LEN];
};</pre>

when we already specified the data type properly in Java

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">record Event(@Unsigned int pid,
             @Size(FILE_NAME_LEN) String filename,
             @Size(TASK_COMM_LEN) String comm) {}</pre>

seems to be a great place to improve our annotation processor. There are only two problems:

1. The annotation processor needs to know about BPFTypes, so we have to move them in there. But the BPFTypes use the Panama API which requires the --enable-preview flag in JDK 21, making it unusable in Java 21. So we have to move the whole library over to JDK 22, as this version includes Panama.
2. There is no C code generation library like [JavaPoet](https://github.com/square/javapoet) for generating Java code.

Regarding the first problem: Moving to JDK 22 is quite easy, the only changes I had to make are listed in this [gist](https://gist.github.com/parttimenerd/d59e3e4213da08b192332cc2c8386d9e). The only major problem was getting the Lima VM to use a current JDK 22. In the end I resorted to just using [sdkman](https://sdkman.io/), you can a look into the [install.sh](https://github.com/parttimenerd/hello-ebpf/blob/main/bin/install.sh) script to see how I did it.

Regarding the second problem: We can reduce the problem of generating C code into two steps:

1. Create an Abstract Syntax Tree (AST) for C
2. Create a pretty printer for this AST

To create an AST I resorted to an [ANSI C grammar](http://www.lysator.liu.se/c/ANSI-C-grammar-y.html#declarator) for inspiration. Each AST node implements the following interface:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public interface CAST {

    List&lt;? extends CAST&gt; children();

    Statement toStatement();

    /** Generate pretty printed code */
    default String toPrettyString() {
        return toPrettyString("", "  ");
    }

    String toPrettyString(String indent, String increment);
}</pre>

We can then create a hierarchy of extending interfaces (PrimaryExpression, ...) and implementing records (ConstantExpression, ...). You can find the whole C AST on [GitHub](https://github.com/parttimenerd/hello-ebpf/blob/main/bpf-processor/src/main/java/me/bechberger/cast/CAST.java).

This leads us to an annotation processor that can add automatically insert struct definitions into the C code of our eBPF program, reducing the amount of hard-to-debug errors as it is guaranteed that both the Java specification and C representation of every type are compatible.

But can we do more with annotation processing?

Generating Map Definitions {#h2-1-generating-map-definitions}
-------------------------------------------------------------

There is another definition that we can auto-generate: Map definitions like

<pre class="EnlighterJSRAW" data-enlighter-language="c" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group=""> struct                                
 {                                     
   __uint (type, BPF_MAP_TYPE_RINGBUF);
   __uint (max_entries, 256 * 4096);   
 } rb SEC (".maps");</pre>

which define maps like hash maps and ring buffers that allow the communication between user- and kernel-space.

With a little of annotation processor, we can define the same ring buffer from above in Java:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@BPFMapDefinition(maxEntries = 256 * 4096)
BPFRingBuffer&lt;Event&gt; rb;</pre>

Our annotation-processor then turns this into the C definition from above and inserts code into the constructor of the Java program that properly initializes `rb`.

But how does the processor know what code it should generate? By parsing the BPFMapClass annotation on BPFRingBuffer (and any other class). This annotation contains the templates for both the C and the Java code:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@BPFMapClass(
        cTemplate = """
        struct {
            __uint (type, BPF_MAP_TYPE_RINGBUF);
            __uint (max_entries, $maxEntries);
        } $field SEC(".maps");
        """,
        javaTemplate = """
        new $class&lt;&gt;($fd, $b1)
        """)
public class BPFRingBuffer&lt;E&gt; extends BPFMap {
}</pre>

Here `$field` is the Java field name, `$maxEntries` the value in the BPFMapDefinition annotation and `$class` the name of the Java class. `$cX`, `$bX`, `$jX` give the C type name, BPFType and Java class names related to the `X`^th^ type parameter.

Ring Buffer Sample Program {#h2-2-ring-buffer-sample-program}
-------------------------------------------------------------

When we combine all this together we can have a much simpler ring buffer sample program (see [TypeProcessingSample2](https://github.com/parttimenerd/hello-ebpf/blob/main/bpf/src/main/java/me/bechberger/ebpf/samples/TypeProcessingSample2.java) on GitHub):

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@BPF(license = "GPL")
public abstract class TypeProcessingSample2 extends BPFProgram {

    private static final int FILE_NAME_LEN = 256;
    private static final int TASK_COMM_LEN = 16;

    @Type(name = "event")
    record Event(
      @Unsigned int pid, 
      @Size(FILE_NAME_LEN) String filename, 
      @Size(TASK_COMM_LEN) String comm) {}

    @BPFMapDefinition(maxEntries = 256 * 4096)
    BPFRingBuffer&lt;Event&gt; rb;

    static final String EBPF_PROGRAM = """
            #include "vmlinux.h"
            #include &lt;bpf/bpf_helpers.h&gt;
            #include &lt;bpf/bpf_tracing.h&gt;
            #include &lt;string.h&gt;

            // This is where the struct and map
            // definitions are inserted automatically          

            SEC ("kprobe/do_sys_openat2")
            int kprobe__do_sys_openat2 (struct pt_regs *ctx)
            {
               // ... // as before
            }
            """;

    public static void main(String[] args) {
        try (TypeProcessingSample2 program = 
           BPFProgram.load(TypeProcessingSample2.class)) {
            program.autoAttachProgram(
              program.getProgramByName("kprobe__do_sys_openat2"));
            // we can use the rb ring buffer directly
            // but have to set the call back
            program.rb.setCallback((buffer, event) -&gt; {
                System.out.printf(
                  "do_sys_openat2 called by:%s " + 
                  "file:%s pid:%d\n", 
                  event.comm(), event.filename(), 
                  event.pid());
            });
            while (true) {
                // consumes all registered ring buffers
                program.consumeAndThrow();
            }
        }
    }
}</pre>

There are two other things missing in the C code that are also auto-generated: Constant defining macros and the license definition. Macros are generated for all static final fields in the program class that are defined at compile time.

Conclusion {#h2-3-conclusion}
-----------------------------

Using annotation processing allows to reduce the amount of C code we have to write and reduces errors by generating all definitions from the Java code. This simplifies writing eBPF applications.

See you in two weeks when we tackle global variables, moving closer and closer to making hello-ebpf's bpf support able to write a small firewall.

*This will also be the topic of a talk that I submitted together with Mohammed Aboullaite to several conferences for autumn.*

Addendum {#h2-4-addendum}
-------------------------

The more I work on writing my own ebpf library, the more I value the effort that the developers of other libraries like bcc, the Go or Rust ebpf libraries put it in to create usable libraries.

They do this despite the lack of of proper documentation.

A simple example is the deattaching of attached ebpf programs: There are multiple (undocumented) methods in libbpf that might be suitable; `bpf_program__unload`, `bpf_link__detach`, `bpf_link__destroy`, `bpf_prog_detach`, but only `bpf_link__destroy` properly detached a program.

*This article is part of my work in the [SapMachine](https://sapmachine.io/) team at [SAP](https://sap.com/), making profiling and debugging easier for everyone. This article first appeared on my personal blog [mostlynerdless.de](https://mostlynerdless.de).*

<br />

<br />
