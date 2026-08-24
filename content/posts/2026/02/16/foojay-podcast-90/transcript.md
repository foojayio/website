**[0:00]** What has changed between Java 21 and 25? Let's find out.

**[0:04]** Welcome to the Foojay OpenJDK.

**[0:10]** Every 6 months, we get a new version of Java. Java 26 is just around the corner and will be released soon, but most companies stick to LTS long-term support versions, which are maintained and receive security updates for many more years.

**[0:25]** Versions 8, 11, 17, 21, and 25 are such LTS versions. I hope most of your systems are already on one of the latest versions and you're not stuck on 8 or even earlier. As a reminder, 8 was released in 2014. So much has changed since then. If you're doubting moving from 21 to 25 or even from an earlier version to the latest LTS, this podcast is for you. I invited one guest for this episode and have a few quotes for interviews recorded at conferences last year to guide you through the most important changes between release 21 and 25. Jacob, this podcast was actually your idea, so thanks a lot. Can you please introduce yourself?

**[1:10]** Yeah, my name is Jakob Yenko. I am Java developer since 1999. I've had some some years in cloud architecture as well, but at the moment I am back to doing architecture and Java development again, but of course all products today have some kind of cloud aspect to it. So yeah, I haven't I haven't let go of the cloud part. we're just now I'm doing more product development but it's with Java and yeah we have actually just recently started migrating to Java 25 at work so that's interesting it's not I think it's not out everywhere yet

**[1:53]** Maybe by the time this is published maybe it is but yeah so it's a process it's a process

**[1:58]** You're working on it

**[2:00]** Exactly I've been yeah so I've been I've been and that's how we met I've been writing a lot about Java and just as you have and I think that's how we met originally and yeah

**[2:10]** Yeah your tutorials is one of the biggest thing you can find online with tutorials related to Java Java VIX but also related technologies so that's the I cannot remember but the number of visitors you have on your tutorials is huge

**[2:29]** Yeah it was but you know as

**[2:33]** AI

**[2:34]** AI I eats everything, right? It also eats it at Stack Overflow, but it has also been eating away from my side. But

**[2:41]** But I'm sure that a lot of the knowledge in the models is based on your website as as one source of truth of about Java.

**[2:51]** I have tried asking it for some of the topics that I know are only on my website and

**[2:57]** They can answer those. So there is something coming from my website, right?

**[3:01]** Yeah. And maybe one day you will get a big bag of money from these models because [laughter] that's

**[3:08]** I wish there was actually the discussion we had in the previous podcast micro payments and an idea of how we could how all these providers could give some of the benefits back to the people who provided the sources. But that's for people please listen to the previous episode of the Foojay podcast and you can know more about that. Okay, let's start with all the changes. I have a quote from Jonathan Villa, I think recorded in Amsterdam last year and it's about the overall bug fixes and performance improvements. let's let us listen.

**[3:43]** In the reality is that usually companies are far from those latest versions. So those new features are great but companies are more I don't know in the safe area and

**[4:01]** Well it takes a lot to migrate from one version to another but definitely staying on the latest version will help on not only on the new features. There are a lot of bug fixes. Yeah, there are a lot of vulnerabilities that usually when we talk about a new feature, a new version of Java, we expect to find I don't know shiny new things. But even if they don't publish any new shiny thing, they have fixed a lot of bugs and vulnerabilities. And this it's important to check the OpenJDK issue track system and see how many bug fixes vulnerabilities even SDK or API new methods.

**[4:50]** Yeah,

**[4:50]** Not not always those new additions are published in the new in the release features

**[4:58]** Published by the new version. But you need to find out and you will find that there are lots of new APIs additions that can help.

**[5:08]** Okay. So important thing to remember here is it's not about all new features. There are a lot of improvements and bug fixes ongoing with each release. Is that something you see when going from 21 to 25 that you just win on performance improvements? It's definitely I don't know if you can like how much you can feel it but the virtual machine is getting better all the time right and especially some of the hidden things that are like hidden a lot from us is the JVM's capability of compiling you know to native code and as part of that native code use these SIMD instructions these vector vector instructions so vectorization is it's sometimes called of the And I don't have any insights on exactly how many changes were made of that but they are typically people are getting like the people who are experts in this they are getting better and better at figuring out how to take normal code and vectorize it. M

**[6:14]** Yeah and because Java jar applications are actually backwards compatible you can run an older jar on a newer runtime and benefit from these advantages.

**[6:25]** Exactly. I mean especially if you come from Java 8 and you go to Java 25 then for sure there has been added a lot of these kind of auto vectorization improvements. So

**[6:38]** It's [laughter] 12 years since Java 8. A lot has has happened there.

**[6:45]** But I remember actually just in this relation I remember having the discussion on LinkedIn with someone who said that they in their company they would stick to Java 8 because it was the long-term support version that had the longest support into the future. Personally, I kind of find that to be like a non-issue because if you might go on Java 17 and it has a shorter long like a shorter end of life. So for the support,

**[7:14]** But before you get to that, you will have, you know, Java 21 and then you have Java 25. So you will always be able to switch to a newer LTS version

**[7:24]** Which has enough of a long-term support life, end of life that you can get from that one to the next one to the next one to the next one. bridge it into the future. So yeah, you should definitely try to stay up to date with the with the [clears throat] latest LTS versions in my opinion.

**[7:43]** And eventually Java 8 support will end.

**[7:46]** Exactly.

**[7:47]** Yeah. Okay. [laughter]

