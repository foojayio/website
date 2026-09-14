**[0:00]** Java 27 makes your heaps smaller and your garbage collector smarter by default. And project Valhalla is finally knocking on the door. Let's dig in.

**[0:09]** Welcome to the Foojay podcast. All your news about OpenJDK.

**[0:16]** Welcome to the first episode of season 6 of the Foojay podcast. Today we're talking about Java 27 released on September 15th, 2026. right on schedule with Java's six-month cadence. Java 27 is not a long-term support release. That was 25 and the next one will be 29. But don't let that fool you into thinking there's nothing here that we should talk about. This one is a bit special. There's no shiny new language feature, but there are a few changes that quietly make almost every Java application smaller and faster the moment you upgrade. And because a release podcast should always look a little bit further down the road, we'll also spend time on Java 28 coming in March 2027. Because after more than a decade of work, project Valhalla is finally landing its first preview with value objects. That's arguably the biggest change to the Java object model in the language history. To help me make sense of it all, I have my regular guest back, the person who's been doing Java for a very long time, Simon Ritter. Simon, welcome back. can you remind

**[1:30]** Us what was the Java version when you stepped in?

**[1:34]** Well, when I joined Sun Microsystems way back in 1996, it was about the same week that JDK 1.0 was officially launched. So, it went from alpha to 1.0. and you went through all these 27 [laughter] releases and all these in between things happening. so Java 27 is what we can call a feature release, not a long-term support or the short-term support. So we have this yeah these names which are a bit controversial now. practically what who should care about a nonLTS release like this one and who's fine waiting for 29?

**[2:15]** Yes. So, everyone should care about each release. as you quite rightly point out that this is not a long-term support release that actually doesn't really have any impact on people other than if you're looking at support and maintenance, maintenance in particular, so updates.

**[2:34]** So what people will do is that to give you the background, Oracle decided that they would make certain versions long-term support release ones. So they would have extended support and maintenance. that was because shifting to the six-month release cadence meant that it was impractical to offer extended updates for all versions. So what we've seen is that Oracle started this all of the other distributions of OpenJDK. So Azul's Zulu distribution, Corretto, Temurin and so on have all aligned along the same thing. But the important thing to understand about this is has nothing to do with OpenJDK. All OpenJDK releases are the same from the point of view of how they're delivered. It's only the binary distributions that you get to install on your machine which are long-term support or not long-term support.

**[3:23]** From that perspective, people will think, okay, if you're looking to deploy an application and you don't want to have to change the version of Java, which might potentially impact your particular application because if there are changes made, things get deprecated, things get removed and so on. If you're, you know, don't want to bother about that, then using a long-term support release gives you that sort of stability where you can use the same version for an extended period of time, like sort of three or five years maybe.

**[3:50]** Mhm. But if you have a good flow and you have a good test approach and you're safe to deploy every six months a new version of Java, you can just do that.

**[4:01]** Yes. If if you have a CI/CD pipeline where you're doing frequent updates and you have the capability to do the testing that's the most important thing doing the testing against new version and then deploy it with that version especially if you're doing you know containerized images then yes that will all work very nicely and I think it's it's as I sort of said everybody should be interested in feature releases and even if you're not going to deploy it what you should be doing is doing again testing you should be testing against new versions as they them out so that rather than waiting two years and having four four sorry four updates or versions worth of changes to then look at how it impacts on an application if you do it every six months just run your test suite see what happens if anything you know turns up then you're more prepared for the next LTS release so everybody should be you know interested in each feature release

**[4:55]** Maybe we should talk shortly about what's happening between those six months because you have these new versions,

**[5:04]** But you also have new updates and something happened in August. So, normally we have every 3 months a new build of each version which is still supported which contains security fixes, bug fixes, a lot of improvements. Not really.

**[5:22]** Yes. I mean as as you quite rightly say we have four scheduled updates not releases four scheduled updates for Java every year. So this happens in January, April, July and October. And what those contain as you quite rightly point out are primarily there's the security patches. There's also a number of bug fixes and then there may be some minor changes in terms of performance improvements and things like that but they won't contain sort of features or anything like that. So it's purely about updates that have bug fixes and so on. And yes, so we have four of those every year up until now. we've had the capacity to include emergency updates if that has been required. So if there was a security issue that was discovered and it had to be addressed really quickly because it was a zero day vulnerability, we had the ability to do that.

**[6:14]** We to my best of my knowledge, we've never actually issued a security update out of bounds. Java is a nice secure platform. But what we have seen in the last few months is that this whole idea of claude mythos and AI is now becoming an issue from the perspective of AI being used to identify vulnerabilities in software. And then more importantly really is creating exploits that can use those vulnerabilities. and it's becoming very sophisticated in terms of taking individual vulnerabilities and then chaining together different things to create an exploit that will actually do some damage. So the decision that's been made is that we are going to move from a 3-month cadence for updates to potentially monthly. so we actually saw in August that we had an update.

**[7:11]** Though July was the scheduled quarterly update and then we had an update in August which was only security changes. So these monthly ones are going to be just security patches between the scheduled quarterly updates. Quarterly updates still include the bug fixes, maybe improvements in performance, but we may see more of these monthly security updates. they're called CSPUs, critical security patch update in Oracle terminology.

