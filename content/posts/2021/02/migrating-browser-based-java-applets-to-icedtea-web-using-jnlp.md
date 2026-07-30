---
title: "Migrating Browser-Based Java Applets to IcedTea-Web using JNLP"
slug: "migrating-browser-based-java-applets-to-icedtea-web-using-jnlp"
date: "2021-02-02T07:55:53+00:00"
lastmod: "2023-05-16T13:40:49+00:00"
description: "Let's use a 20 year old JAR, once created for a Java Applet to be run in the Java 1.1 Browser Plugin... and run it on JDK 11!"
authors:
  - "holger-isenberg"
image: "/images/posts/2021/02/migrating-browser-based-java-applets-to-icedtea-web-using-jnlp/image-11-1024x825.png"
categories:
  - "Java Core"
tags:
related_posts:
enlighterjs: true
frozen: false
---

"Does it run on JDK 11?" -- That's an always interesting question!

As a practical, more challenging example around this question, I used a 20 year old JAR, once created for a Java Applet to be run in the Java 1.1 Browser Plugin. Yes, that's 1.1 not 11!

**To cut to the chase for those who fear this may be too long to read: yes it runs!** 🙂

And to provide more detail---it now runs outside of any Web Browser and without modifying its decades-old JAR. The Applet was once created and offered to browse through all raw color images uploaded by the Mars Pathfinder Lander which settled down on the planet on July 4th, 1997.  

<figure class="alignright size-large is-resized">
 <img fetchpriority="high" decoding="async" src="/images/posts/2021/02/migrating-browser-based-java-applets-to-icedtea-web-using-jnlp/image-11-1024x825.png" alt="" class="wp-image-37589" width="417" height="336">
</figure>

That's the same lander / rover combination which saved the day for Mark Watney in the 2015 movie ​*The Martian* ​ and already before in the 2000 movie ​*Red Planet*​ for Robby Gallagher, where they were able to reuse its parts, telnet into the software and regain contact with Earth.

The need for having access to an old Java Applet sometimes feels like being in Mark Watney's situation -- when the Applet is the only remaining communication channel to a legacy storage array requesting admin input to continue the important migration work. Often, the elongated ultimate diamond support contract has expired decades ago and the vendor cannot be convinced to offer alternatives. The alternative to change the source is blocked by missing USB-C compatible floppy disk drives to import it or the backup tape coating with the CVS repository had disintegrated into oxide dust when picking it up.

