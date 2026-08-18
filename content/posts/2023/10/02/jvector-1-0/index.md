---
title: "Released: JVector 1.0"
date: "2023-10-02T12:36:20+00:00"
lastmod: "2023-10-02T12:37:32+00:00"
description: "JVector is a pure Java embedded vector search engine that powers DataStax Astra and is being added to Apache Cassandra."
authors:
  - "jbellis"
image: "Screenshot-from-2023-09-29-16-39-33.png"
categories:
  - "Apache Cassandra"
  - "Machine Learning"
  - "Release Notes"
related_posts:
  - "adelphi-apache-cassandra-testing-goes-cloud-native"
  - "a-case-for-databases-on-kubernetes-from-a-former-skeptic"
  - "announcing-the-astra-service-broker-tradeoff-free-cassandra-in-kubernetes"
frozen: false
---

JVector is a pure Java embedded vector search engine that powers [DataStax Astra](https://www.datastax.com/products/datastax-astra) and is being [added to Apache Cassandra](https://issues.apache.org/jira/browse/CASSANDRA-18557).

[Vector search is a critical part of today's generative AI applications](https://hackernoon.com/how-llms-and-vector-search-have-revolutionized-building-ai-applications), allowing developers to quickly retrieve the most relevant context to give the large language model enough information to answer accurately and without hallucinating, but innovation in this space has mostly happened outside the Java ecosystem. JVector gives enterprises an easy way to capitalize on their investment in the powerful Java platform, and gives Java developers a state-of-the-art solution that is easy to embed in their applications.

JVector's closest relative is Apache Lucene's vector search. Lucene implements the [HNSW](https://arxiv.org/pdf/1603.09320.pdf) vector search algorithm, which is known to be fast but memory-hungry. Because it is based on the more sophisticated [DiskANN](https://www.microsoft.com/en-us/research/publication/diskann-fast-accurate-billion-point-nearest-neighbor-search-on-a-single-node/) algorithm, JVector is over 10x faster than Lucene for large datasets, holding other things equal. For example, here is a comparison of searching the [Deep100M](https://www.cv-foundation.org/openaccess/content_cvpr_2016/app/S09-38.pdf) dataset (about 35GB of vectors and 20GB of index data) with Lucene and with JVector:

![](https://lh5.googleusercontent.com/XE4JxuQTK7XUZ4sRNL0-PmNsMoxpxYqxiDLUez7zHZ0qLPOBy3jQdmJfayLcGfHWoxlkQcgAe4SaSl-ZhkkiWcXkPmY-thYGUOWH0AJJR7traWDxp-2GxPqRW-j9n6vFlbYj-Alw39m0xH4PExlNUic)

JVector is fast, memory-efficient, disk-aware, concurrent, easy to embed, and incremental.

*Incremental* means that you can start searching your JVector index immediately. There are no batches or microbatches or "commit" stages to wait for.

*Concurrent* means that you can build and search a JVector index with multiple threads simultaneously. Here you can see that doubling the number of threads adding vectors cuts build time in half, out to 32 threads. (X and Y axes are both logarithmic.)
![](https://lh4.googleusercontent.com/hwA3-thdVsb3W7OMcG1v6v8VRSlT6y8sCoR2C32zeH6qumcpAThE8J6SIKhdPQ5wF_U8AXt-B7VX7PDyaOA8Gc6AcnVVaKpHnbQNYKJGeEIaVNaHwGuhOscL-jNBthrbgJ8CjahQ5FD0fXL881KKC1Q)

JVector is designed to be straightforward to embed while preserving high performance. [Here](https://github.com/jbellis/jvector/blob/main/jvector-examples/src/main/java/io/github/jbellis/jvector/example/SiftSmall.java) is the code to compute the index for the [SIFT dataset](http://corpus-texmex.irisa.fr/) shown above. In under 100 lines it

* Computes [product quantization](https://en.wikipedia.org/wiki/Vector_quantization) for the vectors (a kind of compression)
* Loads the vectors into the index, in parallel
* Saves the index to disk
* Conducts searches in parallel, against both in-memory and on-disk indexes
* Computes recall vs ground truth and reports performance numbers

JVector runs on JDK11+, and takes advantage of [Panama SIMD acceleration](https://openjdk.org/jeps/426) on JDK 20+. JVector is available under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).

[Try it out today and let us know what you think](https://github.com/jbellis/jvector/)!
