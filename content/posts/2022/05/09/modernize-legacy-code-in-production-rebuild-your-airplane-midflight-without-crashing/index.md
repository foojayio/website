---
title: "How To Modernize Legacy Code in Production"
slug: "modernize-legacy-code-in-production-rebuild-your-airplane-midflight-without-crashing"
date: "2022-05-09T15:58:36+00:00"
lastmod: "2022-05-09T16:07:02+00:00"
description: "Rewriting applications is easy. Doing it while preserving compatibility... that's a bit hard. Doing it live in prod. That's the big challenge!"
canonical: "https://lightrun.com/best-practices/modernize-legacy-code-in-production-rebuild-your-airplane-midflight-without-crashing/"
authors:
  - "shai-almog"
image: "Blog---20.png"
categories:
  - "DevOps"
  - "Tutorials"
tags:
related_posts:
  - "logging-best-practices-mdc-ingestion-and-scale"
  - "interview-with-tom-granot-developer-observability-koolkits-and-reliability"
  - "debugging-the-java-message-service-jms-api-using-lightrun"
  - "optimizing-the-garbage-collector-when-migrating-cloud-workloads"
frozen: false
---

I spent over a decade as a consultant working for dozens of companies in many fields and pursuits. The diversity of each code base is tremendous. This article will try to define general rules for modernizing legacy code that would hopefully apply to all. But it comes from the angle of a Java developer.

When writing this, my primary focus is on updating an old Java 6 era style J2EE code to the more modern Spring Boot/Jakarta EE code. However, I don't want to go into the code and try to keep this generic. I discuss COBOL and similar legacy systems too. Most of the overarching guidelines should work for migrating any other type of codebase too.

Rewriting a project isn't an immense challenge, mostly -- however, doing it while users are actively banging against the existing system without service disruption?

That requires a lot of planning and coordination.

## Why Modernize?

I don't think we should update projects for the sake of the "latest and greatest". There's a reason common legacy systems like COBOL are still used. Valuable code doesn't lose its shine just because of age. There's a lot to be said for "code that works". Especially if it was built by hundreds of developers decades ago. There's a lot of hidden business logic model knowledge in there...

However, maintenance can often become the bottleneck. You might need to add features making the process untenable. It's hard to find something in millions of lines of code. The ability to leverage newer capabilities might be the final deciding factor. It might be possible to create a similar project without the same complexities, thanks to newer frameworks and tools.

We shouldn't take a decision to overhaul existing code that's in production lightly. You need to create a plan, evaluate the risks and have a way to back out.

Other reasons include security, scalability, end of life to systems we rely on, lack of skilled engineers, etc.

You usually shouldn't migrate for better tooling but better observability, orchestration, etc. Are a tremendous benefit.

Modernization gives you the opportunity to rethink the original system design. However, this is a risky proposition, as it makes it pretty easy to introduce subtle behavioral differences.

## Challenges

Before we head to preparations, there are several deep challenges we need to review and mitigate.

### 1. Access to Legacy Source Code

Sometimes, the source code of the legacy codebase is no longer workable. This might mean we can't add even basic features/functionality to the original project. This can happen because of many reasons (legal or technical) and would make migration harder. Unfamiliar code is an immense problem and would make the migration challenging, although possible.

It's very common to expose internal calls in the legacy system to enable smooth migration. E.g. we can provide fallback capabilities by checking against the legacy system. An old product I worked on had a custom in house authentication. To keep compatibility during migration, we used a dedicated web service. If user authentication failed on the current server, the system checked against the old server to provide a "seamless" experience.

This is important during the migration phase but can't always work. If we don't have access to the legacy code, tools such as scraping might be the only recourse to get perfect backwards compatibility during the migration period.

Sometimes, the source is no longer available or was lost. This makes preparation harder.

### 2. Inability to Isolate Legacy System

In order to analyze the legacy system, we need the ability to run it in isolation so we can test it and verify its behaviors. This is a common and important practice, but isn't always possible.

E.g. a COBOL codebase running on dedicated hardware or operating system. It might be difficult to isolate such an environment.

This is probably the biggest problem/challenge you can face. Sometimes an external contractor with domain expertise can help here. If this is the case, it's worth every penny!

Another workaround is to set up a tenant for testing. E.g. if a system manages payroll, set up a fake employee for testing and perform the tasks discussed below against production. This is an enormous danger and a problem, so this situation is far from ideal and we should take it only if no other option exists.

