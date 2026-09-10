---
title: "Reflection-Free Serialization on the JVM: Four Formats In, Avro Out"
date: "2026-09-10T06:56:00+00:00"
description: "I run a model layer that accepts the same business data in JSON, XML, YAML, and TOML and converts it all to Avro. The project was greenfield, so the…"
authors:
  - "dmitry-moskalyuk"
image: "avro-out.jpg"
categories:
  - "Data Engineering"
  - "Java"
  - "Kotlin"
related_posts:
  - "writing-a-data-orchestrator-in-java"
  - "alternatives-to-dto"
  - "avoid-java-serialization"
frozen: false
---

I run a model layer that accepts the same business data in JSON, XML, YAML, and TOML and converts it all to Avro. The project was greenfield, so the question was never what to migrate away from. It was a question of which serialization library the layer would be built with: Jackson or kotlinx.serialization. Jackson was the safe answer, with a module for every format we needed and years of production behind it. What pulled me the other way was the assumption that runtime reflection costs real CPU under load. That turned out to be true of one Jackson path and not the other, which I will get to. The reasons the choice held up were not the reasons I made it.

### Where the reflection cost comes from

Jackson is not slow by default. Our baseline was Blackbird, the module designed to take reflection out of the hot path: it generates specialized bytecode at runtime for the specific POJO classes it encounters, and that code reads and writes fields directly rather than going through reflective accessors. On a steady-state service, the per-field cost largely disappears. What stays is the runtime part. Jackson still introspects each class before it can generate anything, so the work lands on the first encounter with a type; you carry the machinery that performs it, and none of it is visible to the compiler. The other cost showed up per format. In Jackson terms, a format is a data format module bound to a Content-Type, with its own dependency, its own ObjectMapper or Factory, and a hand-written registration in the chain that maps the media type to a parser and a response writer. The underlying streaming and databind API is shared, so this is not four separate parsers, but the wiring around each module is still written and maintained by hand, and every new format adds another entry to that chain.

One model, generated at build time kotlinx.serialization moves the work to compile time. Its compiler plugin generates the serialization routines during the build, so there is no reflective lookup at runtime and no first-message warmup. The model classes stay plain Kotlin data classes carrying a single annotation, and using them is the same call regardless of where the bytes are going.

```
@Serializable
data class UserProfile(
    val id: String,
    val friendIds: List = emptyList(),
    val nickname: String? = null,
    val fullName: FullName,
)

@Serializable
data class FullName(
    val firstName: String,
    val lastName: String,
)
val model = PaymentInstruction(
    id = "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    postCode = 101000,
    fullName = FullName("Ada", "Lovelace"),
    nickname = "ada",
)
// encode: same model, different format module
val json = Json.encodeToString(model)
val yaml = Yaml.default.encodeToString(model)
val toml = Toml.encodeToString(model)
// decode: same model again
val fromJson = Json.decodeFromString(json)
val fromYaml = Yaml.default.decodeFromString(yaml)
val fromToml = Toml.decodeFromString(toml)
```

Nothing in the model is specific to a wire format. The collection type, the nullable field, the default value, and the nested class are all ordinary Kotlin, and the generated serializer carries them through end to end: a List stays a list of strings on the way in and on the way out, and FullName is resolved as a nested record instead of a map of untyped values. JSON, YAML, and TOML are separate objects from separate dependencies that implement the same StringFormat contract, and that is the full extent of what changes between formats at the call site. One thing worth being precise about: kotlinx.serialization is not a set of format implementations. The core library ships JSON, CBOR, and Protobuf; everything else is a format module built on top of the same SerialFormat contract, either by the community or by you. XML, YAML, and TOML came from third-party modules, which we used as-is rather than forking or writing a custom serializer only where behavior had to differ, such as key ordering in the YAML the orchestrator emits.

### Avro, the part we built by hand

Avro is where the problem's shape changes. The other formats are inputs. Avro is what everything becomes: whatever a message looked like when it arrived, it leaves the layer as an Avro record written against a schema, and every downstream consumer sees that one representation. The input formats are an accident of who is sending us data. The Avro output is the contract we hold with everyone who reads it. So we did not use Avro4k, and we did not write a kotlinx format module for Avro either. Both would have put Avro on the same footing as the input formats, as one more SerialFormat sitting next to Json and Yaml, which is the wrong shape for a destination. What we wrote instead is a schema-driven KSerializer, split into an AvroSchemaDeserializer and a GenericRecordSerializer. One serializer, driven by the Avro schema, decodes the input using whichever format module it arrived in and produces a GenericRecord at the other end. The part that had to be written by hand was the defaults. A default in an .avsc schema is not a Kotlin default value on a data class, and no format module knows about it. The module reads the input it was given, and if a field is absent, it has nothing to say, because as far as it is concerned, nothing was there. The schema is the only place that knows the field has a declared default, so we apply those defaults ourselves, before decoding rather than after.

