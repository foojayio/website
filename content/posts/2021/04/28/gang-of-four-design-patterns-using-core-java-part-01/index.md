---
title: "SKP's Core Java/Java EE Roots : Gang of Four Design Patterns - Part 01"
slug: "gang-of-four-design-patterns-using-core-java-part-01"
date: "2021-04-28T13:43:33+00:00"
lastmod: "2021-10-11T12:38:41+00:00"
description: "Gang of Four patterns are legend, let's walk through examples of the Observer, Factory, Command, Singleton, Decorator, and Factory Patterns."
authors:
  - "sumith-puri"
image: "java.jpg"
categories:
  - "Tutorials"
  - "Videos"
tags:
related_posts:
  - "gof-design-patterns-using-java-part-02"
  - "boxlang-ai-deep-dive-part-2-of-7-building-a-production-grade-ai-tool-ecosystem"
  - "jc-ai-newsletter-15"
  - "jc-ai-newsletter-13"
enlighterjs: true
frozen: false
---

**\[About SKP's Core Java/Java EE Roots\]**  
Series of Articles on Rooted Concepts in Core Java and J2EE. They Revolve Around Memory Architecture, Connection \& Memory Leaks, Core Java Syntax \& Semantics, Java Object Layout/Anatomy, Multi-Threading, Asynchronous Task Execution, Design Patterns, Java Agents, Class Loading, API Design, OOPs \& SOLID.  

To understand the philosophical and historical perspective on the Gang of Four's design patterns, I made a short, 10-minute video. (This was also my PluralSight Author Audition). Please take time to view the \[Introduction to Gang of Four Design Patterns\] Video.  

[Link to YouTube Video - Introduction to Design Patterns](https://youtu.be/vq9zkZBjWkw/)

I came up with my own examples to understand design patterns further. Try downloading the code and see if it helps you in comprehending the patterns in a better way. Some brief code snippets follow each pattern so you can get quick demonstrations. Feel free to bookmark this article as a quick reference/cheat sheet for when you want to quickly review each of them. Without further ado, let's jump into the Observer Pattern.

## Observer Pattern

The Observer Pattern, as the name suggests, is used in scenarios when updates need to be done at multiple points (Observers) depending on changes in state at another place (Subject). Each of the Observers has to register themselves with the Subject, individually. The Subject should also provide methods that allow the observers to remove themselves. Registered observers are informed of changes in state through a notify method. Usually.

The provided example is that of a StockBroker application, which involves the maintenance of various types of financial information. The Subject is the interface in the application that provides a template for the Observed class. StockData is the concrete implementation of the Subject and provides the implementation for addObserver(), removeObserver() and notifyObservers(). Additionally, it maintains a list of registered observers. IncomeHandler, InvestmentHandler and PortfolioHandler includes the various observers used to maintain the income, investments, and portfolio of a specific StockBroker. All these depend on the constantly fluctuating values of stocks. They are specifically interested in the stockSymbol, stockValue, and stockUnits of each individual stock. Each of the Observers implements the interface Observer. The Observer interface provides the update() method, which is implemented by each of these concrete classes.

Only the core concept is provided in the snippets below. You can \[Download the Sample Code\](<http://www.sumithpuri.me/coderanch/observer.jar> "Download the Sample Code") for the Observer Pattern.

```java
package com.sumsoft.design.patterns.observer;

/*
 * @author Sumith Puri
 */
public interface Observer {

    public void update(String stockSymbol, Float stockValue, Integer stockUnits);

}
```

```java
package com.sumsoft.design.patterns.observer;

/*
 * @author Sumith Puri
 */
public class IncomeHandler implements Observer {

    Subject stockData = null;

    public IncomeHandler(Subject stockData) {
        this.stockData = stockData;
        stockData.addObserver(this);
    }

    @Override
    public void update(String stockSymbol, Float stockValue, Integer stockUnits) {
        System.out.println("IncomeHandler received changes... ");
    }

}
```

```java
package com.sumsoft.design.patterns.observer;

/*
 * @author Sumith Puri
 */
public interface Subject {

    public void addObserver(Observer o);
    public void removeObserver(Observer o);
    public void notifyObservers();
}
```

```java
package com.sumsoft.design.patterns.observer;

import java.util.ArrayList;
import java.util.List;

/*
 * @author Sumith Puri
 */
public class StockData implements Subject {

    private String stockSymbol = null;
    private Float stockValue = null;
    private Integer stockUnits = null;
    private List<Observer> observers = null;

    public StockData() {
        observers = new ArrayList<Observer>();
    }

    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void notifyObservers() {
        for(Observer o: observers) {
            o.update(stockSymbol, stockValue, stockUnits);
        }
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    public void setStockData(String stockSymbol, Float stockValue, Integer stockUnits) {
        // In real-time, this method might be invoked with values from a live web service at regular intervals.
        this.stockSymbol = stockSymbol;
        this.stockValue = stockValue;
        this.stockUnits = stockUnits;
        setDataChanged();
    }

    private void setDataChanged() {
        notifyObservers();
    }
}
```

Use StockBroker.java to run the application. Try adding your own Observer to this application. Also, you can try picking up these values from a live web service and writing a custom observer which depends on this.

## Decorator Pattern

The Decorator Pattern provides an elegant way to use composition for enhancing functionality where the result expected has a direct dependency on the composed and composing class. A chain relation (via composition) or decoration can be finally used to achieve the desired output at runtime. In real-time, when the functionality of one particular product is expected to be built from a base product and various other related sub-products or fixtures, we can rely on the Decorator.

The attached example is that of a Pizza application. Here, the pizzas in the shop are made with various combinations of bases and topping combinations. This is a classic example to use the Decorator Pattern on. Pizza is the abstract base class for each of the pizza bases to implement, and ToppingDecorator is another abstract class that inherits from Pizza for each of the toppings to implement. Hawaiian, Italian, and Mexican are the concrete implementations of Pizza, whereas Mushroom, Onion, and Chicken are the concrete implementations of ToppingDecorator. Each of these toppings encapsulates a Pizza instance. This instance, at runtime, will hold another topping or the pizza base instance. Finally, it is when the cost has to be calculated on the entire pizza that the real value of decorator pattern is seen and just one call suffices to calculate the entire bill value.

Only the core concept is provided in the snippets below. You can \[Download the Sample Code\](<http://www.sumithpuri.me/coderanch/decorator.jar> "Download the Sample Code") for the Decorator Pattern.

```java
package com.sumsoft.design.patterns.decorator;

/*
 * @author Sumith Puri
 */
public abstract class ToppingDecorator extends Pizza {

    public abstract String getDescription();
}
```

```java
package com.sumsoft.design.patterns.decorator;

/*
 * @author Sumith Puri
 */
public class Mushroom extends ToppingDecorator {

    Pizza pizza;

    public Mushroom(Pizza pizza) {

        this.pizza = pizza;
    }

    @Override
    public String getDescription() {

        return pizza.getDescription() + ", Mushroom";
    }

    @Override
    public double cost() {

        return 0.25 + pizza.cost();
    }

}
```

```java
package com.sumsoft.design.patterns.decorator;

/*
 * @author Sumith Puri
 */
public abstract class Pizza {

    protected String description = null;

    public String getDescription() {
        return description;
    }

    public abstract double cost();
}
```

```java
package com.sumsoft.design.patterns.decorator;

/*
 * @author Sumith Puri
 */
public class Italian extends Pizza {

    public Italian(String description) {
        this.description = description  + ", Italian";
    }

    @Override
    public double cost() {

        return 1.20;
    }

}
```

```java
package com.sumsoft.design.patterns.decorator;

/*
 * @author Sumith Puri
 */
public class PizzaWorld {

    public static void main(String args[]) {

        Pizza pizza = new Hawaiian("Veg Exotica");
        pizza = new Mushroom(pizza);
        pizza = new Mushroom(pizza);
        pizza = new Onion(pizza);

        Pizza pizza1 = new Italian("Non-Veg Supreme");
        pizza1 = new Chicken(pizza1);
        pizza1 = new Chicken(pizza1);
        pizza1 = new Onion(pizza1);
        pizza1 = new Onion(pizza1);

        System.out.println("Pizza World");
        System.out.println("===========");
        System.out.println("");
        System.out.println(pizza.getDescription() + " " + pizza.cost());
        System.out.println(pizza1.getDescription() + " " + pizza1.cost());
    }
}
```

PizzaWorld is the main class. Try adding more decorators and pizza base classes to see if you can get a real taste of the Decorator.  

## Singleton Pattern

The Singleton Pattern defines a way to maintain only a single instance of a class in the entire execution of a program/application and to provide a uniform way to access it. There are numerous methods that exist in which this pattern can be implemented. I have explained the three most common scenarios here.

### Eager Singleton

The simplest Singleton (\[Download the Sample Code Here\](<http://www.sumithpuri.me/coderanch/singleton_eager.jar> "Download the Sample Code Here")) is the one in which the instance is created at class-load time and stored in a static instance variable. A static getter method is then used to get this instance when required. The instantiation of an object earlier than its first use might not be a recommended approach.

In the given example, MediaContract (Main Thread) works on an instance of the ProductionHouse (Singleton). The Singleton is instantiated at class-load time and maintained in the private static instance variable. getInstance() in ProductionHouse helps in retrieving the instance.

```java
package com.sumsoft.design.patterns.singleton.eager;

/*
 * @author Sumith Puri
 */
public class ProductionHouse {

    private static ProductionHouse productionHouse = new ProductionHouse();

    private ProductionHouse() {

    }

    public static synchronized ProductionHouse getInstance() {

        return productionHouse;
    }
}
```

### Thread-Safe Singleton (Most Common)

To overcome the above drawback, the recommended approach is to instantiate the object at the first access time and also to make it thread-safe (\[Download the Sample Code\](<http://www.sumithpuri.me/coderanch/singleton_threadsafe.jar> "Download the Sample Code")) to prevent concurrent thread instantiation. The disadvantage of this method is poorer performance, as the method is synchronized.

As in the earlier example, the classes are MediaContract (Main Thread) and ProductionHouse (Singleton). The getInstance() method is synchronized, and the instance is created only if it is null.

```java
package com.sumsoft.design.patterns.singleton.threadsafe;

/*
 * @author Sumith Puri
 */
public class ProductionHouse {

    private static ProductionHouse productionHouse = null;

    private ProductionHouse() {

    }

    public static synchronized ProductionHouse getInstance() {

        if(productionHouse == null) {
            productionHouse = new ProductionHouse();
        }

        return productionHouse;
    }
}
```

### Double-Checked Locking

The disadvantage mentioned above can be critical for a highly accessed object in an application. To improve this, the scope of the synchronized block is reduced to affect only the first access. This, again, has some disadvantages.

The example remains the same, the difference being in the reduced scope of synchronization within the getInstance() method --- and also that it affects only the first access and not subsequent accesses. You can \[Download the Sample Code\](<http://www.sumithpuri.me/coderanch/singleton_doublechecked.jar> "Download the Sample Code") here.

```java
package com.sumsoft.design.patterns.singleton.doublechecked;

/*
 * @author Sumith Puri
 */
public class ProductionHouse {

    private static ProductionHouse productionHouse = null;

    private ProductionHouse() {

    }

    public static ProductionHouse getInstance() {

        if(productionHouse == null) {
            synchronized(ProductionHouse.class) {
                if(productionHouse == null) {
                    productionHouse = new ProductionHouse();
                }
            }
        }

        return productionHouse;
    }
}
```

For all the three partial samples above, you may use the following code to run and understand the different ways to instantiate Singletons.

```java
package com.sumsoft.design.patterns.singleton.doublechecked;

/*
 * @author Sumith Puri
 */
public class MediaContract extends Thread {

    public void run() {
        getProductionHouse();
    }

    public void getProductionHouse() {
        ProductionHouse productionHouse = ProductionHouse.getInstance();
        System.out.println(productionHouse.toString());
    }

    public static void main(String args[]) {

        MediaContract thread01 = new MediaContract();
        thread01.start();

        MediaContract thread02 = new MediaContract();
        thread02.start();
    }

}
```

## Command Pattern

In scenarios where we need to create a sequence of actions (or operations) and perform them at a specified (later) point in time, we have a candidate for usage of the Command Pattern. Though it very closely resembles the Observer pattern in implementation, the usage is different and the command (actions) is invoked only on a single chosen receiver by an invoker, rather than on all Observers.

We'll look at an example of an auction house where there are various items for auction. The base abstract class of the lots is represented by AuctionItem. The abstract method to be implemented by implementing classes is sell(). AuctionVase, AuctionFurniture, and AuctionJewel are all concrete implementations of AuctionItem. Instances of each of these are created and set (mapped by an itemKey) into the AuctionControl, which can be thought of as a remote control for presenting items in the AuctionStore. Whenever the presentItem() is invoked on the AuctionControl class, passing in an itemKey, the appropriate AuctionItem instance is selected and sell() is invoked on this instance.

Only the core concept is provided in the snippets below. You can \[Download the Sample Code Here\](<http://www.sumithpuri.me/coderanch/command.jar> "Download the Sample Code Here") for Command Pattern.

```java
package com.sumsoft.design.patterns.command;

/*
 * @author Sumith Puri
 */
public abstract class AuctionItem {

    public void sell() {

    }
}
```

```java
package com.sumsoft.design.patterns.command;

/*
 * @author Sumith Puri
 */
public class AuctionFurniture extends AuctionItem {

    public void sell() {
        System.out.println("Sold Furniture Item");
    }
}
```

```java
package com.sumsoft.design.patterns.command;

import java.util.HashMap;
import java.util.Map;

/*
 * @author Sumith Puri
 */
public class AuctionControl {

    Map<String, AuctionItem> auctionItems = new HashMap<String, AuctionItem>();

    public void setAuctionItem(String itemKey, AuctionItem auctionItem) {

        auctionItems.put(itemKey, auctionItem);
    }

    public void presentItem(String itemKey) {

        AuctionItem auctionItem = auctionItems.get(itemKey);
        auctionItem.sell();
    }
}
```

## Factory Pattern

The Factory Pattern, I am made to believe, is the most widely used and implemented pattern in software projects, after the Singleton Pattern. Since Singleton is only a creational pattern at a single class level, the scale for using the Factory Pattern should be much higher. The Factory Pattern deals with the creation of similar types of objects and producing them in a centralized manner, depending on the condition or type of object requested. There are plenty of variations of the Factory Pattern, three of which I have listed below.

### Simple Factory

The Simplest Factory Pattern (\[Download the Code Here\](<http://www.sumithpuri.me/coderanch/simple_factory.jar> "Download the Code Here")) is the one that is used to create (instantiate) a specific type of product (object) depending on a condition. The specific types of objects that can be created in a single factory are all expected to implement a single interface.

In the attached example, the factory is used to instantiate a specific type of object depending on the operating system. All the specific systems implement the System interface, which defines the common methods that the concrete class of this type should implement. SystemFactory is the factory class that provides the create() method, which takes a type argument. The type argument decides which concrete factory should be instantiated.

```java
package com.sumsoft.design.patterns.factory.simple;

/*
 * @author Sumith Puri
 */
public interface System {

    public void provision();
    public void update();
    public void restart();

}
```

```java
package com.sumsoft.design.patterns.factory.simple;

/*
 * @author Sumith Puri
 */
public class UnixSystem implements System {

    @Override
    public void provision() {
        // TODO Auto-generated method stub

    }

    @Override
    public void restart() {
        // TODO Auto-generated method stub

    }

    @Override
    public void update() {
        // TODO Auto-generated method stub

    }
}
```

```java
package com.sumsoft.design.patterns.factory.simple;

/*
 * @author Sumith Puri
 */
public class SystemFactory {

    public System createSystem(String type) {
        System system = null;
        if(type.equals("WIN")) {
            system = new WindowsSystem();
        } else if (type.equals("LIN")) {
            system = new LinuxSystem();
        } else if (type.equals("SOL")) {
            system = new SolarisSystem();
        } else if (type.equals("MAC")) {
            system = new MacosSystem();
        } else {
            system = new UnixSystem();
        }

        return system;
    }

}
```

### Factory Method

When there can be various families of products (objects) that can be instantiated, but each family of these products needs to be created by a specific type of factory, we define a factory method in the base factory class. The concrete implementations of the base factory then override this method to produce concrete type of products, depending on the condition. \[Download the Sample Code Here\](<http://www.sumithpuri.me/coderanch/factory_method.jar> "Download the Sample Code Here").

In the example, you can notice the presence of two abstract classes, Mobile (Product) and MobileStore (Creator). One family of concrete product implementations are NokiaASeries, NokiaBSeries, and NokiaCSeries --- to be created by the NokiaStore, which is the concrete implementation of the creator. In a similar fashion, another family of products, such as SonyASeries, SonyBSeries, and SonyCSeries are to be created by SonyStore, another concrete implementation of MobileStore. MobileStoreCentre is the main class to run this application. The createMobile() method is the abstract method (factory method) that is to be overridden by the creator implementations.

```java
package com.sumsoft.design.patterns.factory.method;

/*
 * @author Sumith Puri
 */
public abstract class Mobile {

    public void chassis() {
        System.out.println("Default Chassis Included.");
    }

    public void experience() {
        System.out.println("Default Experience Hardware.");
    }

    public void integrity() {
        System.out.println("Default Integrity Check.");
    }

    public void box() {
        System.out.println("Default Box Packaging.");
    }

    public void software() {
        System.out.println("Default Software Bundled.");
    }
}
```

```java
package com.sumsoft.design.patterns.factory.method;

/*
 * @author Sumith Puri
 */
public class NokiaASeries extends Mobile {

    public void experience() {
        System.out.println("Nokia Premium Hardware.");
    }
}
```

```java
package com.sumsoft.design.patterns.factory.method;

/*
 * @author Sumith Puri
 */
public class SonyASeries extends Mobile {

    public void experience() {
        System.out.println("Sony Premium Hardware.");
    }
}
```

```java
package com.sumsoft.design.patterns.factory.method;

/*
 * @author Sumith Puri
 */
public abstract class MobileStore {

    public Mobile assemble(String make) {
        Mobile mobile;
        mobile = createMobile(make);
        mobile.chassis();
        mobile.integrity();
        mobile.experience();
        mobile.software();
        mobile.box();

        return mobile;
    }

    protected abstract Mobile createMobile(String make);

}
```

```java
package com.sumsoft.design.patterns.factory.method;

/*
 * @author Sumith Puri
 */
public class NokiaStore extends MobileStore {

    @Override
    protected Mobile createMobile(String make) {
        Mobile mobile = null;

        if(make.equals("ASeries")) {
            mobile = new NokiaASeries();
        } else if(make.equals("BSeries")) {
            mobile = new NokiaBSeries();
        } else if(make.equals("CSeries")) {
            mobile = new NokiaCSeries();
        }

        return mobile;
    }

}
```

```java
package com.sumsoft.design.patterns.factory.method;

/*
 * @author Sumith Puri
 */
public class MobileStoreCentre {

    public static void main(String args[]) {

        MobileStore mobileStore01 = new NokiaStore();
        MobileStore mobileStore02 = new SonyStore();

        Mobile mobile01 = mobileStore01.assemble("ASeries");
        Mobile mobile02 = mobileStore02.assemble("BSeries");
    }
}
```

## Abstract Factory

The Abstract Factory defines a template or interface for the creation of similar types of objects or implementations. Usually, an Abstract Factory will encapsulate one or more factory methods within it for actually creating the product. \[Download the Sample Code\](<http://www.sumithpuri.me/coderanch/abstract_factory.jar> "Download the Sample Code").

Taking the same example as above, MobileStoreFactory instantiates the concrete instance of the abstract factory (MobileStore) based upon the variable specified, either "Nokia" (NokiaStore) or "Sony"(SonyStore). The factory is then responsible for creating the objects of similar types based upon the choice --- such as "ASeries" or "BSeries" or "CSeries". The mobile is then assembled based upon this by the MobileStore. You may use MobileStoreCentre to run this example and understand the design pattern based on the output.

```java
package com.sumsoft.design.patterns.factory.abstract_;

/**
 * @author sumith_puri
 *
 * The Abstract Factory Design Pattern will instantiate the appropriate abstract
 * factory.
 */
public class MobileStoreFactory {

    // Get Abstract Factory
    public static MobileStore getMobileStore(String mobileStore) {

        MobileStore mStore = null;
        if ("Nokia".equalsIgnoreCase(mobileStore))
        mStore = new NokiaStore();
        else if ("Sony".equalsIgnoreCase(mobileStore))
        mStore = new SonyStore();
        return mStore;
    }
}
```

```java
package com.sumsoft.design.patterns.factory.abstract_;

/*
 * @author Sumith Puri
 */
public class MobileStoreCentre {

    public static void main(String args[]) {

        MobileStore mobileStore = MobileStoreFactory.getMobileStore("Nokia");
        Mobile mobile = mobileStore.assemble("ASeries");
        mobile.experience();

        System.out.println("");

        mobileStore = MobileStoreFactory.getMobileStore("Sony");
        mobile = mobileStore.assemble("BSeries");
        mobile.experience();
    }
}
```

Note: Only the code to explain the various design patterns' core concepts are included int he snippets above. You may download the code from each of the links above and run them on your system for a more thorough understanding. You may also choose to modify the code with your own examples to cement your knowledge.

I will continue this article in another post outlining more design patterns, including the Adapter, Facade, Iterator, and Template patterns. Later, I will Follow it Up with the Remaining 11-12 Design Patterns to complete the Catalog of Original 23 GoF Design Patterns
