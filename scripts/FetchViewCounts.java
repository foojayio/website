///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS com.fasterxml.jackson.core:jackson-databind:2.17.1
//JAVA 17+

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Refreshes data/views.json from the view counter (worker/views/), so the
 * `12,345 views` on a post and on every card is baked into the HTML at build
 * time rather than fetched by the reader's browser.
 *
 * WHY BUILD TIME. A client-side fetch was the obvious shape (and is what the
 * old GoatCounter scaffold in partials/stats.html did), but it renders a dash
 * that turns into a number a moment later on every card on the home page, needs
 * JavaScript to show a number that is already known, and can't be sorted --
 * anything that ranks posts by views has to have them all at once. Baking makes
 * the number up to a day stale, which for a view count is not a defect.
 *
 * Run at every deploy (.github/workflows/build-deploy.yml) and four times a day
 * (.github/workflows/sync-external-content.yml), both of which commit the refreshed file
 * back to main -- the same pattern as data/jugs.yaml and data/events.json.
 *
 * NEVER FAILS THE BUILD. If the counter is unreachable this leaves the
 * committed data/views.json alone and exits 0. Yesterday's numbers on the page
 * are correct to within a day; a red deploy because an analytics endpoint
 * blinked is not a trade worth making.
 *
 * Usage:
 *   jbang scripts/FetchViewCounts.java
 *   jbang scripts/FetchViewCounts.java --endpoint https://foojay.io/api/views
 */
public class FetchViewCounts {

    static final String DEFAULT_ENDPOINT = "https://foojay.io/api/views";
    static final Path OUTPUT_FILE = Path.of("data/views.json");

    static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    static final ObjectMapper JSON = new ObjectMapper();

    public static void main(String[] args) {
        List<String> argList = List.of(args);
        int i = argList.indexOf("--endpoint");
        String endpoint = (i >= 0 && i + 1 < argList.size()) ? argList.get(i + 1) : DEFAULT_ENDPOINT;

        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint + "/all"))
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .build();
            HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                keepExisting("HTTP " + response.statusCode() + " from " + endpoint);
                return;
            }

            JsonNode body = JSON.readTree(response.body());
            if (!body.isObject()) {
                keepExisting("expected a JSON object from " + endpoint);
                return;
            }

            Map<String, Integer> counts = new TreeMap<>();
            for (Iterator<Map.Entry<String, JsonNode>> it = body.fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> entry = it.next();
                if (entry.getValue().isInt() || entry.getValue().isLong()) {
                    counts.put(entry.getKey(), entry.getValue().asInt());
                }
            }
            if (counts.isEmpty()) {
                // An empty counter is indistinguishable from a broken one here,
                // and blanking every number on the site is the worse outcome of
                // the two. Seed it first (scripts/FetchWpViews.java --seed).
                keepExisting("counter returned no rows");
                return;
            }

            ObjectNode root = JSON.createObjectNode();
            counts.forEach(root::put);
            Files.createDirectories(OUTPUT_FILE.getParent());
            Files.writeString(OUTPUT_FILE, JSON.writerWithDefaultPrettyPrinter().writeValueAsString(root) + "\n");

            long total = counts.values().stream().mapToLong(Integer::longValue).sum();
            System.out.printf("Wrote %s: %d entries, %,d views total%n", OUTPUT_FILE, counts.size(), total);
        } catch (Exception e) {
            keepExisting(e.toString());
        }
    }

    static void keepExisting(String reason) {
        System.out.println("WARN: could not refresh view counts (" + reason + ").");
        System.out.println("      Keeping the committed " + OUTPUT_FILE + " -- the build carries on.");
    }
}
