---
title: "How to style a Vaadin Application"
slug: "how-to-style-a-vaadin-application"
date: "2022-04-05T07:41:12+00:00"
lastmod: "2022-04-05T08:00:28+00:00"
description: "The basics of styling a Vaadin application using CSS: custom themes, override Lumo values, and selectively style views and components."
authors:
  - "tarek-oraby"
image: "ij7493vc0zx9vxhoza3p.png"
categories:
  - "Vaadin"
tags:
related_posts:
  - "blink-a-led-on-raspberry-pi-with-vaadin"
  - "build-web-apps-in-pure-java-with-vaadin-flow"
  - "create-a-crud-ui-in-pure-java"
enlighterjs: true
frozen: false
---

In this guide, we learn the basics of styling a Vaadin application using Cascading Style Sheets (CSS).

While it is possible to style some parts of a Vaadin application using the Java API, this approach tends to be rather limited for mid- to large-sized applications.

So, it is recommended to do the styling of a Vaadin application by adding CSS inside standard style sheets. This guide focuses only on the recommended style-sheets approach.

Create a custom theme {#h2-0-create-a-custom-theme}
---------------------------------------------------

To start, we need to inform Vaadin about the location in which we will place our CSS. The easiest way to do this is to add the `@Theme` annotation on a class that implements `AppShellConfigurator`.

For example, in a Spring Boot application, you could have an application configuration class such as the following:

<pre class="EnlighterJSRAW" data-enlighter-language="java">@Theme("myapp")
public class Application extends SpringBootServletInitializer
                         implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}</pre>

