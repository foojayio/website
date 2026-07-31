---
title: "Preparing to Move Away from Twitter"
slug: "preparing-to-move-away-from-twitter"
date: "2022-12-16T06:42:40+00:00"
lastmod: "2022-12-16T07:36:16+00:00"
description: "I opened my Twitter account more than 13 years ago, in August 2009. For 12 years, I kept focusing on professional-related content: Java, the JVM, - by Nicolas Frankel"
canonical: "https://blog.frankel.ch/move-away-twitter/"
authors:
  - "nicolas-frankel"
image: "Mastodon_Logotype_Simple.svg.png"
categories:
  - "Tools"
tags:
related_posts:
  - "foojay-on-mastodon-an-update"
  - "foojay-mastodon-service-here-it-is"
  - "java-mastodon-service-the-feedback"
  - "not-a-lucid-web3-dream-anymore-x402-erc-8004-a2a-and-the-next-wave-of-ai-commerce"
enlighterjs: true
frozen: false
---

I opened my Twitter account more than 13 years ago, in August 2009. For 12 years, I kept focusing on professional-related content: Java, the JVM, programming, etc. I built my audience, trying to promote good technical content, either my own or stuff that I enjoyed reading.

Then, on February 24^th^, Russia invaded Ukraine. My first visit to Ukraine was in 2014, just after the Maidan revolution. During eight years, I returned there often and made plenty of friends.

Of course, I wanted to support them and started to use my Twitter account to fight Russian disinformation. I discovered how toxic Twitter could be after having stayed out of politics since the beginning: bad faith, logical fallacies, flat-out lies, reverse accusations, personal attacks, etc.

With the acquisition of Twitter by Elon Musk, I'm afraid it's going to get much worse - case in point:

