**[0:00]** Is your Java application actually secure or does it just look that way?

**[0:04]** Welcome to the Foojay podcast. All your news about OpenJDK.

**[0:10]** Recently, Steve Poole published two articles on Foojay related to security. The first one asks what happens when a framework reaches its end of life. The second one looks at why Java developers over trust what their AI coding assistant tells them about dependencies. Both articles point to the same uncomfortable truth. Your scanner shows green, your app looks fine,

**[0:34]** And the real problem sits somewhere the tooling never reaches. I invited Steve to talk about these articles together with Dave Welch, his colleague at EuroDevs. Dave is a member of a CV working groups and sees the vulnerability reporting process from the inside. Now, the timing also matters as Spring Boot 3.5 reaches end of open source support on June the 30th this year. That's less than two months from now,

**[1:01]** And when it crosses that line, it takes Spring Framework 6.2 and the entire Spring Security stack with it. If your teams run Spring Boot 3.5 in production, this episode gives you exactly what you need to make an informed decision. Steve and Dave, welcome to the Foojay podcast. Steve, as the articles are yours, let us start with you. Can you introduce yourself, please?

**[1:24]** Yes, I can. Thank you for that great introduction already. My name is Steve Poole. I am a dev advocate for HeroDevs. I've been there a few months, but you'll have seen me at IBM and Red Hat and even at Sonatype doing either JVM things, which was a long time in my past, but a lot now about security from a software supply chain and from a developer angle.

**[1:50]** And Dave?

**[1:51]** Yeah, hey. Thanks for having me. Great to be here. Steve Steve let me tag along, let's be honest. I'm I'm I'm I'm honored to talk to anybody from this space. yeah, I'm Dave Wolich, I'm 20 years in the industry, 12-13 years kind of Java evangelist, come from the camp, really love that. been off, you know, gallivanting in a bunch of other weird spaces and writing far less code than I want to for the number of years. So, it's fun. I got to go to JavaOne this year and meet some of my heroes and show up stuff like this and it's like great to get back into, you know, my mind, my background, like this is the gold standard of engineering community. So, yeah, I I've been with Hero Dev for years since before we had the NES, right? Never Ending Support Program.

**[2:30]** Joined, I knew the founder and he reached out kind of when it first started and I was wrapped up in a business and reached out a year later and told me a little bit about what he's doing and they were starting from the front end world and I'm like, "Oh, I've bought this strategically for years and no one has done it, you know, it's it's crazy still, even for where I'd be like, "Oh, we'll do EOL, just the EOL." And for everything open source. turns out no one else wants to do that and it's actually very bespoke and hard to do. And most maintainers are like, "Please take my old thing. Now my community doesn't yell at me cuz I can say, well, pay your debt and migrate or you can buy your way out." for a while. We still start every call by saying, "Hey, you should migrate. It's dead." but yeah, really good to be here, really good to talk about it. I am a coach here on the AWG, the automation working group for the CD program. I last 18 months I've been showing up more and more. I've spoken at VulnCon twice and started to go to some more of the podcasts there. So, really love the

**[3:25]** Vulnerability community. I went to the first VulnCon and realized, "Oh, I'm the problem." All right? For 18 years I never ran the update scripts and, you know, what's what's it in the Java camp where you refactor stuff and does it for you, like, not a silver bullet, but like would have been better? Yeah. I I'm the guy there. Not a lot of engineers show up there. So, vulnerability space and management space has a lot of opportunities to do good things. People can get involved. Love to make a quick plug of get involved if you want to do some open source work like this current place. And yeah, talk about whatever you guys want to talk about security-wise. So.

**[3:59]** Great. Steve, you used the phrase zombie dependencies in one of the articles. So, it's software that looks like it's alive in the scanner, but it's functionally dead if you look at to it from a security standpoint. What do you mean with the zombie dependencies?

**[4:17]** Okay. So, I was trying to find a term to describe this. And I went to my favorite AI and I went, "Here's the thing, how would you describe describe it?" And it said, "Oh, it sounds like something from The Witcher." And I'm like, "Okay." So, I went down that rabbit hole. The thing here is the whole end EOL story is what used to happen was when a version of something that you use was went stable, when end of life, people stopped fixing it. It just went stable. It was dead, right? But it was still alive, but it was dead. You didn't touch it. The problem now is that those things can often accumulate vulnerabilities. So, it's not dead. It's more like it's come back to life in a particular nasty way. So, I'm going a zombie dependency is one where you think it's safely dead and everything's okay, but actually it's got a life of its own and it's it is going to come and bite you.

**[5:18]** So, there you go. Weird weird analogy. That's why I did that. It's just I don't know how else I honestly find talking about end of life as a very weird way of describing about it. And so, I was just trying to find other ways to describe it to developers.

**[5:35]** Mhm. Dave, you're on the CVE site. a lot of talks about it lately because yeah, you have these AI models who are now very fast finding a lot of CVEs. What is a CV? What is the basic description of it?

**[5:53]** So, you're going to laugh because it's 25 years old and we battle about this every day still. So, so the mission statement, if you read it, and by the way, I asked this question to like all the people who were like in every meeting, you know, whenever we're on a phone call or one of the panels, and they all give a different answer and it's it's always surprising who picks what lane. The definition, the genesis, all the things that came from it is, you know, is an identifier to talk about a common vulnerability or I forget now if it's exposure or exploit or it changed somewhere along the way. So, it's murky. You get on the website. But it's the it's the thing that is a problem, which is very ethereal and it's changed over time.

**[6:35]** So, you it's not an advertising board, right? Obviously, we're not out there plugging our stuff to try and get get ads. Please don't do that. Clogs it up. It's also it's not technically the notification system that everyone should hang on their hat to like respond and or patch, right? There's incident response and then there's like security build, right? Like a build and patch maintenance, right? it's not that, but it effectively is that and the paradigm is shifting to support that. it it's also an intermediary space. So, it's like it is the global vulnerability identification program. That's what it said it is and grew out of it and it's the oldest one and there's others coming along and admitting there's space for those. In essence, the CVE is the ID, right? And there are different CNAs, numbering authorities, who say, "Hey, this is the part of the yard.

**[7:22]** We own this or we do this, right? So, you know, Microsoft is a CNA and they have their whole program and they do Microsoft stuff. you know, Red Hat, obviously, GitHub for anybody who has GitHub open source projects, they're a CNA. But as a maintainer, you also can write or like a vendor or supplier, right? You can also become it for your own. What's interesting though is when we showed up on the scene, everybody got really flustered because there's a lot of bad actors historically or people trying to do that stuff and also if we find a CVE in something old and you don't support it, you know, that you run, you're still going to get phone calls about it, which is a major problem. The whole reason EOL exists is to cut down on that.

**[7:58]** Yeah.

**[7:59]** We we've had to kind of carve the niche for it to say, "Hey, because we don't we don't make anything new." It's a really weird company, right? Like all we do is take old things that are no one wants to do and say, "Hey, we'll help you out if there's enough demand." the you know, the space that we occupy has has opened that up to say, "Hey, like it's these people until it's these people where you have vendor overlap those kind of things." So, sorry. Getting into all of it, it's the vulnerability it's the vulnerability program for identifying things. It's the group of people who are authorized to publish with some sort of rigor and whatnot and, you know, it's a big bureaucracy and people sling a lot of mud about it and there's lots of things that need to improve.

