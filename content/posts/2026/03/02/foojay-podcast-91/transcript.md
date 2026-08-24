**[0:00]** The IDE that grew up with Java celebrates a quarter century.

**[0:04]** Welcome to the Foojay podcast. OpenJDK.

**[0:10]** Welcome to another episode of the Foojay podcast. Today we're celebrating a major milestone in Java development history. 25 years of Intellig IDE. Think about it. Intellig IDE launched in 2000s and since then it's become the go-to IDE for millions of Java developers worldwide. From its revolutionary code completion and refactoring tools to its influence on modern development workflows, intellig has shaped how we write Java code. For this podcast, I'm joined by several people from the Jet Brains team.

**[0:43]** Hi, I'm Mar. I'm a Java developer advocate at Jet Brains and I've been using intelligent idea probably the shortest of my colleagues since 2017. I was a little late to the party. Anton,

**[0:56]** Hello there. I started using Intelligj since 2004 and I joined Jet Brains 2018. Yeah. And in the role of developer advocate. I'm colleague of the same team.

**[1:13]** And I'm Dimmitri. I started using Intellig Idea back in 2002 and I joined Jet Brain 2003. And in 2005, I started working on Intellig and I've had many different roles in the process and right now I'm kind of on a weird part-time position. but yeah, I did a lot on Integ over the years.

**[1:37]** Okay. I don't have someone here that was there at the early start at really the beginning of the Jet Brains company, I guess, but I guess you can tell me more about the history. How did this start? Red started so the original founders of the company Sergey Mitrif Eugene BV and Valentin Kipkov they were working in St. Berke in the office of the company called Togetheroft who was creating a Java model UML tool that also had some integration with Java code and a bunch of other stuff. never used it myself. And they basically decided that this tool was too too heavy, too bloated and too far removed from actual code. And they tried to take a different approach and try to go out on their own and to build a tool that very quickly became IntelligJ that we all know and love.

**[2:30]** Mhm. Mhm. what I find fascinating is in this world driven by open source Java open source and all these open source tools like you have Net Beans, you have Eclipse, then you have Visual Studio Code. Not really open source I think but never mind these are free and still with Jet Brains you were able to build such a strong company with a product that you have to pay for. How did you achieve this goal? Do you have any insights on that? I think in the early days the most of our competition was not actually free. So for example we were competing against JB builder which is which was also a paid tool and also more expensive than intellig and our product was actually was never actually that expensive. So

**[3:13]** Compared to how much money companies nowaday pay to other software vendors like cloud vendors and stuff like that in Dell is just a very small percentage of the cost and also a very small percentage compared to the developer salary and especially in the world of AI the amount of money people spend on tokens is also much more than the price of a license for intellig and also by being a paid tool we are just able to invest in things that free open source tools cannot afford like a team of designers, a team of support engineers, a team of documentation writers, like a lot of the functions that are basically outsourced to the community and logically with open source tools, we had them inhouse and I think this was a big part of our role and simply

**[4:01]** Building a better tool and investing more in actual development, hiring being able to hire more people and I can remember that I had to struggle for my Jet Brains license in the early days of when I was using Java because use the free Eclipse was the mantra at that time. what are the arguments now you give? I think I have to look at the devils here in the room. What are the arguments that you now use to convince managers that a tool like intellig ID is better or is worth the money? for from personal experience I used Jbuilder in university but it wasn't nearly as good as the IDE and editors are now from what I remember you know it would tell you there's a semicolon or bracket missing on line 61 and that meant like plus or five plus or minus five lines from there might be something missing so that's what I remember and then I didn't do any programming for a while and got back into it and I used Eclipse Eclipse and occasionally Net Beans at the time and both were fine like they didn't get in my way. let me do my coding and then I changed job to jobs to a company where they had intellig idea licenses and started using Intelligj Idea and personally I was like really impressed with not that it didn't get in my way but it was actively helping me with suggestions quick fixes features that you know help me write better code and that's essentially my sales pitch is that it has so many useful features that's actively save me time and by integrating everything into the IDE means that I can do most everything from inside my IDE and I don't have to go to the internet to look up stuff and I tend to get distracted by the rest of the internet if I do so you know it helps keep me in my flow and helps keep me focused.

