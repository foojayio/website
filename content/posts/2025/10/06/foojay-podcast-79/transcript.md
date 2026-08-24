**[0:00]** This is part one of my interviews from the AI for devs conference.

**[0:05]** Give me a OpenJDK.

**[0:10]** On September 19th, the first AI for devs conference took place in Amsterdam. I grabbed my camera and microphone to talk with speakers and attendees about the revolution in AI powered coding and application development. In this first part, we'll explore Spring libraries, security infrastructure and scaling, real world use cases, event streaming, Jet Brains tools, and more. I asked all my guests the same opening question. What's your name and what brings you to this conference? Let's get started. My name is Christian Solo as Joshu part of the spring team. I'm co-lead for the spring project.

**[0:51]** My name is Josh. I work on the spring team. lucky enough to serve my friend Christian and the rest of the team on in you know in my capacity as a spring developer advocate and I'm very happy to be here. Thanks for having me

**[1:02]** Developer as well. I mean as as he pointed out the chat chat client abstraction this great story maybe to take more than 20 minutes to tell the story with how it happens some but I love yeah it happens here in Amsterdam but we're trying to find a proper obstruction. We spent a week here in Amsterdam. He flew from New York here to work on this. We came with some API. We were happy and suddenly Josh come around and say okay it's not bad but it's leaky really out fashion voila and not not surprisingly the chat clan has very similar vibe now like the yeah because of all of us and yeah so actually after this trip Josh started drafting the API and that's how it comes so all this kind of collaboration and ideas and stuff without him and all other guys it wouldn't be what it is today and that's that's the amazing part of this

**[1:51]** And that's how these open source things grew.

**[1:54]** Yeah.

**[1:55]** And you're also a coder in airports in hotels in that's why you do the best work.

**[1:59]** I Yeah. Because you get all this that or I listen to books, right? Like but you get this incredible I mean how often do you get 10 hours of complete time with no interruptions except for coffee and bathroom

**[2:11]** And I enjoy the same thing. And also changing the environment sometime can really have a good refreshing.

**[2:17]** Yeah. You've got an office window with a view looking out on the sky.

**[2:20]** Okay. you gave an opening talk today. You were very happy with the talk. Nobody died.

**[2:25]** Yeah.

**[2:26]** What's the main topic that you're talking about today?

**[2:28]** Well, so I mean I work on the spring team as we mentioned. That's an enterprise Java technology. Some people may have heard of it. and this is an AI conference. And my point was that Java is in a uniquely amazing position here in the AI space. We have an opportunity that has never been nobody has this opportunity, right? we can take the enterprise the business logic that runs the enterprise that runs the world

**[2:52]** That's written in Java

**[2:54]** And then extend it with these AI capabilities and you know there's no I can't think of an easier way to do it than with Spring AI but the point is that we can do it you know that this is an opportunity if we as Java programmers don't reach out and grab this opportunity it's going to be a waste you know we have to we have to

**[3:13]** And I'm amazed how fast things are moving two years ago there was AI.

**[3:17]** Yeah.

**[3:17]** Now we have

**[3:18]** Three years.

**[3:19]** Now we have Spring AI, we have MCP.

**[3:23]** Isn't that the strength of the Java community that you have all these people pushing this forward?

**[3:28]** Oh yeah.

**[3:28]** Yeah. That's amazing. I've never been as busy as with this as with the spring project and you can see even the chart of the stars and on GitHub repository the tons of PR we cannot process. So it's a really blame on us and issues. So it is I've never seen on the spring as a part of spring projects as vibrant one that's bit now MCP came as a something small I we started working on it as because it was very important as an integration capability for to let you connect Java to any any other one vice versa you can actually write anything and then there is no this schisma to use single platform it's kind of interoperable so but again it started with something sim small we make the first release you contributed And it's keeping me busy. Yesterday actually I had to miss the dinner for so because I was making MCP Java release stuff. So it is and the community is asking you because we actually we had a partners enterprise partners. Yeah, you have to make the release because we have actually blocking production product that actually so it's and yeah no but yeah and yeah I've never been as

**[4:35]** Busy and with this and I love it. So that's a strange combination. I'm not sure my wife can actually share the same experience because it takes a lot of to this bit but it's been a journey and when we talk about the job I really like the idea about the integration part if you think about it is the if you want to use genifi and something like this you need to solve couple of problems memory as you saw you would like to make the genifi which is frozen in the past stateless aware of your data in order to be useful and aware of your APIs using two column so if you solve these three problems at the very low you can make the generative AI useful for almost any domain. And guess what? Java and Spring AI and Sprint are perfect for this solving this type of integ they they've been solving the Java has been solving integration problem in the enterprise for 30 years now. So it's very good and it's very good mixture about this.

**[5:26]** Couldn't agree more.

**[5:27]** Yeah, I'm now very familiar and I think most people what you can do with AI is you can have this chat interfaces. What is the MCP offering?

**[5:34]** Mhm. Yeah. So if you start building AI application normally you would have to connect AI to AOM all this orchestration prompt engineering with very little engineering part inside but yeah very good principles and then you have to add a capabilities I mean the gen generative AI and or your AI application is very useless if it cannot interact with its environment. So in order to interact with environments AI models already had this concept of tool calling and which we demonstrate. So you can actually build this stuff but anytimes as a AI application developer if you want to build you really have to take care and to build this connectors to this external world. Anytimes you build your applications you cannot really reuse these capabilities and MCP model context protocol is the context stands for managing the context that gets in your prompt aims to solve this problem to provide unified standardized access for the any AI application to the external resources prompts that goes beyond tools. This practically provides you a standardized interoperable way for your application to

**[6:36]** Connect to external resources and it naturally creates two communities developers server side you as a server developer you are familiar with your enterprise service APIs you can build a very tiny shell around these bits because you know your external internal APIs you don't know anything about and details but you still can build this small shell around your enterprise and make your existing service accessible through and AI So this is the one community. Then you have the AI application developers as I mentioned. Now those guys do not have to care about implementing their this capabilities up front as a part of their application. They very easily can connect to desired selected application with the small clients into their applications

