///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 17+
//DEPS org.jsoup:jsoup:1.18.3
//DEPS com.fasterxml.jackson.core:jackson-databind:2.18.2

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.w3c.dom.NodeList;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Writes a Mastodon, Bluesky and LinkedIn text for every post that went live,
 * with Claude working from the article itself. Mastodon and Bluesky are posted
 * directly; LinkedIn has no usable API, so its text goes to Slack in a code
 * block plus a link that opens LinkedIn's composer prefilled, with links to what was posted. A platform that fails or has no
 * credentials falls back to its own text in that Slack message.
 *
 *   new <previous.xml> <current.xml>   posts in current that previous lacks
 *   post <feed.xml> <url>              one post by its URL, to retry after a failure;
 *                                      anything already posted is posted again
 *
 * Same contract as SlackPosts.java: the previous feed is from BEFORE the
 * deploy, so no state is kept, and an empty previous feed aborts instead of
 * writing texts for the whole feed.
 *
 * Environment:
 *   ANTHROPIC_API_KEY      writes the texts
 *   BLUESKY_HANDLE         the account to post as, e.g. foojay.io
 *   BLUESKY_APP_PASSWORD   an app password for it, not the login password
 *   MASTODON_TOKEN         an access token with write:statuses
 *   MASTODON_INSTANCE      defaults to https://foojay.social
 *   SLACK_BOT_TOKEN        a bot with chat:write
 *   SLACK_CHANNEL_ID       the channel the message is posted in; the bot must be a member
 *                          of a private one. Without it the message goes to SLACK_MENTION_ID
 *                          as a direct message.
 *   SLACK_MENTION_ID       a Slack member ID, the fallback when there is no channel
 *
 * Anything unset is printed instead of sent; `--dry-run` posts nothing at all.
 */
public class SocialPosts {

    record Post(String title, String link, String description) {}

    record Author(String name, String bluesky, String mastodon) {}

    record Platform(String id, String label, int max, String extra) {}

    static final ObjectMapper JSON = new ObjectMapper();
    static final HttpClient HTTP = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
    static final int BLUESKY_IMAGE_LIMIT = 900_000;
    static final int MAX_NEW = 5;
    static final int MAX_ARTICLE_CHARS = 8000;
    static final String MODEL = "claude-sonnet-5-5";

    static final List<Platform> PLATFORMS = List.of(
            new Platform("mastodon", "Mastodon", 500,
                    "Add line breaks to separate text, link, etc.\nWhen you mention Foojay, add the emoji `:foojay:`"),
            new Platform("bluesky", "Bluesky", 300, ""),
            new Platform("linkedin", "LinkedIn", 1000,
                    "Add line breaks to separate text, link, etc.\n"
                            + "Mention each author as `@Full Name` exactly as provided (the full name is needed to turn it into a LinkedIn mention later). "
                            + "Don't add a link to the LinkedIn profile(s) of the author(s).\n"
                            + "Use plain text only: no bold, no italic, no markdown (no `**`, no `*`, no `_`) and no unicode bold/italic characters.\n"
                            + "Don't open with a hook or rhetorical question like `If you've ever wondered...`, `Ever wondered...`, "
                            + "`Have you ever...` or `Imagine...`. Start directly with what the article is about, in a plain and "
                            + "factual tone, like a colleague sharing a link."));

