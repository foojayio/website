---
title: "Feedback from calling Rust from Python"
slug: "feedback-from-calling-rust-from-python"
date: "2023-11-22T10:32:55+00:00"
lastmod: "2023-11-22T10:32:56+00:00"
description: "Improving low-level integration with ctypes to the generic ready-to-use pyo3 library."
canonical: "https://blog.frankel.ch/feedback-rust-from-python/"
authors:
  - "nicolas-frankel"
image: "Screenshot-2023-10-29-at-21.31.31.png"
categories:
  - "Research"
  - "Tutorials"
tags:
related_posts:
  - "rust-jvm"
  - "apache-apisix-loves-rust"
  - "java-panama-polyglot-rust-part-4"
  - "research-measuring-energy-consumption-in-programming-languages-for-ai-applications"
frozen: false
---

I got plenty of feedback on my post about [Calling Rust from Python](https://blog.frankel.ch/rust-from-python/):

* [Hacker News](https://news.ycombinator.com/item?id=37811953)
* [/r/python](https://www.reddit.com/r/Python/comments/1733gl6/calling_rust_from_python/)
* [/r/rust](https://www.reddit.com/r/rust/comments/1733gsb/calling_rust_from_python/)

Many comments mentioned `pyo3`, and I should use it instead of cooking my own. Thanks to the authors, I checked: in this post, I explain what it is and how I migrated my code.

## What is pyo3?

> Rust bindings for Python, including tools for creating native Python extension modules. Running and interacting with Python code from a Rust binary is also supported.
>
> [-- PyO3 user guide](https://pyo3.rs/v0.20.0/)

Indeed, `pyo3` fits my use case, calling Rust from Python. Even better, it handles converting Python types to Rust types and back again. Finally, it offers the `maturin` utility to make the interaction between the Python project and the Rust project seamless.

## Maturin

> Build and publish crates with pyo3, rust-cpython, cffi and uniffi bindings as well as rust binaries as python packages.
>
> [-- Maturin on GitHub](https://github.com/PyO3/maturin)

`maturin` is available via `pip install`. It offers several commands:

* `new`: create a new Cargo project with maturin configured
* `build`: build the wheels and store them locally
* `publish`: build the crate into a Python package and publish it to pypi
* `develop`: build the crate as a Python module directly into the current virtual environment, making it available to Python

Note that Maturin started as a companion project to `pyo3` but now offers rust-cpython, cffi and uniffi bindings.

## Migrating the project

The term migrating is a bit misleading here since we will start from scratch to fit Maturin's usage. However, we will achieve the same end state. I won't paraphrase [the tutorial](https://pyo3.rs/v0.20.0/#using-rust-from-python) since it works seamlessly. Ultimately, we have a fully functional Rust project with a single `sum_as_string()` function, which we can call in a Python shell. Note the dependency to `pyo3`:

```bash
pyo3 = "0.20.0"
```

The second step is to re-use the material from the previous project. First, we add our `compute()` function at the end of the `lib.rs` file:

```rust
#[pyfunction]                                                                            //1
fn compute(command: &str, a: Complex<f64>, b: Complex<f64>) -> PyResult<Complex<f64>> {  //2-3
    match command {
        "add" => Ok(a + b),
        "sub" => Ok(a - b),
        "mul" => Ok(a * b),
        _ => Err(PyValueError::new_err("Unknown command")),                              //4
    }
}
```

1. The `pyfunction` macro allows the use of the function in Python
2. Use regular Rust types for parameters; `pyo3` can convert them
3. We need to return a `PyResult` type, which is an alias over `Result`
4. Return a specific Python error if the command doesn't match

`pyo3` automatically handles conversion for most types. However, complex numbers require an additional feature. We also need to migrate from the `num` crate to the `num-complex`:

```ini
pyo3 = { version = "0.20.0" , features = ["num-complex"]}
num-complex = "0.4.4"
```

To convert custom types, you must implement traits `FromPyObject` for parameters and `ToPyObject` for return values.

Finally, we only need to add the function to the module:

```rust
#[pymodule]
fn rust_over_pyo3(_py: Python, m: &PyModule) -> PyResult<()> {
    m.add_function(wrap_pyfunction!(sum_as_string, m)?)?;
    m.add_function(wrap_pyfunction!(compute, m)?)?;              //1
    Ok(())
}
```

1. Add the function to the module

At this point, we can use Maturin to test the project:

```bash
maturin develop
```

After the compilation finishes, we can start a Python shell in the virtual environment:

```bash
python

>>> from rust_over_pyo3 import compute
>>> compute('add',1+3j,-5j)
(1-2j)
>>> compute('sub',1+3j,-5j)
(1+8j)
```

## Finishing touch

The above setup allows us to use Rust from a Python shell but not in a Python file. To leverage the default, we must create a Python project inside the Rust project, whose name matches the Rust module name. Since I named my lib `rust_over_pyo3`, here's the overall structure:

```bash
my-project
├── Cargo.toml
├── rust_over_pyo3
│   └── main.py
├── pyproject.toml
└── src
    └── lib.rs
```

To use the Rust library in Python, we need first to build the library.

```bash
maturin build --release
```

We manually move the artifact from `/target/release/maturin/librust_over_pyo3.dylib` to `rust_over_pyo3.so` under the Python package. We can also run `cargo build --release` instead; in this case, the source file is directly under `/target/release`.

At this point, we can use the library as any other Python module:

```python
from typing import Optional
from click import command, option

from rust_over_pyo3 import compute                                                #1

@command()
@option('--add', 'command', flag_value='add')
@option('--sub', 'command', flag_value='sub')
@option('--mul', 'command', flag_value='mul')
@option('--arg1', help='First complex number in the form x+yj')
@option('--arg2', help='Second complex number in the form x\'+y\'j')
def cli(command: Optional[str], arg1: Optional[str], arg2: Optional[str]) -> None:
    n1: complex = complex(arg1)
    n2: complex = complex(arg2)
    result: complex = compute(command, n1, n2)                                    #2
    print(result)

if __name__ == '__main__':
    cli()
```

1. Regular Python import
2. Look, ma, it works!

## Conclusion

In this post, I improved the low-level integration with `ctypes` to the generic ready-to-use `pyo3` library. I barely scratched the surface, though; `pyo3` is a powerful, well-maintained library with plenty of features.

I want to thank again everyone who pointed me in this direction.

The complete source code for this post can be found on [GitHub](https://github.com/ajavageek/rust-over-pyo3).

**To go further:**

* [PyO3 user guide](https://pyo3.rs/v0.20.0/)
* [maturin](https://github.com/PyO3/maturin)

*Originally published at [A Java Geek](https://blog.frankel.ch/feedback-rust-from-python/) on October 29^th^, 2023*
