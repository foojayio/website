---
title: "Announcing Foojay 2.0"
date: 2026-08-20T09:00:00+00:00
description: "Foojay has a new home: a static, open-source site where publishing an article is a pull request. Same URLs, same archive, faster, and new features."
authors:
  - "frankdelporte"
image: "announcing-foojay-2-0.png"
categories:
  - "Foojay"
related_posts:
  - "how-to-submit-your-next-article-on-foojay-io"
  - "redesigned-foojay-home"
  - "a-week-of-housekeeping-what-changed-on-foojay-io"
---

Foojay has moved house. Not the address — every URL you have bookmarked, linked
or cited still works — but everything behind it. Foojay is now a static site,
its entire content lives in a public Git repository, and publishing an article
is a pull request.

If you only read one paragraph: **nothing you have written has moved, and
nothing you need to do has changed.** The rest of this post is what is different
and why you might care.

## Why change anything

Foojay was a WordPress site, and it did the job for six years and 2,000+
articles. But a few things had been quietly getting in the way.

**Publishing had friction that had nothing to do with writing.** An author with
something good to say had to get an account, learn an editor, and hope the
formatting survived. Meanwhile most of our authors already live in a text editor
and a Git client all day.

**As the amount of posts grew, WordPress became slower.** But not only page load time increased. The whole process to add new features, improve the styling, fix bugs, constantly update plugins and WordPress itself, it all started taking too much time, effort, and money for a community driven project.

**AI happened.** WordPress was one of the few options to build something like Foojay for a long time. But with Hugo and AI assisted coding, replacing what we had into something faster and more user-friendly for visitors, authors, and maintainers, it became possible to build what you see now in a matter of days with a very small budget.

## What Foojay 2.0 actually is

