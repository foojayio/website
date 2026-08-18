---
title: "How to Configure Your Java XML Parsers to Prevent XXE Attacks"
date: "2020-11-06T11:27:18+00:00"
lastmod: "2020-11-06T11:44:16+00:00"
description: "With XML eXternal Entity (XXE) enabled, it is possible to create a malicious XML, and read the content of an arbitrary file on the machine."
authors:
  - "bmvermeer"
image: "Favicon-3-2.png"
categories:
  - "Security"
tags:
related_posts:
frozen: false
---

With XML eXternal Entity (XXE) enabled, it is possible to create a malicious XML, as shown below, and read the content of an arbitrary file on the machine. It's not a surprise that XXE attacks are part of the OWASP Top 10 vulnerabilities. Java XML libraries are particularly vulnerable to XXE injection because most XML parsers have external entities by default enabled.

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<!DOCTYPE bar [
       <!ENTITY xxe SYSTEM "file:///etc/passwd">]>
<song>
   <artist>&xxe;</artist>
   <title>Bohemian Rhapsody</title>
   <album>A Night at the Opera</album>
</song>
```

A naive implementation of the DefaultHandler and the Java SAX parser, like that shown below, parses this XML file and reveals the content of the passwd file. The Java SAX parser case is used as the main example here but other parsers, like DocumentBuilder and DOM4J, have similar default behaviour.

```java
SAXParserFactory factory = SAXParserFactory.newInstance();
SAXParser saxParser = factory.newSAXParser();

DefaultHandler handler = new DefaultHandler() {

    public void startElement(String uri, String localName,String qName,Attributes attributes) throws SAXException {
        System.out.println(qName);
    }

    public void characters(char ch[], int start, int length) throws SAXException {
        System.out.println(new String(ch, start, length));
    }
};
```

Changing the default settings to disallow external entities and doctypes for [xerces1](https://xerces.apache.org/xerces-j/) or [xerces2](https://xerces.apache.org/xerces2-j/), respectively, prevents these kinds of attacks.

```java
...
SAXParserFactory factory = SAXParserFactory.newInstance();
SAXParser saxParser = factory.newSAXParser();

factory.setFeature("https://xml.org/sax/features/external-general-entities", false);
saxParser.getXMLReader().setFeature("https://xml.org/sax/features/external-general-entities", false);
factory.setFeature("https://apache.org/xml/features/disallow-doctype-decl", true); 
...
```

For more hands-on information about preventing malicious XXE injection, please take a look at the [OWASP XXE Cheatsheet](https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html).

This was just 1 of 10 Java security best practices. Take a look at [the full 10](https://snyk.io/blog/10-java-security-best-practices/) and the easy [printable one-pager](https://snyk.io/blog/10-java-security-best-practices/) available.
