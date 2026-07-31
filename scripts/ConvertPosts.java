///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.jsoup:jsoup:1.17.2
//DEPS com.fasterxml.jackson.core:jackson-databind:2.17.1
//SOURCES HtmlToMarkdown.java
//JAVA 21+

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
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Converts foojay.io WordPress blog posts under /today/ into Hugo content
 * markdown files under content/posts/.
 *
 * Usage:
 *   jbang scripts/ConvertPosts.java                       (full crawl)
 *   jbang scripts/ConvertPosts.java --max-pages 5         (cap listing pages -- quick test)
 *   jbang scripts/ConvertPosts.java --days 14             (only posts published in the last 14 days)
 *   jbang scripts/ConvertPosts.java --since 2026-01-01    (only posts published on/after a date)
 *   jbang scripts/ConvertPosts.java --concurrency 12      (posts scraped per page in parallel; default 8)
 *   jbang scripts/ConvertPosts.java --url https://foojay.io/today/some-post/   (single post)
 *
 * Each listing page's posts are scraped + converted concurrently on virtual
 * threads (see crawlAndConvert), bounded by --concurrency to stay polite.
 *
 * The --days/--since flags are handy for a small, recent test set; pair them
 * with --max-pages to bound how many listing pages get fetched (date-filtering
 * has to fetch each post to read its publish date). See crawlAndConvert().
 *
 * SELECTORS:
 * The SELECTOR_* constants were verified against foojay.io's live markup
 * (2026-07): posts are a block/custom theme (no WordPress `entry-content`), the
 * body is in .article__main-content, the chronological feed is section.section-blog,
 * and authors are in the post's .article__author block. Re-check with --url if the
 * theme changes; scrapePost() logs a WARNING when the content selector matches nothing.
 *
 * BODY CONVERSION:
 * The body is converted to Markdown by HtmlToMarkdown.java (shared with
 * ConvertPages.java via `//SOURCES`), which also pulls foojay-hosted images local
 * (co-located per post under static/images/posts/<year>/<month>/<slug>/) and flags
 * the JDoodle / EnlighterJS widgets so the theme only loads their scripts where used.
 *
 * AUTHORS:
 * A post can have several authors, so frontmatter carries an `authors:` list of
 * slugs (see authorSlugs()); the theme resolves each to its author page.
 *
 * IDEMPOTENCY:
 * Re-running updates existing content/posts/<year>/<month>/<slug>.md files rather
 * than duplicating them, so it's safe to re-run repeatedly during the WP -> Hugo
 * trial period. A post whose frontmatter has `frozen: true` (set by hand after a
 * manual edit) is left untouched.
 *
 * FILE LAYOUT:
 * Each post is a Hugo leaf bundle:
 *   content/posts/<year>/<month>/<day>/<slug>/index.md
 * with its images co-located in that same directory and referenced by bare
 * filename (resolved as page-bundle resources -- no separate static/images tree).
 * This keeps a post and its images together, which is far easier for authors and
 * makes image URLs baseURL-correct automatically. The directory is bucketed by the
 * post's *original* publish date so it doesn't move on re-runs (bundleDirFor()
 * reuses an existing bundle by slug). The public URL stays /today/<slug>/ via the
 * `slug` frontmatter + hugo.toml permalinks, wherever the bundle lives.
 */
public class ConvertPosts {

    // ---- CONFIG -------------------------------------------------------
    static final String BASE_URL = "https://foojay.io";
    static final String LISTING_PATH = "/today/";
    static final Path OUTPUT_DIR = Path.of("content/posts");
    static final int REQUEST_TIMEOUT_MS = 20_000;
    static final int MAX_EMPTY_PAGES = 2;   // with --days/--since: stop after this many consecutive out-of-window pages
    // Posts on a listing page are scraped + converted concurrently on virtual
    // threads. This bounds how many requests hit the site at once -- a courtesy
    // to your own server, and enough to hide network latency. Override with
    // --concurrency N.
    static final int DEFAULT_CONCURRENCY = 8;

