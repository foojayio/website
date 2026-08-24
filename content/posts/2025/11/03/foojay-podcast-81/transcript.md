**[0:00]** Maven 4 is approaching its release, bringing many improvements to the build tool, powering millions of Java projects.

**[0:07]** Give me a JDK.

**[0:13]** Welcome to this Foojay podcast episode where we dive deep into Apache Maven 4, a significant milestone that has been years in the making. Maven is the backbone of Java dependency management and build automation and the road to version 4 has been long and deliberate with significant performance improvements, a modernized API for plug-in developers, and changes that affect how we think about project structure. Maven 4 represents both an evolution and a revolution. What does this mean for the millions of developers who depend on Maven daily? How should teams prepare for the transition? and what's the story behind the Maven Central repository changes that have been making headlines. To answer these questions and more, we're joined by a few of the many contributors who are actually building Maven for and driving the project. So, my name is Gim Nad. I've been working on Java and open source and particularly at Apache project since 20 years. so I was first involved in integration projects like service mix camel kafix and a few years ago I went I

**[1:34]** Joined the maven project and I'm I've been working quite hard those past years and months to get 4.0 with me. So I'm using Java. I learned Java since 10 beta 2 in the browser when it was only available in Netscape because it was when I was in engineering school, it was my topic from the end of studying. So I was happy to do that for a bank and Java was successful. So I stayed with Java for the bank for years. I started to use Maven one as a user. I knew it was not possible to do it to use it in the bank but when two started this time I joined a project to fix a few bugs and now I'm working on it since since no 16 17 years. I'm the current PMC chair.

**[2:44]** So I'm currently leading the project for what leading means which is just helping others. And now I'm working for sonotype for now six years which gives a new approach to maven san we discuss about maven central versus maven on maven 4 finally I imagine it with Robert halter quite a few years ago but I was not able to implement it. Gum joined and gum did a job. So thank you Gillon.

**[3:20]** Martin Mullers. I've been working with Java for I think 15 years. So that makes me relatively unexperienced in this group. been contributing to Maven since for a year or eight I believe. became a committer joined the PMC later on. I think most of my efforts have been into the core of Maven and some some features that will hopefully make it into Maven 4 and that would address some some long-standing pains in Maven 3. U, I haven't se Maven 2 or one. So, I can't comment on that. But even in Maven 3, there are some some things that were known to be not working as one would expect and very happy to see that being fixed in Maven 4.

**[4:19]** Okay. thank you all for joining. we scheduled this recording this podcast recording because Martin we met at Devox last month and you said yeah I was already announcing Maven for the year before at Devox but now we are really close I've checked the mailing list indeed when we are recording this there is a discussion about a new release candidate or a GA what is the status how far are we away from a potential release of Maven 4.

**[4:53]** Hopefully, we're really close. we basically froze new features a few months ago already. So, it's now about fixing bugs that are raised by early adopters and stabilizing things. Yeah, we we'll probably do another risk candidate in the coming days. let it cook for a few days or weeks and then release GA.

**[5:23]** I can imagine that there is some fear of breaking the whole Java development tool chain because yeah, everyone is using Maven or at least the Maven repositories. [snorts] Are you a bit hesitating because it's a big change or anyone or everyone is relying on it? Well, worst thing if there are very important bugs, people can still use me 3 for quite some time. So, we we'll have some time to fix any any problem. there are some known incompatibilities because of major internal refactorings and that will also come up in the coming months and years or surface. So some some things will be broken but I think collectively we decided that it was for the best. So there will probably be some time for during which people will have to experiment their builds and take some steps to adjust to Maven for

**[6:31]** For the future.

**[6:33]** And this is what we try to document as clearly as possible. There are a few steps. so Maven 4 against Maven 3 does a great job at compatibility. The same way Maven 3 did a great job at compatibility with Maven 2. Not two versus one. It was not possible. This is why we expect that Maven 3 was with resist for a long time. But it is a great transition period. You should be able to continue with Maven 3 and test with Maven 4 and confirm that it just works and we will fix what does not just works. But honestly for many many project it should just work. Then once they once people consider it works well they can start using the new features because there are many new features that they will want.

**[7:30]** When you start using new features, of course, you cannot get back test first seriously if the compatibility mode is good enough and it is really good. so by good enough, it should be good for the vast majority of people

**[7:50]** Before enjoying the new feature that we all want to start using in reality because because Gum worked for years on it. Martin, you said there were some things which were frustrating or not working in Maven 3. So what why did we need a Maven 4? Well, those two are not perceived related. at least personally speaking, the one of the bigger frustrations could have been fixed in Maven 3.