**[6:01]** Looking back we I think we didn't have to convince the managers that much. Usually it happens from the bottom like developers someone from developers tries intelligj and sees the difference and one the team start using it and then they escalated to manager to get the licenses and this is what was happening to me before to jet brains in different companies. I either was the only intelligj user and by the time when I was leaving the company everybody was using intelligj. So that happened as well.

**[6:37]** We have intellig IDE focusing on Java. I do a lot more with it. I also use my for documentation as doc and all that kind of stuff. But you also have a range of different IDEs provided by Jet Brains. Are they all based on what you have built with intellig and made yeah other versions of it or are they all different products which grew next to each other? This is all the same code base. So like we have like one build one big monora where which we build in many different configurations including intelligj and the smaller ids like pycharm and webtorm and so on. And for example webstorm is a strict subset of intelligj functionality. So everything you have in intellig everything is you have in webtorm you also have in intellig.

**[7:25]** Okay. So that means that all the other languages are really programmed with the java tool. Correct.

**[7:32]** Okay. There are some bits and pieces like for example some PyCharm has some runtime components for example the debugger components some component the debugger that are written in Python but the majority of the support for all the languages is written in cotlin primarily nowadays

**[7:46]** Yeah so cotlin that's one of those side projects of jet brains how did this get started

**[7:53]** Yeah we saw the opportunity basically so we back in the day we were using Java to write and we had a lot experience with other languages besides Java like C# for example was already getting pretty good at that time and we had Scala but we never really considered switching to Scala as a language development LG for different reasons like complexity being probably the key one and we saw an opportunity that there was an unmet need of a more modern language that could exist like if we made it interoperable with Java in such a way that the company could adopt it incrementally. I think we saw that it might get some adoption

**[8:35]** And of course the adoption that we got with Android it was not part of the original plan. It just was a lucky coincidence. but even outside of Android Cotlin is cotlin has its audience and it brings it.

**[8:50]** So you have all these tools just looking at Java which with a new version every six months I can imagine other languages doing the same. How hard is it to maintain all this these tools?

**[9:04]** We have different teams working on different things. So for intellig idea we have intellig idea ultimate team but we have a build to Java build tools team cotlin build tools team Java support team that are all staying on spring framework they're that are all staying on top of what is going on in the ecosystem. For Spring for example also we collaborate with them. For Gradel we collaborate with them. for Java obviously we follow along with the Japs and come up with useful features on how to support it and make sure that we

**[9:39]** Support running and building your projects with the latest Java versions. You can download JDKs from inside the IDE even early access versions. right now we're following closely along with the may or may not be impending release of Maven 4 and making sure that you can build your projects with Maven 4 and also have some useful features for migrating to Maven 4 if and when that drops. so yeah staying on top of what's happening in the ecosystem and partnering also with other companies and making sure that we evalu or sorry evolve with the ecosystem.

**[10:21]** If people are interested in Maven 4 and why is it released or not released yet, I had a podcast a few episodes ago about the upcoming release but

**[10:31]** Any day now.

**[10:33]** Any day now. how did you personally end up working at Jet Brains? What's your history there?

**[10:41]** My story was that in my previous company used to work on development tools as well. as a it was a startup and from the outside well with startup you have to do like wear multiple hats and from the outside it seemed that I'm a developer advocate but it and I was blogging about using Intelligj so it seemed like I'm a I'm a good writer and I'm I like Intelligj and therefore the team was inviting me like several times to join Jet Brains in this role. but you know when startup makes an exit sometimes you just so I was free after the acquisition and it was kind of natural move for me to join.

**[11:33]** Mhm.

**[11:34]** Yeah. And for you Marit

**[11:37]** I joined just over three and a half years ago. I also like Anton started blogging in my previous job where I also started using Intelligj Idea and been singing IntelligJ ideas praises ever since and now they pay me to do that. Yay. So and I started so in my previous job I started blogging I started going to conferences I started speaking at conferences and that's how I met Hari Hariri who was previously the head of developer advocacy at Jet Brains. and also I met Trisha G who was a developer advocate at Jet Brains previously. she also had contacted me two years before I actually joined to ask whether I was interested. and at the time I was part of an amazing team and I'm like I'm having too much fun with these people. It's it's the collaboration is too good. I don't want to leave right now. But then gradually people left from that team. I joined another team and at some point the contacted Hadti about a different job actually that I saw on the Jet Brains website and he was like but wouldn't you want to be a Java developer advocate I'm like actually I would so conversations with him and Trisha and Helen Scott and they decided to hire me

