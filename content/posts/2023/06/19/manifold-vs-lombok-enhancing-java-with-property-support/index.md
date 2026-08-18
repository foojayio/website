---
title: "Manifold vs. Lombok: Enhancing Java with Property Support"
date: "2023-06-19T18:04:59+00:00"
lastmod: "2023-06-19T18:05:00+00:00"
description: "Two decades ago we worked on properties in Java. Lombok filled in that gap. Manifold aims to solve this same problem. Is it ready?"
canonical: "https://debugagent.com/manifold-vs-lombok-enhancing-java-with-property-support"
authors:
  - "shai-almog"
image: "thumbnail-23.jpg"
categories:
  - "IntelliJ IDEA"
  - "Tutorials"
tags:
related_posts:
  - "java-string-templates-today"
  - "revolutionize-json-parsing-in-java-with-manifold"
  - "spring-boot-debugging-with-aspect-oriented-programming-aop"
frozen: false
---

Today, we'll explore an intriguing aspect of Manifold: its property support.

In comparison, we'll also examine Lombok, a popular library that offers similar features.

By analyzing the differences between these two projects, we can gain insight into their core philosophies and advantages.

Let's dive in and discover how Manifold's property support stands out.

{{< youtube z-9jSMq_Yk8 >}}

As always, you can find the code examples and additional resources for this post on my [GitHub page](https://github.com/shai-almog/java-book/).

## Comparing Manifold and Lombok

Manifold and Lombok are very different projects but they do overlap in a few areas. Properties are where the overlap is greatest and the philosophical differences between the two shine out the most.

Let's explore these differences in detail, highlighting the strengths and limitations of each. The following table provides a high-level comparison:

|                   |        **Lombok**        | **Manifold**  |
|-------------------|--------------------------|---------------|
| **Maturity**      | Old                      | New           |
| **IDE Support**   | All Major IDEs Supported | IntelliJ/IDEA |
| **Exit Strategy** | Easy                     | None          |
| **Extensibility** | Challenging              | Modular       |
| **Scope**         | Limited                  | Extensive     |

### Maturity, IDE Support and Exit Strategy

Lombok has been around for quite some time, and many of its features were designed for older versions of Java. Consequently, some of its functionalities may no longer be relevant or applicable.

Furthermore, Lombok's integration with modern IDEs can be limited, and converting Lombok code back to plain Java sources may not be straightforward.

While Lombok provides essential boilerplate removal, it is often perceived as a temporary solution or "band-aid" due to its underlying approach.

### Manifold's Extensibility and Scope

Manifold offers a more extensible and pluggable framework with a well-designed architecture.

While it may still face growing pains and limited IDE support, Manifold's flexibility allows for more ambitious projects and future enhancements.

It leverages the strengths of Java's type system while providing unique features.

## Property Support

Manifold's property support is based on a concept that has been discussed for decades in the Java community. However, reaching a consensus on the direction proved challenging so a JEP never took hold.

On a personal note, I was deeply involved with this discussion and strongly advocated for object properties. With Valhalla, it's possible that object properties will become the golden standard moving forward.

Manifold takes a more standard approach that aligns with other languages such as C# and Kotlin, providing familiar and powerful property notation. This is good, it can provide short-term relief to the verbosity of getters and setters.

### Differentiating Manifold's Property Support

To understand Manifold's property support, let's examine its equivalent code to the Lombok example. This is a standard Plain Old Java Object (POJO). Notice the get and set methods?

Those are standard Java properties, powerful but heavy on the boilerplate:

```
public class HelloPojo {
    private int number;
    private String str;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }
}
```

We can write the same code using Lombok as such:

```
@Getter
@Setter
public class HelloLombok {
    private int number;
    private String str;
}
```

The usage is identical:

```
obj.setNumber(5);
```

This is problematic. You will notice we define the fields as private, yet suddenly a `setNumber()` method appears out of thin air. It feels weird. I get the logic though.

The creators of Lombok wanted it to be a drop-in replacement. You could write the rest of the code with setters so if you choose to remove Lombok you don't need to edit the rest of the code. It still stands out as weird.

The Manifold equivalent is similar but has some nuances:

```
public class HelloProperties {
    @var int number;
    @var String str;
}
```

One notable distinction is that Manifold's properties are defined at the individual field level, rather than applying to an entire class. Although Manifold may introduce a similar feature to Lombok's Data annotation in the future, it does not currently exist. We need to explicitly define each property.

By default, Manifold makes the property private and generates public accessors (getters and setters). However, just like Lombok, Manifold allows customization through annotations.

The big difference is in the usage. Unlike Lombol, Manifold enables accessing the property directly, eliminating the need for explicit method calls. This behavior applies not only to properties but also to regular setters and getters, creating a consistent and intuitive experience.

For example, accessing `Calendar.getInstance()` as `Calendar.instance` or retrieving the time from an instance using `timeInMillis` instead of `getTimeInMillis()` showcases the syntactic clarity and conciseness offered by Manifold:

```
// In Manifold a property acts like field access
var properties = new HelloProperties();
properties.number = 5;

// even POJO getters and setters act like field access
var pojo = new HelloPojo();
pojo.number = 5;

// This is equivalent to Calendar.getInstance().getTimeInMillis()
var time = Calendar.instance.timeInMillis;
```

## Customizing Manifold Properties

Similar to Lombok, Manifold offers customization options for individual properties. By using `val`, a read-only property with only a getter can be defined, behaving similarly to a final field. Conversely, `set` defines a write-only property. Additionally, scoping preferences can be passed as arguments, influencing the generated methods' visibility.

This is an example class that customizes such scoping:

```
public class HelloScoping {
    @val(PropOption.Package) int number = 5;
    final @set String str;
}
```

Which we can use as such:

```
var scoping = new HelloScoping();
scoping.str = "";
System.out.println(scoping.number);

// This line won’t compile due to “read only” property
scoping.number = 4;

// This line won’t compile due to “write only” property
System.out.println(scoping.str);
```

## Encapsulation and Method Implementation

Despite the accessibility of properties in Manifold, encapsulation is not compromised. If a setter or getter method is explicitly implemented, Manifold recognizes the custom implementation and seamlessly interacts with the property.

For instance, implementing `setNumber()` allows the code to treat access to the `number` field accordingly, maintaining consistent behavior throughout the codebase as such:

```
public class HelloComputed {
    @var int number;
    @var String str;

    public void setNumber(int number) {
        this.number = number - 1;
    }
}
```

The following code will print `4`:

```
computed.number = 5;
System.out.println(computed.number);
```

## Other Considerations

While both Lombok and Manifold offer useful features, it's important to consider some aspects that may influence your decision. Lombok provides annotations for generating equals, hashCode, and toString methods. It offers annotations that generate constructors, builder patterns, and loggers, all of which are currently missing from Manifold.

Additionally, Lombok's `@SneakyThrows` annotation enables throwing checked exceptions without explicit declaration or handling. Manifold addresses this with a similar global configuration, disabling checked exceptions throughout the codebase. That might be a bit heavy-handed for most developers. However, it's worth noting that these are not direct equivalents and have different implications.

We can sum this up in the following table:

|--------------------------|--------|----------|
|                          | Lombok | Manifold |
| Data \& Value            | ✅      | ❌        |
| Equals/Hashcode/toString | ✅      | ❌        |
| Constructors             | ✅      | ❌        |
| Builders                 | ✅      | ❌        |
| Loggers                  | ✅      | ❌        |
| .?                       | ❌      | ❌        |
| Fluid Dot Syntax         | ❌      | ✅        |
| Smart Scoping            | ❌      | ✅        |

## Final Word

As we wrap up our exploration of property support in Manifold and Lombok, it's evident that both projects bring unique approaches to enhance Java development. While Lombok has been a popular choice, its limitations and legacy features may prompt developers to seek alternative solutions.

On the other hand, Manifold's extensibility, architectural soundness, and distinctive features make it an enticing option for modern Java projects. Manifold is young and still has some limitations in this particular department. That might hinder its adoption in the short term.

Personally, I'm still on the fence. I think Manifold has great potential here. I feel it's a better thought-out solution. Lombok feels a bit like a bandaid by comparison. The main benefit of Manifold is as a whole the project provides far more capabilities than Lombok.

Remember to check out the code examples and resources on my [GitHub page](https://github.com/shai-almog/java-book/) to further deepen your understanding of Manifold's property support.
