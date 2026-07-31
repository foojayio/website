---
title: "Breaktime Tech Talks (Ep37): Vector Database Frustration + Microsoft LazyGraphRAG"
slug: "breaktime-tech-talks-ep37-vector-database-frustration-microsoft-lazygraphrag"
date: "2025-03-19T12:54:36+00:00"
lastmod: "2025-04-11T07:28:27+00:00"
description: "This is episode 37 of the Breaktime Tech Talks. https://www.youtube.com/watch?v=hv-FtjIHg_I&list=PLzZ7iUdr2mwS6qexoz9dsI0NfnU614p1w&index=1 You - by Jennifer Reif"
canonical: "https://www.youtube.com/watch?v=hv-FtjIHg_I&list=PLzZ7iUdr2mwS6qexoz9dsI0NfnU614p1w&index=1"
authors:
  - "jennifer-reif"
image: "breaktime-tech-talks-img-large.jpg"
categories:
  - "Conference"
  - "Databases"
  - "Events"
  - "Graph"
  - "Java"
  - "JBang"
  - "Neo4J"
  - "Spring"
tags:
related_posts:
  - "intro-to-rag-foundations-of-retrieval-augmented-generation-part-2"
  - "jc-ai-newsletter-9"
  - "intro-to-rag-foundations-of-retrieval-augmented-generation-part-1"
  - "genai-blood-sweat-and-tears-loading-data-to-pinecone"
frozen: false
---

