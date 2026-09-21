---
title: "XML Schema Validation 1.1 in Java"
date: "2025-11-10T19:22:44+00:00"
lastmod: "2026-09-21T05:59:51+00:00"
description: "This week, I received an interesting task: dusting off a legacy Java application. The application analyzes specific XML files in proprietary format. I…"
canonical: "https://blog.frankel.ch/xml-schema-validation-1-1/"
authors:
  - "nicolas-frankel"
image: "Favicon-3-2.png"
categories:
  - "Java"
related_posts:
frozen: false
---

This week, I received an interesting task: dusting off a legacy Java application. The application analyzes specific XML files in proprietary format. I know XML doesn't sound sexy to junior developers, but it has an amazing benefit. One can validate a file against a grammar. Such grammar is called an XSD, the acronym for XML Schema Definition. Fun fact: you write XSDs in XML.

In this post, I explain the problem, what I tried, and the final working solution.

## The problem

The good thing is that the application already uses XSD. Yet, at the time of the application's inception, XSD validation was in [version 1.0](https://www.w3.org/TR/xmlschema-1/). [Version 1.1](https://www.w3.org/TR/xmlschema11-1/) added several significant features. In particular, v1.1 adds [assertions](https://www.w3.org/TR/xmlschema11-1/#cAssertions) and [identity constraints](https://www.w3.org/TR/xmlschema11-1/#Identity-constraint_Definition_details).

There were numerous `//TODO validate` comments in the Java code that couldn't be implemented in XSD 1.0 but could in XSD 1.1. I thought it would be easy to upgrade. I was wrong.

## The naive approach

Under the hood, the JDK uses a wrapped Xerces implementation for parsing. For fun, check the `com.sun.org.apache.xerces.internal.jaxp` package in your installed JDKs. This implementation is stuck on XSD validation 1.1.

