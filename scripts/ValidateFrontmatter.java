///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.yaml:snakeyaml:2.2
//JAVA 17+

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;
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

    /** A clean URL slug: lowercase letters/digits, separated by single dashes or
     *  underscores. Flags emoji, spaces, uppercase, etc. */
    static final Pattern SLUG_FMT = Pattern.compile("[a-z0-9]+(?:[-_][a-z0-9]+)*");

    public static void main(String[] args) throws IOException {
        List<String> problems = new ArrayList<>();

        // Posts are leaf bundles: content/posts/<y>/<m>/<d>/<slug>/index.md. The URL
        // slug is the bundle FOLDER name (permalink :slugorcontentbasename), so
        // that's what we key on -- for duplicate detection (two posts with the same
        // slug collide on /today/<slug>/), for a clean-slug check, and to verify any
        // `slug:` frontmatter matches the folder.
        Path postsDir = Path.of("content/posts");
        Map<String, List<Path>> slugDirs = new TreeMap<>();
        if (Files.isDirectory(postsDir)) {
            List<Path> indexes;
            try (Stream<Path> files = Files.walk(postsDir)) {
                indexes = files.filter(p -> p.getFileName().toString().equals("index.md")).toList();
            }
            for (Path idx : indexes) {
                String slug = idx.getParent().getFileName().toString();
                slugDirs.computeIfAbsent(slug, k -> new ArrayList<>()).add(idx);
                if (!SLUG_FMT.matcher(slug).matches()) {
                    problems.add(idx + ": folder name '" + slug + "' is not a clean URL slug"
                            + " (lowercase letters, digits, dashes and underscores only)");
                }
                Map<String, Object> fm = readFrontmatter(idx);
                Object s = fm == null ? null : fm.get("slug");
                if (s != null && !s.toString().isBlank() && !s.toString().equals(slug)) {
                    problems.add(idx + ": frontmatter slug '" + s + "' does not match folder name '" + slug + "'");
                }
            }
        }
        slugDirs.forEach((slug, paths) -> {
            if (paths.size() > 1) problems.add("duplicate post slug '" + slug + "' in: " + paths);
        });
        Set<String> postSlugs = slugDirs.keySet();

        problems.addAll(checkDir(Path.of("content/posts"), List.of("title", "description", "authors")));
        problems.addAll(checkDir(Path.of("content/authors"), List.of("title")));
        problems.addAll(checkDir(Path.of("content/pages"), List.of("title", "url")));
        problems.addAll(checkDir(Path.of("content/sponsors"), List.of("title", "tier")));
        problems.addAll(checkRelatedPosts(postsDir, postSlugs));
        problems.addAll(checkSponsorAuthors(Path.of("content/sponsors"), authorSlugs()));
        problems.addAll(checkBoardMembers(Path.of("content/pages/board")));
        problems.addAll(checkFeaturedAuthors(Path.of("hugo.toml"), authorSlugs()));

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

    /**
     * Every author slug that exists, i.e. every content/authors/**&#47;<slug>/index.md
     * bundle folder name. Author bundles are bucketed by first letter, so the
     * folder name -- not the path -- is the slug, exactly as posts and the
     * templates treat it.
     */
    static Set<String> authorSlugs() throws IOException {
        Path dir = Path.of("content/authors");
        if (!Files.isDirectory(dir)) return Set.of();
        try (Stream<Path> files = Files.walk(dir)) {
            return files.filter(p -> p.getFileName().toString().equals("index.md"))
                    .map(p -> p.getParent().getFileName().toString())
                    .collect(java.util.stream.Collectors.toSet());
        }
    }

    /**
     * A sponsor's `authors:` list IS its article list: the profile template
     * (themes/foojay/layouts/sponsors/single.html) shows every post written by
     * those slugs. A typo therefore doesn't error, it just silently drops
     * articles off the sponsor's page -- which is exactly the class of mistake
     * a PR check should catch, same reasoning as the related_posts check above.
     *
     * An EMPTY list is fine and not reported: a newly added sponsor legitimately
     * has no authors on foojay.io yet, and the page renders an explanatory
     * empty state for that case.
     */
    static List<String> checkSponsorAuthors(Path sponsorsDir, Set<String> authorSlugs) throws IOException {
        List<String> problems = new ArrayList<>();
        if (!Files.isDirectory(sponsorsDir)) return problems;

        try (Stream<Path> files = Files.walk(sponsorsDir)) {
            for (Path file : files.filter(p -> p.getFileName().toString().equals("index.md")).toList()) {
                Map<String, Object> fm = readFrontmatter(file);
                if (fm == null) continue;
                if (fm.get("authors") instanceof List<?> list) {
                    for (Object slug : list) {
                        if (slug != null && !authorSlugs.contains(slug.toString())) {
                            problems.add(file + ": authors references unknown author slug '" + slug
                                    + "' (expected a folder name under content/authors/)");
                        }
                    }
                }
            }
        }
        return problems;
    }

    /**
     * An advisory board member is a page under content/pages/board/ carrying
     * `type: "board"` -- that type is how partials/board-members.html finds the
     * members, and how layouts/board/single.html gets used instead of the plain
     * page template. Miss it and the member simply doesn't appear on /board/,
     * the same silent-drop failure the two checks above exist for.
     *
     * `logo` is required for the same reason it is on a sponsor: the tile falls
     * back to an initial, which looks like a design choice rather than a
     * mistake, so nothing about the page says the logo was forgotten.
     */
    static List<String> checkBoardMembers(Path boardDir) throws IOException {
        List<String> problems = new ArrayList<>();
        if (!Files.isDirectory(boardDir)) return problems;

        try (Stream<Path> files = Files.walk(boardDir)) {
            for (Path file : files.filter(p -> p.toString().endsWith(".md")).toList()) {
                Map<String, Object> fm = readFrontmatter(file);
                if (fm == null) continue;
                if (!"board".equals(String.valueOf(fm.get("type")))) {
                    problems.add(file + ": board members need `type: \"board\"` to be listed on /board/");
                }
                Object logo = fm.get("logo");
                if (logo == null || logo.toString().isBlank()) {
                    problems.add(file + ": missing/empty required field 'logo'");
                }
            }
        }
        return problems;
    }

    /**
     * hugo.toml's `featuredAuthors` is the monthly Featured Authors pick: a
     * list of author slugs the /today/author/ spotlight band and the home page
     * sidebar widget resolve through the author index. An unknown slug is
     * skipped by the template rather than rendered as a dead entry, so the only
     * symptom of a typo is a featured author quietly not appearing -- the same
     * silent-drop failure the sponsor `authors:` check above exists for.
     *
     * Parsed with a regex rather than a TOML library: it's one flat array of
     * strings in a file no other check reads, and adding a dependency to the
     * PR check for it isn't worth it. An absent or empty list is fine (no
     * spotlight is rendered between rotations).
     */
    static List<String> checkFeaturedAuthors(Path configFile, Set<String> authorSlugs) throws IOException {
        List<String> problems = new ArrayList<>();
        if (!Files.isRegularFile(configFile)) return problems;

        var matcher = Pattern.compile("(?m)^\\s*featuredAuthors\\s*=\\s*\\[([^\\]]*)]")
                .matcher(Files.readString(configFile));
        if (!matcher.find()) return problems;

        var slugMatcher = Pattern.compile("[\"']([^\"']+)[\"']").matcher(matcher.group(1));
        while (slugMatcher.find()) {
            String slug = slugMatcher.group(1);
            if (!authorSlugs.contains(slug)) {
                problems.add(configFile + ": featuredAuthors references unknown author slug '" + slug
                        + "' (expected a folder name under content/authors/)");
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
