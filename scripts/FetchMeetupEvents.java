///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.yaml:snakeyaml:2.2
//DEPS com.fasterxml.jackson.core:jackson-databind:2.17.1
//JAVA 17+

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pulls upcoming events for every JUG in data/jugs.yaml that has a Meetup
 * group (a `meetup_slug`, set by scripts/FetchJugs.java from the JUG's own
 * entry in the World Wide JUGs directory) and writes data/events.json for the
 * calendar page at /calendar/. Runs on a schedule defined in
 * .github/workflows/sync-external-content.yml, which runs FetchJugs.java first so this
 * always sees the current upstream JUG list rather than a stale commit.
 *
 * NO CREDENTIAL IS NEEDED. This used to POST to Meetup's GraphQL API, which
 * requires a Meetup Pro subscription plus an OAuth client -- a paid dependency
 * for reading events that Meetup already publishes to anyone. It now reads two
 * public, machine-readable sources instead:
 *
 *   1. The group's iCal feed, https://www.meetup.com/<slug>/events/ical/ --
 *      the feed a member subscribes to from their calendar app. Gives the id,
 *      title, event URL, start and end (with a real IANA TZID, not a fixed
 *      offset, so a recurring event stays correct across a DST change) and
 *      status. One ~2.5 KB request per group. Permitted by meetup.com's
 *      robots.txt, which disallows the rss/atom/xml variants of the same route
 *      but not this one.
 *
 *   2. Each event's own page, for the schema.org JSON-LD `Event` block Meetup
 *      publishes there for search engines -- the venue name, city and
 *      online/in-person flag, which is the ONE thing the iCal feed omits (it
 *      carries no LOCATION property at all, which is why every event in the
 *      first version of data/events.json had `venue: null`). Event pages are
 *      listed in Meetup's own sitemap and are not disallowed either.
 *
 * Step 2 is deliberately best-effort: a failed or unparseable page leaves
 * venue/city null and keeps the event, because a missing venue is exactly what
 * the calendar already renders and is never worth losing an event over.
 *
 * The requests identify themselves (see USER_AGENT) rather than pretending to
 * be a browser, run one at a time with a short pause between them, and the
 * calendar links every event back to its Meetup page.
 *
 * Usage:
 *   jbang scripts/FetchMeetupEvents.java                 # full sync
 *   jbang scripts/FetchMeetupEvents.java --dry-run       # report, write nothing
 *   jbang scripts/FetchMeetupEvents.java --limit 3       # first 3 groups only
 *   jbang scripts/FetchMeetupEvents.java --slug jug-hamburg
 *   jbang scripts/FetchMeetupEvents.java --no-venues     # skip the JSON-LD pass
 */
public class FetchMeetupEvents {

    static final Path JUGS_FILE = Path.of("data/jugs.yaml");
    static final Path OUTPUT_FILE = Path.of("data/events.json");

    /** How many upcoming events to keep per group. */
    static final int EVENTS_PER_GROUP = 10;

    /** Attributable rather than disguised: who this is, and where to complain. */
    static final String USER_AGENT =
            "foojay.io-calendar/1.0 (+https://foojay.io/calendar/; Java User Group events, refreshed 4x daily)";

    /** Pause between requests. ~130 requests a run, four runs a day. */
    static final long REQUEST_PAUSE_MS = 300;

    static final String ICAL_URL = "https://www.meetup.com/%s/events/ical/";
    static final String GROUP_URL = "https://www.meetup.com/%s/";

