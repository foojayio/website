---
title: "foojay – Sorting Text in Java"
slug: "sorting-text-in-java-how-complicated-can-it-be"
date: "2024-02-07T08:41:54+00:00"
lastmod: "2025-05-17T13:16:33+00:00"
description: "Sorting text should be easy as String implements the Comparable interface. In this article, we'll see that it can be more complicated than that."
authors:
  - "anthony-goubard"
image: "https://foojay.io/wp-content/uploads/2022/06/chemical-scientist-test-chemistry-chemist-student.jpg"
categories:
  - "Java"
  - "Java Core"
tags:
related_posts:
  - "7-functional-programming-techniques-in-java-a-primer"
  - "playing-practically-with-stream-api"
  - "did-you-know-you-can-create-mappers-without-creating-underlying-objects-in-java"
  - "confusing-java-strings"
enlighterjs: true
frozen: false
---

**Sorting text should be easy as String implements the *Comparable* interface. In this article, we'll see that it can be more complicated than that.**

Text is represented by the *String* class in Java. In this article we'll explore how to sort String, the advantages and drawbacks of each possibility.

Level 1: Comparable {#h2-0-level-1-comparable}
----------------------------------------------

The class String implements *Comparable* so sorting a list of String is as simple as

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">List&lt;String&gt; textList = new ArrayList&lt;&gt;(List.of("test","test11","test2","Test4","test3","test5","tést2","test2","3test","testa","test2a","test4a","test2b"));
Collections.sort(textList);</pre>

=\> **\[3test, Test4, test, test11, test2, test2, test2a, test2b, test3, test4a, test5, testa, tést2\]**

#### Advantages:

* Ease of use
* Fast

#### Drawbacks:

* Case sensitive: 'T' is lower than 'a'
* Doesn't sort accents and diacritics correctly: 'f' is lower than 'é'
* Doesn't sort numbers correctly: 'test11' is lower than 'test2'

Another variant of this is

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">Collections.sort(textList, String.CASE_INSENSITIVE_ORDER);</pre>

which fixes the case sensitivity problem.  

=\> **\[3test, test, test11, test2, test2, test2a, test2b, test3, Test4, test4a, test5, testa, tést2\]**

Level 2: Collator {#h2-1-level-2-collator}
------------------------------------------

To sort text in a more accurate way, Java includes the class *java.text.[Collator](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/text/Collator.html)* (and its direct sub-class *java.text.[RuleBasedCollator](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/text/RuleBasedCollator.html)*)

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">Collator collator = Collator.getInstance();
Collections.sort(textList, collator);</pre>

Beware that sorting could be locale sensitive so if you want a consistent result you may prefer using `Collator.getInstance(Locale);`.

#### Advantages:

* Ease of use
* Sort lowercase and uppercase correctly
* Sort accents and diacritics correctly

#### Drawbacks:

* Slower
* Doesn't sort numbers correctly: 'test11' is lower than 'test2'

It's possible with Collator to specify the strength of the comparison. Let's show you with examples:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">Collator collator = Collator.getInstance(Locale.US);
collator.setStrength(Collator.TERTIARY);
Collections.sort(textList, collator);</pre>

=\> **\[3test, test, test11, test2, test2, tést2, test2a, test2b, test3, Test4, test4a, test5, testa\]**

Here is some code to better understand the different collator strengths:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">collator.setStrength(Collator.PRIMARY);
collator.compare("test", "tést"); =&gt; 0
collator.compare("test", "tEst"); =&gt; 0
collator.setStrength(Collator.SECONDARY);
collator.compare("test", "tést"); =&gt; -1
collator.compare("test", "tEst"); =&gt; 0
collator.setStrength(Collator.TERTIARY);
collator.compare("test", "tést"); =&gt; -1
collator.compare("test", "tEst"); =&gt; -1</pre>

Level 3: External library {#h2-2-level-3-external-library}
----------------------------------------------------------

A good resource for different sorting text algorithms with numbers is the [natural order benchmark](https://github.com/ChristianLutz/natural-order-benchmark/) GitHub project from Christian Lutz.

If it's already in your classpath, the best choice from what I've evaluated is IBM ICU4J Collactor which has a [setNumericCollation](https://unicode-org.github.io/icu-docs/apidoc/released/icu4j/com/ibm/icu/text/RuleBasedCollator.html#setNumericCollation-boolean-) method. It's fast and correct. I didn't use it as the library was too big (\> 10MB) for my purpose.

I noted that a few algorithms were using `Character.isDigit()` which was a big performance bottleneck.

#### Advantages:

* Sort numbers
* No code required

#### Drawbacks:

* May require new external library
* Sorting accents and diacritics unknown
* Performance varies based on library used
* Can be quite complex to read how they work

Level 4: Custom algorithm {#h2-3-level-4-custom-algorithm}
----------------------------------------------------------

For my file manager [Ant Commander Pro](https://www.antcommander.com) and for my text utilities software [Japplis Toolbox](https://www.japplis.com/toolbox/), I wanted a fast and accurate sorting algorithm.

So here is my algorithm:

What you'll need:

* A method that returns a `Comparator<String>`
* A method that given 2 String objects returns an integer
* A method that given 2 characters objects returns an integer
* A way to remember numbers when comparing 2 digits. As 987 \< 1234.

Let's start with the comparator, that is the easiest part:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public static Comparator&lt;String&gt; stringComparator() {
    return (s1, s2) -&gt; compareStrings(s1, s2);
}</pre>

For the compare, I like to be on the safe side and if you use a list of String or a String\[\] it may have many values that could be empty or null. So adding a method to take care of empty and null values first.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public static int compareStrings(String s1, String s2) {
    if (s1 == null &amp;&amp; s2 == null) return 0;
    if (s1 == null) return 1;
    if (s2 == null) return -1;
    if (s1.isEmpty() &amp;&amp; s2.isEmpty()) return 0;
    if (s1.isEmpty()) return 1;
    if (s2.isEmpty()) return -1;
    int compare = compareWithNumbers(s1, s2);
    return compare;
}</pre>

Now, we need a method to compare characters.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">private static int compareCharacters(char car1, char car2) {
    // For performance reason
    if (car1 == car2) return 0;
    if (car1 == '.') return -1; // file.txt &lt; file 2.txt
    if (car2 == '.') return 1;
    if (car1 &lt; 128 &amp;&amp; car2 &lt; 128) {
        if (car1 &gt;= 65 &amp;&amp; car1 &lt;=90) car1 += 32; // uppercase to lowercase
        if (car2 &gt;= 65 &amp;&amp; car2 &lt;=90) car2 += 32;
        if (car1 == car2) return 0;
        if ((car1 &gt;= 'a' &amp;&amp; car1 &lt;= 'z' &amp;&amp; car2 &gt;= 'a' &amp;&amp; car2 &lt;= 'z') ||
                (car1 &gt;= '0' &amp;&amp; car1 &lt;= '9' &amp;&amp; car2 &gt;= '0' &amp;&amp; car2 &lt;= '9') ||
                (car1 &gt;= '0' &amp;&amp; car1 &lt;= '9' &amp;&amp; car2 &gt;= 'a' &amp;&amp; car2 &lt;= 'z') ||
                (car1 &gt;= 'a' &amp;&amp; car1 &lt;= 'z' &amp;&amp; car2 &gt;= '0' &amp;&amp; car2 &lt;= '9')) {
            return car1 - car2;
        }
    }
    return getCollactor(1).compare(String.valueOf(car1), String.valueOf(car2));
}

private static Collator PRIMARY_COLLATOR;
private static Collator TERTIARY_COLLATOR;

private static Collator getCollactor(int strenght) {
    if (strenght == 1) {
        if (PRIMARY_COLLATOR == null) {
            PRIMARY_COLLATOR = Collator.getInstance();
            PRIMARY_COLLATOR.setStrength(Collator.PRIMARY);
        }
        return PRIMARY_COLLATOR;
    }
    if (strenght == 3) {
        if (TERTIARY_COLLATOR == null) {
            TERTIARY_COLLATOR = Collator.getInstance();
            TERTIARY_COLLATOR.setStrength(Collator.TERTIARY);
        }
        return TERTIARY_COLLATOR;
    }
    return Collator.getInstance();
}</pre>

And now, the main compare method that will handle numbers:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">private static final long MAX_COMPARE = 100_000_000_000_000_000L;

public static int compareWithNumbers(String s1, String s2) {
    int length = Math.min(s1.length(), s2.length());
    int i = 0;
    long number1 = 0;
    long number2 = 0;
    for (; i &lt; length; i++) {
        char car1 = s1.charAt(i);
        char car2 = s2.charAt(i);
        boolean isDigit1 = (number1 &gt; 0 &amp;&amp; car1 == '0') || (car1 &gt;= '1' &amp;&amp; car1 &lt;= '9');
        boolean isDigit2 = (number2 &gt; 0 &amp;&amp; car2 == '0') || (car2 &gt;= '1' &amp;&amp; car2 &lt;= '9');
        int compare = compareCharacters(car1, car2);
        // Compute on going numbers
        if (isDigit1) number1 = number1 * 10 + car1 - '0';
        if (isDigit2) number2 = number2 * 10 + car2 - '0';
        if (number1 &gt;= MAX_COMPARE || number2 &gt;= MAX_COMPARE) {
            if (isDigit1) number1 = car1 - '0';
            if (isDigit2) number2 = car2 - '0';
        }
        if (!isDigit1 || !isDigit2) {
            if (number1 != number2) {
                if (number1 == 0 || number2 == 0) return compareCharacters(car1, car2); // compare number to letter
                return number1 &lt; number2 ? -1 : 1; // compare numbers
            }
            if (compare != 0) return compare; // compare letters
            number1 = 0;
            number2 = 0;
        }
    }
    int lengthDiff = s1.length() - s2.length();
    if (lengthDiff == 0 &amp;&amp; number1 == number2) return getCollactor(3).compare(s1, s2); // same primary text
    if (number1 &gt; 0 || number2 &gt; 0) {
        if (lengthDiff == 0) return (int) (number1 - number2);
        char nextCar = lengthDiff &gt; 0 ? s1.charAt(i) : s2.charAt(i);
        boolean isDigit = nextCar &gt;= '0' &amp;&amp; nextCar &lt;= '9';
        if (isDigit || number1 == number2) return lengthDiff &gt; 0 ? 1 : -1; // the longest number or text loses
        else return (int) (number1 - number2);
    }
    return lengthDiff; // the longest text loses
}</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">Collections.sort(textList, stringComparator());</pre>

=\> **\[3test, test, test2, test2, tést2, test2a, test2b, test3, Test4, test4a, test5, test11, testa\]**

<br />

#### Advantages:

* Fast
* Sort lowercase, uppercase, accents, diacritics and numbers correctly
* No external library

#### Drawbacks:

* More code
* Works with ASCII digits only for numbers.
* Doesn't support fraction numbers
* Not tested with numbers larger than Long.MAX_COMPARE

![File Manager Ant Commander Pro using the sorting algorithm with numbers](/images/posts/2024/02/sorting-text-in-java-how-complicated-can-it-be/sorting-ant-commander-pro-invoices.png) File Manager Ant Commander Pro using the sorting algorithm with files with numbers  
[![](/images/posts/2024/02/sorting-text-in-java-how-complicated-can-it-be/sorting-toolbox-test-457x1024.png)](https://www.japplis.com/toolbox/) Text Utility Japplis Toolbox applying the sort algorithm on text with numbers

<br />

<br />
