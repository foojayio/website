---
title: "Master-Detail with Hilla"
slug: "master-detail-with-hilla"
date: "2022-06-21T12:13:24+00:00"
lastmod: "2022-06-21T12:20:09+00:00"
description: "Learn how to use the web application framework Hilla to create a master-detail view with a Grid to display data and a Form to edit the data."
authors:
  - "simon-martinelli"
image: "structure.png"
categories:
  - "Hilla"
  - "Vaadin"
tags:
related_posts:
  - "hilla-1-0-a-new-frontend-framework-for-springboot"
  - "blink-a-led-on-raspberry-pi-with-vaadin"
  - "build-web-apps-in-pure-java-with-vaadin-flow"
  - "crafting-your-own-railway-display-with-java"
enlighterjs: true
frozen: false
---

In this article, I'll explain how to use the web application framework [Hilla](https://hilla.dev) to create a master-detail view with a Grid to display the data and a Form to edit the data.

What is Hilla? {#h2-0-what-is-hilla}
------------------------------------

> Hilla integrates a Spring Boot Java back end with a reactive TypeScript front end. It helps you build apps faster with type-safe server communication, including UI components, and integrated tooling. -- From [hilla.dev](https://hilla.dev/)

Hilla uses TypeScript with [Lit](https://lit.dev/)and [Webcomponents](https://developer.mozilla.org/de/docs/Web/Web_Components) in the frontend and Spring Boot in the backend. In the backend you'll create endpoints from which the API and the TypeScript will be generated. This makes the access to the backend much easier and it's also typesafe and compile-time-checked.

How to Start? {#h2-1-how-to-start}
----------------------------------

The source code is available on GitHub: <https://github.com/simasch/hilla-master-detail-with-filter>

There are two ways to create a new project:  

Use npx:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">npx @vaadin/cli init --hilla my-hilla-app</pre>

Or use [start.vaadin.com](https://start.vaadin.com). Make sure that you delete all views and add one of those
![](https://martinelli.ch/wp-content/uploads/2022/06/image.png)

The Endpoint {#h2-2-the-endpoint}
---------------------------------

The demo project uses a `SamplePerson` entity. This is stored in an H2 database using Spring Data JPA.

To access the data from the frontend we need to create a Hilla Endpoint:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Endpoint
@AnonymousAllowed
public class SamplePersonEndpoint {

    private final SamplePersonRepository repository;

    @Autowired
    public SamplePersonEndpoint(SamplePersonRepository repository) {
        this.repository = repository;
    }

    @Nonnull
    public Page&lt;@Nonnull SamplePerson&gt; list(String filter, Pageable pageable) {
        if (filter == null || filter.equals("")) {
            return repository.findAll(pageable);
        } else {
            return repository.findAllByFirstNameLikeIgnoreCaseOrLastNameLikeIgnoreCase(
                                                   filter + "%", filter + "%", pageable);
        }
    }

    public Optional&lt;SamplePerson&gt; get(@Nonnull UUID id) {
        return repository.findById(id);
    }

    @Nonnull
    public SamplePerson update(@Nonnull SamplePerson entity) {
        return repository.save(entity);
    }

    public void delete(@Nonnull UUID id) {
        repository.deleteById(id);
    }

    public long count(String filter) {
        if (filter == null || filter.equals("")) {
            return repository.count();
        } else {
            return repository.countAllByFirstNameLikeIgnoreCaseOrLastNameLikeIgnoreCase(
                                                              filter + "%", filter + "%");
        }
    }

}
</pre>

Hilla Endpoints are secure by default and you can use the security annotations `@RolesAllowed, @PermitAll` etc. But as we don't use authentication in the simple example we have to annotate the Endpoint with `@AnonymousAllowed` to allow unauthenticated access.

The `@Nonnull` annotations are used by the TypeScript generator to define the nullability. This topic is explained [in the documentation](https://hilla.dev/docs/advanced/endpoints-generator#type-nullability).

From this Java class TypeScript code will be generated:

<pre class="EnlighterJSRAW" data-enlighter-language="typescript" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// @ts-ignore
import client from './connect-client.default';
// @ts-ignore
import { Subscription } from '@hilla/frontend';

import type SamplePerson from './com/example/application/data/entity/SamplePerson';
import type Pageable from './dev/hilla/mappedtypes/Pageable';

function _count(
 filter: string | undefined
): Promise&lt;number&gt;
{
 return client.call('SamplePersonEndpoint', 'count', {filter});
}

function _delete(
 id: string
): Promise&lt;void&gt;
{
 return client.call('SamplePersonEndpoint', 'delete', {id});
}

function _get(
 id: string
): Promise&lt;SamplePerson | undefined&gt;
{
 return client.call('SamplePersonEndpoint', 'get', {id});
}

function _list(
 filter: string | undefined,
 pageable: Pageable | undefined
): Promise&lt;Array&lt;SamplePerson | undefined&gt;&gt;
{
 return client.call('SamplePersonEndpoint', 'list', {filter, pageable});
}

function _update(
 entity: SamplePerson
): Promise&lt;SamplePerson&gt;
{
 return client.call('SamplePersonEndpoint', 'update', {entity});
}
export {
  _count as count,
  _delete as delete,
  _get as get,
  _list as list,
  _update as update,
};
</pre>

As you can see all methods from the Endpoint are available and calling the backend will be very simple:

<pre class="EnlighterJSRAW" data-enlighter-language="typescript" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">const data = await SamplePersonEndpoint.list(
                   this.filter, {pageNumber: params.page, pageSize: params.pageSize, sort});</pre>

As you can imagine changing the Endpoint on the Java side will result in changing the generated TypeScript code and you'll get compiler errors if there are breaking changes.

The Entity {#h2-3-the-entity}
-----------------------------

For simplicity, we directly use the JPA Entity in the Endpoint. We also use annotations to define nullability and looking at the email property also some validation.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Entity
public class SamplePerson extends AbstractEntity {

    @Nonnull
    private String firstName;
    @Nonnull
    private String lastName;
    @Email
    @Nonnull
    private String email;
    @Nonnull
    private String phone;
    private LocalDate dateOfBirth;
    @Nonnull
    private String occupation;
    @Nonnull
    private boolean important;

...
}</pre>

This entity will result in two TypeScript types generated by Hilla.

<pre class="EnlighterJSRAW" data-enlighter-language="typescript" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">export default interface SamplePerson extends AbstractEntity {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  dateOfBirth?: string;
  occupation: string;
  important: boolean;
}</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="typescript" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">export default class SamplePersonModel&lt;T extends SamplePerson = SamplePerson&gt; extends AbstractEntityModel&lt;T&gt; {
  static createEmptyValue: () =&gt; SamplePerson;

  get firstName(): StringModel {
    return this[_getPropertyModel]('firstName', StringModel, [false]);
  }

  get lastName(): StringModel {
    return this[_getPropertyModel]('lastName', StringModel, [false]);
  }

  get email(): StringModel {
    return this[_getPropertyModel]('email', StringModel, [false, new Email()]);
  }

  get phone(): StringModel {
    return this[_getPropertyModel]('phone', StringModel, [false]);
  }

  get dateOfBirth(): StringModel {
    return this[_getPropertyModel]('dateOfBirth', StringModel, [true]);
  }

  get occupation(): StringModel {
    return this[_getPropertyModel]('occupation', StringModel, [false]);
  }

  get important(): BooleanModel {
    return this[_getPropertyModel]('important', BooleanModel, [false]);
  }
}</pre>

The `SamplePerson` interface is used by our code and `SamplePersonModel `will be used for form binding.

The View with the Grid {#h2-4-the-view-with-the-grid}
-----------------------------------------------------

To create the view we use Lit. Also the View will be a Webcomponent. You'll find the full source code [here](https://github.com/simasch/hilla-master-detail-with-filter/blob/main/frontend/views/masterdetail/master-detail-view.ts).

<pre class="EnlighterJSRAW" data-enlighter-language="typescript" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@customElement('master-detail-view')
export class MasterDetailView extends View {
</pre>

The `customElement` decorator defines the name of the Webcomponent and we extend from a Hilla class `View `that finally extends `LitElement.`

The most important method is `render` where we create the content of the view.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">render() {
        return html`
            &lt;vaadin-vertical-layout theme="padding"&gt;
                &lt;vaadin-text-field label="Search" @value-changed=${this.search}&gt;&lt;/vaadin-text-field&gt;
            &lt;/vaadin-vertical-layout&gt;

            &lt;vaadin-split-layout&gt;
                &lt;div class="grid-wrapper" style="width: 70%"&gt;
                    &lt;vaadin-grid
                            id="grid"
                            theme="no-border"
                            .size=${this.gridSize}
                            .dataProvider=${this.gridDataProvider}
                            @active-item-changed=${this.itemSelected}
                            .selectedItems=${[personStore.selectedPerson]}
                    &gt;
                        &lt;vaadin-grid-sort-column path="firstName" auto-width&gt;&lt;/vaadin-grid-sort-column&gt;
                        &lt;vaadin-grid-sort-column path="lastName" auto-width&gt;&lt;/vaadin-grid-sort-column&gt;
                        &lt;vaadin-grid-sort-column path="email" auto-width&gt;&lt;/vaadin-grid-sort-column&gt;
                        &lt;vaadin-grid-sort-column path="phone" auto-width&gt;&lt;/vaadin-grid-sort-column&gt;
                        &lt;vaadin-grid-sort-column path="dateOfBirth" auto-width&gt;&lt;/vaadin-grid-sort-column&gt;
                        &lt;vaadin-grid-sort-column path="occupation" auto-width&gt;&lt;/vaadin-grid-sort-column&gt;
                        &lt;vaadin-grid-column
                                path="important"
                                auto-width
                                ${columnBodyRenderer&lt;SamplePerson&gt;((item) =&gt;
                                        item.important
                                                ? html`
                                                    &lt;vaadin-icon
                                                            icon="vaadin:check"
                                                            style="width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: var(--lumo-primary-text-color);"
                                                    &gt;
                                                    &lt;/vaadin-icon&gt;`
                                                : html`
                                                    &lt;vaadin-icon
                                                            icon="vaadin:minus"
                                                            style="width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: var(--lumo-disabled-text-color);"
                                                    &gt;
                                                    &lt;/vaadin-icon&gt;`
                                )}
                        &gt;&lt;/vaadin-grid-column&gt;
                    &lt;/vaadin-grid&gt;
                &lt;/div&gt;
                &lt;person-form
                        style="width: 30%"
                        @contact-form-saved=${this.contactFormSave}
                &gt;&lt;/person-form&gt;
            &lt;/vaadin-split-layout&gt;
        `;
    }
</pre>

We use Vaadin components and for people familiar with the Vaadin framework it will be easy to get started.

The View uses a Grid to display the persons and uses lazy loading with a DataProvider. Therefore we define a property size that will get the number of persons from the Endpoint:

<pre class="EnlighterJSRAW" data-enlighter-language="typescript" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">this.gridSize = (await SamplePersonEndpoint.count(this.filter)) ?? 0;</pre>

And to load the data we have to use the GridDataProvider functionality that also uses the appropriate Endpoint method that provides paging:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">private async getGridData(
        params: GridDataProviderParams&lt;SamplePerson&gt;,
        callback: GridDataProviderCallback&lt;SamplePerson | undefined&gt;
    ) {
        const sort: Sort = {
            orders: params.sortOrders.map((order) =&gt; ({
                property: order.path,
                direction: order.direction == 'asc' ? Direction.ASC : Direction.DESC,
                ignoreCase: false,
            })),
        };
        const data = await SamplePersonEndpoint.list(this.filter, 
                                                {pageNumber: params.page, pageSize: params.pageSize, sort});
        callback(data);
    }</pre>

The Form {#h2-5-the-form}
-------------------------

To bind the `SamplePerson `object to the form we use a binder. A binder controls all aspects of a single form. It is typically used to get and set the form value, access the form model, validate, reset, and submit the form.  

The binder is typed by the generated interface and class based on the Java `SamplePerson` class.

<pre class="EnlighterJSRAW" data-enlighter-language="typescript" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@customElement('person-form')
export class PersonForm extends View {

    private binder = new Binder&lt;SamplePerson, SamplePersonModel&gt;(this, SamplePersonModel);

    constructor() {
        super();
        this.autorun(() =&gt; {
            if (personStore.selectedPerson) {
                this.binder.read(personStore.selectedPerson);
            } else {
                this.binder.clear();
            }
        });
    }</pre>

But how do we get the person that is selected in the Grid to the form?   

This can be solved by a store that holds the currently selected person.

Hilla recommends MobX to manage frontend state. Read more about that in the documentation: <https://hilla.dev/docs/application/state-management#using-a-store>

Also in the form we use Vaadin components that allows us to bind the fields: `${field(this.binder.model.firstName)}`

<pre class="EnlighterJSRAW" data-enlighter-language="typescript" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">render() {
        return html`
            &lt;div class="editor-layout"&gt;
                &lt;div class="editor"&gt;
                    &lt;vaadin-form-layout&gt;
                        &lt;vaadin-text-field
                                label="First name"
                                id="firstName"
                                ${field(this.binder.model.firstName)}
                        &gt;&lt;/vaadin-text-field&gt;
...</pre>

Submitting the Form {#h2-6-submitting-the-form}
-----------------------------------------------

The last thing we want to have a look at is how to save the person in the form.

We can use the binders submitTo method and pass the update method of the Endpoint.

<pre class="EnlighterJSRAW" data-enlighter-language="typescript" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">private async save() {
        try {
            await this.binder.submitTo(SamplePersonEndpoint.update);
            this.binder.clear();

            personStore.selectedPerson = null;

            this.dispatchEvent(new CustomEvent('contact-form-saved'));

            Notification.show(`SamplePerson details stored.`, {position: 'bottom-start'});
        } catch (error: any) {
            if (error instanceof EndpointError) {
                Notification.show(`Server error. ${error.message}`, {theme: 'error', position: 'bottom-start'});
            } else {
                throw error;
            }
        }
    }</pre>

After saving we clear the store and dispatch an event `contact-form-saved`. This event will be used in the grid to refresh the grid with the changed data.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;person-form @contact-form-saved=${this.contactFormSave}&gt;&lt;/person-form&gt;</pre>

Conclusion {#h2-7-conclusion}
-----------------------------

Of course, this was only a superficial introduction to Hilla.

But I hope this article gave you an overview of how to easily develop a data-centric full-stack application with a lazy loading grid.

If you're interested in how Hilla compares to Vaadin check out my video:

{{< youtube AkN3KtCj_5A >}}
