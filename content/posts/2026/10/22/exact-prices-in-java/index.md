---
title: "Exact Prices in Java: Decimal Strings and BigDecimal Traps"
date: "2026-10-22"
description: "Why double breaks prices, why APIs send them as strings, and the BigDecimal and Jackson traps to avoid when you read, compute and display them."
authors:
  - "christian-lauridsen"
image: "preview.jpg"
categories:
  - "Java Core"
related_posts:
  - "double-bigdecimal-or-fixed-point-precision-performance-and-sane-choices-for-numbers-in-java"
---

Prices look like the simplest data there is: a number with a few decimals. Then a crypto price arrives with fifteen decimal places, a token trades at `0.000012341873905`, and the numbers on screen stop adding up. This article walks through what goes wrong between the JSON and the screen, and how to keep every digit with `BigDecimal` and Jackson. For the wider trade-offs between the number types, see [double, BigDecimal, or Fixed-Point? on foojay](https://foojay.io/today/double-bigdecimal-or-fixed-point-precision-performance-and-sane-choices-for-numbers-in-java/); this article follows one price from the JSON to the screen.

## Why double is the wrong type for a price

A `double` stores a binary fraction, and most decimal fractions have no exact binary form. You can see it with three lines:

{{< jdoodle >}}
import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        System.out.println(0.1 + 0.2);
        System.out.println(63520.780763913 * 0.75);
        System.out.println(new BigDecimal("63520.780763913").multiply(new BigDecimal("0.75")));
    }
}
{{< /jdoodle >}}

```
0.30000000000000004
47640.585572934746
47640.58557293475
```

The second line is the value of 0.75 BTC at a price of 63,520.780763913 USD, and the double already gets the last digits wrong. The errors also add up: adding one satoshi (`0.00000001`) a hundred million times gives `1.0000000022898672` as a double, and exactly `1.00000000` as a `BigDecimal`.

Rounding for display hides the error, which is why it survives code review. As soon as you sum, compare or store the values, it becomes a bug report.

## Why price APIs send strings

JSON has a single number type, and most parsers turn a number with a fraction into a `double`. Jackson's tree model (`readTree`) and untyped targets such as `Map<String, Object>` do exactly that (the examples use Jackson 2, the 2.17 line the SDK below depends on):

```java
ObjectMapper mapper = new ObjectMapper();
JsonNode p = mapper.readTree("{\"p\": 63520.780763913123456}").get("p");

p.getClass();       // DoubleNode
p.decimalValue();   // 63520.78076391312
```

The digits are gone before your code sees them, and `decimalValue()` only wraps the rounded double. Binding straight into a `BigDecimal` field is exact; the loss happens in the tree and in untyped maps. If you control the parser, `DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS` keeps the full value there too, but the tree then strips trailing zeros (`1200.00` comes back as `1.2E+3`), so print those values with `toPlainString()`.

You often don't control every client, though, so many market-data APIs send prices as strings instead. The Bitculator Data API, which this article uses for its example, returns each coin as a row like this one (abridged; the [Bitculator Data API documentation](https://bitculator.com/en/documentation/api/v1) lists every field):

```json
{
  "slug": "bitcoin",
  "symbol": "BTC",
  "price": "63520.780763913",
  "marketcap": 1251359381049.09,
  "change_24h": 1.35
}
```

The price is a decimal string with trailing zeros trimmed and never in E-notation. Values where a rounded double is acceptable, such as the market cap and the percentage change, stay JSON numbers.

## Reading a decimal string with Jackson

A string price arrives as a `TextNode`, and that is where the first trap is:

```java
JsonNode price = mapper.readTree("{\"price\": \"63520.780763913\"}").get("price");

price.decimalValue();             // 0
price.asDouble();                 // 63520.780763913, but now a double
new BigDecimal(price.asText());   // 63520.780763913
```

`decimalValue()` is defined on `JsonNode` and returns `BigDecimal.ZERO` for anything that isn't a number node. There is no exception and no warning, just a price of zero. `asDouble()` parses the text, which brings back the double problem. `new BigDecimal(node.asText())` is the conversion you want. Don't use `node.toString()` here: it returns the quoted JSON form, and the `BigDecimal` constructor rejects the quotes.

If you map responses to records, Jackson converts the string for you, because it coerces a JSON string into a `BigDecimal` field by default:

