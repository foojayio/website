**[0:00]** I'm at Vox Days in Amsterdam and I have a microphone. So, let's talk with the guests of this conference.

**[0:05]** Welcome to the Foojay

**[0:12]** Here at the Vox Days. AI is of course a hot topic, but there's a lot more to discuss. like is Java boring or is it really evolving? And what about security and the fun of coding? I had a lot of guests during the two days here in Amsterdam and I combined them all in this podcast. have fun. Who are you? What's your name? What brings you to Fox Days?

**[0:32]** Hi, my name is Kurk and I'm one of the organizer of Fox Days Amsterdam.

**[0:37]** So, you had to be here.

**[0:38]** Yeah, of course. Yeah, we need to be here and enjoying also, right?

**[0:42]** Mhm.

**[0:43]** It's a couple of years ago that we had the idea like a defox, a defox, you have like the feeling, the defox feeling and hey, we also want that have in the Netherlands and then you're talking with the right people and the right community, the jerks and then you make it happen together. So, that's pretty cool. Yeah.

**[0:59]** Okay. Now let's look at Java. This is Java oriented conference of course. can we say that Java is boring and boring is good.

**[1:07]** Oh wow. There's a session about it actually. Are you referring to that?

**[1:12]** Yeah. Java is very very mature, right? And sometimes boring things are also good. So it's pretty mature. A lot of enterprises are using Java. So Java is the way to go I think right now. Yeah. people say that you can easily move from one Java version to another with your same application. Still did you encounter yourself some challenges in moving from one Java version to newer one?

**[1:38]** Currently not like the last move to Java 25 that was going smoothly because they also fixed some things like like virtual threats and that kind of things that was a thing but now they fix it and it's running smoothly. So yeah. Yeah. So no horror stories from your side about Java evolutions

**[1:56]** About Java evolutions. I want to talk about generics. No like soon like a 1.5 or 1.4 what was it even? No but it's Java is evolving right so it's pretty going to be easier right now and I really look at it look at like removing the bootstrap and that kind of things becoming easier. You can even script Java now and it's pretty cool and it's getting more mainstream I think also. Yeah,

**[2:24]** I'm Johannesburgger. I work at the submachine team at SAP. So I'm literally an OpenJK developer. and I'm here because I'm a speaker.

**[2:32]** Okay. And you I don't know if that's your talk for today, but I saw some blog posts about you. You say Java is boring and boring is good. I can totally agree, but what do you mean?

**[2:43]** That's actually my talk for tomorrow. The talk is called Java 26 is boring, which is why it's brilliant. and please come if you're at a conference and you can win some teachers. But no, I think Java 26 is just boring. It's just a release that didn't bring anything except like for the VM. So you get free performance updates, free performance improvements. And that's the whole thing I finally since Java 8 that mostly the VM changed for the better. And you got some syntactic sugar. but that's a good thing because you can trust Java that it doesn't be that it isn't radical. It's like it's it's not it's rather evolution than revolution. It's a good thing for day-to-day development.

**[3:26]** So people who stay on the long-term support version, so 25, they have to wait until September next year for 29. should they experiment with Java 26?

**[3:38]** Yes, they should. But please not in production. Please experiment and tell your fellow OpenJK developers what you like and what you don't like because otherwise the LTS releases aren't as good because we need people to find the bugs in the pre-releases.

**[3:53]** Mhm.

**[3:53]** Okay. in your talk can you give some examples about why you think it's boring and good?

**[4:00]** So one of the examples is appllets. appllets came in with travel 1.0 but they didn't get removed till now. Now they're getting removed. They t like nine years be between they aren't usable anymore because like the browser drop support and it's really removed and it's really good. Security manager is another thing where it took like a couple of years with warnings and everything. So you can trust Java and you can trust the environment that your application that you wrote like 3 years ago probably also still runs. And that's pretty cool. Isn't that one of the most amazing things that you can run your old Java applications still on a newer version and benefit from all the internal improvements? Yes, that's really cool.

**[4:43]** For example, now with Java 26, you get like up to 15% improvement in throughput with G1 because the garbage collector developers, they improved some synchronization. You don't have to do anything. You get the benefits and I think that's how it should work. one of your other topics I know is JFR and then recording events. What's happening inside GVM with that kind of things?

**[5:09]** So with JK25 I got my own new profiler in so you now have CPU time samples. With JK26 there's not that many changes. only some events added for example an event for the final modification. because finally now finally means final. So when you use when you have a final field use reflection and you try to make it accessible and modifiable, it doesn't work. Now we have an event for this and this kind of works. But with JFR we have mostly things behind the scenes to improve it and to maybe add new APIs on the JVMti level. But let's see what comes out of it. people who never used JFR, what is the main use case that you should look at it?

**[5:55]** So JFR is essentially the profiling solution directly built in into Open JK and you might not use it but your vendors when you use profiling solutions they use it and you can use it to profile your application for example using IntellJ profile plugin or using the one that I wrote that you find on the chatbrains marketplace. So yeah, you use it for profane and also for monitoring albite produce a lot of data. So yeah, check it out. but please test it before you run it in production.

**[6:27]** Okay.

**[6:28]** My name is Litzk Leo and I'm a speaker and volunteer at Fox Days Amsterdam.

**[6:33]** Speaker and a volunteer. Let's start with the volunteering. What how did you help the conference? well, in a lot of ways, just by handing out free freebies, scanning batches, but also like doing social media posts and like everything you can think of. I was helping everywhere I could.

**[6:49]** And happy with how everything is going.

**[6:52]** Yes, of course, there are some fires, but we managed to tame them. So, yeah, we're very happy on how things going. Yeah.

**[6:59]** And then the talk, it's a recurring question I have. Is Java boring? you have a talk about Java being boring or not boring. How should I look at it? Well, the title is Java 26 is boring and that's why it's brilliant and what we're talking about like a lot of Java new version talks are like hype hype hype we want to have this new feature and this and it's amazing like the Americans like you're great this is great and we were like let's do it from another perspective

**[7:29]** Perspective sorry I talked too much already

**[7:33]** And we were like okay if you bump your fashion to Java you're upgrade your version to 26 like you got a lot of free performance for example but you don't have to change any line so that's very boring to do that and there are a lot of other features in that you just get them for free and some other upgrades for Java you have like oh now we have records now we have switch statements of something like that and now it's very boring but under the hood a lot of things happen so we think boring in this case is good it's stable And it's very nice.

