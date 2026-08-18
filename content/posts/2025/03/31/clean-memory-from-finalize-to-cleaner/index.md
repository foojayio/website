---
title: "Clean your Memory: From Finalize to Cleaner"
date: "2025-03-31T12:30:08+00:00"
lastmod: "2025-03-31T12:30:10+00:00"
description: "Clean your Memory: exploring approaches for cleaning native resources, starting from finalize() method to the Cleaner API"
canonical: "https://blog.frankel.ch/java-cleaner/"
authors:
  - "stefano-fago"
image: "java_references.png"
categories:
  - "Java Core"
  - "Tutorials"
related_posts:
  - "builders-withers-and-records-javas-path-to-immutability"
  - "code-reviews-with-ai-a-developer-guide"
  - "jdb"
  - "eliminating-bugs-using-the-tong-motion-approach"
frozen: false
---

Garbage collection in Java takes care of memory management, but it does not clean up non-memory resources like sockets or file handles.

Resource leaks may occur without proper management, leading to performance degradation or crashes.

Java's [Cleaner API](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/ref/Cleaner.html), introduced in Java 9, provides a modern and efficient mechanism for resource cleanup when objects are no longer reachable.

It addresses the shortcomings of the deprecated finalize() method, offering a predictable and efficient way to manage non-memory resources: so let's take a short trip on clean memory from finalize to Cleaner API.

## Why is the finalize() method deprecated/removed?

