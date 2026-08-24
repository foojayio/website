**[0:00]** In this podcast, I serve you OpenJDK evolutions and tips and tricks from Devox and Jfall.

**[0:05]** Give me a J. Give me the OpenJDK.

**[0:12]** Hello and welcome to another episode of the Foojay podcast. Just like in the previous episode, I bring you conversations from two of Europe's premier Java conferences, Devox in Belgium and JFall in the Netherlands. At these conferences, I had the opportunity to speak with members of the Java community about topics ranging from the evolution of Java itself to mobile development, performance optimization, and even automotive security. My first guest is Johan Vos, a Java champion who takes us on a journey through Java's history from porting Java to Linux in 1995 to his current work on bringing Java and Java vix to mobile and embedded devices through the Java on mobile project. Then we'll hear from Stefan Chin, author of the definitive guide to modern Java clients with Java AIX, who shares insights on building cross-platform client applications and reflects on how his daughter has followed in his footsteps to become a published author and technology educator from JFall. Joseph Phillips joins us to discuss Java's evolution, the differences between REST and gRPC, and whether virtual threads have replaced

**[1:23]** The need for async implementations in modern Java applications. Next, Franuis Marte walks us through the world of Java performance benchmarking with JH, the Java microbenchmark harness, and explains why it's so valuable for comparing different implementations and optimizing code. Voter Dus shares his inspiring journey from finance and mathematics into Java development and how his employer, the Dutch Tax Authority, supports open-source contributions and the Java community. And finally, Roalt Nefs demonstrates something truly unique using Java and the foreign function and memory API to hack into automotive systems, revealing important security considerations for both hardware and software. Let's get started. I'm Johos and why am I a defox?

**[2:14]** Well, I'm part of the steering committee so I have to be here anyhow. But I'm also interested in Java in general and innovation in Java in particular. And then Devox is the place to be.

**[2:26]** And you have a very long history in Java. Actually me and Java we go back until from 1995 or so when Java was created and it did not exist on Linux and I was a big u Linux fan and I wanted to use Java for my PhD but because there was no Java on Linux we decided with a group of people the blackown team to port Java to Linux and that's how I got started with Java.

**[2:51]** You just fixed it. It was not there and

**[2:54]** It wasn't there. It wasn't easy to fix but it was I believe the right thing to do even though many people did not believe in it at that time. son micros systemystems was definitely not interested in Java on Linux but the history turned out to

**[3:07]** Agree with us

**[3:08]** Was pretty critical

**[3:10]** Right

**[3:11]** So you say it was for your PhD so you have been doing Java since you were still in school is that a bit frightening or is that just relaxing that you're still using the same things

**[3:25]** Actually I never looked at it like that but yeah You're you're probably right. Many people they do some career changes or they change technologies and so and I it's not that I'm religious about Java but Java to me is the is a tool it is a language to achieve something. So Java itself is not a goal but I think that is also one of the reasons why I really use Java. And one of the key things that I remember from what James Gling kept saying is Java is a blue color language. It is designed to help you to fix a problem to do your job. It is not designed to be awarded as the most hip language or the most cool language. It won't get a price for that. But that the goal of Java is to help you to do whatever you want to do and the whatever you want to do can change of course but I think Java is always a good tool set a good choice a good way of programming.

**[4:25]** Yeah. I tend to say that Java is boring, but boring in a good way. The Java tomorrow will be able to do the same things as it's able to do today. Your system will not be broken tomorrow because you're running on Java and something changed somewhere by someone else.

**[4:42]** Exactly. And that has advantages and disadvantages. So I think let's start with the disadvantage. So many people well or people on social media often complain that Java is slow in adopting new things. There are other languages that are much faster in using new paradigms, new concepts and so and then many people say hey why isn't why doesn't this exist yet in Java? But those things are often created in languages that are maybe two three years old and that won't survive for two or three more years. Whereas in Java, which is now about 30 years old and still wants to survive for at least another 30 years, things are different.

