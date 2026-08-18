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
 * Pulls upcoming events for every JUG in data/jugs.yaml that publishes a
 * calendar, and writes data/jug-events.json for the calendar page at /calendar/.
 * Runs once a day from .github/workflows/sync-external-content.yml, which runs
 * FetchJugs.java first so this always sees the current upstream JUG list.
 *
 * NOT MEETUP-SPECIFIC, and NO CREDENTIAL IS NEEDED. It was
 * FetchMeetupEvents.java, POSTing to Meetup's GraphQL API behind a paid Meetup
 * Pro subscription; then only the ~32 JUGs on Meetup could ever appear. But a
 * JUG's calendar is a calendar: of the 90 JUGs in the directory, 30 record a
 * `calendar:` URL and every one of those is an **iCal feed** -- Google Calendar
 * (4), a file on the JUG's own site (5), or Meetup's own iCal export. So iCal
 * is the generic system, and this script speaks iCal to whatever a JUG
 * publishes:
 *
 *   1. `calendar:` from data/jugs.yaml, whatever hosts it. Google Calendar,
 *      jug-da.de/events.ics, a GitLab-Pages file -- all the same format, and
 *      the same format Luma, Eventbrite, Tito, Bevy and Mobilizon export too.
 *   2. Otherwise `meetup_slug:` -> https://www.meetup.com/<slug>/events/ical/,
 *      the feed a member subscribes to from their calendar app.
 *
 * Venues come from the feed's LOCATION property where there is one (the
 * self-hosted and Google feeds carry it; Meetup's export does NOT), and
 * otherwise from the schema.org JSON-LD `Event` block on the event's own page
 * -- the format Meetup, Eventbrite, Luma and WordPress all publish for search
 * engines. That pass is best-effort: a page that fails or won't parse leaves
 * venue/city null and keeps the event.
 *
 * Both sources are public and machine-readable, and meetup.com's robots.txt
 * permits the iCal route (it disallows the rss/atom/xml variants of the same
 * path) and lists event pages in its own sitemap -- checked, not assumed.
 * Requests identify themselves as foojay.io (see USER_AGENT) rather than
 * posing as a browser, run one at a time with a pause between them, and the
 * calendar links every event back to its source page.
 *
 * A JUG with neither field is skipped, not guessed at: scripts/DiscoverJugCalendars.java
 * reports the ones whose own website advertises a calendar or a Meetup group
 * so the missing field can be fixed upstream in GlobalWWJugs, where the rest
 * of data/jugs.yaml comes from.
 *
 * Usage:
 *   jbang scripts/FetchJugEvents.java                  # full sync
 *   jbang scripts/FetchJugEvents.java --dry-run        # report, write nothing
 *   jbang scripts/FetchJugEvents.java --limit 3        # first 3 JUGs only
 *   jbang scripts/FetchJugEvents.java --jug DarmstadtJUG
 *   jbang scripts/FetchJugEvents.java --no-venues      # skip the JSON-LD pass
 */
public class FetchJugEvents {

    static final Path JUGS_FILE = Path.of("data/jugs.yaml");
    static final Path OUTPUT_FILE = Path.of("data/jug-events.json");

    /** How many upcoming events to keep per JUG. Google feeds hold years. */
    static final int EVENTS_PER_GROUP = 10;

    /** Attributable rather than disguised: who this is, and where to complain. */
    static final String USER_AGENT =
            "foojay.io-calendar/1.0 (+https://foojay.io/calendar/; Java User Group events, refreshed daily)";

    /** Pause between requests. */
    static final long REQUEST_PAUSE_MS = 300;

    static final String MEETUP_ICAL = "https://www.meetup.com/%s/events/ical/";
    static final String MEETUP_GROUP = "https://www.meetup.com/%s/";

