---
title: "AI-assisted genealogy"
date: "2026-08-31T05:51:48+00:00"
lastmod: "2026-09-21T05:53:22+00:00"
description: "My son recently came to me to brag about using AI to find our ancestors. While the results were correct, I didn't learn anything new, as it stopped at my…"
canonical: "https://blog.frankel.ch/ai-assisted-genealogy/"
authors:
  - "nicolas-frankel"
image: "cover_large-2.jpg"
categories:
  - "AI"
related_posts:
  - "ai-gateways-why-and-how"
  - "introducing-skills-boxlang-io-the-open-agent-skills-ecosystem-for-boxlang-the-ortus-world"
  - "tokensparsamkeit-for-coding-assistants"
  - "experimenting-with-ai-subagents"
frozen: false
---

My son recently came to me to brag about using AI to find our ancestors. While the results were correct, I didn't learn anything new, as it stopped at my grandparents. I was never very interested in my genealogy, but I decided to see if AI would be a good tool for this. TL;DR: Yes, it is, and even more than that. In a little less than one month, I managed to gather more than 600 individuals and get back 12 generations in some branches.

In this post, I want to explain how I did it so that you can do the same.

## Setup

I decided to handle the project like any other software project:

* `git`.
* A `git` provider. I'm using both Codeberg and GitHub. See below for the rationale.
* Cloudflare Pages to visualize the tree.

