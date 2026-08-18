---
title: "Atlas Online Archive: Efficiently Manage the Data Lifecycle"
date: "2026-03-12T14:55:00+00:00"
lastmod: "2026-03-12T14:55:02+00:00"
description: "In the production environment, in a MongoDB Atlas database, a collection contains massive amounts of data stored, including aged and current data. However, aged data is not frequently accessed through applications, and the data piles up daily in the collection, leading to performance degradation and cost consumption. This results in needing to upgrade the cluster tier size to maintain sufficient resources according to workload, as it would be difficult to continue with the existing tier size. Overall, this negatively impacts application performance and equates to higher resource utilization and increased costs for business."
authors:
  - "ravindar-karampuri"
image: "Screenshot-2026-03-10-at-1.57.52-PM.png"
categories:
  - "Databases"
  - "Java"
  - "Mongo"
related_posts:
frozen: false
---

## Problem statement

In the production environment, in a MongoDB Atlas database, a collection contains massive amounts of data stored, including aged and current data. However, aged data is not frequently accessed through applications, and the data piles up daily in the collection, leading to performance degradation and cost consumption. This results in needing to upgrade the cluster tier size to maintain sufficient resources according to workload, as it would be difficult to continue with the existing tier size.

Overall, this negatively impacts application performance and equates to higher resource utilization and increased costs for business.

## Resolution

To avoid overpaying, you can offload aged data to a cheaper storage area based on the date criteria, which is called *archival storage* in MongoDB. Later, you can access those infrequently archived data by using MongoDB federated databases. Hence, cluster size, performance, and resource utilization are optimized.

To better manage data in the Atlas cluster, MongoDB introduced the Online Archive feature from MongoDB Atlas 4.4 version onward.

### Advantages