```java
@JsonIgnoreProperties(ignoreUnknown = true)
record Quote(String symbol, BigDecimal price) {}

Quote btc = mapper.readValue(
        "{\"symbol\":\"BTC\",\"price\":\"63520.780763913\",\"rank\":1}", Quote.class);
// Quote[symbol=BTC, price=63520.780763913]
```

The annotation matters with real API payloads, which carry more fields than your record. Jackson 2 fails on unknown properties by default.

Jackson 3 (package `tools.jackson`) changes some of this. The string node is a `StringNode`, `decimalValue()` throws a `JsonNodeException` instead of returning zero, and the new `asDecimal()` parses the text into an exact `BigDecimal`. Jackson 3 also no longer fails on unknown properties by default.

## Five BigDecimal traps

Once the value is a `BigDecimal`, the class still has a few surprises.

### 1. The double constructor

```java
new BigDecimal(0.1);       // 0.1000000000000000055511151231257827021181583404541015625
BigDecimal.valueOf(0.1);   // 0.1
new BigDecimal("0.1");     // 0.1
```

`new BigDecimal(double)` faithfully copies the binary error. `BigDecimal.valueOf(double)` looks better because it goes through `Double.toString`, but it can't bring back digits the double never had: `BigDecimal.valueOf(63520.780763913123456)` is `63520.78076391312`. Build prices from strings.

### 2. equals is not compareTo

```java
BigDecimal a = new BigDecimal("2.0");
BigDecimal b = new BigDecimal("2.00");

a.equals(b);       // false, the scales differ (1 and 2)
a.compareTo(b);    // 0
```

`equals` and `hashCode` include the scale, so a `HashSet` keeps both values and `HashMap.get(b)` misses a key stored as `a`. `TreeSet` and `TreeMap` use `compareTo` and see one value. Compare prices with `compareTo`, test for zero with `signum() == 0`, and normalise map keys with `stripTrailingZeros()` if you have to use them.

### 3. divide needs a rule

```java
BigDecimal usd = new BigDecimal("100");
BigDecimal btcPrice = new BigDecimal("63520.780763913");

usd.divide(btcPrice);                          // ArithmeticException
usd.divide(btcPrice, 8, RoundingMode.DOWN);    // 0.00157428
usd.divide(btcPrice, MathContext.DECIMAL64);   // 0.001574287954231371
```

Without a scale or a `MathContext`, `divide` only succeeds when the result is exact; otherwise it throws `Non-terminating decimal expansion; no exact representable decimal result.` Decide what the result means: eight decimals rounded down gives whole satoshis and never more BTC than 100 USD can buy. A `MathContext` counts significant digits, not decimal places. Also watch out for `divide(divisor, RoundingMode.HALF_UP)` without a scale: it keeps the dividend's scale, so `BigDecimal.ONE.divide(new BigDecimal("3"), RoundingMode.HALF_UP)` returns `0`.

### 4. toString switches to scientific notation

```java
BigDecimal tiny = new BigDecimal("0.0000000123");

String s = "price: " + tiny;   // price: 1.23E-8
tiny.toPlainString();           // 0.0000000123

new BigDecimal("1200").stripTrailingZeros();                   // 1.2E+3
new BigDecimal("1200").stripTrailingZeros().toPlainString();   // 1200
```

`toString()` uses E-notation once the value is below 0.000001 (one satoshi prints as `1E-8`) or when stripping zeros leaves a negative scale. String concatenation, `String.valueOf` and `"%s"` all call `toString()`. Use `toPlainString()` for anything a person reads or another system parses. Jackson also writes `BigDecimal` with `toString()` unless you enable `StreamWriteFeature.WRITE_BIGDECIMAL_AS_PLAIN`, for example with `JsonMapper.builder().enable(StreamWriteFeature.WRITE_BIGDECIMAL_AS_PLAIN).build()`.

### 5. Rounding modes differ between tools

```java
new BigDecimal("2.345").setScale(2, RoundingMode.HALF_UP);     // 2.35
new BigDecimal("2.345").setScale(2, RoundingMode.HALF_EVEN);   // 2.34

NumberFormat.getCurrencyInstance(Locale.US).format(new BigDecimal("2.345"));   // $2.34
String.format(Locale.ROOT, "%.2f", new BigDecimal("2.345"));                  // 2.35
```

