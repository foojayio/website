---
title: "\"Transitioning to Java\": My First Book!"
slug: "transitioning-to-java-my-first-book"
date: "2023-05-24T08:01:58+00:00"
lastmod: "2023-05-24T08:01:59+00:00"
description: "I like to describe this book as a beginner's book for experts. Anyone who needs to get up to speed quickly using Java is my primary audience."
authors:
  - "kfogel"
image: "https://foojay.io/wp-content/uploads/2023/05/BookCover.jpg"
categories:
  - "Book Announcement"
  - "Books"
  - "Jakarta EE"
  - "Java Beginner"
  - "Java Core"
  - "JavaFX"
tags:
related_posts:
frozen: false
---

![](/images/posts/2023/05/transitioning-to-java-my-first-book/BookCover-407x510.jpg)

When I retired from the classroom in 2021, I had no intention of sitting on a rocking chair on my front porch.

I looked for speaking opportunities, became a member of the JCP EC, and continued thinking about ways to enhance Java teaching.

I also wanted to write a book. I approached a number of publishers with my idea for a beginner's book. I even wrote a sample chapter and publsihed it on my blog, <https://www.omnijava.com/2021/10/20/java-for-beginners-my-unsuccessful-book-proposal/>.

It was unsuccessful because every publisher told me that they had enough beginners books. One publisher, Apress, did give me a contract to produce a video for beginners <https://link.springer.com/video/10.1007/978-1-4842-8614-2>. But I still wanted to write a book!

This is where "Transitioning to Java" came from. In talking with the publisher, Packt, I proposed another beginner's book but for a different category of beginner.

***The beginners I hope will read my book are developers who already know another coding language. Rather than having to begin at square zero explaining what software is, what a variable is, what a method is, etc. I would explain the skills a developer new to Java needed. I submitted a formal proposal for a small book of 10 chapters and likely no more than 200 pages. In April 2022 Packt accepted the proposal, a contract was signed, and writing began.***

After about two months, I realized that there was far more to say to an experienced developer about Java than I thought. The book grew to 12 and finally 16 chapters. The page count went from 200 to just over 300 pages. I learned that writing was a lot more difficult than I expected.

I found myself agonizing over individual sentences. Writing the code samples that accompanied every chapter was also difficult because I needed to ensure that I was using the best practices. An attack of imposters syndrome came up every time a chapter went to my technical reviewers. But in the end the book was completed and is available now.

### What is in this book? {#h3-0-what-is-in-this-book}

* It begins with a look at how Java is distributed and what is included when you download the SDK. Next up was the development environment. Here I made the decision to to keep this as simple as possible. All you needed was Java, a text editor, and Maven. The version of Java is 17 although a later chapter that looked at virtuakl threads needed Java 19. When Jakarta EE came up I used Glassfish. An entire chapter is dedicated to Maven.
* Next up were language fundamentals. I am quite proud that this section of the book is "Hello World" free. Instead the first program calculates compound interest. There are 9 chapters covering the fundamentals I believe a developer new to Java should know.
* The third section of the book looks at Swing, JavaFX, and web programming. I present the same program using Swing and JavaFX. After discussing the fundamentals of a servlet I present the same application as a Jakarta Faces application.
* The last section looks at packaging and containers. Here I show how to construct a custom JRE using modulaization and then create an installer for your desktop application. I end this section by showing how to deploay a web application in a Docker container.

I like to describe this book as a beginner's book for experts. Anyone who needs to get up to speed quickly using Java is my primary audience. I also believe that it will be useful to developers who had used Java but not for some years.

I hope that you find it useful. Tell your Python, C#, C, C++, Swift, and any other language using friends and co-workers that this will be the book to read when Java crosses their plate.