**[8:10]** Something you can highlight which was in 26.

**[8:14]** Yes, the garbage collector update. So now you get five to 15% more performance just by upgrading because the garbage collection now looks a whole other way to how to do it. I can explain it. I don't know how many times we have but I think that's a very cool feature that that's in there. Yeah. And that's something that people tend to forget by just bumping your version that you get all this Yeah. this contributes. Yeah. Everyone is contributing to this amazing project which is OpenJDK and you get it for free.

**[8:45]** Yes. It's it's it's so amazing. So, and now with the new releases as well, like every half year there's a new release and every two years an LTS version. that and some people are like yeah I don't want to go upgrade to the smaller versions because I don't know they're afraid but it's just like a myth that they are unstable because they are very well tested and now with release train it only goes with it if it's finished so yeah

**[9:13]** If we look to the next ones 27 the next LTS in a year and a half you think we will see big improvements there but we know it only gets released when it's ready What are you looking forward to?

**[9:26]** Well, I'm really looking forward to project Valhalla and Liliput about compact object errors getting smaller and smaller. So, I'm really curious about when's that going to be in there and not as a preview feature but just like finished versions. So, yeah, really looking forward to that.

**[9:41]** How long are you using Java?

**[9:43]** 11 years now and I'm as old as Java. So, so oneird of my life I'm using Java. Yeah. So you have been through this yeah all these evolutions from 11 years ago or was it

**[9:58]** Well we were just on a very old version so I started with Java 5 and now it's 26 so like I could say like yeah a lot of versions I've seen yeah

**[10:08]** Is it still the same language

**[10:10]** Well it's evaluated evolved like a lot but that's a good thing but when for example records cames and now they're talking about value classes I was like ooh Okay, there's less boiler plates and I like the bulkiness of Java. So, that's an adjustment. But yes, it's the same language, but you evolve just like a human. You're not the same person as 15 years ago. And that's totally fine because you're improving yourself.

**[10:35]** My name is Aisha Loveia. And what brings me to the conference is I'm giving a talk today about streams gathers.

**[10:42]** Okay. isn't this something we already had streams in Java 8? What changed since then? Actually streams are great like cleaner code but sometimes they make us feel like complete idiots and gathers is about to help us like making it even easier like get rid of those custom collector we write it and we forgot sometimes how we did it. So gathers is about making it simple like with the custom gathers with the it's only four part you need to do one is required and we already have a five like already built in out of the box tools or like for example for batching for group overlapping for map concurrent with the virtual trees it's a lot of amazing thing that we can do right now with gathers that only take one line instead of 20. Okay. And when did it get added to Java? Which version was it?

**[11:36]** It's GDK25. It's final like right now we can use it and it's already stable. So no need.

**[11:43]** So it's very recent. So do you see a lot of people companies already shifting to Java 25 in production?

**[11:51]** Actually from what I know I think it's Spotify, Netflix too. They are always making sure that they have the latest GDK. Also in chili they always provide like the latest one in their ID because first thing it's security this is the most important part that why we need to upgrade to the latest version we always make sure that we have the security part it's handle like all this previous default we have the second thing is that we try to make Java more easy to read easy to maintain and to get rid of all the stereotypes we had before because it's structured and people think it's complex to write now it's even more fun

**[12:32]** And also we are trying to work more like on concurrency we have the virtual trade even right now like in the streams with the gathers also the sustainability parts we are right now we are the like many futures that's for example the garbage collectors they're even making a way like efforts to handle It's way better with the resources and also like for the virtual trade right now before there was a lot of waiting time. It didn't make it easier for people to use it in every way but right now they are trying to prove this like to emulate this and that's what make it even better. Also there are some thing like for example main the issue that people are like oh the static main thing it's really long to use right now you can have it in one line. There's a lot of things to do. So people please don't be afraid of the latest GDK read about it or go to conference and see people talk about it in a way fun way to do it if you don't like documentation that's it

**[13:41]** Something you want to add from your talk about these gatherers

**[13:45]** Well yes if you don't want to have this for loop like in a 20 line or a custom collector that you blame the person who write it please use the gathers it's easy to read, easy to maintain and also like it's an intermediate operation. So unlike the terminal one for example use that you cannot keep chaining about it like if you call it the pipeline is dead the game over the gathers you can like do whatever logic you want with it in your streams and you can keep chaining after that. So that's one of the perks of it. So a gather I could look at it as some kind of extended map function for instance but a custom one

**[14:28]** Actually map is a stateless every element like in the it's it run on independently and that's what make it like even more complex. This is why actually gathers come gathers is stateful

**[14:42]** Elements and like it has a memory you can elements can interact with each other you can accumulate through the stream. So this is like a map but with a memory to make sure.

**[14:54]** Okay.

**[14:54]** You did a call to join to use the latest one because it just gets better. But isn't that also one of the big evolutions that writing code becomes more fun, more readable in Java?

**[15:05]** Actually because as a Java developer, I'm always getting some stereotypes about Java is really like hard to do. It's boring. It's a lot of like I will say especially for the people like are used to Python they're like Python is really like concise and I don't have to do like any import etc. But right now with GDK like especially from 25 with the import model that we have it's similar to like it's really easy concise the same way as other people do. So if you are like afraid of using Java just because it was long it's not the same anymore.

**[15:42]** Yeah. It's mostly people who use Java 8 long time ago who say things like that.

**[15:47]** Yes, I have even people who are using Java 7. You are saying, "Oh, Java is hard." Of course, it's hard. There is no streams. There is no like records for example. With the records right now, we get rid of all those classes that we have to do the constructor, the getter and so on. With records, you have your immutable class for model, whatever you want. It's easy to rent. And this is what I said to people. Please don't judge the Java that you used to know but see what's coming next. Hi, my name is Marit and I'm at Voxace Amsterdam because I am part of the program committee. so I did the opening this morning and we'll do again tomorrow and also I'm a speaker. I did my talk with Purple this morning called learning modern Java the playful way.

**[16:35]** Okay, playful. We had we discussed it before. It's Java became more fun to code and that's what you have been illustrating.

