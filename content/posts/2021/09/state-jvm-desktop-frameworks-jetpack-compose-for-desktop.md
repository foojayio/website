---
title: "Introducing Jetpack Compose for Desktop | Foojay.io Today"
slug: "state-jvm-desktop-frameworks-jetpack-compose-for-desktop"
date: "2021-09-27T07:36:24+00:00"
lastmod: "2021-09-27T08:46:58+00:00"
description: "This article is dedicated to Jet Compose for Desktop, the new kid on the block that offers a completely different approach."
canonical: "https://blog.frankel.ch/state-jvm-desktop-frameworks/5/"
authors:
  - "nicolas-frankel"
image: "https://foojay.io/wp-content/uploads/2021/02/basic-compose.jpg"
categories:
  - "Kotlin"
tags:
related_posts:
  - "exposed-kotlin-orm-complete-guide"
  - "checking-out-junie-a-coding-agent-by-jetbrains"
  - "duplicate-finder-for-text-requirements"
  - "how-does-it-feel-to-test-a-compiler"
enlighterjs: true
frozen: false
---

The previous articles of this series were dedicated to frameworks that adopted the same traditional Object-Oriented-Programming approach. Components were modeled as classes. This week's article is dedicated to Jet Compose for Desktop, the new kid on the block that offers a completely different approach.

