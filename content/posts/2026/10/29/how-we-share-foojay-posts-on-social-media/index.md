---
title: "How We Share Foojay Posts on Social Media"
date: "2026-10-29"
description: "Every new Foojay article is announced on Mastodon, Bluesky and LinkedIn by one small JBang script. How it works and which problems we had to solve."
authors:
  - "frankdelporte"
image: "foojay-social-home.png"
categories:
  - "Foojay"
  - "Developer Tools"
  - "AI"
  - "JBang"
related_posts:
  - "announcing-the-new-foojay"
  - "how-to-submit-your-next-article-on-foojay-io"
  - "foojay-mastodon-service-here-it-is"
  - "foojay-on-mastodon-an-update"
  - "an-immutable-mastodon-handle"
---

Foojay publishes a few articles every day. Each one needs a post on Mastodon, Bluesky and LinkedIn, with the right author mentioned, the right link and a picture. Doing that manually for every article costs time I don't always have, so I let a script do it.

## Looking for a free alternative

I first checked the paid services that schedule and automate social media posts. None of the ones I looked at where a perfect match. First problem: the price. Of course, each of these services are based on a monthly subscription, but we want to run Foojay for free where possible. One service was reasonably priced, but can only post to mastodon.social and not to [Foojay.social](https://foojay.social), our own Mastodon instance. Paying a monthly fee for a few posts a day also feels wrong for a community site.

The site is built and deployed by GitHub Actions and it has an RSS feed, so I had everything I needed already. The result is [`scripts/notify/SocialPosts.java`](https://github.com/foojayio/website/blob/main/scripts/notify/SocialPosts.java), one Java file that runs with [JBang](https://www.jbang.dev/). No build setup or cloud server is needed.

## How the script works

The script runs as the last step of the deploy workflow:

1. Before the deploy, the workflow saves the live RSS feed. After the deploy, it has the feed of the new build.
2. The script compares both feeds. Every article that only exists in the new feed is new. The script stores no state, and a scheduled article is announced on the moment it goes live.
3. For each new article, the script loads the published page, reads the authors and the article text, and asks Claude to write one text per platform.
4. Mastodon and Bluesky get their post directly. LinkedIn has no API I can use for a company page, so the script sends the text to a Slack channel, together with a link that opens the LinkedIn composer with the text filled in.

When a platform fails or has no credentials, its text lands in the same Slack message. I can still copy it by hand, so nothing gets lost. A failure in this step never turns a finished deploy red.

## Challenge 1: Mention the authors correctly

A post without the author's name feels wrong. A mention that points to the wrong person is worse.

Every article page already contains JSON-LD with the author names and their social profiles. The script reads that data, picks the Bluesky and Mastodon profiles, and converts them to the format of each network, like `@name.bsky.social` or `@user@instance`. The prompt tells Claude to use these handles exactly as given. When an author has no handle, Claude uses the plain name and invents nothing.

{{< tip >}}
**Note for Foojay authors:** The script can only mention you correctly when your profile contains your links. Open `content/authors/<your-slug>/_index.md` and fill in the `bluesky`, `mastodon`, and `linkedin` fields. Send a pull request with that change, and your next article credits you with a real mention. The same fields appear in the [author template](https://github.com/foojayio/website/blob/main/template/author.md), and the [contribution guide](https://github.com/foojayio/website/blob/main/CONTRIBUTING.md) explains how to open a pull request.
{{< /tip >}}

Mastodon turns a mention into a link on its own. Bluesky doesn't. A mention, link or hashtag is only clickable when the post carries a facet with UTF-8 byte offsets, so the script builds those. It first resolves each handle to its identifier. A handle that doesn't resolve stays plain text, and the post still goes out.

LinkedIn is the exception. A mention only works when you pick the person in the composer, so the text contains the full name and I select the profile myself. This is the only manual step that's still needed. But it's just clicking on the link I get in Slack, and the text is already filled in. When the LinkedIn page has opened, I only need to click the author name and pick the right profile. The script even adds a link to the article, so I can check the text before posting.

## Challenge 2: Fit the character limit

Mastodon allows 500 characters and Bluesky only 300. The prompt contains the limit, but a language model counts badly, and the platforms count differently than you expect:

- Mastodon counts every link as 23 characters, however long it is.
- Bluesky shows a shortened link and counts that one.

The script calculates the length the way each platform does. When a text is too long, it asks Claude for a shorter version and mentions how many characters the previous one had. After three attempts, the script reports a failure instead of posting a text that is cut off.

The prompt also holds our house rules. Claude writes as Foojay, uses only facts from the article, avoids hype words, and never puts a link or mention between parentheses, because some clients read the bracket as part of the link.

## Challenge 3: Include the link and the preview image

Mastodon builds the preview card from the `og:` tags of the page. The post only needs the link. A request key based on the article URL prevents a double post when I re-run a deploy.

Bluesky doesn't build a card. The script reads the title, description and image from the same `og:` tags, uploads the image and attaches the card to the post. Bluesky rejects images of about 1 MB, so the script scales the image down and lowers the JPEG quality step by step until it fits. When that fails, the post goes out without a thumbnail.

That is why the article template asks for a 16:9 JPEG as preview image. It is the card on the site and the picture on every social network.

## Credentials

The script reads its credentials from environment variables, and the workflow fills them from GitHub Actions secrets. Nothing sensitive lives in the repository. Claude needs an API key, Mastodon an access token with `write:statuses`, Bluesky a handle and an app password (not the login password), and Slack a bot token with `chat:write`. When a value is missing, the script prints the text instead of posting it, so you can run it locally without risk.

## Safeguards

The script writes texts for five new articles per run at most. It stops when the previous feed is empty, because otherwise it would announce the whole archive. Without credentials, it prints what it would post. With `--dry-run`, it posts nothing at all, and I use that to test every change.

LinkedIn stays a manual step, and I read every text in Slack. Mastodon and Bluesky get their post within minutes after the deploy, and nobody has to remember it.

If you write for Foojay, three things help your announcement: a clear title and description, a good preview image and an author profile with your Bluesky and Mastodon links. The script handles the rest.

## Conclusion: Java drives the Hugo site

Foojay is a static Hugo site, but Java does the work to make it even better. `SocialPosts.java` needs Java 17 and two libraries: jsoup to read the page and Jackson to talk JSON. The JDK covers the rest. `java.net.http` calls the Mastodon, Bluesky, Slack and Claude APIs, `javax.imageio` shrinks the preview image, and the XML parser reads the RSS feed.

JBang makes it as simple as a shell script. A contributor can read the file, change it and run it locally with `--dry-run`, without a Maven or Gradle project.

The other scripts of this site, the fetchers and the validators, work the same way. A Java developer who wants to improve how Foojay reaches its readers already knows every tool involved. Open `scripts/notify/` and send a pull request.
