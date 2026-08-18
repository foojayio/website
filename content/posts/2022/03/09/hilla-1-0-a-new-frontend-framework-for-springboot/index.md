---
title: "Hilla 1.0 - A new reactive frontend framework for Spring Boot backends"
date: "2022-03-09T08:20:42+00:00"
lastmod: "2022-03-09T08:21:32+00:00"
description: "Hilla enables type-safe communication between SpringBoot backends and TS frontend. Integrated tooling and components help you build apps fast!"
canonical: "https://hilla.dev/docs/"
authors:
  - "marcus-hellberg"
image: "og-image-github-700x350.png"
categories:
  - "Hilla"
  - "Release Notes"
  - "Vaadin"
tags:
related_posts:
  - "build-web-apps-in-pure-java-with-vaadin-flow"
  - "create-a-crud-ui-in-pure-java"
  - "delegation-vs-inheritance-in-graphical-user-interfaces"
  - "crafting-your-own-railway-display-with-java"
frozen: false
---

![Hilla Framework](og-image-github-700x350.png)

[Hilla](https://hilla.dev/) is a new framework for building reactive web apps on Java backends. It seamlessly integrates a reactive [Lit](https://lit.dev) TypeScript frontend with a [Spring Boot](https://spring.io/projects/spring-boot) backend. Hilla is built and supported by [Vaadin](https://vaadin.com).

Hilla is designed to be simple enough for small utilities, but robust enough to build complex, enterprise-grade apps.

## Automatic Java to TypeScript Code Generation

Automatic TypeScript code generation helps ensure that the frontend always stays in sync with the backend, so you can build apps faster and with greater confidence, even when your team grows bigger. The strong type-safety also means you can explore server endpoint methods and their input and return types right from your IDE as you type.

**Server endpoint:**

```java
@Endpoint
@AnonymousAllowed
public class PersonEndpoint {
    private PersonRepository repository;

    public PersonEndpoint(PersonRepository repository) {
        this.repository = repository;
    }

    public @Nonnull List<@Nonnull Person> findAll() {
        return repository.findAll();
    }
}
```

**TypeScript View:**

```java
import { PersonEndpoint } from 'Frontend/generated/endpoints';
import Person from 'Frontend/generated/com/example/application/Person';

export class PersonView extends View {
  @state() people: Person[] = [];

  async firstUpdated() {
    this.people = await PersonEndpoint.findAll();
  }

  render() {
    return html`
      <vaadin-grid .items=${this.people}>
        <vaadin-grid-column path="firstName"></vaadin-grid-column>
        <vaadin-grid-column path="lastName"></vaadin-grid-column>
      </vaadin-grid>`;
  }
}
```

## Getting started

You can learn more about Hilla and get started on <https://hilla.dev/>.

## Full Release Notes

### Features

* Zero-configuration toolchain for building web applications with Lit TypeScript UI and Java Spring Boot server side
* Easy and type-safe back end access using TypeScript endpoints and data definitions generated from Java code
* Form binding with shared data validation on server and client
* Includes Vaadin web components for building the UI

### Versions

**Included dependencies:**

* Vaadin Fusion Endpoint (23.0.0)
* Vaadin Dev Server (23.0.0)

**Official add-ons and plugins:**

* Hilla Maven Plugin (1.0.0)
* Hilla Spring Boot Starter (1.0.0)
* Vaadin Design System / Web Components (23.0.0)

### Supported languages and tools

* Java 11
* TypeScript 4.5
* Node.js 16
* npm 8.3
