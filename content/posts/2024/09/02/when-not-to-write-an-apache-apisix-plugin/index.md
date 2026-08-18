---
title: "When (not) to write an Apache APISIX plugin"
slug: "when-not-to-write-an-apache-apisix-plugin"
date: "2024-09-02T20:50:43+00:00"
lastmod: "2024-09-02T20:50:44+00:00"
description: "Practical alternatives to writing a custom plugin, offering solutions you can quickly implement in your projects."
canonical: "https://blog.frankel.ch/when-write-apisix-plugin/"
authors:
  - "nicolas-frankel"
image: "man-5723449.jpg"
categories:
  - "DevOps"
tags:
related_posts:
  - "apisix-api-gateway"
  - "advanced-url-rewriting-with-apache-apisix"
  - "apache-apisix-loves-rust"
  - "implementing-the-idempotency-key-specification-on-apache-apisix"
enlighterjs: true
frozen: false
---

**When I introduce Apache APISIX in my talks, I mention the [massive number of existing plugins](https://apisix.apache.org/plugins/), and that each of them implements a specific feature.**

One of the key features of Apache APISIX is its flexibility. If a feature is missing, you can create your own plugin in Lua or a language compiled into Wasm, showcasing the platform's adaptability to your specific needs.

In this article, I aim to provide practical alternatives to writing a custom plugin, offering solutions you can quickly implement in your projects.

Cons of writing a plugin
------------------------

Before describing alternatives, let me explain the issues of writing a plugin.

The biggest argument against writing a plugin is quite generic. You write code: suddenly, you need to take care of it. It includes fixing bugs, updating dependencies, keeping the code synchronized with APISIX's latest version, etc.

As I mentioned above, APISIX comes with a list of out-of-the-box plugins. A huge majority of them are enabled in the default configuration. However, if you want to add a plugin to the list, you must add all required plugins individually, as your configuration replaces the default one; this is the case with a custom plugin.

Custom plugins require you to configure APISIX with the path to the plugin(s) folder:

```yaml
apisix:
  extra_lua_path: /opt/?.lua
```


Moreover, some plugins may require additional configuration. For example, in my previous version of [Evolving your APIs](https://www.youtube.com/watch?v=wNg__YYiybo&t=1035s), I set a custom nginx snippet to add a Lua shared dictionary to use it in the code's plugin:

```yaml
nginx_config:
  http:
    custom_lua_shared_dict:
      plugin-unauth-limit: 100m
```


Finally, writing a custom plugin requires a fairly advanced understanding of Apache APISIX and its inner workings. This knowledge is a good idea, but it's not great to make it a requirement.

The `vars` and `filter_func` parameters
---------------------------------------

In my earlier blog post [Free tier API with Apache APISIX](https://blog.frankel.ch/free-tier-api-apisix/), I implemented an API-free tier with the help of the `vars` parameter. As a reminder, `vars` is an additional matching condition on your route besides the usual ones: URI, HTTP method, and host.

In the mentioned post, I used `vars` to add a match on an HTTP header.

```yaml
routes:
  - uri: /get
    upstream_id: 1
    vars: [[ "http_apikey", "~~", ".*"]]                      #1
```


1. Match only if the request has an HTTP header named `apikey`

However, the `vars` parameter has its limitations, particularly in its support of a limited range of [operators](https://github.com/api7/lua-resty-expr?tab=readme-ov-file#comparison-operators), which may restrict its use in more complex scenarios. Here it is for convenience:

| Operator  |                      Description                       |                              Example                               |
|-----------|--------------------------------------------------------|--------------------------------------------------------------------|
| `==`      | equal                                                  | `["arg_version", "==", "v2"]`                                      |
| `~=`      | not equal                                              | `["arg_version", "~=", "v2"]`                                      |
| `>`       | greater than                                           | `["arg_ttl", ">", 3600]`                                           |
| `>=`      | greater than or equal to                               | `["arg_ttl", ">=", 3600]`                                          |
| `<`       | less than                                              | `["arg_ttl", "<", 3600]`                                           |
| `⇐`       | less than or equal to                                  | `["arg_ttl", "⇐", 3600]`                                           |
| `~~`      | match [RegEx](https://www.pcre.org)                    | `["arg_env", "~~", "[Dd]ev"]`                                      |
| `~*`      | match [RegEx](https://www.pcre.org) (case-insensitive) | `["arg_env", "~~", "dev"]`                                         |
| `in`      | exist on the right-hand side                           | `["arg_version", "in", ["v1","v2"]]`                               |
| `has`     | contain item on the right-hand side                    | `["graphql_root_fields", "has", "owner"]`                          |
| `!`       | reverse the adjacent operator                          | `["arg_env", "!", "~~", "[Dd]ev"]`                                 |
| `ipmatch` | match an IP address                                    | `["remote_addr", "ipmatch", ["192.168.102.40", "192.168.3.0/24"]]` |

Note that the DSL also supports boolean operators.

Imagine that the need goes beyond what we can express with the DSL. It's time to break our bounds and leverage the full power of Lua.

With `filter_func`, we can write a dedicated Lua function:

* It accepts a `vars` arg, allowing you to access [APISIX built-in variables](https://apisix.apache.org/docs/apisix/apisix-variable/), including nginx variables, *e.g.*, HTTP headers.
* It must return a boolean value. As for `vars`, APSIX uses the value to decide whether the route matches or not.

The `serverless` plugin
-----------------------

The [serverless](https://apisix.apache.org/docs/apisix/plugins/serverless/) plugin actually consists of two plugins: `serverless-pre-function` and `serverless-pre-function`. As their name implies, the former executes before any other plugin in that phase and the latter after any other plugin in that phase. Note that it's because of their respective default `priority`. While it's technically possible to override the priority, common sense should prevent you from ever thinking about doing so.

With `serverless`, you configure two parameters:

* The [phase](https://apisix.apache.org/docs/apisix/terminology/plugin/#plugins-execution-lifecycle) in which APISIX executes it
* A sequential array of Lua functions

A widespread use case with `serverless` is to log input and output data.

```yaml
routes:
  - uri: /get
    upstream_id: 1
    plugins:
      serverless-pre-function:
        phase: rewrite                                             #1
        functions:
          - >
            return function(conf, ctx)
              local core = require("apisix.core")
              core.log.warn("conf: ", core.json.encode(conf))      #2
              core.log.warn("ctx : ", core.json.encode(ctx, true)) #3
            end
      serverless-post-function:
        phase: log                                                 #4
        functions:
          - >
            return function(conf, ctx)
              local core = require("apisix.core")
              core.log.warn("ctx : ", core.json.encode(ctx, true)) #5
            end
```


1. Execute at the start of the `rewrite` phase
2. Serialize the configuration to JSON and write it in the log. We use the `warn` level because it's the default one
3. Serialize the context to JSON and write it in the log
4. Execute at the start of the `log` phase
5. Serialize the context to JSON and write it in the log **again**. The context will probably have changed between the two phases

The APISIX model only allows **a unique plugin per route** . It's a limitation of this approach: while you can have multiple functions per phase, you can't span more than two phases, one for `pre` and one for `post`.

The `script` parameter
----------------------

I must admit that I learned about [script](https://apisix.apache.org/docs/apisix/terminology/script/) when researching for this post. With `script`, you can write Lua code directly in your config without needing a full-fledged plugin! `script` comes with a huge limitation, though: it's exclusive with `plugins`.
> Scripts and Plugins are mutually exclusive, and a Script is executed before a Plugin. This means that after configuring a Script, the Plugin configured on the Route will not be executed.

I believe that, at this point, you'd better write a plugin instead.

The `_meta.filter` parameter
----------------------------

So far, our scope has been the `route` (or the `service` if you prefer the latter). However, an alternative is to execute a plugin *conditionally* . For example, imagine a route configured with the `limit-count` plugin to rate limit the number of requests. We want to test the infrastructure in a stress test. Instead of creating our own plugin, we can bypass the plugin if a specific header is present.

The `filter` syntax is the same as the `vars` syntax.

```yaml
routes:
  - uri: /get
    upstream_id: 1
    plugins:
      limit-count:                                                              #1
        count: 1
        time_window: 60
        rejected_code: 429
        _meta:
          filter: [["http_Secret-Header", "~=", "MySuperDuperSecretBypassKey"]] #2
```


1. Configure the `limit-count` plugin
2. Execute it only if the HTTP header has a different value

Summary
-------

Writing a custom plugin entails lots of downsides. I showed a couple of other alternatives in this post:

|  Alternative   |  Scope   |                Feature                |                                            Comments                                            |
|----------------|----------|---------------------------------------|------------------------------------------------------------------------------------------------|
| `vars`         | `route`  | Additional criterion to match a route | Simple DSL with a couple of comparison operators and boolean operators                         |
| `filter_func`  | `route`  | Additional criterion to match a route | * Full-fledged Lua function * Can access APISIX and nginx variables * No access to the context |
| `script`       | `route`  | Everything a plugin can do            | * Exclusive with `plugins` * Full access to the context                                        |
| `_meta.filter` | `plugin` | Execute a plugin conditionally        | Simple DSL with a couple of comparison operators and boolean operators                         |

Before writing a plugin, I suggest you design your feature using one of the above alternatives (but `script`).

**To go further:**

* [Plugin](https://apisix.apache.org/docs/apisix/terminology/plugin/)
* [Script](https://apisix.apache.org/docs/apisix/terminology/script/)
* [lua-resty-expr](https://github.com/api7/lua-resty-expr)
* [Plugin Common Configurations](https://docs.api7.ai/apisix/reference/plugin-common-configurations)



*Originally published at [A Java Geek](https://blog.frankel.ch/when-write-apisix-plugin/) on August 25^th^, 2024*
