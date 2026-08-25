---
title: "TornadoInsight - Compatibility with TornadoVM SDK 2.0+ & Configuration Guide"
date: "2025-12-30T09:00:00+00:00"
lastmod: "2025-12-30T15:18:44+00:00"
description: "This blog updates the previously published TornadoInsight configuration guidelines and explains how to configure the required environment variable to…"
authors:
  - "thanos-stratikopoulos"
image: "tornado-insight.webp"
categories:
  - "IntelliJ IDEA"
  - "Tools"
  - "TornadoVM"
related_posts:
  - "build-and-run-tornadovm-with-intellij-idea"
  - "ask-a-lille-dev-what-java-developers-really-think-about-quality-frameworks-communities-and-careers"
  - "introducing-the-boxlang-ide-plugin-for-intellij"
  - "foojay-podcast-91"
frozen: false
---

{{< img src="tornado-insight.webp" class="size-full is-resized" width="298" height="298" style="width:138px;height:auto" >}}

This blog updates the previously published **TornadoInsight** configuration [**++guidelines++**](https://www.tornadovm.org/post/introducing-tornadoinsight-unleashing-the-power-of-tornadovm-in-intellij-idea) and explains how to configure the required environment variable to ensure that TornadoInsight correctly detects the TornadoVM SDK when IntelliJ IDEA is launched from a graphical environment.

## **Overview**

TornadoInsight requires access to a compatible Java Development Kit (JDK) and the TornadoVM SDK.

**Important update:**

Since version [++v1.4.0++](https://github.com/beehive-lab/tornado-insight/releases/tag/v1.4.0), TornadoInsight can also use the **JDK configured directly in IntelliJ IDEA** via **Project Structure**. This means that, in many cases, users no longer need to configure the Java SDK in the plugins settings.

## JDK Configuration (Recommended)

TornadoInsight supports using the JDK defined in the IntelliJ project settings.

### Configure the JDK in IntelliJ IDEA

1. Open **IntelliJ IDEA**
2. Go to **File → Project Structure**
3. Select **Project** under **Project Settings**
4. Set **Project SDK** to **JDK 21** (or another supported JDK version)
5. Apply and close the dialog

When a compatible JDK (e.g., JDK 21) is configured here, TornadoInsight will automatically use it.

{{< img src="https://plugins.jetbrains.com/files/23309/screenshot_bd38c017-be77-49d6-a5b0-f9ca54069cd9" class="aligncenter size-large is-resized" style="width:738px;height:auto" >}}

## TornadoVM SDK Configuration (Recommended)

Since version [++v1.4.2++](https://github.com/beehive-lab/tornado-insight/releases/tag/v1.4.2), TornadoInsight requires the environment variable **TORNADOVM_HOME** to be defined and visible to the IntelliJ IDEA process.

On modern operating systems, GUI applications (such as IntelliJ started from JetBrains Toolbox, the Dock, or an application launcher) **do not always inherit shell variables** defined in files like:

* \~/.bashrc
* \~/.zshrc

For this reason, **TORNADOVM_HOME** must be configured using OS-appropriate mechanisms that apply to the **graphical session**, not only to terminal shells.

To define the TornadoVM SDK path across different Operating Systems, you can use the following guidelines which will enable your IntelliJ session to recognize the **TORNADOVM_HOME** environment variable as described below.

```
TORNADOVM_HOME=/absolute/path/to/tornado-sdk
```

### **macOS**

On macOS, GUI applications do **not** read \~/.zshrc or \~/.zprofile.

#### **Recommended:**

Run the following command in a terminal:

```bash
launchctl setenv TORNADOVM_HOME /absolute/path/to/tornado-sdk
```

Then **quit IntelliJ IDEA** (Cmd + Q) and relaunch it.

### Linux

If IntelliJ IDEA is launched from **JetBrains Toolbox** or a desktop launcher, variables set in \~/.bashrc will **not** be visible.

#### **Recommended: systemd** **environment.d**

This method works reliably on modern Linux distributions using systemd.

* Create the directory
* Create a configuration file (tornado.conf)
* Add the variable (no export)
* Log out and log back in, then start IntelliJ IDEA

```bash
mkdir -p ~/.config/environment.d
vim ~/.config/environment.d/tornado.conf
TORNADOVM_HOME=/absolute/path/to/tornado-sdk
```

### **Windows**

1. Open **System Properties**
2. Navigate to **Environment Variables**
3. Add a **User variable** :
   * Name: TORNADOVM_HOME
   * Value: C:\\path\\to\\tornado-sdk
4. Restart IntelliJ IDEA

## **Verification**

Before starting IntelliJ IDEA, verify that the variable is set:

### **macOS / Linux**

```bash
launchctl getenv TORNADOVM_HOME
```

**Linux (systemd check)**

```bash
systemctl --user show-environment | grep TORNADOVM_HOME
```

If the variable is present, TornadoInsight will automatically detect the TornadoVM SDK at startup.

## **Summary**

If **TORNADOVM_HOME** is not recognized by the IntelliJ process, a warning is shown in the settings of the plugin, as shown in the picture below.
![](https://plugins.jetbrains.com/files/23309/screenshot_aa1940fc-c2d6-4120-95fc-2df46fc3d67f)

Some common issues are listed here:

* Setting **TORNADOVM_HOME** only in \~/.bashrc or \~/.zshrc
* Forgetting to log out or restart IntelliJ IDEA
* Using relative paths instead of absolute paths
* Expecting GUI-launched applications to inherit terminal environments

This blog presents some guideline to address these issues across different operating systems.

Useful Links:  

TornadoVM: <https://www.tornadovm.org/>

JetBrains Marketplace: [++https://plugins.jetbrains.com/plugin/23309-tornadoinsight++](https://plugins.jetbrains.com/plugin/23309-tornadoinsight)

GitHub Repository: [++https://github.com/beehive-lab/tornado-insight++](https://github.com/beehive-lab/tornado-insight)

This work has been supported by research funding from the European Union ([++P2Code++](https://p2code-project.eu/), [++AERO++](aero-project.eu)).
