---
title: "How To Get Your JDK As Easily As Possible!"
slug: "get-your-jdk-as-easily-as-possible"
date: "2021-07-20T07:00:31+00:00"
lastmod: "2021-07-20T07:01:30+00:00"
description: "After creating the Disco API, I had the idea to create plugins for IDEs and browsers to enable more easy JDK downloading."
authors:
  - "gerrit-grunwald"
image: "https://foojay.io/wp-content/uploads/2021/07/discoduke.png"
categories:
  - "Eclipse"
  - "IntelliJ IDEA"
  - "JavaFX"
  - "VS Code"
tags:
related_posts:
  - "jdkmon-your-friendly-jdk-distribution-updater"
  - "java-on-azure-tooling-update-july-2022"
  - "will-openjfx-be-merged-into-openjdk-it-would-be-a-perfect-match-with-java-on-mobile"
  - "foojay-podcast-83"
frozen: false
---

Have you ever been in the situation where you've been looking for a specific JDK version of a specific distribution? I think most of us have been there and, I don't know how you handle this, but in the past I used to save links to distributions in my browser and then I usually I started searching through the vendor website for the package I was looking for.

Sometimes that was easy... sometimes it was hard... but it never was fun. And if you're used to working with JavaFX, the whole thing got even harder when JDK 11 came out and JavaFX was not bundled with the distribution any longer.

After creating the Disco API ("Universal OpenJDK Discovery API", in full) which serves up JDK distributions as a service, I had the idea to create plugins for IDEs to enable people to download the JDK of their choice more easily.

### **IDE Plugins** {#h3-0-ide-plugins}

Long story short, we created plugins for Intellij Idea, Eclipse, and Visual Studio Code. NetBeans even comes with bundled support for the Disco API.

To keep it as simple as possible, I've tried to reduce the user interface of the plugin to a minimum.

In principle, it is just a list of dropdown boxes that enables the user to drill down to the package of their choice. Let me take the IntelliJ IDEA plugin as an example:
![](/images/posts/2021/07/get-your-jdk-as-easily-as-possible/IntellijPlugin-354x510.png)

As you can see it is not a very fancy user interface but focused on functionality. 😁

You can see drop down boxes for the following fields:

* **Major version (e.g, 7, 8, 11, 13, 15, 16, 17, 18).** Contains the currently maintained versions, that is, versions that are still getting updates.
* **Version number (e.g., 11.0.11, 18-ea.6).** The list of available update levels for the selected major version.
* **Distribution (e.g., AOJ, Corretto, Dragonwell, JetBrains, Liberica, Microsoft, OJDK Build, OpenLogic, Oracle OpenJDK, SAP Machine, Temurin, Trava, Zulu, Zulu Prime).** Only shows distributions that have packages for the selected major version and update level.
* **Operating system (e.g., Windows, Linux, Mac and more).**
* **Libc type (e.g., libc, glibc, c std. lib, musl).** Depending on the operating system that is selected, shows the available libc types. Only for Linux there two choices, glibc or musl (which is also known as Alpine Linux).
* **Architecture (e.g., x64, aarch64, x86).** The available processor architecture for the current selection.
* **Archive type (zip, pkg, tar.gz etc).** The archive types that are available for the current selection.

If you would like look for JDK distributions that come bundled with JavaFX, you simply check the `JavaFX bundled` checkbox at the top of the user interface. Because there are just a few distributions that offer JDKs bundled with JavaFX, this will restrict the search to those distributions.

Once you have found the JDK of your choice, it will show you the selected file name under the dropdown boxes. If you press the `Download` button, you need to select a folder where the file should be downloaded to and after that the download will start.

The plugin will only let you download the JDK, the installation is still your job to do.

### **IDE Plugins with Links** {#h3-1-ide-plugins-with-links}

* Intellij Idea: [DiscoIdea](https://plugins.jetbrains.com/plugin/16787-discoidea)
* Eclipse: [DiscoEclipse](https://marketplace.eclipse.org/content/discoeclipse)
* Visual Studio Code: [DiscoVSC](https://marketplace.visualstudio.com/items?itemName=GerritGrunwald.discovsc&ssr=false#overview)
* NetBeans: Plugin comes bundeled with latest version

After I had finished those plugins, I thought again about the procedure of downloading a JDK and found myself mainly using the browser to do so.

So why not create plugins for the browsers to make it even easier to get a JDK?

### **Browser Plugins** {#h3-2-browser-plugins}

And so I did, there are now plugins for Chrome, Edge, Firefox, and Safari. And because I don't want to reinvent the wheel, I simply made them look exactly the same as the IDE plugins. Here is a screenshot that shows all four plugins in their browser:
![](/images/posts/2021/07/get-your-jdk-as-easily-as-possible/BrowserPlugins-700x295.jpg)

As you can see, they look more or less all the same and the functionality is the same as in the IDE plugins.

So for me this is now the easiest way to search for a JDK and download it via the browser.

### **Browser Plugins with Links** {#h3-3-browser-plugins-with-links}

* Chrome: [DiscoChrome](https://chrome.google.com/webstore/detail/discochrome/cikmnphhlggceijbbdeohhlkbdagjjce)
* Firefox: [DiscoFox](https://addons.mozilla.org/addon/discofox/)
* Safari: [DiscoSafari](https://apps.apple.com/de/app/discosafari/id1571307341?mt=12)
* Edge: [DiscoEdge](https://microsoftedge.microsoft.com/addons/detail/efeaimfkdbolmkhafogcoocbidfhdkcm)

With these plugins, you are able to search for a JDK and download it, which still leaves room for the question... which JDK do you have installed and is it the latest version?

Well for this problem, I've created JDKMon [which I already blogged about earlier](https://foojay.io/today/jdkmon-your-friendly-jdk-distribution-updater/). Please find the latest available version for your operating system [here](https://github.com/HanSolo/JDKMon/releases/latest "here").

That's it for today, I hope those tools will be helpful for you... so, keep coding!
