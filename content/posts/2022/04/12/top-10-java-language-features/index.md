---
title: "Top 10 Java Language Features | Foojay.io Today"
slug: "top-10-java-language-features"
date: "2022-04-12T17:06:07+00:00"
lastmod: "2022-04-12T17:23:55+00:00"
description: "Let's explore ten Java programming features used frequently by developers in their day-to-day programming jobs."
authors:
  - "bazlur-rahman"
image: "Top-10-Java-Language-features.png"
categories:
  - "Java Core"
tags:
related_posts:
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "are-java-security-updates-important"
  - "avoid-java-serialization"
  - "optional-in-java-a-swiss-army-knife-for-handling-nulls-and-improving-code-quality"
enlighterjs: true
frozen: false
---

Every programming language provides ways to express our ideas and then translates them into reality.

Some are unique to that particular language and some are common to many other programming languages.

In this article, I will explore ten Java programming features used frequently by developers in their day-to-day programming jobs.

Collection's factory method {#h2-0-collection-s-factory-method}
---------------------------------------------------------------

Collections are the most frequently used feature in our daily coding. They are used as a container where we store objects and pass them along.

Collections are also used to sort, search, and iterate objects, making the programmer's life easier. It provides a few basic interfaces, such as List, Set, Map, etc., and multiple implementations.

The traditional way of creating Collections and Maps may look verbose to many developers.

That's why Java 9 introduced a few very concise factory methods.

**List:**

<pre class="EnlighterJSRAW" data-enlighter-language="generic">List countries = List.of("Bangladesh", "Canada", "United States", "Tuvalu");</pre>

**Set:**

<pre class="EnlighterJSRAW" data-enlighter-language="java"> Set countries = Set.of("Bangladesh", "Canada", "United States", "Tuvalu");</pre>

**Map:**

<pre class="EnlighterJSRAW" data-enlighter-language="java">Map&lt;String, Integer&gt; countriesByPopulation = Map.of("Bangladesh", 164_689_383,
                                                            "Canada", 37_742_154,
                                                            "United States", 331_002_651,
                                                            "Tuvalu", 11_792);</pre>

These are very convenient when we want to create immutable containers. However, if we're going to create mutable collections, the traditional approach is advised.

If you want to learn more about the collection framework, please visit here: <https://dev.java/learn/the-collections-framework/>.

