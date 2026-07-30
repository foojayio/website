---
title: "Challenges when Developing a GUI for FIX"
slug: "challenges-when-developing-a-gui-for-fix"
date: "2022-12-21T08:06:18+00:00"
lastmod: "2022-12-21T10:36:50+00:00"
description: "In this article, we explore the challenges in developing a Graphical User Interface (GUI) for Financial Information Exchange (FIX) data."
authors:
  - "rob-austin"
image: "/images/posts/2022/12/challenges-when-developing-a-gui-for-fix/Screen-Shot-2022-12-13-at-2.42.46-PM.png"
categories:
  - "Developer Tools"
  - "Java Core"
  - "Uncategorized"
tags:
related_posts:
  - "chronicle-queue-storing-1tb-in-virtual-memory-on-a-128gb-machine"
  - "creating-terabyte-sized-queues-with-low-latency"
  - "high-performance-java-serialisation"
enlighterjs: true
frozen: false
---

In this article, we explore the challenges in developing a Graphical User Interface (GUI) for Financial Information Exchange (FIX) data.

FIX is both a protocol and a message format, but to create a FIX GUI we will focus just on the message format.

A FIX message is a standard message format for transmitting financial and investment banking data.

Below is an example of a FIX message, where the "\|" character is sent as '\\1':

8=FIX.4.2\|9=196\|35=X\|49=A\|56=B\|34=12\|52=20100318-03:21:11.364\|262=A\|26 8=2\|279=0\|269=0\|278=BID\|55=EUR/USD\|270=1.37215\|15=EUR\|271=2500000\|346= 1\|279=0\|269=1\|278=OFFER\|55=EUR/USD\|270=1.37224\|15=EUR\|271=2503200\|346= 1\|10=171\|

It could be said that the FIX format balances the compactness of data with its readability, but I feel that FIX messages are neither particularly compact nor human readable. So our first challenge was to come up with a design to present FIX data clearly and intuitively.