I searched a bit about how to store data and found the `GEDCOM` format specification:
> The word GEDCOM is an acronym for GEnealogical Data Communication. GEDCOM was created by The Church of Jesus Christ of Latter-day Saints as the specification for exchanging genealogical data between different family tree software products. The original GEDCOM specification was released in 1984, and the last update to it was version 5.5 in 1996 with an incremental upgrade to version 5.5.1 released in 2019.
>
> —- [About GEDCOM](https://gedcom.io/about/)

The latest 5.x specification version is 5.5.1, re-released as a standard in 2019. FamilySearch rebooted the specification in 2021 to create v7.0, which is still maintained today. Few tools are actually compatible with it. For this reason, I limited myself to using 5.5.1.

## GEDCOM 101

GEDCOM is a structured format, based on indentation level. The indentation is not space-based, but relies on a leading number. It looks something like this (spaces added for better readability):

    0 @I1@ INDI                                   # 1
      1 NAME John Martin /Doe/                    # 2
      1 SEX M
      1 BIRT
        2 DATE 1 JAN 1970
        2 PLAC Paris, 8e, Paris, France
        2 SOUR @S1@                               # 3
          3 PAGE Birth certificate EA68410

1. The base entity is the `INDI`, an individual
2. Last name is defined between slashes
3. Source with ID

As I mentioned, I added extra spaces. The following snippet is the correct format:

    0 @I1@ INDI
    1 NAME John Martin /Doe/
    1 SEX M
    1 BIRT
    2 DATE 1 JAN 1970
    2 PLAC Paris, 8e, Paris, France
    2 SOUR @S1@
    3 PAGE Birth certificate EA68410

## The loop

The process is always the same.

Pick someone in the tree with unknown parents, and let the assistant do the search. It knows which genealogy sites to browse, and which archives hold the civil records for the place. When it finds the act, it transcribes it, adds the new individuals to the GEDCOM file, links them to their child, and records the source. Then it commits.

Rinse and repeat. Each loop adds one generation on one branch, until no records are found.

## What worked with the assistant

Most AI-related advice also applies to professional projects.

* **Switch model according to the task**: Your token quota is limited by nature, unless you pay per token (but in this case, you're rich and tokens aren't an issue). It's not efficient to use super powerful models such as Fable 5 to send emails. However, it's equally inefficient to use a weak model for complex multi-step tasks: you will probably get wrong results.
* **Create skills** : When you start either repeating the same instructions or notice that the assistant does come to the same conclusion over and over, it's time to create a [skill](https://agentskills.io/home). Skills are reusable sets of instructions, scripts, assets, and references. However, the biggest benefit of skills is that they don't bloat the context by default: they are triggered if their description matches the prompt. Skills can be orthogonal.

  I have created several skills:
  * more than a couple to know how to navigate a site
  * one to transcribe record images into plain text (see below)
  * two for autonomous sessions (see below)
* **Transcribe records into plain text**: All records you will get will either be PDFs or images. PDFs are hard to parse, and images even more so. In the long run, it pays to parse and transcribe them once, so they can be reused cheaply afterwards.
* **The assistant can make mistakes**: It does the research, so its mistakes are research mistakes. It may misread a name on a scan, or pick the wrong person when two of them share a first name, a surname, and a village. However, my experience is that Claude Code is pretty good on transcriptions, and it's cautious by default.
* **Set up grammar checks**: The bigger the GEDCOM file, the more chances the assistant will break it at some point. If you notice it after many commits, it will be a mess to straighten it back. Make sure to check the GEDCOM structure at every commit. Either use local pre-commit hooks or a remote job on GitHub/GitLab/whatever.
* **Long-running autonomous tasks**: After a while, I got frustrated waiting for my quota to reset, and treated myself to Claude Max. I now had a gazillion tokens to use. It was not long before I started to run sessions overnight or while at work. If you are in this case, you need to come up with a strong structure so you don't find out at the end of the whole session that you have drifted.

  I found out that my sweet spot was:
  * A subagent to avoid polluting the main agent's context
  * Serial subagents, not parallel, since I don't have a deadline, and it avoids facing the quota limit mid-month
  * The subagent works, commits in a worktree branch, the main agent checks and merges

## What I learned about genealogy

Here are the things I learned. For genealogy experts, they may seem obvious. However, if you're a beginner, I hope they might be helpful.

* **Different countries have different potential**: Depending on the country, you may get widely different results with the same process. Results may even differ for places inside the same country.

  For this reason, and depending on where your ancestors stem from, the tree will probably be unbalanced. In some cases, it may even be quite limited.
* **A lot of data is already there**: There are plenty of genealogy websites around. All of these I stumbled upon have a free tier. If you can find a common ancestor with someone who already did the work upwards for you, why not use it?
* **Work of others can be wrong**: While you will populate your tree very fast with third-party work, don't take it as proof. You need to verify the facts with a legitimate administrative certificate.

  For the record, I stumbled upon a collateral family member who supposedly died in Switzerland. Since he was the only one, I spent a bit of time trying to find his death certificate, to no avail. It turns out that he died in France in a hunting accident, but next to the border. It made the news in a local Swiss newspaper, and the person inferred that he had died there. Trust, but verify.
* **People are genuinely trying to help**: Everyone I have contacted on these sites has answered. Everyone tried to help. In the worst case, I got nothing that I could use. In the best case, actually two, I got connected to historians who wrote books on the subject.
* **Birth certificates help push boundaries**: Birth certificates mention the parents and their own birth date and place. If you can loop over several times, you will get a few generations in no time.

  Marriage certificates are the next best thing. You'll find the groom and bride's parents' names, but will probably miss their own birth data.
* **French marriages usually happened at the bride's parish**: I had never heard of this tradition, but Claude Code apparently did. In the past, for marriages happening between people from different towns, the tradition was to hold it at the bride's parish.

  Treat it as a heuristic rather than a rule. I had a couple of cases when my ancestors didn't follow the tradition.
* **French civil acts are public 75 years after the event's date**: In France, if you want a civil act, you need to prove you're either the person in the act or a direct ancestor. But that rule is removed if the act dates back 75 years or more. You can actually access the birth certificate of public figures such as Général de Gaulle this way, and search for their ancestors.
* **In small hamlets, everyone had the same surname**: Because some of them also had the same first name, they had to add a discriminant. The discriminant was fortunately sometimes written in civil records. When it wasn't, it got extremely confusing.

  Savoy is a good example of such surname things. They added a second surname there. You can read more about it [here](https://www.marmottesdesavoie.fr/index.php?page=patro) (in French).
* **GEDCOM allows defining a place format**: I came to know about GEDCOM by working on the project, so the TIL items are a very long list. However, I think this one takes first place.

  Different countries have different location structures. GEDCOM has a powerful **and** flexible scheme for locations, which allows defining the format, then the value.

      2 PLAC Lyon, 7e, Rhône, France
      3 FORM Ville, Arrondissement, Département, Pays

## Visualization with Topola on Cloudflare Pages

The more people you add to your tree, the harder it is to visualize the GEDCOM model in your head. I searched for a couple of visualization solutions and found:

* [Gramps](https://gramps-project.org/): It's a desktop-based application, available for [many different platforms](https://gramps-project.org/wiki/index.php/Download). It works pretty well, but its internal storage is SQLite. Though you can import GEDCOM, you need to clear the existing data first: you can't override them.
* [Topola Genealogy Viewer](https://github.com/PeWu/topola-viewer): Topola provides an [interactive website](https://pewu.github.io/topola-viewer/) for you to display a GEDCOM 5.5.1 file. It's the software I chose.

Topola also allows you to display the tree from one single static GEDCOM file. You can use it in a build process, *e.g.*, a GitHub workflow, to generate the visualization website. The tree holds data about people who are still alive. I keep it private for this reason. GitHub Pages are public by default, so I didn't want to go this route.

I checked for options and found [Cloudflare Pages](https://pages.cloudflare.com/). It's the first time I've used it, and it's amazing:

* It integrates natively with GitHub
* You can configure authentication. The easiest one is on a list of allowed emails you configure.
* It creates a subdomain for each branch, so you can preview changes!

The only downside is a limit of 500 builds a month, more than enough for a free offer and a hobby project.

This is also the reason for the two `git` providers. Codeberg hosts the repository I work on. GitHub only holds a mirror, because Cloudflare Pages builds from it.

## Conclusion

I never thought about genealogy before. However, it's a lot of fun, and I'm hooked on it now. In one month, the assistant got me what some people spend years searching for. I hope this post gave you ideas and set you on the path.

**To go further:**

* [GEDCOM The Genealogy Data Standard](https://www.gedcom.org)
* [Later GEDCOM specifications](https://gedcom.io/specs/)
* [Agent Skills](https://agentskills.io/home)
* [Gramps](https://gramps-project.org/)
* [Topola Genealogy Viewer](https://github.com/PeWu/topola-viewer)
* [Cloudflare Pages](https://pages.cloudflare.com/)

*Originally published at [A Java Geek](https://blog.frankel.ch/ai-assisted-genealogy/) on August 30^th^, 2026.*

*[OTP]: One-Time Password
