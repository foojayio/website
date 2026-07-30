---
title: "Breaktime Tech Talks (Ep39): Why embedding models should match + Advice for starting a blog"
slug: "breaktime-tech-talks-ep39-why-embedding-models-should-match-advice-for-starting-a-blog"
date: "2025-04-16T05:35:04+00:00"
lastmod: "2025-04-22T08:07:51+00:00"
description: "This week I talk through my hands-on experience of what happens when embedding models for creating vectors and searching them don’t match."
authors:
  - "jennifer-reif"
image: "/images/posts/2025/04/breaktime-tech-talks-ep39-why-embedding-models-should-match-advice-for-starting-a-blog/techtalks.png"
categories:
  - "Databases"
  - "Graph"
  - "Java"
  - "Neo4J"
  - "Uncategorized"
tags:
related_posts:
  - "breaktime-tech-talks-ep37-vector-database-frustration-microsoft-lazygraphrag"
  - "building-a-fullstack-imdb-clone-with-a-java-backend-using-sparkjava-and-neo4j"
  - "how-to-create-a-spring-boot-application-to-retrieve-data-from-evernote"
  - "how-to-run-neo4j-on-kubernetes"
frozen: false
---

This is episode 39 of the [Breaktime Tech Talks podcast](https://www.youtube.com/playlist?list=PLzZ7iUdr2mwS6qexoz9dsI0NfnU614p1w).

I'm at a conference this week presenting on a variety of different topics, but I stopped for just a few minutes to document some recent learnings while I was preparing the code projects for this week's sessions.

I'll chat about a hands-on experience that helped me understand why embedding models for vectors and search should actually match. Then I came across an article with advice for creating a blog that outlines a lot of the practices I myself follow when I write tech blogs. I hope you enjoy it as well.

Why embedding models should match {#_why_embedding_models_should_match}
-----------------------------------------------------------------------

The first thing I wanted to talk about is embedding models need to match when you are creating the embeddings and then when you're searching them.

I had heard this before, I think at a conference last summer, that when you use an embedding model to create embeddings for the data in a database, and then when you try to search those embeddings, you should use the same model or the at least the same model family. I found out how true this actually was with a real life example.

Now I was already using the same embedding model family, because I was using OpenAI's models. I think it was 002-text-ada and the 3-text-small models. But what I was trying to do, the Pinecone vector embeddings, so I was trying to save data into Pinecone and create vector embeddings to the database. And then I was trying to do basically the same thing with Neo4j as well. And one model was default for one database and another model was default for another database. Again, both using OpenAI model family, one was using text-ada-002, one was using text-3-small, I believe it was. And I just went with the default models - they were OpenAI, looked good, and created the embeddings.

When I tried to search, it would not find results for one or the other in my application. So my application had a default model set as well for OpenAI, and I believe I set it for the text-3-small, which matched Pinecone at the time, what I'd embedded Pinecone data in. And when I would try to search, it pulled those results back. But then when I would try to search Neo4j, which Neo4j used the ada-002 and my application used 3-small. And I would try to query Neo4j, and it wouldn't retrieve anything or retrieve garbage.

And so then I flipped the model of my application to use ada-002 and thought, well, okay, I'll try it this way and see if Neo4j is just being picky. Then I queried data with Neo4j, and Neo4j data retrieved great. But then I tried to use the same query against my vector store in Pinecone, and it didn't. The model didn't match, and it didn't retrieve anything good either.

So it wasn't the database. It's really the embedding and the vector representation between the models is so different. When I went back and recreated the data in each database using the exact same model, then did a vector similarity search from my application using the exact same model, then I was able to get good results from both databases with no problem, but all of my models had to align.

I had to create the embeddings in Pinecone with text-3-small OpenAI. I had to create the vector embeddings in Neo4j with the model OpenAI text-3-small. And from my application, I had to query vectors using OpenAI text-3-small. So all of those needed to match in order for me to retrieve anything of value with that.

### Differences in similarity search {#_differences_in_similarity_search}

Even with different databases, you still can get slightly different results even when all of your models match, like my case did. There were still a few instances when I would send a query to Neo4j, to Pinecone - again, all three now on the same model - and I would get slightly different results coming back from the vector similarity search.

This is the way that each vector database will implement vector similarity search. It's gonna be slightly different for every database, right? Because every database is slightly different in the way they store and retrieve data. And every database engineer is gonna have maybe a slightly different flavor for how they might want to optimize those queries.

So again, every implementation of every database vector similarity search might be just a little bit different. Even taking the exact same data set, the exact same embeddings to store in there, and using the exact same query to query those vectors in the database, you still might get slightly different results just because your database is different.

Just to be aware of that. This was a case that I had to see firsthand with my own data and my own experience in order for me to understand what was going on and see, oh, it, this really does make a difference. There's a lot of variables when you're talking about AI and this really is no different. Just more to add to the mix.

Advice for starting a blog {#_advice_for_starting_a_blog}
---------------------------------------------------------

I also came across an article that really resonated with me because I really love to tech blog, and it was an article that's called ["Advice for a Friend Who Wants to Start a Blog"](https://www.henrikkarlsson.xyz/p/start-a-blog). I thought this was really great for anyone interested in starting their own blog, whether that's technical blog or something else.

It can apply to lots of different topic matters and subjects, think like code projects or libraries or hobbies or anything general. But I really thought this was cool for tech blogging. It follows a lot of the methodologies that I use for tech blogging.

First, I'm just gonna cover a few of the examples and advice suggested in the article that most resonated with me. But feel free to check out the article for the full list.

**1. Write for yourself.** Find out what's interesting, what you learned. That's gonna make your content unique because it's your learning journey. And hopefully somebody else resonates with that. Maybe they came across the same stumbling blocks or they perceived topics or concepts the same way that you did. And maybe they'll have other differences, but something in there might help resonate with somebody else as well. So code, write blog posts for yourself.

I like to use tech blogging as almost a referencing documentation, dictionary for later because when I come across a problem, I solve that problem. And then, as is typical, you kind of move on and do something else. Well, then you might run across that problem later on down the road and you go, ah, I can't remember what I did with that. Then you're stuck looking through local documents or code repositories, or you're going out to GitHub and digging and searching through things. How did I approach this problem? And maybe you find the solution, but you don't remember how you went about getting to that solution or why you used that solution versus maybe six other solutions.

So I like to tech blog because it documents my thought process - how I went about finding the solution to the problem, maybe resources where I uncovered the solution or located it or put pieces together from multiple solutions in order to get a working piece of code or to go about the problem in a certain way.

I see this as almost self documentation for me to reference later in case I come across the same problem again. I remember how I solved it and why I went about it that way. Now, of course, hopefully that's helpful to others and a broader audience too. But I really love to use it as a self documentation type of exercise, I guess.

**2. Be yourself in your writing and approach writing in a chat-like style.** That's gonna make you more relatable. If you think about when you talk to somebody on the street or at a conference, it's very casual. The conversation feels very natural and organic, even if you're talking very technical topics.

When you do the same thing in a blog post, it feels very relatable, very conversational, very casual. And yes, you wanna avoid too many casual, maybe unprofessional or inappropriate things. You want to have some level and some boundary of professionalism in your writing, but to some degree, you also want it to feel very natural and very interesting to the average person, right?

Whether you're experienced in that particular technology or you're not, or you're just learning or you have some experience, but maybe you need some background on some of the concepts, to come across very "here's what I learned, here's how I went about it, here are the steps that I took," it's pretty easy for anybody to follow along with that conversation. And even if it's somebody doesn't agree with your methodology, they might be able to understand, oh, here's how this person got to this. I can kind of see that. Alright, no big deal.

It's also a little bit less threatening and a little less intimidating for somebody who's coming across your work, whether they're experienced or not. I really enjoy this type of writing and it also makes it fun and interesting for me to reread because again, it feels very natural and what I was thinking and feeling at the time.

**3. Plan and edit your writing.** This kind of goes against, I guess, the last bullet point, if you wanna think of it like that. I don't over structure my content before I write it, but I do have a general idea.

And the way that I actually really like to approach this is that I'll usually work on a code project or deal with a certain problem and find the solution or put together a presentation or something. And there's always something that I learn or that I didn't realize or a roadblock that I had to figure out how to go around or to break through.

And those struggles are usually what I like to document. Because of that, because I'm picking content I've already worked through, that kind of does a lot of the outlining for me because I already have a process for how I went about solving that problem - things that I researched, things that I tried that didn't work until I finally got to a working solution. That helps me outline.

And then there's a little bit less work when I write my content, at least, because it's usually "here's how I solve the problem, here is my process for that". So a lot of the planning, editing, outlining is already done for me because I've already worked through that once when I actually solved the problem.

**4. Digital content will allow people to pick and choose the pieces they find interesting or valuable** and then close the tab when they're not interested or when they've gotten what they need. This I found really cool because instead of standing up in a room and presenting to a crowd or talking to somebody and instructing them through something, digital content allows people to pick and choose what they find interesting.

And this also works for me, self-referencing my own documentation. When I come back and try to solve the problem again, I don't necessarily need to read the whole thing from top to bottom and understand my thought processes from beginning to end. I'm really just looking maybe for one little piece of how to solve the problem or why I did it this way. I'll find a little bit that applies to my situation now or the thing that I need, and then I get that piece and I drop back out again.

I find this super helpful for me, myself on my own content, let alone hopefully for somebody else too. Digital content makes it very accessible for people to pop in, get what they need, and then step out again, and then do the same thing later when they run into that problem or they're trying to explain it to somebody else or somebody else is having the problem, they're like, Hey, check out this piece of content. It might have what you need.

This is what I love about digital content tech writing, tech blogging, tech podcasting, tech video making, and so on. It allows people to just get in, get what they need and get out without really worrying about offending somebody for popping in and leaving a talk or being rude and saying, cut to the chase. I really just wanna know about this.

So I like this almost anonymous, a little bit more efficient maybe, when you're trying to learn. Now, of course, there are some things that you can't replace a one-on-one conversation or sitting down with somebody and watching them code or pair programming on a problem. But I think in a lot of cases that digital content provides a different medium in order to learn and be productive in different ways.

**5. (Related personal tip) A colleague once told me to write the content that I struggle to find the answer to.** When you go out to Google or you go out to any other type of search engine, you try to look to solve a problem. Maybe you can't find content that you need. Write that content so that the next person coming along and struggling with the same thing or very similar things has something to reference.

And that's you included, right? You come back across that problem, ah, there's still nothing on Google for the problem I'm trying to solve. Write that content and fill in those gaps so that way hopefully you or somebody else can benefit from that. It's gonna help fill the gap and hopefully help the next person down the road.

Wrapping up! {#_wrapping_up}
----------------------------

This week I talked through my hands-on experience of what happens when embedding models for creating vectors and searching them don't match, and why it's important to align those models for both. Then I highlighted some advice from an article for starting a blog that applies to tech blogging as well, and that I consistently use.

Thanks for listening and happy coding.
