---
title: "Streaming Large File Uploads in Java Without Killing Your Server"
description: "Spring's @RequestPart cannot stream multipart uploads — not even FilePart. The API that actually does, and the tests that prove it."
authors:
  - "denis111"
image: "cover.png"
categories:
  - "Java"
  - "Spring"
  - "Streaming"
  - "Performance"
canonical: "https://denis111.hashnode.dev/streaming-large-file-uploads"
---
## Introduction

You've seen the tutorial. A `@PostMapping` endpoint, a `MultipartFile` parameter, a call to `transferTo()`. Done in five minutes. Works great on localhost.

Then your first 500MB upload hits production and your pod crashes with an `OutOfMemoryError`.

The problem isn't your code — it's an assumption baked into most tutorials: that files are small enough to buffer entirely in memory or on disk before your handler even runs. In the real world — media platforms, document management, healthcare, fintech — that assumption breaks fast.

And the WebFlux version of the tutorial is often no better. It just moves the buffering somewhere less obvious.

This is Part 1 of a short series on building a production-grade upload pipeline in Java. Here we'll cover:

- Why `MultipartFile` buffers the whole file before you get a chance to touch it
- Why `@RequestPart` in WebFlux does *exactly the same thing*, including the version everyone copies
- The one WebFlux API that genuinely streams, and what it takes to use it correctly

**Part 2** picks up from here: storing that stream to S3, detecting file type from a handful of bytes instead of the whole file, and running antivirus scanning safely at scale — including a failure mode worth knowing about before you put an upload endpoint in front of the public internet (a harmless-looking file that takes the whole server down with it). It also does all of this a second time on the servlet stack, for the many teams who can't move to WebFlux — so if you're on Spring MVC, stay with me.

---

## Why `MultipartFile` Is a Trap

Spring's `MultipartFile` (and its equivalents) seem convenient, but under the hood the servlet container buffers the entire request body — either in memory or in a temp file — before your controller method is even called.

For a 2GB video upload, this means:
- 2GB of heap or disk consumed per concurrent upload
- No streaming to storage until the full file is received
- Zero opportunity for your own checks to run until you've paid the full I/O cost

Be precise about which of the two you get, because they fail differently:

- **`file-size-threshold: 0` (the Tomcat default)** spools the whole body to a temp file. Ten concurrent 2GB uploads is 20GB of temp disk you didn't plan for, and nothing reaches your storage until the last byte lands.
- **A threshold above the file size** keeps it in the heap instead. That's the version that kills the JVM — and every other in-flight request with it.

One honest caveat before we move on: the container *can* reject early on its own limits — Tomcat enforces `max-file-size` during parsing. What you lose is not all rejection, it's *your* rejection: no quota check, no content-type policy, no persistence until the last byte has been spooled. Part 2 is about those checks, done cheaply.

---

## The Big Picture: Where This Series Is Headed

Before diving into WebFlux specifics, here's the shape of the full pipeline we're building across this series — so the WebFlux piece below makes sense in context:

```
1. Accept the upload as a stream — never buffer the whole thing (this post)
2. Peek at the first ~500 bytes to check the file type, then rewind (Part 2)
3. If the type is rejected, bail out immediately — no storage, no AV work wasted
4. If accepted, fan out the single stream to:
     - storage backend (S3 or disk) — unbounded, we must persist the whole file
     - antivirus scan — bounded, capped at a sane max so a huge file can't choke it
```

The common thread through every step: read the incoming bytes from the network **exactly once**, and never require the whole file to be sitting in memory or on disk before you can act on it. Everything in this post is about step 1 — getting a real stream out of the incoming request in the first place. That's the foundation Part 2 builds storage and analysis on top of.

---

## The Part Most WebFlux Tutorials Get Wrong

Here is the version you've probably seen, and it does not stream:

```java
@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public Mono<UploadResult> upload(
        @RequestPart("file") Flux<DataBuffer> fileParts,
        @RequestPart("metadata") UploadMetadata metadata) {

    return uploadService.streamToStorage(fileParts, metadata);
}
```

It looks right. A `Flux<DataBuffer>` is a reactive stream of chunks, and the type says "stream", so surely it streams. It doesn't, and Spring says so in its own documentation:

> To parse multipart data in streaming fashion, you can use the `Flux<PartEvent>` returned from the `PartEventHttpMessageReader` **instead of using `@RequestPart`, as that implies `Map`-like access to individual parts by name and, hence, requires parsing multipart data in full.**

That's the whole trick, stated plainly: `@RequestPart` means "look this part up by name", and you cannot look parts up by name in a body you haven't finished reading. So Spring reads the whole thing first.

What that means for each signature you might reach for:

| Signature | What actually happens |
|---|---|
| `@RequestPart("file") MultipartFile` | Servlet stack: whole body buffered before your handler runs |
| `@RequestPart("file") FilePart` | WebFlux: held in memory up to `max-in-memory-size` (256KB), then written to a temp file — all before your handler runs |
| `@RequestPart("file") Flux<DataBuffer>` | WebFlux: joined into **one** buffer, capped by `max-in-memory-size` (256KB by default) |
| `@RequestBody Flux<PartEvent>` | **Actually streams** |

Three of those four deserve a closer look, because each fails in a different way.

**`FilePart` is a temp-file-backed `MultipartFile`.** This is the one that catches people, because `FilePart` looks like the modern, reactive answer. It isn't: by the time your handler is called, the part has been fully received — held in memory if it is under `maxInMemorySize`, otherwise written to a `.multipart` temp file on disk. Spring Boot's own configuration reference admits it, describing the temp-file setting as *"Directory used to store file parts larger than `maxInMemorySize`. **Ignored when using the PartEvent streaming support**"* — a setting that exists only because `@RequestPart` writes parts to disk before you see them.

**The `Flux<DataBuffer>` version fails loudly, and confusingly.** There's no reactive adapter for a bare `DataBuffer`, so the decoder falls back to its single-value path and joins your "stream" into one composite buffer bounded by `spring.http.codecs.max-in-memory-size`. With the default 256KB, a 1MB upload comes back as **HTTP 413** — which nobody expects from an endpoint with no size limit configured. Raise that limit to "fix" it and you have bought yourself a full in-heap copy of every upload: the OOM, with extra steps.

**And `DataBufferUtils.join()` doesn't help either.** The usual "pragmatic bridge" is:

```java
InputStream inputStream = DataBufferUtils.join(fileParts)
    .map(buf -> buf.asInputStream(true))
    .block();
```

Two problems. `join()` aggregates *every* buffer into one composite buffer, so memory grows with file size — which is the exact thing we're trying to avoid. And `.block()` runs on the Netty event-loop thread the handler was invoked on, so it never gets that far:

```
java.lang.IllegalStateException: block()/blockFirst()/blockLast() are blocking,
which is not supported in thread reactor-http-nio-4
```

That's not a tuning problem. It's a "this code cannot run" problem.

---

## Spring Boot: WebFlux Reactive Multipart, Done Properly

The API that streams is `@RequestBody Flux<PartEvent>`: instead of a map of named parts, you get a flat stream of events as the body is parsed. A form field produces one `FormPartEvent`; a file produces one or more `FilePartEvent`s, each carrying a buffer. The final event of each part has `isLast()` set, which is what lets you split the stream back into parts without buffering it.

```java
@RestController
public class UploadController {

    private final PartEventUploadService uploads;

    @PostMapping(value = "/api/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<UploadResult>> upload(@RequestBody Flux<PartEvent> events) {
        return uploads.store(events)
                .map(result -> ResponseEntity.status(HttpStatus.CREATED).body(result));
    }
}
```

The service handles one part at a time, in order:

```java
public Mono<UploadResult> store(Flux<PartEvent> events) {
    AtomicReference<UploadMetadata> metadata = new AtomicReference<>();

    return events
            .windowUntil(PartEvent::isLast)                    // one window per part
            .concatMap(window -> handlePart(window, metadata)) // strictly sequential
            .next()
            .switchIfEmpty(Mono.error(new InvalidUploadException("No 'file' part in the request")));
}
```

Two details in there are load-bearing.

`windowUntil(PartEvent::isLast)` splits the flat event stream into per-part windows; `concatMap` then processes them one at a time, so there is nowhere for the body to pile up. And because it is a single pass, **part order matters**: `metadata` has to be sent before `file`, because there is no second pass in which to look it up. That's a real constraint, not a stylistic choice — and it's the price of not buffering.