```
override fun deserialize(decoder: Decoder): GenericRecord {
    val record = GenericData.Record(avroSchema)
    val compositeDecoder = decoder.beginStructure(descriptor)
    // Track all required (non-nullable) fields: they must be provided either by input or by schema default.
    val isProcessedKey = record.schema.fields
        .filterNot { it.schema().isNullable }
        .associateWith { false }
        .toMutableMap()
    // Avro defaults applied by hand, before the payload is read.
    // The format module knows nothing about "default" in the .avsc
    record.schema.fields.forEach { field ->
        field.defaultVal()?.let { defaultValue ->
            isProcessedKey[field] = true
            record.put(
                field.name(),
                defaultValue.takeUnless { it == JsonProperties.NULL_VALUE }
            )
        }
    }
    while (true) {
        val index = compositeDecoder.decodeElementIndex(descriptor)
        if (index == CompositeDecoder.DECODE_DONE) break
        val field = fields[index]
        val value = compositeDecoder.decodeSerializableElement(
            descriptor,
            index,
            DeserializerCache.getDeserializer(field.schema())
        )
        record.put(field.name(), value)
        isProcessedKey[field] = true
    }
    compositeDecoder.endStructure(descriptor)
    // whatever is left is a required field with no default and no value in the input
    isProcessedKey.filterNot { it.value }.keys.forEach { missing ->
        error("Field $missing was expected but not provided")
    }
    return record
}
```

The record is pre-populated from Schema.Field.defaultVal(): each field filled this way is marked as handled, and then the input is decoded on top, overwriting whatever it carries. What survives to the bottom is the set of fields that are required, have no schema default, and were not in the input, and that deserve to fail rather than produce a record with a hole. The JsonProperties.NULL_VALUE check is Avro trivia that costs an afternoon if you miss it, since Avro represents a declared null default with a sentinel object rather than an actual null. We wrote this once. Every schema since then gets default handling for free, and adding an input format is nearly trivial: the format module handles the syntax, the same serializer handles the schema, and the Avro side stays untouched.

### The heartbreak

I have been describing a layer in which every input format is a kotlinx format module that feeds into a single serializer. That is true of XML, YAML, and TOML. It is not true of JSON, the format we receive most, which runs on Jackson streaming. The reason is a single missing feature. Some senders include fields we do not model but must not discard, because the message passes through us, and whatever we did not recognize has to come out the other side intact. At the time we wrote this, kotlinx.Serialization could ignore unknown fields or fail on them, but it could not hand them to you mid-parse. Jackson streaming API could.

```
while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
    jsonParser.validateCurrentToken(JsonToken.FIELD_NAME)
    val fieldName = jsonParser.currentName()
    val fieldWithParser = fieldMapping[fieldName]

    if (fieldWithParser == null) {
        when (mode) {
            ParsingMode.STRICT -> {
                listener.withField(fieldName) {
                    listener.newError("Unknown field ")
                }
                jsonParser.skipNext()
            }
            ParsingMode.IGNORE_UNKNOWN_FIELDS -> {
                jsonParser.skipNext()
            }
            ParsingMode.KEEP_UNKNOWN_FIELDS -> {
                listener.processAdditionalField(fieldName, jsonParser)
            }
        }
        continue
    }
    // ...
}
```

The third branch is what kotlinx could not express: the listener receives the live JSON parser rather than a materialized value, which lets it retain the field before the decoder has committed to a shape. The feature is yet to be landed in kotlinx.serialization, in this pull request. None of this reflects badly on the library, though it is worth saying what it costs. The architecture I described serves most of the layers, not the format that carries the most traffic, which has its own parsing path, dependencies, and behaviors to keep in sync. That is the per-format wiring we chose to avoid with kotlinx. Compile-time codegen is better than runtime reflection, and I would choose it again, but you enjoy architectural cleanliness only where the API surface covers your requirements. Jackson has had twenty years to accumulate escape hatches for cases like ours. Check the feature list against your real requirements before you commit.

### What changed, measured

I wrote a benchmark rather than guess, and the results were less flattering to my decision than I expected. Jackson with databind was slower than kotlinx.serialization, as expected, since databind is where reflection does most of the work. Jackson streaming came out level with kotlinx.serialization on throughput, near enough that I would not claim a winner. Streaming skips databind, so there is little reflective work left to remove, and the compile-time advantage has nothing to bite on. What we did get is not on the benchmark. There is less wiring because a format is a format object rather than a mapper, a dependency, and a registration in a media type chain. There is less assembly at runtime, because the serializer for a class sits in the build output, where you can look at it. And the checks moved to the compiler, so a field that does not typecheck against the model fails the build instead of surfacing as a decoding error on a message in production. Whether that trade is worth making depends on how much of your pain is due to CPU and how much is due to wiring. Ours was mostly wiring.

### When I would not bother

The approach pays off in proportion to the number of @Serializable classes you have. One model class and one format do not amount to a serialization problem, and adding a compiler plugin to a build for it is effort spent on nothing. The wiring savings exist only if there is wiring to save, and on a small service with a single Content-Type and no load to speak of, Jackson with databind will serve you for years. If you are already writing against a streaming API and throughput is what you are optimizing for, the benchmark indicates that the compile-time advantage is largely not there. The place I would reach for without hesitation is Kotlin Multiplatform, which this article has not touched on at all. Reflection is a JVM luxury and is not available on JS or Wasm targets, while a compile-time serializer produces the same behavior across every target you compile to. That gives you a shared contract that behaves consistently everywhere, instead of one reimplemented on each platform.

### Takeaway

The question I set out to answer was where the model layer pays its serialization cost, at build time or at runtime. Against a streaming parser, it barely pays either way, and what justified moving the model to one annotated class and one schema-driven serializer was everything around the parsing: fewer dependencies to wire, less behavior assembled at runtime, and a compiler that catches shape errors before a message arrives. That was worth the Avro work it took to get there, and worth keeping Jackson for the one case kotlinx could not cover.
