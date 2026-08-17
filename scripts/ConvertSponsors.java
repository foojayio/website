///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.jsoup:jsoup:1.17.2
//SOURCES HtmlToMarkdown.java
//JAVA 17+

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Converts foojay.io's sponsor pages into Hugo content bundles under
 * content/sponsors/.
 *
 * Source of truth is the live WordPress site: the index at /our-sponsors/
 * lists every sponsor with its tier, and each card links to a profile page at
 * /sponsor/<slug>/ carrying the logo, tagline, about text, topic tags and
 * social links. Both are scraped here.
 *
 * Why content bundles (and not a data/sponsors.yaml)? Sponsors are
 * first-class pages: each keeps its legacy URL (/sponsor/<slug>/, see the
 * `sponsors` entry in hugo.toml's [permalinks]), has a rendered profile page
 * (themes/foojay/layouts/sponsors/single.html) with an About body that is real
 * prose, and ships its logo as a co-located page resource. A data file
 * produces no pages and no URLs, so it can't carry any of that.
 *
 * SPONSOR -> ARTICLE LINKING IS OURS, NOT WORDPRESS'S. WordPress works out
 * which articles "belong" to a sponsor through a plugin relation we have no
 * access to. Here the link is explicit and hand-maintained: each sponsor's
 * frontmatter carries an `authors:` list of author slugs (the folder names
 * under content/authors/), and the profile template lists every post written
 * by any of them. That list is the ONE thing on a sponsor page that this
 * script does not own -- it is read out of the existing index.md and written
 * back unchanged on every re-run, so scraping never clobbers it. Everything
 * derived from it (article count, podcast count, topics covered) is computed
 * at build time by the template, so it never goes stale.
 *
 * Usage:
 *   jbang scripts/ConvertSponsors.java
 *   jbang scripts/ConvertSponsors.java --url https://foojay.io/sponsor/coderabbit/   (single sponsor, for tuning selectors)
 *
 * Selectors below were read off the live markup (2026-08) rather than guessed,
 * unlike the older Convert* scripts -- foojay's sponsor templates use stable
 * BEM class names (sponsors__*, partner-card__*, sponsor-main__*).
 *
 * Idempotent: re-running updates existing bundles in place (found by slug
 * wherever they live), reuses an already-downloaded logo, and preserves both
 * `authors:` and any bundle whose frontmatter says `frozen: true`.
 */
public class ConvertSponsors {

    static final String BASE_URL = "https://foojay.io";
    static final String INDEX_URL = BASE_URL + "/our-sponsors/";
    static final Path OUTPUT_DIR = Path.of("content/sponsors");
    static final int REQUEST_TIMEOUT_MS = 20_000;
    static final int POLITE_DELAY_MS = 250;
    static final String USER_AGENT = "foojay-hugo-migration-bot/1.0";

    // --- index page (/our-sponsors/) ---
    static final String SELECTOR_INDEX_CARD = "article.sponsors__card";
    static final String SELECTOR_INDEX_PROFILE_LINK = "a.sponsors__profile";
    // --- profile page (/sponsor/<slug>/) ---
    static final String SELECTOR_PROFILE_LOGO = "img.partner-card__logo";
    static final String SELECTOR_PROFILE_NAME = ".partner-card__title";
    static final String SELECTOR_PROFILE_TAGLINE = ".partner-card__description";
    static final String SELECTOR_PROFILE_BADGE = ".partner-card__badge";
    static final String SELECTOR_PROFILE_SOCIALS = "a.partner-card__social";
    static final String SELECTOR_PROFILE_ABOUT = ".sponsor-main__about .sponsor-main__text";
    static final String SELECTOR_PROFILE_TOPICS = ".sponsor-main__topics .sponsor-main__tag";

    static final Pattern SPONSOR_SLUG = Pattern.compile("/sponsor/([^/]+)/?$");
    static final Pattern TIER_CLASS = Pattern.compile("(?:sponsors__card|partner-card__badge)--(gold|silver|bronze|platinum)");
    static final Pattern BG_COLOR = Pattern.compile("background-color:\\s*(#[0-9a-fA-F]{3,8}|[a-z]+)");

    public static void main(String[] args) throws Exception {
        Files.createDirectories(OUTPUT_DIR);

        String singleUrl = null;
        for (int i = 0; i < args.length; i++) {
            if ("--url".equals(args[i]) && i + 1 < args.length) singleUrl = args[++i];
        }

        Map<String, String> tierByUrl = new LinkedHashMap<>();
        if (singleUrl != null) {
            tierByUrl.put(singleUrl, null); // tier comes off the profile page's own badge
        } else {
            tierByUrl.putAll(collectSponsorUrls());
            System.out.println("Found " + tierByUrl.size() + " sponsor URLs on " + INDEX_URL);
        }

        int written = 0, skipped = 0, failed = 0;
        for (Map.Entry<String, String> e : tierByUrl.entrySet()) {
            String url = e.getKey();
            String slug = slugFromUrl(url);
            if (isFrozen(slug)) {
                System.out.println("  skipping frozen sponsor: " + slug);
                skipped++;
                continue;
            }
            try {
                SponsorData d = scrapeSponsor(url, slug, e.getValue());
                writeSponsor(d);
                written++;
                sleepPolite();
            } catch (Exception ex) {
                System.err.println("FAILED: " + url + " -> " + ex.getMessage());
                failed++;
            }
        }
        System.out.printf("Done. written=%d skipped(frozen)=%d failed=%d%n", written, skipped, failed);
        if (written > 0) {
            System.out.println();
            System.out.println("Reminder: `authors:` in each content/sponsors/<slug>/index.md is hand-maintained");
            System.out.println("(it drives the article list on the profile page) and was preserved as-is.");
        }
    }

    /**
     * Reads /our-sponsors/ and returns profile URL -> tier, in the page's own
     * order (gold, then silver, then bronze). The tier comes from the card's
     * `sponsors__card--<tier>` modifier class, which is the only place the
     * index states it in machine-readable form -- the visible badge is an emoji.
     */
    static Map<String, String> collectSponsorUrls() throws IOException {
        Map<String, String> out = new LinkedHashMap<>();
        // maxBodySize(0) = unlimited. NOT optional: /our-sponsors/ is a ~3MB
        // document (the theme inlines base64 tier icons), and Jsoup's 1MB
        // default truncates it SILENTLY -- mid-page, with no error -- which cut
        // the parse off after the gold tier and quietly lost every silver and
        // bronze sponsor. Same reason it's set on the profile fetch below.
        Document doc = Jsoup.connect(INDEX_URL)
                .userAgent(USER_AGENT)
                .timeout(REQUEST_TIMEOUT_MS)
                .maxBodySize(0)
                .get();

        for (Element card : doc.select(SELECTOR_INDEX_CARD)) {
            Element link = card.selectFirst(SELECTOR_INDEX_PROFILE_LINK);
            if (link == null) continue;
            String href = link.absUrl("href");
            if (!SPONSOR_SLUG.matcher(href).find()) continue;
            out.put(href, tierFromClasses(card.className()));
        }
        return out;
    }

    static SponsorData scrapeSponsor(String url, String slug, String tierFromIndex) throws IOException {
        Document doc = Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .timeout(REQUEST_TIMEOUT_MS)
                .maxBodySize(0)
                .get();

        SponsorData d = new SponsorData();
        d.slug = slug;
        d.bundleDir = bundleDirFor(slug);
        // Carried over from the existing file: ours, not WordPress's (see class javadoc).
        d.authors = existingAuthors(d.bundleDir);

        d.name = firstNonBlank(
                textOrNull(doc.selectFirst(SELECTOR_PROFILE_NAME)),
                metaContent(doc, "og:title").replaceAll("\\s*-\\s*foojay$", ""));
        d.tagline = textOrEmpty(doc.selectFirst(SELECTOR_PROFILE_TAGLINE));
        d.description = attrContent(doc, "meta[name=description]");

        Element badge = doc.selectFirst(SELECTOR_PROFILE_BADGE);
        d.tier = firstNonBlank(
                tierFromIndex,
                badge != null ? tierFromClasses(badge.className()) : null,
                // last resort: the badge text reads "🏆 Gold Sponsor"
                badge != null ? tierFromText(badge.text()) : null);

        Element logo = doc.selectFirst(SELECTOR_PROFILE_LOGO);
        if (logo != null) {
            d.logoBackground = styleBackgroundColor(logo.attr("style"));
            d.logo = localizeLogo(logo.absUrl("src"), d.bundleDir);
        }

        for (Element a : doc.select(SELECTOR_PROFILE_SOCIALS)) {
            classifySocial(d, a.absUrl("href"), a.text());
        }

        for (Element tag : doc.select(SELECTOR_PROFILE_TOPICS)) {
            String t = tag.text().trim();
            if (!t.isEmpty() && !d.topics.contains(t)) d.topics.add(t);
        }

        Element about = doc.selectFirst(SELECTOR_PROFILE_ABOUT);
        if (about != null) {
            // Images in the About text land in the sponsor's own bundle as bare
            // filenames, same as post heroes -- nothing hotlinks WP after cutover.
            HtmlToMarkdown.Options opts = new HtmlToMarkdown.Options(
                    d.bundleDir, "", "foojay.io", USER_AGENT, REQUEST_TIMEOUT_MS);
            d.body = HtmlToMarkdown.convert(about, opts, "").markdown;
        } else {
            System.err.println("  WARNING: no About text matched for " + url);
        }

        return d;
    }

    /**
     * Routes a sponsor social link to the right field. Unlike the author cards,
     * these links DO carry a text label ("LinkedIn", "Bluesky", "X", and the
     * bare domain for the company site), so the label is used first and the
     * host only as a fallback. The label that is neither a known platform nor
     * empty is the company website (rendered as e.g. "azul.com").
     */
    static void classifySocial(SponsorData d, String href, String label) {
        if (href == null || href.isBlank()) return;
        String h = href.toLowerCase(Locale.ROOT);
        String l = label == null ? "" : label.trim().toLowerCase(Locale.ROOT);

        if (h.contains("linkedin.com") || l.equals("linkedin")) { if (d.linkedin == null) d.linkedin = href; }
        else if (h.contains("youtube.com") || h.contains("youtu.be") || l.equals("youtube")) { if (d.youtube == null) d.youtube = href; }
        else if (h.contains("bsky.app") || l.equals("bluesky")) { if (d.bluesky == null) d.bluesky = href; }
        else if (h.contains("github.com") || l.equals("github")) { if (d.github == null) d.github = href; }
        else if (isMastodon(h) || l.equals("mastodon")) { if (d.mastodon == null) d.mastodon = href; }
        else if (h.contains("twitter.com") || h.contains("x.com") || l.equals("x") || l.equals("twitter")) { if (d.twitter == null) d.twitter = href; }
        else if (d.website == null) {
            d.website = href;
            d.websiteLabel = label != null && !label.isBlank() ? label.trim() : hostOf(href);
        }
    }

    static boolean isMastodon(String lowerHref) {
        return lowerHref.contains("mastodon") || lowerHref.contains("fosstodon") || lowerHref.contains(".social/");
    }

    static String hostOf(String url) {
        return url.replaceFirst("^https?://", "").replaceFirst("^www\\.", "").replaceFirst("/.*$", "");
    }

    /** "sponsors__card sponsors__card--gold" -> "gold". */
    static String tierFromClasses(String classAttr) {
        if (classAttr == null) return null;
        Matcher m = TIER_CLASS.matcher(classAttr);
        return m.find() ? m.group(1) : null;
    }

    /** "🏆 Gold Sponsor" -> "gold". */
    static String tierFromText(String text) {
        if (text == null) return null;
        String t = text.toLowerCase(Locale.ROOT);
        for (String tier : List.of("platinum", "gold", "silver", "bronze")) {
            if (t.contains(tier)) return tier;
        }
        return null;
    }

    /** The `background-color` out of an inline style attribute, or "". */
    static String styleBackgroundColor(String style) {
        if (style == null) return "";
        Matcher m = BG_COLOR.matcher(style);
        return m.find() ? m.group(1) : "";
    }

    /**
     * Downloads the logo into the sponsor's bundle as logo.<ext> and returns the
     * bare filename (a page-bundle resource). Reuses an already-downloaded file;
     * falls back to the remote URL if the download fails, so the page still renders.
     */
    static String localizeLogo(String remoteUrl, Path bundleDir) {
        if (remoteUrl == null || remoteUrl.isBlank()) return "";
        Path existing = findInDir(bundleDir, "logo");
        if (existing != null) return existing.getFileName().toString();
        try {
            Connection.Response res = Jsoup.connect(remoteUrl)
                    .userAgent(USER_AGENT)
                    .timeout(REQUEST_TIMEOUT_MS)
                    .ignoreContentType(true)
                    .maxBodySize(0)
                    .execute();
            String name = "logo" + extensionFor(remoteUrl, res.contentType());
            Files.createDirectories(bundleDir);
            Files.write(bundleDir.resolve(name), res.bodyAsBytes());
            return name;
        } catch (IOException e) {
            System.err.println("  logo download failed: " + e.getMessage() + " (keeping remote URL)");
            return remoteUrl;
        }
    }

    /** A regular file in dir whose name without extension equals baseName, or null. */
    static Path findInDir(Path dir, String baseName) {
        if (!Files.isDirectory(dir)) return null;
        try (Stream<Path> s = Files.list(dir)) {
            return s.filter(Files::isRegularFile)
                    .filter(p -> stripExtension(p.getFileName().toString()).equals(baseName))
                    .findFirst().orElse(null);
        } catch (IOException e) {
            return null;
        }
    }

    static String extensionFor(String url, String contentType) {
        if (contentType != null) {
            String ct = contentType.toLowerCase(Locale.ROOT);
            if (ct.contains("svg")) return ".svg";
            if (ct.contains("png")) return ".png";
            if (ct.contains("gif")) return ".gif";
            if (ct.contains("webp")) return ".webp";
            if (ct.contains("jpeg") || ct.contains("jpg")) return ".jpg";
        }
        String clean = url.replaceAll("[?#].*$", "").toLowerCase(Locale.ROOT);
        int dot = clean.lastIndexOf('.');
        int slash = clean.lastIndexOf('/');
        if (dot > slash && dot < clean.length() - 1) {
            String ext = clean.substring(dot);
            if (ext.matches("\\.(png|gif|webp|svg|jpe?g)")) return ext.equals(".jpeg") ? ".jpg" : ext;
        }
        return ".png";
    }

    /**
     * Reads the `authors:` YAML list out of an existing bundle. This is the
     * hand-maintained sponsor<->article link (see class javadoc); losing it on a
     * re-run would silently empty a sponsor's article list, so it is parsed back
     * out rather than regenerated. Simple line scan: the block is always written
     * by writeSponsor() below as `authors:` followed by `  - "slug"` lines.
     */
    static List<String> existingAuthors(Path bundleDir) {
        List<String> authors = new ArrayList<>();
        Path md = bundleDir.resolve("index.md");
        if (!Files.isRegularFile(md)) return authors;
        try {
            boolean inBlock = false;
            for (String line : Files.readAllLines(md)) {
                if (line.startsWith("authors:")) {
                    inBlock = true;
                    continue;
                }
                if (inBlock) {
                    String t = line.trim();
                    if (t.startsWith("- ")) {
                        authors.add(t.substring(2).trim().replaceAll("^\"|\"$", ""));
                    } else {
                        break; // next key (or the closing ---) ends the list
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("  could not read existing authors from " + md + ": " + e.getMessage());
        }
        return authors;
    }

    static boolean isFrozen(String slug) {
        Path md = bundleDirFor(slug).resolve("index.md");
        try {
            return Files.isRegularFile(md) && Files.readString(md).contains("frozen: true");
        } catch (IOException e) {
            return false;
        }
    }

    /** The sponsor's bundle dir, reusing an existing one wherever it lives. */
    static Path bundleDirFor(String slug) {
        Path existing = findExistingBundle(slug);
        return existing != null ? existing : OUTPUT_DIR.resolve(slug);
    }

    static Path findExistingBundle(String slug) {
        if (!Files.isDirectory(OUTPUT_DIR)) return null;
        try (Stream<Path> s = Files.walk(OUTPUT_DIR)) {
            return s.filter(p -> p.getFileName().toString().equals("index.md")
                            && p.getParent() != null
                            && p.getParent().getFileName().toString().equals(slug))
                    .map(Path::getParent)
                    .findFirst().orElse(null);
        } catch (IOException e) {
            return null;
        }
    }

    static void writeSponsor(SponsorData d) throws IOException {
        StringBuilder fm = new StringBuilder();
        fm.append("---\n");
        fm.append("title: ").append(yamlString(d.name)).append("\n");
        // No `slug`/`url`: the bundle FOLDER name is the WordPress slug and
        // hugo.toml's [permalinks] maps the sponsors section to /sponsor/<that>/,
        // so every legacy sponsor URL keeps working without an alias.
        fm.append("tier: ").append(yamlString(d.tier)).append("\n");
        fm.append("tagline: ").append(yamlString(d.tagline)).append("\n");
        fm.append("description: ").append(yamlString(d.description)).append("\n");
        fm.append("logo: ").append(yamlString(d.logo)).append("\n");
        fm.append("logoBackground: ").append(yamlString(d.logoBackground)).append("\n");
        fm.append("website: ").append(yamlString(d.website)).append("\n");
        fm.append("websiteLabel: ").append(yamlString(d.websiteLabel)).append("\n");
        fm.append("linkedin: ").append(yamlString(d.linkedin)).append("\n");
        fm.append("youtube: ").append(yamlString(d.youtube)).append("\n");
        fm.append("bluesky: ").append(yamlString(d.bluesky)).append("\n");
        fm.append("mastodon: ").append(yamlString(d.mastodon)).append("\n");
        fm.append("twitter: ").append(yamlString(d.twitter)).append("\n");
        fm.append("github: ").append(yamlString(d.github)).append("\n");

        // Hand-maintained: the sponsor's authors on foojay.io. Every post written
        // by any of these author slugs is listed on the sponsor's profile page,
        // and their categories become the "Topics covered" list. Add slugs here
        // by hand (they're the folder names under content/authors/); this script
        // reads them back and writes them out unchanged.
        fm.append("# Hand-maintained: author slugs (content/authors/<letter>/<slug>/) whose posts\n");
        fm.append("# are this sponsor's articles. ConvertSponsors.java preserves this list verbatim.\n");
        fm.append("authors:\n");
        for (String a : d.authors) fm.append("  - ").append(yamlString(a)).append("\n");

        // Fallback only: shown when the sponsor has no `authors:` yet, so the
        // page isn't blank. Once authors are set the template derives the real
        // topic list from their posts' categories instead.
        fm.append("topics:\n");
        for (String t : d.topics) fm.append("  - ").append(yamlString(t)).append("\n");

        fm.append("canonical: ").append(yamlString(BASE_URL + "/sponsor/" + d.slug + "/")).append("\n");
        fm.append("frozen: false\n");
        fm.append("---\n\n");
        fm.append(d.body == null ? "" : d.body.strip()).append("\n");

        Files.createDirectories(d.bundleDir);
        Files.writeString(d.bundleDir.resolve("index.md"), fm.toString());

        System.out.printf("Done sponsor: %-45s tier=%-7s authors=%d topics=%d%n",
                d.slug, d.tier, d.authors.size(), d.topics.size());
    }

    static String slugFromUrl(String url) {
        Matcher m = SPONSOR_SLUG.matcher(url);
        return m.find() ? m.group(1) : slugify(url);
    }

    static String stripExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot > 0 ? filename.substring(0, dot) : filename;
    }

    static void sleepPolite() {
        try {
            Thread.sleep(POLITE_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    static String metaContent(Document doc, String property) {
        Element e = doc.selectFirst("meta[property=" + property + "]");
        return e != null ? e.attr("content") : "";
    }

    static String attrContent(Document doc, String selector) {
        Element e = doc.selectFirst(selector);
        return e != null ? e.attr("content") : "";
    }

    static String textOrNull(Element e) {
        return e != null ? e.text() : null;
    }

    static String textOrEmpty(Element e) {
        return e != null ? e.text().trim() : "";
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

    static class SponsorData {
        String slug;
        Path bundleDir;
        String name;
        String tier;
        String tagline;
        String description;
        String logo = "";
        String logoBackground = "";
        String website;
        String websiteLabel;
        String linkedin;
        String youtube;
        String bluesky;
        String mastodon;
        String twitter;
        String github;
        List<String> authors = new ArrayList<>();
        List<String> topics = new ArrayList<>();
        String body = "";
    }
}
