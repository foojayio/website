///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.jsoup:jsoup:1.17.2
//SOURCES ../shared/HtmlToMarkdown.java
//JAVA 21+

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * One-off migration: puts back the email addresses Cloudflare's "Email Address
 * Obfuscation" hid from the scrapers.
 *
 * foojay.io is behind Cloudflare with that feature on, so an address never
 * reaches a non-browser client -- the HTML carries a placeholder plus an
 * XOR-encoded copy, and a script in the reader's browser swaps them back. The
 * conversion scripts run no JavaScript, so `hello@foojay.io` landed in content/
 * as the literal text "[email protected]" and every mailto: link as a dead
 * `](/cdn-cgi/l/email-protection)`. 293 occurrences across 161 files.
 *
 * It is not only addresses. Cloudflare matches a loose `x@y`, so this also
 * mangled things that merely look like one, INSIDE fenced code:
 * `git@github.com:...` in a clone command, and every line of `java
 * --list-modules` output (`javafx.base@14.0.2`). Those are restored as plain
 * text, not turned into mailto links -- see looksLikeEmail in HtmlToMarkdown.
 *
 * HtmlToMarkdown.decodeCloudflareEmails now undoes all of this at conversion
 * time, so a re-scrape emits the right thing and a re-run here is a no-op. This
 * script exists for the same reason cleanup/EnlighterToFences.java does: the
 * damage is already sitting in content/, and the stored files carry only the
 * placeholder -- the encoded copy was dropped by the converter. So unlike the
 * other migrations this one cannot repair from what it has: it re-fetches each
 * affected page and reads the addresses back out of the live HTML.
 *
 * Safety: an address is only written when the number of placeholders in the
 * file matches the number of obfuscated elements in the live page body, so the
 * n-th placeholder provably corresponds to the n-th address. A file that
 * doesn't match is left untouched and reported, never guessed at.
 *
 * Usage:
 *   jbang scripts/cleanup/CloudflareEmails.java              (repair content/)
 *   jbang scripts/cleanup/CloudflareEmails.java --dry-run    (report only)
 *   jbang scripts/cleanup/CloudflareEmails.java --path content/pages
 */
public final class CloudflareEmails {

    private static final Path CONTENT_DIR = Path.of("content");
    private static final String SITE = "https://foojay.io";
    // WP Engine's WAF 403s a bare Java user agent (same finding as
    // transfer/LegacyViews.java), and this script is read-only against public pages.
    private static final String USER_AGENT =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 "
            + "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
    private static final int TIMEOUT_MS = 30_000;

    // Same containers transfer/Posts.java converts from, so the elements counted
    // here are exactly the ones that produced the placeholders -- the site
    // chrome (footer "hello@foojay.io", share-by-email button) is outside them.
    // .article__main-content is what transfer/Posts.java converts a post from;
    // .about__content is the equivalent container on foojay's WordPress *pages*
    // (privacy policy, terms of use). Scoping to them keeps the site chrome out
    // -- the footer menu carries a "hello@foojay.io" link on every page.
    private static final String SELECTOR_CONTENT =
            ".article__main-content, .about__content, div.entry-content,"
            + " article .entry-content, .post-content";
    private static final String SELECTOR_CONTENT_NOISE =
            "h1, .article__details, .article__tags, .article__author, .article-stats-container,"
            + " .article__table, .section-teaser, .teaser, .homepage-today__guide, script, style";

    /** A link whose text is the placeholder: the whole thing becomes a mailto. */
    private static final Pattern PLACEHOLDER_LINK = Pattern.compile(
            "\\[([*_]*)\\\\?\\[email protected\\\\?\\]([*_]*)\\]"
            + "\\(/cdn-cgi/l/email-protection(?:#([0-9a-fA-F]+))?(?:\\s+\"[^\"]*\")?\\)");
    /**
     * One address as it survives in the file. Two shapes, because WordPress
     * bodies hold two kinds of code block: in a `pre.EnlighterJSRAW` the
     * converter keeps the element's TEXT, so the placeholder lands; in a plain
     * `pre > code` Flexmark keeps the link's HREF instead, so the /cdn-cgi/
     * path lands. Either way it stands for exactly one obfuscated element, which
     * is what lets the n-th one here be paired with the n-th one in the page.
     */
    private static final Pattern PLACEHOLDER = Pattern.compile(
            "\\\\?\\[email protected\\\\?\\]|/cdn-cgi/l/email-protection(?![0-9a-fA-F#])");
    /** A link with ordinary text whose mailto: target Cloudflare encoded into
     *  the fragment. Self-contained -- no fetch needed to decode it. */
    private static final Pattern ENCODED_LINK = Pattern.compile(
            "\\[([^\\]]*)\\]\\(/cdn-cgi/l/email-protection#([0-9a-fA-F]+)((?:\\s+\"[^\"]*\")?)\\)");

