///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.jsoup:jsoup:1.17.2
//SOURCES HtmlToMarkdown.java
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

    // Body conversion (image localization + widget detection + HTML->Markdown)
    // is shared with ConvertPosts.java via HtmlToMarkdown.java. Pages keep their
    // images under static/images/pages/.
    static final HtmlToMarkdown.Options MD_OPTS = new HtmlToMarkdown.Options(
            Path.of("static/images/pages"), "/images/pages/", "foojay.io",
            "foojay-hugo-migration-bot/1.0", REQUEST_TIMEOUT_MS);

    // Verified against foojay.io's live block-theme markup (2026-07): every Page
    // wraps its body in a single .about__content-wrapper (the theme reuses one
    // template for all Pages). The rest are fallbacks for anything that differs.
    static final String SELECTOR_ARTICLE_CONTENT = ".about__content-wrapper, div.entry-content, article .entry-content, article";
    // Chrome that sits inside the content wrapper but isn't part of the body.
    static final String SELECTOR_CONTENT_NOISE = ".yoast-breadcrumbs, script, style";

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
        d.relPath = java.net.URI.create(url).getPath(); // e.g. /who-we-are/

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
        if (content != null) {
            content.select(SELECTOR_CONTENT_NOISE).remove();
            localizeImages(content);
            d.jdoodle = !content.select(SELECTOR_JDOODLE).isEmpty();
            d.enlighterjs = !content.select(SELECTOR_ENLIGHTERJS).isEmpty();
            d.body = htmlToMarkdown(content);
        } else {
            d.body = "";
            System.err.println("  WARNING: no content matched for " + url);
        }

        return d;
    }

    // ---- html -> markdown ------------------------------------------------

    /**
     * Converts the page body to Markdown. Load-bearing HTML blocks (EnlighterJS
     * code, JDoodle snippets, video embeds -- see SELECTOR_PRESERVE) are swapped
     * out for placeholder tokens first, so the converter can't flatten away the
     * classes/attributes they depend on, then restored verbatim afterwards.
     * Hugo renders the restored raw HTML via goldmark's unsafe mode.
     */
    static String htmlToMarkdown(Element content) {
        List<String> preserved = new ArrayList<>();
        for (Element el : outermostMatches(content, SELECTOR_PRESERVE)) {
            String token = PRESERVE_TOKEN + preserved.size() + PRESERVE_TOKEN_END;
            preserved.add(el.outerHtml());
            el.replaceWith(new Element("p").text(token)); // block-level placeholder
        }

        String md = FlexmarkHtmlConverter.builder().build().convert(content.html()).trim();

        for (int i = 0; i < preserved.size(); i++) {
            md = md.replace(PRESERVE_TOKEN + i + PRESERVE_TOKEN_END,
                    "\n\n" + preserved.get(i) + "\n\n");
        }
        return md.trim();
    }

    /** Matched elements that are not themselves nested inside another match. */
    static List<Element> outermostMatches(Element root, String selector) {
        Elements matches = root.select(selector);
        List<Element> outermost = new ArrayList<>();
        for (Element el : matches) {
            boolean nested = false;
            for (Node p = el.parent(); p != null; p = p.parent()) {
                if (matches.contains(p)) {
                    nested = true;
                    break;
                }
            }
            if (!nested) outermost.add(el);
        }
        return outermost;
    }

    // ---- image localization ---------------------------------------------

    /**
     * Downloads every foojay-hosted image referenced in the body into
     * static/images/pages/ and rewrites the reference to the local path.
     * Covers both <img src>/srcset and <a href> lightbox links to image files.
     * Third-party images (kept working after cutover) are left as-is.
     */
    static void localizeImages(Element content) {
        for (Element img : content.select("img[src]")) {
            String local = localizeImage(img.absUrl("src"));
            if (local != null) {
                img.attr("src", local);
                // srcset points at WordPress-sized variants that vanish at cutover;
                // drop it so the browser just uses the localized src.
                img.removeAttr("srcset");
                img.removeAttr("sizes");
            }
        }
        for (Element a : content.select("a[href]")) {
            String href = a.absUrl("href");
            if (IMAGE_HREF.matcher(href).find()) {
                String local = localizeImage(href);
                if (local != null) a.attr("href", local);
            }
        }
    }

    /**
     * Localizes one image URL, returning its new site-absolute path, or null to
     * leave the reference unchanged (not a foojay-hosted image, or the download
     * failed). The WordPress uploads subpath is preserved under images/pages/ so
     * filenames from different upload folders can't collide. Idempotent: an
     * already-downloaded file is not fetched again.
     */
    static String localizeImage(String absoluteUrl) {
        String rel = pageImageRelPath(absoluteUrl);
        if (rel == null) return null;
        Path out = PAGE_IMAGE_DIR.resolve(rel);
        try {
            if (!Files.exists(out)) {
                Connection.Response res = Jsoup.connect(absoluteUrl)
                        .userAgent(USER_AGENT)
                        .timeout(REQUEST_TIMEOUT_MS)
                        .ignoreContentType(true)
                        .maxBodySize(0)
                        .execute();
                Files.createDirectories(out.getParent());
                Files.write(out, res.bodyAsBytes());
            }
            return PAGE_IMAGE_URL_PREFIX + rel;
        } catch (IOException e) {
            System.err.println("  image download failed: " + absoluteUrl + " -> " + e.getMessage());
            return null;
        }
    }

    /**
     * Maps a foojay-hosted image URL to its relative path under images/pages/,
     * or null if it shouldn't be localized. Uses the WordPress uploads subpath
     * (e.g. .../uploads/2025/05/foo.jpg -> 2025/05/foo.jpg) when present, else
     * the URL path minus its leading slash.
     */
    static String pageImageRelPath(String absoluteUrl) {
        if (absoluteUrl == null || absoluteUrl.isBlank() || absoluteUrl.startsWith("data:")) return null;
        URI uri;
        try {
            uri = URI.create(absoluteUrl);
        } catch (IllegalArgumentException e) {
            return null;
        }
        String host = uri.getHost();
        if (host == null || !host.endsWith(LOCAL_HOST_SUFFIX)) return null; // only foojay-hosted
        String path = uri.getPath();
        if (path == null || path.isBlank()) return null;

        int uploads = path.indexOf("/uploads/");
        String rel = uploads >= 0 ? path.substring(uploads + "/uploads/".length()) : path.replaceFirst("^/+", "");
        rel = rel.replaceAll("\\.\\.(?:/|$)", ""); // defensive: no path traversal
        return rel.isBlank() ? null : rel;
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
        if (d.jdoodle) fm.append("jdoodle: true\n");
        if (d.enlighterjs) fm.append("enlighterjs: true\n");
        fm.append("aliases:\n");
        fm.append("  - ").append(yamlString(d.relPath)).append("\n");
        fm.append("frozen: false\n");
        fm.append("---\n\n");
        fm.append(d.body).append("\n");

        Files.writeString(f, fm.toString());

        System.out.println("Done: " + d.title);
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
        String url, relPath, title, description, canonical, body;
        boolean jdoodle, enlighterjs;
    }
}
