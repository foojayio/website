---
title: "UCanAccess: The Modern Pure-Java Bridge to Microsoft Access"
date: "2026-05-06T08:44:00+00:00"
updated: "2026-10-23T20:00:00+00:00"
description: "Learn how to connect Java applications to Microsoft Access (.accdb/.mdb) using UCanAccess. A pure-Java, open-source JDBC driver for cross-platform database access."
authors:
  - "markus-spann"
image: "Favicon-3-2.png"
categories:
  - "Databases"
  - "Java Core"
  - "sql"
  - "Tools"
  - "Tutorials"
related_posts:
  - "idempotent-spring-boot-starter"
  - "boxlang-1-14-0-sets-ranges-inner-classes-and-a-runtime-that-talks-back"
  - "aggregation-optimization-in-mongodb-unnecessary-unwinds-part-2"
  - "boxlang-1-14-0-query-transformers-take-full-control-of-your-query-results"
---

Microsoft Access databases are everywhere. Decades of `.mdb` and `.accdb` files silently power spreadsheets, small business applications, and legacy data stores across organizations of all sizes. Yet for Java developers, connecting to these files has historically meant wrestling with native Windows libraries, ODBC bridges, and platform-specific hacks.

{{< img src="ucanaccess-logo.svg" class="aligncenter size-full is-resized" alt="UCanAccess Logo" style="width:320px" >}}

UCanAccess puts an end to that. It is an open-source, pure-Java JDBC driver that lets you read and write Microsoft Access databases (`.mdb` and `.accdb`) just like any other SQL database. No native drivers, no Windows dependency, no friction.

{{< note >}}
*This article was updated on 24 October 2026: the dependency versions are now 5.2.0, and the tool compatibility, requirements and quality sections were revised.*
{{< /note >}}

## A Brief History

UCanAccess has been around for more than a decade and quickly became the go-to solution for Java-to-Access connectivity. The project was invaluable, but development went quiet around 2020. Development has continued on GitHub since 2022 under its current maintainer, who had already contributed to the original project.

This is not just a maintenance effort. The codebase is being modernized step by step: JUnit 5 tests, Checkstyle and PMD on every build, minimal dependencies with no known CVEs, and steadily improving test coverage.

## What Problems Does It Solve?

### The Disappeared Bridge

Java dropped the `sun.jdbc.odbc.JdbcOdbcBridge` in Java 8, the legacy workaround for reaching Access databases via ODBC. Many projects were left stranded. UCanAccess serves as its pure-Java replacement, filling that gap permanently.

### Cross-Platform Compatibility

Because UCanAccess is 100% Java with zero native code, it runs on Linux, macOS, and Windows alike. This is essential for containerized environments, CI/CD pipelines, and server-side applications where installing Windows ODBC drivers is not an option or undesired.

### Seamless Integration for Tooling