The other detail is the one that leaks memory if you get it wrong. `FilePartEvent`'s content buffers must be consumed, relayed, or released — every one of them — and the framework will not do it for you.

### Streaming to storage

The storage backend takes the file part's buffers and writes them as they arrive:

```java
@Override
public Mono<StoredUpload> store(Flux<DataBuffer> content, StorageSpec spec) {
    Path tempFile = root.resolve(key + ".part");
    Path targetFile = root.resolve(key);
    AtomicLong written = new AtomicLong();
    MessageDigest digest = sha256();

    Flux<DataBuffer> metered = content
            .doOnDiscard(DataBuffer.class, DataBufferUtils::release)
            .doOnNext(buffer -> {
                long total = written.addAndGet(buffer.readableByteCount());
                if (total > maxBytes) {
                    DataBufferUtils.release(buffer);          // we are aborting: this one is ours to free
                    throw new UploadTooLargeException(maxBytes, total);
                }
                digest.update(buffer.asByteBuffer().duplicate());
            })
            .map(FileSystemStorageBackend::toHeapBuffer);

    return Mono.usingWhen(
            openChannel(tempFile),
            channel -> DataBufferUtils.write(metered, channel)
                    // write() re-emits each buffer once it is on disk: releasing is our job
                    .doOnNext(DataBufferUtils::release)
                    .doOnDiscard(DataBuffer.class, DataBufferUtils::release)
                    .then(Mono.fromCallable(() -> commit(channel, tempFile, targetFile, digest, written))),
            channel -> closeQuietly(channel),
            (channel, cause) -> closeQuietly(channel).then(deleteQuietly(tempFile)).then(Mono.error(cause)),
            channel -> closeQuietly(channel).then(deleteQuietly(tempFile)));
}
```

Four things there are worth naming, because each one is a bug in the naive version:

- **`DataBufferUtils.write` does not release the buffers it writes — it re-emits them.** Whoever subscribes has to release them. Skip that line and pooled off-heap memory leaks steadily across uploads until the process dies. This is the single most common defect in hand-rolled streaming code.
- **Bytes already written stay on disk when the stream fails.** The partial file is deleted on error *and* on cancellation (client disconnect), and the real file only ever appears via an atomic rename — so a half-written upload is never visible under a key a caller was given.
- **The size ceiling is enforced mid-stream**, which is why an oversized upload is rejected early rather than after the full transfer. The trade-off is honest and worth knowing: when you abort mid-upload, the client may see the connection drop before your error body arrives. Reject cheap things before the body starts; drain before erroring if a caller needs a clean response.
- **Each chunk is copied to the heap before writing**, because handing Netty's pooled *direct* buffers straight to a file channel isn't portable. On Windows with a current JDK it fails inside the JDK with `UnsupportedOperationException: ByteBuffer derived from closeable shared sessions not supported` — the channel wants a stable address the buffer doesn't have. One memcpy per chunk, and it works everywhere.

Backpressure comes for free and is worth understanding: `DataBufferUtils.write` requests the next buffer only when the previous write completes, and that demand propagates back through the parser to the socket. A slow disk slows the client down instead of filling your heap.

### The curl shape

Because parts are processed in order, and because a JSON part with an explicit content type is not a "form field" as far as Spring is concerned, the request looks like this:

```bash
curl -X POST http://localhost:8080/api/upload \
  -F 'metadata={"filename":"big.bin","contentType":"application/octet-stream"}' \
  -F 'file=@big.bin'
```

Metadata as a plain field, first. File second. The endpoint in the companion repo accepts the typed form too, but the plain field is what you want.

---

## Does It Actually Stream? Measuring Instead of Asserting

Claims like "memory is driven by chunk size, not file size" deserve evidence, so the companion repository ships two tests that I'd want to see before believing any of this.

**Bytes hit the disk before the upload finishes.** The test client sends one chunk and then *refuses to send the rest* until it observes a non-empty `.part` file on the server. A buffering implementation cannot pass this test — it would never write anything before the body completed, and the client would wait forever, so the test fails by timing out rather than by passing for the wrong reason. On the streamed path it observes ~59KB on disk while the remaining 4MB is still unsent.

**A file several times larger than the heap.** The application runs as a separate process with a hard `-Xmx256m`, and the test uploads 512MB generated on the fly — no giant temp file anywhere — then verifies the SHA-256 of what landed on disk:

