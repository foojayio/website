///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 17+

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * One-off: renames post bundle folders whose name isn't a clean URL slug (emoji,
 * spaces, uppercase, ...) to a sanitized slug, keeping the old URL working via an
 * `aliases:` entry. Also drops any now-redundant `slug:` frontmatter (the folder
 * name is the slug -- permalink :slugorcontentbasename). Idempotent.
 *
 * Usage: jbang scripts/cleanup/SanitizeSlugs.java
 */
public class SanitizeSlugs {

    static final Path POSTS = Path.of("content/posts");
    static final Pattern CLEAN = Pattern.compile("[a-z0-9]+(?:[-_][a-z0-9]+)*");

    public static void main(String[] args) throws IOException {
        if (!Files.isDirectory(POSTS)) return;
        List<Path> indexes;
        try (Stream<Path> s = Files.walk(POSTS)) {
            indexes = s.filter(p -> p.getFileName().toString().equals("index.md")).sorted().toList();
        }

        int renamed = 0, skipped = 0;
        for (Path index : indexes) {
            Path dir = index.getParent();
            String slug = dir.getFileName().toString();
            if (CLEAN.matcher(slug).matches()) continue;

            String clean = sanitize(slug);
            if (clean.isBlank()) {
                System.err.println("SKIP (empty after sanitize): " + dir);
                skipped++;
                continue;
            }
            Path target = dir.resolveSibling(clean);
            if (Files.exists(target)) {
                System.err.println("SKIP (target already exists): " + target);
                skipped++;
                continue;
            }

            // Drop redundant slug: line, and preserve the old URL as an alias.
            String text = Files.readString(index);
            text = text.replaceAll("(?m)^slug:.*\\R", "");
            text = text.replaceFirst("(?s)^---\\R",
                    Matcher.quoteReplacement("---\naliases:\n  - \"/today/" + slug + "/\"\n"));
            Files.writeString(index, text);
            Files.move(dir, target);
            System.out.println(slug + "  ->  " + clean);
            renamed++;
        }
        System.out.printf("Done. renamed=%d skipped=%d%n", renamed, skipped);
    }

    static String sanitize(String s) {
        return s.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9_-]+", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("^[-_]+|[-_]+$", "");
    }
}
