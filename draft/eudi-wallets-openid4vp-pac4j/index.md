---
title: "EUDI wallets with OpenID4VP and pac4j: same device and another device"
date: "2026-01-01"
description: "Authenticate Java Spring Boot users with EUDI wallets and pac4j: SD-JWT VC on the same phone, or an mdoc PID via QR code on another device."
authors:
  - "jerome-leleu"
image: "openid4vp-pac4j.jpg"
categories:
  - "Java"
  - "Security"
  - "Tutorials"
---

The European Digital Identity wallet is coming, and applications accepting it will need to talk to it. This tutorial brings wallet authentication to a Java Spring Boot application with pac4j, through two complete examples: a browser and wallet on the same phone, then a browser on a computer and a wallet on a phone, connected by a QR code.

[pac4j](https://www.pac4j.org/) is an open-source Java security library for authentication and authorization across multiple web frameworks. I am Jérôme LELEU, its creator and maintainer. This article combines the two walkthroughs originally published on the pac4j blog: [on the same device](https://www.pac4j.org/blog/openid4vp_in_action_same_device.html) and [on another device](https://www.pac4j.org/blog/openid4vp_in_action_another_device.html).

We start with the shared background and preparation, build each example, then look at the limitations and next steps. Each example has its own Maven project and complete configuration; run one at a time on port 8080.

## 1) Background and shared preparation

### 1.1) Why prepare for EUDI wallets?

[Regulation (EU) 2024/1183](https://eur-lex.europa.eu/eli/reg/2024/1183/oj), better known as eIDAS 2.0, entered into force on May 20, 2024. EU member states must offer EUDI wallets by the end of 2026, holding *person identification data* (PID) and attestations such as driving licences or diplomas. [Using a wallet remains voluntary](https://ec.europa.eu/digital-building-blocks/sites/spaces/EUDIGITALIDENTITYWALLET/pages/713526976/FAQ).

Article 5f also requires acceptance by public services requiring electronic identification, certain private services requiring strong authentication, and very large online platforms requiring authentication. Private-sector obligations have conditions and exemptions, including for microenterprises and small enterprises, with a deadline tied to implementing acts, generally described as late 2027.

For applications in scope, wallet authentication is becoming an integration requirement worth preparing for.


### 1.2) Why it is hard

A wallet login looks simple: the user opens their wallet, sees what your application asks for, consents, and the wallet sends a signed proof. Behind this, the stack is anything but simple. Your application, the *verifier* in the vocabulary of the specifications, has to deal with:

- [OpenID for Verifiable Presentations 1.0](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html), the protocol itself: how the wallet is invoked, how it fetches the request, how it posts the response, with its [Digital Credentials Query Language](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#name-digital-credentials-query-l) (DCQL) to say what you want
- the [High Assurance Interoperability Profile](https://openid.net/specs/openid4vc-high-assurance-interoperability-profile-1_0.html) (HAIP), which pins the options the EUDI wallet uses, and the [Architecture and Reference Framework](https://eudi.dev/2.7.3/architecture-and-reference-framework-main/) (ARF) of the European Commission, with its PID rulebook
- two credential formats: the [SD-JWT VC](https://datatracker.ietf.org/doc/draft-ietf-oauth-sd-jwt-vc/) of the IETF, built on [selective disclosure JWTs](https://datatracker.ietf.org/doc/draft-ietf-oauth-selective-disclosure-jwt/), and the mobile documents (mdoc) of [ISO/IEC 18013-5](https://www.iso.org/standard/69084.html), encoded in CBOR and signed with COSE
- the plumbing underneath: signed request objects ([RFC 9101](https://www.rfc-editor.org/rfc/rfc9101)), JWS, JWE and JWK, X.509 certificate chains and revocation, decentralized identifiers, and the [Digital Credentials API](https://www.w3.org/TR/digital-credentials/) of the browsers for the newest flows.

And several ways to identify the verifier, several ways to reach the wallet, several response modes, each with its own validation rules. Implementing this by hand, and keeping it right as the specifications move, is a project in itself.


### 1.3) pac4j to the rescue

pac4j has one security model for every protocol: a `Client` starts the authentication, a *callback* receives the answer, a `UserProfile` ends up in the session. CAS, SAML, OpenID Connect, OAuth all fit in it, and a digital wallet does too.

The new `pac4j-openid4vp` module, introduced in pac4j v6.6.0, adds the `EudiWalletClient`: you give it a DCQL query, a keystore and the issuers you trust, and it handles the signed request, the encrypted response, credential verification and the user profile. If you have already used pac4j, the integration will look familiar.

We will walk through both flows: first on the same device, where the user clicks a link on their phone and presents an **SD-JWT VC** PID; then on another device, where a QR code connects the computer browser to the phone wallet presenting an **mdoc** PID. The credential format and the device arrangement are separate choices: these examples pair them to demonstrate both formats.


### 1.4) A young protocol: test with demo wallets

OpenID4VP 1.0 was finalized in 2025, and wallet implementations are still evolving. Demo wallets let you test the integration while preparing for national EUDI deployments; expect differences between wallets and versions.

A very good one is the **Paradym wallet** by [Animo](https://animo.id), available for Android and iOS, and its [playground](https://playground.paradym.id/) which plays both the issuer and the verifier:

1. install the Paradym wallet on your phone
2. open [playground.paradym.id](https://playground.paradym.id/) on your computer and, on the **Issuance** page, issue both **PID SD-JWT** (for part 2) and **PID mDoc** (for part 3): for each card, the page shows a QR code; scan it with the Paradym wallet, accept the offer, and the card appears in the wallet
3. keep the **Trust Information** page of the playground at hand: it shows the certificate of the playground issuer, which your application must trust.

The cards are Emma Charlotte van der Berg's, a fictitious Dutch citizen: given name, family name, birth date, address... The mdoc PID also carries a `personal_administrative_number`, used by the profile resolver in part 3.

Issue the card from this playground and no other: a PID issued elsewhere, by the older [playground.animo.id](https://playground.animo.id/) for instance, is signed by another certificate and the truststore in section 1.6 refuses it. If the wallet already holds PID cards from another source, delete them, or at least pick the right one when the wallet asks which card to present.


### 1.5) ngrok: a real URL for the wallet

The wallet talks to your application over HTTPS from the phone: it fetches the request object and posts its response. Although the browser and wallet run on the same phone, the application runs on your development computer. `localhost` on the phone points to the phone itself, so it cannot reach that application.

The simplest way during development is [ngrok](https://ngrok.com/), which exposes your local port on a public HTTPS address:

```bash
ngrok http 8080
```

It prints a public URL such as `https://abcd-1234.ngrok-free.dev`: this is the base URL of your application for the rest of this article. The [free plan](https://ngrok.com/docs/pricing-limits/free-plan-limits) includes a development domain assigned to your account. Copy the URL ngrok prints into the property below. If the browser shows an ngrok warning page, choose **Visit Site** to continue.

### 1.6) Keys and trust

Both examples use the same key and trust setup: two keystores in the `metadata` directory. After creating the project in section 2.1 or 3.1, run the `keytool` commands below from that project's root, next to its `pom.xml`: the paths are relative to it, like the ones in the configuration. If you try both projects, repeat this setup in each project directory or copy the two demo keystores into the second project's `metadata` directory.

**The verifier key.** `EudiWalletClient` signs requests and identifies the verifier by its certificate hash (`x509_hash`). Production EUDI deployments use a *relying party access certificate* issued by the ecosystem. Both demos use a self-signed certificate with an EC P-256 key, accepted by the tested Paradym wallet; self-signed verifier certificates are outside the HAIP requirements:

```bash
keytool -genkeypair -alias rp -keyalg EC -groupname secp256r1 -sigalg SHA256withECDSA \
  -dname "CN=pac4j demo verifier,O=pac4j" -validity 365 \
  -keystore metadata/verifier.p12 -storetype PKCS12 -storepass changeit
```

**The issuers you trust.** No credential is accepted unless its issuer is trusted, and pac4j trusts nobody by default. For an mdoc, the trust anchor is the *issuing authority certificate authority* (IACA) of the PID provider. Copy the *Issuer Certificate* from the [Trust Information](https://playground.paradym.id/info) page of the playground into a `paradym-issuer.pem` file, and import it into a truststore:

```bash
keytool -importcert -alias paradym -file paradym-issuer.pem -noprompt \
  -keystore metadata/pid-issuers.p12 -storetype PKCS12 -storepass changeit
```


## 2) Same device

The browser and wallet run on the same phone. We request an SD-JWT VC PID and let the wallet return to the original browser after sharing it.

### 2.1) Create the Spring Boot project

This is a plain Spring Boot web application, with [spring-webmvc-pac4j](https://github.com/pac4j/spring-webmvc-pac4j) as the pac4j implementation. Java 17 or later and Maven are enough.

```bash
mkdir -p wallet-same-device/src/main/java/org/pac4j/demo/wallet
mkdir -p wallet-same-device/src/main/resources wallet-same-device/metadata
cd wallet-same-device
```

The `pom.xml`:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>4.0.8</version>
        <relativePath/>
    </parent>
    <groupId>org.pac4j.demo</groupId>
    <artifactId>wallet-same-device</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <java.version>17</java.version>
        <pac4j.version>6.6.0</pac4j.version>
        <kotlin.version>2.4.21</kotlin.version>
        <kotlin-coroutines.version>1.11.0</kotlin-coroutines.version>
        <kotlin-serialization.version>1.12.0</kotlin-serialization.version>
        <jackson-2-bom.version>2.22.3</jackson-2-bom.version>
        <commons-lang3.version>3.21.0</commons-lang3.version>
        <slf4j.version>2.0.20</slf4j.version>
        <commons-logging.version>1.3.5</commons-logging.version>
        <jspecify.version>1.0.0</jspecify.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <!-- align pac4j-core and shared dependencies with pac4j-openid4vp -->
            <dependency>
                <groupId>org.pac4j</groupId>
                <artifactId>pac4j-parent</artifactId>
                <version>${pac4j.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <!-- spring-webmvc-pac4j otherwise pulls an older Jakarta EE adapter -->
            <dependency>
                <groupId>org.pac4j</groupId>
                <artifactId>pac4j-jakartaee</artifactId>
                <version>${pac4j.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- the pac4j implementation for Spring MVC -->
        <dependency>
            <groupId>org.pac4j</groupId>
            <artifactId>spring-webmvc-pac4j</artifactId>
            <version>8.0.4</version>
        </dependency>
        <!-- the OpenID4VP support: the application is a verifier -->
        <dependency>
            <groupId>org.pac4j</groupId>
            <artifactId>pac4j-openid4vp</artifactId>
            <version>${pac4j.version}</version>
        </dependency>
        <!-- the SD-JWT VC verification, an optional library of pac4j -->
        <dependency>
            <groupId>eu.europa.ec.eudi</groupId>
            <artifactId>eudi-lib-jvm-sdjwt-kt</artifactId>
            <version>0.20.2</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

SD-JWT VC verification relies on the [EUDI SD-JWT library](https://github.com/eu-digital-identity-wallet/eudi-lib-jvm-sdjwt-kt), which pac4j declares as optional. This example uses version `0.20.2`, matching pac4j. The imported `pac4j-parent` dependency management aligns `pac4j-core` and shared libraries. The explicit management entry for `pac4j-jakartaee` also aligns the adapter pulled in by `spring-webmvc-pac4j`; setting a `pac4j.version` property alone would not override that adapter's transitive version.

The runtime versions match pac4j's build: Kotlin `2.4.21`, Coroutines `1.11.0` and Serialization `1.12.0`. Serialization `1.12.0` is available from Maven Central; the same-device demo does not need the walt.id repository. Spring Boot 4.0.8 supplies Spring Framework 7.0.9, matching pac4j; the properties also align Jackson 2, Commons Lang, SLF4J, Commons Logging and JSpecify. Jackson 3 remains managed by Boot for its own web stack.

Boot's default Kotlin dependencies would otherwise override pac4j's versions. In particular, Coroutines 1.10.2 lacks `BuildersKt.runBlockingK$default`, called by the current pac4j adapter, and causes a `NoSuchMethodError` when the wallet posts its response.

Use the released pac4j `6.6.0` dependencies declared above; no local snapshot installation is needed. Check the resolved versions with `mvn dependency:tree` and keep all pac4j modules on the same version.

The `src/main/resources/application.properties` file holds the base URL and enables the logs we will read at the end:

```properties
app.base-url=https://abcd-1234.ngrok-free.dev
logging.level.org.pac4j.openid4vp=DEBUG
logging.level.PROTOCOL_MESSAGE.OPENID4VP=DEBUG
```

And the usual Spring Boot entry point:

```java
package org.pac4j.demo.wallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WalletApplication {

    public static void main(final String[] args) {
        SpringApplication.run(WalletApplication.class, args);
    }
}
```


Before configuring pac4j, prepare the two keystores in this project's `metadata` directory as described in section 1.6.

### 2.2) Configure pac4j

The configuration is a `Pac4jSecurityConfig` of `spring-webmvc-pac4j`: the `Config` bean defines the client, and the interceptors say which URLs it protects.

```java
package org.pac4j.demo.wallet;

import org.pac4j.core.config.Config;
import org.pac4j.core.config.properties.KeystoreProperties;
import org.pac4j.core.http.callback.PathParameterCallbackUrlResolver;
import org.pac4j.openid4vp.client.EudiWalletClient;
import org.pac4j.openid4vp.config.OpenId4VpConfiguration;
import org.pac4j.openid4vp.dcql.EudiPidQuery;
import org.pac4j.openid4vp.verifier.SdJwtVcVerifier;
import org.pac4j.openid4vp.verifier.trust.CertificateTrustedIssuer;
import org.pac4j.springframework.config.Pac4jSecurityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import java.util.UUID;

import static org.pac4j.core.profile.definition.CommonProfileDefinition.FAMILY_NAME;
import static org.pac4j.openid4vp.profile.EudiPidProfileDefinition.GIVEN_NAME;

@Configuration
public class SecurityConfig extends Pac4jSecurityConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);

    @Value("${app.base-url}")
    private String baseUrl;

    @Bean
    public Config config() {
        // the request: signed with the key of the verifier, asking for two attributes of the PID as an SD-JWT VC
        final var configuration = new OpenId4VpConfiguration()
            .setKeystore(new KeystoreProperties("./metadata/verifier.p12")
                .setKeyStoreType("PKCS12")
                .setKeyStoreAlias("rp")
                .setKeystorePassword("changeit")
                .setPrivateKeyPassword("changeit"))
            .setDcqlQuery(EudiPidQuery.sdJwtVc(GIVEN_NAME, FAMILY_NAME))
            // Mandatory when using the same browser
            .setUseRedirectUri(true);

        // the response: an SD-JWT VC is accepted if its certificate chain ends in the truststore
        final var issuers = new CertificateTrustedIssuer(new KeystoreProperties("./metadata/pid-issuers.p12")
            .setKeyStoreType("PKCS12")
            .setKeystorePassword("changeit"));
        // certificate revocation checking is disabled to simplify this demo; keep it enabled in production
        issuers.setCertificateRevocationEnabled(false);
        // the playground PID references a status list: this demo logs the reference and accepts the credential,
        // a real status checker must retrieve the list, verify it and check the entry of the credential
        configuration.addCredentialVerifier(new SdJwtVcVerifier().addTrustedIssuer(issuers)
            .setStatusChecker(credential -> LOGGER.info("credential status not checked: {}", credential.getClaims().get("status"))));

        // the demo PID carries no stable identifier: a new profile identifier at each login
        configuration.setProfileIdResolver(credentials -> UUID.randomUUID().toString());

        final var client = new EudiWalletClient(configuration);
        // keep the client name in the callback path to work around URL decoding in the tested wallet
        client.setCallbackUrlResolver(new PathParameterCallbackUrlResolver());
        return new Config(baseUrl + "/callback", client);
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        addSecurity(registry, "EudiWalletClient").addPathPatterns("/protected/**");
    }
}
```

A few configuration details:

- `EudiWalletClient` pins the HAIP choices: the request is signed and identifies the verifier by `x509_hash`, the hash of the certificate found in the keystore, and the response comes back encrypted (`direct_post.jwt`). These settings are applied by the client; they do not constitute complete HAIP conformance validation.
- `SdJwtVcVerifier` verifies the expected format. With this trust configuration, the wallet's `x5c` certificate chain must lead to a certificate in your truststore, in addition to passing the signature, validity, disclosure and holder-binding checks.
- The profile identifier is yours to choose: `EudiWalletClient` has no default and refuses to initialize until you configure a resolver. If the PID provider issues a stable, unique and non-reassigned `personal_administrative_number`, request it in DCQL and use `ProfileIdResolver.issuerAndClaim(PERSONAL_ADMINISTRATIVE_NUMBER)`. That claim is not guaranteed for every PID, and this demo does not ask for it, hence the random identifier: it identifies a login, not a persistent user account.
- The callback URL is the regular pac4j one, and the wallet uses it too: it fetches the request object there and posts its response there, without any browser session.
- `setUseRedirectUri(true)` enables the automatic return to the browser. After accepting the wallet response, pac4j returns its callback URL as `redirect_uri`, with a fresh `response_code`. The wallet opens that URL in the browser; pac4j checks the code against the transaction of the browser session, saves the profile and redirects to the protected page originally requested. The option defaults to `false`; this same-device example enables it explicitly.

The playground PID carries a `status` claim, a reference to an [OAuth Token Status List](https://datatracker.ietf.org/doc/draft-ietf-oauth-status-list/): the list where the issuer marks the credentials it has revoked or suspended. pac4j refuses a credential carrying a status unless a *status checker* is configured, because only the application can decide how to read the list, and it never fetches it on its own. The checker of this demo only logs the reference and accepts the credential, so revocation is not checked here. A real checker retrieves the status list token, verifies its signature with the same trust as the credential and checks the entry at the index of the credential: that deserves a dedicated article. See [credential status](https://www.pac4j.org/docs/clients/openid4vp-sd-jwt-vc-verifier.html#7-credential-status). Disabling certificate revocation does not bypass this check.


### 2.3) The DCQL query

The query says what your application wants. `EudiPidQuery.sdJwtVc(GIVEN_NAME, FAMILY_NAME)` builds it in one line for the PID; this is what the wallet receives:

```json
{
  "credentials": [
    {
      "id": "pid",
      "format": "dc+sd-jwt",
      "meta": { "vct_values": ["urn:eudi:pid:1"] },
      "claims": [
        { "path": ["given_name"] },
        { "path": ["family_name"] }
      ]
    }
  ]
}
```

One credential query, identified as `pid`, for an SD-JWT VC whose type is the EUDI PID, and the two claims to disclose. The same can be written by hand, in JSON when it comes from a configuration file:

```java
configuration.setDcqlQuery("""
    {"credentials":[{"id":"pid","format":"dc+sd-jwt","meta":{"vct_values":["urn:eudi:pid:1"]},
     "claims":[{"path":["given_name"]},{"path":["family_name"]}]}]}""");
```

Or in Java, with access to expected values, alternatives (`claim_sets`, `credential_sets`) and trusted authorities:

```java
configuration.setDcqlQuery(new DcqlQuery()
    .addCredential(new CredentialQuery("pid", CredentialFormat.SD_JWT_VC)
        .setVctValues("urn:eudi:pid:1")
        .addClaim("given_name")
        .addClaim("family_name")));
```

Whatever the wallet presents is checked against the query again by pac4j: the format, the type, the claims. The constraints are not left to the wallet.


### 2.4) The pages

Two pages are enough: a public home page, and a protected page which shows the profile. Both come from a single Spring MVC controller, `WalletController`, to create next to the two previous classes:

```java
package org.pac4j.demo.wallet;

import org.pac4j.core.profile.ProfileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WalletController {

    @Autowired
    private ProfileManager profileManager;

    @GetMapping("/")
    @ResponseBody
    public String index() {
        return "<h1>Home</h1>"
            + "<p>Start in your phone's default browser, using its regular profile (not private browsing). "
            + "The wallet must return to the same browser and profile to complete authentication.</p>"
            + "<p><a href='/protected/index'>Open the protected page: the wallet opens</a></p>";
    }

    @GetMapping("/protected/index")
    @ResponseBody
    public String protectedPage() {
        return "<h1>Protected page</h1><p>" + HtmlUtils.htmlEscape(profileManager.getProfiles().toString()) + "</p>"
            + "<p><a href='/logout'>Logout</a></p>";
    }
}
```

The home page needs only the link which starts authentication. The callback is handled by pac4j: with `setUseRedirectUri(true)`, the wallet returns the browser there automatically, then pac4j restores the original request. No second link, polling script or application code relaying the `response_code` is needed.

Run the application, with ngrok running next to it:

```bash
mvn spring-boot:run
```


### 2.5) The flow, step by step

Everything happens on the phone. **First open the Paradym wallet and unlock it**, then leave it in the background: the flow fails if the wallet is not already running when the browser hands it the `openid4vp://` link. Then open the ngrok URL **in the phone's default browser, using its regular profile (not private browsing)** and click the link on the home page: `Open the protected page: the wallet opens`.

The wallet may open the return URL in the phone's default browser. Starting in another browser, a private browsing session or an embedded browser can therefore lose the original session: the callback cannot complete authentication and the demo returns to the home page. Use the same browser and profile throughout the flow.

The six screens below show the home page where login starts, the request to review, the warning about the unverified verifier, the PIN code, the wallet success screen and the return to the protected page in the browser, where the profile is displayed. The steps below follow the exchange through to that final page.

{{< gallery cols="3" >}}
mobilebeforelogin.jpg | 1. The application home page: open the protected page to start the wallet login
sdjwtvc_request.jpg | 2. Paradym: review the request
organization.jpg | 3. Paradym: organization not verified
pincode.jpg | 4. Paradym: enter the PIN code
success.jpg | 5. Paradym: success, the information has been shared
mobilepostlogin.jpg | 6. Back in the application: the protected page displays Emma Charlotte van der Berg's profile after the automatic wallet return
{{< /gallery >}}

**1. The browser asks for the protected page.** pac4j finds no profile, creates a *transaction* and redirects to an `openid4vp://` URL. The browser hands this scheme to the operating system, which opens the wallet.

**2. The wallet fetches the request.** The URL only carries the identifier of the verifier and a `request_uri`: the wallet calls it, on the callback URL of your application, and receives the signed request object, with the DCQL query and the public key to encrypt its response.

**3. The user consents.** The wallet shows who is asking, the hash of your certificate for now, and what is asked: the PID, given name and family name. The user picks the card and confirms with their PIN.

**4. The wallet posts its response.** An encrypted JWT, containing the SD-JWT VC with the two disclosures and a key binding JWT proving the wallet holds the key of the credential, posted on the callback URL. pac4j decrypts, verifies and keeps the result for the browser session which started the transaction. Its HTTP 200 JSON response contains a `redirect_uri` pointing to `/callback/EudiWalletClient?response_code=...`. This is the browser return URL, not the `response_uri` where the wallet just posted the presentation.

**5. The browser returns automatically.** The wallet opens the `redirect_uri` in the default browser. Because the flow started in that browser and profile, its session cookie accompanies the callback. pac4j checks the `response_code` against that session's pending transaction, retrieves the profile already validated at step 4, consumes the transaction and stores the profile in the session. It then redirects to `/protected/index`, the URL saved at step 1: the protected page shows Emma Charlotte van der Berg. The code is handled entirely by pac4j and is not carried to the final protected URL.

The `response_code` is a fresh random value required by [OpenID4VP section 8.2](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#section-8.2). The callback requires both this code and the session which started the transaction; a missing or wrong code is refused, another session cannot claim the presentation, and a completed transaction cannot be consumed again. Keep the return in the original browser: a different browser or an isolated webview does not have its session cookie. Part 3 uses polling instead, because the wallet is on the phone while the browser waiting for the answer is on the computer.


### 2.6) What the logs say

The two loggers enabled in `application.properties` give two views of the flow. The `PROTOCOL_MESSAGE.OPENID4VP` logger prints the messages themselves, as they were sent or received, without any interpretation: one line per message, starting with `>>>` when the application sent it, `<<<` when it received it, followed by the other party, the browser or the wallet. The `org.pac4j.openid4vp` logger tells what pac4j did with them: the transaction created, the request object built, the response decrypted and verified, the profile built. The excerpts below are abbreviated; the final JSON response and browser return illustrate the `setUseRedirectUri(true)` configuration.

The redirection, step 1: a transaction is created, expiring five minutes later, and the browser is sent to the wallet URL. Note the `request_uri`, on the callback URL with the client name in its path, and the identifier of the verifier, the hash of its certificate:

```
EudiWalletClient : applying EUDI wallet settings for client EudiWalletClient: X509_HASH, DIRECT_POST_JWT, PID profile definition
OpenId4VpConfiguration : OpenID4VP configuration checked: 1 credential queries, verifier formats=[SD_JWT_VC, MSO_MDOC], signed requests=true
VpTransaction : transaction 51fef2…: - -> CREATED (client EudiWalletClient, response mode direct_post.jwt, expires at 2026-10-12T09:19:39Z)
PROTOCOL_MESSAGE.OPENID4VP : [tx 51fef2…] >>> browser wallet URL: openid4vp://?client_id=x509_hash%3ApntH00p6r9h1QcHOvJAVv90dkHvm5Jhx_GBAKcJH5CE&request_uri=https%3A%2F%2Fabcd-1234.ngrok-free.dev%2Fcallback%2FEudiWalletClient%3Fvp_tx%3D51fef2…&request_uri_method=post
```

The first two lines appear once, at the first request: the client initializes lazily. The configuration comes with a default verifier per format; section 2.2 replaced the SD-JWT VC one with the trusted issuers and the mdoc one stays, hence the two formats listed.

Step 2: the wallet fetches the request object, which is built for it and signed:

```
PROTOCOL_MESSAGE.OPENID4VP : [tx 51fef2…] <<< wallet GET vp_tx=51fef2…
OpenId4VpRequestObjectBuilder : client metadata built for transaction 51fef2…: formats=[dc+sd-jwt, mso_mdoc], response encryption methods=[A128GCM, A256GCM]
OpenId4VpRequestObjectBuilder : request object signed for transaction 51fef2… with ES256
VpTransaction : transaction 51fef2…: CREATED -> REQUEST_RETRIEVED (GET of the request URI)
PROTOCOL_MESSAGE.OPENID4VP : [tx 51fef2…] >>> wallet 200 eyJ4NWMiOlsiTUlJQmNq…
```

These demo traces show a GET even though the wallet URL advertises `request_uri_method=post`. pac4j accepts both methods; a wallet following the advertised method uses POST and can include its metadata and a wallet nonce.

The request object is a JWT whose header carries your certificate (`x5c`, with the `rp` alias as `kid`) and whose payload is the request. Decoded, its payload reads:

```json
{
  "client_id": "x509_hash:pntH00p6r9h1QcHOvJAVv90dkHvm5Jhx_GBAKcJH5CE",
  "response_type": "vp_token",
  "response_mode": "direct_post.jwt",
  "response_uri": "https://abcd-1234.ngrok-free.dev/callback/EudiWalletClient?vp_tx=51fef2…",
  "nonce": "281fec06efb9492b95b45c09a537e624",
  "state": "7b99f8b2372b42d6b819942add896494…",
  "dcql_query": { "credentials": [ { "id": "pid", "format": "dc+sd-jwt", "meta": { "vct_values": ["urn:eudi:pid:1"] },
                  "claims": [ { "path": ["given_name"] }, { "path": ["family_name"] } ] } ] },
  "client_metadata": { "jwks": { "keys": [ { "kty": "EC", "use": "enc", "crv": "P-256", "alg": "ECDH-ES", "kid": "2f75d54c…", "x": "…", "y": "…" } ] },
                       "encrypted_response_enc_values_supported": ["A128GCM", "A256GCM"],
                       "vp_formats_supported": { "dc+sd-jwt": { "sd-jwt_alg_values": ["ES256"], "kb-jwt_alg_values": ["ES256"] },
                                                 "mso_mdoc": { "issuerauth_alg_values": [-7], "deviceauth_alg_values": [-7] } } },
  "aud": "https://self-issued.me/v2", "iat": 1791796479, "exp": 1791796779
}
```

Step 4: the wallet posts its response, a JWE encrypted with the ephemeral key published in the request. pac4j decrypts it, verifies the SD-JWT VC, its chain, its disclosures and its key binding, hands the status reference to the checker of section 2.2, and builds the profile:

```
PROTOCOL_MESSAGE.OPENID4VP : [tx 51fef2…] <<< wallet POST vp_tx=51fef2…&response=eyJraWQiOiIyZjc1ZDU0Yy…
OpenId4VpAuthenticator : response decrypted and authentication tag verified for transaction 51fef2…: alg=ECDH-ES, enc=A128GCM, members=[vp_token, state]
CertificateTrustedIssuer : issuer truststore loaded: 1 trust anchors
CertificateTrustedIssuer : certificate revocation lists loaded: 0
SecurityConfig : credential status not checked: {status_list={idx=18, uri=https://metadata.paradym.id/7f22b55a…/status-lists/86852c16…}}
SdJwtVcVerifier : SD-JWT VC verified for transaction 51fef2…: holder binding=true
OpenId4VpAuthenticator : presentation 1/1 verified for transaction 51fef2…, query pid: format=SD_JWT_VC, verifier=SdJwtVcVerifier
OpenId4VpAuthenticator : wallet response validated for transaction 51fef2…: 1 credential queries answered
EudiPidProfile : adding => key: issuing_country / value: NL / class java.lang.String
EudiPidProfile : adding => key: vct / value: urn:eudi:pid:1 / class java.lang.String
EudiPidProfile : adding => key: status / value: {status_list={idx=18, uri=https://metadata.paradym.id/7f22b55a…/status-lists/86852c16…}} / class …
EudiPidProfile : adding => key: family_name / value: van der Berg / class java.lang.String
EudiPidProfile : adding => key: given_name / value: Emma Charlotte / class java.lang.String
OpenId4VpAuthenticator : profile built for transaction 51fef2…: profile class=EudiPidProfile, definition=EudiPidProfileDefinition
VpTransaction : transaction 51fef2…: REQUEST_RETRIEVED -> RESPONSE_RECEIVED (presentation validated)
PROTOCOL_MESSAGE.OPENID4VP : [tx 51fef2…] >>> wallet 200 {"redirect_uri":"https://abcd-1234.ngrok-free.dev/callback/EudiWalletClient?response_code=8c731e…"}
```

The profile gets more than the two requested claims: the issuer also signs, outside any disclosure, the `vct`, the issuing country and authority, the validity dates, the holder key (`cnf`) and the status reference, and the wallet cannot withhold them. The two names are the only disclosed claims.

Step 5: the wallet opens the returned callback URL in the browser. pac4j checks the `response_code` using the transaction identifier held in the browser session; the return URL carries no `vp_tx`. It then picks up the validated profile, without verifying it twice, and the regular callback redirects to the saved `/protected/index` URL:

```
VpTransaction : transaction 51fef2…: RESPONSE_RECEIVED -> CONSUMED (the browser came back)
OpenId4VpAuthenticator : transaction 51fef2…: reusing the profile validated when the wallet posted its response, nothing is verified again
```

Note that the logs show the credentials as they travel, tokens and the return `response_code` included: enable them to diagnose, not permanently in production.

## 3) On another device

The browser now runs on a computer, while the wallet stays on the phone. This second project requests an mdoc PID, displays a QR code and polls for the wallet response. Stop the first application before starting this one on port 8080.

### 3.1) Create the Spring Boot project

This is a plain Spring Boot web application, with [spring-webmvc-pac4j](https://github.com/pac4j/spring-webmvc-pac4j) as the pac4j implementation. Java 17 or later and Maven are enough.

```bash
mkdir -p wallet-another-device/src/main/java/org/pac4j/demo/wallet
mkdir -p wallet-another-device/src/main/resources wallet-another-device/metadata
cd wallet-another-device
```

The `pom.xml`. The mdoc verification relies on the [walt.id](https://walt.id) library, which pac4j declares as optional and which walt.id publishes on its own repository:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>4.0.8</version>
        <relativePath/>
    </parent>
    <groupId>org.pac4j.demo</groupId>
    <artifactId>wallet-another-device</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <java.version>17</java.version>
        <pac4j.version>6.6.0</pac4j.version>
        <kotlin.version>2.4.21</kotlin.version>
        <kotlin-coroutines.version>1.11.0</kotlin-coroutines.version>
        <kotlin-serialization.version>1.12.0</kotlin-serialization.version>
        <jackson-2-bom.version>2.22.3</jackson-2-bom.version>
        <commons-lang3.version>3.21.0</commons-lang3.version>
        <slf4j.version>2.0.20</slf4j.version>
        <commons-logging.version>1.3.5</commons-logging.version>
        <jspecify.version>1.0.0</jspecify.version>
    </properties>

    <!-- walt.id publishes the mdoc library outside Maven Central -->
    <repositories>
        <repository>
            <id>waltid-releases</id>
            <url>https://maven.waltid.dev/releases</url>
            <snapshots><enabled>false</enabled></snapshots>
        </repository>
    </repositories>

    <dependencyManagement>
        <dependencies>
            <!-- align pac4j-core and shared dependencies with pac4j-openid4vp -->
            <dependency>
                <groupId>org.pac4j</groupId>
                <artifactId>pac4j-parent</artifactId>
                <version>${pac4j.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <!-- spring-webmvc-pac4j otherwise pulls an older Jakarta EE adapter -->
            <dependency>
                <groupId>org.pac4j</groupId>
                <artifactId>pac4j-jakartaee</artifactId>
                <version>${pac4j.version}</version>
            </dependency>
            <!-- retain walt.id's legacy datetime API using the compatibility artifact used by pac4j -->
            <dependency>
                <groupId>org.jetbrains.kotlinx</groupId>
                <artifactId>kotlinx-datetime-jvm</artifactId>
                <version>0.8.0-0.6.x-compat</version>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- the pac4j implementation for Spring MVC -->
        <dependency>
            <groupId>org.pac4j</groupId>
            <artifactId>spring-webmvc-pac4j</artifactId>
            <version>8.0.4</version>
        </dependency>
        <!-- the OpenID4VP support: the application is a verifier -->
        <dependency>
            <groupId>org.pac4j</groupId>
            <artifactId>pac4j-openid4vp</artifactId>
            <version>${pac4j.version}</version>
        </dependency>
        <!-- the mdoc verification, an optional library of pac4j -->
        <dependency>
            <groupId>id.walt.mdoc-credentials</groupId>
            <artifactId>waltid-mdoc-credentials-jvm</artifactId>
            <version>0.12.0</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

The walt.id library is written in Kotlin. This POM matches pac4j's runtime versions: Kotlin `2.4.21`, Coroutines `1.11.0`, Serialization `1.12.0` and Datetime `0.8.0-0.6.x-compat`. Serialization `1.12.0` is available from Maven Central; the walt.id repository is still needed for the mdoc library. The Datetime compatibility artifact preserves the legacy API used by walt.id 0.12.0. Do not replace it with a version which removes that API. See [Kotlin alignment](https://www.pac4j.org/docs/clients/openid4vp-mdoc-verifier.html#12-kotlin-alignment), especially when verifying both SD-JWT VC and mdoc credentials.

The imported `pac4j-parent` dependency management aligns `pac4j-core` and shared libraries; the explicit `pac4j-jakartaee` entry aligns the adapter pulled in by `spring-webmvc-pac4j` with `pac4j-openid4vp`. Spring Boot 4.0.8 supplies Spring Framework 7.0.9, matching pac4j; the properties align Jackson 2, Commons Lang, SLF4J, Commons Logging and JSpecify too. Jackson 3 remains managed by Boot for its own web stack. Letting Boot select its older Kotlin dependencies can cause runtime linkage failures even if the demo compiles.

Use the released pac4j `6.6.0` dependencies declared above; no local snapshot installation is needed. Check the resolved versions with `mvn dependency:tree` and keep all pac4j modules on the same version.

The `src/main/resources/application.properties` file holds the base URL and enables the logs we will read at the end:

```properties
app.base-url=https://abcd-1234.ngrok-free.dev
logging.level.org.pac4j.openid4vp=DEBUG
logging.level.PROTOCOL_MESSAGE.OPENID4VP=DEBUG
```

And the usual Spring Boot entry point:

```java
package org.pac4j.demo.wallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WalletApplication {

    public static void main(final String[] args) {
        SpringApplication.run(WalletApplication.class, args);
    }
}
```


Before configuring pac4j, prepare the two keystores in this project's `metadata` directory as described in section 1.6.

### 3.2) Configure pac4j

The configuration is a `Pac4jSecurityConfig` of `spring-webmvc-pac4j`: the `Config` bean defines the client, and the interceptors say which URLs it protects.

```java
package org.pac4j.demo.wallet;

import org.pac4j.core.config.Config;
import org.pac4j.core.config.properties.KeystoreProperties;
import org.pac4j.openid4vp.client.EudiWalletClient;
import org.pac4j.openid4vp.config.OpenId4VpConfiguration;
import org.pac4j.openid4vp.dcql.EudiPidQuery;
import org.pac4j.openid4vp.verifier.MdocVerifier;
import org.pac4j.openid4vp.verifier.trust.CertificateTrustedIssuer;
import org.pac4j.springframework.config.Pac4jSecurityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import java.util.Map;

import static org.pac4j.core.profile.definition.CommonProfileDefinition.FAMILY_NAME;
import static org.pac4j.openid4vp.profile.EudiPidProfileDefinition.GIVEN_NAME;
import static org.pac4j.openid4vp.profile.EudiPidProfileDefinition.PERSONAL_ADMINISTRATIVE_NUMBER;
import static org.pac4j.openid4vp.profile.EudiPidProfileDefinition.PID_DOCTYPE;

@Configuration
public class SecurityConfig extends Pac4jSecurityConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);

    @Value("${app.base-url}")
    private String baseUrl;

    @Bean
    public Config config() {
        // the request: signed with the key of the verifier, asking for three attributes of the PID as an mdoc
        final var configuration = new OpenId4VpConfiguration()
            .setKeystore(new KeystoreProperties("./metadata/verifier.p12")
                .setKeyStoreType("PKCS12")
                .setKeyStoreAlias("rp")
                .setKeystorePassword("changeit")
                .setPrivateKeyPassword("changeit"))
            .setDcqlQuery(EudiPidQuery.mdoc(GIVEN_NAME, FAMILY_NAME, PERSONAL_ADMINISTRATIVE_NUMBER))
            // No redirection is needed at the end of the process as the wallet is not on the same device.
            // Therefore, setUseRedirectUri is set to false, although this is unnecessary as it is the default value.
            // This is for demonstration purposes only.
            .setUseRedirectUri(false);

        // the response: an mdoc is accepted if its certificate chain ends in the truststore
        final var issuers = new CertificateTrustedIssuer(new KeystoreProperties("./metadata/pid-issuers.p12")
            .setKeyStoreType("PKCS12")
            .setKeystorePassword("changeit"));
        // certificate revocation checking is disabled to simplify this demo; keep it enabled in production
        issuers.setCertificateRevocationEnabled(false);
        // an mdoc may reference a status list: this demo logs the reference and accepts the document,
        // a real status checker must retrieve the list, verify it and check the entry of the document
        configuration.addCredentialVerifier(new MdocVerifier().addTrustedIssuer(issuers)
            .setStatusChecker((credential, status) -> LOGGER.info("document status not checked: {}", status)));

        // the profile identifier: the issuer and the administrative number, read in the namespace of the PID
        configuration.setProfileIdResolver(credentials -> {
            final var pid = credentials.getVerifiedCredentials().get(EudiPidQuery.PID).get(0);
            final var namespace = (Map<?, ?>) pid.getClaims().get(PID_DOCTYPE);
            return pid.getIssuer() + "|" + namespace.get(PERSONAL_ADMINISTRATIVE_NUMBER);
        });

        final var client = new EudiWalletClient(configuration);
        return new Config(baseUrl + "/callback", client);
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        addSecurity(registry, "EudiWalletClient").addPathPatterns("/protected/**");
    }
}
```

Compared to part 2, three things change:

- `useRedirectUri` stays `false`, its default. In part 2 it is `true`, so the wallet returns the phone's browser to the pac4j callback. Here the authenticated session belongs to the computer: redirecting the phone would not bring its session cookie along. The computer page polls for the response and continues to the callback itself, without a `response_code`.
- The verifier is `MdocVerifier`, which checks the issuer authentication of the document, its digests, its validity, the signature of the device and the binding of the response to the request, and the chain of the document signer up to your truststore.
- The claims of an mdoc are grouped by **namespace**: for the PID, the namespace is its document type, `eu.europa.ec.eudi.pid.1`. The profile identifier reads the administrative number in that namespace, and the profile exposes an attribute named after the namespace, holding the three claims. `ProfileIdResolver.issuerAndClaim(...)` only reads the top-level claims of an SD-JWT VC, hence the small resolver above.

The rest is the same: `EudiWalletClient` pins the HAIP choices, `x509_hash` and `direct_post.jwt`, and the callback URL serves both the wallet and the browser.

These settings do not constitute complete HAIP conformance validation. An mdoc may carry a `status` object in its MSO, a reference to an [OAuth Token Status List](https://datatracker.ietf.org/doc/draft-ietf-oauth-status-list/): the list where the issuer marks the documents it has revoked or suspended. pac4j refuses a document carrying a status unless a *status checker* is configured, because only the application can decide how to read the list, and it never fetches it on its own. The mdoc PID of the playground carries none today, unlike its SD-JWT VC PID, but the checker stays configured so that a document with a status is logged rather than refused: it receives the status object as a map, only logs it and accepts the document, so revocation is not checked here. A real checker retrieves the status list token, verifies its signature with the same trust as the document and checks the entry at the index of the document: that deserves a dedicated article. See [credential status](https://www.pac4j.org/docs/clients/openid4vp-mdoc-verifier.html#7-credential-status). Disabling certificate revocation does not bypass this check.


### 3.3) The DCQL query

The query says what your application wants. `EudiPidQuery.mdoc(GIVEN_NAME, FAMILY_NAME, PERSONAL_ADMINISTRATIVE_NUMBER)` builds it in one line for the PID; this is what the wallet receives:

```json
{
  "credentials": [
    {
      "id": "pid",
      "format": "mso_mdoc",
      "meta": { "doctype_value": "eu.europa.ec.eudi.pid.1" },
      "claims": [
        { "path": ["eu.europa.ec.eudi.pid.1", "given_name"] },
        { "path": ["eu.europa.ec.eudi.pid.1", "family_name"] },
        { "path": ["eu.europa.ec.eudi.pid.1", "personal_administrative_number"] }
      ]
    }
  ]
}
```

One credential query, identified as `pid`, for a mobile document whose type is the EUDI PID, and three data elements, each addressed by its namespace and its name. The same can be written by hand, in JSON when it comes from a configuration file:

```java
configuration.setDcqlQuery("""
    {"credentials":[{"id":"pid","format":"mso_mdoc","meta":{"doctype_value":"eu.europa.ec.eudi.pid.1"},
     "claims":[{"path":["eu.europa.ec.eudi.pid.1","given_name"]},{"path":["eu.europa.ec.eudi.pid.1","family_name"]},
               {"path":["eu.europa.ec.eudi.pid.1","personal_administrative_number"]}]}]}""");
```

Or in Java, with access to expected values, alternatives (`claim_sets`, `credential_sets`) and trusted authorities:

```java
configuration.setDcqlQuery(new DcqlQuery()
    .addCredential(new CredentialQuery("pid", CredentialFormat.MSO_MDOC)
        .setDoctypeValue("eu.europa.ec.eudi.pid.1")
        .addClaim("eu.europa.ec.eudi.pid.1", "given_name")
        .addClaim("eu.europa.ec.eudi.pid.1", "family_name")
        .addClaim("eu.europa.ec.eudi.pid.1", "personal_administrative_number")));
```

Whatever the wallet presents is checked against the query again by pac4j: the format, the document type, the namespaces and the elements. The constraints are not left to the wallet.


### 3.4) The pages, with the QR code

In this flow, the wallet is on the phone rather than the computer running the browser. The page gets the wallet URL as data and displays it as a QR code, then waits for the wallet's response. Three pieces do that:

- the protected page itself, as before
- a `/status` endpoint, which reports the transaction state for the browser session: `PENDING`, `RECEIVED` or `EXPIRED`, from `client.getPresentationStatus(...)`. `RECEIVED` includes a wallet error response; the callback then reports that error
- the page with the QR code and its script, which polls the `/status` endpoint every second until the wallet has answered.

The three live in a single Spring MVC controller, `WalletController`, to create next to the two previous classes:

```java
package org.pac4j.demo.wallet;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.openid4vp.client.OpenId4VpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Locale;
import java.util.Map;

@Controller
public class WalletController {

    @Autowired
    private Config config;

    @Autowired
    private WebContext webContext;

    @Autowired
    private SessionStore sessionStore;

    @Autowired
    private ProfileManager profileManager;

    @GetMapping("/protected/index")
    @ResponseBody
    public String protectedPage() {
        return "<h1>Protected page</h1><p>" + HtmlUtils.htmlEscape(profileManager.getProfiles().toString()) + "</p>"
            + "<p><a href='/logout'>Logout</a></p>";
    }

    /** Whether the wallet answered the transaction of this browser session. */
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> status() {
        final var client = (OpenId4VpClient) config.getClients().findClient("EudiWalletClient").orElseThrow();
        final var status = client.getPresentationStatus(new CallContext(webContext, sessionStore));
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
            .body(Map.of("status", status.name().toLowerCase(Locale.ROOT)));
    }

    @GetMapping("/")
    @ResponseBody
    public String index() {
        return """
            <h1>Log in with your wallet</h1>
            <p>Scan the QR code with your phone's wallet. This page continues automatically when the wallet responds.</p>
            <div id="qr"></div>
            <pre id="log"></pre>
            <script src="https://cdn.jsdelivr.net/npm/qrcode-generator@2.0.4/dist/qrcode.js"></script>
            <script>
              const log = m => document.getElementById('log').textContent += m + '\\n';

              async function start() {
                // the same request a browser would make, but as an AJAX call: pac4j then answers 401,
                // with the wallet URL in the Location header, instead of redirecting the page
                const r = await fetch('/protected/index', {headers: {'X-Requested-With': 'XMLHttpRequest'}});
                if (r.ok) {
                  window.location.assign('/protected/index');
                  return;
                }
                const walletUrl = r.headers.get('Location');
                if (r.status !== 401 || !walletUrl) {
                  throw new Error('Could not start wallet authentication (HTTP ' + r.status + ')');
                }
                log('wallet URL: ' + walletUrl);
                const qr = qrcode(0, 'L');
                qr.addData(walletUrl);
                qr.make();
                document.getElementById('qr').innerHTML = qr.createImgTag(4, 16, 'Scan with your wallet');
                setTimeout(() => poll().catch(reportError), 1000);
              }

              async function poll() {
                const r = await fetch('/status', {cache: 'no-store'});
                if (!r.ok) {
                  throw new Error('Could not read presentation status (HTTP ' + r.status + ')');
                }
                const result = await r.json();
                if (result.status === 'received') {
                  log('the wallet has answered: continuing to the callback');
                  window.location = '/callback?client_name=EudiWalletClient';
                } else if (result.status === 'pending') {
                  setTimeout(() => poll().catch(reportError), 1000);
                } else {
                  log('the transaction expired: reload the page for a new QR code');
                }
              }

              const reportError = error => log(error.message + ': reload the page to retry');
              start().catch(reportError);
            </script>
            """;
    }
}
```

In `start()`, pac4j recognizes the `X-Requested-With` header and returns a 401 with the wallet URL in the `Location` header instead of a redirect. The page renders that URL as a QR code. If the session is already authenticated, it goes directly to the protected page; HTTP or network failures appear in the page's log.

Then `poll()` waits one second between completed status requests until it receives `received` or `expired`. On `received`, it sends the browser to the callback. This limits polling to at most one request per second per page, with a delay of roughly one second plus request time before continuing. Status is read from the transaction store using the identifier in the browser session; the default store is in memory and needs no outgoing network call. The [qrcode-generator](https://github.com/kazuhikoarase/qrcode-generator) library renders the QR code in the browser, without sending its contents to a QR service.

The `WebContext` and `SessionStore` beans come from `spring-webmvc-pac4j`, scoped to the current request; `getPresentationStatus` looks the transaction of the session up and reports whether the wallet has posted its answer.

Run the application, with ngrok running next to it:

```bash
mvn spring-boot:run
```


### 3.5) The flow, step by step

Open the ngrok URL in the browser of your computer: the QR code appears at once.

The whole flow in six screens, the computer at both ends and the phone in between: the page with the QR code, the warning about the unverified verifier, the request to review, the PIN code, the success screen, and the protected page back on the computer. The steps below detail what happens behind each one.

{{< gallery cols="3" >}}
qrcode.png | 1. The page with the QR code, on the computer
organization.jpg | 2. Paradym: organization not verified
mdocrequest.jpg | 3. Paradym: review the request, the three attributes of the PID
pincode.jpg | 4. Paradym: enter the PIN code
success.jpg | 5. Paradym: success, the information has been shared
webpostlogin.png | 6. The protected page with the profile, on the computer
{{< /gallery >}}

**1. The page asks for the protected page, as data.** pac4j finds no profile, creates a *transaction* and answers the `openid4vp://` URL in the `Location` header of a 401. The page draws it as a QR code and starts polling the `/status` endpoint every second.

**2. The phone scans the QR code.** The Paradym wallet (opened and unlocked beforehand: the scan does not work from a cold start) reads the URL, which only carries the identifier of the verifier and a `request_uri`: it calls it, on the callback URL of your application, and receives the signed request object, with the DCQL query and the public key to encrypt its response.

**3. The user consents.** The wallet shows who is asking, the hash of your certificate for now, and what is asked: the PID, given name, family name and administrative number. The user picks the mdoc card and confirms with their PIN.

**4. The wallet posts its response.** An encrypted JWT, containing the mdoc device response, posted on the callback URL. pac4j decrypts, verifies and keeps the result for the browser session which started the transaction. With `useRedirectUri=false`, it answers HTTP 200 with `{}`: the wallet shows that the sharing succeeded, while the computer page continues waiting for its next poll. There is no browser redirect on the phone.

**5. The page continues on its own.** The next poll returns `received`: the page goes to the callback URL, pac4j retrieves the profile already validated when the wallet posted its response, stores it in the session and redirects to the page requested at step 1: the protected page shows Emma Charlotte van der Berg, with the three claims under the `eu.europa.ec.eudi.pid.1` attribute and an identifier made of the issuer and the administrative number.

Nothing to do on the computer between the scan and the result: the whole exchange happened between the phone and the application.


### 3.6) What the logs say

The two loggers enabled in `application.properties` give two views of the flow. The `PROTOCOL_MESSAGE.OPENID4VP` logger prints the messages themselves, as they were sent or received, without any interpretation: one line per message, starting with `>>>` when the application sent it, `<<<` when it received it, followed by the other party, the browser or the wallet. The `org.pac4j.openid4vp` logger tells what pac4j did with them: the transaction created, the request object built, the response decrypted and verified, the profile built. Here is the flow above, shortened.

Step 1: a transaction is created, expiring five minutes later, and the wallet URL is handed to the page in a 401, since the request came as AJAX. Note the `request_uri` on the callback URL, and the identifier of the verifier, the hash of its certificate:

```
EudiWalletClient : applying EUDI wallet settings for client EudiWalletClient: X509_HASH, DIRECT_POST_JWT, PID profile definition
OpenId4VpConfiguration : OpenID4VP configuration checked: 1 credential queries, verifier formats=[SD_JWT_VC, MSO_MDOC], signed requests=true
VpTransaction : transaction c7e29a…: - -> CREATED (client EudiWalletClient, response mode direct_post.jwt, expires at 2026-10-12T14:26:08Z)
PROTOCOL_MESSAGE.OPENID4VP : [tx c7e29a…] >>> browser wallet URL: openid4vp://?client_id=x509_hash%3AE3bEQjnx15zqNwvnVfbxz2GO6SAZXNG6BfdjrZsWvQc&request_uri=https%3A%2F%2Fabcd-1234.ngrok-free.dev%2Fcallback%3Fclient_name%3DEudiWalletClient%26vp_tx%3Dc7e29a…&request_uri_method=post
EudiWalletClient : AJAX request detected -> returning UnauthorizedAction(super=HttpAction(code=401), content=) for https://abcd-1234.ngrok-free.dev/protected/index
```

The first two lines appear once, at the first request: the client initializes lazily. The configuration comes with a default verifier per format; section 3.2 replaced the mdoc one with the trusted issuers and the SD-JWT VC one stays, hence the two formats listed.

Step 2: the wallet fetches the request object, which is built for it and signed:

```
PROTOCOL_MESSAGE.OPENID4VP : [tx c7e29a…] <<< wallet GET client_name=EudiWalletClient&vp_tx=c7e29a…
OpenId4VpRequestObjectBuilder : client metadata built for transaction c7e29a…: formats=[dc+sd-jwt, mso_mdoc], response encryption methods=[A128GCM, A256GCM]
OpenId4VpRequestObjectBuilder : request object signed for transaction c7e29a… with ES256
VpTransaction : transaction c7e29a…: CREATED -> REQUEST_RETRIEVED (GET of the request URI)
PROTOCOL_MESSAGE.OPENID4VP : [tx c7e29a…] >>> wallet 200 eyJ4NWMiOlsiTUlJQmNq…
```

These demo traces show a GET even though the wallet URL advertises `request_uri_method=post`. pac4j accepts both methods; a wallet following the advertised method uses POST and can include its metadata and a wallet nonce.

The request object is a JWT whose header carries your certificate (`x5c`, with the `rp` alias as `kid`) and whose payload is the request. Decoded, its payload reads:

```json
{
  "client_id": "x509_hash:E3bEQjnx15zqNwvnVfbxz2GO6SAZXNG6BfdjrZsWvQc",
  "response_type": "vp_token",
  "response_mode": "direct_post.jwt",
  "response_uri": "https://abcd-1234.ngrok-free.dev/callback?client_name=EudiWalletClient&vp_tx=c7e29a…",
  "nonce": "fb89b405c7ca40689912b460acd0e756",
  "state": "206a039fe14d4d1f9a3fa0c50f8ff207…",
  "dcql_query": { "credentials": [ { "id": "pid", "format": "mso_mdoc", "meta": { "doctype_value": "eu.europa.ec.eudi.pid.1" },
                  "claims": [ { "path": ["eu.europa.ec.eudi.pid.1", "given_name"] }, { "path": ["eu.europa.ec.eudi.pid.1", "family_name"] },
                              { "path": ["eu.europa.ec.eudi.pid.1", "personal_administrative_number"] } ] } ] },
  "client_metadata": { "jwks": { "keys": [ { "kty": "EC", "use": "enc", "crv": "P-256", "alg": "ECDH-ES", "kid": "3885759a…", "x": "…", "y": "…" } ] },
                       "encrypted_response_enc_values_supported": ["A128GCM", "A256GCM"],
                       "vp_formats_supported": { "dc+sd-jwt": { "sd-jwt_alg_values": ["ES256"], "kb-jwt_alg_values": ["ES256"] },
                                                 "mso_mdoc": { "issuerauth_alg_values": [-7], "deviceauth_alg_values": [-7] } } },
  "aud": "https://self-issued.me/v2", "iat": 1791814868, "exp": 1791815168
}
```

The device signature binds the mdoc to this request through a *session transcript*. Its OpenID4VP handover incorporates the response encryption key's JWK thumbprint, the nonce, the verifier's `client_id` and the `response_uri`. pac4j reconstructs this transcript when verifying the presentation, so a response bound to another request is rejected.

Step 4: the wallet posts its response, a JWE encrypted with the ephemeral key published in the request. pac4j decrypts it, verifies the mdoc and builds the profile; the claims come grouped by namespace:

```
PROTOCOL_MESSAGE.OPENID4VP : [tx c7e29a…] <<< wallet POST client_name=EudiWalletClient&vp_tx=c7e29a…&response=eyJraWQiOiIzODg1NzU5YS…
OpenId4VpAuthenticator : response decrypted and authentication tag verified for transaction c7e29a…: alg=ECDH-ES, enc=A128GCM, members=[vp_token, state]
CertificateTrustedIssuer : issuer truststore loaded: 1 trust anchors
CertificateTrustedIssuer : certificate revocation lists loaded: 0
MdocVerifier : mdoc verified for transaction c7e29a…
OpenId4VpAuthenticator : presentation 1/1 verified for transaction c7e29a…, query pid: format=MSO_MDOC, verifier=MdocVerifier
OpenId4VpAuthenticator : wallet response validated for transaction c7e29a…: 1 credential queries answered
EudiPidProfile : adding => key: eu.europa.ec.eudi.pid.1 / value: {given_name=Emma Charlotte, family_name=van der Berg, personal_administrative_number=ADM2023080012345} / class java.util.LinkedHashMap
OpenId4VpAuthenticator : profile built for transaction c7e29a…: profile class=EudiPidProfile, definition=EudiPidProfileDefinition
VpTransaction : transaction c7e29a…: REQUEST_RETRIEVED -> RESPONSE_RECEIVED (presentation validated)
PROTOCOL_MESSAGE.OPENID4VP : [tx c7e29a…] >>> wallet 200 {}
```

Unlike the SD-JWT VC of part 2, the document comes with the three requested elements and nothing else: an mdoc discloses element by element, and the issuer signs nothing outside the namespaces. The status checker of section 3.2 was not called either: the mdoc PID of the playground carries no status in its MSO.

Step 5: the page polled, saw `received`, and came back to the callback URL: the validated profile is picked up, nothing is verified twice, and the identifier is the one of the resolver, the subject of the document signer certificate and the administrative number:

```
VpTransaction : transaction c7e29a…: RESPONSE_RECEIVED -> CONSUMED (the browser came back)
OpenId4VpAuthenticator : transaction c7e29a…: reusing the profile validated when the wallet posted its response, nothing is verified again
EudiWalletClient : credentials: VerifiablePresentationCredentials(… verifiedCredentials={pid=[VerifiedCredential(format=MSO_MDOC, type=eu.europa.ec.eudi.pid.1, issuer=C=NL,CN=Paradym Playground, cryptographicHolderBinding=true, trustedAuthorities={aki=[IyNQbCkBG38NdbXBZT0C3rElbng]}, claims={eu.europa.ec.eudi.pid.1={given_name=Emma Charlotte, family_name=van der Berg, personal_administrative_number=ADM2023080012345}})]})
EudiWalletClient : profile: Optional[EudiPidProfile(id=C=NL,CN=Paradym Playground|ADM2023080012345, attributes={eu.europa.ec.eudi.pid.1={given_name=Emma Charlotte, family_name=van der Berg, personal_administrative_number=ADM2023080012345}}, …)]
```

The credentials line also shows what pac4j established about the document: its issuer, the holder binding, and the *authority key identifier* of the certificate chain, the one a `trusted_authorities` constraint of the DCQL query would be checked against.

Note that the logs show the credentials as they travel, tokens included: enable them to diagnose, not permanently in production.

## 4) Limitations and what comes next

A few things to know before going further:

- **The same-device return needs the original browser session.** `setUseRedirectUri(true)` implements the OpenID4VP redirect, but the wallet must open it in the browser and profile that started the login. Opening another browser or an isolated webview does not transfer that session. In the other-device flow, the computer retains its session and completes authentication through polling.
- **The profile identifier needs an application policy.** The SD-JWT VC example generates a random identifier for each login, so it cannot identify a persistent account. The mdoc example combines the document signer certificate's subject, `C=NL,CN=Paradym Playground` in these traces, with the administrative number. For persistent accounts, your PID provider must guarantee that the combination is stable, unique and never reassigned. Certificate subjects can change or be shared, and not every PID provider issues an administrative number. Choose an identifier policy appropriate to your trusted provider.
- **A refusal was silent** with the tested Paradym wallet: when the user declined or had no matching card, nothing was posted, and the transaction expired after five minutes. In the QR-code flow, the page waited until that expiry. Wallets that post an error let pac4j report it at the callback.
- **Certificate revocation** is disabled in both demos. In production, keep it enabled and supply current CRLs or configure the Java PKIX provider to retrieve revocation data. This setting does not disable credential status checking: credentials carrying status information still need a status checker. The checkers in sections 2.2 and 3.2 only log the status reference and accept the credential; they do not check its revocation or suspension status.
- **These are development integrations.** The self-signed verifier certificate is accepted by the tested demo wallet, but production EUDI deployments require the ecosystem's relying party access certificate. `EudiWalletClient` applies HAIP choices without constituting complete HAIP conformance validation.
- The device MAC of ISO 18013-5, transaction data, the `verifier_attestation` and `openid_federation` prefixes, and the automatic retrieval of status lists are not supported yet.

This integration is introduced in **pac4j v6.6.0**. The full documentation starts at [OpenID for Verifiable Presentations](https://www.pac4j.org/docs/clients/openid4vp.html). Both examples use the same pac4j security model: a client starts authentication, the callback validates and consumes the presentation, and the application receives a user profile. The choice between a browser return and polling determines how the original browser session completes the login.
