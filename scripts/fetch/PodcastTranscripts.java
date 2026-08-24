///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 17+

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Reading transcripts for the Foojay Podcast, written into each episode's page
 * bundle as `transcript.md`.
 *
 * WHY THE CAPTIONS AND NOT A TRANSCRIPTION OF THE AUDIO. Every episode is
 * published on foojay's own YouTube channel, and YouTube has already run speech
 * recognition over all of them -- so the text exists, it is foojay's own
 * content, and fetching it costs seconds per episode where transcribing ~75
 * hours of audio locally costs hours of compute for a result of the same kind.
 * `yt-dlp --write-auto-subs` is the fetch; the YouTube Data API's
 * captions.download would also work but needs an OAuth client and blows the
 * default 10,000-unit daily quota at 250 units per episode.
 *
 * WHAT THIS IS AND IS NOT. It is a machine transcript, and the page says so.
 * Automatic captions do NOT satisfy WCAG 1.2.2 -- captions have to be accurate,
 * and speech recognition mangles exactly what this podcast is full of: names,
 * "Quarkus", "JEP 401", "GraalVM". What a transcript does give is a page a
 * reader can skim, search and hand to a screen reader instead of an audio-only
 * medium, which is a large practical improvement over nothing. Correcting one
 * is a normal content edit afterwards: the file is Markdown in the bundle, and
 * a corrected transcript is never overwritten (see `--force`).
 *
 * THE FILE IS A PAGE RESOURCE, NOT A SECOND PAGE. `transcript.md` inside a leaf
 * bundle is exposed to the layout as a resource of type "page" (verified), so
 * `posts/single.html` renders it with `.Resources.GetMatch` and Hugo never
 * publishes a URL for it. That is also what makes the rendering DERIVED: an
 * episode has a transcript because the file is there, with no frontmatter flag
 * to set, forget or unset.
 *
 * Usage (from the repo root, like every script here):
 *   jbang scripts/fetch/PodcastTranscripts.java
 *   jbang scripts/fetch/PodcastTranscripts.java --slug foojay-podcast-100 --force
 *   jbang scripts/fetch/PodcastTranscripts.java --report-variants
 *
 * Run BY HAND, never in CI: it needs the yt-dlp binary, it talks to YouTube ~100
 * times, and new episodes arrive one at a time -- the natural moment to run it
 * is the pull request that publishes the episode. Same posture as
 * transfer/Sponsors.java.
 */
public class PodcastTranscripts {

    /** Where the raw .vtt files are kept between runs. Gitignored: they are a
     *  download cache, and the transcript in the bundle is the artefact worth
     *  committing. Kept rather than deleted so a conversion change can be
     *  re-run over 99 episodes without re-fetching any of them. */
    static final Path CACHE = Path.of(".cache/podcast-captions");

    static final Path POSTS = Path.of("content/posts");

    /** The episode's video: the first {{< youtube ... >}} in the body. Matches
     *  both the positional form ({{< youtube ID >}}) and the named one
     *  ({{< youtube id="ID" title="..." >}}). */
    static final Pattern YOUTUBE = Pattern.compile("\\{\\{<\\s*youtube\\s+([^>]*?)\\s*>\\s*\\}\\}");
    static final Pattern VIDEO_ID = Pattern.compile("[A-Za-z0-9_-]{11}");
    static final Pattern PODCAST_CATEGORY = Pattern.compile("(?m)^\\s+-\\s+\"Podcast\"\\s*$");
    static final Pattern TITLE = Pattern.compile("(?m)^title:\\s*\"(.*)\"\\s*$");

