**[0:00]** The AI revolution isn't replacing Java developers. No, it's forcing us to think harder.

**[0:05]** Give me a fake. Give me the OpenJDK.

**[0:12]** Welcome to another episode of the Foojay podcast. Today we're talking about AI and Java, how it's changing the way we work, what we need to watch out for, and why understanding what's really happening matters more than ever. I recorded these interviews at Devox and Jall and talked with people who are actually building and using this stuff every day. Marian Hornenborg opened my eyes to something important. Every time an AI generates a token, there's a massive amount of computation happening behind the scenes. She's talking about billions of floating point operations and all that computing power uses a lot of energy. Victor Hamok and Baruk Saduhki did something really cool. They tested six different AI coding tools live on stage with the same task. The results were all over the place, but they found that the tools with access to good documentation performed much better. Stefan Shin showed me how graph databases can make AI responses more reliable by providing a solid source of truth rather than relying on vector search. Mario Fuscoll works on LangChain4j, a leading Java framework for AI. He explained that

**[1:26]** Breaking down large tasks into smaller ones and using specialized agents can help reduce hallucinations. Urun banken and Martain Dhost shared their experiences working with enterprise Java. Even though frameworks are becoming lighter and we are running everything in containers now, there are still complex problems that require real developer expertise. Martin Molders reminds us that AI is a tool, not a replacement. Especially when you're solving problems no one has tackled before. You still need to know what you're doing. And finally, Simon Maple from Tesla discussed moving beyond VIP coding towards a more reliable productionready approach using specifications to guide AI tools. Let's get started. My name is Mariana Hornborg and I'm at an attendee since 1:00 but before that I was a speaker at 12:00 and I'm also an jug lead. So I enjoy seeing the conference here and comparing it to what we do in Holland.

**[2:27]** Okay. Comparing is better here.

**[2:30]** Different things are better other things are better in JFall. So yeah there should be differences in conferences, right?

**[2:37]** Okay.

**[2:37]** You are a speaker. So you have a talk. What was the subject? my subject was the simple math behind AI and I fell into one of the pitfalls that many speakers do. we say AI and we actually mean generative AI and simply put the mechanics around large language models whereas I think we are ignoring so many opportunities of AI that I don't feel are addressed enough today. but what I wanted to do was to make comprehensible that at the core of the nodes in a neural network it is simple computing that's happening there it's just on an enormous scale and if you start realizing how much it costs in computing force for every token that gets generated it's it's extreme

**[3:25]** Simple mod is it really so simple

**[3:27]** In my presentation of course I skip through a lot of layers of complexity many of them are especially around scale. So in the at the core it is really multiplication and addition done with a large set of numbers represented as vectors and matrices but they're just shapes of arrays right so we know arrays the other complexity is in the choices that are made around certain activation functions and two subjects that I didn't address around attention and temperature. So you're right, there is more to it, but even those mechanisms are pretty well explainable if we focus in on them.

**[4:10]** Okay, you touched the complexity of the hardware which is needed to do all this. Hardware means cost means electricity means it has an impact on ecological things. Do we use AI for all the right tools?

**[4:26]** I don't think so. No. And part of that is also because we are focusing on the generation of stuff and I hear in all talks that AI generated stuff is not always that good. So we have to review it and we have to work on it. But if AI keeps producing so much there's an energy cost to the generation of it and an energy cost to the upkeeping of it. Right? So I think in both sides we need to focus a lot on where our energy is going literally. I am dreaming of AI that is taking things out of my landscape that is not useful anymore and taking away legacy systems that are only still in use by by one small component somewhere. That's what I dream of.

**[5:08]** Are we as developers responsible? Should we think more about the impact that we have with our code on ecological on electricity use on hardware use? I think I should leave that to others because I don't know the computations on the carbon print footprint. I can imagine that there is development in hardware to make it energy more energy efficient. But I do think if you realize for us to think of one word, we're doing this all the time, right? And energy wise is very efficient. And I'm I think we compare AI too much with our own text production ability. And if we realize what takes place when generating just one token, just the massive amount of computation, they're called flops. Did you know that? Floating point operations. So they're into the billions of flops for each token generated.

**[6:03]** And then well, you have more respect for the human brain, right? And the capabilities. And then I think like, well, let's produce our own text. I think in intellig if you do a request with a chatbased interface you even see how many tokens are being used. So you make me very scary now.

**[6:20]** Well that you could be or should be. So we compare it to human interaction and human text production but because it feels intuitive but it's just massive computation underneath. It's just brood computation used to force produce every token time and time again.

**[6:39]** Mhm. if there's one thing people should remember from your talk, what is it?

**[6:44]** I think people should stay curious and zoom in onto the details of something especially when something gets mystified such as AI.

**[6:54]** This is Marorski

**[6:55]** And this is Victor Gamov

**[6:56]** And this is how we do introduction to each other during our pair talk as well.

**[7:01]** Okay.

**[7:01]** Because you know it's it's it's not polite to pump yourself. So we pump each other.

**[7:06]** Yes. Yes.

**[7:07]** Do you have a talk? What's the subject?

**[7:10]** Subject talk calls robocoders and agentic ID face off.

