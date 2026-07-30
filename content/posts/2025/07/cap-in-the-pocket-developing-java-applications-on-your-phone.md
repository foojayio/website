---
title: "CAP in the Pocket"
slug: "cap-in-the-pocket-developing-java-applications-on-your-phone"
date: "2025-07-07T15:27:26+00:00"
lastmod: "2025-07-07T15:27:28+00:00"
description: "Learn you how develop and run a CAP Spring-Boot Java app locally on your Android phone using Termux and VSCode."
authors:
  - "johannes-bechberger"
image: "/images/posts/2025/07/cap-in-the-pocket-developing-java-applications-on-your-phone/cap-1024x767.png"
categories:
  - "Java"
tags:
related_posts:
  - "ap-loader-a-new-way-to-use-and-embed-async-profiler"
  - "asyncgetcalltrace-reworked-frame-by-frame-with-an-iterative-touch"
  - "book-review-practical-design-patterns-for-java-developers"
  - "class-loader-hierarchies"
enlighterjs: true
frozen: false
---

Smartphones are more powerful then ever, with processors rivaling old laptops. So let's try to use them like a laptop to develop web-applications on the go.

Below I'll show you how to do use run and develop a [CAP Java](https://cap.cloud.sap/docs/java/) [Spring Boot](https://spring.io/projects/spring-boot) application on your smartphone and how to run [VSCode](https://code.visualstudio.com/) locally to develop and modify it. This, of course, works only on Android phones, as they are a Linux at their core.
![](https://mostlynerdless.de/wp-content/uploads/2025/05/Screenshot-May-8-2025-1_28_58-PM-2000x900.png)

Termux {#h2-0-termux}
---------------------

We first need a proper Linux environment with a package manager and more. The most popular app that facilitates this is [Termux](https://termux.dev/):
> Termux is an **Android terminal emulator and Linux environment app** that works directly with no rooting or setup required. A minimal base system is installed automatically - additional packages are available using the APT package manager.
> [TERMUX Website](https://termux.dev/)

Termux allows us to run Linux applications natively on device and use it to e.g. use OpenSSH to connect to another server, or interact with the Android filesystem using UNIX tools. Of course there are [differences](https://wiki.termux.com/wiki/Differences_from_Linux) to other Linux distributions like Debian or Ubuntu, many of them stem from the fact that Termux wants to integrate directly into Android.

We have now two ways to go forward, we could

1. Run everything directly in Termux
2. Use an emulated Ubuntu in Termux
3. Use the new Linux Terminal App

Develop Directly in Termux {#h2-1-develop-directly-in-termux}
-------------------------------------------------------------

We start by using pure Termux, as it's [faster](https://www.reddit.com/r/termux/comments/hvzp9x/cpu_battle_termux_vs_linux_proot/) and more integrated into Android than the second approach. But we'll also see soon, why running in Ubuntu can have it's benefits.

We start our pure Termux approach by installing our basic dependencies (and yes I like [ohmyzsh](https://ohmyz.sh/) and don't want to miss it anywhere):

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">apt install git zsh wget htop
sh -c "$(curl -fsSL https://raw.githubusercontent.com/ohmyzsh/ohmyzsh/master/tools/install.sh)"</pre>

Now we want to install Java. Termux lacks the [pthread library and a few others](https://github.com/termux/termux-app/issues/3261) so simply getting the Linux aarch64 built of my favourite JDK, [SapMachine](https://sapmachine.io/), running on my phone seemed too much work, so I'm using OpenJDK which already available in the Termux packages.

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">apt install openjdk-21</pre>

Now we have a proper OpenJDK JVM running on our phone:  

<figure class="aligncenter size-large is-resized">
 <img decoding="async" src="https://mostlynerdless.de/wp-content/uploads/2025/05/Screenshot-May-7-2025-12_40_05-PM-900x2000.png" alt="" class="wp-image-2102" style="width:300px">
</figure>

But developing code in shell tools like VIM is cumbersome, so we let's install VSCode. The cool part about VSCode is that it consists of two parts, a backend and a front-end. We can run the backend in Termux and the front-end in the browser.

The official VSCode distribution doesn't support Termux. But [Code-Server](https://github.com/coder/code-server) by [Coder](https://coder.com/) is a fork/variant of VSCode that [has support for running directly in Termux](https://coder.com/docs/code-server/termux) (via [dev.to](https://dev.to/codeledger/how-to-get-visual-studio-code-to-run-in-termux-on-android-405j)). It requires a few packages from the [termux-user-repository](https://github.com/termux-user-repository/tur) and can be installed via:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">apt install tur-repo
apt update
apt upgrade
apt install build-essential python nodejs code-server</pre>

Now just start it:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">nohup code-server --auth none &amp;</pre>

This launches a local version and ignores the shell output. Code-server is by default password-protected, which is great. But we don't need authentication as the device is in home WIFI where nobody from the outside can access it anyway. If for what-ever reason, you want to password protect it, then remove `--auth none` and you'll find the auto-generated password in `$PREFIX/.config/code-server/config.yaml`.

Go to [localhost:8080](8080) to access it. Now we have a IDE, running locally directly on our device:  

<figure class="aligncenter size-large is-resized">
 <img decoding="async" src="https://mostlynerdless.de/wp-content/uploads/2025/05/Screenshot-May-7-2025-1_11_41-PM-900x2000.png" alt="" class="wp-image-2103" style="width:300px">
</figure>

That was simple, wasn't it? The only Problem: The official VSCode extensions and accessing the official VSCode market place is not supported, as code-server is a fork of VSCode:  
![](https://mostlynerdless.de/wp-content/uploads/2025/05/Screenshot-May-7-2025-1_19_15-PM-2000x900.png)

You can still install the Java extensions from file by downloading the latest Linux arm64 release via wget from [GitHub](https://github.com/redhat-developer/vscode-java/releases/tag/v1.41.1) and then use "Install from VSIX" in the front-end, but this is cumbersome.

This is why I looked for other possibilities.

Use an Emulated Ubuntu {#h2-2-use-an-emulated-ubuntu}
-----------------------------------------------------

The problem with trying to run the official VSCode directly in Termux is that the Termux environment is too different from a normal Linux. Using [proot](https://wiki.termux.com/wiki/PRoot) we can emulate an Ubuntu environment and use it to install VSCode ([dev.to](https://dev.to/junaid_dev/setup-official-vs-code-on-android-5a)) and even a proper SapMachine on our phone.

We're especially interested in proot-distro. To quote the termux proot wiki page:
> Termux provides a package [proot-distro](https://github.com/termux/proot-distro) which takes care of management of the Linux distributions inside Termux.
> [TERMUX PROOT WIKI](https://wiki.termux.com/wiki/PRoot)

This allows to easily install an Ubuntu on our phone, which looks a little bit like [Windows Subsystem for Linux](https://ubuntu.com/desktop/wsl):

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">pkg install proot-distro
proot-distro install ubuntu</pre>

Now you can login via the following to run as root and use the Termux home folder as home:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">proot-distro login ubuntu --termux-home</pre>

Running in this environment is, as I told you before, slower than running directly in Termux. To quote a reddit user:
> Proot is slower. It uses Linux debugging interface (ptrace) to control the process execution and hijack arguments and return values of system calls, so it can simulate a different file system layout and user/group ids. This cause a lot overhead. In my experience the biggest performance penalty can be observed when working with a lot of files (e.g. extracting tarball).
> [REDDIT](https://www.reddit.com/r/termux/comments/o4br6w/comment/h2gpbdc/?utm_source=share&utm_medium=web3x&utm_name=web3xcss&utm_term=1&utm_content=share_button)

As before, we want install some basic utilities and ohmyzsh:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">apt install git zsh wget htop
sh -c "$(curl -fsSL https://raw.githubusercontent.com/ohmyzsh/ohmyzsh/master/tools/install.sh)"</pre>

Now we can install SapMachine as explained in the [SapMachine Wiki](https://github.com/SAP/SapMachine/wiki/Installation):

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group=""># Add the SapMachine GPG key
wget -qO- https://dist.sapmachine.io/debian/sapmachine.key | tee /etc/apt/trusted.gpg.d/sapmachine.asc &gt; /dev/null
# Add the SapMachine repository
echo "deb https://dist.sapmachine.io/debian/$(dpkg --print-architecture)/ ./" | tee /etc/apt/sources.list.d/sapmachine.list &gt; /dev/null
# Install SapMachine 21
apt update
apt install sapmachine-21-jdk</pre>

Resulting in a proper JVM:  

<figure class="aligncenter size-large is-resized">
 <img decoding="async" src="https://mostlynerdless.de/wp-content/uploads/2025/05/Screenshot-May-8-2025-12_37_53-PM-900x2000.png" alt="" class="wp-image-2114" style="width:300px">
</figure>

After that, we can install the official VSCode distribution:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">wget 'https://code.visualstudio.com/sha/download?build=stable&amp;os=linux-deb-arm64' -O code.deb
apt install ./code.deb
# Install the missing packages
apt --fix-broken install
rm code.deb</pre>

Installing the missing packages to around half an hour on my Pixel 8a.

Starting the VSCode backend is as easy as before with code-server:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">nohup code serve-web --port 8080 --without-connection-token &amp; </pre>

Now we have a proper official VSCode and can view it in the browser at [localhost:8080](8080):  

<figure class="aligncenter size-large is-resized">
 <img decoding="async" src="https://mostlynerdless.de/wp-content/uploads/2025/05/Screenshot-May-8-2025-1_14_40-PM-900x2000.png" alt="" class="wp-image-2119" style="width:300px">
</figure>

In this we can install all the extensions we want and can start developing applications.

I would recommend to use "Add to Home screen" in your browser menu to create a VSCode web app on your home screen. This app then omits the browser menu bar, giving you more space to see your code.

SAP CAP SFlight {#h2-3-sap-cap-sflight}
---------------------------------------

Let's start running and developing our application. You can of course create any application to your hearts content and use either Termux or the emulated Ubuntu. But I'll choose the latter and as a sample application the [SAP CAP SFlight application](https://github.com/SAP-samples/cap-sflight).

[CAP](https://cap.cloud.sap/) is the framework agnostic application framework of SAP for writing applications in Java and NodeJs and SFlight a simple sample application:
> This is a sample app for the travel reference scenario, built with the [SAP Cloud Application Programming Model (CAP)](https://cap.cloud.sap) and [SAP Fiori Elements](https://experience.sap.com/fiori-design-web/smart-templates).
>
> The purpose of this sample app is to:
>
> * Demonstrate SAP Fiori annotations
> * Demonstrate and compare SAP Fiori features on various stacks (CAP Node.js, CAP Java SDK, ABAP)
> * Run UI test suites on various stacks
>
> [README of CAP-SFLIGHT](https://github.com/SAP-samples/cap-sflight)

I'm neither a CAP nor an SAP Fiori expert, but CAP is really important in the SAP context and can be used to write Java web applications. The initial idea for this whole blog post came out-of a discussion with the CAP Java folks (Robin de Silva Jayasinghe), which is why I'm choosing SFlight.

Building and Running SAP CAP SFlight {#h2-4-building-and-running-sap-cap-sflight}
---------------------------------------------------------------------------------

Let's start by building SFlight on device. First we clone it:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">mkdir code # some hygiene
cd code
git clone https://github.com/SAP-samples/cap-sflight
cd cap-sflight
# install the Maven build system and npm
apt install maven npm</pre>

For those curious, this took:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group=""># Emulated Ubuntu
3.22s user 2.05s system 81% cpu 6.457 total

# Pure Termux
2.98s user 1.19s system 99% cpu 4.184 total

# Mac M4 for reference
1.47s user 0.55s system 58% cpu 3.457 tota</pre>

Now we build and run it.

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">npm ci # NodeJS based CAP tools
npm run build:ui
mvn spring-boot:run</pre>

But the CAP tools run into a problem:  

<figure class="aligncenter size-large is-resized">
 <img decoding="async" src="https://mostlynerdless.de/wp-content/uploads/2025/05/Screenshot-May-7-2025-4_53_38-PM-900x2000.png" alt="" class="wp-image-2107" style="width:300px">
</figure>

Seems like nobody prebuilt their npm packages from the Android arm64 target. And no, self-building all packages doesn't work and even if. `mvn spring-boot:run`, which uses the CAP's NodeJS tools internally, crashes with a double free:  

<figure class="aligncenter size-large is-resized">
 <img decoding="async" src="https://mostlynerdless.de/wp-content/uploads/2025/05/image-1-900x2000.png" alt="" class="wp-image-2108" style="width:300px">
</figure>

Because I still want to build SFlight on my phone, I need to get creative. The problem here are solely the CAP tools, not anything written in Java. So yes, you might call the following hacky, but this would be no problem in standard Spring Boot applications. It only means that you should be aware of the dependencies that your project needs, especially of natively compiled dependencies.

The Idea is: CAP Java generates database model helper classes and the UI using a complex toolchain, so let's directly commit the files to GitHub from a machine that can run all tools (my MacBook) and push all into a seperate [branch](https://github.com/parttimenerd/cap-sflight/tree/cap-in-the-pocket). Developing with this branch is fine, as long as you only modify the backend and don't change the database schema.

Is it hacky? Yes. Is this a problem for a demo? No.

We add the new branch as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">git remote add fork https://github.com/parttimenerd/cap-sflight
git pull fork
git checkout cap-in-the-pocket</pre>

Now we can build and run the application again:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">mvn spring-boot:run</pre>

It might exclaim that `JAVA_HOME` is not set correctly, this can be remedied by

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">export JAVA_HOME=/usr/lib/jvm/sapmachine-21</pre>

You might want to add this to your `.zshrc` to make it permament.

*Do this*

To access the most basic page of the app, visit [localhost:4004](4004)  

<figure class="aligncenter size-large is-resized">
 <img decoding="async" src="https://mostlynerdless.de/wp-content/uploads/2025/05/Screenshot-May-7-2025-5_03_35-PM1-900x2000.png" alt="" class="wp-image-2110" style="width:300px">
</figure>

Or visit <http://localhost:4004/travel_processor/dist/index.html> to a proper page with which we can easily interact. When the website wants you to sign in, choose "privileged" as a user and an empty password.  

<figure class="aligncenter size-large is-resized">
 <img decoding="async" src="https://mostlynerdless.de/wp-content/uploads/2025/05/Screenshot-May-8-2025-12_53_04-PM-900x2000.png" alt="" class="wp-image-2116" style="width:300px">
</figure>

Leading you to:  

<figure class="aligncenter size-large is-resized">
 <img decoding="async" src="https://mostlynerdless.de/wp-content/uploads/2025/05/Screenshot-May-8-2025-12_55_39-PM-1-900x2000.png" alt="" class="wp-image-2118" style="width:300px">
</figure>

Modifying the Application via VSCode {#h2-5-modifying-the-application-via-vscode}
---------------------------------------------------------------------------------

We can use the VSCode instance that we launched before to access our SFlight project (`nohup code serve-web --port 8080 --without-connection-token &` to start it again if you stopped yours) and work with the code:
![](https://mostlynerdless.de/wp-content/uploads/2025/05/Screenshot-May-8-2025-1_27_10-PM-2000x900.png)

Now let's have some fun: The SFlight admin screens allows the privileged user to application deductions to the cost of a flight. We now introduce a small bug in the code that computes the discount in the `DeductDiscountHandler` class to always calculate a ten times higher discount than requested. This might not make any sense, but the reverse (fixing this bug) is a nice demo of how one can use VSCode on their phone to fix a serious bug on the go.

The discount is computed in `DeductDiscountHandler` as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">BigDecimal discount = BigDecimal.valueOf(context.percent())
	.divide(BigDecimal.valueOf(100), new MathContext(3));

BigDecimal deductedBookingFee = travel.bookingFee()
        .subtract(travel.bookingFee().multiply(discount))
	.round(new MathContext(3));
BigDecimal deductedTotalPrice = travel.totalPrice()
        .subtract(travel.totalPrice().multiply(discount));</pre>

We can now introduce the bug by dividing `context.percent()` in the first two lines not by 100, but by 10.
![](https://mostlynerdless.de/wp-content/uploads/2025/05/Screenshot-May-8-2025-1_28_58-PM-2000x900.png)

When we rebuild and run the application via `mvn spring-boot:run`, deducting 10% of the travel costs:
![](https://mostlynerdless.de/wp-content/uploads/2025/05/Screenshot_20250508-133128-2000x900.png)

Decreases the price to 0 USD:
![](https://mostlynerdless.de/wp-content/uploads/2025/05/Screenshot_20250508-133150-2000x900.png)

Nothing prevents us from using git to commit our change to some repo. This shows how we can easily modify our CAP/Spring-Boot application locally on our phone.

Android Linux Terminal {#h2-6-android-linux-terminal}
-----------------------------------------------------

In March Google [launched](https://www.androidpolice.com/android-15-linux-terminal-app/?ref=news.itsfoss.com) the native Linux Terminal app for Android on Google Pixel devices. Luckily the Android phone I use for all these tests is Pixel 8a which is one of the supported devices. The app offers essentially the same experience as the emulated Ubuntu in Termux:
> The Terminal app operates by launching a Debian Linux environment within a virtual machine, powered by Android's Virtualization Framework (AVF). Rather than exposing the underlying Android file system, it gives you an isolated Linux shell---much like what ChromeOS has offered developers and enthusiasts for years.
> [ikkaro.net](https://www.ikkaro.net/android-linux-terminal-features-setup/)

To enable it, you have to setup Developer Mode (by repeatedly clicking the Build Number in the settings) and activate the Linux Development Environment in the settings, as described at [ikkaro.net](https://www.ikkaro.net/android-linux-terminal-features-setup/).

But of course the app has limitations compared to Termux:
> * **Slow startup times:** Booting the Terminal can take 10-20 seconds since it loads a full virtual machine (especially on mid-range hardware).
> * **Limited file system access:** By default, you can only access your phone's Downloads directory (via `cd /mnt/shared`), not the entire Android file system. For privacy and security, other folders remain off-limits. \[...\]
> * **Resource constraints:** The VM's allotted disk space is adjustable (from the Terminal app settings), but heavy use---like AI models or large database systems---can quickly fill storage.
>
> [ikkaro.net](https://www.ikkaro.net/android-linux-terminal-features-setup/)

One tiny but significant limitation is that the terminal app currently doesn't support tabs, but this feature [will apparently be available soon](https://www.androidauthority.com/android-linux-terminal-tabs-3535373/) with Android 16. Overall the app feels far less stable than Termux (but supports pasting via external keyboard) and sometimes restarts the UI, clearing the terminal but killing the underlying user session. You should definitely adjust the disk space which is by default limited to 5GB which quickly fills up.

Let's install the utitlities and SapMachine as before with the main difference that we're not running as root by default:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group=""># Install the utilities
sudo apt update
sudo apt install git zsh wget htop
sh -c "$(curl -fsSL https://raw.githubusercontent.com/ohmyzsh/ohmyzsh/master/tools/install.sh)"
su # change to root
# Add the SapMachine GPG key
wget -qO- https://dist.sapmachine.io/debian/sapmachine.key | 
tee /etc/apt/trusted.gpg.d/sapmachine.asc &gt; /dev/null
# Add the SapMachine repository
echo "deb https://dist.sapmachine.io/debian/$(dpkg --print-architecture)/ ./" | tee /etc/apt/sources.list.d/sapmachine.list &gt; /dev/null
# Install SapMachine 21
apt update
apt install sapmachine-21-jdk
exit # exit root</pre>

And of course you should set `JAVA_HOME` as before to prevent maven from complaining later.

We now have a SapMachine:  

<figure class="aligncenter size-large is-resized">
 <img decoding="async" src="https://mostlynerdless.de/wp-content/uploads/2025/05/Screenshot-May-8-2025-2_05_45-PM-1-900x2000.png" alt="" class="wp-image-2126" style="width:300px">
</figure>

*There are now technically two SapMachines and one OpenJDK installed on my tiny phone. Maybe I should install an OpenJ9 build just for good measure...*

We install VSCode as before too:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">wget 'https://code.visualstudio.com/sha/download?build=stable&amp;os=linux-deb-arm64' -O code.deb
sudo apt install ./code.deb
# Install the missing packages
sudo apt --fix-broken install
rm code.deb</pre>

During the installation I got asked whether I want to add the Microsoft apt repository, having this prompt on my phone just looks funny:  

<figure class="aligncenter size-large is-resized">
 <img decoding="async" src="https://mostlynerdless.de/wp-content/uploads/2025/05/Screenshot-May-8-2025-2_19_39-PM-900x2000.png" alt="" class="wp-image-2128" style="width:300px">
</figure>

Unsuprisingly, launching VSCode via `nohup code serve-web --port 8080 --without-connection-token &` and then accessing the front-end via [localhost:8080](http://localhost:8080) works:  

<figure class="aligncenter size-large is-resized">
 <img decoding="async" src="https://mostlynerdless.de/wp-content/uploads/2025/05/Screenshot-May-8-2025-2_25_43-PM-1-900x2000.png" alt="" class="wp-image-2130" style="width:300px">
</figure>

Now the thing you're all waiting for: How long does a git clone take and do the CAP Java tools work in building the SFlight CAP app on this system?

Though first we have to update the NodeJS version (via [nodejs.org](https://nodejs.org/en/download)): *Report bug, that it contains "\\.. source ..." which doesn't work*

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group=""># Download and install nvm:
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.40.3/install.sh | bash

# in lieu of restarting the shell
. "$HOME/.nvm/nvm.sh"

# Download and install Node.js:
nvm install 22</pre>

Now we can run the commands as before:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">mkdir code # some hygiene
cd code
git clone https://github.com/SAP-samples/cap-sflight
# took: 3.58s user 1.52s system 109% cpu 4.643 total
# which sits in between pure Termux and the emulated Ubuntu
cd cap-sflight
# install the Maven build system and npm
sudo apt install maven

npm ci # NodeJS based CAP tools
npm run build:ui
mvn spring-boot:run</pre>

And well, it worked... Which I find surprising. The UI of the new Linux Terminal App might be lacking and the partition size limit might be limiting, but I can build and run the stock CAP SFlight application without any changes.

Well it worked till I opened the browser and then the Linux Terminal App crashed. I'm happy that I documented everything. I tried it two times more. But this means that despite it's apparent benefits, it's basically unusuable for this specific use case for now.  

<figure class="aligncenter size-large is-resized">
 <img decoding="async" src="https://mostlynerdless.de/wp-content/uploads/2025/05/Screenshot-May-8-2025-4_27_18-PM-1-900x2000.png" alt="" class="wp-image-2134" style="width:300px">
</figure>

Hopefully I can revisit this in a few months and it's fixed.

**Update:** This terminal works much better with Android 16. It's still flaky, but it can run a proper CAP build-pipeline and VSCode without any issues.

Extra: CAP-in-the-Pocket VSCode Extension {#h2-7-extra-cap-in-the-pocket-vscode-extension}
------------------------------------------------------------------------------------------

Switching between VSCode for editing and the shell for killing the previous SFlight server instance is too cumbersome for my demo. So I created the [CAP-in-the-Pocket](https://github.com/parttimenerd/cap-in-the-pocket-extension) extension for VSCode:  

<figure class="aligncenter size-large is-resized">
 <img decoding="async" src="https://mostlynerdless.de/wp-content/uploads/2025/05/Screenshot-May-9-2025-12_42_33-PM-900x2000.png" alt="" class="wp-image-2139" style="width:300px">
</figure>

To install it, download the extension from [GitHub](https://github.com/parttimenerd/cap-in-the-pocket-extension/releases/download/snapshot/cap-in-the-pocket-0.0.1.vsix) and open with the Termux app to make it available to VSCode. Then open the Extensions view in VSCode, click the "..." at the top of the Extensions view, select "Install from VSIX..." and select the downloaded file.

Please be aware that this extension is highly experimental and only created with the specific demo in mind.

The "(Re)Launch CAP App" button tries to kill the previous running instance and relaunches it:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">(lsof -ti:4004 | xargs kill -9) || killall java || true
mvn spring-boot:run</pre>

*Did I say this extension is highly experimental?*

Below the button, you see the output of the commands and links to the CAP application. By default I show buttons that open the two main views of SFlight. But you can configure it via the `settings.json` file. The default configuration is equivalent to:

<pre class="EnlighterJSRAW" data-enlighter-language="json" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">"cap-in-the-pocket.urlButtons": [
  {
    "label": "Travel Processor",
    "url": "http://localhost:4004/travel_processor/dist/index.html"
  },
  {
    "label": "Travel Analytics",
    "url": "http://localhost:4004/travel_analytics/dist/index.html"
  }
]</pre>

Creating this little plugin (with the help of Claude Sonnet and GitHub Copilot) allows me to have a more immersive demo.

Please be aware that you should only use the extension when you access VSCode in the normal browser, as opening links in the web-app added to the home-screen doesn't work properly.

Conclusion {#h2-8-conclusion}
-----------------------------

In this blog post, I showed you how to install and use VSCode and Java on your Android phone and develop applications, using three different options. Of course there a cloud-offerings that allow you to develop applications from mobile devices too, but I like the simplicity of running the web application directly on your phone, with full control. All of this is possible because Android phones use Linux as their base level operating system.

You could go even further and use the [newly introduced Android Desktop Mode](https://www.androidauthority.com/android-desktop-mode-leak-3550321/) with an external screen, connect a keyboard and a mouse to your device and you come close to a basic Linux computer. And if you use the phone directly, you have a laptop with really good battery life, albeit not the best performance. It probably won't become my daily Linux driver but to do coding when I'm on the go.

Thank you for joining me on this journey to learn how to develop your web applications on your phone.

<br />

P.S.: I tried compiling the OpenJDK on my phone but Termux killed the process and the Linux Terminal App ran out of disk memory.

*This article is part of my work in the [SapMachine](https://sapmachine.io/) team at [SAP](https://sap.com/), making profiling and debugging easier for everyone. It has been first published on [my personal blog](https://mostlynerdless.de/blog/2025/05/09/cap-in-the-pocket-developing-java-applications-on-your-phone/).* *Thanks to Antje Luttenberger and Guilherme *Dellagustin* from the [SAP OSPO](http://opensource.sap.com/) for inspiring this work and Robin de Silva Jayasinghe from CAP Java for helping to setup SFlight and fixing bugs along the way.*
