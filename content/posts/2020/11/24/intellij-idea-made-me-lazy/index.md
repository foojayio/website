---
title: "It's all IntelliJ IDEA's Fault: IntelliJ IDEA Made Me Lazy"
slug: "intellij-idea-made-me-lazy"
date: "2020-11-24T08:58:42+00:00"
lastmod: "2020-11-24T08:59:42+00:00"
description: "Being lazy isn't a bad thing, it's an efficiency gain for focusing on the things that matter, which isn't checking my parentheses are correct!"
canonical: "https://www.helenjoscott.com/posts/intellij-idea-made-me-lazy"
authors:
  - "helenjoscott"
image: "https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/lazy-cat.jpg"
categories:
  - "IntelliJ IDEA"
tags:
related_posts:
frozen: false
---

I haven't always been lazy; it's a fairly recent addition to my repertoire of skills. And do you know who I blame? I blame IntelliJ IDEA. I used to check that I'd completed a statement correctly, I used to look at *javadoc*, I used to check I'd closed my parentheses correctly, but now I don't give things a second glance.

![Lazy cat](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/lazy-cat.jpg)

## It's all IntelliJ IDEA's Fault

No, really, it is. Back when I was a *cough* younger adult, we had to type out ***all the code***, we also thought it was cool to mix drinks that turned into something akin to gorilla snot. Maybe we weren't that smart, but I am discovering one thing at a rapid rate, IntelliJ IDEA was smarter than me, even 20 years ago (I'd like to say it was a low bar).

I took a long break from coding, about 20 years, in fact. When I left coding, I wrote code in Vi and had memorised that very useful shortcut *:wq*; IDE's were around in fledgeling forms. Recently I came back to coding, and I walked into IntelliJ IDEA's open arms.

### Exhibit 1 - Statement Completion

Never one to take responsibility for my own actions, I'd like to offer up exhibit one - [Statement Completion in IntelliJ IDEA](https://www.jetbrains.com/help/idea/auto-completing-code.html). When you use the combination of ***Cmd*** +***Shift*** +***Enter*** , on macOS or ***Ctrl*** +***Shift*** +***Enter*** on Windows/Linux, IntelliJ IDEA completes it for you so that it will compile. I don't even know where the semi-colon button is on my keyboard anymore; I don't need it. Allow me to demonstrate.

Try this one:

![Before Statement Completion](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/auto-complete-statement-pre.png)

Rubbish right? Naturally, it won't compile. Use Statement Complete and boom, we get this:

![After Statement Completion](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/auto-complete-statement-post.png)

IntelliJ IDEA is kind and pops the caret in the right place as part of Statement Complete too. Sure, it's only a few characters, but there's a deep-seated comfort in knowing IntelliJ IDEA made it compile. It might not do what you want yet, but that's your problem, mostly. I recommend you train your fingers to master this one, it will help you sleep at night.

![Statement Complete Shortcuts](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/statement-complete-presentation-mode.png)

### Exhibit 2 - Live Templates

I covered these in detail [in this blog](https://www.helenjoscott.com/posts/live-templates-in-intellij-idea), but here's a quick recap. Remember the days when you had to type the whole `for i` loop out? Well, they're as dead as the shell-suit fashion of the 1990s.

![For i loop before completion](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/for-live-template-pre.png)

After we press return, the [Live Template](https://www.jetbrains.com/help/idea/using-live-templates.html) in IntelliJ IDEA does all the boring stuff for us.

![For i loop after completion](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/for-live-template-post.png)

Incidentally, you should press ***Tab*** to move to the next point that needs your input (the red squiggle).

A few more of my favourites are:

`psfs` will give you `public static final String`

And

`soutv` (in a `fori` loop will give you `System.out.println("i = " + i);`)

There's [35 more for you to check out](https://www.jetbrains.com/help/idea/using-live-templates.html) with your beverage of choice! There's no keyboard shortcut for Live Templates; they'll just appear when you summon them by starting to type what you want.

### Exhibit 3 - Post-Fix Completion

It seems like the humble dot is extremely powerful in IntelliJ IDEA. It does [Basic Completion](https://www.jetbrains.com/help/idea/auto-completing-code.html#basic_completion) automagically to suggest what might come next, but, it also does something very cool in that it can be used to tell IntelliJ IDEA to wrap whatever you just typed in some more code.

We're just mere humans, we obey the laws of the languages we speak and write (mostly), but thinking is *hard*. Allow me to demonstrate. We don't think:

1) I want to output some text to the console.  

2) I want to write some text.

No. We think:

1) I want to write some text.  

2) Oh, also, I want to output that text to the console.