**[5:28]** Yeah. And one of the reasons why Java is so old already, why Java survived for so long is that the people developing the JDK are not under pressure to take the latest great technologies, but they have to make sure that whatever is out there and whatever is going to really be released is of top quality. So the quality is much more important than than being hip and that is that often gives Java a dull image because it won't be the first to come out with a new concept but it will be the one when it comes out with a concept with an implementation of a new concept. It's extremely well tested. It's it's been tried in many different use use cases and especially very very intelligent people, very smart people. The OpenJDK team is an incredible team and those really think about how can we maintain this in the next 5 10 15 years because that is the key question. If if you have a critical project and you do that in Java,

**[6:37]** You want that it's that the project is still working in yeah in the future.

**[6:43]** Yeah. And it's by coincidence that already running projects for a long time like Panama seem to now be used or usable for AI development. So it's not that was designed to be able to do that like the FFM API and then the memory access and stuff like that running things on the GPU. They all now come at the right time. They are almost finished or they are finished and they are now available to be used for exactly what a lot of developers need. true true it's it's it's it's it's indeed good that they are available now because otherwise there would be u Java would yeah have a delay and a gap on other languages but the work on Panama started very very long time ago because today yeah AI that's really interested in using u more foreign functions and foreign memory but in the past and in the future it are different things there's always been the need for integrating Java with native languages and native memory and the fact that it was always possible in Java but with a price CPU GPU copy and so all took time and so but there's always been a need

**[7:56]** For doing this in Java and if the JDK team had rushed this and it would have been there in the JDK 5 years ago then it would probably be less good than it is now. So we are really lucky that there was no rush in delivering Panama and today it's the some parts of Panama are in G and those are really first class Java components and ready to use.

**[8:24]** Mhm. you're one of the main maintainers of open of Java vix of open JVIX I always get confused with the naming. you're also working on Java on mobile. Can you give a bit of details of what's happening there or what is the idea behind this?

**[8:42]** Yeah, the idea behind it I think it's something that I've been interested and something that intrigued me about Java from day one is that the right ones run anywhere. As a developer, you don't really care about the underlying operating system and it's a it's more a burden than a help. you want to write your code and then you want to run it on all people's devices and well especially I'm not I'm not a computer scientist. I come from I'm an engineer and typically engineers just want things to work

**[9:19]** Fix problems

**[9:20]** And yeah they want to fix problems and it has to work and then if you write something and it works on someone's Linux machine but then someone comes with a window and says hey this doesn't work for me then that's a bummer. So the crossplatform and the right ones weren't any aware of Java is always extremely important and even in the beginning in the early days of Java one of the goals was to make sure that Java works on the internet of things and on the on network devices on mobile phones PDAs set boxes and so that's always been one of the goals because there are so many people using those devices. So as a Java developer, you want your applications to work on those as well. Now Java took a detour into enterprise which turned out to be extremely lucrative from a financial point. That's where the money is and follow the money then you know where the investments are going. It's in the cloud and the server development and I can understand that but there is no technical reasons. There's no technical reason why Java cannot work on consumer devices, mobile desktops,

**[10:31]** Laptops, setup boxes, Raspberry Pies and other kinds of embedded devices. So technically there's really no reason and for developers it would be really helpful if their applications work on not only in the cloud, not only on servers, but also on those devices that everyone can use cheap devices where hobbies can create great things with. So that is not a financial decision because if you just if you want to program for the money then yeah you follow you go to the cloud and the server

**[11:04]** But from a technical point mobile embedded is really a home market for Java. So I want to make sure that Java runs first class on those devices as well. And that means first of all we need to make sure that the OpenJDK runs on mobile devices. And second there need to be a good UI toolkit for developers to create compelling user interfaces for those devices. And that means that we both need OpenJDK mobile which is the JDK for mobile devices and Open JFX which is the repository where we specify the JavaFX specification and implementation. So that's why I'm working on both those projects

**[11:48]** As you brought Java to Linux one day. I can imagine that it's reachable this goal that you have a clear idea how this can be done. Well, from a technical point, it's definitely reachable. I have to admit that when I started with Java and Linux, I didn't know why I was starting. It's just I had an end goal, which is I want to run this application. By the way, I had a Spark Station running Linux, which made it even more complex. but in the end, it worked. And I had much less exper I had I basically had no experience with Java at the time because well it was new and I didn't know how hard it would be. Now with Java on mobile, we've been working on this for a long time already. Same as with Java X and the difficulty is not the technical part.

