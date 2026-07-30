---
title: "Spring: Internals of @ComponentScan"
slug: "spring-internals-of-componentscan"
date: "2024-08-20T14:48:28+00:00"
lastmod: "2024-08-31T09:24:52+00:00"
description: "Explore the nuances of Spring's @ComponentScan in our extensive guide. Gain insights into its internals and optimize your application's configuration effectively."
authors:
  - "mahendra1413"
image: "https://foojay.io/wp-content/uploads/2024/08/componentscan.png"
categories:
  - "Java"
  - "Spring"
  - "Tutorials"
tags:
related_posts:
enlighterjs: true
frozen: false
---

**The `@ComponentScan` annotation, which is an interface located within the `org.springframework.context.annotation` package, facilitates component scanning in a Spring application. This allows the Spring Container to automatically detect beans.**

Spring automatically identifies Java classes that developers mark with stereotypes like `@Configuration, @Component, @Service, @Controller, and @Repository`. Developers can annotate the component scan with or without parameters.

You can designate either `basePackageClasses() or basePackages()` (or its equivalent `value()`) to identify the specific packages that need to be scanned.

The `@ComponentScan` annotation, when used without any arguments, instructs Spring to scan the current package and all its sub-packages.

For instance:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import org.springframework.context.annotation.ComponentScan;
@ComponentScan
public class MovieApplication {
}</pre>

The `@ComponentScan` annotation instructs Spring to perform a scan of the specified package when you provide it with arguments, as indicated by the `basePackages` attribute. For instance:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "com.bsmlabs.microservices")
public class MovieApplication {}</pre>

The `@ComponentScan` annotation acts as a substitute for the \<context:component-scan/\> XML tag. While it includes an annotation-config attribute, the XML version lacks this feature. In most scenarios where developers utilize `@ComponentScan`, they presume that default annotation configuration processing is in effect, such as the handling of `@Autowired`.

The component scan interface looks like

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Repeatable(ComponentScans.class)
public @interface ComponentScan {
}</pre>

Since the `RetentionPolicy` of ComponentScan is Runtime, it will only execute during the **runtime** phase. You can apply this annotation at the *class level* , as well as at the *interface or enum declaration levels* . Therefore, you specify its `@Target` as ElementType with Type, specifically `@Target(ElementType.TYPE)`.

In the `SpringBootApplication` class, specifies the `@ComponentScan` annotation at the class level to enable component scanning at runtime which helps to create

