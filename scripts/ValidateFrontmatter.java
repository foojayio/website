///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.yaml:snakeyaml:2.2
//JAVA 17+

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

/**
 * Lightweight PR-time content check, run by .github/workflows/pr-check.yml.
 * GitHub Pages has no built-in preview deploys, so this is the fast-feedback
 * substitute: catches missing required frontmatter fields and dangling
 * related_posts references before merge, without needing a rendered preview.
 *
 * Usage: jbang scripts/ValidateFrontmatter.java
 * Exits non-zero (failing the PR check) if any problems are found.
 */
public class ValidateFrontmatter {

    public static void main(String[] args) throws IOException {
        List<String> problems = new ArrayList<>();

        // Posts live under content/posts/<year>/<month>/<slug>.md, so this
        // needs to walk recursively rather than list the top-level dir.
        // Track every file per slug so duplicates can be reported: two posts with
        // the same slug resolve to the same /today/<slug>/ URL, and Hugo would
        // silently drop one (and one writePost would overwrite the other).
        Path postsDir = Path.of("content/posts");
        Map<String, List<Path>> slugFiles = new TreeMap<>();
        if (Files.isDirectory(postsDir)) {
            try (Stream<Path> files = Files.walk(postsDir)) {
                files.filter(p -> p.toString().endsWith(".md") && !p.getFileName().toString().equals("_index.md"))
                     .forEach(p -> slugFiles.computeIfAbsent(stripExt(p.getFileName().toString()),
                             k -> new ArrayList<>()).add(p));
            }
        }
        slugFiles.forEach((slug, paths) -> {
            if (paths.size() > 1) problems.add("duplicate post slug '" + slug + "' in: " + paths);
        });
        Set<String> postSlugs = slugFiles.keySet();

        problems.addAll(checkDir(Path.of("content/posts"), List.of("title", "description", "authors")));
        problems.addAll(checkDir(Path.of("content/authors"), List.of("title")));
        problems.addAll(checkDir(Path.of("content/pages"), List.of("title", "url")));
        problems.addAll(checkRelatedPosts(postsDir, postSlugs));

        if (problems.isEmpty()) {
            System.out.println("Frontmatter check passed.");
            return;
        }

        System.err.println(problems.size() + " problem(s) found:");
        for (String p : problems) System.err.println(" - " + p);
        System.exit(1);
    }

    static List<String> checkDir(Path dir, List<String> requiredFields) throws IOException {
        List<String> problems = new ArrayList<>();
        if (!Files.isDirectory(dir)) return problems;

        try (Stream<Path> files = Files.walk(dir)) {
            for (Path file : files.filter(p -> p.toString().endsWith(".md")).toList()) {
                if (file.getFileName().toString().equals("_index.md")) continue;
                Map<String, Object> fm = readFrontmatter(file);
                if (fm == null) {
                    problems.add(file + ": no frontmatter block found");
                    continue;
                }
                for (String field : requiredFields) {
                    Object v = fm.get(field);
                    if (v == null || (v instanceof String s && s.isBlank())) {
                        problems.add(file + ": missing/empty required field '" + field + "'");
                    }
                }
            }
        }
        return problems;
    }

    @SuppressWarnings("unchecked")
    static List<String> checkRelatedPosts(Path postsDir, Set<String> postSlugs) throws IOException {
        List<String> problems = new ArrayList<>();
        if (!Files.isDirectory(postsDir)) return problems;

        try (Stream<Path> files = Files.walk(postsDir)) {
            for (Path file : files.filter(p -> p.toString().endsWith(".md")).toList()) {
                if (file.getFileName().toString().equals("_index.md")) continue;
                Map<String, Object> fm = readFrontmatter(file);
                if (fm == null) continue;
                Object related = fm.get("related_posts");
                if (related instanceof List<?> list) {
                    for (Object slug : list) {
                        if (slug != null && !postSlugs.contains(slug.toString())) {
                            problems.add(file + ": related_posts references unknown slug '" + slug + "'");
                        }
                    }
                }
            }
        }
        return problems;
    }

    static Map<String, Object> readFrontmatter(Path file) throws IOException {
        String content = Files.readString(file);
        if (!content.startsWith("---")) return null;
        int end = content.indexOf("\n---", 3);
        if (end < 0) return null;
        String yamlBlock = content.substring(3, end).trim();
        Yaml yaml = new Yaml();
        Object loaded = yaml.load(yamlBlock);
        return loaded instanceof Map ? (Map<String, Object>) loaded : null;
    }

    static String stripExt(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot > 0 ? filename.substring(0, dot) : filename;
    }
}
