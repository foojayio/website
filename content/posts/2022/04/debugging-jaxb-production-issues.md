---
title: "Debugging JAXB Production Issues"
slug: "debugging-jaxb-production-issues"
date: "2022-04-04T14:13:10+00:00"
lastmod: "2022-04-04T14:13:12+00:00"
description: "Failures in XML parsing can happen in production when the source data changes. These are notoriously hard to debug... At least until now..."
canonical: "https://lightrun.com/tutorials/debugging-jaxb-production-issues/"
authors:
  - "shai-almog"
image: "https://foojay.io/wp-content/uploads/2022/03/Debug-JAXB-Unmarshaling.jpg"
categories:
  - "IntelliJ IDEA"
  - "Tutorials"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Java Architecture for XML Binding (AKA JAXB API) is a popular API for marshalling XML data. It's a framework for mapping between XML documents and Java POJOs (Plain Old Java Objects, AKA regular Java classes) almost seamlessly. The API is very easy to use and many frameworks leverage it to provide their XML support. JAXB2.0 has gained popularity both in desktop applications (Java SE) and in application server code (Spring Boot, Java EE/Jakarta EE, Microprofile etc.).

JAXB requires a runtime library but doesn't require static analysis, XML schema, or anything like that. While the schema isn't required, it's still the basis of a cool JAXB feature: the ability to generate Java sources from source schema!

JAXB supports the bidirectional mapping of XML. It converts objects into XML and can convert an XML file to an object. The central API behind it is the XML marshalling API, which we can leverage using code such as this:

<pre class="EnlighterJSRAW" data-enlighter-language="java">JAXBContext context = JAXBContext.newInstance(obj.getClass());
Marshaller mar = context.createMarshaller();
try(StringWriter writer = new StringWriter()) {
   mar.marshal(obj, writer);
   return writer.toString();
}</pre>

This code converts an arbitrary source object to an XML String using the marshal method. The reverse is also pretty easy:

<pre class="EnlighterJSRAW" data-enlighter-language="java">JAXBContext context = JAXBContext.newInstance(User.class); 
User user = (User) context.createUnmarshaller().unmarshal(new StringReader(xml));</pre>

Notice we need to give the right type of object to serialize into. We do this through JAXB binding, defined as annotations on the POJO. They indicate how an object should be serialized, this is especially true when creating complex hierarchies of objects.

In the demo, I will show you soon. I integrated Lombok to make JAXB code even easier to write. This posed some challenges though, as you can see from this class:

<pre class="EnlighterJSRAW" data-enlighter-language="java">@Data
@AllArgsConstructor
@XmlRootElement(name = "history")
@XmlAccessorType(XmlAccessType.FIELD)
public class History {
   private String objectId;
   private int ordinal;

   @XmlJavaTypeAdapter(InstantAdapter.class)
   private Instant time;
   private Action action;
   private String userId;

   public History() {}
}</pre>

You will notice several points of interest in the class definition above:

* The first two annotations are Lombok annotations. They are effectively the equivalent of adding getters/setters to all the fields, equals, toString methods and a constructor that accepts all the arguments
* The root element annotation is pretty self-explanatory
* The accessor type annotation is something you should do for Lombok otherwise you might run into a conflict between the field and the generated setters/getters with JAXB
* The adapter for time is used to map the field. Instant object types aren't supported in JAXB by default, so we need to map them using custom code
* Finally, a default constructor is required for JAXB

As you may have noticed, we also have another class, which is the adapter class. It's pretty trivial:

<pre class="EnlighterJSRAW" data-enlighter-language="java">public class InstantAdapter extends XmlAdapter&lt;String, Instant&gt; {
   @Override
   public Instant unmarshal(String v) throws Exception {
       return Instant.ofEpochMilli(Long.parseLong(v));
   }

   @Override
   public String marshal(Instant v) throws Exception {
       return "" + v.toEpochMilli();
   }
}</pre>

JAXB binding invokes this code every time it needs to parse or generate XML for a field annotated with this adapter.

The resulting XML for the History class would look something like this (formatted for clarity):

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;?xml version="1.0" encoding="UTF-8" standalone="yes"?&gt;
&lt;history&gt;
    &lt;objectId&gt;1f3d3cf7-ed7e-4e49-be29-0129a537f3dc&lt;/objectId&gt;
    &lt;ordinal&gt;0&lt;/ordinal&gt;
    &lt;time&gt;1643020804619&lt;/time&gt;
    &lt;action&gt;CREATED&lt;/action&gt;
    &lt;userId&gt;shai&lt;/userId&gt;
&lt;/history&gt;</pre>

**TIP:** Java SE bundled JAXB 2.0 as part of the Java 8 release (AKA JDK/Java 1.8). Unfortunately, Java 9 removed it. It's important that you explicitly include it in your Maven/Gradle dependencies for compatibility between the various versions of Java SE.