**[7:48]** And then you have no choice.

**[7:49]** And then you have no choice indeed. So that's the overall thing. And then there are a lot of improvements and we're trying to focus on what changed between 21 and 25. first one and there are quite a few posts about this is that Java script so Java as a scripting language

**[8:09]** Becomes more and more of a possibility and something you can do and I have a nice quote about that from Ryan Filla.

**[8:17]** Oh it's it's the scripting stuff. Yeah, I mean 21 was already a huge step up from the past. Just being able to write a simple Java class file and then just run Java against it was already a fantastic change. But literally that I can write scripts effectively in Java for me is really going to be a game changer for just working with my environment. It's one less thing to install, one less programming language to worry about and that's for me the biggest change.

**[8:40]** Yes. So using Java for simple scripts like most people use bash, Python scripts but actually Java is also perfectly suitable to do such things. That is true. I think it might be mostly true for Java developers who already have the SDK installed. if if you are just a like a regular Linux user, maybe you won't have Java installed, but you will have Python installed. Maybe you want to stick to Python. But as a Java developer, you can definitely start using this scripting capability. From what I have seen, it has become a lot easier to write a simple script, run it without even compiling it. You just ask the JVM to run it and it compiles it itself. You can even have like multifile scripts, right? You can import other other non-compiled files into your script. And also now in you know with the latest changes in Java 25 and actually between 21 and 25 this is one of the things that has been proved a lot that you can just write some very very small script files and like you don't need a class declaration anymore.

**[9:51]** You

**[9:52]** You can declare even a functions outside of a class and you can still call them and then behind the scenes it gets inserted into a class but you don't need to worry about that. Yeah, that has gotten a lot better. I haven't I haven't played too much around with it myself, but this is definitely something to think about.

**[10:10]** I love it because yeah, you have this compact source files and instance main methods. That was the chapter which was introduced in Java 25. So that's indeed the private static final main string array arcs has been simplified into what is it? Public main and that's it. So if you want to explain Java on to a newbie that's definitely much simpler but I use it a lot even in CI/CD to do some stuff on the build pipeline for instance and compile something. That's that's what I was also thinking like if you're in if you're a Java company and you need to do some some scripting in the CI/CD pipeline instead of having to change to Python it's it's really nice that you can now just stay within the language that you already have a lot of expertise in.

**[11:00]** Yeah. Yeah. The only thing we are missing is a bit that like in Maven and Gradal projects you can add your dependencies. That's not possible with pure Java yet. But still you have the GBang tool. I don't know if you've used that but it's it's really I use a lot of Jbang in the Pi4J examples explaining Java on the Raspberry Pi and interacting with electronic components there you have Jbang that in again in one file you have you define your dependencies and then just your code to blinklet for instance and you don't need a full main and that's yeah that's where we are arriving into a state that is very comparable to Python and these other scripting languages where you can combine everything in one file. But yeah, as a Java developer, I really love this simplicity. Mary Greinski also said something about this

**[11:54]** Definitely the a lot more simpler to use too and I think it's becoming very crucial I think in this modern age when we are now like faced with the other languages that are you know purportedly or also like is true to they are bit the interface is simpler maybe for folks who are less of a programmer to use it so I do see it that one aspect is very good if we can definitely simplify we kind of take away some of the complexity of interfacing with it and so becoming more attractive to let's say the data scientists we we're hoping they can also adopt it and because there's just such powerful features within Java itself so I think Java 25 you're getting back I think definitely has make a lot of inroads into like making it easier and to get for people to get adapted to it I think that should also be the way to go to especially for the AI way things are going vi coding all of these things so yeah yeah

**[12:54]** A lot of changes there a

**[12:55]** Lot of changes yeah that's right yeah

**[12:58]** Yeah v coding AI we have a lot of that LangChain4j also something which brought AI to Java so all this combined I think we can indeed maybe win a few Python believers back to Java with what is now possible it's definitely possible

**[13:16]** It's definitely possible sometimes people are not choosing using tools based on what solves maybe the problem best but what is easiest to use for them.

**[13:27]** Mhm.

**[13:27]** And we can see that when a lot of people have chosen Python instead of maybe C++ or Java or C or whatever. But I and I think this is a realization within the Java camp that ease of use is a big big driver of adoption of a language

**[13:48]** And this these scripting features are simply making Java a lot easier to use. So it's much easier to just play around with it. just install Java, write a couple of scripts, see something happening. And I think especially

**[13:59]** That is my impression that this is often the kind of interaction that data scientists have with data. It's it's not that it's not always that they are writing a complete program and just reusing that program again and again. Sometimes they are doing like oneoff projects like trying to get some data from this system from that system and try to make a report for management and they hand it off. maybe they never have to do that report again. so that kind of more exploratory data science will now be easier to do. Not maybe not easier than in Python, but at least maybe we're getting

**[14:36]** Close to as easy to do in Java as in Python.

**[14:39]** Yeah.

**[14:40]** Right.

**[14:40]** And that you don't need full projects to do that. Yeah,

**[14:43]** Exactly. And it also maybe makes it easier for those of us who come with a Java background to get into the data science tasks right or the projects or whatever because now we don't have to learn a completely new language and a new complete new environment. We can kind of stay within the environment that we already know and now we can like it opens up for us. Yeah. Yeah. And try new stuff. Okay. scripting is on one side it's it's very visual. another topic which is inside the GVM is the garbage collectors. before I joined Azul and learned about all these different garbage collectors and different use cases as a Java developer I never worried about it. It just happened garbage collection and that that's the nice thing about Java. You don't need to be a GVM expert to use Java fully. But as soon as you realize that this is a very interesting topic and you see that there is a lot of evolutions ongoing in OpenJDK with this garbage collectors. like between 21 and 25 we had the generational Shenandoa.

