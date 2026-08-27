///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS com.fasterxml.jackson.core:jackson-databind:2.17.1
//SOURCES ../shared/HtmlToMarkdown.java
//JAVA 17+

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.Separators;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Captures the legacy WordPress comments on foojay.io into the repo, one
 * comments.json per post bundle, so cutover doesn't throw away the conversation
 * under 270 articles. themes/foojay/layouts/partials/legacy-comments.html
 * renders them under the giscus widget as "Discussions on the previous Foojay
 * site".
 *
 * Usage:
 *   jbang scripts/transfer/Comments.java --dry-run        (report only, writes nothing)
 *   jbang scripts/transfer/Comments.java                  (write/refresh every comments.json)
 *   jbang scripts/transfer/Comments.java --slug some-post (one post; with --dry-run, prints its bodies)
 *
 * Needs NO credential of any kind, and writes nothing outside this repository.
 *
 * WHY THIS IS AN ARCHIVE IN THE REPO AND NOT AN IMPORT INTO GITHUB DISCUSSIONS
 * It used to be the latter: it posted all 580 comments into the Discussions
 * giscus reads, as the foojay.io account, so an imported thread and one giscus
 * creates for a new post were indistinguishable. That is dead, and not for a
 * design reason -- GITHUB BANNED THE ACCOUNT after it had handled only a few
 * posts. Several hundred API-driven comment creations from a fresh account is
 * indistinguishable from spam at GitHub's end, and there is no version of that
 * approach that does not look exactly like the thing that got blocked. So the
 * comments do not go to a third party at all now.
 *
 * That turns out to be the better shape regardless, for three reasons:
 *   - THESE COMMENTS ARE A CLOSED RECORD, not a live thread. Their authors are
 *     strangers whose GitHub identities are unknown; nobody can edit, delete or
 *     reply to their own 2020 comment whatever we do. Writing them into a
 *     mutable discussion pretended otherwise. A dated archive says what it is.
 *   - IT IS REVERSIBLE AND REVIEWABLE. The old version made irreversible public
 *     writes to somebody else's API; this one makes a diff. Getting the
 *     conversion wrong now costs a re-run, not an apology to 580 people.
 *   - THE ONLY COPY IS COMMITTED. Same reason data/legacy-views.json is: these
 *     bodies vanish with the WordPress site, and after that no source exists.
 *
 * Nothing about giscus changes. It still owns NEW comments on every post, keyed
 * on the same term (see partials/comments-term.html); this only fills in the
 * history above it. The two live in separate sections that say which is which.
 *
 * WHERE THE COMMENTS COME FROM
 * WordPress's own REST API, open on foojay.io and needing no credentials:
 * /wp-json/wp/v2/comments. Unauthenticated reads return approved comments of
 * type "comment" only, so spam, pending moderation and pingbacks are excluded
 * by construction. Each comment carries its post's public URL in `link`, which
 * is how it is matched to a local bundle -- no WP post-id bookkeeping in
 * frontmatter. Measured: 580 comments across 270 posts, every one of them under
 * /today/, so posts are the only section that can have any (pedia entries and
 * pages take giscus comments but have no WordPress history to import).
 *
 * THE STORED HTML IS SANITIZED HERE, AND THAT IS A SECURITY BOUNDARY
 * hugo.toml sets `unsafe = true` for Goldmark, because post bodies contain
 * deliberate raw HTML. So rendering 580 bodies written by anonymous strangers
 * through `markdownify` would be a stored-XSS hole, and storing Markdown at all
 * would leave the template no way to tell an author's intentional HTML from a
 * commenter's. Instead every body is run through jsoup's Safelist -- the
 * library's own tested sanitizer, not a regex written here -- and the RESULT is
 * stored, so `legacy-comments.html` renders it with `safeHTML` and the template
 * never sees unsanitized input. Anything not on the list is dropped, including
 * every attribute that can execute.
 *
 * The list is `Safelist.basic()`, which is measured against the data rather than
 * guessed: across all 580 comments the only tags used are p (969), br (470),
 * a (110), code (15), pre (12), strong (9), em (2) and blockquote (1). No image,
 * no iframe, no script, no style, no embed. basic() covers all eight and permits
 * none of the rest. If a re-run ever reports a dropped tag, look at it before
 * widening the list.
 *
 * IDEMPOTENCY, AND WHY THERE IS NO TIMESTAMP IN THE FILE
 * Safe to re-run as often as you like, which it needs to be: WordPress keeps
 * accepting comments until it is switched off, so this runs again as late as
 * possible before cutover (see CUTOVER.md), exactly like
 * transfer/LegacyViews.java --seed. A file is REWRITTEN ONLY WHEN ITS CONTENT
 * CHANGED, and it carries no "generated at" field -- the same lesson
 * fetch/JugEvents.java records: a timestamp that moves on every run means every
 * run commits, and therefore deploys, on nothing. Git already records when the
 * file changed. So a re-run with no new comments is a genuine no-op with an
 * empty diff.
 *
 * `frozen: true` is deliberately NOT honoured, unlike the other transfer/
 * scrapers. Those rebuild a post's frontmatter and body, where a hand edit is
 * precious; this writes one generated file beside it and touches nothing a
 * human wrote. A corrected comment body is not a thing that exists -- these are
 * quotes from other people.
 *
 * THREADING
 * WordPress's real nesting is kept: 461 comments are top-level, 115 are one deep
 * and 4 are two deep. The array is written in threaded display order (a comment
 * followed by its own replies, oldest first) with a `depth` on each, so the
 * template renders it with a `range` and no recursion, and the CSS clamps the
 * indent. 3 comments whose parent is not published are written at depth 0 --
 * their parent is gone, so there is nothing to nest them under.
 */