    public static void main(String[] args) throws Exception {
        List<String> a = new ArrayList<>(List.of(args));
        boolean dryRun = a.remove("--dry-run");
        boolean one = a.size() == 3 && a.get(0).equals("post");
        if (a.size() != 3 || !(one || a.get(0).equals("new"))) {
            System.err.println("usage: SocialPosts.java [--dry-run] new <previous.xml> <current.xml> | post <feed.xml> <url>");
            System.exit(2);
        }
        String claude = env("ANTHROPIC_API_KEY");
        String token = env("SLACK_BOT_TOKEN");
        String user = env("SLACK_CHANNEL_ID") != null ? env("SLACK_CHANNEL_ID")
                : env("SLACK_MENTION_ID") == null ? null : env("SLACK_MENTION_ID").replaceAll("^<@|>$", "");
        if (!dryRun && claude == null) {
            System.out.println("ANTHROPIC_API_KEY is not set; printing instead of posting.");
            dryRun = true;
        }

        List<Post> fresh;
        if (one) {
            String url = a.get(2).trim().replaceAll("/+$", "") + "/";
            fresh = readFeed(a.get(1)).stream().filter(p -> p.link().equals(url)).toList();
            if (fresh.isEmpty()) {
                System.err.println(url + " is not in the live feed, so it is not published yet or the URL is wrong.");
                System.exit(1);
            }
        } else {
            List<Post> previous = readFeed(a.get(1));
            if (previous.isEmpty()) {
                System.out.println("Previous feed has no posts; skipping rather than writing texts for everything.");
                return;
            }
            Set<String> known = new LinkedHashSet<>();
            previous.forEach(p -> known.add(p.link()));
            fresh = new ArrayList<>(readFeed(a.get(2)).stream().filter(p -> !known.contains(p.link())).toList());
        }
        if (fresh.isEmpty()) {
            System.out.println("No new posts.");
            return;
        }
        if (fresh.size() > MAX_NEW) {
            System.out.printf("%d new posts; writing texts for %d.%n", fresh.size(), MAX_NEW);
            fresh = fresh.subList(0, MAX_NEW);
        }

        int failures = 0;
        for (Post p : fresh) {
            try {
                send(p, claude, token, user, dryRun);
            } catch (Exception e) {
                failures++;
                System.err.println("Failed for " + p.link() + ": " + e.getMessage());
                System.out.println("::warning title=Social post failed::" + p.link() + ": " + e.getMessage());
                if (!dryRun && token != null && user != null) {
                    try {
                        post(token, user, "*New on Foojay.io:* <" + p.link() + "|" + esc(p.title()) + ">\nSocial texts failed: " + esc(String.valueOf(e.getMessage())) + retryHint());
                    } catch (Exception ignored) {
                        // the warning above is all that is left
                    }
                }
            }
        }
        if (failures > 0) System.exit(1);
    }

    static void send(Post p, String claudeKey, String token, String user, boolean dryRun) throws Exception {
        Document page = Jsoup.connect(p.link()).userAgent("foojay.io").timeout(15000).get();
        List<Author> authors = authors(page);
        String article = articleText(page, p);

        StringBuilder message = new StringBuilder("*New on Foojay.io:* <" + p.link() + "|" + esc(p.title()) + ">\n");
        if (!authors.isEmpty()) {
            message.append("by ").append(esc(String.join(", ", authors.stream().map(Author::name).toList()))).append("\n");
        }
        int failed = 0;
        boolean retry = false;
        for (Platform pl : PLATFORMS) {
            String text, posted = null, problem = "";
            try {
                text = claudeKey == null ? "(not generated: ANTHROPIC_API_KEY is not set)"
                        : write(claudeKey, pl, p, authors, article);
            } catch (Exception e) {
                // One platform's text must not cost the other platforms or the Slack message.
                failed++;
                retry = true;
                System.err.println(pl.label() + " text failed for " + p.link() + ": " + e.getMessage());
                message.append("\n*").append(pl.label()).append("* - no text written, ").append(esc(String.valueOf(e.getMessage()))).append("\n");
                continue;
            }
            if (!dryRun && !pl.id().equals("linkedin")) {
                try {
                    posted = pl.id().equals("bluesky") ? postBluesky(text, p, page) : postMastodon(text, p);
                    if (posted == null) problem = " - not posted, no credentials set";
                } catch (Exception e) {
                    problem = " - not posted, " + esc(String.valueOf(e.getMessage()));
                    retry = true;
                    System.err.println(pl.label() + " failed for " + p.link() + ": " + e.getMessage());
                }
            }
            if (posted != null) {
                message.append("\n*").append(pl.label()).append("* posted: ").append(posted).append("\n");
            } else {
                message.append("\n*").append(pl.label()).append("* (").append(length(pl, text)).append("/").append(pl.max())
                        .append(")").append(problem);
                if (pl.id().equals("linkedin")) {
                    // The page's own composer, so it posts as the page; the @mentions still have to be picked by hand.
                    message.append(" <https://www.linkedin.com/company/98327419/admin/page-posts/published/?share=true&text=")
                            .append(URLEncoder.encode(text, StandardCharsets.UTF_8).replace("+", "%20")).append("|Open in LinkedIn>");
                }
                message.append("\n```\n").append(text.replace("```", "'''")).append("\n```\n");
            }
        }

        if (retry) message.append(retryHint());
        System.out.println("=== " + p.link() + "\n" + message);
        if (dryRun) return;
        if (token == null || user == null) {
            System.out.println("SLACK_BOT_TOKEN or SLACK_CHANNEL_ID is not set; not sent to Slack.");
            return;
        }
        post(token, user, message.toString());
        System.out.println("Sent to Slack.");
        if (failed > 0) throw new IllegalStateException(failed + " platform text(s) failed for " + p.link());
    }