**[7:42]** And again, there's been an announcement on the Oracle blog from Donald Smith, who's product manager for Java SE, and he said that next year the intention is to move to a monthly cadence for up for the security patches. As I say, we may see months where we don't have one because there's no security patches which need to be delivered, but it's expected that we will see a more frequent cadence.

**[8:07]** You have this state the state of Java, it's called the Azul report. are you seeing that companies are moving faster to newer versions?

**[8:17]** Again coming back to the LTS thing, most people are sticking with the LTS. If you look at the results we got from our state of Java report, you'll see that there are distinct spikes in terms of usage with the LTS releases, there's some usage in between because, as we already said, if you've got a CI/CD pipeline that allows you to do that, some people will deploy.

**[8:40]** A lot of people are quite conservative about which versions they use. even with LTS they may select 21 JDK 21 as their preferred version because it's one behind the current LTS and they feel that may be more stable. Whether that's true or not I think that's open to debate.

**[8:59]** Certainly the people from OpenJDK would say that's not true. but a lot of people feel that JDK 21 as one behind the current LTS is the one to use because of you know perceived stability issues

**[9:12]** Which I as a developer find a bit of pity because you have all these improvements in the language and what you can do as a developer so you want to use the latest thing of course.

**[9:22]** Absolutely. Yes.

**[9:22]** Okay. in 27 we have nine JEPs so Java enhancement proposals. there are two I think we should highlight because they are giving you better performance right so we have comp compact object headers by default and the G1 default garbage collector let's start with JEP 534 so that's compact object headers by default what has changed there so this was a an interesting thing that they introduced a while back which was the idea of reducing the size of an object header from 64 24 bits to 32 bits. it was something they'd kind of been working on for a while and previously they they'd introduced it as a preview feature. They they'd let people play with it. Then it had become a full feature, but in order to use it, you still needed to turn it on using a command line flag. What they've now done is they've said because people have had long enough to test it, nobody's found any significant issues with introducing because it's a big change the size of the object header and the way the object header works inside the JVM. they needed to allow plenty of time for people to sort of run their applications

**[10:38]** On it and make sure it didn't break anything. So they're happy with that now. And so what they've said is that rather than having a command line flag to turn it on explicitly, it will be the default. Yeah. it's quite a good idea because it's a very good idea because it actually introduces some some performance benefits as you said. So if you look at the JEP itself that describes this they quote on the SPECjbb 2015 benchmark that they get a 22% reduction in heap space and an 8% reduction in CPU utilization. So that's significant. I mean especially a 22% reduction in heap space usage that's nearly a quarter. So if you've got applications that use a lot of small objects, which frankly most applications tend to do, this can have a significant impact on how much heap space you actually need. and the CPU utilization again is a sort of side benefit of that from the way that the garbage collector works.

**[11:35]** Mhm. I find these numbers amazing because you said, yeah, we're going from 64 bits to 32. So it's only 32 bits that we gain. But then if you think about the number of objects that you have in memory. So it's it's amazing that it has such a big impact.

**[11:54]** Yes. Well, yes. That's the thing, isn't it? Because if if you start thinking you create millions of objects,

**[12:00]** That's one word, 32 bits, one word per object. So millions of objects becomes millions of words and a word is four bytes. So you end up with four you know 1 million objects becomes 4 million bytes or four megab megabytes.

**[12:17]** Mhm. is this a change in the runtime meaning I have a Java 8 117 application a jar file which was created many years ago maybe if I now run this on this new runtime will I benefit from it?

**[12:33]** Oh yes yeah because this is the internal data structures that are used by the JVM doesn't have any impact on the code that you've written. It's the way that the JVM [clears throat] identifies where an object is in the heap. So you have an object header that gives you information about the object including its address in the heap. And so they've managed to reduce that as say from 64 bits to 32 bits. it's it's quite an impressive thing to be able to do because of course you know it's it's just the [clears throat] internal workings of the JVM

**[13:05]** Are quite well but they are very complicated. So it's it's impressive they managed to do this.

**[13:11]** Yeah. And

**[13:11]** Normally we see the reverse normally get bigger rather than smaller. [laughter]

**[13:16]** And it's also impressive that you can run your old application and you will see a reduction in your cloud cost or you can maybe reduce the number of servers that you need because you have less memory less less CPU that you need.

**[13:29]** So by just upgrading the runtime

**[13:32]** Yes I'm I'm not sure you would see less instances perhaps. I think what you'd more likely see is that you could potentially provision less memory in a given instance because you're reducing the heap size. As I say with the spec JB that was a 22% reduction in heap space.

**[13:50]** So if you're running let's say an 8 GB heap, you might be able to reduce that to 7 6 GB possibly. but you know it's it's that sort of size of reduction.

**[14:03]** Yeah. And it's something as a developer, yeah, we don't care because it doesn't change anything in the language. But it's for the DevOps team if they are able to upgrade their environment with existing applications, they immediately have some benefits of it.

**[14:18]** Exactly. Yes.