**[7:15]** So the idea is that we get bunch of tools as much as time allows and yesterday we had a three hours slot. So we brought six AI agentic tools and gave them exactly the same setup, exactly the same prompt and just waited to see what happens. Yeah, we try to show how different tools can provide more or less consistent results based on consistent methodology. So we introduced few methodologies that people can apply for their agentics because sometimes people find themselves very confused and a little bit the pro prop I don't know like frustrated with the vibe coding things because the system is not giving them result what they expecting and this is also expected because you putting garbage as input you're expecting to get something nice but you're actually getting garbage as output

**[8:06]** And we can do better right so there are a different tools that have different model massaging what we call like all the system prompts and pre-processing and post-processing. So there you will get different results by using different tools. But you can also use different models and not all models created equals. There are smarter models. There are models that just catching up and you can also apply methodologies to how model works and you can instruct it to pay attention on different things to do the right thing from the beginning. For example, you can use testdriven development, you can use behavior or specdriven development. you can verify what the model does in different ways. And the fascinating thing is that having the exactly same prompt that was kind of a premise that you will use the same prompt but everything else was different and we got wisely different results which was fascinating to see for us

**[9:04]** Because we obviously didn't rehearse it and it was fascinating to see for the for the audience because they didn't know what to expect

**[9:12]** And what was the best one? So you had six different approaches. Everyone, everyone wants to know what's you need to watch 3 hours video. I'm just kidding. so what the winners surprisingly we

**[9:25]** Yeah, we found ourselves in a very interesting position because in the past when we're trying to do this like a life coding and it usually takes some time and iterating over certain things will take some time and in this particular situation we our winners just spent first 15 minutes and after that they were done. the application was ready and all these specifications were you know implemented. So we found the dead brain junior did amazing job. we found the Kira from Amazon did amazing job and also common line tool cla claude claude cod did amazing job like f first 15 minutes we spent a little bit more time on kira just explain the approach with the specdriven development

**[10:07]** But in general

**[10:09]** That's you know that's what we have that's the results

**[10:12]** But you know to be honest we put this other ideas in slightly more constraint environments we use slightly less research models I would say.

**[10:23]** Yeah. So the winners all had an advantage of having the most advanced models. So cloud code uses sonet 4.5 which is amazing. Juny uses sonet 4.5.

**[10:34]** No no no on the juni we expected this to use sonet 4.5. We actually use GPT5 because

**[10:39]** By mistake

**[10:40]** By mistake because with the recent update the jet brain yeah jet brains put this GPT5 as a default model and it was still surprisingly how good it was. comparing to what we try to do with like GPT OSS and some like local models the hosted models are just amazing

**[11:00]** And the others were we would put them in a more constrained environment so we write for example the new gro coding model in visual visual studio code and it did what it could it's just a younger model it still has to learn so that's why it wasn't as impressive we tried Codex with obviously GPT GP5 and I switched it to thinking mode and it wasn't the thinking process the reasoning was very impressive it just take too long it didn't finish on time so the by now I think that the our take away from this we need a more complicated app to really challenge the top tools out like your Juni, your Sonet 44.5, our app is just too easy for them by now.

**[11:54]** Even though yeah, even though this app was not something kind of like go ride the microser, we wanted to have like something a little bit challenging, a little bit unknown. So that's why it's combination of IoT and microser and little bit of front end and doing some interesting things interacting with the webcam. So essentially on the webcam we can read the color from your t-shirt and the light bulb in the in the room. So that will change the color that matches your t-shirt. trivial not trivial but right it has some moving parts it has APIs that the tool need to research and what's not and in previous runs we started to do this talk I think like probably like 8 months ago it was enough it was challenging enough now it's not we really need to up the game and come up with something more complicated

**[12:39]** Yeah these models are trained on existing codes which it can find online I have very good experience with asking questions for Java Java Vix Vaden stable technologies.

**[12:52]** It's only when you start experimenting with the latest Java 25.

**[12:56]** It's a perfect It's a perfect we have an answer for that.

**[13:00]** Is that a challenge?

**[13:01]** It is. So the way how I started I got get into this type of set of tooling and instead of how I can use this efficiently. it's it's not like I using like new versions. I'm using very obscure piece of technology that maybe the models were no like very vague understanding. I'm in the world of Apache Spark, Apache Flink, Kafka and all these kind of things and I tried to way how I can build like Kafka or Flink applications. It was not easy unless until we

**[13:28]** We discovered one of the most amazing MCPS model context protocol servers out there called Context 7 and those guys provide up toate okay

**[13:41]** Docs APIs and context samples for any technology in the world and the way they do it with any technology in the world that they you can give them either GitHub or a website with examples and documentation and they will expose it to your models through MCP protocol context protocol protocol through MCP and it will learn whatever you needed to learn on the spot. It works amazingly and there are other ways to do it. For example, Simon Maple, our friend in Tesla, they do something very similar with their specdriven development. So there is something that they call the spec registry which is basically the same. It's a library of known documentation that will be downloaded to your machine for your tool to use and then suddenly there is no such thing technology that it didn't know.

**[14:38]** Even more so we actively instruct it against using any previous knowledge. Yeah.

**[14:44]** Right. We start with you know nothing Jon Snow about about this technology only use context 7 to get the right information and then it's magic.

**[14:56]** Yeah. So when you writing your prompt it's it's it's again the part of the prompt prompt engineering basically every time when we're trying to build some frameworks go and check documentation check how the certain classes for example the good example rest template versus rest client which new class in spring in the spring recent versions versus rest template that is deprecated they have some knowledge about rest template but we want to say okay so you don't have any knowledge go there and research this and it definitely change a lot. So that's why we start seeing like less confusion, less hallucinations on the model and compilation errors is gone. Yeah.

