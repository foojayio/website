///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.jsoup:jsoup:1.17.2
//SOURCES ../shared/HtmlToMarkdown.java
//JAVA 17+

import org.jsoup.parser.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * One-off migration: rewrites every legacy EnlighterJS code block already in
 * content/ as a plain Markdown fence.
 *
 *     <pre class="EnlighterJSRAW" data-enlighter-language="java" ...>CODE</pre>
 *
 * becomes
 *
 *     ```java
 *     CODE
 *     ```
 *
 * WHY. The Enlighter tag is eight attributes of WordPress plumbing that no
 * contributor can be expected to type by hand, and posts arrive here as pull
 * requests (CONTRIBUTING.md). Fences are the format people already know. The
 * EnlighterJS markup is put back at RENDER time by
 * themes/foojay/layouts/_default/_markup/render-codeblock.html, so the site is
 * visually unchanged -- only the storage format moves.
 *
 * It also repairs WordPress's double-escaping inside fences -- bodies that
 * store a lambda arrow as `-&amp;gt;`, so the code reads `-&gt;` -- using the
 * same rule the conversion scripts apply (HtmlToMarkdown.resolveDoubleEscaped).
 * Same origin, same cleanup: WP damage to a code block that is now ours to own.
 *
 * This runs over content that has already been converted. The conversion
 * scripts emit fences directly from now on (HtmlToMarkdown.codeFence), so a
 * re-run of Posts/ConvertPages produces the same shape and this script
 * becomes a no-op. It stays in the repo because the WordPress site keeps
 * serving Enlighter markup until cutover, so a late re-scrape of an
 * already-migrated page can reintroduce blocks.
 *
 * Usage:
 *   jbang scripts/cleanup/EnlighterToFences.java --dry-run   (report only, changes nothing)
 *   jbang scripts/cleanup/EnlighterToFences.java
 *   jbang scripts/cleanup/EnlighterToFences.java --path content/pages
 *
 * Idempotent: a file with no Enlighter blocks left is not rewritten. Frontmatter
 * is never touched -- only the body below the closing `---` is scanned.
 */
public class EnlighterToFences {

    static final Path DEFAULT_ROOT = Path.of("content");

