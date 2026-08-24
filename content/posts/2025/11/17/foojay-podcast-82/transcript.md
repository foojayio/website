**[0:00]** Let's dive into some of the ongoing OpenJDK projects.

**[0:04]** Give me a OpenJDK.

**[0:10]** Hello and welcome to another episode of the Foojay podcast. Today we are diving deep into some of the most exciting developments happening within the OpenJDK and Tornado VM projects. At the DevOps and JFAL conferences, I spoke with several speakers and visitors about some of the major themes that are shaping the future of Java development. My first guest is Morris Albriter from the Spring Engineering team. He provides us with more insights into project Leiden and how it's improving Java startup times through ahead of time compilation and profiling. We'll learn how Spring Boot developers can already take advantage of these improvements today. Next, we'll hear from John Ciserelli about performance optimizations, the evolutions from x86 to ARM 64 architectures, and how OpenJDK projects bring improvements to the JVM itself at levels we couldn't achieve before. Then Bal Krishna Rahul will guide us through the world of vector databases and explain how Java's vector API from project Panama is perfectly positioned for AI use cases despite its development beginning years before the current AI boom. And finally, we'll meet

**[1:26]** Some of the team members from the University of Manchester working on Toronto VM. They explain how Java developers can now harness the power of GPUs for AI workloads running large language models in pure Java without leaving the Java ecosystem. They also explain the connection between Tornadov and the OpenJDK project Babylon. A little warning if you're watching this podcast on video. The camera went a little crazy on the focus from time to time. So my apologies and no, you're not drunk. Let's get started.

**[1:58]** My name is Moat. I work in the spring engineering team particularly on spring boot and I'm also the team lead for startup spring.io Oh, so the website where you can create your spring projects.

**[2:08]** The most promoted website ever by George.

**[2:11]** Yes, it's I think second favorite place on the internet or something like that.

**[2:15]** Oh wow.

**[2:16]** I have no idea what the first one is. I guess production

**[2:19]** And you're here as a speaker.

**[2:20]** I'm here as a speaker. Yes. I did a talk together with Anna Maria from the Java team about project lighten together with Spring Boot.

**[2:28]** Yeah. Project Leiden. So the goal is faster startup ahead of time compilation with what gravium is doing.

**[2:36]** What is the status of this project?

**[2:38]** Project lighten has the want to fix the problem that Java is slow to start up essentially. And the idea is that you can shift stuff which you normally would do at runtime, you would do before that like in a you would for example collect profiling information compile code ahead of time essentially while doing a training run and then you can use this cache and enable it later when you're running and this gives you better a better startup time and a little less memory usage. And this profiling is analyzing which code is used and how code is used correctly.

**[3:14]** Right? So usually when running a JVM the it observes the code and all the code path and stuff and then it collects profiling information and then the C2 compiler uses or the JIT compiler uses this information to create like highly optimized code. And this is the same when you're doing a training run

**[3:34]** And in the training run you collect that stuff but like not while running in production but while you're doing your training run and then this data is captured and stored inside the cache. So in production you can reuse that data later

**[3:48]** And this is a bit the main difference of the full ahead of time compilation where you don't have the Java code anymore and not compile it at runtime. Correct.

**[3:56]** Right. so people tend to compare like Gravium and Project Lighten, but I think they are not really like comparable because like Gravium also gives you like a really nice startup time even more than Project Lighten, but Gravium has other benefits like reduced memory consumption and stuff like that because they analyze your application, do a closed world analysis and then only put stuff in the native image that actually needs to be there and they can aggressively ly optimize that stuff so you get like even better startup numbers. But of course in I mean in software engineering there's always a trade-off. Yeah. And the trade-off on the Gravium side is that you may have problems with third party libraries but the Gravium team is heavily working on that one. And with project lighten it's more like you need to think about the training run but otherwise it has like the same semantics like a normal JVM. So you can do reflection you can do all the other stuff. So, it's not restrict restricted, but you don't get those crazy startup performance numbers, but it's still like

**[5:00]** Really impressive.

**[5:01]** It's really impressive and it's ongoing because this project is not finished yet,

**[5:06]** Right? The project lighten is currently a working project and they take like working units which are already finished and put them in general available JDKs. For example, Java 24 has this AOT cache and they collect with Java 25, they now collect profile information, but this whole compile code ahead of time and put it in the cache is still in early access phase.

**[5:34]** And yeah, that's a bit how the JDK evolves. You have this

**[5:38]** Long running projects and then you have the JPS,

