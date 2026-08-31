**[0:00]** Let's talk about Quarkus and Agentic Commerce.

**[0:03]** Welcome to the Foojay podcast. All your news about OpenJDK.

**[0:09]** Welcome to the Foojay podcast. For this episode, I invited the author of three recent posts published on Foojay and he brought a colleague to get even more expert knowledge in this podcast. Thanks for joining MKL and Holly. Can you introduce yourselves please?

**[0:24]** I'm Holly Cummins. I'm an engineer on the Quarkus team. I've been with Quarkus for about 3 years. and I sort of I tend to write and speak a lot about sustainable IT and also sustainable development practices on that sort of human side of it.

**[0:41]** And my name is Mickey Miller and I'm technical writer for Quarkus same as Holly. like three years with Quarkus and like I think I'm writing last eight years about like technology mostly blockchain and now like Java frameworks like Quarkus

**[0:58]** And the three articles that you published are about Quarkus and the blockchain. So we have a lot to talk about. by the way why did you decide to contribute to Foojay? Thanks for that by the way. I think it was idea of Clement Escoier one of the main like Quarkus stakeholders and also the one who say like we need like more like evangelism for Quarkus. It was decided on our face to faces and I say like hey I can write a blog so maybe I can you know give it a shot and yeah that's how it is.

**[1:28]** Thanks and I hope you get many readers. [laughter] That's the goal of these articles of course. Now let's dive into Quarkus first. So two articles are about Quarkus. can you summarize what Quarkus is in two sentences?

**[1:43]** So if I had to put it in two sentences Quarkus is a cloudnative Java framework for any cloud. So basically any cloud and hyperscalers included

**[1:53]** That combines efficiency and cost savings with a greater developer experience

**[1:58]** And it features innovations such as dev mode and continuous testing while it keeps the enterprise stability that Java is known for. So Quarkus is basically designed for cloudnative Java developers who need a quick cold starts, high density deployments and efficient workflow with live reload and at the same time it keeps standards APIs and it offers the option to or running on the JVM or in the native mode. So you can basically pick based on what you need. What I like about Quarkus is you know you sort of have often when you're choosing a Java framework you have to choose you know do I want performance or do I want a good developer experience and you know do I want something really modern that breaks everything or do I want to sort of stay with something that's you know a bit more how we've always been doing things but it's not not moving forward and Quark is really you sort of

**[2:48]** You get to have your cake and eat it too of like it's really performant and it's got a really great developer experience and it's innovating but stuff still works

**[2:56]** So it's a bit of a competitor or similar to Spring, Micronaut, these kinds of frameworks

**[3:04]** Like if you look to it broadly like where is Spring Boot and Micron they all serve the enterprise market but Quirus stands out for its focus on cloud native defaults.

**[3:12]** So for example with Spring Boot like Spring Boot relies on runtime reflection and Quarkus emphasizes buildtime processing and n native compilation with Gravium. So this results in faster startup times and reduced memory usage.

**[3:29]** Mhm.

**[3:29]** And [clears throat] basically from dayto-day perspective, Quarkus also enhanced the developer experience with features like dev mode and live reload which is a killer feature. So you get instant feedback on the code changes with no long rebuilds or restarts like with spring and others. So on top of that like Quarkus integrates both like imperative and reactive models by using Eclipse Works and it still sticks to solid standards like microprofile or Jakarta. I

**[3:58]** I've seen a few demos at conferences with Quarkus and that's one of the highlights that you see during these demos. They just change some code and you go to a web UI where you see the result and indeed it's immediately available. That's that dev mode you're talking about. I think this is like the live reload, right?

**[4:17]** Yeah. Yeah. Yeah. Yeah. Live reload is part of devi of dev mode. And as you say, it's so good once you get used to it, you go back to something else and you're like, wait a minute, what I have to like rebuild just to see my changes.

**[4:29]** Yeah. Okay. I have to experiment. I didn't use Quarkus a lot. I think I was challenged a few years ago to compare some some performance of spring on the Raspberry Pi that I blogged about and I immediately got the question and what does Quarkus do? [laughter] So I think I did it once but that was my experiments with Quarkus and I had some time ago great talk with Quarkus Clemore who created this how are they called in Quarkus plugins? No.

**[4:58]** Oh extensions

