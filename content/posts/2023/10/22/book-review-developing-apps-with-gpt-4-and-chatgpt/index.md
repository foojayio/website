---
title: "Book review: “Developing Apps with GPT-4 and ChatGPT”"
slug: "book-review-developing-apps-with-gpt-4-and-chatgpt"
date: "2023-10-22T06:05:28+00:00"
lastmod: "2023-12-23T14:39:33+00:00"
description: "Thorough, practical examples using a new & rapidly evolving tool. Pro or contra, it's a very worthwhile read."
authors:
  - "simon-verhoeven"
image: "910nFKaAHeL._SL1500_.jpg"
categories:
  - "Book Review"
  - "Books"
  - "Machine Learning"
tags:
related_posts:
  - "book-review-api-design-patterns"
  - "book-review-designing-apis-with-swagger-and-openapi"
  - "book-review-effortless-cloud-native-app-development-using-skaffold"
  - "jc-ai-newsletter-16"
frozen: false
---

Whether you're pro or con the usage of AI, LLMs, and ChatGPT one cannot deny that there are a plethora of possibilities now available to us.

Now, given the rapid shift in this field, a lot of us are left with a lot of queries such as: what are the differences between the models, how do I query them and of course, how can I use them to my advantage?

That's where this useful book by [Olivier Caelen](https://www.linkedin.com/in/oliviercaelen) and [Marie-Alice Blete](https://www.linkedin.com/in/mblete) comes in particularly handy. It offers us some interesting insights into the various models, and how to make use of these using the ChatGPT Python library.  

<figure class="aligncenter is-resized">
 <img decoding="async" src="https://m.media-amazon.com/images/I/910nFKaAHeL._SL1500_.jpg" alt="" style="width:320px;height:420px">
</figure>

**price** : €59.99 for the eBook  
**publication date** : September 2023  
**publisher** : O'Reilly Media, Inc.  
**pages** : 157  
**ISBN**: 9781098152482

About the book {#h2-0-about-the-book}
-------------------------------------

The book starts of with a brief introduction of the key concepts of various generative models, and the evolutions \& challenges behind Large Language Models (LLMs) and Generative Pre-trained Transformer (GPT) that led us to this point.

* how predictions work
* tokenization
* bias
* how LLMs are trained
* the use of vector databases to retrieve complex data in a structured and efficient manner
* ...

It then takes us for a brief stroll through the history of ChatGPT, the challenges encountered, and why there are different models.

Then we consider different possible uses such as learning a new language (Duolingo), improving quality of life (Be my eyes), ...

To wrap up the introduction we take a look at the new possibilities thanks to the plugin system.

As to the meat of the book: we start of by experimenting within the OpenAI Playground, before diving into the actual fun bits.

We learn about

* prompts, the different variants \& their finetuning
* the tokenization system (and the associated costs)
* how to use a variety of APIs to develop simple applications (moderation, news generation, ...)
* finetuning models
* the plugin system to add capabilities
* What (not) to do

My thoughts {#h2-1-my-thoughts}
-------------------------------

It's a very interesting book which gives thorough, practical examples using a new \& rapidly evolving tool. Pro or contra, it's a very worthwhile read, so you can better evaluate the potential for your own use-cases.

For me, it also provided some interesting insights into how to avoid/reduce hallucinations/finetuning my queries, feed it more (up to date) data, and how to provide extra functionality to create a better user experience. It's certainly given me a lot of food for thought for my own "Simon says" application.

I was lucky enough to meet Marie-Alice at Devoxx where she gave a very interesting talk, so if you want to get some more insights I highly recommend watching it:

<https://www.youtube.com/watch?v=7dY8N97qy0k>

In case Java is more your forte than Python, there's also the useful [langchain4j](https://github.com/langchain4j/langchain4j) library.