**[16:44]** Well so Java now has a six-month release cycle which means that it can be a little bit challenging to stay up to date with all of the Java. So we talk a little bit about like how can you stay up to date. and then we have prepared our demo application to be like Java 811ish type code. And then we go over several of some of our favorite features that have been added since starting from records that were introduced in Java 16 or 17 if you go LTS. and basically transform our code to use records. also to look into some preview features like structured concurrency and basically like update the application and by playing with the code. So and of course you know we are friends and we like to make some silly jokes so that adds a playful element to the talk.

**[17:37]** Okay. So you turned fun into the coding in both the code itself and the presentation.

**[17:42]** Yes. Well that's the intent anyway. and someone was kind enough to come up to us after the talk to say I was entertained and I learned something which is what we strive for. So that was really good. Yeah.

**[17:53]** Some say that Java is boring because it's stable. It's boringly stable. So that means that you can run your old code on a new version. But is so it's also true that while it's boring and stable, it also introduces a lot of new developments.

**[18:09]** Yeah. Well, first of all, I like my software development boring. Please and thank you. I know that there are people who thrive on chaos and would like to have some production incidents to keep life interesting. and I can keep a calm head when there's a production incident and have gotten compliments for doing so. But still, I prefer for things to not be on fire. So, I really appreciate the stability that Java brings and the backwards compatibility, etc. But that said, I also really appreciate the six-month release cycle and the improvements to the language with yeah new new language features, new idioms, new ways of writing code. specifically for example structured concurrency that we use an example of where we have the code like the old way of writing the code with or the current way we should say using completable futures and then transforming that in structured concurrency and what I like about structured concurrency is that it reflects the way of thinking the way that you write the code also reflects how it works of course I'm also very interested in reading code and being able to

**[19:19]** Understand the code. I don't know if people still do that with agents generating the code, but that's a different topic. yeah, so I really like these new idioms making it easier to work with the language, making it easier to express what you want the code to do and understand what the code does.

**[19:36]** That's a quote of your you read more codes than you write code. Is that something that you're not questioning with the eye? first of all, it's not my quote, but I don't know where it came from, and I know that people know that I use it. The so that code is read more than it's written. I'm not sure that will continue to hold true with AI. I hope it will because well, I did my reading code tool once and someone asked, okay, with AI, do people still read code? I'm like, I hope so. I hope you don't push code that you didn't read to production, but you know, gestures broadly at everything and apparently some people do. and yeah, so I don't know if people stop reading the code and start verifying it in different ways, hopefully with tests and other ways to verify that the code does what it's supposed to. maybe we won't be reading the code or we'll be reading it maybe on a different abstraction level because when I say I think people should still read code they go okay how often do you read bite code okay I don't so you know maybe we will read specs or prompts or something else that expresses

**[20:48]** What the code should do I don't know

**[20:51]** Yeah you refer to the evolution in Java in the language of course there also a lot of things happening in under the hoods the speed improvements is that something you see happening that applications run faster, smoother, better.

**[21:05]** So yeah, with each new JDK version you get better security, better performance, etc. And of course, there are a few jabs that tackle that. specifically, I saw a really good talk by Modis Halpitor from the Spring team on what AOT can actually do to speed up our favorite spring application, which is the pet clinic. so yeah, all of that is really interesting to make it faster and make it run more economically or ecologically you know, so that we can spend all of that computing power on AI, I guess.

**[21:42]** Yeah, that's where the money goes nowadays. do you have time to watch some of the talks yourself?

**[21:48]** Yes, I saw several talks. I saw language games by Ellie Holderness, which or Eli Holder Holderness, which was amazing. And I just saw 10 things I hate about Java by Adele Carpenter, which was also really, really brilliant and fun.

**[22:04]** So, my name is Adele Carpenter. I'm here at Fox Days as a speaker. I'm actually lucky enough to be speaking for the second time in the second edition, so that's a pretty good strike rate, which I'm pretty proud of. it was a bit of a no-brainer for me to come and at least apply for Vox Amsterdam. It's a very well-known, recognizable name and how lucky for us to have this event here in Amsterdam.

**[22:24]** Okay. And the talk was about because I think I heard about it.

**[22:27]** Yeah. So, my talk is called 10 Things I Hate About Java. it's not a hit list of 10 Things I Hate about Java. Literally, it's actually based on the 1999 movie 10 Things I Hate About You, which is essentially where this like bad boy chases this like disinterested girl and he sort of pursues her and eventually she goes, "Look, you're frustrating, annoying, and quite possibly the worst, but spoiler alert, I'm going to fall in love with you anyway." So, it sort of really summed up my feelings about Java. I'm a Java developer. It's my daily work. I do love working with Java. but there are some little quirks and frustrating things in it. I mean, it's 30 years old now. it's essentially itself legacy software. So I just wanted to explore how we ended up with the Java that we have today and just to really understand the decisions that the green team Gossling etc made in the '90s.

**[23:16]** Yeah. And do you agree that Java has become better?

**[23:20]** Well, yes. I mean what have Sun and Oracle been doing for the last 30 years if it hasn't gotten better? I mean people do joke I spent a lot of time in the Microsoft community. They joke that Java is dead and you know they talk about the dark ages of Java and yeah maybe that was true but especially since we've hit this new release cadence in the last what half a decade now the pace has really picked up and the Java dark ages are long gone long live Java

**[23:47]** And of those 10 things that frustrate you. How many are left now?

**[23:51]** I think the biggest one is definitely still actually two big ones are still left. so I actually do speak about things that are still there and probably going to stay there forever. so I talk about reified generic or typerasia rather and how C# for example has reified generics and how that does create some unique problems for us as Java developers. and then I also speak about checked exceptions and I have actually a Reddit comment of all things from Brian Goods saying that checked exceptions just absolutely are not going anywhere at all.

**[24:21]** Yep.

**[24:22]** So we're stuck with him. We as far as to take Brian at his word we are stuck with checked exceptions.

**[24:28]** Okay. And of those other things you think most have been solved or you see that they will come in the next versions of Java.

**[24:37]** So I think Java is definitely evolving in a good direction. I did stick to the things that sort of will stay. but I think one thing that I did speak about is project Valhalla. Project Valhalla has been going for over 10 years now. it does promise us some improvements around sort of like performance and memory layout and efficiency there and that is coming. I believe that we will get project Valhalla. We're at Jet or Jet draft stage for the key pieces of work there. So it's coming.

**[25:09]** Okay. one of the things that I kept hurting back is that Java is boring in the sense that it's stable so you can run your old applications. is boring a good sign for you?

