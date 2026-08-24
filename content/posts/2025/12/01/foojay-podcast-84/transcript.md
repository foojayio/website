**[0:00]** Let's find out how you as a developer can have an impact on the ecological cost of your code and applications.

**[0:06]** Give me a OpenJDK.

**[0:13]** Welcome to another episode of the Foojay podcast. Today we are exploring a critical topic that's becoming increasingly important in our industry. Develop sustainable software that is both performant and environmental friendly. At the DevOps and JFol conferences, I had fascinating conversations about how we as Java developers can make a real impact on both our cloud cost and our carbon footprint. And it's interesting to learn how these two goals are often perfectly aligned. What's good for your budget is also good for the planet. But let's start with Daniel Witkovski. He published an article on Foojay this week that takes us on a deep dive into performance tuning. He explains why optimizing your code can have a thousand times more impact than saving 30% on cloud costs and walks us through his journey of tuning a simple integer validation challenge into a masterclass on Java performance optimization. Next, I caught up with Kurk who shares his passion for sustainable engineering and space exploration. He introduces us to Kepler, a tool for monitoring the energy consumption of your applications and

**[1:26]** Explains how performance optimization naturally leads to sustainable improvements. Then Ronald Dehiser, founder of JobRunner, reveals how his open-source job scheduling library now enables carbonaware job processing. He explains how JobRunner can automatically schedule non-timer critical jobs to run when renewable energy is the most available. And finally, Yan Owens joins us to discuss practical strategies for reducing both costs and CO2 emissions in your applications. He explains why cloud spending is actually a good proxy for your carbon footprint. So, let's get started. My name is Daniel Vitkovski. I'm a solution consultant working for Azul. I started many many years ago. So I've been through all the pivots that we did starting from the physical hardware appliances that we built ourselves to the virtual machines that we are running just now. And I'm working with our customers helping them to get most of our JVMs.

**[2:31]** Okay. and I invited you for the podcast because you wrote a very interesting article which is called the art of performance tuning. why saving 30% in the cloud means nothing if your code wastes a thousand times more. so you dove into some kind of challenge to see how you can get the best performance and maybe at the same time also gain some yeah ecological benefit out of it. Correct.

**[3:03]** Yes. in Azu we are spending a lot of time trying to help our customers tune their systems you know increase performance and recently reduced the cloud spending. So making application faster maybe visible to the financial services people as cost savings on reducing the CO2 footprint. So you can look at either way. I think that's a nice way of if you as a developer are aware of that you have an ecological impact, you can still sell it to your managers as this will also reduce the cost. So we're gaining on two fronts. Correct.

**[3:45]** Yes, that's correct. And we see this more and more frequently because it's not only about improving something but also reducing the footprint even if it's like physical footprint virtual footprint or CO2.

**[3:59]** In the article you also referred to a research I think it's from a few years ago where they compared the efficienc of a language. So is Java a good language in terms of how much energy it uses? Since I'm working with Java, I would say yes, of course. but the article I think uses C as a baseline and that they assume that C is like you know one and Java is referred as 1.5. So sometimes you can see that it is a little bit less energy efficient compared to C. but C++ is not far behind. so I would say this is still one of the best languages if you want to look at the performance impact or energy impact of a language

**[4:51]** And which are the bad ones on graph.

**[4:54]** Yeah. So interestingly I think and we have probably like 20 different languages. So you can always go and check yourself. But I think one that stands out is Python because now probably everybody who is trying to do something around machine learning AI analytical processing and they are frequently using Python and you can argue if 1.5 or two is high compared to C as a baseline but Python is I think 50 times more energy hungry compared to C. So you know that this is really really even more than order of magnitude more energy that you need to spend and probably related to the fact that Python is not being compiled. So it is usually interpreted and that's using a lot of energy and a lot of resources.

**[5:48]** Then the challenge so the challenge that you try to solve in the article is about checking if a string is an integer. Correct?

**[5:57]** Yes. So this was an interesting puzzle or exercise and you know since I probably even before I started my professional career I was trying to get some puzzles sorted you know play chess or solve some algorithmic challenges. So I always like to see if I still can do something useful and it seems like this exercise or this challenge was quite easy. you had a string or a lot of strings that you use as an input and then you can validate if this is an integer and if it's is like within some boundaries that were defined for this challenge.

