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
 * and before every event sync (.github/workflows/sync-external-content.yml) --
 * both commit the refreshed file back to main, same pattern as
 * data/jug-events.json.
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
public class Jugs {

    static final String REPO = "World-Wide-JUGs/GlobalWWJugs";
    static final String BRANCH = "master";

    /**
     * WHERE THE JUG FILES LIVE UPSTREAM, primary first -- and there are two
     * entries because upstream MOVED them, which cost this site its whole JUG
     * list for one deploy.
     *
     * GlobalWWJugs migrated from Jekyll to Roq on 2026-09-07 (their PR #105),
     * which moved 100 of the 101 JUG files from `_jugs/` to `content/jugs/`.
     * The listing of `_jugs/` still answered 200 with one file in it -- the
     * Boston chapter, added three days earlier while that migration was in
     * flight, so the merge left it behind in the old folder. So this script
     * found 1 JUG, wrote 1 JUG, and the deploy gate caught it on the only
     * assertion that could: tests/e2e's "the jugs map is wired to real points"
     * saw a single marker where it wanted more than five.
     *
     * Reading both folders is not carrying upstream's mistake around forever:
     * the stranded file belongs in `content/jugs/` and the fix is a PR there,
     * and until it lands this is what keeps that JUG on the map. Once the
     * folder is empty the second listing costs one API call and contributes
     * nothing, so this retires itself with nothing to switch off -- the same
     * shape as fetch/JavaChampions.java preferring an upstream `location:`.
     * The frontmatter schema did NOT change in the migration (name, country,
     * website, meetup, twitter, location), which is why only the path moved.
     */
    static final List<String> DIRS = List.of("content/jugs", "_jugs");

    static String apiListUrl(String dir) {
        return "https://api.github.com/repos/" + REPO + "/contents/" + dir + "?ref=" + BRANCH;
    }

    static String rawUrl(String dir, String file) {
        return "https://raw.githubusercontent.com/" + REPO + "/" + BRANCH + "/" + dir + "/" + file;
    }

    static String blobUrl(String dir, String file) {
        return "https://github.com/" + REPO + "/blob/" + BRANCH + "/" + dir + "/" + file;
    }

    static final Path OUTPUT_FILE = Path.of("data/jugs.yaml");

    // Matches meetup.com URLs so we can hand fetch/JugEvents.java a clean
    // group slug without every JUG file needing its own dedicated field for it.
    // The optional locale segment is why this isn't just "the first path
    // segment": Meetup serves a group under /de-DE/JUG-Bonn/ as readily as
    // /JUG-Bonn/, and two JUG files upstream link the localized form -- taking
    // the first segment stored `meetup_slug: de-DE` for both, which is not a
    // group and resolves to nothing at all.
    static final Pattern MEETUP_URL = Pattern.compile(
            "meetup\\.com/(?:(?-i:[a-z]{2}-[A-Z]{2})/)?([^/?#]+)", Pattern.CASE_INSENSITIVE);

    static final ObjectMapper JSON = new ObjectMapper();
    static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();