1. [The state of JVM desktop frameworks: introduction](https://blog.frankel.ch/state-jvm-desktop-frameworks/1/)
2. [The state of JVM desktop frameworks: Swing](https://blog.frankel.ch/state-jvm-desktop-frameworks/2/)
3. [The state of JVM desktop frameworks: SWT](https://blog.frankel.ch/state-jvm-desktop-frameworks/3/)
4. [The state of JVM desktop frameworks: TornadoFX](https://blog.frankel.ch/state-jvm-desktop-frameworks/4/)

Getting your feet wet {#_getting_your_feet_wet}
-----------------------------------------------

Originally, Jetpack Compose is a framework for the Android runtime. Compose for Desktop is its port to the .

Traditional development follows principles: a single graphical component encapsulates state and provides behavior to change it. This is how AWT, Swing, JavaFX, and even SWT work.

The idea behind Compose is to move away from OOP to Functional Programming principles instead. **A component is modeled by a function** and **state is passed as its parameters**. The function is called when the state changed.

Here's a Compose snippet displaying a value in a text field:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">fun main() = Window {
  TextField("Hello world!")   // 1
}</pre>

1. `TextField` is not a call to a constructor but the invocation of a function

The source code does indeed dispel any potential misunderstanding:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">@Composable
fun TextField(
    value: String,
    ...
)</pre>

State hoisting {#_state_hoisting}
---------------------------------

To remove the state from the component is known as *state hoisting*.

Most applications do not stop at displaying state but offer a way to update it. The classical example in GUI applications is to have a label that mirrors a text field value.

![Basic Compose window](/images/posts/2021/09/state-jvm-desktop-frameworks-jetpack-compose-for-desktop/basic-compose.jpg)

Here's how it's done in Compose:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">fun main() = Window {                                               // 1
  val state = remember { mutableStateOf("Hello world!") }           // 2
  Row {                                                             // 3
    TextField(
      state.value,                                                  // 4
      { state.value = it }                                          // 5
    )
    Text(state.value)
  }
}</pre>

This deserves some explanation:

1. Top-level container. Drives the compose loop. We will look at [the compose loop](#the-compose-loop) in the next section.
2. Wraps a state object in a mutable container
3. Layout. With no set layout, components are painted on top of each other
4. Initial value
5. Function to be executed every time the value is changed

The Compose framework is designed around the concept of `State`. It provides several functions to instances of such classes.

![state api](/images/posts/2021/09/state-jvm-desktop-frameworks-jetpack-compose-for-desktop/state-api.png)

In itself, the state isn't interesting, just as mirroring the value of a text field. Let's imagine a simple calculator use-case but restrict it to the summing of the integer value of two fields instead.

![derived state compose](/images/posts/2021/09/state-jvm-desktop-frameworks-jetpack-compose-for-desktop/derived-state-compose.jpg)

We need a state object to hold the sum. Compose offers the concept of *derived state*:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">fun main() = Window {
  val first = remember { mutableStateOf(0) }                    // 1
  val second = remember { mutableStateOf(0) }                   // 2
  val sum = derivedStateOf { first.value + second.value }       // 3
  Row {
    TextField(first.value.toString(),  { first.value = it.toInt() })
    TextField(second.value.toString(), { second.value = it.toInt() })
    Text(sum.value.toString())
  }
}</pre>

1. First value field
2. Second value field
3. Whenever `first` or `second` value changes, `sum` is re-computed

Your own Compose component {#_your_own_compose_component}
---------------------------------------------------------

Creating your own Compose component is as easy as implementing a function and annotating it with `@Composable`.
> Composable can be applied to a function or lambda to indicate that the function/lambda can be used as part of a composition to describe a transformation from application data into a tree or hierarchy.
Composable JavaDocs,

The "calculator" snippet above can be rewritten like this:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">@Composable
fun IntField(state: MutableState) = TextField(     // 1
  state.value.toString(),
  { state.value = it.toInt() }
)

fun main() = Window {
  val first = remember { mutableStateOf(0) }
  val second = remember { mutableStateOf(0) }
  val sum = derivedStateOf { first.value + second.value }
  Row {
    IntField(first)
    IntField(second)
    Text(sum.value.toString())
  }
}</pre>

1. Look, ma, a new custom component!

Annotating a component with `@Composable` has an important consequence: it changes the signature of the function in the *bytecode*. In this, it's similar to coroutines.

This is the de-compiled version of the `IntField` function:

<pre class="EnlighterJSRAW" data-enlighter-language="java">public static final void IntField(
    androidx.compose.runtime.MutableState,
    androidx.compose.runtime.Composer, int
);</pre>

Note the additional `Composer` parameter. This is where the magic of Compose lies.

While the compiler itself handles coroutines, Compose needs a dedicated compiler plugin to achieve the same result.

The Compose loop {#_the_compose_loop}
-------------------------------------

So far, we have focused the post on how to develop with Compose. We avoided how Compose works. Still, I believe that developing with Compose is so different from working with other frameworks that it deserves a section.

Remember that every Compose component is *just* a function. Such functions are stateless and one injects state from outside by passing parameters. When the state changes, Compose detects it and triggers the repaint of the application.  

Functions are invoked again, including the top `Window()` one.

Compose achieves it via the added `Composer` parameter in `@Composable` functions. In the end, the `Window` function sets up this mechanism.

Interestingly enough, Compose for Desktop relies on a GUI class that inherits from...​ Swing's `JFrame`! This is summarized in the following class diagram:

![compose window class diagram](/images/posts/2021/09/state-jvm-desktop-frameworks-jetpack-compose-for-desktop/compose-window-class-diagram.png)

Remembering state {#_remembering_state}
---------------------------------------

Now is the time to write about the `remember` function. We know that Compose invokes functions for each state change. The state is stored in variables inside those functions. Thus, when Compose invokes a function, the state *is* lost and is reset to its initial value.

Run any of the above snippets without the `remember()` function: they don't do anything because the state is lost after each change.

To keep track of state across recompositions, you need to wrap it inside a `remember` block. This tells Compose to cache the state's value and to set it again after functions have been invoked - to remember it.

![remember api](/images/posts/2021/09/state-jvm-desktop-frameworks-jetpack-compose-for-desktop/remember-api.png)

The first `remember()` function will only run `calculation` during the initial composition. Further recompositions will yield the cached value.

Overloaded functions allow passing one or more parameters. If parameters have changed since the previous composition, Compose will invoke the `calculation` function and set the state to its return value. Otherwise, it will behave as above - cache the value.

Other considerations {#_other_considerations}
---------------------------------------------

* Alpha:First of all, note that Compose for Desktop is alpha. It's subject to change. You've been warned.
* Gradle plugin:Because of its Android roots, the Compose plugin that does the magic of changing function signatures in the bytecode is only available in Gradle. Make your peace with this, I'm pretty sure no Maven plugin is ever going to be officially published. Unless you write one.
* Distribution:The plugin provides a `package` task that creates an OS-specific installer. This is great to distribute your application.

  The task uses `jpackage` under the hood, so be sure to use a JDK 14 or more. Also, be aware that you will still require a JRE to execute the installed application.
* Labels:To label fields, avoid placing `Text` components on the UI as in the previous frameworks. Instead, set them on the fields themselves.

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">TextField(
    value = "Hello world!",
    onValueChange = {},
    label = { Text("Say hello!") },
)</pre>

  With no value, Compose display text labels as placeholders. With a value or when they receive focus, it will move them just above.

  ![Displaying labels](/images/posts/2021/09/state-jvm-desktop-frameworks-jetpack-compose-for-desktop/text-compose.jpg)
* Missing features:Again because of Android and because of the framework's maturity, some important features are missing. I noticed at least the following:
  * No tabbing *i.e.* pressing `TAB` jumps to the next field
  * No Table component *à la* `JTable`. On the other side, Compose provides a `SwingPanel` that allows you to embed any Swing component.
  * Complex layouts are possible but the implementation seems complex, at least to me

This is the final result:

![Resulting application](/images/posts/2021/09/state-jvm-desktop-frameworks-jetpack-compose-for-desktop/result-compose.jpg)

Conclusion {#_conclusion}
-------------------------

Jetpack Compose for Desktop seems to be an interesting initiative. The framework is in its early stage. But the functional approach is original compared to all other Java desktop frameworks.

The complete source code for this post can be found on [Github](https://github.com/ajavageek/renamer-compose) in Maven format.

**To go further:**

* [Build better apps faster with Jetpack Compose (Android)](https://developer.android.com/jetpack/compose)
* [Compose for Desktop](https://www.jetbrains.com/lp/compose/)
* [GitHub repo with samples](https://github.com/jetbrains/compose-jb)
* [Under the hood of Jetpack Compose --- part 2 of 2](https://medium.com/androiddevelopers/under-the-hood-of-jetpack-compose-part-2-of-2-37b2c20c6cdd)

*Originally published at [A Java Geek](https://blog.frankel.ch/state-jvm-desktop-frameworks/5/) on February 7^th^ 2021*

*[JVM]: Java Virtual Machine
*[OOP]: Object-Oriented Programming
*[GUI]: Graphical User Interface
