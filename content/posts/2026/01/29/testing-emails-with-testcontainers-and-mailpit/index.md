---
title: "Testing Emails with Testcontainers and Mailpit"
slug: "testing-emails-with-testcontainers-and-mailpit"
date: "2026-01-29T08:38:10+00:00"
lastmod: "2026-01-29T10:00:03+00:00"
description: "Testing email functionality is often painful. SMTP servers are external, tests become slow or flaky, and local setups differ from CI environments. As a result, many teams either mock the mail sender or skip proper email tests completely. - by Simon Martinelli"
authors:
  - "simon-martinelli"
image: "maven.png"
categories:
  - "Java"
  - "Spring"
  - "Testcontainers"
  - "Testing"
tags:
related_posts:
  - "browserless-testing-of-vaadin-applications-with-karibu-testing"
  - "foojay-podcast-36"
  - "foojay-podcast-46"
  - "foojay-podcast-53"
enlighterjs: true
frozen: false
---

Testing email functionality is often painful. SMTP servers are external, tests become slow or flaky, and local setups differ from CI environments. As a result, many teams either mock the mail sender or skip proper email tests completely.

Both approaches are unsatisfying. Mocking does not test real behavior, and shared SMTP servers introduce hidden dependencies. What we really want is a real SMTP server that runs locally and in CI, is fully isolated per test run, and allows us to inspect sent emails easily.

This is exactly what Testcontainers and Mailpit provide.

## What is Mailpit?

Mailpit is a small and fast SMTP testing server with a modern web UI. Instead of delivering emails, it captures them and exposes everything through an HTTP API and a browser-based inbox. Applications can send emails via SMTP as usual, while tests can inspect the captured messages programmatically or visually in the UI.

This makes Mailpit ideal for automated tests and local development.

## Why Testcontainers fits perfectly

Testcontainers allows you to start Docker containers directly from your tests. Containers are created on demand, work the same locally and in CI, and are automatically cleaned up afterwards. There is no manual setup and no shared infrastructure.

Since Mailpit already provides an official Docker image, combining it with Testcontainers is a natural fit.

## The Mailpit Testcontainer module

To make this integration easy, I created a dedicated Testcontainers module for Mailpit: <https://github.com/martinellich/testcontainers-mailpit>

It provides a ready-to-use `MailpitContainer`, a Java client for the Mailpit API, and convenient test assertions.

### Maven dependency

Add the dependency to your test scope:

```xml
<dependency>
  <groupId>ch.martinelli.oss</groupId>
  <artifactId>testcontainers-mailpit</artifactId>
  <version>1.2.0</version>
  <scope>test</scope>
</dependency>
```

## Using Spring Boot with @ServiceConnection

If you use Spring Boot 3.1 or newer, the cleanest solution is `@ServiceConnection`. Spring Boot will automatically wire the SMTP connection and also provide a `MailpitClient` bean.

You only need a small test configuration:

```java
@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

  @Bean
  @ServiceConnection
  MailpitContainer mailpitContainer() {
    return new MailpitContainer();
  }
}
```

In your test, you can now use `JavaMailSender` as usual, and verify emails via `MailpitClient`:

```java
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class EmailServiceTest {

  @Autowired
  JavaMailSender mailSender;

  @Autowired
  MailpitClient client;

  @Test
  void shouldSendAndVerifyEmail() {
    var msg = new SimpleMailMessage();
    msg.setFrom("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="b2dcddc0d7c2decbf2dfcbd3c2c29cd1dddf">[email protected]</a>");
    msg.setTo("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="720701170032170a131f021e175c111d1f">[email protected]</a>");
    msg.setSubject("Welcome");
    msg.setText("Hello!");

    mailSender.send(msg);

    var messages = client.getAllMessages();
    assertThat(messages).hasSize(1);
    assertThat(messages.get(0).subject()).isEqualTo("Welcome");
  }
}
```

