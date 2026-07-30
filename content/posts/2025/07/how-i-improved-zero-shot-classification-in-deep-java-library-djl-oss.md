---
title: "How I Improved Zero-Shot Classification in Deep Java Library (DJL) OSS: Enhancing Machine Learning Model Translators"
slug: "how-i-improved-zero-shot-classification-in-deep-java-library-djl-oss"
date: "2025-07-09T10:51:16+00:00"
lastmod: "2025-07-09T11:00:59+00:00"
description: "Learn how I improved zero-shot classification in Deep Java Library (DJL) by fixing token_type_ids support, optimizing logit handling, and aligning it with Hugging Face Transformers for accurate Java machine learning."
authors:
  - "raphael-delio"
image: "/images/posts/2025/07/how-i-improved-zero-shot-classification-in-deep-java-library-djl-oss/Screenshot-2025-06-15-at-18.41.17.png"
categories:
  - "Java"
  - "Machine Learning"
tags:
related_posts:
  - "fixed-window-counter-rate-limiter-redis-java"
  - "semantic-search-with-spring-boot-redis"
  - "sliding-window-counter-rate-limiter-redis-java"
  - "sliding-window-log-rate-limiter-redis-java"
enlighterjs: true
frozen: false
---

> Did you know the Deep Java Library (DJL) powers Spring AI and Redis OM Spring? DJL helps you run machine learning models right inside your Java applications.
>
> Check them out:
>
> * Spring AI with DJL: <https://docs.spring.io/spring-ai/reference/api/embeddings/onnx.html>
> * Semantic Search with SpringBoot \& Redis: <https://foojay.io/today/semantic-search-with-spring-boot-redis/>

