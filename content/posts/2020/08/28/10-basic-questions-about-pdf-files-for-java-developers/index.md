---
title: "10 Basic Questions About PDF Files for Java Developers"
date: "2020-08-28T05:45:52+00:00"
lastmod: "2021-08-23T12:55:40+00:00"
description: "PDF files are the world's most common file format, defining 70% of the world's documents. But they are also complex and poorly supported by Java. Tips!"
authors:
  - "mark-stephens"
image: "Favicon-3-2.png"
categories:
  - "Library"
related_posts:
frozen: false
---

PDF files are the world's most common file format, defining 70% of the world's documents. But they are also complex and poorly supported by Java.

As I have spent over 20 years working with Java and PDF files, I thought a useful contribution to the excellent new foojay.io (a place for Friends Of OpenJDK), where you are reading this now, would be a quick guide for Java Developers!

#### 1. What are PDF files?

PDF files are designed to display documents, fast and accurately, on multiple platforms. They do this by storing all the information they need (text, images, fonts) inside a complex binary data structure.

PDF is based on Postscript and you should think of it more as a program than a data dump. The displayed document is what you get when you run the PDF 'program' contained in the PDF file. So you need a program that understands PDF files to display it.

#### 2. Is PDF an open standard?

The PDF file format was created by Adobe but is now an [Open ISO standard](https://www.iso.org/obp/ui/#iso:std:iso:32000:-2:ed-1:v1:en). There are several versions of the PDF file format (all backwards compatible) with PDF 2.0 being the latest.

#### 3. How can I see what a PDF file really looks like?

If you really want to see what a PDF file really looks like inside... try the [RUPS](https://github.com/itext/i7j-rups) diagnostic viewer from IText.

#### 4. How am I likely to meet PDF files?

Because they are very common, you may well need to store them in a database or repository, edit them, extract data from them, or display them.

#### 5. How should I store them?

Always treat a PDF as a blob. If you treat it as a text structure, you will break the internal reference table.

Also, avoid adding padding at the start or end, as this can make the PDF file unusable.

#### 6. What support does Java provide for PDF files?

Java provides no support for PDF files out of the box.

#### 7. Are there any Java libraries out there?

The good news is that there are lots of active libraries, both open source and commercial, available to work with PDF files.

My personal recommendations would be to look at the following:

* [Itext](https://itextpdf.com/en) (commercial/AGPL)
* Apache [PDFBox](https://pdfbox.apache.org) and [FOP](https://xmlgraphics.apache.org/fop/) (Apache License)
* [Qoppa](https://www.qoppa.com) (commercial)
* [Datalogics](https://www.datalogics.com) (commercial)

Also, I am the founder of [IDRsolutions](https://www.idrsolutions.com), and we have a range of commercial Java PDF libraries

#### 8. How easy is it to create/edit a PDF directly?

PDF files store data in a complex hybrid binary/text structure, which you would need to parse and process. It is not a file format which you can easily just scrape the data from.

Our **strong** recommendation is always to use a Java library to work with PDF files. Developers have already spent years solving these complex problems for you.

#### 9. How easy is it to display PDF files?

PDF files need a PDF viewer to display them. If you are building a web application, you can use [PDF.js](https://mozilla.github.io/pdf.js/) to display PDF files in the browser client side or convert them to another format.

It is a sign of how the Java market has changed that most of our clients in 2000 to 2010 wanted a Java client viewer/applet, while the current trend is to convert PDF to Image or to HTML5/SVG and display in a web browser, with no Java on the client.

#### 10. Can I convert PDF files into another file format?

There are lots of software tools to convert PDF into images, HTML, Word, etc.

The thing to remember is that you will lose a lot of the features of the PDF , i.e., it is rescalable, searchable, can include annotations, notes, video, and sound. PDF also supports non-RGB colourspaces.

Conversion can make it much simpler to display PDF files, but much will be 'lost in translation'.

#### Conclusion

Java does not have any support for PDF files out of the box.

If you understand how PDF files work and the tools available to work with them, you can find Java a very productive language for working with them.

If you have any comments or suggestions on these points, drop me a message on Twitter [@markee174](https://twitter.com/search?q=%40markee174&src=typed_query)
