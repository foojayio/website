**[0:00]** Welcome to part two of my interviews from the AI for devs conference.

**[0:09]** OpenJDK.

**[0:11]** Welcome back. In part one of the interviews recorded on September the 19th at the first AI for devs conference in Amsterdam. We explored many AI related topics as libraries, security, infrastructure, use cases and more. In this second part, we'll dive into data science, tools for better AI development, Java in the cloud, and a behind-the-scenes look at how the conference came together. I also asked these guests the same opening question. What's your name and what brings you to this conference?

**[0:43]** My name is Alen Capel and what brings me is that I'm I've been asked to present something. So, this is all new for me. I'm not a hard developer, more a data scientist. But I wanted more to show also developers that it's not all about the code. There's much more behind them. We need to make things also usable for people. think about compliancy because I work for a bank and just to show a bit more what it takes to actually get something working.

**[1:16]** Okay. It's very good to have a data scientist here because you're at the front of what's happening with these models. So what are you doing collecting data, structuring data to be able to have a model built on top of it?

**[1:31]** It's needed that there is someone to get the data but currently not. So also data collection ingestion then when we have the data explore it, go over it cleaning it also. But a lot of it is also talking with the users, talking with the business. What does this mean? What can we get out of it? And actually making the model is easy. It's a few lines of code. But then it's also improving it, testing it with people, evaluating it. the actual model part is quite small. I think most users of AI only know the models of the big names like you have chat GPT and then cloud AI.

**[2:16]** But what I think a lot of developers don't realize is you can build your own model if you need to find a structure within invoices for instance that's something you can do. Yes. should more people know about this that you can build your own model for specific use cases?

**[2:38]** I would say yes. I think also in a big company you can't always use everything that's out there. For example, we are not allowed to use JBT just out of the box. We need to think about what is more behind it. What is the data that's in there? Is there no personal information? What's happening? for what we are doing with my team we're more looking at more simpler models also to make it understandable for a person what's comes out of it

**[3:10]** So yeah I agree you need to actually know what's happening in the back

**[3:16]** Back end to know what comes out of it

**[3:19]** So these are very specialized models for one specific task I guess

**[3:24]** Usually yes but you can make them generalizable But every context needs like its tweaks to fit to that one problem. So yeah.

**[3:35]** Okay. So the one big model that will solve all problems in the world that will be a hard one to create.

**[3:44]** You're not a Java user yourself.

**[3:45]** No, definitely not.

**[3:47]** How you're looking at this conference at the many Java tools which are presented or are you here specifically for the other talks?

**[3:54]** I'm more for the other talks. I was a bit overwhelmed with all the Java and the keynotes I must admit. I also was a bit afraid of my talk because of it. But I also think there should be a place for more the beginners to also see what does an actual model take? And usually it shows oh you can do all these cool things but people also need to think about how do we evaluate it? How make how do we make it statistically sound? and she's not right on the wave of oh lls are cool.

**[4:31]** Yeah, they are really cool but indeed you have to really consider how you use them.

**[4:35]** Yes indeed.

**[4:36]** For your talk what is the one thing that people should remember if they leave the talk?

**[4:41]** I think a model itself should be reliable and also we should take the users into account. just think about who are we doing it for? What is the value?

**[4:55]** I really like that you say because there are many developers who do development because they don't like to talk to people. They want to focus on the code. But actually it is very important to understand what comes into the model and how it's used.

**[5:10]** Yes, that I agree. I don't want to see just a number. People don't understand that. We need to see why did we get this number.

**[5:17]** Mhm. So you work in a bank. securing the data and keeping privacy is a very hot topic.

**[5:25]** Yes.

**[5:26]** Is that one of the reasons that it takes a lot of time before chatbased systems really appear on websites? You cannot trust them at this moment. I think first yeah I think more about seeing what they actually do testing them thoroughly before going widely and deploying them. I myself don't do anything with chat but there are some PC's happening and fully monitor monitoring them because if a chatbot says something today and the regulations changed yesterday and it says something wrong we can be audited. Yeah,

**[6:07]** There are repercussions for it. So, it's difficult in this domain especially.

**[6:12]** Yeah.

**[6:13]** I'm Jonathan Ellis and I came to the conference to speak about Brock and how that can help engineers do effective context engineering for coding with AI.

**[6:24]** Okay, going back come back to that.

**[6:26]** And I'm Ryan Cila. I also work for Brock. I came here to help support Jonathan, but more importantly to get to know everybody in the community and to expand really the network that I have in inside of AI.

**[6:39]** That's very important that I get to actually know the people also building tools.

**[6:44]** Okay. What is Brock,

**[6:47]** Especially on

**[6:48]** Brock is a new coding platform that it's not a VS code plugin and it's not a terminal tool. so it's its own beast and relevant to Foojay it's written in Java swing in 2025 and so we're we're taking we've made some unusual decisions here and I think that we're starting to validate those in terms of being able to do things and solve problems that other tools can't.

**[7:18]** So you're here to give a presentation. what should people what is the one thing that people should remember of the presentation

**[7:25]** That the Brock lets you see and correct what is being sent to the large language model when it's generating code. So you don't have the problem in Brock of this solution went sideways and I have no idea how to correct it. It's always there. It's always explicit. You always have control over it. That's not to say everything's manual because we have tools to automate that. But you can always override that and you can always correct it so that if something goes wrong you can fix it.

**[7:56]** So if I can summarize that one of the key things that we have found with also the Brock power ranking but even our own intuition I think is AI developers we all know this. The more useless stuff you send to the model the more stuff you send to the model that you may or may not need the worse the performance. And a lot of what Brock does is actually trim down to the bare essential what is needed to complete the task. Not only does that end up being a lot cheaper and a lot faster, that also ends up being a lot more accurate and more what we intended to do in the first place.

**[8:24]** So you're taking out what is not really needed because I know like for instance in Lang Shape 4J, I think you can enrich a question with extra content. You're actually doing the reverse. You're taking out what is not really needed. Correct.

