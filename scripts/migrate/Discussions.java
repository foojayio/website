///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS com.fasterxml.jackson.core:jackson-databind:2.17.1
//DEPS org.jsoup:jsoup:1.17.2
//JAVA 17+

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.io.IOException;
import java.net.URI;
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
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * One-off: posts the archived WordPress comments (content/posts/** /comments.json)
 * into GitHub Discussions, in the shape giscus expects (title = post slug, strict
 * sha1 marker in the body), then the archive can be deleted from the repo.
 *
 *   jbang scripts/migrate/Discussions.java --dry-run   (reads GitHub when a token is set, writes nothing)
 *   jbang scripts/migrate/Discussions.java             (post up to --limit comments, then stop)
 *   jbang scripts/migrate/Discussions.java --slug some-post
 *
 * $FOOJAY_BOT_TOKEN (deliberately not GITHUB_TOKEN, so no other tool picks it up) must be the foojayio-bot account's, with Discussions read/write.
 *
 * SLOW ON PURPOSE. The first run was fast enough to get the account suspended
 * as spam, so every write waits --delay seconds (plus up to 33% jitter), a run
 * posts at most --limit comments, and the FIRST rate-limit or abuse answer ends
 * the run: retrying is what looks like abuse. Run it again later; nothing is
 * duplicated, because a comment is skipped when its discussion already holds the
 * `<!-- wp-comment-id: N -->` marker, and a discussion is reused when one exists
 * for the term. Those markers are the ones the first run wrote.
 */
public class Discussions {

    static final String DEFAULT_REPO = "foojayio/website";
    static final String DEFAULT_CATEGORY = "Blog Comments";
    static final String SITE_BASE = "https://foojay.io";
    static final String SHA1_MARKER_PREFIX = "<!-- sha1: ";
    static final Pattern WP_COMMENT_MARKER = Pattern.compile("<!--\\s*wp-comment-id:\\s*(\\d+)\\s*-->");
    static final DateTimeFormatter HUMAN_DATE = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);

    static final ObjectMapper JSON = new ObjectMapper();
    static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).build();

    static String repo = DEFAULT_REPO;
    static String category = DEFAULT_CATEGORY;
    static boolean dryRun = false;
    static String onlySlug = null;
    static int limit = 60;
    static long delayMs = 30_000;
    static String token;
    static int writes = 0;
    static long lastWriteAt = 0;

    /** A comment as archived; `parent` is the index of its top-level ancestor, or -1. */
    record Comment(int id, int depth, String author, String authorUrl, LocalDateTime date, String html) {
    }

    record Discussion(String id, int number, String title) {
    }

    /** Thrown on the first sign of throttling, to end the run instead of retrying. */
    static class RateLimited extends IOException {
        RateLimited(String message) {
            super(message);
        }
    }

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--dry-run" -> dryRun = true;
                case "--slug" -> onlySlug = args[++i];
                case "--limit" -> limit = Integer.parseInt(args[++i]);
                case "--delay" -> delayMs = Long.parseLong(args[++i]) * 1000;
                case "--repo" -> repo = args[++i];
                case "--category" -> category = args[++i];
                default -> {
                    System.err.println("Unknown argument: " + args[i]
                            + "\nOptions: --dry-run --slug <s> --limit <n> --delay <seconds> --repo <o/n> --category <c>");
                    System.exit(2);
                }
            }
        }
        token = Optional.ofNullable(System.getenv("FOOJAY_BOT_TOKEN"))
                .filter(s -> !s.isBlank()).orElse(null);
        if (token == null && !dryRun) {
            System.err.println("Set FOOJAY_BOT_TOKEN to the foojayio-bot token (or use --dry-run).");
            System.exit(2);
        }
        try {
            run();
        } catch (RateLimited e) {
            System.out.println("\nSTOPPED, GitHub pushed back: " + e.getMessage()
                    + "\nDo not retry right away. Wait at least an hour, then run again.");
            System.exit(3);
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getMessage());
            System.exit(1);
        }
    }

    static void run() throws Exception {
        Map<String, List<Comment>> byTerm = loadArchive();
        if (onlySlug != null) byTerm.keySet().removeIf(t -> !t.equals(onlySlug));
        int total = byTerm.values().stream().mapToInt(List::size).sum();
        System.out.printf("Archive: %d comment(s) on %d post(s)%n", total, byTerm.size());

        String[] ownerName = repo.split("/");
        String repoId = null, categoryId = null;
        Map<String, Discussion> existing = new HashMap<>();
        if (token == null) {
            System.out.println("No token: mapping check only, GitHub is not read.");
        } else {
            JsonNode info = repoAndCategory(ownerName[0], ownerName[1]);
            repoId = info.get("repoId").asText();
            categoryId = info.get("categoryId").asText();
            existing = fetchDiscussions(ownerName[0], ownerName[1], categoryId);
            System.out.printf("GitHub: %d discussion(s) in \"%s\"%n%n", existing.size(), category);
        }

        int posted = 0, present = 0, createdDiscussions = 0;
        outer:
        for (var entry : byTerm.entrySet()) {
            String term = entry.getKey();
            List<Comment> comments = entry.getValue();
            Discussion discussion = existing.get(sha1(term));
            Map<Integer, String> imported = discussion == null ? new HashMap<>() : fetchImported(discussion);
            if (discussion != null) Thread.sleep(500);
            List<Comment> todo = comments.stream().filter(c -> !imported.containsKey(c.id())).toList();
            present += comments.size() - todo.size();
            if (todo.isEmpty()) continue;

            if (dryRun) {
                System.out.printf("%-70s %d to post%s%n", term, todo.size(), discussion == null ? " (new discussion)" : "");
                posted += todo.size();
                if (discussion == null) createdDiscussions++;
                continue;
            }
            if (writes >= limit) break;
            if (discussion == null) {
                discussion = createDiscussion(repoId, categoryId, term);
                existing.put(sha1(term), discussion);
                createdDiscussions++;
                System.out.printf("+ discussion #%d  %s%n", discussion.number(), term);
            }
            // Archive order is threaded (a reply follows its parent), so a reply
            // always finds its top-level ancestor's node id already recorded.
            Map<Integer, Integer> rootOf = rootAncestors(comments);
            for (Comment c : todo) {
                if (writes >= limit) break outer;
                String replyTo = c.depth() == 0 ? null : imported.get(rootOf.get(c.id()));
                if (c.depth() > 0 && replyTo == null) {
                    System.out.printf("  ! comment %d: its parent is not on GitHub yet, skipped%n", c.id());
                    continue;
                }
                imported.put(c.id(), addComment(discussion.id(), replyTo, body(c)));
                posted++;
                System.out.printf("  + comment %d by %s%s%n", c.id(), c.author(), replyTo == null ? "" : " (reply)");
            }
        }
        System.out.printf("%n%s: %d comment(s), %d new discussion(s); already on GitHub: %d%n",
                dryRun ? "Would post" : "Posted", posted, createdDiscussions, present);
        if (!dryRun && posted + present < total) {
            System.out.printf("%d left. Run again later.%n", total - posted - present);
        }
    }

    // ---- archive ------------------------------------------------------

    static Map<String, List<Comment>> loadArchive() throws IOException {
        Path posts = Path.of("content/posts");
        if (!Files.isDirectory(posts)) throw new IOException("Run this from the repository root");
        Map<String, List<Comment>> out = new TreeMap<>();
        List<Path> files;
        try (Stream<Path> s = Files.walk(posts)) {
            files = s.filter(p -> p.getFileName().toString().equals("comments.json")).sorted().toList();
        }
        for (Path file : files) {
            Path dir = file.getParent();
            String term = slugOf(dir);
            List<Comment> list = new ArrayList<>();
            for (JsonNode n : JSON.readTree(file.toFile())) {
                list.add(new Comment(n.get("id").asInt(), n.get("depth").asInt(), n.path("author").asText("").trim(),
                        n.path("authorUrl").asText("").trim(), LocalDateTime.parse(n.get("date").asText()),
                        n.get("html").asText()));
            }
            out.put(term, list);
        }
        return out;
    }

    /** What comments-term.html resolves: the `slug:` frontmatter, else the bundle folder. */
    static String slugOf(Path dir) throws IOException {
        for (String name : List.of("index.md", "index.adoc")) {
            Path index = dir.resolve(name);
            if (!Files.exists(index)) continue;
            List<String> lines = Files.readAllLines(index);
            for (int i = 1; i < lines.size() && !lines.get(i).startsWith("---"); i++) {
                Matcher m = Pattern.compile("^slug:\\s*\"?([^\"\\s]+)\"?\\s*$").matcher(lines.get(i));
                if (m.matches()) return m.group(1);
            }
        }
        return dir.getFileName().toString();
    }

    /** GitHub nests one level, WordPress deeper: every reply goes under its top-level ancestor. */
    static Map<Integer, Integer> rootAncestors(List<Comment> comments) {
        Map<Integer, Integer> root = new HashMap<>();
        int current = -1;
        for (Comment c : comments) {
            if (c.depth() == 0) current = c.id();
            root.put(c.id(), current);
        }
        return root;
    }

    static String body(Comment c) {
        String author = c.author().isBlank() ? "an anonymous reader" : c.author();
        String name = c.authorUrl().isBlank() ? "**" + author + "**" : "[**" + author + "**](" + c.authorUrl() + ")";
        return "_Originally posted by " + name + " on " + HUMAN_DATE.format(c.date()) + " in Foojay.io Discussions._\n\n"
                + toMarkdown(c.html()) + "\n\n<!-- wp-comment-id: " + c.id() + " -->";
    }

    // ---- HTML -> Markdown (the archive only holds p/br/a/strong/em/code/pre/blockquote/lists) ----

    static String toMarkdown(String html) {
        StringBuilder sb = new StringBuilder();
        for (Node n : Jsoup.parseBodyFragment(html).body().childNodes()) block(n, sb);
        return sb.toString().replaceAll("\n{3,}", "\n\n").trim();
    }

    static void block(Node n, StringBuilder sb) {
        if (n instanceof TextNode t) {
            if (!t.isBlank()) sb.append(escape(t.text())).append("\n\n");
            return;
        }
        if (!(n instanceof Element e)) return;
        switch (e.tagName()) {
            case "pre" -> sb.append("```\n").append(e.wholeText().replaceAll("\n+$", "")).append("\n```\n\n");
            case "blockquote" -> {
                StringBuilder inner = new StringBuilder();
                e.childNodes().forEach(c -> block(c, inner));
                inner.toString().trim().lines().forEach(l -> sb.append("> ").append(l).append("\n"));
                sb.append("\n");
            }
            case "ul", "ol" -> {
                int i = 1;
                for (Element li : e.children()) {
                    sb.append(e.tagName().equals("ol") ? (i++) + ". " : "- ").append(inline(li).trim()).append("\n");
                }
                sb.append("\n");
            }
            default -> sb.append(inline(e).trim()).append("\n\n");
        }
    }

    static String inline(Element e) {
        StringBuilder sb = new StringBuilder();
        for (Node n : e.childNodes()) {
            if (n instanceof TextNode t) {
                sb.append(escape(t.text()));
            } else if (n instanceof Element c) {
                String inner = inline(c);
                switch (c.tagName()) {
                    case "br" -> sb.append("  \n");
                    case "strong", "b" -> sb.append("**").append(inner).append("**");
                    case "em", "i" -> sb.append("*").append(inner).append("*");
                    case "code" -> sb.append("`").append(c.text()).append("`");
                    case "a" -> sb.append("[").append(inner).append("](").append(c.attr("href")).append(")");
                    default -> sb.append(inner);
                }
            }
        }
        return sb.toString();
    }

    static String escape(String text) {
        return text.replaceAll("([\\\\`*_\\[\\]<>])", "\\\\$1");
    }

    // ---- GitHub -------------------------------------------------------

    static JsonNode repoAndCategory(String owner, String name) throws Exception {
        JsonNode r = graphql("""
                query($owner:String!, $name:String!) {
                  repository(owner:$owner, name:$name) {
                    id
                    discussionCategories(first:50) { nodes { id name } }
                  }
                }""", Map.of("owner", owner, "name", name), false).path("repository");
        if (r.isMissingNode() || r.isNull()) throw new IOException("Repository " + repo + " not found");
        for (JsonNode c : r.path("discussionCategories").path("nodes")) {
            if (category.equalsIgnoreCase(c.path("name").asText())) {
                ObjectNode out = JSON.createObjectNode();
                out.put("repoId", r.path("id").asText());
                out.put("categoryId", c.path("id").asText());
                return out;
            }
        }
        throw new IOException("No discussion category \"" + category + "\" on " + repo);
    }

    /** sha1-of-term -> discussion, as giscus strict mode finds them. */
    static Map<String, Discussion> fetchDiscussions(String owner, String name, String categoryId) throws Exception {
        String query = """
                query($owner:String!, $name:String!, $categoryId:ID!, $after:String) {
                  repository(owner:$owner, name:$name) {
                    discussions(first:100, categoryId:$categoryId, after:$after) {
                      pageInfo { hasNextPage endCursor }
                      nodes { id number title body }
                    }
                  }
                }""";
        Map<String, Discussion> out = new HashMap<>();
        String after = null;
        do {
            Map<String, Object> vars = new HashMap<>(Map.of("owner", owner, "name", name, "categoryId", categoryId));
            vars.put("after", after);
            JsonNode page = graphql(query, vars, false).path("repository").path("discussions");
            for (JsonNode n : page.path("nodes")) {
                Discussion d = new Discussion(n.path("id").asText(), n.path("number").asInt(), n.path("title").asText());
                String b = n.path("body").asText("");
                int at = b.indexOf(SHA1_MARKER_PREFIX);
                String hash = at >= 0 ? b.substring(at + SHA1_MARKER_PREFIX.length()).split("\\s")[0] : sha1(d.title());
                out.putIfAbsent(hash, d);
            }
            after = page.path("pageInfo").path("hasNextPage").asBoolean() ? page.path("pageInfo").path("endCursor").asText() : null;
        } while (after != null);
        return out;
    }

    /** WP comment id -> GitHub node id, for what the discussion already holds. */
    static Map<Integer, String> fetchImported(Discussion d) throws Exception {
        String query = """
                query($id:ID!, $after:String) {
                  node(id:$id) {
                    ... on Discussion {
                      comments(first:50, after:$after) {
                        pageInfo { hasNextPage endCursor }
                        nodes { id body replies(first:100) { nodes { id body } } }
                      }
                    }
                  }
                }""";
        Map<Integer, String> out = new HashMap<>();
        String after = null;
        do {
            Map<String, Object> vars = new HashMap<>();
            vars.put("id", d.id());
            vars.put("after", after);
            JsonNode page = graphql(query, vars, false).path("node").path("comments");
            for (JsonNode c : page.path("nodes")) {
                mark(out, c);
                for (JsonNode r : c.path("replies").path("nodes")) mark(out, r);
            }
            after = page.path("pageInfo").path("hasNextPage").asBoolean() ? page.path("pageInfo").path("endCursor").asText() : null;
        } while (after != null);
        return out;
    }

    static void mark(Map<Integer, String> out, JsonNode comment) {
        Matcher m = WP_COMMENT_MARKER.matcher(comment.path("body").asText(""));
        if (m.find()) out.put(Integer.parseInt(m.group(1)), comment.path("id").asText());
    }

    static Discussion createDiscussion(String repoId, String categoryId, String term) throws Exception {
        String text = "# " + term + "\n\n" + SITE_BASE + "/today/" + term + "/\n\n"
                + "_Comments below were imported from the previous foojay.io WordPress site._\n\n"
                + SHA1_MARKER_PREFIX + sha1(term) + " -->";
        JsonNode d = graphql("""
                mutation($repoId:ID!, $categoryId:ID!, $title:String!, $body:String!) {
                  createDiscussion(input:{repositoryId:$repoId, categoryId:$categoryId, title:$title, body:$body}) {
                    discussion { id number title }
                  }
                }""", Map.of("repoId", repoId, "categoryId", categoryId, "title", term, "body", text), true)
                .path("createDiscussion").path("discussion");
        return new Discussion(d.path("id").asText(), d.path("number").asInt(), d.path("title").asText());
    }

    static String addComment(String discussionId, String replyTo, String text) throws Exception {
        Map<String, Object> vars = new HashMap<>();
        vars.put("discussionId", discussionId);
        vars.put("replyTo", replyTo);
        vars.put("body", text);
        return graphql("""
                mutation($discussionId:ID!, $replyTo:ID, $body:String!) {
                  addDiscussionComment(input:{discussionId:$discussionId, replyToId:$replyTo, body:$body}) {
                    comment { id }
                  }
                }""", vars, true).path("addDiscussionComment").path("comment").path("id").asText();
    }

    /** Writes are paced; any throttling answer ends the run (see the class comment). */
    static JsonNode graphql(String query, Map<String, Object> variables, boolean mutation) throws Exception {
        if (mutation) {
            long jitter = ThreadLocalRandom.current().nextLong(delayMs / 3 + 1);
            long wait = delayMs + jitter - (System.currentTimeMillis() - lastWriteAt);
            if (lastWriteAt > 0 && wait > 0) Thread.sleep(wait);
            lastWriteAt = System.currentTimeMillis();
        }
        ObjectNode payload = JSON.createObjectNode();
        payload.put("query", query);
        payload.set("variables", JSON.valueToTree(variables));
        HttpResponse<String> response = HTTP.send(
                HttpRequest.newBuilder(URI.create("https://api.github.com/graphql"))
                        .timeout(Duration.ofSeconds(30))
                        .header("Authorization", "Bearer " + token)
                        .header("User-Agent", "foojay-discussions-import/2.0")
                        .POST(HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(payload)))
                        .build(),
                HttpResponse.BodyHandlers.ofString());
        JsonNode json = response.body().isBlank() ? JSON.createObjectNode() : JSON.readTree(response.body());
        List<String> messages = new ArrayList<>();
        if (json.has("message")) messages.add(json.path("message").asText());
        for (JsonNode e : json.path("errors")) messages.add(e.path("message").asText());
        String errors = String.join(". ", messages);
        String lower = errors.toLowerCase(Locale.ROOT);
        if (response.statusCode() == 403 || response.statusCode() == 429 || lower.contains("rate limit")
                || lower.contains("abuse") || lower.contains("too quickly") || lower.contains("try again later")
                || lower.contains("suspended") || lower.contains("spam")) {
            throw new RateLimited("HTTP " + response.statusCode() + " " + errors
                    + response.headers().firstValue("retry-after").map(v -> " (retry-after " + v + "s)").orElse(""));
        }
        if (response.statusCode() != 200 || !errors.isEmpty()) {
            throw new IOException("GitHub GraphQL HTTP " + response.statusCode() + ": " + errors);
        }
        if (mutation) writes++;
        return json.path("data");
    }

    static String sha1(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-1").digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : digest) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
