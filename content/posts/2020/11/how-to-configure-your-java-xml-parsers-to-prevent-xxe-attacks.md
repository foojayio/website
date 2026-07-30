---
title: "How to Configure Your Java XML Parsers to Prevent XXE Attacks"
slug: "how-to-configure-your-java-xml-parsers-to-prevent-xxe-attacks"
date: "2020-11-06T11:27:18+00:00"
lastmod: "2020-11-06T11:44:16+00:00"
description: "With XML eXternal Entity (XXE) enabled, it is possible to create a malicious XML, and read the content of an arbitrary file on the machine."
authors:
  - "bmvermeer"
image: "https://foojay.io/wp-content/uploads/2020/04/Favicon-3-2.png"
categories:
  - "Security"
tags:
related_posts:
enlighterjs: true
frozen: false
---

With XML eXternal Entity (XXE) enabled, it is possible to create a malicious XML, as shown below, and read the content of an arbitrary file on the machine. It's not a surprise that XXE attacks are part of the OWASP Top 10 vulnerabilities. Java XML libraries are particularly vulnerable to XXE injection because most XML parsers have external entities by default enabled.

<pre class="EnlighterJSRAW" data-enlighter-language="xml" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;?xml version="1.0" encoding="UTF-8" standalone="yes"?&gt;
&lt;!DOCTYPE bar [
       &lt;!ENTITY xxe SYSTEM "file:///etc/passwd"&gt;]&gt;
&lt;song&gt;
   &lt;artist&gt;&amp;xxe;&lt;/artist&gt;
   &lt;title&gt;Bohemian Rhapsody&lt;/title&gt;
   &lt;album&gt;A Night at the Opera&lt;/album&gt;
&lt;/song&gt;</pre>

A naive implementation of the DefaultHandler and the Java SAX parser, like that shown below, parses this XML file and reveals the content of the passwd file. The Java SAX parser case is used as the main example here but other parsers, like DocumentBuilder and DOM4J, have similar default behaviour.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">SAXParserFactory factory = SAXParserFactory.newInstance();
SAXParser saxParser = factory.newSAXParser();

DefaultHandler handler = new DefaultHandler() {

    public void startElement(String uri, String localName,String qName,Attributes attributes) throws SAXException {
        System.out.println(qName);
    }

    public void characters(char ch[], int start, int length) throws SAXException {
        System.out.println(new String(ch, start, length));
    }
};
</pre>

Changing the default settings to disallow external entities and doctypes for [xerces1](https://xerces.apache.org/xerces-j/) or [xerces2](https://xerces.apache.org/xerces2-j/), respectively, prevents these kinds of attacks.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">...
SAXParserFactory factory = SAXParserFactory.newInstance();
SAXParser saxParser = factory.newSAXParser();

factory.setFeature("https://xml.org/sax/features/external-general-entities", false);
saxParser.getXMLReader().setFeature("https://xml.org/sax/features/external-general-entities", false);
factory.setFeature("https://apache.org/xml/features/disallow-doctype-decl", true); 
...</pre>

For more hands-on information about preventing malicious XXE injection, please take a look at the [OWASP XXE Cheatsheet](https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html).

This was just 1 of 10 Java security best practices. Take a look at [the full 10](https://snyk.io/blog/10-java-security-best-practices/) and the easy [printable one-pager](https://snyk.io/blog/10-java-security-best-practices/) available.
