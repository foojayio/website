---
title: "Exploring Java Records In A Jakarta EE Context"
date: "2023-06-14T21:08:35+00:00"
lastmod: "2023-06-14T21:08:36+00:00"
description: "This article explores the adoption of Java Records in a Jakarta EE application as a data transfer and projection object."
canonical: "https://blog.payara.fish/exploring-java-records-in-a-jakarta-ee-context"
authors:
  - "jadon-ortlepp"
  - "luqman-saeed"
image: "record.png"
categories:
  - "Jakarta EE"
  - "Payara"
  - "Records"
tags:
related_posts:
  - "a-quick-look-at-faces-jsf-4-0-in-jakarta-ee-10"
  - "a-simple-service-with-spring-boot"
  - "are-java-jakarta-ee-application-servers-heavy"
  - "issues-with-old-glassfish-server-upgrade-to-eclipse-glassfish"
frozen: false
---

Java Records, one of the major highlights of the Java 16 release, provides a concise and immutable way to define classes for modelling data.

This conciseness lends itself useful in a typical Jakarta EE application that can have a number of layers that need to share data.

For example, the data layer might want to return a subset of a given data set to a calling client through a data projection object.

The REST layer might want to have separate entities for server and client side among others.

This article explores the adoption of Java Records in a Jakarta EE application as a data transfer and projection object.

Java Records is an excellent construct for achieving these kinds of data flow in an application because of their consciousness. Among the benefits of adopting Java Records in a [Jakarta EE](https://blog.payara.fish/jakarta-ee-java-ee-guide) application are

1. **Conciseness.** Java Records allow you to define classes with less ceremonial and boiler plate code than typical Java classes. Because they come with built-in methods for equality, hash code, to String and accessor methods, they are easier to read and write, and reduce the chances of introducing subtle, and sometimes not so subtle bugs.
2. **Immutability.** By default Java Records are immutable. This means their values cannot be changed once they are created. This makes them ideal for use as data transfer objects, as they can be safely passed around between different layers of your application without the risk of their values being changed.
3. **Interoperability.** Java Records can be used with existing Java constructs such as streams, lambdas and JSON processing. This makes them a powerful tool for building modern Java applications.

### Using Java Records in a Jakarta EE Application

To show the use of Java Records as data transfer and projection objects, let us consider the following non-trivial Order Jakarta Persistence entity.

```
@Entity
@Table(name = "OrderTable")
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private LocalDate orderDate;
  private String orderId;
  @Enumerated(EnumType.STRING)
  private OrderStatus orderStatus;
  @Embedded
  private Address shippingAddress;
  @Embedded
  private Address billingAddress;
  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
  private List<OrderItem> orderItems = new ArrayList<>();
  @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
  private Payment payment;
  private BigDecimal subtotal;
  private BigDecimal tax;
  private BigDecimal shippingFee;
  private BigDecimal discount;
  private BigDecimal total;
  private String notes;
}
```

For a given case, we want to return just a tiny subset of this entity. For a given date, we want to return all orders that were created before or after the given date, depending on which endpoint is called.

Using Java Records, let us create an OrderSummary record with just the fields we need.

```
public record OrderSummary(String orderId, OrderStatus orderStatus, BigDecimal total, LocalDate orderDate) {

}
```

The OrderSummary has just the orderId, OrderStatus, order total and order date. This is the order summary we wish to return, based on which the client can call for details of each order using the returned orderId.

With the record in place, let's create a query using the Jakarta Criteria API to return a list of OrderSummary for all orders based on a given OrderStatus.

```
public List<OrderSummary> getOrderSummariesByStatus(final OrderStatus orderStatus) {

 CriteriaBuilder cb = em.getCriteriaBuilder();
 CriteriaQuery<OrderSummary> cq = cb.createQuery(OrderSummary.class);

 Root<Order> rootEntity = cq.from(Order.class);

 cq.select(cb.construct(OrderSummary.class,rootEntity.get("orderId"), rootEntity.get("orderStatus"), rootEntity.get("total"), rootEntity.get("orderDate"))).where(cb.equal(rootEntity.get("orderStatus"), orderStatus));

 return em.createQuery(cq).getResultList();

}
```

The above method shows the use of the typesafe Criteria API to construct a projection of order summaries based on the returned set of queried data.

With the above in place, the following sample REST resource uses it to directly return the summaries to the calling client.

```
@Path("/order")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {
    @Inject
    private PersistenceService persistenceService;
    @GET
    @Path("{orderStatus}")
    public List<OrderSummary> getOrdersByStatus(@PathParam("orderStatus") @NotNull OrderStatus orderStatus) {
        return persistenceService.getOrderSummariesByStatus(orderStatus);
    }
}
```

The REST resource method calls the getOrderSummariesByStatus method on the PersistenceService. The implementation of this method was shown earlier, using the Criteria API to return projected order summaries.

As you can see, the REST resource directly uses the OrderSummary record as the return type, without the need for an extra DTO or any other abstraction.

Java Records are a powerful feature introduced in Java 16 that can simplify the creation of data transfer and projection objects, and help reduce much of the boilerplate code needed for mapping data in a Jakarta EE application.

By using this construct, you can create more concise and immutable classes that are easy to read and write in your Jakarta EE application.

And as a developer, code clarity, readability and reasonable conciseness is a gift you give to your future self.

Read more about Jakarta EE:

* [A Business Guide to NoSQL on the Jakarta EE Platform](https://www.payara.fish/resource/a-business-guide-to-nosql-on-the-jakarta-ee-platform/)
* [A Developer Guide To Jakarta EE NoSQL Development With MongoDB And Morphia](https://www.payara.fish/resource/a-developer-guide-to-mongodb-with-morphia-and-jakarta-ee/)
* [The Complete Guide to Testing on the Jakarta EE Platform](https://www.payara.fish/resource/the-complete-guide-to-testing-on-the-jakarta-ee-platform/)