**[25:22]** Well, it's this is sort of an idea I do explore in the talk is that maybe Java is backwards compatible to a fault in the eyes of some certainly C# developers. but I think that backwards compatibility and that real the weight that the Java developers place on like as in the Java development team place on backwards compatibility. I think that's really what leads to Java being able to be the backbone of some of our most critical systems on this planet. It is stable. It is mature. And you know what? When you're doing something really important, boring is not a bad thing. but I like that I think we've really started to hit our stride now in that balance between being boring, reliable, dependable, but then also having these cool fun features that are kind of fun to play with.

**[26:12]** Yeah. How do you look at AI development and what's going on there? Are you a bit worried for your job?

**[26:18]** It's a broad question. people like to say, "Oh, I just like solving the problem and you know, creating the best solution for the customer." And I do say those things myself, but that's true. I do love it when users use a feature that we've put in production. I love that feeling. But I do enjoy writing code. I really do. And so I sort of kind of wish that, you know, the best function for me of AI is to do my laundry, so to speak. Like, let me write the music, let me do the paintings, and you can do my laundry. And I kind of at least for as long as I possibly can want to keep that approach with AI and Aentic AI. So, put everything on the latest version. U maybe refactor some things, really sort of put our systems as as modern as possible. And I kind of want the ability to still write some artisal code, you know, some handcrafted really specific stuff. How long I'll be able to do that for, I'm not sure. But I'm certainly willing to embrace this technology and modify my workflow. I mean, my parents did the whole computer revolution. the first time when we just got computers in the workplace,

**[27:27]** They went from notebooks and actual filing cabinets to a computer on the desktop. this is a similar kind of revolution. I think work's just going to change.

**[27:37]** So I'm Patrick from Switzerland and actually like we were running last week Vox day sur and basically since Vox is Amsterdam is also like part of the family of Voxes and we belong to that. I'm actually speaking here tomorrow about my favorite topic about how do I optimize my spring boot applications.

**[27:54]** Okay. So you're two things. You're an organizer.

**[27:58]** How hard is it to organize a conference like this?

**[28:01]** Oh, you won't imagine. sometimes it's super hard because like you might have issues and even though like participants are not really like realizing what kind of issues you have but sometimes you're really like sweating and running. So yeah, so I really appreciate that. Now I have like time to enjoy the conference and be here and they basically like mingle with people because last week we also had some some challenges with audio and that means like you're just running around to like solve the problems and there the fun fact is there is always like an issue you know even though participants don't realize so I really appreciate that it works flawlessly here so far at least and like fingers crossed and yeah I understand like what it means and then the second thing is you have a talk so optimizing spring boots a few tips that you can give well actually like what I've learned in the last I would say like 15 20 years I packed into my talk so it's actually a deep dive so we'll go like through the newest features of Java how to integrate that I also like

**[29:06]** Combine it with my favorite topic which is build packs how to do containers like without writing a docker file and I'll I'll just show like people how they can basically like improve their spring boot applications like with simple steps just using the configurations in the Maven plugin and the gradal plugin and then you will have like for example smaller run image sizes or like you can easily change to the latest version of Java or you use something like compact object headers virtual threads and so on and actually I do benchmarks so I can show you like in which direction your optimization will actually like lead in your application and also like sometimes you don't see like what you would expect and of course I mean there is room for discussion but it's also always like your application is not like mine. So we have to take it with a grain of salt but at least you get an indication. So yeah that's actually like super nice and I'm I'm pretty sure people can take about a handful of tips they can implement within 10 minutes and that's super cool because it's just

**[30:09]** There and nobody knows about it you know. so you do benchmarks that means you can check improvements in your code but do you also see improvements just by switching to a newer Java version for instance? I don't I don't do like code optimizations. So I assume your application is like your application and you know like how to optimize it but we basically like just as said before we are changing the container image we are changing like the Java versions we are changing like some parameters on Java. There is some ideas about spring boot where you put some parameters there like as I said like virtual threads enabling we use something like AOT caching so spring AOT and AOT caching and other things. So basically like I show you more like the parameters you could use which is like general there and it's not specific to your applications. Of course I have a slide in there where I say like these are topics you probably should also have a look at but I can't help you with that. So that's the thing. Yeah. optimizing containers also is building your own runtime from for Java one of those things you should do you

**[31:19]** Think about JLink I guess of course

**[31:21]** So it's one of the solutions basically like to shrink container image size quite a bit and since it's basically like with build packs just like one or two flags you add sometimes I do two because I know it a little bit better what I want yes that's that's definitely one thing Yeah. what is the difference between a docker and a build pack? So for me build packs are better because like imagine you know that you have like layers in containers and that that's great but that means like if you change one layer you have to rebuild all the other steps. Now build packs allow me to basically like relink the layers. So if the layer was already done and nothing has changed in the layer, they really can reuse that layer and you don't have to do like from where things have changed downwards everything again.

**[32:11]** So that means like if you do the caching properly in the pipeline, your builds are actually faster.

**[32:16]** And another great advantage I think is so many people just do the docker files wrong. And buildpax is a tool like for devops teams or platform engineering where you can say like we have one approach how to tackle containers but it doesn't matter if it's python if it's javascript or if it's java we do it the same way. So basically you standardize it across the different platforms and you just can say like that's the way how we add for example CA certificates it's always the same it's not like customuilt container and that's another thing which is great. So we're standardizing it for teams and we make sure that not everyone needs to know how to build containers the right way

**[32:56]** And think of this what you find in the internet often is just like the docker file which basically runs with root privileges. So that's not the way you should do it. Yeah.

**[33:07]** If I want to learn build packs how do I get started?

**[33:11]** Well it's built in spring boot actually. So just use it. So if you run mvn spring minus boot colon build minus image then you get the containers built with the build packs. So it's there and of course if you are not in the spring world and if you using Quarkus there is an extension for that. you can use pto the pex cli directly on the command line. You don't need to do it like via maven or gradal. So there is enough resources out there but still like you have a lot of possibilities and actually it's super easy to write your own bill packs because imagine in your company you need to have a custom font added to the container. So that means actually like you're building one of a bill pack yourself that means a bill pack is just like one step in the process to create the container and then you can do that yourself. before EOT caching was not there yet. I did it myself because it's quite easy to write the bash script to do the training run to add the cache file basically and manipulate the starting parameters for the JVM. So yeah if you get into it I find it quite fascinating. And the