    /** Points at the manual workflow (social-post.yml) that reruns one post. */
    static String retryHint() {
        String repo = env("GITHUB_REPOSITORY") == null ? "foojayio/website" : env("GITHUB_REPOSITORY");
        return "\nTo retry, run the manual GitHub action \"Retry social posts\" via <https://github.com/" + repo
                + "/actions/workflows/social-post.yml|this link> and enter the post URL.";
    }

    /** Returns the URL of the new post, or null when there are no credentials. */
    static String postMastodon(String text, Post p) throws Exception {
        String token = env("MASTODON_TOKEN");
        if (token == null) return null;
        String instance = env("MASTODON_INSTANCE") == null ? "https://foojay.social" : env("MASTODON_INSTANCE").replaceAll("/$", "");
        ObjectNode b = JSON.createObjectNode();
        b.put("status", text);
        b.put("visibility", "public");
        b.put("language", "en");
        // Mastodon builds the preview card from the page's og: tags by itself.
        // The key makes a re-run of the same deploy return the first post, not a second.
        HttpResponse<String> r = HTTP.send(
                HttpRequest.newBuilder(URI.create(instance + "/api/v1/statuses")).timeout(Duration.ofSeconds(30))
                        .header("Authorization", "Bearer " + token)
                        .header("Idempotency-Key", "foojay-" + p.link())
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(b))).build(),
                HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() != 200) throw new IllegalStateException("Mastodon answered " + r.statusCode() + ": " + r.body());
        return JSON.readTree(r.body()).path("url").asText();
    }

    /**
     * Bluesky does not recognise links, mentions or hashtags in text: each needs
     * a facet with UTF-8 byte offsets, and the link card and its thumbnail are
     * ours to build from the page's og: tags. Returns the post's URL, or null
     * when there are no credentials.
     */
    static String postBluesky(String text, Post p, Document page) throws Exception {
        String handle = env("BLUESKY_HANDLE"), password = env("BLUESKY_APP_PASSWORD");
        if (handle == null || password == null) return null;
        String pds = "https://bsky.social/xrpc/";

        ObjectNode login = JSON.createObjectNode();
        login.put("identifier", handle);
        login.put("password", password);
        JsonNode session = JSON.readTree(bsky(pds + "com.atproto.server.createSession", null, "application/json",
                HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(login))));
        String jwt = session.path("accessJwt").asText();

        List<String[]> links = new ArrayList<>();
        String out = shortenLinks(text, links);

        var facets = JSON.createArrayNode();
        for (String[] l : links) {
            int at = out.indexOf(l[0]);
            if (at >= 0) facet(facets, out, at, at + l[0].length(), "link").put("uri", l[1]);
        }
        Matcher mention = Pattern.compile("(?<![\\w/])@([a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)+)").matcher(out);
        while (mention.find()) {
            String did = resolveHandle(mention.group(1));
            if (did != null) facet(facets, out, mention.start(), mention.end(), "mention").put("did", did);
        }
        Matcher tag = Pattern.compile("(?<![\\w/#&])#([\\p{L}\\p{N}_]*\\p{L}[\\p{L}\\p{N}_]*)").matcher(out);
        while (tag.find()) facet(facets, out, tag.start(), tag.end(), "tag").put("tag", tag.group(1));

        ObjectNode record = JSON.createObjectNode();
        record.put("$type", "app.bsky.feed.post");
        record.put("text", out);
        record.put("createdAt", Instant.now().toString());
        record.putArray("langs").add("en");
        record.set("facets", facets);
        ObjectNode card = record.putObject("embed");
        card.put("$type", "app.bsky.embed.external");
        ObjectNode ext = card.putObject("external");
        ext.put("uri", p.link());
        ext.put("title", meta(page, "og:title", p.title()));
        ext.put("description", meta(page, "og:description", p.description()));
        String image = page.select("meta[property=og:image]").attr("abs:content");
        if (!image.isEmpty()) {
            try {
                byte[] thumb = thumbnail(image);
                if (thumb != null) {
                    ext.set("thumb", JSON.readTree(bsky(pds + "com.atproto.repo.uploadBlob", jwt, "image/jpeg",
                            HttpRequest.BodyPublishers.ofByteArray(thumb))).path("blob"));
                }
            } catch (Exception e) {
                System.err.println("Bluesky thumbnail skipped for " + p.link() + ": " + e.getMessage());
            }
        }

        ObjectNode body = JSON.createObjectNode();
        body.put("repo", session.path("did").asText());
        body.put("collection", "app.bsky.feed.post");
        body.set("record", record);
        String uri = JSON.readTree(bsky(pds + "com.atproto.repo.createRecord", jwt, "application/json",
                HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(body)))).path("uri").asText();
        return "https://bsky.app/profile/" + session.path("handle").asText(handle) + "/post/" + uri.substring(uri.lastIndexOf('/') + 1);
    }

    /** Bluesky shows a short form of each link, so a full URL does not eat the 300 characters. */
    static String shortenLinks(String text, List<String[]> links) {
        Matcher m = Pattern.compile("https?://\\S+").matcher(text);
        StringBuilder shown = new StringBuilder();
        while (m.find()) {
            String url = m.group().replaceAll("[.,;:!?)]+$", "");
            String display = url.replaceFirst("^https?://(www\\.)?", "");
            if (display.length() > 25) display = display.substring(0, 22) + "...";
            links.add(new String[]{display, url});
            m.appendReplacement(shown, Matcher.quoteReplacement(display + m.group().substring(url.length())));
        }
        m.appendTail(shown);
        return shown.toString();
    }

    /** What the platform counts against its limit: Bluesky the shortened links, Mastodon 23 per link. */
    static int length(Platform pl, String text) {
        String counted = switch (pl.id()) {
            case "bluesky" -> shortenLinks(text, new ArrayList<>());
            case "mastodon" -> text.replaceAll("https?://\\S+", "x".repeat(23));
            default -> text;
        };
        return counted.codePointCount(0, counted.length());
    }

    /** Adds a facet over [from, to) and returns its feature, for the caller to fill in. */
    static ObjectNode facet(com.fasterxml.jackson.databind.node.ArrayNode facets, String text, int from, int to, String kind) {
        ObjectNode f = facets.addObject();
        ObjectNode index = f.putObject("index");
        index.put("byteStart", text.substring(0, from).getBytes(StandardCharsets.UTF_8).length);
        index.put("byteEnd", text.substring(0, to).getBytes(StandardCharsets.UTF_8).length);
        return f.putArray("features").addObject().put("$type", "app.bsky.richtext.facet#" + kind);
    }

    /** A handle that does not resolve stays plain text rather than failing the post. */
    static String resolveHandle(String handle) {
        try {
            HttpResponse<String> r = HTTP.send(HttpRequest.newBuilder(URI.create(
                            "https://public.api.bsky.app/xrpc/com.atproto.identity.resolveHandle?handle=" + handle))
                    .timeout(Duration.ofSeconds(15)).build(), HttpResponse.BodyHandlers.ofString());
            return r.statusCode() == 200 ? JSON.readTree(r.body()).path("did").asText(null) : null;
        } catch (Exception e) {
            return null;
        }
    }

    static String bsky(String url, String jwt, String contentType, HttpRequest.BodyPublisher body) throws Exception {
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(60))
                .header("Content-Type", contentType).POST(body);
        if (jwt != null) b.header("Authorization", "Bearer " + jwt);
        HttpResponse<String> r = HTTP.send(b.build(), HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() != 200) throw new IllegalStateException("Bluesky answered " + r.statusCode() + ": " + r.body());
        return r.body();
    }

    static String meta(Document page, String property, String fallback) {
        String v = page.select("meta[property=" + property + "]").attr("content").trim();
        return v.isEmpty() ? fallback : v;
    }

    /** Bluesky rejects blobs over about 1 MB, so shrink the cover until it fits. */
    static byte[] thumbnail(String url) throws Exception {
        System.setProperty("java.awt.headless", "true");
        HttpResponse<byte[]> r = HTTP.send(HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(30))
                .header("User-Agent", "foojay.io").build(), HttpResponse.BodyHandlers.ofByteArray());
        if (r.statusCode() != 200) throw new IllegalStateException(url + " answered " + r.statusCode());
        BufferedImage src = ImageIO.read(new ByteArrayInputStream(r.body()));
        if (src == null) throw new IllegalStateException("not a readable image: " + url);
        int w = Math.min(src.getWidth(), 1200), h = Math.max(1, src.getHeight() * w / src.getWidth());
        BufferedImage rgb = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        var g = rgb.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        g.drawImage(src.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
        g.dispose();
        for (float q : new float[]{0.85f, 0.7f, 0.55f, 0.4f}) {
            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(q);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try (var ios = new MemoryCacheImageOutputStream(bytes)) {
                writer.setOutput(ios);
                writer.write(null, new IIOImage(rgb, null, null), param);
            }
            writer.dispose();
            if (bytes.size() <= BLUESKY_IMAGE_LIMIT) return bytes.toByteArray();
        }
        return null;
    }

    /** A channel ID, or a user ID, which opens the bot's direct message with that person. */
    static void post(String token, String user, String text) throws Exception {
        ObjectNode b = JSON.createObjectNode();
        b.put("channel", user);
        b.put("text", text);
        b.put("unfurl_links", false);
        b.put("unfurl_media", false);
        HttpResponse<String> r = HTTP.send(
                HttpRequest.newBuilder(URI.create("https://slack.com/api/chat.postMessage")).timeout(Duration.ofSeconds(30))
                        .header("Authorization", "Bearer " + token)
                        .header("Content-Type", "application/json; charset=utf-8")
                        .POST(HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(b))).build(),
                HttpResponse.BodyHandlers.ofString());
        // Slack answers 200 with ok:false on errors.
        if (r.statusCode() != 200 || !JSON.readTree(r.body()).path("ok").asBoolean()) {
            throw new IllegalStateException("Slack answered " + r.statusCode() + ": " + r.body());
        }
    }

    static String write(String key, Platform pl, Post p, List<Author> authors, String article) throws Exception {
        StringBuilder who = new StringBuilder();
        if (!authors.isEmpty()) {
            who.append("Credit the author(s) of the article by name in the post.\n")
                    .append("Number of authors: ").append(authors.size()).append("\n")
                    .append("Use the handle or link exactly as provided for the ").append(pl.label())
                    .append(" mention. If no handle or link is provided for an author, use the plain name without inventing a handle.\n");
            for (Author au : authors) {
                String handle = switch (pl.id()) {
                    case "bluesky" -> blueskyMention(au.bluesky());
                    case "mastodon" -> mastodonMention(au.mastodon());
                    default -> "@" + au.name(); // LinkedIn has no text handles
                };
                who.append("Author: ").append(au.name()).append("\n    ").append(pl.label())
                        .append(" handle/link: ").append(handle.isBlank() ? "(none)" : handle).append("\n");
            }
        }
        String prompt = """
                Create a short %s post (max %d characters) for this article:
                Title: %s
                Link: %s

                Article content (the only allowed source of facts):
                \"\"\"
                %s
                \"\"\"

                %s

                Write the message as being the publisher and maintainer of the Foojay website.
                Don't use `–`.
                Avoid typical LLM words (like `reveal`, `dive`, `explore`, `exciting`, `thrilled`,...) and characters (like `—`).
                Never add content, names or brand names that are not in the original article (title, description or text above).
                Only Foojay (the publisher) and the author names and handles provided above are allowed on top of that.
                Don't use `breaks down` or `break down`, use `explains` instead.
                Don't go nuts in the wording, keep it simple, no exaggerations.
                Make it engaging but keep it concise.
                Make sure to include a link to the original article.
                Never put a link or an @mention between parentheses.
                You can include extra relevant hashtags, but only a few, and add them at the end of the post.

                %s

                Answer with the post text only, nothing before or after it.""".formatted(
                pl.label(), pl.max(), p.title(), p.link(), article, who, pl.extra());

        String text = unwrapLinks(ask(key, prompt));
        for (int attempt = 0; attempt < 3 && length(pl, text) > pl.max(); attempt++) {
            text = unwrapLinks(ask(key, prompt + "\n\nYour previous answer counted " + length(pl, text) + " characters. The limit is "
                    + pl.max() + ". Shorten it clearly below the limit, keeping the link:\n" + text));
        }
        if (length(pl, text) > pl.max()) {
            throw new IllegalStateException(pl.label() + " text is " + length(pl, text) + " characters, over " + pl.max());
        }
        return text;
    }

    static String ask(String key, String prompt) throws Exception {
        ObjectNode b = JSON.createObjectNode();
        b.put("model", MODEL);
        b.put("max_tokens", 1024);
        b.putArray("messages").addObject().put("role", "user").put("content", prompt);
        HttpResponse<String> r = HTTP.send(
                HttpRequest.newBuilder(URI.create("https://api.anthropic.com/v1/messages")).timeout(Duration.ofSeconds(120))
                        .header("x-api-key", key).header("anthropic-version", "2023-06-01")
                        .header("content-type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(b))).build(),
                HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() != 200) throw new IllegalStateException("Claude answered " + r.statusCode() + ": " + r.body());
        StringBuilder out = new StringBuilder();
        for (JsonNode block : JSON.readTree(r.body()).path("content")) {
            if (block.path("type").asText().equals("text")) out.append(block.path("text").asText());
        }
        if (out.toString().isBlank()) throw new IllegalStateException("Claude returned no text: " + r.body());
        return out.toString().trim();
    }

    /** The same JSON-LD the article page carries: names plus the social profiles in sameAs. */
    static List<Author> authors(Document page) {
        List<Author> out = new ArrayList<>();
        for (Element s : page.select("script[type=application/ld+json]")) {
            try {
                JsonNode root = JSON.readTree(s.data());
                JsonNode graph = root.has("@graph") ? root.get("@graph") : root;
                for (JsonNode item : graph.isArray() ? graph : List.of(graph)) {
                    JsonNode an = item.path("author");
                    for (JsonNode n : an.isArray() ? an : List.of(an)) {
                        String name = n.path("name").asText("").trim();
                        if (name.isEmpty()) continue;
                        String bluesky = "", mastodon = "";
                        for (JsonNode same : n.path("sameAs")) {
                            String href = same.asText("");
                            String host = host(href);
                            if (host.endsWith("bsky.app")) bluesky = href;
                            else if (isMastodon(href, host)
                                    && (mastodon.isEmpty() || host.contains("social") || host.contains("mastodon"))) mastodon = href;
                        }
                        out.add(new Author(name, bluesky, mastodon));
                    }
                }
            } catch (Exception ignored) {
                // a block that is not the article's metadata
            }
        }
        return out;
    }

    static final List<String> NOT_MASTODON = List.of("youtube.com", "youtu.be", "medium.com", "tiktok.com",
            "instagram.com", "threads.net", "threads.com", "github.com", "gitlab.com", "twitch.tv", "dev.to",
            "substack.com", "hashnode.dev");

    static boolean isMastodon(String href, String host) {
        return href.matches("https?://[^/]+/@[^/]+/?")
                && NOT_MASTODON.stream().noneMatch(h -> host.equals(h) || host.endsWith("." + h));
    }

    static final String LINK_OR_MENTION =
            "https?://[^\\s()]+|@[\\w-]+(?:\\.[\\w-]+)*(?:@[\\w-]+(?:\\.[\\w-]+)*)?";

    /** A parenthesis glued to a link or mention is read as part of it, so give it a space. */
    static String unwrapLinks(String text) {
        return text.replaceAll("\\((?=" + LINK_OR_MENTION + ")", "( ")
                .replaceAll("(" + LINK_OR_MENTION + ")\\)", "$1 )");
    }

    static String blueskyMention(String url) {
        if (url == null || url.isBlank()) return "";
        String handle = url.replaceFirst("^https?://(www\\.)?bsky\\.app/profile/", "").replaceAll("/.*$", "");
        return handle.isEmpty() ? url : "@" + handle.replaceFirst("^@", "");
    }

    static String mastodonMention(String url) {
        if (url == null || url.isBlank()) return "";
        try {
            URI u = URI.create(url);
            if (u.getHost() != null && u.getPath() != null && u.getPath().startsWith("/@")) {
                return "@" + u.getPath().substring(2).replaceAll("/$", "") + "@" + u.getHost();
            }
        } catch (IllegalArgumentException ignored) {
            // fall through
        }
        return url;
    }

    static String host(String url) {
        try {
            String h = URI.create(url).getHost();
            return h == null ? "" : h.toLowerCase();
        } catch (IllegalArgumentException e) {
            return "";
        }
    }

    static String articleText(Document page, Post p) {
        Element body = page.selectFirst("article .prose");
        if (body == null) body = page.selectFirst("article");
        String t = body == null ? "" : body.text().trim();
        if (t.isEmpty()) t = p.description();
        return t.length() > MAX_ARTICLE_CHARS ? t.substring(0, MAX_ARTICLE_CHARS) : t;
    }

    static List<Post> readFeed(String file) throws Exception {
        Path path = Path.of(file);
        byte[] xml = Files.exists(path) ? Files.readAllBytes(path) : new byte[0];
        if (xml.length == 0) return List.of();
        var doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml));
        NodeList items = doc.getElementsByTagName("item");
        List<Post> out = new ArrayList<>();
        for (int i = 0; i < items.getLength(); i++) {
            var item = (org.w3c.dom.Element) items.item(i);
            out.add(new Post(text(item, "title"), text(item, "link"), text(item, "description")));
        }
        return out;
    }

    static String text(org.w3c.dom.Element e, String tag) {
        NodeList n = e.getElementsByTagName(tag);
        return n.getLength() == 0 ? "" : n.item(0).getTextContent().trim();
    }

    // Slack mrkdwn treats these three as control characters.
    static String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    static String env(String name) {
        String v = System.getenv(name);
        return v == null || v.isBlank() ? null : v;
    }
}
