---
title: "Deep Learning in Java Using Deep Netts (Part 2)"
slug: "getting-started-with-deep-learning-in-java-using-deep-netts-part-2"
date: "2022-08-03T09:48:18+00:00"
lastmod: "2023-07-11T09:37:04+00:00"
description: "For more complex deep learning challenges, more data, and need better performance, take a look at Deep Netts Professional Edition."
authors:
  - "zoran-sevarac"
image: "deepnetts.png"
categories:
  - "Deep Netts"
  - "Machine Learning"
  - "NetBeans"
tags:
related_posts:
  - "getting-started-with-deep-learning-in-java-using-deep-netts"
  - "deep-learning-in-java-for-nuclear-physics-using-deep-netts"
  - "deep-learning-in-java-for-drug-discovery"
enlighterjs: true
frozen: false
---

[Deep Netts](https://www.deepnetts.com/) is a deep learning development toolkit that enables Java developers to easily add modern AI to their apps. It provides a deep learning IDE and a Java-native deep learning library for embedding AI models into Java apps.

In [part 1](https://foojay.io/today/getting-started-with-deep-learning-in-java-using-deep-netts/), we looked at the Deep Netts Community Edition, which is capable of solving basic machine learning problems, and provides a standard set of features.

If you have more complex challenges, more data, and need better performance or just want friendly tools to quickly try and learn AI, you should take a look at Deep Netts Professional Edition.

Download \& Installation {#h2-0-download-installation}
------------------------------------------------------

1. Download Deep Netts from the [official download page](http://https://deepnetts.com:10172/download/faces/user/signUpAndDownload.xhtml "official download page").
2. Run Maven installation scripts provided by the downloaded package to install Deep Netts JARs into your local Maven repo.

A detailed step by step installation guide is [available here](http://https://www.deepnetts.com/blog/deep-learning-in-java-getting-started-with-deep-netts.html "available here").

Note that the free download is fully functional and that it can be used for development. If you build a working prototype, it is possible to get a free low-volume production license.

Example: Hand-Written Digits Recognition {#h2-1-example-hand-written-digits-recognition}
----------------------------------------------------------------------------------------

This example shows how to build a deep learning model in Java that can learn how to classify images, given the set of example images with corresponding category labels/classes.

The data set consists of 60000 images of handwritten digits. Each image is size of 28x28 pixels. Few sample images are shown in the image below.

![](https://miro.medium.com/max/720/0*hQYNvxeSBRnr062z)

As you can see, there is a lot of variation in each digit, and it would be impossible to come out with some set of rules to recognize these digits - that's why we want to build a model which will be capable to extract and learn patterns from these examples.

The following code snipet loads images and corresponding labels that will be used to train/build the model.

<pre class="EnlighterJSRAW" data-enlighter-language="java">ImageSet imageSet = new ImageSet(imageWidth, imageHeight);
imageSet.setInvertImages(true); 
imageSet.loadLabels(new File(labelsFile));
imageSet.loadImages(new File(trainingFile), 1000);</pre>

Convolutional neural networks are type of deep learning models which are capable to learn patterns in image pixels, and they will be used to demonstrate how Deep Netts can be used to accomplish this.

The image bellow shows the architecture of a typical convolutional neural network which consists of a few blocks of convolutional and pooling operations which perform feature detection/extraction, followed by the fully connected layers that perform classification.

![](https://miro.medium.com/max/1750/0*oNCO_BeAyzirc2s3)

The convolutional neural network can be created using the following Java code snippet in Deep Netts, using [ConvolutionalNetwork](https://www.deepnetts.com/apidocs/deepnetts/net/ConvolutionalNetwork.html) class and it's corresponding [builder](https://www.deepnetts.com/apidocs/deepnetts/net/ConvolutionalNetwork.Builder.html).

<pre class="EnlighterJSRAW" data-enlighter-language="java">// create convolutional neural network architecture
ConvolutionalNetwork neuralNet = ConvolutionalNetwork.builder()
            .addInputLayer(imageWidth, imageHeight)
            .addConvolutionalLayer(12, 5)
            .addMaxPoolingLayer(2, 2)
            .addFullyConnectedLayer(60)
            .addOutputLayer(labelsCount, ActivationType.SOFTMAX)
            .hiddenActivationFunction(ActivationType.RELU)
            .lossFunction(LossType.CROSS_ENTROPY)
            .randomSeed(123)
            .build();
</pre>

Full source of this example with comments and more detailed explanation is available at [GitHub](https://github.com/deepnetts/How-to-Get-Started-With-Deep-Learning-in-Java) and [step by step tutorial](https://www.deepnetts.com/blog/using-deep-learning-in-java-for-image-recognition.html).

Deep Learning IDE {#h2-2-deep-learning-ide}
-------------------------------------------

Deep Netts IDE provides visual tools and wizards that simplify building, debugging, and experimenting with various AI-related development activities.

Not only does it increases productivity in AI development, but it also helps developers to better understand the functioning of deep learning and problems they experience.

It is built on top of [Apache NetBeans](https://netbeans.apache.org/), following proven practices in UX and architecture.

![](https://miro.medium.com/max/1750/0*ilQdE60YGfg6BUBd)

Differences Compared to Community Edition {#h2-3-differences-compared-to-community-edition}
-------------------------------------------------------------------------------------------

The professional edition provides tools and features that can significantly improve and simplify building your AI models.

This includes:

1. **Multi threading support.** Faster execution, both training and inference
2. **IDE with visual tools and debugger.** Highly productive environment, enables understanding and fixing models and data.
3. **Improved algorithms.** And other features that provide faster training and higher accuracy.

Links {#h2-4-links}
-------------------

* [Deep Netts download](https://deepnetts.com:10172/download/faces/user/signUpAndDownload.xhtml)
* [Fulls source of the example on GiHub](https://github.com/deepnetts/How-to-Get-Started-With-Deep-Learning-in-Java)
* [Step by step tutorial with explanations](https://www.deepnetts.com/blog/using-deep-learning-in-java-for-image-recognition.html)
