---
title: "Python \"magic\" methods - part 1"
slug: "python-magic-methods-part-1"
date: "2023-11-03T09:08:42+00:00"
lastmod: "2023-11-03T09:18:37+00:00"
description: "Java was the first language I used professionally and is the scale by which I measure other languages I learned afterward. Hence, Python feels a bit weird because of its dynamic typing approach."
canonical: "https://blog.frankel.ch/python-magic-methods/1/"
authors:
  - "nicolas-frankel"
image: "pexels-benni-fish-9468322.jpg"
categories:
  - "Tutorials"
tags:
related_posts:
  - "the-maze-of-python-dependency-management"
  - "java-panama-polyglot-part-3"
  - "compiling-java-code-executing-bytecode"
  - "python-magic-methods-part-2"
enlighterjs: true
frozen: false
---

Java was the first language I used professionally and is the scale by which I measure other languages I learned afterward. It's an statically-typed language. Hence, Python feels a bit weird because of its dynamic typing approach.

For example, `Object` offers methods `equals()`, `hashCode()`, and `toString()`. Because all other classes inherit from `Object`, directly or indirectly, all objects have these methods by definition.

Conversely, Python was not initially built on OOP principles and is dynamically typed. Yet, any language needs cross-cutting features on unrelated objects. In Python, these are specially-named methods: methods that the runtime interprets in a certain way but that you need to know about. You can call them magic methods.

The documentation is pretty exhaustive, but it needs examples for beginners. The goal of this post is to list most of these methods and provide these examples so that I can remember them. I've divided it into two parts to make it more digestible.

Lifecycle methods
-----------------

Methods in this section are related to the lifecycle of new objects.

### `object.__new__(cls[, ...])`

The `__new()__` method is static, though it doesn't need to be explicitly marked as such. The method **must** return a new object instance of type `cls`; then, the runtime will call the `__init__()` (see below) method on the new instance.

`__new__()` is meant to customize instance creation of subclasses of immutable classes.

```python
class FooStr(str):                                     #1

    def __new__(cls, value):
        return super().__new__(cls, f'{value}Foo')     #2

print(FooStr('Hello'))                                 #3
```


1. Inherit from `str`
2. Create a new `str` instance, whose value is the value passed to the constructor, suffixed with `Foo`
3. Print `HelloFoo`

### `object.__init__(self[, ...])`

`__init__()` is the regular initialization method, which you probably know if you've read any basic Python tutorial. The most significant difference with Java is that the superclass `__init__()` method has no implicit calling. One can only wonder how many bugs were introduced because somebody forgot to call the superclass method.

`__init__()` differs from a constructor in that the object is already created.

```python
class Foo:

  def __init__(self, a, b, c):                         #1
    self.a = a                                         #2
    self.b = b                                         #2
    self.c = c                                         #2

foo = Foo('one', 'two', 'three')
print(f'a={foo.a}, b={foo.b}, c={foo.c}')              #3
```


1. The first parameter is the instance itself
2. Initialize the instance
3. Print `a=one, b=two, c=three`

### `object.__del__(self)`

If `__init()__` is akin to an *initializer* , then `__del__()` is it's *finalizer* . As in Java, finalizers are unreliable, *e.g.*, there's no guarantee that the interpreter finalizes instances when it shuts down.

Representation methods
----------------------

Python offers two main ways to represent objects: one "official" for debugging purposes and the other "informal". You can use the former to reconstruct the object.

The official representation is expressed via the `object.__repr__(self)`. The documentation states that the representation must be "information-rich and unambiguous".

```python
class Foo:

  def __init__(self, a, b, c):
    self.a = a
    self.b = b
    self.c = c

  def __repr__(self):
    return f'Foo(a={foo.a}, b={foo.b}, c={foo.c})'

foo = Foo('one', 'two', 'three')
print(foo)                                             #1
```


1. Print `Foo(a=one, b=two, c=three)`

My implementation returns a `string`, though it's not required. Yet, you can reconstruct the object with the information displayed.

The `object.__str__(self)` handles the unofficial representation. As its name implies, it must return a `string`. The default calls `__repr__()`.