**[7:17]** And they can do this at start time and even better the MCP is birectional realtime application it can change in the list of tools list of stuff. So even at the runtime you can your application can get be get can get enriched with additional tools and stuff without so now the application developers is not concerned to build the details and know how this back end services work in order to connect to them he just can opt and configure this and so this is the premise that MC proide and that's why I think it's so popular because it creates this kind of distinction between two development group and it they complement each other so if I'm developer of a backend service that manages the orders of the company. The MCP answers the question like how many orders did we get yesterday? Is that something that you would ask an MCP?

**[8:03]** Okay. So that's interesting question because it very often people get confused with MCP and REST client REST APIs, Swagger and all this grow graphql stuff. They say okay why I have this API servers why I cannot expose this directly as a MCP server? And the answer is my understanding my opinion is that this is a wrong approach to map one to one the MCP abstraction is the gateway to for this backend server to the AOM you're trying to provide usually it's high cross grain so it uses the back end APIs but it tries to provide a service that you indeed expect your user to interact with one of those is for example to provide certain account questions so then you building and this is one of the way so you're exposing some subset to access to the back end service back to up to the expectation and desire that you would like the M to be able to interact. So this is this bridge and it's unified because you can write this in Python and JavaScript and stuff and then use it from Java JavaScript Python net. So this is kind of this another very beautiful things. It kind of breaks the

**[9:16]** The boundaries across the different bits. But indeed and then you can combine multiple different services and then bracket it adds a lot of capabilities to stuff.

**[9:27]** One of the things I love about what MCP is it's a simple protocol. It's not that simple but it's a protocol and it's it is something you can scale out in a consistent way. So it doesn't matter what the thing behind it is, right? And because we have the ability to scale out

**[9:46]** Services these days,

**[9:48]** You know, now you can actually scale out your AI

**[9:51]** Capabilities, right, with the as you scale out these services. I know that's an obvious point,

**[9:56]** But

**[9:57]** It's it's a protocol. It's a stateless. It can be stateful, but it's a stateful stateless protocol. You know,

**[10:03]** Actually, it's mostly stateful, but it can be stateless with the latest things. This is which is very beneficial for

**[10:10]** What are the real use cases you see now? Are people moving on from experimenting to using this in production and what kind of use cases do you see there?

**[10:20]** Around what around AI

**[10:23]** My goodness what aren't they doing? So the I don't know I mean it's sort of you've got the chat model that's the use the obvious thing but for me I think there's a lot of like just using it to sort of intervene quickly in an automatic thing where humans might have otherwise or the service would fail right I feel like it's very useful to have it as a backs stop obviously and then of course as you start to get more sophisticated use cases people are building these sort of agentic workflows right and that's that is the sort of the you just put these chat clients these models mod in a flow. You know, if you can orchestrate them, you can get more interesting results than if you just did one shot

**[11:02]** Request. And that's I think the next frontier.

**[11:04]** Okay.

**[11:05]** Yeah. Unfortunately, I'm this dev guy sitting behind the scene. So,

**[11:10]** You're not exposed that much. I really love to, but because it's very vibrant community, you can't Yeah. You tend to hear about someone is using it in that in the other question. So people apparently did people have been using spring in production before it was G release which was scary for me but they were doing it and they were coping with all breaking changes we constantly introducing which was painful but they

**[11:34]** Before it was G

**[11:35]** Before it was G people were using it for various production reasons I don't yeah I know some enterprise companies but I don't think I can share the names about this but they they're using I can share one interesting fact the first time I heard people using enterprise spring in production was banks and it was bank industry and I was really surprised. I always assumed that this is very conservative environment but they had a very clever use case for this. They using it for internal for example chat clients for the customer support. So practically they were building this internal environments not facing the end users which provides some sort of security and yeah and they could benefit from this. So

**[12:17]** Yeah. Do you remember the migration to the digital office, the Xerox, you know, no paper, zero paper office, right? And for years we spent time digitizing analog workflows, right? And so the goal was to get these existing things

**[12:32]** But on computers, right? It wasn't there's a compounding effect. Once everything is digital, now you can build things out of those atoms that you could not build before, right? That just were not possible. But you can't get there until everything is digitized. And I think right now how do we get you know what can we what can we a AI what workflows can we take that are now and make them to sort of

**[12:53]** Driven as much as possible or at least augmented heavily via via AI and then once we've got that now you start looking in the compounding effect and that's going to be the next wave I imagine you know

**[13:03]** Yeah workflows are patterns yeah

**[13:05]** And pattern matching pattern finding patterns in workflows is exactly what AI can do so there's definitely a combination there

**[13:13]** What can we look forward to the release of the MCP M2.

**[13:17]** Yeah. And what Josh mentioned that big story is agentic works and stuff and there's different way how to achieve this. We already have some groundwork based this but being spring framework we working from ground up. So we're trying to build a basic boring box rather than trying to jump and so the next level we are working right now is what are the missing building blocks within the agentic if you define so one one important part of the agent we have I can argue that we have almost everything I've been implementing and playing with the spring to implement various agentic workflows to stuff is you have one more important component is some agendic loop this is practically the ability for sing single agent or multi- aent system to iterate introspect its response decide whether is received and so and with this help of the advisors now we have a prototypes looping advisor that would allow you to do this thing so again this small building blocks my premise the premise I looking for is that you should be able to have all components to build anyone points you to some agentic

**[14:23]** Workflow okay I can build it for a day with spring yeah just because I can all because there's a single identic framework something on the springi side that we can expect in the near future.

**[14:35]** Well I showed the old approach to building MCP services today come to his talk later today much produce now annotations for MCP to try to make it little bit more

