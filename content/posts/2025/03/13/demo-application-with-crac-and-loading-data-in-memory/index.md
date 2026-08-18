---
title: "Demo Application with CRaC and Loading Data in Memory"
slug: "demo-application-with-crac-and-loading-data-in-memory"
date: "2025-03-13T08:11:19+00:00"
lastmod: "2025-03-13T08:11:21+00:00"
description: "Take a look at a nice illustration of how time-consuming processes can be stored in a checkpoint in CRaC."
canonical: "https://webtechie.be/post/2025-02-19-demo-application-with-crac-and-loading-data-in-memory/"
authors:
  - "frankdelporte"
image: "thumbnail-crac-data-in-memory.jpg"
categories:
  - "CRaC"
  - "Raspberry Pi"
tags:
related_posts:
  - "how-to-run-a-java-application-with-crac-in-a-docker-container"
  - "introducing-the-openjdk-coordinated-restore-at-checkpoint-project"
  - "running-a-crac-java-application-on-raspberry-pi"
  - "java-21-on-raspberry-pi-zero-2-is-back-in-business"
enlighterjs: true
frozen: false
---

**[Coordinated Restore at Checkpoint (CRaC)](https://docs.azul.com/core/crac/crac-introduction) is a JDK project initiated by Azul. With CRaC, you can start Java programs with a shorter time to the first transaction and less time and resources to achieve full code speed. This is achieved by taking a snapshot (checkpoint) of a fully warmed-up Java process and launching one or more new JVMs from that snapshot.**

I have been experimenting with CRaC on a Raspberry Pi in 2023 and blogged about it, [first when it didn't work yet](https://webtechie.be/post/2023-06-15-crac-on-raspberry-pi/), and a [second time when it did work perfectly](https://webtechie.be/post/2023-10-16-crac-on-raspberry-pi-update/).

Improving the startup time of Java applications is only one use case for CRaC. But in this post, I describe a different kind of example to illustrate how it can also help speed up applications that need to load a lot of data into memory. Below, you can find the detailed explanation, and I recorded the demo on a Raspberry Pi and published it on YouTube:

{{< youtube RW2K5qC9_v0 >}}

## Demo Application

The [sources of the demo application can be found on GitHub](https://github.com/FDelporte/crac-example). They are based on another example from the CRaC repository at [github.com/CRaC/example-jetty](https://github.com/CRaC/example-jetty), and the PostgreSQL integration is based on [stackabuse.com/working-with-postgresql-in-java](https://stackabuse.com/working-with-postgresql-in-java/). The CSV files located in `src/main/resources/data` were downloaded from [datablist.com](https://www.datablist.com/learn/csv/download-sample-csv-files).

### Goal of the Application

This demo application shows a few use cases:

* It provides an HTTP endpoint to load data from zipped CSV files and returns the first 100 records.
* The data is unzipped, and each line is converted into a Java object. This takes a long time - on purpose for the demo - for a large dataset when a file is requested for the first time.
* After the first request of a file, the data is stored into memory, so each new request can quickly return the first 100 records.
* The duration of all actions is stored in a database. The stored logs can also be retrieved with the HTTP endpoint.

In the code, a few CRaC-specific implementations can be found:

* The database connection is closed in the `beforeCheckpoint` method, and reopened in the `afterRestore` method. See [DatabaseManager.java](https://github.com/FDelporte/crac-example/blob/main/src/main/java/be/webtechie/crac/example/manager/DatabaseManager.java).
* Because the CSV data is stored in memory in [CsvManager.java](https://github.com/FDelporte/crac-example/blob/main/src/main/java/be/webtechie/crac/example/manager/CsvManager.java), it demonstrates that the application returns data immediately in the HTTP calls after restore from checkpoint, as it's not needed to reload from the zipped CSV files. This is different behavior compared to starting the application from JAR.

After starting the application, you can find all available endpoints on <http://localhost:8080>.

### Running the Application

As I like to experiment with Java on the Raspberry Pi, and the full CRaC-functionality is only available on a Linux machine, I ran this application on a Raspberry Pi 4 with:

* Raspberry Pi OS, 64-bit, Bookworm edition, released on October 11, 2023.
* PostgreSQL with the database `crac` and table `app_log` (see the README of the repository for all details).
* Azul Zulu Build of OpenJDK, version 21 with CRaC:

```
$ sdk install java 21.crac-zulu
```

#### Build and Run From JAR

I compiled the application on the Raspberry Pi and executed it with the following command which specifies the directory where the checkpoint must be created:

```
$ git clone https://github.com/FDelporte/crac-example.git
$ cd crac-example
$ mvn package
$ java -XX:CRaCCheckpointTo=cr -jar target/crac-example.jar
```

These are the durations needed for the HTTP endpoints to respond, as logged in the database. As you can see, the biggest file needs about 12 seconds to unzip and return the results.

```
duration=11800, description=Handled request for /files/organizations-1000000.csv
duration=8721, description=Data was converted to Java objects from organizations-1000000.csv
duration=3054, description=ZIP was unpacked from organizations-1000000.csv
duration=5197, description=Handled request for /files/organizations-500000.csv
duration=3609, description=Data was converted to Java objects from organizations-500000.csv
duration=1567, description=ZIP was unpacked from organizations-500000.csv
duration=1482, description=Handled request for /files/organizations-100000.csv
duration=1080, description=Data was converted to Java objects from organizations-100000.csv
duration=372, description=ZIP was unpacked from organizations-100000.csv
duration=373, description=Handled request for /files/organizations-10000.csv
duration=309, description=Data was converted to Java objects from organizations-10000.csv
duration=35, description=ZIP was unpacked from organizations-10000.csv
duration=222, description=Handled request for /files/organizations-1000.csv
duration=94, description=Data was converted to Java objects from organizations-1000.csv
duration=7, description=ZIP was unpacked from organizations-1000.csv
duration=0, description=Started from main
duration=0, description====================================================]
```

Once this is done, we can create a checkpoint by opening a second terminal and executing the following command:

```
$ jcmd target/crac-example.jar JDK.checkpoint
```

In the first terminal, you can see what's happening during the checkpoint creation, and the application getting terminated at the end if the checkpoint creation was successful.

```
Oct 17, 2023 7:21:29 PM jdk.internal.crac.LoggerContainer info
INFO: Starting checkpoint
17/10/2023 19:21 | ServerManager                       | beforeCheckpoint     | INFO     | Executing beforeCheckpoint
2023-10-17 19:21:29.978:INFO:oejs.AbstractConnector:Attach Listener: Stopped ServerConnector@77a98a6a{HTTP/1.1, (http/1.1)}{0.0.0.0:8080}
17/10/2023 19:21 | DatabaseManager                     | beforeCheckpoint     | INFO     | Executing beforeCheckpoint
Oct 17, 2023 7:21:31 PM jdk.internal.crac.LoggerContainer info
INFO: /home/crac/crac-example/target/dependency/log4j-api-2.20.0.jar is recorded as always available on restore
Oct 17, 2023 7:21:31 PM jdk.internal.crac.LoggerContainer info
INFO: /home/crac/crac-example/target/dependency/log4j-core-2.20.0.jar is recorded as always available on restore
Oct 17, 2023 7:21:31 PM jdk.internal.crac.LoggerContainer info
INFO: /home/crac/crac-example/target/dependency/checker-qual-3.31.0.jar is recorded as always available on restore
Oct 17, 2023 7:21:31 PM jdk.internal.crac.LoggerContainer info
INFO: /home/crac/crac-example/target/dependency/postgresql-42.6.0.jar is recorded as always available on restore
Oct 17, 2023 7:21:31 PM jdk.internal.crac.LoggerContainer info
INFO: /home/crac/crac-example/target/dependency/crac-1.4.0.jar is recorded as always available on restore
Oct 17, 2023 7:21:31 PM jdk.internal.crac.LoggerContainer info
INFO: /home/crac/crac-example/target/dependency/jetty-io-9.4.51.v20230217.jar is recorded as always available on restore
Oct 17, 2023 7:21:31 PM jdk.internal.crac.LoggerContainer info
INFO: /home/crac/crac-example/target/dependency/jetty-util-9.4.51.v20230217.jar is recorded as always available on restore
Oct 17, 2023 7:21:31 PM jdk.internal.crac.LoggerContainer info
INFO: /home/crac/crac-example/target/dependency/jetty-http-9.4.51.v20230217.jar is recorded as always available on restore
Oct 17, 2023 7:21:31 PM jdk.internal.crac.LoggerContainer info
INFO: /home/crac/crac-example/target/dependency/javax.servlet-api-3.1.0.jar is recorded as always available on restore
Oct 17, 2023 7:21:31 PM jdk.internal.crac.LoggerContainer info
INFO: /home/crac/crac-example/target/dependency/jetty-server-9.4.51.v20230217.jar is recorded as always available on restore
Oct 17, 2023 7:21:31 PM jdk.internal.crac.LoggerContainer info
INFO: /home/crac/crac-example/target/crac-example.jar is recorded as always available on restore
Killed
```

#### Run From Checkpoint

Our checkpoint has been created in the `cr` directory, so we can now restart it with the following command.

```
$ java -XX:CRaCRestoreFrom=cr

17/10/2023 19:22 | DatabaseManager                     | afterRestore         | INFO     | Executing afterRestore
17/10/2023 19:22 | DatabaseManager                     | initConnection       | WARN     | Setting up database connection
17/10/2023 19:22 | DatabaseManager                     | initConnection       | INFO     | Database connection status: {ApplicationName=PostgreSQL JDBC Driver}
17/10/2023 19:22 | ServerManager                       | afterRestore         | INFO     | Executing afterRestore
2023-10-17 19:22:48.800:INFO:oejs.AbstractConnector:Attach Listener: Started ServerConnector@77a98a6a{HTTP/1.1, (http/1.1)}{0.0.0.0:8080}
```

As you can see, within a second the `afterRestore` methods get executed, meaning the application is up-and-running in the same state as it was just before the checkpoint was created. As unzipping and converting the CSV files into Java objects was already done, the HTTP endpoint can respond immediately. For the largest file this means a response in 7 milliseconds instead of over 10 seconds.

```
duration=7, description=Handled request for /files/organizations-1000000.csv
duration=7, description=Handled request for /files/organizations-500000.csv
duration=6, description=Handled request for /files/organizations-100000.csv
duration=8, description=Handled request for /files/organizations-10000.csv
duration=15, description=Handled request for /files/organizations-1000.csv
duration=0, description=Reopened DB connection after restore
duration=0, description====================================================
```

## Conclusion

Of course, this time-consuming CSV-reading process is not a typical use case, but it's a nice illustration of how time-consuming processes can be stored in a checkpoint.

In December '23, I shared the "Things I learned On OpenJDK While Experimenting With CRaC for Fast Java Startup" at the Brussels Java User Group (BruJUG). The complete session has been recorded and is available on YouTube:

{{< youtube RqtFXkznX_o >}}