A [Hugo](https://gohugo.io/) site built from
[a public repository](https://github.com/foojayio/website), deployed
automatically whenever something lands on the main branch. Concretely:

| | Before | Now                                                   |
|---|---|-------------------------------------------------------|
| Publishing | CMS account + web editor | a pull request                                        |
| Search | server-side | a static index, in your browser                       |
| Comments | a plugin | GitHub Discussions                                    |
| Read counts | a plugin | a counter we run, that stores a slug and a number |
| Analytics | Google Analytics via 360 KB of Tag Manager | the same Google Analytics, loaded directly |
| The archive | in a database | 2,147 Markdown files you can read on GitHub           |

Everything came across: 2,147 articles, 344 author profiles, 47 glossary
entries, the JUG directory, the calendar, the sponsors,... Every legacy path
still resolves, including the ones nobody would think to check — the old
`/blog/` scheme, the retired slugs, and the URLs that changed when an article
was renamed years ago.

## The part that matters to authors

Your article is a folder. That is the entire model.

```
draft/
  my-great-article/
    index.md          <- the article, in Markdown
    a-diagram.png     <- images live next to it
```

The folder name becomes the URL. The images sit beside the text, so they never
get lost or hotlinked. The frontmatter at the top of `index.md` asks for six
things — title, date, description, author, hero image, categories — and derives
everything else. There is no "SEO section" to fill in, no excerpt to write
twice, no tag taxonomy to guess at.

Open a pull request and an automated check reads your frontmatter and builds the
whole site. If you mistyped your author slug or forgot to commit an image, you
find out in about a minute, from a robot, before a human has spent any time on
it.

**Already published the article on your own blog?** Post it here too. Add one
line —

```yaml
canonical: "https://your-blog.example/the-original/"
```

— and search engines keep crediting your site while the article reaches Foojay's
readers. Roughly 800 articles here are cross-posts, and this is how.

## What we deliberately did not build

A few things are absent on purpose, and they are the decisions we would defend
hardest.

**There is no "related articles" algorithm.** When an article links to three
others at the bottom, a human chose those three.

**There is no tag cloud.** Categories only, because a taxonomy nobody curates
becomes 4,000 tags used once each.

## Visitor counting and privacy

Let's be straight about this, because it is the one place where "static site"
does not automatically mean "nothing is watching". There are **two** counters,
they do different jobs, and only one of them is ours.

### 1. Google Analytics — because the marketing team loves it

Foojay reports into the same Google Analytics property it always has. That has
not changed and we are not going to pretend otherwise.

What *has* changed is how it gets there. The old site loaded Google Tag Manager
— 360 KB of JavaScript — to deliver a grand total of eleven lines of tags,
including a Universal Analytics tag from 2023 that only still worked because
Google quietly aliases the retired id to the current property behind the scenes.
Foojay 2.0 loads the GA4 tag directly instead. Same numbers, one fewer legacy
shim that Google can retire without telling anyone, and — this is the part we
care about — **everything your browser is asked to run is now readable in the
repository**, in a file you can review in a pull request, rather than living in
a web console nobody outside the team can see.

[Ketch](https://www.ketch.com/) is still the consent manager, and Google
[Consent Mode](https://support.google.com/analytics/answer/9976101) now defaults
every category to *denied* before it loads. So until you actually agree to
something, GA sets no cookie and sends cookieless pings. If you decline, it
stays that way.

**You use an ad blocker and refuse cookies? No problem — so do I.** Which is
exactly why there is a second counter.

### 2. Our own read counter — because we want to know what is actually read

If Google Analytics were the only source, every number we published would be
wrong, and wrong in a predictable direction: this is an audience of Java
developers, a large share of whom block third-party analytics domains outright.
A page-view count that silently misses a third of its readers is not a
statistic, it is a guess. And the read count on a Foojay article is a *published*
number — it is on the page, next to the byline — so it had better be true.

So the `12,345 views` you see comes from something we run ourselves: a small
[Cloudflare Worker](https://developers.cloudflare.com/workers/) on
`foojay.io/api/views`, in front of a table with exactly two columns — a page key
like `posts/announcing-foojay-2-0`, and an integer.

**Nobody's article went back to zero.** Six years of reading history was sitting
in WordPress, and throwing it away to start from a clean slate would have been
the easy option and a rotten one — a 2021 article that has been read 40,000
times should say so. So we carried the numbers across: **13.8 million reads**
over 2,147 articles, 47 glossary entries and 32 pages. They are loaded as the
starting value of each page's count and live reads are added on top, so what you
see is one number rather than an old total sitting next to a new one.

That transfer moved exactly what the counter itself holds, and nothing else: **a
single number per post or page.** No visitor records, no IP addresses, no
sessions, no personal data of any kind — there was nothing of that sort to carry
over, because a total is all we asked WordPress for and all the new table can
store. (Author pages are the one exception, and they genuinely do start at zero:
WordPress was never counting them.)

That is the whole design, and the privacy properties fall out of it rather than
being promised on top of it:

- **No cookie, no identifier, no IP address, no user agent, no fingerprint.**
  The request carries a page key and nothing else. There is nothing to anonymise
  because nothing about *you* is ever sent.
- **It cannot follow you between pages,** because it stores no notion of a
  visitor. Two reads of two articles are two numbers going up, with nothing
  linking them.
- **An ad blocker does not need to block it,** because there is nothing to
  block: it is first-party, on the same domain as the article you are reading.
  That is not a loophole — it is why the number is accurate.
- **The count is baked into the page at build time**, so displaying it costs no
  request at all. No JavaScript, no dash that turns into a number a second
  later.

The single piece of state on your machine is a `sessionStorage` flag that stops
a page refresh counting twice, and it dies when you close the tab.

And the thing doing the counting is in this repository too: the Worker is
[under two hundred lines, comments included](https://github.com/foojayio/website/tree/main/worker/views),
and you can read every query it makes against that table.

For the formal version, see the [privacy policy](https://foojay.io/privacy-policy/).

## Come and write something

This is the part that has not changed and never will: Foojay is worth reading
because people in this community take the time to write things down.

If you have ever thought *someone should write that up* — a JVM flag that saved
you a week, a migration that went sideways, a library nobody knows about, a
conference report, a thing you finally understood — that someone can be you. You
do not need to be a Java Champion, and it does not need to be 3,000 words.

- **Start here:**
  [How To Submit Your Next Article On Foojay.io](https://foojay.io/today/how-to-submit-your-next-article-on-foojay-io/)
- **Or just read the templates:** everything you need is in the
  [`template/` folder](https://github.com/foojayio/website/tree/main/template) —
  copy a file, fill it in, open a pull request.
- **Questions, or not sure your idea fits?** Ask in the
  [Foojay Slack](https://bit.ly/join-foojay-slack). The answer is usually yes.

And if you spot a typo in this very article: its source is a file in that
repository, and the edit button is right there. That is rather the point.
