---
title: "Building for Failure: Best Practices for Easy Production Debugging"
date: "2023-07-21T15:29:09+00:00"
lastmod: "2023-07-21T15:29:11+00:00"
description: "Applications fail, that's inevitable. Testing, QA, CI and defensive programming can make this a rare occurrence, but can't eliminate failure."
canonical: "https://debugagent.com/building-for-failure-best-practices-for-easy-production-debugging"
authors:
  - "shai-almog"
image: "Building-for-Failure-Best-Practices-for-Easy-Production-Debugging.jpg"
categories:
  - "Developer Tools"
  - "Java Core"
  - "Tutorials"
related_posts:
  - "understanding-security-vulnerabilities-a-first-step-in-preventing-attacks"
  - "manifold-vs-lombok-enhancing-java-with-property-support"
  - "operator-overloading-in-java"
frozen: false
---

**Before going into the content of this post, check out** [**my new book**](https://www.amazon.com/Java-21-Explore-cutting-edge-features-ebook/dp/B0C9WW8PFL/) **which launched last week!**

Quite a few years ago I was maintaining a database-driven system and ran into a weird production bug.

The column I was reading from had a null value, but this wasn't allowed in the code and there was no place where that value could have been null.

The database was corrupt in a bad way and we didn't have anything to go on.

Yes, there were logs.

But due to privacy concerns you can't log everything, even if we could, how would we know what to look for?

{{< youtube uz06__w2mjI >}}

Programs fail, that's inevitable. We strive to reduce failures but failure will happen. We also have another effort and it gets less attention: failure analysis. There are some best practices and common approaches, most famously logging. I've often said before that logs are pre-cognitive debugging, but how do we create an application that's easier to debug?

How do we build the system so that when it fails like that we would have a clue of what went wrong?

A common military axiom goes "Difficult training makes combat easy". Assuming the development stage is the "training", any work we do here will be harder as we don't yet know the bugs we might face in production. But that work is valuable as we arrive prepared for production.

This preparation goes beyond testing and QA. It means preparing our code and our infrastructure for that point where a problem occurs. That point is where both testing and QA fail us. By definition, this is preparation for the unexpected.

## Defining a Failure

We first need to define the scope of a failure. When I talk about production failures people automatically assume crashes, websites going down and disaster-level events. In practice those are rare. The vast majority of these cases are handled by OPS and system engineers.

When I ask developers to describe the last production problem they ran into they often stumble and can't recall. Then upon discussion and querying it seems that a recent bug they dealt with was indeed reported by a customer, in production. They had to somehow reproduce it locally or review information to fix it. We don't think of such bugs as production bugs but they are. The need to reproduce failures that already happened in the real world makes our job harder.

What if we could understand the problem just by looking at the way it failed, right in production?

## Simplicity

The rule of simplicity is common and obvious but people use it to argue both sides. Simple is subjective. Is this block of code simple?

```
return obj.method(val).compare(otherObj.method(otherVal));
```

Or is this block simple?

```
var resultA = obj.method(val);
var resultB = otherObj.method(otherVal);
return resultA.compare(resultB);
```

In terms of lines of code, the first example seems simpler and indeed many developers will prefer that. This would probably be a mistake. Notice that the first example includes multiple points of failure in a single line. The objects might be invalid. There are three methods that can fail. If a failure occurs it might be unclear what part failed.

Furthermore, we can't log the results properly. We can't debug the code easily as we would need to step into individual methods. If a failure occurs within a method the stack trace should lead us to the right location even in the first example. Would that be enough?

Imagine if the methods we invoked there changed state. Was `obj.method(val)` invoked before `otherObj.method(otherVal)`?

With the second example, this is instantly visible and hard to miss. Furthermore, the intermediate state can be inspected and logged as the values of resultA and resultB.

Let's inspect a common example:

```
var result = list.stream()
                 .map(MyClass::convert)
                 .collect(Collectors.toList());
```

That's a pretty common code that is similar to this code:

```
var result = new ArrayList<OtherType>();
for(MyClass c: list) {
    result.add(c.convert());
}
```

There are advantages to both approaches in terms of debuggability and our decision can have a significant impact on the long-term quality. A subtle change in the first example is the fact that the returned list is unmodifiable. This is a boon and a problem. Unmodifiable lists fail at runtime when we try to change them, that's a potential risk of failure. However, the failure is clear. We know what failed.

A change to the result of the second list can create a cascading problem but might also simply solve a problem without failing in production.

Which should we pick?

The read-only list is a major advantage. It promotes the fail-fast principle which is a major advantage when we want to debug a production issue. When failing fast we reduce the probability of a cascading failure. Those are the worst failures we can get in production as they require a deep understanding of the application state which is complex in production.

When building big applications the word "robust" gets thrown around frequently. Systems should be robust, but they should offer that outside of your code which should fail fast.

## Consistency

In my talk about logging best practices, I mention the fact that every company I ever worked for had a style guide for code, or at least aligned with a well-known style. Very few had a [guide for logging](https://www.youtube.com/watch?v=CFL--dAX3FQ), where should we log, what should we log etc. This is a sad state of affairs.

We need consistency that goes deeper than code formatting. When debugging we need to know what to expect. If specific packages are prohibited from use I would expect this to apply to the entire code base. If a specific practice in coding is discouraged I'd expect this to be universal.

Thankfully, with CI these consistency rules are easy to enforce without burdening our review process. Automated tools such as SonarQube are pluggable and can be extended with custom detection code. We can tune these tools to enforce our set of consistent rules to limit usage to a particular subset of the code or require a proper amount of logging.

Every rule has an exception, we shouldn't be bound to overly strict rules. That's why the ability to override such tools and merge a change with a developer review is important.

## Double Verification

Debugging is the process of verifying assumptions as we circle the area of the bug. Typically this happens very quickly. We see what's broken, verify and fix it. But sometimes we spend an inordinate amount of time tracking a bug. Especially a hard-to-reproduce bug or a bug that only manifests in production.

As a bug becomes elusive it's important to take a step back, usually, it means that one of our assumptions was wrong. In this case, it might mean that the way in which we verified the assumption was faulty. The point of double verification is to test the assumption that failed using a different approach to make sure the result is correct.

Typically we want to verify both sides of the bug, e.g. let's assume I have a problem in the backend. It would express itself via the front end where data is incorrect. To narrow the bug I initially made two assumptions:

* The front end displays the data correctly from the backend
* The database query returned the right data

To verify these assumptions I can open a browser and look at the data. I can inspect responses with the web developer tools to make sure the data displayed is what the server query returned. For the backend, I can issue the query directly against the database and see if the values are the correct ones.

But that's only one way of verifying this data. Ideally, we would want a second way. What if a cache returned the wrong result? What if the SQL made the wrong assumption?

The second way should ideally be different enough so it wouldn't simply repeat the failures of the first way. For the front-end code our knee-jerk reaction would be to try with a tool like cURL. That's good and we probably should try that. But a better way might be to look at logged data on the server or invoke the WebService that underlies the front end.

Similarly, for the backend, we would want to see the data returned from within the application. This is a core concept in observability. An observable system is a system for which we can express questions and get answers. During development, we should aim our observability level at two different ways to answer a question.

### Why not Three Ways to Verify?

We don't want more than two ways because that would mean we're observing too much, and as a result, our costs can go up while performance goes down. We need to limit the information we collect to a reasonable amount. Especially given the risks of personal information retention which is an important aspect to keep in mind!

Observability is often defined through its tools, pillars or similar surface area features. This is a mistake. Observability should be defined by the access it provides us. We decide what to log and what to monitor. We decide the spans of the traces. We decide the granularity of the information and we decide whether we wish to deploy a developer observability tool.

We need to make sure that our production system will be properly observed. To do that we need to run failure scenarios and possibly chaos game days. When running such scenarios we need to think about the process of solving the issues that come up. What sort of questions would we have for the system, how could we answer such a question?

E.g. When a particular problem occurs we would often want to know how many users were actively modifying data in the system. As a result, we can add a metric for that information.

### Verifying with Feature Flags

We can verify an assumption using observability tools but we can also use more creative verification tools. One unexpected tool is the feature flag system. A feature flag solution can often be manipulated with very fine granularity, we can disable or modify a feature only for a specific user, etc.

This is very powerful, we can toggle a feature that could provide us with verification of a specific behavior if that specific code is wrapped in a flag. I don't suggest spreading feature flags all over the code, but the ability to pull levers and change the system in production is a powerful debugging tool that is often underutilized as such.

## Bug Debriefs

Back in the 90s I developed flight simulators and worked with many fighter pilots. They instilled in me a culture of debriefing. Up until that point, I thought of these things only for discussing failures but fighter pilots go to debrief immediately after the flight, whether it is successful or a failed mission.

There are a few important points we need to learn here:

* Immediate - we need this information fresh in our minds. If we wait some things get lost and our recollection changes significantly.
* On Success and Failure - Every mission gets things right and wrong. We need to understand what went wrong and what went right. Especially in successful cases.

When we fix a bug we just want to go home, we often don't want to discuss it anymore. Even if we do want to "show off" it's often our broken recollection of the tracking process. By conducting an open discussion of what we did right and wrong… with no judgment. We can create an understanding of our current status. This information can then be used to improve our results when tracking issues.

Such debriefs can point at gaps in our observability data, inconsistencies and problematic processes. A common problem in many teams is indeed in the process. When an issue is raised it is often:

* Encountered by the customer
* Reported to support
* Checked by ops
* Passed to R\&D

If you're in R\&D you're four steps away from the customer and receive an issue that might not include the information you need. Refining these processes isn't a part of the code, but we can include tools within the code to make it easier for us to locate a problem. A common trick is to add a unique key to every exception object. This propagates all the way to the UI in case of a failure.

When a customer reports an issue there's a good possibility they will include the error key which R\&D can find within the logs. These are the types of process refinements that often rise through such debriefs.

## Review Successful Logs and Dashboards

Waiting for failure is a problematic concept. We need to review logs, dashboards, etc. regularly both to track potential bugs that aren't manifesting, but also to get a sense of a "baseline". What does a healthy dashboard or log look like…

We have errors in a normal log, if during a bug hunt we spend time looking at a benign error, then we're wasting our time. Ideally, we want to minimize the amount of these errors as they make the logs harder to read. The reality of server development is that we can't always do that. But we can minimize the time spent on this through familiarity and proper source code comments.

I went into more detail in the logging best practices post and [talk](https://www.youtube.com/watch?v=CFL--dAX3FQ).

## Final Word

A couple of years after founding Codename One our Google App Engine bill suddenly jumped to a level that would trigger bankruptcy within days. This was a sudden regression due to a change on their backend.

This was caused because of uncached data but due to the way App Engine worked at the time there was no way to know the specific area of the code triggering the problem. There was no ability to debug the problem and the only way to check if the issue was resolved was to deploy a server update and wait a lot…

We solved this through dumb luck. Caching everything we could think of in every single place. To this day I don't know what triggered the problem and what solved it.

What I do know is this:

I made a mistake when I decided to pick "App Engine". It didn't provide proper observability and left major blind spots. Had I taken the time before the deployment to review the observability capabilities I would have known that. We lucked out but I could have saved a lot of our cash early on had we been more prepared.
