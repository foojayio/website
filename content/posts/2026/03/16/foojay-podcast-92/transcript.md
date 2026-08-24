**[0:00]** Java 26 is here and brings faster startup HTTP3 and goodbye to applets.

**[0:06]** Welcome to the Foojay podcast. All your news about OpenJDK.

**[0:12]** Welcome to another episode of the Foojay podcast. Today we're talking about Java 26 released on March 17 in the year 26. Again, right on schedule with Java's six-month release cycle. Now, Java 26 is not a long-term support LTS release. That was Java 25, but don't let that fool you into thinking there's nothing interesting here. This release brings 10 JDK enhancement proposals, JEPS, as we know them. They cover everything from performance improvements to long overdue cleanups and improvements. Of those 10 Jeeps, five are new features and we also have five preview incubator features. I have two guests for this podcast. Welcome both of you. can you please introduce yourself?

**[0:59]** Hello, I'm Lloyd Machum. I'm a lift software engineer at Kestra. I have more than 20 years of experience with Java and GVM technology. you can find me in various social media but I have also my blog on lom.fr there and yeah just quickly Kra is open source workflow orchestrator and scheduling platform writing in Java.

**[1:26]** I'm Simon Ritter. I'm the deputy CTO at Azour and I've been doing Java for a very long time shall we say. [snorts] [laughter]

**[1:35]** Okay. Before we dive into the changes in Java 26, Simon, can you remind us about the difference in this long-term and short term? support releases and why people may or may not stick to 25.

**[1:51]** Yes. So, we need to understand that there's this idea of long-term support, but very importantly, long-term support only applies to the binary distributions of OpenJDK. We sometimes confuse this with thinking that JDK 25 has long-term support, but it's actually only binary distributions. This is something that Oracle introduced and thankfully all the other binary distributions whether they're from Timarin or Amazon or Microsoft or whoever as we all follow the same cadence in terms of those long-term support releases which means that this particular what 25 has extended maintenance and support for a period of time but how long that is for is entirely down to how long the distribution wants to provide it. and people who stick to these LTS's, they should wait till 29 to get the full new version.

**[2:44]** Well, you see, Oracle would definitely argue that that's not the case and they would say that all releases are fine for production.

**[2:52]** But let's be fair and say that if you're running something in production, you're most likely not going to want to change the version of Java every 6 months. So long-term support releases make a lot of sense because you can continue to use them for you know several years and continue to get those security patches and bug fixes. Th

**[3:11]** This is exactly why at Kra for example we only follow the LTS LTS release because also we have users that sometimes run Kra directly from a Java file. So also they will need to install Java. [clears throat]

**[3:31]** Most of our users are using Docker images, but yeah, we don't want to update each 6 months because it's just unnecessary work.

**[3:40]** Mhm. [clears throat] does that mean that all your systems are already on Java 25 which was released in September?

**[3:46]** So, I created the pull request I think a few weeks ago.

**[3:52]** And this will coincide with our LTS also. So, it's good. usually what I did is that I migrate to Java LTS approximately 6 months after it's out. So you see here a little earlier but just to be sure that it's stable and for example we have lately an issue with the tarin distribution because it can also happens that there is issue in the distribution. So we also need to be yeah a bit conservative you know because 5 is used by

**[4:32]** Tens of thousands of users.

**[4:34]** Yeah. Is that something you see in more companies Simon waiting 6 months till the first patch update?

**[4:40]** Yes. I would say that a lot of companies will actually wait even longer than that. 6 months is definitely good because obviously there's two updates that have come out. So if there's any significant issues those tend to get ironed out pretty quickly. But I think a lot of enterprises can be more conservative and think to themselves, okay, we'll wait a year before we deploy a long-term support release because again, you know, you can continue using the previous one without any problem and then move to the next one when you feel confident in terms of doing that.

**[5:10]** Azul released this state of Java 2026 report. Can we also get some some numbers from there recording how many people are still on Java 8 or is everyone already on 25?

**[5:24]** Yes. And that was very interesting actually when we released that because the fact that we released the report just a couple of weeks ago I think it was but the data for that report was gathered sort of at the end of last year. And when I look at the graph and the bar chart that we've got of people's usage of different versions of Java, I was quite surprised to see that 25 came out at 18% of the people responded. We're using 25 in production. So that's that's a very good sign that people are confident that they can use the latest long-term support release in production. I mean that compares with JDK2 which we had 37% of people. So that's clearly the one that most people are using in production, but there's there's a good shift in people actually switching to JDK25 fairly quickly. if we look at older versions, you know, 17 and 11 are fairly similar at sort of 20 mid20s percent. JDK8, there's still 21% of people using JDK8 in production. that doesn't really surprise me at all because JDK8 is very popular. It's rock solid. And certainly if you move from 8 to anything later,

