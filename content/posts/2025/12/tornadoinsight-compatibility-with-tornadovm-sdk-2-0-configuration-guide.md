---
title: "TornadoInsight - Compatibility with TornadoVM SDK 2.0+ & Configuration Guide"
slug: "tornadoinsight-compatibility-with-tornadovm-sdk-2-0-configuration-guide"
date: "2025-12-30T09:00:00+00:00"
lastmod: "2025-12-30T15:18:44+00:00"
description: "This blog updates the previously published TornadoInsight configuration guidelines and explains how to configure the required environment variable to - by Thanos Stratikopoulos"
authors:
  - "thanos-stratikopoulos"
image: "/images/posts/2025/12/tornadoinsight-compatibility-with-tornadovm-sdk-2-0-configuration-guide/tornado-insight.webp"
categories:
  - "IntelliJ IDEA"
  - "Tools"
  - "TornadoVM"
tags:
related_posts:
  - "build-and-run-tornadovm-with-intellij-idea"
  - "ask-a-lille-dev-what-java-developers-really-think-about-quality-frameworks-communities-and-careers"
  - "introducing-the-boxlang-ide-plugin-for-intellij"
  - "foojay-podcast-91"
enlighterjs: true
frozen: false
---

<figure class="wp-block-image size-full is-resized">
 <img fetchpriority="high" decoding="async" width="298" height="298" src="/images/posts/2025/12/tornadoinsight-compatibility-with-tornadovm-sdk-2-0-configuration-guide/tornado-insight.webp" alt="" class="wp-image-106112" style="width:138px;height:auto">
</figure>