**[8:17]** Mhm. but it was that big of a change that once we finished it, we were already on the road to Maven 4 and we decided let's not bring this back to Maven 3 back then and we're speaking 2020ish. we thought, well, Maven 4 can't be that far away. I remember a blog post about it. What's new in Maven 4? people still quote it, but it's 5 years old and it actually addresses just one or two things. a lot less than what eventually made it into Maven 4. So, I should maybe rewrite that blog post, one of the new things in Maven, and say, by the way, this is just 1% and although it might be very visible, it is by far not the biggest change in M. is just something that you as an end user are likely to see at the surface.

**[9:20]** If we get back to why we did Maven 4, I think one of the main reason is that the pom file which has a model which is known as 4.0.0 O is deployed for all jars that are in central in Maven central and this pom file is quite frozen. We can't easily change it because it would mean that all consumers would be broken if we had new features or if we change things. So that was really what make Maven really powerful because everyone's using it and everyone can consume jars but at the same time for the Maven project itself it meant that we could not easily introduce new features that would ease our users they job. So one of the main thing from a user perspective is that we introduced the ability to write and enhance this pom file with a newer model version and Maven for will retro fit it into the known model which is uploaded on Maven central. the benefits that we users will be able to have access to those new features that will come in Maven 4 and in the next minor or major versions without breaking the world ecosystem

**[11:07]** And that was really one of the key point from a user perspective

**[11:14]** And it is what I documented in the famous article on Java advent to explain

**[11:21]** [clears throat]

**[11:21]** And to explain it at length, what build pal and consumer palms which are the two terms we use build pom where we can enhance because the user can choose which version of Maven he uses for building the project that uses Maven 4 but consumer pal from Maven any tool needs the standard one. When I wrote the article, I was doing a talk in every jug in France mostly but sometimes at some events where I was explaining why this was forcing us to create Maven 4 and I was always honest. I wrote the theory why we need it what we want. I am not able to implement it. Please join. It took a few years for Guom to join us and to do it. Nobody else did it. That's why between between explaining the plan on why the first level the key breaking change in fact was introduced. Gum was able to implement it and then other people added nice other additions that are that could have been done in Maven 3. Yes, why not? most of them.

**[12:38]** As a developer, if I start a new project, should I immediately go to Maven 4? What are the benefits if I just start from scratch and go to Maven 4?

**[12:51]** If you start a project, there are a bunch of things that are way easier to deal with when writing your poem with Maven 4 rather than Maven 3. So all the layouts of the projects there are lots of things that are redundant in the Maven 3 build pumps. For example, you usually need to specify your parents and the child sub projects. Whereas if your layout is just the usual layout with the parents being at the top and the children being directories below the parents then all these kind of things can be now inferred in the maven for build pom. So that makes it way easier to u maintain the bombs because you don't have to change lots of the same information in lots of different places.

**[13:51]** Yeah. I mean, I've been using ME for almost exclusively since years. it's really stable. It works. so I would definitely encourage people to start with the new one.

**[14:04]** The release candidate five serlessly has been tested. So if it was done to fix one edge case from someone, it is a very advanced edge case. Vast majority of project should not have that type of problem. So

**[14:21]** And this will be definitely managed and fixed because we want to we want to pass to four. We need it. It's long long overdue

**[14:36]** As as I've been using snapshots even of Maven 4 for many many years already. literally if if the build on the main branch is green, I have it on my machine a few hours later. and the number of times I ran into a problem with an existing project not being able to build it, it fits on the fingers of one hand because it's zero.

**[15:03]** Mhm.

**[15:04]** And [snorts] I don't even need to count them. it. We have such an extensive suite of tests that proves that everything works as it worked in the previous build that well, of course, bugs can happen. It's [clears throat] software after all, but the chances of it slipping through into something that gets uploaded and it could be used, I'm [clears throat] not saying it's zero, but it's it's very very small and I haven't been hurt by them. On the other end, I have been able to enjoy these newer features and just be happy with even the tiny improvements in your developer life. Like I don't think we even mention it, but the fact that your terminal becomes wider. Yeah, I don't know about you, but my screen fits more than 80 characters in a line. But somehow Maven 3 still sticks to 80 characters.

**[16:05]** Again, historical reasons, and it makes perfect sense. But in Maven 4, if you have these large blended of banded monitors and even if you have just a regular screen, we will use more of that screen real estate, which to me it's it's a small thing, but it makes me happy just to have this

**[16:27]** [clears throat] this more concise build output. So another thing which is really useful to end users is the ability to resume a build where it stopped. In ME 3 it was very diffic especially if you built in parallel. Now with Maven 4 you just add minus R to the command line and [clears throat] the build will resume from where it stopped. And that's really and in terms of ease of use, it's it's really a tremendous feature. I'm using it every day.

