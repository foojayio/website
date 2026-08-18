---
title: "Spring AI Debugging + How to Contribute to Open Source"
date: "2025-04-02T05:56:01+00:00"
lastmod: "2025-04-11T07:29:01+00:00"
description: "In this episode, I focus on continuing my journey with vector databases, integrating Pinecone, Neo4J , and Spring AI."
authors:
  - "jennifer-reif"
image: "techtalks.png"
categories:
  - "Debugging"
  - "Spring"
tags:
related_posts:
  - "breaktime-tech-talks-ep37-vector-database-frustration-microsoft-lazygraphrag"
  - "building-a-fullstack-imdb-clone-with-a-java-backend-using-sparkjava-and-neo4j"
  - "native-graphql-api-with-neo4j-auradb-on-heroku"
  - "faster-integration-tests-with-reusable-testcontainers"
frozen: false
---

In this episode, I continue my journey with vector databases, integrating [Pinecone](https://docs.spring.io/spring-ai/reference/api/vectordbs/pinecone.html), [Neo4J](https://docs.spring.io/spring-ai/reference/api/vectordbs/neo4j.html) , and [Spring AI](https://docs.spring.io/spring-ai/reference/index.html). While making some progress, I also encountered hurdles, such as evolving APIs and the unique architecture of vector stores.

Next, I share insights from [an article on contributing to open-source source projects](https://dzone.com/articles/why-and-how-to-participate-in-open-source-projects), how it can accelerate your career and enhance both your technical and soft skills. From picking the right project to building credibility within the community, it's a series of steps that gets better with time and practice!

{{< youtube W36VlhRHo_g >}}

## Vector database architecture

Vector store architecture, I found out is a little bit different from what I'm used to with relational or graph databases. As an example, graph databases start with data and then the index will sit on top of that data. Indexes are used as the starting point of the query or for relational databases, to do a lookup for the tables and such, the rows that you're looking for. So you can load data and or create the indexes first in a graph database. It doesn't really matter as long as you don't have conflicts between the index and the data that you're trying to add that index to.

The vector store has an index that actually encases the data, which allows you to partition the database by different indexes. So you could have multiple indexes in the same vector database and store different vectors, different documents, different whatever you're putting in there, and separate those out by index. So you're going to load data into an index and then removing that index also removes the data.

This was an architecture thing that I just don't think really sunk in until I saw some of the architecture diagrams and started playing with importing and exporting data in and out of the vector database. And then I realized, oh, this is different from what I was expecting. I need that index in order to put data into it. And in order for me to query the data, I have to reference and use that index in order to find the data that's connected to it. If you're playing with vector databases or you're new to that, that is going to be a shift from maybe other types of databases.

And then I also ran up against loading the data into the database. I did some further work there, and there were some things that were out of sync that made it a little bit more of a trial and error process than I would have liked.

First off is that Pinecone has changed some of their API stuff, and their documentation doesn't always reflect the latest thing. It's not fully up to date. Some pieces are and that works fine. But then other things seem to be out of sync or don't quite explain fully what the new steps and process are. Then also in Spring AI, some of the documentation there's a little bit out of sync because they recently upgraded to a new milestone release. Some things have changed there. Most of the documentation aligns and matches that M6 release, but not all of it completely aligns, especially when you look at different vector stores.

That was the thing that I noticed. I was having trouble with one vector store, so I just dropped that dependency and pulled in another vector store and similar issues there. Even though the API For Spring AI VectorStore, it looks the same. Underlying, if the database has changed, they don't quite sync up with the latest version of the database always. Hopefully there will be some changes there and they'll update some of the libraries and such that are working with the backend databases to reflect the latest API changes from those vector stores, but not everything is in sync just yet. And again, lots of changes going on lots of times, so things changing, at variable times and that's hard to keep up with.

## JSON formatting

But I went back to Spring AI to read and parse the JSON that I was talking about last week, and it was a little bit tricky because I realized and it fully sunk in that I'm dealing with JSON lines format, which is different than a lot of, I guess, standard or typical JSON formatting.

It's just wired a little bit differently. Instead of throwing multiple objects into an array separated by commas, it just lists the multiple objects on each line. So they're separated by lines, basically. This was a little bit tricky when I was trying to read and parse. A lot of the Java libraries work with the regular JSON array formats, but don't necessarily work with JSON lines, so I had to do some kind of reworking or some additional research in order to figure out how to read JSON lines format and parse that and operate on it.

But once I figured it out, I think now I have a decent starting place where maybe, hopefully, I won't have those same problems going forward. I do plan to put together a project and probably some content, blog posts and so on, detailing the differences and the things that I worked with and things I ran up against, but that's not out yet and, hopefully I'll get to it sometime soon.

## Pinecone configuration

The next thing was that the required Pinecone configuration has changed in Spring AI and the UI has changed in Pinecone itself. So I spun up a free tier instance on Pinecone on their cloud. First of all, their free tier doesn't use the namespace qualifier, which is fine, it's not required, but it is a little bit confusing sometimes looking at their documentation, it throws that namespace thing in there all the time, and sometimes it's hard to know, do I need to have that, do I need to not,

Then the environment format has changed. It used to be something like cloud provider plus the tier that you were on as the environment, but now it's actually the cloud region where that database is hosted. Again, it's not super clear in the documentation how this looks. Also, project ID is crazy hard to find, but I finally figured it out that it's inside the host name itself.

So when you look at your free tier instance of Pinecone, it shows the full host name, the full URI. And the project ID is actually within that value, but you do have to know where to look for it. So it took a little bit of trial and error, but I figured out that the host name combines \<index name\>-\<project ID\>.svc (for service) .\<environment\>.pinecone.io.

So in case you're wondering, it's `<index>-<projectID>.svc.<environment>.pinecone.io`. Super confusing to find out, but once I know the format that they're looking for, it's actually much, much easier, and I was able to get Spring AI to connect with no problem once I figured out all the pieces and assembled them correctly. That was really tricky, took me way longer than what I wanted it to, but again, some things that were out of sync between Pinecone and Spring AI.

## Configuring multiple vector stores

The next thing is I spun up a Spring AI application using Pinecone and Neo4j. The tricky part I found here is that I needed to have multiple vector databases configured in the same application. Which meant that if I tried to just let Spring do its auto configuration, the vector store beans would conflict because a generic vector store bean would try to pick up Pinecone, and it would try to pick up Neo4j, and it'd be like, which one do you want me to pick up? I don't know.

I actually needed to create two separate beans and and set those values separately. But then, I was setting the properties for the index name and label and project ID and so on inside the `application.properties` file, and not in my environment itself, so I needed to wire in the environment inside my application to set those values from properties files.

I'll probably try to rework this and clean this up a little bit, but it is working for now. I do have the beans working and I'm able to connect to either database as I so choose.

## Why and how to participate in open source

The last piece I want to talk about is a piece of content that I came across and actually I came across this a few weeks ago and just haven't had a chance to cover it yet. So I'm excited to do that today. And the article is called Why and How to Participate in Open Source Projects in 2025.

I've actually had a few different questions on this and I've just been thinking about this because I feel like just recently I've started to feel pretty comfortable about finding gaps or holes or things that are missing and being willing and able to contribute back to a project.

When I originally graduated school, everyone was telling me Oh, you need to build your your portfolio and work on projects and contribute back to projects and so on. And I always found this really intimidating and hard to do because I felt like, well, I don't know what I can contribute. I'm not sure where to start.

Really, I've just found that over time and over comfort and building more things, I'm starting to see the gaps or where things could be improved or where I might be able to help and contribute something back. I would give it some time. Give it some practice. Just keep building things as you can and it's easier said than done. I know that.

But this article talks about contributing to open source projects as a way to accelerate your career, enhance your skills, and expand your professional network. It's going to improve both your hard and your soft skills.

First of all, on the more technical side, there's a wide range of technical contributors on our project that have all kinds of technical knowledge. And from my perspective, think about where a lot of open source projects start. They're going to be the things that are gaps or that people want to complement existing tools or technologies or help make things easier when working with those tools, or those who just want to explore and learn. They're going to be some pretty bright people who want to be there and want to contribute and build better things and make development easier.

And then on the other side, you have communication and group decisions and building the credibility and your voice within the community. Those are really valuable skills to have. It's going to improve your technical standing as well as just your community standing and networking inside the community itself.

Then the article goes into how to actually get started contributing to open source projects. The first thing is to pick a project. Try to align with your interests and your career goals. Remember that open source contributions often start as a hobby. Find something that interests you. I always say to find something you want to know about, you want to learn, something to enhance your career, your skills, and just look for those projects that seem fun and interesting.

Then the article says to join the community communication channels that help you understand the tech and how the contributors work. Really get a feel for how the process works, how these contributors operate, and handle conflicts and discussions and so on.

Then the next thing is to look at the documentation, and I would add to this content as well. The article does talk about content alongside the documentation, but it might be slightly separate from official reference documentation.

Over time, I think this is the easiest place to find gaps and contribute because there's often things that are overlooked or that are missed or that are out of sync because the code changes or features have been added. Documentation is sometimes an afterthought, or things get missed, or maybe some people understand things a little bit differently, and so might need a few other descriptions or examples to help comprehend what's going on. That's an onboarding ramp if you want to start somewhere. I think documentation is a great place.

Then the article also says to start small when you're ready to start contributing. Look at things like tests, documentations, refactoring, tasks that are really valuable. It's those small hidden things that may be frustrating when you're just starting out or that you might not have a lot of the background experience that long term contributors might have had. Those are things that you're going to have those external skills that you can find those gaps and maybe help fill them much better.

Then, as you build your credibility, then you can start adding to enhancements and new features. The article points out that if you just drop into a community and say this needs added, or I think this would be good to add, you're not really going to have a good community rapport built up there. It's not going to be taken maybe quite as seriously or prioritized as highly as if you've already been involved in the community and are already trying to make the project better and to help out.

Lastly, the article closes with some example open source project ideas...but whatever stack you might be working on probably has some libraries or some things that are missing and out of sync that could help you some contributions too. So I would encourage you to look within whatever stack you might be dealing with at the time.

## Wrapping up!

This episode, I talked through my latest progress in playing with vector databases, getting data loaded, and then exploring it with Spring AI. Then I discussed an article on the why and how of contributing to open source projects.

As always, thanks for listening and happy coding.
