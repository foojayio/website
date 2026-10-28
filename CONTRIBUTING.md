# Contributing

Two things arrive here by pull request: **articles** and **calendar events**.

Some lists on the site are **not edited here at all**, because a community
project already maintains them and foojay.io refreshes its copy every day. A
fix made in this repo is overwritten at the next sync, so send it to the
source instead:

| What | Where it is maintained | Shown on foojay.io at |
|------|------------------------|-----------------------|
| Java User Groups (and their meetup calendars) | [World-Wide-JUGs/GlobalWWJugs](https://github.com/World-Wide-JUGs/GlobalWWJugs/tree/master/content/jugs) — one small file per JUG | [/jugs/](https://foojay.io/jugs/), [/calendar/](https://foojay.io/calendar/) |
| Java Champions | [aalmiray/java-champions](https://github.com/aalmiray/java-champions/blob/main/java-champions.yml) — `java-champions.yml` | [/java-champions/](https://foojay.io/java-champions/) |
| Java conferences, dates and CFPs | [javaconferences/javaconferences.github.io](https://github.com/javaconferences/javaconferences.github.io/blob/main/README.md) — `README.md` | [/java-conferences/](https://foojay.io/java-conferences/), [/calendar/](https://foojay.io/calendar/) |

Everything under `data/` that these produce is generated. Never edit it by hand.

## Code of Conduct

Everyone who contributes here agrees to follow our
[Code of Conduct](CODE_OF_CONDUCT.md): communicate in a friendly way, respect
other opinions, and keep feedback constructive.

## Contributing a post

The full, up-to-date guide for writing and submitting an article lives on the
site itself:

**→ [How to submit your next article on foojay.io](https://foojay.io/today/how-to-submit-your-next-article-on-foojay-io/)**

It walks through creating your author profile, preparing your post folder, adding
images, and delivering it (pull request, fork, or zip).

### In short

- Posts are contributed via pull request (fork the repo if you don't have write access).
- Create your post in a folder under **`draft/<your-slug>/`** with the text in
  `index.md` (or `index.adoc`) and its images in the same folder. Copy `template/post.md` (or `template/post.adoc`) as your
  starting point. A maintainer moves the folder into
  `content/posts/<year>/<month>/<day>/<your-slug>/` when it's published — you
  never need to pick that path yourself unless you want the post to be published on a specific date. In that case, clearly specify the preferred date in your pull request.
- **Markdown or AsciiDoc.** Name the file `index.adoc` and write AsciiDoc
  instead, starting from `template/post.adoc`. Everything else is the same —
  same folder shape, same frontmatter, same checks. Markdown is shorter for
  ordinary prose and what most articles use; AsciiDoc is worth it for captions
  on code blocks, numbered callouts inside a snippet, admonitions, and tables
  that span cells.
- **Company-based and commercial articles are welcome if they are Java/OpenJDK/JVM-oriented**
  — they need real, working Java example code or developer tooling, not just a product description.
  Marketing-only submissions are declined.
- New author? Add yourself as `content/authors/<your-slug>/_index.md` in the same
  PR (note the underscore — see `template/author.md`). **An author is a real
  person**: a real photo of yourself as the avatar (no logos) and at least one
  link we can verify you by (GitHub, LinkedIn, Bluesky, Mastodon, website).
  Company or team profiles are not accepted.
- **Leave `date:` as it is in the template.** A maintainer sets it and moves
  your folder to match when the article is published. Only set it yourself if
  you need the article out on a particular day: it's a plain day
  (`date: "2026-01-01"`), with **no time** — the site is rebuilt once a day, at
  07:00 UTC, so that is when articles go out and a time cannot make it earlier,
  only later, by missing that build. A future date schedules the post (it
  stays unbuilt and unlisted until then, and appears in "Coming soon" on the
  home page).
- You don't need to build anything: the PR check validates your frontmatter and
  builds the site, so it catches a bad author slug or a missing image for you.
- If you *do* want to preview it, run `hugo server` and open
  `http://localhost:1313/website/today/<your-slug>/`. `draft/` is deliberately
  not built, so the post has to be somewhere Hugo looks. Either:
  - copy your folder to `content/posts/<year>/<month>/<day>/<your-slug>/`,
    with today's date (or an earlier one) in both the path and `date:` — a
    future date is a scheduled post and is not built; or
  - keep a future `date:` and start the server with `hugo server --buildFuture`.

  Don't commit the copy: a maintainer does the move.
  Previewing an **AsciiDoc** post locally also needs the `asciidoctor` binary
  (`gem install asciidoctor`); without it Hugo stops with
  `failed to execute binary "asciidoctor"`. Nothing extra is needed for
  Markdown, and the PR check installs it either way.

See the guide linked above for the details.

## Adding an event to the calendar

[foojay.io/calendar/](https://foojay.io/calendar/) carries three kinds of event,
and they arrive by different routes. Pick the right one:

### A Java User Group meetup — nothing to do here

JUG meetups are pulled automatically, once a day, from the calendar each group
already publishes (its own `.ics`, a Google Calendar, or Meetup's export).
Nothing in this repo is hand-edited for them.

If your JUG's events are missing, the fix belongs **upstream**, in the
community-run [World Wide JUGs directory](https://github.com/World-Wide-JUGs/GlobalWWJugs/tree/master/content/jugs):
add or correct your group's file there, giving it a `calendar:` (any iCal URL)
or a `meetup:` entry. It shows up on foojay's calendar at the next daily sync,
and on every other site that reads the directory too.

### A Java conference — add it to javaconferences.org

Conferences are pulled in daily from [javaconferences.org](https://javaconferences.org/),
the community list of upcoming Java conferences. If yours is missing, or a date,
location or call for papers is wrong, send a pull request to the
[`README.md` of that repository](https://github.com/javaconferences/javaconferences.github.io/blob/main/README.md)
(one table row per conference). It appears on foojay.io's
[/java-conferences/](https://foojay.io/java-conferences/) map and on the calendar
at the next daily sync, and on javaconferences.org itself. **Don't add a file to
`data/events/` for a conference that belongs on that list.**

### A workshop, webinar or other one-off — one small file

These have no list or feed to join, so they are added by hand:

1. Copy [`template/event.yaml`](template/event.yaml) to
   `data/events/<event-slug>.yaml`.
2. Name the file after the event **including its year** —
   `ai-coding-workshop-berlin-2026.yaml`, `ocx-webinar-week-2026.yaml`. Lowercase letters, digits
   and dashes only. Next year's edition is a new file, not an edit of this one.
3. Fill it in and open a pull request. Nothing else needs touching.

```yaml
name: "AI Coding Workshop Berlin 2026"
type: "Workshop"
url: "https://example.org/ai-coding-workshop/"
start: "2026-09-15"
venue: "Example Venue"
city: "Berlin"
country: "Germany"
```

| Field | Required | Notes |
|-------|----------|-------|
| `name` | **yes** | Shown on the calendar. Include the year, like the filename. |
| `url` | **yes** | The event's own site — where the calendar sends a reader. |
| `start` | **yes** | `YYYY-MM-DD`. Add a time only when it matters: `"2026-09-15T19:00:00+02:00"`, in the event's own UTC offset. Without one the event is shown as running all day. |
| `city` | **yes** unless online | |
| `country` | **yes** unless online | Feeds the "N countries" count. |
| `type` | no | `"Conference"` (the default), `"Workshop"`, `"Community Day"`, `"Hackathon"`, `"Webinar"`… Shown next to the event. |
| `end` | no | `YYYY-MM-DD`, the **last day**, inclusive. Leave it out for a one-day event; a multi-day one is drawn as a band across those days. |
| `venue` | no | The building, if it's worth naming. |
| `online` | no | `true` for an online-only event; then `city`/`country` can be left out. |

Everything else is derived, so there is nothing to keep in step: the colour on
the calendar comes from the filename, the month band from the dates, the counts
in the page header from the entries themselves.

**Two things worth knowing:**

- **Never add an event to `data/jug-events.json`.** That file is regenerated
  from the JUGs' calendar feeds and committed every day — a hand-written entry
  in it is gone within 24 hours. That is exactly why `data/events/` exists.
- **You never have to come back and delete the file.** An event drops off the
  calendar on its own the day after it ends. Removing it afterwards is welcome
  tidying, never urgent.

One file per event, so two people adding two conferences in the same week never
touch the same file and never get a merge conflict. The PR check
(`jbang scripts/validate/Frontmatter.java`) validates your entry — it will tell
you about a misspelled field, a date it can't read, or an end before the start.
