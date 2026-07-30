---
title: "Indexing all of Wikipedia, on a laptop"
slug: "indexing-all-of-wikipedia-on-a-laptop"
date: "2024-05-29T15:54:32+00:00"
lastmod: "2024-12-30T10:55:24+00:00"
description: "Indexing the entirety of English Wikipedia on a laptop has become a practical reality thanks to recent advances in the JVector library that will be part of the imminent 3.0 release. - by Jonathan Ellis"
authors:
  - "jbellis"
image: "/images/posts/2024/05/indexing-all-of-wikipedia-on-a-laptop/wikiindex.png"
categories:
  - "DataStax"
  - "Performance"
  - "Tools"
tags:
related_posts:
  - "jvector-1-0"
  - "spring-ai-how-to-write-genai-applications-with-java"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "how-is-leyden-improving-java-performance-part-3-of-3"
frozen: false
---

<br />

<br />

In November, [Cohere released a dataset containing all of Wikipedia](https://huggingface.co/datasets/Cohere/wikipedia-2023-11-embed-multilingual-v3), chunked and embedded to vectors with [their multilingual-v3 model](https://cohere.com/blog/introducing-embed-v3).

Computing this many embeddings yourself would cost in the neighborhood of $5000, so the public release of this dataset makes creating [a semantic, vector-based index](https://www.datastax.com/guides/what-is-vector-search) of Wikipedia practical for an individual for the first time.

Here's what we're building:
![](https://lh7-us.googleusercontent.com/ydeHYk97v6Bza1GF0wbbHUEzxgCAJLfwbRcVnWvUP6QDPKKY5YQH00Dvi2n6VgkioW_PGqwckcCnQu9cJ2nOz2XSuL_27HNPAAbZdv2vXPOy_vUJ_Vcg-ii83E4jaqMycskzmzt8wBP1XsOYh5b7Cv4)

You can try searching the completed index [on a public demo instance here](https://jvectordemo.com:8443/).

Sure, the dataset is big (180GB for the English corpus), but that's not the obstacle per se. We've been able to build full-text indexes on larger datasets for a long time.

The obstacle is that until now, off-the-shelf vector databases could not index a dataset larger than memory, because both the full-resolution vectors and the index (edge list) needed to be kept in memory during index construction. Larger datasets could be split into [segments](https://stackoverflow.com/questions/2703432/what-are-segments-in-lucene), but this means that at query time they need to search each segment separately, then combine the results, turning an O(log N) search per segment into O(N) overall. (In their latest release, [Lucene attempts to mitigate this by processing segments in parallel with multiple threads](https://www.elastic.co/search-labs/blog/elasticsearch-lucene-vector-database-gains), but obviously (1) this only gives you a constant factor of improvement before you run out of CPU cores and (2) this does not improve throughput.)

Specifically, if you're indexing 1536-dimension vectors (the size of ada002 or openai-v3-small), then you can fit about 5M vectors and their associated edge lists in a 32GB index construction RAM budget.
![](https://lh7-us.googleusercontent.com/-BwVEUQqMIDekxlKXgiuOiQcycoM_fP3ncRjVNgRD7W7SzcsTigI4tjsmE-S4x35PIgEpwNxVioZxD50ah2PzQXuVCo22TXiI80EFpjpnCf4X-JjTPBb6FqVC4CJFdrYkoG6aLYxFNotM_MX_NoIpDk)

[JVector](https://github.com/jbellis/jvector/), the library that powers [DataStax Astra](https://www.datastax.com/products/datastax-astra) vector search, now supports indexing larger-than-memory datasets by performing construction-related searches with compressed vectors. This means that the edge lists need to fit in memory, but the uncompressed vectors do not, which gives us enough headroom to index Wikipedia-en on a laptop.

1. Linux or MacOS. It will not work on Windows because ChronicleMap, which we are going to use for the non-vector data, is limited to a 4GB size there. (If you are interested enough, you could shard the Map by vector id to keep each shard under 4GB and still have O(1) lookup times.)
2. About 180GB of free space for the dataset, and 90GB for the completed index.
3. Enough RAM to run a JVM with 36GB of heap space during construction (\~28GB for the index, 8GB for GC headroom).
4. Disable swap before building the index. Linux will aggressively try to cache the index being constructed to the point of swapping out parts of the JVM heap, which is obviously counterproductive. In my test, building with swap enabled was almost twice as slow as with it off.

<!-- -->

1. Check out the project:  
   $ git clone <https://github.com/jbellis/coherepedia-jvector>$ cd coherepedia-jvector
2. Edit *config.properties* to set the locations for the dataset and the index.
3. Run *pip install datasets* . (Setting up a [venv](https://docs.python.org/3/library/venv.html) or conda environment first is recommended but not strictly necessary.)
4. Run *python download.py.*This downloads the 180 GB dataset to the location you configured. For me that took about half an hour.
5. Run *./mvnw compile exec:exec@buildindex.* This took about 5 and a half hours on my machine (with an i9-12900 CPU).
6. Run *./mvnw compile exec:exec@serve* and open a browser to [http://localhost:4567](http://localhost:4567/). Search away!

We're using [JVector](https://github.com/jbellis/jvector) for the vector index and [Chronicle Map](https://github.com/OpenHFT/Chronicle-Map) for the article data. There are [several](https://github.com/OpenHFT/Chronicle-Map/issues/533) [things](https://github.com/OpenHFT/Chronicle-Map/issues/537) I don't love about Chronicle Map, but nothing else touches it for simple disk-based key/value performance.

The full source of the index construction class is [here](https://github.com/jbellis/coherepedia-jvector/blob/master/src/main/java/io/github/jbellis/BuildIndex.java). I'll explain it next in pieces.

Compression parameters {#h2-0-compression-parameters}
-----------------------------------------------------

JVector is based on the [DiskANN](https://www.microsoft.com/en-us/research/publication/diskann-fast-accurate-billion-point-nearest-neighbor-search-on-a-single-node/) vector index design, which performs an initial search using vectors compressed lossily with [product quantization (PQ)](https://towardsdatascience.com/similarity-search-product-quantization-b2a1a6397701) in memory, then reranks the results using high-resolution vectors from disk. However, while DiskANN stores full, uncompressed vectors to perform reranking, JVector is able to improve on that using [Locally-Adaptive Quantization (LVQ)](https://arxiv.org/abs/2402.02044) compression.

To set this up, we'll first load some vectors into a RandomAccessVectorValues (RAVV) instance. RAVV is a JVector interface for a vector container; it could be List or Map based, in-memory or on-disk. In this case we'll use a simple List-backed RAVV. We'll compute the parameters for both compressions (kmeans clustering for PQ, global mean for LVQ) from a single shard of the dataset. At about 110k rows, this is enough data to have a statistically valid sample.
![](https://lh7-us.googleusercontent.com/aN200b1SULDTwrg5inwDuNFKLCVyYstVuYOSXLqAos2D_psAoMp8V5CXjXDKCEKcCZc5JyM7U27qg7LPp14mfQh9nktRzXaXE4pteHFINO-HPS_xxW4ESxf1glxanb5gG2xoAmx1r2qaiReZXcFI--4)

Next, we compute the PQ compression codebook; we're compressing the vectors by a factor of 64, because the Cohere v3 embeddings can be PQ-compressed that much without losing accuracy, after reranking. [Binary Quantization only gives us 32x compression and is less accurate](https://thenewstack.io/why-vector-size-matters/).
![](https://lh7-us.googleusercontent.com/PS0HlbtZNajjlTe9AFg1yoW7fvGyKeSpHGwfk3_k5dHs08QOkTphXeO03AO2Chx-mxw5lV2wD81xo3lNGB9raJojFYrg6z2-OTIA05fUfVHzpGIM12R-veeTPLirOhjGTvcM-Uch31c5SZmgGDbiIrM)

Finally, we need to set up LVQ. LVQ gives us 4x compression while losing no measurable accuracy over the full uncompressed vectors, resulting in both a smaller footprint on disk and faster searches. (I thank the vector search team at Intel Research for pointing this out to us.)
![](https://lh7-us.googleusercontent.com/XheHrYXEE6j_GaROcmgI_0-OFJx9GJes1uVcEGDcYFUvi0Gu3ZqXgpqV38iMbxL25JvCmIcFRsxG8EoqZ2aT332JWYAwSeRHnKPzY-un5LO2eun1Eio0ZTya312IXv_AV1xJ88HUT6Fxb96uNtFokGU)

GraphIndexBuilder {#h2-1-graphindexbuilder}
-------------------------------------------

Next, we need to instantiate and configure our GraphIndexBuilder.
![](https://lh7-us.googleusercontent.com/veV6oVgpkDyr-WLPIMzzTtHD0q8MIT3sQxOauqdXwzXExFBQ2FD9btPpVXf-DTuk0OEJAVWpHf6IduBDIiyGSyDwdsEICTyoTjUocG7PgkxIRiMIpIRPpGjiSFoKm9Z-B0vOU4uYRtPsew1Oi3f_bis)

This instantiates a JVector GraphIndexBuilder and connects it to an OnDiskGraphIndexWriter, and tells it to use the PQ-compressed vectors list (which starts empty and will grow as we add vectors to the index) during construction (in the BuildScoreProvider).

Chronicle Map and RowData {#h2-2-chronicle-map-and-rowdata}
-----------------------------------------------------------

We'll store article contents in RowData records. This content is what has been encoded as the corresponding vector in the dataset, and is what we want to return to the user in our search results.
![](https://lh7-us.googleusercontent.com/veVvO8QUrY_k_YGDwavo_dBaIoM5ZGGfaN5dowCroJAgJv-37JZIWq0jX78rY0R8g6wvRO1QxvTv-dMuEVMJRvmrvdbmLAHlBJqUd9yoyIXD0DADDlZQXyZcyLPcp-F4zAcRb1obXtvJO6d4oXTGD7M)

To turn the vector index's search results (a list of integer vector ids) into RowData, we store the RowData in a Map keyed by the vector id. This will be a lot of data, so we use [ChronicleMap](https://github.com/OpenHFT/Chronicle-Map) to store this on disk with a minimal in-memory footprint.
![](https://lh7-us.googleusercontent.com/hFMjxcQstglWY0IjbgKqiHB9dk7KlKATQnBIBLZh_hGvdsuo6_UDQi8ydn3RA0ELYpJlng0HERqxUG1nmpj5HRNFPRhIHhhOtnC6vc7XHsIZnwI-fcyRK8gNnPeKpLUUQNVjGnK9EP1RHU6UPs0SLsw)

We need to tell ChronicleMap how large it's going to be, both in entry count and entry size. Undersizing these will cause it to crash ([my primary complaint](https://github.com/OpenHFT/Chronicle-Map/issues/533) about ChronicleMap), so we deliberately use a high estimate.

We *do not* need to explicitly tell ChronicleMap how to read and write RowData objects, instead we just have RowData implement Serializable. While ChronicleMap supports custom de/serialize code, it's perfectly happy to use simple out-of-the-box serialization and since profiling shows that's not a bottleneck for us we'll just leave it at that.

Ingesting the data {#h2-3-ingesting-the-data}
---------------------------------------------

We use Java's parallel Streams to process the shards in parallel. For each row in each shard, we

1. Add it to *pqVectorsList*
2. Call *writer.writeInline* to add the LVQ-compressed vector to disk
3. Call *builder.addGraphNode*-- order is important because both (1) and (2) are used when we call addGraphNode
4. Call *contentMap.put* with the article chunk data.

![](https://lh7-us.googleusercontent.com/5souUR9e_gbEEdcUwWvq8_cjleyFGglaQaCSV-XFkv-3Ij7cGYgd13UcyGdwIYE6Xw5zD4WiFSxGO1phrEK8w6UWx6BanZVWXQ4oBnkHdh6aEFB4DIllhK15HjZJ9iJyQKV5ts9QTLQqF3uufChXdPE) ![](https://lh7-us.googleusercontent.com/_Z7fqvsbvQ2kXJY286iM-ysvQJCHwlaWgmBACBFZfSOscrSbGtYMkGOlKbA5cWuaB4-M_aN1Y6idM1pWEEvUvQJh-22d71eAxAR5hxZNwBy1dedc1DIApiKTfSpLQMsIIzN3yozbSPProh5TJT_TkvU)

You can look at [the full source](https://github.com/jbellis/coherepedia-jvector/blob/master/src/main/java/io/github/jbellis/BuildIndex.java) if you're curious about *forEachRow*, it's just standard "pull data out of Arrow" stuff.

When the build completes, you should see files like this:

$ ls -lh \~/coherepedia

-rw-rw-r-- 1 jonathan jonathan 48G May 20 15:53 coherepedia.ann

-rw-rw-r-- 1 jonathan jonathan 36G May 20 18:05 coherepedia.map

-rw-rw-r-- 1 jonathan jonathan 2.5G May 20 15:53 coherepedia.pqv

-rw-rw-r-- 1 jonathan jonathan 4.1K May 17 23:04 coherepedia.lvq

-rw-rw-r-- 1 jonathan jonathan 1.1M May 17 23:04 coherepedia.pq

These are respectively

* ANN: the vector index, containing the edge lists and LVQ-compressed vectors for reranking.
* MAP: the map containing article data indexed by vector id.
* PQV: PQ-compressed vectors, which are read into memory and used for the approximate search pass.
* LVQ: the LVQ global mean, used during construction.
* PQ: the PQ codebooks, used during construction.

Loading the index (after construction) {#h2-4-loading-the-index-after-construction}
-----------------------------------------------------------------------------------

The code for serving queries is found in the [WebSearch](https://github.com/jbellis/coherepedia-jvector/blob/master/src/main/java/io/github/jbellis/WebSearch.java) class. We're using Spark ([the web framework](https://sparkjava.com/), not the big data engine) to serve a simple search form:
![](https://lh7-us.googleusercontent.com/a-y2F-t0K9ph-4-0ERwSLy7-xhLDMQZD1qz7FU8tDPvj6w1MUhkhznWEksElvPh_1twzn68B8nD6q6wheKlAqxUyyghNhPmxDEs69fYiKTKEtILwwuFhSPNmsDVhS395kDu3hlggzUQIKtG0S_PJxRw)

Construction needed a relatively large heap to keep the edge lists in memory. With that complete, we only need enough to keep the PQ-compressed vectors in memory; *exec@serve*is configured to use a 4GB heap.

WebSearch ([the class behind *exec@serve*](https://github.com/jbellis/coherepedia-jvector/blob/master/src/main/java/io/github/jbellis/WebSearch.java)) first has a static initializer to load the PQ vectors and open the ChronicleMap. We also create a reusable GraphSearcher instance:
![](https://lh7-us.googleusercontent.com/ofHaU8px5jnF0FCupz_mJt4CMc1Bg8Lul36DcScuviM3IPj8UnL7FKD-TMnUh3Lyn41n0Krn_FoooHNaJjf_112xF44SZk9BPe5O-74tuF8VwrmCVEeB571RGYJ-DILbeq4qGFN1MHZQaqxI6v-U8Bw)

Performing a search {#h2-5-performing-a-search}
-----------------------------------------------

Executing a search and turning it into RowData for the user looks like this:
![](https://lh7-us.googleusercontent.com/-bY_lTq_EAXUwfuP_MEsLYcuwwdx13wKCCYAAL83KaxSQ1x8VBbAjlbqGWCxL998vVAlBfEmOxTXZIRkJp8-uTLb0FLXrvCdGICWggC13UKXPCjBq42D5guoHk5IvShjzgpf1IvD2JYcQiIJnyDKedo)

There are four "paragraphs" of code here, containing

1. The call to *getVectorEmbedding*. This calls Cohere's API to turn the search query (a String) into a vector embedding.
2. Creating approximate and reranking score functions. Approximate scoring is done through our product quantization, and reranking is done with the LVQ vectors in the index. Since the LVQ vectors are encapsulated in the index itself, we never need to explicitly deal with LVQ decoding; the index does it for us.
3. The call to *searcher.search*that actually does the query, and finally
4. Retrieving the RowData associated with the top vector neighbors using *contentMap*.

That's it! We've indexed all of Wikipedia with high performance, parallel code in about 150 loc, and created a simple search server in another 100.

On my machine, searches (which each run in a single thread) take about 50ms. We would expect it to take over twice as long if this were split across multiple segments. We would also expect it to lose significant accuracy if searches were performed only with compressed vectors without reranking.

Indexing the entirety of English Wikipedia on a laptop has become a practical reality thanks to recent advances in the JVector library that will be part of the imminent 3.0 release. ([Star the repo](https://github.com/jbellis/jvector) and stand by!) This article demonstrates how to do exactly that using JVector in conjunction with Chronicle Map, while also showcasing the use of [LVQ](https://arxiv.org/abs/2402.02044) to reduce index size while preserving [accurate reranking](https://thenewstack.io/why-vector-size-matters/).

To take advantage of the power of JVector alongside powerful indexing for non-vector data, rolled into a document api with support for realtime inserts, updates, and deletes, check out the [DataStax Astra](https://www.datastax.com/products/datastax-astra) service.

Enjoy hacking with JVector and Astra!

<br />

<br />
