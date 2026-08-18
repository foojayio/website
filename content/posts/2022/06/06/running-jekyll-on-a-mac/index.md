---
title: "Running Jekyll on a Mac"
slug: "running-jekyll-on-a-mac"
date: "2022-06-06T17:41:49+00:00"
lastmod: "2022-06-06T17:41:50+00:00"
description: "Here is the complete process for setting up Jekyll on a Mac to help other developers who want to do the same and my future self."
canonical: "https://blog.frankel.ch/running-jekyll-mac/"
authors:
  - "nicolas-frankel"
image: "chemical-scientist-test-chemistry-chemist-student.jpg"
categories:
  - "Tools"
tags:
related_posts:
  - "5-tips-to-create-secure-docker-images-for-java-applications"
  - "are-java-security-updates-important"
  - "beginning-javafx-with-intellij"
enlighterjs: true
frozen: false
---

At the beginning of the year, I had two new Macs in a row in one month. I changed my company and had to return my previous laptop. Thus, I ordered a replacement one, but due to the current hardware shortage, the shipping took weeks: I had to rent one in the meanwhile.

It means I had to install my Jekyll stack twice in a row. The first time took quite some time; the second one was much faster.

In this post, I'd like to write it down once and for all to help other developers who want to do the same and my future self.

A new Mac OS system comes with an already installed Ruby distribution. Unfortunately, you cannot upgrade it. On my Mac, at the time of this writing, it's `2.6.8p205 (2021-07-07 revision 67951)`.

The first step is to install a more modern version. For this, we need first to install `rbenv`:
> Use rbenv to pick a Ruby version for your application and guarantee that your development environment matches production. Put rbenv to work with Bundler for painless Ruby upgrades and bulletproof deployments. -- <https://github.com/rbenv/rbenv>

**Note.** The following relies on [Homebrew](https://brew.sh/), the command-line package manager for Mac OS. That's the first thing I install when I acquire a new one.

```bash
brew install rbenv
```

Next, we have to initialize our shell. For that, let's update our shell profile:

```bash
eval "$(rbenv init - zsh)"
```

I'm using the default Z-shell. If you're using another shell, locate its profile.

Then, we need to execute the profile in the current Terminal window:

```bash
. ~/.zshrc
```

At this point, we should list all available Ruby distributions:

```bash
rbenv install --list
```

The output should be similar to the following:

```
2.6.9
2.7.5
3.0.3
3.1.1
jruby-9.3.4.0
mruby-3.0.0
rbx-5.0
truffleruby-22.0.0.2
truffleruby+graalvm-22.0.0.2
```

Let's install the latest "standard" version:

```bash
rbenv install 3.1.1
```

We can now use this version. Go to your Jekyll folder and type:

```bash
rbenv local 3.1.1
```

I manage the dependencies of my Jekyll blog with [Bundler](https://bundler.io/). Bundler is a Gem like all others:

```bash
gem install bundler
```

Dependencies are written in my `Gemfile`. We can execute `bundler` to install them:

```bash
bundle install
```

At this stage, a standard Jekyll blog should work. Yet, my blog also uses [Asciidoctor](https://asciidoctor.org/), and more importantly, [asciidoctor-diagram](https://docs.asciidoctor.org/diagram-extension/latest/). I draw my diagrams using the [PlantUML](https://plantuml.com/) syntax. PlantUML requires a JVM and `graphviz`.

For the JVM, you can either install a dedicated one or install JRuby instead of a simple Ruby distribution. `graphviz` requires a dedicated executable:

```bash
brew install graphviz
```

*Et voilà !*

If I had to follow these steps more frequently than this, I'd probably automate it further.

**To go further:**

* [Jekyll](https://jekyllrb.com/)
* [rbenv](https://github.com/rbenv/rbenv)
* [rbenv cheatsheet](https://devhints.io/rbenv)
* [Bundler](https://bundler.io/)

*Originally published at [A Java Geek](https://blog.frankel.ch/running-jekyll-mac/) on June 5^th^, 2022*
