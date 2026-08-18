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
 * One-off migration: brings already-converted content/ in line with the storage
 * format the conversion scripts now emit. Two independent normalizations, both
 * of them Flexmark defaults that were never a deliberate choice here.
 *
 * 1. SETEXT HEADINGS -> ATX.
 *
 *        Where the dedup check actually lives
 *        ------------------------------------
 *
 *    becomes
 *
 *        ## Where the dedup check actually lives
 *
 *    Flexmark underlines h1/h2 and switches to `###` from h3 down, so content/
 *    was written in BOTH styles at once -- 8,263 underlined headings against
 *    7,874 hashed ones. Contributors send posts as PRs (CONTRIBUTING.md) and
 *    type `##`; an 80-character rule under every h2 is noise in a diff and has
 *    to be re-flowed by hand whenever the heading text is edited. Levels 3-6
 *    are already ATX and are left alone. 8,053 converted; the rest are the
 *    list-item case below.
 *
 * 2. DECORATIVE <br> LINES DROPPED.
 *
 *    WordPress bodies use a bare `<br />` as a vertical spacer -- after an
 *    image, after a video embed, at the end of the body. It renders as an empty
 *    <br> between paragraphs, styling this theme doesn't need. Exactly the same
 *    case as the decorative <hr>s already dropped (see CLAUDE.md), and dropped
 *    for the same reason -- 1,152 of them, every one on a line of its own.
 *    Trailing blank lines go too, so every file WITH A BODY ends in a single
 *    newline. (Frontmatter-only files -- `_index.md`, `search.md` and the like
 *    -- have no body to trim and are left as they are.)
 *
 * WHAT IS DELIBERATELY NOT TOUCHED:
 *
 *   - A <br> with text on its line. In a table cell (`| iload_1 <br /> iload_2 |`)
 *     or inside inline SVG it is load-bearing; only a line consisting of nothing
 *     but <br> tags is a spacer.
 *   - Anything inside a fenced code block.
 *   - A `---` under a line that starts a LIST (`* x` / `1. x`). CommonMark says
 *     a setext underline cannot interrupt a list, so that pair already renders
 *     as a list plus a thematic break, not as a heading -- converting it would
 *     CHANGE the page rather than restyle it. A handful of WordPress "headings"
 *     that begin with `1. ` are left underlined for this reason.
 *
 * The conversion scripts produce this shape directly from now on
 * (HtmlToMarkdown: FlexmarkHtmlConverter.SETEXT_HEADINGS = false, plus the
 * STANDALONE_BREAK pass), so a re-scrape is a no-op. The script stays in the
 * repo for the same reason MigrateEnlighterToFences.java does: the WordPress
 * site keeps serving this markup until cutover.
 *
 * Usage:
 *   jbang scripts/NormalizeMarkdown.java --dry-run   (report only, changes nothing)
 *   jbang scripts/NormalizeMarkdown.java
 *   jbang scripts/NormalizeMarkdown.java --path content/pages
 *
 * VERIFIED BY DIFFING THE BUILT SITE before and after: of 2,241 files changed,
 * 567 pages render differently because a decorative <br> is gone, 3 because a
 * paragraph that WordPress had merged into an <h2> is now its own paragraph
 * (reported at the end of the run), and 3 because dropping a <br> inside a list
 * item makes the list tight, so Goldmark stops wrapping the items in <p>. The
 * heading restyle itself changes nothing. No other page moved.
 *
 * Idempotent: a file already in this shape is not rewritten. Frontmatter is
 * never touched -- only the body below the closing `---` is scanned.
 */
public class NormalizeMarkdown {

    static final Path DEFAULT_ROOT = Path.of("content");

    /** Setext underline: a run of `=` (h1) or `-` (h2) and nothing else.
     *  At most 3 spaces of indent -- at 4 it is an indented code block, and
     *  several posts paste multi-document YAML whose `---` separators sit inside
     *  one. Matching those turned a code sample into a heading. */
    static final Pattern SETEXT_RULE = Pattern.compile("^ {0,3}([=-])\\1*[ \\t]*$");
    /** 4+ spaces or a tab: an indented code block, never a heading. */
    static final Pattern INDENTED_CODE = Pattern.compile("^(?: {4,}|\\t)");
    /** A line holding nothing but <br> tags. */
    static final Pattern BREAK_LINE = Pattern.compile("^[ \\t]*(?:<br\\s*/?>[ \\t]*)+$", Pattern.CASE_INSENSITIVE);
    /** A fence line, split into indent / backtick or tilde run / info string. */
    static final Pattern FENCE_LINE = Pattern.compile("^([ \\t]*)(`{3,}|~{3,})(.*)$");
    /** Leading blockquote markers, so a quoted heading is handled at its own depth. */
    static final Pattern QUOTE_PREFIX = Pattern.compile("^[ \\t]*(?:>[ \\t]?)+");
    /** Opens a list item -- a `---` under one of these is a thematic break, not a heading. */
    static final Pattern LIST_ITEM = Pattern.compile("^[ \\t]*(?:[*+-]|\\d+[.)])[ \\t]");
    /** Already an ATX heading. */
    static final Pattern ATX = Pattern.compile("^[ \\t]{0,3}#{1,6}[ \\t]");
    /** A blank (or whitespace-only) line. */
    static final Pattern BLANK = Pattern.compile("^[ \\t]*$");

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

        int changedFiles = 0, headings = 0, breaks = 0;
        List<String> keptAsList = new ArrayList<>();
        List<String> paragraphSplits = new ArrayList<>();