**[4:59]** Extensions yeah to use Quarkus in a Java Vix application. So that's really great that you see that also there there's some possibility. now the first blog post that you've written is Quarkus runtime and framework for cloud native Java. So you mentioned runtime. Should we see Quarkus as a replacement for the JVM or is that the native part that you're talking about?

**[5:26]** No, it does not replace the JVM like in JVM. Quarkus runs on the JVM like other Java frameworks

**[5:34]** While leveraging its grow VM integration and in native mode Quarkus compiles the application into native executable. So the JVM is not required at the runtime. So in this context runtime refers more to the execution stack that boots the application and integrates the frameworks configuration and runtime services u the application relies on. So in other words, Quarkus works as intelligent liar that manages the application life cycle whether it runs on the standard OpenJDK or runs as a native binary.

**[6:09]** Quarkus, you know, as Mickey says, Quarkus is if you're running in JVM mode, it's just a set of libraries that sit on top of your normal JVM. And what's what's different and what gives you that kind of performance boost is that a lot of the stuff that happens at build time which means that by the time it hits runtime there's less code and that code can run more efficiently. But then of course if you're going to run as a native application the JVM is gone and there we're sort of leveraging grow VM in order to just compile right down to something that doesn't need the JVM.

**[6:40]** So those are the things that you explain in the article about buildtime optimization. How is this different to just in time compilation? So which happens with traditional Java applications.

**[6:52]** Built-time optimization is the mechanism by which Quarkus shifts costly startup procedures like scanning classes for annotations, parsing configuration files and building application meta models to the build phase. So instead of doing this work every time the application starts, Quarkus does it once during compilation and generates optimized by code that runs with precomputed results. And this gives you four main benefits if I can say so like the first is faster startup as we already said because the application skips the heavy lifting of scanning and parsing when it boots. second it's ley sorry lower memory usage because the final artifacts stay smaller and leaner and libraries needed only for initialization such as parsers do not get loaded at runtime and third error detection because quarkus catches configuration errors or incorrect annotations usage during the build so it fails fast instead of crashing at the runtime and yeah I think it's What? And fourth reduce reflection. So reduce reflection because Quirus generates proxies that avoids reflection calls which improves

**[8:06]** Performance.

**[8:07]** And it helps to separate this from both like just in time GT compilation and also from ahead of time AOT initiatives like project lighten.

**[8:16]** Mhm. So with git the GVM optimize hot code path into machine code while application runs and quer buildtime optimization do something else they improve the baseline startup and memory profile so the application reaches the git warm-up phase much more faster and with AOT work like the project maiden the goal is to shift the general GVM mechanics such as class loading and heap archiving to build time where quarkus focuses on semantic frame framework optimization instead.

**[8:48]** Yeah.

**[8:49]** So it understands the framework you use such as hibernate or camel and wrestles their wiring before the GVM even starts. And that's why Quarkus can deliver these benefits today on standard open GDA releases independent of lighten timeline and its constraints.

**[9:07]** Those three technologies they're all really complimementaryary. So, so AOT is working at the JVM level to get rid of well to move work from the runtime to the build time. Quarkus is working at the application level and at the library level to move work from runtime to build time. And then the JIT is working to make sure that once the work does get to runtime, it gets quicker faster. Historically in the JVM, all a lot of the architectural decisions have been about trying to move work away from the build phase to runtime to make everything as dynamic as possible. And so really a lot of these sort of things like reflection, they're they're kind of optimized so that you can almost change the engine while the plane is flying and you know you can swap out your libraries at runtime and swap in new ones and if you only deploy your application every six months that architectural decision makes a lot of sense. But if you live in the modern world where you have containers, you know, you're not going to be sshing into your system in order to patch, you know, copy in libraries or I mean, I always

**[10:08]** Say that and then I kind of look around. I'm like, I hope you're not sshing into your system to patch libraries because we have CI/CD, we have containers. The things we deploy tend to be pretty immutable, but we still have all of this overhead in order to support this dynamism that we're not even using. And it's so wasteful. And so the sort of the architectural decision that Quarkus made was well why do we do these things when with reflection when if we do it with reflection every single instance of the application has to do this work every single time we start. Why not do it once at build time and then we get you know as Mickey says we get the sort of the improved startup and the lower memory consumption but it also gives this really big benefit for throughput all the way through the life cycle of the application