    public static void main(String[] args) throws Exception {
        List<String> argv = List.of(args);
        boolean dryRun = argv.contains("--dry-run");
        boolean force = argv.contains("--force");
        boolean noDownload = argv.contains("--no-download");
        boolean reportVariants = argv.contains("--report-variants");
        String onlySlug = valueOf(argv, "--slug");
        int limit = Integer.parseInt(Objects.requireNonNullElse(valueOf(argv, "--limit"), "0"));

        List<Episode> episodes = discover();
        System.out.println(episodes.size() + " podcast episode(s) with a video");
        if (onlySlug != null) {
            episodes = episodes.stream().filter(e -> e.slug.equals(onlySlug)).toList();
            if (episodes.isEmpty()) { System.err.println("no episode with slug " + onlySlug); System.exit(1); }
        }
        if (limit > 0 && episodes.size() > limit) episodes = episodes.subList(0, limit);

        if (reportVariants) { reportVariants(episodes); return; }

        Files.createDirectories(CACHE);

        int written = 0, kept = 0, missing = 0, failed = 0;
        Tally tally = new Tally();

        for (Episode ep : episodes) {
            Path out = ep.dir.resolve("transcript.md");
            if (Files.exists(out) && !force) {
                // Idempotent, and protective: a transcript that has been corrected
                // by hand must not be replaced by the machine one on the next run.
                kept++;
                continue;
            }

            Path vtt = cachedVtt(ep.videoId);
            if (vtt == null && !noDownload) {
                if (!download(ep.videoId)) { failed++; System.out.println("  ! " + ep.slug + ": download failed"); continue; }
                vtt = cachedVtt(ep.videoId);
            }
            if (vtt == null) {
                missing++;
                System.out.println("  ? " + ep.slug + ": no captions available (" + ep.videoId + ")");
                continue;
            }

            String markdown = toMarkdown(Files.readString(vtt, StandardCharsets.UTF_8), ep, tally);
            if (markdown == null) {
                missing++;
                System.out.println("  ? " + ep.slug + ": caption file held no usable text");
                continue;
            }
            if (dryRun) {
                System.out.println("  . " + ep.slug + ": would write " + (markdown.length() / 1024) + " KB");
            } else {
                Files.writeString(out, markdown, StandardCharsets.UTF_8);
                System.out.println("  + " + ep.slug + ": " + (markdown.length() / 1024) + " KB");
            }
            written++;
        }

        System.out.println();
        System.out.println((dryRun ? "Would write " : "Wrote ") + written + " transcript(s); "
                + kept + " already present (--force to replace); "
                + missing + " without captions; " + failed + " failed.");
        tally.print();
    }

    /* ------------------------------------------------------------------ discovery */

    record Episode(String slug, String title, String videoId, Path dir, Path index) {}

    /** Every post in the "Podcast" category that embeds a video. The category is
     *  the definition of what an episode is -- the same one the site's own
     *  listing uses -- rather than a slug pattern, which would miss the episodes
     *  whose slug is a title instead of `foojay-podcast-N`. */
    static List<Episode> discover() throws IOException {
        List<Episode> found = new ArrayList<>();
        List<String> noVideo = new ArrayList<>();
        if (!Files.isDirectory(POSTS)) return found;
        try (Stream<Path> files = Files.walk(POSTS)) {
            for (Path index : files.filter(p -> p.getFileName().toString().equals("index.md")).sorted().toList()) {
                String text = Files.readString(index, StandardCharsets.UTF_8);
                int end = text.indexOf("\n---", 3);
                if (end < 0) continue;
                String frontmatter = text.substring(0, end);
                if (!PODCAST_CATEGORY.matcher(frontmatter).find()) continue;

                String slug = index.getParent().getFileName().toString();
                Matcher title = TITLE.matcher(frontmatter);
                Matcher yt = YOUTUBE.matcher(text.substring(end));
                String id = null;
                if (yt.find()) {
                    Matcher m = VIDEO_ID.matcher(yt.group(1));
                    if (m.find()) id = m.group();
                }
                if (id == null) { noVideo.add(slug); continue; }
                found.add(new Episode(slug, title.find() ? title.group(1) : slug, id, index.getParent(), index));
            }
        }
        // Reported rather than passed over: an episode with no video is either
        // audio-only somewhere else or a post that carries the category by
        // mistake, and both are things a human should look at once.
        if (!noVideo.isEmpty()) {
            System.out.println("No video embed, so no transcript: " + String.join(", ", noVideo));
        }
        return found;
    }

    /* ------------------------------------------------------------------ fetching */

    /** `en-orig` is the track the recognition actually produced; `en` can be a
     *  translation of it. Prefer the original, fall back to whatever is there. */
    static Path cachedVtt(String videoId) throws IOException {
        for (String suffix : new String[]{".en-orig.vtt", ".en.vtt"}) {
            Path p = CACHE.resolve(videoId + suffix);
            if (Files.exists(p) && Files.size(p) > 0) return p;
        }
        try (Stream<Path> files = Files.list(CACHE)) {
            return files.filter(p -> p.getFileName().toString().startsWith(videoId + "."))
                    .filter(p -> p.toString().endsWith(".vtt")).findFirst().orElse(null);
        }
    }