**[12:54]** About Trisha it's it's remarkable she was devil but even after she left the company she wrote the book about it So, it's not because she left that she lost the love for the tool, I guess.

**[13:07]** No,

**[13:09]** I mean, if if I wasn't at Jet Brains, I'd still be singing in Sanj's praises. I was before and I will after. So,

**[13:18]** And for you, Aditri was the

**[13:20]** Yes. My story was that like early on like it was around the year 2000 I think I got really interested in extreme programming which was like a hot new thing back then like they really like Kent Beck and Ron Jeff created that and I basically randomly picked up a book in the bookstore to find out about it and then that's how I found out and then I started looking for other people who were also do like interested in that and I found a people in Russia and I got to know them and some of them ended up working at Jet Brains and then we then one of them invited me at one point and so that's I that's how I ended up on the on an interview. Now if you look into the history so 25 years of Intellig IDE by the way do we always have to say the full name Intellig IDE that's

**[14:11]** Intelligj I Intelligj IDEA is the IDE for Java primarily and Intelligj is the name of the platform that allies are built on top of

**[14:24]** Okay

**[14:25]** Including also Android Studio is built on top of that by Google

**[14:30]** Yeah the name probably goes even deeper than that because the initial name was Intellig Labs if I remember correctly but Mitri can correct me on this.

**[14:40]** Yeah, the name of the St. Petersburg company was Intellig Labs for a very long time and the very very early the very very original name under which the company released the software was intellig software and like intellig ID was like the name of the first product back then.

**[14:56]** Okay. Yeah. If you exist for 25 years, some names change in the future.

**[15:02]** Sorry, naming things is hard. It's one of those.

**[15:05]** Yeah. Is that also the hardest programming problem? Yes.

**[15:09]** Yeah. We know how it started, how it grew, but there are a lot of changes still ongoing. So, this tool keeps getting updated. What are the most important ongoing changes now that we see in the programming tools? Apparently probably the AI is a big thing in the room that affects everything. But also if you take a look back a few years maybe four or five years when this thing started that was that put us all offline in home offices. remote development. and like from my point of view, this is one of the events that kind of triggered more refactoring of the platform to separate it from its logic so that we could have like a nice thin interface into something in into this that the ID provides and we we're still I think we are still working on that in on many fronts.

**[16:21]** Yep. Is that the code with me tool that you're talking about?

**[16:26]** One of that's one of the solutions like when dependent the first solution was the projector that was a quick solution to project the swing interface into the browser. Then code with me was one of the response to the demand to let people work together like bare pro programming. at the same time other teams were working on something that we called remote de where you could develop on a remote more powerful machine over the SSH and I think both teams doing like different efforts and like the latest I have seen was just a convenient interface like Jet Brains client we call it which looks almost exactly the same has the normal ID but like a widget with the connection and the telemetry from the remote machine but the experience you get basically as if you are developing locally but everything happens on the remote machine.

**[17:35]** Mhm. I have to say the code with me I didn't use it a lot but it's so great. I have been using it a few times even for a live stream to be with someone remote working on code on my machine and then seeing what's happening. It's almost magical at some point. so yeah that's that's a great there were some recent changes regarding licensing. So you had two versions of Intellig. You had a community version and a pro version. There's only one left correct.

**[18:08]** Yes. So we as of the last release 2025.3 we have a unified distribution which means that you no longer have to install community edition and ultimate edition separately but you can use one installation either with or without the ultimate subscription and the ultimate subscription then unlocks the features that you need for professional software development. So better spring framework support web technology support but also that means that if your license lapses or you can't connect to the license server occasionally then instead of having to install community edition to continue working your IDE will continue to work except for some of the ultimate features won't be available. Hope that actually makes it easier for people to continue using Intelligj Idea.