| Endpoint | Payload at a 256MB heap | Result |
|---|---|---|
| `POST /api/upload` (`Flux<PartEvent>`) | 512MB | **201**, digest matches, no OOM |
| the `join()` bridge from earlier | 512MB | **OutOfMemoryError**, process exits |

Same bytes, same heap, only the endpoint changes. That's the whole argument in one table.

### Two things I got wrong first

Both were found by measuring, and both are in the repository's history rather than quietly fixed:

**Chunk size, not write batching, is the throughput knob.** Reactor Netty's HTTP decoder hands the application 8KB at a time by default, so a 512MB upload arrives as ~65,000 separate part events. Raising the decoder's `maxChunkSize` to 256KB cut that to ~8,200 events and took the same upload from ~29s to ~14.5s — with no change to the memory profile at all, since buffers are still consumed one at a time. (For scale: raw disk write throughput on the same machine is ~81MB/s, so at that point you're bound by disk and hashing, not by the streaming design.)

**A cleverer batching operator was also incorrect.** Gathering several chunks per write changed the timing by under 5%, and it turned out to be unsafe: `bufferTimeout` emits on a timer independent of downstream demand, while the writer requests exactly one write at a time. Under load it produced `OverflowException: Could not emit buffer due to lack of requests`. The test suite missed it because it's timing-dependent; a manual curl loop caught it. It was removed, and the remaining path is strictly demand-driven — which is also what makes the backpressure real.

---

## A Note on Limits

If you take one configuration lesson from this post, take this one: in WebFlux, `max-in-memory-size` has nothing to do with how large an upload you can accept.

- `spring.webflux.multipart.max-in-memory-size` — for `@RequestPart`, the threshold at which a part spills to disk. For `PartEvent`, it bounds *form fields* only. It does not bound the file.
- `spring.webflux.multipart.max-disk-usage-per-part` — this is the real ceiling for a streamed part, enforced while the bytes are still arriving.
- `spring.http.codecs.max-in-memory-size` — bounds aggregated decodes, including the `@RequestPart Flux<DataBuffer>` case above.
- Your own limit, checked as bytes flow past — which is what lets you return a useful error instead of a generic framework rejection.

Also worth knowing if you're on Boot 4: `spring.codec.max-in-memory-size` no longer exists. It's `spring.http.codecs.max-in-memory-size` now, and every tutorial using the old name is silently configuring nothing.

---

## What's Next

Next comes the servlet-stack version of everything above — `request.getInputStream()`, keeping the multipart resolver away from your upload, bounded buffers, and the thread-pool policy that stops a fast client from exhausting a slow disk. If you're on Spring MVC and you've been reading this thinking "none of this applies to my application", that one's for you: you don't have to move to WebFlux to stop buffering uploads.

After that, the pipeline those uploads feed into:

- **Storage**: streaming the same bytes to S3 via multipart upload, still without buffering — using `AsyncRequestBody.fromPublisher()` rather than dropping back to an `InputStream` bridge
- **Type detection**: reading ~500 bytes instead of the whole file to decide whether you accept it at all, and why running Apache Tika as an embedded library is a liability against hostile input (and how `tika-server` fixes it)
- **Antivirus**: bounding ClamAV scans so a 100GB upload can't choke the scanner, and what to do about the files that are too big to scan in full

---

## Conclusion

Naive file uploads work fine until file size or concurrency grows past what buffering can absorb. The WebFlux version of the naive approach is sneakier, because `@RequestPart` and `FilePart` both *look* like streaming APIs while quietly parsing the whole body first — a `FilePart` is a temp-file-backed `MultipartFile` wearing reactive clothes. The API that actually streams is `@RequestBody Flux<PartEvent>`, and it asks something of you in return: handle parts in order, release every buffer you touch, clean up after yourself when a transfer dies halfway, and enforce your own limits while the bytes are still moving.

Get that right and a 512MB upload costs the same heap as a 5MB one — which is the foundation everything else in this series builds on.

The full source code, including the tests that pin every claim in this post, is on GitHub: **[denis111/spring-boot-large-file-upload](https://github.com/denis111/spring-boot-large-file-upload)**

---

*Found this useful? Next up: the same pattern implemented on the servlet stack, for those who can't move to WebFlux — followed by streaming storage to S3, tika-server, and bounded antivirus scanning.*
