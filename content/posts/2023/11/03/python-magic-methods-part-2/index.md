---
title: "Python \"magic\" methods - part 2"
date: "2023-11-03T09:15:44+00:00"
lastmod: "2023-11-03T09:15:46+00:00"
description: "Let's continue our exploration of Python's magic methods in this second part of the series."
canonical: "https://blog.frankel.ch/python-magic-methods/2/"
authors:
  - "nicolas-frankel"
image: "pexels-benni-fish-9468322.jpg"
categories:
  - "Tutorials"
related_posts:
  - "the-maze-of-python-dependency-management"
  - "java-panama-polyglot-part-3"
  - "compiling-java-code-executing-bytecode"
  - "python-magic-methods-part-1"
frozen: false
---

Let's continue our exploration of Python's magic methods in this second part of the series. This part will focus on numbers and containers, *i.e.* , collections. You can read the first part [here](https://foojay.io/today/python-magic-methods-part-1/).

## Container-related methods

Python provides the usual containers, *e.g.*, lists, sets, and dictionaries. You can use the following methods when you want to implement your own.

### Common methods

Containers have a size. Python defines two methods to implement to return the number of items in a container: `object.__len__(self)` for the exact size and `object.__length_hint__(self)` for an approximation. You should use the latter when getting the exact size is computationally expensive.

### Item-related methods

Containers contain objects. Some containers offer index-based access, *e.g.* , `list(1)`, while others offer key-based access, *e.g.* , `dict('mykey')`. In both cases, here are the methods to implement:

|                 Method                 |                               Functionality                               |
|----------------------------------------|---------------------------------------------------------------------------|
| `object.__getitem__(self, key)`        | Get the object                                                            |
| `object.__setitem__(self, key, value)` | Set the object                                                            |
| `object.__delitem__(self, key)`        | Remove the object                                                         |
| `object.__missing__(self, key)`        | Called when the key is not found by the default `get(key)` implementation |
| ` object.__iter__(self)`               | Return an iterator over items (or keys) in the container                  |
| `object.__reversed__(self)`            | Reverse the objects in the container                                      |
| `object.__contains__(self, item)`      | Check whether an item is part of the container                            |

Let's create a simple hash-map-like container for illustration purposes:

```python
class Container:

  def __init__(self):
      self.items = {}

  def __getattribute__(self, name):
      raise AttributeError()

  def __len__(self):
      return len(self.items)                           #1

  def __setitem__(self, key, value):
      self.items[key] = value                          #1

  def __getitem__(self, key):
      return self.items[key]                           #1

  def __delitem__(self, key):
      return self.items.pop(key)                       #1

  def __contains__(self, key):
      return key in self.items                         #2

  def __iter__(self):
      return iter(self.items.keys())                   #3

  def __reversed__(self):
      return iter(reversed(self.items.keys()))         #4

container = Container()
container['foo'] = 'foo'
container['bar'] = 'bar'
print(len(container))                                  #5
for x in container:                                    #6
    print(f'{x}: {container[x]}')
print('---')
for x in reversed(container):                          #7
    print(f'{x}: {container[x]}')
print('---')
del container['foo']
for x in container:                                    #8
    print(f'{x}: {container[x]}')
print('---')
print('foo' in container)                              #9
```

1. Delegate on the `items` dictionary
2. Check if the key belongs to `items`
3. Get the keys' iterator
4. Get the reversed key's iterator
5. Print 2 as the container has two items at this point
6. Implicitly calls the `__iter__()` method
7. Implicitly calls the `__reversed__()` method
8. Print `bar: bar` since the `foo` key has been deleted
9. Implicitly calls the `__contains__()` method

## Number-related methods

Just as we can emulate containers, we can emulate numbers as well.

### Arithmetic methods

Arithmetic methods abound; it's easier to summarize them in a table:

|    Kind     |                  Method                  | Operator/function |            Comment            |
|     All     |
|     All     |      `object.__add__(self, other)`       |        `+`        |                               |
|     All     |      `object.__sub__(self, other)`       |        `-`        |                               |
|     All     |      `object.__mul__(self, other)`       |        `*`        |                               |
|     All     |     `object.__matmul__(self, other)`     |        `@`        |     Matrix multiplication     |
|     All     |    `object.__truediv__(self, other)`     |        `/`        |       Regular division        |
|     All     |    `object.__floordiv__(self, other)`    |       `//`        | Division without the reminder |
|     All     |      `object.__mod__(self, other)`       |        `%`        |   Reminder of the division    |
|     All     |     `object.__divmod__(self, other)`     |    `divmod()`     |                               |
|     All     | `object.__pow__(self, other[, modulo])`  |      `pow()`      |                               |
|     All     |     `object.__lshift__(self, other)`     |       `<<`        |                               |
|     All     |     `object.__rshift__(self, other)`     |       `>>`        |                               |
|     All     |      `object.__and__(self, other)`       |        `&`        |                               |
|     All     |      `object.__xor__(self, other)`       |        `^`        |        Exclusive `OR`         |
|     All     |       `object.__or__(self, other)`       |        `|`        |        Inclusive `OR`         |
|   Binary    |
|   Binary    |      `object.__radd__(self, other)`      |        `+`        |                               |
|   Binary    |      `object.__rsub__(self, other)`      |        `-`        |                               |
|   Binary    |      `object.__rmul__(self, other)`      |        `*`        |                               |
|   Binary    |    `object.__rmatmul__(self, other)`     |        `@`        |                               |
|   Binary    |    `object.__rtruediv__(self, other)`    |        `/`        |                               |
|   Binary    |   `object.__rfloordiv__(self, other)`    |       `//`        |                               |
|   Binary    |      `object.__rmod__(self, other)`      |        `%`        |                               |
|   Binary    |    `object.__rdivmod__(self, other)`     |    `divmod()`     |                               |
|   Binary    | `object.__rpow__(self, other[, modulo])` |      `pow()`      |                               |
|   Binary    |    `object.__rlshift__(self, other)`     |       `<<`        |                               |
|   Binary    |    `object.__rrshift__(self, other)`     |       `>>`        |                               |
|   Binary    |      `object.__rand__(self, other)`      |        `&`        |                               |
|   Binary    |      `object.__rxor__(self, other)`      |        `^`        |                               |
|   Binary    |      `object.__ror__(self, other)`       |        `|`        |                               |
| Assignement |
| Assignement |      `object.__iadd__(self, other)`      |       `+=`        |                               |
| Assignement |      `object.__isub__(self, other)`      |       `-=`        |                               |
| Assignement |      `object.__imul__(self, other)`      |       `*=`        |                               |
| Assignement |    `object.__imatmul__(self, other)`     |       `@=`        |                               |
| Assignement |    `object.__itruediv__(self, other)`    |       `/=`        |                               |
| Assignement |   `object.__ifloordiv__(self, other)`    |       `//=`       |                               |
| Assignement |      `object.__imod__(self, other)`      |       `%=`        |                               |
| Assignement | `object.__ipow__(self, other[, modulo])` |     `pow()=`      |                               |
| Assignement |    `object.__ilshift__(self, other)`     |       `<<=`       |                               |
| Assignement |    `object.__irshift__(self, other)`     |       `>>=`       |                               |
| Assignement |      `object.__iand__(self, other)`      |       `&=`        |                               |
| Assignement |      `object.__ixor__(self, other)`      |       `^=`        |                               |
| Assignement |      `object.__ior__(self, other)`       |       `|=`        |                               |
|    Unary    |
|    Unary    |          `object.__neg__(self)`          |        `-`        |                               |
|    Unary    |          `object.__pos__(self)`          |        `+`        |                               |
|    Unary    |          `object.__abs__(self)`          |      `abs()`      |        Absolute value         |
|    Unary    |        `object.__invert__(self)`         |        `~`        |         Bitwise `NOT`         |
|-------------|------------------------------------------|-------------------|-------------------------------|

Imagine an e-commerce site with products and stocks of them dispatched in warehouses. We need to subtract stock levels when someone orders and add stock levels when the stock is replenished. Let's implement the latter with some of the methods we've seen so far:

```python
class Warehouse:                                       #1

  def __init__(self, id):
    self.id = id

  def __eq__(self, other):                             #2
    if not isinstance(other, Warehouse):
      return False
    return self.id == other.id

  def __repr__(self):                                  #3
    return f'Warehouse(id={self.id})'

class Product:                                         #1

  def __init__(self, id):
    self.id = id

  def __eq__(self, other):                             #2
    if not isinstance(other, Product):
      return False
    return self.id == other.id

  def __repr__(self):                                  #3
    return f'Product(id={self.id})'

class StockLevel:

  def __init__(self, product, warehouse, quantity):
    self.product = product
    self.warehouse = warehouse
    self.quantity = quantity

  def __add__(self, other):                            #4
    if not isinstance(other, StockLevel):
      raise Exception(f'{other} is not a StockLevel')
    if self.warehouse != other.warehouse:
      raise Exception(f'Warehouse are not the same {other.warehouse}')
    if self.product != other.product:
      raise Exception(f'Product are not the same {other.product}')
    return StockLevel(self.product, self.warehouse,\
                      self.quantity + other.quantity)  #5

  def __repr__(self):
    return f'StockLevel(warehouse={self.warehouse},\
             product={self.product},quantity={self.quantity})'

warehouse1 = Warehouse(1)
warehouse2 = Warehouse(2)
product = Product(1)                                   #6
product1 = Product(1)                                  #6
stocklevel111 = StockLevel(product, warehouse1, 1)     #7
stocklevel112 = StockLevel(product, warehouse1, 2)     #7
stocklevel121 = StockLevel(product1, warehouse2, 1)    #7

print(stocklevel111 + stocklevel112)                   #8

stocklevel111 + stocklevel121                          #9
```

1. Define necessary classes
2. Override equality to compare ids
3. Override representation
4. Implement addition. If the warehouse and product don't match, raise an exception.
5. Create a new `StockLevel` with the same product and warehouse and the quantity as the sum of both quantities
6. Define two products that point to the same id; it's the same product for equality purposes
7. Create new stock-level objects
8. Print `StockLevel(warehouse=Warehouse(id=1),product=Product(id=1),quantity=3)`
9. Raise an exception as warehouses are different, though products are the same

### Conversion methods

Conversion methods allow changing an instance to a numeric type, *i.e.* , `int`, `float`, or `complex`.

|           Method           | Built-in function |
|----------------------------|-------------------|
| `object.__complex__(self)` | `complex()`       |
| `object.__int__(self)`     | `int()`           |
| `object.__float__(self)`   | `float()`         |

If no such method is implemented, Python falls back to the `object.__index__(self)`, for example, when using the instance as an index.

The following sample, however irrelevant it is, highlights the above:

```python
class Foo:

  def __init__(self, id):
    self.id = id

  def __index__(self):                                 #1
    return self.id

foo = Foo(1)
array = ['a', 'b', 'c']
what = array[foo]                                      #2
print(what)                                            #3
```

1. Define the fallback method
2. Coerce `foo` into an `int`. We didn't implement any conversion method; Python falls back to `index()`
3. Print `b`

### Other methods

Finally, Python delegates to a magic method when your code calls a specific number-related function.

|               Method                | Built-in function |
|-------------------------------------|-------------------|
| `object.__round__(self[, ndigits])` | `round()`         |
| `object.__trunc__(self)`            | `trunc()`         |
| `object.__floor__(self)`            | `floor()`         |
| `object.__ceil__(self)`             | `ceil()`          |

## Context managers' methods

Python's context managers allow fine-grained control over resources that must be acquired and released. It works with the `with` keyword. For example, here's how you open a file to write to:

```python
with open('file', 'w') as f:                           #1
    f.write('Hello world!')
                                                       #2
```

1. Open the file
2. At this point, Python has closed the file

A context manager is syntactic sugar. The following code is equivalent to the one from above:

```python
f = open('file', 'w')
try:
  f.write('Hello world!')
finally:
  f.close()
```

To write your context manager requires to implement two methods: one for opening the context and one for closing it, respectively, `object.__enter__(self)` and `object.__exit__(self, exc_type, exc_value, traceback)`.

Let's write a context manager to manage a pseudo-connection.

```python
import traceback

class Connection:

  def __enter__(self):
    self.connection = Connection()
    return self.connection

  def __exit__(self, exc_type, exc_value, exc_traceback):
    self.connection = None
    if exc_type is not None:
      print('An exception happened')
      print(traceback.format_exception(exc_type, exc_value, exc_traceback))
    return True

  def do_something(self):
    pass

with Connection() as connection:
  connection.do_something()
```

## Callable objects

I was first exposed to callable objects in Kotlin. A callable object looks like a function but is an object:

```python
hello = Hello()
hello('world')
```

The method to implement to make the above code run is `object.__call__(self[, args...])`.

```python
class Hello:

  def __call__(self, who):
    print(f'Hello {who}!')
```

## Conclusion

The post concludes our 2-part series on Python "magic" methods. I didn't mention some of them, though, as they are so many. However, they cover the majority of them.

Happy Python!

**To go further:**

* [Special method names](https://docs.python.org/3/reference/datamodel.html#special-method-names)
* [PEP 560 – Core support for typing module and generic types](https://peps.python.org/pep-0560/)

*Originally published at [A Java Geek](https://blog.frankel.ch/python-magic-methods/2/) on October 22^nd^, 2023*