**[8:41]** Everybody involved will also tell you that. It's also a 25-year-old program globally with, you know, no no real ownership because everyone has to participate and it's got a little weird ownership in the US government, but it's really a global program and so, but I will say it's it's it's one of the coolest programs and communities I've ever been in because they will welcome anyone in because we're all there for security and so, yeah.

**[9:08]** So, I that's interesting cuz the way you answered that the question was you talked about the process and the back thing and when you say CVE to a developer, they have a completely different view of what it is.

**[9:22]** So, that's actually so, coming back I missed my flight. I was on a panel with, you know, several awesome people from like GitHub and minor and like art like the guy who writes the rules like he's like CVE incarnate of the program. and it's funny because we ended up like debating and I missed my flight twice after to stay and took the very like I got the last cab out of town to the flight last flight back to back home. but I'm so glad I stayed cuz we were arguing about the definition of a component, right? So a package, right? A version of it being vulnerable. And there's this nexus of three camps of there's the vuln people, right? I went to VulnCon last year and was like, oh, I found the people like these are the analysts, these are the responders, these are the aggregators, right? These are some of the tool makers and stuff, but like they're the analysis side of taking in the feedback and kind of triaging and getting it somewhere.

**[10:13]** There's the build side, right? Where I come from where we're the problem and also like you got to get us to fix it to stop shipping the bad product. And then there's the incident response where it's on fire out in the server. And none of us actually agree on the definition of A, what a vulnerability is, that's a long known one that we all laugh about, but B, what it means for a component version to be vulnerable. For instance, if it comes in through a supply chain attack or a malware, right? Like it isn't necessarily considered vulnerable. And as an engineer, I'm was shaking people, right? Like this package we all agree has bad stuff in it and if you don't send that signal to me, I will just ship my product all day, no problem.

**[11:04]** And some of that's finally coming out and there's a lot of evolution coming. The not to plug anyone, but the mythos hype is real. That's another reason I went down there this year is to find people who are hands on keyboard and I'm pretty sure they're all sequestered in a hotel like a jury. but the people sitting downstream from them on every front that I've talked to, you know, one one guy from a very big scanner company said he's like, I find I've got more credible CVEs with more depth to them in the last 3 months than I've gotten in the last 5 years.

**[11:33]** Yeah.

**[11:34]** And we're we're we're just at the tip of the iceberg on it, so.

**[11:38]** All because of this Me Too thing?

**[11:41]** Not just because of it, right? And then and Codex has come along and doing stuff, too. The AI capability and this is actually the scary part. The AI capability, you know, this is an analogy we kind of came up with while we're going to use it and you know, it's a little rough, so pardon me, but it's like we have this giant wall of all this stuff and it's it's duct tape, right? Like there's holes everywhere, right? And our flashlights have gotten a little better over time and the attackers have their flashlight and we have our defensive thing. We're trying to find where the holes are and like plug them first. Sometimes they get ahead and then we find out we got to respond or whatever, but our flashlights are pretty similar, right?

**[12:14]** The problem is even if we get a bigger flashlight for everything first, you know, everyone's just worried about cataloging it, right? And that's a huge problem, right? We're we're not ready for that scale. We don't invest in it. It's not a revenue driving thing for most companies and so like we're not aligned. But even if they're 2 weeks or 2 months behind us, like you can't keep the genie in the bottle and suddenly when they can find all the holes at once, like not only does the risk go up really fast, but even if they we start tracking it and we know that, you know, known exploits and vulnerabilities, right? It have file have list. Like if you started tracking those even with great omniscience, right? That are actually being used, think of like Heartbleed, right?

**[12:53]** We responded. That's a great example of a vendor come together, coordinate vulnerability disclosure. All this stuff happened, but we're not really ready for a Heartbleed twice a day, right? Some of those organizations, a lot of a lot of our customers deploy quarterly at most. So we have to be that far ahead and sure they'll hot patch fix, but like that's the one exception when a Heartbleed happens.

**[13:15]** Yeah.

**[13:16]** No one is ready to deploy from our world that much, let alone even for a have everything turned over every day, so.

**[13:22]** Yeah.

**[13:23]** Sorry, Steve.

**[13:24]** Yeah, I was going to say it's even worse than you describe,

**[13:26]** Oh, no.

**[13:27]** Okay, so if I take your analogy [laughter] So if I take your analogy, I love your analogy. So in your in if you put the bad guys on one side of this duct tape and you put you us on the other side what happens is you shine a light through and you show a hole to them. But when they shine a light through looking for holes, you don't see this because they do not report their the things they find.

**[13:52]** Mhm.

**[13:53]** So we're whatever we do to discover them, we then make them public available. So we've now armed the bad guys. We said, "Hey, there's a vulnerability." Whereas everything they discover they don't report.

**[14:05]** Mhm.

**[14:06]** And yeah.

**[14:08]** And the fact that we can find there's another dimension to the fact that we can either both say like when I say we can all find it, right? We don't have the compute that we're actually scanning it all. It's just doesn't matter where you put the spotlight is up and it's just where you're looking.

**[14:22]** Mhm.

**[14:23]** And yeah, that's the problem is if we don't look over there and they know there's a CPanel, right? Just had a big old vulnerability and I started out as a PHP like LAMP stack guy back in college, right? Like my experimental days. thinking of all of CPanel broken and then all the stuff behind it terrifies me, right? Because like PHP injection attacks are so much harder to find. Lots of systems don't have like get history and you know it SDLC stuff around it, right? And so it's like it's just I'm just getting scared literally thinking of all the things that I could do and I'm not a like security and attack and all that kind of stuff. but the other dimension that you said there real quick, it's the new types of attacks. So one of the one of the biggest companies in the world are new to CNAs and I got to meet their analysts and like I asked them a simple question and they just like it's like Marvel people talking about the movie they're like, "I don't know what's in it. I was filmed and I now have forgotten it, right? Like I can't tell you anything." There's no Tom

**[15:20]** Holland I found yet. But these guys were close enough that I could at least ask a question like, "Hey, has the velocity changed? Has the impact changed?" Like, up or down maybe velocity, quality of report. And they were like shocked because they were like like you could see almost like A, they said there's a before and after event from doing this for everyone once it goes broad. And B, the types of vulnerabilities or attack chains now are much more sophisticated because even if you get like the best black hat guy, right? And you know, an attack chain is like this three things might not be a vulnerability, but I got a breach. I exploited these three things that are pretty common and now I got root, right?

**[16:00]** I privilege escalate or whatever. you can't mentally keep context on that once you start to get certain complexities and every environment's a little bit different. AI and tools are being trained to do that breach and try the all of the attack chain patterns and find whichever one works bespoke in that environment. And like we don't have a way even even cataloging these things if we had like the CVE for attack chains, like we can't catalog every permutation and see it. So, it's just it's the world is changing and defenders will catch up, right? But the velocity of the change in a condensed window is potentially what makes this different from any other kind of technical overhaul. Okay, sorry. I'm really passionate about this and I love that you're you're putting this out to

**[16:50]** [laughter]

**[16:50]** You know, will reach out to me and tell me where I'm wrong. So, I'm really excited.