**[5:41]** Right,

**[5:41]** Which are building blocks to achieve the goal of the project. Correct.

**[5:45]** Right. Yeah. So there is no clear answer yet when we will have a project like which is finished but along the road we get these improvements

**[5:54]** Right. Yeah.

**[5:55]** Is this something which can also already be used if you are a spring developer?

**[6:00]** These improvements. So with Spring Brute 3 I think 33 or something like that we added support for the CDS. So class data sharing which was the predecessor of AOT cache. And it turns out that all the stuff we did for the CDS is also applicable to IoT cache. So there is a mode where you can extract your big Uber jar which you would with which comes out out of Maven package or gradal build and then you can extract that jar into a format which is like AOT cache compatible and then you can run that. So the thing the workflow normally works like this. You build your Uber jar, you extract the Uber jar into an AOT cache capable format and then you do your training run and then you use this extracted jar also in production.

**[6:48]** So, for example, put it in a Docker container and point it to the cache you created in the warming warm-up phase and then you can use it. And it's the same with line. It doesn't change like the workflow doesn't change. It's only like an a different JDK.

**[7:00]** Mhm. And the training run that means that you run your application and you put the load on it similarly like it would be in production. Correct.

**[7:10]** Right. So, I said there's trade-offs everywhere. And here's the trade-off of Leaden. What they did is the training run should be as close to production as possible to get the optimal benefit. so there are two ways to do it essentially. One way is the easy way and this is like only start your spring application to a certain point and then just stop it so that it caches all the class loading. This already gives you a lot of startup performance like we're talking from 3.3 seconds in my talk down to 0.8 8 seconds or something like that, but it doesn't give you like profile information and it doesn't give you like optimized compiled code for a hot path. But this is the easy way. This is like easily done. The different way is to get your training run close to production.

**[7:57]** For that, you usually need like either great integration tests or you need to somehow mirror production data against your training run. And I'm not sure what your chief information security officer is saying from that idea. So

**[8:11]** Depends on the kind of applications. but this reminds me a bit of what crack is doing. So there also you can take a snapshots. What's the difference between the crack way and the leen way?

**[8:25]** Yeah. Right. So crack is the coordinated restore at checkpoint and it's it already says like it's coordinated. So the application has to do stuff when you take the checkpoint. And one of the trade-offs the crack inventors did is that you need to close all files because this stuff doesn't work on a JVM level but it works on a process level of the operating system which is only supported on Linux and there it's using I think Creo it's called which essentially takes a snapshot of the whole process and you can restore that very fast and it's instantly hot because it captures the whole JIT and all the code cache and stuff like that. But the trade-off is that when you take that snapshot, you need to close all files and sockets and stuff like that

**[9:12]** And reopen them when you restart

**[9:13]** And you have to reopen when you restart.

**[9:15]** So there are some code changes there while with lighten it could be just the same code

**[9:20]** But with the test run.

**[9:21]** Yeah. So the one important goal of lighten is to not change Java semantics. So you don't they don't expect you to change your code. It should work like out of the box without any changes. Yeah. And if you want to use this, you have this training run somewhere in your build process for instance that you have.

**[9:39]** Yeah, you need the training run. But even for the training run, you do not need to change your code.

**[9:43]** Yeah.

**[9:43]** Yeah. Okay. what is the most important takeaway from the talk?

**[9:47]** The most important takeaway I would say is that even now with JDK 25, you have something from Leiden, from Project Leiden, which is general availability. It has the same quality stuff like every JDK release has. You can just enable that with your spring boot application and you get like a performance factor a startoff factor of maybe 4x or something like that. Like it's it starts like four times faster which is just crazy.

**[10:10]** And that's by enabling the preview features or that's just a configuration spring

**[10:15]** That is like only extract the thing do small warm-up. It doesn't even have to be the production warm-up. Just like do the easy warmup

**[10:24]** Until your main method get called actually.

**[10:27]** Right. Yeah. There's a flag which you can pass. just look it up in our documentation and then you get like a startup performance of 4x. This is what I would do.

**[10:36]** Yeah.

**[10:36]** Which can be very helpful if you have a scaling application for instance that you can run additional dockers very fast.

**[10:43]** Yeah. So the reason why people want to have like a fast startup is because they like to compare numbers like every number is good for comparison but like the real operational value is if you do dynamic scaling. So if you get like a lot requests, you want to dynamically scale your applications and then of course they have to start up fast.

**[11:02]** My name is John Czecharelli and I'm here at my first Java developer conference in many years