**[15:51]** Can you explain both words? So Shenandoa is a garbage collector. I'm not completely 100% into all the details about the different garbage collectors because there are several right. Mhm.

**[16:03]** We have the is it the set or the C1 which is like Oracle's official it's a Oracle's official concurrent garbage collector and I think Shannonoa is becoming the OpenJDK's version of that or like alternative to set one or C1 [gasps]

**[16:27]** And basically as far as I understand is like with the concurrent garbage collectors is that before they had to kind of or like earlier they had to kind of stop the world and then garbage collect and then they could start the world again and that means stopping the world means stopping your program.

**[16:46]** Yeah.

**[16:46]** Yeah. Now what they can do is they stop your program for a short while. They do a little bit of marking or a little bit of internal bookkeeping. Then they can start in the background a concurrent garbage collection process which is then figuring out in the background what needs to be garbage collected and when it's ready it can stop I think it can stop your program again for a little while clean up and then your program continues running. So a lot of the garbage collection that before would would take execution time from your program is now happening or can happen in the background when you're using these concurrent garbage collectors. Yeah.

**[17:26]** And the generational is yeah that

**[17:29]** The different concept.

**[17:30]** Yeah. Yeah. The objects which are in memory what is it that most objects die very young. So those are variables within a method. So they're only used within that method. So they can be cleaned up very easily. And the generational memory is then yeah objects which are kept in your application for a longer time and they move through different spaces of the garbage of the memory space. [laughter] But yeah my colleague Gunald, he has done some amazing talks about all these different I will add a link to the show notes of this podcast. But indeed yeah you have all these different garbage collectors. you have then specific ones like Azul has also the C4 garbage collector in Zing runtime.

**[18:18]** It's an amazing piece of technology and it's indeed as a Java developer you don't need to care. It just happens and the defaults will make sure that your application runs as smooth as possible with the shortest amount of time for stop the world pauses if they are needed even there are even use cases that you don't care if you're processing a batch file in the background again with the script for instance it just needs to run for one time you don't mind if it stops for 2 seconds then you can pick another garbage collector which makes your application run faster but has these stop the world pauses but you don't mind. It's unbelievable what happens under the hoods of GVM.

**[19:05]** Yeah, there is really a lot going on and you're right you're right that there is this there is a little little performance penalty for using a concurrent garbage collector. But what you're getting instead is a lot lower maximum latency, right? like your garbage collection processes are never going to be very long,

**[19:26]** But maybe your throughput might be slightly lower. But like you say, it depends on the situation.

**[19:33]** Sometimes that's fine, sometimes it's not. And you like you say if you are doing batch processing, yeah,

**[19:39]** You can choose the old

**[19:41]** Yeah.

**[19:41]** Garbage collector if you want.

**[19:44]** Okay. U next topic. so you have these OpenJDK projects. So these are evolutions within OPJDK which run over a longer time and then from time to time new features get introduced. So one of those projects is project loom. It's about virtual threats and structured concurrency. let's start with a quote from Anton Aribof. Loom is probably one of the one of the most prominent or impactful updates in this that has happened in this period because if let's say now we have this in platform right we have virtual threats in the platform although the story is not complete without the structured concurrency and everything like this but now with the recent improve improvements. If the framework like Spring incorporates this feature into the supported whatever mechanics framework

**[20:50]** You get the improvement from the platform for free

**[20:55]** Like the need for the other tools let's say like reactive frameworks or even cotlin cor routines

**[21:05]** Even though they solve slightly different problem is pushed further away. Yeah. So you need it later and actually you get a lot of improvements just for free. Of course you need to stay up up to date. You have to update the JDK the framework and so on. But I feel like naturally I feel it's less of a hassle than trying to solve it like

**[21:34]** With clutches and

**[21:35]** Libraries

**[21:36]** Libraries and everything like that. Yeah. So I think Loom is one of those that is very interesting. There's a lot of interesting updates in the platform in the upcoming releases or something that is a work in progress like Walhalla the outcomes of Walhalla. It has been a long long project but now suddenly because of Valhalla now safety is also coming

**[22:02]** Is also like coming

**[22:04]** At least on the let's say it's it's in the air it's in the air somewhere so now safety is important feature of cotling of course probably the single most important feature there is and now if we have that in Java it's also like a huge improvement for the platform there are some like small other updates. I'm not so like keen on records and everything that is related to the pattern matching. nice nice syntactic feature definitely needed for data oriented programming as we call it right now. But this is not a huge game changer in my mind like when and when I when I'm thinking about the platform and what the platform can enable in the future like with the recent changes around AI ML these kind of things.

**[23:01]** Yeah.

**[23:03]** Yeah. Anon mentions a lot here. project Walhalla is for one of the next versions. We'll see when that arrives. but the main topic here was the virtual threats. Did this change a lot of how you implement code or develop architectures? Most of the time you when we develop like services or like big applications the threading is handled by a platform such as spring boot or in our case we are using bacheco which is a fork of a the scala scala platform.

**[23:44]** Mhm.