**[34:19]** Other thing is actually like the development team does not need to care about how containers are built because they're built the best way when you use build packs. That's why I like it a lot because it removes my cognitive overload basically from another topic. So I don't need to know about infrastructure so much. Right. So that's great. Yeah. Other people have managed it already for you.

**[34:41]** Yeah. Exactly. And the best of in the best way because like would you know that the spring boot application with the fat char is not running faster than for example if you explode the whole thing and run it from the command line in an exploded way. So this is kind of like what the spring team says is best practice and it's just built in. So I don't really have to care about that. So that's awesome.

**[35:02]** My name is Sohan Maheshwar and I'm here at walks days to talk about the problem of authorization and why it's so important today. Okay.

**[35:10]** So you're a presenter. You gave a talk here.

**[35:12]** Yes, I have a talk in a couple of hours. It's about authorization again.

**[35:16]** Nervous?

**[35:16]** No.

**[35:18]** Good thing because I know what I'm going to speak about makes sense. So, and I think it'll resonate with the audience because authorization is such a critical piece of the enterprise stack right now. for all those people writing Java, you're probably writing enterprise critical code, right? And authorization is such a problem now. In fact, OASP, which I'm sure most of us have heard of, defines broken access control as number one in the risk to web apps

**[35:42]** In the last like 5 years, you know. and that's what I'm here to talk about.

**[35:45]** Okay.

**[35:46]** Are you talking about best practices, some techniques that can use or some systems that you can integrate? I'm specifically talking about something slightly controversial which could be fun which is why using jots or JSON web tokens for authorization is not a good idea and it's a lightning talk but that's what I'm going to be speaking about today

**[36:03]** And why is it the bad idea or is it spoiling your presentation?

**[36:06]** No no I'm happy to talk about it. So essentially people use jots for things like read permissions and oh this person's an admin and things but the fundamental flaw is once a token is passed you can't meaningfully revoke it you know so say something changes in the org someone's access is removed that token's still out there in the wild so you'll have to invalidate it there it's not a very elegant situation and that does lead to a lot of security flaws there are a bunch of other reasons as well but that is the main reason why you know using jots is not great for authorization

**[36:36]** Okay what's better bests.

**[36:38]** So according to OASP themselves using like any modern way of authorization is what counts. there are a few different ways depending on your use case right. there are like what is called a Zanzibar like system which is based on Google's internal authorization where you map out authorization as a graph of relationships. So if a user can edit document one there's a relationship between user and document one. The other one is using policy engines like OPA and Cedar etc. which evaluates it against a set of rules. But again, the idea is centralizing your authorization which reflects the reality of the system you're operating as opposed throwing a token out there, you know, which and you don't know what's going to happen later.

**[37:18]** So that's the modern stack and I think both apply to AI and agents and rag and stuff as well.

**[37:24]** So the modern way is having an upto-date realtime status that you can request. Yes, doing that with what's called fine grain permissions. Even with jots and what we use right now is called like a role based access control which is very broad. It's very coarse and as a software engineer you probably have access to millions of software objects, right? So just a role of software engineer is not safe anymore. So you need fine grained access to like a certain repo or you know a certain codebase with only a certain type of permission and the modern authorization systems actually enable that.

**[37:54]** Yeah. Okay.

**[37:55]** Are you a Java developer yourself? No, I'm not. I did a bit of Java way back in university. I was never good at it. I've always done like either Python or JavaScript, TypeScript. never Java though.

**[38:07]** Okay. But I can imagine at a conference like this that there are different topics and different talks that you can join. What did you learn?

**[38:13]** Well, so much to learn actually. One is it's pretty cool to see that you know Java is still going strong as an ecosystem. it's there and like I said lots of mission critical resources. lots of talk about AI and the Java world as well. I saw a talk about that too. So, you know, I think lots to learn. The industry is moving so quickly, so it's hard to like really keep up. But yeah,

**[38:34]** I am ego and I'm speaking tomorrow at folks days. but of course the Java community, I really want to be involved. So, I really like it. So, that's also why I'm here.

**[38:44]** And you're speaking about I'm speaking about AI of course. I'm prompt engineering advocate for social. So I'm going to a lot of companies to help them on prompt engineering and working with agentic systems in software development and my presentation tomorrow will be about aentic programming. This is how it's done. That's the title and I'm really going to deep dive in how can we solve the problems that we see at the moment at enterprise environments.

**[39:10]** Okay. it's the new way of programming. A lot of people are afraid of it, see it as a threat. How do you look at it? Well where my presentation is also going to be about is that what we are currently trying to do is really to map new technology like generative AI on old processes and that's also where it's getting difficult for people to keep up to understand what's going to happen and I've done a lot of research we wrote a book about it as well to really change the process that we are currently very very used to come up with something new that really integrates AI and then it also makes it way easier for us to adopt it. That will also change our roles as developers, for example. but I think it will be much more fun if we really understand it and really can collaborate with the new tooling.

**[40:02]** Okay. So, you're not looking at it as a threat.

**[40:05]** I'm definitely not looking as a threat. No. I do a lot of P programming nowadays with assisted coding. So, I have a new colleague, let's say. is that how we should look like it cooperate and work on certain problems and challenges?

**[40:23]** Yeah, I think that is definitely the first step. So that will really help you to also understand how it works, right? And you will also face a lot of issues. but what I think that is really going to change is that we are going to put much more to AI systems. So we are just spinning up agents and they will build a software for us but someone should guide it make sure that quality is on par and also of course security things AI is not that good in it yet and of course we all know the things that happened to Java etc the issues that came up there is no documentation about that and it's not trained on it so we should be the person and we should be the people who solve this with AI and not only ourselves so I think we will shift more towards sending it to agents.

**[41:08]** They will do the heavy lifting, let's say, but we will steer where it should go and set the boundaries.

**[41:14]** I even heard that it could be that we need more developers in the future because there are a lot of box proof of concepts just throwing out and then yes, someone has to make them more stable and better and extend them. Is could that also be the new role of developers? Well, the new role for developers, I can give you that away already a little bit, is I think forward deployed engineers and this are just developers like how we are used to it, but they are more forward deployed to the customers. So they are more in touch with customers, clients with end users and so that they can solve the real problems that they're facing instead of listening to a product owner and then building what is being said to by that person. So I think our work will shift more to u communication will be much more important and critical thinking and all those kind of things development a bit less but we should stay critical in those kind of things because we see all the things happening. So what I think in terms of how it will evolve is that definitely because how economics work and how companies work is that you will

