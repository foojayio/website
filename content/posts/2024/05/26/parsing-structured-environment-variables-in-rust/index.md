---
title: "Parsing structured environment variables in Rust"
date: "2024-05-26T18:34:33+00:00"
lastmod: "2026-09-21T06:03:41+00:00"
description: "I'm in the process of adding more components to my OpenTelemetry demo (again!). The new design deploys several warehouse services behind the inventory…"
canonical: "https://blog.frankel.ch/structured-env-vars-rust/"
authors:
  - "nicolas-frankel"
image: "scaffolding-1617969.jpg"
categories:
  - "Research"
related_posts:
  - "making-illegal-state-unrepresentable"
  - "the-try-block-in-rust"
  - "feedback-from-calling-rust-from-python"
  - "learning-by-doing-an-http-api-with-rust"
frozen: false
---

I'm in the process of adding more components to my OpenTelemetry demo (again!). The new design deploys several warehouse services behind the `inventory` service so the latter can query the former for data via their respective HTTP interface. I implemented each warehouse on top of a different technology stack. This way, I can show OpenTelemetry traces across several stacks.

Anyone should be able to add a warehouse in their favorite tech stack if it returns the correct JSON payload to the inventory. For this, I want to make the configuration of the inventory "easy"; add a new warehouse with a simple environment variable pair, *i.e.*, the endpoint and its optional country.

The main issue is that environment variables are not *structured* . I searched for a while and found a [relevant post](https://charemza.name/blog/posts/software-engineering/devops/structured-data-in-environment-variables/). Its idea is simple but efficient; here's a sample from the post:

```
FOO__1__BAR=setting-1         #1
FOO__1__BAZ=setting-2         #1
FOO__2__BAR=setting-3         #1
FOO__2__QUE=setting-4         #1

FIZZ__1=setting-5             #2
FIZZ__2=setting-6             #2

BILL=setting-7                #3
```

1. Map-like structure
2. Table-like structure
3. Just a value

With this approach, I could configure the inventory like this:

```yaml
services:
  inventory:
    image: otel-inventory:1.0
    environment:
      WAREHOUSE__0__ENDPOINT: http://apisix:9080/warehouse/us #1
      WAREHOUSE__0__COUNTRY: USA                              #2
      WAREHOUSE__1__ENDPOINT: http://apisix:9080/warehouse/eu #1
      WAREHOUSE__2__ENDPOINT: http://warehouse-jp:8080        #1
      WAREHOUSE__2__COUNTRY: Japan                            #2
      OTEL_EXPORTER_OTLP_ENDPOINT: http://jaeger:4317
      OTEL_RESOURCE_ATTRIBUTES: service.name=inventory
      OTEL_METRICS_EXPORTER: none
      OTEL_LOGS_EXPORTER: none
```

1. Warehouse endpoint
2. Set country

You can see the three warehouses configured in the above. Each has an endpoint/optional country pair.

My first attempt looked like the following:

```rust
lazy_static::lazy_static! {                                                     //1
    static ref REGEXP_WAREHOUSE: Regex = Regex::new(r"^WAREHOUSE__(\d)__.*").unwrap();
}

std::env::vars()
    .filter(|(key, _)| REGEXP_WAREHOUSE.find(key.as_str()).is_some())           //2
    .group_by(|(key, _)| key.split("__").nth(1).unwrap().to_string())           //3
    .into_iter()                                                                //4
    .map(|(_, mut group)| {                                                     //5
        let some_endpoint = group.find(|item| item.0.ends_with("ENDPOINT"));    //6
        let endpoint = some_endpoint.unwrap().1;
        let some_country = group                                                //7
            .find(|item| item.0.ends_with("COUNTRY"))
            .map(|(_, country)| country);
        println! {"Country pair is: {:?}", some_country};
            (endpoint, some_country).into()                                     //8
    }
    .collect::<Vec<_>>()
```

1. For making constants out of code evaluated at runtime
2. Filter out warehouse-related environment variable
3. Group by index
4. Back to an `Iter` with the help of `itertools`
5. Consist of just the endpoint or the endpoint *and* the country
6. Get the endpoint
7. Get the country
8. Into a structure - irrelevant

I encountered issues several times when I started the demo. The code somehow didn't find the endpoint **at all**. I chose this approach because I've been taught that it's more performant to iterate throughout the key-value pairs of a map than iterate through its key only and then get the value in the map. I tried to change to the latter.

```rust
lazy_static! {
    static ref REGEXP_WAREHOUSE_ENDPOINT: Regex =
        Regex::new(r"^WAREHOUSE__(?<index>\d)__ENDPOINT.*").unwrap();           //1
}
std::env::vars()
    .filter(|(key, _)| REGEXP_WAREHOUSE_ENDPOINT.find(key.as_str()).is_some())  //2
    .map(|(key, endpoint)| {
        let some_warehouse_index = REGEXP_WAREHOUSE_ENDPOINT.captures(key.as_str()).unwrap(); //3//4
        println!("some_warehouse_index: {:?}", some_warehouse_index);
        let index = some_warehouse_index.name("index").unwrap().as_str();
        let country_key = format!("WAREHOUSE__{}__COUNTRY", index);             //5
        let some_country = var(country_key);                                    //6
        println!("endpoint: {}", endpoint);
        (endpoint, some_country).into()
    })
    .collect::<Vec<_>>()
```

1. Change the regex to capture only the endpoint-related variables
2. Filter out warehouse-related environment variable
3. I'm aware that the `filter_map()` function exists, but I think it's clearer to separate them here
4. Capture the index
5. Create the country environment variable from a known string, and the index
6. Get the country

With this code, I didn't encounter any issues.

Now that it works, I'm left with two questions:

* Why doesn't the `group()`/`find()` version work in the deployed Docker Compose despite working in the tests?
* Is anyone interested in making a crate out of it?

**To go further:**

* [Structured data in environment variables](https://charemza.name/blog/posts/software-engineering/devops/structured-data-in-environment-variables/)
* [lazy_static crate](https://docs.rs/lazy_static/latest/lazy_static/)
* [envconfig crate](https://docs.rs/envconfig/latest/envconfig/)

*Originally published at [A Java Geek](https://blog.frankel.ch/structured-env-vars-rust/) on May 26th, 2024*
