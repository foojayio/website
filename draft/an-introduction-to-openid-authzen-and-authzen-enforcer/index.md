---
title: "An Introduction to OpenID AuthZEN and authzen-enforcer, a Java SDK"
description: "An introduction to OpenID AuthZEN, the standard API between PEPs and PDPs, with Keycloak as the PDP and authzen-enforcer, a Java SDK."
authors: ["huseyin-akdogan"]
image: "authzen-cover.jpg"
categories:
  - "Java"
  - "Security"
  - "Spring"
related_posts:
  - "dpop-what-it-is-how-it-works-and-why-bearer-tokens-arent-enough"
  - "make-your-security-policy-auditable"
  - "vaadin-oauth2-and-keycloak"
  - "how-to-secure-a-spring-boot-application-with-openid-connect-and-pac4j"
---

Over the years, as the software industry became aware of the problems that come with authorization logic embedded in application code, moving that logic out of the application has gradually become the accepted solution. In this model, which the OASIS XACML standard described back in 2003, a **Policy Decision Point (PDP)** holds the policies and makes the decisions, and a **Policy Enforcement Point (PEP)** inside the application consults the PDP and enforces its decision.

This separation created a new need, namely a common way for the PEP to consult the PDP. Every PDP (_OPA, Cerbos, Topaz, Axiomatics, ..._) offers its own API and its own request structure. As a natural consequence, applications become dependent on one vendor's API, and replacing the PDP means rewriting every enforcement point.

The same fragmentation affects libraries. Every PDP needs its own SDK for every language and framework, so many pairs are never covered, and the PDP you choose may simply have no library for your stack. And API gateways, identity providers and SaaS products cannot offer a "bring your own PDP" option without building a separate integration for each vendor.

In this article, I introduce AuthZEN, a standard that addresses exactly these problems, and authzen-enforcer, the open source Java SDK I developed to put it into practice. After a look at the specification and at Keycloak as an AuthZEN PDP, I show how the SDK brings AuthZEN to Java and Spring Boot applications.

## What is AuthZEN?