**[15:42]** No, like seriously with the right documentation, right code snippets and we know that the Spring AI has great documentation while the Spring Boot has a lot of good documentation. It just need to better off like attaching correct documentation bits and the context 7 solve this problem. You don't need to build your own rag to do semantic search or like a vector vector search of the things. It's already handled on context 7 and exposed in the MCP protocol to any model. Whenever ID supports MCP, I think that's essential. If you releasing new ID or new a coding tool, you need to have the support this because otherwise it's it's it's useless. it's you will be very far behind until you have this implemented.

**[16:25]** Steven Chin. I work at Neo4j which is a graph database company and I'm going to be chatting a bit about building applications using using agents using MCP servers so you can abstract out tooling and then also graph rag which is a way of improving on retrieval augmented generation by using graph databases as the knowledge layer.

**[16:52]** What's the difference between a graph database and MySQL?

**[16:56]** Fundamentally, relational databases store things in rows and columns. So, they're tabular data.

**[17:03]** Graph databases store things with nodes and relationships and properties on nodes. So, they're a more flexible structure. Typically, also relational databases require a schema

**[17:18]** To get started with. Graph databases are schema optional. So you can start with the schema if you want to, but you can also just start to populate it with nodes and relationships and

**[17:28]** So they can grow more easily. Let's say

**[17:31]** Exactly. And for AI use cases in general the large language models are quite good at constructing knowledge graphs. So you can ask them to build out a knowledge graph from a set of documents from other structured data sources. you can ask them questions about the data using a knowledge graph and they'll actually do searches and write cipher queries. Cippher is the equivalent to SQL for relational databases but for graph databases

**[18:02]** And it helps to kind of ground the LM if you tell it only answer using the graph database as the knowledge source for a question then you can get more reliable and accurate results coming out of the LM then if you let it kind of pull out of a vector database which is kind of a black box it's opaque or if it can't find the information and it starts to kind of hallucinate or pull information from its general knowledge store.

**[18:32]** So is such a database better for AI use cases?

**[18:37]** It depends on what your use cases and what you're trying to do. actually what a lot of companies are doing is they'll use graph databases together with vector

**[18:47]** Vector embeddings.

**[18:49]** And so one strategy is you know you populate all your vector embeddings. you can actually put those embeddings as properties on nodes in a graph. And then when you query the vector side and you get back some results, you can see which nodes come up highest in the list

**[19:09]** And then pull out related nodes from the graph databases using cosine similarity or community grouping algorithms and then you have a larger more relevant set of results to pass as context. Yeah.

**[19:25]** To the answer from then if you just gave it the s vector embeddings will give you similarity rankings only. So they're they're useful but usually it's incomplete like you won't get a full set of results back and sometimes you'll get back results which are similar but unrelated like you know you might get back fruits when you ask about Apple the company rather than

**[19:50]** Other companies other tech companies in the same space. you have a talk about this subject. What's the main thing I should remember from the talk?

**[20:00]** The big takeaway from the talk would be when you go to your friends and you try to explain to them what MCP is because I think everyone everyone's implementing MCP servers or using MCP servers that vendors provide. Don't use the standard USB hub analogy because it's boring and overused. And instead, a great way of thinking about MCP servers and agents is agents are like super robots that combine together kind of like, you know, Ultron or those like like historical ones where you get better with more agents, more powerful. And MCP tools are like the weapons like the swords and the weapons that you use to kill the bad guy at the end of each episode. So that's a much better analogy for agents and MCP servers.

**[20:48]** All these things are evolving so fast, but do we already have future proof workable solutions there or is a lot of this still experimental?

**[21:01]** Well, obviously it's experimental because a year ago it didn't exist, right? I think the MCP spec came out Novemberish last year,

**[21:10]** But I think that's quickly moved towards practical people are actually using it in production and applying it. agents came out earlier last year, but they I would say the patterns for the best ways of using agents and you know, how you use them with new language models and LMS keep evolving those that's kind of a moving target for the best use case for agents, but it has quickly become the best practice for how you build AI applications you want to put in production. A lot of people probably don't realize, but if you're now using Intellig IDE and asking some questions to the their chatbased approach, then it actually does a lot of MCP calls already behind the scenes.

**[21:57]** Yeah. Yeah. And you can add your own additional MCP tools which you want to use as well. and it's it is I think the fact that it's exposed to developers where you you're using MCP servers as you write code is helpful because it's also the best way to write your application to have an application where you're breaking out services and databases and APIs as MCP calls rather than doing a hard coupling where the LM is directly accessing resources.

**[22:26]** Yeah. So it's another way of exposing your knowledge or the data which is inside your business

**[22:33]** To make it questionable something like that so that you can ask questions on top of your

**[22:38]** Yeah one of the one of the demos I'll show tomorrow is using a standard lookup you know using using a vector database versus using MCP tools which can query the database schema can kind of navigate the graph with multiple quer queries to search for the information it wants and the answer you get back from the MCP approach actually is a more thorough response than what you get back just by looking up the vector embeddings. My name is Mario Fusco and well I have done quite a few editions now speaking about different topics. I started more than 10 years ago. You know it was the time when Java 8 introduced the lambda expression. So my very first talk was were about how to do functional programming in Java and then I focus it more on well my daytoday work at time was on D so D engine of red that so I also had a few talks on this topic the let's say

