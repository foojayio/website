---
title: "How does Java handle different Images and ColorSpaces – Part 4"
date: "2020-06-23T17:20:00+00:00"
lastmod: "2021-08-23T12:53:08+00:00"
description: "Unless you are creating all your images, by drawing then inside the code with the Graphics2D commands, you will need an image library to load images as BufferedImages. You will also need an Image library if you wish to save the results.  ImageIO is part of Java, it is free and it supports a range of Image formats including GIF, JPEG, PNG, and TIF. Because it is expandable, there are additional libraries to extend it."
authors:
  - "mark-stephens"
image: "Favicon-3-2.png"
categories:
  - "Java Core"
related_posts:
frozen: false
---

Unless you are creating all your images, by drawing then inside the code with the Graphics2D commands, you will need an image library to load images as BufferedImages. You will also need an Image library if you wish to save the results.

ImageIO is part of Java, it is free and it supports a range of Image formats including GIF, JPEG, PNG, and TIF. Because it is expandable, there are additional libraries to extend it. We recommend you check out the excellent [TwelveMonkeys image library](https://github.com/haraldk/TwelveMonkeys) which is free and Open Source.

ImageIO does have some limitations which can be summed up as:-

### 1. Could have better support for some image file formats.

JPEG/JPEG2000 support in ImageIO is not as good as it could be and the main reason we started writing our own Image library.

### 2. Does not support Image file formats

There are lots of image formats which ImageIO will not read and write.

### 3. Memory issues and Bugs

ImageIO uses native memory so it runs out of memory even if there is lots of Java heap. This can be a big issue with server software being shared between multiple users.

## Alternatives?

Luckily there are options. Below we give you some reasons to use the excellent Open Source Apache Imaging library or own commercial JDeli image library. We think they provide the two best options if you need something more than ImageIO. Which one is best will depend on your exact requirements.

### Why use Apache Imaging Library?

* Free and Open source
* prevents heap related JVM crashes
* Very wide range of [image formats](https://commons.apache.org/proper/commons-imaging/formatsupport.html) (but not JPEG2000)
* implements unsupported image formats in ImageIO
* reduced output file size over ImageIO
* Source code under Apache license.

Visit the [Apache Commons Imaging library](http://commons.apache.org/proper/commons-imaging/)
