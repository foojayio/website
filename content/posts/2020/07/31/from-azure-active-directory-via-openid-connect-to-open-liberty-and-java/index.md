---
title: "Azure Active Directory, OpenID Connect, Open Liberty, and Java"
date: "2020-07-31T20:31:00+00:00"
lastmod: "2021-08-23T12:30:34+00:00"
description: "Let's look at an example of how to configure the Liberty social login feature as OpenID Connect client to secure Java apps with Azure Active Directory."
authors:
  - "m-reza-rahman"
image: "https://dzone.com/storage/temp/13800230-1596136325278.png"
categories:
  - "Azure"
tags:
related_posts:
  - "java-on-azure-tooling-update-july-2022"
frozen: false
---

Long gone are the days when you had to create your own user account management, authentication, and authorization for your web delivered software. Instead, contemporary applications leverage these functions (Identity and Access Management, IAM for short) from an external provider. As a full featured Java application runtime, Open Liberty has great options for externally provided IAM. Open Liberty supports IAM mainstays such as social media login, SAML and OpenID Connect. In Bruce Tiffany's blog post [Securing Open Liberty apps and micro-services with MicroProfile JWT and Social Media login](https://openliberty.io/blog/2019/08/29/securing-microservices-social-login-jwt.html), you have a solid example on how to use the Open Liberty social media login feature to authenticate users using their existing social media credentials. In this blog post, let's take a look at another example on how to configure the Liberty social login feature as an OpenID Connect client to secure Java applications with Azure Active Directory.