**[14:19]** Okay, good. any reason you should not do this? It was tested. I think it the fact that they make it before it means it's stable.

**[14:31]** Yeah, exactly. So the fact that it is now the default there's there's no reason to turn it off unless you saw some regression against your application but I'd be very surprised if you did see that simply because of the amount of testing that has been done how many people have run applications on that.

**[14:47]** But yeah by default it's on so use it.

**[14:50]** No great another one is 523 make G1 the default garbage collector in all environments. again a change of this was already there but we're making it default.

**[15:03]** Yeah this is more related to the way that they've improved the way the G1 collector works. G1's been around for a long time now. but the G1 collector was always has been the default on the server side since JDK 9. So if you're running a two, if I remember rightly, if you if you're running on a machine which has two or more cores and two or more gigabytes of memory, it's considered a server-class machine.

**[15:31]** [laughter] my Raspberry Pis.

**[15:35]** Well, yeah, pretty much any machine now is a server-class machine. So it would use G1 by default for small machines where you had a single core and less than and this is the specific number 1,792 megabytes of memory which I have to try and figure out what that actually it's not 1 GB it's like one and a half or a bit more gigabytes. Anyway, if you got less than 1,792 megabytes and a single core stroke CPU, then by default, the JVM will use the serial collector because it was more efficient in that constrained environment. What's now happened is that G1 has improved in terms of various internal things that they've done with it and they've managed to now decide or they have decided that in all situations G1 will outperform other collectors by default. So we're no longer going to use the serial collector.

**[16:35]** Mhm. the whole garbage collector thing I think we talked about it in the previous podcast too is when I joined Azul I had been doing Java development for 15 years. I never cared about garbage collector. It was there. it worked. And one of the first posts I did as as an author for Azul was write about all the different ones and then even additional ones like the C4 in inul prime zing.

**[17:01]** Why should I care as a developer what garbage collector I'm using? as a developer you probably don't really if if you have nothing to do with the deployment of your application then you shouldn't consider you the garbage collection algorithm at all. and this is one of those things where when we talk about performance what you don't want to try and do is write code with the idea that you're going to help the garbage collector unless you're running in some very niche specific applications. There are what are called zerogc frameworks that people write if they're doing things like trading systems where latency is absolutely critical to how the system runs.

**[17:41]** But for normal applications, don't even consider how the garbage collector works or what it's doing for your application. Just write your application based on what the business logic is and what you want to do in terms of your application code because the G the garbage collector will handle that for you transparently. And like you say that there are all sorts of different algorithms available. We've seen that the sophistication of those algorithms has become considerably better over time and we've moved away from things like the concurrent mark sweep collector that was in earlier versions of Java that's been deprecated and yeah in certain incremental CMS has been removed. and we now have G1 which is the sort of general collector. You've got ZGC or ZGC depending on which side of the Atlantic you're on. And you've got Shenandoah, you've got C4 from Azul in our prime product. and these are more sophisticated collectors because they're much lower in terms of latency or if you use Azul's C4, it's doesn't have any latency associated with garbage collection pauses. So there

**[18:50]** There's a lot more to consider there but it it's down to the DevOps people who should consider that rather than developers.

**[18:57]** Okay, good. then a few JEPs related to security. So JEP 527 is the post-quantum hybrid key exchange for TLS 1.3. That's too much complicated words in one sentence. So you need to explain to me. [laughter]

**[19:14]** Yeah, it sounds great, doesn't it? post-quantum computing science fiction.

**[19:19]** It does. Yes. Well, it's it's interesting because of course you know we talked at the beginning about AI being an having an impact in terms of security from the ability to identify vulnerabilities and develop exploits. And we're seeing that more modern technology is also having an impact from the cryptographic side. And so we've we've had the development of quantum computing for a while now. And because of the way that quantum quantum computing works, it's able to take some of the algorithms that we use for standard cryptography which are based on sort of mathematical problems where you're factoring very large numbers. for standard machines where you're just using ordinary processes, this can take computers like you know thousands of years if you're using a very large key of like many many bits or 2,000 bits plus it. It's unrealistic to expect people to be able to break the encryption on those types of messages. But with quantum computing because of the way it works and I must admit I don't despite a degree in physics I don't understand enough about quantum computing to give you a

**[20:26]** Description of how it actually works. But the net effect is that you can break standard mathematical algorithm encryption in much shorter time. So we're talking about you know hours or even minutes. So the that leads to a problem because what we're also seeing is harvesting of data and I was reading about this related to quantum computing which is that quantum computers are available but they're not sort of general generally available like if you go on the IBM cloud you can actually provision a quantum computer to do work for you but they're very expensive and they're very limited in availability. But what hackers are doing is rather than saying well yeah we can use quantum computers to break existing crypto cryptography what we'll do is we'll we'll harvest data that we can find from people that's encrypted store it and then at some point in the future we could use quantum computers to then decrypt it. So the idea of this JEP is to introduce quantum resistant algorithms specifically in TLS which is the transport layer security that's used for you know passing messages back and forth between

