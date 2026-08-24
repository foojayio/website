---
title: "Push V3: One Message From Your Server to Every Surface"
date: "2026-08-07T23:14:10+00:00"
description: "Codename One Push V3 adds typed messages, managed credentials, segmentation, analytics, and Surface updates. Existing push apps should test the new cloud e"
canonical: "https://www.codenameone.com/blog/push-v3-new-cloud/"
authors:
  - "shai-almog"
image: "push-v3-new-cloud.jpg"
categories:
  - "Java"
related_posts:
  - "the-third-generation-gui-builder-one-workspace-for-every-form"
  - "the-codename-one-javascript-port-is-now-free-and-open-source"
  - "own-your-pixels-native-fidelity-on-your-schedule"
  - "how-we-beat-hotspot-performance-by-cheating-but-not-like-that"
frozen: false
---

![Push V3 connects one typed message to phones, widgets, and live surfaces](https://www.codenameone.com/blog/push-v3-new-cloud.jpg)

Push notifications should be application infrastructure, not a pile of expiring certificates and provider-specific JSON.
| **What is Codename One?** Codename One is an open-source framework for building native iOS, Android, desktop, and web apps from a single Java or Kotlin codebase. Learn more at [codenameone.com](https://www.codenameone.com/).

This week we merged [Push V3](https://github.com/codenameone/CodenameOne/pull/5440) into the Codename One core and completed its new cloud implementation. It gives an application a typed message model, managed provider credentials, subscriptions, server-side segments, campaigns, analytics, and a direct path into [Surfaces](https://www.codenameone.com/blog/widgets-live-activities-dynamic-island/).

There is also one thing every existing push developer should do now:
> Change the push service URL from `https://push.codenameone.com` to `https://cloud.codenameone.com` and send a real notification through your existing code.

Next week we plan to bring down the old push service and direct `push.codenameone.com` traffic to the new implementation. The compatibility endpoint accepts the existing request format, so existing code should keep working. It is still a completely new server, and "should" is not a test result. Please test before the cutover while both routes are easy to compare.

## TL;DR

Push is the lead story today. Over the next four posts, I will unpack the other changes in this release:

* [Our own Maven repository](#phase-one-of-our-maven-repository-move): We are moving Codename One releases to `repo.codenameone.com`. New projects get the setting automatically next week. Existing projects will need a short POM block before new versions stop appearing on Maven Central.
* [On-device AI and MCP](#on-device-ai-and-mcp-on-every-port): The core now has portable OCR, vision, language, and LiteRT APIs. The MCP server can inspect and operate a real application on mobile ports through loopback, with a release-build guard because another local process can also reach that socket.
* [Health data](#health-data-without-fake-certainty): A new API covers HealthKit, Health Connect, workouts, nutrition, eight Bluetooth health sensor profiles, and a deterministic simulator. The design preserves denied reads, missing values, source overlap, and compliance boundaries instead of flattening them into convenient answers.
* [Road-following map routes](#a-polyline-is-not-a-route): `Routing.showRoute(...)` can now turn two coordinates into road geometry, distance, duration, legs, and steps. OSRM provides the default test path, while `RouteService` keeps production provider choice in application code.

## Test the new push server now

If your server currently sends through the classic endpoint, keep the request exactly as it is and change only the host:

```diff
-https://push.codenameone.com/push/push
+https://cloud.codenameone.com/push/push
```

Send to test devices on every platform your application supports. Exercise a visible notification, a data payload, a cold start, and any badge, sound, category, image, or deep-link behavior you use. Compare the result with the old host and [open an issue](https://github.com/codenameone/CodenameOne/issues) if the two disagree.

The new server contains a classic compatibility layer. Existing applications do not need to adopt the Java V3 client or the new REST API before the hostname switch. That separation matters: validating the new transport is a small operational change, while adopting the V3 model is an application change you can schedule.

The queue records a provider response for each target. "Accepted" means APNs, FCM, or another provider accepted the request. It does not prove that the operating system displayed the notification, that the user saw it, or that the application opened. The console keeps those states separate because a comforting number with the wrong definition is worse than no number.

## V3 makes the message a real type

The classic API encoded behavior into numeric push types and positional strings. It worked, but it made provider differences and new destinations increasingly hard to express.

V3 uses an immutable schema:

```java
PushMessage message = PushMessage.builder()
        .title("Boarding changed")
        .body("Flight CN1 42 now leaves from gate C7")
        .deepLink("myapp://trip/CN142")
        .data("tripId", "CN142")
        .ttlSeconds(900)
        .build();
```

The same envelope can carry visible content, application data, an image, a deep link, collapse and lifetime rules, provider-specific options, and a `surface` command. Incoming messages are parsed before reaching application code, exposed through immutable maps, and rejected when their schema is unsupported.

V3 also replaces the old `PushCallback` contract. Your main application class no longer implements the push interface. You create a `PushClient` and give it a `PushListener`:

|             Previous API             |                     V3 API                      |
|--------------------------------------|-------------------------------------------------|
| `PushCallback.push(String value)`    | `PushListener.onMessage(PushMessage message)`   |
| `registeredForPush(String deviceId)` | `onRegistration(PushSubscription subscription)` |
| `pushRegistrationError(...)`         | `onError(PushError error)`                      |

This is more than a method rename. The listener receives a parsed `PushMessage`, registration returns a subscription object, and errors carry a code plus retry information.

```java
private PushClient push;

public void init(Object context) {
    push = PushClient.builder("APP_KEY_FROM_CONSOLE")
            .listener(new PushListener() {
                public void onMessage(PushMessage message) {
                    Log.p("Push: " + message.getTitle());
                }

                public void onRegistration(PushSubscription subscription) {
                    Log.p("Registered " + subscription.getTransportId());
                }

                public void onError(PushError error) {
                    Log.p(error.getCode() + ": " + error.getMessage());
                }
            })
            .build();
}

public void start() {
    push.register();
}
```

Create one client in `init()`, retain it, and call `register()` from `start()`. Registration is idempotent. Do not unregister from `stop()`, because that removes the subscription rather than pausing it.

Applications that run their own push infrastructure are not trapped behind the managed service. `PushTransport` is a public seam for custom registration and delivery, while `PushRegistrationSink` lets an application mirror registration changes to its own backend.

## One push can update a notification or a Surface

A lock-screen notification is only one destination. Widgets, Live Activities, the Dynamic Island, watch complications, and other [Surfaces](https://www.codenameone.com/blog/widgets-live-activities-dynamic-island/) also need fresh state.

V3 reserves a typed `surface` object in the same envelope. Native bootstrap code can route a Surface command before the main application UI is running.

![Diagram](https://mermaid.ink/img/Zmxvd2NoYXJ0IFRCCiAgICBBWyJQdXNoIFYzIGVudmVsb3BlIl0gLS0-IEJ7IlBheWxvYWQga2luZCJ9CiAgICBCIC0tPiBDWyJWaXNpYmxlIG5vdGlmaWNhdGlvbiJdCiAgICBCIC0tPiBEWyJBcHBsaWNhdGlvbiBkYXRhIl0KICAgIEIgLS0-IEVbIlN1cmZhY2UgY29tbWFuZCJdCiAgICBFIC0tPiBGWyJXaWRnZXQgdGltZWxpbmUiXQogICAgRSAtLT4gR1siTGl2ZSBBY3Rpdml0eSJdCiAgICBFIC0tPiBIWyJEeW5hbWljIElzbGFuZCBvciBjb21wbGljYXRpb24iXQogICAgQyAtLT4gSVsiUHVzaExpc3RlbmVyIG9uIHRoZSBDb2RlbmFtZSBPbmUgRURUIl0KICAgIEQgLS0-IEk=?type=png&bgColor=ffffff)

Consider a delivery application that is not running while the customer waits for a courier. A server push can update its Live Activity and Dynamic Island to say "Driver is 2 minutes away" without launching the main Codename One UI. On a platform without that Surface, the same campaign can deliver a normal notification instead.

The message view still shows whether APNs or another provider accepted each update. That is useful when the Surface changes while no `PushListener` is running inside the application.

## The certificate stops being your server's problem

The old arrangement often made an application team generate a push certificate, place it on its own server, watch its expiry date, and repeat the process. That is fragile infrastructure disguised as setup.

The new console stores provider credentials for each application and environment. APNs can use a `.p8` signing key, which does not have the annual expiry cycle of the old certificate workflow. The push service signs provider requests and isolates credentials from campaign users.

![Push application settings and provider credentials](https://www.codenameone.com/blog/push-v3-new-cloud/push-v3-console-settings.png)

Credentials are encrypted at rest and treated as write-only secrets in the console. Reading application settings does not return the secret value. This removes certificate hosting from your application server, but it does not remove normal secret hygiene: use a narrowly scoped provider key, rotate it when a team member or system boundary changes, and separate production from development.

## Segmentation without handing identity to a device

The console separates applications, environments, subscriptions, audiences, messages, campaigns, and analytics.

![Push applications, environments, and operational state in the console](https://www.codenameone.com/blog/push-v3-new-cloud/push-v3-console-overview.png)

A device can register its provider token through the public client endpoint. It cannot declare an external user identity or attach arbitrary tags to itself. Those operations require the authenticated server API. Otherwise a modified client could simply label itself `premium`, `administrator`, or `patient-high-risk` and enter a segment it did not belong in.

![A saved push audience built from server-assigned subscription data](https://www.codenameone.com/blog/push-v3-new-cloud/push-v3-console-audience.png)

Saved segments are evaluated on the server against application-scoped subscription data. A segment might select a locale, application version, platform, or a tag assigned by your backend. The audience is resolved when the message is sent, so a corrected tag does not require rebuilding a static mailing list.

This is segmentation for application behavior, not an advertising profile. Codename One does not sell the subscription data or combine it across customers. The service still has to retain what delivery requires: provider tokens, installation and optional external identifiers, server-assigned tags, message payloads, target status, and provider responses.

Never place a password, access token, medical result, or other secret in a notification payload. Providers and operating systems participate in delivery, lock screens can expose visible text, and notification data may outlive the screen where you intended to show it.

## Monitoring that answers operational questions

The new message view exposes queued, accepted, failed, and dead targets, including provider error information.

![Per-message push state and provider outcomes](https://www.codenameone.com/blog/push-v3-new-cloud/push-v3-console-messages.png)

This makes several operational checks possible:

* Is the queue moving?
* Did one provider fail while the others accepted the message?
* Are stale device tokens being removed?
* Did a rate limit delay a large audience?
* Which environment and campaign produced this message?

Analytics are retained for 30 days. They are operational delivery analytics, not proof of attention. Application opens or business outcomes still belong in consent-aware product analytics under your control.

## What each plan includes

Push sending and managed provider credentials are available on every subscription level, including Free. The plans differ in monthly volume, rate limits, and persistent campaign tooling:

|    Plan    | Monthly deliveries per seat | Requests per minute | Recipients per minute | Persistent audiences and campaigns | Automation |
|------------|-----------------------------|---------------------|-----------------------|------------------------------------|------------|
| Free       | 1,000                       | 30                  | 100                   | No                                 | No         |
| Basic      | 5,000                       | 120                 | 1,000                 | No                                 | No         |
| Pro        | 1,000,000                   | 600                 | 10,000                | Yes                                | No         |
| Enterprise | 10,000,000                  | 3,000               | 100,000               | Yes                                | Yes        |

Free and Basic applications can send through the same durable provider pipeline. Pro adds saved templates, segments, campaigns, and analytics. Enterprise adds automation and higher operational limits. Quotas are organization and seat aware, so a team can see which allowance a notification run consumes.

These numbers are the initial policy, not a claim that every application needs a million notifications. Start with a small, explicit audience. A precise notification that helps 200 people is better than a vague blast that trains 200,000 people to turn notifications off.

## Phase one of our Maven repository move

We have also merged [phase one of a move from Maven Central to a Codename One repository on Cloudflare R2](https://github.com/codenameone/CodenameOne/pull/5497).

Maven Central has every right to set commercial usage limits and charge for infrastructure. Codename One also has a workload that is difficult to fit inside those limits. One release currently publishes enough duplicated fat-jar content that our dashboard reports 2.12 GB against an 80 MB storage guideline, 19,962 files against 1,000, and 27 releases against 7.

![Maven Central publishing usage shows Codename One far beyond the soft guidelines](https://www.codenameone.com/blog/maven-central-cloudflare-r2/maven-central-limit.png)

Those limits are soft guidelines, and the dashboard offers both open-source adjustments and a commercial plan. We are not leaving because Sonatype is doing something wrong. We are leaving because our weekly, multi-platform release shape is expensive to host there, while we can provide the same Maven layout free to users on infrastructure that fits it better.

Phase one reduced a measured release payload from 229.5 MB to 76.9 MB. The release pipeline now copies the signed Central staging tree to R2, so it does not perform a second build with potentially different bytes.

![Diagram](https://mermaid.ink/img/Zmxvd2NoYXJ0IExSCiAgICBBWyJKdWx5IDMxPGJyLz5TaHJpbmsgYW5kIGR1YWwgcHVibGlzaCJdIC0tPiBCWyJBdWd1c3QgNzxici8-R2VuZXJhdGVkIFBPTXMgdXNlIFIyIl0KICAgIEIgLS0-IENbIlRocmVlLXdlZWsgb2JzZXJ2YXRpb24gd2luZG93Il0KICAgIEMgLS0-IERbIkF1Z3VzdCAyODxici8-TmV3IHJlbGVhc2VzIG9uIFIyIG9ubHkiXQogICAgQSAtLT4gRVsiTWF2ZW4gQ2VudHJhbCByZW1haW5zIGF1dGhvcml0YXRpdmUiXQogICAgQiAtLT4gRQogICAgRSAtLT4gRlsiRXhpc3RpbmcgQ2VudHJhbCB2ZXJzaW9ucyByZW1haW4gYXZhaWxhYmxlIl0=?type=png&bgColor=ffffff)

Existing projects can prepare for post-cutover versions by adding the repository to both Maven resolution paths:

```xml

        codenameone
        https://repo.codenameone.com/maven2

        codenameone-plugins
        https://repo.codenameone.com/maven2
```

The second block matters. Maven resolves build plugins separately from ordinary dependencies. Adding only ` can leave a future ``codenameone-maven-plugin` version undiscoverable.

New projects receive this automatically on August 7. Existing versions remain on Central. The new repository guarantees at least six months of history, while the optimized payload currently fits about 1.6 years at the recent release rate.

R2 object storage and Cloudflare's edge should improve dependency resolution and remove Central throttling from our release path. We also expect CI and releases to become faster and more stable. Those are expectations we will measure during dual publication, not results we have already proved.

[The Maven article publishes on August 4 with the full payload audit, cutover plan, R2 release safeguards, retention policy, and failure modes we are testing during dual publication.](https://www.codenameone.com/blog/maven-central-cloudflare-r2/)

## On-device AI and MCP on every port

[PR #5467](https://github.com/codenameone/CodenameOne/pull/5467) brings vision, language, and LiteRT inference into the core. The API covers OCR, barcode recognition, face detection, image labels, pose detection, selfie segmentation, document correction, language identification, translation, smart reply, and application-owned `.tflite` models.

The public surface stays portable, but the work remains on the device. Android uses ML Kit for higher-level operations. Apple ports use Vision, Core Image, and Natural Language where they fit. Unsupported ports report an unsupported capability instead of silently uploading input to a cloud fallback.

OCR is deliberately asynchronous because native conversion and recognition cannot block the Codename One EDT:

```java
TextRecognizer recognizer = new TextRecognizer();
recognizer.process(VisionImage.encoded(jpegBytes))
        .ready(result -> textArea.setText(result.getText()))
        .except(error -> Log.e(error));
```

Inference sessions keep an application-owned model loaded across multiple runs. Runtime model downloads can use `ModelCache`, which requires HTTPS and verifies the SHA-256 digest before activating the file. A model changes application behavior, so accepting unverified model bytes would be a software supply chain bug.

[PR #5472](https://github.com/codenameone/CodenameOne/pull/5472) takes the existing semantic MCP server beyond JavaSE. On loopback-capable ports, an LLM can read the component tree, find a button by semantic identity, set text, activate an action, and inspect the resulting state in the actual application.

![Diagram](https://mermaid.ink/img/c2VxdWVuY2VEaWFncmFtCiAgICBwYXJ0aWNpcGFudCBEZXYgYXMgRGV2ZWxvcGVyIGFuZCBMTE0gY2xpZW50CiAgICBwYXJ0aWNpcGFudCBQb3J0IGFzIERldmljZSBwb3J0IGZvcndhcmQKICAgIHBhcnRpY2lwYW50IE1DUCBhcyAxMjcuMC4wLjEgTUNQIHNlcnZlcgogICAgcGFydGljaXBhbnQgVUkgYXMgQ29kZW5hbWUgT25lIEVEVAogICAgRGV2LT4-UG9ydDogQ29ubmVjdCB0byBkZWJ1ZyBkZXZpY2UKICAgIFBvcnQtPj5NQ1A6IEZvcndhcmQgcG9ydCA4NjQyCiAgICBEZXYtPj5NQ1A6IHVpX3NuYXBzaG90CiAgICBNQ1AtPj5VSTogUmVhZCBzZW1hbnRpYyBjb21wb25lbnQgdHJlZQogICAgVUktLT4-RGV2OiBSb2xlcywgdGV4dCwgc3RhdGUsIGFuZCBhY3Rpb25zCiAgICBEZXYtPj5NQ1A6IEFjdGl2YXRlIHNlbWFudGljIHRhcmdldAogICAgTUNQLT4-VUk6IFBlcmZvcm0gY29tcG9uZW50IGFjdGlvbgogICAgVUktLT4-RGV2OiBVcGRhdGVkIHN0YXRl?type=png&bgColor=ffffff)

This replaces coordinate guessing with application semantics. It also creates a control channel. Binding to `127.0.0.1` prevents accidental exposure to the local network, but it does not authenticate other processes on the device or workstation.

```java
if (Display.getInstance().isDebuggableBuild()) {
    MCP.startSocketServer(8642);
}
```

MCP refuses to start in a release build by default. An explicit override exists for controlled test labs, but it should not become a convenience flag in a consumer application.

[The AI and MCP article publishes on August 2 with the capability matrix, portable inference model, semantic debugging loop, and the reasons loopback still needs a release-build gate.](https://www.codenameone.com/blog/on-device-ai-mcp-loopback/)

## Health data without fake certainty

[PR #5475](https://github.com/codenameone/CodenameOne/pull/5475) adds a first-class API for HealthKit, Health Connect, recorded workouts, sparse nutrition data, deterministic simulation, and eight adopted Bluetooth health sensor profiles.

The public API is divided at the boundaries an application needs to reason about:

|             Package              |                                 Responsibility                                 |
|----------------------------------|--------------------------------------------------------------------------------|
| `com.codename1.health`           | Stores, permissions, samples, queries, aggregates, sources, and change cursors |
| `com.codename1.health.workout`   | Recorded workout sessions, events, configuration, and collected samples        |
| `com.codename1.health.sensors`   | Live standard Bluetooth health devices without requiring a phone health store  |
| `com.codename1.health.nutrition` | Sparse nutrient records where an absent value remains absent                   |

Simulator, desktop, and JavaScript builds return `LOCAL_ONLY`. That is a supported store with reads and writes, not a missing provider. Only Android provider failures should send the user to provider setup:

```java
Health health = Health.getInstance();
HealthAvailability availability = health.getAvailability();
if (availability == HealthAvailability.PROVIDER_NOT_INSTALLED
        || availability == HealthAvailability.PROVIDER_UPDATE_REQUIRED) {
    health.openProviderSetup();
    return;
}
if (availability == HealthAvailability.NOT_SUPPORTED) {
    return;
}

HealthStore store = health.getStore();
```

![Diagram](https://mermaid.ink/img/Zmxvd2NoYXJ0IFRCCiAgICBBWyJBcHBsaWNhdGlvbiJdIC0tPiBCWyJIZWFsdGgiXQogICAgQiAtLT4gQ1siSGVhbHRoU3RvcmUiXQogICAgQiAtLT4gRFsiV29ya291dE1hbmFnZXIiXQogICAgQiAtLT4gRVsiSGVhbHRoU2Vuc29ycyJdCiAgICBDIC0tPiBGWyJIZWFsdGhLaXQiXQogICAgQyAtLT4gR1siSGVhbHRoIENvbm5lY3QiXQogICAgQyAtLT4gSFsiTG9jYWwgYW5kIHNpbXVsYXRlZCBzdG9yZSJdCiAgICBFIC0tPiBJWyJFaWdodCBCbHVldG9vdGggTEUgcHJvZmlsZXMiXQogICAgSSAtLT4gRAogICAgSSAtLT4gQw==?type=png&bgColor=ffffff)

Some of the API looks cautious because the platform contracts are cautious. HealthKit does not reveal whether a user denied read access. A completed authorization sheet means the user was asked, not that the application can read the category. The shared API therefore has no `hasReadPermission()` method that would lie on iOS.

The API preserves these distinctions throughout the model. An empty aggregate returns `null`, not zero. Calendar-day buckets require a time zone. Phone and watch sources remain distinguishable because silently adding overlapping samples can double-count a walk. Unsupported phone mappings fail with `TYPE_NOT_SUPPORTED` instead of returning an empty collection that looks successful.

The simulator includes a mode that grants writes while denying reads without an error. That lets you test the UI mistake that a permissive fake would miss: accusing a user of denying access when the only accurate statement is "no data available."

This API does not make an application HIPAA compliant. It never uploads health data, and it can enforce specific purpose strings and reject unsupported writes. The application still owns access control, encryption, audit records, retention, consent, breach handling, backend contracts, and store disclosures.

[The Health article publishes on August 1 with the platform matrix, authorization trap, sample model, change cursors, workouts, Bluetooth sensors, simulator failure modes, build configuration, and HIPAA boundary.](https://www.codenameone.com/blog/health-api-false-certainty/)

## A polyline is not a route

A map polyline joins coordinates that already exist. It cannot discover the roads, travel time, maneuvers, or alternate paths between them.

[PR #5480](https://github.com/codenameone/CodenameOne/pull/5480) adds `com.codename1.maps.routing`. The smallest useful path is two coordinates:

```java
MapView map = new MapView();
Routing.showRoute(
        map,
        new LatLng(38.8977, -77.0365),
        new LatLng(38.8894, -77.0352)
);
```

The call returns immediately. The active `RouteService` finds a route, then the API draws its geometry and frames the map on the Codename One EDT.

![Diagram](https://mermaid.ink/img/Zmxvd2NoYXJ0IExSCiAgICBBWyJPcmlnaW4sIGRlc3RpbmF0aW9uLCBhbmQgd2F5cG9pbnRzIl0gLS0-IEJbIlJvdXRlUmVxdWVzdCJdCiAgICBCIC0tPiBDWyJSb3V0ZVNlcnZpY2UiXQogICAgQyAtLT4gRFsiUm9hZCBuZXR3b3JrIGNhbGN1bGF0aW9uIl0KICAgIEQgLS0-IEVbIlJvdXRlIGFsdGVybmF0aXZlcyJdCiAgICBFIC0tPiBGWyJHZW9tZXRyeSBhbmQgYm91bmRzIl0KICAgIEUgLS0-IEdbIkRpc3RhbmNlLCBkdXJhdGlvbiwgbGVncywgYW5kIHN0ZXBzIl0KICAgIEYgLS0-IEhbIk1hcFN1cmZhY2UiXQogICAgRyAtLT4gSVsiQXBwbGljYXRpb24gVUkiXQ==?type=png&bgColor=ffffff)

The default service is OSRM, so the first driving route needs no provider signup. It points to the public OSRM demonstration server, which has no production SLA and uses a car profile. A `WALKING` request does not turn that graph into a pedestrian route.

Production applications can point `OsrmRouteService` at their own server or install another provider:

```java
Routing.setService(new OsrmRouteService(
        "https://routing.example.com"
));
```

The application consumes portable `Route` objects rather than provider JSON. Those objects carry alternatives, distance, duration, bounds, geometry, legs, step instructions, maneuver locations, and provider metadata.

This is routing, not turn-by-turn navigation. The release does not claim rerouting, traffic prediction, offline map packages, or voice guidance. Those features need location updates, lifecycle state, and provider-specific rules.

[The routing article publishes on August 3 with custom route styling, ETA handling, encoded polyline support, OSRM limits, travel modes, provider replacement, and the boundary between a route result and navigation.](https://www.codenameone.com/blog/road-following-map-routing/)

Tomorrow I will start taking these changes one at a time with the new Health API. For today, please send a real notification through `cloud.codenameone.com`. This is the one week when you can compare the old and new push implementations side by side. If anything behaves differently, [open an issue](https://github.com/codenameone/CodenameOne/issues) before we move the traffic next week.
