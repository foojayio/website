---
title: "Faster Maven Builds (Part 2): Inside Docker"
date: "2021-10-14T08:33:09+00:00"
lastmod: "2021-10-14T09:15:39+00:00"
description: "Following on from different techniques to speed up Maven builds, I'd like to widen the scope and do the same for Maven builds inside Docker."
canonical: "https://blog.frankel.ch/faster-maven-builds/2/"
authors:
  - "nicolas-frankel"
image: "Apache_Maven_logo.svg.png"
categories:
  - "Maven"
  - "Performance"
tags:
related_posts:
  - "faster-maven-builds-1"
  - "understanding-apache-maven-part-1-the-basics"
  - "understanding-apache-maven-part-2-pom-hierarchy"
  - "boxlang-1-15-0-released-blazing-fast-strings-runtime-portability-and-much-more"
frozen: false
---

Following on from [different techniques](https://foojay.io/today/faster-maven-builds-1/) to speed up your Maven builds, I'd like to widen the scope and do the same for Maven builds *inside Docker*.

Between each run, we change the source code by adding a single blank line; between each section, we remove all built images, including the intermediate ones that are the results of the multi-stage build. The idea is to avoid reusing a previously built image.

## Baseline

To compute a helpful baseline, we need a sample project. I created [one](https://github.com/nfrankel/fast-maven-builds) just for this purpose: it's a relatively small Kotlin project.

Here's the relevant `Dockerfile`:

```dockerfile
FROM openjdk:11-slim-buster as build                         #1

COPY .mvn .mvn                                               #2
COPY mvnw .                                                  #2
COPY pom.xml .                                               #2
COPY src src                                                 #2

RUN ./mvnw -B package                                        #3

FROM openjdk:11-jre-slim-buster                              #4

COPY --from=build target/fast-maven-builds-1.0.jar .         #5

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "fast-maven-builds-1.0.jar"]     #6
```

1. Start from a JDK image for the packaging step
2. Add required resources
3. Create the JAR
4. Start from a JRE for image creation step
5. Copy the JAR from the previous step
6. Set the entry point

Let's execute the build:

```bash
time DOCKER_BUILDKIT=0 docker build -t fast-maven:1.0 . #1
```

1. Forget the environment variable for now, as I'll explain in the next section

Here are the results of the five runs:

```
* 0.36s user 0.53s system 0% cpu 1:53.06 total
* 0.36s user 0.56s system 0% cpu 1:52.50 total
* 0.35s user 0.55s system 0% cpu 1:56.92 total
* 0.36s user 0.56s system 0% cpu 2:04.55 total
* 0.38s user 0.61s system 0% cpu 2:04.68 total
```

## Buildkit for the win

The last command line used the `DOCKER_BUILDKIT` environment variable. It's the way to tell Docker to use the legacy engine. If you didn't update Docker for some time, it's the engine that you're using. Nowadays, [BuildKit](https://github.com/moby/buildkit) has superseded it and is the new default.

BuildKit brings several performance improvements:

* Automatic garbage collection
* Concurrent dependency resolution
* Efficient instruction caching
* Build cache import/export
* etc.

Let's re-execute the previous command on the new engine:

```bash
time docker build -t fast-maven:1.1 .
```

Here's an excerpt of the console log of the first run:

```
...
 => => transferring context: 4.35kB
 => [build 2/6] COPY .mvn .mvn
 => [build 3/6] COPY mvnw .
 => [build 4/6] COPY pom.xml .
 => [build 5/6] COPY src src
 => [build 6/6] RUN ./mvnw -B package
...

0.68s user 1.04s system 1% cpu 2:06.33 total
```

The following executions of the same command have a slightly different output:

```
...
 => => transferring context: 1.82kB
 => CACHED [build 2/6] COPY .mvn .mvn
 => CACHED [build 3/6] COPY mvnw .
 => CACHED [build 4/6] COPY pom.xml .
 => [build 5/6] COPY src src
 => [build 6/6] RUN ./mvnw -B package
...
```

Remember that we change the source code between runs. Files that we do not change, namely `.mvn`, `mvnw` and `pom.xml`, are *cached* by BuildKit. But these resources are small, so that caching doesn't significantly improve the build time.

```
* 0.69s user 1.01s system 1% cpu 2:05.08 total
* 0.65s user 0.95s system 1% cpu 1:58.51 total
* 0.68s user 0.99s system 1% cpu 1:59.31 total
* 0.64s user 0.95s system 1% cpu 1:59.82 total
```

A fast glance at the logs reveals that the biggest bottleneck in the build is the download of all dependencies (including plugins). It occurs every time we change the source code. That's the reason why BuildKit doesn't improve the performance.

## Layers, layers, layers

We should focus our efforts on the dependencies. For that, we can leverage *layers* and split the build into two steps:

* In the first step, we download dependencies
* In the second one, we do the proper packaging

Each step creates a layer, the second depending on the first.

With layering, if we change the source code in the second layer, the first layer is not impacted and can be reused. We don't need to download dependencies again. The new `Dockerfile` looks like:

```dockerfile
FROM openjdk:11-slim-buster as build

COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .

RUN ./mvnw -B dependency:go-offline                          #1

COPY src src

RUN ./mvnw -B package                                        #2

FROM openjdk:11-jre-slim-buster

COPY --from=build target/fast-maven-builds-1.2.jar .

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "fast-maven-builds-1.2.jar"]
```

1. The `go-offline` goal downloads all dependencies and plugins
2. At this point, all dependencies are available

Note that `go-offline` doesn't download everything. The command won't run successfully if you try to use the `-o` option (for offline). It's [a well-known old bug](https://issues.apache.org/jira/browse/MDEP-82). In all cases, it's "good enough".

Let's run the build:

```bash
time docker build -t fast-maven:1.2 .
```

The first run takes significantly more time than the baseline:

```
0.84s user 1.21s system 1% cpu 2:35.47 total
```

However, the subsequent builds are much faster. Changing the source code only affects the second layer and doesn't trigger the download of (most) dependencies:

```
* 0.23s user 0.36s system 5% cpu 9.913 total
* 0.21s user 0.33s system 5% cpu 9.923 total
* 0.22s user 0.38s system 6% cpu 9.990 total
* 0.21s user 0.34s system 5% cpu 9.814 total
* 0.22s user 0.37s system 5% cpu 10.454 total
```

## Volume mount in build

Layering the build improved the build time drastically. We can change the source code and keep it low. There's one remaining issue, though. Changing a single dependency invalidates the layer, so we need to download all of them again.

Fortunately, BuildKit introduces **volumes mount during the build** (and not only during the run). Several types of mounts are available, but the one that interests us is the [cache mount](https://github.com/moby/buildkit/blob/master/frontend/dockerfile/docs/syntax.md#run---mounttypecache). It's an *experimental* feature, so you need to explicitly opt-in:

```dockerfile
# syntax=docker/dockerfile:experimental                      #1
FROM openjdk:11-slim-buster as build

COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
COPY src src

RUN --mount=type=cache,target=/root/.m2,rw ./mvnw -B package #2

FROM openjdk:11-jre-slim-buster

COPY --from=build target/fast-maven-builds-1.3.jar .

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "fast-maven-builds-1.3.jar"]
```

1. Opt-in to experimental features
2. Build using the cache

It's time to run the build:

```bash
time docker build -t fast-maven:1.3 .
```

The build time is higher than for the regular build but still lower than the layers build:

```
0.71s user 1.01s system 1% cpu 1:50.50 total
```

The following builds are on par with layers:

```
* 0.22s user 0.33s system 5% cpu 9.677 total
* 0.30s user 0.36s system 6% cpu 10.603 total
* 0.24s user 0.37s system 5% cpu 10.461 total
* 0.24s user 0.39s system 6% cpu 10.178 total
* 0.24s user 0.35s system 5% cpu 10.283 total
```

However, as opposed to layers, we only need to download updated dependencies. Here, let's change Kotlin's version from `1.5.30` to `1.5.31`:

```xml
<properties>
    <kotlin.version>1.5.31</kotlin.version>
</properties>
```

It's a huge improvement regarding the build time:

```
* 0.41s user 0.57s system 2% cpu 44.710 total
```

## Considering the Maven daemon

In the previous post regarding [regular Maven builds](https://blog.frankel.ch/faster-maven-builds/1/), I mentioned the Maven daemon. Let's change our build accordingly:

```dockerfile
FROM openjdk:11-slim-buster as build

ADD https://github.com/mvndaemon/mvnd/releases/download/0.6.0/mvnd-0.6.0-linux-amd64.zip . #1

RUN apt-get update \                                         #2
 && apt-get install unzip \                                  #3
 && mkdir /opt/mvnd \                                        #4
 && unzip mvnd-0.6.0-linux-amd64.zip \                       #5
 && mv mvnd-0.6.0-linux-amd64/* /opt/mvnd                    #6

COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
COPY src src

RUN /opt/mvnd/bin/mvnd -B package                            #7

FROM openjdk:11-jre-slim-buster

COPY --from=build target/fast-maven-builds-1.4.jar .

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "fast-maven-builds-1.4.jar"]
```

1. Download the latest version of the Maven daemon
2. Refresh the package index
3. Install `unzip`
4. Create a dedicated folder
5. Extract the archive that we downloaded in step #1
6. Move the content of the extracted archive to the previously created folder
7. Use `mvnd` instead of the Maven wrapper

Let's run the build now:

```bash
docker build -t fast-maven:1.4 .
```

The log outputs the following:

```
* 0.70s user 1.01s system 1% cpu 1:51.96 total
* 0.72s user 0.98s system 1% cpu 1:47.93 total
* 0.66s user 0.93s system 1% cpu 1:46.07 total
* 0.76s user 1.04s system 1% cpu 1:50.35 total
* 0.80s user 1.18s system 1% cpu 2:01.45 total
```

There's no significant improvement compared to the baseline.

I tried to create a dedicated `mvnd` image and use it as a parent image:

```dockerfile
# docker build -t mvnd:0.6.0 .
FROM openjdk:11-slim-buster as build

ADD https://github.com/mvndaemon/mvnd/releases/download/0.6.0/mvnd-0.6.0-linux-amd64.zip .

RUN --mount=type=cache,target=/var/cache/apt,rw apt-get update \
 && apt-get install unzip \
 && mkdir /opt/mvnd \
 && unzip mvnd-0.6.0-linux-amd64.zip \
 && mv mvnd-0.6.0-linux-amd64/* /opt/mvnd
```

```dockerfile
# docker build -t fast-maven:1.5 .
FROM mvnd:0.6.0 as build

# ...
```

This approach changes the output in any significant way.

`mvnd` is only good when the daemon is up during several runs. I found no way to do that with Docker. If you've any idea on how to achieve it, please tell me; extra points if you can point me to an implementation.

Here's the summary of all execution times:

|                        |  Baseline  | BuildKit |   Layers   | Volume mount |  mvnd  |
|         #1 (s)         |   113.06   |  125.08  | ~~155.47~~ |  ~~110.5~~   | 111.96 |
|         #2 (s)         |   112.5    |  118.51  |    9.91    |     9.68     | 107.93 |
|         #3 (s)         |   116.92   |  119.31  |    9.92    |     10.6     | 106.07 |
|         #4 (s)         |   124.55   |  119.82  |    9.99    |    10.46     | 110.35 |
|         #5 (s)         |   124.68   |          |    9.81    |    10.18     | 121.45 |
|         #6 (s)         |            |          |   10.45    |    10.28     |        |
|         #7 (s)         |            |          |            |  ~~44.71~~   |        |
|      Average (s)       | **118.34** |  120.68  |    9.91    |    10.24     | 111.55 |
|       Deviation        |   28.55    |   6.67   |    0.01    |     0.10     | 111.47 |
| Gain from baseline (s) |     0      |  -2.34   |   108.43   |  **108.10**  |  6.79  |
|         % gain         |   0.00%    |  -1.98%  |   91.63%   |  **91.35%**  | 5.74%  |
|------------------------|------------|----------|------------|--------------|--------|

## Conclusion

Speeding up the performance of Maven builds inside of Docker is pretty different from regular builds. In Docker, the limiting factor is the download speed of dependencies If you're stuck on an old version, you need to use layers to cache dependencies.

With BuildKit, I recommend using the new cache mount capability to avoid downloading all dependencies if the layer is invalidated.

The complete source code for this post can be found on [Github](https://github.com/ajavageek/fast-maven-builds) in Maven format.

**To go further:**

* [Faster Maven builds](https://blog.frankel.ch/faster-maven-builds/1/)
* [Introducing BuildKit](https://blog.mobyproject.org/introducing-buildkit-17e056cc5317)
* [Docker Layers Explained](https://mydeveloperplanet.com/2019/03/13/docker-layers-explained/)
* [Build Mounts](https://github.com/moby/buildkit/blob/master/frontend/dockerfile/docs/syntax.md#build-mounts-run---mount)

*Originally published at [A Java Geek](https://blog.frankel.ch/faster-maven-builds/2/) on October 10^th^, 2021*