**[16:53]** I know the CVEs because I do the release notes for Azul. So, every 3 months we have if we just look at Java itself, so every 3 months we have a new release of the still supported Java versions. So, there's a list of CVEs with the release notes being published. I myself as doing the release notes, I only get this list the moment it is released by Oracle. So, we're following Oracle there. They are in the lead for this list of Java CVEs. But, even the moment they are released, I only get a number, a score between 0 and 10, the severity, and some basic info. And even at that time, it's still not all released yet. So, there is there's really good flow of how these CVEs are collected and who knows about them. So, only a few people in Azul are involved and in other Java distributors. So, this is really a cooperation within the industry where everyone tries to take responsibility.

**[17:57]** Yeah, so the term that's used pretty often for that, I think, right? Two terms are One is embargo, right? So, when they give it to you, you're now part of the embargo. And the PR has gone out from Oracle or it's now the trusted group with you being ready, but it hasn't gone live to the website yet, right? Is that

**[18:15]** Yeah, it as soon as they publish it on the website, I get the list and we push it on the website of Azul.

**[18:21]** Gone gone. So, that's that leaves embargo at that point. And you

**[18:24]** Yeah, yeah, yeah, yeah, yeah.

**[18:25]** So, you are now zero-dayed.

**[18:27]** Mhm.

**[18:27]** In theory, like have you built your fixes in this scenario?

**[18:30]** Yeah.

**[18:31]** Sorry. Just going to play it. Okay. So, you built your fixes. So, you guys did coordinated vulnerability disclosure, CVD, right? Can I ask you, do you know that process, what that looks like between those two groups?

**[18:42]** No, I'm really the last step. So, the magic already happened. So, the people who are involved in fixing it already did and tested already the builds. So, the builds are ready to go to the customers and to the users of the runtime. So, it's really this we know this vulnerability, we already fixed it, and now we put a fixed version in the open. And then I publish the release notes together with my colleagues.

**[19:06]** Don't know it, which is good.

**[19:07]** But, yeah, no, no. It's really the last step, yeah.

**[19:10]** The devs and the experts on it. And Java's is such a good example like JVM, right? Because you've got yours, there's other JVMs out there, there's other operating system like that's where CVD really needs to happen. And I asked the question because I come from the engineering background. I'm not even a security guy. Like, you know, like I'm I'm a very different lens in this space that I've lived in the last few years. but I'm terrified because it's not you said the magic's happened and that's how I view it and I think most people view it. And especially when I look like it's magic that we did OpenSSL and Heartbleed and all those, you know, things that we did that affected everybody like that where we got 98% of the inoculation by five players coming together for all the cloud stuff, right?

**[19:49]** That's amazing to me. I don't know the full process, but I did I actually when I was at I'm so glad I missed my flights. I happened to catch a cab with Chris Chris Gibson, the CEO of first.org who puts on VulnCon but a million other conferences and it's literally the forum of incident in response security teams. Sorry I'm bad with the acronym. it's a great domain though, right? I asked him this question and I'm like, "Hey, how do you all like where's the marketplace that you all do this so you can find each other?" And no one yell at Chris if this is wrong cuz we were talking very loosely, but he kind of said he's like, "Yeah, it's the kind of the old dogs world where you have the contacts like right? Steve at JavaOne was so great to hang out with. Steve knows everyone, introduced me to like some of my heroes, fantastic.

**[20:36]** I Steve is who I go to if I'm like, "Yo, I need someone at Oracle cuz I just found a problem." Like he's probably the first person I'll call. But Chris laid it out. He's like, "No, there are many documented cases where Steve finds the thing and he only knows his three friends and two of those know four friends so they you can run the chain but your friend at Oracle now moved. And maybe it was messy because, you know, big companies have stuff. Sorry, using Oracle as an example here. and now you're stuck just going through the C cert or P cert or reporting process. And the is crazy. So like if you've got the CEO's phone number or the CTO, like I have messaged CTOs and stuff before and been like, "Hey, this is your notification that's going to go in the deposition. You have this problem.

**[21:17]** Call us. Call someone. It's a free fix. Just talk to us, right? there's not the connective tissue that I once thought there was.

**[21:24]** Mhm.

**[21:25]** And there's actually some really interesting efforts coming together for people to try and solve that problem. So,

**[21:29]** Yeah.

**[21:30]** What are you what are you guys' views on that? I mean, you you've been around longer. You're in much better camps than I. Should I be as scared? Is it Is it really that [laughter] bad out there or are there more people talking than I realize?

**[21:40]** Steve, looking at you.

**[21:43]** The there's no easy way to say this. I'm not going to say people don't care, but people's understanding of this whole process, the what a CVE is, how fixes work, the whole thing, is

**[22:05]** [clears throat]

**[22:05]** I don't even want to say it's a fairy tale because it's not a fairy tale, but it's a fairy tale in the sense that everybody expects thing magic to happen. And if you look around and you go, "Who cares about these things?" Well, you've got the security team. And they're they go, "Oh, you've got a compliance failure." How are you Why do you have a compliance failure? Well, you've got a compliance failure because this tool that we use has spotted that you're using some component that's got a vulnerability in it. Nobody ever says things like, "How good the scanning tool? You know, does it find everything?" and then how does the scanning tool know that you've got a vulnerability? Well, it goes to the CVE database. And it's like, did it go at the beginning? How does it keep up to date? Because CVEs again, people think a CVE is a thing. It's a hap- It's a It's event. It's happened.

**[22:51]** All possible places the CVE could occur have been recorded. It's all done. But it's not like, you know, so that One of the articles I wrote was this whole how it evolves over time. And if your scanner if your scanner picks it up the first time, it might figure out that you've got this for this it affects version A. But then the people responsible fixing it realize it's applicable to version B. So they come back and update the CVE. And if the scanning tool hasn't caught that, that scanning tool won't tell you that your version B has these things. So this there's a cloud of uncertainty around with these things. Yet developers assume and security teams assume that it's a very hard crisp process and it isn't.

**[23:34]** I mean you could you it just devolves very very badly. because it like like software gets forked. So like the Tomcat article I wrote, the reason I wrote about the 8.5 Tomcat thing cuz it's a wonderful example A of how this CVE evolves over time and information is added and people recorded. It's an example of just how badly we can recall record information so that machines can't read them. But there's no concept in here about well if you fork Apache Tomcat 8.5 and you have your own version, you're still vulnerable. The bad guys will find you. But you don't realize you have a vulnerability because you're using something that somebody forked. Who's responsible for making sure that the forked version has the CVE? Right? It's this horrible process and the reason that we've got away with it for all these years is because it doesn't get exercised very often. We don't have well up until now very rarely do we have big major vulnerabilities. And when it does happen, it makes the press and we all do deal with it.

**[24:40]** That we know of.

**[24:41]** That we know of. Yes. Yes.

**[24:43]** You know, conspiracy theory someone's had you know what they call quantum, right? Someone's had quantum forever so they're just reading all the traffic. They just didn't tell anyone, right? Same thing. Someone's had mythos for four years and it's just like yeah, I got the skeleton key guys. Come on.

**[24:58]** Okay, let's let's not do the conspiracy theory level.