**[17:06]** When I finished that feature, I remember one colleague saying to me, "This alone is going to be my primary reason for going to Maven 4 as soon as get as it gets out."

**[17:18]** And I still love that one indeed. you regret not to have backported it to 3.9. [laughter]

**[17:26]** No, no, I don't regret because this it's these kind of things that I think will highly encourage people to use Maven for as soon as it gets out. the fact that these these tiny annoyances that you know and that you may be even have come to work around let let us not start the Maven clean install debate here but you know what I'm I'm referring to right you don't need to anymore it can make your life so much easier by the way what I also like is that in Maven 4 speaking of tiny improvements in developer life you will get these useful suggestions questions like you haven't pinned the version of a plugin that ships with Maven, let's say the compiler plugin or something, which effectively makes your build less reproducible because if I'm using this version of Maven and Guom is using another version of Maven, we may have the same code base and yet have a different build artifact that comes out of it. That shouldn't happen only because we have different versions of Maven with a different version of the compiler plug-in shipping by default.

**[18:40]** Now Maven will just tell you, hey, you didn't pin that version. You should and maybe Maven 5 or something. this will just break your build because you shouldn't be doing this at all.

**[18:53]** So, one of the big changes is in the pump file. So, are we still in XML? Yes. [laughter]

**[19:02]** Yes. luckily because I I'm I'm happy you didn't change to jam or something like that.

**[19:07]** The pom file is still an XML. It's just an a slightly modified version of the existing pom files. at the same time during this past years we introduced a way to actually have other format or languages to represent the content of the bump file. So there is an extension hosted outside of Maven that leverage that and provides YAML and okon and JSON 5 support for example. and the benefit of the consumer versus built pom is exactly to make that possible because your built pom can be a YML file while your consumer pom that will be uploaded to me central and consumed by other will still be the traditional pom file.

**[20:03]** And I have a big announce we have a road map. You know what for Maven 4 it is an extension for Maven five it will probably be built in when will Maven 5 happen we don't know but you know it is the same announce than Maven four five years ago I don't know how many years but it is probably for Maven five stay tuned

**[20:30]** Are there other important changes for plug-in developers or integrators that they should know at this moment Yes, one of the things that we did in Maven 4 is change the internal model to rely on immutable objects. So for example, whenever you pass the pom file, everything is now old into immutable objects and we will offer a new API for plug-in developers that will be much more close to or at least easy to use. At least that's the goal. the reason that Maven 3 evolved for the last 15 years and there was no clear definition of what the API is and what plug-in developers could rely on. this was also a problem for the Maven project because everything kind of became part of the API and we couldn't change things without risking breaking existing plugins. So this new API is not really it it's embedded in Maven 4. It it's still in preview for the GA release. The goal is to attract plug-in developers to try the API, see if there are any missing things for the plugins use cases and there will certainly be a bunch of missing features and to add

**[22:07]** Them and in the coming months the goal will be to release this API publicly and prepare for the migration of Maven Swift to Maven 4 plugins with a much cleaner API and easier way to develop and test

**[22:24]** As a developer as a Maven user. What will be the biggest [clears throat] change that I see when I am working on a project or building a project?

**[22:34]** If you keep your project the way it is, you won't see any change or not much. I mean on the face if you still use the existing plugins the 4.0 model. So the one from Maven 3, you can work the way you were with Maven 3 without changing anything. But for example, now in Maven 4, you have much more control over the life cycle and the way the plugins are bound to phases. this has been improved a lot, the wall life cycle. and this will mostly make sense when people switched really to Maven 4 and leverage the new features. One example in Maven 3 we introduced a long time ago the parallel builder which allows you to run things in parallel. in Maven for we made that a bit more powerful by allowing slicing per plug-in for example because of this tree of things of this tree of phases we can then we don't have to wait for dependency to be fully built before being able to use it. So for example, if you have a few modules which are now called sub projects with Maven 3, you had to wait for the your dependency sub project to be fully built before

**[24:15]** Being able to start building the next sub projects. this is no longer the case and that makes sense because you can test your dependencies after having them packaged as a jar so that they can be consumed in later in the build. So these kind of things will allow you to have a much nicer parallel build for example and gives you more control over the life cycle and everything. I think another thing that users will definitely see when switching from Maven 3 to 4 is that if they run a Java version older than 17, Maven will no longer run. [snorts] And although it may sound like a very small thing I can imagine in some organizations this is going to be a gamecher in the sense that well we've always run Java 11 or who knows [clears throat] what old version of Java and now all of a sudden we need this newer version of Java. so that is something to be aware of and maybe even prepare for if it requires you to install that latest version of Java [clears throat] which I would say is always a good idea to do but well reality kicks in and some

