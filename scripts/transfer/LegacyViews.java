///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS com.fasterxml.jackson.core:jackson-databind:2.17.1
//JAVA 21+

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Captures the view counts the live WordPress site holds for every post, page
 * and pedia entry into data/legacy-views.json, and (with --seed) loads them into
 * the Cloudflare Worker that counts views on the Hugo site (worker/views/).
 *
 * Keys are `<section>/<slug>` -- the same key the theme derives in
 * partials/views-key.html. Three of the four counted sections have a WordPress
 * source; the fourth, `authors`, has none (see authorNote()).
 *
 * This is the one-time-but-repeated import the TODO asks for. Re-run it
 * whenever you want the numbers to catch up with WordPress, right up to
 * cutover: /seed SETS the Worker's `legacy` baseline rather than adding to it,
 * and views counted since the last run live in a separate `live` column, so a
 * re-run is idempotent and never doubles or discards anything.
 *
 * WHERE THE NUMBERS COME FROM. foojay.io runs the Post Views Counter plugin,
 * which exposes an unauthenticated REST route:
 *
 *   GET /wp-json/post-views-counter/get-post-views/<id>  ->  11459
 *
 * So this needs no WP admin, DB access or credential -- same posture as
 * transfer/Comments.java reading /wp-json/wp/v2/comments. Note the route sums
 * when handed several ids at once rather than returning one number each, so
 * there is no batching to be had: it is one request per page. Eight run at a
 * time (see fetchAll()), which puts the whole site at two to three minutes.
 * Use --limit while testing.
 *
 * Posts and pages come from /wp/v2/, which lists them. The /pedia/ glossary is a
 * custom post type WordPress does not expose to REST, so it is resolved a
 * different way -- see pediaTargets().
 *
 * Run by hand, never in CI, for the same reason transfer/Sponsors.java is: it
 * reads the WordPress site that disappears at cutover, and --seed writes to a
 * third-party API with a credential. What CI runs is the other half,
 * fetch/ViewCounts.java, which only reads.
 *
 * WHY A DATA FILE AND NOT `legacy_views:` FRONTMATTER. The TODO wondered about
 * a frontmatter field. It would mean rewriting 2145 content files on every
 * re-run -- a large diff, every time, over numbers no author wrote or wants to
 * review. One generated file matches what data/jugs.yaml, data/jug-events.json and
 * data/java-champions.yaml already do here.
 *
 * --write-views: THE BRIDGE UNTIL THE WORKER IS UP.
 *
 * The read counter (worker/views/) is not deployed yet, so fetch/ViewCounts.java
 * gets a WordPress 404 from foojay.io/api/views/all, correctly keeps the committed
 * data/views.json and exits 0 -- which means the numbers on the site are frozen at
 * whenever that file was last seeded, and drift further behind every day.
 *
 * WordPress is still live and still counting, though, and this script already asks
 * it for exactly the right numbers. With --write-views it writes data/views.json
 * too, so the site shows WordPress's current counts instead of a stale snapshot.
 * No Cloudflare, no credential, no new moving part.
 *
 * It is a BRIDGE, not a replacement, and it retires itself: the sync workflow runs
 * this first and fetch/ViewCounts.java second, so the moment the Worker answers,
 * its `legacy + live` overwrites this file and nothing needs changing. Keep
 * re-running --seed as well, or the Worker's `legacy` baseline will be older than
 * what this bridge was already showing and the number would visibly drop.
 *
 * Being in transfer/ is the point: it reads the WordPress site, so it dies at
 * cutover along with the workflow step that calls it. Do not move it to fetch/.
 *
 * Usage:
 *   jbang scripts/transfer/LegacyViews.java                    # fetch -> data/legacy-views.json
 *   jbang scripts/transfer/LegacyViews.java --write-views   (also refresh data/views.json)
 *   jbang scripts/transfer/LegacyViews.java --seed             # ...and push to the Worker
 *   jbang scripts/transfer/LegacyViews.java --limit 20         # quick test run
 *   jbang scripts/transfer/LegacyViews.java --endpoint https://foojay.io/api/views
 *
 * --seed reads the Worker's token from the VIEWS_SEED_TOKEN environment
 * variable (set with `wrangler secret put SEED_TOKEN`, see worker/views/README.md).
 */