**[23:53]** The old fashion the AI before the LLM

**[23:58]** Was the this rule engine thing and then another area where I gave a few talk is when they introduced the virtual thread so I did a few talk about the concurrency as well and nowadays I'm totally into this agent and AI stuff because if you don't you are out of the world for some reason so I'm doing this and today I'm mostly participating to demonstrate the work that I've done in the langu framework.

**[24:36]** So LangChain4j is one of the main Java ways of doing AI and in code

**[24:42]** At the moment. Yeah, as as far as I know, yes, it's by far the mostly used framework and it is well integrated with many application framework. So okay, I'm a Quarkus contributor. So I also do the Quarkus extension that it integrates LangChain4j. But if you are doing Spring or Micronote, you can have some mis integration P as well. Yes.

**[25:09]** So what does Quarkus offer? If you combine it with LangChain4j,

**[25:14]** It offers a more declarative API in general. So instead of programmatically creating your AI services or agent, you can wire them with annotation in a more programmatic way. it offers CDI so you can have the automatic injection of the agent of the chat model and stuff like this and so the wiring is much more simple. and offer of course a lots of the enterprise feature that you want when you actually put this stuff in production. So you have observability. You can trace how many time an agent takes and how many times it is invoked. it offers some the security features and so traceability and yeah all all the things that you may want to use in an in a natural and the enterprise project. Yes,

**[26:21]** You have quite some history you just mentioned in Java. How do you look at what the AI changes are now bringing to how we develop applications? You mean this vibe coding thing or

**[26:34]** I don't like the vibe coding myself. It's it generates too much fluffy stuff.

**[26:40]** But can we do more things and other things if we use these tools? Now

**[26:46]** If we use these tools carefully and without and without ever turning off our brain. I mean it happens I I'm I'm honest. It happens some quite often now to me that I just wrote right the signature of the method and if I give a meaningful name for the meth name and for the arguments it generates the body of the method for me and sometimes I'm surprised because it really seems that it's reading my brain. I mean since u you know you have the project in the context of the LLM what impresses me so sometimes is that not only generates the code

**[27:40]** Which is mostly correct but it also follow my style of coding.

**[27:45]** Yeah. how I use the new line, now how I

**[27:53]** Yeah.

**[27:53]** The way blocks really the same style. It really seems that I wrote it myself. So that's that surprise me. Sometimes it seems that it reads my brain. Some other times it's a totally failure. So, but it's a tool and it's a boost for your dayby-day work. So, I use it daily. Yes.

**[28:18]** It's not a developer replacement. It's it's a developer tool.

**[28:21]** Tool. Yes.

**[28:22]** I use it as such.

**[28:23]** So, I really appreciate what Stefan often said is that you will not replace by AI by but a programmer that uses AI

**[28:33]** Knows how to use these tools. Yeah. as you talked about Java streams and virtual threads, what are some of the ongoing improvements within OpenJDK that you're really looking forward to that they get?

**[28:46]** Well, the things that Paul Sandos mentioning this morning are really exciting. I mean really we need a way to leverage C GPU inside from inside Java otherwise it's impossible to run inference of big model from the JM. so I'm really excited about this FFM new API and the work they are doing with project Babylon and project Panama.

**[29:25]** It's bit surprising that these two projects now seem to help to run AI based tools in Java while they started.

**[29:34]** Yeah. Okay. Even before they started well this happened quite often in our field. I mean you start doing something for a totally different reason and then it becomes useful in an unexpected way. So yes I believe I mean it's a combination of intuition but also luck. I mean think about Nvidia right? they start doing graphic card just for gaming and then it comes the blockchain. So people who starting buy graphic cards but just to do the bitcoin the mining that stuff and then they have been lucky for a second time because then it started this genai craziness and people start buying graphic cards but to do genai. So it's I don't know if it's called C and DPD I think but yes

**[30:28]** And the same for tornado VM which is already working for 10 years or more to run Java on GPS they're

**[30:36]** And then they find the perfect application. Yes. Yes. I'm following them and we are working with them to that they have a model to integrate longchain for JD with tornado VM. So that's that's really interesting. Yes. you're a speaker so you have talks what's the most important fact people should remember if they join one of your talks you can pick one

**[31:03]** Well this year I'm mostly speaking about LangChain4j the message that I want to convey is that you know it's not black or white there is a whole spectrum of possibility you can have agent that create a plan or you can create your own plan and coordinate agent and everything in the mix of the two. So my message for this year is that there are lots of amazing possibility with this genai but you really need to know what you're doing and this afternoon indeed I gave with a couple of colleagues a talk not about how my stuff work now how the new aentic model of J4J works what it does but how we get there so it's more about

**[31:57]** The journey than the destination. And the journey is very interesting because again we figure it out while working on this that there is not so maybe this is really the message especially in this field there is not a solution that fits all problem but you really need to be critic and figure out what's the right combination for the problem at the end. So you're talking about agents a lot.

**[32:30]** Can I create an agent for simple tasks for inance with LangChain4j? Is that a bit the goal

**[32:36]** To isolate problems into single tasks that you can assign to an agent? So one problem with big LLM and if you not only with big LLM but if you give a big task to it in one shot is that the probability of hallucination increases a lot. So what you do and what it works for us is that you try to split the task in a smaller step and maybe you can have an agent with a model that is specialized for a given field. For instance, I was speaking right now with friends that work they does medical software in Switzerland and they are using now specialized model of course to check to find cancer or to find specific disease in some pattern. so you really need to have specialized model. You need to break your problem in smaller task and then of course and this is what the agentical modules give you. You need a way to coordinate all these agents. You need a way to have a to run this task in a workflow let's say.