    public static void main(String[] args) throws Exception {
        boolean allowShrink = List.of(args).contains("--allow-shrink");

        // File name -> the folder it was found in. Keyed on the name because
        // that IS the JUG's identity here (the slug is the file name), so a
        // file present in both folders resolves to the primary one.
        Map<String, String> found = new LinkedHashMap<>();
        for (String dir : DIRS) {
            List<String> files = listJugFiles(dir);
            System.out.println("Found " + files.size() + " JUG files in " + REPO + "/" + dir);
            int only = 0;
            for (String file : files) {
                if (found.putIfAbsent(file, dir) == null && !dir.equals(DIRS.get(0))) only++;
            }
            // Named rather than silently absorbed: a file outside the primary
            // folder is an upstream leftover somebody has to move, and the only
            // way anyone learns of it is this line.
            if (only > 0) {
                System.out.println("  " + only + " of these are ONLY in " + dir
                        + " -- they belong in " + DIRS.get(0) + " upstream (open a PR there).");
            }
        }

        ExecutorService pool = Executors.newFixedThreadPool(8);
        try {
            List<Future<Map<String, Object>>> futures = new ArrayList<>();
            for (Map.Entry<String, String> e : found.entrySet()) {
                futures.add(pool.submit(() -> fetchJug(e.getValue(), e.getKey())));
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

            // A COLLAPSE IS NEVER NEWS ABOUT THE JUG DIRECTORY, so it does not
            // get to overwrite the only good copy -- the same guard, and the
            // same reasoning, as fetch/ViewCounts.java keeping the committed
            // file when the counter answers with fewer pages than it holds.
            // This is what the folder move above would have cost nothing if it
            // had existed: a 100-to-1 answer is a moved folder, a renamed
            // branch or a bad listing, never 99 JUGs disbanding overnight.
            //
            // HALF, rather than "any drop": a JUG genuinely leaving the
            // directory is an ordinary one-entry change, and a guard that
            // froze the file on that is one nobody trusts. The two cases are
            // not close -- a restructure lands at 1%, a removal at 99%.
            //
            // And it KEEPS THE FILE and exits 0 rather than failing. This runs
            // before the Hugo step in build-deploy.yml, so a hard failure here
            // would take a deploy down over the JUG map; the stale-but-correct
            // list is the better answer, and the message is the report.
            int existing = countExisting();
            if (!allowShrink && existing >= 10 && jugs.size() < existing / 2) {
                System.err.println("REFUSING TO WRITE " + OUTPUT_FILE + ": upstream answered with "
                        + jugs.size() + " JUG(s) where the committed file holds " + existing + ".");
                System.err.println("  That is a broken source, not a smaller directory -- check whether "
                        + DIRS.get(0) + " still exists in " + REPO + " on branch " + BRANCH + ".");
                System.err.println("  The committed file is kept as-is. Re-run with --allow-shrink once you"
                        + " have confirmed the drop is real.");
                return;
            }

            System.out.println("Parsed " + jugs.size() + " JUGs, writing " + OUTPUT_FILE);
            writeYaml(jugs);
        } finally {
            pool.shutdownNow();
        }
    }

    static List<String> listJugFiles(String dir) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiListUrl(dir)))
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

    static Map<String, Object> fetchJug(String dir, String file) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(rawUrl(dir, file)))
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
        // are written: meetup_slug is what fetch/JugEvents.java needs for the
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
        jug.put("source_url", blobUrl(dir, file));

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

    /**
     * How many JUGs the committed data/jugs.yaml holds, or 0 when there is no
     * file yet (a fresh clone, or a fork that has never run this) -- which is
     * why the guard also requires a floor of its own before it fires.
     *
     * Counted from the parsed YAML rather than by grepping `- slug:`, so a
     * comment in the header that happens to start that way cannot inflate it.
     */
    @SuppressWarnings("unchecked")
    static int countExisting() {
        if (!Files.isRegularFile(OUTPUT_FILE)) return 0;
        try {
            Object parsed = new Yaml().load(Files.readString(OUTPUT_FILE));
            return parsed instanceof List<?> l ? l.size() : 0;
        } catch (Exception e) {
            // An unreadable file is not evidence of anything, so it must not be
            // read as "the directory shrank" -- fall through and write.
            System.err.println("Could not read " + OUTPUT_FILE + " to compare counts: " + e);
            return 0;
        }
    }

    static void writeYaml(List<Map<String, Object>> jugs) throws IOException {
        String header = """
                # Java User Groups -- generated automatically by scripts/fetch/Jugs.java
                # from https://github.com/World-Wide-JUGs/GlobalWWJugs (the community-run,
                # crowd-sourced directory of JUGs worldwide).
                #
                # DO NOT EDIT THIS FILE BY HAND: it's regenerated at every site build
                # (.github/workflows/build-deploy.yml) and by the external-content sync
                # (.github/workflows/sync-external-content.yml), and any manual change here is
                # overwritten the next time either runs.
                #
                # To add, fix, or remove a JUG, open a PR against that repo's content/jugs/
                # folder instead: https://github.com/World-Wide-JUGs/GlobalWWJugs/tree/master/content/jugs
                #
                # meetup_slug/meetup_url are set only when a JUG's file has an explicit
                # `meetup` field (never inferred from `website`); scripts/fetch/JugEvents.java
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
