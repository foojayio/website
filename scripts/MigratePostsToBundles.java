///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 17+

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * One-time migration: converts the flat post layout
 *   content/posts/<year>/<month>/<slug>.md      (+ static/images/posts/<year>/<month>/<slug>/*)
 * into Hugo leaf bundles
 *   content/posts/<year>/<month>/<day>/<slug>/index.md   (+ images alongside)
 * so each post and its images live in one directory. Image references
 * (frontmatter `image:`, Markdown, {{< img >}} shortcodes, raw <img>) are
 * rewritten from /images/posts/.../<slug>/<file> to the bare <file>, which Hugo
 * resolves as a page-bundle resource.
 *
 * Idempotent: a post already at <dir>/<slug>/index.md is skipped, and images
 * already moved are left in place.
 *
 * Usage: jbang scripts/MigratePostsToBundles.java
 */
public class MigratePostsToBundles {

    static final Path POSTS = Path.of("content/posts");
    static final Path IMG_ROOT = Path.of("static/images/posts");
    static final Pattern DATE = Pattern.compile("(?m)^date:\\s*\"?(\\d{4})-(\\d{2})-(\\d{2})");

    public static void main(String[] args) throws IOException {
        if (!Files.isDirectory(POSTS)) {
            System.err.println("No content/posts directory.");
            return;
        }
        List<Path> flat;
        try (Stream<Path> s = Files.walk(POSTS)) {
            flat = s.filter(p -> p.toString().endsWith(".md"))
                    .filter(p -> !p.getFileName().toString().equals("_index.md"))
                    .filter(p -> !p.getFileName().toString().equals("index.md")) // already a bundle
                    .sorted()
                    .toList();
        }

        int migrated = 0, skipped = 0, failed = 0;
        for (Path md : flat) {
            try {
                if (migrate(md)) migrated++;
                else skipped++;
            } catch (Exception e) {
                System.err.println("FAILED: " + md + " -> " + e.getMessage());
                failed++;
            }
        }
        pruneEmptyDirs(IMG_ROOT);
        pruneEmptyDirs(POSTS);
        System.out.printf("Done. migrated=%d skipped=%d failed=%d%n", migrated, skipped, failed);
    }

    static boolean migrate(Path md) throws IOException {
        String slug = stripExt(md.getFileName().toString());
        String text = Files.readString(md);

        // Bundle directory: content/posts/<year>/<month>/<day>/<slug>/ from the
        // publish date; fall back to the file's current parent if the date is unparseable.
        Path bundleDir;
        Matcher dm = DATE.matcher(text);
        if (dm.find()) {
            bundleDir = POSTS.resolve(dm.group(1)).resolve(dm.group(2)).resolve(dm.group(3)).resolve(slug);
        } else {
            Path parentRel = POSTS.relativize(md.getParent());
            bundleDir = POSTS.resolve(parentRel).resolve(slug);
        }
        Path indexFile = bundleDir.resolve("index.md");
        if (Files.exists(indexFile)) return false; // already migrated

        // Find this post's image directory from its refs (…/<slug>/ prefix) and
        // rewrite those refs to bare filenames (page-bundle resources).
        Pattern refPrefix = Pattern.compile("/images/posts/[^\"')\\s]*?/" + Pattern.quote(slug) + "/");
        Matcher rm = refPrefix.matcher(text);
        Path imgDir = null;
        if (rm.find()) {
            String prefix = rm.group();                 // e.g. /images/posts/2026/07/<slug>/
            imgDir = Path.of("static" + prefix);        // static/images/posts/2026/07/<slug>/
            text = text.replace(prefix, "");            // -> bare filenames
        }

        Files.createDirectories(bundleDir);
        // Move images into the bundle.
        if (imgDir != null && Files.isDirectory(imgDir)) {
            try (Stream<Path> imgs = Files.list(imgDir)) {
                for (Path img : imgs.toList()) {
                    Files.move(img, bundleDir.resolve(img.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
        Files.writeString(indexFile, text);
        Files.delete(md);
        return true;
    }

    static void pruneEmptyDirs(Path root) throws IOException {
        if (!Files.isDirectory(root)) return;
        List<Path> dirs;
        try (Stream<Path> s = Files.walk(root)) {
            dirs = s.filter(Files::isDirectory).sorted(Comparator.reverseOrder()).toList();
        }
        for (Path d : dirs) {
            if (d.equals(root)) continue;
            try (Stream<Path> c = Files.list(d)) {
                if (c.findAny().isEmpty()) Files.delete(d);
            } catch (IOException ignored) {
                // non-empty or racing; leave it
            }
        }
    }

    static String stripExt(String name) {
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(0, dot) : name;
    }
}