**[6:39]** You do sometimes bump into problems with the encapsulation of internal APIs. Although most of the libraries have iron that problem out, you know, if you look at Spring and things like that, that's all been ironed out. But if you're using some older libraries because you're using JDK8, and those libraries maybe aren't maintained anymore, then you might have problems moving to a newer version. So there will be that lag for people still continuing to use JDK8. Even JDK 7 and JDK 6 which we asked about JDK 6 9% of respondents and JDK 7 11% of respondents. So there's there's a healthy number of people still using very old versions of Java [snorts]

**[7:16]** And Java 8 is 12 years old or something like that. Ah yes it will be because yeah

**[7:23]** I think yeah so yeah these are old run times although they maintained of course still by the way for people who are interested in all the changes between 21 and 25 I had a very interesting podcast with Jacob Jenkov that's 90 of course still available you can listen online where I have this very amusing talk and a lot of quotes of people I interview at conferences about what they find most important of what change between 21 and 25. Okay, but let's move on. 26. Lu, you have again prepared a very interesting post with all the changes. in this podcast, we focus mainly on Jeeps. but you also had a lot of other Yeah. under the hoods and bug fixes. And can you maybe highlight the most important ones besides the Jeeps?

**[8:15]** Yeah. So besides the jeeps, I think the most important one and maybe it should have had its own jeep it's UID7 support because it's very it's the new version of UU ID and you create it from a u a time stamp a Unix time stamp. So this is a sortable UU ID. which mean it can fit in a database primary key. very yeah it's it's very good for database primary key because you it's sortable by default

**[8:52]** And what is great also is that it's it remains 128 bit long. So it remains the same size as a previous version of U ID.

**[9:04]** Mhm. So this is for me the main change in Java 26 that is not in a jet.

**[9:11]** Okay. I read it's really important if you use UIDs as as a primary key in a database that it can lead to much larger improvement of the speed of fetching data and sorting data.

**[9:24]** Yeah. because it's a it's it's naturally suitable and you don't need to store a time stamp with your record, you know, because you only have one in the UID.

**[9:36]** Okay, so you can just extract the time stamp again from the UID.

**[9:41]** It's interesting, isn't it, how something as small as that can have such an big impact and somebody kind of suddenly realizes, oh, we can do it this way and actually make life a whole lot easier.

**[9:50]** [laughter]

**[9:50]** Yeah, there are a lot of these small changes happening under the hood of Yeah, like we also had string going back from two bytes to one bite per character, something like that. You don't notice this as a developer, but on the other hand, it makes sure that you have less memory use or you can fetch data faster, stuff like that. So yeah, that's really the great thing of all these evolutions and new versions that I also heard like the FFM API which was introduced in Java 22. But if you switch to Java 25, exactly the same functionality seems to work a lot faster. [snorts] Again, you don't know why because yes, some some experts and gurus did something in the code for this. And that's what I find amazing with all these new versions that you get all these speed improvements.

**[10:41]** Yeah, there is a lot of pull requests in open GDK that are performance improvements you know and also there they add interesting intrinsic for a new architecture or for existing architecture for on every release and so you get better performance without noticing

**[10:59]** And for example this for this release I noted that they had performance improvement for release add in common code you add all you add a collection very often and with this release I don't remember exactly but you it's just way more better you know way more performance

**[11:22]** Yeah again an under the hood improvement that yeah you don't need to do anything to get the benefits out of it just upgrade your runtime

**[11:30]** Yeah we upgrade from 21 to 25 and we saw a decrease on the e size for example I'm not sure why [snorts] because it's very strange But it goes down to I think 350 to 220 something like that.

**[11:47]** Quite possibly compact object tennis that would have had that change because that did have an impact on heap size.

**[11:55]** Perfect. So all these small improvements make life easier and cheaper running things on the cloud. They need less resources. Now let's dive into the jeeps the JDK enhancement proposals. I try to group them related to the projects that they are included in. some have overlap so we'll see how that turns out. Let's start with the core changes. and there we have two f let's see two final jets and one preview. The first one is prepare to make final mean final in jet 500. Does that mean that we all got light about final being final? What's happening here? Yeah, it it's an interesting discussion, isn't it? Because of course in Java you have the final keyword. So you think to yourself, okay, well, if I make a field final, then it's immutable.