    static final ObjectMapper JSON = new ObjectMapper();
    static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    static final Pattern LD_JSON = Pattern.compile(
            "<script[^>]+type=\"application/ld\\+json\"[^>]*>(.*?)</script>",
            Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    public static void main(String[] args) throws Exception {
        boolean dryRun = has(args, "--dry-run");
        boolean withVenues = !has(args, "--no-venues");
        int limit = intArg(args, "--limit", Integer.MAX_VALUE);
        String onlySlug = arg(args, "--slug");

        List<Map<String, Object>> jugs = loadJugs();
        if (onlySlug != null) {
            jugs = jugs.stream()
                    .filter(j -> onlySlug.equalsIgnoreCase(String.valueOf(j.get("meetup_slug"))))
                    .toList();
            if (jugs.isEmpty()) {
                System.err.println("No JUG in " + JUGS_FILE + " has meetup_slug " + onlySlug);
                System.exit(1);
            }
        }
        System.out.println("Loaded " + jugs.size() + " JUGs with a Meetup group from " + JUGS_FILE);

        List<Map<String, Object>> allGroups = new ArrayList<>();
        int events = 0, venues = 0, failed = 0;

        for (Map<String, Object> jug : jugs) {
            if (allGroups.size() >= limit) break;
            String slug = String.valueOf(jug.get("meetup_slug"));
            String jugSlug = String.valueOf(jug.get("slug"));
            String fallbackName = String.valueOf(jug.getOrDefault("name", slug));

            Map<String, Object> group = new LinkedHashMap<>();
            group.put("slug", slug);
            group.put("name", fallbackName);
            group.put("jug", jugSlug);

            try {
                String ics = get(String.format(ICAL_URL, slug));
                List<Map<String, String>> vevents = parseVEvents(ics);
                String calName = calendarName(ics);
                if (calName != null && !calName.isBlank()) group.put("name", calName);
                group.put("url", String.format(GROUP_URL, slug));

                List<Map<String, Object>> groupEvents = new ArrayList<>();
                for (Map<String, String> ve : vevents) {
                    Map<String, Object> event = toEvent(ve);
                    if (event != null) groupEvents.add(event);
                }
                groupEvents.sort(Comparator.comparing(e -> String.valueOf(e.get("startTime"))));
                if (groupEvents.size() > EVENTS_PER_GROUP) {
                    groupEvents = new ArrayList<>(groupEvents.subList(0, EVENTS_PER_GROUP));
                }

                if (withVenues) {
                    for (Map<String, Object> event : groupEvents) {
                        if (enrichWithVenue(event)) venues++;
                    }
                }

                group.put("events", groupEvents);
                events += groupEvents.size();
                System.out.println("  " + slug + ": " + groupEvents.size() + " upcoming event(s)");
            } catch (NotFound e) {
                // A 404 here is not a fetch failure: the slug in the upstream
                // JUG directory is wrong, or the group was renamed or removed.
                // Say so, so the calendar's note and the JUG lead can act on it.
                group.put("error", "Meetup group not found (404) -- the meetup_slug in GlobalWWJugs "
                        + "is wrong, or the group was renamed or removed");
                failed++;
                System.err.println("  " + slug + ": NOT FOUND on Meetup");
            } catch (Exception e) {
                group.put("error", e.getMessage());
                failed++;
                System.err.println("  " + slug + ": FAILED -- " + e.getMessage());
            }
            allGroups.add(group);
        }

        Map<String, Object> output = new LinkedHashMap<>();
        output.put("generatedAt", DateTimeFormatter.ISO_INSTANT.format(Instant.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS)));
        output.put("source", "meetup.com public iCal feeds (/<group>/events/ical/), venues from the "
                + "schema.org JSON-LD on each event page -- see scripts/FetchMeetupEvents.java");
        output.put("groups", allGroups);

        String json = JSON.writerWithDefaultPrettyPrinter().writeValueAsString(output) + "\n";
        System.out.println(events + " events across " + allGroups.size() + " groups"
                + (withVenues ? ", " + venues + " with a venue" : "")
                + (failed > 0 ? ", " + failed + " group(s) unavailable" : ""));

        if (dryRun || onlySlug != null || limit != Integer.MAX_VALUE) {
            // A partial run would drop every group it didn't ask for, so print
            // what was fetched instead of writing a truncated file.
            System.out.println(json);
            System.out.println(dryRun ? "--dry-run: " + OUTPUT_FILE + " not written"
                    : "--slug/--limit run: " + OUTPUT_FILE + " not written");
            return;
        }

        // Only rewrite when the events themselves changed. `generatedAt` moves
        // on every run, and writing it unconditionally would commit -- and so
        // deploy -- four times a day whether or not a single event changed.
        if (Files.exists(OUTPUT_FILE) && sameEvents(Files.readString(OUTPUT_FILE), json)) {
            System.out.println("No event changes -- " + OUTPUT_FILE + " left untouched");
            return;
        }
        Files.createDirectories(OUTPUT_FILE.getParent());
        Files.writeString(OUTPUT_FILE, json);
        System.out.println("Wrote " + OUTPUT_FILE);
    }

    // ---------------------------------------------------------------- input --

    @SuppressWarnings("unchecked")
    static List<Map<String, Object>> loadJugs() throws IOException {
        Yaml yaml = new Yaml();
        List<Map<String, Object>> all;
        try (var in = Files.newInputStream(JUGS_FILE)) {
            all = (List<Map<String, Object>>) yaml.load(in);
        }
        List<Map<String, Object>> withMeetup = new ArrayList<>();
        for (Map<String, Object> jug : all) {
            if (jug.get("meetup_slug") != null) withMeetup.add(jug);
        }
        return withMeetup;
    }