    static boolean download(String videoId) {
        try {
            Process p = new ProcessBuilder("yt-dlp",
                    "--skip-download", "--write-auto-subs",
                    "--sub-langs", "en-orig,en", "--sub-format", "vtt",
                    // One request at a time with a pause, and identified as
                    // yt-dlp rather than dressed up as a browser: this is
                    // foojay's own channel, and there is no reason to hammer it.
                    "--sleep-requests", "1",
                    "-o", CACHE.resolve("%(id)s.%(ext)s").toString(),
                    "--quiet", "--no-warnings",
                    "https://www.youtube.com/watch?v=" + videoId)
                    .redirectErrorStream(true).start();
            String out = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            if (!p.waitFor(180, TimeUnit.SECONDS)) { p.destroyForcibly(); return false; }
            if (p.exitValue() != 0 && !out.isBlank()) System.out.println("    yt-dlp: " + out.strip());
            return p.exitValue() == 0;
        } catch (Exception e) {
            System.out.println("    yt-dlp: " + e.getMessage()
                    + " (install it with `brew install yt-dlp`)");
            return false;
        }
    }

    /* ------------------------------------------------------------------ conversion */

    static final Pattern CUE_TIME = Pattern.compile("^(\\d\\d):(\\d\\d):(\\d\\d[.,]\\d+)\\s+-->");
    static final Pattern INLINE_TAG = Pattern.compile("<[^>]*>");

    /** Sound annotations. [music] and [singing] are the jingle, i.e. noise in a
     *  READING transcript, and they land in the middle of sentences. [laughter]
     *  and [applause] stay: they are part of a conversation and a reader who
     *  cannot hear the audio is exactly who they are for. */
    static final Pattern DROP_SOUNDS = Pattern.compile("(?i)\\s*\\[\\s*(music|singing|sound|silence)\\s*]\\s*");

    /** Fillers, removed as whole words only. `like`, `you know`, `I mean` and
     *  `right?` are deliberately NOT here: they are meaning-bearing often enough
     *  that stripping them rewrites what someone said rather than tidying it. */
    static final Pattern FILLERS = Pattern.compile("(?i)(?<![\\w'-])(uh+|um+|erm+|mm+|hmm+)(?![\\w'-])[,]?");

    /** Stutters: an immediately repeated word. Restricted to a closed list of
     *  function words, which is where speech repetition actually happens -- an
     *  unrestricted rule eats legitimate English ("very very", "had had",
     *  "that that" in "I know that that works"). Even here it is a judgement
     *  call, which is why the count is reported at the end of a run. */
    static final String STUTTER_WORDS =
            "i|it|is|a|an|the|and|to|of|we|you|they|he|she|in|on|at|so|but|or|"
            + "this|that|there|then|was|were|will|can|do|does|did|just|with|for|from|"
            + "what|when|where|which|who|how|why|my|your|our|their|its|be|been|am|are";
    static final Pattern STUTTER = Pattern.compile("(?i)(?<![\\w'-])(" + STUTTER_WORDS + ")(\\s+\\1)+(?![\\w'-])");

    /** A repeated two-word false start: "I think I think", "and then and then". */
    static final Pattern FALSE_START = Pattern.compile("(?i)(?<![\\w'-])((?:\\w+)\\s+(?:\\w+))\\s+\\1(?![\\w'-])");