**[25:01]** That's why I lose sleep. I know it's not the case, [clears throat] but you said a couple of interesting things there and like so first off like the agreement of it, yeah. There's this idea two and a half years of doing NES and you know, we started in JavaScript, right? And by the way, this is a really hard thing to do because our packages have to look the same and function the same so that it's not impossible or blows up your dependency graph. But they're also not the original things that they were, so we have to figure out how to find the balance and it used to be way harder. It's getting a lot better of I'm giving you this thing to pretend to be this thing, but in your S-bomb needs to be this other thing and your scanner can figure that out.

**[25:38]** And my first CV experience really like the reason I got involved with the program, I showed up to the first meeting was just like I waited till the end, introduced myself, was like, "Hey, I got a dumb question. Like I've got this registry, and I'm trying to give stuff to people, and I'm trying to figure out the right way to like turn off their scanner alerts or give them feedback. Like, how do you guys name things?" And the whole group like put their head down, right? Like identifying the vendor thing of it like Pearl or, you know, Omnivore, if you know that, like some of these things. And they're like, "Yeah, we all fight about it. It's it's it's madness." So I'm like, "Oh, this is These are hard problems to actually solve." And they still are. but it's not Yeah, we don't we don't have a crisp starting place, right? The defect, the vulnerability that we talk about is a memory overflow, right? Well, is that the line of code? Cuz that line of code's in everything that has it. So, no, it's kind of this product. But if I fork this product or I include this product or I, you know, copy and paste

**[26:28]** Insert it, like is it me? Is it them? And there's some good rules around that, but they get murky fast. And so, there's there's there's a lot of times we break the rules. And this is one thing that's great about CV program. I showed up with all sorts of problems because of EOL, and I'm like, "The rule says I got to get that CNA to update their thing, but they say no." And technically, it's EOL, so they don't touch it. So technically, it's in my yard, but I got to file a new CVE for the same old problem which breaks the rules. And like credit to like the guy Art that I mentioned and Lisa from Microsoft and some of these people, they were all like, "Yeah, there's not really a good option today, so break the rule and we'll change the rules." And many rules have changed since I got there based on my point of view. So like the program again, program works.

**[27:12]** The problem is hard. And Steve, what you just described, and I don't think enough developers know about this because we wash our hands a little bit of like oh, it's too much work or my boss won't let me do it or whatever. We started top-down. This thing has this vulnerability and it works especially those bigger cases, but even like you can kind of eyeball whether it affects you or not, whatever. Top-down, they broadcast. Now you get the problem of everything goes off, right? Sneak, Black Duck, Sonar, like any of these things you run, you know if you turn it on especially on an old project, like it's just going to light up like Christmas and half of it doesn't apply. So we already just declare issue bankruptcy, right? And like give up, right? Or we just snooze it all, anything new, we'll we'll go with that. That's terrible.

**[27:54]** Well, bottom-up is now getting solved. So if you haven't heard of Vex, right? Vulnerability Exchange Format, right? Vex files are meant to be the local comparison, right? So we issue CVEs for the broad of these things are affected. We have our own forks of them or maybe a customer's running in a certain environment as part of a bigger product, you know, the local the product developer, not the library developer, can say, "Oh, we actually don't have that because our security model puts that behind the network boundary or whatever else." Now I don't have to manually exclude everything and like life gets sane from an operator and a builder to go back to normal. But that's still very new. A lot of people don't know it. So like we all have to be engaged. And then that's the reason why you said people don't care and I think that's a symptom. I don't think that's the actual truth. The problem is that twofold.

**[28:42]** One, the visibility isn't there. So we don't see again that delta of all of us can't even agree with the volume people and the IR people and the engineer, we all had a different definition of whether something qualifies for a vulnerability even though I'm like, "Dude, the poison is in the package. You can't tell me it's not." And they're like, "No, under the rules here it's not. It's got to It's got to go a different way." the visibility isn't there in connected enough and that's actually a second degree concern of the money's not there. Nothing like open source is about building and maintainers we build, we don't want to maintain anyways, right? Like we just want new stuff. But we'll do it if we can do it actively.

**[29:20]** We're one of the few like we got involved in a lot of the same board and they're like, "Hey, you're like the greatest CNA. You show up to everything." I'm like, "We filed six CVs. What are you talking about?" They're like, "Yeah, but you guys care like you're moving the program." And it's like, "Oh, yeah, cuz we're blind and we see it." And every time a CV is found it is good for us, right? Like we're not we're not out trying to scare people, but like the phones ring when someone finds something even if it's not in our camp, right?

**[29:42]** It needs to be fixed, yeah.

**[29:44]** We've got to work towards the middle.

**[29:46]** Yeah, I don't want to say they don't care as in I just don't care. They don't care because it's not in their list of things to care about.

**[29:55]** Yeah.

**[29:55]** And also cuz well, because a lot of developers are going to say what we use is restricted. If we want to upgrade it, we have to go and get up, you know, we have to get corporate agreements and things like that. But mostly they're going it's the security team's job to tell us job to tell us if there's a problem and then we'll do something about it. What thought when I the way I think about this is that I want developers to be making better choices with the first time.

**[30:25]** Mhm.

**[30:25]** And so I want them to say when they choose a component they put some more thought into has this thing got vulnerabilities, right? Sonatype had this great statistic which basically said 95% of the time when you choose a component there's a safer conversion of that component available to you. But because you tend to choose one based on what an AI or the internet tells you, you don't go looking for the next version.

**[30:52]** Yeah, because it's not your problem. Yeah, and also you're not We need to figure out how to get people to be invested in this problem, right? And it's a really hard thing because ultimately, given what we've just been saying here, ultimately the only way that we can keep people safe is that everybody has to be as close to the safest level of software possible all the time. And you the amount of engineering is going to take us to get there because we've got to get to the point where when a vulnerability turns up, it's fixed and it's applied. And you know, we're asking mean shots. Yeah.

**[31:30]** Automated and near real-time remediation is something that is now possible. There's even groups doing really interesting things out there that they're not talking about yet, but DM me and I'll I'll share some facts.

**[31:42]** As a Java developer, is it two sides? I need to keep my Java runtime up to date? That's one thing.

**[31:49]** Yeah.

**[31:50]** And or do I need to keep updating all my dependencies to make sure I'm on all the latest versions of everything?

**[31:58]** No, and your operating system.

**[31:59]** Yeah, and your operating system.

**[32:01]** Yeah, but like if I look at it as an org, right? Like I've been a CTO several times at least, right? And advised many, right? Like teams of 500 to 5,000, right? you like my app engineers, my spring developers, right? Like that's the camp I came from, right? They own their product in, right? Whether it's an API or a full product or whatever else, right? And that means their dependency graph, they should know what's in the box and they need to be able to do it in a way that's feasible. And that's where we've not been for a long time and the industry's changing. up. I'll come back to that in a second. The JVM is an interesting one. If you your guy like me is building it and then your guy like me is also running it in Kubernetes or whatever, you have a separation of concerns problem. You're either tiny or that's a different skill set. That guy should be able to keep you on the latest Java 17.

**[32:55]** I need to keep an eye out if Java 17 goes end of life, so that when he's like, "Hey, it's dead and there's a vulnerability." I'm not like, "Oh." Again, I left Java like eight-ish, so now you have to actually pick your versions. I don't know what happened. Guys, it worked everywhere until it doesn't now, you know, it's a little little more thought goes into it, but now I got to now I got to move my stuff, right? And but the lowering of the barrier is really the first thing and there's a group doing it. So team and Steve, you said something. Security teams are, you know, it's the security team's problem. Guys like me, totally do that. If they don't show up, I'm good.

