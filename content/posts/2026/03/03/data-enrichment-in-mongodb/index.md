---
title: "Data Enrichment in MongoDB"
date: "2026-03-03T16:10:36+00:00"
lastmod: "2026-03-03T16:10:38+00:00"
description: "In a recent design review, a customer was enriching new data as it came in. As the enrichment process was fairly complex, they ran into some issues with concurrency. To solve this, they decided that data should go into a staging collection rather than the main collection that held the data. This did nothing to help with concurrency issues and actually created more work on the database side of things when enrichment was complete."
authors:
  - "mike-laspina"
image: "Screenshot-2025-12-30-at-10.48.52-PM.png"
categories:
  - "Databases"
  - "Mongo"
related_posts:
  - "agents-meet-databases-the-future-of-agentic-architectures"
  - "atlas-searching-with-the-java-driver"
  - "best-practices-for-deploying-mongodb-in-kubernetes"
  - "beyond-keywords-hybrid-search-with-atlas-and-vector-search-part-3"
frozen: false
---

In a recent design review, a customer was enriching new data as it came in. As the enrichment process was fairly complex, they ran into some issues with concurrency. To solve this, they decided that data should go into a staging collection rather than the main collection that held the data. This did nothing to help with concurrency issues and actually created more work on the database side of things when enrichment was complete.

A common data pattern is to enrich data after a new item is created. Once the new item is created, it often needs to be enriched by both data from other systems as well as human beings. There are a few nuances to get this entire process correct. In a typical scenario, a new product or service will be created and must go through a workflow before being offered to customers. This workflow often includes multiple steps of data enrichment so that informed decisions can be made. A common example of this is in the insurance industry where new policies are typically offered on a yearly basis. Let's explore this example in more detail.

The RiskReducer insurance company provides insurance for commercial structures. These include retail or office buildings, warehouses, factories, and the like. Prior to the policy renewal for an existing client, a new policy proposal needs to be generated. This sets a workflow in motion that would require the following data enrichment prior to making a renewal offer to the customer:

1. Parts of the existing policy plan are copied to a new plan for the following year.
2. Reference to prior claims are added via an automated process.
3. Assets to be insured are given a valuation via an automated process and then reviewed by a human before being finalized.
4. Risk factors are adjusted by a human.
5. The policy is sent to underwriting to determine insurability and proposed rate.
6. A final review by finance is done to ensure that the premiums are appropriate.

Note that not all of these enrichment steps are occurring in the sequence shown above. Some may be happening in parallel and others may depend on prior steps. This gets even more complicated when we consider concurrency, workflow dependencies, etc...

## Concurrency in data enrichment

Data enrichment often needs to occur in a specific order to fulfill dependencies in the workflow. A status indicator would normally be used to ensure that each step of the workflow happens in the proper sequence. RiskReducer insurance uses the following statuses for their workflow above:

* New: initial policy ready for enrichment
* Claims: history of prior claims
* Assets: an inventory of assets
* Valuation: valuations of assets
* Valuation review: this is done by a human
* Risk factor review: done by a human
* Underwriting: done by a human
* Final review: done by a human
* Complete: enrichment complete

There are two types of concurrency we need to consider for this workflow:

### Machine concurrency

In this case, multiple processes may attempt to update the same document(s) at the same time. A common approach here is to add jobs to a queue to enrich data as needed. If jobs are taken from the queue by multiple processes, there is no way to guarantee that the tasks will be picked up in the order they were created. If jobs must be done in a specific sequence, we can use a combination of status, an in-process flag, and optimistic locking to ensure that tasks are completed in the correct order. For example, claim enrichment would issue the following update to set the status and begin its work:

|-------------------------------------------------------------------------------------------------------------------------------------------|
| policy.updateOne ({policyNum : 'C456789', year : 2025, status : 'New', inProcess : False},{$set : {status : 'Claims', inProcess : True}}) |

If the updateOne did not update any documents, then we know that this policy has already either been processed or is currently being processed by another job. The job should be left in the queue and re-visited at a later time, or removed if the status is beyond "New." Once the Claims enrichment is complete, the inProcess flag should be set to False in order to allow the next step (Assets). Note that the Asset job would also issue a similar update to above to begin its work:

