---
title: "How does Java handle different Images and ColorSpaces – Part 3"
slug: "how-does-java-handle-different-images-and-colorspaces-part-3-introducing-the-bufferedimage"
date: "2020-06-22T17:16:00+00:00"
lastmod: "2021-08-23T12:52:36+00:00"
description: "BufferedImage is one of the most useful Java abstractions. It hides all the complexity of different types of images whilst allowing access to the underlying data. Under the hood, a BufferedImage can be many types of image.  Java includes support to load and save images in various formats using ImageIO, and other libraries such as Apache Imaging and our JDeli library also offer this feature.  Next time we will talk more about ImageIO and other Image libraries. - by Mark Stephens"
authors:
  - "mark-stephens"
image: "https://blog.idrsolutions.com/wp-content/uploads/2020/01/Screenshot-2020-01-20-at-15.22.53-284x300.png"
categories:
  - "Uncategorized"
tags:
related_posts:
frozen: false
---

BufferedImage is one of the most useful Java abstractions. It hides all the complexity of different types of images whilst allowing access to the underlying data. Under the hood, a BufferedImage can be many types of image. This is the list of types visible in an IDE.  
![BufferedImage types](https://blog.idrsolutions.com/wp-content/uploads/2020/01/Screenshot-2020-01-20-at-15.22.53-284x300.png)

The differences between the types of BufferedImage are:-

1. Range of Colours (Binary will give back and white only, RGB will provide 24-bit color)
2. Opacity (only available in ARGB -- the A is an 8 bit Alpha channel).
3. Amount of memory used (ARGB uses the most as 4 bytes per pixel).
4. Size of the saved file.

A BufferedImage generally uses the RGB, Gray or Binary Color Spaces. Data can be in other formats, and the raw raster data can be loaded and accessed for CMYK and YCCK, but the Colors will not display correctly. BufferedImage provides lots of methods to access the raw image data, including the Raster, pixel and ColorModel used.

All BufferedImages work the same, so the same code can be used to manipulate a BufferedImage. A Graphics2D drawing surface can be obtained directly from an instance of BuggeredImage with **getGraphics()** and used to draw shapes, text and other images over the original image. All are converted to the Image type used. So a Binary Image will only show black and white and any colors used will be converted.

BufferedImages can be converted into other supported ColorSpaces types by using a ColorConvertOp. Java can be used for Image processing either via manipulating the BufferedImage directly or the Graphics2D object.

So BufferedImage offers a really flexible abstraction which makes it very easy to make use of images in Java. Java developers no longer need to worry about image types and formats.

Java includes support to load and save images in various formats using ImageIO, and other libraries such as Apache Imaging and our JDeli library also offer this feature.

Next time we will talk more about ImageIO and other Image libraries.
