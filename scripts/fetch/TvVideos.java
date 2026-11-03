///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.yaml:snakeyaml:2.2
//DEPS com.fasterxml.jackson.core:jackson-databind:2.17.1
//JAVA 17+

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Writes data/tv-videos.json, the videos behind /tv/ ("Foojay Television"),
 * for the channels listed in data/tv-channels.yaml. Runs daily from
 * .github/workflows/sync-external-content.yml. Needs no key: it reads each
 * channel's public RSS feed (the 15 newest uploads).
 *
 * A SHORT IS NOT LABELLED IN THE FEED. /shorts/<id> answers 200 for a Short and
 * redirects to /watch (303) for a normal video, so that is the probe. Shorts
 * are remembered by id so a warm run probes only new uploads.
 *
 * Never fails the sync: a channel that cannot be fetched keeps its committed
 * videos. The file is rewritten only when its content changed.
 */
public class TvVideos {

    static final Path CHANNELS_FILE = Path.of("data/tv-channels.yaml");
    static final Path OUTPUT_FILE = Path.of("data/tv-videos.json");
    static final String SYNC_KEY = "tv-videos";
    static final int KEEP_PER_CHANNEL = 24;
    static final long PAUSE_MS = 500;

    // The Shorts probe reads the redirect itself, so it must not follow it.
    static final HttpClient HTTP = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NEVER)
            .connectTimeout(Duration.ofSeconds(15)).build();
    static final HttpClient FOLLOWING = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(15)).build();
    static final ObjectMapper JSON = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        List<Map<String, Object>> wanted = new Yaml().load(Files.readString(CHANNELS_FILE));
        Map<String, Object> previous = Files.exists(OUTPUT_FILE)
                ? JSON.readValue(OUTPUT_FILE.toFile(), Map.class) : new HashMap<>();
        Map<String, Map<String, Object>> oldChannels = new HashMap<>();
        for (Object c : (List<Object>) previous.getOrDefault("channels", List.of())) {
            Map<String, Object> m = (Map<String, Object>) c;
            oldChannels.put((String) m.get("handle"), m);
        }
        List<Map<String, Object>> oldVideos = (List<Map<String, Object>>) previous.getOrDefault("videos", List.of());
        Set<String> shorts = new TreeSet<>((List<String>) previous.getOrDefault("shorts", List.of()));
        Set<String> known = new HashSet<>(shorts);
        oldVideos.forEach(v -> known.add((String) v.get("id")));

        List<Map<String, Object>> channels = new ArrayList<>();
        Map<String, Map<String, Object>> videos = new LinkedHashMap<>();
        oldVideos.forEach(v -> videos.put((String) v.get("id"), v));
        int failed = 0;

        for (Map<String, Object> w : wanted) {
            String handle = (String) w.get("handle");
            Map<String, Object> channel = oldChannels.get(handle);
            try {
                if (channel == null) channel = resolveChannel(handle);
                channels.add(channel);
                String xml = get("https://www.youtube.com/feeds/videos.xml?channel_id=" + channel.get("id"));
                for (String entry : xml.split("<entry>")) {
                    String id = first(entry, "<yt:videoId>([^<]+)");
                    if (id == null || shorts.contains(id)) continue;
                    if (!known.contains(id)) {
                        Thread.sleep(PAUSE_MS);
                        if (isShort(id)) { shorts.add(id); continue; }
                    }
                    Map<String, Object> v = new LinkedHashMap<>();
                    v.put("id", id);
                    v.put("channel", handle);
                    v.put("title", unescape(first(entry, "<title>([^<]+)")));
                    v.put("published", first(entry, "<published>([^<]+)"));
                    videos.put(id, v);
                }
                System.out.println(handle + ": ok");
            } catch (Exception e) {
                failed++;
                if (channel != null) channels.add(channel);
                System.out.println(handle + ": " + e + " -- keeping its committed videos");
            }
        }
        if (failed > 0) System.out.println("SYNC-DEGRADED " + SYNC_KEY + ": " + failed + " channel(s) unavailable");
        if (channels.isEmpty()) return;

        Map<String, Integer> perChannel = new HashMap<>();
        List<Map<String, Object>> kept = videos.values().stream()
                .sorted(Comparator.comparing((Map<String, Object> v) -> (String) v.get("published")).reversed())
                .filter(v -> perChannel.merge((String) v.get("channel"), 1, Integer::sum) <= KEEP_PER_CHANNEL)
                .toList();

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("channels", channels);
        out.put("videos", kept);
        out.put("shorts", new ArrayList<>(shorts));
        String text = JSON.writeValueAsString(out) + "\n";
        if (Files.exists(OUTPUT_FILE) && Files.readString(OUTPUT_FILE).equals(text)) {
            System.out.println("No change to " + OUTPUT_FILE);
            return;
        }
        Files.writeString(OUTPUT_FILE, text);
        System.out.println("Wrote " + OUTPUT_FILE + ": " + kept.size() + " videos, " + shorts.size() + " Shorts skipped");
    }

    static Map<String, Object> resolveChannel(String handle) throws Exception {
        String html = get("https://www.youtube.com/@" + handle);
        String id = first(html, "channel_id=(UC[\\w-]+)");
        if (id == null) throw new IOException("no channel id on the page of @" + handle);
        Map<String, Object> c = new LinkedHashMap<>();
        c.put("handle", handle);
        c.put("id", id);
        c.put("title", unescape(first(html, "<meta property=\"og:title\" content=\"([^\"]*)\"")));
        String avatar = first(html, "<meta property=\"og:image\" content=\"([^\"]*)\"");
        c.put("avatar", avatar == null ? "" : avatar.replaceAll("=s\\d+-", "=s176-"));
        return c;
    }

    static boolean isShort(String id) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create("https://www.youtube.com/shorts/" + id))
                .timeout(Duration.ofSeconds(20)).header("User-Agent", "foojay.io tv sync")
                // Without it an EU client is sent to a consent page (302), not told the answer.
                .header("Cookie", "SOCS=CAI")
                .method("HEAD", HttpRequest.BodyPublishers.noBody()).build();
        HttpResponse<Void> r = HTTP.send(req, HttpResponse.BodyHandlers.discarding());
        if (r.statusCode() == 200) return true;
        String to = r.headers().firstValue("Location").orElse("");
        if (r.statusCode() == 303 && to.contains("/watch")) return false;
        throw new IOException("HTTP " + r.statusCode() + " " + to + " probing " + id);
    }

    static String get(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(30))
                .header("User-Agent", "Mozilla/5.0 (compatible; foojay.io tv sync)")
                .header("Accept-Language", "en").GET().build();
        HttpResponse<String> r = FOLLOWING.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (r.statusCode() != 200) throw new IOException("HTTP " + r.statusCode() + " for " + url);
        return r.body();
    }

    static String first(String s, String regex) {
        Matcher m = Pattern.compile(regex).matcher(s);
        return m.find() ? m.group(1) : null;
    }

    static String unescape(String s) {
        if (s == null) return "";
        return s.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"")
                .replace("&#39;", "'").replace("&amp;", "&");
    }
}
