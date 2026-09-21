---
title: "From Kotlin Scripting to Python"
date: "2024-03-07T01:04:15+00:00"
lastmod: "2026-09-21T06:03:55+00:00"
description: "GitHub offers a way to customize one's profile by allowing one to create a README in a specific repository, named as your profile, e.g.,…"
canonical: "https://blog.frankel.ch/kotlin-scripting-to-python/"
authors:
  - "nicolas-frankel"
image: "pexels-valentin-antonucci-691637.jpg"
categories:
  - "Use Cases"
related_posts:
  - "seasons-time-lapse-the-video"
  - "agent-memory-with-spring-ai-redis"
  - "checking-out-junie-a-coding-agent-by-jetbrains"
  - "debug-without-breakpoints"
frozen: false
---

GitHub offers a way to customize one's profile by allowing one to create a `README` in a specific repository, named as your profile, *e.g.* , `nfrankel/nfrankel`. A couple of years ago, I automated the update of my GitHub profile with up-to-date info: my latest blog posts, my upcoming talks, and the last recorded YouTube talk. I took the time to document how to do it [on this blog](https://blog.frankel.ch/automating-conference-submission-workflow/).

At the time, I chose Kotlin scripting because I was proficient enough in Kotlin, but I wanted to learn the scripting part. Over the years, I became more and more dissatisfied with the solution. I recently moved away from Kotlin Scripting to Python. In this post, I want to explain my reasons and document the migration.

## The previous situation

First things first, I'm a big proponent of Kotlin; my issues were in other areas.

When I first developed the code, I had the feeling that Kotlin scripting was an unloved child. The documentation is pretty straightforward about it:
> Kotlin scripting is Experimental. It may be dropped or changed at any time. Use it only for evaluation purposes. We appreciate your feedback on it in YouTrack.
>
> [Get started with Kotlin custom scripting](https://kotlinlang.org/docs/custom-script-deps-tutorial.html)

I didn't pay much attention then, believing it was temporary. The problem is that the status has stayed the same over two years. At some point, an experiment either graduates or the product is dropped. But Kotlin scripting stays in an intermediate Schrödinger state, neither really living nor truly dead.

Kotlin scripting allows dependencies via in-file annotations. Here's a dependency to the Freemarker templating engine:

```kotli
@file:DependsOn("org.freemarker:freemarker:2.3.32")
```

Dependabot and Renovate are bots that can regularly check dependencies for updates. Renovate manages more ecosystems than Dependabot, *, e.g.,*, Docker Compose, but still doesn't handle Kotlin scripting. It's a vicious circle because since not many use Kotlin scripting, it doesn't support it, and since it doesn't, not many use it. The consequence is that I had to check dependencies by myself regularly.

The final problem in my setup was entirely unrelated to Kotlin scripting but was the push to change anyway. I had put the script inside the magic GitHub repo. For this reason, the GitHub history contained daily README commits sprinkled with dependency upgrades and my changes.

## The target setup

I put the code in a different repo than the profile one. I now have two repositories:

* `nfrankel`: the target repo with the `README`
* `nfrankel-update`: the repo hosting the script

It fixes the latest issue I mentioned above.

I chose Python because I used it for simple scheduled jobs in the last couple of years, and I'm happy enough about the results. A simple search pointed me to the following dependencies:

* [requests](https://requests.readthedocs.io/en/latest/) to send HTTP requests
* [PyYAML](https://pyyaml.org/) to parse YAML payloads
* [Jinja2](https://palletsprojects.com/p/jinja/) for the templating engine

I manage them via Poetry. Renovate manages Poetry; it takes care of my first concern.

What the code does is precisely the same. It's about reading posts from this blog's [RSS feed](https://blog.frankel.ch/feed.xml), talks from my underlying blog repo, and videos from YouTube.

## Challenge

During the migration, I had a couple of hiccups, but the biggest challenge was committing to another repository.

Most GitHub actions either don't touch the repository hosting the action at all or commit to it and it only. In my case, I'm decoupling the script and the repo it acts upon:

```yaml
jobs:
  update:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout repo                                 #1
        uses: actions/checkout@v4
      - name: Checkout profile repo                         #2
        uses: actions/checkout@v4
        with:
          repository: nfrankel/nfrankel
          path: nfrankel
          token:  ${{ secrets.NFRANKEL_GITHUB_TOKEN }}      #3
# Set up and run the script here
      - name: Commit README and push                        #4
        uses: EndBug/add-and-commit@v9
        with:
          cwd: './nfrankel'
          add: README.adoc
          default_author: github_actions
          message: Automatically update README.adoc
```

1. Checkout current repo, the one hosting the script
2. Checkout profile repo in a sub-directory
3. Use an explicit GitHub token
4. Commit the `README` and push it to the profile repo

The magic happens in step 3 above. By default, GitHub allows you to commit to the repo of the action. When one wants to commit to other repos, one needs to have a regular token, the same one you'd use *outside* of GitHub. Besides, you must set the token in the checkout step, not the commit step. The rest is usual.

## Conclusion

In this post, I've explained why and how I migrated from Kotlin scripting to Python. In my context, the latter is a better fit than the former. It will be the case until JetBrains commits to Kotlin scripting.

The complete source code for this post can be found on [GitHub](https://github.com/nfrankel/nfrankel-update).

**To go further:**

* [Get started with Kotlin custom scripting](https://kotlinlang.org/docs/custom-script-deps-tutorial.html)
* [Poetry](https://python-poetry.org/)
* [Checkout multiple repos (nested)](https://github.com/actions/checkout#Checkout-multiple-repos-nested)
* [Automatic token authentication](https://docs.github.com/en/actions/security-guides/automatic-token-authentication)

*Originally published at [A Java Geek](https://blog.frankel.ch/kotlin-scripting-to-python/) on March 3^rd^, 2024*