**[10:59]** And does it impact the build time a lot

**[11:02]** It impacts the build time slightly I would say so maybe like 20% more build time or something like that so it's not like so when you do a native compil ation, you know, your build time is long. and you know, that's the trade-off is you get this sort of magically instantaneous starting, but your build time is long. With Quarkus, it's much more subtle. So, you have, you know, maybe like order of magnitude of 20% longer build time. but I think that impact on build time is then one of the things that led to the innovation of the continuous testing and the live reload because if you have that slightly longer build time, then it's going to be even more annoying to every time you make a code change, run a full build. And so it the team did the architectural work to say well you can run with live reload and then every change gets reflected instantly because we're just compiling that change. and so then that means that you for all of your development you don't see that slightly worsened build time and then your CI/CD 20% it's like nothing

**[12:09]** And actually I mean it might not even be 20% it might be 10%. It's like that kind of

**[12:13]** Yeah. But it's not that there such a big impact that Yeah. you need really need to consider it. No, it's just a bit longer. Yeah.

**[12:20]** Yeah. So like when I do demos, you know, if I'm going to do a native compilation, I kind of think really seriously before I do a native compilation live in front of people and I'm usually like I always have a backup. So that but with, you know, just a normal carcass build, it's, you know, it's fine.

**[12:35]** And the problem with the native compilation is you also have to do it for each platform where you want to run on.

**[12:40]** Yes. So you it's the sort of the trade-off of ahead of time and it's again the reason that the JVM hasn't done this historically is that you do end up really coupled to your deployment environment.

**[12:54]** Can you highlight some of the other benefits of Quarkus? I have two Quarkus specialists and deers here. So I guess you have a whole list of things you want to highlight. Why should I consider switching to Quarkus or learning Quarkus? Okay. So here we can maybe talk about how Quarkus connects productive developer workflow with operational requirements in production and split it into three batches. So the first on the developer experience side which teams often call them one meaning development time. So the first nice per to start with is the dev services mean like automatic dev time services and these reduce like it works on my machine problem not on yours. I mean miswatch between your local setup and someone else setup by starting external services such as databases Kafka for event streaming or cake log or any other identity and access management in docker containers and connecting them to the app and this approach removes the need for manual docker compo compose files in many local deployment setups.

**[14:01]** Then the development UI also called deaf UI a local dashboard which runs alongside the app and displays configuration active beans like manage components and routting. So troubleshooting is more direct. And for Kubernetes native deployment, Quarkus can generate Kubernetes manifests like deployment descriptors and build container images by using JIP by container image built for Java or Docker. So developers can deploy without learning everything Kubernetes detail first and second batch on the operational side which teams often call day two. meaning running like maintaining the service. So for security, Quarkus integrates a standard such as OIDC open ID connect and web alphen for passwordless authentication through dedicated extensions and because qu workers does framework work at build time, it can reduce the deployment attack surface by removing unused code paths before deployment for observability meaning metrics, logs, traces. Now Quarkus integrates with open telemetry and micrometer for metric fat. So matrix distributed tracing request

**[15:25]** Tracing across services and lock correlation often works with defaults and require no extra setup at all.

**[15:33]** Mhm. And for full tolerance I think microprofile full tolerance is used you know the standard resilience API and this lets developers add annotations that apply circuit breaks stop calling a failing dependency retries u retries a repeat and file call or bulkheads like limit concurrency. So the application handles common network failures without you know without custom plumbing. And finally Quirus has the Quir Quark ecosystem you know community extension catalog huge one which provides a large set of extensions that connect Quarkus to common Java libraries and platforms. the Quarkus extensions catalog currently is over 100,000 extensions if we including unlisted and relocated entries. that if we should go to this like really up to date. I think Holly can say the precise number, but I think it's like 850

**[16:34]** Something like that.

**[16:35]** Yeah. Yeah.

**[16:36]** It was 800 last time I checked, but I can believe that there's 50 more since I [laughter] checked.

**[16:40]** Okay. And the one I mentioned, Quark of X, is only one of them.

**[16:44]** Yeah. [laughter]

**[16:45]** Okay. Holly, anything you want to add?