**[21:39]** Web servers and browsers and so on. So what we're doing is introducing the ability to harden that cryptography so that quantum computing doesn't enable it to be broken very quickly now even though quantum computing isn't readily available so that people can't then harvest that data and break it later on. So it's it's thinking about the future of security. It's a very good idea.

**[22:02]** Mhm. So it is really science fiction. We're already preparing our data for the future so that it doesn't

**[22:08]** Exactly. Yeah. There's been a couple of JEPs in previous versions where there there's been some of the post-quantum cryptography side of things, but this is specifically around the TLS

**[22:17]** Algorithm.

**[22:18]** The only thing I know about quantum is the more you know about quantum, the less you understand it, something like that. [laughter] So

**[22:26]** Yeah, there there's definitely elements of that because it's it's the whole idea of existing in more than one state at the same time. So you can have a bit which is both zero and one at the same time

**[22:38]** And then it's only when you look at it because this is the observability thing. So Heisenberg's uncertainty principle the act of observing something changes what you're observing as cat is the other thing.

**[22:51]** It's too complex to fit in my head. So I will not try to [laughter] to do that. the next is 538 PM encodings of cryptographic objects. third preview. So also the previous one is a preview feature. I don't know yet.

**[23:06]** No TLS is a final feature

**[23:09]** Is a final feature. And this is the third preview of PM encodings. So that's about files where you store secrets.

**[23:17]** Yeah, it's about encoding and decoding cryptographic keys, certificates and certificate revocation lists. and essentially the idea of moving between object representation and what's called PM or privacy enhanced mail transport format. So it's this idea that if you want to send these things through email, you need to be able to convert them from the object representation into the encrypted standard for sending them through email. And so it just introduces that as a preview feature.

**[23:51]** Mhm. being previewed.

**[23:54]** I guess they're still looking to see whether there's any feedback. there are some minor changes in the API but

**[24:03]** Yeah so if there are minor changes that means that people were testing it and trying it out gave feedback and it's not finalized yet.

**[24:11]** Yes. So if I look at the changes here, there was one interesting change which was that they moved the primary PM class from being a record which it was before to being a normal object or a normal class I guess. and the reason for this because I thought to myself, oh, why did they go from being a record, which is a nice new feature, to going back to using an object. And the reason for this is that they've included a new constructor which accepts a base 64 encoded content as a bite array. And because of the way records work, they couldn't do that using the structure of a record. So, it had to go back to a normal object. So, it makes perfect sense. but it was just interesting to see how that works in reality.

**[24:57]** Something I've done also when writing code. So you start with a record because you think this is a nice small object. I'll never change it, nothing complex to construct and then you think, yeah, but I need a different way to give it this data and then you end up with a story like this. Yeah. Okay. Good.

**[25:14]** Yeah. I guess that comes back to the whole idea behind records which is really just data carrying object and

**[25:21]** If you think oh yes this is just a data carrying object use a record but then you discover oh actually it's more of a proper object where there is both behavior and state involved and so you do need to switch back to using an object or a class.

**[25:34]** Mhm.

**[25:35]** The next one is about observability. So the Java flight recorder there we have JEP 536 JFR in-process data redaction. so JFR is a tool to see what's happening in your application. How many objects you have? It's debugging tool. What is the improvement with this JEP?

**[25:58]** Right. So this as you rightly say Java flight recorder is a way of collecting data from the JVM so that you can see what's actually happening inside when your application is running and that can be used in two ways. It can either be used at runtime so you can have observability of a running JVM process. You can see exactly as you said how many objects your application has generated. You can see various things like number of threads. You can look at thread information. all of the sort of internal workings. You can either you can do that in a running application or you can create like a dump at the end of the run and if you have a problem where you're trying to debug and something you know crashes or whatever you can do a postmortem analysis of that to see what happened when your application actually stopped that that's very powerful very useful. The idea behind this JEP is actually more security focused. So it's not really changing the performance or making Java flight recorder any better. What it's doing is it's saying that the kind of data that gets collected is things like

**[27:02]** Command line arguments, environment variables and system properties as well as all the internal data that you have in the JVM. And the problem with this is that can contain sensitive data. So you've got things like secrets in command line arguments, you got access tokens in environment variables, passwords in system properties and so on. And what they're doing now is they're saying, "Okay, that's a little bit dangerous to record that into a JFR file because it could be then exposing that sensitive information."

**[27:30]** What you can now do is you can say, "Well, I don't actually need that to do my debugging." So, what I'm going to do is I'm going to redact that information from the log that's generated by Java Flight Recorder. It just basically means you could turn off that information and therefore it's not included and makes your flight logs safer especially if you wanted to share them with other people.

**[27:53]** Yeah. Something you have to do with an argument or with within your code.

**[27:57]** Yes. Yeah. There's arguments which says you know which things you can redact.

**[28:01]** Okay. Good. and then we have more preview features or incubators like JEP 531 the lazy constants. we already talked about this as stable values. Did it was it renamed or what happened there?

**[28:18]** Yes, because they started out as yeah was stable values, wasn't it? Now they become lazy constants. and that's making it more precise in the definition I suppose because you know they're saying constants rather than values. it's it's about deferred initialization is the thing because we have final in Java. So you can mark a field as being final. That means that its value is set once and once only unless you use reflection deep reflection will allow you to change final variable which it shouldn't really but

