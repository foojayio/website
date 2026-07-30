---
title: "7 Ways to Improve Your Code Reading Skills | Foojay Today"
slug: "7-ways-to-improve-your-code-reading-skills"
date: "2022-02-18T21:21:56+00:00"
lastmod: "2022-02-21T06:29:24+00:00"
description: "As more and more companies go Agile, being able to read other's code is more important than ever. Read on for some tips on how to improve this skill."
authors:
  - "bazlur-rahman"
image: "https://foojay.io/wp-content/uploads/2022/02/aaron-burden-6jYoil2GhVk-unsplash-scaled.jpg"
categories:
  - "Agile"
tags:
related_posts:
frozen: false
---

Reading source code is in the job description of a software developer. However, this experience is not always pleasant. Not everyone would like to read someone else's code because they find it boring, sometimes frustrating. There are cases when you start reading someone else's code but end up getting a bitter feeling because you are not being able to understand it or the code is not well written. Most developers want to focus on writing code rather reading it, not realizing that reading code is also an important skill to have.

However, reading source code has a lot of benefits. The payoffs of reading code are enormous. One can compare coding with literature. Most writers focus on reading great masterpieces while they write. The reason is you cannot just create anything out of a vacuum. You have to absorb things from different sources and work on top of that and create your own masterpiece. Writing code is not in any way different from this.

To be able to write good code, you have to read a lot of good code. This way you can learn how other developers think and how they solve a particular problem and where they lack. You can take up different ideas, styles, and patterns from reading others' code and let your knowledge grow gradually.
> If we want to put it in simple words: code reading skills improve coding skills.

There are several ways to improve reading skills. In this article, I would like to mention a few.

**1. Run the Code** --- Well, this is the first step in reading code. This might not give you a lot of details about the project, however, you will know how to build it and run it, and you will get to learn about the libraries it uses, the framework(s) it depends on, etc., which is a great way to improve your understanding of a particular project.

If you want to write your own piece of software similar to the particular project you are exploring, you might get some ideas about the possible framework or libraries you should use.

**2. Find the High-Level Logic** --- You may not jump into each detail when you start reading a project. Rather, you should focus on the high-level structure. Start from the entry point. Most projects have a main method, start from there. Besides, if this is a web application, start looking into different packages, like where the business logic is housed, where the UI code is kept, where the controllers are, etc.

Basically, skim through the whole project and gain a primary idea, and then ask yourself where you want to focus and which part you want to read first. You may not read the entire code base, rather, just a portion of it may interest you. When you find your spot, you can start by method calling and see where it leads you.

**3. Use/Know the Tools ---** There are plenty of tools out there for reading and exploring source code that can help to visualize code. For example, IntelliJIdea has the capability to navigate source code, allowing you to search by words, part of a word, or even an abbreviation. You should learn the keyboard shortcuts as well. Navigating source code with the mouse can be pretty boring and slow where working with the keyboard shortcuts can be faster. You can jump from one part of the source code to another quickly.

There is another great software to read code, called Sourcegraph, which was created by two Stanford grads, Quinn Slack and Beyang Liu, who, after spending hours hunting through the poorly documented code, decided to build a tool to help them better reading and understanding the code.

**4. Know the Language/Conventions ---** Knowing a particular language deeply helps you to improve your code reading skill. Every language has its own set of conventions, styles, and syntax. Knowledge of those helps you to be familiar with a particular piece of code, quickly. For example, in Java, method names start with a lower case letter, whereas in C# they start with an uppercase letter. Knowing this distinction helps you to identify the methods from the source code.

**5. Read the Best Practices/Design Patterns ---** The code you are reading, or a certain construct of it, may seem obscure or you may not be familiar with it, which is natural. There are plenty of good practices and design patterns that people use to do things correctly and optimally. For example, there is a pattern called Singleton where the constructor is kept private. You may ask, why on earth someone want to keep a constructor private, I have always seen it is as public, otherwise, how would I create an instance out of a class.

Well, there is a reason behind it. Singleton patterns prevent you from instantiating a class more than once, which has many practical use cases. If you know the pattern, you would not bang your head against the wall for that. So, having the knowledge of certain patterns, for example, Gang of Four has 23 documented design patterns, can help your code reading skill dramatically.

**6. Code Review ---** Software development is a very collaborative effort. No one can build a large or a significant software alone. Every software is built by a team. In a team, everyone contributes to shaping a project. At the end of the day, everyone's contributions get merged and become a good piece of work which has a real value to the customers. Besides doing the actual coding, there is another practice that every team these days does, which is, review each other's code while making observations, suggestions, and learning from one another.

This is a powerful tool for building knowledge of a code base, creating strong bonds in a team, and improving code quality that leads to fewer bugs in the system, and happy customers.

Doing code reviews, you are forced to read someone else's code in your team which ends up improving your code reading skills.

**7. Temporary Refactoring ---** Temporary refactoring also can help you improve your code reading skill. You can start taking a long method and then keep breaking the method into multiple pieces.

Keep doing that until you get a sense of what the intent behind the larger method was. After that, you can take a few notes on what the method does and then rollback the changes.

This can help your understanding of the method construct and improve your knowledge of refactoring as well.
