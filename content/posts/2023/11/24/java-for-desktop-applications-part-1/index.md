---
title: "Java for desktop applications: Tips and Tricks (Part 1)"
date: "2023-11-24T09:18:15+00:00"
lastmod: "2023-11-24T09:21:13+00:00"
description: "Improve your desktop applications with easy-to-apply tips!"
authors:
  - "christopher-schnick"
image: "permission.png"
categories:
  - "Desktop"
  - "Java"
  - "JavaFX"
  - "Use Cases"
related_posts:
  - "9-outdated-ideas-about-java"
  - "state-jvm-desktop-frameworks-jetpack-compose-for-desktop"
  - "starting-docker-desktop-with-spring-boot"
  - "sheetmusic4j-0-0-3-abc-notation-guitar-pro-engraving-improvements"
frozen: false
---

**As desktop applications have kind of become a niche topic, it's getting harder to find information about up-to-date best practices for desktop development.**

Since I have been doing this for many years now, I hope that I can share some of the lessons learned with other fellow desktop developers. Most items that are discussed here are taken straight from [XPipe](https://github.com/xpipe-io/xpipe), the application I am working on right now. So if you're skeptical or are interested in more details, you can take a look at the actual source code or try them in action yourself in the actual application.

You can be assured that everything you read here is tried and tested with many different end users, over a longer period of time, and across all platforms. The general idea is that you can quickly integrate these tips into your own desktop application projects with minimal effort.

## 1. Verify directory permissions

Most applications store some kind of configuration data, usually in a subdirectory located somewhere in the user home directory. While this is a straightforward process, a lot of unexpected things can go wrong here.

### What can go wrong

Apart from the conventional issues such as a faulty or full hard drive, one common issue is an overzealous AV (AntiVirus) program blocking your application from accessing the file system on Windows. While this problem is already bad enough, it usually gets amplified by these AVs not telling the user or application when the block occurs. Instead, users will just end up with generic access denied exceptions in your Java program. On macOS, all applications are blocked by default when accessing common directories like the documents directory, but at least on there users will get a notification for that.

Another common problem is dealing with cloud-synced directories which are getting more and more prevalent these days. For example, it can happen that a user will get logged out from their account after some time. If that occurs, all file system operations will fail for synced directories. OneDrive behaves pretty badly here as the user is not notified that their cloud storage is currently not working. Other cloud file providers can also run into a myriad of issues, but they usually handle it better than OneDrive.

### Implementing a check

From experience, these issues are pretty common and affect around 1 out of 10 users at some point. To remedy them, you can call a method like this on application startup:

```java
public static void checkDirectoryPermissions() {
    var dataDirectory = Path.of("<data dir>"); // Usually ~/myapp or <User Home>\myapp
    try {
        Files.createDirectories(dataDirectory);
        var testDirectory = dataDirectory.resolve("permissions_check");
        Files.createDirectories(testDirectory);
        Files.delete(testDirectory);

        // Uncomment to test
        // if (true) throw new IOException();
    } catch (IOException e) {
        handlError("Unable to access directory " + dataDirectory
                + ". Please make sure that you have the appropriate permissions and no Antivirus program is blocking the access. "
                + "In case you use cloud storage, verify that your cloud storage is working and you are logged in."), e);
    }
}
```

This check has drastically reduced reported issues of this kind for us as users now understand why the access is failing. Of course, you can also adapt it to fit your needs and targeted directories. You can make use of this approach in basically all desktop applications, it is not specific to Java. It is very simple but very effective.

## 2. Adapt tray icons to the OS

While you want your application to stand out, you don't want it to stand out in a bad way. Sadly, Java applications usually do stand out in a bad way in some desktop environments. Many parts of the Linux desktop implementation for AWT feel like they were made to satisfy the minimum requirements in order to be called cross-platform rather than well-thought-out implementations.

It doesn't help the case that these longstanding issues have never been properly addressed. If your application utilizes the system tray, which is only supported with AWT, you have to perform some manual work to achieve a satisfying result across all platforms.

### Fixing a bad Linux implementation

On Linux, any Java tray icon will have a completely white background and no title. Why? Because there is no proper implementation to determine the appropriate background color, and it is therefore not set. The same goes for the tray icon title, which is also not set properly. However, you can use reflection to manually set the background color to be at least transparent and fix the title. This is not publicly exposed because it is considered an implementation detail. The following code will make your tray icon look much better on all Linux systems:

```java
var trayIcon = new TrayIcon(<image>);
SystemTray.getSystemTray().add(trayIcon);

// Use your own implementation here to check
// for the OS, you probably have one for that
if (isLinux()) {
    // invokeLater as we have to give the tray time to set up the peer
    EventQueue.invokeLater(() -> {
        try {
            Field peerField;
            peerField = TrayIcon.class.getDeclaredField("peer");
            peerField.setAccessible(true);
            var peer = peerField.get(trayIcon);

            // If tray initialization fails, this can be null
            if (peer == null) {
                return;
            }

            var canvasField = peer.getClass().getDeclaredField("canvas");
            canvasField.setAccessible(true);
            Component canvas = (Component) canvasField.get(peer);
            canvas.setBackground(new Color(0, 0, 0, 0));

            var frameField = peer.getClass().getDeclaredField("eframe");
            frameField.setAccessible(true);
            Frame frame = (Frame) frameField.get(peer);
            frame.setTitle("MyApp");
        } catch (Exception e) {
            // Handle errors your preferred way
            handleError(e);
        }
    });
}
```

To make this work, you also have to add this to your JVM args for Linux builds:

```
--add-opens "java.desktop/sun.awt.X11=my.module"
```

### Choosing the right resolution

Furthermore, to improve the image look, it can be pretty useful to use the proper image sizes for the tray icon to avoid any image scaling taking place. Of course, it doesn't tell you what sizes the implementation uses, so you would have to figure them out yourself. These are the used sizes:

```java
// Use your own OS check here
var image = switch (OsType.getLocal()) {
    // Switch expression with sealed class and
    // unnamed local variables (Java 21 preview feature)
    case OsType.Windows _ -> "img/logo/logo_16x16.png";
    case OsType.Linux _ -> "img/logo/logo_24x24.png";
    case OsType.MacOs _ -> "img/logo/logo_macos_tray_24x24.png";
};
```

The images should also be adapted in terms of their padding. On Windows, the input image looks good without any padding, but on macOS you have to apply around 4px of padding to the tray icon image to make it look like other macOS icons. Furthermore, many tray icons are usually designed as black and white images, so you might have to convert your colorful logo to some form of grayscale image to integrate them better into the existing tray.

At the end of the day, we have now moved into somewhat acceptable territory with the tray icon. This is the best that we can do with the current tools, assuming that you don't want to dip into custom native code.

## 3. Utilize the advantages of the module system

The Java Platform Module System (JPMS) is still a controversial topic amongst many Java developers, mostly because it breaks their existing classpath-based projects that were working fine before. One major selling point, however, is that with modules, you get access to [jlink](https://docs.oracle.com/en/java/javase/21/docs/specs/man/jlink.html) and [jpackage](https://docs.oracle.com/en/java/javase/21/docs/specs/man/jpackage.html), which are essential tools to build proper self-contained desktop applications.

Nowadays, you don't want to distribute software that requires end users to have the correct JDK installed or depend on third-party tools; this should be a thing of the past. Of course, there are also a few more module features that can make your life easier but are not used this frequently.

### Automatic modularization

Even though Java runtime images are a first-party feature as the tools are included in all JDKs, build tools such as Maven and Gradle are still not shipping proper support for it. As a result, you have to resort to third-party solutions through plugins. To use jlink and jpackage, in case you are not dealing with modules at all, there exists the [badass-runtime plugin](https://badass-runtime-plugin.beryx.org/releases/latest/) for Gradle which utilizes bytecode manipulation to create a merged module of all your dependencies. If you intend to make use of proper modules but still have some non-modular dependencies, the [badass-jlink plugin](https://badass-jlink-plugin.beryx.org/releases/latest/) can be used.

Both plugins make use of [ASM](https://asm.ow2.io/) manipulation to feed the proper modules into jlink. ASM is not perfect however and also requires frequent updates for every new Java version. You also don't get any module advantages with merged modules and might run into issues sometimes. You are able to avoid the ASM usage with these plugins if all dependencies are properly modularized, but that is often not the case. You will also find somewhat equivalent plugins for Maven, e.g. the [JPackage Maven Plugin](https://akman.github.io/jpackage-maven-plugin/).

### Taking things into your own hand

The major roadblock for many people has always been the usage of non-modular dependencies, mostly unmaintained legacy libraries and other libraries where the maintainers just don't care about anything after Java 8. The solution here is to modularize your dependencies by yourself if needed. Manually modularizing dependencies has been possible for quite some time with [moditect](https://github.com/moditect/moditect) in Maven and Gradle. This process is quite tedious and complicated, plus the project and its plugins are currently not maintained.

The more recently created [extra-java-module-info](https://github.com/gradlex-org/extra-java-module-info) Gradle plugin is much easier to use. Essentially, it can dynamically patch and replace any dependencies with generated or provided module information. This module information only takes a couple of lines to define thanks to some shortcuts like being able to automatically open a module and exporting all packages with one statement.

Furthermore, you don't have to replace any dependency artifacts. This is handled by the plugin automatically, allowing you to basically drop in the plugin and module definitions without modifying your original dependencies in your build scripts. It can also handle many complicated cases that were previously a dead end. For example, it is able to fix the problem of split packages by automatically merging modules.

The implementation strategy is simple if you want to use it in your own project: For a better separation, create a separate file like `modules.gradle`, and add the following to your root `build.gradle` if you're dealing with multiple Gradle subprojects:

```groovy
plugins {
    id 'org.gradlex.extra-java-module-info' version '1.6' apply false
}

allprojects { p ->
    apply plugin: 'org.gradlex.extra-java-module-info'
    apply from: "$rootDir/modules.gradle"
}
```

Then define the custom module information for non-modular dependencies in your `modules.gradle` like this:

```java
extraJavaModuleInfo {
    // groupId:artifactId, moduleName
    module("org.apache.commons:commons-lang3", "org.apache.commons.lang3") {
        exportAllPackages()
    }
    module("commons-io:commons-io", "org.apache.commons.io") {
        exportAllPackages()
    }

    module("com.vladsch.flexmark:flexmark", "com.vladsch.flexmark") {
        mergeJar('com.vladsch.flexmark:flexmark-util-data')
        mergeJar('com.vladsch.flexmark:flexmark-util-format')
        // Merge all the parts you need ...

        exportAllPackages()
    }

    ...
}
```

Whenever you now refer to any listed dependency, e.g. with

```
implementation 'org.apache.commons:commons-lang3:<version>'
```

, it will automatically pick up the generated module. And with only that you're already good to go! With all dependencies now automatically modularized, you never have to worry about the classpath ever again. It took only 75 lines to fully modularize all 10 non-modular dependencies of [XPipe](https://github.com/xpipe-io/xpipe) as you can see in the [modules.gradle](https://github.com/xpipe-io/xpipe/blob/1.7.4/modules.gradle). I was even able to integrate [Flexmark](https://github.com/vsch/flexmark-java), which has split packages across like 15 dependency jars.

Now you just have to create the tasks to build your application images with jlink and jpackage. This is now trivial as all dependencies are proper modules. Finally, by creating CI workflows to be executed on multiple systems, e.g., via GitHub Actions, you can build a modern self-contained application that runs on all platforms without the need for an existing Java installation.

Using this approach, the benefits of the module system easily outweigh the effort it takes to adapt to it. Of course, you need to spend a few minutes setting up the plugin and providing module information, but you get a standalone application right out of the box in return.

## Outlook

I hope you liked the tips and can maybe implement some of them in your own projects. This may become a somewhat regular series as I have a lot of items in the backlog. So stay tuned for that!