**[14:48]** Easy to developic. Yeah. So it is just a declarative layer to sugar on top of what we already have and our colleagues from this from the spring security team Daniel is working day then night. So very soon he's going to present the all to full support within the MCP as well with the spring security support. So with all digital is very tricky space to evolve but we work hard. So the idea is that MCP Java the core MCP Java SDK just provided small hooks. There is a tension to be to push the all out implementation inside the SDK. So we're trying to push this because this is not typical for Java ecosystem. But it is apparently the way how the Python and JavaScript SDK did the stuff. So they really dumped the whole implementation which is scary for me.

**[15:36]** That's how and it's very hard to argue that that's not the right that but what's interesting the net community has the same problem that Java because I think it shared the same organization yes so pluggability is very important but having said this so we have provided the basic hooks inside the ODK so everyone can build on top of course Daniel he built hooks for top of this for spring security and this is coming so he actually is going to present this in deox And this is another things that I cannot wait to see it as a fully because it's been a very very frequent and request from the enterprise. Yeah,

**[16:14]** The R&D work for that has been because the spec has changed so much. It's not like the I mean the R&D I mean we already have full OOTH support and we already have Spring AI. How hard could it be to get these things to be together? No, it's a spec. It keeps changing, you know. Daniel Go and Joe Granja and the rest of the spring security team and the spring team everyone

**[16:34]** And the MCP Java you know all working together and special thanks also the other maintainer of the SDK is Darius our colleague he's a maintainer for the reactor project as well and the protocol is very involving because of this birectionality implemented on top of SSC this is server sent events and stuff so it's kind of it doesn't Yeah, currently it has a lot of dynamicity. So, the reactor is and his expertise is invaluable to implement this week. So,

**[17:05]** Could agree more?

**[17:05]** Yeah, it is.

**[17:07]** Hey, my name is Brian Vir. I work for Sneak as a developer advocate/engineer and yeah, my stuff that I normally do is looking at Java AI and cross reference with security because a whole lot of things can go wrong. and how do they get wrong? Are we looking at authentication or are coming up with answers who are not related to the question or the product companies are selling?

**[17:36]** Yep. I think all of them all of the above like authentication authorization is obviously a thing but I think in most cases the problem that we're now having with AI infused applications is that people forget their basics. as in normally if you have a front end you probably check whatever comes into your back end

**[17:59]** People tend to forget that now now there is AI involved they also don't look at what's coming out so like input validation output validation what could possibly go wrong and it's getting more difficult because it used to be quite easy as in you put some sort of rags in and you can

**[18:17]** Filter things out

**[18:19]** Now we can use natural language into an AI I or an LLM and we can work around it with different phrasing and that kind of stuff. So these kind of things that we normally already needed to do is now are now exposed as well when you are infusing your application with LLMs or any other AI

**[18:40]** And are they very hard to solve? If I was experimenting with a tool that checks the documentation of Azul to answer Java related questions, but you could still ask Python questions which the tool cannot answer. So how can we prevent this? Are there tools available libraries? Is it just checking some words or preventing it to answer some words?

**[19:04]** It's a very good question and there is no clear answer to that. Like I cannot give you the oh do this and you're done. it are a there are a bunch of things like first of all if you use an LLM that is generally trained on general purpose answers it will try to help you. So you have to put in guardrails.

**[19:22]** Yeah.

**[19:23]** Making sure that your LM only answers the stuff that you want to answer. For instance, a very strict system message makes sense. having input guardrails and output guardrails that check if your if the input is valid and maybe come up with something like hey I only answer Java questions so if you come up with a Python answer we are not the authority to talk about that but also think about what kind of other stuff can or is infused in that answer because if you look at an LLM from an API point of view an LM is nothing more than an API call and it's stateless If I call an LM from Entropic, from OpenAI, from anyone, even on my own machine, Olama,

**[20:06]** It's a stateless thing. So everything is bound to the request,

**[20:11]** The context, maybe the stuff from your documentation, which is which where we use rack to only have the stuff in it, maybe a chat history, that's all part of that same request. so these are all attack factors. So, if I can poison your rag, I can make sure that your LLM changes the answer into something that might be out of policy or out of the scope that you wanted. If I can infuse a previous chat history with some weird statements, it tries to act upon that. then is the question, what kind of model are you using? So, there are so many different aspects of that can happen. And then next to that is like okay if you have that model and it's just answering that is okay then it's just text

**[21:00]** But what if that model can also execute tools autonomously if the tools are yourself you can again you can interfere with that you can check

**[21:10]** When are you when do you want that is it the right person that logged in for instance it does that delete this user from that's probably not something that is like logged in as a anonymous user right and then we even have more with MCPS that we actually don't know. We just make a call and we have no clue what's happening on the other side. So these are all things that we need to think about as developers.

**[21:34]** LM is just another component that we can use.

**[21:37]** But we need to think as a developer as okay, what kind of state machine do we have in my application? What kind of input does it have? What kind of output does it have? What could go wrong? And not think like oh this is great. Now we can do 10 times as much as we used to do. let's just push it to production and

**[21:55]** It's and I'm telling it to a lot of people like it's I'm telling the same things as I'm as I was telling people 5 years ago or 10 years ago about you need to do input validation you need to do u parameterization with SQL injection things

**[22:11]** It's basically the same but now with a different format

**[22:14]** And do libraries like LangChain4j has has it some kind of system in it for these guard rails Yeah. Yeah. Yeah. I mean if you look at the most of the tooling or most of the frameworks do have something in place. I know the LangChain4j has annotations for input guardrails and output guardrails then you can get these input guardrails and output guardrails will intercept that message. So if you put an input guardrails or you put multiple input guardrails in, they will intercept that message and check it basically a boolean check or an integer check like if it's a certain score

**[22:49]** If it will pass it to the actual LLM service or to the actual so you can basically it's almost like aspect oriented programming that we have you wrap something in between. So the most of these frameworks do have something in place. what these frameworks also have in place is like okay forcing structured output. So not just a plain sentence but a structured for instance JSON output that you already force your machine into something that you can

