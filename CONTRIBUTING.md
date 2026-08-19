# Contributing

Two things arrive here by pull request: **articles** and **calendar events**.

## Contributing a post

The full, up-to-date guide for writing and submitting an article lives on the
site itself:

**→ [How to submit your next article on foojay.io](https://foojay.io/today/how-to-submit-your-next-article-on-foojay-io/)**

It walks through creating your author profile, preparing your post folder, adding
images, and delivering it (pull request, fork, or zip).

### In short

- Posts are contributed via pull request (fork the repo if you don't have write access).
- Each post is a folder under `content/posts/<year>/<month>/<day>/<your-slug>/`
  with the text in `index.md` and its images in the same folder. Copy
  `template/post.md` as your starting point.
- New author? Add yourself as `content/authors/<your-slug>/_index.md` in the same
  PR (note the underscore — see `template/author.md`).
- You don't need to build anything: the PR check validates your frontmatter and
  builds the site, so it catches a bad author slug or a missing image for you.
- If you *do* want to preview it, note that `draft/` is deliberately not built —
  `hugo server -D` will not show it. Copy your folder to
  `content/posts/<year>/<month>/<day>/<your-slug>/`, run `hugo server`, and open
  `http://localhost:1313/website/today/<your-slug>/`.

See the guide linked above for the details.

## Adding an event to the calendar

[foojay.io/calendar/](https://foojay.io/calendar/) carries two kinds of event,
and they arrive by two different routes. Pick the right one:

### A Java User Group meetup — nothing to do here

JUG meetups are pulled automatically, once a day, from the calendar each group
already publishes (its own `.ics`, a Google Calendar, or Meetup's export).
Nothing in this repo is hand-edited for them.

If your JUG's events are missing, the fix belongs **upstream**, in the
community-run [World Wide JUGs directory](https://github.com/World-Wide-JUGs/GlobalWWJugs/tree/master/_jugs):
add or correct your group's file there, giving it a `calendar:` (any iCal URL)
or a `meetup:` entry. It shows up on foojay's calendar at the next daily sync,
and on every other site that reads the directory too.

### A conference, workshop or other one-off — one small file

Conferences publish no feed anyone can subscribe to, so they are added by hand:

1. Copy [`template/event.yaml`](template/event.yaml) to
   `data/events/<event-slug>.yaml`.
2. Name the file after the event **including its year** —
   `devoxx-belgium-2026.yaml`, `jfokus-2027.yaml`. Lowercase letters, digits
   and dashes only. Next year's edition is a new file, not an edit of this one.
3. Fill it in and open a pull request. Nothing else needs touching.

```yaml
name: "Devoxx Belgium 2026"
type: "Conference"
url: "https://devoxx.be/"
start: "2026-10-05"
end: "2026-10-09"
venue: "Kinepolis Antwerpen"
city: "Antwerp"
country: "Belgium"
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
