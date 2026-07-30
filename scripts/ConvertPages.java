///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.jsoup:jsoup:1.17.2
//JAVA 17+

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Converts every remaining foojay.io page (i.e. everything that is NOT a
 * /today/ blog post or /today/author/ profile -- those are handled by
 * ConvertPosts.java / ConvertAuthors.java) into Hugo content markdown under
 * content/pages/, mirroring the legacy URL structure as directories so nested
 * paths like /java-quick-start/install-java/install-java-on-windows/ survive
 * intact.
 *
 * Usage:
 *   jbang scripts/ConvertPages.java
 *   jbang scripts/ConvertPages.java --url https://foojay.io/who-we-are/   (single page, for tuning selectors)
 *
 * Discovery strategy: tries the Yoast-style XML sitemap index first
 * (sitemap_index.xml -> page-sitemap*.xml), and falls back to scraping the
 * human-readable https://foojay.io/sitemap/ page for links if that XML isn't
 * reachable. Same selector caveat as the other two scripts: tune
 * SELECTOR_ARTICLE_CONTENT below against a couple of real pages first.
 *
 * Idempotent: re-running updates existing files, and skips any page whose
 * frontmatter has been hand-marked `frozen: true`.
 */
public class ConvertPages {

    static final String BASE_URL = "https://foojay.io";
    static final Path OUTPUT_DIR = Path.of("content/pages");
    static final int REQUEST_TIMEOUT_MS = 20_000;
    static final int POLITE_DELAY_MS = 250;

    static final String SELECTOR_ARTICLE_CONTENT = "div.entry-content, article .entry-content, main .content, article";

    // Path prefixes that belong to other scripts or are WP system paths, not
    // real content pages.
    static final List<String> EXCLUDED_PREFIXES = List.of(
            "/today/", "/wp-json/", "/wp-admin/", "/wp-content/", "/wp-includes/",
            "/feed/", "/cdn-cgi/", "/sitemap", "/xmlrpc.php"
    );

    public static void main(String[] args) throws Exception {
        Files.createDirectories(OUTPUT_DIR);

        String singleUrl = null;
        for (int i = 0; i < args.length; i++) {
            if ("--url".equals(args[i]) && i + 1 < args.length) singleUrl = args[++i];
        }

        if (singleUrl != null) {
            PageData d = scrapePage(singleUrl);
            writePage(d);
            System.out.println("Wrote " + d.relPath + " (single-page test run)");
            return;
        }

        Set<String> pageUrls = discoverPageUrls();
        System.out.println("Found " + pageUrls.size() + " candidate page URLs.");

        int written = 0, skipped = 0, failed = 0;
        for (String url : pageUrls) {
            try {
                PageData d = scrapePage(url);
                if (isFrozen(d.relPath)) {
                    skipped++;
                    continue;
                }
                writePage(d);
                written++;
                Thread.sleep(POLITE_DELAY_MS);
            } catch (Exception e) {
                System.err.println("FAILED: " + url + " -> " + e.getMessage());
                failed++;
            }
        }
        System.out.printf("Done. written=%d skipped(frozen)=%d failed=%d%n", written, skipped, failed);
    }

    // ---- discovery --------------------------------------------------