**[23:19]** Can

**[23:19]** Check

**[23:20]** Check and validate.

**[23:21]** So with the would that help you can already do a bunch of stuff but the rest is like there's a lot of programming around that if you want to infuse your applications with LLMs that you need to do yourself. if anybody says we are out of work, we are not. We have more work. Trust me.

**[23:37]** Yeah. It's it's not only implementing the whole thing with chat and then using some models behind it. It's also the whole flow around it which needs

**[23:46]** A lot more work than just doing some escaping of accents and then SQL injection thing. Absolutely.

**[23:52]** It's actually more complex nowadays

**[23:56]** To integrate this safely.

**[23:57]** I think it's it's it's getting more complex. and it depends on what you offload to an LLM. Some things you don't want to offload to an LLM because you want to have it as a deterministic process. Maybe just a co a piece of code will be very more deterministic than an LLM. If you have an LLM solving things, you have to be aware that some things can go wrong. So then you have to do threat analysis. Do I want my LM to do this? Yes or no? Because what could go wrong? And if you limit your service into only doing the essential things, the necessary things and giving the necessary tools and functions available, then you already limit that scope. And then it's just then it's basically back to architecture and threat modeling like what could go wrong and how much do we want to invest in that and that is use case dependent obviously.

**[24:50]** So defining a clear documents before you implement something about what can go wrong which is a actually a front thing. Yeah, I've been in this exercises before where you just go wild. Imagine everything which can go wrong

**[25:04]** And then try to determine will it happen and how bad is it.

**[25:08]** So actually I think more people should do this when they start implementing something.

**[25:12]** I think in general people should do this as part of the implementation process. figure out your scope, what is in scope, what is out of scope and then figure out what can go wrong. And that is for deterministic processes as we used to program for decades.

**[25:30]** But now for nondeterministic processes just as much like we need to like what could go wrong have a threat analysis and how much can it go wrong and if it goes wrong what kind of costs or effects does it have? So do we need to mitigate yes or no? And then you can make a fair assessment of what you need to do about it. But it's always more complicated than just hey co-pilot vibe coat this thing for me.

**[25:57]** Okay. But the clear message is if you think you're losing your job as a developer because of AI actually it's not.

**[26:05]** No it's

**[26:06]** Your work will even become more complicated and you have to think about more corner cases.

**[26:10]** Yeah. What I what I think you've got two two cases on AI for now is in hey AI can help you generate an application either vibe coding fully or help you as a tool or you can infuse AI in your application that solves certain problems for that. If we look at the first one, I think we need to have more specialists because if I like I tried vibe coding some some some stuff and yes, it worked. And then I looked at the code, it was like this is horrible. This is really horrible. So I had to come up with things like, hey, we're using Java 21. Please implement records as well. Don't use use the onboard HTTP client and not some some some some library from a patch or something like that. I and it's like oh yeah sure you're right. So you need to know what's in there. You need to know what's happening, what's available.

**[27:02]** Sometimes it goes overboard with all sorts of exception handling that I don't want at that level, but I want at a higher level. so you need to know what you want. So there is a process before after, but the technical the actual coding some parts you can you can let your generative AI assistant help

**[27:23]** But you need to know what you're doing.

**[27:25]** It's still still it's knowledge. This is a tool. Mhm. That same holds for if you have your LLM in your application, you need to know you don't don't give it all the services and all the things in your application and because that could go wrong.

**[27:38]** Yeah, things can go wrong. Keep that in mind.

**[27:40]** Things things will go wrong.

**[27:42]** Will go wrong. Absolutely. But hey, u I think with all this AI and LLM infused applications, I will be into a job for a long time when you're in security. So if you want job security, I think you should go into security. security was great.

**[27:58]** So my name is Kami. I'm based in Switzerland and I'm at this conference because I'm very passionate about AI and this conference guarantees a lot of coding.

**[28:08]** I'm Martin passionate about AI as well and upd which is my background here at the conference mainly then to talk about software and AI to keep in control of the data and because yeah it's dev oriented it's a cool format. So you both gave a presentation. What is the main thing we should remember after your presentation?

**[28:30]** I think that's doing so keeping control, staying control of your data, staying control of your models is not as hard and complex as you think it is.

**[28:38]** Mhm. we talked before about MCP and AI and security. What are some of the practices that I should follow if I'm developing application which allows users to do something with my database for instance using a chat interface is that a good idea it is a good idea but it's which database which models how complex to have buzzwords the cognitive load how complex it is to get it starting to get started with it as a developer that's something with the platform engineering concepts we try to solve that you have an abstraction layer on the infrastructure on the bootstrapping of these applications that you can start with zero in between quotes complexity and in a couple of clicks a couple of minutes that you're free to go with the default configurations.

**[29:28]** How complex is implementing AI in an application?

**[29:33]** I don't think it's a very complex topic in itself. but I think you should follow best practices. this is very important. and also be aware of monitoring that monitoring is very important for applications. Yeah.

**[29:51]** Which tools can I use to do that to achieve such a thing? for example, the Quarkus libraries if you want to start with it, which goes very well together with LangChain4j libraries and then yeah on local model level for example VLM as an yeah tool to deploy your local to deploy your models in a local Kubernetes environment for example.

**[30:13]** We also have an open source project called trusty AI and this is a project which enables you to make your LLM safer. So you have there the opportunity to include for example reg x pattern. If you don't want a customer to a customer of yours to use your chatbot for different purposes you can just block some some some words using reg x patterns. and you can also make sure that your customer doesn't use your chatbot using bad language. So there are some profanity language models that you can use to do that. I just discussed just with Brian Vermier that for inance I was experimenting with a chat interface to use the Azul documentation to learn things about Java but still I could ask it questions about Python. Is that the thing that you can prevent with this kind of regax filtering?

