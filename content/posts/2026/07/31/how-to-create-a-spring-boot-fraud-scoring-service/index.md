---
title: "How to Create a Spring Boot Fraud Scoring Service"
date: "2026-07-31T08:45:43+00:00"
lastmod: "2026-07-31T11:36:10+00:00"
description: "Most Java teams who want a machine learning model in production end up standing up a Python service and calling it over HTTP. That works, but it buys you, - by Geertjan Wielenga"
authors:
  - "geertjan-wielenga"
  - "zoran-sevarac"
image: "Favicon-3-2.png"
categories:
  - "AI"
  - "Deep Netts"
  - "Machine Learning"
  - "Spring"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Most Java teams who want a machine learning model in production end up standing up a Python service and calling it over HTTP. That works, but it buys you, as a Java developer, a second runtime, a second deployment pipeline, a network hop on every prediction, and a team boundary that turns retraining into someone else's ticket.

**[Deep Netts](https://www.deepnetts.com/) removes that split: it's a pure-Java deep learning library, so the model trains in Java, serializes to a file, and loads back into your Spring Boot application as an ordinary bean. Predictions become in-process method calls measured in microseconds, with nothing extra to deploy, secure, or monitor.**

This tutorial starts from [Deep Netts' credit card fraud detection example](https://github.com/deepnetts/CreditCardFraudDetection) and takes it somewhere the example doesn't go --- a running HTTP service shaped like something you could put in front of real traffic.

You'll:

1. Train a feed-forward network on the public Kaggle transactions dataset.
2. Export it alongside the scaling parameters it silently depends on.
3. Wrap it in a Spring Boot application with proper thread safety, a decision threshold you chose on purpose, health checks, and regression tests.

Expect to spend as much time on operational concerns --- dependency resolution in CI and model versioning --- as on the network architecture, because in practice those are what decide whether the thing survives its first month.

Plan around two hours for working through the scenario below!

## Step 1 --- Get the original example running

### 1.1 Install Deep Netts into your local Maven repository

Deep Netts Pro is not on Maven Central, you need it for this scenario, and it is free (unless the annual revenue generated through the product is over USD 100,000 and the total company revenue is over USD 1,000,000, as explained in step 11 "Licensing, before you go live", below).

Download the Deep Netts Pro ZIP from [Deep Netts](https://www.deepnetts.com/) (click "Free download" in the top right) unzip it, then run the import script from the extracted directory:

```
./importToLocalMaven.sh      # importToLocalMaven.bat on Windows
```

Verify it landed:

```
ls ~/.m2/repository/com/deepnetts/
# expect: deepnetts-core-pro/  deepnetts-license/
```

### 1.2 Clone and run the example

```
git clone https://github.com/deepnetts/CreditCardFraudDetection.git
cd CreditCardFraudDetection
unzip creditcard.zip          # the full 284k-row dataset
mvn clean package
mvn exec:java
```

The project's `pom.xml` declares:

|                    Dependency                     |                   Purpose                   |
|---------------------------------------------------|---------------------------------------------|
| `com.deepnetts:deepnetts-core-pro:3.2.0`          | the neural network engine                   |
| `com.deepnetts:deepnetts-license:1.0`             | license jar --- **required at runtime too** |
| `tech.tablesaw:tablesaw-core` / `tablesaw-jsplot` | dataframe + charts for exploration          |
| `javax.visrec:visrec-ri:1.0.3`                    | JSR-381 reference implementation            |

Get this running before you write a line of Spring code. If the license jar isn't resolving, nothing downstream will work and you'll waste an hour blaming Spring.

### 1.3 Understand what's in the data

Two files ship with the repo:

* `creditcard.csv` (inside the zip) --- \~284,807 transactions, of which roughly 492 are fraud. That's about **0.17%**.
* `creditcard-balanced.csv` --- an undersampled version with a roughly even class split.

Columns: `Time`, `V1`--`V28`, `Amount`, `Class` (0 = legitimate, 1 = fraud). That's 30 usable inputs and 1 binary output.

The imbalance is the whole problem. A model that predicts "not fraud" for every transaction scores 99.83% accuracy and is worthless. **Never report accuracy on this problem.** You want precision, recall, and a confusion matrix --- covered in step 3.

Training on the balanced file is the standard starting move, but understand the trade: undersampling throws away \~99% of the legitimate examples, and the resulting model's raw output is calibrated to a 50/50 world that doesn't exist. Your decision threshold will need work (step 4).

## Step 2 --- Make the split deterministic

The example splits data at runtime with a random shuffle. For a service, you need to persist a scaler alongside the model, and the scaler's parameters must come from *exactly* the training rows --- not a fresh random split. So split once, to files, and keep them.

Create `src/main/java/com/example/fraud/training/SplitData.java`:

```
package com.example.fraud.training;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/** Stratified 70/30 split into train.csv and test.csv, deterministic given the seed. */
public class SplitData {

    private static final long SEED = 42L;
    private static final double TRAIN_FRACTION = 0.7;

    public static void main(String[] args) throws IOException {
        Path source = Path.of(args.length > 0 ? args[0] : "creditcard-balanced.csv");
        List<String> lines = Files.readAllLines(source);
        String header = lines.get(0);

        List<String> fraud = new ArrayList<>();
        List<String> legit = new ArrayList<>();
        for (String line : lines.subList(1, lines.size())) {
            if (line.isBlank()) continue;
            (line.trim().endsWith(",1") ? fraud : legit).add(line);
        }

        Random rnd = new Random(SEED);
        Collections.shuffle(fraud, rnd);
        Collections.shuffle(legit, rnd);

        List<String> train = new ArrayList<>(), test = new ArrayList<>();
        partition(fraud, train, test);
        partition(legit, train, test);
        Collections.shuffle(train, rnd);   // avoid class-ordered batches
        Collections.shuffle(test, rnd);

        write(Path.of("data/train.csv"), header, train);
        write(Path.of("data/test.csv"), header, test);
        System.out.printf("train=%d test=%d%n", train.size(), test.size());
    }

    private static void partition(List<String> rows, List<String> train, List<String> test) {
        int cut = (int) Math.round(rows.size() * TRAIN_FRACTION);
        train.addAll(rows.subList(0, cut));
        test.addAll(rows.subList(cut, rows.size()));
    }

    private static void write(Path path, String header, List<String> rows) throws IOException {
        Files.createDirectories(path.getParent());
        try (BufferedWriter w = Files.newBufferedWriter(path)) {
            w.write(header); w.newLine();
            for (String r : rows) { w.write(r); w.newLine(); }
        }
    }
}
```

Run it once. Commit the seed, not the CSVs.
> The `endsWith(",1")` check assumes `Class` is the final column with no trailing whitespace. Verify against your header before trusting it.

## Step 3 --- Train and export a deployable artifact

Training produces **two** files. Everyone remembers the model. The forgotten one causes most production incidents.

Create `TrainFraudModel.java`:

```
package com.example.fraud.training;

import deepnetts.data.DataSet;
import deepnetts.data.DataSets;
import deepnetts.data.norm.MaxNormalizer;
import deepnetts.eval.Evaluators;
import deepnetts.eval.ClassifierEvaluationResult;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import deepnetts.net.train.BackpropagationTrainer;
import deepnetts.util.DeepNetts;
import deepnetts.util.FileIO;

import java.nio.file.*;
import java.util.*;

public class TrainFraudModel {

    private static final int NUM_INPUTS  = 30;   // Time, V1..V28, Amount
    private static final int NUM_OUTPUTS = 1;    // Class

    public static void main(String[] args) throws Exception {
        DataSet trainSet = DataSets.readCsv("data/train.csv", NUM_INPUTS, NUM_OUTPUTS, true, ",");
        DataSet testSet  = DataSets.readCsv("data/test.csv",  NUM_INPUTS, NUM_OUTPUTS, true, ",");

        // Scale using statistics from the TRAINING set only, then apply the same to test.
        MaxNormalizer normalizer = new MaxNormalizer(trainSet);
        normalizer.normalize(trainSet);
        normalizer.normalize(testSet);

        // Independently compute the same column maxima so we can persist them for serving.
        float[] columnMax = columnMaxFrom("data/train.csv", NUM_INPUTS);
        writeScaler(Path.of("target/model/scaler.json"), columnMax);

        FeedForwardNetwork net = FeedForwardNetwork.builder()
                .addInputLayer(NUM_INPUTS)
                .addFullyConnectedLayer(32, ActivationType.RELU)
                .addFullyConnectedLayer(16, ActivationType.RELU)
                .addOutputLayer(NUM_OUTPUTS, ActivationType.SIGMOID)
                .lossFunction(LossType.CROSS_ENTROPY)
                .randomSeed(123)
                .build();

        BackpropagationTrainer trainer = net.getTrainer();
        trainer.setMaxError(0.03f)
               .setMaxEpochs(3000)
               .setLearningRate(0.01f);
        trainer.train(trainSet);

        ClassifierEvaluationResult result = Evaluators.evaluateClassifier(net, testSet);
        System.out.println(result);   // read precision/recall/F1 here, ignore accuracy

        Files.createDirectories(Path.of("target/model"));
        FileIO.writeToFile(net, "target/model/fraud-model.dnet");

        DeepNetts.shutdown();
    }

    /** Max absolute value per input column, matching MaxNormalizer's scaling. */
    private static float[] columnMaxFrom(String csv, int numInputs) throws Exception {
        float[] max = new float[numInputs];
        List<String> lines = Files.readAllLines(Path.of(csv));
        for (String line : lines.subList(1, lines.size())) {
            if (line.isBlank()) continue;
            String[] parts = line.split(",");
            for (int i = 0; i < numInputs; i++) {
                max[i] = Math.max(max[i], Math.abs(Float.parseFloat(parts[i].trim())));
            }
        }
        for (int i = 0; i < numInputs; i++) if (max[i] == 0f) max[i] = 1f;  // guard
        return max;
    }

    private static void writeScaler(Path path, float[] max) throws Exception {
        Files.createDirectories(path.getParent());
        StringJoiner j = new StringJoiner(",", "{\"columnMax\":[", "]}");
        for (float m : max) j.add(Float.toString(m));
        Files.writeString(path, j.toString());
    }
}
```

**Why the duplicated max computation?** The service needs the scaling constants at inference time, and how you retrieve them from `MaxNormalizer` varies by version. Computing them independently from the same file is version-proof and takes 10 lines. Add a test asserting the two agree if your version exposes them.

**This is the number-one production bug in Java ML services:** the model is deployed, the scaler isn't, raw un-normalized values get fed to a network trained on values in `[-1, 1]`, and every output saturates. Nothing throws. Your fraud rate just quietly becomes 0% or 100%.

Ship `fraud-model.dnet` and `scaler.json` as a pair, versioned together, always.

## Step 4 --- Pick a threshold (do not use 0.5)

Before writing any Spring code, decide what score means "fraud". Add this to the end of training:

```
System.out.println("threshold\tTP\tFP\tFN\tprecision\trecall");
for (float t = 0.05f; t < 1.0f; t += 0.05f) {
    int tp = 0, fp = 0, fn = 0;
    for (var item : testSet) {
        net.setInput(item.getInput());
        boolean predicted = net.getOutput()[0] >= t;
        boolean actual = item.getTargetOutput().get(0) >= 0.5f;
        if (predicted && actual) tp++;
        else if (predicted) fp++;
        else if (actual) fn++;
    }
    System.out.printf("%.2f\t%d\t%d\t%d\t%.3f\t%.3f%n",
            t, tp, fp, fn,
            tp + fp == 0 ? 0 : (double) tp / (tp + fp),
            tp + fn == 0 ? 0 : (double) tp / (tp + fn));
}
```

Now pick based on cost, not on a round number. A false negative is a chargeback plus fraud loss. A false positive is a declined card, an angry customer, and a support call. If a missed fraud costs 40× a false decline, you want a low threshold and you accept the noise. That's a business decision --- bring the table above to whoever owns it, and put the answer in config, not in code.

Remember the calibration caveat from step 1.3: a model trained on a balanced set outputs numbers that look like probabilities but aren't. The threshold table is empirical and honest; the raw score is not a probability. Don't show it to users as one.

## Step 5 --- Create the Spring Boot project

```
curl https://start.spring.io/starter.zip \
  -d dependencies=web,actuator,validation \
  -d javaVersion=17 -d type=maven-project \
  -d groupId=com.example -d artifactId=fraud-service \
  -o fraud-service.zip && unzip fraud-service.zip -d fraud-service
```

Add to `pom.xml`:

```
<dependency>
    <groupId>com.deepnetts</groupId>
    <artifactId>deepnetts-core-pro</artifactId>
    <version>3.2.0</version>
</dependency>
<dependency>
    <groupId>com.deepnetts</groupId>
    <artifactId>deepnetts-license</artifactId>
    <version>1.0</version>
</dependency>
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
    <version>2.12.0</version>
</dependency>
```

> **CI will break here.** A local `.m2` install works on your laptop and nowhere else. Publish both Deep Netts jars to your internal Nexus/Artifactory once with `mvn deploy:deploy-file` and point CI at that repository. Do this now rather than on the day of your first build-server failure.

`src/main/resources/application.yml`:

```
fraud:
  model-path: classpath:model/fraud-model.dnet
  scaler-path: classpath:model/scaler.json
  threshold: 0.35          # from step 4, not from a textbook
  pool-size: 8
management:
  endpoints.web.exposure.include: health,metrics,prometheus
  endpoint.health.show-details: always
```

Copy `fraud-model.dnet` and `scaler.json` into `src/main/resources/model/`.

## Step 6 --- Load the model and scaler

`FraudProperties.java`:

```
package com.example.fraud;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@ConfigurationProperties(prefix = "fraud")
public record FraudProperties(Resource modelPath, Resource scalerPath,
                              float threshold, int poolSize) {}
```

`ModelConfig.java`:

```
package com.example.fraud;

import com.fasterxml.jackson.databind.ObjectMapper;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.util.DeepNetts;
import deepnetts.util.FileIO;
import jakarta.annotation.PreDestroy;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

import java.io.*;
import java.nio.file.*;

@Configuration
@EnableConfigurationProperties(FraudProperties.class)
public class ModelConfig {

    /** Copy the model out of the jar once; pooled instances are deserialized from this file. */
    @Bean
    File modelFile(FraudProperties props) throws IOException {
        Path tmp = Files.createTempFile("fraud-model", ".dnet");
        tmp.toFile().deleteOnExit();
        try (InputStream in = props.modelPath().getInputStream()) {
            Files.copy(in, tmp, StandardCopyOption.REPLACE_EXISTING);
        }
        return tmp.toFile();
    }

    @Bean
    Scaler scaler(FraudProperties props, ObjectMapper mapper) throws IOException {
        try (InputStream in = props.scalerPath().getInputStream()) {
            return mapper.readValue(in, Scaler.class);
        }
    }

    @Bean(destroyMethod = "close")
    GenericObjectPool<FeedForwardNetwork> networkPool(File modelFile, FraudProperties props) {
        var config = new GenericObjectPoolConfig<FeedForwardNetwork>();
        config.setMaxTotal(props.poolSize());
        config.setMinIdle(props.poolSize());          // pre-warm: deserialization is slow
        config.setMaxWait(java.time.Duration.ofMillis(200));
        config.setBlockWhenExhausted(true);

        var pool = new GenericObjectPool<>(new BasePooledObjectFactory<FeedForwardNetwork>() {
            @Override public FeedForwardNetwork create() throws Exception {
                return FileIO.createFromFile(modelFile, FeedForwardNetwork.class);
            }
            @Override public PooledObject<FeedForwardNetwork> wrap(FeedForwardNetwork net) {
                return new DefaultPooledObject<>(net);
            }
        }, config);

        try { pool.preparePool(); } catch (Exception e) {
            throw new IllegalStateException("Could not initialise model pool", e);
        }
        return pool;
    }

    @PreDestroy
    void shutdownEngine() {
        DeepNetts.shutdown();   // Deep Netts runs its own thread pool
    }
}
```

Two things that matter here:

**The pool exists because a Deep Netts network is stateful.** `setInput()` followed by `getOutput()` is a two-step sequence against instance fields. Two concurrent Tomcat threads sharing one network instance will interleave and hand each other's results back to the wrong caller. It won't throw. It won't log. You'll find it in a customer complaint. A pool of independently deserialized instances is the fix; a `synchronized` block is the simpler fix if your throughput is modest --- measure before choosing.

**`DeepNetts.shutdown()` is not optional.** Without it your context close hangs and your pods take the full termination grace period to die.

## Step 7 --- Scaler and scoring service

`Scaler.java`:

```
package com.example.fraud;

public record Scaler(float[] columnMax) {

    public float[] apply(float[] raw) {
        if (raw.length != columnMax.length) {
            throw new IllegalArgumentException(
                "Expected " + columnMax.length + " features, got " + raw.length);
        }
        float[] scaled = new float[raw.length];
        for (int i = 0; i < raw.length; i++) scaled[i] = raw[i] / columnMax[i];
        return scaled;
    }
}
```

`FraudScoringService.java`:

```
package com.example.fraud;

import deepnetts.net.FeedForwardNetwork;
import deepnetts.tensor.Tensor;
import io.micrometer.core.annotation.Timed;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.stereotype.Service;

@Service
public class FraudScoringService {

    private final GenericObjectPool<FeedForwardNetwork> pool;
    private final Scaler scaler;
    private final float threshold;

    public FraudScoringService(GenericObjectPool<FeedForwardNetwork> pool,
                               Scaler scaler, FraudProperties props) {
        this.pool = pool;
        this.scaler = scaler;
        this.threshold = props.threshold();
    }

    @Timed(value = "fraud.score", percentiles = {0.5, 0.95, 0.99})
    public ScoreResult score(float[] rawFeatures) {
        float[] input = scaler.apply(rawFeatures);
        FeedForwardNetwork net = null;
        try {
            net = pool.borrowObject();
            net.setInput(new Tensor(input));
            float score = net.getOutput()[0];
            return new ScoreResult(score, score >= threshold);
        } catch (Exception e) {
            throw new ScoringUnavailableException(e);
        } finally {
            if (net != null) pool.returnObject(net);
        }
    }

    public record ScoreResult(float score, boolean flagged) {}
}
```

Note the `finally` block returns the instance even on failure. Miss that and the pool drains under error conditions, then every request blocks for 200ms and times out --- a slow, confusing outage.

`ScoringUnavailableException.java`:

```
package com.example.fraud;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ScoringUnavailableException extends RuntimeException {
    public ScoringUnavailableException(Throwable cause) { super("Scoring unavailable", cause); }
}
```

## Step 8 --- The REST layer

Thirty positional floats in a JSON array is a terrible public contract --- one silently reordered field and the model reads `Amount` as `V7`. Name the fields.

`TransactionRequest.java`:

```
package com.example.fraud;

import jakarta.validation.constraints.*;

public record TransactionRequest(
        @NotNull String transactionId,
        @NotNull Float time,
        @NotNull @Size(min = 28, max = 28) float[] v,   // V1..V28, order matters
        @NotNull @PositiveOrZero Float amount) {

    /** Must match the training CSV column order exactly: Time, V1..V28, Amount. */
    public float[] toFeatureVector() {
        float[] features = new float[30];
        features[0] = time;
        System.arraycopy(v, 0, features, 1, 28);
        features[29] = amount;
        return features;
    }
}
```

`FraudController.java`:

```
package com.example.fraud;

import jakarta.validation.Valid;
import org.slf4j.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
public class FraudController {

    private static final Logger log = LoggerFactory.getLogger(FraudController.class);
    private final FraudScoringService scoringService;

    public FraudController(FraudScoringService scoringService) {
        this.scoringService = scoringService;
    }

    @PostMapping("/score")
    public ScoreResponse score(@Valid @RequestBody TransactionRequest request) {
        var result = scoringService.score(request.toFeatureVector());
        log.info("scored transaction={} score={} flagged={}",
                 request.transactionId(), result.score(), result.flagged());
        return new ScoreResponse(request.transactionId(), result.score(),
                                 result.flagged(), "fraud-model-v1");
    }

    public record ScoreResponse(String transactionId, float score,
                                boolean flagged, String modelVersion) {}
}
```

The `modelVersion` field isn't decoration. When someone asks in six months why a specific transaction was declined, you need to know which model made the call. Log the score and the version for every decision --- in most jurisdictions an automated financial decision needs an audit trail, and reconstructing one retroactively is not possible.

Try it:

```
curl -X POST localhost:8080/api/v1/transactions/score \
  -H 'Content-Type: application/json' \
  -d '{"transactionId":"t-1","time":406,
       "v":[-2.31,1.95,-1.61,3.99,-0.52,-1.43,-2.54,1.39,-2.77,-2.77,
            3.20,-2.90,-0.60,-4.29,0.39,-1.14,-2.83,-0.02,0.42,0.13,
            0.52,-0.03,-0.47,0.32,0.04,0.18,0.26,-0.14],
       "amount":0.0}'
```

## Step 9 --- Health check and tests

A health check that only reports "the bean exists" tells you nothing. Run a known vector through the model:

```
package com.example.fraud;

import org.springframework.boot.actuate.health.*;
import org.springframework.stereotype.Component;

@Component
public class ModelHealthIndicator implements HealthIndicator {

    private static final float[] PROBE = new float[30];   // replace with a real known-good row
    private final FraudScoringService service;

    public ModelHealthIndicator(FraudScoringService service) { this.service = service; }

    @Override
    public Health health() {
        try {
            var result = service.score(PROBE);
            return Float.isNaN(result.score())
                    ? Health.down().withDetail("reason", "model returned NaN").build()
                    : Health.up().withDetail("probeScore", result.score()).build();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}
```

Then a golden-vector test --- the single most valuable test in an ML service, because it catches the model/scaler mismatch that nothing else catches:

```
@SpringBootTest
class ScoringRegressionTest {

    @Autowired FraudScoringService service;

    @Test
    void knownFraudulentTransactionScoresHigh() {
        float[] features = { /* a row from test.csv with Class=1 */ };
        assertThat(service.score(features).score()).isGreaterThan(0.7f);
    }

    @Test
    void knownLegitimateTransactionScoresLow() {
        float[] features = { /* a row from test.csv with Class=0 */ };
        assertThat(service.score(features).score()).isLessThan(0.3f);
    }
}
```

Pull ten of each from `test.csv`, hard-code them, and let the test fail loudly whenever someone swaps the model without the matching scaler.

Also worth adding: a concurrency test that fires the same vector from 50 threads and asserts every response is identical. If you skipped the pool, this is what catches it.

## Step 10 --- Package and deploy

**Model location.** Bundling `.dnet` into the jar is simplest and gives you immutable, atomically-deployed builds. It also means every retrain is a full app deploy. The alternative --- mount from a volume or pull from S3 at startup --- decouples the two but demands you version the model/scaler pair rigorously and handle a bad artifact at boot. Start bundled; move it out when retrain frequency actually hurts.

**Dockerfile** --- the Deep Netts jars must be in the image, which means your build stage needs access to your internal repository:

```
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY settings.xml /root/.m2/settings.xml    # points at your internal Nexus
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:17-jre
COPY --from=build /app/target/fraud-service-*.jar /app/app.jar
ENV JAVA_OPTS="-Xmx1g -XX:MaxRAMPercentage=75"
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
```

Deep Netts is pure Java, so everything lives on the heap --- no off-heap surprises, but size `-Xmx` for `poolSize × model size` plus normal application overhead. Eight copies of a large network is eight times the memory.

**Readiness vs liveness.** Deserializing eight model instances at startup takes real time. Point your readiness probe at `/actuator/health/readiness` and give it a generous `initialDelaySeconds`, or Kubernetes will kill pods mid-warmup and you'll never reach a stable state.

## Step 11 --- Licensing, before you go live

The free tier is genuinely restrictive for a service like this. It permits deployment in **no more than one production environment** , requires annual revenue generated through the product under **USD 100,000** and total company revenue under **USD 1,000,000** , and explicitly prohibits use to operate or enable any hosted AI platform, managed service, or **SaaS offering**.

An internal fraud-scoring service inside a single production environment at a small company may fit. Anything customer-facing, multi-region, or sold as a service does not. Read the [EULA](https://www.deepnetts.com/end-user-license-agreement/) and talk to Deep Netts before you build a roadmap on the free tier --- I'm not a lawyer and this is a summary, not advice.

## Step 12 --- What you still need for production

The service works now. These are the things that separate it from a demo:

* **Retraining pipeline.** Fraud patterns change monthly. A model with no scheduled retrain is a depreciating asset. Automate steps 2--4 in CI and gate promotion on the threshold table, not on a single metric.
* **Drift monitoring.** Emit a histogram of scores. When the distribution shifts, the world changed before your metrics did. This is your earliest warning and it costs one Micrometer counter.
* **Shadow mode first.** Deploy scoring alongside your existing rules engine, log both decisions, change nothing. Run for a few weeks, compare, then act on the model. Never let a fresh model decline transactions on day one.
* **A kill switch.** A config flag that bypasses the model and falls back to rules, flippable without a deploy.
* **No PII in the feature vector.** The Kaggle data is anonymized for you. Your own features won't be --- decide deliberately what goes into the model and what gets logged.

## Reference: project layout

```
fraud-service/
├── pom.xml
├── data/
│   ├── train.csv                    # generated, gitignored
│   └── test.csv
└── src/
    ├── main/
    │   ├── java/com/example/fraud/
    │   │   ├── FraudApplication.java
    │   │   ├── FraudProperties.java
    │   │   ├── ModelConfig.java
    │   │   ├── Scaler.java
    │   │   ├── FraudScoringService.java
    │   │   ├── ScoringUnavailableException.java
    │   │   ├── ModelHealthIndicator.java
    │   │   ├── TransactionRequest.java
    │   │   ├── FraudController.java
    │   │   └── training/
    │   │       ├── SplitData.java
    │   │       └── TrainFraudModel.java
    │   └── resources/
    │       ├── application.yml
    │       └── model/
    │           ├── fraud-model.dnet
    │           └── scaler.json
    └── test/java/com/example/fraud/
        └── ScoringRegressionTest.java
```

Training classes living in the same module is fine to start. Split them into a separate `fraud-training` module once the training dependencies (tablesaw, plotting) start bloating your service image.

**Conclusion**

What you have now is a Java application that scores transactions in-process, with no Python runtime, no inference sidecar, and no network hop between your business logic and your model.

That's the real payoff of the pure-Java approach --- not raw speed, though microsecond predictions are pleasant, but the fact that one team owns the whole thing in one language with one deployment pipeline.

The model is a versioned artifact in your build, the scaler travels with it, and a bad prediction is debuggable with the same tools you'd use for any other bug in the service.
