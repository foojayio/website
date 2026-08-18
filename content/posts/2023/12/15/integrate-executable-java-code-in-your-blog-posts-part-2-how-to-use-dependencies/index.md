---
title: "Integrate executable Java code in your blog posts, part 2: How to use dependencies"
slug: "integrate-executable-java-code-in-your-blog-posts-part-2-how-to-use-dependencies"
date: "2023-12-15T10:07:55+00:00"
lastmod: "2023-12-15T10:12:29+00:00"
description: "Not only \"simple\" Java code can be added: a more complex class with a record and Maven dependencies can be created and executed on Foojay."
authors:
  - "frankdelporte"
image: "defdeps.png"
categories:
  - "Foojay"
  - "Tutorials"
tags:
related_posts:
  - "integrate-executable-java-code-in-your-blog-posts"
  - "how-to-submit-your-next-article-on-foojay-io"
  - "join-slack-com-t-foojay-signup"
  - "interview-with-gokul-chandrasekaran-the-creator-of-jdoodle"
jdoodle: true
enlighterjs: true
frozen: false
---

In a previous post, we [explained how you can add executable Java code to your posts here on Foojay](https://foojay.io/today/integrate-executable-java-code-in-your-blog-posts/), by using JDoodle. In this post, you will learn how you can extend this with one or more libraries.

## Define Dependencies

To integrate Java code in your post or page that needs dependencies, you need to specify them in the initial `div` with `data-libs`.

```
<div data-pym-src='https://www.jdoodle.com/plugin' 
   data-language="java" 
   data-version-index="4"
   data-client-id="34d6e81ae45d88cdb9fb98fed1415b81" 
   data-libs="mavenlib1,mavenlib2">
   <div data-type="script"><xmp>

     // This is the place to put your Java code

   </xmp></div>
</div>
<script src="https://www.jdoodle.com/assets/jdoodle-pym.min.js" type="text/javascript"></script>
```

The `data-client-id` can only be used for the Foojay website! Create your own [account on the JDoodle site](https://www.jdoodle.com) if you want to use this functionality on another website.

## Example Application

For example, let's use the Jackson library to parse JSON.

```
<div data-pym-src='https://www.jdoodle.com/plugin' 
   data-language="java" 
   data-version-index="4"
   data-client-id="34d6e81ae45d88cdb9fb98fed1415b81" 
   data-libs="com.fasterxml.jackson.core:jackson-annotations:2.16.0,com.fasterxml.jackson.core:jackson-core:2.16.0,com.fasterxml.jackson.core:jackson-databind:2.16.0">
   <div data-type="script"><xmp>
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;

public class JsonParsing {
    public static void main (String[] args) {
        var json = """
                [
                    { 
                        "level": 0,
                        "timestamp": 1675867184342,
                        "message": "Program started"
                    },
                    {
                        "level": 5,
                        "timestamp": 1675867185921,
                        "message": "File X not found"
                    },
                    {
                        "level": 9,
                        "timestamp": 1675867186357,
                        "message": "Error at line Y"
                    }
                ]
                """;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            LogMessage[] logMessages = objectMapper.readValue(json, LogMessage[].class);

            System.out.println("Data loaded from JSON:\n");

            for (LogMessage logMessage : logMessages) {
                System.out.println("Log message at " + logMessage.timestamp()
                        + "\n\tSeverity: " + logMessage.level()
                        + "\n\tMessage: " + logMessage.message());
            }
        } catch (IOException ex) {
            System.err.println("Json processing exception: " + ex.getMessage());
        }
    }

    record LogMessage(int level, Long timestamp, String message) { }
}
   </xmp></div>
</div>
<script src="https://www.jdoodle.com/assets/jdoodle-pym.min.js" type="text/javascript"></script>
```

Will produce the following output. Hit the "Execute" button to run the code.

<div data-pym-src="https://www.jdoodle.com/plugin" data-language="java" data-version-index="4" data-client-id="34d6e81ae45d88cdb9fb98fed1415b81" data-libs="com.fasterxml.jackson.core:jackson-annotations:2.16.0,com.fasterxml.jackson.core:jackson-core:2.16.0,com.fasterxml.jackson.core:jackson-databind:2.16.0">
 <div data-type="script">
  <xmp>
   import com.fasterxml.jackson.annotation.JsonCreator; import com.fasterxml.jackson.annotation.JsonIgnore; import com.fasterxml.jackson.annotation.JsonValue; import com.fasterxml.jackson.databind.ObjectMapper; import java.io.IOException; import java.time.Instant; import java.time.ZoneId; import java.time.ZonedDateTime; import java.util.Arrays; public class JsonParsing { public static void main (String[] args) { var json = """ [ { "level": 0, "timestamp": 1675867184342, "message": "Program started" }, { "level": 5, "timestamp": 1675867185921, "message": "File X not found" }, { "level": 9, "timestamp": 1675867186357, "message": "Error at line Y" } ] """; try { ObjectMapper objectMapper = new ObjectMapper(); LogMessage[] logMessages = objectMapper.readValue(json, LogMessage[].class); System.out.println("Data loaded from JSON:\n"); for (LogMessage logMessage : logMessages) { System.out.println("Log message at " + logMessage.timestamp() + "\n\tSeverity: " + logMessage.level() + "\n\tMessage: " + logMessage.message()); } } catch (IOException ex) { System.err.println("Json processing exception: " + ex.getMessage()); } } record LogMessage(int level, Long timestamp, String message) { } }
  </xmp>
 </div>
</div>

**Notice you can also select Java 21 now to execute this code!**

## Conclusion

Not only "simple" Java code can be added to blog posts. As we illustrated here, a more complex class with a record and Maven dependencies can be created and executed within a webpage.

In part 3 of the JDoodle posts, that will be published soon, we'll talk with the creator of JDoodle, [Gokul Chandrasekaran](https://www.linkedin.com/in/gokulchandrasekaran-jdoodle?miniProfileUrn=urn%3Ali%3Afs_miniProfile%3AACoAAANJfEYBcRBniUXnKroUIsiftQzJwkwXl4I).