**[31:07]** Yes. So reg there are multiple ways but regax I would say is the easy way because you can just block some words. the reason why you should block for example in this in your use case the word Python is that it also cost you money right so you have a product about Java

**[31:26]** You want your customer to use your chatbot main your LLM that you are hosting etc it costs some money to host all this and you want your customer to solely use it for your product so you don't want to start a huge story about Python for example.

**[31:44]** Yeah,

**[31:44]** It's not that I want to ditch Python but

**[31:47]** We are in the space

**[31:48]** But I think it's it's it's very a fair question because it's so expensive it can be so expensive doesn't have to be you can use quantized model where it's less expensive but still you don't want to invest resources and then your customer are using to explain their problems at work and as a psychologist this is not the goal. Yeah. And the fun part is with this new era of AI that old components are coming back to life. For example, what you're saying that you should not ask a poem to your Yeah. web web shop application is it for example in the pre-g guard rails in Kafka system in Alama stack system that you have for example the good old rules engine coming back to life to do validation on input and output as well

**[32:33]** That you don't have for example the famous Tesla chatbots that's saying you have to buy a Mercedes something that you should avoid as well

**[32:42]** Maybe we can agree with that but that's not that's another discussion I guess you both have quite some experience with Quarkus. you have Quarkus plus LangChain4j on the other side al you also have Spring AI. What is the difference? Is it just two tools that I can use as a developer if I have experience with one of them? I can build whatever I want or is one better than the other?

**[33:10]** Quarkus is always better, right? That's the answer.

**[33:12]** That was the answer I was expecting. No, I think the main difference there is that Quarkus started in the need for cloud native Java where spring with the reflection backpack. Yeah, it's less flexible if you look at the cloud development for example. So the answer was among others like micronote as well quarkus to have cloud native java and then a lot of the quarkus engineers worked together on the len chain for j project as well. So both both now are standards not a custom dialect of these tools which are built by some same engineers. So yeah it works pretty well together there and then you have the cloud native aspects the build efficiency and all these things that come with quarkus. Yeah, you already mentioned with George Long, it's how amazing it is that this Java community has picked up these new technologies which didn't exist 2 three years ago and we now have all these amazing libraries like Langchain, 4J, Spring AI which can do all this kind of stuff which was Python only a few years ago.

**[34:18]** Yeah. And now it's mainly shifting to the Java ecosystem. Right.

**[34:21]** If I want to use AI in production,

**[34:24]** Should I look at Java or Python? If I have experience with Python, what can Java offer me more than than Python libraries or Python tools?

**[34:33]** I don't think it's an AI question. I think it's the default question that was there as well a couple of years ago. Why Java instead of Groovy for example, Java with the object-oriented solutions you have around it? It's more mature for enterprise use. In the AI scenery, it's the same story now, just with other libraries to connect to AI models instead of databases.

**[34:57]** I think you should be more careful with which LMS, which AI SS am I using to think about data and data linkage.

**[35:05]** I saw the presentation, a short piece of it. It was a lot of Harry Potter theme. is AI magic?

**[35:14]** It feels like magic.

**[35:16]** Our presentation is magic. I would say but no the models themselves as well as all the infrastructure around it is based on humanmade patterns right I mean it's based on mathematics in the end it's probability that's what is behind it it's linear algebra but it's not it's not magic and this we shouldn't forget in my opinion everyone should be able to understand the general concepts of LLM because this is becoming more important in our society and those are not extremely complicated concepts because we have made them themselves as human.

**[36:02]** It's not like some topics in medicine for example that are extremely complex because we have not

**[36:09]** Invented them right they were there already. So that's my opinion. Yes, education is important.

**[36:15]** Okay. My name is Luca Berton and I'm just happy to be here.

**[36:19]** Okay. You're happy to be here because you're a Java developer. You're doing something in the cloud. Whatever. What?

**[36:26]** Well, actually my specialty is more about infrastructure and everything is about AI nowadays because I mean things workload are getting more complicated and I would love to serve better our final user.

**[36:40]** Mhm. are you working with a big company and having a big load of applications?

**[36:45]** Oh yeah I'm working as a professional services and I have a privilege to enter in a lot of different companies especially on financial institution and this type of real high regulated company.

**[36:57]** Mhm. and do you see that there's a big demand of new services needed to run AI applications

**[37:06]** Or are people still experimenting with it? Well, my feeling is that there is a huge appetite because we I mean we all experiment a little bit with our CH GPT but I think that this is we start like to a lot of companies start to find a value. I mean myself I was traveling recently and I use a lot of this generative AI to plan my trip because it was like a backpacking and all that time I was like hey is it worth to go to this place in this season? It gave a lot of inside sometimes also it could just make it out but it's fun and then

**[37:44]** So you're more focused on the cloud side on running the applications do you think Java is a good way of producing applications to run in the cloud for this kind of use cases? Well, most of my client actually are building on premises facilities and I mean data center the I saw a huge a spike in data center adoption especially also in this GPU workload

**[38:13]** And I mean Java is definitely in the equation. We know that a lot of AI think more about Python nowadays

**[38:24]** But I think that there is a space for every type of languages. I saw a lot of like Rust for API routter so there is a lot of a lot of things involved and I have a feeling also that the complexity of the computer science in general is getting more difficult. I remember when I was studying the university, Java was my first goto and I started with spring and all this basic. So I mean Java is always a first love. you mentioned the hippos so the graphical processor units. are they mainly used where a model gets created or do we also need that kind of infrastructure to run applications with models? well we need for both use cases because for when the AI get created it's actually we are doing some training and this type of job usually require a lot of huge power and GPUs are they think in a different way so they make very convenient

**[39:31]** But people think that training happen only once by real also our commercial model it happen every 3 months because little by little the model starts losing accuracy. So you need to retrain and this is the business model of this type of activity. On the production side when the model is ready to go we distribute in production is called like inference and this is different kind of problem because sometimes the model is too big to fit inside one single GPU. So you need to kind of unite together and there is this technique that is called RDMA. So basically you combine together the memory of different GPUs and you link together usually high speeded networking. This is how infinity band and this type of technologies come into the equation.

