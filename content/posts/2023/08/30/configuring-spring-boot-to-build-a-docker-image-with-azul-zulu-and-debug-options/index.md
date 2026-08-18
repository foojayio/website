---
title: "Spring Boot Configuration for Docker Images with Azul Zulu"
date: "2023-08-30T16:14:52+00:00"
lastmod: "2023-08-30T16:14:54+00:00"
description: "The Spring Boot Maven Plugin makes creating a Docker image from your app very easy! Let's see how define the Java runtime used and more."
canonical: "https://www.azul.com/blog/configuring-spring-boot-to-build-a-docker-image-with-azul-zulu-and-debug-options/"
authors:
  - "frankdelporte"
image: "azul-zulu-springboot-docker.jpg"
categories:
  - "Spring"
  - "Tutorials"
related_posts:
  - "5-tips-to-create-secure-docker-images-for-java-applications"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "a-simple-service-with-spring-boot"
  - "starting-docker-desktop-with-spring-boot"
frozen: false
---

*The Spring Boot Maven Plugin makes creating a Docker image from your application very easy! In this article, we give you some extra tips and examples for configuring Spring Boot to define the Java runtime used in such a Spring Boot Docker image and explain how to add additional environmental options to ease debugging.*
![](azul-zulu-springboot-docker-1024x400.jpg)
>
> ### For this post, I got the help of [DaShaun Carter](https://twitter.com/dashaun), Spring Developer Advocate, who immediately jumped in when I raised some questions about these configuration options. Thanks, DaShaun!
>
## Spring Boot Petclinic Project

To illustrate our approach, we'll use the Spring Boot Petclinic project, a Spring Boot application built using Maven or Gradle, to demonstrate how a Spring Boot project is set up fully and how the code is structured and must be packaged.

You can get the complete project, including a lot of configurations for various use cases, from GitHub:

```
$ git clone https://github.com/spring-projects/spring-petclinic
$ cd spring-petclinic
```

## Modify and extend the default settings

We just need a few additional configurations in the `spring-boot-maven-plugin` section in the `pom.xml` file of the project to modify the OpenJDK distribution in the generated Docker image and add additional debug settings. Don't change the executions; just insert the configuration section. The goal of this Docker image is to use the Azulu Zulu Build of OpenJDK as the runtime and to include all possible debug and test tools, so it has all the following options. Of course, which ones you use will depend on your use case.

```
    <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <configuration>
            <image>
                <buildpacks>
                    <buildpack>paketobuildpacks/azul-zulu</buildpack>
                    <buildpack>paketobuildpacks/java</buildpack>
                </buildpacks>

                <env>
                    <BP_JVM_VERSION>17</BP_JVM_VERSION>
                    <BP_JVM_TYPE>JDK</BP_JVM_TYPE>

                    <BPE_DELIM_JAVA_TOOL_OPTIONS xml:space="preserve"> </BPE_DELIM_JAVA_TOOL_OPTIONS>
                    <BPE_APPEND_JAVA_TOOL_OPTIONS>-Xlog:gc:/tmp/gc.log</BPE_APPEND_JAVA_TOOL_OPTIONS>

                    <BPE_DEFAULT_BPL_DEBUG_ENABLED>true</BPL_DEBUG_ENABLED>
                    <BPE_DEFAULT_BPL_DEBUG_PORT>8000</BPL_DEBUG_PORT> <!-- This is the default value -->
                    <BPE_DEFAULT_BPL_JMX_ENABLED>true</BPL_JMX_ENABLED>
                    <BPE_DEFAULT_BPL_JMX_PORT>5000</BPL_JMX_PORT> <!-- This is the default value -->
                    <BPE_DEFAULT_BPL_JAVA_NMT_ENABLED>true</BPL_JAVA_NMT_ENABLED> <!-- This is the default value -->
                    <BPE_DEFAULT_BPL_JFR_ENABLED>true</BPL_JFR_ENABLED> 
                    <BPE_DEFAULT_BPL_JFR_ARGS>dumponexit=true,filename=/tmp/rec.jfr,duration=600s</BPL_JFR_ARGS>
                </env>
            </image>
        </configuration>
        <executions>
            ...
        </executions>
    </plugin>
```