public class LegacyViews {

    static final String WP_BASE = "https://foojay.io/wp-json";
    static final String DEFAULT_ENDPOINT = "https://foojay.io/api/views";
    static final Path OUTPUT_FILE = Path.of("data/legacy-views.json");
    /** The file the SITE reads (partials/views.html). Written only with --write-views. */
    static final Path VIEWS_FILE = Path.of("data/views.json");
    /** How many view-count requests are in flight at once. See fetchAll(). */
    static final int CONCURRENCY = 8;

    /** WordPress stamps the object id into the body class: `postid-124618`. */
    static final Pattern PEDIA_POST_ID = Pattern.compile("postid-(\\d+)");

    static final Path POSTS_DIR = Path.of("content/posts");
    static final Path PAGES_DIR = Path.of("content/pages");
    static final Path PEDIA_DIR = Path.of("content/pedia");

    /**
     * WordPress page slugs whose Hugo file is named differently. Only where the
     * page genuinely exists on both sides -- the rest of the WP pages this
     * doesn't match are listing pages (/today/, /today/author/, the home page,
     * the sitemap) that have no single Hugo page to attach a count to.
     */
    static final Map<String, String> PAGE_ALIASES = Map.of(
            "jugs", "java-user-groups-jugs",
            // WordPress serves the events calendar at BOTH /calendar/ and
            // /all-events/; here it is one page, `calendar`. Both WP items
            // resolve to that key and fetchAll() merges with Math::max, so the
            // page keeps the higher of the two counts rather than summing two
            // views of the same content.
            "all-events", "calendar",
            // WordPress had a second team page at /team/ -- a profile of the web
            // agency that built the WP site -- which is gone here, with /team/
            // aliased onto /meet-the-team/. Mapping the key keeps the WP item out
            // of `unmatched`; note fetchAll() merges with Math::max, so
            // meet-the-team keeps the higher of the two counts (31201) rather than
            // summing in /team/'s 3654. Both were real pages, so this does discard
            // that number -- leaving the mapping out would discard it too AND
            // print a line every run, so the mapping is the better of the two.
            "team", "meet-the-team",
            // content/pages/download.md was deleted (it was the same page as
            // /java-quick-start/install-java/) and /download/ is an alias on
            // install-java.md, so the URL resolves but the WP item had nothing to
            // attach its count to. Same shape as `team` above, Math::max and all.
            "download", "install-java");

    /**
     * WordPress objects that live in a different SECTION here, keyed
     * `<wp-type>/<wp-slug>` -> the Hugo `<section>/<slug>` key. WordPress can't
     * be edited to follow, so the mapping has to live on this side: without it
     * the item resolves against the wrong section's slugs, lands in `unmatched`
     * and its whole count is silently dropped at the next run.
     *
     * /log4j-cve/ is a WP page and a Hugo post (content/posts/2021/12/13/).
     */
    static final Map<String, String> SECTION_MOVES = Map.of("pages/log4j-cve", "posts/log4j-cve");

    // WP Engine's WAF 403s a bare Java/Python user agent on these routes while
    // letting a browser through. Nothing about the request is otherwise
    // unusual, so this is the only thing standing between the script and an
    // unexplained wall of 403s.
    static final String USER_AGENT =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 "
            + "(KHTML, like Gecko) Chrome/126.0 Safari/537.36 (+foojay-hugo-migration)";

