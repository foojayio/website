---
title: "GitLab Pages Preview"
slug: "gitlab-pages-preview"
date: "2023-06-12T08:55:31+00:00"
lastmod: "2023-06-13T10:44:04+00:00"
description: "How to preview GitLab Pages and share the preview's URL with teammates in a couple of steps."
canonical: "https://blog.frankel.ch/gitlab-pages-preview/"
authors:
  - "nicolas-frankel"
image: "https://foojay.io/wp-content/uploads/2023/06/pexels-noelle-otto-906018.jpg"
categories:
  - "DevOps"
  - "GitLab"
tags:
related_posts:
enlighterjs: true
frozen: false
---

When I write Apache APISIX-related blog posts, I want my colleagues to review them first. However, it's my blog, and since I mix personal and business posts, I want to keep them from the repository. I need a preview, accessible only to a few, something like [Vercel's preview](https://vercel.com/docs/concepts/deployments/preview-deployments). I'm using GitLab Pages, and there's no such out-of-the-box feature.

I tried two methods: GitHub gists and PDFs. Both have issues.

Gists don't display as nicely as the final page. I tried to improve the situation by using [DocGist](https://gist.asciidoctor.org/). It's an improvement, even if not the panacea.

Moreover, gists don't display images since I write my posts in Asciidoc. I've to set images in comments, and it breaks the flow. I've tried to [attach the images to the gist](https://gist.github.com/mroderick/1afdd71aa69f6b29601d335751a1a9be), but they don't appear in the flow of the post in any case. The pro over comments is that they are ordered; the con is that I need to change the Asciidoc.

I used gists because I'm used to GitHub reviews. But since it's my blog, I neither need nor want the same kind of reviews as in a regular Merge Request. I need people to point me to when something needs to be clarified, or I missed a logical jump, not that I made a typo (I use Grammarly for this). For this reason, a PDF export of a post is enough to review.

However, PDFs have issues on their own: a web "page" is potentially endless, while a regular PDF page cuts the former into standard pages. Splits can happen across diagrams. Besides, PDFs make distribution much harder.

In this post, I'll describe how I configured GitLab Pages to get the preview I want.

Summary of GitLab Pages {#h2-0-summary-of-gitlab-pages}
-------------------------------------------------------

GitLab Pages are akin to [GitHub Pages](https://pages.github.com/):
> With GitLab Pages, you can publish static websites directly from a repository in GitLab. To publish a website with Pages, you can use any static site generator, like Gatsby, Jekyll, Hugo, Middleman, Harp, Hexo, or Brunch. You can also publish any website written directly in plain HTML, CSS, and JavaScript.
>
> -- [GitLab Pages](https://docs.gitlab.com/ee/user/project/pages/)

That's how you see this blog post. I found no preview feature for GitLab Pages. I asked experts to no avail; GitLab doesn't offer previews.

Laying out the work {#h2-1-laying-out-the-work}
-----------------------------------------------

I didn't believe it initially, but you only need to create a dedicated artifact. Since the artifact consists of web files, the browser will render them.

The idea is to create such an artifact, accessible under an URL, which cannot be easily predicted. I can then share the URL with my colleagues and ask for their review. To start with, we can copy the existing build on `master`:

<pre class="EnlighterJSRAW" data-enlighter-language="yaml">stages:
  - preview

preview:
  stage: preview
  image:
    name: registry.gitlab.com/nfrankel/nfrankel.gitlab.io:latest
  before_script: cd /builds/nfrankel/nfrankel.gitlab.io
  script: bundle exec jekyll b --future -t --config _config.yml -d public
  artifacts:
    paths:
      - public
  only:
    refs:
      - preview
  variables:
    JEKYLL_ENV: production</pre>

At this point, the site is available at `https://$CI_PROJECT_NAMESPACE.gitlab.io/-/$CI_PROJECT_NAME/-/jobs/$CI_JOB_ID/artifacts/public/index.html`. Many issues need fixing, though.

Making it work {#h2-2-making-it-work}
-------------------------------------

Let's fix the issues by order of importance.

### Fixing access permissions {#h3-3-fixing-access-permissions}

The project is *private*; hence, only I can access the artifact: it defeats the initial purpose of offering the preview to others.

I want to give my teammates only limited access to my GitLab repository. I gave them *Guest access* , according to the [Principle of least privilege](https://en.wikipedia.org/wiki/Principle_of_least_privilege). However, it still didn't work.

As per the documentation, you must also make your pipeline *public*. Go to Settings \> CI/CD \> General pipelines and check the Public pipelines checkbox.

### Fixing relative links {#h3-4-fixing-relative-links}

I use [Jekyll](https://jekyllrb.com/) to build HTML from Asciidoc. To generate links, Jekyll uses two configuration parameters:

* The domain, *e.g.* , , set with `url`
* The path, *e.g.* , `/`, set with `baseurl`

Both are different on the preview. You must set those parameters in a YAML configuration file; there's no environment variable alternative.

Let's change the build accordingly:

<pre class="EnlighterJSRAW" data-enlighter-language="yaml">preview:
  stage: preview
  image:
    name: registry.gitlab.com/nfrankel/nfrankel.gitlab.io:latest
  before_script:
    - cd /builds/nfrankel/nfrankel.gitlab.io
    - "printf 'url: https://%s.gitlab.io\n' $CI_PROJECT_NAMESPACE &gt;&gt; _config_preview.yml"                        #1
    - "printf 'baseurl: /-/%s/-/jobs/%s/artifacts/public/\n' $CI_PROJECT_NAME $CI_JOB_ID &gt;&gt; _config_preview.yml" #2
    - cat _config_preview.yml                                                                                    #3
  script: bundle exec jekyll b --future -t --config _config.yml,_config_preview.yml -d public                    #4</pre>

1. Set `url` using the `CI_PROJECT_NAMESPACE` environment variable. I could have used a hard-coded value since it's static, but it makes the script more reusable
2. Set `baseurl` using the `CI_PROJECT_NAME` and `CI_JOB_ID` environment variables. The latter is the random part of the requirement
3. Display the configuration's content for debugging purposes
4. Use it!

### Improving usability {#h3-5-improving-usability}

It's a bore trying to distribute the correct URL each time. Better to write it down in the console after building:

<pre class="EnlighterJSRAW" data-enlighter-language="yaml">after_script: echo https://$CI_PROJECT_NAMESPACE.gitlab.io/-/$CI_PROJECT_NAME/-/jobs/$CI_JOB_ID/artifacts/public/index.html</pre>

There's still one missing bit. GitLab Pages offer an index page. For example, if you request <https://blog.frankel.ch>, they will serve the root `index.html`. With plain artifacts, it's not the case. Given that I only want to offer a single post for preview, it's not an issue, so I didn't research the configuration further.

### Usage {#h3-6-usage}

At this point, I only need to push to my `preview` branch:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">git push --force origin HEAD:preview</pre>

Icing on the cake, we don't need to have the branch locally; just push to the remote one.

Conclusion {#h2-7-conclusion}
-----------------------------

In this post, I showed how to preview GitLab Pages and share the preview's URL with teammates in a couple of steps. The hardest part was to realize that web artifacts are rendered regularly with the browser.

**To go further:**

* [GitLab CI/CD permissions](https://docs.gitlab.com/ee/user/permissions.html#gitlab-cicd-permissions)
* [Set artifacts visibility independent of the project or group visibility](https://gitlab.com/gitlab-org/gitlab/-/issues/17544)
* [Change which users can view your pipelines](https://docs.gitlab.com/ee/ci/pipelines/settings.html#change-which-users-can-view-your-pipelines)

*Originally published at [A Java Geek](https://blog.frankel.ch/gitlab-pages-preview/) on June 4^th^, 2023*