**[18:58]** Mhm. Yeah. So this is not as much a licensing change as a distribution change. So like the licensing model still stays the same. And if you want the original OG open source version with no commercial plugins, you can still get that from GitHub. We are no longer maintaining this like as an official download on the website, but it still exists and

**[19:17]** A pure oss version is still available if you want one.

**[19:21]** Yeah. Isn't this a logical move? Why were there two different versions? There has been a bunch of evolutions. So originally intellig So originally this was we had the purely open source intelligent community edition and then we had the ultimate version which was a commercial product. So like I mean even like so that happened in 20 in 2009 and before that we had just the commercial version and of course the release of the open source version has opened so many doors for us. Android Studio is probably the biggest thing that was enabled by that. But also for example people using Linux packaging intelligions they were able to do that like in a proper way so to say and other product it was it became possible to build other products based on the intellig platform and there are a few IDs based on top of intellig. Yeah. Yeah. So we wanted just to have this pure open source version but then as the product evolved we started realizing that more and more things we wanted to make them available to the users of the open source version but they were we but we could not open source them themselves.

**[20:29]** For example licensing support for paid plugins. So we want people to be able to install the a paid plug-in from the marketplace or code with me that we have already mentioned. code with me itself is not open source but we made it we wanted to include it with the community edition so community edition stopped being this pures product it's became like a thing that it became a thing that has some open source has an open source core and a bunch of non open source components

**[20:56]** And so there was no the purity or like

**[21:01]** Yeah like basically license purity was no longer an argument so it just became logical to have this unified distribution and probably harder to maintain all these different parts.

**[21:11]** It's not the maintenance not as much because we as I mentioned we build many different products from the monor repo and so basically building building 12 like building I don't know 15 different products or 17 different products is not that much of a difference.

**[21:28]** Yeah. By the way 15 different products how big is the Jet Brains team?

**[21:32]** So the company is almost 3,000 people by now I think. Wow.

**[21:37]** Of course, that includes everyone, not developers, but also, you know, any and all of the marketing support,

**[21:46]** Our amazing travel team who get me to where I need to go,

**[21:51]** Motion, lots of people who do other stuff, but also many many talented and amazingly smart developers. Yeah, we are people short from 3,000.

**[22:07]** Okay, good. Good. But you have more than ide and

**[22:12]** Yep. Yep.

**[22:13]** Yeah. Yeah. Yeah.

**[22:13]** We also have a huge department internal systems. So it's not that everybody is working on IDs or in the products. It's

**[22:21]** There are a lot of supporting teams. I always find it amazing how some something can be built as a company on top of opensource products like you also have people who start with a library and suddenly they grow a company out of it. I think in Belgium JobRunner is one of those examples to schedule jobs. I find it amazing timefold planning stuff. So how you can grow from an open-source ID to something like this and I think that Jet Brains is a great example how you can yeah grow outside of open source.

**[22:58]** Yeah, we started as a paid product. So we were already quite large and

**[23:02]** By the time we open source the core of our products it's not really a pure open source grow growth story more like an open core model.

**[23:09]** Yeah. Yeah. Vice versa. We do also evolve alongside a lot of open source like

**[23:17]** Maven gradal

**[23:19]** Etc.

**[23:19]** Yeah. Yeah. By the way, how do you make money out of cotlin for instance?

**[23:24]** We don't make a lot of money on cotlin. It's very hard to calculate how much money we're making on cotlin. So basically other than license sale ID license sales there is no other direct revenue source. Mhm. But you maybe profit from programming faster programming because you made Cotlin for your own tools something like that. It's very hard to quantify. It's not just faster programming just for example we are right now rebuilding a we have been for the actual for the last threeish years we have been rebuilding a lot of the foundations of the intelligent platform around cotlin core routines in order to fix the UI freezes in order to ensure that the UI is always responsive no matter what the user is doing and this type of work simply would not have been possible without cotlin

**[24:18]** And also we are gradually starting the process of migrating our UI from swing to compost desktop and compos desktop is also a technology that only works with cotlin because it relies on the cotlin compiler plugin as one of the core components

**[24:35]** And we simply would not have been able to do any of these things if we use java.

**[24:40]** It was mentioned that something is happening with AI in this world nowadays. how much of does this influence what you are planning on new tools in intelligy? All the LLM stuff and chat interfaces and coding tools that can now be driven by AI.