**[23:45]** With the actor model. So a lot of the threading is handled behind the scenes by the by those platforms. However, we do have some level of compute that you know that is happening within the application and where we have to manage threats ourselves or where we do manage threats ourselves because we can we need to or we want to parallelize the workloads and I think virtual threads they don't really change much if if you're like if what you are doing in these workloads is mostly compute. But as soon as you start doing IO [clears throat] such as you know calls to rest services or

**[24:32]** Database

**[24:33]** Yeah databases sending messages to [gasps] to like an external message broker all these kind of things then virtual threads starts really helping a lot because it just yeah get we can use the CPU resources a lot more efficiently that way and also they don't take up as much they don't take up as much memory per per thread. So we can allow ourselves to use a lot of them.

**[24:59]** Again, we don't need to care about it because it's not the GVM who manages it, but the framework that you're using. So if you're a Spring user, a lot of cases it falls back to these virtual threads where they are applicable and it's managed by the framework, but you just see that because this happens your application can handle more load, can handle more messages, responds faster. So again, you're benefiting from running on a newer version of the runtime

**[25:31]** And a new version of the framework. You have to upgrade. That's that's for sure. Definitely. And also I am working a little bit on a like on a hobby project from time to time and in one of those parts I need to do some like to implement some servers. It won't be using HTTP. So it will be using some some other protocol. That's why I'm doing it myself

**[25:51]** Like hand implementing it. And I just did some experimentation with the virtual threads and I realized that I can probably implement this whole system like the server part much much simpler with virtual threads than trying to do it in IO. I think I can use virtual threads and blocking IO and instead of using NIO which is a lot more complicated to handle. there are a lot more details you need to handle yourself in N IO before before before your server becomes stable and robust. So

**[26:26]** Yeah yeah

**[26:27]** A part of that project loom includes virtual threads and structured concurrency and structured concurrency is about enabling you to more easily express an like a computation graph that can be parallelized. So like you say, I want to start here with this little operation. Then I want to fork that into three operations. When these two are finished, I want to start another one. And when this one down here is finished, and this one over here is finished, then I want to finish with something else here. this kind of parallelizable and dependent graph of little jobs to execute I think is what structured concurrency is supposed to help you

**[27:12]** Make easier to express right because if you have to write this today with futures and like callable completable futures and these kind of things it gets a little bit messy but I mean you can do it of course but

**[27:25]** Yeah it can probably get a little bit elegant.

**[27:30]** Yeah. It also want to help you if one of these tasks in between fails that you can immediately stop stop the others and say yeah I cannot finish this job because

**[27:41]** The whole graph cannot be completed because one of the node or one of the tasks in the graph has failed. Yeah,

**[27:47]** That's a good point.

**[27:48]** And this structures concurrency it was Jeep 505 in Java 25. I had to look it up. and it is in the fifth preview. So again, it means if you really need this, if you have a use case for this, you can use it. It's a preview feature that means you have to enable preview features and have a bit of a risk that the API can still change before it becomes a finalized version or a finalized API, but it is there. So you can use it because it's a preview feature. That also means it is inside OpenJDK. It is well tested. It is documented. it is actually ready to be used but they didn't finalize it yet because they're waiting for feedback from the community. Is this actually what you need? So people who have a use case like this please try it out.

**[28:39]** Correct.

**[28:40]** Yeah. Yeah. Yeah. Exactly. Exactly. And [clears throat] people who are doing like especially like heavy heavy calculations like sometimes like in the pension industry we have to calculate like such a prognosis or what do you call like like a projection into the future like how you know how much money will you approximately have when you retire. These are can be very heavy calculations and the laws have changed recently. So you have to like calculate maybe 10 different scenarios. So I mean in these cases you really want to kind of like parallelize these kinds of calculations and take advantage of every bit of

**[29:16]** Computing resource you have in your in available in the computer right so

**[29:21]** I can imagine everyone working in tax calculations can also benefit from this

**[29:25]** Yeah exactly [laughter] it's the same kind of thing right

**[29:28]** Yeah if I think of I live in Belgium I think of all the Belgian rules and [laughter] and corner cases I think you will have a lot of structured concurrency there. yeah, it's it's one of those yeah, nice things coming to OpenJDK. on Foojay, we have a podcast with every new release. So 26 will soon be announced and will then handle all the changes in 26. So people who want to know what changed from version to version can also look back into the previous Foojay podcasts. yeah, people sometimes say that Java doesn't evolve fast enough. Maybe they're right. I don't know. What you see is if something gets introduced like virtual threads that it's very stable, well thought out and because there were several incubator preview features that people could already try out in earlier versions.

**[30:23]** It's also based on feedback from actual users. This is what we want, what we need or can you maybe change this and then sometimes it happens in the next version. So that's also why I love how Java evolves

**[30:37]** With a new 6 month release cadence or it's not so new anymore but like once they switch to the 6 month release cadence

**[30:47]** Java feels a lot more alive right we are getting okay maybe we're not getting 10,000 new features per release but we are getting new releases and we know exactly when we get them it's it's a lot more predictable something is coming it's coming soon every two years we need to plan you know actually in the beginning it was every 3 years with the long-term

**[31:06]** Support versions right now it's every two years you kind of know already kind of need to plan around that in two years new LTS we need to look at what's what has come in and see if some of it is something we can use and if not we still upgrade because we still get all the security fixes the

**[31:25]** Performance and stability fixes as well right

**[31:28]** Yeah yeah maybe we need to remind so I mentioned Java 26 is coming in few weeks months. that's a short-term version. So that means it will only be maintained for 6 months but then we have 27 and then we have to wait for what is 29 will be the next LTS I think. So then we can