    // ----------------------------------------------------------------- iCal --

    /**
     * Splits a VCALENDAR into its VEVENTs, each as property name -> raw value,
     * with any property parameters kept under "NAME;params" as well so the
     * caller can read DTSTART's TZID. Line folding (RFC 5545: a continuation
     * line starts with a space or tab) is undone first -- Meetup wraps long
     * DESCRIPTIONs at 75 octets, mid-word.
     */
    static List<Map<String, String>> parseVEvents(String ics) {
        List<String> lines = unfold(ics);
        List<Map<String, String>> events = new ArrayList<>();
        Map<String, String> current = null;
        for (String line : lines) {
            if (line.equals("BEGIN:VEVENT")) {
                current = new LinkedHashMap<>();
            } else if (line.equals("END:VEVENT")) {
                if (current != null) events.add(current);
                current = null;
            } else if (current != null) {
                int colon = line.indexOf(':');
                if (colon < 0) continue;
                String key = line.substring(0, colon);
                String value = line.substring(colon + 1);
                int semi = key.indexOf(';');
                String name = semi < 0 ? key : key.substring(0, semi);
                current.put(name, value);
                if (semi >= 0) current.put(name + ".params", key.substring(semi + 1));
            }
        }
        return events;
    }

    static List<String> unfold(String ics) {
        List<String> out = new ArrayList<>();
        for (String raw : ics.replace("\r\n", "\n").replace('\r', '\n').split("\n", -1)) {
            if (!out.isEmpty() && (raw.startsWith(" ") || raw.startsWith("\t"))) {
                out.set(out.size() - 1, out.get(out.size() - 1) + raw.substring(1));
            } else {
                out.add(raw);
            }
        }
        return out;
    }

    /** The calendar's own display name -- the group name, without a second request. */
    static String calendarName(String ics) {
        for (String line : unfold(ics)) {
            if (line.startsWith("X-WR-CALNAME:")) return unescapeText(line.substring("X-WR-CALNAME:".length()));
            if (line.startsWith("NAME:")) return unescapeText(line.substring("NAME:".length()));
        }
        return null;
    }

