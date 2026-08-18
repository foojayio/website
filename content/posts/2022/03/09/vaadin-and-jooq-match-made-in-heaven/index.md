---
title: "Vaadin and jOOQ: Match Made in Heaven"
slug: "vaadin-and-jooq-match-made-in-heaven"
date: "2022-03-09T08:25:52+00:00"
lastmod: "2022-03-10T10:03:41+00:00"
description: "Quickly learn how easy it is to create data-centric web applications with the combination of Vaadin and jOOQ!"
authors:
  - "simon-martinelli"
image: "vqs.png"
categories:
  - "jOOQ"
  - "Vaadin"
tags:
related_posts:
  - "build-web-apps-in-pure-java-with-vaadin-flow"
  - "create-a-crud-ui-in-pure-java"
  - "hilla-1-0-a-new-frontend-framework-for-springboot"
  - "securing-vaadin-applications-with-microsoft-entra"
enlighterjs: true
frozen: false
---

Have you ever had to write an application that didn't do much other than display and change data? And did you use a Single Page Application framework like Angular, a REST API, and Hibernate?

How about if that was a lot easier?

## Introducing Vaadin and jOOQ

Vaadin exists already for 20 years but was completely overhauled three years ago. Vaadin Flow is a web framework that allows the development of a web application in Java. It's based on web components and the state is synchronized between browser and server.

That makes Vaadin a perfect match for data-centric applications are the components. We will see how simple it is to display data in a grid.

The second framework we will use is jOOQ for database access. jOOQ consists of two parts: a code generator and a DSL.

With the code generator, you can generate Java code for all database objects including tables, views, procedures, user-defined types, and many more. The DSL allows you to write type-safe, compile-time-checked SQL in Java using the generated Java classes.

## Displaying a List of Data

### Data Providers

To fetch data from a backend Vaadin provides data providers. Data providers are used when a component displays a collection of data. For example, in Grids or ComboBoxes.

There are two types of data providers: InMemoryDataProvider and CallbackDataProvider. An InMemoryDataProvider is used if you only have a handful of records to display where as the CallBackDataProvider provides lazy loading and paging.

### Creating the Grid

We want to create a grid to display the revenue of customers for that we use a Java Record:

```java
public record CustomerInfo(Integer id, String lastname, String firstname, double revenue) {
}
```

This Java Record can then be used to define the type of the grid and add the columms. The `addColumn` is an overloaded method and we use pass a method reference that returns the value that must be displayed in the grid cell.

```java
Grid<CustomerInfo> grid = new Grid<>();
grid.addColumn(CustomerInfo::id).setHeader("ID");
grid.addColumn(CustomerInfo::firstname).setHeader("First Name");
grid.addColumn(CustomerInfo::lastname).setHeader("Last Name");
grid.addColumn(CustomerInfo::revenue).setHeader("Revenue");
```

### Fetching the Data

As we have many customers we want to use paging. The `Grid` has an overloaded method `setItems` where we only have to pass a callback that is used to fetch the data. Internaly a CallbackDataProvider is created.

```java
grid.setItems(query -> dsl
	.select(CUSTOMER.ID, CUSTOMER.LASTNAME, CUSTOMER.FIRSTNAME, DSL.sum(PRODUCT.PRICE))
	.from(CUSTOMER)
	.join(PURCHASE_ORDER).on(PURCHASE_ORDER.CUSTOMER_ID.eq(CUSTOMER.ID))
	.join(ORDER_ITEM).on((ORDER_ITEM.ORDER_ID.eq(PURCHASE_ORDER.ID)))
	.join(PRODUCT).on((PRODUCT.ID.eq(ORDER_ITEM.PRODUCT_ID)))
	.groupBy(CUSTOMER.ID)
	.orderBy(CUSTOMER.LASTNAME, CUSTOMER.FIRSTNAME)
	.offset(query.getOffset())
	.limit(query.getLimit())
	.fetchStreamInto(CustomerInfo.class)
```

To fetch the data we use the jOOQ DSL with the generated Java code. There are Java classes for the tables (CUSTOMER, PURCHASE_ORDER, ORDER_ITEM and PRODUCT) that contain the columms (CUSTOMER.ID, CUSTOMER.LASTNAME etc). Finally we fetch the result into the `CustomerInfo` record.

We use offest and limit from the query object that is passed to the fetch callback method form Vaadin so the grid will have infinite scrolling and will call the callback method to fetch data when needed.

As you see in the example the code looks like SQL but due to the generated code it is fully type-safe and is checked at compile time. If you have breaking database changes your code may no longer compile after regenereting the Java code.

And finally this is how the grid looks like. There are many more features in the grid like filtering and sorting. You can find more information about theese featrues and also about all other Vaadin components in the [components directory](https://vaadin.com/docs/latest/ds/components/grid).

![](grid-1-1024x381.png)

## Conclusion

This introduction covered only a small part of the feature set of both frameworks but I hope the example gives you an impression of how easy it is to create data-centric web applications with the combination of Vaadin and jOOQ.

If you'd like to know more checkout the [jOOQ documentation and tutorials](https://www.jooq.org/learn/) and watch my Vaadin quick start tutorial to learn more about Vaadin:

{{< youtube QrN1WI87xu8 >}}
