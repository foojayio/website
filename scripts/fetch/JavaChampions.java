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
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;

/**
 * Regenerates data/java-champions.yaml from aalmiray/java-champions's
 * java-champions.yml (https://github.com/aalmiray/java-champions), a single
 * YAML file listing every Java Champion -- this is the data behind
 * https://javachampions.org/. Run at every deploy
 * (.github/workflows/build-deploy.yml) and once a day
 * (.github/workflows/sync-external-content.yml) -- both commit the refreshed file
 * back to main, same pattern as data/jugs.yaml.
 *
 * Champions add/update their OWN entry by editing java-champions.yml
 * directly upstream (see that repo's CONTRIBUTING.adoc), not this repo --
 * same reasoning as pulling the JUG list from GlobalWWJugs instead of
 * maintaining our own copy.
 *
 * COORDINATES, for the world map on /java-champions/. Three sources, in order:
 *
 *   1. An upstream `location: {lat, lon}` on the member itself. This is what
 *      aalmiray/java-champions#318 adds; until it merges no entry has one, and
 *      once it does every entry that has one costs us no request at all. Same
 *      self-retiring shape as the transfer/LegacyViews.java view-count bridge:
 *      the better source wins automatically, with nothing here to switch off.
 *   2. data/geocode-cache.yaml, keyed by the LOOKUP STRING ("Toronto, Canada")
 *      rather than by champion. That is what makes this cheap enough to run on
 *      every deploy: 422 champions share only 252 distinct places, so the 22
 *      champions in "USA" and the 16 in "London, UK" are one lookup each, and
 *      renaming a champion or editing their socials costs nothing. Committed,
 *      like data/legacy-views.json, because it is the only copy.
 *   3. geocode.maps.co, for cache misses only -- i.e. genuinely new or moved
 *      champions, normally none. Free tier is 5000 requests/day at 1/sec, so a
 *      full cold run (252 lookups, ~5 minutes) fits inside one day's quota with
 *      room to spare, and every run after it does nothing. Needs GEOCODE_API_KEY.
 *
 * Note the coordinate is a CITY (or, for the 97 champions with no city, a
 * whole country) -- it is not anybody's address, and /java-champions/ says so.
 * That is also why one marker there covers everyone in a place rather than
 * stacking 16 markers on the same point.
 *
 * NEVER FAILS over geocoding. No API key, an unreachable geocoder, an exhausted
 * quota: the run still writes every champion, just without coordinates for the
 * ones not yet cached. Both workflows that run this commit their result, so a
 * hard failure here would block a deploy over a map.
 *
 * Usage:
 *   jbang scripts/fetch/JavaChampions.java
 *   GEOCODE_API_KEY=... jbang scripts/fetch/JavaChampions.java
 *   jbang scripts/fetch/JavaChampions.java --geocode-key <key>
 *   jbang scripts/fetch/JavaChampions.java --geocode-limit 50   # cap new lookups
 *   jbang scripts/fetch/JavaChampions.java --no-geocode         # cache only
 */
public class JavaChampions {

    static final String SOURCE_URL = "https://raw.githubusercontent.com/aalmiray/java-champions/main/java-champions.yml";
    static final String EDIT_URL = "https://github.com/aalmiray/java-champions/edit/main/java-champions.yml";
    // Avatar paths in the source (e.g. "img/avatars/aalmiray.png") are
    // relative to the published site root, not the repo -- javachampions.org
    // is also the more stable long-term host (repo file layout is an
    // implementation detail of their JBake build, the published URL isn't).
    // Not every entry is relative, though: see avatarUrl below.
    static final String AVATAR_BASE = "https://javachampions.org/";

    static final Path OUTPUT_FILE = Path.of("data/java-champions.yaml");
    static final Path CACHE_FILE = Path.of("data/geocode-cache.yaml");

