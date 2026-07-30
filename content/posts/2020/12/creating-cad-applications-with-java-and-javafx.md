---
title: "Creating CAD Applications with Java and JavaFX"
slug: "creating-cad-applications-with-java-and-javafx"
date: "2020-12-09T11:26:19+00:00"
lastmod: "2021-01-21T12:02:33+00:00"
description: "Some days ago I finished a CAD application whose purpose is to calculate the energy efficiency of Dwellings (or multiple Dwellings). It can be seen as an application similar to Autocad (which is used in Civil Engineering, Architecture, etc) but with the specific purpose to do energy efficiency assessment. I believe, having a good UX, features users have been dying to have, good, well structured code that lets you continuously evolve and better maintain what you already have and finally topping all that with a nice looking user interface, are the markers to a successful application. I think we’ve been able to score high in all those markers. - by Pedro Duque Vieira"
authors:
  - "pedro-vieira"
image: "/images/posts/2020/12/creating-cad-applications-with-java-and-javafx/showcase-model-view-1024x555.png"
categories:
  - "JavaFX"
  - "Use Cases"
tags:
related_posts:
frozen: false
---

I'm a [Java (JavaFX) freelance consultant, Software Engineer and Software Designer](http://www.pixelduke.com/) and a few months ago I finished a CAD application for a client.

This is a CAD application whose purpose is to calculate the energy efficiency of Dwellings (or multiple Dwellings). It can be seen as an application similar to Autocad (which is used in Civil Engineering, Architecture, etc) but with the specific purpose to do energy efficiency assessment.
![](/images/posts/2020/12/creating-cad-applications-with-java-and-javafx/showcase-model-view-1024x555.png)

After about one year of development, my client (he himself an energy rater) and I were able to finish the project within a much smaller time frame and a much smaller budget than other similar projects from competing companies. It wouldn't be an exaggeration to say that its cost was about 30 times less and it took at least less than half the time.

The feedback and reception to our application has gone far beyond what we expected even though the application is still in Beta (at the time this article was first published).

### Background {#h3-0-background}

My responsibility in this project was to define the higher level architecture, do the implementation, User Interface Design and User Experience and all the aspects of setting up a professional environment and workflow for developing a software application.

My client, Nick, an energy rater by profession, has always been pushing energy rating to a higher level. He's done the energy ratings for several buildings including commercial, public and residential. Some of them award winning projects. As a self-taught, amateur programmer he also helped implement the application.

The purpose of the tool, called HERO, is to do the energy efficiency assessment of buildings and in the end generate an energy rating certificate. These certificates are mandatory and every building must meet a minimum value of energy efficiency. Applications that have this purpose must be certified by a public entity (ours already is) and the professional doing the energy rating must himself be a certified energy rater (having completed an accredited course with success).

### High Level View {#h3-1-high-level-view}

![](/images/posts/2020/12/creating-cad-applications-with-java-and-javafx/showcase-model-view2-1024x555.png)

