---
title: "The Future of EJB"
slug: "the-future-of-ejb"
date: "2022-11-10T16:06:56+00:00"
lastmod: "2022-11-10T16:06:57+00:00"
description: "A possible future direction for EJB is to rebase it on CDI. We would end up with just one component model in Jakarta EE."
authors:
  - "ondro-mihalyi"
image: "beans.jpg"
categories:
  - "Jakarta EE"
  - "Opinion"
tags:
related_posts:
  - "top-7-features-in-jakarta-ee-10"
  - "getting-started-with-jakarta-ee-9-context-and-dependency-injection-cdi"
  - "jakarta-concurrency-present-and-future-2"
  - "ejb-support-in-piranha-via-cdi"
enlighterjs: true
frozen: false
---

<figure class="alignleft is-resized">
 <img decoding="async" src="beans-300x260.jpg" alt="" class="wp-image-60965" width="225" height="195">
</figure>

*EJB, or Enterprise Beans, are Java classes with a number of container provided services attached to them, such as transactions, remoting and security. In this article we will take a look at what we can expect for EJB in the future.*

Once upon a time EJB was almost synonymous with what was called Java EE or J2EE back then (Jakarta EE now). It suffered from many issues though, although it did incrementally got better. We'll explore some of those issues next.

The past {#h2-0-the-past}
-------------------------

The very first version of EJB was released before XML even existed and featured a rather awkward "programmatic" configuration, where a java class had to be compiled and its binary and serialised version used as configuration file.

The second version of EJB introduced the now ubiquitous XML configuration, but featured a heavyweight programming model with rather annoying things like the much dreaded home interface. There were tools needed to generate things like proxies, as reflection wasn't a thing yet. Also included was a very flawed persistence model called Entity Beans (not to be confused with the later Jakarta Persistence "Entities", which are totally different).

The third version of EJB started a kind of renaissance for the technology in 2006. The entire EJB 2 API wasn't needed anymore and instead a simple class with an annotation could be used. On the surface it looked almost indistinguishable from modern day bean technologies, although still with some restrictions such as a required "business" interface and the requirement of EJB beans to be packaged inside their own jar.

These restrictions were lifted in 2009 with EJB 3.1. A great simplification was achieved by introducing a subset of EJB without many of the more troublesome and archaic constructs that had troubled EJB before; EJB Lite.

At the same time when EJB 3.1 was introduced, a new component model was added to Java EE: CDI. Though both are "beans with annotations", the approach and philosophy beween EJB and CDI are completely opposite. EJB tried to be a facade over many other technologies in Java EE, such as e.g. JTA (now Jakarta Transactions). That meant EJB had its own rules and annotations for transactions, and used JTA internally.

In CDI, things work exactly the other way around; CDI offers a core component model, for which other APIs such as Jakarta Transactions can provide pluggable things such as scopes and interceptors. In the CDI approach, Jakarta Transactions builds on CDI instead of CDI abstracting over Jakarta Transactions.

Unfortunately, a few mistakes crept into CDI as especially in the beginning people didn't quite agree on CDI becoming this core model, or CDI becoming another attempt at doing EJB, but in broad lines other APIs building on CDI is what CDI is about.

The Present {#h2-1-the-present}
-------------------------------

In the years after EJB 3.1, various APIs such as Jakarta Faces rebased on CDI, and new APIs such as Jakarta Security and Jakarta MVC were introduced that fully build on CDI from the get-go. Several features of EJB, such as the `@Asynchronous` annotation, were implemented in other APIs as a CDI compatible feature.

EJB Full incrementally inched closer to EJB Lite, by removing Entity Beans (CMP/BMP), CORBA/IIOP Distributed Interoperability, and the embedded EJB container, as well as making the entire Enterprise Beans 2.x API Group optional.

The Future {#h2-2-the-future}
-----------------------------

With EJB being greatly de-emphasised in favour of CDI and many APIs in Jakarta EE that build upon CDI, there's almost certainly not going to be any further innovation in EJB itself. That is, no new features are foreseen to be added. On the other hand, due to the large amount of existing code that uses EJB beans, the technology is also not expected to be removed from Jakarta EE anytime soon.

A possible future direction for EJB is to rebase it on CDI. Instead of having those two essentially competing component models in Jakarta EE, we would end up with just one. EJB would essentially be a compatibility API for existing applications, or perhaps a convenience API for those who appreciate the fact that with EJB one annotation gives you a lot of things and behaviour vs the more ala-carte approach of CDI where you combine different annotations. Interesting is that David Blevins from Tomitribe wrote about something like this almost [a decade ago](http://blog.dblevins.com/2012/11/cdi-when-to-break-out-ejbs.html).

The first step to get to this future would be to create an EJB implementation, passing the EJB Lite TCK, fully implemented using CDI and technologies that build on CDI, such as Jakarta Concurrency and Jakarta Transactions.

The [OmniBeans](https://github.com/omnifaces/omnibeans) project is one example of an attempt to do exactly that. This project tries to implement EJB Lite with as little of its own code as possible, delegating as much as possible to the aforementioned APIs. For instance, the following EJB bean can be used with the current version of OmniBeans:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import jakarta.ejb.AsyncResult;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateless;

@Stateless
public class AsyncBean {
    private static final Logger LOGGER = Logger.getLogger(AsyncBean.class.getName());

    @Asynchronous
    public Future&lt;Integer&gt; multiply(int number1, int number2) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Thread.interrupted();
            LOGGER.log(SEVERE, null, ex);
        }

        return new AsyncResult&lt;&gt;(number1 * number2);
    }
}
</pre>

Using OmniBeans, the above bean would become a regular CDI bean, with `@Stateless` translated to a custom scope that emulates some of the semantics of the real `@Stateless` in EJB, and the `@Asyncronous` annotation translated to a CDI based interceptor.

At the moment of writing, the support is still limited, as transactions aren't yet supported and neither does the `@Asyncronous` support comes from Jakarta Concurrency. There's obviously a lot of work to be done still, but the beginning is there. Ideally we would end up with pluggable implementations of Jakarta Enterprise Beans, Jakarta Concurrency and Jakarta Transactions, which could then be somewhat trivially added to our own Jakarta EE runtime [Piranha Cloud](https://piranha.cloud/) or perhaps to up and coming Jakarta EE Core Profile runtimes such as Rudy De Busscher's [AtBash Runtime](https://github.com/atbashEE/runtime).

Even after there's a CDI based EJB implementation, it might still not be feasible to have the EJB (lite) specification fully rebase on CDI. There are existing EJB containers in GlassFish, JBoss, Open Liberty, TomEE, etc, for which it may not be doable to convert them, let alone replace them. Alternatively it might be an option to investigate if there's any value in having a specified CDI-based EJB variant next to EJB Lite and EJB Full. Time will tell.

More information:

* [EJB support in Piranha via CDI](https://omnifish.ee/2022/11/01/ejb-support-in-piranha-via-cdi/) (at OmniFish blog)
* [CDI, when to break out the EJBs](http://blog.dblevins.com/2012/11/cdi-when-to-break-out-ejbs.html) (by David Blevins)
* [](https://jakarta.ee/specifications/enterprise-beans/)[Jakarta Enterprise Beans](https://jakarta.ee/specifications/enterprise-beans/) (EJB) specification
* [Jakarta Contexts and Dependency Injection (CDI) specification](https://jakarta.ee/specifications/cdi/)

> This article was originally published on the [OmniFish blog](https://omnifish.ee/2022/06/29/the-future-of-ejb/). For more information about Jakarta EE, Eclipse GlassFish and related topics, subscribe to follow the OmniFish blog here: <https://omnifish.ee/blog/>.

<br />

<figure class="alignleft size-full is-resized">
 <img decoding="async" src="omnifish-logo-transparent-400px-margin.png" alt="" class="wp-image-60966" width="200" height="200">
</figure>

OmniFish - Jakarta EE experts {#h2-3-omnifish-jakarta-ee-experts}
-----------------------------------------------------------------

* Eclipse GlassFish Production Support
* Jakarta EE Consulting
* Custom Development with Jakarta EE

For more information about OmniFish, contact them at their [contact page](https://omnifish.ee/contact-us/), or Twitter at [@OmniFishEE](https://twitter.com/OmniFishEE).