Aside from the two methods above, the `object.__format__(self, format_spec)` method returns a `string` representation of the object. The second argument follows the rules of the [Format Specification Mini-Language](https://docs.python.org/3/library/string.html#formatspec). Note that the method *must* return a `string`. It's a bit involved, so that I won't implement it.

Finally, the `object.__bytes__(self)` returns a byte representation of the object.

```python
from pickle import dumps                              #1

class Foo:

  def __init__(self, a, b, c):
    self.a = a
    self.b = b
    self.c = c

  def __repr__(self):
    return f'Foo(a={foo.a}, b={foo.b}, c={foo.c})'

  def __bytes__(self):
    return dumps(self)                                #2

foo = Foo('one', 'two', 'three')
print(bytes(foo))                                     #3
```


1. Use the [pickle](https://docs.python.org/3/library/pickle.html) serialization library
2. Delegage to the [dumps()](https://docs.python.org/3/library/pickle.html#pickle.dumps) method
3. Print the byte representation of `foo`

Comparison methods
------------------

Let's start with similarities with Java: Python has two methods `object.__eq__(self, other)` and `object.__hash__(self)` that work in the same way. If you define `__eq__()` for a class, you **must** define `__hash__()` as well. Contrary to Java, if you don't define the former, you *must not* define the latter.

```python
class Foo:

  def __init__(self, a, b):
    self.a = a
    self.b = b

  def __eq__(self, other):
    if not isinstance(other, Foo):                    #1
      return false
    return self.a == other.a and self.b == other.b    #2

  def __hash__(self):
      return hash(self.a + self.b)                    #3

foo1 = Foo('one', 'two')
foo2 = Foo('one', 'two')
foo3 = Foo('un', 'deux')

print(hash(foo1))
print(hash(foo2))
print(hash(foo3))

print(foo1 == foo2)                                   #4
print(foo2 == foo3)                                   #5
```


1. Objects that are not of the same type are not equal by definition
2. Compare the equality of attributes
3. The hash consists of the addition of the two attributes
4. Print `True`
5. Print `False`

As in Java, `__eq__()__` and `__hash__()` have plenty of gotchas. Some of them are the same, others not. I won't paraphrase the documentation; have a look at it.

Other comparison methods are pretty self-explanatory:

|            Method            | Operator |
|------------------------------|----------|
| `object.__lt__(self, other)` | `<`      |
| `object.__le__(self, other)` | \`\`     |
| `object.__ge__(self, other)` | `>=`     |
| `object.__ne__(self, other)` | `!=`     |

```python
class Foo:

  def __init__(self, a):
    self.a = a

  def __ge__(self, other):
    return self.a >= other.a                          #1

  def __le__(self, other):
    return self.a <= other.a                          #1

foo1 = Foo(1)
foo1 = Foo(1)
foo2 = Foo(2)

print(foo1 >= foo1)                                   #2
print(foo1 >= foo2)                                   #3
print(foo1 <= foo1)                                   #4
print(foo2 <= foo2)                                   #5
```


1. Compare the single attribute
2. Print `True`
3. Print `False`
4. Print `True`
5. Print `True`

Note that comparison methods may return something other than a boolean. In this case, Python will transform the value in a boolean using the `bool()` function. I advise you not to use this implicit conversion.

Attribute access methods
------------------------

As seen above, Python allows accessing an object's attributes via the dot notation. If the attribute doesn't exist, Python complains: `'Foo' object has no attribute 'a'`. However, it's possible to define *synthetic* accessors on a class, via the `object.__getattr__(self, name)` and `object.__setattr__(self, name, value)` methods. The rule is that they are fallbacks: if the attribute doesn't exist, Python calls the method.

```python
class Foo:

  def __init__(self, a):
    self.a = a

  def __getattr__(self, attr):
    if attr == 'a':
      return 'getattr a'                              #1
    if attr == 'b':
      return 'getattr b'                              #2

foo = Foo('a')

print(foo.a)                                          #3
print(foo.b)                                          #4
print(foo.c)                                          #5
```


1. Return the string if the requested attribute is `a`
2. Return the string if the requested attribute is `b`
3. Print `a`
4. Print `getattr b`
5. Print `None`

For added fun, Python also offers the `object.__getattribute__(self, name)`. The difference is that it's called **whether the attribute exists or not**, effectively shadowing it.

```python
class Foo:

  def __init__(self, a):
    self.a = a

  def __getattribute__(self, attr):
    if attr == 'a':
      return 'getattr a'                              #1
    if attr == 'b':
      return 'getattr b'                              #2

foo = Foo('a')

print(foo.a)                                          #3
print(foo.b)                                          #4
print(foo.c)                                          #5
```


1. Return the string if the requested attribute is `a`
2. Return the string if the requested attribute is `b`
3. Print `getattr a`
4. Print `getattr b`
5. Print `None`

The `dir()` function allows returning an object's list of attributes and methods. You can set the list using the `object.__dir__(self)__` method. By default, the list is empty: you need to set it explicitly. Note that it's the developer's responsibility to ensure the list contains actual class members.

```python
class Foo:

  def __init__(self, a):
    self.a = 'a'

  def __dir__(self):                                  #1
    return ['a', 'foo']

foo = Foo('one')

print(dir(foo))                                       #2
```


1. Implement the method
2. Display `['a', 'foo']`; Python sorts the list. Note that there's no `foo` member, though.

Descriptors
-----------

Python descriptors are accessors delegates, akin to Kotlin's [delegated properties](https://kotlinlang.org/docs/delegated-properties.html). The idea is to factor a behavior somewhere so other classes can reuse it. In this way, they are the direct consequence of favoring composition over inheritance. They are available for getters, setters, and finalizers, respectively:

* `object.__get__(self, instance, owner=None)`
* `object.__set__(self, instance, value)`
* `object.__delete__(self, instance)`

Let's implement a *lazy* descriptor that caches the result of a compute-intensive operation.

```python
class Lazy:                                           #1

  def __init__(self):
    self.cache = {}                                   #2

  def __get__(self, obj, objtype=None):
    if obj not in self.cache:
      self.cache[obj] = obj._intensiveComputation()   #3
    return self.cache[obj]

class Foo:

  lazy = Lazy()                                       #4

  def __init__(self, name):
    self.name = name
    self.count = 0                                    #5

  def _intensiveComputation(self):
    self.count = self.count + 1                       #6
    print(self.count)                                 #7
    return self.name

foo1 = Foo('foo1')
foo2 = Foo('foo2')

print(foo1.lazy)                                      #8
print(foo1.lazy)                                      #8
print(foo2.lazy)                                      #9
print(foo2.lazy)                                      #9
```


1. Define the descriptor
2. Initialize the cache
3. Call the intensive computation.

Conclusion
----------

This concludes the first part of Python magic methods. The [second part](https://foojay.io/today/python-magic-methods-part-2/) will focus on class, container, and number-related methods.



*Originally published at [A Java Geek](https://blog.frankel.ch/python-magic-methods/1/) on October 15^th^, 2023*

*[OOP]: Object-Oriented Programming
