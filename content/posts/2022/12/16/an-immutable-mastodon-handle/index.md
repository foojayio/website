---
title: "An immutable Mastodon handle"
date: "2022-12-16T13:53:54+00:00"
lastmod: "2026-09-21T06:05:21+00:00"
description: "Whether Twitter crumbles remains to be seen, though some signs are telling. Whatever happens, I'm continuing to invest a bit in Mastodon. Last week, I…"
canonical: "https://blog.frankel.ch/immutable-mastodon-handle/"
authors:
  - "nicolas-frankel"
image: "Mastodon_Logotype_Simple.svg-1.png"
categories:
  - "Research"
  - "Tools"
  - "Tutorials"
related_posts:
  - "preparing-to-move-away-from-twitter"
  - "foojay-on-mastodon-an-update"
frozen: false
---

Whether Twitter crumbles remains to be seen, though some signs are telling. Whatever happens, I'm continuing to invest a bit in Mastodon. Last week, I showed [how to sync](https://blog.frankel.ch/move-away-twitter/) one's content between Twitter and Mastodon. This week, I've set up a Mastodon handle on my domain that redirects to my profile page: I want to explain how I achieved it and the problems I'm still having.

## Mastodon 101

Mastodon is different from Twitter in that it's not centralized: it's a federation of Mastodon servers, run independently and connected - the [Fediverse](https://en.wikipedia.org/wiki/Fediverse). To be precise, the Fediverse is more than Mastodon nodes, but let's not go that far. The first problem when one wants to create a Mastodon account is to choose the correct instance. My first choice was [mastodon.social](http://mastodon.social), but it was closed to new accounts at the time. I set my eyes on [mastodon.top](http://mastodon.top) for no reason but that it was in the proposal list and was French.

The choice of a server is not that important since you can always [move your account](https://docs.joinmastodon.org/user/moving/) to another instance and keep your followers. Note that you'll leave (and lose) your content on the original server. In all cases, your profile is namespaced by the server; thus, your handle changes.

Currently, I'm [@frankel@mastodon.top](https://mastodon.top/web/@frankel). But perhaps I'll join my friends at [foojay.social](https://foojay.social/) or set up my own `frankel.social` in the future? In both cases, I'll need to change the suffix of my handle. Yet, I publish my handle on many sites and don't want to forget any updates when migrating. Hence, I require that the handle **must be immutable**.

I mentioned above that Mastodon nodes belong to a network named Fediverse. Fediverse nodes may be connected through *several different* protocols. Mastodon nodes uses [ActivityPub](https://en.wikipedia.org/wiki/ActivityPub). Underneath, ActivityPub relies on WebFinger to find the correct location of a handle.

## WebFinger

Mastodon needs to translate `@frankel@mastodon.top` to <https://mastodon.top/web/@frankel>. The translation must happen on any Mastodon instance, regardless of its domain. The process is based on the [WebFinger specification](https://www.rfc-editor.org/rfc/rfc7033), *aka* RFC 7033:
> WebFinger as described in [RFC 7033](https://tools.ietf.org/html/rfc7033) is a spec that defines **a method for resolving links to a resource** , given only a URI on a particular server. This allows anyone to look up where a resource is located without having to know its exact location beforehand; for example, by email or phone number. This lookup is directed at the endpoint `/.well-known/webfinger`, and a `resource` query parameter is passed along with the lookup. The resource URI used with Mastodon is the `acct:` URI as described in [RFC 7565](https://tools.ietf.org/html/rfc7565), with the username of a profile that is hosted on a particular domain.
>
> -- [What is WebFinger, and why is it used?](https://docs.joinmastodon.org/spec/webfinger/)

According to the above, when searching for my profile, the query is the following: <https://mastodon.top/.well-known/webfinger?resource=acct:frankel@mastodon.top>. You can check by going to a Mastodon instance you're logged in, searching for my handle, and watching the traffic via your preferred browser's developer tools.

The response is the following:

```json
{
  "subject":"acct:frankel@mastodon.top",
  "aliases":[
    "https://mastodon.top/@frankel",                                        #1
    "https://mastodon.top/users/frankel"                                    #1
  ],
  "links":[
    {
      "rel":"http://webfinger.net/rel/profile-page",                        #2
      "type":"text/html",
      "href":"https://mastodon.top/@frankel"
    },
    {
      "rel":"self",
      "type":"application/activity+json",
      "href":"https://mastodon.top/users/frankel"
    },
    {
      "rel":"http://ostatus.org/schema/1.0/subscribe",
      "template":"https://mastodon.top/authorize_interaction?uri={uri}"
    }
  ]
}
```

1. URL to the profile
2. `rel` for Mastodon

## The immutable Mastodon handle

It should work if I return the same response to the same query on a custom domain. That's what I did: <https://blog.frankel.ch/.well-known/webfinger?resource=acct:me@frankel.ch>. Because it's a static page and I'm the only account, we don't need the query parameter: <https://blog.frankel.ch/.well-known/webfinger>.

Given this, I can search on <https://mastodon.top> with `@me@frankel.ch` (or any handle `@frankel.ch`), and it returns the expected results:

{{< img src="mastodon.top_-700x289.jpg" class="aligncenter size-medium" width="700" height="289" >}}

I checked on other instances, *e.g.* , <https://mastodon.social/>, but it doesn't work. The reason is simple. When searching on the instance you're logged in, the XHR is [https://mastodon.top/api/v2/search?q=@me@frankel.ch\&resolve=true\&limit=5](https://mastodon.top/api/v2/search?q=@me@frankel.ch&resolve=true&limit=5); when not, it's [https://mastodon.top/api/v2/search?q=@me@frankel.ch\&resolve=false\&limit=5](https://mastodon.top/api/v2/search?q=@me@frankel.ch&resolve=false&limit=5). Conclusion: you can only query handles on the same instance when you're not authenticated.

The documentation confirms that if `resolve` is `false`, then the query doesn't try to use WebFinger:
> `resolve`
>
> Boolean. Attempt WebFinger lookup? Defaults to false.
>
> -- [Perform a search](https://docs.joinmastodon.org/methods/search/#query-parameters)

## Conclusion

The theory behind Mastodon and WebFinger is fascinating. I've managed to configure my immutable mastodon handle `@me@frankel.ch`. That's the handle I can communicate to potential followers: if I move to another server, I'll update the `webfinger` with my new coordinates.

The trick works because I'm the only Mastodon user on my domain. If you have several, you'll need to go beyond a static page to return a different ID depending on the `acct:` parameter; the rest stays the same.

**To go further:**

* [Steps toward the glory of REST](https://martinfowler.com/articles/richardsonMaturityModel.html)
* [JSON Hypertext Application Language](https://datatracker.ietf.org/doc/html/draft-kelly-json-hal-08)
* [RFC 8288 - Web Linking](https://www.rfc-editor.org/rfc/rfc8288.html)
* [Choosing an HTTP Status Code — Stop Making It Hard](https://www.codetinkerer.com/2015/12/04/choosing-an-http-status-code.html)

*Originally published at [A Java Geek](https://blog.frankel.ch/immutable-mastodon-handle/) on December 18^th^, 2022*