**[6:41]** You took several steps to yeah to compare different ways of implementing this but actually how did you compare the result? Did you use some kind of tool to see if your code was really improving? This is interesting because the nice thing is that you got the defoon framework where you can write start your Java application and it was loading all the data that were required and then it was running through this method that was checking the result in a loop. so you don't need to use any external framework. You don't need to really do anything with that. but this is the first thing that probably I didn't describe in my article so that may be an interesting topic to cover here as well. the thing is that you had I think two millions of strings that you need to check and you had three data sets each was having more and more errors in the input data and I remember when I was trying to play around with algorithmic challenges one of the restrictions was that you couldn't use more memory than you were assigned. So in this case you

**[8:00]** Know that the test case was just loading one string at a time and that's probably not efficient if you want to test if your function is really working or if your method is really working as fast as possible but at the same time you can load technically unlimited amount of strings right because you go for through one by one and then if you have one 100,000 2 millions 10 50 millions, it doesn't really matter. And in this case, the solution was to load everything into the memory. So you can have everything in memory and you can go through these strings really quickly. But at the same time, the challenge was that you know if the data set got too big, you just had out of memory error, right?

**[8:51]** So the first thing I tried to understand is how much data we have and if this will be something that we can easily fit into the memory for the test case and in this case it worked but in many cases you are just not able to efficiently test it. And this is something that we frequently see with our customers when we try our high performance in JVM when they say that the only way they can measure if it works or helps them is in production because outside of production they have have not enough data, not enough users, there is not enough load. So in many cases the first question we are asking is you know how you can verify that it is better right and this was probably the first

**[9:35]** Challenge here. So we started with just loading everything into the memory and this allowed us to go through the 1 million or 2 million checks really quickly.

**[9:47]** It's not using the Java performance microbenchmark JH because I had this in the previous podcast. So I talked to Franuis Mart who had a talk at JF. He was explaining that you can also do this kind of performance test and see if your code improves with JH but yeah it's not in production. So it's something

**[10:08]** Not in production.

**[10:10]** Correct. So this is framework that we are using frequently and the nice thing about JH is that you can define that you have so and many warm- up cycles. It takes so long to warm up. So you are really avoiding a lot of challenges o of the Java runtime and you can make sure that you are running this in a few cycles you have some average and you are getting a lot of data back. So this is really good tool or good framework if you want to run some benchmark. but usually in the algorithmic challenges you have something that does this for you in the background or you have like a simple class that you can just execute. So you don't really spend a lot of time trying to understand how the framework works if this is the right format or did I miss something. So in this case I think the simple

**[11:02]** Java class was enough. So you have this framework that you can run and run each time and then see what the effect is of your improvements. the first one you did was relate to exceptions. What's wrong with exceptions or what can I do wrong with exceptions?

**[11:21]** A lot of things that you can do wrong and I think there are like two interesting topics here. The first is that probably you should never use exception as a part of your business logic. So exception as probably it's you know you can figure out from the name is something that doesn't happen frequently right so it is something that you shouldn't be expecting or you know it may happen but it is not a part of your typical workflow or analysis so handling exceptions especially in Java is quite expensive because you need to build a stack you need to have all the information about the exception you need to report this it is being analyzed or catched by the runtime. So this is really a lot of overhead and this may be anything from a few times to 10 times more or probably more if you are optimizing something on your side. So I think the first approach was just to load a string and try to build in integer from it and you can do either parse int or you can build a new integer and then you can catch an exception and see if it's a number or not. So this is usually not a

**[12:36]** Good idea. So I think this was the first thing I removed just trying to validate you know if you have digits or if it's something that is not acceptable as a part of the integer and this is something that we see frequently also with our customers because and probably you may create another post podcast about how the JDM works but since Java is an has just in time compiler it is a really smart compiler that can remove some part of the code if it is being used. So quite frequently we see that if you have multiple branches you are checking if this is the value or statement do this if else else do this else do this and else if and you can check this. So typically what you want to do is to have the most frequently used result or you know statement in the beginning right so not checking for all the errors first and then in the last else do what you really want to do because it will be adding a lot of complexity on the just in time compiler level. So contrary to that you should be assuming that everything goes well and do this as a part of the first if and then you can check some