**[24:59]** There's so many things happening. So if you ask one product manager, they are thinking about one thing. in a week I get to know that the they're like change the direction because of I don't know new inputs new idea so everything changes very fast and I see that a lot of functionality that we implemented last year that people demanded today probably goes unused just because things now something else is popular

**[25:35]** Things change so fast it's really hard to stay on top of. I know that we try to integrate AI also into the IDE and actually those are the things that I like the most like the little ones like writing documentation or explaining code or you know writing a commit message so that I don't write whip for everything

**[25:55]** Or even when you rename a variable it just doesn't you offer you the tech the name based on technical details that we can collect from the code but actually scans the semantics of the code and gives you like the main driven name like

**[26:14]** Yes

**[26:15]** That's that that's a very neat feature but I think the latest thing that we or basically I think we I should say because we decided to be the platform not provider of the tool we position ourselves as a platform for integrating other AI tools as well in the same the entry point and together with that Z technologies we or was it Z editor? I might be wrong naming here. Z editor is the product but the company name might might be different. so they started this protocol project to integrate agents into the editor and we joined that effort and now in AI assistant chat that is inside the ID you can actually using that protocol you can any CLI based agent and let the CLI based tool actually work inside this which is

**[27:20]** Well It's it's an improvement to some people like some people prefer to stay in the ID opposed to switching to the terminal but I guess it's the subjective preference I switching between the terminal and but you know we shouldn't we shouldn't limit people how they use the tools. So, we already have several agents that are integrated in the AI I forget what the plug-in is called currently. AI it used to be AI assistant. It's AI chat now.

**[27:56]** No, the plug-in is still AI assistant. Yeah, I think

**[27:58]** Okay. So, in the AI Thank you. in the AI assistant, we have you there's a chat mode. So, if you want to ask questions so that you don't have to go outside your IDE because I also like to stay there. but then we have the juni agent in there. We have cl cloud and we have codeex at the moment and they also have different models that you can use with those agents and of course you know we continue to add more

**[28:26]** There as new stuff is released but yeah like anon says like it changes literally every day all day every day. But am I correct that you tried to have your own LLM model behind it and you now decided to integrate things like cloth?

**[28:43]** Not quite. So we have a we have a we have an open we have a model called Melum. It's an open source model

**[28:52]** And we use it for it's like it's it's not a large language model. It's like a medium-sized language model so to say and we use it for code completion for the type of for multi-line code completion for the type of tasks that cop copilot was originally built for and for like agentic development we used a large language models we never had a goal of having our own large language model for this type of tasks

**[29:15]** Okay so you have a code language model correct

**[29:19]** Yeah yeah

**[29:21]** That's used for the completion inside the IDE like like Ditri said Yeah. Yeah.

**[29:26]** And that model just got its own landing page at gym/melonum.

**[29:32]** I'm going to add links to the show notes of this podcast. So, please send them to me and I will add them.

**[29:36]** Yeah, I just did some some open-source development this week for a library for the Pi 4J library again on the Raspberry Pi trying to fix something which we were stuck and I was really impressed with how much assistance I can now get from this chatbased thing. really explain to me why this happens here and how deep it goes into the codes. The ever returning question will developers get replaced or will we work better because of these tools?

**[30:09]** I very much hope the latter that's you know they will allow us to continue to build things to solve problems but with better tools. Mhm.

**[30:19]** And I do very firmly believe that we should have humans in the loop because you know someone has to be responsible but also someone has to think about how people actually use the software and you know what to take into account for actual users. My perspective is that like with intellig I've actually I've held hel held the head of product role for some time and at that time it was like writing code was actually never the hard part of for most of the features writing the code was never the hard part of doing things. So the hard part was actually figure out what to build, what are the actual valuable features that are still missing, how do we integrate them into the product that they are discoverable, that they help at the right time but don't get in the way and so on and how do they fit conceptually so that people don't get confused when they try to use multiple features designed by different teams at the same time and so that they those features are consistent in how they are built in terms of mental model and the user experience and I think that we still need humans at least for that.

