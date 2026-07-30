---
title: "Wordish with JavaFX - Part 2 | Foojay.io Today"
slug: "wordish-with-javafx-part-2"
date: "2022-05-24T07:50:54+00:00"
lastmod: "2022-05-24T07:50:55+00:00"
description: "Learn specialized JavaFX Labels and Buttons, pseudo-classes for CSS styling, and third-party font libraries and customizing Scene Builder!"
authors:
  - "gail-anderson"
image: "https://foojay.io/wp-content/uploads/2022/05/playgame5.png"
categories:
  - "Game Development"
  - "Gluon"
  - "Java Core"
  - "JavaFX"
  - "Maven"
  - "Tutorials"
tags:
related_posts:
  - "wordish-with-javafx-part-1"
  - "beginning-javafx-with-intellij"
  - "creating-a-javafx-world-clock-from-scratch-part-1"
  - "the-javafx-revival"
enlighterjs: true
frozen: false
---

Building Games and Having Fun with Java and JavaFX {#h2-0-building-games-and-having-fun-with-java-and-javafx}
-------------------------------------------------------------------------------------------------------------

Welcome to Part 2 of this five part series!

In [Part 1](https://foojay.io/today/wordish-with-javafx-part-1/), we introduced the Wordish game with JavaFX and discussed the main UI layout.

In this second part, we'll discuss some important look and feel enhancements.

We'll show you specialized Label and Button controls and using pseudo-classes for advanced CSS styling.

We'll also cover incorporating third-party font libraries and customizing Scene Builder to leverage these features.

Here's an example screenshot of Wordish.

<figure class="wp-block-image size-large is-resized">
 <img fetchpriority="high" decoding="async" src="/images/posts/2022/05/wordish-with-javafx-part-2/playgame5-619x1024.png" alt="Wordish Game in progress" class="wp-image-56002" width="350" height="580">
 <figcaption>
  Figure 1. Wordish Main View Layout
 </figcaption>
</figure>

You can access the code on github here: <https://github.com/gailasgteach/Wordish>.

Part 2: Look and Feel Enhancements {#h2-1-part-2-look-and-feel-enhancements}
----------------------------------------------------------------------------

Wordish uses a specialized Label control that reflects the user's guess compared to the target word. Similarly, the virtual keyboard Button control must also reflect the matching status of its specific key.

Let's examine how to provide specialized Label and Button controls that hold meaningful state in our application. We'll extend the standard controls and add JavaFX properties.

By leveraging these properties with CSS pseudo-classes, you'll see how to keep the UI synchronized with the model when updating the controls' properties.

Special icons add flair to an application, so we picked a few appropriate icons from the iKonli Font Library. We show you how to incorporate such third-party icons in JavaFX applications.

It's straightforward to add our specialized controls as well as third-party libraries to [Scene Builder](https://gluonhq.com/products/scene-builder/). This means you can continue to use Scene Builder visualizations, even when your controls and fonts are not part of the standard JavaFX library.

Finally, we show you some of the CSS customizations we use to help keep our UI looking Wordishly.

### Specialized Label Control {#h3-2-specialized-label-control}

For the Labels in TilePane and the Buttons in FlowPane, we add state to these controls to reflect the matching status of its letter as the user plays Wordish.

For the Label, here's class LetterLabel with two added JavaFX properties:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class LetterLabel extends Label {
    private final ObjectProperty&lt;DisplayType&gt; matchResult = new 
         SimpleObjectProperty&lt;&gt;(DISPLAYING);
    private final ObjectProperty&lt;DisplayType&gt; letterDisplay = new
         SimpleObjectProperty&lt;&gt;(PLAIN);
    . . .
}</pre>

Property `matchResult` holds the status of that letter's match against the target word: exact match, partial match, or no-match. Since we don't reveal match results until after animating the tiles, a second property, `letterDisplay`, determines the CSS styling of the LetterLabel. `DisplayType` is an enum that reflects one of the five possible states of a LetterLabel.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class LetterStyle {
     . . .
    public enum DisplayType {
            PLAIN, DISPLAYING, NOMATCH, PARTIALMATCH, MATCHING;
    }  
}</pre>

Figure 2 illustrates all five `DisplayType` enum states: The three lower rows of LetterLabel in the TilePane are styled with DisplayType `PLAIN`. The word "BLEAK" is styled with DisplayType `DISPLAYING`. This state applies when the user is entering the word before submitting it. The words "STONE" and "FLAIR" illustrate the other three DisplayTypes applied after the matching process is complete: the L in FLAIR is `MATCHING`, the E in STONE and the A in FLAIR is `PARTIALMATCH`, and the remaining LetterLabels are DisplayType `NOMATCH`.

<figure class="wp-block-image size-large is-resized" id="figure-2">
 <img decoding="async" src="/images/posts/2022/05/wordish-with-javafx-part-2/playgame4-619x1024.png" alt="Wordish UI shoing customized LetterLabel and KeyButton controls" class="wp-image-56018" width="350" height="579">
 <figcaption>
  Figure 2. Customized LetterLabel and KeyButton controls
 </figcaption>
</figure>

### Specialized Button Control {#h3-3-specialized-button-control}

Similar to LetterLabel, the virtual keyboard is made up of enhanced Button controls called KeyButton. Here, we add a single JavaFX property to reflect the status of that keyboard letter in the current game.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class KeyButton extends Button {
    private final ObjectProperty&lt;DisplayType&gt; letterDisplay = new
         SimpleObjectProperty&lt;DisplayType&gt;(PLAIN);
    . . .
}</pre>

As [Figure 2](#figure-2) illustrates, key L is `MATCHING`, keys E and A are `PARTIALMATCH`, and keys R, T, I, O, S, F, and N are `NOMATCH`. The remaining keys are `PLAIN`.

When you submit a word, the controller code updates the LetterLabel and KeyButton states. In the following code snippet, we invoke the setter for property `matchResult`, which specifies that the letter in the LetterLabel is matching.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// ll is LetterLabel, **pseudo code here**
// Do we have a match?
if (isMatching(ll.getText())) {
   ll.setMatchResult(MATCHING);
}</pre>

After processing the submitted word, we update the LetterLabel's `letterDisplay` property with the value from its `matchResult` property.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// update the LetterLabel’s letterDisplay property
ll.setLetterDisplay(ll.getMatchResult());</pre>

Next, we synchronize the associated key in KeyButton by updating its `letterDisplay` property. Here, `list` is the row of LetterLabel controls for this play and `keyLetters` is a Map. Note that we use the Map's `key` to identify the KeyButton control for LetterLabel's letter.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">list.stream().forEach(ll -&gt; keyLetters.get(ll.getText())
            .setLetterDisplay(ll.getMatchResult()));</pre>

Importantly, by calling the JavaFX property setter `setLetterDisplay() `for both LetterLabel and KeyButton, the UI styles automatically update to reflect the matching state. To see how this works, let's discuss CSS pseudo-classes with the well-known Button control.

### CSS Pseudo-Classes {#h3-4-css-pseudo-classes}

Many JavaFX controls, including Button, have a JavaFX property called `disableProperty`. To disable button `myButton`, for example, use

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">myButton.setDisable(true);    // disable myButton</pre>

When this code executes, the UI reflects the disabled state by reducing the opacity of the control. *Note that we don't update the styling ourselves.* JavaFX implements this styling behavior with CSS pseudo-classes, where a change in a property triggers a change in the node's CSS styling.

We want this same behavior for both LetterLabel and KeyButton, where updates to the `letterDisplay` property automatically update the UI.

For this to work, we have to build CSS scaffolding in our classes. Let's return to our LetterStyle class where we define the `DisplayType` enum. Here, we associate each `DisplayType` enum with a pseudo-class that names a CSS state.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import javafx.css.PseudoClass;

public class LetterStyle {
    public static final PseudoClass PLAIN_PSEUDO_CLASS = 
          PseudoClass.getPseudoClass("plain");
    public static final PseudoClass DISPLAYING_PSEUDO_CLASS = 
          PseudoClass.getPseudoClass("displaying");
    public static final PseudoClass NOMATCH_PSEUDO_CLASS = 
          PseudoClass.getPseudoClass("nomatch");
    public static final PseudoClass PARTIALMATCH_PSEUDO_CLASS = 
          PseudoClass.getPseudoClass("partialmatch");
    public static final PseudoClass MATCHING_PSEUDO_CLASS = 
          PseudoClass.getPseudoClass("matching");

    public enum DisplayType {
            PLAIN, DISPLAYING, NOMATCH, PARTIALMATCH, MATCHING;
    }   
}</pre>

Let's examine the pseudo-class returned by `PseudoClass.getPseudoClass("matching")`. Here, the styling is identified in our CSS file and depends on the node. For LetterLabel control in a TilePane, for instance, style class `.matching-letter` in state `matching` has the following style.

<pre class="EnlighterJSRAW" data-enlighter-language="css" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">.tile &gt; .matching-letter:matching {
    -fx-outer-border: -fx-match-color;
    -fx-body-color: -fx-match-color;
    -fx-text-fill: -fx-text-fill-alt-color;
}</pre>

And for a KeyButton control in a FlowPane, style class `.matching-letter` in state `matching` is defined as follows.

<pre class="EnlighterJSRAW" data-enlighter-language="css" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">.flow &gt; .matching-letter:matching {
    -fx-button-base-color: -fx-match-color;
    -fx-text-fill: -fx-text-fill-alt-color;
}</pre>