UCanAccess is not just for bespoke Java applications. Some tools already ship with it: DBeaver downloads UCanAccess automatically as its default MS Access driver, and recent SQuirreL SQL versions come with a predefined UCanAccess driver entry. Other tools that speak JDBC, such as LibreOffice Base, OpenOffice, NetBeans and DbVisualizer, can connect to Access files through UCanAccess as well. The [project documentation](https://spannm.github.io/ucanaccess/30-jdbc-client-tools.html) describes the setup for each of them.

## Tech Stack & Requirements

- Java 11 or higher. The test suite runs on Java 11, 17 and 21.
- Maven or Gradle to pull in the dependency.
- Core dependencies:
  - [Jackcess](https://github.com/spannm/jackcess) handles the low-level binary Access file format.
  - [HSQLDB](https://hsqldb.org/) provides the in-memory SQL engine for query translation.
  - [Jackcess Encrypt](https://github.com/spannm/jackcess-encrypt) is optional and only needed for encrypted databases.
- License: Apache License 2.0

## Getting Started

### Add the Dependency

Maven (`pom.xml`):

```xml
<dependency>
    <groupId>io.github.spannm</groupId>
    <artifactId>ucanaccess</artifactId>
    <version>5.2.0</version>
</dependency>
```

Gradle (Kotlin DSL):

```kotlin
implementation("io.github.spannm:ucanaccess:5.2.0")
```

If you are moving over from the old `net.sf.ucanaccess` artifact, the switch is usually just a change of the group id. The [README](https://github.com/spannm/ucanaccess#-migrating-from-netsfucanaccess) covers the details.

### Connect and Query

Connecting is as simple as using any standard JDBC driver:

```java
import java.sql.*;

// path to that legacy file someone "forgot" to migrate in 2010
String url = "jdbc:ucanaccess:///data/legacy_system_final_v2_REAL.accdb";

try (Connection conn = DriverManager.getConnection(url);
     Statement stmt = conn.createStatement();
     ResultSet rs = stmt.executeQuery("SELECT Name, Role FROM Foojay")) {

    while (rs.next()) {
        System.out.printf("Friend found: %s (%s)%n",
                          rs.getString("Name"), rs.getString("Role"));
    }
} catch (SQLException ex) {
    System.err.printf("Database access failed: %s%n", ex);
}
```

No class registration, no native driver setup.
Standard `DriverManager.getConnection()` is all you'll need.

### Write Data Back

UCanAccess supports full DML (Data Manipulation Language), both reads and writes. Even DDL operations like `CREATE TABLE` and `ALTER TABLE` are supported:

```java
String insert = "INSERT INTO passwd (Id, UserName, Password) VALUES (?, ?, ?)";

try (Connection conn = DriverManager.getConnection(url);
     PreparedStatement ps = conn.prepareStatement(insert)) {

    // adding some highly secure legacy credentials
    ps.setInt(1, 42);
    ps.setString(2, "admin");
    ps.setString(3, "alligator3");

    int rowsAffected = ps.executeUpdate();

    System.out.printf("Inserted %d row(s)%n", rowsAffected);
} catch (SQLException ex) {
    System.err.printf("Failed to update credentials: %s%n", ex);
}
```

### Access-Specific Functions

One common pain point when working with Access queries is the use of Access-specific SQL functions that standard JDBC drivers simply do not understand. UCanAccess ships with built-in emulations of the most common ones: `IIf()`, `Nz()`, `Format()`, date functions like `DateDiff()`, and financial functions like `PMT()` and `PV()`. Queries originally written for Access often run without any modification at all.

### Uber JAR for Non-Maven Projects

If you are integrating UCanAccess into a tool that does not use Maven, such as LibreOffice Base or SQuirreL SQL, the project ships an uber JAR: a single self-contained archive with all dependencies bundled in. You can download it directly from [Maven Central](https://central.sonatype.com/artifact/io.github.spannm/ucanaccess) (the file with the `uber` classifier) and drop it onto the classpath without any further setup.

## Quality & Maintenance

Every build runs Checkstyle and PMD, and the JUnit 5 suite is executed on Linux and Windows against Java 11, 17 and 21 via GitHub Actions. CodeQL and SonarCloud analyses run alongside. Dependencies are updated regularly to keep the library free of known CVEs, and security reports are handled through GitHub security advisories. Every release is published to Maven Central.

The project is also still fixing long-standing behavior. Recent releases, for example, made the date functions respect daylight saving time changes and corrected `DateDiff` results for months and quarters.

## Get Involved

UCanAccess lives at [github.com/spannm/ucanaccess](https://github.com/spannm/ucanaccess). Whether you have a bug to report, a feature idea, or want to contribute a test case, contributions are warmly welcomed.

If UCanAccess has helped you keep a legacy system running or simplified a migration, consider dropping a star on GitHub. It helps the project stay visible and signals to the community that this bridge is alive and well-maintained.

*UCanAccess is licensed under the Apache License 2.0 and available on [Maven Central](https://central.sonatype.com/artifact/io.github.spannm/ucanaccess).*
