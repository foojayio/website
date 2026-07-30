---
title: "Interview: Marc Hoffmann and Java Version Almanac"
slug: "interview-java-version-almanac"
date: "2020-08-25T06:39:44+00:00"
lastmod: "2020-09-01T14:07:55+00:00"
description: "The Java Version Almanac provides details per release on OpenJDK distributions, new features, and differences between APIs across releases."
authors:
  - "geertjan-wielenga"
image: "/images/posts/2020/08/interview-java-version-almanac/Screenshot-2020-08-25-at-08.35.09-1024x559.png"
categories:
  - "Interviews"
tags:
related_posts:
frozen: false
---

From this week, we're happy to announce that we're hosting Marc Hoffmann's [Java Version Almanac](http://javaalmanac.io) [right here on foojay](https://foojay.io/almanac/jdk-8/), providing details per release on OpenJDK distributions, new features, and differences between APIs across releases.
![](/images/posts/2020/08/interview-java-version-almanac/Screenshot-2020-08-25-at-08.35.09-1024x559.png)

It also shows differences between the APIs of the currently selected Java release and all previous releases, handy when you're upgrading!
![](/images/posts/2020/08/interview-java-version-almanac/Screenshot-2020-08-25-at-08.55.35-1024x435.png)

Go here to take a look, yourself: <http://foojay.io/almanac/jdk-8>

And, let's meet Marc, here's a quick interview with him about the Java Version Almanac**!**  

<figure class="alignleft size-large is-resized">
 <img decoding="async" src="/images/posts/2020/08/interview-java-version-almanac/Screenshot-2020-08-21-at-09.20.34.png" alt="" class="wp-image-32786" width="154" height="176">
</figure>

**Hi Marc, can you briefly introduce yourself and how you've been active in the Java ecosystem over the years?**   

I somehow accidentally stepped into Java at the very beginning, back in 1996, and I did a couple of projects already on the very first Java version 1.0.2 and then 1.1.

Over the years, I continued to work with Java in very different contexts --- also in various open source projects. Probably the best known is the [JaCoCo code coverage tool](https://www.jacoco.org/jacoco/) which I started in 2009.

As I'm always eager to learn new things, I visited many conferences and happened to become a conference speaker myself. This helped me to make many friends in the Java community. And since 10 years I co-unorganize the JCrete unconference on the beautiful island of Crete.

All this is mostly spare time activity --- in my day job I design and implement software for railroad networks together with my wonderfu[l mtrail](https://www.mtrail.ch/) team, in Switzerland.  

**At some point, you created [javaalmanac.io](http://javaalmanac.io/), how did that come about?**   

As always, things started small --- I used a collection of markdown documents in a Github repo to keep track of recent Java features and prepare talks.

One day, I realized that this repo has more than 500 stars on GitHub. So, it appears to me that this collection of information seems to be somewhat useful.

That was the beginning of [javaalmanac.io](http://javaalmanac.io/).

**How do you get all that content, what exactly does it consist of?**   

Most of the content is actually collected by hand. While I already had lots of content from my presentations about the latest Java versions, I started digging in the past and also tried to collect information about historic Java versions. For example, did you know that Java 1.0.2 only had 8 packages and its API documentation (not yet Javadoc) comes with the best web design of the nineties with funny gifs as headers? Have a [look](https://javaalmanac.io/jdk/1.0/api/)!

As the repository became more and more popular, people started to contribute with pull request. Also, [Cay Horstmann](https://horstmann.com/) contributed technical articles about the latest Java features. And I created two tools that help me to keep track about recent development:

* For the website, I create API diffs between all Java versions. So I can keep an eye on new APIs that come in with every Java version. In the meantime, the process is mostly automated and also API diffs for early access version are available.   
* The other nice tool is an online compiler and executor for Java snippets. Want to try the latest Java 16 build without installing it? Just go [here](https://javaalmanac.io/jdk/16/).

**Maybe the data can be exposed as a REST endpoint?**   

Nice idea! While I started out with markdown files, over time I converted most of the content into JSON files. This makes the content machine readable and can be used in other contexts --- [like on foojay](https://foojay.io/almanac/jdk-8/). I really like the idea of sharing knowledge and data!

Currently, the JSON files can be simply obtained from the [GitHub repository](https://github.com/marchof/java-almanac/tree/master/site/data). The next step would to provide a proper API from that data.

I have to admit I do not have much experience with open data standards. Maybe you have a an idea or one of our readers?

**Which areas of the data would you most like collaboration on, i.e., what is most incomplete?**

I try to maintain a list of OpenJDK distributions from all vendors known to me. There are many versions and even more supported platforms. I think it is important to users to find distributions that fulfil their needs.

But I don't think I will be able to keep this matrix up-to-date in the long run. Would be really nice if vendors could provide artefact lists in machine readable formats or even update their product list on [javaalmanac.io](http://javaalmanac.io/), like Azul already did, recently.

**What are you happiest about, in relation to [javaalmanac.io](http://javaalmanac.io/)?**   

Simply the fact several collaborations were triggered with this little site. For example, writing articles with Cay Horstmann and now working together with foojay.

**Yes, and now it's hosted on foojay, too, what do you think about this direction?**   

For me this is wonderful proof of the concept that data and knowledge can be shared when information is provided in a well structured way and --- most importantly --- with an open license that allows the re-use of information.

And maybe [foojay is the place](https://foojay.io/blog/foojay-a-place-for-friends-of-openjdk/) where we, the Java community, will be collecting and maintaining all knowledge about OpenJDK!