* ApplicationContext()
* Register Environment
* Loads Beans Definitions
* Applying the ApplicationContextInitializer and ApplicationContextInitilizerEvent

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = { @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
        @Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public @interface SpringBootApplication {

}</pre>

*** ** * ** ***

Attributes used with ComponentScan annotation {#h2-0-attributes-used-with-componentscan-annotation}
---------------------------------------------------------------------------------------------------

The `value()` function acts as an alias for basePackages, allowing developers to make more specific annotation declarations when they do not require additional attributes. For instance, developers can use `@ComponentScan("com.bsmlabs.microservices")` as an alternative to `@ComponentScan(basePackages = "com.bsmlabs.microservices")`.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@AliasFor("basePackages")
String[] value() default {};</pre>

1. 

The `basePackages()` identifies annotated components, using the value as an alias for this attribute. To find a type-safe alternative to Spring-based package names, one can employ `basePackageClasses`.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@AliasFor("value")
String[] basePackages() default {};</pre>

For example:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import org.springframework.context.annotation.ComponentScan;
@ComponentScan(basePackages = "com.bsmlabs.microservices")
public class MovieApplication {
}</pre>

The `basePackageClasses()` method outlines the process of scanning all packages that contain annotated components. This indicates that the scanning procedure will be applied to the package associated with each specified class.

For example:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import org.springframework.context.annotation.ComponentScan;
@ComponentScan(basePackageClasses = {"com.bsmlabs.microservices.movie", "com.bsmlabs.microservices.payment"})
public class MovieApplication {
}</pre>

`nameGenerator()`: The Spring container specifically designates the `BeanNameGenerator` class for assigning names to the components it identifies within the `ApplicationContext`.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">Class&lt;? extends BeanNameGenerator&gt; nameGenerator()
default BeanNameGenerator.class;</pre>

The inherent value of the `BeanNameGenerator` interface signifies that the scanner responsible for processing the `@ComponentScan` annotation should use its inherited bean name generator.

Default [AnnotationBeanNameGenerator](https://www.codota.com/code/java/classes/org.springframework.context.annotation.AnnotationBeanNameGenerator) or any custom instance supplied to the application context at startup or bootstrap time.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@ComponentScan(nameGenerator = FullBeanNameGenerator.class)
public class MovieApplication {
}</pre>

`scopeResolver`: The `ScopeMetadataResolver` is utilized for determining the scope of identified components.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">Class&lt;? extends ScopeMetadataResolver&gt; scopeResolver() default AnnotationScopeMetadataResolver.class;</pre>

For example:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ScopedProxyMode;

@ComponentScan(basePackages = "com.bsmlabs.microservices", scopeResolver = MyResolverBean.class)
public class MovieApplication {
}</pre>

**scopedProxy:** This specifies whether spring framework should create proxies for identified components, which may be essential when they employ scopes in a proxy-oriented manner.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ScopedProxyMode;

@ComponentScan(basePackages = "com.bsmlabs.microservices", scopedProxy = ScopedProxyMode.DEFAULT)
public class MovieApplication {
}</pre>

**useDefaultFilters:** This specifies the automatic detection of classes that are annotated with `@Component`, `@Controller`, `@Service`, and `@Repository`. The values can be set to either *false* or *true* , with the default being *true*.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">boolean useDefaultFilters() default true;</pre>

For example:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import org.springframework.context.annotation.ComponentScan;
@ComponentScan(basePackages = "com.bsmlabs.microservices", useDefaultFilters = false)
public class MovieApplication {
}</pre>

**includeFilters:** It delineates the types that qualify for component scanning.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">Filter[] includeFilters() default {};</pre>

For example:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@ComponentScan(basePackages = "com.bsmlabs.microservices", includeFilters = @ComponentScan.Filter(type= FilterType.ANNOTATION))
public class MovieApplication {
}</pre>

*5 Types of filter available for* **ComponentScan.Filter**

* ***FilterType.ANNOTATION :** Filter candidates marked with a given annotations. It is part of package org.springframework.core.type.filter.AnnotationTypeFilter*
* ***FilterType.ASSIGNABLE_TYPE:** Filter candidates assignable to a given type. It is part of package org.springframework.core.type.filter.AssignableTypeFilter*
* ***FilterType.ASPECTJ:**Filter candidates matches a given AspectJ type pattern expression. It is part of package org.springframework.core.type.filter.AspectJTypeFilter*
* ***FilterType.REGEX:**Filter candidates matching a given regex pattern. It is part of package org.springframework.core.type.filter.RegexPatternTypeFilter*
* ***FilterType.CUSTOM:** Filter candidates using a given custom org.springframework.core.type.filter.TypeFilter*

**excludeFilters:** It specifies which types are not eligible for component scanning.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@ComponentScan(basePackages = "com.bsmlabs.microservices", excludeFilters = @ComponentScan.Filter(type= FilterType.REGEX))
public class MovieApplication {
}</pre>

**lazyInit():** It specifies whether scanned beans should be registered for lazy initiation.

Conclusion {#h2-1-conclusion}
-----------------------------

The \`@ComponentScan\` annotation streamlines the configuration process of Spring applications by minimizing the need for developers to manually define beans, promoting a convention-over-configuration methodology, and ensuring that the application context contains the essential components.

Reference {#h2-2-reference}
---------------------------

<https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/ComponentScan.html>