**[42:20]** See probably a delay of a decrease a little bit in the beginning but what you already mentioning in the end we just want more. So we can build more but then we want also more and companies want the same. So that will bring us back in the race let's say to shift towards that position.

**[42:36]** Actually it's a returning story. I once talked to developers who said, "Yeah, I started doing development because I want to be on a computer and not talk to people."

**[42:46]** But that's that's not a bad that's not a good way of doing it. You have to talk to your end user. Yeah. And I think right you can now build applications in minutes, let's say. but what are you actually solving? And if you're really going to put it in the field and get feedback from people, then you are getting the right discussions and you can build way better software. So I think that is really the way forward and I I'm really looking forward to it because that's what we like to do, right? solving problems, making sure that we really make impact on society or wherever you're working for. And I think that will bring us much closer to that only development and building something that might hit the field and the right people.

**[43:32]** My name is Mandas. I come to Fox because I'm a Java enthusiast. I'm an architect nowadays, but I like programming. I'm still I like to learn about development. So,

**[43:45]** Okay. And I heard that you gave a talk yourself at the J.

**[43:48]** Yeah. Yeah, that's right. So the ARMS I gave a talk about cryptography about digital signatures.

**[43:55]** Related to Java and how they are used in Java. yes, it is related to Java, but the topic is of course much more generic in the world of IT. It's really about securing data and securing messages. but of course showing also a little bit of code examples in cotlin in my case actually on how can we secure data in cotlin so you're a java and a cotlin lover

**[44:24]** Yes actually last year I started learning myself cotlin because some of my teams were doing it and I hadn't done it before so I really I started learning it because I understood that more and more developers are going into into cotlin now so I like cotlin a lot.

**[44:43]** It runs on the GVM, so it's a good language. Can you compare what is the main benefit for you of coding in Cotlin?

**[44:51]** Well, it's made by developers for developers. That's the I believe that's the main reason and I was just in a talk about the 10 things that I hate about Java. And I think that's one of the main reasons why some of us choose to go with cotlin because it's a little bit less annoying and it's quite elegant. So yeah,

**[45:12]** My name is Alex Alexander Shopov. what brings me to Vox Days? I won a ticket for Vox Days. Yes. So that's wonderful.

**[45:21]** What are the talks that you're looking forward to? Oh yeah.

**[45:24]** Yeah like I chose to be at the short talks which means it's a high dynamic very different and varied talks. So it's interesting and yeah otherwise I as usual I come for to look around what's available what people are doing who is hiring and yeah what other people are doing there is always the fear of missing out and I can see that I'm not missing out anything.

**[45:56]** Yeah a lot of sessions at the same time I guess you're a Java developer. Ah yeah I used to be like I was used to be a run-of-the-mill Java enterprise developer which means we all danced around the corporate database. but recently I've been programming mainly Python and a lot of Go.

**[46:19]** Okay. So like I work at Uber. Java is used mainly in financial services and a lot in data processing like churning lots amounts of data like data pipelines the data lake where all the information is port. So yeah so you can compare Java, Python. What is the main advantage of doing something in Java or another language for you?

**[46:53]** Oh it's always like what type of people you have like you have if you have Java people then Java will be the best solution. and in the last years like things have changed. neither Java nor Go are they used to be. so I'm looking forward to like the current way of modernizing Java and it is taking a lot of things from other languages. and like I'm not so interested in the language part but more about the engineering part the tools that you get available with the thing that you you're getting how you can refactor quickly and stuff like that.

**[47:44]** Yeah.

**[47:44]** And has Java an advantage there or is it just as good in other tools if you're just have the experiments with the language? Oh definitely I have the experience but like if we go in particular to compare Java to let's say Go Java is much more cooperative language like on the JVM you have other languages on top of and they are popular

**[48:10]** And you have like many more implementations of other languages on top of that. there is currently the whole thing with the Java implementation of the Java runtime. So that is quite interesting. So in this sense Java is much more cooperative than the other ones. where you would be more like confined within the ecosystem like in Go there is always like everything should be in Go. in Python there is like the usual cooperation with the s runtime like modules developed in C or like forran for the data part but then like your typical Python programmer is not a C or a forran developer while with Java there is a lot more to and fro like people trying Cotlin or Scala for example and seeing how things go so it is not so siloed within its own ecosystem.

**[49:18]** I have a new guest. What's your name and what brings you to Vox Days? M and getting new knowledge and learning new things.

**[49:26]** And what did you learn? A lot of talks, so I think you have to pick one. Yeah. Yeah. Yeah. Well, I really love the talk about building your own Spotify rap. there was a interesting like key takeaways and one was LMS don't always work. The 2024 raft was apparently a mess. I had missed that. pro possibly due to using LLMs and the fun takeaway was that you can use GDPR to get your data and then perform analysis on it. So data is cool and apparently GDPR is always also cool.

**[50:00]** Mhm.

**[50:00]** Yeah. It's unbelievable what companies know about us and what they also keep in the databases.

**[50:06]** True. True. True. And the fun what I found interesting because as developers we're normally on the back end side so we have to be aware of GDPR but in this case it was flipped around the script was flipped and using your own data to perform your own analysis. It was yeah really fun to see.

**[50:26]** Okay. So she made her own Spotify summary of her what she loves about music. Yeah, correct. Correct.

**[50:33]** And probably can use that also for Yeah. any other use case like shopping and

**[50:37]** Yeah. Yeah. Yeah. Yeah. She also told about getting the asking the data from in this case a high supermarket. Yeah. Where you can shop online and stuff like that. So that's also fun that you can actually use as a customer you can also use this and perform analysis yourself on your own habits essentially.

**[50:55]** Mhm. I guess you're here so you're a Java developer correct for a long time. yeah very long time since 1.2.

**[51:05]** Yeah.

**[51:05]** So you can compare Java what we have now with the early Java. Is it like someone said it's boring as stable and good but still evolving. I think that's a proper summary. Yes. because it hasn't changed that much essenti essence and where other languages sometimes have like a hard break compared to earlier versions and this will most likely run for a long time. I have this like the Java 8 to Java 9 bump is a is one that needs attention and well for other case normally it just works out of the box. So that's really nice.

