///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 17+
//DEPS com.fasterxml.jackson.core:jackson-databind:2.17.1

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Posts what was published to Slack, read from the site's own RSS feed so the
 * title, authors, description and link are the ones the page carries.
 *
 *   new <previous.xml> <current.xml>   posts in current that previous lacks
 *   weekly <feed.xml|url>              posts from last Monday to Sunday
 *
 * The webhook is $SLACK_WEBHOOK_URL. Unset, it prints and posts nothing, so a
 * fork or a local run is harmless. `--dry-run` always prints only.
 *
 * `new` takes a feed from BEFORE the deploy rather than remembering what was
 * announced, so there is no state file to commit. An empty or unreadable
 * previous feed aborts: treating it as "nothing was published" would announce
 * the whole feed.
 */
public class SlackPosts {

    record Post(String title, String link, String authors, String categories, String description, ZonedDateTime date) {}

    static final String SITE = "https://foojay.io";
    static final int MAX_NEW = 10;
    static final int DESCRIPTION_LIMIT = 300;

    public static void main(String[] args) throws Exception {
        List<String> a = new ArrayList<>(List.of(args));
        boolean dryRun = a.remove("--dry-run");
        if (a.isEmpty()) usage();

        String webhook = System.getenv("SLACK_WEBHOOK_URL");
        if (!dryRun && (webhook == null || webhook.isBlank())) {
            System.out.println("SLACK_WEBHOOK_URL is not set; printing instead of posting.");
            dryRun = true;
        }

        switch (a.get(0)) {
            case "new" -> {
                if (a.size() != 3) usage();
                announceNew(webhook, dryRun, a.get(1), a.get(2));
            }
            case "weekly" -> {
                if (a.size() != 2) usage();
                announceWeekly(webhook, dryRun, a.get(1));
            }
            default -> usage();
        }
    }

    static void announceNew(String webhook, boolean dryRun, String previousFile, String currentFile) throws Exception {
        List<Post> previous = readFeed(previousFile);
        if (previous.isEmpty()) {
            System.out.println("Previous feed has no posts; skipping rather than announcing everything.");
            return;
        }
        Set<String> known = new LinkedHashSet<>();
        previous.forEach(p -> known.add(p.link()));

        List<Post> fresh = new ArrayList<>(readFeed(currentFile).stream()
                .filter(p -> !known.contains(p.link())).toList());
        if (fresh.isEmpty()) {
            System.out.println("No new posts.");
            return;
        }
        fresh.sort((x, y) -> x.date().compareTo(y.date()));
        if (fresh.size() > MAX_NEW) {
            System.out.printf("%d new posts; announcing only the newest %d.%n", fresh.size(), MAX_NEW);
            fresh = fresh.subList(fresh.size() - MAX_NEW, fresh.size());
        }
        for (Post p : fresh) {
            StringBuilder text = new StringBuilder("The following new post just got published:\n*<" + p.link() + "|" + esc(p.title()) + ">*");
            if (!p.authors().isEmpty()) text.append("\nby ").append(esc(p.authors()));
            if (!p.description().isEmpty()) text.append("\n").append(esc(p.description()));
            send(webhook, dryRun, text.toString());
        }
    }