**[8:38]** Well, so like and I won't I won't pick on names or anything, but what a lot of tools do is they'll do searches and they actually hide that from the end users. And so they'll say, for example, search the codebase, add a bunch of stuff to the context. You really don't necessarily know what they've added. In the case with Brock, not only do we do extra things like for example, we have a code intelligence. We're aware of the structure of the code of the A. And so we actually can do things like, okay, we know the structure of these files. We don't have to include all of them because they're maybe a dependent thing and they're going to help inform what the LM does, but instead of sending say a 4,000line file, we're maybe just sending the signature, which might be a 15eenth of the size. And so that again feeds back into the thing, less tokens, more accurate, less time to process. I mean, it's cheaper, faster, and better.

**[9:23]** Who else wouldn't want that, right?

**[9:24]** Okay. I mean,

**[9:25]** So is Brock a new programming language or a tool to be used

**[9:30]** Inside an application?

**[9:32]** Brock is a standalone development tool.

**[9:35]** So you would use it in conjunction with your Intelligj or with your VS code or with your Net Beans. and it's focused on that managing and supervising of the AI rather than going through line by line at human speed. How do you look at the evolutions of Java? Is Java now a better tool to do AI development than it was a few years ago?

**[10:00]** Oh man, I have so many answers here.

**[10:03]** So Ryan and I both spent a lot of time in the Apache Cassandra system where for years we were stuck on Java 6 and then you know we were able to upgrade to Java 8 and 9 and now I think Cassandra's on Java 17. So, it's still lagging a little bit, still very conservative, but Java's gotten so much better. It's so much more usable with streams and with records and all of those things.

**[10:30]** And one of the things that I love about Brock is that we can upgrade whenever we want, right? So, right now we're on Java 24, but like in a week or two, we'll probably on be on 25 because we've got our own installer. We've got our own auto upgrade stuff based on JEP. By the way, shout out to Steve building J deploy. That's a that's a great piece of work.

**[10:53]** This is a small thing, right? But I it makes me so happy to see it improving even in the small things.

**[11:00]** The new rules about constructors and calling super and it's not super often that makes my life hard, but when it does it's like this the workarounds are so awkward. So I was just really happy to see that go in. And then the other thing that's going to be really relevant for us is the improved profiler and medium-term the AOT caching but short term just making our code cleaner.

**[11:28]** Getting clean code is so important. Marit F has a whole talk about that we read more code than that we write code as a developer.

**[11:36]** Yes. So I think you have a right point there that the code now that you write now with Java 25 is so completely different than you wrote a few years ago.

**[11:47]** Oh yeah. Compared to eight, it's night and day. Like I remember I had a I had a project I maintained at my last job that was all JDK17 but originally there was a requirement to write an 8. And the 8 to 17 upgrade just going to records I had many files that I halfed in size because they were just nothing but getters and setters. and I still had some functionality on there, but because of the flexibility of records, I was able to have that as well. I had my cake and I could eat it too. It was like perfect. So, I think with the stuff that we're seeing, I'm very excited about the changes in the Java ecosystem. And before I give the mic back, I just want to say as a scripting nerd, I'm very excited to be able to write scripts in Java without having to bother with the class declaration. So, that's pretty awesome as native Java without Jbag. I mean, that's pretty cool. Yeah.

**[12:30]** And I would also add that it's been rewarding to see the community change what they consider best practices around this as well. So I remember in the early days of my Java journey that you know there was a lot of factory factory right and that kind of thing and like that never bothered me a great deal because I was working in code where I could just mostly just ignore that stuff. But still like that hurt adoption right it for a lot of people and so now you know now you've got cullibles and you've got suppliers and you can do that kind of thing in a much more low ceremony kind of fashion. So I think that's healthy like that that's align with my preferences and my intuition for what good code looks like

**[13:20]** Because like you said you spend more time reading it than writing it. So that does matter.

**[13:25]** Mhm. You have the most read blog post on Foojay. What happened there? What was it about? Can you

**[13:32]** So that post was I think the title was something like indexing all of Wikipedia on my laptop. And so u I took that J vector library that I mentioned and I loaded embeddings for a company called Coher had taken all of the English language. They had actually all of the all of the language Wikipedia articles and I couldn't quite fit all of w all of the languages but I could fit all of the English language articles which if I remember correctly was something like u on the order of 40 or 50 million vectors and so like that's not just like 40 million rows of a few bytes in your database like you're talking about 40 million you know 4 kilob vectors. So that's material, right? Like that's something that vector indexes struggle with.

**[14:22]** And so being able to do that in Java on a laptop in a relatively low memory footprint because of you know the Java SIMD support because of the optimizations we made with quantization in J Vector. that was something that was this was about a year ago. So that was pretty impressive at the time and still pretty impressive.

**[14:43]** Yeah. How will this further improve with the vector API which is being developed now in openg it's not there yet it's still experiment

**[14:50]** Yeah so we we're using the preview vector API in J vector and u yeah so that continue they haven't made any changes to the API over the last couple versions it's been stable which makes our lives easier as maintaining this but like before that we had separate versions for 19 then 20 it didn't change 21 it change. So we had to have, you know, the 19 version and the 21 version. so stability is good. Like I like stability.

**[15:19]** Yeah. As they say, your runtime should be very boring.

**[15:24]** It should be stable day to day.

**[15:26]** You don't don't have any surprises.

**[15:28]** I like that philosophy. Yeah. And Java's been better than average at that I would say for sure.

**[15:33]** Oh, quite a bit. Yeah.

**[15:35]** Well, yeah. I just thinking of projects that were painful. Python 2 to 3 was really painful. We've never had that equivalent in the Java ecosystem.

**[15:44]** I mean there was some pain between I think 8 and 11

**[15:48]** And but after that settled down it's been rock solid.