**[13:58]** Exceptions or you can check some exceptions first have a flag to see if there is something wrong and then handle this exception later in the else close. So that's something that we see frequently that is being optimized with applications that really care about performance and

**[14:19]** Making everything as fast as possible.

**[14:21]** I think I need to review all the code I've ever written because there are two things I'm definitely doing wrong. I use a lot of the integer parse integer and if it's an exception then do something else. So that probably that's already wrong. And what I also do a lot is first check errors in a value and then return something else. So your approach I actually like it because yeah you should go for the most logical first thing that can happen and return the result. But I probably also depends on yeah the use case. What kind of data are you expecting to come into the system? I guess with your the experiment that you're doing, yeah, you know the data, you can exactly optimize it for this use case.

**[15:17]** Yes. this is exactly the case. So, you know, sometimes you may divide your logic into two parts. first is just check for I would say the data if it's being correct and then you can have a handle error as a part of the second if right so sometimes yes you need to validate things before you start doing something but you probably don't need to handle all the issues immediately right so if there is no error no issue on the validation phase you just move move on and do what you really need to do so

**[15:53]** That that's one thing I just recalled one anecdote that may be interesting to people that are not really indepth in how the just in time compiler for Java works. So you know we've been doing some experiments and running some benchmarks within our team and then one person noticed that the improvement was not like 10% 20% but it was 10 times faster right so it went from I think in this case 300 milliseconds to 30 milliseconds so the improvement was really huge and that discussion that was about that was really interesting. ing because you know we were thinking about you know how the just in time compiler got so fast you know what has we have been missing before how we can optimize this to get this 10 times improvement in other cases but just in time compiler doesn't need to compile the full full code of your source code so this is why I said that if you have like multiple if statements it may only compile the first or the second one because this is something that is being used and it will put the trap on unreached in the missing if. So in this case what

**[17:12]** Happened after a few iterations just in time compiler noticed that the part that of the code is not really used anywhere else. So instead of optimizing this code it just removed the code at all. So we were not doing anything. So it was like a empty loop that was just spinning and all the logic of the application or of the method that we were trying to improve was removed because it was never used externally you know in the scope of this method. Right? So it was not 10 times faster. It is just something that was totally removed. So there was no logic. So, so be careful you know if you see that you can do magic or amazing improvements because maybe it is not something that you will see in production if the real users and the real traffic comes.

**[18:03]** So you already said yeah exceptions is not a good thing and then also a logical order of your ifs but you also mentioned that reg x is also not the most efficient way to do things like this correct? yes. So this was also very interesting because you know typically if you are experienced with regex this is something that you can easily copy paste between your application code and you know check for the if we have only digits or you know how many digits you have and you know you can see how long it is or you can validate if the structure of the input is really as expected And when I tried to validate the input by using regular expressions it was really the slowest result of all. So it was not even improving anything because you know I wanted to exclude the wrong input string strings in the beginning but the processing of the regular expression was a really adding a huge overhead on top of what we have before and this is I think something that we probably don't focus as much on because I expect that we are using or everybody is using regular

**[19:25]** Expressions s in the reprocessing of the data because that's really easy way to do this and if you think about that you may be using Python that is like adding 50x more energy requirements on your application and then you are using regular expression to just check the input every time you have something in your Python code. you know it may easily get you to like 100 times more energy than you really need for executing some small part of your application code right so consider how much more you need to do to save you know 10 times the energy that you are using so I think where we really should understand how much energy we are using or how much expensive it is to use some methods that may be easy to use, but really they are not optimized for being invoked like millions times per per minute or per second.

**[20:24]** Yeah. Yeah. Apparently, you had the most improvements with letting the CPU breathe. What do you mean by that? I

