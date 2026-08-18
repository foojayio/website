---
title: "Hello eBPF: First steps with libbpf (5)"
slug: "hello-ebpf-first-steps-with-libbpf-5"
date: "2024-03-07T13:51:35+00:00"
lastmod: "2024-03-07T13:51:36+00:00"
description: "Learn why using libbcc is not the best idea and start working with the newer libbpf."
authors:
  - "johannes-bechberger"
image: "bcc_vs_bpf-1-2000x1125-1.png"
categories:
  - "Tools"
tags:
related_posts:
  - "hello-ebpf-developing-ebpf-apps-in-java-1"
  - "hello-ebpf-recording-data-in-basic-ebpf-maps-2"
  - "hello-ebpf-recording-data-in-event-buffers-3"
  - "hello-ebpf-xdp-based-packet-filter-9"
frozen: false
---

**Welcome back to my blog series on eBPF. Two weeks ago, I showed you [how to write your own eBPF application using my hello-ebpf library based on libbcc](https://mostlynerdless.de/blog/2024/02/12/hello-ebpf-tail-calls-and-your-first-ebpf-application-4/). This week, I show you why using libbcc is not the best idea and start working with the newer [libbpf](https://libbpf.readthedocs.io/en/latest/).**

With my current libbcc-based approach, we essentially embed the executed eBPF program into our programs as a string into our applications and compile them on the fly for every run:

```java
public class HelloWorld {
    public static void main(String[] args) {
        try (BPF b = BPF.builder("""
                int kprobe__sys_clone(void *ctx) {
                   bpf_trace_printk("Hello, World!");
                   return 0;
                }
                """).build()) {
            b.trace_print();
        }
    }
}
```

## Problems with Libbcc

Using libbcc and porting the Python wrapper made it easy to start developing a user-land Java library and offers some syntactic sugar, but it has major disadvantages, to quote [Andrii Nakryiko](https://twitter.com/anakryiko):
> * Clang/LLVM combo is a big library, resulting in big fat binaries that need to be distributed with your application.
> * Clang/LLVM combo is resource-heavy, so when you are compiling BPF code at start up, you'll use a significant amount of resources, potentially tipping over a carefully balanced production workfload. And vice versa, on a busy host, compiling a small BPF program might take minutes in some cases.
> * BPF program testing and development iteration is quite painful as well, as you are going to get even most trivial compilation errors only in run-time, once you recompile and restart your user-space control application. This certainly increases friction and is not helping to iterate fast.
>
> [BPF Portability and CO-RE](https://facebookmicrosites.github.io/bpf/blog/2020/02/19/bpf-portability-and-co-re.html) by [Andrii Nakryiko](https://twitter.com/anakryiko)

Additionally, the libbcc binaries in the official Ubuntu package repositories are outdated, so we're accumulating technical debt using them.

## BPF-based Library

So what is the alternative? We compile the embedded C code in our application to eBPF bytecode at build time using a custom annotation processor and load the bytecode using libbpf at run-time:
![](https://mostlynerdless.de/wp-content/uploads/2024/02/bcc_vs_bpf-1-2000x1125.png)

This allows us to create self-contained JARs that will eventually neatly package our eBPF application.

With this new chapter of the [hello-ebpf](https://github.com/parttimenerd/hello-ebpf) project, I am trying to create a proper Java API that

* builds on top of libbpf
* isn't bound to mimic the Python API, thus making it easier to understand for Java developers
* is tested with a growing number of tests so that it is safe to use
* prefers usability (and a small API) over speed

The annotation processor for this lives in the [bpf-processor](https://github.com/parttimenerd/hello-ebpf/tree/main/bpf-processor), and the central part of the library is in the [bpf](https://github.com/parttimenerd/hello-ebpf/tree/main/bpf) folder. It is in its earliest stages, but you can expect more features and tests in the following months.

## HelloWorld Example

Writing programs with libbpf is not too dissimilar to using my libbcc wrapper:

```java
@BPF // annotation to trigger the BPF annotation processor
public abstract class HelloWorld extends BPFProgram {

    // eBPF program code that is compiled at build
    // time using clang
    static final String EBPF_PROGRAM = """
            #include "vmlinux.h"
            #include <bpf/bpf_helpers.h>
            #include <bpf/bpf_tracing.h>

            SEC ("kprobe/do_sys_openat2")
            int kprobe__do_sys_openat2(struct pt_regs *ctx){                                                             
                bpf_printk("Hello, World from BPF and more!");
                return 0;
            }

            char _license[] SEC ("license") = "GPL";
            """;

    public static void main(String[] args) {
        // load an instance of the HelloWorld implementation
        try (HelloWorld program = BPFProgram.load(HelloWorld.class)) {
            // attach to the kprobe
            program.autoAttachProgram(
                program.getProgramByName("kprobe__do_sys_openat2"));
            program.tracePrintLoop(f -> 
                String.format("%d: %s: %s", (int)f.ts(), f.task(), f.msg()));
        }
    }
}
```

Running this class via `./run_bpf.sh HelloWorld` will then print the following:

```
3385: irqbalance: Hello, World from BPF and more!
3385: irqbalance: Hello, World from BPF and more!
3385: irqbalance: Hello, World from BPF and more!
3385: irqbalance: Hello, World from BPF and more!
3385: irqbalance: Hello, World from BPF and more!
3385: irqbalance: Hello, World from BPF and more!
3385: irqbalance: Hello, World from BPF and more!
3385: C2 CompilerThre: Hello, World from BPF and more!
```

The annotation processor created an implementation of the HelloWorld class, which overrides the `getByteCode` method:

```java
public final class HelloWorldImpl extends HelloWorld {
    /**
     * Base64 encoded gzipped eBPF byte-code
     */
    private static final String BYTE_CODE = "H4sIAA...n5q6hfQNFV+sgDAAA=";

    @Override
    public byte[] getByteCode() {
        return Util.decodeGzippedBase64(BYTE_CODE);
    }
}
```

## Compiler Errors

But what happens when you make a mistake in your eBPF program, for example, not writing a semicolon after the `bpf_printk` call? Then, the annotation processor throws an error at build-time and prints the following error message when calling `mvn package`:

```
Processing BPFProgram: me.bechberger.ebpf.samples.HelloWorld
Obtaining vmlinux.h header file
Could not compile eBPF program
HelloWorld.java:[19,66]  error: expected ';' after expression
    bpf_printk("Hello, World from BPF and more!")
                                                 ^
                                                 ;
1 error generated.
```

The annotation processor compiles the eBPF program using Clang and post-processes the error messages to show the location in the Java program. Using libbcc, we only get this error at run-time, which makes finding these issues far harder.

## Conclusion

Using libbpf instead of libbcc has many advantages: Smaller, self-contained JARs, better developer support, and a more modern library. The hello-ebpf project will evolve to focus on libbpf to become a fully functional and tested eBPF user-land library. Using an annotation processor offers so many possibilities, so stay tuned.

Thanks for joining me on this journey to create a proper Java API for eBPF. I'll see you in two weeks for the next installment in this series, and possibly before for a trip report on my current travels.

*This article is part of my work in the [SapMachine](https://sapmachine.io/) team at [SAP](https://sap.com/), making profiling and debugging easier for everyone, first published on my personal blog [mostlynerdless.de](https://mostlynerdless.de/blog/2024/02/26/hello-ebpf-first-steps-with-libbpf-5/). This article was written in Canada, thanks to [ConFoo](https://confoo.ca/) and Theresa Mammarella, who made this trip possible. Inspiration came from [Ansil H's series on eBPF](https://ansilh.com/posts/08-ebpf-for-linux-admins-part8/).*  
![](https://mostlynerdless.de/wp-content/uploads/2024/02/IMG_2772-2000x690.jpeg)