**Note** : Colors `-fx-match-color` and **-**`fx-text-fill-alt-color` are defined elsewhere in the CSS file.

There are similar style classes defined for states `plain`, `displaying`, `partialmatch`, and `nomatch` for controls in TilePane. We also have classes defined for states `plain`, `partialmatch`, and `nomatch` with controls in FlowPane. We assign these style classes to our customized controls in the LetterLabel and KeyButton constructors with the following (for example).

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">getStyleClass().add("matching-letter");</pre>

### Reacting to Property Updates {#h3-5-reacting-to-property-updates}

The final piece of scaffolding code is to react when the KeyButton and LetterLabel `letterDisplay` property updates. Since JavaFX properties are observable, we add this code to the property's `invalidated()` method. This method is invoked each time you call the property setter.

Here is the `letterDisplay` property for LetterLabel. We leverage the Java 14 switch syntax, which makes this code more streamlined than the pre-Java 14 switch statement. The KeyButton `letterDisplay` property is similar, but with style `"displaying-letter"` not needed.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class LetterLabel extends Label {
    public LetterLabel() {
          super();
        // assign these style classes to our customized controls
          getStyleClass().add("displaying-letter");
          getStyleClass().add("matching-letter");
          getStyleClass().add("nomatch-letter");
          getStyleClass().add("partialmatch-letter");
          getStyleClass().add("plain-letter");
    }

    private final ObjectProperty&lt;DisplayType&gt; letterDisplay = new 
          SimpleObjectProperty&lt;&gt;(PLAIN) {
        @Override
        public void invalidated() {
            // Turn off all custom PseudoClasses in the LetterLabel,
            // then uses a switch statement to set a
            // specific PseudoClass that depends on the value
            // in the letterDisplay property
            pseudoClassStateChanged(PLAIN_PSEUDO_CLASS, false);
            pseudoClassStateChanged(DISPLAYING_PSEUDO_CLASS, false);
            pseudoClassStateChanged(NOMATCH_PSEUDO_CLASS, false);
            pseudoClassStateChanged(PARTIALMATCH_PSEUDO_CLASS, false);
            pseudoClassStateChanged(MATCHING_PSEUDO_CLASS, false);
            switch (get()) {
                case PLAIN -&gt; 
                    pseudoClassStateChanged(PLAIN_PSEUDO_CLASS, true);
                case DISPLAYING -&gt; 
                    pseudoClassStateChanged(DISPLAYING_PSEUDO_CLASS, true);
                case NOMATCH -&gt; 
                    pseudoClassStateChanged(NOMATCH_PSEUDO_CLASS, true);
                case PARTIALMATCH -&gt; 
                    pseudoClassStateChanged(PARTIALMATCH_PSEUDO_CLASS, 
                    true);
                case MATCHING -&gt; 
                    pseudoClassStateChanged(MATCHING_PSEUDO_CLASS, true);
            }
        }
    };
. . .
}</pre>