**[20:32]** I think this is something that may be easy to understand on the higher level. So the idea is that probably we should spend more time trying to understand what we really are trying to do. So in this case we had string as an input and the string may have some letters or characters that were not expected. Instead of using some regular expression for that, we can easily verify this with built-in functions like you know every character or the character object has the in digit method that you can use to check if that the given character is a digit or not. So this is something that you can use really quickly. It has almost no overhead. You know, it's checking everything without allocating too many objects. And this is something that you can adapt to your use cases, right? So I tried to remove all the exception handling that was using a lot of cycles outside of a typical execution path. then I removed this regular expression check that added a lot of complexity or a lot of resources that were required. So in the next step I had only simple

**[21:54]** Checks for each character a digit or not. And then after I checked all of them I was able to build a digit or build the number and then verify this against the criteria

**[22:06]** And at the end you had a much faster result than in the beginning when using the simplest way of implementing this like with exception handling. is this something we as developers should try to do more? Because yeah, you had this one specific challenge now. How can we take this into our ongoing developments? How can we be aware of how much energy, time, resources we are using and make our application as efficient as possible?

**[22:42]** Yeah, a very good question. So you know trying to get as fast as possible within this challenge. I even you know remove this is d check and wrote a simple function to check if my digit is correct by just you know assuming that I know how the input string looks like. So I can save some exception analysis in this case and it was like few% faster. So it's not something that will give you a huge improvement but it will also require a lot of work on your side. So you know on one side you are looking at the energy efficiency of the code that you are using but if you are then spending you know two days or two weeks trying to write some code that is probably a little bit faster but not used frequently you are wasting your time you are wasting you know energy that you are using to build this. So I think what really helps and again we are doing this a lot of times with our customers is trying to understand where the biggest bottlene bottleneck is because if you are improving small part of the code and even if you can make it 10 times faster it is

**[23:59]** Not something that will be 10 times faster if you look at the end to end pipeline or processing Right. So, what usually helps is using either some kind of APM tool that can give you some breakdown of the application logic or even using profilers. You know, there are built-in profilers within each JVM. I'm not sure if you use the Java flight recorder frequently, but you can tune it to really have very small overhead. if you are interested in some high level details and this can give you information where my JVM my application is spending most of my time and if you see that there is a method that is taking 30% of your time this is probably a good starting point because if you can save 20% of 30% it will be visible change but if you have a method that is only using like fraction of a percent if you make it 10 times faster nobody will notice right. So I think trying to understand what we are doing and what should be improved is the first step because you know as engineers we like to improve things and you may spend a lot of time trying to make something

**[25:21]** Really really fast but then nobody will be using that. So you know we were we are not helping the world in this case.

**[25:29]** And in your conclusion you really are highlighting the craftsmanship. So it's you have to know your code. You have to know how it behaves. You have to understand the monitoring tools to find exactly where the bottlenecks are like you said. So is this where a good developer can shine and prove his value?

**[25:52]** Yes. So I think you know trying to think a little bit outside the box when you are developing things really helps because we quite frequently only focus on just a small part of the problem and this is not something that is really a major problem or a major bottleneck for your organization. So from what I like to do and you know since I'm working with Azul we may have more challenges like that in our daily work because we have a lot of customers trying to squeeze as much performance as possible from the application from the JVM from the hardware. So this is really something that we spend a lot of time on. But if you are a developer that are building some typical application, you probably don't think about how it will scale, how many resources you will be using and for example, you know, if you are using an all these cloud native services, they are really great and but typically you have 1,000 of them, right? So it's not like enough to understand 10 of them and just using them all the time regardless if this is a good use case or not. But just be aware that you know there are

**[27:13]** 900 more options that you could choose and maybe one of them that you don't know about is something that can help you. And recently I had a discussion with my friends when they were using AWS lambdas. So this is something that you can use to scale to zero right. So if you have no users, no traffic, you can save resources because you are not using anything. And then when you have a first transaction, it will try to quickly create a new resource to process this transaction or request. And this is really idea if you really for example don't do anything for 8 hours at night because your users are sleeping and there is no traffic. but if you are using lambdas to process things that are always up and running, this is usually much more cost

**[28:08]** It is using much more energy than anything else. Right? So if you have your lambdas all the time active, you are if you are scaling up them according to load, then it is probably not something that will help you to save money. Even though you know on the marketing side, it is something that was designed to save resources and time and capacity. So I think we should understand how these tools that we have at our disposal can help us to make the right choice.

