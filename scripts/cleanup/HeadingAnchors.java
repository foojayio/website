///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 17+

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * One-off migration: removes the WordPress heading anchors from content/.
 *
 *     Where the dedup check actually lives {#h2-2-where-the-dedup-check-actually-lives}
 *     ------------------------------------------------------------------------------
 *
 * becomes
 *
 *     Where the dedup check actually lives
 *     ------------------------------------
 *
 * WHERE THEY CAME FROM. They are not something the conversion invented. The
 * live WordPress site stamps every heading with one --
 * `<h2 class="wp-block-heading" id="h2-2-where-the-dedup-check-actually-lives">`
 * -- and Flexmark carries an id attribute over as Markdown attribute syntax
 * (`{#id}`), which Goldmark then reads back and applies. The round trip works;
 * that is why nobody noticed.
 *
 * WHY REMOVE THEM. Three reasons, none of them cosmetic-only:
 *
 *   1. They are positional. The number is the heading's INDEX in the document,
 *      so inserting a new H2 leaves `h2-3-...` sitting above `h2-2-...`. Any
 *      editing of a post makes them progressively more misleading.
 *   2. A good few are corrupt at the source. WordPress's slugifier eats
 *      leading capitals: "Podcast Apps" is stamped `h2-1--odcast-pps`, and
 *      foojay.io really does serve that id today. Carrying it forward carries
 *      the bug forward.
 *   3. Posts arrive as pull requests (CONTRIBUTING.md). A contributor writing a
 *      new post has no reason to type one, so every post gets one only for as
 *      long as a scraper is putting it there -- meaning content would be
 *      permanently split between two conventions.
 *
 * WHAT IT COSTS. Section-level deep links minted before cutover break: an
 * inbound `...#h2-2-where-the-dedup-check-actually-lives` will land at the top
 * of the post rather than at that heading. Post-level URLs are untouched (this
 * changes no path, no alias and no frontmatter), and Hugo still generates an id
 * for every heading from its text, so the "On this page" panel, its scroll-spy
 * (static/js/toc.js) and any NEW deep link all keep working. Accepted
 * deliberately: fragment links into a blog post are rare next to the standing
 * cost of the two conventions above.
 *
 * The conversion scripts drop heading ids from now on
 * (HtmlToMarkdown.toMarkdown), so a re-scrape of the still-live WordPress site
 * produces the same shape and this script becomes a no-op. It stays in the repo
 * for the same reason cleanup/EnlighterToFences.java does: WordPress keeps
 * serving the anchors until cutover, so a late re-scrape through an older
 * checkout can reintroduce them.
 *
 * Usage:
 *   jbang scripts/cleanup/HeadingAnchors.java --dry-run   (report only, changes nothing)
 *   jbang scripts/cleanup/HeadingAnchors.java
 *   jbang scripts/cleanup/HeadingAnchors.java --path content/pages
 *
 * Idempotent: a file with no anchors left is not rewritten. Frontmatter is never
 * touched -- only the body below the closing `---` is scanned -- and neither are
 * fenced code blocks, where `{#...}` is somebody's CSS or shell parameter
 * expansion rather than a heading anchor.
 */
public class HeadingAnchors {

    static final Path DEFAULT_ROOT = Path.of("content");

    /** Trailing `{#anchor}` on a heading line, with the whitespace before it. */
    static final Pattern ANCHOR = Pattern.compile("[ \\t]*\\{#[^}\\s]*\\}[ \\t]*$");
    /** ATX heading: `## Title`. */
    /**
     * The same WordPress id, carried over onto a LINK rather than a heading:
     * `[Ty Morton](https://.../){#31db}`. Medium-imported posts are full of
     * them (`<a id="31db">`). Unlike the heading case this one does NOT round
     * trip -- Goldmark's attribute syntax only applies to a whole block, so an
     * id sitting mid-paragraph is rendered as the literal text "{#31db}" in the
     * middle of a sentence. Anchored to the link's closing paren so a `{#id}`
     * in a CSS example is never touched.
     */
    static final Pattern LINK_ANCHOR = Pattern.compile("\\)\\{#[^}\\n]*\\}");

    /**
     * The same id again, this time stamped on anything else WordPress felt like
     * -- `<p class="sect0" id="_quarkus_unpacked_insights_from_the_foojay_podcast">`
     * on an Asciidoc-imported post, `<p id="caption-attachment-36528">` under
     * every captioned image, `<span id="more-36262">` where the editor's "read
     * more" break was. Flexmark carries all of them over the same way, and like
     * the link case they do NOT round trip: Goldmark applies an attribute block
     * only to a HEADING here, so on any other line it is rendered as the literal
     * text "{#caption-attachment-36528}" at the end of a paragraph.
     *
     * That this can be stripped by position alone is a measurement, not an
     * assumption. Across content/, every one of the 1293 `{#...}` occurrences
     * outside a code fence sits at the END of its line, and 1293 minus the 13 on
     * a heading line is exactly the 1280 that the built HTML renders as visible
     * text. So "at the end of a line that is not a heading" is precisely the set
     * that is broken, with nothing else caught in it.
     *
     * The no-whitespace rule inside the braces is what keeps Qute and Handlebars
     * out of it: a template tag written in prose is `{#if item.price > 100}` or
     * `{#each rows}`, which never matches. A single-word tag (`{#insert}`) would
     * -- but only if it were at the end of a line and outside backticks, and
     * MID-LINE occurrences, the shape a real template tag in a sentence has, are
     * counted and reported rather than touched. There are none today.
     */
    static final Pattern BODY_ANCHOR = ANCHOR;
    /** A `{#id}` that is NOT at the end of its line: reported, never stripped. */
    static final Pattern MIDLINE_ANCHOR = Pattern.compile("\\{#[^}\\s]*\\}(?=.*\\S)");
    /** Inline code spans, masked before the mid-line search so `{#if}` in
     *  backticks is not reported as a near-miss on every Qute post. */
    static final Pattern CODE_SPAN = Pattern.compile("`[^`\\n]*`");

    static final Pattern ATX = Pattern.compile("^[ \\t]{0,3}#{1,6}[ \\t]");
    /** Setext underline: a run of `=` or `-` and nothing else. */
    static final Pattern SETEXT_RULE = Pattern.compile("^([ \\t]*)([=-])\\2*[ \\t]*$");
    /** A fence line, split into indent / backtick or tilde run / info string. */
    static final Pattern FENCE_LINE = Pattern.compile("^([ \\t]*)(`{3,}|~{3,})(.*)$");
    /** Leading blockquote markers. Several posts quote a whole section, headings
     *  and all, so the heading tests run against the line WITHOUT this prefix and
     *  the prefix is put back afterwards. */
    static final Pattern QUOTE_PREFIX = Pattern.compile("^[ \\t]*(?:>[ \\t]?)+");

    public static void main(String[] args) throws IOException {
        boolean dryRun = false;
        Path root = DEFAULT_ROOT;
        for (int i = 0; i < args.length; i++) {
            if ("--dry-run".equals(args[i])) dryRun = true;
            else if ("--path".equals(args[i]) && i + 1 < args.length) root = Path.of(args[++i]);
        }
        if (!Files.isDirectory(root)) {
            System.err.println("No such directory: " + root);
            System.exit(1);
        }

        List<Path> files;
        try (Stream<Path> s = Files.walk(root)) {
            files = s.filter(p -> p.toString().endsWith(".md")).sorted().toList();
        }

        int changedFiles = 0, anchors = 0, linkAnchors = 0, bodyAnchors = 0;
        List<String> skipped = new ArrayList<>();
        List<String> reported = new ArrayList<>();

        for (Path file : files) {
            String original = Files.readString(file);
            int split = bodyStart(original);
            String head = original.substring(0, split);
            String body = original.substring(split);
            if (!body.contains("{#")) continue;

            int[] count = new int[1];
            int[] linkCount = new int[1];
            int[] bodyCount = new int[1];
            int[] inCode = new int[1];
            List<String> midline = new ArrayList<>();
            String newBody = strip(body, count, linkCount, bodyCount, inCode, midline);
            if (inCode[0] > 0) skipped.add(file + " (" + inCode[0] + " inside code)");
            midline.forEach(l -> reported.add(file + ": " + l));
            if (newBody.equals(body)) continue;

            changedFiles++;
            anchors += count[0];
            linkAnchors += linkCount[0];
            bodyAnchors += bodyCount[0];
            if (!dryRun) Files.writeString(file, head + newBody);
        }

        System.out.printf("%s %d file(s), %d heading anchor(s), %d link anchor(s), %d body anchor(s)%n",
                dryRun ? "[dry-run] would change" : "Changed", changedFiles, anchors, linkAnchors, bodyAnchors);
        if (!skipped.isEmpty()) {
            System.out.println("Left alone (inside a fenced code block):");
            skipped.forEach(s -> System.out.println("  " + s));
        }
        if (!reported.isEmpty()) {
            // A `{#...}` sitting mid-line is the shape a real Qute/Handlebars tag
            // written in a sentence has, and nothing here can tell the two apart --
            // so it is named for a human rather than guessed at. Same posture as
            // fetch/DiscoverJugCalendars.java printing its near-misses.
            System.out.println("Left alone (not at the end of its line -- check by hand):");
            reported.forEach(s2 -> System.out.println("  " + s2));
        }
        if (dryRun) System.out.println("\nNothing written. Re-run without --dry-run to apply.");
    }

    /**
     * Strips the anchor from every heading in the body.
     *
     * A `{#...}` only counts when the line it sits on is actually a heading --
     * ATX (`## Title {#id}`) or the text line of a setext heading (the one with
     * `----` under it, which is what the converter emits for h1/h2). Anywhere
     * else it is left alone, so a `{#id}` in a CSS example in prose survives.
     *
     * A setext underline is resized to the shortened title. Its length carries no
     * meaning in Markdown, but leaving a 79-character rule under a 36-character
     * heading looks like damage to the next person reading the file.
     */
    static String strip(String body, int[] count, int[] linkCount, int[] bodyCount,
                        int[] inCode, List<String> midline) {
        String[] lines = body.split("\n", -1);
        boolean[] drop = new boolean[lines.length];
        String openMarker = null; // non-null while inside a fence
        for (int i = 0; i < lines.length; i++) {
            Matcher fence = FENCE_LINE.matcher(lines[i]);
            boolean isFence = fence.matches();
            if (openMarker == null) {
                // Opening fence. A backtick in the info string means this isn't
                // one (inline code can start a line), so it's treated as prose.
                if (isFence && fence.group(3).indexOf('`') < 0) {
                    openMarker = fence.group(2);
                    continue;
                }
            } else {
                // Closing fence: at least as long as the opener, nothing else on it.
                if (isFence && fence.group(2).length() >= openMarker.length()
                        && fence.group(2).charAt(0) == openMarker.charAt(0)
                        && fence.group(3).isBlank()) {
                    openMarker = null;
                } else if (ANCHOR.matcher(lines[i]).find()) {
                    inCode[0]++;
                }
                continue;
            }

            // Link anchors first: they sit mid-line, so none of the heading
            // logic below applies to them, and a heading line can carry one too.
            Matcher link = LINK_ANCHOR.matcher(lines[i]);
            if (link.find()) {
                int n = 0;
                do { n++; } while (link.find());
                lines[i] = LINK_ANCHOR.matcher(lines[i]).replaceAll(")");
                linkCount[0] += n;
            }

            // Anything left mid-line is NOT ours to touch -- see MIDLINE_ANCHOR.
            String masked = CODE_SPAN.matcher(lines[i]).replaceAll(m2 -> " ".repeat(m2.group().length()));
            Matcher mid = MIDLINE_ANCHOR.matcher(masked);
            while (mid.find()) midline.add("line " + (i + 1) + ": " + lines[i].strip());

            if (!ANCHOR.matcher(lines[i]).find()) continue;

            String quote = quotePrefix(lines[i]);
            String text = lines[i].substring(quote.length());
            Matcher m = ANCHOR.matcher(text);
            if (!m.find()) continue;

            boolean atx = ATX.matcher(text).find();
            // A setext underline has to sit at the same blockquote depth.
            Matcher rule = null;
            if (!atx && i + 1 < lines.length
                    && quotePrefix(lines[i + 1]).trim().equals(quote.trim())) {
                rule = SETEXT_RULE.matcher(lines[i + 1].substring(quotePrefix(lines[i + 1]).length()));
                if (!rule.matches()) rule = null;
            }
            boolean setext = rule != null;
            // Not a heading, so Goldmark never consumes this and the reader sees
            // it. Strip it; if the anchor WAS the line -- a `<span id="more-N">`
            // on its own, a `> {#block-...}` inside a quote -- the line goes with
            // it, or an empty paragraph is left where the anchor used to be.
            if (!atx && !setext) {
                String rest = m.replaceFirst("");
                bodyCount[0]++;
                if (rest.isBlank()) drop[i] = true;
                else lines[i] = quote + rest;
                continue;
            }

            String title = m.replaceFirst("");
            // An anchor-only heading line would leave an empty title; leave it be
            // rather than produce a heading with no text.
            if (title.isBlank()) continue;

            lines[i] = quote + title;
            count[0]++;
            if (setext) {
                char c = rule.group(2).charAt(0);
                lines[i + 1] = quotePrefix(lines[i + 1]) + rule.group(1)
                        + String.valueOf(c).repeat(Math.max(3, title.trim().length()));
            }
        }

        List<String> out = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            if (!drop[i]) { out.add(lines[i]); continue; }
            // The dropped line usually sat alone between two blank lines, so one
            // of those blanks goes too -- otherwise every removal leaves a double
            // blank line behind and the diff is full of whitespace churn.
            boolean prevBlank = !out.isEmpty() && isBlankLine(out.get(out.size() - 1));
            if (prevBlank && i + 1 < lines.length && isBlankLine(lines[i + 1])) i++;
        }
        return String.join("\n", out);
    }

    /** Blank, or blank apart from blockquote markers (`>` on its own). */
    static boolean isBlankLine(String line) {
        return line.substring(quotePrefix(line).length()).isBlank();
    }

    /** The blockquote markers a line opens with, or "" if it isn't quoted. */
    static String quotePrefix(String line) {
        Matcher m = QUOTE_PREFIX.matcher(line);
        return m.find() ? m.group() : "";
    }

    /**
     * Index of the first character after the frontmatter block, so the scan can
     * never touch YAML. Files without frontmatter are treated as all body.
     */
    static int bodyStart(String content) {
        if (!content.startsWith("---")) return 0;
        int end = content.indexOf("\n---", 3);
        if (end < 0) return 0;
        int nl = content.indexOf('\n', end + 1);
        return nl < 0 ? content.length() : nl + 1;
    }
}