**[16:49]** Yeah. I sort of I tend to think of it in terms of like who you're talking to. So like if if you know for us as developers usually the thing we care most about is like how does this feel to program and then Quarkus has that really great developer experience but I think we sometimes find you know if we go to our management and we say yes I want to switch to Quarkus because you know it makes my life really great management will go yeah but what about me and so then there the sort of the conversation is a lot about then you know the sort of the performance and the cost savings that come with that performance but I think Mickey touched on another really sort of important aspect to which is those kind of like enterprise qualities of service things like the observability and the attack surface and the way that Quarkus does stuff at build time means that often there's sort of things that you would really like to do to the whole of your codebase to you know inject logging or to you know inject your custom whatever it is for your domain or to you know have the instrumentation for the

**[17:52]** Observer ability but doing it with reflection would be too expensive in terms of runtime. So we sort of say okay no we have to do it all manually or we just don't do it at all. But because we have this really good understanding of the codebase at build time it means we can put that instrumentation in and so then you get that greater reliability and that greater maintainability of the running application.

**[18:13]** You mentioned something about the cloud cost. So, how can I convince my manager that I want this? It's probably hard to put this into into numbers, but can I reduce my cloud costs by introducing Quarkus ex instead of another framework.

**[18:31]** So, in practice Quarkus reduce cloud span through resources density and elasticity and it's because Quarkus application often use less RSR RSS memory like real memory used by the processes. So, teams can fit more pods like Kubernetes app instances onto the same Kubernetes nodes or they can move to a smaller node. That usually means fewer nodes, smaller instances sizes or of course both.

**[19:00]** And to make that concrete, you can often run an Quarkus service in JV mode on a smaller instance types than you would pick for heavier Java stacks while still handling the real traffic and that reduces the baseline cost before you even touch autoscaling.

**[19:14]** [clears throat]

**[19:15]** Then there is the service serviceless angle. When startup time is slow sorry low the cold start penalty gets smaller. So teams can scale to zero like run zero instances while more aggressively and that reduces payment for ill capacity because you pay mainly when the service actually runs. This is something that you know when we measure in the lab we can see the lower resource usage and we can see that we can run in small smaller containers but it's definitely not something that's just sort of theoretical when people switch to Quarkus you know they come back and they tell us numbers which match up with what we're measuring which is always always reassuring. so we typically see that resource usage will maybe be like half of what it was before or sometimes even a third of what it is was before. So we get these really significant cost savings. We heard from one team and it was a couple of years ago. So it was that sort of postlockdown surge in energy prices when all of a sudden you know nobody could afford to run their refrigerator. We couldn't afford to run

**[20:16]** Our oven and then you know at a business level a lot of businesses sort of looked around at all of these servers and said can we reduce that bill? and so they sort of had this corporate mandate to say okay you need to reduce your bills and they looked at their bills and they were like actually no we don't because our bills are so small we already did that work of converting to quarkus and so we're fine we can just do

**[20:35]** It fits in the story of also building ecological

**[20:39]** Apps that you think about what is the impact of my application on the environment but that's always oneonone related with the cost. Yeah, definitely. It's it's again it's this sort of double whim where you're you're saving the costs which is fantastic and you but then you're also reducing the environmental impact and it's in two ways. So one is that sort of daily cost of running, you know, that the electricity bill is lower. So then that means that there's less environmental impact from the electricity, but also the hardware makes a huge difference because if you need fewer machines, then that means that you aren't causing machines to be manufactured with the enormous environmental impact and embodied carbon of manufacturing servers.

**[21:24]** Okay. By the way, you had a great talk about this. I should add a link to the show notes of this post about this. what I also read in the article is Quarkus is built on Eclipse vertex. So that's reactive toolkit. I heard some some talks about yeah we have virtual threads now. Should we still use this kind of technology in applications? Is this something you are working on within Quarkus? They are like highly relevant these days like highly relevant and complimentary like not competitive where to thread solve the concurrency problem. they allow you to write simple blocking style code that can handle massive concurrency with very little memory. However, virtual threads do not solve the in andout problem like input and output problem. You still need that high performance no blocking layer to actually move bytes in and out of the network efficiently and whereex provides the foundation. it acts as the underlying in andout engine for Quarkus managing network interaction by using the multi-rection pattern like non-blocking event loops and when a virtual thread performs a network call

