---
title: "MicroProfile Metrics with Prometheus and Grafana"
date: "2020-12-02T09:11:30+00:00"
lastmod: "2020-12-02T09:11:32+00:00"
description: "Learn how to connect MicroProfile Metrics with Prometheus and Grafana to produce useful graphics and to help investigate your architecture."
canonical: "https://blog.payara.fish/microprofile-metrics-with-prometheus-and-grafana"
authors:
  - "jadon-ortlepp"
image: "MP-metrics.jpg"
categories:
  - "Microservices"
  - "Videos"
tags:
related_posts:
frozen: false
---

In a distributed microservices architecture, it is important to have an overview of your systems in terms of CPU, memory management and other important metrics.

This is called *Observability*, measuring the internal state of a system, in this case, the micro-services instances.

These metrics are gathered centrally, so that you can instantly have an overview of all the values and it allows the determination (mostly automated based on some rules) of the health of the instances.

MicroProfile Metrics allows you to expose some custom defined metrics, but also exposes system metrics, related to CPU and memory, for example.

In this short video Rudy de Busscher shows how to connect MicroProfile Metrics with Prometheus and Grafana to produce useful graphics and to help investigate your microservice architecture.

{{< youtube HfmEFwAmiDY >}}

The goal of [MicroProfile](https://microprofile.io/ "MicroProfile") Metrics is to expose monitoring data from the implementation in a unified way. It also defines a Java API so that the developer can define and supply his own values.

[Prometheus](https://prometheus.io/ "Prometheus") is one of the most popular Open source solutions for gathering metrics. Created in 2012 by SoundCloud (online audio distribution platform and music sharing) and in 2018 graduated at the Cloud Native Computing Foundation. It can be used as a database for storing time series but has many more features:

* Multi-dimensional data model with time series
* Query Language
* Pull data from metric sources
* Alert Manager

Therefore, MicroProfile Metrics exposes the values in the Prometheus format by default. So it can be consumed easily by the scrapers.

[Grafana](https://grafana.com/ "Grafana") is a multi-platform open source solution for running data analytics, pulling up metrics that make sense of the massive amount of data, and monitoring apps through customizable dashboards.

*Republished with permission from the original author - Rudy De Busscher*
