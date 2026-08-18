---
title: "Spring Boot, Java 16, and New Java Records"
date: "2021-03-23T09:55:23+00:00"
lastmod: "2021-07-12T04:56:01+00:00"
description: "Let's discuss Records, an official feature in Java 16, and apply this knowledge in conjunction with a Spring Boot application!"
canonical: "https://ashishtechmill.com/spring-boot-and-java-16-records"
authors:
  - "yrashish"
image: "jan-kopriva-AAMaDdDHLFg-unsplash.jpg"
categories:
  - "Records"
tags:
related_posts:
frozen: false
---

In this article, we will discuss Records, which is an official feature in Java 16, and we will apply this knowledge while using it in conjunction with a Spring Boot application.

### Introduction

On March 16th, 2021, Java 16 was GA. With this new release, tons of new exciting features are added. Check out [the highlights here on Foojay](https://foojay.io/today/highlights-of-new-jeps-in-java-16/) to know more about these changes.

In this article, we'll focus on Java Records, defined in [JEP 395](https://openjdk.java.net/jeps/395). Records were first introduced in JDK 14 as a preview feature, proposed by JEP 359, and with JDK 15 they remained in preview with JEP 384. However, with JDK 16, Records are now no longer in preview: they're an official part of the Java language now.

I have picked Records because they are definitely the most favored feature added in Java 16, according to this Twitter poll by Java Champion Mala Gupta:
> The release of [#Java16](https://twitter.com/hashtag/Java16?src=hash&ref_src=twsrc%5Etfw) is around the corner.
>
> Please vote for your favorite Java 16 language feature:
>
> --- Mala Gupta (@eMalaGupta) [March 4, 2021](https://twitter.com/eMalaGupta/status/1367274033184575497?ref_src=twsrc%5Etfw)

I also conducted a similar survey, but it was focused on features from Java 8 onwards. The results were not unexpected as Java 8 is still widely used, which is very unfortunate, though, as tons of new features and improvements have been added to newer Java versions. But in terms of features, Java 8 was definitely a game-changer from a developer perspective.
> Which feature from [@java](https://twitter.com/java?ref_src=twsrc%5Etfw) 8 and onwards do you like the most?[@javarevisited](https://twitter.com/javarevisited?ref_src=twsrc%5Etfw) [@Java_Champions](https://twitter.com/Java_Champions?ref_src=twsrc%5Etfw) [@java_masters](https://twitter.com/java_masters?ref_src=twsrc%5Etfw) [@Oraclejavamag](https://twitter.com/Oraclejavamag?ref_src=twsrc%5Etfw) [@javacodegeeks](https://twitter.com/javacodegeeks?ref_src=twsrc%5Etfw) [@piotr_minkowski](https://twitter.com/piotr_minkowski?ref_src=twsrc%5Etfw) [@vlad_mihalcea](https://twitter.com/vlad_mihalcea?ref_src=twsrc%5Etfw)[#100DaysOfCode](https://twitter.com/hashtag/100DaysOfCode?src=hash&ref_src=twsrc%5Etfw) [#MovedByJava](https://twitter.com/hashtag/MovedByJava?src=hash&ref_src=twsrc%5Etfw) [#100daysofjava](https://twitter.com/hashtag/100daysofjava?src=hash&ref_src=twsrc%5Etfw) [#java](https://twitter.com/hashtag/java?src=hash&ref_src=twsrc%5Etfw)
>
> --- Ashish Choudhary 👨🏻‍💻🧔🏻👨‍👩‍👦 (@iASHeeesh) [January 24, 2021](https://twitter.com/iASHeeesh/status/1353207204761899010?ref_src=twsrc%5Etfw)

For some more Java and tech-related stuff, follow me on [Twitter](https://twitter.com/iASHeeesh)

So let's discuss what's the fuss is about Java Records.

### What are Records?

As per JEP 395:
> Records are a new kind of class in the Java language. They act as transparent carriers for immutable data with less ceremony than normal classes. Records can be thought of as nominal tuples.

Another quote from the JEP clearly explains developers' frustration while writing typical data carrier classes.
> Properly writing such a data-carrier class involves a lot of low-value, repetitive, error-prone code: constructors, accessors, equals, hashCode, toString, etc. For example, a class to carry x and y coordinates inevitably ends up like this:

```java
class Point {
    private final int x;
    private final int y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    int x() { return x; }
    int y() { return y; }

    public boolean equals(Object o) {
        if (!(o instanceof Point)) return false;
        Point other = (Point) o;
        return other.x == x && other.y == y;
    }

    public int hashCode() {
        return Objects.hash(x, y);
    }

    public String toString() {
        return String.format("Point[x=%d, y=%d]", x, y);
    }
}
```

Another option that we developers use most often is to leave the handling of the boilerplate to the IDE. For example, with Intellij, you can generate constructors, getters, setters, equals, hashCode, and toString, etc., by simply pressing Command + N shortcut key. But the boilerplate code is still there.

![image.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1616085356856/X6aLicL5f.png)

With Java 16 Records, it's just one line of code. Cool, isn't it:

```java
record Point(int x, int y) { }
```

Here a record class declaration consists of a name, optional type parameters, a header, and a body.

### Demystifying Records

The internals of the Java Record class can be checked using a decompiler that comes with IntelliJ IDE, for example, or you can use the `javap` command-line utility. To understand the internals, I've created the following Record class:

```java
public record State(String name, String capital) {}
```

Following is the decompiled Java Record class. I have used the `javap` command-line utility to check class file internals.

```
$ javap State.class
```

Following is the output:

```java
Compiled from "State.java"
public final class com.example.indianstates.State extends java.lang.Record {
  public com.example.indianstates.State(java.lang.String, java.lang.String);
  public final java.lang.String toString();
  public final int hashCode();
  public final boolean equals(java.lang.Object);
  public java.lang.String name();
  public java.lang.String capital();
}
```

You can conclude the following from the above output.

1. State record class is extending the `java.lang.Record` abstract class.
2. State record class is declared final cannot be extended further using extends keyword.
3. hashCode(), equals(), toString() and a canonical constructor are implicitly generated for us.
4. There are no setters or getters, only accessors.
5. With no setters and final in the class declaration clarifies that you cannot change the state, and hence records are immutable.

You can further validate these points by writing tests as well:

```java
@Test
 public void testRecordAccessors() {
     String name = "Maharashtra" ;
     String capital = "Mumbai" ;
     State state = new State("Maharashtra", "Mumbai");
     Assert.assertEquals(name, state.name());
     Assert.assertEquals(capital, state.capital());
 }

 @Test
 public void testRecordToString() {
     State state = new State("Maharashtra", "Mumbai");
     System.out.println(state);
     //Output:-State[name=Maharashtra, capital=Mumbai]
 }

 @Test
 public void testRecordEquals() {
     State state1 = new State("Maharashtra", "Mumbai");
     State state2 = new State("Maharashtra", "Mumbai");
     Assert.assertTrue(state1.equals(state2));
 }

 @Test
 public void testRecordHashCode() {
     State state1 = new State("Maharashtra", "Mumbai");
     State state2 = new State("Maharashtra", "Mumbai");
     Assert.assertEquals(state1.hashCode(), state2.hashCode());
 }
```

```

```

There are some restrictions in the declaration of record classes as compared to normal classes. Checkout [JEP 395](https://openjdk.java.net/jeps/395) for such restrictions.

### Lombok and Records, Friends or Foe?.

You are probably already using [Lombok](https://projectlombok.org/) annotations such as @[Value](https://projectlombok.org/features/Value), which is closest if not the same to the Java records. Then you can get rid of one dependency and those Christmas trees of annotations. I might have oversimplified things, and it may make sense to replace Lombok for some cases. But you might be using Lombok for other features and not just one annotation that it provides. And believe me, while Java Records are a welcome feature for Java lovers, but it's not going to replace Lombok, at least for now. You do not believe me? Check out this [answer](https://stackoverflow.com/a/61325018/1472027) from Brain Goetz on StackOverflow.

And be careful with what dependencies you add to your project as the problem that is part of that dependency becomes your problem too.

### Spring Boot and Java Records

From version 2.5.0-M1 onwards, Spring Boot provides preliminary [support](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.5.0-M1-Release-Notes#java-16-support) for Java 16. I have a working Spring Boot application that I will use to demo Java records with it. The source code is available [here](https://github.com/yrashish/indian-states/tree/develop).

This is a simple Spring Boot application which, when accessed via /states or /state?name=statename REST endpoint show all or specific Indian states and their capitals. This application uses an in-memory H2 database that inserts rows at the start of the application.

As always, you can use start.spring.io to generate stubs for your Spring Boot application, and as explained earlier, make sure that you select the 2.5.x milestone version.

Here is what the REST controller class looks like:

```java
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Controller {
    private final StateService stateService;

    public Controller(StateService stateService) {
        this.stateService = stateService;
    }

    @GetMapping("/states")
    private List<State> getAllStates() {
        return stateService.findAll();
    }

    @GetMapping(value = "/state")
    private String getSpecificState(@RequestParam(required = false, name = "name", defaultValue = "Maharashtra") String name) {
        return stateService.findByName(name);
    }
}
```

We can focus on the getAllStates() method, which returns a list of State record class objects.

We have already seen the State record class. There is no change in that.

```java
public record State(String name, String capital) {}
```

Following is the `StateRepository` interface implemented by the `StateService` class:

```java
import java.util.List;

public interface StateRepository {
    List<State> findAll();

    String findByName(String name);
}
@Service
public class StateService implements StateRepository{
    private final JdbcTemplate jdbcTemplate;

    public StateService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    private final RowMapper<State>  rowMapper = (rs, rowNum) -> new State(rs.getString("name"),rs.getString("capital"));

    @Override
    public List<State> findAll() {
        String findAllStates = """
                select * from States
                """;
        return jdbcTemplate.query(findAllStates, rowMapper);
    }

    @Override
    public String findByName(String name) {
        String findByName = """
                select capital from States where name = ?;
                """;
        return jdbcTemplate.queryForObject(findByName, String.class, name);
    }
}
```

`StateService` is autowired using the constructor of the `Controller` class. It has a method named findAll() that uses Spring JdbcTemplate to query and returns a `State` record class list from the in-memory H2 database. As you can see, we have used the `RowMapper` functional interface, which JdbcTemplate uses for mapping rows of a ResultSet on a per-row basis, and it returns the Row object for the current row. We have also used the `new` keyword to initialize the record class, which means we can initialize the record class like normal classes in Java. I have also used the Java 15 [Text Blocks](https://openjdk.java.net/jeps/378) feature, which helps in the readability of SQL queries and JSON string values.

However, there were some issues when I started using records with this application. Earlier I was using `BeanPropertyRowMapper,` which resulted in the following exception when I disabled Lombok and used Records instead for the `State` class.

```
2021-03-19 02:01:55.434 ERROR 66059 --- [nio-8080-exec-1] o.a.c.c.C.[.[.[/].[dispatcherServlet]    : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed; nested exception is org.springframework.beans.BeanInstantiationException: Failed to instantiate [com.example.indianstates.State]: No default constructor found; nested exception is java.lang.NoSuchMethodException: com.example.indianstates.State.<init>()] with root cause
```

```

```

From the exception and `BeanPropertyRowMapper` [documentation](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/BeanPropertyRowMapper.html) it is pretty clear that we must declare a default or no-arg constructor in the `State` records class, which leads to some interesting discoveries about the records classes in Java.

To solve this error, I naively added a no-arg constructor to the `State` record class.

```java
public record State(String name, String capital) {
    public State() {
    }
}
```

But, that resulted in the following compilation error.
> Non-canonical record constructor must delegate to another constructor

To solve this compilation, I added the following constructor, but this will make the values `null` in the response:

```java
public record State(String name, String capital) {
    public State() {
        this(null,null);
    }
}
```

Then I took the help of the IntelliJ feature to generate the constructor for this record class. It provided me with the following options.

![image.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1616100878502/6syBh6OHq.png)

I tried these options but got the same result. I already knew that these options wouldn't work, but I tried my luck, which makes me wonder how to use records with `BeanPropertyRowMapper.` I don't have an answer for this right now, but I will dig this further. If you see any issue with the code or have a better answer, then let me know.

Update:- I posted these exceptions to Spring Boot Gitter chat and got this answer.
> BeanPropertyRowMapper can't be used with records since it consists of creating an instance of a Java Bean with its no-arg constructor and then calling its setters to populate the bean. But records don't have a no-arg constructor and are immutable and thus don't have setters. So, either use a traditional Java Bean or use records, but then don't use BeanPropertyRowMapper.

Fair enough. So clearly `BeanPropertyRowMapper` cannot be used with records. It's a wrap for now. Happy coding.

### Conclusion

In this article, you have learned that Records are immutable data carrier classes that reduce lots of boilerplate code that we are used to writing. Then we looked at the internals of a Record class and discovered that hashCode(), equals(), toString(), and constructors are implicitly generated for us by the compiler. Then we learned that you should not really compare or replace Records with external libraries like Lombok because both are different tools for different things.

In the last section, we discovered that Records are good for use in cases such as getting data from a database (or some external service) with an example of the Spring Boot application. We also discovered some issues while using `BeanPropertyRowMapper,` and concluded that we could not use it with records.

### Support me

If you like what you just read, then you can buy me a coffee by clicking the link in the image below:

[![Buy Me A Coffee](https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png)](https://www.buymeacoffee.com/meashish)
