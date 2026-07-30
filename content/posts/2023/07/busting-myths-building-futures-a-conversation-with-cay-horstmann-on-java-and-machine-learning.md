---
title: "Busting Myths, Building Futures: A Conversation with Cay Horstmann on Java and Machine Learning"
slug: "busting-myths-building-futures-a-conversation-with-cay-horstmann-on-java-and-machine-learning"
date: "2023-07-24T15:12:00+00:00"
lastmod: "2023-07-24T15:37:44+00:00"
description: "Cay Horstmann shares his experiences with Java, his writing process for technical books, the challenges of teaching Java, and discusses its role in education."
authors:
  - "bazlur-rahman"
image: "https://foojay.io/wp-content/uploads/2023/07/Cay-Horstmann.png"
categories:
  - "Interviews"
tags:
related_posts:
enlighterjs: true
frozen: false
---

<figure class="alignright size-medium is-resized">
 <img fetchpriority="high" decoding="async" src="/images/posts/2023/07/busting-myths-building-futures-a-conversation-with-cay-horstmann-on-java-and-machine-learning/cay-pfh-big-edited.jpg" alt="" class="wp-image-101427" width="503" height="356">
</figure>

<br />

In our ongoing series of interviews with the movers and shakers of the software world, we bring you a conversation with Cay Horstmann, a towering figure in the Java community and a respected academic and entrepreneur.

Known for his prolific contributions in the form of textbooks, online content, and courses, Cay has been instrumental in shaping the Java education landscape.

In this insightful conversation, he speaks about a range of topics, including the challenges and potential for Java in the realms of data science and machine learning, the current state of the software development industry, the impact of AI on developers, and his unique perspective on learning by practice with immediate feedback. He also shares some intriguing anecdotes from his journey in academia and the software industry.

If you're a software developer, an educator, or just interested in the evolution of technology and its implications, this conversation provides a wealth of insights and thoughtful perspectives. So, without further ado, let's dive into our conversation with Cay Horstmann.