**[31:33]** So even though like even if producing code becomes like cheap and can be delegated to a machine, you still need to figure out what code to produce and how to produce the right code and the right amount of code because like an LLM can very easily write a huge pile of code that is like several times larger than if than what you would write if you wrote about if you created this. M the hard part is also understanding what let's say you're working on a brownfield project and if you have like state-of-the-art agents they can do only as much as they can fit into the context window. yes the technology evolves and we get larger context windows but the effect like the added that is yet to be confirmed.

**[32:26]** And if you have to make sense what you do, how does it work? if you can do it deterministically without actually big little question that you have to the project then you know it's a good thing then you have a tool much cheaper much deterministic and gives you like precise results. Am I correct that models are trained on existing code? So they will never be able to write really new breaking developments.

**[33:06]** We don't know that. I mean with the best results we are getting like the models do their best when the it's extremely clear what they need to do. So basically if you have a code base in one language and you need to port it to another language and you basically can't just do it in a fully automated way by now. So if you have tests and if you have implement implementation the model will convert the tests run the test until all they all pass and convert the implementation so that we you get essentially a version for a different platform. But to me this was never the interesting part. The interesting part is building new software and also very interesting thing is how the abstractions are going to evolve because

**[33:41]** For example someone invented react at some point and now LLMs are really good at writing code targeting react. and is react the ever best way to write front-end UIs that ever could could ever be invented? Clearly no. but any other frameworks like if someone wants to create a new development framework there's such a headwind by by now because agents will not know how to use a code for the framework and even though even if the framework provides better abstractions and better encapsulation it will be harder to adopt a new projects and agents themselves don't seem to be possible to be able to create good new abstractions nowadays

**[34:26]** With the current state to my understanding also when I'm talking to people you know you can build a basic spring spring boot crud application using agents because there are plenty of examples of doing that but I was for example talking to moral just this week in fox day and he was saying like updating a spring boot application then to the latest 4x version is harder because there's not so much training material for that. So there is that on the user side and then on the writing the library side I've also spoken to other people on the spring team who said you know if I apply the agents to the work that I'm doing they can't do it because the training material is just not there

**[35:13]** But will that change with new technologies I don't know

**[35:17]** Yeah what I've also seen is I use a lot of Java vix and vaden these are stable available since a long time think the model can really create something a screen if you can clearly describe what you need the knowledge is there inside the model but indeed when you start using new stuff like I was experimenting with the FFM API which is only there since Java 22 then it's harder to find all the right tools and whistles and bells indeed so yeah the programming world is evolving how is intellig IDE evolving what Can we expect what can you already announce?

**[36:02]** So well we continue to evolve with the ecosystem as previously mentioned. So we make sure that we support all of the latest technologies. So the latest Java versions, the latest Spring Spring Boot, Spring Framework, other frameworks, build tools like Maven and Gradal, etc. but also we continue to evolve our UI and to make it so that you know all of the gazillion features that we have plus or minus a few are actually discoverable. so one of the things that also I wrote a blog post about on Foojay recently is called command completion or rather universal entry points. So probably all of the developers who use intellig idea use command completion. So if you have for example a variable and you type a dot you get API completion of all of the methods that you can call on that variable you get postfix completion which are a kind of templates that you can apply and now that also includes commands. So any command that is relevant to your current context you can execute from there without actually having to know the shortcuts. So we hope that a that makes it makes development easier makes it so that you can stay in the flow. better because it provides you all of the options that you want for your context, but also that it might help you discover features that you didn't know were there. Or there's a I tried to learn all of the shortcuts, but there are a few that I always forget and now I don't have to learn them anymore.

**[37:33]** I can just dot or dot dot if I want to filter to the commands and I can use that on a blank line as well. One of the shortcuts I always forget is optimize import. So now I can just dot dot on a blank line and it will offer to optimize imports or reformat my code or whatever.

**[37:49]** I think that that's really useful and we develop other useful things like the spring debugger to help you provide information about what your spring application is doing at runtime. so I hope that we continue to bring features to make developers lives easier. Mhm. The very first thing I always change in a new project in intellig ID is go to actions on safe and then organize imports. So I never have to do that again. I don't have to remember the shortcut.

**[38:21]** Yeah, you can do optimize imports on the fly, but I don't always like sometimes it uses star imports where you have to then configure how many makes a star import.

