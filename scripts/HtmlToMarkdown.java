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
import java.util.regex.Matcher;
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
    // Markdown, because Markdown can't represent what makes them special:
    //   - pre.EnlighterJSRAW / [data-pym-src] / iframe / .wp-block-embed
    //       widgets & embeds whose tag/class/attributes are load-bearing
    //   - floated images (align left/right), resized / smaller images
    //     (is-resized, size-medium/thumbnail) and galleries -- Markdown's
    //     ![](...) drops the float, the dimensions, the <figcaption> and the
    //     gallery grid, so these keep their original WordPress HTML.
    // (The image src inside them is still localized first, in localizeImages.)
    // Kept as raw HTML: code widgets, embeds and galleries (multi-image, complex).
    private static final String SELECTOR_PRESERVE = String.join(", ",
            "pre.EnlighterJSRAW", "[data-pym-src]", "iframe",
            "figure.wp-block-embed", ".wp-block-embed",
            ".wp-block-gallery", "figure.gallery", ".gallery");
    // Single formatted images (float / resized / captioned). These become a
    // {{< img >}} shortcode (see shortcodes/img.html) rather than raw HTML, so the
    // localized src runs through relURL and gets the baseURL subpath -- raw HTML in
    // content bypasses render hooks and would 404 on a /subpath/ deploy.
    private static final String SELECTOR_IMG_SHORTCODE = String.join(", ",
            "figure.alignleft", "figure.alignright", "img.alignleft", "img.alignright",
            "figure.is-resized", "img.is-resized",
            "figure.size-medium", "figure.size-thumbnail", "img.size-medium", "img.size-thumbnail");
    private static final Pattern FORMATTING_CLASS = Pattern.compile("align(?:left|right|center)|size-[\\w-]+|is-resized");
    private static final String PRESERVE_TOKEN = "PRESERVEDHTMLBLOCKZZ";
    private static final String PRESERVE_TOKEN_END = "ZZEND";

    // YouTube embeds are turned into Hugo's built-in {{< youtube ID >}} shortcode
    // instead of kept as a raw <iframe> (handled before SELECTOR_PRESERVE, which
    // would otherwise catch the wrapping figure.wp-block-embed).
    private static final String SELECTOR_YOUTUBE = String.join(", ",
            "figure.wp-block-embed-youtube", "figure.is-provider-youtube",
            "iframe[src*=\"youtube.com/embed\"]", "iframe[src*=\"youtube-nocookie.com/embed\"]",
            "iframe[src*=\"youtu.be/\"]");
    private static final Pattern YOUTUBE_ID = Pattern.compile(
            "(?:youtube(?:-nocookie)?\\.com/embed/|youtu\\.be/|[?&]v=)([A-Za-z0-9_-]{6,})");

    // Images hosted on foojay.io die at cutover, so they are pulled local.
    // Third-party images (youtube thumbs, badges, ...) are left untouched.
    private static final Pattern IMAGE_HREF =
            Pattern.compile("(?i)\\.(jpe?g|png|gif|webp|svg|avif)(?:[?#].*)?$");

    /** Where and how to localize images. */
    public static final class Options {
        final Path imageBaseDir;      // base, e.g. static/images/pages
        final String imageUrlPrefix;  // base URL, e.g. /images/pages/
        final String localHostSuffix; // only localize images on this host, e.g. foojay.io
        final String userAgent;
        final int timeoutMs;

        public Options(Path imageBaseDir, String imageUrlPrefix, String localHostSuffix,
                       String userAgent, int timeoutMs) {
            this.imageBaseDir = imageBaseDir;
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

    /**
     * Localizes images, detects widgets, converts prose to Markdown. Mutates content.
     *
     * itemSubpath is the content item's own path (e.g. "java-quick-start/hello-world"
     * for a page, "2026/07/my-post" for a post). Its images are co-located under
     * imageBaseDir/itemSubpath/ -- one image directory per content item, mirroring
     * the content tree, so a page and its images are managed together.
     */
    public static Result convert(Element content, Options opts, String itemSubpath) {
        localizeImages(content, opts, itemSubpath);
        boolean jdoodle = !content.select(SELECTOR_JDOODLE).isEmpty();
        boolean enlighterjs = !content.select(SELECTOR_ENLIGHTERJS).isEmpty();
        String markdown = toMarkdown(content);
        return new Result(markdown, jdoodle, enlighterjs);
    }

    // ---- html -> markdown ------------------------------------------------

    /**
     * Converts the body to Markdown. YouTube embeds become {{< youtube ID >}}
     * shortcodes; other load-bearing HTML blocks (see SELECTOR_PRESERVE) are
     * swapped out for placeholder tokens so the converter can't flatten away the
     * classes/attributes they depend on. Both are restored after conversion (the
     * shortcode/raw HTML never passes through the Markdown converter).
     */
    static String toMarkdown(Element content) {
        List<String> preserved = new ArrayList<>();

        // YouTube embeds -> Hugo shortcode. Done first so the wrapping
        // figure.wp-block-embed isn't grabbed by SELECTOR_PRESERVE below.
        for (Element el : outermostMatches(content, SELECTOR_YOUTUBE)) {
            String id = youtubeId(el);
            if (id == null) continue; // unparseable -> leave for the raw-HTML preserve pass
            String token = PRESERVE_TOKEN + preserved.size() + PRESERVE_TOKEN_END;
            preserved.add("{{< youtube " + id + " >}}");
            el.replaceWith(new Element("p").text(token));
        }

        // Code widgets, embeds and galleries -> raw HTML (done before the image
        // pass so a gallery's inner images aren't turned into standalone shortcodes).
        for (Element el : outermostMatches(content, SELECTOR_PRESERVE)) {
            String token = PRESERVE_TOKEN + preserved.size() + PRESERVE_TOKEN_END;
            preserved.add(el.outerHtml());
            el.replaceWith(new Element("p").text(token)); // block-level placeholder
        }

        // Formatted single images -> {{< img >}} shortcode (subpath-safe src).
        for (Element el : outermostMatches(content, SELECTOR_IMG_SHORTCODE)) {
            String shortcode = imageShortcode(el);
            if (shortcode == null) continue;
            String token = PRESERVE_TOKEN + preserved.size() + PRESERVE_TOKEN_END;
            preserved.add(shortcode);
            el.replaceWith(new Element("p").text(token));
        }

        String md = FlexmarkHtmlConverter.builder().build().convert(content.html()).trim();

        for (int i = 0; i < preserved.size(); i++) {
            md = md.replace(PRESERVE_TOKEN + i + PRESERVE_TOKEN_END,
                    "\n\n" + preserved.get(i) + "\n\n");
        }
        // Collapse runs of blank lines (left by placeholder restoration and the
        // converter) down to a single blank line. Treats whitespace-only lines as
        // blank, but leaves a content line's trailing spaces (Markdown hard breaks).
        md = md.replaceAll("\\n(?:[ \\t]*\\n)+", "\n\n");
        return md.trim();
    }

    /** Builds a {{< img >}} shortcode call from a formatted image element (an
     *  <img> or a <figure> wrapping one), or null if there's no image. */
    static String imageShortcode(Element el) {
        Element img = "img".equals(el.tagName()) ? el : el.selectFirst("img");
        if (img == null) return null;
        String cls = formattingClasses(el.className().isBlank() ? img.className() : el.className());
        Element caption = el.selectFirst("figcaption");

        StringBuilder sb = new StringBuilder("{{< img");
        appendParam(sb, "src", img.attr("src"));
        appendParam(sb, "class", cls);
        appendParam(sb, "alt", img.attr("alt"));
        appendParam(sb, "width", img.attr("width"));
        appendParam(sb, "height", img.attr("height"));
        // Honour an author-set display size (WordPress "is-resized" puts it in an
        // inline style, e.g. width:300px) so the image isn't rendered oversized.
        appendParam(sb, "style", img.attr("style"));
        appendParam(sb, "caption", caption != null ? caption.text() : "");
        return sb.append(" >}}").toString();
    }

    /** Keeps only the WordPress alignment/size/resize classes (drops wp-image-NN etc.). */
    static String formattingClasses(String classAttr) {
        List<String> keep = new ArrayList<>();
        for (String c : classAttr.split("\\s+")) {
            if (FORMATTING_CLASS.matcher(c).matches()) keep.add(c);
        }
        return String.join(" ", keep);
    }

    private static void appendParam(StringBuilder sb, String key, String value) {
        if (value == null || value.isBlank()) return;
        // Shortcode param values are double-quoted; neutralize quotes/newlines.
        String clean = value.replace("\"", "'").replace("\n", " ").trim();
        sb.append(' ').append(key).append("=\"").append(clean).append('"');
    }

    /** Extracts the video id from a YouTube embed element (a figure wrapping an
     *  iframe, or the iframe itself), or null if it can't be parsed. */
    static String youtubeId(Element el) {
        Element iframe = "iframe".equals(el.tagName()) ? el : el.selectFirst("iframe[src]");
        if (iframe == null) return null;
        Matcher m = YOUTUBE_ID.matcher(iframe.attr("src"));
        return m.find() ? m.group(1) : null;
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
     * Downloads every foojay-hosted image referenced in the body into this item's
     * own image directory (imageBaseDir/itemSubpath/) and rewrites the reference
     * to the local path. Covers both <img src>/srcset and <a href> lightbox links
     * to image files. Third-party images (kept working after cutover) are left as-is.
     */
    static void localizeImages(Element content, Options opts, String itemSubpath) {
        for (Element img : content.select("img[src]")) {
            String local = localizeImage(img.absUrl("src"), opts, itemSubpath);
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
                String local = localizeImage(href, opts, itemSubpath);
                if (local != null) a.attr("href", local);
            }
        }
    }

    /**
     * Localizes one image URL into this item's image directory, returning its new
     * site-absolute path, or null to leave the reference unchanged (not a
     * foojay-hosted image, or the download failed). Idempotent: an already-
     * downloaded file is not fetched again.
     */
    static String localizeImage(String absoluteUrl, Options opts, String itemSubpath) {
        String filename = localImageFilename(absoluteUrl, opts.localHostSuffix);
        if (filename == null) return null;
        String rel = (itemSubpath == null || itemSubpath.isBlank())
                ? filename
                : itemSubpath + "/" + filename;
        Path out = opts.imageBaseDir.resolve(rel);
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
     * The filename to store a foojay-hosted image under (its basename, sanitized),
     * or null if it shouldn't be localized (not foojay-hosted, data:, or blank).
     * Co-location per content item means the WP upload folders don't matter -- the
     * basename is enough, and collisions within a single page are vanishingly rare.
     */
    static String localImageFilename(String absoluteUrl, String localHostSuffix) {
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

        String name = path.substring(path.lastIndexOf('/') + 1);
        name = name.replaceAll("[^A-Za-z0-9._-]", "-"); // filesystem-safe, no traversal
        return name.isBlank() || name.equals(".") ? null : name;
    }
}