**TL;DR:** {#h2-0-tl-dr}
------------------------

* You're doing zero-shot classification in a Java app using DJL.
* DJL didn't handle some models well --- like DeBERTa. It missed support for token_type_ids, assumed wrong label positions, and oversimplified the softmax implementation.
* It was fixed by reading the model config files and adjusting DJL's translator logic.
* Now DJL gives correct results across different models --- just like the Transformers library does in Python.
* The fix is merged and will probably be released with version 0.34.0.

What's Zero-Shot Classification (and Why It Matters) {#h2-1-what-s-zero-shot-classification-and-why-it-matters}
---------------------------------------------------------------------------------------------------------------

Zero-shot classification is a machine learning technique that allows models to classify text into categories they haven't explicitly seen during training. Unlike traditional classification models that can only predict classes they were trained on, zero-shot classifiers can generalize to new, unseen categories.

One example of a zero-shot classification model is `MoritzLaurer/DeBERTa-v3-large-mnli-fever-anli-ling-wanli`. Like many other models for this task, it works by comparing a sentence (the premise) to different hypotheses (the labels) and scoring how likely each one is to be true.

For example, we can compare "Java is a great programming language", the premise, to "Software Engineering, Software Programming, and Politics", the hypotheses. In this case, the model will return:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Software Programming: 0.984
Software Engineering: 0.015
Politics: 0.001</pre>

Meaning that "Software Programming" is the hypothesis that best classifies the premise.

In this example, we're comparing the premise to all hypotheses, but we could also compare them individually. We can do it by enabling the "multi_label" option. In this case, it will return:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Software Programming: 0.998
Software Engineering: 0.668
Politics: 0.000</pre>

With a higher score for "Software Engineering" and an even lower score for "Politics."

![](https://cdn-images-1.medium.com/max/2000/1*H7FiUe-NTKoYSlqg76peww.gif)

You can easily try it out at: [https://huggingface.co/MoritzLaurer/DeBERTa-v3-large-mnli-fever-anli-ling-wanli](https://huggingface.co/MoritzLaurer/DeBERTa-v3-large-mnli-fever-anli-ling-wanli?candidate_labels=Software+Engineering%2C+Software+Programming%2C+Politics&amp;multi_class=true&amp;text=Java+is+a+great+programming+language)

Integrating a Zero-Shot Classification Model with the Deep Java Library {#h2-2-integrating-a-zero-shot-classification-model-with-the-deep-java-library}
-------------------------------------------------------------------------------------------------------------------------------------------------------

![](https://cdn-images-1.medium.com/max/2612/0*MqJEP40BE5Gi3Ay7.png)

**The Deep Java Library (DJL) is an open-source library that makes it easier to work with machine learning models in Java.** It lets you run models locally, in-process, inside your Java application. It supports many engines (like PyTorch and TensorFlow), and it can load models directly from Hugging Face or from disk.

A cool thing about this library is that **it hosts a collection of pre-trained models in its model zoo.**Those models are ready to use for common tasks like image classification, object detection, text classification, and more. They are curated and maintained by the DJL team to ensure they work out of the box with DJL's APIs and that developers can load these models easily using a simple criteria-based API.

One example is this zero-shot classification model developed by Facebook: facebook/bart-large-mnli. This model is hosted by DJL in their model zoo and can easily be reached at the following URI djl://ai.djl.huggingface.pytorch/facebook/bart-large-mnli.

Let's see how we can easily load it into our Java application and use it to classify text.

### Dependencies {#h3-3-dependencies}

The dependencies we're gonna be using are:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">implementation("ai.djl.huggingface:tokenizers:0.32.0")
implementation("ai.djl.pytorch:pytorch-engine:0.32.0")
implementation("ai.djl:model-zoo:0.32.0")</pre>

### The Criteria Class {#h3-4-the-criteria-class}

The Criteria class in DJL is a builder-style utility that tells DJL **how to load and use a model**. It defines:

* **Input and output types** (e.g., ZeroShotClassificationInput, ZeroShotClassificationOutput)
* **Where to get the model from** (like a URL or model zoo ID)
* **Which engine to use** (e.g., PyTorch, TensorFlow, ONNX)
* **Extra arguments** (like tokenizer ID, batch size, device)
* **Custom logic** , like a translator to convert between raw inputs/outputs and tensors

<pre class="EnlighterJSRAW" data-enlighter-language="generic">String modelUrl = "djl://ai.djl.huggingface.pytorch/facebook/bart-large-mnli";

Criteria criteria = Criteria.builder()
            .optModelUrls(modelUrl)
            .optEngine("PyTorch")
            .setTypes(ZeroShotClassificationInput.class, ZeroShotClassificationOutput.class)
            .optTranslatorFactory(new ZeroShotClassificationTranslatorFactory())
            .build();</pre>

When building a Criteria in DJL, we need to pick an engine that matches what the model was trained with. Most Hugging Face models use PyTorch. We also have to define the input and output types the model expects. **For zero-shot classification, DJL gives us ready-to-use classes:**

**ZeroShotClassificationInput**: lets us set the text (premise), candidate labels (hypotheses), whether it's multi-label, and a hypothesis template;

**ZeroShotClassificationOutput**: returns the labels with their confidence scores.

Under the hood, machine learning models work with tensors that are basically arrays of numbers. **To go from readable input to tensors and then back from model output to readable results, DJL uses a Translator.** The **ZeroShotClassificationTranslatorFactory** creates a translator that knows how to tokenize the input text and how to turn raw model outputs (logits) into useful scores.

### Loading and using the model {#h3-5-loading-and-using-the-model}

Loading the model is easy --- you just call ModelZoo.loadModel(criteria). The criteria tells DJL what kind of model you're looking for, like the engine (PyTorch), input/output types, and where to find it. Once the model is loaded, we get a Predictor from it. That's what we use to actually run the predictions.

Next, we prepare the input. In this example, we're checking how related the sentence *"Java is the best programming language"* is to a few labels like *"Software Engineering"* , *"Software Programming"* , and *"Politics"*. Since a sentence can relate to more than one label, we set multiLabel to true.

Then, we run the prediction and check the result that contains the labels and their scores. Basically, how likely it is that the input belongs to each category.

Finally, we loop over the results and print each label with its score. Once we're done, we clean up by closing the predictor and model, which is always a good practice to free up resources.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">// Load the model
Model model = ModelZoo.loadModel(criteria);
Predictor predictor = model.newPredictor();

// Create the input
String inputText = "Java is the best programming language";
String[] candidateLabels = {"Software Engineering", "Software Programming", "Politics"};
boolean multiLabel = true;
ZeroShotClassificationInput input = new ZeroShotClassificationInput(inputText, candidateLabels, multiLabel);

// Perform the prediction
ZeroShotClassificationOutput result = predictor.predict(input);

// Print results
System.out.println("\nClassification results:");
String[] labels = result.getLabels();
double[] scores = result.getScores();
for (int i = 0; i &lt; labels.length; i++) {
    System.out.println(labels[i] + ": " + scores[i]);
}

// Clean up resources
predictor.close();
model.close();</pre>

By running the code above, we should see the following output:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Classification results:
Software Programming: 0.82975172996521
Software Engineering: 0.15263372659683228
Politics: 0.017614541575312614</pre>

This has been easy so far. But what if you want to use a different model?

Using different models {#h2-6-using-different-models}
-----------------------------------------------------

If you want to use a different model, you have two options: pick one that's hosted by DJL or load one directly from Hugging Face. To see all the models that DJL hosts, just run the code below , it'll all available models.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">// Create an empty criteria to fetch all available models
Criteria criteria = Criteria.builder().build();

// List available model names
Set modelNames = ModelZoo.listModels(criteria);
System.out.println("Available models from DJL:");
for (String name : modelNames) {
    System.out.println("- " + name);
}</pre>

This will output multiple models for you with their respective URIs that you can simply replace on the criteria we implemented previously in this tutorial. It should just work.

However, if you want to host a model that is not available in the Model Zoo, you will have to not only download it from HuggingFace, but also convert it to a format that is compatible with DJL.

Using a model that is not available in the Model Zoo {#h2-7-using-a-model-that-is-not-available-in-the-model-zoo}
-----------------------------------------------------------------------------------------------------------------

The model I want to use is the one I introduced in the beginning of this article: MoritzLaurer/DeBERTa-v3-large-mnli-fever-anli-ling-wanli. But since It's not available in the Model Zoo, we will need to perform a few extra steps to make it compatible with DJL.

**To convert Python models, DJL provides a tool called djl-convert that transforms these models into a format that works in Java**, removing Python-specific dependencies to make them ready for efficient inference with DJL.

To install djl-convert, you can run the following commands in your terminal:

<pre class="EnlighterJSRAW" data-enlighter-language="generic"># install release version of djl-converter
pip install https://publish.djl.ai/djl_converter/djl_converter-0.30.0-py3-none-any.whl
# install from djl master branch
pip install "git+https://github.com/deepjavalibrary/djl.git#subdirectory=extensions/tokenizers/src/main/python"
# install djl-convert from local djl repo
git clone https://github.com/deepjavalibrary/djl.git
cd djl/extensions/tokenizers/src/main/python
python3 -m pip install -e .
# Add djl-convert to PATH (if installed locally or not globally available)
export PATH="$HOME/.local/bin:$PATH"
# install optimum if you want to convert to OnnxRuntime
pip install optimum
# convert a single model to TorchScript, Onnxruntime or Rust
djl-convert --help
# import models as DJL Model Zoo
djl-import --help</pre>

After that, you can run the following command to convert the model to a format DJL can understand:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">djl-convert -m MoritzLaurer/DeBERTa-v3-large-mnli-fever-anli-ling-wanli</pre>

This will store the converted model under folder model/DeBERTa-v3-large-mnli-fever-anli-ling-wanli in the working directory.

Now we're ready to go back to our Java application.

### Loading a local model with DJL {#h3-8-loading-a-local-model-with-djl}

Loading a local model is also straightforward. Instead of loading it from the DJL URL, you're going to load it from the directory that was created during the conversion:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Criteria criteria = Criteria.builder()
                .optModelPath(Paths.get("model/DeBERTa-v3-large-mnli-fever-anli-ling-wanli"))
                .optEngine("PyTorch")
                .setTypes(ZeroShotClassificationInput.class, ZeroShotClassificationOutput.class)
                .optTranslatorFactory(new ZeroShotClassificationTranslatorFactory())
                .build();</pre>

Running it should be as straightforward as before:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">// Load the model
Model model = ModelZoo.loadModel(criteria);
Predictor predictor = model.newPredictor();

// Create the input
String inputText = "Java is the best programming language";
String[] candidateLabels = {"Software Engineering", "Software Programming", "Politics"};
boolean multiLabel = true;
ZeroShotClassificationInput input = new ZeroShotClassificationInput(inputText, candidateLabels, multiLabel);

// Perform the prediction
ZeroShotClassificationOutput result = predictor.predict(input);

// Print results
System.out.println("\nClassification results:");
String[] labels = result.getLabels();
double[] scores = result.getScores();
for (int i = 0; i  Dict(str, Tensor)
</pre>

Problem #1: No support for token_input_ids {#h2-9-problem-1-no-support-for-token-input-ids}
-------------------------------------------------------------------------------------------

Not every Zero-Shot Classification Model is the same, and one thing that sets them apart is whether they use **token type IDs**.

Toke Type IDs are just extra markers that tell the model where one part of the input ends and the other begins, like separating the main sentence from the label it's being compared to.

Some models, like BERT or DeBERTa, were trained to expect these markers, so they need them to work properly. Others, like RoBERTa or BART, were trained without them and just ignore that input.

And well, DJL's ZeroShotClassificationTranslator had been implemented and tested with a BART model, which didn't require token_type_ids to work properly.

By digging into the implementation of ZeroShotClassificationTranslator, I was able to see that token_type_ids were actually supported by DJL, it was simply hardcoded in the Translator, not allowing us to set it even if we initialized the Translator with its Builder:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">// Line 85 of ZeroShotClassificationTranslator: https://github.com/deepjavalibrary/djl/blob/fe8103c7498f23e209adc435410d9f3731f8dd65/extensions/tokenizers/src/main/java/ai/djl/huggingface/translator/ZeroShotClassificationTranslator.java
// Token Type Ids is hardcoded to false
NDList in = encoding.toNDList(manager, false, int32);</pre>

I fixed this by adding a method to the Translator Builder. This method sets the token_type_id property during initialization. I also refactored the class to make it work.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public ZeroShotClassificationTranslator.Builder optTokenTypeId(boolean withTokenType) {
    this.tokenTypeId = withTokenType;
    return this;
}</pre>

And even though it worked as I expected, I was surprised to find out that the scores that were output way off from scores I expected.

While Python's Transformers library would output the following, correct, results:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Software Programming: 0.9982864856719971
Software Engineering: 0.7510316371917725
Politics: 0.00020543287973850965</pre>

The Deep Java Library was outputting completely wrong scores:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Politics: 0.9988358616828918
Software Engineering: 0.0009450475918129086
Software Programming: 0.00021904722962062806</pre>

You can see that the scores were so wrong that it actually output that Politics was the label that best fit our premise: "Java is the best programming language."

What's going on here?

Problem #2: Hard coded logit positions and oversimplified softmax implementation {#h2-10-problem-2-hard-coded-logit-positions-and-oversimplified-softmax-implementation}
------------------------------------------------------------------------------------------------------------------------------------------------------------------------

To understand what's going on, we also need to understand how Zero-Shot Classification models work. These models aren't trained to classify things directly. Instead, they take two sentences, the input and the label as a hypothesis, and decide how they relate.

They return logits: raw scores for each label like "entailment", "contradiction", or "neutral". These logits are just numbers. To make them readable, we apply softmax, which turns them into probabilities between 0 and 1.

DJL's original implementation didn't handle this properly. It grabbed the last logit from each label's output, assuming it was the "entailment" score. Then, it normalized those scores across all labels.

This approach ignored how each label is its own comparison. Each one is a separate classification task. So softmax must be applied within each label, not across all labels.

Also, not all models use the same order for their logits. We can't assume "entailment" is always the last. To know the correct position, we should read the model's config.json and check the label2id field.

This mapping shows which index belongs to each class. Using it, we can apply softmax to the correct pair, usually "entailment" and "contradiction," for each label.
> Check an example of a config.json file here: <https://huggingface.co/MoritzLaurer/DeBERTa-v3-large-mnli-fever-anli-ling-wanli/blob/main/config.json>

Therefore, I not only had to fix the way softmax was applied, but also make sure we were using the correct index for the entailment score --- based on what the model actually defines in its config. That meant reading the label2id mapping from config.json, identifying which index corresponds to "*entailment* " and "*contradiction*", and then applying softmax to just those two values for each label.

After refactoring the softmax logic, the translator started outputting the expected results. To test it with different types of models, I created a GitHub repository comparing the expected results from Python's Transformers Library with the refactored ZeroShotClassificationTranslator.

You can check it out at: <https://github.com/raphaeldelio/deep-java-library-zero-shot-classification-comparison-to-python/>

Contributing to the Deep Java Library {#h2-11-contributing-to-the-deep-java-library}
------------------------------------------------------------------------------------

After I had tested and made sure the translator was working as expected, it was time to contribute back to the library. I opened a pull request to the DJL repository with the changes I had made. The maintainer was super responsive and helped me refactor my changes to follow the guidelines of the project, and after a few tweaks, the changes were approved and merged.

As a result, you can find the PR here: <https://github.com/deepjavalibrary/djl/pull/3712>

![](https://cdn-images-1.medium.com/max/800/1*qWVMzOJ0JDDrqtJpIFIRbg.png)

Final Words {#h2-12-final-words}
--------------------------------

If you're a Java developer working with AI, I really encourage you to check out the [Deep Java Library](https://github.com/deepjavalibrary/djl), the [Spring AI](https://docs.spring.io/spring-ai/), and the [Redis OM Spring](https://github.com/redis/redis-om-spring) projects, which build on top of it.

Thank you for following along!

### Stay Curious {#h3-13-stay-curious}