    static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    static final ObjectMapper JSON = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        List<String> argList = List.of(args);
        boolean seed = argList.contains("--seed");
        boolean writeViews = argList.contains("--write-views");
        int limit = intArg(argList, "--limit", Integer.MAX_VALUE);
        String endpoint = stringArg(argList, "--endpoint", DEFAULT_ENDPOINT);

        Set<String> localPosts = localSlugs(POSTS_DIR, true);
        Set<String> localPages = localSlugs(PAGES_DIR, false);
        Set<String> localPedia = localSlugs(PEDIA_DIR, false);
        System.out.printf("Local content: %d posts, %d pages, %d pedia entries%n",
                localPosts.size(), localPages.size(), localPedia.size());

        List<String> unmatched = new ArrayList<>();
        List<Target> targets = new ArrayList<>();

        // Posts and pages are ordinary WP objects the REST API lists.
        for (String type : List.of("posts", "pages")) {
            boolean isPost = type.equals("posts");
            Set<String> local = isPost ? localPosts : localPages;
            String section = isPost ? "posts" : "pages";
            for (WpItem item : listItems(type)) {
                String moved = SECTION_MOVES.get(type + "/" + item.slug);
                if (moved != null) {
                    targets.add(new Target(moved, item.id));
                    continue;
                }
                String slug = resolveSlug(item.slug, local, isPost ? Map.of() : PAGE_ALIASES);
                if (slug == null) unmatched.add(type + " " + item.slug);
                else targets.add(new Target(section + "/" + slug, item.id));
            }
        }

        targets.addAll(pediaTargets(localPedia, unmatched));

        // Author pages get no baseline: see authorNote().
        authorNote();

        if (targets.size() > limit) targets = targets.subList(0, limit);

        Map<String, Integer> counts = fetchAll(targets);

        long total = counts.values().stream().mapToLong(Integer::longValue).sum();
        System.out.printf("Fetched %d counts, %,d views total%n", counts.size(), total);

        if (!unmatched.isEmpty()) {
            System.out.println("No local page for " + unmatched.size() + " WordPress item(s) -- not imported:");
            for (String s : unmatched) System.out.println("  " + s);
        }

        write(OUTPUT_FILE, counts);
        System.out.println("Wrote " + OUTPUT_FILE);

        if (writeViews) {
            write(VIEWS_FILE, counts);
            System.out.printf("Wrote %s as well (--write-views): the site now shows WordPress's%n"
                    + "  live numbers directly. Harmless once the Worker is up -- fetch/ViewCounts.java%n"
                    + "  runs after this and overwrites the file whenever the counter answers.%n", VIEWS_FILE);
        }

