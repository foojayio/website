---
title: "double, BigDecimal, or Fixed-Point? Precision, Performance, and Sane Choices for Numbers in Java"
slug: "double-bigdecimal-or-fixed-point-precision-performance-and-sane-choices-for-numbers-in-java"
date: "2026-06-24T07:38:07+00:00"
description: "There is an evergreen debate in the Java world: Should you always use MARKDOWN_HASHeb731dbabfc7392f8ad8c1cdb326a26aMARKDOWNHASH for money? The short - by Stefano Fago"
canonical: "https://blog.frankel.ch/bigdecimal-vs-double/"
authors:
  - "stefano-fago"
image: "https://foojay.io/wp-content/uploads/2026/06/cover_large-1.jpg"
categories:
  - "Java"
tags:
related_posts:
  - "7-habits-of-highly-effective-java-coding"
  - "does-language-still-matter-in-the-age-of-ai-yes-but-the-tradeoff-has-changed"
  - "effective-coding-with-java-observability"
  - "hard-things-computer-science"
enlighterjs: true
frozen: false
---

There is an evergreen debate in the Java world: *Should you always use* `MARKDOWN_HASHeb731dbabfc7392f8ad8c1cdb326a26aMARKDOWN`*HASH* for money?

The short answer is **no** . The real answer is: *it depends on your computational context: the precision you need, the rounding rules you must follow, and the performance budget you have.*

The problem is that this conversation is often driven by dogma rather than engineering. You hear statements like "`double` is broken," "`BigDecimal` is slow," "always use fixed-point," or "always use IEEE 754," each treated as an absolute truth. Reality is more nuanced and more interesting.

We start from what IEEE 754 actually does under the hood, move through `BigDecimal` pitfalls and fixed-point arithmetic, tour the libraries that solve these problems for you, and end with the production traps (serialization, testing, concurrency) that can quietly undo a good numeric choice. Sections include working code.

The floating-point problem {#h2-0-the-floating-point-problem}
-------------------------------------------------------------

Most floating-point surprises trace back to a single fact: Java's `float` and `double` use binary arithmetic, not decimal. Understanding why that matters, and when it doesn't, is the foundation for every choice in this post.

### IEEE 754 {#h3-1-ieee-754}

`float` and `double` in Java follow the IEEE 754 standard: `float` uses binary32, `double` uses binary64. The keyword here is *binary*. Values are stored as a signed bit, an exponent, and a mantissa, all in base 2. This means that many numbers that look trivially simple in base 10 have no exact representation in base 2.

Consider the classic example:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class FloatingPointProblem {
    public static void main(String[] args) {
        double x = 0.1 + 0.2;                              // 1
        System.out.println(x);          // 0.30000000000000004
        System.out.println(x == 0.3);   // false             // 2
    }
}</pre>

1. `0.1` cannot be represented exactly in binary; the compiler stores the nearest binary64 value
2. Equality check fails because of accumulated representation error

This does **not** mean that `double` is broken. It means that the decimal literal `0.1` cannot be represented exactly in binary, just as 1/3 cannot be represented exactly in decimal. When you write `0.1` in Java, the compiler stores the nearest binary64 value: `0.1000000000000000055511151231257827021181583404541015625`. Every subsequent operation compounds that tiny initial gap.

`double` is fast, compact, and purpose-built for numerical computation, but it is inherently approximate in the decimal sense. That is not a defect; it is a design trade-off, and in many domains it is exactly the right one.

### Naive equality checks {#h3-2-naive-equality-checks}

The most common floating-point bug is not imprecision itself but testing equality with `==`.

Never do this:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">if (result == expected) { ... }  // dangerous with floating-point</pre>

Instead, compare within a tolerance (often called *epsilon*):

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">boolean nearlyEqual(double a, double b, double epsilon) {
    return Math.abs(a - b) &lt;= epsilon;
}</pre>

This works well when your values live in a known range. But if the magnitudes vary widely (say from `0.0001` to `1_000_000`), a fixed epsilon is either too tight for large values or too loose for small ones. In that case, use a relative tolerance:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">boolean nearlyEqualRelative(double a, double b, double relTol) {
    double diff = Math.abs(a - b);
    double norm = Math.max(Math.abs(a), Math.abs(b));
    return diff &lt;= relTol * norm;
}</pre>

The choice between absolute and relative tolerance depends on your data. Scientific applications often use relative tolerance; financial rounding at display boundaries often uses a fixed number of decimal places. The point is: **understand your comparison strategy before you write a single `if`.**

### `strictfp`, the x87 FPU, and Java 17's silent fix {#h3-3-strictfp-the-x87-fpu-and-java-17-s-silent-fix}

Java 1.0 enforced strict IEEE 754 semantics, but Intel's x87 FPU computed intermediates in 80-bit extended precision, making strict mode slow. Java 1.2 introduced a compromise:

* *default* mode, which allowed extended exponent range, producing subtly different results across hardware)
* *strict* mode, exact IEEE 754, enabled with `strictfp`

