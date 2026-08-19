///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.jsoup:jsoup:1.17.2
//DEPS com.vladsch.flexmark:flexmark-html2md-converter:0.64.8
//SOURCES ../shared/HtmlToMarkdown.java
//JAVA 17+

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts foojay.io's glossary ("pedia") entries into content/pedia/<slug>.md.
 *
 * WHY THIS EXISTS AGAIN. The original one-off converter was retired once
 * content/pedia/ was done and the section was declared hand-maintained -- and
 * then the live site kept growing. An audit of foojay.io's own
 * terminology-sitemap.xml found 47 published entries against this repo's 30:
 * SEVENTEEN glossary pages, live and linked from the entries that DID make it,
 * simply did not exist here. That is a content gap no template could paper
 * over, and it is not the kind of thing to notice twice.
 *
 * So this is the pedia twin of transfer/Posts.java and transfer/Authors.java: it
 * lives in transfer/ because it reads the WordPress site and therefore dies at
 * cutover.
 *
 * IT ADDS, IT DOES NOT OVERWRITE -- and that is the opposite default from the
 * other transfer/ scripts, deliberately. The 30 entries already in
 * content/pedia/ have been REVIEWED AND EDITED BY HAND since they were
 * converted; a re-scrape in the usual style would silently replace that work
 * with whatever WordPress currently holds. So the default run only creates
 * files that do not exist yet, and an existing entry is never written unless
 * --refresh is passed explicitly.
 *
 * `--diff` is the middle ground and the one to reach for routinely: it reports
 * where an existing entry has drifted from the live page WITHOUT writing
 * anything, so an upstream edit can be merged by hand instead of steamrolling
 * the local one. Run it before cutover to see what, if anything, changed
 * upstream.
 *
 * WHY THE WP INDEX PAGE IS NOT THE SOURCE OF TRUTH. /pedia/ renders 31 of the 47
 * entries -- it is paginated, and its default page size is what hid the gap for
 * months. `?per-page=60&sort=term` shows all of them, but the sitemap is the
 * honest list and needs no query string to be right, so DISCOVERY_SITEMAP is
 * tried first and the index page is only the fallback.
 *
 * SELECTORS were read off the live markup (2026-08), not guessed. foojay's
 * terminology template uses stable BEM names: the body is everything inside
 * `.terminology-inner__content` except the title box, the view counter and the
 * share buttons, which is what SELECTOR_BODY_DROP removes. There is no
 * <meta name="description"> on these pages -- Yoast emits only og:description --
 * so that is where the description comes from.
 *
 * Usage:
 *   jbang scripts/transfer/Pedia.java              (add entries that are missing here)
 *   jbang scripts/transfer/Pedia.java --dry-run    (report, write nothing)
 *   jbang scripts/transfer/Pedia.java --diff       (existing entries: report drift, write nothing)
 *   jbang scripts/transfer/Pedia.java --refresh    (existing entries: OVERWRITE -- discards hand edits)
 *   jbang scripts/transfer/Pedia.java --url https://foojay.io/pedia/virtual-threads/
 */
public class Pedia {

    static final String BASE_URL = "https://foojay.io";
    static final String DISCOVERY_SITEMAP = BASE_URL + "/terminology-sitemap.xml";
    /** Fallback only. per-page=60 because the default page size hides half the glossary. */
    static final String DISCOVERY_INDEX = BASE_URL + "/pedia/?per-page=60&sort=term";

    /** WP Engine's WAF 403s a bare Java user agent, same as transfer/LegacyViews.java. */
    static final String USER_AGENT =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 "
            + "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
    static final int REQUEST_TIMEOUT_MS = 30_000;
    /** One at a time with a pause, same courtesy as fetch/JugEvents.java. */
    static final long REQUEST_PAUSE_MS = 400;

    static final Path CONTENT_DIR = Path.of("content", "pedia");

    static final String SELECTOR_TITLE = "h1.terminology-inner__title";
    static final String SELECTOR_BODY = ".terminology-inner__content";
    /**
     * Everything in the content column that is chrome rather than the definition:
     * the "OpenJDK Terminology" back link and heading, the "Unique Views: 78
     * since July 2026" counter (this site has its own), and the share buttons.
     */
    static final String SELECTOR_BODY_DROP =
            ".terminology-inner__title-box, .terminology-inner__social-box, "
            + ".post-views, .entry-meta, .terminology-inner__cta-box";

    static final Pattern PEDIA_SLUG = Pattern.compile("/pedia/([a-z0-9][a-z0-9-]*)/?$");
    /** Yoast's " - foojay" tail on og:title. */
    static final Pattern SITE_SUFFIX = Pattern.compile("\\s*[-–—|]\\s*foojay(\\.io)?\\s*$", Pattern.CASE_INSENSITIVE);