**[28:54]** The yeah that's actually going to change in the future. I know that there are plans to make final truly final.

**[29:01]** But the important thing about final fields in your code is that where you have to initialize them is basically in the constructor or in static initialization. But you once the object is created, you have to have initialized those final fields in order for your code to be valid. And there's situations where people would like to have something which is going to be have a value that's set only once, but they want to have more control over when the value is actually set. And so it's outside of the constructor or static initialization. So this is the idea behind lazy constants. it's back for a third preview. three small changes to the API, but nothing that was significant. So it's it's just about giving you greater flexibility over when these values are initialized.

**[29:54]** Okay, good. the next one is primitive types in patterns instant off and switch is 532. one of the returning things fifth preview. We have seen already a lot of improvements in instance of and the switch case things and you have some amazing talks with the when example that you have given. what is the change here?

**[30:18]** So there's actually no changes between 26 and 27. So it's it's returned as another preview feature so that people do have still have time to continue looking at it and reporting any issues that they find with that. it. As you say, I've I've I've done some presentations on this and one of the ones I'm doing at the moment at conferences is my Java puzzlers talk and that is where I look at how to use things like primitives in patterns for instance of and switch

**[30:50]** And there's a couple of situations where I found that the way that it worked didn't actually work. [laughter] So, so it wasn't correct if you like. So there was a bug. so I've reported that through the Amber mailing list and that was one of some of the things that they they've kind of changed in the modifications in previous versions where

**[31:15]** Especially if you're using both primitives and reference types in switch. There's what's called pattern dominance that you need to be aware of in switch. because if one pattern dominates another, so let's say you if you've got one type which dominates another. So if you had a case for object and then you had a case for string, everything will match against case object because you know everything is an object. [clears throat] So it will match against object. You would never get to string. So object is dominating string. Now you can if you start mixing reference types like the integer wrapper class with primitives like bite and short then the way pattern dominance works is quite complicated because one is a pattern for a reference type and the other is a pattern in terms of the primitive and then we have to have a runtime check to see whether the value that we have will fit into that primitive or not. So it's I don't envy the people who have created [laughter] this feature to get all the different cases I shouldn't say cases all the different situations right is very difficult

**[32:20]** And then they have people like you testing what they have done [laughter]

**[32:24]** And you mentioned project amber so project amber is about a lot of small improvements everywhere in the code to make it simpler

**[32:34]** Yes it's amber is focused purely on the language features things It's like the switch expression is part of project amber. Primitives in switch u pattern matching all of those things are part of project amber because they're they're specific to the syntax or the language.

**[32:55]** A lot has already been done and this is I think one of the few things that keeps hanging. probably maybe related to project fala where we will talk about later where we have the same thing happening with yeah primitives becoming objects or vice versa we'll see structured concurrency is in the seventh preview is 533 it's about virtual threads and improvements there

**[33:23]** Yes it's technically it's not virtual threads you can use platform threads if you want to with is it's it's about making concurrency easier. it is part of project

**[33:35]** Loom

**[33:35]** Like that. [laughter]

**[33:36]** Yeah, I've got another talk on this that I've been doing recently and it's it's interesting to look at the history of how we've improved concurrency in Java over the years because obviously we started out with JDK 1.0 where we had threads and we had very little that we could do in terms of coordinating more than one thread. Yeah. Then we moved on to the concurrency utilities in JDK 5. that gave us things like semaphors and rewrite locks and mutxes and things like that. Higher level constructs. and then we moved on to JDK 7 where we had the fork join framework which was great if you can you know split a task into subtasks and do that recursively. have a set of threads work on that and do all of the coding where you don't actually see all the stuff that sits underneath. Then we had JDK 8 where we had the idea of parallel streams that we introduced. so there's been a number of things that we've changed over time and structured concurrency is really good because it allows you to have multiple t subtasks happening and where we would do things like a callable and a future in

**[34:46]** In code previously. What we're now doing is we're having a subtask and we're forking that. But then we join those tasks together within a structured concurrency block. And because structured concurrency implements the closable interface, you have a try with resources block. So you know that at the end of that block, you either have completed everything or you've thrown an exception. So it's a nice tidy way of doing things that have subtasks. And even within that idea of subtasks and the join, if you have subtasks that fail, then you can essentially interrupt other subtasks. So you don't sit around waiting for these subtasks to complete while you know that you're never going to be able to generate the result at the end because you've already failed. So there's there's a lot of good stuff in that. I do like structure currency.

**[35:37]** It's a tidy way of handling this type of situation.

**[35:42]** If it's such an improvement, why are we already on the seventh preview? Why is it not finalized?

**[35:49]** I guess I would say because concurrency is never simple. [laughter] I think it's more to do with just fine-tuning the API. There were some there were some changes around the way that they because when you create a structured concurrent structured task scope is the class that they use. So for example, one of the changes they made was they changed the way you create a structured task scope because there are two different ways of doing that. You can do exit on shut sorry shutdown on failure or shutdown on success. Mhm.