**[34:00]** Yeah. That's what we already discussed before in another interview is that one big LLM solving everything is

**[34:08]** Yeah yeah well

**[34:09]** Almost impossible

**[34:10]** It's almost impossible and again it increases a lot to the possibility of that something can go wrong

**[34:18]** So if you can create an MCP or have some data source with the truth about the specific subject

**[34:25]** Yes

**[34:25]** That's a better source

**[34:26]** Yes or now there is a way so you have remote agent specialized for specific task and you can connect the remote agent and you can mix local agent that you do specialized for something and a remote agent that you know that is good at another task and you can mix these two things in one single big agentic system. And probably is also better for businesses who have very critical data or personal data that everything stays inside your

**[35:00]** Company. Sometimes sometimes and this is another winning point for local inference for instance in Java because yes sometimes you want to send your data to a big NLM but by doing so you are also sending your data. So probably many cases he said for instance for the medical situation you don't want to do this. So you want to have local inference. You want to run that model locally and that's the exciting part of project Panama because they allow you to do this directly on the JM. So that's nice.

**[35:35]** So a lot of ongoing evolutions that will make us even better.

**[35:39]** But you see pieces are coming together in a way or another. So that's nice. Yes.

**[35:43]** My name is Yun Bankiser. I work for group 9 and I'm visiting here just to see some talks and also working on our sand to show to the people over here.

**[35:54]** Okay. What is the most fascinating change in Java for you in the recent years?

**[35:59]** Oh jeez. What a qu what a question. I've come away a long way back since I started with Java. I think I started around the 1.3 1.4 era. so in recent years the whole functional programming paradigm is yeah well I think a huge change that first of all and I think the whole change in I'm working a lot in enterprise development the whole working way more lightweight offloading stuff to the container yeah so just reducing your application footprint is a huge change I think

**[36:37]** Is it reducing to reduce costs. Is it reducing to make it smaller in maintenance?

**[36:44]** I think smaller in maintenance mostly. I think what you see is that there's quite a few logic for which we previously had had our big frameworks the java infrastructure etc which is now largely offloaded to stuff like the container kubernetes mostly infrastructure layer. so we can basically focus in our own application way more on just the core business logic instead of building a whole lot of technical code which is still fun to make but it's it's it's kind of offloaded and that's a nice change to see.

**[37:15]** Is that a bit change in how applications are developed? I liked it actually because it's it allows me to the most the applications I work on are quite difficult on a functional level and you need to really communicate with your business analyst with your quality assurance people and if the code is way more easy to comprehend for them because it's just business logic that makes it way easier to talk about is this is the correct thing that we want to implement and it makes for a really nice separation of concerns so to speak between the technical stuff and just the core business logic. So that's nice.

**[37:53]** Do you see any change related to AI and autogenerated code because you say you really need to understand the business?

**[38:00]** Yeah.

**[38:01]** Is that not the real strength of a good developer that you can translate a business problem into code?

**[38:08]** I think it is. I mean I'm I'm quite still curious what the whole AI transition will bring because if it is just having another layer on top where we speak sort of structured English instead of Java then a lot of work is still in understanding the business understanding the user in what do we want to achieve I think you see quite a trend where again technical logic is moved away from the application so that's stuff that we don't need to build anymore spring boot and stuff like that just automates it for us. you can really focus on that core logic and then understanding that is to me always a core quality that a good developer has not just being able to write good Java code but also really having a good understanding of the business being able to comprehend the business domain. Yeah.

**[39:00]** A lot of talks today.

**[39:02]** Yeah.

**[39:03]** Did you select the ones you definitely want to see? I have a set of talks which I definitely want to see. I missed one because I also have to do a bit of stand stuff but I was interested in the Java 25 stuff. I think I mean that that's just basic housekeeping to keep up to date with that of course. and there were some nice talks also on project Valhalla on Babylon but the integration with the GPU that I liked. So yeah nice talks that I wanted to see. Yeah,

**[39:34]** I talked at DevOps with the people from the tornado van who also do that kind of stuff bringing things from Java to the KPU.

**[39:42]** Yeah.

**[39:43]** You have use cases for that.

**[39:46]** No that that's that's that's mostly I think I'm working mostly in enterprise development. So there's not a real use case for that. So that's that's mostly just hobby being interested in that stuff. Yeah. But is that the fun thing that the boring business application stuff we can do with Java? We can also do a lot more.

**[40:06]** Yeah.

**[40:06]** And we can do this kind fun stuff. I experiment a lot with Raspberry Pies.

**[40:10]** Yeah.

**[40:11]** You have this whole evolution of trying Java on other

**[40:15]** GPUs and risk is there also around the corner, other processors.

**[40:20]** Yeah.

**[40:22]** Shouldn't we just play more with Java even?

**[40:24]** Yeah. I mean if if it's it's I think that really helps just playing with more with Java makes you really help with getting a real understanding what's what's going on the hood where can you use Java what are the strengths what are the weaknesses so and it keeps working in Java fun I mean workers still workers I think I like my work but it's just doing business stuff can be boring and it's it's a nice change of work and to learn indeed what's happening in Java new new APIs which are available.