    /**
     * VOCABULARY. Every entry here is a spelling speech recognition produces for
     * a word this podcast says constantly, and every one was taken from the
     * FREQUENCY REPORT over the real caption files (`--report-variants`), not
     * from imagination -- which is why "Foojay" has as many spellings as it does.
     *
     * The rule for adding one: it must be a term the show uses in nearly every
     * episode, and the wrong spelling must not be a word that could legitimately
     * appear in a Java conversation. That is why PEOPLE'S NAMES ARE NOT HERE.
     * Recognition mangles them worse than anything else, but a name has no
     * "obviously intended" spelling a script can know, and inventing one puts
     * words in a guest's mouth. Those stay wrong until a human fixes them, which
     * is the honest failure mode.
     */
    static final LinkedHashMap<Pattern, String> VOCABULARY = new LinkedHashMap<>();
    /** A term, bounded so it cannot fire inside another word. The trailing guard
     *  allows an apostrophe -- "Fuj's Discord API" has to become "Foojay's" --
     *  while both guards still exclude a HYPHEN, which is what keeps the real URL
     *  `bit.ly/join-fuj-slack` in one piece. Found the hard way: both showed up
     *  in the leftovers after the first pass over the archive. */
    static void vocab(String regex, String replacement) {
        VOCABULARY.put(Pattern.compile("(?i)(?<![\\w'-])(?:" + regex + ")(?![\\w-])"), replacement);
    }
    static {
        // ORDER MATTERS: longest and most specific first, because these run in
        // sequence over the same string ("open jdk" has to win before "jdk" is
        // considered, and "log forj" before anything looks at "forj").

        // --- the "4j" family -------------------------------------------------
        // "forj" is what recognition makes of "4j", NOT of Foojay -- the report
        // showed it only ever next to Neo, Log, SLF and LangChain. Mapping it to
        // Foojay, which is what its spelling suggests, would have turned every
        // mention of Log4j in the archive into a mention of the site.
        vocab("(?:lang|line|long)\\s*(?:chain)?\\s*(?:4j|forj)|longchain\\s*(?:4j|forj)|link\\s*ch\\s*forj", "LangChain4j");
        vocab("neo\\s*(?:4j|forj)", "Neo4j");
        vocab("log\\s*(?:4j|forj)|lo\\s+forj|lock\\s+forj", "Log4j");
        vocab("slf\\s*(?:4j|forj)", "SLF4J");
        // Pi4J comes out as "pi forj", "py forj" and "P forj". Frank maintains it,
        // so it comes up constantly and none of those spellings is a word.
        vocab("p[iy]?\\s*(?:4j|forj)", "Pi4J");

        // --- the show's own name ---------------------------------------------
        // 450 occurrences across the archive and four spellings, none of which is
        // a word that can appear in a Java conversation by accident.
        vocab("fuj|fuji|fuja|fujay|fujjay|fuje|fujo|fooj|foojay", "Foojay");
        // NOT here on purpose: "fujio", "fujito" and "fujia", which the archive
        // also uses for Foojay (4 occurrences, each checked by hand -- "So Fujia
        // stands for friends" is unmistakable). They are left to a human because
        // they are also real Japanese names -- Fujio Masuoka invented flash
        // memory, and a guest may well say so one day. The rule against
        // correcting names is worth more than four occurrences.
        //
        // Three of those four ARE Foojay and were corrected by hand in episodes
        // 10 and 70 (the fourth, in episode 15, is a real person). A hand fix
        // lives in the transcript file, so `--force` reverts it -- which is the
        // documented trade for being able to regenerate the archive at all.

        // --- project names the show says constantly ---------------------------
        // "open jdk" outnumbers "openjdk" ten to one in the raw captions, and the
        // project's name is one word.
        vocab("open\\s+jdk|openjdk", "OpenJDK");
        vocab("java\\s*fx|javafx", "JavaFX");
        // Quarkus, 28 wrong spellings against 187 right ones. "caucus" is the
        // one entry here that IS an ordinary English word, so it was checked
        // rather than assumed: all 9 occurrences in the archive are the
        // framework ("Helon and Caucus", "Caucus Corkus itself"), and a
        // political caucus on this show is not a thing that is going to happen.
        vocab("quirkus|quarkas|caucus|corkus", "Quarkus");
        vocab("graal\\s*vm|graalvm", "GraalVM");

        // --- acronyms, which recognition writes in lower case about as often as
        // not (jdk 716 / JDK 224, jvm 186 / JVM 150) -------------------------
        vocab("jdk", "JDK");
        vocab("jvm", "JVM");
        vocab("jep", "JEP");
    }