**[11:09]** And is something going on.

**[11:10]** It seems like there's a lot going on.

**[11:12]** What's what's the main thing that you're seeing?

**[11:14]** Oh well it's all AI. It's all about building agents and how we can how our Java programs can interact with AI can feed into the LLMs can pull things out of the LLM. So, so yeah, exciting stuff.

**[11:27]** And is Java the right thing for you?

**[11:30]** Well, yeah. I think that, you know, LLMs are part of the story, but you know, LLMs can't do everything. They need to offload things to the agents, and you should absolutely be building those agents in Java.

**[11:40]** Yeah.

**[11:41]** You've been in Java for a long time.

**[11:43]** Yes.

**[11:43]** What's the main evolution going on in OpenJDK for you?

**[11:47]** Oh, in OpenJDK, the thing I'm looking most closely at is project leaden right now. you know at I work for Azul we have Azul platform prime and we've been doing AOT profiling and AOT compilation for a long time with our ready now technology and with our cloudnative compiler so it's really interesting to see what's happening in those the warm-up sphere where we're looking very closely at laiden we're seeing you know how we're going to how we're going to interact with it to what extent we're going to uptake it in our builds of OpenJDK versus to what extent will, you know, kind of keep keep doing this thing we've been doing ever since JDK8. So, so yeah, but, Leighton is a thing that's that's very interesting, for me. and then performance, you know, I I'm very interested in both, what's happening at the JVM level and then what's also what's happening at the language level around performance. I'm I'm a performance guy, so that's that's what I'm interested in. and performance is that also impacted by how we evolve like we have the graviton instance ARM based

**[12:52]** Computers

**[12:53]** Does this have any impact

**[12:55]** Yeah graviton is really interesting the ARM 64 I mean you know we've been we as a the Java community as a whole have been optimizing for the x86 architecture for you know many years

**[13:08]** Many many years right and graviton is new and so we're really seeing in JDK2 you're seeing OpenJDK starting to catch up and starting to do some very interesting optimizations that we've had in Azual Platform Prime for a while around loop unrolling and so forth and effectively final to get the code speed up because people you know we ben we do a lot of benchmarking and Java runs slower on ARM 64 than it does on x86. So people look at transitioning their workloads from x86 to ARM because they're going to save all this money and then they do the calculations, right? and they go, "Oh, well x86 costs this, Graviton costs that and so I'll save this much money."

**[13:55]** But they're not factoring in that they're going to actually have to run more compute to handle the same workloads because their Java is going to run slower. At least OpenJDK Java was going to run slower on Graviton. so you know, we at Azul are optimizing the heck out of Graviton. OpenJDK is starting to pick up some of those some of those optimizations that we've had for a couple of years and it's starting to catch up and I'm sure that ARM 64 will be as faster or faster than than x86 very soon. And you mentioned project Leiden and then the products from Azul. Do we see there's a lot of other evolutions like you have to turn VM which already is working a long time on JPU support which is also coming in project Babylon I think. So all these different projects they run for a long time.

**[14:41]** So people who are really waiting for a new feature yeah could be a bit disappointed. But on the other hand it's all stable which comes out of this changes. Correct. Well, as Gosling once said, aggressive innovation is good at the edges, but not at the core. Aggressive in innovation in Java.lang.string would be a very bad thing, right? and yeah, when I'm looking at the JVM level, I mean, of course, I'm a product manager. I'd love to have some shiny whisbang new feature go like, oh my god, that's cool every day. But really, what our customers want is they want rock solid stability. They want reliability. They want performance, right? they want it to just work to work as fast as possible and to deliver the best you know the best experience customer experience the best reliability the lowest cloud costs and the lowest emissions you know and that's that's what they want but yeah there is a lot of stuff happening you know outside of just OpenJDK and prime obviously some turbulence in the growl VM world with the Java announcement with the Java team's announcement that they were kind

**[15:45]** Of disengaging from grow VM M and then you know to turn to VM starting to come up. So there's lots of players in the market and you know and then you know even inside OpenJDK different contributors. It's exciting that Shannondoa now has generational support. so at the JVM level there's you know at these conferences people typically come to hear about fancy new language patterns and frameworks and so forth. You know I operate at the JVM level. There's a lot of exciting stuff happening at the JVM level too.

**[16:16]** If you look into OpenJDK and how it evolves,

**[16:20]** They say Java is boring because the Java tomorrow will be just as stable as the Java yesterday. But what is happening inside is really fascinating.

