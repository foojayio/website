///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 17+

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Tells a post's authors that a reader started a discussion on it.
 *
 *   <slug> <discussion-url>
 *
 * giscus creates the discussion with the first comment and titles it with the
 * post slug, so that is all it takes to find the post and its `authors:`. The
 * authors are named in a Slack message ($SLACK_WEBHOOK_URL), never in a comment
 * on the discussion: giscus shows that thread under the article to every reader.
 * Secret unset, or `--dry-run`, and the message is printed instead.
 * Only posts are handled: other sections' terms are `<section>/<slug>`.
 */
public class DiscussionAuthors {

    record Author(String slug, String name) {}

    static final HttpClient HTTP = HttpClient.newHttpClient();

    public static void main(String[] args) throws Exception {
        List<String> a = new ArrayList<>(List.of(args));
        boolean dryRun = a.remove("--dry-run");
        if (a.size() < 2) {
            System.err.println("usage: DiscussionAuthors.java [--dry-run] <slug> <discussion-url>");
            System.exit(2);
        }
        String slug = a.get(0), url = a.get(1);

        if (slug.contains("/")) {
            System.out.println("'" + slug + "' is not a post; nothing to do.");
            return;
        }
        Path post = findPost(slug);
        if (post == null) {
            System.out.println("No post with slug '" + slug + "'; nothing to do.");
            return;
        }
        List<Author> authors = new ArrayList<>();
        for (String s : frontmatterList(post, "authors")) authors.add(readAuthor(s));
        if (authors.isEmpty()) {
            System.out.println(post + " lists no authors.");
            return;
        }

        String names = String.join(", ", authors.stream().map(Author::name).toList());
        slack("*New discussion on " + slug + "*\n" + url + "\nAuthor(s): " + names, dryRun);
    }

    static void slack(String text, boolean dryRun) throws Exception {
        String webhook = System.getenv("SLACK_WEBHOOK_URL");
        if (dryRun || webhook == null || webhook.isBlank()) {
            System.out.println("Would post to Slack: " + text);
            return;
        }
        HttpResponse<String> r = HTTP.send(HttpRequest.newBuilder(URI.create(webhook))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"text\":" + quote(text) + "}")).build(),
                HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() / 100 != 2) throw new IOException("Slack answered " + r.statusCode() + ": " + r.body());
        System.out.println("Posted to Slack.");
    }

    static Path findPost(String slug) throws IOException {
        try (Stream<Path> dirs = Files.walk(Path.of("content/posts"), 5)) {
            return dirs.filter(d -> d.getFileName().toString().equals(slug) && Files.isDirectory(d))
                    .flatMap(d -> Stream.of(d.resolve("index.md"), d.resolve("index.adoc")))
                    .filter(Files::exists).findFirst().orElse(null);
        }
    }

    static Author readAuthor(String slug) throws IOException {
        Path file = Path.of("content/authors", slug, "_index.md");
        if (!Files.exists(file)) return new Author(slug, slug);
        String name = frontmatterValue(file, "title");
        return new Author(slug, name.isEmpty() ? slug : name);
    }

    /** Frontmatter lines only: stops at the closing `---`, since bodies can repeat a key at column 0. */
    static List<String> frontmatter(Path file) throws IOException {
        List<String> out = new ArrayList<>();
        List<String> lines = Files.readAllLines(file);
        for (int i = 1; i < lines.size() && !lines.get(i).equals("---"); i++) out.add(lines.get(i));
        return out;
    }

    static String frontmatterValue(Path file, String key) throws IOException {
        for (String l : frontmatter(file)) {
            if (l.startsWith(key + ":")) return unquote(l.substring(key.length() + 1));
        }
        return "";
    }

    static List<String> frontmatterList(Path file, String key) throws IOException {
        List<String> lines = frontmatter(file), out = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            if (!lines.get(i).startsWith(key + ":")) continue;
            String rest = lines.get(i).substring(key.length() + 1).trim();
            if (rest.startsWith("[")) {
                for (String s : rest.replaceAll("^\\[|\\]$", "").split(",")) {
                    if (!unquote(s).isEmpty()) out.add(unquote(s));
                }
            } else {
                for (int j = i + 1; j < lines.size() && lines.get(j).trim().startsWith("-"); j++) {
                    out.add(unquote(lines.get(j).trim().substring(1)));
                }
            }
        }
        return out;
    }

    static String unquote(String s) {
        s = s.trim();
        if (s.length() >= 2 && (s.startsWith("\"") && s.endsWith("\"") || s.startsWith("'") && s.endsWith("'"))) {
            s = s.substring(1, s.length() - 1);
        }
        return s.trim();
    }

    static String quote(String s) {
        StringBuilder b = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"' -> b.append("\\\"");
                case '\\' -> b.append("\\\\");
                case '\n' -> b.append("\\n");
                case '\r' -> b.append("\\r");
                case '\t' -> b.append("\\t");
                default -> b.append(c < 0x20 ? String.format("\\u%04x", (int) c) : String.valueOf(c));
            }
        }
        return b.append('"').toString();
    }
}
