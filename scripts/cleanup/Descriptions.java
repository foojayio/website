///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.jsoup:jsoup:1.17.2
//DEPS com.vladsch.flexmark:flexmark-html2md-converter:0.64.8
//SOURCES ../shared/HtmlToMarkdown.java
//JAVA 21+

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * One-off migration: repairs the `description:` frontmatter WordPress handed the
 * scrapers. TWO defects, both Yoast's, both invisible on the page itself and
 * visible in every search result and link preview.
 *
 * (1) RUN-ON SENTENCES.
 *
 * Yoast builds a post's meta description by concatenating the body's text nodes
 * with no separator between them, so a heading runs straight into the paragraph
 * that follows it and the boundary punctuation loses its space:
 *
 *   ...developers do this is using the Service Layer pattern.What you'll learn
 *   ...beyond the limits of a single machine.In this article, we'll walk through:What
 *
 * That string is what a reader sees in a Google result, in a Slack/LinkedIn link
 * preview and inside the BlogPosting JSON-LD, so it is worth fixing even though
 * the page itself renders correctly.
 *
 * The rule lives in HtmlToMarkdown.repairRunOnSentences so this script and the
 * scrapers agree -- transfer/Posts.java and transfer/Sponsors.java apply it to the
 * scraped description, which makes a re-scrape emit what this script wrote and a
 * re-run here a no-op. See that method for the two guards that keep it away from
 * System.Logger, FetchType.EAGER and sun.misc.Unsafe, which have exactly the same
 * shape and are not damage.
 *
 * (2) THE " - by <Author>" TAIL.
 *
 * When Yoast has no description to use it builds one by truncating the body and
 * stamping the byline on the end:
 *
 *   ...copy-pasting code that may or may not still - by Cristobal Escobar
 *
 * 290 of 2148 posts carry it. It restates a byline the search result already
 * shows, and it does so INSIDE the ~155 characters Google renders -- so on a long
 * description the byline is the part that survives and the subject is the part
 * that gets cut. HtmlToMarkdown.stripBylineSuffix removes it and re-terminates
 * the fragment with an ellipsis; transfer/Posts.java applies the same method to
 * the scraped description, so a re-scrape emits this and a re-run here is a
 * no-op.
 *
 * That strip is AUTHOR-AWARE, which is the whole safety story: " - by
 * <Capitalised Words>" is also how a human writes "a new translation of the
 * Odyssey - by Emily Wilson". So the tail is only removed when it names one of
 * the post's OWN credited authors, which is what makes it Yoast's stamp rather
 * than prose. This script resolves each `authors:` slug to its display name via
 * content/authors/<slug>/_index.md -- the same join the theme does. 289 of the
 * 290 match; the one that does not has the byline buried mid-string and is
 * reported instead.
 *
 * SCOPE: the `description:` line only. Bodies are never touched -- a body kept its
 * real markup, so it never lost these spaces, and a blanket pass over prose would
 * hit every legitimately dotted identifier in 2147 articles.
 *
 * WHAT IT DECLINES, AND WHY THAT IS PRINTED. The "starts lowercase" guard also
 * rejects a sentence that legitimately ends on a capitalised word -- "...upgraded
 * with ReadyNow.Azul has developed..." is real damage, and it is spelled exactly
 * like System.Logger. No lexical rule separates those two, and inserting a space
 * into a type name is worse than leaving a space out of a sentence, so the
 * ambiguous ones are reported as "needs a human" rather than guessed at (the same
 * posture fetch/DiscoverJugCalendars.java takes).
 *
 * THREE are reported in content/ today and all three are correct refusals:
 * System.Logger, FetchType.EAGER and "DALL.E API" (a mis-typed DALL-E, not two
 * sentences). The four that WERE real damage -- ReadyNow.Azul, MongoDB.In,
 * "Hibernate API.If", Caching.Now -- were fixed by hand, which is why the report
 * is now clean. An exception list would have let the script fix those four
 * itself, and was rejected deliberately: it rots, the next post to end a sentence
 * on Duration.ZERO gets silently corrupted, and a space inserted into a type name
 * reads as our bug where a missing space reads as WordPress's. So the standing
 * rule is: anything this prints is a human decision, and a non-empty report is
 * not necessarily a problem.
 *
 * Usage:
 *   jbang scripts/cleanup/Descriptions.java                 (rewrite content/)
 *   jbang scripts/cleanup/Descriptions.java --dry-run       (report only)
 *   jbang scripts/cleanup/Descriptions.java --path content/posts/2025
 */