**[36:26]** So shut down on failure is like I said if if one of your subtasks fails then you basically shut down all the other subtasks. There's no point in carrying on. Shut down on success is the opposite of that where if let's say you've got three different you know price providing web services that you're using and you want to get a price from just one of them. Then you set off three subtasks going ask these three different things to give me a result. when one of them succeeds, you got your result and you don't need to worry about waiting for the other two. So you then can say, "Okay, I've succeeded with one. I can then shut down the other two." So there's shutdown on success, shut down on failure. And the way that you create your structured task scope, the API has changed for the way that you do that rather than doing explicitly. It's just changed. So it's things like that they've changed in the API. and as long as within a thing like this that is evolving, as long as they plan to change the API, I think they keep it in preview, right? So when they are satisfied with this is the API that

**[37:32]** Everyone can and will use, then they make it a final thing.

**[37:36]** Yes. And that's absolutely the point because if they want to make changes like that, so the way that you actually generate a structured task scope or the way that you use the constructor or whatever, if they're not 100% certain that is the definite way they're going to do it, they might change it, then keep it as a preview feature until they're absolutely happy with the API and then set it in stone, if you like, as part of the spec. because before that people know that it's subject to change and so if they're going to use it it's like yep I'm going to use it I have to enable preview features and I have to accept that there may be some changes if I move to the next version of Java and I might have to change my code slightly to reflect the changes in the API.

**[38:19]** Okay, good. And then the next one, I said in the beginning, this is the sixth season of the Foojay podcast, meaning we probably already had 12 times that we talked about the vector API [laughter] because it's in the 12th incubator, but it's waiting for project Valhalla, right?

**[38:35]** Yes. Yes. So the vector API is a good API from the point of view of improving performance because if you look at the way that the CPU works underneath there is these idea of vector operations which are very wide registers and you can load up multiple values into that register and you can have the same operation applied to each of those values in a single clock cycle. So it's the instructions on the Intel processor are things like AVX, AVX2 and AVX 512 depending on how wide the register is and how modern your processor is. Now that's all good. And if you look at the way the JIT compiler works in the JVM, it will do auto vectorization. So if it can see a situation where you've got a an array of numbers and you're simply adding two to each of those numbers and you're just iterating through that array, it will recognize that quite easily and it will go, "Oh, okay. We can auto vectorize that. It will fill the wide register with those values. It will add two to the each of the values in that register in the single clock cycle and it will improve efficiency. The problem that you have is there are lots of situations

**[39:42]** Especially where you start introducing a predicate where you've got an if statement involved that the JIT compiler can't autovectorize that code because it's not smart enough if you like. algic compiler Falcon autovectorizes a lot more code than hotspot does. But there are still situations where if you can write the code explicitly yourself, you can say right this is what I want the code to do. I know it will fit into a vector I can then

**[40:10]** Not auto vectorize it. I can manually vectorize it.

**[40:13]** Yeah. And that's what we will get with this new JEP.

**[40:16]** Yeah. But in this JEP as you said this is the 12th iteration and there are no changes. So they they've kind of reached stability but they're waiting for Valhalla. Yeah. Okay. Which brings us to project Valhalla because in Java 28 to be expected in March. I'll assume that we will stick to the schedule as we've done in all the previous releases. so expected in March 2027 and it was announced that we will see the first preview or incubator whatever of project [snorts] falhalla and what will it bring with this chap 401 value objects and chap 539 strict field initialization. What will this bring us?

**[41:03]** Yes. So, so this is the idea of treating it's the differences between primitives and reference types.

**[41:13]** Mhm.

**[41:14]** So, what we want is to be able to use objects but have the performance of primitives.

**[41:21]** So, code's like an integer, performance like an int is basically the idea behind this. So that if you create an integer which has a value of 10 then as long as you're not changing that then you every integer object which has a value of 10 can be treated as a single copy of that and rather than creating multiple objects of in type integer all of which have 10 in them and filling up your heap with that what you do is you create a single one and the JVM is aware that okay this is an object which stores 10 so we can treat it as the same instance and we can use that because again so long as you're not changing it that's the important thing if it's a value rather than a variable hence value types

**[42:14]** It's something that we can make much more efficient in terms of the code that sits underneath. So we've introduced this idea of value types and there is a new keyword that will be introduced which is value. So you can actually have a class which will be marked as a value.

**[42:30]** Okay, I found somewhere. So it's 12 years of work, 179,000s of lines in,800 files that were changed. Who's doing the pull request review there? [laughter]

**[42:48]** That's a good question.

**[42:50]** I mean, who's doing this? Is this community work? Is this the core Java developers?

**[42:59]** This is coming from the Java engineering team at Oracle. So Brian Goetz is the architect for the Java language and he's the one that's leading this. There's obviously a lot of other people involved in this as as part of the project, but as you say, it is a very big piece of work and it will have a substantial impact on how we write code and what code looks like as we move forward because there's there's other aspects to this which is not going to be in JDK 28 but in the future there will be the concept of if you have a collection in Java at the moment. Let's say you have a list and you want to store numbers in that list. You have to use the wrapper class. So what you end up with is a list of integer and you create your generic class. So you have list of type integer and you can store integers in it. Now that means that if you've got an array of numbers and you want to store them in that list, you've got to create all these objects which you know it populates objects in your heap and uses up space. So the idea behind Valhalla going forward, one of the other things they will introduce

