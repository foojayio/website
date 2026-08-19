///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.jsoup:jsoup:1.17.2
//SOURCES ../shared/HtmlToMarkdown.java
//JAVA 17+

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * One-off migration: rewrites every WordPress gallery block already sitting in
 * content/ as the {{< gallery >}} shortcode.
 *
 *     <figure class="wp-block-gallery has-nested-images columns-3 ...">
 *      <figure class="wp-block-image size-large">
 *       <a href="shot.png"><img src="shot-1024x768.png" alt="" ...></a>
 *      </figure>
 *      ... 20 more lines ...
 *     </figure>
 *
 * becomes
 *
 *     {{< gallery cols="3" >}}
 *     shot-1024x768.png
 *     ...
 *     {{< /gallery >}}
 *
 * WHY. Same reason the Enlighter blocks became fences: posts arrive as pull
 * requests (CONTRIBUTING.md) and nobody can be asked to type WordPress block
 * markup. A gallery is a list of filenames; that is now what content/ holds,
 * and the grid, the captions, the lightbox and the link to each image's
 * full-size original are the template's job (see
 * themes/foojay/layouts/shortcodes/gallery.html).
 *
 * The conversion scripts emit the shortcode directly from now on
 * (HtmlToMarkdown.galleryShortcode -- the SAME method this script calls, so
 * both agree by construction), which makes a re-scrape produce the same shape.
 * This script stays in the repo for as long as the WordPress site is still
 * being scraped, exactly like cleanup/EnlighterToFences.java.
 *
 * Usage:
 *   jbang scripts/cleanup/GalleriesToShortcode.java --dry-run   (report only, changes nothing)
 *   jbang scripts/cleanup/GalleriesToShortcode.java
 *   jbang scripts/cleanup/GalleriesToShortcode.java --path content/posts/2022
 *
 * Idempotent: a file with no gallery markup left is not rewritten. Frontmatter
 * is never touched -- only the body below the closing `---` is scanned.
 */
public class GalleriesToShortcode {

    static final Path DEFAULT_ROOT = Path.of("content");

    /** Opening tag of a gallery block. The matching close is found by counting
     *  <figure> depth, because the block nests one figure per image. */
    static final Pattern GALLERY_OPEN = Pattern.compile(
            "<figure\\b[^>]*\\bclass=\"[^\"]*\\b(?:wp-block-gallery|gallery)\\b[^\"]*\"[^>]*>",
            Pattern.CASE_INSENSITIVE);
    static final Pattern FIGURE_TAG = Pattern.compile("</?figure\\b[^>]*>", Pattern.CASE_INSENSITIVE);

    public static void main(String[] args) throws IOException {
        boolean dryRun = false;
        Path root = DEFAULT_ROOT;
        for (int i = 0; i < args.length; i++) {
            if ("--dry-run".equals(args[i])) dryRun = true;
            else if ("--path".equals(args[i]) && i + 1 < args.length) root = Path.of(args[++i]);
        }
        if (!Files.isDirectory(root)) {
            System.err.println("No such directory: " + root);
            System.exit(1);
        }

        List<Path> files;
        try (Stream<Path> s = Files.walk(root)) {
            files = s.filter(p -> p.toString().endsWith(".md")).sorted().toList();
        }

        int changedFiles = 0, galleries = 0, images = 0, skipped = 0;
        List<String> notes = new ArrayList<>();

        for (Path file : files) {
            String original = Files.readString(file);
            int split = bodyStart(original);
            String head = original.substring(0, split);
            String body = original.substring(split);
            if (!GALLERY_OPEN.matcher(body).find()) continue;

            StringBuilder out = new StringBuilder();
            int last = 0, fileGalleries = 0, fileImages = 0;
            Matcher m = GALLERY_OPEN.matcher(body);
            while (m.find(last)) {
                int end = closingFigure(body, m.start());
                if (end < 0) { // unbalanced markup -- leave the block exactly as it is
                    notes.add(file + ": unclosed gallery at offset " + m.start() + ", left untouched");
                    skipped++;
                    break;
                }
                String html = body.substring(m.start(), end);
                Element gallery = Jsoup.parseBodyFragment(html).body().selectFirst("figure");
                String shortcode = gallery == null ? null : HtmlToMarkdown.galleryShortcode(gallery);
                if (shortcode == null) { // no images in it -- not ours to rewrite
                    notes.add(file + ": gallery with no images, left untouched");
                    skipped++;
                    out.append(body, last, end);
                    last = end;
                    continue;
                }
                out.append(body, last, m.start());
                // The shortcode has to start its own line with a blank line
                // above it, or Goldmark folds it into the paragraph before.
                out.append(padBefore(out)).append(shortcode).append("\n");
                last = end;
                fileGalleries++;
                fileImages += shortcode.split("\n").length - 2; // minus the open/close lines
            }
            out.append(body.substring(last));

            String newBody = collapseBlankLines(out.toString());
            if (newBody.equals(body)) continue;
            changedFiles++;
            galleries += fileGalleries;
            images += fileImages;
            if (!dryRun) Files.writeString(file, head + newBody);
        }

        System.out.printf("%s %d file(s), %d gallery block(s), %d image(s)%n",
                dryRun ? "[dry-run] would change" : "Changed", changedFiles, galleries, images);
        if (skipped > 0) System.out.printf("%d block(s) left as raw HTML:%n", skipped);
        notes.forEach(n -> System.out.println("  " + n));
        if (dryRun) System.out.println("\nNothing written. Re-run without --dry-run to apply.");
    }

    /**
     * Index just past the </figure> that closes the block opening at {@code from},
     * or -1 if the markup never balances. Counted rather than matched with a
     * regex because a gallery contains one <figure> per image.
     */
    static int closingFigure(String body, int from) {
        Matcher m = FIGURE_TAG.matcher(body).region(from, body.length());
        int depth = 0;
        while (m.find()) {
            depth += m.group().startsWith("</") ? -1 : 1;
            if (depth == 0) return m.end();
        }
        return -1;
    }

    /**
     * Index of the first character after the frontmatter block, so the scan can
     * never touch YAML. Files without frontmatter are treated as all body.
     */
    static int bodyStart(String content) {
        if (!content.startsWith("---")) return 0;
        int end = content.indexOf("\n---", 3);
        if (end < 0) return 0;
        int nl = content.indexOf('\n', end + 1);
        return nl < 0 ? content.length() : nl + 1;
    }

    /** Collapses the runs of 3+ newlines a lifted-out block leaves behind. Safe
     *  here in a way it is not in the Enlighter migration: this script only ever
     *  removes HTML, and a gallery can't contain a fenced code block. */
    static String collapseBlankLines(String body) {
        return body.replaceAll("\\n{3,}", "\n\n");
    }

    /** Ensures the shortcode begins on a fresh line preceded by a blank one. */
    static String padBefore(StringBuilder out) {
        if (out.length() == 0) return "";
        if (out.charAt(out.length() - 1) != '\n') return "\n\n";
        if (out.length() >= 2 && out.charAt(out.length() - 2) != '\n') return "\n";
        return "";
    }
}
