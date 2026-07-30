---
title: "BoxLang RSS : Full-Featured RSS/Atom Feed Module for BoxLang"
slug: "boxlang-rss-full-featured-rss-atom-feed-module-for-boxlang"
date: "2025-12-02T11:19:11+00:00"
lastmod: "2025-12-02T11:44:24+00:00"
description: "We're thrilled to announce the release of bx-rss, a comprehensive RSS and Atom feed module that brings powerful syndication capabilities to BoxLang! - by Cristobal Escobar"
authors:
  - "cristobal-escobar"
image: "https://foojay.io/wp-content/uploads/2025/10/BoxLang-Logo-Dark.png"
categories:
  - "BoxLang"
  - "Developer Tools"
  - "Java"
  - "Release Notes"
  - "Tools"
  - "Tutorials"
  - "Use Cases"
tags:
related_posts:
  - "whats-new-in-the-july-2026-azul-payara-release"
  - "boxlang-aws-azure-and-google-secrets-manager-module-released"
  - "virtual-thread-pinning-field-guide"
  - "boxlang-1-15-0-released-blazing-fast-strings-runtime-portability-and-much-more"
enlighterjs: true
frozen: false
---

![](/images/posts/2025/12/boxlang-rss-full-featured-rss-atom-feed-module-for-boxlang/bx-rss-700x467.jpg)

We're thrilled to announce the release of `bx-rss`, a comprehensive RSS and Atom feed module that brings powerful syndication capabilities to [BoxLang](https://www.boxlang.io/# "BoxLang")! Whether you're building a content aggregator, publishing a podcast, or exposing your application's data as feeds, `bx-rss` makes it incredibly simple.

