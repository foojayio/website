import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Cancel {
    enum Stop { QUIT_KEY, DESTROY, DESTROY_FORCIBLY }

    static Process start(String in, String out, Path log) throws Exception {
        return new ProcessBuilder("ffmpeg", "-hide_banner", "-y", "-i", in,
                "-c:v", "libx264", "-preset", "medium", "-c:a", "aac", out)
            .redirectError(log.toFile())
            .start();
    }

    static void stop(Process p, Stop how) throws Exception {
        switch (how) {
            case QUIT_KEY -> {                     // what pressing q in a terminal does
                p.getOutputStream().write('q');
                p.getOutputStream().flush();
            }
            case DESTROY -> p.destroy();           // SIGTERM on macOS and Linux
            case DESTROY_FORCIBLY -> p.destroyForcibly();   // SIGKILL
        }
    }

    // How many video frames can still be read back from what was left on disk.
    static String check(String file) throws Exception {
        Process p = new ProcessBuilder("ffprobe", "-v", "error", "-count_frames",
                "-select_streams", "v:0", "-show_entries", "stream=nb_read_frames",
                "-of", "csv=p=0", file)
            .redirectErrorStream(true).start();
        String out = new String(p.getInputStream().readAllBytes()).strip();
        String first = out.lines().findFirst().orElse("");
        return p.waitFor() == 0 ? out.lines().reduce((a, b) -> b).orElse("") + " frames"
                : "unreadable (" + first.replaceAll("^\\[[^]]*\\] ", "") + ")";
    }

    public static void main(String[] args) throws Exception {
        String in = args[0];
        System.out.println("supportsNormalTermination: "
                + new ProcessBuilder("true").start().supportsNormalTermination());
        for (String ext : List.of("mp4", "mkv")) {
            for (Stop how : Stop.values()) {
                String out = "out/cancel-" + how.name().toLowerCase() + "." + ext;
                Path log = Path.of(out + ".log");
                Process p = start(in, out, log);
                Thread.sleep(3000);
                stop(p, how);
                if (!p.waitFor(10, TimeUnit.SECONDS)) {
                    p.destroyForcibly();
                    p.waitFor();
                }
                String last = Files.readAllLines(log).stream()
                        .filter(l -> l.contains("signal") || l.contains("Exiting"))
                        .findFirst().orElse("-");
                System.out.printf("%-4s %-16s exit %3d  %-36s %s%n",
                        ext, how, p.exitValue(), check(out), last.strip());
            }
        }
    }
}