**[51:48]** Yeah.

**[51:49]** What was for you in the job that you do? One of the most important changes in Java virtual threats or one of the other evolutions there?

**[51:58]** Oh dear. Probably the streaming API that's that was a real innovation. and the virtual threats it's it's a performance thing but usually as normal normal back end developers you're not that well work on that level. I agree that streams I think was Java 8 that was a big shift. I think C# had that before we did but that was like like a real new a new way of thinking and reasoning about your code and making it more compact and self-describing essentially. so yeah

**[52:35]** My name is Audian times and I'm in Fox at Fox days to just look at the talks of course. I'm normally like in a more active role. So we either have a booth as a conference or we have a talk ourselves. but today I'm just as a visitor here.

**[52:51]** Okay. What's your role in the Java community? like I'm a product lead of several Jakarta specs. So I'm leading Jakarta faces although my coworker Bus is doing most of the actual work. I'm doing Jakarta security Jakarta authentication authorization. I was the co-coordinator of Sakarta E11 and I'm a committer of classes. So I'm doing like a lot of work over there. so yeah mostly the Jakarta environment and the Jakarta specs.

**[53:22]** A lot of Jakarta.

**[53:24]** Yeah

**[53:24]** For someone who's new to Java dement how can you explain what Jakarta does? Okay. So, Jakarta is a framework, a full stack framework mostly suited for the server side. and it basically helps you with everything you do in your Java application. So, it takes care of security for you. transactions, APIs for persistence are there. If you need to validate your input, we have APIs for that. if you need to respond to HTTP requests, of course, I think everybody knows that there's a serlet API. but there's also the websocket and the rest APIs. there's a bean model CDI. So that's the foundational part of the platform. So yeah, quite a lot al together.

**[54:12]** Someone who does spring should recognize it because of the annotations for instance.

**[54:17]** Yeah. So Spring does use a number of Jakarta specs. so specifically Jakarta persistence. Many people still know it by it previous name JPA. that we simplified all the names a while ago. So all the obscure abbreviations like GTS, JMS, GTA, GCCA. I think even some hard hard times or some people who were involved. They got lost what all the abbreviations actually meant. They were inconsistent too. So like I saw some of them for instance in JPA I think it was API where the A stood for but in JCA it was architecture and it was like really confusing so we basically looked at spring and we took all the simple names so it's just Jakarta security Jakarta rest Jakarta authentication so that made everything a bit more simple but so yeah spring people use spring they would recognize Jakarta because we they use a number of specs APIs from Jakarta.

**[55:30]** I think you have some history in Java some experience in Java with the earlier versions. How do you look at the current Java compared to the older ones?

**[55:39]** Yeah so Java is clearly evolving and Java is taking a more careful approach. So it not bolts on features nearly really. it does take a lot of care and a lot of thinking how would this feature this new feature in Java interact with all the existing stuff that we have. so backwards compatibility is quite important in Java. but also like making sure as much as you possibly can that like a new feature in Java does not cause problems down the line when new things are going to be introduced. that does make Java frustrating sometimes because you wait a long time for new features. Bahala I mean that has been on the waiting list for like forever. I don't know if it ever will be released. but the people are quite careful with it and that's good.

**[56:29]** Yeah, that's one of those things that I find about remarkable Java. Indeed, it takes some long time but they really think about yeah how are we going to use this new feature that we add now in the future with other things. And I think that one of the blocking things for Vajala, correct?

**[56:46]** Yeah. So you clearly see that there's a big plan. There's a big road map. like you don't always see that road map yet. Of course, if you read all the mailing list, you spit through all the jabs, you would have an idea of what's going on. but casually if you look at it, you think, okay, why don't they just add this feature? I mean, it's simple. but as typically Brian gets explained, it's not that simple because it will interact with this and this and that and so they need to weigh a lot of different factors in before they can add a new feature.

**[57:22]** You talking about the mailing list so anyone can join the mailing lists which are within the OpenJDK project. Is it something interesting for a developer to just watch what's happening there?

**[57:32]** Yeah, I think so. Absolutely. like I think that contributing to open source of open source in general it starts of course with using open source now well I think almost everybody does that these days that's not controversial anymore compared to maybe like in 2003 where open source was still a bit awkward sometimes for companies that that's now completely the normal but the next phase is typically just surfing observing the mailing lists looking at issues is really tickets just have an idea of what's going on and when you follow along those things at some points you think okay maybe I can contribute here and it could be like an opinion it could maybe like you don't understand something and then you might think okay this is weird to ask because there's all those so-called clever people on the mailing list and I don't understand this but basically all the people they're not the super clever They're just the same as you. Like we're not smarter than the effortless developer. We like we speak now Jakarta terms, but I think it holds for the OpenJDK too. a lot of them

**[58:46]** Are just regular developers like everybody. They just happen to be assigned to the OpenJDK project or they happen to be assigned to the Scarta projects or to the Spring projects maybe, but they're not inherently smarter or better. So if you don't understand something which you see on the menu list and you ask the question about it then it might be that something is simply not clear. It's not carefully worded, it's not fleshed out correctly. So question a simple question can already go a long way.

**[59:16]** Yeah. So contributing to open source is not only fixing a bug or implementing a new feature.

**[59:22]** Absolutely not. It's like participating in the discussions. it's providing help where you can. So it could be like fixing typos but also just asking questions voicing opinions but also just trying out features like that's what a lot of people on the projects always ask for like just try this out give us feedback does it work for you does the syntax or the API methods do they speak to you so to say what is your opinion about it

**[59:55]** My name is Yost Khan and what brings me to vox days. Well, last year I heard from a lot of my Java team members that this was their favorite conference of the year and before that I always went to Jfall. So, you know, figured got a look around and I saw a lot of interesting speakers on the lineup. So, I'm really excited to be here.

**[1:00:14]** A lot of talks. which topics do you pick?

**[1:00:18]** So, I'm more on the learning side of things. I'm not an engineer myself. So I just went to the talk of Simon Deite which was super interesting you know now that there's so many new tools coming out every single week and so much information I think concepts such as rate of learning and rate of retainment which she talks about in her talk are super important to you want to stay ahead of the curve so I'm looking around on those and yeah absorbing as much as I can. How do you look at Java evolutions, new things in the language?

