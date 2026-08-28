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

Foojay has moved house. The address stays exactly where it was, so every URL you
have bookmarked, linked or cited still works. Everything behind that address is
new. Foojay is now a static site, its entire content lives in a public Git
repository, and publishing an article is a pull request.

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

To put real numbers on "a matter of days with a very small budget" for the version of Foojay you are now looking at: 
* **About 70 hours of work across 17 days** of which a lot outside official working hours as I got dragged into a mission ;-)
* **Roughly $1000 of AI model usage** at published token prices. 
We measured both rather than remembering them. The hours come from clustering
four weeks of commit history into working sessions, the cost from adding up the
token counts in the assistant's own logs.

## What Foojay 2.0 actually is

A [Hugo](https://gohugo.io/) site built from
[a public repository](https://github.com/foojayio/website), deployed
automatically whenever something lands on the main branch. Concretely:

| | Before | Now |
|---|---|---|
| Publishing | CMS account + web editor | a pull request |
| Search | server-side | a static index, in your browser |
| Comments | a plugin | GitHub Discussions, with the old ones kept as an archive |
| Read counts | a plugin | a counter we run, that stores a slug and a number |
| Analytics | Google Analytics via 360 KB of Tag Manager | the same Google Analytics, loaded directly |
| Podcast episodes | press play and listen | a transcript on the page as well |
| The archive | in a database | 2,147 Markdown files you can read on GitHub |

Everything came across: 2,147 articles, 344 author profiles, 47 glossary
entries, the JUG directory, the calendar, the sponsors,... Every legacy path
still resolves, including the ones nobody would think to check: the old `/blog/`
scheme, the retired slugs, and the URLs that changed when an article got a new
name years ago.

### Hugo? No Java?

Yes, Java! Just not for the part you would expect.

I did look at the Java generators. I had tried [JBake](https://jbake.org/)
before, and at the time it looked like a project that had gone quiet. No release
between early 2023 and late 2025 is a long gap to stake a site on.
(Credit where it is due: it has since shipped 2.7.0.) And when I mentioned this
project on the Foojay Slack, someone pointed me at
[Roq](https://github.com/quarkiverse/quarkus-roq), the Quarkus-based static site
generator. It is a thin layer over Quarkus that renders Markdown through Qute
templates, with type-safe templating and code completion. It looks genuinely
good, and if you want your generator in Java too, start there.

I went with what I already know. I have built a lot of very different sites on
Hugo: [webtechie.be](https://webtechie.be), [codewriter.be](https://codewriter.be),
[pi4j.com](https://pi4j.com), [lottie4j.com](https://lottie4j.com) and
[melodymatrix.rocks](https://melodymatrix.rocks). A personal blog, an
open-source project's documentation, a product site. All of them work well, and
Hugo has been stable, long-lived and very actively maintained the whole time.
For a community project that has to still be standing in ten years, "I know this
tool and it is not going anywhere" beat "this is the most interesting choice".
It also builds all 2,147 articles in about eight seconds, which stops mattering
right up until the moment it matters a lot.

And the generator is the *smallest* part of this anyway. Everything around it
comes to roughly **8,500 lines of Java**: the migration, the daily data syncs,
the checks on your pull request. That is the part that actually made this
possible.

All of it runs on [JBang](https://www.jbang.dev/), so there is no `pom.xml`, no
Gradle, no build step. Each script is a single file that declares its own
dependencies at the top and runs directly:

```java
///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.jsoup:jsoup:1.17.2
//DEPS com.vladsch.flexmark:flexmark-html2md-converter:0.64.8
//JAVA 21+
```

`jbang scripts/transfer/Posts.java` and it goes. jsoup parses the HTML, flexmark
turns it into Markdown, Jackson and SnakeYAML handle the JSON and YAML.

**Getting the content out.** Nobody used a WordPress admin login or a database
dump. Every scraper read the public site instead, its HTML plus the REST routes
WordPress already exposes to anyone. The scrapers are idempotent and skip any
file marked `frozen: true`, which is what made the whole thing survivable: they
ran over and over for weeks while the old site stayed live and kept publishing,
instead of being one big risky switch-flip.

**And then the unglamorous half.** A faithful scrape gives you WordPress's
habits faithfully, so most of the work was repairing things nobody would think
to look for:

- **10,270 non-breaking spaces** used to indent code samples. They *look* like
  indentation and break the moment you paste the snippet into an editor.
- **14,344 heading anchors** of the form `#h2-3-some-title`, numbered by
  position, so inserting one heading silently renumbered the rest. Another
  1,268 of the same ids, stamped on captions and paragraphs instead of
  headings, were being *printed to the reader* mid-article on 91 posts.
- **7 posts whose entire tail rendered as a block of source code**, because one
  code sample had been wrapped in an extra, empty code fence and the last one
  on the page never closed.
- **279 email addresses** that no script could see at all: Cloudflare replaces
  them with a placeholder plus an encoded copy that only a browser puts back.
- **259 gallery images** across 94 posts, stored as thirty lines of block markup
  each, now one small shortcode.
- **8,053 headings** restyled, and every code block turned from eight
  attributes of WordPress plumbing into a plain Markdown fence.

**Links were the other half of the job, and the broken ones do not announce
themselves.** A dead link inside an article renders perfectly.

- **596 redirect pages**, so every URL the old site ever answered still lands
  somewhere real. 89 of those come from WordPress's own redirect table, and the
  entries in it point at other entries, so we followed every chain to its final
  destination. A redirect aimed at another redirect is one search engines throw
  away. Another 17 rules went in the bin, because their targets 404 on the
  live WordPress site too, and recreating a redirect to a missing page helps
  nobody.
- **143 internal links that were already dead**, and had been for years. A link
  to a file sitting in the article's own folder resolved against the site root
  instead, so `![](shot.png)` worked and `[handout](handout.pdf)` quietly did
  not. Nothing in the Markdown shows that asymmetry, which is why it survived so
  long.
- **We checked all 837 cross-post canonicals against the live web**, one request
  each. 48 pointed at an article that no longer exists, which is worse than
  having no canonical at all: it tells Google the real version of the text sits
  at a URL that 404s, so Google suppresses Foojay's copy in favour of nothing.
  Those 48 now point at Foojay. That check needed two different HTTP clients,
  because Medium answers a script with 403 and sometimes 410, and a bot wall
  looks exactly like a deleted page.
- **1.26 GB of images, down to 0.69 GB.** GitHub Pages refuses to deploy a site
  over 1 GB, and the warning for crossing that line lands on a build that
  otherwise goes green. The same 52 MB animated GIF was the header image of
  three different articles.

**The part that keeps running.** The `scripts/` folders are grouped by one
question, *does this still exist after cutover?*, because two of them are meant
to be deleted whole. The scrapers and the one-off repairs go in the bin the day
WordPress is switched off. What stays is the Java that does the ongoing
work:

| Runs | What it does |
|---|---|
| every deploy + daily | pulls the JUG directory and the Java Champions list from the community-run repositories that own that data |
| daily | reads the iCal feed each JUG publishes, so meetups appear on the calendar with nobody typing them in |
| 4× a day | refreshes the read counts |
| **every pull request** | validates frontmatter, the check that tells you about a mistyped author slug before a human looks at your article |

So: a Go static site generator, and a pile of Java doing everything that
actually needed writing.

I worked on this on two different computers, and Claude gave me a nice insight
about the "real" cost of using it. **The logs sit right there, so here is the AI
half.** 2,244 requests, 713 million tokens, about $500 at list prices. The shape
of that number is the interesting part. **98% of those tokens are cached context
that the model re-reads**, meaning the conversation so far, handed back on every
turn at a tenth of the normal input price. The tokens it actually *wrote*, the
code and the prose, come to 2 million of the 713 and only 10% of the bill.
Thinking about a large codebase costs more than writing to it. (One honest
caveat: those logs cover 9 of the 17 working days, because I built some of this
on a second machine.)

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
things, title, date, description, author, hero image and categories, then
derives everything else. There is no "SEO section" to fill in, no excerpt to write
twice, no tag taxonomy to guess at.

Open a pull request and an automated check reads your frontmatter and builds the
whole site. If you mistyped your author slug or forgot to commit an image, you
find out in about a minute, from a robot, before a human has spent any time on
it.

**Already published the article on your own blog?** Post it here too. Add one
line:

```yaml
canonical: "https://your-blog.example/the-original/"
```

Search engines keep crediting your site while the article reaches Foojay's
readers. Roughly 800 articles here are cross-posts, and that line is how.

## Nothing goes live unchecked

The flip side of "publishing is a pull request" is that a robot can read every
pull request, and every deploy, before a reader does. Three things run
automatically, and they are worth describing because they catch different
classes of mistake.

**On a pull request: your frontmatter and a full site build.** The check reads
the article you added, including one still sitting in `draft/`, which nothing
else would look at. It fails on the things that otherwise fail *silently*: an
author slug with no matching profile (the article renders, but never appears on
your author page), a hero image naming a file you forgot to commit, two articles
claiming the same URL, leftover text from the template. Then it builds all 4,200
pages, so a broken shortcode is a red cross on your PR rather than a broken page
on the site.

**Before every deploy: the site Hugo just built.** Two questions, and the content
answers both rather than a list somebody has to maintain. Did every source file
produce a page? That one matters because a section never breaks with an error.
It breaks as a template that runs fine and quietly matches nothing. And does
every internal link resolve? Half a million of them across those 4,200 pages,
checked against the files actually on disk, in about five seconds. That one earns
its keep. It caught a broken "back to the homepage" link on the 404 page itself,
one wrong character in a template, on the one page a lost reader ever sees.

**Then a real browser, on the built site.** Some of Foojay only exists once
JavaScript has run: the search index, the two world maps, the image lightbox,
the sortable tables in the [sitemap](/sitemap/), syntax highlighting. Every one
of those fails the same way. The page still returns 200, still looks full, and
simply stops doing the thing. So a throwaway local server hosts the built site
and about forty checks click through it: search for a word and get grouped
results, open a gallery, page a table, flip to dark mode.

**One of those checks is about security rather than mistakes.** Foojay's
markdown allows raw HTML, and it has to, because two thousand imported WordPress
articles carry tables, collapsible blocks and embeds with no Markdown
equivalent. A merged pull request could therefore carry a `<script>`, and a
static site keeps no server-side layer to catch it. So the check refuses seven
kinds of executable markup in article text:
`<script>`, inline event handlers like `onerror=`, `javascript:` links,
`<form>`, `<base>`, meta-refresh redirects, and `<object>`/`<embed>`. Before
turning it on I counted every one of them across all 2,153 published articles.
Every count came back zero, so nothing in the archive needed fixing first, and
anything new is by construction something nobody has written here in five years
of publishing.

The check skips code samples, and that is not a loophole. Hugo escapes a fenced
block before it reaches the page, so a `<script>` inside one renders as visible
text and cannot run. You can still publish an article *about* XSS. It just has to
fence its examples, which is what we ask for anyway. `<iframe>` is the one shape
that warns instead of failing, because 33 articles legitimately use one for
Vimeo, Speaker Deck or Apple Podcasts. The warning names the host so a reviewer
can glance at it. Findings land as annotations on the diff itself, on the file
and the line, rather than in a log four clicks away. They come from the log
stream rather than the API, so they work identically on a pull request from a
fork, which is how most first-time authors arrive.

This is the honest shape of security after leaving WordPress. There is no CMS to
log into, no database, and no PHP running on a server, so there is nothing to
exploit remotely. What is left is that the site changes when a maintainer merges
something. Review is therefore the security control, and these checks are what
give it teeth.

Two decisions in there were deliberate. **Only breakage we caused blocks a
deploy.** The check reports and counts a dead link an author typed in 2021
rather than treating it as an emergency. The archive held 53 of those when the
check first ran, and a gate that blocks every future deploy on a five-year-old
typo is a gate somebody switches off within the week. (They are down to one, for
the record, and it needs a URL only its author knows.) And **the checks never
call anyone else's server.** Roughly 440 articles embed a YouTube player.
Asserting that one reaches "playing" would assert that YouTube is up, on a check
that can stop our own deploy. The checks answer third-party requests locally
instead, so nothing here goes red because someone else's CDN is having an
afternoon.

None of this is unusual for a software project. It is unusual for a website, and
it is the part I would most want back if we ever moved again.

## Every podcast episode is now readable

The Foojay Podcast had exactly one way in: press play and listen for the next
forty-five minutes. **99 episodes now carry a transcript on the page** — roughly
800,000 words of conversation you can read, skim, `Ctrl-F` through, or quote
from.

Nothing was transcribed to make that happen. Every episode is on Foojay's own
YouTube channel and YouTube has already run speech recognition over all of them,
so the text was ours to fetch — seconds per episode, against hours of local
compute for a result of the same quality. A JBang script pulls the captions,
collapses YouTube's rolling repeats (its captions re-send each settled line in
the following cue, so a 45-minute episode arrives as 2,643 cues holding about
1,300 distinct lines), drops the jingle and the "uh"s, and writes a
`transcript.md` next to the episode's `index.md`. The page renders it *because
the file is there* — there is no flag on the episode to set, and none to
remember to unset. It is a plain section of the page with its own entry in "On
this page", not a collapsed box you have to know to open: your browser's
find-in-page does not look inside a closed one, so `Ctrl-F` for a guest's name
would have found nothing on the very page that says it.

**And it is a machine transcript, which every episode says out loud above the
text.** Automatic captions get names and Java vocabulary wrong, and an
uncorrected machine transcript presented as a faithful record is worse than one
that admits what it is. The corrections that *are* applied were derived from
what recognition actually produced rather than guessed at, and the best argument
for working that way is `forj`. It looks exactly like a mangled "Foojay". It is
in fact **`4j`** — it only ever appears next to Neo, Log, SLF and LangChain. The
spelling-based guess would have quietly rewritten every mention of Log4j in six
years of archive into a mention of this website.

Guests' names are deliberately left uncorrected. Recognition mangles them worse
than anything else, but there is no spelling a script can *know* is the intended
one, and inventing one puts words in someone's mouth. So if you were on an
episode and your name comes out wrong, there is a **Suggest a correction** link
next to every transcript that opens that episode's transcript file in an editor
— and a corrected transcript is never overwritten by the script again.

One deliberate omission — **transcripts are not in the search index.** 800,000
words against the article archive's 115,000 would make every episode a hit for
any word anyone happened to say out loud, and bury the thing you were actually
searching for.

## Your old comments are still there

Foojay collected 580 comments across 270 articles over six years, and a lot of
them earn their place. Someone corrected the author, someone posted the command
that actually worked, someone asked the question everybody else also had.

New comments go to [giscus](https://giscus.app/), which keeps each thread as a
GitHub Discussion on the same public repository as the articles. You sign in with
GitHub, the thread has a URL you can link to, and the team moderates it with the
tools it already uses every day. A thread keys on the article slug and not on its
path, so moving the site to its new home orphaned none of them.

The 580 old comments needed a different answer, and the first attempt failed in a
way worth writing down. The plan was to post them into those same Discussions
from the Foojay account, so that an old conversation and a new one looked
identical. GitHub blocked the account a few articles in. Several hundred comments
created through an API by a brand new account looks exactly like spam from their
side, and it is hard to argue they read it wrong.

So the old comments live in the repository instead. Each article carries a small
JSON file next to its text, and the page renders it under the live discussion as
**Discussions on the previous Foojay site**. Two of the 580 did not survive,
because they belong to an article WordPress itself has deleted, which leaves 578
across 269 articles.

Losing that import turned out to improve the result. Nobody knows the GitHub
identity of a reader who commented in 2021, so not one of those people could ever
have edited or replied to their own comment inside a Discussion. An archive says
what it honestly is. It also forgives a mistake: a bad conversion costs a re-run,
where a bad import cost an apology to 580 people. And it is now the only copy of
those 580 comments, which matters more every day, because they disappear with the
WordPress site.

One detail here is not cosmetic. Foojay's Markdown allows raw HTML, for the same
reason the security check above has to allow it. Printing 578 bodies written by
strangers straight onto the page would therefore hand any one of them a
`<script>` tag, on 269 article pages, with no server left to catch it. So the
conversion runs every comment through jsoup's own sanitizer and stores the
result, and the page prints only what the sanitizer approved. The tags those
comments actually use come to eight: paragraphs, line breaks, links, `code`,
`pre`, `strong`, `em` and one blockquote. Not a single image, iframe or script
among them.

## Accessibility

Foojay is very likely not *legally* required to be accessible — the European
Accessibility Act covers consumer services in listed sectors, not community
blogs. That is a poor reason to skip it. This is an audience of Java developers,
which is precisely the audience that browses with a keyboard, at 200% zoom, in a
dark colour scheme, or with a screen reader.

So the site targets **[WCAG 2.2 AA](https://www.w3.org/TR/WCAG22/)**, and there
is now an [accessibility statement](https://foojay.io/accessibility/) that says
where it actually stands. What went in:

- A skip link, one `<h1>` and real landmarks on every page.
- **Everything works without a mouse**: the menu, the search field, the image
  viewer, the event calendar, the sortable tables, the sponsor banners. Read [the accessibility page](https://foojay.io/accessibility/) for more information about navigating with the keyboard through the site.
- **Contrast is measured, and the numbers are recorded next to each colour in
  the stylesheet.** That is how we found that Foojay's own logo blue is 2.02:1
  on white — fine as a fill, unusable as text — so it is never text here, and
  the focus indicator got a colour of its own. Links in running text are
  underlined for the same reason: at 1.2:1 against the body text, colour alone
  was telling you nothing.
- **Nothing moves that you cannot stop**, and nothing moves at all if your
  system asks for reduced motion.

The failures that got fixed are more instructive than the list of things that
pass. The mobile menu was moved off-screen but left in the tab order, so a
keyboard walked you through a menu you could not see. The image viewer's click
handler was on the `<img>`, so click-to-enlarge did not exist for a keyboard at
all. Two pages overflowed a 390px phone screen by 243 and 151 pixels.

**And the biggest gap is on the statement rather than glossed over: roughly
3,000 images in the archive have no alt text.** They were imported that way, and
no script can invent a description of a screenshot it cannot see. New articles
are checked when you open the pull request — as a *warning*, not a failure,
because whether an image carries meaning is a judgement call, and the
predictable response to a hard failure is `alt="image"`, which is worse for a
screen reader than nothing at all.

Which makes it 3,000 tiny independent jobs in a public repository, and if you
wrote one of those articles you are the best person alive to describe its
screenshots.

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

And if you spot a typo in this very article: scroll to the foot of it and there
is an **Edit this page on GitHub** link. It opens the file this page was built
from, in an editor, and turns your fix into a pull request — GitHub forks the
repository for you, so it takes three clicks and no local setup. Every article,
page and glossary entry on the site has one. That is rather the point.
