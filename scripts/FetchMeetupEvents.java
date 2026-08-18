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
import java.util.*;

/**
 * Pulls upcoming events for every JUG in data/jugs.yaml that has a Meetup
 * group (a `meetup_slug`, derived by scripts/FetchJugs.java from any JUG
 * whose `website` is a meetup.com URL) from Meetup's GraphQL API, and writes
 * data/events.json for the Hugo site's calendar page. Run four times a day by
 * .github/workflows/sync-external-content.yml, which runs FetchJugs.java first so this
 * always sees the current upstream JUG list rather than a stale commit.
 *
 * JUGs without a Meetup group (their own website/calendar instead) are
 * skipped here -- there's no Meetup API to query for them.
 *
 * REQUIRES: a Meetup Pro subscription + an OAuth client created under that
 * Pro network (Meetup retired the old open REST API; reading event data now
 * needs an authenticated GraphQL request). Pass the OAuth access token via
 * the MEETUP_OAUTH_TOKEN environment variable.
 * See: https://help.meetup.com/hc/en-us/articles/41453576628749-How-can-I-get-access-to-Meetup-s-API
 *
 * IMPORTANT: Meetup's GraphQL schema was still evolving as of when this
 * script was written, and this environment had no Pro/OAuth credentials to
 * test against. The endpoint + query below are a best-effort starting point
 * -- verify the query shape against https://www.meetup.com/api/schema/
 * (or the GraphQL explorer under your Pro account) before relying on it, and
 * adjust GRAPHQL_QUERY / the response-parsing code in fetchEventsForGroup()
 * if the schema differs.
 */
public class FetchMeetupEvents {

    static final String GRAPHQL_ENDPOINT = "https://api.meetup.com/gql-ext"; // VERIFY against current docs
    static final Path JUGS_FILE = Path.of("data/jugs.yaml");
    static final Path OUTPUT_FILE = Path.of("data/events.json");
    static final int EVENTS_PER_GROUP = 10;

    static final String GRAPHQL_QUERY = """
        query($urlname: String!, $itemsNum: Int!) {
          groupByUrlname(urlname: $urlname) {
            name
            urlname
            upcomingEvents(input: { first: $itemsNum }) {
              edges {
                node {
                  id
                  title
                  eventUrl
                  dateTime
                  endTime
                  venue { name city }
                }
              }
            }
          }
        }
        """;

    static final ObjectMapper JSON = new ObjectMapper();
    static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();

    public static void main(String[] args) throws Exception {
        String token = System.getenv("MEETUP_OAUTH_TOKEN");
        if (token == null || token.isBlank()) {
            System.err.println("MEETUP_OAUTH_TOKEN is not set. See script header for setup instructions.");
            System.exit(1);
        }

        List<Map<String, Object>> jugs = loadJugs();
        System.out.println("Loaded " + jugs.size() + " JUGs with a Meetup group from " + JUGS_FILE);

        List<Map<String, Object>> allGroups = new ArrayList<>();
        for (Map<String, Object> jug : jugs) {
            String slug = String.valueOf(jug.get("meetup_slug"));
            try {
                Map<String, Object> groupResult = fetchEventsForGroup(slug, token);
                allGroups.add(groupResult);
                System.out.println("Fetched events for " + slug);
            } catch (Exception e) {
                System.err.println("FAILED to fetch " + slug + ": " + e.getMessage());
                Map<String, Object> errorEntry = new LinkedHashMap<>();
                errorEntry.put("slug", slug);
                errorEntry.put("error", e.getMessage());
                allGroups.add(errorEntry);
            }
        }

        Map<String, Object> output = new LinkedHashMap<>();
        output.put("generatedAt", java.time.Instant.now().toString());
        output.put("groups", allGroups);

        Files.createDirectories(OUTPUT_FILE.getParent());
        Files.writeString(OUTPUT_FILE, JSON.writerWithDefaultPrettyPrinter().writeValueAsString(output));
        System.out.println("Wrote " + OUTPUT_FILE);
    }

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

    static Map<String, Object> fetchEventsForGroup(String slug, String token) throws IOException, InterruptedException {
        Map<String, Object> variables = new LinkedHashMap<>();
        variables.put("urlname", slug);
        variables.put("itemsNum", EVENTS_PER_GROUP);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("query", GRAPHQL_QUERY);
        body.put("variables", variables);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GRAPHQL_ENDPOINT))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(body)))
                .timeout(Duration.ofSeconds(20))
                .build();

        HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("HTTP " + response.statusCode() + ": " + response.body());
        }

        JsonNode root = JSON.readTree(response.body());
        if (root.has("errors")) {
            throw new IOException("GraphQL errors: " + root.get("errors").toString());
        }

        JsonNode group = root.path("data").path("groupByUrlname");
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("slug", slug);
        result.put("name", group.path("name").asText(slug));

        List<Map<String, Object>> events = new ArrayList<>();
        for (JsonNode edge : group.path("upcomingEvents").path("edges")) {
            JsonNode node = edge.path("node");
            Map<String, Object> event = new LinkedHashMap<>();
            event.put("title", node.path("title").asText());
            event.put("url", node.path("eventUrl").asText());
            event.put("startTime", node.path("dateTime").asText());
            event.put("endTime", node.path("endTime").asText());
            event.put("venue", node.path("venue").path("name").asText(null));
            event.put("city", node.path("venue").path("city").asText(null));
            events.add(event);
        }
        result.put("events", events);
        return result;
    }
}