**[12:42]** There's writing the code is actually easy but the difficulties are in the tooling. for example, take iOS. If you want to create an application and deploy it to the iPhone, you need to obey all the Apple rules. you need to upload it to the app store. You need to sign it notoriization and all those things. and those those are things that Apple developers know very well or well learned to deal with it

**[13:13]** But for Java developers it's it's really yeah boring and confusing. So the tool

**[13:19]** And extremely difficult and it often changes between versions. So the hard part is not really the technical development but the everything around the whole well dealing with the Apple ecosystem and so and the other hard thing is the yeah the marketing of it because Java is positioned so clearly as an as a server side cloud enterprise language that it's a

**[13:45]** That it's really difficult to explain ironically to explain that Java also works on the devices it was actually created for

**[13:52]** Yeah and That's yeah that's my pet project is using Java in all other cases not enterprise but in home automation in on Raspberry Pies and creating fun applications just as a pet projects where it's a perfect tool.

**[14:07]** Yeah. and it works, right?

**[14:09]** But and so how comes there's not more attention for it because it works so many people should be using it. But

**[14:16]** It's still hard to get on the radar because

**[14:20]** I often talk to people and then I show them hello Java V on a on an iPhone. I say, "Oh, I didn't know that you can do this. I've been doing this for 10 years now maybe, but people don't realize it. Granted, it's also not that polished as as I wanted it as I wanted it to be. There's no full website. There's no develop rails talking about it because there's not really I mean I don't really have to be of the business model behind it. We have the technology but yeah and that's cool but we often need more

**[14:53]** And indeed yeah OpenJDK Open Javix it's an open-source project. luckily we have companies behind it who earn money out of it like Oracle, Azul, Gluon because you offer services and support and additional tools without those it wouldn't be possible to maintain the open source and then keep it evolving I guess

**[15:17]** Right yeah and that's a bit the difficult model but it's the only model I don't think it's the only model and I don't think it's the best model And actually you already described a number of models because every company has a different approach. Oracle has a different approach than for example Azul I mean both try to sell LTS support for the JDK but Azul probably also provides more time and material projects and so Oracle is more into the licensing but in both cases it's actually

**[15:54]** You need a way to pay the engineers the developers that are working on the OpenJDK features

**[16:01]** And the new features and the maintainability and so that and that costs money But in the IT industry, people take it for granted that there's an there's a JDK

**[16:13]** And it's it's like almost nobody wants to pay for it. But the companies realize that yeah, that this costs money. So where does the money comes from? And then yeah, you have Belloft, Oracle, Amazon, Microsoft, they all have their own models to fund the development. And I think it's it's a bit a pity that there's no more direct funding because the yeah the people developing the OpenJDK and open JFX and all the other open source technologies if they can be rewarded more directly instead of having to rely on a different department in their company which provides the revenues and then they are actually the cost and that's yeah I don't think that's how it should be but I'm I'm honestly not the person who can solve problem. I try to solve some technical things, but that's it.

**[17:04]** And you not only solve the technical things, you also push them forward like with OpenJDK on mobile. So, that's great. What could the community do to help here?

**[17:15]** I think there are a number of things. So first of all the community being aware of that this happens and recognizing that this is an important thing and a useful thing that's already really helpful because the more people that say oh yeah Java on mobile is relevant and Java Vix on the Raspberry Pi it works and it's cool

**[17:37]** I can confirm

**[17:38]** Definitely and it really helps that you say that and that you show it and that you write about it in a book and so spreading the word is It's an easy thing that doesn't cost too much time and it's it's really helpful because it helped the image of Java being more than just a serverside cloud language. It's it shows that Java is really suited for those kinds of things. And then of course if you have if if if you want to try out things and give feedback that's always useful as as as well. And we're fortunate to have most of the Java on mobile and Java users are very friendly and they are they do understand that we don't have the same resources the same amount of resources we don't have the funding to work on Java on mobile and on the client that companies who work on Java on the server side have. So we can't at this moment have the same speed, the same level of well we try to have the same level of quality. That's something that I don't want to give in.

