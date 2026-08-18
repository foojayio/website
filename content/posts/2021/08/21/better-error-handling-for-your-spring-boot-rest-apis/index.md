---
title: "Better Error Handling for Your Spring Boot REST APIs"
slug: "better-error-handling-for-your-spring-boot-rest-apis"
date: "2021-08-21T07:41:59+00:00"
lastmod: "2023-08-28T12:45:38+00:00"
description: "One of the things that distinguishes a decent API from one that is a pleasure to work with is robust error handling."
authors:
  - "wim-deblauwe"
image: "Favicon-3-2.png"
categories:
  - "Spring"
tags:
related_posts:
  - "build-a-status-dashboard-using-spring-boot-and-astra-db"
  - "building-microservices-spring-boot-fat-uber-jar"
  - "creating-a-simple-spring-boot-application-in-intellij-idea"
  - "ask-a-lille-dev-what-java-developers-really-think-about-quality-frameworks-communities-and-careers"
enlighterjs: true
frozen: false
---

One of the things that distinguishes a decent API from one that is a pleasure to work with is robust error handling. Nothing is more frustrating than using some API and getting back cryptic errors where you can only guess why the server is not accepting your request.

Spring Boot lets you [customize the error handling](https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc) for your application, but there is quite a lot of low-level coding involved if you want to do this correctly. What consititutes good error handling and good error responses in particular might be up for debate, but I think we can agree on a few general guidelines:

* The HTTP response code should reflect the nature of the error (e.g. return 404 for something that was not found, or 400 for a validation error)
* The response body should contain more information about what is wrong exactly.
* The response body should have a kind of code where the clients can act upon (e.g. `USER_NOT_FOUND`)
* For validation problems, the response body should indicate the field names so that clients can for instance highlight the form fields where there are validation problems.

The default mechanismn of Spring Boot does not do to well on those points, so that is where the [Error Handling Spring Boot Starter](https://github.com/wimdeblauwe/error-handling-spring-boot-starter) library comes into play.

When you [add the library](https://wimdeblauwe.github.io/error-handling-spring-boot-starter/#add-the-library-to-your-project) to your Spring Boot application, it will register a controller advice automatically that will return very nice response bodies for common Spring exceptions.

This is for example what is returned for a validation error on a `@RestController` method:

```json
{
  "code": "VALIDATION_FAILED",
  "message": "Validation failed for object='exampleRequestBody'. Error count: 2",
  "fieldErrors": [
    {
      "code": "INVALID_SIZE",
      "property": "name",
      "message": "size must be between 10 and 2147483647",
      "rejectedValue": ""
    },
    {
      "code": "REQUIRED_NOT_BLANK",
      "property": "favoriteMovie",
      "message": "must not be blank",
      "rejectedValue": null
    }
  ]
}
```


Another example is when an `ObjectOptimisticLockingFailureException` happens:

```json
{
  "code": "OPTIMISTIC_LOCKING_ERROR",
  "message": "Object of class [com.example.user.User] with identifier [87518c6b-1ba7-4757-a5d9-46e84c539f43]: optimistic locking failed",
  "identifier": "87518c6b-1ba7-4757-a5d9-46e84c539f43",
  "persistentClassName": "com.example.user.User"
}
```


Custom Application Exceptions
-----------------------------

For the Exception classes that you create in your own application, the library will generate an error `code` using the name of the Exception class. For instance, if you have `UserNotFoundException`, then a `USER_NOT_FOUND` error code will be generated.

In code, for an exception class like this:

```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UserId userId) {
        super("Could not find user with id " + userId);
    }
}
```


The following JSON would be returned:

```json
{
  "code": "USER_NOT_FOUND",
  "message": "Could not find user with id 123"
}
```


The library also honors the `@ResponseStatus` annotation to determine the HTTP response code that is used.

This basic behaviour can be customized in a few ways:

1. Override the error code via `application.properties`
2. Override the error code via `@ResponseErrorCode`
3. Add extra fields in the error response

### Override the error code via properties

Using the `error.handling.codes` key and the full qualified name of the exception class, the error code can be changed. For example:

```properties
error.handling.codes.com.company.app.user.UserNotFoundException=COULD_NOT_FIND_USER
```


Applying this will change the response body to something like this:

```json
{
  "code": "COULD_NOT_FIND_USER",
  "message": "Could not find user with id 123"
}
```


If you don't own the Exception type, this might be the only way to influence the error code. If you *do* own the Exception type, then using the `@ResponseErrorCode` annotation is probably easier.

### Override the error code via annotation

By adding the `@ResponseErrorCode` annotation on the class level, we can override the used error code.

For example:

```java
@ResponseStatus(HttpStatus.NOT_FOUND)
@ResponseErrorCode("NO_SUCH_USER")
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UserId userId) {
        super("Could not find user with id " + userId);
    }
}
```


Will generate the following response:

```json
{
  "code": "NO_SUCH_USER",
  "message": "Could not find user with id 123"
}
```


### Additional fields in response

If you want to add additional fields in the error response, then this can be done by annotating fields or methods on the Exception class with `@ErrorResponseProperty`.

For example:

```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    private final UserId userId;

    public UserNotFoundException(UserId userId) {
        super(String.format("Could not find user with id %s", userId));
        this.userId = userId;
    }

    @ResponseErrorProperty
    public String getUserId() {
        return userId.getValue();
    }
}
```


Will generate the following response:

```json
{
  "code": "USER_NOT_FOUND",
  "message": "Could not find user with id UserId{id=8c7fb13c-0924-47d4-821a-36f73558c898}",
  "userId": "8c7fb13c-0924-47d4-821a-36f73558c898"
}
```


Note the extra `userId` field in the response.

Testing
-------

One of the advantages of using the library is also the testing support. The exact same error responses are returned when using the actual application, or when using a full integration test with `@SpringBootTest`, or using a [web test slice](https://rieckpil.de/spring-boot-test-slices-overview-and-usage/) with `@WebMvcTest`.

This is [not the case in Spring Boot by default](https://github.com/spring-projects/spring-boot/issues/7321). When using MockMvc, you don't get the error handling. Using Error Handling Spring Boot Starter, you can test the error handling with MockMvc, no need to start a complete `@SpringBootTest`.

Conclusion
----------

The [Error Handling Spring Boot Starter](https://github.com/wimdeblauwe/error-handling-spring-boot-starter) can really simplify correct and consistent implementation of errors in your REST API. Check out [the documentation](https://wimdeblauwe.github.io/error-handling-spring-boot-starter) for more detailed information on all the things that are possible.