    static String toMarkdown(String vtt, Episode ep, Tally tally) {
        List<String> lines = new ArrayList<>();
        List<Integer> starts = new ArrayList<>();

        String previous = null;
        int cueStart = 0;
        for (String block : vtt.split("\\R\\R+")) {
            String[] rows = block.split("\\R");
            int timeRow = -1;
            for (int i = 0; i < rows.length; i++) {
                Matcher m = CUE_TIME.matcher(rows[i].strip());
                if (m.find()) {
                    cueStart = Integer.parseInt(m.group(1)) * 3600 + Integer.parseInt(m.group(2)) * 60
                            + (int) Double.parseDouble(m.group(3).replace(',', '.'));
                    timeRow = i;
                    break;
                }
            }
            if (timeRow < 0) continue;

            for (int i = timeRow + 1; i < rows.length; i++) {
                String text = unescape(INLINE_TAG.matcher(rows[i]).replaceAll("")).strip();
                if (text.isEmpty()) continue;
                // YouTube's rolling captions repeat the settled line in the next
                // cue, so the same sentence arrives two or three times. Dropping a
                // line identical to the one before it is what collapses that.
                if (text.equals(previous)) continue;
                previous = text;
                lines.add(text);
                starts.add(cueStart);
            }
        }
        if (lines.isEmpty()) return null;

        StringBuilder body = new StringBuilder();
        StringBuilder paragraph = new StringBuilder();
        int paragraphStart = starts.get(0);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            boolean speakerChange = line.startsWith(">>");
            if (speakerChange) line = line.replaceFirst("^>+\\s*", "");

            if (speakerChange && paragraph.length() > 0) {
                appendParagraph(body, paragraphStart, paragraph.toString(), tally);
                paragraph.setLength(0);
                paragraphStart = starts.get(i);
            }
            if (paragraph.length() > 0) paragraph.append(' ');
            paragraph.append(line);

            // A paragraph ends at the first sentence boundary past ~700
            // characters -- long enough to keep a thought together, short enough
            // that the timestamps stay useful for finding a spot in the audio.
            //
            // THE SECOND CONDITION IS NOT A SAFETY NET, it is the common case.
            // Recognition punctuates some episodes and not others, and with a
            // sentence boundary as the only place to break, 66 of the 99
            // transcripts came out as ONE paragraph of up to 70 KB -- unreadable,
            // and with a single timestamp at the top it is unnavigable too. Past
            // 1100 characters the break happens at the next caption line whether
            // or not anything ended a sentence. An unpunctuated episode still
            // reads as a run-on; it is at least a run-on in chunks that can be
            // scanned and pointed at.
            boolean sentenceEnd = line.matches(".*[.!?][\"')]?$");
            if ((paragraph.length() > 700 && sentenceEnd) || paragraph.length() > 1100) {
                appendParagraph(body, paragraphStart, paragraph.toString(), tally);
                paragraph.setLength(0);
                paragraphStart = i + 1 < starts.size() ? starts.get(i + 1) : paragraphStart;
            }
        }
        if (paragraph.length() > 0) appendParagraph(body, paragraphStart, paragraph.toString(), tally);