**[31:47]** Yeah to the next one. so yeah you have all these this changes in between and normally companies are advised to stick to the LTSS unless they really want a feature which appears in one of those shortterm releases like I know that the vectors is one thing that people are waiting for vector improvements. We'll see when that arrives and maybe they have to then switch to an STS version. Okay. next topic is project lighten. I don't have a quote about that but it's project leen is about can we improve the GVM to start faster use less memory I think that's also a part of it but what we've seen in Java 21 to2 if that there were improvements for ahead of time can you explain a bit what's happening there it's all about startup time like you want the JVM to start up fast and the Reason you want that is for instance if you want to be you want to use Java for serverless services you want them you know the time from a request arrives at your service your serverless service you know the time it takes to start up your service and activate it and

**[33:07]** And yeah execute the you know the request and process it and send a response back. You want that startup time to be as short as possible. And sometimes sometimes if you have a very large application just loading all the classes can take a lot of time. There are several things they're trying to do in this area. So one of them is to I know that

**[33:28]** AWS has their own take on this.

**[33:32]** Yeah snap start. I think that's snap start is the name.

**[33:35]** Snap. Exactly. That's the name. Snap start. But basically what they're doing is they start up the application. They figure out everything that is in memory at the time.

**[33:43]** Mhm. They take a snapshot of that. They break that up into a lot of small parts and then they write that out to a lot of different discs so that they when a request arrives they can load all these different parts from all these different discs in parallel very fast into memory. You assemble your application again and send the request to it. So this is what AWS is doing. But I think as far as I remember ASU has something similar, right?

**[34:10]** Yeah. Azul Azul has has ready now and it it's it's a bit similar ready now is already exists for a long time you create a file during runtime with every decision of the compiler

**[34:22]** Why did I compile this method into this native code

**[34:26]** And when you restart it you can do the read that file again we also have crack that's an open that's a great project open J crack is another thing I think it was the snap starting was even based a bit on crack is that you yeah dump the state of the program like you just dump the state of your system when you do put it to sleep and then just restart from memory. It's it's it's nice to see that projects within companies like Azul but also in open source influence how OpenJDK evolves.

**[35:00]** Exactly. Yeah. the things that have been proven in the field as this is something which is actually needed in OpenJDK it's not there let's now bring it to OpenJDK and this project light and then ahead of time is one of the ideas is that during your build time you actually run your application and then some decisions on which classes need to be loaded is stored within the jar file so that it can use this info to start up faster. So it already knows I need to do this and this or it has already been done. It's so but yeah it's an ongoing project and it's all influenced by the cloud like you say you want if you have a scaling system you want your new scaled services to start as fast as possible and this is something new if you compare it to the history of Java. Yeah,

**[35:59]** Java started yet almost 30 years ago. So it's it's cloud didn't exist like AI didn't exist two years ago. Serless

**[36:10]** Serverless definitely didn't exist at that time. Right.

**[36:12]** And it's nice to see that all these other types of how you use applications or run your applications, deploy them are now reflected in how the OpenJDK evolves.

**[36:24]** Yeah. And I think maybe we can even benefit from these faster startup times when we are doing scripting, right? Because then the JVM is just very fast at compiling and running your application or like your tiny little script, right?

**[36:38]** Instead of you having to wait for a 50 megabyte Java virtual machine to start up. But I mean they start up pretty fast, but it of course it can always get better, right? M there's another thing like with the ahead of time like we're talking about here the these ahead of time features like one thing is the startup time but the other thing is just getting faster to an optimized performance right like normally when you start up an application in Java it has to run for a certain amount of time before the hotspot the yeah the GP compiler has has realized which parts of your application need to be optimized and which don't. This doesn't really work very well for again for serverless because serverless starts up it starts up it runs maybe one request and who knows when the next request is coming.

**[37:29]** Mhm. So in order to get the maximum performance out of that they like they say they run the application they run it for a while they then write some profiling data down and then that profiling data can also be used the next time the application starts up to for the JVM to know immediately okay I need to optimize this and this and this over here is not necessary right

**[37:52]** So it can load it very fast

**[37:54]** That's again one of those stories I was not fully aware of as just a being a Java developer for many years is again what happens with your Java code the Java code that you write is not the thing that's being executed this is the class file in between so that's the bite code that's that's the first thing

**[38:14]** And then as you mentioned when your application runs you have this hotspots this code has been executed five 5,000 times it's time to recompile it to native code because you're running on a Mac with an ARM processor or because you're running in the cloud on Amazon or on a graviton then this class file becomes this native code which can do the same thing but a lot better and faster.

**[38:41]** Yeah. And that's again one of those

**[38:45]** Who invented this, who created this, who maintained this? There are so many different I run here on I have a Mac, I have a Windows, I have a Linux, I have Raspberry Pies with different types of processors. I now have Risk V which is new type of processor and again Java runs. So who are all these people making this? probably some of the people who also doing all these LLVM [laughter]

**[39:10]** Cross cross architecture compilation tools right so

**[39:14]** Yeah but it's very it's very interesting and one of the things that is coming a little bit in the future is the project Babylon who you know with Juan Fumero is

**[39:23]** Is part of that team you know from VM and

**[39:27]** That's one of the projects I'm really looking forward to because that's when we can get Java code to run both on all of the CPUs that you have in your machine. Of course, we can already do that with threads, but then they can also run it on, you know, the onboard GPU.

**[39:45]** Mhm.