    static final String GEOCODE_URL = "https://geocode.maps.co/search";
    static final String GEOCODE_KEY_ENV = "GEOCODE_API_KEY";
    // The free tier allows one request per second; a little over that costs
    // nothing and keeps us clear of being throttled on a burst.
    static final Duration GEOCODE_PAUSE = Duration.ofMillis(1100);
    // A ceiling on NEW lookups per run, not a target -- a normal run does a
    // handful. High enough that the one cold run (252 today) completes in a
    // single pass, low enough that a mangled upstream file can't burn the
    // 5000/day quota.
    static final int DEFAULT_GEOCODE_LIMIT = 500;
    // Enough consecutive failures to mean "the geocoder is unreachable" rather
    // than "one request was unlucky".
    static final int MAX_CONSECUTIVE_FAILURES = 5;

    static final List<String> SOCIAL_FIELDS = List.of(
            "twitter", "mastodon", "bluesky", "youtube", "linkedin", "github",
            "website", "sessionize", "xing", "speakerdeck"
    );

    static final ObjectMapper JSON = new ObjectMapper();
    static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();

    /** A resolved place. Null anywhere a Coords is expected means "looked up, not found". */
    record Coords(double latitude, double longitude) {}

    public static void main(String[] args) throws Exception {
        String geocodeKey = System.getenv(GEOCODE_KEY_ENV);
        int geocodeLimit = DEFAULT_GEOCODE_LIMIT;
        boolean geocode = true;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--no-geocode" -> geocode = false;
                case "--geocode-key" -> geocodeKey = args[++i];
                case "--geocode-limit" -> geocodeLimit = Integer.parseInt(args[++i]);
                default -> {
                    System.err.println("Unknown option: " + args[i]);
                    System.exit(2);
                }
            }
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SOURCE_URL))
                .timeout(Duration.ofSeconds(20))
                .build();
        HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("HTTP " + response.statusCode() + " fetching " + SOURCE_URL);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> root = new Yaml().load(response.body());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rawMembers = (List<Map<String, Object>>) root.get("members");
        System.out.println("Found " + rawMembers.size() + " Java Champions in the source file");

        // A place is either resolved (Coords) or definitively unresolvable
        // (null value, still a key). containsKey is therefore "we have already
        // asked", which is what stops an unresolvable city being re-queried on
        // every run for ever.
        Map<String, Coords> cache = loadCache();
        String cacheBefore = renderCache(cache);
        int cachedBefore = cache.size();

        List<Map<String, Object>> champions = new ArrayList<>();
        // Kept alongside each champion so the geocoding pass below doesn't have
        // to re-derive it from the already-converted (flattened) map.
        List<String> queries = new ArrayList<>();
        int fromUpstream = 0;

        for (Map<String, Object> raw : rawMembers) {
            Map<String, Object> champion = convert(raw);
            if (champion == null) continue;
            champions.add(champion);

            Coords upstream = upstreamCoords(raw);
            if (upstream != null) {
                put(champion, upstream);
                queries.add(null);
                fromUpstream++;
            } else {
                queries.add(locationQuery(raw));
            }
        }

        if (fromUpstream > 0) {
            System.out.println(fromUpstream + " champions carry upstream coordinates (no lookup needed)");
        }

        // Everything we still need a place for, deduplicated, in first-seen
        // order so a partial run (--geocode-limit, or the quota running out)
        // makes progress from the top of the list rather than at random.
        LinkedHashSet<String> wanted = new LinkedHashSet<>();
        for (String q : queries) {
            if (q != null && !cache.containsKey(q)) wanted.add(q);
        }

        if (wanted.isEmpty()) {
            System.out.println("No new places to look up" + (cachedBefore == 0 ? "" : " (" + cachedBefore + " cached)"));
        } else if (!geocode) {
            System.out.println("--no-geocode: " + wanted.size() + " new place(s) left without coordinates");
        } else if (geocodeKey == null || geocodeKey.isBlank()) {
            System.out.println("No " + GEOCODE_KEY_ENV + " set: " + wanted.size() + " new place(s) left without"
                    + " coordinates (cached places are unaffected). Get a free key at https://geocode.maps.co");
        } else {
            geocodeAll(wanted, cache, geocodeKey, geocodeLimit);
        }

        // Fill in from the cache AFTER the lookup pass, so a place resolved
        // just now lands on every champion sharing it.
        int located = fromUpstream;
        for (int i = 0; i < champions.size(); i++) {
            String q = queries.get(i);
            if (q == null) continue;
            Coords coords = cache.get(q);
            if (coords != null) {
                put(champions.get(i), coords);
                located++;
            }
        }

        // Named on EVERY run, not only the run that discovered them. A place
        // cached as `found: false` is never queried again, so without this the
        // two champions it costs would drop off the map silently and for good --
        // and the fix is upstream (both misses today are malformed source
        // strings: a "South Holland Province" Nominatim can't parse, and a
        // "Zhytomyr/Limassol" naming two cities with a slash). Same posture as
        // fetch/DiscoverJugCalendars.java printing its near-misses rather than
        // guessing at them.
        List<String> unresolved = new ArrayList<>();
        for (int i = 0; i < champions.size(); i++) {
            String q = queries.get(i);
            if (q != null && cache.containsKey(q) && cache.get(q) == null) unresolved.add(q);
        }
        if (!unresolved.isEmpty()) {
            System.out.println("Not on the map -- the geocoder knows nowhere by these names, fix them upstream in"
                    + " java-champions.yml (or delete the entry from " + CACHE_FILE + " to retry):");
            new TreeSet<>(unresolved).forEach(q -> System.out.println("  - " + q));
        }

        champions.sort(Comparator.comparing(c -> String.valueOf(c.get("name")), String.CASE_INSENSITIVE_ORDER));
        System.out.println("Parsed " + champions.size() + " Java Champions ("
                + located + " with coordinates), writing " + OUTPUT_FILE);

        writeYaml(champions);

        // Only rewrite the cache when it actually changed, so an unchanged run
        // leaves nothing for the workflow to commit -- same rule as
        // data/jug-events.json's generatedAt.
        String cacheAfter = renderCache(cache);
        if (!cacheAfter.equals(cacheBefore)) {
            Files.createDirectories(CACHE_FILE.getParent());
            Files.writeString(CACHE_FILE, cacheAfter);
            System.out.println("Updated " + CACHE_FILE + " (" + cachedBefore + " -> " + cache.size() + " places)");
        }
    }

    // ---------------------------------------------------------------- convert

    @SuppressWarnings("unchecked")
    static Map<String, Object> convert(Map<String, Object> raw) {
        String name = trimToNull(raw.get("name"));
        if (name == null) return null;

        Map<String, Object> champion = new LinkedHashMap<>();
        champion.put("name", name);
        if (raw.get("year") != null) champion.put("year", raw.get("year"));

        Object countryObj = raw.get("country");
        if (countryObj instanceof Map) {
            Map<String, Object> country = (Map<String, Object>) countryObj;
            putIfPresent(champion, "country", country.get("nomination"));
            putIfPresent(champion, "country_residence", country.get("residence"));
            putIfPresent(champion, "country_citizenship", country.get("citizenship"));
            putIfPresent(champion, "country_birth", country.get("birth"));
        }

        putIfPresent(champion, "city", raw.get("city"));

        Object socialObj = raw.get("social");
        if (socialObj instanceof Map) {
            Map<String, Object> social = (Map<String, Object>) socialObj;
            for (String field : SOCIAL_FIELDS) {
                putIfPresent(champion, field, social.get(field));
            }
        }

        String avatarPath = trimToNull(raw.get("avatar"));
        if (avatarPath != null) {
            champion.put("avatar", avatarUrl(avatarPath));
        }

        Object statusObj = raw.get("status");
        if (statusObj instanceof List && !((List<?>) statusObj).isEmpty()) {
            champion.put("status", statusObj);
        }

        return champion;
    }

    /**
     * Resolves an upstream {@code avatar:} value to a URL that works on an
     * https page.
     *
     * <p>Almost every entry is a path relative to the published site root
     * ({@code img/avatars/aalmiray.png}), but one records an ABSOLUTE url of
     * its own -- and prefixing {@link #AVATAR_BASE} blindly turned that into
     * {@code https://javachampions.org/http://i.picasion.com/...}, which 404s.
     * Of the 422 avatars that was the only one broken by anything on our side.
     *
     * <p>An absolute {@code http://} url is upgraded to {@code https://},
     * because the page is served over https and a browser blocks a
     * mixed-content image outright -- so passing it through unchanged would
     * trade a 404 for a silently blocked request. If the host turns out to
     * have no TLS the image fails either way, and the empty avatar circle in
     * the CSS covers that the same way it covers an avatar deleted upstream.
     */
    static String avatarUrl(String value) {
        if (value.startsWith("https://")) {
            return value;
        }
        if (value.startsWith("http://")) {
            return "https://" + value.substring("http://".length());
        }
        return AVATAR_BASE + value.replaceAll("^/+", "");
    }

    /**
     * The place string to look up: "<city>, <country>", or just the country
     * when a champion records no city (97 of 422 today).
     *
     * Deliberately IDENTICAL to what aalmiray/java-champions#318's
     * onetimeAddLocations.java builds -- residence first, falling back to the
     * country of nomination -- so our coordinates and the ones upstream would
     * store for the same champion agree, and switching to theirs when the PR
     * merges doesn't visibly move anybody on the map.
     */
    static String locationQuery(Map<String, Object> raw) {
        String residence = null, nomination = null;
        if (raw.get("country") instanceof Map<?, ?> country) {
            residence = trimToNull(country.get("residence"));
            nomination = trimToNull(country.get("nomination"));
        }
        String country = residence != null ? residence : nomination;
        if (country == null) return null;

        String city = trimToNull(raw.get("city"));
        return city == null ? country : city + ", " + country;
    }

    /** Reads aalmiray/java-champions#318's `location: {lat, lon}`, when it exists. */
    static Coords upstreamCoords(Map<String, Object> raw) {
        if (!(raw.get("location") instanceof Map<?, ?> location)) return null;
        Double lat = firstNumber(location, "lat", "latitude");
        Double lng = firstNumber(location, "lon", "lng", "longitude");
        return valid(lat, lng) ? new Coords(lat, lng) : null;
    }

    static Double firstNumber(Map<?, ?> map, String... keys) {
        for (String key : keys) {
            Double value = toDouble(map.get(key));
            if (value != null) return value;
        }
        return null;
    }

    static void put(Map<String, Object> champion, Coords coords) {
        champion.put("latitude", coords.latitude());
        champion.put("longitude", coords.longitude());
    }

    // ---------------------------------------------------------------- geocode

    static void geocodeAll(LinkedHashSet<String> wanted, Map<String, Coords> cache, String key, int limit) {
        int todo = Math.min(wanted.size(), limit);
        if (todo < wanted.size()) {
            System.out.println("Looking up " + todo + " of " + wanted.size()
                    + " new place(s) this run (--geocode-limit " + limit + "); the rest follow on the next run");
        } else {
            System.out.println("Looking up " + todo + " new place(s) at ~1/sec"
                    + (todo > 30 ? " -- about " + Math.round(todo * 1.1 / 60.0) + " min" : ""));
        }

        int done = 0, found = 0, missing = 0, failed = 0, consecutiveFailures = 0;
        for (String query : wanted) {
            if (done >= limit) break;
            if (done > 0) sleep(GEOCODE_PAUSE);
            done++;

            Lookup lookup = geocode(query, key);
            if (lookup.abort()) {
                // A bad key or an exhausted quota: every remaining lookup would
                // fail the same way. Stop asking, and cache NOTHING from it -- a
                // failure to ask must never be recorded as "this place does not
                // exist".
                System.err.println("Stopping geocoding for this run: " + lookup.reason());
                break;
            }
            if (lookup.coords() != null) {
                cache.put(query, lookup.coords());
                found++;
                consecutiveFailures = 0;
            } else if (lookup.definitive()) {
                // The geocoder answered and knows nowhere by that name. Record
                // the miss so we don't ask again daily for ever; delete the
                // entry (or the whole file) to force a retry.
                cache.put(query, null);
                missing++;
                consecutiveFailures = 0;
                System.out.println("  no match for \"" + query + "\" -- recorded, will not be retried");
            } else {
                failed++;
                System.err.println("  lookup failed for \"" + query + "\": " + lookup.reason() + " -- will retry next run");
                // A RUN of failures is a geocoder that is down, and worth
                // stopping for; a single one is a blip, and stopping on it would
                // abandon a cold run 200 places in for nothing.
                if (++consecutiveFailures >= MAX_CONSECUTIVE_FAILURES) {
                    System.err.println("Stopping geocoding for this run: " + consecutiveFailures
                            + " lookups in a row failed -- the geocoder looks unreachable");
                    break;
                }
            }
        }
        System.out.println("Geocoded " + found + " place(s), " + missing + " with no match"
                + (failed == 0 ? "" : ", " + failed + " to retry next run"));
    }

    /**
     * One lookup's outcome. The three-way split is the point: {@code coords}
     * resolved, {@code definitive} means the geocoder answered and found
     * nothing (cache it), and neither means we simply failed to ask properly
     * (don't cache it). Same distinction fetch/JugEvents.java draws between a
     * 404 -- a real "not found" a JUG lead can fix -- and a fetch error.
     */
    record Lookup(Coords coords, boolean definitive, boolean abort, String reason) {
        static Lookup found(Coords c) { return new Lookup(c, false, false, null); }
        static Lookup none() { return new Lookup(null, true, false, null); }
        static Lookup failed(String why) { return new Lookup(null, false, false, why); }
        static Lookup abort(String why) { return new Lookup(null, false, true, why); }
    }

    static Lookup geocode(String query, String key) {
        String url = GEOCODE_URL
                + "?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8)
                + "&api_key=" + URLEncoder.encode(key, StandardCharsets.UTF_8);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    // Identify ourselves rather than pose as a browser, same as
                    // fetch/JugEvents.java.
                    .header("User-Agent", "foojay-website-champions-sync (+https://foojay.io)")
                    .timeout(Duration.ofSeconds(20))
                    .build();
            HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();

            if (status == 401 || status == 403) {
                // Every remaining lookup would fail the same way, so don't make
                // 251 more requests to find that out.
                return Lookup.abort("HTTP " + status + " -- " + GEOCODE_KEY_ENV + " is missing, wrong or expired");
            }
            if (status == 429) {
                return Lookup.abort("HTTP 429 -- rate limited or the daily quota is used up");
            }
            if (status != 200) {
                // 5xx included: one flaky response shouldn't abandon a cold run
                // that is 200 places in. geocodeAll gives up after a RUN of
                // these instead, which is what a geocoder genuinely being down
                // looks like.
                return Lookup.failed("HTTP " + status);
            }

            JsonNode results = JSON.readTree(response.body());
            if (!results.isArray() || results.isEmpty()) return Lookup.none();

            JsonNode first = results.get(0);
            Double lat = toDouble(first.path("lat").asText(null));
            Double lng = toDouble(first.path("lon").asText(null));
            // A 200 carrying something we can't read is not evidence the place
            // doesn't exist, so it's a failure and not a miss.
            return valid(lat, lng) ? Lookup.found(new Coords(lat, lng))
                    : Lookup.failed("no usable lat/lon in the response");
        } catch (Exception e) {
            return Lookup.failed(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    static boolean valid(Double lat, Double lng) {
        return lat != null && lng != null
                && Math.abs(lat) <= 90 && Math.abs(lng) <= 180
                // 0,0 is in the Atlantic and is what a geocoder returns when it
                // has parsed something it didn't understand.
                && !(lat == 0.0 && lng == 0.0);
    }

    static void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ------------------------------------------------------------------ cache

    @SuppressWarnings("unchecked")
    static Map<String, Coords> loadCache() throws IOException {
        // TreeMap: the file is committed, so a stable key order keeps its diff
        // to the lines that actually changed.
        Map<String, Coords> cache = new TreeMap<>();
        if (!Files.exists(CACHE_FILE)) return cache;

        Object parsed = new Yaml().load(Files.readString(CACHE_FILE));
        if (!(parsed instanceof Map)) return cache;

        for (Map.Entry<String, Object> entry : ((Map<String, Object>) parsed).entrySet()) {
            String query = trimToNull(entry.getKey());
            if (query == null) continue;
            if (!(entry.getValue() instanceof Map<?, ?> value)) {
                cache.put(query, null);
                continue;
            }
            Double lat = firstNumber(value, "latitude", "lat");
            Double lng = firstNumber(value, "longitude", "lon", "lng");
            cache.put(query, valid(lat, lng) ? new Coords(lat, lng) : null);
        }
        System.out.println("Loaded " + cache.size() + " cached place(s) from " + CACHE_FILE);
        return cache;
    }

    static String renderCache(Map<String, Coords> cache) {
        StringBuilder out = new StringBuilder("""
                # Geocoding cache -- written by scripts/fetch/JavaChampions.java, which resolves
                # each Java Champion's "<city>, <country>" to coordinates for the world map on
                # /java-champions/.
                #
                # Keyed by the PLACE, not by the champion: 422 champions live in 252 distinct
                # places, so everyone in "London, UK" shares one entry, and renaming a champion
                # or editing their socials costs no lookup at all.
                #
                # COMMITTED ON PURPOSE, and not hand-edited. It is what keeps this script from
                # re-querying geocode.maps.co on every deploy -- with the cache warm a run makes
                # no requests, so only genuinely new or moved champions cost anything.
                #
                # `found: false` means the geocoder answered and knows nowhere by that name;
                # it is recorded so we stop asking daily. Delete an entry (or this whole file)
                # to force a fresh lookup -- a full rebuild is ~250 requests at 1/sec, inside
                # the free tier's 5000/day.
                #
                # A place is looked up ONLY on a cache miss, and only when GEOCODE_API_KEY is
                # set (a repository secret in CI, an env var locally). Without it the cached
                # places still work and nothing here changes.

                """.stripIndent());

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);

        Map<String, Object> dump = new LinkedHashMap<>();
        for (Map.Entry<String, Coords> entry : cache.entrySet()) {
            Coords coords = entry.getValue();
            Map<String, Object> value = new LinkedHashMap<>();
            if (coords == null) {
                value.put("found", false);
            } else {
                value.put("latitude", coords.latitude());
                value.put("longitude", coords.longitude());
            }
            dump.put(entry.getKey(), value);
        }
        return out.append(dump.isEmpty() ? "{}\n" : yaml.dump(dump)).toString();
    }

    // ------------------------------------------------------------------ utils

    static void putIfPresent(Map<String, Object> map, String key, Object value) {
        String s = trimToNull(value);
        if (s != null) map.put(key, s);
    }

    static String trimToNull(Object value) {
        if (value == null) return null;
        String s = String.valueOf(value).trim();
        return s.isEmpty() ? null : s;
    }

    static Double toDouble(Object value) {
        if (value == null) return null;
        if (value instanceof Number n) return n.doubleValue();
        try {
            return Double.parseDouble(String.valueOf(value).trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    static void writeYaml(List<Map<String, Object>> champions) throws IOException {
        String header = """
                # Java Champions -- generated automatically by scripts/fetch/JavaChampions.java
                # from https://github.com/aalmiray/java-champions's java-champions.yml, the
                # data behind https://javachampions.org/.
                #
                # DO NOT EDIT THIS FILE BY HAND: it's regenerated at every site build
                # (.github/workflows/build-deploy.yml) and by the external-content sync
                # (.github/workflows/sync-external-content.yml), and any manual change here is
                # overwritten the next time either runs.
                #
                # To add, fix, or remove a Java Champion, edit java-champions.yml directly
                # upstream instead: %s
                #
                # latitude/longitude drive the world map on /java-champions/. They are the
                # champion's CITY (or country, for the ones recording no city) -- not an
                # address -- resolved through data/geocode-cache.yaml, and taken straight from
                # an upstream `location:` field the moment aalmiray/java-champions#318 adds one.

                """.formatted(EDIT_URL).stripIndent();

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        Yaml yaml = new Yaml(options);

        Files.createDirectories(OUTPUT_FILE.getParent());
        Files.writeString(OUTPUT_FILE, header + yaml.dump(champions));
    }
}
