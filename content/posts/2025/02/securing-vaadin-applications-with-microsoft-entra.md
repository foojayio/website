---
title: "Securing Vaadin Applications with Microsoft Entra"
slug: "securing-vaadin-applications-with-microsoft-entra"
date: "2025-02-26T12:55:43+00:00"
lastmod: "2025-02-26T12:57:16+00:00"
description: "Many companies use Microsoft 365, so letting users log in with their Microsoft account is a good choice. This blog post shows how to secure your Vaadin - by Simon Martinelli"
canonical: "https://martinelli.ch/securing-vaadin-applications-with-microsoft-entra/"
authors:
  - "simon-martinelli"
image: "https://foojay.io/wp-content/uploads/2022/05/VaadinLogo_RGB_1000x310.png"
categories:
  - "Security"
  - "Spring"
  - "Vaadin"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Many companies use Microsoft 365, so letting users log in with their Microsoft account is a good choice. This blog post shows how to secure your Vaadin applications using Microsoft Entra for authentication and authorization and explains how Karibu Testing must be configured.

Step 1: Create an Application in Entra {#h2-0-step-1-create-an-application-in-entra}
------------------------------------------------------------------------------------

The first step is to create an application, configure roles, assign users, and set the redirect URI.

To create an application, log in to http://entra.microsoft.com and select "Applications" -\> "Enterprise applications." There, you can create a new application. Select "Register an application to integrate with Microsoft Entra ID (App you're developing)."

Set a name and add a Redirect URI like in the screenshot. Choose Web and set `http://localhost:8080/login/oauth2/code/` as the URI. As you can see, this URI is application-environment-specific, and you will need to create an app registration per stage (dev, test, production, etc.).
![](/images/posts/2025/02/securing-vaadin-applications-with-microsoft-entra/image-9-1536x1000-1-1024x667.png)

### Create App Role {#h3-1-create-app-role}

We want to use role-based security in our application. To create an app role, go to "App registrations" and select the application. Click on "App roles":
![](/images/posts/2025/02/securing-vaadin-applications-with-microsoft-entra/image-7-1536x705-1-1024x470.png)

And create the app role. In this case, we will create an Administrator account. The value will be what you will get in the JWT token. I prefer to have the role names in uppercase.
![](/images/posts/2025/02/securing-vaadin-applications-with-microsoft-entra/image-2-768x618-1.png)

### Assign Users {#h3-2-assign-users}

Once you've created your application role, return to "Enterprise applications" and click "Users and groups." You can assign existing users or groups to the application roles:
![](/images/posts/2025/02/securing-vaadin-applications-with-microsoft-entra/image-3-1024x483.png)

### Create Client Secret {#h3-3-create-client-secret}

To be able to connect to Entra from the application, we must create a client secret that allows our application to connect to Entra:
![](/images/posts/2025/02/securing-vaadin-applications-with-microsoft-entra/image-4-1536x656-1-1024x437.png)

Make sure to copy the value of the client secret; we'll need that in the application configuration.
![](/images/posts/2025/02/securing-vaadin-applications-with-microsoft-entra/image-5-1024x76.png)

We also need to copy the ClientId and the TenantId. The ClientId (Application (client) ID) can be found on the App registration overview page (below), and the TenantId is located on the Entra overview page.
![](/images/posts/2025/02/securing-vaadin-applications-with-microsoft-entra/image-6-1024x380.png)

Step 2: Configure OAuth2 with Entra in our Application {#h2-4-step-2-configure-oauth2-with-entra-in-our-application}
--------------------------------------------------------------------------------------------------------------------

As we have a Vaadin application, we will use the [OAuth 2.0 authorization code grant flow](https://learn.microsoft.com/en-us/azure/active-directory/develop/v2-oauth2-auth-code-flow).

### Add Dependencies {#h3-5-add-dependencies}

First, add the Microsoft starter dependencies and the OAuth2 client starter. Don't be confused about the dependency's name. Entra is the new name for Azure Active Directory (Azure AD).

<pre class="EnlighterJSRAW" data-enlighter-language="xml" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;dependency&gt;
   &lt;groupId&gt;com.azure.spring&lt;/groupId&gt;
   &lt;artifactId&gt;spring-cloud-azure-starter-active-directory&lt;/artifactId&gt;
&lt;/dependency&gt;
&lt;dependency&gt;
   &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
   &lt;artifactId&gt;spring-boot-starter-oauth2-client&lt;/artifactId&gt;
&lt;/dependency&gt;</pre>

### Configure the Application {#h3-6-configure-the-application}

There are four properties to set. For simplicity, the snippet below shows them as Java properties. But you must be careful with secret values.  
**Please don't put them into application.properties or commit them to your Git repository because they are secret values you don't want to share with the public.**   

It's better to set the properties on the platform where your application is running, for example, as environment variables.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">spring.cloud.azure.active-directory.enabled=true
spring.cloud.azure.active-directory.profile.tenant-id=&lt;teanantId&gt;
spring.cloud.azure.active-directory.credential.client-id=&lt;clientId&gt;
spring.cloud.azure.active-directory.credential.client-secret=&lt;clientSecret&gt;</pre>

### Enable Entra Security {#h3-7-enable-entra-security}

To integrate Entra with Spring Security, we need to adjust the security configuration. We extend from VaadinWebSecurity because we have a Vaadin application.   

Add `AadWebApplicationHttpSecurityConfigurer.aadWebApplication()` to enable Entra security as the first line in the `configure method`.  

Also, ensure you don't set a LoginView because the login will happen with the Microsoft login.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.with(AadWebApplicationHttpSecurityConfigurer.aadWebApplication(), c -&gt; {
        });
        http.authorizeHttpRequests(authorize -&gt; authorize
            .requestMatchers(new AntPathRequestMatcher("/images/*.png"),
                             new AntPathRequestMatcher("/line-awesome/**/*.svg"), 
                             EndpointRequest.to(HealthEndpoint.class))
            .permitAll());
        super.configure(http);
    }
}</pre>

