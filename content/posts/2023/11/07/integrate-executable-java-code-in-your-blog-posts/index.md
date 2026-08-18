---
title: "Integrate executable Java code in your blog posts"
slug: "integrate-executable-java-code-in-your-blog-posts"
date: "2023-11-07T14:49:57+00:00"
lastmod: "2023-11-08T09:05:17+00:00"
description: "Thanks to JDoodle you can now also add executable code to your Foojay content!"
authors:
  - "frankdelporte"
image: "jdoodle-example.png"
categories:
  - "Foojay"
  - "Java Beginner"
  - "Tutorials"
tags:
related_posts:
  - "how-to-submit-your-next-article-on-foojay-io"
  - "how-to-add-an-event-to-the-foojay-event-calendar"
  - "join-slack-com-t-foojay-signup"
  - "interview-with-gokul-chandrasekaran-the-creator-of-jdoodle"
jdoodle: true
frozen: false
---

While developing the [Foojay Quickstart Java Tutorial](https://foojay.io/java-quick-start/), I was looking for an easy way to integrate runnable Java code examples into the Foojay pages and blogs. That's when I discovered [jdoodle.com](https://www.jdoodle.com/). I started by using their online editor, but with this blog I want to show you an even easier method to integrate runnable code here on Foojay.

## Integration examples

### Single file code

To integrate "plain" Java code in your post or page, use the following syntax and add it as "Custom HTML" widget:

```
<div data-pym-src='https://www.jdoodle.com/plugin' 
   data-language="java" 
   data-version-index="4">
     // This is the place to put the code
</div>
<script src="https://www.jdoodle.com/assets/jdoodle-pym.min.js" type="text/javascript"></script>
```

For example, this "Custom HTML":

```
<div data-pym-src='https://www.jdoodle.com/plugin' 
   data-language="java" 
   data-version-index="4">
public class MainArguments {
    public static void main (String[] args) {
        System.out.println("Number of arguments: " + args.length);
        if (args.length > 0) {
            System.out.println("First argument: " + args[0]);
        }
        for (int i = 0; i < args.length; i++) {
            System.out.println("Argument " + (i + 1) + ": " + args[i]);
        }
    }
}
</div>
<script src="https://www.jdoodle.com/assets/jdoodle-pym.min.js" type="text/javascript"></script>
```

Will produce the following output. Hit the "Execute" button to run the code.

<div data-pym-src="https://www.jdoodle.com/plugin" data-language="java" data-version-index="4">
 public class MainArguments { public static void main (String[] args) { System.out.println("Number of arguments: " + args.length); if (args.length &gt; 0) { System.out.println("First argument: " + args[0]); } for (int i = 0; i 
 <p class="wp-block-paragraph"></p>
 <p class="wp-block-paragraph">As you can see, the reader of your Foojay post can modify the code, execute it, and even add CommandLine Arguments to change the behavior of the code as you can see in this screenshot:</p>
 <figure class="wp-block-image size-medium">
  <img fetchpriority="high" decoding="async" width="629" height="510" src="jdoodle-example-629x510.png" alt="" class="wp-image-102637">
 </figure>
 <h3 class="wp-block-heading" id="h3-2-code-with-external-data-files">Code with external data files</h3>
 <p class="wp-block-paragraph">In one of the more advanced tutorial steps, I wanted to read data from a text file. This can also be done with JDoodle, but needs a slightly different "Custom HTML" block that looks like this:</p>
 
```
<div data-pym-src="https://www.jdoodle.com/plugin" 
   data-version-index="4"
   data-language="java" 
   data-client-id="34d6e81ae45d88cdb9fb98fed1415b81" 
   data-has-files="true">
   <div data-type="file" data-file-name="testdata.csv">
        // This is the place to put the text data
   </div>
   <div data-type="script"><xmp>
        // This is the place to put the code
   </xmp></div>
</div>
<script src="https://www.jdoodle.com/assets/jdoodle-pym.min.js" type="text/javascript"></script>
```

 <p class="wp-block-paragraph">The <code>data-client-id</code> is important here to allow the use of external files, but is only valid when used on the Foojay website! Create your own <a target="_blank" href="https://www.jdoodle.com">account on the JDoodle site</a> if you want to use this functionality on another website.</p>
 <p class="wp-block-paragraph">This is a simple example to read data from a CSV file:</p>
 
```
<div data-pym-src="https://www.jdoodle.com/plugin" 
   data-version-index="4"
   data-language="java" 
   data-client-id="34d6e81ae45d88cdb9fb98fed1415b81" 
   data-has-files="true">
   <div data-type="file" data-file-name="testdata.csv">
1,Ada,Gomez,40,Mabvob Pike,Radafso,LA,60500
2,Bernard,Jordan,28,Dotcu Court,Cewbufbim,MS,17422
   </div>
   <div data-type="script"><xmp>
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ReadTextFile {
    public static void main (String[] args) {
        File file = new File("/uploads/testdata.csv");
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        } catch (FileNotFoundException ex) {
            System.err.println("Could not find the file to be loaded");
        }
    }
}
   </xmp></div>
</div>
<script src="https://www.jdoodle.com/assets/jdoodle-pym.min.js" type="text/javascript"></script>
```

 <p class="wp-block-paragraph"></p>
 <p class="wp-block-paragraph">A full example with this approach can be found in the tutorial: <a href="https://foojay.io/java-quick-start/quick-start-tutorial/reading-a-text-file/">"Reading a Text File"</a>.</p>
 <h2 class="wp-block-heading" id="h2-3-conclusion">Conclusion</h2>
 <p class="wp-block-paragraph">JDoodle allows to experiment with code in the browser and provides many other easy tools. Thanks to JDoodle you can now also add executable code to your Foojay content!</p>
 <p class="wp-block-paragraph">In a next post that will be published soon, we'll talk with the creator of JDoodle, <a target="_blank" href="https://www.linkedin.com/in/gokulchandrasekaran-jdoodle?miniProfileUrn=urn%3Ali%3Afs_miniProfile%3AACoAAANJfEYBcRBniUXnKroUIsiftQzJwkwXl4I">Gokul Chandrasekaran</a>.</p>
</div>