> This is episode 37 of the [Breaktime Tech Talks](https://www.youtube.com/playlist?list=PLzZ7iUdr2mwS6qexoz9dsI0NfnU614p1w).

{{< youtube hv-FtjIHg_I >}}

You are listening to the Breaktime Tech Talks podcast, a bite-sized tech podcast for busy developers, where we'll briefly cover technical topics, new snippets, and more in short time blocks. I'm your host, Jennifer Reif, an avid developer and problem solver with special interest in data, learning, and all things technology.

It's been a bit of a week over here, from daylight savings time last weekend, to just some typical developer frustrations, this week has put me through the ringer. But, I've been stressed all week about upcoming projects and tasks that I'm working on, and after chatting with a colleague, I'm seeing that it's not just me feeling some of these things.

Which is a good reminder to put things in perspective and escape a little bit of the imposter syndrome by reminding myself that others are experiencing similar things.

Devnexus conference shout-out {#_devnexus_conference_shout_out}
---------------------------------------------------------------

First of all this week, I want to give a huge shout out to the Devnexus conference. It was a fabulous event that I attended last week, and I gave a keynote presentation there which I loved doing.

I was a little nervous about, but I was super thrilled and excited to do it and then was super happy about it afterwards. I had some really great feedback. I got to talk with some really amazing people and just wonderful people in the community doing really fun and exciting and inspirational things.

That was a ton of fun. I made a whole lot of new connections and got to hear some ideas and inspiration as well.

Vector database exploration {#_vector_database_exploration}
-----------------------------------------------------------

But the first topic that I started digging in hard on this week was some vector database exploration and a few frustrations I ran into along the way, too. I'm working on this for an upcoming presentation and demo.

I'm looking at vector databases and graph databases and RAG and how all of those kind of play together. I kind of went back to the basics. [What is a vector database?](https://www.pinecone.io/learn/vector-database/) I pulled up an article from Pinecone because initially when AI hit the market a couple of years back, Pinecone became the default leader in the space for vector databases.

And I'd spun up an account a couple of years back on Pinecone and had spun up a database and then just never really did much with it. So I circled back on that this week and thought I would explore Pinecone a little bit more just seeing what vector databases look like, how they index, what their data looks like, what the similarity searches do, and so on.

### Issues getting up and running {#_issues_getting_up_and_running}

But there were a few issues I ran into along the way. The first thing was that I got the database up and running with no problem and I wanted to connect to it in any way that I felt like I could. And so I went out to the documentation and I was super excited. They have a Java SDK.

So I thought, great, I will write a Jbang script. I'll connect using their Java SDK to Pinecone and kind of work that way and see where things are. And I spun up the instance, I tried the Java SDK, but the first thing that I ran across was that their API reference documentation is all based around Python, JavaScript, and curl, which is totally fine. But I was struggling a little bit figuring out the syntax that I needed, how the code might look a little bit differently in Java, and so on, and ended up running into some issues where some of the functions and methods that were shown in the Python, JavaScript, and cURL shell availability APIs are not available in some of the other languages, including Java, of course.

Some of the functionality that was available in their API was not available via Java. So there were just some things, for instance, to list the indexes. I could list the index, get an array of results back of the indexes I had on the database, but I couldn't actually see if a particular index was in that list because I couldn't sift through the indexes and loop through them, basically.

I would have to parse the result object coming back and sift through it very manually, almost text-based. So again, there were lots more functionalities available in some of the other SDKs, but just the Java one was a little bit reduced and not as full fledged, I will say.

I thought, okay, I ran into some walls there, so I thought, well, I know Spring AI has really great integration with databases. I know I can load all kinds of files and things through that. Let me just spin up a Spring AI app with Chroma, because I had heard that that vector database, especially with Spring AI, is super easy to just configure and auto configure out of the box. So I spun that up, spun up a Spring Boot project, but was struggling to get the application to connect to the database at all.

Even just a basic, here's the dependency, it should roll up an embedded, and then putting in the property in the application.properties file for the initialized schema. That's all the documentation was showing was necessary. But I couldn't even connect. It was showing that the connection would close on the database and so on.

I didn't really dig terribly far into that because then I also saw that Spring AI integrates with Pinecone, as well. So I spun up and just switched out the dependency for Pinecone. And I was able to connect the database really easily there, but then I was running into some roadblocks on how to manipulate some of the JSON data to get the format that Pinecone is expecting in its database.

For instance, it does an ID, a vector (key value), and then metadata (map), if you will. And so I needed to convert my JSON into that format - an ID, a vector, and a map of metadata. And so I was struggling a little bit to figure out how to parse through the JSON and figure out how to put it into an object that then I could upsert into Pinecone.

I asked around a little bit inside my company on options for this and someone mentioned, "Hey, we actually have at Neo4j awesome procedures on Cypher or our APOC library. We have some procedures and functions that will connect directly to vector databases."

And I thought, great, this feels a little bit more natural. I'm much more comfortable manipulating data with Cypher and then mapping it to the format that Pinecone is going to expect. And I know how to manipulate and transform that data in Cypher. So this felt like a little bit more of a natural, easy on-ramp for me at least.

But I popped into the APOC and Pinecone stuff and some of the documentation's a little bit behind maybe, or Pinecone has changed their APIs, one of the two. So there were some things missing. There was lacking a little bit of clarity on the config, specifically from the APOC side on what I needed to provide and how that was supposed to look.

So I'm still running into some roadblocks. I did finally get the connection up and running - able to create indexes and am able to put the data into something Pinecone is looking for, but I'm still not able to get it upstarted. But again, I'm making progress. I will get there, working through it just a little bit more slowly.

### Overall experience {#_overall_experience}

Overall, my thoughts and my experiences on this. I tried multiple different approaches, ran into a few different roadblocks along the way. And it gave me a reminder that Neo4j, yes, from my point of view, because I'm on the inside, I do see some of the gaps and the things that are missing. But honestly, no product is perfect. And there are things that Neo4j really handles very well and at least matches market on several other things.

And it really, actually, because it has the Awesome Procedures On Cypher, I felt, and it was a more natural translation, there's multiple options you can go at approaching this problem. I love that Neo4j has an option that you can use Cypher in order to port data over to a vector database or vice versa, right?

You could pull data from a vector database into Neo4j and it just gives you more options for whatever feels most natural and comfortable for you. Again, running into some roadblocks aside there, hopefully those will get worked out along the way.

But also, AI is changing so, so fast, and I think that's one thing that is hard for everyone to keep up on. APIs are changing, libraries are changing, documentation's out of date, features are getting added, and so much of that just gets the gaps and the things that end up missing fall through the cracks. And so, then you have kind of user issues or frustrations on the other end because stuff is just changing so fast, it's hard to keep up with.

Also the Java support and a lot of the generative AI or AI tools and APIs is still a little bit lagging behind things like JavaScript or Python or even TypeScript. And it's a little bit limited, which is a little bit frustrating unless you have options where you can delve into the Python end of things and play with the things that are going on in Python.

If you're dealing with Java and only Java, you are kind of limited a little bit in certain aspects of the tools and the capabilities that are available because the Java libraries and tools just haven't quite caught up with everything that's going on in the Python or GenAI front, if you will.

Then, the last piece of which is that data and databases are the crux of technology, and you can't escape some of the pre processing steps no matter what database you have chosen. Neo4j we see some of these frustrations in a lot of the pre processing and modeling and things that have to go into before you put data into the graph, but we're seeing that with vector databases, we're seeing that with other types of databases. You can't completely escape a preprocessing.

You have to take some form of raw data, put it into a usable and interesting format for your use case, and then put that into your database and get value from it. So there's a little bit of that work that has to be done up front, no matter what database you choose.

Microsoft LazyGraphRAG {#_microsoft_lazygraphrag}
-------------------------------------------------

The content highlight I want to look at today is I was trying to catch up on all of the AI stuff, hence the vector side of things that we just talked about, but I came across an [article on Microsoft's LazyGraphRAG](https://medium.com/data-science-in-your-pocket/microsofts-lazygraphrag-smarter-faster-and-more-cost-effective-data-retrieval-63823d8b8622), and this is something that has been mentioned a couple of different times.

I heard a little bit about it at Devnexus and inside my company as well, things floating around. So I pulled out an article that was written on Medium talking a little bit about what it is. And just from what I can understand, it's looking at the opportunity costs of pre processing data for GraphRAG versus post processing data for GraphRAG.

This approach, a lazy GraphRAG approach, provides a way to handle the processing at runtime or on the fly. So one of the problems is that when you do a lot of pre processing, so you have data that's coming in, maybe a lot of data coming in very quickly or in real time. And you end up doing a lot of pre processing steps to get that data cleaned up, to put vectors and embeddings on it, to add maybe some other layers of algorithms or communities or something like that.

And so there's a lot of upfront work that has to happen. And then of course, maintenance, every time something changes or things get added, you have to rerun some of those things and do that pre processing levels again. Lazy GraphRAG gives you an opportunity to only do those things at runtime and only maybe in certain segments of the graph where you need them.

This is going to avoid the heavy preprocessing and the upfront maintenance. The trade off that I see is that you're going to load that latency or your or that cost into the actual runtime of things. So when you actually run the queries that you need to, you're going to have to front-load those queries and add a little bit more time ahead of the query in order to do some of that preprocessing.

It might be worthwhile if you have very, very large data sets from what I can tell, but maybe not for more manageable or time sensitive results. So if you have smaller graphs, personally, I would think that pre processing ahead of time and just routine maintenance would be more beneficial. You would get probably faster query time results than loading that processing and kind of the pre processing and stuff into the runtime or the query time.

So that was just my take on it, but feel free to read the article for yourself and check it out and see what you think. I'm happy to take input or if someone knows a little bit more in depth or has explored this a little bit more, I'd love to hear your take on that.

Wrapping up! {#_wrapping_up}
----------------------------

Even though progress was slow this week, I am learning a lot, so that makes it worthwhile in the long run. Today, I recounted some frustrations and learnings and I'm going to be exploring trying to get data into a vector database from a Java developers perspective. Then I caught up on the recent Microsoft LazyGraphRAG and the opportunities and implications that I see from an initial review.

As always, thanks for listening and happy coding.
