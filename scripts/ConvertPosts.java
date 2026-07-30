///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.jsoup:jsoup:1.17.2
//DEPS com.fasterxml.jackson.core:jackson-databind:2.17.1
//JAVA 17+

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Converts foojay.io WordPress blog posts under /today/ into Hugo content
 * markdown files under content/posts/.
 *
 * Usage:
 *   jbang scripts/ConvertPosts.java
 *   jbang scripts/ConvertPosts.java --max-pages 5        (quick test run)
 *   jbang scripts/ConvertPosts.java --url https://foojay.io/today/some-post/   (single post, for tuning selectors)
 *
 * IMPORTANT - selector tuning:
 * This script was written without direct access to foojay.io's raw HTML/theme
 * markup (the environment it was authored in could not fetch raw HTML). The
 * selectors below are best-effort based on standard WordPress + Yoast SEO
 * conventions (which the site's meta tags confirm it uses) plus common related-
 * posts plugin markup. Run with --url against a couple of real posts first and
 * adjust the SELECTORS block below if fields come back empty.
 *
 * IDEMPOTENCY:
 * Re-running this script updates existing content/posts/<year>/<month>/<slug>.md
 * files rather than duplicating them, so it's safe to schedule/re-run repeatedly
 * during the WP -> Hugo trial period. If a post's frontmatter has `frozen: true`
 * (set by hand once someone has hand-edited the converted file), the script
 * leaves it alone instead of overwriting it.
 *
 * FILE LAYOUT:
 * Posts are filed under content/posts/<year>/<month>/<slug>.md, bucketed by
 * the post's *original* publish date (not the date the script runs) so the
 * file doesn't move around on re-runs. This is purely a repo-organization
 * choice for keeping a directory of 1000+ posts browsable -- it has no effect
 * on the public URL, which stays /today/<slug>/ via the permalinks config in
 * hugo.toml regardless of where the file lives.
 */
public class ConvertPosts {

    // ---- CONFIG -------------------------------------------------------
    static final String BASE_URL = "https://foojay.io";
    static final String LISTING_PATH = "/today/";
    static final Path OUTPUT_DIR = Path.of("content/posts");
    static final int REQUEST_TIMEOUT_MS = 20_000;
    static final int POLITE_DELAY_MS = 250; // be a good citizen against your own site

    // Best-effort CSS selectors -- adjust after testing against a live post.
    static final String SELECTOR_LISTING_POST_LINKS = "article a.post-card-title, h2 a, h3 a";
    static final String SELECTOR_PAGINATION_NEXT = "a.next, a[rel=next]";
    static final String SELECTOR_ARTICLE_CONTENT = "div.entry-content, article .entry-content, .post-content";
    static final String SELECTOR_CATEGORY_LINKS = "a[href*=/today/category/]";
    static final String SELECTOR_TAG_LINKS = "a[href*=/today/tag/]";
    static final String SELECTOR_AUTHOR_LINK = "a[rel=author], a[href*=/today/author/]";
    static final String SELECTOR_RELATED_POSTS = "div.related-posts a, aside.related a, .jp-relatedposts a, .related_post a";
    static final String SELECTOR_FEATURED_IMAGE_FALLBACK = "article img, .entry-content img";

    static final ObjectMapper JSON = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        Files.createDirectories(OUTPUT_DIR);

        Integer maxPages = null;
        String singleUrl = null;
        for (int i = 0; i < args.length; i++) {
            if ("--max-pages".equals(args[i]) && i + 1 < args.length) {
                maxPages = Integer.parseInt(args[++i]);
            } else if ("--url".equals(args[i]) && i + 1 < args.length) {
                singleUrl = args[++i];
            }
        }

        if (singleUrl != null) {
            PostData data = scrapePost(singleUrl);
            writePost(data, true);
            System.out.println("Wrote " + data.slug + ".md (single-post test run)");
            return;
        }

        List<String> postUrls = collectPostUrls(maxPages);
        System.out.println("Found " + postUrls.size() + " post URLs.");

        int written = 0, skippedFrozen = 0, failed = 0;
        for (String url : postUrls) {
            try {
                PostData data = scrapePost(url);
                if (isFrozen(data.slug)) {
                    skippedFrozen++;
                    continue;
                }
                writePost(data, false);
                written++;
                Thread.sleep(POLITE_DELAY_MS);
            } catch (Exception e) {
                System.err.println("FAILED: " + url + " -> " + e.getMessage());
                failed++;
            }
        }
        // NOTE: writePost() below resolves each post's path under
        // content/posts/<year>/<month>/<slug>.md.
        System.out.printf("Done. written=%d skipped(frozen)=%d failed=%d%n", written, skippedFrozen, failed);
    }

    // ---- Listing crawl --------------------------------------------------

    static List<String> collectPostUrls(Integer maxPages) throws IOException, InterruptedException {
        List<String> urls = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        String pageUrl = BASE_URL + LISTING_PATH;
        int page = 1;

        while (pageUrl != null) {
            System.out.println("Listing page " + page + ": " + pageUrl);
            Document doc = fetch(pageUrl);

            Elements links = doc.select(SELECTOR_LISTING_POST_LINKS);
            for (Element a : links) {
                String href = a.absUrl("href");
                if (isLikelyPostUrl(href)) {
                    seen.add(stripTrailingSlash(href) + "/");
                }
            }

            Element next = doc.selectFirst(SELECTOR_PAGINATION_NEXT);
            pageUrl = next != null ? next.absUrl("href") : null;
            page++;
            if (maxPages != null && page > maxPages) break;
            Thread.sleep(POLITE_DELAY_MS);
        }
        urls.addAll(seen);
        return urls;
    }

    static boolean isLikelyPostUrl(String href) {
        if (href == null || href.isBlank()) return false;
        if (!href.startsWith(BASE_URL + LISTING_PATH)) return false;
        String rest = href.substring((BASE_URL + LISTING_PATH).length());
        // Exclude listing/taxonomy/pagination sub-paths, keep only .../today/<slug>/
        return !rest.isBlank()
                && !rest.startsWith("category/")
                && !rest.startsWith("tag/")
                && !rest.startsWith("author/")
                && !rest.startsWith("page/");
    }

    // ---- Per-post scrape --------------------------------------------------

    static PostData scrapePost(String url) throws IOException {
        Document doc = fetch(url);
        PostData d = new PostData();
        d.url = stripTrailingSlash(url) + "/";
        d.slug = lastPathSegment(d.url);

        d.title = firstNonBlank(
                metaContent(doc, "og:title"),
                textOrNull(doc.selectFirst("h1")),
                doc.title());

        d.description = firstNonBlank(
                attrContent(doc, "meta[name=description]"),
                metaContent(doc, "og:description"));

        d.canonical = attrHref(doc, "link[rel=canonical]");
        if (d.canonical == null) d.canonical = d.url;

        d.image = firstNonBlank(
                metaContent(doc, "og:image"),
                attrSrc(doc.selectFirst(SELECTOR_FEATURED_IMAGE_FALLBACK)));

        JsonNode ld = findArticleJsonLd(doc);

        d.date = firstNonBlank(
                textOf(ld, "datePublished"),
                attrContent(doc, "meta[property=article:published_time]"));

        d.authorSlug = firstNonBlank(
                slugFromAuthorLink(doc),
                slugify(textOf(ld, "author", "name")));

        d.categories = linksToNames(doc, SELECTOR_CATEGORY_LINKS, "/today/category/");
        d.tags = linksToNames(doc, SELECTOR_TAG_LINKS, "/today/tag/");
        d.relatedSlugs = relatedPostSlugs(doc);

        Element content = doc.selectFirst(SELECTOR_ARTICLE_CONTENT);
        d.bodyHtml = content != null ? content.html() : "";

        return d;
    }

    static JsonNode findArticleJsonLd(Document doc) {
        for (Element script : doc.select("script[type=application/ld+json]")) {
            try {
                JsonNode node = JSON.readTree(script.data());
                JsonNode found = findNodeByType(node, "Article", "BlogPosting", "NewsArticle");
                if (found != null) return found;
            } catch (Exception ignored) {
                // not valid/parsable JSON-LD, skip
            }
        }
        return null;
    }

    static JsonNode findNodeByType(JsonNode node, String... types) {
        if (node == null) return null;
        if (node.isObject()) {
            JsonNode type = node.get("@type");
            if (type != null) {
                String t = type.isArray() ? type.toString() : type.asText();
                for (String want : types) {
                    if (t.contains(want)) return node;
                }
            }
            JsonNode graph = node.get("@graph");
            if (graph != null) {
                JsonNode inGraph = findNodeByType(graph, types);
                if (inGraph != null) return inGraph;
            }
        } else if (node.isArray()) {
            for (JsonNode child : node) {
                JsonNode found = findNodeByType(child, types);
                if (found != null) return found;
            }
        }
        return null;
    }

    static String textOf(JsonNode node, String... path) {
        if (node == null) return null;
        JsonNode cur = node;
        for (String p : path) {
            if (cur == null) return null;
            cur = cur.get(p);
        }
        return cur != null && cur.isTextual() ? cur.asText() : null;
    }

    static String slugFromAuthorLink(Document doc) {
        Element a = doc.selectFirst(SELECTOR_AUTHOR_LINK);
        if (a == null) return null;
        String href = a.absUrl("href");
        if (href.isBlank()) href = a.attr("href");
        Matcher m = Pattern.compile("/today/author/([^/]+)/?").matcher(href);
        return m.find() ? m.group(1) : slugify(a.text());
    }

    static List<String> linksToNames(Document doc, String selector, String pathMarker) {
        LinkedHashSet<String> names = new LinkedHashSet<>();
        for (Element a : doc.select(selector)) {
            String text = a.text().trim();
            if (!text.isBlank()) names.add(text);
        }
        return new ArrayList<>(names);
    }

    static List<String> relatedPostSlugs(Document doc) {
        LinkedHashSet<String> slugs = new LinkedHashSet<>();
        for (Element a : doc.select(SELECTOR_RELATED_POSTS)) {
            String href = a.absUrl("href");
            if (isLikelyPostUrl(href)) {
                slugs.add(lastPathSegment(stripTrailingSlash(href) + "/"));
            }
        }
        return new ArrayList<>(slugs);
    }

    static boolean isFrozen(String slug) {
        Optional<Path> existing = findExistingPostFile(slug);
        if (existing.isEmpty()) return false;
        try {
            return Files.readString(existing.get()).contains("frozen: true");
        } catch (IOException e) {
            return false;
        }
    }

    /** Recursively looks for content/posts/**&#47;<slug>.md so a post already
     *  filed under its year/month keeps living there on re-runs, even if this
     *  run's date parsing landed on a slightly different bucket. */
    static Optional<Path> findExistingPostFile(String slug) {
        if (!Files.isDirectory(OUTPUT_DIR)) return Optional.empty();
        try (Stream<Path> files = Files.walk(OUTPUT_DIR)) {
            return files.filter(p -> p.getFileName().toString().equals(slug + ".md")).findFirst();
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    /** Bucket directory for a post: content/posts/<year>/<month>/, derived from
     *  the post's original publish date. Falls back to "undated/" (logged) if
     *  the date couldn't be parsed, so nothing silently gets lost. */
    static Path bucketDirFor(PostData d) {
        try {
            OffsetDateTime dt = OffsetDateTime.parse(d.date, DateTimeFormatter.ISO_DATE_TIME);
            String year = String.format("%04d", dt.getYear());
            String month = String.format("%02d", dt.getMonthValue());
            return OUTPUT_DIR.resolve(year).resolve(month);
        } catch (Exception e) {
            System.err.println("WARN: could not parse date '" + d.date + "' for " + d.slug + ", filing under posts/undated/");
            return OUTPUT_DIR.resolve("undated");
        }
    }

    // ---- Writing markdown --------------------------------------------------

    static void writePost(PostData d, boolean verbose) throws IOException {
        StringBuilder fm = new StringBuilder();
        fm.append("---\n");
        fm.append("title: ").append(yamlString(d.title)).append("\n");
        fm.append("date: ").append(yamlString(d.date)).append("\n");
        fm.append("description: ").append(yamlString(d.description)).append("\n");
        fm.append("canonical: ").append(yamlString(d.canonical)).append("\n");
        fm.append("author: ").append(yamlString(d.authorSlug)).append("\n");
        fm.append("image: ").append(yamlString(d.image)).append("\n");
        fm.append("categories:\n");
        for (String c : d.categories) fm.append("  - ").append(yamlString(c)).append("\n");
        fm.append("tags:\n");
        for (String t : d.tags) fm.append("  - ").append(yamlString(t)).append("\n");
        fm.append("related_posts:\n");
        for (String r : d.relatedSlugs) fm.append("  - ").append(yamlString(r)).append("\n");
        fm.append("aliases:\n");
        fm.append("  - ").append(yamlString(URI(d.url).getPath())).append("\n");
        fm.append("frozen: false\n");
        fm.append("---\n\n");
        fm.append(d.bodyHtml).append("\n");

        Path out = findExistingPostFile(d.slug).orElseGet(() -> bucketDirFor(d).resolve(d.slug + ".md"));
        Files.createDirectories(out.getParent());
        Files.writeString(out, fm.toString());
        if (verbose) System.out.println("Wrote " + out);
    }

    // ---- small utils --------------------------------------------------

    static Document fetch(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent("foojay-hugo-migration-bot/1.0 (+https://github.com/foojayio/website)")
                .timeout(REQUEST_TIMEOUT_MS)
                .get();
    }

    static java.net.URI URI(String s) {
        try {
            return new java.net.URI(s);
        } catch (Exception e) {
            return java.net.URI.create("/");
        }
    }

    static String metaContent(Document doc, String property) {
        return attrContent(doc, "meta[property=" + property + "]");
    }

    static String attrContent(Document doc, String selector) {
        Element e = doc.selectFirst(selector);
        return e != null ? e.attr("content") : null;
    }

    static String attrHref(Document doc, String selector) {
        Element e = doc.selectFirst(selector);
        return e != null ? e.attr("href") : null;
    }

    static String attrSrc(Element e) {
        return e != null ? e.absUrl("src") : null;
    }

    static String textOrNull(Element e) {
        return e != null ? e.text() : null;
    }

    static String firstNonBlank(String... vals) {
        for (String v : vals) if (v != null && !v.isBlank()) return v;
        return "";
    }

    static String lastPathSegment(String urlWithTrailingSlash) {
        String path = URI(urlWithTrailingSlash).getPath();
        String[] parts = path.split("/");
        return parts.length > 0 ? parts[parts.length - 1] : "";
    }

    static String stripTrailingSlash(String s) {
        return s.endsWith("/") ? s.substring(0, s.length() - 1) : s;
    }

    static String slugify(String s) {
        if (s == null) return null;
        return s.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
    }

    static String yamlString(String s) {
        if (s == null) s = "";
        String escaped = s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ");
        return "\"" + escaped + "\"";
    }

    static class PostData {
        String url, slug, title, description, canonical, image, date, authorSlug, bodyHtml;
        List<String> categories = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        List<String> relatedSlugs = new ArrayList<>();
    }
}