Here the original Pathfinder Applet Page: [​https://areo.info/mpf/java11.html](http://areo.info/mpf/java11.html). You can click on that page, but don't try to make it usable by installing any plugins or extension into your Web Browser, as that would introduce the unsafe and outdated Applet Plugin to your system!

Instead, use the recipe explained below to create a JNLP file so you can run the Applet on current OpenJDK 11 with IcedTea-Web independently from any Web Browser.

Overview {#h2-0-overview}
-------------------------

For those who consider this rocket science and just want to see the result after migration, the migrated Pathfinder application is accessible here: ​<https://areo.info/mpf>. For those who are curious about all the details, here is the general workflow with code:

The idea provided here utilizes the Applet-feature of Java Web Start which was integrated into the JNLP file format to help during the migration of Java Applets to standalone applications. With that feature, it is possible to run at least those Applets which don't interact much with the HTML page they are embedded into. Simple interaction though, like calling URLs over http or https still works. While the same name "Applet" is used in JNLP, it is technically a normal standalone application, not connected to any web browser.

### **Preparation** {#h3-1-preparation}

To start, you first need to install OpenJDK 11 and IcedTea-Web on the desktop system where you like to run the Applet. Both are offered on various download locations. For example, you can get them from azul.com here:

* [Zulu Builds of OpenJDK](https://www.azul.com/downloads/zulu-community/?version=java-11-lts&package=jdk)
* [Free Builds of IcedTea-Web](https://www.azul.com/downloads/icedtea-web-community/)

IcedTea-Web also runs on OpenJDK 8, but I recommend to start first with JDK 11, even for decade-old Applets. After installation of both, the next step is to check details about the web page containing the Applet you want to migrate.

### **Locating the Applet in the HTML Page** {#h3-2-locating-the-applet-in-the-html-page}

Usually, an Applet is embedded in a single large web page using the \<applet\>, \<object\> or \<embed\> tag.

That page might be visible only after logging in with username and password into the application start page. Download the Applet-HTML page or look into it in the Browser HTML source/inspect view. The interesting section which needs to be copied as text should look similar to one of the following three examples listed below. Also write down the full URL of that Applet-HTML page. Sometimes several of the 3 tags are included to offer compatibility for different browsers, but the information in all of them should be the same.

**Applet-Tag**

This is the original method to include Applets.

<pre class="EnlighterJSRAW" data-enlighter-language="xml" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;applet
    code="MyJavaClass.class" archive="myapp.jar" 
    width="600" height="400" &gt; 
  &lt;param name="param1" value="value1"&gt;
  &lt;param name="param2" value="value2"&gt;
  &lt;param name="param3" value="somedir/otherdir"&gt; 
  Alternative text.
&lt;/applet&gt;</pre>

**Object-Tag**

This was added later as an alternative to the Applet tag.

<pre class="EnlighterJSRAW" data-enlighter-language="xml" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;object codetype="application/java" 
    classid="java:MyJavaClass.class" archive="myapp.jar" 
    width="600" height="400" 
    codebase="plugin/1.3/jinstall-13-win32.cab" &gt;
  &lt;param name="code" value="MyJavaClass.class"&gt; 
  &lt;param name="archive" value="myapp.jar"&gt; 
  &lt;param name="codebase" value="."&gt;
  &lt;param name="type"
     value="application/x-java-applet;version=1.1"&gt; 
  &lt;param name="scriptable" value="true"&gt;
  &lt;param name="param1" value="value1"&gt;
  &lt;param name="param2" value="value2"&gt;
  &lt;param name="param3" value="somedir/otherdir"&gt;
  Alternative text.
&lt;/object&gt;</pre>

**Embed-Tag**

This was used by the Netscape Browser.

<pre class="EnlighterJSRAW" data-enlighter-language="xml" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;embed type="application/x-java-applet;version=1.1" 
   code="MyAppClass.class" archive="myapp.jar"
   width="600" height="400"
   codebase="."
   pluginspage="plugin/1.3/plugin-install.html" &gt; 
   param1="value1" param2="value2" param3="somedir/otherdir"
  &lt;noembed&gt;Alternative text.&lt;/noembed&gt; 
&lt;/embed&gt;</pre>

### **Creating a JNLP file** {#h3-3-creating-a-jnlp-file}

Based on the copied \<applet\>, \<object\> or \<embed\> section, create a JNLP file named myapp.jnlp here in this example. The content should look similar to the following, with all attributes, parameters and URLs replaced by the actual values from your application:

<pre class="EnlighterJSRAW" data-enlighter-language="xml" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;?xml version="1.0" standalone="yes"?&gt;
&lt;jnlp spec="1.5+" xmlns="http://www.w3.org/1999/xhtml" codebase="" href="myapp.jnlp"&gt;
    &lt;information&gt;
       &lt;title&gt;MyApplet-Title&lt;/title&gt;
       &lt;vendor&gt;vendorname&lt;/vendor&gt; 
       &lt;description&gt;text&lt;/description&gt;
       &lt;homepage href="https://mywebsite.domain"&gt;&lt;/homepage&gt; 
       &lt;offline-allowed /&gt;
    &lt;/information&gt;
    &lt;resources&gt;
       &lt;j2se version="1.2+"&gt;&lt;/j2se&gt;
       &lt;jar href="http://mywebsite.domain/somepath/myapp.jar" main="true"&gt;&lt;/jar&gt;
       &lt;jar href="http://mywebsite.domain/somepath/mylib.jar"/&gt;
    &lt;/resources&gt;
    &lt;security&gt;
       &lt;sandbox/&gt; 
    &lt;/security&gt; 
    &lt;applet-desc
      name="MyMainClass" main-class="MyMainClass"
      width="600" height="400"&gt;
     &lt;param name="param1" value="value1"/&gt;
     &lt;param name="param2" value="value2"/&gt;
     &lt;param name="param3" value="somedir/otherdir"/&gt;
    &lt;/applet-desc&gt;
&lt;/jnlp&gt;</pre>

I'm using an empty "codebase" attribute as for the first step the file will only be used locally. Because of the empty codebase, we then have to add the complete absolute URLs in the jar href attributes. Without this, IcedTea-Web would try to update the local JNLP file from the codebase URL, which will fail. We'll now try to run the newly created file on IcedTea-Web.

### **Running the Migrated Applet** {#h3-4-running-the-migrated-applet}

To run the Applet now on IcedTea-Web, enter the following commands depending on the OS you use on your desktop system.

Linux / macOS:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">javaws.sh myapp.jnlp</pre>

Windows (open a command window / Powershell first):

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">javaws myapp.jnlp</pre>

I'm not using the javaws -Xoffline parameter as then IcedTea-Web won't download the application JARs. If you see a JAR no found error on this first start, try again as then IcedTea-Web will have filled the local JAR cache. Depending on the further outcome of these first tries, you might need to adjust the JNLP file, like fixing jar href URLs, changing application parameters or others. For some applications you might also need to add the -nosecurity parameter (javaws -nosecurity myapp.jnlp).

To see more detailed error messages and warnings, run the IcedTea-Web configuration tool:

Linux / macOS:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">itweb-settings.sh</pre>

Windows:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">itweb-settings</pre>

Then enable logging to the terminal at Debugging -\> Enable debugging. Writing the logs to a file can also enabled there. The configuration tool also allows to wipe the local JAR cache which is helpful during implementing the solution: Cache -\> View Files -\> Purge

### **Placing the JNLP file on a Web Server** {#h3-5-placing-the-jnlp-file-on-a-web-server}

This step is not always needed and often not possible, but it allows for easier start of the migrated Applet. This step is about copying the JNLP file to a Web Server and add a link to it from the original Applet start page to help users to migrate away from the Plugin-based Applet.

To make the JNLP file usable from a web page, you need to enter the base URL of that web page into the codebase attribute of the JNLP file. If the web page URL is for example http://mywebsite.domain/somepath/page.html then the base URL is entered into the JNLP file as codebase="http://mywebsite.domain/somepath/".

Now, users can start the application with the following command. If the JNLP file is later updated on the web server, it will automatically be downloaded again in the background before starting the application.

Linux / macOS:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">javaws.sh http://mywebsite.domain/somepath/myapp.jnlp</pre>

Windows:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">javaws http://mywebsite.domain/somepath/myapp.jnlp</pre>

For security reasons I would refrain from registering the JNLP file type with the javaws application on the Operating System as it can be mis-used by malicious web sites and unaware users can be lured to click and download JNLP files and then get it executed locally on their system. Instead, storing a script or start menu entry with the complete javaws command as used above including the JNLP file or URL is often a good solution.