**[15:52]** Did they say I don't know if you have had projects like that moving from 8 to 11 was the big step

**[15:58]** But once you were on 11

**[15:59]** 11 was trivial. No I did it several times. It was fantastic.

**[16:03]** And I really appreciate that stability. it I used Go quite a bit in past life and that's one of the things I loved about Go was it was just you could that same code I wrote forever. Go would continue to compile and continue to work.

**[16:16]** So it's good to see Java return to that sort of rock solid stability. It's it for me as a developer makes me much more productive.

**[16:24]** U my name is David Perry and what brings me here Jonathan brought me here. He asked me to come. I've spoken at a couple Java mugs and a couple conferences before and I'm always open to travel and go wherever there's great Java developers cuz I've loved Java since it first came out and been doing it.

**[16:44]** I've been Yeah, a couple years ago. So, yeah, I'm been professionally writing code for 30 years

**[16:50]** And I'm here now with COD

**[16:53]** And COD we are an AI developer tools. So I get to go hands-on with developers and help them bring in AI as not just some vibe coding but actually as a tool that's going to actually help you produce quality. So hence the Q and quota is for quality and that's my whole big deal. we were first the first thing that brought me as a customer of Kodto when we were Kodm Kod Kodium and we changed our name is all the unit tests it could generate and that just blew my mind because as we a lot of developers they're not you know doing unit tests

**[17:34]** Don't like it

**[17:35]** And don't like it and do that and so now it's like there's no excuse not to have the unit test written for you and review it a little bit or look at it and now with the agents and doing that. So that was my forte into Codto getting there first doing some developer adv advocacy but now I'm back to principal architect where that we're now going into customers and helping take away some of the mundane things for a developer. So, think of a bug report or a bug coming in from pager duty or something like that. So, instead of getting it and having to do all this raw analysis and finally coming down to it, if you could have something that could come and say

**[18:23]** Here, I've done all this work for you now and here's maybe here's the repo where it's at or whatever. So, think of the first initial RCA or root cause analysis is done for you and then you get it. one of our other cool products we have is Codto Merge and it is a code review. So that's how it started. It's been we've been doing it for 3 years now with it. It was comes from our open source project PR agent that is like a lower ver not lower but doesn't do as much functionality. And that tool is really cool because it sits there and it's the first initial code reviewer. So we've been at this idea of an agent and working there for you and on behalf of you and help you

**[19:09]** Enhance your quality and the mundane things before all this vibe coding. So a lot of times to good or bad and maybe marketing I'm not a marketing person I'm an engineer but trait so you know maybe it's not a big deal or it wasn't a big thing that we got our name out there like maybe all the you know GitHub or other ones the tools to do all this vibe coding or cursor but we're the one that we're getting in it for the long term we're in it for the quality

**[19:38]** And to really not just produce a bunch of crap code and then you have to try to figure it out if that makes sense. Because that's the danger of how easy these tools generate code, but then you still have to understand what they have done, improve it, fit it correctly into your own existing code.

**[20:00]** Is that where a lot of developers are struggling with finding the right way of using these tools?

**[20:06]** Yes. you hit on a great point and that's the whole idea. So we're that last you know that traffic cop that reviewer there

**[20:18]** Because there's so much now code being generated

**[20:22]** And it's like how do you even get through this? So our tool helps for improvements you can actually put like things and saying you know compliance kind of really set a rules to stop.

**[20:33]** So think of someone's physically going to look through that. Now, we work with like Sneak and other tools too to integrate into our tool to help do better on reporting. But again, one of the things that I like with our tool and where we're not like anthropic where it's kind of like, oh, well, are they going to actually have other models?

**[20:53]** But one of the unique techniques you can do, especially as like a junior developer, is turn around and go in and say, "Okay, use this model to generate some code for me. Now I can pick another model and say, "Hey, can you review this code for me? What does this really look good?" And if you're new now, you can start having it help teach you about these things. Also to stay small.

**[21:18]** And you can do small units of work and then review them too. So one of the things like with an agent, you can say, "I'll have an agent is create a whole website for you." Well, it's probably not going to get all the way better or it's going to get really

**[21:32]** Kind of go off there. But if you we h I have one and we have it on our website or on GitHub. You can pull down the definitions for agents. I have one that's for TDD.

**[21:42]** And what the agent does is just takes in a small requirement for a function or a method and then we'll produce the TDD all around it. and you'd be I mean for me I thought I was a good developer and doing all this and I'm like wow I never thought of that test and that one and that one

**[22:02]** And I probably would have not written it either

**[22:04]** But it made sense for it

**[22:06]** Happened yet. Yeah.

**[22:07]** And you and you're like oh you might have a tool well and only you need is 75% code coverage I'm good to go. Well who cares about the code coverage? You want to know good

**[22:18]** Coverage of your code. So, and you know, you look at him like, "Ah, yeah, I didn't." Oh, wow. Oh, yeah. Yeah. And so, you're going through your head like, "Wow, that really makes sense." Or, "Oh, this one doesn't make sense."

**[22:30]** I also see, you know, what's you what's been very neat for me to see in companies. Now, I'm not going to name companies cuz we've all been in every company. All companies are this way, but oh, we have an old code base. We just want to do unit tests on it because we're supposed to have it and that makes quality. And then they said, "Well, your tool doesn't work cuz we can't unit it won't unit test." And we can break it down smaller, but when you really look at the essence of it, the code is not testable cuz it's

**[23:00]** That's the problem with a lot of old codes. Yeah.

**[23:02]** Yeah. And it's like, don't blame any tool or anyone else. You put a human on there, they're not going to be able to do it. And it's a bad smell and it's really bad. And so I've always gone with the fact of you can have code that's not testable and I'm probably can say around 99.9% of the time that code's going to be really bad and it has no test. You can already smell it then it's bad.

**[23:25]** If you have code that has tests there's a highly likelihood that it's probably good design because it's written to do it. Now, unfortunately, there's some of these mocks and other things like, "Hey, we'll mock the whole universe and make your life."

