---
title: "One Embedding per Job, or Two? Measuring Semantic Search in a Spring Boot Job Portal"
date: "2026-10-14"
description: "I stored two embeddings for every job in my Spring Boot job portal and measured which one finds the right jobs. One of them barely mattered, and my similarity threshold turned out to mean much less than I thought."
authors:
  - "somil-garg"
image: "cover.jpg"
categories:
  - "AI"
  - "Spring"
  - "Testing"
related_posts: []
---

This job portal started as a practice project. I built it after college, mostly so I'd have something to put on my resume. Recently I picked it up again. I was job hunting myself, looking for a switch, and it struck me that I should develop it properly, and maybe even launch it someday.

The first thing that bothered me was search. The results just weren't good, so I started reading about how to make them better.

There was another reason, too. I've been working with Java for a long time, and I love it. But whenever AI comes up, everyone seems to reach for Python. I don't think it has to be that way. There are also a lot of older, legacy Java systems out there that can't simply be rewritten in another language. If they're going to get AI features, those features have to work in Java. So I decided to try it myself, in plain Spring Boot, and see how far I could get.

Here's what was wrong. The search box used to be a SQL `LIKE`. It worked fine as long as you typed exactly what the recruiter typed. Search "Java developer" and you got every posting with "Java developer" in it. Search "SDE working on Java services" and you got nothing, while the database was full of Java backend jobs.

Embeddings are the obvious fix, so I added them. What I didn't expect was how quickly I'd get stuck on a question the tutorials don't spend much time on: what text are you actually supposed to embed?

A job posting isn't one thing. There's a title, a paragraph of company marketing, a list of responsibilities, and somewhere in there a list of what they actually want from you. I couldn't decide, so I did both: two vectors per job. Then I built a small evaluation to find out whether that was smart or just twice the work.

Short version: for the search mode I actually ship, it barely mattered. The longer version, plus one number that genuinely surprised me, is below.