    // Each post is a Hugo leaf bundle: content/posts/<y>/<m>/<d>/<slug>/index.md
    // with its images co-located in that directory (referenced by bare filename,
    // resolved as page-bundle resources). Body conversion is shared with the other
    // converters via HtmlToMarkdown.java; the image Options are built per-post
    // (imageBaseDir = the bundle dir, empty url prefix -> relative filenames).
    static final String USER_AGENT = "foojay-hugo-migration-bot/1.0";

    // Verified against foojay.io's live listing (2026-07): the /today/ page has
    // featured / "most viewed" / podcast blocks up top that are NOT in date order,
    // then the real chronological feed in <section class="section-blog"> (dates
    // strictly descending, same container on /today/page/N/). We scope to that
    // section so those out-of-order featured cards don't pollute the crawl (and
    // so the newest-first cutoff logic holds); isLikelyPostUrl() then drops any
    // taxonomy/pagination/author links. Pagination is a.next (…page/N/).
    static final String SELECTOR_LISTING_POST_LINKS = "section.section-blog a[href*=/today/]";
    static final String SELECTOR_PAGINATION_NEXT = "a.next, a[rel=next]";
    // Verified against foojay.io's live post markup (2026-07): the body lives in
    // .article__main-content, which also holds the <h1> and the date/read-time
    // meta -- those are stripped as noise below (they're already in frontmatter).
    static final String SELECTOR_ARTICLE_CONTENT = ".article__main-content, div.entry-content, article .entry-content, .post-content";
    // Chrome injected into the body that isn't article content:
    //   .article__table            - the collapsible "Table of Contents" widget
    //   .section-teaser / .teaser / .homepage-today__guide
    //                              - "Sponsored Content" promo cards / CTAs
    //   .article__details/tags/... - the title/date/read-time/author meta
    static final String SELECTOR_CONTENT_NOISE =
            "h1, .article__details, .article__tags, .article__author, .article-stats-container,"
            + " .article__table, .section-teaser, .teaser, .homepage-today__guide, script, style";
    // Scope to the post's own taxonomy block (.article__tags). A bare
    // a[href*=/today/category/] also matches the nav/sidebar menu (every post
    // would get "Podcast", "JC-AI Newsletter", ...) and related-post cards.
    static final String SELECTOR_CATEGORY_LINKS = ".article__tags a[href*=/today/category/]";
    static final String SELECTOR_TAG_LINKS = ".article__tags a[href*=/today/tag/]";
    // Authors: scope to the post's own author bio block(s). A bare
    // a[href*=/today/author/] also matches the nav "Authors" link (slug "authors")
    // and the related-post cards' author links -- both wrong. A post can carry
    // more than one author, so this yields a list.
    static final String SELECTOR_AUTHOR_LINKS = ".article__author a[href*=/today/author/]";
    static final Pattern AUTHOR_SLUG_IN_HREF = Pattern.compile("/today/author/([^/]+)/");
    // Verified 2026-07: foojay renders "related" posts in a .related-articles
    // section (article-small cards linking to /today/<slug>/). The rest are
    // fallbacks for classic related-posts plugins.
    static final String SELECTOR_RELATED_POSTS =
            ".related-articles a[href*=/today/], div.related-posts a, .jp-relatedposts a, .related_post a";
    static final String SELECTOR_FEATURED_IMAGE_FALLBACK = "article img, .entry-content img";

    static final ObjectMapper JSON = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        Files.createDirectories(OUTPUT_DIR);

