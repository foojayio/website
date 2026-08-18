///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.yaml:snakeyaml:2.2
//DEPS com.fasterxml.jackson.core:jackson-databind:2.17.1
//JAVA 17+

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Regenerates data/jugs.yaml from the community-run World Wide JUGs directory
 * (https://github.com/World-Wide-JUGs/GlobalWWJugs), so the /jugs/ page and
 * the Meetup calendar sync always reflect that upstream list instead of a
 * one-time snapshot. Run at every deploy (.github/workflows/build-deploy.yml)
 * and before every Meetup sync (.github/workflows/sync-external-content.yml) --
 * both commit the refreshed file back to main, same pattern as
 * data/events.json.
 *
 * JUG leaders add/update their OWN group by opening a PR against
 * World-Wide-JUGs/GlobalWWJugs's `_jugs/` folder, not this repo -- that's
 * the whole point of pulling from there instead of maintaining our own copy.
 *
 * Source format: one Markdown file per JUG under _jugs/, each just a small
 * YAML frontmatter block, no body -- e.g.
 * https://github.com/World-Wide-JUGs/GlobalWWJugs/blob/master/_jugs/TorontoJUG.md
 *     ---
 *     name:     "Belgian Java User Group"
 *     country:  Belgium
 *     website:  https://bejug.github.io/
 *     meetup:   https://www.meetup.com/belgian-java-user-group
 *     location: 50.846816, 4.352442
 *     ---
 * Not every file has every field (meetup/twitter/mastodon/calendar/founded_date/
 * contact/email are all optional and frequently blank), so everything here
 * is written defensively -- a missing field is just omitted, never a blank
 * string or null in the output.
 */
public class FetchJugs {

    static final String REPO = "World-Wide-JUGs/GlobalWWJugs";
    static final String BRANCH = "master";
    static final String DIR = "_jugs";
    static final String API_LIST_URL =
            "https://api.github.com/repos/" + REPO + "/contents/" + DIR + "?ref=" + BRANCH;
    static final String RAW_BASE =
            "https://raw.githubusercontent.com/" + REPO + "/" + BRANCH + "/" + DIR + "/";
    static final String SOURCE_BLOB_BASE =
            "https://github.com/" + REPO + "/blob/" + BRANCH + "/" + DIR + "/";

    static final Path OUTPUT_FILE = Path.of("data/jugs.yaml");

    // Matches meetup.com URLs so we can hand FetchMeetupEvents.java a clean
    // group slug without every JUG file needing its own dedicated field for it.
    static final Pattern MEETUP_URL = Pattern.compile("meetup\\.com/([^/?#]+)", Pattern.CASE_INSENSITIVE);

    static final ObjectMapper JSON = new ObjectMapper();
    static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();

    public static void main(String[] args) throws Exception {
        List<String> files = listJugFiles();
        System.out.println("Found " + files.size() + " JUG files in " + REPO + "/" + DIR);

        ExecutorService pool = Executors.newFixedThreadPool(8);
        try {
            List<Future<Map<String, Object>>> futures = new ArrayList<>();
            for (String file : files) {
                futures.add(pool.submit(() -> fetchJug(file)));
            }

            List<Map<String, Object>> jugs = new ArrayList<>();
            for (Future<Map<String, Object>> future : futures) {
                try {
                    Map<String, Object> jug = future.get();
                    if (jug != null) jugs.add(jug);
                } catch (ExecutionException e) {
                    System.err.println("FAILED to fetch a JUG file: " + e.getCause());
                }
            }
            pool.shutdown();

            jugs.sort(Comparator.comparing(j -> String.valueOf(j.get("name")), String.CASE_INSENSITIVE_ORDER));
            System.out.println("Parsed " + jugs.size() + " JUGs, writing " + OUTPUT_FILE);

            writeYaml(jugs);
        } finally {
            pool.shutdownNow();
        }
    }

    static List<String> listJugFiles() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_LIST_URL))
                .header("Accept", "application/vnd.github+json")
                .header("User-Agent", "foojay-website-jugs-sync")
                .timeout(Duration.ofSeconds(20))
                .build();
        HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("GitHub API HTTP " + response.statusCode() + ": " + response.body());
        }

        List<String> files = new ArrayList<>();
        for (JsonNode entry : JSON.readTree(response.body())) {
            String name = entry.path("name").asText("");
            if ("file".equals(entry.path("type").asText()) && name.endsWith(".md")) {
                files.add(name);
            }
        }
        return files;
    }

    static Map<String, Object> fetchJug(String file) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(RAW_BASE + file))
                .timeout(Duration.ofSeconds(20))
                .build();
        HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.err.println("Skipping " + file + ": HTTP " + response.statusCode());
            return null;
        }

        Map<String, Object> front = parseFrontmatter(response.body());
        String name = front == null ? null : trimToNull(front.get("name"));
        if (name == null) {
            System.err.println("Skipping " + file + ": no usable frontmatter");
            return null;
        }

        Map<String, Object> jug = new LinkedHashMap<>();
        String slug = file.endsWith(".md") ? file.substring(0, file.length() - 3) : file;
        jug.put("slug", slug);
        jug.put("name", name);
        putIfPresent(jug, "country", front.get("country"));

        String website = trimToNull(front.get("website"));
        putIfPresent(jug, "website", website);

        // Meetup: only when the dedicated `meetup:` field is provided upstream
        // (added in GlobalWWJugs, e.g. _jugs/BelgianJUG.md). We deliberately do
        // NOT infer it from `website` even when that happens to be a meetup.com
        // URL -- only use a Meetup link when it's specifically given. Both forms
        // are written: meetup_slug is what FetchMeetupEvents.java needs for the
        // GraphQL API, meetup_url is the ready-to-link full address.
        String meetup = trimToNull(front.get("meetup"));
        if (meetup != null) {
            Matcher m = MEETUP_URL.matcher(meetup);
            if (m.find()) {
                jug.put("meetup_slug", m.group(1).replaceAll("/+$", ""));
                jug.put("meetup_url", meetup);
            }
        }

        putIfPresent(jug, "twitter", front.get("twitter"));
        putIfPresent(jug, "mastodon", front.get("mastodon"));
        putIfPresent(jug, "calendar", front.get("calendar"));
        putIfPresent(jug, "founded_date", formatDateLike(front.get("founded_date")));
        putIfPresent(jug, "contact", front.get("contact"));
        putIfPresent(jug, "email", front.get("email"));

        String location = trimToNull(front.get("location"));
        if (location != null) {
            String[] parts = location.split(",");
            if (parts.length == 2) {
                try {
                    jug.put("latitude", Double.parseDouble(parts[0].trim()));
                    jug.put("longitude", Double.parseDouble(parts[1].trim()));
                } catch (NumberFormatException ignored) {
                    // Leave coordinates out rather than write bad data.
                }
            }
        }

        // Lets the /jugs/ page link each row straight back to its own source
        // file, so "found an error? edit it here" is a one-click affair.
        jug.put("source_url", SOURCE_BLOB_BASE + file);

        return jug;
    }

    @SuppressWarnings("unchecked")
    static Map<String, Object> parseFrontmatter(String content) {
        int start = content.indexOf("---");
        if (start == -1) return null;
        int end = content.indexOf("---", start + 3);
        if (end == -1) return null;

        Object parsed = new Yaml().load(content.substring(start + 3, end));
        return parsed instanceof Map ? (Map<String, Object>) parsed : null;
    }

    static void putIfPresent(Map<String, Object> jug, String key, Object value) {
        String s = trimToNull(value);
        if (s != null) jug.put(key, s);
    }

    static String trimToNull(Object value) {
        if (value == null) return null;
        String s = String.valueOf(value).trim();
        return s.isEmpty() ? null : s;
    }

    // YAML 1.1 (which SnakeYaml follows) implicitly resolves unquoted
    // "yyyy-MM-dd"-shaped scalars to java.util.Date, not String -- so
    // `founded_date: 2013-01-01` in a source file comes back as a Date
    // object, and Object.toString() on that would print something like
    // "Tue Jan 01 00:00:00 UTC 2013" instead of the plain date. Reformat it
    // back to plain "yyyy-MM-dd" so the output is always a normal string,
    // regardless of whether SnakeYaml parsed the source value as a Date or
    // left it as a plain String (already-blank/quoted values do the latter).
    static Object formatDateLike(Object value) {
        if (value instanceof Date d) {
            return new java.text.SimpleDateFormat("yyyy-MM-dd").format(d);
        }
        return value;
    }

    static void writeYaml(List<Map<String, Object>> jugs) throws IOException {
        String header = """
                # Java User Groups -- generated automatically by scripts/FetchJugs.java
                # from https://github.com/World-Wide-JUGs/GlobalWWJugs (the community-run,
                # crowd-sourced directory of JUGs worldwide).
                #
                # DO NOT EDIT THIS FILE BY HAND: it's regenerated at every site build
                # (.github/workflows/build-deploy.yml) and by the external-content sync
                # (.github/workflows/sync-external-content.yml), and any manual change here is
                # overwritten the next time either runs.
                #
                # To add, fix, or remove a JUG, open a PR against that repo's _jugs/
                # folder instead: https://github.com/World-Wide-JUGs/GlobalWWJugs/tree/master/_jugs
                #
                # meetup_slug/meetup_url are set only when a JUG's file has an explicit
                # `meetup` field (never inferred from `website`); scripts/FetchMeetupEvents.java
                # uses meetup_slug to pull calendar events.

                """.stripIndent();

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        Yaml yaml = new Yaml(options);

        Files.createDirectories(OUTPUT_FILE.getParent());
        Files.writeString(OUTPUT_FILE, header + yaml.dump(jugs));
    }
}