**[12:43]** And technically, you know, from the point of view of the programming language, yes, it is immutable because if you create, you know, final int i equals 10, then it's going to be 10. You can't change it in your code unless you use deep reflection. but even then there's there's three different types of variables that you can affect and some of them you can change with deep reflection some of them you can't change with deep reflection but ultimately it is about the ability to make final objects final and not be able to change them which then means that the JVM can be more effective about how it uses them. So that's that's a big thing again for performance and maybe even size and this is related to project Valhalla as well.

**[13:24]** And the next 526 which is a second preview lazy constants to me it seems related what I found in the It's about lazy constants. These are objects that hold unmodified unmodifiable data. Lazy constants are treated as true consists by GGVM. Is it like we use enums and is that a bit the idea of this lazy constants?

**[13:50]** It's more a way to compute the value of a constant not immediately because when you it's when you define a constant. So with private final static private final you must set the value of the constant immediately because it's it's final here. It's like a lazy final. So you will compute the value of the constant at first use and it will be computed once and never changed after that as it's never changed the JIT compiler will be able to con to optimize it. It will be able to constant fold it. this is related to also the final means final because one of the reason why the open team want to one final really to mean final is to be able to constant fold the value of final fs and with lazy constant it's already there.

**[14:48]** All these improvements again relate to how code gets compiled and optimized leading to better performance. as Lu says, there's there's quite a lot to this in terms of the way it works because the difference between final again is that it's to do with when you initialize it. That's the key difference here is that you've got more control over when the initialization happens and that gives greater flexibility. It is interesting that they've changed the name because it used it was stable values in the previous preview and now it's lazy constants and they the argument they use there is that this gives you a better description of it. the fact that it's more of a higher level feature rather than a lower level feature. so yeah, it's it's definitely interesting from the point of view of both performance but also because of the way that you can then you know factor your code in certain ways.

**[15:38]** Yeah, I think in the case codebase I have more than 15 places where I use the double locked idom. and very often one of the junior developers in my team when they try to do it they do it wrong you know because it's a very complex idium code idium and with lazy constant you just do it with lazy constant and you are sure that it's concurrently okay

**[16:11]** This is very important

**[16:12]** Another chap related to the core libraries is http3 I had to look it what it actually means but it is it uses quick and it means that we can now do HTTP over UDP instead of TCP why is it important for Java or why did it needed to be added

**[16:34]** Again it's performance and it's also protocol support you know HTTP3 is a protocol that be that starts to be used a lot because it's more performant and so having it covered inside the Java. It's it's a it's also very important, you know.

**[16:52]** Yeah, I had to look this one up as well when I was looking through the Jets and obviously it's like oh HTTP3 what is different about that? And as you say it's it's the fact that it uses a UDPbased protocol rather than a TCP based protocol. And that kind of I found really interesting because like way way way back in my career, right at the very beginning of my career, I actually wrote a network protocol for a system that we were working on and it was like a you know the ISO7 layer model and we made this complicated thing that used it. But what really kind of struck me is the fact that TCP has been around for so long and it's it's stayed so like relevant. we haven't replaced it with something better. again, I actually had to look this up and TCP actually came out in 1974 and then TCPIP was 1978. So, we've been using TCP for a long time and it's very interesting to see that they've decided to switch to a UDP based protocol to get better performance out of it. So, interesting stuff there.

**[17:55]** I cannot imagine HTTP over UDP over internet. Does that work or is it something for local applications?

**[18:03]** No, my understanding is that because it uses this quick thing. So, it's quick UDP inter internet connections. So, it gives you the reliability of TCP I think. Although I haven't looked into it too much detail, but I assume that you get the reliability of TCP because if you don't have a reliable connection that you would get with TCP, I don't see how that would work with HTTP at all. Yeah, I used UDP pay a lot in a previous job where we had communication between devices next to each other in the same network. Great stuff. But yeah, using it on internet. I have to look into that quick thing what how it works and what it can do. But yeah, indeed an interesting one. It's again adding a protocol which is widely popular and keeping Java in sync with that. Another one, important one, but it's actually a cleanup in the client libraries is Jeep 504. Remove the applet API. Is it still used?