**[40:58]** Yeah. And I think what also helps is if you do that stuff, it might give you inspiration on how to use that weird stuff in a business sense. I mean, I think a month back we had a discussion at our own company on how to use reinforcement learning. So not the whole mumbo jumbo around large language models, but just look at it from another edge and try to think about how can we use the more core AI principles in a business environment which is kind of hard but it's it's it's first innovation. It's first thinking about real other stuff and that's in the end I think our job is in the end is a creative job. I mean it's it's not just programming it's being creative. It's it's thinking about stuff and doing other stuff that helps that process. That's the fun of it. Yeah.

**[41:44]** I'm Martin Dorst and I am a speaker.

**[41:48]** Okay. And the topic

**[41:49]** The topic was catching the 137 killer.

**[41:53]** Oh. And that's

**[41:54]** That's a serial killer that haunts your that hunts for JVMs in your Kubernetes cluster.

**[41:59]** And why would it kill my JVM?

**[42:02]** Because your JVM wants to use more memory than is allowed. And then Kubernetes says no. And it doesn't ask nicely. It just kills your process.

**[42:15]** Okay. And what can we do about it? Is it just a configuration? Is it did I do something wrong in my codes?

**[42:22]** Oh, that's the that's the case. the presentation is a talk about an investigation on what you can see and what you can use to figure out what is killing your why your application is being killed.

**[42:37]** In my journey for this presentation, I could not find a culprit. I looked in Drafana in our logs heap dumps Java native memory tracking and I couldn't figure it out what was happening. So and that's the fun of this of giving a talk, right? you learn a lot and I learned quite a lot and yesterday evening at 9:00 p.m. I probably found the actual culprit. I gave the talk previously on another conference and that I reached the wrong conclusion there and that I figured that out the night before as well. So that's that's very stressful. having a presentation ready to go and then discovering oh well my last 15 slides are completely wrong

**[43:31]** Is wrong. So and fortunately yesterday evening probably we found the thing and with the help of large language models that helped analyze the native memory reports and it was just a lucky lucky strike. I cleaned the I trimmed the native memory of my JVM application and I put that in the report together with the native memory tracking report. So, let's see if if if it can find it. And it said, "WELL, OKAY, I FOUND IT."

**[44:07]** Java being killed. Is that why for people who are watching this podcast instead of listening, is this why you brought the juke being killed by a big

**[44:14]** Yeah, I like Juke. It's It's a really nice mascot. And I got this Juke way back. I probably

**[44:23]** It has a label by Sun.

**[44:25]** It has a label by Sun. And Sun is not all around for a long time anymore. and so my wife is very crafty so she made him a murder victim and but as you said we have logs you have heap dumps I guess if a process a Java process gets killed you won't see that in the locks

**[44:50]** The reason no

**[44:51]** No so the out of memory that's one of the tips in my presentation of course watch the presentation cost. Hey, that's it's a really good one. but I I'll I'll I'll give this tip. what we did is if you have an out of memory kill,

**[45:07]** The process is gone and you don't see that in the logs other than that your log stops. you have no recourse. So, you can't take a memory dump. there's no chance for a heap dump to be created, etc, etc, etc. So one of the things we I focused for a very long time on but if the out of memory kill comes along what is the status of the application at that time and you can't figure it out because the process is no longer there. So a co-orker of mine said, "Well, if we just increase the pot memory and limit the JVM to be

**[45:44]** Lower much lower than that and then just see what and then you still have the process and you can figure out what is causing the memory issue, what grows beyond what the limit is." So that was really nice. I also exacerbated the behavior of the program to shorten the time span in which the

**[46:07]** Before it happens

**[46:08]** Before it happens.

**[46:09]** And I limited the memory really memory use really really really low to see if it could fit in a very low memory profile. and then from there on it still was not visible in any of the dashboards in the memory dumps etc until I did the trim of the native memory.

**[46:31]** Yeah.

**[46:32]** And the large language model came with oh but then you need to fix these three parameters for gip c. So that's the memory library that JVM uses. And then the it should not it should not fragment your memory as much. And I never heard of those parameters. So, and I would not know to Google for it

**[46:55]** Because you don't know that they exist.

**[46:57]** So, and the large language models have really a big benefit for figuring out stuff you don't know.

**[47:05]** Such a very technical deep problem. I guess it's unique. You don't have this though every day with different kind of applications. I guess

**[47:16]** No. This is a very particular application. It spawns 150 threats to do a crawling a spider for status status information of our the tenant systems. I implemented it very badly. It's a very naive implementation because well and the issue was not I know how to fix it right I know how to fix the application that it would not run out of memory etc etc. the exercise was can I find the tools and all the dashboarding and such to analyze this problem

**[47:51]** To find the specific problem

**[47:52]** So that in other cases I have the tools readily available and that was the exercise not fixing the application because yeah the memory is cheap so we could just give it

**[48:03]** I don't know twice the memory it needed and then it would run indefinitely

**[48:08]** Is that the nice thing about Java that all this memory handling we can just ignore it. It happens behind the scenes

**[48:16]** For the most part. Yeah. Yeah. Unless you're talking about native memory

**[48:21]** Which is hidden from you completely.

**[48:24]** Yeah. Is this a problem you would only have in a Kubernetes closed environment which makes it even harder to get to locks into states of applications.

