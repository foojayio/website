---
title: "Debugging the Technical Interview: Methods and Cheating"
date: "2021-12-07T09:00:30+00:00"
lastmod: "2021-12-07T09:00:32+00:00"
description: "Can you cheat in a technical interview? This approach for hiring tuned over decades is how you moneyball hiring and work with great people!"
canonical: "https://talktotheduck.dev/debugging-the-technical-interview-methods-and-cheating"
authors:
  - "shai-almog"
image: "Lightrun-Talk-to-the-duck-Debugging-Tutorial-Opt1.jpg"
categories:
  - "Interviews"
related_posts:
  - "the-debugger-checklist-part-i"
  - "the-debugger-checklist-part-ii"
  - "eight-debugging-tips-for-intellijidea-users-you-never-knew-existed"
  - "video-series-javafx-in-action-part-4"
frozen: false
---

The headline caught my attention right away "[I was shocked to catch a candidate cheating in an online interview](https://levelup.gitconnected.com/i-was-shocked-to-catch-a-candidate-cheating-in-an-online-interview-2441fef0ab4)". How do you cheat in an interview? Does [Cyrano de Bergerac](https://en.wikipedia.org/wiki/Cyrano_de_Bergerac_(play)) whisper the answer from outside the camera?

Well, close enough. The candidate was googling answers and copying them into a shared screen, pretending this was his code. Hiding this is lying. That's never OK. But this really reminded me of a tweet I read ages ago which unfortunately I can't find... It went something like this:
> My wife is learning to code and confided in me that she's "cheating". She Googles the stuff she doesn't know. Should I tell her?

This is brilliant!

We all know what the author means. We all Google coding stuff all the time. Obviously, there's a way to do this right (especially regarding IP) but if you're testing something that's Googlable then you're testing the wrong skill. Technical Interview questions should focus on things that are more relevant and not Googleable.

After reading [these](https://yieldcode.blog/how-to-be-a-great-technical-interviewer/) [articles](https://levelup.gitconnected.com/software-interviews-suck-here-is-how-to-fix-them-ea587f89db89) on the subject, I felt I needed to write something of my own that reflects my perspective on a good technical interview.

## What's the Goal in a Technical Interview

When I conduct a technical interview, I'm hiring a teammate. This teammate should be a person I want to work with. As a result, I focus on:

* Obviously, I don't want to hire a person who's unreliable. Lying is an instant disqualification
* No live coding tests
* No take home exercises
* No ranking websites or hacker challenges
* All interviews are one on one
* Have three people conduct one-on-one interviews (separately). Reach consensus
* No "make you think" puzzle questions

When I tell this to people I get the knee jerk reaction of "so what the hell do you ask" or "why not X?". I'll start with the positive first, the things I ask...

## Debugging as a Technical Interview Technique

I don't care about programming experience in a specific company. I care about programming experience, it can be in open source, university or elsewhere. If you did any real world programming, then you had bugs and you dealt with them.

That's programming. If you don't know a programming language, you can pick it up. If you don't know a library or API, you can learn it. But debugging and dealing with issues is a skill you develop by doing.

### The Hardest Technical Interview Question I Ever Got

Sun Microsystems conducted interviews by using three separate engineers. The 3rd one, [Eran Davidov](https://medium.com/@erand) asked me a question that was one of the hardest questions I ever got in a technical interview. Years later, I asked him about it and he totally forgot that...

The question was: "Tell me about a bug you made, how you tracked it and fixed it?".  

I started telling him about a bug I tracked, and he stopped me. Was it a bug you made?

Well, no. "I want to hear about something you made".

I don't get stage fright. I'm never speechless. But I was drawing blanks. I was sitting there and the only bug that came to mind was two days I spent looking for a problem because of a `<` sign pointing in the wrong direction... I can't tell him THAT!

Eventually I had to confess that I know I'm responsible for bugs... But my brain completely erased them from my mind. He still must have recommended me for the job despite my clear, over inflated ego. At least he knew about that in advance.

### Great Technical Job Interviews Questions

Based on that, here are some of the best technical interview questions to ask in no particular order:

**Tell me about the last project you're passionate about**

I want people who enjoy what they're doing. I want them to tell me what worked, what didn't work. What they can do better and what they learned. By creating a discussion around this, I need to form a metal image of the project.

I love seeing people "geek out" on a project. If they don't, it's a serious warning sign.

**Tell me about the last big bug you tracked. What was your process?**

A debugging story tells me more about a candidate than anything else. People might draw a blank here or might take a while to find something, but these are engaging stories, so they're great for an interview.

People need to describe the tools and techniques they used to track a bug. That's something you can only do with experience.

**How would you implement something like X?**

When I was interviewing at Lightrun, they asked me how I would scale a specific system for production. I could use a whiteboard but honestly this works OK verbally, too. Making architectural decisions, explaining them and then adapting based on additional considerations... That's core to what we do.

You can ask dozens of questions like this... The nice thing about this is that you can even give them to candidates in advance and they can come prepared. You still wouldn't lose much of the value.

### What about Code?

Reading code is much harder than writing it. That's why I consider coding questions completely redundant. Here, open source can come to the rescue.

One thing I did in recent interviews was this. I sent a link to an open source repository and an issue.

**Describe to me how you would debug this issue?**

Then I would guide them through the process verbally to see if they pick up my intention. E.g. I explained a situation where the user clicks something and system doesn't respond. So where would you look?

Obviously, people had to locate the part that handles event dispatching to place a breakpoint there. Following that thought process was very illuminating. Far more valuable than a bubble sort.

## Why Common Technical Interview Questions are Problematic

So it's time to get negative. Previously, I instantly discarded many technical interview techniques as being flawed. Your favorite might have been there, so it's time to explain why I don't like them.

I'll skip the "no liars" rule. I think that's pretty obvious. But here are the others.

### No live coding tests

We spend more time debugging and reading code than coding in a normal day to day. So writing code in an interview is redundant. It's stressful and doesn't show much.

Doing it on a whiteboard or a foreign computer is the worst.

### No take home exercises

This is humiliating. Getting homework from an employer shows a future employee complete disregard for their time. The people who will go through with this are:

* Desperate - I don't mean this as a slight. Getting a job in a rough market or a first job is hard and if this is what the employer demands... But even if you get the job, this leaves a bad taste
* Would probably just copy off the internet

So you get absolutely no valuable information from this and lose potentially outstanding candidates who take a job with an employer that values their time.

### No ranking websites or hacker challenges

These websites over value people like myself who have a larger social footprint. When hiring you want to "moneyball" the candidates. You want someone who doesn't look good for a casual interview but is an amazing candidate. I've run into some coders who are timid people with a small social footprint. But they are amazing coders.

I have 50k on stackoverflow, it mostly says I answered over 3000 questions. It means I spent time on that, not that I'm exceptionally talented.

I think it speaks to my work ethic and patience. Those make me a great developer advocate. But for a programming position, that doesn't really say anything about my skill as a developer. My hacker rank is also very high because I wrote a lot of code. But my experience is very domain specific and might not apply to a specific job. I'm also REALLY expensive...

Ultimately, I think those websites are a small data point you can consider. But for coding or team leading, they shouldn't be a major factor.

### All interviews are one on one

This isn't something I experienced. But I heard people (especially women) complain that they felt "ganged up on" when several interviewers "bombed them" with questions. This is difficult. We want the interviewee to feel at ease. After all, if we like them, we need them to like us back. The interview experience is the first step in hiring.

As such, we want a one-on-one process to help form a relationship. This will be valuable later when hiring.

Ideally, you want diverse interviewers to prevent bias from creeping into the process. This is tough in smaller startups before they have representation in wider demographics. Once you have that, you need to integrate it into the process.

### Have Three people conduct one-on-one interviews (separately). Reach consensus

This sort of interview breeds mistakes. I can form a positive opinion of a person because of charm, projection, etc. When using this method with a single interviewer, you will get problematic results.

The strength is in numbers and consensus. Charm might fool one person. One person might miss a problematic sign. But three people are hard to fool.

Two is problematic, if they disagree about a fact there's no one to balance this out. They might still miss something. Four is a bit too much. They might create a situation where the interviewee might feel uncomfortable.

No "make you think" puzzle questions  

People love the "why are pothole covers round" type of questions. I honestly love them too. As a game.

Do they tell me if a person can be a good coder?

Hell no.

Why not head hunt chess grand masters?

The skill set required to solve these puzzles is completely different.

So why do companies such as Google still do it?

To make the hiring process seem tough. By making getting a job at Google seem "exclusive" and hard to get, you create an aura of achievement around your company. This makes people value that achievement more than it's actually worth.

So why don't I recommend this?

Because we're not Google. Without the branding surrounding a hiring process like that, you just have another filter that will block excellent candidates. All of that for a redundant question that makes no sense. A major faceless organization can play to its made up "exclusive club" as a strength. Smaller companies need to have a more personal touch when hiring (hence one on ones).

## TL;DR

In technical interviews don't:

* Do live coding tests
* Take home exercises
* Use ranking websites or hacker challenges
* Interview as a group
* Interview by just one or two people
* Use puzzle questions

Do:

* Ask open questions
* Ask about debugging
* Ask about passion and recent projects
* Interview one on one
* Have three interviewers
* Support diversity