**[18:58]** Well, there's 9% of people using JDK6. So, [laughter]

**[19:01]** Okay. Yeah, probably is. Applets. help me. I'm from the Flash generation, so I created a lot of Flash animations and interfaces in the browser. Is it the same kind of stuff that now gets removed from Java?

**[19:18]** Yeah, I suppose it is. I mean applets go right back to the very beginning of Java. that's what was really the whole deployment mechanism that was used in the very early days was putting them into applets into a browser. the problem is that it just hasn't maintained any level of popularity and nowadays yes there are still some people using applets. certainly at Azour we come across people who are still using them and we can provide support as I said for six and seven and eight that still have them in there but applets the really got removed completely in JDK 11 and then they were the applet API was deprecated way back in JDK 9 and then they also removed the security manager in JDK 24 and the [snorts] fact is that none of the major browsers still support applets at all.

**[20:09]** You can't get a supported browser that will run applets at all. even Internet Explorer 11, which I think was the last one that was a hold out, they stopped supporting that a while ago. So, they're just dead technology really.

**[20:23]** Look, you use

**[20:25]** Yeah, I already create, but this was before flash. [laughter]

**[20:32]** It's not a good memory. Mhm. [clears throat]

**[20:35]** It was very complex but you was able to do anything almost anything that we can do in a swing or a T application with applets.

**[20:47]** But yeah, it's removed now. So no way back. [laughter] We're not going to use applets anymore. The next one I think we can do short is Java 524 PM encodings of cryptographic objects. It's a second preview. We talked about it in the previous podcast with Java 25. it's about how you can use certain files to define security. Correct. Yes. We wait so long for it. Yeah, every major language support to prom file from years and it's yeah it's a it's a nice addition and it didn't change a lot at least the two main classes PM on and PM decoder didn't change too much on Java 25.

**[21:34]** Sim you have any idea then why isn't it not finalized?

**[21:38]** I don't actually. I'm assuming that you see that the idea is that they've made a couple of changes and they still want to see if there's any feedback on that

**[21:48]** Probably because they wanted to make a couple of changes. they thought they give it one more round before they make it final.

**[21:54]** Good. So we'll see in the next release if it's finalized. Okay. two new Japs are related to the Hotspot and Leiden projects. Hotspot is at the core of JBM. It takes care of class loading and code interpretation, compilation, manages memory, all that related stuff. Now, project Leiden is about extending OpenJDK hotspots by capturing runtime JIT optimizations ahead of time to make Java start faster and make applications actually start faster. ahead of [clears throat] time, we also had a few of these in the last release. So there's now a Jeep 516 ahead of time object caching with any garbage collector. Will it bring a big change or is it an ongoing evolution?

**[22:44]** It's an ongoing evolution. As I understand it's just that they built on top of what they already have a way to have a cache that is diagnostic to the G garbage collector that creates a cache that can be read with a different garbage collector. Yeah, it's it's related to the ahead of time class loading because that that's a very effective way of reducing startup time for applications. I mean if you take something like the Spring Pet Store sample application that loads about 21,000 classes when it starts up and that leads to quite a long sort of startup time.

**[23:22]** Using ahead of time class loading, you basically do a trial run. you create the internal data structures of those loaded classes and then you can then load those in very quickly. The problem that they had was that would work with most of the existing garbage collectors so G1 and serial and so on but it wouldn't work with ZGC. So they wanted to make it so that it would work with any garbage collector. this is good news for us, I guess, because we have platform prime and we have the C4 collector and since ZGC is heavily based on C4, [snorts]

**[23:56]** Then it should mean that we can integrate the ahead of time class loading with that. Although I suspect we already do that with ready now, so we probably don't even need to worry about it.

**[24:04]** Yeah. Yeah. So ready now is a way of storing everything which is happening in the GVM. So in the next run you can instruct the comp compiler to do that again in a very fast way. So this ahead of time changes which are happening in OpenJDK very seem to be related to what ready now is already doing. Correct.

**[24:28]** Yes. Yes. when I look at the ahead of time stuff for project Leiden, I see that as yes, they're very much catching up with what we've done with ready now because they're doing the class loading and then they're then going to move on to the idea of caching the compiled code

