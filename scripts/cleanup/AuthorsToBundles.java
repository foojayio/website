///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 17+

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * One-time migration: converts the flat author layout
 *   content/authors/<letter>/<slug>.md   (+ static/images/author/<letter>/<slug>*.<ext>)
 * into Hugo leaf bundles
 *   content/authors/<slug>/index.md       (+ avatar images alongside)
 * so each author profile and its images (small `avatar` + large `avatarFull`) live
 * in one directory named after the slug. The `avatar:`/`avatarFull:` references are
 * rewritten from /images/author/.../<file> to the bare <file> (page-bundle
 * resources), and the now-redundant `slug:` frontmatter is dropped (the folder name
 * is the slug -- permalink :slugorcontentbasename).
 *
 * Idempotent. Usage: jbang scripts/cleanup/AuthorsToBundles.java
 */
public class AuthorsToBundles {

    static final Path AUTHORS = Path.of("content/authors");
    static final Path IMG_ROOT = Path.of("static/images/author");
    static final Pattern IMG_REF = Pattern.compile("/images/author/[^\"'\\s)]+");

    public static void main(String[] args) throws IOException {
        if (!Files.isDirectory(AUTHORS)) return;
        List<Path> flat;
        try (Stream<Path> s = Files.walk(AUTHORS)) {
            flat = s.filter(p -> p.toString().endsWith(".md"))
                    .filter(p -> !p.getFileName().toString().equals("_index.md"))
                    .filter(p -> !p.getFileName().toString().equals("index.md"))
                    .sorted().toList();
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
        pruneEmptyDirs(AUTHORS);
        System.out.printf("Done. migrated=%d skipped=%d failed=%d%n", migrated, skipped, failed);
    }

    static boolean migrate(Path md) throws IOException {
        String slug = stripExt(md.getFileName().toString());
        Path bundleDir = AUTHORS.resolve(slug);
        Path indexFile = bundleDir.resolve("index.md");
        if (Files.exists(indexFile)) return false; // already migrated

        String text = Files.readString(md);
        Files.createDirectories(bundleDir);

        // Move each referenced avatar image into the bundle and relativize the ref.
        Set<String> refs = new LinkedHashSet<>();
        Matcher m = IMG_REF.matcher(text);
        while (m.find()) refs.add(m.group());
        for (String ref : refs) {
            Path onDisk = Path.of("static" + ref);
            String base = ref.substring(ref.lastIndexOf('/') + 1);
            Path target = bundleDir.resolve(base);
            if (Files.exists(onDisk) && !Files.exists(target)) {
                Files.move(onDisk, target, StandardCopyOption.REPLACE_EXISTING);
            }
            text = text.replace(ref, base); // path -> bare filename (bundle resource)
        }

        text = text.replaceAll("(?m)^slug:.*\\R", ""); // folder name is the slug now
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
                // non-empty; leave it
            }
        }
    }

    static String stripExt(String name) {
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(0, dot) : name;
    }
}
