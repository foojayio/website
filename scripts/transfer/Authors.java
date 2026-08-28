///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.jsoup:jsoup:1.17.2
//JAVA 17+

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Converts foojay.io author profile pages (/today/author/<slug>/) into Hugo
 * content markdown files under content/authors/.
 *
 * Why a markdown file per author (and not a data/authors.yaml)? Authors are
 * first-class pages: each keeps its legacy URL (/today/author/<slug>/ via
 * aliases), renders a profile page listing that author's articles
 * (themes/foojay/layouts/author/section.html), and is referenced by posts via
 * their `author:` slug. A Hugo data file produces no pages and no URLs, so it
 * can't carry any of that. Content files are the correct model here.
 *
 * Files are bucketed by the first letter of the slug
 * (formerly content/authors/a/<slug>.md, .../b/..., non-letters -> _) to keep the
 * directory browsable, exactly like posts are bucketed by publish date. The
 * permalink is slug-only (see hugo.toml), so the subdirectory has NO effect on
 * the URL. author<->post linking uses the base filename, not the path, so it is
 * unaffected too.
 *
 * Avatars are pulled LOCAL: the remote WordPress/Gravatar image is downloaded
 * into static/images/author/<letter>/<slug>.<ext> and the frontmatter `avatar:`
 * points at the site-absolute path. This keeps the static site self-contained
 * so nothing hotlinks the WP install that goes away at cutover.
 *
 * All authors are collected by paginating the sorted index
 * (/today/author/page/N/?posts_per_page=9&sort_by=name_asc). Past the final
 * page the site keeps returning that same last page, so pagination stops as
 * soon as a page contributes no new author URLs.
 *
 * Usage:
 *   jbang scripts/transfer/Authors.java
 *   jbang scripts/transfer/Authors.java --url https://foojay.io/today/author/frankdelporte/   (single author, for tuning selectors)
 *
 * Same caveat as transfer/Posts.java: selectors are best-effort WordPress
 * conventions, not verified against the site's actual raw HTML/class names.
 * Tune SELECTOR_* below against a couple of real author pages first.
 *
 * Idempotent: re-running updates existing author files (found by slug wherever
 * they live, so a file that moved buckets isn't duplicated) and reuses an
 * already-downloaded avatar. Respects `frozen: true` to skip hand-edited
 * profiles (checked before any network fetch).
 */
public class Authors {

    static final String BASE_URL = "https://foojay.io";
    static final Path OUTPUT_DIR = Path.of("content/authors");
    static final int REQUEST_TIMEOUT_MS = 20_000;
    static final int POLITE_DELAY_MS = 250;
    static final int MAX_AUTHOR_PAGES = 500; // safety cap; real count is ~38
    static final String USER_AGENT = "foojay-hugo-migration-bot/1.0";

    // Verified against foojay.io's live author-card markup (2026-07).
    static final String SELECTOR_AUTHOR_INDEX_LINKS = "a[href*=/today/author/]";
    static final String SELECTOR_AVATAR = ".author-card__avatar-box img, img.avatar";
    static final String SELECTOR_BIO = ".author-card__description";
    static final String SELECTOR_SOCIAL_LINKS = ".author-card__social-list a";

    static final Pattern AUTHOR_SLUG = Pattern.compile("/today/author/([^/]+)/?$");

    public static void main(String[] args) throws Exception {
        Files.createDirectories(OUTPUT_DIR);

        String singleUrl = null;
        for (int i = 0; i < args.length; i++) {
            if ("--url".equals(args[i]) && i + 1 < args.length) singleUrl = args[++i];
        }

        if (singleUrl != null) {
            String slug = slugFromUrl(singleUrl);
            AuthorData d = scrapeAuthor(singleUrl, slug);
            localizeAvatars(d);
            writeAuthor(d);
            System.out.println("Wrote " + d.slug + "/_index.md (single-author test run), avatar=" + d.avatar);
            return;
        }

        Set<String> authorUrls = collectAuthorUrls();
        System.out.println("Found " + authorUrls.size() + " author URLs.");

        int written = 0, skipped = 0, failed = 0;
        for (String url : authorUrls) {
            String slug = slugFromUrl(url);
            if (isFrozen(slug)) {
                skipped++;
                continue;
            }
            try {
                AuthorData d = scrapeAuthor(url, slug);
                localizeAvatars(d);
                writeAuthor(d);
                written++;
                Thread.sleep(POLITE_DELAY_MS);
            } catch (Exception e) {
                System.err.println("FAILED: " + url + " -> " + e.getMessage());
                failed++;
            }
        }
        System.out.printf("Done. written=%d skipped(frozen)=%d failed=%d%n", written, skipped, failed);
    }

    /**
     * Walks the sorted author index page by page. Stops when a page yields no
     * author URL not already seen -- which is exactly what happens once we run
     * past the last real page, since the site then keeps serving that final
     * page verbatim.
     */
    static Set<String> collectAuthorUrls() throws IOException {
        Set<String> urls = new LinkedHashSet<>();
        for (int page = 1; page <= MAX_AUTHOR_PAGES; page++) {
            String pageUrl = (page == 1)
                    ? BASE_URL + "/today/author/?posts_per_page=9&sort_by=name_asc"
                    : BASE_URL + "/today/author/page/" + page + "/?posts_per_page=9&sort_by=name_asc";

            int before = urls.size();
            try {
                Document doc = Jsoup.connect(pageUrl)
                        .userAgent(USER_AGENT)
                        .timeout(REQUEST_TIMEOUT_MS)
                        .get();
                for (Element a : doc.select(SELECTOR_AUTHOR_INDEX_LINKS)) {
                    Matcher m = AUTHOR_SLUG.matcher(a.absUrl("href"));
                    if (m.find()) {
                        String slug = m.group(1);
                        if ("page".equals(slug)) continue; // pagination links, not authors
                        urls.add(BASE_URL + "/today/author/" + slug + "/");
                    }
                }
            } catch (IOException e) {
                System.err.println("  author index page " + page + " failed: " + e.getMessage());
                break;
            }

            int added = urls.size() - before;
            System.out.printf("  page %d: +%d new (total %d)%n", page, added, urls.size());
            if (added == 0) break; // past the last page: no new authors -> done
            sleepPolite();
        }
        return urls;
    }

    static AuthorData scrapeAuthor(String url, String slug) throws IOException {
        Document doc = Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .timeout(REQUEST_TIMEOUT_MS)
                .get();

        AuthorData d = new AuthorData();
        d.slug = slug;

        d.name = firstNonBlank(
                textOrNull(doc.selectFirst("h1")),
                metaContent(doc, "og:title"),
                doc.title());

        d.avatar = bestAvatarUrl(doc.selectFirst(SELECTOR_AVATAR));

        Element bio = doc.selectFirst(SELECTOR_BIO);
        d.bio = bio != null ? bio.text() : "";

        for (Element a : doc.select(SELECTOR_SOCIAL_LINKS)) {
            classifySocial(d, a.absUrl("href"));
        }

        return d;
    }

    /**
     * Routes a social link to the right AuthorData field. foojay's author-card
     * links are icon-only (SVG, no text), so the platform is inferred from the
     * host. First link of each kind wins; anything unrecognised becomes the
     * website. youtube is checked before mastodon because both use /@handle.
     */
    static void classifySocial(AuthorData d, String href) {
        if (href == null || href.isBlank()) return;
        String h = href.toLowerCase(Locale.ROOT);
        if (h.contains("bsky.app") || h.contains("bluesky")) { if (d.bluesky == null) d.bluesky = href; }
        else if (h.contains("linkedin.com")) { if (d.linkedin == null) d.linkedin = href; }
        else if (h.contains("github.com")) { if (d.github == null) d.github = href; }
        else if (h.contains("gitlab.com")) { if (d.gitlab == null) d.gitlab = href; }
        else if (h.contains("youtube.com") || h.contains("youtu.be")) { if (d.youtube == null) d.youtube = href; }
        else if (isMastodon(h)) { if (d.mastodon == null) d.mastodon = href; }
        else if (d.website == null) d.website = href;
    }

    static boolean isMastodon(String lowerHref) {
        // instance domains vary (foojay.social, fosstodon.org, mastodon.social, ...);
        // the reliable shared signals are a mastodon/fosstodon host, a .social
        // host, or the /@handle path convention.
        return lowerHref.contains("mastodon")
                || lowerHref.contains("fosstodon")
                || lowerHref.contains(".social/")
                || lowerHref.contains("/@");
    }

    /**
     * The largest avatar URL the img exposes: the highest-density/width
     * candidate from srcset (foojay serves a 192x192 at 2x), else src.
     */
    static String bestAvatarUrl(Element img) {
        if (img == null) return "";
        String srcset = img.attr("srcset");
        String best = null;
        double bestScore = -1;
        for (String candidate : srcset.split(",")) {
            String[] tok = candidate.trim().split("\\s+");
            if (tok[0].isBlank()) continue;
            double score = 1;
            if (tok.length > 1 && tok[1].matches("\\d+(\\.\\d+)?[wx]")) {
                score = Double.parseDouble(tok[1].substring(0, tok[1].length() - 1));
            }
            if (score > bestScore && tok[0].startsWith("http")) {
                bestScore = score;
                best = tok[0];
            }
        }
        return best != null ? best : img.absUrl("src");
    }

    /**
     * Pulls the avatar local, co-located in the author's bundle directory
     * (content/authors/<slug>/), in two versions referenced by bare filename:
     *   avatar      -> the URL as served (foojay's 192x192 thumbnail) -> <slug>.<ext>
     *   avatarFull  -> the same URL with the WordPress "-WxH" size suffix stripped,
     *                  i.e. the full-size original                    -> <slug>-full.<ext>
     * If stripping the suffix changes nothing, avatarFull just mirrors avatar.
     */
    static void localizeAvatars(AuthorData d) throws IOException {
        d.bundleDir = bundleDirFor(d.slug);
        if (d.avatar == null || d.avatar.isBlank()) {
            d.avatar = "";
            d.avatarFull = "";
            return;
        }
        Path bundleDir = d.bundleDir;
        String remoteThumb = d.avatar;
        String remoteFull = stripWpSize(remoteThumb);

        d.avatar = localizeAvatar(remoteThumb, d.slug, bundleDir);
        d.avatarFull = remoteFull.equals(remoteThumb)
                ? d.avatar
                : localizeAvatar(remoteFull, d.slug + "-full", bundleDir);
    }

    /**
     * Downloads one avatar into the bundle dir as <baseName>.<ext> and returns the
     * bare filename (a page-bundle resource). Reuses an already-downloaded file.
     * Falls back to the remote URL if the download fails so the profile still renders.
     */
    static String localizeAvatar(String remoteUrl, String baseName, Path bundleDir) {
        if (remoteUrl == null || remoteUrl.isBlank()) return "";
        Path existing = findInDir(bundleDir, baseName);
        if (existing != null) return existing.getFileName().toString();
        try {
            Connection.Response res = Jsoup.connect(remoteUrl)
                    .userAgent(USER_AGENT)
                    .timeout(REQUEST_TIMEOUT_MS)
                    .ignoreContentType(true)
                    .maxBodySize(0)
                    .execute();
            String name = baseName + extensionFor(remoteUrl, res.contentType());
            Files.createDirectories(bundleDir);
            Files.write(bundleDir.resolve(name), res.bodyAsBytes());
            return name;
        } catch (IOException e) {
            System.err.println("  avatar download failed for " + baseName + ": " + e.getMessage() + " (keeping remote URL)");
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

    /** Removes a WordPress "-WxH" size suffix (e.g. -192x192) before the file extension. */
    static String stripWpSize(String url) {
        return url.replaceFirst("-\\d+x\\d+(?=\\.[A-Za-z0-9]+(?:[?#].*)?$)", "");
    }

    static String extensionFor(String url, String contentType) {
        if (contentType != null) {
            String ct = contentType.toLowerCase(Locale.ROOT);
            if (ct.contains("png")) return ".png";
            if (ct.contains("gif")) return ".gif";
            if (ct.contains("webp")) return ".webp";
            if (ct.contains("svg")) return ".svg";
            if (ct.contains("jpeg") || ct.contains("jpg")) return ".jpg";
        }
        String clean = url.replaceAll("[?#].*$", "").toLowerCase(Locale.ROOT);
        int dot = clean.lastIndexOf('.');
        int slash = clean.lastIndexOf('/');
        if (dot > slash && dot < clean.length() - 1) {
            String ext = clean.substring(dot);
            if (ext.matches("\\.(png|gif|webp|svg|jpe?g)")) return ext.equals(".jpeg") ? ".jpg" : ext;
        }
        return ".jpg";
    }

    /** A single quoted frontmatter value from an existing bundle, or null.
     *  Deliberately a line match rather than a YAML parse: this reads back one
     *  key the scrape cannot supply, not the whole file. */
    static String existingParam(Path bundleDir, String key) {
        Path file = bundleDir.resolve("_index.md");
        if (!Files.isRegularFile(file)) return null;
        try {
            for (String line : Files.readAllLines(file)) {
                if (line.startsWith(key + ":")) {
                    String v = line.substring(key.length() + 1).trim();
                    if (v.length() >= 2 && v.startsWith("\"") && v.endsWith("\"")) {
                        v = v.substring(1, v.length() - 1);
                    }
                    return v.isBlank() ? null : v;
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    static boolean isFrozen(String slug) {
        Path bundle = findExistingAuthorBundle(slug);
        if (bundle == null) return false;
        try {
            return Files.readString(bundle.resolve("_index.md")).contains("frozen: true");
        } catch (IOException e) {
            return false;
        }
    }

    /** The author's bundle dir, content/authors/<slug>/ -- reused if it already
     *  exists so a re-run never moves or duplicates a bundle.
     *
     *  There is no first-letter bucket any more. Author bundles used to live in
     *  content/authors/<letter>/<slug>/ purely to keep 344 folders browsable, but
     *  each profile is now a BRANCH bundle (so .Paginate accepts it -- see
     *  content/authors/_index.md), and Hugo turns every directory holding pages
     *  into a section: the 23 letter folders became 23 sections of their own,
     *  claiming URLs like /today/author/a/. Flat is what removes them. */
    static Path bundleDirFor(String slug) {
        Path existing = findExistingAuthorBundle(slug);
        return existing != null ? existing : OUTPUT_DIR.resolve(slug);
    }

    /** Locates an existing content/authors/<slug>/_index.md bundle. */
    static Path findExistingAuthorBundle(String slug) {
        if (!Files.isDirectory(OUTPUT_DIR)) return null;
        try (Stream<Path> s = Files.walk(OUTPUT_DIR)) {
            return s.filter(p -> p.getFileName().toString().equals("_index.md")
                            && p.getParent() != null
                            && p.getParent().getFileName().toString().equals(slug))
                    .map(Path::getParent)
                    .findFirst().orElse(null);
        } catch (IOException e) {
            return null;
        }
    }

    static void writeAuthor(AuthorData d) throws IOException {
        Path bundleDir = d.bundleDir != null ? d.bundleDir : bundleDirFor(d.slug);

        StringBuilder fm = new StringBuilder();
        fm.append("---\n");
        fm.append("title: ").append(yamlString(d.name)).append("\n");
        // No `slug`: the bundle FOLDER name is the URL slug (permalink
        // :slugorcontentbasename), and writeAuthor names that folder d.slug.
        fm.append("avatar: ").append(yamlString(d.avatar)).append("\n");
        fm.append("avatarFull: ").append(yamlString(d.avatarFull)).append("\n");
        fm.append("bio: ").append(yamlString(d.bio)).append("\n");
        fm.append("bluesky: ").append(yamlString(d.bluesky)).append("\n");
        fm.append("mastodon: ").append(yamlString(d.mastodon)).append("\n");
        fm.append("linkedin: ").append(yamlString(d.linkedin)).append("\n");
        fm.append("github: ").append(yamlString(d.github)).append("\n");
        // foojay's author card has no GitLab icon, so a gitlab: value can only
        // ever have been written by hand -- and writeAuthor rebuilds the whole
        // block, which would silently drop it on the next re-run. Carried over
        // from the existing file when the scrape found none, the way
        // Sponsors.java carries `authors:` through.
        //
        // WRITTEN ONLY WHEN THERE IS A VALUE, unlike every other key here, and
        // the asymmetry is the point. The other fields are ones the scrape can
        // genuinely report on, so `bluesky: ""` is a real answer: we read the
        // card and there was no Bluesky icon on it. gitlab is the one field the
        // card cannot supply, so an empty gitlab: states nothing at all -- and
        // 345 of the 348 author bundles have no such line today, so writing one
        // unconditionally rewrites 345 files to add no information. A diff that
        // size is not free: it is what the real change in the next re-scrape
        // hides behind. `existingParam` already returns null for a blank value,
        // so a hand-written one still survives and an empty one is cleaned up.
        if (d.gitlab == null) d.gitlab = existingParam(bundleDir, "gitlab");
        if (d.gitlab != null) fm.append("gitlab: ").append(yamlString(d.gitlab)).append("\n");
        fm.append("youtube: ").append(yamlString(d.youtube)).append("\n");
        fm.append("website: ").append(yamlString(d.website)).append("\n");
        fm.append("frozen: false\n");
        fm.append("---\n");

        Files.createDirectories(bundleDir);
        Files.writeString(bundleDir.resolve("_index.md"), fm.toString());

        System.out.println("Done author: " + d.slug);
    }

    static String slugFromUrl(String url) {
        Matcher m = AUTHOR_SLUG.matcher(url);
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

    static class AuthorData {
        String slug;
        Path bundleDir;
        String name;
        String avatar;
        String avatarFull;
        String bio;
        String bluesky;
        String mastodon;
        String linkedin;
        String github;
        String gitlab;
        String youtube;
        String website;
    }
}
