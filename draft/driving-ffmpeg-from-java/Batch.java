import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Batch {
    static int waitOrStop(Process p, Duration limit) throws InterruptedException {
        if (p.waitFor(limit.toMillis(), TimeUnit.MILLISECONDS)) return p.exitValue();
        p.destroy();                                   // SIGTERM: ffmpeg still finalizes the file
        if (!p.waitFor(10, TimeUnit.SECONDS)) p.destroyForcibly();
        return p.waitFor();
    }

    static int encode(String in, String out) throws Exception {
        Process p = new ProcessBuilder("ffmpeg", "-hide_banner", "-nostdin", "-y",
                "-i", in, "-c:v", "libx264", "-preset", "medium", "-c:a", "aac", out)
            .redirectError(Path.of(out + ".log").toFile())
            .start();
        return waitOrStop(p, Duration.ofMinutes(10));
    }

    static double runAll(List<String> inputs, int limit) throws Exception {
        if (limit < 1) throw new IllegalArgumentException("limit must be at least 1, got " + limit);
        var slots = new Semaphore(limit);
        long start = System.nanoTime();
        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Integer>> jobs = new ArrayList<>();
            for (int i = 0; i < inputs.size(); i++) {
                String in = inputs.get(i), out = "out/batch-" + i + ".mp4";
                jobs.add(pool.submit(() -> {
                    slots.acquire();
                    try {
                        return encode(in, out);
                    } finally {
                        slots.release();
                    }
                }));
            }
            for (var job : jobs) {
                if (job.get() != 0) throw new IllegalStateException("an encode failed");
            }
        }
        return (System.nanoTime() - start) / 1e9;
    }

    public static void main(String[] args) throws Exception {
        List<String> inputs = List.of(args).subList(1, args.length);
        System.out.printf("%d cores reported, %d files%n",
                Runtime.getRuntime().availableProcessors(), inputs.size());
        for (String limit : args[0].split(",")) {
            int n = Integer.parseInt(limit);
            System.out.printf("%2d at a time: %5.1f s%n", n, runAll(inputs, n));
        }
    }
}