public class Comments {

    // ---- CONFIG -------------------------------------------------------
    static final String WP_BASE = "https://foojay.io";
    static final Path POSTS_DIR = Path.of("content/posts");
    /** The generated file, beside index.md in the post's bundle. */
    static final String COMMENTS_FILE = "comments.json";

    static final int WP_PAGE_SIZE = 100;
    static final int REQUEST_TIMEOUT_MS = 30_000;

    /** WP comment links look like https://foojay.io/today/<slug>/#comment-123 */
    static final Pattern WP_COMMENT_LINK = Pattern.compile("^https?://[^/]+/today/([^/]+)/?(?:#.*)?$");
    /** An `aliases:` entry pointing at a legacy /today/<slug>/ path. */
    static final Pattern ALIAS_TODAY_PATH = Pattern.compile("^\\s*-\\s*\"?/today/([^/\"]+)/?\"?\\s*$");
    static final Pattern SLUG_FRONTMATTER = Pattern.compile("^slug:\\s*\"?([^\"\\s]+)\"?\\s*$");

    static final DateTimeFormatter WP_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    static final DateTimeFormatter HUMAN_DATE = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);

    /**
     * What a comment body may contain after sanitization. See the class comment:
     * this is jsoup's own tested safelist, and every tag the 580 real comments
     * use is on it. Links additionally get rel/target below.
     */
    static final Safelist COMMENT_HTML = Safelist.basic();

    static final ObjectMapper JSON = new ObjectMapper();
    /**
     * Pinned to "\n" rather than using the default pretty printer, which indents
     * with System.lineSeparator(): on Windows that alone would rewrite all 269
     * files with CRLF and produce a diff of pure line endings, which is exactly
     * the churn the write-only-when-changed rule exists to prevent.
     */
    static final DefaultPrettyPrinter PRETTY = new DefaultPrettyPrinter()
            .withObjectIndenter(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE.withLinefeed("\n"))
            .withArrayIndenter(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE.withLinefeed("\n"))
            // `"id": 4`, not Jackson's default `"id" : 4` -- these files are read
            // in diffs by people, and the stray space before the colon is not what
            // JSON looks like anywhere else in the repo.
            .withSeparators(Separators.createDefaultInstance()
                    .withObjectFieldValueSpacing(Separators.Spacing.AFTER));
    static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(REQUEST_TIMEOUT_MS))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    // ---- STATE --------------------------------------------------------
    static boolean dryRun = false;
    static String onlySlug = null;

    /** What the local content tree knows: every URL slug a post answered to, and where its bundle is. */
    record PostIndex(Map<String, String> slugToTerm, Map<String, Path> bundles) {
    }

    /** One WordPress comment, as the REST API hands it over. */
    record WpComment(int id, int parent, String author, String authorUrl,
                     LocalDateTime date, String html, String slug) {
    }

    public static void main(String[] args) throws Exception {
        try {
            run(args);
        } catch (IOException e) {
            // Being run from the wrong directory, or WordPress being down, is for
            // the person at the terminal to fix -- the message says what is wrong,
            // a stack trace only buries it.
            System.err.println("ERROR: " + e.getMessage());
            System.exit(1);
        }
    }

    static void run(String[] args) throws Exception {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--dry-run" -> dryRun = true;
                case "--slug" -> onlySlug = args[++i];
                case "--help", "-h" -> {
                    usage();
                    return;
                }
                default -> {
                    System.err.println("Unknown argument: " + args[i]);
                    usage();
                    System.exit(2);
                }
            }
        }

        // ---- 1. what does WordPress have --------------------------------
        Fetched fetched = fetchWpComments();
        List<WpComment> comments = fetched.comments();
        System.out.printf("WordPress: %d approved comments%n", comments.size());
        // WP's own header count against what the listing actually handed over. A
        // gap is not an error (a comment on an unpublished post is counted and not
        // listed), but it is the one number that would show a page of the listing
        // having silently failed, so it is printed rather than assumed away.
        if (fetched.reportedTotal() > 0 && fetched.reportedTotal() != comments.size() + fetched.skipped()) {
            System.out.printf("NOTE: WordPress reports %d comments in total; %d were listed%s. "
                            + "The remainder are normally comments whose post is not published.%n",
                    fetched.reportedTotal(), comments.size(),
                    fetched.skipped() > 0 ? " and " + fetched.skipped() + " skipped as unusable" : "");
        }

        // ---- 2. which local post does each belong to --------------------
        PostIndex posts = indexLocalPosts();
        System.out.printf("Local content: %d post bundles%n", posts.bundles().size());

        Map<String, List<WpComment>> byTerm = new TreeMap<>();
        List<WpComment> unmatched = new ArrayList<>();
        for (WpComment c : comments) {
            String term = resolveTerm(c.slug(), posts.slugToTerm());
            if (term == null) {
                unmatched.add(c);
                continue;
            }
            if (onlySlug != null && !onlySlug.equals(term)) continue;
            byTerm.computeIfAbsent(term, k -> new ArrayList<>()).add(c);
        }
        for (WpComment c : unmatched) {
            System.out.printf("WARNING: no post bundle for /today/%s/ -- skipping comment %d by %s%n",
                    c.slug(), c.id(), c.author());
        }
        if (onlySlug != null && byTerm.isEmpty()) {
            System.out.printf("No WordPress comments found for post '%s'.%n", onlySlug);
            return;
        }
        System.out.printf("To archive: %d comments across %d posts%n",
                byTerm.values().stream().mapToInt(List::size).sum(), byTerm.size());
        System.out.println();

        // ---- 3. write one comments.json per post ------------------------
        int written = 0, unchanged = 0;
        Set<String> droppedTags = new TreeSet<>();
        for (Map.Entry<String, List<WpComment>> entry : byTerm.entrySet()) {
            String term = entry.getKey();
            Path bundle = posts.bundles().get(term);
            if (bundle == null) {
                System.out.printf("WARNING: no bundle directory for '%s' -- skipping %d comment(s)%n",
                        term, entry.getValue().size());
                continue;
            }
            List<WpComment> threaded = threadedOrder(entry.getValue());
            String json = renderJson(threaded, droppedTags);
            Path target = bundle.resolve(COMMENTS_FILE);

            String existing = Files.exists(target) ? Files.readString(target) : null;
            if (json.equals(existing)) {
                unchanged++;
                continue;
            }
            String verb = existing == null ? "+" : "~";
            if (dryRun) {
                System.out.printf("%s %s  %d comment(s) (would %s)%n",
                        verb, target, threaded.size(), existing == null ? "create" : "update");
            } else {
                Files.writeString(target, json);
                System.out.printf("%s %s  %d comment(s)%n", verb, target, threaded.size());
            }
            written++;

            // With --slug there is one post in play, so showing the bodies is cheap
            // and is the way to eyeball the sanitizer's output before a bulk run.
            if (onlySlug != null && dryRun) {
                System.out.println();
                for (WpComment c : threaded) {
                    System.out.printf("--- comment %d, depth %d, %s on %s%n",
                            c.id(), depthOf(c, byId(entry.getValue())), c.author(),
                            HUMAN_DATE.format(c.date()));
                    System.out.println(sanitizeHtml(c.html(), droppedTags).replaceAll("(?m)^", "      | "));
                }
                System.out.println();
            }
        }

        // ---- 4. what is here that WordPress no longer has ---------------
        // Only a FULL run knows the complete set, so a --slug run cannot judge
        // this. Reported, never deleted: an orphan means WP dropped a comment (or
        // a post was renamed), and which of those it is needs a human.
        if (onlySlug == null) {
            for (Path orphan : orphanedFiles(byTerm.keySet(), posts)) {
                System.out.printf("NOTE: %s exists but WordPress now reports no comments for that post%n", orphan);
            }
        }

        System.out.println();
        System.out.printf("%s %d file(s) %s, %d already up to date%n",
                dryRun ? "Dry run --" : "Done.",
                written, dryRun ? "to write" : "written", unchanged);
        if (!droppedTags.isEmpty()) {
            System.out.printf("NOTE: tags dropped by the sanitizer: %s. See the class comment "
                    + "before widening COMMENT_HTML.%n", String.join(", ", droppedTags));
        }
        if (dryRun) {
            System.out.println("Nothing was written. Drop --dry-run to write the files.");
        }
    }

    static void usage() {
        System.out.println("""
                Captures the legacy WordPress comments on foojay.io into the repo, as one
                comments.json per post bundle. Rendered by partials/legacy-comments.html
                under the giscus widget. Needs no credential and writes nothing outside
                this repository.

                  jbang scripts/transfer/Comments.java [options]

                  --dry-run           report what would change, write nothing
                  --slug <post-slug>  only this post (with --dry-run, prints its comment bodies)

                Safe to re-run: a file is rewritten only when its content changed, so a run
                with no new comments leaves an empty diff. Run it again as late as possible
                before cutover -- WordPress keeps accepting comments until it is switched
                off, and after that these bodies have no other source.""");
    }

    // ---- WordPress ----------------------------------------------------

    /** The listing, plus the two counts needed to notice a page having gone missing. */
    record Fetched(List<WpComment> comments, int reportedTotal, int skipped) {
    }

    /**
     * Pulls every approved comment through WP's open REST API. Unauthenticated
     * reads return approved comments of type "comment" only, so spam, pending
     * moderation and pingbacks never reach us.
     */
    static Fetched fetchWpComments() throws IOException, InterruptedException {
        List<WpComment> out = new ArrayList<>();
        int page = 1, totalPages = 1, reportedTotal = 0, skipped = 0;
        do {
            String url = WP_BASE + "/wp-json/wp/v2/comments"
                    + "?per_page=" + WP_PAGE_SIZE + "&page=" + page
                    + "&order=asc&orderby=date"
                    + "&_fields=id,post,parent,author_name,author_url,date,content,link";
            HttpResponse<String> response = HTTP.send(
                    HttpRequest.newBuilder(URI.create(url))
                            .timeout(Duration.ofMillis(REQUEST_TIMEOUT_MS))
                            .header("Accept", "application/json")
                            .header("User-Agent", "foojay-hugo-migration/1.0 (+https://foojay.io)")
                            .GET().build(),
                    HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IOException("HTTP " + response.statusCode() + " fetching " + url);
            }
            if (page == 1) {
                totalPages = response.headers().firstValue("x-wp-totalpages")
                        .map(Integer::parseInt).orElse(1);
                reportedTotal = response.headers().firstValue("x-wp-total")
                        .map(Integer::parseInt).orElse(0);
            }
            for (JsonNode node : JSON.readTree(response.body())) {
                WpComment c = toComment(node);
                if (c == null) skipped++;
                else out.add(c);
            }
            page++;
        } while (page <= totalPages);
        return new Fetched(out, reportedTotal, skipped);
    }

    static WpComment toComment(JsonNode node) {
        String link = node.path("link").asText("");
        Matcher m = WP_COMMENT_LINK.matcher(link);
        if (!m.matches()) {
            System.out.printf("WARNING: comment %d has an unexpected link (%s) -- skipping%n",
                    node.path("id").asInt(), link);
            return null;
        }
        String html = node.path("content").path("rendered").asText("");
        if (html.isBlank()) return null;
        return new WpComment(
                node.path("id").asInt(),
                node.path("parent").asInt(),
                node.path("author_name").asText("").trim(),
                node.path("author_url").asText("").trim(),
                LocalDateTime.parse(node.path("date").asText(), WP_DATE),
                html,
                URLDecoder.decode(m.group(1), StandardCharsets.UTF_8));
    }

    // ---- local content ------------------------------------------------

    /**
     * Maps every URL slug a post has ever been served under to its term (the
     * value hugo.toml's `:slugorcontentbasename` resolves to, i.e. the `slug`
     * frontmatter if set and otherwise the bundle folder name), and each term to
     * its bundle directory. Legacy `aliases:` paths map to the same term, so a
     * post whose folder was renamed is still found from its WordPress URL.
     */
    static PostIndex indexLocalPosts() throws IOException {
        Map<String, String> slugToTerm = new HashMap<>();
        Map<String, Path> bundles = new HashMap<>();
        if (!Files.isDirectory(POSTS_DIR)) {
            throw new IOException("Run this from the repository root: " + POSTS_DIR + " not found");
        }
        List<Path> indexes;
        try (Stream<Path> s = Files.walk(POSTS_DIR)) {
            indexes = s.filter(p -> p.getFileName().toString().equals("index.md")).sorted().toList();
        }
        for (Path index : indexes) {
            String folder = index.getParent().getFileName().toString();
            String term = folder;
            List<String> aliases = new ArrayList<>();
            boolean inAliases = false;
            for (String line : frontmatterLines(index)) {
                Matcher slug = SLUG_FRONTMATTER.matcher(line);
                if (slug.matches()) {
                    term = slug.group(1);
                    continue;
                }
                if (line.startsWith("aliases:")) {
                    inAliases = true;
                    continue;
                }
                if (inAliases) {
                    Matcher alias = ALIAS_TODAY_PATH.matcher(line);
                    if (alias.matches()) aliases.add(alias.group(1));
                    else if (!line.startsWith(" ") && !line.startsWith("-")) inAliases = false;
                }
            }
            slugToTerm.put(folder, term);
            slugToTerm.put(term, term);
            for (String alias : aliases) slugToTerm.put(alias, term);
            bundles.put(term, index.getParent());
        }
        return new PostIndex(slugToTerm, bundles);
    }

    static List<String> frontmatterLines(Path file) throws IOException {
        List<String> all = Files.readAllLines(file);
        if (all.isEmpty() || !all.get(0).startsWith("---")) return List.of();
        List<String> out = new ArrayList<>();
        for (int i = 1; i < all.size(); i++) {
            if (all.get(i).startsWith("---")) break;
            out.add(all.get(i));
        }
        return out;
    }

    /**
     * The WP slug is usually the local one. When it isn't, it's because the
     * folder was sanitized (an emoji or a capital in the WP slug), so try the
     * same sanitization the slug cleanup applied.
     */
    static String resolveTerm(String wpSlug, Map<String, String> index) {
        String direct = index.get(wpSlug);
        if (direct != null) return direct;
        return index.get(sanitize(wpSlug));
    }

    static String sanitize(String s) {
        return s.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9_-]+", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("^[-_]+|[-_]+$", "");
    }

    /** comments.json files whose post WordPress no longer reports any comment for. */
    static List<Path> orphanedFiles(Set<String> haveComments, PostIndex posts) {
        List<Path> out = new ArrayList<>();
        posts.bundles().forEach((term, dir) -> {
            if (haveComments.contains(term)) return;
            Path f = dir.resolve(COMMENTS_FILE);
            if (Files.exists(f)) out.add(f);
        });
        Collections.sort(out);
        return out;
    }

    // ---- comment bodies -----------------------------------------------

    /**
     * The security boundary -- see the class comment. jsoup's own sanitizer
     * decides what survives; anything it drops is collected so a run REPORTS a
     * tag it threw away rather than losing it silently.
     *
     * Links then get `target`/`rel`: these are 110 URLs typed by strangers years
     * ago, so `nofollow ugc` is what they are, and `noopener` because a new tab
     * without it hands the opened page a handle on ours.
     *
     * Note there is deliberately no Cloudflare email decoding here, unlike the
     * HTML scrapers: Cloudflare's obfuscator rewrites HTML responses, not JSON
     * ones, so the REST API hands over the real address. Measured -- 0 of 580
     * bodies carry a placeholder.
     */
    static String sanitizeHtml(String html, Set<String> droppedTags) {
        Element before = Jsoup.parseBodyFragment(html).body();
        String cleaned = Jsoup.clean(html, "", COMMENT_HTML);
        Element after = Jsoup.parseBodyFragment(cleaned).body();

        Set<String> had = new TreeSet<>();
        before.getAllElements().forEach(e -> had.add(e.tagName()));
        Set<String> kept = new TreeSet<>();
        after.getAllElements().forEach(e -> kept.add(e.tagName()));
        had.removeAll(kept);
        droppedTags.addAll(had);

        for (Element a : after.select("a[href]")) {
            a.attr("target", "_blank");
            a.attr("rel", "nofollow ugc noopener");
        }
        // A U+00A0 inside code looks like an indent and is not one -- copy the
        // sample out and the compiler chokes. Same rule as HtmlToMarkdown's
        // normalizeCodeSpaces; 2 of the 580 bodies are affected. Prose is left
        // alone, where a non-breaking space can be deliberate.
        for (Element code : after.select("code, pre")) {
            code.html(code.html().replace('\u00a0', ' '));
        }
        return after.html().trim();
    }

    /**
     * Threaded display order: each comment followed by its own replies, oldest
     * first at every level. A comment whose parent is not published is treated as
     * top-level -- there is nothing to nest it under.
     */
    static List<WpComment> threadedOrder(List<WpComment> postComments) {
        List<WpComment> sorted = new ArrayList<>(postComments);
        sorted.sort(Comparator.comparing(WpComment::date).thenComparingInt(WpComment::id));
        Map<Integer, WpComment> byId = byId(sorted);
        Map<Integer, List<WpComment>> children = new LinkedHashMap<>();
        List<WpComment> roots = new ArrayList<>();
        for (WpComment c : sorted) {
            if (c.parent() != 0 && byId.containsKey(c.parent())) {
                children.computeIfAbsent(c.parent(), k -> new ArrayList<>()).add(c);
            } else {
                roots.add(c);
            }
        }
        List<WpComment> out = new ArrayList<>();
        for (WpComment root : roots) appendThread(root, children, out, 0);
        return out;
    }

    /** Depth-first, with a guard: a cycle in WP's parent ids must not hang the run. */
    static void appendThread(WpComment c, Map<Integer, List<WpComment>> children,
                             List<WpComment> out, int depth) {
        if (depth > 20 || out.contains(c)) return;
        out.add(c);
        for (WpComment child : children.getOrDefault(c.id(), List.of())) {
            appendThread(child, children, out, depth + 1);
        }
    }

    static Map<Integer, WpComment> byId(List<WpComment> comments) {
        Map<Integer, WpComment> byId = new HashMap<>();
        comments.forEach(c -> byId.put(c.id(), c));
        return byId;
    }

    /** How deep a comment sits, counting only ancestors that are actually published. */
    static int depthOf(WpComment c, Map<Integer, WpComment> byId) {
        int depth = 0;
        WpComment current = c;
        for (int guard = 0; guard < 20; guard++) {
            WpComment parent = byId.get(current.parent());
            if (parent == null) return depth;
            depth++;
            current = parent;
        }
        return depth;
    }

    // ---- output -------------------------------------------------------

    /**
     * A bare JSON array, because there is nothing true about the set that isn't
     * derivable from it: a count would be `len`, and a "generated at" is what git
     * already records (and would make every run a commit -- see the class
     * comment). A blank authorUrl is omitted rather than written as "", so the
     * template's `with` is the only test it needs.
     */
    static String renderJson(List<WpComment> threaded, Set<String> droppedTags) throws IOException {
        Map<Integer, WpComment> byId = byId(threaded);
        ArrayNode array = JSON.createArrayNode();
        for (WpComment c : threaded) {
            ObjectNode node = array.addObject();
            node.put("id", c.id());
            node.put("depth", depthOf(c, byId));
            node.put("author", c.author().isBlank() ? "Anonymous" : c.author());
            if (!c.authorUrl().isBlank()) node.put("authorUrl", c.authorUrl());
            node.put("date", c.date().format(WP_DATE));
            node.put("html", sanitizeHtml(c.html(), droppedTags));
        }
        return JSON.writer(PRETTY).writeValueAsString(array) + "\n";
    }
}
