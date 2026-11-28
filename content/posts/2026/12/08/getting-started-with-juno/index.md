---
title: "Getting Started with Juno: Running Java on Arduino & M5Stack"
date: "2026-12-08"
description: "Learn how Juno compiles a practical subset of Java into native code for Arduino and M5Stack boards."
authors:
  - "jabrena"
image: "juno.jpg"
categories:
  - "Java Core"
  - "Embedded"
  - "Tutorials"
related_posts:
  - "java-benchmarks-on-single-board-computers"
  - "bringing-raspberry-pico-to-quarkus-with-firmata"
  - "first-test-of-java-on-banana-pi-arm-and-risc-v-plus-a-blinking-led-with-pi4j"
  - "pi4j-v5-work-in-progress"
---

What if you could write a small Java program and run it directly on an Arduino board without putting a JVM on the device? This is the idea behind [Juno](https://github.com/jabrena/juno), an ahead-of-time compiler created by [Juan Antonio Brena Moral](https://foojay.io/today/author/jabrena/).

Juno uses `javac` as its Java front end, follows the code reachable from `main`, and translates it into native assembly plus a `C++` shim. The Arduino toolchain then turns that output into firmware for the selected board.

In the past, there were other initiatives for developing Java programs for physical devices, such as [Sun SPOT](https://en.wikipedia.org/wiki/Sun_SPOT) and [LeJOS](https://en.wikipedia.org/wiki/LeJOS), but Juno offers a new opportunity for modern Java.

In this article, you will learn how to use the project, compile a simple blinking-LED program, and verify and upload it to an `Arduino UNO R4 WiFi`.

## What you need

Before you begin, install the following software. You will also need a supported Arduino board to run the example:

- `JDK 27`.
- `Maven 3.10.0` or newer.
- [Arduino CLI](https://docs.arduino.cc/arduino-cli/installation/).
- A supported Arduino board. This article uses an `Arduino UNO R4 WiFi`.

**Note:** Install the Arduino core for your board by following the [Arduino CLI documentation](https://arduino.github.io/arduino-cli/latest/commands/arduino-cli_core_install/).

## Create your first Juno project

### Create the project directory

```bash
mkdir my-first-juno-project
```

The project is created from Juno's published Maven archetype. It uses the Juno BOM to keep the API and compiler-side artifacts at the same version.

### Generate the Maven project

Run the archetype command from the parent directory. Maven creates `my-first-juno-project/`:

```bash
mvn archetype:generate \
  -DarchetypeGroupId=io.github.jabrena \
  -DarchetypeArtifactId=juno-archetype \
  -DarchetypeVersion=0.2.0 \
  -DgroupId=com.example \
  -DartifactId=my-first-juno-project \
  -Dversion=1.0.0-SNAPSHOT \
  -Dpackage=com.example.juno \
  -DjunoVersion=0.2.0 \
  -DinteractiveMode=false
```

If you are already inside `my-first-juno-project`, add `-DoutputDirectory=.` to the command. The archetype version identifies the project template; `junoVersion` selects the published Juno release.

Create the Maven Wrapper and use it for the remaining commands:

```bash
cd my-first-juno-project
mvn wrapper:wrapper
```

The generated project has this shape:

```text
my-first-juno-project/
├── .mvn/
│   ├── jvm.config
│   └── wrapper/
│       └── maven-wrapper.properties
├── mvnw
├── mvnw.cmd
├── pom.xml
└── src/main/java/com/example/juno/
    └── Blink.java
```

### Understand the generated project

The generated `pom.xml` sets Java 27 as the source level, imports the Juno BOM, and configures the Juno Maven plugin. Its defaults select the `Arduino UNO R4 WiFi` and the `com.example.juno.Blink` entry point:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>my-first-juno-project</artifactId>
    <version>1.0.0-SNAPSHOT</version>

    <properties>
        <java.version>27</java.version>
        <maven-compiler-plugin.version>3.16.0</maven-compiler-plugin.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <juno.version>0.2.0</juno.version>
        <juno.board>arduino-uno-r4-wifi</juno.board>
        <juno.main>com.example.juno.Blink</juno.main>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>io.github.jabrena</groupId>
                <artifactId>juno-bom</artifactId>
                <version>${juno.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>io.github.jabrena</groupId>
            <artifactId>juno-api</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>${maven-compiler-plugin.version}</version>
                <configuration>
                    <release>${java.version}</release>
                </configuration>
            </plugin>
            <plugin>
                <groupId>io.github.jabrena</groupId>
                <artifactId>juno-maven-plugin</artifactId>
                <version>${juno.version}</version>
                <configuration>
                    <board>${juno.board}</board>
                    <mainClass>${juno.main}</mainClass>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

The archetype also includes `.mvn/jvm.config` with the compiler module options required by Juno. Maven reads it automatically.

### Understand the generated `Blink` example

```java
package com.example.juno;

import io.github.jabrena.juno.hardware.MCU;
import io.github.jabrena.juno.hardware.gpio.DigitalOutput;
import io.github.jabrena.juno.util.Delay;

public final class Blink {
    public static void main(String[] args) {
        DigitalOutput led = DigitalOutput.of(MCU.getGpio().getBuiltinLed());
        while (true) {
            led.high();
            Delay.millis(500);
            led.low();
            Delay.millis(500);
        }
    }
}
```

`MCU.getGpio().getBuiltinLed()` obtains the board's built-in LED. The two 500-millisecond delays create one-second blink cycles. The board abstraction keeps the program portable across supported boards, and the example intentionally produces no serial output.

### Install board dependencies

Install the Arduino cores and libraries required by Juno once per machine:

```bash
./mvnw juno:install-deps
```

### Generate and verify the sketch

```bash
./mvnw compile juno:compile
./mvnw compile juno:verify
```

`juno:verify` compiles and checks the generated sketch with Arduino CLI; it does not upload anything. The generated sketch and assembly files are normally written under `target/juno/<board>/`.

### Understand the generated output

Maven keeps build products under `target/`. After compiling the default `Blink` entry point, the important files look like this:

```text
target/
├── classes/                    # Java bytecode compiled from src/main/java
└── juno/
    └── BlinkAsm/               # Arduino CLI sketch generated by Juno
        ├── BlinkAsm.ino        # Arduino sketch wrapper
        ├── Blink.S             # generated target assembly
        └── BlinkShim.cpp       # C++ bridge to the Arduino runtime
```

`juno:compile` creates the sketch directory and its source files. `juno:verify` then passes that directory to Arduino CLI, which compiles it for the selected board. These files are disposable build output: edit the Java source and POM, then run the Maven goals again to regenerate them. The generated assembly and shim are useful when diagnosing a compiler or board toolchain problem.

### Flash the board

Connect the board over USB, list matching boards, and flash it:

```bash
./mvnw juno:boards
./mvnw compile juno:flash -Djuno.port=<PORT>
```

Use `-Djuno.board=<board-id>` when the program supports more than one board. The port is the USB serial device reported by `juno:boards`; do not use a network address for uploading.

### What to expect after flashing

The default project targets the Arduino UNO R4 WiFi. After `juno:flash` finishes, the board resets and its built-in LED should blink repeatedly: on for 500 milliseconds, then off for 500 milliseconds.

{{< gallery cols="1" >}}
arduino-uno-r4-wifi-led.jpg | Arduino UNO R4 WiFi board | Top view of the Arduino UNO R4 WiFi with an arrow marking the built-in LED labelled L
{{< /gallery >}}

**Note:** The `Blink` example intentionally produces no serial output, so a quiet serial monitor is expected. If the LED does not blink, check the selected `juno.board`, the USB serial `juno.port`, and that the flash command completed without errors.


## Supported boards in Juno

Juno 0.2.0 supports seven boards through three native backends: AVR for the UNO R3; Thumb-2 for the UNO R4 WiFi, Raspberry Pi Pico 2 W, and Portenta H7; and Xtensa for the Nano ESP32, M5Stack Cardputer, and AI Thinker ESP32-CAM.

{{< gallery cols="1" >}}
juno-020-boards-supported.jpg | Juno 0.2.0 supported boards | Seven supported boards in a yellow tray: Arduino UNO R3 and R4 WiFi, M5Stack Cardputer, Pico 2 W, Portenta H7, Nano ESP32 and ESP32-CAM
{{< /gallery >}}

Each board has different capabilities, and all boards except the `Arduino UNO R3` support internet connectivity.

| Board | Microcontroller | CPU and clock | Flash | SRAM | SDRAM/PSRAM | Wireless |
|---|---|---|---:|---:|---:|---|
| [Arduino Portenta H7](https://docs.arduino.cc/hardware/portenta-h7/) | STM32H747XI | Cortex-M7 at 480 MHz; M4 at 240 MHz | 2 MB internal + 16 MB NOR | 1 MB | 8 MB SDRAM | Wi-Fi, BLE |
| [Arduino Nano ESP32](https://docs.arduino.cc/hardware/nano-esp32/) | ESP32-S3 (NORA-W106) | Dual-core 32-bit Xtensa LX7 at 240 MHz | 16 MB | 512 kB | 8 MB PSRAM | Wi-Fi, BLE |
| [M5Stack Cardputer](https://docs.m5stack.com/en/core/Cardputer) | ESP32-S3 | Dual-core 32-bit Xtensa LX7 at 240 MHz | 8 MB | 512 kB | — | Wi-Fi, BLE |
| [AI Thinker ESP32-CAM](https://jabrena.github.io/juno/boards/ai-thinker-esp32-cam/) | ESP32 Q | Dual-core 32-bit Xtensa LX6 at 240 MHz | 4 MB | 520 kB | 4 MB PSRAM | Wi-Fi, BLE |
| [Raspberry Pi Pico 2 W](https://www.raspberrypi.com/products/raspberry-pi-pico-2/) | RP2350A | Dual-core 32-bit Cortex-M33F at 150 MHz | 4 MB | 520 kB | — | CYW43439 Wi-Fi, BLE |
| [Arduino UNO R4 WiFi](https://docs.arduino.cc/hardware/uno-r4-wifi/) | Renesas RA4M1 | 32-bit Cortex-M4F at 48 MHz | 256 kB | 32 kB | — | ESP32-S3 Wi-Fi, BLE |
| [Arduino UNO R3](https://docs.arduino.cc/hardware/uno-rev3/) | ATmega328P | 8-bit AVR at 16 MHz | 32 kB | 2 kB | — | None |

## How Java becomes board code

Juno is an ahead-of-time compiler, not a JVM. At a high level, the build follows this path:

```text
Java source -> javac -> .class -> Juno linker and compiler -> assembly and C++ shim -> Arduino toolchain -> firmware
```

The compiler starts at `main`, follows the methods reachable from that entry point, resolves Juno hardware intrinsics, and rejects unsupported bytecode before generating the sketch. The generated assembly and runtime shim are then compiled for the selected Arduino board.

This closed-world approach makes it possible to use familiar Java language features and a Java-like programming model in a much smaller environment. It also means that valid Java is not automatically valid Juno input: code outside the supported subset is reported during compilation instead of being silently emitted as incomplete firmware.

### Preserving Java semantics on a microcontroller

Juno consumes the `.class` files produced by `javac`, so it preserves Java behavior for the features it supports. Integer arithmetic wraps at the same width as on the JVM; `byte`, `short`, and `char` conversions follow Java's sign-extension and zero-extension rules; and `long`, `float`, and `double` retain their Java-defined widths. Floating-point comparisons also preserve Java's handling of `NaN`, and supported exceptions can be handled with `try`, `catch`, and `finally`.

The difference is the execution environment. An Arduino has no JVM, dynamic class loader, or desktop-sized heap. Juno resolves reachable calls ahead of time and uses fixed memory budgets for arrays and objects. Unsupported bytecode, APIs, or object models stop compilation with a diagnostic. Code that exhausts the board's arena or stack can still stop at runtime, so successful compilation does not remove the need to design for the target board's RAM and flash limits.

### Java language and library support

Juno supports a deliberately smaller subset of Java and its libraries than a desktop JVM. The table below groups the supported types and features by category:

| Java type or feature |
|---|
| **Primitive types** |
| `boolean`, `byte`, `short`, `char`, `int` |
| `long` |
| `float`, `double` |
| **Data structures and objects** |
| Primitive arrays |
| Records and objects |
| **Language features** |
| Enums |
| Interfaces, lambdas, and method references |
| **Library classes** |
| `String` and `StringBuilder` |
| `BigInteger`, `BigDecimal`, `LocalDateTime`, `UUID` |
| `AtomicInteger`, `AtomicBoolean`, `AtomicLong` |

## Juno's memory model

Juno does not run a JVM and does not provide a general-purpose Java heap that grows at runtime. The compiler produces firmware ahead of time, and the board's memory is divided into several areas:

- **Flash** stores compiled code, the runtime shim, and constant data such as string literals.
- **Static RAM** stores mutable static fields.
- **The arena** stores Java objects, arrays, strings, records, and other runtime values. Its capacity is fixed for the build.
- **Stacks** store local variables, operand slots, and call frames. Native carrier and task stacks use additional RAM.

The arena is reclaimed automatically by a conservative mark-and-sweep garbage collector when an allocation needs more space. Live objects cannot be reclaimed, and the collector does not move objects, so object identity remains stable. If the arena is still full after collection, the program stops with a diagnostic; there is no `OutOfMemoryError` to catch.

Board memory budgets vary significantly. The default arena is 1 KB on the UNO R3, 8 KB on the UNO R4 WiFi, 64 KB on the Nano ESP32, AI Thinker ESP32-CAM, M5Stack Cardputer, Raspberry Pi Pico 2 W, and Portenta H7. You can request a different arena size with `-Djuno.Xmx=<size>`, subject to the board's limit and the RAM required by the rest of the firmware.

External memory is not a second Java heap. Some boards, such as the Arduino Nano ESP32, AI Thinker ESP32-CAM, and Portenta H7, use PSRAM or SDRAM for large model buffers. Ordinary Java objects, arrays, strings, and thread stacks remain in internal SRAM or the fixed arena.

### Concurrency and memory

Juno supports Java virtual threads. A virtual thread is a Java task scheduled on a native carrier; it does not permanently own a full native thread stack. The UNO R3 has four scheduler slots, including `main`, while the UNO R4 WiFi uses one carrier. The Nano ESP32, AI Thinker ESP32-CAM, M5Stack Cardputer, Raspberry Pi Pico 2 W, and Portenta H7 use two carriers by default. These boards can run two Java tasks at the same time, while a single-carrier board runs one task at a time.

On a single-carrier board, parked virtual-thread frames are copied into continuation data in the arena. On a dual-carrier board, active task slots use native stacks from the run-time heap. This means that more virtual threads also consume arena space, task slots, or native stack memory, depending on the board.

Structured concurrency with `StructuredTaskScope` and `ScopedValue`, as well as `ReentrantLock`, is supported on every board except the UNO R3. On the UNO R3, use `synchronized` and atomic classes instead.

For predictable memory use, allocate reusable objects and buffers during startup instead of inside hot loops. The compiler reports risks such as loop allocations, unbounded recursion, and constant tables that could not be placed in flash.

## What can you build with Juno's APIs?

Once `Blink` is working, combine Java logic with one of Juno's API categories. The project includes APIs for hardware control, storage, networking, observability, application structure, and even small AI models:

| Category | APIs | Project ideas |
|---|---|---|
| AI | `JEV` decision models, `JLM` language models | Use a compact decision model to select a legal action from the device's current state, or run a small language model linked into the firmware to generate text on the board. |
| Observability | `Serial`, `Syslog` | Print diagnostics over USB, or send allocation-free RFC 5424 log messages over UDP and follow them remotely over Wi-Fi with `juno:monitor`. |
| Storage | `SdCard`, `SecretStore` | Read configuration from a microSD card, or keep Wi-Fi credentials and other secrets outside the firmware image. |
| Internet | `Wifi`, `Time`, `HttpClient`, `HttpServer`, `Email` | Call an HTTP API, serve a dashboard from the board, synchronize time, or send and receive email. |
| Application structure | `MVC` | Organize a multi-screen application with separate models, views, navigation, and routing. |
| Hardware | `Gpio`, `Delay`, `Clock` | Read and drive pins, measure intervals, and coordinate actions with precise delays. |

Explore the [Juno API documentation](https://jabrena.github.io/juno/api/) for complete examples and board-specific details.

## Summary

- Juno compiles a practical subset of Java ahead of time; it does not run a JVM on the Arduino.
- The Juno Maven plugin connects Java compilation, native code generation, Arduino CLI verification, and upload.
- `juno:verify` is useful for checking a program without a physical board.
- The selected board must be supplied with `-Djuno.board` unless the entry point declares a specific board.
- Juno reports unsupported reachable bytecode at compile time.

Happy coding!

Juan Antonio