**[18:45]** So that but that's also why we move forward slower because we don't have the same resources. So we do appreciate all the feedback and we also do appreciate that developers understand that we don't have the same funding as the server cloud companies. Steven Chin. I work at Neo4j, which is a graph database company.

**[19:09]** The thickest book on my bookshelf is from you. It's about JavaSix. How much are you still involved with that?

**[19:17]** So we just updated the Java effects book for Java 23, I believe.

**[19:23]** Maybe Java 24 is out now, but I mean we did

**[19:26]** 25 yet

**[19:27]** 25. We did update it fairly frequently. and it has become the definitive guide for building Java effects applications and in general client applications in Java. it's kind of funny because we're we're here at the DevOps conference and the reason why I started coming to DevOps is because I wrote the Pro JavaFX book together with Jim Weaver, Dean Iverson, Wayade Chigal, and Johan Voss. And then we're here what like 20 years later and not only is this still the definitive book on Java effects, this is still the definitive technology conference in Europe.

**[20:10]** Mhm.

**[20:11]** So it's aged well.

**[20:14]** It aged well. Did Javaix age well?

**[20:17]** Opinions.

**[20:19]** So it's basically become the way you build client applications in Java, right? I mean nobody's using AWT.

**[20:24]** Mhm. like occasionally you use swing for certain sorts of applications but if you're doing a modern application you're using Java effects

**[20:32]** Y

**[20:33]** And you know I think you could argue that a lot of people are using other languages or frameworks for doing UI development but if you already have a Java application and you're doing something crossplatform it's just a really good solution and then Yan Vos who I mentioned actually has a mobile and embedded version of Java as well with this company Gluon. so I think again like it gives you that the best technology for doing that crossplatform development for UIs.

**[21:06]** Okay. I also have to thank you for having a house full of 3D printers and Raspberry Pies because you also inspired your daughter to become an author and a Java user. how do you look at her book?

**[21:18]** Are you a bit proud?

**[21:21]** Yeah. Yeah. Yeah. So, so like I think the reason why I'm here at DevOps is obviously because you know I'm going to give a presentation and this is a great conference,

**[21:30]** But really I'm I'm the plus one for my daughter. She's she's giving the keynote, right?

**[21:37]** How does that feel?

**[21:38]** Stefan's like, "Okay, your daughter's coming. You can give a talk, too, Steve." So, it's nice to see the next generation coming up and being very successful with technology, doing amazing things. And I think we need more female role models

**[21:55]** In our industry where people can look up to aspiring developers and folks who are kind of leading technology and driving more things. So now she's she's the content share for CNCF Kids Day. She's writing books on education and teaching people how to raise their kids and raise coders and also a new book series she's going to talk about tomorrow for AI first development. So kind of how the next generation should be thinking about learning technology together with AI tools. You know, I spoke at AI for death also with the daughter of Jonathan Villa who's even younger and how she looks at AI and I think for those young people it's really a changing time with AI and how it influences how they go to school, how they learn also how they learn for a job.

**[22:50]** So I think also for them this will be a major shift.

**[22:53]** Yeah. Yeah. And schools aren't quite ready today,

**[22:56]** Right? I mean they look at AI as a tool which has a potential to be used for cheating or you know bypassing the regular curriculum

**[23:08]** But they just need to embrace it.

**[23:10]** It's how kids are learning. It's what they're the technology they're using on their own time and it's something which we should be encouraging and then guiding them towards the right uses of AI where they learn.

**[23:22]** My name is Devin Phillips. I have been a Java developer for 15 years, 17 years, something like that. and I recently moved to the Netherlands and Europe and want to get more involved in the community. So Jall seemed and NLJ Jug seemed like a very good place to start.

**[23:41]** Yeah, great community here in the Netherlands. A lot of jugs. are you a jug speaker?

**[23:47]** I have been a jug speaker. I was also a jug organizer when I lived in the United States. I ran the Java users group of greater Louisville or juggle as we called it.

**[23:59]** And while it was a small community, it was a very tight community.

**[24:04]** Yeah.

**[24:05]** And what's your topic? The main topic that you've been talking about.

**[24:08]** Oh, I talk about so many. So, probably one of my favorites of the last couple of years is contract first API development using things like open API, gRPC, async API to design an API for your end users before you implement code so you can validate it and do design thinking and have a better chance at doing domain driven design.

