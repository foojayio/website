---
title: "Foojay Podcast #54: Music and MIDI with Java and Kotlin"
slug: "foojay-podcast-54"
date: "2024-06-24T18:23:33+00:00"
lastmod: "2025-11-13T08:45:42+00:00"
description: "Within OpenJDK, there is a whole Java package dedicated to MIDI communication and data handling. Is it up to date? Are there better approaches now? And what can we do with music, Java, and Kotlin?"
authors:
  - "frankdelporte"
  - "geert-bevin"
image: "podcast-guests-java-midi.png"
categories:
  - "Desktop"
  - "Java Core"
  - "Kotlin"
  - "Podcast"
tags:
related_posts:
  - "foojay-podcast-53"
  - "foojay-podcast-52"
  - "foojay-podcast-51"
frozen: false
---

MIDI is a universal standard for communicating between musical instruments and computers.

Within OpenJDK, there is a whole Java package dedicated to MIDI communication and data handling. Is it up to date? Are there better approaches now? And what can we do with music, Java, and Kotlin?

Let's find out...

Video {#h2-0-video}
-------------------

{{< youtube tHKZA9yqIVM >}}

Podcast Apps {#h2-1-podcast-apps}
---------------------------------

You can listen and subscribe to the Foojay Podcast on:

* [Spotify](https://open.spotify.com/show/6CpTfgn9LirzJGAtc4ICdQ)
* [Apple Podcasts](https://podcasts.apple.com/be/podcast/foojay-io-the-friends-of-openjdk/id1652281304)
* And most others...

Guests {#h2-2-guests}
---------------------

### Atsushi Eno {#h3-3-atsushi-eno}

* <https://atsushieno.github.io/>
* [@\[email protected\]](https://g0v.social/@atsushieno)
* [@\[email protected\]](https://fedibird.com/@atsushieno)

### Geert Bevin {#h3-4-geert-bevin}

* <https://www.linkedin.com/in/gbevin/>
* <https://gbevin.com/cv/>
* <https://www.uwyn.com/>
* <https://www.gbevin.com/>

Content {#h2-5-content}
-----------------------

00:00 Introduction of the topic and guests  

04:27 What is MIDI?  

Learn more about MIDI and the javax.sound implementation in OpenJDK:  
<https://docs.oracle.com/javase/tutorial/sound/overview-MIDI.html>  
<https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/sound/midi/package-summary.html>  
<https://github.com/openjdk/jdk/tree/master/src/java.desktop/share/classes/javax/sound/midi>  
<https://www.baeldung.com/java-packages-vs-javax>  

09:53 MIDI Polyphonic Expression (MPE)  
<https://roli.com/mpe>  
<https://midi.org/midi-polyphonic-expression-mpe-specification-adopted>   
<https://midi.org/insights>   

11:23 Instruments require real-time systems  

15:18 Why Atsushi used Kotlin for ktmidi  
<https://github.com/atsushieno/ktmidi>  
<https://github.com/jazz-soft/JZZ>  
<https://github.com/thestk/rtmidi>  

Applications created with ktmidi: <https://github.com/atsushieno/ktmidi/discussions/14>  
[https://play.google.com/store/apps/details?id=org.androidaudioplugin.resident_midi_keyboard\&pli=1](https://play.google.com/store/apps/details?id=org.androidaudioplugin.resident_midi_keyboard&pli=1)  

23:31 Using ktmidi with JavaFX and the benefits of Kotlin  
<https://melodymatrix.rocks>  

25:00 Geert sticks to Java and loves the 6-month releases  

27:24 Apps created by Geert for various Apple devices  
<https://uwyn.com/midiwrist-unleashed>  

31:11 Atsushi uses MIDI to develop audio plugins  

32:34 Geert found back his love for Java and created Rife2 and BLD  
<https://rife2.com>  
<https://rife2.com/bld>  
<https://software.moogmusic.com/store>  

Erik Thauvin <https://www.linkedin.com/in/ethauvin/>   

43:13 How things just happen and finding a good open-source approach  
<https://codewithrockstar.com>  
<https://webtechie.be/post/2024-06-18-jfxinaction-christopher-schnick>  
<https://www.jdeploy.com>  

50:46 Conclusions