**[38:30]** So I do like to do that explicitly or on commit. You can set it to do on commit as well.

**[38:38]** But so we do support many many different workflows as well.

**[38:42]** Yeah.

**[38:42]** Do these sheets still exist with all the shortcuts that you can print?

**[38:46]** Yep. You can still print the PDF with all the shortcuts if you still want to learn them. And in some cases it is faster if I can just stay on the keyboard. so I use I don't know a random mix of whatever I learned. Aren't there too too many tools and too many shortcuts in the IDE?

**[39:07]** It would be interesting to know how many shortcuts do we actually and it's it's actually hard to answer because the same shortcut can often be used in different contexts.

**[39:18]** Mhm.

**[39:19]** That happens as well. And we also have shortcuts that are kind of chords of like you one combination of the keys and then press some other key and you get this chord like keystroke. So I my guess it's it should be around 500 shortcuts all together but maybe it's even more.

**[39:46]** Yeah.

**[39:47]** Yeah. And some of them are specific. For example, there are they work in a single tool window or in a single egg panel in the UI and then it's even more

**[39:56]** Shortcut for generate for example which on Mac is command N and if you use that in the project tool window you can create a new Java class or file. If you use that in your editor you can generate constructors getters setters to string equals hash code. If you use that in a test you can generate a test method. so like Anton said the same shortcut can do different things different contexts or if you use the same shortcut like recent files which is commande on Mac. if you use that twice then it shows you recently edited files and the same for recent files and locations which is command e shift on Mac that gives you recently recent locations and if you hit that twice and it gives you recently edited locations.

**[40:45]** So yeah there are so many ways to use them.

**[40:49]** I used to heavily learn them and teach people how to memorize those short in the recent years. I actually started teaching them how not to memorize the shortcuts and command is probably one of those additions that we have in the platform that helps not to learn the shortcuts.

**[41:13]** Yeah. I also try to show in my demos like you know obviously I like to use them but I also want to try to teach them how to find things. So that's I always say like the point I'm going to show you because I use the presentation assistant which is an included feature to show which features I'm using and what the shortcuts are for the standard Mac and the standard Windows and Linux key map. But I always say like the point is not to teach you the shortcuts but just to so that you can see what I'm doing so you know the feature to look for. but like if any shortcut I would say shift shift which is s search everywhere. you can find all the features and you can use tab to go to the different tabs if you want to filter it down to classes, files, actions, symbols. and from there you should be able to find most things and otherwise in the settings you can look for stuff. And I really rather like Anon said help teach people to find what they need rather than to memorize all of the shortcuts.

**[42:15]** There are always geeks like us who do want to learn. So if that's you, there's a there's a plugin called Key Promoter X that can tell you every time that you do something that has a shortcut, it will tell you in the bottom what the shortcut is. I used that for a while, but after it told me like you've missed this shortcut 800 times, I felt so bad for the plugin. I turned it off.

**[42:40]** Still go and press the button because that's just what I'm used to.

**[42:44]** By the way, Deita, you said you were on the product management side for a while. How do Yeah. How do you decide to remove features? I think yeah some people may be used to it but then yeah it evolves and there are better ways to do things. We always try to look at the statistics for how much the feature is used and also on the feedback that we get and also we consider how good the replacements are. So some of the things we remove for example because no one uses underlying technology for example I don't know tapestry we had a plug-in for tapestry we had a plugin for I don't know we had a plug many plugins for stuff that people don't use strat version one for example I think it's also removed from the product with like more core features this I think basically this depends on the usage count so we if we see like in the statistics that like out of the all out of the entire user base of intellig we can see only like a hundred people or a couple hundred people using the feature for us it's an argument to consider removing it and also like and then how much is the feature in the way so is it just sit does it just sit in a menu somewhere or is it like does it affect developers lives even if developers don't use it I think that's the type of considerations we usually follow

**[44:01]** Yeah I have a nice story about plugins so I created one to organize my recent projects in a directory structure based just on the blaming I published about it and then someone to not mention Mit said but actually you can already do this in other ways so yeah sometimes features are a bit hidden or yeah you just need to know where to find them