The code used in this blog post is hosted on this [GitHub repository](https://github.com/Azure-Samples/liberty-aad-oidc) as part of the official Azure samples - feel free to check it out and follow its user guide to run the Java EE demo application before or after reading this blog.

## **Set Up Azure Active Directory**

Azure Active Directory (Azure AD) implements OpenID Connect (OIDC), an authentication protocol built on OAuth 2.0, which lets you securely sign in a user from Azure AD to an application. Before going into the sample code, you must first set up an Azure AD tenant and create an application registration with a redirect URL and client secret. The tenant ID, application (client) ID and client secret are used by Open Liberty to negotiate with Azure AD to complete an [OAuth 2.0 authorization code flow](https://docs.microsoft.com/azure/active-directory/develop/v2-oauth2-auth-code-flow).

Learn how to set up Azure AD from these articles:

* [Create a new tenant](https://docs.microsoft.com/azure/active-directory/develop/quickstart-create-new-tenant)
* [Register an application](https://docs.microsoft.com/azure/active-directory/develop/quickstart-register-app)
* [Add a new client secret](https://docs.microsoft.com/azure/active-directory/develop/howto-create-service-principal-portal#create-a-new-application-secret)

## **Configure Social Login as OpenID Connect Client**

The following sample code shows how an application running on an Open Liberty server is configured with the `socialLogin-1.0` feature as an OpenID Connect client to authenticate a user from an OpenID Connect Provider, with Azure AD as the designated security provider.

The relevant server configuration in `server.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<server description="defaultServer">
  <!-- Enable features -->
  <featureManager>
    <feature>socialLogin-1.0</feature>
    <feature>transportSecurity-1.0</feature>
    <feature>appSecurity-3.0</feature>
  </featureManager>
  <!-- trust JDK’s default truststore -->
  <ssl id="defaultSSLConfig" trustDefaultCerts="true"/>
  <!-- add your tenant ID, client ID and secret from Azure AD -->
  <oidcLogin
    id="liberty-aad-oidc-javaeecafe" clientId="${client.id}"
    clientSecret="${client.secret}"
    discoveryEndpoint="https://login.microsoftonline.com/
            ${tenant.id}/v2.0/.well-known/openid-configuration"
    signatureAlgorithm="RS256"
    userNameAttribute="preferred_username"/>
  <!-- grant role "users" to all authenticated users -->
  <webApplication id="javaee-cafe"
    location="${server.config.dir}/apps/javaee-cafe.war">
    <application-bnd>
      <security-role name="users">
        <special-subject type="ALL_AUTHENTICATED_USERS"/>
      </security-role>
    </application-bnd>
  </webApplication>
  <!-- define http endpoints -->
  <httpEndpoint id="defaultHttpEndpoint" host="*"
    httpPort="9080" httpsPort="9443"/>
</server>
```

The `oidcLogin` element has a large number of configuration options. With Azure AD, most of them are not required as discovery endpoints are supported, allowing for most configuration to be automatically handled. Indeed Azure AD instances follow a known pattern for discovery endpoint URLs, allowing us to parameterize the URL using a tenant ID. In addition to that, a client ID and secret is needed. `RS256` must be used as the signature algorithm with Azure AD.

The `userNameAttribute` parameter is used to map a token value from Azure AD to a unique subject identity in Liberty. There are a number of Azure AD token values you can use that are [listed here](https://docs.microsoft.com/azure/active-directory/develop/access-tokens). Do be cautious as the required tokens that exist for v1.0 and v2.0 differ (with v2.0 not supporting some v1.0 tokens). Either `preferred_username` or `oid` can be safely used, although in most cases you will probably want to use the `preferred_username`.

Using Azure AD allows your application to use a certificate with a root CA signed by Microsoft's public certificate. This certificate is added to the default `cacerts` of the JVM. Trusting the JVM default `cacerts`ensures a successful SSL handshake between the OIDC Client and Azure AD (i.e. setting the `defaultSSLConfig` `trustDefaultCerts` value to `true`).

In our case, we assign all users authenticated via Azure AD the `users` role. More complex role mappings are possible with Liberty if desired.

## **Use OpenID Connect to Authenticate Users**

The sample application exposes a JSF client which defines a Java EE security constraint that only users with role `users` can access.

The relevant configuration in `web.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app>
    <security-role>
        <role-name>users</role-name>
    </security-role>
    <security-constraint>
        <web-resource-collection>
            <web-resource-name>javaee-cafe</web-resource-name>
            <url-pattern>/*</url-pattern>
        </web-resource-collection>
        <auth-constraint>
            <role-name>users</role-name>
        </auth-constraint>
    </security-constraint>
</web-app>
```

This is just standard Java EE security. The authentication and authorization workflow is shown in the following diagram.
![](https://dzone.com/storage/temp/13800230-1596136325278.png)

*Picture 1: OpenID Connect sign-in and token acquisition flow, from* [*Microsoft identity platform and OpenID Connect protocol*](https://docs.microsoft.com/azure/active-directory/develop/v2-protocols-oidc#protocol-diagram-access-token-acquisition)

When an unauthenticated user attempt's to access the JSF client, they are redirected to Microsoft to provide their Azure AD credentials. Upon success, the browser gets redirected back to the client with an authorization code. The client then contacts Microsoft again with the authorization code, client ID and secret to obtain an ID token and access token, and finally create an authenticated user on the client, which then gets access to the JSF client.

To get authenticated user information, use the `@Inject` annotation to obtain a reference to the `javax.security.enterprise.SecurityContext` and call its method `getCallerPrincipal()`:

```java
@Named
@SessionScoped
public class Cafe implements Serializable {
  @Inject
  private transient SecurityContext securityContext;
  public String getLoggedOnUser() {
    return securityContext.getCallerPrincipal().getName();
  }
}
```

## **Secure Internal REST Calls Using JWT RBAC**

The `Cafe` bean depends on `CafeResource`, a REST service built with JAX-RS, to create, read, update and delete coffees. The `CafeResource` implements RBAC (role based access control) using MicroProfile JWT to verify the groups claim of the token.

```java
@Path("coffees")
public class CafeResource {
    @Inject
    private CafeRepository cafeRepository;
    @Inject
    @ConfigProperty(name = "admin.group.id")
    private String ADMIN_GROUP_ID;
    @Inject
    private JsonWebToken jwtPrincipal;
    @DELETE
    @Path("{id}")
    public void deleteCoffee(@PathParam("id") Long coffeeId) {
        // Only users in the "admin group" are authorized to delete coffee
        if (!this.jwtPrincipal.getGroups().contains(ADMIN_GROUP_ID)) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }
        try {
            this.cafeRepository.removeCoffeeById(coffeeId);
        } catch (IllegalArgumentException ex) {
            logger.log(Level.SEVERE, "Error calling deleteCoffee() for coffeeId {0}: {1}.", new Object[] { coffeeId, ex });
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public List<Coffee> getAllCoffees() {
        return this.cafeRepository.getAllCoffees();
    }
    @POST
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Coffee createCoffee(Coffee coffee) {
        try {
            return this.cafeRepository.persistCoffee(coffee);
        } catch (PersistenceException e) {
            logger.log(Level.SEVERE, "Error creating coffee {0}: {1}.", new Object[] { coffee, e });
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
```

The `admin.group.id` is injected into the application using MicroProfile Config at the application startup using the `@ConfigProperty` annotation. MicroProfile JWT enables you to `@Inject` the JWT (Json Web Token). The `CafeResource` REST endpoint receives the JWT with the `preferred_username` and `groups` claims from the ID Token issued by Azure AD in the OpenID Connect authorization workflow. The ID Token can be retrieved using the `com.ibm.websphere.security.social.UserProfileManager` and `com.ibm.websphere.security.social.UserProfile` APIs.

Here is the relevant configuration snippet in `server.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<server description="defaultServer">
    <!-- Enable features -->
    <featureManager>
        <feature>jwt-1.0</feature>
        <feature>mpJwt-1.1</feature>
        <feature>mpConfig-1.3</feature>
    </featureManager>
    <!-- JWT consumer -->
    <mpJwt id="jwtUserConsumer"
        jwksUri="https://login.microsoftonline.com/${tenant.id}/discovery/v2.0/keys"
        issuer="https://login.microsoftonline.com/${tenant.id}/v2.0"
        audiences="${client.id}"
        userNameAttribute="preferred_username"
        authFilterRef="mpJwtAuthFilter"/>
    <!-- JWT auth filter -->
    <authFilter id="mpJwtAuthFilter">
        <requestUrl id="myRequestUrl" urlPattern="/rest" matchType="contains"/>
    </authFilter>
</server>
```

Note, the `groups` claim is not propagated by default and requires additional Azure AD configuration. To add a groups claim into the ID token, you will need to create a group with type as 'Security' and add one or more members to it in Azure AD. In the application registration created as part of Azure AD configuration, you will also need to: find 'Token configuration' \> select 'Add groups claim' \> select 'Security groups' as group types to include in ID token \> expand 'ID' and select 'Group ID' in 'Customize token properties by type' section. Learn more details from these articles:

* [Create a new group and add members](https://docs.microsoft.com/azure/active-directory/fundamentals/active-directory-groups-create-azure-portal)
* [Configuring groups optional claims](https://docs.microsoft.com/azure/active-directory/develop/active-directory-optional-claims#configuring-groups-optional-claims)

## **Summary**

In this blog entry, we demonstrated how to effectively secure an Open Liberty application using OpenID Connect and Azure Active Directory. This write-up and the underlying [official Azure sample](https://github.com/Azure-Samples/liberty-aad-oidc) should also easily work for WebSphere Liberty.

This effort is part of a broader partnership between Microsoft and IBM to provide better guidance and tools for Java EE, Jakarta EE (Java EE has been transferred to the Eclipse Foundation as Jakarta EE under vendor-neutral open source governance) and MicroProfile (MicroProfile is a set of open source specifications that build upon Java EE technologies and target the microservices domain) developers on Azure.

We would like to hear from you as to what kind of tools and guidance you need. If possible, please [fill out a five minute survey](https://aka.ms/migration-survey) on this topic and share your invaluable feedback - especially if you would like to work closely with us (completely for free) on a cloud migration case.
