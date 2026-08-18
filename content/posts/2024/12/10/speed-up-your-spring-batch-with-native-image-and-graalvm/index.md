---
title: "Speed up your Spring Batch with Native Image and GraalVM"
date: "2024-12-10T15:47:53+00:00"
lastmod: "2025-04-02T06:55:24+00:00"
description: "Learn how to speed up your Spring Batch jobs using GraalVM Native Image! Discover how to compile batch apps into native executables and more!"
authors:
  - "vincent-vauban"
image: "speedup-batch.png"
categories:
  - "Cloud"
  - "Java"
  - "Performance"
  - "Spring"
  - "Tools"
  - "Videos"
related_posts:
  - "prevent-ldap-injection-in-java-with-springboot"
  - "springboot-3-2-crac"
  - "openrewrite-migrate-to-spring-boot-3-2"
  - "a-guide-to-creating-javafx-native-images"
frozen: false
---

**Spring Batch is often used for** data processing **jobs that don't run continuously.** Instead, they start, process, and stop,**which makes them a perfect candidate for GraalVM Native Image.** Unlike traditional Java applications that require a long JVM startup time, Native Images execute almost instantly, giving a significant performance boost.

In this article, you'll learn how to build a Spring Batch application that reads a CSV file, logs its content, and writes it to a PostgreSQL database.**We'll see how to compile it into a Native Image using GraalVM** , and most importantly**, we'll benchmark the difference between** running it as **a traditional JAR and** as **a native binary**.

1. Definitions
2. Requirement
3. Create a Spring Batch app
4. Compile it into a Native Image
5. Benchmark

🔵⚪⚪⚪⚪⚪⚪⚪

## What is a Native Image?

**A Native Image is a standalone executable** that includes everything the application needs to run *(classes, libraries, and the JVM itself)* in one package. Unlike JVM-based JAR files, **native images do not require a JVM at runtime**, which means:

