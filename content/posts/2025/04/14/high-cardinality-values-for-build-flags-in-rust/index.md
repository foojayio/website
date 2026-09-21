---
title: "High-cardinality values for build flags in Rust"
date: "2025-04-14T19:24:47+00:00"
lastmod: "2026-09-21T06:03:04+00:00"
description: "While working on my demo on WebAssembly and Kubernetes, I wanted to create three different binaries based on the same code: Native: compile the Rust code…"
canonical: "https://blog.frankel.ch/high-cardinality-values-build-flags-rust/"
authors:
  - "nicolas-frankel"
image: "cover_large.jpeg"
categories:
  - "Tutorials"
related_posts:
  - "feedback-from-calling-rust-from-python"
  - "introduction-tower"
  - "java-panama-polyglot-rust-part-4"
frozen: false
---

While working on my [demo](https://github.com/ajavageek/wasm-kubernetes) on WebAssembly and Kubernetes, I wanted to create three different binaries based on the same code:

* Native: compile the Rust code to regular native code as a baseline
* Embed: compile to WebAssembly and use the WasmEdge runtime image as the base Docker image
* Runtime: compile to WebAssembly, use a base `scratch` image as my base image, and set the runtime when running the code

The code itself is an HTTP server that offers a single endpoint. For the sake of the demo, I wanted it to return the flavour of the underlying image.

```bash
curl localhost:3000/get
```

```json
{"source": "native", "data": {}}
```

The idea is to have a single codebase that I can compile to native or WebAssembly. I solved this requirement by using a `cfg` compile flag.

```rust
#[cfg(flavor = "native")]
const FLAVOR: &str = "native";

#[cfg(flavor = "embed")]
const FLAVOR: &str = "embed";

#[cfg(flavor = "runtime")]
const FLAVOR: &str = "runtime";

// Use FLAVOR later in the returned HTTP response
```

In my case, I can live with three different values. But what if I had to deal with a high cardinality, say, fifteen? It would be quite a bore to define them manually. I searched for a solution and found Build Scripts.
> Some packages need to compile third-party non-Rust code, for example C libraries. Other packages need to link to C libraries which can either be located on the system or possibly need to be built from source. Others still need facilities for functionality such as code generation before building (think parser generators).
>
> Cargo does not aim to replace other tools that are well-optimized for these tasks, but it does integrate with them with custom build scripts. Placing a file named `build.rs` in the root of a package will cause Cargo to compile that script and execute it just before building the package.
>
> -- [Build Scripts](https://doc.rust-lang.org/cargo/reference/build-scripts.html)

Let's try with a simple `build.script` at the root of the crate:

```rust
fn main() {
    println!("Hello from build script!");
}
```

```bash
cargo build
```

```
Compiling high-cardinality-cfg-compile v0.1.0 (/Users/nico/projects/private/high-cardinality-cfg-compile)
 Finished `dev` profile [unoptimized + debuginfo] target(s) in 0.08s
```

There isn't any log–nothing seems to happen. The documentation reveals the reason:
> The output of the script is hidden from the terminal during normal compilation.  
>
> If you would like to see the output directly in your terminal, invoke Cargo as "very verbose" with the `-vv` flag.

Let's compile again with the verbose flag:

```bash
cargo build --vv
```

The output is what we expect:

```
...
[high-cardinality-cfg-compile 0.1.0] Hello from build script!
...
```

Now is the time to do something useful: I'll use the build script to replace the hard-coded constants above. For that, I'll generate a Rust code file that contains the value passed for flavor.

Note the **build script runs before compilation** . Hence, it doesn't have access to the `cfg` flags. Instead, I'll pass the value as a `Cargo.toml` package metadata key value.

```ini
[package.metadata]
flavor = "foobar"
```

For our build script to read the `Cargo.toml` metadata, we need to add a build dependency:

```ini
[build-dependencies]
cargo_metadata = "0.19.1"
```

The code is straightforward:

```rust
fn main() {
    let metadata = MetadataCommand::new()                                         //1
        .exec()
        .expect("Failed to fetch cargo metadata");

    let package = metadata.root_package().expect("No root package found");        //2

    let flavor = package                                                          //3
        .metadata
        .get("flavor")
        .and_then(|f| f.as_str())
        .expect("flavor is not set in Cargo.toml under [package.metadata]");

    let dest_path = Path::new("src").join("flavor.rs");                           //4

    fs::write(&dest_path, format!("pub const FLAVOR: &str = \"{}\";\n", flavor))  //5
        .expect("Failed to write flavor.rs");

    println!("cargo:rerun-if-changed=Cargo.toml");
    println!("cargo:warning=FLAVOR written to {}", dest_path.display());
}
```

1. Get the metadata
2. Get the package where we want to create the new file
3. Read the flavor value
4. Reference `src/flavor.rs`
5. Write `pub const FLAVOR: &str = `

On the code side, we only need to use the `FLAVOR` const from the `flavor` module:

```rust
mod flavor;

fn main() {
    println!("Hello from flavor {}", flavor::FLAVOR);
}
```

Running the program outputs the flavor configured in the `Cargo.toml` file:

```bash
cargo run
```

```
Hello from flavor foobar
```

## Conclusion

In this, I've shown how to use high-cardinality build parameters from the package metadata. I could have achieved the same with environment variables, but I wanted to learn about the `metadata` section of the `Cargo.toml` file.

The `build.rs` file is a handy trick for achieving goals that aren't possible with regular Cargo features.

**To go further:**

* [Build Scripts](https://doc.rust-lang.org/cargo/reference/build-scripts.html)

*Originally published at [A Java Geek](https://blog.frankel.ch/high-cardinality-values-build-flags-rust/) on April 13^th^, 2025*
