---
title: "A Walk to Lazy Fetching With Hibernate and Spring Data JPA"
slug: "a-walk-to-lazy-fetching-with-hibernate-and-spring-data-jpa"
date: "2021-11-17T11:47:30+00:00"
lastmod: "2023-08-28T12:40:10+00:00"
description: "Using FetchType.EAGER is a very bad practice, since our services may not require all the data of the mapped entities in all cases."
authors:
  - "mainuls18"
image: "https://foojay.io/wp-content/uploads/2021/11/oie_koKWLHXsUxuL.jpg"
categories:
  - "Spring"
tags:
related_posts:
  - "a-simple-service-with-spring-boot"
  - "annotation-free-spring"
  - "better-error-handling-for-your-spring-boot-rest-apis"
enlighterjs: true
frozen: false
---

<figure class="wp-block-image is-resized">
 <img fetchpriority="high" decoding="async" src="/images/posts/2021/11/a-walk-to-lazy-fetching-with-hibernate-and-spring-data-jpa/oie_koKWLHXsUxuL.jpg" alt="" style="width:452px;height:226px" width="452" height="226">
</figure>

### Background {#h3-0-background}

Using `FetchType.EAGER` is a very bad practice, since our services may not require all the data of the mapped entities in all cases. And moreover, it is a bad idea to fetch so much data in a single session and makes the session heavy.

Therefore we will refactor the code and use `FetchType.LAZY`.

Our old code for the entity was below:
![Domain model with fetch type EAGER](/images/posts/2021/11/a-walk-to-lazy-fetching-with-hibernate-and-spring-data-jpa/1_Tr3xvq9hSvqoK3LHNaib6g-445x510.png)

After using FetchType.LAZY our new code will be like below:
![Domain model with fetch type LAZY](/images/posts/2021/11/a-walk-to-lazy-fetching-with-hibernate-and-spring-data-jpa/1_Tr3xvq9hSvqoK3LHNaib6g-1-441x510.png)

But now when we try to build our project, we get a new problem. We have the LazyInitializationException.
![LazyInitializationException](/images/posts/2021/11/a-walk-to-lazy-fetching-with-hibernate-and-spring-data-jpa/1_Tr3xvq9hSvqoK3LHNaib6g-2-700x177.png)

### Troubleshooting LazyInitializationException {#h3-1-troubleshooting-lazyinitializationexception}

There might be found many suggestions over online. But my preferred one (I found it really effective) is using something called EntityGraph.
> A point to note, we can do it in 2 ways.

1. One is using the annotations @NamedEntityGraphs, @NamedEntityGraph and @NamedAttributeNode annotations, which are provided by javax.persistence package.
2. Using @EntityGraph annotation in repository interfaces. This annotation is provided by Spring Data JPA.

We will see both approaches here.

**Using javax.persistence Provided Annotations** {#h2-2-using-javax-persistence-provided-annotations}
-----------------------------------------------------------------------------------------------------

We will annotate our above mentioned Status entity with the annotations shown below.
![Annotating the Entity with @NamedEntityGraph](/images/posts/2021/11/a-walk-to-lazy-fetching-with-hibernate-and-spring-data-jpa/1_Tr3xvq9hSvqoK3LHNaib6g-3-700x294.png)

**Explanation:**   

Here, @NamedEntityGraphs annotation accepts an array of @NamedEntityGraph. So we can define as many entity graphs as we need.

In the @NamedEntityGraph annotation we have defined a name which we will call later. An "attributeNodes" attribute which we will use to mention the mapped entities we want to join for this named entity graph.

Notice that here we have only used the attributes owner and locations in our @NamedAttributeNode annotation, meaning that when we will call this named entity graph, the joined data will be fetched for these 2 attributes only and we will get no data for the attachments attribute.

**Calling the named entity graph:**

To call this named entity graph in our query, we have added the following additional lines (line 96, 97 and 101) in our existing code.
![Calling the named entity graph](/images/posts/2021/11/a-walk-to-lazy-fetching-with-hibernate-and-spring-data-jpa/1_Tr3xvq9hSvqoK3LHNaib6g-4-700x433.png)

**Using Spring Data JPA Provided @EntityGraph Annotation** {#h2-3-using-spring-data-jpa-provided-entitygraph-annotation}
------------------------------------------------------------------------------------------------------------------------

Let's see the entity first.
![](/images/posts/2021/11/a-walk-to-lazy-fetching-with-hibernate-and-spring-data-jpa/1_Tr3xvq9hSvqoK3LHNaib6g-5-455x510.png)

We have an entity which has a List of String Roles, mapped as @ElementCollection. Its fetch type is FetchType.LAZY.

Now let's see our UserRepository interface.
![](/images/posts/2021/11/a-walk-to-lazy-fetching-with-hibernate-and-spring-data-jpa/1_Tr3xvq9hSvqoK3LHNaib6g-6-700x158.png)

Here we have used the @EntityGraph annotation and provided the attribute name which we want to fetch, in attributePaths parameter. And it is that simple to do the whole stuff.

The code for javax.persistence provided annotations can be found in this [github repository](https://github.com/mainul35/social-community "github repository").
