---
title: "Why I Love IntelliJ IDEA for Java Development"
slug: "why-i-love-intellij-idea-for-java-development"
date: "2020-11-30T09:16:06+00:00"
lastmod: "2020-11-30T20:45:11+00:00"
description: "I like many things about IntelliJ IDEA, but I thought it'd be fun to write about the ones that make me most productive. Read on for details!"
authors:
  - "matt-raible"
image: "https://foojay.io/wp-content/uploads/2020/11/idea-2020.2.3-700x455.png"
categories:
  - "IntelliJ IDEA"
  - "Tutorials"
tags:
related_posts:
enlighterjs: true
frozen: false
---

If you're a Java developer like me, you like to crank out code and get shit done. I like many things about IntelliJ IDEA, but I thought it'd be fun to write about the ones that make me most productive. First, a bit of my development history.

When I first started doing Java development in the late 90s, I used HomeSite as my editor. HomeSite was an HTML editor initially developed by Nick Bradbury. I liked it because it defaulted to a view of your code rather than being WYSIWYG like Dreamweaver and FrontPage. It's funny to look back now and laugh about how inefficient I was: I used to google for import statements, then copy/pasted them into the editor.

In the early 2000s, I tried a few other IDEs, but they always seemed to be memory hogs and never felt quite as good as HomeSite to me. Then, along came Eclipse. When I first used it, I fell in *love*. Not only did it have a native look-and-feel, but it was fast and efficient.

I stayed in love with Eclipse until I started migrating AppFuse to Maven in 2006. IntelliJ IDEA's Maven support for multiple modules was so much better than Eclipse's m2e, I switched. In 2008, I started doing more web development and found that IDEA had excellent HTML, JavaScript, and CSS support. I haven't looked back since.

**Disclaimer:** as a Java Champion, I get a free license to IntelliJ IDEA. If I didn't, my company would gladly pay for it.

NOTE: I used IDEA 2020.2.3 (Ultimate Edition) for this blog post.

![](/images/posts/2020/11/why-i-love-intellij-idea-for-java-development/idea-2020.2.3-700x455.png "IntelliJ IDEA 2020.2.3")

### Run Your Java Apps from IntelliJ IDEA {#h3-0-run-your-java-apps-from-intellij-idea}

I used to prefer running my apps from the command line with Ant or Maven. Then, along came Spring Boot. Spring Boot is a plain ol' Java application with a `public static void main()`. This makes it a lot easier for Java IDEs to run apps since this is a standard part of *any* Java app.

In fact, if you go to **File** \> **New** \> **Project**, you'll see IDEA supports many popular Java frameworks like Spring Boot, Micronaut, Quarkus, and MicroProfile.

![](/images/posts/2020/11/why-i-love-intellij-idea-for-java-development/intellij-new-project-2-629x510.png "New Project Dialog")

If you select any of these frameworks, a multi-step wizard will guide you through options.

#### Run Spring Boot Apps in IntelliJ IDEA

If you select Spring Boot, it'll even create a run configuration for you with the name matching your main `Application` class. In the screenshot below, the buttons are as follows:

![](/images/posts/2020/11/why-i-love-intellij-idea-for-java-development/spring-boot-run.png "Spring boot run")

* The hammer icon on the left will build your project
* The play icon will run your project
* The bug icon will run your project in debug mode
* The far-right icon will run with code coverage