**[25:50]** Organizations just have policies around these and some organizations even seem to have policies that prohibit you from running newer versions. I fa to see the rationale for that but

**[26:04]** Let's consider that a flaw on my side.

**[26:06]** [snorts]

**[26:07]** Yeah, that's a whole other discussion that you all the improvements in both the language and the runtime and yeah that your Java application

**[26:16]** Aspect of course but yes still I know such organizations exist and for them it may be a little bit of a bigger hurdle [clears throat] and no that does not mean that you can't build your Java 7 project anymore because you still can But it will require that would require a change in your pawn. Actually

**[26:41]** Imagine the hot discussion between the two extremes of the scope. Some wanting to stay with Java 8, others wanted to go with Java 25 given we are. [laughter]

**[26:55]** So true.

**[26:56]** The exercise at keeping Java 17 was a hard exercise which is both old and new. [laughter] But anyhow, if you start a new project nowadays, it should at least be 21, 25 and Maven 4 in a few weeks hopefully. So if we start a new project, then I think it's not an issue to go to latest versions.

**[27:20]** Absolutely. hope for you that you can use those latest versions. But as I said, reality kicks in and Java of course is famous for

**[27:31]** Providing a very long support even on old code bases. you can run still run them even if you haven't touched them for many years. But well in terms of building this will require you touching your build setup if you want to leverage of course Maven 4 there's nothing as as GM earlier said

**[27:58]** That forces you to do so and if you don't feel like Maven 4 will bring you benefit for your old Java 7 application then you always have the choice of keeping Maven free for your bit

**[28:13]** And as you said the libraries which are created or which are upgraded to a newer ma maven maven 4 will still be uploaded with that same pump file so they keep the compatibility. Yeah.

**[28:24]** Yes. Yes. Yeah. Otherwise everyone else will be very sad. [snorts]

**[28:30]** Sad or angry [laughter]

**[28:32]** Or both even

**[28:33]** Or both. which actually brings us to the repository. So if we talk about Maven yeah it's it's a build tool. It's how you define your project with a pom file, but it's also the repository. And we have someone here in the room of Sonotype. So, can you explain us what is the difference between Maven, what I use as a developer on my PC and Maven the repository?

**[29:00]** This was one [clears throat] of the key aspect discovered created when we created Maven one. to have a dependency resolution you need the build Maven one and you need the location where binaries are available and this one it is a repository somewhere at that time it was a small machine somewhere. so this is where you need to download from an external repository and this is where the splits between the command line tool, the build tool and the source of JAR files is necessary is something that happens quite normally and now it is something that is quite normal because we know that we download dependencies everywhere. The term dependencies is no natural. when Maven one was created in early 2000, it was a discovery of that need of the both both aspects and hosting artifact and distributing artifacts and having project publish artifacts is definitely not something that is that has the same expectations from people, the same needs than really writing code as we do as Apache. So this is why after years there was a split that was even bigger from Apache software

**[30:30]** Foundation maintaining the software and finally individuals on their own laptops and with friends doing mirrors started and when the mirrors started growing when more people wanted to publish it became hard to do it as just a group of people it started to become something that had to be more professional is where stonotypes entered the loop at managing the repository aspect of it the Maven central aspect of it and letting Apache software foundation continue maintain the build to it

**[31:09]** And can we be proud of Maven the repository because we have similar systems for JavaScript libraries

**[31:17]** Isn't the one we use as a Java developer the safest one because there were quite some security issues with JavaScript libraries for instance.

**[31:27]** There are many many choices done very early that when you are when you are a Java hater you hate them. [snorts] When you are a Java lover you say you know what it is why we chose Java even be before Maven existed. which is the notion of group ID, the notion of package. In fact, when you look at any other language, the notion of package that Java introduced is really a love or hate attitude for everybody at language level. And it is that structure that exists at language level that was brought to the repository level. And just that structure is helping us securing the one aspect that other ecosystems have which is avoiding they dislike that the name is long but it's because they know name is short that you can do a lot of tricks [snorts] in Java sorry name is long yes but it had good reasons good intents and good results in fact at the end

**[32:39]** This is one of the aspects the other aspects We are Maven Central is immutable. So you [snorts] can't play at replacing you can't no no no no everything is enforced particularly when the job of maintaining maven central has been hardened by son type it was one of the objective when splitting from just a few people helping at the Apache software foundation to something that was more strict at controlling every rules. We introduced PGP very early. People hate it. but it helps because what do you think about a students learning a language in five minutes and one hour later is starting to publish on official repositories. so and these all the small hurdles for people who hate it but we love it. [laughter] It is designed to improve the situation.

