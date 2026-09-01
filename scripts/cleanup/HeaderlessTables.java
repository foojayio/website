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
 * One-off migration: gives every headerless table in content/ the empty header
 * row that makes it a table again.
 *
 *     |------|------|
 *     | 1 | Indicates the configured 30s recording is ongoing. |
 *
 * becomes
 *
 *     |   |   |
 *     |---|---|
 *     | 1 | Indicates the configured 30s recording is ongoing. |
 *
 * WHERE THEY CAME FROM. A GFM table is a header row, then a delimiter row, then
 * the body; there is no way to write one without a header. WordPress has no such
 * rule, and its table block only emits <th> when the author ticked "header
 * section" -- most did not. Handed a <table><tbody><tr><td>, Flexmark emits the
 * delimiter row with nothing above it, which Goldmark does not recognise as a
 * table at all: the reader gets the pipes as literal text, a wall of them where
 * the table should be. 111 tables across 54 posts were live in that state.
 *
 * WHY AN EMPTY HEADER AND NOT THE FIRST ROW. Promoting the first row is the
 * obvious fix and is wrong for most of these. Of the 111, 25 have a first row
 * that really is a header (every cell bold); the other 86 are legends and
 * WordPress note boxes (an empty icon cell, then the note) whose first row is
 * data. Promoting those states something the author did not. An empty header
 * keeps every row a row, and the reader never sees it:
 * layouts/_default/_markup/render-table.html omits the <thead> when every header
 * cell is blank, so the rendered table matches what WordPress serves.
 *
 * shared/HtmlToMarkdown.java now inserts the same empty header at conversion
 * time, so a re-scrape produces this shape and a re-run here is a no-op. The
 * script stays for the same reason cleanup/EnlighterToFences.java does: the WP
 * site keeps serving headerless tables until cutover.
 *
 * Usage:
 *   jbang scripts/cleanup/HeaderlessTables.java --dry-run   (report only, changes nothing)
 *   jbang scripts/cleanup/HeaderlessTables.java
 *   jbang scripts/cleanup/HeaderlessTables.java --path content/pages
 *
 * Idempotent: a delimiter row that already has a header above it is left alone.
 * Frontmatter is never touched, and neither are fenced code blocks -- a row of
 * dashes and pipes is ordinary output in a console transcript.
 */
public class HeaderlessTables {

    static final Path DEFAULT_ROOT = Path.of("content");

    /** A GFM delimiter row: `|---|:---:|---|`, with or without the outer pipes. */
    static final Pattern DELIMITER = Pattern.compile("^\\s*\\|?\\s*:?-{2,}:?\\s*(\\|\\s*:?-{2,}:?\\s*)+\\|?\\s*$");
    /** A fence line, split into indent / backtick or tilde run / info string. */
    static final Pattern FENCE_LINE = Pattern.compile("^([ \\t]*)(`{3,}|~{3,})(.*)$");

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

        int changedFiles = 0, tables = 0;
        for (Path file : files) {
            String original = Files.readString(file);
            int split = bodyStart(original);
            String head = original.substring(0, split);
            String body = original.substring(split);
            if (!body.contains("--")) continue;

            int[] count = new int[1];
            String newBody = addHeaders(body, count);
            if (newBody.equals(body)) continue;

            changedFiles++;
            tables += count[0];
            if (!dryRun) Files.writeString(file, head + newBody);
        }

        System.out.printf("%s %d file(s), %d headerless table(s)%n",
                dryRun ? "[dry-run] would change" : "Changed", changedFiles, tables);
        if (dryRun) System.out.println("\nNothing written. Re-run without --dry-run to apply.");
    }

    /**
     * Inserts an empty header row above every delimiter row that has no header.
     *
     * The header is built to the delimiter's own column count, so a table whose
     * body rows disagree about how many cells they have (WordPress emits those)
     * still parses -- the delimiter is what fixes a GFM table's width.
     */
    static String addHeaders(String body, int[] count) {
        String[] lines = body.split("\n", -1);
        List<String> out = new ArrayList<>(lines.length + 8);
        String openMarker = null; // non-null while inside a fence
        for (int i = 0; i < lines.length; i++) {
            Matcher fence = FENCE_LINE.matcher(lines[i]);
            boolean isFence = fence.matches();
            if (openMarker == null) {
                if (isFence && fence.group(3).indexOf('`') < 0) openMarker = fence.group(2);
            } else {
                if (isFence && fence.group(2).length() >= openMarker.length()
                        && fence.group(2).charAt(0) == openMarker.charAt(0)
                        && fence.group(3).isBlank()) {
                    openMarker = null;
                }
                out.add(lines[i]);
                continue;
            }

            if (openMarker == null && DELIMITER.matcher(lines[i]).matches() && !hasHeaderAbove(out)) {
                out.add(emptyHeaderFor(lines[i]));
                count[0]++;
            }
            out.add(lines[i]);
        }
        return String.join("\n", out);
    }

    /** True when the line already emitted above is a table row rather than blank or prose. */
    static boolean hasHeaderAbove(List<String> out) {
        if (out.isEmpty()) return false;
        String prev = out.get(out.size() - 1);
        return prev.contains("|") && !prev.isBlank();
    }

    /** `|   |   |   |`, one cell per column in the delimiter row. */
    static String emptyHeaderFor(String delimiter) {
        String trimmed = delimiter.strip();
        if (trimmed.startsWith("|")) trimmed = trimmed.substring(1);
        if (trimmed.endsWith("|")) trimmed = trimmed.substring(0, trimmed.length() - 1);
        int columns = trimmed.split("\\|", -1).length;
        return "|" + "   |".repeat(Math.max(1, columns));
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