**[22:34]** In parkus the operation is seamlessly offloaded to the vert.x non-blocking in an outlier the virtual thread parks like cheaply while vertex handles the heavy lifting and resumes when the data is ready. So vert.x is the engine that makes in andout efficient while virtual threads are the abstraction that makes the code simpler and this means you get the developer experience of blocking code with runtime performance of a reactive stack which is huge.

**[23:04]** Yeah because this reactive stack is not a traditional way of doing Java programming. So I think for a lot of people this is very hard to do or to understand. Every time I look at reactive code, my head hurts. And I think it's one of the [laughter] one of the really nice things that Quarkus has done is because that reactive code, you know, it does scale really well. It can be super efficient, but as a mere mortal, I do not want to program it. and so it's sort of what we've done with Quark is you can do reactive all the way through. So you can do reactive in your application layer and then there's the reactive core. But if like most of us you don't want to touch the reactive code the reactive code is still there in the core giving the efficiency but then you know as Mickey says you have this you can have your sort of normal easy to understand blocking programming model on top and then now if if you want to sort of get again a bit more of that scalability with the blocking programming model you can bring in virtual threads at the application layer and so then you've got

**[24:06]** The kind of the three-way [clears throat] best of both worlds of the double performance win with the best programming model.

**[24:14]** Now that I have two people that are working inside the Quarkus team, any new features or things you should not announce yet but that you can already talk about? [laughter]

**[24:24]** One of the things about working in open source is everything is sort of you know always everybody's always the first to know. I think it was something like with that life reload right like where we like describing the life reload feature you know because that's the most killer Quarkus feature as I was you know asking the guys around

**[24:45]** About the quarkus and I think there was like

**[24:47]** I think quarkus introduced like death mcp like or model context protocol

**[24:53]** Which lets local AI coding agents connect to the running application so they get more context for debugging and writing code. M

**[25:02]** Which is really huge when you combine it with the live reload because in the Quarkus dev mode live reload applies code configuration and the resource changes while the application runs. so you see right away without rebuild or restart you know the changes output and in other words like it removes the slow turnaround that people often associated with Java

**[25:26]** And it brings more of a scripting style workflow to enterprise Java development and it goes beyond code changes because dev mode also powers the services we were you know talking at the beginning of the podcast which provisions infrastructure such as databases or Kafka brokers in docker containers and connect them to your app with no extra configuration and to bring it home like this continuous testing runs relevant test in the background as soon as you save a file.

**[25:54]** Yeah.

**[25:55]** And then you add that you know AI AI coding edges to it and it's really nice package.

**[26:02]** One final question about Quarkus. Can we conclude that it's more modern? I know it's it's it doesn't exist as long as Spring for instance. Is it was born in the cloud age let's say so is it more adapted or more tailored to solve this kind of problems that people want to run Java applications massively in a cloud environment

**[26:28]** Quarkus is newer than many established Java frameworks I think but newer doesn't mean more modern

**[26:37]** Because in practice its design

**[26:39]** Like it its design avoids leg legacy constraints. So it's say so it stays away from all the patterns that do not fit cloud native deployment. Instead quarkus focuses on built-in processing and streamline develop loop and it also applies lessons learned from Ilia frameworks and you can see that in its efficienc not not just because it's newer but because it's embraced cloud paradigms at a fundamental level. Yeah, it's like an it's an architectural thing, not just a chronology thing that makes it so modern.

**[27:14]** Then switch to your third article that you published. So the first two ones were about Quarkus. links in the show notes so people can read all the details there. but in the third article you handle a different topic and it's about chain transactions. you mentioned that most people have never heard of X42. That's true for me at least. can you walk us through a concrete example of what is the topic that you are explaining in this blog post? The basic idea is basically if you are somebody of or you have anything of value for example book or nice blog post you have on the on your web and the way you monetize the content is that you have some advertisements advertisement sites there and basically you wait for reader to came there and read it and so that you are paid by you know the sponsors or you know the providers of the banner and what's happens these days that you basically developers creates the first bots like AI agents that basically reach your content you know on the level to basically curl the page so you have another visit but you know these banners