* It archives data based on the date criteria in the archival rule, and the job runs every five minutes by default.
* Query the data through a [federated database connection](https://www.mongodb.com/docs/atlas/data-federation/tutorial/connect/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=atlas-mongodb-foojay&utm_term=tony.kim), which is available in the Data Federation tab.
* Infrequent data access through federated connections apart from the main cluster improves performance and reduces traffic on the main cluster.
* Archived data can be queried by downstream environments and consumed in read-only mode.

### Limitations

* Archived data is available for reading purposes, but it does not support writing or modification.
* [Capped collections](https://www.mongodb.com/docs/manual/core/capped-collections/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=atlas-mongodb-foojay&utm_term=tony.kim) do not support online archival.
* [Atlas serverless clusters](https://www.mongodb.com/docs/atlas/manage-serverless-instances/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=atlas-mongodb-foojay&utm_term=tony.kim) do not support online archival.
* Separate federated connection strings connect archived data.

### Pre-requisites

* Online Archive is supported by cluster tier M10 and above.
* Indexes offer better performance during archival.
* To create or delete an online archive, you must have one of the following [roles](https://www.mongodb.com/docs/atlas/online-archive/manage-online-archive/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=atlas-mongodb-foojay&utm_term=tony.kim):

Project Data Access Admin, Project Cluster Manager, or Project Owner.

## Online archival configuration setup

The cluster DemoCluster has a collection called movies in the database sample_mflix. As per the business rule, you are storing aged and the latest data in the main cluster, but day by day, data keeps piling up, as expected. Therefore, right-sizing your cluster resources by upgrading tier size leads to increased costs.

To overcome this issue and maintain the cluster efficiently, you have to offload the infrequent or aged data to lower cost storage by the online archive feature and access it through a federated database connection. You can manage online archival at any point in time as per business requirements through [managing archives](https://www.mongodb.com/docs/atlas/online-archive/query-online-archive/).

In your case, you have [loaded a sample dataset](https://www.mongodb.com/docs/atlas/sample-data/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=atlas-mongodb-foojay&utm_term=tony.kim) from the MongoDB Atlas cluster setup — one of the databases is sample_mflix — and there is a collection called movies that has aged, plus the latest data itself. As per the business requirement, the last 10 years of data have been frequently used by customers. Therefore, plan to implement archived data after 10 years from the collection based on the date field.

To implement the Online Archive feature, you need a basic M10 cluster or above:  
![Overview of cluster configuration details on MongoDB Atlas web browser](Screenshot-2026-03-10-at-1.57.52-PM.png)

#### Define archiving rules

Once business requirements are finalized, define the rules on which data fields will be archived based on criteria like age, size, and other conditions. We can [set up Online Archive](https://www.mongodb.com/docs/atlas/online-archive/configure-online-archive/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=atlas-mongodb-foojay&utm_term=tony.kim) rules through the Atlas UI or using the Atlas API.

The movies collection in the sample_mflix database has a date field called released. To make online archival perform better, you need to create an index on the released field using the below command.

```
use sample_mflix

db.movies.createIndex({"released":1})
```

After creating the index, you can choose this field as a date-based archive and move the data that is older than 10 years (3652 days) to cold storage. This means the cluster will store documents less than 10 years old, and all other documents move to archival storage which is cheaper to maintain.

Before implementing the archival rule, the movies collection's total document count was 21,349, as seen in the below image.  
![Before setting up online archive – sample_mflix database movies collection document count: 21,349](Screenshot-2026-03-10-at-1.59.12-PM.png)

#### Implementation steps

Step 1: Go to Browse Collections on Cluster Overview and select the Online Archive tab.  
![Location to configure online archive from main cluster – DemoCluster](Screenshot-2026-03-10-at-1.59.39-PM.png)

Step 2: You have to supply a namespace for the collection, storage region, date match field, and age limit to archive. In your case:

* Namespace: sample_mflix.movies
* Chosen Region: AWS / Mumbai (cloud providers AWS, Azure, GCP)
* Date Field: released (Indexed field required)
* Age Limit: 3652 days (10 years from the date)

For instance, today is February 28, 2024, so that means that 3652 days before today would be Feb 28, 2014.  
![Configure an online archive – archiving rule criteria to configure online archive.](Screenshot-2026-03-10-at-2.00.15-PM.png)

Step 3: Here are a couple of features you can add as optional.

Delete age limit: This allows the purging of data from archival storage based on the required criteria. It's an optional feature you can use as per your organization's decision.

In this example, we are not purging any data as per business rules.

Schedule archiving window: This feature enables you to customize schedules. For example, you can run archive jobs during non-business hours or downtime windows to make sure it has a low impact on applications.

Step 4: You can add any further partition fields required.  
![Configure online archive – partition query fields](Screenshot-2026-03-10-at-2.01.48-PM.png)

Step 5: Once the rule configuration is completed, the wizard prompts a detailed review of your archival rule. You can observe Namespace, service provider (AWS), Storage Region (Mumbai), Archive Field, Age Limit, etc.  
![configure an online archive – Review and Confirm page](Screenshot-2026-03-10-at-2.02.23-PM.png)

Step 6: Once the steps are reviewed, click on BeginArchiving to create data federation instances in the [DataFederation](https://www.mongodb.com/docs/atlas/data-federation/tutorial/getting-started/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=atlas-mongodb-foojay&utm_term=tony.kim) tab. Then, it will start archiving data based on the validation rule and move to [AWS S3 storage](https://www.mongodb.com/docs/atlas/billing/data-federation/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=atlas-mongodb-foojay&utm_term=tony.kim#data-federation-costs). One of the best features is you can modify, pause, and delete online archival rules any time around the clock. For instance, your archival criteria can change at any time.  
![configure an online archive – Begin Archiving confirm page](Screenshot-2026-03-10-at-2.02.51-PM.png)

Step 7: Once the Online Archive is set, there will be an archive job run every five minutes by default. This validates criteria based on the date field and moves the data to archival storage. Apart from that, you can set up this job as per your custom range instead of the default schedule. You can view this archival job in the cluster main section as seen in the below image, with the actual status Archiving/IDLE.  
![Online archive – overview page from main cluster consolidated rule information.](Screenshot-2026-03-10-at-2.03.21-PM.png)

The Atlas Online Archive feature will create two federated database instances in the Data Federation tab for the cluster to access data apart from the regular connection string:

* A federated database instance to query data on your archive only
* A federated database instance to query both your cluster and archived data

![Data federation – online archive federated database instances window](Screenshot-2026-03-10-at-2.03.50-PM.png)

When the archival job runs as per the schedule, it moves documents to archival storage. As a result, the document count of the collection in the main cluster will be reduced by maintaining the latest data or hot data.

Therefore, as per the above scenario, the movies collection now contains fresh/the latest data.

Movies collection document count: 2186 (it excludes documents more than 10 years old).

Every day, it validates 3652 days later to find documents to move to archival storage.

You can observe the collection document count in the below image:  
![movies collection latest count on base cluster after online archive setup.](Screenshot-2026-03-10-at-2.04.20-PM.png)

#### How to connect and access

You can access archived or read-only data through the [Data Federation](https://www.mongodb.com/docs/atlas/data-federation/tutorial/connect/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=atlas-mongodb-foojay&utm_term=tony.kim) wizard. Simply connect with connection strings for both:

* Archived only (specific database collection for which we set up archive rule)
* Cluster archive (all the databases in it)

*\*\* You can point these connection strings to downstream environments to read the data or consume it via end-user applications.*

#### Atlas Data Federation

[Data Federation](https://www.mongodb.com/docs/atlas/data-federation/overview/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=atlas-mongodb-foojay&utm_term=tony.kim) provides the capability to federate queries across data stored in various supported storage formats, including Atlas clusters, Atlas online archives, Data Lake datasets, AWS S3 buckets, and HTTP stores. You can derive insights or move data between any of the supported storage formats of the service.  
![Once the archival job has run, we will see data movement to federated instances with details.](Screenshot-2026-03-10-at-2.05.16-PM.png)

1. *DemoCluster archive:*

This is a federated database instance for your archive that allows you to query data on your archive only. By connecting with this string, you will see only archived collections, as shown in the below screen. For more details check, [visit the docs](https://www.mongodb.com/docs/atlas/online-archive/connect-to-online-archive/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=atlas-mongodb-foojay&utm_term=tony.kim#connect-to-the-federated-database-instance-for-your-online-archive).

Here, the cluster name DemoCluster has archived collection data that you can retrieve only by using the below connection string, as shown in the image.

Connection string:

```
mongodb://Username:Password@archived-atlas-online-archive-65df00164668c44159eb65c8-abcd6.a.query.mongodb.net/?ssl=true&authSource=admin
```

![Data federation – federated instances connection string access steps](Screenshot-2026-03-10-at-2.06.04-PM.png)

As shown in the image, you can view only those archived collections data in the form of READ-ONLY mode, which means you cannot modify these documents in the future.  
![Archived database collection – sample_mflix.moves data only from this connection string](Screenshot-2026-03-10-at-2.06.55-PM.png)

2. *DemoCluster cluster archive:*

This federated database instance for your cluster and archive allows you to query both your cluster and archived data. Here, you can access all the databases in the cluster, including non-archived collections, as shown in the below image.

Connection string:

```
mongodb://Username:Password@atlas-online-archive-65df00164668c44159eb65c8-abcd6.a.query.mongodb.net/?ssl=true&authSource=admin
```

Note: Using this connection string, you can view all the databases inside the cluster and the archived collection's total document count. It also allows READ-ONLY mode.  
![Archived cluster data – both your cluster and archived data access by using this instance/connection string](Screenshot-2026-03-10-at-2.07.26-PM.png)

#### Project cluster overview

As discussed earlier, the main cluster DemoCluster contains the latest data as per the business requirements — i.e., frequently consumed data. You can access data and perform read and write operations at any time by pointing to live application changes.

*Note: In your case, the latest data refers to anything less than 10 years old.*

Connection string:

```
mongodb+srv://Username:Password@democluster.abcd6.mongodb.net/
```

In this scenario, after archiving aged data, you can see only 2186 documents for the movies collection with data less than 10 years old.  
![MongoCompass – only latest data available on main cluster after online archive setup. Aged data can be accessed through separate federated database instances as discussed in the Federated Databases tab.](Screenshot-2026-03-10-at-2.07.50-PM.png)

You can use MongoShell, an application, or any third-party tools (like MongoCompass) to access the archived data and main cluster data.

Alternatively, with all three of these connection strings, you can fetch from the below wizard in cluster connect.

1. Connect to cluster and Online Archive (read-only archived instance connection string)
2. Connect to cluster (direct cluster connection to perform CRUD operations)
3. Connect to Online Archive (read-only specific to an archived database connection string)

![Three separate connection strings after online archive setup completed](Screenshot-2026-03-10-at-2.08.24-PM.png)

MongoShell prompt: To connect both archived data from the Data Federation tab, you can view the difference between both archived data in the form of READ-ONLY mode.  
![ShellPrompt → The left side is only an archived database view; the right side is an Atlas online archive cluster databases view.](Screenshot-2026-03-10-at-2.08.53-PM.png)

MongoShell prompt: Here in the main cluster, you can view a list of databases where you can access, read, and write frequent data through a cluster connection string.  
![ShellPrompt → Base cluster list of databases where CRUD operations are performed from the application](Screenshot-2026-03-10-at-2.09.17-PM.png)

## Conclusion

Overall, MongoDB Atlas's online archival feature empowers organizations to optimize storage costs, enhance performance, adhere to data retention policies by securely storing data for long-term retention periods, and effectively manage data and storage efficiency throughout its lifecycle.

We'd love to hear your thoughts on everything you've learned! Join us in the [Developer Community](https://www.mongodb.com/community/forums/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=atlas-mongodb-foojay&utm_term=tony.kim) to continue the conversation and see what other people are building with MongoDB.
