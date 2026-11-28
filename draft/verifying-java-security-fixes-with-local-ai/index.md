---
title: "Verifying Local AI-Generated Java Security Fixes with Regression Tests"
date: "2026-01-01"
description: "Use JUnit and Maven to verify that a Java patch keeps SQL and input separate, then rerun the same test in an offline Docker container."
authors:
  - "matthaios-stavrou"
image: "cover.jpg"
categories:
  - "Java Core"
  - "Security"
  - "Testing"
  - "AI"
---

A local model suggests a Java patch and explains why it should work. Before I apply it, I want to answer a narrower question. Can the same test detect the missing behavior before the change and confirm it afterward?

Consider a JDBC lookup by display name. I want a fixed SQL statement with a placeholder, and I want each caller's name bound through the JDBC driver. That gives the reviewer something concrete to check in a diff and something deterministic to assert in a test.

## Write down the JDBC contract

In the example below, `CustomerLookup.prepare` receives a connection, prepares a lookup and returns a statement to its caller. The caller owns the returned statement. The test checks that both `Alex` and `Taylor` reach JDBC through `setString`, with the SQL template held constant. It uses Mockito to observe the JDBC calls, so Maven can run it without a database.

Put these classes in the same package, with the test under `src/test/java`. The example uses JDK 21, JUnit Jupiter 5.12.2 and Mockito Core 5.18.0. With Maven, include `junit-jupiter` and `mockito-core` as test dependencies and use a JUnit Platform-aware version of Maven Surefire, such as 3.5.3.

```java
import java.sql.Connection;
import java.sql.PreparedStatement;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerLookupTest {
    @ParameterizedTest
    @ValueSource(strings = {"Alex", "Taylor"})
    void bindsEachRequestedName(String displayName) throws Exception {
        var connection = mock(Connection.class);
        var statement = mock(PreparedStatement.class);
        var expectedSql = "SELECT id FROM customer WHERE display_name = ?";
        when(connection.prepareStatement(expectedSql)).thenReturn(statement);

        try (var prepared = CustomerLookup.prepare(connection, displayName)) {
            var calls = inOrder(connection, statement);
            calls.verify(connection).prepareStatement(expectedSql);
            calls.verify(statement).setString(1, displayName);
            assertSame(statement, prepared);
        }
    }
}
```

Run the focused test with `mvn -B -Dtest=CustomerLookupTest test`. For a small baseline, use an unfinished lookup that prepares the same statement but binds a fixed sample name, `Alex`. The Alex case passes and the Taylor case fails on the binding assertion. That failure tells you the test can distinguish passing the caller's input from binding a fixed value. A compilation error or unresolved Maven dependency would give you no evidence about the binding.

## Apply the patch and rerun the same test

The implementation below binds the supplied value and transfers ownership of the prepared statement to the caller. If binding throws, the helper closes the statement and preserves the original exception.

```java
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

final class CustomerLookup {
    private static final String SQL =
            "SELECT id FROM customer WHERE display_name = ?";

    static PreparedStatement prepare(Connection connection, String displayName)
            throws SQLException {
        var statement = connection.prepareStatement(SQL);
        try {
            statement.setString(1, displayName);
            return statement;
        } catch (SQLException bindingFailure) {
            try {
                statement.close();
            } catch (SQLException closeFailure) {
                bindingFailure.addSuppressed(closeFailure);
            }
            throw bindingFailure;
        }
    }
}
```

Rerun the same Maven command with the test unchanged. Both cases should now pass. Review the diff to make sure execution uses this prepared statement and that the caller closes it. The unit test checks the JDBC interaction. In a real application, keep an integration test for the actual driver and schema, and run the project's existing suite before merging.

The JDBC API documents [how `PreparedStatement` binds typed values](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/PreparedStatement.html), and the [OWASP prevention guidance](https://cheatsheetseries.owasp.org/cheatsheets/SQL_Injection_Prevention_Cheat_Sheet.html) explains why parameterized queries matter.

## Repeat the test in an offline container

Prime a dedicated Maven dependency cache from a trusted checkout, using the same JDK and Maven versions as the test image. Run `mvn -B clean test` during that trusted setup to populate the cache. Use two disposable source snapshots for the verification runs. They should differ by the patch and carry the same test.

Set `MAVEN_IMAGE` to an already downloaded, digest-pinned Maven image with JDK 21. Set `MAVEN_REPO` to the absolute path of the seeded Maven artifact repository. Run the following command from each snapshot.

```bash
docker run --rm --pull=never --network none \
  --mount "type=bind,source=$PWD,target=/source,readonly" \
  --mount "type=bind,source=$MAVEN_REPO,target=/seed-m2,readonly" \
  "$MAVEN_IMAGE" sh -ec '
    cp -R /source /tmp/project
    cp -R /seed-m2 /tmp/maven-repository
    cd /tmp/project
    mvn -o -B -Dmaven.repo.local=/tmp/maven-repository \
      -Dtest=CustomerLookupTest clean test
  '
```

Docker's [none network driver](https://docs.docker.com/engine/network/drivers/none/) isolates the container's network stack, and [Maven's offline mode](https://maven.apache.org/ref/current/maven-embedder/cli.html) uses the seeded repository. Mount only the source snapshot and artifact cache. Leave credentials, personal Maven settings and the Docker socket outside the container. Preserve the failing assertion and the passing test summary together with the base revision and patch.

## Where Hushavyr and Ollama fit

I maintain [Hushavyr](https://github.com/mathias82/Hushavyr), an open-source extension available on the [Visual Studio Marketplace](https://marketplace.visualstudio.com/items?itemName=mathias82.hushavyr). I use it with Ollama for local code review. After reviewing a finding from `/security`, I can use `/prove` with its finding number to request a test and a patch. I read the proposed assertions and the patch before approving either. Hushavyr runs the verification in Docker. The Java example above is a minimal fixture you can run with a manually written test and patch to see what the check measures.

Hushavyr's [public Java benchmark notes](https://github.com/mathias82/Hushavyr/blob/main/docs/owasp-benchmark.md) report scanner behavior on OWASP's test suite. That's a measure of detection across a corpus. The two JUnit runs here provide evidence about one contract in one patch. When I review an AI-generated change, I want the reviewer to be able to identify that contract, inspect both outcomes and explain what the assertions cover.
