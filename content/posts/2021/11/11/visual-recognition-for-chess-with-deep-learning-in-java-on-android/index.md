---
title: "Visual Recognition for Chess with Deep Learning in Java on Android"
slug: "visual-recognition-for-chess-with-deep-learning-in-java-on-android"
date: "2021-11-11T20:07:51+00:00"
lastmod: "2023-01-08T07:42:34+00:00"
description: "Learn about Deep Netts, a lightweight Java-native library, easy to learn, and solves many technical challenges related to ML."
authors:
  - "krasimir-topchiyski"
  - "zoran-sevarac"
image: "Favicon-3-2.png"
categories:
  - "Deep Netts"
  - "Machine Learning"
  - "Use Cases"
tags:
related_posts:
  - "deep-learning-in-java-for-drug-discovery"
  - "creating-a-snake-game-with-javafx-fxgl-in-three-pair-programming-sessions"
  - "foojay-podcast-1"
frozen: false
---

**Find out how can you use AI in Java to build applications that can see and understand the world. This article describes an Android chessboard position scanner built with pure Java deep learning engine [Deep Netts](https://www.deepnetts.com/).**

Over the last few decades, the technological advancement in the Artificial Intelligence (AI) field has greatly assisted many chess grand masters' preparation as well as understanding chess theory. There are programs called chess engines, like Bagatur, Stockfish, and Komodo, which play extremely strong chess. These engines make deep and beautiful moves, but sometimes these moves are difficult to understand even by chess experts. Clearly, AI/ML has allowed chess engines to become quite advanced, but they are only one aspect of the story. There are more applications of AI/ML in playing the game of chess.

One example is a chessboard position scanner application. This application can understand the current board position by using a screen snapshot of a laptop monitor or using an image of a traditional paper chess book. This application could help people to solve chess puzzles, but it has an even deeper purpose. By taking pictures of traditional paper chess books, we could preserve the human history of chess. We could scan the chess positions from the old books and convert them to computer-readable chess position format (like FEN) and keep them in digital form for future chess players to study.

If you want to try this kind of software, our free Chess Position Scanner and Analyzer application are available on many Android app stores:

* Chess Position Scanner, Edit, and Analyze (<https://play.google.com/store/apps/details?id=com.chessboardscanner>)
* Chess Position Scanner, Edit and Analyze (No Ads) (<https://play.google.com/store/apps/details?id=com.chessboardscanner.paid>)

{{< youtube jxPEccHEIIg >}}

For more info see <https://metatransapps.com/chess-board-scanner-and-analyzer/>

If you are a Java developer and would like to have a look at the underlying source code, you could check out our open source project, which shows how it works (Windows version) look at <https://github.com/bagaturchess/ChessBoardScanner/tree/main/BoardScanner>

### Technologies

This project uses technologies from two subdomains of the Artificial Intelligence field, Computer Vision and Machine Learning. The actual Java frameworks used by this application are OpenCV and Deep Netts Professional Edition version 1.3. OpenCV is a computer vision library, which helps in extracting the chessboard from the picture and prepares the image for visual recognition.

The [Deep Netts Java-based ML software](https://www.deepnetts.com/) capabilities recognize the various chess pieces from the image which are placed on some of the board squares. Behind these frameworks, there is a lot of mathematics and algorithms, which the Java application developer can use to solve the given problem.

### Comparison with Alternatives

I had investigated other alternative ML libraries, such as DeepLearning4J. However such libraries have a significant number of native components and are quite big in file size. For example, if you incorporate DeepLearning4J in your Android app, the actual downloaded size for the production version that a user downloads will be \~300 MB for all chip-set architectures (arm, arm64, x86, x86_64).

In contrast, Deep Netts is pure Java without any native parts. Its file size is extremely small, under 1 MB. Another concern of any ML software is its performance running in an Android runtime environment. The Deep Netts framework is a bit slower than DeepLearning4J, but this is a worthwhile tradeoff with a **file size decrease of 60 times!**

### Summary

Overall, the performance of Deep Netts is acceptable running on an Android device and this is achieved **without any native code or hardware assistance** . This strongly suggests there is **very well-optimized Java code inside the framework** . Running on Windows, where the training of the convolutional neural networks (CNNs) happens, the **speed of learning/training is also very acceptable.**

I would like to mention that I have received great support from the primary author of the Deep Netts software, Zoran Sevarac, during the integration of Deep Netts into the Chess Position Scanner and Analyzer project.

If you are a Java software developer and are looking for an ML framework that is Java-centric and is compatible with the Android operating system, I suggest you definitely consider using [Deep Netts](https://www.deepnetts.com/).

It is a **lightweight Java-native library, easy to learn, and solves many technical challenges related to ML.**
