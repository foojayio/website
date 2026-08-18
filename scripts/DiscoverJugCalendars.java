///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.yaml:snakeyaml:2.2
//JAVA 17+

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reports JUGs that publish a calendar somewhere but whose entry in the
 * upstream World Wide JUGs directory doesn't record it -- so they are absent
 * from /calendar/ even though their events are public.
 *
 * scripts/FetchJugEvents.java only reads `calendar:` and `meetup_slug:` from
 * data/jugs.yaml, and deliberately doesn't go looking: data/jugs.yaml is
 * generated from GlobalWWJugs and a fetcher that scraped a JUG's home page on
 * every run would be both fragile and invisible. But 45 of the 90 JUGs have
 * neither field, and a good few of them do have a Meetup group or an .ics --
 * so this finds them ONCE, by hand, and prints the line to add upstream, where
 * the rest of the directory lives and where every other consumer of it
 * benefits too.
 *
 * Run by hand, like ConvertSponsors.java and for the same reason. It never
 * writes data/jugs.yaml: that file is generated, and a local edit would be
 * overwritten by the next FetchJugs.java run.
 *
 * A candidate is only reported as confident when it VERIFIES: the Meetup slug
 * has to resolve to a real group whose iCal feed loads, and that group's name
 * has to look like this JUG's (its own page linking to three sibling JUGs, or
 * to meetup.com/blog, is exactly the kind of thing that would otherwise land
 * a wrong slug in a community directory). Everything else is printed as
 * "needs a human", never as a suggestion.
 *
 * Usage:
 *   jbang scripts/DiscoverJugCalendars.java              # report
 *   jbang scripts/DiscoverJugCalendars.java --all        # include JUGs that already have a feed
 *   jbang scripts/DiscoverJugCalendars.java --yaml       # print the upstream frontmatter lines
 */
public class DiscoverJugCalendars {

    static final Path JUGS_FILE = Path.of("data/jugs.yaml");
    static final String USER_AGENT =
            "foojay.io-calendar/1.0 (+https://foojay.io/calendar/; one-off JUG calendar discovery)";

    static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    /** meetup.com paths that are Meetup's own site, not somebody's group. */
    static final Set<String> MEETUP_RESERVED = Set.of(
            "about", "apps", "blog", "help", "home", "find", "cities", "topics", "members",
            "login", "register", "pro", "meetup-api", "legal", "jobs", "press", "contact",
            "create", "start", "groups", "events", "online", "search", "explore", "es-es",
            "en-us", "de-de", "fr-fr", "it-it", "pt-br", "nl-nl", "ja-jp", "ko-kr", "pl-pl",
            "tr-tr", "ru-ru", "es", "de", "fr", "it", "pt", "nl", "ja", "ko", "pl", "tr", "ru");

