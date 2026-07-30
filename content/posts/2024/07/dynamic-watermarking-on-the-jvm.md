---
title: "Dynamic watermarking on the JVM"
slug: "dynamic-watermarking-on-the-jvm"
date: "2024-07-09T07:59:29+00:00"
lastmod: "2024-07-10T13:51:22+00:00"
description: "Displaying images on your website makes for an interesting problem: on one side, you want to make them publicly available; on the other, you want to protect them against undue use."
canonical: "https://blog.frankel.ch/dynamic-watermarking/1/"
authors:
  - "nicolas-frankel"
image: "https://foojay.io/wp-content/uploads/2024/06/faucet-1684902.jpg"
categories:
  - "Java"
  - "Java Core"
  - "Kotlin"
tags:
related_posts:
  - "dynamic-watermarking-with-imgproxy-and-apache-apisix"
  - "a-list-of-cache-providers"
  - "kubernetes-gateway-api"
enlighterjs: true
frozen: false
---

Displaying images on your website makes for an interesting problem: on one side, you want to make them publicly available; on the other, you want to protect them against undue use. The age-long method to achieve it is watermarking:
> A **digital watermark** is a kind of marker covertly embedded in a noise-tolerant signal such as audio, video or image data. It is typically used to identify ownership of the copyright of such signal. "Watermarking" is the process of hiding digital information in a carrier signal; the hidden information should, but does not need to, contain a relation to the carrier signal. Digital watermarks may be used to verify the authenticity or integrity of the carrier signal or to show the identity of its owners. It is prominently used for tracing copyright infringements and for banknote authentication.
>
> -- [Digital watermarking](https://en.wikipedia.org/wiki/Digital_watermarking)

The watermark can be visible to act as a deterrent to people stealing the image; alternatively, you can use it to prove its origin after it has been stolen.

However, if there are too many images on a site, it can be a burden to watermark them beforehand. It can be much simpler to watermark them dynamically. I searched for an existing JVM library dedicated to watermarking but surprisingly found nothing. We can achieve that in a Jakarata EE-based web app with the Java 2D API and a simple `Filter`.

The Java 2D API has been part of the JDK since 1.0, and it shows.

![](/images/posts/2024/07/dynamic-watermarking-on-the-jvm/java2d-class-diagram.png)

It translates into the following code:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">private fun watermark(imageFilename: String): BufferedImage? {
    val watermark = ImageIO.read(ClassPathResource("/static/$imageFilename").inputStream) ?: return null //1
    val watermarker = ImageIO.read(ClassPathResource("/static/apache-apisix.png").inputStream) //2
    watermark.createGraphics().apply {                         //3
        drawImage(watermarker, 20, 20, 300, 300, null)         //4
        dispose()                                              //5
    }
    return watermark
}</pre>

1. Get the original image
2. Get the watermarking image
3. Get the canvas of the original image
4. Draw the watermark. I was too lazy to make it partially transparent
5. Release system resources associated with this object

Other stacks may have dedicated libraries, such as [photon-rs](https://docs.rs/photon-rs/latest/photon_rs/multiple/fn.watermark.html) for Rust and WebAssembly. With this in place, we can move to the web part. As mentioned above, we need a `Filter`.

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">class WatermarkFilter : Filter {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val req = request as HttpServletRequest
        val imageFilename = req.servletPath.split("/").last()  //1
        val watermarked = watermark(imageFilename)             //2
        response.outputStream.use {
            ImageIO.write(watermarked, "jpeg", it)             //3
        }
    }
}</pre>

1. Get the image filename
2. Watermark the image
3. Write the image in the response output stream

I explained how to watermark images on a Java stack in this post. I did the watermark manually because I didn't find any existing library. Next week, I'll show a no-code approach based on infrastructure components.

**To go further:**

* [Digital watermarking](https://en.wikipedia.org/wiki/Digital_watermarking)
* [Java 2D API](https://docs.oracle.com/javase/8/docs/technotes/guides/2d/spec/j2d-intro.html)
* [Image Processing in WebAssembly](https://silvia-odwyer.github.io/photon/)

*** ** * ** ***

*Originally published at [A Java Geek](https://blog.frankel.ch/dynamic-watermarking/1/) on June 30^th^, 2024*

<br />

<br />