    /** One VEVENT -> the event shape data/events.json holds, or null to skip it. */
    static Map<String, Object> toEvent(Map<String, String> ve) {
        String status = ve.getOrDefault("STATUS", "");
        if (status.equalsIgnoreCase("CANCELLED")) return null;

        OffsetDateTime start = parseIcalDate(ve.get("DTSTART"), ve.get("DTSTART.params"));
        if (start == null) return null;
        // The feed is an upcoming-events feed, but don't rely on that: a stale
        // entry would otherwise sit in a past month of the calendar forever.
        if (start.isBefore(OffsetDateTime.now().minusDays(1))) return null;
        OffsetDateTime end = parseIcalDate(ve.get("DTEND"), ve.get("DTEND.params"));

        Map<String, Object> event = new LinkedHashMap<>();
        event.put("title", unescapeText(ve.getOrDefault("SUMMARY", "Untitled event")));
        event.put("url", ve.getOrDefault("URL", ""));
        event.put("startTime", start.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        event.put("endTime", end == null ? null : end.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        event.put("venue", null);
        event.put("city", null);
        event.put("online", null);
        return event;
    }

    /**
     * iCal dates come in three shapes: a UTC instant ("...Z"), a local time
     * with a TZID parameter naming an IANA zone, and a bare date (VALUE=DATE)
     * for an all-day event. All three end up as an offset date-time so the
     * stored string carries the event's own local time, which is what the
     * calendar page renders.
     */
    static OffsetDateTime parseIcalDate(String value, String params) {
        if (value == null || value.isBlank()) return null;
        try {
            if (value.endsWith("Z")) {
                return LocalDateTime.parse(value.substring(0, value.length() - 1),
                        DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")).atOffset(ZoneOffset.UTC);
            }
            ZoneId zone = zoneOf(params);
            if (value.length() == 8) { // VALUE=DATE
                return LocalDate.parse(value, DateTimeFormatter.BASIC_ISO_DATE)
                        .atStartOfDay(zone).toOffsetDateTime();
            }
            return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"))
                    .atZone(zone).toOffsetDateTime();
        } catch (Exception e) {
            System.err.println("    unparseable date '" + value + "' (" + params + ")");
            return null;
        }
    }

    static ZoneId zoneOf(String params) {
        if (params != null) {
            for (String part : params.split(";")) {
                if (part.regionMatches(true, 0, "TZID=", 0, 5)) {
                    String tzid = part.substring(5).replace("\"", "");
                    try {
                        return ZoneId.of(tzid);
                    } catch (Exception ignored) {
                        System.err.println("    unknown TZID '" + tzid + "', falling back to UTC");
                    }
                }
            }
        }
        return ZoneOffset.UTC;
    }

    /** RFC 5545 text escaping: \n \, \; \\ */
    static String unescapeText(String s) {
        if (s == null) return null;
        StringBuilder out = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char next = s.charAt(++i);
                switch (next) {
                    case 'n', 'N' -> out.append('\n');
                    case ',' -> out.append(',');
                    case ';' -> out.append(';');
                    case '\\' -> out.append('\\');
                    default -> out.append(next);
                }
            } else {
                out.append(c);
            }
        }
        return out.toString().trim();
    }

    // -------------------------------------------------------------- JSON-LD --

    /**
     * Reads the venue out of the event page's schema.org JSON-LD. Best-effort
     * by design: anything that goes wrong leaves venue/city null and keeps the
     * event, which is how the calendar renders an online or venue-less event
     * anyway. Returns true when a venue was found.
     */
    static boolean enrichWithVenue(Map<String, Object> event) {
        String url = String.valueOf(event.get("url"));
        if (url.isBlank()) return false;
        try {
            String html = get(url);
            JsonNode ldEvent = findEventNode(html);
            if (ldEvent == null) return false;

            String mode = ldEvent.path("eventAttendanceMode").asText("");
            if (!mode.isBlank()) event.put("online", mode.contains("Online"));

            JsonNode location = ldEvent.path("location");
            if (location.isArray()) location = location.isEmpty() ? location.path(0) : location.get(0);
            String venue = text(location.path("name"));
            String city = text(location.path("address").path("addressLocality"));
            if (venue != null) event.put("venue", venue);
            if (city != null) event.put("city", city);
            return venue != null || city != null;
        } catch (Exception e) {
            System.err.println("    no venue for " + url + " (" + e.getMessage() + ")");
            return false;
        }
    }

    static JsonNode findEventNode(String html) throws IOException {
        Matcher m = LD_JSON.matcher(html);
        while (m.find()) {
            JsonNode node;
            try {
                node = JSON.readTree(m.group(1));
            } catch (Exception e) {
                continue;
            }
            for (JsonNode candidate : node.isArray() ? node : JSON.createArrayNode().add(node)) {
                if ("Event".equals(candidate.path("@type").asText())) return candidate;
            }
        }
        return null;
    }

    static String text(JsonNode node) {
        if (node == null || !node.isTextual()) return null;
        String s = node.asText().trim();
        return s.isEmpty() ? null : s;
    }

    // ----------------------------------------------------------------- HTTP --

    static class NotFound extends IOException {
        NotFound(String message) { super(message); }
    }

    /** One GET, with a single retry on a transient failure. */
    static String get(String url) throws IOException, InterruptedException {
        IOException last = null;
        for (int attempt = 1; attempt <= 2; attempt++) {
            Thread.sleep(REQUEST_PAUSE_MS);
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .header("User-Agent", USER_AGENT)
                    .header("Accept", "text/calendar, text/html;q=0.9, */*;q=0.5")
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();
            try {
                HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
                int code = response.statusCode();
                if (code == 200) return response.body();
                if (code == 404 || code == 410) throw new NotFound("HTTP " + code);
                last = new IOException("HTTP " + code);
            } catch (NotFound e) {
                throw e;
            } catch (IOException e) {
                last = e;
            }
            if (attempt == 1) Thread.sleep(2000);
        }
        throw last;
    }

    // ----------------------------------------------------------------- misc --

    /** True when two renderings of data/events.json differ only in generatedAt. */
    static boolean sameEvents(String existing, String fresh) {
        try {
            var a = (com.fasterxml.jackson.databind.node.ObjectNode) JSON.readTree(existing);
            var b = (com.fasterxml.jackson.databind.node.ObjectNode) JSON.readTree(fresh);
            a.remove("generatedAt");
            b.remove("generatedAt");
            return a.equals(b);
        } catch (Exception e) {
            return false;
        }
    }

    static boolean has(String[] args, String flag) {
        return Arrays.asList(args).contains(flag);
    }

    static String arg(String[] args, String flag) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals(flag)) return args[i + 1];
        }
        return null;
    }

    static int intArg(String[] args, String flag, int fallback) {
        String value = arg(args, flag);
        return value == null ? fallback : Integer.parseInt(value);
    }
}