This blog updates the previously published **TornadoInsight** configuration [**++guidelines++**](https://www.tornadovm.org/post/introducing-tornadoinsight-unleashing-the-power-of-tornadovm-in-intellij-idea) and explains how to configure the required environment variable to ensure that TornadoInsight correctly detects the TornadoVM SDK when IntelliJ IDEA is launched from a graphical environment.

*** ** * ** ***

**Overview** {#6bbq84970}
-------------------------

TornadoInsight requires access to a compatible Java Development Kit (JDK) and the TornadoVM SDK.{#o7o5c20810}

**Important update:**{#mvwo920904}

Since version [++v1.4.0++](https://github.com/beehive-lab/tornado-insight/releases/tag/v1.4.0), TornadoInsight can also use the **JDK configured directly in IntelliJ IDEA** via **Project Structure**. This means that, in many cases, users no longer need to configure the Java SDK in the plugins settings.{#welj420814}

*** ** * ** ***

JDK Configuration (Recommended) {#kj3lj29712}
---------------------------------------------

TornadoInsight supports using the JDK defined in the IntelliJ project settings.{#w8y9b29714}

### Configure the JDK in IntelliJ IDEA {#ev5aq29716}

1. Open **IntelliJ IDEA**
2. Go to **File → Project Structure**
3. Select **Project** under **Project Settings**
4. Set **Project SDK** to **JDK 21** (or another supported JDK version)
5. Apply and close the dialog

{#o658529718}

When a compatible JDK (e.g., JDK 21) is configured here, TornadoInsight will automatically use it.{#fibw029743}  

<figure class="aligncenter size-large is-resized">
 <img decoding="async" src="https://plugins.jetbrains.com/files/23309/screenshot_bd38c017-be77-49d6-a5b0-f9ca54069cd9" alt="" style="width:738px;height:auto">
</figure>

*** ** * ** ***

TornadoVM SDK Configuration (Recommended) {#keesm31250}
-------------------------------------------------------

Since version [++v1.4.2++](https://github.com/beehive-lab/tornado-insight/releases/tag/v1.4.2), TornadoInsight requires the environment variable **TORNADOVM_HOME** to be defined and visible to the IntelliJ IDEA process.{#b74ch20726}

On modern operating systems, GUI applications (such as IntelliJ started from JetBrains Toolbox, the Dock, or an application launcher) **do not always inherit shell variables** defined in files like:{#goqx633285}

* \~/.bashrc
* \~/.zshrc

{#8ku9a4982}

For this reason, **TORNADOVM_HOME** must be configured using OS-appropriate mechanisms that apply to the **graphical session**, not only to terminal shells.{#auwk84989}

To define the TornadoVM SDK path across different Operating Systems, you can use the following guidelines which will enable your IntelliJ session to recognize the **TORNADOVM_HOME** environment variable as described below.{#iju4t56513}

<pre class="EnlighterJSRAW" data-enlighter-language="raw" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">TORNADOVM_HOME=/absolute/path/to/tornado-sdk</pre>

*** ** * ** ***

### **macOS** {#i80s15288}

On macOS, GUI applications do **not** read \~/.zshrc or \~/.zprofile.{#g8z335290}

#### **Recommended:** {#kghgn5648}

Run the following command in a terminal:{#ip1ei5300}

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">launchctl setenv TORNADOVM_HOME /absolute/path/to/tornado-sdk</pre>

Then **quit IntelliJ IDEA** (Cmd + Q) and relaunch it.

*** ** * ** ***

### Linux {#xbi0p5638}

If IntelliJ IDEA is launched from **JetBrains Toolbox** or a desktop launcher, variables set in \~/.bashrc will **not** be visible.{#xmt6z5640}

#### **Recommended: systemd** **environment.d** {#ted4t6250}

This method works reliably on modern Linux distributions using systemd.{#b9wg15651}

* Create the directory
* Create a configuration file (tornado.conf)
* Add the variable (no export)
* Log out and log back in, then start IntelliJ IDEA

{#ds8xx5653}

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">mkdir -p ~/.config/environment.d
vim ~/.config/environment.d/tornado.conf
TORNADOVM_HOME=/absolute/path/to/tornado-sdk</pre>

*** ** * ** ***

### **Windows** {#x9em06931}

1. Open **System Properties**
2. Navigate to **Environment Variables**
3. Add a **User variable** :
   * Name: TORNADOVM_HOME
   * Value: C:\\path\\to\\tornado-sdk
4. Restart IntelliJ IDEA

{#d8w276933}

*** ** * ** ***

**Verification** {#ja8uq7253}
-----------------------------

Before starting IntelliJ IDEA, verify that the variable is set:{#hgeqi7255}

### **macOS / Linux** {#k9yq97257}

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">launchctl getenv TORNADOVM_HOME</pre>

**Linux (systemd check)**

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">systemctl --user show-environment | grep TORNADOVM_HOME</pre>

If the variable is present, TornadoInsight will automatically detect the TornadoVM SDK at startup.

*** ** * ** ***

**Summary** {#dschd9476}
------------------------

If **TORNADOVM_HOME** is not recognized by the IntelliJ process, a warning is shown in the settings of the plugin, as shown in the picture below.{#wo6nn88008}  
![](https://plugins.jetbrains.com/files/23309/screenshot_aa1940fc-c2d6-4120-95fc-2df46fc3d67f)

Some common issues are listed here:{#1pehk136703}

* Setting **TORNADOVM_HOME** only in \~/.bashrc or \~/.zshrc
* Forgetting to log out or restart IntelliJ IDEA
* Using relative paths instead of absolute paths
* Expecting GUI-launched applications to inherit terminal environments

{#vuasm55650}

This blog presents some guideline to address these issues across different operating systems.{#yvef2138600}

Useful Links:  

TornadoVM: <https://www.tornadovm.org/>{#7s1my157954}

JetBrains Marketplace: [++https://plugins.jetbrains.com/plugin/23309-tornadoinsight++](https://plugins.jetbrains.com/plugin/23309-tornadoinsight){#7s1my157954}

GitHub Repository: [++https://github.com/beehive-lab/tornado-insight++](https://github.com/beehive-lab/tornado-insight){#7s1my157954}

This work has been supported by research funding from the European Union ([++P2Code++](https://p2code-project.eu/), [++AERO++](aero-project.eu)).
