---
title: "Getting Started with Game Development Basics with FXGL"
date: "2021-06-17T08:48:28+00:00"
lastmod: "2021-06-17T08:48:30+00:00"
description: "Learn the fundamental concepts of FXGL to abstract away complex details that are present in modern games and help simplify development."
authors:
  - "almasbaimagambetov"
image: "Favicon-3-2.png"
categories:
  - "Game Development"
  - "JavaFX"
tags:
related_posts:
frozen: false
---

Game Development is a large field of Computer Science with a lot of underpinning theory behind the concepts and practices used in the industry. In this short article, we will learn some fundamental basics of these concepts, which will be explored within the context of the [FXGL](https://github.com/AlmasB/FXGL) game engine.

However, the concepts themselves are language-agnostic and engine-agnostic. Please note that the material presented here is deliberately simplified to provide a gentle introduction. Those seeking in-depth coverage are encouraged to pursue further research.

## Main (Game) Loop

The main loop in games is similar to that of any other front-end application. It is an infinite loop, set to terminate when the user exits the application. The game loop, at a high-level, consists of 3 phases:

1. Input
2. Update
3. Render

This is the typical order of these phases: first we take user input, then we update the game (taking into account the new input) and finally we draw the game to the screen.

### Input

In this phase the game (or engine) will capture all types of input from the user. This includes keyboard presses, mouse events, touch screen gestures, controller and joystick inputs. In the same phase, the game (or engine) may wish to process the captured input. Typically this means: when `F` key is pressed, do whatever `F` is supposed to do in the game. For example, in a first-person shooter game, `F` may be used to perform a melee attack.

In FXGL, any input trigger (keyboard, mouse or virtual) is captured internally and handled by a `UserAction` that is specified by the developer. A `UserAction` has three states:

* BEGIN (1 tick)
* ACTION (1 or more ticks)
* END (1 tick)

Those states correspond to the user pressing, holding and releasing a trigger. This 3-state system allows us to provide an easy high-level API for handling any type of input. Consider a `UserAction` for hitting a golf ball:

```java
UserAction hitBall = new UserAction("Hit") {
    @Override
    protected void onActionBegin() {
        // action just started (key has just been pressed), play swinging animation
    }

    @Override
    protected void onAction() {
        // action continues (key is held), increase swing power
    }

    @Override
    protected void onActionEnd() {
        // action finished (key is released), play hitting animation based on swing power
    }
};
```

Now that we have created an action `hitBall`, we can ask the input object to bind it to `KeyCode.F`:

```java
@Override
protected void initInput() {
    Input input = getInput();

    input.addAction(hitBall, KeyCode.F);
}
```

### Update

Most of the game logic happens in this phase. A non-exhaustive list of things to be called inside the update method includes:

* Game world update
* AI pathfinding
* AI behaviour
* Physics
* Audio
* Achievements
* Notifications

We will cover those that are most prominent: game world update, AI and physics.

Any game object that you can think of (player, coin, power-up, wall, dirt, particle, weapon) is an entity. By itself an entity is nothing more but a generic object. Two instances of the `Entity` class are practically indistinguishable from each other. Components are the bits that define an entity. By attaching one or more components to an entity, we can shape each entity to be anything we like.

The game world is responsible for adding, updating and removing entities. It also provides means of querying entities by various criteria. In FXGL, the game world instance can be obtained by calling `FXGL.getGameWorld()`. The game world update iterates through all existing entities and performs a single frame tick on each of them. This, in turn, will update each component attached to an entity.

During the AI tick, each relevant entity's behaviour will be computed. This determines what the entity will do next. If the behaviour is related to movement, then the pathfinding algorithm kicks in to return a valid path from where the entity is to where the entity needs to go. Finally, the physics tick will ensure that all entity positions are valid, i.e. entities with rigid bodies do not overlap and that entities do not leave their respective boundaries. Collision detection will also be performed inside the physics tick. The detected collisions are then reported back to the game, so the user can handle them appropriately.

### Render

In essence, the render phase is about turning the game world into a visual product. Modern games utilise several sophisticated techniques in order to achieve a high-quality image of the game state. Once the game state is drawn, a myriad of post-processing effects can be applied to generate a unique or even cinematic feel for the game. By repeating this process every frame, we are able to obtain the right level of smoothness and fluidity to match the screen refresh rate.

In general, there are two common ways to render an object: using some form of an immediate mode or a retained mode. In JavaFX (and therefore in FXGL), the immediate mode is implemented via the `Canvas` object and the retained mode is implemented via the scene graph architecture. You can also read [this](http://docs.oracle.com/javase/8/javafx/get-started-tutorial/jfx-architecture.htm#A1106328) for in-depth coverage of the JavaFX architecture, including the graphics aspect. We will discuss the scene graph approach.

In FXGL, in order to display an entity, you need to configure and attach its view. This is done via `ViewComponent`, as follows:

```java
// this can be any JavaFX node
Node node = ...

entity.getViewComponent().addChild(node);
```

Most of the time you want to use an image as the view. There is a `Texture` class which allows you to do just that, besides it is also a JavaFX node:

```java
Node node = FXGL.texture("player.png"); 
// same as above
```

Alternatively, the entity builder provides static methods to make building entities easier, including setting their view:

```java
var entity = FXGL.entityBuilder()
                 .view("player.png")
                 .build();
```

## Conclusion

In this article, we have covered the basics of game development using FXGL as a case study.

These fundamental concepts allow us to abstract away complex details that are present in modern games and help us simplify development.

If you would like a more detailed overview of how a game engine works, FXGL has [comprehensive wiki pages](https://github.com/AlmasB/FXGL/wiki/FXGL-11).
