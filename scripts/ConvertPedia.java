///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.jsoup:jsoup:1.17.2
//SOURCES HtmlToMarkdown.java
//JAVA 17+

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts the foojay.io "Terminology" archive at /pedia/ (the "Java Terms
 * Explained" glossary -- a WordPress custom post type) into Hugo content markdown
 * under content/pedia/, one file per term, keeping the legacy /pedia/<slug>/ URL.
 *
 * Usage:
 *   jbang scripts/ConvertPedia.java
 *   jbang scripts/ConvertPedia.java --url https://foojay.io/pedia/bytecode/   (single term, for tuning)
 *
 * Verified against the live markup (2026-07): the archive lists terms as
 * .terminology-card cards linking to /pedia/<slug>/ (paginated at /pedia/page/N/),
 * and each term page holds its definition in .terminology-inner__content with the
 * title in h1.terminology-inner__title.
 *
 * Body conversion (HTML->Markdown, image localization, widget detection) is
 * shared with the other converters via HtmlToMarkdown.java. Images are pulled
 * local under static/images/pedia/<slug>/.
 *
 * Idempotent: re-running updates existing content/pedia/<slug>.md files and
 * respects `frozen: true` to skip hand-edited terms.
 */
public class ConvertPedia {

    static final String BASE_URL = "https://foojay.io";
    static final String INDEX_PATH = "/pedia/";
    static final Path OUTPUT_DIR = Path.of("content/pedia");
    static final int REQUEST_TIMEOUT_MS = 20_000;
    static final int POLITE_DELAY_MS = 250;
    static final int MAX_INDEX_PAGES = 100; // safety cap
    static final String USER_AGENT = "foojay-hugo-migration-bot/1.0";

    static final HtmlToMarkdown.Options MD_OPTS = new HtmlToMarkdown.Options(
            Path.of("static/images/pedia"), "/images/pedia/", "foojay.io", USER_AGENT, REQUEST_TIMEOUT_MS);

    // Verified selectors (2026-07).
    static final String SELECTOR_TERM_LINKS = ".terminology a[href*=/pedia/]";
    static final String SELECTOR_PAGINATION_NEXT = "a.next, a[rel=next]";
    static final String SELECTOR_TITLE = "h1.terminology-inner__title";
    static final String SELECTOR_CONTENT = ".terminology-inner__content";
    // Chrome inside the content wrapper that isn't part of the definition: the
    // title-box (back link + the <h1>, already captured as the title), the
    // views/date meta, the share box, and any CTA/comments blocks.
    static final String SELECTOR_CONTENT_NOISE = String.join(", ",
            ".terminology-inner__title-box", ".post-views", ".terminology-inner__date",
            ".terminology-inner__social-box", ".terminology-inner__cta-box",
            ".terminology-inner__comments-box", "script", "style");

    // Only clean term slugs -- excludes /pedia/ itself, /pedia/page/N/ and the
    // sort links (/pedia/?sort=...).
    static final Pattern TERM_SLUG = Pattern.compile("/pedia/([a-z0-9-]+)/?$");

    public static void main(String[] args) throws Exception {
        Files.createDirectories(OUTPUT_DIR);

        String singleUrl = null;
        for (int i = 0; i < args.length; i++) {
            if ("--url".equals(args[i]) && i + 1 < args.length) singleUrl = args[++i];
        }

        if (singleUrl != null) {
            TermData d = scrapeTerm(singleUrl);
            writeTerm(d);
            System.out.println("Wrote " + d.slug + ".md (single-term test run)");
            return;
        }

        Set<String> termUrls = collectTermUrls();
        System.out.println("Found " + termUrls.size() + " term URLs.");

        int written = 0, skipped = 0, failed = 0;
        for (String url : termUrls) {
            String slug = slugFromUrl(url);
            if (isFrozen(slug)) {
                skipped++;
                continue;
            }
            try {
                writeTerm(scrapeTerm(url));
                written++;
                Thread.sleep(POLITE_DELAY_MS);
            } catch (Exception e) {
                System.err.println("FAILED: " + url + " -> " + e.getMessage());
                failed++;
            }
        }
        System.out.printf("Done. written=%d skipped(frozen)=%d failed=%d%n", written, skipped, failed);
    }

    /** Walks the /pedia/ archive (following pagination) collecting term URLs. */
    static Set<String> collectTermUrls() throws IOException, InterruptedException {
        Set<String> urls = new LinkedHashSet<>();
        String pageUrl = BASE_URL + INDEX_PATH;
        int page = 1;
        while (pageUrl != null && page <= MAX_INDEX_PAGES) {
            System.out.println("Index page " + page + ": " + pageUrl);
            Document doc = fetch(pageUrl);
            for (Element a : doc.select(SELECTOR_TERM_LINKS)) {
                Matcher m = TERM_SLUG.matcher(a.absUrl("href"));
                if (m.find()) {
                    String slug = m.group(1);
                    if ("page".equals(slug) || "feed".equals(slug)) continue;
                    urls.add(BASE_URL + "/pedia/" + slug + "/");
                }
            }
            Element next = doc.selectFirst(SELECTOR_PAGINATION_NEXT);
            pageUrl = next != null ? next.absUrl("href") : null;
            page++;
            Thread.sleep(POLITE_DELAY_MS);
        }
        return urls;
    }

    static TermData scrapeTerm(String url) throws IOException {
        Document doc = fetch(url);
        TermData d = new TermData();
        d.slug = slugFromUrl(url);

        d.title = stripSiteSuffix(firstNonBlank(
                textOrNull(doc.selectFirst(SELECTOR_TITLE)),
                metaContent(doc, "og:title"),
                doc.title()));

        d.description = firstNonBlank(
                attrContent(doc, "meta[name=description]"),
                metaContent(doc, "og:description"));

        Element content = doc.selectFirst(SELECTOR_CONTENT);
        if (content != null) {
            content.select(SELECTOR_CONTENT_NOISE).remove();
            d.body = HtmlToMarkdown.convert(content, MD_OPTS, d.slug).markdown;
        } else {
            d.body = "";
            System.err.println("  WARNING: no content matched for " + url);
        }
        return d;
    }

    static boolean isFrozen(String slug) {
        Path f = OUTPUT_DIR.resolve(slug + ".md");
        if (!Files.exists(f)) return false;
        try {
            return Files.readString(f).contains("frozen: true");
        } catch (IOException e) {
            return false;
        }
    }

    static void writeTerm(TermData d) throws IOException {
        StringBuilder fm = new StringBuilder();
        fm.append("---\n");
        fm.append("title: ").append(yamlString(d.title)).append("\n");
        fm.append("description: ").append(yamlString(d.description)).append("\n");
        // Explicit URL keeps the legacy /pedia/<slug>/ path.
        fm.append("url: ").append(yamlString("/pedia/" + d.slug + "/")).append("\n");
        fm.append("frozen: false\n");
        fm.append("---\n\n");
        fm.append(d.body).append("\n");

        Files.writeString(OUTPUT_DIR.resolve(d.slug + ".md"), fm.toString());
        System.out.println("Done: " + d.title);
    }

    // ---- small utils --------------------------------------------------

    static Document fetch(String url) throws IOException {
        return Jsoup.connect(url).userAgent(USER_AGENT).timeout(REQUEST_TIMEOUT_MS).get();
    }

    static String slugFromUrl(String url) {
        Matcher m = TERM_SLUG.matcher(url);
        return m.find() ? m.group(1) : slugify(url);
    }

    /** Drops a trailing "… - foojay" / "… | foojay" site-name suffix. */
    static String stripSiteSuffix(String title) {
        if (title == null) return "";
        return title.replaceAll("(?i)\\s*[|\\-–]\\s*foojay(\\.io)?\\s*$", "").strip();
    }

    static String metaContent(Document doc, String property) {
        return attrContent(doc, "meta[property=" + property + "]");
    }

    static String attrContent(Document doc, String selector) {
        Element e = doc.selectFirst(selector);
        return e != null ? e.attr("content") : null;
    }

    static String textOrNull(Element e) {
        return e != null ? e.text() : null;
    }

    static String firstNonBlank(String... vals) {
        for (String v : vals) if (v != null && !v.isBlank()) return v;
        return "";
    }

    static String slugify(String s) {
        return s.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("^-+|-+$", "");
    }

    static String yamlString(String s) {
        if (s == null) s = "";
        String escaped = s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ");
        return "\"" + escaped + "\"";
    }

    static class TermData {
        String slug, title, description, body;
    }
}
