---
title: "Interview: Frank Delporte, Foojay Raspberry Pi Community Manager"
date: "2020-09-07T13:20:38+00:00"
lastmod: "2025-02-20T10:53:16+00:00"
description: "Today we introduce a community manager for all things Raspberry Pi on foojay, Frank Delporte. He'd love to publish your Java/Raspberry Pi content on foojay!"
authors:
  - "geertjan-wielenga"
image: "vik.png"
categories:
  - "Interviews"
  - "JavaFX"
  - "Pi4J"
  - "Raspberry Pi"
tags:
related_posts:
frozen: false
---

*Today we'd like to introduce a new community manager for all things Raspberry Pi on foojay: Frank Delporte!*

**Welcome, Frank!** **Tell us about yourself. 🙂**  

{{< img src="pasfoto-2.jpg" class="alignleft size-large is-resized" width="173" height="208" >}}

Hi, I'm Frank Delporte, living in Belgium. I started programming when I was 10 on my Commodore 64.

I started my professional career as a video editor for television, 25 years ago. I went through all stages from video, multimedia, websites to back-end development using different technologies.

But now I'm mainly doing Java development and in my free time doing experiments within the Java ecosystem.

#### **How did you get into the world of Raspberry Pi?**

Seven years ago, I started two CoderDojo clubs, where we introduce children (age 7 - 18) to the digital world, with programming in Scratch, Minecraft, Mindstorms, Arduino, etc. The coaches are volunteers who bring their interests and knowledge to the club. That's where I first saw what can be done with cheap electronics, combined with Arduino and the Raspberry Pi.

My first own experiments with the Raspberry Pi was [a simple Pong game with physical buttons](https://webtechie.be/post/2017-12-20-pong-on-a-raspberry-pi), which we used at a few events at my son's school, but I have to admit I didn't like the UI-programming with Python.

That's when I started experimenting with JavaFX on the Pi.

#### What do you find so interesting about it?

It works! It's cheap! It's powerful! You have GPIOs! 🙂

I have to admit, though, that I had to search for the best way to use JavaFX on the Raspberry Pi but I shared all my experiments [on my blog](https://webtechie.be/post/2020-04-08-installing-java-and-javafx-on-raspberry-pi/) and now also on foojay.

And, of course, the added value of the "general-purpose input/output" pins is very important. There is an endless list of components you can control, starting from a LED which costs a few cents up to more expensive motor controllers, relays boards, screens...

#### What's the role of Java and JavaFX in that context?

The [Pi4J library](https://www.pi4j.com) allows you to control these GPIOs in a very easy way and I joined the Pi4J-team to help building a new version that will be fully Java11+ and modular to provide even more flexibility and possibilities. If you combine this with JavaFX, you can build beautiful user interfaces that can control different components.

But the Raspberry Pi is also a perfect match for "back-end" Java applications to store data in a database or provide a message queue to your system. The new Raspberry Pi 4 with up to 8 GB RAM is a really powerful but inexpensive big computer with a small size.

#### What have you created with that combination of technologies?

My son plays drums and his drum kit is above our living room. So, I created an isolated drum booth to reduce the sound level for us.

<figure class="wp-block-embed-vimeo wp-block-embed is-type-video is-provider-vimeo wp-embed-aspect-16-9 wp-has-aspect-ratio">
 <div class="wp-block-embed__wrapper">
  <iframe title="Drum booth controller with Java, JavaFX, Raspberry Pi and Arduino" src="https://player.vimeo.com/video/402179641?dnt=1&amp;app_id=122963" width="500" height="281" frameborder="0" allow="autoplay; fullscreen; picture-in-picture; clipboard-write"></iframe>
 </div>
</figure>

But of course he doesn't hear us if he needs to stop playing for dinner for example. For that reason, and of course, because it was a fun project, I created a touch screen controller for him to control multiple different lights and LED strips.

It uses Java, JavaFX, Pi4J, and TilesFX (make sure to [check out this project](https://github.com/HanSolo/tilesfx) by [Gerrit Grunwald](https://twitter.com/hansolo_)) for the user interface and to control two relay boards. This also interacts through serial communication with an Arduino which controls the LED strips.

The Java application also provides a very simple web interface for us to start the "red light" signal to tell him it's time to stop playing, if we are too lazy to go up the stairs. We hardly use it, but it's there because it is so easy to provide such functionality with Java.

No rocket science, just a fun project using different technologies to see what could be done with Java on the Raspberry Pi.

More info and links to the sources [can be found on my blog](https://webtechie.be/post/2020-03-30-drumbooth-controller-with-java-javafx-raspberrypi-arduino/).

#### And you even wrote a book, tell us about that!

{{< img src="ebook-paperbook.jpg" class="alignleft size-large is-resized" alt="ebook-paperbook.jpg" width="355" height="236" >}}

When I started my Java on Raspberry Pi experiments, I wrote a first 4-page article for "The MagPi", the official Raspberry Pi magazine, which was [published in the Dutch and French edition](https://webtechie.be/articles/).

This triggered the question by Elektor if this could be the subject for a book. I didn't find any recent one so the next day I already started writing "Getting Started with Java on Raspberry Pi"...

It was a hard job, as I also wanted to create a lot of different example applications and even ended up creating some Java libraries which are now available on Maven Central ([here](https://webtechie.be/post/2019-10-02-led-number-display-javafx-library-published-to-maven/) and [here](https://webtechie.be/post/2019-11-25-resistor-color-codes-and-calculations-a-java-maven-library/)).

As I'm a software developer, I still had a lot to learn on the hardware side, but I'm proud I could cover a wide range of topics in the book, from Java and JavaFX history to how to use Maven and the GPIO, as well as on controling different electronic components, setting up a Mosquitto queue on the Raspberry Pi to connect multiple systems, using Spring on the Raspberry Pi to store sensor data... and much more.

The book is available [as an ebook on Leanpub](https://leanpub.com/gettingstartedwithjavaontheraspberrypi/) and [paper book on the Elektor website](https://www.elektor.com/getting-started-with-java-on-the-raspberry-pi).

<figure class="wp-block-embed-vimeo wp-block-embed is-type-video is-provider-vimeo wp-embed-aspect-16-9 wp-has-aspect-ratio">
 <div class="wp-block-embed__wrapper">
  <iframe loading="lazy" title="Getting started with Java on Raspberry Pi - Intro to the book" src="https://player.vimeo.com/video/400172853?dnt=1&amp;app_id=122963" width="500" height="281" frameborder="0" allow="autoplay; fullscreen; picture-in-picture; clipboard-write"></iframe>
 </div>
</figure>

**You'll now be leading the community side of the Raspberry Pi on foojay. How do you see that role, what kind of content are you looking for, and how can people reach you?**

Java on Raspberry Pi is a controversial topic. And, I agree, some stuff is easier to do with Python in relation to the GPIOs. In my book, I even have an example with a JavaFX application which calls a Python script to demonstrate this.

But, as always, use the tool that fits your needs and that you love to use the most. In my case, that's Java and JavaFX, and with those you can create powerful and beautiful applications on and for the Raspberry Pi. With the newest boards supporting 64bit operating systems, we can even make more use of their power and flexibility!

I'm really curious about what's already been created with Java on the Raspberry Pi and what we can all learn together from these experiences. You can contact me with a direct message on Twitter to discuss further and share articles for publication on foojay: <https://twitter.com/frankdelporte>.