**[16:28]** It's really fascinating. It really is. Yeah. And we just keep you know we and AI is opening up a lot of things, right? A lot of at a very very fine level. we are now using AI to analyze optimization patterns for the JIT compiler and anal and unearth opportunities for faster code that at a rate that we couldn't have done before. So it's not just about you know Java developers coding in AI. It's about us the JVM people using AI to improve the Java runtime that the Java developers run on. So it's all exciting stuff. My name is Valus Raul and I am here at JFall to talk about vector databases. I did a talk it's called making sense of vector databases. So I talk about what is a vector, how does it work, what is vector search and then we go on implementing from simple to more complex examples using vectors.

**[17:30]** And is a vector database something related to AI and only for AI? Well, it's not only for AI, but yes, you're right. It is related to AI. because of the current AI, vector databases have become more popular and there are like more and more new vector databases coming in the market. it is useful in AI because with vector databases basically it's about storing vectors. Vectors are large arrays of numbers and these numbers are typically used to store semantic information about any input or any data. So that's how vectors become popular in this AI era and then vector databases have become more and more interesting. Although my talk does give a lot of ideas about how vector databases work.

**[18:18]** It was something that I wanted to connect to the vector API from Java. So there is some connection there as well.

**[18:25]** Okay. So you have the vector database to store vectors and then you have the vector API which is an ongoing implementation in OpenJDK in project Panama

**[18:37]** Which has I think the 10th or 11th incubator. So it's already there for a long time but not finished.

**[18:44]** Yeah. Yeah. Yeah. It's been there for quite some time. And the thing with vector API is primarily I mean it uses this thing called single instruction multiple data SIMD. it takes advantage of certain CPU architectures for faster processing and maybe I can talk a bit about that. So SIMD what it does is if you have pairs of data and you want to apply one operation on all these pairs it does that whole thing in one CPU cycle.

**[19:09]** Okay. and it is very natural choice if you look at vectors because with vectors you have large know numbers what do you say big arrays of numbers and if you have two of them like two vectors and you want to apply an operation you apply that on these pairs so simply becomes a natural choice there and that's what they take advantage of in the implementation of vector API and you're right it's in its 10th incubation right now I think with Jeep 508 and it's ongoing for quite some time and what I do is as you rightly said vector databases store vectors in some way and form and vector API is processing of vectors. so in the talk what I do is I have a simple implementation of vector as a record which has an array of doubles and then you there are certain operations that are possible with vectors. Those are implemented with vector API and the database itself is like a hashmap which stores all the vectors and the corresponding data

**[20:05]** And then I introduce different different concepts and then we use the vector database implementation to then explore these these concepts. That's that's what the talk is about.

**[20:14]** Okay. And we have two Java releases per year is that we are the 10th or 11th incubator. So that means that implementation started even 5 years and before for the vector API

**[20:28]** Before AI became such a big thing.

**[20:31]** Yeah.

**[20:31]** Isn't it a coincidence that something which started let's say six years ago

**[20:36]** And now is a hot topic related to AI and the calculations on these models.

**[20:41]** Yeah.

**[20:42]** Isn't Java catching up with something which wasn't even foreseen? Well, I I'm I'm not sure exactly how the history went, but I think there was some connection to it in the sense that I think 5 years ago there were still deep learning was there, there were still neural nets. So, we still had lot of traction towards AI and nowadays we see the real what you say implementations applications of it. But the work has been being done for many years and so is the case with vector API as well. there has been work been done for many years and I think there was some foresight in having that API designed and thoughts about how to implement that and yeah I think right now is a good time to explore the API but also make it in a way that fits in with these other projects really well and then we can have faster processing of vectors in Java

**[21:39]** And people who want to try this out so it is in the JDK but it's not enabled by default, you need to enable preview that this little flag.

**[21:47]** Yeah,

**[21:47]** I guess you were doing that with your experiment.

**[21:50]** Yes.

**[21:50]** Because it's in the JDK although it's a preview, it is stable. It is something you can use. It's tested, it's unit tested, it's quality code, it's a good API, correct?

**[22:03]** Yeah. so there is slight difference. So it is an API which is in the JDK. it you need to use preview flag to use it but it is in its incubation so one step earlier than than preview you still have to explicitly include the incubator in when you are running the or when you're using the API and you want to run your application and it is because I think there are connection to other projects for example even Valhalla has an impact on vector API so those things can happen and at the time at the time when these projects become more mature Then I think we will have the vector API as a preview feature available for anyone to use.