**[33:30]** If the scanner that I use that's totally blind on data, right, or best efforts doesn't light up, I don't do it. That my problem is my engineering manager, my risk team, my CTO. They're like, "Go drive value, not risk management if there's not a red flag." And the security team's problem is not to find that, right? It I think this is fair. Security teams, please find me and yell at me if this is wrong, but the security team's problem, they don't know your product like you, the Java guy, know your product. But they know the tools in the ecosystem and they build the culture of it. And yes, they show up and club you when you're messing up cuz you have an accountability chain. But they have to build that and someone has to advocate for that and it doesn't come out of revenue in most places until you get burned. And I'd rather save the $100 million and spend a million every year for the next 10 years than have to pay out, right?

**[34:19]** And the there's a there's actually a really interesting phenomenon going on. So, 2027 is the year of compliance regimes. 2026 is the year of data, right? If you go anywhere, you talk to anyone, you meet Bob Lord who came out of the CISA camp and does like secure by design and all that. He's like, quality, quality, quality. We need that quality. So, that's really the pre-steps so we can find the problems. Next, we need the tools to be able to find those things. And yeah, even if we get those, there's enough free tools out there. No one has an excuse to not be able to scan your project. But no one has the incentive to also do it. Well, I got bad news. A, compliance regimes on the rise. CRA coming out of the EU is the first. The US has its own coming. and there's several others from actual like regulatory and or security frameworks. SOC 2 going to get an update I'm positive, right? Like or you know, and FedRAMP and all those kind of things, right? But also, there's a new player in town. And this is the one that everybody should be afraid of. And this is how you go get Risk Incorporated to

**[35:14]** Take you seriously is the insurance groups, the insurers. They are aware and they're so good at the actuary stuff, right? Where they like, oh, like you go running on Tuesdays, you're 7% more likely to die, right? If you're at this altitude or whatever. So, cybersecurity's always been kind of a joke in insurance land, right? Like you get big umbrella policies cuz no one knows how to do it. We don't know how to do it. They don't know how to do it. Like it's it's guesswork of just when you get struck you have a policy. Well, now the tools exist. And now the capability is there that anybody should be able to go to chat GPT on an incognito tab and be like, I have my Java Spring app. Here's my effective palm.

**[35:53]** What tools make me safe in my free GitHub action? It will be negligent to not do that and legal will catch up with that slow. But the insurers have already caught wind and I've talked to a few groups that are doing this where they're saying, oh, well, you know, I mean, sure you run a construction company with one Windows computer like you're probably not going to get that. But anybody else who has any sort of data, they're going to oh, your time for your cyber renewal, right? Yeah, that's okay. So, you actually have to engage a firm. You can take that out of the price when we're done or whatever, but they're going to check you out. And if you don't have the minimum those, we'll call it out. And the first year it's only a 100 times increase on your premium for cyber.

**[36:31]** Next year it's a 100,000%. And those numbers have actually been said. And honestly, it terrifies me as someone who has started businesses. And I know that we have to sign it. And I have commercial guarantees that I have to keep a certain dollar amount. So, I can't just like, well, I'll skim down and, you know, pay the fee. No, I'm stuck at a floor where I'm at for here and ever forward if you're any sort of corporate company. So, 100,000% is a very scary number and very, very easy to then say, "How about we just do the thing?" They'll take it out of the cost of engaging a group that gives you the bill of health. Plenty of experts are available. New companies are coming up to help be third parties alike.

**[37:11]** See, if you got to mention this earlier, like, there's the click wrap, right? Like, oh, I have to ship and be secure. I put sonar on the repo. It runs every night. All the rules are turned off. Right? Or I have, you know, code coverage. I have JUnit tests. They all say console log, but there's no asserts, but like, now I have test coverage. That day is ending. And as much as legal will do it, finance is going to kill us. Risk is going to kill us with insurance. So.

**[37:36]** And it's not just updating the libraries. So, what you were talk writing about Stevies, if you are actually have built something which on something which comes end of life, like certain spring versions, yeah, then you're stuck. There are no updates coming anymore. So, should we foresee an amount of time as a developer for updating and doing our responsibility?

**[38:04]** Where are we going with this? Because we should be updating, right? But we don't update because updating is risky. And the business says, "Well, if you got to choose between updating to get the next version of something versus shipping some features, ship the features Because it's it's value. And so, you keep applying that and eventually you'll find yourself with a very large dependency tree, which is end of life. And I said at the beginning, that was okay because these things were seen to be safe. But now when you start seeing these things are no longer safe, and possibly they might not be They might be safe today, but tomorrow major one turns up. You've got to be able to update. and as they were saying about the insurance thing, it just says to me that however hard it has been, we have to move to the point where we're as current as we can be because we need to be.

**[39:02]** And I'm hoping [sighs] I'm hopeful that will put pressure on us who develop these APIs and these tools to get better at making sure things are more up-a-compatible so then the cost of upgrade upgrading is less horrific. You know, cuz the other stat that I had from Sonatype, we did the and there's a third-party survey study that Coral did was that when you look at the semantic versioning information, you know, so you have major, minor, and dot release. The theory is that you should be able to upgrade something to a dot release and just get a bug fix. And if you upgrade the minor thing, you get a new feature. But if you upgrade the major version, you get to get a breakage. It turns out that it doesn't matter which one of those they change, the likelihood of you getting a breaking change is like one in three.

**[39:55]** Right? Well, it just things break because people don't realize. But again, it's because we've not been had enough incentive to do that. And developers make mistakes and they want to you know, just it's easier to break you, Mr. Customer. That word has to change. We have to get better at signaling when we are breaking things so people can do the right due diligence at the right time. But we have to get better at updating so that it's just regular, doesn't cause you any problems, the risk is low, and it's just life.

**[40:26]** Mhm. It's it's also indefensible to not get better because it's economical. If you're not using Now, I'm not like an AI fanboy, right? Like And actually in February in January in our company if one of my engineers showed up and said like, "Oh, I wrote three features this sprint and I didn't touch a line of code. I just had Claude do it all." I would have been like, "Hey, A, you have to read every line." Like that's normally what we do. We still do. But B, like we need to have a talk cuz if I find anything like this, like you're banned from AI for a while, and that sucks. February the capability revolution happened, and now I'm like kind of the opposite of like it writes better code than you, me, or anybody else, and I used to be good at coding at least, but you still have to know how to tell it what to do, right? You can't cognitively offload it. You have to instruct it just like you would a team that you're guiding through doing pair programming, right? There's still a level of that. And that will still improve and everything else, but it's it's not there. but guess what?

**[41:24]** Even if you just have it right exactly like if you skeleton code everything it does, it will do the thing that you don't because it will put documentation in, right? I don't write Java doc. Everybody knows Java The method defines itself. Read the code, right? well, I can get docs for free now, right? Like it's marginal. Mhm. The testing Yeah, guess what? If you don't check your tests, it will just happy path you. We're back to code coverage. But it at least put the scaffolding there and the happy path. And you can't So the goal for me like I think of Dave at the various stages of my career. Goal number one is Dave actually has the tools that it's harder to be reckless than just do the thing. They're economical, they're available, and they have signal on it, right? Cuz most days will do it if it's not bad. But you know, but then we get into like you get into the version thing.