**[44:11]** Is the idea of being able to do a list of type int so that you can use a primitive rather than a wrapper class and then you can actually put things into the collection as primitives and underneath even though a list has to have objects in it. The project Valhalla because it's using value types will be able to do this in a very efficient way. So there's there's a lot to Valhalla. That's why it's taken a long time. They've gone through I think they're probably on their third iteration now of the fundamental ideas behind it because it is such a big change to the JVM and how the language works. They they've taken a measured approach to developing this and I think that's absolutely the right thing to do. We don't want to sort of rush and go okay this is what it's going to do. put it in there and then people don't like it and it doesn't really work the way that it doesn't deliver all the results that people want. So I think having taken a long time to do this is the right approach and we'll gradually get it and we'll see yes it works.

**[45:15]** If people are interested how this process works. So, OpenJDK lives on GitHub. How can I see the commits and how they keep merging this? And is that a separate repository? Is it a branch within OpenJDK? You have any idea? of

**[45:34]** Often with these larger projects there is a different repository silia like a Valhalla one because they don't want to merge it into the mainline because it's not part of the mainline but they want to they want to take a branch from the mainline so that they can develop against that so that they keep tracking the changes in the rest of Java but it won't be merged back into the mainline until it's actually ready in terms of understanding what's going on there's there's two kind of things that you can look at one is the JEPs because that gives a lot of information about how these things are being developed. So it's interesting when we look at this that the value objects is JEP 401 and the what was it

**[46:16]** Strict field initializ

**[46:16]** Strict field initialization is JEP 539. So that shows you how old the value object one is that it's been around for quite a while.

**[46:26]** The other thing that's really good to look at for this is the mailing list. So, Valhalla has mailing list. You can look at that. It's all public. there's a lot of discussion goes on about how the project's being developed in there. And then occasionally Brian will publish a sort of paper where he's he's got thoughts about how Valhalla works. So, there's there's a project Valhalla on at the OpenJDK website where you got links to the different documents that have been produced.

**[46:55]** Okay. I will add links to all of these in the show notes of this podcast. How will we see this as developers? Is this a lot of new functions that we can use or is it runtime stuff?

**[47:09]** No, this is going to affect the language because as I say, we'll actually see with value types that we got a new keyword and it will be similar to records in that we will think of how we're creating classes slightly differently to the way that we have before. So we we've obviously got the class records and enumerations are a special type of class. Value types are going to be a special type of class in the same way but obviously different.

**[47:35]** It will be a preview in 28. Is it too then too fast to assume that we will see something land in the next LTS 29.

**[47:44]** I would be very surprised [laughter] if they finally given how long it's taken to get to here. I would be absolutely amazed if they suddenly went, "Yep, 401 preview feature for one release and then yeah, it's it's final in JDK 29." I could be wrong. I mean, cuz how long did it take off in No, I think it was two releases that project loom, so virtual threads,

**[48:07]** Even though that had been, you know, hammered a lot,

**[48:10]** Which was also a big big change.

**[48:12]** Big big change, but even that took two releases to get to final. So I can't imagine that they will make it final after one just one preview.

**[48:22]** No, hopefully it won't be 12,

**[48:24]** But it probably won't be one.

**[48:26]** At some point it was defined as we will have LTS every 3 years then it moved to two years maybe one year because we will have this month releases and

**[48:39]** Any rumors about that? I can't see that happening either because the whole point of having the LTS releases is they don't want to be trying to support too many versions of Java at the same time over the different distributions

**[48:53]** Because of the practicality of that.

**[48:55]** If anything, the introduction of a monthly cadence for security updates

**[49:01]** Almost makes it a situation where they might want to go back to a three years between long-term sport releases. The only reason I say that is because the amount of work that we're having to do to keep up with all the updates. and when it shifts to a monthly cadence, that just increases the number of

**[49:19]** Builds and tests and so on that you've got to do.

**[49:22]** I'm not saying that we'll move to a three-year release cadence. Again, I doubt it will. I think we're we're fixed on two, but it's definitely not going to go to one in my opinion. H I don't know the exact numbers but I know like in Azul so there are builds for Windows, Linux, Mac still some 32 bits, 64 bits, then old versions of Linux, then all the distribution it's in the thousands of tests has been executed and they now moved from three to one month. So I can imagine that quality control is has exploded.

**[49:55]** Yeah. And in fact, that's that's another point because there's another JEP which is JEP 541 and that's deprecating the macOS/x64 port.

**[50:05]** And so that is directly tied into what you're saying because you know Apple don't [clears throat] make MacBooks anymore with Intel processors in them and they haven't done for quite a while now. So the idea is that would you really want to have version 29 or 30 or 31 on a very old MacBook? Yeah, probably some people would, but the demand for that is quite small. So, they made the decision that they're going to deprecate that port. There's been quite a lot of discussion about that in various aliases that I've seen,

