///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.yaml:snakeyaml:2.2
//JAVA 17+

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
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
 * Usage: jbang scripts/validate/Frontmatter.java
 * Exits non-zero (failing the PR check) if any problems are found.
 */
public class Frontmatter {

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
        problems.addAll(checkEvents(Path.of("data/events")));
        problems.addAll(checkAds(Path.of("content/ads")));
        problems.addAll(checkImageWeight(Path.of("content")));
        problems.addAll(checkHeroImageStill(Path.of("content")));

        // WARNINGS, which never fail the check -- see reportWarnings below for
        // why image descriptions are on this side of the line and everything
        // above it is not.
        List<String> warnings = new ArrayList<>();
        Set<Path> touched = changedFiles(args);
        warnings.addAll(checkImageAltText(Path.of("content"), touched));
        warnings.addAll(checkImageAltText(Path.of("draft"), touched));

        reportWarnings(warnings, touched);

        if (problems.isEmpty()) {
            System.out.println("Frontmatter check passed.");
            return;
        }

        System.err.println(problems.size() + " problem(s) found:");
        for (String p : problems) System.err.println(" - " + p);
        System.exit(1);
    }

    /* ---------------------------------------------------------------------
       Warnings
       ---------------------------------------------------------------------

       A WARNING IS A DIFFERENT CLAIM FROM A PROBLEM, and the difference is
       whether a machine can be sure. Everything in `problems` above is
       decidable: a required field is present or it is not, a related_posts slug
       resolves or it does not, two pages claim the same weight or they do not.
       A missing image description is not: `![](chart.png)` is WRONG for a chart
       and RIGHT for a divider, and nothing here can tell those apart by looking
       at the file.

       Failing on it would therefore do the one thing this repo's contribution
       goal cannot afford -- block a first-time author's pull request over a
       judgement call the check cannot make -- and the predictable response is a
       description typed to get the build green ("image", "screenshot"), which is
       worse for a screen-reader user than nothing at all, because a bad alt
       cannot be skipped the way an empty one can.

       So it reports, and a human decides. */
    static void reportWarnings(List<String> warnings, Set<Path> touched) {
        if (warnings.isEmpty()) return;

        // Unscoped runs see the whole archive (~3000 images imported from
        // WordPress with no description), which is a backlog and not this PR's
        // doing -- so it is summarised rather than printed. A scoped run prints
        // everything it found, because all of it is the author's own work.
        int shown = touched != null ? warnings.size() : Math.min(warnings.size(), 25);

        System.out.println();
        System.out.println(warnings.size() + " accessibility warning(s) -- these do NOT fail the check:");
        for (int i = 0; i < shown; i++) System.out.println(" ~ " + warnings.get(i));
        if (shown < warnings.size()) {
            System.out.println(" ~ ... and " + (warnings.size() - shown) + " more across the archive."
                    + " Run with --changed-since <ref> to see only what this branch touched.");
        }
        System.out.println("   An image with no description is skipped by a screen reader, so a reader"
                + " who cannot see it is told nothing at all. Add one where the image carries meaning;"
                + " leave it empty where the image is decoration and the text beside it already says"
                + " everything. See /accessibility/ for the standard the site aims at.");
        System.out.println();
    }

    /**
     * The markdown files this branch actually touches, or null when the run is
     * not scoped -- in which case every check below looks at everything.
     *
     * `--changed-since <ref>` is what .github/workflows/pr-check.yml passes, so
     * a contributor's warnings are about their own post and not about 2000
     * imported ones. Deliberately soft: a repo with no git, a ref that does not
     * exist on a shallow clone, or git missing entirely all fall back to the
     * unscoped run rather than failing -- a warning system that can break the
     * check it is attached to has the priority backwards.
     */
    static Set<Path> changedFiles(String[] args) {
        String ref = null;
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals("--changed-since")) ref = args[i + 1];
        }
        if (ref == null) return null;
        try {
            Process git = new ProcessBuilder("git", "diff", "--name-only", ref + "...HEAD")
                    .redirectErrorStream(false).start();
            Set<Path> out = new HashSet<>();
            try (Scanner sc = new Scanner(git.getInputStream())) {
                while (sc.hasNextLine()) {
                    String line = sc.nextLine().trim();
                    if (!line.isEmpty()) out.add(Path.of(line));
                }
            }
            if (!git.waitFor(30, java.util.concurrent.TimeUnit.SECONDS) || git.exitValue() != 0) {
                System.out.println("(could not diff against '" + ref + "'; checking the whole tree)");
                return null;
            }
            return out;
        } catch (Exception e) {
            System.out.println("(git unavailable; checking the whole tree)");
            return null;
        }
    }

    static List<String> checkDir(Path dir, List<String> requiredFields) throws IOException {
        List<String> problems = new ArrayList<>();
        if (!Files.isDirectory(dir)) return problems;

        try (Stream<Path> files = Files.walk(dir)) {
            for (Path file : files.filter(p -> p.toString().endsWith(".md")).toList()) {
                // Skip only the SECTION's own _index.md, not every _index.md in the
                // tree. Author and sponsor profiles are branch bundles now
                // (content/authors/<slug>/_index.md -- .Paginate refuses a page kind),
                // so a blanket skip on the filename silently stopped checking all 344
                // author profiles and all 7 sponsors for their required fields.
                if (file.equals(dir.resolve("_index.md"))) continue;
                if (isPageResource(file)) continue;
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
     * A .md file that is a page RESOURCE rather than a page: it sits in a leaf
     * bundle next to that bundle's index.md. Hugo hands those to the layout
     * through .Resources and never publishes a URL for them, so they carry no
     * frontmatter and none of the rules below apply.
     *
     * Written as "is there an index.md beside it" rather than as a list of
     * filenames, so the next one costs nothing. Today it is the podcast
     * transcripts (scripts/fetch/PodcastTranscripts.java, 99 of them), which
     * without this were 99 failures reading "no frontmatter block found" --
     * a check failing on a file that is not the kind of thing it checks.
     */
    static boolean isPageResource(Path file) {
        String name = file.getFileName().toString();
        if (name.equals("index.md") || name.equals("_index.md")) return false;
        Path parent = file.getParent();
        return parent != null && Files.exists(parent.resolve("index.md"));
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
                            + "' -- add content/authors/" + a + "/_index.md"
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
     * `Posts.stripEmoji` takes these off everything it scrapes, but a
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
                // Posts are LEAF bundles (index.md), so any _index.md under
                // content/posts/ is a section index and never an article.
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
                // Posts are LEAF bundles (index.md), so any _index.md under
                // content/posts/ is a section index and never an article.
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
     * Every author slug that exists, i.e. every content/authors/<slug>/_index.md
     * bundle folder name -- which is what posts reference in `authors:` and what
     * :slugorcontentbasename resolves to.
     *
     * `_index.md`, not `index.md`: an author profile is a BRANCH bundle so that
     * .Paginate will accept it (see content/authors/_index.md). The section's own
     * _index.md is excluded, or "authors" itself would count as an author slug.
     */
    static Set<String> authorSlugs() throws IOException {
        Path dir = Path.of("content/authors");
        if (!Files.isDirectory(dir)) return Set.of();
        try (Stream<Path> files = Files.walk(dir)) {
            return files.filter(p -> p.getFileName().toString().equals("_index.md"))
                    .filter(p -> !p.equals(dir.resolve("_index.md")))
                    .map(p -> p.getParent().getFileName().toString())
                    .collect(java.util.stream.Collectors.toSet());
        }
    }

    /**
     * A sponsor's `authors:` list IS its article list: the profile template
     * (themes/foojay/layouts/sponsor/section.html) shows every post written by
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
            // Sponsor bundles are branch bundles (_index.md), same reason as authors.
            for (Path file : files.filter(p -> p.getFileName().toString().equals("_index.md"))
                    .filter(p -> !p.equals(sponsorsDir.resolve("_index.md"))).toList()) {
                Map<String, Object> fm = readFrontmatter(file);
                if (fm == null) continue;
                if (fm.get("authors") instanceof List<?> list) {
                    for (Object entry : list) {
                        if (entry == null) continue;
                        problems.addAll(checkSponsorAuthorEntry(file, entry, authorSlugs));
                    }
                }
            }
        }
        return problems;
    }

    /** Keys allowed on a dated `authors:` entry. Closed on purpose -- see below. */
    static final Set<String> SPONSOR_AUTHOR_KEYS = Set.of("slug", "from", "till");
    static final Pattern ISO_DAY = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

    /**
     * One entry of a sponsor's `authors:` list, which is either a bare slug or a
     * map carrying an attribution window:
     *
     *   authors:
     *     - "tim-kelly"
     *     - slug: "pratik-patel"
     *       till: "2026-04-01"
     *
     * The window exists because people change employer (see
     * partials/sponsor-authors.html): `[from, till)` is half-open, so `till` is
     * the day they left and their later articles stop being the sponsor's.
     *
     * Every rule here is a SILENT failure otherwise, which is the bar for adding
     * a check at all:
     *
     *   - a misspelled key (`until:` for `till:`, `since:` for `from:`) is simply
     *     ignored by the template, so the departed author's new posts keep
     *     appearing and nothing says why. Hence a CLOSED key set, the same
     *     reasoning as checkEvents.
     *   - a map with no `slug` matches no author at all, silently dropping every
     *     article that person wrote.
     *   - a date Hugo's time.Format can't read halts the build with a template
     *     error rather than a content error, which points at the wrong file.
     *   - from >= till is an empty window: the author is listed on the page and
     *     contributes nothing, which reads as a bug in the site.
     */
    static List<String> checkSponsorAuthorEntry(Path file, Object entry, Set<String> authorSlugs) {
        List<String> problems = new ArrayList<>();

        if (!(entry instanceof Map<?, ?> map)) {
            String slug = entry.toString();
            if (!authorSlugs.contains(slug)) {
                problems.add(file + ": authors references unknown author slug '" + slug
                        + "' (expected a folder name under content/authors/)");
            }
            return problems;
        }

        for (Object key : map.keySet()) {
            if (!SPONSOR_AUTHOR_KEYS.contains(String.valueOf(key))) {
                problems.add(file + ": authors entry has unknown key '" + key
                        + "' -- allowed: " + new java.util.TreeSet<>(SPONSOR_AUTHOR_KEYS));
            }
        }

        Object slugValue = map.get("slug");
        if (slugValue == null || slugValue.toString().isBlank()) {
            problems.add(file + ": authors entry " + map + " has no 'slug'"
                    + " (a dated entry is `- slug: \"name\"` plus from:/till:)");
            return problems;
        }
        String slug = slugValue.toString();
        if (!authorSlugs.contains(slug)) {
            problems.add(file + ": authors references unknown author slug '" + slug
                    + "' (expected a folder name under content/authors/)");
        }

        String from = sponsorAuthorDay(file, map.get("from"), "from", slug, problems);
        String till = sponsorAuthorDay(file, map.get("till"), "till", slug, problems);
        if (from != null && till != null && from.compareTo(till) >= 0) {
            problems.add(file + ": authors entry '" + slug + "' has from: " + from
                    + " on or after till: " + till + " -- an empty window, so the author"
                    + " would be listed with none of their articles"
                    + " (the range is half-open: till is the first day NOT attributed)");
        }
        return problems;
    }

    /**
     * A `from:`/`till:` value as an ISO yyyy-MM-dd string, or null when absent or
     * unusable. YAML turns an unquoted 2026-04-01 into a Date and a quoted one
     * into a String, and both are accepted -- the templates normalise via
     * time.Format either way.
     */
    static String sponsorAuthorDay(Path file, Object value, String key, String slug, List<String> problems) {
        if (value == null) return null;
        if (value instanceof java.util.Date d) {
            return new java.text.SimpleDateFormat("yyyy-MM-dd").format(d);
        }
        String text = value.toString().trim();
        if (ISO_DAY.matcher(text).matches()) return text;
        problems.add(file + ": authors entry '" + slug + "' has " + key + ": '" + text
                + "' -- expected a yyyy-MM-dd date");
        return null;
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
    /**
     * A hero `image:` must be a STILL image.
     *
     * Not a format rule -- 41 posts use .webp, 5 .avif and one .svg as their hero
     * and all are fine. The constraint is animation, because of where a hero
     * actually goes: the card in every grid, `og:image` in the link preview, and
     * the `image` in the BlogPosting JSON-LD. None of those animate. A social
     * preview shows frame one, a grid of animating cards is unreadable, and the
     * file is downloaded in full for a thumbnail -- three 52 MB animated GIFs were
     * heroes, which is where the 1 GB artifact problem came from.
     *
     * The animation is not lost: it belongs in the BODY, where it plays. 17 of the
     * 20 posts that tripped this already referenced the same file in their body.
     *
     * Detection is per container, and deliberately FAILS OPEN -- a format we can't
     * inspect is passed rather than guessed at:
     *   GIF   more than one image in the stream (ImageIO; the JDK reads GIF).
     *   WebP  an ANIM chunk in the RIFF container. Java has no WebP reader at all,
     *         so this is read from the bytes.
     *   AVIF  an `avis` brand in ftyp, i.e. an image sequence.
     */
    static List<String> checkHeroImageStill(Path contentDir) throws IOException {
        List<String> problems = new ArrayList<>();
        if (!Files.isDirectory(contentDir)) return problems;

        try (Stream<Path> files = Files.walk(contentDir)) {
            for (Path md : files.filter(p -> {
                String n = p.getFileName().toString();
                return n.equals("index.md") || n.equals("_index.md");
            }).toList()) {
                Map<String, Object> fm = readFrontmatter(md);
                if (fm == null) continue;
                if (!(fm.get("image") instanceof String hero) || hero.isBlank()) continue;
                if (hero.contains("://") || hero.startsWith("/")) continue; // remote or site-absolute
                Path img = md.getParent().resolve(hero);
                if (!Files.isRegularFile(img)) continue; // checkDrafts/authors cover missing files
                String why = animationKind(img);
                if (why == null) continue;
                problems.add(md + ": hero image '" + hero + "' is " + why
                        + " -- a hero is used as the card thumbnail, og:image and JSON-LD image,"
                        + " none of which animate. Point `image:` at a still frame"
                        + " (cleanup/images.py writes one as <name>-poster.png) and keep the"
                        + " animation in the body.");
            }
        }
        return problems;
    }

    /** A description of how this file is animated, or null when it is a still. */
    static String animationKind(Path file) {
        String name = file.getFileName().toString().toLowerCase();
        try {
            if (name.endsWith(".gif")) {
                try (javax.imageio.stream.ImageInputStream in =
                             javax.imageio.ImageIO.createImageInputStream(file.toFile())) {
                    var readers = javax.imageio.ImageIO.getImageReaders(in);
                    if (!readers.hasNext()) return null;
                    var reader = readers.next();
                    try {
                        reader.setInput(in);
                        int n = reader.getNumImages(true);
                        return n > 1 ? "an animated GIF (" + n + " frames)" : null;
                    } finally {
                        reader.dispose();
                    }
                }
            }
            if (name.endsWith(".webp") || name.endsWith(".avif")) {
                byte[] head = new byte[4096];
                int read;
                try (var in = Files.newInputStream(file)) {
                    read = in.readNBytes(head, 0, head.length);
                }
                String marker = name.endsWith(".webp") ? "ANIM" : "avis";
                String text = new String(head, 0, Math.max(read, 0), java.nio.charset.StandardCharsets.ISO_8859_1);
                if (text.contains(marker)) {
                    return name.endsWith(".webp") ? "an animated WebP" : "an animated AVIF";
                }
            }
        } catch (Exception e) {
            return null; // unreadable: not this check's job to fail the build
        }
        return null;
    }

    /**
     * The biggest a single bundle image may be.
     *
     * 4 MB, set from what content/ actually looks like after cleanup/images.py has
     * run -- the largest legitimate asset is moveRefactoring.webp at 3.53 MB, a
     * 228-frame screen recording -- rather than from a round number that happens to
     * look strict. 3 MB would have forced that one down to roughly 550px, where the
     * code being refactored is no longer readable, which is a worse outcome than a
     * slightly looser guard.
     *
     * The point is to catch the egregious, and it still does: the files that put
     * the artifact 255 MB over GitHub Pages' 1 GB limit were 52 MB, 26 MB, 10 MB
     * and 6.4 MB. Nothing in content/ is within 500 KB of this ceiling.
     */
    static final long MAX_IMAGE_BYTES = 4_000_000L;
    static final Set<String> IMAGE_EXTS = Set.of(".png", ".jpg", ".jpeg", ".gif", ".webp", ".avif", ".svg");

    /**
     * Fails the PR on an image too large to ship.
     *
     * This is the check that stops the whole problem coming back. The deploy
     * artifact reached 1.26 GB against GitHub Pages' hard 1 GB limit, and the
     * warning for that ("Deployment might fail") appears on a run that otherwise
     * goes GREEN -- so the site sails past the limit invisibly until a deploy
     * finally fails, at which point the cause is 2000 posts old. Three bundles
     * each carried the same 52 MB animated GIF.
     *
     * A contributor cannot see any of that from their own PR: one image looks
     * harmless. So the budget is enforced per file, at PR time, where it is
     * actionable and where the fix is obvious.
     *
     * Deliberately a per-FILE budget and not a total-size one: a total would fail
     * whichever PR happened to cross the line, blaming an author whose own images
     * were fine.
     */
    static List<String> checkImageWeight(Path contentDir) throws IOException {
        List<String> problems = new ArrayList<>();
        if (!Files.isDirectory(contentDir)) return problems;

        try (Stream<Path> files = Files.walk(contentDir)) {
            for (Path file : files.filter(Files::isRegularFile).toList()) {
                String name = file.getFileName().toString().toLowerCase();
                int dot = name.lastIndexOf('.');
                if (dot < 0 || !IMAGE_EXTS.contains(name.substring(dot))) continue;
                long size = Files.size(file);
                if (size <= MAX_IMAGE_BYTES) continue;
                problems.add(String.format(
                        "%s: image is %.1f MB, over the %.0f MB budget -- resize or compress it"
                        + " (an animated GIF should be an animated WebP; `python3 scripts/cleanup/images.py"
                        + " --path %s` does both)",
                        file, size / 1e6, MAX_IMAGE_BYTES / 1e6, file.getParent()));
            }
        }
        return problems;
    }

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

    /**
     * A hand-added calendar entry: data/events/<slug>.yaml, one file per
     * conference / community day / online event, opened as a pull request.
     * See template/event.yaml for the field-by-field version a contributor
     * reads.
     *
     * These need checking harder than most content does, for two reasons.
     * They are DATA, so Hugo will not complain about them the way it does
     * about a page -- a misspelled key is simply a key nothing reads, and the
     * event renders with a piece silently missing. And they are the one thing
     * on the site a first-time contributor is likely to send without ever
     * having built it locally: it is six lines of YAML, so of course they
     * won't.
     *
     * Hence the closed key set below. It is the check that earns its keep:
     * `website:` instead of `url:`, `dates:` instead of `start:`, `location:`
     * instead of `city:` are all things a reasonable person types, and every
     * one of them fails without a symptom. The schema is ours, so we can
     * afford to say exactly what belongs in it.
     *
     * What is NOT checked, deliberately: whether the event is in the past.
     * The layout drops an event the day after it ends, so an old file is
     * harmless -- failing the build over one would mean the calendar rots
     * into a chore, which is the opposite of the point.
     */
    static final Set<String> EVENT_KEYS = Set.of(
            "name", "type", "url", "start", "end", "venue", "city", "country", "online");

    static final Set<String> AD_KEYS = Set.of(
            "title", "description", "link", "cta", "image", "background", "sponsored",
            "secondaryCta", "secondaryLink", "publishDate", "expiryDate");

    static final Pattern HEX_COLOUR = Pattern.compile("#(?:[0-9a-fA-F]{3}|[0-9a-fA-F]{6})");

    /**
     * The home page banners in content/ads/. These are the only pages on the site
     * with money attached, and every way one goes wrong is silent: a banner is
     * never rendered as a page of its own, so nothing 404s and nothing errors --
     * the slide simply comes out missing its picture, its label or its link, on the
     * most-visited page of the site, and the advertiser is the one who notices.
     */
    static List<String> checkAds(Path adsDir) throws IOException {
        List<String> problems = new ArrayList<>();
        if (!Files.isDirectory(adsDir)) return problems;

        List<Path> dirs;
        try (Stream<Path> s = Files.list(adsDir)) {
            dirs = s.filter(Files::isDirectory).sorted().toList();
        }

        for (Path dir : dirs) {
            String slug = dir.getFileName().toString();
            Path index = dir.resolve("index.md");

            // A banner is a LEAF bundle: the creative has to sit next to the copy,
            // which is the whole reason these live in content/ instead of data/.
            // An _index.md would make it a branch bundle -- a section, whose child
            // pages would each claim a URL.
            if (!Files.isRegularFile(index)) {
                problems.add(dir + ": no index.md (a banner is a page bundle:"
                        + " content/ads/<slug>/index.md -- start from template/ad.md)");
                continue;
            }
            if (!SLUG_FMT.matcher(slug).matches()) {
                problems.add(dir + ": folder name '" + slug + "' is not a clean slug"
                        + " (lowercase letters, digits and dashes only,"
                        + " e.g. coderabbit-ai-code-review)");
            }

            Map<String, Object> fm = readFrontmatter(index);
            if (fm == null) {
                problems.add(index + ": no frontmatter block found (start from template/ad.md)");
                continue;
            }

            for (String key : fm.keySet()) {
                if (AD_KEYS.contains(key)) continue;
                // `url:` is the one worth naming, because it is what a data file
                // would have called this and it does not merely go unread: `url` is
                // Hugo's OWN frontmatter key, so it would set the banner's page URL
                // -- publishing the ad as a page at whatever path it names -- while
                // the button it was meant to point at silently loses its target.
                if (key.equals("url")) {
                    problems.add(index + ": 'url' is Hugo's own frontmatter key and would give"
                            + " this banner a page URL of its own -- the click target is 'link'");
                    continue;
                }
                problems.add(index + ": unknown field '" + key + "' -- nothing reads it."
                        + " Known fields: " + new TreeSet<>(AD_KEYS));
            }

            problems.addAll(checkRequired(index, fm, List.of("title", "link", "cta", "image", "background")));

            if (fm.get("link") instanceof String link && !link.isBlank() && !link.startsWith("http")) {
                problems.add(index + ": link '" + link + "' should be the advertiser's"
                        + " full https:// address");
            }

            // The optional second button needs both halves: the template keys off
            // secondaryCta, so a link with no label renders NOTHING -- the button
            // silently does not exist -- while a label with no link renders a
            // button pointing at the page it is already on.
            boolean hasCta = fm.get("secondaryCta") instanceof String c && !c.isBlank();
            boolean hasLink = fm.get("secondaryLink") instanceof String l && !l.isBlank();
            if (hasCta != hasLink) {
                problems.add(index + ": secondaryCta and secondaryLink go together --"
                        + " '" + (hasCta ? "secondaryLink" : "secondaryCta") + "' is missing,"
                        + " so the second button would render "
                        + (hasCta ? "with nowhere to go" : "not at all"));
            }
            // An internal destination must be root-relative so relURL can apply the
            // baseURL subpath. A bare "sustainability/..." resolves against
            // whatever directory the page happens to be served from.
            if (hasLink && fm.get("secondaryLink") instanceof String sl
                    && !sl.startsWith("http") && !sl.startsWith("/")) {
                problems.add(index + ": secondaryLink '" + sl + "' must start with '/'"
                        + " (an internal path) or 'https://' (an external URL)");
            }

            // The creative is a bundle resource, so a name that does not match a
            // file in this folder resolves to nothing at all: .Resources.GetMatch
            // returns no resource and resource-url.html falls through to treating
            // it as a path under the site root, which 404s.
            if (fm.get("image") instanceof String img && !img.isBlank()
                    && !img.startsWith("http") && !Files.isRegularFile(dir.resolve(img))) {
                problems.add(index + ": image '" + img + "' is not a file in " + dir
                        + " -- the creative belongs in the bundle, not hotlinked from the"
                        + " advertiser's own host");
            }

            // The text colour is derived from this, so an unparseable value does not
            // just lose the background -- it takes the contrast decision with it.
            if (fm.get("background") instanceof String bg && !bg.isBlank()
                    && !HEX_COLOUR.matcher(bg.trim()).matches()) {
                problems.add(index + ": background '" + bg + "' is not a hex colour"
                        + " (#RGB or #RRGGBB)");
            }

            if (fm.get("sponsored") != null && !(fm.get("sponsored") instanceof Boolean)) {
                problems.add(index + ": sponsored must be true or false, not '"
                        + fm.get("sponsored") + "'");
            }

            // publishDate/expiryDate are Hugo's own scheduling: a banner outside its
            // window is dropped from the build. Hugo fails the build outright on an
            // unparseable one, so this is only here to say which file and why.
            LocalDate from = eventDate(index, "publishDate", fm.get("publishDate"), problems);
            LocalDate till = eventDate(index, "expiryDate", fm.get("expiryDate"), problems);
            if (from != null && till != null && !till.isAfter(from)) {
                problems.add(index + ": expiryDate (" + till + ") is not after publishDate ("
                        + from + ") -- the banner would never run");
            }
        }
        return problems;
    }

    static List<String> checkEvents(Path eventsDir) throws IOException {
        List<String> problems = new ArrayList<>();
        if (!Files.isDirectory(eventsDir)) return problems;

        List<Path> files;
        try (Stream<Path> s = Files.list(eventsDir)) {
            files = s.filter(Files::isRegularFile).sorted().toList();
        }

        for (Path file : files) {
            String filename = file.getFileName().toString();
            // Hugo reads data/ as data: anything it cannot unmarshal fails the
            // BUILD, with a message about "format" that says nothing about what
            // to do. Catch it here, where we can.
            if (!filename.endsWith(".yaml") && !filename.endsWith(".yml")) {
                problems.add(file + ": only .yaml files belong in data/events/"
                        + " (Hugo reads every file in data/ as data and fails the build on"
                        + " anything else) -- put notes in template/event.yaml or CONTRIBUTING.md");
                continue;
            }

            String slug = stripExt(filename);
            if (!SLUG_FMT.matcher(slug).matches()) {
                problems.add(file + ": file name '" + slug + "' is not a clean slug"
                        + " (lowercase letters, digits and dashes only, e.g. devoxx-belgium-2026)");
            }

            Object loaded = new Yaml().load(Files.readString(file));
            if (!(loaded instanceof Map)) {
                problems.add(file + ": not a YAML mapping (start from template/event.yaml)");
                continue;
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> e = (Map<String, Object>) loaded;

            for (String key : e.keySet()) {
                if (!EVENT_KEYS.contains(key)) {
                    problems.add(file + ": unknown field '" + key + "' -- nothing reads it."
                            + " Known fields: " + new TreeSet<>(EVENT_KEYS));
                }
            }

            problems.addAll(checkRequired(file, e, List.of("name", "url", "start")));

            // An online event has no city; anything else without one cannot be
            // found, and the country is what the "N countries" count is built
            // from.
            if (!Boolean.TRUE.equals(e.get("online"))) {
                problems.addAll(checkRequired(file, e, List.of("city", "country")));
            }

            if (e.get("url") instanceof String url && !url.isBlank() && !url.startsWith("http")) {
                problems.add(file + ": url '" + url + "' should be the event's full https:// address");
            }

            LocalDate start = eventDate(file, "start", e.get("start"), problems);
            LocalDate end = e.get("end") == null ? start : eventDate(file, "end", e.get("end"), problems);
            if (start != null && end != null) {
                if (end.isBefore(start)) {
                    problems.add(file + ": end (" + end + ") is before start (" + start + ")");
                } else if (start.plusDays(31).isBefore(end)) {
                    // A month-long conference is a mistyped year, and it would
                    // otherwise draw a band across every cell of the calendar.
                    problems.add(file + ": start (" + start + ") to end (" + end + ") is longer than"
                            + " 31 days -- check the year");
                }
            }
        }
        return problems;
    }

    /**
     * A date from an event file. SnakeYAML turns an unquoted `2026-10-05` into
     * a Date and a quoted one into a String, and a contributor writes whichever
     * looks right to them, so both have to work -- as does a full timestamp
     * (`2026-10-05T19:00:00+02:00`), which is how an entry says the time of day
     * matters.
     */
    static LocalDate eventDate(Path file, String field, Object value, List<String> problems) {
        if (value == null) return null;
        if (value instanceof Date d) return d.toInstant().atZone(ZoneOffset.UTC).toLocalDate();
        String s = value.toString().trim();
        try {
            return LocalDate.parse(s.length() > 10 ? s.substring(0, 10) : s);
        } catch (DateTimeParseException ex) {
            problems.add(file + ": " + field + " '" + s + "' is not a date --"
                    + " write YYYY-MM-DD, or YYYY-MM-DDTHH:MM:SS+HH:MM when the time matters");
            return null;
        }
    }

    /* ---------------------------------------------------------------- alt text --

       An image with no description is invisible to a screen reader, and 1.1.1
       is the most-reported failure in any accessibility audit -- but see
       reportWarnings above for why this reports rather than fails.

       Three shapes, because content/ writes images three ways and all three
       default to an empty description when the author says nothing:

         ![](chart.png)                     a plain Markdown image
         {{< img src="chart.png" >}}        the formatted-image shortcode, whose
                                            template emits alt="{{ .Get "alt" }}"
         {{< gallery >}}chart.png{{< /gallery >}}
                                            a gallery line, whose alt is its
                                            caption (the text after the first |)

       WHAT IS DELIBERATELY NOT FLAGGED: a hero `image:` in frontmatter. The
       templates derive its alt from the post title, so there is nothing for an
       author to write and nothing to warn about.

       Fenced code is skipped. Several posts document Markdown itself, and one
       of them has ![](...) inside a fence as its subject matter -- a warning
       there is a warning about a code sample, which is noise of exactly the
       kind that teaches people to ignore the report.
    */
    static final Pattern MD_IMAGE_NO_ALT = Pattern.compile("!\\[\\s*\\]\\(");
    static final Pattern IMG_SHORTCODE = Pattern.compile("\\{\\{<\\s*img\\s[^>]*>\\s*\\}\\}");
    static final Pattern IMG_SHORTCODE_ALT = Pattern.compile("\\balt\\s*=\\s*\"([^\"]*)\"");

    static List<String> checkImageAltText(Path dir, Set<Path> touched) throws IOException {
        List<String> warnings = new ArrayList<>();
        if (!Files.isDirectory(dir)) return warnings;

        try (Stream<Path> files = Files.walk(dir)) {
            for (Path file : files.filter(p -> p.toString().endsWith(".md")).sorted().toList()) {
                if (touched != null && !touched.contains(file.normalize())) continue;
                if (isPageResource(file)) continue;
                List<Integer> lines = imagesWithoutAlt(Files.readString(file));
                if (lines.isEmpty()) continue;
                String where = lines.size() > 6
                        ? lines.subList(0, 6) + " and " + (lines.size() - 6) + " more"
                        : lines.toString();
                warnings.add(file + ": " + lines.size() + " image"
                        + (lines.size() == 1 ? "" : "s") + " with no description, on line"
                        + (lines.size() == 1 ? " " : "s ") + where.replace("[", "").replace("]", ""));
            }
        }
        return warnings;
    }

    /** Line numbers (1-based) of images carrying no description, body only. */
    static List<Integer> imagesWithoutAlt(String content) {
        List<Integer> lines = new ArrayList<>();
        String[] all = content.split("\\n", -1);

        boolean inFrontmatter = all.length > 0 && all[0].trim().equals("---");
        boolean inFence = false;
        boolean inGallery = false;

        for (int i = 0; i < all.length; i++) {
            String line = all[i];
            String trimmed = line.trim();

            if (inFrontmatter) {
                if (i > 0 && trimmed.equals("---")) inFrontmatter = false;
                continue;
            }
            if (trimmed.startsWith("```") || trimmed.startsWith("~~~")) { inFence = !inFence; continue; }
            if (inFence) continue;

            // A gallery's items are one per line between the tags, and an item's
            // alt text is the caption after the first `|` (or the third field
            // when the two differ), so a bare filename means no description.
            if (trimmed.matches("\\{\\{<\\s*gallery.*?/?>\\s*\\}\\}")) {
                inGallery = !trimmed.endsWith("/>}}") && !trimmed.contains("{{< /gallery");
                continue;
            }
            if (trimmed.startsWith("{{< /gallery") || trimmed.startsWith("{{</gallery")) { inGallery = false; continue; }
            if (inGallery) {
                if (trimmed.isEmpty()) continue;
                String[] parts = trimmed.split("\\|", -1);
                boolean described = (parts.length > 1 && !parts[1].isBlank())
                        || (parts.length > 2 && !parts[2].isBlank());
                if (!described) lines.add(i + 1);
                continue;
            }

            if (MD_IMAGE_NO_ALT.matcher(line).find()) lines.add(i + 1);

            Matcher shortcode = IMG_SHORTCODE.matcher(line);
            while (shortcode.find()) {
                Matcher alt = IMG_SHORTCODE_ALT.matcher(shortcode.group());
                if (!alt.find() || alt.group(1).isBlank()) lines.add(i + 1);
            }
        }
        return lines;
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