**[22:44]** I'm Chris Coites. I'm a professor at University of Manchester and together with the tornado VM team, we have two talks about how to accelerate Java programs on GPUs.

**[22:53]** I'm Hal Papadrio. I'm a research fellow with the University of Manchester. I'm part of the Tornado VM team and I'm here because I'm presenting our latest work at GPU Lama 3 Java which is a full Java inference engine. for LMS.

**[23:10]** Yeah. Okay. the rest of the team is behind the camera.

**[23:13]** Yes. Yes.

**[23:15]** You're with the whole team representing your system. I know VM a bit. Can you explain in two sentences what it is?

**[23:23]** Yeah. tornado VM essentially it's a plug-in to all JVM distributions that enables Java developers to write and accelerate their code on GPUs.

**[23:35]** Okay,

**[23:36]** That's it. GPUs which are a hot topic everything AI related

**[23:41]** That's true that's true like we were have been discussing with the team like five years ago the topic of GPUs wouldn't even be a talk in Devox or any conference and now this today we have seven talks on GPUs on Java which is becoming more important

**[23:59]** More important so what is the advantage why would I want to run my code on a GPU I would say the big advantage is that if you have a lot of parallelism in your code and you want to harness a device that can offer more performance because GPUs have more cores and they are suitable for kind of workloads. it would make sense to offload some of it on the GPUs. So there are workloads like traditional machine learning or LLM which is the hot topic now that can benefit especially for large models running on GPUs.

**[24:35]** Mhm. Can you maybe give an example of what can be improved for AI clients?

**[24:41]** Yeah, I think what's very interesting is that you can build your own AI libraries in pure Java and this is what we try to demonstrate with this project the GPU lama with tornado is that now through tornado VM you have all the tools available to build AI ready libraries in true Java. So what one have seen as a llama. C++ that is just a C++ program that runs inference on CPUs GPUs. We can have the same equivalent with pure Java and it's not intimidating anymore to run to write GPU code as a Java programmer. The entry barrier it's much much lower with frameworks like tornado VM. So that that's a use case that it's been made very obvious with LLMs.

**[25:29]** Mhm. As you said, cube have a history that AI didn't exist when you started with tornado VM. So you found a niche and now suddenly this appears to be very hot topic. we also see within the OpenJDK project that some things are going on around this topic. So you have the vector API which is more or less related to calculations. Then you have project babon. Are they competitors? what's happening in OpenJDK and tornado VM or are you somewhere emerging into one thing?

**[26:02]** We are complimentary approaches. So tornado can work with Babylon and Babylon can work with tornado VM. essentially Babylon it's more than just GPU acceleration because it involves techniques and methods for data structures which we are very eager to adopt. we were the first adopter one of the first adopters of project Panama. So having vector API working with the turn VM and the future with Babylon this is something that we're really looking forward to because our objective is maximum performance and actually as Mal can say we have our demos which they combine vector API and trend of VM to run workloads

**[26:44]** Actually vector API and project it's two of ideas that we build on top to have this pure Java GPU inference engine and for us project Panama it's great convenient because it simplified a lot how to load all all these very large models that can be up to 20 30 GB. So for us it's just a very big segments and then you can use the tornado VM API to simpl to split these segments into data structures that make sense for GPU. So tornado is a complimentary to this project. So it's not like a competitor. Same thing is for Babylon. So Babalon has its own data structures that it's something that we see in the foreseeable future to adopt in native VM.

**[27:26]** So yeah all the as I mentioned earlier all the components are there and they're mature enough to start building AI libraries for GPU acceleration in pure Java and this is what excite us the most.

**[27:38]** Mhm.

**[27:39]** If you didn't start with AI in mind what was the initial goal of VM? kind of applications, what kind of improvements can I gain with another VM?

**[27:50]** That's a really good question. when we started like 10 years ago building the system, we started out of curiosity. and the

**[27:58]** Which is a great way to start.

**[28:00]** Exactly. Exactly. So we're thinking okay if GPUs or even other device like FPGAs become let's say commodity how what changes the JVM should make to accommodate them. So back then we didn't have a use case but throughout the years we realized that there are use cases beyond AI like traditional codes that require acceleration. for example this year we had our first production deployment of toVM. So now it's being used by the European Space Agency in the Gaia mission. So for our audience Gaia mission is the largest European space mission where the Gaia space takes pictures of our galaxy. so in the University of Geneva they have a lot of data pipelines where they take the data the pictures and then they do very complex pipelines of processing those and they use tornado VM as part of a European project IRO and they managed to minimize the time from one month of processing to 5 days.

