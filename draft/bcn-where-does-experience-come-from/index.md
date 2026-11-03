---
title: "Where Does The Experience Come From?"
description: "Proton’s CTO wrote a memo that nearly eulogized writing code by hand. Lars Faye points out that expertise comes from experience, and the memo doesn’t say where the next generation gets any. Here’s what used to teach it, and three ways to keep teaching it."
authors: ["jottinger"]
categories: ["AI", "Testing"]
image: "preview.jpg"
canonical: "https://bytecode.news/posts/2026/11/where-does-the-experience-come-from"
---

Bart Butler, CTO of [Proton](https://proton.me/), published an [internal memo](https://proton.me/blog/ai-manifesto) that says that writing code by hand is a skill whose time has passed, the way memorizing phone numbers did. He *does* say that engineering and expertise are not dead, but coding might be. The memo has made the rounds, and a fair amount of the reaction has been some version of the question Lars Faye asked [on LinkedIn](https://www.linkedin.com/feed/update/urn:li:activity:7523022211708878848/): if nobody writes the code, where do the next senior engineers come from?

Butler's probably right about the code: code is the part that the AI tools excel at writing, given adequate direction, but he's wrong by omission about the coders themselves.

## What the memo gets right

First off, it's pretty gutsy to publish the internal memo in full. The claim that best practices are cost-dependent is correct and underappreciated: the old rule that too many end-to-end tests are a bad tradeoff was a rule about the cost of writing and maintaining them, and if that cost drops, the tradeoff moves. 

For Java, we've managed to make individual unit tests near-zero, for decades: JUnit and TestNG are embedded in our subconscious minds, for the most part. Butler is saying that the same kind of usage will apply to the tools *writing code*, one layer up, to the point where we no longer argue over whether AI should be used to assist writing code, any more than we argue about the use of IntelliSense[^1] to help us write code.

He also says the engineer brings a lifetime of experience, judgment, and understanding that the model lacks. This is true... of the engineers he has right now.

## The omission

As Mr. Faye points out, the memo has no account of where the _next_ lifetime of experience comes from. The expertise Butler is relying on knows which questions to ask of a design, what feels wrong about it, which failure modes a given shape invites, when "it works" is not the same as "it's done." That experience has historically been acquired one way: by experiencing being wrong in code, repeatedly, with consequences you had to live with. The friction people keep mentioning is *that friction*. The keystrokes are replaceable; the scars from learning the hard way are not.

There are two ways engineers can learn what works and what doesn't.

The first is refusal to, like, run. Something says no and you have to understand why before you can proceed. On the JVM that is mostly `javac` and the test suite: the type checker won't compile your wrong idea (hopefully!), and the red bar won't let you ship it (again, "hopefully." I'm going to pretend I've never been told "the tests are fine, ship it anyway.") 

The JVM itself is famously forgiving about memory, which is exactly why Java developers lean so hard on the compiler and the tests; the platform took one refusal mechanism away and the ecosystem built two more[^2].

The second is exposition. You write down what you thought about, which means you have to have thought about it. Knuth called this literate programming in 1984, and his claim was that a programmer should [think like an essayist](https://www-cs-faculty.stanford.edu/~knuth/lp.html), with the program readable as a work of literature. Java has always been pretty explicit, always been pretty structured, and lately has gotten much nicer about this; you *should* be able to write code such that you can enjoy *reading* it, but it takes discipline.

Butler's memo takes neither refusal nor exposition into account. The agent does not refuse; a compiler refuses, a colleague pushes back, an LLM affirms (see [The Floor and the Multiplier](https://bytecode.news/posts/2026/08/the-floor-and-the-multiplier)). And the engineer is no longer writing the exposition. 

Except, interestingly, they sort of *are* writing the exposition. "Repository-level context files, shared skills and prompts" are literate programming with the reader swapped out: the reader is the LLM, instead of a programmer. The explanation is now the only thing the human writes, and it is written for the model. Knuth wanted the human to read the essay. Butler wants the machine to. Whether an engineer who only ever writes the `AGENTS.md` and never the code learns what to think about is the open question and probably the whole concern for the future of the field.

## Bandages

I don't have a good recommendation for the generalized problem. I have three practices that put a person where the questions get asked, though. Nobody can make someone learn what to think about; you can only make sure they are standing where it happens.

One possibility is to **restate the diff.** Before an agent's change merges, the human says what it does and why it is shaped that way, in their own words. If they can't, they don't understand it, and that is the red test for literacy. This keeps human review, but inverts its purpose: it catches the human's ignorance rather than the agent's mistakes[^3]. 

Next, **predict before you observe.** This is the one I'd push hardest. When a test goes red, the human says what the fix will be and what will make it go green _before_ anyone, agent included, touches the code. When you reach for JMH, write the number down before you run the benchmark, even as conjecture. When you open the GC log, say what the pause is going to look like first. 

Being wrong about a prediction is exactly the friction that used to come from being wrong in code, and it survives automation, because the agent cannot predict on your behalf without defeating the point. A prediction is a question you have asked yourself in advance, and the gap between the prediction and the result is where the taste gets built. If you're surprised by the result, great! This is you *learning*, low-stakes and high-value.

Last, **constrain on purpose.** Given unlimited time and an unlimited number of monkeys, you can solve *any* problem, but the monkeys aren't likely to understand the Hamlet they create. Give yourself a set of restrictions on things like dependencies, on memory, on number of lines, on build time, and see what happens. The platform won't force those limitations anymore, so policy has to. This is artificial, but it makes you *consider* what you're doing, and that consideration leads to judgment, and judgment is what teaches you.

These all ask a coder to commit to answers before the machine supplies one. Butler's memo does not. These aren't perfect, but they're what I've found to be a step in the right direction - I think - and steps in the right direction are better than floundering, or wondering if you even chose the right profession.


[^1]: IntelliSense here is the IDE facility for code completion; it's a general-enough term, like "ketchup" is for catsup, but it's broad enough now that even editors like vim have the ability to do code completion, probably calling it "intellisense" along the way.

[^2]: I write for the Commodore 64 for fun and nostalgia, and part of the challenge is that the 6510 has three registers and 64K of RAM to work with in the normal case, and will not run the program you didn't think carefully about. "How much memory, what kind of math, how many cycles" get asked early and often, unless you're vastly interested in the program taking *forever* to execute. The JVM asks none of those questions; that's the trade we made, and it was a good one, but it means the asking has to come from somewhere else.

[^3]: I do this for writing and review: I ask an LLM to explain to me what I wrote, and if it tells me something surprising, I wrote it poorly. Back to the drawing board I go! .. and the same applies to code. I have thrown away multiple articles and solutions thanks to this process, and while you won't thank me because you never saw them, you probably *would* thank me. If you saw them.
