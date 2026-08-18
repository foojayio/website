---
title: "Disco API"
description: "The Disco API (short for Universal OpenJDK Discovery API) is a database and REST API that catalogues every available OpenJDK package from every major distributor — Temurin, Zulu, Corretto, Liberica, Oracle, and many others — in one place. It covers ..."
url: "/pedia/disco-api/"
frozen: false
---

The Disco API (short for Universal OpenJDK Discovery API) is a database and REST API that catalogues every available OpenJDK package from every major distributor — Temurin, Zulu, Corretto, Liberica, Oracle, and many others — in one place. It covers every combination of Java version, operating system, CPU architecture, package type (JDK or JRE), and archive format, with tens of thousands of packages indexed.

The API is maintained by Foojay and developed by Gerrit Grunwald. Its source code is available on [GitHub](https://github.com/foojayio/discoapi). The base URL for the REST endpoints is `https://api.foojay.io/disco/v3.0/`, and a full [Swagger UI](https://api.foojay.io/swagger-ui) makes the API easy to explore interactively. Key endpoints let you query major versions, list all distributions, and filter packages by any combination of version, architecture, OS, and JavaFX bundling.

Read more about the Disco API in [this Foojay post](https://foojay.io/today/disco-api-helping-you-to-find-any-openjdk-distribution/).