**[28:40]** Okay. so if people are interested in what you exactly solved with this challenge and your code so everything is in the article there are probably also links to the source code for that. yes so before you go into details you may also go and check the original challenge that Kier created. So you know this will help you to go through the same process and maybe try to find another way to solve this. So starting there will be probably a great place but then I have article on my LinkedIn page. We have article on the Azour blog. So you can go and check these articles to get a little bit more details source code changes and the way we took to understand and fix this challenge.

**[29:31]** Okay. And probably if people find a solution which is way better than yours, you're also interested in finding out.

**[29:37]** Yes. Yes. If you can make our 10 times faster solution even 10 times faster, then will be a great

**[29:44]** Challenge to have. Yes.

**[29:46]** My name is Turk. coming from Netherlands and I'm talking about sustainability and space.

**[29:53]** Oh, at the Java conference. Explain

**[29:55]** The sustainability part or the space part?

**[29:58]** Both. No, I have a passion in the space and I follow everything with NASA in it and I'm also a huge fan of sustainability. So I show the people how Kepler works.

**[30:11]** And Kepler is a tool to measure your energy consumption of your application.

**[30:17]** Yeah. and how can we make our codes more sustainable? Is that something that you as a developer can control or improve?

**[30:26]** Yes, definitely. But most people don't know that you can monitor monitor your application with Kepler or your Docker instance, Kubernetes instance. But there are also other ways to contribute to like performance. You can increase or your improve your performance and that also helps sustainability.

**[30:46]** Mhm. And sustainability. I always tell people who find it important but they cannot sell it to their manager.

**[30:54]** Yeah.

**[30:54]** Sustainability. If you make an application more sustainable, it's also less expensive.

**[31:00]** Correct.

**[31:01]** Yes, mostly. It depends.

**[31:04]** Yeah. Software.

**[31:05]** Yeah, indeed. You can also sell it like hey we are improving our build pipeline and then it also improves sustainability because you need less time and less development time. So it's also less money at the end

**[31:21]** And even at the end when running your application and you can handle more load

**[31:26]** With the same application again it's less expensive because yeah you can do run it on a smaller machine or handle more traffic on the same machine. Yeah, definitely. That's that is Yeah.

**[31:37]** How is NASA involved then in this story?

**[31:39]** NASA. Oh, yeah. I love NASA and we can connect the NASA data APIs and you can collect all kind of interesting data from asteroid information or photos from other planets from the telescope and that's just like yeah fun. It's it's an open AI and an open API.

**[32:04]** It's an open API. You need to generate your key and you can just call the API with your REST template or whatever.

**[32:10]** And yeah,

**[32:12]** From your talk, what is the most important takeaway?

**[32:15]** The most important takeaway is that we need to think about greener work like think about tooling to fix performance. think about sustainability behind you think about caching. If you making a call 1 million times and you only need it once, then you need to think like, hey, maybe I should cash it or something else. So that's the main takeaway. Think to greener your work.

**[32:43]** Mhm.

**[32:43]** Okay. I'm Ronald Hers. and what brings me to DevOps is all the nice people I meet each year again. like Patrick whom I'm just met from who presented about Spring and Maximleian from AWS are the people for me more than all the conferences itself.

**[33:06]** So the hallway track is probably more important indeed. Indeed.

**[33:11]** With JobRunner, you did some major announcements recently indeed.

**[33:15]** About being carbon aware.

**[33:18]** Yeah.

**[33:18]** Help me. Yeah. Yeah. No, it's quite a new concept. But what we now did with Job V8, which we launched in July, is that you can really easy do carbon aware job processing. What does that mean? Well, when you have a certain job that is not time critical, we make sure to plan it on the time on the moment where the least amount of carbon will be generated. and how do you do that? Well, you just schedule a job like before. You just say jobRunner dots schedule and then instead of an instant passing an instant, you say carbonaware dot from and then you give a time range and then within that time range, we search for the best moment and that's it.

**[34:05]** Like for instance, creating invoices after an order has been sent can be just shifted until

**[34:11]** Indeed

**[34:12]** The best possible time.

**[34:13]** Yeah, indeed. And will this also help reduce m costs like for instance spreading some of like invoices are all created the jobs at the same point but then executing all these jobs can this be spread over a time to also reduce the cost of a

