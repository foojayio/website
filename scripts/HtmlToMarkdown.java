//DEPS org.jsoup:jsoup:1.17.2
//DEPS com.vladsch.flexmark:flexmark-html2md-converter:0.64.8

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Shared body conversion for the WordPress -> Hugo migration scripts
 * (ConvertPosts.java and ConvertPages.java, which include this file via
 * jbang's `//SOURCES HtmlToMarkdown.java`).
 *
 * Given the scraped article-content element it:
 *   1. downloads foojay-hosted images local (so nothing hotlinks the WP site
 *      after cutover) and rewrites the references,
 *   2. detects the interactive widgets carried over from WordPress (JDoodle,
 *      EnlighterJS) so the caller can flag them in frontmatter, and
 *   3. converts the prose to Markdown while keeping load-bearing HTML blocks
 *      (code widgets, video embeds) verbatim -- Hugo renders those via
 *      goldmark's unsafe mode.
 *
 * NOTE: convert() mutates the element it is given (image src rewrites +
 * placeholder swaps); callers only need the returned Result afterwards.
 */
public final class HtmlToMarkdown {

    private HtmlToMarkdown() {
    }

    // Interactive widgets embedded in bodies, detected so the layout only loads
    // their scripts on the pages/posts that use them.
    // JDoodle: <div data-pym-src="https://www.jdoodle.com/plugin" ...> runnable snippets.
    public static final String SELECTOR_JDOODLE = "[data-pym-src]";
    // EnlighterJS: <pre class="EnlighterJSRAW"> / <code class="EnlighterJSRAW"> code blocks.
    public static final String SELECTOR_ENLIGHTERJS = "pre.EnlighterJSRAW, code.EnlighterJSRAW";

    // Block-level elements kept as raw HTML instead of being flattened to
    // Markdown, because their tag/class/attributes are load-bearing: EnlighterJS
    // needs pre.EnlighterJSRAW, JDoodle needs data-pym-src, embeds need <iframe>.
    private static final String SELECTOR_PRESERVE =
            "pre.EnlighterJSRAW, [data-pym-src], iframe, figure.wp-block-embed, .wp-block-embed";
    private static final String PRESERVE_TOKEN = "PRESERVEDHTMLBLOCKZZ";
    private static final String PRESERVE_TOKEN_END = "ZZEND";

    // Images hosted on foojay.io die at cutover, so they are pulled local.
    // Third-party images (youtube thumbs, badges, ...) are left untouched.
    private static final Pattern IMAGE_HREF =
            Pattern.compile("(?i)\\.(jpe?g|png|gif|webp|svg|avif)(?:[?#].*)?$");

    /** Where and how to localize images. */
    public static final class Options {
        final Path imageDir;          // e.g. static/images/pages
        final String imageUrlPrefix;  // e.g. /images/pages/
        final String localHostSuffix; // only localize images on this host, e.g. foojay.io
        final String userAgent;
        final int timeoutMs;

        public Options(Path imageDir, String imageUrlPrefix, String localHostSuffix,
                       String userAgent, int timeoutMs) {
            this.imageDir = imageDir;
            this.imageUrlPrefix = imageUrlPrefix;
            this.localHostSuffix = localHostSuffix;
            this.userAgent = userAgent;
            this.timeoutMs = timeoutMs;
        }
    }

    /** Markdown body plus which widgets it uses. */
    public static final class Result {
        public final String markdown;
        public final boolean jdoodle;
        public final boolean enlighterjs;

        Result(String markdown, boolean jdoodle, boolean enlighterjs) {
            this.markdown = markdown;
            this.jdoodle = jdoodle;
            this.enlighterjs = enlighterjs;
        }
    }

    /** Localizes images, detects widgets, converts prose to Markdown. Mutates content. */
    public static Result convert(Element content, Options opts) {
        localizeImages(content, opts);
        boolean jdoodle = !content.select(SELECTOR_JDOODLE).isEmpty();
        boolean enlighterjs = !content.select(SELECTOR_ENLIGHTERJS).isEmpty();
        String markdown = toMarkdown(content);
        return new Result(markdown, jdoodle, enlighterjs);
    }

    // ---- html -> markdown ------------------------------------------------

    /**
     * Converts the body to Markdown. Load-bearing HTML blocks (see
     * SELECTOR_PRESERVE) are swapped out for placeholder tokens first, so the
     * converter can't flatten away the classes/attributes they depend on, then
     * restored verbatim afterwards.
     */
    static String toMarkdown(Element content) {
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
     * Downloads every foojay-hosted image referenced in the body into the
     * configured image dir and rewrites the reference to the local path. Covers
     * both <img src>/srcset and <a href> lightbox links to image files.
     * Third-party images (kept working after cutover) are left as-is.
     */
    static void localizeImages(Element content, Options opts) {
        for (Element img : content.select("img[src]")) {
            String local = localizeImage(img.absUrl("src"), opts);
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
                String local = localizeImage(href, opts);
                if (local != null) a.attr("href", local);
            }
        }
    }

    /**
     * Localizes one image URL, returning its new site-absolute path, or null to
     * leave the reference unchanged (not a foojay-hosted image, or the download
     * failed). The WordPress uploads subpath is preserved so filenames from
     * different upload folders can't collide. Idempotent: an already-downloaded
     * file is not fetched again.
     */
    static String localizeImage(String absoluteUrl, Options opts) {
        String rel = imageRelPath(absoluteUrl, opts.localHostSuffix);
        if (rel == null) return null;
        Path out = opts.imageDir.resolve(rel);
        try {
            if (!Files.exists(out)) {
                Connection.Response res = Jsoup.connect(absoluteUrl)
                        .userAgent(opts.userAgent)
                        .timeout(opts.timeoutMs)
                        .ignoreContentType(true)
                        .maxBodySize(0)
                        .execute();
                Files.createDirectories(out.getParent());
                Files.write(out, res.bodyAsBytes());
            }
            return opts.imageUrlPrefix + rel;
        } catch (IOException e) {
            System.err.println("  image download failed: " + absoluteUrl + " -> " + e.getMessage());
            return null;
        }
    }

    /**
     * Maps a foojay-hosted image URL to its relative path under the image dir,
     * or null if it shouldn't be localized. Uses the WordPress uploads subpath
     * (e.g. .../uploads/2025/05/foo.jpg -> 2025/05/foo.jpg) when present, else
     * the URL path minus its leading slash.
     */
    static String imageRelPath(String absoluteUrl, String localHostSuffix) {
        if (absoluteUrl == null || absoluteUrl.isBlank() || absoluteUrl.startsWith("data:")) return null;
        URI uri;
        try {
            uri = URI.create(absoluteUrl);
        } catch (IllegalArgumentException e) {
            return null;
        }
        String host = uri.getHost();
        if (host == null || !host.endsWith(localHostSuffix)) return null; // only foojay-hosted
        String path = uri.getPath();
        if (path == null || path.isBlank()) return null;

        int uploads = path.indexOf("/uploads/");
        String rel = uploads >= 0 ? path.substring(uploads + "/uploads/".length()) : path.replaceFirst("^/+", "");
        rel = rel.replaceAll("\\.\\.(?:/|$)", ""); // defensive: no path traversal
        return rel.isBlank() ? null : rel;
    }
}