    static boolean dryRun = false;
    /** Report drift on entries that already exist here. Never writes. */
    static boolean diff = false;
    /** Overwrite entries that already exist here. Discards hand edits -- opt in. */
    static boolean refresh = false;
    static String singleUrl = null;

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--dry-run" -> dryRun = true;
                case "--diff" -> diff = true;
                case "--refresh" -> refresh = true;
                case "--url" -> singleUrl = args[++i];
                default -> {
                    System.err.println("Unknown argument: " + args[i]);
                    System.exit(2);
                }
            }
        }
        Files.createDirectories(CONTENT_DIR);

        List<String> urls = singleUrl != null ? List.of(singleUrl) : discover();
        System.out.printf("%d entr%s to consider%n", urls.size(), urls.size() == 1 ? "y" : "ies");

        int created = 0, overwritten = 0, drifted = 0, identical = 0, frozen = 0, left = 0, failed = 0;
        List<String> problems = new ArrayList<>();
        List<String> drift = new ArrayList<>();

        for (String url : urls) {
            String slug = slugOf(url);
            if (slug == null) { problems.add("no slug in " + url); failed++; continue; }
            Path file = CONTENT_DIR.resolve(slug + ".md");
            boolean exists = Files.isRegularFile(file);

            // An entry that is already here is only fetched when there is a
            // reason to: --diff wants to compare it, --refresh wants to replace
            // it. Otherwise it is left alone WITHOUT a request, which also keeps
            // the routine run down to one fetch per genuinely new entry.
            if (exists && !diff && !refresh) { left++; continue; }
            if (exists && isFrozen(file)) {
                System.out.printf("  frozen, left alone: %s%n", slug);
                frozen++;
                continue;
            }

            try {
                Entry e = scrape(url, slug);
                if (e.body.isBlank()) { problems.add("empty body: " + url); failed++; continue; }
                String rendered = render(e);

                if (!exists) {
                    if (!dryRun) Files.writeString(file, rendered);
                    System.out.printf("  CREATED %s (%d chars)%n", slug, e.body.length());
                    created++;
                } else if (rendered.equals(Files.readString(file))) {
                    identical++;
                } else if (refresh) {
                    if (!dryRun) Files.writeString(file, rendered);
                    System.out.printf("  OVERWRITTEN %s%n", slug);
                    overwritten++;
                } else {
                    drifted++;
                    drift.add(describeDrift(slug, Files.readString(file), rendered));
                }
            } catch (Exception ex) {
                problems.add(slug + ": " + ex);
                failed++;
            }
            Thread.sleep(REQUEST_PAUSE_MS);
        }

        System.out.printf("%ncreated %d | overwritten %d | identical %d | drifted %d | frozen %d | left alone %d | failed %d%s%n",
                created, overwritten, identical, drifted, frozen, left, failed,
                dryRun ? "   (DRY RUN, nothing written)" : "");

        if (!drift.isEmpty()) {
            System.out.printf("%n%d existing entr%s differ%s from the live page. NOTHING was written --%n"
                    + "these files have been edited by hand here, so merge anything worth keeping%n"
                    + "yourself rather than running --refresh over them:%n",
                    drift.size(), drift.size() == 1 ? "y" : "ies", drift.size() == 1 ? "s" : "");
            drift.forEach(System.out::println);
        }
        if (!problems.isEmpty()) {
            System.out.println("\nProblems -- these need a human:");
            problems.forEach(p -> System.out.println("  " + p));
        }
    }

    /**
     * Every entry URL. The sitemap is authoritative and complete; the paginated
     * index is the fallback, and it needs its query string to show everything.
     */
    static List<String> discover() throws IOException {
        Set<String> urls = new LinkedHashSet<>();
        try {
            Document sm = Jsoup.connect(DISCOVERY_SITEMAP)
                    .userAgent(USER_AGENT).timeout(REQUEST_TIMEOUT_MS)
                    .maxBodySize(0).parser(org.jsoup.parser.Parser.xmlParser()).get();
            for (Element loc : sm.select("url > loc")) {
                String u = loc.text().trim();
                if (PEDIA_SLUG.matcher(u).find()) urls.add(u);
            }
            if (!urls.isEmpty()) System.out.printf("discovered %d entries from %s%n", urls.size(), DISCOVERY_SITEMAP);
        } catch (IOException ex) {
            System.out.printf("sitemap unavailable (%s) -- falling back to the index page%n", ex);
        }
        if (urls.isEmpty()) {
            Document idx = Jsoup.connect(DISCOVERY_INDEX)
                    .userAgent(USER_AGENT).timeout(REQUEST_TIMEOUT_MS).maxBodySize(0).get();
            for (Element a : idx.select("a[href*=/pedia/]")) {
                String u = a.absUrl("href");
                if (PEDIA_SLUG.matcher(u).find()) urls.add(u);
            }
            System.out.printf("discovered %d entries from %s%n", urls.size(), DISCOVERY_INDEX);
        }
        List<String> out = new ArrayList<>(urls);
        out.sort(String::compareTo);
        return out;
    }

    static Entry scrape(String url, String slug) throws IOException {
        Document doc = Jsoup.connect(url)
                .userAgent(USER_AGENT).timeout(REQUEST_TIMEOUT_MS).maxBodySize(0).get();

        Entry e = new Entry();
        e.slug = slug;
        e.title = HtmlToMarkdown.normalizeBrandName(firstNonBlank(
                text(doc.selectFirst(SELECTOR_TITLE)),
                SITE_SUFFIX.matcher(meta(doc, "og:title")).replaceAll(""),
                SITE_SUFFIX.matcher(doc.title()).replaceAll("")));

        // These pages carry no <meta name=description> -- only og:description --
        // and it is Yoast's truncated body excerpt, so it gets the same run-on
        // repair every other scraped description gets.
        e.description = HtmlToMarkdown.repairRunOnSentences(
                HtmlToMarkdown.normalizeBrandName(meta(doc, "og:description")));

        Element body = doc.selectFirst(SELECTOR_BODY);
        if (body == null) throw new IOException("no " + SELECTOR_BODY + " on the page");
        body.select(SELECTOR_BODY_DROP).remove();

        // Images live under static/images/pedia/<slug>/ rather than beside the
        // markdown: content/pedia entries are single FILES, not page bundles, so
        // there is no folder to co-locate anything in. resource-url.html already
        // resolves a root-relative path for exactly this case.
        HtmlToMarkdown.Options opts = new HtmlToMarkdown.Options(
                Path.of("static", "images", "pedia"), "/images/pedia/",
                "foojay.io", USER_AGENT, REQUEST_TIMEOUT_MS);
        e.body = HtmlToMarkdown.convert(body, opts, slug).markdown.strip();
        return e;
    }

    /**
     * The file, byte for byte. Matches what content/pedia already holds -- four
     * keys and the body -- so a re-run over an entry converted by the original
     * script is a no-op rather than a reformat.
     */
    static String render(Entry e) {
        return "---\n"
                + "title: " + yaml(e.title) + "\n"
                + "description: " + yaml(e.description) + "\n"
                + "url: \"/pedia/" + e.slug + "/\"\n"
                + "frozen: false\n"
                + "---\n\n"
                + e.body + "\n";
    }

    /**
     * A one-screen summary of how the local file and the live page differ: which
     * frontmatter values changed, and how the body length moved. Deliberately not
     * a full diff -- the point is to say "look at this one", not to reproduce
     * `git diff` badly.
     */
    static String describeDrift(String slug, String local, String remote) {
        StringBuilder sb = new StringBuilder("  " + slug + "\n");
        for (String key : new String[] {"title", "description"}) {
            String a = frontmatterValue(local, key), b = frontmatterValue(remote, key);
            if (!a.equals(b)) {
                sb.append("      ").append(key).append(" here : ").append(trim(a)).append('\n');
                sb.append("      ").append(key).append(" live : ").append(trim(b)).append('\n');
            }
        }
        int la = bodyOf(local).length(), lb = bodyOf(remote).length();
        if (!bodyOf(local).equals(bodyOf(remote))) {
            sb.append(String.format("      body differs: %d chars here, %d live (%+d)%n", la, lb, lb - la));
        }
        return sb.toString().stripTrailing();
    }

    static String frontmatterValue(String file, String key) {
        Matcher m = Pattern.compile("^" + key + ":\\s*\"(.*)\"\\s*$", Pattern.MULTILINE).matcher(file);
        return m.find() ? m.group(1) : "";
    }

    static String bodyOf(String file) {
        int end = file.indexOf("\n---", 3);
        return end < 0 ? file : file.substring(end + 4).strip();
    }

    static String trim(String s) { return s.length() <= 110 ? s : s.substring(0, 107) + "..."; }

    /** Hand-edited entries opt out, the same way they do in the other transfer/ scripts. */
    static boolean isFrozen(Path file) throws IOException {
        String head = Files.readString(file);
        int end = head.indexOf("\n---", 3);
        return end > 0 && head.substring(0, end).contains("frozen: true");
    }

    static String slugOf(String url) {
        Matcher m = PEDIA_SLUG.matcher(url);
        return m.find() ? m.group(1) : null;
    }

    static String text(Element el) { return el == null ? "" : el.text().trim(); }

    static String meta(Document doc, String property) {
        Element el = doc.selectFirst("meta[property=" + property + "]");
        if (el == null) el = doc.selectFirst("meta[name=" + property + "]");
        return el == null ? "" : el.attr("content").trim();
    }

    static String firstNonBlank(String... values) {
        for (String v : values) if (v != null && !v.isBlank()) return v.trim();
        return "";
    }

    static String yaml(String s) {
        if (s == null) s = "";
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ") + "\"";
    }

    static class Entry {
        String slug, title, description, body;
    }
}
