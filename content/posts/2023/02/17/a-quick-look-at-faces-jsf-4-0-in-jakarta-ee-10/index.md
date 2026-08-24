---
title: "A Quick Look At Faces (JSF) 4.0 In Jakarta EE 10"
date: "2023-02-17T09:07:05+00:00"
lastmod: "2023-02-17T09:07:07+00:00"
description: "Learn why Jakarta Faces is a great choice especially as a Java developer when you need a UI framework for your application!"
canonical: "https://blog.payara.fish/a-quick-look-at-faces-jsf-4.0-in-jakarta-ee-10"
authors:
  - "jadon-ortlepp"
  - "luqman-saeed"
image: "Screenshot_20230110_135104.png"
categories:
  - "Jakarta EE"
  - "Tools"
related_posts:
  - "easy-jakarta-ee-integration-testing"
  - "evolution-of-microservices"
  - "migrating-from-java-ee-to-jakarta-ee-with-intellij-idea"
  - "getting-started-with-apache-camel-on-jakarta-ee-10"
frozen: false
---

Jakarta EE 10 shipped with the fourth major release of its component based web framework Jakarta Faces.

Hitherto known as Jakarta Server Faces, and Java Server Faces before that, [Jakarta Faces](https://blog.payara.fish/getting-started-with-jakarta-ee-9-jakarta-faces-jsf), or just Faces, [version 4.0](https://jakarta.ee/specifications/faces/4.0/) is the first major version with API change since version 2.3 in Java EE 8.

Among the major changes in this version are the following.

* [Issue #1581](https://github.com/eclipse-ee4j/faces-api/issues/1581): New API to programmatically create Facelets
* [Issue #1508](https://github.com/eclipse-ee4j/faces-api/issues/1508): New automatic extensionless mapping
* [Issue #1509](https://github.com/eclipse-ee4j/faces-api/issues/1509): New annotation @ClientWindowScoped
* [Issue #1555](https://github.com/eclipse-ee4j/faces-api/issues/1555): New attribute \<h:inputFile multiple="…"\>
* [Issue #1556](https://github.com/eclipse-ee4j/faces-api/issues/1556): New attribute \<h:inputFile accept="…"\>
* [Issue #1559](https://github.com/eclipse-ee4j/faces-api/issues/1559): New tag \<f:selectItemGroups\>
* [Issue #1560](https://github.com/eclipse-ee4j/faces-api/issues/1560): New attribute \<h:inputText type="…"\>
* [Issue #1563](https://github.com/eclipse-ee4j/faces-api/issues/1563): New tag \<f:selectItemGroup\>
* [Issue #1565](https://github.com/eclipse-ee4j/faces-api/issues/1565): Skip type attribute from \<link\> and \<script\> when doctype is HTML5

There were also some pruning of the API such as:

* [Issue #1552](https://github.com/eclipse-ee4j/faces-api/issues/1552): Rename "JSF" to "Faces" over all place
* [Issue #1553](https://github.com/eclipse-ee4j/faces-api/issues/1553): Rename "<http://xmlns.jcp.org/jsf/>*" URL to "jakarta.faces.*" URN
* [Issue #1546](https://github.com/eclipse-ee4j/faces-api/issues/1546): Remove all JSP support
* [Issue #1547](https://github.com/eclipse-ee4j/faces-api/issues/1547): Remove native Managed Beans (@ManagedBean and related)
* [Issue #1548](https://github.com/eclipse-ee4j/faces-api/issues/1548): Remove MethodBinding, ValueBinding and friends
* [Issue #1571](https://github.com/eclipse-ee4j/faces-api/issues/1571): Remove CURRENT_COMPONENT constants from UIComponent class
* [Issue #1578](https://github.com/eclipse-ee4j/faces-api/issues/1578): Remove deprecated methods of StateManager class
* [Issue #1583](https://github.com/eclipse-ee4j/faces-api/issues/1583): Remove entire ResourceResolver class

As you can see, there was a lot of changes.

In this article, we take a look at the programmatic API, extensionless mapping, @ClientWindowScoped, and multi-file upload.

## Programmatic View API In Faces 4

The only way to create views in previous versions of Faces was through .xhtml files.

With Faces 4, a new Java API is available for creating views, without the need for .xhtml files.

A programmatic view is a Java class that extends jakarta.faces.view.facelets.Facelet, annotated with the new jakarta.faces.annotation.View CDI qualifier, passing in the view ID pattern.

The code below shows a Greet.java Facelet that is mapped to the /greet.xhtml path.

```java
@View("/greet.xhtml")
@ApplicationScoped
public class Greet extends Facelet {

   @Override
   public void apply(FacesContext facesContext, UIComponent root) throws IOException {
      if (!facesContext.getAttributes().containsKey(IS_BUILDING_INITIAL_STATE)) {
         return;
      }

      var components = new ComponentBuilder(facesContext);
      var rootChildren = root.getChildren();

      var doctype = new UIOutput();
      doctype.setValue("<!DOCTYPE html>");
      rootChildren.add(doctype);

      var htmlTag = new UIOutput();
      htmlTag.setValue("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
      rootChildren.add(htmlTag);

      HtmlBody body = components.create(HtmlBody.COMPONENT_TYPE);
      rootChildren.add(body);

      HtmlForm form = components.create(HtmlForm.COMPONENT_TYPE);
      form.setId("form");
      body.getChildren().add(form);

      HtmlOutputText message = components.create(HtmlOutputText.COMPONENT_TYPE);
      message.setId("message");

      HtmlCommandButton actionButton = components.create(HtmlCommandButton.COMPONENT_TYPE);
      actionButton.setId("button");
      actionButton.addActionListener(
            e -> message.setValue("Hello, World! Welcome to Faces 4.0 on Jakarta EE 10"));
      actionButton.setValue("Greet");

      form.getChildren().add(actionButton);

      root.getChildren().add(message);

      htmlTag = new UIOutput();
      htmlTag.setValue("</html>");
      rootChildren.add(htmlTag);
   }

   private static class ComponentBuilder {
      FacesContext facesContext;

      ComponentBuilder(FacesContext facesContext) {
         this.facesContext = facesContext;
      }

      @SuppressWarnings("unchecked")
      <T> T create(String componentType) {
         return (T) facesContext.getApplication().createComponent(facesContext, componentType, null);
      }
   }
}
```

The class is annotated @View("/greet.xhtml"), meaning the created component should be hosted/accessible at that path.

The apply method creates an HTML doctype, literal html element, a form, an output text and a button.

The button has a click listener that sets the message value on the output text.

The Faces Java API is very similar to any Java GUI building API like JavaFX, Swing and Vaadin.

All the Faces components used in Greet.java have Java methods for setting the various values and properties you would if you had created a .xhtml Facelets file.

Accessing this UI at the path <http://localhost:8080/jakarta-faces/greet.xhtml> renders the following.

![Screenshot_20230110_125156](https://blog.payara.fish/hs-fs/hubfs/Screenshot_20230110_125156.png?width=615&height=143&name=Screenshot_20230110_125156.png)

Clicking the Greet button renders the message passed in the click listener in the code as shown below.

![Screenshot_20230110_125324](https://blog.payara.fish/hs-fs/hubfs/Screenshot_20230110_125324.png?width=615&height=143&name=Screenshot_20230110_125324.png)

The Faces Java API is very new, and has some missing functionality.

However, for developers that prefer to create their application views in Java, this API is a welcome development.

Whether it will be developed further is going to be a matter of how many people adopt this API and give feedback.

## Extensionless Mapping

Hitherto, accessing a view in the browser meant having to add the .xhtml file extension to the view. So for instance, to access the view index.xhtml, you needed to add the literal .xhtml in the browser.

With Faces 4.0, you can now enable extensionless mapping and have /index automatically resolved to /index.xhtml by the FacesServlet as shown below. The first image shows the extensionless mapping to /index.
![Screenshot_20230110_130210](https://blog.payara.fish/hs-fs/hubfs/Screenshot_20230110_130210.png?width=615&height=143&name=Screenshot_20230110_130210.png)

The same url can be accessed in the old way still via /index.xhtml as shown below.

![Screenshot_20230110_130327](https://blog.payara.fish/hs-fs/hubfs/Screenshot_20230110_130327.png?width=615&height=143&name=Screenshot_20230110_130327.png)

This extensionless mapping feature can be enabled in the web.xml file via context param as shown below.

```xml
 <context-param>
     <param-name>jakarta.faces.AUTOMATIC_EXTENSIONLESS_MAPPING</param-name>
     <param-value>true</param-value>
 </context-param>
```

In my test run, I found that extensionless mapping didn't seem to work with programmatic UIs. This is something you should test for yourself and keep in mind.

## @ClientWindowScoped

Faces 4.0 comes with the @ClientWindowScoped bean scope that is based on the jakarta.faces.lifecycle.ClientWindow.

This scope keeps a backing/managed bean alive as long as the auto-generated jfwid query parameter value as shown in the image below is not changed, or the same URL isn't opened in another browser window/tab.

As long as the generated value is the same, the bean instance will be kept across different page navigations.
![Screenshot_20230110_134123](https://blog.payara.fish/hs-fs/hubfs/Screenshot_20230110_134123.png?width=782&height=143&name=Screenshot_20230110_134123.png)

This feature can be enabled through the context param as shown below.

```xml
<context-param>
    <param-name>jakarta.faces.CLIENT_WINDOW_MODE</param-name>
    <param-value>url</param-value>
</context-param>
```

The number of client window scoped bean instances per HTTP session can be set through the context param as shown below.

```xml
<context-param>
    <param-name>jakarta.faces.NUMBER_OF_CLIENT_WINDOWS</param-name>
    <param-value>20</param-value>
</context-param>
```

## Multiple File Upload

The \<h:inputFile/\> has a new multiple attribute that when set to true, allows the upload of multiple files.

Another new attribute, accept, helps to restrict the file types that the browser will display/allow to select.

The file-upload.xhmtl file below shows a simple example of multi-file upload using these new attributes.

```html
<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE html>
<html lang="en" xmlns:h="jakarta.faces.html">
<h:head>
    <title>Upload files</title>
</h:head>
<h:body>
    <h2>Faces multi-file upload</h2>
    <h:form id="uploadPanel" enctype="multipart/form-data">
        <h:panelGrid columns="2" styleClass="default">
            <h:inputFile  id="inputFiles"  multiple="true" value="#{fileUploadBean.files}" accept="image/jpeg,image/png,image/gif" required="true"/>
            <h:commandButton value="Upload"
                             action="#{fileUploadBean.uploadFile}"/>
        </h:panelGrid>
    </h:form>
</h:body>
</html>
```

The above facelet file renders as shown below.

![Screenshot_20230110_135104](https://blog.payara.fish/hs-fs/hubfs/Screenshot_20230110_135104.png?width=782&height=204&name=Screenshot_20230110_135104.png)

The backing bean of this page is shown below. Faces will conveniently skip empty files, that is files with empty name or zero file size.

You still will have to however, validate that the file types selected meet business requirements.

```java
@Named
@Model
public class FileUploadBean {
    private List<Part> files;
    private static Logger logger = Logger.getLogger(FileUploadBean.class.getName());

    public String uploadFile() {
        for (final var part : files) {

            //Process files, carry out custom validations etc etc.
            logger.log(Level.INFO, () -> String.format("File name: %s, Content-type: %s, File size: %d", part.getSubmittedFileName(),
                    part.getContentType(), part.getSize()));
        }
        return "upload-success.xhtml?faces-redirect=true";
    }

}
```

The files list will contain the uploaded files for the bean to validate and consequently upload/store in the database or however the business case demands.

## Conclusion

As you can see, Faces 4.0 has a number of major and quality of life features that makes this venerable framework even more productive.

Despite the meteoric rise of "heavy clients" in the form of JavaScript/TypeScript frontend frameworks, server side web application frameworks like Jakarta Faces are still important for many enterprises that need a lower learning curve and much more easy to pick up UI development platform.

Jakarta Faces is a great choice especially as a Java developer when you need a UI framework for your application.