* 📚 Books by Cay Horstmann: [Amazon](https://www.amazon.ca/s?i=stripbooks&rh=p_27%3ACay+Horstmann&s=relevancerank&text=Cay+Horstmann&ref=dp_byline_sr_book_1)
* 💼 LinkedIn: [Cay Horstmann](https://www.linkedin.com/in/cay-horstmann-659a4b/)
* 🐦 Twitter: [@cayhorstmann](https://twitter.com/cayhorstmann)
* 🌐 Homepage: [horstmann.com](https://horstmann.com/)
* 👨‍💻 Github: [cayhorstmann](https://github.com/cayhorstmann)

<br />

**Bazlur: Please tell us about your professional background and how you became involved in the Java community. What motivates you to remain committed to it?**

Cay: I am a computer science professor with several entrepreneurial excursions in the software industry. In the fall of 1995, I received a call from Gary Cornell, who told me, "*Cay, we are going to write a Java book* ." That was a surprise to me since neither of us knew much about Java beyond having seen the [HotJava](https://en.wikipedia.org/wiki/HotJava) browser run a couple of applets. But Gary had gotten us a contract on the strength of his prior books on Visual Basic and mine on C++ and OO design.

He saw the potential of a mainstream language with garbage collection and a standard library with support for GUI and network programming. There was minimal technical information available, and what little there was was not very accurate. But as a professor, I got a "research license" for the source code. (This was long before Java was released under the GPL.) We learned all about the language and standard library (about 100 classes!) over Christmas and started writing [Core Java](https://www.amazon.ca/Core-Java-I-Fundamentals-11th-Horstmann/dp/0135166306). We based the book around a collection of realistic apps, and we really needed that source code to get them working. The result was a 400-page book that sold like hotcakes.

As Java grew, I kept updating the book, rewriting each example with the newest features, adding new APIs, and removing outdated ones. Right now, I am working on the 13th edition, which will cover Java 21 in about 1600 pages. I really enjoy seeing Java grow and mature.
> *I really enjoy seeing Java grow and mature.*

Core Java is intended for professional programmers who already know how to program in some language. I also wrote a book for college students that teaches them Java as their first programming language. That book became the basis for one of the first MOOCs in 2013.

I am currently developing software for teaching beginning computer science concepts that provide interactive practice with program tracing, data structures, and algorithms. Sadly, in JavaScript, applets are no longer a viable delivery mechanism.

*** ** * ** ***

**Bazlur: Writing is indeed a rewarding endeavour. As an author, could you shed some light on your writing process? Writing technical books seems to present unique challenges and requirements. How do you navigate these complexities? How do you go about conceptualizing, planning, and eventually putting pen to paper?**   

**Cay:** I work backwards from the end product. What am I producing, and for whom?

Is the material meant for learning something new, or a concise summary of the most useful facts, or a complete reference? Often, it is more than one. For example, a section in Core Java introduces new concepts when the reader works through it for the first time, but it must also be an easily navigable reference when the reader comes back for that code snippet that he or she dimly remembers and needs right now.

And who is the reader? My college books are for beginning students with no prior programming experience. Core Java assumes programming competence in a language other than Java. The first edition of [Scala for the Impatient](https://horstmann.com/scala/) assumed basic Java knowledge, but I dialled that back in the third edition to attract Python programmers. My blog (<https://horstmann.com/unblog>) generally assumes a fairly deep knowledge of Java.

My first step is to make a list of the facts that I want to cover and a mental map of what the reader already knows. Then I try to come up with a set of tasks that the reader should be able to accomplish after having mastered the material. That is often a short program. If so, I write the program and tweak it until it contains all or most of what I want to show. I try to write something that could be realistic, not cute or contrived. I learned that from Kernighan and Ritchie. If you look at "[The C Programming Language](https://en.wikipedia.org/wiki/The_C_Programming_Language)," all the example code could have come from some actual program. No animals, no pizza, no my_function.

Next, there is the challenge of arranging the new material so that I can present it one step at a time. I want to start with something that is motivating yet simple and then gradually add complexity. In such a way that related concepts are explored together. If that gets too tedious or overwhelming, I plan another section that is clearly marked as "*ok to skip on first reading*."

I can see that describing this process is getting pretty tedious too. My suggestion to anyone who wants to hone their writing craft is to try it out yourself. Here is a good exercise. Take a JEP (<https://openjdk.org/jeps/0>) that you care about. Plan an article that explains it to your colleagues. Don't use the examples in the JEP. Try to find a different order of presentation.
> *My suggestion to anyone who wants to hone their writing craft is to try it out yourself.*

Of course, I don't put pen to paper. I don't know how anyone got any writing done before word processors. And the days where the output is paper (or a PDF that imitates paper) are numbered. It becomes increasingly feasible to integrate live code snippets, code quizzes, algorithm animations, and so on into an electronic book.

That is a fantastic opportunity for a writer. I get much more control over the learning experience, but it also makes authoring pretty complex. The key is still to work backwards. What do you want to achieve? What should the reader experience along the way? In which order can you take the reader from the base to the top?

*** ** * ** ***

**Bazlur: Let me ask you a slightly different question since you have been in academia, and major parts, I assume, include teaching students. As an experienced educator, could you share your thoughts on effective ways to teach Java, especially to newcomers? Also, how do you see Java's role in education evolving, and what steps can be taken to promote its adoption?**

**Cay:** Let's take these one at a time, and there is a lot there to unpack.

First off, here are some of Java's strengths as a teaching language.

* Java was purposefully created as a "blue collar" language: easy to read, unsurprising.
* Java has compile time type checking. That's a huge help for beginners, much better than JavaScript or Python, where a type error causes a runtime error that can be far from the cause
* Java has garbage collection. (Surprisingly, many college courses are still taught in C++ (!))
* Java has sane OO features (again, much better than JavaScript or Python)
* IDEs for Java are excellent
* There are specialized IDEs for students (BlueJ, JGrasp) that are also wonderful

These are pretty awesome strengths.

Java has gotten a bad rap for a verbose "Hello World" program with a gratuitous class and inscrutable "public static void main".

JEP 445 helps by allowing a non-static main:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">class HelloWorld {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;void main() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("Hello, World!");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
}</pre>

You don't even need a class:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">void main() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("Hello, World!");
}</pre>

Personally, I teach classes from day 1. This is called "objects early" in the ed biz. I do it for a trivial reason. That way, any students who have prior programming knowledge are just as confused as the newbies, and there is less intimidation.

But many instructors like "objects late," where you start out with the main and a few other static methods. Now they don't have to be static anymore.

So, JEP 445 is a modest win for both styles.

Let's put that in perspective, though. Having taught Java for almost 30 years, I have never met a student who complained about public static void main. I tell them to copy and paste it without worrying about what public, static, void, main, and String\[\] mean, and they do without a murmur. Learners live in a world of accidental complexity and are used to not asking about every detail in the first lesson.

When you teach programming, there is an initial hump with syntax and tools and maybe some confusion on how variables work, but input, output, and if/else come pretty naturally. Right after that, you hit a brick wall when asking students to write their own loops. It is really hard. A couple of orders of magnitude are harder than ignoring "public static void main." It really doesn't matter what programming language you use. I can go into a lot more detail with strategies for getting past that cliff, but they are not specific to Java.  
> *When you teach programming, there is an initial hump with syntax and tools and maybe some confusion on how variables work, but input, output, and if/else come pretty naturally.*

When someone kvetches that in JavaScript or Python, "Hello World" is a one-liner, and in Java, it isn't, who cares? They probably never taught an introductory course in programming, or if they did, they didn't see where their students actually struggled.

Finally, here is something where Java has lost ground. In the glorious early days of Java, the API was so much better than anything else. Writing GUIs? Check. Web programming? Check. Database access? Check. You could assign cool projects that were relevant to students. Nowadays, web apps are written in JavaScript,, and data is stored in MongoDB. even if it is very much non-humongous. Don't ask.)

Today's exciting projects are all about data science and machine learning. And that's where Java isn't the coolest choice. It is possible to ingest, process, and plot data in Java, but it is not "batteries included." There are machine learning libraries in Java, and they are bound to get even more performant with the new foreign function API, but it requires effort and dedication to turn them into an assignment. You can set up Jupyter Notebooks with a Java kernel, but it is a painful step.
> *Today's exciting projects are all about data science and machine learning. And that's where Java isn't the coolest choice.*

The Python libraries are not actually wonderful--just take Matplotlib. Please. If Oracle, Red Hat, or anyone else in the Java ecosystem wants to help make Java more popular in education, work on the cool libraries! And make it so that they can be explored on the web, just like Google Colab does with Python. This is a solvable problem.  

*** ** * ** ***

***Bazlur: You emphasized the need for more accessible libraries in Java to promote its use in data science and machine learning education, where Python currently dominates. Given that this is a significant undertaking, do you believe a collaborative open-source approach can make significant strides in this direction? And how do you envision educators and the larger Java community getting involved in this process?***   
**Cay:**That is a really good question. In my mind, there are three parts:

* - Jupyter Notebooks and Java
* - Data science (data frames, stats, plotting/visualization)
* - Machine learning

I think it would be in Oracle's interest to have a "Colab for Java." They are rumored to be working on an online version of an online Java code runner, so this would be a logical next step.

I am really unsure how a Java-based data science toolkit would emerge. There are bits and pieces of open-source solutions, but nothing that makes a coherent whole. As you say, it is a lot of work, and some entity or group would need to be motivated to get behind it. When it comes to visualization, Gluon might be interested because of JavaFX.

With machine learning, I think the big opportunity is the up-and-coming foreign functions API in Java, which makes it very natural to interface with existing ML libraries efficiently.

In Python, it also took a long time for the puzzle pieces to fall into place. I don't think there was one community that was "in charge." Each piece gradually got better. But my feeling is that Jupyter notebooks formed the common substrate.

There are actually many things that are not wonderful about Jupyter notebooks. It would be fantastic if we could leapfrog over those limitations in the Java space. The core advantage of a notebook is its social aspect. You can share a notebook with others, given the infrastructure, which is nontrivial. In the world of JavaScript, where the infrastructure is much less of a constraint, we see that sharing aspect on steroids, with JSFiddle, JSBin, and its many more advanced cousins.

So, let's cycle back to "Why Java?" Java has compile-time typing, which catches errors early. It also forces library designers to think deeply about their designs early instead of providing an endless sea of muddleheaded options. Note how I am not mentioning matplotlib here at all. There is room for well-designed libraries that take care of data frames, statistics, and plotting.

*Java has compile-time typing, which catches errors early. It also forces library designers to think deeply about their designs early instead of providing an endless sea of muddleheaded options.*

Java, unlike other open-source communities, has deep pockets behind it that ought to be interested in growing the pie. Short term, what is needed is a bunch of tutorial material that stitches together what is already there. In the medium term, I am hoping for a social infrastructure that makes it easy to share Java-based solutions. Maybe because some benevolent entity pays for server costs, or maybe because Wasm advances enough that it can happen in the browser.

I know---not a very satisfactory answer. As always, when paying for resources is involved, I brought this up with [Chad Arimura](https://www.linkedin.com/in/chadarimura/) from Oracle yesterday, and he was frustrated himself how hard it is. If you want to ask me about something else where I have a strong opinion, ask me about learning by practice with immediate feedback.  

*** ** * ** ***

**Bazlur: Let's shift gears and talk about the current state of software development. In your opinion, what are some of the biggest challenges that the software industry and its developers face today?**

**Cay:**I don't think there have been high-level changes since the "mythical man-month." Software development is hard, and the reasons for it being hard haven't changed in decades: shifting and hard-to-capture user requirements, poor team dynamics, byzantine architectures, premature optimization, fragile ecosystems, and chasing fads.

Software development is hard, and the reasons for it being hard haven't changed in decades: shifting and hard-to-capture user requirements, poor team dynamics, byzantine architectures, premature optimization, fragile ecosystems, and chasing fads.  
> *Software development is hard, and the reasons for it being hard haven't changed in decades: shifting and hard-to-capture user requirements, poor team dynamics, byzantine architectures, premature optimization, fragile ecosystems, and chasing fads.*   

*** ** * ** ***

***Bazlur: Do you have any advice on how the developer can overcome these challenges?***

**Cay:**I am not sure that "the developer," by her/himself, can effect meaningful change. But let's assume that you are in a role where your opinion is valued. There are human problems (capturing requirements, team dynamics) that are intrinsically hard and not amenable to simple advice. As for technical problems, probably the first approach should be to slow down and ask "why." I remember when so many projects embraced MongoDB. What could possibly be wrong with being "mongo"?

Which presumably indicated insane scalability. Now maybe you were working on a Netflix-like system where insane scalability was everything. Much, much, more likely you weren't. Then Mongo is a net loss. Weird query API and semantics. Eventual consistency instead of transactions. Or, on the frontend consider React. A great ecosystem if you have insane throughput requirements.

And a total pain in the rear if you serve up corporate data and await user feedback. Which many developers do for a living. Ruby and Java developers have been laughing all the way to the bank. Keep it simple and testable.
> *Keep it simple and testable.*

*** ** * ** ***

**Bazlur: I must ask this question: As AI continues to evolve and becomes more integrated into various sectors, how do you foresee developers adapting to this change? Given the widespread concern that AI may replace many job roles, what is your perspective?**

Cay: The one thing that I found was truly different and potentially a breath of fresh air was "coding with large language models". But perhaps it isn't. Reports from industry-friendly sources report a definite but modest increase in productivity (in single-digit percent) from code assistants. That's nice. But at that level not a game changer.

Economists (or at least, the podcasting variety), proposed that the current wave of generative AI gives a leg up to low-skilled employees, such as entry-level programmers. At some level, that is bound to be true. When in an unfamiliar situation, we all have a much easier time selecting and editing a solution instead of generating it from first principles. Will this lift the wages of entry-level programmers or depress the wages of experienced ones? Perhaps a bit of both. Disclaimer: I am NOT an economist.  
> *Economists (or at least, the podcasting variety), proposed that the current wave of generative AI gives a leg up to low-skilled employees, such as entry-level programmers.*   

*** ** * ** ***

**Bazlur: Would you be willing to share some of your memorable experiences from your time in academia and entrepreneurial ventures in the software industry with us? We would love to hear your stories.**

**Cay:**Let me share this completely useless story. When I was 17, my dad told his boss that he had this kid who knew something about computers. I got hired into a research lab where I had to program a computer in assembly instructions that I was expected to internalize over a weekend. Every day I went to work and was presented with a flowchart that I was to reduce to assembly.

Each such, as we would call it now, function was punched into tape and put into a plastic box matrix that was designed to hold 35 millimeter slides. Google for that because you would never have seen those unless you were my age... When the roll of tape fit, my boss was happy. But there was one function, a giant switch statement, in assembly, whose punched tape I could not reduce to fit into a 35mm slide box. To this day, I am frustrated by that failure and am a defiant switch statement hater.

**Bazlur: You've hinted at the importance of 'learning by practice with immediate feedback' as an effective method for skill acquisition. Could you please expand on that? How could this concept be implemented more effectively within coding and data science education, particularly in the context of Java programming?**   

*** ** * ** ***

Cay: In my article, I discuss the importance of '[learning by practice with immediate feedback](https://horstmann.com/unblog/2023-06-08/index.html)' in coding and data science education. I argue that traditional lecture-based learning isn't as effective as hands-on, interactive practice. I believe learning should come from concrete experiences, and watching a lecture isn't enough.

I propose interactive practice, where learners actively participate, and instant feedback, which allows immediate correction of mistakes. I also highlight the concept of scaffolding, providing structure and support to students as they learn. However, I conclude by emphasizing that while these tools are effective, they aren't a one-size-fits-all solution for teaching and learning.
> *I propose interactive practice, where learners actively participate, and instant feedback, which allows immediate correction of mistakes* .  

*** ** * ** ***

**Bazlur: Thank you so much for sharing your insights with us. We appreciate your time. Before we end, is there any parting advice or resources you would like to share with our readers, such as a list of recommended books or any other helpful information?**

**Cay:**The one piece of advice I have is for you to write about something. Do you have an audience? Be grateful if you do. But even if you don't, writing (or communicating in another way, such as a video or podcast) marvellously focuses your attention and makes you into a better writer (or podcaster or video celebrity).
> *The one piece of advice I have is for you to write about something...writing (or communicating in another way, such as a video or podcast), marvelously focuses your attention and makes you into a better writer (or podcaster or video celebrity).*

You might think, what if everyone did that? Would my contribution get lost? But for that, we now have decades of experience. Those who contribute fluff can't expect permanence, but as long as you transcend the threshold of mediocrity. Go for it!

*** ** * ** ***

**Conclusion:**   

In our insightful discussion with Cay Horstmann, we covered the potential of Java in data science, the persistent challenges in software development, and the future impact of AI in programming.

Cay also emphasized the importance of hands-on learning in education and the value of contributing to the community.

His perspectives provide valuable insights for anyone navigating the ever-evolving software industry.
