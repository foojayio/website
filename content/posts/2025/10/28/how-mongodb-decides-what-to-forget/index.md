---
title: "How MongoDB Decides What to Forget?"
slug: "how-mongodb-decides-what-to-forget"
date: "2025-10-28T14:00:39+00:00"
lastmod: "2025-10-28T14:00:40+00:00"
description: "Inside MongoDB’s storage engine, WiredTiger, nothing happens by accident. Every page in memory exists under policy — governed, measured, and continuously evaluated against the limits of RAM, I/O bandwidth, and checkpoint cadence. Eviction is not cleanup. It’s runtime arbitration between volatility and durability.When the process starts, WiredTiger allocates a fixed memory region known as the cache arena typically 50% of physical RAM. Within that space live B-tree pages: internal nodes, leaf nodes, and history-store entries. Each page carries operational metadata: dirty, clean, hazard-protected, in-use, last_access_time, and generation. This metadata feeds into a per-page score, which informs the eviction subsystem’s next decision."
authors:
  - "elie-hannouch"
image: "1_2RyMETT6diUSVGKdJPoQgQ.webp"
categories:
  - "Databases"
  - "Mongo"
tags:
related_posts:
  - "agents-meet-databases-the-future-of-agentic-architectures"
  - "best-practices-for-deploying-mongodb-in-kubernetes"
  - "beyond-keywords-implementing-semantic-search-in-java-with-spring-data-part-1"
  - "beyond-keywords-optimizing-vector-search-with-filters-and-caching-part-2"
frozen: false
---

<figure class="aligncenter size-full is-resized">
 <img fetchpriority="high" decoding="async" width="720" height="720" src="1_2RyMETT6diUSVGKdJPoQgQ.webp" alt="" class="wp-image-121588" style="width:368px;height:auto">
</figure>

Inside MongoDB's storage engine, [WiredTiger](https://www.mongodb.com/docs/manual/core/wiredtiger/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=mongodb-forget-foojay&utm_term=tony.kim), **nothing happens by accident.**{#4cf7}

