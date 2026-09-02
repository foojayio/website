///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.yaml:snakeyaml:2.2
//DEPS com.fasterxml.jackson.core:jackson-databind:2.17.1
//JAVA 17+

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Writes data/jvm-weekly.yaml -- every edition of Artur Skowronski's JVM Weekly
 * newsletter that is a Foojay roundup, rendered at /jvm-weekly/ ("Foojay
 * Monthly Review") and linked from the News menu. Runs once a day from
 * .github/workflows/sync-external-content.yml.
 *
 * NEEDS NO CREDENTIAL. Frank found this on LinkedIn
 * (linkedin.com/newsletters/jvm-weekly-7097859802881540096), but LinkedIn is a
 * republication: it publishes no feed and serves a logged-out client a wall.
 * The newsletter's home is Substack at www.jvm-weekly.com.
 *
 * TWO SOURCES, BECAUSE THE FEED IS A WINDOW. The RSS feed carries the full body
 * of the 20 most recent editions -- about five months, which reaches five
 * roundups. The newsletter has run since April 2022 and the archive holds 197
 * editions, 24 of them roundups, so a feed-only version silently shipped 5 of
 * 24. The archive LISTING (/api/v1/archive, paged 50) gives the title, date,
 * subtitle and URL of every edition for four requests and no body.
 *
 * SO A BODY IS FETCHED ONLY WHEN IT IS BOTH NEEDED AND UNKNOWN, and that
 * matters: the per-post endpoint 429s under load (measured -- 25 consecutive
 * fetches were rate-limited). A body is needed only to confirm a candidate and
 * to list the articles it covered, so:
 *   - the feed supplies it free for the newest 20;
 *   - data/jvm-weekly.yaml IS THE CACHE for everything older. An edition
 *     already in that file keeps its stored articles and is never re-fetched,
 *     the same way data/geocode-cache.yaml stops fetch/JavaChampions.java
 *     re-geocoding. A warm run makes FIVE requests and no per-post call at all;
 *     a cold rebuild makes ~17, paced.
 *
 * IDENTIFYING A ROUNDUP, AND WHY THE ANSWER BELONGS UPSTREAM. JVM Weekly is
 * weekly and mostly about the wider JVM; the FIRST edition of each month is the
 * Foojay roundup. Artur has already created a Substack SECTION for exactly this
 * -- "Foojay.io Community Newsletter", id 194419, "Monthly Highlights: What's
 * Happening in the Foojay.io Community" -- and has NEVER FILED A POST UNDER IT:
 * every edition reports section_id: null and the section's own feed 404s. This
 * script queries that section on every run and takes it as authoritative when
 * it is non-empty, so the moment he starts ticking it the heuristic below
 * retires itself -- the same self-retiring shape as fetch/JavaChampions.java
 * preferring an upstream `location:` over geocoding. Until then, a roundup is
 * identified in two steps.
 *
 * STEP 1, A CANDIDATE, FROM THE TITLE ALONE (so it costs no request). Artur
 * titles these editions to a formula, and it has taken two forms:
 *   A. `"<Article Title>" with <Author Name>` -- 2025 onwards. The author is a
 *      Foojay author, so the test is that the title contains the display name
 *      of a bundle in content/authors/. Catches all 10.
 *   B. `Best of Foojay.io <Month> Edition` -- the 2024 series. Catches all 7.
 *   C. The title matches one of our post titles outright, which is form A with
 *      the byline dropped.
 *
 * STEP 2, CONFIRMATION: the body must link AT LEAST ONE Foojay article. This is
 * what makes the rule safe rather than merely plausible. Form C fires on four
 * OLD editions that are not roundups at all -- a 2023 edition whose title
 * happens to share three words with "2023 in retrospective", and similar -- and
 * every one of those links ZERO Foojay articles, where all 17 real roundups
 * link between 3 and 9. Neither step is sufficient alone.
 *
 * Result: 24 roundups from 197 editions, monthly without a gap from May 2024 to
 * date. (An earlier version of this reported a seven-month pause across 2025-08
 * to 2026-02. There was none -- it was the archive paging bug described in
 * archive() below, which skipped 27 consecutive editions. Don't infer a gap in
 * the newsletter from a gap in what a paged API handed back.) Rules tried and
 * REJECTED, so nobody re-derives them:
 *   - "the description mentions Foojay": the wording differs every time ("Best
 *     of Foojay.io", "Another Foojay editon", "Next Foojay.io edition is
 *     here!") and several say nothing at all.
 *   - "it links at least N Foojay articles", as an INCLUSION rule: the August
 *     2026 roundup links 3 and an ordinary edition links 2, one link of
 *     headroom on a rule that would silently drop a month. It is only ever used
 *     here to confirm a title candidate, and to flag a near-miss.
 *   - "the first Foojay link is the lead": wrong twice. An edition does not
 *     necessarily link its lead article at all ("Where Production Policy
 *     Belongs: Building Eliya in Public" never links the Eliya post), and the
 *     May 2026 edition's first link is background reading.
 *
 * THE MAIN ARTICLE is the post whose title the edition is named after, else --
 * scoped by the author the title names -- that person's post whose title best
 * matches. NINE of the 24 get none, and that is the correct answer for all
 * nine: the seven 2024 "Best of Foojay.io" editions are flat roundups with
 * nothing leading, and two more lead with a guest post written FOR the
 * newsletter that was never published on Foojay ("Diagnosing Your Leyden AOT
 * Cache", "Buzzers Over Blocking"). Giving those a lead means picking an
 * article Artur did not lead with; the layout renders the covered articles and
 * no lead, which is true. See mainArticle() for the fallback that was removed
 * for inventing exactly that.
 *
 * WHAT IS STORED IS A REFERENCE, NOT A REPUBLICATION: a title, a date, a link
 * back to the edition, Artur's own subtitle, and the SLUGS of the Foojay posts
 * it covered. The summary, byline, thumbnail and read count on the page are all
 * derived at build time from our own copy of the post, so no description is
 * stored twice and a retitled article updates itself. The feed is
 * `Copyright Artur Skowronski`; republishing an edition's body would need his
 * explicit permission and is deliberately not what this does.
 *
 * NO TIMESTAMP IN THE FILE, and it is rewritten only when the editions changed
 * -- the fetch/JugEvents.java lesson: a "generated at" field moves on every run,
 * so every run would commit and therefore deploy on nothing.
 *
 * Usage:
 *   jbang scripts/fetch/JvmWeekly.java                 # sync
 *   jbang scripts/fetch/JvmWeekly.java --dry-run       # print the YAML, write nothing
 *   jbang scripts/fetch/JvmWeekly.java --all           # also report the editions skipped
 *   jbang scripts/fetch/JvmWeekly.java --refetch       # ignore the cache, re-read every body
 *   jbang scripts/fetch/JvmWeekly.java --body-limit N  # cap per-post fetches in one run
 */
public class JvmWeekly {

    static final Path OUTPUT_FILE = Path.of("data/jvm-weekly.yaml");
    static final Path POSTS_DIR = Path.of("content/posts");
    static final Path AUTHORS_DIR = Path.of("content/authors");

    static final String FEED_URL = "https://www.jvm-weekly.com/feed";
    static final String ARCHIVE_API =
            "https://www.jvm-weekly.com/api/v1/archive?sort=new&limit=50&offset=%d";
    static final String POST_API = "https://www.jvm-weekly.com/api/v1/posts/%s";

    /**
     * The Substack section Artur created for exactly this and has not yet used.
     * Checked on every run: the moment it holds posts they are the
     * authoritative answer and the heuristic stops being consulted for them.
     */
    static final String SECTION_API =
            "https://www.jvm-weekly.com/api/v1/archive?sort=new&limit=50&section_id=194419";

    /** Attributable rather than disguised: who this is, and where to complain. */
    static final String USER_AGENT =
            "foojay.io-jvm-weekly/1.0 (+https://foojay.io/jvm-weekly/; monthly Foojay roundup index, refreshed daily)";

    /** Pause between per-post body fetches. Generous, because this endpoint
     *  rate-limits and a run that trips it learns nothing. */
    static final long REQUEST_PAUSE_MS = 1700;

    /** Safety valve on a cold rebuild; a normal run fetches no bodies at all. */
    static final int DEFAULT_BODY_LIMIT = 60;

    /** Safety stop on the archive walk; ~200 editions today, 50 to a page. */
    static final int MAX_ARCHIVE_PAGES = 20;

    /**
     * How much of a post's title has to appear in the edition title. Not 1.0:
     * Artur re-punctuates ("Thread-Safe" for our "Thread Safe") and drops
     * subtitles, which costs whole words.
     */
    static final double TITLE_MATCH = 0.8;

    /**
     * The bar for tier 2, where the edition title already names the author and
     * the field is therefore that one person's posts rather than all 2165. It
     * can be far looser than TITLE_MATCH for the same confidence: an author
     * with one Foojay post still has to clear it, which is what keeps an
     * edition whose lead was never published here (Markus Eisele's "Buzzers
     * Over Blocking", 0% overlap with his one Foojay article; María Arias de
     * Reyna Domínguez's "Diagnosing Your Leyden AOT Cache", 33% against her
     * Leyden series) from being credited to the wrong piece.
     */
    static final double AUTHOR_TITLE_MATCH = 0.6;

    /** An edition we have a body for, that is not a candidate, but that links
     *  this many Foojay posts, is reported rather than silently dropped. */
    static final int NEAR_MISS_LINKS = 3;

    /** Words too common in this corpus to be evidence of anything. */
    static final Set<String> STOPWORDS = Set.of(
            "java", "jvm", "weekly", "with", "from", "that", "this", "your", "what",
            "when", "then", "they", "into", "over", "just", "more", "than", "about");

    /** /today/<slug>/ only -- never /today/author/x/ or /today/category/x/. */
    static final Pattern FOOJAY_POST = Pattern.compile(
            "https?://(?:www\\.)?foojay\\.io/today/([a-z0-9][a-z0-9-]*)/?(?![a-z0-9/-])");

    /** "... - JVM Weekly vol. 187" -- the volume is data, not part of the title. */
    static final Pattern VOLUME_SUFFIX = Pattern.compile(
            "\\s*[-\u2013\u2014]\\s*JVM Weekly vol\\.?\\s*(\\d+)\\s*$", Pattern.CASE_INSENSITIVE);

    /** The 2024 series title, which names itself. */
    static final Pattern BEST_OF = Pattern.compile("^\\s*best of foojay", Pattern.CASE_INSENSITIVE);

    static final ObjectMapper JSON = new ObjectMapper();
    static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    // ------------------------------------------------------------------ main --

    public static void main(String[] args) throws Exception {
        boolean dryRun = has(args, "--dry-run");
        boolean reportAll = has(args, "--all");
        boolean refetch = has(args, "--refetch");
        int bodyLimit = intArg(args, "--body-limit", DEFAULT_BODY_LIMIT);

        Site site = Site.read();
        System.out.println(site.titles.size() + " posts and " + site.authorNames.size()
                + " authors in content/");

        List<Edition> editions = archive();
        System.out.println(editions.size() + " editions in the JVM Weekly archive ("
                + editions.get(editions.size() - 1).date + " to " + editions.get(0).date + ")");

        Map<String, String> feedBodies = feedBodies();
        System.out.println(feedBodies.size() + " of them carry a full body in the RSS feed");

        Map<String, Cached> cache = refetch ? new HashMap<>() : readCache();
        if (!cache.isEmpty()) {
            System.out.println(cache.size() + " already resolved in " + OUTPUT_FILE
                    + " -- those need no per-post request");
        }

        Set<String> sectionSlugs = sectionEditions();
        if (sectionSlugs.isEmpty()) {
            System.out.println("Substack section 194419 holds no posts -- falling back to the"
                    + " title candidate + link confirmation (see the header, and for what to ask Artur).");
        } else {
            System.out.println(sectionSlugs.size() + " edition(s) filed under the Foojay section"
                    + " upstream -- taken as authoritative.");
        }

        List<Map<String, Object>> kept = new ArrayList<>();
        List<String> nearMisses = new ArrayList<>();
        List<String> unresolved = new ArrayList<>();
        int fetched = 0, viaSection = 0, viaTitle = 0, viaByline = 0, noLead = 0;

        for (Edition e : editions) {
            boolean inSection = sectionSlugs.contains(e.slug);
            boolean candidate = inSection || site.isCandidate(e.title);

            // A body comes from the feed, then the cache, then the network --
            // and the network only for a candidate, so an ordinary edition
            // never costs a request.
            String body = feedBodies.get(e.slug);
            Cached cached = cache.get(e.slug);
            List<String> links = null;

            if (body != null) {
                links = site.foojayPosts(body);
            } else if (cached != null) {
                links = cached.articles();
            } else if (candidate && fetched < bodyLimit) {
                try {
                    Thread.sleep(REQUEST_PAUSE_MS);
                    JsonNode post = JSON.readTree(get(String.format(POST_API, e.slug)));
                    fetched++;
                    links = site.foojayPosts(post.path("body_html").asText(""));
                } catch (Exception ex) {
                    // Never fatal: a body we cannot read means one edition is
                    // missing from the page, not a failed daily sync.
                    unresolved.add(e.date + "  " + e.title + "  (" + ex.getMessage() + ")");
                    continue;
                }
            }

            if (!candidate) {
                if (links != null && links.size() >= NEAR_MISS_LINKS) {
                    nearMisses.add(e.date + "  " + e.title
                            + "  (links " + links.size() + " Foojay posts, but its title names"
                            + " neither one of our post titles nor one of their authors)");
                } else if (reportAll) {
                    System.out.println("  skip  " + e.date + "  " + e.title);
                }
                continue;
            }

            // STEP 2. A title candidate that links nothing is not a roundup --
            // this is what excludes the four old editions whose titles collide
            // with a short post title by coincidence.
            if (!inSection && (links == null || links.isEmpty())) {
                if (reportAll) {
                    System.out.println("  drop  " + e.date + "  " + e.title
                            + "  (title candidate, but links no Foojay article)");
                }
                continue;
            }
            if (links == null) links = List.of();

            Match match = site.mainArticle(e.title);
            String main = match == null ? null : match.slug;
            if (inSection) viaSection++;
            else if (match != null && match.tier == 1) viaTitle++;
            else if (match != null) viaByline++;
            else noLead++;

            List<String> also = links.stream().filter(s -> !s.equals(main)).toList();

            Map<String, Object> edition = new LinkedHashMap<>();
            edition.put("date", e.date);
            edition.put("title", e.title);
            if (e.volume != null) edition.put("volume", e.volume);
            edition.put("url", e.url);
            if (e.subtitle != null) edition.put("subtitle", e.subtitle);
            if (main != null) edition.put("main", main);
            if (!also.isEmpty()) edition.put("articles", also);
            kept.add(edition);

            System.out.println("  keep  " + e.date + "  " + e.title);
            System.out.println("          main: " + (main == null
                    ? "-- no lead on Foojay (a flat roundup, or a guest post that lives on JVM Weekly) --"
                    : main + "  [" + match.how + "]")
                    + ", covers " + also.size() + " other post(s)");
        }

        System.out.println();
        System.out.println(kept.size() + " Foojay roundup(s): " + viaSection + " from the upstream"
                + " section, " + viaTitle + " by title match, " + viaByline + " by byline match, "
                + noLead + " with no lead article on Foojay");
        System.out.println(fetched + " per-post body request(s) this run");

        if (!nearMisses.isEmpty()) {
            System.out.println();
            System.out.println("NEEDS A HUMAN -- " + nearMisses.size() + " edition(s) link several"
                    + " Foojay articles but their title matches no post or author:");
            for (String s : nearMisses) System.out.println("  " + s);
        }
        if (!unresolved.isEmpty()) {
            System.out.println();
            System.out.println("COULD NOT READ " + unresolved.size() + " candidate edition(s)"
                    + " -- they are absent from the page; re-run to retry:");
            for (String s : unresolved) System.out.println("  " + s);
        }
        // Near-miss detection can only see editions we hold a body for -- the
        // feed's 20 plus whatever is cached. Said out loud rather than left as
        // an implied guarantee of completeness.
        System.out.println();
        System.out.println("(Near-miss detection covers the " + feedBodies.size()
                + " editions in the feed window; older ones are judged on their title alone.)");

        String yaml = render(kept);
        if (dryRun) {
            System.out.println();
            System.out.println(yaml);
            return;
        }
        if (Files.exists(OUTPUT_FILE) && Files.readString(OUTPUT_FILE).equals(yaml)) {
            System.out.println("data/jvm-weekly.yaml already up to date.");
            return;
        }
        Files.createDirectories(OUTPUT_FILE.getParent());
        Files.writeString(OUTPUT_FILE, yaml);
        System.out.println("Wrote " + OUTPUT_FILE);
    }

    // --------------------------------------------------------------- sources --

    /** One edition of the newsletter, as the archive listing describes it. */
    record Edition(String slug, String date, String title, Integer volume,
                   String url, String subtitle) {}

    /** What a previous run already worked out, read back from the output file. */
    record Cached(String main, List<String> articles) {}

    /**
     * Every edition, newest first, from the paged archive listing. Four requests
     * for 197 editions, and no body -- which is the point: the title is all
     * step 1 needs.
     */
    static List<Edition> archive() throws Exception {
        List<Edition> out = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        // ADVANCE BY WHAT THE PAGE ACTUALLY RETURNED, never by the requested
        // limit, and do not stop on a short page. Substack answers offset=0
        // with 23 rows for a limit of 50 and then 50 rows for every later
        // offset -- so stepping by 50 requests 0, 50, 100 and never asks for
        // rows 23-49 at all. That silently swallowed 27 consecutive editions,
        // which looked exactly like a seven-month pause in the newsletter and
        // was reported as one. It is not: offset=23 returns them.
        int offset = 0;
        for (int page = 0; page < MAX_ARCHIVE_PAGES; page++) {
            JsonNode rows = JSON.readTree(get(String.format(ARCHIVE_API, offset)));
            if (!rows.isArray() || rows.isEmpty()) break;
            offset += rows.size();
            for (JsonNode row : rows) {
                String slug = row.path("slug").asText(null);
                String posted = row.path("post_date").asText(null);
                String rawTitle = row.path("title").asText(null);
                if (slug == null || posted == null || rawTitle == null || !seen.add(slug)) continue;

                Integer volume = null;
                String title = rawTitle;
                Matcher m = VOLUME_SUFFIX.matcher(rawTitle);
                if (m.find()) {
                    volume = Integer.valueOf(m.group(1));
                    title = rawTitle.substring(0, m.start()).trim();
                }
                String url = row.path("canonical_url").asText(null);
                if (url == null || url.isBlank()) url = "https://www.jvm-weekly.com/p/" + slug;
                out.add(new Edition(slug, posted.substring(0, 10), title, volume, url,
                        str(row.path("description").asText(null))));
            }
        }
        return out;
    }

    /** slug -> full body, for the 20 editions the RSS feed carries. */
    static Map<String, String> feedBodies() throws Exception {
        Map<String, String> out = new LinkedHashMap<>();
        // Deliberately NOT namespace-aware: the body lives in <content:encoded>,
        // and with namespaces off that is just a tag called "content:encoded".
        var factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        Document doc = factory.newDocumentBuilder()
                .parse(new ByteArrayInputStream(get(FEED_URL).getBytes(StandardCharsets.UTF_8)));
        NodeList nodes = doc.getElementsByTagName("item");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element el = (Element) nodes.item(i);
            String link = child(el, "link");
            String body = child(el, "content:encoded");
            if (link == null || body == null) continue;
            String s = link.replaceAll("[?#].*$", "").replaceAll("/$", "");
            out.put(s.substring(s.lastIndexOf('/') + 1), body);
        }
        return out;
    }

    /**
     * data/jvm-weekly.yaml read back as a cache. An edition already resolved
     * keeps its articles and costs no per-post request -- the same reason
     * data/geocode-cache.yaml is committed. Keyed by the edition URL's slug,
     * which is what the archive listing and the feed both give.
     */
    @SuppressWarnings("unchecked")
    static Map<String, Cached> readCache() {
        Map<String, Cached> out = new HashMap<>();
        try {
            if (!Files.exists(OUTPUT_FILE)) return out;
            Object parsed = new Yaml().load(Files.readString(OUTPUT_FILE));
            if (!(parsed instanceof List<?> rows)) return out;
            for (Object row : rows) {
                if (!(row instanceof Map<?, ?> m)) continue;
                String url = str(m.get("url"));
                if (url == null) continue;
                String slug = url.replaceAll("[?#].*$", "").replaceAll("/$", "");
                slug = slug.substring(slug.lastIndexOf('/') + 1);
                List<String> articles = new ArrayList<>();
                if (m.get("articles") instanceof List<?> a) {
                    for (Object o : a) if (o != null) articles.add(String.valueOf(o));
                }
                String main = str(m.get("main"));
                // The lead is stored separately from `articles`, so put it back
                // at the front: the rest of this script works on one list.
                if (main != null) articles.add(0, main);
                out.put(slug, new Cached(main, articles));
            }
        } catch (Exception e) {
            System.out.println("  (could not read " + OUTPUT_FILE + " as a cache: " + e.getMessage() + ")");
        }
        return out;
    }

    /**
     * The editions Artur has filed under his Foojay Substack section. Empty
     * today, and FAILS OPEN: this is an undocumented endpoint on somebody
     * else's site and a daily sync must not go red because it moved. A failure
     * simply means the title rule decides, which is what happens anyway.
     */
    static Set<String> sectionEditions() {
        try {
            JsonNode rows = JSON.readTree(get(SECTION_API));
            Set<String> slugs = new LinkedHashSet<>();
            for (JsonNode row : rows) {
                String slug = row.path("slug").asText(null);
                if (slug != null && !slug.isBlank()) slugs.add(slug);
            }
            return slugs;
        } catch (Exception e) {
            System.out.println("  (could not read the Substack section: " + e.getMessage() + ")");
            return Set.of();
        }
    }

    static String child(Element parent, String tag) {
        NodeList nodes = parent.getElementsByTagName(tag);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getParentNode() != parent) continue;
            String text = node.getTextContent();
            if (text != null && !text.isBlank()) return text.trim();
        }
        return null;
    }

    // ------------------------------------------------------------ our content --

    /** What content/ knows: every post's slug, title and author display names. */
    static class Site {
        final Map<String, String> titles = new LinkedHashMap<>();        // slug -> title
        final Map<String, List<String>> bylines = new LinkedHashMap<>();  // slug -> display names
        final Map<String, String> aliases = new HashMap<>();              // URL slug -> real slug
        final Map<String, String> authorNames = new LinkedHashMap<>();    // author slug -> display name

        /**
         * A URL slug -> the post's bundle folder. WordPress slugs are not always
         * ours: /today/foojay-podcast-94-more-than-a-blog-.../ is an `aliases:`
         * entry on the bundle called foojay-podcast-94, and an edition links
         * whichever URL foojay.io served it. Without this those articles
         * silently drop out of the edition.
         */
        String resolve(String urlSlug) {
            if (titles.containsKey(urlSlug)) return urlSlug;
            return aliases.get(urlSlug);
        }

        /** Foojay posts a body links, in document order, deduplicated, and only
         *  those we actually have -- a post that is not in content/ cannot be
         *  summarised, so it is not a covered article. */
        List<String> foojayPosts(String body) {
            List<String> out = new ArrayList<>();
            if (body == null) return out;
            Matcher m = FOOJAY_POST.matcher(body);
            while (m.find()) {
                String slug = resolve(m.group(1));
                if (slug != null && !out.contains(slug)) out.add(slug);
            }
            return out;
        }

        /** STEP 1: does this title look like a roundup at all? Costs no request. */
        boolean isCandidate(String editionTitle) {
            if (BEST_OF.matcher(editionTitle).find()) return true;
            String lower = editionTitle.toLowerCase(Locale.ROOT);
            for (String name : authorNames.values()) {
                // 4 characters is not a name, and a short string is exactly the
                // kind that turns up inside an unrelated word.
                if (name.length() > 4 && lower.contains(name.toLowerCase(Locale.ROOT))) return true;
            }
            return titleMatch(editionTitle) != null;
        }

        /** The post this edition is titled after, or null. */
        String titleMatch(String editionTitle) {
            Set<String> edition = keywords(editionTitle);
            if (edition.isEmpty()) return null;
            String best = null;
            double bestScore = 0;
            for (Map.Entry<String, String> e : titles.entrySet()) {
                Set<String> post = keywords(e.getValue());
                if (post.size() < 2) continue;  // too short to be evidence
                double score = (double) post.stream().filter(edition::contains).count() / post.size();
                if (score >= TITLE_MATCH && score > bestScore) {
                    bestScore = score;
                    best = e.getKey();
                }
            }
            return best;
        }

        /**
         * The article the edition led with, or null when it has none ON FOOJAY.
         *
         * Tier 1 scores the edition title against every post at TITLE_MATCH,
         * which is strict because it is unscoped. Tier 2 uses the author the
         * title names to narrow the field to that person's posts first, and can
         * then afford a much looser bar -- "Optimizing the GC when Migrating
         * Cloud Workloads to Arm" is our "Optimizing the Garbage Collector when
         * Migrating Cloud Workloads", which tier 1 cannot see because Artur
         * wrote GC where we wrote Garbage Collector.
         *
         * THERE IS DELIBERATELY NO "FIRST LINKED POST BY THAT AUTHOR" FALLBACK.
         * It was here and it invented leads: the October 2025 edition is titled
         * after Miro Wengner's energy-consumption research and it credited his
         * design-patterns article instead, purely because that one was linked
         * first. Worse, some editions lead with a guest post written FOR the
         * newsletter that was never published on Foojay -- "Diagnosing Your
         * Leyden AOT Cache", "Buzzers Over Blocking" -- and for those the
         * honest answer is that there is no Foojay article to summarise. The
         * page renders the covered articles and no lead, which is true.
         */
        Match mainArticle(String editionTitle) {
            String byTitle = titleMatch(editionTitle);
            if (byTitle != null) return new Match(byTitle, 1, "title match");

            for (Map.Entry<String, String> author : authorNames.entrySet()) {
                String name = author.getValue();
                if (name.length() <= 4 || !containsIgnoreCase(editionTitle, name)) continue;
                // The author's own name is not evidence about WHICH of their
                // articles this is, and it is the one part of the edition title
                // guaranteed to be there -- so it comes out before scoring.
                Set<String> edition = keywords(stripName(editionTitle, name));
                if (edition.isEmpty()) continue;
                String best = null;
                double bestScore = 0;
                for (Map.Entry<String, String> e : titles.entrySet()) {
                    if (!bylines.getOrDefault(e.getKey(), List.of()).contains(name)) continue;
                    Set<String> post = keywords(e.getValue());
                    if (post.size() < 2) continue;
                    // SCORED IN BOTH DIRECTIONS, and the better one wins. Artur
                    // quotes the article's headline, which is often SHORTER than
                    // our own title -- his "Stream Gatherers" is our
                    // "Introduction to Intermediate Operations Modeler: Stream
                    // Gatherers", where his two words are 100% covered by our
                    // title and only 33% of ours appears in his. One-directional
                    // scoring drops exactly the editions whose headline Artur
                    // trimmed.
                    long shared = post.stream().filter(edition::contains).count();
                    double score = Math.max((double) shared / post.size(),
                                            (double) shared / edition.size());
                    if (score >= AUTHOR_TITLE_MATCH && score > bestScore) {
                        bestScore = score;
                        best = e.getKey();
                    }
                }
                if (best != null) {
                    return new Match(best, 2, String.format("%s, %.0f%% title overlap", name, bestScore * 100));
                }
            }
            return null;
        }

        static Site read() throws IOException {
            Site site = new Site();
            try (Stream<Path> walk = Files.walk(AUTHORS_DIR, 2)) {
                for (Path p : walk.filter(p -> p.getFileName().toString().equals("_index.md")).toList()) {
                    String slug = p.getParent().getFileName().toString();
                    String title = value(Files.readString(p), "title");
                    if (title != null) site.authorNames.put(slug, title);
                }
            }
            try (Stream<Path> walk = Files.walk(POSTS_DIR)) {
                for (Path p : walk.filter(p -> p.getFileName().toString().equals("index.md")).toList()) {
                    // The BUNDLE FOLDER NAME, which is the key the layout looks a
                    // post up by (partials/post-index.html) and the key
                    // `related_posts` uses. Deliberately not `slug:` frontmatter
                    // even though :slugorcontentbasename would prefer it: no post
                    // carries one that differs (checked, 0 of 2163), and keying on
                    // it here while the layout keys on the folder would put a slug
                    // in this file that the page silently cannot resolve.
                    String slug = p.getParent().getFileName().toString();
                    String text = Files.readString(p);

                    site.titles.put(slug, Objects.requireNonNullElse(value(text, "title"), ""));
                    List<String> names = new ArrayList<>();
                    for (String author : list(text, "authors")) {
                        names.add(site.authorNames.getOrDefault(author, author));
                    }
                    site.bylines.put(slug, names);
                    for (String alias : list(text, "aliases")) {
                        String a = alias.replaceAll("^/?today/", "").replaceAll("/$", "");
                        if (!a.isBlank() && !a.contains("/")) site.aliases.putIfAbsent(a, slug);
                    }
                }
            }
            return site;
        }
    }

    record Match(String slug, int tier, String how) {}

    /** The frontmatter block, or "" -- everything up to the closing ---. */
    static String frontmatter(String text) {
        if (!text.startsWith("---")) return "";
        int end = text.indexOf("\n---", 3);
        return end < 0 ? "" : text.substring(3, end);
    }

    static String value(String text, String key) {
        Matcher m = Pattern.compile("^" + key + ":\\s*\"?(.*?)\"?\\s*$", Pattern.MULTILINE)
                .matcher(frontmatter(text));
        if (!m.find()) return null;
        String v = m.group(1).trim();
        return v.isEmpty() ? null : v;
    }

    /** A block-style list under `key:`, as quoted-or-bare scalars. */
    static List<String> list(String text, String key) {
        String fm = frontmatter(text);
        Matcher m = Pattern.compile("^" + key + ":\\s*$", Pattern.MULTILINE).matcher(fm);
        if (!m.find()) return List.of();
        List<String> out = new ArrayList<>();
        for (String line : fm.substring(m.end()).split("\n")) {
            String t = line.trim();
            if (t.isEmpty()) continue;
            if (!t.startsWith("- ")) break;          // next key: the list ended
            out.add(t.substring(2).trim().replaceAll("^\"|\"$", ""));
        }
        return out;
    }

    // -------------------------------------------------------------- matching --

    /**
     * The words of a title worth comparing: 4+ characters, lowercased,
     * stopwords dropped. Anything shorter ("AOT", "the", "is") is either noise
     * or too common to distinguish two of 2100 posts.
     */
    static Set<String> keywords(String s) {
        Set<String> out = new LinkedHashSet<>();
        Matcher m = Pattern.compile("[\\p{L}\\p{N}]+").matcher(s.toLowerCase(Locale.ROOT));
        while (m.find()) {
            String w = m.group();
            if (w.length() > 3 && !STOPWORDS.contains(w)) out.add(w);
        }
        return out;
    }

    /** The edition title with the author's name taken out of it. */
    static String stripName(String title, String name) {
        return Pattern.compile(Pattern.quote(name), Pattern.CASE_INSENSITIVE)
                .matcher(title).replaceAll(" ");
    }

    static boolean containsIgnoreCase(String haystack, String needle) {
        return haystack.toLowerCase(Locale.ROOT).contains(needle.toLowerCase(Locale.ROOT));
    }

    // ---------------------------------------------------------------- output --

    static String render(List<Map<String, Object>> editions) {
        String header = """
                # The monthly Foojay roundup in Artur Skowronski's JVM Weekly newsletter --
                # generated automatically by scripts/fetch/JvmWeekly.java from the public
                # archive and RSS feed at https://www.jvm-weekly.com/, refreshed once a day
                # by .github/workflows/sync-external-content.yml.
                #
                # DO NOT EDIT THIS FILE BY HAND: it is regenerated on every sync and any
                # manual change here is overwritten the next time that runs. It doubles as
                # the script's CACHE -- an edition listed here is never re-fetched -- so
                # deleting an entry is the documented way to make it be read again.
                #
                # `main` and `articles` are SLUGS of our own posts. Everything a reader sees
                # about them on /jvm-weekly/ -- summary, byline, thumbnail, read count -- is
                # resolved from content/posts/ at build time, so nothing here can go stale.
                # An entry with no `main` is a flat "Best of Foojay.io" roundup, which has
                # no lead article. Newest edition first.

                """;
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setSplitLines(false);
        return header + new Yaml(options).dump(editions);
    }

    // ------------------------------------------------------------------ HTTP --

    static String get(String url) throws IOException, InterruptedException {
        IOException last = null;
        for (int attempt = 1; attempt <= 3; attempt++) {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .header("User-Agent", USER_AGENT)
                    .header("Accept", "application/rss+xml, application/xml;q=0.9, application/json;q=0.8, */*;q=0.5")
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();
            try {
                HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) return response.body();
                last = new IOException("HTTP " + response.statusCode() + " from " + url);
                // 429 is the failure this endpoint actually has; back off hard
                // rather than spending the remaining attempts immediately.
                if (response.statusCode() == 429) Thread.sleep(10_000);
            } catch (IOException e) {
                last = e;
            }
            if (attempt < 3) Thread.sleep(2000);
        }
        throw last;
    }

    // ------------------------------------------------------------------ misc --

    static String str(Object o) {
        if (o == null) return null;
        String s = String.valueOf(o).trim();
        return s.isEmpty() || s.equals("null") ? null : s;
    }

    static boolean has(String[] args, String flag) {
        return Arrays.asList(args).contains(flag);
    }

    static String arg(String[] args, String flag) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals(flag)) return args[i + 1];
        }
        return null;
    }

    static int intArg(String[] args, String flag, int fallback) {
        String value = arg(args, flag);
        return value == null ? fallback : Integer.parseInt(value);
    }
}
