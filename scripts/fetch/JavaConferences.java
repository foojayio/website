///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.yaml:snakeyaml:2.2
//DEPS com.fasterxml.jackson.core:jackson-databind:2.17.1
//JAVA 17+

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Regenerates data/java-conferences.yaml from javaconferences.org
 * (https://github.com/javaconferences/javaconferences.github.io), which feeds
 * both the /java-conferences/ map and the conferences on /calendar/.
 *
 * It reads the site's published conferences.json rather than the README the
 * list is edited in: that is the same list with the location already geocoded,
 * so this needs no geocoder and no key. Dates arrive as free text
 * ("31 January-1 February 2026") and are parsed here; one that cannot be is
 * reported and skipped, never guessed.
 *
 * Past conferences are kept: filtering them here would change the file every
 * day, and the templates already drop an event once it has ended.
 */
public class JavaConferences {

    static final String SOURCE = "https://javaconferences.org/conferences.json";
    static final String SOURCE_REPO = "https://github.com/javaconferences/javaconferences.github.io";
    static final Path OUTPUT_FILE = Path.of("data/java-conferences.yaml");

    // Read by .github/scripts/sync-health.sh to tell a blip from a sustained failure.
    static final String SYNC_KEY = "java-conferences";

    static final ObjectMapper JSON = new ObjectMapper();
    static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();

    // "5-9 October 2026", "31 January-1 February 2026", "31 March - 1 April 2027", "6 February 2026"
    static final Pattern DATE = Pattern.compile(
            "(\\d{1,2})(?:\\s+([A-Za-z]+))?(?:\\s*[-\\u2013\\u2014]\\s*(\\d{1,2})\\s+([A-Za-z]+))?\\s+(\\d{4})");

    public static void main(String[] args) throws Exception {
        boolean allowShrink = List.of(args).contains("--allow-shrink");

        JsonNode source;
        try {
            HttpResponse<String> response = HTTP.send(HttpRequest.newBuilder(URI.create(SOURCE))
                    .header("User-Agent", "foojay.io conferences sync")
                    .timeout(Duration.ofSeconds(30)).build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) throw new java.io.IOException("HTTP " + response.statusCode());
            source = JSON.readTree(response.body());
        } catch (Exception e) {
            if (e instanceof InterruptedException) Thread.currentThread().interrupt();
            degraded("could not read " + SOURCE + " (" + e + ")");
            return;
        }
        if (!source.isArray() || source.isEmpty()) {
            degraded(SOURCE + " is not a non-empty list");
            return;
        }

        List<Map<String, Object>> conferences = new ArrayList<>();
        Set<String> slugs = new HashSet<>();
        for (JsonNode c : source) {
            String name = c.path("name").asText("").trim();
            LocalDate[] range = parseDates(c.path("date").asText(""));
            if (name.isEmpty() || range == null) {
                System.err.println("Skipping '" + name + "': cannot read the date '" + c.path("date").asText() + "'"
                        + " -- fix it upstream in " + SOURCE_REPO);
                continue;
            }

            String location = c.path("locationName").asText("").trim();
            boolean online = location.equalsIgnoreCase("online");
            JsonNode at = c.path("coordinates");

            String slug = slugify(name + " " + range[0].getYear());
            for (int n = 2; !slugs.add(slug); n++) slug = slugify(name + " " + range[0].getYear()) + "-" + n;

            Map<String, Object> out = new LinkedHashMap<>();
            out.put("slug", slug);
            out.put("name", name);
            put(out, "url", c.path("link").asText(""));
            out.put("start", range[0].toString());
            out.put("end", range[1].toString());
            out.put("online", online);
            out.put("hybrid", c.path("hybrid").asBoolean(false));
            if (!online) {
                put(out, "city", location.contains(",") ? location.substring(0, location.indexOf(',')) : location);
                String country = at.path("countryName").asText("");
                put(out, "country", country.isBlank() && location.contains(",")
                        ? location.substring(location.lastIndexOf(',') + 1) : country);
                double lat = at.path("lat").asDouble(0), lon = at.path("lon").asDouble(0);
                // 0,0 is a geocoder that parsed nothing, and the Atlantic.
                if (lat != 0 || lon != 0) {
                    out.put("latitude", lat);
                    out.put("longitude", lon);
                } else {
                    System.err.println("No coordinates for '" + name + "' (" + location + ") -- off the map.");
                }
            }
            put(out, "cfp_url", c.path("cfpLink").asText(""));
            // Date-only text, so the same parser reads it. Anything else is left out.
            LocalDate[] closes = parseDates(c.path("cfpEndDate").asText(""));
            if (closes != null) out.put("cfp_closes", closes[1].toString());
            conferences.add(out);
        }

        conferences.sort(Comparator.comparing((Map<String, Object> m) -> (String) m.get("start"))
                .thenComparing(m -> (String) m.get("name")));

        int existing = countExisting();
        if (!allowShrink && existing >= 10 && conferences.size() < existing / 2) {
            degraded("refused to write " + conferences.size() + " conferences over the " + existing
                    + " committed -- a broken source, not a smaller list (--allow-shrink to override)");
            return;
        }

        System.out.println("Parsed " + conferences.size() + " of " + source.size() + " conferences");
        writeYaml(conferences);
    }

    static void degraded(String why) {
        System.err.println("KEEPING " + OUTPUT_FILE + " as committed: " + why);
        System.out.println("SYNC-DEGRADED " + SYNC_KEY + ": " + why);
    }

    /** start and end, or null when the text is not one of the shapes above. */
    static LocalDate[] parseDates(String text) {
        Matcher m = DATE.matcher(text.trim());
        if (!m.matches()) return null;
        try {
            int year = Integer.parseInt(m.group(5));
            int startDay = Integer.parseInt(m.group(1));
            if (m.group(3) == null) {
                // "6 February 2026" carries one day; "5-9 October 2026" has no month on the first.
                return m.group(2) == null ? null : span(year, startDay, m.group(2), startDay, m.group(2));
            }
            String endMonth = m.group(4);
            return span(year, startDay, m.group(2) == null ? endMonth : m.group(2), Integer.parseInt(m.group(3)), endMonth);
        } catch (RuntimeException e) {
            return null;
        }
    }

    static LocalDate[] span(int year, int startDay, String startMonth, int endDay, String endMonth) {
        LocalDate end = LocalDate.of(year, month(endMonth), endDay);
        LocalDate start = LocalDate.of(year, month(startMonth), startDay);
        // "29 December-2 January 2027": the stated year belongs to the end.
        if (start.isAfter(end)) start = start.minusYears(1);
        return new LocalDate[] {start, end};
    }

    static Month month(String name) {
        String n = name.toLowerCase(Locale.ROOT);
        for (Month m : Month.values()) {
            if (m.name().toLowerCase(Locale.ROOT).startsWith(n.length() >= 3 ? n.substring(0, 3) : n)) return m;
        }
        throw new IllegalArgumentException(name);
    }

    static String slugify(String s) {
        String ascii = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return ascii.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }

    static void put(Map<String, Object> m, String key, String value) {
        String v = value == null ? "" : value.trim();
        if (!v.isEmpty()) m.put(key, v);
    }

    @SuppressWarnings("unchecked")
    static int countExisting() {
        if (!Files.isRegularFile(OUTPUT_FILE)) return 0;
        try {
            Object parsed = new Yaml().load(Files.readString(OUTPUT_FILE));
            return parsed instanceof List<?> l ? l.size() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    static void writeYaml(List<Map<String, Object>> conferences) throws java.io.IOException {
        String header = """
                # Java conferences -- generated automatically by scripts/fetch/JavaConferences.java
                # from https://javaconferences.org (source: %s).
                #
                # DO NOT EDIT THIS FILE BY HAND: the external-content sync regenerates it daily
                # (.github/workflows/sync-external-content.yml) and overwrites any change.
                #
                # To add, fix or remove a conference, send a pull request to that repository's
                # README.md: %s/blob/main/README.md
                #
                # Read by /java-conferences/ (the map) and /calendar/. A conference with its own
                # file in data/events/ is shown from that file instead.

                """.formatted(SOURCE_REPO, SOURCE_REPO);

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        String body = new Yaml(options).dump(conferences);

        // Rewrite only on a real change, so a no-op run commits and deploys nothing.
        String next = header + body;
        if (Files.isRegularFile(OUTPUT_FILE) && Files.readString(OUTPUT_FILE).equals(next)) {
            System.out.println("No change.");
            return;
        }
        Files.createDirectories(OUTPUT_FILE.getParent());
        Files.writeString(OUTPUT_FILE, next);
    }
}
