---
title: "Getting Started with FXGL Game Development"
slug: "getting-started-with-fxgl-game-development"
date: "2020-11-12T08:24:54+00:00"
lastmod: "2021-04-23T12:36:38+00:00"
description: "FXGL is a JavaFX Game Library Engine for Java and Kotlin, created by Almas Baimagambetov. Here is everything you need to get started!"
canonical: "https://webtechie.be/post/2020-05-07-getting-started-with-fxgl/"
authors:
  - "almasbaimagambetov"
  - "frankdelporte"
image: "Favicon-3-2.png"
categories:
  - "Game Development"
  - "JavaFX"
tags:
related_posts:
  - "foojay-podcast-25"
  - "creating-a-snake-game-with-javafx-fxgl-in-three-pair-programming-sessions"
  - "first-experiments-with-java-on-the-lattepanda-iota"
  - "javafx-links-of-november-2025"
enlighterjs: true
frozen: false
---

[FXGL](https://github.com/AlmasB/FXGL) is a Java, JavaFX and Kotlin Game Library (Engine) made by [Almas Baimagambetov](https://twitter.com/AlmasBaim). As my son (almost 10y) challenged me to make a game during my "Corona-stay-at-home-time", I had the luck Almas provided me a getting-started with this detailed step-by-step.

This will be the end result of this tutorial:

{{}}

We will make a *very* simple clone of Geometry Wars using [FXGL](https://github.com/AlmasB/FXGL), which is a JavaFX game development framework. You can find [the finished project of this post on my GitHub](https://github.com/FDelporte/FXGLFirstTest) as a Maven project.

To complete this tutorial you first need to [get FXGL](https://github.com/AlmasB/FXGL/wiki/Get-FXGL-%28Maven%2C-Gradle%2C-Uber%29) either via Maven / Gradle, or as an uber-jar. Ensure you use FXGL 11, the current latest version is `11.8`. This tutorial is standalone and does not require previous FXGL knowledge.

Dependencies {#h2-0-dependencies}
---------------------------------

I started with an empty Maven project and extended the pom.xml file with this dependency:

```xml
<dependency>
    <groupId>com.github.almasb</groupId>
    <artifactId>fxgl</artifactId>
    <version>11.8</version>
</dependency>
```


Code {#h2-1-code}
-----------------

The code is split into two classes

* GeoWarsApp.java which is the main application
* GeoWarsFactory.java which defines how the graphical elements look like and how they behave

### GeoWarsApp minimal code {#h3-2-geowarsapp-minimal-code}

Create a file `GeoWarsApp.java` and let's import all of these and forget about them for the rest of the tutorial. **Note:** the static imports allow us to write for instance `getAppHeight()` instead of `FXGL.getAppHeight()`, which makes the code cleaner.

```java
import static com.almasb.fxgl.dsl.FXGL.getAppHeight;
import static com.almasb.fxgl.dsl.FXGL.getAppWidth;
import static com.almasb.fxgl.dsl.FXGL.getGameController;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxgl.dsl.FXGL.onBtnDown;
import static com.almasb.fxgl.dsl.FXGL.onCollisionBegin;
import static com.almasb.fxgl.dsl.FXGL.onKey;
import static com.almasb.fxgl.dsl.FXGL.run;
import static com.almasb.fxgl.dsl.FXGL.showMessage;
import static com.almasb.fxgl.dsl.FXGL.spawn;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;
```


#### Minimal code in the game class

This section will go through each method and explain the major parts of the code. By default FXGL sets the game size to 800x600, which works for our game. You can change these and various other settings, for example `settings.setWidth(1280)`. For now, we will just set the title and add the entry point - `main()`.

```java
public class GeoWarsApp extends GameApplication {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Geometry Wars");
    }
}
```


#### Defining the game entities

The next step is to think about the types of game objects, which are called entities, that we will have in the game. In this simple example we will have the player, bullets and enemies. To mark these types, we will create an enum:

```java
public enum EntityType {
    PLAYER, BULLET, ENEMY
}
```


### GeoWarsFactory {#h3-3-geowarsfactory}

In this factory we create the element types. In FXGL, there is one place where all entities are created and it is called an entity factory. To create your own factory, we create a second class with the listed imports, again some are static to keep the code clean:

```java
import static com.almasb.fxgl.dsl.FXGL.entityBuilder;
import static com.almasb.fxgl.dsl.FXGL.getAppHeight;
import static com.almasb.fxgl.dsl.FXGL.getAppWidth;
import static com.almasb.fxgl.dsl.FXGL.getInput;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameWorld;

import be.webtechie.GeoWarsApp.EntityType;
import com.almasb.fxgl.dsl.components.OffscreenCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.dsl.components.RandomMoveComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class GeoWarsFactory implements EntityFactory {

}
```


#### Define the player entity

The factory needs to know how to create each entity. So, for each type we specified above in the EntityType enum, starting with `player` first, we must provide the following details:

```java
public class GeoWarsFactory implements EntityFactory {

    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        return entityBuilder()
                .from(data)
                .type(EntityType.PLAYER)
                .viewWithBBox(new Rectangle(30, 30, Color.BLUE))
                .collidable()
                .build();
    }
}
```


First, the method signature is of importance. It has an annotation that we can later use to spawn a player. Next, let's have a look at how we define the player.

* We use `entityBuilder()` to help us do so.
* `.from(data)` sets up typical properties such as the position, which is obtained from `SpawnData data`.
* We also set the type of the entity via `.type(EntityType.PLAYER)`.
* The next line `.viewWithBBox(new Rectangle(30, 30, Color.BLUE))` has two purposes: a) it provides the rectangle view for the player and b) it generates a bounding box for collisions from the view.
* Lastly, we mark the entity as `.collidable()` and build it.

#### Extend with the bullet entity

Same approach now for the bullet...

```
public class GeoWarsFactory implements EntityFactory {

    // ...

    @Spawns("bullet")
    public Entity newBullet(SpawnData data) {
        Entity player = getGameWorld().getSingleton(EntityType.PLAYER);
        Point2D direction = getInput().getMousePositionWorld()
            .subtract(player.getCenter());

        return entityBuilder()
                .from(data)
                .type(EntityType.BULLET)
                .viewWithBBox(new Rectangle(10, 2, Color.BLACK))
                .collidable()
                .with(new ProjectileComponent(direction, 1000))
                .with(new OffscreenCleanComponent())
                .build();
    }
}
```


First we get the player instance. The second line computes the direction in which the bullet will travel when created, starting from the players center. Excluding the API we already covered above, `.with(new ProjectileComponent(direction, 1000))` and `.with(new OffscreenCleanComponent())` attach **components** to our bullet entity. A component can contain data and behaviour and brings new functionality to an entity. For example, `ProjectileComponent` moves the entity every frame along `direction` with the given speed. `OffscreenCleanComponent`, as the name implies, removes the entity from the game if it is beyond the screen bounds.

#### Finally, we have the enemy entity

And the enemy is the last one we defined in the EntityType enum.

```java
public class GeoWarsFactory implements EntityFactory {

    // ...

    @Spawns("enemy")
    public Entity newEnemy(SpawnData data) {
        Circle circle = new Circle(20, 20, 20, Color.RED);
        circle.setStroke(Color.BROWN);
        circle.setStrokeWidth(2.0);

        return entityBuilder()
                .from(data)
                .type(EntityType.ENEMY)
                .viewWithBBox(circle)
                .collidable()
                .with(new RandomMoveComponent(
                    new Rectangle2D(0, 0, 
                    getAppWidth(), getAppHeight()), 50))
                .build();
    }
}
```


For our enemy, we will use a circle as the view. Most methods have already been covered above, so we will focus on `RandomMoveComponent`. This component, using the provided rectangular bounds, moves the entity randomly within these bounds. This simple behaviour is sufficient for our little game.

We are done with the factory class now!

### GeoWarsApp further extended {#h3-4-geowarsapp-further-extended}

Now FXGL knows how to create the entities, let's go back to the application class GeoWarsApp to add input and logic.

First we add a reference to the factory and our player, which we will spawn later.

```java
public class GeoWarsApp extends GameApplication {

    private final GeoWarsFactory geoWarsFactory = new GeoWarsFactory();
    private Entity player;

    // .. other code
}
```


Using this reference, we can start adding some user actions, known as input handling:

```java
@Override
protected void initInput() {
    onKey(KeyCode.W, () -> player.translateY(-5));
    onKey(KeyCode.S, () -> player.translateY(5));
    onKey(KeyCode.A, () -> player.translateX(-5));
    onKey(KeyCode.D, () -> player.translateX(5));
    onBtnDown(MouseButton.PRIMARY, () -> 
        spawn("bullet", player.getCenter()));
}
```


The code above should be self-explanatory and makes use of Java 8+ lambda notation. If you want to be able to shoot while the mouse button is being pressed, rather than just on a single press, you can change `onBtnDown` to `onBtn`.

There are two other things to add to our game: initialization logic and physics. Don't worry, whilst it may sound complex, it isn't. Initialization logic:

```java
@Override
protected void initGame() {
    getGameWorld().addEntityFactory(geoWarsFactory);

    player = spawn("player", getAppWidth() / 2 - 15, getAppHeight() / 2 - 15);
    geoWarsFactory.setPlayer(player);

    run(() -> spawn("enemy"), Duration.seconds(1.0));
}
```


First, we add our factory to the game world, so that we can use methods like `spawn()`. Next, we initialize our `player` reference by spawning the player entity in the center of the game. We also need to provide this player to our geoWarsFactory as it needs it to define the starting point of new bullets.

The last call sets up a timer that runs every second. Can you guess what happens every second? The answer is: `spawn("enemy")`, i.e. a new enemy entity is spawned. Since we don't provide any position, the enemy entities will spawn at `(0,0)`.

The physics code is straightforward as we have already set up the most complex things:

```java
@Override
protected void initPhysics() {
    onCollisionBegin(EntityType.BULLET, EntityType.ENEMY, (bullet, enemy) -> {
        bullet.removeFromWorld();
        enemy.removeFromWorld();
    });

    onCollisionBegin(EntityType.ENEMY, EntityType.PLAYER, (enemy, player) -> {
        showMessage("You Died!", () -> {
            getGameController().startNewGame();
        });
    });
}
```


We set up two collision handlers. The first one handles the collision between the bullet type and the enemy type. When such a collision occurs, we simply remove both entities from the game. In the second handler, we show an information dialog with text "You Died!" and restart the game.

Conclusion {#h2-5-conclusion}
-----------------------------

Hit run in your IDE on the main-method and that is all! A first working JavaFX game created with FXGL.

You can get the finished sources from [github.com/FDelporte/FXGLFirstTest](https://github.com/FDelporte/FXGLFirstTest).

Visit [FXGL wiki](https://github.com/AlmasB/FXGL/wiki/FXGL-11) for more tutorials. Pre-built games are available from [itch.io](https://fxgl.itch.io/) to give an idea of what can be achieved with FXGL. More complex examples are available at [FXGLGames](https://github.com/AlmasB/FXGLGames).
