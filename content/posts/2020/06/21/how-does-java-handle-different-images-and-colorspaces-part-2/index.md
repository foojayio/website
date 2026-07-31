---
title: "How does Java handle different Images and ColorSpaces – Part 2"
slug: "how-does-java-handle-different-images-and-colorspaces-part-2"
date: "2020-06-21T17:11:00+00:00"
lastmod: "2021-08-23T12:25:20+00:00"
description: "There are lots of different ways of describing Color. As developers, we are most familiar with the RGB model, where every color is defined by mixing Red, Green and Blue together. In the print world, CMYK is very common, where colors or printed by literally mixing different amounts of Cyan, Magenta, Yellow and Key (black). You may also come across other ways of describing color such as DeviceN. There are also lots of different versions of RGB.  Next time we will talk more about BufferedImages. - by Mark Stephens"
authors:
  - "mark-stephens"
image: "https://blog.idrsolutions.com/wp-content/uploads/2019/11/Screenshot-2019-11-19-at-10.43.30-134x300.png"
categories:
  - "Java Core"
tags:
related_posts:
  - "learning-java-as-a-first-language"
  - "running-single-file-java-source-code-without-compiling-part-1"
  - "fantastic-jvms-and-where-to-find-them"
  - "highlights-of-changes-to-the-core-java-platform"
frozen: false
---

There are lots of different ways of describing Color. As developers, we are most familiar with the RGB model, where every color is defined by mixing Red, Green and Blue together. In the print world, CMYK is very common, where colors or printed by literally mixing different amounts of Cyan, Magenta, Yellow and Key (black). You may also come across other ways of describing color such as DeviceN. There are also lots of different versions of RGB.

Java is based on RGB and partially supports other ColorSpaces. The ColorSpace class itself defines lots of constants for ColorSpaces, along with some nice methods to convert to and from RGB and CIEXYZ (a mathematical ColorSpace useful for convert to and from other ColorSpaces with).  
![Java Colorspaces](https://blog.idrsolutions.com/wp-content/uploads/2019/11/Screenshot-2019-11-19-at-10.43.30-134x300.png)

ColorSpaces can be defined using a CIE profile file and if you have one for a ColorSpace you can create an instance of the ColorSpace and convert color values between ColorSpaces). But BufferedImage itself only understands a more limited subset of formats.  
![BufferedImage colorspaces](https://blog.idrsolutions.com/wp-content/uploads/2019/11/Screenshot-2019-11-19-at-10.41.31-300x126.png)

Java can generally load lots of image types and allow access to the raw Image data, but it does not fully understand the data -- so you will get oddly coloured images like the one [in this post](https://blog.idrsolutions.com/2011/10/ycck-color-conversion-in-pdf-files/).

If you wanted to view a CMYK image as a BufferedImage in Java directly, you would need to convert the Image data into RGB before you could display it properly. You would need an ICC CMYK profile to create a CMYK ColorSpace and then I should you how to transform the data [in this blog post.](https://blog.idrsolutions.com/2012/02/java-cmyk-to-rgb-conversion-speed-comparison-of-diy-versus-letting-java-do-it/)

Next time we will talk more about BufferedImages.