**[24:33]** Okay. REST gRPC, what's the main difference? What's the advantage one above the other?

**[24:41]** I don't know that there's necessarily an advantage to one over the other. it really comes down to the style of application that you're deploying. If your application is traditionally CRUD based, then REST is probably very good. But if you're worried about asynchronicity and being able to implement things like CQRS, then maybe gRPC is a better choice or Yeah.

**[25:05]** Yeah. I read a few things about async. Do we still need async implementations if we have virtual threads?

**[25:14]** That also depends on who you ask. So I've seen a lot of people doing benchmarks and performance analysis. So I was actually just reading an article recently about open liberty and they'd done extensive analysis of do we continue using our thread pools as they exist or do we switch to Java virtual threads and so far their performance analysis says that their typical traditional thread pool is better for now.

**[25:39]** Now will that change as virtual threads evolve and mature? Probably.

**[25:46]** Yeah. virtual address is one of those new things in Java.

**[25:50]** Yeah.

**[25:50]** How are you looking at the evolution of Java?

**[25:53]** Oh, it's been fantastic the last few years seeing the evolution accelerate in the Java community. So for a long time we saw reactions to the stagnation in the Java world. So, Enterprise Java was so stagnant that Spring came about and then even Spring has somewhat maybe stagnated a little bit and so you're seeing responses like Helon and Quarkus and so it's very interesting to see that we're finally getting faster evolution. Brian and the JVM team have been doing a wonderful job of carefully analyzing new features and seeing what makes sense. I know it's frustrating to a lot of people that Java moves slowly, but move fast and break things when you run most of the enterprise internet is

**[26:48]** Not a good place.

**[26:49]** Yeah. It's it's typical that you say careful

**[26:54]** And still evolving. Yeah.

**[26:56]** So, they are really taking care of backwards compatibility, being ready for future features.

**[27:03]** What's the most the thing you're looking forward to that's in the pipeline?

**[27:08]** Well, actually the things that I was most looking forward to have have already dropped. I mean, virtual threads was a big one. the ability to do records and record patterns and pattern matching. They've they've made my code so much clearer, so much easier to read, so much easier to implement, and I don't end up with these huge if then else trees that they're brilliant. Absolutely brilliant. and what in 25 they dropped the ability to do pattern matching on primitives.

**[27:38]** Yeah.

**[27:40]** So now the whole gang is there and you can do just these wonderful switch statements that are so easy to read, so easy to understand. My name is Fosa Marta and I'm here at JF because I'm a speaker and I'm giving a talk here.

**[27:54]** Okay. And the talk is about

**[27:56]** So the talk is called my code is faster than yours. Let me prove it to you. So it's essentially about showing how to do a microbenchmark with JH. I just noticed there weren't that many talks on introductions on doing the JH microbenchmarks. And I've also noticed that a lot of people are really struggling to get into using JH. Actually just yesterday at speakers dinner I met someone who mentioned that for his talk he prepared his first JH microbenchmark and I asked him how his experience was because I was kind of curious you know as a new person getting into it and he said oh it was very painful so I was thinking yeah that's exactly why I made my talk right

**[28:35]** That's what you want to solve for what is JH

**[28:38]** Is it a tool is it a library you need to add to your code how do you get started

**[28:44]** Sure yeah so J mage is I don't know I think it's something between a library and a tool I would say I mean it's a dependency you install right but it's something you use more as a tool so the way you get started I mean on one hand you can just watch my talk you know there is a recording available but I would say the easiest is to go to the read me file of the Jes project it's open source on GitHub and then you read through it and it shows you pretty much exactly how you can get started it's like a Maven archetype command you can run and it will bootstrap the project for And then from there you can write your own benchmarks.

**[29:18]** And what is the main goal of such a benchmark? Is it finding blockers within your code or proving that your code really does what it should do within a certain time?

**[29:28]** Well, it's actually kind of neither of them. I would say it's showing if you have two different implementations that do the same thing, which one of those is faster?