**[42:13]** Semver, by the way, not the same on all the projects, not the same in all the ecosystems, right? Some put new features in, some don't on a patch version, right? Some break stuff all over. Some have four dots. My regex in my data drives me crazy. Thank you. Sorry, Ruby guys. it's nice. I got to be honest. Four would probably be better if everyone did it, but it's not the same for everybody. so you have to work around all those things as we're solving this as a community. But visibility drives the regulation and the change to be possible, right? Visibility at that level. Dave is the problem, right? Dave now has the visibility and signal. I can say, "Sure, I'll only work on product stuff, but the list of red goes longer, and now vulnerability manager EM, right?

**[42:56]** Engineering manager, when the deposition comes of like, "How do we lose everything?" You got to testify. They're going to come to me and say I'm reckless, and I'm going to say, "Nope, you over prioritized." So, you better have the receipts that it goes up to the CTO. CTO is usually the one that you go yell at, and the C they, you know, they used to be able to say, "Well, I can't see that far down." And the guy is like, he gets lost in the mix. That's where the insurance companies are now, like, "No, there's liability. We can have that, right? We can get personal liability." Like, it's going to be a bloodbath for a while, and honestly, we deserve it, right? Like, if you don't get it. And you said something like, you pay the fee no matter what. You pay it while you're doing it. You pay it at the end. And you have bonuses and rewards, skipping the fee up front, selling, and then somebody else gets stuck with the bill when they get in and the risk shows up. But, you're not going to be able to bury the lead on that anymore.

**[43:44]** You build a lot of liability, a lot bigger if you wait until something happens.

**[43:48]** Yeah. Well, but it's funny, like, I remember having a conversation early on, right? For a front-end library with a big org, they had a really big risk, and they were looking for like a bigger contract. And it was like, you know, one of our first quarter million deals, right? And an engineer like me shows up and like, we mean these front-end guys, right? Like, they're going to do this for a library that like, you know, whatever, we were getting it for free. We'll just go patch it ourselves. I'm like, dude, you're not thinking about this. You have like, what, 400 products in your suite? You have all these customers, you have all these SLAs. By the way, your SOC 2 auditors, your job is not to do open source, so they will not accept that as a remediation.

**[44:22]** And at the end of the day, how many engineers do you pay to migrate? Like, 250 grand is a drop in the bucket versus one engineer who's decent, you know, out of the bay at least or whatever. but then you still have to probably got to either rewrite or move away and stuff. And the economics around that are changing. People can rewrite faster doing green field with Claude. Early on, data not showing that's quite as easy as we think. Turns out we rebuild same problems. So

**[44:49]** Something else I want to talk about is people say that we are safer within Java with the Maven Central Repository system. There are some horror stories about JavaScript dependencies being taken over by another maintainer then pushing vulnerabilities inside a new version. Or am I still right that Java is pretty safe? No, you're

**[45:15]** [laughter]

**[45:15]** They've not agreed.

**[45:17]** Safer safer from a few types of attack. Nothing's Nothing would intrinsically mean that a bad guy couldn't try and take over a Java project. That's, you know, that the social engineering that they use to do that is the same. the basic thing is because there's there's more control over whether what ownership you have to demonstrate to publish. You have to show you own the domain. So that's that's a barrier. so that sort of prevents a lot of the basic typosquatting things. It also prevents dependency confusion, which is where I publish version one and Dave publishes version two or version 9999, so that when I go give me latest, I get Dave's. that's not in there, but it's it's it's the other thing that's going on is because I'm going to sell sell Sonatype here is the tools that Sonatype use are applied to all the things that people upload.

**[46:17]** So, you get safety in that thing. There are other Java repos out there which don't have these rules, and people use those. I mean, there are some companies that just share their stuff by their own repos.

**[46:30]** Mhm.

**[46:30]** So, generally, you're a bit safer. The thing you're really safe from is not a Maven Central thing. It's the fact that Java doesn't have any install scripts when you download. Right? Cuz the killer is you install your Python or your node code, and there's an exit script, and things happen, right? So, you don't get them in Java.

**[46:53]** It's far less dynamic, right, on that last one? Yeah, it's far less dynamic for language, and so the attack surface is different. No offense, Brian. I'm a bit like Maven's not the thing that saves us, Maven Central or anything else. The domain one with the squatters, that's a great example of it. And but like so, how do you publish a package? Well, you sign it, right? And so, you do that, you get a key in there, whatever else. Cool, I get your credentials, and I put my key in. Now I'm you, and I lock you out. You got the same problem that NPM has, right? you know, yeah, the name space is a locked-down name space is locked down in other things, too. So, like some of that I mean, it happens, but like But here's actually thing. I think there's actually it's less about by the way, Maven, come on, and I gave you Brian crack for this after we did a podcast on that, but like you know what you don't have that everybody else has? Hey, something's bad or something's old, but not poison.

**[47:39]** Deprecated. How do I find that out? And when you send me your RSS blog feed, which is like a spec that's dead, I'm going to punch you. please, people, put it in Maven and Gradle. It Ivy would be really great, too, but you're already off road at that point. So, so or SBT or whatever else, right? stuff needs to get better in that ecosystem. I actually think that the Java community now I came from this and so people used to give me more crap when I was in the Java community. Now I've been in all the other communities and I can say this. The Java community typically leads better even in.NET which you know, I was never a.NET guy so we're we're the fighting cousins, right? Like a lot of respect for what they have there. The Java community generally leads by practice, right?

**[48:24]** They 2020 maybe 1918 like the word S-bomb starts coming around and people are like we need a bill of materials whatever and like there's blowing minds people are like how do I do it and whatever else. Yeah, y'all like you guys are just figuring out bombs? Like this is a concept you didn't have before? Like shipping has been doing this for 200 years. Like we just steal from real engineers, right? Java leads with that stuff. We lead with signed packages. We lead with infrastructure as code and providence, right? And so you also have more natural immunity and defense from being more attacked from being a more enterprise language. You know, and then we're an old language it's all static and stuff so it's easy to find stuff too, right?

**[49:04]** Not really easy ways to eval stuff without being flagged for it, right? So, we do need to do better though. We need the people who run those things to do better. We need to fund those things better. Guess what? Maven's really expensive to run. NPM GitHub right as NPM. That thing is really expensive running and they got more traffic than anybody and we need to get it but it you know, where does the money come from? And that's the whole shift that really needs to happen. Not just Java but Java could and should lead actually. We are better organized than any other camp. We should lead by saying I as the developer have the bar is so low to not mess up. Again, dependency management with Maven, right?

**[49:46]** I was pre-Maven days even, right? Maven comes around and you're like, "Oh, no jars, no scripts, no ant, like a whole classification of problems goes away. We're We're at the point where we're there. and actually What's his name? Gosling, the language author for Java. I was talking to him at JavaOne by chance.

**[50:05]** Yeah.

**[50:06]** Yeah. And he mentioned we talked about this and he had a really interesting take that I didn't see. He's like, "Oh, yeah. No, we totally just we've broken it in every language. We package dependency management with build tools.

**[50:16]** Mhm.

**[50:17]** Why don't we just make dependency manage it its own thing? And then we make build tools their own thing. And honestly, like a universal dependency manager protocol probably solves like I don't know the data, but like that pie chart, it's got a big part of just problems go away. Not to mention our build tools get simpler, right? So.