**[23:39]** I don't agree with that because now your code is not good and designed well.

**[23:44]** But I mean, I'm not saying anything new that AI is going to help you in doing it,

**[23:49]** But AI will, you know, you need to make sure it's not this magic bullet. You're still in the driver's seat. You still need to have it go and do those things for you. and you have to review them and you have to look at it and say, don't blame the AI and say, "Oh, my code is not testing it or it's always failing." Well, yeah, it's bad. Or you might test it and you're like, "Well, now I ran it again and it's failed." Oh, well, because of your design, there was one that's like writing to a log file in sequential or keep on testing multiple tests. there was a contention on a file

**[24:23]** And so the tests were saying look

**[24:27]** In production you're probably going to have this if you hit that edge case of the contention of that

**[24:32]** So I always you know I go with that fact I think it's really good though

**[24:38]** Once you get all that it'll mortalize all your bugs in your code already but if you're in production

**[24:44]** You don't want to touch it yet because maybe that edge case or anything is not captured

**[24:49]** But now you and start refactoring with confidence. So, and again, I'm not

**[24:53]** I'm not Martin Fowler or anything, but I've read his book and been doing it. And that's why I love it, too, because it, you know, everyone says, "Oh, yeah, you say get everything tested and ready to go for refactoring, but who has the time to do that

**[25:06]** To do that?" what I also see with all these coding tools is, yeah, you had this company who were firing their developers. They didn't need them anymore, and a lot of them are already reverting those IDs. Is it we already mentioned this before with Brian Vermier is developers won't have less work they will probably have other work in current systems when autogenerated code needs to be reviewed and integrated and tested so actually the kind of work is shifting for developers if they are using these tools correct

**[25:41]** Yes so I always say this now and I maybe you call it drinking the Kool-Aid you know American term or something like that. But if you don't learn your place and where how to do work as a new developer in the AI world, you're going to be left behind eventually.

**[26:01]** If you do adopt it and know where your place is and embrace it so that you can be here's where I'm going to be effective now. And you're going to have to be that salesman of yourself too and say here's this new space here. This is how I'm contributing. This is what I found. this is my skill now using these tools then yeah you have a place

**[26:23]** And what I don't have an answer for and everyone that sees this podcast please write in and comment and thing cuz I always get this the junior developer when you come out of college doesn't make you a professional developer and everything else like that so now how are you going to learn that skill as a journeyman for the 15 or 10 years or whatever that you learn when you come out of college and learn how to do real reviewing of code and what it means because you're getting your own code reviewed. Where are you going to build that skill up to do that? So AI is going to be that junior developer writing some of that code and everything. That's my whole thing is eventually is going to do that. So who's going to know how to review the code? Yeah.

**[27:06]** So, I think that's a skill set that we're going to have to start teaching maybe in colleges or a different way of like not writing the code more is okay here's all this code

**[27:19]** You understand it and read and there's a great book and I can't remember it has black glasses set on a table in front of it that's code reading and I've read that book a long time ago and there and someone else wrote a book about code reading and I got it I was given to me a long time ago And it's really beneficial for anyone to read that code reading because it's more and more important now. and it goes over doing FreeBSD from Berkeley and it uses all the FreeBSD source code and working through that because it's very readable code. It's from Berkeley. It's a lot slower, but the real key to it is it's done by in the college and in for learning. And so if you read that you can really it's readable and I that is when you become a really big professional is when you can write code that's readable

**[28:11]** And that's understandable. Anyone can generate code like the vibe coding AI and

**[28:16]** And you know be taken. So that would be my thing is I don't see developers going away anytime soon until we get it to where AI is you just talk to it and your programs written and you talk to it again your program's written there's no program there's no language it's just running in CPU compute somewhere

**[28:38]** You know 2000 or whatever so I don't think I'll be around then so

**[28:43]** And until then we need good developers

**[28:45]** Yes

**[28:46]** My name is Aleandro What's your interest in AI, Java?

**[28:50]** AI for sure. So, I run the Dutch cloud native meetup. so all about Kubernetes, cloud native technologies. Of course, this is fundamental to AI. Now, as you know, many many models are trained on large Kubernetes clusters. so the world of cloud native and AI intersects. there's a lot of working groups within the crowd complete foundation community that works on AI and conformance AI and infrastructure and I'm particularly interested in sustainability so in fact we coming next year with a conference about a AI and sustainability how to make how to understand how AI impacts our consumption of course fossil fuels renewables and water

**[29:39]** We just had a very interesting post published on Foojay from Miro Wangner about he has created a tool to measure the energy consumption of different Java versions Python

**[29:52]** And there you see that Java is actually a very solid choice for heavy applications because it uses a lot less energy

**[29:59]** So forget the energy it's actually the cost because these are related so do you see that Java is a very good tool to be used in iCloud.

**[30:11]** It is actually the modern Java the if you use it properly and if you measure and you know how to mitigate and don't waste energy. Yeah, Java is perfectly good fit for a good green compute. I think they there was this there's this table out there listing programming languages, right? and their efficiency and of course Python is the worst and the Rust is the best and Java is there in the middle and I think

**[30:42]** Oh it's it's high

**[30:44]** It's high but I don't think that's it's a simplification of what you need to use you know there's also more considerations around which language you should use and Jav so we should invest in making Java as green as we can because it's so widely adopted and so we need to intervene where there is the most code and I think there's a ING made a lot of contribution to understanding how Java code can be made greener. the CNCF as well. So all this code of course runs on cloud native technology most of the time. So we need to work across the spectrum from top to bottom from the code to the server to the data center to make it really sustainable. Yeah, I have here a leaflet. The cloud native community days of Amsterdam. Is that something you organized?