### 3. Odd Formats and Custom Stores

Some legacy systems might rely on deeply historical approaches to coding. A great example is COBOL. In it, they stored numbers based on their form and are closer to BCD (Java's BigDecimal is the closest example). This isn't bad. For financial systems, this is actually the right way to go. But it might introduce incompatibilities when processing numeric data that might prevent the systems from running in parallel.

Worse, COBOL has a complex file storage solution that isn't a standard SQL database. Moving away from something like that (or even some niche newer systems) can be challenging. Thankfully, there are solutions, but they might limit the practicality of running both the legacy and new product in parallel.

## Preparation

Before we need to even consider an endeavor of this type, we need to evaluate and prepare for the migration. The migration will be painful regardless of what you do, but this stage lets you shrink the size of the band aid you need to pull off.

There are many general rules and setups you need to follow before undergoing a code migration. Each one of these is something you need to be deeply familiar with.

### 1. Feature Extraction

When we have a long running legacy system, it's almost impossible to keep track of every feature it has and the role it plays in the final product. There are documents, but they are hard to read and go through when reviewing. Issue trackers are great for followup but they aren't great maps.

Discovering the features in the system and the ones that are "actually used" is problematic. Especially when we want to focus on minutiae. We want every small detail. This isn't always possible but if you can use observability tools to indicate what's used it would help very much. Migrating something that isn't used is frustrating and we'd want to avoid it, if possible.

This isn't always practical as most observability tools that provide very fine grained details are designed for newer platforms (e.g. Java, Python, Node etc). But if you have such a platform such as an old J2EE project. Using a tool like Lightrun and placing a [counter](https://docs.lightrun.com/actions/metrics/#counter) on a specific line can tell you what's used and what probably isn't. I discuss this further below.

I often use a spreadsheet where we list each feature and minor behavior. These spreadsheets can be huge and we might divide them based on sub modules. This is a process that can take weeks. Going over the code, documentation and usage. Then iterating with users of the application to verify that we didn't miss an important feature.

Cutting corners is easy at this stage. You might pay for them later. There were times I assigned this requirement to a junior software developer without properly reviewing the output. I ended up regretting that, as there were cases where we missed nuances within the documentation or code.

### 2. Compliance Tests

This is the most important aspect for a migration process. While unit tests are good, compliance and integration tests are crucial for a migration process.

We need feature extraction for compliance. We need to go over every feature and behavior of the legacy system and write a generic test that verifies this behavior. This is important both to verify our understanding of the code and that the documentation is indeed correct.

Once we have compliance tests that verify the existing legacy system, we can use them to test the compatibility of the new codebase.

The fundamental challenge is writing code that you can run against two completely different systems. E.g. If you intend to change the user interface, adapting these tests would be challenging.

I would recommend writing the tests using an external tool, maybe even using different programming languages. This encourages you to think of external interfaces instead of language/platform specific issues. It also helps in discovering "weird" issues. E.g. We had some incompatibilities because of minute differences in the HTTP protocol implementation between a new and legacy system.

I also suggest using a completely separate "thin" adapter for the UI differences. The tests themselves must be identical when running against the legacy and the current codebase.

The process we take for test authoring is to open an issue within the issue tracker for every feature/behavior in the spreadsheet from the previous step. Once this is done, we color the spreadsheet row in yellow.

Once we integrate a test and the issue is closed, we color the row green.

Notice that we still need to test elements in isolation with unit tests. The compliance tests help verify compatibility. Unit tests check quality and also complete much faster, which is important to productivity.

### 3. Code Coverage

Code coverage tools might not be available for your legacy system. However, if they are, you need to use them.

One of the best ways to verify that your compliance tests are extensive enough is through these tools. You need to do code reviews on every coverage report. We should validate every line or statement that isn't covered to make sure there's no hidden functionality that we missed.

### 4. Recording and Backup

If it's possible, record network requests to the current server for testing. You can use a backup of the current database and the recorded requests to create an integration test of "real world usage" for the new version. Use live data as much as possible during development to prevent surprises in production.

This might not be tenable. Your live database might be access restricted or it might be too big for usage during development. There's obviously privacy and security issues related to recording network traffic, so this is only applicable when it can actually be done.

### 5. Scale

One of the great things about migrating an existing project is that we have a perfect sense of scale. We know the traffic. We know the amount of data and we understand the business constraints.

What we don't know is whether the new system can handle the peak load througput we require. We need to extract these details and create stress tests for the critical portions of the system. We need to verify performance, ideally compare it to the legacy to make sure we aren't going back in terms of performance.

## Targets

Which parts should we migrate and in what way?

What should we target first and how should we prioritize this work?

### 1. Authentication and Authorization

Many older systems embed the authorization modules as part of a monolith process. This will make your migration challenging regardless of the strategy you take. Migration is also a great opportunity to refresh these old concepts and introduce a more secure/scalable approach for authorization.

A common strategy in cases like this is to send a user to "sign up again" or "migrate their accounts" when they need to use the new system. This is a tedious process for users and will trigger a lot of support issues e.g. "I tried password reset and it didn't work". These sorts of failures can happen when a user in the old system didn't perform the migration and tries to reset the password on the new system. There are workarounds such as explicitly detecting a specific case such as this and redirecting to the "migration process" seamlessly. But friction is to be expected at this point.

However, the benefit of separating authentication and authorization will help in future migrations and modularity. User details in the shared database is normally one of the hardest things to migrate.

### 2. Database

When dealing with the legacy system, we can implement the new version on top of the existing database. This is a common approach and has some advantages:

* Instant migration -- this is probably the biggest advantage. All the data is already in the new system with zero downtime
* Simple -- this is probably one of the easiest approaches to migration and you can use existing "real world" data to test the new system before going live

There are also a few serious disadvantages:

* Data pollution -- the new system might insert problematic data and break the legacy system, making reverting impossible. If you intend to provide a staged migration where both the old and new systems are running in parallel, this might be an issue
* Cache issues -- if both systems run in parallel, caching might cause them to behave inconsistently
* Persisting limits -- this carries over limitations of the old system into the new system

If the storage system is modern enough and powerful enough, the approach of migrating the data in this way makes sense. It removes, or at least postpones, a problematic part of the migration process.

### 3. Caching

The following three tips are at the root of application performance. If you get them right, your apps will be fast:

1. Caching
2. Caching
3. Caching

That's it. Yet very few developers use enough caching. That's because proper caching can be very complicated and can break the single source of knowledge principle. It also makes migrations challenging, as mentioned in the above section.

Disabling caching during migration might not be a realistic option, but reducing retention might mitigate some issues.

## Strategy

There are several ways we can address a large-scale migration. We can look at the "big picture" in a migration e.g. Monolith to Microservices. But more often than not, there are more nuanced distinctions during the process.

I'll skip the obvious "complete rewrite" where we instantly replace the old product with the new one. I think it's pretty obvious and we all understand the risks/implications.

### 1. Module by Module

If you can pick this strategy and slowly replace individual pieces of the legacy code with new modules, then this is the ideal way to go. This is also one of the biggest selling points behind microservices.

This approach can work well if there's still a team that manages and updates the legacy code. If one doesn't exist, you might have a serious problem with this approach.

### 2. Concurrent Deployment

This can work for a shared database deployment. We can deploy the new product to a separate server, with both products using the same database as mentioned above. There are many challenges with this approach, but I picked it often, as it's probably the simplest one to start with.

Since the old product is still available, there's a mitigation workaround for existing users. It's often recommended to plan downtime for the legacy servers to force existing users to migrate. Otherwise, in this scenario, you might end up with users who refuse to move to the new product.

### 3. Hidden Deployment

In this strategy, we hide the existing product from the public and set up the new system in its place. In order to ease migration, the new product queries the old product for missing information.

E.g. if a user tried to login and didn't register in the system, the code can query the legacy system to migrate the user seamlessly. This is challenging and ideally requires some changes to the legacy code.

The enormous benefit is that we can migrate the database while keeping compatibility and without migrating all the data in one fell swoop.

A major downside is that this might perpetuate the legacy code's existence. It might work against our development goals as a result of that.

## Implementation

You finished writing the code. We're ready to pull the trigger and do the migration... Now we need to update the users that the migration is going to take place. You don't want an angry customer complaining that something suddenly stopped working.

### 1. Rehearsal

If possible, perform a dry run and prepare a script for the migration process. When I say script, I don't mean code. I mean a script of responsibilities and tasks that need to be performed.

We need to verify that everything works as the migration completes. If something is broken, there needs to be a script to undo everything. You're better off retreating to redeploy another day. I'd rather have a migration that fails early that we can "walk back" from than having something "half-baked" in production.

### 2. Who?

In my opinion you should use a smaller team for the actual deployment of the migrated software. Too many people can create confusion. You need the following personnel on board:

* IT/OPS -- to handle the deployment and reverting if necessary
* Support -- to field user questions and issues. Raise flags in case a user reports a critical error
* Developers -- to figure out if there are deployment issues related to the code
* Manager -- we need someone with instant decision-making authority. No one wants to pull a deployment. We need someone who understands what's at stake for the company

There's a tendency to make a code fix to get the migration through. This works OK for smaller startups and I'm pretty guilty of that myself. But if you're working at scale, there's no way to do it. A code change done "on the spot" can't pass the tests and might introduce terrible problems. It's probably a bad idea.

### 3. When?

The axiom "don't deploy on a Friday" might be a mistake for this case. I find Fridays are a great migration period when I'm willing to sacrifice a weekend. Obviously I'm not advocating forcing people to work the weekend. But if there's interest in doing this (in exchange for vacation time) then low traffic days are the ideal time for making major changes.

If you work in multiple time zones, developers in the least active time zone might be best to handle the migration. I would suggest having teams in all time zones to keep track of any possible fallout.

Agility in these situations is crucial. Responding to changes quickly can make the difference between reverting a deployment and soldering on.

### 4. Staged Rollout

With small updates, we can stage our releases and push the update to a subset of users. Unfortunately, when we do a major change, I find it more of a hindrance. The source of errors becomes harder to distinguish if you have both systems running. Both systems need to run concurrently, and it might cause additional friction.

## Post Migration

A couple of weeks had passed, things calmed down, and the migration worked. Eventually.

Now what?

### 1. Retirement Plan

As part of the migration, we brought with us a large set of features from legacy. We probably need some of them, while some others might not be necessary. After finishing the deployment, we need to decide on the retirement plan. Which features that came from legacy should be retired and how?

We can easily see if we use a specific method or if it's unused in the code. But are the users using a specific line of code? A specific feature?

For that, we have observability.

We can go back to the feature extraction spreadsheet and review every potential feature. Then use observability systems to see how many users invoke a feature. We can easily do that with tools like [Lightrun](https://lightrun.com/) by placing a [counter metric](https://docs.lightrun.com/metrics/#counter) in the code (you can download it for free [here](https://lightrun.com/free)). According to that information, we can start narrowing the scope of features used by the product. I discussed this before so it might not be as applicable if this functionality worked in the legacy system.

Even more important is the retirement of the running legacy. If you chose a migration path in which the legacy implementation is still running, this is the time to decide when to pull the plug. Besides costs, the security and maintenance problems make this impractical for the long run. A common strategy is to shut down the legacy system periodically for an hour to detect dependencies/usage we might not be aware of.

Tools such as network monitors can also help gauge the level of usage. If you have the ability to edit the legacy or a proxy into the legacy this is the time to collect data about the usage. Detect the users that still depend on that and plan the email campaign/process for moving them on.

### 2. Use Tooling to Avoid Future Legacy

A modern system can enjoy many of the newer capabilities at our disposal. CI/CD processes include sophisticated linters that detect security issues, bugs and perform reviews that are far superior to their human counterparts. A code quality tool can make a vast difference to the maintainability of a project.

Your product needs to leverage these new tools so it won't deteriorate back to legacy code status. Security patches get delivered "seamlessly" as pull requests. Changes get implicit reviews to eliminate common mistakes. This enables easier long-term maintenance.

### 3. Maintaining the Compliance Testing

After the migration process, people often discard the compliance tests. It makes sense to convert them to integration tests if possible/necessary, but if you already have integration tests, it might be redundant and harder to maintain than your standard testing.

The same is true for the feature extraction spreadsheet. It's not something that's maintainable and is only a tool for the migration period. Once we're done with that, we should discard it and we shouldn't consider it as authoritative.

## Finally

Migrating old code is always a challenge, as agile practices are crucial when taking on this endeavor.

There are so many pitfalls in the process and so many points of failure.

This is especially true when that system is in production and the migration is significant.

I hope this list of tips and approaches will help guide your development efforts.

I think pain in this process is unavoidable. So is some failure. Our engineering teams must be agile and responsive to such cases. Detect potential issues and address them quickly during the process.

There's a lot more I could say about this, but I want to keep it general enough so it will apply to wider cases. If you have thoughts on this, reach out to me on [twitter](https://twitter.com/debugagent) to let me know.