**[34:31]** Yeah what we even in fact use behind the scenes is not the carbon footprint but the energy prices because the energy prices are easier to get here in Europe. and so even with doing that you will reduce your cost if you are hosting your data center yourself if you have your own data center.

**[34:57]** And the next thing we're going to look at is because currently this only works in the EU. we're now looking at the US but it's a bit more difficult because there are so many different data providers for electricity usage. and then afterwards we are going to go per cloud provider so AWS, Azure and Google and have a look whether we can for example in yes use spot instances where your pricing will reduce dramatically because there is extra computing power available.

**[35:31]** So finding all the best prices and times across the world is probably the biggest challenge in this.

**[35:37]** Yeah, it's definitely a big challenge. and not only that there was a the biggest challenge related to this was on how we process our recurring jobs because the recurring jobs before in job runner v7 and before we created a normal job like the moment right they had to run but then it was really hard to plan that efficiently with the for the carbon moment. so what we now do is schedule them ahead of time and that was really a tough cookie to crack.

**[36:12]** Okay.

**[36:13]** Scheduling the jobs are using time folds that other Bel Belgian company.

**[36:16]** No no no not yet but it could. So who knows there might be some collaboration a nice yeah yeah you are growing with job runner from a small team to a bigger team. How do you maintain

**[36:30]** Code readability and sharing knowledge about what's changing within a your code within a team, a growing team. Well, that's a good question and for me the tip there is to not grow too fast. So, we are now before we were with two developers, now we're with three developers. And yeah, by just on boarding one new developer, we can make sure that there is time to so he gets to know our standards, our

**[37:02]** Domain. Yeah. Yeah. Yeah. Yeah. so that's I think I like growing slow so that way everybody knows the coding standards. And

**[37:15]** What we also have is a couple of arunit rules to make sure that if someone something changes related to the architecture at least we have unit test failing related to that. And the other thing that we're now introducing is Eric to make sure that we all have the same

**[37:35]** Standards.

**[37:36]** My name is Yan Owens and I'm here for to give a talk but also attend talks and see what's happening in the community.

**[37:45]** Okay, let's start with the talk. What's the topic? The topic is what you can do as a developer to reduce the costs of your application but also the CO2 emissions of the application and do that in a way that's actually fun so that we want to do it.

**[38:03]** Okay. it's a discussion I had before if you want to be an eco-friendly developer

**[38:10]** You can ask for time at your manager because you will also reduce the cost and that's what you also mentioned. So is it really related?

**[38:18]** It's it's a proxy basically. So the idea the central idea of my talk is that if you can reduce what you spend on cloud services that's a proxy a standin for the CO2 emissions that your application has. So for example if you have three instances of some service running at the same time you have to pay for all three of them. but if you only need one, if you can make it so you only need one of them, well that's a reduction in cost because you're not using the services anymore.

**[38:50]** And is that because you write better codes or do you need to measure really how many instances you need? Is it a bit a combination of all these things? Well, measurement is always good, but the idea in my talk is that if you can't or don't want to measure, then if you are able to reduce these costs, then that's also a pretty good indicator that your program will be eco-friendlier simply because you're using less resources.

**[39:21]** And how you go about that? Well, there are some some things that you can do that are well, I try to go for the quick rinse,

**[39:28]** Like turning on build caching or making your Docker images smaller, that kind of things.

**[39:34]** Or even thinking about whether you need all of those nines of up times, right? Because you measure up time in nines, like 99%, 99.9, etc. But do you really need that? There are websites out there that are switched off for a couple of minutes every day to do maintenance and then they don't need to have a load balancer and failovers when they do maintenance. So those are things that you can think about that are quick wins I think

**[40:04]** And that can help make your application more eco-friendly but also cheaper to run and that's something that will make your boss happier in the long run I think.

**[40:15]** Yeah. Okay. how do you make your Docker for instance smaller? Is that by choosing a different runtime or by cleaning up your application and then dependencies?

**[40:24]** Well, all of those things really. so the first thing I do is I make a multi-stage build so you don't have build artifacts lying around. you can also think about using a minimal base image that saves a lot of room. you can use graph VM to build a single binary instead of having to ship the whole JDK. And the last step in my example is just shipping the binary and not shipping an operating system at all. And then

