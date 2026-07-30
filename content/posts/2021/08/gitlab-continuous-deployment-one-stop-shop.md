---
title: "GitLab: Your Continuous Deployment One-Stop Shop | Foojay Today"
slug: "gitlab-continuous-deployment-one-stop-shop"
date: "2021-08-04T07:44:20+00:00"
lastmod: "2023-06-12T08:57:32+00:00"
description: "This article shows how one could offload the Docker part of your build pipeline from your local machine to GitLab using the Kaniko image. It saves on time and resources. The only regret I have is that I should have done it much earlier as I'm a huge proponent of automation!"
canonical: "https://blog.frankel.ch/gitlab-continuous-deployment-one-stop-shop/"
authors:
  - "nicolas-frankel"
image: "https://foojay.io/wp-content/uploads/2021/07/Method-Draw-Image1.png"
categories:
  - "DevOps"
  - "GitLab"
  - "Tools"
  - "Use Cases"
tags:
related_posts:
enlighterjs: true
frozen: false
---

This week, I want to take a break from my Rust series and focus on a different subject. I've already written about [my blogging stack](https://blog.frankel.ch/my-blogging-stack-publishing-process/) in detail.

However, I didn't touch on one facet and that facet is how I generate the static pages from Jekyll. As I describe in the blog post, I've included quite a couple of customizations. Some of them require external dependencies, such as:

* A for PlantUML diagrams generation
* The *graphviz* package for the same reason
* etc.

All in all, it means that I require a fully configured system. I solved this problem by using containerization, namely Docker. Within the `Dockerfile`, I'm able to install all required dependencies. Then, in my GitLab build file, I can reference this image and benefit from all its capabilities.

<pre class="EnlighterJSRAW" data-enlighter-language="yaml">image: registry.gitlab.com/nfrankel/nfrankel.gitlab.io:latest

# ...</pre>

Updating, the hard way {#h2-0-updating-the-hard-way}
----------------------------------------------------

Jekyll is built on top of Ruby. Shared libraries in Ruby are known as *gems* . I'm using a few of them, along with the Jekyll gem itself. As a long-time Maven user, I searched for the equivalent dependency management utility in the Ruby world and stumbled upon [https://bundler.io/\[Bundler\]:](https://bundler.io/%5BBundler%5D:)
> Bundler provides a consistent environment for Ruby projects by tracking and installing the exact gems and versions needed.

Bundler rests on a `Gemfile` file. It's similar to npm's `package.json`. When you execute `bundle install`, it creates a `Gemfile.lock` with the latest gems' version; with `bundle update`, it updates them.

So far, this is how my update process looked like:

1. Update the gems to their latest version
2. Build the Docker image on my laptop
3. Upload the image to my project's GitLab registry
4. Commit the change to the lock file
5. Push
6. In turn, that triggers the build on GitLab and deploys my site on GitLab Pages.

It has several drawbacks:

* It requires Docker on my laptop. Granted, I have it already, but not everybody is happy with that
* The build takes time, as well as CPU time
* The image takes up storage. I can clean it up, but it's an additional waste of my time.
* It clogs my network. As my upload speed is very limited, I cannot do anything that involves the Internet when I'm uploading.

Updating, the smart way {#h2-1-updating-the-smart-way}
------------------------------------------------------

I recently stumbled upon the excellent [series of GitLab cheatsheets](https://dev.to/jphi_baconnais/series/12928). In the [6^th^ part](https://dev.to/zenika/gitlabcheatsheet-6-registry-2bjo), the author mentions [Kaniko](https://github.com/GoogleContainerTools/kaniko):
> kaniko is a tool to build container images from a Dockerfile, inside a container or Kubernetes cluster.
>
> kaniko doesn't depend on a Docker daemon and executes each command within a Dockerfile completely in userspace. This enables building container images in environments that can't easily or securely run a Docker daemon, such as a standard Kubernetes cluster.
>
> kaniko is meant to be run as an image: `gcr.io/kaniko-project/executor`.

It means that you can move the Docker image build part to the build process itself. The new process becomes:

So far, this is how my update process looked like:

1. Update the gems to their latest version
2. Commit the change to the lock file
3. Push
4. Enjoy!

To achieve that, I had to browse through the documentation quite intensively. I also moved the build file to the "new" syntax. Here's the new version:

<pre class="EnlighterJSRAW" data-enlighter-language="yaml">stages:
  - image                                                                        # 1
  - deploy                                                                       # 1

build:                                                                           # 2
  stage: image                                                                   # 3
  image:
    name: gcr.io/kaniko-project/executor:debug                                   # 4
    entrypoint: [""]                                                             # 5
  script:
    - mkdir -p /kaniko/.docker
    - echo "{\"auths\":{\"$CI_REGISTRY\":{\"auth\":\"$(echo -n ${CI_REGISTRY_USER}:${CI_REGISTRY_PASSWORD} | base64)\"}}}" &gt; /kaniko/.docker/config.json # 6
    - /kaniko/executor --context $CI_PROJECT_DIR --dockerfile $CI_PROJECT_DIR/Dockerfile --destination $CI_REGISTRY_IMAGE:$CI_COMMIT_TAG                 # 7
  only:
    refs:
      - master
    changes:
      - Gemfile.lock                                                             # 8

pages:                                                                           # 2
  stage: deploy                                                                  # 3
  image:
    name: registry.gitlab.com/nfrankel/nfrankel.gitlab.io:latest                 # 9
# ...</pre>

1. Define the *stages* . Stages are ordered: here, `image` runs before `deploy`.
2. Define the *jobs*
3. A job is associated with a stage. For the record, jobs associated with the same stage run in parallel.
4. Use the `debug` flavor of the Kaniko Docker image. While it's not necessary, this image logs what it's doing to improve debugging if something goes wrong.
5. Reset the `entrypoint`
6. Create the credentials file used by Kaniko to push to the Docker registry in the next line
7. Build the image using the provided `Dockerfile` and push it to the project's Docker registry. Note that GitLab passes all environment variables used here
8. Run this job only if the `Gemfile.lock` file has been changed
9. Generate the static site using the previously generated image

Conclusion {#h2-2-conclusion}
-----------------------------

This article shows how one could offload the Docker part of your build pipeline from your local machine to GitLab using the Kaniko image. It saves on time and resources. The only regret I have is that I should have done it much earlier as I'm a huge proponent of automation.

I miss one last step: schedule a job that updates dependencies and creates a *merge request* *à la* Dependabot.

**To go further:**

* [GitLab Cheatsheet Series](https://dev.to/jphi_baconnais/series/12928)
* [Use kaniko to build Docker images](https://docs.gitlab.com/ee/ci/docker/using_kaniko.html)
* [Keyword reference for the .gitlab-ci.yml file](https://docs.gitlab.com/ee/ci/yaml/)
* [Least Privilege Container Builds with Kaniko on GitLab](https://www.youtube.com/watch?v=d96ybcELpFs)
* [GitLab's predefined variables reference](https://docs.gitlab.com/ee/ci/variables/predefined_variables.html)

*Originally published at [A Java Geek](https://blog.frankel.ch/gitlab-continuous-deployment-one-stop-shop/) on August 1^st^, 2021*

*[JRE]: Java Runtime Environment