**[48:34]** I found it quite easy to get to the running process actually. typically if you deploy on virtual machines and such then you have to SSH into the machine you have to enable your VPN and such and if it runs in your Kubernetes cluster you can just go to your open lens thing and then say oh give me a terminal

**[48:57]** Or use cube control to issue commands directly to the to your container. this issue is prevent well the memory leak is always present of course. is it a thing that manifests itself in containers anywhere where you put a hard limit on your memory and if the hard limit is very fairly close to actual process usage. Yeah.

**[49:29]** So if you say I have 1 GB of extra RAM assigned, well then probably you will never see it.

**[49:37]** My name is Mullers. I've titled my talk I'm not afraid AI will take my job because I'm convinced it will not.

**[49:44]** Mhm.

**[49:44]** But we can't close our eyes. It will change our jobs for sure.

**[49:48]** For sure.

**[49:48]** If it already has.

**[49:50]** If it didn't do already, then it will. But I think for many of us it already happened. I see people around me using large language models to answer simple programming questions to even fill in pieces of code that they are like I know exactly what it should look like. I'm just

**[50:10]** I want it to be there and you probably know.

**[50:13]** Yeah. A bit lazy.

**[50:14]** Yeah. Well, lazy has this negative vibe around it. So, I'm I'm I'm trying to avoid that word. Yeah. It's it's a bit of a laziness. Like we both you and I we both know what should happen. Maybe a better analogy. they use large language models and the tools around it to delegate work to the work that they know pretty well what should happen. what the code should look like as if they had a junior programmer next to them. An intern maybe they can coach it. They can provide some guard rules and no you shouldn't be doing it like that but if you do it that way it looks better. so, so for those situations, yes, it helps. But on the other hand, if we weren't aware already, AI isn't that magical device that will solve all our problems. If only it were. But it isn't. And we should pretty well be aware of that, too. Use it where it makes sense, but know that it there are many, many cases where it absolutely does not make sense. Mhm.

**[51:15]** You're not a believer of AI will generate all our codes. It's not capable of doing that yet or it will never be capable of doing that.

**[51:26]** It is not capable yet and I seriously question if it ever will be. I think that a part of our job as engineers is finding good solutions that fit within the problem space within the context that we're operating in. And some of these things maybe you can tell them to a large language model and give that to them as a to the tool as a guard rail. But sometimes there are these these nitty-gritty details, let's call them that way, that highly influence your solution.

**[51:59]** Yeah.

**[51:59]** And it's very hard to make tools aware of that. And that's especially where I think we as humans provide the additional brains,

**[52:10]** But really literal brains that can fine-tune for the particular context that you operate in.

**[52:19]** And then there's obviously the kind of projects that we might work on that well we are doing something fundamentally new, something that no one has done before.

**[52:30]** And what is a large language model good in? It is good in repeating what it has seen before. But if I'm doing something that no one has done before, who is it going to copy?

**[52:40]** Yeah. As a developer, you should not be just coding. You are fixing a problem. So, first you need to understand the problem. Yes.

**[52:48]** And then you can find a fix for it. And that's where I think where AI is not even involved

**[52:54]** In finding the real problem.

**[52:57]** Let alone figuring out what a proper solution is. But if we know the solution, then we can ask a large language model. Hey, I'm looking for this kind of a solution. It looks like that. Take this as inspiration. Take that as an example. Model it for me. Code is for me. And I'll be the one that's reviewing your work. I'll be proving if that code should be shipped to production. Yes or no?

**[53:20]** Yeah. Okay. if you're looking to OpenJDK evolutions, Java 25 now being released. What's one of the main changes happening within OpenJDK or that already happened?

**[53:33]** I'm I'm really enthusiastic to see that the language is still evolving. I have a lot of colleagues who work on at and they kept making fun of ah those Java people when will your language finally have this or that feature and I think one of the great things that we see now with the increased release cadence is that Java is really catching up and adding lots of new things and things even that other languages may not yet have or yes well we have it but differently. so that I think is in itself already great and I also like the fact that from a platform side a lot of effort is going into even further optimizations like smaller objects like improved startup times. You could say after 30 years of engineering the JVM is a established product and it's done. Don't touch it. But no we keep on improving. we keep on getting the late the last bits of performance squeezed out and I think that's amazing.

**[54:36]** It's not the language only which evolves but it's also the runtime which with which contains a lot of changes with each release. Yeah, that is really amazing to see and we don't often see it, but when I was preparing the talk about improving your Maven builds speed for this year's DevOps, I noticed a funny thing. we had been running demos with Java 17 and then Java 21, but with Java 24, it was a significant improvement in speed

**[55:09]** Just by using a later Java runtime. That's I mean free performance gains who doesn't want that

**[55:16]** And that's why people should actually consider moving to each new release. I know for big operations that's bit difficult

**[55:23]** Let alone all the security improvements that we see and but there's enough reasons to stay up to date and I think especially the last few years it hasn't been particularly hard anymore. I mean sure when we were on Java 8 and Java 9 was introduced with the module system of course that was a big hurdle and not all libraries were compatible and whatnot and many many reasons I totally understand I was there just like yourself

**[55:49]** We've seen it we felt the pain but it isn't that case anymore now it's often a matter of changing 24 into 25

**[55:59]** Building

**[55:59]** Maybe at four places but that should be it right and then very small chance that something breaks, but I haven't any seen any examples recently. Have you?

**[56:11]** No. No, absolutely not.

