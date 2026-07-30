---
title: "Introducing a New Java DMX512 Library With Demo JavaFX User Interface"
slug: "introducing-a-new-java-dmx512-library-with-demo-javafx-user-interface"
date: "2025-07-25T06:17:00+00:00"
description: "In this post, I would like to inform you about a new Java library that is now available on Maven Central, allowing interaction with DMX512 devices using - by Frank Delporte"
canonical: "https://webtechie.be/post/2025-07-17-introducing-java-dmx512-library-with-demo-javafx-ui/"
authors:
  - "frankdelporte"
image: "https://foojay.io/wp-content/uploads/2025/07/thumbnail-dmx512-ofl.jpg"
categories:
  - "Desktop"
  - "Java"
  - "JavaFX"
  - "Library"
  - "Videos"
tags:
related_posts:
enlighterjs: true
frozen: false
---

In this post, I would like to inform you about a new Java library that is now available on Maven Central, allowing interaction with DMX512 devices using (optionally) the Open Fixture Library (OFL). I also published a video with a code walkthrough of my test setup and demo code.

{{< youtube ztrO3Crexmg >}}

But first...

A Personal Story {#h2-0-a-personal-story}
-----------------------------------------

I've always been fascinated by sound and light equipment. As a teenager (over 30 years ago...), I built two disco bars and used them as "DJ Franky" to bring ambiance to many weddings and other parties. I loved the DJ-ing, but I loved even more the technique of getting all the devices together and finding the best way to connect them, building custom housing, and creating the best possible connections. But there was one problem with this hobby, it all cost a lot of money...
![](https://webtechie.be/images/2025/dmx/djfranky-1.jpg)

Fast forward to now. Thanks to modern technology and improved production processes, prices have decreased significantly, allowing you to purchase marvelous pieces of technology at an affordable price. However, most of these can still be controlled by an "ancient" standard: DMX512.

As I wanted to control a few of these from a JavaFX user interface, but couldn't find a suitable Java library to do so, I created one myself.

About DMX512 and OFL {#h2-1-about-dmx512-and-ofl}
-------------------------------------------------

Let's start by explaining the standards used in this project.

### What is DMX512 {#h3-2-what-is-dmx512}

[DMX512](https://en.wikipedia.org/wiki/DMX512) is a digital communication protocol, based on [RS-485](https://en.wikipedia.org/wiki/RS-485), widely used in professional lighting and stage equipment to control dimmers, moving lights, fog machines, and other effects. The protocol transmits data in a serial format over standard XLR cables, with each "universe" capable of controlling up to 512 channels of information. Each channel can carry values from 0 to 255, allowing for precise control of parameters such as brightness, color, position, and speed across multiple fixtures simultaneously. DMX512 has become the industry standard because it's reliable, relatively simple to implement, and allows complex lighting shows to be programmed and synchronized from a central console.
![](https://webtechie.be/images/2025/dmx/dmx-fixtures.png)

#### DMX512 Data Example

In my test setup, I have two RGB LED fixtures who have five values for red, green, blue, dimmer, and effect. If I give them address 1 and 6, and want to first one to be full red, and the second one blue half dimmed (`127 = 0x7f`), both without effect, I would need to create this byte array in Java:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">//                    Address 1                     
//                    Red   Green Blue  Dim   Effect 
var data = new byte[]{0xff, 0x00, 0x00, 0xff, 0x00, 
//                    Address 6
//                    Red   Green Blue  Dim   Effect 
                      0x00, 0x00, 0xff, 0x7f, 0x00};</pre>

#### DMX512 Control from PC

Not many PCs nowadays have a serial port. Still, luckily, you can control DMX512 devices through several other methods, such as USB-to-DMX and IP-to-DMX interface devices that convert the data into the DMX512 format. These devices use different protocols to handle DMX512 data, for example:

* USB-to-DMX:
  * [Enttec](https://cdn.enttec.com/pdf/assets/70304/70304_DMX_USB_PRO_API.pdf)
  * [FTDI](https://github.com/Hperigo/DMX-FTDI?tab=readme-ov-file)
* IP-to-DMX:
  * [ArtNet](https://art-net.org.uk/)
  * [sACN (Streaming ACN, ANSI E1.31)](https://tsp.esta.org/tsp/documents/docs/ANSI_E1-31-2018.pdf).

Software applications like [QLC+](https://www.qlcplus.org/), [ONYX](https://www.elationlighting.com/products/onyx), or [MagicQ](https://be.chamsyslighting.com/product/magicq-software/) run on the PC and provide user-friendly interfaces for programming lighting cues, effects, and real-time control. The software sends commands through the USB interface, which then transmits the DMX512 data stream to control brightness, color, movement, and other parameters across hundreds of connected lighting fixtures.

But none of these are based on Java...

### What is Open Fixture Library {#h3-3-what-is-open-fixture-library}

The [Open Fixture Library (OFL)](https://open-fixture-library.org/) is a collaborative, open-source platform that addresses a persistent issue in lighting control: fixture definitions that are tied to specific software platforms.
![](https://webtechie.be/images/2025/dmx/ofl-picospot.png)

The project emerged when lighting professionals wanted to switch between different software controllers, but discovered their fixture definitions couldn't be easily transferred. This meant recreating all their work from scratch -- a time-consuming process the entire lighting community had been dealing with for years.

#### The OFL Solution

Open Fixture Library creates a centralized, wiki-style repository where fixture definitions are stored in a universal format. The platform automatically generates fixture files compatible with multiple lighting control software packages, eliminating the need to recreate definitions for each program.

Key features include universal compatibility across lighting software formats, collaborative development where anyone can contribute improvements, and a user-friendly online editor for creating new definitions or importing existing ones.

#### Impact

Since joining the [Open Lighting Project](https://github.com/OpenLightingProject) in 2018, OFL has become an essential tool for lighting professionals worldwide. By standardizing fixture definitions and making them freely available, setup time has been reduced, reliability improved, and users have been given greater flexibility in software choice. The platform demonstrates how community-driven projects can solve industry-wide challenges more effectively than proprietary solutions.

#### OFL JSON Files

In my library, you can use OFL exports in the "Open Fixture Library JSON" format. This is the internal data model of OFL and contains all the info in a nice readable way.

The OFL project is [well documented on GitHub](https://github.com/OpenLightingProject/open-fixture-library/tree/master/docs) with more [details about the data model of the JSON files here](https://github.com/OpenLightingProject/open-fixture-library/blob/master/docs/model-api.md).

DMX512 Java Library {#h2-4-dmx512-java-library}
-----------------------------------------------

The library I created is open-source with its [sources on GitHub](https://github.com/codewriterbv/DMX512/) and [releases on Maven Central](https://central.sonatype.com/artifact/be.codewriter/dmx512).

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;dependency&gt;
    &lt;groupId&gt;be.codewriter&lt;/groupId&gt;
    &lt;artifactId&gt;dmx512&lt;/artifactId&gt;
    &lt;version&gt;${dmx512.version}&lt;/version&gt;
&lt;/dependency&gt;</pre>

### My Test Setup {#h3-5-my-test-setup}

At this moment, the library is only tested with this IP-to-DMX controller: [JUNELIONY ArtNet 1024 2-Port Sulite DMX LAN512 2-Port ArtNet Converter](https://www.amazon.com.be/dp/B0CYPQ2Z4V). It comes configured with a fixed IP, but I changed it to use DHCP.

I have the following fixtures connected to it:

* Address 1 and 12: [PicoSpot 20 LED](https://www.thomann.de/intl/fun_generation_picospot_20_led.htm) with 11 channels.
* Address 23 and 28 : [LED PARty TCL Spot](https://www.steinigke.de/en/mpn42110193-eurolite-led-party-tcl-spot.html) with 5 channels.

![](https://webtechie.be/images/2025/dmx/test-setup-2.jpg)

### Minimal Code Example {#h3-6-minimal-code-example}

You can send a byte array directly via the controller. Create an array with the expected length by your device and fill in the values.

This is an example for a PicoSpot on channel 1 = the data starts at index 0 of the byte array.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">var controller = new DMXIPController(InetAddress.getByName("172.16.1.144"));

// The PicoSpot on DMX channel 1 expects 11 values
/*
"Pan",
"Tilt",
"Pan fine",
"Tilt fine",
"Pan/Tilt Speed",
"Color Wheel",
"Gobo Wheel",
"Dimmer",
"Shutter / Strobe",
"Program",
"Program Speed"
*/
// Set all to 0
controller.render(new byte[]{(byte) 0, (byte) 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
sleep(2_000);
// Set pan and tilt to 127
controller.render(new byte[]{(byte) 127, (byte) 127, 0, 0, 0, 0, 0, 0, 0, 0, 0});
sleep(2_000);
// Set color wheel to 44 and dimmer full op
controller.render(new byte[]{0, 0, 0, 0, 0, (byte) 44, 0, (byte) 255, 0, 0, 0});</pre>

### Using Fixtures and Modes {#h3-7-using-fixtures-and-modes}

By using a fixture loaded from an OFL JSON file, it becomes significantly easier to modify the data. You can use the name of the channel (e.g., "red", "dimmer", ...) and don't need to know the index of the data in the byte array.

This is a minimal example:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">var address = InetAddress.getByName("172.16.1.144");
var controller = new DMXIPController(address);

// Load a fixture
var oflFilePath = "/your/path/to/led-party-tcl-spot.json";
Fixture fixture = OpenFormatLibraryParser
        .parseFixture(new File(oflFilePath));

// Create a DMX client based on the fixture, a mode, and DMX channel (23 in this example)
var fixtureMode = fixture.modes().getFirst();
DMXClient client = new DMXClient(fixture, fixtureMode, 23);

// This fixture has only one mode with the following channels:
// "channels": [
//   "Red",
//   "Green",
//   "Blue",
//   "Dimmer",
//   "Effects"
// ]

// Set to full red
client.setValue("red", (byte) 255);
client.setValue("dimmer", (byte) 255);

// Send the data to the DMX interface
controller.render(client);

// Color change effect
for (int i = 0; i &lt;= 100; i++) {
    float ratio = i / 100.0f;
    client.setValue("red", (byte) (255 * (1 - ratio)));
    client.setValue("blue", (byte) (255 * ratio));
    controller.render(client);
    sleep(50);
}

controller.close();</pre>

### Detecting USB-to-DMX and IP-to-DMX interfaces {#h3-8-detecting-usb-to-dmx-and-ip-to-dmx-interfaces}

Two tools in the library can be used to detect these interfaces:

* USB-to-DMX: Returns a list of all serial devices connected to the PC. This list can also contain Bluetooth, test, or other ports that are not related to the DMX interface.
* IP-to-DMX: Returns a list of devices that reply to an ArtNet detect packet. Only DMX interfaces should appear in this list.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">List&lt;SerialConnection&gt; serialDevices = DMXSerialDiscoverTool.getAvailablePorts();
List&lt;DMXIPDevice&gt; ipDevices = DMXIPDiscoverTool.discoverDevices();</pre>

DMX512 JavaFX Demo Project {#h2-9-dmx512-javafx-demo-project}
-------------------------------------------------------------

To demonstrate how the DMX data can be controlled from a user interface and what gets loaded from the OFL JSON files, a separate project has been created. It's also [available as open-source on GitHub](https://github.com/codewriterbv/DMX512-Demo).
![](https://webtechie.be/images/2025/dmx/demo-app-picospot-channels.png)

Next Steps {#h2-10-next-steps}
------------------------------

At this moment, with V0.0.1 of the library, devices can be successfully controlled with IP-to-DMX with the ArtNet protocol. My first USB experiments didn't succeed. That's why I focused on the DMX data handling, IP-to-DMX, and OFL integration to reach a first milestone.

New releases will (soon?) include more support for other protocols. However, I hope that some of you can already use this version and are interested in experimenting with it. Please let me know your ideas and remarks!
