---
title: "Testing Emails with Testcontainers and Mailpit"
slug: "testing-emails-with-testcontainers-and-mailpit"
date: "2026-01-29T08:38:10+00:00"
lastmod: "2026-01-29T10:00:03+00:00"
description: "Testing email functionality is often painful. SMTP servers are external, tests become slow or flaky, and local setups differ from CI environments. As a result, many teams either mock the mail sender or skip proper email tests completely. - by Simon Martinelli"
authors:
  - "simon-martinelli"
image: "https://foojay.io/wp-content/uploads/2026/01/maven.png"
categories:
  - "Java"
  - "Spring"
  - "Testcontainers"
  - "Testing"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Testing email functionality is often painful. SMTP servers are external, tests become slow or flaky, and local setups differ from CI environments. As a result, many teams either mock the mail sender or skip proper email tests completely.

Both approaches are unsatisfying. Mocking does not test real behavior, and shared SMTP servers introduce hidden dependencies. What we really want is a real SMTP server that runs locally and in CI, is fully isolated per test run, and allows us to inspect sent emails easily.

This is exactly what Testcontainers and Mailpit provide.

What is Mailpit? {#h2-0-what-is-mailpit}
----------------------------------------

Mailpit is a small and fast SMTP testing server with a modern web UI. Instead of delivering emails, it captures them and exposes everything through an HTTP API and a browser-based inbox. Applications can send emails via SMTP as usual, while tests can inspect the captured messages programmatically or visually in the UI.

This makes Mailpit ideal for automated tests and local development.

Why Testcontainers fits perfectly {#h2-1-why-testcontainers-fits-perfectly}
---------------------------------------------------------------------------

Testcontainers allows you to start Docker containers directly from your tests. Containers are created on demand, work the same locally and in CI, and are automatically cleaned up afterwards. There is no manual setup and no shared infrastructure.

Since Mailpit already provides an official Docker image, combining it with Testcontainers is a natural fit.

The Mailpit Testcontainer module {#h2-2-the-mailpit-testcontainer-module}
-------------------------------------------------------------------------

To make this integration easy, I created a dedicated Testcontainers module for Mailpit: <https://github.com/martinellich/testcontainers-mailpit>

It provides a ready-to-use `MailpitContainer`, a Java client for the Mailpit API, and convenient test assertions.

### Maven dependency {#h3-3-maven-dependency}

Add the dependency to your test scope:

<pre class="EnlighterJSRAW" data-enlighter-language="xml" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;dependency&gt;
  &lt;groupId&gt;ch.martinelli.oss&lt;/groupId&gt;
  &lt;artifactId&gt;testcontainers-mailpit&lt;/artifactId&gt;
  &lt;version&gt;1.2.0&lt;/version&gt;
  &lt;scope&gt;test&lt;/scope&gt;
&lt;/dependency&gt;</pre>

Using Spring Boot with @ServiceConnection {#h2-4-using-spring-boot-with-serviceconnection}
------------------------------------------------------------------------------------------

If you use Spring Boot 3.1 or newer, the cleanest solution is `@ServiceConnection`. Spring Boot will automatically wire the SMTP connection and also provide a `MailpitClient` bean.

You only need a small test configuration:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

  @Bean
  @ServiceConnection
  MailpitContainer mailpitContainer() {
    return new MailpitContainer();
  }
}</pre>

In your test, you can now use `JavaMailSender` as usual, and verify emails via `MailpitClient`:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@SpringBootTest
@Import(TestcontainersConfiguration.class)
class EmailServiceTest {

  @Autowired
  JavaMailSender mailSender;

  @Autowired
  MailpitClient client;

  @Test
  void shouldSendAndVerifyEmail() {
    var msg = new SimpleMailMessage();
    msg.setFrom("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="fc92938e998c9085bc91859d8c8cd29f9391">[email&nbsp;protected]</a>");
    msg.setTo("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="295c5a4c5b694c51484459454c074a4644">[email&nbsp;protected]</a>");
    msg.setSubject("Welcome");
    msg.setText("Hello!");

    mailSender.send(msg);

    var messages = client.getAllMessages();
    assertThat(messages).hasSize(1);
    assertThat(messages.get(0).subject()).isEqualTo("Welcome");
  }
}</pre>

No mail properties are required. Spring Boot derives everything from the running container.

Using Mailpit without Spring Boot {#h2-5-using-mailpit-without-spring-boot}
---------------------------------------------------------------------------

The Mailpit container can also be used in plain JUnit tests. In this case, you configure the SMTP host and port manually and then verify messages via the container's client.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Testcontainers
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
    message.setFrom(new InternetAddress("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="4a392f242e2f380a2f322b273a262f64292527">[email&nbsp;protected]</a>"));
    message.setRecipient(RecipientType.TO, new InternetAddress("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="027067616b726b676c7642677a636f726e672c616d6f">[email&nbsp;protected]</a>"));
    message.setSubject("Test Subject");
    message.setText("Hello, this is a test email!");

    Transport.send(message);

    var messages = mailpit.getClient().getAllMessages();
    assertThat(messages).hasSize(1);
    assertThat(messages.get(0).subject()).isEqualTo("Test Subject");
  }
}</pre>

This approach works well if you are not using Spring Boot or want full control over the mail setup.

Fluent AssertJ assertions {#h2-6-fluent-assertj-assertions}
-----------------------------------------------------------

Recent versions of the library include AssertJ-style assertions that make tests much more readable. Instead of manually fetching messages, you can express expectations directly.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import static ch.martinelli.oss.testcontainers.mailpit.assertions.MailpitAssertions.assertThat;

@Test
void shouldVerifyEmailSent() {
  // send email...

  assertThat(mailpit)
      .hasMessages()
      .hasMessageCount(1)
      .hasMessageWithSubject("Welcome")
      .hasMessageTo("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="90e5e3f5e2d0f5e8f1fde0fcf5bef3fffd">[email&nbsp;protected]</a>")
      .hasMessageFrom("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="4729283522372b3e072a3e2637376924282a">[email&nbsp;protected]</a>");
}</pre>

You can also assert details of a specific message:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Test
void shouldVerifyMessageDetails() {
  // send email...

  assertThat(mailpit)
      .firstMessage()
      .hasSubject("Order Confirmation")
      .isFrom("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="a5cad7c1c0d7d6e5d6cdcad58bc6cac8">[email&nbsp;protected]</a>")
      .hasRecipient("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="ccafb9bfb8a3a1a9be8ca9b4ada1bca0a9e2afa3a1">[email&nbsp;protected]</a>")
      .hasNoAttachments()
      .hasSnippetContaining("Thank you");
}</pre>

Waiting for asynchronous emails {#h2-7-waiting-for-asynchronous-emails}
-----------------------------------------------------------------------

Many applications send emails asynchronously. For these cases, the assertions support waiting with timeouts and polling.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Test
void shouldWaitForAsyncEmail() {
  // trigger async email sending...

  assertThat(mailpit)
      .withTimeout(Duration.ofSeconds(30))
      .withPollInterval(Duration.ofSeconds(1))
      .awaitMessage()
      .withSubject("Password Reset")
      .to("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="b4c1c7d1c6f4d1ccd5d9c4d8d19ad7dbd9">[email&nbsp;protected]</a>")
      .isPresent();
}</pre>

This removes the need for manual `Thread.sleep` calls and makes async tests reliable.

Why this approach works well {#h2-8-why-this-approach-works-well}
-----------------------------------------------------------------

With Mailpit and Testcontainers, you test the full email flow end-to-end. There are no mocks, no shared servers, and no environment-specific configuration. The same setup works locally and in CI, and debugging is easy thanks to the Mailpit web UI.

Most importantly, you test what you actually ship.

Conclusion {#h2-9-conclusion}
-----------------------------

Email testing does not need to be complex. A small Testcontainer and a lightweight SMTP server are enough to get reliable, readable, and maintainable tests. Mailpit fits naturally into modern Spring Boot and JUnit setups and removes a common source of fragile tests.

Give it a try. Keep IT simple.

*This article was originally published on <https://martinelli.ch/testing-emails-with-testcontainers-and-mailpit/>*