Almost nobody used `strictfp`, so floating-point results could differ across platforms, exactly the kind of non-determinism that makes numerical debugging a nightmare.

[JEP 306](https://openjdk.org/jeps/306) in **Java 17** resolved this. Modern hardware (SSE2, AVX) supports strict IEEE 754 natively with no penalty, so JEP 306 restored *always-strict* semantics as the default. The `strictfp` modifier is now a no-op, accepted for backward compatibility, but with no effect.

The practical implication: **on Java 17+, floating-point arithmetic is fully reproducible across platforms.** If you still see `strictfp` in legacy code, it can safely be removed.

### The `NaN` and `-0.0` landmines {#h3-4-the-nan-and-0-0-landmines}

IEEE 754 defines two special values that break common assumptions about how numbers behave.

**`NaN` (Not a Number)** has a unique property: it is not equal to itself. This is mandated by the standard, and Java follows it faithfully:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">double nan = Double.NaN;
System.out.println(nan == nan);              // false            // 1
System.out.println(Double.compare(nan, nan)); // 0               // 2
System.out.println(Double.isNaN(nan));        // true</pre>

1. The `==` operator says `NaN != NaN`, mandated by IEEE 754
2. `Double.compare()` considers two `NaN` values as equal for sorting consistency

The `==` operator says `NaN != NaN`, but `Double.compare()` considers two `NaN` values as equal for sorting consistency, and `Double.valueOf(NaN).equals(Double.valueOf(NaN))` returns `true` for `HashMap` consistency. If you use `double` as logic keys or in conditional branches, always guard with `Double.isNaN()` first.

**`-0.0` (negative zero)** is the other trap:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">System.out.println(0.0 == -0.0);              // true            // 1
System.out.println(Double.compare(0.0, -0.0)); // 1 (0.0 &gt; -0.0) // 2
System.out.println(1.0 / 0.0);                // Infinity
System.out.println(1.0 / -0.0);               // -Infinity       // 3</pre>

1. Arithmetic equality says `0.0 == -0.0`
2. `Double.compare` and `Double.valueOf().equals()` distinguish them
3. Dividing by `-0.0` yields `-Infinity`, not `+Infinity`

The practical rule: **if your `double` values can be `NaN` or `-0.0`, test for them explicitly before using the values in collections, comparisons, or as divisors.**

When `double` is the right choice {#h2-5-when-double-is-the-right-choice}
-------------------------------------------------------------------------

Many developers reach for `BigDecimal` "just to be safe," even when approximation is perfectly acceptable. `double` is an excellent choice when:

**The error is tolerable:**   

If your result is a ranking score, a similarity metric, or a percentage displayed to one decimal place, a few ULPs of drift are irrelevant. (A *ULP* (Unit in the Last Place) is the smallest representable difference between two adjacent `double` values at a given magnitude. It is the natural unit of floating-point error.)

**The domain is naturally approximate:**   

Sensor readings, GPS coordinates, simulation outputs, and statistical aggregates are already noisy. Decimal exactness adds nothing.

**Performance matters:**   
`double` arithmetic maps directly to hardware FPU instructions. It is orders of magnitude faster than `BigDecimal`.

**Rounding happens at the boundaries:**   

You compute in `double` and round to two decimal places only when you display or persist. The interior computation needs speed, not decimal fidelity.

Here is a typical example, computing an average:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">double average(double[] values) {
    double sum = 0.0;
    for (double v : values) sum += v;
    return sum / values.length;
}</pre>

For a dozen values, this works perfectly. But what happens when you sum ten thousand, or ten million? Each addition introduces a rounding error of a few ULPs, and those errors accumulate. Over long sequences, the final result can drift significantly from the mathematically exact answer, not because `double` is wrong, but because naive summation is *numerically unstable* . The good news: you do not need to abandon `double` to fix this. The next section presents a toolkit of well-known algorithms that keep your computation in `double` while dramatically reducing accumulated error.

Reducing floating-point errors {#h2-6-reducing-floating-point-errors}
---------------------------------------------------------------------

The algorithms below all work on plain `double`, with no `BigDecimal` needed. The choice between them depends on your trade-off between accuracy, performance, and implementation complexity.

### Kahan Compensated Summation {#h3-7-kahan-compensated-summation}

The most famous technique. When you add a small number to a large running sum, the low-order bits of the small number get lost. Kahan summation keeps a separate *compensation* variable that tracks the accumulated rounding error and feeds it back into the next addition.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">double kahanSum(double[] values) {
    double sum = 0.0;
    double compensation = 0.0;                                   // 1
    for (double value : values) {
        double y = value - compensation;
        double t = sum + y;
        compensation = (t - sum) - y;                            // 2
        sum = t;
    }
    return sum;
}</pre>

1. Tracks the accumulated rounding error across iterations
2. Recovers the lost low-order bits, the core of the algorithm

The error bound is essentially independent of *n* , versus O(*n*) for naive summation. However, Kahan has a weakness: if the next term exceeds the running sum, the compensation logic fails. Neumaier's variant fixes this.

### Neumaier's improved Kahan-Babuška algorithm {#h3-8-neumaier-s-improved-kahan-babu-ka-algorithm}

Neumaier (1974) introduced a variant that handles the case where the new term is larger than the running sum, by checking the relative magnitudes and adjusting the compensation accordingly:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">double neumaierSum(double[] values) {
    double sum = 0.0;
    double compensation = 0.0;
    for (double value : values) {
        double t = sum + value;
        if (Math.abs(sum) &gt;= Math.abs(value)) {                  // 1
            compensation += (sum - t) + value;
        } else {
            compensation += (value - t) + sum;                   // 2
        }
        sum = t;
    }
    return sum + compensation;                                   // 3
}</pre>