**[33:43]** Yes. By being a little bit a little bit harder to use at first. Sorry.

**[33:49]** But there's nothing too big of a hurdle to take. I mean you don't require people to pay a yearly fee to be able to publish. You don't require specific credentials. Everybody that has a GitHub handle or domain name can publish on Maven Central eventually even without spending a scent. you could do that but there will be checks and balances that make sure that not somebody else publishes under your name for instance that is one thing I like as a person but I think companies will also like it it's impossible for me to publish something under a group idea that I don't own or that I don't belong to

**[34:39]** That I c I could only do that if the owner approves me of doing such I think that for one is already a big improvement compared to other package repositories.

**[34:52]** As a library developer, something changed recently in how you authenticate to Sona type to upload u a new version of a library. what's the reason behind that?

**[35:04]** The reason behind that is very easy. it [clears throat] started in 2008 with technologies from 2008 and these technologies are aging. [laughter] So it was it worked. So the there was no urgency about changing it for years. It still worked. People were not happy. It was not funny. The UI was ah started to get old and so on. Okay, it just worked at some point in time invested brought more money on the table because it is the question of funding that is behind that because changing requires even more effort not only maintaining a system that runs but building the new one that permits to get to the next generation. If you go to Maven Central, there are different URLs which you can use. So you have oss.ssonotype.org, you have central.ssonatype.com.

**[36:12]** What's the difference there? When I was saying that some parts of maven central are aging, I was talking about a third one with oss.otype.org and search. Maven.org. oss.otype.org or is also called OSSH which is the publication aspect that was there for years. search.mmaven.org is a UI for searching artifacts. These two ones are very old and are yes currently dying and they were merged in fact they were rewritten into one single central.ssonotype.com sonotype.com portal the famous central portal as they put in the documentation that brings in one single UI the search and the publication which is the modern approach that every other ecosystems have from the beginning because they started later and this is what is implemented now when replacing the old individual pieces is

**[37:28]** So someone who wants to create a library and publish it to Maven should go to central.ssonotype.com at this moment that's the way to go now.

**[37:36]** Yes, from a publication perspective and also from a search perspective if you go to search.mmaven.org or you should be redirected. But if you are not because you managed to not be redirected, just know that it will disappear one day [laughter] and it is not for example it is not updated as fast as it should as because it's dying. [laughter]

**[37:59]** Okay. But the central is the one that we should remember and forget all the other ones. Okay.

**[38:04]** Exactly. Is it true that the very first Maven repository server was somewhere behind or below the desk at the Sona type office and is it still there?

**[38:18]** So said like that it cannot be true because the company [snorts] was created later.

**[38:25]** Okay. [laughter]

**[38:27]** But it may be a laptop that was under the table of one of the founders of Sonotype. Yes, [laughter] before he founded Satite

**[38:39]** But yes yes yes it started very small and the first there are some stories about in 2000 how do you find bandwidth free bandwidth how do you find free disk because at that time it was individuals funding the machine funding internet which was it was cloud need did not exist so yes having anything was not so easy.

**[39:05]** Mhm.

**[39:06]** So yes, it started very very small from a few people doing paying with their own with their own bugs.

**[39:15]** Yes.

**[39:16]** But I really love the idea and how it's all evolved. It's still all free just like Mart said. If I want to publish an app to Apple, I need to pay I think $90 a year while everything with related to Java, Maven, the build tools, the libraries, all this hosting. Isn't it amazing that we can use this all for free?

**[39:42]** This is why I joined Sonotype [laughter] because they managed to deliver and to keep it free to keep the spirits. Imagine the cost behind you are under what you imagine because there is the machines the storage terabytes of storage pabytes of downloads but also all controls of identity of people asking for one group ID that they lost their password they lost even the everything that prove they are the legitimate owners and things like that. So there is a lot of work behind it and yes I'm I'm happy to that site managed to continue to fund it continuing to be as as open as the first spirit when they started

**[40:32]** I think you can actually contribute to that have a correct me if I'm wrong if if you are operating inside a large company by considering to actually host your own caching repository just to prevent a lot of bandwidth going directly to Sona type. Right.

**[40:53]** I need to share the blog post and the recent post that were done by Brian Fox. he did it in the name of Maven Sunfall one or two years ago to explain people that they should cash things and stop downloading as crazy. And after having published that for Maven Central other ecosystem started bringing him ah we [clears throat] have the same problem we are Python foundation we have the same problem. Oh we are Eclipse we have the same problem. Oh we are and in fact every every repository public registry is currently having the same issue that people don't get that it's free but you can exhaust it. two months ago they've wrote an open letter to let people know about stop abusing registries will be forced to do something whatever the something everybody will choose what they can do what they will do but now it is not only mavenful it's every public registry which has which is facing the exact same problem and recently it was funny to see someone on Reddit saying, "Oh, I'm building every every minute I'm building a software from an empty machine, so I'm done downloading."