    static void announceWeekly(String webhook, boolean dryRun, String feed) throws Exception {
        // Post dates are midnight UTC, so the week is cut in UTC too.
        ZonedDateTime end = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.DAYS)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        ZonedDateTime start = end.minusWeeks(1);
        List<Post> posts = readFeed(feed).stream().filter(p -> !p.date().isBefore(start) && p.date().isBefore(end))
                .sorted((x, y) -> y.date().compareTo(x.date())).toList();
        if (posts.isEmpty()) {
            System.out.println("Nothing was published last week.");
            return;
        }
        String review = SITE + "/today/week-review/" + start.toLocalDate() + "/";
        StringBuilder text = new StringBuilder("*Published on Foojay.io the past week* (" + posts.size()
                + "), see the overview on <" + review + "|the weekly review>");
        newAuthors(review + "review.json").ifPresent(n -> text.append("\n\n*New authors*\n").append(n));
        send(webhook, dryRun, text.toString());
    }

    // The week page works out who published a first article; the feed cannot.
    // Any failure drops the section rather than the whole announcement.
    static Optional<String> newAuthors(String url) {
        try {
            HttpResponse<String> r = HttpClient.newHttpClient().send(
                    HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(30))
                            .header("User-Agent", "foojay.io").build(),
                    HttpResponse.BodyHandlers.ofString());
            if (r.statusCode() != 200) throw new IllegalStateException(url + " answered " + r.statusCode());
            List<String> links = new ArrayList<>();
            for (JsonNode a : new ObjectMapper().readTree(r.body()).path("newAuthors")) {
                links.add("<" + a.path("url").asText() + "|" + esc(a.path("name").asText()) + ">");
            }
            if (links.isEmpty()) return Optional.empty();
            String names = links.size() == 1 ? links.get(0)
                    : String.join(", ", links.subList(0, links.size() - 1)) + " and " + links.get(links.size() - 1);
            return Optional.of("Welcome to " + names + ", who published " + (links.size() == 1 ? "a" : "their")
                    + " first article on foojay.io this week.");
        } catch (Exception e) {
            System.out.println("No new-authors section: " + e.getMessage());
            return Optional.empty();
        }
    }

    static List<Post> readFeed(String source) throws Exception {
        byte[] xml;
        if (source.startsWith("http://") || source.startsWith("https://")) {
            HttpResponse<byte[]> r = HttpClient.newHttpClient().send(
                    HttpRequest.newBuilder(URI.create(source)).timeout(Duration.ofSeconds(30))
                            .header("User-Agent", "foojay.io").build(),
                    HttpResponse.BodyHandlers.ofByteArray());
            if (r.statusCode() != 200) throw new IllegalStateException(source + " answered " + r.statusCode());
            xml = r.body();
        } else {
            Path p = Path.of(source);
            xml = Files.exists(p) ? Files.readAllBytes(p) : new byte[0];
        }
        if (xml.length == 0) return List.of();

        var doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml));
        NodeList items = doc.getElementsByTagName("item");
        List<Post> out = new ArrayList<>();
        for (int i = 0; i < items.getLength(); i++) {
            Element item = (Element) items.item(i);
            List<String> authors = new ArrayList<>();
            NodeList creators = item.getElementsByTagName("dc:creator");
            for (int j = 0; j < creators.getLength(); j++) authors.add(creators.item(j).getTextContent().trim());
            List<String> categories = new ArrayList<>();
            NodeList cats = item.getElementsByTagName("category");
            for (int j = 0; j < cats.getLength(); j++) categories.add(cats.item(j).getTextContent().trim());
            out.add(new Post(text(item, "title"), text(item, "link"), String.join(", ", authors),
                    String.join(", ", categories),
                    truncate(text(item, "description")),
                    ZonedDateTime.parse(text(item, "pubDate"), DateTimeFormatter.RFC_1123_DATE_TIME)));
        }
        return out;
    }

    static String text(Element e, String tag) {
        NodeList n = e.getElementsByTagName(tag);
        return n.getLength() == 0 ? "" : n.item(0).getTextContent().trim();
    }

    static String truncate(String s) {
        s = s.replaceAll("\\s+", " ");
        if (s.length() <= DESCRIPTION_LIMIT) return s;
        int cut = s.lastIndexOf(' ', DESCRIPTION_LIMIT);
        return s.substring(0, cut > 0 ? cut : DESCRIPTION_LIMIT) + "…";
    }

    // Slack mrkdwn treats these three as control characters.
    static String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    static void send(String webhook, boolean dryRun, String text) throws Exception {
        if (dryRun) {
            System.out.println("---\n" + text);
            return;
        }
        String body = "{\"text\":" + json(text) + ",\"unfurl_links\":false}";
        HttpResponse<String> r = HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create(webhook)).timeout(Duration.ofSeconds(30))
                        .header("Content-Type", "application/json; charset=utf-8")
                        .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8)).build(),
                HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() != 200) {
            throw new IllegalStateException("Slack answered " + r.statusCode() + ": " + r.body());
        }
        System.out.println("Posted: " + text.lines().findFirst().orElse(""));
    }

    static String json(String s) {
        StringBuilder b = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"' -> b.append("\\\"");
                case '\\' -> b.append("\\\\");
                case '\n' -> b.append("\\n");
                case '\r' -> b.append("\\r");
                case '\t' -> b.append("\\t");
                default -> { if (c < 0x20) b.append(String.format("\\u%04x", (int) c)); else b.append(c); }
            }
        }
        return b.append('"').toString();
    }

    static void usage() {
        System.err.println("usage: SlackPosts.java [--dry-run] new <previous.xml> <current.xml> | weekly <feed.xml|url>");
        System.exit(2);
    }
}
