---
title: "Release V0.0.2 of Java DMX512 Library With Universes and USB-to-DMX support"
slug: "release-v0-0-2-of-java-dmx512-library-with-universes-and-usb-to-dmx-support"
date: "2025-08-04T14:19:25+00:00"
description: "Earlier this month, I released V0.0.1 of my new Java library to interact with DMX512 devices using (optionally) the Open Fixture Library (OFL). After some - by Frank Delporte"
canonical: "https://webtechie.be/post/2025-07-29-java-dmx512-library-v0.0.2-universes-and-usb/"
authors:
  - "frankdelporte"
image: "https://foojay.io/wp-content/uploads/2025/07/thumbnail-dmx512-ofl.jpg"
categories:
  - "Java"
  - "Library"
  - "Videos"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Earlier this month, I [released V0.0.1 of my new Java library to interact with DMX512 devices using (optionally) the Open Fixture Library (OFL)](https://webtechie.be/post/2025-07-17-introducing-java-dmx512-library-with-demo-javafx-ui/). After some more experimenting, I'm able to announce the next (beta) release V0.0.2 with the following major changes:

* **Code refactoring**: As this library is still in beta, major changes were expected and happened 😉 The video of V0.0.1 is still valid, but some of the demonstrated code has changed.
* **Improved demos** : Demo code has been moved to the [`demo` directory in the sources](https://github.com/codewriterbv/DMX512/tree/main/src/main/java/be/codewriter/dmx512/demo) to make them easier to understand and reuse.
* **Introduction of DMX Universes**: to be able to control fixtures connected to the two ports of my IP-to-DMX controller, universes needed to be added. (more info below)
* **USB-to-DMX Support**: First working protocol over serial communication to DMX512! (more info below)

<figure class="wp-block-gallery has-nested-images columns-default is-cropped wp-block-gallery-1 is-layout-flex wp-block-gallery-is-layout-flex">
 <figure class="wp-block-image size-large">
  <img fetchpriority="high" decoding="async" width="1024" height="637" data-id="117016" src="/images/posts/2025/08/release-v0-0-2-of-java-dmx512-library-with-universes-and-usb-to-dmx-support/ip-to-dmx-universes-1024x637.jpg" alt="" class="wp-image-117016">
 </figure>
 <figure class="wp-block-image size-large">
  <img decoding="async" width="1024" height="572" data-id="117015" src="/images/posts/2025/08/release-v0-0-2-of-java-dmx512-library-with-universes-and-usb-to-dmx-support/usb-to-dmx-1024x572.jpg" alt="" class="wp-image-117015">
 </figure>
</figure>

Introduction of DMX Universes {#h2-0-introduction-of-dmx-universes}
-------------------------------------------------------------------

{{< youtube slC4niKWUq0 >}}

For my tests, I'm using an [JUNELIONY ArtNet 1024 2-Port Sulite DMX LAN512 2-Port ArtNet Converter](https://www.amazon.com.be/dp/B0CYPQ2Z4V) controller. It has two XLR-connectors, labeled as `DMX1` and `DMX2`, which can be controlled as universe ID 0 and 1.

A new object `DMXUniverse` has been introduced to support the use of universes, defined by an `id and a list of `DMXClient\`. Check [this demo code for a full example](https://github.com/codewriterbv/DMX512/blob/main/src/main/java/be/codewriter/dmx512/demo/IPTwoUniversesDemo.java). This is the simplified code:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// Load moving head fixture from an OFL file
Fixture movingHead = getFixture("picospot-20-led.json");
var movingHeadMode = movingHead.getModeByName("11-channel");

// Load RGB fixture from an OFL file
Fixture rgb = getFixture("led-party-tcl-spot.json");

// Create moving head clients on channel 1 and 12 in universe 1 (= DMX1 = ID 0)
DMXClient movingHead1 = new DMXClient(1, movingHead, movingHeadMode);
DMXClient movingHead2 = new DMXClient(12, movingHead, movingHeadMode);
DMXUniverse universe1 = new DMXUniverse(0, List.of(movingHead1, movingHead2));

// Create RGB clients on channel 23 and 28 in universe 2 (= DMX2 = ID 1)
DMXClient rgb1 = new DMXClient(23, rgb);
DMXClient rgb2 = new DMXClient(28, rgb);
DMXUniverse universe2 = new DMXUniverse(1, List.of(rgb1, rgb2));

// Universe 1 (= DMX1): Set moving heads to center position
movingHead1.setValue("pan", (byte) 127);
movingHead1.setValue("tilt", (byte) 127);
movingHead2.setValue("pan", (byte) 127);
movingHead2.setValue("tilt", (byte) 127);
controller.render(universe1);

// Universe 2 (= DMX2): Set RGBs green and red
rgb1.setValue("green", (byte) 255);
rgb2.setValue("red", (byte) 255);
controller.render(universe2);</pre>

USB-to-DMX Support {#h2-1-usb-to-dmx-support}
---------------------------------------------

{{< youtube q7T66fzsym0 >}}

USB-to-DMX seems to be more challenging compared to the IP-to-DMX ArtNet protocol that was already integrated in V0.0.1. But V0.0.2 has been tested and is working as expected with a [DSD TECH SH-RS09B USB to DMX Cable for Freestyler QLC MagicQ and Pi Open Lighting](https://www.amazon.com.be/dp/B0F2MQZCWR). Such a device is handled on your computer as a serial device, and the [`com.fazecast.jSerialComm` library](https://fazecast.github.io/jSerialComm/) is used in my DMX512-library for the serial data transmission.

[Multiple example implementations are available in the sources](https://github.com/codewriterbv/DMX512/blob/main/src/main/java/be/codewriter/dmx512/Main.java#L59), this is a simplified version:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// Get a list of all available serial ports
var ports = DMXSerialDiscoverTool.getAvailablePorts();

// Log the serial ports
for (var port : ports) {
    LOGGER.info(port.getName());
}

// Create a serial controller
var controller = new DMXSerialController("tty.usbserial-BG01OL60",
        SerialProtocol.OPEN_DMX_USB);

// Send raw data, identical as in V0.0.1 with an IP controller
controller.render(0, new byte[]{(byte) 127, (byte) 127, 0, 0, 0, 0, 0, 0, 0, 0, 0});

// Are use a fixture, also similar as done with an IP controller
Fixture fixture = OFLParser.parse(new File("led-party-tcl-spot.json"));
DMXClient client = new DMXClient(23, fixture);
DMXUniverse universe = new DMXUniverse(0, client);
client.setValue("dimmer", (byte) 255);
client.setValue("red", (byte) 255);
controller.render(universe);</pre>

DMX512 Java Library {#h2-2-dmx512-java-library}
-----------------------------------------------

The library I created is open-source with its [sources on GitHub](https://github.com/codewriterbv/DMX512/) and [releases on Maven Central](https://central.sonatype.com/artifact/be.codewriter/dmx512).

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;dependency&gt;
    &lt;groupId&gt;be.codewriter&lt;/groupId&gt;
    &lt;artifactId&gt;dmx512&lt;/artifactId&gt;
    &lt;version&gt;${dmx512.version}&lt;/version&gt;
&lt;/dependency&gt;</pre>

DMX512 JavaFX Demo Project {#h2-3-dmx512-javafx-demo-project}
-------------------------------------------------------------

The JavaFX user interface demo application has been updated to use V0.0.2 of the library and has proven to work identically with IP-to-DMX and USB-to-DMX. Check the [sources on GitHub](https://github.com/codewriterbv/DMX512-Demo).
![](https://webtechie.be/images/2025/dmx/demo-app-picospot-channels.png)

Next Steps {#h2-4-next-steps}
-----------------------------

I also have an Enttec Open DMX USB interface, but I didn't get it working yet... With a chat-based coding approach, I have implemented several serial DMX512 protocols that you can test, but none have resulted in a working solution. I reached out to Enttec for more information about the protocol, but I haven't received a reply yet.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public enum SerialProtocol {
    /**
     * Simple serial transmission
     */
    OPEN_DMX_USB,
    /**
     * Direct FTDI chip communication
     */
    FTDI_CHIP_DIRECT,
    /**
     * Enttec Open DMX USB (FTDI-based)
     */
    ENTTEC_OPEN_DMX,
    /**
     * Generic serial-based DMX
     */
    GENERIC_SERIAL
}</pre>

If a solution for this interface can be found, or if other changes or improvements are added, a new version will be released.

<br />
