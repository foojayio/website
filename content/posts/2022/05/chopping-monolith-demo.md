---
title: "Chopping the Monolith: The Demo | Foojay.io Today"
slug: "chopping-monolith-demo"
date: "2022-05-23T12:53:49+00:00"
lastmod: "2022-05-23T12:53:50+00:00"
description: "Learn how to chop up the monolith by exposing the to-be-chopped parts via HTTP and use an API Gateway to route the wanted requests to one's service of choice!"
canonical: "https://blog.frankel.ch/chopping-monolith-demo/"
authors:
  - "nicolas-frankel"
image: "/images/posts/2022/05/chopping-monolith-demo/roger_stone.jpg"
categories:
  - "Kotlin"
  - "Microservices"
tags:
related_posts:
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "book-review-monolith-to-microservices-part-1"
  - "building-microservices-spring-boot-fat-uber-jar"
  - "the-right-feature-at-the-right-place"
enlighterjs: true
frozen: false
---

In my previous blog post [Chopping the Monolith](https://foojay.io/today/chopping-monolith/), I explained my stance on microservices and why it shouldn't be your golden standard.

However, I admitted that some parts of the codebase were less stable than others and had to change more frequently.

I proposed "chopping" these few parts to cope with this requirement while keeping the monolith intact.

As Linus Torvalds once wrote:
> Talk is cheap, show me the code!

I want to show how to do it within the scope of a small demo project to comply with the above statement.

The use-case: pricing {#h2-0-the-use-case-pricing}
--------------------------------------------------

In my career, I've spent some years in the e-commerce domain. E-commerce in the real world is much more complex than people might think. Yet, I found that simplifications of some parts of e-commerce are easy to understand because it "speaks" to the audience.

A huge chunk of e-commerce is dedicated to pricing. Pricing rules are very volatile and need to change quite frequently. Here are some reasons:

* Too much stock of a specific product
* End of season: the new collection has arrived, and we need to make room in the shop (or the warehouse)
* Studies show that decreasing the price (and thus the margin) of a product will increase sales of this product so that the company will earn more money overall
* Marketing purposes: for example, a product prominently branded with the company logo
* etc.

Here, we have an e-commerce shop:

![User interface of an e-commerce shop demo](/images/posts/2022/05/chopping-monolith-demo/eshop.jpg)

We can add items to the cart and check its content:

![Checkout page](/images/posts/2022/05/chopping-monolith-demo/checkout.jpg)

The initial situation {#h2-1-the-initial-situation}
---------------------------------------------------

The following diagram models the existing flow:

![Initial pricing flow](/images/posts/2022/05/chopping-monolith-demo/initial-pricing.png)

The application relies on the Spring Boot framework: it's coded in Kotlin and uses the Beans and Routers DSLs. It leverages Kotlin's coroutines to implement asynchronous communication.

The code is implemented as the following:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">internal fun Cart.toCheckout() = CheckoutView(this, price(this))

class CheckoutView(private val cart: Cart, val total: Double) {
    val lines: List&lt;Pair&lt;Product, Int&gt;&gt;
        get() = cart.content.entries.map { it.toPair() }
}

class CheckoutHandler(private val catalog: Catalog) {
    suspend fun fetchCheckout(req: ServerRequest) =
        ServerResponse.ok().bodyValueAndAwait(req.cart().toCheckout())
}

fun checkoutRoutes(catalog: Catalog) = coRouter {
    val handler = CheckoutHandler(catalog)
    GET("/checkout/c", handler::fetchCheckout)
}</pre>

The pricing logic is coded in its file:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">fun price(cart: Cart): Double {
    return cart.content.entries                                     // 1
        .fold(0.0) { current, entry -&gt;
            current + entry.key.price * entry.value
        }
}</pre>

1. Only sum up the prices of each product separately; it's a demo, after all

At this point, pricing is strongly coupled to the `CheckoutHandler`.

Chopping pricing {#h2-2-chopping-pricing}
-----------------------------------------

Before using an alternative pricing service, we have to chop the pricing service by moving it to its dedicated route. The new flow is the following:

![Chopped pricing flow](/images/posts/2022/05/chopping-monolith-demo/chopped-pricing.png)

The new architecture includes a couple of changes:

* Pricing is exposed as a dedicated route
* The view doesn't return the price anymore
* The client orchestrates the flow between the checkout and pricing routes

The new code reflects this:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">fun price(checkout: CheckoutView): Double {
    println("Pricing computed from the monolith")
    return checkout.lines
        .fold(0.0) { current, line -&gt;
            current + line.first.price * line.second
        }
}

class PricingHandler {
    suspend fun compute(req: ServerRequest): ServerResponse {
        val cart = req.bodyToMono&lt;CheckoutView&gt;().awaitSingle()
        val price = price(cart)
        return ServerResponse.ok().bodyValueAndAwait(price)
    }
}

fun pricingRoute() = coRouter {
    val handler = PricingHandler()
    POST("/price", handler::compute)
}</pre>

Opening the browser dev tools reveals both HTTP requests on the checkout page:

| Status | Method |     Domain     |  File   |  Initiator   |  Type   |
|--------|--------|----------------|---------|--------------|---------|
| 200    | `GET`  | localhost:9080 | `c`     | `checkout:1` | `json`  |
| 200    | `POST` | localhost:9080 | `price` | `checkout:1` | `plain` |

Using an alternative pricing service {#h2-3-using-an-alternative-pricing-service}
---------------------------------------------------------------------------------

At this stage, if we decide to use an alternative pricing feature, we would have to deploy an updated version of the application with the client calling the alternative URL. Each change to the pricing alternative may require a new deployment. Since the idea is to keep the deployed monolith, we shall improve the architecture instead.

Real-world architectures rarely expose their backend services directly to the outside world. Most, if not all, organizations hide them behind a single entry-point; a [Reverse Proxy](https://en.wikipedia.org/wiki/Reverse_proxy).

However, Reverse Proxies are rigid regarding configuration in general and route configuration in particular. For flexibility reasons, one may be inclined to use an [API Gateway](https://en.wikipedia.org/wiki/API_management#Components). For example, the [Apache APISIX](https://apisix.apache.org/) API Gateway allows changing route configuration on the fly via its REST API.

![Apache APISIX helps with chopping the monolith](/images/posts/2022/05/chopping-monolith-demo/chop-monolith.jpg)

I've prepared a Microsoft Azure Function where I uploaded the pricing code implemented in JavaScript:

<pre class="EnlighterJSRAW" data-enlighter-language="js">module.exports = async function (context, req) {
    context.log('Pricing computed from the function')
    const lines = req.body.lines
    context.log(`Received cart lines: ${JSON.stringify(lines)}`)
    const price = lines.reduce(
        (current, line) =&gt; { return current + line.first.price * line.second },
        0.0
    )
    context.log(`Computed price: ${price}`)
    context.res = {
        body: price
    }
    context.done()
}</pre>

With Apache APISIX, we can configure the two routes above.

<pre class="EnlighterJSRAW" data-enlighter-language="bash">curl -v -i http://apisix:9080/apisix/admin/routes/1 -H 'X-API-KEY: xyz' -X PUT -d '
{
  "uri": "/*",                      # 1
  "upstream": {
    "type": "roundrobin",
    "nodes": {
      "chopshop:8080": 1
    }
  }
}'</pre>

1. Configure the generic catch-all route

<pre class="EnlighterJSRAW" data-enlighter-language="bash">curl -v -i http://apisix:9080/apisix/admin/routes/2 -H 'X-API-KEY: xyz' -X PUT -d '
{
  "methods": ["POST"],
  "uris": ["/price"],                                                              # 1
  "plugins": {
    "azure-functions": {                                                           # 2
      "function_uri": "https://chopshoppricing.azurewebsites.net/api/HttpTrigger", # 3
      "authorization": {
        "apikey": "'"$AZURE_FUNCTION_KEY"'"                                        # 4
      },
      "ssl_verify": false
    }
  }
}'</pre>

1. Configure the pricing route to use the Azure Function
2. Apache APISIX provides a plugin that integrates natively with Azure Functions
3. Function's URL
4. Function's secret key

At this point, while the monolithic shop contains pricing code, it's never called. We can plan to retire it during the next release.

On the other side, we can update the pricing logic according to new business requirements without deploying anything but the function itself.

Conclusion {#h2-4-conclusion}
-----------------------------

My previous post focused on why to use microservices and, more importantly, why *not* to use them.

The reason is to speed up the pace of deployment of *some* parts of the code.

Instead of microservices, we can isolate these parts in a dedicated Function-as-a-Service.

In this post, I tried to go beyond the theory and show how you could achieve it concretely.

It boils down to exposing the to-be-chopped part via HTTP and using an API Gateway to route the wanted requests to one's service of choice.

The complete source code for this post can be found on [Github](https://github.com/nfrankel/chop-monolith).

*Originally published at [A Java Geek](https://blog.frankel.ch/chopping-monolith-demo/) on May 22^nd^, 2022*
