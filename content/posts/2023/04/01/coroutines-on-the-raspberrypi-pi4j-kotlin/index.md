---
title: "Coroutines on the RaspberryPi (Pi4J-Kotlin)"
slug: "coroutines-on-the-raspberrypi-pi4j-kotlin"
date: "2023-04-01T09:05:29+00:00"
lastmod: "2023-04-01T20:12:59+00:00"
description: "Find out all the details on Pi4J-Kotlin v2.4.0: Coroutines, I2C, and Serial DSL on Foojay.io Today, the place for OpenJDK friends."
authors:
  - "mhashim6"
image: "code.png"
categories:
  - "Embedded"
  - "Kotlin"
  - "Pi4J"
  - "Raspberry Pi"
  - "reactive"
tags:
related_posts:
  - "kotlin-on-the-raspberrypi-pi4j-kotlin"
  - "metaphorical-programming-gossips-event-bus"
  - "blink-a-led-on-raspberry-pi-with-vaadin"
  - "using-the-raspberry-pi-sense-hat-with-pi4j-drivers"
enlighterjs: true
frozen: false
---

If you didn't know already, [Pi4J](https://pi4j.com/) has had a [Kotlin DSL](https://pi4j.com/kotlin/) for quite some time now.

And I'm here to announce the [latest release `v2.4.0`](https://github.com/Pi4J/pi4j-kotlin/releases/tag/2.4.0) with a sack of additions and to tell you about all the good stuff that's been added since the [first release](https://foojay.io/today/kotlin-on-the-raspberrypi-pi4j-kotlin/).

1. Coroutines
-------------

If the `pi4j { ... }` wasn't good enough for ya, and you want to use coroutines instead of weaving fat threads on your precious precious Pi, you can now use the new `pi4jAsync { ... }` block---It can do everything `pi4j { ... }` does + you can run `suspend`ed functions within.

I know you guys just want to use coroutines so that you can call `delay()` instead of `Thread::sleep`. And I won't disappoint you. Here's a blinking LED example with coroutines:

```kotlin
pi4jAsync {
    digitalOutput(PIN_LED) {
        id(“led”)
        name(“LED Flasher”)
        shutdown(DigitalState.LOW)
        initial(DigitalState.LOW)
        piGpioProvider()
    }.run {
        while (true) {
            toggle()
            delay(500L)  // The most loved suspended function
        }
    }
}
```

Feel free to visit the [docs](https://pi4j.com/kotlin/coroutines/) on coroutines support, and the full example.

2. I²C DSL
----------

This will add a little beauty to your life when dealing with `I²C`

```kotlin
i2c(1, 0x3f) {
    id(“TCA9534”)
    linuxFsI2CProvider()
}.use { tca9534Dev ->
  // use here. Will auto close
}
```

Feel free to visit the [docs](https://pi4j.com/kotlin/i2c/) on `I²C` support, and the full example.

3. Serial DSL
-------------

I know I'm messing with "taboos" right now, but I've just made Serial look nice. You guessed it right, it's as simple as just a `serial { ... }` block

```kotlin
serial(“/dev/ttyS0”) {
    use_9600_N81()
    dataBits_8()
    parity(Parity.NONE)
    stopBits(StopBits._1)
    flowControl(FlowControl.NONE)
    piGpioSerialProvider()
}.open {
  // use here. 
}
```

You know the drill, feel free to visit the [docs](https://pi4j.com/kotlin/serial/) on Serial support, and the full example.

4. Misc
-------

* Updated Pi4J to `v2.3.0`
* Updated docs and examples

---  

If you want to share feedback, or report a bug, feel free to discuss and open issues on the [Github Repo](https://github.com/Pi4J/pi4j-kotlin)!
