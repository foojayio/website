---
title: "Immutability vs concurrency: making a fast graph writable"
date: "2026-01-01"
description: "An in-memory graph is fast until you need to write to it."
authors:
  - "andrey-minogin"
image: "cover.jpg"
categories:
  - "Performance"
  - "Kotlin"
canonical: "https://minogin.com/immutability-vs-concurrency-making-a-fast-graph-writable/"
related_posts:
  - "optimizing-nanosecond-operations-when-the-cpu-cache-matters"
---

In [my last article](https://minogin.com/when-postgres-is-the-wrong-place-to-traverse-the-graph/) I promised to explain how the in-memory graph could be modified in real-time without losing high traversal throughput.

The structure in question is this:

```kotlin
val graph = IntObjectHashMap<IntArrayList>()
```

Every map entry is a bunch of edges, where key is the source node id and value is the list of target node ids.

A common requirement is to regularly sync this in-memory structure with the database. We assume that writes are small and not frequent compared to reads.

## Immutability vs concurrency

To make this structure updatable we could use two general mechanisms — immutability and concurrency. Immutability means we create a new object instead of editing the existing one, and replace the reference. Concurrency means we use locks to ensure the data is not modified and accessed at the same time.

First we must agree that the whole map cannot be made immutable as it consumes a lot of memory and takes a long time to populate. HPPC collections are non thread-safe so we need to wrap the map into some concurrent structure.

## Optimistic locking

A naive approach would be to synchronize access to the map, but it will kill the whole idea of a high-performance map, as reads don’t need to be synchronized with each other while writes are rare. Alternatively we might use a non-exclusive lock for reads and exclusive lock for writes, but that still means acquiring a lock on every read operation which is expensive.

There’s a better alternative — optimistic locking (based on a version counter rather than a lock). We try to read from the map without acquiring a lock, and then we check if there was no write intervention. If someone wrote to the map in the meantime, we could either repeat the read or switch to a read lock.

To make this convenient let’s draft a utility for optimistic locking.

**Caution: code in this article has flaws and is only used as a simplified example!**

```kotlin
class OptimisticLock {
    // This one has all the capabilities we need
    private val lock = StampedLock()
    
    fun <T> blockingWrite(block: () -> T): T {
        // An exclusive write lock
        val stamp = lock.writeLock()
        try {
            return block()
        } finally {
            lock.unlockWrite(stamp)
        }
    }

    fun <T> blockingRead(block: () -> T): T {
        // A non-exclusive read lock
        val stamp = lock.readLock()
        try {
            return block()
        } finally {
            lock.unlockRead(stamp)
        }
    }

    fun <T> optimisticRead(block: () -> T): T {
        val stamp = lock.tryOptimisticRead()

        // stamp == 0 means there's a write in-progress
        if (stamp != 0L) {
            val result = try {
                block()
            } catch (e: RuntimeException) {
                if (!lock.validate(stamp)) return blockingRead(block)
                throw e
            }
            // validate returns true if there was no intervening write
            if (lock.validate(stamp)) return result
        }
        return blockingRead(block)
    }
}
```

Notice the `try / catch` inside the optimistic read function. It’s not just a dummy protection, the `block()` execution is dangerous as it runs in a concurrent environment without any thread safety. In our case `keys` and `mask` fields of `IntObjectHashMap` can get out of sync which will cause an `ArrayIndexOutOfBoundsException`. But if we know that it happened due to concurrent access we can safely switch to blocking read instead of rethrowing the exception.

A few things to improve here if you are going to use this code:

1. Inline the block calls.
2. Try optimistic read 2-3 times before switching to blocking read.
3. Make sure to not allow writes within read blocks.
4. Fix possible infinite loop inside `block()` call due to concurrent write.
5. Beware of `StampedLock` non-reentrancy.

## Immutable nested lists

Using the above technique we could make the whole map concurrent without hurting read performance. So what about the nested lists? We could also make them concurrent but this means introducing a huge amount of locks (one per source node) while it’s highly improbable that we will write the same list concurrently.

In this case it’s much easier to make nested lists immutable and replace them as a whole. But, again, HPPC does not provide an immutable version of `IntArrayList`, so let’s create one.

```kotlin
class ImmutableIntArrayList(values: IntArray) {
    private val list = IntArrayList(values.size).apply {
        add(values, 0, values.size)
    }

    fun get(index: Int) = list[index]

    val size = list.size()
}
```

## Final high-throughput solution

Now let’s put it all together.

```kotlin
class ConcurrentGraph {
    private val graph = IntObjectHashMap<ImmutableIntArrayList>()
    private val lock = OptimisticLock()

    fun get(fromId: Int): ImmutableIntArrayList? =
        lock.optimisticRead { graph.get(fromId) }

    fun put(fromId: Int, toIds: ImmutableIntArrayList) {
        lock.blockingWrite { graph.put(fromId, toIds) }
    }
}
```

One more thing to consider. If you perform a large-scale read operation, for example counting the edges, you should use blocking read as the optimistic read most probably will not succeed and the whole operation will need to be restarted.

```kotlin
fun countEdges(): Int =
    lock.blockingRead { graph.sumOf { it.value.size } }
```

Also if you are going to perform multiple writes in a batch consider something like this to acquire lock just once.

```kotlin
inner class WriteScope internal constructor() {
    fun put(fromId: Int, toIds: ImmutableIntArrayList) {
        graph.put(fromId, toIds)
    }
}

fun write(block: WriteScope.() -> Unit) {
    lock.blockingWrite { WriteScope().block() }
}
```

Usage:

```kotlin
graph.write {
    put(1, ids1)
    put(2, ids2)
    put(3, ids3)
}
```