**[1:00:55]** Well, that's I don't think I'm the right person to ask that, you know, since I'm not an engineer myself. I did see a really interesting talk by Anton yesterday Anton Aripov on Coug and how you can integrate your LLMs into your Java code. And I think that's a topic that a lot of people are interested in right now, right? And you also see it in the attendance per talk. a lot of the talks that feature some kind of AI topic even though we all have a certain degree of AI allergy of course but still you see that those rooms are most full and I think that's very interesting. Yeah.

**[1:01:38]** Yeah. It's it's definitely changing how we write code, how we work with projects, how we write our requirements. So it's all instructions for an AI tool and we as developer are there somehow to guide all this.

**[1:01:54]** Yeah. I mean so like a year ago you know I think we were very opposed to it because you know there was a push from top down to implement as much AI as we could and engineers by nature like to do things in their own way but I think by now we're all starting to see the value of it also for our own work. but human in the loop remains I think one of the core concept that I'm hearing here at the conference a lot but also at the meetups I go to. AI on the AMT store is a really interesting meetup group that I go to frequently in Amsterdam. And we are absolutely not at the level yet that these agents can be fully autonomous even though we love using the word autonomy in this context but you know we're not there at all yet. And I think we're finding out where exactly the role of agents lie and therefore also logically where the role for humans lies in you know in the new way of working and so far I think there's a lot of roles for us to play still orchestration reviewing we're still the people that are responsible and accountable for the quality of the code. So that's not

**[1:03:08]** Changing anytime soon. quality and also the way that the code is readable and maintainable those things we will need to monitor as a developer of an application.

**[1:03:18]** Yeah. Yeah. Absolutely. maintainability I think is one of the areas where AI is making more steps than others. but for sure accountability humans are the ones that are responsible for you know reaching a specific goal or implementing a specific feature. an agent can't be Yeah, you can give it feedback on how to do it better next time, but you can't say, "Oh, you missed your deadline. How dare you?" You know, so yeah, we're not there for a long time. Yeah.

**[1:03:49]** Okay. Challenging times. Thank you.

**[1:03:51]** Absolutely. Thanks, Frank.

**[1:03:52]** So, I'm Stefan Jansen. I'm actually the co-founder of Fox Days. so, you know, I love to come to these type of events where I'm just a tourist and I can enjoy talks, but mainly the hallway discussions.

**[1:04:04]** I mean, you know, normally I work at home. I'm alone so I can't really talk to my wife about technology because she's not interested in that stuff. But here you know I have so many like-minded people where we can just chat and like yesterday I think I followed three talks excluding the keynotes I did of course but I was just sitting at a table talking with other people friends and ex-colagues etc talking about you know the state of our community especially AI coding of course how people are using it etc and that's that's just the best I mean

**[1:04:39]** I don't think you can ever replace that with AI because human beings, we're social animals. We want to get out of our sellers and talk to like-minded people and share ideas and new approaches, etc. So, for me, that's really what I love of these type of conferences.

**[1:04:59]** You mentioned AI, of course, you're a big fan of AI, you're a big user, but what is the sentiment around AI or developers feeling threatened?

**[1:05:07]** So, I call it the quantum superposition state. It's both exciting and frightening at the same time. And it still is. I mean, but to be honest, we're like cutting off the what is it? The legs of our chairs below us. We're basically factoring out our own jobs, which is a bit strange, but I've never been more productive since I've been switching away from code. I mean, since July last year, I've started VIP coding. Now, I'm doing something more interesting, which is called agentic engineering. But basically it's specd driven developments. and it's just amazing. I mean you know we have this baseline for DevOps with the developer community. I think I'm going to rebrand it to the builder community because I'm not the developer anymore. I'm a builder. I build things and the code creation the AI coding agents can do this for me much better than I can.

**[1:06:01]** But I'm like turning into a specd driven technical project lead where if I have a creative ID I can realize it in a weekend. So it's like it does open up quite a bit. But of course the other downside of it is that yeah junior developers or just developers in general will be still need developers.

**[1:06:21]** I mean that's that's huge, right? I mean that's something we're going to be struggling with and we need to pivot and reposition ourselves as an industry. but it does open up a whole lot of new opportunities as well. The bottleneck is not developing anymore. The bottleneck is coming up with new features, new ideas. The implementation that's not the issue anymore.

**[1:06:42]** So a developer should more than ever be in contact with the end user and know exactly which problem I'm trying to solve here.

**[1:06:50]** Yeah, I agree. I agree. it's it's the features, the usability, and the implementation is a detail. If it's in Java or in Go and Rust, it doesn't matter. If you have a good spec, it can be developed in any language. So, whatever you feel comfortable with because at a certain moment, you still want to look a bit at the code and you we're all boys, you know, you want to open up the package and see what was generated. But if you start, that's my workflow to these days is that I'm not using one LLM. I'm not just one using one cloud code CLI. I'm using multiple different ones. So for example, I'm using for the moment I'm using a Chinese model Kimmy to create my specs. I have cloth code to actually implement and then I use codeex with GPT 5.4 for to review it and they both like the specification has like acceptance criteria and if the acceptance criteria are not met CEX will say to CL hey you still need to fix this okay it tries to fix it then it goes back hey and so it goes in this type of dance and so I was doing this manually and I was like man this doesn't scale if I do this manually

**[1:07:56]** So I developed my own IDE called Genie Builder which is a plug for the ID but it's like you know it's a it's a personal project where I created a visual workflow where You can document these type of collaborations with different agents using CLI runs and so on. Before I go to bed, I select 10 features. I link it to the workflow that I defined which is a visual workflow and then it runs while I sleep and in the morning all these features have been implemented and I need have a whole full day again to think about what other features should I add. And that's that's now how I work.

**[1:08:32]** But that's just crazy.

**[1:08:34]** And is that an open source project? It's not yet open source. It's free. It's a There's a beta version. So, Geniebuilder.ai, you can just download it and play with it. But it's more like I think we're moving more towards personal software because it's so easy to build software that if you have I mean, you're doing the same thing, right? You just build your own project for myself and sure I'll put it on a website so other people can use it, but yeah, it's just there for me to be more productive. That's a wrap for this episode of the Foojay podcast. Please subscribe on YouTube or in your favorite podcast app if you want to learn more about what's happening in the Java world and all the articles that are published on Foojay.io. Thank you for listening. Thank you for watching and see you next time.

**[1:09:20]** Give me the friends of OpenJDK.