If you follow my [blog](http://www.pixelduke.com), you've probably noticed from the picture that the application is using [JMetro](https://www.pixelduke.com/java-javafx-theme-jmetro/).

The picture above is from the Model view. In this view you create the project and enter all its data. The pane to the left is where you can view the visuals, and create the floor plan (visually). The pane to the right is the Datagrid where you see and enter data for all the objects of the project (walls, windows, floors, top lights, etc) in a table like format. All these views are inside a Dockpane like container that allows you to resize, restore and maximize each pane.

There are also other views, inside the Model view, not shown in the picture. Like the library where you can create and manage the materials that you can use throughout your project, your custom assemblies for walls, the different types of windows, etc.
![](/images/posts/2020/12/creating-cad-applications-with-java-and-javafx/showcase-results-view-1024x555.png)

After you run the simulation, you can also see the results of your energy efficiency modelling (picture above). The Results View shows a dashboard, with charts, tables, etc, that lets the user view at a glance how efficient the dwelling or dwellings are and quickly notice what might need to be readjusted.

### Details and Features {#h3-2-details-and-features}

The calculations for the energy rating can get really complex. They can depend on a multitude of factors. To name a few:

1. The materials used. There's a huge database of materials that can be used in Floors, Ceilings, Walls, etc. And the user can also, for instance, create his own custom assembly for Walls defining their list of materials in a specified order (from inside to the outside).
2. Size and layout of the building's areas.
3. Climate over the year in the area where the building is located.
4. Orientation of the building (influences how the Sun heats up each area).
5. Shading of other buildings, fences, etc that are around the building.
6. The building's penetrations.
7. Top lights used, ceiling fans, exhaust fans, etc.
8. Other factors.

All the items mentioned are modeled and calculated in HERO.

All the geometry, intersections, etc are also computed by the application. For instance, if you create a Zone in a level -- a Zone can be a living room, kitchen, bedroom, etc -- and then another smaller Zone in the level above, there will be an intersection. This intersection will produce a "hole" in the ceiling of the level below, like a donut. Hero will realize automatically that the 2 zones are connected and will create a new Ceiling in the zone below (in the place of the donut) adjacent to the zone above.
![](/images/posts/2020/12/creating-cad-applications-with-java-and-javafx/donut-step1-1024x555.jpg) 1 - Create zone ![](/images/posts/2020/12/creating-cad-applications-with-java-and-javafx/donut-step2-1024x555.jpg) 2- Create another Zone on level above ![](/images/posts/2020/12/creating-cad-applications-with-java-and-javafx/donut-step3-1024x555.jpg) 3 - Geometry automatically split (new Ceiling created)

Shading objects are also calculated automatically, one Zone can cast a shade on another Zone placed elsewhere.
![](/images/posts/2020/12/creating-cad-applications-with-java-and-javafx/auto-wingwall-step-1-1024x555.png) 1 -- Create Zone ![](/images/posts/2020/12/creating-cad-applications-with-java-and-javafx/auto-wingwall-step-2-1024x555.png) 2 -- Create second Zone to the right ![](/images/posts/2020/12/creating-cad-applications-with-java-and-javafx/auto-wingwall-step-3-1024x555.png) 3- Shading object automatically calculated and created

Another thing that HERO automatically computes are adjacencies between Walls and Zones. If you create a Zone right next to another, instead of creating 2 Walls between the Zones, the application will automatically figure out that the Zones are connected. It will connect the 2 zones by creating 1 internal Wall between them, which is different from an outside facing Wall.
![](/images/posts/2020/12/creating-cad-applications-with-java-and-javafx/Internal-Wall-step-1-1024x555.png) 1 -- Create Zone ![](/images/posts/2020/12/creating-cad-applications-with-java-and-javafx/Internal-Wall-step-2-1024x555.png) 2- Create another overlapping Zone

For this post not to get too long, too tedious, and too technical I just pointed out some of the features of Hero, there are a lot more. I think you can get a general idea of Hero's features from what was mentioned above.

### Reception and Feedback {#h3-3-reception-and-feedback}

My client and I were surprised with the reception the application got. Given that, at the time this article was first written (and at the time the application got its first release), the application was still in Beta and that there were already 3 applications in the market, 2 of them well established, we were expecting a much more moderate reaction.

Before 24 hours had passed since the first release, we had already 50 registered users and 1 certificate issued. In our issue tracker, which has the purpose of only storing bug reports from our users, we were getting thanks messages.
![](/images/posts/2020/12/creating-cad-applications-with-java-and-javafx/comentario-1-1.jpg) ![](/images/posts/2020/12/creating-cad-applications-with-java-and-javafx/comentario-2-2.jpg)

### Technology and Form Factor {#h3-4-technology-and-form-factor}

*Notice: I might be a bit biased because I've been working with JavaFX (and Java) since the very first release. However, I'm going to be as impartial as possible.*

If the requirements are to build a productivity application, the best option in my opinion is to go desktop native, instead of, for instance, doing a mobile app. Nothing beats a keyboard and a mouse in this area. Besides more precise and faster input, you can display much more content on screen without having to worry about the touch targets being too small for the fingers. This also means the user will be able to see more data on screen which will be important in a productivity application.

As for "native" vs web. Native allows for better performance, security, etc than a web application. Web has the advantage of higher availability (no required installations, all the user needs is a browser) but if you're building a performance demanding app, I think your best bet is to go native.

Regarding the programming model, I tend to prefer Java (or any other object orientated, type safe language) and its framework and big collection of third party libraries than the web programming model. Especially when building complex applications.

JavaFX (and Java) are a perfect fit in this type of scenario as JavaFX really shines in the Desktop world. HERO is currently running on Windows, which will probably be the majority of users for your desktop applications. But we can easily, with a few tweaks, support Mac as well and even Linux (though in our case there probably won't be many users using that O.S., if any).

If, for some reason, we ever have a need to create a scaled-down mobile or tablet version or reuse some of our existing components in a mobile application, we can. Oracle and Gluon have been pushing hard for this, continuously working and making it better. Recently, by leveraging GraalVM, applications can be AOT compiled, benefiting from faster startup times and performance. This is also great for IOS, since IOS doesn't allow for JIT compilation.

To be fair and to try to be impartial, I'll conclude this section with what, in my view, are some of downsides of JavaFX (currently, at this moment):

* Table API.
* Lack of tools to better debug CSS issues and try out CSS tweaks, like for instance having something like firebug (ScenicView is great for other purposes but not specifically for this one).
* Lack of support for some of the Web's CSS properties. Also one of my pet peeves is the fact that every property is prefixed with "-fx-" (I would prefer this would be dropped on properties that have the same behavior as the ones in Web CSS).
* IDE support for JavaFX CSS, currently isn't great: error highlighting, etc (side note: I'm using Intellij).
* JavaFX API is too closed and lacks better support for extension on some cases (classes that are final, classes with final methods, etc) regarding this issue I preferred Swing's more open API.
* Some Font rendering issues (this might only happen with some fonts and only on some platforms -- Windows is worse than Mac).
* Better separation of concerns in JavaFX Controls. There was a plan a few years ago to make Behavior classes public, which would probably provide a better separation between the View, Controller and Model aspects of a Control. Right now it's a bit difficult to create a new Skin for an existing JavaFX SDK Control if your only requirement is to tweak some of its View aspects and when that can't be done through CSS. This often results in developers preferring to create a new Control altogether than providing a new Skin for an existing one which will mean more work and a higher degree of coupling with those Controls (for instance, you won't be able to easily switch between Skins). The fact that the API is often too closed also means that, usually, extending an existing Control isn't an option.

### Conclusion {#h3-5-conclusion}

Despite being a quite complex application, I believe HERO was created in a record time. Not only that but the feedback from our users makes us feel the application fills a gap and that they are keen to start using it and pushing it to be the default tool for their company.

I believe, having a good UX, features users have been dying to have, good, well structured code that lets you continuously evolve and better maintain what you already have and finally topping all that with a nice looking user interface, are the markers to a successful application. I think we've been able to score high in all those markers.

The choice of form factor and technology is also really important when building an application. I believe the right choice was done for this productivity application: going desktop native and choosing Java and JavaFX.

You can check out my talk at JFXDays about this application [here](https://www.youtube.com/watch?v=8jkKF5LgonQ).

Finally, you can follow me on [twitter](https://twitter.com/P_Duke) if you want to keep up-to-date with my work, open source projects and published articles.

_____________________________________________________________________________

**Note:** Originally published by the author [in his blog](https://pixelduke.com/2020/07/27/creating-a-cad-application-in-java-javafx/).