    static final Pattern MEETUP_LINK = Pattern.compile(
            "meetup\\.com/(?:[a-z]{2}-[A-Z]{2}/)?([A-Za-z0-9_-]{3,})", Pattern.CASE_INSENSITIVE);
    static final Pattern ICS_LINK = Pattern.compile(
            "href=[\"']([^\"']+\\.ics(?:\\?[^\"']*)?)[\"']", Pattern.CASE_INSENSITIVE);
    static final Pattern CALENDAR_REL = Pattern.compile(
            "<link[^>]+type=[\"']text/calendar[\"'][^>]*>", Pattern.CASE_INSENSITIVE);
    static final Pattern HREF = Pattern.compile("href=[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE);

    /** Other platforms worth a human look -- each has an iCal export somewhere. */
    static final Map<String, Pattern> PLATFORMS = Map.of(
            "Eventbrite", Pattern.compile("eventbrite\\.[a-z.]+/[eo]/", Pattern.CASE_INSENSITIVE),
            "Luma", Pattern.compile("\\blu\\.ma/[A-Za-z0-9_-]+", Pattern.CASE_INSENSITIVE),
            "Tito", Pattern.compile("\\bti\\.to/[A-Za-z0-9_-]+", Pattern.CASE_INSENSITIVE),
            "Sessionize", Pattern.compile("sessionize\\.com/[A-Za-z0-9_-]+", Pattern.CASE_INSENSITIVE),
            "Bevy", Pattern.compile("bevylabs|\\.bevy\\.com", Pattern.CASE_INSENSITIVE),
            "Mobilizon", Pattern.compile("mobilizon\\.[a-z.]+/@?[A-Za-z0-9_-]+", Pattern.CASE_INSENSITIVE));

    public static void main(String[] args) throws Exception {
        boolean all = has(args, "--all");
        boolean yaml = has(args, "--yaml");

        List<Map<String, Object>> jugs = loadJugs();
        List<Map<String, Object>> targets = jugs.stream()
                .filter(j -> j.get("website") != null)
                .filter(j -> all || (j.get("calendar") == null && j.get("meetup_slug") == null))
                .toList();

        System.out.println(targets.size() + " JUG site(s) to check"
                + (all ? "" : " (those with neither `calendar` nor `meetup_slug`)"));
        System.out.println();

        List<String[]> confident = new ArrayList<>();
        List<String[]> needsHuman = new ArrayList<>();

        for (Map<String, Object> jug : targets) {
            String slug = String.valueOf(jug.get("slug"));
            String name = String.valueOf(jug.getOrDefault("name", slug));
            String site = String.valueOf(jug.get("website"));

            String html;
            try {
                html = get(site);
            } catch (Exception e) {
                System.out.println(pad(slug) + "site unreachable (" + e.getMessage() + ")");
                continue;
            }

            // 1. A calendar the site links to directly, in the format everything speaks.
            String ics = findIcalLink(html, site);
            if (ics != null) {
                String feed = tryFeed(ics);
                if (feed != null) {
                    confident.add(new String[]{slug, name, "calendar", ics, feed});
                    System.out.println(pad(slug) + "iCal feed: " + ics + "  (" + feed + ")");
                    continue;
                }
                needsHuman.add(new String[]{slug, name, "ics-link-dead", ics, ""});
                System.out.println(pad(slug) + "links an .ics that didn't load: " + ics);
            }

            // 2. A Meetup group the site links to. Verified, not assumed.
            List<String> candidates = meetupCandidates(html);
            boolean matched = false;
            for (String candidate : candidates) {
                String groupName = tryFeed(String.format("https://www.meetup.com/%s/events/ical/", candidate));
                if (groupName == null) continue;
                if (looksLikeSameGroup(name, slug, groupName, candidate)) {
                    confident.add(new String[]{slug, name, "meetup", candidate, groupName});
                    System.out.println(pad(slug) + "Meetup group: " + candidate + "  (\"" + groupName + "\")");
                    matched = true;
                    break;
                }
                needsHuman.add(new String[]{slug, name, "meetup-unsure", candidate, groupName});
                System.out.println(pad(slug) + "Meetup group found but the name doesn't match: "
                        + candidate + " (\"" + groupName + "\") vs \"" + name + "\"");
                matched = true;
                break;
            }
            if (matched) continue;

            // 3. Something else entirely -- named, not guessed at.
            List<String> platforms = new ArrayList<>();
            PLATFORMS.forEach((platform, pattern) -> {
                Matcher m = pattern.matcher(html);
                if (m.find()) platforms.add(platform + " (" + m.group() + ")");
            });
            if (!platforms.isEmpty()) {
                needsHuman.add(new String[]{slug, name, "platform", String.join(", ", platforms), ""});
                System.out.println(pad(slug) + "uses " + String.join(", ", platforms)
                        + " -- check whether it exposes an iCal feed");
            } else {
                System.out.println(pad(slug) + "no calendar found");
            }
        }

        System.out.println();
        System.out.println("== " + confident.size() + " verified, "
                + needsHuman.size() + " needing a human ==");

        if (yaml) {
            System.out.println();
            System.out.println("Add upstream, in World-Wide-JUGs/GlobalWWJugs/_jugs/<slug>.md:");
            System.out.println();
            for (String[] row : confident) {
                System.out.println("# _jugs/" + row[0] + ".md  -- " + row[1]);
                if (row[2].equals("meetup")) {
                    System.out.println("meetup:   https://www.meetup.com/" + row[3] + "/");
                    System.out.println("calendar: https://www.meetup.com/" + row[3] + "/events/ical/");
                } else {
                    System.out.println("calendar: " + row[3]);
                }
                System.out.println();
            }
        } else if (!confident.isEmpty()) {
            System.out.println("Re-run with --yaml for the lines to add upstream.");
        }
    }

    // ------------------------------------------------------------ discovery --

    static String findIcalLink(String html, String base) {
        Matcher rel = CALENDAR_REL.matcher(html);
        if (rel.find()) {
            Matcher href = HREF.matcher(rel.group());
            if (href.find()) return absolute(href.group(1), base);
        }
        Matcher ics = ICS_LINK.matcher(html);
        if (ics.find()) return absolute(ics.group(1), base);
        return null;
    }

    static List<String> meetupCandidates(String html) {
        List<String> out = new ArrayList<>();
        Matcher m = MEETUP_LINK.matcher(html);
        while (m.find()) {
            String slug = m.group(1);
            if (MEETUP_RESERVED.contains(slug.toLowerCase(Locale.ROOT))) continue;
            if (!out.contains(slug)) out.add(slug);
        }
        return out;
    }

    /** Fetches a candidate feed; returns its calendar name when it really is one. */
    static String tryFeed(String url) {
        try {
            String body = get(url);
            if (!body.contains("BEGIN:VCALENDAR")) return null;
            for (String line : body.replace("\r\n", "\n").split("\n")) {
                if (line.startsWith("X-WR-CALNAME:")) return line.substring(13).trim();
                if (line.startsWith("NAME:")) return line.substring(5).trim();
            }
            return "unnamed calendar";
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Is the Meetup group this JUG's own? A JUG site links to sibling JUGs and
     * to Meetup's own pages, so a bare "the page mentions meetup.com" is not
     * evidence. Ask for a shared significant word between the two names (or
     * the directory slug), after dropping the words every JUG shares.
     */
    static boolean looksLikeSameGroup(String jugName, String jugSlug, String groupName, String groupSlug) {
        Set<String> a = significantWords(jugName + " " + jugSlug);
        Set<String> b = significantWords(groupName + " " + groupSlug.replace('-', ' '));
        return a.stream().anyMatch(b::contains);
    }

    static final Set<String> STOPWORDS = Set.of(
            "java", "user", "group", "jug", "the", "community", "usergroup", "users", "org",
            "de", "of", "and", "club", "developers", "developer", "meetup");

    static Set<String> significantWords(String s) {
        Set<String> out = new LinkedHashSet<>();
        // "KnoxvilleJUG" / "NYJavaSIG" -> split camel case as well as spaces.
        String spaced = s.replaceAll("([a-z])([A-Z])", "$1 $2").toLowerCase(Locale.ROOT);
        for (String word : spaced.split("[^a-z0-9]+")) {
            if (word.length() >= 3 && !STOPWORDS.contains(word)) out.add(word);
        }
        return out;
    }

    static String absolute(String href, String base) {
        try {
            return URI.create(base).resolve(href).toString();
        } catch (Exception e) {
            return href;
        }
    }

    // ----------------------------------------------------------------- util --

    static String get(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("User-Agent", USER_AGENT)
                .timeout(Duration.ofSeconds(25))
                .GET()
                .build();
        HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new IOException("HTTP " + response.statusCode());
        return response.body();
    }

    @SuppressWarnings("unchecked")
    static List<Map<String, Object>> loadJugs() throws IOException {
        Yaml yaml = new Yaml();
        try (var in = Files.newInputStream(JUGS_FILE)) {
            return (List<Map<String, Object>>) yaml.load(in);
        }
    }

    static String pad(String slug) {
        return String.format("%-24s", slug);
    }

    static boolean has(String[] args, String flag) {
        return Arrays.asList(args).contains(flag);
    }
}
