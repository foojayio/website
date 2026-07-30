---
title: "Exception Chaos Java Code Quiz | Foojay.io Today"
slug: "exception-chaos-java-code-quiz"
date: "2021-10-27T15:03:38+00:00"
lastmod: "2021-10-27T15:03:39+00:00"
description: "Working correctly with exceptions is crucial to a high-quality application that users enjoy using. This quiz reinforces some of the basics."
authors:
  - "rafael-del-nero"
image: "/images/posts/2021/10/exception-chaos-java-code-quiz/Favicon-3-2.png"
categories:
  - "Books"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Working correctly with exceptions is crucial to a high-quality application that users enjoy using. This quiz reinforces some of the basic concepts of exceptions.

Therefore, what do you think will happen when running the following code?

<pre class="EnlighterJSRAW" data-enlighter-language="java">import java.io.FileNotFoundException;

public class ExceptionChaosChallenge {
    static String s = "-";

    public static void main(String... doYouBest) {
        try {
            throw new IllegalArgumentException();
        } catch (Exception e) {
            try {
                try {
                    throw new FileNotFoundException();
                } catch (RuntimeException ex) {
                    s += "run";
                }
                throw new NullPointerException();
            } catch (Exception x) {
                s += "exc";
            } finally {
                s += "fin";
            }
        } finally {
            s += "of";
            try {
                throw new VirtualMachineError("JVMError") {};
            } catch (Error error) {
                s += "error" + error.getMessage();
            }
        }

        System.out.println(s);
    }

}</pre>

A) -runfinoferror

B) -excfinoferror

C) -runfinoferrorJVMError

D) -excfinoferrorJVMError

E) -runoferrorJVMError

### Warning: Try to answer the correct alternative BEFORE seeing the answer! Only then will you refine your Java skills! {#h3-0-warning-try-to-answer-the-correct-alternative-before-seeing-the-answer-only-then-will-you-refine-your-java-skills}

Working correctly with exceptions is crucial to a high-quality application that users enjoy using.

There are three main categories of exceptions:

1. **Checked Exceptions**: used to handle a business requirement or something that is expected and the system can recover from the error.
2. **Unchecked Exceptions**: used to handle non-functional errors and errors that can be avoided with an if such as ArrayOutOfBoundsException, NullPointerException... All of these exceptions inherit from RuntimeException.
3. **Error**: Very serious errors that shouldn't be handled, instead we should fix the code.

Now that we know what are the main Exception/Error types, let's analyze the code, firstly an IllegalArgumentException will be thrown:

`throw new IllegalArgumentException();`

Then, the logic will go in the inner try statement. Then it will **throw a new FileNotFoundException()** . However, **FileNotFoundException** is not a **RuntimeException** , instead, it's an **Exception**.

Therefore, the code execution continues, and the next Exception that is thrown is **NullPointerException** . As **NullPointerException** is an **Exception** , we will concatenate**"exc"** in the s variable.

The **finally** clause should be always executed unless there is a **System.exit(0)** or some other edge-cases in the program. Therefore the next String to be concatenated is: **"fin"** . The next **finally** clause will be also executed concatenating **"of"**.

Inside the last finally clause we are throwing **VirtualMachineError("JVMError")** , therefore we will concatenate "error" and "JVMError" coming from this **Error**.

To conclude, the following **String** will be printed... what do you think?

If you have any questions about this Java Challenge, don't hesitate to leave a question!

You can also check the original Java Challenge and take a look at the whole content of the Java Challengers initiative: <https://javachallengers.com/exception-chaos-java-challenge/>

Keep breaking your limits!