**[40:21]** You need to have like a sub millisecond connectivity because you don't want to lose computer cycle. So it's interesting to see how the industry is moving across and you can also move in and when there is not enough capacity one card become two card now I think you can host till eight card in one server plus you can also duplicate the server so in a data center I'm seeing batteries of server but also prices go up to be honest

**[40:54]** And these would be fantastic machines to play some kind of game on it with such such processor power and

**[41:01]** Be now I'm experimenting with model up to 400 billion parameters and you require a lot of memories. So this is a type of project that I'm involved at the moment

**[41:14]** And it's super fun.

**[41:16]** My name is Sham. I'm a cloud solution architect working at Microsoft. What brings me here? Two things. It's a very AIdriven conference with lots of interesting topic and some of them I'm working on, some of them I'm very interested in to grow my knowledge in and I'm also speaking in one of the topics. So yeah,

**[41:34]** I saw you a few time at conferences speaking about these amazing things you can do with AI. I think Fox Brussels you were there.

**[41:43]** Yeah.

**[41:43]** You have very interesting demos but they were demos.

**[41:47]** Yeah.

**[41:47]** We are now I think one or two years further.

**[41:50]** Yeah. what is being used in production of these things? Are they stable and secure enough to be used?

**[41:58]** Yes and no. I think it's it's a very interesting question and I cannot answer it with a yes and no. There are a lot of use cases which are running quite nicely and securely in on internet and production. and some of them are also a failure. so as in any new technology there are failures and successes and also added to this space of technology is very fastm moving so today what you cannot tomorrow you can or maybe the time I'm talking right now have five new models came out which can do five interesting new stuff

**[42:35]** What I seen from my role and my work with the AI startup scene within Netherlands it's the B2B market is very much vibrant. B2C is

**[42:53]** Still in a little bit backstage because it's a consumerf facing you need to take a lot of guardrails security things into consideration when infusing AI into your application. But on a businessto business scenario, these are much more simpler where you are exposing to another business who are doing a very specific task with your application. And on top of that, if you concentrate your use cases mostly on the automation side like a CI/CD pipeline, reviewing your codes or a order comes in whether that fulfills all everything as a claim comes in that fulfills everything. those mundane boring jobs which were done manually can be replaced or make efficient by AI and those are one of the very successful use cases in the industry that I am actually seeing these days. So without telling any names of or inside information. So what you see is yeah like for instance filling in claims

**[43:58]** Insurance claims is a big area where AI is heavily used these days. receipt processing or in a retail business where there are a lot of u receipts comes in and you need to do a payments of these receipts.

**[44:16]** AI is heavily used. So if you think of old AC OCR so object sorry image processing those are done now using mostly AI because doing OCR was not only costlier but also problematic because you need to train a model to see this is a receipt which looks like this. So your name has to be on the top right corner. with models like this you can explain like that. Okay. Okay. I have five different distributors who send me receipts where name can be on the left, right and top. look for it and it becomes easier for somebody to MVP and PC a project and see how it goes

**[44:54]** And iteratively do better in

**[44:58]** The human in the loop is still there. So the human which was doing every check previously now doing a few checks on exception scenarios or something. So you have to find a specific task where that you can very strictly align and say this is something which can be handled by a model.

**[45:18]** Yeah, definitely.

**[45:19]** So using a chat model which says they know everything that's not the way forward at this moment. I think

**[45:27]** In my opinion I don't really like the chat use cases at all. Even though that's easier to show as a demo, I show that also also. but I think the real use cases are different financial processing in retail in claim processing insurance. those are the areas where we have a lot of mundane boring and costly jobs which we can make efficient. It's also time consuming from the perspective of cost. So you make it the life easier for somebody and I think

**[45:56]** There is the power more even though you can provide a lot of tools there. So basically it's not just only one task but it uses a lot of different aspects to do that task. Maybe look up the information of the distributor, look up the information of a claim or a specific country or a specific regulation.

**[46:14]** Chat is nice. Chat is good but there are a lot of points of failure. So your testing of that application will also go very very difficult.

**[46:23]** Yeah. You had a talk or you have a talk at this conference. What is it about about all these kind of tools? what I'm going to talk about is mostly concentrated on a journey of a developer. So let's say you found your use case, you found the framework, you found the language which you want to write this, you did that and now what how would you bring this to production? How would you make this scalable? more or less if tomorrow that a new model comes up how would you make sure that model I can replace with the model that I'm right now using. so it's more about the journey on around the code. so how would you how would you select a model? How do you compare those models? what are the things that you should take care when you select a model

**[47:04]** On a functional basis? So you know you do image processing, you have 10,000 choices but on top of that what is your token per minutes? What is a request cost etc.

**[47:14]** It is one thing I should remember from your presentation. What is it?

**[47:18]** I would say GitHub marketplace. I don't know whether how many people know about this. I'm every time I ask this question in a conference I get half and half response. GitHub marketplace is free. and all the a large quantity of models are available there for free to use. So you get a developer key just to write code on top of it. Of course there are limitations how many calls you make per hour or per minute but still for starting purpose to kickstart your idea to code that's the best place to start. And I'm going to show also a platform where you can in GitHub where you can compare two models next to each other.

**[47:56]** So if somebody wants to take something back, I would say take that because that's a very easy step to take to start AI journey.

**[48:03]** My name is Mary Regleski and I was also invited too about this conference. It's so amazing and I got approached and they said would you like to come to Amsterdam and speak at this AI conference for developers? I said, "Wow, I would love to come to Amsterdam." And so I said, "Yeah, let me see." Yes. And I will come. So here I am. Yes. And Yeah. So I'm from Chicago. So

**[48:28]** And for the people who are listening to this podcast, you're dressed for the occasion with the Foojay t-shirt.

