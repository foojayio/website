///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.yaml:snakeyaml:2.2
//JAVA 17+

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
 * NOTE: unlike data/jugs.yaml, there are no coordinates here (yet). A
 * pending PR (aalmiray/java-champions#318, not merged as of writing) adds a
 * `location: {lat, lng}` per member via a one-time geocoding script. Once
 * that merges, this can pick it up the same way scripts/FetchJugs.java
 * reads JUG coordinates from GlobalWWJugs's `location` field.
 */
public class FetchJavaChampions {

    static final String SOURCE_URL = "https://raw.githubusercontent.com/aalmiray/java-champions/main/java-champions.yml";
    static final String EDIT_URL = "https://github.com/aalmiray/java-champions/edit/main/java-champions.yml";
    // Avatar paths in the source (e.g. "img/avatars/aalmiray.png") are
    // relative to the published site root, not the repo -- javachampions.org
    // is also the more stable long-term host (repo file layout is an
    // implementation detail of their JBake build, the published URL isn't).
    static final String AVATAR_BASE = "https://javachampions.org/";

    static final Path OUTPUT_FILE = Path.of("data/java-champions.yaml");

    static final List<String> SOCIAL_FIELDS = List.of(
            "twitter", "mastodon", "bluesky", "youtube", "linkedin", "github",
            "website", "sessionize", "xing", "speakerdeck"
    );

    static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();

    public static void main(String[] args) throws Exception {
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

        List<Map<String, Object>> champions = new ArrayList<>();
        for (Map<String, Object> raw : rawMembers) {
            Map<String, Object> champion = convert(raw);
            if (champion != null) champions.add(champion);
        }

        champions.sort(Comparator.comparing(c -> String.valueOf(c.get("name")), String.CASE_INSENSITIVE_ORDER));
        System.out.println("Parsed " + champions.size() + " Java Champions, writing " + OUTPUT_FILE);

        writeYaml(champions);
    }

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
            champion.put("avatar", AVATAR_BASE + avatarPath.replaceAll("^/+", ""));
        }

        Object statusObj = raw.get("status");
        if (statusObj instanceof List && !((List<?>) statusObj).isEmpty()) {
            champion.put("status", statusObj);
        }

        return champion;
    }

    static void putIfPresent(Map<String, Object> map, String key, Object value) {
        String s = trimToNull(value);
        if (s != null) map.put(key, s);
    }

    static String trimToNull(Object value) {
        if (value == null) return null;
        String s = String.valueOf(value).trim();
        return s.isEmpty() ? null : s;
    }

    static void writeYaml(List<Map<String, Object>> champions) throws IOException {
        String header = """
                # Java Champions -- generated automatically by scripts/FetchJavaChampions.java
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
                # No coordinates yet -- aalmiray/java-champions#318 (pending) would add them
                # via a one-time geocoding script; nothing to read here until it merges.

                """.formatted(EDIT_URL).stripIndent();

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        Yaml yaml = new Yaml(options);

        Files.createDirectories(OUTPUT_FILE.getParent());
        Files.writeString(OUTPUT_FILE, header + yaml.dump(champions));
    }
}
