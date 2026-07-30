---
title: "Run an Atlas cluster locally in minutes"
slug: "run-an-atlas-cluster-locally-in-minutes"
date: "2025-08-05T07:33:47+00:00"
lastmod: "2025-08-05T07:38:32+00:00"
description: "You no longer need a cloud account to try MongoDB Atlas features. Spin up a fully compatible local cluster in seconds, with no login, no credit card, and no friction."
authors:
  - "arekborucki"
image: "https://foojay.io/wp-content/uploads/2025/05/mongologo.png"
categories:
  - "Java"
  - "Mongo"
tags:
related_posts:
enlighterjs: true
frozen: false
---

You no longer need a cloud account to try [MongoDB](https://www.mongodb.com/lp/cloud/atlas/try4-reg/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=foojay-blog-atlas&utm_term=tony.kim) Atlas features. Spin up a fully compatible local cluster in seconds, with no login, no credit card, and no friction.

🕒 Reading time: 2-3 min

🍃 What is the MongoDB Atlas Platform?

MongoDB Atlas is a fully managed, multi-cloud data platform provided by MongoDB Inc. It allows you to deploy and run MongoDB clusters on Amazon Web Services (AWS), Google Cloud Platform (GCP), and Microsoft Azure, with full support for replica sets (for high availability), sharding (for horizontal scalability), and multi-cloud deployments. Atlas handles infrastructure, backups, security, scaling, and monitoring out of the box. It also extends the core MongoDB server capabilities with a set of integrated features.

* Atlas Search ★: Use full-text indexing built on Apache Lucene.
* Vector Search ★: Run semantic similarity queries for gen AI use cases.
* Global clusters: Deploy data close to users across multiple regions or cloud providers.
* SQL interface: Query data using SQL syntax and JDBC/ODBC-compatible tools.
* Online archive: Tier cold data automatically from hot storage to lower-cost S3.
* Atlas Stream Processing: Process real-time event streams directly in the platform.
* Integrated security: Secure data with fine-grained access control, encryption, and auditing.

★ These features will also be available in upcoming releases of MongoDB Community Edition and MongoDB Enterprise Advanced.

With Atlas CLI, you can now replicate much of that experience locally and interact through mongosh - without needing a cloud account. It's ideal for development, testing, and workshops running entirely on your machine.

🔧 What is Atlas CLI?  

Atlas CLI is the official command-line tool for managing MongoDB Atlas clusters. While it's commonly used to create and manage cloud MongoDB environments, it now also supports running fully local clusters that behave like real Atlas clusters. This makes it a perfect tool for local development, workshops, testing, or experimenting with new MongoDB 8.0 features offline.

💻 What is mongosh?  

mongosh is the official MongoDB shell, used to interact with MongoDB deployments from the command line. It is built on Node.js and uses JavaScript as its execution language. As a modern replacement for the legacy mongo shell, it provides a richer developer experience with syntax highlighting, autocompletion, support for ECMAScript features, and asynchronous operations using promises or async/await.

You can use mongosh to run queries, inspect and modify data, run aggregation pipelines, manage indexes, and explore features such as full-text search and vector search, whether you're connected to a local or cloud deployment.

📦 Installing Atlas CLI and mongosh

On macOS, you can install Atlas CLI and mongosh using Homebrew:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">brew tap mongodb/brew
brew install mongodb-atlas
brew install mongosh
</pre>

On Linux, installation is just as simple:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">curl -s https://mongodb.dev/cli | bash</pre>

🚀 Creating a Local Atlas Deployment  

Atlas CLI uses Docker internally to create local MongoDB environments that replicate the Atlas runtime. Before running the Atlas deployments setup, ensure that Docker is installed and the Docker daemon is active. Without it, local deployments won't work.

Once Docker is running, launch the interactive setup:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">atlas deployments setup</pre>

Choose the local option, accept the defaults, and specify a port (e.g., 27017). The CLI will spin up a containerized MongoDB 8.0 replica set with Atlas-compatible features. Now you can list active deployments:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">atlas deployments list
NAME       TYPE    MDB VER   STATE
local813   LOCAL   8.0.11    IDLE
</pre>

🔗 Connecting to the Deployment

To connect to your local deployment, simply run:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">atlas deployments connect</pre>

You'll be prompted to choose how you want to connect. For example:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">? How would you like to connect to local813?
&gt; mongosh - MongoDB Shell
 compass - MongoDB Compass
 vscode - MongoDB for VSCode
 connectionString - Connection String
</pre>

Selecting mongosh will launch an interactive session connected to your local MongoDB replica set. You can now run queries, create indexes, test aggregations, or explore features like MongoDB Atlas Search and Vector Search.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">AtlasLocalDev local813 [direct: primary] test&gt; show dbs
admin   256.00 KiB 
config  232.00 KiB 
local   588.00 KiB
AtlasLocalDev local813 [direct: primary] test&gt; 
</pre>

⚙️ Managing the Deployment

Pause the deployment:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">atlas deployments pause</pre>

Start the deployment:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">atlas deployments start</pre>

View logs:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">atlas deployments logs</pre>

Delete the deployment:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">atlas deployments delete</pre>

Each command will prompt you to select a deployment if none is specified.

📙 What's Next?  

Want to learn how MongoDB Atlas Search and Vector Search work in a local environment? That's exactly what the next issue will cover.

📘 More Tips Like This  

Want more hands-on examples, best practices, and deep dives into MongoDB 8.0 and the Atlas platform? Check out 👉 MongoDB in Action: Building on the Atlas Data Platform. Published by Manning Publications Co.

![](data:image/png;base64,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)
