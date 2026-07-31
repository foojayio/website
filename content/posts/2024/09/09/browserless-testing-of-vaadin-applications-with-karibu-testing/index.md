---
title: "Browserless Testing of Vaadin Applications with Karibu Testing"
slug: "browserless-testing-of-vaadin-applications-with-karibu-testing"
date: "2024-09-09T06:27:41+00:00"
lastmod: "2024-09-09T06:27:42+00:00"
description: "About a testing framework that stands out for its ability to run browserless testing, offering several advantages over traditional end-to-end testing approaches, such as Selenium, Playwright, or Cypress."
authors:
  - "simon-martinelli"
image: "pexels-tomas-malik-793526-26690667.jpg"
categories:
  - "Testing"
  - "Vaadin"
tags:
related_posts:
  - "blink-a-led-on-raspberry-pi-with-vaadin"
  - "book-review-modern-frontends-with-htmx"
  - "build-web-apps-in-pure-java-with-vaadin-flow"
enlighterjs: true
frozen: false
---

I**n modern web development, testing is essential for ensuring the reliability and performance of applications. For developers working with Vaadin, one of the best testing tools is Karibu Testing. This testing framework stands out for its ability to run browserless testing, offering several advantages over traditional end-to-end testing approaches, such as Selenium, Playwright, or Cypress.**