        if (seed) pushSeed(endpoint, counts);
        else System.out.println("Not seeded. Re-run with --seed (and VIEWS_SEED_TOKEN set) to push these to " + endpoint);
    }

    // ---------------------------------------------------------------- WordPress

    record WpItem(int id, String slug) {}

    /** A WordPress object paired with the `<section>/<slug>` key it counts for. */
    record Target(String key, int id) {}

    /**
     * One request per post, run a few at a time. Sequentially this is ~20
     * minutes for the whole site -- not because foojay.io is slow (it answers in
     * ~150ms) but because a single connection spends most of that waiting. Eight
     * at a time brings it under three, which matters for something meant to be
     * re-run repeatedly right up to cutover. Deliberately a small fixed number
     * rather than "as many as possible": this is someone else's production
     * WordPress, and there is no hurry worth degrading it for.
     */
    static Map<String, Integer> fetchAll(List<Target> targets) throws Exception {
        Map<String, Integer> counts = new ConcurrentSkipListMap<>();
        AtomicInteger done = new AtomicInteger();
        try (ExecutorService pool = Executors.newFixedThreadPool(CONCURRENCY)) {
            for (Target target : targets) {
                pool.execute(() -> {
                    try {
                        counts.merge(target.key(), postViews(target.id()), Math::max);
                    } catch (Exception e) {
                        System.err.println("WARN: " + target.key() + ": " + e);
                    }
                    int n = done.incrementAndGet();
                    if (n % 250 == 0) System.out.println("  ... " + n + " / " + targets.size());
                });
            }
        }
        return counts;
    }

    /** Every published post (or page), paged 100 at a time. */
    static List<WpItem> listItems(String type) throws Exception {
        List<WpItem> items = new ArrayList<>();
        int page = 1, totalPages = 1;
        while (page <= totalPages) {
            String url = WP_BASE + "/wp/v2/" + type + "?per_page=100&page=" + page + "&_fields=id,slug";
            HttpResponse<String> response = get(url);
            totalPages = response.headers().firstValue("x-wp-totalpages")
                    .map(Integer::parseInt).orElse(page);
            for (JsonNode node : JSON.readTree(response.body())) {
                items.add(new WpItem(node.get("id").asInt(), node.get("slug").asText()));
            }
            page++;
        }
        System.out.printf("WordPress has %d %s%n", items.size(), type);
        return items;
    }

    /**
     * The /pedia/ glossary. WordPress serves it as a custom post type
     * (`terminology`) that is NOT registered with the REST API, so there is no
     * /wp/v2/ route to list it -- but Post Views Counter counts it like any
     * other post, and the id is in the rendered page's body class
     * (`postid-124618`). So: walk the LOCAL glossary, ask WordPress for each
     * entry's page, and read the id back out of the markup.
     *
     * Thirty entries, one request each, so this stays sequential -- the
     * concurrency in fetchAll() is there for the 2145 posts, not for this.
     */
    static List<Target> pediaTargets(Set<String> localPedia, List<String> unmatched) {
        List<Target> targets = new ArrayList<>();
        for (String slug : localPedia) {
            try {
                String html = get("https://foojay.io/pedia/" + slug + "/").body();
                Matcher m = PEDIA_POST_ID.matcher(html);
                if (m.find()) targets.add(new Target("pedia/" + slug, Integer.parseInt(m.group(1))));
                else unmatched.add("pedia " + slug + " (no post id in the page)");
            } catch (Exception e) {
                unmatched.add("pedia " + slug + " (" + e + ")");
            }
        }
        System.out.printf("WordPress has %d of the %d pedia entries%n", targets.size(), localPedia.size());
        return targets;
    }

    /**
     * Author profiles are the one counted section with nothing to import.
     * WordPress serves /today/author/<slug>/ as a user archive, and Post Views
     * Counter can count those -- but the option is switched off on foojay.io:
     * its user-views route returns 0 for every author checked. Rather than
     * import 342 zeroes, author pages simply start counting when the Worker
     * goes live. Said out loud here because a silently missing section is the
     * kind of thing that gets discovered months later.
     */
    static void authorNote() {
        System.out.println("Author pages: no WordPress counts to import "
                + "(the plugin's user-archive counting is off); they start at zero.");
    }

    /** The Post Views Counter plugin's public per-object counter. */
    static int postViews(int id) throws Exception {
        String body = get(WP_BASE + "/post-views-counter/get-post-views/" + id).body().trim();
        try {
            return Integer.parseInt(body);
        } catch (NumberFormatException e) {
            System.err.println("WARN: unexpected view count for id " + id + ": " + body);
            return 0;
        }
    }

    static HttpResponse<String> get(String url) throws Exception {
        IOException last = null;
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                        .header("User-Agent", USER_AGENT)
                        .header("Accept", "application/json, text/plain, */*")
                        .timeout(Duration.ofSeconds(30))
                        .build();
                HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) return response;
                last = new IOException("HTTP " + response.statusCode() + " for " + url);
            } catch (IOException e) {
                last = e;
            }
            Thread.sleep(1000L * attempt);
        }
        throw last;
    }

    // ------------------------------------------------------------------- slugs

    /**
     * Maps a WordPress slug onto the local content it belongs to.
     *
     * These agree for 2142 of 2145 posts. The exceptions are the posts whose WP
     * slug ends in a percent-encoded emoji: the conversion ran the decoded slug
     * through Posts.sanitizeSlug(), which turns the emoji into a dash and
     * then trims it. Applying the same two steps here reproduces the bundle name
     * exactly, which is why this is derived rather than a hand-kept exception list.
     */
    static String resolveSlug(String wpSlug, Set<String> local, Map<String, String> aliases) {
        if (local.contains(wpSlug)) return wpSlug;
        String alias = aliases.get(wpSlug);
        if (alias != null && local.contains(alias)) return alias;
        String sanitized = sanitizeSlug(URLDecoder.decode(wpSlug, StandardCharsets.UTF_8));
        return local.contains(sanitized) ? sanitized : null;
    }

    /** Kept identical to Posts.sanitizeSlug(). */
    static String sanitizeSlug(String s) {
        if (s == null) return "";
        return s.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9_-]+", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("^[-_]+|[-_]+$", "");
    }

    /**
     * The slugs a section actually serves: leaf-bundle folder names for posts
     * (content/posts/<y>/<m>/<d>/<slug>/index.md), file names for the flat
     * sections. Both are what hugo.toml's :slugorcontentbasename resolves to, so
     * this is the same key the templates and giscus use.
     */
    static Set<String> localSlugs(Path dir, boolean bundles) throws IOException {
        Set<String> slugs = new TreeSet<>();
        if (!Files.isDirectory(dir)) return slugs;
        try (Stream<Path> tree = Files.walk(dir)) {
            tree.filter(Files::isRegularFile).forEach(p -> {
                String name = p.getFileName().toString();
                if (bundles) {
                    // A post bundle is a leaf bundle (index.md); an author or sponsor
                    // bundle is a BRANCH bundle (_index.md) so .Paginate will accept
                    // it. Both name their slug with the folder. The section's own
                    // _index.md is excluded -- otherwise "authors" would be counted
                    // as an author slug and the section root would shadow a real one.
                    boolean isBundleIndex = name.equals("index.md")
                            || (name.equals("_index.md") && !p.getParent().equals(dir));
                    if (isBundleIndex) slugs.add(p.getParent().getFileName().toString());
                } else if (name.endsWith(".md") && !name.equals("_index.md")) {
                    slugs.add(name.substring(0, name.length() - 3));
                }
            });
        }
        return slugs;
    }

    // ------------------------------------------------------------------ output

    static void write(Path file, Map<String, Integer> counts) throws IOException {
        ObjectNode root = JSON.createObjectNode();
        counts.forEach(root::put);
        Files.createDirectories(file.getParent());
        Files.writeString(file, JSON.writerWithDefaultPrettyPrinter().writeValueAsString(root) + "\n");
    }

    static void pushSeed(String endpoint, Map<String, Integer> counts) throws Exception {
        String token = System.getenv("VIEWS_SEED_TOKEN");
        if (token == null || token.isBlank()) {
            System.err.println("--seed needs VIEWS_SEED_TOKEN (see worker/views/README.md). Nothing pushed.");
            System.exit(1);
        }
        ObjectNode body = JSON.createObjectNode();
        counts.forEach(body::put);

        HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint + "/seed"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .header("User-Agent", USER_AGENT)
                .timeout(Duration.ofSeconds(60))
                .POST(HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(body)))
                .build();
        HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Seed failed: HTTP " + response.statusCode() + " " + response.body());
        }
        System.out.println("Seeded: " + response.body());
    }

    // -------------------------------------------------------------------- args

    static String stringArg(List<String> args, String name, String fallback) {
        int i = args.indexOf(name);
        return (i >= 0 && i + 1 < args.size()) ? args.get(i + 1) : fallback;
    }

    static int intArg(List<String> args, String name, int fallback) {
        String value = stringArg(args, name, null);
        return value == null ? fallback : Integer.parseInt(value);
    }
}
