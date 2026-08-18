---
title: "Porting an Existing JavaFX App to iOS"
slug: "porting-an-existing-javafx-app-to-ios"
date: "2023-02-24T11:42:11+00:00"
lastmod: "2023-02-24T11:42:13+00:00"
description: "Sometimes there are JavaFX apps that I would also like to use on my iPhone but I wrote them for the desktop and... how to do that?"
authors:
  - "gerrit-grunwald"
image: "jarkanoid-ios.png"
categories:
  - "Gluon"
  - "JavaFX"
tags:
related_posts:
  - "native-applications-for-multiple-devices-from-a-single-javafx-project-with-gluon-mobile-and-github-actions"
  - "starting-a-javafx-project-with-gluon-tools"
  - "cross-platform-development-in-java-with-gluon-and-graalvm"
enlighterjs: true
frozen: false
---

Sometimes there are JavaFX apps that I would also like to use on my iPhone but I wrote them for the desktop and it's always the question how to do that?

These days the simple answer is use [Gluon Mobile](https://gluonhq.com/products/mobile/). But the real question remains... how to do it?

To give you an idea, I will take my little game called JArkanoid (a clone of the game [Arkanoid](https://github.com/HanSolo/jarkanoid) from Taito from 1986) as an example.

Here is a screenshot from one level to give you an idea:

![Games](https://github.com/HanSolo/jarkanoid/blob/main/resources/level1.png?raw=true)

I wrote this game mainly by watching a video on YouTube that showed the original game from 1986.

From this video I've also grabbed the screen dimensions which are 560x740 px.

The game works fine on the different operatings systems like Linux, Windows and Mac but now the question is how to port it to my iPhone.

The first thing I usually do is creating a separate branch from my main branch and give it the name gluon.

Because at the moment sound is not officially supported by Gluon (but there will probably be support in the upcoming version), we have to disable all usage of sound in our code.

This is not a big deal because I normally have a method playSound(final AudioClip audioClip).

This method takes JavaFX AudioClips as parameter which are made for short playing sounds.

We simply have to comment out all places where we call this method and where we load sounds.

#### Maven

Because I like Gradle more than Maven, I use Gradle for all my projects BUT as soon as I build something for iOS using Gluon, I usually use Maven because it was more reliable to me when deploying to iOS.

Here is the Maven pom file for JArkanoid:

```xml
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <groupId>eu.hansolo.fx</groupId>
    <artifactId>jarkanoid</artifactId>
    <version>17.0.7</version>
    <packaging>jar</packaging>

    <name>JArkanoid</name>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.release>11</maven.compiler.release>
        <javafx.version>17.0.6</javafx.version>
        <javafx.plugin.version>0.0.8</javafx.plugin.version>
        <gluonfx.plugin.version>1.0.16</gluonfx.plugin.version>
        <charm.version>6.2.2</charm.version>
        <attach.version>4.0.16</attach.version>
        <main.class>eu.hansolo.fx.jarkanoid.Main</main.class>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-controls</artifactId>
            <version>${javafx.version}</version>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-media</artifactId>
            <version>${javafx.version}</version>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-swing</artifactId>
            <version>${javafx.version}</version>
        </dependency>

        <dependency>
            <groupId>com.gluonhq</groupId>
            <artifactId>charm-glisten</artifactId>
            <version>${charm.version}</version>
        </dependency>

        <dependency>
            <groupId>com.gluonhq.attach</groupId>
            <artifactId>runtime-args</artifactId>
            <version>${attach.version}</version>
        </dependency>
        <dependency>
            <groupId>com.gluonhq.attach</groupId>
            <artifactId>audio</artifactId>
            <version>${attach.version}</version>
        </dependency>
        <dependency>
            <groupId>com.gluonhq.attach</groupId>
            <artifactId>storage</artifactId>
            <version>${attach.version}</version>
        </dependency>
        <dependency>
            <groupId>com.gluonhq.attach</groupId>
            <artifactId>connectivity</artifactId>
            <version>${attach.version}</version>
        </dependency>
        <dependency>
            <groupId>com.gluonhq.attach</groupId>
            <artifactId>display</artifactId>
            <version>${attach.version}</version>
        </dependency>
        <dependency>
            <groupId>com.gluonhq.attach</groupId>
            <artifactId>lifecycle</artifactId>
            <version>${attach.version}</version>
        </dependency>
        <dependency>
            <groupId>com.gluonhq.attach</groupId>
            <artifactId>local-notifications</artifactId>
            <version>${attach.version}</version>
        </dependency>
        <dependency>
            <groupId>com.gluonhq.attach</groupId>
            <artifactId>settings</artifactId>
            <version>${attach.version}</version>
        </dependency>
        <dependency>
            <groupId>com.gluonhq.attach</groupId>
            <artifactId>statusbar</artifactId>
            <version>${attach.version}</version>
        </dependency>
        <dependency>
            <groupId>com.gluonhq.attach</groupId>
            <artifactId>util</artifactId>
            <version>${attach.version}</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
            </plugin>

            <plugin>
                <groupId>org.openjfx</groupId>
                <artifactId>javafx-maven-plugin</artifactId>
                <version>${javafx.plugin.version}</version>
                <configuration>
                    <mainClass>${main.class}</mainClass>
                </configuration>
            </plugin>
            <plugin>
                <groupId>com.gluonhq</groupId>
                <artifactId>gluonfx-maven-plugin</artifactId>
                <version>${gluonfx.plugin.version}</version>
                <configuration>
                    <target>${gluonfx.target}</target>
                    <mainClass>${main.class}</mainClass>
                    <attachList>
                        <list>runtime-args</list>
                        <list>audio</list>
                        <list>connectivity</list>
                        <list>display</list>
                        <list>lifecycle</list>
                        <list>local-notifications</list>
                        <list>settings</list>
                        <list>statusbar</list>
                        <list>storage</list>
                    </attachList>
                    <graalvmHome>/Library/Java/JavaVirtualMachines/gluon-graalvm/Contents/Home</graalvmHome>
                </configuration>
            </plugin>
        </plugins>
    </build>

    <profiles>
        <profile>
            <id>android</id>
            <properties>
                <gluonfx.target>android</gluonfx.target>
            </properties>
        </profile>
        <profile>
            <id>ios</id>
            <properties>
                <gluonfx.target>ios</gluonfx.target>
            </properties>
        </profile>
    </profiles>

    <repositories>
        <repository>
            <id>gluon-releases</id>
            <url>https://nexus.gluonhq.com/nexus/content/repositories/releases/</url>
        </repository>
    </repositories>
</project>
```


#### iOS

To make the project work, you will need some specific settings for iOS.

You will find things like icons and so called plist files for iOS in the `jarkanoid/src/ios/` folder.

If I need to port a new JavaFX project to iOS, I usually copy this folder the new project in the `project/src` folder and modify the content of it's files (e.g., icons and plist files).

For JArkanoid, I copied the ios folder from my [SpaceFX](https://github.com/HanSolo/SpaceFX/tree/mobile) game that I wrote some time ago.

### Prerequisites

There are a couple of things you have to do before you can compile and run your JavaFX application on your iOS device which are:

* Make sure you have an Apple Developer account ($99 per year!)
* Enable [developer mode](https://developer.apple.com/documentation/xcode/enabling-developer-mode-on-a-device) on your iOS device
* Make sure you have an adhoc wildcard provisioning profile [created](https://developer.apple.com/account/resources/certificates/list) to be able to install it
* Download the latest [Gluon GraalVM](https://github.com/gluonhq/graal/releases) version
* Set the GRAALVM_HOME environment variable (e.g. `export GRAALVM_HOME=/Library/Java/JavaVirtualMachines/gluon-graalvm/Contents/Home`)

### Modifications

Usually you have to make some modifications to your application related to the size, layout and things like control. Just keep in mind that on your mobile device you normally don't use a keyboard to interact with the application where on a desktop machine using the keyboard is more or less natural.

Meaning to say if your application requires keyboard control, you need to change that to touch control.

JArkanoid makes use of the keyboard for starting the app which means I need to modify the start screen which reacts on pressing the spacebar to start the game to something like reacting on touching the screen to start the game.

The good thing is that mouse events on the desktop will be handled as touch events on mobile devices. And because I make use of the MOUSE_DRAGGED event to move the paddle with the mouse, on the mobile device, dragging the paddle with the finger will directly work without any need for changes.

Well that's the easy thing, a bit more work is needed to adjust the size from the desktop to the mobile device. In case of JArkanoid this was not that hard because the screen orientation of the original game was already vertical. This means I only have to make sure that the width and height fits to the mobile screen.

Because Gluon does not support all the latest features available e.g. functional switch-case expressions, you need to change those as follows:

```
// Desktop version
switch(enemySpawnPosition) {
    case TOP_LEFT  -> topLeftDoorAlpha  = 0.99;
    case TOP_RIGHT -> topRightDoorAlpha = 0.99;
}

// Gluon version
switch(enemySpawnPosition) {
    case TOP_LEFT : topLeftDoorAlpha  = 0.99; break;
    case TOP_RIGHT: topRightDoorAlpha = 0.99; break;
}
```


So you have to make sure that you change all those switch-case statements.

#### Scaling

The original resolution of the desktop game was 560x740 px and on my iPhone 14 Pro the resolution is 393x852 px. You can look up the resolution of all iOS devices [here](https://www.ios-resolution.com/).

I've decided to set the width to 390 px which also means that it won't fit on iPhones like iPhone 12,13 mini etc. because they only support 375 px.

When you do the math and keep the aspect ratio of the original game the same it will lead to a height of 516 px.

That means the resulting width and height are smaller by the factor of 0.6964285714. And it also means that the game won't fill the whole screen. Of course it would be nicer to use the full screen but for that we would need to rewrite the complete UI (which I don't want to do).

Instead we scale all elements that we use like sprites with the scaling factor that we calculated. This will result in a scaled version of the game that will fit on the screen of our mobile device.

Everywhere in our code where we define sizes (width, height) of elements (also font size) we need to add `* SCALE_FACTOR`.

That means for example that the code

```
protected static final double INSET      = 22;
protected static final Font   SCORE_FONT = Fonts.emulogic(20);
```


needs to be modified to this:

```
protected static final double INSET      = 22 * SCALE_FACTOR;
protected static final Font   SCORE_FONT = Fonts.emulogic(20 * SCALE_FACTOR);
```


I know that this does not look really nice...but it works and does the job 🙂

Once you've done all the modifications it's time to build and install the app on your iOS device.

### Build and install

Once you have all things setup, you can hook up your iOS device to your computer and go to your project folder.

Here you now can execute the following commands in the given order:

* mvn -Pios gluonfx:build
* mvn -Pios gluonfx:package
* mvn -Pios gluonfx:install

This will build, package and install the JavaFX app on your iOS device.

If everything worked as it should, you should see something like the following on your device.

![JArkanoid on iOS](https://i.ibb.co/DtQgLCr/jarkanoid-ios.png)

When you fulfil all the requirements, you should be able to build and install the code from the JArkanoid Gluon branch directly onto you iOS device.

### TIP

If you need to switch between the Gradle based main branch and the Maven based Gluon branch of JArkanoid, you should always make sure to close the project in your IDE (e.g., IntelliJ IDEA).

I usually perform the branch switch in the following order:  

Gradle -\> Maven:

* Close project in IDE
* Switch to gluon branch (Maven based)
* Remove the /build, /.gradle folder
* Remove the build.gradle, gradlew, gradlew.bat, gradle.properties and settings.gradle files
* Remove the /.idea folder
* Open the project in the IDE from the pom.xml file

Maven -\> Gradle:

* Close project in IDE
* Switch to main branch (Gradle based)
* Remove the /target folder
* Remove the /.idea folder
* Open the project in the IDE from the build.gradle file

With this procedure I do not have any problems when switching between the branches.

I only do this because the IDE's are not really able to handle those switches easily, I often saw some problems when doing this.
