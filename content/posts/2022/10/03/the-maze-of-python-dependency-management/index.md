---
title: "The Maze of Python Dependency Management"
date: "2022-10-03T12:49:11+00:00"
lastmod: "2022-10-03T12:51:36+00:00"
description: "The default Python's dependency management system breaks automated version upgrades, take a look at the pip-compile alternative."
canonical: "https://blog.frankel.ch/maze-python-dependency-management/"
authors:
  - "nicolas-frankel"
image: "snake-g2283fc6ac.jpg"
categories:
  - "DevOps"
related_posts:
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "controlling-electronics-with-jbang-on-the-raspberry-pi"
  - "continuous-production-profiling-and-diagnostics"
frozen: false
---

For over 20 years, I've developed code for the JVM, first in Java, then in Kotlin.

However, the JVM is not a silver bullet, *e.g.*, in scripts:

1. Virtual machines incur additional memory requirements.
2. In many cases, the script doesn't run long enough to gain any benefit performance-wise. The bytecode is interpreted and never compiles to native code.

For these reasons, I now write my scripts in Python. One of them collects social media metrics from different sources and stores them in BigQuery for analytics.

I'm not a Python developer, but I'm learning - the hard way.

In this article, I'd like to shed some light on dependency management in Python.

## Just enough dependency management in Python

On the JVM, dependency management seems like a solved problem. First, you choose your build tool, preferably Maven or the alternative-that-I-shall-not-name. Then, you declare your *direct* dependencies, and the tool manages the indirect ones. It doesn't mean there aren't gotchas, but you can solve them more or less quickly.