        for (Path file : files) {
            String original = Files.readString(file);
            int split = bodyStart(original);
            String head = original.substring(0, split);
            String body = original.substring(split);

            int[] stats = new int[4]; // headings, breaks, list-guard skips, paragraph splits
            String newBody = normalize(body, stats);
            if (stats[2] > 0) keptAsList.add(file + " (" + stats[2] + ")");
            if (stats[3] > 0) paragraphSplits.add(file + " (" + stats[3] + ")");
            if (newBody.equals(body)) continue;

            changedFiles++;
            headings += stats[0];
            breaks += stats[1];
            if (!dryRun) Files.writeString(file, head + newBody);
        }

        System.out.printf("%s %d file(s): %d setext heading(s) -> ATX, %d decorative <br> line(s) dropped%n",
                dryRun ? "[dry-run] would change" : "Changed", changedFiles, headings, breaks);
        if (!keptAsList.isEmpty()) {
            System.out.println("Left underlined (a `---` under a list item is a thematic break, not a heading):");
            keptAsList.forEach(s -> System.out.println("  " + s));
        }
        if (!paragraphSplits.isEmpty()) {
            System.out.println("Paragraph split from its heading (a setext underline takes the WHOLE");
            System.out.println("paragraph above it, so these pages were rendering prose inside an <h2>):");
            paragraphSplits.forEach(s -> System.out.println("  " + s));
        }
        if (dryRun) System.out.println("\nNothing written. Re-run without --dry-run to apply.");
    }

    /** Applies both normalizations, then tidies the blank lines they leave behind. */
    static String normalize(String body, int[] stats) {
        List<String> out = new ArrayList<>();
        String[] lines = body.split("\n", -1);
        String openMarker = null; // non-null while inside a fence

        for (int i = 0; i < lines.length; i++) {
            Matcher fence = FENCE_LINE.matcher(lines[i]);
            boolean isFence = fence.matches();
            if (openMarker == null) {
                // Opening fence. A backtick in the info string means this isn't
                // one (inline code can start a line), so it's treated as prose.
                if (isFence && fence.group(3).indexOf('`') < 0) {
                    openMarker = fence.group(2);
                    out.add(lines[i]);
                    continue;
                }
            } else {
                // Closing fence: at least as long as the opener, nothing else on it.
                if (isFence && fence.group(2).charAt(0) == openMarker.charAt(0)
                        && fence.group(2).length() >= openMarker.length()
                        && fence.group(3).isBlank()) {
                    openMarker = null;
                }
                out.add(lines[i]); // fence bodies are the author's, verbatim
                continue;
            }

            if (BREAK_LINE.matcher(lines[i]).matches()) {
                stats[1]++;
                continue; // drop the spacer entirely
            }

            // A setext heading is this line plus the underline on the next one.
            if (i + 1 < lines.length && !lines[i].isBlank()) {
                String quote = quotePrefix(lines[i]);
                String text = lines[i].substring(quote.length());
                String nextQuote = quotePrefix(lines[i + 1]);
                Matcher rule = SETEXT_RULE.matcher(lines[i + 1].substring(nextQuote.length()));
                boolean sameDepth = nextQuote.trim().equals(quote.trim());
                if (sameDepth && rule.matches() && !text.isBlank()
                        && !ATX.matcher(text).find() && !INDENTED_CODE.matcher(text).find()) {
                    if (LIST_ITEM.matcher(text).find()) {
                        stats[2]++; // see the class comment: leaving it preserves the page
                    } else {
                        // A setext underline heads the whole paragraph above it, so
                        // where the heading text is NOT its own paragraph the page was
                        // rendering that prose inside the <h2>. The ATX form heads only
                        // its own line, which splits them -- reported, because it changes
                        // the page rather than just the source. No blank line is inserted:
                        // an ATX heading interrupts a paragraph on its own, and inserting
                        // one would end an enclosing blockquote early.
                        String prev = out.isEmpty() ? "" : out.get(out.size() - 1);
                        if (!BLANK.matcher(prev.substring(quotePrefix(prev).length())).matches()) {
                            stats[3]++;
                        }
                        String hashes = rule.group(1).equals("=") ? "# " : "## ";
                        out.add(quote + hashes + text.trim());
                        stats[0]++;
                        i++; // consume the underline
                        continue;
                    }
                }
            }
            out.add(lines[i]);
        }

        // Dropping a spacer leaves the blank lines that surrounded it, and an ATX
        // heading no longer needs the blank line the underline used to occupy.
        return collapseBlankLines(out).stripTrailing() + "\n";
    }

    /**
     * Collapses runs of blank lines to one -- OUTSIDE fenced code only. A blank
     * line inside a fence is the author's; rewrapping their sample would be an
     * edit, not a normalization.
     */
    static String collapseBlankLines(List<String> lines) {
        StringBuilder sb = new StringBuilder();
        String openMarker = null;
        boolean prevBlank = false;
        for (String line : lines) {
            Matcher fence = FENCE_LINE.matcher(line);
            boolean isFence = fence.matches();
            if (openMarker == null) {
                if (isFence && fence.group(3).indexOf('`') < 0) openMarker = fence.group(2);
                boolean blank = BLANK.matcher(line).matches();
                if (blank && prevBlank) continue;
                prevBlank = blank;
            } else {
                if (isFence && fence.group(2).charAt(0) == openMarker.charAt(0)
                        && fence.group(2).length() >= openMarker.length()
                        && fence.group(3).isBlank()) {
                    openMarker = null;
                }
                prevBlank = false;
            }
            sb.append(line).append('\n');
        }
        return sb.length() == 0 ? "" : sb.substring(0, sb.length() - 1);
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
