---
title: "Role-Based Access Control in Java Applications"
date: "2026-03-05T20:31:49+00:00"
lastmod: "2026-03-05T20:31:51+00:00"
description: "In this article, we will explore how to implement RBAC at the application level, using MongoDB to store user metadata and keeping the authorization logic close to the core of the system. The goal is not only to make things secure, but also to make them architecturally consistent. All the code used in this article is inside this repository."
authors:
  - "matteo-rossi"
image: "rbac-image2-1.png"
categories:
  - "Databases"
  - "Java"
  - "Mongo"
related_posts:
  - "abstracting-data-access-in-java-with-the-dao-pattern"
  - "agents-meet-databases-the-future-of-agentic-architectures"
  - "atlas-searching-with-the-java-driver"
  - "best-practices-for-deploying-mongodb-in-kubernetes"
frozen: false
---

We often work with Java applications where security begins and ends with authentication. The JWT token is validated, Spring Security is integrated, and an identity provider is added, thinking that this type of configuration is sufficiently secure.

The real problem is that authentication only answers one question: who are you? In real applications, we also have to answer another question, which is often more complex and more dangerous to get wrong: what are you allowed to do?

This question concerns authorization. The first step in incorporating this question into backend applications in enterprise contexts is to apply role-based access control (RBAC).

RBAC is certainly not new; it has been around for decades. However, the way we apply this principle within modern Java applications determines maintainability and the ability to evolve appropriately, without becoming a tangle of annotations and implicit behaviors of the framework used.

