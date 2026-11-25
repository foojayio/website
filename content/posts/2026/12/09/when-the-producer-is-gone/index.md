---
title: "When the Producer Is Gone: Designing Key Recovery for Asynchronous Java File Exchange"
date: "2026-12-09"
description: "How CANIS separates encrypted file storage from key custody so asynchronous Java consumers can recover files after the producer exits."
authors:
  - "tiago-souza"
image: "cover.jpg"
categories:
  - "Java Core"
  - "Security"
related_posts:
  - "an-introduction-to-openid-authzen-and-authzen-enforcer"
  - "eudi-wallets-openid4vp-pac4j"
  - "openid-federation-with-pac4j-and-cas"
  - "arcana-pure-java-archive-library"
---

*How we separated encrypted files from key custody so a later consumer can recover data without depending on the original producer.*

A file exchange can outlive the service that created it. A producer writes an encrypted file today; another team processes it next week, perhaps after a deployment, a staff change, or an incident. The ciphertext may still be available, yet the key needed to read it may have disappeared with the producer.

We built CANIS around that gap. It combines a Java client library with a key recovery service for applications operating within a protected enterprise trust domain. The client encrypts the producer's file locally and helps a later consumer decrypt it. The service keeps the recovery keys under organizational control; file contents stay outside it.

## The Enterprise Problem

Consider a finance service that produces a monthly settlement file. It encrypts the file and places it where a reconciliation team can collect it after the reporting period closes. The producer may have finished its job long before reconciliation begins. If the only copy of the decryption key lived in that producer’s memory or deployment secrets, the encrypted file is effectively lost when those resources disappear.

The same pattern appears in claims processing, departmental handoffs, archival exports, and batch integrations. The teams may work on different schedules. A file might wait for an approval, a compliance review, or an external event before anyone is allowed to process it. During that interval, applications are redeployed, credentials rotate, and people change roles.

Encryption protects a file while it waits. The exchange also needs an answer to who will supply the key later, how that key will survive an outage, and who is admitted to request recovery. Giving every consumer a permanent copy of every key complicates custody and rotation. Depending on the original producer at recovery time turns an asynchronous exchange back into a live service dependency.

We therefore treated key recovery as part of the exchange design, before a missing key could become an incident.

## Why Key Recovery Matters

Protecting information and making it permanently inaccessible can look alike when a file cannot be read. The difference matters to the business.

A team may need a prior export for an audit, restore unprocessed encrypted files after a disaster, or reopen documents created by an application version that is no longer deployed. In each case, recovery must remain under organizational control without requiring the original producer to run.

