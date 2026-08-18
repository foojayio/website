---
title: "Spring: Internals of @ComponentScan"
date: "2024-08-20T14:48:28+00:00"
lastmod: "2024-08-31T09:24:52+00:00"
description: "Explore the nuances of Spring's @ComponentScan in our extensive guide. Gain insights into its internals and optimize your application's configuration effectively."
authors:
  - "mahendra1413"
image: "componentscan.png"
categories:
  - "Java"
  - "Spring"
  - "Tutorials"
tags:
related_posts:
  - "how-to-improve-your-spring-boot-skills"
  - "spring-ai-how-to-write-genai-applications-with-java"
  - "spring-internals-of-restclient"
enlighterjs: true
frozen: false
---

**The `@ComponentScan` annotation, which is an interface located within the `org.springframework.context.annotation` package, facilitates component scanning in a Spring application. This allows the Spring Container to automatically detect beans.**

Spring automatically identifies Java classes that developers mark with stereotypes like `@Configuration, @Component, @Service, @Controller, and @Repository`. Developers can annotate the component scan with or without parameters.

You can designate either `basePackageClasses() or basePackages()` (or its equivalent `value()`) to identify the specific packages that need to be scanned.

The `@ComponentScan` annotation, when used without any arguments, instructs Spring to scan the current package and all its sub-packages.

For instance:

```
import org.springframework.context.annotation.ComponentScan;
@ComponentScan
public class MovieApplication {
}
```


The `@ComponentScan` annotation instructs Spring to perform a scan of the specified package when you provide it with arguments, as indicated by the `basePackages` attribute. For instance:

```
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "com.bsmlabs.microservices")
public class MovieApplication {}
```


The `@ComponentScan` annotation acts as a substitute for the \<context:component-scan/\> XML tag. While it includes an annotation-config attribute, the XML version lacks this feature. In most scenarios where developers utilize `@ComponentScan`, they presume that default annotation configuration processing is in effect, such as the handling of `@Autowired`.

The component scan interface looks like

```
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Repeatable(ComponentScans.class)
public @interface ComponentScan {
}
```


Since the `RetentionPolicy` of ComponentScan is Runtime, it will only execute during the **runtime** phase. You can apply this annotation at the *class level* , as well as at the *interface or enum declaration levels* . Therefore, you specify its `@Target` as ElementType with Type, specifically `@Target(ElementType.TYPE)`.

In the `SpringBootApplication` class, specifies the `@ComponentScan` annotation at the class level to enable component scanning at runtime which helps to create

* ApplicationContext()
* Register Environment
* Loads Beans Definitions
* Applying the ApplicationContextInitializer and ApplicationContextInitilizerEvent

```
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = { @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
        @Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public @interface SpringBootApplication {

}
```




Attributes used with ComponentScan annotation
---------------------------------------------

The `value()` function acts as an alias for basePackages, allowing developers to make more specific annotation declarations when they do not require additional attributes. For instance, developers can use `@ComponentScan("com.bsmlabs.microservices")` as an alternative to `@ComponentScan(basePackages = "com.bsmlabs.microservices")`.

```
@AliasFor("basePackages")
String[] value() default {};
```


1. 

The `basePackages()` identifies annotated components, using the value as an alias for this attribute. To find a type-safe alternative to Spring-based package names, one can employ `basePackageClasses`.

```
@AliasFor("value")
String[] basePackages() default {};
```


For example:

```
import org.springframework.context.annotation.ComponentScan;
@ComponentScan(basePackages = "com.bsmlabs.microservices")
public class MovieApplication {
}
```


The `basePackageClasses()` method outlines the process of scanning all packages that contain annotated components. This indicates that the scanning procedure will be applied to the package associated with each specified class.

For example:

```
import org.springframework.context.annotation.ComponentScan;
@ComponentScan(basePackageClasses = {"com.bsmlabs.microservices.movie", "com.bsmlabs.microservices.payment"})
public class MovieApplication {
}
```


`nameGenerator()`: The Spring container specifically designates the `BeanNameGenerator` class for assigning names to the components it identifies within the `ApplicationContext`.

```
Class<? extends BeanNameGenerator> nameGenerator()
default BeanNameGenerator.class;
```


The inherent value of the `BeanNameGenerator` interface signifies that the scanner responsible for processing the `@ComponentScan` annotation should use its inherited bean name generator.

Default [AnnotationBeanNameGenerator](https://www.codota.com/code/java/classes/org.springframework.context.annotation.AnnotationBeanNameGenerator) or any custom instance supplied to the application context at startup or bootstrap time.

```
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@ComponentScan(nameGenerator = FullBeanNameGenerator.class)
public class MovieApplication {
}
```


`scopeResolver`: The `ScopeMetadataResolver` is utilized for determining the scope of identified components.

```
Class<? extends ScopeMetadataResolver> scopeResolver() default AnnotationScopeMetadataResolver.class;
```


For example:

```
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ScopedProxyMode;

@ComponentScan(basePackages = "com.bsmlabs.microservices", scopeResolver = MyResolverBean.class)
public class MovieApplication {
}
```


**scopedProxy:** This specifies whether spring framework should create proxies for identified components, which may be essential when they employ scopes in a proxy-oriented manner.

```
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ScopedProxyMode;

@ComponentScan(basePackages = "com.bsmlabs.microservices", scopedProxy = ScopedProxyMode.DEFAULT)
public class MovieApplication {
}
```


**useDefaultFilters:** This specifies the automatic detection of classes that are annotated with `@Component`, `@Controller`, `@Service`, and `@Repository`. The values can be set to either *false* or *true* , with the default being *true*.

```
boolean useDefaultFilters() default true;
```


For example:

```
import org.springframework.context.annotation.ComponentScan;
@ComponentScan(basePackages = "com.bsmlabs.microservices", useDefaultFilters = false)
public class MovieApplication {
}
```


**includeFilters:** It delineates the types that qualify for component scanning.

```
Filter[] includeFilters() default {};
```


For example:

```
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@ComponentScan(basePackages = "com.bsmlabs.microservices", includeFilters = @ComponentScan.Filter(type= FilterType.ANNOTATION))
public class MovieApplication {
}
```


*5 Types of filter available for* **ComponentScan.Filter**

* ***FilterType.ANNOTATION :** Filter candidates marked with a given annotations. It is part of package org.springframework.core.type.filter.AnnotationTypeFilter*
* ***FilterType.ASSIGNABLE_TYPE:** Filter candidates assignable to a given type. It is part of package org.springframework.core.type.filter.AssignableTypeFilter*
* ***FilterType.ASPECTJ:**Filter candidates matches a given AspectJ type pattern expression. It is part of package org.springframework.core.type.filter.AspectJTypeFilter*
* ***FilterType.REGEX:**Filter candidates matching a given regex pattern. It is part of package org.springframework.core.type.filter.RegexPatternTypeFilter*
* ***FilterType.CUSTOM:** Filter candidates using a given custom org.springframework.core.type.filter.TypeFilter*

**excludeFilters:** It specifies which types are not eligible for component scanning.

```
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@ComponentScan(basePackages = "com.bsmlabs.microservices", excludeFilters = @ComponentScan.Filter(type= FilterType.REGEX))
public class MovieApplication {
}
```


**lazyInit():** It specifies whether scanned beans should be registered for lazy initiation.

Conclusion
----------

The \`@ComponentScan\` annotation streamlines the configuration process of Spring applications by minimizing the need for developers to manually define beans, promoting a convention-over-configuration methodology, and ensuring that the application context contains the essential components.

Reference
---------

<https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/ComponentScan.html>
