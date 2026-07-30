---
title: "Local Variable Type Inference: Declare Var, Not War"
slug: "local-variable-type-inference-declare-var-not-war"
date: "2021-01-04T08:29:00+00:00"
lastmod: "2021-08-23T13:04:16+00:00"
description: "In this article, I will try to explain the new feature, from Java 10, local variable type inference using the reserved type name \"var\"."
canonical: "https://ashishtechmill.com/local-variable-type-inference-declare-var-not-war"
authors:
  - "yrashish"
image: "https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png"
categories:
  - "JEPs"
tags:
related_posts:
enlighterjs: true
frozen: false
---

In this article, I will try to explain the new feature, from Java 10, "local variable type inference", which also provides the new reserved type name "var".

Java is changing rapidly and with the new release cycle of 6 months, we are continually getting new features to try out with every release. In Java 10, a new feature Local-Variable Type Inference was added. It is aimed at reducing boilerplate code and improving readability when declaring local variables with initializers.
> *"Programs must be written for people to read, and only incidentally for machines to execute," -- Harold Abelson*

Let's understand this with an example. The following code is written without using Local-Variable Type Inference:

<pre class="EnlighterJSRAW" data-enlighter-language="java">List&lt;Employee&gt; employees = Arrays.asList(new Employee(1, "Ashish", 28, 10000)
                , new Employee(2, "Ajay", 29, 1000)
                , new Employee(3, "Abhishek", 29, 10000));</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="java">Map&lt;Integer, String&gt; map = employees
  .stream()
  .collect(Collectors.toMap(Employee::getId,Employee::getName));</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="java">Map&lt;Long, List&lt;Employee&gt;&gt; listMap = employees
  .stream()
  .collect(Collectors.groupingBy(Employee::getSalary));</pre>

After refactoring, as per Java 10 local variable type inference:

<pre class="EnlighterJSRAW" data-enlighter-language="java">var employees = Arrays.asList(new Employee(1, "Ashish", 28, 10000)
                , new Employee(2, "Ajay", 29, 1000)
                , new Employee(3, "Abhishek", 29, 10000));</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="java">var employeeMap = employees.stream()
  .collect(Collectors.toMap(Employee::getId,Employee::getName));
</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="java">var groupedMap = employees.stream()
  .collect(Collectors.groupingBy(Employee::getSalary));</pre>

In the above refactored code, the compiler can infer the declared type itself by looking at the RHS declaration. These are just some examples to help you understand the feature and how we can use local variable type inference.

Now let's understand where local variable type inference can be used and where it cannot be used.

#### Where it can be used

1. Local variable initializers
2. Indexes in the enhanced forloop
3. Locals declared in a traditional forloop
4. Resource variables of the try-with-resources statement
5. Formal parameters of implicitly typed lambda expressions. (Support added in Java 11)

The following code snippet shows some valid examples:

<pre class="EnlighterJSRAW" data-enlighter-language="java">var str = new String("Hello");
var list = new ArrayList&lt;String&gt;();
var integerList = List.of(1,2,3,4);
for (var data :integerList) {
      System.out.println(data);
}
for (var i = 1; i &lt;= integerList.size(); i++) {
      System.out.println(i);
}
var path = Paths.get("/src/main/resources/app.log");
      try(var file = Files.lines(path)){
            //some code
}</pre>

#### Where it cannot be used

1. Fields
2. Method parameters
3. Method return types
4. Local variable declarations without any initialization
5. Cannot use null for initialization

I have tried to explain in the below code snippet what compiler error will come if it is used in a not supported way.

<pre class="EnlighterJSRAW" data-enlighter-language="java">//Cannot infer type: 'var' on variable without initializer
var x;
//Cannot infer type: lambda expression requires an explicit target type
var f = () -&gt; {};
//Cannot infer type: variable initializer is 'null'
var g = null;
//Array initializer is not allowed here
var k = {1, 2};</pre>

#### What's the benefit?

If you ask me personally, I think developers should use it judiciously. It is understood that there is excitement whenever a new feature comes in and you definitely want to give it try. But you have to understand that we as developers read code more often than we write it.

Since this feature is related to readability, some folks will like it and some will hate it. If during a code review someone says that he/she is not able to know the declared type of var, it means that it is not very clear to others so that maybe switching back to the "old fashioned" way where we declare types explicitly is not that bad after all. Again, in some situations declared types are quite obvious, so that you can skip the explicitly declared type and make use of the var instead.

#### Conclusion

In this article, we have covered what is the Local-Variable Type Inference new Java 10 feature with some examples where it can be used and where it's not supported. You can further read these [FAQs](https://openjdk.java.net/projects/amber/LVTIFAQ.html) prepared by the OpenJDK team about Local-Variable Type Inference.

#### Support me

If you like what you've just read, you can buy me a coffee! 🙂  
[![Buy Me A Coffee](https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png)](https://www.buymeacoffee.com/meashish)

#### Further Reading

You can continue reading some of my previous [articles](https://ashishtechmill.com).
