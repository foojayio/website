---
title: "Leverage the Richness of HTTP Status Codes"
slug: "leverage-the-richness-of-http-status-codes"
date: "2023-05-02T09:34:43+00:00"
lastmod: "2023-05-02T09:34:44+00:00"
description: "While many discussions about REST focus on entities and methods, using the correct response status codes can make your API stand out."
authors:
  - "nicolas-frankel"
image: "money-2724241_1280.jpg"
categories:
  - "Opinion"
tags:
related_posts:
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "7-functional-programming-techniques-in-java-a-primer"
  - "5-tips-to-create-secure-docker-images-for-java-applications"
  - "quest-for-rest"
enlighterjs: true
frozen: false
---

If you're not a REST expert, you probably use the same HTTP codes over and over in your responses, mostly 200, 404, and 500.

If using authentication, you might perhaps add 401 and 403; if using redirects 301 and 302, that might be all.

But the range of possible status codes is much broader than that and can improve semantics a lot.

While many discussions about REST focus on entities and methods, using the correct response status codes can make your API stand out.

## 201: Created

Many applications allow creating entities: accounts, orders, what have you. In general, one uses HTTP status code 200 is used, and that's good enough. However, the **201** code is more specific and fits better:
> The HTTP `201 Created` success status response code indicates that the request has succeeded and has led to the creation of a resource. The new resource is effectively created before this response is sent back. and the new resource is returned in the body of the message, its location being either the URL of the request, or the content of the `Location` header.
>
> -- [MDN web docs](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/201)

## 205: Reset Content

Form-based authentication can either succeed or fail. When failing, the usual behavior is to display the form again with all fields cleared.

Guess what? The **205** status code is dedicated to that:
> The HTTP `205 Reset Content` response status tells the client to reset the document view, so for example to clear the content of a form, reset a canvas state, or to refresh the UI.
>
> -- [MDN web docs](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/205)

## 428: Precondition Required

When using [Optimistic Locking](https://en.wikipedia.org/wiki/Optimistic_concurrency_control), validation might fail during an update because data has already been updated by someone else. By default, frameworks (such as Hibernate) throw an exception in that case. Developers, in turn, catch it and display a nice information box asking to reload the page and re-enter data.

Let's check the **428** status code:
> The origin server requires the request to be conditional. Intended to prevent the 'lost update' problem, where a client GETs a resource's state, modifies it, and PUTs it back to the server, when meanwhile a third party has modified the state on the server, leading to a conflict.
>
> [MDN web docs](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/428)

The code describes exactly the conflict case in optimistic locking!

Note that [RFC 6585](https://tools.ietf.org/html/rfc6585#section-3) mentions the word **conditional** and shows an example using the `If-Match` header. However, it doesn't state exactly how to achieve that condition.

## 409: Conflict

Interestingly enough, the *409* code states:
> The HTTP `409 Conflict` response status code indicates a request conflict with current state of the server.
>
> -- [MDN web docs](https://developer.mozilla.org/en/docs/Web/HTTP/Status/409)

It can also apply to the previous case but is more general. For example, a typical use case would be to update a resource that has been deleted.

## 410: Gone

Most of the time, when you `GET` a resource that is not found, the server returns a 404 code. What if the resource existed before but doesn't anymore? Interestingly enough, there's an alternative for a particular use case: The semantics of the returned HTTP code could tell that. That is precisely the reason for 410.
> The HTTP `410 Gone` client error response code indicates that access to the target resource is no longer available at the origin server and that this condition is likely to be permanent.
>
> If you don't know whether this condition is temporary or permanent, a 404 status code should be used instead.
>
> -- [MDN web docs](https://developer.mozilla.org/en/docs/Web/HTTP/Status/410)

## 300: Multiple choices

WARNING: This one seems a bit far-fetched, but the IETF specification fits the case.

-driven applications offer a root page, which is an entry point allowing navigating further.

For example, this is the response when accessing the Spring Boot actuator:

```json
  "_links": {
    "self": {
      "href": "http://localhost:8080/manage",
      "templated": false
    },
    "beans": {
      "href": "http://localhost:8080/manage/beans",
      "templated": false
    },
    "health": {
      "href": "http://localhost:8080/manage/health",
      "templated": false
    },
    "metrics": {
      "href": "http://localhost:8080/manage/metrics",
      "templated": false
    },
  }
}
```

No regular resource is present at this location. The server provides a set of resources, each with a dedicated identifier. It looks like a match for the **300** status code:
> \[... \] the server SHOULD generate a  
>
> payload in the 300 response containing a list of representation  
>
> metadata and URI reference(s) from which the user or user agent can  
>
> choose the one most preferred.
>
> -- [IETF HTTP 1.1: Semantics and Content](https://tools.ietf.org/html/rfc7231#section-6.4.1)

## Conclusion

Generally, specific HTTP statuses only make sense when having a REST backend accessed by a JavaScript frontend. For example, resetting the form (205) doesn't make sense if the server generates the page.

The issue with those codes is about the semantics: they are subject to a lot of interpretation. Why would you choose to use 409 over 428? It may be a matter of my interpretation over yours in the end.

If you offer a REST public API, you'll have a combination of those codes (and others) and headers.

You'll need full-fledged detailed documentation in all cases to refine the general semantics in your context.

That shouldn't stop you from using them, as they offer a rich set from which you can choose.

**To go further:**

* [HTTP response status codes](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status)
* [List of HTTP status codes](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes)
* [Series of posts on HTTP status codes](https://evertpot.com/http/)
* [The HTTP Status Codes Problem](https://twirl.medium.com/the-http-status-codes-problem-40b3160a189f)

*Originally published at [A Java Geek](https://blog.frankel.ch/leverage-richness-http-status-codes/) on April 23^th^, 2023*

*[HATEOAS]: Hypermedia As The Engine Of Application State