    static Set<String> discoverPageUrls() throws IOException {
        Set<String> urls = new LinkedHashSet<>();

        try {
            Document sitemapIndex = fetchXml(BASE_URL + "/sitemap_index.xml");
            for (Element loc : sitemapIndex.select("sitemap > loc")) {
                String sitemapUrl = loc.text();
                if (sitemapUrl.contains("page-sitemap") || sitemapUrl.contains("page_sitemap")) {
                    Document pageSitemap = fetchXml(sitemapUrl);
                    for (Element pageLoc : pageSitemap.select("url > loc")) {
                        addIfEligible(urls, pageLoc.text());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("XML sitemap discovery failed (" + e.getMessage() + "), falling back to HTML /sitemap/ scrape.");
        }

        if (urls.isEmpty()) {
            Document sitemapPage = Jsoup.connect(BASE_URL + "/sitemap/")
                    .userAgent("foojay-hugo-migration-bot/1.0")
                    .timeout(REQUEST_TIMEOUT_MS)
                    .get();
            for (Element a : sitemapPage.select("a[href^=" + BASE_URL + "], a[href^=/]")) {
                addIfEligible(urls, a.absUrl("href"));
            }
        }

        return urls;
    }

    static void addIfEligible(Set<String> urls, String url) {
        if (url == null || url.isBlank()) return;
        if (!url.startsWith(BASE_URL)) return;
        String path = url.substring(BASE_URL.length());
        if (path.isBlank() || path.equals("/")) return;
        for (String prefix : EXCLUDED_PREFIXES) {
            if (path.startsWith(prefix)) return;
        }
        urls.add(url.endsWith("/") ? url : url + "/");
    }

    // ---- scrape + write --------------------------------------------------

    static PageData scrapePage(String url) throws IOException {
        Document doc = Jsoup.connect(url)
                .userAgent("foojay-hugo-migration-bot/1.0")
                .timeout(REQUEST_TIMEOUT_MS)
                .get();

        PageData d = new PageData();
        d.url = url;
        d.relPath = new java.net.URI(url).getPath(); // e.g. /who-we-are/

        d.title = firstNonBlank(
                metaContent(doc, "og:title"),
                textOrNull(doc.selectFirst("h1")),
                doc.title());

        d.description = firstNonBlank(
                attrContent(doc, "meta[name=description]"),
                metaContent(doc, "og:description"));

        Element canonicalEl = doc.selectFirst("link[rel=canonical]");
        d.canonical = canonicalEl != null ? canonicalEl.attr("href") : d.url;

        Element content = doc.selectFirst(SELECTOR_ARTICLE_CONTENT);
        d.bodyHtml = content != null ? content.html() : "";

        return d;
    }

    static boolean isFrozen(String relPath) {
        Path f = fileFor(relPath);
        if (!Files.exists(f)) return false;
        try {
            return Files.readString(f).contains("frozen: true");
        } catch (IOException e) {
            return false;
        }
    }

    static void writePage(PageData d) throws IOException {
        Path f = fileFor(d.relPath);
        Files.createDirectories(f.getParent());

        StringBuilder fm = new StringBuilder();
        fm.append("---\n");
        fm.append("title: ").append(yamlString(d.title)).append("\n");
        fm.append("description: ").append(yamlString(d.description)).append("\n");
        fm.append("canonical: ").append(yamlString(d.canonical)).append("\n");
        fm.append("url: ").append(yamlString(d.relPath)).append("\n");
        fm.append("aliases:\n");
        fm.append("  - ").append(yamlString(d.relPath)).append("\n");
        fm.append("frozen: false\n");
        fm.append("---\n\n");
        fm.append(d.bodyHtml).append("\n");

        Files.writeString(f, fm.toString());
    }

    /** Mirrors the legacy path as nested directories, e.g.
     *  /java-quick-start/install-java/install-java-on-windows/
     *  -> content/pages/java-quick-start/install-java/install-java-on-windows.md
     */
    static Path fileFor(String relPath) {
        String trimmed = relPath.replaceAll("^/+|/+$", "");
        String[] segments = trimmed.split("/");
        Path dir = OUTPUT_DIR;
        for (int i = 0; i < segments.length - 1; i++) {
            dir = dir.resolve(segments[i]);
        }
        String filename = segments.length > 0 ? segments[segments.length - 1] : "index";
        return dir.resolve(filename + ".md");
    }

    static Document fetchXml(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent("foojay-hugo-migration-bot/1.0")
                .timeout(REQUEST_TIMEOUT_MS)
                .parser(org.jsoup.parser.Parser.xmlParser())
                .get();
    }

    static String metaContent(Document doc, String property) {
        Element e = doc.selectFirst("meta[property=" + property + "]");
        return e != null ? e.attr("content") : null;
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

    static String yamlString(String s) {
        if (s == null) s = "";
        String escaped = s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ");
        return "\"" + escaped + "\"";
    }

    static class PageData {
        String url, relPath, title, description, canonical, bodyHtml;
    }
}