**[50:38]** Yeah. Mhm.

**[50:39]** Lots of mind share, lots of stuff going on.

**[50:41]** Yeah, I like that. It's a good idea.

**[50:42]** Yeah.

**[50:42]** We just have to keep it safe.

**[50:44]** Okay, and again, Java could lead this. so, we're already in a good language, let's say. As me as a Java developer, after this podcast, I want to improve my workflow without paying a lot of money. What should I look at? Open rewrite for instance, to have this in my GitLab pipelines? Is that an approach? Other tips?

**[51:08]** Everyone I lean toward more than ChatGPT or Code It these days. But I don't care which one. Again, open up incognito even, so it's not like aware of you as much as possible. And ask that same question.

**[51:20]** Mhm.

**[51:21]** And even frame it as like I'm a small team, I'm broke. Compliance matters. But whatever you're you know, even just a circumstance where it'd be hard. Like, you know, Anchor, right? Trivi- or sorry, Trivi is a thing right now. Obviously, that was big in the news. And I actually still really like their product. It's the thing that can happen to a lot of people. Shift to gripe though, free tools. Everyone can use them. Builds into an enterprise story. So, Devs out there, just go do it. Right? Turn it on and start using it and then go say, "Hey, the value's already there. Doesn't cost us anything. Do the procurement thing for the free product or whatever." But, yeah, renovate and open rewrite and all those kind of things are good. Sneak stuff.

**[52:02]** Yeah.

**[52:03]** So, yeah.

**[52:04]** I think what I'd say is first off, go get one of these tools or the SIVs or whatever and go and get yourself that S-bomb. Go figure out what you got because I think the first thing you want to do is go, "What's in your dependency tree?" Cuz take a look at it cuz most developers don't really look beyond what the first dependencies. And then, plug for HeroDevs, come to our website cuz we have this EOL data set. So, upload the S-bomb and we'll tell you everything that's in that list that's got end of life. And I mean, you can do it for more than just Java stuff. Not for any other reason than than start to get awareness of your real estate and understand where you are because that would be really happy. If everything you've got is got a is in support, you're in a good position.

**[52:51]** But, if 95% of what you've got is, you know, 5 years old, then you're in a bad position. People don't do that. They They're very rarely will developers have a look. You know, they do the Maven install and you get those pack lists and lists of, you know, downloading. They're like, "Okay, stuff." But, it's like, go have a look at it cuz I always say this, go have a look at it. fire it up and go get Sneak and see what dependencies what vulnerabilities you've got and do the EOL stuff and just become a bit more aware, you know? It's like, self-awareness, that's number one. And then, you can start making decisions about why am I using this in the first place? you know, do I really really really need this? You know, where does this come from? Where it comes from this dependency. Why do we have this dependency?

**[53:39]** So, do stuff. Or do I need exactly two JSON libraries to do the same thing?

**[53:45]** Yes. So, the other stack, which I think it's a type one, but the basically other stack is there's that the only time I can't remember the exact numbers, but basically the only time that anybody puts pays any attention to why something's in your dependency tree is when it's first selected. Once it's in, it's in. Nobody ever Yeah, and nobody ever comes back and says, "Why do we got that?" So, now would be a time perhaps to do a bit sort of spring cleaning. And even if the best thing you did was to raise a little bit of a work item that says, "We're just going to get rid of that old thing cuz it's we're only using it for that one-line string routine." That could make your security posture significantly better just by doing something like that.

**[54:29]** Steve, let's let's do this and you get another blog post from Steve, hopefully, of let's pick one of our projects and actually go do that because because again, with some of these tools and things becoming available, there's probably a little bit of a recipe that you can prompt in there to say, like not even just prompt, right? Like just like

**[54:45]** Yeah.

**[54:46]** Do I have you know, what is it? Like Guava and Commons and these three things. It used to be you had to find those and create risk cuz all of them have a different like new array list or whatever, right?

**[54:58]** Yes.

**[54:58]** But like

**[54:59]** Once you know

**[54:59]** I've already had these the PR is so easy.

**[55:02]** Yeah, I I've had this conversation with Brian Verber at Sonatype. We are going to vibe code this app. As you do. I've already got a name for it. We're going to call it exhume.

**[55:15]** Is it new though?

**[55:17]** Is it new? It doesn't exist. Yeah, but it So, you sneak sneak all of the data Sonatype has so everyone has the data. So, we could Why don't we just take the APIs they've got run the tools, you know, whether it's Sift or whatever, get the data, wire it up to the AR database, and just present it and see what we've got. It's not a I mean, I've done these things in the past, but to actually do something and join these things together, I think that would be quite interesting. I think people would use that.

**[55:43]** By the way, not everyone has the ELD data. Not to be a shameless shill, but like

**[55:47]** Yeah.

**[55:48]** We had to buy it at the company.

**[55:49]** You're ready but does anybody else

**[55:51]** No, no, no. Like again, that was like I existed in the CVE program for like a year and a half of like

**[55:56]** Mhm.

**[55:57]** What about ELD? What about ELD? What about ELD? Cuz no one Everyone puts it out of mind. The whole idea is to put it out of mind. So, yeah, that's a That's a really great idea.

**[56:04]** Yeah.

**[56:04]** One I want to actually go back and change my answer on one thing because that's actually the second phase that I would do. The first one is to say I'm in this scenario. This is my setup or whatever.

**[56:13]** Mhm.

**[56:14]** How do I build a culture so that this stuff happens automatically even if I have no dollars and I have to do it first and then ask for permission from management. Because if you put that in there, you'll get the free things. And I want to go write a blog post about this cuz I know how to do this and I'm I advise groups on the side and everything else like that.

**[56:33]** So, if you're on MPN and you install stuff, you get the list that says you've got some vulnerabilities you should upgrade.

**[56:39]** Yeah.

**[56:40]** That comes out of the build tool. So, why don't we have that for Maven?

**[56:44]** Yeah. Yes. A, yes.

**[56:49]** That's a call for someone to develop, yeah.

**[56:51]** Yeah.

**[56:51]** Well, and again, if we separated the dependency into a universal dependency, it's actually really easy to funnel all of everybody's undifferentiated lift into one thing cuz at the end of the day, we all just get it on the disk and then figure out what to do. So, yeah, kind of universal. Yeah, it's There's so many ways to solve this now. And people People, you know, again, the people at JavaOne that I talked with, the people down at like Last year at like Nothing Nothing's going to change. No, like pro- protocol doesn't change. We're big bureaucrat or whatever else. This year, everybody's like, "Yep. Okay, you want to change? You want to tear down the institution? You want to You want to break up the group, like the wave is coming and we're all much more pliable from the executive leadership all the way down. And if you work in a company that won't listen to you to do this. I used to do consulting for M&A work, right? Mergers and acquisitions. And my whole existence was either to like defend us from losing $10 million or go knock the price down by $10 million and literally all I did was ask

**[57:54]** The people that were there, like, why is it all broken and it has holes? No one listens to me. Cool. Call me later. We Without dropping dime, we can help get somebody involved to do that. Or call, you know, call CISA or the CVE program, right? Many of the people there, so. It's not untain- It's not unviable anymore.

**[58:13]** Mhm.