The `invalidated()` method for property `letterDisplay` must ensure that a pseudo-class state is mutually exclusive. That is, no `letterDisplay` pseudo-class state can match more than one state. Therefore, before turning on the `matching` pseudo-class state, we make all pseudo-class states false, as shown above.

**Note** : See **[LetterStyle.java](https://github.com/gailasgteach/Wordish/blob/master/src/main/java/com/asgteach/modelview/LetterStyle.java)** , **[LetterLabel.java](https://github.com/gailasgteach/Wordish/blob/master/src/main/java/com/asgteach/modelview/LetterLabel.java)** , and **[KeyButton.java](https://github.com/gailasgteach/Wordish/blob/master/src/main/java/com/asgteach/modelview/KeyButton.java)** for the above discussion.

**Note** : See **[styles.css](https://github.com/gailasgteach/Wordish/blob/master/src/main/resources/com/asgteach/style.css)** for the CSS styles used in Wordish.

iKonli Font Library {#h2-6-ikonli-font-library}
-----------------------------------------------

It's easy to add flair to your applications with third party icon packs. In Wordish, we use the [iKonli Font Library](https://kordamp.org/ikonli/) for the Delete Key as well as the Replay, Information, and Stats (Bar Chart) buttons. In addition, the Close icon lets you return to the main view from either the Information or Stats view. These icons are based on Material Design. Using familiar icons and fonts help make an application intuitive when you match icons with common, expected behavior.

To access the iKonli Font library in Wordish, we add appropriate modules to the **module-info.java** file, as follows.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">module wordish {
    . . .
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign2;
      . . .
}</pre>

And add these dependencies to the **pom.xml** file.

<pre class="EnlighterJSRAW" data-enlighter-language="xml" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;dependencies&gt;
     &lt;dependency&gt;
            &lt;groupId&gt;org.kordamp.ikonli&lt;/groupId&gt;
            &lt;artifactId&gt;ikonli-javafx&lt;/artifactId&gt;
            &lt;version&gt;12.3.1&lt;/version&gt;
     &lt;/dependency&gt;
     &lt;dependency&gt;
            &lt;groupId&gt;org.kordamp.ikonli&lt;/groupId&gt;
            &lt;artifactId&gt;ikonli-materialdesign2-pack&lt;/artifactId&gt;
            &lt;version&gt;12.3.1&lt;/version&gt;
     &lt;/dependency&gt;
      . . . 
&lt;/dependencies&gt;</pre>

Here's how to add an icon graphic to either a Button or Label in FXML. For example, we add the Delete icon named `"mdi2b-backspace-outline"` to our virtual keyboard KeyButton control. Note that you can specify its pixel width with attribute `iconSize`.

<pre class="EnlighterJSRAW" data-enlighter-language="xml" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;KeyButton fx:id="deleteButton" onAction="#processDelete" 
         prefHeight="57.0" 
         prefWidth="57.0" 
         textAlignment="CENTER"&gt;
    &lt;graphic&gt;
        &lt;FontIcon iconLiteral="mdi2b-backspace-outline" iconSize="24" /&gt;
    &lt;/graphic&gt;
&lt;/KeyButton&gt;</pre>

The [Material Design2 cheat sheet](https://kordamp.org/ikonli/cheat-sheet-material2.html) provides a handy list of other iconLiteral codes that you can incorporate into your UI.

Adding Custom Controls to Scene Builder {#h2-7-adding-custom-controls-to-scene-builder}
---------------------------------------------------------------------------------------

To use customized controls with Scene Builder, supply a JAR file with these compiled classes. After you import them into Scene Builder, you can use Scene Builder's visual features with your custom controls. Similarly, to incorporate third party icons within Scene Builder, import the required JAR file or repository.

From the Scene Builder Library menu, select the **Settings** icon (at the top next to Library) and the **JAR/FXML Manager**, as shown in Figure 3.

<figure class="wp-block-image size-full is-resized">
 <img decoding="async" src="/images/posts/2022/05/wordish-with-javafx-part-2/screenbuilder-librarysettings.png" alt="" class="wp-image-56022" width="477" height="292">
 <figcaption>
  Figure 3. Use the JAR/FXML Manager to customize Scene Builder
 </figcaption>
</figure>

Scene Builder will then display the Library Manager. To add the KeyButton and LetterLabel classes, select **Add Library/FXML from file system** , as shown in Figure 4. Supply the jar file containing the compiled class files for LetterLabel, KeyButton, and LetterStyle (here, file **wordish.jar**).

<figure class="wp-block-image size-full is-resized">
 <img loading="lazy" decoding="async" src="/images/posts/2022/05/wordish-with-javafx-part-2/screenbuilder-addcustom.png" alt="" class="wp-image-56023" width="388" height="377">
 <figcaption>
  Figure 4. The Scene Builder Library Manager
 </figcaption>
</figure>

Once you add them, the classes appear in the Custom section under Library. You can then select a LetterLabel, for example, place it in your visual view, and even configure its properties, including the custom properties `letterDisplay` and `matchResult` that we showed earlier.

For the iKonli Font Library, select **Search repositories** and provide the Group or Artifact ID. You can then select the libraries you need.

Customizing CSS {#h2-8-customizing-css}
---------------------------------------

Besides providing style classes to support the customized pseudo-classes, we also added custom styles to support the look you see in [Figure 2](#figure-2). We styled the application using root values for certain colors.

By assigning names to these colors, such as `-fx-match-color`, we can reuse them throughout the style sheet. We can even change the color theme of the entire application just by changing the colors assigned to our color names.

For our application, each FXML file uses the same style sheet. By identifying the CSS style sheet in the FXML file, Scene Builder can accurately reflect the styles you're using. Furthermore, the views share many of the same style settings.

We use black and white for the letter stroke color. Wordish has four main colors: a basic button background color and three match result colors: goldish, greenish, and dark gray. We define these in the style sheet root style as follows.

<pre class="EnlighterJSRAW" data-enlighter-language="css" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">.root {
    -fx-button-base-color: #d3d6d9;
    -fx-text-fill-base-color: black;
    -fx-text-fill-alt-color: white;
    -fx-partialmatch-color: #c5b565;
    -fx-nomatch-color: #797c7e;
    -fx-match-color: #78a86b;
}</pre>

These colors are then reused when we define our pseudo-class states.

Normally, when a JavaFX button is disabled, its opacity is reduced to .4. To mimic the Wordle UI, we override this behavior by leaving disabled buttons in our virtual keyboard fully opaque, as shown.

<pre class="EnlighterJSRAW" data-enlighter-language="css" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">.flow &gt; .button:disabled {
    -fx-opacity: 1.0 ;
}</pre>

However, we commented out the above style during development and testing, since it was helpful to notice when we disabled a button.

**Note** : See **[styles.css](https://github.com/gailasgteach/Wordish/blob/master/src/main/resources/com/asgteach/style.css)** for the CSS styles used in Wordish.

**Next**: In Part 3 we'll examine the controller code. The controller code maintains game state and responds to user input with appropriate updates to the UI.