**[24:42]** From a previous run so that you can reuse that without having to go through the whole warm-up phase and identifying methods and C1 and C2 JIT compilation. You can just load the code straight away. I'll add the link in the show notes to the warm-up documentation that I wrote some time ago together with my colleague because as a Java Java developer I had only small applications in the company I was run working for before I joined Azul. I never worried about this warm-up but actually that's a very interesting story and it tells you a lot about how the Java runtime works. It starts with loading files and checks how much your code is used and how it's used and then start optimizing it. So there's a whole story behind there. So interesting we have a few posts about this I think on Foojay. So we should definitely add a link. There's another related to the hotspot is 522 G1 garbage collector improve throughput by reducing synchronization.

**[25:40]** Again one of those under the hood changes which leads to performance improvements. Correct.

**[25:46]** Yes, it's absolutely it's a looking at ways that they can improve the way the garbage collector works. and the way that G1 is designed is it uses regions. So although it has like an old heap, the heap is divided into young and old generations. That's what I should say old generation. The generation itself is divided up into regions. And so you can do garbage collection of regions specifically and that can be much more efficient because you identify regions that have low amount of live data. You can priorit prioritize those over ones that have a high amount of live data and that becomes more efficient. The problem with doing that from the GC point of view is that you need to maintain or need to find links between objects in different regions. And rather than scanning the whole heap, which is one way of doing it, they maintain a list internally of that which is called a card table. And the way that the G1 collector works internally is that it has to do some of this work in terms of updating that list as the application is running. So it can't do it fully concurrently. [snorts] And the

**[26:53]** Drawback is that for certain types of applications, the card table can become quite big. That means the way that the objects are being allocated, you just end up with lots of cross region links. And so you end up with a very big card table and that can then slow down applications when it has to be scanned. So what they've now done with this particular Jeep is to introduce the idea of having a second card table. So they make a copy and then that can reduce the amount of synchronization between the application threads and the GC to alleviate some of those performance issues. So it won't affect every application but there'll be certain applications that get hit by this and then are going to perform a lot better using G1.

**[27:32]** For people who are not really knowing what garbage collectors do, there are a lot of interesting talks. If you can join a conference and talk about garbage collectors, please do because there's amazing technology in there. I would thoroughly recommend one of my colleagues presentations, Garrett Grvald, who does the it talk.

**[27:53]** Yes. Yeah, he's got a very good presentation that you'll find that on YouTube.

**[27:57]** And as a bonus, he has a lot of animations in that presentation and I know they are created with Java VIX. Yoohoo. [laughter] The next one is Project Loom. it wants to bring support for easytouse, high throughput, lightweight concurrency, and new programming models to the Java platform. We all knew it from the virtual threats who arrived in Java 21. we are now in Java 26. Are we still working on those virtual threats or are we adding stuff like in 525 structured concurrency but is the s preview? What is the idea of this concurrency structured concurrency? The idea of structure concurrency is to simplify writing concurrent code you know because it's it's it's it's very complex to have it right. so you know open a scope for your concurrency and then you create a subtask and you have multiple primitives to join the sub task and at the end of the scope and the scope you can open it inside the tri resource at the end of the scope you are sure that every thread every every task will be terminated every thread will be closed. So this is really this is under the hood using the new

**[29:17]** Facility of project loom. but it's really yeah the main purpose is to be able to write concurrent code more easier.

**[29:28]** It's one of the things that we've had in Java right from the very beginning is the idea of built-in multi-threading. But just having multiple threads is only a small part of the solution to writing multi-threaded code. synchronization and cooperating between threads is the really hard thing and we've we've seen multiple sort of stages where we've changed the way things work in Java added more functionality so you know JDK 6 I think we introduced the concurrency utilities then we had up or was it JDK 5 anyway a long time ago we introduced concurrency utilities and then that gave us highle abstractions which we could use things like semaphors and mutxes and stuff like that Then we had the fork join framework that was introduced. JDK8 introduced the idea of parallel streams to try and simplify some of this kind of work. then obviously we've got virtual threads that have been introduced and now structured concurrency which as Luix says putting it inside a try with resources type of construct makes life a whole lot easier. it just really does make life much easier. And it's not

**[30:33]** Only about are all the tasks finished, it's also if one of the tasks fails that you can stop the other one. I think and that's that's part of the challenge there. When you open a scope, you define the joint policy. basically you can say that everything need to success or you can say that to a wait even if something slow or to stop for the first failure. So you