By default, projects downloaded from [start.vaadin.com](https://start.vaadin.com/) come with the `@Theme` annotation added to the `Application` class.

Where to place your CSS {#h2-1-where-to-place-your-css}
-------------------------------------------------------

Custom CSS should be placed inside a specific folder located under the `frontend/themes` directory in your project. This directory has something like the following minimal structure:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">frontend
└── themes              
    └── myapp        
        ├── components/ 
        └── styles.css</pre>

**NOTE:** the `myapp` sub-folder might be named differently in your case. This folder should have the same name as the one used for the custom theme using the `@Theme` annotation.

Custom CSS should be placed in one of the following locations:

1. Inside the `styles.css` file
2. As a separate CSS file, which is imported from within `styles.css`
3. Under the `components/` directory in order to style the (local CSS) internals of Vaadin components (such as a Vaadin Button)

The first and simplest option is to place the custom CSS directly inside the `styles.css` file. For example, adding the following snippet to `styles.css` would change the font family and size of the whole Vaadin application.

<pre class="EnlighterJSRAW" data-enlighter-language="css">* {
    font-family: "Lucida Console", "Courier New", monospace;
    font-size: 1rem;
}</pre>

However, as the number of custom stylings increases, it becomes convenient to organize the styles into separate `.css` files. In order to do this, simply create a new CSS file and import that from within `styles.css`.

For example, if we create a new CSS file called `main-view.css` at `frontend/themes/myapp/views/main-view.css`, then we would import this file by adding the following line to `styles.css`:

<pre class="EnlighterJSRAW" data-enlighter-language="css">@import url('./views/main-view.css');
</pre>

Further below, we will discuss the scenarios in which we should add our CSS to the `components/` directory.

How to override Lumo values {#h2-2-how-to-override-lumo-values}
---------------------------------------------------------------

The default look and feel of a Vaadin application is based on a built-in theme, called Lumo. This theme is essentially a bunch of CSS custom properties (such as colors, typography, and sizes) that each has a CSS variable assigned to it.

One easy way to customize the look and feel of our application requires us to override the default values of the Lumo variables.

For example, suppose our application has a bunch of TextField, ComboBox, DatePicker, and Button components. By default, they will look as follows:

![A Vaadin TextField, ComboBox, DatePicker, and Button with default look and feel.](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/gle8hdd9f89lwqus4mb8.png)

Suppose that we want to increase the roundedness of their corners. By default, the Lumo theme provides these components with a small rounded corner whose value is defined in the `--lumo-border-radius-m` variable.

In order to increase the roundedness of the corners, we can override the default value of this variable. Specifically, we can add the following inside the `styles.css` file in order to increase the default roundedness value:

<pre class="EnlighterJSRAW" data-enlighter-language="css">html {
    --lumo-border-radius-m: 1em;
}</pre>

This style will increase the corner roundedness of many components at once, so that they will look similar to the following screenshot:

![A Vaadin TextField, ComboBox, DatePicker, and Button with increased corner roundedness.](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/jlqwo3qrr1gac3s94i6g.png)

But what if one wants to override the defaults for only a subset of components? No problem; simply use the name of the components as the CSS selector.

For example, if one wants to override the rounded corner defaults for only the TextField and ComboBox components, then the following should be added inside the `styles.css` file:

<pre class="EnlighterJSRAW" data-enlighter-language="css">vaadin-text-field, vaadin-combo-box {
    --lumo-border-radius-m: 1em;
}</pre>

This will change the defaults for the TextField and ComboBox only, leaving other components, such as the DatePicker and Button, with their default values.

![A Vaadin TextField, ComboBox with increased corner roundedness. A Vaadin DatePicker, and Button with default look and feel.](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/lrjca5ijn3rdfa2szh66.png)

How to selectively style views and components {#h2-3-how-to-selectively-style-views-and-components}
---------------------------------------------------------------------------------------------------

So far, we have been discussing ways to style all components belonging to a certain kind in the same way.

For example, if we want to set the width of all Button components within our application, we could add something like the following to the `styles.css` file:

<pre class="EnlighterJSRAW" data-enlighter-language="css">vaadin-button {
    width: 140px;
}</pre>

However, we often want to style the same kind of component differently, depending on its function within our application. For example, we might want to style the Button component differently in our application, depending on whether it is a "Signup" button or a "Delete account!" button.

The easiest way to style views and components selectively is by providing them with a CSS class name from the Java API. All Vaadin components and views have an `addClassNames()` method that can be used for this purpose.

To illustrate, the following view makes use of the `addClassNames()` method to assign a CSS class name to the view itself, as well as to the components that it includes.

<pre class="EnlighterJSRAW" data-enlighter-language="java">import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
public class MyView extends VerticalLayout {

    public MyView() {
        addClassNames("my-view"); //CSS classname for the whole view

        H1 heading = new H1("My View Title");
        Paragraph text = new Paragraph("Thanks for shopping with us. Click the button to submit your order.");

        Button submitButton = new Button("Submit");
        submitButton.addClassNames("submit-button", "wide-button"); //CSS classnames for the submit button

        Button cancelButton = new Button("Cancel");
        cancelButton.addClassNames("cancel-button", "wide-button"); //CSS classnames for the cancel button

        add(heading, text, submitButton, cancelButton);

    }
}</pre>

These CSS class names can then be used to selectively style this view and its components.

For example, adding the following to `styles.css` demonstrates how selective styling can be achieved.

<pre class="EnlighterJSRAW" data-enlighter-language="css">.my-view  {
    padding: 1em;
}

.my-view h1 {
    /*Only applies to the h1 headers inside my-view*/
    margin-top: 0;
}

p {
    /*Applies to all p elements regardless of whether they are
     located inside my-view*/
    font-size: 1.2em;
}

.submit-button {
    color: green;
}

.cancel-button {
    color: red;
}

.wide-button {
    width: 120px;
}</pre>

Now after the selective styling is applied, `MyView` will look as follows:

![MyView after selective styling](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/ij7493vc0zx9vxhoza3p.png)

When to add CSS under the `/components` directory {#h2-4-when-to-add-css-under-the-components-directory}
--------------------------------------------------------------------------------------------------------

Customizing styling using the two aforementioned options (namely by placing styles inside `styles.css`, or inside a separate file imported from within `styles.css`) should work for views and layouts created within the project. However, these two options are not guaranteed to work if one aims to style the internals of a Vaadin component.

Custom styling of the internals of Vaadin components, such as the Grid or ComboBox components, needs to be placed under the `frontend/themes/myapp/components/` directory. (Note again that the `myapp` folder name might be different in your case.)

For example, let's assume we want to increase the size of the toggle icon that opens the dropdown menu of a ComboBox.

![ComboBox with an arrow pointing at toggle](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/4i8koa51nx4fol9sko1i.png)

If we inspect this toggle in Chrome (right-click on the toggle and select the **Inspect** option), we will see that it has an attribute called `part` whose value is equal to `toggle-button`.

![A screenshot of a div Element in the DOM with a highlighted part attribute whose value is equal to toggle-button](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/ta1xltpw3emxd76521cu.png)

To style this part, we need to create a file called `vaadin-combo-box.css` and place it under the `frontend/themes/myapp/components/` directory. In this file, we can do something like the following to increase the size of the ComboBox toggle.

<pre class="EnlighterJSRAW" data-enlighter-language="css">[part="toggle-button"] {
    font-size: 2em;
}</pre>

Note that the files placed under the `/components` directory have to exactly match the Vaadin component names as they appear in the DOM. For example, the ComboBox component appears in the DOM as `vaadin-combo-box`.

Accordingly, the styling file of the ComboBox has to be named exactly `vaadin-combo-box.css`.

How to selectively style the internals of a Vaadin component {#h2-5-how-to-selectively-style-the-internals-of-a-vaadin-component}
---------------------------------------------------------------------------------------------------------------------------------

We just saw how to style the internals of a Vaadin component by using the `part` property and placing the CSS inside a specific file placed under the `frontend/themes/myapp/components/` directory. Specifically, we saw how to style the ComboBox toggle using the `[part="toggle-button"]` selector and placing the CSS in a file called `vaadin-combo-box.css` that resides under the `frontend/themes/myapp/components/` directory.

However, this approach will style all toggle elements of all the ComboBox components in our application in the same way. What if we want our styling to apply to only some toggle elements but not to others?

Here we can follow the same approach as we did above to selectively style views and components. That is, we can provide the component with a CSS class name from the Java API using the `addClassNames()` method. Then we can use this CSS class name to selectively style the internal part of the component.

For example, suppose that our application has the following two ComboBox components:

<pre class="EnlighterJSRAW" data-enlighter-language="java">ComboBox&lt;String&gt; firstCombo = new ComboBox&lt;&gt;();
comboBox.setItems("Earth", "Mars");

ComboBox&lt;String&gt; secondCombo = new ComboBox&lt;&gt;();
comboBox.setItems("Mercury", "Venus");</pre>

If we want to style the toggle part of these two components differently, we can do this by providing each ComboBox with a different classname as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="java">firstCombo.addClassNames("first-combo");
secondCombo.addClassNames("second-combo");</pre>

Then, in `frontend/themes/myapp/components/vaadin-combo-box.css`, we can selectively style the toggle element of the two components differently by doing something like:

<pre class="EnlighterJSRAW" data-enlighter-language="java">:host(.first-combo) [part="toggle-button"] {
    font-size: 2em;
}

:host(.second-combo) [part="toggle-button"] {
    font-size: 1em;
}</pre>

Notice that we have to use here the `:host()` shadow function because the `toggle-button` part is located inside the [shadow DOM](https://developer.mozilla.org/en-US/docs/Web/Web_Components/Using_shadow_DOM) of the ComboBox.

How to selectively style a sub-component of a Vaadin component {#h2-6-how-to-selectively-style-a-sub-component-of-a-vaadin-component}
-------------------------------------------------------------------------------------------------------------------------------------

There are cases in which we cannot use CSS classnames to do the selective styling. Specifically, we cannot rely on CSS classnames when we want to style the sub-components of a Vaadin component.

What do I mean by a sub-component? Some Vaadin components create sub-components that exist in the DOM independently from the Vaadin component itself. This is noticeably the case for the Vaadin components, such as the DatePicker, Dialog, and ComboBox, that open an overlay during user interaction.

For example, a ComboBox adds a `vaadin-combo-box-overlay` element in the DOM when the user clicks on the toggle button to see the list of the available options. This `vaadin-combo-box-overlay` element exists in the DOM independently from the `vaadin-combo-box` element itself.

The fact that they exist in the DOM independently from their parent component means that a CSS classname that is given to the parent element is not propagated down to the sub-component. For example, if we give a ComboBox a classname from the Java API, only the `vaadin-combo-box` element will get this classname in the DOM, not the `vaadin-combo-box-overlay` element.

Instead of classnames, we can rely on the `theme` attribute as a selector in order to selectively style a sub-component of a Vaadin component. Unlike the classnames, the `theme` attribute has the benefit of propagating through to the sub-components.

We can provide a component with a theme attribute from the Java API using the `addThemeName()` method.

For example, like before, suppose that our application has the following two ComboBox components:

<pre class="EnlighterJSRAW" data-enlighter-language="java">ComboBox&lt;String&gt; firstCombo = new ComboBox&lt;&gt;();
firstCombo.setItems("Earth", "Mars");

ComboBox&lt;String&gt; secondCombo = new ComboBox&lt;&gt;();
secondCombo.setItems("Mercury", "Venus");</pre>

Suppose that we want to change the background color of the drop-down list of items that the user sees when they open the ComboBox.

This background color is controlled by the `background-color` property of the `overlay` part of the `vaadin-combo-box-overlay` element. This `overlay` part in the DOM is highlighted in the following screenshot.

![Vaadin combo-box-overlay element in the DOM with the overlay part highlighted](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/50ek9lzuxks0rvt39y6n.png)

To selectively style the background of the overlay of the two ComboBox components, we can first give each one of them a theme name using the Java API as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="java">firstCombo.addThemeName("first-combo");
secondCombo.addThemeName("second-combo");</pre>

Then, we need to create a new file called `vaadin-combo-box-overlay.css` and place it under the `frontend/themes/myapp/components` directory.

In this file, we can selectively style the overlay part of the `vaadin-combo-box-overlay` element of the two ComboBoxes by doing something like:

<pre class="EnlighterJSRAW" data-enlighter-language="java">:host([theme="first-combo"]) [part="overlay"] {
    background-color: rosybrown;
}

:host([theme="second-combo"]) [part="overlay"] {
    background-color: aquamarine;
}</pre>

Here again we are using the `:host()` shadow function because the overlay part is located inside the shadow DOM of the `vaadin-combo-box-overlay` element.

Conclusion {#h2-7-conclusion}
-----------------------------

In this guide, we learned the basics of styling a Vaadin application using CSS.

Be sure to check the official Vaadin [documentation](https://vaadin.com/docs/latest/ds/overview) for more advanced use cases.
