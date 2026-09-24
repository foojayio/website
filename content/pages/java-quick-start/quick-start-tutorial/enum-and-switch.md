---
title: "Enum and Switch"
description: "Java Quick Start Tutorial: Replace loose integer values with an enum, and use a switch to react to each of its options in Java."
url: "/java-quick-start/quick-start-tutorial/enum-and-switch/"
frozen: false
duration: "2:45"
weight: 6
---

{{< youtube G-M2uMq7FrY >}}

In the previous example, we used an integer value to define testValue. But if we know that only a limited set of possibilities should be used, defining an "enum" with the available choices is a better option.

Enums are, by convention, typed in uppercase.

{{< jdoodle >}}
public class EnumSwitch {
    public static void main (String[] args) {
        // Define value based on an enum
        TestOption option = TestOption.TYPE_2;
        // Compare the value with the available options
        switch(option) {
            case TYPE_1:
                System.out.println("Do something specific for Type 1");
                break;
            case TYPE_2:
                System.out.println("Do something specific for Type 2");
                break;
            case TYPE_3:
                System.out.println("Do something specific for Type 3");
                break;
            default:
                System.out.println("No action defined for this type: " + option);
        }
    }
    enum TestOption {
        TYPE_1, TYPE_2, TYPE_3, TYPE_4, TYPE_5, UNKNOWN;
    }
}
{{< /jdoodle >}}

Change the value in line 4 to check the output, for instance when running with `TYPE_2` and `TYPE_4`:

```bash
$ java EnumSwitch.java
Do something specific for Type 2

$ java EnumSwitch.java
No action defined for this type: TYPE_4
```

Instead of if/then/else we are now using switch/case/default, resulting in more readable code. This is also easier to use and maintain as we know exactly which are the possible values.

Don't forget to add the `break;` lines in each case. Try removing them and re-running the example.

```bash
$ java EnumSwitch.java
Do something specific for Type 2
Do something specific for Type 3
No action defined for this type: TYPE_2
```

That does not what we wanted to achieve! All the cases after the valid one are returned now... That's also the reason we don't need to add a break in the `default` block, as this is the last one anyhow.  