    private CloudflareEmails() {
    }

    public static void main(String[] args) throws Exception {
        boolean dryRun = false;
        Path root = CONTENT_DIR;
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

        List<Path> files;
        try (Stream<Path> walk = Files.walk(root)) {
            files = walk.filter(p -> p.toString().endsWith(".md"))
                    .filter(CloudflareEmails::isAffected)
                    .sorted()
                    .toList();
        }
        System.out.println("Affected files: " + files.size() + (dryRun ? " (dry run)" : ""));

        int changed = 0, addresses = 0, skipped = 0;
        List<String> problems = new ArrayList<>();
        for (Path file : files) {
            String body = Files.readString(file);
            // Each placeholder link matches PLACEHOLDER twice (its text and its
            // /cdn-cgi/ destination) but stands for one element, so the second
            // half is discounted here exactly as the scan in repair() skips it.
            int placeholders = count(PLACEHOLDER, body) - count(PLACEHOLDER_LINK, body);
            if (placeholders == 0) {
                // Nothing needs the live page: any /cdn-cgi/ link left carries
                // its own encoded target in the fragment.
                String selfFixed = repair(body, List.of());
                if (!selfFixed.equals(body)) {
                    changed++;
                    System.out.println((dryRun ? "would fix " : "fixed ") + file + " (encoded link target)");
                    if (!dryRun) Files.writeString(file, selfFixed);
                }
                continue;
            }
            String url = liveUrl(file, body);
            if (url == null) {
                problems.add(file + ": no live URL could be derived");
                skipped++;
                continue;
            }
            List<String> decoded;
            try {
                decoded = obfuscatedValues(url);
            } catch (IOException e) {
                problems.add(file + ": fetch failed (" + url + "): " + e.getMessage());
                skipped++;
                continue;
            }
            if (decoded.size() != placeholders) {
                // Zero on the live page means WordPress serves the same literal
                // text: the author typed "[email protected]" themselves (one post
                // does, in a prompt example), so there is nothing to restore.
                problems.add(file + ": " + placeholders + " placeholder(s) but "
                        + decoded.size() + " obfuscated element(s) at " + url + " -- left alone"
                        + (decoded.isEmpty() ? " (the live page has the same literal text)" : ""));
                skipped++;
                continue;
            }

            String fixed = repair(body, decoded);
            if (fixed.equals(body)) continue;
            changed++;
            addresses += placeholders;
            System.out.println((dryRun ? "would fix " : "fixed ") + file
                    + " (" + placeholders + ": " + String.join(", ", decoded.stream().distinct().toList()) + ")");
            if (!dryRun) Files.writeString(file, fixed);
        }

        System.out.println();
        System.out.println("Files " + (dryRun ? "to change" : "changed") + ": " + changed
                + ", addresses restored: " + addresses + ", skipped: " + skipped);
        if (!problems.isEmpty()) {
            System.out.println("\nNeeds a look:");
            problems.forEach(p -> System.out.println("  " + p));
        }
    }

