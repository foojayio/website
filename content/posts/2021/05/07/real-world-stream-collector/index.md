---
title: "A Real-World Example of a Stream Collector"
date: "2021-05-07T07:34:45+00:00"
lastmod: "2022-09-04T13:41:55+00:00"
description: "Java Stream's Collectors methods fit most use-cases. Let's imagine an e-commerce platform that implements a shopping cart."
canonical: "https://blog.frankel.ch/real-world-stream-collector/"
authors:
  - "nicolas-frankel"
image: "pexels-joshua-woroniecki-2607956.jpg"
categories:
  - "Use Cases"
related_posts:
  - "introducing-boxlang-ai-explorer-a-local-catalog-for-every-ai-pattern"
  - "boxlang-rss-full-featured-rss-atom-feed-module-for-boxlang"
  - "get-started-with-allocation-profiling"
  - "beginners-guide-to-java-profiler"
frozen: false
---

Java Stream's `Collectors` methods fit most use-cases. They allow returning either a `Collection` or a scalar. For the former, you use one of the `toXXX()` method, for the latter, one of the `reducing()` one.

Let's imagine an e-commerce platform that implements a shopping cart. The cart is modeled as the following:

{{< img src="cart-700x332.png" class="aligncenter size-medium" width="700" height="332" >}}

This diagram might translate into the following (abridged) code:

```java
public class Product {

    private final Long id;                           // 1
    private final String label;                      // 1
    private final BigDecimal price;                  // 1

    public Product(Long id, String label, BigDecimal price) {
        this.id = id;
        this.label = label;
        this.price = price;
    }

    @Override
    public boolean equals(Object object ) { ... }    // 2

    @Override
    public int hashCode() { ... }                    // 2
}
```

1. Getters
2. Only depend on `id`

```java
public class Cart {

    private final Map<Product, Integer> products = new HashMap<>(); // 1

    public void add(Product product) {
        add(product, 1);
    }

    public void add(Product product, int quantity) {
        products.merge(product, quantity, Integer::sum);
    }

    public void remove(Product product) {
        products.remove(product);
    }

    public void setQuantity(Product product, int quantity) {
        products.put(product, quantity);
    }

    public Map<Product, Integer> getProducts() {
        return Collections.unmodifiableMap(products);               // 2
    }
}
```

1. Organize products into a map. The key is the `Product`; the value is the quantity.
2. Remember to return a read-only copy of the collection to maintain encapsulation.

Once we have defined how we store data in memory, we need to design how to display the cart on-screen. We know that the checkout screen needs to show two different bits of information:

* The list of rows with the price for each row, *i.e.*, the price per product times the quantity.
* The overall price of the cart.

Here's the corresponding code:

```java
public record CartRow(Product product, int quantity) {                // 1

    public CartRow(Map.Entry<Product, Integer> entry) {
        this(entry.getKey(), entry.getValue());
    }

    public BigDecimal getRowPrice() {
        return product.getPrice().multiply(new BigDecimal(quantity));
    }
}
```

1. `CartRow` is a value object.  
   We can model it as a Java 16 `record`.

```java
var rows = cart.getProducts()
    .entrySet()
    .stream()
    .map(CartRow::new)
    .collect(Collectors.toList());                                    // 1

var price = cart.getProducts()
    .entrySet()
    .stream()
    .map(CartRow::new)
    .map(CartRow::getRowPrice)                                        // 2
    .reduce(BigDecimal.ZERO, BigDecimal::add);                        // 3
```

1. Collect the list of rows.
2. Compute the price for each row.
3. Compute the total price.

One of the main limitations of Java streams is that **you can only consume them once** . The reason is that streamed objects are not necessarily immutable (though they can be). Hence, executing the same stream twice might not be *idempotent*.

Therefore, to get both the rows and the price, we need to create two streams from the cart. From one stream, we will get the rows and from the other the price.  

This is not the way.

We want to collect both rows and the price from a single stream. We need a custom `Collector` that returns both in one pass as a single object.

```java
public class PriceAndRows {

    private BigDecimal price;                              // 1
    private final List<CartRow> rows = new ArrayList<>();  // 2

    PriceAndRows(BigDecimal price, List<CartRow> rows) {
        this.price = price;
        this.rows.addAll(rows);
    }

    PriceAndRows() {
        this(BigDecimal.ZERO, new ArrayList<>());
    }
}
```

1. Total cart price.
2. List of cart rows that can display the product's label, the product's price, and the row price.

Here's a summary of the `Collector` interface. For more details, please check [this previous post](https://blog.frankel.ch/custom-collectors-java-8/).

{{< img src="class-diagram-700x473.png" class="aligncenter size-medium" width="700" height="473" >}}

|      Interface      |                                                 Description                                                  |
|---------------------|--------------------------------------------------------------------------------------------------------------|
| `supplier()`        | Supply the base object to start from                                                                         |
| `accumulator()`     | Describe how to accumulate the current streamed item to the container                                        |
| `combiner()`        | If the stream is parallel, describe how to merge them                                                        |
| `finisher()`        | If the mutable container type is not the returned type, describe how to transform the former into the latter |
| `characteristics()` | Provide meta-data to optimize the stream                                                                     |

Given this, we can implement the `Collector` accordingly:

```java
private static class PriceAndRowsCollector
    implements Collector<Map.Entry<Product, Integer>, PriceAndRows, PriceAndRows> {

    @Override
    public Supplier<PriceAndRows> supplier() {
        return PriceAndRows::new;                                                // 1
    }

    @Override
    public BiConsumer<PriceAndRows, Map.Entry<Product, Integer>> accumulator() {
        return (priceAndRows, entry) -> {                                        // 2
            var row = new CartRow(entry);
            priceAndRows.price = priceAndRows.price.add(row.getRowPrice());
            priceAndRows.rows.add(row);
        };
    }

    @Override
    public BinaryOperator<PriceAndRows> combiner() {
        return (c1, c2) -> {                                                     // 3
            c1.price = c1.price.add(c2.price);
            var rows = new ArrayList<>(c1.rows);
            rows.addAll(c2.rows);
            return new PriceAndRows(c1.price, rows);
        };
    }

    @Override
    public Function<PriceAndRows, PriceAndRows> finisher() {
        return Function.identity();                                              // 4
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(Characteristics.IDENTITY_FINISH);                          // 4
    }
}
```

1. The mutable container is an instance of `PriceAndRows`.
2. For each map entry containing the product and the quantity, accumulate both into the `PriceAndRows`.
3. Two `PriceAndRows` can be combined by summing their total price and aggregating their respective rows.
4. The mutable container can be returned as-is.

Designing the `Collector` is a bit involved, but using the custom collector is as easy as:

```java
var priceAndRows = cart.getProducts()
                       .entrySet()
                       .stream()
                       .collect(new PriceAndRowsCollector());
```

## Conclusion

You can solve most use cases with one of the out-of-the-box collectors provided in the `Collectors` class. However, some require to implement a custom `Collector`, *e.g.*, when you need to collect more than a single collection or a single scalar.

While it may seem complicated if you never developed one before, it's not. You only need a bit of practice. I hope this post might help you with it.

You can find the source code of this post on [GitHub](https://github.com/ajavageek/custom-collector/) in Maven format.

**To go further:**

* [Custom collectors in Java 8](https://blog.frankel.ch/custom-collectors-java-8/)
* [Collector Javadocs](https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/util/stream/Collector.html)

*Originally published at [A Java Geek](https://blog.frankel.ch/real-world-stream-collector/) on May 2^nd^, 2021*
