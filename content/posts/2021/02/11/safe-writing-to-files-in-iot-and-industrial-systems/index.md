---
title: "Safe Writing to Files in IoT and Industrial Systems"
slug: "safe-writing-to-files-in-iot-and-industrial-systems"
date: "2021-02-11T09:12:23+00:00"
lastmod: "2021-02-11T09:13:06+00:00"
description: "Learn how to write to disk safely in Java, combining disk sync, shutdown hooks, and atomic renaming of files!"
authors:
  - "michael-roeschter"
image: "Favicon-3-2.png"
categories:
  - "Embedded"
  - "Performance"
  - "Security"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Especially on IoT devices, file corruption on shutdown is a common concern. This article discusses how to write to disk safely in Java, combining disk sync, shutdown hooks, and atomic renaming of files.

### Files On Disk Can Still Easily Become Corrupted

For performance optimization, file systems write to disks asynchronously resulting in potential corruption when a hard system shutdowns occurs through power off or crashes.

As this has become a rare experience for desktop and server users, it comes as a surprise to many developers working on IoT devices and industrial computers that hard power cuts are a common operational scenario and storage is far less robust than expected.

### General Problem

Operating systems cache writes to storage devices in RAM buffers. Additionally, spinning disks and SSDs have their own RAM buffers for write optimization. To clear buffers and guarantee safe physical storage, file systems provide the sync() command, which cascades through the hierarchy of storage devices.

It should be no surprise that the "sync" call is expensive, even more so on embedded systems where I/O is often slower for cost reasons and even writes to SSD are heavily buffered and delayed for the purpose of reducing wear on the SSD. As calling sync() for every write operation is a no go area due to poor performance in most scenarios, the developer needs to make the correct design choices.

### Useful Design Patterns

1. File flush() and sync()
2. Shutdown hooks and SIGTERM versus SIGKILL
3. Atomic renaming of files

#### File Flush() and Sync()

**FileOutputStream.flush()** or equivalent calls do not guarantee write to disk. It only ensures that whatever Java internal buffer is held for optimization is flushed to the underlaying storage. While using flush is strongly recommended before closing streams and files, it does not address our safe storage problem.

**FileDescriptor.sync()** will call sync() on OS level to synchronize the underlying file system buffer and disks. Note that this syncs one whole file system. All buffers, including those created by other programs will be synced.

The performance impact can be large and calls to sync() should be infrequent. If the system has multiple file system or storages, the call will generally only sync() one file system.

The flush() and sync() calls are often mixed up, but both are required to safely write a file to disk:

1. FileOutputStream out = new FileOutputStream(filename); //Open a file
2. BufferedOutputStream bout = new BufferedOutputStream(out);
3. bout.write(....) //repeat in loop - writes optimized through buffering
4. bout.flush(); //pushes our Java side buffers to the OS
5. out.getFD().sync(); //Makes sure all OS and disk buffer out done. Worst case this can take from 100s of ms on a servers to seconds on an embedded device.

Note that we skipped the file close(). We can safely write a portion of a file (e.g. a log) and expect the file system to "repair" our open file after a crash. But skipping the sync() or flush() will potentially leave the data corrupted even if we closed the file nicely.

#### Shutdown Hooks and SIGTERM vs. SIGKILL

Our application may be asked to shutdown before our naturally reaches a sync() point. As it is impractical to add shutdown related logic in each I/O related code, shutdown hooks run on regular(!) JVM shutdown an allow us to clean up and flush/sync databases and files.

```java
Runtime.getRuntime().addShutdownHook()
```


Note that Shutdown hooks run during a "soft kill" but do NOT run during a "hard kill" of the JVM.

The shutdown hook is triggered by:

1. System.exit()
2. "kill -SIGTERM" which is the default for kill

A Hard shutdown, skipping the shutdown hook, is triggered by:

1. System.halt()
2. "kill -9" or "kill -- SIGKILL"

#### Atomic Renaming of Files

Even with the above design patterns, when something goes wrong, our devices powers off or network connection is broken, files may still be corrupted and incomplete. How can we perform an atomic file operation which guarantees that the reading application always see a correct file?

You may expect that modern file system have transactions or locking mechanism for this purpose but few do and therefore its best not to rely on them. Renaming a file is for all practical purposes atomic on all file systems, local or remote and offers and age old way and portable way out of the dilemma.

1. Create and write a temporary file "abcd_temp"
2. (Optionally) Delete "config.dat_old"
3. (Optionally) rename "config.dat" to "config.dat_old"
4. Rename "abcd_temp" to "config.dat"

The above works for local file system as well as network protocos like NFS, SMB FTP, SCP...

### Notes

* When the reading application does not find a "config.dat", it can recover the previous "config.dat_old".
* This also works when the reading application is polling for files as it can never open a half written file by accident.