That requirement changes backup planning. Saving ciphertext alone is insufficient. For CANIS, recovery also depends on the encrypted key database **and its matching master key**. A replacement master key cannot unlock an older database. The [recovery runbook](https://github.com/metiago/canis-kms/blob/develop/docs/operational-recovery.md) calls for protected backups of the pair and a restore test that decrypts a known, existing file. Successful startup alone is not proof that old files remain recoverable.

Recovery also needs an audit story. CANIS records which credential requested a key operation and which stored key name it used. Where tamper-evident records are required, the organization's logging system must supply retention and integrity controls.

## Design Goals and Trade-offs

Several goals shaped the architecture.

**Keep file encryption close to the application.** The producer knows when it has a complete file and where that file belongs. It generates a fresh AES key, encrypts locally, and publishes only an encrypted envelope. The key service does not become a bulk file transfer path.

**Allow delayed recovery.** A consumer should be able to decrypt after the producer exits. CANIS therefore keeps the private key needed to unwrap the file key in a separately operated service.

**Separate responsibilities.** Shared storage holds encrypted files. CANIS holds recovery key material. Client applications decide when to publish or consume. This gives platform and application teams distinct components to operate, review, and govern without placing file storage and key custody in the same service.

**Define one enterprise trust domain.** CANIS is intended for participating applications that an organization has approved to share key recovery. Admission grants use of a shared key namespace; key names identify cryptographic material rather than separate authorization domains. The organization governs that access through deployment boundaries, client admission, operational controls, and policy.

**Keep deployment practical.** We used Java’s established cryptography and TLS facilities and a small client library that Java services can call. We also made recovery procedures explicit. A design is difficult to operate safely if restoring its key store requires guesswork.

We considered the trade-offs behind these choices. Copying private keys into every producer and consumer would make custody harder to control. Keeping file keys with producers would leave later consumers dependent on them. Sending whole files through the key service would expand the data it handles. CANIS instead concentrates key custody and recovery in one governed service while applications handle file data.

Why build this instead of calling an existing KMS directly? The problem represented in the [repository](https://github.com/metiago/canis-kms/tree/develop) is the complete asynchronous Java file workflow: local encryption, delayed consumption, and recovery through an organization-operated service. CANIS brings the client library, envelope format, recovery protocol, and operational procedures into one implementation. Existing KMS products can also support envelope encryption; organizations that need managed availability, hardware-backed custody, or finer key policies should evaluate services designed for those requirements. Our design focuses on systems already governed within one enterprise trust domain.

## Shared-Trust Architecture

The central design decision is to separate the producer application, consumer application, shared storage, and CANIS key service. Each application uses its own instance of the CANIS Java client. The two applications can run at different times; the diagram shows the producer finishing before the consumer begins.

{{< gallery cols="1" >}}
canis-file-exchange.jpg | The client library handles cryptography and service calls inside each Java application. The applications decide when and where to publish or retrieve the envelope. | Diagram of CANIS: a producer encrypts a file into shared storage and a later consumer recovers the key from the CANIS service
{{< /gallery >}}

The **producer application** supplies the input and output paths to the Java client. The client retrieves a public key from CANIS, generates a fresh AES file key, encrypts locally with AES-GCM, and wraps that key with RSA-OAEP. It writes a versioned `CANISHYB1` envelope containing the wrapped key, nonce, ciphertext, and authentication tag. The application publishes the completed envelope to shared storage and can then finish its job.

The **storage layer** may be a shared filesystem or another file exchange location chosen by the organization. Its job is to retain and deliver encrypted envelopes. It does not need the plaintext file or the private recovery key. Storage access controls and retention still matter, while storage operators can move files independently of the key recovery service.

The **consumer application** may start much later. It retrieves the envelope and calls the Java client's file decryption method with the same key name. The client parses the envelope and sends its wrapped file key to CANIS. The service unwraps it with its server-side private key and returns the AES file key over the authenticated, TLS-protected connection. The client decrypts locally and writes the output file. The RSA private key stays on the server; file contents do not pass through it. The [protocol guide](https://github.com/metiago/canis-kms/blob/develop/docs/canisp-protocol.md) documents the exchange and envelope format.

The **key service** generates and holds the RSA key pairs and their metadata in an encrypted database. An externally provisioned master key protects that database. The service admits clients through mutual TLS and credentials, performs private-key recovery operations, and records key operations in audit events. These are different layers of key material: the AES file key encrypts one file, an RSA key pair enables its later recovery, and the master key protects CANIS’s stored key pairs. Recovery of older envelopes depends on retaining the database and its matching master key.

This division gives each team a clear responsibility. Application teams own file publication and consumption; storage operators own file retention; key service operators protect the database, master key, and recovery procedure. Security teams can review admission and key use without treating each storage location as a key store. Retention and recovery policies still need coordination across those teams.

The shared-trust model determines how the components are governed. CANIS handles recovered file keys, and admission grants access to the shared key namespace, including decryption and key management operations across its names. Service key names are identifiers, not tenant boundaries. Organizations requiring independent administrative or authorization domains can deploy separate CANIS instances. The [threat model](https://github.com/metiago/canis-kms/blob/develop/docs/threat-model.md) describes this boundary.

## Using CANIS from Java

Java applications use [`io.canis.jpaw.client.JpawClient`](https://github.com/metiago/canis-kms/blob/develop/client/src/main/java/io/canis/jpaw/client/JpawClient.java), the public client API. It handles public-key retrieval, the `CANISHYB1` file envelope, and the connection to CANIS; application code does not need to construct CANISP commands. The `develop` checkout builds `io.canis:client` for Java 21; install that development build locally with `mvn -pl client -am install`. For Maven dependency and repository setup, see the [client package guide](https://github.com/metiago/canis-kms/blob/develop/docs/client-package.md). Its published package version differs from the `develop` build, and compatibility with the current server is not documented.

The key name in this example, `settlement-2026`, must already have an RSA key pair registered with CANIS. A setup process can create it once through the public `JpawClient.set(String)` method. Creation is a key lifecycle operation, not part of every file publication. The producer then writes an encrypted envelope to a temporary name in the shared directory and publishes the completed file:

```java
import io.canis.jpaw.client.JpawClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class SettlementProducer {
    public static void main(String[] args) throws Exception {
        Path source = Path.of(args[0]);
        Path shared = Path.of(args[1]);
        Path pending = shared.resolve("settlement.canis.part");
        Path published = shared.resolve("settlement.canis");

        try (JpawClient canis = new JpawClient()) {
            canis.encryptFile("settlement-2026", source.toFile(), pending.toFile());
        }
        Files.move(pending, published, StandardCopyOption.ATOMIC_MOVE);
    }
}
```

The application owns the shared directory and publication step; `encryptFile` writes the envelope to the path it receives. This single-exchange example assumes fresh filenames: with `ATOMIC_MOVE`, an existing destination may be replaced or cause the move to fail, depending on the filesystem. A failed encryption does not reach the move, but may leave a `.part` file to handle before retrying. The same-directory move requires a filesystem that supports atomic moves and fails if that guarantee is unavailable. Once the published file is present, the producer process can stop. A separate consumer process can run later:

```java
import io.canis.jpaw.client.JpawClient;
import java.nio.file.Path;

public final class SettlementConsumer {
    public static void main(String[] args) throws Exception {
        Path envelope = Path.of(args[0], "settlement.canis");
        Path destination = Path.of(args[1]);

        try (JpawClient canis = new JpawClient()) {
            canis.decryptFile("settlement-2026", envelope.toFile(), destination.toFile());
        }
    }
}
```

The consumer supplies the key name because the envelope stores the wrapped key and encrypted content, not that name. Each application configures its own admitted client identity. The no-argument `JpawClient` constructor reads the server host and port, CANIS username and password, and PKCS#12 client identity and truststore paths and passwords from `CANIS_*` environment variables. Operators provide those values through protected configuration; the example embeds no credentials. The [configuration guide](https://github.com/metiago/canis-kms/blob/develop/docs/client-package.md#configuration) lists the exact variable names. The [reviewer workflow test](https://github.com/metiago/canis-kms/blob/develop/server/src/test/java/io/canis/ReviewerWorkflowTest.java) exercises this producer-to-recipient sequence with temporary credentials and storage.

## Why We Chose Java

We designed CANIS for Java producers and consumers. The client library lets those applications use an existing workflow without implementing the wire protocol or file envelope. Java 21 provides familiar cryptographic, certificate, TLS, filesystem, and testing APIs.

The Java Cryptography Architecture lets us use established providers for AES-GCM and RSA-OAEP. Java’s TLS support gives the client and server certificate validation and mutual authentication without inventing a transport security layer. Maven keeps the client and server build reproducible for contributors who already work in the Java ecosystem.

Spring Boot is relevant to many teams that might use CANIS, but the CANIS server itself is **not** a Spring Boot application. That was a practical boundary: the key service can run as a focused Java process, while a Spring Boot producer or consumer can integrate the Java client into its existing configuration, scheduling, and operational setup. Framework choice does not change the recovery model.

Java also shaped operational decisions. The client helper buffers whole files, so teams should size workloads and concurrency with available memory in mind. Deleting a stored key removes it from the active store; it cannot guarantee erasure from JVM memory, snapshots, or backups. Retention and backup policies must account for those copies.

## Security Model

We designed CANIS for file exchange inside a protected environment managed by one organization. Files remain encrypted in shared storage, while approved applications share a recovery domain. The operating system, infrastructure administrators, deployment automation, service account, and corporate identity systems are part of that trusted environment.

The supported client and server path uses TLS 1.3 with client certificates. The client validates the server certificate and hostname; CANIS validates the client certificate and then requires application credentials. Enterprise operators place the service on a restricted network, provision certificates through their PKI, protect service credentials and host access, and back up the encrypted database and matching master key under separate controls. These measures establish the environment in which CANIS is intended to run.

Within that environment, file encryption protects content in shared storage; encrypted key persistence protects stored key material at rest; and TLS plus credentials govern entry to recovery. CANIS does not enforce per-key authorization: an admitted client can request operations, including decryption and deletion, under any stored key name. Certificates and usernames identify admitted clients but do not divide the namespace into tenant-specific permissions. The organization manages credential compromise through identity revocation, network controls, and incident response, and treats privileged host administrators as trusted custodians.

Two boundaries matter when integrating CANIS into a wider workflow. AES-GCM authenticates encrypted content relative to its file key; the publication process must establish producer identity when required. CANIS emits audit events for authentication and key operations but has no built-in audit-integrity verifier. An enterprise logging platform can provide retention and tamper-evidence for those records.

## Lessons Learned

**Key governance belongs in the initial design.** The encryption path is straightforward to demonstrate: generate a key, encrypt a file, and confirm that the ciphertext cannot be read directly. The longer-lived workflow requires custody decisions. Who backs up the key database? Who holds its master key? How do we know a restored pair can read an old file? We made the last question an operational test, with a known envelope that can be decrypted after restoration.

**Authentication and authorization are separate decisions.** Mutual TLS and credentials establish which client has entered the recovery domain. Within that domain, the shared namespace is an intentional policy choice: admitted applications operate under the same organizational governance rather than per-key application policies. Reviewing exchanges with multiple admitted identities reinforced the need to explain this boundary clearly. We use separate deployments when applications belong to independent authorization domains.

**Failure behavior is part of the architecture.** A new, empty database is appropriate for a new installation; it cannot recover keys for existing files. CANIS therefore requires explicit initialization for a new database. After an operational loss, operators restore the original database with its matching master key and verify a known envelope before returning to service. The procedure keeps recovery focused on preserving the key material that existing files require.

**Security controls work best with clear procedures.** Key-file checks help catch unsafe provisioning, while operators set ownership, permissions, backups, and restart validation for the managed host. The same coordination applies to rotation: a new key name can be created quickly, but retained envelopes must remain readable before an older key is retired. The procedure connects key lifecycle decisions to file retention policy.

Clear operational boundaries proved essential during testing. Whole-file buffering informs workload sizing, while logical deletion and backup retention must be planned together. Continuity depends on the organization's backup, restore, and service availability arrangements. CANIS provides an operator-controlled recovery procedure, not automatic failover.

## Future Directions

One natural extension is finer authorization for organizations that want distinct application policies inside a deployment. Per-key rules could provide those scopes while preserving the existing shared-trust mode for teams that govern admission at the deployment boundary. Such an extension would need clear migration rules and an explicit account of how it changes key recovery operations.

Audit integration can evolve with a defined event schema and dependable delivery to an organization's logging system, where retention and integrity controls can be applied. Key lifecycle automation could make rotation and retention easier to coordinate with encrypted files that still depend on older keys. Both directions extend the governance model already used by enterprise operators.

Deployment automation and observability are further opportunities. Operators benefit from signals for certificate expiry, key-file and database availability, failed recovery requests, backup health, and restore exercises. Streaming file processing could support larger workloads with a different memory profile. We would evaluate each addition against the same question that guided the original design: can an authorized consumer recover the right file, at the right time, through an operational path we can explain?

## Open Source Availability

We published CANIS so engineers can inspect the client, server, file format, trust boundary, and recovery procedures together. The [GitHub repository](https://github.com/metiago/canis-kms) includes the source, documentation, build instructions, and a runnable example. The public [v0.1.0-rc.1 release](https://github.com/metiago/canis-kms/releases/tag/v0.1.0-rc.1) is identified as a prerelease and includes a source archive; its release notes describe the validation scope and operational boundaries. The example above follows the newer `develop` tree.

We welcome issues and contributions that make the boundaries clearer or improve the client, operations, and documentation. The [contribution guide](https://github.com/metiago/canis-kms/blob/develop/CONTRIBUTING.md) explains how to report and reproduce a change.

## Conclusion

Encrypted file exchange succeeds when the intended consumer can recover the file when its turn arrives, even after the producer has finished. CANIS supports that outcome by keeping file encryption with applications, encrypted files in shared storage, and recovery keys in a separately operated service. Its shared enterprise trust domain gives the organization a defined place to govern admission, custody, and recovery. Teams can assess that model alongside their authorization policy, retention needs, and ability to operate and test the recovery path.
