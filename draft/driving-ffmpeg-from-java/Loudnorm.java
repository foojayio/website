///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 21+
//DEPS tools.jackson.core:jackson-databind:3.2.3

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.json.JsonMapper;

public class Loudnorm {
    // loudnorm prints every number as a JSON string; keeping them as strings means
    // they go back into the second pass exactly as ffmpeg wrote them.
    record Stats(String inputI, String inputTp, String inputLra, String inputThresh,
                 String outputI, String outputTp, String normalizationType, String targetOffset) {}

    static final String TARGET = "I=-14:TP=-1:LRA=11";

    // One ffmpeg run with a loudnorm filter; the stats are the last {...} in its log.
    static Stats loudnorm(String in, String options, List<String> output) throws Exception {
        var cmd = new ArrayList<>(List.of("ffmpeg", "-hide_banner", "-nostdin", "-y", "-i", in,
                "-af", "loudnorm=" + options + ":print_format=json"));
        cmd.addAll(output);
        Result r = run(cmd);
        if (r.exit() != 0) throw new IllegalStateException(r.stderr());
        String log = r.stderr();
        return JSON.readValue(log.substring(log.lastIndexOf('{'), log.lastIndexOf('}') + 1),
                Stats.class);
    }

    public static void main(String[] args) throws Exception {
        String in = args[0], out = args[1];
        Stats first = loudnorm(in, TARGET, List.of("-vn", "-f", "null", "-"));
        System.out.printf("pass 1: %s LUFS, true peak %s dBTP, LRA %s LU%n",
                first.inputI(), first.inputTp(), first.inputLra());

        String measured = "%s:measured_I=%s:measured_TP=%s:measured_LRA=%s:measured_thresh=%s"
                .formatted(TARGET, first.inputI(), first.inputTp(), first.inputLra(),
                        first.inputThresh())
                + ":offset=" + first.targetOffset() + ":linear=true";
        Stats second = loudnorm(in, measured,
                List.of("-c:v", "copy", "-c:a", "aac", "-b:a", "192k", "-ar", "48000", out));
        System.out.printf("pass 2: %s, output %s LUFS, true peak %s dBTP%n",
                second.normalizationType(), second.outputI(), second.outputTp());
    }

    static final JsonMapper JSON = JsonMapper.builder()
            .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    record Result(int exit, String stdout, String stderr) {}

    static Result run(List<String> command) throws Exception {
        Process p = new ProcessBuilder(command).start();
        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<String> err = pool.submit(() -> new String(p.getErrorStream().readAllBytes()));
            String out = new String(p.getInputStream().readAllBytes());
            return new Result(p.waitFor(), out, err.get());
        }
    }
}