**[44:26]** Yeah and I know that we are working on making stuff more discoverable and

**[44:31]** I hope that features like command completion will help in that regard but

**[44:37]** It's it's so often than that. I have had experience showing something unrelated intellig ID and after the talk at the conference someone could come to me and ask hey you showed this is it new and then if you look at the feature it has been there like for 15 years like break points or something like that it has been always there and then for many people's very much new

**[45:10]** And I continue to discover new features on top of the ones that of course we are adding

**[45:17]** But even if the functionality is there people love my plug-in that I created so seems a few of these a few people were looking for the same functionality and didn't find it

**[45:27]** Yeah it's actually sometimes we release new features as plugins because like a plug-in is in a way easier to promote like you can talk it has a homepage it

**[45:36]** It's searchable by Google you can talk about it you it also gives you statistics like you see how many people download the plugin so for example the spring debugger even though it was it's something that is built by the team from the very beginning but it's we are releasing it as a plug-in in for those reasons

**[45:55]** Maybe it will become bundled at some point but right now it's not

**[45:58]** I think the intention is to bundle it at some point as far as I know but right now also because it's new it's a plugin which means that it can be also released in independently. So if they want to do updates to it, they can in regular release cycles.

**[46:13]** We can do this for bundled plugins too. So for example, the EI system plugin is bundled but it's updated very often.

**[46:19]** Yeah,

**[46:19]** That's because everything changes all day every day.

**[46:24]** Yeah, I can confirm that

**[46:26]** A little less, but yeah,

**[46:28]** I can confirm the AI plugin is the one that needs most updates.

**[46:33]** Yep. Yep. On a very regular schedule. Okay. So there are a few announcements you wanted to do. one is and you also blogged about this on Foojay Marit is intellig ID conf 20126 a free virtual event on March 26 and 27th. what can we expect on those days?

**[46:53]** So we will again be live streaming talks on both of those days with experts and industry leaders. Off the top of my head, we have Anna Maria Melchanu from Oracle. we have Josh Long from Spring. we have Victor Gamov. We have Anon who is going to tell us about AI stuff and specri development. And we have my friend Adele Carpenter who is going to show us how she's using Cotlin notebooks for her real world application. and a bunch of other folks. But all of the information is on the website. so we'll link that in the show notes. and you can register for the conference. It's free of charge. the videos will be published at a later date, but if you want to catch it live we will also take questions from the audience. so the speakers can answer your questions that you can ask in the chat. and we hope that many of you will join us live for that and we'll live stream it from our offices in Cyprus and sessions will also be hosted by myself and Anton and our colleagues

**[48:04]** And there's also a documentary coming

**[48:07]** Yes so there's a documentary coming about intellig idea the trailer is already out Dimmitri is in the documentary among others Brian gets Trisha a few other people, some Jet Brains folks and people from around the JVM ecosystem and that should be very interesting for you know the history of our beloved IDE that we've been using for years. So

**[48:35]** Anything more you want to add? We also had a campaign for intellig idea 25th birthday

**[48:41]** And one of the things that we have is a game that you can play inside the IDE that is developed by Alexander Chi Saharas.

**[48:50]** So you know if you want to fight bugs in the IDE not using quick fixes from IntelligJ I idea but using a game created by Alex now you can. It's a separate plugin. I saw his presentation at the conference where he was blowing up his code and committing that code into his repository. So yeah, it's a really fun thing to do.

**[49:13]** Yeah. So he built a game for the IntelligJ idea 25 campaign that you can play inside your IDE to show that you can use IntelligJ IDE as a game engine.

**[49:25]** Okay. thanks a lot for your time. Thanks for joining this recording. also huge thank you to all the listeners. whether you've been using Intellig since the early days or you're just discovering what makes it special, it's clear that this IDE has earned its place in the Java history. If you enjoyed this episode, please subscribe to the Foojay podcast on YouTube or in your favorite podcast app. You can find all our episodes, show notes, and links to the resources mentioned today on Foojay.io. And don't forget to follow Friends of OpenJDK on social media for the latest news and updates from the Java community. Until next time, keep coding and keep learning and maybe take a moment to appreciate the tools that make our work better every day. Thanks for listening.

**[50:13]** Be the friends of OpenJDK.