The [finalize()](https://openjdk.org/jeps/421) method was initially introduced in Java to provide a way for objects to perform cleanup actions before they are garbage collected. It belongs to `java.lang.Object` and can be overridden by subclasses to release resources like file handles or network sockets. Why was this approach problematic?

* **Unpredictable Execution** : The `finalize()` method is invoked at an undetermined time when GC decides to collect the object.
* **Performance Overhead** : Objects with a `finalize()` method take longer to collect, as they must go through an extra GC cycle.
* **Memory Leaks** : If an object is unintentionally retained (e.g., due to an exception in `finalize()`), it may never be garbage collected.
* **Finalization Queue Mechanism** : `finalize()` execution happens in a separate thread(the Finalizer Thread), which can lead to thread contention and delays.

## How does Cleaner relate to Java Reference classes?

In Java, there are four types of references differentiated by the way by which they are garbage collected:

* **Strong References**: objects having an active, strong reference are not eligible for garbage collection. The object is garbage collected only when the variable, which was strongly referenced, points to null.
* **Weak References**: the objects referenced by a weak reference do not prevent their referents from being made finalizable, finalized, and reclaimed.
* **Soft References**: the objects which are being referenced by soft reference, even if the object is free for garbage collection, it's not garbage collected until JVM needs memory badly
* **Phantom References**: the objects referenced by phantom references are eligible for garbage collection, but before removing them, the JVM puts them in the "reference queue".

{{< img src="java_references-700x361.png" class="size-medium" alt="Clean your Memory: From Finalize to Cleaner - Java Reference Hierarchy" width="700" height="361" style="margin-left: auto; margin-right: auto;" >}}

The logic that guides how they are collected is always related to the concept of [reachability](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/ref/package-summary.html#reachability), as described in the related Javadoc. Java Reference classes are not easy to use, and the resulting [code](https://www.baeldung.com/java-phantom-reference) is sometimes complicated.  

We must also consider that in the case of a Phantom Reference, once the Referent is registered, the reference always returns `null`, making it seem useless, but it is not!

Here's an example of `PhantomReference`:

```java
import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.HashMap;
import java.util.Map;

public class UsingPhantomRef {

    static class Resource {
        public void cleaning() {
            System.out.println("cleaning");
        }
    }

    record ResourceHolder(Resource resource) {}

    private static final Map lookup = new HashMap();

    private static final ReferenceQueue queue = new ReferenceQueue();

    public static void main(String[] args) {

        var holder = new ResourceHolder(new Resource());
        lookup.put(new PhantomReference(holder, queue), holder.resource());

        holder = null;
        System.gc();

        Reference element = null;
        while ((element = queue.poll()) == null) {
            System.out.println("wating for GC");
        }
        System.out.println("GCollected!");
        lookup.remove(element).cleaning();
    }
}
```

`Cleaner` shares similarities with Java's reference classes, but it is more efficient for managing external resources. While PhantomReference enables cleanup logic, `Cleaner` adds an abstraction layer, making it easier to implement and manage. Internally, `Cleaner` uses a phantom reference behind the scenes to detect when an object becomes unreachable.

Here's an example of `Cleaner`:

```java
import java.lang.ref.Cleaner;

public class BasicCleanerExample {

    private static final Cleaner cleaner = Cleaner.create();

    static class CleaningAction implements Runnable {
        @Override
        public void run() {
            System.out.println("Resource cleaned up!");
        }
    }

    static class ManagedObject {
        private final Cleaner.Cleanable cleanable;

        ManagedObject() {
            cleanable = cleaner.register(this, new CleaningAction());
        }
    }

    public static void main(String[] args) {
        new ManagedObject();
        System.gc();
        pause();
    }
}
```

## Behind the scenes of Cleaner

The implementation behind Cleaner is not trivial. It leverages a combination of the Java `PhantomReference` and a background daemon thread. Let's explore the inner workings:

* **Registration with Cleaner** : an object registered with a `Cleaner` is associated with a `Cleanable` task. The object is essentially "watched" by a background daemon thread.
* **Phantom Reference Management** : Under the hood, `Cleaner` uses a `PhantomReference` to track the object's reachability.
* **Daemon Thread for Cleanup**: The Cleaner framework uses a dedicated daemon thread (normally named Common-Cleaner) that monitors registered objects. Once an object becomes unreachable, the cleanup task for that object is queued for execution in this background thread.

{{< img src="JVM_threads_and_cleaner-700x338.jpg" class="size-medium" alt="Clean your Memory: From Finalize to Cleaner - JVM Thread and the Common-Cleaner Thread" width="700" height="338" style="margin-left: auto; margin-right: auto;" >}}

The following snippet uses a `Cleaner` to manage files.

```java
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.Cleaner;

public class FileCleanerExample {

    private static final Cleaner cleaner = Cleaner.create();

    static class FileResource implements Runnable {

        private final File file;

        FileResource(File file) {
            this.file = file;
            System.out.printf("resource create with temporary file: %s%n", file.getAbsolutePath());
        }

        @Override
        public void run() {
            if (file.exists()) {
                System.out.printf("temporary file deleted: %s%n",file.delete());
            }
        }
    }

    static class ManagedFile {
        private Cleaner.Cleanable cleanable;

        ManagedFile(String path) throws IOException {
            var file = new File(path);
            cleanable = cleaner.register(this, new FileResource(file));
            new FileWriter(file).write("Temporary data");
        }
    }

    public static void main(String[] args) throws IOException {
        var managed = new ManagedFile("temp.txt");
        managed = null;
        System.gc();
        pause();
    }
    // ...
}
```

## Cleaner vs. try-with-resources

The example with a file might suggest a more idiomatic solution using the `try-with-resources` construct. It may be a real solution but is less so if we consider particular resources such as images or memory buffers directly mapped to memory. As a last note, we also need to consider that `Autocloseable` can be used with `Cleanable`, invoking the `clean()` method from the `close()` one, having the same effect as a GC operation.  

Here's a dedicated example.

```java
import java.lang.ref.Cleaner;

public class CleanerWithCloseExample {

    private static final Cleaner cleaner = Cleaner.create();

    static class CleaningAction implements Runnable {
        @Override
        public void run() {
            System.out.println("Resource cleaned up!");
        }
    }

    static class ManagedObject implements AutoCloseable{
        private final Cleaner.Cleanable cleanable;

        ManagedObject() {
            cleanable = cleaner.register(this, new CleaningAction());
        }

        @Override
        public void close(){
            System.out.println("close invoked!");
            cleanable.clean();
        }
    }

    public static void main(String[] args) {
        var object = new ManagedObject();
        try(object){
            System.out.printf("using: %s%n",object);
        }
    }
}
```

The following table allows us to summarize the tools presented and choose the one that best suits our purposes.

| Feature        | try-with-resources                      | Cleaner                                                                        |
|:---------------|:----------------------------------------|:-------------------------------------------------------------------------------|
| Resource Type  | Autocloseable                           | It also works with no Autocloaseable object                                    |
| Cleanup Timing | Immediate                               | Deferred, asynchronous                                                         |
| Use Case       | When precise cleanup timing is required | When working with external resources or objects without explicit close methods |
| Overhead       | Minimal                                 | Higher due to the background thread                                            |

## Avoid overusing Cleaner

Using `Cleaner` is more straightforward than using references. Still, we must exercise caution when using this kind of resource: only use `Cleaner` when the runtime can't release resources via `try-with-resources` or explicit `close()` calls.  

We also need to consider that we create new elements, and sometimes it's better to use one `Cleaner` for multiple resources or to listen to the cleaning action.  

More aspects need our attention, as for cleaning action, following some rules:

* Avoid using lambda because it risks capturing the object reference by referring to fields of the object being cleaned, preventing the object from becoming phantom reachable.
* Cleaning actions can be invoked concurrently with other cleaning actions. They should be swift to execute and not block: it may delay processing other cleaning actions registered to the same cleaner.
* We can also consider executing a cleaning action and delegating it to another thread pool, but this idea can involve more complexity and not solve concurrency issues.

## Conclusion

Java's Cleaner API provides a modern, efficient approach to resource management, addressing the limitations of `finalize()`. By leveraging `Cleaner`, developers can ensure that non-`AutoCloseable` resources, such as native memory and caches, are properly cleaned up when no longer needed. However, you should always prefer try-with-resources when possible for deterministic resource management.

By understanding when and how to use `Cleaner`, developers can write more reliable, high-performance, and memory-efficient Java applications. Mastering these best practices will be essential for building robust, scalable software as Java evolves.

## Further Resources

* [How to handle Java errors and cleanup without finalize](https://www.infoworld.com/article/2334445/how-to-handle-java-errors-and-cleanup-without-finalize.html)
* [Replacing Finalizers with Cleaners](https://inside.java/2022/05/25/clean-cleaner/)
* [Java Cleaners: The Modern Way to Manage External Resources](https://dev.to/ahmedjaad/java-cleaners-the-modern-way-to-manage-external-resources-4d4)
* [Cleaner code practices for Java programming](https://softwareengineering.stackexchange.com/questions/348770/cleaner-code-practices-for-java-programming)