[![](Screenshot-2022-12-11-at-17.59.21-782x1024.png)](https://twitter.com/SarahKSilverman/status/1589418271308386304)

Some (most?) people I know planned or already had moved away. The target seems to be Mastodon, an alternate decentralized Open Source using the [ActivityPub](https://en.wikipedia.org/wiki/ActivityPub) protocol:
> Mastodon is free and open-source software for running self-hosted social networking services. It has microblogging features similar to the Twitter service, which are offered by a large number of independently run Mastodon nodes (technically known as instances), each with its own code of conduct, terms of service, privacy options, and moderation policies.
>
> Each user is a member of a specific Mastodon instance (also called a server), which can interoperate as a federated social network, allowing users on different nodes to interact with each other. This is intended to give users the flexibility to select a server whose policies they prefer, but keep access to a larger social network. Mastodon is also part of the Fediverse ensemble of server platforms, which use shared protocols allowing users to also interact with users on other compatible platforms, such as PeerTube and Friendica.
>
> -- <https://en.wikipedia.org/wiki/Mastodon_(software>)

Forewarned is forearmed. I plan to stay on Twitter as long as possible while building up my Mastodon account with the same content. Then, if (when?) all hell breaks loose, I can just jump ship.

Evaluating the alternatives {#h2-0-evaluating-the-alternatives}
---------------------------------------------------------------

Let's state things clearly: I believe I'm a good developer because I'm lazy. There's no way I'm going to copy-paste content on both channels. Plus, I'm using Twitter's scheduling feature, so I need something else.

I'm one of many who want to keep a foot in each realm. For example, I found that Martin Fowler is also following [the same strategy](https://martinfowler.com/articles/exploring-mastodon.html). However, his approach is "specific":
> One of the main things I wanted to do with Mastodon was to replicate my twitter feed there, so that folks who would rather follow me on Mastodon could get everything. To do this, I used moa.party. You have to give it credentials to access both your Twitter and Mastodon feeds, which is a little worrisome, but my Mastodon-aware colleagues have used it without problems.

There's no way I'd give my credentials to a third party! I searched further and found this gem:
> This tool synchronizes posts from Mastodon to Twitter and back. It does not matter where you post your stuff - it will get synchronized to the other!
>
> -- [Mastodon Twitter Sync](https://github.com/klausi/mastodon-twitter-sync)

It looked exactly what I was searching for!

Mastodon Twitter Sync {#h2-1-mastodon-twitter-sync}
---------------------------------------------------

The tool provides two execution options:

* A Docker image
* Run from source - Rust

The [Docker image](https://hub.docker.com/r/klausi/mastodon-twitter-sync/tags) has no tags, save `latest`, and I had some issues mapping volumes. Hence, I decided to run from source. Again, I'm lazy and don't want to run the tool manually. I've been using GitHub Actions for a couple of years to schedule my scripts.

Let's start with the following:

<pre class="EnlighterJSRAW" data-enlighter-language="yaml">name: Sync Twitter to and from Mastodon
on:
  schedule:
    - cron: "24 */2 * * *"                               #1
  workflow_dispatch:
jobs:
  sync:
    runs-on: ubuntu-latest
    steps:
      - name: Check out the synchronization code         #2
        uses: actions/checkout@v3
        with:
          repository: klausi/mastodon-twitter-sync
      - name: Install Rust                               #3                            
        uses: actions-rs/toolchain@v1
        with:
          toolchain: stable
          profile: minimal                               #4
      - name: Execute synchronization                    #5
        uses: actions-rs/cargo@v1
        with:
          command: run
          args: --release</pre>

1. Schedule every two hours, 24 minutes after the hour
2. Checkout the sync project's code
3. Install the Rust toolchain
4. The toolchain comes in different flavors called [profiles](https://rust-lang.github.io/rustup/concepts/profiles.html). For scripting, `minimal` is enough, providing only `rustc`, `rust-std`, and `cargo`
5. Run the code

### Managing credentials {#h3-2-managing-credentials}

Spoiler: the workflow doesn't work. By default, the code runs interactively: it will ask for credentials to connect to both Twitter and Mastodon. Alternatively, the project accepts a configuration file containing all data - `mastodon-twitter-sync.toml`.

My advice is to run the project interactively locally once. If the TOML file doesn't exist, the executable will ask for credentials and generate a new one containing them. But we shouldn't add a file containing credential data in plain text on a Git repo. Instead, we shall:

1. Encrypt the file
2. Add and commit the encrypted file
3. During workflow run, decrypt the file using a GitHub Action secret

<pre class="EnlighterJSRAW" data-enlighter-language="yaml">jobs:
  sync:
    runs-on: ubuntu-latest
    steps:
      - name: Install GPG to decrypt the configuration file
        run: sudo apt-get update &amp;&amp; sudo apt-get install -y gnupg
      - name: Decrypt the configuration file
        run: gpg --quiet --batch --yes --decrypt --passphrase="$GPG_PASSPHRASE" --decrypt mastodon-twitter-sync.toml.gpg &gt; mastodon-twitter-sync.toml 
        env:
          GPG_PASSPHRASE: ${{ secrets.GPG_PASSPHRASE }}</pre>

At this point, we have mixed the Rust source code with our configuration file in the same Git repository. Handling such a project involves a lot of `git rebase`, which I want to avoid. Let's keep the code separate with its dedicated lifecycle locally.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">mastodon-twitter-sync-job               #1
|_ .github
|  |_ workflows
|    |_ sync.yml                        #2
|_ mastodon-twitter-sync.toml.gpg       #3

mastodon-twitter-sync                   #4
|_ src
|_ ...</pre>

1. My project
2. GitHub action
3. Encrypted credential file
4. Independent sync project

We need to change how we check out the code:

<pre class="EnlighterJSRAW" data-enlighter-language="yaml">jobs:
  sync:
    runs-on: ubuntu-latest
    steps:
      - name: Check out the repo itself
        uses: actions/checkout@v3
        with:
          path: job
      - name: Check out the synchronization code
        uses: actions/checkout@v3
        with:
          repository: klausi/mastodon-twitter-sync
          path: code</pre>

When we run the workflow, the layout is the following:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">|_ job
|  |_ .github
|  |  |_ workflows
|  |    |_ sync.yml
|  |_ mastodon-twitter-sync.toml.gpg
|
|_ code
|  |_ src
|  |_ ...</pre>

Henceforth, we should update the decrypting and run the steps accordingly:

<pre class="EnlighterJSRAW" data-enlighter-language="yaml">jobs:
  sync:
    runs-on: ubuntu-latest
    steps:
      - name: Decrypt the configuration file
        run: |
          gpg --quiet --batch --yes --decrypt --passphrase="$GPG_PASSPHRASE"
              --decrypt job/mastodon-twitter-sync.toml.gpg &gt; mastodon-twitter-sync.toml #1
        env:
          GPG_PASSPHRASE: ${{ secrets.GPG_PASSPHRASE }}
      - name: Execute synchronization
        uses: actions-rs/cargo@v1
        with:
          command: run
          args: --manifest-path=./code/Cargo.toml --release                             #2
</pre>

1. Decrypt from the `job` subfolder in the current root folder
2. Run in the current folder using the `code` subfolder

### Sync only once {#h3-3-sync-only-once}

The project creates a `post_cache.json` file that contains all previously synced content to avoid duplicating the same content during each execution. We need to take it into account:

<pre class="EnlighterJSRAW" data-enlighter-language="yaml">jobs:
  sync:
    runs-on: ubuntu-latest
      - name: Update post cache
        run: &gt;
          cp ./post_cache.json ./job/ 2&gt;/dev/null || :    #1
      - name: Commit and push post cache
        uses: EndBug/add-and-commit@v7                    #2
        with:
          cwd: './job'
          add: post_cache.json
          default_author: github_actions
          message: Update post cache</pre>

1. Copy the `post_cache.json` in the `job` subfolder. Only succeed the step if the job synchronizes no content, and the file is generated.
2. Commit back the file if it has changed

### Workflow optimization {#h3-4-workflow-optimization}

In the current state, each run downloads the dependencies and compiles the project, even though the source code stays the code; it's highly inefficient.

The platform provides a generic [caching GitHub Action](https://github.com/marketplace/actions/cache). However, I found [rust-cache](https://github.com/Swatinem/rust-cache), a Rust-specific one that provides appropriate defaults for Rust. Let's use it to cache the dependencies and the executable across workflow executions (provided some parameters stay the same):

<pre class="EnlighterJSRAW" data-enlighter-language="yaml">jobs:
  sync:
    runs-on: ubuntu-latest
    steps:
      - name: Install Rust
        uses: actions-rs/toolchain@v1
        with:
          toolchain: stable
          profile: minimal
      - name: Cache executable             #1
        uses: Swatinem/rust-cache@v2
        with:
          workspaces: code                 #2</pre>

1. Must be installed after Rust install, as the cache key contains Rust-specific data
2. Cache artifacts located in the `code` subfolder

### Final notes {#h3-5-final-notes}

With this setup, I need to update the repo with the new JSON cache file before I commit any change to the workflow. I could create a dedicated repo for it to improve the situation, but it's good enough for now.

The connection to Mastodon is fickle; a lot of actions fail with the following message:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Error connecting to Mastodon: Http(
    reqwest::Error {
        kind: Request,
        url: Url {
            scheme: "https",
            cannot_be_a_base: false,
            username: "",
            password: None,
            host: Some(
                Domain(
                    "mastodon.top",
                ),
            ),
            port: None,
            path: "//api/v1/accounts/verify_credentials",
            query: None,
            fragment: None,
        },
        source: TimedOut,
    },
)</pre>

It's not an issue *per se*; it just means that synchronization lags. Should I move to a more reliable instance or even host my own?

So far, I've kept Twitter as my source of truth. I post content there, and it should appear on Mastodon. However, synchronization should happen both ways. Once I make Mastodon my main channel, I don't need to change the above work.

Conclusion {#h2-6-conclusion}
-----------------------------

Twitter's new owner claims to promote "comedy" but suspends accounts that make fun of him. At the same time, he claims to be a proponent of free speech but confuses opinion with information. The advertising market may curb his misguided views, but it's still being determined.

In the meantime, I'm not willing to sit idly. Mastodon is gaining a lot of momentum. In this post, I've explained how you can cross the chasm while still keeping your presence on Twitter until you don't want to. Thanks to [klausi](https://github.com/klausi/) for their fantastic sync project and patience with my stumbling.

You can find the source code for this

The source code is available on [GitHub](https://github.com/nfrankel/mastodon-twitter-sync-job/).

**To go further:**

* [Me on Mastodon](https://mastodon.top/web/@frankel)
* [Martin Fowler's adventures in Mastodon](https://martinfowler.com/articles/exploring-mastodon.html)
* [Mastodon documentation](https://docs.joinmastodon.org/)
* [Moa bridge (be careful!)](https://moa.party/)
* [Mastodon Twitter Sync](https://github.com/klausi/mastodon-twitter-sync)

*Originally published at [A Java Geek](https://blog.frankel.ch/move-away-twitter/) on December 11^th^, 2022*
