---
title: "Getting Started with Java - Using Objects"
linkTitle: "Using Objects"
description: "Java is object-oriented: turn part of your program into an object with its own variables and methods."
canonical: "https://foojay.io/java-quick-start/quick-start-tutorial/using-objects/"
url: "/java-quick-start/quick-start-tutorial/using-objects/"
jdoodle: true
aliases:
  - "/java-quick-start/quick-start-tutorial/using-objects/"
frozen: false
weight: 8
---

{{< youtube vJf6_d0c56I >}}

Java is an object-oriented programming language. This means we can turn any part of our code into an object with its own variables and methods. For instance, let's create a program containing a shopping cart list with items.

Just like the previous example, this one also uses some methods which are not part of "base Java". That's why our code starts with the import lines to determine where ArrayList and List can be found.

<div data-pym-src="https://www.jdoodle.com/plugin" data-language="java" data-version-index="6" data-libs="mavenlib1, mavenlib2">
 import java.util.ArrayList;
 import java.util.List;
 public class UsingObject {
     public static void main (String[] args) {
         List&lt;ShoppingCartItem&gt; items = new ArrayList&lt;&gt;();
         items.add(new ShoppingCartItem("Raspberry Pi 4, 4Gb", 1, 59.95F));
         items.add(new ShoppingCartItem("Micro-HDMI cable", 2, 5.9F));
         items.add(new ShoppingCartItem("Raspberry Pi 4 power supply", 1, 9.95F));
         double total = 0D;
         for (ShoppingCartItem item : items) {
             System.out.println(item.getName());
             System.out.println(" " + item.getQuantity() + "\tx\t" + item.getPrice() + "\t= " + item.getTotal() + " Euro");
             total += item.getTotal();
         }
         System.out.println("\nTotal for shopping cart:\n " + total + " Euro");
     }
     public static class ShoppingCartItem {
         // These values are final as they should not be changed
         private final String name;
         private final int quantity;
         private final float price;
         public ShoppingCartItem(String name, int quantity, float price) {
             this.name = name;
             this.quantity = quantity;
             this.price = price;
         }
         public String getName() {
             return name;
         }
         public int getQuantity() {
             return quantity;
         }
         public float getPrice() {
             return price;
         }
         public float getTotal() {
             return quantity * price;
         }
     }
 }
</div>

The class `ShoppingCartItem` is an object that can hold the data for each item in the shopping list. The constructor `ShoppingCartItem(String name, int quantity, float price)` enables us to make an item that has a name, quantity, and price. The method `getTotal()` inside the item will return the total cost for the item based on quantity and price.

In the main method, we can now easily create a list with objects of the type `ShoppingCartItem` and add a few of them to the list. With a for-loop, we can read all the items inside the list and calculate the total value.

In the `System.out.println` two special characters are used for better readable output:

* `\n` to jump to a new line
* `\t` to insert a tab

```
$ java UsingObject.java
Raspberry Pi 4, 4Gb
     1  x       59.95   = 59.95 Euro
Micro-HDMI cable
     2  x       5.9     = 11.8 Euro
Raspberry Pi 4 power supply
     1  x       9.95    = 9.95 Euro

Total for shopping cart:
     81.70000076293945 Euro
```

Records got introduced in Java 16 and can be used to simplify this code a lot! The `ShoppingCartItem` as a record, removes a lot of the so-called "boilerplate" code. When applying all improvements up to Java 25, the code with the same functionality looks like this:

```
import java.util.ArrayList;
import java.util.List;

void main() {
    List<ShoppingCartItem> items = new ArrayList<>();
    items.add(new ShoppingCartItem("Raspberry Pi 4, 4Gb", 1, 59.95F));
    items.add(new ShoppingCartItem("Micro-HDMI cable", 2, 5.9F));
    items.add(new ShoppingCartItem("Raspberry Pi 4 power supply", 1, 9.95F));

    double total = 0D;
    for (ShoppingCartItem item : items) {
        IO.println(item.name());
        IO.println("     " + item.quantity() + "\tx\t"
                + item.price() + "\t= " + item.total() + " Euro");
        total += item.total();
    }

    IO.println("\nTotal for shopping cart:\n     " + total + " Euro");
}

record ShoppingCartItem(String name, int quantity, float price) {
    float total() {
        return quantity * price;
    }
}
```