To better understand this section, we need to look at all the different options separately:

* The `<buildpacks>` section defines which buildpacks are used to generate the Docker image.
  * On [github.com/paketo-buildpacks/azul-zulu](https://github.com/paketo-buildpacks/azul-zulu) you can see a list of available configurations.
* The `<env>` section groups all environment-related changes, with different targets described on the [Paketo Docs \> How To Configure Paketo Buildpacks \> Environment Variables](https://paketo.io/docs/howto/configuration/#environment-variables).
  * **BP_**: variables used to configure the build process itself, e.g. setting the Java version.
  * **BPE_**: image embedded environment variables.
  * **BPL_** : runtime features of the app image, which are set as environment variables in the app container. You must append them with `BPE_DEFAULT_` to "bake" them into the container image to automatically use them.

### Building the Docker image

At this point, building the Docker image is a single-line command, and the output will show a few references to the Azul JDK and our settings after the various test cycles:

```
$ ./mvnw spring-boot:build-image

[INFO] Scanning for projects...
[INFO] 
[INFO] ------------< org.springframework.samples:spring-petclinic >------------
[INFO] Building petclinic 3.1.0-SNAPSHOT
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 

...

[INFO] --- spring-boot:3.1.1:build-image (default-cli) @ spring-petclinic ---
[INFO] Building image 'docker.io/library/spring-petclinic:3.1.0-SNAPSHOT'
[INFO] 
[INFO]  > Pulling builder image 'docker.io/paketobuildpacks/builder:base' 100%

...

[INFO]  > Pulling buildpack image 'docker.io/paketobuildpacks/azul-zulu:latest' 100%
[INFO]  > Pulled buildpack image 'paketobuildpacks/azul-zulu@sha256:79419af00c95f85c088e68808f61b2486c39a7e12a0033995970c97e95408069'

...

[INFO]  > Running creator
[INFO]     [creator]     ===> ANALYZING
[INFO]     [creator]     Image with name "docker.io/library/spring-petclinic:3.1.0-SNAPSHOT" not found
[INFO]     [creator]     ===> DETECTING
[INFO]     [creator]     8 of 27 buildpacks participating
[INFO]     [creator]     paketo-buildpacks/azul-zulu             10.1.5

...

[INFO]     [creator]     ===> BUILDING
[INFO]     [creator]     
[INFO]     [creator]     Paketo Buildpack for Azul Zulu 10.1.5
[INFO]     [creator]       https://github.com/paketo-buildpacks/azul-zulu
[INFO]     [creator]       Build Configuration:
[INFO]     [creator]         $BP_JVM_JLINK_ARGS           --no-man-pages --no-header-files --strip-debug --compress=1  configure custom link arguments (--output must be omitted)
[INFO]     [creator]         $BP_JVM_JLINK_ENABLED        false                                                        enables running jlink tool to generate custom JRE
[INFO]     [creator]         $BP_JVM_TYPE                 JDK                                                          the JVM type - JDK or JRE
[INFO]     [creator]         $BP_JVM_VERSION              17                                                           the Java version
[INFO]     [creator]       Launch Configuration:
[INFO]     [creator]         $BPL_DEBUG_ENABLED           true                                                         enables Java remote debugging support
[INFO]     [creator]         $BPL_DEBUG_PORT              8000                                                         configure the remote debugging port
[INFO]     [creator]         $BPL_DEBUG_SUSPEND           false                                                        configure whether to suspend execution until a debugger has attached
[INFO]     [creator]         $BPL_HEAP_DUMP_PATH                                                                       write heap dumps on error to this path
[INFO]     [creator]         $BPL_JAVA_NMT_ENABLED        true                                                         enables Java Native Memory Tracking (NMT)
[INFO]     [creator]         $BPL_JAVA_NMT_LEVEL          summary                                                      configure level of NMT, summary or detail
[INFO]     [creator]         $BPL_JFR_ARGS                dumponexit=true,filename=/tmp/rec.jfr,duration=600s          configure custom Java Flight Recording (JFR) arguments
[INFO]     [creator]         $BPL_JFR_ENABLED             true                                                         enables Java Flight Recording (JFR)
[INFO]     [creator]         $BPL_JMX_ENABLED             true                                                         enables Java Management Extensions (JMX)
[INFO]     [creator]         $BPL_JMX_PORT                5000                                                         configure the JMX port
[INFO]     [creator]         $BPL_JVM_HEAD_ROOM           0                                                            the headroom in memory calculation
[INFO]     [creator]         $BPL_JVM_LOADED_CLASS_COUNT  35% of classes                                               the number of loaded classes in memory calculation
[INFO]     [creator]         $BPL_JVM_THREAD_COUNT        250                                                          the number of threads in memory calculation
[INFO]     [creator]         $JAVA_TOOL_OPTIONS                                                                        the JVM launch flags
[INFO]     [creator]         Using Java version 17 from BP_JVM_VERSION
[INFO]     [creator]       A JDK was specifically requested by the user, however a JRE is available. Using a JDK at runtime has security implications.
[INFO]     [creator]       Azul Zulu JDK 17.0.7: Contributing to layer

...

[INFO] Successfully built image 'docker.io/library/spring-petclinic:3.1.0-SNAPSHOT'
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  01:29 min
[INFO] Finished at: 2023-07-03T11:26:39+02:00
[INFO] ------------------------------------------------------------------------
```

## Checking the created Docker images

During the build process, various images were downloaded and created. As you can see, there are more than we may expect, but a few of these are used during the unit tests.

```
$ docker images

REPOSITORY                   TAG              IMAGE ID       CREATED        SIZE
paketobuildpacks/run         base-cnb         f2e5000af0cb   3 days ago     87.1MB
postgres                     15.3             1921dda0e2c5   2 weeks ago    412MB
mysql                        5.7              2be84dd575ee   2 weeks ago    569MB
testcontainers/ryuk          0.5.1            ec913eeff75a   6 weeks ago    12.7MB
paketobuildpacks/builder     base             99ec7fb86b9d   43 years ago   1.34GB
paketobuildpacks/azul-zulu   latest           276db25e20db   43 years ago   10.4MB
paketobuildpacks/java        latest           2ddc6cc7d346   43 years ago   207MB
spring-petclinic             3.1.0-SNAPSHOT   c05d70c78109   43 years ago   496MB
```

## Running the Docker image

You can now start the Docker image. Three ports are configured for various use cases:

* **8080**: to access the website
* **8000**: debug port
* **5000**: JMX port

Via the terminal, start the Docker image.

```
$ docker run -p 8080:8080 -p 8000:8000 -p 5000:5000 --name petclinic spring-petclinic:3.1.0-SNAPSHOT

Setting Active Processor Count to 16
Debugging enabled on port *:8000
Enabling Java Flight Recorder with args: dumponexit=true,filename=/tmp/rec.jfr,duration=600s
JMX enabled on port 5000
...
[0.349s][info][jfr,startup] Started recording 1. The result will be written to:
[0.349s][info][jfr,startup] 
[0.349s][info][jfr,startup] /tmp/rec.jfr

              |\      _,,,--,,_
             /,`.-'`'   ._  \-;;,_
  _______ __|,4-  ) )_   .;.(__`'-'__     ___ __    _ ___ _______
 |       | '---''(_/._)-'(_\_)   |   |   |   |  |  | |   |       |
 |    _  |    ___|_     _|       |   |   |   |   |_| |   |       | __ _ _
 |   |_| |   |___  |   | |       |   |   |   |       |   |       | \ \ \ \
 |    ___|    ___| |   | |      _|   |___|   |  _    |   |      _|  \ \ \ \
 |   |   |   |___  |   | |     |_|       |   | | |   |   |     |_    ) ) ) )
 |___|   |_______| |___| |_______|_______|___|_|  |__|___|_______|  / / / /
 ==================================================================/_/_/_/

:: Built with Spring Boot :: 3.1.1

2023-07-06T10:55:23.329Z  INFO 1 --- [           main] o.s.s.petclinic.PetClinicApplication     : Starting PetClinicApplication v3.1.0-SNAPSHOT using Java 17.0.7 with PID 1 (/workspace/BOOT-INF/classes started by cnb in /workspace)
...
```

## Validating the Docker image

At this point, you can perform various steps to validate the Docker image we created with the additional settings:

* Open a browser and point it to `localhost:8080`. The Petclinic web interface allows you to browse pet owners and veterinarians.
* Open a second terminal and perform the following checks inside the running Docker:

```
$ docker exec -it petclinic sh

# Check the Java version
$ /layers/paketo-buildpacks_azul-zulu/jdk/bin/java -version
openjdk version "17.0.7" 2023-04-18 LTS
OpenJDK Runtime Environment Zulu17.42+19-CA (build 17.0.7+7-LTS)
OpenJDK 64-Bit Server VM Zulu17.42+19-CA (build 17.0.7+7-LTS, mixed mode, sharing)

# Check if we can execute JCMD which is not included in the default build with a JRE
$ /layers/paketo-buildpacks_azul-zulu/jdk/bin/jcmd
1 org.springframework.boot.loader.JarLauncher
181 jdk.jcmd/sun.tools.jcmd.JCmd

# Check the generated files we can use later for further investigation
# The application must be running for a longer time, before data is saved in rec.jfr
$ ls -l /tmp
total 20
drwxr-xr-x 2 cnb cnb 4096 Jul 10 06:44 2023_07_10_06_44_57_1
-rw-r--r-- 1 cnb cnb 1819 Jul 10 06:45 gc.log
drwxr-xr-x 2 cnb cnb 4096 Jul 10 06:44 hsperfdata_cnb
-rw-r--r-- 1 cnb cnb    0 Jul 10 06:44 rec.jfr
drwx------ 2 cnb cnb 4096 Jul 10 06:44 tomcat-docbase.8080.16382390067193381204
drwx------ 3 cnb cnb 4096 Jul 10 06:44 tomcat.8080.7527925084169386730

# Check the Java info, using Process ID 1 we found with jcmd
$ /layers/paketo-buildpacks_azul-zulu/jdk/bin/jinfo 1

Java System Properties:
#Mon Jul 10 06:46:46 UTC 2023
com.sun.management.jmxremote.rmi.port=5000
java.specification.version=17
sun.jnu.encoding=ANSI_X3.4-1968
com.sun.management.jmxremote.authenticate=false
java.class.path=/workspace
java.vm.vendor=Azul Systems, Inc.
...
java.library.path=/layers/paketo-buildpacks_azul-zulu/jdk/lib\:/usr/java/packages/lib\:/usr/lib64\:/lib64\:/lib\:/usr/lib
...

VM Flags:
-XX:ActiveProcessorCount=16 -XX:CICompilerCount=12 -XX:CompressedClassSpaceSize=117440512 -XX:ConcGCThreads=3 -XX:+ExitOnOutOfMemoryError -XX:+FlightRecorder -XX:G1ConcRefinementThreads=13 -XX:G1EagerReclaimRemSetThreshold=128 -XX:G1HeapRegionSize=16777216 -XX:GCDrainStackTargetSize=64 -XX:InitialHeapSize=536870912 -XX:+ManagementServer -XX:MarkStackSize=4194304 -XX:MaxDirectMemorySize=10485760 -XX:MaxHeapSize=23857201152 -XX:MaxMetaspaceSize=133729280 -XX:MaxNewSize=14310965248 -XX:MinHeapDeltaBytes=16777216 -XX:MinHeapSize=16777216 -XX:NativeMemoryTracking=summary -XX:NonNMethodCodeHeapSize=7602480 -XX:NonProfiledCodeHeapSize=122027880 -XX:+PrintNMTStatistics -XX:ProfiledCodeHeapSize=122027880 -XX:ReservedCodeCacheSize=251658240 -XX:+SegmentedCodeCache -XX:SoftMaxHeapSize=23857201152 -XX:StartFlightRecording=dumponexit=true,filename=/tmp/rec.jfr,duration=600s -XX:ThreadStackSize=1024 -XX:+UnlockDiagnosticVMOptions -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseFastUnorderedTimeStamps -XX:+UseG1GC 

VM Arguments:
jvm_args: -Djava.security.properties=/layers/paketo-buildpacks_azul-zulu/java-security-properties/java-security.properties -XX:+ExitOnOutOfMemoryError -Xlog:gc:/tmp/gc.log -XX:ActiveProcessorCount=16 -agentlib:jdwp=transport=dt_socket,server=y,address=*:8000,suspend=n -XX:StartFlightRecording=dumponexit=true,filename=/tmp/rec.jfr,duration=600s -Djava.rmi.server.hostname=127.0.0.1 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.port=5000 -Dcom.sun.management.jmxremote.rmi.port=5000 -XX:MaxDirectMemorySize=10M -Xmx23297904K -XX:MaxMetaspaceSize=130595K -XX:ReservedCodeCacheSize=240M -Xss1M -XX:+UnlockDiagnosticVMOptions -XX:NativeMemoryTracking=summary -XX:+PrintNMTStatistics -Dorg.springframework.cloud.bindings.boot.enable=true 
java_command: org.springframework.boot.loader.JarLauncher
java_class_path (initial): /workspace
Launcher Type: SUN_STANDARD
```

## Making use of the logging and debug configurations

Analyzing and debugging the application inside Docker with the various tools and configurations is beyond the scope of this article. But we can validate all of them quickly to confirm their correct configuration, and how you can use them.

### Getting files out of the Docker

You can copy both the JFR recording and the Garbage Collector files inside the Docker image to your PC.

```
$ docker cp petclinic:/tmp/rec.jfr rec.jfr
$ docker cp petclinic:/tmp/gc.log gc.log
```

### Connecting to debug

From within your IDE, for instance, IntelliJ IDEA, you can start a debug connection to the running application inside the Docker via port 8000 and set breakpoints in your code.

```
Connected to the target VM, address: 'localhost:8000', transport: 'socket'
```

{{< gallery >}}
debug-config-1024x663.png
debug-breakpoint.jpg
{{< /gallery >}}

### Connecting to JMX

The JMX connection configured on port 5000 allows you to connect to the running application inside the Docker with VisualVM or [Azul Mission Control](https://docs.azul.com/azul-mission-control/). For instance, you can start a JFR recording for a given duration and get the results immediately visualized.

Click on any of the three images below for a larger image.

{{< gallery >}}
springboot-jmx-config.png
springboot-jmx-jfr-recording.png
springboot-jmx-result-1024x662.png
{{< /gallery >}}

## Conclusion

This post's extended `pom.xml` configuration includes various ways to enable remote debugging and generate log files.

You should never use all of them simultaneously, and definitely not in production! But the goal of this article is to determine if the default Java distribution in the Spring Boot Docker can be replaced and extended with additional settings.

And... mission accomplished!

## Read more

* [Video created by DaShaun Carter](https://www.youtube.com/watch?v=6FfP3v40il8)
* Paketo docs:
  * [How to Build Java Apps with Paketo Buildpacks](https://paketo.io/docs/howto/java/)
  * [Image Embedded Environment Variables](https://paketo.io/docs/howto/configuration/#image-embedded-environment-variables)
* Spring docs:
  * [Spring Boot Maven Plugin Documentation \> Packaging OCI Images \> Image Customizations](https://docs.spring.io/spring-boot/docs/2.3.x/maven-plugin/reference/html/#build-image-customization)
* GitHub:
  * [Paketo Buildpack for Environment Variables](https://github.com/paketo-buildpacks/environment-variables)
* StackOverflow:
  * [Environment properties of Spring Boot OCI image aren't picked up by CNB launcher](https://stackoverflow.com/questions/76294215/environment-properties-of-spring-boot-oci-image-arent-picked-up-by-cnb-launcher)
