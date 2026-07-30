---
title: "All about Generics: Part 1 | Foojay Today"
slug: "all-about-generics-part-1"
date: "2022-03-20T22:54:03+00:00"
lastmod: "2022-03-21T07:05:00+00:00"
description: "Java requires to specify types in all aspects of its code, methods, fields, interface etc, however, still, with generics a piece of code can be written generically enough so that we can fit a variety of types in it."
authors:
  - "mainuls18"
image: "https://foojay.io/wp-content/uploads/2022/03/hello.png"
categories:
  - "Java Core"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Generics are used to parameterize types. A piece of code can be written generically enough so that we can fit a variety of types in it. Being a strongly type-safe language, Java requires to specify types in all aspects of its code, methods, fields, interfaces, etc.

However, generics is one of the tricky ways to make it possible to use type parameterization so that one particular portion or entirely of a class or their subclass can support a variety of types if an entity such as class, interface, or method that operates on a parameterized type is said to be a generic entity.

Benefits of Generics {#h2-0-benefits-of-generics}
-------------------------------------------------

### Type safety {#h3-1-type-safety}

Using generics helps us define a type to consume in and produce from, where it is used. This means we can be sure of adding a specific type of data in a Collection type for example (***List\<Integer\>*** ) and get the exact same type. This saves us from getting ***ClassCastException*** and from lots of development pain.

### Type erasure {#h3-2-type-erasure}

Type erasure means, all the additional information included while using Java Generics, is removed from bytecode while generated. In bytecode, it will be old java syntax prior to Java 5.

Alternatively, we can say, we will be enforced to use a type in compile-time, but the types are discarded at runtime.

Where we can use Generics {#h2-3-where-we-can-use-generics}
-----------------------------------------------------------

* Using Generics with wildcards
  * Unbounded wildcards
  * Bounded wildcards
    * Upper bounded wildcards
    * Lower bounded wildcards
* Arrays with Generic types
* Using Generic types as parameters of a class or an interface.
* Using Generic types with method or constructor definition.

Components of Generics {#h2-4-components-of-generics}
-----------------------------------------------------

1. ***\<\>*** - Diamond operator - we put a generic type inside it.
2. ***T*** - known as a Generic type. We can actually use any character or word instead of T here. usually used as ***\<T\>***
3. ***?*** - known as a wildcard. We use it like ***\<?\>***
4. ***extends*** - Used to set Upper Bound of Generic type. This means we can not use any parent class of this upper-bounded class.
5. ***super*** - Used to set Lower Bound of Generic type. We cannot use any child class of this lower bounded class.

Let's start our today's discussion to explain using Generics with wildcards.

### Using Generics with wildcards {#using-generics-with-wildcards}

We have talked about the wildcard above. Wildcard represents an unknown type. The followings are examples of wildcard parameterized generics.  

<pre class="EnlighterJSRAW" data-enlighter-language="java">Collection&lt;?&gt;
List&lt;? extends Number&gt;
Comparator&lt;? super String&gt; 
</pre>

#### Wildcard can be used in the following situations:

* To define the type for a generic containing parameter

<pre class="EnlighterJSRAW" data-enlighter-language="java">void addAll(List&lt;? extends Object&gt; objects) {}
</pre>

* To define the type for a generic containing field or local variable

<pre class="EnlighterJSRAW" data-enlighter-language="java">Set&lt;? extends Number&gt; numList = Set.of(3, 100_000_000_000_000L, 2.5F, 2.7);
</pre>

* To define an unknown type for a generic containing return type. But it is better to make the type-specific.

<pre class="EnlighterJSRAW" data-enlighter-language="java">ResponseEntity&lt;?&gt; getAll() { 
 //
 // code goes here
 //
}</pre>

#### Unbounded wildcard parameterized type {#unbounded-wildcard-parameterized-type}

A generic type that does not contain any boundary of the upper or lower limit only contains wildcard as type.

<pre class="EnlighterJSRAW" data-enlighter-language="java">ArrayList&lt;?&gt;  list = new ArrayList&lt;Long&gt;();  
//or
ArrayList&lt;?&gt;  list = new ArrayList&lt;String&gt;();  
//or
ArrayList&lt;?&gt;  list = new ArrayList&lt;Employee&gt;(); 

</pre>

#### Bounded wildcard parameterized type {#bounded-wildcard-parameterized-type}

Bounded wildcards have 2 types - upper-bounded and lower bounded. These bounds put some restrictions on the range of classes that can be used as generic types. We usually achieve the upper bound limit by ***extends*** and the lower bound limit by ***super*** keyword.

##### Upper bounded wildcard {#upper-bounded-wildcard}

Let's assume, we have a method, that takes a parameter of ***List***. The list items can have different instances of a class, that has multiple child classes. Now, we don't know, at which point of time which child class instance list will be passed through the method parameter. In such a case, we can simply set the upper-bounded wildcard.  

<pre class="EnlighterJSRAW" data-enlighter-language="java">List&lt;Integer&gt; ints = Arrays.asList(1,2,3,4,5); 
System.out.println(sum(ints)); 

// and the sum method 

private static Number sum (List&lt;? extends Number&gt; numbers){ 
    double s = 0.0; 
    for (Number n : numbers) 
        s += n.doubleValue(); 
    return s; 
}</pre>

##### Lower bounded wildcard {#lower-bounded-wildcard}

When we want to set a lower limit of a class hierarchy, and we don't want to add any instance of any class below to that hierarchy, we use ***super*** keyword to make this lower bounded wildcard. This applies for a method parameter.

To demonstrate, let us create 3 classes - ***Fruit*** , ***Apple*** and ***AsianApple***  

<pre class="EnlighterJSRAW" data-enlighter-language="java">class Fruit {
    @Override
    public String toString() {
        return "Any fruit!!";
    }
}

class Apple extends Fruit {
    @Override
    public String toString() {
        return "This is an Apple !!";
    }
}

class AsianApple extends Apple {
    @Override
    public String toString() {
        return "This is an AsianApple !!";
    }
}</pre>

We will also define a method that receives a ***List\<? super Apple\>*** as a parameter.

<pre class="EnlighterJSRAW" data-enlighter-language="java">public static void printApples(List&lt;? super Apple&gt; apples)
{
    System.out.println(apples);
}</pre>

Now observe the following example. When we pass a list of ***AsianApple*** in the method ***printApples(...)*** , it gives us compile-time error. But when we try to pass a list of ***Fruit*** , which is the super of ***Apple***, it accepts it without any error.

The interesting thing to observe, we can add **AsianApple** to the 2nd list, and it is acceptable. But it is impossible to pass a list wholly of an unsupported type.

<pre class="EnlighterJSRAW" data-enlighter-language="java">List&lt;AsianApple&gt; basket1 = new ArrayList&lt;&gt;();
basket1.add(new AsianApple()); 
printApples(basket1); // Compilation error 

List&lt;Fruit&gt; basket2 = new ArrayList&lt;&gt;(); 
basket2.add(new AsianApple()); 
basket2.add(new Apple()); 
basket2.add(new Fruit()); 
printApples(basket2);</pre>

Now, let's see some limitations and exceptional cases of ***extends*** and ***super***.

##### Limitations and exceptional cases {#limitations-and-exceptional-cases}

* We can create a list like ***List\<? extends Apple\>*** and instantiate the list. But when we try to add elements in it, it will give us compilation error.

<pre class="EnlighterJSRAW" data-enlighter-language="java">Set&lt;? extends Apple&gt; appleSet = new HashSet&lt;&gt;(); 
appleSet.add(new Apple()); // Compilation error 
appleSet.add(new AsianApple()); // Compilation error</pre>

The reason is that we actually don't need to mention***extends*** If we define a list of ***Apple*** , it simply can accept any child of ***Apple*** type.

* However, we can define something like the following when we define it in one go.

<pre class="EnlighterJSRAW" data-enlighter-language="java">List&lt;? super Apple&gt; basket = List.of(new Apple(), new AsianApple());

    // Because we are populating all objects first, 
    // then assigning the list in the left side object.
    // So the above code satisfies the below valid condition

    List&lt;? super Apple&gt; basket = new ArrayList&lt;Object&gt;();

    List&lt;? super Apple&gt; basket1 = List.of(new Apple(), new AsianApple(), new Fruit(), new Object(), 123, 12.45);</pre>

The reason is that when we define with ***List.of(...)*** it actually accepts objects and produces a list of ***Object*** . And ***List\<Object\>*** is a type of ***List\<? super Apple\>***.

* Notice the following example:

<pre class="EnlighterJSRAW" data-enlighter-language="java">List&lt;? super Apple&gt; basket = new ArrayList&lt;&gt;(); 
basket.add(new Apple()); //Successful 
basket.add(new AsianApple()); //Successful 
// basket.add(new Fruit()); //Compile time error 
// basket.add(new Object()); //Compile time error</pre>

The interesting fact here is, we were supposed to be able to add any supertype of ***Apple*** to the list, but it seems to happen the opposite. When we try to add items in a list demonstrated above, it only accepts the children of the ***supertype***.

#### PECS \[Producer extends, Consumer super\] {#pecs-producer-extends-consumer-super}

* If we want to produce items, e.g.: iterate over a list or get items from a list, we use extends.

The reason is that we cannot know which subtype or ***Fruit*** our collection will hold. So it is safe to get the object from the list and store it in a ***Fruit*** type.

* If we want to add items to our collection, we use super.

The reason is that we don't care what is already stored in the collection. As long as we are adding a ***Fruit*** and its subtype in the collection, the compiler doesn't complain.

#### Where we cannot use Generic types {#where-we-cannot-use-generic-types}

* With static fields

<pre class="EnlighterJSRAW" data-enlighter-language="java">public class GenericsExample&lt;T&gt; { 
    private static T member; //This is not allowed 
}</pre>

* We cannot instantiate any generic type

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public class GenericsExample&lt;T&gt; { 
    public GenericsExample(){ 
        new T(); // Not allowed here
    } 
}</pre>

* We cannot refer to primitive types with Generic type

<pre class="EnlighterJSRAW" data-enlighter-language="java">final List&lt;int&gt; ids = new ArrayList&lt;&gt;(); //Not allowed 
final List&lt;Integer&gt; ids = new ArrayList&lt;&gt;(); //Allowed</pre>

* We cannot create Generic exception classes

<pre class="EnlighterJSRAW" data-enlighter-language="java">// causes compiler error 
public class GenericException&lt;T&gt; extends Exception {}</pre>

That's all for this article. In our next article, we will discuss on **Arrays with Generics** , **Using Generic type as parameter of a class** and **Using Generic types with method or constructor definition**.

Thank you for your patience and taking time to read this long article 🙂
