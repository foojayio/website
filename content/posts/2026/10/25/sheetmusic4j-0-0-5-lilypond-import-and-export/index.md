---
title: "Sheetmusic4J 0.0.5: LilyPond Import and Export"
date: "2026-10-25"
description: "Sheetmusic4J 0.0.5 can read and write LilyPond files through the ScoreFile facade, and brings engraving improvements like tuplet placement and octave clefs."
canonical: "https://webtechie.be/post/sheetmusic4j-0.0.5-lilypond/"
authors:
  - "frankdelporte"
image: "lilypond-bach-bwv610.png"
categories:
  - "JavaFX"
related_posts:
  - "introducing-sheetmusic4j-a-javafx-library-to-render-and-interact-with-sheet-music"
  - "sheetmusic4j-0-0-3-abc-notation-guitar-pro-engraving-improvements"
  - "melodymatrix-v1-0-0-released-shipping-a-javafx-app-with-jdeploy-github-actions-and-auto-update"
  - "the-javafx-revival"
---

[Sheetmusic4J](https://sheetmusic4j.com/) is a Java(FX) library to render and interact with sheet music. After MusicXML, MIDI, ABC and Guitar Pro, version **0.0.5** adds another format: [LilyPond](https://sheetmusic4j.com/inspired-by/lilypond/).

Thanks to Dani_E2e on [Reddit](https://www.reddit.com/r/java/comments/1vapmf2/comment/p0ppvzf/) and Jurgen Lambrecht for suggesting LilyPond support!

{{< youtube 8n7WONyX0yk >}}

## What Is LilyPond?

[LilyPond](https://lilypond.org/) is the free, text-based engraving program that many musicians and publishers use for beautiful printed scores. You write the music as plain text in a `.ly` file, and LilyPond compiles it into a PDF. A lot of classical repertoire, from Bach chorales to full orchestral scores, is available as `.ly` source, for example at the [Mutopia Project](https://www.mutopiaproject.org/).

```lilypond
\version "2.24.0"
\header {
  title = "C Major Scale"
}

\relative c' {
  \clef treble
  \key c \major
  \time 4/4
  c4 d e f | g a b c | c b a g | f e d c |
}
```

## Import and Export

With the `ScoreFile` facade, a `.ly` file can be loaded and rendered natively in JavaFX, without running LilyPond. Any score, whatever its original format, can also be saved as LilyPond to get a high-quality PDF out of it:

```java
Score score = ScoreFile.load(Path.of("bach-bwv610.ly"));

// ...and write any score back out as LilyPond
ScoreFile.save(score, Path.of("bach-copy.ly"));
```

LilyPond's input is closer to a programming language than to a data format, so the reader works like LilyPond itself: a parser resolves note names, default durations and variables, an interpreter runs the music through time into staves, voices, lyrics and chord names, and an assembler cuts the result into measures and derives what LilyPond leaves implicit, like ties across bar lines, accidentals and beams.

Supported are, among others, `\relative`, `\fixed` and `\transpose`, chords, ties, slurs, tuplets, grace notes, repeats with alternatives, multiple staves and voices, piano staves, lyrics, chord names, dynamics, tempo and rehearsal marks. Engraving tweaks like `\override` and Scheme code are skipped, because Sheetmusic4J does its own layout. The full list is on the [LilyPond page](https://sheetmusic4j.com/inspired-by/lilypond/).

The test fixtures include real-world snippets from the LilyPond documentation (a Bach organ chorale, an orchestral score, tablature, a Stockhausen piece...), which is a nice way to discover what still needs work.

## Engraving Improvements

Real LilyPond scores exercised the engraver in new ways, which led to improvements that benefit every format:

* **Tuplets** now go on the stem side like beams do, or where the score says (`placement` is also read from MusicXML), clear the noteheads and beams, and support nested tuplets.
* **Octave clefs**, such as the guitar's treble clef with an 8 below, and the percussion clef.
* **Identical layouts across CPU architectures**, by avoiding `Math.pow` differences in note spacing.

## Try It

Read the [full status update](https://sheetmusic4j.com/status/2026/20261021-release-0.0.5/) on the Sheetmusic4J website, check the [code examples](https://sheetmusic4j.com/code-examples/) to get started, or look at the [difference on GitHub](https://github.com/sheetmusic4j/sheetmusic4j/compare/v0.0.4...v0.0.5).

If a LilyPond file doesn't load or render correctly, [please let me know on GitHub](https://github.com/sheetmusic4j/sheetmusic4j/issues), ideally with the file attached.
