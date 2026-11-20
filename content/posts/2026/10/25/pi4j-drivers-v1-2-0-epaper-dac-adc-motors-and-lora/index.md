---
title: "Pi4J Drivers V1.2.0: ePaper, DAC/ADC, Motors, and LoRa"
date: "2026-10-25"
description: "Pi4J Drivers V1.2.0 adds ePaper displays with partial updates, new DAC and ADC drivers, motor and servo controllers, and a LoRa transceiver driver for Java on the Raspberry Pi."
canonical: "https://webtechie.be/post/pi4j-drivers-v1-2-0/"
authors:
  - "frankdelporte"
image: "pi4j-overview.jpg"
categories:
  - "Pi4J"
  - "Raspberry Pi"
  - "Embedded"
related_posts:
  - "pi4j-drivers-simplifying-sensor-and-hardware-integration-in-java"
  - "using-the-raspberry-pi-sense-hat-with-pi4j-drivers"
  - "pi4j-v5-work-in-progress"
  - "the-ffm-api-how-openjdk-changed-the-game-for-native-interactions-and-made-pi4j-better"
---

[Pi4J](https://pi4j.com/) is the Java library that gives you access to the hardware I/O of the Raspberry Pi: GPIO pins, I2C, SPI, PWM, and serial communication. With it, you can read a button, blink an LED, or talk to a sensor from plain Java code, running on the Pi itself.

Talking to a real component, however, takes more than raw pin access. Every sensor, display, or motor controller has its own registers, commands, and timing rules. That's where [Pi4J Drivers](https://pi4j.com/drivers/) comes in: a library on top of Pi4J with ready-to-use drivers for popular hardware components, so you can work with a high-level Java API instead of reading datasheets and writing low-level I2C, SPI, or GPIO code yourself.

A new release of the [Pi4J Drivers library](https://pi4j.com/drivers/) is available! Version 1.2.0 is a big step forward, adding support for many new components. The full announcement is on the [Pi4J blog](https://pi4j.com/blog/2026/20260925-pi4j-drivers-v1.2.0/), here are the highlights.

## ePaper displays

This release adds a generic [SSD1680 driver](https://github.com/Pi4J/pi4j-drivers/tree/main/src/main/java/com/pi4j/drivers/display/graphics/ssd1680), plus drivers for the [Waveshare 2.7-inch ePaper display hat v2](https://github.com/Pi4J/pi4j-drivers/blob/main/src/main/java/com/pi4j/drivers/display/graphics/ssd1680/Waveshare2in7V2.java) and the large [Waveshare 13.3-inch black-and-white 960x680 ePaper display](https://github.com/Pi4J/pi4j-drivers/blob/main/src/main/java/com/pi4j/drivers/display/graphics/ssd1677/Waveshare13in3kDriver.java). The SSD1680 and Waveshare 2.7" v2 drivers now also support **partial display updates**, so you no longer need to redraw (and flicker) the full screen for every small change.

## Text and graphics

A new [`GraphicsTextAnimator`](https://github.com/Pi4J/pi4j-drivers/blob/main/src/main/java/com/pi4j/drivers/display/graphics/GraphicsTextAnimator.java) makes scrolling text easy, displays can now be mirrored, and cursor management is now part of the [`CharacterDisplay`](https://github.com/Pi4J/pi4j-drivers/blob/main/src/main/java/com/pi4j/drivers/display/character/CharacterDisplay.java) interface.

## Analog in and out

There is a new [`DigitalAnalogConverter`](https://github.com/Pi4J/pi4j-drivers/blob/main/src/main/java/com/pi4j/drivers/io/da/DigitalAnalogConverter.java) interface, with reworked [MCP49xx](https://github.com/Pi4J/pi4j-drivers/tree/main/src/main/java/com/pi4j/drivers/io/da/mcp49xx) drivers and new drivers for the [MCP4725](https://github.com/Pi4J/pi4j-drivers/blob/main/src/main/java/com/pi4j/drivers/io/da/mcp472x/Mcp4725Driver.java), [MCP4921](https://github.com/Pi4J/pi4j-drivers/blob/main/src/main/java/com/pi4j/drivers/io/da/mcp49xx/Mcp49x1Driver.java)/[MCP4922](https://github.com/Pi4J/pi4j-drivers/blob/main/src/main/java/com/pi4j/drivers/io/da/mcp49xx/Mcp49x2Driver.java), and [MCP4728](https://github.com/Pi4J/pi4j-drivers/blob/main/src/main/java/com/pi4j/drivers/io/da/mcp472x/Mcp4728Driver.java) DACs. On the input side, the TI [ADS111x](https://github.com/Pi4J/pi4j-drivers/tree/main/src/main/java/com/pi4j/drivers/io/ad/ads111x) and [NAU7802](https://github.com/Pi4J/pi4j-drivers/blob/main/src/main/java/com/pi4j/drivers/io/ad/nau7802/Nau7802Driver.java) ADCs are now supported, and the [`Mcp300xDriver`](https://github.com/Pi4J/pi4j-drivers/blob/main/src/main/java/com/pi4j/drivers/io/ad/mcp300x/Mcp300xDriver.java) is split into 10-bit ([MCP3008](https://github.com/Pi4J/pi4j-drivers/blob/main/src/main/java/com/pi4j/drivers/io/ad/mcp300x/Mcp3008Driver.java)) and 12-bit ([MCP3208](https://github.com/Pi4J/pi4j-drivers/blob/main/src/main/java/com/pi4j/drivers/io/ad/mcp300x/Mcp3208Driver.java)) subclasses.

## Motors and robotics

Building something that moves? You can now use the [LN298 motor driver](https://github.com/Pi4J/pi4j-drivers/blob/main/src/main/java/com/pi4j/drivers/motor/ln298/Ln298Driver.java), the [ULN2003 driver](https://github.com/Pi4J/pi4j-drivers/blob/main/src/main/java/com/pi4j/drivers/motor/uln2003/Uln2003Driver.java), and the [PiBorg UltraBorg board](https://github.com/Pi4J/pi4j-drivers/blob/main/src/main/java/com/pi4j/drivers/multipurpose/piborg/UltraBorgDriver.java).

## Long-range communication

Finally, a driver for the [Semtech LR11xx LoRa transceiver](https://github.com/Pi4J/pi4j-drivers/tree/main/src/main/java/com/pi4j/drivers/radio/lora/lr11xx) opens the door to low-power, long-distance wireless projects in Java.

## Learn more

Check the [drivers release notes](https://pi4j.com/drivers/drivers-release-notes/) for all details, or [compare v1.1.0 and v1.2.0 on GitHub](https://github.com/Pi4J/pi4j-drivers/compare/v1.1.0...v1.2.0) to see every change. And as always, the [Pi4J website](https://pi4j.com/) is the place to get started with Java on the Raspberry Pi. Many thanks to all contributors who made this release possible!