**[31:02]** It's very flexible and it's it's it's something that is very complex to have it right if you do it by yourself. The next project that we can mention is project Panama. It's actually my favorite ongoing project. it has brought the foreign function and memory API the FFM API to Java 22. So already again already delivered a few versions ago. we use it to simplify the Pi4J project to talk to native libraries to work with electronic components on raspberry pi billboard banana pie all those boards. so the ffm API is actually finished as I said in the introduction. jumping from Java 24 to 25 seems to bring even speed improvements there without knowing what actually changed under the hood. So people did a lot of improvements there.

**[31:52]** But we have this ghost project ongoing there with the vector API. We are already on the 11th incubator with 529. Are we still waiting for other projects to influence this?

**[32:06]** I think vector API is finished also you know but they want to wait for project Valhalla. so they can use the improvement of project Vala inside the vector API. they still kind of keep tweaking it because when I looked at the jet there there's just like some very they say there's some minor changes although it wasn't really too obvious when I looked at it what those changes were.

**[32:30]** I guess it's kind of interesting that they introduced this jet so far back knowing that they weren't going to finalize it until Valhalla was going to be released and knowing that was going to be some time away. So it's it's interesting that it has like a double use because of course vector API you can get better performance for certain types of numerically intensive operations where you're using conditionals and things like that and you can specify explicitly what you want the code to do.

**[32:57]** But because it's not a final feature you have to turn on the preview features in order to use it in production. So some people might kind of decide not to do that. M

**[33:09]** I think there is a trick here because I read somewhere that some frameworks already use a vector API and they use it even if it's not enabled by pre they use a trick. So they use it because it's really way performant.

**[33:23]** Yeah, because I know it's a it's an incubator module rather than a preview feature. So I don't know whether it actually because I think the distinction there is that because it's in a separate module. So it's not you don't actually have to turn on the preview features to use that. is just in a different name space and when they finalize it, it'll go into the Java or Java X namespace. so that's probably how they can get around that with the frameworks.

**[33:47]** I think it was mentioned a few times in earlier podcast because Jacobebased database already uses this indeed since yeah many versions ago and they indeed yeah because it's incubator or preview it means it can change the API can change. So they had to adapt the J vector implementation based on changes with each new version. and they probably also will have to change it when it comes finalized because it moves from name space then as you said Simon but yeah indeed you're right it's already used by a lot of projects which really need this kind of functionality. This Falha thing is it far away?

**[34:28]** There is some announcement by Nicolay Parlo. he said that it's May comes with TDK 28 the first jet

**[34:40]** Why 28 and not 20 7 it's because 27 is already started

**[34:47]** And they basically they want

**[34:50]** Almost nothing nothing else to be planned in the GDK release it may change you know it's it's not an official statement from horicult

**[34:59]** Yeah because we are now recording this podcast by the We are recording this almost a month before the release of 26. But we can do that because all these new jabs are already defined way up front and that's as you said Simon is because of the stability. It is tested, it is built, it is reviewed and approved many times. So yeah, 27 is already in the making. So they're not going to change a lot of jets and adding new jets now just a few months before they need to finalize it. If you look at the actual specification for JDK26, the it actually the ramp down phase was entered in December. So that's when they kind of feature freeze the jets that are going to go into that.

**[35:45]** So it's

**[35:46]** So more than 3 months before it actually released. Yeah. Okay.

**[35:49]** Exactly. It's about halfway through. We can complain that this is the 11th incubator but actually it's a proof of steps are not taken quickly to fix something. It's really we want to build something which is tied to other improvements and which are ready for future extensions or ideas we already have that could be influenced by this. I keep hitting the same nail in all these podcasts about Java releases. It's changes don't happen fast but they happen because they produce something new which is stable and we can rely on for the future and that's been the idea of OpenJDK.

**[36:28]** You're right. You see, this is one of the great things about the way that OpenJDK is run and I always say that I have a lot of respect for people like Brian Girtz and Mark Reinhold and the others who are responsible for these things because they do a they are changing the platform on a fairly fast basis if you like because of the six-month release cadence, but they're doing it in such a way that is very well controlled. So, we're not breaking backwards compatibility. we are adding features that are considered and that the whole preview thing does allow time for feedback. and we've even seen where, you know, features have actually been removed completely because people weren't happy with what was it? Text

**[37:07]** String string templates.

**[37:08]** String templates. That's it. Yeah. String templates got removed completely because people didn't like it and you know the feedback was such that they thought, right, let's have another go at it.