    static boolean isAffected(Path p) {
        try {
            String s = Files.readString(p);
            return s.contains("[email protected]") || s.contains("/cdn-cgi/l/email-protection");
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Rewrites the placeholders left to right, taking the n-th address for the
     * n-th placeholder. A link around a placeholder becomes an explicit
     * `[addr](mailto:addr)`; a bare placeholder becomes the address itself,
     * which is what a code sample or a line of prose held before Cloudflare got
     * to it. Anything that decodes to something that isn't an address (module
     * names in `java --list-modules` output) is text either way.
     */
    static String repair(String body, List<String> decoded) {
        // Where the placeholder sits inside a link, that link is rewritten as a
        // whole; collected up front so the scan below can just ask "is this
        // placeholder inside one?" rather than guessing at offsets.
        List<int[]> linkSpans = new ArrayList<>();
        List<String[]> linkParts = new ArrayList<>();
        Matcher link = PLACEHOLDER_LINK.matcher(body);
        while (link.find()) {
            linkSpans.add(new int[]{link.start(), link.end()});
            linkParts.add(new String[]{link.group(1), link.group(2), link.group(3)});
        }

        StringBuilder out = new StringBuilder();
        int pos = 0, next = 0;
        Matcher text = PLACEHOLDER.matcher(body);
        while (text.find(pos)) {
            String address = decoded.get(next++);
            int idx = indexOfSpanContaining(linkSpans, text.start());
            if (idx >= 0) {
                String[] parts = linkParts.get(idx);
                // Prefer the link's own encoded target when it holds one -- it is
                // the actual href, where the placeholder is only the anchor text.
                String target = parts[2] == null ? null : HtmlToMarkdown.decodeCfEmail(parts[2]);
                if (target == null || !HtmlToMarkdown.looksLikeEmail(target)) target = address;
                out.append(body, pos, linkSpans.get(idx)[0]);
                if (HtmlToMarkdown.looksLikeEmail(address)) {
                    out.append('[').append(parts[0]).append(address).append(parts[1])
                            .append("](mailto:").append(target).append(')');
                } else {
                    out.append(address); // not an address after all -- no link
                }
                pos = linkSpans.get(idx)[1];
            } else {
                out.append(body, pos, text.start()).append(address);
                pos = text.end();
            }
        }
        out.append(body.substring(pos));

        // Links whose text was never a placeholder (their target is all Cloudflare
        // encoded): `[let us know](/cdn-cgi/l/email-protection#6a01...)`, and the
        // share-by-email button, whose target is a recipient-less `?subject=...`.
        return ENCODED_LINK.matcher(out.toString()).replaceAll(m -> {
            String target = HtmlToMarkdown.decodeCfEmail(m.group(2));
            if (target == null) return Matcher.quoteReplacement(m.group());
            // Cloudflare's matcher has false positives -- one post has
            // `<code>@name</code>` wrapped this way -- and `mailto:code>@name`
            // is worse than no link, so those keep the words and lose the link.
            if (!target.startsWith("?") && !HtmlToMarkdown.looksLikeEmail(target)) {
                return Matcher.quoteReplacement(m.group(1));
            }
            return Matcher.quoteReplacement("[" + m.group(1) + "](mailto:" + target + m.group(3) + ")");
        });
    }

    static int indexOfSpanContaining(List<int[]> spans, int offset) {
        for (int i = 0; i < spans.size(); i++) {
            if (spans.get(i)[0] <= offset && offset < spans.get(i)[1]) return i;
        }
        return -1;
    }

    /** Every Cloudflare-obfuscated value in the page body, in document order. */
    static List<String> obfuscatedValues(String url) throws IOException {
        Document doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(TIMEOUT_MS).get();
        Element content = doc.selectFirst(SELECTOR_CONTENT);
        if (content == null) return List.of();
        content.select(SELECTOR_CONTENT_NOISE).remove();
        List<String> values = new ArrayList<>();
        for (Element el : content.select(".__cf_email__[data-cfemail]")) {
            String decoded = HtmlToMarkdown.decodeCfEmail(el.attr("data-cfemail"));
            if (decoded != null) values.add(decoded);
        }
        return values;
    }

    /** The live WordPress URL a content file was converted from. */
    static String liveUrl(Path file, String body) {
        if (file.toString().contains("content/posts/")) {
            String slug = frontmatter(body, "slug");
            if (slug == null) {
                String name = file.getFileName().toString();
                slug = "index.md".equals(name)
                        ? file.getParent().getFileName().toString()
                        : name.substring(0, name.length() - 3);
            }
            return SITE + "/today/" + slug + "/";
        }
        // Pages keep their legacy path in an explicit `url:` (hugo.toml has no
        // permalink rule for them), which is exactly the WordPress URL.
        String url = frontmatter(body, "url");
        return url == null ? null : SITE + url;
    }

    static String frontmatter(String body, String key) {
        Matcher m = Pattern.compile("(?m)^" + key + ":\\s*\"?([^\"\\n]+)\"?\\s*$").matcher(body);
        int end = body.indexOf("\n---", 4);
        return m.find() && (end < 0 || m.start() < end) ? m.group(1).trim() : null;
    }

    static int count(Pattern p, String s) {
        Matcher m = p.matcher(s);
        int n = 0;
        while (m.find()) n++;
        return n;
    }
}