Introduction {#h2-0-introduction}
---------------------------------

This article will highlight the benefits of browserless testing with Karibu Testing and how it compares to the end-to-end testing approach (E2E).   

Karibu Testing is maintained by Martin Vysny, a senior software engineer at Vaadin.

#### **1. Speed and Efficiency**

Speed is one of the most significant advantages of browserless testing with Karibu Testing. Traditional end-to-end testing involves launching a browser, loading the entire application, and interacting with the user interface. This can be time-consuming and resource-intensive.

In contrast, Karibu Testing bypasses the need for a browser. Directly interacting with the server-side components of the Vaadin application allows tests to run much faster. This increase in speed means that developers can execute a more tests in less time, leading to faster feedback and a more efficient development cycle.

#### **2. Integration with Java and Spring Boot**

Karibu Testing integrates seamlessly with Java and Spring Boot. Tests written with Vaadin Karibu can be executed using popular Java testing frameworks like JUnit, and the results can be easily integrated into Continuous Integration (CI) pipelines.

Spring Boot's MockMVC also provides a way to perform server-side testing without launching a full web server. However, MockMVC is more focused on testing REST endpoints and Spring controllers, which means it does not fully support the component-based UI testing required in a Vaadin application. Karibu Testing, explicitly designed for Vaadin, offers a more natural and expressive API for testing Vaadin components and UI logic.

#### **3. Consistent Testing Environment**

When testing with a browser, various factors such as network speed, browser version, and hardware differences can introduce variability in test results. Browserless testing with Karibu Testing provides a more consistent environment by eliminating these variables. Tests are run directly on the server-side code, ensuring they are not affected by client-side discrepancies.

This consistency is particularly important for ensuring the reliability of test results. When tests fail, developers can be confident that the failure is due to an issue in the code rather than an external factor. This reduces the time spent troubleshooting and allows developers to focus on resolving the problem.

#### **4. Focused Testing on Application Logic**

Karibu Testing allows developers to focus on testing their applications' business logic and user interface components. By bypassing the need to test client-side rendering and browser interactions, developers can write tests that are more focused on the application's core functionality.

It's comparable to testing with MockMVC in Spring Boot. While MockMVC is excellent for testing RESTful APIs and Spring MVC controllers, Vaadin Karibu is better suited for testing the full stack of a Vaadin application, including its rich user interface components.

Getting Started {#h2-1-getting-started}
---------------------------------------

The documentation of Karibu Testing can be found on GitHub: <https://github.com/mvysny/karibu-testing>  

All code examples are from my [Java Track and Field](https://jtaf.ch/) project and the code is hosted on GitHub as well: <https://github.com/72services/jtaf4>

### Dependency {#h3-2-dependency}

First, you'll need to add the Karibu Testing dependency. In my project I use Maven but you can also use Gradle if you prefer this build tool.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;dependency&gt;
    &lt;groupId&gt;com.github.mvysny.kaributesting&lt;/groupId&gt;
    &lt;artifactId&gt;karibu-testing-v10-spring&lt;/artifactId&gt;
    &lt;version&gt;${karibu-testing.version}&lt;/version&gt;
    &lt;scope&gt;test&lt;/scope&gt;
&lt;/dependency&gt;</pre>

### Base Test Class {#h3-3-base-test-class}

Second, we want to create a base test class with the Karibu Testing configuration for convenience. The convenience class contains the setup and tear down of the Mock servlet and Vaadin stack. Plus a login method that lets you fake the user and roles and also a logout method.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@SpringBootTest
public abstract class KaribuTest {

    private static Routes routes;

    @Autowired
    protected ApplicationContext ctx;

    @BeforeAll
    public static void discoverRoutes() {
        Locale.setDefault(Locale.ENGLISH);
        routes = new Routes().autoDiscoverViews("ch.jtaf.ui");
    }

    @BeforeEach
    public void setup() {
        Function0&lt;UI&gt; uiFactory = UI::new;
        SpringServlet servlet = new MockSpringServlet(routes, ctx, uiFactory);
        MockVaadin.setup(uiFactory, servlet);
    }

    @AfterEach
    public void tearDown() {
        logout();
        MockVaadin.tearDown();
    }

    protected void login(String user, String pass, List&lt;String&gt; roles) {
        List&lt;SimpleGrantedAuthority&gt; authorities =
            roles.stream().map(role -&gt; new SimpleGrantedAuthority("ROLE_" + role)).toList();

        UserDetails userDetails = new User(user, pass, authorities);
        UsernamePasswordAuthenticationToken authReq = 
            new UsernamePasswordAuthenticationToken(userDetails, pass, authorities);
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(authReq);

        // however, you also need to make sure that ViewAccessChecker works properly;
        // that requires a correct MockRequest.userPrincipal and MockRequest.isUserInRole()
        FakeRequest request = (FakeRequest) VaadinServletRequest.getCurrent().getRequest();
        request.setUserPrincipalInt(authReq);
        request.setUserInRole((principal, role) -&gt; roles.contains(role));
    }

    protected void logout() {
        try {
            SecurityContextHolder.getContext().setAuthentication(null);
            if (VaadinServletRequest.getCurrent() != null) {
                FakeRequest request = (FakeRequest) VaadinServletRequest.getCurrent().getRequest();
                request.setUserPrincipalInt(null);
                request.setUserInRole((principal, role) -&gt; false);
            }
        } catch (IllegalStateException ignore) {
            // Ignored
        }
    }
}</pre>

### Writing a Test {#h3-4-writing-a-test}

As we want to test a protected view we first login and then navigate to the view we want to test. As mentioned above the login method fakes the login. But be aware that the application may access the user in the database so you should use a username that also exists in your database. Alternatively, you could use the UserDetailsService to load the user and roles from the database instead of faking it entirely.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">class ClubsViewTest extends KaribuTest {

    @BeforeEach
    public void login() {
        login("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="6112080c0e0f210c001315080f040d0d084f0209">[email&nbsp;protected]</a>", "", List.of(Role.ADMIN));

        UI.getCurrent().navigate(ClubsView.class);
    }
    ...
}</pre>

After login and navigation, we check the content of the grid, add a new club, and check if it was added. To ensure that the test doesn't affect other tests we delete the club and check if it was deleted.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Test
void add_club() {
    // Check content of clubs grid
    Grid&lt;ClubRecord&gt; clubsGrid = LocatorJ._get(Grid.class, spec -&gt; spec.withId("clubs-grid"));
    assertThat(GridKt._size(clubsGrid)).isEqualTo(4);
    assertThat(GridKt._get(clubsGrid, 0).getName()).isEqualTo("Erlach");

    // Add new club
    LocatorJ._get(Button.class, spec -&gt; spec.withId("add-button")).click();
    LocatorJ._assert(ClubDialog.class, 1);

    // Test maximize and restore
    Button toggle = LocatorJ._get(Button.class, spec -&gt; spec.withId("toggle"));
    toggle.click();
    toggle.click();

    LocatorJ._get(TextField.class, spec -&gt; spec.withCaption("Abbreviation")).setValue("Test");
    LocatorJ._get(TextField.class, spec -&gt; spec.withCaption("Name")).setValue("Test");
    LocatorJ._get(Button.class, spec -&gt; spec.withCaption("Save")).click();

    // Check if club was added
    assertThat(GridKt._size(clubsGrid)).isEqualTo(5);
    assertThat(GridKt._get(clubsGrid, 0).getName()).isEqualTo("Test");

    // Remove club
    GridKt._getCellComponent(clubsGrid, 0, "edit-column").getChildren()
          .filter(component -&gt; component instanceof Button).findFirst().map(component -&gt; (Button) component)
          .ifPresent(Button::click);

    ConfirmDialog confirmDialog = LocatorJ._get(ConfirmDialog.class);
    assertThat(confirmDialog.isOpened()).isTrue();
    LocatorJ._get(Button.class, spec -&gt; spec.withId("delete-confirm-dialog-confirm")).click();

    // Check if club was removed
    assertThat(GridKt._size(clubsGrid)).isEqualTo(4);
}</pre>

There are two classes of Karibu Testing involved: LocatorJ and GridKt. Karibu Testing is written in Kotlin and for Java there is a special LocatorJ class written in Java to access the components. Additionally, it provides helper classes for certain components like the Grid to access properties or execute methods like in the case above the size of the grid.

Conclusion {#h2-5-conclusion}
-----------------------------

Vaadin Karibu Testing offers a robust and efficient approach to testing Vaadin applications. Eliminating the need for a browser speeds up the testing process reduces complexity, and provides a more consistent testing environment.

Compared to Spring Boot's MockMVC, which is tailored for testing REST APIs and Spring controllers, Vaadin Karibu is specifically designed to test Vaadin's component-based architecture, making it the superior choice for developers working with Vaadin.

For developers looking to ensure the reliability of their Vaadin applications, adopting Vaadin Karibu Testing can lead to faster development cycles, more reliable tests, and a greater focus on delivering quality software.

*Originally published on [martinelli.ch](https://martinelli.ch/browserless-testing-of-vaadin-applications-with-karibu-testing/)*