AuthZEN is the OpenID Foundation's effort to standardize how PDPs and PEPs, in the words of the specification, _communicate authorization requests and decisions to each other without requiring knowledge of each other's inner workings_, and in doing so provides the missing common language between PEPs and PDPs. Its specification, the [**Authorization API 1.0**](https://openid.net/specs/authorization-api-1_0.html), was developed by the [AuthZEN working group](https://openid.net/wg/authzen/) and [approved as an OpenID Final Specification in January 2026](https://openid.net/authorization-api-1-0-final-specification-approved/). The standard deliberately says nothing about how policies are written or stored; it standardizes only the communication between the two parties, that is, what the PEP asks, what the PDP answers, and how the PEP discovers the PDP's endpoints. How the PEP authenticates is left to established mechanisms, with OAuth 2.0 recommended.

In short, just as OpenID Connect standardized authentication by giving applications a standard way to answer _who is this user?_, AuthZEN does the same for authorization, providing a standard way to answer _is this user allowed to do this?_

## A Look at the Specification

The Authorization API is a short and readable specification. Its core fits into a few ideas, and each of them answers one of the problems described above.

### Four entities and a decision

Every access question is built from the same four parts. The **subject** is who wants access, the **action** is what they want to do, the **resource** is what they want to do it to, and the optional **context** describes the circumstances, such as the time of the request, the client's IP address or whether the user signed in with multi-factor authentication. Subjects and resources are identified by a `type` and an `id`, an action by its `name`, and anything else travels in `properties`. The answer is a **decision**, `true` or `false`, optionally with a context of its own that can carry reasons or ask for a step-up authentication.

```json
{
  "subject":  { "type": "user", "id": "alice",
                "properties": { "department": "finance" } },
  "action":   { "name": "read" },
  "resource": { "type": "document", "id": "123" },
  "context":  { "time": "2026-11-12T16:30:00Z", "ip": "203.0.113.7" }
}
```

```json
{ "decision": true }
```

Neither side of this exchange reveals how the PDP works. The request says nothing about how the decision is reached, so whether the policies follow RBAC, ABAC, ReBAC or a mix of them, the application asks the same question in the same shape. The response, in turn, is deliberately minimal. A single boolean is all the PEP needs to enforce the decision, while all reasoning about policies stays inside the PDP. This is what removes the coupling to a single vendor, since replacing the PDP changes neither the question the application asks nor the way it enforces the answer.

Note that a deny is not an error. The PDP answers `200 OK` with `"decision": false`, while a `401` or `403` means that the PEP itself was not allowed to ask.

### Three ways to ask

The Authorization API offers three ways to ask the PDP for decisions. The first is the **Access Evaluation API** at `/access/v1/evaluation`, which returns a single answer to a single question, such as _can Alice read document 123?_

```http
POST /access/v1/evaluation

{
  "subject":  { "type": "user", "id": "alice" },
  "action":   { "name": "read" },
  "resource": { "type": "document", "id": "123" }
}
```

```json
{ "decision": true }
```

The second is the **Access Evaluations API** at `/access/v1/evaluations`, which returns many answers to many questions in one round trip. Top-level values act as defaults for every item, and each item overrides only what differs. A page that decides which buttons to show for a document needs exactly this.

```http
POST /access/v1/evaluations

{
  "subject":  { "type": "user", "id": "alice" },
  "resource": { "type": "document", "id": "123" },
  "evaluations": [
    { "action": { "name": "update" } },
    { "action": { "name": "delete" } }
  ]
}
```

```json
{
  "evaluations": [
    { "decision": true },
    { "decision": false }
  ]
}
```

By default every item is evaluated. With `options.evaluations_semantic` set to `deny_on_first_deny` or `permit_on_first_permit`, the batch stops at the first deny or the first permit, much like `&&` and `||`.

A bulk delete is a natural fit for `deny_on_first_deny`. Either Alice may delete every selected document, or the operation does not run at all, so there is no point in evaluating the rest once one is denied.

```http
POST /access/v1/evaluations

{
  "subject": { "type": "user", "id": "alice" },
  "action":  { "name": "delete" },
  "evaluations": [
    { "resource": { "type": "document", "id": "123" } },
    { "resource": { "type": "document", "id": "456" } },
    { "resource": { "type": "document", "id": "789" } }
  ],
  "options": { "evaluations_semantic": "deny_on_first_deny" }
}
```

```json
{
  "evaluations": [
    { "decision": true },
    { "decision": false, "context": { "reason": "deny_on_first_deny" } }
  ]
}
```

The PDP stopped at the second document, so the third has no decision. A PEP has to treat such a missing decision as a deny.

Runnable versions are in the examples of authzen-enforcer, the Java SDK introduced below, with both semantics in the [plain Java example](https://github.com/authzenenforcer/authzen-enforcer-examples/blob/2d24a87675370fb1e4260cc28c6930686764cb3c/plain-java/src/main/java/io/github/authzenenforcer/examples/plainjava/PlainJavaExample.java#L161-L189) and the bulk delete above in the [Spring Boot example](https://github.com/authzenenforcer/authzen-enforcer-examples/blob/2d24a87675370fb1e4260cc28c6930686764cb3c/spring-boot-mvc/src/main/java/io/github/authzenenforcer/examples/springbootmvc/BulkDeleteController.java#L78-L89).

The third is the **Search APIs**, three endpoints below `/access/v1/search/`, which turn the question around. Leave out the id of the entity you are looking for, and the PDP returns the ones it would permit. The resource search below answers _which documents can Alice read?_

```http
POST /access/v1/search/resource

{
  "subject":  { "type": "user", "id": "alice" },
  "action":   { "name": "read" },
  "resource": { "type": "document" }
}
```

```json
{
  "results": [
    { "type": "document", "id": "123" },
    { "type": "document", "id": "456" }
  ]
}
```

Subject search (_who can read document 123?_) and action search (_what can Alice do with document 123?_) work the same way, and large results come page by page. A search result is not a decision, so access is still enforced with an evaluation.

### Finding the PDP and trusting the PEP

A PEP does not need the URLs of these endpoints one by one; it can discover them. The specification recommends that a PDP publish a metadata document at `/.well-known/authzen-configuration` that lists the endpoints it offers, so knowing the PDP's base URL is enough for a PEP to find out where to send each request. An endpoint missing from that metadata tells the PEP that the PDP does not offer the API, and a PDP without metadata is reached at the default paths shown above. The specification also asks the PDP to authenticate the calling PEP, and the PDP trusts the PEP for the values it sends, the subject included. In practice, this means that the PEP talks to the PDP with its own identity, such as a service account, and not with the end user's token.

### Why the shape matters

Each of these choices maps back to the problems described at the beginning. The API itself is transport-agnostic, and other bindings such as gRPC may be defined as profiles, but every compliant PDP must implement the HTTPS binding with JSON. A shared vocabulary on top of a transport that every PDP speaks means that an application written against one PDP works against another, and on the application side, switching becomes a matter of configuration. A PEP library written once for a language or framework can work with any compliant PDP, so a new library is no longer needed for every vendor, even if details such as how the PEP authenticates still differ from one PDP to another. And an API gateway or identity provider can implement the PEP side once and let its customers bring their own PDP, as gateways such as Kong, Envoy and Tyk already did in the [AuthZEN interop events](https://github.com/openid/authzen/tree/main/interop/authzen-api-gateways).

## Who supports AuthZEN today

At the time of writing, close to twenty implementations, among them Axiomatics, Cerbos, Topaz, PlainID, Ping Identity and WSO2, have published their results from the [interoperability events](https://authzen-interop.net/) of the AuthZEN working group. These results were produced against pre-final drafts of the specification, and a conformance certification program is still in development.

This article focuses on **Keycloak**. It is the identity provider many Java teams already run, and since [version 26.7.0](https://www.keycloak.org/2026/05/authzen-as-experimental-feature) it acts as an AuthZEN PDP as an experimental feature (`--features=authzen`). It evaluates each request against the resources, scopes and policies defined under Authorization Services of the very client the PEP authenticates with, and the AuthZEN request maps onto them directly. The subject is a Keycloak user or client, the resource's type and id are the type and name of a Keycloak resource, and the action is a scope. A resource that is not registered in Keycloak is simply denied.

As of Keycloak 26.8.2, the latest release at the time of writing, it supports the Evaluation and Evaluations APIs, but not yet the Search APIs. It publishes metadata per realm, so discovery works with the realm URL as the PDP URL; the endpoints live below `/realms/{realm}/authzen`. The PEP authenticates with a token of a confidential client that has a service account and Authorization Services enabled.

### How closely Keycloak follows the specification

While building interop tests against Keycloak, I found a few places where it differs from the specification or loses a distinction the specification makes, and opened issues for them in the Keycloak project:

- **Unknown fields are rejected.** `action.properties` is optional in the specification, and receivers must ignore fields they do not know. Keycloak answers both with `400`, also for unknown keys in `options` ([keycloak/keycloak#54542](https://github.com/keycloak/keycloak/issues/54542)).
- **No challenge on `401`.** The specification says a `401` should carry a `WWW-Authenticate` header; Keycloak's AuthZEN endpoints send none ([#54516](https://github.com/keycloak/keycloak/issues/54516), a fix is proposed in pull request [#54517](https://github.com/keycloak/keycloak/pull/54517), awaiting review).
- **Resource properties and context share one namespace.** Both end up in the same attribute map, and a `context` entry silently overrides a resource property with the same name, so a policy cannot tell them apart ([#54848](https://github.com/keycloak/keycloak/issues/54848)).

None of these block real use, and the feature is explicitly experimental. They are typical of what interop testing against a real PDP brings to light.

## authzen-enforcer: a PEP for Java

Even with a standard API in place, every application still has to discover the PDP, authenticate to it, handle its failures and deny when no decision arrives. An SDK takes this ceremony away and does it the same way everywhere, which carries the specification's goal of standardization into the application itself.

My own experience taught me how much such an SDK matters. In a previous role, I designed and built a Java PEP SDK with Keycloak Authorization Services as the PDP, and as a contributor to Keycloak, I have been following its AuthZEN support closely. My first plan was to help with the AuthZEN extension the Quarkus team had planned, but their work was already under way and their review capacity was committed elsewhere. So instead of a Quarkus extension, I started a framework-independent Java SDK that any framework integration, Quarkus included, can build on.

This is how [authzen-enforcer](https://github.com/authzenenforcer/authzen-enforcer) came about. It is an open source (Apache 2.0) Java SDK that plays the PEP role. It is published on Maven Central, and its interop tests run against Keycloak on every build. **It is an independent project, not affiliated with the OpenID Foundation.**

Its design goals follow from the specification:

- **Zero runtime dependencies** in the core module. It runs on Java 17+ and uses only the JDK, including its own small JSON codec, so it never conflicts with your dependency tree.
- **Minimal configuration.** You give the PDP URL and a credential; endpoints come from discovery, with the metadata validated as the specification requires.
- **Fail-closed by default.** Unreachable PDP, timeout, `4xx`/`5xx`, invalid response: no decision means deny.
- **The PEP authenticates as itself.** The SDK never talks to an authorization server and never forwards the end user's token. You supply the PEP's credential through a `CredentialProvider`.

At the time of writing, the latest release is 0.2.0, available on [Maven Central](https://central.sonatype.com/namespace/io.github.authzenenforcer). The core module, `authzen-enforcer-core`, covers the whole Authorization API, that is, the Access Evaluation and Access Evaluations APIs, the Subject, Resource and Action Search APIs with pagination, and discovery. Next to it are a Spring Boot starter, modules that publish the client's metrics to Micrometer and its traces to OpenTelemetry, and a BOM that keeps the versions of all modules aligned. Planned next are an adapter for Spring Security's OAuth 2.0 client, so that Spring obtains and refreshes the PEP's token, support for WebFlux, auto-configuration for metrics and health, [DPoP-bound tokens](/today/dpop-what-it-is-how-it-works-and-why-bearer-tokens-arent-enough/) towards the PDP, and a Quarkus extension.

### Spring Boot support out of the box

The role of frameworks in enterprise application development is clear, and an SDK that aims to be useful to the community cannot leave them out. Considering how widely Spring is used in the Java community, Spring Boot was the natural first target. The Spring Boot starter, a preview for Spring MVC on Spring Boot 4, enforces annotated handlers before they run, and the PDP's base URL is the only setting it requires.

To include the starter in your project, add the following dependency.

```xml
<dependency>
    <groupId>io.github.authzenenforcer</groupId>
    <artifactId>authzen-enforcer-spring-boot-starter</artifactId>
    <version>0.2.0</version>
</dependency>
```

The minimum configuration is the PDP's base URL. With Keycloak as the PDP, this is the URL of the realm.

```yaml
authzen:
  pdp:
    url: https://keycloak.example.com/realms/acme
```

Keycloak also expects the PEP to authenticate, so the application defines a `CredentialProvider` bean that supplies the PEP's token.

### Auto-inference with `@CheckPermission`

For protecting endpoints, the starter offers a developer-friendly `@CheckPermission` annotation. Placed on a controller method, it asks the PDP before the method runs. It can also be placed on the controller class to cover all of its handler methods, in which case an annotation on a method takes precedence attribute by attribute. In either place, it does not have to spell out the AuthZEN request. Whatever the annotation leaves out, the starter infers from the request itself, and every inferred value can be overridden explicitly. The details are described in the [`@CheckPermission` documentation](https://github.com/authzenenforcer/authzen-enforcer/blob/main/docs/spring-boot.md#checkpermission).

The following controller methods show typical usage scenarios, each annotated with the request it produces.

```java
@CheckPermission                       // read on {type: "documents", id: <id>}
@GetMapping("/documents/{id}")
Document get(@PathVariable String id) { ... }

@CheckPermission                       // list on {type: "collection", id: "documents"}
@GetMapping("/documents")
List<Document> list() { ... }

@CheckPermission(action = "cancel", resourceType = "order", resourceId = "#id")
@PostMapping("/orders/{id}/cancel")
void cancel(@PathVariable String id) { ... }
```

Whatever the annotation does not say is inferred:

- the **subject** from the authenticated caller in Spring Security (the `sub` claim, with type `user`);
- the **action** from the HTTP method: `GET` is `read` on a single resource and `list` on a collection, `POST` is `create`, `PUT`/`PATCH` is `update`, `DELETE` is `delete`, and CORS preflight requests pass through without asking the PDP;
- the **resource** from the route pattern that Spring matched, never from the raw URI, so the way a client spells the path cannot change which resource the PDP is asked about.

The inferred type is the route segment as it is, `documents` in the examples above, so it must match the resource type in Keycloak, or be mapped with `authzen.enforcer.resource.type-names.documents: document`. Every inferred part can be overridden on its own, in the annotation or in configuration. Routes that cannot be inferred safely, such as `/{tenant}/documents`, are denied without asking the PDP and reported as warnings at startup, so you find them before your users do. A collection is never sent as a `"*"` wildcard, which a PDP could read as "every resource". Expressions in the annotation can read path variables, selected request fields and the caller's claims, but never credentials.

A deny becomes Spring Security's `AccessDeniedException`, an anonymous caller its authentication entry point, so existing error handling keeps working. The PDP's reason stays on the server.

### Operating it

A PEP that silently denies everything because its credential expired is a production incident waiting to happen. The SDK logs such failures as warnings with a rate limit and without leaking tokens or request values, and reports every call and enforcement to listeners. Optional modules publish them as **Micrometer** metrics and **OpenTelemetry** traces. A complete, runnable Spring Boot application with a Keycloak realm, along with a plain Java client, is available in the [examples repository](https://github.com/authzenenforcer/authzen-enforcer-examples).

## How to help

I have learned a great deal from the Java community, and I try to give back through articles, tutorials and talks at conferences, meetups and webinars. I started this project in the same spirit, driven by the ideas shared in this article, so any kind of contribution is warmly welcome, whether it is code, documentation, testing against another PDP or simply feedback.

If you run a PDP other than Keycloak, the most valuable contributions are interop tests against it and a short page about its behavior; both are open as issues, along with a few good first issues. Questions and ideas are welcome in [GitHub Discussions](https://github.com/authzenenforcer/authzen-enforcer/discussions).

## Conclusion

AuthZEN gives authorization what OpenID Connect gave authentication, a standard way for applications to ask their question. Its Authorization API, final since January 2026, reduces every access check to a subject, an action, a resource and a context, answers it with a simple decision, and offers single, batch and search requests along with discovery of the PDP's endpoints. Close to twenty implementations have already taken part in its interop events, and Keycloak, the identity provider many Java teams already run, acts as an AuthZEN PDP today, even if its support is still experimental. With the standard in place, what remained was putting it into practice inside applications, and authzen-enforcer is an effort to do that for Java, with a zero-dependency, fail-closed core and a Spring Boot starter built around `@CheckPermission`. That is where a standard pays off, letting you write the enforcement once and choose the PDP freely.

## References

- [AuthZEN Authorization API 1.0](https://openid.net/specs/authorization-api-1_0.html)
- [Authorization API 1.0 Final Specification approval](https://openid.net/authorization-api-1-0-final-specification-approved/)
- [AuthZEN working group](https://openid.net/wg/authzen/)
- [Interop results](https://authzen-interop.net/)
- [Keycloak AuthZEN support announcement](https://www.keycloak.org/2026/05/authzen-as-experimental-feature)
- [authzen-enforcer](https://github.com/authzenenforcer/authzen-enforcer)
- [Examples](https://github.com/authzenenforcer/authzen-enforcer-examples)