**[29:38]** So this is why the code the talk is called my code is faster than yours. Let me prove it to you because the idea is let's say you have two different implementations and you want to see which one is faster you could use JH for that to see you know exactly but if you want to find out where your blockers are then you would use profiling for that I always say profiling is what you do first to find your bottlenecks and then as soon as you identified your bottlenecks you're using JH to then find out which implementation is the best to choose in a scenario to make sure that you can unblock your implementation

**[30:09]** So it's not on production it's not that you analyze results afterwards. It's really a tool during your development to finding out how this gets better.

**[30:18]** Absolutely. Yes. Yeah.

**[30:20]** Could you for instance also compare different versions of the runtime if that has an impact on your performance?

**[30:27]** Yes, definitely. That's a good point. Yeah. So in the talk I also mentioned that you should make sure that you use you know the conditions that are very close to production as possible. And this is exactly for these reasons because depending on the runtime you're using or the Java version or whatever you might have differences in the performance. So this is something yeah you could keep in mind if you feel like you know this could make a difference. Try out different you know JVMs for example you know maybe try the GV VM to see if that makes it faster maybe.

**[30:55]** Yeah totally that is something you can measure as well. getting as close as possible to production. Isn't that a big problem for all test cases, integration tests? It's an ideal scenario.

**[31:08]** Yeah,

**[31:09]** You test what's running in production, but isn't that very hard to set up in a lot of cases?

**[31:14]** Well, I would say it depends if you have a more classical setup where you're deploying just on a server that you control, it's usually a lot easier. But of course with nowadays where you have Kubernetes and you deploy in a container and so on, it makes a lot more difficult to make sure that it's close to that. But still I just say try to keep it as close as you can. But of course, you know, for example, use the same JVM, you know, whatever. you know, maybe if you can get some similar hardware, maybe, you know, whatever you can do is making just the results a little bit better. But usually for most microbenchmarks that you're doing, those things don't affect results that much. It's just more if you want to really do like micro optimizations where this is especially necessary to do.

**[31:59]** Do you see a lot of advantages of moving to the latest Java versions?

**[32:04]** Well, do you mean for performance reasons or for other reason?

**[32:07]** Developer friendliness.

**[32:09]** I'd say develop per friendliness definitely. I mean, you know, there are so many nice features that we can now use. And I mean, performance, especially now with the move to Java 25, for example, with virtual threads that we can use for the first time since, you know, the last LTS, this for sure also makes a big difference. And also virtual threads of course makes it a lot nicer to work with threads for Java developers, right? So I think on the other hand and also with stream gatherers and things like this, you can also make sure that your performance can be better in your application. Mhm.

**[32:41]** So definitely yes for this. But I wouldn't say that just by switching you know by updating from 21 for example to 25 that your application will be you know that much faster unless of course you're making use of virtual threads and those things.

**[32:56]** Is virtual threads one of the most important changes for you?

**[33:01]** I would say yes because I mean this has been a change that's been long coming right. We have been long waiting for this change. but I think so this is I think pretty big change and you know it's something that makes it a lot you know easier to just you know get a lot of things you know done in parallel and you know having just a simple replacement essentially I mean if your code you know doesn't do some very if you didn't like use threats in a very lowle way it makes it you know very easy to change to them as well which is really nice. Yeah, my name is V and what brings me to JFAL is really learning new things meeting new and former colleagues and just enjoying the nice vibe.

**[33:41]** Okay. And I guess you are a Java developer.

**[33:44]** Absolutely. Yes, I'm a Java developer

**[33:46]** Since version.

**[33:47]** Long time ago, I actually was not start I've not been started like a typical IT as an IT person. I actually did more in finance and mathematics. but with the introduction of well back then the iPad even and the iPhone I was just yeah amazed by what the new technology wanted to learn it. So I taught a lot of it myself and I started with doing Java first as a hobby and that became just you know evening and weekend work and then I decided to switch my career to a full Java developer.

**[34:21]** Okay. Do you have a financial background a developer? You work at the needless revenue. Yeah, the boss back office.

**[34:30]** Can you combine your financial knowledge still with your programming there?

**[34:35]** Yes, absolutely. Because it's it has always been valuable to have kind of been both like having a domain knowledge of finance and tax as well as an IT mindset. it really helps bridging the gaps which are typically perceived between the two domains. and yes I can definitely do that and leverage on that in this in my current job. Yeah.