I assumed that the [regular Xerces](https://github.com/apache/xerces-j) project would have moved on and implemented it. I added the latest version to my build, and... nothing. I dug deeper into the JAR and found the following:
> 

```java
public final class Constants {

    /** XML 1.1 feature ("xml-1.1"). */
    public static final String XML_11_FEATURE = "xml-1.1";

    // Constant to enable Schema 1.1 support
    public final static boolean SCHEMA_1_1_SUPPORT = false;
    public final static short SCHEMA_VERSION_1_0          = 1;
    public final static short SCHEMA_VERSION_1_0_EXTENDED = 2;
}
```

>
> -- [Constants.java](https://github.com/apache/xerces-j/blob/main/src/org/apache/xerces/impl/Constants.java)

If you are puzzled because schema 1.1 support is a constant, welcome to my world. I went further down the rabbit hole. The [Xerces-J](https://github.com/apache/xerces-j/) project features two potentially interesting branches, `xml-schema-1.1-dev` and `xml-schema-1.1-tests`. The `Constants` class in the former branch looks like:
> 

```java
public final class Constants {

    /** XML 1.1 feature ("xml-1.1"). */
    public static final String XML_11_FEATURE = "xml-1.1";

    // Constant to enable Schema 1.1 support
    public final static boolean SCHEMA_1_1_SUPPORT = false;    <1>
    public final static short SCHEMA_VERSION_1_0          = 1;
    public final static short SCHEMA_VERSION_1_0_EXTENDED = 2;
    public final static short SCHEMA_VERSION_1_1 = 4;          <2>
}
```

>
> -- [Constants.java](https://github.com/apache/xerces-j/blob/main/src/org/apache/xerces/impl/Constants.java)

1. Still not supported
2. But looks promising?

I looked for such an artifact on [Maven Central](https://mvnrepository.com/artifact/xerces/xercesImpl): it shows no dedicated artifact.

It didn't come to my mind at the time, but while writing the post, I checked: the [Xerces downloads](https://xerces.apache.org/mirrors.cgi#binary) page does offer a XSD 1.1 distribution. It would mean getting the JAR, creating a dummy POM, and publishing them on the internal Artifactory, which I don't have write access to. Possible, but time-consuming.

## Searching for alternatives

I searched for alternatives, but the only one I found was Saxon.
> The Saxon package is a collection of tools for processing XML documents. The main components are:
>
> * An XSLT processor, which can be used from the command line, or invoked from an application, using a supplied API. Saxon implements the XSLT 3.0 Recommendation. The product can also be used to run XSLT 2.0 stylesheets, or XSLT 1.0 stylesheets in backwards compatibility mode.
> * An XPath processor accessible to applications via a supplied API. This supports XPath 2.0 and XPath 3.1. It can also be used in backwards-compatibility mode to evaluate XPath 1.0 expressions.
> * An XQuery processor that can be used from the command line, or invoked from an application by use of a supplied API. This supports XQuery 3.1, which also allows XQuery 1.0 or 3.0 queries to be executed. With Saxon-EE, you can also use the XQuery extensions defined in the XQuery Update 1.0 Recommendation, but later working drafts of XQuery Update are not supported (W3C has abandoned work on these versions).
> * An XML Schema Processor. *This supports both XSD 1.0 and XSD 1.1*. This can be used on its own to validate a schema for correctness, or to validate a source document against the definitions in a schema. It is also used to support the schema-aware functionality of the XSLT and XQuery processors. Like the other tools, it can be run from the command line, or invoked from an application.
>
> -- [What is Saxon?](https://www.saxonica.com/html/documentation12/about/whatis.html)

After investigating a bit, I found that Sax had two major drawbacks:

* Though it's possible to use the regular JAXP API, you must switch to Saxon's proprietary API to unlock its full power. It includes XSD 1.1 validation.
* Saxon comes in two flavors: the Enterprise Edition is paid, and the Home Edition is free. The latter doesn't offer XSD 1.1 validation.

I had no budget and no time to hunt for it.

## Back to square one

It was time to reassess the situation. Building from source? Not feasible. Saxon? Paying. My last hope was AI. I must say that for once, it was a lifesaver.

It turns out that two builds of Xerces with XSD 1.1 features exist on [Maven Central](https://mvnrepository.com/artifact/org.opengis.cite.xerces). They were published by OpenGIS in 2015-2016, at a time when it was pretty easy to publish there. [Their](https://repo1.maven.org/maven2/org/opengis/cite/xerces/xercesImpl-xsd11-shaded/2.12-beta-r1667115/xercesImpl-xsd11-shaded-2.12-beta-r1667115.pom) [POM](https://repo1.maven.org/maven2/org/opengis/cite/xerces/xercesImpl-xsd11/2.12-beta-r1667115/xercesImpl-xsd11-2.12-beta-r1667115.pom) actually mentions which branch it comes from: `http://svn.apache.org/viewvc/xerces/java/branches/xml-schema-1.1-dev/`.

At this point, I was a few lines of code away from the goal:

```java
var schemaFactory = SchemaFactory.newInstance(Constants.W3C_XML_SCHEMA11_NS_URI); //1
var schema = schemaFactory.newSchema(schemaFile);
var saxParserFactory = SAXParserFactory.newInstance();
saxParserFactory.setNamespaceAware(true);
saxParserFactory.setSchema(schema);                                               //2

var reader = saxParserFactory.newSAXParser().getXMLReader();                      //3
reader.setContentHandler(handler);
reader.setErrorHandler(handler);
reader.setEntityResolver(handler);
```

1. Set the XSD 1.1 version
2. Set the schema to the factory
3. Regular SAX parsing

I still need to properly validate there's no security issue from this "wild" build, but it works: I can leverage features from XSD 1.1!

## Conclusion

In this post, I described my journey implementing XSD 1.1 validation in Java. It's much less easy than I had expected in the first place, but I hope it will help others who find themselves in the same predicament.

**To go further:**

* [Java API for XML Processing (JAXP) Tutorial](https://www.oracle.com/java/technologies/jaxp-introduction.html)
* [The Apache Xerces™ Project](https://xerces.apache.org/)

*Originally published at [A Java Geek](https://blog.frankel.ch/xml-schema-validation-1-1/) on November 9th, 2025*