|--------------------------------------------------------------------------------------------------------------------------------------------|
| policy.updateOne ({policyNum : 'C456789', year : 2025, status : 'Claims', inProcess : False},{$set : status : 'Assets', inProcess : True}) |

If different parts of the data can be enriched concurrently, then document structure can be used to determine if work is being done. Let's assume that both the Claims and Assets steps can be done concurrently. In this case, we can use the existence of sub-documents to determine if these steps have begun. When a document is created, neither the claims nor asset sub documents exist. They would be created upon the start of each of those tasks, resulting in the following document if both jobs start at roughly the same time:

|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| { _id : '1234-5436-7896-5478', policyNum : 'C456789', year : 2025, claims : \[{...}, {...}\], -- Added at start of claims process assets : \[{...}, {...}\], - Added at start of asset process ...} |

Note that we'll still need the same optimistic locking construct here. It just looks a little different:

|-----------------------------------------------------------------------------------------------------------------------|
| policy.updateOne ({policyNum : "C456789", year : 2025, claims : {$exists : false}},{$set : {claims.inProcess : True}) |

It's entirely possible that a mix of both concurrent and sequential processing is needed. Typically, we can enforce this using an array or process indicators in the document:

|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| { _id : '1234-5436-7896-5478', policyNum : 'C456789', year : 2025, enrichStatus: \[{step: 'claims', status: 'Complete'}, {step: 'assets', status: 'InProcess'}\] ...} |

The status array above indicates that the Claims step is complete and the Assets step has been started but is still in process. The lack of any other status section in the array indicates that those steps have not been started yet. Each step of the enrichment process can use this array to track what's going on.

### Human concurrency

Whenever data is to be modified by a set of fingers and eyeballs (i.e., a human), concurrency is a bit of a different concern. A common approach in this case is pessimistic locking. In short, the user updating a section of the document can check out the entire document, or just a portion of it. This has some implications as humans work in a completely different timescale than computers do. In addition, humans can be interrupted in the middle of their work. When designing a locking mechanism for human data enrichment, ensure the following:

* One and only one person can take a lock at any given time.
* The application (or database) must be able to forcibly release that lock—preferably without any intervention.

Let's take Mary as an example. Mary locks the Asset portion of the policy for review and starts to make changes. Mary gets pulled into a customer emergency. She leaves her desk in a hurry and does not unlock the Asset part of the document. Underwriting needs this policy first thing in the morning to meet the customer deadline, so asset review needs to be completed today by someone else. Mary still has the lock on the Asset portion of the policy. How do we handle this in an automated way?

I typically recommend locking in a separate collection when fingers and eyeballs are involved. The main advantage of this is that we can use a [Time To Live](https://www.mongodb.com/docs/manual/core/index-ttl/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=data-foojay&utm_term=tony.kim) (TTL) index to automatically remove the lock after a certain period of inactivity. If the application has a timeout period, then the TTL index should remove the lock shortly after the user has been logged out. For example, a 30-minute application timeout could use a 35-minute TTL index to automatically remove the lock. Since we're locking section of the doc, our lock collection might look something like this:

|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| { _id : ObjectId("507f1f77bcf86cd799439011"), policyId : '1234-5436-7896-5478', section : 'Assets', lockedBy : 'Mary', lastUpdate : ISODate("2025-01-29T14:10:30.000Z") } |

Note that whenever data is saved, the lastUpdate field should be updated in order to extend the lock. After Mary leaves to pick up her child, she will be auto-logged out of the application after a time, and the TTL index on lastUpdate will automatically remove the lock shortly after. This will allow another user to complete the asset valuation shortly after Mary leaves her desk.

If the status of the document is appropriate, taking the lock is a simple process of inserting a document into the lock table if one is not there. In our example, the simplest (and ATOMIC) way of doing this is to create a unique index on the policyId and section fields:

|-----------------------------------------------------------------------|
| db.locks.createIndex( { policyId: 1, section: 1 }, { unique: true } ) |

Inserting a new document will fail if someone else has a lock on that section. The followingWriteResult would be returned if the lock cannot be taken:

|---------------------------------------------------------------------------------------------------------------------------------------------------|
| { "nInserted" : 0, "writeError" : { "code" : 11000, "errmsg" : "E11000 duplicate key error index: db.locks.\<index name\> dup key: { : null }" }} |

In the case that two people try to lock the same document at the same time, only one will be able to take the lock. There is no need to check the status and take the lock inside of a multi-document transaction if your status progression for a given section is thought out properly. In this case, the ability to take a lock might be based on that section's status being a value that allows for locking, which should only occur after any machine processing is complete.

In our example, 35 minutes after Mary's last update to the document, the TTL index will automatically remove the lock. At this point, the person assigned the job of completing the asset valuation in Mary's absence can then take the lock on the Asset section and complete the work. Note that the application should release the lock as part of the completion process.

## Schema design patterns

When enriching data, it's important to keep schema design patterns in mind. In our insurance policy example, there are many things to take into account. We typically recommend embedding data that is needed for most reads in a single document within reason. However, this is not always the case. For example, a policy can apply to any number of buildings. The needs of a single location business may differ widely from Starbucks, which has approximately one million\* locations. There is no way we can embed all of these locations in a document given the 16mb document size limit as well as other performance considerations.

\* This is just a guess based on the number of locations near me.

When data varies widely like this, we can store small data sets in-document and larger ones in a separate collection using the [outlier pattern](https://www.mongodb.com/blog/post/building-with-patterns-the-outlier-pattern/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=data-foojay&utm_term=tony.kim). The question now is: Where do we go to fetch the data? In this case, we may want to modify the [subset](https://www.mongodb.com/blog/post/building-with-patterns-the-subset-pattern/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=data-foojay&utm_term=tony.kim) pattern a bit in order to let the application know where to get the data. If we're insuring a small number of assets, we can embed them in an array and use a flag to let the app know all assets are in the document:

Policy:

|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| { _id : '1234-5436-7896-5478', policyNum : 'C456789', year : 2025, hasExtras: false, assets : \[ {id : 1, name : 'Carls Car wash location 1', ...}, {id : 2, name : 'Carls Car wash location 2', ...\], {id : 3, name : 'Carls Car wash location 3', ...}} |

However, if we're insuring a large number of assets, we'll need to reference these in a separate collection as they won't fit inside a single collection. We'll use the same flag to let the app know:

Policy:

|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| { _id : '1234-5436-7896-7123', policyNum : "C745603", year : 2025, hasExtras: true,assets : \[ {id : 1, name : 'Starbucks HQ', ...}, {id : 2, name : 'Safeway store 3456', ...\], {id : 3, name : 'Target 543', ...}}} |

Assets:

|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| { _id : 'sbx0000000001, policyID : '1234-5436-7896-7123', name : 'Starbucks store 1', ...}...{ _id : 'sbx1000000000, policyID : '1234-5436-7896-7123', name : 'Starbucks store 1000000', ... } |

Be sure to keep the following in mind:

* The number of assets for this cutoff will depend on two rules of thumb for efficient schema design:
  * Arrays should generally have 200 or fewer elements.
  * Document size should generally be 200kb or less. This rule can (and should) be broken if all of the data in the document is almost always used by the application when retrieved.
* Enforce the embedding rule at enrichment time. This requires the enrichment process to know how many assets are to be insured up front.
* Create an index starting with policyID in the Assets collection to ensure a quick retrieval of assets for the given policy.
* Depending how you decide to track enrichment status, you may need to create an empty array of assets in the collection in order to use the $exists clause for optimistic locking of that section. If you are using separate fields to control workflow, then the externalFlag operation may not be needed as you can use $exists on the Assets array to determine if they are in-document or in a separate collection.

## Best practices

### Plan for concurrency

Both human and machine concurrency are common issues in today's parallel processing architectures. It's best to assume that every human and every process will want to modify the same document at the same time. A good workflow and locking strategy are needed, especially when things start to scale.

### Use a separate collection for pessimistic locking

The locks collection can be a central place for all locks. This has several advantages of locking within the collection itself:

* All active locks can be seen in one place.
* A Time To Live index can be added to automatically remove locks after a certain amount of time has passed. The expireAfterSeconds should be just a little longer than the application timeout.
* A single ATOMIC statement should be used to obtain, update, or delete the lock.
* Ensure the document is in the correct status immediately prior to taking the lock.

### Add an auto-save to your application when humans and locks are involved

Although the user may not have changed any data, the application should periodically auto-save to update the lastUpdated field in the lock. This will prevent the lock from being released pre-maturely. The auto-save should only fire if the user is still logged into the application.

### Release the lock app-side on logout

In most cases, the lock should be released when the user logs out of the application. The exception here is when a long, multi-day lock is needed. For short locks, release the lock regardless of whether the user logs out, or the application automatically logs them out after a time of inactivity. For longer, multi-day locks, rely on the TTL index to release the lock if the user has not.

## Anti-patterns

### Using separate (temporary) collections to enrich data

One pattern I've seen is to use a separate collection to create and enrich data. Once enrichment is complete, the document is then copied to the destination collection and removed from the temporary one. This can be problematic for a few reasons:

* Additional collections and indexes must be maintained: This will take up more disk and memory on the server.
* The document(s) for *both* collections must be in cache when doing the copy, resulting in twice the memory consumption on the server for this step.
* Concurrency can be an issue when copying as there is no way to do this as a single ACID transaction. Resist the urge to use a multi-document transaction here as it's not needed if the document is stored in a single place during the entire lifecycle.

### Avoid using multi-document transactions for locking/unlocking

In some cases, a multi-document transaction is needed. Maintaining concurrency does not require this as we are only updating a single document whether we are storing the lock inside the collection or in a separate one. If two users attempt to lock the same document at the same time, one will win the lock and the other won't. Wrapping this inside of a transaction will only consume more resources on the server without providing any additional benefits as single document updates are already ATOMIC.

### Using an optimistic locking strategy for human editing

Humans, with their fingers and eyeballs, tend to work in a non-linear fashion. For example, Mary is editing a document and heads off to one of the million\* Starbucks locations for a coffee. Since she is away from her desk for 20 minutes, another user may edit the same document. An optimistic locking strategy will cause Mary to lose her unsaved changes when she returns to editing the document. A pessimistic locking strategy is better in this case to accommodate the non-linear workflow of humans.

\* Again, just a guess based on the number of locations near me.

### Ignoring concurrency for machine enrichment

When using an optimistic locking strategy for machine enrichment, be mindful of possible collisions due to concurrency. For example, if four worker processes attempt to update the same document at the same time, using "version" for an optimistic lock, a lot of extra work may be done. Use the update statement below:

|----------------------------------------------------------------------------------------------------|
| db.policy.updateOne( {_id: 322326, version: 1}, {$set: {\<fields to set\>}, $inc: {"version": 1}}) |

On the first attempt, one of the workers will update the document and three will fail. On the second try, the three remaining threads will need to re-read data, apply changes, and attempt an update with the latest version—only one will succeed. The third attempt will be two workers reading data and attempting an update with one failing. Finally, on the fourth pass, only one worker is left to read and update the document. In this case, four threads going after the same document at the same time result in:

* 4+3+2+1 (10) reads.
* 4+3+2+1 (10) update attempts, with only four succeeding.

When processing is being done at scale via multiple workers, it's best to try and organize these workers so that any given document is processed sequentially by a single worker, rather than randomly by multiple workers. This will avoid the multiple failed attempts to update the document.

## Conclusion

Data enrichment can be a complex process, especially when fingers and eyeballs are part of the mix. Use a solid concurrency strategy to ensure updates are not overwritten and any human can lock the document (or part of the document) they need in order to edit the data without worry of someone else obliterating their changes. A lock taken by a human (or a machine) may need to be forcibly released for a variety of reasons. Using a separate lock collection with a TTL index can do this for you without the need to manually intervene. Finally, enriching a document in-place using status indicators will consume fewer resources on the DB server than creating the document in one collection and then moving it to another after enrichment is complete.
