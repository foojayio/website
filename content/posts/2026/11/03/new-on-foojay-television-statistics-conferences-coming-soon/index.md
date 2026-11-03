---
title: "New on Foojay: Television, Statistics, Java Conferences and Coming Soon"
date: "2026-11-03"
description: "Four new pages on foojay.io: Java videos from YouTube, the numbers behind Foojay, a map of Java conferences and a peek at the articles that are coming."
authors:
  - "frankdelporte"
image: "foojay-new-pages.jpg"
categories:
  - "Foojay"
related_posts:
  - "announcing-the-new-foojay"
  - "how-we-share-foojay-posts-on-social-media"
  - "how-to-submit-your-next-article-on-foojay-io"
  - "featured-foojay-authors-for-october-2026"
---

Foojay is more than a list of articles, and the site keeps growing. Four new pages went live in the past week: a page with Java videos, a page with the numbers behind Foojay, a map of Java conferences and a page showing which articles are coming next. None of them needs any manual work to stay up to date. Each one is built from data that is fetched or counted automatically, and I'll show where that data comes from.

## Foojay Television

**Page:** [foojay.io/tv](/tv/)

There is a lot of good Java content on YouTube, spread over many channels. [Foojay Television](/tv/) puts the newest videos of those channels in one place.

The channels are listed on the left, with a search box to filter them and the number of videos for each one. The right side shows a player with the latest video and a grid of the other videos. Pick a channel to see only its videos, or click any video to play it on the page.

The page is built to be light on tracking:

- Nothing from YouTube is loaded until you click a video. Until then, you see a thumbnail and a play button.
- After a click, the video is played through `youtube-nocookie.com`.
- Without JavaScript, every video is a normal link to YouTube.

YouTube Shorts are filtered out. The feed of a channel doesn't say which videos are Shorts, so the fetch script checks every new video and skips the Shorts.

{{< gallery cols="2" >}}
tv-overview.jpg | Foojay Television with the channel list on the left and the player on the right
tv-videos.jpg | The grid with the latest videos of all channels
{{< /gallery >}}

**How it works:**

- The channels are listed in [`data/tv-channels.yaml`](https://github.com/foojayio/website/blob/main/data/tv-channels.yaml). A YouTube handle is all you need to add one.
- The script [`TvVideos.java`](https://github.com/foojayio/website/blob/main/scripts/fetch/TvVideos.java) reads the public RSS feed of every YouTube channel and writes the result to `data/tv-videos.json`. It runs every day, and no API key is needed.

{{< tip >}}
Missing an OpenJDK or Java-related YouTube channel? Add its handle to [`tv-channels.yaml`](https://github.com/foojayio/website/blob/main/data/tv-channels.yaml) with a pull request. The channel name, avatar and videos are fetched automatically.
{{< /tip >}}

Thanks to [Marit van Dijk](/today/author/marit-van-dijk/) for the idea of this page.

## Foojay Statistics

**Page:** [foojay.io/statistics](/statistics/)

How many articles did Foojay publish, how many authors write for us, and how often are the articles read? The [statistics page](/statistics/) answers these questions, and every number is counted from the site as it was built. When Foojay grows, the page grows with it.

The page has five sections:

- **Content**: articles, authors, categories, pedia entries, pages, sponsors, published words, cross-posted articles and articles with a transcript.
- **Published Articles**: a chart with the articles per year, which you can narrow down by year and month.
- **New Authors**: a chart with how many authors published their first article on Foojay.
- **Reads**: how often pages have been read, including the counts carried over from the previous Foojay site.
- **Community**: the Java user groups, Java Champions, conferences and events listed on Foojay.

The read counts come from our own counter and not from a third-party analytics service. That way, blocked trackers don't make the numbers too low.

{{< gallery cols="1" >}}
statistics-overview.jpg | The content numbers and the chart with the published articles per year
{{< /gallery >}}

**How it works:**

- [`site-stats.html`](https://github.com/foojayio/website/blob/main/themes/foojay/layouts/partials/site-stats.html) counts everything while the site is built.
- [`ViewCounts.java`](https://github.com/foojayio/website/blob/main/scripts/fetch/ViewCounts.java) refreshes `data/views.json` from the [read counter](https://github.com/foojayio/website/tree/main/worker/views), a small Cloudflare Worker.
- The same numbers are available as JSON on [foojay.io/stats.json](/stats.json), if you want to use them in your own project.

## Java Conferences

**Page:** [foojay.io/java-conferences](/java-conferences/)

Where can you meet the Java community, and where can you speak? The [Java Conferences page](/java-conferences/) shows every upcoming conference on a world map, with a list below it.

Each row has the dates, the location and the status of the call for papers. Type a name, city or country in the filter box to find a conference. Online conferences have no place on the map, but they are in the list. Every upcoming conference is also added to the [Foojay events calendar](/calendar/).

{{< gallery cols="1" >}}
conferences-map-and-list.jpg | The map with the upcoming conferences and the list with dates, locations and calls for papers
{{< /gallery >}}

**How it works:**

- The list is maintained by the community on [javaconferences.org](https://javaconferences.org/), which is built from the [javaconferences repository on GitHub](https://github.com/javaconferences/javaconferences.github.io).
- [`JavaConferences.java`](https://github.com/foojayio/website/blob/main/scripts/fetch/JavaConferences.java) reads the published `conferences.json` of that site every day and writes `data/java-conferences.yaml`.
- The dates on that site are written as free text, so the script converts them. A date it doesn't understand is reported and skipped, never guessed.

{{< tip >}}
A conference is missing, or a date or call for papers is wrong? Fix it in the [`README.md` of the javaconferences repository](https://github.com/javaconferences/javaconferences.github.io/blob/main/README.md) with a pull request. It will show up on Foojay automatically.
{{< /tip >}}

## Coming Soon

**Page:** [foojay.io/today/coming-soon](/today/coming-soon/)

Foojay articles are scheduled in advance. A post with a date in the future doesn't exist on the site until that day, so you couldn't see what was coming. The [Coming soon page](/today/coming-soon/) fixes that. It lists the scheduled articles per day, with the title, the author, a short description and the categories.

The titles are not links, because the pages don't exist yet. They go live on the date shown. A short version of this list is also on the [Foojay home page](/).

{{< gallery cols="2" >}}
coming-soon.jpg | The scheduled articles, grouped by publication day
coming-soon-homepage.jpg | The scheduled articles in the sidebar on the homepage
{{< /gallery >}}

**How it works:**

- Scheduled posts are not built, so no page can list them. [`scheduled-posts.html`](https://github.com/foojayio/website/blob/main/themes/foojay/layouts/partials/scheduled-posts.html) reads them directly from the folders of the repository.
- A static site only changes when it is built. The workflow [`publish-scheduled.yml`](https://github.com/foojayio/website/blob/main/.github/workflows/publish-scheduled.yml) checks if an article has come due and, if so, starts the build that publishes it.

{{< note >}}
Want your article on this page? Send it in a pull request, as described in [How To Submit Your Next Article On Foojay.io](/today/how-to-submit-your-next-article-on-foojay-io/), and a maintainer will schedule it.
{{< /note >}}

## Feedback

All of these pages are open source, like the rest of the [Foojay website](https://github.com/foojayio/website). Do you have an idea to improve one of them, or a page you would like to see? Open an issue or a pull request on GitHub, or let us know on [Slack](/today/join-slack-com-t-foojay-signup/).