**[58:13]** There's old paradigms. There's demographics of people who come from a place. There's a whole demographic of people that are like, "Why would I file a CVE if there's no fix, right? If it's the last patch version and it gets vulnerable?" Don't file CVE, it doesn't belong in the program. And I'm sitting there shaking. I'm like, "I don't know that. I still run it." Like, tell me somehow, right? Vulnerable means it's there whether you have a fix. So, be the change. Get involved. If you're scared, we're out there. People will help you. It's actually super friendly. I had a new We one of our co-workers, Ed, went down to VulnCon this time. And like, I was like, "People will love you. You're an engineer. Like, they can't build stuff. A lot of them, right? They'll They'll take you in." By like noon the first day, he's like, "I met the keynote speaker and three people from the government." And I'm like, "We're meeting later, so."

**[58:57]** We can all make the change. Start today.

**[59:01]** I You both make me a bit scared, but on the other hand, you both suggest tools that will come up in the near future because there are very good ideas about this. We will definitely read about it on Foojay because you will both publish posts about this.

**[59:16]** [laughter]

**[59:17]** That's an invite day.

**[59:19]** Yeah, I'll do it. Let's go. I'm a lover.

**[59:21]** Okay, we are up on the hour, so I think we should wrap up. Is there one last message you want to share about security CVEs, what we should do as a developer? All the things we've talked about.

**[59:34]** Your vibe coding is going to make it worse unless you really are very careful reviewing what your dependencies are that your vibe code has chosen.

**[59:45]** Mhm.

**[59:45]** So, spend a lot more time thinking about what the AI tool has built for you from a quality point of view for sure, but have a good look at the dependency tree because it doesn't know anything that you know.

**[59:59]** Mhm.

**[59:59]** And it doesn't use the latest by default.

**[1:00:02]** And

**[1:00:03]** No, it does whatever Stack Overflow says was the one to choose.

**[1:00:05]** Mhm.

**[1:00:06]** Yeah, whatever is said the most, which inherently skews old, right? And by the way, the older your project, which are more likely to be the risk, the less helpful vibe coding is. Not to mention your dependency tree is screwed. So.

**[1:00:20]** Yep. Okay.

**[1:00:21]** Yeah, a good way to think about this might might would build on that to say it used to get lost in the mix, and we're solving that problem. And AI usually want to solve that problem. It's actually better for everyone, especially cuz, you know, the day that some major grocery store loses 10 million IDs for PII, and we're like, "Oh, that sucks." Like, we're like, "No, bring in the executive team and yell at them, right?" Like, that day is coming. That's what we're aiming for. And, you know, the liability will fall to you. Guess what you can't do. This is what I tell my engineers, right? Like, my January message, right? That was like, "Hey, by the way, like, here's the thing you kind of put on those." It's like, "I can't sue or fire the LLM."

**[1:01:02]** Mhm.

**[1:01:03]** So, you as the builder are not a get out of jail free card. You can make it do what you would do a lot faster. That's the right level of it. And eventually that'll get better, too. Like, you'll be able to let go of more and more, but like, we're still relevant. Please, CEOs, stop saying that everyone's an engineer because we have a whole AI industry where we're enabling tons of people to do it. It's crazy. Like, whole worth a whole podcast right there. but we've already found we strategically did it and we found many things that would have been risk risky to ship if you didn't have at least some level of barrier around it. And again, it's Everyone might code their own stuff. You realize that's SDLC, right? That's software, right? Go to write things that ship somewhere. It's got to go through an SDLC. It's got to go into a code repository. Otherwise, you're in violation and breach of all your contracts to your customers. Like, do the job. We can do the job. We can do it efficiently. Just plan for it, build for it, get help where you need it. Here at Nvisia, we are a decent-sized company

**[1:01:58]** Now, but we started from no one knew us, right? We were Angular JS migration. We're pretty dang good and friendly. We do specific things and we don't do the rest, but we know who does it. So, reach out. We'll help you out. Mhm. Love to hear also love to hear anywhere that this is wrong or you think it's not real, because I was not scared before. You should be scared. It will be okay, but the two to three-year period in the middle is going to be brutal and now is the time, even if you don't like your company, now is the time to make your job just not suck for a continuum, because you won't catch up otherwise.

**[1:02:34]** And maybe as developer, the time that we now win because LLMs are doing it part of our job, we can use it to improve our workflow and pipelines and checks and whatever that's happening there. Yeah.

**[1:02:46]** And security cool security, there's broad things for it, but it's also very bespoke to your environment, like eventually it'll weave in, but just like the documentation thing, right? I have a thing in my prompt that's like, "Document my stuff when we're done, right?" Throw in another thing there. And throw in a security expert edit, you know, from Claude in the PR check. And just and make it like literally I had this guy that was the old engineer who turned into a QA guy for the last three years of his career. When at my first engineering shop at a corporate place, name was Lynn. And all of us not hated, just feared Lynn. Like if you're getting a bug down the QA review with Lynn, you're like take all the rest of my tasks for the sprint. I'm just going to be going back and forth about balance checks and stuff.

**[1:03:30]** Lynn, that's who I have on my team. I'm like he hates developers. And he's good at finding stuff. And dude, honestly, my quality went up so much after like it was scary. So but now it's free. It's there every time. You don't carry the burden. So

**[1:03:43]** So we should ask our clothes to become our Lynn or whoever.

**[1:03:47]** Yeah. And can be very picky. Well, and if you're not using agent teams, right? Like to go write stuff to find agents in them, right? There's a whole way to do this where you say like I'm Dave the developer and I lean this way or architect right? I'm usually solutions architect. I'm my buddy Andre is like senior engineer or project lead or whatever, right? Lynn is the guy we're afraid of. You know, the tech writer does this before works out like you define these once. You can define them at your LLM level or you can put it per project or whatever. But you don't do it anymore, right? The old XKCD of like you automate yourself out of the job and the chart being like straight up, right? That's not straight up anymore. That's like you know, it's leveled out. We're due for a refresh actually if you do it, right?

**[1:04:27]** If people want to know more about security, there's a lot of articles about that on Foojay. I will also add a lot of links below this video for all the people who want to read more and also check out what you both are doing. Thank you a lot Dave and Steve for your time and being in this podcast. If you enjoyed this last episode, then please subscribe to the Foojay podcast on YouTube or in your favorite podcast app. You can find all our episodes, show notes and links to the resources mentioned today on Foojay.io. And don't forget to follow Friends of OpenJDK on social media for the latest news and updates from the Java community and also the people here in this podcast who write on Foojay and share a lot of their experience. Until next time, keep coding and keep learning and what should we add? Keep Keep making secure code.

**[1:05:18]** [laughter]

**[1:05:18]** Keep not giving up that we can do it, right? Find new ways. It's really actually it's super fun to have the tools to do it now, so.

**[1:05:25]** Yeah. Automate the boring stuff as they say. Let the tools work for you and find everything that you messed up related to security and that they can help you.

**[1:05:35]** Yeah.

**[1:05:35]** Okay.

**[1:05:35]** Or new stuff that comes out, it'll find it for you, right? Like that's We're getting there, so.

**[1:05:40]** We're getting there and we have to use the tools how they can help us as much as possible. Okay, thank you for listening. Thank you for joining and I'll see you next time.

**[1:05:48]** Give me a foo.

**[1:05:50]** Give me a jay. Give me the friends of OpenJDK.
