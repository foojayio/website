---
title: "Getting Started with Deep Learning in Java using Deep Netts"
date: "2022-07-05T07:22:18+00:00"
lastmod: "2023-07-11T09:37:36+00:00"
description: "Learn how to make it easy to quickly start using deep learning and to integrate deep learning into existing Java applications."
authors:
  - "zoran-sevarac"
image: "deeplearning.png"
categories:
  - "Deep Netts"
  - "Machine Learning"
  - "NetBeans"
related_posts:
  - "quick-start-with-machine-learning-in-java"
  - "deep-learning-in-java-for-drug-discovery"
  - "deep-learning-in-java-for-nuclear-physics-using-deep-netts"
frozen: false
---

[Deep Netts](https://www.deepnetts.com/) is pure Java deep learning library with a friendly, Java centric API.

It makes it easy for Java developers to quickly start using deep learning and it is easy to integrate with existing Java applications.

It supports commonly used neural network architectures (feed forward networks, convolutional networks) for classification, regression and image recogniton tasks.

### Adding Deep Netts to your Project

To be able to use Deep Netts in Maven based Java project, add the following dependency into dependencies section of your pom.xml file:

```xml
<dependency>
    <groupId>com.deepnetts</groupId>
    <artifactId>deepnetts-core</artifactId>
    <version>1.13.2</version>
</dependency>
```

You can also clone the entire library and examples from the GitHub: <https://github.com/deepnetts/deepnetts-communityedition>

### Hello World: Iris Flowers Classifiction

Iris flowers classification problem is commonly used as a "hello world" example for machine learning.

Briefly, we have a CSV file that contains data about 4 atributes which describe Iris flowers (sepal length, sepal width, petal length and petal width), and 3 categories of Iris flowers.

For more details, see <https://en.wikipedia.org/wiki/Iris_flower_data_set>.

```java
package deepnetts.examples;

import deepnetts.data.DataSets;
import deepnetts.data.preprocessing.scale.MaxScaler;
import deepnetts.data.preprocessing.scale.MinMaxScaler;
import deepnetts.eval.ClassifierEvaluator;
import deepnetts.eval.ConfusionMatrix;
import javax.visrec.ml.eval.EvaluationMetrics;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import deepnetts.net.train.BackpropagationTrainer;
import deepnetts.net.train.opt.OptimizerType;
import deepnetts.util.DeepNettsException;
import java.io.IOException;
import javax.visrec.ml.data.DataSet;
import javax.visrec.ml.data.preprocessing.Scaler;

public class IrisFlowersClassifier {

    public static void main(String[] args) throws DeepNettsException, IOException {

        int numInputs = 4;  // corresponds to number of input features/attribute in data set
        int numOutputs = 3; // corresponds to number of categories/classes in data set

        // load iris data  set from csv file
        DataSet dataSet = DataSets.readCsv("datasets/iris.csv", numInputs, numOutputs, true);

        // scale data to range [0,1] in order to make it suitable for neural network processing
        Scaler scaler = new MaxScaler(dataSet);
        scaler.apply(dataSet);

        // split loaded data into training and test set 60 : 40% ratio
        DataSet[] trainTestSet = dataSet.split(0.6, 0.4);
        DataSet trainingSet = trainTestSet[0]; // part of data to use for training
        DataSet testSet = trainTestSet[1]; // part of data set to use for testing/evaluation

        // create instance of feed forward neural network (aka multi layer percetpron) using corresponding builder
        FeedForwardNetwork neuralNet = FeedForwardNetwork.builder()
                .addInputLayer(numInputs) // input layer accepts inputs from data set, and it's size must correspond to number of inputs in data set
                .addFullyConnectedLayer(8, ActivationType.RELU) // hidden fully connected layer enables solving more complex problems
                .addOutputLayer(numOutputs, ActivationType.SOFTMAX) // commonly used activation function in output layer for multi class classification
                .lossFunction(LossType.CROSS_ENTROPY) // commonly used loss function for multi class classification problems
                .randomSeed(456)    // fix ramdomization seed in order to be able to repeat the results - can use nay value
                .build();

        // get and configure instanceof training algorithm for neural network - backpropagation trainer
        BackpropagationTrainer trainer = neuralNet.getTrainer();
        trainer.setMaxError(0.04f); // training is stopped when thie error valueis reached
        trainer.setLearningRate(0.01f); // controls the learning step, percent of error used to tune internal weights parametars [0, 0.9]
        trainer.setOptimizer(OptimizerType.MOMENTUM); // use accelerated optimization method
        trainer.setMomentum(0.9f); // ammount of acceleration to use 

        // run the training
        neuralNet.train(trainingSet);

        // evaluate/test classifier - estimate how it will behave with unseen data
        ClassifierEvaluator evaluator = new ClassifierEvaluator();
        EvaluationMetrics em = evaluator.evaluate(neuralNet, testSet);
        System.out.println("CLASSIFIER EVALUATION METRICS"); 
        System.out.println(em); // print classifier test results
        System.out.println("CONFUSION MATRIX"); // print details of the confusion matrix
        ConfusionMatrix cm = evaluator.getConfusionMatrix();
        System.out.println(cm);
    }
}
```

Full source code of the example is available on the [GitHub](http://https://github.com/deepnetts/deepnetts-communityedition/blob/community-visrec/deepnetts-examples/src/main/java/deepnetts/examples/IrisFlowersClassifier.java "GitHub").

After running this you'll get something like this:

```
-------------------------------------------------------------------------------------
TRAINING NEURAL NETWORK
-------------------------------------------------------------------------------------
Epoch:1, Time:4ms, TrainError:0.947609, TrainErrorChange:0.947609, TrainAccuracy: 0.62857145
Epoch:2, Time:2ms, TrainError:0.58163124, TrainErrorChange:-0.36597776, TrainAccuracy: 0.6393557
...
Epoch:280, Time:0ms, TrainError:0.04000282, TrainErrorChange:-5.1554292E-5, TrainAccuracy: 0.97840476
Epoch:281, Time:0ms, TrainError:0.03988598, TrainErrorChange:-1.16840005E-4, TrainAccuracy: 0.97840476

TRAINING COMPLETED
Total Training Time: 134ms
------------------------------------------------------------------------
CLASSIFIER EVALUATION METRICS
Accuracy: 0.93460923 (How often is classifier correct in total)
Precision: 0.96491224 (How often is classifier correct when it gives positive prediction)
F1Score: 0.96491224 (Harmonic average (balance) of precision and recall)
Recall: 0.96491224 (When it is actually positive class, how often does it give positive prediction)

CONFUSION MATRIX
                none    setosaversicolor virginica
      none         0         0         0         0
    setosa         0        21         0         0
versicolor         0         0        20         2
 virginica         0         0         0        17
```

More examples like this that you can use as starter templates for your own AI/machine learning projects in Java are available at:

<https://github.com/deepnetts/examples>

**Tip.** A very cool example of how Deep Netts can be used with Apache Groovy to get Python-like development experience created by Paul King is available at <https://github.com/paulk-asert/groovy-data-science/tree/master/subprojects/IrisGraalVM>.

### Links

* [Deep Netts Community Edition at GitHub](https://github.com/deepnetts/deepnetts-communityedition "Deep Netts Community Edition at GitHub")
* [Official Deep Netts homepage](https://www.deepnetts.com "Official Deep Netts homepage")
* [Classifying Iris Flowers with Deep Learning, Groovy and GraalVM](https://blogs.apache.org/groovy/entry/classifying-iris-flowers-with-deep "Classifying Iris Flowers with Deep Learning, Groovy and GraalVM"), great comparison of various Java DL libraries