**[42:23]** Download is taking time. How [snorts] can I download in parallel to be faster? No, don't exhaust even more the system. [laughter] It is what people need to understand. You are exhausting the system. At one of my previous jobs, we indeed set up a cache in between which also sped up our build times because the files are very close to the build machine. So it's not only that you take away a little bit of load on the system itself, but you make your own system a lot faster. So that's that's also one of the advantage and you can keep your own internal libraries within that cache without releasing the Maven central which is an issue for a lot of companies. And on top of that, you can even use advanced features like blocking certain artifacts that you don't want to be consumed at any situation. we've all seen the log for shell attack. by now hopefully all libraries and tools that you use don't use that version of Log4j anymore. So you could safely say if any tool or whatever tries to download that, it will just not get it. It's still on Mason Central for good reason because

**[43:34]** Immutability and so on. But in my company, I don't want to use it anymore. So if somebody tries to download it, just refuse. By the way, there was a great interview with one of the creators of lock for shell Log4j about the lock for shell problem. I think it was an interview by GitHub. I will add it in the show notes because it really shows how an open-source project can suffer from a problem like that and how it can be resolved thanks to the many volunteers. If we go back to Maven 4, so there are some changes there in the bomb files and the structure. How is this related to the evolution? We see that some companies and more companies ask for bombs. So these bill of materials which are in my opinion a bit the same thing within your pom you say which libraries you're using and which version. So is generating bumps something which is now part of Maven 4 or which will be easier.

**[44:35]** So for me there is misuse of terms. Maven created the notion of bomb of bill of material 10 years ago which defined the versions of certain dependencies. So in your build you don't need each time you use a dependency to define the version. It will use the bill of material that is predefined in your this notion of bill of material. I try to I try to force people to not tell bum anymore but tell bum pom because it is a bum pom it is a pum that defines the versions then you can import it to force your build to use some version so it's it is really build oriented at forcing using some versions of dependencies for now five to six five to six years we have the s bomb software bill of material which is a bill of material too but that one is completely different with cyclonics with spdx and these ones are about listing what is inside your build or what are the effective dependencies and for that we have plugins that do the job and it will remain at plug-in level for now I don't see why you would do something inside maven for it we have plugins for spedics we have plug-in for cyclone DX. I

**[46:08]** Maintain the Cyclone DX1. I help on the SPEX one. and just understand that ESBOM is what is the current expectation from regulation and we have plugins for that for listing dependencies which is the first level of bomb that are necessary. The famous bum that people that expert in Maven talk about are bum bum which is completely different.

**[46:38]** To [clears throat] get back on the bomb problem on the technical side.

**[46:43]** You mean or sbomb [laughter]

**[46:47]** Bum bum

**[46:48]** Bum bum. so we have some very well-known projects such as spring boots and such projects that defines and rely a lot of on bump poms. This actually causes a lot of a bunch of problems [snorts] for the Maven build tool itself in terms of memory consumption and performance. And another point is that as said the bum bum is specifying which dependencies or which version of the dependencies you should use. but as soon as you have multiple conflicting sources, it's becoming very difficult to manage and currently the only real solution is to actually override in your own pawn to clearly specify which one you want. It's it's really difficult to handle. so that definitely one area that could be improved to some degree. we I'm not sure yet how it will actually become something usable but there's definitely some some things to do in this regard but it's not for auto so maybe later

**[48:17]** For the record adding bum poms and the import of bumps is the feature in 2030 13 something like that was added to Maven 3 where we did not were not able to change the pump format but added a little trick and this is where we discovered oh we found a trick but we can't do anything the in fact and this was the beginning of the thinking in 2013 we have a problem then two or three years we need to find a solution then for three Oh, we found a solution. Then five years for oh, we implemented a solution. But it is exactly for bum bum is was really the first case where we tried to add a big new feature to Maven 3 and we discovered that not being able to change the P format was part of or anything really strong was part of we are stuck because of Maven Central. and this is where the relationship between the repository and the build tool remains quite quite important.

**[49:36]** Yeah. And you have this backlog of all the existing libraries. Yeah. We cannot ask developers to update all of them. [laughter]

**[49:43]** And at the same time, we have some conflicting things coming from other builds such as Gradel which actually upload some meta data to Maven Central which they use but slightly differently from Maven. And so it makes consuming things a bit more difficult because depending on which building which build tool you use, you may have different results and it's kind of breaking some expectations. So there's definitely some some stuff to do around that, but we'll see. And you talk about that because gradal is okay is heavy and it's currently happening a lot. One thing that people don't know a lot is for example SBT published some artifacts to Maven Central that does not conf conform to the name to the naming convention. So Maven itself cannot get the file because the name of the file cannot be represented by normal group ID artifact ID classifier extension. SBT is only one to be able to download.

