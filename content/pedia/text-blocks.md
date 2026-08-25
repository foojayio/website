---
title: "Text Blocks"
description: "Text blocks, finalised in Java 15 (JEP 378), are a multi-line string literal that avoids the need for most escape sequences and manual concatenation when embedding formatted text — SQL, JSON, HTML, XML, code snippets — directly in Java source. ..."
url: "/pedia/text-blocks/"
frozen: false
---

Text blocks, finalised in Java 15 (JEP 378), are a multi-line string literal that avoids the need for most escape sequences and manual concatenation when embedding formatted text — SQL, JSON, HTML, XML, code snippets — directly in Java source.

A text block begins with `"""` followed by a newline, contains the content verbatim (with optional indentation stripping), and closes with `"""`:

```java
String query = """
        SELECT id, name
        FROM users
        WHERE active = true
        ORDER BY name
        """;
```

The compiler automatically strips the common leading whitespace determined by the position of the closing `"""`, so the indented appearance in source code does not appear in the runtime value. Escape sequences (`\n`, `\t`, `\\`) still work inside text blocks, and two new escape sequences were added: `\<newline>` to suppress a line break, and `\s` to preserve trailing whitespace.

Before text blocks, the equivalent code required string concatenation with explicit `\n` characters, making the SQL or JSON structure hard to read and edit. Text blocks make the intent immediately visible and the content directly copy-pasteable from external tools.

Text blocks are plain `String` objects at runtime — there is no new type, no runtime overhead, and no change to the String API.

## See Also

* [Basic Java Concepts](/pedia/basic-java-concepts/)
* [Records](/pedia/records/)
* [Sealed Classes](/pedia/sealed-classes/)
* [Project Amber](/pedia/project-amber/)