**[48:33]** Exactly. Yes. Yeah. And I know Foojay is wholeheartedly supporting this. So when I was selecting what to wear, I said Foojay. Yes. I think I'm representing that too. So yeah.

**[48:44]** Perfect. Perfect. So you had a talk. Yes.

**[48:46]** What was the talk about? I guess it has something to do with AI.

**[48:49]** Yes, that's correct. So, my talk is a bit exploratory so to speak, but it's really with the multi- aentic right the agents being the center of talk of town now with AI development and so I've been actually exploring into this space and also previously I was heavily involved too with event streaming and event architecture that technique. So I really find a lot of synergies between these different techniques and technology and approaches and also now with generative AI with agents I feel there are not as much talk you know kind of explicitly talking about the complex cases of agents execution and what kind of techniques can we use so immediately I've just been thinking you know event-driven way would be definitely the way and I'm seeing more articles or talks about it but Oh, not in general is a common thing. But anyway, that's that's what my topic is about. So,

**[49:47]** So people who know AI from chat GPT

**[49:50]** Or maybe know it from within intellig feedback.

**[49:54]** That's right.

**[49:55]** What is the difference with an agent? What does an agent do differently than chat GPT for instance?

**[50:00]** First of all, yeah, chat GPD right by itself, let's say, we are kind of interacting it with like a LLM, the model is the oneshot deal kind of thing. you we formulate a perfect prompt what you need and it sends it over to the LLM and it answers. Now, of course, too, you can also do use an agent agent so to speak, right? It it's not confined to an AI terminology. It's a general computing type of terminology that you can apply to agents. And you know, even back in, you know, the early days of Unix and a lot of Unix machines, they are really when you think about it, it's really agents. It's a kind of a functional kind of way of doing things. There are specific task they each carry out and they are adaptive. That's one thing is a key kind of property of agents. So it's finding its way into AI and of course the marketing people are all kind of trying to oh wow agents is the way and talk but to me there's still a lot of confusion still people just oh agents they're thinking of a travel agent that book my thing do things oneot deal true yeah there are lots of things we don't need complicated thing can be chat GPT I

**[51:08]** Have a dialogue I want to interact with an online agent a robot then I can use an agent to do it

**[51:14]** Now the thing the difference to me I think is we very often too maybe

**[51:20]** Somehow right the marketing the business likes to talk about something more direct and simpler for in general everybody but in reality we live in a complex world we're going to be not just doing things in one step we're booking like a flight reservation involves so many things so many steps I need to search for schedule all of these things and then eventually I buy it then let me process the payment is another step all these So that's where I'm kind of exploring in these complex cases in which we want the data to be absolutely preserved their consistency or how they transform as they travel through different stages of the processing. So to me I think there's a lot of potential in this space and we need to start to talk about it and how to approach it. There are also the security side and that's explains why a lot of financial companies are not willing to do it because a lot of things are not consistent. The LLM by nature is not deterministic. But anyway, that's what the long story short, I feel

**[52:21]** That's where it is. You can talk about agents in the simple case, single single shot deal what you need, but we let's look at the complicated cases and that's where we can use complex techniques.

**[52:32]** So you're looking at the way that you have several agents which can do something simple and how they talk to each other. I finished my task. Now it's up to you something like that. That's a bit the kind of flow you were looking for.

**[52:44]** That's correct. Yes, that's also it's very much yeah the kind of approach we can also take. It is very true. Each complex task can be broken up into smaller pieces. Each be responsible by a certain type of agent and with them we can many agents then we will need like a orchestrator agent for example to make sure every everything is working in sync in tandem with one another. Yeah. In correct. Yeah. That would be one way. you've been in Java space for a long time. Yeah.

**[53:12]** How do you look at all these evolutions last years with all this influence by LangChain4j, Spring AI, how AI

**[53:23]** Can now be developed with Java.

**[53:25]** Oh yeah, that's a very good question. I think yeah definitely I feel you know Java being such a versatile programming language and now with Java 25 just came out a few days ago and a lot more kind of simplify of how we use it and to really dig into its essence right of the language that's above you know every other languages like say concurrency the scalability that time of aspect I think you know right now it's really cool to see spring AI making so much you know kind of big steps forward right since it started a little late. Yeah, that's true. Then compared with Python,

**[54:03]** But Spring AI and link chain 4J and there's really great effort in there from the group. All these I think are really good support and indicators that yeah Java is going to like you know make a comeback so to speak into this AI space and maybe not even a comeback but we really want to kind of get in and get the piece of the pie that we deserve and also having folks that are looking deeper into it and I actually got to talk with Frank Greco who's a leading expert in more AI matters from the Java community and Zoran from deep nets and yeah they are doing a lot great work and I do see too yeah those are they come from more of the traditional AI machine learning that's still a lot of bearing into the newer generative AI so I see a lot of potential right in this area and then with us being such a rich community of very smart extremely smart folks I see it that's yeah like a very good potential we can yeah find our space in the AI world yeah

**[55:04]** My name is Anton I work for Jet Brains and I'm involved into products like juni AI assistant intellig idea and I'm kind of related to cotlin language so if you have seen any announcements of the new versions on YouTube it's usually

**[55:26]** You

**[55:27]** My face yes

**[55:28]** Okay what are you doing with AI within jet brains to help developers

**[55:33]** Because we build tools for developers and the whole premise of Jet Brains tools is to make more efficient tools to make developers more efficient and these days is everything is all about AI and we are looking for ways to make our tools also efficient and pleasure to work with. it's a hard problem to solve in fact like how AI should be integrated in such a way that it actually makes developers more productive predictably

**[56:11]** So that there is trust so that the developers not just feel productive but are actually productive

**[56:20]** It's a long journey it's going to be a long journey

**[56:23]** Is it also a bit I think for you difficult to put a price on this because the more you call these APIs to get an answer from chatbased systems. Yeah. You have someone has to pay for it.

**[56:37]** Exactly.

