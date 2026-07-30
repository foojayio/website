---
title: "Creating a JavaFX World Clock from Scratch (Part 2)"
slug: "creating-a-javafx-world-clock-from-scratch-part-2"
date: "2020-12-24T09:46:11+00:00"
lastmod: "2022-04-30T03:36:27+00:00"
description: "Join Carl Dea in his JavaFX clock tutorial series where he shows you how he animates the clock face's hands using basic trigonometry."
authors:
  - "carldea"
image: "/images/posts/2020/12/creating-a-javafx-world-clock-from-scratch-part-2/Screen-Shot-2020-12-19-at-9.35.09-PM.png"
categories:
  - "JavaFX"
  - "Tutorials"
tags:
related_posts:
  - "javafx-links-of-november-2025"
  - "will-openjfx-be-merged-into-openjdk-it-would-be-a-perfect-match-with-java-on-mobile"
  - "new-video-series-javafx-in-action-part-1"
  - "javafx-links-of-june-2026"
enlighterjs: true
frozen: false
---

> *"The only way to learn mathematics is to do mathematics."* -- Paul Halmos[\[1\]](https://en.wikipedia.org/wiki/Paul_Halmos "Mathematician Paul Halmos")

Introduction {#h2-0-introduction}
---------------------------------

Welcome to *Creating a JavaFX World Clock from Scratch Part 2!*

This is the second installment of a series of blog entries on how I created a "sci-fi" looking world clock using JavaFX.

If you have not read Part 1[\[2\]](https://foojay.io/today/creating-a-javafx-world-clock-from-scratch-part-1/ "Creating a JavaFX World Clock from Scratch Part 1") please see the introduction section of the *Creating a JavaFX World Clock from Scratch (Part 1)* [\[2\]](https://foojay.io/today/creating-a-javafx-world-clock-from-scratch-part-1/ "Creating a JavaFX World Clock from Scratch Part 1").

In Part 2, I will show you how I animate the clock face's hands using basic trigonometry. If you want to skip the tutorial and go straight to the source code head over to GitHub WorldClock [\[4\]](https://github.com/carldea/worldclock "JavaFX WorldClock")  

<img fetchpriority="high" decoding="async" aria-describedby="caption-attachment-36624" class="size-medium wp-image-36624" src="/images/posts/2020/12/creating-a-javafx-world-clock-from-scratch-part-2/Screen-Shot-2020-12-19-at-9.35.09-PM-280x510.png" alt="Creating a JavaFX World Clock from Scratch (Part 2)" width="280" height="510">

*Creating a JavaFX World Clock from Scratch (Part 2)*{#caption-attachment-36624}

Scene Builder {#h2-1-scene-builder}
-----------------------------------

First lets give a quick recap of Part 1. In the first part I discussed my design workflow, then later I mention tools such as a WYSIWYG graphical editor called Scene Builder[\[5\]](https://gluonhq.com/products/scene-builder/ "Gluon Scene Builder"). Scene Builder is a tool to allows you to style shapes and to layout nodes onto the JavaFX scene graph.

I want to give a shout out to the folks from GluonHQ[\[3\]](https://gluonhq.com "GluonHQ") who maintain and provide the Scene Builder tool free of charge. Lets help Gluon keep this tool free by testing, contributing or requesting for support or services at Gluon Services. [\[1\]](https://gluonhq.com/services/ "SceneBuilder Support")  

<img decoding="async" aria-describedby="caption-attachment-36633" class="size-medium wp-image-36633" src="/images/posts/2020/12/creating-a-javafx-world-clock-from-scratch-part-2/Screen-Shot-2020-12-20-at-6.38.48-PM-484x510.png" alt="Gluon Scene Builder" width="484" height="510">

*Gluon Scene Builder*{#caption-attachment-36633}

Finally, If you remember in part 1 I pointed out the hour hand shapes (arc \& circle) having hard coded values for their angles and position attributes purely for prototyping purposes. In order to animate the arms around the clock face we will begin to make these values dynamic.

Before I show you the code, I want to show you the world clock face and its hour hand parts again. Just focus on the hour hand arc and hour hand tip which is a JavaFX Arc [\[3\]](https://openjfx.io/javadoc/15/javafx.graphics/javafx/scene/shape/Arc.html "Arc API") and Circle [\[4\]](https://openjfx.io/javadoc/15/javafx.graphics/javafx/scene/shape/Circle.html "Circle API") shape node respectively.  
![Clock parts](/images/posts/2020/12/creating-a-javafx-world-clock-from-scratch-part-2/Screen-Shot-2020-12-08-at-12.29.31-AM.png)

*Parts of the hour hand of the world clock*{#caption-attachment-36529}

Hour Hand Arc {#h2-2-hour-hand-arc}
-----------------------------------

An Arc shape is really a wedge (pizza slice). As we previously described the fill color is set to be transparent and the stroke width set to 4 pixels with a stroke color of orange. This gives it the appearence of a curved piece cut from a circle. Next, we will look at the Arc's attributes that will determine how long the arc will be and how to move it around the clock circle.

To change the length of the arc position around the clock face you will need to modify the following attributes of the JavaFX **Arc** shape:

| Attribute  | Value |                                       Description                                        |
|------------|-------|------------------------------------------------------------------------------------------|
| startAngle | 0.0   | Start angle (in degrees). Zero is at the 3'o clock position moving counter clockwise     |
| length     | 90.0  | Extent angle (in degrees). The angle offset from the start angle going counter clockwise |

<img loading="lazy" decoding="async" aria-describedby="caption-attachment-36628" class="size-medium wp-image-36628" src="/images/posts/2020/12/creating-a-javafx-world-clock-from-scratch-part-2/Screen-Shot-2020-12-19-at-10.23.06-PM-626x510.png" alt="Drawing the Hour Hand Arc" width="626" height="510">

*Drawing the Hour Hand*{#caption-attachment-36628}

Since the Arc shape is drawn in a counter clockwise rotation we have to do some math to make the arc appear in a clockwise direction. For example if it were moving from the 12:00 postion to the 3:00 position the start angle is 0° and the length (extent angle) would be 90° as shown below.  
![0 to 90 degrees in a Counter Clockwise direction](/images/posts/2020/12/creating-a-javafx-world-clock-from-scratch-part-2/Screen-Shot-2020-12-20-at-8.32.50-PM-1.png)

*0 to 90 degrees in a Counter Clockwise direction*{#caption-attachment-36635}

The hour hand has 12 positions and given a circle is 360° degrees (or 2π radians) each hour would be 360÷12 equalling 30° degrees. To represent 1:00 the hour hand arc should appear as a 30° (length) arc looking like its a moving in a clockwise direction from 12:00 to 1:00 when really its startAngle is 60° and length (extent angle) is 30°.

To make things a little easier let's look at a couple of simple math equations. These equations will later be converted to Java lambdas (functions).

#### Start Angle Based on the Hour

The following is the formula to calculate the start angle based on the hour passed in:

1. `degrees = ( 12 - hour ) * 30` // one hour is 30 degrees.
2. `startAngle = (degrees + 90) % 360`

In the second equation you'll notice the 90 degrees added, this simulates the hour hand starting from the 12:00 o'clock position as opposed to the 3:00 position (which is zero degrees). Also, the modulus 360° is to ensure the angle between 0° to 359°.

The equation is translated to Java code as a lambda of type `Function<Integer, Integer>`. The Function interface accepts a value and returns a value respectively. In this case the **hour** is passed in and the **start angle** is calculated and returned.

<pre class="EnlighterJSRAW" data-enlighter-language="java">/**
 * Start Angle of arc to draw the hour hand (start).
 */
private Function&lt;Integer, Integer&gt; startAngleHour = ( hours ) -&gt; {
    // 360 ÷ 12 = 30 degrees for each hours tick on the clock
    int degrees = (12 - hours) * 30;
    // add 90 degress to position start at the 12'o clock position.
    // JavaFX arc goes counter clockwise starting zero degrees at the 3 o'clock
    return (degrees + 90) % 360;
};</pre>

The following code snippet is how to call the lambda function to calculate the startAngle:

```java
int startAngle = startAngleHour.apply(1); // 60 degrees
```

After, creating a convenience function to calculate the **startAngle** I also created a function to calculate the **length** (extent angle) of the arc based on the hour of the clock (1-12).

#### Length or Extent Angle Based on the Hour

The following is the formula to calculate the length (extent angle) based on the hour passed in:

1. `degrees = ( 12 - hour ) * 30`
2. `extentAngle = (360 - degrees) % 360`

As before the first formula is to determine degrees from the 12 o'clock position. The second formula is to move the arc to a start position by being subtracted from 360 (degrees).

The equation is translated to Java code as a lambda of type `Function<Integer, Integer>` shown in the listing below:

<pre class="EnlighterJSRAW" data-enlighter-language="java">/**
 * Extent angle of the arc to draw the hour hand (end)
 */
private Function&lt;Integer, Integer&gt; extentAngleHour = ( hours ) -&gt; {
    // 360 ÷ 12 = 30 degrees for each hours tick on the clock
    int degrees = (12 - hours) * 30;
    // make the extent angle counter clockwise to the 12'o clock position
    return (360 - degrees) % 360;
};</pre>

With the lambda function (**extentAngleHour**) ready to be used, the following code statement illustrates how to invoke the function to calculate the extentAngle.

```java
int extentAngle = extentAngleHour.apply(1); // 30 degrees
```

The following is an example of step-by-step calculations using the above equations to determine the **startAngle** and **extentAngle** at 1:00, 2:00, and 3:00 o'clock.  
![Calculating Start and Length angles for Hour Hand Arc](/images/posts/2020/12/creating-a-javafx-world-clock-from-scratch-part-2/Screen-Shot-2020-12-21-at-12.18.03-AM.png)

*Calculating Start and Length angles for Hour Hand Arc*{#caption-attachment-36636}

Now that you know how to calculate and draw the arc now we need to get a reference to the JavaFX Arc and Circle nodes in order to update values dynamically.

If you remember in Scene Builder the nodes have their fx:id set with a name such as `hourHandArc` and `hourHandTip`. In the controller java file these variables will be defined using the FXML annotation as shown below:

<pre class="EnlighterJSRAW" data-enlighter-language="java">@FXML
private Arc hourHandArc;
@FXML
private Circle hourHandTip;</pre>

Using the above annotation (@FXML) is JavaFX's dependency injection mechanism to reference nodes in the scene graph. This allows the application to obtain instance objects such as the **Arc** and **Circle** nodes to be injected (assigned) during runtime. This makes the nodes available to methods in the controller (WorldClockController.java) class.

After referencing the Arc the controller code can now update the positions on every clock tick as shown below.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">// draw orange glowing hour hand arc
int hourStartAngle = startAngleHour.apply(hour);
int hourExtentAngle = extentAngleHour.apply(hour);
hourHandArc.setStartAngle(hourStartAngle);
hourHandArc.setLength(hourExtentAngle);<code class="language-java"></code></pre>

Similar to a time lapse an animation of the hour hand is shown below. It doesn't show the hour hand tip, more on that next.  
![Hour Hand Animation without the tip](/images/posts/2020/12/creating-a-javafx-world-clock-from-scratch-part-2/hourhand-animation-without-tip.gif)

*Hour Hand Animation without the tip*{#caption-attachment-36631}

Now that you know how to position and draw arcs to appear to move, let's look at basic trigonometry to move the hour hand tip around the clock face.

### Hour Hand Tip {#h3-3-hour-hand-tip}

To change the (X, Y) position of the Circle shape around the clock face you will need to modify the following attributes of the JavaFX **Circle** shape:

| Attribute  | Value |           Description           |
|------------|-------|---------------------------------|
| translateX | 35.0  | moves right 35 pixels           |
| translateY | 0.0   | moves zero pixels on the Y axis |

To move the hour hand tip (circle) in a clockwise direction I will be using Math's cosine and sine to determine on a unit circle its X, Y coordinate (point on the circle). Also, multiplying by the radius amount will project the point onto the hour hand track circle.

1. X coordinate on the unit circle - cosine (angle)
2. Y coordinate on the unit circle - sine(angle)
3. X, Y projected coordinate point on the larger circle based on the radius. Projected by `X * radius` and `Y * radius` respectively.

Calculating the position of the tip:

<pre class="EnlighterJSRAW" data-enlighter-language="java">/**
 * Positions the ball or tip at the start of the arc
 * The angle in degrees creating a point on the unit circle multiplied by the radius.
 */
private BiFunction&lt;Integer, Double, double[]&gt; tipPointXY = ( angDegrees, radius ) -&gt; {
    double [] pointXY = new double[2];
    pointXY[0] = Math.cos(Math.toRadians(angDegrees)) * radius;
    pointXY[1] = Math.sin(Math.toRadians(angDegrees)) * radius;
    return pointXY;
};</pre>

To use the function tipPointXY() it will return an array of type double containing two values where `hourTipPoint[0]` is the X coordinate and `hourTipPoint[1]` is the Y coordinate respectively.

<pre class="EnlighterJSRAW" data-enlighter-language="java">// draw orange glowing hour hand tip
 double [] hourTipPoint = tipPointXY.apply(hourStartAngle , 35.0);
 hourHandTip.setTranslateX(hourTipPoint[0]);
 hourHandTip.setTranslateY(hourTipPoint[1] * -1);</pre>

You should notice the method call to `setTranslateY(hourTipPoint[1] * -1)` where its value is multiplied by **-1**. This is to convert to the screen coordinate system where the Y coordinate going in a southerly direction are positive values.

Shown below is the hour hand **arc** and **tip** moving around the clock face.  
![Hour Hand Animation with tip](/images/posts/2020/12/creating-a-javafx-world-clock-from-scratch-part-2/hourhand-animation.gif)

*Hour Hand Animation with tip*{#caption-attachment-36630}

To see the full listing of the code to move the clock arms see WorldClockController.java[\[6\]](https://github.com/carldea/worldclock/blob/main/src/main/java/com/carlfx/worldclock/WorldClockController.java "WorldClockController.java") on GitHub.

There you have it! A way to animate the clock face. In [Part 3](https://foojay.io/today/creating-a-javafx-world-clock-from-scratch-part-3/) of this blog series I will be creating a UI form to configure the world clock such as changing timezones and locations (I will finally remove my pesky hardcoded cities).

Conclusion {#h2-4-conclusion}
-----------------------------

In Part 2, you got a chance to use some math and trig skills to determine how to position parts of the hour hand.

After learning how to convert the math to usable functions, you get a chance to see JavaFX's FXML annotations to reference nodes on the scene graph.

Lastly, you were able to see animations of the hour hand move about the clock face.

As always comments are welcome. Happy coding!
