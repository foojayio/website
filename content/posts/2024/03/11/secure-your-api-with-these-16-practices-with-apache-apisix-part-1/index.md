---
title: "Secure your API with these 16 Practices with Apache APISIX - part 1"
slug: "secure-your-api-with-these-16-practices-with-apache-apisix-part-1"
date: "2024-03-11T09:35:51+00:00"
lastmod: "2024-03-11T09:42:39+00:00"
description: "See how to configure Apache APISIX to secure your APIs against 7 of the 16 rules in the \"16 practices to secure your API\" list."
canonical: "https://blog.frankel.ch/secure-api-practices-apisix/1/"
authors:
  - "nicolas-frankel"
image: "img-BuLDzx81CexYQAzkaF36h.png"
categories:
  - "DevOps"
  - "Security"
tags:
related_posts:
  - "apisix-api-gateway"
  - "authenticate-with-openid-connect-and-apache-apisix"
  - "canary-releases-with-apache-apisix"
  - "free-tier-api-with-apache-apisix"
frozen: false
---

A couple of months ago, I stumbled upon this list [16 practices to secure your API](https://www.linkedin.com/posts/brijpandeyji_secure-your-api-with-these-16-practices-activity-7094020647529369601-5kzQ/):
> 1. Authentication 🕵️️ - Verifies the identity of users accessing APIs.
> 2. Authorization 🚦 - Determines permissions of authenticated users.
> 3. Data Redaction 🖍️ - Obscures sensitive data for protection.
> 4. Encryption 🔒 - Encodes data so only authorized parties can decode it.
> 5. Error Handling ❌ - Manages responses when things go wrong, avoiding revealing sensitive info.
> 6. Input Validation \& Data Sanitization 🧹 - Checks input data and removes harmful parts.
> 7. Intrusion Detection Systems 👀 - Monitor networks for suspicious activities.
> 8. IP Whitelisting 📝 - Permits API access only from trusted IP addresses.
> 9. Logging and Monitoring 🖥️ - Keeps detailed logs and regularly monitors APIs.
> 10. Rate Limiting ⏱️ - Limits user requests to prevent overload.
> 11. Secure Dependencies 📦 - Ensures third-party code is free from vulnerabilities.
> 12. Security Headers 📋 - Enhances site security against types of attacks like XSS.
> 13. Token Expiry ⏳ - Regularly expiring and renewing tokens prevents unauthorized access.
> 14. Use of Security Standards and Frameworks 📘 - Guides your API security strategy.
> 15. Web Application Firewall 🔥 - Protects your site from HTTP-specific attacks.
> 16. API Versioning 🔄 - Maintains different versions of your API for seamless updates.

While it's debatable whether some points relate to security, *e.g.,*, versioning, the list is a good starting point anyway. In this two-post series, I'd like to describe how we can implement each point with Apache APISIX (or not).

## Authentication

Authentication is about identifying yourself with a system. It requires a proof.

Apache APISIX provides two kinds of authentications: internal, with APISIX checking credentials, and external, when delegated to a third party. All authentication mechanisms work via plugins. Here's the current list of available authentication plugins.

|   Type   |       Name       |                                               Description                                                |
|----------|------------------|----------------------------------------------------------------------------------------------------------|
| Internal | `key-auth`       | Authenticate via an HTTP Header                                                                          |
| Internal | `basic-auth`     | Relies on a browser callback                                                                             |
| Internal | `jwt-auth`       | Uses a JWT token to authenticate                                                                         |
| External | `authz-keycloak` | Delegates to [Keycloak](https://www.keycloak.org/)                                                       |
| External | `authz-casdoor`  | Delegates to [Casdoor](https://casdoor.org/)                                                             |
| External | `wolf-rbac`      | Delegates to [wolf](https://github.com/iGeeky/wolf)                                                      |
| External | `openid-connect` | Delegates to an [OpenID Connect](https://openid.net/connect/)-compliant third-party                      |
| External | `cas-auth`       | Delegates to a [CAS](https://en.wikipedia.org/wiki/Central_Authentication_Service)-compliant third-party |
| External | `hmac-auth`      | Delegates to an [HMAC](https://en.wikipedia.org/wiki/HMAC)-compliant third-party                         |
| External | `authz-casbin`   | Delegates to a [Lua Casbin](https://github.com/casbin/lua-casbin/)-compliant third-party                 |
| External | `ldap-auth`      | Delegates to an LDAP                                                                                     |
| External | `opa`            | Delegates to an [Open Policy Agent](https://www.openpolicyagent.org/) endpoint                           |
| External | `forward-auth`   | Forwards the authentication to a third-party endpoint                                                    |

APISIX assigns authenticated calls to a *consumer* . For example, we can create a consumer authenticated with the `key-auth` plugin:

```yaml
consumers:
  - username: john
    plugins:
      key-auth:
        key: mykey
```

Every request containing the header `apikey` with the key `mykey` will be assigned to the consumer `john`.

## Authorization

Authentication alone isn't enough. Once a request to a URL has been authenticated, we need to decide whether it's allowed to proceed further. That's the role of authorization.
> Authorization \[...\] is the function of specifying access rights/privileges to resources, which is related to general information security and computer security, and to access control in particular. More formally, "to authorize" is to define an access policy.
>
> -- [Authorization on Wikipedia](https://en.wikipedia.org/wiki/Authorization)

Apache APISIX implements authorization mainly via the [consumer-restriction](https://apisix.apache.org/docs/apisix/plugins/consumer-restriction/) plugin. Here's the most straightforward usage of the `consumer-restriction` plugin:

```yaml
consumers:
  - username: johndoe                     #1
    plugins:
      keyauth:
        key: mykey

routes:
  - upstream_id: 1                        #2
    plugins:
      keyauth: ~
      consumer-restriction:
        whitelist:                        #3
          - johndoe
```

1. Define a consumer
2. Reference an already existing upstream
3. Only allows defined consumers to access the route

Most real-world authorization models avoid binding an identity directly to a permission. They generally bind a group (and even a role) so that it becomes easier to manage many identities. Apache APISIX provides the [consumer group](https://apisix.apache.org/docs/apisix/terminology/consumer-group/) abstraction for this.

```yaml
consumer_groups:
  - id: accountants                      #1

consumers:
  - username: johndoe
    group_id: accountants                #2
    plugins:
      keyauth:
        key: mykey

routes:
  - upstream_id: 1
    plugins:
      keyauth: ~
      consumer-restriction:
        type: consumer_group_id          #3
        whitelist:
          - accountants
```

1. Define a consumer group
2. Assign the consumer to the previously defined consumer group
3. Restrict the access to members of the defined consumer group, *i.e.* , `accountants`

## Input validation

With Apache APISIX, you can define a set of JSON schemas and validate a request against any of them. My colleague Navendu has written an exhaustive blog post on the subject: [Your API Requests Should Be Validated](https://navendu.me/posts/request-validation/).

I think it's not the API Gateway's responsibility to handle request validation. Each upstream has specific logic, and moving the validation responsibility from the upstream to the Gateway ties the latter to the logic for no actual benefit.

In any case, the checkbox is ticked.

## IP Whitelisting

Apache APISIX implements IP Whitelisting via the [ip-restriction](https://apisix.apache.org/docs/apisix/plugins/ip-restriction/) plugin. You can define either regular IPs or CIDR blocks.

```yaml
routes:
  - upstream_id: 1
    plugins:
      ip-restriction:
        whitelist:
          - 127.0.0.1
          - 13.74.26.106/24
```

## Logging and Monitoring

Logging and Monitoring fall into the broader *Observability* category, also encompassing *Tracing*. Apache APISIX offers a broad range of Observability plugins in each category.

|  Type   |          Name          |                                  Description                                   |
|---------|------------------------|--------------------------------------------------------------------------------|
| Tracing | `zipkin`               | Collect and send traces according to the Zipkin specification                  |
| Tracing | `skywalking`           | Integrate with the [Apache SkyWalking](https://skywalking.apache.org/) project |
| Tracing | `opentelemetry`        | Report data according to the OpenTelemetry specification                       |
| Metrics | `prometheus`           | Expose metrics in the Prometheus format                                        |
| Metrics | `node-status`          | Expose metrics in JSON format                                                  |
| Metrics | `datadog`              | Integrate with Datadog                                                         |
| Logging | `file-logger`          | Push log streams to a local file                                               |
| Logging | `syslog`               | Push logs to a Syslog server                                                   |
| Logging | `http-logger`          | Push JSON-encoded logs to an HTTP server                                       |
| Logging | `tcp-logger`           | Push JSON-encoded logs to a TCP server                                         |
| Logging | `udp-logger`           | Push JSON-encoded logs to a UDP server                                         |
| Logging | `kafka-logger`         | Push JSON-encoded logs to a Kafka cluster                                      |
| Logging | `rocketmq-logger`      | Push JSON-encoded logs to a RocketMQ cluster                                   |
| Logging | `loki-logger`          | Push JSON-encoded logs to a Loki instance                                      |
| Logging | `splunk-hec-logging`   | Push logs to a Splunk instance                                                 |
| Logging | `loggly`               | Push logs to a Loggly instance                                                 |
| Logging | `elasticsearch-logger` | Push logs to an Elasticsearch instance                                         |
| Logging | `sls-logger`           | Push logs to Alibaba Cloud Log Service                                         |
| Logging | `google-cloud-logging` | Push access logs to Google Cloud Logging Service                               |
| Logging | `tencent-cloud-cls`    | Push access logs to Tencent Cloud CLS                                          |

## Rate Limiting

Rate Limiting protects upstreams from Distributed Denial of Services attacks, *a.k.a* DDoS. It's one of the main features of reverse proxies and API Gateways. APISIX implements rate limiting through three different plugins:

* The [limit-conn](https://apisix.apache.org/docs/apisix/plugins/limit-conn/) Plugin limits the number of concurrent requests to your services
* The [limit-req](https://apisix.apache.org/docs/apisix/plugins/limit-req/) Plugin limits the number of requests to your service using the [leaky bucket algorithm](https://en.wikipedia.org/wiki/Leaky_bucket)
* The [limit-count](https://apisix.apache.org/docs/apisix/plugins/limit-count/) Plugin limits the number of requests to your service by a given count per time. The plugin is using *Fixed Window* algorithm

Let's use `limit-count` for the sake of example:

```yaml
routes:
  - upstream_id: 1
    plugins:
      limit-count:
        count: 10
        time_window: 1
        rejected_code: 429
```

The above configuration snippet protects the upstream from being hit by more than ten requests per second. It applies to every IP address because of the default configuration. The complete snippet would look like the following:

```yaml
routes:
  - upstream_id: 1
    plugins:
      limit-count:
        count: 10
        time_window: 1
        rejected_code: 429
        key_type: var
        key: remote_addr
```

When dealing with APIs, there's a considerable chance you want to differentiate between your clients. Some might get a better rate for different reasons: they paid a premium offer; they are considered strategic; they are internal clients, etc. The same consumer could also use different IP addresses because they run on various machines with other APIs. Allowing the same consumer more calls because they execute their requests on a distributed infrastructure would be unfair.

As it stands, the IP is not a great way to assign the limit; we prefer to use a named consumer or, even better, a consumer group. It's perfectly possible with APISIX:

```
consumer_groups:
  - id: basic
    plugins:
      limit-count:
        count: 1
        time_window: 1
        rejected_code: 429
  - id: premium
    plugins:
      limit-count:
        count: 10
        time_window: 1
        rejected_code: 429

consumers:
  - username: johndoe
    group_id: basic
    plugins:
      keyauth:
        key: mykey1
  - username: janedoe
    group_id: premium
    plugins:
      keyauth:
        key: mykey2

routes:
  - upstream_id: 1
    plugins:
      key-auth: ~
```

Now, `johndoe` can only send a request every second, as he's part of the `basic` plan, while `janedoe` can request ten times as much as part of the premium plan.

## Conclusion

We've seen how to configure Apache APISIX to secure your APIs against 7 of the 16 rules in the original list.

The rules left could be less straightforward to implement; [we will cover them in the second installment](https://foojay.io/today/secure-your-api-with-these-16-practices-with-apache-apisix-part-2/).

*Originally published at [A Java Geek](https://blog.frankel.ch/secure-api-practices-apisix/1/) on February 18^th^, 2024*
