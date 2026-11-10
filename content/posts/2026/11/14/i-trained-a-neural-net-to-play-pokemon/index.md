---
title: "I Trained a Neural Net to Play Pokémon"
date: "2026-11-14"
description: "A neural net that plays the Pokémon TCG, 4.2 million games, and the Java batch jobs that kept the training league running overnight."
authors:
  - "nicholas-dhondt"
image: "neural-net-plays-pokemon.jpg"
categories:
  - "Java"
  - "Machine Learning"
  - "AI"
related_posts:
  - "durable-execution-is-a-property-not-a-product"
  - "how-we-built-a-java-ai-agent-by-connecting-the-dots-the-ecosystem-already-had"
  - "carbon-aware-job-processing-with-jobrunr-v8"
---

I taught a neural network to play the Pokémon Trading Card Game. It has now played more than 4.2 million games against itself and a room full of other decks, and these days it is my sparring partner: I play against it to get better at my own decks.

This is a story about a side project that got out of hand. It is also, I promise, a Java story. The neural net is small and the training algorithm is textbook. The part that took real engineering was everything around it: playing thousands of games in parallel, training on them, testing the result, and doing that 455 times in a row without me sitting next to the laptop. That part is written in Java.

Full disclosure: I work at [JobRunr](https://www.jobrunr.io/en/?utm_source=foojay&utm_medium=article&utm_campaign=pokemon-neural-net&utm_content=intro), a background job scheduler for Java, and part of my job is finding odd projects to throw it at. This one was too good to pass up.

## Why Pokémon

Because I play it, and because it is a much nastier game for a machine than it looks.

In chess, both players see the whole board. In Pokémon you see your own hand, and your opponent's hand, deck and prize cards are hidden. Every turn starts with a random draw. And a turn is not one move. A turn is ten or fifteen small decisions in a row: which card to play, which Pokémon gets the Energy, which two cards to discard, whether to attack now or set up for next turn. An average game in my simulator is 104 decisions.

I also had a selfish reason. I wanted a sparring partner for my own decks that plays at any hour and never gets tired of the same matchup.

## Three languages, one boss

Here is the architecture, and I am going to be honest about it, because "everything in Java" would be a nicer headline and a worse design.

- **Node plays.** I did not write the rules of the card game myself. I reused the engine from [tcgmasters.net](https://tcgmasters.net/), which already knows what every card does, so I did not have to script hundreds of cards by hand again. That engine is JavaScript, so the games run headless in Node. One game takes about 11 milliseconds on one core of my MacBook. (If you feel like writing a Java port of a Pokémon TCG engine, let me know. I will happily swap it into the project.)
- **Python trains.** PyTorch does the gradient descent. The net has 1.3 million parameters, which is tiny. It learns with PPO, a standard reinforcement learning algorithm.
- **Java decides what runs when.** It schedules the games, starts the training steps, runs the tests, promotes the winners, and picks everything back up after a crash.

The net itself is exported to ONNX and runs inside the Node workers. I considered calling into the JVM for every decision, and then did the math: a net call takes 0.12 milliseconds, a whole game takes 16. A round trip between processes for each of those 104 decisions would have cost more than the game itself. So the JVM stays out of the hot path and does what it is good at, which is being the adult in the room.

With 12 workers, that setup plays about 200 games per second with the net choosing every move.

## It's not an AI problem, it's a background job problem

Reinforcement learning sounds exotic. In practice the nets learn by playing each other, and I call that setup the league. Every deck has its own neural net. They all play each other and a set of scripted opponents, and each deck has a reigning champion that a newer version has to beat. One round of the league looks like this:

1. Play a few thousand games in parallel, every deck against every other deck.
2. When all of those are done, train each deck's net on its own games.
3. Every fifth round, let the new net play the reigning champion.
4. Crown the new net only if it really is better.
5. Start the next round.

Read that list again as a Java developer. Fan out, wait for all, continue, gate, repeat. With retries, because a process will die somewhere in round 212. With a cap on parallelism, because the laptop has 14 cores and a fan. With a way to resume, because the run takes all night.

That is a batch job with continuations. So I wrote it as one. This is the heart of the league, trimmed a little:

```java
public void iteration(String run, int it, JobContext ctx) {
    JobProId play = BackgroundJob.create(aBatchJob()
            .withId(id(run, "it" + it + ":play"))
            .withName("it " + it + ": play, " + shards + " shards")
            .withLabels("run:" + runName(run), "it:" + it, "play")
            .withJobLambda(() -> jobs.playBatch(run, it, specPath, shards)));

    JobProId train = BackgroundJob.create(aBatchJob()
            .withId(id(run, "it" + it + ":train"))
            .runAfterSuccessOf(play)
            .withName("it " + it + ": train " + decks.size() + " decks")
            .withJobLambda(() -> jobs.trainBatch(run, it, shards, JobContext.Null)));

    // every fifth iteration: eval batch, then the promotion gate

    BackgroundJob.create(aJob()
            .withId(id(run, "it" + it + ":finish"))
            .runAfterSuccessOf(lastStep)
            .withName("it " + it + ": finish, state.json and next iteration")
            .withJobLambda(() -> jobs.finish(run, it, JobContext.Null)));
}
```

The play batch creates one child job per shard, and each child starts a Node process that plays its slice of the games. The train batch only starts when every shard has succeeded. The last job writes the results and enqueues the next iteration, so the league keeps itself going.

{{< gallery cols="1" >}}
play-shards-processing.jpg | The JobRunr dashboard during iteration 115: one batch job and its twelve play shards, all processing at the same time
{{< /gallery >}}

A job in this setup is a thin wrapper around a child process. It runs `node` or `python` with `ProcessBuilder`, reads the last JSON line from stdout, and throws if the exit code is not zero. Throwing is the whole error strategy, because a thrown exception means a failed job, and a failed job gets retried.

```java
public void playShard(String run, String specPath, int shard, int nshards, JobContext ctx) throws Exception {
    JsonNode result = Proc.runJson(List.of("node", "rl/play.mjs", "--spec", specPath,
            "--shard", String.valueOf(shard), "--nshards", String.valueOf(nshards)), simDir, ctx);
    if (result.path("gameErrors").asInt() > 0)
        throw new IllegalStateException("shard " + shard + ": crashed games, retrying");
    PyJson.writeAtomic(summaryPath(shard), PyJson.dumps(result));
}
```

Everything the child process prints as its summary also lands in the dashboard log of that job. So when I wanted to know how training for one deck was going, I did not tail a log file. I clicked on the job.

{{< gallery cols="1" >}}
train-job-ppo-log.jpg | A training job in the dashboard: the exact Python command, the PPO summary with 79,088 training steps, and the job finishing in 17 seconds
{{< /gallery >}}

## Three Java details I ended up liking

**Deterministic job ids.** Every coordinating job gets an id derived from the run and the step:

```java
static UUID id(String run, String step) {
    return UUID.nameUUIDFromBytes((run + "|" + step).getBytes(StandardCharsets.UTF_8));
}
```

JobRunr ignores a job whose id already exists. So if a job that creates other jobs gets retried, or I start the CLI a second time by accident, no step is ever created twice. It is the cheapest idempotency trick I know, and it removed a whole category of "why did iteration 41 run twice" bugs before they existed.

**Rate limiters as a thermostat.** Play jobs go through a limiter called `node`, training jobs through one called `python`. Two lines of configuration decide how many of each run at once.

```java
.useRateLimiter(
        concurrentJobRateLimiter("node", maxNode).asCreatedByConfiguration(),
        concurrentJobRateLimiter("python", parUpdates).asCreatedByConfiguration())
```

{{< gallery cols="1" >}}
rate-limiters.png | The rate limiters page: node capped at 12 concurrent jobs, python at 3
{{< /gallery >}}

This turned out to matter more than I expected. More on that in a minute.

**I ported Python's random number generator to Java.** The league started life as a Python loop. I wanted the Java version to be provably the same league, so that a run could be continued by either one. That meant sampling the same opponents for the same iteration, which meant the same random numbers, which meant a 90-line Mersenne Twister in Java with CPython's seeding, plus a unit test full of reference values printed by Python itself. The parity test passed: one iteration from the Java league and one from the Python loop, starting from the same nets, produced the same 220 games, result for result. Not my most productive evening.

## The gate that decides who gets the crown

A new net does not become champion because it trained longer. It has to win.

Every fifth iteration, the candidate and the reigning champion play the same set of games: same opponents, same shuffles, same seats. Because the games are paired, I can look at the difference per game instead of two noisy averages. The gate itself is one line of Java:

```java
boolean promote = mean >= gateMin && mean >= gateSe * se;
```

The candidate needs to be at least one point better, and that gap has to be at least 1.5 standard errors. Otherwise the snapshot is deleted and the old champion stays.

{{< gallery cols="1" >}}
promotion-gate.jpg | The promotion gate at iteration 110: the Mew candidate wins 48.8% against the champion's 46.3% over 1,000 paired games and is promoted, the Rayquaza candidate is 0.4 points better and is not
{{< /gallery >}}

I like this screenshot a lot. Two decks, same iteration. One candidate is 2.5 points better and takes the crown. The other is 0.4 points better, which is noise, and gets thrown away. No human was awake for either decision.

## The night my laptop tried to quit

On the first evening I let it run at full load: 12 Node processes plus training. Around iteration 40 of the third league I noticed something I had never seen before. The battery was draining while the laptop was plugged in. A 90-watt charger could not keep up with 14 busy cores.

So I killed the run, lowered the `node` rate limiter to 6, and started it again. And here is the part where I got to enjoy my own product. I did not have to work out which shards had finished and which had not. The jobs that were in flight when the JVM died were detected as orphaned and simply ran again. Over the whole project that happened to 50 jobs, and every one of them completed afterwards. I had tested this earlier with a deliberate `kill -9` in the middle of a play batch: the log said "Found 6 orphan jobs", the six shards ran again, and the iteration finished with exactly the right number of games and no duplicates.

For the record, retries and orphan recovery are in the open-source version of JobRunr. Batches, continuations and the rate limiters are JobRunr Pro, which is what the league runs on. Storage is a single H2 file. No cluster, no message broker, nothing to install except a JDK.

## So, can it play?

Yes. Better than I hoped.

Before any reinforcement learning, each net first learned to imitate a hand-written strategy bot for its deck. Then the league took over. For my main deck, the bot it copied won 55.5% of its games against ten opponent decks. After the first league, the net won 72% of the same games.

After three league generations, its win rate against 13 decks from a recent Regional Championship went from 69% to 81%. That is 200 games per opponent, so give each number a few points of slack. When two new decks showed up that it had never trained against, I added them to the league, and it went from 47% to 59.5% against one and from 56.5% to 70% against the other, without getting worse against the old ones.

{{< gallery cols="1" >}}
dashboard-overview.jpg | The dashboard after the first league: 4,408 jobs, 100% success ratio, zero retries
{{< /gallery >}}

The full tally so far:

- more than 4.2 million games in total, about 2.8 million of them in the league
- 455 league iterations
- 18,560 background jobs, of which 2 ended up failed
- 50 jobs recovered after a restart
- all four league generations done within about 26 hours

## What I took from it

Most of what we call "AI infrastructure" in a project like this is plain old job processing. Fan out, wait, continue, retry, resume. Java developers have been solving that for twenty years, and the tools for it on the JVM are boring in the best sense of the word. The model can live in Python and the simulator in whatever language it was born in. The thing that keeps the whole circus running overnight is a very good fit for Java.

Durable jobs are not only for invoices and emails. Sometimes they are for a Charizard.

---

*Nicholas D'hondt works at [JobRunr](https://www.jobrunr.io/en/?utm_source=foojay&utm_medium=article&utm_campaign=pokemon-neural-net&utm_content=footer), an open-source background job scheduler for Java. The league described here runs on JobRunr Pro 9 with Java 21, Node and PyTorch on a single MacBook Pro. Pokémon and the Pokémon Trading Card Game are trademarks of Nintendo, Creatures and GAME FREAK. This is a personal hobby project and is not affiliated with or endorsed by them.*
