---
title: "Domain-Deadline-Dog-Driven Development"
slug: "domain-deadline-dog-driven-development"
date: "2023-10-21T10:06:53+00:00"
lastmod: "2023-10-23T06:04:22+00:00"
description: "Many Something-Driven Developments are available nowadays! Which are your favorites and are you going to introduce to your company or team?"
canonical: "https://webtechie.be/post/2023-07-25-domain-deadline-dog-driven-development/"
authors:
  - "frankdelporte"
image: "dogdriven.png"
categories:
  - "Opinion"
tags:
related_posts:
  - "hard-things-computer-science"
  - "how-to-share-your-work-with-a-video-or-podcast"
  - "the-anatomy-of-a-jvm"
frozen: false
---

On Twitter - sorry, X - and Mastodon I asked the following question:
> ***"In software development, "Domain-Driven Design" (#DDD) is one of the many great (?) ways to handle a project. But who has experienced other types of DDD in real life, like "Deadline-Driven Development" or "Disaster-Driven Development", and wants to share her/his experience for a blog? Thanks!"***

This is the result of my quest...

DDD, TDD, BDD {#h2-0-ddd-tdd-bdd}
---------------------------------

Every few years, a new best practice appears to structure the development of software projects. **[Domain-Driven Design (DDD)](https://en.wikipedia.org/wiki/Domain-driven_design)** is one of them. It focuses on modeling software to match a domain according to input from that domain's experts. Inside a DDD project, you focus first to clearly understand the problem to be solved, leading to a structure in the code that represents the solution. The example project [DDDSample based on a book by Eric Evans](https://github.com/citerus/dddsample-core/tree/master/src/main/java/se/citerus/dddsample/domain/model) illustrates this by splitting the code of a shipping application into packages for cargo, handling, location, and voyage.

Another well-known abbreviation is **[Test-Driven Development (TDD)](https://en.wikipedia.org/wiki/Test-driven_development)**. The development is based on tests that get expanded, while you write the code, which happens within a continuous cycle of red/green tests (failed/succeeded). With this approach, you first define the test to be solved, then implement the minimal solution to make the test succeed, after which you can try to make it better and faster while keeping all the tests green.

**[Behavior-Driven Development (BDD)](https://en.wikipedia.org/wiki/Behavior-driven_development)** is based on TDD and agile software development processes. It encourages collaboration and conversation among developers, QA, and users to formalize a shared understanding of how the application should behave.

But these are the official terms. And we all know that many good intentions and plans get replaced with others based on "the real world." Let's take a look at a few of them...

Alternative DDD Versions {#h2-1-alternative-ddd-versions}
---------------------------------------------------------

### Deadline-Driven Development {#h3-2-deadline-driven-development}

At many projects in my career, I have seen this **Deadline-Driven Development** too many times. Because of unrealistic or moving deadlines, shortcuts get introduced in the project. Testing gets reduced or is not existing, hotfixes are deployed to production, manual steps never get automated, etc. And although a shortcut can be a good choice in a very short time, it will always backfire in the long run. And stacking shortcuts on top of each other is a "highway to hell"...

### Directive-Driven Development {#h3-3-directive-driven-development}

Related to Deadline-Driven Development, we can also encounter the closely related **Directive-Driven Development**, as shared by someone who wishes to remain anonymous:
> "I've dealt with a CTO like that before. He didn't have much knowledge about software development, but if he received a directive from upper management, he would immediately order my manager to have it completed by a certain deadline. He wouldn't consider any other factors or opinions. And if necessary, he would even suggest that the team work longer hours."

Which leads us to the idea of another blog post with the different abbreviations for the manager roles. In this case, the **Chief Troublesome Officer (CTO)**.

### Disaster-Driven Development {#h3-4-disaster-driven-development}

While Deadline-Driven Development can lead to an accumulation of bad decisions, **Disaster-Driven Development** may actually lead to improving a system. A disaster can, for instance, get detected if one of the developers, by accident of course, deletes (a part of) the production database. At that point, managers may discover insufficient protection regarding the database, hopefully leading to a better organization and more time to set up suitable testing environments...

### Dog-Driven Development {#h3-5-dog-driven-development}

I learned **Dog-Driven Development** from [Kevin Dubois](https://mastodon.social/@kevindubois/110004626789624545):
> "When you can't figure out an issue until you go take the dog for a walk."

As a fulltime remote worker, I am a big fan of this type of development! Our dog, Wifi, is also a sure way to get me from my desk at least a few times per day.

### Dastardly-Directorate Development. {#h3-6-dastardly-directorate-development}

[Chris Bensen](https://mastodon.social/@chrisbensen/110769640519496396) shared this one: **Dastardly-Directorate Development**:
> "It is when someone in the management chain (executive, VP or director) gets a bonus or personal gain for just literally f%\^£ing everything and everyone over. For example, when the CEO of Borland bought a $50,000 Italian couch with company money for his office and had a massive layoff all in the same day. Can you imagine what was on his desk and the conversation with the secretary? I was new in my career and shocked at the time. It always surprises me when people only look out for themselves and what they can get."

### Drama-Driven Development {#drama-driven-development}

Proposed by [Post Tenebras Lire](https://diaspodon.fr/@ptl/111273811525505131) on Mastodon: **Drama-Driven Development** *.*
> "The outcome is very often pretty bad."

Alternative **TDD Versions** {#h2-8-alternative-tdd-versions}
-------------------------------------------------------------

### **Tab-Driven Development** {#h3-9-tab-driven-development}

Vlad Mihalcea seems to be a big fan of **Tab-Driven Development** as he [shared on LinkedIn](https://www.linkedin.com/posts/vladmihalcea_i-use-tab-driven-development-the-more-activity-7095402263153639424-dDhC/):
> "The more difficult the task, the more browser tabs I open."

Other Variants {#h2-10-other-variants}
--------------------------------------

In alphabetic order...

### Bug-Driven Development {#h3-11-bug-driven-development}

In this [podcast by Adam Bien with Roni Dover](https://airhacks.fm/?hss_channel=tw-2599580401#episode_252log) you can hear a very nice discussion about **Bug-Driven Development (BDD)** vs. Continuous Observability.

<iframe allow="autoplay *; encrypted-media *; fullscreen *; clipboard-write" frameborder="0" height="450" style="width:100%;max-width:660px;overflow:hidden;border-radius:10px;" sandbox="allow-forms allow-popups allow-same-origin allow-scripts allow-storage-access-by-user-activation allow-top-navigation-by-user-activation" src="https://embed.podcasts.apple.com/us/podcast/airhacks-fm-podcast-with-adam-bien/id1296655154?theme=auto"></iframe>

<br />

[Alejandro Pablo Revilla added](https://twitter.com/apr/status/1683475102912921600) a very nice one to BDD:
> "It plays very well with **Customer Yelling Project Management (CYPM)**".

### Coffee-Driven Development {#h3-12-coffee-driven-development}

The type of development that I'm probably missing the most as a home-office-worker: **Coffee-Driven Development (CDD)**. Meeting people at the coffee machine and chatting about a problem and the solutions that didn't work, often leads to an alternative solution you didn't consider yet and does work!

### Competitor-Driven Development {#h3-13-competitor-driven-development}

**Competitor-Driven Development** is another **CDD**, but a bad one! If you are following what your competitors are doing, you are too late! Look at your competitors to know what NOT to do, and look for better and newer ideas...

### Meeting-Driven Development {#h3-14-meeting-driven-development}

Maybe the worst development approach: **Meeting-Driven Development (MDD)**. For many developers, including myself, certain meetings of one hour will occupy a full day as you want to be prepared and create a summary afterwards for followup.

### Meme- and Ego-Driven Development {#h3-15-meme-and-ego-driven-development}

Inspired by the actions of Elon Musk at Twitter, it seems 2023 has brought us **Meme-Driven Development (MDD)** and **Ego-Driven Development (EDD)** . It's very strange to see how a **Chief Explosion Officer (CEO)** manages to vaporize the value of a company in a matter of months... He didn't use a good **DD**, that's for sure.

BTW, if you are looking for a friendly social space, we welcome you on [Mastodon on Foojay.social](https://foojay.io/today/foojay-mastodon-service-here-it-is/)!

### Pizza-Driven Development {#h3-16-pizza-driven-development}

Since Amazon introduced the organization structure of ["two-pizza teams"](https://aws.amazon.com/executive-insights/content/amazon-two-pizza-team/), we can use **Pizza-Driven Development (PDD)**. What I don't like about PDD is the fact it assumes that the team is always available to work overtime in return for a few slices of pizza. I have been lucky in my career that I only endured such circumstances a few times, for very short times, when a deadline or untraceable bug was putting pressure on a project.

When you find yourself in a company where this occurs frequently, it's time to create on update of your **Curriculum Vitae (CV)** and look around...

### **Stick-Driven Development** {#h3-17-stick-driven-development}

I found this nice one by [Tim Zöller on Mastodon](https://freiburg.social/@javahippie/110864799216854562):
> "If psychological safety is low in your software dev team, they will default to **Stick-Driven Development (SDD)**. They will make decisions based on the probability of getting hit with a (metaphorical) stick. In most settings this will mean estimating higher efforts, avoiding risk at any cost, staying on the safe side for everything and not discussing or admitting mistakes."

ChatGPT Alternatives {#h2-18-chatgpt-alternatives}
--------------------------------------------------

I also asked my new buddy ChatGPT to come up with a few alternatives:
> ***PDD (Penguin Driven Development)**: Developers work in harmony with penguins, who provide testing feedback with their cute flapping and waddling.*
>
> *** ** * ** ***
>
> ***ADD (Alien Driven Development)**: Aliens from outer space assist with testing the software, providing unique perspectives on usability and functionality.*
>
> *** ** * ** ***
>
> ***GDD (Giraffe Driven Development)**: With the help of giraffes' towering views, developers gain a higher perspective on the code and anticipate potential issues.*
>
> *** ** * ** ***
>
> ***KDD (Knitting Driven Development)**: Developers knit together the software, ensuring it's robust and cozy enough to withstand any challenge.*
>
> *** ** * ** ***
>
> ***CDD (Corgi Delivery Development)**: Corgis deliver feedback on the software's quality by enthusiastically barking for good code and giving disapproving looks for bugs*
>
> *** ** * ** ***
>
> *Remember, these are purely fictional and fanciful alternatives meant to bring a smile to your face. In real software development, sticking to proven methodologies like TDD or other established approaches is essential for successful and reliable results.*

Thanks, ChatGPT, well done. And some of these are maybe not even a bad idea...

Conclusion {#h2-19-conclusion}
------------------------------

Many **Something-Driven Development**s are available nowadays! Which are your favorites and are you going to introduce to your company or team?

Choose wisely, but remember you have alternatives, if the chosen one doesn't seem to work out...