**[39:45]** And if you have an extra external GPU, like many laptops, they have both the onboard GPU and an extra external GPU, it can run on the external GPU and probably, who knows, maybe in the future also on FPGAs like tornado VM can. But this is really going to make Java a completely different beast right and then again we come so project Babylon I don't know how long it's going on I think it's already 8 years to VM also an old project it's it has maturity

**[40:15]** And then you see now suddenly they become very relevant because we have all this AI model training a lot of calculations KPUs are very good in calculations so we should really offload loads some of our methods, functionality, things which are happening in our application are better handled on this kind of architectures on this kind of systems and it's really nice to see that Babylon VM really fits in that spot.

**[40:47]** Yeah, it does. And I think there's another thing that I've been thinking about and that is sometimes we know when we deploy services to the cloud we are deploying in maybe in an instance that only has like two virtual cores and no GPU.

**[41:04]** So in that kind of setting we are not actually getting that much out of all the new Java features because you can parallelize all you want but if you only have two cores it's it's not going to it's not going to help very much. Right.

**[41:16]** But a lot of our personal computers, they now also come with a lot of cores. They come with iGPUs, they come with NPUs, they come with sometimes two graphics cards, right? Like they have both the internal and the external

**[41:30]** Card. And when you look at it like that and you see that Java is now becoming more and more able to use that, I have a feeling that Java on the desktop is getting a lot more interesting than it has been for a lot of years, especially combined with something like Java effect that is already using some level of

**[41:50]** Of GPU or hardware acceleration in the background for generating the UI.

**[41:55]** Mhm. So in the cloud you can kind of decide exactly which resources you have. So

**[42:01]** Maybe you don't need to have you know maybe you don't benefit so much from the Java VM automatically realizing what you have in your system and using it. But on a personal computer, that's a completely different game, right? You want run something on a big laptop, you take the same program, you run it on a small SPC, you know, like Raspberry Pi or whatever.

**[42:22]** And the Java VM will in the future be able to just kind of use everything in your chips, right? In your chipset

**[42:31]** And not just the CPU. And I think that's very very exciting to see where that lands.

**[42:37]** But we have to be honest, it's not in 25. So people who are on Java 25. So maybe when the future yeah maybe when we repeat this podcast in two years and we are talking about 29 maybe we will have some of these in there but there are things ongoing and if you want to try them out you can either already try to VM or I'm not sure about project Babylon if there are many preview features in Java 25 already but they will surely come. Maybe you can get Juan back on the podcast one time and then see if he's allowed to talk about it.

**[43:12]** But I interviewed the Tornado VM team at Devox. They are in one of the earlier podcast. So I will add the link again in the show notes. I don't have a quote now but a longer interview with them is available. Okay. next point is the class file API and I have a quote from Ronald Dehoser and I was also at Devox. I'm going to say something that nobody probably mentioned before but for us it's going to be and I don't know whether it's already in 25 I think it is to be honest but it's the chap where ESM the library for bite code reading is now natively part of the GDK in fact so it mean that where in the past we had a dependency on ASM to find out what is going on exactly when you pass us a lambda that needs to be scheduled, we could now use it do it with standard Java 25.

**[44:12]** Are there a lot of changes in that topic within Java is how you can read class files and other files related to the program itself. I think that's I that's the biggest that I know of and it's and it's huge because you know beforehand we always had to use ASM which is a great library but which showed its age also and to have like a nice built-in way in the JDK itself is really really cool. Rod is a creator of JobRunner. So that's a library to schedule jobs and he really needs to be able to look into class files and load class files when he needs them. and [snorts] again he mentions something which we already said before he was using a library which can now be replaced by a feature which is in OpenG which is in the GVM which is in the Java language. So the class file API is that something you use that you know or that you can take advantage of?

**[45:13]** Not immediately but I mean both at work we have a we have our own programming language or the company has its own programming language that is implemented and interpreted in Java.

**[45:27]** Mhm. And it's [clears throat] usually something that you know that is loaded at runtime at startup time and then it is turned into like an opic graph that is that can then be executed. But if we at some point need more performance there it would be a possibility then to take this dynamically interpreted object graph and turn it into a Java class which then can run faster, right? so that's that's definitely something that we might be able to use u where I work. It's it's not the area that I'm sitting in, but I know the guys who are working on it. And we have talked a little bit about that possibility. It's probably not something we're going to do in the near future, but it's a possibility.

**[46:12]** It's definitely a possibility

**[46:14]** And is there to use that

**[46:15]** Indefinitely and I'm also like in one of the my little projects at home I am also implementing a little programming language and this is also something that is intended to be like interpreted but if you define a function and you expect to be calling it a lot of times then maybe it would make sense to you know compile that function into a complete Java class so it runs faster. But

**[46:44]** We'll see. I think it's definitely for like these kind of these kind of use cases where you have something that is kind of dynamic and you have your application which is more static once it runs and you want to combine them. So you want to put the dynamic stuff into your application when it when the application starts up. But you don't want this to run slow just because it's being interpreted. Then you can compile it into classes at runtime and then you can gain the speed. I think that makes a lot of sense and I think there are plenty of use cases out there that

**[47:15]** We just maybe haven't thought about yet. I mean

**[47:18]** The people who are already using these as and all these kind of older toolkits they already know what their use cases are. So there plenty of people who already need this right