1. Sum is bigger: low-order digits of value are lost
2. Value is bigger: low-order digits of the sum are lost
3. Correction applied once at the end

The practical difference: for the sequence `[1.0, 1e100, 1.0, -1e100]`, Kahan summation yields `0.0`, while Neumaier correctly returns `2.0`. This matters in real workloads where your data has values at very different scales: financial portfolios mixing micro-cent adjustments with large transfers, or scientific datasets spanning orders of magnitude.

### Pairwise summation {#h3-9-pairwise-summation}

Pairwise, or cascade, summation takes a completely different approach: recursively split the array in half, sum each half, then add the two results. The error grows as O(log *n* ), not as good as Kahan's O(1), but very close in practice, and with a crucial advantage: it requires the same number of arithmetic operations as naive summation and is naturally parallelizable. The implementation is a simple divide-and-conquer recursion with a small base case (typically 16-32 elements summed naively) to amortize recursion overhead. Pairwise summation is the default algorithm in NumPy and Julia for their `sum` functions, and is used internally in many FFT implementations.

### Fused multiply-add {#h3-10-fused-multiply-add}

Since Java 9, `Math.fma(a, b, c)` computes `a * b + c` with a single rounding step instead of two. This corresponds to the IEEE 754-2008 `fusedMultiplyAdd` operation. In a normal `a * b + c`, the multiplication rounds once and the addition rounds again, losing precision at each step. FMA computes the exact product internally and only rounds once when adding `c`.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">double standard = a * b + c;    // two rounding steps
double fma = Math.fma(a, b, c); // one rounding step, more accurate</pre>