In this article, we will explore how to implement RBAC at the application level, using MongoDB to store user metadata and keeping the authorization logic close to the core of the system. The goal is not only to make things secure, but also to make them architecturally consistent. All the code used in this article is inside this [repository](https://github.com/matteoroxis/mongo-rbac-engine).

## Authorization Is a Business Concern

In Spring applications, it is very easy to find authorization hidden within annotations:

```
@PreAuthorize("hasRole('ADMIN')")
Or embedded directly within controllers:
if (!user.getRoles().contains("ADMIN")) {
    throw new ForbiddenException();
}
```

Technically, both approaches work. The real problem is that they tend to spread authorization rules across various levels, mixing business logic with security concepts. The issue is complex and the difference is very subtle. Authorization rules are rarely purely technical rules. The fact that only finance users can approve reimbursements is not a framework configuration detail. It is a business rule.

The fact that only the owner of an order can cancel it is business logic.

However, this does not mean that annotations should be eliminated. On the contrary, annotations are incredibly useful when they serve to enforce technical, cross-cutting, or repetitive authorization models. They reduce human error, simplify and aid static code analysis, and offer declarative enforcement of consistent security constraints. The main difference lies in where each rule resides:

* Business authorization (e.g., "the requester must be the owner of the order") should remain visible within the application layer where all business logic resides
* Technical authorization (e.g., "requires authentication") is well suited to annotations or framework-level configuration

Making business rules explicit and technical rules declarative allows us to achieve both clarity and security.

## Modeling Permissions First

A common mistake is to think about roles before permissions. Permissions represent actions. They are verbs, concrete operations that the system makes available.

Let's assume we have a system with simple authorization requirements. All this can be expressed in Java as follows:

```
public enum Permission {
    ORDER_CREATE,
    ORDER_CANCEL,
    ORDER_VIEW,
    REFUND_APPROVE,
    USER_MANAGE
}
```

These permissions describe the functionalities present within the system. They are part of the logic implemented by the application itself. Roles therefore become sets of permissions:

```
public enum Role {
    CUSTOMER(Set.of(
        Permission.ORDER_CREATE,
        Permission.ORDER_CANCEL,
        Permission.ORDER_VIEW
    )),

    FINANCE(Set.of(
        Permission.ORDER_VIEW,
        Permission.REFUND_APPROVE
    )),
    ADMIN(Set.of(Permission.values()));
    private final Set<Permission> permissions;
    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> permissions() {
        return permissions;
    }
}
```

A design like this has two important consequences. First, permissions are clear and can be versioned: if a new feature is added, it is expressed within the application code. If a role changes, the difference is traceable.

Second, this entire logic does not depend in any way on technological implementations or any security framework. This independence is intentional, as authorization rules should not be linked in any way to infrastructure or external dependencies.

## Using MongoDB for User Metadata

MongoDB can be an excellent choice for storing user metadata. User profiles change over time: new attributes appear, others disappear. Roles change.

Flexibility therefore becomes a fundamental requirement.

Below, let's try to imagine a simple document representing a user

```
{
  "_id": "user-123",
  "email": "alice@example.com",
  "roles": ["CUSTOMER"],
  "status": "ACTIVE"
}
```

With Spring Data MongoDB, mapping is simple:

```
@Document(collection = "users")
public class UserDocument {
    @Id
    private String id;
    private String email;
    private Set<String> roles;
    private String status;
    // constructors and getters omitted
}
```

MongoDB does one thing and one thing only: it persists metadata. It does not decide on permissions for a given user. It does not evaluate security policies. It simply stores the data from which authorization decisions can be made.

This type of separation keeps the core application code clean.

## From Infrastructure Model to Application Principal

Instead of using a representation of the User as UserDocument, contaminating its representation with persistence logic, we use a representation closer to the domain, such as the following

```
public class UserPrincipal {
    private final String id;
    private final Set<Role> roles;
    public UserPrincipal(String id, Set<Role> roles) {
        this.id = id;
        this.roles = roles;
    }

    public boolean hasPermission(Permission permission) {
        return roles.stream()
                .flatMap(role -> role.permissions().stream())
                .anyMatch(p -> p == permission);
    }

    public String id() {
        return id;
    }
}
```

Mapping becomes a concept that can be managed by an adapter:

```
public class UserMapper {
    public static UserPrincipal toPrincipal(UserDocument document) {
        Set<Role> roles = document.getRoles().stream()
                .map(Role::valueOf)
                .collect(Collectors.toSet());
        return new UserPrincipal(document.getId(), roles);
    }
}
```

By doing this, authorization works on the application domain and not on a MongoDB entity or a specific framework principle.

## Centralizing Authorization Logic

We explicitly introduce an AuthorizationService, which allows us to centralize authorization control in a single place, rather than spreading it throughout the codebase.

```
public class AuthorizationService {
    public void checkPermission(UserPrincipal user, Permission permission) {
        if (!user.hasPermission(permission)) {
            throw new ForbiddenException(
                "Missing permission: " + permission
            );
        }
    }
}
```

Application services can now clearly expose their authorization logic:

```
public class OrderService {
    private final AuthorizationService authorizationService;
    public OrderService(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    public void cancelOrder(UserPrincipal user, String orderId) {
        authorizationService.checkPermission(user, Permission.ORDER_CANCEL);
        // domain logic here
    }
}
```

When reading this method, the authorization requirement is clear: there are no hidden or nested annotations, no proxy magic. The rule is located where it matters, namely protecting the boundary between application behavior and the domain.

## Contextual Rules: Where RBAC Stops

RBAC policies handle static permissions well, but real-world systems rarely stop at this level. For example, a user may have permission to cancel orders, but that does not automatically mean they can cancel any order.

These constraints can be modeled in two complementary ways:

* Domain rules: the application logic checks the "here and now" context.
* Context-aware RBAC (or conditional RBAC): an extension of RBAC in which a role-based permission is only effective if certain conditions are true (e.g., user, resource, or environment attributes).

Contextual rules reside within domain logic:

```
public void cancelOrder(UserPrincipal user, Order order) {
    authorizationService.checkPermission(user, Permission.ORDER_CANCEL);
    if (!order.belongsTo(user.id())) {
        throw new ForbiddenException("Cannot cancel another user's order");
    }
    // continue with cancellation
}
```

The same constraint can be expressed as context-aware RBAC, making the permission conditional on the role:

```
public boolean canCancel(Order order, Authentication authentication) {
     if (order == null || authentication == null || !authentication.isAuthenticated()) {
         return false;
     }

     Object principal = authentication.getPrincipal();

     if (principal instanceof UserPrincipal up) {
         //Contextual-contrstraint: can only cancel their own orders
         return order.belongsTo(up.id());
     }
     return false;
}
```

In practice, RBAC tells you whether the action is allowed "in general," while context constraints—whether you choose to implement them in domain logic or as context-aware RBAC in the policy engine—tell you whether the action is allowed here and now. Keeping these layers separate helps avoid confusing identity-based access with business constraints related to the specific case.

## RBAC vs ABAC: When Roles Are Not Enough

As mentioned, as the system grows in both functionality and complexity, RBAC policies show their limitations. Let's take another example:

* A user in the finance department can approve expense reimbursements, but only up to $10,000.
* A regional manager can access data, but only for the region assigned to them.
* A support agent can view user accounts, but not those marked as high risk.

These rules are not and can no longer be based solely on roles: they depend on the attributes of the user, the resource, and the environment.

This is where ABAC policies and attribute-based access control come into play.

In ABAC policies, decisions are based on rules that dynamically evaluate attributes. Instead of asking "does the user have role X?", the system asks "given these attributes, is this policy being respected?

ABAC allows you to address this level of complexity and scalability, but it introduces others:

* Policies can be outsourced
* The decision logic becomes difficult to trace
* Testing requires the evaluation of rule engines rather than simple role checks

For the vast majority of enterprise applications, especially those with well-defined business rules, RBAC integrated with contextual domain controls is more than sufficient. But the basic idea is that you don't have to choose between RBAC and ABAC in advance, exclusively. It is possible to design authorization at the application level to allow for evolution and expansion.

If permissions are explicit and authorization is centralized, we can start with this approach and then extend the AuthorizationService by applying richer policies, and why not, attribute-based ones. In this case, the architecture offers options.

## The Architectural Payoff

Implementing RBAC policies as configurations gives us the idea that we have added functionality to our system. Modeling RBAC policies in the core of our application allows us to make these rules part of the business logic. Permissions describe capabilities, roles describe responsibilities, and authorization controls describe intent.

MongoDB stores metadata, Spring Security authenticates users. The decision about what a user can do is business logic, and as such, the responsibility lies with the application layer.

Applying this attribution of responsibilities allows us to gain a lot of long-term benefits, such as:

* The domain remains free of framework annotations
* Authorization logic can be tested without infrastructure
* Rules are visible and versioned within the application code
* The architecture remains adaptable in case requirements evolve towards more complex policies

The real strength lies in the fact that by reading the service layer of an application, you not only know what that application does, but also who is authorized to do what.

## Conclusion

We often see role-based access control functionality as something that can be activated, a box to tick in the security settings. In many cases, an annotation above a method.

In Java enterprise applications, authorization must reflect business rules; it cannot be solely and exclusively a technical mechanism.

The recipe is simple: we model authorizations explicitly, grouping them into roles, saving metadata in a database, and applying access rules in the application layer. Security follows architecture. Everything is where it should be.

The basic idea is to make authorization and the logic associated with it visible and testable, avoiding contamination of this logic with application frameworks and/or infrastructure objects.  

You can find an example of how to apply these concepts at the following [link](https://github.com/matteoroxis/mongo-rbac-engine).