        Integer maxPages = null;
        String singleUrl = null;
        OffsetDateTime cutoff = null; // only convert posts published on/after this
        int concurrency = DEFAULT_CONCURRENCY;
        for (int i = 0; i < args.length; i++) {
            if ("--max-pages".equals(args[i]) && i + 1 < args.length) {
                maxPages = Integer.parseInt(args[++i]);
            } else if ("--url".equals(args[i]) && i + 1 < args.length) {
                singleUrl = args[++i];
            } else if ("--days".equals(args[i]) && i + 1 < args.length) {
                cutoff = OffsetDateTime.now().minusDays(Long.parseLong(args[++i]));
            } else if ("--since".equals(args[i]) && i + 1 < args.length) {
                cutoff = LocalDate.parse(args[++i]).atStartOfDay().atOffset(ZoneOffset.UTC);
            } else if ("--concurrency".equals(args[i]) && i + 1 < args.length) {
                concurrency = Math.max(1, Integer.parseInt(args[++i]));
            }
        }

        if (singleUrl != null) {
            PostData data = scrapePost(singleUrl);
            writePost(data, true);
            System.out.println("Wrote " + data.slug + "/index.md (single-post test run)");
            return;
        }

        if (cutoff != null) {
            System.out.println("Only converting posts published on/after " + cutoff.toLocalDate() + ".");
        }

