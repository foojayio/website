---
title: "Book Review: Why Programs Fail by Andreas Zeller"
slug: "book-review-why-programs-fail"
date: "2021-11-09T10:18:25+00:00"
lastmod: "2021-11-09T10:29:36+00:00"
description: "The book provides the theoretical infrastructure needed for the quality process. Viewed as a tutorial it’s pretty good for students!"
canonical: "https://talktotheduck.dev/why-programs-fail-a-book-review"
authors:
  - "shai-almog"
image: "https://foojay.io/wp-content/uploads/2021/11/Lightrun-Talk-to-the-duck-Book-Review-Recovered-01.jpg"
categories:
  - "Book Review"
  - "Books"
tags:
related_posts:
  - "the-debugger-checklist-part-ii"
  - "eight-debugging-tips-for-intellijidea-users-you-never-knew-existed"
  - "production-horrors-handling-disasters-public-debrief"
  - "foojay-podcast-67"
frozen: false
---

![](/images/posts/2021/11/book-review-why-programs-fail/wpf3-415x510.jpeg)

When I got my new job as developer advocate at Lightrun, one of the first things I asked for was books. Lots of books. Some of them cover my new job and others talk about debugging. I've been debugging for decades, but I feel like the theory around debugging is a bit vague. My goal was to bolster my terminology used by academics and peers. A secondary goal is to see how others teach ideas related to debugging.

The first book I received was ["Why Programs Fail -- a guide to systematic debugging" (second edition) by Andreas Zeller](https://www.whyprogramsfail.com/). I think this book is only half of the equation, the other half is Andreas's wonderful website <https://www.debuggingbook.org/>. I liked both even though I'm not exactly in the target demographic for either one.

Andreas is a professor at Saarland University and as such has written a book and website both of which are designed as course material. They are still enjoyable without taking a course. I will qualify that both are (for the most part) mostly aimed at beginners.

I used the word mostly since the book includes some more intermediate concepts and might be difficult for complete novices. My review focuses on the book since the website is a moving target and you can probably check it out faster than you can read this.

What is it About {#h2-0-what-is-it-about}
-----------------------------------------

I was mostly looking for a debugging book. This isn't a debugging book. At least not in the way I was hoping for, in fact his website is closer to what I was looking for in the first place.

The book is about preventing failures, by preventing/lowering bugs. It classifies and organizes the terms we use to define a bug e.g.:

* Defect -- that's a bug in the code
* Infection -- incorrect program state
* Failure -- observable incorrect behavior

These definitions might seem semantic and related. After all a defect will usually be the cause of an infection/failure or both. But I see his point. I see developers often fix the infection instead of the defect or fix the defect without dealing with the infection.

I'll give an example, say we have a defect in the code that causes "undefined" values to pass into the application. That's an infection. It went into the database which now has many cells with "undefined" as a value... That's a failure a user might see in the UI.

A bad fix will be to have a special case for the "undefined" string that would just hide the problem. Or stopping the undefined value before it enters the database.

We need to fix the root defect.

But that's not enough, now that we have an infection we also need to clean up the bad data.

The book doesn't stop here, it talks about reproducibility and goes deep into testing, observability, anomalies etc.

The book spends a lot of time on the process surrounding the actual debugging. Issue tracking, testing, logging and defensive coding practices.

What I Liked {#h2-1-what-i-liked}
---------------------------------

The book is filled with anecdotes and stories that I loved. A lot of them are well known (first bug, F16 etc.) but for the target audience they might be new. As the book progresses there are more personal and lesser known stories which were my personal favorites. The basic story on the Commodore 64 made me laugh out loud.

The Lufthansa Warsaw accident was a perfect example of a workaround to a design flaw. I think I might use that in future talks, very interesting.

The explanations in the book and deduction are top notch. I particularly liked the discussion on slicing program logic to find the culprit in a case of failure. It's a great way to explain such an elaborate process. The process of slicing is divided in a very methodical way. The process makes a lot of sense and I think I intuitively used a similar process when debugging but never actually followed a clear path like that.

I think the sections about process are especially important for beginners. Usually when we onboard college graduates we need to explain the tools and they eventually understand the theory behind this. This book might explain the theory beforehand so they might pick the tools more easily.

What I Didn't Like {#h2-2-what-i-didn-t-like}
---------------------------------------------

I'd like to qualify this by saying that I like the book. I think most of the problems I had relate to me. I'm not a college student and I was looking for a book about debugging. There is one objective problem about the book though: it's out of date. This isn't too bad. It highlights how everything stays the same in some regards.

Whenever the book talks about tools (bugzilla anyone?) It feels a bit like a blast from the past.

While the core concepts in terms of developers are surprisingly similar to the ones in the book... The industry has shifted a lot. SRE, devops and QA industries completely changed the way we handle regressions and track issues. Modern APMs, log processing tools, continuous observation tools etc. are a seismic shift in our industry.

TL;DR {#h2-3-tl-dr}
-------------------

If you want to learn debugging as a beginner check out this site and also <https://www.debuggingbook.org/> which seems like a great resource to learn debugging. This book provides the theoretical infrastructure you need to go through the whole quality process. Viewed under that lens as a tutorial it's pretty good. The content is somewhat out of date but that doesn't matter much if the target demographic is students.

If you want to read it to learn more about these processes I suggest bolstering it with some newer books that cover newer thinking on these subjects.

Alternatively, a 3rd edition can probably revisit a lot of the chapters and revitalize this book. Here's hoping it will happen at some point.
