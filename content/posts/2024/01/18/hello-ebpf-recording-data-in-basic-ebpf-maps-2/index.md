---
title: "Hello eBPF: Recording data in basic eBPF maps (2)"
date: "2024-01-18T15:50:44+00:00"
lastmod: "2024-01-30T00:11:32+00:00"
description: "This week we'll add support eBPF maps to hello-ebpf to communicate between eBPF program and userland Java programs."
authors:
  - "johannes-bechberger"
image: "ebpf_maps-2000x425-1.png"
categories:
  - "Tools"
related_posts:
  - "hello-ebpf-developing-ebpf-apps-in-java-1"
  - "continuous-production-profiling-and-diagnostics"
  - "looking-back-on-one-year-of-speaking-and-blogging"
  - "hello-ebpf-xdp-based-packet-filter-9"
frozen: false
---

Welcome back to my article series on eBPF.

Last week, I introduced [eBPF, the series, and the project](https://mostlynerdless.de/blog/2023/12/31/hello-ebpf-developing-ebpf-apps-in-java-1/) and showed how you can write a [simple eBPF application](https://github.com/parttimenerd/hello-ebpf/blob/spotless/bcc/src/main/java/me/bechberger/ebpf/samples/chapter2/HelloWorld.java) with Java that prints "Hello World!" whenever a process calls `execve`:

```java
public class HelloWorld {
  public static void main(String[] args) {
    try (BPF b = BPF.builder("""
            int hello(void *ctx) {
               bpf_trace_printk("Hello, World!");
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

But what if we want to send more information from our eBPF program to our userland application than just some logs?

For example, to share the accumulated number of `execve` calls, the processes of a specific user called and transmits information akin to:

```java
record Data(
     /** user id */
     @Unsigned long uid,
     /** group id */
     @Unsigned long gid, 
     /** count of execve calls */
     @Unsigned int counter) {}
```

This is what this week's article is all about.

## Communication

When two regular programs want to share information, they either send data via sockets or use shared memory that both programs can access:
![](https://mostlynerdless.de/wp-content/uploads/2024/01/sockets_and_shared_mem-2000x865.png)

eBPF uses none of the above two approaches: Working with sockets makes a shared state hard to maintain, and using shared memory is difficult because the eBPF program lives in the kernel and the Java program in userland.

Accessing any userland memory from eBPF at all is deemed to be experimental, according to the official BPF Design Q\&A:
>
> ### [Q: Can BPF overwrite arbitrary user memory?](https://www.kernel.org/doc/html/v6.6-rc5/bpf/bpf_design_QA.html#id29)
>
> A: Sort-of.
>
> Tracing BPF programs can overwrite the user memory of the current task with bpf_probe_write_user(). Every time such program is loaded the kernel will print warning message, so this helper is only useful for experiments and prototypes. Tracing BPF programs are root only.
> [BPF Design Q\&A](https://www.kernel.org/doc/html/v6.6-rc5/bpf/bpf_design_QA.html#q-can-bpf-overwrite-arbitrary-user-memory)[](https://www.kernel.org/doc/html/v6.6-rc5/bpf/bpf_design_QA.html#bpf-design-q-a)

But how can we then communicate? This is where eBPF maps come in:
![](https://mostlynerdless.de/wp-content/uploads/2024/01/ebpf_maps-2000x425.png) BPF 'maps' provide generic storage of different types for sharing data between kernel and user space. There are several storage types available, including hash, array, bloom filter and radix-tree. Several of the map types exist to support specific BPF helpers that perform actions based on the map contents.
>
> BPF maps are accessed from user space via the `bpf` syscall, which provides commands to create maps, lookup elements, update elements and delete elements.
> [LINUX Kernel Documentation](https://www.kernel.org/doc/html/latest/bpf/maps.html)

These fixed-size data structures form the backbone of every eBPF application, and their support is vital to creating any non-trivial tool.

## Using basic eBPF maps

Using these maps, we can implement our execve-call-counter eBPF program. We start with the simple version that just stores the counter in a simple user-id-to-counter hash map:

```cpp
// macro to create a uint64_t to uin64_t hash map
BPF_HASH(counter_table);

// u64 (also known as uint64_t) is an unsigned
// integer with a width of 64 bits
// in Java terms, it's the unsigned version
// of long

int hello(void *ctx) {
   u64 uid;
   u64 counter = 0;
   u64 *p;

   uid = bpf_get_current_uid_gid() & 0xFFFFFFFF;
   p = counter_table.lookup(&uid);
   // p is null if the element is not in the map
   if (p != 0) {
      counter = *p;
   }
   counter++;
   counter_table.update(&uid, &counter);
   return 0;
}
```

*This example is from the [Learning eBPF book](https://cilium.isovalent.com/hubfs/Learning-eBPF%20-%20Full%20book.pdf) by Liz Rice, pages 21 to 23, where you can find a different take. And if you're wondering why we're using `u64` instead of the more standard `uint64_t`, this is because the Linux kernel predates the definition of `u64` (and other such types) in `stdint.h` (see [StackOverflow](https://stackoverflow.com/a/30896945)), although today it's possible to use both.*

In this example, we first create a hash called counter_table using the bcc macro BPF_HASH. We can access the hash map using the bcc-only method `lookup` and `update`, which are convenience wrappers for **`void *bpf_map_lookup_elem(struct bpf_map *`** *`map`* **`, const void *`** *`key`* **`)`** and **`long bpf_map_update_elem(struct bpf_map *`** *`map`* **`, const void *`** *`key`* **`,const void *`** *`value`* **`, u64 `** *`flags`* **`)`** (see the [bpf-helpers man-page](https://www.man7.org/linux/man-pages/man7/bpf-helpers.7.html)). Additionally, we use `bpf_get_current_uid_gid`() to get the current user-id:
> **u64 bpf_get_current_uid_gid(void)**
>
> **Description** Get the current uid and gid.
>
> **Return** A 64-bit integer containing the current GID and UID, and created as such: *current_gid* **<<** `32`**|** *current_uid*.
> [bpf-helpers man-page](https://www.man7.org/linux/man-pages/man7/bpf-helpers.7.html)

*A side note regarding naming: "table" and "map" are used interchangeably in the bcc Python-API and related examples, which I carried over into the Java-API for consistency.*

Now to the userland program: The [hello-ebpf](https://github.com/parttimenerd/hello-ebpf) Java API offers methods to access these maps and can be used to write a userland program, [HelloMap](https://github.com/parttimenerd/hello-ebpf/blob/de6d87babd85aa4e2313cbd45f4c6e417cb7fc6c/bcc/src/main/java/me/bechberger/ebpf/samples/chapter2/HelloMap.java), that prints the contents of the maps every few seconds:

```java
public class HelloMap {
    public static void main(String[] args) 
      throws InterruptedException {
        try (var b = BPF.builder("""
                ...
                """).build()) {
            var syscall = b.get_syscall_fnname("execve");
            // attach the eBPF program to execve
            b.attach_kprobe(syscall, "hello");
            // create a mirror for the hash table eBPF map
            BPFTable.HashTable<Long, Long> counterTable = 
               b.get_table("counter_table", 
                           UINT64T_MAP_PROVIDER);
            while (true) {
                Thread.sleep(2000);
                // the map mirror implements the Java Map
                // interface with methods like 
                // Map.entrySet
                for (var entry : counterTable.entrySet()) {
                    System.out.printf("ID %d: %d\t", 
                                      entry.getKey(), 
                                      entry.getValue());
                }
                System.out.println();
            }
        }
    }
}
```

This program attaches the eBPF program to the `execve` system call and uses the HashTable map mirror to access the map `counter_table`.

You can run the [example](https://github.com/parttimenerd/hello-ebpf/blob/de6d87babd85aa4e2313cbd45f4c6e417cb7fc6c/bcc/src/main/java/me/bechberger/ebpf/samples/chapter2/HelloMap.java) using the `run.sh` script (after you built the project via the `build.sh` script) as root on an x86 Linux:

```bash
> ./run.sh chapter2.HelloMap
ID 0: 1 ID 1000: 3
ID 0: 1 ID 1000: 3
ID 0: 1 ID 1000: 4
ID 0: 1 ID 1000: 11
ID 0: 1 ID 1000: 11
ID 0: 1 ID 1000: 12
...
ID 0: 22 ID 1000: 176
```

Here, user 0 is the root user, and user 1000 is my non-root user, I called `ls` in the shell with both users a few times to gather some data.

But maybe my map mirror is broken, and this data is just a fluke? It's always good to have a way to check the content of the maps. This is where [bpftool-map](https://man.archlinux.org/man/bpftool-map.8.en) comes into play: We can use

```bash
> bpftool map list
2: prog_array  name hid_jmp_table  flags 0x0
        key 4B  value 4B  max_entries 1024  memlock 8512B
        owner_prog_type tracing  owner jited
40: hash  name counter_table  flags 0x0
        key 8B  value 8B  max_entries 10240  memlock 931648B
        btf_id 142

> bpftool map dump name counter_table
[{
        "key": 1000,
        "value": 163
    },{
        "key": 0,
        "value": 22
    }
]
```

We can see that our examples are in the correct ballpark.

*To learn more about the features of bpftool, I highly recommend reading the article ["Features of bpftool: the thread of tips and examples to work with eBPF objects"](https://qmonnet.github.io/whirl-offload/2021/09/23/bpftool-features-thread/) by Quentin Monnet.*

Storing simple numbers in a map is great, but what if we want to keep more complex information as values in the map, like the Data record with user-id, group-id, and counter from the beginning of this article?

The most recent addition to the hello-ebpf project is the support of record/struct values in maps:

## Storing more complex structs in maps

The eBPF code for this [example](https://github.com/parttimenerd/hello-ebpf/blob/a5e7f979b0560d9603b6736b9beb8874618d93fb/bcc/src/main/java/me/bechberger/ebpf/samples/own/HelloStructMap.java) is a slight extension of the previous example:

```cpp
// record Data(
//    @Unsigned long uid, 
//    @Unsigned long gid, 
//    @Unsigned int  counter
// ){}
struct data_t {
   u64 uid;
   u64 gid;
   u32 counter;
};

// u64 to data_t map
BPF_HASH(counter_table, u64, struct data_t);

int hello(void *ctx) {
   // get user id
   u64 uid = bpf_get_current_uid_gid() & 0xFFFFFFFF;
   // get group id
   u64 gid = bpf_get_current_uid_gid() >> 32;
   // create data object 
   // with uid, gid and counter=0
   struct data_t info = {uid, gid, 0};
   struct data_t *p = counter_table.lookup(&uid);
   if (p != 0) {
      info = *p;
   }
   info.counter++;
   counter_table.update(&uid, &info);
   return 0;
}
```

The Java application is slightly more complex, as we have to model the `data_t` struct in Java. We start by defining the record Data as before:

```java
record Data(
     /** user id */
     @Unsigned long uid,
     /** group id */
     @Unsigned long gid, 
     /** count of execve calls */
     @Unsigned int counter) {}
```

*The @Unsigned annotation is part of the [ebpf-annotations](https://github.com/parttimenerd/hello-ebpf/tree/main/annotations) module and allows you to document type properties that aren't present in Java.*

The mirror `BPFType` for structs in hello-ebpf [BPFType.BPFStructType](https://github.com/parttimenerd/hello-ebpf/blob/a5e7f979b0560d9603b6736b9beb8874618d93fb/bcc/src/main/java/me/bechberger/ebpf/bcc/BPFType.java#L172):

```java
/**
 * Struct
 *
 * @param bpfName     name of the struct in BPF
 * @param members     members of the struct, 
 *                    order should be the same as 
 *                    in the constructor
 * @param javaClass   class that represents the struct
 * @param constructor constructor that takes the members 
 *                    in the same order as 
 *                    in the constructor
 */
record BPFStructType(String bpfName, 
                    List<BPFStructMember> members, 
                    AnnotatedClass javaClass,
                    Function<List<Object>, ?> constructor) 
    implements BPFType
```

Which model struct members as follows:

```
/**
 * Struct member
 *
 * @param name   name of the member
 * @param type   type of the member
 * @param offset offset from the start of the struct in bytes
 * @param getter function that takes the struct and returns the member
 */
record BPFStructMember(String name, 
                       BPFType type, 
                       int offset, 
                       Function<?, Object> getter)
```

With these classes, we can model our `data_t` struct as follows:

```java
BPFType.BPFStructType DATA_TYPE = 
    new BPFType.BPFStructType("data_t",
        List.of(
          new BPFType.BPFStructMember(
            "uid", 
            BPFType.BPFIntType.UINT64, 
            /* offset */ 0, (Data d) -> d.uid()),
          new BPFType.BPFStructMember(
            "gid", 
            BPFType.BPFIntType.UINT64, 
            8, (Data d) -> d.gid()),
          new BPFType.BPFStructMember(
            "counter", 
            BPFType.BPFIntType.UINT32, 
            16, (Data d) -> d.counter())),
        new BPFType.AnnotatedClass(Data.class, List.of()),
            objects -> 
              new Data((long) objects.get(0), 
                       (long) objects.get(1), 
                       (int) objects.get(2)));
```

*This is cumbersome, I know, but it will get easier soon, I promise.*

The `DATA_TYPE` type can then be passed to the `BPFTable.HashTable` to create the `UINT64T_DATA_MAP_PROVIDER`:

```java
BPFTable.TableProvider<BPFTable.HashTable<@Unsigned Long, Data>> 
    UINT64T_DATA_MAP_PROVIDER =
        (/* BPF object */ bpf, 
         /* map id in eBPF */ mapId, 
         /* file descriptor of the map */ mapFd, 
         /* name of the map */ name) ->
                new BPFTable.HashTable<>(
                     bpf, mapId, mapFd, 
                     /* key type */   BPFType.BPFIntType.UINT64, 
                     /* value type */ DATA_TYPE, 
                     name);
```

We use this provider to access the map with `BPF#get_table`:

```java
public class HelloStructMap {

    // ...

    public static void main(String[] args) 
      throws InterruptedException {
        try (var b = BPF.builder("""
                // ...
                """).build()) {
            var syscall = b.get_syscall_fnname("execve");
            b.attach_kprobe(syscall, "hello");

            var counterTable = b.get_table("counter_table", 
                 UINT64T_DATA_MAP_PROVIDER);
            while (true) {
                Thread.sleep(2000);
                for (var value : counterTable.values()) {
                    System.out.printf(
                       "ID %d (GID %d): %d\t", 
                       value.uid(), value.gid(), 
                       value.counter());
                }
                System.out.println();
            }
        }
    }
}
```

We can run the example and get the additional information:

```bash
> ./run.sh own.HelloStructMap
ID 0 (GID 0): 1 ID 1000 (GID 1000): 3
ID 0 (GID 0): 1 ID 1000 (GID 1000): 9
...
ID 0 (GID 0): 1 ID 1000 (GID 1000): 13
ID 0 (GID 0): 5 ID 1000 (GID 1000): 14

> bpftool map dump name counter_table
[{
        "key": 0,
        "value": {
            "uid": 0,
            "gid": 0,
            "counter": 5
        }
    },{
        "key": 1000,
        "value": {
            "uid": 1000,
            "gid": 1000,
            "counter": 13
        }
    }
]
```

Granted, it doesn't give you more insights into the observed system, but it is a showcase of the current state of the map support in hello-ebpf.

## Conclusion

eBPF maps are the primary way to communicate information between the eBPF program and the userland application.

Hello-ebpf gained with this article support for basic eBPF hash maps and the ability to store structures in these maps.

But of course, hash maps are not the only type of maps; we'll add support for other map types, like perf maps and queues, in the next articles, as well as making the struct definitions a little bit easier.

So, stay tuned.

Thanks for joining me on this journey to create a proper Java API for eBPF. Feel free to try the examples for yourself or even write new ones and join the discussions on [GitHub](https://github.com/parttimenerd/hello-ebpf/discussions). See you in my next article.

*This article is part of my work in the [SapMachine](https://sapmachine.io/) team at [SAP](https://sap.com/), making profiling and debugging easier for everyone. Thanks to [Mohammed Aboullaite](https://www.linkedin.com/in/aboullaite) for answering my many questions. This article first appeared on my personal blog [mostlynerdless.de](https://mostlynerdless.de/blog/2024/01/12/hello-ebpf-recording-data-in-basic-ebpf-maps-2/).*
