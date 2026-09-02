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
 * Writes data/jvm-weekly.yaml -- the monthly Foojay roundup Artur Skowronski
 * publishes in JVM Weekly, rendered at /jvm-weekly/ and linked from the News
 * menu. Runs once a day from .github/workflows/sync-external-content.yml.
 *
 * NEEDS NO CREDENTIAL, and reads ONE public URL. Frank found this on LinkedIn
 * (linkedin.com/newsletters/jvm-weekly-7097859802881540096), but LinkedIn is a
 * republication and is not machine-readable -- there is no feed, and scraping a
 * logged-out LinkedIn page gets a wall. The newsletter's own home is Substack
 * at www.jvm-weekly.com, which publishes a perfectly ordinary RSS feed carrying
 * the full body of the 20 most recent editions. So this makes exactly one GET
 * per run. DON'T reach for Substack's /api/v1/ endpoints instead: they are
 * undocumented internals, and the per-post one 429s after ~20 calls (measured
 * -- 25 consecutive fetches got rate-limited), where the feed never has.
 *
 * ONLY THE FOOJAY EDITIONS ARE KEPT. JVM Weekly is weekly and mostly about the
 * wider JVM ecosystem; the FIRST edition of each month is the Foojay roundup.
 * August's says so outright: "First edition of the month, so this is the Foojay
 * roundup: the best of what landed on foojay.io in July."
 *
 * IDENTIFYING THAT EDITION IS THE WHOLE PROBLEM, AND THE ANSWER BELONGS
 * UPSTREAM. Artur has already created a Substack SECTION called "Foojay.io
 * Community Newsletter" (id 194419, "Monthly Highlights: What's Happening in
 * the Foojay.io Community") -- but it has no posts assigned to it: every
 * edition reports section_id: null, and the section's own feed 404s. If he ever
 * starts ticking it, that becomes an exact, upstream-owned marker and the
 * heuristic below can go, the same self-retiring shape as the `location:` field
 * scripts/fetch/JavaChampions.java prefers over geocoding. Until then:
 *
 *   Tier 1 -- the edition's TITLE IS a Foojay article's title. Artur leads each
 *            roundup with one article and titles the edition after it, so
 *            "How to Create a Spring Boot Fraud Scoring Service with Geertjan
 *            Wielenga and Zoran Sevarac" is our own post's title plus a byline.
 *            That post is the main article.
 *   Tier 2 -- the edition title NAMES THE AUTHOR of a Foojay article it links
 *            to. '"Diagnosing Your Leyden AOT Cache" with María Arias de Reyna
 *            Domínguez' is not any post's title, but María wrote the Leyden
 *            series it links, so the lead is recoverable.
 *
 * Measured over the 20 editions the feed carries: 5 Foojay editions, all 5
 * found (4 by tier 1, 1 by tier 2) and NOTHING else matched -- the regular
 * editions and the monthly "The Rest of the Story" wrap-ups score zero on both
 * tiers. Rules that were tried and REJECTED, so nobody re-derives them:
 *
 *   - "the description mentions Foojay": the wording is different every time
 *     ("Best of Foojay.io", "Another Foojay editon", "Next Foojay.io edition is
 *     here!") and two of the five say nothing at all.
 *   - "it links at least N Foojay articles": separates, but only just -- the
 *     August roundup links 3 and a regular edition links 2, which is one link
 *     of headroom on a rule that would silently drop a month. It survives here
 *     only as the NEAR-MISS report below, never as an inclusion rule.
 *   - "the first Foojay link is the lead": wrong twice. An edition does not
 *     necessarily link its own lead article at all -- "Where Production Policy
 *     Belongs: Building Eliya in Public" never links the Eliya post -- and the
 *     May edition's first link is background reading, not the lead.
 *
 * WHAT IS STORED IS A REFERENCE, NOT A REPUBLICATION: a title, a date, a link
 * back to Artur's edition, and the SLUGS of the Foojay posts it covers. The
 * summary, byline, thumbnail and read count on /jvm-weekly/ are all derived at
 * build time from our own copy of the post, so there is nothing here to go
 * stale and nothing of Artur's reproduced beyond what a feed exists to carry
 * (the feed is Copyright Artur Skowronski). Republishing an edition's body
 * would need his explicit permission and is deliberately not what this does.
 *
 * NO TIMESTAMP IN THE FILE, and it is rewritten only when the editions
 * themselves change -- the scripts/fetch/JugEvents.java lesson: a "generated
 * at" field moves on every run, so every run would commit and therefore deploy
 * on nothing. Git already records when it changed.
 *
 * Usage:
 *   jbang scripts/fetch/JvmWeekly.java              # sync
 *   jbang scripts/fetch/JvmWeekly.java --dry-run    # print the YAML, write nothing
 *   jbang scripts/fetch/JvmWeekly.java --all        # report every edition, not just the matches
 */
public class JvmWeekly {

    static final Path OUTPUT_FILE = Path.of("data/jvm-weekly.yaml");
    static final Path POSTS_DIR = Path.of("content/posts");
    static final Path AUTHORS_DIR = Path.of("content/authors");

    static final String FEED_URL = "https://www.jvm-weekly.com/feed";

    /**
     * The Substack section Artur created for exactly this and has not yet used.
     * Checked first on every run: the moment it holds posts, they are the
     * authoritative answer and the heuristic stops being consulted for them.
     */
    static final String SECTION_API =
            "https://www.jvm-weekly.com/api/v1/archive?sort=new&limit=50&section_id=194419";

    /** Attributable rather than disguised: who this is, and where to complain. */
    static final String USER_AGENT =
            "foojay.io-jvm-weekly/1.0 (+https://foojay.io/jvm-weekly/; monthly Foojay roundup index, refreshed daily)";

    /**
     * How much of a post's title has to appear in the edition title for tier 1.
     * Not 1.0: Artur re-punctuates ("Thread-Safe" for our "Thread Safe") and
     * drops a subtitle, which costs whole words.
     */
    static final double TITLE_MATCH = 0.8;

    /** An edition linking at least this many Foojay posts, but matching neither
     *  tier, is reported as a near-miss rather than silently dropped. */
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

    static final ObjectMapper JSON = new ObjectMapper();
    static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    // ------------------------------------------------------------------ main --

    public static void main(String[] args) throws Exception {
        boolean dryRun = has(args, "--dry-run");
        boolean reportAll = has(args, "--all");

        Site site = Site.read();
        System.out.println(site.titles.size() + " posts and " + site.authorNames.size()
                + " authors in content/");

        String xml = get(FEED_URL);
        List<Item> items = parseFeed(xml);
        System.out.println(items.size() + " editions in " + FEED_URL);

        Set<String> sectionSlugs = sectionEditions();
        if (sectionSlugs.isEmpty()) {
            System.out.println("Substack section 194419 holds no posts -- falling back to the"
                    + " title/byline match (see the header for why, and for what to ask Artur).");
        } else {
            System.out.println(sectionSlugs.size() + " edition(s) assigned to the Foojay section"
                    + " upstream -- those are taken as authoritative.");
        }

        List<Map<String, Object>> editions = new ArrayList<>();
        List<String> nearMisses = new ArrayList<>();
        int viaSection = 0, viaTitle = 0, viaByline = 0;

        for (Item item : items) {
            List<String> linked = item.foojayPosts(site);
            Match match = site.mainArticle(item.cleanTitle, linked);
            boolean inSection = sectionSlugs.contains(item.slug());

            if (!inSection && match == null) {
                if (linked.size() >= NEAR_MISS_LINKS) {
                    nearMisses.add(item.date + "  " + item.cleanTitle
                            + "  (links " + linked.size() + " Foojay posts, but its title names"
                            + " neither one of our post titles nor one of their authors)");
                } else if (reportAll) {
                    System.out.println("  skip  " + item.date + "  " + item.cleanTitle);
                }
                continue;
            }
            if (inSection) viaSection++;
            else if (match.tier == 1) viaTitle++;
            else viaByline++;

            String main = match == null ? null : match.slug;
            List<String> also = linked.stream().filter(s -> !s.equals(main)).toList();

            Map<String, Object> edition = new LinkedHashMap<>();
            edition.put("date", item.date);
            edition.put("title", item.cleanTitle);
            if (item.volume != null) edition.put("volume", item.volume);
            edition.put("url", item.link);
            if (item.subtitle != null) edition.put("subtitle", item.subtitle);
            // The main article is a SLUG. Everything a reader sees about it --
            // the summary, the byline, the thumbnail, the read count -- is
            // resolved from our own copy of that post by the layout, so there
            // is no second copy of a description here to drift.
            if (main != null) edition.put("main", main);
            if (!also.isEmpty()) edition.put("articles", also);
            editions.add(edition);

            System.out.println("  keep  " + item.date + "  " + item.cleanTitle);
            System.out.println("          main: " + (main == null ? "-- none resolved --" : main)
                    + (match == null ? " (upstream section)" : "  [" + match.how + "]"));
            if (!also.isEmpty()) System.out.println("          also covers " + also.size() + " post(s)");
        }

        System.out.println();
        System.out.println(editions.size() + " Foojay edition(s): " + viaSection + " from the"
                + " upstream section, " + viaTitle + " by title match, " + viaByline + " by byline match");

        if (!nearMisses.isEmpty()) {
            // Reported, never guessed at -- the same posture as
            // fetch/DiscoverJugCalendars.java and cleanup/Descriptions.java. A
            // Foojay edition that neither tier catches would otherwise vanish
            // with nothing to say so.
            System.out.println();
            System.out.println("NEEDS A HUMAN -- " + nearMisses.size() + " edition(s) look like a"
                    + " Foojay roundup but no main article could be resolved:");
            for (String s : nearMisses) System.out.println("  " + s);
            System.out.println("  If one of these IS a roundup, the durable fix is upstream: ask"
                    + " Artur to file it under his 'Foojay.io Community Newsletter' Substack section.");
        }

        String yaml = render(editions);
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

    // ------------------------------------------------------------------ feed --

    /** One edition of the newsletter. */
    record Item(String date, String rawTitle, String cleanTitle, Integer volume,
                String link, String subtitle, String body) {

        /** The Substack post slug, which is what the section API returns. */
        String slug() {
            String s = link.replaceAll("[?#].*$", "").replaceAll("/$", "");
            return s.substring(s.lastIndexOf('/') + 1);
        }

        /** Foojay posts this edition links, in document order, deduplicated,
         *  and only those we actually have -- a link to a post that is not in
         *  content/ cannot be summarised, so it is not a covered article. */
        List<String> foojayPosts(Site site) {
            List<String> out = new ArrayList<>();
            Matcher m = FOOJAY_POST.matcher(body);
            while (m.find()) {
                String slug = site.resolve(m.group(1));
                if (slug != null && !out.contains(slug)) out.add(slug);
            }
            return out;
        }
    }

    static List<Item> parseFeed(String xml) throws Exception {
        // Deliberately NOT namespace-aware: the body lives in <content:encoded>,
        // and with namespaces off that is just a tag called "content:encoded".
        var factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        Document doc = factory.newDocumentBuilder()
                .parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

        List<Item> items = new ArrayList<>();
        NodeList nodes = doc.getElementsByTagName("item");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element el = (Element) nodes.item(i);
            String rawTitle = child(el, "title");
            String link = child(el, "link");
            if (rawTitle == null || link == null) continue;

            Integer volume = null;
            String cleanTitle = rawTitle;
            Matcher vm = VOLUME_SUFFIX.matcher(rawTitle);
            if (vm.find()) {
                volume = Integer.valueOf(vm.group(1));
                cleanTitle = rawTitle.substring(0, vm.start()).trim();
            }

            String body = child(el, "content:encoded");
            items.add(new Item(
                    date(child(el, "pubDate")),
                    rawTitle,
                    cleanTitle,
                    volume,
                    link,
                    child(el, "description"),
                    body == null ? "" : body));
        }
        return items;
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

    /** RFC-1123 pubDate -> an ISO date. The time of day is noise on a monthly. */
    static String date(String pubDate) {
        if (pubDate == null) return null;
        try {
            return OffsetDateTime.parse(pubDate, DateTimeFormatter.RFC_1123_DATE_TIME)
                    .withOffsetSameInstant(ZoneOffset.UTC)
                    .toLocalDate().toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * The editions Artur has filed under his Foojay Substack section. Empty
     * today, and FAILS OPEN: this is an undocumented endpoint on somebody
     * else's site, and a daily sync must not go red because it moved. A failure
     * simply means the heuristic decides, which is what happens anyway.
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

    // ------------------------------------------------------------ our content --

    /** What content/ knows: every post's slug, title and author display names. */
    static class Site {
        final Map<String, String> titles = new LinkedHashMap<>();      // slug -> title
        final Map<String, List<String>> bylines = new LinkedHashMap<>(); // slug -> display names
        final Map<String, String> aliases = new HashMap<>();           // any known path slug -> real slug
        final Map<String, String> authorNames = new LinkedHashMap<>();  // author slug -> display name

        /**
         * A URL slug -> the post's actual bundle folder. WordPress slugs are not
         * always ours: /today/foojay-podcast-94-more-than-a-blog-.../ is an
         * `aliases:` entry on the bundle called foojay-podcast-94, and an
         * edition links whichever one foojay.io served it.
         */
        String resolve(String urlSlug) {
            if (titles.containsKey(urlSlug)) return urlSlug;
            return aliases.get(urlSlug);
        }

        /** Tier 1 then tier 2; null when neither fires. */
        Match mainArticle(String editionTitle, List<String> linked) {
            Set<String> edition = keywords(editionTitle);
            if (edition.isEmpty()) return null;

            // Tier 1: the edition is titled after one of our posts. Scored over
            // ALL posts, not just the linked ones -- an edition does not
            // necessarily link the article it leads with.
            String best = null;
            double bestScore = 0;
            for (Map.Entry<String, String> e : titles.entrySet()) {
                Set<String> post = keywords(e.getValue());
                if (post.size() < 2) continue;  // too short to be evidence
                long shared = post.stream().filter(edition::contains).count();
                double score = (double) shared / post.size();
                if (score >= TITLE_MATCH && score > bestScore) {
                    bestScore = score;
                    best = e.getKey();
                }
            }
            if (best != null) {
                return new Match(best, 1, String.format("title match %.0f%%", bestScore * 100));
            }

            // Tier 2: the edition title names the AUTHOR of a post it links.
            // Document order, so the lead beats a later mention by the same person.
            for (String slug : linked) {
                for (String name : bylines.getOrDefault(slug, List.of())) {
                    if (name.length() > 4 && containsIgnoreCase(editionTitle, name)) {
                        return new Match(slug, 2, "byline match: " + name);
                    }
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
                    // The BUNDLE FOLDER NAME, which is the key the layout looks
                    // a post up by (partials/post-index.html) and the key
                    // `related_posts` uses. Deliberately not `slug:` frontmatter
                    // even though hugo.toml's :slugorcontentbasename would prefer
                    // it: no post in content/ carries one that differs (checked,
                    // 0 of 2163), and if one ever did, keying on it here while
                    // post-index.html keys on the folder would put a slug in this
                    // file that the page silently cannot resolve.
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
     * The words of a title worth comparing: 4+ letters, lowercased, stopwords
     * dropped. Anything shorter ("AOT", "the", "is") is either noise or too
     * common to distinguish two of 2100 posts.
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

    static boolean containsIgnoreCase(String haystack, String needle) {
        return haystack.toLowerCase(Locale.ROOT).contains(needle.toLowerCase(Locale.ROOT));
    }

    // ---------------------------------------------------------------- output --

    static String render(List<Map<String, Object>> editions) {
        String header = """
                # The monthly Foojay roundup in Artur Skowronski's JVM Weekly newsletter --
                # generated automatically by scripts/fetch/JvmWeekly.java from the public
                # RSS feed at https://www.jvm-weekly.com/feed, refreshed once a day by
                # .github/workflows/sync-external-content.yml.
                #
                # DO NOT EDIT THIS FILE BY HAND: it is regenerated on every sync and any
                # manual change here is overwritten the next time that runs.
                #
                # `main` and `articles` are SLUGS of our own posts. Everything a reader sees
                # about them on /jvm-weekly/ -- summary, byline, thumbnail, read count -- is
                # resolved from content/posts/ at build time, so nothing here can go stale.
                # Newest edition first.

                """;
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setSplitLines(false);
        return header + new Yaml(options).dump(editions);
    }

    // ------------------------------------------------------------------ HTTP --

    static String get(String url) throws IOException, InterruptedException {
        IOException last = null;
        for (int attempt = 1; attempt <= 2; attempt++) {
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
            } catch (IOException e) {
                last = e;
            }
            if (attempt == 1) Thread.sleep(2000);
        }
        throw last;
    }

    static boolean has(String[] args, String flag) {
        return Arrays.asList(args).contains(flag);
    }
}