Python dependency management is a whole different world. To start with, in Python, the runtime and its dependencies are system-wide. There's only a single runtime for a system, and dependencies are shared across all projects on this system. Because it's not feasible, the first thing to do when starting a new project is to create a virtual environment.
> The solution for this problem is to create a virtual environment, a self-contained directory tree that contains a Python installation for a particular version of Python, plus a number of additional packages. Different applications can then use different virtual environments. To resolve the earlier example of conflicting requirements, application A can have its own virtual environment with version 1.0 installed while application B has another virtual environment with version 2.0. If application B requires a library be upgraded to version 3.0, this will not affect application A's environment.
>
> -- [Virtual Environments and Packages](https://docs.python.org/3/tutorial/venv.html)

Once this is done, things start in earnest.

Python provides a dependency management tool called `pip` out-of-the-box:
> You can install, upgrade, and remove packages using a program called pip.
>
> -- [Managing Packages with pip](https://docs.python.org/3/tutorial/venv.html#managing-packages-with-pip)

The workflow is the following:

1. One installs the desired dependency in the virtual environment:

   ```bash
   pip install flask
   ```

2. After one has installed all required dependencies, one saves them in a file named `requirements.txt` by convention:

   ```bash
   pip freeze > requirements.txt
   ```

   The file should be saved in one's along with the regular code.
3. Other project developers can install the same dependencies by pointing `pip` to `requirements.txt`:

   ```bash
   pip install -r requirements.txt
   ```

Here's the resulting `requirements.txt` from the above commands:

```
click==8.1.3
Flask==2.2.2
itsdangerous==2.1.2
Jinja2==3.1.2
MarkupSafe==2.1.1
Werkzeug==2.2.2
```

## Dependencies and transitive dependencies

Before describing the issue, we need to explain what are *transitive* dependencies. A transitive dependency is a dependency that's not required by the project directly but by one of the project's dependencies, or a dependency's dependency, all the way down. In the example above, I added the `flask` dependency, but `pip` installed 6 dependencies in total.

We can install the `deptree` dependency to check the dependency tree.

```bash
pip install deptree
deptree
```

The output is the following:

```
Flask==2.2.2  # flask
  Werkzeug==2.2.2  # Werkzeug>=2.2.2
    MarkupSafe==2.1.1  # MarkupSafe>=2.1.1
  Jinja2==3.1.2  # Jinja2>=3.0
    MarkupSafe==2.1.1  # MarkupSafe>=2.0
  itsdangerous==2.1.2  # itsdangerous>=2.0
  click==8.1.3  # click>=8.0
# deptree and pip trees
```

It reads as the following: `Flask` requires `Werkzeug`, which in turn requires `MarkupSafe`. `Werkzeug` and `MarkupSafe` qualify as transitive dependencies for my project.

The version part is interesting as well. The first part mentions the installed version, while the commented part refers to the compatible version range. For example, `Jinja` requires version `3.0` or above, and the installed version is `3.1.2`.

The installed version is the latest *compatible* version found by `pip` at install time. `pip` and `deptree` know about the compatibility in the `setup.py` file distributed along each library:
> The setup script is the centre of all activity in building, distributing, and installing modules using the Distutils. The main purpose of the setup script is to describe your module distribution to the Distutils, so that the various commands that operate on your modules do the right thing.
>
> -- [Writing the Setup Script](https://docs.python.org/3/distutils/setupscript.html)

Here for Flask:

```python
from setuptools import setup

setup(
    name="Flask",
    install_requires=[
        "Werkzeug >= 2.2.2",
        "Jinja2 >= 3.0",
        "itsdangerous >= 2.0",
        "click >= 8.0",
        "importlib-metadata >= 3.6.0; python_version < '3.10'",
    ],
    extras_require={
        "async": ["asgiref >= 3.2"],
        "dotenv": ["python-dotenv"],
    },
)
```

## Pip and transitive dependencies

The problem appears because I want my dependencies to be up-to-date. For this, I've configured Dependabot to watch for new versions of dependencies listed in `requirements.txt`. When such an event occurs, it open a in my repo. Most of the time, the PR works like a charm, but in a few cases, an error occurs when I run the script after I merge. It looks like the following:

    ERROR: libfoo 1.0.0 has requirement libbar<2.5,>=2.0, but you'll have libbar 2.5 which is incompatible.

The problem is that Dependabot opens a PR for *every* library listed. But a new library version can be released, which falls outside the range of compatibility.

Imagine the following situation. My project needs the `libfoo` dependency. In turn, `libfoo` requires the `libbar` dependency. At install time, `pip` uses the latest version of `libfoo` and the latest *compatible* version of `libbar`. The resulting `requirements.txt` is:

```
libfoo==1.0.0
libbar==2.0
```

Everything works as expected. After a while, Dependabot runs and finds that `libbar` has released a new version, *e.g.* , `2.5`. Faithfully, it opens a PR to merge the following change:

```
libfoo==1.0.0
libbar==2.5
```

Whether the above issue appears depends solely on how `libfoo 1.0.0` specified its dependency in `setup.py`. If `2.5` falls within the compatible range, it works; if not, it won't.

## `pip-compile` to the rescue

The problem with `pip` is that it lists transitive dependencies and direct ones. Dependabot then fetches the latest versions of all dependencies but doesn't verify if transitive dependencies version updates fall within the range. It could potentially check, but the `requirements.txt` file format is not structured: it doesn't differentiate between direct and transitive dependencies. The obvious solution is to list only direct dependencies.

The good news is that `pip` allows listing only direct dependencies; it installs [transitive dependencies automatically](https://disjoint.ca/til/2016/03/18/pip-and-transitive-dependencies/). The bad news is that we now have two `requirements.txt` options with no way to differentiate between them: some list only direct dependencies, and other lists all of them.

It calls for an alternative. The [pip-tools](https://github.com/jazzband/pip-tools) has one:

1. One lists their direct dependencies in a `requirements.in` file, which has the same format as `requirements.txt`
2. The `pip-compile` tool generates a `requirements.txt` from the `requirements.in`.

For example, given our Flask example:

```
#
# This file is autogenerated by pip-compile with python 3.10
# To update, run:
#
#    pip-compile requirements.in
#
click==8.1.3
    # via flask
flask==2.2.2
    # via -r requirements.in
itsdangerous==2.1.2
    # via flask
jinja2==3.1.2
    # via flask
markupsafe==2.1.1
    # via
    #   jinja2
    #   werkzeug
werkzeug==2.2.2
    # via flask
```

```bash
pip install -r requirements.txt
```

It has the following benefits and consequences:

* The generated `requirements.txt` contains comments to understand the dependency tree
* Since `pip-compile` generates the file, you shouldn't save it in the VCS
* The project is compatible with legacy tools that rely on `requirements.txt`
* Last but not least, it changes the installation workflow. Instead of installing packages and then saving them, one first list packages and then install them.

Moreover, Dependabot can manage dependencies version upgrades of `pip-compile`.

## Conclusion

This post described the default Python's dependency management system and how it breaks automated version upgrades.

We continued to describe the `pip-compile` alternative, which solves the problem.

Note that a dependency management specification exists for Python, [PEP 621 – Storing project metadata in pyproject.toml](https://peps.python.org/pep-0621/).

It's similar to a Maven's POM, with a different format.

It's overkill in the context of my script, as I don't need to distribute the project.

But should you do, know that `pip-compile` is compatible with it.

**To go further:**

* [Virtual Environments and Packages](https://docs.python.org/3/tutorial/venv.html)
* [Managing Packages with pip](https://docs.python.org/3/tutorial/venv.html#managing-packages-with-pip)
* [pip tools](https://github.com/jazzband/pip-tools)
* [PEP 621 – Storing project metadata in pyproject.toml](https://peps.python.org/pep-0621/)

*Originally published at [A Java Geek](https://blog.frankel.ch/maze-python-dependency-management/) on September, 11^th^, 2022*

*[PR]: Pull Request
*[VCS]: Version Control System
