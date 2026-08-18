---
title: "Smarter Logging in Spring Boot with AOP"
date: "2024-04-16T06:25:36+00:00"
lastmod: "2024-04-16T06:25:37+00:00"
description: "Smart logging with AOP in Spring boot with our smart Logging, leveraging AOP for detailed, configurable logging. Ideal for cleaner code"
authors:
  - "abo-saad-muaath"
image: "Favicon-3-2.png"
categories:
  - "Java"
related_posts:
  - "aspect-oriented-programming-aop"
  - "microservices-design-principles-for-well-crafted-architecture"
  - "evolution-of-microservices-from-soa-to-modern-architecture"
  - "idempotent-spring-boot-starter"
frozen: false
---

Hey **AOP** fan, after explaining the basics of AOP in ***[Part 1](https://foojay.io/today/aspect-oriented-programming-aop/)***, we will dive deeper and demonstrate hands-on how to implement smart logging in Spring Boot using AOP.

The complete example can be found on ***[GitHub](https://github.com/mezocode/healthcare-management-system/blob/main/src/main/java/com/mezocode/healthcare/shared/aop/LoggingAspect.java).***

## **The Problem with Logging Everywhere**

As software developers, we know the importance of logging 📝. But scattering log statements everywhere comes with drawbacks:

* Code duplication as the same logs show up in multiple places

* Cluttered, harder-to-read class files

* Modifying logs means changing code directly

Look at this example:

```
public void updateUser(User user) {

  // Log method entry
  log.info("Updating user {}", user.getId()); 

  // Update user logic
  user.update();

  // Log method exit
  log.info("User updated");
}
```

Without AOP, your logs might be a mix of different actions, making it hard to filter what's important:

```
INFO: Updating user 12345

INFO: User updated 12345

INFO: Deleted user 67890
```

The same logger call shows up everywhere. Yuck!

This leads to a dilemma - log everything and suffer messy code, or omit logging and lose visibility.

There must be a better way! 🤔

## **Introducing Aspect-Oriented Logging**

With AOP, we can modularize cross-cutting concerns like logging away from core functionality. Let's see it in action!

Here are examples and code snippets that illustrate how to use this aspect:

**Custom Annotation @** *[**Loggable**](https://github.com/mezocode/healthcare-management-system/blob/main/src/main/java/com/mezocode/healthcare/shared/annotation/Loggable.java)*

First, you would define a custom annotation **@Loggable** which could look something like this:

```
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {
    boolean showValues() default false;
    String[] showParameters() default {};
    String[] hideParameters() default {};
}
```

Then define the *[LoggingAspect](https://github.com/mezocode/healthcare-management-system/blob/main/src/main/java/com/mezocode/healthcare/shared/aop/LoggingAspect.java)*.

Finally, utilize the smart **@Loggable** in your application at either the class or method level, e.g., [DoctorController](https://github.com/mezocode/healthcare-management-system/blob/main/src/main/java/com/mezocode/healthcare/doctor/controller/DoctorController.java):

```
@Loggable(hideParameters = {"name"})
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    @LogExecution
    public List<DoctorDto> getAllDoctors() {
        return doctorService.getAllDoctors();
    }
    @GetMapping("/{id}")
    public DoctorDto getDoctor(@PathVariable Long id, @RequestParam String name, @RequestParam String date) {
        return doctorService.getDoctor(id, name, date);
    }
}
```

Now logging is cleanly separated from core code via AOP!

After Code Sample Output

With AOP, your logs are now consistent and centralized:

```
DoctorController -> getAllDoctors()

Execution time of getAllDoctors is 104ms

DoctorController -> getDoctor(id = 1, date = mydate)
```

This output shows method names and parameters being logged systematically, and the name parameter is hidden, thanks to AOP.

## **Smart Logging Techniques 🧠**

We can take it further by controlling what gets logged based on metadata.

For example:

```
@Loggable(showParams = {"id"}, hideParams = {"password"})
public void updateUser(Long id, String password) {
// User update logic
}
```

## **Smart Logging Technique Output**

With smart logging annotations, you get precise control over logged information:

```
INFO: Entering UserService.updateUser with params [id=12345] 

INFO: Exiting UserService.updateUser
```

Passwords are masked, while IDs are shown, enhancing both security and clarity.

## **Where to Go from Here**

With AOP, we can log smarter, not harder! Some ideas:

* Performance tracing with @Around
* Control logging level via configuration
* Log to different outputs

Remember to keep logging output meaningful to avoid flooding your logs with unnecessary details, which can make it harder to find important information when you need it.

With LoggingAspect, you have a powerful tool to fine-tune your application's logging strategy, so make the most of it to enhance monitoring and debugging capabilities.

**The possibilities are endless! What will you build? Let me know in the comments!**

**Follow us to learn smarter techniques. Subscribe to our news channel and clone our** [***GitHub***](https://github.com/mezocode/healthcare-management-system)**repository. See you soon!**
