---
title: "Front End Debugging Part 1: Not just Console Log"
slug: "front-end-debugging-part-1-not-just-console-log"
date: "2024-11-05T14:01:45+00:00"
lastmod: "2024-12-03T16:31:07+00:00"
description: "Learn advanced techniques for debugging across the full stack, from frontend tools to system-level troubleshooting, streamline development."
canonical: "https://debugagent.com/front-end-debugging-part-1-not-just-console-log"
authors:
  - "shai-almog"
image: "bc0c6cc72cdd01372f0495cd7bdd464e.webp-copy.jpg"
categories:
  - "Debugging"
  - "Tutorials"
tags:
related_posts:
  - "front-end-debugging-part-2-console-log-to-the-max"
  - "debugging-kubernetes-part-1-an-introduction"
  - "debugging-tips-and-tricks-a-comprehensive-guide"
enlighterjs: true
frozen: false
---

* [**Instant Debugging with the** `debugger` Keyword](#instant-debugging-with-the-raw-debugger-endraw-keyword)
* [**Triggering Debugging from the Console**](#triggering-debugging-from-the-console)
* [**DOM Breakpoints: Monitoring DOM Changes**](#dom-breakpoints-monitoring-dom-changes)
* [**XHR Breakpoints: Uncovering Hidden Network Calls**](#xhr-breakpoints-uncovering-hidden-network-calls)
* [**Simulating Environments for Debugging**](#simulating-environments-for-debugging)
* [**Debugging Layout and Style Issues**](#debugging-layout-and-style-issues)
  * [Final Word](#final-word)

**As a Java developer, most of my focus is on the backend side of debugging. Front-end debugging poses different challenges and has sophisticated tools of its own. Unfortunately, print-based debugging has become the norm in front-end. To be fair, it makes more sense there as the cycles are different and the problem is always a single user problem. But even if you choose to use `Console.log`, there's a lot of nuance to pick up there.**

See video version of this post [here:](https://youtu.be/1KFlbecOmc0)

{{< youtube 1KFlbecOmc0 >}}

<br />

As a side note, if you like the content of this and the other posts in this series check out my [Debugging book](https://www.amazon.com/dp/1484290410/) that covers this subject. If you have friends that are learning to code I'd appreciate a reference to my [Java Basics book.](https://www.amazon.com/Java-Basics-Practical-Introduction-Full-Stack-ebook/dp/B0CCPGZ8W1/) If you want to get back to Java after a while check out my [Java 8 to 21 book](https://www.amazon.com/Java-21-Explore-cutting-edge-features/dp/9355513925/).

**Instant Debugging with the** `debugger` Keyword {#h2-0-instant-debugging-with-the-debugger-keyword}
-----------------------------------------------------------------------------------------------------

A cool yet powerful tool in JavaScript is the `debugger` keyword. Instead of simply printing a stack trace, we can use this keyword to launch the debugger directly at the line of interest. That is a fantastic tool that instantly brings your attention to a bug, I often use it in my debug builds of the front-end instead of just printing an error log.

**How to Use It:** Place the `debugger` keyword within your code, particularly within error-handling methods. When the code execution hits this line, it automatically pauses, allowing you to inspect the current state, step through code, and understand what's going wrong.

Notice that while this is incredibly useful during development, we must remember to remove or conditionally exclude `debugger` statements in production environments. A release build should not include these calls in a production site live environment.

**Triggering Debugging from the Console** {#h2-1-triggering-debugging-from-the-console}
---------------------------------------------------------------------------------------

Modern browsers allow you to invoke debugging directly from the console, adding an additional layer of flexibility to your debugging process.

**Example:** By using the `debug(functionName)` command in the console, you can set a breakpoint at the start of the specified function. When this function is subsequently invoked, the execution halts, sending you directly into the debugger.

```
function hello(name) {
    Console.log("Hello " + name)
}
debug(hello)
hello("Shai")
```


This is particularly useful when you want to start debugging without modifying the source code, or when you need to inspect a function that's only defined in the global scope.

**DOM Breakpoints: Monitoring DOM Changes** {#h2-2-dom-breakpoints-monitoring-dom-changes}
------------------------------------------------------------------------------------------

DOM breakpoints are an advanced feature in Chrome and Firebug (Firefox plugin) that allow you to pause execution when a specific part of the DOM is altered.

To use it we can right-click on the desired DOM element, select "Break On," and choose the specific mutation type you are interested in (e.g., subtree modifications, attribute changes, etc.).

![Subtree modification](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/tnhcf65djlyctm44fjxr.png)

DOM breakpoints are extremely powerful for tracking down issues where DOM manipulation causes unexpected results, such as dynamic content loading or changes in the user interface that disrupt the intended layout or functionality. Think of them like field breakpoints we discussed in the past.

These breakpoints complement traditional line and conditional breakpoints, providing a more granular approach to debugging complex front-end issues. This is a great tool to use when the DOM is manipulated by an external dependency.

**XHR Breakpoints: Uncovering Hidden Network Calls** {#h2-3-xhr-breakpoints-uncovering-hidden-network-calls}
------------------------------------------------------------------------------------------------------------

Understanding who initiates specific network requests can be challenging, especially in large applications with multiple sources contributing to a request. XHR (`XMLHttpRequest`) breakpoints provide a solution to this problem.

![XHR Breakpoint](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/g7ojrpu5d225e69qoc46.png)

In Chrome or Firebug, set an XHR breakpoint by specifying a substring of the URI you wish to monitor. When a request matching this pattern is made, the execution stops, allowing you to investigate the source of the request.

This tool is invaluable when dealing with dynamically generated URIs or complex flows where tracking the origin of a request is not straightforward.

Notice that you should be selective with the filters you set; leaving the filter blank will cause the breakpoint to trigger on all XHR requests, which can become overwhelming.

**Simulating Environments for Debugging** {#h2-4-simulating-environments-for-debugging}
---------------------------------------------------------------------------------------

Sometimes, the issues you need to debug are specific to certain environments, such as mobile devices or different geographical locations. Chrome and Firefox offer several simulation tools to help you replicate these conditions on your desktop.

* **Simulating User Agents:** Change the browser's user agent to mimic different devices or operating systems. This can help you identify platform-specific issues or debug server-side content delivery that varies by user agent.
* **Geolocation Spoofing:** Modify the browser's reported location to test locale-specific features or issues. This is particularly useful for applications that deliver region-specific content or services.
* **Touch and Device Orientation Emulation:** Simulate touch events or change the device orientation to see how your application responds to mobile-specific interactions. This is crucial for ensuring a seamless user experience across all devices.

These are things that are normally very difficult to reproduce. E.g. touch related issues are often challenging to debug on the device. By simulating them on the desktop browser we can shorten the debug cycle and use the tooling available on the desktop.

**Debugging Layout and Style Issues** {#h2-5-debugging-layout-and-style-issues}
-------------------------------------------------------------------------------

CSS and HTML bugs can be particularly tricky, often requiring a detailed examination of how elements are rendered and styled.

![Inspect element](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/n1u9cdoost95glyce2it.png)

**Inspect Element:** The "inspect element" tool is the cornerstone of front-end debugging, allowing you to view and manipulate the DOM and CSS in real-time. As you make changes, the page updates instantly, providing immediate feedback on your tweaks.

**Addressing Specificity Issues:** One common problem is CSS specificity, where a more specific selector overrides the styles you intend to apply. The inspect element view highlights overridden styles, helping you identify and resolve conflicts.

**Firefox vs. Chrome:** While both browsers offer robust tools, they have different approaches to organizing these features. Firefox's interface may seem more straightforward, with fewer tabs, while Chrome organizes similar tools under various tabs, which can either streamline your workflow or add complexity, depending on your preference.

### Final Word {#h3-6-final-word}

There are many front-end tools that I want to discuss in the coming posts. I hope you picked up a couple of new debugging tricks in this first part.

Front-end debugging requires deep understanding of browser tools and JavaScript capabilities. By mastering the techniques outlined in this post---instant debugging with the `debugger` keyword, DOM and XHR breakpoints, environment simulation, and layout inspection---you can significantly enhance your debugging efficiency and deliver more robust, error-free web applications.
