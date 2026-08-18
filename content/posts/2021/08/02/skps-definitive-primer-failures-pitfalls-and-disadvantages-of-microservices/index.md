---
title: "SKP's Primer: Failures, Pitfalls, and Disadvantages of Microservices"
date: "2021-08-02T09:35:44+00:00"
lastmod: "2021-10-11T14:04:44+00:00"
description: "Pitfalls, failures, and disadvantages of microservices, a one stop reference and gateway for principal architects for decisions of go/no-go."
authors:
  - "sumith-puri"
image: "microservices.png"
categories:
  - "DevOps"
  - "Microservices"
  - "Use Cases"
related_posts:
  - "can-java-jakarta-ee-do-microservices"
  - "idempotent-spring-boot-starter"
  - "effortless-updates-zero-migration"
  - "get-recognized-for-your-cloud-native-java-development-skills-with-this-new-badge"
frozen: false
---

**\[About SKP's Definitive Microservices Primer\]**  
Series of Articles on Microservices Architecture. They Revolve Around Microservices Strategy, Decision Making, Monolithic vs Microservices, Microservices Infrastructure, Failures \& Pitfalls of Microservices, Microservices Design Patterns, Database Design, Security, Logging, Messaging, Containerization, Cloud Providers and Everything that is Microservices and More!  

## Intro to Microservices Adoption in Real World

Microservices Adoption is almost becoming the normal in almost all software product and it services organizations. Hereby, i am taking this opportunity to make you aware of the Real-World Issues (Case Studies) that are faced by organizations (small, mid, large) of all sizes in migrating or adopting microservices. Since learning from other's mistakes and knowing the pitfalls upfront help us prepare and plan better - this article will be helpful for those planning a shift to the Microservices Strategy organization-wide or simply making a move from Monolithic to Microservices Architecture.

It is mandatory that all of you skim through the below articles (hyperlinks) and then read a few articles that you find are closest to your job function. In this article, i am providing only the brief explanation of the main challenges of adopting Microservices. Also, you may use this as a ready reference index for articles published on the Internet to further your knowledge fo the pitfalls of Adopting Microservices.

Adopting Microservices has Been a Great Challenge for All -- Netflix, Amazon, Uber and even IT Service Companies have had their share of Challenges in Microservices Migration/Adoption -- Thoughtworks, Cognizant, IBM, Infosys, Xebia. Few Companies have Reverted Back to Monolith after their Microservices Strategy / Implementation Failed or Did Not Yield the Desired Gains.

\[So, The Idea is that You Do Not Make the Same Mistakes, Learn from the Below and Try to Get it Right the First Time -- Hence, We Need to Invest Time in Quality Research and Development to build the Initial and Robust Architectural Pieces. Also, To Create a Meaningful and a More Realistic Plan for Organization-Wide Microservices Adoption in a Software Product Company. These are also valid for Adoption / Evangelizing to your Customers in an IT Services Organization.\]

## Practical Challenges in Adoption of Microservices

#### **Overhead**

The Setup of Microservices Requires More Efforts as there a lot of Infrastructural Architectural pieces as part of a Complete Microservices Architecture. For Example - API Gateway, Circuit Breaker, Discovery Server, Config Server, Health Monitoring, Vault Service, Token Service, Logging \& Auditing - These are just a few of the Initial Infrastructural Services Required to Correctly Implement a Complete Microservices Architecture in the Enterprise.

#### **High Skill**

There is a High Skill Level Require Especially in terms of Experienced Senior Architects, DevOps and Developers who can now understand the change in Development Paradigm. Also, This may either increase the Development Time or Development Costs or Both.

#### **Complexity**

One of the most Important Disadvantages of Microservices is the complexity Involved. Even though the Point is captured in the above 2 Points - It still requires a mention on its own. The Interplay of the plethora of Infrastructural Services along with the actual Application Microservices may create unending cycle(s) of Complexity.

#### **Redundancy**

During Development, the Architects and Lead Engineers may need to keep a watch on making sure that there is No Duplication of Code, Configuration or any other Effort that may already have been Developed by another Team. This starts becoming a greater concern when there are Geographically Distributed Teams and Focussing Entirely on their own set of Business or Product Deliverables. Though some very Trivial Code Duplication may be inevitable - As far as Possible, Discipline will be required to Isolate and group Common Functionality or Common Code Together and also, Correctly.

#### **Data Duplication**

One of the greatest challenge is Data Duplication. Most Inexperienced Architects and Engineers may end up in such situations. It is because the thought that was required to Build Monolith Data Access and Creation cannot Now be used for Microservices. The mechanism to maintain Data Integrity and Data Consistency in a Product to make sure that there exists a Correct 'Single Version of Truth'. This needs to be Planned, Architected and Infused into the Design.

#### **Communication**

The communication between External World and Microservices or within Microservices may fail. This could be due to Multiple Reasons including Network Failure and Service Unavailability. In any case, the Communication between Microservices is critical and is tough to Setup and Maintain. It also requires Constant Monitoring of Health and Performance of each of the Microservices. Partial Failures is actually an advantage of Microservices - But still the Monitoring, Failure Detection, Failure Handling and simply the Internal/External Communication can be Quite an Overhead in the Implementation of Microservices.

#### **Maintenance**

So, Once the Initial Part is Over - Even If the Microservices Strategy and Implementation Turns out to be a Success - It still means a Heavy Hit on the Pocket for Maintenance. Even though all of the Current Generation Organizations Rely on an External Cloud Provider, It still means more Expenditure on Hardware, 'Software', Cloud, DevOps Engineers. Also, the Total Cost of Ownership (TCO) of Such an Infrastructural Masterpiece is only bound to Increase as More Abstractions are added over Time - Also, the Maintenance itself could be a complicated process.

[![](https://1.bp.blogspot.com/-sQgs_D2r0gM/YNb_PUeOkYI/AAAAAAAA9dU/TcRqW71TP6wYIN78FBDsVLO4_qyzTmlwQCLcBGAsYHQ/w640-h350/skp_microservices_complexity_001.png)](https://1.bp.blogspot.com/-sQgs_D2r0gM/YNb_PUeOkYI/AAAAAAAA9dU/TcRqW71TP6wYIN78FBDsVLO4_qyzTmlwQCLcBGAsYHQ/w640-h350/skp_microservices_complexity_001.png)

**FIG. 1** : INDICATIVE COMPLEXITY OF MICROSERVICES ARCHITECTURE IN THE ENTERPRISE

## Articles from Online Developer Magazines (By Topic)

### Challenges of Moving from Monolithic to Microservices

[Top 3 Challenges of Adopting Microservices as Part of Your Cloud Migration](https://devops.com/top-3-challenges-of-adopting-microservices-as-part-of-your-cloud-migration/ "Top 3 Challenges of Adopting Microservices as Part of Your Cloud Migration")

[From Monolith to Microservices: Horror Stories and Best Practices](https://techbeacon.com/app-dev-testing/monolith-microservices-horror-stories-best-practices "From Monolith to Microservices: Horror Stories and Best Practices")

[Advantages and Disadvantages of Microservices Architecture](https://cloudacademy.com/blog/microservices-architecture-challenge-advantage-drawback/ "Advantages and Disadvantages of Microservices Architecture")

[Fearless Monolith to Microservices Migration -- A Guided Journey](https://www.dynatrace.com/news/blog/fearless-monolith-to-microservices-migration-a-guided-journey/ "Fearless Monolith to Microservices Migration – A Guided Journey")

### Failure and Pitfalls of the Microservices Strategy

[Experiences from Failing with Microservices](https://www.infoq.com/news/2014/08/failing-microservices/ "Experiences from Failing with Microservices")

[How To Succeed at Failure with Microservices](https://thenewstack.io/succeed-failure-microservices/ "How To Succeed at Failure with Microservices")

[How to Condemn Your Microservices Architecture to Fail](https://blog.couchbase.com/condemn-microservices-architecture-fail-even-start/ "How to Condemn Your Microservices Architecture to Fail")

[Your Microservice Architecture Will Collapse](https://dzone.com/articles/your-microservice-architecture-will-collapse "Your Microservice Architecture Will Collapse")

### Learn from Others Mistakes in Microservices

[5 Big Microservices Pitfalls to Avoid During Migration](https://searchapparchitecture.techtarget.com/tip/5-big-microservices-pitfalls-to-avoid-during-migration "5 Big Microservices Pitfalls to Avoid During Migration")

[Tech Talks With Tom Smith: Issues in Migrating to Microservices](https://dzone.com/articles/issues-in-migrating-to-microservices "Tech Talks With Tom Smith: Issues in Migrating to Microservices")

[11 Reasons Why You Are Going To Fail With Microservices](https://medium.com/xebia-engineering/11-reasons-why-you-are-going-to-fail-with-microservices-29b93876268b "11 Reasons Why You Are Going To Fail With Microservices")

[Failing at Microservices - Please Avoid Our Mistakes!](https://rclayton.silvrback.com/failing-at-microservices "Failing at Microservices - Please Avoid Our Mistakes!")

### Planning a Microservices Adoption in the Enterprise

[Pitfalls \& Challenges Faced During a Microservices Architecture Implementation](https://www.cognizant.com/whitepapers/pitfalls-and-challenges-faced-during-a-microservices-architecture-implementation-codex5066.pdf "Pitfalls &amp; Challenges Faced During a Microservices Architecture Implementation")

[4 Challenges You Need to Address with Microservices Adoption](https://www.appdynamics.com/blog/product/4-challenges-you-need-to-address-with-microservices-adoption/ "4 Challenges You Need to Address with Microservices Adoption")

[Challenges when Implementing Microservices](https://developer.ibm.com/technologies/microservices/articles/challenges-and-benefits-of-the-microservice-architectural-style-part-1/ "Challenges when Implementing Microservices")

[How to Plan a Microservices Migration](https://medium.com/@philsarin/how-to-plan-a-microservices-migration-9b4ac50dfa95 "How to Plan a Microservices Migration")

[Fearless Monolith to Microservices Migration -- A Guided Journey](https://www.dynatrace.com/news/blog/fearless-monolith-to-microservices-migration-a-guided-journey/ "Fearless Monolith to Microservices Migration – A Guided Journey")

### Real-World Case Studies of Microservices Adoption

[Why and How Netflix, Amazon, and Uber Migrated to Microservices: Learn from Their Experience](https://www.hys-enterprise.com/blog/why-and-how-netflix-amazon-and-uber-migrated-to-microservices-learn-from-their-experience/ "Why and How Netflix, Amazon, and Uber Migrated to Microservices: Learn from Their Experience")

[Explore UBER's Microservice Architecture](https://medium.com/edureka/microservice-architecture-5e7f056b90f1 "Explore UBER’s Microservice Architecture")

[Why Microservices Fail? --- Xebia Blog](https://xebia.com/blog/why-microservices-fail/ "Why Microservices Fail? — Xebia Blog")

### Is it the Death of Microservices - Already, In 2021 ?

[Microservices to Not Reach Adopt Ring in ThoughtWorks Technology Radar](https://www.infoq.com/news/2018/06/microservices-adopt-radar/ "Microservices to Not Reach Adopt Ring in ThoughtWorks Technology Radar")

[The Death of Microservice Madness in 2018](https://dwmkerr.com/the-death-of-microservice-madness-in-2018/ "The Death of Microservice Madness in 2018")

[Goodbye Microservices: From 100s of Problem Children to 1 Superstar](https://segment.com/blog/goodbye-microservices/ "Goodbye Microservices: From 100s of Problem Children to 1 Superstar")

[Unraveling Microservices: Higher Agility or Hype?](https://www.infosys.com/digital/insights/documents/higher-agility-hype.pdf "Unraveling Microservices: Higher Agility or Hype?")

## Conclusion and 'Gyan' for Real-World Adoption

Let us take a look a Uber's Microservices Dependency Graph (Source : Online Presentation by an Uber Engineer - All Rights Reserved by Uber - Solely Used to Demostrate Complexity). It seems like an Outer Space Image from a Hubble Telescope! 🙂

[![](https://dzone.com/storage/temp/14889462-skp-uber-microservices-depedency-graph-2021-compre.jpg)](https://dzone.com/storage/temp/14889462-skp-uber-microservices-depedency-graph-2021-compre.jpg)

**FIG. 2** : REAL-WORLD MICROSERVICES DEPENDENCY GRAPH FROM UBER ENGINEERING

Idea is not to cause Paranoia or Pessimism -- But to Better Estimate and Plan for Success. The Microservices Animal isn't like our Regular Monolithic Technology Evaluations -- Only working together in a focussed way, We can tame it Decisively. Else, We may end up in Being Unable to Achieve Desired Functionality, Cycles of Rework, Poor Architecture, High Data Redundancy, Questionable Data Integrity, Performance Worser than Monolith among Other Issues -- We may even have to Revert Back / Miserably Fail.

Individually, I have worked in 3 Organizations where Microservices Adoption or Migration was going on -- As far as i know, the first project was shelved after about 4-6 Months -- In the second one, 'Real Microservices' did not go ahead even after about 1.5 Years (Including the time of 6 months after my Joining) -- It was far from a Microservices Readiness or Certification. In the 3rd Organization, where the majority or meat of my experience for this article comes from - I was in charge of Technically Leading the Microservices Strategy, in the role of a Principal Architect. Until my Tenure in the Organization, We had Upfront Identified Major Issues in Architecture by Group Review Processes. We had also Improved Upon Existing Implementation by Iterating through Cycles of Refinement for Infrastructure Architecture, DevOps Decisions, Technology Evaluations, Creating Reference Implementations and Deciding Release Branches. All this was possible via Regular Architecture Demos and Planning for Architecture Validation and deciding to roll out the Reference Architecture Implementation for General Developer Usage in Product Development.

Kindly take time to Internalize these thoughts as Microservices Architecture is the Current Big Bet for Software Product Organizations and IT Services Organizations in 2021. It will continue to be the same for a few years from now! Organizations are going to spend a maximum part of their IT/R\&D Budget in Adopting Microservices - So, Upfront Identification by Stakeholders will give the Bang for the Buck!

So, Here's Wishing you a Lot of Skill, Focus, Pragmatism, Understanding, Reasoning and Continued Experience to get the Best Out of Microservices for your Organization and Needs!

#### **Disclaimer**

Please Note that the Content in the Links Provided in this Article is the Sole Property of the Respective Authors and their Publishers.