No mail properties are required. Spring Boot derives everything from the running container.

## Using Mailpit without Spring Boot

The Mailpit container can also be used in plain JUnit tests. In this case, you configure the SMTP host and port manually and then verify messages via the container's client.

```java
@Testcontainers
class PlainEmailTest {

  @Container
  static MailpitContainer mailpit = new MailpitContainer();

  @Test
  void shouldSendEmail() throws Exception {
    Properties props = new Properties();
    props.put("mail.smtp.host", mailpit.getSmtpHost());
    props.put("mail.smtp.port", String.valueOf(mailpit.getSmtpPort()));

    Session session = Session.getInstance(props);

    MimeMessage message = new MimeMessage(session);
    message.setFrom(new InternetAddress("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="abd8cec5cfced9ebced3cac6dbc7ce85c8c4c6">[email protected]</a>"));
    message.setRecipient(RecipientType.TO, new InternetAddress("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="770512141e071e12190337120f161a071b125914181a">[email protected]</a>"));
    message.setSubject("Test Subject");
    message.setText("Hello, this is a test email!");

    Transport.send(message);

    var messages = mailpit.getClient().getAllMessages();
    assertThat(messages).hasSize(1);
    assertThat(messages.get(0).subject()).isEqualTo("Test Subject");
  }
}
```

This approach works well if you are not using Spring Boot or want full control over the mail setup.

## Fluent AssertJ assertions

Recent versions of the library include AssertJ-style assertions that make tests much more readable. Instead of manually fetching messages, you can express expectations directly.

```java
import static ch.martinelli.oss.testcontainers.mailpit.assertions.MailpitAssertions.assertThat;

@Test
void shouldVerifyEmailSent() {
  // send email...

  assertThat(mailpit)
      .hasMessages()
      .hasMessageCount(1)
      .hasMessageWithSubject("Welcome")
      .hasMessageTo("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="9aefe9ffe8daffe2fbf7eaf6ffb4f9f5f7">[email protected]</a>")
      .hasMessageFrom("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="0e60617c6b7e62774e63776f7e7e206d6163">[email protected]</a>");
}
```

You can also assert details of a specific message:

```java
@Test
void shouldVerifyMessageDetails() {
  // send email...

  assertThat(mailpit)
      .firstMessage()
      .hasSubject("Order Confirmation")
      .isFrom("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="0d627f69687f7e4d7e65627d236e6260">[email protected]</a>")
      .hasRecipient("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="c9aabcbabda6a4acbb89acb1a8a4b9a5ace7aaa6a4">[email protected]</a>")
      .hasNoAttachments()
      .hasSnippetContaining("Thank you");
}
```

## Waiting for asynchronous emails

Many applications send emails asynchronously. For these cases, the assertions support waiting with timeouts and polling.

```java
@Test
void shouldWaitForAsyncEmail() {
  // trigger async email sending...

  assertThat(mailpit)
      .withTimeout(Duration.ofSeconds(30))
      .withPollInterval(Duration.ofSeconds(1))
      .awaitMessage()
      .withSubject("Password Reset")
      .to("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="542127312614312c35392438317a373b39">[email protected]</a>")
      .isPresent();
}
```

This removes the need for manual `Thread.sleep` calls and makes async tests reliable.

## Why this approach works well

With Mailpit and Testcontainers, you test the full email flow end-to-end. There are no mocks, no shared servers, and no environment-specific configuration. The same setup works locally and in CI, and debugging is easy thanks to the Mailpit web UI.

Most importantly, you test what you actually ship.

## Conclusion

Email testing does not need to be complex. A small Testcontainer and a lightweight SMTP server are enough to get reliable, readable, and maintainable tests. Mailpit fits naturally into modern Spring Boot and JUnit setups and removes a common source of fragile tests.

Give it a try. Keep IT simple.

*This article was originally published on <https://martinelli.ch/testing-emails-with-testcontainers-and-mailpit/>*
