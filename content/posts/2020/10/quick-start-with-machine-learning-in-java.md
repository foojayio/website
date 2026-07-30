---
title: "Quick Start with Machine Learning for Java"
slug: "quick-start-with-machine-learning-in-java"
date: "2020-10-07T01:21:00+00:00"
lastmod: "2023-02-16T14:07:14+00:00"
description: "So you're a Java developer and you want to do some machine learning. Meet JSR 381, a standard Java API for Visual Recognition using machine learning."
authors:
  - "zoran-sevarac"
image: "https://foojay.io/wp-content/uploads/2020/04/Favicon-3-2.png"
categories:
  - "Deep Netts"
  - "Machine Learning"
tags:
related_posts:
enlighterjs: true
frozen: false
---

So you're a Java developer and you want to do some machine learning. Some of the questions that you might be wondering about are---what can machine learning do for me anyway, which library to use, which algorithms, and is there a common standard API?

Since recently there is a standard API that was created to address exactly these questions. Meet [JSR 381](https://www.jcp.org/en/jsr/detail?id=381), a standard Java API for Visual Recognition using machine learning.

VisRec API was designed to be used for machine learning tasks for Java developers with a minimum background in machine learning and to be intuitive for Java developers getting started with machine learning.

Beside basic visual recognition tasks, such as image classification and object detection, it provides support for common machine learning tasks, such as classification and regression.

Since it is an official Java technology standard, multiple implementations are possible and currently there are two available:

* The [reference implementation](https://github.com/JavaVisRec/visrec-ri) based on [Deep Netts](https://www.deepnetts.com/)
* [Deep Java Library](https://github.com/JavaVisRec/visrec-djl) implementation from Amazon

Here is an example of Java code based on the VisRec API to build and use a classifier. Without any explanations, it should be clear to you what is happening:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">ImageClassifier&lt;BufferedImage&gt; classifier = 
NeuralNetImageClassifier.builder()
   .inputClass(BufferedImage.class)
   .imageHeight(28)
   .imageWidth(28)
   .labelsFile(dataSet.getLabelsFile())
   .trainingFile(dataSet.getTrainingFile())
   .networkArchitecture(new File("mnist.json"))
   .modelFile(new File("mnist.dnet"))
   .maxError(1.4f)
   .maxEpochs(100)
   .learningRate(0.01f)
   .build();

BufferedImage image = ImageIO.read(new File(input.getFile()));
Map&lt;String, Float&gt; results = classifier.classify(image);
</pre>

For detailed step-by-step instructions and examples, see the [getting started guide](https://github.com/JavaVisRec/visrec-api/wiki/Getting-Started-Guide).
