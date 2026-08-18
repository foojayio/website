---
title: "Create a CRUD UI in Pure Java"
date: "2022-02-02T17:10:06+00:00"
lastmod: "2023-03-08T10:03:12+00:00"
description: "Many developers struggle with writing a web UI, but it becomes absolute fun if we could do it with pure java; let's find out how."
authors:
  - "tarek-oraby"
image: "Screen-Shot-2022-02-02-at-10.49.28-AM.png"
categories:
  - "Tutorials"
  - "Vaadin"
tags:
related_posts:
  - "build-web-apps-in-pure-java-with-vaadin-flow"
  - "delegation-vs-inheritance-in-graphical-user-interfaces"
  - "new-book-practical-vaadin"
  - "securing-vaadin-applications-with-microsoft-entra"
frozen: false
---

In this guide, we create a web UI that performs full CRUD (create, read, update, and delete) operations on a Spring Data JPA backend. This UI is developed entirely in pure Java using [Vaadin Flow](https://vaadin.com/docs/latest/flow/guide/quick-start "Vaadin Flow") (no HTML or JavaScript involved). You can explore the full source code on [GitHub](https://github.com/tarekoraby/crud-ui-tutorial "GitHub").

### What You Need

* About 15 minutes
* JDK 17 or later

### Step 1: Import a Starter Project

[Click here to download an empty project starter](https://start.vaadin.com/dl?preset=no-views "Click here to download an empty project starter"). Unpack the downloaded zip into a folder on your computer, and import the project in the IDE of your choice.

The `pom.xml` of the starter project comes with all the dependencies necessary to complete this tutorial.

### Step 2: Create the Backend Classes

Let's start by creating a simple JPA entity and a Spring Data repository.

The following `Person` class is a simple JPA entity with three fields, representing a person's first name, last name, and email.

```java
package com.example.application;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Person {

   @NotBlank
   private String firstName;

   @NotBlank
   private String lastName;

   @Id
   @NotBlank
   @Email
   private String email;

   public String getEmail() {
       return email;
   }

   public void setEmail(String email) {
       this.email = email;
   }

   public String getFirstName() {
       return firstName;
   }

   public void setFirstName(String firstName) {
       this.firstName = firstName;
   }

   public String getLastName() {
       return lastName;
   }

   public void setLastName(String lastName) {
       this.lastName = lastName;
   }
}
```

We also create a repository interface using Spring Data in order to work with `Person` entities as follows:

```java
package com.example.application;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
}
```

In order to work with some data, we also populate the embedded H2 database with some random entries that are generated using the `ExampleDataGenerator` helper class. The following `DataGenerator` class generates 100 random entities.

```java
package com.example.application;

import com.vaadin.exampledata.DataType;
import com.vaadin.exampledata.ExampleDataGenerator;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringComponent
public class DataGenerator {
   @Bean
   public CommandLineRunner loadData(@Autowired PersonRepository personRepo) {
       return args -> {
           Logger logger = LoggerFactory.getLogger(getClass());

           logger.info("Generating demo data");

           logger.info("... generating 100 Sample Person entities...");
           ExampleDataGenerator<Person> samplePersonGenerator = new ExampleDataGenerator<>(
                   Person.class, LocalDateTime.now());
           samplePersonGenerator.setData(Person::setFirstName, DataType.FIRST_NAME);
           samplePersonGenerator.setData(Person::setLastName, DataType.LAST_NAME);
           samplePersonGenerator.setData(Person::setEmail, DataType.EMAIL);
           personRepo.saveAll(samplePersonGenerator.create(100, 123));

           logger.info("Generated demo data");
       };
   }
}
```

### Step 3: Create the Editor Layout

Next, we create the Editor Layout, which is the part of the UI that will enable users to manipulate the currently selected `Person` entity. We can do this in pure Java using Vaadin, which comes with an extensive [set of UI components](https://vaadin.com/docs/latest/ds/components "set of UI components") that we can use as the building blocks for any application.

The following `EditorLayout` class extends Vaadin `VerticalLayout`, and it includes three `TextField` components to hold the first name, last name, and email of the selected `Person` entity. `EditorLayout` also includes three `Button` components that will later be used to perform the CRUD operations. (Note, the save button is used to both create and update entities).

```java
package com.example.application;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class EditorLayout extends VerticalLayout {

   private TextField firstName;
   private TextField lastName;
   private TextField email;

   private Button deleteButton = new Button("Delete...");
   private Button cancelButton = new Button("Cancel");
   private Button saveButton = new Button("Save");

   public EditorLayout() {
       FormLayout formLayout = new FormLayout();

       firstName = new TextField("First Name");
       lastName = new TextField("Last Name");
       email = new TextField("Email");

       formLayout.add(firstName, lastName, email);

       HorizontalLayout buttonLayout = new HorizontalLayout();
       deleteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
       cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
       saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
       buttonLayout.setWidthFull();
       buttonLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
       buttonLayout.setSpacing(false);
       buttonLayout.add(deleteButton, new HorizontalLayout(cancelButton, saveButton));

       add(formLayout, buttonLayout);
       setWidth("600px");
       setMinWidth("300px");
       setFlexGrow(0);
   }

   public Button getDeleteButton() {
       return deleteButton;
   }

   public Button getCancelButton() {
       return cancelButton;
   }

   public Button getSaveButton() {
       return saveButton;
   }

}
```

### Step 4: Create the CRUD View

Now we define the main view that holds both the tabular data as well as the `EditorLayout` that was just created. This view extends Vaadin's `SplitLayout` component. This SplitLayout shows the tabular data inside a Vaadin `Grid` component on the left side of the screen, and the `EditorLayout` on its right side. When an item is selected from the Grid, the form is automatically updated.

Note that this view makes use of `BeanValidationBinder` in order to bind and validate the values of the `EditorLayout` form. Note also that the view is made accessible to the end user via the `@Route` annotation (in this case, it would be accessible via the empty \`\` route).

```java
package com.example.application;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("")
public class CrudView extends SplitLayout {

   private Grid<Person> grid;
   private final EditorLayout editorLayout;

   /**
    * The currently edited person
    */
   private Person person;

   ListDataProvider<Person> dataProvider;

   private final PersonRepository repo;

   private BeanValidationBinder<Person> binder;

   public CrudView(@Autowired PersonRepository repo) {
       this.repo = repo;

       configureGrid();

       editorLayout = new EditorLayout();
       configureBinding();

       setSizeFull();

       addToPrimary(grid);
       addToSecondary(editorLayout);
   }

   private void configureGrid() {
       // Auto create Grid's columns based on Person.class members
       grid = new Grid<>(Person.class, true);
       grid.setSizeFull();

       dataProvider = DataProvider.ofCollection(repo.findAll());
       grid.setDataProvider(dataProvider);

       // when a row is selected or deselected, populate form
       grid.asSingleSelect().addValueChangeListener(event -> {
           Person person = event.getValue();
           if (person != null) {
               populateForm(person);
           } else {
               clearForm();
           }
       });
   }

   private void configureBinding() {
       binder = new BeanValidationBinder<>(Person.class);

       //Bind member fields found in the EditorLayout object.
       binder.bindInstanceFields(editorLayout);

       editorLayout.getDeleteButton().addClickListener(e -> {
           if (this.person != null) {
               repo.delete(this.person);
               dataProvider.getItems().remove(person);

               clearForm();
               refreshGrid();
               Notification.show("Person deleted.");
           }
       });

       editorLayout.getCancelButton().addClickListener(e -> {
           clearForm();
           refreshGrid();
       });

       editorLayout.getSaveButton().addClickListener(e -> {
           try {
               if (this.person == null) {
                   this.person = new Person();
               }
               binder.writeBean(this.person);

               repo.save(this.person);
               dataProvider.getItems().add(person);

               clearForm();
               refreshGrid();
               Notification.show("Person details stored.");
           } catch (ValidationException validationException) {
               Notification.show("Please enter a valid person details.");
           }
       });
   }

   void clearForm() {
       populateForm(null);
   }

   void populateForm(Person person) {
       this.person = person;
       binder.readBean(this.person);
   }

   public void refreshGrid() {
       grid.select(null);
       dataProvider.refreshAll();
   }
}
```

### Step 5: Run the Application

To run the project from the command line, type `mvnw spring-boot:run` (on Windows), or `./mvnw spring-boot:run` (on macOS or Linux).

Then, in your browser, open <http://localhost:8080/>.

### Go further

Congratulations! You have created a web UI that performs full CRUD (create, read, update, and delete) operations on a Spring Data JPA backend. And you did it in pure Java -- without the need for any HTML or JavaScript, and without having to expose REST services or think about HTTP requests, responses, and filter chains.

You can explore the full source code on [GitHub](https://github.com/tarekoraby/crud-ui-tutorial "GitHub").

Visit [start.vaadin.com](https://start.vaadin.com/app/?preset=flow "start.vaadin.com") to customize your own Vaadin app starter, or learn more about Vaadin Flow in its [documentation](https://vaadin.com/docs/latest/flow/overview "documentation").
