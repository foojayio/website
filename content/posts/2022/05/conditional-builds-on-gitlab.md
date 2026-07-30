---
title: "Conditional Builds on GitLab | Foojay.io Today"
slug: "conditional-builds-on-gitlab"
date: "2022-05-09T09:16:26+00:00"
lastmod: "2023-06-12T08:56:41+00:00"
description: "Learn how to run a build on GitLab only if a condition is met, especially helpful when conditions are expensive and time-consuming operations."
canonical: "https://blog.frankel.ch/conditional-build-gitlab/"
authors:
  - "nicolas-frankel"
image: "https://foojay.io/wp-content/uploads/2022/05/pexels-james-wheeler-1578750.jpg"
categories:
  - "DevOps"
  - "GitLab"
tags:
related_posts:
  - "gitlab-continuous-deployment-one-stop-shop"
  - "how-to-beautify-your-github-repo"
  - "github-actions-with-java-part-1"
  - "boxlang-aws-azure-and-google-secrets-manager-module-released"
enlighterjs: true
frozen: false
---

Regular readers of my articles know that I'm using [Jekyll](https://jekyllrb.com/) to generate my static blog site, together with GitLab. When I push on the `master` branch, it triggers the generation job.

However, Jekyll is Ruby-based and requires a couple of Gem dependencies. I've also added a few plugins. For this reason, I've created a [Docker image with all required dependencies](https://blog.frankel.ch/musings-dockerfile-jekyll/). Regularly, I update the versions in the `Gemfile.lock` via Bundler. Afterward, I need to rebuild the Docker image.

Hence, two jobs are necessary:

1. After a push containing a change that influences the Docker image, build it.
2. After any push, generate the site.

For the first, the trigger condition is whether any of the following files have been changed: `Gemfile.lock`, `Dockerfile` and `.gitlab-ci.yml`.

The problem is how to run a build **only** if the condition is met, as it's an expensive and time-consuming operation. GitLab's build file configuration offers a solution for this. In a job, you can configure an `only` clause to run only if a condition is met. The condition can be:

* A reference, *e.g.*, a branch, or a tag
* A trigger, *e.g.*, a push, the web UI or an API call
* The value of a variable
* A change on a specific file
* A couple of others

The next to last option is the answer to our problem. We can configure a set of files, and if any of them has been changed, the build should run. Otherwise, do nothing.

It translates into the following structure:

<pre class="EnlighterJSRAW" data-enlighter-language="yaml">stages:
  - image                                        # 1
  - deploy                                       # 1

build:                                           # 2
  stage: image                                   # 2
  image:
    name: gcr.io/kaniko-project/executor:debug
    entrypoint: [""]
  script: # Build the Docker image
  only:
    refs:
      - master                                   # 4
    changes:
      - Gemfile.lock                             # 5
      - Dockerfile                               # 5
      - .gitlab-ci.yml                           # 5

pages:                                           # 3
  stage: deploy                                  # 3
  image:
    name: registry.gitlab.com/nfrankel/nfrankel.gitlab.io:latest
  script: # Generate the site
  only:
    refs:
      - master</pre>

1. Define the two stages
2. Define the `build` job in the `image` stage. The job creates the Docker image (via Kaniko)
3. Define the `pages` job in the `deploy` stage. The job generates the site via Jekyll
4. Build the `master` branch only
5. Only build the image if any of these files have changed

At this point, each change triggers the build: the image building job runs before the site generation, but GitLab skips the former if the image hasn't changed.

**To go further:**

* [only:changes / except:changes](https://docs.gitlab.com/ee/ci/yaml/index.html#onlychanges--exceptchanges)
* [only:changes / except:changes examples](https://docs.gitlab.com/ee/ci/jobs/job_control.html#onlychanges--exceptchanges-examples)
* [GitHub: Running your workflow only when a push affects specific files](https://docs.github.com/en/actions/using-workflows/events-that-trigger-workflows#running-your-workflow-only-when-a-push-affects-specific-files)

*Originally published at [A Java Geek](https://blog.frankel.ch/conditional-build-gitlab/) on May 8^th^, 2022*
