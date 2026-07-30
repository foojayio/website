---
title: "Creating Mobile Apps with JavaFX - Part 2"
slug: "creating-mobile-apps-with-javafx-part-2"
date: "2021-06-18T07:10:41+00:00"
lastmod: "2022-02-11T09:12:35+00:00"
description: "Let's discuss technologies we use with JavaFX to build JVM byte code versions as well as native images that target iOS and Android devices!"
authors:
  - "gail-anderson"
image: "/images/posts/2021/06/creating-mobile-apps-with-javafx-part-2/tiltmaze-logo-small.png"
categories:
  - "Game Development"
  - "JavaFX"
  - "Maven"
tags:
related_posts:
  - "creating-mobile-apps-with-javafx-part-1"
  - "creating-mobile-apps-with-javafx-part-3"
  - "virtual-foojay-openjdk-17-jug-tour"
enlighterjs: true
frozen: false
---

In [Part 1](https://foojay.io/today/creating-mobile-apps-with-javafx---part-1/), we introduced a mobile app game, TiltMaze, written completely in JavaFX, which you can download from either the [Apple App Store](https://apps.apple.com/app/id1566464439) or [Google Play](https://play.google.com/store/apps/details?id=com.asgteach.accelerometer.TiltMaze) and install it on your mobile device or tablet.

In this article, we'll discuss the technologies we use with JavaFX to build the JVM byte code version as well as native images that target iOS and Android devices.

### GluonFX plugin for Maven {#h3-0-gluonfx-plugin-for-maven}

Gluon ([https://docs.gluonhq.com/](https://docs.gluonhq.com/#_the_gluon_client_plugin_for_maven)) has a Maven plugin that lets you build native-image JavaFX applications targeting multiple platforms. While Java has always been about compiling Java byte code that runs in a Java Virtual Machine (JVM) anywhere and everywhere (Write Once, Run Anywhere), there are prohibitions with running byte code in the mobile environment. Besides the performance penalties of executing byte code in a virtual machine and the relatively long startup time, Apple prohibits JVM applications on mobile targets and Android Java is non-standard. If you choose to leave the JavaFX world for native toolkits, you need to acquire expertise in Objective C/Swift as well as the respective native UI toolkits to target these platforms. Spoiler alert: I didn't do that!

Enter Gluon, JavaFX, and GraalVM. The GluonFX plugin leverages GraalVM, OpenJDK and OpenJFX (JavaFX) by compiling the Java client application and all its required dependencies into native code. The target platform directly executes the native application.
> While putting together this series, Gluon has been busy updating their Gluon "Client" maven plugin. As of this week, the plugin is now known as the "GluonFX" plugin at the 1.0.0 version. We have updated this post, but, as always, refer to the docs at [https://docs.gluonhq.com/#_gluonfx_plugin_for_maven](https://t.co/O1E01TiQkY?amp=1). This is a major milestone for building native applications from Java and [#JavaFX](https://twitter.com/hashtag/JavaFX?src=hashtag_click) client apps for desktop, mobile and embedded.

<figure class="wp-block-embed is-type-rich is-provider-twitter wp-block-embed-twitter">
 <div class="wp-block-embed__wrapper">
  <blockquote class="twitter-tweet" data-width="500" data-dnt="true">
   <p lang="en" dir="ltr">The Gluon "Client" maven plugin is now known as the "GluonFX" plugin and we are at the 1.0.0 version. <br>
    Docs at <a target="_blank" href="https://t.co/O1E01TiQkY">https://t.co/O1E01TiQkY</a> <br>
    A major milestone for building native applications from Java and <a target="_blank" href="https://twitter.com/hashtag/JavaFX?src=hash&amp;ref_src=twsrc%5Etfw">#JavaFX</a> client apps for desktop, mobile and embedded.</p>— GluonHQ (@GluonHQ) <a target="_blank" href="https://twitter.com/GluonHQ/status/1404353863055032322?ref_src=twsrc%5Etfw">June 14, 2021</a>
  </blockquote>
 </div>
</figure>

### **GraalVM** {#h3-1-graalvm}

What is GraalVM and why should I use it? GraalVM is a high-performance JDK distribution from Oracle designed to accelerate the execution of applications written in Java (and other JVM languages). GraalVM allows compiling Java applications ahead-of-time (AOT) into native executables for faster startup time and lower memory overhead. Gluon leverages the GraalVM Community Edition to build your mobile application to the target platform using AOT compilation and linking with native libraries. The package step builds the executable for the target device.

### **Gluon Substrate** {#h3-2-gluon-substrate}

If your head is spinning, wondering how to collect all the native libraries, signing requirements, and packaging details into one native executable, fear not! Gluon Substrate (with its GluonFX Maven plugin) pulls together the pieces and dependencies for you. Gluon Substrate lets you build a specific target package that includes all the necessary dependencies.

Here are the steps we took to build TiltMaze.

We built our Maven-based JavaFX application from Gluon's starter page: <https://start.gluon.io/>. This helpful interactive web page lets you specify the main mobile components for your application. Options include Glisten (Gluon's mobile-friendly licensed-based skinned controls), Glisten Afterburner (light-weight dependency injection), Gluon Maps (integrate map functionality), Connect (data resources), CloudLink (connect your mobile application to the enterprise), and Attach (use low-level features of the mobile device in a device and OS-agnostic way). Some of these options require a license from Gluon, but Gluon offers a generous non-pay license for open source and student projects. This page also includes a helpful list of features you can leverage with your mobile application all summarized in one place.

We selected Glisten (to use the mobile-friendly skinned controls), Glisten Afterburner, FXML, and Attach and downloaded the resulting Maven project to our local work machine.

Note: Install maven here: <https://maven.apache.org/install.html>.

### **JVM Desktop** {#h3-3-jvm-desktop}

Although I use an IDE to edit my project, a command line interface performs all the builds to run and install the application. The first task is to build and run the application with regular byte code on the JVM for application design and debugging. My default host machine is Mac OSX with the standard JVM using the default installed (11+) Java and JavaFX. This is the environment we initially used to build and test TiltMaze; the build/run cycle is very fast and efficient for debugging. (For accelerometer testing, we built a small test program to react to the device readings and ran this test on the phone.)

Here is the maven command you run from the top-level directory of the TiltMaze project directory:

<pre class="EnlighterJSRAW" data-enlighter-language="raw" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ mvn javafx:run</pre>

At some point, though, you must test your application on the target hardware. Because native builds take much longer than Java byte code builds, you should perform as much coding, debugging, and testing as possible before you move to actual device testing.

### **iOS** {#h3-4-ios}

Gluon provides documentation on what you need to build native iOS images. If your goal is to create an app that others can install from the App Store, you'll need a Mac, Xcode (Apple's development tool, free to install), an Apple Developer License ($99/year), and Apple signing certificates (one for installing on your own device for testing and a different one for uploading the application to the Apple Connect site).

**Updates on building native images.**
> [Gluon has leveraged GitHub Actions](https://gluonhq.com/use-github-actions-to-automate-your-gluon-build-and-release-cycle/) that allow you to build native images in a GitHub-hosted cloud environment using the required host operating system. This works for both iOS and Android targets, where the required hosts (Mac OSX and Linux, respectively) can be configured remotely through GitHub Actions. We didn't go this route, but GitHub Actions provide a great alternative to building everything locally.

Before testing your app, it's best to build a basic do-nothing Xcode project and install it on your device. This verifies that you have Xcode working with a valid apple ID and your signing certificate lets you install an app to your device.

Next, follow the documentation for the GluonFX plugin. This plugin manages all the pieces for creating native images based on GraalVM JDK and Gluon Substrate from your Java/JavaFX source code. Download and unpack GraalVM from the latest Gluon community edition, <https://github.com/gluonhq/graal/releases/latest>. For Mac and iOS, download [graalvm-svm-darwin-gluon-21.1.0-dev.zip](https://github.com/gluonhq/graal/releases/download/21.1.0-dev-20210415_0700/graalvm-svm-darwin-gluon-21.1.0-dev.zip) (or the most current release version).

Next, set environment variable `GRAALVM_HOME` to the GraalVM JDK:

<pre class="EnlighterJSRAW" data-enlighter-language="raw" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ export GRAALVM_HOME=/path/to/graalvm-svm-darwin-gluon-21.1.0-dev/Contents/Home</pre>

and set `JAVA_HOME` as well:

<pre class="EnlighterJSRAW" data-enlighter-language="raw" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ export JAVA_HOME=$GRAALVM_HOME</pre>

For IOS targets, use[Homebrew](https://brew.sh) to install the following packages:

<pre class="EnlighterJSRAW" data-enlighter-language="raw" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ brew install --HEAD libusbmuxd
$ brew install --HEAD libimobiledevice</pre>

The GluonFX plugin lets you define profiles, so you can specify the desired target in your build/run/install commands. When you build your project from Gluon's starter page (<https://start.gluon.io/>), profiles for ios and android (as well as desktop) are predefined in the `pom.xml` file.

Here is the sequence we use to build and install/run TiltMaze on a connected iPhone. As before, we are in the top-level directory of the TiltMaze project.

<pre class="EnlighterJSRAW" data-enlighter-language="raw" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ mvn -Pios gluonfx:build</pre>

The build action performs a compile followed by a link. Next, connect your iPhone via USB to your Mac and run the following command.

<pre class="EnlighterJSRAW" data-enlighter-language="raw" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ mvn -Pios gluonfx:nativerun</pre>

This command both installs the application created by the build action and runs the application. At this point, standard output from `System.out.println` statements appear on the desktop console and any logging is written to log files in the appropriate target subdirectory. The application is installed on your phone, so you can also exit and re-run the application from the phone. Exiting the application, however, disconnects remote execution from the Mac console.

Note: If you're having issues getting a successful build or install, try

* Turn on verbose mode in the gluonfx plugin.

<pre class="EnlighterJSRAW" data-enlighter-language="xml" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">                &lt;artifactId&gt;gluonfx-maven-plugin&lt;/artifactId&gt;
                &lt;version&gt;${gluonfx.maven.plugin.version}&lt;/version&gt;
                &lt;configuration&gt;
                    . . .
                    &lt;verbose&gt;true&lt;/verbose&gt;
                &lt;/configuration&gt;</pre>

* Return to the [Gluon Documents](https://docs.gluonhq.com/#platforms_ios) for the iOS platform to make sure your pom.xml file is configured correctly.

### **IOS Configuration** {#h3-5-ios-configuration}

The GluonFX plugin builds all its output in the `target` subdirectory. In the process, it generates default configurations for ios-specific settings, such as file **Default-Info.plist** , and a complete icon set under subdirectory `target/gluonfx/arm64-ios/gensrc/ios/`. File **Default-Info.plist** is an XML-based file where you specify ios-specific features, such as support for portrait or landscape modes, or if your app uses the accelerometer, for example.

The GluonFX plugin generates a default icon set for your application based on the Gluon icon. Since these default settings and icons are generated anew for each build, any modifications you make will not be retained. Therefore, you should copy any customized files from `target/gluonfx/arm64-ios/gensrc/ios/` (your icon files and your **Default-Info.plist** file) to `src/ios/`. (Note: we have already customized **Default-Info.plist** and the icon set and moved these files to `src/ios/`.)

In TiltMaze, we modified **Default-Info.plist** to permit portrait mode only, use the accelerometer, and not use Bluetooth.

<pre class="EnlighterJSRAW" data-enlighter-language="xml" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;dict&gt;
          . . .
        &lt;key&gt;CFBundleName&lt;/key&gt;
        &lt;string&gt;TiltMaze&lt;/string&gt;
         . . .
        &lt;key&gt;UISupportedInterfaceOrientations&lt;/key&gt;
        &lt;array&gt;
            &lt;string&gt;UIInterfaceOrientationPortrait&lt;/string&gt;
        &lt;/array&gt;
        &lt;key&gt;UISupportedInterfaceOrientations~ipad&lt;/key&gt;
        &lt;array&gt;
            &lt;string&gt;UIInterfaceOrientationPortrait&lt;/string&gt;
        &lt;/array&gt;
        &lt;key&gt;UIRequiredDeviceCapabilities&lt;/key&gt;
        &lt;array&gt;
            &lt;string&gt;arm64&lt;/string&gt;
            &lt;string&gt;accelerometer&lt;/string&gt;
        &lt;/array&gt;
          . . .
        &lt;key&gt;NSBluetoothAlwaysUsageDescription&lt;/key&gt;
        &lt;string&gt;This app does not use Bluetooth&lt;/string&gt;        
        &lt;key&gt;NSMotionUsageDescription&lt;/key&gt;
        &lt;string&gt;This app uses accelerometer services&lt;/string&gt;
&lt;/dict&gt;
</pre>

We created our own application icon, which you supply in various sizes for the App Store and the device icons. The entire icon set includes 18 different PNG images.

Here are several resources to generate the icons for the various devices.

* App Icon generator: <https://appicon.co/> -- generates a set of icons from a single 1024 x 1024 PNG file.
* Maven Image Builder: You add a scale goal and specify your desired sizes, an input image and an output image name for each size. See the readme file here for more information: <https://github.com/zayass/image-maven-plugin>

Here is the TiltMaze icon. Be sure to leave generous margins around the icon, since Apple will round the corners, possibly cutting into your design.  
![](/images/posts/2021/06/creating-mobile-apps-with-javafx-part-2/tiltmaze-logo-small.png) TiltMaze icon

### **Android** {#h3-6-android}

For Android applications, your development environment is Linux. If you have a Linux box, great! You're ready to install GraalVM, Maven, and optionally your favorite IDE, and start building Android applications. Faced with the prospect of buying extra hardware, I decided to use Oracle's Virtual Box and install Ubuntu Linux on my Mac. This is a very workable solution, but there are a few cautions:

* The "normal" install allocates 16GB for the Ubuntu virtual image, which is too small. After installation, I used the `gparted` utility to increase the size up to 50GB. It's much easier if you configure a larger allocation during setup.
* Normal memory allocation is 4GB; again, too small. I increased memory to more than 10GB.
* Close as many running programs as possible on your Mac before running native builds for Android. This reduces the elapsed time needed to complete the task.
* Configure file sharing so that you can work with the same physical files with both Linux and Mac OSX.

To target an application for the Android platform, you need the Android SDK. Fortunately, the GluonFX plugin automatically downloads and configures the required Android SDK packages for you! (Alternatively, with GitHub Actions you can build your android application in a cloud-hosted Linux system.)

Here's a summary of the steps we followed to create the Android native image after configuring Ubuntu Linux with the larger disk image, more memory, and file sharing.

* Download and install the latest Gluon community edition for Linux, <https://github.com/gluonhq/graal/releases/latest>. Currently, this is <kbd>graalvm-svm-linux-gluon-21.1.0-dev.zip</kbd>. Set the `GRAALVM_HOME` environment variable to the GraalVM install directory on your Linux system.
* Make sure you have `gcc` version 6 or higher.
* Make sure you have `ld` version 2.26 or higher.
* You will use profile target `android` in your Maven `pom.xml` file.

Build the project for Android with

<pre class="EnlighterJSRAW" data-enlighter-language="raw" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ mvn -Pandroid gluonfx:build</pre>

This runs the compilation phase and links the compiled objects into an Android executable.

* Generate an Android application package (an APK):

<pre class="EnlighterJSRAW" data-enlighter-language="raw" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ mvn -Pandroid gluonfx:package</pre>

* Install the application on a connected Android device. Note that "USB debugging" must be enabled on the connected device. To enable USB debugging follow the steps listed [here](https://developer.android.com/training/basics/firstapp/running-app#RealDevice). Install with the following action.

<pre class="EnlighterJSRAW" data-enlighter-language="raw" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ mvn -Pandroid gluonfx:install</pre>

* And finally, launch the application on the connected device with

<pre class="EnlighterJSRAW" data-enlighter-language="raw" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ mvn -Pandroid gluonfx:nativerun</pre>

The same as with the iOS target, you can configure `<verbose>true</verbose>` mode for help in identifying configuration errors.

### **Android Configuration** {#h3-7-android-configuration}

Similar to the iOS targets, the Gluon Plugin generates default settings and application resources for the Android target in subdirectory `target/gluonfx/aarch64-android/gensrc/android/res`. Copy the configuration **AndroidManifest.xml** and icon PNG files to `src/android/res` before customizing these files.

In our case, we specify the application runs only in portrait mode and requires the accelerometer:

<pre class="EnlighterJSRAW" data-enlighter-language="xml" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;uses-feature android:name="android.hardware.sensor.accelerometer" android:required="true" /&gt;</pre>

We provide Android-specific icon resource files for TiltMaze under `src/android/res/`.

### Next {#h3-8-next}

In [Part 3](https://foojay.io/today/creating-mobile-apps-with-javafx-part-3/), we'll show you how to upload a mobile application to the respective mobile app stores.