**[34:57]** Are you following also what's happening within Java releases and all the new features or is that what you want to learn here?

**[35:05]** Both. Yes, I am following that. it's also that you know within within my current employee we do have a so a thing called the group. So it actually tries to stay on top of the you know the latest developments. and for that I also for example follow this podcast to stay a breast of the latest developments. Yes.

**[35:24]** Can you already use some of these 21 25 features in your day job?

**[35:30]** Well the 25 I would say is a bit too recent to be honest but definitely the 20 21st LTS release. U but the 25 I'm do kind of for pet projects

**[35:42]** Within the company you work for or the government institution. you have a big group of Java developers.

**[35:48]** Yeah, if I'm not mistaken, I think the Dutch tax revenue is one of the biggest employers in terms of Java. so a lot of Java software which is operated. so yeah indeed yeah which is great as me as a Java lover to see that this is really the language running a lot of companies but also running the government.

**[36:14]** Yeah. to a certain extent of course because the Dutch tax revenue and I take it for the even the entire government operates many many different languages. and I think for the Dutch tax revenue Java is a big big component of you know all the solutions that we have but even more legacy systems which are cobalt based for example we also still have them.

**[36:36]** I see that you are even some kind of sponsor of this events or you contributed the app or you work together on the app correctly. my employer actually did develop this app. I haven't you know I don't know these precise persons who did develop them but apparently we do assist these kind of venues in and support them and I think that's great because it's I think there is a good alignment between you know what the Java community wants it's open source it's all you know atmosphere of collaboration and of course as a as a societal yeah body the Dutch government of course it's also good to important I think and even I know that certain developers sometimes do commit back and give back to the community so to the open source community

**[37:27]** The true spirit of open source

**[37:28]** Yeah correct yeah

**[37:29]** And it's nice to see that you as a government can contribute to open source because in a lot of companies it's not allowed or the time is only limited to real company work contributing back to open source is in most cases yeah all supported by the company.

**[37:48]** Yeah, that's correct. And that's I think what makes it in that sense like a like beneficial or an add-on or you know compared to commercially where you always have well at least I also work with commercial companies you always have this kind of well implicit pressure that you always have to you know be accountable for the hours that you are spending on and of course you have it now as well but it's less let's say it's more aligned with the community and that you can perhaps easier you know how you say allow yourself to also spend your time on things that also contribute not only your employer immediately yeah but also the community in a broader sense. Yeah.

**[38:30]** Because yeah a lot of the companies benefit from the open source but they forget that yes someone has to build it and maintain it.

**[38:37]** Absolutely. Yes. Yeah. There ain't no such thing as a free lunch. That is an a nice economic quote.

**[38:43]** Yes indeed. what are you looking forward from the other talks today? well learn two things for example the FFM API for example I'd like to know more and see whether that would be a nice thing to integrate with a thing that I know from the past called quant lip that's a C-based mathematical finance library and see whether that would be kind of nicely it can be nicely integrated with Java and yeah and many other things and also just pick a session which I don't know nothing about and just learn and that's that's the great thing about these venues. It's also typically you tend to click on a video or something you are interested in but here it's like you're you're walking around and you get to know get to know new persons and new new topics new new technologies

**[39:30]** Or end up in a podcast

**[39:32]** Or end up in a podcast indeed. Yes, I'm Ross CTO at what brings me to JAL just being went to JAL last year. It was such an amazing community. so I had to come up with a talk to make a reason to come here for the second time

**[39:49]** And the talk was about

**[39:50]** Automotive security.

**[39:52]** So I'm somewhat of a security specialist myself by no means a Java developer but I found a way to hack cars using Java.

**[40:02]** Okay. How did you do that?

**[40:03]** Yeah, that's an amazing question. there are so many ways to hack cars. and in this case, I exploited the keyless entry system and the canvas in the car itself, using some hardware tools and I interface with the hardware tools using JVA.

**[40:20]** Okay. And these hardware tools.

**[40:23]** So, in cars you have this chain of blocks, correct? Of all the computers which are inside a car.