**[56:37]** Or it will be an extra license in intellig who will pay for this or is it is there a way to earn money on this for you as as a developer of tools?

**[56:50]** The currently the situation is like this. We have our ID suit, right? It has a price. It has a license and the AI plugins have their own license. So if you're all products pack owner when you own all the licenses for the IDs the basic license for AI tools also comes in the box.

**[57:14]** Mhm. but if you're let's say an advanced or more active AI user you start using AI agents and they are pretty hungry on consuming tokens. So we say that the ultimate package the ultimate license AI ultimate that's the name naming is hard is what suits for the best use of jun

**[57:43]** Because juny is like it's it it's doing 20 iterations while you were you would do one iteration with a normal chat and because it's automated you can blink and You don't see it. Yeah.

**[58:00]** Mhm. How much do you trust the code that you get from such a tool?

**[58:06]** Is it just copy paste it in your production file and deploy?

**[58:10]** No, I wouldn't of course. luckily I don't have to ship the production code myself. my job is a little bit different because being a developer advocate it's more about demos but I still think about how to make u work with these tools more predictable. Mhm.

**[58:28]** And just by following the intuition, trying the different tools, not only pet brains but everything that we have in the market, there's many of them and new ones appear like every week. I developed this intuition that you should be not just telling the agent to do something but you should first ask the agent how it will do something. Maybe it's the ideation like you don't know how to I don't know create a synthesizer maybe or MIDI keyboard or whatever else you don't know how to do something and then you can ideate or like brainstorm with the model. It doesn't have to be inside the ID itself. It can be chpt. It can be set. It can be any other politically incorrect model that you can get a hold of. but then you get the ideas list and then you can actually refine this ideas list into a specification.

**[59:34]** I have developed some prompts for myself to put these ideas into let's say formalized way of writing the specification of like when something something something then something something something is just refinement of those things because it actually puts

**[59:55]** This LLM into a more predictable way of handling this kind of instructions and then I can ask the LM them to provide me a development plan or the design document for how to implement those requirements and this through this refinement I'm keeping my hand on or my eyes on depends how you tell it on the process like how you refine the spec from the just requirement into the design that you can validate say that it's good to be implemented and then you can actually ask the assistant or whatever tool you use to refine the design into the task list. Why is it important? Because actually you can follow the progress by seeing that the tasks are unchecked. If you don't have that, if you let the agent just to work iteratively on this design document or development plan, there is no sequence. You don't know where it stops and where it ends. So you have no idea what like about the progress.

**[1:01:15]** Did it implement something? It can hallucinate and tell you that it actually did. But

**[1:01:20]** Yeah.

**[1:01:21]** Yeah. But you have this clear flow of how you use the tool. And that's actually I think an important message is don't just ask to get some code. It's really find your best way to use these tools to help you doing your job.

**[1:01:39]** Right. We I think we all right the whole development community needs to develop an intuition for actually using those tools and it's the best time to do that currently because a lot of companies are still subsidizing the use of those tools right so current plans are at the price of $20 $40 that's not the real price we all know that we know we all know that the real price will be in hundreds of dollars per month per user. And this is the great time to actually explore, develop the intuition and develop the understanding of how to use those tools fluently, which models work better for you. In which case, what approach would you take in depending on the task that you need to do and the scope of that task as well. because depending on which model you select this task list approach might not work that well or might work differently.

**[1:02:45]** So developing the intuition is very important right now doesn't matter if you are a senior developer and a junior or a junior developer that's that's my main message. Yeah. Okay. A different story. Your Cotlin defil a bit. Yeah.

**[1:03:02]** Yeah.

**[1:03:02]** How do you look compared Java Cotlin? They were a bit far away. Now they're closer together. Correct. Or

**[1:03:12]** Depends how you look at it. Like some people like to compare the features. And if we start unchecking the check boxes, there will be a set of check boxes that Cotlin has, Java doesn't have. And then now there is a set of checkboxes that Java has and Cotlin doesn't have. And it's all about trade-offs, right? The language designers always say that it's about the trade-offs and philosophy. What do we want to put into the language and I don't know like cotton is a pleasure to work with. It feels very modern but some things I'm I'm missing some things in cotlin that we have in Java and I it's a very unpopular opinion. I like checked exceptions. I do like checked exceptions just for the sake of the indication that something can throw or something can fail.

**[1:04:11]** In Cotlin the philosophy is a little different like we use exceptions only for unreoverable situations. So this is like an un an unfortunate state of things. I like checked exceptions in Java. I don't like the implementation of checked exceptions in Java. some some things are missing and this causes this bad nested code. if you have if you have been writing the pure GDBC

**[1:04:39]** In the past like open the connection prepared statements result set iterate then nested try catches to close it all and so on

**[1:04:52]** And this shouldn't have been like this right we yes we've got the tribe with resources that improve the situation a lot

**[1:05:03]** It's still is just one case. is just when we work with the resources there's more cases

**[1:05:11]** And it could have been more fluent let's say or more handy to use at the same time again in cotlin I don't have this problem I sometimes prototype in cotlin notebooks that's a kind of jupyter notebooks but in cotlin

**[1:05:28]** Inside intellig idea and I do write JDBC statements there using plain JDBC API and I have none of the ceremony around checked exceptions caused by checked exceptions but at the same time it's the with the tradeoff that I don't know if it's going to throw or not and my view on the language is that it should be as predictive as possible as helping as possible and decabilation time. So we do have a lot of interesting features in cotlin that help that but checked exceptions are not

**[1:06:06]** Not one of them

**[1:06:06]** Not one of them.

**[1:06:07]** That's it for part one. In the next episode I'll bring you more interviews covering data science AI development tools, Java in the cloud, a behindthe-scenes look at how the conference came together and more. Thanks for listening. Visit Foojay.io for more articles and podcasts about Java, the GVM, and growing your developer career. Give me a foo. Give me a J. Give me the friends of OpenJDK.
