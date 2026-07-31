---
title: "GoF Design Patterns Using Java - Part 02"
slug: "gof-design-patterns-using-java-part-02"
date: "2022-02-04T16:46:55+00:00"
lastmod: "2022-02-04T16:55:30+00:00"
description: "Let's continue learning design patterns by implementing the Adapter, Facade, Template, Iterator, and State patterns using Java."
authors:
  - "sumith-puri"
image: "https://1.bp.blogspot.com/-hKI50AxF4-E/YTtUtL05bfI/AAAAAAAA-cc/WHuoaObj0W0uErR_5nwcEBIG33ggDqtKQCLcBGAsYHQ/s16000/W3sDesign_Adapter_Design_Pattern_UML.jpg"
categories:
  - "Tutorials"
tags:
related_posts:
  - "gang-of-four-design-patterns-using-core-java-part-01"
  - "evolution-of-java-memory-architecture-post-java-7-0"
  - "java-roots-1-java-memory-architecture"
  - "boxlang-ai-deep-dive-part-2-of-7-building-a-production-grade-ai-tool-ecosystem"
frozen: false
---

[Link to YouTube Video - Introduction to Design Patterns](https://youtu.be/vq9zkZBjWkw/)  
[Link to Article : GoF Design Patterns Using Java - Part 01](https://foojay.io/today/gang-of-four-design-patterns-using-core-java-part-01/ "Link to Article : GoF Design Patterns Using Java - Part 01")  
[Link to GitHub Code Samples in Core Java for this Article](https://github.com/sumithpuri/skp-code-marathon-pattaya "Link to GitHub Code Samples in Core Java for this Article")  

*** ** * ** ***

**Adapter Pattern \[[Sample Code](https://github.com/sumithpuri/skp-code-marathon-pattaya/tree/master/src/main/java/me/sumithpuri/github/pattaya/adapter "Sample Code")\]** {#h2-0-adapter-pattern-sample-code}
---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

What can one do if he needs to use an Asian Hairdryer in a European Country, each with different socket types? I would seek an Adapter! As in real life, when we want to plug and play with similar but incompatible interfaces we use the Adapter. The Adapter adapts the Adaptee to the desired interface, by composing the Adaptee object and inheriting the desired interface or by multiple inheritance.

![](https://1.bp.blogspot.com/-hKI50AxF4-E/YTtUtL05bfI/AAAAAAAA-cc/WHuoaObj0W0uErR_5nwcEBIG33ggDqtKQCLcBGAsYHQ/s16000/W3sDesign_Adapter_Design_Pattern_UML.jpg)  
Fig. 1 : Adapter (Structural) Design Pattern - Class and Sequence Diagram   
\[Source : Wikipedia\]

The attached example is a real world Computer scenario, where I want to plug in an external hard drive (pre-usb era!), SeagateDrive of interface type SeagateGeneric to an incompatible computer, SamsungComputer of type Computer. SeagateGeneric provides read() and write() methods for the specified purposes, which needs to be adapted to the actual bufferData(), flushData() and purgeData() methods of the Computer. Note that there is no equivalent of purgeData().

The ideal way to handle this scenario is to throw an exception, whenever this method is invoked on the hard drive as it would do in the real world. The adapter to perform the translation in this scenario is the SeagateAdapter, which implements the Computer interface. It encapsulates a SeagateGeneric instance reference, and adapts it to the Computer interface. Whenever a bufferData() method is invoked on the Computer interface, it actual requires three invocations of read() on the SeagateGeneric implementation to match up to the Computer's standards. These kinds of translations are done by the adaptee.

PCAssembler is the main class here. Try adding your own device and its adapter to the computer.

**Facade Pattern \[[Sample Code](https://github.com/sumithpuri/skp-code-marathon-pattaya/tree/master/src/main/java/me/sumithpuri/github/pattaya/facade "Sample Code")\]** {#h2-1-facade-pattern-sample-code}
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Consider a scenario where we require multiple method invocations on various classes, to achieve the desired functionality. Also, consider that this set of functionality is repeatedly being used in your code. If you are thinking of an option where you will perform direct invocations, you are bound to end up with code maintenance issues and tightly coupled code. If these invocations are remote, it is going to be worse with respect to the performance.

![](https://1.bp.blogspot.com/-SHSmFHwnIgg/YTtcN2xnWmI/AAAAAAAA-dM/3FIRsJgIMmUz2eOwRunZjbAanXl_eVlqACLcBGAsYHQ/w400-h400/300px-Facade_Design_Pattern_Class_Diagram_UML.svg.jpg)  
Fig. 2 : Facade (Structural) Design Pattern - Class Diagram   
\[Source : Wikipedia\]

Under the above mentioned conditions is where the facade comes into play. Herein multiple method invocations are encapsulated into a single method of the facade class, to achieve the desired functionality. It provides us with a single point of change and looser coupling, with respect to the individual implementations. Remote method invocation patterns like SessionFacade (EJB) adapt from here to improve the overall performance and lower complexity.

![](https://1.bp.blogspot.com/-hr0nOY5U8v8/YTtcXbH6s5I/AAAAAAAA-dQ/gtgj5dW2B3UqEVukjPE83BYjaSqeUo0RwCLcBGAsYHQ/w400-h343/1024px-Facade_Design_Pattern_Sequence_Diagram_UML.svg.jpg)  
Fig. 3 : Facade (Structural) Design Pattern - Sequence Diagram   
\[Source : Wikipedia\]

The example attached is a very simple scenario of a InvoiceManagerFacade which has addInvoice() and deleteInvoice() methods. To achieve the desired result, each of these methods encapsulates the method invocations from OrderManager, LedgerManager and BillingManager classes.

AccountsCentral is the main class. Try adding your own method to the facade class, or try plugging in a new type of facade.  

**Template Pattern \[[Sample Code](https://github.com/sumithpuri/skp-code-marathon-pattaya/tree/master/src/main/java/me/sumithpuri/github/pattaya/template "Sample Code")\]** {#h2-2-template-pattern-sample-code}
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Imagine a real-world scenario where a factory is creating both aluminium nails and screws. Though the machine has to create both of them through similar processes, the way some steps are implemented may vary in each of these. When we think of such scenarios in software, we utilize the template pattern. Template pattern defines a way to re-use algorithms for various implementations with different or slightly different outcomes.

![](https://1.bp.blogspot.com/-JKo6kpXHhrw/YTtXG7Q3BiI/AAAAAAAA-c0/hSlaAhn5r64zCl64KJopOS-sOzDg7WCZwCLcBGAsYHQ/s16000/W3sDesign_Template_Method_Design_Pattern_UML.jpg)  
Fig. 4 : Template (Structural) Design Pattern - Sequence Diagram   
\[Source : Wikipedia\]

In the attached example, the abstract class SoftwareProcessor defines a general set of algorithmic steps (functions) to deliverSoftware(). This class is my template class. Since the implementation and testing phases differ in projects based on the technology stack being used, CProcessor and JavaProcessor classes adapt this algorithm for these phases. The common methods are all implemented in SoftwareProcessor and the specific ones are left as abstract.  
SoftwareConsultants can be used to run this example. Try adding your own processor.

**Iterator Pattern \[[Sample Code](https://github.com/sumithpuri/skp-code-marathon-pattaya/tree/master/src/main/java/me/sumithpuri/github/pattaya/iterator "Sample Code")\]** {#h2-3-iterator-pattern-sample-code}
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

The need to have a handle to a collection of elements, without exposing its internal implementation is met by the Iterator Pattern. I would term this as a pure programming pattern, in its own right. By utilising this handle (Iterator), the client using the collection can easily process the same without any dependency on the internal logic.

![](https://1.bp.blogspot.com/-wuCHJ2bZwbE/YTtawLGUBRI/AAAAAAAA-c8/Zrfn01Drx8oZPWae69FPYp5GwjZDWNxiACLcBGAsYHQ/s16000/W3sDesign_Iterator_Design_Pattern_UML.jpg)  
Fig. 5 : Iterator (Behavioral) Design Pattern - Class and Sequence Diagram   
\[Source : Wikipedia\]

In the attached example, ProductMenu holds a menu or list of ProductItem. This list and its usage should be implementation agnostic to the clients. Hence, the need for a ProductIterator which implements the generic Iterator interface. The createIterator() method of ProductMenu, passes the array implementation of ProductItem to the constructor of ProductIterator.

The example can be run using ProductMenuTester.

**State Pattern \[[Sample Code](https://github.com/sumithpuri/skp-code-marathon-pattaya/tree/master/src/main/java/me/sumithpuri/github/pattaya/state "Sample Code")\]**

State Pattern defines a way to maintain various steps or states of the same machine or class. The word machine comes to the mind easily, because it is the simplest example of a real-world scenario where there is a need for operating the same object in steps or set states, with the transition from one step to the next defined by a single action (or multiple actions).

![](https://1.bp.blogspot.com/-WUhotKRBznc/YTtbFuTg6XI/AAAAAAAA-dE/GLfK33tzQbgGIfr71pJTqGQiKXd4pmp7ACLcBGAsYHQ/s16000/W3sDesign_State_Design_Pattern_UML.jpg)  
Fig. 6 : State (Behavioral) Design Pattern - Class and Sequence Diagram   
\[Source : Wikipedia\]  
The example attached is a very crude but helpful one, that of an OnlineShopping site. The limitation of the site being that at any given point only a single item can be purchased and processed. The various states during the purchase and processing are SelectionState, PurchaseState, AuthoriseState, AssembleState (optional) and DispatchState. Each of these states is processed and followed in a sequential manner.

OnlineShopping maintains an instance variable of each of these states and also a currentState variable. The various state methods that exist within OnlineShopping are selection(), purchase(), authorise(), assemble() and dispatch(). When client calls these methods, the actual invocations are performed on the state implementation held in the currentState variable. All state implementations implement the State interface, which specifies the lifecycle methods.  
ShoppingClient is the main class. Try adding your own states along with the required lifecycle method.

*** ** * ** ***

**\[About SKP's Core Java/Java EE Roots\]**

Series of articles on rooted concepts in Core Java and Java EE. They revolve around Memory Architecture, Connection \& Memory Leaks, Core Java Syntax \& Semantics, Java Object Layout/Anatomy, Multi-Threading, Asynchronous Task Execution, Design Patterns, Java Agents, Class Loading, API Design, OOPs \& SOLID.