Taking inspiration from targetcompid.com we realised that before we create a commercial FIX GUI, we could first offer a free-to-use [FIX parser](https://fixparser.chronicle.software/ "FIX parser"), which can display any FIX message in a human readable form.

Let's start with the FIX Parser:  
![](/images/posts/2022/12/challenges-when-developing-a-gui-for-fix/Screen-Shot-2022-12-13-at-2.36.08-PM-1024x765.png)

*The user can paste in a FIX message, click parse, and it will be translated below.*

With the shift to the cloud, and the ability to display rich web-based functionality, we decided to render our UI in the browser rather than creating a local runnable executable. We selected [React](https://reactjs.org/ "React") for our front end technology and Java as the backend.

For the FIX parsing we used an existing FIX engine. FIX engines are FIX protocol translators for business messages used by the financial and investment banking industry.

They usually receive their data directly from a TCP/IP connection, then adapt the messages into a form that can be easily read by the organization's business logic. Most FIX engines are written in C++ or Java. I've been working on [Chronicle FIX](https://chronicle.software/fix-engine/ "Chronicle FIX") for the last 5 years so for me Chronicle FIX was the obvious choice, but using it presented me with a bit of a challenge.

FIX Engines are usually fail-fast. If there is anything invalid in the FIX message, the protocol mandates that rejects are sent or [TCP/IP](https://en.wikipedia.org/wiki/Internet_protocol_suite "TCP/IP") connection are dropped. On the flip side, a UI should make best efforts to display what it is given. Our first challenge was to adapt Chronicle FIX to relax the standard message validation.

Once we were able to read the raw FIX messages, and even those occasional invalid FIX messages, we then had to convert the data into a format that could be read by the UI layer.

We selected JSON as it could be easily parsed by JavaScript, using open-source [Chronicle Wire](https://chronicle.software/wire/ "Chronicle Wire") to convert our Java DTOs to JSON wire. Whilst under development, we added a button on the web page to view the JSON message from the server.

This was initially meant as a temporary feature to assist us with debugging, but we realised how useful this may be to users, who are often developers themselves and would like access to the underlying JSON data.

![](/images/posts/2022/12/challenges-when-developing-a-gui-for-fix/Screen-Shot-2022-12-13-at-2.38.52-PM-1024x263.png)

#### Enums

When it came to handling enums we had to extend chronicle FIX to send out additional fields to reflect the enums. In FIX, enum encoding sets a number for both the field name and the enum value:

`54=1`

A BUY trade is written as "54=1", where the field number 54 represents the field name "side" and the "1" represents the BUY

`"side": "1",`

Or if the value is a "2" then it is a SELL, hence 54=2,There are other variants of the side enum in FIX a full list can be seen [here](https://www.onixs.biz/fix-dictionary/4.2/tagnum_54.html "here"). While it is useful to show the "1" as this was what was actually in the raw fix message, it is not exactly easy to read. We added an additional description field, which we called our "_Desc" fields to represent a human-readable form of each enum.

`"side_Desc": "BUY"`

we display both in the browser like this:

![](/images/posts/2022/12/challenges-when-developing-a-gui-for-fix/Screen-Shot-2022-12-13-at-2.42.05-PM-1024x215.png)

#### Repeating Groups

Another challenge was repeating groups, without going into the FIX message format in too much detail (and trust me the specification gets very detailed very quickly), a repeating group is a list of order fields that repeat.

Repeating groups can repeat inside other repeating groups.

![](/images/posts/2022/12/challenges-when-developing-a-gui-for-fix/Screen-Shot-2022-12-13-at-2.42.46-PM-873x1024.png)

One of the reasons we selected JSON is it can handle this type of hierarchical data structure with ease.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">[
  {
    "msgType": 88,
    "msgType_Desc": "MarketDataIncrementalRefresh",
    "senderCompID": "A",
    "targetCompID": "B",
    "msgSeqNum": 12,
    "sendingTime": "2010-03-18T03:21:11.364",
    "mDReqID": "A",
    "marketDataIncrementalRefresh_MDEntriesGrp_1s": [
      {
        "mDUpdateAction": "0",
        "mDEntryType": "0",
        "mDEntryID": "BID",
        "symbol": "EUR/USD",
        "mDEntryPx": 1.37215,
        "currency": "EUR",
        "mDEntrySize": 2500000,
        "numberOfOrders": 1,
        "mDUpdateAction_Desc": "NEW",
        "mDEntryType_Desc": "BID"
      },
      {
        "mDUpdateAction": "0",
        "mDEntryType": "1",
        "mDEntryID": "OFFER",
        "symbol": "EUR/USD",
        "mDEntryPx": 1.37224,
        "currency": "EUR",
        "mDEntrySize": 2503200,
        "numberOfOrders": 1,
        "mDUpdateAction_Desc": "NEW",
        "mDEntryType_Desc": "OFFER"
      }
    ],
    "noMDEntries": 2,
    "msg": 
"8=FIX.4.2|9=196|35=X|49=A|56=B|34=12|52=20100318-03:21:11.364|262=A|2
68=2|279=0|269=0|278=BID|55=EUR/USD|270=1.37215|15=EUR|271=2500000|346
=1|279=0|269=1|278=OFFER|55=EUR/USD|270=1.37224|15=EUR|271=2503200|346
=1|10=171|"
  }
]</pre>

#### Undertow

We selected Undertow as our web-server because it is relatively lightweight and easy to develop with.

*Below is a snippet of Java code that uses Undertow. I added this Java code to peak your interest, but going into this is out of the scope of this article.*

<pre class="EnlighterJSRAW" data-enlighter-language="java">PathHandler pathHandler = path()
        .addExactPath(prefix + "/fixparser", disableCache(new 
EagerFormParsingHandler(main::doFixParser)))

if (prefix.length() &gt; 0) {
   pathHandler.addPrefixPath("/", resource(new FileResourceManager(new File("./html/" + basePath))));
}

HttpHandler handler = header(pathHandler, "Access-Control-Allow-Origin", "*");
Undertow server = Undertow.builder().addHttpListener(port, 
"localhost", handler).build();
server.start();</pre>

#### Finally

It's worth mentioning that a fully-fledged FIX GUI includes session management, ability to add new FIX connections or sessions to other FIX engines and a lot of other functionality that I chose to not cover in this article.

#### Try it Out

You can try out the fix parser at <https://fixparser.chronicle.software>.

It would be great to hear your feedback as we are continually looking to make improvements.