**[47:30]** I think it's it's also a big part of what Spring is doing is spring boot loading classes at runtime configurations and stuff like that. So again it will be handled by the framework. The frameworks get improved. They run faster. They start faster. So everything is actually connected. And there were some of the early of these OM frameworks that used the bite code manipulation as well. Right? So they were also like looking at a configur configuration of a class and then or like a table and then from that at runtime generating some

**[48:05]** Some classes and making changes to the class files and such tools could probably also benefit from

**[48:12]** Yeah this kind of library related to bite code that's one of those other hot topics that I never cared about as a Java developer is actually how much info for is in a jar file in byte code and I had a wonderful conversation with Matt Coley who developed recap and recap is a tool to look into byte code and again we'll add a link to the show notes he explained to me what you can find in bite code and what I didn't expect from that interview is that he also ex showed me what you how you can be a mislet with what's inside the jar file. So he ex executes a jar file which writes hello world and if you look into the sources there's something completely different. So how you can he's he's on the side of preventing hacks and what can be inside the jar file and how people can abuse it and or misuse it. It's actually very fascinating the whole bite codes history and what's inside a jar file. So again something [clears throat] interesting for people who want to know more. [snorts]

**[49:21]** One of my you have a lot of pet projects. Mine is Java on Raspberry Pi and the Pi4J library. I didn't implement it myself but we are working on a version 4 hoping to release it soon. which is completely based on the foreign function and memory API. This is something arrived in Java 22. So between 21 and 25. So it's a way to replace JNI which was inside Java since 1.1 of the very first versions. Very complex to use and now we have FFM. I've talked about it at a few conferences. it makes it a lot easier to interact from Java directly with memory. So outside of the garbage collector and with native libraries do you see use cases in what you are doing there? Most of what we are doing is staying within within the Java virtual machine. but I can imagine that I can imagine this is very useful for like if you want to use like AI libraries or something like that are implemented in C++ and you want to use them or call them from inside of your from inside of your Java applications.

**[50:44]** Mhm. I like that you again mentioned the AI because again this FFM so it started in Java 14. So AI was not in the picture really and again you see that something which is now recently finished perfectly matches with I want to use in an easier way libraries which were written for AI LLM processing text processing on the Raspberry Pi is just to yeah interact with the GPIOs and toggle some lets and more complex stuff but there are really a lot of use cases in the AI space for instance

**[51:20]** Yeah in the AI space And I also think I like for other things that are normally more CPU heavy such as let's say compression and decompression or encryption and de and you know encryption and decryption. Maybe maybe there's something to gain from using a library you know that is written in some native code because after all you're just asking it to take this data and give me you know the decompressed or the decrypted code and you don't need to interact much with it but it just needs to do this big job give me the result and yeah maybe maybe these kinds of or like encoding and decoding of video and audio maybe these kinds of use cases can then make your Java application faster.

**[52:08]** Mhm.

**[52:09]** Because it's not only Java anymore, but I introduced someone who had a history in mathematics and was now working on accountancy financial products with Java and he indeed mentioned I know of this very good C library to do very complex calculations. not something I want to reimplement in Java because I know it's there but and now he can very easily call these functions with the FFM API. I spoke about this in the Java Champions conference recently. So again a show note I can add I have in my presentation the most complex method to get the length of a string in Java is not by using Java string length but by using a native library. it's just a fun way of showing how you can put a string directly in memory then give that memory address to a native library and get the length of the string. It's it's 10 lines of code. It's much it's much to get the length of a string, but it's it shows you how with a few lines of code, you can directly put something in memory, give that location in memory to a completely different application and get that result from that. And I

**[53:24]** Really love how this is now possible with something which was introduced in Java 22. So people who are using this kind of functionality can benefit from it if they jump to 25. Earlier I kind of hinted that it was mostly for performance but it's not I mean sometimes you don't have you don't have that API in Java at all. Mhm. Mhm.

**[53:49]** It only exists in another language and in that case you don't really have a choice right you need to use that

**[53:55]** That API in that you know native in that native code and I think in those cases is really nice you know that Java can then integrate with all these other languages you know just like in grow is one of the things they are looking a lot at right but yeah so it's it's it gives us the possibility of integrating with you know with APIs that we don't have available in our own ecosystem. So this is very cool I think.

**[54:26]** Okay. I have one last quote and that's Jonathan Ellis and Ryan Chilva and it's about the vector API.

**[54:35]** Oh man like so for Brock we are a fairly straightforward like we're we're wrapping rest calls to LLMs right? So it's not doing anything super computational or anything super native oriented. but my last project before Brock was building J vector where we definitely took advantage of the SIMD support with the auto vectorization and the improved foreign function API. So I've definitely seen the advantages that brings. Not using them in Brock yet. but we may have some native code in our future. Yeah, I think we have some batch jobs coming up pretty soon. So I would say any day now. So the getting listen, I've used Growl quite a bit and we actually heard it in one of the talks today and I love the Growl project, but compile time from building a native thing can sometimes be a real pain point. So getting that out of the way and getting good enough startup times. Very excited about that to the KVM to run everything.

**[55:35]** Yeah. No, I'm very excited about that. So Brock is a tool related to AI. Not going to talk into details about that but so they are really combining. So they talked about foreign function and memory API. They talked about graph VM ahead of time. and then the vector API it's not finished in Java 25. I think we are on the 12th incubated [laughter] chap. So but as he mentioned

