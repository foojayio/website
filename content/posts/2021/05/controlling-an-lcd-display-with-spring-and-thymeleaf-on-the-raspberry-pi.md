---
title: "Controlling an LCD Display with Spring and Thymeleaf on the Raspberry Pi"
slug: "controlling-an-lcd-display-with-spring-and-thymeleaf-on-the-raspberry-pi"
date: "2021-05-19T07:10:12+00:00"
lastmod: "2021-05-19T07:10:16+00:00"
description: "Igor De Souza shares a lot fun and inspirational experiments with Java on Raspberry Pi. Some of those were already shared here on Foojay.io."
canonical: "http://www.igfasouza.com/blog/spring-thymeleaf-raspberry-pi-lcd/"
authors:
  - "frankdelporte"
  - "igor-de-souza"
image: "/images/posts/2021/05/controlling-an-lcd-display-with-spring-and-thymeleaf-on-the-raspberry-pi/spring_lcd.jpg"
categories:
  - "Raspberry Pi"
tags:
related_posts:
  - "foojay-podcast-55"
  - "blink-a-led-on-raspberry-pi-with-vaadin"
  - "pi4j-welcomes-java-21-on-the-raspberry-pi"
  - "a-fresh-look-at-embedded-java"
enlighterjs: true
frozen: false
---