    static final ObjectMapper JSON = new ObjectMapper();
    static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    static final Pattern LD_JSON = Pattern.compile(
            "<script[^>]+type=\"application/ld\\+json\"[^>]*>(.*?)</script>",
            Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    /** "64289 Darmstadt" -> "Darmstadt": a German/most-European postcode prefix. */
    static final Pattern POSTCODE = Pattern.compile("^[A-Z]{0,2}[-\\s]?\\d{3,6}\\s+");

    public static void main(String[] args) throws Exception {
        boolean dryRun = has(args, "--dry-run");
        boolean withVenues = !has(args, "--no-venues");
        int limit = intArg(args, "--limit", Integer.MAX_VALUE);
        String onlyJug = arg(args, "--jug");

        List<Map<String, Object>> jugs = loadJugs();
        List<Source> sources = resolveSources(jugs);
        if (onlyJug != null) {
            sources = sources.stream().filter(s -> onlyJug.equalsIgnoreCase(s.jugSlug)).toList();
            if (sources.isEmpty()) {
                System.err.println("No JUG in " + JUGS_FILE + " named " + onlyJug + " publishes a calendar");
                System.exit(1);
            }
        }
        long viaCalendar = sources.stream().filter(s -> s.kind.equals("calendar")).count();
        System.out.println(jugs.size() + " JUGs in " + JUGS_FILE + ", " + sources.size()
                + " with a calendar (" + viaCalendar + " own feed, " + (sources.size() - viaCalendar) + " Meetup)");

        List<Map<String, Object>> allGroups = new ArrayList<>();
        int events = 0, venues = 0, failed = 0;

        for (Source source : sources) {
            if (allGroups.size() >= limit) break;

            Map<String, Object> group = new LinkedHashMap<>();
            group.put("slug", source.slug());
            group.put("name", source.name);
            group.put("jug", source.jugSlug);
            group.put("source", source.kind);

            try {
                String ics = get(source.feedUrl);
                if (!looksLikeIcal(ics)) {
                    throw new IOException("not an iCal feed (no VCALENDAR) at " + source.feedUrl);
                }
                String calName = calendarName(ics);
                if (source.kind.equals("meetup") && calName != null && !calName.isBlank()) {
                    // Meetup's own group name is better than the directory's.
                    group.put("name", calName);
                }
                group.put("url", source.groupUrl());

                List<Map<String, Object>> groupEvents = new ArrayList<>();
                int recurring = 0;
                for (Map<String, String> ve : parseVEvents(ics)) {
                    if (ve.containsKey("RRULE")) recurring++;
                    Map<String, Object> event = toEvent(ve);
                    if (event != null) groupEvents.add(event);
                }
                if (recurring > 0) {
                    // Nothing in the feeds seen so far repeats, so rather than
                    // implement RRULE expansion on speculation: say it out loud
                    // if a feed ever starts using it, since only the first
                    // occurrence would be listed.
                    System.out.println("    note: " + recurring + " recurring event(s), only the first occurrence is listed");
                }
                groupEvents.sort(Comparator.comparing(e -> String.valueOf(e.get("startTime"))));
                if (groupEvents.size() > EVENTS_PER_GROUP) {
                    groupEvents = new ArrayList<>(groupEvents.subList(0, EVENTS_PER_GROUP));
                }

                if (withVenues) {
                    for (Map<String, Object> event : groupEvents) {
                        if (event.get("venue") == null && enrichWithVenue(event)) venues++;
                        else if (event.get("venue") != null) venues++;
                    }
                }

                group.put("events", groupEvents);
                events += groupEvents.size();
                System.out.println("  " + source.jugSlug + " (" + source.kind + "): "
                        + groupEvents.size() + " upcoming event(s)");
            } catch (NotFound e) {
                // Not a fetch failure: the URL in the upstream directory is
                // wrong, or the group/feed was renamed or removed. Say which,
                // so the calendar's note and the JUG lead can act on it.
                group.put("error", (source.kind.equals("meetup")
                        ? "Meetup group not found (404)"
                        : "calendar feed not found (404): " + source.feedUrl)
                        + " -- the entry in GlobalWWJugs is out of date");
                failed++;
                System.err.println("  " + source.jugSlug + " (" + source.kind + "): NOT FOUND -- " + source.feedUrl);
            } catch (Exception e) {
                group.put("error", e.getMessage());
                failed++;
                System.err.println("  " + source.jugSlug + " (" + source.kind + "): FAILED -- " + e.getMessage());
            }
            allGroups.add(group);
        }

        Map<String, Object> output = new LinkedHashMap<>();
        output.put("generatedAt", DateTimeFormatter.ISO_INSTANT.format(
                Instant.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS)));
        output.put("source", "iCal feeds published by the JUGs themselves (data/jugs.yaml `calendar`) "
                + "and Meetup's iCal export for the groups that use it; venues from the feed's LOCATION "
                + "or the schema.org JSON-LD on the event page -- see scripts/FetchJugEvents.java");
        output.put("groups", allGroups);

        String json = JSON.writerWithDefaultPrettyPrinter().writeValueAsString(output) + "\n";
        System.out.println(events + " events across " + allGroups.size() + " groups"
                + (withVenues ? ", " + venues + " with a venue" : "")
                + (failed > 0 ? ", " + failed + " feed(s) unavailable" : ""));

        if (dryRun || onlyJug != null || limit != Integer.MAX_VALUE) {
            // A partial run would drop every group it didn't ask for, so print
            // what was fetched instead of writing a truncated file.
            System.out.println(json);
            System.out.println(dryRun ? "--dry-run: " + OUTPUT_FILE + " not written"
                    : "--jug/--limit run: " + OUTPUT_FILE + " not written");
            return;
        }

        // Only rewrite when the events themselves changed. `generatedAt` moves
        // on every run, and writing it unconditionally would commit -- and so
        // deploy -- on a timestamp.
        if (Files.exists(OUTPUT_FILE) && sameEvents(Files.readString(OUTPUT_FILE), json)) {
            System.out.println("No event changes -- " + OUTPUT_FILE + " left untouched");
            return;
        }
        Files.createDirectories(OUTPUT_FILE.getParent());
        Files.writeString(OUTPUT_FILE, json);
        System.out.println("Wrote " + OUTPUT_FILE);
    }

