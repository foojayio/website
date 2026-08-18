---
title: "Delegation vs. Inheritance in Graphical User Interfaces"
date: "2021-02-24T16:07:53+00:00"
lastmod: "2021-12-16T09:45:05+00:00"
description: "Find out what why Sven Ruppert from JFrog prefer delegation and why he wants to emphasize this rarely-used feature in Java!"
authors:
  - "sven-ruppert"
image: "Favicon-3-2.png"
categories:
  - "Vaadin"
  - "Videos"
related_posts:
frozen: false
---

In this article, we will look at the difference between inheritance and delegation concepts.

Or, to put it in another way, why I prefer delegation and why I want to emphasize this rarely-used feature in Java.

### The Challenge

The challenge we face today is quite common in the field of graphic user interfaces like desktop- or web-apps: Java is widely used as the development language for both worlds and it does not matter if we are in the classic fields of Swing or JavaFX or in the field of web frameworks like Vaadin. Explicitly, I've opted for a pseudo-class model in core Java, as I'd like to look at the design patterns here without any technical details.

The goal is to create a custom component that consists of a text input field and a button. Both elements should be displayed next to each other, i.e., in a horizontal layout. The respective components have no function in this example. Here I want to exclusively work towards the differences between inheritance and delegation.

If you are too lazy to read... check out my YouTube version of this article here:

{{< youtube xgViS-wlH7Q >}}

### The Base Class Model

Mostly, there are the respective essential components in a framework. In our case, it is a TextField, a button, and a horizontal or vertical layout. However, all of these components are embedded in an inheritance structure. In our case, I chose the following construction. Each component corresponds to the Component interface, for which there is an abstract implementation called AbstractComponent.

The class AbstractComponent contains framework-specific and technologically-based implementations. The Button, as well as the TextField, extend the class AbstractComponent. Layouts are usually separate and, therefore, a specialized group of components that leads in our case to an abstract class named Layout, which inherits from the class AbstractComponent.

In this abstract class, there are layout-specific implementations that are the same for all sorts of layouts. The implementations HorizontalLayout and VerticalLayout based on this. Altogether, this is already a quite complex initial model.

### Inheritance --- First Version

In the first version, I show a solution that I have often seen in projects. As a basis for a custom component, a base component from the framework is used as a parent. The direct inheritance from a layout is often used to structure all other internally child components on the screen. Inside the constructor, the internally required elements are generated and added to the inherited layout structure.

```java
public class InputComponent 
  extends Horizontal Layout // Layout is abstract 
  implements HasLogger { 
  private button button = new Button (); 
  private TextField textField = new TextField (); 
  public InputComponent () { 
    addComponent(text field); 
    addComponent (button); 
  } 
  public void click () { 
    button.click (); 
  } 
  public void setText (String text) { 
    textField.setText (text); 
  } 
  public String getText () { 
    return textField.getText (); 
  } 
}
```

If you now look at how the component will behave during later use, it becomes visible that a derivation from a fundamental component brings its pitfalls with it.

What exactly happened here? If an instance of the custom component InputComponent is now used, it can be viewed as a layout. But that is not the case here anymore; on the contrary, it is even wrong. All methods inherited from the layout implementation are also public available with this component. But you wanted to achieve something else. First of all, we wanted to reuse the existing code, provided in the component implementation HorizontalLayout.

On the other hand, you want a component that externally delegates only the methods for the necessary interaction, needed for the custom behaviour. In this case, the public methods from the Button and the TextField used symbolically. Besides, this component is tied to visual design that leads to possible interactions that are not part of the domain-specific behaviour of this component. This technical debt should be avoided as much as possible.

In practical words, general methods from the implementation of the HorizontalLayout are made visible to the outside. If somebody uses exactly these methods, and later on the parent becomes a VerticalLayout, the source code can not compile without further corrections.

```java
public class MainM01 
  implements HasLogger { 
  public static void main (String [] args) { 
    var inputComponent = new InputComponent (); 
    inputComponent.setText ("Hello Text M01"); 
    inputComponent.click (); 
    // critical things 
    inputComponent.doSomethingLayoutSpecific (); 
    inputComponent.horizontalSpecific (); 
    inputComponent.doFrameworkSpecificThings (); 
  } 
}
```

### Inheritance --- Second Version