Local Type Inference {#h2-1-local-type-inference}
-------------------------------------------------

Java 10 introduced type inference for local variables, which is super convenient for developers.

Traditionally Java is a strongly typed language, and developers have to specify types twice while declaring and initializing an object. It seems tedious. Look at the following example-

<pre class="EnlighterJSRAW" data-enlighter-language="java">Map&lt;String, Map&lt;String, Integer&gt;&gt; properties = new HashMap&lt;&gt;();</pre>

We specified the type of information on both sides in the above statement. If we define it in one place, our eyes can easily interpret that this has to be a Map type. The Java language has matured enough, and the Java compiler should be smart enough to understand that. The Local Type inference does precisely that.  

The above code can now be written as follows-

<pre class="EnlighterJSRAW" data-enlighter-language="java">var properties = new HashMap&lt;String, Map&lt;String, Integer&gt;&gt;();
</pre>

Now we have to write type once. The above code may not look a lot less. However, it makes a lot shorter when we call a method and store the result in a variable. Example:

<pre class="EnlighterJSRAW" data-enlighter-language="java">var properties = getProperties();</pre>

Similarly,

<pre class="EnlighterJSRAW" data-enlighter-language="java"> var countries = Set.of("Bangladesh", "Canada", "United States", "Tuvalu");</pre>

Although this seems a handy feature, there is some criticism as well. Some developers would argue that this may reduce readability, which is more important than this little convenience.

To know more about it, visit:

<https://openjdk.java.net/projects/amber/guides/lvti-faq>  
<https://openjdk.java.net/projects/amber/guides/lvti-style-guide>

Enhanced Switch Expression {#h2-2-enhanced-switch-expression}
-------------------------------------------------------------

The traditional switch statement has been in Java from the beginning, which resembled C and C++. It was ok, but as the language evolved, it hasn't offered us any improvement until Java 14. It certainly has some limitations as well. The most infamous was the [fall-through](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/switch.html.):

To tackle the issue, we use break statements which are pretty much boilerplate code. However, Java 14 introduced a new way of looking at this switch statement, and it offers much more rich features.

We no longer need to add break statements; it solves the fall-through problem; on top of that, a switch statement can return a value, which means we can use it as an expression and assign it to a variable.

<pre class="EnlighterJSRAW" data-enlighter-language="java">int day = 5;
String result = switch (day) {
    case 1, 2, 3, 4, 5 -&gt; "Weekday";
    case 6, 7 -&gt; "Weekend";
    default -&gt; "Unexpected value: " + day;
};</pre>

Read more about it: <https://dev.java/learn/branching-with-switch-expressions/>

Records {#h2-3-records}
-----------------------

Although records are relatively new features in Java, released in Java 16, many developers find it super helpful to create immutable objects.

Often we need data career objects in our program to hold or pass values from one method to another, for example- a class to carry x, y and z coordinates which we would write as follows.

<pre class="EnlighterJSRAW" data-enlighter-language="java">package ca.bazlur.playground;

import java.util.Objects;

public final class Point {
    private final int x;
    private final int y;
    private final int z;

    public Point(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int z() {
        return z;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Point) obj;
        return this.x == that.x &amp;&amp;
                this.y == that.y &amp;&amp;
                this.z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return "Point[" +
                "x=" + x + ", " +
                "y=" + y + ", " +
                "z=" + z + ']';
    }

}
</pre>

The class seems super verbose and has little to do with our whole intention. This entire code can be replaced with the following code -

<pre class="EnlighterJSRAW" data-enlighter-language="generic">package ca.bazlur.playground;

public record Point(int x, int y, int z) {
}
</pre>

Read more about records here: <https://nipafx.dev/java-record-semantics/>.

Optional {#h2-4-optional}
-------------------------

A method is a contract: we put thought into it when defining one. We specify parameters with their type and also a return type. We expect it to behave according to the contract when we invoke a method. If it doesn't, it's a violation of the contract.

However, we often get null from a method instead of a value with the specified type. This is a violation. An invoker cannot know upfront unless it invokes it. To tackle this violation, the invoker usually tests the value with an if condition, whether this value is null or not. Example-

<pre class="EnlighterJSRAW" data-enlighter-language="java">public class Playground {

    public static void main(String[] args) {
        String name = findName();
        if (name != null) {
            System.out.println("Length of the name : " + name.length());
        }
    }

    public static String findName() {
        return null;
    }
}</pre>

Look at the above code. The findName() method is supposed to return a String value, but it returns null. The invoker now has to check nulls first to deal with it. If an invoke forgets to do so, they will end up getting NullPointerException which is not expected behaviour.

On the other hand, if the method signature would specify the possibility of not being able to return the value, it would solve all the confusion. And that's where Optional comes into play.

<pre class="EnlighterJSRAW" data-enlighter-language="java">import java.util.Optional;

public class Playground {

    public static void main(String[] args) {
        Optional&lt;String&gt; optionalName = findName();
        optionalName.ifPresent(name -&gt; {
            System.out.println("Length of the name : " + name.length());
        });
    }

    public static Optional&lt;String&gt; findName() {
        return Optional.empty();
    }
}
</pre>

Now we have rewritten the findName() method with Optional, which specified the possibility of not returning any value, and we can deal with it. That gives an upfront warning to the programmers and fixes the violation.

Read more about Optional:

<https://foojay.io/today/lets-use-optional-to-fix-method-contracts/>  
<https://dzone.com/articles/optional-in-java>  
<https://www.baeldung.com/java-optional>

Java Date Time API {#h2-5-java-date-time-api}
---------------------------------------------

Every developer is confused with date-time calculation to some degree. This isn't an overstatement. This was mainly due to not having a good Java API for dealing with Dates and times in Java for a long time.

However, the problem no longer exists because Java 8 brings an excellent API set in **java.time** package that solves all the date time-related issues.

**java.time** package provides many interfaces and classes that solve most problems dealing with date and time, including timezone (which is crazy complex at some point); however, primarily, we use the following classes -

* LocalDate
* LocalTime
* LocalDateTime
* Duration
* Period
* ZonedDateTime etc.

These classes are designed to have all the methods that are commonly needed. e.g.

<pre class="EnlighterJSRAW" data-enlighter-language="java">import java.time.LocalDate;
import java.time.Month;

public class Playground3 {
    public static void main(String[] args) {
        LocalDate date = LocalDate.of(2022, Month.APRIL, 4);
        System.out.println("year = " + date.getYear());
        System.out.println("month = " + date.getMonth());
        System.out.println("DayOfMonth = " + date.getDayOfMonth());
        System.out.println("DayOfWeek = " + date.getDayOfWeek());
        System.out.println("isLeapYear = " + date.isLeapYear());
    }
}
</pre>

Similarly, LocalTime has all the methods required for calculating time.

<pre class="EnlighterJSRAW" data-enlighter-language="java">LocalTime time = LocalTime.of(20, 30);
int hour = time.getHour(); 
int minute = time.getMinute(); 
time = time.withSecond(6); 
time = time.plusMinutes(3);</pre>

We can combine both of them -

<pre class="EnlighterJSRAW" data-enlighter-language="java">LocalDateTime dateTime1 = LocalDateTime.of(2022, Month.APRIL, 4, 20, 30);
LocalDateTime dateTime2 = LocalDateTime.of(date, time);</pre>

How we include timezone -

<pre class="EnlighterJSRAW" data-enlighter-language="java">ZoneId zone = ZoneId.of("Canada/Eastern");
LocalDate localDate = LocalDate.of(2022, Month.APRIL, 4);
ZonedDateTime zonedDateTime = date.atStartOfDay(zone);</pre>

Read more about Java Date Time:

<https://www.infoq.com/articles/java.time>  
<https://docs.oracle.com/javase/tutorial/datetime/TOC.html>

Helpful NullPointerException {#h2-6-helpful-nullpointerexception}
-----------------------------------------------------------------

Every developer hates the Null Pointer Exception. It becomes challenging when StackTrace doesn't provide helpful information it. To demonstrate the problem, let's see an example:

<pre class="EnlighterJSRAW" data-enlighter-language="java">package com.bazlur;

public class Main {

    public static void main(String[] args) {
        User user = null;
        getLengthOfUsersName(user);
    }

    public static void getLengthOfUsersName(User user) {
        System.out.println("Length of first name: " + user.getName().getFirstName());
    }
}

class User {
    private Name name;
    private String email;

    public User(Name name, String email) {
        this.name = name;
        this.email = email;
    }

   //getter
   //setter
}

class Name {
    private String firstName;
    private String lastName;

    public Name(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

   //getter
   //setter
}
</pre>

Look at the main method of the above code. We can see that we will get a null pointer exception. If we run and compile the code with pre Java 14, we will get the following StackTrace:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Exception in thread "main" java.lang.NullPointerException
at com.bazlur.Main.getLengthOfUsersName(Main.java:11)
at com.bazlur.Main.main(Main.java:7)</pre>

This StackTrace is okay, but it has not much information about where and why this NullPointerException happened.

However, in java 14 and onward, we get much more information in the StackTrace, which is super convenient. In java 14, we will get:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Exception in thread "main" java.lang.NullPointerException: Cannot invoke "ca.bazlur.playground.User.getName()" because "user" is null
at ca.bazlur.playground.Main.getLengthOfUsersName(Main.java:12)
at ca.bazlur.playground.Main.main(Main.java:8)</pre>

Read more about it: <https://openjdk.java.net/jeps/358>

CompletableFuture {#h2-7-completablefuture}
-------------------------------------------

We write program line and line, and typically it gets executed line by line. However, there are times when we want relatively parallel execution to make the program faster. To accomplish that, we usually consult Java Thread.

Well, Java thread programming is not always about parallel programming. Instead, it gives us a way to compos multiple independent units of a program to be executed independently to make progress along with others, and often they run asynchronously.

However, thread programming and its intricacies seem dreadful. Most junior and intermediate developers struggle with it. That's why Java 8 brings a more straightforward API that lets us accomplish a portion of the program to run asynchronously. Let's see an example-

Let's assume we have to call three rest APIs and then combine the result. We can call them one by one. If each of them takes around 200 milliseconds, then the total time to fetch all of them would take 600 milliseconds.

What if we could run them parallelly, as modern CPU has multicores in them, so they can easily handle three rest calls in three different CPUs. Using the CompletableFuture, we can easily accomplish that.

<pre class="EnlighterJSRAW" data-enlighter-language="java">package ca.bazlur.playground;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class SocialMediaService {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var service = new SocialMediaService();

        var start = Instant.now();
        var posts = service.fetchAllPost().get();
        var duration = Duration.between(start, Instant.now());

        System.out.println("Total time taken: " + duration.toMillis());
    }

    public CompletableFuture&lt;List&lt;String&gt;&gt; fetchAllPost() {
        var facebook = CompletableFuture.supplyAsync(this::fetchPostFromFacebook);
        var linkedIn = CompletableFuture.supplyAsync(this::fetchPostFromLinkedIn);
        var twitter = CompletableFuture.supplyAsync(this::fetchPostFromTwitter);

        var futures = List.of(facebook, linkedIn, twitter);

        return CompletableFuture.allOf(futures.toArray(futures.toArray(new CompletableFuture[0])))
                .thenApply(future -&gt; futures.stream()
                        .map(CompletableFuture::join)
                        .toList());
    }
    private String fetchPostFromTwitter() {
        sleep(200);
        return "Twitter";
    }

    private String fetchPostFromLinkedIn() {
        sleep(200);
        return "LinkedIn";
    }

    private String fetchPostFromFacebook() {
        sleep(200);
        return "Facebook";
    }

    private void sleep(int millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
</pre>

Read more about it:

<https://www.linkedin.com/pulse/asynchronous-programming-java-completablefuture-aliaksandr-liakh/>

Lambda Expression {#h2-8-lambda-expression}
-------------------------------------------

Lambda Expression is probably the most powerful feature in the Java language. It reshaped the way we write code. A Lambda expression is like an anonymous function that can take arguments and return a value.

We can assign the function to a variable and pass it to a method as arguments, and a method can return it. It has a body. The only difference from a method is that it doesn't have a name.

The expressions are short and concise. It usually doesn't contain much boilerplate code. Let's see an example:

We want to list all the files from a directory with the .java extension.

<pre class="EnlighterJSRAW" data-enlighter-language="java">var directory = new File("./src/main/java/ca/bazlur/playground");
String[] list = directory.list(new FilenameFilter() {
    @Override
    public boolean accept(File dir, String name) {
        return name.endsWith(".java");
    }
});</pre>

If you carefully look at the piece of the code, we passed an anonymous inner class to the method list(). In the inner class, we put the logic to filter out the files.

Essentially we are interested in this piece of logic, not the boilerplate around the logic.

Lambda expression, in fact, allows us to remove all the boilerplate, and we can write the code that we care about. Example:

<pre class="EnlighterJSRAW" data-enlighter-language="java">var directory = new File("./src/main/java/ca/bazlur/playground");
String[] list = directory.list((dir, name) -&gt; name.endsWith(“.java"));</pre>

Well, I have just shown you one example here, but there are plenty of other benefits of the lambda expression.

Read more about it: <https://dev.java/learn/lambda-expressions/>

Stream API {#h2-9-stream-api}
-----------------------------

> "Lambda Expressions are the gateway drug to Java 8, but Streams are the real addiction."  
>
> - Venkat Subramaniam.

In our day-to-day programming jobs, one common task we do frequently is to process a collection of data. There are a few common operations such as filtering, converting and collecting the result.

Pre Java8, these sorts of operations were inherently imperative. We had to write code for our intention (aka what we want to achieve) and how we want it.

With the invention of the Lambda expression and stream API, we can now write out data processing functionality rather declaratively. We only specify our intention, but we don't have to write how we get the result. Let's see an example-

We have a list of Books, and we want to find all the Java books' names comma-separated and sorted.

<pre class="EnlighterJSRAW" data-enlighter-language="java">public static String getJavaBooks(List&lt;Book&gt; books) {
    return books.stream()
            .filter(book -&gt; Objects.equals(book.language(), "Java"))
            .sorted(Comparator.comparing(Book::price))
            .map(Book::name)
            .collect(Collectors.joining(", "));
}</pre>

The above code is simple, readable and concise. The alternative imperative code would be-

<pre class="EnlighterJSRAW" data-enlighter-language="java">public static String getJavaBooksImperatively(List&lt;Book&gt; books) {
    var filteredBook = new ArrayList&lt;Book&gt;();
    for (Book book : books) {
        if (Objects.equals(book.language(), "Java")){
            filteredBook.add(book);
        }
    }
    filteredBook.sort(new Comparator&lt;Book&gt;() {
        @Override
        public int compare(Book o1, Book o2) {
            return Integer.compare(o1.price(), o2.price());
        }
    });

    var joiner = new StringJoiner(",");
    for (Book book : filteredBook) {
        joiner.add(book.name());
    }

    return joiner.toString();
}</pre>

Although both methods return the same value, we see the difference clearly.

Learn more about stream API:

<https://dev.java/learn/the-stream-api/>  
<https://jenkov.com/tutorials/java-functional-programming/streams.html>

That's all for today. Cheers!
