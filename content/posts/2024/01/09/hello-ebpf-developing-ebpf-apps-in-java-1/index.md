---
title: "Hello eBPF: Developing eBPF Apps in Java (1)"
slug: "hello-ebpf-developing-ebpf-apps-in-java-1"
date: "2024-01-09T16:34:56+00:00"
lastmod: "2024-01-30T00:11:41+00:00"
description: "eBPF allows you to attach programs directly to hooks in the Linux kernel without loading kernel modules, like hooks for networking or executing programs."
authors:
  - "johannes-bechberger"
image: "text2102.png"
categories:
  - "Tools"
tags:
related_posts:
  - "ap-loader-a-new-way-to-use-and-embed-async-profiler"
  - "asyncgetcalltrace-reworked-frame-by-frame-with-an-iterative-touch"
  - "class-loader-hierarchies"
  - "hello-ebpf-xdp-based-packet-filter-9"
enlighterjs: true
frozen: false
---

**eBPF allows you to attach programs directly to hooks in the Linux kernel without loading kernel modules, like hooks for networking or executing programs.**

This has historically been used for writing custom package filters in firewalls. Still, nowadays, it is used for monitoring and tracing, becoming an ever more critical building block of modern observability tools. To quote from [ebpf.io](https://ebpf.io/what-is-ebpf/):
> 

<figure class="aligncenter size-full is-resized">
 <img decoding="async" src="https://mostlynerdless.de/wp-content/uploads/2023/12/image-20.png" alt="" class="wp-image-1469" style="width:524px;height:auto">
</figure>

>
> Historically, the operating system has always been an ideal place to implement observability, security, and networking functionality due to the kernel's privileged ability to oversee and control the entire system. At the same time, an operating system kernel is hard to evolve due to its central role and high requirement towards stability and security. The rate of innovation at the operating system level has thus traditionally been lower compared to functionality implemented outside of the operating system.[](https://ebpf.io/static/e293240ecccb9d506587571007c36739/f2674/overview.png)
>
> eBPF changes this formula fundamentally. It allows sandboxed programs to run within the operating system, which means that application developers can run eBPF programs to add additional capabilities to the operating system at runtime. The operating system then guarantees safety and execution efficiency as if natively compiled with the aid of a Just-In-Time (JIT) compiler and verification engine. This has led to a wave of eBPF-based projects covering a wide array of use cases, including next-generation networking, observability, and security functionality.
>
> Today, eBPF is used extensively to drive a wide variety of use cases: Providing high-performance networking and load-balancing in modern data centers and cloud native environments, extracting fine-grained security observability data at low overhead, helping application developers trace applications, providing insights for performance troubleshooting, preventive application and container runtime security enforcement, and much more. The possibilities are endless, and the innovation that eBPF is unlocking has only just begun.

Writing eBPF apps {#h2-0-writing-ebpf-apps}
-------------------------------------------

On the lowest level, eBPF programs are compiled down to [eBPF bytecode](https://www.kernel.org/doc/html/latest/bpf/standardization/instruction-set.html) and attached to hooks in the kernel via a syscall. This is tedious; so many libraries for eBPF allow you to write applications using and interacting with eBPF in C++, [Rust](https://aya-rs.dev/), [Go](https://ebpf-go.dev/), Python, and even Lua.

But there are none for Java, which is a pity. So... I decided to write bindings using the new [Foreign Function API](https://openjdk.org/jeps/454) (Project Panama, preview in 21) and [bcc](https://github.com/iovisor/bcc), the first and widely used library for eBPF, which is typically used with its Python API and allows you to write eBPF programs in C, compiling eBPF programs dynamically at runtime.

*That's why I wrote [From C to Java Code using Panama](https://mostlynerdless.de/blog/2023/12/11/from-c-to-java-code-using-panama/) a few weeks ago.*

Anyway, I'm starting my new blog series and eBPF library [hello-ebpf](https://github.com/parttimenerd/hello-ebpf):  
![](https://mostlynerdless.de/wp-content/uploads/2023/12/text2102.png)

Let's discover eBPF together. Join me on the journey to write all examples from the [Learning eBPF book](https://cilium.isovalent.com/hubfs/Learning-eBPF%20-%20Full%20book.pdf) (get it also from [Bookshop.org](https://bookshop.org/p/books/learning-ebpf-programming-the-linux-kernel-for-enhanced-observability-networking-and-security-liz-rice/19244244?ean=9781098135126), [Amazon](https://www.amazon.com/Learning-eBPF-Programming-Observability-Networking/dp/1098135121), or [O'Reilly](https://www.oreilly.com/library/view/learning-ebpf/9781098135119/)) by Liz Rice and more in Java, implementing a Java library for eBPF along the way, with a blog series to document the journey. I highly recommend reading the book alongside my articles; for this blog post, I read the book till page 18.

The project is still in its infancy, but I hope that we can eventually extend the overview image from [ebpf.io](https://ebpf.io/what-is-ebpf/) with a [duke](https://wiki.openjdk.org/display/duke/Main):
![](https://mostlynerdless.de/wp-content/uploads/2023/12/image-23.png)

Goals {#h2-1-goals}
-------------------

The main goal is to provide a library (and documentation) for Java developers to explore eBPF and write their own eBPF programs without leaving their favorite language and runtime.

The initial goal is to be as close to the BCC Python API as possible to easily port the book's examples to Java. You can find the Java versions of the examples in the [src/main/me/bechberger/samples](https://github.com/parttimenerd/hello-ebpf/blob/main/src/main/me/bechberger/samples) and the API in the [src/main/me/bechberger/bcc](https://github.com/parttimenerd/hello-ebpf/blob/main/src/main/me/bechberger/bcc) directory in the [GitHub repository](https://github.com/parttimenerd/hello-ebpf).

Implementation {#h2-2-implementation}
-------------------------------------

The Python API is just a wrapper around the bcc library using the built-in [cffi](https://cffi.readthedocs.io/en/latest/), which extends the raw bindings to improve usability. The initial implementation of the library is a translation of the Python code to Java 21 code with Panama for FFI.

For example the following method of the Python API

```python
    def get_syscall_fnname(self, name):
        name = _assert_is_bytes(name)
        return self.get_syscall_prefix() + name
```


is translated into Java as follows:

```java
    public String get_syscall_fnname(String fnName) {
        return get_syscall_prefix() + fnName;
    }
```


This is the reason why the library has the same license as the Python API, Apache 2.0. The API is purposefully close to the Python API and only deviates where absolutely necessary, adding a few helper methods to improve it slightly. This makes it easier to work with the examples from the book and speeds up the initial development. But finishing a translation of the Python API is not the end goal:

Plans {#h2-3-plans}
-------------------

A look ahead into the future so you know what to expect:

* Implement the full API so that we can recreate all bcc examples from the book
* Make it adequately available as a library on Maven Central
* Support the newer [libbpf](https://github.com/libbpf/libbpf) library
* Allow writing eBPF programs in Java

These plans might change, but I'll try to keep this current. I'm open to suggestions, contributions, and ideas.

Contributing {#h2-4-contributing}
---------------------------------

Contributions are welcome; just open an [issue](https://github.com/parttimenerd/hello-ebpf/issues) or a [pull request](https://github.com/parttimenerd/hello-ebpf/pulls). Discussions take place in the [discussions](https://github.com/parttimenerd/hello-ebpf/discussions) section of the [GitHub repository](https://github.com/parttimenerd/hello-ebpf). Please spread the word if you like it; this greatly helps the project.

I'm happy to include more example programs, API documentation, helper methods, and links to repositories and projects that use this library.

Running the first example {#h2-5-running-the-first-example}
-----------------------------------------------------------

The Java library is still in its infancy, but we are already implementing the most basic eBPF program from the book that prints "Hello World!" every time a new program is started via [execve](https://www.man7.org/linux/man-pages/man2/execve.2.html):

```
> ./run.sh bcc.HelloWorld
           <...>-30325   [042] ...21 10571.161861: bpf_trace_printk: Hello, World!\n
             zsh-30325   [004] ...21 10571.164091: bpf_trace_printk: Hello, World!\n
             zsh-30325   [115] ...21 10571.166249: bpf_trace_printk: Hello, World!\n
             zsh-39907   [127] ...21 10571.167210: bpf_trace_printk: Hello, World!\n
             zsh-30325   [115] ...21 10572.231333: bpf_trace_printk: Hello, World!\n
             zsh-30325   [060] ...21 10572.233574: bpf_trace_printk: Hello, World!\n
             zsh-30325   [099] ...21 10572.235698: bpf_trace_printk: Hello, World!\n
             zsh-39911   [100] ...21 10572.236664: bpf_trace_printk: Hello, World!\n
 MediaSu~isor #3-19365   [064] ...21 10573.417254: bpf_trace_printk: Hello, World!\n
 MediaSu~isor #3-22497   [000] ...21 10573.417254: bpf_trace_printk: Hello, World!\n
 MediaPD~oder #1-39914   [083] ...21 10573.418197: bpf_trace_printk: Hello, World!\n
 MediaSu~isor #3-39913   [116] ...21 10573.418249: bpf_trace_printk: Hello, World!\n
```


This helps you track the processes that use [execve](https://www.man7.org/linux/man-pages/man2/execve.2.html) and lets you observe that Firefox creates many processes.

The related code can be found in [chapter2/HelloWorld.java](https://github.com/parttimenerd/hello-ebpf/blob/main/src/main/java/me/bechberger/ebpf/samples/chapter2/HelloWorld.java):

```java
public class HelloWorld {
  public static void main(String[] args) {
    try (BPF b = BPF.builder("""
            int hello(void *ctx) {
               bpf_trace_printk("Hello, World!\\\\n");
               return 0;
            }
            """).build()) {
      var syscall = b.get_syscall_fnname("execve");
      b.attach_kprobe(syscall, "hello");
      b.trace_print();
    }
  }
}
```


Which is equivalent to the Python [code](https://github.com/parttimenerd/hello-ebpf/blob/main/pysamples/chapter2/hello.py) from the book. But, of course, many features have not yet been implemented.

Conclusion {#h2-6-conclusion}
-----------------------------

eBPF is an integral part of the modern observability tech stack. The hello-ebpf Java library will allow you to write eBPF applications directly in Java for the first time. This is an enormous undertaking for a side project so it will take some time. With my new blog series, you can be part of the journey, learning eBPF and building great tools.

I plan to write a blog post every few weeks and hope you join me. You wouldn't be the first: [Mohammed Aboullaite](https://www.linkedin.com/in/aboullaite) has already entered and helped me with his eBPF expertise.

*This article is part of my work in the [SapMachine](https://sapmachine.io/) team at [SAP](https://sap.com/), making profiling and debugging easier for everyone.*