    // --------------------------------------------------------------- source --

    /** One JUG's calendar: where to fetch it, and what kind of thing it is. */
    record Source(String jugSlug, String name, String kind, String feedUrl,
                  String meetupSlug, String website) {
        String slug() { return meetupSlug != null ? meetupSlug : jugSlug; }
        String groupUrl() {
            if (meetupSlug != null) return String.format(MEETUP_GROUP, meetupSlug);
            return website == null ? "" : website;
        }
    }

    /**
     * An explicit `calendar:` wins over `meetup_slug:` -- it is what the JUG
     * chose to publish, and for a Meetup group it usually IS the Meetup feed.
     *
     * Two JUGs pointing at one feed is an upstream mistake (one JUG's entry
     * naming another's calendar), and left unhandled it would list the same
     * events twice under two names. Keep the first and report the rest rather
     * than quietly duplicating.
     */
    static List<Source> resolveSources(List<Map<String, Object>> jugs) {
        List<Source> sources = new ArrayList<>();
        Map<String, String> seenFeeds = new HashMap<>();
        for (Map<String, Object> jug : jugs) {
            String jugSlug = str(jug.get("slug"));
            String name = str(jug.getOrDefault("name", jugSlug));
            String calendar = str(jug.get("calendar"));
            String meetupSlug = str(jug.get("meetup_slug"));
            String website = str(jug.get("website"));

            String feedUrl;
            String kind;
            if (calendar != null) {
                // A meetup.com calendar URL is normalised to the iCal export:
                // the directory has one entry pointing at /events/calendar/,
                // Meetup's HTML page, and another carrying a /de-DE/ locale
                // prefix. Both are the same feed under a different URL, and
                // this is the one platform whose URL shape we already know.
                feedUrl = calendar.contains("meetup.com")
                        ? String.format(MEETUP_ICAL, meetupSlug != null ? meetupSlug : meetupSlugFromUrl(calendar))
                        : calendar;
                kind = calendar.contains("meetup.com") ? "meetup" : "calendar";
            } else if (meetupSlug != null) {
                feedUrl = String.format(MEETUP_ICAL, meetupSlug);
                kind = "meetup";
            } else {
                continue;
            }

            String owner = seenFeeds.putIfAbsent(feedUrl, jugSlug);
            if (owner != null) {
                System.err.println("  " + jugSlug + ": skipped -- its calendar is the same feed as "
                        + owner + "'s (" + feedUrl + "); fix one of them in GlobalWWJugs");
                continue;
            }
            // Only claim a Meetup identity when the feed really is Meetup's.
            String effectiveMeetupSlug = feedUrl.contains("meetup.com")
                    ? (meetupSlug != null ? meetupSlug : meetupSlugFromUrl(feedUrl))
                    : null;
            sources.add(new Source(jugSlug, name, kind, feedUrl, effectiveMeetupSlug, website));
        }
        return sources;
    }

    static String meetupSlugFromUrl(String url) {
        Matcher m = Pattern.compile("meetup\\.com/(?:[a-z]{2}-[A-Z]{2}/)?([^/]+)/").matcher(url);
        return m.find() ? m.group(1) : null;
    }

    @SuppressWarnings("unchecked")
    static List<Map<String, Object>> loadJugs() throws IOException {
        Yaml yaml = new Yaml();
        try (var in = Files.newInputStream(JUGS_FILE)) {
            return (List<Map<String, Object>>) yaml.load(in);
        }
    }

    // ----------------------------------------------------------------- iCal --

    static boolean looksLikeIcal(String body) {
        return body != null && body.contains("BEGIN:VCALENDAR");
    }

