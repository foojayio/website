---
title: "Learning by doing: An HTTP API with Rust"
date: "2023-01-29T16:24:21+00:00"
lastmod: "2026-09-21T06:05:17+00:00"
description: "When I started working on this post, I had another idea in mind: I wanted to compare the developer experience and performance of Spring Boot and GraalVM…"
canonical: "https://blog.frankel.ch/http-api-rust/"
authors:
  - "nicolas-frankel"
image: "pexels-pixabay-76966.jpg"
categories:
  - "Research"
related_posts:
  - "making-illegal-state-unrepresentable"
  - "the-try-block-in-rust"
  - "parsing-structured-environment-variables-in-rust"
  - "fixing-duplicate-api-requests"
frozen: false
---

When I started working on this post, I had another idea in mind: I wanted to compare the developer experience and performance of Spring Boot and GraalVM with Rust on a demo HTTP API application. Unfortunately, the M1 processor of my MacBook Pro had other ideas.

[](https://twitter.com/nicolas_frankel/status/1608847301103214593)

[

{{< img src="Screenshot-2023-01-28-at-16.43.21-530x510.png" class="aligncenter size-medium" width="530" height="510" >}}

](https://twitter.com/nicolas_frankel/status/1608847301103214593)

Hence, I change my initial plan. I'll write about the developer experience of developing the above application in Rust, compared to what I'm used to with Spring Boot.

## The sample application

Like every pet project, the application is limited in scope. I designed a simple HTTP API. Data are stored in PostgreSQL.

When one designs an app on the JVM, the first and only design decision is to choose the framework: a couple of years ago, it was Spring Boot. Nowadays, the choice is mostly between [Spring Boot](https://spring.io/), [Quarkus](https://quarkus.io/), and [Micronaut](https://micronaut.io/). In many cases, they all rely on the same underlying libraries, *e.g.* logging or connection pools.

Rust is much younger; hence the ecosystem has yet to mature. For *every* feature, one needs to choose precisely which library to use - or to implement it. Worse, one needs to understand there's such a feature. Here are the ones that I searched for:

* Reactive database access
* Database connection pooling
* Mapping rows to structures
* Web endpoints
* JSON serialization
* Configuration from different sources, *e.g.*, YAML, environment variables, etc.

## Web framework

The choice of the web framework is the most critical. I've to admit I had no prior clue about such libraries. I looked around and stumbled upon [Which Rust web framework to choose in 2022](https://kerkour.com/rust-web-framework-2022). After reading the post, I decided to follow the conclusion and chose `axum`:
> * Route requests to handlers with a macro-free API.
> * Declaratively parse requests using extractors.
> * Simple and predictable error handling model.
> * Generate responses with minimal boilerplate.
> * Take full advantage of the [tower](https://crates.io/crates/tower) and [tower-http](https://crates.io/crates/tower-http) ecosystem of middleware, services, and utilities.
>
> In particular, the last point is what sets `axum` apart from other frameworks. `axum` doesn't have its own middleware system but instead uses [tower::Service](https://docs.rs/tower-service/0.3.2/x86_64-unknown-linux-gnu/tower_service/trait.Service.html). This means axum gets timeouts, tracing, compression, authorization, and more, for free. It also enables you to share middleware with applications written using [hyper](http://crates.io/crates/hyper) or [tonic](http://crates.io/crates/tonic).
>
> -- [axum crate documentation](https://docs.rs/axum/latest/axum/)

`axum` uses the [Tokio](https://tokio.rs/) *asynchronous* library underneath. For basic usage, it requires two crates:

```ini
[dependencies]
axum = "0.6"
tokio = { version = "1.23", features = ["full"] }
```

`axum`'s router looks very similar to Spring's Kotlin Routes DSL:

```rust
let app = Router::new()
    .route("/persons", get(get_all))         //1
    .route("/persons/:id", get(get_by_id))   //1//2

async fn get_all() -> Response { ... }
async fn get_by_id(Path(id): Path<Uuid>) -> Response { ... }
```

1. A route is defined by the path and a function reference
2. A route can have path parameters. `axum` can infer parameters and bind them

## Shared objects

An issue commonly found in software projects is sharing an "object" with others. We established long ago that there were better ideas than sharing global variables.

Spring Boot (and similar JVM frameworks) solves it with runtime dependency injection. Objects are created by the framework, stored in a context, and injected into other objects when the application starts. Other frameworks do dependency injection at compile-time, *e.g.*, Dagger 2.

Rust has neither runtime nor objects. Configurable dependency injection is not "a thing". But we can create a variable and inject it manually where needed. In Rust, it's a problem because of *ownership*:
> Ownership is a set of rules that govern how a Rust program manages memory. All programs have to manage the way they use a computer's memory while running. Some languages have garbage collection that regularly looks for no-longer-used memory as the program runs; in other languages, the programmer must explicitly allocate and free the memory. Rust uses a third approach: memory is managed through a system of ownership with a set of rules that the compiler checks. If any of the rules are violated, the program won't compile. None of the features of ownership will slow down your program while it's running.
>
> -- [What Is Ownership?](https://doc.rust-lang.org/book/ch04-01-what-is-ownership.html)

`axum` provides a dedicated wrapper, the [State](https://docs.rs/axum/latest/axum/extract/struct.State.html) extractor, to reuse variables across different scopes.

```rust
struct AppState {                                                  //1
    ...
}

impl AppState {
    fn create() -> Arc<AppState> {                                 //2
        Arc::new(AppState { ... })
    }
}

let app_state = AppState::create();
let app = Router::new()
    .route("/persons", get(get_all))
    .with_state(Arc::clone(&app_state));                           //3

async fn get_all(State(state): State<Arc<AppState>>) -> Response { //4
    ...                                                            //5
}
```

1. Create the `struct` to be shared
2. Create a new `struct` wrapped in an [Atomically Reference Counted](https://doc.rust-lang.org/std/sync/struct.Arc.html)
3. Share the reference with all routing functions, *e.g.* , `get_all`
4. Pass the `state`
5. Use it!

## Automated JSON serialization

Modern JVM web frameworks automatically serialize objects in JSON before sending. The good thing is that `axum` does the same. It relies on [Serde](https://serde.rs/). First, we add the `serde` and `serde_json` crate dependencies:

```ini
[dependencies]
serde = { version = "1.0", features = ["derive"] }
serde_json = "1.0"
```

Then, we annotate our `struct` with the `derive(Serialize)` macro:

```rust
#[derive(Serialize)]
struct Person {
    first_name: String,
    last_name: String,
}
```

Finally, we return the `struct` wrapped in a `Json` and the HTTP status code in an `axum` `Response`.

```rust
async fn get_test() -> impl IntoResponse {        //1
    let person = Person {                         //2
        first_name: "John".to_string(),
        last_name: "Doe".to_string()
    };
    (StatusCode::OK, Json(person))                //3
}
```

1. The tuple `(StatusCode, Json)` is automatically converted into a `Response`
2. Create the `Person`
3. Return the tuple

At runtime, `axum` automatically serializes the `struct` in JSON:

```json
{"first_name":"Jane","last_name":"Doe"}
```

## Database access

For a long time, I used the MySQL database for my demos. But I started to read a lot of good stuff about PostgreSQL and decided to switch. I needed an asynchronous library compatible with Tokio: it's exactly what the [tokio_postgres](https://docs.rs/tokio-postgres/latest/tokio_postgres/) crate does.

The problem with the crate is that it creates direct connections to the database. I searched for a connection pool crate and stumbled upon `deadpool` (sic):
> Deadpool is a dead simple async pool for connections and objects of any type.
>
> -- [Deadpool](https://github.com/bikeshedder/deadpool)

Deadpool provides two distinct implementations:

* An unmanaged pool: the developer has complete control - and responsibility - over the pooled objects' lifecycle
* A managed pool: the crate creates and recycles objects as needed

More specialized implementations of the latter cater to different databases or "drivers", *e.g.* Redis and... `tokio-postgres`. One can configure Deadpool directly or defer to the [config](https://docs.rs/config/0.13.3/config/) crate it supports. The latter crate allows several alternatives for configuration:
> Config organizes hierarchical or layered configurations for Rust applications.
>
> Config lets you set a set of default parameters and then extend them via merging in configuration from a variety of sources:
>
> * Environment variables
> * String literals in well-known formats
> * Another Config instance
> * Files: TOML, JSON, YAML, INI, RON, JSON5, and custom ones defined with Format trait
> * Manual, programmatic override (via a `.set` method on the Config instance)
>
> Additionally, Config supports:
>
> * Live watching and re-reading of configuration files
> * Deep access into the merged configuration via a path syntax
> * Deserialization via serde of the configuration or any subset defined via a path
>
> -- [Crate config](https://docs.rs/config/0.13.3/config/)

To create the base configuration, one needs to create a dedicated structure and use the crate:

```rust
#[derive(Deserialize)]                                       //1
struct ConfigBuilder {
    postgres: deadpool_postgres::Config,                     //2
}

impl ConfigBuilder {
    async fn from_env() -> Result<Self, ConfigError> {       //3
        Config::builder()
            .add_source(
                Environment::with_prefix("POSTGRES")         //4
                    .separator("_")                          //4
                    .keep_prefix(true)                       //5
                    .try_parsing(true),
            )
            .build()?
            .try_deserialize()
    }
}

let cfg_builder = ConfigBuilder::from_env().await.unwrap();  //6
```

1. The `Deserialize` macro is mandatory
2. The field *must* match the environment prefix, see below
3. The function is `async` and returns a `Result`
4. Read from environment variables whose name starts with `POSTGRES_`
5. Keep the prefix in the configuration map
6. Enjoy!

Note that environment variables should conform to what Deadpool's `Config` expects. Here's my configuration in Docker Compose:

|    Env variable     |    Value     |
|---------------------|--------------|
| `POSTGRES_HOST`     | `"postgres"` |
| `POSTGRES_PORT`     | `5432`       |
| `POSTGRES_USER`     | `"postgres"` |
| `POSTGRES_PASSWORD` | `"root"`     |
| `POSTGRES_DBNAME`   | `"app"`      |

Once we have initialized the configuration, we can create the pool:

```rust
struct AppState {
    pool: Pool,                                                     //1
}

impl AppState {
    async fn create() -> Arc<AppState> {                            //2
        let cfg_builder = ConfigBuilder::from_env().await.unwrap(); //3
        let pool = cfg_builder                                      //4
            .postgres
            .create_pool(
                Some(deadpool_postgres::Runtime::Tokio1),
                tokio_postgres::NoTls,
            )
            .unwrap();
        Arc::new(AppState { pool })                                 //2
    }
}
```

1. Wrap the pool in a custom `struct`
2. Wrap the `struct` in an `Arc` to pass it within an `axum` `State` (see above)
3. Get the configuration
4. Create the pool

Then, we can pass the pool to the routing functions:

```rust
let app_state = AppState::create().await;                           //1
let app = Router::new()
    .route("/persons", get(get_all))
    .with_state(Arc::clone(&app_state));                            //2

async fn get_all(State(state): State<Arc<AppState>>) -> Response {
    let client = state.pool.get().await.unwrap();                   //3
    let rows = client
        .query("SELECT id, first_name, last_name FROM person", &[]) //4
        .await                                                      //5
        .unwrap();
    //                                                              //6
}
```

1. Create the state
2. Pass the state to the routing functions
3. Get the pool out of the state, and get the client out of the pool
4. Create the query
5. Execute it
6. Read the row to populate the `Response`

The last step is to implement the transformation from a `Row` to a `Person`. We can do it with the `From` trait.

```rust
impl From<&Row> for Person {
    fn from(row: &Row) -> Self {
        let first_name: String = row.get("first_name");
        let last_name: String = row.get("last_name");
        Person {
            first_name,
            last_name,
        }
    }
}

let person = row.into();
```

## Docker build

The last step is the building of the application. I want everybody to be able to build, so I used Docker. Here's the `Dockerfile`:

```dockerfile
FROM --platform=x86_64 rust:1-slim AS build                                  //1

RUN rustup target add x86_64-unknown-linux-musl                              //2
RUN apt update && apt install -y musl-tools musl-dev                         //3

WORKDIR /home

COPY Cargo.toml .
COPY Cargo.lock .
COPY src src

RUN --mount=type=cache,target=/home/.cargo \                                 //4
 && cargo build --target x86_64-unknown-linux-musl --release                 //5

FROM scratch                                                                 //6

COPY --from=build /home/target/x86_64-unknown-linux-musl/release/rust /app   //7

CMD ["/app"]
```

1. Start from a standard Rust image
2. Add `musl` target so we can compile to Alpine Linux
3. Install the required Alpine dependencies
4. Cache the dependencies
5. Build for Alpine Linux
6. Start from *scratch*
7. Add the previously built binary

The final image is *7.56MB*. My experience has shown that an equivalent GraalVM native compiled image would be more than 100MB.

## Conclusion

Though it was not my initial plan, I learned about quite a few libraries with this demo app and how they work.

More importantly, I've experienced what it is like to develop an app without a framework like Spring Boot. You need to know the following:

1. Available crates for each capability
2. Crate compatibility
3. Version compatibility

Last but not least, the documentation of most above crates ranges from average to good. I found `axum`'s to be good; on the other hand, I didn't manage to use Deadpool correctly from the start and had to go through several iterations. The documentation quality of Rust crates is different from crate to crate. All in all, they have room for potential to reach the level of modern JVM frameworks.

Also, the demo app was quite simple. I assume that more advanced features could be more painful.

The complete source code for this post can be found on [Github](https://github.com/ajavageek/http-api-rust).

**To go further:**

* [Which Rust web framework to choose in 2022](https://kerkour.com/rust-web-framework-2022)
* [Create an Optimized Rust Alpine Docker Image](https://levelup.gitconnected.com/create-an-optimized-rust-alpine-docker-image-1940db638a6c)
* [How to create small Docker images for Rust](https://kerkour.com/rust-small-docker-image)
* [Using Axum Framework To Create Rest API](https://medium.com/intelliconnect-engineering/using-axum-framework-to-create-rest-api-part-1-7d434d2c5de4)

*Originally published at [A Java Geek](https://blog.frankel.ch/http-api-rust/) on January 29^th^, 2023*

*[CRUD]: Create Read Update Delete
