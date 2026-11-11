import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Progress {
    record Snapshot(long frame, double seconds, String speed, boolean done) {
        // out_time_us is "N/A" before the first packet and again while the encoder
        // flushes at the end, so N/A keeps the last real value. Audio-only
        // output has no frame key at all, so it reads as 0.
        static Snapshot of(Map<String, String> kv, double lastSeconds) {
            String us = kv.get("out_time_us");
            return new Snapshot(Long.parseLong(kv.getOrDefault("frame", "0")),
                    us.equals("N/A") ? lastSeconds : Long.parseLong(us) / 1e6,
                    kv.get("speed"), kv.get("progress").equals("end"));
        }
    }

    static int encode(String in, String out, Consumer<Snapshot> listener) throws Exception {
        Process p = new ProcessBuilder("ffmpeg", "-hide_banner", "-nostdin", "-y",
                "-loglevel", "error", "-i", in,
                "-c:v", "libx264", "-preset", "medium", "-c:a", "aac",
                "-progress", "pipe:1", "-nostats", out)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start();
        Map<String, String> block = new HashMap<>();
        double seconds = 0;
        try (BufferedReader stdout = p.inputReader()) {
            for (String line; (line = stdout.readLine()) != null; ) {
                int eq = line.indexOf('=');
                block.put(line.substring(0, eq), line.substring(eq + 1).trim());
                if (line.startsWith("progress=")) {
                    Snapshot snapshot = Snapshot.of(block, seconds);
                    seconds = snapshot.seconds();
                    listener.accept(snapshot);
                    block.clear();
                }
            }
        }
        return p.waitFor();
    }

    static double duration(String file) throws Exception {
        Process p = new ProcessBuilder("ffprobe", "-v", "error",
                "-show_entries", "format=duration", "-of", "csv=p=0", file)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start();
        String out = new String(p.getInputStream().readAllBytes()).trim();
        if (p.waitFor() != 0) throw new IllegalStateException("ffprobe failed: " + file);
        return Double.parseDouble(out);
    }

    public static void main(String[] args) throws Exception {
        double total = duration(args[0]);
        int exit = encode(args[0], args[1], s -> System.out.printf(
                "%5.1f%%  frame %5d  speed %s%s%n", 100 * s.seconds() / total,
                s.frame(), s.speed(), s.done() ? "  done" : ""));
        System.out.println("exit " + exit);
    }
}
