---
title: "On inheritance and subtyping"
date: "2025-01-26T12:45:07+00:00"
lastmod: "2026-09-21T06:03:12+00:00"
description: "Java is the first language I learned in my career. Its structure is foundational in my early years of understanding programming concepts. After going…"
canonical: "https://blog.frankel.ch/on-inheritance/"
authors:
  - "nicolas-frankel"
image: "duck-meet-4127713.jpg"
categories:
  - "Opinion"
related_posts:
  - "on-cosmetics-vs-intrinsics-programming"
  - "three-mistakes-junior-software-developers-make-preventing-getting-hired-on-amazing-projects"
frozen: false
---

Java is the first language I learned in my career. Its structure is foundational in my early years of understanding programming concepts. After going through several other languages with very different approaches, I've widened my point of view. Today, I want to reflect on the idea of *inheritance*.

## Inheritance in Java

In Java, the idea of *inheritance* is tightly coupled with the concept of *subtyping* . Subtyping is the implementation of a *IS A* relationship. For example, the `Rabbit` class is a subtype of the `Mammal` class. Henceforth, a `Rabbit` instance has all the *behaviour* of a `Mammal`: it inherits the behaviour.

Because of this, you can pass a `Rabbit` instance when a method calls for a `Mammal` parameter or return a `Rabbit` instance when a method return type is `Mammal`. If you've learned Java, .Net, or anything remotely similar, that's how you see inheritance, and it becomes the new normal.

It is **explicit inheritance**.

```java
class Animal {
    void feed();
}

class Rabbit extends Animal {                     //1
}
```

1. Because a `Rabbit` `IS A` `Animal`, it can `feed()`

## Inheritance in Go

When I first looked at Go, I was amazed that it does not have subtyping while still providing inheritance. Go uses duck typing:
> If it looks like a duck, swims like a duck, and quacks like a duck, then it probably is.

If a Go `struct` implements the same functions as an interface, it **implicitly implements** the interface.

```go
type Animal interface {
    feed()                                        //1
}

type Rabbit struct {
}

func (rabbit Rabbit) feed() {                     //2
    // feeds
}
```

1. An `Animal` can feed
2. Because a `feed()` function exists that takes a `Rabbit` as a parameter, `Rabbit` implements `Animal`

I do dislike Go for its error-handling approach, but I was of two minds about implicit implementation. On one side, I understood it was a new concept, and I tried to stay open-minded; on the other hand, I think things are always better explicit than implicit, either in software development or real life.

## Inheritance in Python

Python is the most interesting language I know of regarding inheritance.

Subtyping and type-based inheritance have been present in Python since its inception.

```python
class Animal:
    def feed(self):                               #1
        pass                                      #2

class Rabbit(Animal):                             #3
    pass
```

1. n `Animal` can feed
2. There are no abstract classes nor interfaces in Python, only classes
3. Because a `Rabbit` `IS A` `Animal`, it can `feed()`

In this regard, Python works the same as Java in terms of inheritance. Python also offers duck typing, which I described as [magic methods](https://blog.frankel.ch/python-magic-methods/). For example, to make something *iterable* , *e.g.* , that can return an *iterator* , you only need to implement `__iter__()` and `__next__()`:

```python
class SingleValueIterable():

    done = False

    def __init__(self, value):
        self.value = value

    def __iter__(self):                           #1
        return self

    def __next__(self):                           #1
        if self.done:
            raise StopIteration
        else:
            self.done = True
            return self.value

svi = SingleValueIterable(5)
sviter = iter(svi)                                #2

for x in sviter:
    print(x)                                      #3
```

1. Duck typing methods
2. Create an `Iterator` - Pythons knows how since we implemented the methods above
3. Print `5`

The problem with this duck typing approach is that it works only for Python's predefined magic methods. What if you want to offer a class that a third party could inherit from implicitly?

```python
class Animal:

    def feed():
        pass

class Rabbit:

    def feed():
        pass
```

In the above snippet, `Rabbit` is not an `Animal`, much to our chagrin. Enter [PEP 544](https://peps.python.org/pep-0544/), titled Protocols: Structural subtyping (static duck typing). The PEP solves the impossibility of defining magic methods for our classes. It defines a simple `Protocol` class: once you inherit from it, methods defined in the class become eligible for duck typing, hence the name - static duck typing.

```python
from typing import Protocol

class Animal(Protocol):                           #1

    def feed():                                   #2
        pass

class Rabbit:

    def feed():                                   #2
        pass

class VenusFlytrap:

    def feed():                                   #2
        pass
```

1. Inherit from `Protocol`
2. Because `Animal` is a `Protocol`, any class that defines `feed()` becomes an `Animal`, for better or worse

## Conclusion

Object-oriented programming, inheritance, and subtyping may have specific meanings that don't translate into other languages, depending on the first language you learn. Java touts itself as an language and offers the complete package. Go isn't an OO language, but it still offers subtyping via duck typing. Python offers both explicit and implicit inheritance but no interfaces.

You learn a new programming language by comparing it with the one(s) you already know. Knowing a language's features is key to writing idiomatic code in your target language. Familiarize yourself with features that don't exist in your known languages: they will widen your understanding of programming as a whole.

**Go further:**

* [Inheritance (object-oriented programming)](https://en.wikipedia.org/wiki/Inheritance_(object-oriented_programming))
* [Duck typing](https://en.wikipedia.org/wiki/Duck_typing)
* [Python Protocol](https://peps.python.org/pep-0544/)

*Originally published at [A Java Geek](https://blog.frankel.ch/on-inheritance/) on January 26th, 2025*

*[Object-Oriented]: OO
