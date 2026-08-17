---
title: "A Faster Way to Build React + Spring Boot Apps Using Hilla 1.3"
slug: "a-faster-way-to-build-react-spring-boot-apps-using-hilla-1-3"
date: "2022-12-22T09:59:35+00:00"
lastmod: "2022-12-22T10:35:10+00:00"
description: "Hilla 1.3 offers an improved way to build React and Spring Boot apps, by enabling end-to-end type-safety and 45 UI components."
canonical: "https://hilla.dev/blog/hilla-1-3-adds-react-support/"
authors:
  - "marcus-hellberg"
image: "hilla-react-featured-logo.webp"
categories:
  - "Hilla"
tags:
related_posts:
  - "hilla-1-3-faster-react-spring-boot-development"
  - "blink-a-led-on-raspberry-pi-with-vaadin"
  - "build-web-apps-in-pure-java-with-vaadin-flow"
  - "master-detail-with-hilla"
enlighterjs: true
frozen: false
---

It's time to build a new web application. You've decided to use React with a Java back end, so you're good to go right? Not quite.

There's still a lot of work to do to set up a new project, configure your build tools, find good UI components, and create APIs for communication between your front end and back end.

What if you could skip all that work and focus on building your app and delivering features?

The latest version of **Hilla now makes it faster to build React apps that connect to Spring Boot** backends than with React alone.

* **No need for REST**, call the backend through automatically generated type-safe methods
* Discover and fix errors earlier thanks to **end-to-end type-safety**
* **Zero-configuration build tooling** runs and auto-reloads both frontend and backend code
* Over **45 UI components** included

TypeScript generation for easy backend access {#h2-0-typescript-generation-for-easy-backend-access}
---------------------------------------------------------------------------------------------------

Hilla apps use **Java and Spring Boot on the backend** and **TypeScript on the frontend**. This means you have type-safety through your entire app.

Here's how it works. In your Spring Boot app, create an endpoint:

**`ContactsEndpoint.java`**

```java
// Make public methods callable from TypeScript.
@Endpoint
// Let anyone access them. Default = logged in only.
@AnonymousAllowed
class ContactsEndpoint {
 private ContactRepository repo;

 // Use Spring to autowire needed dependencies
 ContactsEndpoint(ContactRepository repo) {
   this.repo = repo;
 }

 public List<Contact> findAll() {
   return repo.findAll();
 }
}
```


Hilla generates corresponding TypeScript types and accessor methods automatically.

Any time somebody on your project changes the backend, they'll **catch breaking changes to frontend code at build time -- not in production**.

**`ContactsList.tsx`**

```typescript
import { useEffect, useState } from 'react';
// Hilla-generated TypeScript types and access methods, always in sync with backend
import { ContactsEndpoint } from 'Frontend/generated/endpoints';
import Contact from 'Frontend/generated/com/example/application/data/entity/Contact';

export default function ContactsList() {
 const [contacts, setContacts] = useState<Contact[]>([]);

 // Call the backend as an async method, update the state
 useEffect(() => {
   ContactsEndpoint.findAll().then(setContacts);
 }, []);

 return (
   <div className="contact-list">
     <h2>Contacts:</h2>
     <ul>
       {contacts.map((contact) => (
         <li key={contact.id}>
           {contact.firstName} {contact.lastName}
         </li>
       ))}
     </ul>
   </div>
 );
}
```


**There are no REST endpoint URLs, no Swagger docs you need to read.** Your IDE helps you explore APIs through auto-complete, and ensure correct use with type checking.

One project, one build -- optimized for productivity {#h2-1-one-project-one-build-optimized-for-productivity}
-------------------------------------------------------------------------------------------------------------

Hilla speeds up development by having the frontend and backend code in the same project. There is only one build tool to run. The Maven build runs both the Spring Boot backend and a Vite dev server for the frontend.

This way, whenever you change code, the browser updates automatically - no matter if it's a backend or frontend change.

**But wait, isn't coupling the frontend and backend a bad idea?** Hilla is built around the [backends for frontends (BFF) pattern](https://hilla.dev/blog/why-we-built-hilla/ "backends for frontends (BFF) pattern") because we have found that full-stack teams with control over the API they need for their frontend are more productive. Hilla apps are stateless (and start in a fraction of a second when compiled to native) for efficient scaling.

UI components included {#h2-2-ui-components-included}
-----------------------------------------------------

Hilla includes more than [45 great-looking and accessible UI components](https://vaadin.com/docs/latest/components "45 great-looking and accessible UI components") by Vaadin. You can customize colors, sizing, roundness, flatness, and other properties through CSS properties to make them fit your brand.

[![A line chart, data grid, and date picker](https://hilla.dev/static/6213de6a6291a6335a77df85d256e195/8c76f/components.png "A line chart, data grid, and date picker")](https://hilla.dev/static/6213de6a6291a6335a77df85d256e195/8c76f/components.png "A line chart, data grid, and date picker")

Get started {#h2-3-get-started}
-------------------------------

Learn the basics of Hilla with React through the [basics tutorial](https://hilla.dev/docs/react/start/basics "basics tutorial"). Or jump right in and create a project with the CLI:

```
npx @hilla/cli init --react hello-react
```


Read the [Hilla React documentation](https://hilla.dev/docs/react "Hilla React documentation") to dig deeper.

Need help? Join us on the [Vaadin Discord](https://discord.gg/vaadin "Vaadin Discord") or post your questions on [StackOverflow](https://stackoverflow.com/questions/ask?tags=hilla "StackOverflow") with the "hilla" tag.

Follow [@hillaframework](https://twitter.com/hillaframework "@hillaframework") on Twitter for updates and be sure to tag us when you share cool stuff you've built with Hilla!

What does this mean for Lit support? {#h2-4-what-does-this-mean-for-lit-support}
--------------------------------------------------------------------------------

Hilla added React support as an alternative to Lit, not as a replacement to it. We continue to build and support both to give you more options to choose the stack you prefer.