### Configure Role Prefix {#h3-8-configure-role-prefix}

The security configuration will prefix the roles with APPROLE_. To use the role name that we set in Microsoft Entra, we must configure the default prefix because ROLE_ is the prefix by default.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Configuration
public class RolePrefixConfiguration {
    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("APPROLE_");
    }
}</pre>

### Roles in Action {#h3-9-roles-in-action}

The setup is completed, and we can use role-based security in the Vaadin application.   

It's convenient to define the roles as constants, like in the example, in case the role name changes, so you only have to change it in one place.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@RolesAllowed({ Roles.USER, Roles.ADMIN })
@Route("event-registrations")
public class EventRegistrationView extends Div implements HasUrlParameter&lt;Long&gt;, HasDynamicTitle {</pre>

Step 3: Setup Karibu Testing {#h2-10-step-3-setup-karibu-testing}
-----------------------------------------------------------------

To use [Browserless Testing of Vaadin Applications with Karibu Testing](https://martinelli.ch/browserless-testing-of-vaadin-applications-with-karibu-testing/), we must fake the Entra setup's security context.

The most important part is the `createOAuth2AuthenticationToken` Method.   

An `OAuth2AuthenticationToken` is created and then set to the `SecurityContext` and the Karibu FakeRequest. The `OidcIdToken` is created with minimal attributes that our application uses.

It's also important to override the `getUserPrincipal` method because no login is happening. Using OAuth2 means that the application assumes that a JWT is part of the request instead.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@SpringBootTest
public abstract class KaribuTest {
    private static Routes routes;
    @Autowired
    protected ApplicationContext ctx;
    // Default user and role
    private String username = "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="4f25202721612b202a0f3b2a3c3b612c2022">[email&nbsp;protected]</a>";
    private String name = "John Doe";
    private String role = Roles.ADMIN;
    private OAuth2AuthenticationToken oAuth2AuthenticationToken;
    @BeforeAll
    public static void discoverRoutes() {
        Locale.setDefault(Locale.GERMAN);
        routes = new Routes().autoDiscoverViews("ch.martinelli.oss.registration.ui.views");
    }
    @BeforeEach
    public void setup() {
        MockVaadin.INSTANCE.setMockRequestFactory(session -&gt; new FakeRequest(session) {
            @Override
            public Principal getUserPrincipal() {
                createAuthentication();
                return SecurityContextHolder.getContext().getAuthentication();
            }
        });
        final Function0&lt;UI&gt; uiFactory = UI::new;
        MockVaadin.setup(uiFactory, new MockSpringServlet(routes, ctx, uiFactory));
    }
    @AfterEach
    public void tearDown() {
        logout();
        MockVaadin.tearDown();
    }
    protected void login(String username, String role) {
        this.username = username;
        this.role = role;
        oAuth2AuthenticationToken = null;
        createOAuth2AuthenticationToken();
    }
    private void createAuthentication() {
        createOAuth2AuthenticationToken();
        SecurityContextHolder.getContext().setAuthentication(oAuth2AuthenticationToken);
        FakeRequest request = (FakeRequest) VaadinServletRequest.getCurrent().getRequest();
        request.setUserPrincipalInt(oAuth2AuthenticationToken);
        request.setUserInRole((principal, roleName) -&gt; oAuth2AuthenticationToken.getPrincipal()
            .getAuthorities()
            .stream()
            .anyMatch(a -&gt; a.getAuthority().equals(roleName)));
    }
    private void createOAuth2AuthenticationToken() {
        if (oAuth2AuthenticationToken == null) {
            OidcIdToken oidcIdToken = new OidcIdToken("tokenValue", null, null,
                    Map.of("sub", "-", "preferred_username", username, "name", name));
            DefaultOidcUser defaultOidcUser = new DefaultOidcUser(List.of(new SimpleGrantedAuthority(role)),
                    oidcIdToken);
            oAuth2AuthenticationToken = new OAuth2AuthenticationToken(defaultOidcUser, defaultOidcUser.getAuthorities(),
                    "oidc");
        }
    }
    protected void logout() {
        try {
            SecurityContextHolder.getContext().setAuthentication(null);
            if (VaadinServletRequest.getCurrent() != null) {
                FakeRequest request = (FakeRequest) VaadinServletRequest.getCurrent().getRequest();
                request.setUserPrincipalInt(null);
                request.setUserInRole((principal, roleName) -&gt; false);
            }
        }
        catch (IllegalStateException e) {
            // Ignored
        }
    }
}</pre>

Summary {#h2-11-summary}
------------------------

Thanks to the spring-cloud-azure-starter-active-directory, the setup is straightforward. The Karibu Testing setup was more difficult, but thanks to Martin Mysny's help, I was able to make it work.

To learn more, check out the official documentation: [Spring Boot Starter for Microsoft Entra developer's guide](https://learn.microsoft.com/en-us/azure/developer/java/spring-framework/spring-boot-starter-for-entra-developer-guide?tabs=SpringCloudAzure5x).

*This blog post was first published on <https://martinelli.ch/securing-vaadin-applications-with-microsoft-entra/>*

<br />