**[56:13]** So, my name is Simon Maple. what brings me to DevOps? I've been a long-term kind of DevOps fan. done many presentations for Devoxes all over the world. So, and yeah, actually the company I'm with now, Tesla, new startup and, once we launched, it's like, oh, we've got to be at Devox Belgium. Let's do that. And what are you doing with the company?

**[56:35]** So the company is a new newly founded company. It's only 18 I say newly founded. It's 18 months already but we only launched a couple a few weeks ago. And it's really looking at how some of the amazing new AI technologies while being amazing can we actually harness them in such a way where we can reliably use them in a more spec oriented spec driven way to provide to go the next level beyond vibe coding and actually have something which is more production ready more verifiable more you know something that we can validate very deterministically. So it's all about how spec driven can take us to the next level with AI. So you are using AI on the developer side.

**[57:16]** Absolutely. It's all about developer workflows. and of course you know trying to look at it a little bit more natively. So we're trying to you know think about AI native and what that means. So ultimately if the developer environment were to be looked at from the ground up again using AI as a core first class citizen, how would that change what we do? And we lean into specs more. and while yeah from my personal developer point of view it kind of like moves me a little bit further away from the code it's definitely you know remains that creativity that developer almost architect style creativity around how things should be designed how things should be built and organized. but yeah that's the that's the model we're aiming for.

**[58:01]** So AI is great. I use it a lot for experiments. Give me an example of how I can do this. But building something completely AIdriven is a bit far away or

**[58:13]** It's very challenging. We love agents. Agents are very very powerful. they there are a number of ways in which agents can kind of like fall apart though. I'm sure we've all you know your listeners have kind of like fallen into the trap as well where they make one change and actually it affects several other things like this collateral damage across the application. Sometimes it spirals and you just can't get the thing right because it doesn't have it in the knowledge base. it doesn't have the right context. So there's a ton of things that we're trying to do that kind of first of all makes agents more reliable, more accurate. so we're trying to provide a registry of specifications completely open beta right now and completely free to use and the idea is these specifications describe how an agent should use libraries. So open source libraries each of them have specifications by version and that specification describes the APIs that exist how to use the APIs examples of best practices of using those and then when that information then gets sent well it actually gets

**[59:15]** Installed on the machine which just essentially means downloaded onto the local file system. We spawn off a little sub agent which then based on the request you ask takes the right context from each of those usage specs and passes that as context to the agent

**[59:29]** The main agent thread and that main agent then has a greater chance of being able to accurately write code generate code based on the fact that it's connecting with these APIs. Exactly. Yeah. So without that agents can code against the wrong versions or hallucinate APIs far far more easily. Yeah.

**[59:47]** Yeah.

**[59:47]** How are you looking at all the evolutions now both not only on developer side but also what you can do in your application with AI?

**[59:54]** It's it's amazing frightening. It's all of these it's like I think one of the things particularly working in a company that is using AI. I've never seen anything travel so fast in through an industry is as what we're seeing right now with AI. I think there's definite there's definite being you know people wanting to be able to run before they can walk. I think is absolutely imperative that when we build with AI both in our development processes and our applications, we do so with that sense of understanding of you know the fact that this is can generate anything at any time. which is obviously during runtime a little bit a little bit more challenging. but we can add guardrails in and I think guardrails are the key to not just effective but any kind of reasonable AI usage in development. Yeah, tooling is one of the best things we can do we can use to make sure that you know we are guiding the processes at the best of times but also recognizing what we need to do better from a code review point of view, how we don't burn ourselves out from just being code

**[1:01:02]** Review machines. but also these guard rails that we can add in through tools in runtime as well. guards that you know effectively are points which validate input, validate output. You know you give it material that you are happy for it to spill. all of those types of things. So it's it's incredible what it unlocks. But yeah, it's very challenging to make sure we use it we use it morally, ethically and you know without security issues and those types of

**[1:01:33]** And not trusting it blindly.

**[1:01:35]** Oh gosh. Yeah. Not trusting it blindly. I mean everything you know a lot of people say treat an AI like a junior developer whatever it puts out you know would you put that to production. yeah it's it's like you know they are your brand. If you put them in your rout they are your brand. And so whatever they put whatever they share you've got to be valid. you got to be you've got to stand behind it and so yeah, you always got to make sure there's either some level of human touch at that point when something critical is going to happen or you put the guardrails in so that it can't do things that are potentially damaging to your company, your brand, etc. if it gets the chance.

**[1:02:12]** And that's a wrap for this episode of the Foojay podcast. Thanks to my guests for sharing their stories and thank you for listening. The big takeaway from all these conversations, AI is really helpful, but you can't just blindly trust it. You need to understand what's happening, ask the right questions, and know when the AI is giving you something useful versus when it's just making stuff up. The Java world keeps evolving. We're getting KPU access through Project Panama, performance improvements in every new JDK release, and better tools for working with AI. But the real skill is still about solving problems, understanding your specific situation, and making good decisions, not just letting an AI generate code for you. If you enjoyed this episode, please subscribe to the Foojay podcast on YouTube or in your favorite podcast app. You can find all our episodes, show notes, and links on Foojay.io.

**[1:03:09]** Follow friends of OpenJDK on social media to stay uptodate on what's happening in the Java community. Until next time, keep coding, keep learning, and remember, AI is a powerful tool, but your brain and experience are what turn code into something that actually works. Thanks for listening. Be the friends of OpenJDK.