This is exactly what [Post-Fix Completion](https://www.jetbrains.com/help/idea/settings-postfix-completion.html) does. It builds on live templates, where applicable, and helps your brain to flow in the order of things that it's probably become accustomed to over the years (many in my case).

Before post-fix completion, the earlier example becomes:

1. I want to Output some text to the console -\> `sout.`-\> `System.out.println();`
2. And then I want to write the text to output -\> `"I wandered lonely as a cloud"`

But, with post-fix completion:

1. I want to write some text. -\> `"I wandered lonely as a cloud"`
2. Oh, also, I want to output that text to the console -\> `.sout` -\> `System.out.println("I wandered lonely as a cloud");`

There are [46 types of post-fix completion](https://www.jetbrains.com/help/idea/settings-postfix-completion.html) in IntelliJ IDEA 2020.2. That's a lot of time saved in your day and a lot of language-based syntactic-sugar for your thinking-brain. Other cool examples include:

`assert` to create an assertion based on a boolean expression, for example:  
`is2020OverYet.assert`

Becomes

`assert value;`

My favourite code construct, the humble `for` (and `fori` loops):

`String[] monthsLeftIn2020 = {"November", "December"}`  
`monthsLeftIn2020.for`

Becomes

```String[]
for (String months : monthsLeftIn2020) {

}
```

Lastly, this one is pretty cool. I like `var` and this is one way that IntelliJ IDEA takes the notion of var to make your life just a little easier. You can use `.var` to ask IntelliJ IDEA to declare something that you've created as the correct explicit variable type. Let's look at the `String.var` portion of this:

    void speed2020Up(Magic magic) {
       magic instanceof String.var
    }

Using `.var` asks IntelliJ IDEA to put the variable type in for us, it's a boolean because that's what `instanceof` returns.

    void speed2020Up(Magic magic) {
       boolean b = magic instanceof String;
    }

You will also have the option to use the `var` type for real at this point if you want to. Now, `var` is fabulous, but Java is still a statically typed language. If you, as a human-being (presumably), can't figure out what type it is, please don't use `var` as the variable type. Using `.var` is geat, but declaring something as type `var` is a big no-no *if* the type of the expression is not quickly decipherable. Gift your fellow humans (right now, we all need it) by stating the type that it evaluates to when all is said and done. They'll thank you for it and you'll help *them* sleep better at night.

### Exhibit 4 - Smart Completion

With IntelliJ IDEA you get [Basic Completion](https://www.jetbrains.com/help/idea/auto-completing-code.html#basic_completion) out of the box, but [Smart Completion](https://www.jetbrains.com/help/idea/auto-completing-code.html#smart_completion) is just an extra finger tap away by using ***Ctrl*** +***Shift*** +***Space***.

Smart Completion not only analyses what could be correct, but it takes it one step further and looks at the context of the surrounding code to make the right decision. If IntelliJ IDEA can determine the appropriate type for the right-hand side of an assignment statement, initiating a variable, a return statement, arguments of a method call and more, it will filter the list to just that.

To show you the power of Smart Completion, I'll show it to you alongside Basic Completion for comparison.

First up, we'll invoke Basic Completion (no 'dot' so it won't come up automatically in this case) Note that I have a space after `new`.

`HashMap hashMap = new`

With basic completion we get a whole list of options, some valid, some not so much.

![Basic Completion for Hashmap](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/basic-completion-hashmap.png)

But, with Smart Completion, we get a much shorter, more relevant list.

![Smart Completion for Hashmap](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/smart-completion-hashmap.png)

Okay, let's look at a return statement. With basic completion.

![Basic Completion with Return Statement](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/basic-completion-return-statement.png)

And with smart completion.

![Smart Completion with Return Statement](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/smart-completion-return-statement.png)

You get the idea, it's very useful, and here is the keyboard shortcut that you need.

![Smart Completion Shortcuts](https://helenjoscott-blogs.s3.eu-west-2.amazonaws.com/smart-completion-shortcuts.png)

## Did IntelliJ IDEA Make You Lazy Too?

There's a lot of advantages to being lazy. It affords you more time for yourself, and your loved ones for a start. However, it's more than that, being a developer isn't all about writing code; there's also:

* Thinking / flow time
* Working out what your code needs to do
* Asking questions / Answering questions
* Mentoring others / Being mentored yourself
* So much more!

Being lazy isn't a bad thing, it's an efficiency gain that allows me to focus on the things that matter, which isn't checking my parentheses are correct or remembering to put a semi-colon after my statement. No, it's taking time for myself and those around me. Sure, it's IntelliJ IDEA's fault, but I am happy that we're here!