* Faster startup times (up to 50x faster)
* Lower memory usage (since there's no JVM)
* There is no need for JVM warm-up (perfect for short-lived apps like batch jobs)

## 🏆 What is GraalVM?

**GraalVM is a universal virtual machine** that can execute applications written in Java, Kotlin, Scala, and other languages. **One of its** most powerful **features is the Native Image capabilit** y, which converts Java applications into ahead-of-time (AOT) compiled executables. **This means the Java bytecode is turned into platform-specific machine code**.

When you compile a Spring Boot application into a native image, you remove the need for the JVM, which leads to instant start times and minimal resource usage.

🔵🔵⚪⚪⚪⚪⚪⚪

Before starting, ensure you have the following tools installed on your machine:

### GraalVM:

Download and install from GraalVM Downloads.  
<https://www.graalvm.org/downloads/>

### Visual Studio and Visual Studio Code:

Install Visual Studio (for C++ tools), as GraalVM uses native compilers.  
<https://www.graalvm.org/latest/getting-started/windows/>  

{{< img src="desktop_development_with_C-700x394.png" class="size-medium" alt="Speed up your Spring Batch with Native Image and GraalVM" width="700" height="394" >}}

Speed up your Spring Batch with Native Image and GraalVM{#caption-attachment-114938}

### PostgreSQL:

Run a PostgreSQL container with Docker  
`docker run --name postgres -e POSTGRES_PASSWORD=mysecretpassword -d -p 5432:5432 postgres`

🔵🔵🔵⚪⚪⚪⚪⚪

### Spring Initializr:

Go to Spring Initializr: <https://start.spring.io>  

{{< img src="spring-init-native-image-700x348.png" class="size-medium" alt="Speed up your Spring Batch with Native Image and GraalVM" width="700" height="348" >}}

Speed up your Spring Batch with Native Image and GraalVM{#caption-attachment-114939}

Add the following dependencies:

* Spring Batch
* PostgreSQL Driver
* Spring Data JPA
* Spring Cloud Native

### Add the implementation

Here, I defined a batch for a billing service.

#### The business object

```java
@Entity
public class Billing {

    private int year;
    private int month;
    private int accountNumber;
    @Id
    private String phoneNumber;
    private double amount;
    private int calls;
    private int messages;
```

#### The batch job with its tasks: reading a CSV, logging it, and writing it to DB

```java
@Configuration
public class BillingJobConfig {

    private final EntityManagerFactory entityManagerFactory;

    public BillingJobConfig(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Bean
    public Job billingJob(JobRepository jobRepository, Step billingStep) {
        return new JobBuilder("billingJob", jobRepository)
                .start(billingStep)
                .build();
    }

    @Bean
    public Step billingStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("billingStep", jobRepository)
                .<Billing, Billing>chunk(10, transactionManager)
                .reader(billingItemReader())
                .processor(billingProcessor())
                .writer(billingItemWriter())
                .build();
    }

    @Bean
    public FlatFileItemReader<Billing> billingItemReader() {
        return new FlatFileItemReaderBuilder<Billing>()
                .name("billingItemReader")
                //relative paths mess up with native images -- so put an absolute path
                .resource(new FileSystemResource(
                        "C:/_dev/foojay/nativeBatch/nativeBatch/src/main/resources/billing-2023-01.csv")) // absolute
                                                                                                          // path
                .strict(false) // Turn off strict mode
                .delimited()
                .delimiter(",")
                .names("year", "month", "accountNumber", "phoneNumber", "amount", "calls", "messages")
                .targetType(Billing.class)
                .build();
    }

    @Bean
    public ItemProcessor<Billing, Billing> billingProcessor() {
        return billing -> {
            System.out.println(billing);

            // Example processing logic (optional)
            // billing.setAmount(billing.getAmount() * 1.1); // Apply a 10% increase
            // (optional)
            return billing;
        };
    }

    @Bean
    public JpaItemWriter<Billing> billingItemWriter() {
        JpaItemWriter<Billing> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    @Bean
    public CommandLineRunner runJob(JobLauncher jobLauncher, Job billingJob) {
        return args -> {
            var jobParameters = new JobParametersBuilder()
                    .addString("input.file", "src/main/resources/billing-2023-01.csv")
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(billingJob, jobParameters);
        };
    }

}
```

Full complete code: <https://github.com/vinny59200/spring-batch-native-image>

🔵🔵🔵🔵⚪⚪⚪⚪

Commands to Compile

1. Open Visual Code, open a terminal
2. Clear previous builds: `mvn clean package`
3. Compile to Native Image: `./mvnw -Pnative native:compile -DskipTests`*(It is a bit long --1 or 2min)*

🔵🔵🔵🔵🔵⚪⚪⚪

### Without Native Image

Run it using the standard JVM-based JAR:

`java -jar target/nativeBatch-0.0.1-SNAPSHOT.jar`**⏱️ Execution time: \~4 seconds**

### With Native Image

Run it using the compiled native image:

`./target/nativeBatch`**⏱️ Execution time: \~0.2 seconds**

## Results Summary

| 𝐄𝐗𝐄𝐂𝐔𝐓𝐈𝐎𝐍 | 𝐖𝐈𝐓𝐇𝐎𝐔𝐓 𝐍𝐀𝐓𝐈𝐕𝐄 | 𝐖𝐈𝐓𝐇 𝐍𝐀𝐓𝐈𝐕𝐄 |
|--------------------|-----------------------------|-----------------------|
| Startup Time       | 4 seconds                   | 0.2 seconds           |
| Resource Usage     | Higher (JVM)                | Lower (Native)        |

## 📊 Why Native Image Wins

| 𝐀𝐒𝐏𝐄𝐂𝐓 | 𝐖𝐈𝐓𝐇𝐎𝐔𝐓 𝐍𝐀𝐓𝐈𝐕𝐄 |  𝐖𝐈𝐓𝐇 𝐍𝐀𝐓𝐈𝐕𝐄  |
|--------------|-----------------------------|-------------------------|
| Startup Time | Slow (JVM Warm-up)          | Instant (Native Binary) |
| Memory Usage | High (JVM required)         | Low (No JVM)            |
| Footprint    | Requires JVM + JAR          | Single Executable       |
| Use Case     | Long-running Jobs           | Short, On-Demand Jobs   |

🔵🔵🔵🔵🔵🔵⚪⚪

**For Spring Batch** applications, **Native Images are a game-change**r.

1. **Instantaneous** startup: From 4 seconds to 0.2 seconds.
2. **Perfect for batch jobs**: Jobs that start, process, and exit benefit the most.
3. **Lower memory usage**: No JVM, no warm-up, no extra overhead.

By **using GraalVM Native Imag** e, you can build **batch jobs** that **are fast, efficient, and perfectly suited for cloud** environments where "scale to zero" is essential.

**If you're dealing with micro-batch jobs that run for seconds, Native Image is a must-have.** If your batch jobs are long-lived (running for hours), the benefit is less significant, but for fast, one-shot batch jobs, Native Image is unbeatable.
> **🚀 Switch to Native Image for Spring Batch. Your jobs will love it. 🚀**

🔵🔵🔵🔵🔵🔵🔵⚪

{{< youtube kFVwJSKVckA >}}

🔵🔵🔵🔵🔵🔵🔵🔵

* <https://foojay.io/today/prevent-ldap-injection-in-java-with-springboot/>
* <https://foojay.io/today/a-simple-service-with-spring-boot/>
* <https://youtu.be/_oXnnQcD_wc> (Legal JVM Dopes For Your Apps 🇬🇧 --- Dmitri Chuyko)
