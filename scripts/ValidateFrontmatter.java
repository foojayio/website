///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.yaml:snakeyaml:2.2
//JAVA 17+

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
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

        Set<String> authorSlugs = authorSlugs();

        problems.addAll(checkDir(Path.of("content/posts"), List.of("title", "description", "authors")));
        problems.addAll(checkDir(Path.of("content/authors"), List.of("title")));
        problems.addAll(checkDir(Path.of("content/pages"), List.of("title", "url")));
        problems.addAll(checkDir(Path.of("content/sponsors"), List.of("title", "tier")));
        problems.addAll(checkTitleEmoji(postsDir));
        problems.addAll(checkRelatedPosts(postsDir, postSlugs));
        problems.addAll(checkDrafts(Path.of("draft"), postSlugs, authorSlugs));
        problems.addAll(checkSponsorAuthors(Path.of("content/sponsors"), authorSlugs));
        problems.addAll(checkBoardMembers(Path.of("content/pages/board")));
        problems.addAll(checkSeriesWeights(Path.of("content/pages")));
        problems.addAll(checkFeaturedAuthors(Path.of("hugo.toml"), authorSlugs));

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
                problems.addAll(checkRequired(file, fm, requiredFields));
            }
        }
        return problems;
    }

    /**
     * The required-field rule itself, split out so the draft check below applies
     * exactly the same one -- a draft becomes a post by being moved, so anything
     * the two disagree about is a rule that only bites AFTER the contributor has
     * gone.
     *
     * An empty list counts as missing: `authors: []` and `categories: []` are
     * what a half-filled template looks like, and both render as silently
     * missing rather than as an error.
     */
    static List<String> checkRequired(Path file, Map<String, Object> fm, List<String> requiredFields) {
        List<String> problems = new ArrayList<>();
        for (String field : requiredFields) {
            Object v = fm.get(field);
            boolean empty = v == null
                    || (v instanceof String s && s.isBlank())
                    || (v instanceof List<?> l && l.isEmpty());
            if (empty) problems.add(file + ": missing/empty required field '" + field + "'");
        }
        return problems;
    }

    /**
     * Leftover template text. `template/post.md` ships these exact strings, so
     * a copy that still carries one is a template the author started from and
     * did not finish -- worth naming as such rather than reporting the field as
     * present, which it technically is.
     */
    static final Map<String, String> PLACEHOLDERS = Map.of(
            "title", "Your Article Title Here",
            "description", "A short, one- or two-sentence summary of the article.",
            "authors", "your-author-slug");

    /**
     * A contributor's submission, which lands in draft/<slug>/index.md.
     *
     * This is the one place the PR check was blind, and the worst place to be
     * blind: `draft/` sits OUTSIDE `content/` on purpose (drafts must not
     * publish themselves), which means the `hugo --gc --minify` step in
     * pr-check.yml never reads a draft at all -- so before this, a submission
     * could be missing every required field, name an author who doesn't exist
     * and collide with a published URL, and the PR would go green. The one PR
     * most likely to be wrong is a first-time contributor's, and it was the
     * only one nothing checked.
     *
     * The rules are deliberately the SAME ones a published post is held to
     * (checkRequired, SLUG_FMT, the `slug:`-matches-folder rule, TITLE_EMOJI,
     * related_posts), because publishing is nothing more than a maintainer
     * moving the folder into content/posts/<y>/<m>/<d>/ -- a rule that applies
     * only after the move is one that fails when the author is no longer around
     * to fix it. Required fields are the four `template/post.md` marks
     * "Required", all of which hold across every one of the published posts.
     *
     * Two checks go beyond what content/posts gets, because both are silent
     * failures a contributor cannot see and a maintainer would have to know to
     * look for: an `authors:` slug with no author bundle (the post renders, but
     * it never appears on the author's profile and the byline links nowhere),
     * and a hero `image:` naming a file that isn't in the folder (a remote URL
     * is left alone -- 76 published posts legitimately use one).
     */
    static List<String> checkDrafts(Path draftDir, Set<String> postSlugs, Set<String> authorSlugs)
            throws IOException {
        List<String> problems = new ArrayList<>();
        if (!Files.isDirectory(draftDir)) return problems;

        // A submission is a FOLDER, because the folder name is the URL slug and
        // the images sit next to the index.md. Copying template/post.md straight
        // to draft/my-article.md is the obvious near-miss, and it would other-
        // wise be skipped by every check below rather than reported.
        try (Stream<Path> loose = Files.list(draftDir)) {
            for (Path file : loose.filter(Files::isRegularFile).sorted().toList()) {
                String name = file.getFileName().toString();
                if (!name.endsWith(".md") || name.equals("README.md")) continue;
                problems.add(file + ": a submission is a folder, not a loose file"
                        + " -- move it to draft/" + stripExt(name) + "/index.md"
                        + " (the folder name becomes the URL slug)");
            }
        }

        List<Path> bundles;
        try (Stream<Path> dirs = Files.list(draftDir)) {
            bundles = dirs.filter(Files::isDirectory).sorted().toList();
        }

        Set<String> draftSlugs = new TreeSet<>();
        for (Path dir : bundles) draftSlugs.add(dir.getFileName().toString());
        Set<String> knownSlugs = new TreeSet<>(postSlugs);
        knownSlugs.addAll(draftSlugs);

        for (Path dir : bundles) {
            String slug = dir.getFileName().toString();
            Path index = dir.resolve("index.md");
            if (!Files.isRegularFile(index)) {
                problems.add(dir + ": no index.md (the article itself goes in"
                        + " draft/" + slug + "/index.md, images alongside it)");
                continue;
            }
            if (!SLUG_FMT.matcher(slug).matches()) {
                problems.add(index + ": folder name '" + slug + "' is not a clean URL slug"
                        + " (lowercase letters, digits, dashes and underscores only)");
            }
            // The folder name is the URL, so a draft sharing a slug with a
            // published post collides on /today/<slug>/ the moment it is moved.
            if (postSlugs.contains(slug)) {
                problems.add(index + ": slug '" + slug + "' is already used by a published post"
                        + " -- both would claim /today/" + slug + "/");
            }

            Map<String, Object> fm = readFrontmatter(index);
            if (fm == null) {
                problems.add(index + ": no frontmatter block found"
                        + " (start from template/post.md)");
                continue;
            }

            problems.addAll(checkRequired(index, fm,
                    List.of("title", "description", "authors", "image", "categories")));

            Object declared = fm.get("slug");
            if (declared != null && !declared.toString().isBlank()
                    && !declared.toString().equals(slug)) {
                problems.add(index + ": frontmatter slug '" + declared
                        + "' does not match folder name '" + slug + "'");
            }

            if (fm.get("title") instanceof String title) {
                Matcher m = TITLE_EMOJI.matcher(title);
                if (m.find()) {
                    problems.add(index + ": title contains emoji (" + m.group()
                            + ") -- titles are used as cards, RSS items and link previews;"
                            + " put it in the body instead: \"" + title + "\"");
                }
            }

            PLACEHOLDERS.forEach((field, placeholder) -> {
                Object v = fm.get(field);
                boolean unchanged = v instanceof List<?> list
                        ? list.stream().anyMatch(e -> placeholder.equals(String.valueOf(e)))
                        : placeholder.equals(String.valueOf(v));
                if (unchanged) {
                    problems.add(index + ": '" + field + "' still holds the template placeholder"
                            + " (\"" + placeholder + "\")");
                }
            });

            if (fm.get("authors") instanceof List<?> list) {
                for (Object author : list) {
                    if (author == null) continue;
                    String a = author.toString();
                    if (a.equals(PLACEHOLDERS.get("authors")) || authorSlugs.contains(a)) continue;
                    problems.add(index + ": authors references unknown author slug '" + a
                            + "' -- add content/authors/" + a.charAt(0) + "/" + a + "/index.md"
                            + " in this same PR (start from template/author.md)");
                }
            }

            if (fm.get("related_posts") instanceof List<?> list) {
                for (Object related : list) {
                    if (related != null && !knownSlugs.contains(related.toString())) {
                        problems.add(index + ": related_posts references unknown slug '" + related + "'");
                    }
                }
            }

            // A bare filename means the file next to index.md; a URL is somebody
            // else's server and not ours to verify.
            if (fm.get("image") instanceof String image && !image.isBlank()
                    && !image.startsWith("http") && !image.startsWith("/")
                    && !Files.isRegularFile(dir.resolve(image))) {
                problems.add(index + ": image '" + image + "' is not in the folder"
                        + " (put it next to index.md and reference it by filename)");
            }
        }
        return problems;
    }

    /**
     * A post title carrying an emoji.
     *
     * `ConvertPosts.stripEmoji` takes these off everything it scrapes, but a
     * contributor writes their own frontmatter by hand and the scraper never
     * sees that title -- so without this a decorated headline sails straight
     * into the repo. It fails at PR time rather than silently, because a title
     * is the card in every grid, the RSS item, the browser tab and the
     * `og:title` a link preview renders, and it is exactly the kind of thing
     * nobody notices is wrong until it is on the home page.
     *
     * Same rule as the converter, and deliberately so -- Extended_Pictographic
     * plus the modifiers, NOT \p{IsEmoji}, which is true for ASCII digits, `#`
     * and `*` and would reject "The 5 Knights" and every `#release` hashtag.
     * Only TITLES: an emoji in the body is the author's writing, and in a table
     * or a legend it carries meaning.
     */
    static final Pattern TITLE_EMOJI = Pattern.compile(
            "[\\p{IsExtended_Pictographic}\\x{1F3FB}-\\x{1F3FF}\\x{FE0F}\\x{20E3}\\x{200D}]");

    static List<String> checkTitleEmoji(Path postsDir) throws IOException {
        List<String> problems = new ArrayList<>();
        if (!Files.isDirectory(postsDir)) return problems;

        try (Stream<Path> files = Files.walk(postsDir)) {
            for (Path file : files.filter(p -> p.toString().endsWith(".md")).toList()) {
                if (file.getFileName().toString().equals("_index.md")) continue;
                Map<String, Object> fm = readFrontmatter(file);
                if (fm == null) continue;
                if (!(fm.get("title") instanceof String title)) continue;
                Matcher m = TITLE_EMOJI.matcher(title);
                if (m.find()) {
                    problems.add(file + ": title contains emoji (" + m.group()
                            + ") -- titles are used as cards, RSS items and link previews;"
                            + " put it in the body instead: \"" + title + "\"");
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
    /**
     * Two steps of one series claiming the same position.
     *
     * A folder of pages that carry a `weight` IS a series
     * (partials/series-steps.html): the progress bar, the step count and the
     * prev/next cards all come from sorting on it. A duplicate doesn't fail the
     * build -- Hugo's sort is stable, so it silently picks one -- it just makes
     * "Step 7 of 11" point at the wrong page and lets prev/next skip a step.
     * That is precisely the kind of wrong-but-quiet the PR check exists for.
     *
     * Weights are only compared WITHIN a folder, because that is the unit a
     * series is scoped to: the Java Quick Start steps are a sequence, while the
     * install-java pages next door are alternatives and carry no weight at all.
     */
    static List<String> checkSeriesWeights(Path pagesDir) throws IOException {
        List<String> problems = new ArrayList<>();
        if (!Files.isDirectory(pagesDir)) return problems;

        Map<Path, Map<Integer, List<Path>>> byDir = new LinkedHashMap<>();
        try (Stream<Path> files = Files.walk(pagesDir)) {
            for (Path file : files.filter(p -> p.toString().endsWith(".md")).sorted().toList()) {
                if (file.getFileName().toString().equals("_index.md")) continue;
                Map<String, Object> fm = readFrontmatter(file);
                if (fm == null || !(fm.get("weight") instanceof Integer weight)) continue;
                byDir.computeIfAbsent(file.getParent(), d -> new LinkedHashMap<>())
                        .computeIfAbsent(weight, w -> new ArrayList<>())
                        .add(file);
            }
        }
        for (Map.Entry<Path, Map<Integer, List<Path>>> dir : byDir.entrySet()) {
            for (Map.Entry<Integer, List<Path>> e : dir.getValue().entrySet()) {
                if (e.getValue().size() > 1) {
                    problems.add(dir.getKey() + ": weight " + e.getKey()
                            + " is claimed by more than one page, so the series order is ambiguous: "
                            + e.getValue());
                }
            }
        }
        return problems;
    }

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
