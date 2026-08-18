---
title: "Hello eBPF: Tail calls and your first eBPF application (4)"
slug: "hello-ebpf-tail-calls-and-your-first-ebpf-application-4"
date: "2024-02-23T08:44:51+00:00"
lastmod: "2024-02-23T08:44:52+00:00"
description: "This week, we use tail calls and create our first application using hello-ebpf as a library."
authors:
  - "johannes-bechberger"
image: "image-1-1.png"
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

Welcome back to my series on eBPF. Two weeks ago, I showed you [how to use perf event buffers to stream data from the eBPF program to the Java application](https://foojay.io/today/hello-ebpf-recording-data-in-event-buffers-3/).

This week, we will finish chapter 2 of the Learning eBPF book, learn how to use tail calls and the hello-ebpf project as a library and implement one of the book's exercises.

We start with function and tail calls:

## Function Calls

Regular C programs are divided into functions that call each other; so far in this series, all our eBPF programs consisted of just a single function that calls kernel functions. But can we call other eBPF functions? End of 2017, Daniel Borkman et al. introduced the ability to call other functions defined in eBPF:
> It allows for better optimized code and finally allows to introduce the core bpf libraries that can be reused in different projects, since programs are no longer limited by single elf file. With function calls bpf can be compiled into multiple .o files.
> [bpf: introduce function calls](https://lwn.net/Articles/741773/) by Alexei Starovoitov

Before this change, you had to inline the functions essentially. There is just one problem with this approach: Every new function call takes space on the stack for its call frame that contains its parameters and local variables:
![](https://mostlynerdless.de/wp-content/uploads/2024/02/tail_call-2000x599.png)

The maximum stack size is limited to [512 bytes](https://github.com/torvalds/linux/blob/841c35169323cd833294798e58b9bf63fa4fa1de/tools/include/linux/filter.h#L28), so every call frame counts for larger eBPF programs. Modern compilers will, therefore, try to inline the function calls and save space. To reduce the required stack memory, we have essentially two options besides inlining: We can either use static variables or tail calls. Andrii Nakryiko describes the former:
> Starting with Linux 5.2, [d8eca5bbb2be ("bpf: implement lookup-free direct value access for maps")](https://github.com/torvalds/linux/commit/d8eca5bbb2be9) adds support for BPF global (and static) variables, which we are going to use here to get rid of on-the-stack array.
> [BPF tips \& tricks: the guide to bpf_trace_printk() and bpf_printk()](https://nakryiko.com/posts/bpf-tips-printk/)

Declaring a variable as static, e.g. `static int x`, means that the value is stored as a global variable, existing once per program run. This is not a problem if a function doesn't transitively call itself, which is true for all functions you would typically want to write in eBPF.

## Tail Calls

Now to tail calls. If the function calls another function directly before returning (or as an argument to the return statement), then the call frames can be replaced. This is called a tail call and avoids growing the stack. In eBPF, it is possible to tail call one eBPF program (entry function that gets passed a context) from another program:
![](https://mostlynerdless.de/wp-content/uploads/2024/02/image-1.png) From [ebpf.io](https://ebpf.io/what-is-ebpf/#tail--function-calls)'s section on tail calls

A tail call is achieved by storing the other program in a program array, which maps a 4-byte int to an eBPF program. The kernel function **bpf_tail_call**`(ctx, program_array, index)` can then be used to call a specific program:
> This special helper is used to trigger a "tail call", or in other words, to jump into another eBPF program. The same stack frame is used (but values on stack and in registers for the caller are not accessible to the callee). This mechanism allows for program chaining, either for raising the maximum number of available eBPF instructions, or to execute given programs in conditional blocks. For security reasons, there is an upper limit to the number of successive tail calls that can be performed.
>
> Upon call of this helper, the program attempts to jump into a program referenced at index *index* in *prog_array_map* , a special map of type **BPF_MAP_TYPE_PROG_ARRAY** , and passes *ctx*, a pointer to the context.
> [BPF-HELPERS(7)](https://www.man7.org/linux/man-pages/man7/bpf-helpers.7.html)

This function only returns when it encounters an error, returning a negative error code.

## Tail Call Example

Let's create, as an example, an entry function that is triggered for every system call and tail calls another function using the stored ebpf programs for each system call number, based on the example in the [Learning eBPF book](https://cilium.isovalent.com/hubfs/Learning-eBPF%20-%20Full%20book.pdf):

```cpp
BPF_PROG_ARRAY(syscall, 300);

int hello(struct bpf_raw_tracepoint_args *ctx) {
    // args[1] is here the syscall number
    int nr = ctx->args[1];
    // this is the BCC syntax for bpf_tail_call
    syscall.call(ctx, nr);
    // we only reach the print if the
    // syscall number is not associated
    // with a function
    bpf_trace_printk("Another syscall: %d", nr);
    return 0;
}

int hello_exec(void *ctx) {
    bpf_trace_printk("Executing a program");
    return 0;
}

int hello_timer(struct bpf_raw_tracepoint_args *ctx) {
    int nr = ctx->args[1];
    switch (nr) {
        case 222:
            bpf_trace_printk("Creating a timer");
            break;
        case 226:
            bpf_trace_printk("Deleting a timer");
            break;
        default:
            bpf_trace_printk("Some other timer operation");
            break;
    }
    return 0;
}

int ignore_nr(void *ctx) {
    return 0;
}
```

We can now store a function for every system call in the `syscall` program array, register the `hello` for every system call and tail call the specified function for every system call number.

You can find this example in the [hello-ebpf](https://github.com/parttimenerd/hello-ebpf/blob/main/bcc/src/main/java/me/bechberger/ebpf/samples/chapter2/HelloTail.java) repository. This includes all the Java code required to attach the eBPF program and log the result. I could just show you the example code, but let's do something different this time:

## Tail Example Application

I recently released the hello-ebpf library, consisting mainly of the bcc and annotation libraries, in [Sonatype's snapshot repository](https://s01.oss.sonatype.org/content/repositories/snapshots/). Let's use these releases to create our first application. This first application is a version of the HelloTail example from before.

We start by cloning my new [sample-bcc-project](https://github.com/parttimenerd/sample-bcc-project), which we subsequently modify. This sample project contains essentially the following three parts:

* `src/main/java/Main.java`: Main class for our Maven-based build
* `pom.xml`: Maven pom that uses the snapshot repository to depend on the `me.bechberger.bcc `library. It also allows you to build a JAR with all dependencies included via `mvn package`.
* `run.sh`: run the built JAR with the required flags "*--enable-preview --enable-native-access=ALL-UNNAMED*"
* `README.md`: Information on how to run the program and more.

We only have to change the Main class to develop our application, adding our system-call-logging-related code. Our application should be able only to log execve, and itimer-related system calls when passed the `--skip-others` flag on the command line. So, we start with implementing the argument parsing:

```java
record Arguments(boolean skipOthers) {
    static Arguments parseArgs(String[] args) {
        boolean skipOthers = false;
        if (args.length > 0) {
            if (args.length == 1 && args[0].equals("--skip-others")) {
                skipOthers = true;
            } else {
                // print usage for all other arguments, this
                // includes --help
                System.err.println("""
                Usage: app [--skip-others]

                   --skip-others: Only log execve and itimer system calls
                """);
                System.exit(1);
            }
        }
        return new Arguments(skipOthers);
    }
}
```

We then define the eBPF program, as well as some system calls that come up a lot, as static variables:

```java
static final String EBPF_PROGRAM = """
            ...
            """;

static final int[] IGNORED_SYSCALLS = new int[]{
        21, 22, 25, 29, 56, 57, 63, 64, 66,
        72, 73, 79, 98, 101, 115, 131, 134,
        135, 139, 172, 233, 280, 291};
```

Now to the important part: The `main` and `run` methods that contain the central part of our application:

```java
public static void main(String[] args) {
    run(Arguments.parseArgs(args));
}

static void run(Arguments args) {
    try (var b = BPF.builder(EBPF_PROGRAM).build()) {
        // attach to the tracepoint that is
        // called at the start of every system call
        b.attach_raw_tracepoint("sys_enter", "hello");

        // get the function ids of all defined functions
        var ignoreFn = b.load_raw_tracepoint_func("ignore_nr");
        var execFn = b.load_raw_tracepoint_func("hello_exec");
        var timerFn = b.load_raw_tracepoint_func("hello_timer");

        // obtain the program array
        var progArray = b.get_table("syscall", 
            BPFTable.ProgArray.createProvider());

        // map the system call execve to the hello_exec function
        progArray.set(Syscalls.getSyscall("execve").number(), 
                      execFn);

        // map the itimer system calls to the hello_timer function
        for (String syscall : new String[]{
                "timer_create", "timer_gettime",
                "timer_getoverrun", "timer_settime",
                "timer_delete"}) {
            progArray.set(Syscalls.getSyscall(syscall).number(), 
                          timerFn);
        }

        // ignore some system calls that come up a lot
        for (int i : IGNORED_SYSCALLS) {
            progArray.set(i, ignoreFn);
        }

        // print the trace using a custom formatter
        b.trace_print(f -> formatTrace(f, args.skipOthers));
    }
}
```

This code uses the [Syscalls](https://github.com/parttimenerd/hello-ebpf/blob/774e813457642430313dd498c6f1e9ca596b4cdf/bcc/src/main/java/me/bechberger/ebpf/bcc/Syscalls.java) class from the bcc library to map system calls to their number. The only part left now is the custom formatter, which takes care of the --skip-others option:

```java
static @Nullable String formatTrace(BPF.TraceFields f, 
  boolean skipOthers) {       
    String another = "Another syscall: ";                                          
    String line = f.line().replace("bpf_trace_printk: ", "");                      
    // replace other syscall with their names                                      
    if (line.contains(another)) {                                                  
        // skip these lines if --skip-others is passed                             
        if (skipOthers) {                                                          
            return null;                                                           
        }                                                                          
        var syscall =                                                              
                Syscalls.getSyscall(                                               
                        Integer.parseInt(                                          
                                line.substring(                                    
                                        line.indexOf(another) +                    
                                                another.length())));               
        return line.replace(another + syscall.number(),                            
                another + syscall.name());                                         
    }                                                                              
    return line;                                                                   
}
```

This gives us an application that we can build via `mvn package`, and run:

```bash
> sudo -s PATH=$PATH                                                   
> ./run.sh --skip-others                                               
     ps-26459   [031] ...2. 91897.197604: Executing a program          
    git-26551   [052] ...2. 91935.368240: Executing a program          
    git-26553   [031] ...2. 91935.373159: Executing a program          
    git-26555   [016] ...2. 91935.378132: Executing a program          
  <...>-26558   [053] ...2. 91935.383839: Executing a program          
   tail-26561   [004] ...2. 91935.388621: Executing a program          
    git-26562   [099] ...2. 91935.388970: Executing a program
   ...          
> ./run.sh                                                      
  <...>-3277    [122] ...2. 91946.796677: Another syscall: recvmsg     
   Xorg-3045    [121] ...2. 91946.796678: Another syscall: setitimer   
  <...>-26461   [074] ...2. 91946.796680: Another syscall: readlink    
   Xorg-3045    [121] ...2. 91946.796680: Another syscall: epoll_wait  
  <...>-3457    [068] ...2. 91946.796681: Another syscall: recvmsg     
  <...>-3277    [122] ...2. 91946.796682: Another syscall: recvmsg     
  <...>-26461   [074] ...2. 91946.796684: Another syscall: readlink    
  <...>-3277    [122] ...2. 91946.796685: Another syscall: recvmsg     
  <...>-3457    [068] ...2. 91946.796689: Another syscall: recvmsg     
  <...>-3277    [122] ...2. 91946.796690: Another syscall: recvmsg
  ...
```

You can run this either on a Linux machine with Java 21 and [libbcc](https://github.com/iovisor/bcc/blob/master/INSTALL.md) installed or on Mac using the [Lima VM](https://lima-vm.io/):

```bash
> limactl start hello-ebpf.yaml
> limactl shell hello-ebpf
> sudo -s
> ./run.sh
# ...
```

More information and the whole implementation in the [System Call Logger](https://github.com/parttimenerd/sample-bcc-project/tree/tail-example) branch of the [sample-bcc-project](https://github.com/parttimenerd/sample-bcc-project).

## Conclusion

In this article, I showed you how to use tail calls and develop your first standalone eBPF application using the hello-ebpf library. Most of the bcc implementation was present two weeks ago when I wrote my previous article of this series, but now it's slightly more polished. The hello-ebpf libaries' releases are currently live in the snapshot repository.

Now, on to you: There are exercises at the end of chapter 2 of the [Learning eBPF](https://cilium.isovalent.com/hubfs/Learning-eBPF%20-%20Full%20book.pdf) book. Can you implement them on your own? Clone the [sample-bcc-project](https://github.com/parttimenerd/sample-bcc-project) and give it a try. I'm happy to showcase any cool forks in my next article.

Thanks for joining me on this journey to create a proper Java API for eBPF. I'm looking forward to finishing porting the whole bcc API and starting with the next iteration of this project. I'll keep you posted; see you in my next post.

*This article is part of my work in the [SapMachine](https://sapmachine.io/) team at [SAP](https://sap.com/), making profiling and debugging easier for everyone. This article first appeared on my personal blog [mostlynerdless.de](https://mostlynerdless.de).*
