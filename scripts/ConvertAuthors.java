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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts foojay.io author profile pages (/today/author/<slug>/) into Hugo
 * content markdown files under content/authors/.
 *
 * Usage:
 *   jbang scripts/ConvertAuthors.java
 *   jbang scripts/ConvertAuthors.java --url https://foojay.io/today/author/frankdelporte/   (single author, for tuning selectors)
 *
 * Same caveat as ConvertPosts.java: selectors are best-effort WordPress
 * conventions, not verified against the site's actual raw HTML/class names.
 * Tune SELECTOR_* below against a couple of real author pages first.
 *
 * Idempotent: re-running updates existing content/authors/<slug>.md files.
 * Respects `frozen: true` in an author's frontmatter to skip overwriting
 * hand-edited profiles.
 */
public class ConvertAuthors {

    static final String BASE_URL = "https://foojay.io";
    static final String AUTHOR_INDEX_PATH = "/today/author/";
    static final Path OUTPUT_DIR = Path.of("content/authors");
    static final int REQUEST_TIMEOUT_MS = 20_000;
    static final int POLITE_DELAY_MS = 250;

    static final String SELECTOR_AUTHOR_INDEX_LINKS = "a[href*=/today/author/]";
    static final String SELECTOR_AVATAR = "img.avatar, .author-avatar img, article img";
    static final String SELECTOR_BIO = ".author-bio, .author-description, article p";
    static final String SELECTOR_SOCIAL_LINKS = ".author-social a, .social-links a";

    public static void main(String[] args) throws Exception {
        Files.createDirectories(OUTPUT_DIR);

        String singleUrl = null;
        for (int i = 0; i < args.length; i++) {
            if ("--url".equals(args[i]) && i + 1 < args.length) singleUrl = args[++i];
        }

        if (singleUrl != null) {
            AuthorData d = scrapeAuthor(singleUrl);
            writeAuthor(d);
            System.out.println("Wrote " + d.slug + ".md (single-author test run)");
            return;
        }

        Set<String> authorUrls = collectAuthorUrls();
        System.out.println("Found " + authorUrls.size() + " author URLs.");

        int written = 0, skipped = 0, failed = 0;
        for (String url : authorUrls) {
            try {
                AuthorData d = scrapeAuthor(url);
                if (isFrozen(d.slug)) {
                    skipped++;
                    continue;
                }
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

    static Set<String> collectAuthorUrls() throws IOException {
        Set<String> urls = new LinkedHashSet<>();
        Document doc = Jsoup.connect(BASE_URL + AUTHOR_INDEX_PATH)
                .userAgent("foojay-hugo-migration-bot/1.0")
                .timeout(REQUEST_TIMEOUT_MS)
                .get();
        for (Element a : doc.select(SELECTOR_AUTHOR_INDEX_LINKS)) {
            String href = a.absUrl("href");
            Matcher m = Pattern.compile("/today/author/([^/]+)/?$").matcher(href);
            if (m.find()) {
                urls.add(BASE_URL + "/today/author/" + m.group(1) + "/");
            }
        }
        return urls;
    }

    static AuthorData scrapeAuthor(String url) throws IOException {
        Document doc = Jsoup.connect(url)
                .userAgent("foojay-hugo-migration-bot/1.0")
                .timeout(REQUEST_TIMEOUT_MS)
                .get();

        AuthorData d = new AuthorData();
        Matcher m = Pattern.compile("/today/author/([^/]+)/?$").matcher(url);
        d.slug = m.find() ? m.group(1) : slugify(url);

        d.name = firstNonBlank(
                textOrNull(doc.selectFirst("h1")),
                metaContent(doc, "og:title"),
                doc.title());

        Element avatar = doc.selectFirst(SELECTOR_AVATAR);
        d.avatar = avatar != null ? avatar.absUrl("src") : "";

        Element bio = doc.selectFirst(SELECTOR_BIO);
        d.bio = bio != null ? bio.text() : "";

        for (Element a : doc.select(SELECTOR_SOCIAL_LINKS)) {
            String href = a.absUrl("href");
            if (href.contains("twitter.com") || href.contains("x.com")) d.twitter = href;
            else if (href.contains("linkedin.com")) d.linkedin = href;
            else if (!href.isBlank() && d.website == null) d.website = href;
        }

        return d;
    }

    static boolean isFrozen(String slug) {
        Path f = OUTPUT_DIR.resolve(slug + ".md");
        if (!Files.exists(f)) return false;
        try {
            return Files.readString(f).contains("frozen: true");
        } catch (IOException e) {
            return false;
        }
    }

    static void writeAuthor(AuthorData d) throws IOException {
        StringBuilder fm = new StringBuilder();
        fm.append("---\n");
        fm.append("title: ").append(yamlString(d.name)).append("\n");
        fm.append("avatar: ").append(yamlString(d.avatar)).append("\n");
        fm.append("bio: ").append(yamlString(d.bio)).append("\n");
        fm.append("twitter: ").append(yamlString(d.twitter)).append("\n");
        fm.append("linkedin: ").append(yamlString(d.linkedin)).append("\n");
        fm.append("website: ").append(yamlString(d.website)).append("\n");
        fm.append("aliases:\n");
        fm.append("  - ").append(yamlString("/today/author/" + d.slug + "/")).append("\n");
        fm.append("frozen: false\n");
        fm.append("---\n");

        Files.writeString(OUTPUT_DIR.resolve(d.slug + ".md"), fm.toString());
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
        return s.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-+|-+$", "");
    }

    static String yamlString(String s) {
        if (s == null) s = "";
        String escaped = s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ");
        return "\"" + escaped + "\"";
    }

    static class AuthorData {
        String slug, name, avatar, bio, twitter, linkedin, website;
    }
}
