---
title: "DPoP: What It Is, How It Works, and Why Bearer Tokens Aren't Enough"
slug: "dpop-what-it-is-how-it-works-and-why-bearer-tokens-arent-enough"
date: "2026-03-09T08:08:06+00:00"
lastmod: "2026-03-09T08:08:08+00:00"
description: "DPoP is one of the most exciting developments in the IAM (Identity and Access Management) space in recent years. Yet many backend developers either have - by Hüseyin Akdoğan"
authors:
  - "huseyin-akdogan"
image: "/images/posts/2026/03/dpop-what-it-is-how-it-works-and-why-bearer-tokens-arent-enough/Screenshot-2026-03-09-at-09.33.06-scaled.png"
categories:
  - "Security"
tags:
related_posts:
  - "quarkus-unpacked-insights-from-the-foojay-podcast"
  - "optimizing-java-for-the-cloud-native-era-with-quarkus"
  - "quarkus-a-runtime-and-framework-for-cloud-native-java"
  - "ensuring-safe-and-reliable-ai-interactions-with-llm-guardrails"
enlighterjs: true
frozen: false
---

DPoP is one of the most exciting developments in the IAM (Identity and Access Management) space in recent years. Yet many backend developers either have not heard of it or are unsure what it actually changes. In this article, I will break down what DPoP is, what problem it solves, and walk through a working implementation with Keycloak and Quarkus.