**[31:36]** Yes. Yes, the we organized. So, it's a community that exists for 9 years now in Amsterdam in the Netherlands. So, it used to be the Kubernetes meetup of course evolving cloud native meetup. We have now 4,100 members. So, it's pretty good, pretty big. We organize meetups every month across the Netherlands from Indovven to Amsterdam to we hope to go to Groingen and other other regions and we did Kubernetes community days in Amsterdam 2018 19 2023 and so we're coming back in 2026 and we decided to really adopt this theme of AI and sustainability because that's

**[32:17]** For me for us there is no no more important topic than this one.

**[32:22]** If we don't have a planet,

**[32:24]** There's no there's no future. So this is it. We need to talk about the impact of our job. You know, we work every day with this technology

**[32:33]** And we need to understand what is the impact on our future.

**[32:37]** It's not only us is our I have kids but if you don't I think you should care about the future.

**[32:43]** That's a big shift. So you say you already exist for a long time. So you've

**[32:47]** It in the past it was just try it out deploy whatever you want we'll see what it gives and now it's it shifted towards the cost and it shifted even further too and it has to be ecological too.

**[33:00]** Exactly.

**[33:01]** But I think those two last things are very related.

**[33:04]** Yes. So if it's good for your wallet should be good for you for the planet as well. And that's so is this the idea in DevOps to make the right choice also the easy one. So if we make things cheap and green then that is a no-brainer right so everybody would adopt that approach that technology and that's that's what we want to do it's every days actually no it's not every days is we getting actually to a maturity of models of impact softer carbon intensity I'm a green software foundation champion green software foundation gsf it's it's also very active there are many communities in there as well. So there's also a meetup also in Endovven in October. so lots of people are talking about it really and I'm glad to see also the government and the institutions.

**[33:57]** University of Amsterdam is a whole PhD program dedicated to green software. So there is a real movement there and I think we just want to be part of it. We want to bring together academia, industry, government, institutions and the people so we can really make a change.

**[34:14]** Yeah. So people who want to developers who want to make a change and build something more green they can sell it to their manager as it's cheaper.

**[34:22]** Yes the manager should already know right. So but yes every developer has the tools to first of all understand like a measure what is your impact how much CO2 carbon intensity

**[34:36]** And then make a change right so try to optimize not only for performance but also for cost and for impact it's we got the tools they are maturing very fast so it's just a matter of choosing you know and using it and applying it yeah okay thanks a lot I will add a link to the conference in the show called Native Amsterdam. It's it's where we're going to be next year in May. So I hope to see you there.

**[35:01]** Okay. Thanks a lot. My name is Susant Susant Shakhar and I am a member of I'm from Jagu that's how I came to know that we have a conference already always and for a long time been interested in AI

**[35:16]** And I just yesterday had another session with AWS and this was a great continuation basically exploring bit outside of AWS. So yeah

**[35:27]** And you are a Java developer. Yes, I I've worked like 17 years in Java but in between right now mostly into Python, Go and a bit of Java. Yes.

**[35:38]** Mhm. How do you use AI in your job? Is it a tool to help you to code? Is it something you use inside your application?

**[35:46]** So we are rigorously using GitHub copilot.

**[35:48]** Mhm.

**[35:49]** We have recently started with canvas. and we also are part of say building some applications or say tools for the organization. recently we're building recommendation engine for our organization.

**[36:07]** Yeah.

**[36:08]** And recommendation engine is using some kind of model behind this.

**[36:11]** Yes. So mostly open source model.

**[36:15]** Are they correct enough for your use case or do you consider to build your own models? Now we take the models but we train them on our data then we tweak it based on our requirements and then we use what is the output of what we have done with it. So

**[36:32]** Is it something people should try out building your own model or tweaking the existing model because it gives better results. So I would say tweaking definitely building I'm not sure it's very viable or feasible for everyone to do it

**[36:49]** And what I've seen is there are already I think there are 100,000 models already I have seen so

**[36:57]** And each day there have five models

**[36:59]** Yeah so it's not contributing anything is just adding to the clutter

**[37:06]** So yeah what everyone wants is to start using AI. Building a model is not using AI but using a model would definitely get you the results what you wanted out of the AI and ML

**[37:20]** I would say landscape or paradigm or

**[37:23]** Realm

**[37:25]** So you're doing Java development already for a long time.

**[37:28]** Yes.

**[37:30]** How do you like or dislike what has been happening recently with all these new versions and changes in the language and in the runtime? Do they help you as a developer?

**[37:40]** At the point of Java 8, I was very excited from because I started like Java 4, Java 4, 5, 6, 7,

**[37:46]** It was like okay, but Java 8 was groundbreaking.

**[37:51]** Then Java 11, Java 12, there were changes. But then it got stagnant. So most of us we were started trying out other

**[38:03]** Things like we went into Scotland. it provided much better tools. Java 17 came, there was few improvements but not that much.

**[38:15]** Go came in that point in time. So, we went Jed into Go, we started using Go then. But then I would say after Java 21 I would say yeah mostly then records and everything was mostly available and it changed then virtual thread came in and it mostly basically that was groundbreaking I would say and now everything which you are doing with the SDKs and everything for AI.

**[38:43]** Mhm.

**[38:44]** It's kind of like bringing back into Java.

**[38:47]** Yeah. So you're shifting from different languages back to Java.

**[38:50]** Yeah.

**[38:50]** Yeah. which is a nice evolution

**[38:52]** Or I would put it like that like back in the days I was kind of forcing Java

**[38:59]** Because it was used everywhere and it's more reliable I would say in that sense but then I moved out now again back into the saddles where I'm again pushing for Java okay we can solve it with Java

**[39:09]** My name is Ara and what brings me to the conference well actually that's the story of someone you will know very well u but also the organizer of the Amsterdam Java user group. we met on a meetup. he always is looking for rooms to actually host this event and we hosted him once upon a time and we actually looked at the venue and we were like there's so much more possible here and he introduced us to Jonathan Jonathan Villa and he had the idea of doing this AI conference in Amsterdam and we had the opportunity and we just made it work

