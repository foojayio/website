---
title: "Interview with Paul Kocian aka Orango_Mango"
date: "2023-05-08T07:49:40+00:00"
lastmod: "2023-05-22T08:24:14+00:00"
description: "Learn about an impressive list of JavaFX and Raspberry Pi projects and beyond worked on by a 16 year old in Italy!"
canonical: "https://webtechie.be/post/2023-05-02-interview-paul-kocian-orangomango/"
authors:
  - "frankdelporte"
image: "3dcar-415x510-1.png"
categories:
  - "Interviews"
  - "Java Core"
  - "JavaFX"
related_posts:
  - "foojay-podcast-9"
  - "game-development-basics-with-fxgl"
  - "visualizing-brain-computer-interface-data-using-javafx"
  - "foojay-podcast-25"
frozen: false
---

***Some time ago on Twitter, I discovered [@Orango_Mango](https://twitter.com/orango_mango), who shared his progress in creating a 3D engine with JavaFX. The shared videos started with a [Rubik's cube in January '23](https://twitter.com/orango_mango/status/1610894079654563843), over a [basic tumbling car in February](https://twitter.com/orango_mango/status/1620493609287172096), to a [full Minecraft-like world in April](https://twitter.com/orango_mango/status/1646218237535813639). Who is this @OrangoMango? And why did he take up the challenge to create a 3D engine with Java and JavaFX? Let's find out...***

{{< gallery >}}
rubikscube-319x510.png
3dcar-415x510.png
minecraft-645x510.png
{{< /gallery >}}

***Let's start with the first mystery... Who is behind the @Orango_Mango account? Can you introduce yourself?***

My nickname is Orango_Mango, but my real name is Paul Kocian, and I live in Italy. I started programming at the age of 13 and learned the basics of Python by building simple programs and, later on, my first GUI applications. I started with a simple game where the computer asks a few operations, and the user must calculate the result in the shortest time possible without getting any of these wrong.

Once I learned more advanced things like OOP programming, SQLite, and the Tkinter module, I started building applications like a [login interface](https://github.com/OrangoMango/LoginInterfaceSimulator) and then my first game using the [tkinter canvas: Pong](https://github.com/OrangoMango/Bounce).  

At this time, I loved very much building games and learning how games are built on a low level, so I started coding [FlappyBird](https://github.com/OrangoMango/FlappyBird) and [LavaPlatformer](https://github.com/OrangoMango/LavaPlatformer). As I was very new at game development, the code was written really badly when I read it now.

At the age of 14, I started coding in Java, and a year later, I also started using the JavaFX framework. I started building very simple GUI apps and then games. I also joined the [GMTK game jam in 2022](https://itch.io/jam/gmtk-jam-2022/rate/1621600). Now I'm 16 and try to further extend my experience with apps like a [MySQL user interface](https://github.com/OrangoMango/MySQL-GUI-Viewer) and games like [TrisGame](https://github.com/OrangoMango/TrisGame), a [basic multiplayer game](https://github.com/OrangoMango/BasicMultiplayerGame), [Projectile](https://github.com/OrangoMango/Projectile), and [FoodDice](https://github.com/OrangoMango/FoodDice).

***That's an impressive list of projects, and on top, you're only 16 years old! How do you find the time to learn and create all this? Are you still in school? And where did you learn all this?***

Yes, I go to the "Liceo Filippo Buonarroti" school in Pisa, Italy. We mainly use C++, but I don't learn to program so profoundly. That's why I learn those things mostly alone at home.

When I don't know how to code something, I look it up on StackOverflow or other sites until I find a solution.

As I also have homework and exams, I don't have much time during the week to code my stuff. Building a 3D engine takes time, so I mostly code during the weekend and holidays.

***For many, Java is considered to be an old and boring programming language. So I'm delighted to learn that someone your age selected Java to learn more and develop games and other applications. What is the main reason for you choosing Java?***

I like making games but I think the best part is to code a game on a low level, that's why I don't like to use game engines like Unity (C#), where everything is already created for you (the graphics, physics, AI,...).

I know that Java is an old language, but I think it's still the best for making games.

For example, Minecraft is made in Java, and was my inspiration for a 3D engine. And the JavaFX canvas API is really impressive, it has everything a game needs (drawing images, shapes, and even pixels). As Android and Java are so related, it will also allow me to port my code to mobile.

{{< gallery >}}
minecraft-trees.png
minecraft-water.png
3dgraphics-970x1024.png
{{< /gallery >}}

***Java and JavaFX on mobile is indeed a very nice approach and doesn't get the amount of attention it deserves. I guess you know the work Gluon is doing on this topic? If not, please take a look at [their docs](https://docs.gluonhq.com/#platforms_android).***

As JavaFX is Java-based, I always asked myself if it's possible to run a JavaFX application on Android.

So one day, I randomly discovered ["JavaFXPorts"](https://gluonhq.com/products/mobile/javafxports/). I'm aware of Gluon, but as it's not entirely free, I started compiling JavaFX applications for Android myself.

I tried with the JavaFXPorts Gradle plugin and some code changes to make the application work also on newer Android versions (the plugin is outdated right now).

***I saw on Twitter that you shared a problem you were having with your 3D engine, and [Almas Baim advised you to check with VisualVM what code was blocking your rendering](https://twitter.com/orango_mango/status/1642571365210378242). How did that work out? Was it difficult to set up and find the part of your code that was causing issues? And what problem did it reveal?***

The main problem of the 3D engine is performance. Coding and understanding the math is hard, but writing the engine in an efficient way is more complex. I worked for weeks and tried many ways to make the code as efficient as possible. As I'm developing the application on a Raspberry Pi, it's tough to make it lightweight.

In the end, as Almas Baim suggested, I used a profiler to see what was causing all the lag of the engine. I found out that the vertices of each cube were calculated 36 times instead of 8 (!). This issue occurred because each cube was constructed out of a list of 12 triangles (each triangle with 3 vertices, 12\*3=36), and some vertices were used more than once by many triangles.

So to fix this, I cached the calculated vertices and used the data to construct the other triangles of the cube. This could still be improved by also checking adjacent cubes (2 adjacent cubes share the same 4 vertices).

***You mentioned you are developing the 3D engine on a Raspberry Pi!? Is that an extra challenge to limit the computer power and see the impact on the 3D rendering? Can you share what computer and programs you use for your development?***

Yes, It's a challenging task to develop a 3D engine on a Raspberry Pi. I use a model 4B with 2GB of RAM.

Although Java uses a lot of memory, those 2GB are somehow fine for my 3D engine. As I have so little RAM, I need to write the code in a very efficient way and keep the window resolution as low as possible. My main IDE is [Geany](https://www.geany.org/), a very lightweight application optimized for a Raspberry Pi.

I actually have a laptop with Windows but I don't like it for coding. For that task, I prefer Linux and a Raspberry Pi isn't that expensive... So my setup is very simple, a Raspberry Pi and one screen.

{{< gallery >}}
desk-rpi-1024x768.jpg
desk-monitor-1024x768.jpg
{{< /gallery >}}

***As you learned all this on your own, can you share what was the most challenging part of getting started with Java and JavaFX?***

When I started learning Java, the most challenging part was to get familiar with the curly brackets, for loops, and all the class names (for example, the java.util package) as I came from Python. When I knew all the essential things of Java, I searched for a GUI API for Java that was similar to the Tkinter module of Python (I was familiar with the canvas API of Python, which is similar to the JavaFX one).

I found Swing and JavaFX. I chose JavaFX instead of Swing because JavaFX basics were well explained in a book I was reading about Java, and the Swing syntax was a little bit more confusing compared to the JavaFX one.

Unfortunately, in this book, the canvas API was not explained, so I had to learn it by myself, as it's essential for 2D game development.

***What would be your ideal career?***

I would like to be a Java game developer or anything else coding-related!

***With your current projects, I'm convinced that will work out as you already collected a very impressive curriculum vitae! I'm looking forward to your future projects. Thanks a lot for this interview*.**
