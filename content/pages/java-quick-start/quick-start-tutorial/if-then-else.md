---
title: "If, Then, Else"
description: "Java Quick Start Tutorial: Compare values and branch on the result with if, then and else - the most-used construct in any programming language."
url: "/java-quick-start/quick-start-tutorial/if-then-else/"
frozen: false
duration: "3:14"
weight: 5
---

{{< youtube J2VbPjPASns >}}

Probably one of the most used functions in programming... Comparing different values, to perform specific actions, which is done with if/then/else.

The line starting with "//" is a comment line, so Java ignores it. By changing the `testValue` to 2, 3, or something else on line 4, you will see the text output of the several if blocks.

The second part of this example script shows how to compare string-values. Because a String is an object, you can only correctly compare the content (= text) of the String by using the `equals()` or `equalsIgnoreCase()` methods.

{{< jdoodle >}}
public class IfThenElse {
    public static void main (String[] args) {
        // Compare integer value
        int testValue = 1;
        // We don't change the variable in this example,
        // but here could be some code to define this variable.
        if (testValue <= 1) {
            System.out.println("The value is 1 or smaller");
        } else if (testValue == 2) {
            System.out.println("The value is 2");
        } else {
            System.out.println("The value is " + testValue);
        }
        // Compare strings
        String string1 = "Hello world";
        String string2 = "Hello" + " " + "world";
        String string3 = "Hello World";
        System.out.println("Are string1 and string2 equal? " + string1.equals(string2));
        System.out.println("Are string1 and string3 equal? " + string1.equals(string3));
        System.out.println("Are string1 and string3 equal ignoring the case? " + string1.equalsIgnoreCase(string3));
        if (string1.equalsIgnoreCase(string3)) {
            System.out.println("string1 and string3 are equal ignoring the case");
        }
    }
}
{{< /jdoodle >}}

The output of this example looks like this:

```
$ java IfThenElse.java

The value is 1 or smaller
Are string1 and string2 equal? true
Are string1 and string3 equal? false
Are string1 and string3 equal ignoring the case? true
string1 and string3 are equal ignoring the case
```