**[39:50]** And here we are.

**[39:51]** Here we are today. Yes.

**[39:52]** Okay.

**[39:55]** What do you do with AI?

**[39:57]** What do I do with AI? Well, I used to work here at IO Digital and at IO Digital, we are, I think, front runners in using AI in our day-to-day job. all the research projects I did, I used a lot of AI. I had a lot of team members that I stimulated to actually, we have this concept of Google days where we just dive into new stuff. One of my developers actually dove into the performance of the different plugins versus the different models you have and how they perform code-wise. so yeah, I always like that and I always encourage people to just dive into that and try it out. You have these two tracks. So you have AI which helps developers with tools and you have AI in applications. How do you look at both sides?

**[40:48]** So there are two different things. I think one is helping you be a better coder maybe even or a faster coder or actually maybe not just faster. I have a hard word hard time putting it into words. I think if you go into the flow of using L&M during coding you can be so much more productive and really be in the sort of a high over review kind of way. It's like you're pair programming and you're in the driver's seat and you tell the LM to do the work which makes you go faster in the end which I really find cool. I just witnessed the workshop with Juni with Anton and it was great to see how you can actually make the L&M specify your requirements and your guide your guidelines and actually code way cooler than we could before.

**[41:43]** Mhm. So we had we talked about this before is there's also a danger of pulling in too much code that you didn't review. So you should actually use it as a tool to help you. Correct.

**[41:54]** Exactly. Yes. Definitely true.

**[41:57]** And then the other side which is using AI in applications. We talked about it. Yeah. You should not use it just in production because it's a bit dangerous. You can use it for very specific tasks. Do you see it used a lot already in production? So actually we did a proof of concept with Scripple. my last coding gig was at Scripple and I think in November 24 we actually did a proof of concept. Spring AI wasn't ready back then. So we did it ourselves. We built a back end and we had a mobile app that actually took a picture, send it to the back end and the back end would forward it to an L&M saying like, "Oh, could you classify the type of problem you see on this picture?" we were always registering defects or things that happened in the airport and classify it and also categorize it and reply in this JSON format.

**[42:55]** And we actually had that working. the project got killed due to politics and stuff, but we had a proof of concept running which was

**[43:04]** Kind of awesome to see it. It worked really well. We did a two weeks test with it and it was able to classify a lot of pictures the right way whereas as they are normally reported by somebody working for Skipool who doesn't like to type in too much details and you know they categorize half the things they did they just pass. so the data was really well classified and I think in those kind of areas it could have a big benefit

**[43:31]** And that was one year ago you say?

**[43:33]** Yes.

**[43:33]** So now you have springi language 4j all these extra tools better models

**[43:38]** Definitely.

**[43:39]** Were you using a specific model you trained on previous self-deployed jg version back then? so I think now if you would bring this kind of product to production you could use a lot more tools. I would even say that you would have to run a llama with a private model and

**[44:01]** Really put some guy guide on it.

**[44:05]** Yes.

**[44:05]** Are you a Java developer yourself?

**[44:08]** I'm I still classify myself as a Java I am an engineering manager but I still love code.

**[44:13]** You love code. How do you look at how Java has evolved? So I'm I'm not maybe I'm not the one to ask this question because I am very much on the cotlin track.

**[44:23]** Okay. And how why cotlin is it better?

**[44:27]** I don't like to talk in ways of better or worse. I just like a lot of constructs in cotlin. I like the fact that they went for safety first so to say the nullability but also closed classes by default. Everything that is less safe you have to specify it. You have to tell it I'm I'm I'm aware what I'm doing here. I know this is less safe, but I'm I'm I'm using it because reasons

**[44:53]** Instead of the other way around. And that's why I love Cotlin.

**[44:56]** You have Java, you have Cotlin, you have Scala, they all run on the GVM. And that's actually where I think is the whole power of this ecosystem of different languages.

**[45:05]** I fully agree. I think Java has come a long way as well. I think due to cotlin maybe even Java has picked up their pace and you've seen so much nice change changes there as well.

**[45:18]** One of my old team members was a big Scala fanboy. he actually did a talk on the Jall about how much the languages actually got together and actually excelled each other.

**[45:33]** So I think we're in a very nice time where everything is just improving.

**[45:37]** Yeah. My name is Yostast Khan and organizing this conference brought me to this conference

**[45:45]** And the fun thing is we talked about this in December last year. It was broadcasted in the podcast

**[45:52]** Where you were announcing this conference.

**[45:54]** Yeah.

**[45:54]** So how did things go between then and now?

**[45:57]** Things escalated.

**[46:00]** So the idea was indeed roughly around JFO last year that we were thinking well we were seeing right. So I'm I'm a business developer and one of the things that my job is really important is picking up the signals out of the market and not necessarily immediately having to do something with it but signaling and I noticed especially in our team but also in other teams there was a really big question for learning opportunities that were really really focused on developers. why? Because I think developers and I'm I'm a bit like this as well. We get a little bit allergic when it's only about buzzwords.

**[46:44]** And I get it. AI has a lot of buzzwords around it, you know, and there's people talking about it that don't know what it is. but yeah so to keep that kind of what we called fluff in the preparation towards this conference keep all the fluff out of it just the code no fluff just code and that's really what we wanted to do today and I think so far we're we're having a great day.

**[47:11]** Yeah there's a lot of focus on Java.

**[47:13]** Mhm.

**[47:14]** Is that also a real choice you've made