**[51:02]** So this is where we are sharing now a common resource and clarifying which rules need to be kept and when someone try to expand on it when it's just something that will be usable by himself is just it's something that will break others because if it is a library done with grad we expect that it should be consumable by others. So sometimes it's tricky. it's really hard. No blaming of anybody. That's not the topic.

**[51:36]** How should we compare Gradel and Maven? Like Gradel is also using the Maven repository. Is it the competition? Is it good ideas from one or going back to the other? The main difference in my opinion is that Maven relies a lot on descript descriptive things. so you when you write your poem, it's not executable code. So I think that's what maybe led to creating gradal because they wanted maybe something more easily to tune and modify but at the same time it causes problems because it's actually not just a tool it becomes a program itself your build environment and build configuration. so it's it's much more tricky to dive in my opinion than than just Maven, which is

**[52:42]** Purely descriptive, but they have some good things. I think they weren't frozen like Maven was, so it was easier for them for Gradel to experiment and bring new features. hopefully once Maven 4 is out we will be able to bring new features also by enhancing our build bomb while keeping the ecosystem friendly and not breaking anything with the consumer bomb. So yeah I mean and at the end I as said it's just a matter of u how you better like one or the other. I

**[53:27]** They have different strengths. So

**[53:30]** Choose your problem flexibility or just use as everybody and then it is easier to just build because it's MVN package. The fact is that from a gradal company perspective a few years ago they started to have some business offer that works both for Maven and Gradle. Then there is no conflict. It is clear that m make your choice. Are you these type of guys who love to the to adapt everything and pay for it because pay the complexity for it but it is a complexity you want to pay for because for you it's not something that it's not a cost it's a benefit for us it's the opposite. we avoid the complexity which avoid the costs but you need to accept the rules

**[54:27]** And now I think that line of choice which is really a line of choice is well established which permits not fighting it's not a question of fight it's now it's eventually finding problems because eventually sometimes there are some some problems the famous gradal modules it's it's not intentional but it's something that will have to fix. For example,

**[54:51]** I think you summarized it quite nicely. choose your poison. and sometimes it's not really you can't really choice. let's say you're building an Android application. Well, you may try to do that with Maven, but I think you will really be hurting yourself if you try to do that. you will also try hurt yourself when using gradal for it because you need to do all kind of things that as a maven user you might not be prepared for. Gradle works in [clears throat] a different way. Their philosophy is different and I [snorts] guess if you're used to that philosophy it makes a lot of sense how it works. me coming from a Maven background working on an Android application, I don't feel at home. But that's my personal experience that I bring to the table that makes it harder for me to use Gradle effectively.

**[55:46]** Let let alone make customizations on my Gradle build other than the incidental scripting right inside my build. Gradle. building a plug-in or something is not something I would try. Whereas on Maven, I I've seen projects where I built a Maven plug-in just for that project because we had a very particular situation that we wanted to address which was only happening inside that project. And it made a lot of sense to just write one or two classes really that together build a Maven plugin that fixed the issue we faced.

**[56:33]** I think in a gradal world I wouldn't have been able to do that. But then again that's bringing my experience to the table and if you are familiar in the in a gradal world then you might have been easy to do it in Gradle. You might have been able to do it in Grado as well.

**[56:51]** But we know people we are friends. Yes. Because we agree to disagree on some topics and others. And sharing what are the benefits and the issues of the different choice we did is interesting because there is not one single unique choice that is the right one. M what I do find interesting though is that there that the two communities also look at each other and some things that were envisioned for example in the gradal community like having a built cache or a demon made their way into Maven

**[57:29]** As well and the other way around I think Gradle leverages the fact that we have a wellestablished metadata format that we have a central repository tree and Gradle would probably not have been as successful as they are if it weren't

**[57:48]** For being able to use whatever the Maven community had already established.

**[57:54]** Yeah, I want to thank all of you for all your work you're doing on Maven and there are a lot of other people also. I just checked there are over 200 contributors on the Maven GitHub repository. you spend a lot of time on this and not only you all the other people also who contributed. What is the reason that you are putting effort into this?

**[58:16]** So as a disclaimer I'm not doing that on my outside of my paid job. Okay.

**[58:22]** I'm actually paid so to work on Maven. So

**[58:26]** That's a fantastic model because yeah I'm also involved in a few open source projects. Throw the money at me. No.

**[58:32]** [laughter]

