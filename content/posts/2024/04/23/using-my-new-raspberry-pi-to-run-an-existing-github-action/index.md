---
title: "Using my new Raspberry Pi to run an existing GitHub Action"
date: "2024-04-23T18:45:04+00:00"
lastmod: "2024-04-24T21:02:14+00:00"
description: "Recently, I mentioned how I refactored the script that kept my GitHub profile up-to-date. Since Geecon Prague, I'm also a happy owner of a Raspberry Pi."
canonical: "https://blog.frankel.ch/raspberry-pi-github-action/"
authors:
  - "nicolas-frankel"
image: "pexels-alessandro-oliverio-1472443.jpg"
categories:
  - "DevOps"
  - "Raspberry Pi"
related_posts:
  - "a-better-way-to-use-gradle-with-github-actions"
  - "devops-for-developers-continuous-integration-github-actions-and-sonar-cloud"
  - "how-to-release-a-java-module-with-jreleaser-to-maven-central-with-github-actions"
  - "whats-new-in-actions-setup-java-5-4-and-5-5-signature-verification-kona-jdk-and-a-better-maven-experience"
frozen: false
---

Recently, I mentioned [how I refactored](https://blog.frankel.ch/kotlin-scripting-to-python/) [the script](https://github.com/nfrankel/nfrankel-update/) that kept [my GitHub profile](https://github.com/nfrankel/) up-to-date. Since Geecon Prague, I'm also a happy owner of a Raspberry Pi:

[](https://twitter.com/nicolas_frankel/status/1715313240568197233)

[

{{< img src="Screenshot-2024-03-10-at-17.20.56-557x510.png" class="aligncenter size-medium" width="557" height="510" >}}

](https://twitter.com/nicolas_frankel/status/1715313240568197233)

Though the current setup works flawlessly - and is free, I wanted to experiment with self-hosted runners. Here are my findings.

## Context

GitHub offers a large free usage of GitHub Actions:
> GitHub Actions usage is free for standard GitHub-hosted runners in public repositories, and for self-hosted runners. For private repositories, each GitHub account receives a certain amount of free minutes and storage for use with GitHub-hosted runners, depending on the account's plan. Any usage beyond the included amounts is controlled by spending limits.
>
> -- [About billing for GitHub Actions](https://docs.github.com/en/billing/managing-billing-for-github-actions/about-billing-for-github-actions)

Yet, the policy can easily change tomorrow. Free tier policies show a regular trend of shrinking down when:

* A large enough share of users use the product, *lock in*
* Shareholders want more revenue
* A new finance manager decides to cut costs
* The global economy shrinks down
* A combination of the above

Forewarned is forearmed. I like to try options before I need to choose one. Case in point: what if I need to migrate?

## The theory

GitHub Actions comprise two components:

* The GitHub Actions infrastructure itself.  
  It hosts the scheduler of jobs.
* Runners, which do run the jobs

By default, jobs run on GitHub's runners. However, it's possible to configure one's job to run on other runners, whether on-premise or in the Cloud: these are called **self-hosted runners**.

The [documentation](https://docs.github.com/en/actions/hosting-your-own-runners/managing-self-hosted-runners/about-self-hosted-runners) regarding how to create self-hosted runners gives all the necessary information to build one, so I won't paraphrase it.

I noticed two non-trivial issues, though. First, if you have jobs in different repositories, you **need** to set up a job for each repository. Runner groups are only available for organization repositories. Since most of my repos depend on my regular account, I can't use groups. Hence, you must duplicate each repository's package on the runner's Pi.

In addition, there's no dedicated package: you must untar an archive. This means there's no way to upgrade the runner version easily.

That being said, I expected the migration to be one line long:

```yaml
jobs:
  update:
    #runs-on: ubuntu-latest
    runs-on: self-hosted
```

It's a bit more involved, though. Let's detail what steps I had to undertake in my repo to make the job work.

## The practice

GitHub Actions depend on Docker being installed on the runner. Because of this, I thought jobs ran in a dedicated image: it's plain wrong. Whatever you script in your job happens on the running system. Case in point, the initial script installed Python and Poetry.

```yaml
jobs:
  update:
    runs-on: ubuntu-latest
    steps:
      - name: Set up Python 3.x
        uses: actions/setup-python@v5
        with:
          python-version: 3.12
      - name: Set up Poetry
        uses: abatilo/actions-poetry@v2
        with:
          poetry-version: 1.7.1
```

In the context of a temporary container created during each run, it makes sense; in the context of a stable, long-running system, it doesn't.

Raspbian, the Raspberry default operating system, already has Python 3.11 installed. Hence, I had to downgrade the version configured in Poetry. It's no big deal because I don't use any specific Python 3.12 feature.

```ini
[tool.poetry.dependencies]
python = "^3.11"
```

Raspbian forbids the installation of any Python dependency in the primary environment, which is a very sane default. To install Poetry, I used the regular APT package manager:

```bash
sudo apt-get install python-poetry
```

The next was to handle secrets. On GitHub, you set the secrets on the GUI and reference them in your scripts via environment variables:

```yaml
jobs:
  update:
    runs-on: ubuntu-latest
    steps:
      - name: Update README
        run: poetry run python src/main.py --live
        env:
          BLOG_REPO_TOKEN: ${{ secrets.BLOG_REPO_TOKEN }}
          YOUTUBE_API_KEY: ${{ secrets.YOUTUBE_API_KEY }}
```

It allows segregating individual steps so that a step has access to only the environmental variables it needs. For self-hosted runners, you set environment variables in an existing `.env` file inside the folder.

```
jobs:
  update:
    runs-on: ubuntu-latest
    steps:
      - name: Update README
        run: poetry run python src/main.py --live
```

If you want more secure setups, you're on your own.

Finally, the architecture is a pull-based model. The runner constantly checks if a job is scheduled. To make the runner a service, we need to use out-of-the-box scripts inside the runner folder:

```bash
sudo ./svc.sh install
sudo ./svc.sh start
```

The script uses `systemd` underneath.

## Conclusion

Migrating from a GitHub runner to a self-hosted runner is not a big deal but requires changing some bits and pieces.

Most importantly, you need to understand the script runs on the machine.

This means you need to automate the provisioning of a new machine in the case of crashes.

I'm considering the benefits of running the runner inside a container on the Pi to roll back to my previous steps.

I'd be happy to hear if you found and used such a solution. In any case, I'm not migrating any more jobs to self-hosted for now.

**To go further:**

* [About billing for GitHub Actions](https://docs.github.com/en/billing/managing-billing-for-github-actions/about-billing-for-github-actions)
* [About self-hosted runners](https://docs.github.com/en/actions/hosting-your-own-runners/managing-self-hosted-runners/about-self-hosted-runners)
* [Configuring the self-hosted runner application as a service](https://docs.github.com/en/actions/hosting-your-own-runners/managing-self-hosted-runners/configuring-the-self-hosted-runner-application-as-a-service)

*Originally published at [A Java Geek](https://blog.frankel.ch/raspberry-pi-github-action/) on March 10^th^ 2024*
