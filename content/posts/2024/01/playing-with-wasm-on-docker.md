---
title: "Playing with WASM on Docker"
slug: "playing-with-wasm-on-docker"
date: "2024-01-03T07:48:29+00:00"
lastmod: "2024-01-03T07:48:30+00:00"
description: "While the ecosystem has room for improvement, it's already possible to benefit from Docker's WASM support."
canonical: "https://blog.frankel.ch/wasm-docker/"
authors:
  - "nicolas-frankel"
image: "https://foojay.io/wp-content/uploads/2023/06/wasm-ferris.png"
categories:
  - "DevOps"
tags:
related_posts:
enlighterjs: true
frozen: false
---

The idea of bytecode that can run anywhere dates back to the JVM inception (as far as I know).

WebAssembly is the new implementation of an old idea. While WebAssembly is meant to run in the browser, Docker recently announced its capability to run WASM code without needing containers.

In this post, I want to explore how it can work.

Prerequisite {#h2-0-prerequisite}
---------------------------------

Running WebAssembly is a *beta* feature and requires using `containerd`. To enable `containerd`, go to the Docker Desktop dashboard, then Settings \> Features in development \> Beta features \> Use containerd for storing and pulling image.

Be warned that enabling `containerd` previously broke one of my Kubernetes demos. Play with WASM to your heart's content, but remember to roll back the configuration immediately afterward, or there's a chance downloaded containers won't run anymore.

I want to compare regular images with WebAssembly; hence, I require a project that can compile to both native code and WASM. For this reason, I chose to use the Rust language. I'll have a single simple project with two Dockerfiles: one that compiles to native, the other that compiles to WASM.

Building locally {#h2-1-building-locally}
-----------------------------------------

Here's the Rust expected Hello World:

<pre class="EnlighterJSRAW" data-enlighter-language="rust">fn main() {
    println!("Hello, world!");
}</pre>

We can install the Webassembly target and build locally for comparison purposes:

<pre class="EnlighterJSRAW" data-enlighter-language="shell">rustup target add wasm32-wasi
cargo build --target wasm32-wasi --release</pre>

The file is relatively small:

    -rwxr-xr-x  1 nico  staff   2.0M Jun  4 15:44   wasm-native.wasm

Building the basic Docker images {#h2-2-building-the-basic-docker-images}
-------------------------------------------------------------------------

The `Dockerfile` that builds the Webassembly image is the following:

<pre class="EnlighterJSRAW" data-enlighter-language="dockerfile">FROM rust:1.70-slim-bullseye as build                                    #1

COPY Cargo.toml .
COPY Cargo.lock .
COPY src src

RUN rustup target add wasm32-wasi                                        #2

RUN cargo build --target wasm32-wasi --release                           #3

FROM scratch                                                             #4

COPY --from=build /target/wasm32-wasi/release/wasm-native.wasm wasm.wasm #5

ENTRYPOINT [ "/wasm.wasm" ]</pre>

1. Start from the last Rust Docker image
2. Add the WASM target
3. Build, targeting Webassembly
4. Use a multi-stage build. Start from scratch
5. Copy the Webassembly file generated in the previous stage

The reference material uses the `--platform wasi/wasm32` argument when building the Docker image. It doesn't work on my machine. It may be because I'm on an M1 Mac, or the documentation needs to be updated. In any case, I build "normally":

<pre class="EnlighterJSRAW" data-enlighter-language="shell">docker build -f Dockerfile-wasm -t docker-wasm:1.0 .</pre>

We can now run it, specifying a supported WASM runtime:

<pre class="EnlighterJSRAW" data-enlighter-language="shell">docker run --runtime=io.containerd.wasmedge.v1 docker-wasm:1.0</pre>

To compare, we can create a native image *with the same code*:

<pre class="EnlighterJSRAW" data-enlighter-language="dockerfile">FROM rust:1.70-slim-bullseye as build

COPY Cargo.toml .
COPY Cargo.lock .
COPY src src

RUN RUSTFLAGS='-C target-feature=+crt-static' cargo build --release #1

FROM scratch                                                        #2

COPY --from=build /target/release/wasm-native native</pre>

1. Make the binary self-sufficient
2. Can start from scratch

We can now compare the images size:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">REPOSITORY         TAG      IMAGE ID       CREATED       SIZE
docker-native      1.0      0c227194910a   7 weeks ago   7.09MB
docker-wasm        1.0      f9a88747f798   4 weeks ago   2.61MB</pre>

The Webassembly image is about one-third of the native binary package.

We cheat a bit because we add the WASM runtime... at runtime.

Building more complex images {#h2-3-building-more-complex-images}
-----------------------------------------------------------------

Let's see how we can add parameters to the binary and update the code accordingly:

<pre class="EnlighterJSRAW" data-enlighter-language="rust">use std::env;

fn main() {
    let args: Vec&lt;String&gt; = env::args().collect();
    if args.len() &gt; 1 {
        println!("Hello, world!");
    } else {
        println!("Hello, {}!", args[1]);
    }
}</pre>

Let's rebuild the images and compare again:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">REPOSITORY         TAG      IMAGE ID       CREATED          SIZE
docker-native      1.0      0c227194910a   7 weeks ago      7.09MB
docker-native      1.1      3ae029030e83   39 minutes ago   7.1MB
docker-wasm        1.0      f9a88747f798   4 weeks ago      2.61MB
docker-wasm        1.1      41e38b68f4e4   39 minutes ago   2.63MB</pre>

Executing HTTP calls? {#h2-4-executing-http-calls}
--------------------------------------------------

With this, it's easy to get carried away and start thinking big: what if we could execute HTTP calls?

I'll use the [reqwest](https://docs.rs/reqwest/latest/reqwest/) crate since I'm familiar with it. `reqwest` relies on Tokio.

<pre class="EnlighterJSRAW" data-enlighter-language="ini">[dependencies]
reqwest = { version = "0.11", features = ["json"] }
tokio = { version = "1.28", features = ["full"] }
serde = { version = "1.0", features = ["derive"] }</pre>

We can now update the code to make a request to and print the result:

<pre class="EnlighterJSRAW" data-enlighter-language="rust">#[tokio::main]
async fn main() {
    match get("http://httpbin.org/get").await {
        Ok(response) =&gt; {
            let result = response.json::&lt;GetBody&gt;().await;
            match result {
                Ok(json) =&gt; {
                    println!("{:#?}", json);
                }
                Err(err) =&gt; {
                    println!("{:#?}", err)
                }
            }
        }
        Err (err) =&gt; {
            println!("{:#?}", err)
        }
    }
}

#[derive(Debug, Serialize, Deserialize)]
struct GetBody {
    args: HashMap&lt;String, String&gt;,
    headers: HashMap&lt;String, String&gt;,
    origin: String,
    url: String,
}</pre>

Compiling this code reveals WASM limitations, though:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">#0 12.40 error: Only features sync,macros,io-util,rt,time are supported on wasm.
#0 12.40    --&gt; /usr/local/cargo/registry/src/index.crates.io-6f17d22bba15001f/tokio-1.28.2/src/lib.rs:488:1
#0 12.40     |
#0 12.40 488 | compile_error!("Only features sync,macros,io-util,rt,time are supported on wasm.");
#0 12.40     | ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^</pre>

WASM is not multi-threaded, while Tokio is *by default* . We can, however, configure Tokio to work in a single-thread environment. Let's start by using the features that we need: `macros` for the `main` function and `rt` for the tokio runtime.

<pre class="EnlighterJSRAW" data-enlighter-language="ini">tokio = { version = "1.28", features = ["rt", "macros"] }</pre>

Now, we can limit Tokio to the unique thread:

<pre class="EnlighterJSRAW" data-enlighter-language="rust">#[tokio::main(flavor = "current_thread")]
async fn main() {}</pre>

Compiling now works. However, I encounter issues when running:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">[2023-06-05 12:22:11.986] [error] instantiation failed: unknown import, Code: 0x62
[2023-06-05 12:22:11.986] [error]     When linking module: "__wbindgen_placeholder__" , function name: "__wbindgen_object_drop_ref"
[2023-06-05 12:22:11.986] [error]     At AST node: import description
[2023-06-05 12:22:11.986] [error]     At AST node: import section
[2023-06-05 12:22:11.986] [error]     At AST node: module
docker: Error response from daemon: Others("unknown import"): unknown.</pre>

The `reqwest` crate doesn't work with the WASI environment. Until it does, there's a fork aptly named [reqwest_wasi](https://docs.rs/reqwest_wasi/latest/reqwest/). The [tokio_wasi](https://docs.rs/tokio_wasi/latest/tokio/) is the WASI-compatible crate for `tokio`. Note that the latter's version needs to catch up. Let's replace the crates:

<pre class="EnlighterJSRAW" data-enlighter-language="ini">[dependencies]
reqwest_wasi = { version = "0.11", features = ["json"] }
tokio_wasi = { version = "1.25", features = ["rt", "macros"] }</pre>

With the new crates, compilation works, as well as execution. On the other side, the native image works flawlessly, with slight changes for the Dockerfile:

<pre class="EnlighterJSRAW" data-enlighter-language="rust">#docker build -f Dockerfile-native -t docker-native:1.2 .
FROM rust:1.70-slim-bullseye as build

COPY Cargo.toml .
COPY Cargo.lock .
COPY src src

RUN apt-get update &amp;&amp; apt-get install -y pkg-config libssl-dev   #1

RUN cargo build --release

FROM debian:bullseye-slim                                        #2

COPY --from=build /target/release/wasm-native native

ENTRYPOINT [ "/native" ]</pre>

1. Install required libraries for SSL
2. Change to a more complete base image to avoid installing additional libraries

Here's the final comparison:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">REPOSITORY         TAG      IMAGE ID       CREATED          SIZE
docker-native      1.0      0c227194910a   7 weeks ago      7.09MB
docker-native      1.1      3ae029030e83   22 hours ago     7.1MB
docker-native      1.2      4ff64cf9de46   7 hours ago      123MB
docker-wasm        1.0      1cc78a392477   23 hours ago     2.61MB
docker-wasm        1.1      41e38b68f4e4   22 hours ago     2.63MB
docker-wasm        1.2      6026f5bd789c   18 seconds ago   5.34MB</pre>

I didn't fiddle with the optimization of the native image. However, it would be hard to beat the WASM image, as it stands below 6MB!

There's no chance to implement an Axum server, though.

Conclusion {#h2-5-conclusion}
-----------------------------

I implemented a couple of WASM Docker images in this post, from the most straightforward Hello World to an HTTP client.

While the ecosystem has room for improvement, it's already possible to benefit from Docker's WASM support. The small size of WASM images is a huge pro.

The complete source code for this post can be found on [GitHub](https://github.com/ajavageek/rust-wasm-vs-native).

**To go further:**

* [Docker containerd image store](https://docs.docker.com/desktop/containerd/)
* [Docker+Wasm](https://docs.docker.com/desktop/wasm/)
* [WASI, first steps](https://k33g.hashnode.dev/wasi-first-steps)
* [WebAssembly: Docker without containers!](https://wasmlabs.dev/articles/docker-without-containers/)

*Originally published at [A Java Geek](https://blog.frankel.ch/wasm-docker/) on June 11^th^, 2023*