**[28:23]** Or these mechanisms doesn't catch eye screen oh sorry I eye time on your content and you get no pains at all and basically these days like the internet of these web blogs works like free for all scrapers where basically the boss just invade your pages, steal the content and you get no pain. And the idea is like what if I create a payw wall or wall to protect my content put an AI agent in front of that and say if somebody wants my content good but you will make a microtransaction you know using the blockchain and it's a it could be u a few cents or bug or whatever you specify and then the agents do the transactions and when it's all settled they get the data and you get paid. So it's like more fair environment these days because we still live in [snorts] that age where you know the people came to your page you know there was a claim there was a time and they were like you know paid for these things which is not happening and I think this topic is like huge for 2026 when this will roll. So that's basically the idea and the 2K keywords for the third article which is really huge. I

**[29:43]** Think it's 24 normals is like X 402 and ERC 84. And these are basically the two standards for you know blockchain development the operational blockchain where X42 is the payment flow for an HTTP call. It defines how a client gets a price quote pays and retries to receive the results. And ERC804 is the identity and register layer for agents.

**[30:16]** It links an agent to onchain metadata such as identity pointers and reputation signals. And basically ERC 84 can store or reference an agent's history and reputation, but it's not the payment protocol itself. So that's why you need these two to work together where one is something as your proof of record like is this agent good if so like what he did you know how many contracts per day successfully ends you know in his pocket and then they are using the x42 which is you know the replacement when you need to call a human agent using a fall or emails you know this like classic ways. So basically you need something they can communicate and then how to how they can prove their identity and proof of record so you know that you're dealing with somebody worth of your setup and your money. I think that's a problem we started seeing last year is that because of all these models being trained for AI that websites get scraped a lot to be honest we had a downtime of Foojay this week of an hour I think because one specific IP I think from Singapore was pulling the whole website at an amazing speed. so probably

**[31:35]** Again some some model model being trained with content and scraping a lot of websites. so in your article you talk about nano business so that agents pay for visiting a website or reading a post actually in my ears it's a bit like science fiction will I suddenly get paid for my personal blog some way? yeah, exactly. So basically these AI agents can work like normal agents. So they can be used to you get paid from other agents so that they can scrape your content but they can also take the content and say hey you and you know you have contact to readers that you know can find a value in this content. So they act as as you know as ushers they just move your content and to broader audience which is also good.

**[32:27]** And there's also that you know protection side. They protect you from these kind of attacks. And I think the cloud cloudfare ships paper crawl controls that lets like sites charge automated clients through a 402 base flow and this matches the nano business idea one paid request, one paid response, no subscription. And I can maybe just highlight how this you know how this get along. Basically a client calls an API endpoint through a normal HTTP request. the server replies with 402 payment required you know some statement and includes machine readable payment terms. Then the client pays then retrieves the same request with the payment confirmation attached. And lastly the server validates the payment and returns the result. So that's basically how that nano business is today and it's basically I do one transaction with the agent it has its results somebody gets or both sides get the value and that's basically the nano business you don't have anything anything besides that

**[33:35]** Is this already used or their websites already working like that or the agents already paying for content is Google already paying back to the people who create the content

**[33:47]** I'm not sure about the Google but I think the Google is great example and I wrote in the article about it but basically when people want to know like what is this idea is about the idea is about that when you every time you type something in the Google search or any search let's say Google the Google spend resources right so the basically if you run like 100 Google requests or questions per week you should be you know awaiting a Google a bill right like hey you're using Google search to spare something but you know you will never get a bill from Google because Google don't assume you to be a customer

**[34:30]** The advertising is so basically what Google does is basically when you do a search the Google shows you there's a result based on the subscription and you know basically who paid most so it's these results are not that relevant to what you're searching for it's basically ally they show you what they were paid to show you. And this can be also solved by these agents because you can set you know your requirements specifics and based on your search and based on your behaviors that can be set and store these agents can produce like search that fits you better. And I think teams already deploy like X42 endpoints and present them as working ecosystem and public numbers about income stays rare. So the clearest signals so far is vendors shipping support and tooling and not revenue dashboard. So I think it's something that starts like as as we speak. So maybe a question for Holly is, can we redevelop the Foojay website with Quarkus to have this payment integration?

**[35:41]** [laughter]

**[35:45]** And can you do that for us? This sounds all so new. I don't think we have something ready to develop a website like that.

