import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

public class Deadlock {
    public static void main(String[] args) throws Exception {
        long start = System.nanoTime();
        Process p = new ProcessBuilder(
                "ffmpeg", "-hide_banner", "-nostdin", "-i", args[0],
                "-af", "ebur128", "-f", "null", "-")
            .start();

        // The mistake: wait for the exit first, read the log afterwards.
        if (p.waitFor(30, TimeUnit.SECONDS)) {
            String log = new String(p.getErrorStream().readAllBytes());
            System.out.printf("exit %d after %.1f s, %d bytes of log%n",
                    p.exitValue(), seconds(start), log.length());
            return;
        }
        System.out.printf("still running after %.1f s, %d bytes sitting in the stderr pipe%n",
                seconds(start), p.getErrorStream().available());

        // Proof that it was blocked rather than slow: read the pipe and it finishes.
        long drained = p.getErrorStream().transferTo(OutputStream.nullOutputStream());
        System.out.printf("drained %d bytes, exit %d after %.1f s%n",
                drained, p.waitFor(), seconds(start));
    }

    static double seconds(long start) {
        return (System.nanoTime() - start) / 1e9;
    }
}