**[47:18]** So partially. So the founding team of this conference came from the Java world for sure. You know Arnold Kuler was our Java chapter lead here. we had of course from the NLJ and Amsterdam Java user group. We have Jonathan Villa from the Barcelona Java user group. So it's no coincidence right that network that we are all connected in found their way to this conference easier. At the same time, our goal was to have three tracks, Java, Python, and JavaScript. And especially the third one, we found it very difficult to fill with the right speakers. And we only really got in touch with a few. Now, that's partially a network challenge also, of course, but it's also partially I think that the AI specifically front-end development is in a very different phase than in the backend development. and the data of course the Python and that's a challenge we want to pick up for next time and so it's you could see this as a shout out to all JavaScript viewers that have good ideas for speakers who are really on the cutting edge of AI powered development you know get in touch cuz we'd love to meet you and we'd

**[48:32]** Love to talk about AI for devs too which is definitely coming

**[48:37]** Or is it because Java and Python are the main drivers of AI evolution on the back end side.

**[48:45]** Yeah,

**[48:46]** Maybe.

**[48:46]** Of course.

**[48:47]** Yeah.

**[48:47]** Yeah, there's a lot of interesting things you can do on the front end also. You know, automized unit testing, you name it. There's going to be a talk by one of our colleagues, Michelle Anetta. There's going to be talk about automized testing in front end.

**[49:00]** But it's yeah less less matured as on the Java and the Python side of course. Yeah.

**[49:08]** Actually, we had this idea of a conference. Yeah.

**[49:10]** It's always nice to have ideas. I have a lot of them. How did you actually realize it? Because I can imagine the cost and all these things. How did this all get solved?

**[49:20]** Yeah, absolutely. So, it started I think with a very nice foundation where there was from the organization IO a belief in that we needed to do this

**[49:33]** And the willingness to put ours into it, you know, to invest into it. at the same time, there was the realization we weren't going to be able to do it alone. We don't have enough speakers by ourselves and we wanted to connect the whole community anyway.

**[49:46]** So the first thing we did was we tried to check our idea to the market. We put it to the test. We spoke to people within our network outside of our network said hey this is our idea. What do you think of it? And then after that question of course the question do you want to play a role? And we were actually stunned by the amount of people that said

**[50:07]** Yes we want to play a role. Based on that enthusiasm, we realized we had a good idea and from there we you know especially with ING went really well worked together with Samantha Bulantini really nice collaboration. Content stack joined in fairly quickly which is a valued partner of IO. of course Azul Systems who are our top sponsor. They were really enthusiastic to join in and we're really happy Platik came all the way from the states to come talk and what a keynote and yeah and Cotto joined in actually just like two weeks ago.

**[50:45]** I'm Kundal and I'm a front end developer advocate.

**[50:49]** Hi Samantha Buratini. I'm a customer journey expert.

**[50:52]** Hello I'm a senior S Luis San Martin.

**[50:55]** Welcome you at the conference. what brings you here? are you interested in AI in the Java side?

**[51:02]** Actually, we're sponsoring one of the sponsor of this event. they approached us and we believe in give opportunity but also help young companies and with fantastic ideas behind and we are want to be there to be to see to meet interesting people and listen to what's going on the AI world. How is AI already used within the company or what are you thinking about integrating how you will integrate AI?

**[51:36]** Yeah, so we have multiple initiatives going on in different areas. So one of them is really interesting of course is engineering. and there we have different types of initiatives. but one big one of course is with GitHub copilot. so where we are yeah enabling engineers in their code editor and we are yeah really working on getting people up to speed with that and yeah implementing all the best practices in the tooling and so on around that.

**[52:14]** You have two very different tracks in EI. So you have indeed helping developers to be more more productive or make more mistakes. That's discussion which is ongoing. do you also use it towards your customers to have give them extra tools on the website or your customer support?

**[52:32]** Yeah. So customers is of course a bit more sensitive because yeah without sort of a man in the middle it's indeed as you say you can make mistakes. and on the engineering side you always have that validation from the engineer itself. so it is more controlled but there are experiments going on because we do think it's valuable also for the customer. So where we can we really do focus on the yeah the initiatives and we want to support those as well indeed.

**[53:03]** You mentioned you're a bit of a Java developer. Are you interested in Java? Yeah, because it's one of the main languages or

**[53:12]** Many languages within ING or

**[53:13]** Yeah.

**[53:14]** Yes. I think that's one of the reason why it's interesting this conference for us because the one of the talk is it was about yeah and also how it can be used. I think it was Apache iceberg. So in this case it's one of the areas we are exploring.

**[53:32]** Mhm. So you have large amount of data and you want to somehow explore if it is worth trying AI or later.

**[53:41]** Are you also here hoping to find new colleagues?

**[53:45]** Is that one of the goals of sponsoring an event like this?

**[53:48]** Well, of course the door is always open. so we love to attract new brilliant brain. So why not?

**[53:57]** I mean the world is free. You can go anywhere. Of course we have we offer fantastic packages and our team everyone has got possibility to learn to grow quite fast. So for us it's really interesting

**[54:13]** To be here

**[54:14]** To be here indeed.

**[54:15]** You as a U developer how did AI change your job? mostly I guess in the sense of the potential initiatives that we're able to start up. So with coming of AI like everyone's of course thinking like oh wait this really great we could do this or we could do that. So they're starting up some initiatives inside of the company. I think that's the biggest thing so far and then yeah with the coming of get co-pilot in IDE as long we going to start use that then I think that will grow even more.

**[54:51]** My name is Na and my dad's the organizer. So I came with him because I wanted to visit Amsterdam.

**[55:01]** Mhm. He's behind the camera so we cannot make him responsible. what's your interest in programming? You were interested in Amsterdam, of course, but what is your interest in programming?

**[55:11]** Well, actually, when I was little, I like to program. I used to create apps, mobile apps. and I created a lot of apps. I created an app to book the sofa in my house

**[55:32]** Because your father was occupying it.

**[55:34]** Yes. But after that this was when we were at home with the co

**[55:43]** And after that well I went to school and I don't program a lot now

**[55:48]** We have this whole shift of chat GPT and

**[55:53]** How are you using those tools? Okay. So, I use a lot TGBT. first I use it when I want to buy something to be informed to have a lot of options and also I use it to learn because in school sometimes the teachers they don't teach very well. So I go to Ted DBD and ask him to explain it better to me.

