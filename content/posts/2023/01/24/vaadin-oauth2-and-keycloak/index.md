---
title: "Vaadin, OAuth2, and Keycloak"
slug: "vaadin-oauth2-and-keycloak"
date: "2023-01-24T09:34:41+00:00"
lastmod: "2023-01-24T09:34:42+00:00"
description: "Security matters! Learn how to configure Vaadin and Spring Security to use OAuth2 with Keycloak on Foojay.io Today!"
authors:
  - "simon-martinelli"
image: "image-2-1.png"
categories:
  - "Security"
  - "Vaadin"
tags:
related_posts:
  - "a-faster-way-to-build-react-spring-boot-apps-using-hilla-1-3"
  - "blink-a-led-on-raspberry-pi-with-vaadin"
  - "build-web-apps-in-pure-java-with-vaadin-flow"
  - "crafting-your-own-railway-display-with-java"
enlighterjs: true
frozen: false
---

This article shows how to configure Vaadin and Spring Security to use OAuth2 with Keycloak.

Keycloak {#h2-0-keycloak}
-------------------------

First, we must start Keycloak and configure a realm. The easiest way is to start Keycloak with Docker.   
*Caution: This is just for development purposes. Don't use the setup in production.*

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">docker run -d -p 8180:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin \ 
       quay.io/keycloak/keycloak:20.0.1 start-dev</pre>

Now you can log in to the admin console: <http://localhost:8180/admin> (user: admin, password: admin)

Next we must create a realm and a user, add roles to the user, and finally, a client.

**Create a realm**

1. Click on the drop-down in the upper left corner and select "Create realm"
2. Enter the realm name "vaadin" and save

**Create realm roles**

1. Select "Realm roles" in the menu
2. Click "Create role"
3. Enter the role name "admin" and save
4. Repeat steps 1 to 3 with the role "user"

**Create users**

1. Select "Users" in the menu
2. Click on "Create new user"
3. Enter the user name "admin"
4. Click on "Create"
5. Select the tab "Credentials" and set a password
6. Disable "Temporary" to prevent having to update the password on the first login
7. Click "Save"
8. Select the tab "Role mapping"
9. Click on "Assign role"
10. Choose the previously created roles "admin" and "user"
11. Do the same for the user with username "user" but add only the role "user"

**Create a client**

1. Select "Client" in the menu
2. Click on "Create client"
3. Enter the client id "vaadin"
4. Click next and the save
5. In the "Access settings" set
   * "Valid redirect URIs" to http://localhost:8080/\*
   * "Web origins" to http://localhost:8080

**Important: Role Mapping**

Now comes a crucial step. We must disable the role mapping to the ID token. When I created the example, the roles were missing in the application, but I found the solution thanks to Thomas Vitale's answer on [StackOverflow](https://stackoverflow.com/questions/69331013/springboot-oauth2-with-keycloak-not-returning-mapped-roles-as-authorities).

1. Select "Client scopes"
2. Select the client scope "roles"
3. Click on the tab "Mappers"
4. Select "realm roles"
5. Disable "Add to ID token"

The configuration must look like this:
![](https://martinelli.ch/wp-content/uploads/2022/11/image-1024x723.png)

Vaadin Application with Security Configuration {#h2-1-vaadin-application-with-security-configuration}
-----------------------------------------------------------------------------------------------------

First, we need to extend VaadinWebSecurity to set up the Vaadin Spring security integration. There we override the configure method.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Override
protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests().requestMatchers(new AntPathRequestMatcher("/images/*.png")).permitAll();

    http.oauth2Login()
            .and()
            .logout()
            .addLogoutHandler(keycloakLogoutHandler)
            .logoutSuccessUrl("/");

    super.configure(http);
}</pre>

Starting from line 5, the generic OAuth2 login is configured, and a special LogoutHandler for Keycloak is configured. The LogoutHandler uses the Keycloak REST API to log out.

As we configured to map the roles to the userinfo we now need to map these roles to GrantedAuthority. The roles will be in the claim "realm_access".

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Bean
public GrantedAuthoritiesMapper userAuthoritiesMapperForKeycloak() {
    return authorities -&gt; {
        Set&lt;GrantedAuthority&gt; mappedAuthorities = new HashSet&lt;&gt;();
        var authority = authorities.iterator().next();

        if (authority instanceof OidcUserAuthority oidcUserAuthority) {
            var userInfo = oidcUserAuthority.getUserInfo();

            if (userInfo.hasClaim("realm_access")) {
                var realmAccess = userInfo.getClaimAsMap("realm_access");
                var roles = (Collection&lt;String&gt;) realmAccess.get("roles");
                mappedAuthorities.addAll(roles.stream()
                        .map(role -&gt; new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                        .toList());
            }
        }
        return mappedAuthorities;
    };
}</pre>

Finally, we must add the OAuth2 configuration in the application.properties file:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">spring.security.oauth2.client.registration.keycloak.client-id=vaadin
spring.security.oauth2.client.registration.keycloak.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.keycloak.scope=openid
spring.security.oauth2.client.provider.keycloak.issuer-uri=http://localhost:8180/realms/vaadin
spring.security.oauth2.client.provider.keycloak.user-name-attribute=preferred_username</pre>

Now you can start the application and open <http://localhost:8080>.
![](https://martinelli.ch/wp-content/uploads/2022/11/image-1.png)

As you can see in the menu on the left, there is only one entry, "Index".

Click on the "Sign in" button in the lower left corner. This will direct you to the Keycloak login screen.
![](https://martinelli.ch/wp-content/uploads/2022/11/image-2.png)

Sign in with admin/admin, and you'll be redirected to the app:
![](https://martinelli.ch/wp-content/uploads/2022/11/image-3.png)

Now you have access to all views.

Conclusion {#h2-2-conclusion}
-----------------------------

Setting up Vaadin, Spring Security, and Keycloak is straight forwarded. The only tricky part was the role mapping to get the realm roles as GranteAuthority.
