---
title: "Hello eBPF: Recording data in event buffers (3)"
slug: "hello-ebpf-recording-data-in-event-buffers-3"
date: "2024-02-02T14:45:02+00:00"
lastmod: "2024-02-02T14:45:03+00:00"
description: "How to use another kind of eBPF maps, the perf event buffer, and run tests with docker and JUnit 5."
authors:
  - "johannes-bechberger"
image: "https://mostlynerdless.de/wp-content/uploads/2024/01/perf_event_buffer.png"
categories:
  - "Java"
  - "Observability"
  - "Tools"
tags:
related_posts:
enlighterjs: true
frozen: false
---

**Welcome back to my article series on eBPF. Last week, I showed you how the [eBPF program and Java application can communicate using eBPF maps](https://foojay.io/today/hello-ebpf-recording-data-in-basic-ebpf-maps-2/). This allowed us to write an application that counts the number of `execve` calls per user.**

This week, I'll show you briefly how to use another kind of eBPF maps, the perf event buffer, and run tests with docker and [JUnit 5](https://junit.org/junit5/docs/current/user-guide/).

*This article is shorter than the previous one as I'm preparing for the OpenJDK committers workshop in Brussels and my [Python and Java DevRoom talks](https://fosdem.org/2024/schedule/speaker/WS77F8/) at FOSDEM. I'm happy to meet my readers; say hi when you're there.*

Perf Event Buffer {#h2-0-perf-event-buffer}
-------------------------------------------

Data structures, like the hash map described in the previous article, are great for storing data but have their limitation when we want to pass new bits of information continuously from the eBPF program to our user-land application. This is especially pertinent when recording performance events. So, [in 2015, the Linux kernel got a new map type](https://github.com/torvalds/linux/commit/457f44363a8894135c85b7a9afd2bd8196db24ab): `BPF_MAP_TYPE_PERF_EVENT_ARRAY`.

This map type functions as a fixed-size ring buffer that can store elements of a given size and is allocated per CPU. The eBPF program submits data to the buffer, and the user-land application retrieves it. When the buffer is full, data can't be submitted, and a drop counter is incremented.  
![](https://mostlynerdless.de/wp-content/uploads/2024/01/perf_event_buffer.png)

*Perf Event Buffers have their issues, as explained by [Andrii Nakryiko](https://nakryiko.com/posts/bpf-ringbuf/), so in* [2020](https://github.com/torvalds/linux/commit/bf99c936f9478a05d51e9f101f90de70bee9a89c),*[eBPF got ring buffers](https://www.kernel.org/doc/html/latest/bpf/ringbuf.html), which have less overhead. Perf Event Buffers are still used, as only Linux 5.8 and above supports ring buffers. It doesn't make a difference for our toy examples, but I'll show you how to use ring buffers in a few weeks.*

You can read more about Perf Event Buffers in the [Learning eBPF](https://cilium.isovalent.com/hubfs/Learning-eBPF%20-%20Full%20book.pdf) book by Liz Rice, pages 24 to 28.

Example {#h2-1-example}
-----------------------

Now, to a small example, called [chapter2.HelloBuffer](https://github.com/parttimenerd/hello-ebpf/blob/main/bcc/src/test/java/me/bechberger/ebpf/bcc/HelloWorldTest.java), which records for every `execve` call the calling process id, the user id, and the current task name and transmits it to the Java application:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&gt; ./run.sh chapter2.HelloBuffer
2852613 1000 code Hello World  # vs code
2852635 1000 code Hello World
2852667 1000 code Hello World
2852690 1000 code Hello World
2852742 1000 Sandbox Forked Hello World  # Firefox
2852760 1000 pool-4-thread-1 Hello World
2852760 1000 jspawnhelper Hello World    # Java ProcessBuilder
2852760 1000 jspawnhelper Hello World
2852760 1000 jspawnhelper Hello World
2852760 1000 jspawnhelper Hello World
2852760 1000 jspawnhelper Hello World
2852760 1000 jspawnhelper Hello World
2852760 1000 jspawnhelper Hello World
2852760 1000 jspawnhelper Hello World
</pre>

This gives us already much more information than the simple counter from my [last article](https://mostlynerdless.de/blog/2024/01/12/hello-ebpf-recording-data-in-basic-ebpf-maps-2/). The eBPF program to achieve this is as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="cpp" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">BPF_PERF_OUTPUT(output);                                                 

struct data_t {                                                          
    int pid;                                                             
    int uid;                                                             
    char command[16];                                                    
    char message[12];                                                    
};                                                                       

int hello(void *ctx) {                                                   
    struct data_t data = {};                                             
    char message[12] = "Hello World";                                    

    // obtain process and user id                                                                     
    data.pid = bpf_get_current_pid_tgid() &gt;&gt; 32;                         
    data.uid = bpf_get_current_uid_gid() &amp; 0xFFFFFFFF;                   

    // obtain the current task/thread/process name, 
    // without the folder, of the task that is currently
    // running                                                                     
    bpf_get_current_comm(&amp;data.command, 
        sizeof(data.command));
    // "Safely attempt to read size bytes from kernel space
    //  address unsafe_ptr and store the data in dst." (man-page)           
    bpf_probe_read_kernel(&amp;data.message, 
        sizeof(data.message), message); 

    // try to submit the data to the perf buffer                                                                     
    output.perf_submit(ctx, &amp;data, sizeof(data));                        

    return 0;                                                            
}                                                                        </pre>

You can get more information on `bpf_get_current_com`, `bpf_probe_read_kernel` in the [bpf-helpers(7) man-page](https://www.man7.org/linux/man-pages/man7/bpf-helpers.7.html).

The Java application that reads the buffer and prints the obtained information is not too dissimilar from the example in [my previous article](https://mostlynerdless.de/blog/2024/01/12/hello-ebpf-recording-data-in-basic-ebpf-maps-2/). We first define the `Data` type:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">record Data(
   int pid, 
   int uid, 
   // we model char arrays as Strings
   // with a size annotation
   @Size(16) String command,
   @Size(12) String message) {}                                                                                                                              

// we have to model the data type as before                                                                                                                              
static final BPFType.BPFStructType&lt;Data&gt; DATA_TYPE = 
   new BPFType.BPFStructType&lt;&gt;("data_t",                              
        List.of(                                                                                                               
                new BPFType.BPFStructMember&lt;&gt;("pid", 
                     BPFType.BPFIntType.INT32, 0, Data::pid),                                  
                new BPFType.BPFStructMember&lt;&gt;("uid", 
                     BPFType.BPFIntType.INT32, 4, Data::uid),                                  
                new BPFType.BPFStructMember&lt;&gt;("command", 
                     new BPFType.StringType(16), 8, Data::command),                        
                new BPFType.BPFStructMember&lt;&gt;("message", 
                     new BPFType.StringType(12), 24, Data::message)),                      
        new BPFType.AnnotatedClass(Data.class, List.of()),                                                                     
            objects -&gt; new Data((int) objects.get(0), 
                                (int) objects.get(1), 
                                (String) objects.get(2),
                                (String) objects.get(3)));</pre>

*You might recognize that the BPF types now have the matching Java type in their type signature. I added this to have more type safety and less casting.*

To retrieve the events from the buffer, we first have [to open it and pass in a call-back](https://github.com/parttimenerd/hello-ebpf/blob/df7feea50f8ae126de6f436fbc960ea66f8baa39/bcc/src/main/java/me/bechberger/ebpf/bcc/BPFTable.java#L971C34-L971C50). This call-back is called for every available event when we call [PerfEventArray](https://github.com/parttimenerd/hello-ebpf/blob/df7feea50f8ae126de6f436fbc960ea66f8baa39/bcc/src/main/java/me/bechberger/ebpf/bcc/BPFTable.java#L887)`#`[perf_buffer_poll](https://github.com/parttimenerd/hello-ebpf/blob/df7feea50f8ae126de6f436fbc960ea66f8baa39/bcc/src/main/java/me/bechberger/ebpf/bcc/BPF.java#L955):

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">try (var b = BPF.builder("""                                                                                                    
        ...                                                                                                                     
        """).build()) {                                                                                                         
    var syscall = b.get_syscall_fnname("execve");                                                                               
    b.attach_kprobe(syscall, "hello");                                                                                          

    BPFTable.PerfEventArray.EventCallback&lt;Data&gt; print_event = 
      (/* PerfEventArray instance */ array, 
       /* cpu id of the event */     cpu, 
       /* event data */              data, 
       /* size of the event data */  size) -&gt; {                                     
        var d = array.event(data);                                                                                              
        System.out.printf("%d %d %s %s%n", 
            d.pid(), d.uid(), d.command(), d.message());                                         
    };                                                                                                                          

    try (var output = b.get("output", 
         BPFTable.PerfEventArray.&lt;Data&gt;createProvider(DATA_TYPE))
             .open_perf_buffer(print_event)) { 
        while (true) {
            // wait till packages are available,
            // you can a timeout in milliseconds                                                                                                          
            b.perf_buffer_poll();                                                                                               
        }                                                                                                                       
    }                                                                                                                           
}                                                                                                                               

                                                                                                                                </pre>

Tests {#h2-2-tests}
-------------------

I'm happy to announce that [hello-ebpf](https://github.com/parttimenerd/hello-ebpf) now has its own test runner, which uses [virtme](https://github.com/amluto/virtme) and docker to run all tests in their own runtime with their own kernel. All this is wrapped in my [testutil/bin/java](https://github.com/parttimenerd/hello-ebpf/blob/main/testutil/bin/java) wrapper so that you can run the tests using `mvn test`:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">mvn -Djvm=testutil/bin/java</pre>

And the best part? All tests are written using plain [JUnit 5](https://junit.org/junit5/). As an example, here is the [HelloWorld](https://github.com/parttimenerd/hello-ebpf/blob/df7feea50f8ae126de6f436fbc960ea66f8baa39/bcc/src/test/java/me/bechberger/ebpf/bcc/HelloWorldTest.java) test:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class HelloWorldTest {
    @Test
    public void testHelloWorld() throws Exception {
        try (BPF b = BPF.builder("""
                int hello(void *ctx) {
                   bpf_trace_printk("Hello, World!");
                   return 0;
                }
                """).build()) {
            var syscall = b.get_syscall_fnname("execve");
            b.attach_kprobe(syscall, "hello");
            Utils.runCommand("uname", "-r");
            // read the first trace line
            var line = b.trace_readline();
            // assert its content
            assertTrue(line.contains("Hello, World!"));
        }
    }
}</pre>

There are currently only two tests, but I plan to add many more.

Conclusion {#h2-3-conclusion}
-----------------------------

In this article, we learned about Perf Event Buffers, a valuable data structure for repeatedly pushing information from the eBPF program to the user-land application. Implementing this feature, we're getting closer and closer to completing chapter 2 of the [Learning eBPF](https://cilium.isovalent.com/hubfs/Learning-eBPF%20-%20Full%20book.pdf) book.

Truth be told, the implementation in the GitHub repository supports enough of the BCC to implement the remaining examples and even the exercises from Chapter 2.

In the next part of the [hello-ebpf](https://mostlynerdless.de/blog/tag/hello-ebpf/) series, I'll show you how to tail call in eBPF to other eBPF functions and how to write your first eBPF application that uses the [hello-ebpf](https://github.com/parttimenerd/hello-ebpf) library as a dependency.

Thanks for joining me on this journey to create a proper Java API for eBPF. Feel free to try the examples for yourself or even write new ones and join the discussions on [GitHub](https://github.com/parttimenerd/hello-ebpf/discussions). See you in my next article or at [FOSDEM](https://fosdem.org/2024).

*This article is part of my work in the [SapMachine](https://sapmachine.io/) team at [SAP](https://sap.com/), making profiling and debugging easier for everyone. This article first appeared on my personal blog [mostlynerdless.de](https://mostlynerdless.de/blog/2024/01/29/hello-ebpf-recording-data-in-event-buffers-3/).*