**[56:05]** In J vector which is a vector database they use that preview feature with the new vector API because they really need it. So although it's not stable it's it's it's stable it's not finalized yet. So although it's not finalized yet they decided we're going to use this preview feature because we really need it. this vector API again hot topic AI machine learning language models they use a lot of vectors. Do you think this will be an important change in OpenJDK? It's hard to say if the vector API itself will be the version of vectorization that will win because we also have project Babylon as we talked about earlier coming in the future. But the idea of vectorization meaning you can work you know execute the same instruction on many different memory locations at the same time in a vector of data points. Yeah, that is something that is going to be or is already it's already used internally in the Java virtual machine through auto vectorization. But the fact that we can now do it ourselves explicitly means that we can squeeze more performance out of the CPUs

**[57:28]** That we have available, right? And most CPUs, they already have some some level of these kind of SIMD or these vector instructions available within the CPUs as it is today.

**[57:40]** Mhm. What remains to be seen is if vectorization or like executing vector instructions on the CPUs will be able to compete with executing the same kind of logic on the GPU that comes you know with your CPU on an iGPU or

**[57:56]** Like a I think that we don't know yet. so may maybe what happens in the future is do you prefer to just use the API that is in something like tornado tornado VM or tornado VM or like in project Babylon and then it will take care of vector you know running or compiling it to

**[58:18]** To the vector instructions of your CPU or your GPU or whatever. So this whole space is definitely like very very interesting. and the fact that we get the instructions available for us in the vector API is a big step forward because I've seen also like not in Java but I know in I think it is in C they have a they have a JSON passer which is using these vector SIMD instructions and it the performance is like two three times faster than just using normal CPU instructions. So there is something to be gained from this in a in probably in more use cases than we know of and once we get the API and it's available for us we will start playing with it and we will start realizing how to write algorithms for these kinds of instructions. Right.

**[59:09]** Mhm. And the fact that the vector API is already in preview for such a long time is as we mentioned before they new features don't get introduced in Java just because someone wants to have them. They get introduced when they are stable when they're well thought of when they solve a problem which existed but also can fix a problem which is being worked on. And I think that's the main thing with the vector API. They want to use it for some other improvements and as long as they are not finalized, they're not going to finalize the vector API. So again, the architects of OpenJDK are really the persons looking after the whole thing.

**[59:56]** Yeah.

**[59:57]** Are we evolving in the right direction?

**[59:59]** Yes. And I think they even removed a feature recently, right? I think was it string templates that templates? Yeah. Yeah.

**[1:00:07]** It was introduced in kind of a

**[1:00:09]** Also in the preview I think

**[1:00:11]** Preview and then it was taken out again because something in it was not [gasps] working as they wanted it to work. So they said okay this is not the this is not the way to do it. So they took it out again and this so yeah use preview features at your own peril right [laughter]

**[1:00:27]** At your own risk. Yeah. But I can imagine like the vector API, we have tw 12 preview features or 13, I cannot even remember. they're not going to remove it. [laughter]

**[1:00:38]** No, no, no. I mean, we know we want this API in there, right? So this the like if you take a step back and you look at the big themes of the where the JV VM is going, it's definitely parallelization, right? So both on the CPUs

**[1:00:52]** Mhm.

**[1:00:53]** But also on the GPUs and like yeah in at instruction level at CPU core level and also at GPU level. So we want these things in there. This is not just this is just not not just something that is nice to have. Yeah.

**[1:01:08]** Yeah. Yeah. And you really link them together. So you have project Leiden Babylon the vector API it's in project Panama. So they are all different projects but they are all inside OpenJDK. They are all related.

**[1:01:25]** Yeah.

**[1:01:25]** And one cannot exist without the other.

**[1:01:28]** No. And it's it's all related to getting the Java virtual machine to be you know execute your code faster.

**[1:01:35]** So yeah start up faster profiling and like get to a like a well compiled version faster. Use auto vectorization. enable you to use vectorization yourself and

**[1:01:51]** Yeah the process garbage yeah and the garbage collection as fast as possible.

**[1:01:57]** Exactly. Yeah. And with you know with the FF the FFM APIs like the foreign functional memory API like being able to call out to something that is faster than Java can do when you need it. So yeah,

**[1:02:10]** Getting to the fastest executed code possible in every use case in every environment even because yeah we have cloud we have desktop we have embedded

**[1:02:22]** Being able to run on all these these environments is really a critical thing. Yeah. Yeah. And yeah and Java is probably not so big on the desktop at the moment but that's kind of where Java started. And yeah, it's I kind of have a feeling that it can run well on the desktop these days, right? At least the things that I'm starting up on the desktop, they actually feel they feel very snappy. It's not it's not like, you know, the old swing kind of laggy laggy UIs from like 15 years ago, right? It's it's it feels like a native app. I mean, it doesn't maybe not it doesn't look like a native app exactly, but it definitely feels like it most of the time now. And with Java Vix, you can style it in whatever you want to.

**[1:03:05]** Exactly. Okay. Yeah. Styling is your choice, right?

**[1:03:08]** Mhm.

**[1:03:08]** I want to thank you for your time and your insights in what has happened between 21 and 25 and being my guest again in the podcast.

**[1:03:17]** Yeah, it was a pleasure. Thank you for Thank you for allowing me on again.

**[1:03:23]** It was pleasure was all mine. thank you and hope to see you in one of the next podcasts. to you my listeners also. Thank you for listening. please subscribe to Foojay on social media on YouTube in Spotify wherever that you listen or watch this podcast and follow us on Foojay.io for more news and changes related to OpenJDK and the Java world. Thanks for listening.

**[1:03:48]** Give me a give me a OpenJDK.