Every page in [memory](https://www.mongodb.com/docs/atlas/sizing-tier-selection/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=mongodb-forget-foojay&utm_term=tony.kim#memory) exists under policy --- governed, measured, and continuously evaluated against the limits of RAM, I/O bandwidth, and checkpoint cadence. Eviction is not cleanup. It's **runtime arbitration between volatility and durability**.{#ba51}

When the process starts, WiredTiger allocates a fixed memory region known as the **cache arena** typically 50% of physical RAM. Within that space live **B-tree pages** : internal nodes, leaf nodes, and history-store entries. Each page carries operational metadata: **dirty, clean, hazard-protected, in-use, last_access_time, and generation** . This metadata feeds into a **per-page score**, which informs the eviction subsystem's next decision.{#61cb}

At steady state, the cache holds both **clean pages** (byte-for-byte identical to disk) and **dirty pages** (modified since last checkpoint). When total occupancy approaches the configured **eviction_target** , a **cooperative multi-threaded subsystem** engages background **evict_lru_worker threads** start scanning the B-trees, guided by internal structures like WT_PAGE_INDEX and WT_REF, using an adaptive **LRU-like algorithm** tuned by recent access histograms.{#6487}

The core loop looks deceptively simple:{#533a}

### **Scan → Score → Hazard Check → Write (if dirty) → Evict → Adjust Metrics**

But each stage involves subtle synchronization:{#2494}

* **Scanning** is probabilistic workers don't traverse full trees; they sample subtrees to avoid lock contention.
* **Scoring** blends recency and reuse. WiredTiger doesn't track strict LRU; it uses *generational counters* that decay over time.
* **Hazard checking** ensures no session currently holds a read cursor (WT_SESSION_IMPL) referencing that page.
* **Write phase** invokes wt_evict_dirty_leaf(), writing through the **WiredTiger Journal (WAL)** if necessary, updating the **durable history store** when timestamped versions are needed.
* **Evict phase** frees the WT_PAGE structure and updates eviction statistics for adaptive tuning.

The eviction engine constantly measures its own efficiency using internal metrics like eviction server candidate queue length, eviction active workers, and eviction empty queue passes.  

When efficiency drops meaning eviction cannot keep up with mutation rate the engine escalates through defined phases:{#0275}

1. **Passive eviction** --- background threads maintain equilibrium below eviction_target.
2. **Aggressive eviction** --- triggered near eviction_trigger; application threads begin assisting.
3. **Emergency eviction** --- above 95% occupancy; eviction threads run synchronously, blocking writers until space is reclaimed.

This escalation is controlled by the **Eviction Server** , which coordinates worker pools using an internal **task queue** implemented with spinlocks and condition variables to minimize scheduler overhead.{#edf3}

Eviction is deeply coupled to **checkpointing** . While checkpoints provide stable on-disk consistency by writing all dirty pages up to a **stable timestamp** , eviction continuously frees memory between checkpoints. If eviction outpaces checkpointing, dirty data accumulates in the **WiredTiger Journal (WiredTigerLog.\*)** . If checkpointing lags, the **tracked dirty bytes** metric grows, triggering **flow control**.{#cf6e}

Flow control, implemented in the replication coordinator layer, slows down writers at the client operation level. It computes a delay token proportional to (dirty_bytes / cache_size) and (replication_lag / apply_rate). In effect, **the cache backpressure propagates up to the client layer** a closed feedback loop spanning the storage, replication, and client I/O pipelines.{#6146}

A key subtlety lies in how WiredTiger separates **internal** and **leaf page eviction quotas** . Internal nodes are critical for cursor traversal; evicting too many increases read amplification. Thus, the system enforces ratios like **internal_pages_evicted / leaf_pages_evicted ≈ 0.05--0.1** to maintain navigational efficiency. This is continuously adjusted based on statistics in **WiredTigerStatType::cache_eviction_internal.**{#71d4}

Another often-missed detail: **dirty pages are written incrementally, not atomically**. WiredTiger splits them into smaller "update chains," writing partial updates if page size exceeds split_page_max. This design avoids long stalls under large document updates a crucial factor for workloads with oversized BSON documents or frequent $push operations.{#07a8}

In a high-throughput deployment, this entire mechanism behaves like a distributed feedback system:{#41b7}

* The **sensor layer** measures cache pressure (bytes currently in cache, dirty_bytes_ratio, eviction_trigger).
* The **controller layer** adjusts eviction thread counts and page scoring heuristics.
* The **actuator layer** executes writes, advancing checkpoints and freeing pages.
* The **stabilizer layer** (flow control) modulates client throughput to keep the system near steady state.

This is control theory implemented in a storage engine a practical PID loop embedded inside C code and shared locks.{#1db7}

The result is not perfection but **bounded instability**: cache utilization oscillates, dirty bytes rise and fall, eviction queues expand and drain. That's the intended state. A flat metric line means eviction has stalled no breathing room left.{#1da8}

Engineers who monitor clusters at scale don't watch "cache size"; they track oscillation frequency. A healthy system breathes roughly once per checkpoint interval. When the oscillation slows, latency spikes. When it quickens uncontrollably, disk I/O saturates. Balancing those two edges is what the eviction engine was designed to do.{#7061}

What fascinates me most is how **architecturally pure** this mechanism is.  

It doesn't rely on heuristics alone; it encodes system health as measurable control variables. Memory, disk, and replication are not separate domains, they're interconnected feedback loops sharing a common timebase. That's what gives MongoDB its stability under unpredictable workloads.{#a59a}

Eviction isn't a cleanup cycle. It's a **continuous act of governance** the system regulating its own volatility so that higher layers can deliver consistency and performance. And the deeper you study it, the more you realize:{#9b70}
> The hardest part of building reliable systems isn't writing data. **It's knowing exactly when to let go.**{#5b5a}