**[40:30]** Exactly. you have like these electronic control units, the ECUs in a car which communicate with each other to make the car drive in a way we want it to drive. and all those nodes on this network communicate with each other and I am using tools to interface with this bus, this control area network. So, so it's called and to be able to interact with it. Yeah, I heard that in each car, modern car, there is over millions of rows of codes in all these blocks.

**[41:07]** Yeah.

**[41:08]** Didn't it become all too complicated?

**[41:11]** So you imagine like quite current now consists of about 70 computers ECUs. some are quite simple like small microcontrollers and others are fully fledged computers. and they have to find a way to communicate with each other. So they using most of the same protocols and it comes quite complex but for me it's like an area where you can keep discovering new things.

**[41:42]** Yeah. Yeah. and as they say security is limited by the weakest link. Is it easy to find a way in as you did? the funny thing about doing things on the hardware security side is that there are many people who already discovered weak links. but in software we used to being able to patch your software but in cars it's quite hard to patch hardware. So using the research by others you can quickly get an idea on how to approach projects like this and there is are many many hobby projects to come to discover your own vulnerabilities. but the hardest part is just interfacing with the car the moment you're in it's like you have all those research projects you can just copy and paste. Yeah. and what did you hack or what did you do wrong with the car?

**[42:39]** So, the first step or the first case is being able to access the car by exploiting something called the roll gem attack. So, when a user presses the key fob of a car, it sends a radio signal towards the car. And by stealing those codes and making sure they aren't able to reach the car, we can use them at a later moment to still open the car without a user noticing it. So then you're in the car. That's step one. And step two is to find a way to communicate with those computers in the car. and in this case, we are going to exploit the canvas. and I show in my presentation how you can read the signals so you can see what actual is being sent on the bus itself but also spoof messages yourself and in this case the example is by spoofing messages toward the instrument cluster we can let the instrument cluster think it's in a car and we can rev the motor without it being in an actual car.

**[43:47]** Okay. is it dangerous? Can I do something wrong with my car that way?

**[43:52]** Yeah, for sure. if you dive into automotive security, you will find that most of those researchers try things on their own car and they have all have stories about breaking their car. you will also find ways of breaking the law. For example, in automotive, it's quite important to have like the autometer of a car only go up. But there are ways you can exploit things. so it you can actually break the law, but I think that's not only limited to car hacking. That's limited hacking. That's to all hacking. Yeah.

**[44:27]** Do you do your experiments on your own car?

**[44:30]** That's a funny question. I'm supposed to do them on my own car. but I use my wife's car. and there's a reason for it. I do not own a car myself. I lease a car, so it's not mine. I'm not allowed to test on it. and my wife does happen to own a car nice at the pizza. but I limit the test cases on the actual car. I know for sure that are quite safe to test and what I do instead is buy components out of eBay for example so I can do it on a lab setup on my desk.

**[45:07]** This is your job?

**[45:09]** Somewhat. Yeah. I'm myself a CTO at a cyber security company but we do focus on IT security. but I find it quite interesting to bring stories out of the hardware hacking scene towards IT because it it's a nice way of showing people why it's important to do IT security stuff without telling them their software is early.

**[45:34]** You said you're not a Java developer yourself.

**[45:36]** Yeah.

**[45:37]** What do you learn from a conference like this? the way Java developers think because I'm not a developer myself but I do work at clients where we have like big DevOps teams and they mainly use Java and I think it's important to learn what are the current frameworks language we're using so we can use that in the examples we are trying to show on how important security actually is without being outdated without using the wrong examples And especially being able to show that I without being a Java developer can use Java to hack cars. I know that we have like a whole conference full of people who are more skilled in Yava and should be able to also push that part of the community a little bit further.

**[46:29]** And that's a wrap for this episode of the Foojay podcast. A huge thank you to my guests for sharing their expertise and passion for OpenJDK. And thank you for listening. If you enjoyed this episode, please subscribe to the Foojay podcast on YouTube or in your favorite podcast app. You can find all our episodes, show notes, and links to the resources mentioned today on Foojay.io. And don't forget to follow friends of OpenJDK on social media for the latest news and updates from the Java community. Until next time, keep coding, keep learning, and stay curious. Thanks for listening. Give me a foo. Give me a J. Give me the friends of OpenJDK.
