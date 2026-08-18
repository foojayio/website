---
title: "DRY your Apache APISIX config"
date: "2024-09-12T09:59:44+00:00"
lastmod: "2024-09-12T09:59:45+00:00"
description: "DRY is an important principle in software development. In this article, you learn how to apply it to Apache APISIX configuration."
canonical: "https://blog.frankel.ch/dry-apisix-config/"
authors:
  - "nicolas-frankel"
image: "desert-279862.jpg"
categories:
  - "DevOps"
tags:
related_posts:
  - "poor-mans-api"
  - "kubernetes-gateway-api"
  - "apache-apisix-loves-rust"
  - "implementing-the-idempotency-key-specification-on-apache-apisix"
frozen: false
---

is an important principle in software development. In this article, you learn how to apply it to Apache APISIX configuration.

## The DRY principle

> "Don't repeat yourself" (DRY) is a principle of software development aimed at reducing repetition of information which is likely to change, replacing it with abstractions that are less likely to change, or using data normalization which avoids redundancy in the first place.
>
> -- [Wikipedia - Don't repeat yourself](https://en.wikipedia.org/wiki/Don%27t_repeat_yourself)

The main idea behind DRY is that if you repeat yourself and the information changes, then you must update the changed information in multiple places. It's not only extra effort; there's a chance you'll forget about it and have different information in different places. DRY shines in bug fixing.

Imagine a code snippet containing a bug. Imagine now that you have duplicated the snippet in two different places. Now, you must fix the bug in these two places, and that's the easy part; the hard being to know about the duplication in the first place. There's a high chance that the person duplicating and the one fixing are different. If the snippet had been refactored to be shareable and called from the two places instead, you only need to fix the bug in this one place.

Most people associate DRY with code. However, it could be more limiting and contrary to the original idea.
> The principle has been formulated by Andy Hunt and Dave Thomas in their book The Pragmatic Programmer. They apply it quite broadly to include database schemas, test plans, the build system, even documentation.
>
> -- [Wikipedia - Don't repeat yourself](https://en.wikipedia.org/wiki/Don%27t_repeat_yourself)

Sound configuration systems allow DRY or even encourage it.

## DRY in Apache APISIX

Apache APISIX offers DRY configuration in two places.

### DRY upstreams

In an e-commerce context, your beginner journey to define a route on Apache APISIX probably starts like the following:

```yaml
routes:
  - id: 1
    name: Catalog
    uri: /products*
    upstream:
      nodes:
        "catalog:8080": 1
```

If you're familiar with APISIX, we defined a route to the catalogue under the `/products` URI. However, there's an issue: you probably want would-be customers to browse the catalogue but want to prevent people from creating, deleting, or updating products. Yet, the route matches every HTTP method by default.

We should allow only authenticated users to manage the catalogue so everybody can freely browse it. To implement this approach, we need to split the route in two:

```yaml
routes:
  - id: 1
    name: Read the catalogue
    methods: [ "GET", "HEAD" ]                         #1
    uri: /products*
    upstream:                                          #2
      nodes:
        "catalog:8080": 1
  - id: 1
    name: Read the catalogue
    methods: [ "PUT", "POST", "PATCH", "DELETE" ]      #3
    uri: /products*
    plugins:
      key-auth: ~                                      #4
    upstream:                                          #2
      nodes:
        "catalog:8080": 1
```

1. Match browsing
2. Duplicated upstream!
3. Match managing
4. Only authenticated consumers can use this route; `key-auth` is the simplest plugin for this

We fixed the security issue in the simplest way possible: by copy-pasting. By doing so, we duplicated the `upstream` section. If we need to change the topology, *e.g.*, by adding or removing nodes, we must do it in two places. It defeats the DRY principle.

In real-world scenarios, especially when they involve containers, you wouldn't implement the `upstream` by listing `nodes`. You should instead implement a dynamic [service discovery](https://apisix.apache.org/docs/apisix/discovery/) to accommodate topology changes. However, the point still stands when you need to change the service discovery configuration or implementation. Hence, my point applies equally to nodes and service discovery.

Along with the *Route* abstraction, APISIX offers an *Upstream* abstraction to implement DRY. We can rewrite the above snippet like this:

```yaml
upstreams:
  - id: 1                                              #1
    name: Catalog
    nodes:
      "catalog:8080": 1

routes:
  - id: 1
    name: Read the catalogue
    methods: [ "GET", "HEAD" ]
    uri: /products*
    upstream_id: 1                                     #2
  - id: 1
    name: Read the catalogue
    methods: [ "PUT", "POST", "PATCH", "DELETE" ]
    uri: /products*
    upstream_id: 1                                     #2
    plugins:
      key-auth: ~
```

1. Define an upstream with ID `1`
2. Reference it in the route

If anything happens in the topology, we must update the change only in the single *Upstream*.

Note that defining the `upstream` embedded and referencing it with `upstream_id` are **mutually exclusive**.

### DRY plugin configuration

Another area where APISIX can help you DRY your configuration with the *Plugin* abstraction. APISIX implements most features, if not all, through plugins

Let's implement [path-based versioning](https://blog.frankel.ch/api-versioning/#path-based-versioning) on our API. We need to rewrite the URL before we forward it.

```yaml
routes:
  - id: 1
    name: Read the catalogue
    methods: [ "GET", "HEAD" ]
    uri: /v1/products*
    upstream_id: 1
    plugins:
      proxy-rewrite:
        regex_uri: [ "/v1(.*)", "$1" ]                 #1
  - id: 1
    name: Read the catalogue
    methods: [ "PUT", "POST", "PATCH", "DELETE" ]
    uri: /v1/products*
    upstream_id: 1
    plugins:
      proxy-rewrite:
        regex_uri: [ "/v1(.*)", "$1" ]                 #1
```

1. Remove the `/v1` prefix before forwarding

Like with `upstream` above, the `plugins` section is duplicated. We can also factor the plugin configuration in a dedicated *Plugin Config* object. The following snippet has the same effect as the one above:

```yaml
plugin_configs:
  - id: 1                                              #1
    plugins:
      proxy-rewrite:
        regex_uri: [ "/v1(.*)", "$1" ]

routes:
  - id: 1
    name: Read the catalogue
    methods: [ "GET", "HEAD" ]
    uri: /v1/products*
    upstream_id: 1
    plugin_config_id: 1                                #2
  - id: 1
    name: Read the catalogue
    methods: [ "PUT", "POST", "PATCH", "DELETE" ]
    uri: /v1/products*
    upstream_id: 1
    plugin_config_id: 1                                #2
```

1. Factor the plugin configuration in a dedicated object
2. Reference it

Astute readers might have noticed that I'm missing part of the configuration: the `auth-key` mysteriously disappeared! Indeed, I removed it for the sake of clarity.

Unlike `upstream` and `upstream_id`, `plugins` and `plugin_config_id` are **not mutually exclusive** . We can fix the issue by just adding the missing `plugin`:

```yaml
routes:
  - id: 1
    name: Read the catalogue
    methods: [ "GET", "HEAD" ]
    uri: /v1/products*
    upstream_id: 1
    plugin_config_id: 1
  - id: 1
    name: Read the catalogue
    methods: [ "PUT", "POST", "PATCH", "DELETE" ]
    uri: /v1/products*
    upstream_id: 1
    plugin_config_id: 1
    plugins:
      key-auth: ~                                      #1
```

1. Fix it!

This way, you can move the shared configuration to a `plugin_config` object and keep a specific one to the place it applies to. But what if the same plugin with different configurations is used in the `plugin_config` and directly in the `route`? The [documentation](https://apisix.apache.org/docs/apisix/terminology/plugin/#plugins-merging-precedence) is pretty clear about it:
> `Consumer` \> `Consumer Group` \> `Route` \> `Plugin Config` \> `Service`

In short, the `plugin` configuration in a `route` overrules the configuration in the `plugin_config_id`. It also allows us to provide the `apikey` variable for the `key-auth` plugin in a `consumer` and only set it in a route. APISIX will find and use the key for each `consumer`!

## Conclusion

DRY is not only about code; it's about data management in general. Configuration is data and thus falls under this general umbrella.

APISIX offers two DRY options: one for `upstream` - `upstream_id`, and one for `plugin` - `plugin_config_id`. Upstreams are exclusive; plugins allow for overruling.

Both mechanisms should help you toward DRYing your configuration and make it more maintainable in the long run.

**To go further:**

* [The Don't repeat yourself principle](https://en.wikipedia.org/wiki/Don%27t_repeat_yourself)
* [Configuration merging precedence](https://apisix.apache.org/docs/apisix/terminology/plugin/#plugins-merging-precedence)

*Originally published at [A Java Geek](https://blog.frankel.ch/dry-apisix-config/) on September 1^st^, 2024*

*[DRY]: Don't Repeat Yourself
