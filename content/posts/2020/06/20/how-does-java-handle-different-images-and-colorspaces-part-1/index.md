---
title: "How does Java handle different Images and ColorSpaces – Part 1"
date: "2020-06-20T17:03:00+00:00"
lastmod: "2021-08-23T12:24:46+00:00"
description: "Java makes images simple to use. You can work with a BufferedImage and just load or save this to any supported image file format. A BufferedImage includes lots of functionality which allows you to render and process the image, with all the complexity and implementation hidden by Java. A BufferedImage can even be used as a Graphics2D canvas which can be drawn on. Here is some example code.  While Java removes a lot of Image complexity, it is worth understanding in more detail how images work. In this series of articles, we will be diving deep into how BufferedImage provides this abstraction, how different types of images work and how you can access the low-level Image data. - by Mark Stephens"
authors:
  - "mark-stephens"
image: "Favicon-3-2.png"
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

One of the attractions of Java is the way it abstracts and simplifies many programming constructs. In place of Tiffs, PNGs, JPEGs and other Image formats, you get a simple BufferedImage object. ImageIO and other third-party libraries such as our own [JDeli image library](https://www.idrsolutions.com/jdeli/) provide methods to read and write a BufferedImage.

We have spent a lot of time working with different images formats in the process of writing the [JDeli](https://www.idrsolutions.com/jdeli/) Image library as a [replacement for ImageIO](https://www.idrsolutions.com/jdeli/imageio-replacement/) and the aim of this series is to share that knowledge to a wider audience.

Java makes images simple to use. You can work with a BufferedImage and just load or save this to any supported image file format. A BufferedImage includes lots of functionality which allows you to render and process the image, with all the complexity and implementation hidden by Java. A BufferedImage can even be used as a Graphics2D canvas which can be drawn on. Here is some example code.

```java
//load image with ImageIO or JDeli
BufferedImage image = ImageIO.read(new File("image.png"));
BufferedImage image = JDeli.read(new File("image.png"));

//draw a red diagonal line on it
Graphics2D g2 = image.createGraphics();
g2.setColor(Color.red);
g2.drawLine(0, 0, image.getWidth(), image.getHeight());

//save image with ImageIO or JDeli
ImageIO.write(image, "PNG", new File("image.png"));
JDeli.write(image, "PNG", new File("image.png"));
```

While Java removes a lot of Image complexity, it is worth understanding in more detail how images work. In this series of articles, we will be diving deep into how BufferedImage provides this abstraction, how different types of images work and how you can access the low-level Image data.

See you next time when we will look at ColorSpaces.
