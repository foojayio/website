// JDK 26+: Process is Closeable. Leaving the try block closes the pipes and then
// waits for ffmpeg to exit; it does not stop the encode.
public class CloseTest {
    public static void main(String[] args) throws Exception {
        long start = System.nanoTime();
        Process p;
        try (Process running = new ProcessBuilder("ffmpeg", "-hide_banner", "-y", "-i", args[0],
                "-c:v", "libx264", "-preset", "medium", "-c:a", "aac", args[1]).start()) {
            p = running;
            Thread.sleep(1000);
            System.out.println("leaving the try block, alive=" + running.isAlive());
        }
        System.out.printf("close() returned after %.1f s, exit %d%n",
                (System.nanoTime() - start) / 1e9, p.exitValue());
    }
}
