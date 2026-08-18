---
title: "Daemon Thread Java Code Quiz"
slug: "daemon-thread-java-code-quiz"
date: "2021-09-08T07:29:56+00:00"
lastmod: "2021-10-07T10:23:18+00:00"
description: "In your Java applications, do you know if the main Thread is a daemon or not? And do you know what a daemon Thread is?"
authors:
  - "rafael-del-nero"
image: "daemon_thread.png"
categories:
  - "Tutorials"
tags:
related_posts:
  - "java-thread-programming-part-1"
  - "eight-debugging-tips-for-intellijidea-users-you-never-knew-existed"
  - "a-closer-look-at-jfr-streaming"
  - "jedi-lambda-join-java-challenge"
enlighterjs: true
frozen: false
---

When we are working with Threads, it's important to know when to use a daemon or a non-daemon Thread.

Do you know if the main Thread is a daemon or not? And do you know what a daemon Thread is?

That's what you will find out by trying out the following Java Challenge!

It's time to improve your Java skills with this Daemon Thread Challenge...

Daemon Thread Challenge
-----------------------

What will happen when running the following main method?

```java
public class DaemonThreadChallenge implements Runnable {

        public static void main(String... doYourBest) {
            Thread thread = new Thread(new DaemonThreadChallenge());
            thread.setDaemon(true);
            thread.start();
        }

        @Override
        public void run() {
            for (;;) {
                System.out.println("For ever");
            }
        }
}
```


A) It will print "Forever" for an indeterminate time and the program will finish normally.  

B) It will throw StackOverflowError because of the infinite looping.  

C) It will print "Forever" infinitely.

**Explanation**:

The amount of time that `"Forever"` will be printed is indeterminate because it will depend on the time from the main thread execution.

There are two types of threads:

* **Non-daemon**: the main method is executed with a non-daemon thread behind the scenes and this thread will be executed until the end.
* **Daemon**: it will die if all non-daemon threads have finished. So in this quiz, if the main method is finished, the daemon thread we created will be finished as well because all daemon threads depend on a non-daemon thread to continue its execution.

Therefore, the final answer will be... what do you think?

That's it challenger, keep pushing your limits to be a better software developer!
