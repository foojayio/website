---
title: "Getting Started with Java 17 and IntelliJ IDEA | Foojay.io Today"
slug: "getting-started-with-java-17-and-intellij-idea"
date: "2021-09-14T09:20:56+00:00"
lastmod: "2021-09-14T10:23:35+00:00"
description: "In this article, I will limit the coverage of Java 17 to its language features – Sealed Classes and Pattern Matching for switch."
canonical: "https://blog.jetbrains.com/idea/2021/09/java-17-and-intellij-idea/"
authors:
  - "mala-gupta"
image: "https://foojay.io/wp-content/uploads/2021/09/Java17_blog_Blog_1280x720.png"
categories:
  - "IntelliJ IDEA"
  - "JEPs"
  - "Sealed Classes"
tags:
related_posts:
  - "project-panama-for-newbies-part-1"
  - "java-17-on-the-raspberry-pi"
  - "schedule-for-foojay-virtual-openjdk-17-jug-tour"
  - "ask-a-lille-dev-what-java-developers-really-think-about-quality-frameworks-communities-and-careers"
enlighterjs: true
frozen: false
---

A new Java release every six months can be exciting, overwhelming, or both. Given that [Java 17](https://openjdk.java.net/projects/jdk/17/) is also an [LTS](https://www.oracle.com/java/technologies/java-se-support-roadmap.html) release, it's not just the developers but enterprises also noticing it. If you have been [waiting](https://www.ted.com/talks/tim_urban_inside_the_mind_of_a_master_procrastinator) to move on from Java 8 or 11, now is the time to weigh its advantages.

In this article, I will limit the coverage of Java 17 to its language features -- [Sealed Classes](https://openjdk.java.net/jeps/409) and [Pattern Matching for switch](https://openjdk.java.net/jeps/406). I'll cover what these features are, why you might need them, and how you can start using them in [IntelliJ IDEA](https://blog.jetbrains.com/idea/2021/08/intellij-idea-2021-2-1/). I will also highlight how these features can reduce the *cognitive complexity* for developers. You can use [this link](https://openjdk.java.net/projects/jdk/17/) for a comprehensive list of all the new Java 17 features.

* **Sealed classes.** Added as a standard Java language feature in Java 17, sealed classes enable you to *control* the hierarchies to model your business domain. Sealed classes decouple accessibility from extensibility. Now a visible class or interface doesn't need to be implicitly extensible.

<!-- -->

* **Pattern matching for switch statements** . Pattern matching for switch is introduced as a [preview feature](https://openjdk.java.net/jeps/12). As the name suggests, it adds patterns to the case labels in the switch statements *and* switch expressions. The type of the *selector expression* that can be used with a switch is expanded to any reference value. Also, case labels are no longer limited to constant values. It also helps replace if-else statement chains with switch, improving code readability.

Let's start with pattern matching.
![](https://blog.jetbrains.com/wp-content/uploads/2021/09/Java17_blog_Blog_1280x720.png)

Before we dive into pattern matching for switch, let's ensure we have the basic IntelliJ IDEA configuration set up.

**IntelliJ IDEA Configuration**

Basic support for Java 17 is available in [IntelliJ IDEA 2021.2.1](https://blog.jetbrains.com/idea/2021/08/intellij-idea-2021-2-1/). More support is on the way in future IntelliJ IDEA releases.

To use pattern matching for switch with Java 17, go to *ProjectSettings \| Project* , set the *Project SDK* to 17 and set *Project language level* to '*17 (Preview) - Pattern matching for switch*':
![](https://blog.jetbrains.com/wp-content/uploads/2021/09/java17-img1.png)

You can use any version of the JDK that has already been downloaded on your system, or download another version by clicking on '*Edit* ' and then selecting '*Add SDK \>* ', followed by '*Download JDK...*'. You can choose the JDK version to download from a list of vendors.

On the modules tab, ensure the same language level is selected for the modules - *17 (Preview) - Pattern matching for switch*:
![](https://blog.jetbrains.com/wp-content/uploads/2021/09/java17-img2.png)

Once you select this, you might see the following pop-up which informs you that IntelliJ IDEA might discontinue the support for the Java preview language features in its next versions. Since a preview feature is not permanent (yet), and it is possible that it could change (or even be dropped) in a future Java release.
![](https://blog.jetbrains.com/wp-content/uploads/2021/09/java17-img3.png)

Ok, now we are ready to start with the Java 17 language features.

Pattern matching is a big topic and it is being rolled out in batches in the Java language. It started with [pattern matching for instanceof](https://openjdk.java.net/jeps/394) (previewed in Java 14, and becoming a standard feature in Java 16). Pattern matching for switch is included in Java 17, and we are already looking at deconstructing records and arrays with [record patterns and array patterns](https://openjdk.java.net/jeps/405) in Java 18.

To understand pattern matching for switch, it will be beneficial to have an understanding of:

1. Pattern matching, in general
2. Pattern matching for instanceof
3. The enhancement of switch construct with [Switch Expressions](https://openjdk.java.net/jeps/361)

If you are already familiar with all of the preceding topics, feel free to skip to the section 'Welcome to pattern matching for switch'.

**What is pattern matching?** {#h2-0-what-is-pattern-matching}
--------------------------------------------------------------

[Wikipedia](https://en.wikipedia.org/wiki/Pattern_matching) states pattern matching is "the act of checking a given sequence of tokens for the presence of the constituents of some pattern".

Let's make it more specific to our examples. You can compare pattern matching to a test -- a test that should be passed by a value (primitive or object) against a condition. For example, the following are valid pattern matching examples:

1. Is the value an instance of class `String`?
2. Is the value a subclass of class `AirPollution`, and the value returned by one of its methods, say, `getAQI()` is \> 200?

There are different types of patterns. In this blog post, I'll cover type patterns, guarded patterns, and parenthesised patterns -- since they are relevant to pattern matching for switch.

Pattern matching for instanceof uses type pattern. Let's look at how it works.

**Pattern matching for instanceof** {#h2-1-pattern-matching-for-instanceof}
---------------------------------------------------------------------------

This feature extends the `instanceof` operator with the possibility to use a type pattern. It checks whether an instance is of a certain type. If the test passes, it casts and assigns the value to a pattern variable. This removes the need to define an additional variable, or for explicit casting, to use members of the instance being compared.

Here's an example of code that can be commonly found in codebases (which doesn't use patterns matching for instanceof):

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">void outputValueInUppercase(Object obj) {
   if (obj instanceof String) {              
       String s = (String) obj;             
       System.out.println(s.toUpperCase()); 
   }
}
</pre>

In IntelliJ IDEA, you can invoke context-sensitive actions on the variable s (by using Alt+Enter or by clicking the light bulb icon) and selecting *Replace 's' with pattern variable* to use pattern matching for instanceof:
![](https://blog.jetbrains.com/wp-content/uploads/2021/09/java17-1.gif)

The scope of the pattern variable (a local variable) is limited to the `if`-block because it makes no sense to be able to access the pattern variable if the test fails.

The simplicity of pattern matching of instanceof might be deceptive. If you are thinking it doesn't matter much since it only removes one line of code, think again. Removal of just one line of code can open up a number of possibilities in which you can modify your code. For example, aside from using pattern matching for instanceof, the following code merges `if` statements, introduces a pattern variable, and replaces a for loop with `Collection.removeIf()`:
![](https://blog.jetbrains.com/wp-content/uploads/2021/09/java17-2.gif)

Now, let me brief you on the enhancements to the switch statement with the switch expressions (covered in detail [here](https://blog.jetbrains.com/idea/2019/02/java-12-and-intellij-idea), with Java 12, and [here](https://blog.jetbrains.com/idea/2019/11/java-13-and-intellij-idea/) with changes in Java 13). As I mentioned before, if you are already familiar with switch expressions, please feel free to jump to the section 'Welcome to pattern matching for switch'.

### **Switch expressions -- what benefits do they bring to the table?** {#h3-2-switch-expressions-what-benefits-do-they-bring-to-the-table}

Switch expressions enhance the switch statement and improve the coding experience for developers. As compared to the switch statements, switch expressions can *return a value* . The ability to define *multiple constants with a switch branch* , and the improved code semantics, makes it *concise* . By removing default fall-through the switch branches, you are less likely to introduce a *logical error* in a switch expression.

Let's look at an example that demonstrates the advantages switch expressions can have over switch statements.

In the following code, the switch statement has repetitive break and assignment statements in case labels, which adds noise to the code. The default fall-through in switch branches can sneak in a logical error. For example, if we delete the break statement for case label `STRAW`, it results in an assignment of 300 instead of 200 to the variable `damage` when you call the method `getDamageToPlanet()`, passing it the value `SingleUsePlastic.STRAW`. Also, with switch statements there isn't any way to exhaustively iterate over the finite enum values:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class Planet {

    enum SingleUsePlastic {
        CUP, STRAW, BOTTLE
    }

    int getDamageToPlanet(SingleUsePlastic plastic) {
        int damage = -1;
        switch (plastic) {
            case CUP:
                damage = 100;
                break;
            case STRAW:
                damage = 200;
                break;
            case BOTTLE:
                damage = 300;
                break;
        }
        return damage;
    }
}
</pre>

Let's see how switch expressions can help. The following gif demonstrates some of the benefits of switch expressions such as concise code, improved code semantics, no redundant break statements, exhaustive iteration, and more:
![](https://blog.jetbrains.com/wp-content/uploads/2021/09/java17-3.gif)

With a basic understanding of pattern matching, pattern matching for instanceof, and switch expressions, let's look at what pattern matching is and why you need it?

**Welcome to Pattern matching for switch** {#h2-3-welcome-to-pattern-matching-for-switch}
-----------------------------------------------------------------------------------------

Imagine being able to replace long if-else statement chains with concise switch statements *or* expressions. Yes, you read that correctly. Pattern matching for switch applies to both switch statements and switch expressions.

If you are wondering about the limited types of selector expressions (integral primitives, namely `byte`, `short`, `char`, `int`, their corresponding wrapper classes, `String` and enum) that could be earlier passed to switch, don't worry. With pattern matching for switch, type of selector expression for a switch statement and switch expression has been increased to *any reference value* and integral primitive values (`byte`, `short`, `char`, and `int`).

Also, the case labels are no longer restricted to constants. They can define patterns -- like type patterns, guarded patterns, and parenthesized patterns.

Let's start with an example.

### **Replace if-else statement chains with concise switch constructs -- that test types beyond int integrals, String, or enums.** {#h3-4-replace-if-else-statement-chains-with-concise-switch-constructs-that-test-types-beyond-int-integrals-string-or-enums}

You can work with switch constructs that can be passed a wide range of selector expressions, and can test values not just against constants but also types. That's not all, case labels can also include complex conditions.

Let's work with a set of unrelated classes -- `AirPollution`, `Discrimination`, and `Deforestation`. These classes represent things that harm our planet. To quantify the harm, each of these classes define methods that return an int value, like, `getAQI()`, `damagingGenerations()`, and `getTreeDamage()`. The classes define minimal code to keep it simple:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">class AirPollution {
    public int getAQI() {
        return 100;
    }
}

public class Discrimination {
   public int damagingGenerations() {
       return 2000;
   }
}
public class Deforestation {
   public int getTreeDamage() {
       return 300;
   }
}
</pre>

Imagine a class `MyEarth`, with a method, say, `getDamage()` that accepts a method parameter of type `Object`. Depending on the type of the object passed to this method, it calls the relevant method on the method parameter to get a quantifiable number for the amount of harm it is causing to our planet:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class MyEarth {
   int getDamage(Object obj) {
       int damage = 0;
       if (obj instanceof AirPollution) {
           final AirPollution airPollution = ((AirPollution) obj);
           damage = airPollution.getDamage();
       }
       else if (obj instanceof Discrimination) {
           Discrimination discrimination = ((Discrimination) obj);
           damage = discrimination.damagingGenerations();
       } else if (obj instanceof Deforestation) {
           Deforestation deforestation = ((Deforestation) obj);
           damage = deforestation.getTreeDamage();
       } else {
           damage = -1;
       }
       return damage;
   }
}
</pre>

Let's look at how we can use switch expressions and IntelliJ IDEA to make this code more concise:
![](https://blog.jetbrains.com/wp-content/uploads/2021/09/java17-4.gif)

Here's the final (concise) code for reference:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class MyEarth {
    int getDamage(Object obj) {
        return switch (obj) {
            case final AirPollution airPollution -&gt; airPollution.getDamage();
            case Discrimination discrimination -&gt; discrimination.damagingGenerations();
            case Deforestation deforestation -&gt; deforestation.getTreeDamage();
            case null, default -&gt; -1;
        };
    }
}
</pre>

The power of this construct lies in how often it helps to reduce the cognitive complexity in the code, as I discuss in the following section.

### **Reducing cognitive complexity with pattern matching for switch** {#h3-5-reducing-cognitive-complexity-with-pattern-matching-for-switch}

An if-else statement chain *seems* complex to read and understand -- each condition should be *carefully* read together with its then-and-else code blocks. If we consider the if statement chain from the preceding section, it can be represented roughly as follows:
![](https://blog.jetbrains.com/wp-content/uploads/2021/09/java17-img4.png)

Now let me represent the switch construct from the preceding section:
![](https://blog.jetbrains.com/wp-content/uploads/2021/09/java17-img5.png)

Even by looking at both these images, the switch logic (though similar) looks simpler to read and understand. An if statement chain *seems* to represent a *long* , *complex* path, in which the next turn *seems* to be unknown. But this isn't the case with the switch construct.

Let's look at other reasons for working with pattern matching for switch.

### **Yay! You can now handle nulls within a switch construct** {#h3-6-yay-you-can-now-handle-nulls-within-a-switch-construct}

Previously, switch constructs never allowed using null as a case label, even though it accepted instances of class `String` and enumerations. Then how was it possible to test whether the reference variable you are switching over is not null?

One approach has been to add a `@NotNull` annotation to the variable accepted by the switch construct. You can add this annotation to a method argument, a local variable, field, or static variable. Another approach (much widely used) has been to check if the variable is not null by using an if condition.

Of course, if you do not explicitly check for null values and the selector expression is null, it throws a `NullPointerExpression`. For backward compatibility, `null` selector expression won't match the default label.

Now, you can define null as one of the valid case labels -- so that you can define what to do if the selector expression is null.
![](https://blog.jetbrains.com/wp-content/uploads/2021/09/java17-5.gif)

### **Does IntelliJ IDEA convert your if-statement to a switch expression or a switch statement?** {#h3-7-does-intellij-idea-convert-your-if-statement-to-a-switch-expression-or-a-switch-statement}

In the preceding example, the if-else construct was converted to a switch expression. However, if you'd have selected this conversion, *before* using pattern matching for instanceof, you would have got a switch statement, as shown in the following gif:
![](https://blog.jetbrains.com/wp-content/uploads/2021/09/java17-6.gif)

Since the code block for if-else in the original code snippet defined multiple lines of code, it made sense to convert it to a switch statement rather than a switch expression.

This brings us to another interesting question -- what is the relation between switch statement, switch expression, colon syntax, and arrow syntax? Let's have a look.

### **Switch statements vs. Switch expressions and Colon Syntax vs. Arrow Syntax** {#h3-8-switch-statements-vs-switch-expressions-and-colon-syntax-vs-arrow-syntax}

A switch is classified as a statement or an expression depending on whether it returns a value or not. If it returns a value, it is a switch expression, otherwise a statement. Switch can also use either a colon or an arrow syntax.

Interestingly, the switch style (statement or expression) and arrow/colon syntax are orthogonally related, as shown in the following image:
![](https://blog.jetbrains.com/wp-content/uploads/2021/09/java17-img6.png)

The preceding matrix is not just limited or specific to switch statements or expressions that define a pattern in their case labels. It applies to switch statements and expressions that define constants too.

As shown in the previous examples, the case labels are no longer limited to constants. Let's see what they have to offer.

### **Type pattern -- case labels with a data type** {#h3-9-type-pattern-case-labels-with-a-data-type}

In the previous examples, case labels included a data type. This is a type pattern. A type compares the selector expression with a type. If the test passes, the value is cast and assigned to the pattern variable that is defined right after the type name. Let's pull the exact lines of code from these previous examples:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">case Discrimination discrimination -&gt; discrimination.damagingGenerations();

case Discrimination d -&gt; {
    Discrimination discrimination = ((Discrimination) obj);
    damage = discrimination.damagingGenerations();
}
</pre>

### **Scope of pattern variables** {#h3-10-scope-of-pattern-variables}

Pattern variables are local variables, which are casted and initialized when a type pattern tests true. Their scope is limited to the case labels in which they are declared -- it doesn't make sense for a pattern variable to be available in a switch branch in which its argument doesn't match.

### **When do missing break statements in a switch statement become a compilation error?** {#h3-11-when-do-missing-break-statements-in-a-switch-statement-become-a-compilation-error}

In the following example, the pattern variable `d` is limited to the case label `Discrimination`. When patterns, instead of constants, are used in case labels for switch statements or expressions, missing `break` statements is a compilation error because it can result in a default fall-through to a case label that did not pass the test:
![](https://blog.jetbrains.com/wp-content/uploads/2021/09/java17-7-1.gif)

### **Guarded patterns -- conditions that follow test patterns** {#h3-12-guarded-patterns-conditions-that-follow-test-patterns}

Guarded patterns can help you to add conditions to your case labels, beyond test patterns, so that you don't have to define another if construct within a switch branch.

Let's revisit a switch construct from a previous section:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class MyEarth {
    int getDamage(Object obj) {
        return switch (obj) {
            case AirPollution airPol -&gt; airPol.getDamage();
            case Deforestation deforestation -&gt; deforestation.getTreeDamage();
            case null, default -&gt; -1;
        };
    }
}
</pre>

Imagine you want to return the value 5000, if the `getAQI()` method on an `AirPollution` instance returns a value of more than 200. We are talking about two conditions here:

1. The variable obj is an instance of `AirPollution`
2. `airPol.getAQI()` \> 200

With the guarded patterns, you can add this condition to the case label, as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class MyEarth {
    int getDamage(Object obj) {
        return switch (obj) {
            case AirPollution airPol &amp;amp;&amp;amp; airPol.getAQI() &gt; 200 -&gt; 500;
            case Deforestation deforestation -&gt; deforestation.getTreeDamage();
            case null, default -&gt; -1;
        };
    }
}
</pre>

It is interesting to note that when you pass an `AirPollution` instance with `getAQI()` value \<= 200, `getDamage()` method will execute the default branch and return -1.

Imagine adding multiple conditions to a switch label after the type patterns. While using operators like the conditions OR and AND, the order of execution can be unclear. In this case you can use parentheses to remove all ambiguities. Here's an example that would return 500 when `getDamage()` is called passing it an instance of `AirPollution`:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class MyEarth {
    int getDamage(Object obj) {
        return switch (obj) {
            case AirPollution air
                    &amp;&amp;
                    air.getAQI() &gt; 99 || (air.getDamage() &lt; 101 &amp;&amp; air.getRate() &gt; 11) -&gt; 500;
            case Discrimination discrimination -&gt; discrimination.damagingGenerations();
            case Deforestation deforestation -&gt; deforestation.getTreeDamage();
            case null, default -&gt; -1;
        };
    }
}
</pre>

If I modify the placement of the parentheses from the preceding code (as shown in the following code snippet), calling `getDamage()` passing it an instance of `AirPollution` would return -1:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class MyEarth {
    int getDamage(Object obj) {
        return switch (obj) {
            case AirPollution air
                    &amp;&amp;
                    (air.getAQI() &gt; 99 || air.getDamage() &lt; 101) &amp;&amp; air.getRate() &gt; 11 -&gt; 500;
            case Discrimination discrimination -&gt; discrimination.damagingGenerations();
            case Deforestation deforestation -&gt; deforestation.getTreeDamage();
            case null, default -&gt; -1;
        };
    }
}
</pre>

### **Parenthesized patterns** {#h3-13-parenthesized-patterns}

So far, the necessity of parenthesized patterns is very low. It's only to distinguish guard and expression in instanceof syntax: `if(o instanceof (String s && !s.isEmpty())` -- here we use a parenthesized pattern (with guarded pattern inside). It will be more useful in the future with deconstruction patterns.

### **Pattern dominance -- handling general types before specific types in case labels** {#h3-14-pattern-dominance-handling-general-types-before-specific-types-in-case-labels}

What happens if the types being checked in switch case labels have an inheritance relationship? You should check for the most specific case, prior to checking for the general type.

Failing to do so would be a compilation error -- as shown in the following image, when the code in method `getDamageForDifferentPollutionTypes` compares its method parameter `obj` with class `AirPollution` and `Pollution` (class `AirPollution` extends `Pollution`).
![](https://blog.jetbrains.com/wp-content/uploads/2021/09/java17-img7.png)

An interesting observation is that with a similar logic it isn't a compilation error for an if-else statement.

However, in such cases, IntelliJ IDEA would not offer you the option to convert it to a switch. You get the option, when you remove checking a superclass before its subclass, or, perhaps checking for unrelated types:
![](https://blog.jetbrains.com/wp-content/uploads/2021/09/java17-8.gif)

### **Should you care about handling all possible values for the selector expression in switch?** {#h3-15-should-you-care-about-handling-all-possible-values-for-the-selector-expression-in-switch}

Yes, you must have a branch to execute, regardless of the value that is passed to it, if you are using any kind of patterns in case labels with switch expressions or switch statements.

Imagine the following hierarchy of classes:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">abstract class Pollution {}
class WaterPollution extends Pollution {}
class AirPollution extends Pollution {}
</pre>

Defining a case label which handles instances of type Pollution as the last case label might look obvious in the following code, since switch is returning a value. Since the switch is switching over a reference variable of type pollution, it can be assigned a value of type Pollution or one of its subclasses. In this case a default label is not required:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">class MyEarth {
    static int getDamageForDifferentPollutionTypes(Pollution pollution) {
        return switch (pollution) {
            case WaterPollution w -&gt; 100;
            case AirPollution a -&gt; 200;
            case Pollution p -&gt; 300;
        };
    }
}
</pre>

Also, you would need to handle all the possible values for method parameter pollution, even when the switch-statement is not returning a value:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">class MyEarth {
    static void getDamageForDifferentPollutionTypes(Pollution pollution) {
        switch (pollution) {
            case WaterPollution w -&gt; System.out.println(100);
            case AirPollution a -&gt; System.out.println(200);
            case Pollution p -&gt; System.out.println(300);
        };
    }
}
</pre>

Or when using an old-style colon syntax:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">class MyEarth {
    static void getDamageForDifferentPollutionTypes(Pollution pollution) {
        switch (pollution) {
            case WaterPollution w :
                System.out.println(100);
                break;
            case AirPollution a :
                System.out.println(200);
                break;
            case Pollution p : 
                System.out.println(300);
                break;
        };
    }
}
</pre>

Adding a null case to switch is not mandatory to ensure that it handles all the possible values.

### **Using sealed classes as type patterns -- are they treated differently to non-sealed classes?** {#h3-16-using-sealed-classes-as-type-patterns-are-they-treated-differently-to-non-sealed-classes}

The short answer is yes they are. Please refer to the section 'Sealed classes' in this blog post for their detailed coverage.

Let's revisit the hierarchy of the Pollution classes from our previous example and modify it by sealing it:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">sealed abstract class Pollution {}
final class WaterPollution extends Pollution {}
non-sealed class AirPollution extends Pollution {}</pre>

Now the compiler is sure that the abstract class Pollution has exactly *two* subclasses. So you can handle values passed to method parameter pollution, as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">class MyEarth {
    static int getDamageForDifferentPollutionTypes(Pollution pollution) {
        return switch (pollution) {
            case WaterPollution w -&gt; 100;
            case AirPollution a -&gt; 200;
            // case Pollution is no longer required
        };
    }
}
</pre>

This rule applies to the hierarchy of an interface too.

### **Freedom from defining code that might never execute** {#h3-17-freedom-from-defining-code-that-might-never-execute}

I know the title is confusing. To understand what it means, let's look at an example of a sealed interface and the classes that implement it:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">sealed interface Expandable {}
record Circle(int radius) implements Expandable {}
record Square(int side) implements Expandable {}
</pre>

Without pattern matching for switch, the if statement in the following code would require you to define an else part even though you have handled both the implementing classes of the interface `Expandable`, that is, `Circle` and `Square`:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class Geometry {
    double getArea(Expandable expandable) {
        if (expandable instanceof Circle c) {
            return 3.14 * c.radius() * c.radius();
        }
        else if (expandable instanceof Square s) {
            return s.side() * s.side();
        }
        else {
            return -1; // This code might never execute
        }
    }
}
</pre>

However, this changes when you use pattern matching for switch, as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class Geometry {
    double getArea(Expandable expandable) {
        return switch (expandable) {
            case Circle c -&gt; 3.14 * c.radius() * c.radius();
            case Square s -&gt; s.side() * s.side();
        };
    }
}
</pre>

### **Running inspection** **'if' can be replaced with 'switch' on your code base** {#h3-18-running-inspection-if-can-be-replaced-with-switch-on-your-code-base}

It can be time-consuming to look for if-else constructs in your code and check if they can be replaced with switch. You can run the inspection 'if can be replaced with switch' on all the classes in your codebase or its subset.

With this inspection, you can convert *most* of the if-statements to switch. I stated 'most' of the if-statements and not 'all', for a reason. As demonstrated using a lot of examples in the preceding section, you'll notice that at times IntelliJ IDEA won't offer you an option to convert an if-else statement to switch, or it might not convert it the way you have assumed it would. This is due to missing adherence to the multiple rules we talked about in this blog.

To run the inspection 'if can be replaced with switch', you can use the feature -- Run inspection by name, using the shortcut Ctrl+Alt+Shift+I or ⌥⇧⌘I. Enter the inspection name, followed by selecting the scope and other options. The Problems Tool window will show you where you can apply this inspection. You can choose to apply or ignore the suggested changes as you browse the list in the Problems View Window.
![](https://blog.jetbrains.com/wp-content/uploads/2021/09/java17-9.gif)

We have talked a lot about the pattern matching for switch. Now let's cover sealed classes and interfaces. Added as a standard language feature in Java 17, they haven't changed from Java 16.

The language syntax of Sealed types enables you to restrict the classes or interfaces that can extend or implement them. The goal of this language feature is to let you define the possible hierarchies in your business domain in a declarative manner. But why would you ever need to create restricted hierarchies?

**Need for creating restricted hierarchies** {#h2-19-need-for-creating-restricted-hierarchies}
----------------------------------------------------------------------------------------------

Imagine you are creating an application that helps its users with gardening activities. Depending on the type of plant, a gardener might need to do different activities. Let's model the plant hierarchy as follows (I'm not detailing the classes on purpose):

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">class Plant {}

class Herb extends Plant {}
class Shrub extends Plant {}
class Climber extends Plant {}

class Cucumber extends Climber {}
</pre>

The following code is an example of how the `Gardener` class might use this hierarchy:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class Gardner {
   int process(Plant plant) {
       if (plant instanceof Cucumber) {
           return harvestCucumber(plant);
       } else if (plant instanceof Climber) {
           return sowClimber(plant);
       } else if (plant instanceof Herb) {
           return sellHerb(plant);
       } else if (plant instanceof Shrub) {
           return pruneShrub(plant);
       } else {
           System.out.println("Unreachable CODE. Unknown Plant type");
           return 0;
       }
   }

   private int pruneShrub(Plant plant) {...}
   private int sellHerb(Plant plant) {...}
   private int sowClimber(Plant plant) {...}
   private int harvestCucumber(Plant plant) {...}
}
</pre>

The problem code is the assumption that a developer has to deal with in the last else construct - defining actions even though the developer knows that all possible types of the method parameters plant have been addressed. Though it might look unreachable now, what happens if another developer adds a class to this hierarchy? Sealed classes can impose this restriction on the hierarchies at the language level.

**Define secure hierarchies with sealed classes** {#h2-20-define-secure-hierarchies-with-sealed-classes}
--------------------------------------------------------------------------------------------------------

With the [contextual keyword](https://openjdk.java.net/jeps/8223002#:~:text=Contextual%20keyword%3A%20A%20sequence%20of,declarations%2C%20since%20Java%209) sealed, you can **declare** a class as a sealed class. A sealed class uses the [contextual keyword](https://openjdk.java.net/jeps/8223002#:~:text=Contextual%20keyword%3A%20A%20sequence%20of,declarations%2C%20since%20Java%209) permits to list the classes that can extend it directly. Its subclasses can either be final, non-sealed*,* or sealed.

The following gif shows how you can use IntelliJ IDEA to change the declaration of a regular class to a sealed class (by using the context action 'Seal class'). By default, IntelliJ IDEA declares all derived classes as non-sealed. You can modify it to be `final` or `sealed`:
![](https://lh3.googleusercontent.com/wO_grmMXfKLDbfW4olxIRkrdPJRYDhyoWOcoUM-JTZUnlF5D000JrE6vD08gbNAFwabDdZFHVIC9-KPh4GfFCex57Pm2oorXjqZt8gRJEmhTOONJMgsgreeBN5ReK-KGQXlf-Q3D=s0)

Here's the modified code for reference:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public sealed class Plant permits Herb, Shrub, Climber {
}

public final class Shrub extends Plant {}
public non-sealed class Herb extends Plant {}
public sealed class Climber extends Plant permits Cucumber{}

public final class Cucumber extends Climber {}</pre>

By allowing a predefined set of classes to extend your class, you can **decouple accessibility from extensibility**. You can make your sealed class accessible to other packages and modules, and you can still control who can extend it.

In the past, to prevent classes from being extended, developers created package-private classes. But, this also meant that these classes had limited accessibility. Another approach to prevent extension was to create public classes with private or package-private constructors. Though it enabled a class to be visible, it gave limited control on the exact types that could extend your class.

This is no longer the case if you use sealed classes. The goal of the Sealed types is to model your business domain, so that you process it in a definitive manner.

You can't create another class, say, `AquaticPlant`, that tries to extend the sealed class Plant, without adding it to the permits clause of the class `Plant`. As shown in the following gif, this is a compilation error:
![](https://lh5.googleusercontent.com/DL4Yi0XavZK8P5ZER2TlX8v-r5CIzBMVjOmmN69bCmRG-Cmq7PS4DBSwoqDKXCZiRv5n2fGvnfnvXTPLzG9VnREFX8sBvA_XCpH6dpmOsLTcIUyOVnr5H0uEC-Reij2g48pHosJW=s0)

Let's quickly check the configuration of IntelliJ IDEA on your system to ensure you can get the code to run it.

**Revisiting processing of Plant types in class Gardner** {#h2-21-revisiting-processing-of-plant-types-in-class-gardner}
------------------------------------------------------------------------------------------------------------------------

After creating a sealed hierarchy, you will be able to process an instance from the hierarchy in a precise way, and won't need to deal with any unknown implementations. The `process` method in class `Gardner` will work with no chance of running the `else` clause. However, the syntax of the if-else construct will still need you to define the else part (this may change in a future Java version).

*Type-test-patterns* , introduced with Pattern Matching for instanceof in Java 14, are added to the switch statements and expressions. This lets you eliminate the definition of code to execute for an unmatched `Plant` type passed to the method `process()`:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">int process(Plant plant) {
   return switch (plant) {
       case Cucumber c -&gt; c.harvestCucumber();
       case Climber cl -&gt; cl.sowClimber();
       case Herb h -&gt; h.sellHerb();
       case Shrub s -&gt; s.pruneShrub();
   }
}
</pre>

**Package and module restrictions** {#h2-22-package-and-module-restrictions}
----------------------------------------------------------------------------

Sealed classes and their implementations can't span across multiple Java modules.

If a sealed base class is declared in a named Java module, all its implementations must be defined in the same module. However, they can appear in different packages.

For a sealed class declared in an unnamed Java module, all its implementations must be defined in the same package.

**Rules for base and extended classes** {#h2-23-rules-for-base-and-extended-classes}
------------------------------------------------------------------------------------

The classes that extend a sealed class must either be final, non-sealed, or sealed. A final class prohibits further extension. A non-sealed class allows other classes to extend it. And a sealedsubclass must follow the same set of rules as the parent base class -- it could be extended by an explicit list of other classes.

**Abstract sealed base class** {#h2-24-abstract-sealed-base-class}
------------------------------------------------------------------

A sealed class can be abstract too. The extended classes could be defined as abstract or concrete classes. Here's the modified code which adds an abstract method `grow()` to the class `Plant`:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">sealed abstract public class Plant permits Herb, Shrub, Climber {
   abstract void grow();
}

public final class Shrub extends Plant {
   @Override
   void grow() {
   }
}

public non-sealed abstract class Herb extends Plant {}

public sealed class Climber extends Plant permits Cucumber{
   @Override
   void grow() {
   }
}

final class Cucumber extends Climber {}
</pre>

**Implicit subclasses** {#h2-25-implicit-subclasses}
----------------------------------------------------

If you define a sealed class and its derived classes in the same source file, you can omit the contextual keyword permits and the name of the derived classes that are included in the declaration of a sealed class. In this case, the compiler can infer the hierarchy.
![](https://lh4.googleusercontent.com/xzlgIYLYBNICSpdXjJgWCCb5C4slW5LLoRF9Dq9NB-Q7WuwUdqR7bdToJkbs-pyVH_nQbbOQy-DChZRHSxnuz6_pRZJbug8dPzyiKen4uyILnQg4eSN4Ml_JocWKKQ-9WczN8yy9=s0)

**Sealed interfaces** {#h2-26-sealed-interfaces}
------------------------------------------------

Unlike classes, interfaces can not define constructors. Before the introduction of *sealed classes*, a public class could define a private or package-private constructor to limit its extensibility, but interfaces couldn't do that.

A sealed interface allows you to explicitly specify the interfaces that can extend it and the classes that can implement it. It follows rules similar to sealed classes.

However, since you can't declare an interface using the modifier final -- because doing so would clash with its purpose, as interfaces are meant to be implemented -- an inheriting interface can be declared using either sealed or non-sealed modifiers. The permits clause of an interface declaration lists the classes that can directly implement a sealed interface and interfaces that can extend it. An implementing class can be either final, sealed, or non-sealed:
![](https://lh6.googleusercontent.com/XFTePwAnaj8L5idDHi-mbdAlwluK-guL9z-nVOpCd2y9RIVQc8IxtYH_KZ3jzFMPu82XsAd6mG-gEsWkkrKJzlY8o_7zmjQX9GCp-biUhl5C7kbMo2DvuPyb6wiX7Dmrr_0LSSbO=s0)

Here's the code for reference:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">sealed public interface Move permits Athlete, Jump, Kick {
}

final class Athlete implements Move {}
non-sealed interface Jump extends Move {}
sealed interface Kick extends Move permits Karate {}

final class Karate implements Kick {}</pre>

**Stronger code analysis with closed list of subclasses** {#h2-27-stronger-code-analysis-with-closed-list-of-subclasses}
------------------------------------------------------------------------------------------------------------------------

With sealed classes and interfaces, you can have an explicit list of inheritors that is known to the compiler, IDE and the runtime (via reflection). This closed list of subclasses makes the code analysis more powerful.

For example, consider the following completely sealed hierarchy of `WritingDevice` (which doesn't have non-sealed subtypes):

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">interface Erasable {}

sealed class WritingDevice permits Pen, Pencil {}
final class Pencil extends WritingDevice {}
sealed class Pen extends WritingDevice permits Marker {}
final class Marker extends Pen {}</pre>

Now, instanceof and casts can check the complete hierarchy statically. Code on line1 and line2 are compilation errors. The compiler checks all the inheritors from the permits list and finds that no one of them implements the `Erasable` or the `CharSequence` interface:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">class UseWritingDevice {
   static void write(WritingDevice pen) {
       if (pen instanceof Erasable) {                   // line1
       }
       CharSequence charSequence = ((CharSequence) pen);// line2
   }
}
</pre>

The following gif demonstrates it in IntelliJ IDEA:
![](https://lh6.googleusercontent.com/tHpt2ZISzzPS4hYpuSiXaWTrTiz7vUufr6xq7LOvuyrlOB4pSG_MrRll_IcFI_QbXIpKUpDcW15_1Ib0J4eFdtE87btbBakLOrPa2iYeyB96xe26QbMgf2pq0eBAbkje7bF-S887=s0)

I mentioned that Pattern Matching for switch is introduced as a preview language feature in Java 17. Just in case you are unaware of what preview features mean, I've covered it in the next section.

With Java's new release cadence of six months, new language features are released as preview features. They may be reintroduced in later Java versions in the second or third preview, with or without changes. Once they are stable enough, they may be added to Java as a standard language feature.

Preview language features are complete but not permanent, which essentially means that these features are ready to be used by developers, although their finer details could change in future Java releases depending on developer feedback. Unlike an API, language features can't be deprecated in the future. So, if you have feedback about any of the preview language features, feel free to share it on the [JDK mailing list](https://mail.openjdk.java.net/mailman/listinfo/amber-dev) (free registration required).

Because of how these features work, IntelliJ IDEA is committed to only supporting preview features for the current JDK. Preview language features can change across Java versions, until they are dropped or added as a standard language feature. Code that uses a preview language feature from an older release of the Java SE Platform might not compile or run on a newer release. For example, Switch Expressions in Java 12 were released with the usage of break to return a value from its branch, which was later changed to yield. Support for using break to return a value from Switch Expressions has already been dropped in IntelliJ IDEA.

IntelliJ IDEA is not only committed to supporting new Java features, but also to ensuring that our existing intentions and inspections work with them.

IntelliJ IDEA 2021.2.1 supports basic support for the pattern matching for switch. More support is in the works. This version has full support for recent additions like sealed classes and interfaces, records, and pattern matching for instanceof.

We love to hear from our users. Don't forget to submit your feedback regarding the support for these features in IntelliJ IDEA.

Happy Developing!

<br />