The setup, in case it matters to you: Spring Boot 3.5 on Java 17, TiDB as the database (it's MySQL-compatible and has a native `VECTOR` type), and Google's `gemini-embedding-001` at 768 dimensions. All the code is [on GitHub](https://github.com/SomilGarg1807/Job_Portal).

## Two ways to describe the same job

Both strategies build a small labelled text from the job entity. I don't think the labels themselves matter much. What matters is that it's the same fields in the same order every time.

`job_full_v1` takes everything: title, employment type, work mode, experience range and the whole description as plain text.

`job_req_v1` keeps the title and experience range, and then only the parts of the description that look like requirements. Finding those parts was more annoying than I thought, because nobody formats a job description the same way. Some people use real `h2` tags. Most just make a line bold, or end it with a colon, in whatever rich-text editor they're using. So with jsoup I treat all three as headings, and keep whatever sits under a heading that matches this:

```java
private static final Pattern REQUIREMENT_HEADING = Pattern.compile(
        "(?i)\\b(requirements?|qualifications?|skills?|must[- ]haves?|nice[- ]to[- ]haves?"
        + "|what you('ll)? (need|bring)|you (have|bring|are)|who you are|experience"
        + "|tech(nology)? stack|preferred)\\b");
```

If a posting has no headings like that, it falls back to every bullet point, and if there are no bullets either, to the whole description. That way it never returns empty text for a job that has a description.

My reasoning for the second vector went like this. When someone searches "Kafka" or "keep cloud services reliable", they're describing what *they* can do. A sentence like "Our synthetic team is hiring to deliver reliable software with product partners" is the company talking about itself. So the requirements should sit closer to what people actually type. That was the theory, anyway.

One small detail that paid off later: every stored vector has a SHA-256 hash of the strategy, the model, the dimension and the exact text next to it. If any of those change, the vector is stale and gets re-embedded. If nothing changed, re-running the backfill does nothing and costs nothing.

## Searching with both

Search is an exact cosine scan in SQL, only over the jobs that already passed the user's filters (location, job type, experience):

```sql
SELECT job_post_id, strategy, source_hash,
       1 - VEC_COSINE_DISTANCE(embedding, CAST(:vector AS VECTOR)) AS similarity
FROM ai_job_embedding
WHERE job_post_id IN (:ids) AND model = :model AND dimensions = :dims
  AND strategy IN ('job_full_v1', 'job_req_v1')
```

You might wonder why there's no vector index. TiDB only uses one for a bare `ORDER BY distance LIMIT k` with no `WHERE` clause, and my query always has one. With a few hundred jobs, an exact scan is correct and fast enough, so I stopped worrying about it.

That query gives back up to two rows per job, one for each strategy. Turning them into a single score is a small loop in plain Java:

```java
for (var match : vectors.matches(byId.keySet(), vector, properties.model(), properties.dimensions())) {
    JobPostActivity job = byId.get(match.id());
    if (job == null || !Double.isFinite(match.similarity()) || match.similarity() < minimumSimilarity) continue;
    EmbeddingStrategy strategy = EmbeddingStrategy.fromId(match.strategy());
    if (!properties.strategiesFor(EmbeddingTarget.JOB).contains(strategy)) continue;
    String text = EmbeddingTexts.job(strategy, job, properties.maxChars());
    String hash = SourceHash.of(strategy, properties.model(), properties.dimensions(), text);
    if (hash.equals(match.hash())) scores.merge(match.id(), match.similarity(), Math::max);
}
```

Two things are going on here. The hash check means a vector only counts if it was built from the job's *current* text. If a recruiter edited the posting and the backfill hasn't caught up yet, the old vector is simply ignored instead of quietly ranking the job on what it used to say. And `scores.merge(..., Math::max)` is the whole "two vectors" decision in one line: if either the full text or the requirements match, the job matches, with whichever score is higher.

Hybrid mode sits on top of that, in the controller. Every job that also matched the old keyword search gets a flat `+1`:

```java
keywords.forEach(j -> {
    combined.put(j.getJobPostId(), j);
    hybrid.put(j.getJobPostId(), scores.getOrDefault(j.getJobPostId(), 0.0) + 1.0);
});
```

Cosine similarity can't go above 1, so in practice that bonus puts every keyword hit above the semantic-only results. It's crude, but it means someone who types an exact job title never sees it pushed down by a "close enough" match.

To be honest, "take the max of both" was a guess. It sounded reasonable, so I went with it. Testing that guess is what the rest of this article is about.

## Building something to measure it

It's tempting to run a few searches yourself and decide it looks good. The problem is that you mostly end up seeing what you already believe. So I wrote a small evaluation harness instead. It's a separate Spring Boot entry point with its own profile, and it refuses to start unless the database name contains `eval`. I didn't want any chance of it touching real data.

Here's what it runs against:

- 288 synthetic jobs across 12 role families (Java backend, React, data engineering, SRE, security, product management and so on), generated from a fixed seed. Every company name starts with `Synthetic`, so they're easy to spot.
- 40 queries I wrote by hand, in seven groups: exact titles, synonyms ("SDE working on Java services"), bare skills ("Kafka", "OAuth"), role plus location, queries with experience filters, full sentences ("I want to keep cloud services reliable and respond to incidents"), and three queries that should return nothing at all, like "marine biologist studying coral reefs".
- Graded labels: 0 for irrelevant, 1 for relevant, 2 for highly relevant.

For every query it reports precision@5 (how many of the top five are relevant), recall@10 (how many of all the relevant jobs made it into the top ten), MRR@10 (how high the first good result sits), and whether the no-answer queries actually came back empty.

Two decisions made it cheap enough that I kept running it. First, every embedding is cached on disk, keyed by its SHA-256. The run behind the numbers below made zero Gemini API calls: all 80 embedding lookups were cache hits. Second, retrieval happens once per query, and every mode, strategy and threshold is scored in memory from that one result set. When I wanted to add the per-strategy comparison, I only had to touch the scoring loop.

## What came out

At the default similarity threshold of 0.55:

| Mode | Strategy | Precision@5 | Recall@10 | MRR@10 |
|---|---|---:|---:|---:|
| Keyword only | — | 0.370 | 0.168 | 0.450 |
| Semantic only | `job_full_v1` | 0.780 | 0.488 | 0.913 |
| Semantic only | `job_req_v1` | **0.825** | **0.515** | 0.913 |
| Semantic only | max of both | 0.815 | 0.510 | **0.925** |
| Hybrid | `job_full_v1` | **0.830** | **0.519** | **0.925** |
| Hybrid | `job_req_v1` | 0.825 | 0.515 | 0.913 |
| Hybrid | max of both | **0.830** | **0.519** | **0.925** |

The first result is the boring one: semantic search more than doubles precision compared to keywords. No surprise there. The fun part was the queries keyword search couldn't answer at all. "SDE working on Java services", "site reliability and production operations" and the incident-response sentence all got zero keyword results. Hybrid search put three highly relevant jobs at the top of each one.

The second result is the one I was actually curious about. In this eval, the requirements-only vector on its own beat the full posting. In semantic-only search, `job_req_v1` scored 4.5 points higher on precision@5 than `job_full_v1`. Taking the max of both gave the best MRR, but slightly lower precision than requirements alone. My guess is that all the company prose pulls the vector towards "generic software job" and away from the specific skills people search for.

The third result kind of undid the second. In hybrid mode, the difference almost disappears. With the keyword bonus in place, the full-description vector on its own scored exactly the same as "max of both". Keyword hits already pin the obvious results to the top, and the vectors mostly just sort the long tail, where both strategies agree anyway.

So in this eval, for the search mode I actually ship, the second vector bought me almost nothing. I haven't deleted it yet, though, and the next two sections explain why.

## What I can't claim from this

While writing this up, I had to be careful about what these numbers really show.

- The labels are partly estimated. I graded the first 341 judgements by hand. The rest were needed for results the first pass never saw, and I estimated those with rules I calibrated against my own grades. So treat the comparison as exploratory. It's a useful hint about which strategy works better, not a benchmark, and I'd want more human review and a fresh holdout set before trusting it in production.
- The jobs are synthetic. Real postings are longer, messier and full of boilerplate. If anything, that could help the requirements-only strategy *more* than my clean fixtures do.
- Forty queries can't tell 0.825 apart from 0.830. The semantic-only gap is worth noticing. The hybrid gap is noise.
- I tuned the threshold on the same data I'm reporting here. Which brings me to the part that surprised me.

## 0.55 isn't really a cutoff

Remember the three queries that should return nothing? They all returned jobs. Here's how close their best matches were:

| Query | Best match | Similarity |
|---|---|---:|
| marine biologist studying coral reefs | a data analytics job | 0.744 |
| airline pilot flying passenger aircraft | a site reliability job | 0.732 |
| veterinary surgeon for small animals | a cloud engineering job | 0.739 |

You'd expect a marine biologist and a data analytics job to score somewhere near zero. They scored 0.744.

I had set 0.55 almost without thinking. It looked like a safe number. Seeing a marine biologist land on a data analytics job at 0.744 was the moment I realised I didn't really understand what these scores meant.

With `gemini-embedding-001`, texts that are all shaped like job postings sit close together, even when they have nothing to do with each other.

I swept the threshold to see where those three finally came back empty. The first value that worked was 0.75, which beat their highest score by just 0.006. And it was expensive: recall for semantic-only skill queries dropped hard. "Kafka" went from 0.556 recall@10 down to 0.056.

So I left production at 0.55. I chose those three no-answer queries myself and then used them to pick a threshold, and that isn't evidence of anything. What I need next is a fresh holdout set with many more no-answer queries, evaluated *after* the threshold is fixed. It's on my list.

## What I'd tell myself before starting

1. **Measure what you embed.** In semantic-only search, just changing which text I embedded moved precision@5 by 4.5 points, without touching the model.
2. **Embed the part that sounds like a search.** If you can only store one view of a document, pick the one closest to how people search. For jobs, that's the requirements, not the marketing.
3. **Hybrid search hides a lot.** The keyword bonus smoothed over most of the difference between strategies. Nice for users, less nice when you're trying to justify a second vector.
4. **A similarity score isn't a probability.** 0.74 between "marine biologist" and a data job is completely normal here. Set thresholds using queries that are supposed to fail.
5. **Make the eval cheap, or you won't run it.** The disk cache plus scoring everything from one retrieval pass turned a rerun into a few minutes with no API cost. That's why I run it after every change instead of once.

For me, though, the biggest takeaway wasn't a number. It was that none of this needed Python. Spring Boot, a SQL database and a few hundred lines of Java were enough to build it and measure it. If you're sitting on an older Java application and wondering whether AI features are out of reach, I hope this shows they aren't.

If you want to dig in, the harness, the dataset and the full results (including the threshold sweep) are in the [`eval` folder of the repository](https://github.com/SomilGarg1807/Job_Portal/tree/main/eval). And if you've found a better way to decide what to embed, I'd really like to hear about it.
