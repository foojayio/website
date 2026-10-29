import java.util.concurrent.Executors;

// Counts platform threads while one virtual thread per child process waits on it.
public class Carriers {
    public static void main(String[] args) throws Exception {
        int jobs = Integer.parseInt(args[0]);
        boolean read = args[1].equals("read");
        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < jobs; i++) {
                pool.submit(() -> {
                    Process p = new ProcessBuilder("sleep", "3").start();
                    if (read) p.getInputStream().readAllBytes();
                    return p.waitFor();
                });
            }
            Thread.sleep(1500);
            var threads = Thread.getAllStackTraces().keySet();
            long carriers = threads.stream()
                    .filter(t -> t.getName().startsWith("ForkJoinPool-1-worker")).count();
            long reapers = threads.stream()
                    .filter(t -> t.getName().startsWith("process reaper")).count();
            System.out.printf("%d %d jobs (%s): %d carrier threads, %d process reaper threads, "
                    + "%d platform threads total%n", Runtime.version().feature(), jobs, args[1],
                    carriers, reapers, threads.size());
        }
    }
}