`HALF_EVEN` sends a tie to the even neighbour, which keeps sums of many rounded values unbiased. `NumberFormat` uses it by default, while `String.format` rounds half up, so the same value can show two different ways on one page. Pick a mode on purpose, and round once, at the end.

## Putting it together: valuing a small portfolio

Here is the whole path in one program: fetch prices for three coins, value the holdings and show each coin's share. It uses the Bitculator Java SDK ([source on GitHub](https://github.com/Bitculator/bitculator-java-sdk)), which needs Java 17 and returns rows as Jackson 2 `JsonNode`s:

```xml
<dependency>
  <groupId>com.bitculator</groupId>
  <artifactId>bitculator</artifactId>
  <version>1.1.0</version>
</dependency>
```

Set `BITCULATOR_API_KEY` to a Data API key before running it; a free-tier key is enough.

```java
import com.bitculator.Bitculator;
import com.bitculator.Params;
import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class PortfolioValue {

    public static void main(String[] args) {
        Map<String, BigDecimal> holdings = Map.of(
                "bitcoin", new BigDecimal("0.75"),
                "ethereum", new BigDecimal("2.5"),
                "shiba-inu", new BigDecimal("2500000"));

        Bitculator client = new Bitculator(System.getenv("BITCULATOR_API_KEY"));
        JsonNode rows = client.prices().list(Params.of().set("slugs", holdings.keySet()));

        Map<String, BigDecimal> values = new LinkedHashMap<>();
        for (JsonNode row : rows) {
            JsonNode price = row.get("price");
            if (price == null || price.isNull()) {
                continue; // no current price for this coin
            }
            BigDecimal amount = holdings.get(row.get("slug").asText());
            values.put(row.get("symbol").asText(), new BigDecimal(price.asText()).multiply(amount));
        }
        if (values.size() < holdings.size()) {
            System.err.println("No price for " + (holdings.size() - values.size()) + " holding(s); the total leaves them out");
        }

        BigDecimal total = values.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        NumberFormat usd = NumberFormat.getCurrencyInstance(Locale.US);

        values.forEach((symbol, value) -> {
            BigDecimal share = value.multiply(BigDecimal.valueOf(100))
                    .divide(total, 2, RoundingMode.HALF_EVEN);
            System.out.printf("%-5s %14s %7s%%%n", symbol, usd.format(value), share.toPlainString());
        });
        System.out.printf("%-5s %14s%n", "Total", usd.format(total));
        System.out.println("Exact total: " + total.stripTrailingZeros().toPlainString());
    }
}
```

With these example prices (63,520.780763913 for BTC, 2,431.5527310904 for ETH and 0.000012341873905 for SHIB) it prints:

```
BTC       $47,640.59   88.63%
ETH        $6,078.88   11.31%
SHIB          $30.85    0.06%
Total     $53,750.32
Exact total: 53750.32208542325
```

The program keeps full precision through every multiplication and the sum, and rounds only where a person reads the result: in `NumberFormat` and in the one division. SHIB's price sits just above the point where `toString()` switches to E-notation, so it would print plain either way; a cheaper token at `0.00000065231842` prints as `6.5231842E-7`. The program doesn't rely on that, because every number a person sees goes through `NumberFormat` or `toPlainString()`. The `stripTrailingZeros()` on the last line is there because the scale of a product is the sum of the two scales and `add()` keeps the larger scale: the SHIB value has 15 decimals, so the exact sum is `53750.322085423250000` until the zeros are trimmed. The check after the loop matters too: the API leaves out slugs it doesn't know, and a silently missing holding is the same kind of bug as a silently wrong digit.

## A short checklist

- Never put a price in a `double`, not even "just for display".
- Read decimal strings with `new BigDecimal(node.asText())`, never with `decimalValue()` or `asDouble()` (on Jackson 3: `node.asDecimal()`).
- If prices arrive as JSON numbers and you read them with `readTree` or into a `Map`, enable `USE_BIG_DECIMAL_FOR_FLOATS` before parsing.
- Build `BigDecimal`s from strings, not from doubles.
- Compare with `compareTo`, test zero with `signum()`.
- Give every `divide` a scale and a rounding mode, or a `MathContext`.
- Use `toPlainString()` for output, and `WRITE_BIGDECIMAL_AS_PLAIN` when writing JSON.
- Choose the rounding mode on purpose, and round once, at the end.

*Disclosure: the author builds the Bitculator Data API and Java SDK used in the example.*