**[37:16]** It happens but it doesn't happen a lot and chips get removed again. Okay.

**[37:19]** It was the only one.

**[37:20]** The only one. Okay. [laughter] who should be a bad feeling if you worked on it.

**[37:27]** And 26 is the only release where there is no features coming out of preview. So there is new features.

**[37:34]** Mhm.

**[37:35]** And features that remain in preview.

**[37:37]** Okay. I didn't even notice. So the new features are really new. They didn't happen in the preview. No,

**[37:42]** I didn't notice that either. That's interesting.

**[37:44]** Okay. Good.

**[37:46]** In my blog post I have sections. So when I arrived in this section I look at them and I look at the jeep and say oh not nothing.

**[37:55]** So I should change my introduction. We have no five new we have really new new new options there [laughter] in the OpenJDK. next one is related to project amber which is a collection of all different small productivity oriented Java language features. So what do we see there? It's about improving the code and make it more readable like records, improved pattern matching and much more. I checked there were already 14 jeeps going back to Java 10 related to project amber like records and pattern matching. and now we get a fourth preview with Jeep 530 primitive types in patterns instance of and switch. So again further improving the pattern matching correct. Yes, this is quite a an interesting one because primitives and you know type references in Java are kind of a bit difficult because you end up with you know we had primitives in Java rather than treating everything as an object because of performance. So back at the very beginning we had to have primitives. ideally everything should be treated as an object, but we end up with wrapper classes and that

**[39:09]** Kind of leads to some very interesting things around the whole boxing and unboxing that you get which is sort of invisible and I think I've said this before on this podcast. I sometimes wonder whether adding autoboxing and unboxing wasn't actually a bad idea for Java, but that's another debate. But the whole thing with this is that it becomes very complicated when you have primitives in things like switch because you can combine primitives with full types and that then leads to some difficult situations to resolve and I actually do a there's a presentation I did at Devox well a bof that I did at Devox last year which was Java puzzles and some of that was related to pattern matching for primitives and switch. And I came across there was two examples where I couldn't explain why the code worked the way that it did. So sometimes it would compile and I couldn't explain why it would compile and sometimes it wouldn't compile and I could I couldn't explain why it wouldn't compile. And I actually I sent a message to the Amber mailing list. I got some feedback from that and then I

**[40:21]** Had a discussion with Gavin Beerman who's the Oracle person who works on this particular feature because I was at the JCP executive committee meeting last year and he was there as well. So I had a chat with him and he said oh yes we saw your stuff and it we thought that was very interesting and now we've had to go back and have bit of a think about how some of this stuff works. So what they're actually sort of dealing with here is what's called unconditional exactness. and being able to prove dominance of one case over another. So there's some quite complicated things underneath that need to be fixed.

**[40:55]** There is a good write up of unconditional exactness and dominance in the j people are people that want to learn more about pattern matching and primitives should definitely read them.

**[41:07]** It's in the Jeep 530.

**[41:09]** Yes.

**[41:10]** Yeah. Okay. Again, all the links are in the show notes. Yeah, I can imagine if you have the number five and you want to pattern match, is it a bite? Is it an int? Is it an integer? I guess it's this kind of challenges and then much deeper.

**[41:26]** Yeah, it's not so much constants like that. It's when you have a situation where if you have if I can remember the thing that I had it's like if you have a case for an integer wrapper class and you have a case for a bite then one of those is unconditionally exact because you can prove that it's an integer as it's passed in because it's an object reference. But the other one is a runtime check for the bite because you'll get unboxing and then you need to test whether the value that you've got in that object will fit into a bite and that leads to some very complicated situations in terms of dominance

**[42:07]** Things which will be solved again in the GVM itself. So we as a developer don't need to worry about it. I want to refer to the JVM weekly newsletter by Arur Scoonsky. He makes this on LinkedIn every week. A very nice overview of what happened of everything related to Java. And he has something to add regarding this is from the Amber mailing list. It's data oriented programming beyond records. That's the title of that mail in the mailing list and it talks about yeah a new kind of object that maybe we need to be added which is like a record but not really final. Are we making things complicated or do we solve is it a real problem that can be solved with it? I read it and I also read the some some thread on the umber mailing list about it and as I understand it it's to bring the sugar syntax of records to classes by calling them carrier classes.

**[43:14]** So they will have components which are like the component of a record but then there is a blur because everything is final in a records but not in a class and in a class you may want to have additional fields and or do you evolve a record to a career class a career class to a record I think we will about it more in soon but for now for me it's it's it's kind of complex. Yes.

