---
title: "AI-assisted genealogy, a follow-up"
date: "2026-09-27"
description: "A follow-up on AI-assisted genealogy: source quality with GEDCOM QUAY, per-site browsing skills, Playwright sessions, transcriptions, and hiccups along the way."
canonical: "https://blog.frankel.ch/ai-assisted-genealogy-follow-up/"
authors:
  - "nicolas-frankel"
image: "cover_large.jpg"
categories:
  - "AI"
  - "Use Cases"
related_posts:
  - "ai-assisted-genealogy"
  - "my-first-steps-with-playwright"
  - "writing-an-agent-skill"
---

I got a lot of feedback on my post [AI-assisted genealogy](https://blog.frankel.ch/ai-assisted-genealogy/), some good, some not so good. In any case, I felt the subject was interesting to a lot of people. Meanwhile, I continue working on my tree, and I have deepened my understanding of the subject. In this post, I want to share again.

## Trust, but verify

Let's address the not-so-good feedback first. The gist of it was: AI can hallucinate. It's true in theory: when you ask a question, it provides any answer. The pushback is much less valid when the answer is grounded in data.

The other feedback I had was that genealogy sites weren't trustworthy sources. It's a valid point, which I already acknowledged in the initial post.

> _The process is always the same._
>
> _Pick someone in the tree with unknown parents, and let the assistant do the search. It knows which genealogy sites to browse, and which archives hold the civil records for the place. When it finds the act, it transcribes it, adds the new individuals to the GEDCOM file, links them to their child, and records the source. Then it commits._
>
> _Rinse and repeat. Each loop adds one generation on one branch, until no records are found._
>
> — [The loop](https://blog.frankel.ch/ai-assisted-genealogy/#the_loop)

I realize that I could have been more detailed, so let me fix it.

GEDCOM has the concept of quality, with the `QUAY` directive:

> The QUAY tag's value conveys the submitter's quantitative evaluation of the credibility of a piece of information, based upon its supporting evidence. Some systems use this feature to rank multiple conflicting opinions for display of most likely information first. It is not intended to eliminate the receiver's need to evaluate the evidence for themselves.
>
> | Quality | Description |
> | --- | --- |
> | 0 | Unreliable evidence or estimated data |
> | 1 | Questionable reliability of evidence (interviews, census, oral genealogies, or potential for bias for example, an autobiography) |
> | 2 | Secondary evidence, data officially recorded sometime after event |
> | 3 | Direct and primary evidence used, or by dominance of the evidence |

The table defines guidelines, not precisely defined rules, so I interpreted them:

* Genealogy sites are `QUAY 2`
* Official acts, whether civil or parish-based, are `QUAY 3`

The process is the following:

* Mine the huge amount of data available on genealogy sites
* Set their `QUAY` to 2
* Check the data is in an official record
* If yes, set the data's `QUAY` to 3
* Otherwise, discard the data

Hence, trust, but verify.

## Archives and browsing skills

In the original post, I mentioned you should create one skill "per site". As in the previous section, I may have been a bit concise. Let me detail:

* One per genealogy site (Geni, Geneanet, etc.)
* One skill per archive site, whether city or department archives

Archive sites can be roughly classified along two orthogonal axes.

* Account: fully open access vs. required account
* Bot protection: I identified the 3 following levels.
  * At the lowest, the site is accessible with `curl`
  * Needs a full-fledged browser. For this, I added a small `uv`-based Python project to steer [Playwright](https://blog.frankel.ch/first-steps-playwright/).
  * Cloudflare-protected (or similar). This is the highest protection level I faced. In some cases, even checking the "I'm a human" checkbox didn't work. To bypass it, I used Chrome in debug mode; I'll write a full-fledged post about it.

## Automating account login

I mentioned that some sites require you to be logged in. Of course, you could give your assistant access to your credentials, but that wouldn't be a smart thing to do.

For the highest bot protection, there's no alternative but to start Chrome in debug mode, log in, and let the assistant steer the browser via the debug port. It's fragile: anything that closes the browser ends the run.

For other sites, you should use Playwright with stored sessions. It goes like this:

* Launch a browser with a persistent session
* Log in to the sites you want to use
* Close the browser. The profile has stored the authentication data.

Later on, the assistant can start the browser within the context of the same session: the assistant will have the same access as the user who logged in. Session length depends on the exact site, so you should do it before an unattended run.

The benefit over the debug approach is that it survives closing the browser.

## Leveraging genealogy sites

My original advice was to mine genealogy sites with a premium subscription, then switch to official records. Since then, I have refined the approach.

First, I have made it a cycle. After uncovering new ancestors in records, I went back to genealogy sites. In some cases, these ancestors were already part of others' trees, and I could jump a couple of generations again. Even if these later jumps are much smaller than the initial ones, it still means fewer tokens pinpointing the exact act in a record.

These new ancestors don't automatically come into your tree. Most genealogy sites have the concept of a smart match. They run heuristics able to match (hence the name) people from unrelated trees. Some sites are better than others for this. You need to be **very** careful when you accept a match, as it can corrupt a whole branch of your tree, by adding unrelated people. Hence, I tend to be very conservative. On the other hand, you can discover new spelling variants of your ancestors' names. Perhaps you missed an act because the assistant didn't search for the correct variant?

Finally, most (all?) of these sites have two tiers: full data access and limited data access. Full access is unlocked during a trial period, either when you register or when you subscribe. Subscription can be expensive. Worse, some sites only offer a yearly subscription package. I had no qualms about registering email addresses.

## Leveraging Topola

I still use **and** love [Topola](https://github.com/PeWu/topola-viewer), the GEDCOM viewer.

Among its features, it allows displaying a thumbnail for an `INDI` in the tree. It works with the first `OBJE` object associated with the `INDI`. Other `OBJE` are displayed in the right-side panel. If you have photographs of your ancestors, it's a nifty feature.

I like order, so I follow these rules:

* The profile picture must be a portrait. If I have no portrait, I crop an existing one to serve as such.
* All `OBJE` that reference images are set in chronological order. Thus, the right panel displays them in the same order.

## Transcriptions

Transcriptions are essential. Acts are images, and you want their data to be easily accessible.

From the beginning, I delegated the transcription process to the assistant. Your ability to decipher cursive script depends on several factors:

* The writing style of the clerk. The further back in time you go, the less standard the writing style is and the harder it is to decipher.
* Your familiarity with the language. Depending on your tree, you may have ancestors with acts in foreign languages.
* The act's age. The older the act, the harder it is to recognize some expressions, and the more the orthography is "fluid".

For these reasons, I started with transcriptions made by the assistant. I noticed it was quite careful sometimes: where I would have inferred a word, it decided to put a placeholder. It did the same for letters. I tried several alternatives.

* I asked for the best tool available and [Transkribus](https://app.transkribus.org/). It's a paid online tool, with lots of model choices. Some models are premium, others are free. It also gives you 50 free transcriptions a month. You just need to agree to wait in the queue, as runners for freeloaders are few.

    I tried 3 or 4 times after having exceeded my assistant quota. Results were abysmally bad, producing only gibberish, not even word soup. I read later that you had to train the model on your own document. It means you need to transcribe one or two pages of a record, and the results would improve over subsequent acts of the same record. Honestly, I won't use it again.

* To save my assistant tokens, I also tried a local setup. I leveraged [kraken](https://kraken.re/main/index.html) with dedicated models depending on the language and the era. It was much better than Transkribus, but nowhere near the assistant capabilities. Whether I missed a configuration step or assistants have made huge leaps in ability, I'll never know. In the end, I just scrapped the skill.
* A very good surprise is [Filae](https://www.filae.com/). It's a small French genealogy site. However, its transcription capabilities are second to none for French handwriting. I used it a couple of times with amazing results. The only issue is that it seems that they switched off this service. A pity.

## Hiccups and hurdles

Now that I've fleshed out more details about creating your family tree, here are some issues I have encountered, for fun. In no particular order:

* Three or even four generations of ancestors who lacked imagination and gave a child their first name. Now, you have a bunch of people in the same place with the same first name and last name.
* You get used to records being births, baptisms, marriages, deaths, or burials? What about a bit more variety, mixing some of them into the same one? Binary-searching for an act by date is now much more challenging.
* Mixed act types in a single record is just the beginning. It gets even better when dates aren't monotonic, _i.e._, in no order. It's the case for some notarial records I stumbled upon.

## Conclusion

I'm still working on the project, and now have more than 1200 people and span 13 generations on a branch. I hope this additional advice will be helpful if you choose to follow the same path.

**To go further:**

* [GEDCOM 5.5.1 Specification](https://gedcom.io/specifications/ged551.pdf)
* [Playwright: Profile & State](https://playwright.dev/mcp/configuration/user-profile)
* [Topola Genealogy Viewer](https://github.com/PeWu/topola-viewer)
* [Transkribus](https://app.transkribus.org/)
* [kraken](https://kraken.re/main/index.html)
* [Filae](https://www.filae.com/)

_Originally published at [A Java Geek](https://blog.frankel.ch/ai-assisted-genealogy-follow-up/) on September 20<sup>th</sup>, 2026._