[Igor De Souza](https://twitter.com/Igfasouza) shares on his blog a lot fun and inspirational experiments with Java on Raspberry Pi. Some of those were already shared here on Foojay.io:

1. [Electronics \& Quarkus Qute on Raspberry Pi](https://foojay.io/today/electronics-quarkus-qute-on-raspberry-pi/)
2. [Electronics \& Micronaut Velocity with Raspberry Pi](https://foojay.io/today/electronics-micronaut-velocity-with-raspberry-pi/)
3. [Vert.x Example on the Raspberry Pi with a Virtual Potentiometer](https://foojay.io/today/vert-x-example-on-the-raspberry-pi-with-a-virtual-potentiometer/)

This time we want to highlight his work which combines a web app made with Spring and Thymeleaf, to control an LCD display connected to a Raspberry PI.
![](/images/posts/2021/05/controlling-an-lcd-display-with-spring-and-thymeleaf-on-the-raspberry-pi/spring_lcd.jpg)

Frameworks and components {#h2-0-frameworks-and-components}
-----------------------------------------------------------

1. [**Spring Boot**](https://spring.io/projects/spring-boot) is an extension of the Spring framework, which eliminates the boilerplate configurations required for setting up a Spring application.
2. [**Thymeleaf**](https://www.thymeleaf.org/) is a Java template engine for processing and creating HTML, XML, JavaScript, CSS, and text.
3. The **LCD display is an [ST7920 model](https://www.benl.ebay.be/sch/i.html?_from=R40&_trksid=m570.l1313&_nkw=St7920&_sacat=0)** (or 12864ZW), which is probably the cheapest 128×64 graphic LCD that you can find.
4. This [**"Universal Character/Graphics LCD Library for Java"**](https://github.com/ribasco/ucgdisplay) by Rafael Ibasco is used to control the display.

![](/images/posts/2021/05/controlling-an-lcd-display-with-spring-and-thymeleaf-on-the-raspberry-pi/lcd_example01-1024x365.jpg)

Wiring {#h2-1-wiring}
---------------------

<figure class="wp-block-image size-large is-resized">
 <img decoding="async" src="/images/posts/2021/05/controlling-an-lcd-display-with-spring-and-thymeleaf-on-the-raspberry-pi/glcd-pinout-683x1024.jpg" alt="" class="wp-image-44923" width="683" height="1024">
</figure>

The display is connected according to this table indicating the physical PIN number and its according BCM number used in the code:
![](/images/posts/2021/05/controlling-an-lcd-display-with-spring-and-thymeleaf-on-the-raspberry-pi/LCD_raspberry_map.png)

Project idea {#h2-2-project-idea}
---------------------------------

Goal of this project, is to setup a simple Spring Boot Thymeleaf application, which shows a form with a 128×64 table. Each table position is the representation of a pixel of the LCD graphic display. This is achieved by using an array of bits with all the positions of this table.
![](/images/posts/2021/05/controlling-an-lcd-display-with-spring-and-thymeleaf-on-the-raspberry-pi/spring_thymeleaf_app01-1024x713.png)

The user interface is limited with a button to convert the table to an array, and one to create a preview picture of the result.
![](/images/posts/2021/05/controlling-an-lcd-display-with-spring-and-thymeleaf-on-the-raspberry-pi/PXL_20210502_193343880-scaled-1-1024x768.jpg)

The LCD graphic Display expects an Array of Bytes in the XBM format. [XBM is a monochrome bitmap format](https://en.wikipedia.org/wiki/X_BitMap) in which data is stored as a C language data array. It is primarily used for the storage of cursor and icon bitmaps for use in X graphical user interfaces.

A method was added to the application to convert the Array of bits into an Array of Bytes to match the XBM format.

Many tools including GIMP can save an image as XBM. A [nice step-by-step instruction that the API docs show is here](https://sandhansblog.wordpress.com/2017/04/16/interfacing-displaying-a-custom-graphic-on-an-0-96-i2c-oled/).

Code {#h2-3-code}
-----------------

A few examples of the code in the project.

This part configures the control of the LCD display with the BCM pin numbers listed above in the table.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">config = GlcdConfigBuilder
       //Use ST7920 - 128 x 64 display, SPI 4-wire Hardware
       .create(Glcd.ST7920.D_128x64, GlcdCommProtocol.SPI_SW_4WIRE_ST7920)
       //Set to 180 rotation
       .option(GlcdOption.ROTATION, GlcdRotation.ROTATION_180)
       .option(GlcdOption.PROVIDER, Provider.SYSTEM)
       .mapPin(GlcdPin.SPI_MOSI, 19)
       .mapPin(GlcdPin.SPI_CLOCK, 13)
       .mapPin(GlcdPin.CS, 26)
       .build();</pre>

Converting the bits array to a byte array for the XBM format:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">private static byte[] encodeToByteArray(int[] bits) {
    BitSet bitSet = new BitSet(bits.length);
    for (int index = 0; index  0);
        bitSet.set(index, bits[index] &gt; 0);
    }
    return bitSet.toByteArray();
}</pre>

You can get the full code from [this GitHub repository](https://github.com/igfasouza/Spring-Thymeleaf-Raspberry-PI-LCD).

Result {#h2-4-result}
---------------------

There are some XBM files inside the resources folder and you can follow up on the API example to display these images.

<figure class="wp-block-gallery columns-2 is-cropped wp-block-gallery-1 is-layout-flex wp-block-gallery-is-layout-flex">
 <ul class="blocks-gallery-grid">
  <li class="blocks-gallery-item">
   <figure>
    <a href="/images/posts/2021/05/controlling-an-lcd-display-with-spring-and-thymeleaf-on-the-raspberry-pi/lcd_example02.jpg"><img loading="lazy" decoding="async" width="1024" height="404" src="/images/posts/2021/05/controlling-an-lcd-display-with-spring-and-thymeleaf-on-the-raspberry-pi/lcd_example02-1024x404.jpg" alt="" data-id="44927" data-full-url="https://foojay.io/wp-content/uploads/2021/05/lcd_example02.jpg" data-link="https://foojay.io/?attachment_id=44927" class="wp-image-44927"></a>
   </figure></li>
  <li class="blocks-gallery-item">
   <figure>
    <a href="/images/posts/2021/05/controlling-an-lcd-display-with-spring-and-thymeleaf-on-the-raspberry-pi/lcd_example03.jpg"><img loading="lazy" decoding="async" width="1024" height="364" src="/images/posts/2021/05/controlling-an-lcd-display-with-spring-and-thymeleaf-on-the-raspberry-pi/lcd_example03-1024x364.jpg" alt="" data-id="44928" data-full-url="https://foojay.io/wp-content/uploads/2021/05/lcd_example03.jpg" data-link="https://foojay.io/?attachment_id=44928" class="wp-image-44928"></a>
   </figure></li>
 </ul>
</figure>

Some additional work is still needed to finish the logic for a combobox to show all XMB files inside resources and once selected, display that image on the LCD. But as you can see in this video, the Thymeleaf table can already be used to draw an image, which can be displayed on the screen.

{{< youtube 57Si7NI6_b8 >}}

Remember to use the hashtag ***#JavaOnRaspberryPi*** on Twitter to show the world Raspberry Pi with Java.

Links {#h2-5-links}
-------------------

* <https://en.wikipedia.org/wiki/X_BitMap>
* <https://www.fileformat.info/format/xbm/egff.htm>
* [https://arduino-tutorials.net/tutorial/control-graphic-lcd-display-spi-st7920-128×64-with-arduino](https://arduino-tutorials.net/tutorial/control-graphic-lcd-display-spi-st7920-128%C3%9764-with-arduino)
* <https://spring.io/projects/spring-boot>
* <https://www.thymeleaf.org/>
* <https://github.com/ribasco/ucgdisplay>

*** ** * ** ***

Originally shared by [Igor De Souza on his blog](http://www.igfasouza.com/blog/spring-thymeleaf-raspberry-pi-lcd/).