If you're doing microservices with Spring Boot, there's also a run dashboard you can use to start/stop/monitor all your services. To demonstrate what it looks like, you can clone the example code from [Secure Reactive Microservices with Spring Cloud Gateway](https://developer.okta.com/blog/2019/08/28/reactive-microservices-spring-cloud-gateway) and open it in IDEA:

`git clone https://github.com/oktadeveloper/java-microservices-examples.git `

`cd java-microservices-examples/spring-cloud-gateway idea .`

After downloading dependencies and initializing the project, you'll see a **Services** popup in the bottom right corner.

![](/images/posts/2020/11/why-i-love-intellij-idea-for-java-development/4-spring-boot-service.png "Services popup")

Click on it and it'll expand to give you a couple of options. Click on the first one to show run configurations.

![](/images/posts/2020/11/why-i-love-intellij-idea-for-java-development/5-spring-boot-show-run-configs.png "Show Run Configurations")

This will show the run dashboard and you'll see all your apps listed. You can click on the **Not Started** element and click the play icon to start them all.

![](/images/posts/2020/11/why-i-love-intellij-idea-for-java-development/6-spring-boot-services.png "Spring Boot Services")

Pretty cool, eh?!

![](/images/posts/2020/11/why-i-love-intellij-idea-for-java-development/7-spring-boot-services-running.png "Spring Boot Services runnning")

#### Run Micronaut Apps in IntelliJ IDEA

Micronaut is similar to Spring Boot in that it has a `public static void main()`. When I created a new app using IDEA's Micronaut wizard, it did not generate any run configurations for me. However, when I clicked on the play icon next to the `main()` method, it allowed me to easily create one.

![](/images/posts/2020/11/why-i-love-intellij-idea-for-java-development/8-micronaut-run.png "Create a Micronaut Run Configuration")

#### Run Quarkus Apps in IntelliJ IDEA

Quarkus is a bit different---it has no `main()` method. You have to run the `quarkus:dev` Maven goal to start the app. The good news is you can create a run (or debug) configuration from this by right-clicking on the goal in the Maven tool window and selecting the second option.

![](/images/posts/2020/11/why-i-love-intellij-idea-for-java-development/9-quarkus-run.png "Create a Quarkus Run Configuration")

#### Configure Environment Variables

You might be wondering, "why do I need a run configuration?" First of all, it's nice to click a button (or use a keyboard shortcut) to start and re-start your app. Secondly, it provides a way for you to configure JVM options and environment variables.

For example, if you're using Spring Boot with Okta's Spring Boot starter, you'll want to use environment variables rather than putting a client secret in your source code.![](/images/posts/2020/11/why-i-love-intellij-idea-for-java-development/10-env-variables.png "Set environment variables")

### Debug Your Java Apps {#h3-1-debug-your-java-apps}

Setting breakpoints in apps and debugging them is a valuable skill for software engineers, regardless of language.

In IntelliJ IDEA, you can click in the left gutter next to the line you want to debug. Then, run your app with a debug configuration, and it'll stop at your breakpoint. Then you can step into, step over, etc.

#### Debug via Maven in IntelliJ IDEA

If you start your app with Maven, you can debug it too. Let's use Micronaut in this example. If you run `mvnDebug mn:run`, it'll wait for you to attach a remote debugger on port 8000. To create a remote debugging configuration in IntelliJ IDEA, go to **Add Configuration** \> **+** \> **Remote** and give it a name. Then change the port to `8000`.

![](/images/posts/2020/11/why-i-love-intellij-idea-for-java-development/12-remote-debug.png "Remote Debugging")

#### Debug via Gradle in IntelliJ IDEA

Gradle has a similar ability. For example, if you created a Spring Boot app with Gradle, you could start it with the following command to run in debug mode.

    `gradle bootRun -Dorg.gradle.debug=true --no-daemon`

In this case, Gradle will listen on port 5005 by default, so you'll need to modify your remote configuration to listen on this port.

Confession: I was a Java developer for over five years before I learned [you can remotely debug **any** Java application](https://raibledesigns.com/rd/entry/remotely_debug_your_app_in). All it takes is starting your Java app with some extra arguments, and it'll wait until you attach to it. For example:

    `java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar path/to/jar.jar`

### Run Your Java Tests from IntelliJ IDEA {#h3-2-run-your-java-tests-from-intellij-idea}

When I run tests from the command line with a Java build tool, I often run all the tests.

    # Maven
    mvn test
    # Gradle
    gradle test

When a test fails, I know I can run it as an individual test by adding extra parameters to the command, but I prefer to iterate on tests in IDEA.

IntelliJ has excellent testing support. When you open a Java test in the editor, there will be a play icon next to your test class and individual methods. Click it and you'll get the option to run, debug, run with coverage, or edit the configuration.

![](/images/posts/2020/11/why-i-love-intellij-idea-for-java-development/13-play-icon-tests.png "Play options for tests")

I use this support a lot to run and debug individual tests.

![](/images/posts/2020/11/why-i-love-intellij-idea-for-java-development/14-quarkus-test.png "Running a Quarkus test")

You can also run them at a package level by right-clicking on the page and selecting the **Run Tests** option.

TIP: You might notice I'm using [SDKMAN](https://sdkman.io/) for my JDK installation. IDEA pairs well with SDKMAN (which is very useful when testing with different vendors/versions).

### My Favorite Keyboard Shortcuts in IntelliJ IDEA {#h3-3-my-favorite-keyboard-shortcuts-in-intellij-idea}

I'm a big fan of keyboard shortcuts because leaving my hands on the keyboard makes me more efficient. Since I do a lot of presentations and use keyboard shortcuts, I use [Presentation Assistant](https://plugins.jetbrains.com/plugin/7345-presentation-assistant) for showing commands during presentations (and learning Windows/Linux commands).

I also recommend using [ShortcutFoo](https://www.shortcutfoo.com/) to learn and practice shortcuts for your favorite IDEs. I learned about this site from [Venkat Subramaniam](https://twitter.com/venkat_s) a few years ago.

Below are some of the IntelliJ IDEA keyboard shortcuts I use daily.

|     macOS Shortcut      |         Windows/Linux         |                     Purpose                     |
|-------------------------|-------------------------------|-------------------------------------------------|
| ⌘ + Shift + N           | Ctrl + Shift + N              | Find Files                                      |
| ⌘ + E and ⌘ + Shift + E | Ctrl + E and Ctrl + Shift + E | Recent files and Recent Locations               |
| ⌘ + / and ⌘ + Shift + / | Ctrl + / and Ctrl + Shift + / | Comment/uncomment a line and multiline comments |
| ⌘ + Option + L          | Ctrl + Alt + L                | Reformat code                                   |
| ⌘ + Option + O          | Ctrl + Alt + O                | Optimize imports                                |
| Press Ctrl twice        |                               | Run Anything (e.g. `mvn` compile)               |

You can also use ⌘ + Shift + A to pop an actions dialog and search for commands. Heck, you can even create your own shortcuts!  
> Assign shortcuts to frequently used actions, menu items or features, if they don't already have one. Use Project Settings -\> Keymap, or Find Action -\> Alt+Enter. [#IntelliJTopShortcut](https://twitter.com/hashtag/IntelliJTopShortcut?src=hash&ref_src=twsrc%5Etfw) [pic.twitter.com/uv8Joj2fHT](https://t.co/uv8Joj2fHT)
>
> --- JetBrains IntelliJ IDEA (@intellijidea) [October 20, 2020](https://twitter.com/intellijidea/status/1318461975844724736?ref_src=twsrc%5Etfw)

### IDEA's Command-Line Launcher {#h3-4-idea-s-command-line-launcher}

Did you know you can install a command-line launcher (**Tools** \> **Create Command-line launcher**) and open projects from your terminal? For example:

    # Maven
    idea pom.xml
    # Gradle
    idea gradle.build
    # Figure it out for me
    idea .

The [IDEA CLI has other commands](https://www.jetbrains.com/help/idea/working-with-the-ide-features-from-command-line.html#arguments) like `diff` and `format` too, but I've never used them.

### Markdown and AsciiDoc Support {#h3-5-markdown-and-asciidoc-support}

I write blog posts like this one as much as I write Java code. I was a big fan of writing HTML until I spent a year writing mostly Markdown. Now, I prefer Markdown over HTML, and AsciiDoc is even better!

I like AsciiDoc because it supports things like table of contents, code blocks with callouts, admonitions (tip, note, etc.), and I used it (along with Asciidoctor) to write the [JHipster Mini-Book](http://www.jhipster-book.com/).

IntelliJ IDEA bundles Markdown support, and it works "good enough" when I have to write in Markdown.

I use the [IntelliJ AsciiDoc Plugin](https://intellij-asciidoc-plugin.ahus1.de/) for AsciiDoc authoring. This plugin has gotten so good over the past few years, I do almost all my authoring in IDEA and only render it via build tools as a QA process.

### Local History is Fantastic! {#h3-6-local-history-is-fantastic}

IntelliJ's Local History support can be a lifesaver. I try to edit all text-based files in IDEA because of this feature. If things crash or I want to go back to what I'd written before, local history works excellent for that.

Simply right-click on a file or directory and go to **Local History** \> **Show History**.

### IntelliJ Live Templates {#h3-7-intellij-live-templates}

An awesome way to pre-record code snippets for demos and increase your productivity is to use [Live Templates](https://www.jetbrains.com/help/idea/using-live-templates.html).

For Java, some built-in ones are `sout` and `fori`. You type those characters, hit tab, and it expands to the code you want.

I use live templates for almost all my screencasts and keep them updated at [github.com/mraible/idea-live-templates](https://github.com/mraible/idea-live-templates).

For example, here's my `ss-resource-config` shortcut that configures Spring Security to be an OAuth 2.0 resource server.

    `import com.okta.spring.boot.oauth.Okta;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

    @EnableWebSecurity
    public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http
                .authorizeRequests().anyRequest().authenticated()
                .and()
                .oauth2ResourceServer().jwt();
            // @formatter:on

            Okta.configureResourceServer401ResponseBody(http);
        }
    }`

To add new live templates, select the code you want to save, then go to **Tools** \> **Save as Live Template**. You can also use variables in your template that you can tab to change after you expand it.

TIP: Make sure you test your templates before doing a demo because sometimes they get saved for the wrong context!

### Web Framework Support {#h3-8-web-framework-support}

If you're a hard-core Java developer, you might not care about web framework support. That is, unless your web framework requires Java code, like Wicket or GWT. Personally, I'm a fan of JavaScript and don't trust Java developers to write my JavaScript for me. I'm a full-stack developer, and I ❤️ [JHipster](https://www.jhipster.tech)! 🤓

I first switched to IntelliJ IDEA in 2006 because of its Maven multi-module support. I stuck with it because I switched to mostly front-end development in 2009. I found that IDEA's HTML, CSS, and JavaScript support is excellent. It picks up my ESLint settings automatically and *just works*.

Support for Angular is bundled, and there are lots of React plugins. I've never installed any because I haven't had a need. As far as Vue is concerned, there's a [Vue.js plugin](https://plugins.jetbrains.com/plugin/9442-vue-js) from JetBrains.

### Learn More About Java and IntelliJ {#h3-9-learn-more-about-java-and-intellij}

I've heard from many developers that they prefer to use Eclipse or NetBeans because they're free. If you were a carpenter, would you look around for free tools, or would you buy new fancy tools that make you a better carpenter? 😏

I hope you've enjoyed reading about why I love IntelliJ! If you're curious about IDEA's support for specific Java frameworks, see the following links. Note that these are all features of the Ultimate edition.

* [Spring Boot](https://www.jetbrains.com/help/idea/spring-boot.html)
* [Micronaut](https://www.jetbrains.com/help/idea/micronaut.html)
* [Quarkus](https://www.jetbrains.com/help/idea/quarkus.html)

We've also written several blog posts about Java on this here blog.

* [Java REST API Showdown: Which is the Best Framework on the Market?](https://developer.okta.com/blog/2020/01/09/java-rest-api-showdown)
* [Five Tools to Improve Your Java Code](//developer.okta.com/blog/2019/12/20/five-tools-improve-java)
* [10 Myths About Java in 2019](https://developer.okta.com/blog/2019/07/15/java-myths-2019)
* [Which Java SDK Should You Use?](https://developer.okta.com/blog/2019/01/16/which-java-sdk)

If you have things you love about IntelliJ IDEA, please leave a message in the comments!

If you liked this blog post, please follow us (on [Twitter](https://twitter.com/oktadev), [Facebook](https://facebook.com/oktadev), [LinkedIn](https://linkedin.com/oktadev)) and watch us stream on [YouTube](https://youtube.com/oktadev) and [Twitch](https://twitch.tv/oktadev).