The custom component has to fit into the already existing component hierarchy from the framework. A place must be found inside the inheritance to start from; otherwise, the custom component cannot be used. But at the same time, we do not want to own specific implementation details, and neither the effort to implement basic technical requirements based on the framework needs. The point from which you split up the inheritance must be used wisely.

Please assume that the class AbstractComponent is what we are looking for as a start point.  

If you derive your class from it, so you certainly have the essential features that you would like to have as a user of the framework. However, this abstraction mostly associated with the fact that also framework-specific things are to be considered. This abstract class is an internally used, fundamental element. Starting with this internal abstract class very likely leads to the need to implement internal and technical related methods. As an example, the method signature with the name doFrameworkSpecificThings() has been created and implemented with just a log message.

```java
public class InputComponent 
  extends AbstractComponent 
  implements HasLogger { 
  private button button = new Button(); 
  private TextField textField = new TextField(); 
  public InputComponent() { 
    var layout = new HorizontalLayout(); 
    layout.addComponent(text field); 
    layout.addComponent(button); 
    addComponent(layout); 
  } 
  public void click () { 
    button.click (); 
  } 
  public void setText (String text) { 
    textField.setText (text); 
  } 
  public String getText () { 
    return textField.getText (); 
  } 
  // to deep into the framework for EndUser 
  public void doFrameworkSpecificThings () { 
    logger().info ("doFrameworkSpecificThings -" 
                   + this.getClass ().getSimpleName ()); 
  } 
}
```

In use, such a component is already a little less dangerous. Only the internal methods that are visible on other components are accessible on this component.

```java
public class MainM02 
  implements HasLogger { 
  public static void main (String [] args) { 
    var inputComponent = new InputComponent (); 
    inputComponent.setText ("Hello Text M02"); 
    inputComponent.click (); 
    // critical things 
    inputComponent.doFrameworkSpecificThings (); 
  } 
}
```

But I am not happy with this solution yet. Very often, there is no requirement for new components on the technical side. Instead, they are compositions of already existing essential elements, composed in a professional, domain-specific context.

### Composition --- My Favorite

So what can you do at this point? The beautiful thing about the solution is that you can use it to put a wrapper around already existing components, which have been generated by inheritance.

One solution may be to create a composite of type T. `Composite<T extends AbstractComponent>` This class serves as an envelope for the compositions of the required components. This class can then even itself inherit from the interface Component, so those technical methods of the abstract implementation not repeated or released to the outside. The type T itself is the type to be used as the external component that holds in the composition.

In our case, it is the horizontal layout. With the method getComponent(), you can access this instance if necessary.

```java
public final class InputComponent     
  extends Composite<HorizontalLayout>     
  implements HasLogger {   
  private button button = new Button ();   
  private TextField textField = new TextField ();   
  public InputComponent () {     
    super (new Horizontal Layout ());     
      getComponent().addComponent(text field)
      getComponent().addComponent(button)   
  }   
  public void click () {     
    button.click ();   
  }   
  public void setText (String text) {     
    textField.setText (text);   
  }   
  public String getText () {     
    return textField.getText ();   
  } 
}
```

Seen in this way, it is a neutral shell, but it will behave towards the outside as a minimal component since the minimum contract via the Component interface. Again, only the methods by delegation to the outside are made visible, which explicitly provided. Use is, therefore, harmless.

```java
public class MainSolution { 
  public static void main (String [] args) { 
    var inputComponent = new InputComponent (); 
    inputComponent.setText ("Hello Text M03"); 
    inputComponent.click (); 
  } 
}
```

### Targeted Inheritance

Let's conclude with what I believe is rarely used Java feature at the class level.

To prevent an unintentional derivation, I recommend the targeted use of final classes. Thus, from this point, the unfavourable inheritance on the composition level is troublesome. Understandably, in most frameworks, no use of it was made. After all, you want to allow the user of the component Button to offer a specialized version. But at the beginning of your abstraction level, you can very well use it.

### Conclusion

At this point, we have seen how you can achieve a more robust variant of a composition by delegation rather than inheritance.

You can also use this if you are confronted with legacy source codes with this anti-pattern.

It's not always possible to clean up everything or change it to the last detail.

But I hope this has given an incentive to approach this situation.

Happy coding!

{{< youtube xgViS-wlH7Q >}}