    /** The whole element, non-greedy so consecutive blocks don't merge into one. */
    static final Pattern BLOCK = Pattern.compile(
            "<pre\\b[^>]*\\bclass=\"[^\"]*EnlighterJSRAW[^\"]*\"[^>]*>(.*?)</pre>",
            Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    static final Pattern LANG_ATTR = Pattern.compile(
            "data-enlighter-language=\"([^\"]*)\"", Pattern.CASE_INSENSITIVE);
    /** ```EnlighterJSRAW / ```kotlin EnlighterJSRAW -> ``` / ```kotlin. */
    static final Pattern FENCE_INFO = Pattern.compile(
            "(?m)^([ \\t]*`{3,})[ \\t]*(\\w+[ \\t]+)?EnlighterJSRAW[^\\n]*$");
    /** Inline <code class="EnlighterJSRAW">x</code> -> `x`. */
    static final Pattern INLINE = Pattern.compile(
            "<code\\b[^>]*\\bclass=\"[^\"]*EnlighterJSRAW[^\"]*\"[^>]*>(.*?)</code>",
            Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    /** A fence line, split into indent / backtick run / info string. */
    static final Pattern FENCE_LINE = Pattern.compile("^([ \\t]*)(`{3,})(.*)$");
    /** An inline code span: a backtick run, its content, the same run again. */
    static final Pattern CODE_SPAN = Pattern.compile("(`+)([^`]+)\\1");
    /** A Markdown link/image destination: the text up to whitespace or `)` inside `](...)`. */
    static final Pattern LINK_DEST = Pattern.compile("(?<=\\]\\()([^()\\s]+)");
    /** Any HTML tag. Marks a line as belonging to a preserved raw-HTML block,
     *  where entities are correct as written and backticks are not code spans. */
    static final Pattern HTML_TAG = Pattern.compile("(?i)<[a-z][a-z0-9]*(\\s[^<>]*)?/?>|</[a-z][a-z0-9]*>");

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

        int changedFiles = 0, blocks = 0, inlines = 0, entityLines = 0;
        Map<String, Integer> byLanguage = new TreeMap<>();

        for (Path file : files) {
            String original = Files.readString(file);
            int split = bodyStart(original);
            String head = original.substring(0, split);
            String body = original.substring(split);
            // Two independent repairs below; a file needs only one of them.
            // U+2013 is here for normalizeCodeDashes: 3 of the 13 posts carrying a
            // mangled flag hold no entity and no nbsp at all, so without it they
            // were skipped before either repair got to look at them.
            if (!body.contains("EnlighterJSRAW") && body.indexOf('&') < 0
                    && body.indexOf('\u00a0') < 0 && body.indexOf('\u2013') < 0) continue;

            StringBuilder out = new StringBuilder();
            Matcher m = BLOCK.matcher(body);
            int last = 0, fileBlocks = 0;
            while (m.find()) {
                String lang = "";
                Matcher lm = LANG_ATTR.matcher(m.group(0));
                if (lm.find()) lang = lm.group(1);

                String fence = HtmlToMarkdown.codeFence(unescape(m.group(1)), lang);
                byLanguage.merge(HtmlToMarkdown.fenceLanguage(lang).isEmpty()
                        ? "(none)" : HtmlToMarkdown.fenceLanguage(lang), 1, Integer::sum);

                // Blank-line hygiene is applied to the PROSE between blocks only.
                // The WP export leaves runs of empty lines where blocks sat, but
                // collapsing them globally would also eat blank lines INSIDE the
                // code we just fenced, silently editing people's samples.
                out.append(collapseBlankLines(body.substring(last, m.start())));
                // A fence must start its own line and be separated from
                // surrounding prose, or Goldmark folds it into the paragraph above.
                out.append(padBefore(out)).append(fence).append("\n");
                last = m.end();
                fileBlocks++;
            }
            out.append(collapseBlankLines(body.substring(last)));

            // Blank-line hygiene only makes sense where a block was actually
            // lifted out; on a file that just needs the entity repair below,
            // rewrapping its prose would be an unrelated cosmetic diff.
            String newBody = fileBlocks > 0 ? out.toString() : body;
            Matcher im = INLINE.matcher(newBody);
            StringBuilder inlineOut = new StringBuilder();
            int fileInlines = 0;
            while (im.find()) {
                String code = unescape(im.group(1)).replace("\n", " ").trim();
                // Backtick inside the code needs a longer delimiter around it.
                String tick = code.contains("`") ? "``" : "`";
                String pad = code.startsWith("`") || code.endsWith("`") ? " " : "";
                im.appendReplacement(inlineOut, Matcher.quoteReplacement(tick + pad + code + pad + tick));
                fileInlines++;
            }
            im.appendTail(inlineOut);
            newBody = inlineOut.toString();

            // A handful of posts already had hand-written fences carrying the
            // plugin's class name as the info string -- ```EnlighterJSRAW and
            // ```kotlin EnlighterJSRAW. Goldmark reads the whole info string as
            // the language, so those render as language "enlighterjsraw" (i.e.
            // unhighlighted). Strip the token, keeping any real language before it.
            newBody = FENCE_INFO.matcher(newBody).replaceAll(r ->
                    r.group(1) + (r.group(2) == null ? "" : r.group(2).trim()));

            int[] fileEntities = new int[1];
            newBody = fixFenceEntities(newBody, fileEntities);

            if (newBody.equals(body)) continue;
            changedFiles++;
            blocks += fileBlocks;
            inlines += fileInlines;
            entityLines += fileEntities[0];
            if (!dryRun) Files.writeString(file, head + newBody);
        }

        System.out.printf("%s %d file(s), %d block(s), %d inline snippet(s), %d double-escaped line(s)%n",
                dryRun ? "[dry-run] would change" : "Changed", changedFiles, blocks, inlines, entityLines);
        System.out.println("Fence languages emitted:");
        byLanguage.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(e -> System.out.printf("  %-12s %d%n", e.getKey(), e.getValue()));
        if (dryRun) System.out.println("\nNothing written. Re-run without --dry-run to apply.");
    }

    /**
     * Repairs WordPress's double-escaping (`-&gt;` where the author wrote `->`)
     * inside every fenced block, via HtmlToMarkdown.resolveDoubleEscaped -- the
     * single definition of which entities are safe to resolve, shared with the
     * conversion scripts so a re-scrape produces the same result.
     *
     * Covers the three places WP damage can land, and only those:
     *
     *   fence bodies      -- via resolveDoubleEscaped, plus normalizeCodeSpaces
     *       for the U+00A0 WordPress indents samples with, and
     *       normalizeCodeDashes for the flag whose `--` arrived as an en dash
     *       (see both methods).
     *   inline code spans -- same rule, same reason: Markdown does not decode
     *       entities inside `...` either, so `DESCRIBE KEYSPACE &lt;name>`
     *       renders as a literal `&lt;`.
     *   link destinations -- via resolveEscapedUrl. `[x](...?a=1&amp;b=2)` is
     *       CORRECT Markdown (CommonMark decodes entities in destinations), so
     *       only the over-escaped `&amp;amp;` is a bug; resolveEscapedUrl
     *       collapses the surplus levels and leaves a single one alone.
     *
     * Everything else is left exactly as it is. Bare prose keeps its entities --
     * an `&amp;` there is ordinary Markdown the author typed, and one post has a
     * TABLE of entity names as its subject matter. Lines carrying an HTML tag are
     * skipped wholesale: they are preserved raw-HTML blocks (embeds, galleries,
     * inline SVG) where `&amp;` is correct markup, not damage.
     */
    static String fixFenceEntities(String body, int[] fixedLines) {
        String[] lines = body.split("\n", -1);
        String openMarker = null; // non-null while inside a fence
        for (int i = 0; i < lines.length; i++) {
            Matcher m = FENCE_LINE.matcher(lines[i]);
            boolean isFence = m.matches();
            if (openMarker == null) {
                // Opening fence. A backtick in the info string means this isn't
                // one (inline code can start a line), so it's treated as prose.
                if (isFence && m.group(3).indexOf('`') < 0) {
                    openMarker = m.group(2);
                    continue;
                }
                String fixedProse = fixProseLine(lines[i]);
                if (!fixedProse.equals(lines[i])) {
                    lines[i] = fixedProse;
                    fixedLines[0]++;
                }
                continue;
            }
            // Closing fence: at least as long as the opener, nothing else on it.
            if (isFence && m.group(2).length() >= openMarker.length() && m.group(3).isBlank()) {
                openMarker = null;
                continue;
            }
            String fixed = HtmlToMarkdown.resolveDoubleEscaped(
                    HtmlToMarkdown.normalizeCodeDashes(
                            HtmlToMarkdown.normalizeCodeSpaces(lines[i])));
            if (!fixed.equals(lines[i])) {
                lines[i] = fixed;
                fixedLines[0]++;
            }
        }
        return String.join("\n", lines);
    }

    /**
     * Repairs a single line of prose: inline code spans and link destinations.
     * A line containing an HTML tag is returned untouched -- see fixFenceEntities.
     */
    static String fixProseLine(String line) {
        if ((line.indexOf('&') < 0 && line.indexOf('\u00a0') < 0
                && line.indexOf('\u2013') < 0)
                || HTML_TAG.matcher(line).find()) return line;
        String out = LINK_DEST.matcher(line).replaceAll(r ->
                Matcher.quoteReplacement(HtmlToMarkdown.resolveEscapedUrl(r.group(1))));
        out = CODE_SPAN.matcher(out).replaceAll(r ->
                Matcher.quoteReplacement(r.group(1)
                        + HtmlToMarkdown.resolveDoubleEscaped(
                                HtmlToMarkdown.normalizeCodeDashes(
                                        HtmlToMarkdown.normalizeCodeSpaces(r.group(2))))
                        + r.group(1)));
        return out;
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

    /** Collapses runs of 3+ newlines to a single blank line. Prose only -- never
     *  applied to fenced code, where blank lines are the author's. */
    static String collapseBlankLines(String prose) {
        return prose.replaceAll("\\n{3,}", "\n\n");
    }

    /** Ensures the fence begins on a fresh line preceded by a blank one. */
    static String padBefore(StringBuilder out) {
        if (out.length() == 0) return "";
        if (out.charAt(out.length() - 1) != '\n') return "\n\n";
        if (out.length() >= 2 && out.charAt(out.length() - 2) != '\n') return "\n";
        return "";
    }

    /**
     * Turns the escaped HTML inside the <pre> back into literal code. Jsoup's
     * parser is used rather than a hand-rolled entity table so every entity the
     * WordPress export emits (&nbsp;, &#039;, &mdash;, ...) is handled.
     *
     * &nbsp; becomes a normal space on purpose: a non-breaking space that looks
     * like an indent but isn't would be copied out of a code sample and break
     * whatever it's pasted into.
     */
    static String unescape(String html) {
        return Parser.unescapeEntities(html, false).replace(' ', ' ');
    }
}