**[43:41]** Yeah. I must I haven't read Arthur Arthur's article about this, but I know that there's this idea of withers, isn't there, that they're looking at in Amber, and that sort of is, I guess, somewhat related to that in the sense that you're you're taking existing objects and then creating a sort of a different representation with something different. but yeah, I know I saw on the Amber mailing list there was some discussion about some of these things and I always find it very reassuring that Brian Girtz will give a very detailed explanations about why they're not going to do it this way. And it's not just like, yeah, we're not going to do it this way. He actually gives a very good explanation of the fact that they thought about doing it this way, but then it doesn't necessarily make sense to do it this way because of these reasons. And he actually gives good feedback on that.

**[44:30]** Again showing that this stability is the most important thing and changes are really thought out. Yeah. [snorts] another one that I also found in this JVM weekly it's about project Babylon. So project Babylon wants to bring among other things also Java to the JUS a bit like what is already possible with tornadovm and we have a Jeep which is in draft and I learned now how the numbering works because this has the number 8361105 so that probably means it's a draft and it will get the shorter number when it's

**[45:09]** Unique IDs are first writ in the GBS in the

**[45:15]** Okay. Yeah.

**[45:17]** And then they are extracted as a JIP.

**[45:19]** Okay. So it's a buck ID. Nice. So it's about code reflection and libraries to make it easier to move Java code to the HP with current AI evolutions and all the stuff happening with LLMs and models and training models and building models. Do we need a lot of improvements in Java to make it even better for this kind of use cases?

**[45:45]** Yes.

**[45:46]** Short and sweet. [laughter]

**[45:47]** Short and sweet. but a lot of things are happening with LangChain4j and all this spring AI. So they bring a lot of the AI functionality to Java, but I guess they are on the side of using a model. Is Java already there to build something like a model? I guess when we look at it from the GPU perspective, it always makes sense to have more capability for the JVM to use whatever processing power is in the machine. So if you do want to run some of these things, then it could be very good to have the JVM capable of using the GPU functionality. I guess the sort of flip side of that is that as you quite rightly mentioned, there's the tornado VM which is specifically targeted at GPUs, but I haven't seen particularly widescale adoption of that.

**[46:34]** So I maybe you know it's it's more of a peripheral kind of thing that we need to integrate into the JVM so that it can use both the you know existing CPUs and build on GPUs as well.

**[46:48]** I think there's more work to do there. convert friction is more broad like that the example shows GPU because it's GPU but it's a way to describe a piece of computation and then to schedule it and I think there is way more in the project than just GP calculation on GPU

**[47:12]** Yeah Pablon also mentions SQL other programming methods machine learning models yeah they talk about a lot of different use cases. So, but again, yeah, I don't think we had something of Babylon in this release and I don't know the state of that project either. So, we'll need to check back on it. people who are interested in this OpenJDK projects in these jeeps. So, the links are in the show notes. all these OpenJDK projects have a mailing list. You can easily join if you're really interested in what's happening there. and it's open to everyone to follow because yeah it's an open source project so all the sources are also on git anything you want to add about this release

**[47:56]** I think the only thing I'd add is it's kind of interesting because JDK25 came out in 2025 JDK26 comes out in 2026 and that's it after that we're we're no longer tying numbers to years

**[48:10]** But we will need to do some calculation to see what happens after the year 100 [laughter] but I'm not sure if we'll indeed reach that kind of milestone again.

**[48:21]** Java 20 24 at 24 j [laughter] again I don't think we will see it again.

**[48:28]** No no no. Okay. that's wrap for this episode of the Foojay podcast. A huge thank you to my guests for sharing their insights and to you for listening. Please remember Java 26 is a solid release and you can use it in production. Maybe you have to stick to the long-term support versions in your production systems, but this 26 short-term support version is the kind of release that quietly makes your applications start faster, run smoother, and make your code a little more readable. If you enjoyed this episode, please subscribe to the Foojay podcast on YouTube or in your favorite podcast app. You can find all our episodes, show notes, and links to the resources mentioned today on Foojay.io. And don't forget to follow friends of OpenJDK on social media for the latest news and updates from the Java community. Until next time, keep coding and keep learning. And if you haven't upgraded yet, Java 26 is waiting for you. Thanks for listening.

**[49:27]** Me the friends of OpenJDK.