What is DPoP? {#h2-0-what-is-dpop}
----------------------------------

DPoP (Demonstration of Proof-of-Possession) is an OAuth 2.0 security mechanism defined in [RFC 9449](https://datatracker.ietf.org/doc/html/rfc9449). Its core purpose is simple: cryptographically bind an access token to the client that requested it. This way, even if a token is intercepted, it cannot be used by another client.

In the traditional Bearer token model, anyone who possesses the token is considered authorized. DPoP changes this model; to use a token, the client must also prove possession of the corresponding private key.

The Problem: Bearer Tokens and the "Finders Keepers" Risk {#h2-1-the-problem-bearer-tokens-and-the-finders-keepers-risk}
------------------------------------------------------------------------------------------------------------------------

Bearer tokens are tokens carried in the HTTP Authorization header and accepted by the server without any additional verification of the presenter. [RFC 6750](https://datatracker.ietf.org/doc/html/rfc6750) explicitly states that possession of the token is the sole authorization criterion. This means any party that obtains the token can act as if it were the legitimate client.

This is not a theoretical risk. Real-world breaches have shown, time and again, that stolen Bearer tokens translate directly into unauthorized access:

* **Codecov Supply Chain Attack (2021):** Attackers who infiltrated Codecov's CI/CD process harvested tokens stored in customers' environment variables. These tokens potentially granted access to private repositories of hundreds of organizations, including HashiCorp, which confirmed it was affected.
* **GitHub OAuth Token Leak (2022):** OAuth tokens belonging to Heroku and Travis CI were stolen, allowing attackers to list private repositories and access repository metadata across dozens of GitHub organizations, including npm.
* **Microsoft SAS Token Incident (2023):** Microsoft's AI research team accidentally shared an overly permissive SAS token in a GitHub repository. This token made it possible to access 38 TB of internal data.

The common thread across these incidents is that a token was obtained and seamlessly used in a different context by a different actor. What makes this possible is the Bearer token model's core assumption: **whoever presents the token = the authorized actor.** The model checks who holds the token, not who the token belongs to.

How Does DPoP Work? {#h2-2-how-does-dpop-work}
----------------------------------------------

DPoP requires the client to send a **DPoP Proof** JWT with every request. This proof is signed with the client's private key and contains the following claims:

1. **htm and htu (HTTP method and URL):** Restricts the proof to a specific endpoint, preventing a proof generated for one resource from being used against another.
2. **jti (JWT ID):** Each proof carries a unique ID. The server records used jti values and rejects any proof that attempts to reuse one.
3. **iat (Issued At):** Indicates when the proof was generated, allowing the server to enforce a validity window and reject stale proofs.
4. **ath (Access Token Hash):** Specifies which access token the proof is associated with.

The flow works as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">1. Client generates an asymmetric key pair.
2. During the token request, the client sends a DPoP proof JWT whose header contains the public key (JWK).
3. The authorization server issues a DPoP-bound access token containing the JWK thumbprint (cnf.jkt).
4. When calling a protected resource, the client sends:
   - Authorization: DPoP &lt;access_token&gt;
   - DPoP: &lt;signed proof JWT&gt;
5. The resource server:
   - Verifies the proof signature
   - Checks that the proof's public key matches the token's cnf.jkt
   - Validates htm, htu, iat, jti
   - Verifies the ath claim binding the proof to the access token</pre>

With this model, stealing the token alone is not enough. The attacker cannot generate valid proofs without the private key, limiting any potential misuse to an already captured, unused proof within its narrow validity window. Compare this to the Bearer model, where a stolen token grants unrestricted access until it expires. DPoP does not eliminate token theft, but it makes stolen tokens fundamentally harder to exploit.

Configuring DPoP in Keycloak {#h2-3-configuring-dpop-in-keycloak}
-----------------------------------------------------------------

For this article, I use Keycloak (v26.5.5) as the identity provider. It is open-source, widely adopted, and provides built-in DPoP support with a straightforward configuration.

DPoP was introduced as a preview feature in Keycloak 23.0.0 and became [officially supported in version 26.4](https://www.keycloak.org/2025/10/dpop-support-26-4), working out of the box without any additional client configuration. If a client sends a DPoP proof during the token request, Keycloak validates it and includes the key thumbprint in the issued token. No extra setup is needed for this default behavior.

However, if you want to **enforce** DPoP for a specific client, meaning Bearer tokens will no longer be accepted for that client's resources, follow these steps:

**Step 1:** In the Keycloak Admin Console, navigate to the relevant realm and select the client from the **Clients** menu.

**Step 2:** In the **Settings** tab, locate the **Capability config** section.

**Step 3:** Enable the **Require DPoP bound tokens** switch.

<img fetchpriority="high" decoding="async" class="alignnone size-medium wp-image-122936" src="/images/posts/2026/03/dpop-what-it-is-how-it-works-and-why-bearer-tokens-arent-enough/image1-700x370.png" alt="" width="700" height="370">

<br />

With this option enabled, the client must include a DPoP proof with every token request. Requests without valid proof will be rejected, and Bearer tokens will not be accepted to access this client's resources.

DPoP in Action with Quarkus {#h2-4-dpop-in-action-with-quarkus}
---------------------------------------------------------------

To see DPoP in practice, I built a Quarkus application with protected REST endpoints and tested them using a [k6](https://k6.io) script. The full source code is available on [GitHub](https://github.com/hakdogan/quarkus-dpop-example).

### Project Setup {#h3-5-project-setup}

The application uses Quarkus 3.32.2 with the following key extension: **OpenId Connect**. Quarkus provides extensions for OpenID Connect and OAuth 2.0 access token management, focusing on acquiring, refreshing, and propagating tokens.

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;dependency&gt;
    &lt;groupId&gt;io.quarkus&lt;/groupId&gt;
    &lt;artifactId&gt;quarkus-oidc&lt;/artifactId&gt;
&lt;/dependency&gt;</pre>

The `quarkus.oidc.auth-server-url` property specifies the base URL of the OpenID Connect (OIDC) server, which points to the Keycloak instance in this case:

<pre class="EnlighterJSRAW" data-enlighter-language="ini">quarkus.http.port=8180
quarkus.oidc.auth-server-url=http://localhost:8080/realms/master
quarkus.oidc.client-id=dpop-demo
quarkus.oidc.token.authorization-scheme=dpop</pre>

The key line here is `quarkus.oidc.token.authorization-scheme=dpop`. This property tells Quarkus OIDC extension to expect the `Authorization: DPoP ` scheme and to perform the full DPoP proof verification process as defined by [RFC 9449](https://datatracker.ietf.org/doc/html/rfc9449). This includes validating the proof's signature, `htm`, `htu`, `ath`, and the `cnf` thumbprint binding between the token and the proof's public key.

### Protected Endpoints {#h3-6-protected-endpoints}

The application exposes three endpoints under the `/api` path, all requiring authentication. Each endpoint returns the caller's name and the token type (Bearer or DPoP) by checking the presence of the `cnf` claim in the JWT:

<pre class="EnlighterJSRAW" data-enlighter-language="java">@Path("/api")
@Authenticated
public class ProtectedResource {

    private final JsonWebToken jwt;

    public ProtectedResource(JsonWebToken jwt) {
        this.jwt = jwt;
    }

    @GET
    @Path("/user-info")
    @Produces(MediaType.TEXT_PLAIN)
    public String getUserInfo() {
        return buildResponse();
    }

    @POST
    @Path("/user-info")
    @Produces(MediaType.TEXT_PLAIN)
    public String postUserInfo() {
        return buildResponse();
    }

    @POST
    @Path("/list-users")
    @Produces(MediaType.TEXT_PLAIN)
    public String listUsers() {
        return buildResponse();
    }

    private String buildResponse() {
        return "Hello, %s! Token type: %s".formatted(
                jwt.getName(),
                jwt.containsClaim("cnf") ? "DPoP" : "Bearer"
        );
    }
}</pre>

Having both GET and POST on `/user-info` plus a separate `/list-users` endpoint is intentional. These allow us to demonstrate how DPoP proof claims (`htm` and `htu`) restrict token usage to a specific HTTP method and URL.

### Replay Protection with a jti Filter {#h3-7-replay-protection-with-a-jti-filter}

As mentioned above, Quarkus OIDC extension handles the core DPoP verification. However, `jti` replay protection is not part of that process, since tracking used values requires server-side state, which falls outside the scope of a stateless token validation layer.

I added a minimal `@ServerRequestFilter` that records each proof's `jti` and rejects any reuse:

<pre class="EnlighterJSRAW" data-enlighter-language="java">@Singleton
public class DpopJtiFilter {

    private final Set&lt;String&gt; usedJtis = ConcurrentHashMap.newKeySet();

    @ServerRequestFilter
    public Optional&lt;Response&gt; checkJti(ContainerRequestContext ctx) {
        String dpopHeader = ctx.getHeaderString("DPoP");
        if (dpopHeader == null || dpopHeader.isBlank()) {
            return Optional.empty();
        }

        String[] parts = dpopHeader.split("\\.");
        if (parts.length != 3) {
            return Optional.empty();
        }

        try {
            String payloadJson = new String(
                    Base64.getUrlDecoder().decode(parts[1]));
            String jti = extractJti(payloadJson);
            if (jti != null &amp;&amp; !usedJtis.add(jti)) {
                return Optional.of(Response.status(Response.Status.UNAUTHORIZED)
                        .type(MediaType.TEXT_PLAIN)
                        .entity("DPoP proof replay detected: jti '%s' has already been used"
                                .formatted(jti))
                        .build());
            }
        } catch (Exception e) {
            // Let Quarkus OIDC handle malformed proofs
        }

        return Optional.empty();
    }

    // ...
}</pre>

In this example, I use an in-memory `ConcurrentHashMap` to keep the demo simple. In a production environment, you would use a distributed store such as Redis or Infinispan to track used `jti` values across multiple application instances and to apply TTL-based eviction aligned with the proof's validity window.

It is worth noting that Keycloak already performs jti replay protection at the **authorization server** level. Internally, its [DPoPReplayCheck](https://github.com/keycloak/keycloak/blob/main/services/src/main/java/org/keycloak/services/util/DPoPUtil.java#L378) uses the `SingleUseObjectProvider`, which is backed by Infinispan's replicated cache. When a DPoP proof arrives at the token endpoint, Keycloak hashes the `jti` combined with the request URI using SHA-1 and stores it with a TTL derived from the proof's `iat` claim. If the same proof is submitted again, the `putIfAbsent` call fails and the request is rejected.

However, this protection only covers requests made to Keycloak itself. Once a DPoP-bound token is issued, the **resource server** is responsible for its own jti tracking. A stolen proof could be replayed against the Quarkus application, and Keycloak would have no visibility into that. This is why I added the jti filter at the resource server level, creating a two-layer defense: **Keycloak guards the token endpoint, and the filter guards the application endpoints.**

### Testing with k6 {#h3-8-testing-with-k6}

The repository includes a k6 test script (`k6/dpop-test.js`) that exercises the full DPoP flow. Run it with:

<pre class="EnlighterJSRAW" data-enlighter-language="bash">k6 run k6/dpop-test.js</pre>

The script performs seven HTTP calls in sequence. The first request obtains a DPoP-bound token from Keycloak, the next three are happy-path requests (one per endpoint), and the final three test failure scenarios. Let's take a closer look at what happens behind the scenes at both the Keycloak and Quarkus layers:

#### 1. Token Request (Keycloak)

Before any resource access, the script requests a DPoP-bound access token:

1. The script generates an **EC key pair** (P-256) using the [WebCrypto API](https://developer.mozilla.org/en-US/docs/Web/API/Web_Crypto_API).
2. It creates a DPoP proof JWT targeting Keycloak's token endpoint (`htm: POST`, `htu: .../protocol/openid-connect/token`), signed with the private key. The public key is embedded in the proof's `jwk` header.
3. It sends a `POST` to the token endpoint with the `DPoP` header and user credentials (`grant_type=password`).
4. **Keycloak** validates the DPoP proof (signature, structure, claims), then issues an access token containing a `cnf` (confirmation) claim with the SHA-256 thumbprint of the client's public key. This binds the token to that specific key pair. Notice the `typ: DPoP` and the `cnf.jkt` field in the issued token:

<pre class="EnlighterJSRAW" data-enlighter-language="json">{
  "typ": "DPoP",
  "azp": "dpop-demo",
  "sub": "830783f9-ab1b-4c41-9c23-fa6a335de1bc",
  "cnf": {
    "jkt": "8iU6dz7Uclsxek7kgyreJc8sc2LjZIbFqtUUFpWKZIc"
  },
  "scope": "email profile",
  "preferred_username": "hakdogan"
}</pre>

#### 2. GET /user-info (Happy Path)

1. The script creates a **fresh DPoP proof** for `GET /api/user-info` with a new `jti`, current `iat`, and an `ath` computed from the access token's SHA-256 hash. The proof payload looks like this:

<pre class="EnlighterJSRAW" data-enlighter-language="json">{
  "jti": "6f0bf628-309d-489b-9243-38ed169e1d8c",
  "htm": "GET",
  "htu": "http://localhost:8180/api/user-info",
  "iat": 1772897361,
  "ath": "3yFPVhSab16gaSgMAFtZCgm7GXpBMx5t3ZYCeuWqT0w"
}</pre>

2. It sends `GET /api/user-info` with `Authorization: DPoP ` and `DPoP: `.
3. **Quarkus jti filter** checks the proof's `jti` against the used-jti store. This is a new `jti`, so the request passes through.
4. **Quarkus OIDC** **extension** validates the DPoP proof as required by [RFC 9449 (Section 7.1)](https://datatracker.ietf.org/doc/html/rfc9449#section-7.1), which assigns this responsibility to the resource server. It verifies the proof's signature, confirms `htm` matches `GET`, `htu` matches the request URL, `ath` matches the token hash, and the `cnf` thumbprint in the token matches the proof's public key. All checks pass.
5. The endpoint reads the `cnf` claim from the token, identifies it as a DPoP token, and responds:

<pre class="EnlighterJSRAW" data-enlighter-language="raw">HTTP 200: Hello, hakdogan! Token type: DPoP</pre>

The script repeats this same flow for `POST /user-info` and `POST /list-users`, each with a fresh proof matching the target method and URL. Both return `200` with the same response.

#### 3. GET /user-info (Replay Attack)

1. The script sends the **exact same proof** that was used in the happy path request.
2. **Quarkus jti filter** checks the `jti` and finds it already in the used-jti store. The request is rejected before reaching OIDC validation:

<pre class="EnlighterJSRAW" data-enlighter-language="raw">HTTP 401: DPoP proof replay detected: jti '...' has already been used

</pre>

> **Note:** The error message above includes the `jti` value for demonstration purposes, making it easy to observe what the filter caught. In a production environment, avoid exposing internal claim values in error responses. A generic `401 Unauthorized` with no body, or a minimal message like `"invalid DPoP proof"`, is sufficient and prevents information leakage.

#### 4. POST /user-info (Method Mismatch - htm)

1. The script creates a new proof with `htm: GET` targeting `/api/user-info`, but sends it as a `POST` request.
2. **Quarkus jti filter** passes the request (new `jti`).
3. **Quarkus OIDC** **extension** compares the proof's `htm` (`GET`) with the actual request method (`POST`). They do not match. The request is rejected:

<pre class="EnlighterJSRAW" data-enlighter-language="raw">HTTP 401</pre>

#### 5. POST /list-users (URL Mismatch - htu)

1. The script creates a new proof targeting `POST /api/user-info`.
2. It sends the request to `POST /api/list-users` instead.
3. **Quarkus jti filter** passes the request (new `jti`).
4. **Quarkus OIDC** **extension** compares the proof's `htu` with the actual request URL. They do not match. The request is rejected:

<pre class="EnlighterJSRAW" data-enlighter-language="raw">HTTP 401</pre>

```
All seven checks pass:
```

<pre class="EnlighterJSRAW" data-enlighter-language="raw">✓ Token request succeeds
✓ GET /user-info returns 200
✓ POST /user-info returns 200
✓ POST /list-users returns 200
✓ Replay attack returns 401
✓ htm mismatch returns 401
✓ htu mismatch returns 401</pre>

In contrast, if the same requests were sent as plain Bearer tokens without DPoP proofs, all of them would succeed with `200`. The replay, method mismatch, and URL mismatch scenarios would go undetected because there is no proof to validate. **This is exactly the gap that DPoP closes.**

Conclusion {#h2-9-conclusion}
-----------------------------

Bearer tokens follow a simple rule: whoever holds the token is authorized. DPoP changes this by binding each token to a cryptographic key pair and requiring a fresh, signed proof on every request. A stolen token alone is no longer sufficient.

The IAM ecosystem is moving in this direction. Identity providers like Keycloak and frameworks like Quarkus already offer built-in DPoP support, making adoption straightforward. Bearer tokens are not going away, but for access to sensitive resources, adopting DPoP is becoming less of a choice and more of a necessity.