        int[] r = crawlAndConvert(cutoff, maxPages, concurrency);
        // writePost() files each post under content/posts/<year>/<month>/<slug>.md.
        System.out.printf("Done. written=%d skipped(frozen)=%d failed=%d%n", r[0], r[1], r[2]);
    }

    // ---- Listing crawl --------------------------------------------------

    /**
     * Walks the /today/ section-blog feed, scraping and converting each post.
     * Returns {written, skippedFrozen, failed}.
     *
     * The listing crawl (following pagination) stays sequential, but each page's
     * posts -- the network-heavy part: fetch + image downloads + write -- are
     * scraped and converted concurrently on virtual threads, since posts are
     * independent (distinct output file and image directory). A Semaphore caps how
     * many requests run at once (--concurrency); each page is a barrier so the
     * cutoff bookkeeping below stays correct.
     *
     * With a cutoff (--days / --since) only posts *published* on/after it are
     * written; older ones are skipped. We don't stop at the first old post: the
     * feed is ordered by the card's display date, which can differ from the
     * publish date (a post published weeks ago but re-featured sits up top), so
     * recent posts aren't strictly contiguous. Instead the crawl stops after
     * MAX_EMPTY_PAGES consecutive listing pages with nothing in-window. Pair with
     * --max-pages to hard-cap the number of listing pages fetched for a quick test.
     */
    static int[] crawlAndConvert(OffsetDateTime cutoff, Integer maxPages, int concurrency)
            throws IOException, InterruptedException {
        AtomicInteger written = new AtomicInteger();
        AtomicInteger skippedFrozen = new AtomicInteger();
        AtomicInteger failed = new AtomicInteger();
        Set<String> seen = new HashSet<>();
        Semaphore gate = new Semaphore(concurrency); // bound concurrent requests

        try (ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor()) {
            String pageUrl = BASE_URL + LISTING_PATH;
            int page = 1;
            int emptyPages = 0;

            while (pageUrl != null) {
                System.out.println("Listing page " + page + ": " + pageUrl);
                Document doc = fetch(pageUrl);

                List<String> pageUrls = new ArrayList<>();
                for (Element a : doc.select(SELECTOR_LISTING_POST_LINKS)) {
                    String href = a.absUrl("href");
                    if (!isLikelyPostUrl(href)) continue;
                    String url = stripTrailingSlash(href) + "/";
                    if (seen.add(url)) pageUrls.add(url);
                }

                List<Future<Boolean>> futures = new ArrayList<>();
                for (String url : pageUrls) {
                    futures.add(pool.submit(
                            () -> handlePost(url, cutoff, gate, written, skippedFrozen, failed)));
                }
                boolean anyInWindow = awaitAnyInWindow(futures);

                if (cutoff != null && !pageUrls.isEmpty() && !anyInWindow) {
                    if (++emptyPages >= MAX_EMPTY_PAGES) {
                        System.out.println("No in-window posts for " + emptyPages + " pages; stopping crawl.");
                        break;
                    }
                } else {
                    emptyPages = 0;
                }

                Element next = doc.selectFirst(SELECTOR_PAGINATION_NEXT);
                pageUrl = next != null ? next.absUrl("href") : null;
                page++;
                if (maxPages != null && page > maxPages) break;
            }
        }
        return new int[]{written.get(), skippedFrozen.get(), failed.get()};
    }

    /** Waits for all of a page's post tasks and reports whether any was in-window. */
    static boolean awaitAnyInWindow(List<Future<Boolean>> futures) throws InterruptedException {
        boolean any = false;
        for (Future<Boolean> f : futures) {
            try {
                if (Boolean.TRUE.equals(f.get())) any = true;
            } catch (ExecutionException ignored) {
                // handlePost already logged and counted the failure
            }
        }
        return any;
    }

    /**
     * Scrapes and converts one post. Returns true if it was within the
     * --days/--since window (or there is no cutoff), false if it was skipped as
     * out-of-window or failed. Safe to run concurrently: it writes a distinct
     * file and its own image directory. The gate bounds concurrent requests.
     */
    static boolean handlePost(String url, OffsetDateTime cutoff, Semaphore gate,
                              AtomicInteger written, AtomicInteger skippedFrozen, AtomicInteger failed) {
        try {
            gate.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        try {
            PostData data = scrapePost(url);
            if (cutoff != null && isOlderThan(data.date, cutoff)) {
                return false; // published before the window -- skip
            }
            if (isFrozen(data.slug)) {
                skippedFrozen.incrementAndGet();
            } else {
                writePost(data, false);
                written.incrementAndGet();
            }
            return true;
        } catch (Exception e) {
            System.err.println("FAILED: " + url + " -> " + e.getMessage());
            failed.incrementAndGet();
            return false;
        } finally {
            gate.release();
        }
    }

    /** True if an ISO-8601 date string is strictly before the cutoff. Unknown or
     *  unparseable dates return false so a post is kept rather than stopping the crawl. */
    static boolean isOlderThan(String isoDate, OffsetDateTime cutoff) {
        if (isoDate == null || isoDate.isBlank()) return false;
        try {
            return OffsetDateTime.parse(isoDate, DateTimeFormatter.ISO_DATE_TIME).isBefore(cutoff);
        } catch (Exception e) {
            return false;
        }
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
        d.slug = sanitizeSlug(lastPathSegment(d.url));

        d.title = stripSiteSuffix(firstNonBlank(
                metaContent(doc, "og:title"),
                textOrNull(doc.selectFirst("h1")),
                doc.title()));

        d.description = firstNonBlank(
                attrContent(doc, "meta[name=description]"),
                metaContent(doc, "og:description"));

        // Only keep a canonical when it points to a DIFFERENT site (cross-posted
        // content). A self-canonical is redundant -- Hugo/the theme already emit
        // <link rel=canonical> to the page's own permalink.
        String canon = attrHref(doc, "link[rel=canonical]");
        d.canonical = (canon != null && !canon.contains("foojay.io")) ? canon : "";

        d.image = firstNonBlank(
                metaContent(doc, "og:image"),
                attrSrc(doc.selectFirst(SELECTOR_FEATURED_IMAGE_FALLBACK)));

        JsonNode ld = findArticleJsonLd(doc);

        d.date = firstNonBlank(
                textOf(ld, "datePublished"),
                attrContent(doc, "meta[property=article:published_time]"));

        // Recorded as `lastmod` when present and different from the publish date.
        d.dateModified = firstNonBlank(
                textOf(ld, "dateModified"),
                attrContent(doc, "meta[property=article:modified_time]"),
                d.date);

        // Resolve the post's bundle directory (needs the publish date) and localize
        // all images INTO it as bare filenames (empty url prefix).
        d.bundleDir = bundleDirFor(d);
        HtmlToMarkdown.Options opts = new HtmlToMarkdown.Options(
                d.bundleDir, "", "foojay.io", USER_AGENT, REQUEST_TIMEOUT_MS);

        // Pull the hero (og:image) local too, so it isn't hotlinked from the
        // WordPress site that goes away at cutover. Non-foojay images are left as-is.
        String localHero = HtmlToMarkdown.localizeImage(d.image, opts, "");
        if (localHero != null) d.image = localHero;

        d.authors = authorSlugs(doc);
        if (d.authors.isEmpty()) {
            // Last resort: slugify the JSON-LD author name. Note this may not match
            // an author page's slug exactly (e.g. "Carl Dea" -> carl-dea vs carldea).
            String fallback = slugify(textOf(ld, "author", "name"));
            if (fallback != null && !fallback.isBlank()) d.authors.add(fallback);
        }

        d.categories = linksToNames(doc, SELECTOR_CATEGORY_LINKS, "/today/category/");
        d.tags = linksToNames(doc, SELECTOR_TAG_LINKS, "/today/tag/");
        normalizeCategories(d);
        d.relatedSlugs = relatedPostSlugs(doc);

        Element content = doc.selectFirst(SELECTOR_ARTICLE_CONTENT);
        if (content != null) {
            content.select(SELECTOR_CONTENT_NOISE).remove();
            HtmlToMarkdown.Result r = HtmlToMarkdown.convert(content, opts, "");
            d.body = r.markdown;
            d.jdoodle = r.jdoodle;
            d.enlighterjs = r.enlighterjs;
        } else {
            d.body = "";
            System.err.println("  WARNING: no content matched for " + url);
        }

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

    /** All author slugs credited on the post, in order, de-duplicated. */
    static List<String> authorSlugs(Document doc) {
        LinkedHashSet<String> slugs = new LinkedHashSet<>();
        for (Element a : doc.select(SELECTOR_AUTHOR_LINKS)) {
            Matcher m = AUTHOR_SLUG_IN_HREF.matcher(a.absUrl("href"));
            if (m.find() && !m.group(1).isBlank()) slugs.add(m.group(1));
        }
        return new ArrayList<>(slugs);
    }

    static List<String> linksToNames(Document doc, String selector, String pathMarker) {
        LinkedHashSet<String> names = new LinkedHashSet<>();
        for (Element a : doc.select(selector)) {
            String text = a.text().trim();
            if (!text.isBlank()) names.add(text);
        }
        return new ArrayList<>(names);
    }

    // WordPress's catch-all "Uncategorized" is noise on a category page, so we
    // drop it everywhere. Posts that had NO other category would then vanish
    // from every category listing, so we guess a fitting one from the title +
    // tags instead (best-effort keyword match, first rule wins; see RULES).
    // These categories all exist in template/categories.md.
    static final String CATEGORY_UNCATEGORIZED = "Uncategorized";
    static final String CATEGORY_FALLBACK = "Java";
    static final String[][] CATEGORY_GUESS_RULES = {
        {"BoxLang", "boxlang"},
        {"JavaFX", "javafx", "lottie", "gluon", "sheetmusic4j", "scenebuilder"},
        {"Raspberry Pi", "raspberry pi", "pi4j", "gpio", "risc v", "banana pi", "blinking led"},
        {"AI", "copilot", "chatgpt", "openai", "gpt", "llm", "genai", "spring ai",
               "ai agent", "ai agents", "ai powered", "ai system", "ai systems",
               "ai found", "ai shepherd", "machine learning", "langchain"},
        {"Mongo", "mongodb", "mongo", "cqrs"},
        {"Spring", "spring", "grails", "componentscan"},
        {"Testing", "junit", "testcontainers", "unit test", "testing"},
        {"Debugging", "debugging", "stack trace", "stack traces", "race conditions"},
        {"Security", "vulnerability", "cve", "log4j", "cspu", "cspus", "security"},
        {"Trip Reports", "trip report", "trip reports"},
        {"Conference", "kcdc", "conference", "devoxx", "jfokus", "jchateau"},
        {"Agile", "agile", "scrum"},
        {"Library", "itext", "pdf"},
        {"DevOps", "jib", "docker", "container", "kubernetes", "openshift"},
        {"Tutorials", "getting started", "learning java", "new to java", "primer",
                      "first language", "tutorial", "introduction to", "cheatsheet"},
        {"Opinion", "predictions", "retrospective", "overengineering", "myths",
                    "is java still", "why java", "reason java", "emerging technology"},
        {"Performance", "performance", "profiler", "profile", "profiling",
                        "cache providers", "garbage collection"},
        {"Java Core", "thread", "threading", "concurrency", "optional", "stream",
                      "records", "sealed", "pattern matching", "module", "modules",
                      "hashcode", "equals", "jvm", "memory management", "logging",
                      "colorspace", "colorspaces", "images", "bufferedimage", "jshell",
                      "single file", "teeing", "functional programming", "refactoring",
                      "field type", "executable jar", "acronym", "core java",
                      "java platform", "java evolution", "java evolved", "jeps", "panama"},
    };

    static void normalizeCategories(PostData d) {
        d.categories.removeIf(c -> c != null && c.strip().equalsIgnoreCase(CATEGORY_UNCATEGORIZED));
        if (d.categories.isEmpty()) d.categories.add(guessCategory(d.title, d.tags));
    }

    /** Best-effort category from a post's title + tags, for posts WordPress left
     *  uncategorized. First matching rule wins; falls back to "Java". */
    static String guessCategory(String title, List<String> tags) {
        String hay = " " + ((title == null ? "" : title) + " " + String.join(" ", tags))
                .toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", " ") + " ";
        for (String[] rule : CATEGORY_GUESS_RULES) {
            for (int i = 1; i < rule.length; i++) {
                if (hay.contains(" " + rule[i] + " ") || hay.contains(rule[i])) return rule[0];
            }
        }
        return CATEGORY_FALLBACK;
    }

    static List<String> relatedPostSlugs(Document doc) {
        LinkedHashSet<String> slugs = new LinkedHashSet<>();
        for (Element a : doc.select(SELECTOR_RELATED_POSTS)) {
            String href = a.absUrl("href");
            if (isLikelyPostUrl(href)) {
                slugs.add(sanitizeSlug(lastPathSegment(stripTrailingSlash(href) + "/")));
            }
        }
        return new ArrayList<>(slugs);
    }

    static boolean isFrozen(String slug) {
        Optional<Path> bundle = findExistingBundle(slug);
        if (bundle.isEmpty()) return false;
        try {
            return Files.readString(bundle.get().resolve("index.md")).contains("frozen: true");
        } catch (IOException e) {
            return false;
        }
    }

    /** Recursively looks for content/posts/**&#47;<slug>.md so a post already
     *  filed under its year/month keeps living there on re-runs, even if this
     *  run's date parsing landed on a slightly different bucket. */
    static Optional<Path> findExistingBundle(String slug) {
        if (!Files.isDirectory(OUTPUT_DIR)) return Optional.empty();
        try (Stream<Path> files = Files.walk(OUTPUT_DIR)) {
            return files.filter(p -> p.getFileName().toString().equals("index.md")
                            && p.getParent() != null
                            && p.getParent().getFileName().toString().equals(slug))
                    .map(Path::getParent)
                    .findFirst();
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    /** Date bucket for a post: content/posts/<year>/<month>/<day>/, from the
     *  publish date. Falls back to "undated/" (logged) if the date won't parse. */
    static Path bucketDirFor(PostData d) {
        try {
            OffsetDateTime dt = OffsetDateTime.parse(d.date, DateTimeFormatter.ISO_DATE_TIME);
            return OUTPUT_DIR.resolve(String.format("%04d", dt.getYear()))
                    .resolve(String.format("%02d", dt.getMonthValue()))
                    .resolve(String.format("%02d", dt.getDayOfMonth()));
        } catch (Exception e) {
            System.err.println("WARN: could not parse date '" + d.date + "' for " + d.slug + ", filing under posts/undated/");
            return OUTPUT_DIR.resolve("undated");
        }
    }

    /** The post's leaf-bundle directory (content/posts/<y>/<m>/<d>/<slug>/), reused
     *  if a bundle for this slug already exists so re-runs don't move it. */
    static Path bundleDirFor(PostData d) {
        return findExistingBundle(d.slug).orElseGet(() -> bucketDirFor(d).resolve(d.slug));
    }

    // ---- Writing markdown --------------------------------------------------

    static void writePost(PostData d, boolean verbose) throws IOException {
        StringBuilder fm = new StringBuilder();
        fm.append("---\n");
        fm.append("title: ").append(yamlString(d.title)).append("\n");
        // No `slug`: the bundle FOLDER name is the URL slug (hugo.toml permalink
        // uses :slugorcontentbasename), and writePost names that folder d.slug.
        fm.append("date: ").append(yamlString(d.date)).append("\n");
        if (d.dateModified != null && !d.dateModified.isBlank() && !d.dateModified.equals(d.date)) {
            fm.append("lastmod: ").append(yamlString(d.dateModified)).append("\n");
        }
        fm.append("description: ").append(yamlString(d.description)).append("\n");
        if (!d.canonical.isBlank()) {
            fm.append("canonical: ").append(yamlString(d.canonical)).append("\n");
        }
        fm.append("authors:\n");
        for (String a : d.authors) fm.append("  - ").append(yamlString(a)).append("\n");
        fm.append("image: ").append(yamlString(d.image)).append("\n");
        fm.append("categories:\n");
        for (String c : d.categories) fm.append("  - ").append(yamlString(c)).append("\n");
        fm.append("tags:\n");
        for (String t : d.tags) fm.append("  - ").append(yamlString(t)).append("\n");
        fm.append("related_posts:\n");
        for (String r : d.relatedSlugs) fm.append("  - ").append(yamlString(r)).append("\n");
        if (d.jdoodle) fm.append("jdoodle: true\n");
        if (d.enlighterjs) fm.append("enlighterjs: true\n");
        // No aliases: `slug` above already makes the permalink the legacy
        // /today/<slug>/ URL, so a self-referential alias would be redundant.
        fm.append("frozen: false\n");
        fm.append("---\n\n");
        fm.append(d.body).append("\n");

        Path bundleDir = d.bundleDir != null ? d.bundleDir : bundleDirFor(d);
        Files.createDirectories(bundleDir);
        Files.writeString(bundleDir.resolve("index.md"), fm.toString());

        System.out.println("Done post: " + d.title);
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

    /** Drops a trailing site-name suffix ("… | foojay", "… - foojay.io",
     *  "… | Foojay Today") that the WordPress/Yoast <title>/og:title tags
     *  append. */
    static String stripSiteSuffix(String title) {
        if (title == null) return "";
        return title.replaceAll("(?i)\\s*[|\\-–]\\s*foojay(\\.io)?(\\s+today)?\\s*$", "").strip();
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

    /** Cleans a WordPress slug into a safe URL/folder slug: lowercases, replaces
     *  anything outside [a-z0-9_-] (emoji, spaces, punctuation) with a dash,
     *  collapses/trims dashes. Keeps existing dashes and underscores. */
    static String sanitizeSlug(String s) {
        if (s == null) return "";
        return s.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9_-]+", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("^[-_]+|[-_]+$", "");
    }

    static String yamlString(String s) {
        if (s == null) s = "";
        String escaped = s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ");
        return "\"" + escaped + "\"";
    }

    static class PostData {
        String url, slug, title, description, canonical, image, date, dateModified, body;
        Path bundleDir;
        boolean jdoodle, enlighterjs;
        List<String> authors = new ArrayList<>();
        List<String> categories = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        List<String> relatedSlugs = new ArrayList<>();
    }
}