The XML Database Demo {#h2-0-the-xml-database-demo}
---------------------------------------------------

To show the power and ease of JAXB, I created a simple demo that uses JAXB to store/read the objects stored within it. You can find the source code [here](https://github.com/lightrun-platform/lightrun/tree/main/examples).

The demo includes a simple web service that accepts typical restful requests and stores each request as an XML file. There's also history and user authentication, all of which map to XML storage/reading using the JAXB API.

We can debug this project, but this is a non-transferable skill. Your project will look different. Instead, we will debug the JAXB implementation. This will work even for your custom projects using roughly the same process.

Debugging JAXB In Production {#h2-1-debugging-jaxb-in-production}
-----------------------------------------------------------------

We need to start by setting up Lightrun and running your project with the Lightrun agent. I'll skip the details as the getting started process covers them well.

In the case of the XML demo, we can run it with Lightrun using the command:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">java -agentpath:./agent/lightrun_agent_x86.so -jar target/jaxb-0.0.1-SNAPSHOT.jar</pre>

Once it's running, we can open the project in the IDE. I'll show the rest in IntelliJ/IDEA, but VSCode should work just fine.

As you recall from the code above, we can decode XML with the JAXB unmarshaller and encode it with marshaller. Our first step is to look for the marshaller. Using the Control-O shortcut (or Command-O on Mac) we can open the find class dialog and look for the Marshal interface:

![Screen Shot 2022-01-27 at 15.14.04.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1648536541148/GffXo6YTV.png)

This is why naming is so important. We can see that the JAXB runtime jar has a class that looks like the right implementation of the JAXB API. When we open it, we get the download source prompt (which you must click).

Browsing through the code, we find the marshal methods that we invoke normally. They all invoke an internal write method. That makes it the perfect location for a snapshot if we want to debug any XML writing logic:

Before we can see this happening, we need to create a new database user which we can do using the following curl command:

![Screen Shot 2022-01-27 at 15.18.42.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1648536726722/EXR0PHzNg.png)

<pre class="EnlighterJSRAW" data-enlighter-language="generic">curl -X PUT -H "Content-Type: application/json" -d '{"login":"shai","password":"123456"}' http://localhost:8080/addUser</pre>

This command will return a user token which you can use in the following curl command. In my case, the token is: `cf3d2809-bd25-4f34-afe0-d4dd6b04cb87`.

Then we can just add a new command and replace my token with yours:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">curl -X POST -H "Content-Type: application/json" -H "Authorization: 46d37d7a-5984-4acd-8932-f12c1a475d4f" -d '{"coreData":[20,22,22,22,33,44]}' http://localhost:8080/create</pre>

After that command is sent, you will see the snapshot representing the XML marshaling:

![Screen Shot 2022-01-27 at 15.21.56.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1648537102216/ogc6F0iMM.png)

In the variables view, you can deeply inspect every property, including the state of the JAXB reference implementation and every aspect of the request. The really cool thing is that you can go up the stack and see how you got there, literally inspect the object you passed into the JAXB API:

![Screen Shot 2022-01-27 at 15.28.29.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1648537232346/iNvT4AX2X.png)

JAXB Unmarshaller {#h2-2-jaxb-unmarshaller}
-------------------------------------------

The reading process is practically identical to the writing process. We open a class file and type Unmarshall into it (I didn't even need to finish typing):

![Screen Shot 2022-01-27 at 15.30.29.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1648537466786/_kiT_rZj4.png)

The UnmarshallerImpl class includes a similar internal implementation method, specifically unmarshal0, which uses the SAX approach to parse the XML file.

We can then use the following curl command to read the previously added entry:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">curl -H "Content-Type: application/json" -H "Authorization: 46d37d7a-5984-4acd-8932-f12c1a475d4f" "http://localhost:8080/read?id=cf3d2809-bd25-4f34-afe0-d4dd6b04cb87"</pre>

You need to update authorization like before. Notice you can set the value for the ID parameter from the values we saw in the previous stack:

![Screen Shot 2022-01-27 at 16.06.26.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1648537592431/5eqDoijKv.png)

Once we have that in place, we can add a snapshot and see the resulting stack:

![Screen Shot 2022-01-27 at 16.04.41.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1648537705035/i3mstdc2B.png)

We can dig deeper similarly into the sax parser content tree and inspect an individual XML element. This approach makes it much easier to debug applications with XML.

TL;DR {#h2-3-tl-dr}
-------------------

The JAXB runtime API is a powerful tool for converting generic types to XML and vice versa. It's popular in Java EE (Jakarta EE), Spring and pretty much all application server platforms. JAXB 2.0 was bundled in Java 8 but removed in Java 9, which means the runtime API must be included as a dependency.

JAXB unmarshaller can map class files to XML files almost seamlessly. It uses the SAX parser internally for faster performance. The SAX approach uses an event-based parsing system that skips the DOM approach.

When debugging, we can place a snapshot directly into the parsing or writing code in the JAXB RI. These steps will work regardless of your code. We can use the snapshot stack to go back in the tree to the source object and inspect values there.
