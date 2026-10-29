///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 21+
//DEPS tools.jackson.core:jackson-databind:3.2.3

import java.util.List;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.json.JsonMapper;

public class Probe {
    record MediaInfo(List<Stream> streams, Format format) {}

    record Format(String formatName, double duration, long bitRate) {}

    record Stream(int index, String codecType, String codecName,
                  Integer width, Integer height, String avgFrameRate,
                  Integer sampleRate, Integer channels) {
        double fps() {
            String[] f = avgFrameRate.split("/");
            return Double.parseDouble(f[0]) / Double.parseDouble(f[1]);
        }
    }

    static final JsonMapper JSON = JsonMapper.builder()
            .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    static MediaInfo probe(String file) throws Exception {
        Process p = new ProcessBuilder("ffprobe", "-v", "error", "-print_format", "json",
                "-show_format", "-show_streams", file)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start();
        MediaInfo info = JSON.readValue(p.getInputStream(), MediaInfo.class);
        if (p.waitFor() != 0) throw new IllegalStateException("ffprobe failed: " + file);
        return info;
    }

    public static void main(String[] args) throws Exception {
        for (String file : args) {
            MediaInfo info = probe(file);
            System.out.printf("%s: %s, %.3f s, %d kb/s%n", file, info.format().formatName(),
                    info.format().duration(), info.format().bitRate() / 1000);
            for (Stream s : info.streams()) {
                if (s.codecType().equals("video")) {
                    System.out.printf("  #%d video %s %dx%d %.3f fps%n",
                            s.index(), s.codecName(), s.width(), s.height(), s.fps());
                } else if (s.codecType().equals("audio")) {
                    System.out.printf("  #%d audio %s %d Hz, %d ch%n",
                            s.index(), s.codecName(), s.sampleRate(), s.channels());
                }
            }
        }
    }
}