**[29:01]** Oh wow.

**[29:02]** Yeah. And which means if we look very closely there are u use cases that have been built in Java and they are in desperate need for high performance parallelism.

**[29:14]** Yeah.

**[29:14]** Hopefully we'll find more this future.

**[29:16]** If I want to try out tornado VM how complex is a hello world example.

**[29:21]** Well we have simplified the process a lot. So if you want to run your first let's say tornado program you can go in our GitHub which is tornadovm. There's a super simplified installer script that you just run it. It's going to detect your Java version, your JDK. As we mentioned, we are plugging for multiple JDK. So, it doesn't matter if you're using Grav Tein, Zulu, whatever. It's going to detect it and then it's going to install Tornado on your system. If you have a Nvidia GPU, it's going to ask you if you want to run OpenCl or PTX. If you have Intel, it's going to ask if you want to have a Spearv, for example. It's simple as that. it's an interactive let's say CLI tool it's going to it's going to be there and then you run a simple commatas tornado devices and then you're ready to go and then we have plenty of examples benchmarks guides because as Chris mentioned to been around for 10 years which is great because we live in the LLM era so even if you tryp and ask I want to write a tornado VM program we have enough material online and all these LLMs are trained with tornado

**[30:30]** Source code. So the entry barrier as I mentioned again is very very low to write your first Java program. So we invite you give it a go and give us your feedback if it's that easy.

**[30:41]** If I want to add also we are integrating with SDK man.

**[30:44]** So hopefully in a couple of months you just got SDK man then it's even simpler than running. Yes.

**[30:50]** And yeah, because we talk about simplifying things and since last week, tornado VM is integrated with Lchain 4j as a modern provider. So you just import your LangChain4j and then you have the tornado for free. So you just install tornado importing LangChain4j in your project and you have now also the option to run inference on your local GPU through tornado VM. things become much much easier and because you know with all this push towards AI also for us we iterate much faster and you know we get feedback that we didn't get in the past so yeah things are much much easier now

**[31:31]** Why doing AI and this kind of things with Java and not Python because Python seems to be the big player here but are you convinced Java is better?

**[31:43]** It is a tough question. Okay. Okay, we are myself and all the group we love Java. We have been doing research on JVMs for the last 20 years. we I mean I don't want to go discussions which language is better

**[31:59]** But I would say that each language has its own strengths and one of the strengths of Java it's stability, security and you know everything that we love about Java. So if somebody wants to use Python, let's say for training or making a prototype, that's fine. But if you want to go in production or even integrate AI into the large enterprise code bases that Java has, I would assume that somebody would like to stay in the ecosystem and use tools that are built within the Java ecosystem. So for me people have a choice and we are trying to enable Java developers to also have a choice within the Java ecosystem

**[32:37]** Also because as Java developers we don't really like dependencies also for sometimes it's very difficult even to p dependencies through GNI to native code so getting into Python as a Java developer really you know it's

**[32:51]** It's very hard

**[32:53]** Yeah so if the tools are available and they're mature enough to build similar libraries with Python and getting the same performance in just Java. Why to add you know one more dimension of complexity your project learning Python keeping up with libraries rather than getting something and maintain something which is Java and at the end of the day you can really understand what what's going on there.

**[33:14]** What is the one thing I should remember from your talk? If you're a Java developer and you're really looking forward to enter into native AI acceleration on the GPUs today, tornadov can offer you a platform and ecosystem that can help all developers from writing code to deployment to debugging because we have a vast amount of tools. They can start today and we are looking very forward for the feedback to make it even better and more suitable for the use cases.

**[33:42]** That's a wrap for this episode of the Foojay podcast. A huge thank you to our guests for their insights into the OpenJDK projects and evolutions. Whether you're excited about faster startup times with Project Leiden, curious about running Java on ARM, ready to explore vector databases for AI applications, or interested in GPU acceleration with Tornadov and Project Babylon, there's never been a more exciting time to be a Java developer. If you enjoyed this episode, then please subscribe to the Foojay podcast on YouTube or in your favorite podcast app. You can find all our episodes, show notes, and links to the resources mentioned today on Foojay.io. And don't forget to follow friends of OpenJDK on social media and LinkedIn for the latest news and updates from the Java community. Until next time with more DevOps and JFall interviews, keep coding, keep learning, and stay curious.

**[34:40]** Thanks for listening.

**[34:43]** J OpenJDK.