**[56:26]** Do you believe everything what comes out of this tool?

**[56:29]** Not everything because I've used TABT for math for example and it tends to give some bad results. So I don't believe that much everything it says to me. And also in school essays that we have to look for information. Our teachers they say to us that we have to check our information.

**[57:02]** So it isn't always reliable.

**[57:06]** I'm I'm very happy that you realize it because we as coders see the same problem. it comes up with an answer which is not always the most correct one. Do you still do any coding nowadays?

**[57:18]** Actually this conference inspired me to create another app or I don't know maybe code a bit.

**[57:26]** Very happy that you as the youngest person here in the room I think in the conference are getting inspired by all these talks. Thanks a lot.

**[57:33]** I'm Jonathan. I'm coming from Barcelona. and what that brings me here well I'm one of the founders of AI fors it was an idea well that I' had time ago but one day heron he pushed you convinced me yeah and also said yeah but I have a company that they want to do this in the Netherlands and everything is fine and easy said uh-huh okay So, yeah, we are here. I'm so happy. I would say I'm a bit tired, but I'm so happy because the experience has been amazing for me as an organizer and also from the feedback from attendees. We we're doing this interview between the last session and the network drink.

**[58:28]** Yeah.

**[58:29]** So, I can imagine that you are exhausted. The day is was it a success? Did everything went smooth enough? Well, yes, with conferences, some things fail. It's, like it's a it's a it's a rule. But I have to say that everything was, super well organized in terms of logistics, the rooms, the food, drinks, I don't know, it's it's easy to come to this place, four rooms distributed. So, it's it's been perfect. minor things that we will improve for the next edition and I would say that

**[59:10]** The best feedback that I've received and even it's my opinion

**[59:15]** Is that this doesn't look like a conference but looks more like a series of meetups

**[59:21]** The engagement from the audience this kind of small approach that people feel like in a family or in a very cozy face

**[59:33]** Makes the atmosphere complete completely different than a conference that usually people run from one talk to another.

**[59:40]** Big venues well it's like the individual losses themselves into the big venue here it's like well I don't know like a bunch of colleagues

**[59:55]** Meeting so it's been awesome. why a new conference and why about AI?

**[1:00:02]** Well, definitely about AI because it's the new trend. Yeah. But I would say that the difference with this conference is that we've put a lot of effort on having talks that are mainly code. So there are a lot of AI conferences. people show slides, show concepts, show ideas, but I think that developers need to show need to see code reality things in the real world. So that was the idea just to bring another approach to the AI conferences ecosystem and also especially on not trying to be a big thing but trying to be I think an event that brings value

**[1:00:59]** That it is easy for people to consume that it's easy for people to mingle and to know new people and they don't lose themselves into the big forest, let's say.

**[1:01:12]** Okay. How are you using AI in your job?

**[1:01:16]** Well, I work for Sonar. It's a company that well the company behind Sonar Cube is one of it's a famous tool that checks code quality. and my role is developer advocate. So I usually use AI in order to create the articles outlines also. It helps me to write articles and to write content and also it helps me especially on proof of concepts when I need to try the code that will be put into the articles. But I I'm kind of the ideal portion of the AI consumption because I don't need to take my code to production.

**[1:02:09]** Even with my articles, I don't just simply ask AI and it generates the article and done. No,

**[1:02:18]** AI generates the some parts of the article. It helps me one of the biggest things is it helps me to fight the blank page syndrome. So it's hard to start typing a word but also it creates like the narrative. So I know the concept but it creates like the three lines in a natural language that will describe that idea. So it helps me to create words the boilerplate wording

**[1:02:49]** But definitely I need to review it I need to check I need to take the code and try it myself

**[1:02:56]** And see if it works and modify it and well

**[1:02:59]** Yeah I also found also for articles that it's actually also a good reviewer you can give it your article and does this make sense or what is missing that's also a good question that you can ask to this kind of systems

**[1:03:11]** Yeah definitely I would say that one of the best usages of AI it's to ask for explanations to verifications or to yeah to check okay I've written this article is this following this tone this is for I don't know this target is it really

**[1:03:34]** Is it for them to understand this content and also a very important part that every content writer has their own

**[1:03:45]** Style,

**[1:03:45]** Voice, style. I'm not an English native speaker, so I usually well, I use a subset of words, but those are the words that I use. It's like, yeah, I don't have a super high level of English, but that's my English level. So, my articles should should be aligned to my level. So AI with the right prompting, with the right constraints, with the right instructions can help to adapt content into our own language. Yes.

**[1:04:17]** Yeah. What is for you the most important thing between Java 21 and 25? So companies who are following this LTS releases and you're moving now to the newest one, what will they see as a big benefit? For me what I found in the reality is that usually companies are far from those latest versions. So those new features are great. but companies are more I don't know in the safe area and well it takes a lot to migrate from one version to another but definitely staying on the latest version will help on not only on the new features.

**[1:05:01]** There are a lot of bug fixes. there are a lot of vulnerabilities that usually when we talk about a new feature a new version of Java we expect to find I don't know shiny new things but even if they don't publish any new shiny thing they have fixed a lot of bugs and vulnerabilities and this it's important to check the OpenJDK issue track system and see how many bug fixes, vulnerabilities, even SDK or API, new methods. Yeah.

**[1:05:40]** Not not always those new additions are published in the new in the release, features

**[1:05:48]** Published by the new version, but you need to find out and you will find that there are lots of new APIs additions that can help that definitely. That's it for part two and that wraps up our coverage of the AI for devs conference. I hope you enjoyed these conversations about AI in development from data science and tooling to Java in the cloud and the story behind the event itself. Thanks for listening. Visit Foojay.io for more articles and podcasts about Java, the GVM, and growing your developer career. Give me a foo. Give me a J. Give me the friends of OpenJDK.