Thanks To Ray Camden {#h2-0-thanks-to-ray-camden}
-------------------------------------------------

I just want to thank you to [Ray Camden](https://www.raymondcamden.com/ "Ray Camden") for his contributions to this module. Thanks Ray! You rock!

Why bx-rss? {#h2-1-why-bx-rss}
------------------------------

In today's interconnected web, RSS and Atom feeds remain crucial for content distribution and syndication. From blog posts and news articles to podcasts and video channels, feeds power how content flows across the internet. The bx-rss module brings enterprise-grade feed capabilities to BoxLang with an elegant, easy-to-use API.

✨ Key Features at a Glance {#h2-2-key-features-at-a-glance}
-----------------------------------------------------------

📖 **Universal Feed Reading** - Parse RSS 2.0, RSS 1.0 (RDF), and Atom feeds from URLs or files  

✍️ **Feed Creation** - Generate professional RSS 2.0 and Atom feeds with full metadata support  

🎙️ **iTunes Podcast Support** - Auto-detect and parse iTunes podcast extensions (23+ additional fields)  

📹 **Media RSS Extensions** - Automatically include thumbnails, media content, and player information  

🔄 **Multi-Source Aggregation** - Read and merge items from multiple feed URLs simultaneously  

🎯 **Smart Filtering** - Apply custom filters and pagination with `maxItems`  

🔌 **Flexible Output** - Return as structs, save to files, or get raw XML  

🔁 **CFML Compatible** - Provides similar functionality to CFML's `cffeed` tag.  

🏢 **Enterprise Ready** - Built and supported by Ortus Solutions

🚀 Installation {#h2-3-installation}
------------------------------------

Getting started is as simple as running one command:

<pre class="EnlighterJSRAW" data-enlighter-language="java">box install bx-rss</pre>

The module automatically registers as `bxrss` in your BoxLang applications. That's it!

💡 Quick Start Examples {#h2-4-quick-start-examples}
----------------------------------------------------

Reading a Feed (The Easy Way)  

The simplest way to read an RSS feed is with the `rss()` BIF:

<pre class="EnlighterJSRAW" data-enlighter-language="java">// Read any RSS/Atom feed with one line
feedData = rss( "https://news.ycombinator.com/rss" );

println( "Found #feedData.items.size()# items" );
println( "Feed: #feedData.channel.title#" );

// Loop through items
feedData.items.each( ( item ) =&gt; {
    println( "→ #item.title#" );
    println( "  #item.link#" );
    println();
} );</pre>

#### Using the Component for More Control

For advanced scenarios, use the `, ``bx:feed` component:

<pre class="EnlighterJSRAW" data-enlighter-language="java">bx:feed 
    action="read"
    source="https://example.com/feed.xml"
    result="feedData"
    maxItems="10";

println( "Latest 10 items from #feedData.channel.title#" );</pre>

🎙️ Podcast Power: iTunes Extension Support {#h2-5-podcast-power-itunes-extension-support}
------------------------------------------------------------------------------------------

One of the most powerful features of bx-rss is its automatic iTunes podcast extension detection. When you read a podcast feed, the module automatically includes all iTunes-specific fields without any configuration:

<pre class="EnlighterJSRAW" data-enlighter-language="java">// Read a podcast feed
bx:feed 
    action="read"
    source="https://feeds.simplecast.com/54nAGcIl"
    result="podcast";

println( "Podcast: #podcast.channel.title#" );
println( "Author: #podcast.channel.itunesAuthor#" );
println( "Categories: #podcast.channel.itunesCategories.toList()#" );

// Access rich episode metadata
podcast.items.each( ( episode ) =&gt; {
    println( "🎧 #episode.itunesTitle#" );
    println( "   Duration: #episode.itunesDuration#" );
    println( "   Season #episode.itunesSeason# Episode #episode.itunesEpisode#" );
    println( "   Type: #episode.itunesEpisodeType#" );
    println();
} );</pre>

#### Available iTunes Fields

**Channel Level:**

* Author, subtitle, summary
* Artwork/image URL
* Content rating (explicit)
* Categories
* Owner name and email

#### Episode Level:

* Episode title, duration
* Season and episode numbers
* Episode type (full, trailer, bonus)
* Individual artwork
* Author and summary

📹 Media RSS: Video \& Image Feeds {#h2-6-media-rss-video-image-feeds}
----------------------------------------------------------------------

Media RSS extensions are also automatically detected, making it perfect for video platforms and image galleries:

<pre class="EnlighterJSRAW" data-enlighter-language="java">bx:feed 
    action="read"
    source="https://vimeo.com/channels/staffpicks/videos/rss"
    result="videos";

videos.items.each( ( video ) =&gt; {
    println( "📺 #video.title#" );

    if ( !isNull( video.mediaThumbnail ) ) {
        println( "   Thumbnail: #video.mediaThumbnail.url#" );
        println( "   Size: #video.mediaThumbnail.width#x#video.mediaThumbnail.height#" );
    }
    println();
} );</pre>

🔄 Feed Aggregation: Read Multiple Sources {#h2-7-feed-aggregation-read-multiple-sources}
-----------------------------------------------------------------------------------------

Build a feed aggregator by reading multiple sources simultaneously:

<pre class="EnlighterJSRAW" data-enlighter-language="java">sources = [
    "https://blog.ortussolutions.com/feed",
    "https://www.forgebox.io/blog/rss", 
    "https://news.boxlang.io/feed.xml"
];

bx:feed 
    action="read"
    source=sources
    result="aggregated"
    maxItems="20";

println( "Aggregated #aggregated.items.size()# items from #sources.size()# feeds" );

// Items are automatically sorted by date across all feeds
aggregated.items.each( ( item ) =&gt; {
    println( "[#item.feed#] #item.title#" );
} );</pre>

✍️ Creating Feeds: Share Your Content {#h2-8-creating-feeds-share-your-content}
-------------------------------------------------------------------------------

Generate professional RSS feeds from your data with ease:

<pre class="EnlighterJSRAW" data-enlighter-language="java">// Define your feed metadata
feedProps = {
    "version" : "rss_2.0",
    "title" : "My Tech Blog",
    "link" : "https://myblog.com",
    "description" : "Latest articles on BoxLang and web development",
    "publishedDate" : now()
};

// Your feed items
feedItems = [
    {
        "title" : "Getting Started with BoxLang",
        "link" : "https://myblog.com/getting-started-boxlang",
        "description" : "Learn the basics of BoxLang in this comprehensive guide",
        "publishedDate" : now(),
        "author" : "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="1470716271787b64716654796d76787b733a777b79">[email&nbsp;protected]</a>"
    },
    {
        "title" : "Building REST APIs with BoxLang",
        "link" : "https://myblog.com/rest-apis-boxlang",
        "description" : "Create powerful REST APIs using BoxLang",
        "publishedDate" : dateAdd( "d", -1, now() ),
        "author" : "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="67030211020b08170215270a1e050b08004904080a">[email&nbsp;protected]</a>"
    }
];

// Generate the feed
bx:feed 
    action="create"
    properties=feedProps
    data=feedItems
    outputFile="/var/www/feeds/blog.xml"
    overwrite="true"
    xmlVar="feedXml";

println( "✅ Feed created with #feedItems.size()# items" );</pre>

🎙️ Create Your Own Podcast Feed {#h2-9-create-your-own-podcast-feed}
---------------------------------------------------------------------

Launch your podcast with full iTunes support:

<pre class="EnlighterJSRAW" data-enlighter-language="java">podcastProps = {
    "version" : "rss_2.0",
    "title" : "BoxLang Bytes",
    "link" : "https://podcast.boxlang.io",
    "description" : "Weekly insights into BoxLang development",
    "publishedDate" : now(),
    // iTunes extensions
    "itunesAuthor" : "Ortus Solutions",
    "itunesSubtitle" : "BoxLang Development Podcast",
    "itunesSummary" : "Join us for weekly discussions about BoxLang features, best practices, and community news",
    "itunesImage" : "https://podcast.boxlang.io/artwork.jpg",
    "itunesExplicit" : "false",
    "itunesCategories" : [ "Technology", "Software How-To" ]
};

episodes = [
    {
        "title" : "Episode 1: Welcome to BoxLang",
        "link" : "https://podcast.boxlang.io/episode-1",
        "description" : "Introduction to BoxLang and what makes it special",
        "publishedDate" : now(),
        "author" : "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="5f2f303b3c3e2c2b1f3d3027333e3138713630">[email&nbsp;protected]</a>",
        // iTunes episode metadata
        "itunesTitle" : "Welcome to BoxLang",
        "itunesDuration" : "00:42: 15",
        "itunesEpisode" : "1",
        "itunesSeason" : "1",
        "itunesEpisodeType" : "full",
        // Audio enclosure
        "enclosure" : {
            "url" : "https://podcast.boxlang.io/episodes/episode-1.mp3",
            "type" : "audio/mpeg",
            "length" : "42000000"
        }
    }
];

bx:feed 
    action="create"
    properties=podcastProps
    data=episodes
    outputFile="/var/www/feeds/podcast.xml"
    overwrite="true";

println( "🎙️ Podcast feed created!" );</pre>

📊 Create Feeds from Database Queries {#h2-10-create-feeds-from-database-queries}
---------------------------------------------------------------------------------

Generate feeds directly from your database:

<pre class="EnlighterJSRAW" data-enlighter-language="java">// Query your content
posts = queryExecute( 
    "SELECT title, url, content, published_date, author_email
     FROM blog_posts  
     WHERE status = 'published'
     ORDER BY published_date DESC
     LIMIT 50",
    []
);

feedProps = {
    "version" : "rss_2.0",
    "title" : "Company Blog",
    "link" : "https://company.com/blog",
    "description" : "Latest news and updates from our team"
};

// Map query columns to feed fields
columnMap = {
    "title" : "title",
    "link" : "url",
    "description" : "content",
    "publishedDate" : "published_date",
    "author" : "author_email"
};

bx:feed 
    action="create"
    properties=feedProps
    data=posts
    columnMap=columnMap
    outputFile="/var/www/public/feed.xml"
    overwrite="true";</pre>

🎯 Flexible Output Options {#h2-11-flexible-output-options}
-----------------------------------------------------------

The module gives you multiple ways to access feed data:

<pre class="EnlighterJSRAW" data-enlighter-language="java">bx:feed 
    action="read"
    source="https://example.com/feed.xml"
    result="fullFeed"          // Complete struct with items &amp; channel
    properties="metadata"       // Channel metadata only
    query="itemsQuery"         // Items as a query object
    xmlVar="rawXml"            // Original XML string
    outputFile="/tmp/cache.xml" // Save to file for caching
    overwrite="true";

// Access data however you need it
println( "Full structure: #fullFeed.items.size()# items" );
println( "Feed title: #metadata.title#" );
println( "Query rows: #itemsQuery.recordCount#" );
println( "XML length: #rawXml.len()# chars" );</pre>

🔌 MCP Server Integration {#h2-12-mcp-server-integration}
---------------------------------------------------------

Want even more power? Connect to our **Model Context Protocol (MCP) server** for enhanced documentation and AI-assisted development:

**MCP Server URL** : [https://boxlang.ortusbooks.com/\~gitbook/mcp](https://boxlang.ortusbooks.com/readme/release-history/1.6.0#boxlang-documentation-mcp-server "https://boxlang.ortusbooks.com/~gitbook/mcp")

This gives you:

* Interactive documentation
* AI-powered code examples
* Real-time assistance
* Context-aware suggestions

📚 Complete Documentation {#h2-13-complete-documentation}
---------------------------------------------------------

The bx-rss module is fully documented with extensive examples, best practices, and troubleshooting guides:

**Documentation** : [https://boxlang.ortusbooks.com/boxlang-framework/modularity/rss](https://boxlang.ortusbooks.com/boxlang-framework/modularity/rss "https://boxlang.ortusbooks.com/boxlang-framework/modularity/rss")

Topics covered:

* Complete API reference
* iTunes podcast guide
* Media RSS usage
* Feed creation best practices
* Performance optimization
* Security considerations
* Troubleshooting common issues

🎯 Use Cases {#h2-14-use-cases}
-------------------------------

The bx-rss module is perfect for:

* 📰 **News Aggregators** - Combine multiple news sources
* 🎙️ **Podcast Platforms** - Host and distribute podcast feeds
* 📝 **Blog Syndication** - Share your content across platforms
* 📺 **Video Platforms** - Distribute video content via RSS
* 🔔 **Notification Systems** - Monitor feeds for updates
* 📱 **Mobile App Backends** - Provide feed data to apps
* 🏢 **Enterprise Content** - Syndicate internal content
* 🤖 **Automation** - Trigger workflows from feed updates

✨ Why Choose bx-rss? {#h2-15-why-choose-bx-rss}
-----------------------------------------------

#### Enterprise-Grade Quality

Built by Ortus Solutions with years of experience in CFML and BoxLang development. Production-ready, well-tested, and actively maintained.

#### Automatic Intelligence

Smart auto-detection of iTunes and Media RSS extensions means you get rich metadata without configuration.

#### Developer-Friendly API

Simple for basic tasks, powerful for advanced scenarios. Whether you use the BIF or component, you get a clean, intuitive interface.

#### CFML Compatibility

Drop-in replacement for `cffeed` - migrate existing applications with minimal changes.

#### Future-Proof

Built on BoxLang's modern architecture with continued support and feature updates.

🚀 Get Started Today {#h2-16-get-started-today}
-----------------------------------------------

<pre class="EnlighterJSRAW" data-enlighter-language="java"># Install the module
box install bx-rss

# Start using it immediately
feedData = rss( "https://example.com/feed.xml" );
println( feedData.items.size() );</pre>

📞 Support \& Community {#h2-17-support-community}
--------------------------------------------------

* **BoxLang+/++** : [https://boxlang.io/plans](https://boxlang.io/plans "https://boxlang.io/plans")
* **Documentation** : [https://boxlang.ortusbooks.com/boxlang-framework/modularity/rss](https://boxlang.ortusbooks.com/boxlang-framework/modularity/rss "https://boxlang.ortusbooks.com/boxlang-framework/modularity/rss")
* **GitHub** : [github.com/ortus-boxlang/bx-rss](https://github.com/ortus-boxlang/bx-rss "github.com/ortus-boxlang/bx-rss")
* **BoxLang Community** : [https://community.ortussolutions.com/](https://community.ortussolutions.com/ "https://community.ortussolutions.com/")
* **Ortus Solutions** : [ortussolutions.com](https://www.ortussolutions.com/ "ortussolutions.com")

🎉 Final Thoughts {#h2-18-final-thoughts}
-----------------------------------------

The bx-rss module brings professional RSS and Atom feed capabilities to BoxLang, making it easier than ever to integrate syndication into your applications. Whether you're reading feeds, creating podcasts, or building a content aggregator, bx-rss has you covered.

We can't wait to see what you build with it! Share your projects with us on the BoxLang community forums or tag us on social media.

Happy coding! 🚀