        if (body.isEmpty()) return null;
        return body.toString().stripTrailing() + "\n";
    }

    static void appendParagraph(StringBuilder body, int startSeconds, String text, Tally tally) {
        String cleaned = clean(text, tally);
        if (cleaned.isBlank()) return;
        if (body.length() > 0) body.append("\n\n");
        body.append("**[").append(timestamp(startSeconds)).append("]** ").append(cleaned);
    }

    /** The readability pass, in the order the steps have to run: sounds out
     *  first (they sit inside sentences), then fillers, then the repetitions the
     *  first two passes can expose ("the uh the" only becomes "the the" once the
     *  filler is gone), then vocabulary, then whitespace and punctuation left
     *  behind by the removals. */
    static String clean(String text, Tally tally) {
        String out = DROP_SOUNDS.matcher(text).replaceAll(" ");
        tally.sounds += count(DROP_SOUNDS, text);

        tally.fillers += count(FILLERS, out);
        out = FILLERS.matcher(out).replaceAll("");

        tally.stutters += count(STUTTER, out);
        out = STUTTER.matcher(out).replaceAll("$1");

        tally.falseStarts += count(FALSE_START, out);
        out = FALSE_START.matcher(out).replaceAll("$1");

        for (Map.Entry<Pattern, String> fix : VOCABULARY.entrySet()) {
            int n = count(fix.getKey(), out);
            if (n > 0) {
                tally.vocabulary += n;
                tally.vocabularyByTerm.merge(fix.getValue(), n, Integer::sum);
                out = fix.getKey().matcher(out).replaceAll(Matcher.quoteReplacement(fix.getValue()));
            }
        }

        out = out.replaceAll("\\s+", " ")
                 .replaceAll("\\s+([,.!?;:])", "$1")
                 .replaceAll("([,.!?;:]){2,}", "$1")
                 .replaceAll("(?<=[.!?])\\s*,", "")
                 .strip();
        // A paragraph that begins with a comma or a lower-case leftover reads as
        // a fragment; recapitalise the first letter, which the removals above are
        // the only thing that can have cost it.
        out = out.replaceFirst("^[,;:]\\s*", "");
        if (!out.isEmpty() && Character.isLowerCase(out.charAt(0))) {
            out = Character.toUpperCase(out.charAt(0)) + out.substring(1);
        }
        return out;
    }

    static int count(Pattern p, String s) {
        Matcher m = p.matcher(s);
        int n = 0;
        while (m.find()) n++;
        return n;
    }

    static String timestamp(int seconds) {
        int h = seconds / 3600, m = (seconds % 3600) / 60, s = seconds % 60;
        return h > 0 ? String.format("%d:%02d:%02d", h, m, s) : String.format("%d:%02d", m, s);
    }

    static String unescape(String s) {
        return s.replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">")
                .replace("&quot;", "\"").replace("&#39;", "'").replace("&nbsp;", " ");
    }

    /* ------------------------------------------------------------------ variants */

    /** Words that could be a mangled "Foojay". Broad on purpose: the point of the
     *  report is to SEE what recognition actually produced before deciding what
     *  to correct, so it over-collects and a human reads the list. */
    static final Pattern FOOJAY_ISH = Pattern.compile(
            "(?i)(?<![\\w'-])(f[ouwe][a-z]{1,12})(?![\\w'-])");

    /** Ordinary English that starts the same way, excluded so the report is
     *  short enough to actually read. */
    static final Set<String> COMMON = new HashSet<>(Arrays.asList(
            "for", "from", "four", "found", "focus", "focused", "full", "fully", "fun", "funny",
            "function", "functions", "functional", "future", "fourth", "forward", "forget",
            "forgot", "form", "forms", "format", "formats", "former", "fourteen", "forty",
            "food", "foot", "football", "fought", "foundation", "founder", "fond", "force",
            "forced", "forces", "few", "fewer", "feel", "feels", "feeling", "felt", "fell",
            "fed", "feed", "feedback", "fetch", "female", "fest", "festival", "feature",
            "features", "featured", "february", "federal", "fewest", "foreign", "forever",
            "followed", "follow", "following", "follows", "folks", "fourty", "fox", "foxes",
            "fuel", "fundamental", "fundamentally", "funding", "furthermore", "further",
            "fortunately", "fortune", "focusing", "forum", "forums", "founding", "fossil"));

    static void reportVariants(List<Episode> episodes) throws IOException {
        Map<String, Integer> counts = new TreeMap<>();
        Map<String, Set<String>> episodesByToken = new TreeMap<>();
        int scanned = 0;

        for (Episode ep : episodes) {
            Path vtt = cachedVtt(ep.videoId);
            if (vtt == null) continue;
            scanned++;
            String text = unescape(INLINE_TAG.matcher(Files.readString(vtt, StandardCharsets.UTF_8)).replaceAll(""));
            Matcher m = FOOJAY_ISH.matcher(text);
            while (m.find()) {
                String token = m.group(1);
                if (COMMON.contains(token.toLowerCase())) continue;
                String key = token.toLowerCase();
                counts.merge(key, 1, Integer::sum);
                episodesByToken.computeIfAbsent(key, k -> new TreeSet<>()).add(ep.slug);
            }
        }

        System.out.println("Scanned " + scanned + " cached caption file(s).");
        System.out.println();
        System.out.printf("%-8s %-6s %s%n", "COUNT", "EPS", "TOKEN");
        counts.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(80)
                .forEach(e -> System.out.printf("%-8d %-6d %s%n",
                        e.getValue(), episodesByToken.get(e.getKey()).size(), e.getKey()));
        System.out.println();
        System.out.println("Add the ones that are obviously the show's own vocabulary to VOCABULARY,"
                + " and leave anything that could be an ordinary word alone.");
    }

    /* ------------------------------------------------------------------ plumbing */

    static class Tally {
        int sounds, fillers, stutters, falseStarts, vocabulary;
        Map<String, Integer> vocabularyByTerm = new TreeMap<>();

        void print() {
            if (sounds + fillers + stutters + falseStarts + vocabulary == 0) return;
            System.out.println("Cleanup: " + sounds + " sound marker(s), " + fillers + " filler(s), "
                    + stutters + " stutter(s), " + falseStarts + " repeated phrase(s), "
                    + vocabulary + " vocabulary fix(es).");
            if (!vocabularyByTerm.isEmpty()) {
                System.out.println("  " + vocabularyByTerm);
            }
        }
    }

    static String valueOf(List<String> argv, String flag) {
        int i = argv.indexOf(flag);
        return (i >= 0 && i + 1 < argv.size()) ? argv.get(i + 1) : null;
    }
}
