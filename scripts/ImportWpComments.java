///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS com.fasterxml.jackson.core:jackson-databind:2.17.1
//SOURCES HtmlToMarkdown.java
//JAVA 17+

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * One-off migration: moves the legacy WordPress comments on foojay.io into
 * GitHub Discussions, so the giscus widget on the Hugo site
 * (themes/foojay/layouts/partials/comments.html) shows the existing
 * conversation instead of starting every post at zero.
 *
 * Usage:
 *   jbang scripts/ImportWpComments.java --dry-run          (report only, writes nothing)
 *   jbang scripts/ImportWpComments.java --print-config      (resolve + print the [params.giscus] block)
 *   jbang scripts/ImportWpComments.java                     (do the import)
 *   jbang scripts/ImportWpComments.java --slug some-post    (one post, for a first live test)
 *   jbang scripts/ImportWpComments.java --limit 300         (stop after 300 creations, then re-run)
 *
 * Needs GITHUB_TOKEN (or GH_TOKEN) in the environment, holding a token of the
 * account the comments should be posted as -- the foojay.io account, since the
 * GitHub identity of the original commenters is unknown. Scope: `public_repo`
 * for a classic token, or a fine-grained token with "Discussions: Read and
 * write" on the repo. Every imported comment therefore opens with
 *
 *     _Originally posted by **Jan** on October 3, 2020 in Foojay.io Discussions._
 *
 * which is the only honest way to attribute it, and the original comment's WP
 * id is left in an HTML comment underneath for idempotency (see below).
 *
 * WHY THIS IS NOT PART OF ConvertPosts.java
 * The TODO asked whether the post converter could take this over. It shouldn't:
 * ConvertPosts writes files into content/ and is re-run against the live WP site
 * throughout the trial period, while this posts irreversible public content into
 * a third-party API, needs a write token, and needs to run exactly once (plus
 * top-ups for comments posted on WP before cutover). Mixing the two would mean
 * every routine content re-scrape carries a credential and a side effect on
 * GitHub. Same reason ConvertSponsors.java is run by hand and FetchJugs.java
 * isn't.
 *
 * WHERE THE COMMENTS COME FROM
 * WordPress's own REST API, which is open on foojay.io and needs no
 * credentials: /wp-json/wp/v2/comments (580 approved comments across 270 posts
 * at the time of writing; unauthenticated reads only ever return approved
 * ones, so spam and pending moderation are excluded by construction). Each
 * comment carries its post's public URL in `link`, which is how a comment is
 * matched to a local post bundle -- no WP post-id bookkeeping needed in
 * frontmatter.
 *
 * HOW A POST MAPS TO A DISCUSSION
 * giscus finds the discussion belonging to a page by searching the repo for the
 * page's "term". comments.html configures mapping="specific" with the post's
 * slug as the term (NOT pathname, which would break at cutover: the trial
 * deploy serves /website/today/<slug>/ and production serves /today/<slug>/, so
 * pathname-keyed threads would all be orphaned the day the domain moves).
 *
 * It also configures strict="1", and this script therefore writes the discussion
 * exactly the way giscus itself would: title = the term, and
 * `<!-- sha1: <sha1-of-term> -->` appended to the body, which is what strict
 * mode searches for. Strict matters here: non-strict mode does a fuzzy
 * `in:title` search and takes the first hit, and 30 of foojay's slugs are
 * substrings of another slug ("a-dissection-of-java-jdbc-to-postgresql-connections"
 * inside "...-part-2-batching"), so a fuzzy match would attach a post's comments
 * to the wrong thread. Keeping both the title and the sha1 marker means a
 * discussion created here is indistinguishable from one giscus creates when the
 * first visitor comments on a new post.
 *
 * IDEMPOTENCY
 * Re-running never duplicates anything, and the derived state lives on GitHub
 * rather than in a state file in this repo:
 *   - a discussion is reused when one already exists for the term (matched on
 *     the sha1 marker, or on an exact title),
 *   - a comment is skipped when the discussion already holds a comment with its
 *     `<!-- wp-comment-id: N -->` marker.
 * So an interrupted run, a rate-limit stop or a `--limit` batch is simply
 * resumed by running the script again.
 *
 * THREADING
 * GitHub Discussions allow one level of replies; WordPress allows more. A
 * top-level WP comment becomes a discussion comment, and everything below it
 * becomes a reply to that same comment (foojay has 118 comments one level deep
 * and 4 two levels deep, so this flattens 4 comments' nesting). A comment whose
 * parent is itself not published -- 3 of them -- is imported top-level.
 *
 * RATE LIMITS
 * GitHub throttles content creation far more aggressively than reads (a few
 * hundred per hour), and this import is ~850 creations. Mutations are therefore
 * spaced by --delay (default 1200ms) and back off on a secondary-rate-limit
 * error, up to a point; when GitHub keeps refusing, the run stops with a resume
 * hint rather than hammering it. Because the whole thing is idempotent, running
 * it again an hour later picks up where it left off.
 */
public class ImportWpComments {

    // ---- CONFIG -------------------------------------------------------
    static final String WP_BASE = "https://foojay.io";
    /** Production URLs, deliberately: they're what the discussion body should point at after cutover. */
    static final String SITE_BASE = "https://foojay.io";
    static final String GITHUB_GRAPHQL = "https://api.github.com/graphql";
    static final Path POSTS_DIR = Path.of("content/posts");

    static final String DEFAULT_REPO = "foojayio/website";
    /** A dedicated category keeps 2000+ comment threads out of "General". Must match hugo.toml's params.giscus.category. */
    static final String DEFAULT_CATEGORY = "Blog Comments";

    static final int WP_PAGE_SIZE = 100;
    static final int REQUEST_TIMEOUT_MS = 30_000;
    static final long DEFAULT_MUTATION_DELAY_MS = 1200;
    static final int MAX_RETRIES = 4;

    /** Marker carrying the WordPress comment id, so a re-run knows what it already posted. */
    static final Pattern WP_COMMENT_MARKER = Pattern.compile("<!--\\s*wp-comment-id:\\s*(\\d+)\\s*-->");
    /** giscus's own strict-mode marker: <!-- sha1: <hex> -->. */
    static final String SHA1_MARKER_PREFIX = "<!-- sha1: ";
    /** WP comment links look like https://foojay.io/today/<slug>/#comment-123 */
    static final Pattern WP_COMMENT_LINK = Pattern.compile("^https?://[^/]+/today/([^/]+)/?(?:#.*)?$");
    /** An `aliases:` entry pointing at a legacy /today/<slug>/ path. */
    static final Pattern ALIAS_TODAY_PATH = Pattern.compile("^\\s*-\\s*\"?/today/([^/\"]+)/?\"?\\s*$");
    static final Pattern SLUG_FRONTMATTER = Pattern.compile("^slug:\\s*\"?([^\"\\s]+)\"?\\s*$");

    static final DateTimeFormatter WP_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    static final DateTimeFormatter HUMAN_DATE = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);

    static final ObjectMapper JSON = new ObjectMapper();
    static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(REQUEST_TIMEOUT_MS))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    // ---- STATE --------------------------------------------------------
    static String repo = DEFAULT_REPO;
    static String category = DEFAULT_CATEGORY;
    static boolean dryRun = false;
    static String onlySlug = null;
    static int limit = Integer.MAX_VALUE;
    static long mutationDelayMs = DEFAULT_MUTATION_DELAY_MS;
    static String token = null;
    static int creations = 0;
    static long lastMutationAt = 0;

    /** What the local content tree knows: every URL slug a post answered to, and its title. */
    record PostIndex(Map<String, String> slugToTerm, Map<String, String> titles) {
    }

    /** One WordPress comment, as the REST API hands it over. */
    record WpComment(int id, int parent, String author, String authorUrl,
                     LocalDateTime date, String html, String slug) {
    }

    public static void main(String[] args) throws Exception {
        try {
            run(args);
        } catch (IOException e) {
            // A wrong repo/category, a missing token scope or being run from the
            // wrong directory are for the person at the terminal to fix -- the
            // message says what's wrong, a stack trace only buries it.
            System.err.println("ERROR: " + e.getMessage());
            System.exit(1);
        }
    }

    static void run(String[] args) throws Exception {
        boolean printConfig = false;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--dry-run" -> dryRun = true;
                case "--print-config" -> printConfig = true;
                case "--repo" -> repo = args[++i];
                case "--category" -> category = args[++i];
                case "--slug" -> onlySlug = args[++i];
                case "--limit" -> limit = Integer.parseInt(args[++i]);
                case "--delay" -> mutationDelayMs = Long.parseLong(args[++i]);
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

        token = firstNonBlank(System.getenv("GITHUB_TOKEN"), System.getenv("GH_TOKEN"));
        if (token == null && !dryRun) {
            System.err.println("""
                    No GITHUB_TOKEN (or GH_TOKEN) in the environment.

                    This script posts as whoever owns the token, so it must be the foojay.io
                    account's -- see the class comment. Use --dry-run to check the mapping
                    without a token.""");
            System.exit(2);
        }

        if (printConfig) {
            printGiscusConfig();
            return;
        }

        // ---- 1. what does WordPress have --------------------------------
        List<WpComment> comments = fetchWpComments();
        System.out.printf("WordPress: %d approved comments%n", comments.size());

        // ---- 2. which local post does each belong to --------------------
        PostIndex posts = indexLocalPosts();
        System.out.printf("Local content: %d post bundles%n", posts.titles().size());

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
        System.out.printf("To import: %d comments across %d posts%n",
                byTerm.values().stream().mapToInt(List::size).sum(), byTerm.size());

        if (dryRun && token == null) {
            System.out.println();
            byTerm.forEach((term, list) ->
                    System.out.printf("  %-70s %2d comment(s)%n", term, list.size()));
            if (onlySlug != null) {
                System.out.println();
                for (WpComment c : importOrder(byTerm.get(onlySlug))) {
                    System.out.printf("--- comment %d, %s a reply%n", c.id(), c.parent() == 0 ? "not" : "as");
                    System.out.println(commentBody(c));
                    System.out.println();
                }
            }
            System.out.println("""
                    Dry run without a token: stopped after the mapping check. Nothing was read
                    from or written to GitHub. Re-run with GITHUB_TOKEN set (still --dry-run) to
                    also see which discussions and comments already exist there.""");
            return;
        }

        // ---- 3. what does GitHub already have ---------------------------
        String[] ownerName = splitRepo(repo);
        JsonNode repoInfo = resolveRepoAndCategory(ownerName[0], ownerName[1]);
        String repoId = repoInfo.get("repoId").asText();
        String categoryId = repoInfo.get("categoryId").asText();
        DiscussionIndex existing = fetchDiscussions(ownerName[0], ownerName[1], categoryId);
        System.out.printf("GitHub: %d existing discussion(s) in category \"%s\"%n", existing.size(), category);
        System.out.println();

        // ---- 4. import ---------------------------------------------------
        int createdDiscussions = 0, createdComments = 0, skipped = 0;
        boolean stoppedEarly = false;

        outer:
        for (Map.Entry<String, List<WpComment>> entry : byTerm.entrySet()) {
            String term = entry.getKey();
            List<WpComment> postComments = importOrder(entry.getValue());
            Map<Integer, WpComment> byId = byId(postComments);

            Discussion discussion = existing.find(term);
            boolean freshDiscussion = discussion == null;
            if (discussion == null && !dryRun) {
                if (creations >= limit) {
                    stoppedEarly = true;
                    break;
                }
                discussion = createDiscussion(repoId, categoryId, term,
                        posts.titles().getOrDefault(term, term));
                existing.add(term, discussion);
                createdDiscussions++;
                System.out.printf("+ discussion #%d  %s%n", discussion.number, term);
            } else if (discussion == null) {
                System.out.printf("+ discussion (would create)  %s%n", term);
                createdDiscussions++;
            } else {
                System.out.printf("= discussion #%d  %s%n", discussion.number, term);
            }

            // Which WP comments are already in there? A discussion we just created (or
            // would create) holds none, so don't spend a query asking.
            Map<Integer, String> importedIds = freshDiscussion
                    ? new HashMap<>()
                    : fetchImportedComments(discussion);

            // postComments is in import order: top-level first, so a reply always
            // finds its parent's GitHub node id already recorded.
            for (WpComment c : postComments) {
                if (importedIds.containsKey(c.id())) {
                    skipped++;
                    continue;
                }
                if (creations >= limit) {
                    stoppedEarly = true;
                    break outer;
                }
                String replyTo = c.parent() == 0 ? null : importedIds.get(rootAncestor(c, byId));
                if (dryRun) {
                    // Reply-ness from the WP parent, not from replyTo: nothing was
                    // posted, so no parent node id exists to resolve against.
                    System.out.printf("    + comment %d by %s%s (would post)%n",
                            c.id(), c.author(), c.parent() == 0 ? "" : " (reply)");
                    // With --slug there's only one post in play, so showing the exact
                    // body that would be posted is cheap and worth a look before a
                    // one-way write to a public discussion.
                    if (onlySlug != null) {
                        System.out.println(commentBody(c).replaceAll("(?m)^", "      | "));
                    }
                    createdComments++;
                    continue;
                }
                String nodeId = addComment(discussion.id, replyTo, commentBody(c));
                importedIds.put(c.id(), nodeId);
                createdComments++;
                System.out.printf("    + comment %d by %s%s%n",
                        c.id(), c.author(), replyTo == null ? "" : " (reply)");
            }
        }

        System.out.println();
        System.out.printf("%s discussions %s: %d, comments %s: %d, comments already present: %d%n",
                dryRun ? "Dry run --" : "Done.",
                dryRun ? "to create" : "created", createdDiscussions,
                dryRun ? "to post" : "posted", createdComments,
                skipped);
        if (stoppedEarly) {
            System.out.println("""
                    Stopped at --limit. Re-run the same command to continue -- already-imported
                    comments are detected and skipped.""");
        }
        if (dryRun) {
            System.out.println("Nothing was written. Drop --dry-run to perform the import.");
        }
    }

    static void usage() {
        System.out.println("""
                Imports the legacy WordPress comments on foojay.io into GitHub Discussions,
                in the shape giscus expects (see comments.html).

                  jbang scripts/ImportWpComments.java [options]

                  --dry-run            report what would happen, write nothing
                  --print-config       resolve repoId/categoryId and print the hugo.toml block
                  --repo <owner/name>  default: %s
                  --category <name>    discussion category, default: "%s"
                  --slug <post-slug>   import only this post's comments
                  --limit <n>          stop after n creations (re-run to continue)
                  --delay <ms>         pause between writes, default %d

                GITHUB_TOKEN (or GH_TOKEN) must hold the foojay.io account's token with
                Discussions write access.""".formatted(DEFAULT_REPO, DEFAULT_CATEGORY, DEFAULT_MUTATION_DELAY_MS));
    }

    // ---- WordPress ----------------------------------------------------

    /**
     * Pulls every approved comment through WP's open REST API. Unauthenticated
     * reads return approved comments of type "comment" only, so spam, pending
     * moderation and pingbacks never reach us.
     */
    static List<WpComment> fetchWpComments() throws IOException, InterruptedException {
        List<WpComment> out = new ArrayList<>();
        int page = 1, totalPages = 1;
        do {
            String url = WP_BASE + "/wp-json/wp/v2/comments"
                    + "?per_page=" + WP_PAGE_SIZE + "&page=" + page
                    + "&order=asc&orderby=date"
                    + "&_fields=id,post,parent,author_name,author_url,date,content,link";
            HttpResponse<String> response = HTTP.send(
                    HttpRequest.newBuilder(URI.create(url))
                            .timeout(Duration.ofMillis(REQUEST_TIMEOUT_MS))
                            .header("Accept", "application/json")
                            .header("User-Agent", "foojay-hugo-migration-bot/1.0")
                            .GET().build(),
                    HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IOException("HTTP " + response.statusCode() + " fetching " + url);
            }
            if (page == 1) {
                totalPages = response.headers().firstValue("x-wp-totalpages")
                        .map(Integer::parseInt).orElse(1);
            }
            for (JsonNode node : JSON.readTree(response.body())) {
                WpComment c = toComment(node);
                if (c != null) out.add(c);
            }
            page++;
        } while (page <= totalPages);
        return out;
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
     * Maps every URL slug a post has ever been served under to its giscus term
     * (the value hugo.toml's `:slugorcontentbasename` resolves to, i.e. the
     * `slug` frontmatter if set and otherwise the bundle folder name). Legacy
     * `aliases:` paths map to the same term, so a post whose folder was renamed
     * (SanitizeSlugs.java) is still found from its WordPress URL.
     */
    static PostIndex indexLocalPosts() throws IOException {
        Map<String, String> slugToTerm = new HashMap<>();
        Map<String, String> titles = new HashMap<>();
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
            String title = folder;
            List<String> aliases = new ArrayList<>();
            boolean inAliases = false;
            for (String line : frontmatterLines(index)) {
                Matcher slug = SLUG_FRONTMATTER.matcher(line);
                if (slug.matches()) {
                    term = slug.group(1);
                    continue;
                }
                if (line.startsWith("title:")) {
                    title = line.substring("title:".length()).trim().replaceAll("^\"|\"$", "");
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
            titles.put(term, title);
        }
        return new PostIndex(slugToTerm, titles);
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
     * same sanitization SanitizeSlugs.java applies.
     */
    static String resolveTerm(String wpSlug, Map<String, String> index) {
        String direct = index.get(wpSlug);
        if (direct != null) return direct;
        return index.get(sanitize(wpSlug));
    }

    /** Kept in step with SanitizeSlugs.sanitize(). */
    static String sanitize(String s) {
        return s.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9_-]+", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("^[-_]+|[-_]+$", "");
    }

    // ---- comment bodies -----------------------------------------------

    /**
     * The attribution line the TODO asked for, then the comment itself as
     * Markdown, then the id marker that makes a re-run idempotent. The name is
     * linked when the commenter left a URL.
     */
    static String commentBody(WpComment c) {
        String author = c.author().isBlank() ? "an anonymous reader" : c.author();
        String name = c.authorUrl().isBlank()
                ? "**" + author + "**"
                : "[**" + author + "**](" + c.authorUrl() + ")";
        String attribution = "_Originally posted by " + name + " on "
                + HUMAN_DATE.format(c.date()) + " in Foojay.io Discussions._";
        return attribution + "\n\n" + toMarkdown(c.html()) + "\n\n"
                + "<!-- wp-comment-id: " + c.id() + " -->";
    }

    /**
     * Shares the converter the post bodies went through (HtmlToMarkdown), so a
     * code sample or an over-escaped entity in a comment gets the same repairs.
     * Comment HTML is simple -- p/br/a/strong/em/code/pre/blockquote and, across
     * all 580, not a single image or embed -- so none of the converter's
     * shortcode paths can fire here.
     */
    static String toMarkdown(String html) {
        return HtmlToMarkdown.toMarkdown(Jsoup.parseBodyFragment(html).body()).trim();
    }

    /**
     * Import order: oldest first, and every top-level comment before any reply,
     * so a reply always finds its parent's GitHub node id already recorded.
     */
    static List<WpComment> importOrder(List<WpComment> postComments) {
        List<WpComment> sorted = new ArrayList<>(postComments);
        sorted.sort(Comparator.comparing(WpComment::date).thenComparingInt(WpComment::id));
        Map<Integer, WpComment> byId = byId(sorted);
        List<WpComment> topLevel = new ArrayList<>();
        List<WpComment> replies = new ArrayList<>();
        for (WpComment c : sorted) {
            // An unpublished parent leaves its child dangling; import it top-level.
            if (c.parent() != 0 && byId.containsKey(c.parent())) replies.add(c);
            else topLevel.add(c);
        }
        return concat(topLevel, replies);
    }

    static Map<Integer, WpComment> byId(List<WpComment> comments) {
        Map<Integer, WpComment> byId = new HashMap<>();
        comments.forEach(c -> byId.put(c.id(), c));
        return byId;
    }

    /** The top-level ancestor's WP id: GitHub Discussions nest one level, WordPress nests deeper. */
    static int rootAncestor(WpComment c, Map<Integer, WpComment> byId) {
        WpComment current = c;
        for (int guard = 0; guard < 20; guard++) {
            WpComment parent = byId.get(current.parent());
            if (parent == null) return current.id();
            if (parent.parent() == 0) return parent.id();
            current = parent;
        }
        return current.id();
    }

    // ---- GitHub -------------------------------------------------------

    record Discussion(String id, int number, String title) {
    }

    static void printGiscusConfig() throws Exception {
        if (token == null) {
            System.err.println("--print-config needs GITHUB_TOKEN (or GH_TOKEN) to read the repository ids.");
            System.exit(2);
        }
        String[] ownerName = splitRepo(repo);
        JsonNode info = resolveRepoAndCategory(ownerName[0], ownerName[1]);
        System.out.printf("""
                Paste into hugo.toml:

                [params.giscus]
                  repo = "%s"
                  repoId = "%s"
                  category = "%s"
                  categoryId = "%s"
                %n""", repo, info.get("repoId").asText(), category, info.get("categoryId").asText());
    }

    static JsonNode resolveRepoAndCategory(String owner, String name) throws Exception {
        String query = """
                query($owner:String!, $name:String!) {
                  repository(owner:$owner, name:$name) {
                    id
                    hasDiscussionsEnabled
                    discussionCategories(first:50) { nodes { id name } }
                  }
                }""";
        JsonNode data = graphql(query, Map.of("owner", owner, "name", name), false);
        JsonNode repository = data.path("repository");
        if (repository.isMissingNode() || repository.isNull()) {
            throw new IOException("Repository " + repo + " not found, or the token can't see it");
        }
        if (!repository.path("hasDiscussionsEnabled").asBoolean(true)) {
            throw new IOException("Discussions are not enabled on " + repo
                    + " -- Settings -> General -> Features -> Discussions");
        }
        String categoryId = null;
        List<String> available = new ArrayList<>();
        for (JsonNode node : repository.path("discussionCategories").path("nodes")) {
            available.add(node.path("name").asText());
            if (category.equalsIgnoreCase(node.path("name").asText())) {
                categoryId = node.path("id").asText();
            }
        }
        if (categoryId == null) {
            throw new IOException("No discussion category named \"" + category + "\" on " + repo
                    + ". Available: " + String.join(", ", available));
        }
        ObjectNode out = JSON.createObjectNode();
        out.put("repoId", repository.path("id").asText());
        out.put("categoryId", categoryId);
        return out;
    }

    /**
     * The discussions already in the category, looked up the way giscus looks
     * them up: by the sha1 of the term (strict mode's marker) first, falling
     * back to an exact title match for a thread created without one.
     */
    static final class DiscussionIndex {
        final Map<String, Discussion> byHash = new HashMap<>();
        final Map<String, Discussion> byTitle = new HashMap<>();

        Discussion find(String term) {
            Discussion d = byHash.get(sha1(term));
            return d != null ? d : byTitle.get(term);
        }

        void add(String term, Discussion d) {
            byHash.put(sha1(term), d);
            byTitle.putIfAbsent(term, d);
        }

        int size() {
            Set<String> ids = new HashSet<>();
            byHash.values().forEach(d -> ids.add(d.id()));
            byTitle.values().forEach(d -> ids.add(d.id()));
            return ids.size();
        }
    }

    static DiscussionIndex fetchDiscussions(String owner, String name, String categoryId) throws Exception {
        String query = """
                query($owner:String!, $name:String!, $categoryId:ID!, $after:String) {
                  repository(owner:$owner, name:$name) {
                    discussions(first:100, categoryId:$categoryId, after:$after) {
                      pageInfo { hasNextPage endCursor }
                      nodes { id number title body }
                    }
                  }
                }""";
        DiscussionIndex index = new DiscussionIndex();
        String after = null;
        do {
            Map<String, Object> vars = new HashMap<>();
            vars.put("owner", owner);
            vars.put("name", name);
            vars.put("categoryId", categoryId);
            vars.put("after", after);
            JsonNode discussions = graphql(query, vars, false).path("repository").path("discussions");
            for (JsonNode node : discussions.path("nodes")) {
                Discussion d = new Discussion(node.path("id").asText(),
                        node.path("number").asInt(), node.path("title").asText());
                index.byTitle.putIfAbsent(d.title(), d);
                String body = node.path("body").asText("");
                int at = body.indexOf(SHA1_MARKER_PREFIX);
                if (at >= 0) {
                    String hash = body.substring(at + SHA1_MARKER_PREFIX.length()).split("\\s")[0];
                    index.byHash.putIfAbsent(hash, d);
                }
            }
            after = discussions.path("pageInfo").path("hasNextPage").asBoolean()
                    ? discussions.path("pageInfo").path("endCursor").asText() : null;
        } while (after != null);
        return index;
    }

    /** WP comment id -> GitHub comment node id, for everything already imported into this discussion. */
    static Map<Integer, String> fetchImportedComments(Discussion discussion) throws Exception {
        String query = """
                query($id:ID!, $after:String) {
                  node(id:$id) {
                    ... on Discussion {
                      comments(first:50, after:$after) {
                        pageInfo { hasNextPage endCursor }
                        nodes {
                          id body
                          replies(first:100) { nodes { id body } }
                        }
                      }
                    }
                  }
                }""";
        Map<Integer, String> imported = new HashMap<>();
        String after = null;
        do {
            Map<String, Object> vars = new HashMap<>();
            vars.put("id", discussion.id());
            vars.put("after", after);
            JsonNode comments = graphql(query, vars, false).path("node").path("comments");
            for (JsonNode node : comments.path("nodes")) {
                recordMarker(imported, node);
                for (JsonNode reply : node.path("replies").path("nodes")) recordMarker(imported, reply);
            }
            after = comments.path("pageInfo").path("hasNextPage").asBoolean()
                    ? comments.path("pageInfo").path("endCursor").asText() : null;
        } while (after != null);
        return imported;
    }

    static void recordMarker(Map<Integer, String> imported, JsonNode comment) {
        Matcher m = WP_COMMENT_MARKER.matcher(comment.path("body").asText(""));
        if (m.find()) imported.put(Integer.parseInt(m.group(1)), comment.path("id").asText());
    }

    /**
     * Creates the thread exactly as giscus's own Widget would: title = term,
     * body = "# term", the post link, and the strict-mode sha1 marker. The extra
     * line about the import is only visible on GitHub -- giscus renders a
     * discussion's comments, never its body.
     */
    static Discussion createDiscussion(String repoId, String categoryId, String term, String title) throws Exception {
        String body = "# " + term + "\n\n"
                + title + "\n\n"
                + SITE_BASE + "/today/" + term + "/\n\n"
                + "_Comments below were imported from the foojay.io WordPress site "
                + "(scripts/ImportWpComments.java)._\n\n"
                + SHA1_MARKER_PREFIX + sha1(term) + " -->";
        String mutation = """
                mutation($repoId:ID!, $categoryId:ID!, $title:String!, $body:String!) {
                  createDiscussion(input:{repositoryId:$repoId, categoryId:$categoryId, title:$title, body:$body}) {
                    discussion { id number title }
                  }
                }""";
        JsonNode discussion = graphql(mutation,
                Map.of("repoId", repoId, "categoryId", categoryId, "title", term, "body", body),
                true).path("createDiscussion").path("discussion");
        return new Discussion(discussion.path("id").asText(),
                discussion.path("number").asInt(), discussion.path("title").asText());
    }

    /** Posts a comment, or a reply to one when replyTo is given. Returns its node id. */
    static String addComment(String discussionId, String replyTo, String body) throws Exception {
        String mutation = """
                mutation($discussionId:ID!, $replyTo:ID, $body:String!) {
                  addDiscussionComment(input:{discussionId:$discussionId, replyToId:$replyTo, body:$body}) {
                    comment { id }
                  }
                }""";
        Map<String, Object> vars = new HashMap<>();
        vars.put("discussionId", discussionId);
        vars.put("replyTo", replyTo);
        vars.put("body", body);
        return graphql(mutation, vars, true).path("addDiscussionComment").path("comment").path("id").asText();
    }

    /**
     * One GraphQL call, with the throttling and back-off the content-creation
     * limits require. Reads are cheap and go straight through; mutations are
     * spaced by --delay and retried with a growing pause when GitHub answers
     * with a secondary-rate-limit error.
     */
    static JsonNode graphql(String query, Map<String, Object> variables, boolean mutation) throws Exception {
        ObjectNode payload = JSON.createObjectNode();
        payload.put("query", query);
        payload.set("variables", JSON.valueToTree(variables));
        String body = JSON.writeValueAsString(payload);

        long backoffMs = 30_000;
        for (int attempt = 1; ; attempt++) {
            if (mutation) {
                long wait = mutationDelayMs - (System.currentTimeMillis() - lastMutationAt);
                if (wait > 0) Thread.sleep(wait);
                lastMutationAt = System.currentTimeMillis();
            }
            HttpResponse<String> response = HTTP.send(
                    HttpRequest.newBuilder(URI.create(GITHUB_GRAPHQL))
                            .timeout(Duration.ofMillis(REQUEST_TIMEOUT_MS))
                            .header("Authorization", "Bearer " + token)
                            .header("Accept", "application/json")
                            .header("User-Agent", "foojay-hugo-migration-bot/1.0")
                            .POST(HttpRequest.BodyPublishers.ofString(body))
                            .build(),
                    HttpResponse.BodyHandlers.ofString());

            boolean throttled = response.statusCode() == 403 || response.statusCode() == 429;
            JsonNode json = response.body().isBlank() ? JSON.createObjectNode() : JSON.readTree(response.body());
            String errors = errorMessages(json);
            if (!throttled && errors == null && response.statusCode() == 200) {
                if (mutation) creations++;
                return json.path("data");
            }
            if (errors != null && isRateLimit(errors)) throttled = true;

            if (!throttled || attempt > MAX_RETRIES) {
                throw new IOException("GitHub GraphQL HTTP " + response.statusCode()
                        + (errors == null ? ": " + response.body() : ": " + errors));
            }
            long retryAfter = response.headers().firstValue("retry-after")
                    .map(v -> Long.parseLong(v) * 1000).orElse(backoffMs);
            System.out.printf("Rate limited by GitHub, waiting %ds (attempt %d/%d)%n",
                    retryAfter / 1000, attempt, MAX_RETRIES);
            Thread.sleep(retryAfter);
            backoffMs *= 2;
        }
    }

    static String errorMessages(JsonNode json) {
        List<String> messages = new ArrayList<>();
        if (json.has("message")) messages.add(json.path("message").asText());
        for (JsonNode error : json.path("errors")) messages.add(error.path("message").asText());
        return messages.isEmpty() ? null : String.join(". ", messages);
    }

    static boolean isRateLimit(String message) {
        String m = message.toLowerCase(Locale.ROOT);
        return m.contains("rate limit") || m.contains("too quickly") || m.contains("abuse")
                || m.contains("was submitted too") || m.contains("try again later");
    }

    // ---- misc ---------------------------------------------------------

    /** giscus's strict-mode term digest (lib/utils.ts digestMessage: SHA-1, lowercase hex). */
    static String sha1(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-1").digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(digest.length * 2);
            for (byte b : digest) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            throw new IllegalStateException("SHA-1 unavailable", e);
        }
    }

    static String[] splitRepo(String repoWithOwner) throws IOException {
        String[] parts = repoWithOwner.split("/");
        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new IOException("--repo must be owner/name, got: " + repoWithOwner);
        }
        return parts;
    }

    static <T> List<T> concat(List<T> a, List<T> b) {
        List<T> out = new ArrayList<>(a);
        out.addAll(b);
        return out;
    }

    static String firstNonBlank(String... values) {
        for (String v : values) if (v != null && !v.isBlank()) return v;
        return null;
    }
}