FMA is particularly valuable for dot products, polynomial evaluation (Horner's method), and any linear combination where you are accumulating products. On hardware that supports FMA natively (most modern x86 and ARM CPUs), there is no performance penalty, often faster than the two-instruction sequence.

**JDK itself.** The Javadoc for `DoubleStream.sum()` explicitly states that the implementation "may be implemented using compensated summation or other technique to reduce the error bound." The order of addition is intentionally unspecified to allow flexibility. Similarly, `Collectors.summingDouble()` uses a compensation-based approach internally (a two-element array to track sum and compensation).

**Apache Commons Numbers.** The `Sum` class in `commons-numbers-core` implements the *Sum2S* and *Dot2S* algorithms (Ogita, Rump, Oishi, *SIAM J. Sci. Comput* , 2005), the most practical ready-to-use compensated arithmetic in the Java ecosystem. The companion `Precision` class provides production-ready epsilon and ULP comparison (see Apache Commons Numbers).

|                                        Algorithm                                        |    Error bound     | Cost vs naive |        Parallelizable        |            Best for            |
|-----------------------------------------------------------------------------------------|--------------------|---------------|------------------------------|--------------------------------|
| Naive summation                                                                         | O(*n*)             | 1x            | Yes                          | Quick-and-dirty                |
| Pairwise summation                                                                      | O(log *n*)         | \~1x          | Yes                          | General-purpose (NumPy, Julia) |
| Kahan compensated                                                                       | O(1)               | \~4x          | No (loop-carried dependency) | High-accuracy sequential       |
| Neumaier improved                                                                       | O(1)               | \~4x          | No                           | Mixed-magnitude data           |
| [Klein second-order](https://en.wikipedia.org/wiki/Kahan_summation_algorithm#Precision) | O(1), even tighter | \~6x          | No                           | Maximum accuracy               |
| Apache Commons `Sum` (Sum2S)                                                            | O(1)               | \~4x          | No                           | Production Java code           |

What `BigDecimal` actually solves {#h2-11-what-bigdecimal-actually-solves}
--------------------------------------------------------------------------

`BigDecimal` is not "more precise" in some absolute sense. It is **decimal** , **arbitrary-precision** , **controllable** , and **auditable**. Those four properties are what matter.

Internally, a `BigDecimal` is an unscaled integer value plus a scale:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">unscaled value = 1999, scale = 2  →  19.99</pre>

This means that `19.99` is stored as exactly `19.99`, not as the nearest binary approximation. Every arithmetic operation preserves decimal semantics, and you control the rounding mode explicitly at every step.

### Constructing `BigDecimal` from `double` {#h3-12-constructing-bigdecimal-from-double}

This is one of the most frequently encountered `BigDecimal` bugs:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">new BigDecimal(0.1);                                             // 1</pre>

1. Captures the inexact binary value; result is `0.100000000000000005551...`, not `0.1`

The result is `0.1000000000000000055511151231257827021181583404541015625`, because the constructor faithfully records the binary64 representation of `0.1`. If you want exact decimal semantics, construct from a `String` or use `valueOf`:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">new BigDecimal("0.1");     // exact: 0.1
BigDecimal.valueOf(0.1);   // also correct: uses Double.toString internally</pre>

### Controlled Rounding {#h3-13-controlled-rounding}

Where `BigDecimal` truly shines is in explicit rounding control. In tax, invoicing, and accounting, the rounding rule is not a suggestion; it is a legal requirement.

Computing VAT:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">BigDecimal vat = amount.multiply(rate)
                       .setScale(2, RoundingMode.HALF_UP);</pre>

Performing division (which often produces non-terminating decimals):

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">BigDecimal result = a.divide(b, 2, RoundingMode.HALF_UP);       // 1</pre>

1. Without specifying scale and rounding mode, `divide` throws `ArithmeticException` if the result is non-terminating (e.g., `1 / 3`)

Without specifying scale and rounding mode, `divide` will throw `ArithmeticException` if the result is non-terminating. This is by design: `BigDecimal` refuses to silently lose precision. You must be explicit about how many decimal places you want and how to round.

### The immutability trap {#h3-14-the-immutability-trap}

This may be the single most common `BigDecimal` bug in production code:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">BigDecimal amount = new BigDecimal("19.995");
amount.setScale(2, RoundingMode.HALF_UP);                        // 1
System.out.println(amount);                 // still 19.995</pre>

1. BUG: return value is discarded; `BigDecimal` is immutable, every method returns a **new** instance

`BigDecimal` is immutable. Every method (`setScale`, `add`, `multiply`, `divide`) returns a **new** instance. The original is never modified. Static analysis tools like SonarQube flag this (rule `S2201`), but it still slips through code reviews with surprising frequency. The fix is trivial: `amount = amount.setScale(2, RoundingMode.HALF_UP);`.

### The `equals()` vs `compareTo()` Trap {#h3-15-the-equals-vs-compareto-trap}

This bug appears constantly in enterprise code:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">BigDecimal a = new BigDecimal("2.0");
BigDecimal b = new BigDecimal("2.00");

System.out.println(a.equals(b));      // false                   // 1
System.out.println(a.compareTo(b));   // 0                       // 2</pre>

1. `equals()` compares both value **and** scale: `2.0` (scale 1) is not equal to `2.00` (scale 2)
2. `compareTo()` compares only the numeric value; use this for numerical equality

`equals()` compares both value **and** scale. Since `2.0` has scale 1 and `2.00` has scale 2, they are not equal according to `equals()`. This has consequences everywhere: `HashMap` keys, `Set` membership, assertions in tests. The rule is simple: **use `compareTo() == 0` for numerical equality of `BigDecimal` values.** If you must use `BigDecimal` as a `Map` key, normalize with `stripTrailingZeros()` first, but be aware that in older Java versions (prior to JDK 8u), `stripTrailingZeros()` on zero values had inconsistent behavior.

### Performance Ccost {#h3-16-performance-ccost}

`BigDecimal` operations allocate objects on the heap, perform arbitrary-precision integer arithmetic internally, and manage scale and precision metadata at every step. Compared to `double`, which compiles down to a single FPU instruction, the overhead is enormous. Benchmarks published by Peter Lawrey (Vanilla Java / Chronicle Software) have shown throughput differences of 100x or more for tight arithmetic loops.
> **Note:** Though the work is regularly cited on the Web, I couldn't find the original research.

**But this does not mean `BigDecimal` is wrong.** It means it has a real cost, and that cost must be justified by the domain. If you are computing invoices at 100 transactions per second, `BigDecimal` is perfectly fine. If you are running a matching engine processing millions of price updates per second, it is not.

Never use `System.nanoTime()` loops; JIT dead-code elimination, GC pauses, and branch prediction make them meaningless. Use :

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Benchmark
public void doubleCalc(Blackhole bh) {
    double result = (100.10 + 200.20) / 2.0;
    bh.consume(result);                                          // 1
}

@Benchmark
public void bigDecimalCalc(Blackhole bh) {
    BigDecimal a = new BigDecimal("100.10");
    BigDecimal b = new BigDecimal("200.20");
    BigDecimal result = a.add(b).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
    bh.consume(result);
}</pre>

1. `Blackhole.consume()` prevents the JIT from eliminating dead code; without it, you benchmark nothing

### Fast `double` rounding {#h3-17-fast-double-rounding}

Peter Lawrey's blog (Vanilla Java / Chronicle Software) demonstrated that rounding a `double` to a fixed number of decimal places can be done at very different speeds depending on the technique. His original benchmark measured three approaches for rounding to two decimal places: cast-based rounding (\~6 ns), `Math.round()`-based (\~17 ns), and `BigDecimal.setScale()` (\~932 ns).

The cast-based approach is the fastest. Here is the original form from his blog, for two decimal places:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">static double roundToTwoPlaces(double d) {
    return ((long) (d &lt; 0 ? d * 100 - 0.5 : d * 100 + 0.5)) / 100.0;  // 1
}</pre>

1. Shift decimal point, bias +/-0.5 for half-up, truncate via cast, shift back; handles negatives by branching on sign

The pattern generalizes naturally:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">static double roundHalfUp(double value, int decimalPlaces) {
    double factor = Math.pow(10, decimalPlaces);
    return value &gt;= 0
        ? Math.floor(value * factor + 0.5) / factor
        : Math.ceil(value * factor - 0.5) / factor;
}</pre>

This works correctly for both positive and negative values, within the safe integer range of `double` (up to 2\^53). It requires `HALF_UP` rounding only; for banker's rounding, *i.e.* , `HALF_EVEN`, use `Math.rint()` or `BigDecimal` with `RoundingMode.HALF_EVEN`. For audit-mandated traceability, `BigDecimal` with explicit `RoundingMode` is easier to defend to a regulator.

|                 Approach                  |    Speed (Lawrey's bench)    |  Rounding modes   |         Best for         |
|-------------------------------------------|------------------------------|-------------------|--------------------------|
| Cast trick / `Math.floor`                 | \~6 ns                       | HALF_UP only      | Hot paths, fixed scale   |
| `Math.round(value * factor) / factor`     | \~17 ns                      | HALF_UP (Java 7+) | Readable middle ground   |
| `BigDecimal.valueOf(v).setScale(n, mode)` | \~932 ns                     | All modes         | Audit, regulatory        |
| `decimal4j` `DoubleRounder.round(v, n)`   | Fast (cast-based internally) | Configurable      | Production-ready, tested |

Use `BigDecimal.valueOf(value)` and not `new BigDecimal(value)` in the BigDecimal approach to avoid capturing the inexact binary representation.

Fixed-point arithmetic {#h2-18-fixed-point-arithmetic}
------------------------------------------------------

If even the fast rounding tricks above feel like too much overhead, there is a more radical alternative. Many high-performance financial systems do not use `BigDecimal` at all. They use **fixed-point arithmetic on `long`**.

The idea is trivially simple: instead of storing `19.99` as a floating-point or decimal value, store `1999` as an integer representing cents. All arithmetic happens on integers, which are fast, deterministic, and allocation-free.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public record Money(long cents) {
    public Money plus(Money other) {
        return new Money(Math.addExact(cents, other.cents));      // 1
    }

    public Money multiply(long multiplier) {
        return new Money(Math.multiplyExact(cents, multiplier));  // 1
    }

    public BigDecimal toBigDecimal() {
        return BigDecimal.valueOf(cents, 2);                      // 2
    }
}</pre>

1. `Math.addExact` and `Math.multiplyExact` throw `ArithmeticException` on overflow instead of silently wrapping, critical for financial systems
2. Converts back to `BigDecimal` at the boundary, preserving scale

The advantages of fixed-point are compelling: no heap allocation, no GC pressure, no arbitrary-precision overhead, deterministic results, and performance that rivals raw `double`. The disadvantages are equally real: you must manage the scale yourself, handle overflow explicitly, implement rounding manually, and convert at system boundaries.

Applying a percentage like VAT in a fixed-point requires care. The standard approach uses *basis points* (hundredths of a percent, so 22% = 2200 basis points):

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">static long applyVat(long netCents, long vatBasisPoints) {
    long numerator = Math.multiplyExact(netCents, 10_000 + vatBasisPoints);
    return (numerator + 5_000) / 10_000;                         // 1
}</pre>

1. Returns the VAT-inclusive gross total (net + VAT); `+ 5_000` before dividing by `10_000` implements half-up rounding in pure integer arithmetic, with no floating-point involved

Everything stays in integer arithmetic. This is the pattern used by many low-latency trading systems and payment processors.

Real-world libraries and frameworks {#h2-19-real-world-libraries-and-frameworks}
--------------------------------------------------------------------------------

The Java ecosystem offers several mature libraries for numeric precision. Each occupies a different niche. Here is a practical guide.

### JavaMoney and Moneta {#h3-20-javamoney-and-moneta}

[JSR 354](https://javamoney.github.io/) defines a standard API for monetary amounts and currencies in Java, aka JavaMoney. The reference implementation, **Moneta**, provides two concrete types:

* `Money`: backed by `BigDecimal`. Maximum precision, full decimal control.
* `FastMoney`: backed by `long` with a fixed scale of 5 decimal places. Maximum performance.

This mirrors exactly the trade-off we have been discussing. The JSR 354 API (`MonetaryAmount`) abstracts over both, so you can choose the implementation that fits your use case without changing your business logic. The API also provides currency handling, rounding policies, formatting, and exchange rate conversion.

**Use case:** Enterprise applications that need a standardized money type with currency awareness, formatting, and conversion. Ideal when you want a well-defined abstraction layer and do not need to squeeze out the last nanosecond.

### Joda-Money {#h3-21-joda-money}

Created by Stephen Colebourne, author of Joda-Time and `java.time`, [Joda-Money](https://www.joda.org/joda-money/) is a deliberately simpler alternative to JSR 354. It provides `Money`, a fixed-scale class, backed by `BigDecimal`, and `BigMoney`, of arbitrary scale. There's no `FastMoney` equivalent; the focus is a clean, minimal API for applications where money is a secondary concern (e-commerce, SaaS billing, reporting).

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import org.joda.money.Money;
import org.joda.money.CurrencyUnit;

Money price = Money.of(CurrencyUnit.EUR, 19.99);
Money total = price.multipliedBy(3);
Money vat   = total.multipliedBy(0.22, RoundingMode.HALF_UP);</pre>

**Use case:** Simpler alternative to JSR 354 when you need a robust money type but do not require the full JSR API surface (conversion providers, custom currencies, monetary queries). The 2.x branch requires Java 21+; the 1.x branch works with Java 8+.

### decimal4j {#h3-22-decimal4j}

[decimal4j](https://github.com/tools4j/decimal4j) implements fixed-point decimal arithmetic on `long`, with a configurable scale of up to 18 decimal places, pluggable rounding modes, and a zero-garbage API designed for low-latency systems. It offers both immutable and mutable decimal types, and the scale is encoded in the type itself (*e.g.* , `Decimal2f` for 2 decimal places).

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import org.decimal4j.immutable.Decimal2f;

Decimal2f price = Decimal2f.valueOf("19.99");
Decimal2f total = price.multiply(3);</pre>

The mutable variant, `MutableDecimal2f`, avoids object allocation entirely by modifying its internal state and returning `this`, which is useful in tight loops where GC pressure matters.

The library also provides `DoubleRounder`, a utility for fast rounding of `double` values to a fixed number of decimal places (see Fast `double` Rounding above).

**Use case:** Low-latency systems where you need fixed-point arithmetic with a clean API and do not want to manage raw `long` arithmetic yourself. Trading systems, pricing engines, financial microservices.

### Apache Commons Numbers {#h3-23-apache-commons-numbers}

[commons-numbers-core](https://commons.apache.org/proper/commons-numbers/) is not a money library; it is a numerical precision toolkit. Its key components for our purposes are:

**`Sum`:**   

Compensated summation and dot product using the same Sum2S/Dot2S algorithms described earlier. This is a drop-in replacement for naive summation loops that need higher accuracy.

**`Precision`:**   

Utilities for floating-point comparison: `equals(double, double, double eps)` for epsilon-based comparison, `equals(double, double, int maxUlps)` for ULP-based comparison, and rounding with configurable `RoundingMode`. Also provides `EPSILON` (machine epsilon, 2\^-53) and `SAFE_MIN` (smallest normalized double, 2\^-1022) as named constants.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import org.apache.commons.numbers.core.Precision;

boolean eq = Precision.equals(0.1 + 0.2, 0.3, 1);               // 1
boolean eq2 = Precision.equals(a, b, 1e-10);                    // 2</pre>

1. ULP-based comparison: equal if within 1 ULP
2. Epsilon-based comparison

**Use case:** Any application that does floating-point computation and needs reliable comparison, compensated summation, or compensated dot products. Scientific computing, analytics, ML pipelines, numerical simulations.

### Library Comparison {#h3-24-library-comparison}

The table also includes [ta4j](https://github.com/ta4j/ta4j), Technical Analysis for Java, whose `Num` interface abstracts over `DecimalNum` (`BigDecimal`) and `DoubleNum` (`double`), letting you swap precision for performance without changing business logic, the same pattern Moneta uses with `Money` vs `FastMoney`.

|            Library            |      Backing type       |    Allocation-free    | Currency support |            Best for             |
|-------------------------------|-------------------------|-----------------------|------------------|---------------------------------|
| Moneta `Money`                | `BigDecimal`            | No                    | Yes (JSR 354)    | Enterprise, accounting          |
| Moneta `FastMoney`            | `long` (5 dp)           | Mostly                | Yes (JSR 354)    | Moderate-perf with JSR API      |
| Joda-Money                    | `BigDecimal`            | No                    | Yes (ISO 4217)   | Simpler projects, billing       |
| decimal4j                     | `long` (0-18 dp)        | Yes (mutable variant) | No               | Low-latency, fixed-scale        |
| Commons Numbers `Sum`         | `double` (compensated)  | Yes                   | No               | Accurate summation/dot products |
| Commons Numbers `Precision`   | `double`                | Yes                   | No               | Comparison, rounding            |
| ta4j `DecimalNum`/`DoubleNum` | `BigDecimal` / `double` | No / Yes              | No               | Technical analysis, backtesting |

Decision Guide {#h2-25-decision-guide}
--------------------------------------

Choosing the right numeric type is an engineering decision, not a religious one.

|        Type        |            Precision             |           Speed            |                                  Use when                                  |
|--------------------|----------------------------------|----------------------------|----------------------------------------------------------------------------|
| `double`           | \~15 decimal digits, approximate | Fastest (HW FPU)           | Analytics, ML, simulations, graphics, ranking, metrics                     |
| `float`            | \~7 decimal digits, approximate  | Fast, half the memory      | GPU shaders, image/audio, large ML vectors, massive datasets               |
| `BigDecimal`       | Arbitrary, exact decimal         | Slowest (\~100x vs double) | Accounting, invoicing, tax, audit, regulatory, reconciliation              |
| `long` fixed-point | Exact within fixed scale         | Very fast, no allocation   | Trading, payment processors, low-latency pricing                           |
| `decimal4j`        | Exact, 0-18 decimal places       | Fast, zero-garbage         | Structured fixed-point with rounding API, less boilerplate than raw `long` |

Production pitfalls {#h2-26-production-pitfalls}
------------------------------------------------

Choosing the right numeric type is only half the battle. The other half is making sure your choice survives contact with JSON serializers, test frameworks, and multi-threaded runtimes.

### JSON Serialization {#h3-27-json-serialization}

Serializing numeric values to JSON is one of the most common sources of silent precision loss, because the JSON specification defines only one number type and has no concept of scale or precision.

**`BigDecimal` and scale loss.** When Jackson serializes a `BigDecimal`, it writes it as a JSON number by default. This means `new BigDecimal("19.10")` becomes `19.1` in JSON: the trailing zero, which carries meaning in `BigDecimal` (scale = 2 vs scale = 1), is silently dropped. On deserialization, you get a `BigDecimal` with a different scale. If your code uses `equals()` (which compares scale), this breaks. If your financial system relies on the scale to determine the number of decimal places for rounding, this is a bug.

The common fix is to serialize `BigDecimal` as a JSON string:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class Invoice {
    @JsonFormat(shape = JsonFormat.Shape.STRING)                  // 1
    private BigDecimal amount;
}</pre>

1. Preserves the exact textual representation: `"19.10"` stays `"19.10"` in JSON

Alternatively, you can configure the Jackson `ObjectMapper` globally:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">ObjectMapper mapper = new ObjectMapper();
mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
mapper.configure(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN, true);</pre>

`WRITE_BIGDECIMAL_AS_PLAIN` prevents Jackson from using scientific notation (e.g., `1.2E+3`), and `USE_BIG_DECIMAL_FOR_FLOATS` ensures that incoming JSON numbers are deserialized as `BigDecimal` rather than `Double`.

**`double` and spurious decimals.** When Jackson serializes a `double`, you might see `19.989999999999998` instead of `19.99`. This is the representation error surfacing at the serialization boundary. If you are using `double` internally and rounding at the boundary, make sure the rounding happens *before* serialization, not after.

**`long` fixed-point.** If your internal representation is `long` cents, you need a custom serializer/deserializer that converts between `1999` (internal) and `19.99` (JSON). This is extra code, but it gives you full control and eliminates any ambiguity.

### Testing floating-point code {#h3-28-testing-floating-point-code}

Testing numeric code has its own set of traps. The core issue: you cannot use exact equality for `double` results, but choosing the right tolerance is a skill that is rarely taught.

**JUnit 5** provides `assertEquals` with a delta:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import static org.junit.jupiter.api.Assertions.assertEquals;

@Test
void testAverage() {
    double result = average(new double[]{0.1, 0.2, 0.3});
    assertEquals(0.2, result, 1e-15);                            // 1
}</pre>

1. Delta = tolerance: too tight and the test fails on CI; too loose and it misses regressions

**AssertJ** offers a more expressive API:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import static org.assertj.core.api.Assertions.assertThat;
import org.assertj.core.data.Offset;
import org.assertj.core.data.Percentage;

@Test
void testAverageAssertJ() {
    double result = average(new double[]{0.1, 0.2, 0.3});
    assertThat(result).isCloseTo(0.2, Offset.offset(1e-15));     // 1
    assertThat(result).isCloseTo(0.2, Percentage.withPercentage(0.0001)); // 2
}</pre>

1. Absolute tolerance
2. Relative tolerance: useful when the output magnitude varies across test cases

**For `BigDecimal` tests** , use `compareTo`, not `equals`, in your assertions, or normalize scale first:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Test
void testVatCalculation() {
    BigDecimal result = calculateVat(new BigDecimal("100.00"), new BigDecimal("0.22"));
    assertThat(result.compareTo(new BigDecimal("22.00"))).isZero();
}</pre>

### `double` is not atomic {#h3-29-double-is-not-atomic}

JLS §17.7 states that a write to a non-volatile `double` or `long` is treated as **two separate 32-bit writes** . A reading thread can observe a *torn value* (high bits from one write, low bits from another), producing a meaningless bit pattern.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">private double sharedPrice;            // UNSAFE: another thread may read a torn value
private volatile double sharedPrice;   // SAFE: volatile guarantees atomic read/write</pre>

On 64-bit JVMs, `double` writes happen to be atomic at the hardware level, but the JLS does not guarantee this. If you share a `double` across threads without `volatile`, your code is incorrect per the spec.

### Parallel streams {#h3-30-parallel-streams}

The Javadoc for `DoubleStream.sum()` explicitly warns that the order of addition is *intentionally not defined* . This has a direct consequence: `DoubleStream.parallel().sum()` can return different results on different invocations with the same input data.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">double[] values = {0.1, 0.2, 0.3, 1e15, -1e15, 0.4};
double seqSum = DoubleStream.of(values).sum();             // sequential: consistent
double parSum = DoubleStream.of(values).parallel().sum();  // parallel: may differ between runs</pre>

The difference is usually tiny, within a few ULPs for well-conditioned data. But for ill-conditioned sums (large positive and negative terms that nearly cancel), the difference can be significant. If your application requires **reproducible** results across runs or machines, either use sequential streams or use a deterministic summation algorithm like Kahan or Neumaier.

This connects directly to the earlier section on summation algorithms: floating-point addition is not associative, so different partition orders yield different results.

### Concurrent accumulation with `DoubleAdder` {#h3-31-concurrent-accumulation-with-doubleadder}

When multiple threads need to update a shared sum (metrics counters, running totals, real-time aggregates), the naive approaches all have problems. A `synchronized` block serializes all threads. An `AtomicReference` with a CAS loop allocates a new `BigDecimal` on every retry. Both create severe contention under load.

`DoubleAdder` (added in Java 8) solves this with **striped cells** : internally, it maintains multiple partial sums across different memory locations, so threads rarely contend on the same cache line. The final sum is only computed when you call `sum()`.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import java.util.concurrent.atomic.DoubleAdder;

DoubleAdder totalRevenue = new DoubleAdder();
totalRevenue.add(19.99);           // called from many threads, minimal contention
double current = totalRevenue.sum(); // aggregates all stripes when read</pre>

The trade-off: `DoubleAdder.sum()` is not atomic: it reads the stripes sequentially, so if other threads are writing concurrently, the result is an approximation. For exact point-in-time snapshots, you still need external synchronization. But for metrics, dashboards, and monitoring (where approximate-but-fast beats exact-but-slow), `DoubleAdder` is the right primitive.

For `BigDecimal` accumulation, there is no built-in equivalent. The common pattern is a `LongAdder` on fixed-point cents, converted to `BigDecimal` only when reading.

Project Valhalla and Value Types {#h2-32-project-valhalla-and-value-types}
--------------------------------------------------------------------------

One of the strongest arguments for `double` or `long` over `BigDecimal` has always been performance, specifically the cost of heap allocation and garbage collection pressure for every arithmetic operation. Project Valhalla may fundamentally change this equation.

[JEP 401 (Value Classes and Objects)](https://openjdk.org/jeps/401), currently in preview, introduces *value classes*, classes that give up object identity in exchange for potential stack allocation and flattening. A value class instance is compared by its field values rather than by reference identity, and the JVM is free to eliminate the object header and allocate it on the stack or inline it into arrays.

What does this mean for numeric types? Consider a `Money` wrapper:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">value class Money {  // preview syntax, requires --enable-preview
    private final long cents;
    // ... arithmetic methods ...
}</pre>

With value semantics, `Money` could be inlined into arrays without per-element object headers, passed in registers instead of on the heap, and eliminated entirely by escape analysis. The GC pressure that makes `BigDecimal` expensive in tight loops could largely disappear.

The implications extend beyond custom types. `BigDecimal` itself is unlikely to become a value class, because its `stringCache` field carries mutable state and deep compatibility constraints. New purpose-built decimal types could be designed as value classes from the start, and standard types like `Optional` and `LocalDate` may benefit too.

Value classes are currently in preview, *aka* [JEP 401](https://openjdk.org/jeps/401), with early-access builds available since JDK 23. This is the next major architectural shift in the JVM, and it will reshape the performance trade-offs we have discussed throughout this article.

Conclusion {#h2-33-conclusion}
------------------------------

The statement "never use `double` for money" is too simplistic. A more honest version would be: **never use any numeric type without understanding its precision model, rounding behavior, overflow characteristics, performance profile, and how those properties interact with the requirements of your domain.**

`double` is fast and approximate, ideal for computation where decimal exactness is irrelevant. `BigDecimal` is precise and auditable, essential where rounding rules are legally mandated. Fixed-point on `long` is fast and deterministic, often the best compromise for high-performance financial systems.

The real engineering mistake is not choosing `double` over `BigDecimal`. It is choosing any of them without understanding *why*.

**To go further:**

* [JEP 306: Restore Always-Strict Floating-Point Semantics](https://openjdk.org/jeps/306)
* [JEP 401: Value Classes and Objects (Preview)](https://openjdk.org/jeps/401)
* [JSR 354: Java Money and Currency API](https://javamoney.github.io/)
* [decimal4j --- Fast Fixed-Point Decimal Arithmetic](https://github.com/tools4j/decimal4j)
* [Apache Commons Numbers](https://commons.apache.org/proper/commons-numbers/)
* [Joda-Money](https://www.joda.org/joda-money/)

*** ** * ** ***

*Originally published at [A Java Geek](https://blog.frankel.ch/bigdecimal-vs-double/) on June 14^th^, 2026.*

*[JMH]: Java Microbenchmark Harness
