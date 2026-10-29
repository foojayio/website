import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Drained {
    record Result(int exit, String stdout, String stderr) {}

    static Result run(List<String> command) throws Exception {
        Process p = new ProcessBuilder(command).start();
        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<String> err = pool.submit(() -> new String(p.getErrorStream().readAllBytes()));
            String out = new String(p.getInputStream().readAllBytes());
            return new Result(p.waitFor(), out, err.get());
        }
    }

    public static void main(String[] args) throws Exception {
        long start = System.nanoTime();
        Result r = run(List.of("ffmpeg", "-hide_banner", "-nostdin", "-i", args[0],
                "-af", "ebur128", "-f", "null", "-"));
        String summary = r.stderr().substring(r.stderr().lastIndexOf("Summary:"));
        System.out.printf("exit %d after %.1f s, %d bytes of log%n%s",
                r.exit(), (System.nanoTime() - start) / 1e9, r.stderr().length(), summary);
    }
}