**[58:32]** These open source project there are a lot of companies using them and only a few of them are putting money on it. So Sonotype is hosting the repository. Gil then paid for your work you're doing there.

**[58:44]** I have been paid for it past as well by the way but not

**[58:48]** Not be not as part of my consultancy but next to it. So

**[58:53]** For a couple of years I could spend one day a week or something or I don't even know the exact numbers anymore. because my company says, "Hey, we benefit from open source. We should contribute."

**[59:06]** Mhm.

**[59:06]** And we can contribute by sending money like like you just solicited for or we can contribute [laughter] by contributing time and brains and features and we liked the ladder more than we do the former. as someone who wants to join an open source project, how can I convince my manager that this brings value to a company? G, how did you convert this into a job?

**[59:34]** So, I I'm working on at IBM and I was previously part of Redat and my business unit has just been moved to IBM a few months ago. Redat is well known to be a shop of open source projects. So Maven may not be dominated by paid workers mostly because they are not a bunch of companies actually selling support directly on top of Maven as a build tool. for example if you take all the open source projects for example a database just or CFKA whatever you have a very you have big companies that are paid to deploy those things in the system of their customers it may be slightly different for Maven for example but I think a couple of people are actually paid to work on that as Martin said. So it's it may be a bit less than for other projects. And the other thing I want to mention is that you said 200 customers, but maybe you touch core

**[1:00:58]** And all the plugins and all the there are also a bunch of small libraries that are mostly used by Maven. that attract other contributors and I think the number is much higher if you take the whole Maven ecosystem

**[1:01:21]** Into account

**[1:01:22]** But it's harder to count because you need to merge something like 100 Git repositories.

**[1:01:30]** [laughter]

**[1:01:30]** Yeah,

**[1:01:31]** I think for me at least one of the reasons that made me try to convince my manager back then and I succeeded is for one it is in line with the open source philosophy. You get something, you bring something. That is a bit of an ideological approach maybe and for commercial companies may not be that convincing but what I've also seen is it is super motivating for the employees because working on Maven or any open source project with a significant user base that brings a lot of that gives you a great way to bring impact to many people and to many other software projects. I remember having a discussion and saying okay but the code that I write will one day run on millions of machines worldwide because that code is in Maven. on how many machine does your code run? Yeah, five. That's our production environment.

**[1:02:37]** How many users do you have? Yeah, we don't know. Well, I do. so that is one thing. And if your company is still not convinced then you can say well having such a program h having rules around this that allow people to do this can also be a great way of attracting talent talent that may otherwise say think well they are just one of a dozen companies there's nothing special about them. it may it might make you stand out from other companies that are trying to attract the same talent and don't do it because it's hey it's a nice distraction from your day-to-day work which might be very challenging but might also sometimes be a bit dull and then imagine being able to look forward hey but on Friday I'm getting back to work on Maven Quarkus whatever your favorite open source project is [snorts]

**[1:03:37]** And Given in general we are talking about the project that you use during paid job being better at it by contributing improves your efficiency improves your capacity to improve your day job. Even if yes you don't do it only for your company you do it for others but you benefit from others too then the benefit is even greater and for individuals is immense. I learned so much from others that it is incredible. I would not be a developer as I am without learning from all great people developing on Maven. So but for the company yes there is really an efficiency behind it.

**[1:04:25]** That's that's a good one. It makes you more proficient in the tool that you're contributing to and that can also help you in your regular job where you depends on what you're doing. But well knowing the tools that you use and the libraries that you use can make you deliver more impact there as well. For a long time I was in the company that just [clears throat] let me do without blocking me which was a minimum because some in some companies you are not allowed to even try to do something. So but I switch to a company that helps. I'm not paid for that but they help. I am there at the conference paid by them. So we're we're not really paid for that but seriously hurt which is great and yeah it helps the company too.

**[1:05:23]** Okay with the release in a few weeks hopefully I'm ready to move all my projects. What should I do? Is there an easy way I can do that?

**[1:05:33]** Yes, definitely. so Maven for brings a small CLI tool that you can use it's called Maven upgrade so MVN up and it allows you to get your Maven 3 U pom files and migrate them to the new model so that you can leverage everything for example the inference that we talked about the fact that you don't have to specify your parents anymore and stuff like that. So this can migrate all your wall project to Maven 4. So there's a downside that you will require Maven 4 because it will upgrade to the newest model. but it's it's provides an very easy way to migrate and leverage things.

**[1:06:28]** Definitely something to try out. thanks for joining this podcast recording and all the work that you've done to bring Maven 4 to the stage where we are now and looking to yeah the publication of the release and then some examples and Martin you maybe will publish something on Foojay on getting started with Maven for let's see what happens in a few weeks a JDK Hey.