public class Descriptions {

    static boolean dryRun = false;
    static Path root = Path.of("content");

    public static void main(String[] args) throws IOException {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--dry-run" -> dryRun = true;
                case "--path" -> root = Path.of(args[++i]);
                default -> {
                    System.err.println("Unknown argument: " + args[i]);
                    System.exit(2);
                }
            }
        }
        if (!Files.isDirectory(root)) {
            System.err.println("Not a directory: " + root);
            System.exit(2);
        }

        List<Path> files;
        try (Stream<Path> s = Files.walk(root)) {
            files = s.filter(p -> p.getFileName().toString().endsWith(".md")).sorted().toList();
        }

        Map<String, String> authorNames = authorDisplayNames();

        int changed = 0;
        List<String> report = new ArrayList<>();
        List<String> bylines = new ArrayList<>();
        List<String> review = new ArrayList<>();
        List<String> strayBylines = new ArrayList<>();
        for (Path f : files) {
            String text = Files.readString(f);
            String updated = repairFrontmatterDescription(text);
            String afterByline = stripFrontmatterByline(updated != null ? updated : text, authorNames);
            if (afterByline != null) {
                bylines.add(f.toString());
                updated = afterByline;
            }
            if (updated != null) {
                changed++;
                report.add(f.toString());
                if (!dryRun) Files.writeString(f, updated);
            }
            // A " - by " that survived the strip is one the author list could not
            // vouch for -- reported, never guessed at, same posture as the
            // capitalised run-on candidates below.
            String line = descriptionLine(updated != null ? updated : text);
            if (line != null && line.contains(" - by ")) strayBylines.add(f + "\n      " + line.trim());
            // Report what the guards refused, on the post-repair text, so a real
            // fix isn't listed as ambiguous as well.
            for (String snippet : ambiguous(updated != null ? updated : text)) {
                review.add(f + "\n      " + snippet);
            }
        }

        System.out.printf("%s%d of %d files (%d of them a \" - by <Author>\" tail)%n",
                dryRun ? "Would repair " : "Repaired ", changed, files.size(), bylines.size());
        report.forEach(r -> System.out.println("  " + r));

        if (!strayBylines.isEmpty()) {
            System.out.printf("%nNeeds a human -- %d description(s) still holding \" - by \" that names%n"
                    + "nobody this post credits, so it cannot be told apart from prose:%n", strayBylines.size());
            strayBylines.forEach(r -> System.out.println("  " + r));
        }

        if (!review.isEmpty()) {
            System.out.printf("%nNeeds a human -- %d description(s) where the word before the%n"
                    + "punctuation is capitalised, so a sentence boundary (ReadyNow.Azul has)%n"
                    + "cannot be told apart from a type name (System.Logger that):%n", review.size());
            review.forEach(r -> System.out.println("  " + r));
        }
    }

    /**
     * Every author bundle's slug -> display name, read from
     * content/authors/<slug>/_index.md. This is the join the theme does at build
     * time (partials/author-page.html) and the only way to turn a post's
     * `authors:` slugs into the names Yoast's tail actually spells.
     *
     * Read from `root`'s SIBLING content/authors rather than from `root` itself,
     * so `--path content/posts/2025` still resolves names.
     */
    static Map<String, String> authorDisplayNames() throws IOException {
        Path authors = Path.of("content", "authors");
        Map<String, String> out = new LinkedHashMap<>();
        if (!Files.isDirectory(authors)) return out;
        try (Stream<Path> s = Files.list(authors)) {
            for (Path dir : s.filter(Files::isDirectory).toList()) {
                Path index = dir.resolve("_index.md");
                if (!Files.isRegularFile(index)) continue;
                Matcher m = TITLE_LINE.matcher(Files.readString(index));
                if (m.find()) out.put(dir.getFileName().toString(), m.group(1));
            }
        }
        return out;
    }

    static final Pattern TITLE_LINE = Pattern.compile("^title:\\s*\"(.*)\"\\s*$", Pattern.MULTILINE);

    /** The `authors:` slugs in this file's frontmatter, in order. */
    static Set<String> authorSlugs(String text) {
        Set<String> out = new LinkedHashSet<>();
        if (!text.startsWith("---")) return out;
        int end = text.indexOf("\n---", 3);
        if (end < 0) return out;
        String fm = text.substring(0, end);
        int at = fm.indexOf("\nauthors:");
        if (at < 0) return out;
        for (String line : fm.substring(at + 1).split("\n")) {
            if (line.startsWith("authors:")) continue;
            Matcher m = LIST_ITEM.matcher(line);
            if (m.matches()) out.add(m.group(1));
            else break;                    // next key -- the list is over
        }
        return out;
    }

    static final Pattern LIST_ITEM = Pattern.compile("\\s+-\\s*\"?([^\"\\s]+)\"?\\s*");

    /**
     * Returns the file with Yoast's " - by <Author>" tail removed from its
     * `description:` line, or null when there is nothing to change.
     *
     * Same raw-line treatment as repairFrontmatterDescription: the value is
     * always a double-quoted scalar written by the scrapers, and removing a tail
     * of plain words cannot change the quoting.
     */
    static String stripFrontmatterByline(String text, Map<String, String> authorNames) {
        String line = descriptionLine(text);
        if (line == null || !line.contains(" - by ")) return null;

        List<String> names = new ArrayList<>();
        for (String slug : authorSlugs(text)) {
            String name = authorNames.get(slug);
            if (name != null) names.add(name);
        }
        if (names.isEmpty()) return null;

        String prefix = "description: \"";
        String trimmed = line.trim();
        if (!trimmed.startsWith(prefix) || !trimmed.endsWith("\"")) return null;
        String value = trimmed.substring(prefix.length(), trimmed.length() - 1);

        String fixed = HtmlToMarkdown.stripBylineSuffix(value, names);
        if (fixed.equals(value)) return null;

        int lineStart = text.indexOf("\ndescription:") + 1;
        int lineEnd = lineStart + line.length();
        return text.substring(0, lineStart) + prefix + fixed + "\"" + text.substring(lineEnd);
    }

    /** Same shape as the repair, minus the guards -- used only for reporting. */
    static final Pattern CANDIDATE = Pattern.compile("([A-Za-z]+)([.!?:])(?=[A-Z])");

    /**
     * The run-on candidates in this file's description that the repair declined,
     * as context snippets. Only the capitalised-word case can reach here: the
     * dotted-path case (sun.misc.Unsafe) is not ambiguous and needs no review.
     */
    static List<String> ambiguous(String text) {
        List<String> out = new ArrayList<>();
        String line = descriptionLine(text);
        if (line == null) return out;
        Matcher m = CANDIDATE.matcher(line);
        while (m.find()) {
            String word = m.group(1);
            if (word.length() < 2 || !Character.isUpperCase(word.charAt(0))) continue;
            int at = m.start(2);
            // Skip a dotted path -- unambiguous, and correctly left alone.
            int start = at, end = at + 1;
            while (start > 0 && !Character.isWhitespace(line.charAt(start - 1))) start--;
            while (end < line.length() && !Character.isWhitespace(line.charAt(end))) end++;
            boolean otherDot = false;
            for (int i = start; i < end; i++) if (i != at && line.charAt(i) == '.') otherDot = true;
            if (otherDot) continue;
            out.add("..." + line.substring(Math.max(0, at - 40), Math.min(line.length(), at + 40)) + "...");
        }
        return out;
    }

    /** The raw `description:` frontmatter line, or null. */
    static String descriptionLine(String text) {
        if (!text.startsWith("---")) return null;
        int end = text.indexOf("\n---", 3);
        if (end < 0) return null;
        String fm = text.substring(0, end);
        int lineStart = fm.indexOf("\ndescription:");
        if (lineStart < 0) return null;
        lineStart++;
        int lineEnd = fm.indexOf('\n', lineStart);
        return fm.substring(lineStart, lineEnd < 0 ? fm.length() : lineEnd);
    }

    /**
     * Returns the file with its `description:` line repaired, or null when there
     * is nothing to change.
     *
     * Operates on the raw line rather than parsing YAML because the value is
     * always a double-quoted scalar written by the scrapers (yamlString), and the
     * repair only ever inserts a space -- so no quoting or escaping can change.
     * Guarded to the frontmatter block, so a `description:` inside a body (a
     * Kubernetes manifest in a fence, say) is never touched.
     */
    static String repairFrontmatterDescription(String text) {
        String line = descriptionLine(text);
        if (line == null) return null;
        int lineStart = text.indexOf("\ndescription:") + 1;
        int lineEnd = lineStart + line.length();
        String fixed = HtmlToMarkdown.repairRunOnSentences(line);
        if (fixed.equals(line)) return null;

        return text.substring(0, lineStart) + fixed + text.substring(lineEnd);
    }
}