**[35:55]** Yeah. And as you were talking, Mickey, I was just sort of thinking about some of the open source ecosystem, too, because a lot of I can't remember what company it was, but there was big layoffs announced last week or the week before because it was one of the open source vendors and they you know so many sort of media producers are have had their business model sort of gutted by the change to crawling rather than

**[36:20]** Sending traffic and it had affected the open source companies too because their business model had been that people would come to their site looking for documentation and then stay to pay for a support contract but because nobody was even coming to the site. And so I was wondering whether that sort of microtransaction

**[36:38]** Would help with that business model as well of again just sort of you know for your usage just that you know tiny little revenue stream.

**[36:46]** Yeah, exactly. I think it can help and then basically the blockchain is really great tool for that to make it happen because you know if you use for example Visa it's super fast but there's a bottleneck problem because if you run or let's imagine that for example TGPT every time somebody asks TGPT a question there could be a process that burns something out of your wallet I want to ask something they you know send me a bell for 000.1 one dollar and then they give me the questions now like how many people using TGPD today like daily I think it's billions right and if this would be like really a new thing and big companies would start using it or for example Google or chpt you need something that can handles like millions of little transactions which is which are not really built for like it's really fast but they don't expect that they will have like you know 2,000 or I don't know 10 10 million transactions in a few seconds. So that's where the blockchain could be better because you have lots of blockchains. All these blockchains have their layer two where they can just offload all these

**[38:00]** Transactions. So you have blockchains like layer one there's when all the important stuff is going on but the transaction back and forth and their verification and sequencing in a correct order can be you know offloaded to something else. So you don't have the bottleneck problems

**[38:16]** There and you're using really microtransactions. It's it could be you know

**[38:22]** 0000 something of a dollar per request. So it doesn't cost you that much but it needs to be done in the way. So

**[38:30]** Basically the people are paid for the value they provide.

**[38:34]** In the article you talk about open standards. So the standard is there but will the big companies like a Google and all these model generators adopt this model and go into these standards and agree on paying all the people where they get the knowledge from.

**[38:56]** I think public chains expose who paid whom and when and how. I think they could be reluctant until this one little piece will be solved which is basically the security or you want to do something in the private right so if you will have these agents which will be doing 10 thousands of transactions with the company you don't like and everybody will see it because it's on the blockchain there will be not you know something that covers this it could be a problem so this needs to be fixed so you can basically operate without anybody knowing what you do or with whom you are interacting with.

**[39:33]** So that could create intelligence you don't want to share.

**[39:37]** And again like the blockchain is good for that because you can use chains that are specified on the you know subtle transactions anonymity and security.

**[39:47]** On the other hand it would maybe solve a problem that open source maintainers have like you said Holly it's it's very difficult if you're working on open source library project to get some revenue from it. Most people do it because

**[40:01]** They had a good idea, they use it for themselves, they made it public and then somehow everyone starts using it like some kind of logging frameworks. so how do you make money out of it? Do you see stuff happening there to support these people who are working on these open source projects? Holly,

**[40:21]** We're seeing sort of experimentation with different different models. and there are models now, and I'm just blanking. I was talking about it on Monday, and I'm just blanking on the name, which is kind of terrible. there are models now where which are sort of set up in order to funnel money to maintainers through again through through these sort of, you know, like just kind of chuck a cup of coffee. I mean not literally that would be rude but you check the price of a cup of coffee you know towards a maintainer and what we're seeing as well is some shifts in how some of the foundations work. So Quarkus recently joined the Common House Foundation and Micronaut is joining Common House. And so one of the things that Common House is looking at is that sort of revenue model of can we take money in? Well, I mean Common House does take money in. And then how do we send that money then to the projects either as marketing budgets for the projects or just as funding for the maintainers. And I think for some of these small open source projects to try

**[41:36]** And you know set up that financial infrastructure is just way too much. but doing it collectively either through it's going to I'll have to send you afterwards on the show notes the name because it's something I think it's something like software collective

**[41:52]** Is trying to solve that financial problem and then you know sort of other vehicles like common house also have that mechanism to [gasps]

**[41:58]** To allow it to be a shared problem and a shared solution.

**[42:01]** Mhm. And yeah maybe this microtransactions could also provide some kind of solution there. So integrating that also in did I use this library or not could also be some kind of transaction going back to who developed it.

