---
title: "Hello eBPF: Ring buffers in libbpf (6)"
slug: "hello-ebpf-ring-buffers-in-libbpf-6"
date: "2024-03-19T17:54:22+00:00"
lastmod: "2024-03-19T17:54:23+00:00"
description: "This week I'll implement ring buffer support in my eBPF library and explain how ring buffers are used in eBPF."
authors:
  - "johannes-bechberger"
image: "ring_buffer.png"
categories:
  - "Tools"
tags:
related_posts:
  - "hello-ebpf-developing-ebpf-apps-in-java-1"
  - "hello-ebpf-recording-data-in-basic-ebpf-maps-2"
  - "hello-ebpf-recording-data-in-event-buffers-3"
enlighterjs: true
frozen: false
---

**Welcome back to my article series on eBPF. Some weeks ago, [I got started in using libbpf instead of libbcc](https://foojay.io/today/hello-ebpf-developing-ebpf-apps-in-java-1/). This week, I show you how to use ring buffers, port the code from Ansil H's blog post eBPF for Linux Admins: Part IX from C to Java, and add tests to the underlying map implementation.**

*My libbpf-based implementation advances slower than the bcc-based, as I thoroughly test all added functionality and develop a proper Java API, not just a clone.*

But first, what are eBPF ring buffers:

Ring buffers {#h2-0-ring-buffers}
---------------------------------

In [Hello eBPF: Recording data in event buffers (3)](https://foojay.io/today/hello-ebpf-recording-data-in-event-buffers-3/), I showed you how to use perf event buffers, which are the predecessor to ring buffers and allow us to communicate between kernel and user-land using events. But perf buffers have problems:
> It works great in practice, but due to its per-CPU design it has two major short-comings that prove to be inconvenient in practice: inefficient use of memory and event re-ordering.
>
> To address these issues, starting from Linux 5.8, BPF provides a new BPF data structure (BPF map): **BPF ring buffer** (ringbuf). It is a multi-producer, single-consumer (MPSC) queue and can be safely shared across multiple CPUs simultaneously.
> [BPF ring buffer](https://nakryiko.com/posts/bpf-ringbuf/) by Andrii Nakryiko

Ring buffers are still circular buffers:
![](https://mostlynerdless.de/wp-content/uploads/2024/03/ring_buffer.png)

Their usage is similar to the perf event buffers we've seen before. The significant difference is that we implemented the perf event buffers using the libbcc-based eBPF code, which made creating a buffer easy:

```cpp
BPF_PERF_OUTPUT(rb);
```


Libbcc compiles the C code with macros. With libbpf, we have to write all that ourselves:

```cpp
// anonymous struct assigned to rb variable
struct
{
  // specify the type, eBPF specific syntax
  __uint (type, BPF_MAP_TYPE_RINGBUF);
  // specify the size of the buffer
  // has to be a multiple of the page size 
  __uint (max_entries, 256 * 4096);
} rb SEC (".maps") /* placed in maps section */;
```


More on the specific syntax in the [mail for the patch specifying it](https://lwn.net/ml/netdev/20190531202132.379386-7-andriin@fb.com/), more in the [ebpf-docs](https://ebpf-docs.dylanreimerink.nl/linux/concepts/maps/).

On the eBPF side in the kernel, ring buffers have several important helper functions that allow their easy use:

### [bpf_ringbuf_output](https://ebpf-docs.dylanreimerink.nl/linux/helper-function/bpf_ringbuf_output/) {#h3-1-bpf-ringbuf-output}

```cpp
long bpf_ringbuf_output(void *ringbuf, void *data, __u64 size, __u64 flags)
```


Copy the specified number of bytes of data into the ring buffer and send notifications to user-land. This function returns a negative number on error and zero on success.

### [bpf_ringbuf_reserve](https://ebpf-docs.dylanreimerink.nl/linux/helper-function/bpf_ringbuf_reserve/) {#h3-2-bpf-ringbuf-reserve}

```cpp
void* bpf_ringbuf_reserve(void *ringbuf, __u64 size, __u64 flags)
```


Reserve a specified number of bytes in the ring buffer and return a pointer to the start. This lets us write events directly into the ring buffer's memory ([source](https://www.kernel.org/doc/html/latest/bpf/ringbuf.html)).

### [bpf_ringbuf_submit](https://ebpf-docs.dylanreimerink.nl/linux/helper-function/bpf_ringbuf_submit/) {#h3-3-bpf-ringbuf-submit}

```cpp
void *bpf_ringbuf_submit(void *data, __u64 flags)
```


Submit the reserved ring buffer event (reserved via `bpf_ringbuf_reserve`).

You might assume that you can build your own `bpf_ringbuf_output` with just bpf_ringbuf_reserve and `bpf_ringbuf_submit` and you're correct. When we look into the [actual implementation](https://github.com/torvalds/linux/blob/855684c7d938c2442f07eabc154e7532b4c1fbf9/kernel/bpf/ringbuf.c#L524) of `bpf_ringbuf_output`, we see that it is not that much more:

```
BPF_CALL_4(bpf_ringbuf_output, struct bpf_map *, map, 
           void *, data, u64, size,
	   u64, flags)
{
  struct bpf_ringbuf_map *rb_map;
  void *rec;

  // check flags
  if (unlikely(flags & ~(BPF_RB_NO_WAKEUP | BPF_RB_FORCE_WAKEUP)))
    return -EINVAL;

  // reserve the memory
  rb_map = container_of(map, struct bpf_ringbuf_map, map);
  rec = __bpf_ringbuf_reserve(rb_map->rb, size);
  if (!rec)
    return -EAGAIN;

  // copy the data into the reserved memory
  memcpy(rec, data, size);

  // equivalent to bpf_ringbuf_submit(rec, flags)
  bpf_ringbuf_commit(rec, flags, false /* discard */);
  return 0;
}
```


### [bpf_ringbuf_discard](https://ebpf-docs.dylanreimerink.nl/linux/helper-function/bpf_ringbuf_discard/)[](https://ebpf-docs.dylanreimerink.nl/linux/helper-function/bpf_ringbuf_query/) {#h3-4-bpf-ringbuf-discard}

```cpp
void bpf_ringbuf_discard(void *data, __u64 flags)
```


Discard the reserved ring buffer event.

### [bpf_ringbuf_query](https://ebpf-docs.dylanreimerink.nl/linux/helper-function/bpf_ringbuf_query/) {#h3-5-bpf-ringbuf-query}

```cpp
__u64 bpf_ringbuf_query(void *ringbuf, __u64 flags)
```


> Query various characteristics of provided ring buffer. What exactly is queries is determined by *flags*:
>
> * **BPF_RB_AVAIL_DATA**: Amount of data not yet consumed.
> * **BPF_RB_RING_SIZE**: The size of ring buffer.
> * **BPF_RB_CONS_POS**: Consumer position (can wrap around).
> * **BPF_RB_PROD_POS**: Producer(s) position (can wrap around).
>
> Data returned is just a momentary snapshot of actual values and could be inaccurate, so this facility should be used to power heuristics and for reporting, not to make 100% correct calculation.
>
> **Return** : Requested value, or 0, if *flags* are not recognized.
> [bpf-Helpers man-Page](https://www.man7.org/linux/man-pages/man7/bpf-helpers.7.html)

You can find more information in these resources:

* [eBPF Docs](https://ebpf-docs.dylanreimerink.nl/) by Dylan Reimerink
* [official Linux eBPF documentation](https://www.kernel.org/doc/html/latest/bpf/ringbuf.html)
* [bpf-helpers(7) man-page](https://man7.org/linux/man-pages/man7/bpf-helpers.7.html)
* [Linux kernel source code](https://github.com/torvalds/linux/blob/855684c7d938c2442f07eabc154e7532b4c1fbf9/kernel/bpf/ringbuf.c#L498), as you saw above, can give us insights that no documentation can provide us with

Ring Buffer eBPF Example {#h2-6-ring-buffer-ebpf-example}
---------------------------------------------------------

After I've shown you what ring buffers are on the eBPF side, we can look at the eBPF example that writes an event for every `openat call, capturing the process id, filename, and process name and comes as an addition` from Ansil H's blog post [eBPF for Linux Admins: Part IX](https://ansilh.com/posts/09-ebpf-for-linux-admins-part9/):

```cpp
#include "vmlinux.h"
#include <bpf/bpf_helpers.h>
#include <bpf/bpf_tracing.h>
#include <string.h>

#define TARGET_NAME "sample_write"
#define MAX_ENTRIES 10
#define FILE_NAME_LEN 256
#define TASK_COMM_LEN 256

// Structure to store the data that we want to pass to user
struct event
{
  u32 e_pid;
  char e_filename[FILE_NAME_LEN];
  char e_comm[TASK_COMM_LEN];
};

// eBPF map reference
struct
{
  __uint (type, BPF_MAP_TYPE_RINGBUF);
  __uint (max_entries, 256 * 4096);
} rb SEC (".maps");

// The ebpf auto-attach logic needs the SEC
SEC ("kprobe/do_sys_openat2")
     int kprobe__do_sys_openat2(struct pt_regs *ctx)
{
  char filename[256];
  char comm[TASK_COMM_LEN] = { };
  struct event *evt;
  const char fmt_str[] = "do_sys_openat2 called by:%s file:%s pid:%d";

  // Reserve the ring-buffer
  evt = bpf_ringbuf_reserve(&rb, sizeof (struct event), 0);
  if (!evt) {
      return 0;
  }
  // Get the PID of the process.
  evt->e_pid = bpf_get_current_pid_tgid();

  // Read the filename from the second argument
  // The x86 arch/ABI have first argument 
  // in di and second in si registers (man syscall)
  bpf_probe_read(evt->e_filename, sizeof(filename), 
        (char *) ctx->si);

  // Read the current process name
  bpf_get_current_comm(evt->e_comm, sizeof(comm));

  bpf_trace_printk(fmt_str, sizeof(fmt_str), evt->e_comm,
        evt->e_filename, evt->e_pid);
  // Also send the same message to the ring-buffer
  bpf_ringbuf_submit(evt, 0);
  return 0;
}

char _license[] SEC ("license") = "GPL";
```


Ring Buffer Java Example {#h2-7-ring-buffer-java-example}
---------------------------------------------------------

With this in hand, we can implement the [RingSample](https://github.com/parttimenerd/hello-ebpf/blob/main/bpf/src/main/java/me/bechberger/ebpf/samples/RingSample.java) using the newly added functionality in hello-ebpf:

```java
@BPF
public abstract class RingSample extends BPFProgram {

  static final String EBPF_PROGRAM = """
              // ...
            """;

  private static final int FILE_NAME_LEN = 256;
  private static final int TASK_COMM_LEN = 16;

  // event record
  record Event(@Unsigned int pid, 
               String filename, 
               @Size(TASK_COMM_LEN) String comm) {}

  // define the event records layout
  private static final BPFStructType<Event> eventType = 
          new BPFStructType<>("rb", List.of(
          new BPFStructMember<>("e_pid", 
                  BPFIntType.UINT32, 0, Event::pid),
          new BPFStructMember<>("e_filename", 
                  new StringType(FILE_NAME_LEN), 
                  4, Event::filename),
          new BPFStructMember<>("e_comm", 
                  new StringType(TASK_COMM_LEN), 
                  4 + FILE_NAME_LEN, Event::comm)
  ), new AnnotatedClass(Event.class, List.of()), 
  fields -> new Event((int)fields.get(0),
          (String)fields.get(1), (String)fields.get(2)));

  public static void main(String[] args) {
    try (RingSample program = BPFProgram.load(RingSample.class)) {
      // attach the kprobe
      program.autoAttachProgram(
              program.getProgramByName("kprobe__do_sys_openat2"));
      // obtain the ringbuffer
      // and write a message every time a new event is obtained
      var ringBuffer = program.getRingBufferByName("rb", eventType, 
              (buffer, event) -> {
        System.out.printf("do_sys_openat2 called by:%s file:%s pid:%d\n", 
                event.comm(), event.filename(), event.pid());
      });
      while (true) {
        // consume and throw any captured
        // Java exception from the event handler
        ringBuffer.consumeAndThrow();
      }
    }
  }
}
```


You can run the example via `./run_bpf.sh RingSample`:

```bash
do_sys_openat2 called by:C1 CompilerThre file:/sys/fs/cgroup/user.slice/user-1000.slice/<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="e693958394a6d7d6d6d6c8958394908f8583">[email protected]</a>/app.slice/snap.intellij-idea-community.intellij-idea-community-a46a168b-28d0-4bb9-9e15-f3a966353efe.scope/memory.max pid:69817
do_sys_openat2 called by:C1 CompilerThre file:/sys/fs/cgroup/user.slice/user-1000.slice/<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="86f3f5e3f4c6b7b6b6b6a8f5e3f4f0efe5e3">[email protected]</a>/app.slice/snap.intellij-idea-community.intellij-idea-community-a46a168b-28d0-4bb9-9e15-f3a966353efe.scope/memory.max pid:69812
do_sys_openat2 called by:java file:/home/i560383/.sdkman/candidates/java/21.0.2-sapmchn/lib/libjimage.so pid:69797
```


Conclusion {#h2-8-conclusion}
-----------------------------

The libbpf part of hello-ebpf keeps evolving. With this article, I added support for the first kind of eBPF maps and ring buffers, with a simplified Java API and five unit tests. I'll most likely work on the libbpf part in the future, as it is far easier to work with than with libbcc.

Thanks for joining me on this journey to create a proper Java API for eBPF. Feel free to try the examples for yourself or even write new ones and join the discussions on [GitHub](https://github.com/parttimenerd/hello-ebpf/discussions). See you in my next article about my journey to Canada or in two weeks for the next installment of this series.

*This article is part of my work in the [SapMachine](https://sapmachine.io/) team at [SAP](https://sap.com/), making profiling and debugging easier for everyone. This article first appeared on my personal blog [mostlynerdless.de](https://mostlynerdless.de).*