**[50:36]** But the idea has been to deprecate that port because it's one that is just too much work

**[50:42]** For the project. So, they're just saying, "Okay, we're not going to support that one anymore. We'll deprecate it." If somebody wants to step up and still maintain that, they can,

**[50:52]** But it's unlikely anybody would because of the amount of work involved.

**[50:56]** Yeah. What I also read is that Apple will also stop building their own operating system for Intel machines. So, which is a bit of a pity because Mac makes amazing hardware which keeps working. [laughter] I have a very old Mac still running. so it's a bit a pity that Yeah. It's not only Java, it's also the operating system itself which will move away from Intel.

**[51:20]** Yes. Yeah. I actually I recently just sold my very first MacBook which was one of the ones with the it wasn't the unibody. It was the one before the unibody which had the removable battery. You could just take the battery out and put a new one in. and that was still working fine. obviously like you said, I mean, I think the it was like Snow Leopard or something like that was the last operating system that you could install on there,

**[51:45]** But yeah, it still worked fine. I sold it. Somebody wanted it [laughter]

**[51:49]** For spare parts maybe.

**[51:52]** Okay, good. I think we had everything which is

**[51:55]** No there's one more JEP that I really want to highlight which is actually coming in JDK 28 and that's JEP 540 because finally there is the idea of putting a JSON API into Java.

**[52:10]** Yeah, I read about it. So that means that all my projects where I have three different dependencies like Jackson and the other ones and JSON and what is it all I will not need them completely or is it the minimal support for JSON? What should I expect from it?

**[52:28]** Yeah. Yeah. I wouldn't hold breath. [laughter] This is going to be one of those things that I don't know. I mean I probably have rather negative view of JavaScript. because of like what I've seen in the past and if you look at some of the things that you can do in JavaScript, I know there's examples of what you can bad things you can do in Java as well, but there are some really some real howlers of what you can do in JavaScript. but the problem is that there is no formal specification for JSON.

**[53:00]** There's a IF RFP isn't there or RFC, not RFP, RFC request for comment. So there's a sort of specification for JSON under the IETF. but the problem is that the existing APIs so like you mentioned Jackson and GSON they don't they're not too strict about how they impose this parsing of this and in fact you can have the idea of not what's it lenient mode you can have a lenient mode for parsing the JSON and the JSON API that they're going to put in JDK 8 is going to be very strict. So, it won't pass stuff that doesn't fully match the spec as it exists. And there there's even some situations where it's it's more strict than the spec or something like that. But

**[54:00]** It's going to be very interesting to see how that develops and whether it even stays because there's been so much back and forth about should we put a JSON API in Java.

**[54:11]** It will be a preview, I guess.

**[54:13]** Yeah. Oh, definitely.

**[54:16]** So, it could become the story like we had with I think it was string templates. We had a few previews and then it actually was abandoned. So, it was removed again because

**[54:26]** There was no agreement.

**[54:28]** Yeah, that with the string templates. Yeah, but there was some back and forth on that and then they eventually decided they didn't like the way that it worked so they withdrew that which is really good because it shows the power of preview features in that if people don't like it and the developers, you know, finally come to the conclusion that it doesn't work in the way that they want it to, they've withdrawn it and go, "All right, we'll have another go at that." And I think we'll be interesting to see whether that happens with JSON because there has been a lot of reluctance on the part of the Java the OpenJDK developers to include a JSON API and I think because of the problems you have like I said with the lenient mode and not really having a formal specification for JSON

**[55:13]** Whether that really works in practice we might get enough sort of push back that people go yeah well let's just keep on using JSON or J and not bother about putting it in Java.

**[55:25]** Okay, good. Yeah, we'll see what happens. So, that's for the next podcast in March. what is the one thing we should remember from the 28 release?

**[55:34]** I think the one thing or two things that I would take away is the as you mentioned at the beginning the performance improvements. So, the idea of using G1 as the default collector and the compact object headers. I think those are the two most significant changes. Although compact objectives is definitely more significant than G1 because most people won't see that because they're not using a single core machine with less than 1792 megabytes of memory but compact object headers definitely.

**[56:03]** Okay, good. That's the one thing we want people to remember and they can read more also on the Foojay website because Hano will also post full go through with all the chips and we may expect more posts. Also an invite to everyone. if you have any article that you would like to contribute to Foojay authors all the time and we welcome all new content. Okay. that's also a wrap for Java 27 this podcast. So smaller heaps, smarter garbage collector and a first step into post-quantum security science fiction. And also a whole set of previews inching closer to the finishing line. And with value objects landing in Java 28 next March, project Valhalla is also about to reward more than a decade of patience for everyone.

**[56:54]** A huge thank you to Simon. Thank you for joining this podcast and explaining everything, making sense of all what's happening inside the OpenJDK. And thank you for listening. Of course, if you enjoyed this episode, please subscribe to the Foojay podcast on YouTube or in your favorite podcast app, Spotify, Apple Podcasts, and lots of other channels where you can find us. see you next time for the next podcast and of course see you Simon next time for the release of Java 28. Until then, keep upgrading your runtimes and keep running on the latest version.