    /**
     * Splits a VCALENDAR into its VEVENTs, each as property name -> raw value,
     * with any property parameters kept under "NAME.params" as well so the
     * caller can read DTSTART's TZID. Line folding (RFC 5545: a continuation
     * line starts with a space or tab) is undone first.
     */
    static List<Map<String, String>> parseVEvents(String ics) {
        List<String> lines = unfold(ics);
        List<Map<String, String>> events = new ArrayList<>();
        Map<String, String> current = null;
        for (String line : lines) {
            if (line.startsWith("BEGIN:VEVENT")) {
                current = new LinkedHashMap<>();
            } else if (line.startsWith("END:VEVENT")) {
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

    /** The calendar's own display name, where the feed carries one. */
    static String calendarName(String ics) {
        for (String line : unfold(ics)) {
            if (line.startsWith("X-WR-CALNAME:")) return unescapeText(line.substring("X-WR-CALNAME:".length()));
            if (line.startsWith("NAME:")) return unescapeText(line.substring("NAME:".length()));
        }
        return null;
    }

    /** One VEVENT -> the event shape data/jug-events.json holds, or null to skip it. */
    static Map<String, Object> toEvent(Map<String, String> ve) {
        if (ve.getOrDefault("STATUS", "").equalsIgnoreCase("CANCELLED")) return null;

        OffsetDateTime start = parseIcalDate(ve.get("DTSTART"), ve.get("DTSTART.params"));
        if (start == null) return null;
        // A JUG's own feed is its whole history -- one Google Calendar here
        // holds 170 events back to 2014 -- so this filter is what makes it an
        // upcoming-events list at all.
        if (start.isBefore(OffsetDateTime.now().minusDays(1))) return null;
        OffsetDateTime end = parseIcalDate(ve.get("DTEND"), ve.get("DTEND.params"));

        Map<String, Object> event = new LinkedHashMap<>();
        event.put("title", unescapeText(ve.getOrDefault("SUMMARY", "Untitled event")));
        event.put("url", ve.getOrDefault("URL", ""));
        event.put("startTime", start.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        event.put("endTime", end == null ? null : end.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        String[] place = splitLocation(unescapeText(ve.get("LOCATION")));
        event.put("venue", place[0]);
        event.put("city", place[1]);
        event.put("online", null);
        return event;
    }

    /**
     * iCal LOCATION is one free-text line, and in practice it is a postal
     * address: "DICOS GmbH, Alsfelder Straße 11, 64289 Darmstadt". The venue
     * is the first part and the city the last, with its postcode dropped;
     * anything with no separator at all stays whole rather than being guessed
     * at. Semicolons count as separators too -- one feed writes its address
     * "Öffentliche Versicherung Braunschweig; Theodor-Heuss-Straße 10; 38122
     * Braunschweig", which on a comma-only split would put the whole thing in
     * the venue.
     */
    static String[] splitLocation(String location) {
        if (location == null || location.isBlank()) return new String[]{null, null};
        String[] parts = Arrays.stream(location.split("[,;]"))
                .map(String::trim).filter(s -> !s.isEmpty()).toArray(String[]::new);
        if (parts.length == 0) return new String[]{null, null};
        if (parts.length == 1) return new String[]{parts[0], null};
        String city = POSTCODE.matcher(parts[parts.length - 1]).replaceFirst("").trim();
        return new String[]{parts[0], city.isEmpty() ? null : city};
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
     * Reads the venue out of the event page's schema.org JSON-LD -- the format
     * Meetup, Eventbrite, Luma and WordPress all publish for search engines.
     * Only called for events whose feed carried no LOCATION (i.e. every Meetup
     * one, since its iCal export has no LOCATION property at all).
     *
     * Best-effort by design: anything that goes wrong leaves venue/city null
     * and keeps the event, which is how the calendar renders an online or
     * venue-less event anyway. Returns true when a venue was found.
     */
    static boolean enrichWithVenue(Map<String, Object> event) {
        String url = String.valueOf(event.get("url"));
        if (url.isBlank()) return false;
        try {
            JsonNode ldEvent = findEventNode(get(url));
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

    static JsonNode findEventNode(String html) {
        Matcher m = LD_JSON.matcher(html);
        while (m.find()) {
            JsonNode node;
            try {
                node = JSON.readTree(m.group(1));
            } catch (Exception e) {
                continue;
            }
            for (JsonNode candidate : node.isArray() ? node : JSON.createArrayNode().add(node)) {
                if (candidate.path("@type").asText().endsWith("Event")) return candidate;
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

    /** True when two renderings of data/jug-events.json differ only in generatedAt. */
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

    static String str(Object o) {
        if (o == null) return null;
        String s = String.valueOf(o).trim();
        return s.isEmpty() ? null : s;
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