**[42:17]** Yeah. And I think you do need sort of the full range of solutions from the sort of the microtransactions at one end to at the other end some some of the larger companies. It's actually very difficult to get a small amount of money out of a large company, but a large amount of money is much more straightforward just because of how they're set up organizationally is that the sort of the procurement departments and the sales processes and everything, they're they're geared up for large amounts of money. So what they want to do is have one single transaction that covers most of their needs. And so that sort of you know at one end of the open source model you have that and then at the other end you have the microtransactions and then in the middle you have the foundations and the collectives.

**[43:00]** It's a bit the same thing that all these chat providers are doing. You buy a budget of $100 and then you can ask a lot of questions until your budget is done completely used and then you have to buy budget again. That's what you mean with some where you put a lot of money or a bigger amount of money and then you can use it for a lot of these small transactions. So it's a bit that model but then again yeah using blog post using open source code. I love the idea. I it's still science fiction to me but [laughter] I love the blog post because it explains a lot. and I hope that in one year if you look back at this podcast and maybe do another one that you can say, yeah, and it's already used in these and these services where you see that some money is going back to the people who are creating this content. But that's the same thing for Foojay. We rely on people like you writing content to help people use some tool like Quarkus or to understand something like this these microtransactions. But there goes a lot of effort in writing this. So, finding some way to have a revenue out

**[44:10]** Of this would would be great.

**[44:12]** Yeah, definitely. And I think that's the that's the last idea maybe we can talk about is like how the website works like these days, you know, because when you develop the website, it needs to be, you know, like quick to load, you know, you have hamburger menu on the right, nice picture, it looks good because it was built for human eyes to see it, interact with it, and then you put your banners or, you know, everything. So but the like I think this year will some shift happened and the developers will need to figure out how to make that same well appearing website for human eye to appealing like sexy for these agents you know these API endpoints need to be accessible so they can just reach what they want and do all these interactions get paid get the value hello come again so I think

**[45:03]** They will shift slightly from how human access to it compared to how these agents AI can access it. So this can happen and AI is a huge topic in all industries. So I think this will go hand in hand.

**[45:20]** Yeah, I was just reading this week Cassidy Williams wrote a blog post about how she'd sort of optimized her SEO for LLMs and she was talking about a new pattern which I hadn't heard of which is that you put an LLM.ext text file next to your robots.ext. and the first thing is that you have to make sure they don't contradict each other because often the robots text says don't don't crawl me and then the LM's text is saying please crawl me. but almost in that file, you know, as Mickey says, you know, you don't need all of that heavy styling. You just need to get straight to the content and to the text. And so you have this sort of very optimized for LLM summary of your site and they can use that to then, you know, decide where they rank you. Is this a bit and then we can maybe go back to Quarkus. Is this MCP stuff? We should provide our content content of a website through an MCP API which we can build with Quarkus.

**[46:15]** I mean certainly you can Quarkus has that really nice MCP integration for both clients and servers. But I think going to the sort of the that bigger question, it's exactly that you have to be providing your stuff, whatever it is, through more and more channels. And so then you need that infrastructure support because you don't want to be the one who's reinventing. Okay, well I need to take my content and then I need to rewrite it into my LLM text and then I need to like reut new endpoints for my MCP and then new endpoints for whatever else. And you know, you need to be pushing it down into the infrastructure layer. And then we have the combination again of the third article with the Quarkus articles which is really great. Okay, then thank you both for your time. Thank you for the articles. Mickey, so I hope we get a lot more. Holly, you're also Holly, you're also invited to become an author, of course. [laughter]

**[47:05]** Thank you.

**[47:06]** I think you have plenty of time to write.

**[47:09]** Yeah. Well, that's exactly it. I've got so much time I don't know what to do with it. [laughter]

**[47:12]** Okay. Put it at the to-do list somewhere in between. Okay. thank you for your time. Thank you for joining this recording. Thank you also to the listeners of course. please stay connected to Foojay on our blog posts on the website, social media, wherever you can find us, LinkedIn and join us for the next podcast. I hope it will be one about all the changes between 21 and 25 and what was important there. But more about that later. thank you and

**[47:42]** So thank you so much for having me and thank you so much Holly to be my Quarkus expert. Great sidekick. Thank you both.

**[47:49]** Give me a fake. Give me the OpenJDK.
