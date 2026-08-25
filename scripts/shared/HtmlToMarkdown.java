//DEPS org.jsoup:jsoup:1.17.2
//DEPS com.vladsch.flexmark:flexmark-html2md-converter:0.64.8

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Shared body conversion for the WordPress -> Hugo migration scripts. Now used
 * by transfer/Posts.java (which includes this file via jbang's
 * `//SOURCES ../shared/HtmlToMarkdown.java`); the one-off page/glossary scrapers that also
 * used it have been retired now that content/pages and content/pedia are done.
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
    //   - [data-pym-src] / iframe / .wp-block-embed
    //       widgets & embeds whose tag/class/attributes are load-bearing
    //   - floated images (align left/right) and resized / smaller images
    //     (is-resized, size-medium/thumbnail) -- Markdown's ![](...) drops the
    //     float, the dimensions and the <figcaption>. Those go through the
    //     {{< img >}} shortcode below rather than staying raw.
    // (The image src inside them is still localized first, in localizeImages.)
    private static final String SELECTOR_PRESERVE = String.join(", ",
            "[data-pym-src]", "iframe",
            "figure.wp-block-embed", ".wp-block-embed");
    // WordPress galleries -> {{< gallery >}} shortcode (see shortcodes/gallery.html).
    // Both block shapes: the modern figure-of-figures and the older one nesting a
    // <ul class="blocks-gallery-grid">. A gallery is a list of filenames with the
    // odd caption, so that is what lands in content/ -- 30 lines of WordPress
    // block markup is not something a contributor can be asked to type, and the
    // grid, the lightbox and the link to the full-size original are the theme's
    // job to supply rather than the author's to spell out.
    private static final String SELECTOR_GALLERY = String.join(", ",
            ".wp-block-gallery", "figure.gallery", ".gallery");
    // Single formatted images (float / resized / captioned). These become a
    // {{< img >}} shortcode (see shortcodes/img.html) rather than raw HTML, so the
    // localized src runs through relURL and gets the baseURL subpath -- raw HTML in
    // content bypasses render hooks and would 404 on a /subpath/ deploy.
    private static final String SELECTOR_IMG_SHORTCODE = String.join(", ",
            "figure.alignleft", "figure.alignright", "img.alignleft", "img.alignright",
            "figure.is-resized", "img.is-resized",
            "figure.size-medium", "figure.size-thumbnail", "img.size-medium", "img.size-thumbnail");
    // WordPress's column count for a gallery block: `columns-3`. `columns-default`
    // does not match on purpose -- it means "however many the theme picks".
    private static final Pattern GALLERY_COLUMNS = Pattern.compile("\\bcolumns-([1-9])\\b");
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

    // Flexmark renders every <hr> as this thematic break. WordPress bodies are
    // littered with decorative <hr>s between sections -- they carried styling the
    // WP theme supplied and this site doesn't, so here they land as bare rules
    // that add nothing. Dropped rather than converted to `---`.
    private static final Pattern FLEXMARK_THEMATIC_BREAK =
            Pattern.compile("(?m)^[ \\t]*\\*\\*\\* \\*\\* \\* \\*\\* \\*\\*\\*[ \\t]*$");

    // A line holding nothing but <br> tags. WordPress bodies use these as
    // vertical spacers -- after an image, after a video embed, at the end of the
    // body -- and they arrive here as a bare `<br />` line that renders as an
    // empty <br> between paragraphs. Same story as the decorative <hr>s above:
    // the WP theme gave them meaning, this one doesn't. Dropped for the same
    // reason and at the same point, BEFORE the preserved placeholders are
    // restored, so a <br> inside a raw-HTML block (a table cell, inline SVG) is
    // never touched. A <br> with text on the line is a real hard break and stays.
    private static final Pattern STANDALONE_BREAK =
            Pattern.compile("(?im)^[ \\t]*(?:<br\\s*/?>[ \\t]*)+$");

    // "Friends Of OpenJDK" is a brand, so it is capitalized like one wherever it
    // appears in a body -- including the middle of a sentence, which is where WP
    // authors write it in lower case ("Foojay.io, a place for friends of
    // OpenJDK"). content/ is already 103 to 1 in favour of the capitalized form,
    // so this guards a re-scrape from reintroducing the odd one out rather than
    // changing the house style.
    //
    // The separators are CAPTURED and written back rather than replaced with
    // literal spaces, so the rule can neither invent nor drop whitespace around
    // the phrase -- a WP body wraps its source, so it really does arrive with a
    // newline inside it. In a BODY that hardly shows, since Flexmark reflows the
    // paragraph afterwards anyway; it is the normalizeBrandName(String) callers
    // (a title, a bio) that get their own line breaks back untouched.
    //
    // "OpenJDK" on its own is deliberately NOT normalized. That would be a far
    // wider rule than a brand name -- it would hit package names, `--list-modules`
    // output and URLs -- and only the three-word phrase is the brand.
    private static final Pattern BRAND_NAME =
            Pattern.compile("(?i)\\bfriends(\\s+)of(\\s+)openjdk\\b");

    // ATX headings (`## Title`) rather than Flexmark's default, which underlines
    // h1/h2 with ==== / ---- and only uses ### from h3 down. That split leaves
    // content/ written in two styles at once, and the underline is the worse of
    // the two here: contributors send posts as PRs and type `##`, and an 80-dash
    // rule under every h2 is noise in a diff. Levels 3-6 are unaffected.
    // TYPOGRAPHIC_SMARTS off: Flexmark's default rewrites the real characters WP
    // serves -- em dash, en dash, ellipsis -- as `---`, `--` and `...`. Goldmark
    // has no smartypants pass to turn them back, so the reader gets a literal
    // `Fair challenge --- JEP 491` mid-sentence. Keeping the Unicode character is
    // both what the author wrote and what an author writing a new post today
    // would type. TYPOGRAPHIC_QUOTES is deliberately left ON: curly quotes in
    // stored Markdown are harder to type and to diff, and ASCII quotes render
    // identically.
    private static final MutableDataSet CONVERTER_OPTIONS = new MutableDataSet()
            .set(FlexmarkHtmlConverter.SETEXT_HEADINGS, false)
            .set(FlexmarkHtmlConverter.TYPOGRAPHIC_SMARTS, false);

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
        // Names this Options actually downloaded, so the converted-sibling rule in
        // localizeImage() can tell "cleanup/images.py re-encoded this one" from
        // "WordPress genuinely serves both foo.png and foo.jpg". An Options is built
        // per content item and never shared across threads, so no synchronization.
        final Set<String> fetchedThisItem = new HashSet<>();

        public Options(Path imageBaseDir, String imageUrlPrefix, String localHostSuffix,
                       String userAgent, int timeoutMs) {
            this.imageBaseDir = imageBaseDir;
            this.imageUrlPrefix = imageUrlPrefix;
            this.localHostSuffix = localHostSuffix;
            this.userAgent = userAgent;
            this.timeoutMs = timeoutMs;
        }
    }

    /** Markdown body plus which widgets it uses. (EnlighterJS is NOT among them:
     *  the layout detects code blocks in the rendered page, so no flag is written.) */
    public static final class Result {
        public final String markdown;
        public final boolean jdoodle;

        Result(String markdown, boolean jdoodle) {
            this.markdown = markdown;
            this.jdoodle = jdoodle;
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
        repairEscapedUrls(content);
        normalizeLegacyUrls(content);
        localizeImages(content, opts, itemSubpath);
        boolean jdoodle = !content.select(SELECTOR_JDOODLE).isEmpty();
        String markdown = toMarkdown(content);
        return new Result(markdown, jdoodle);
    }

    // ---- html -> markdown ------------------------------------------------

    /**
     * Converts the body to Markdown. YouTube embeds become {{< youtube ID >}}
     * shortcodes; other load-bearing HTML blocks (see SELECTOR_PRESERVE) are
     * swapped out for placeholder tokens so the converter can't flatten away the
     * classes/attributes they depend on. Both are restored after conversion (the
     * shortcode/raw HTML never passes through the Markdown converter).
     *
     * Public because transfer/Comments.java converts WordPress *comment* bodies
     * with it -- same repairs (entities, code fences, nbsp indents), but none of
     * convert()'s image localization, which a comment doesn't need.
     */
    public static String toMarkdown(Element content) {
        List<String> preserved = new ArrayList<>();

        // Cloudflare's email obfuscation, undone before anything else -- the
        // placeholder it leaves otherwise ends up baked into code fences and
        // preserved HTML blocks by the passes below. See decodeCloudflareEmails.
        decodeCloudflareEmails(content);

        // YouTube embeds -> Hugo shortcode. Done first so the wrapping
        // figure.wp-block-embed isn't grabbed by SELECTOR_PRESERVE below.
        for (Element el : outermostMatches(content, SELECTOR_YOUTUBE)) {
            String id = youtubeId(el);
            if (id == null) continue; // unparseable -> leave for the raw-HTML preserve pass
            String token = PRESERVE_TOKEN + preserved.size() + PRESERVE_TOKEN_END;
            preserved.add("{{< youtube " + id + " >}}");
            el.replaceWith(new Element("p").text(token));
        }

        // EnlighterJS code blocks -> fenced Markdown. Authors write ```java, not
        // a <pre class="EnlighterJSRAW" data-enlighter-language="java" ...> tag
        // with seven more attributes, so the fence is what lands in content/.
        // The EnlighterJS markup is put back at RENDER time by
        // layouts/_default/_markup/render-codeblock.html, so the site looks
        // identical -- the raw HTML simply stops being the storage format.
        //
        // Emitted through the preserve/restore token like everything else, so the
        // fence body never passes through the Markdown converter (which would
        // escape its punctuation).
        //
        // WHAT GETS REPLACED IS THE <pre>, NOT ALWAYS THE MATCHED ELEMENT. Most
        // blocks are a <pre class="EnlighterJSRAW">, but some are the nested
        // shape -- <pre><code class="EnlighterJSRAW" data-enlighter-language=
        // "kotlin">. Replacing the <code> there leaves the <pre> standing with
        // the placeholder inside it, so Flexmark renders that <pre> as a code
        // block of its own and the restored fence comes back wrapped in a SECOND,
        // empty one:
        //
        //     ```
        //
        //     ```kotlin
        //     measureTimeMillis {
        //     ```
        //
        //     ```
        //
        // Which is not cosmetic. Only the FIRST bare ``` of that pair is read as
        // a closing fence, so the last one on the page opens a block that never
        // closes and the whole tail of the post renders as source code -- 7 posts
        // were live in that state, each with everything after its final code
        // sample shown as one grey slab.
        for (Element el : outermostMatches(content, SELECTOR_ENLIGHTERJS)) {
            String token = PRESERVE_TOKEN + preserved.size() + PRESERVE_TOKEN_END;
            // Climbing rather than checking the immediate parent, because the
            // nesting goes two deep on some posts:
            // <pre><code class="language-xml"><code class="EnlighterJSRAW">. Each
            // wrapper that holds nothing but this block would otherwise become an
            // empty code block of its own.
            Element target = el;
            String wrapperLang = "";
            while (target.parent() != null
                    && ("pre".equals(target.parent().tagName()) || "code".equals(target.parent().tagName()))
                    && target.parent().children().size() == 1
                    && target.parent().ownText().isBlank()) {
                target = target.parent();
                if (wrapperLang.isEmpty()) wrapperLang = languageClass(target);
            }
            // A WRAPPER'S `language-*` CLASS BEATS THE ENLIGHTER ATTRIBUTE. Both
            // of the posts with a wrapper that named a language disagreed with the
            // plugin -- `<code class="language-xml">` around a Maven
            // `<dependency>` block and `<code class="language-ts">` around a React
            // component, both stamped data-enlighter-language="java", i.e. the
            // site-wide default on a site about Java. The class is what the
            // author's own markup said; the attribute is what the plugin was set
            // to. This does mean the fence can name a different language from the
            // one foojay.io highlights that block with today -- deliberately: it
            // is XML, and reproducing the plugin's default faithfully would be
            // reproducing a mistake.
            preserved.add(codeFence(el.wholeText(),
                    wrapperLang.isEmpty() ? el.attr("data-enlighter-language") : wrapperLang));
            target.replaceWith(new Element("p").text(token));
        }

        // Inline <code> spans left over (the EnlighterJS ones are gone by now).
        // Flexmark turns these into backtick spans, where -- exactly as in a
        // fence -- an entity is never what the author typed, it is WordPress's
        // double-escaping. Without this a span reading `DESCRIBE KEYSPACE
        // &lt;name>` survives into content/ and renders as literal `&lt;`,
        // because Markdown does not decode entities inside code spans.
        for (Element code : content.select("code")) {
            if (code.parent() != null && "pre".equals(code.parent().tagName())) continue;
            String text = code.wholeText();
            String fixed = resolveDoubleEscaped(normalizeCodeSpaces(text));
            if (!fixed.equals(text)) code.text(fixed);
        }

        // Galleries -> {{< gallery >}}. Before both passes below, so a gallery's
        // own images are never lifted out of it as standalone shortcodes.
        for (Element el : outermostMatches(content, SELECTOR_GALLERY)) {
            String shortcode = galleryShortcode(el);
            if (shortcode == null) continue; // no images in it -- leave it to the passes below
            String token = PRESERVE_TOKEN + preserved.size() + PRESERVE_TOKEN_END;
            preserved.add(shortcode);
            el.replaceWith(new Element("p").text(token));
        }

        // Code widgets and embeds -> raw HTML.
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

        // EVERY id goes, on every element, and this is deliberately not a list of
        // the ones we have seen. Flexmark carries an id over as Markdown attribute
        // syntax (`{#id}`), and WordPress stamps ids on anything: headings
        // (`<h2 id="h2-2-where-the-dedup-check-actually-lives">`, positional, and
        // corrupt at the source often enough -- "Podcast Apps" becomes
        // `h2-1--odcast-pps`), links (`<a id="31db">` on every paragraph of a
        // Medium import), captions (`<p id="caption-attachment-36528">`), the
        // editor's read-more break (`<span id="more-36262">`) and whole paragraphs
        // (`<p class="sect0" id="_quarkus_unpacked_...">` on an Asciidoc import).
        //
        // Only the heading case round trips: Goldmark applies an attribute block
        // to a heading, so everything else is rendered to the reader as the
        // literal text "{#caption-attachment-36528}" at the end of a paragraph.
        // 1268 of those were live across 91 posts, which is what taking the
        // elements one at a time cost -- headings and links were each fixed here
        // when they were noticed, and the next kind of element simply started the
        // bug again. Nothing downstream reads an id, so there is nothing to keep
        // a list for. See cleanup/HeadingAnchors.java, which repairs content/.
        //
        // AFTER the preserve passes above, so a raw-HTML block keeps its own ids:
        // those are rendered verbatim rather than converted, and an author's
        // hand-written footnote or in-page anchor inside one still has to resolve.
        content.select("[id]").removeAttr("id");

        // Brand capitalization, applied LAST -- after every preserve pass above, so
        // a code fence, a widget, a gallery and a shortcode are all placeholder
        // tokens by now and none of them can be rewritten. What is left in the DOM
        // is prose plus the inline <code> spans, which normalizeBrandName skips.
        normalizeBrandName(content);

        String md = FlexmarkHtmlConverter.builder(CONVERTER_OPTIONS).build()
                .convert(content.html()).trim();

        // Drop the <hr> rules and the <br> spacers the converter just emitted.
        // Done BEFORE the placeholders are restored, so a code sample that
        // happens to contain the same run of asterisks -- or a raw-HTML block
        // with a <br> on its own line -- can never be hit.
        md = FLEXMARK_THEMATIC_BREAK.matcher(md).replaceAll("");
        md = STANDALONE_BREAK.matcher(md).replaceAll("");

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

    /**
     * Wraps code in a Markdown fence tagged with the language.
     *
     * The fence is made one backtick longer than the longest backtick run in the
     * code, so a snippet that itself contains ``` (Markdown examples, shell
     * heredocs) can't terminate its own block early.
     *
     * Trailing whitespace on the last line is trimmed but leading indentation is
     * untouched -- it's significant in Python, YAML and anything wrapped.
     */
    public static String codeFence(String code, String enlighterLanguage) {
        String body = code == null ? "" : resolveDoubleEscaped(
                normalizeCodeSpaces(code.replace("\r\n", "\n")).replaceAll("\\s+$", ""));
        int longestRun = 0, run = 0;
        for (char c : body.toCharArray()) {
            run = (c == '`') ? run + 1 : 0;
            longestRun = Math.max(longestRun, run);
        }
        String fence = "`".repeat(Math.max(3, longestRun + 1));
        return fence + fenceLanguage(enlighterLanguage) + "\n" + body + "\n" + fence;
    }

    /**
     * Capitalizes the brand name in every prose text node of a body.
     *
     * Element-by-element rather than over the converted Markdown, because at
     * this point in toMarkdown the fences, widgets, galleries and shortcodes are
     * placeholder tokens, so the only thing left that must not be rewritten is
     * an inline <code> span -- and inCode() already answers that question. Doing
     * it on the Markdown string instead would mean re-finding backtick spans by
     * hand.
     *
     * Attribute values (href, src, alt, title) are deliberately out of scope: a
     * text-node walk never sees them, which is what keeps a URL containing the
     * words out of it.
     */
    private static void normalizeBrandName(Element content) {
        for (Element el : content.select("*")) {
            if ("pre".equals(el.tagName()) || "code".equals(el.tagName()) || inCode(el)) continue;
            for (TextNode tn : el.textNodes()) {
                String text = tn.getWholeText();
                String fixed = normalizeBrandName(text);
                if (!fixed.equals(text)) tn.text(fixed);
            }
        }
    }

    /**
     * "friends of openjdk", in any casing, becomes "Friends Of OpenJDK".
     *
     * Public so the callers that handle a body's METADATA rather than its prose
     * -- a scraped title, description or author bio, none of which pass through
     * toMarkdown -- can apply the same rule. Change the capitalization here and
     * it changes everywhere; there is no second copy of the brand name.
     *
     * Only the full three-word phrase matches, so "a friend of OpenJDK" and a
     * bare "OpenJDK" are both left as written. See BRAND_NAME.
     */
    public static String normalizeBrandName(String text) {
        if (text == null) return "";
        return BRAND_NAME.matcher(text).replaceAll("Friends$1Of$2OpenJDK");
    }

    /**
     * Puts back the spaces WordPress dropped when it built a post's meta
     * description by stripping the tags out of the body.
     *
     * Yoast generates the description by concatenating the body's text nodes with
     * no separator, so a heading runs straight into the paragraph that follows
     * it and the boundary punctuation loses its space:
     *
     *     ...using the Service Layer pattern.What you'll learn
     *     ...we'll walk through:What sharding actually is
     *
     * That reaches the reader in the search result, in the link preview and in
     * the BlogPosting JSON-LD, so it's worth repairing -- but ONLY where the
     * missing space is provable, because the same shape is how a Java identifier
     * is spelled. Three real cases in content/ are NOT damage:
     *
     *     System.Logger          FetchType.EAGER          sun.misc.Unsafe
     *
     * Hence two guards, each of which rules one of them out:
     *
     *   1. The word ending at the punctuation must start LOWERCASE. A dotted name
     *      whose first segment is capitalised is a type, not a sentence
     *      (System.Logger, FetchType.EAGER).
     *   2. The whitespace-delimited token holding the punctuation must contain no
     *      OTHER `.`. A chain of dots is a package path, never two sentences
     *      (sun.misc.Unsafe).
     *
     * A single-letter word is skipped too, so initials ("J.K. Rowling" with the
     * space already missing) aren't split mid-name.
     *
     * `:` is included alongside `.!?` because a colon jammed against a capital is
     * the same heading-boundary artefact ("we'll walk through:What"), and unlike
     * `.` it has no identifier spelling to collide with -- a time reads "10:30",
     * digits not capitals.
     *
     * Idempotent: once a space is there the pattern no longer matches. Applied to
     * scraped descriptions, never to bodies -- a body keeps its real markup, so it
     * never had this damage in the first place.
     */
    public static String repairRunOnSentences(String text) {
        if (text == null || text.isBlank()) return text;
        Matcher m = RUN_ON.matcher(text);
        StringBuilder out = new StringBuilder();
        int last = 0;
        while (m.find()) {
            // m.group(1) is the word before the punctuation, m.group(2) the mark.
            String word = m.group(1);
            if (word.length() >= 2
                    && Character.isLowerCase(word.charAt(0))
                    && !hasOtherDot(text, m.start(2))) {
                out.append(text, last, m.start(2)).append(m.group(2)).append(' ');
                last = m.end(2);
            }
        }
        return last == 0 ? text : out.append(text.substring(last)).toString();
    }

    /** A word, then sentence punctuation, then an immediate capital. */
    private static final Pattern RUN_ON = Pattern.compile("([A-Za-z]+)([.!?:])(?=[A-Z])");

    /**
     * True when the whitespace-delimited token containing the character at
     * {@code at} holds a `.` other than that one -- i.e. it is a dotted path such
     * as sun.misc.Unsafe rather than a sentence boundary.
     */
    private static boolean hasOtherDot(String text, int at) {
        int start = at;
        while (start > 0 && !Character.isWhitespace(text.charAt(start - 1))) start--;
        int end = at + 1;
        while (end < text.length() && !Character.isWhitespace(text.charAt(end))) end++;
        for (int i = start; i < end; i++) {
            if (i != at && text.charAt(i) == '.') return true;
        }
        return false;
    }

    /**
     * Strips the " - by &lt;Author&gt;" tail Yoast appends when it builds a meta
     * description by truncating the body instead of using one the author wrote:
     *
     *     ...copy-pasting code that may or may not still - by Cristobal Escobar
     *
     * 290 of 2148 descriptions in content/ carry it. It is worth removing for
     * two reasons: it restates a byline the search result and the link preview
     * already show separately, and it does so inside the ~155 characters Google
     * renders -- so on a long description the suffix is the part that survives
     * and the actual subject is the part that gets cut.
     *
     * AUTHOR-AWARE ON PURPOSE, and this is the whole safety story. " - by
     * &lt;Capitalised Words&gt;" at the end of a sentence is also how a human
     * writes "a new translation of the Odyssey - by Emily Wilson", and no
     * lexical rule tells the two apart. So the tail is only removed when the
     * name it gives is one of the post's OWN credited authors -- which is
     * exactly what makes it Yoast's stamp rather than the author's prose. 289 of
     * the 290 match that way; the one that does not is a description with the
     * byline buried mid-string, left alone and reported.
     *
     * The remainder is then re-terminated with an ellipsis when it does not
     * already end in sentence punctuation. That is not cosmetic: Yoast cut the
     * body mid-sentence to make room for the byline, so removing the byline
     * leaves a fragment ("...code that may or may not still"), and a trailing
     * "…" is how a snippet says it is a snippet. Only applied where a byline was
     * actually removed -- a description that merely lacks a full stop is
     * somebody's deliberate wording, not a truncation.
     *
     * Idempotent: with the tail gone the pattern no longer matches, and the
     * ellipsis is itself terminal punctuation.
     *
     * @param authorNames the display names of the post's credited authors; an
     *                    empty or null collection means nothing is stripped.
     */
    public static String stripBylineSuffix(String description, java.util.Collection<String> authorNames) {
        if (description == null || description.isBlank() || authorNames == null) return description;
        for (String name : authorNames) {
            if (name == null || name.isBlank()) continue;
            String tail = " - by " + name.trim();
            if (!description.endsWith(tail)) continue;
            String head = description.substring(0, description.length() - tail.length()).stripTrailing();
            if (head.isBlank()) return description;   // the byline was the whole thing
            return endsSentence(head) ? head : head + "…";
        }
        return description;
    }

    /** True when the text already closes on sentence punctuation or a quote. */
    private static boolean endsSentence(String text) {
        char c = text.charAt(text.length() - 1);
        return c == '.' || c == '!' || c == '?' || c == '…' || c == '"' || c == '\u201d' || c == ')';
    }

    /**
     * Resolves the HTML entities left over in code by WordPress's double-escaping.
     *
     * Some post bodies store a Java lambda arrow as `-&amp;gt;`, so the HTML
     * parser hands us `-&gt;` and that lands in the fence verbatim. The live WP
     * site renders those blocks wrong too (it really does show `-&gt;`), so this
     * is a long-standing content bug rather than something the conversion
     * introduced -- but now that the fence is the storage format, it's ours.
     *
     * WHY NOT JUST UNESCAPE EVERYTHING AGAIN. A second blanket pass can't tell
     * WordPress's damage apart from an entity the author meant literally, because
     * both arrive here looking identical. Two real cases in content/ prove it:
     * a JSF sample whose `value="Food &amp; Culture"` attribute is CORRECT XML,
     * and a post that appends the string `"&nbsp;"` to build HTML padding --
     * unescaping either one corrupts the sample. So only entities that cannot
     * plausibly be literal source are resolved:
     *
     *   &lt; &gt; &quot; &apos; (+ numeric forms)  -- always; no snippet in
     *       content/ wants a literal one, they're all mangled operators/generics.
     *   &amp;  -- ONLY in the three shapes where it is unambiguously an operator
     *       rather than markup: the `&&` operator, a shell redirect (`2>&1`) and
     *       a URL query separator (`?a=1&b=2`). A bare `&amp;` is left alone.
     *
     * Anything else, `&nbsp;` included, is left exactly as it is. Idempotent:
     * once an entity is resolved there is nothing left for a second run to find.
     */
    public static String resolveDoubleEscaped(String code) {
        if (code == null || code.indexOf('&') < 0) return code;
        String s = code;
        s = s.replaceAll("&lt;|&#0*60;|&#[xX]0*3[cC];", "<");
        s = s.replaceAll("&gt;|&#0*62;|&#[xX]0*3[eE];", ">");
        s = s.replaceAll("&quot;|&#0*34;|&#[xX]0*22;", "\"");
        s = s.replaceAll("&apos;|&#0*39;|&#[xX]0*27;", "'");
        // `&&` -- replaced as a pair so both halves resolve in one pass.
        s = s.replace("&amp;&amp;", "&&");
        // Shell redirect: `2>&1`. Runs after &gt; above, so the `>` is real by now.
        s = s.replaceAll("(?<=>)&amp;", "&");
        // URL query separator: preceded by the end of a value, followed by `key=`.
        s = s.replaceAll("(?<=[?&\\w])&amp;(?=[A-Za-z_][\\w.-]*=)", "&");
        return s;
    }

    /**
     * Turns the non-breaking spaces WordPress uses for code indentation into
     * ordinary ones.
     *
     * WP bodies indent samples with `&nbsp;`, which Jsoup hands over as U+00A0.
     * It LOOKS like an indent in the rendered block but isn't one: copy the
     * sample into an editor and the compiler/shell chokes on a character it
     * doesn't recognise as whitespace, which is the whole point of a code block
     * on this site. EnlighterToFences has always done this to the blocks
     * it converted; the scraper did not, so a re-scrape put 10,270 of them back
     * across 36 posts. The rule lives here now so both paths agree -- the same
     * arrangement as resolveDoubleEscaped.
     *
     * U+00A0 only. A narrow/thin/zero-width space is never accidental
     * indentation, so those are left alone. Idempotent.
     */
    public static String normalizeCodeSpaces(String code) {
        return code == null ? null : code.replace('\u00a0', ' ');
    }

    /**
     * Strips the surplus escaping WordPress leaves in a URL attribute.
     *
     * Companion to resolveDoubleEscaped, for the other place WP damage lands.
     * Jsoup has already decoded one level by the time an attribute value gets
     * here, so a correctly-stored `?a=1&amp;b=2` arrives as `?a=1&b=2` and this
     * finds nothing to do. A LEFTOVER `&amp;` therefore means the source was
     * over-escaped, and there is no reading of a URL where the query separator
     * is meant to be the six characters `&amp;` -- so it is resolved, repeatedly,
     * since WP bodies carry `&amp;amp;` (three levels) as well as two.
     *
     * Deliberately unconditional, unlike the `&amp;` handling in
     * resolveDoubleEscaped: that method inspects arbitrary code, where `&amp;`
     * can be correct source, whereas this one only ever sees an href/src.
     * Idempotent: once resolved there is nothing left for a second run.
     */
    public static String resolveEscapedUrl(String url) {
        if (url == null || !url.contains("&amp;")) return url;
        String prev;
        do {
            prev = url;
            url = url.replace("&amp;", "&");
        } while (!url.equals(prev));
        return url;
    }

    /**
     * Undoes Cloudflare's "Email Address Obfuscation".
     *
     * foojay.io is behind Cloudflare with that feature on, so every address in
     * the HTML it serves -- in a mailto: href, in prose, and inside a <pre> --
     * is rewritten server-side into a placeholder plus an XOR-encoded copy, and
     * only put back by a script that runs in the READER's browser:
     *
     *   <a href="/cdn-cgi/l/email-protection" class="__cf_email__"
     *      data-cfemail="94fcf1f8f8fbd4f2fbfbfef5edbafdfb">[email&#160;protected]</a>
     *   <a href="/cdn-cgi/l/email-protection#0d327e78...">let us know</a>
     *
     * A scraper runs no JavaScript, so without this pass the literal text
     * "[email protected]" is what lands in content/ -- and it is not only
     * addresses: Cloudflare's matcher is a loose `x@y`, so `git@github.com` in a
     * shell sample and `javafx.base@14.0.2` in `java --list-modules` output get
     * mangled the same way, inside fenced code. Decoding therefore has to happen
     * FIRST in toMarkdown, before the code-block and preserve passes lift those
     * bodies out as text.
     *
     * The encoding is trivially reversible (first hex byte is the XOR key), so
     * nothing is guessed. Two shapes, and they nest -- an obfuscated mailto link
     * around an obfuscated address is encoded twice, with a different key each
     * time:
     *   .__cf_email__ + data-cfemail  -> the address itself
     *   href .../email-protection#HEX -> the whole mailto: target, which for a
     *                                    share-by-email button is a recipient-less
     *                                    `?subject=...&body=...`
     *
     * A decode that is not an address is left as text rather than linked: the
     * matcher has false positives (a post has `<code>@name</code>` wrapped this
     * way), and `mailto:code&gt;@name` would be worse than the words.
     */
    static void decodeCloudflareEmails(Element content) {
        // Links first: the href carries the whole target, and the anchor text may
        // itself be an obfuscated address handled by the pass below.
        for (Element a : content.select("a[href*=/cdn-cgi/l/email-protection#]")) {
            String href = a.attr("href");
            String decoded = decodeCfEmail(href.substring(href.indexOf('#') + 1));
            if (decoded == null) continue;
            if (inCode(a)) {
                a.replaceWith(new TextNode(decoded)); // see the code note below
            } else if (decoded.startsWith("?") || looksLikeEmail(decoded)) {
                a.attr("href", "mailto:" + decoded);
            } else {
                a.unwrap(); // false positive -- keep the words, drop the dead link
            }
        }
        for (Element el : content.select(".__cf_email__[data-cfemail]")) {
            String decoded = decodeCfEmail(el.attr("data-cfemail"));
            if (decoded == null) continue;
            // Inside code, always plain text: the address was a literal in a
            // shell command or a config sample, and a link there is not just
            // ugly -- Flexmark renders an <a> inside <pre> as its bare HREF, so
            // leaving one turns `--docker-email="a@b"` into
            // `--docker-email="mailto:a@b"`.
            if (!inCode(el) && "a".equals(el.tagName()) && looksLikeEmail(decoded)) {
                el.attr("href", "mailto:" + decoded).text(decoded);
                el.removeClass("__cf_email__").removeAttr("data-cfemail");
            } else {
                el.replaceWith(new TextNode(decoded));
            }
        }
    }

    /**
     * Decodes one Cloudflare-obfuscated value: hex bytes, the first of which is
     * the XOR key for all the others. Returns null on anything that isn't that
     * (so a caller leaves the element alone rather than writing nonsense).
     */
    static String decodeCfEmail(String hex) {
        if (hex == null) return null;
        hex = hex.trim();
        if (hex.length() < 4 || hex.length() % 2 != 0 || !hex.matches("(?i)[0-9a-f]+")) return null;
        int key = Integer.parseInt(hex.substring(0, 2), 16);
        // Cloudflare XORs the UTF-8 BYTES of the source, so the result is read
        // back as UTF-8 rather than assembled char by char.
        byte[] bytes = new byte[hex.length() / 2 - 1];
        for (int i = 2; i < hex.length(); i += 2) {
            bytes[i / 2 - 1] = (byte) (Integer.parseInt(hex.substring(i, i + 2), 16) ^ key);
        }
        String decoded = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
        // The source was HTML, so an address inside markup arrives entity-encoded.
        return decoded.isEmpty() ? null : Parser.unescapeEntities(decoded, false);
    }

    /** Whether an element sits inside a code block or code span. */
    private static boolean inCode(Element el) {
        for (Element p = el.parent(); p != null; p = p.parent()) {
            if ("pre".equals(p.tagName()) || "code".equals(p.tagName())) return true;
        }
        return false;
    }

    /** Loose enough for the addresses WordPress bodies hold, strict enough to
     *  reject Cloudflare's false positives (`code&gt;@name&lt;/code`). */
    static boolean looksLikeEmail(String s) {
        return s.matches("[^\\s@<>\"']+@[^\\s@<>\"']+\\.[A-Za-z][^\\s@<>\"']*");
    }

    /** Applies resolveEscapedUrl to every href/src in the body. */
    static void repairEscapedUrls(Element content) {
        for (Element el : content.select("[href], [src]")) {
            for (String attr : new String[]{"href", "src"}) {
                if (!el.hasAttr(attr)) continue;
                String fixed = resolveEscapedUrl(el.attr(attr));
                if (!fixed.equals(el.attr(attr))) el.attr(attr, fixed);
            }
        }
    }

    /**
     * Rewrites foojay's own LEGACY paths in body links to where they resolve today.
     *
     * WHY. `/today/` is not foojay's original URL scheme -- `/blog/` was, and post
     * bodies written in 2020-2021 still link that way. WordPress covers it with a
     * host-level redirect, so the live HTML we scrape hands those links back
     * verbatim: 22 of them were fixed by hand in "Link fixes" (f0bd683), and the
     * next re-scrape put every one back. Same shape as cleanup/images.py's renames
     * -- a hand fix that a re-scrape silently reverts, in a diff too large to spot
     * it in.
     *
     * It is also the durable fix rather than a tidy-up. These three are the rules
     * that CANNOT become `aliases:` (see cutover/legacy-redirects.md: they are
     * regexes, so they have to be configured on Cloudflare), which means a stored
     * `/blog/` link is the one kind of internal link that depends on host config
     * surviving cutover. The 89 concrete rules are deliberately NOT resolved here:
     * those are `aliases:` in content/, Hugo emits a redirect page for each, and
     * nothing outside the repo has to be right for them to work.
     *
     * The rules are lifted from that file, which is the Redirection plugin's own
     * export and not a guess:
     *   /blog/<rest>                 -> /today/<rest>     (209,365 hits)
     *   /almanac/(jdk|java)-<n>...   -> javaalmanac.io    (102,636)  -- off-site
     *   /docs/<rest>                 -> /today/           (530)
     * plus http -> https on foojay's own host, because the page is served over
     * https and a stored http link is one more redirect (same call as the avatar
     * URLs in fetch/JavaChampions.java).
     *
     * Links to any OTHER host are untouched, and so are images: a `/blog/` image
     * path never existed, and localizeImages resolves those against the live host.
     */
    static void normalizeLegacyUrls(Element content) {
        for (Element a : content.select("a[href]")) {
            String href = a.attr("href");
            String fixed = normalizeFoojayUrl(href);
            if (!fixed.equals(href)) a.attr("href", fixed);
        }
    }

    // Either an absolute foojay.io URL or a root-relative path; group 1 is the
    // scheme (null when relative), group 2 the path with its query/fragment.
    private static final Pattern FOOJAY_URL =
            Pattern.compile("(?i)^(?:(https?)://(?:www\\.)?foojay\\.io)?(/\\S*)$");
    private static final Pattern LEGACY_ALMANAC =
            Pattern.compile("(?i)^/almanac/(?:jdk|java)-(\\d+)");

    /** One URL through the rules above; anything else is returned unchanged. */
    public static String normalizeFoojayUrl(String url) {
        if (url == null || url.isBlank()) return url;
        Matcher m = FOOJAY_URL.matcher(url.trim());
        if (!m.matches()) return url;
        String scheme = m.group(1);      // null => root-relative, so already ours
        String path = m.group(2);

        Matcher almanac = LEGACY_ALMANAC.matcher(path);
        if (almanac.find()) return "https://javaalmanac.io/jdk/" + almanac.group(1);

        String moved = path;
        if (path.equals("/blog")) moved = "/today/";
        else if (path.startsWith("/blog/")) moved = "/today/" + path.substring("/blog/".length());
        else if (path.equals("/docs") || path.startsWith("/docs/")) moved = "/today/";

        // A relative link that did not move is left exactly as written -- there is
        // no scheme on it to upgrade, and absolutising it is not this pass's job.
        if (scheme == null) return moved;
        return "https://foojay.io" + moved;
    }

    /**
     * Maps a WordPress `data-enlighter-language` value to the Markdown fence tag
     * an author would naturally type.
     *
     * The stored values are NOT all real EnlighterJS languages -- the plugin
     * accepts free text -- so they are normalised to the tag an author would
     * type, which the render hook maps back to a real lexer.
     *
     * Note "vb" is only an ALIAS of the `visualbasic` lexer, so the canonical
     * name is emitted; the render hook's supported-language list is keyed on
     * canonical names.
     *
     * "generic"/"raw"/"text" mean "no highlighting" and become a bare fence.
     */
    /**
     * The language named by an element's `language-x` / `lang-x` class, or "".
     * The convention every Markdown-to-HTML converter emits for a fence, so it
     * is what an author's own ```xml survived as through the import into
     * WordPress -- see the EnlighterJS pass in toMarkdown for why it wins over
     * the plugin's attribute when the two disagree.
     */
    static String languageClass(Element el) {
        for (String cls : el.classNames()) {
            String c = cls.toLowerCase(Locale.ROOT);
            if (c.startsWith("language-")) return c.substring("language-".length());
            if (c.startsWith("lang-")) return c.substring("lang-".length());
        }
        return "";
    }

    public static String fenceLanguage(String enlighterLanguage) {
        String l = enlighterLanguage == null ? "" : enlighterLanguage.trim().toLowerCase(Locale.ROOT);
        return switch (l) {
            case "", "generic", "raw", "text", "plain", "plaintext" -> "";
            case "shell", "console", "sh", "zsh" -> "bash";
            case "js" -> "javascript";
            case "ts" -> "typescript";
            case "yml" -> "yaml";
            case "c++", "cc" -> "cpp";
            case "py" -> "python";
            case "kt" -> "kotlin";
            case "rb" -> "ruby";
            case "cs" -> "csharp";
            case "golang" -> "go";
            case "md" -> "markdown";
            case "bat", "cmd" -> "batch";
            case "gradle" -> "groovy";
            case "vb", "vbnet", "vba" -> "visualbasic";
            case "assembly" -> "asm";
            default -> l.replaceAll("[^a-z0-9+#._-]", "");
        };
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

    /**
     * Turns a WordPress gallery block into the {{< gallery >}} shortcode
     * (themes/foojay/layouts/shortcodes/gallery.html), or null if it holds no
     * images at all -- in which case the caller leaves it to the raw-HTML pass.
     *
     * Both WordPress shapes are handled: the modern figure-of-figures, and the
     * older one nesting a <ul class="blocks-gallery-grid">. What survives is
     * what the page actually shows -- the filenames in order, each image's own
     * caption and alt text, one caption for the whole gallery, and the column
     * count from WordPress's `columns-N` class:
     *
     *     {{< gallery cols="2" caption="The three views" >}}
     *     one-1024x768.png | Its caption
     *     two-1024x768.png | | Alt text, where it differs from the caption
     *     {{< /gallery >}}
     *
     * Deliberately dropped: the <a href> WordPress wraps each image in. It
     * points at the full-size original of the `-1024x768` thumbnail beside it,
     * and the shortcode DERIVES that link from the filename instead (see the
     * template), so recording it here would be storing something the build can
     * work out. The three links to a WordPress attachment PAGE go with it --
     * those pages don't exist after cutover, and the lightbox ignores a link
     * that isn't an image anyway.
     */
    static String galleryShortcode(Element gallery) {
        List<String> lines = new ArrayList<>();
        for (Element img : gallery.select("img")) {
            String src = img.attr("src").trim();
            if (src.isEmpty()) continue;
            String caption = ownCaption(img, gallery);
            String alt = sanitizeField(img.attr("alt"));
            // The caption doubles as the alt text in the template, so a third
            // field is only written when the two genuinely differ.
            String line = sanitizeField(src);
            if (!alt.isEmpty() && !alt.equals(caption)) line += " | " + caption + " | " + alt;
            else if (!caption.isEmpty()) line += " | " + caption;
            lines.add(line);
        }
        if (lines.isEmpty()) return null;

        StringBuilder sb = new StringBuilder("{{< gallery");
        Matcher cm = GALLERY_COLUMNS.matcher(gallery.className());
        // `columns-default` (WordPress's own default is 3) carries no
        // information the template doesn't already have -- left off.
        if (cm.find()) sb.append(" cols=\"").append(cm.group(1)).append('"');
        String caption = sanitizeField(galleryCaption(gallery));
        if (!caption.isEmpty()) sb.append(" caption=").append(quoteParam(caption));
        sb.append(" >}}\n");
        for (String line : lines) sb.append(line).append('\n');
        return sb.append("{{< /gallery >}}").toString();
    }

    /** The caption belonging to one gallery image: the figcaption of the
     *  <figure>/<li> wrapping it, never the gallery's own. */
    private static String ownCaption(Element img, Element gallery) {
        for (Element el = img.parent(); el != null && el != gallery; el = el.parent()) {
            if (!"figure".equals(el.tagName()) && !"li".equals(el.tagName())) continue;
            for (Element child : el.children()) {
                if ("figcaption".equals(child.tagName())) return sanitizeField(child.text());
            }
        }
        return "";
    }

    /** The caption for the gallery as a whole: WordPress marks it
     *  `blocks-gallery-caption`, and in both shapes it is a direct child of the
     *  gallery element (so an item's caption can never be picked up here). */
    private static String galleryCaption(Element gallery) {
        Element marked = gallery.selectFirst("figcaption.blocks-gallery-caption");
        if (marked != null) return marked.text();
        for (Element child : gallery.children()) {
            if ("figcaption".equals(child.tagName())) return child.text();
        }
        return "";
    }

    /** One field of a gallery line: single-line, and free of the `|` the
     *  template splits filename / caption / alt on. */
    private static String sanitizeField(String value) {
        if (value == null) return "";
        return value.replace('|', '/').replaceAll("\\s+", " ").trim();
    }

    /** A shortcode parameter value. Hugo takes a backtick-delimited raw string,
     *  which is what a caption containing a double quote needs -- one does. */
    private static String quoteParam(String value) {
        if (!value.contains("\"")) return '"' + value + '"';
        return '`' + value.replace('`', '\'') + '`';
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
     * downloaded file is not fetched again, and an already-RE-ENCODED one is
     * recognised under its new extension rather than fetched back (see
     * convertedSibling).
     */
    static String localizeImage(String absoluteUrl, Options opts, String itemSubpath) {
        String filename = localImageFilename(absoluteUrl, opts.localHostSuffix);
        if (filename == null) return null;
        String dirRel = (itemSubpath == null || itemSubpath.isBlank()) ? "" : itemSubpath + "/";
        Path out = opts.imageBaseDir.resolve(dirRel + filename);
        try {
            if (!Files.exists(out)) {
                String converted = convertedSibling(out.getParent(), filename, opts);
                if (converted != null) {
                    System.out.println("  keeping " + converted + " (cleanup/images.py re-encoded "
                            + filename + "); not re-downloading");
                    return opts.imageUrlPrefix + dirRel + converted;
                }
                Connection.Response res = Jsoup.connect(absoluteUrl)
                        .userAgent(opts.userAgent)
                        .timeout(opts.timeoutMs)
                        .ignoreContentType(true)
                        .maxBodySize(0)
                        .execute();
                Files.createDirectories(out.getParent());
                Files.write(out, res.bodyAsBytes());
                opts.fetchedThisItem.add(filename);
            }
            return opts.imageUrlPrefix + dirRel + filename;
        } catch (IOException e) {
            System.err.println("  image download failed: " + absoluteUrl + " -> " + e.getMessage());
            return null;
        }
    }

    /**
     * cleanup/images.py's container conversions -- source extension -> the extension
     * it writes. Each is a plain Path.with_suffix() there, so the stem never moves
     * and the sibling is derivable from the name alone.
     */
    private static final Map<String, String> RE_ENCODED_AS = new LinkedHashMap<>();
    static {
        RE_ENCODED_AS.put(".gif", ".webp");  // convert_gif: animated GIF -> animated WebP
        RE_ENCODED_AS.put(".png", ".jpg");   // png_to_jpeg: large PNG -> JPEG
    }

    /**
     * The name a file has ALREADY been re-encoded to by cleanup/images.py, or null.
     *
     * WHY THIS EXISTS. That script shrank content/ from 1.26 GB to 0.69 GB to fit
     * GitHub Pages' 1 GB artifact limit, and two of its passes change the FILENAME:
     * 42 animated GIFs became WebP and 518 large PNGs became JPEG. Without this,
     * every re-scrape looks for foo.gif, does not find it, downloads the 52 MB
     * original back and rewrites the body reference to foo.gif -- so the whole
     * saving is undone, the re-encoded file is orphaned, and the diff is hundreds
     * of files of pure churn. (The resize pass keeps the filename, which is why it
     * was always safe: Files.exists short-circuits and images.py re-shrinks the
     * fresh original harmlessly.)
     *
     * Derived, not recorded: the evidence is the file images.py left on disk, so
     * there is no manifest to keep in step and nothing to unset. It also dies with
     * the scrapers at cutover. transfer/Authors.java has been immune all along for
     * the same reason -- its localizeAvatar() matches on the basename and ignores
     * the extension, which is why 203 converted -full.jpg avatars survive a
     * re-scrape; this is that rule, narrowed to the two conversions that exist so a
     * post referencing a genuinely different foo.png and foo.svg is never confused.
     *
     * The exact name is checked by the caller FIRST, so a bundle shipping both
     * foo.gif and a hand-made foo.webp (the case images.py itself skips) keeps
     * using the GIF. And a sibling this run downloaded is never treated as a
     * conversion -- that is WordPress genuinely serving both, not our re-encode.
     */
    static String convertedSibling(Path dir, String filename, Options opts) {
        int dot = filename.lastIndexOf('.');
        if (dot <= 0) return null;
        String ext = filename.substring(dot).toLowerCase(Locale.ROOT);
        String target = RE_ENCODED_AS.get(ext);
        if (target == null) return null;
        String candidate = filename.substring(0, dot) + target;
        if (opts.fetchedThisItem.contains(candidate)) return null;
        return Files.isRegularFile(dir.resolve(candidate)) ? candidate : null;
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
