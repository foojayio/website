---
title: "Build Web Apps in Pure Java with Vaadin Flow"
slug: "build-web-apps-in-pure-java-with-vaadin-flow"
date: "2021-12-16T09:21:24+00:00"
lastmod: "2022-04-04T10:58:54+00:00"
description: "In this guide, learn how to build a small but fully functional ToDo application in pure Java using Vaadin Flow."
authors:
  - "tarek-oraby"
image: "VaadinLogo_RGB_500x155.png"
categories:
  - "Tutorials"
  - "Vaadin"
tags:
related_posts:
  - "new-book-practical-vaadin"
  - "vaadin-22-released-with-quarkus-support-and-stateless-fusion"
  - "delegation-vs-inheritance-in-graphical-user-interfaces"
frozen: false
---

Vaadin Flow enables you to quickly build web apps in *pure Java*, without writing any HTML or JavaScript.

In this guide, you learn how to build a small but fully functional ToDo application using Vaadin Flow.

### What You Need

* About 5 minutes
* JDK 11 or higher (For example, [Eclipse Temurin JDK](https://adoptium.net/ "Eclipse Temurin JDK")).

### Step 1: Download a Vaadin Project

[Click here to download an empty project starter](https://start.vaadin.com/dl?preset=flow-quickstart-tutorial "here"). Unpack the downloaded zip into a folder on your computer, and import the project in the IDE of your choice.

### Step 2: Add Your Code

Open `src/main/java/com/example/application/views/main/MainView.java`. Replace the code in `MainView.java` with the code below:

```java
package com.example.application.views.main;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("") // (1)
public class MainView extends VerticalLayout { // (2)

  public MainView() {
    VerticalLayout todosList = new VerticalLayout(); // (3)
    TextField taskField = new TextField(); // (4)
    Button addButton = new Button("Add"); // (5)
    addButton.addClickListener(click -> { // (6)
      Checkbox checkbox = new Checkbox(taskField.getValue());
      todosList.add(checkbox);
    });
    addButton.addClickShortcut(Key.ENTER); // (7)

    add( // (8)
      new H1("Vaadin Todo"),
      todosList,
      new HorizontalLayout(
        taskField,
        addButton
      )
    );
  }
}
```

(1) This `@Route` annotation makes the view accessible to the end user, in this case, using the empty \`\` route.  

(2) As the the `MainView` class extends `VerticalLayout`, components added to it will be ordered vertically.  

(2) `todosList` is a vertical layout that displays a list of the tasks along with checkboxes.  

(4) `taskField` is a text input field to enter the description of new tasks.  

(5) `addButton` is a button for adding a new task.  

(6) In the listener for the button click, first create a new checkbox with the value from the `taskField` as its label, then add the checkbox to the `todosList`.  

(7) Add a shortcut for the `addButton` component when the `Enter` key is pressed.  

(8) Call `add()` on the `VerticalLayout` to display the components vertically.  

Notice that `taskField` and `addButton` are in a `HorizontalLayout`, which puts them next to each other.

### Step 3: Run the Application

To run the project in your IDE, launch `Application.java`, which is located under `src/main/java/org/vaadin/example`.

Alternatively, you can run the project from the command line by typing `mvnw` (on Windows), or `./mvnw` (on macOS or Linux).

Then, in your browser, open [http://localhost:8080](http://localhost:8080 "http://localhost:8080") and you should see the following:

![Animation of Vaadin Todo app](https://vaadin.com/docs/latest/static/54a9ced3610b3825d3c8b32e3c7e95dd/completed-app.gif "Animation of Vaadin Todo app")

## Go further

Now that you have a taste of how Vaadin Flow empowers you to quickly build web apps in pure Java, without writing any HTML or JavaScript.

Visit [start.vaadin.com](https://start.vaadin.com/app/ "start.vaadin.com") to customize your own Vaadin app starter, or learn more about Vaadin Flow in its [documentation](https://vaadin.com/docs/latest/flow/overview "documentation").

Source code of the todo project is [available on GitHub](https://github.com/vaadin-learning-center/vaadin-todo "available on GitHub").