**[40:52]** For a hello world application that comes down to 16 megabytes which is quite small for a Docker image.

**[40:58]** So those are things that you can do and I actually had a lot of fun trying to trim down this application and making it as small as possible. So I hope that kind of fun will translate to other people as well.

**[41:10]** Mhm. That's the Docker side. And what can you do in the codes?

**[41:15]** In the code itself, I don't have that many examples at the moment, but a thing that you can do is think about what you need in your application, right? So for example, the big hype is to add AI chat bots in your tool.

**[41:33]** Do you really need that? well, if you do, then you should totally do it, but if you don't, then take it out. And the same goes for other things that sound cool, but you may not actually need. Maybe you serve advertisements in your application that are full videos. now those are pretty heavy as well. Maybe you can just use an image,

**[41:53]** Right? Those kinds of things, they all help.

**[41:55]** Yeah. as a developer are we responsible for guiding our company and the applications we built to take all these kinds of things into account?

**[42:06]** I think to some extent everybody has a responsibility but we as developers we understand how software works. so we can think about what's what is good and what is not good in terms of performance and CO2 emissions and once we find out that something that we do is actually not good then I do think it's our responsibility to discuss that and try to change it

**[42:32]** Because we are the ones who notice it so we are the ones who have to

**[42:37]** The power to change it

**[42:38]** We have the power to change it yes so I think we should at least try if you can.

**[42:43]** Mhm. what is the main the one thing people should remember from your talk?

**[42:49]** The one thing that you should remember I think is that cloud spend is a proxy for CO2 emissions. So if you can make your application cheaper to run in your cloud provider of choice,

**[43:02]** It will probably also emit less CO2 and therefore be more friendly for the environment. Mhm.

**[43:08]** If we only look at the costs, do you believe that running things in the cloud is the best solution

**[43:15]** Or should we focus more on our own servers, our own infrastructure?

**[43:21]** Yeah, that's a that's a difficult question because it seems like a good idea to run it on your own infrastructure, but then you lose a lot of you know the advantage of scale, right? an Amazon or possibility maybe.

**[43:37]** Yeah. An Amazon or an Azure, they are really specialized in this kind of thing and optimizing it from their end. and that's something that you can't achieve if you run your own server in your server closet somewhere in your building.

**[43:54]** So I think it has to come from both ways. but

**[43:58]** Our end of the equation is just as important.

**[44:00]** Yeah. Okay. You have some fun board with you. What is the goal of this? Well, the goal was to present my presentation on a Raspberry Pi because why use a powerful laptop to present some HTML slides if you can do it with a Raspberry Pi that's low energy

**[44:19]** With a few watts?

**[44:20]** With only a few watts. So, that would be great, but I just learned in the room that there is no screen in front of me. And I didn't bring an extra screen. So, I may have to do it from my laptop after all, which is a pity. So you will still burn a bit more.

**[44:35]** I will burn a little bit more carbon, but that will have the advantage that I will be able to give my talk. and hopefully that will save some carbon in the long run as well. So I hope it evens out.

**[44:49]** I like the idea that you just even try to do that and like we have another presenter here who arrives by bike. All the little efforts you do.

**[44:58]** I often came here by bike. It's actually quite easy from the train station. I have a Dutch OV chip card and I can just rent a bike for cheap and it works really well.

**[45:08]** Yeah,

**[45:09]** All these little efforts,

**[45:11]** All the small things, the small things add up. Yes.

**[45:14]** And that's a wrap for this episode of the Foojay podcast. A huge thank you to my guests for sharing their insights and you for listening. And remember, what's good for the planet is usually good for your budget, too. If you enjoyed this episode, please subscribe to the Foojay podcast on YouTube or in your favorite podcast app. You can find all our episodes, show notes, and links to the resources mentioned today on Foojay.io. And don't forget to follow friends of OpenJDK on social media for the latest news and updates from the Java community. Until next time, keep coding efficiently, keep learning, and remember, every line of code you optimize is a small step towards a more sustainable future. Thanks for listening.

**[46:03]** Give me the friends of OpenJDK.
