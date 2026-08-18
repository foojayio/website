---
title: "How I Automated Weekly Twitter/X Posts With Java, JBang and GitHub Actions"
slug: "how-i-automated-weekly-twitter-x-posts-with-java-jbang-and-github-actions"
date: "2026-04-14T16:05:06+00:00"
lastmod: "2026-04-15T06:12:54+00:00"
description: "Every Monday at 10 AM Eastern, @javaevolved now tweets a modern Java pattern — automatically. No manual steps, no third-party services, no cron servers. - by Bruno Borges"
authors:
  - "bruno-borges"
image: "social-post-banner.png"
categories:
  - "Foojay"
  - "Java"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Every Monday at 10 AM Eastern, [@javaevolved](https://x.com/javaevolved) now tweets a modern Java pattern --- automatically. No manual steps, no third-party services, no cron servers. Just a GitHub Actions workflow, a couple of JBang scripts, and the Twitter API.

Here's how it works, and how you can do the same for your own project.

## The Problem

[Java Evolved](https://javaevolved.github.io) is a static site with 113 code patterns showing the old way vs. the modern way to write Java. Each pattern has a title, summary, old/modern approach labels, JDK version, and a link to its detail page.

I wanted to promote each pattern on Twitter --- one per week, in random order, cycling forever. The requirements:

* **Fully automated** --- no manual tweeting
* **Pre-drafted tweets** --- reviewable and editable before they go live
* **Resumable** --- survives failures, picks up where it left off
* **Auditable** --- git history shows what was posted and when
* **Zero infrastructure** --- no servers, no databases, no paid services

## The Architecture

The system has three components:

```
content/*.yaml → [Queue Generator] → social/queue.txt
                                    → social/tweets.yaml
                                    → social/state.yaml

social/* → [Post Script] → Twitter API v2 → updated state

GitHub Actions cron → runs Post Script every Monday
```

Everything lives in the repository. State is tracked via committed files, not external databases.

## Component 1: The Queue \& Tweet Generator

**File:** `html-generators/generatesocialqueue.java`

A JBang script that scans all content YAML files, shuffles them randomly, and produces three files:

* **`social/queue.txt`** --- the posting order, one `category/slug` per line
* **`social/tweets.yaml`** --- pre-drafted tweet text for each pattern
* **`social/state.yaml`** --- a pointer tracking where we are in the queue

The tweet template looks like this:

```
☕ {title}

{summary}

{oldApproach} → {modernApproach} (JDK {jdkVersion}+)

🔗 https://javaevolved.github.io/{category}/{slug}.html

#Java #JavaEvolved
```

The generator also validates that every tweet fits within Twitter's 280-character limit. If a summary is too long, it's automatically truncated with an ellipsis. Of the 113 patterns, 12 needed truncation.

### Handling New Patterns

When you re-run the generator after adding new content files, it detects new patterns and appends them to the end of the existing queue --- preserving the current order and any manual tweet edits. Deleted or renamed patterns are automatically pruned.

Use `--reshuffle` to force a full reshuffle when the cycle is exhausted.

## Component 2: The Post Script

**File:** `html-generators/socialpost.java`

Another JBang script that:

1. Reads the current index from `social/state.yaml`
2. Looks up the next pattern key in `social/queue.txt`
3. Retrieves the pre-drafted tweet text from `social/tweets.yaml`
4. Posts to the Twitter API v2 using OAuth 1.0a
5. Updates the state file **only after confirmed API success**

### OAuth 1.0a in Java

I initially planned to use a shell script with `curl` and `openssl` for OAuth signing. That turned out to be a bad idea --- percent-encoding, signature base strings, and nonce generation are error-prone in Bash.

Instead, the post script uses Java's built-in `java.net.http.HttpClient` and `javax.crypto.Mac` for HMAC-SHA1 signing. Here's the core of the OAuth signature:

```
// Build signature base string
var paramString = oauthParams.entrySet().stream()
    .map(e -> percentEncode(e.getKey()) + "=" + percentEncode(e.getValue()))
    .collect(Collectors.joining("&"));

var baseString = method + "&" + percentEncode(url) + "&" + percentEncode(paramString);
var signingKey = percentEncode(consumerSecret) + "&" + percentEncode(tokenSecret);

// HMAC-SHA1
var mac = javax.crypto.Mac.getInstance("HmacSHA1");
mac.init(new javax.crypto.spec.SecretKeySpec(
    signingKey.getBytes(UTF_8), "HmacSHA1"));
var signature = Base64.getEncoder().encodeToString(
    mac.doFinal(baseString.getBytes(UTF_8)));
```

The script also supports `--dry-run` to preview the next tweet without posting:

```
$ jbang html-generators/socialpost.java --dry-run

Queue has 113 entries, current index: 1
Pattern: language/guarded-patterns
Tweet (200 chars):
---
☕ Guarded patterns with when

Add conditions to pattern cases using when guards.

Nested if → when Clause (JDK 21+)

🔗 https://javaevolved.github.io/language/guarded-patterns.html

#Java #JavaEvolved
---
DRY RUN — not posting.
```

## Component 3: The GitHub Actions Workflow

**File:** `.github/workflows/social-post.yml`

```
name: Weekly Social Post

on:
  schedule:
    - cron: '0 14 * * 1'  # Every Monday at 14:00 UTC (10 AM ET)
  workflow_dispatch:       # Manual trigger

concurrency:
  group: social-post
  cancel-in-progress: false

jobs:
  post:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v6
      - uses: actions/setup-java@v5
        with:
          distribution: 'temurin'
          java-version: '25'
      - uses: jbangdev/setup-jbang@main

      - name: Post to Twitter
        env:
          TWITTER_CONSUMER_KEY: ${{ secrets.TWITTER_APP_CONSUMER_KEY }}
          # ... other secrets
        run: jbang html-generators/socialpost.java

      - name: Commit updated state
        run: |
          git add social/state.yaml
          git commit -m "chore: update social post state [skip ci]"
          git pull --rebase
          git push
```

A few details worth noting:

* **`concurrency` group** prevents double-posts if a manual dispatch overlaps with the cron
* **`[skip ci]`** in the commit message prevents the state update from triggering other workflows
* **Social files live in `social/`** , not `content/` --- the deploy workflow watches `content/**`, so keeping state separate avoids unnecessary site rebuilds
* **`git pull --rebase`** before push handles the rare case where another commit lands between checkout and push

## The Economics

Twitter's API pricing means each tweet costs about $0.01. With 113 patterns posted weekly:

* **\~$0.52/year**
* **\~2.2 years** of unique content per cycle before reshuffling

That's essentially free for a perpetual social media presence.

## Built in a Single Copilot CLI Session

This entire feature --- the queue generator, the post script, the GitHub Actions workflow, the tweet drafts, the documentation updates --- was built in a single interactive session with [GitHub Copilot CLI](https://githubnext.com/projects/copilot-cli). From planning to the first live tweet, everything happened in the terminal.

The session included planning the architecture, getting a rubber-duck critique (which caught several issues --- like using shell for OAuth signing and putting state files where they'd trigger deploys), implementing all three components, testing locally with `--dry-run`, committing, pushing, and triggering the first real tweet.

You can read the full session transcript here: [gist.github.com/brunoborges/40ef1b5e9b05de279dab64e443b96a11](https://gist.github.com/brunoborges/40ef1b5e9b05de279dab64e443b96a11)

## What I'd Do Differently

* **Add Bluesky support** --- the AT Protocol API is simpler than Twitter's OAuth 1.0a. The architecture already supports it; just add a second API call in the post script.
* **Content hash tracking** --- if a pattern's title or summary changes, the pre-drafted tweet goes stale. A hash per entry would flag which drafts need regeneration.

## Try It Yourself

The entire implementation is open source at [github.com/javaevolved/javaevolved.github.io](https://github.com/javaevolved/javaevolved.github.io). You'll need:

1. A Twitter Developer account with OAuth 1.0a credentials (Read + Write)
2. Java 25+ and [JBang](https://www.jbang.dev/)
3. Content in YAML with `title`, `summary`, `oldApproach`, `modernApproach`, `jdkVersion`, `category`, and `slug` fields

Generate the queue, review the drafts, push, and let GitHub Actions handle the rest.

*Follow [@javaevolved](https://x.com/javaevolved) for a new modern Java pattern every Monday.*
