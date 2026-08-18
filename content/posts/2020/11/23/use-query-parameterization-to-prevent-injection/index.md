---
title: "Use Query Parameterization to Prevent Injection"
slug: "use-query-parameterization-to-prevent-injection"
date: "2020-11-23T08:14:52+00:00"
lastmod: "2020-11-23T08:14:54+00:00"
description: "Tip: By distinguishing between the SQL code and the parameter data, the query can’t be hijacked by malicious input."
authors:
  - "bmvermeer"
image: "Favicon-3-2.png"
categories:
  - "Security"
tags:
related_posts:
frozen: false
---

In the 2017 version of the OWASP Top 10 vulnerabilities, injection appeared at the top of the list as the number one vulnerability that year.

When looking at a typical SQL injection in Java, the parameters of a sequel query are naively concatenated to the static part of the query. The following is an unsafe execution of SQL in Java, which can be used by an attacker to gain more information than otherwise intended:

```java
public void selectExample(String parameter) throws SQLException {
   Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
   String query = "SELECT * FROM USERS WHERE lastname = " + parameter;
   Statement statement = connection.createStatement();
   ResultSet result = statement.executeQuery(query);

   printResult(result);
}
```

If the parameter in this example is something like `'' OR 1=1`, the result contains every single item in the table. This could be even more problematic if the database supports multiple queries and the parameter would be `''; UPDATE USERS SET lastname=''`.

To prevent this in Java, we should parameterize the queries by using a prepared statement. This should be the only way to create database queries. By defining the full SQL code and passing in the parameters to the query later, the code is easier to understand. Most importantly, by distinguishing between the SQL code and the parameter data, the query can't be hijacked by malicious input.

```java
public void prepStatmentExample(String parameter) throws SQLException {
   Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
   String query = "SELECT * FROM USERS WHERE lastname = ?";
   PreparedStatement statement = connection.prepareStatement(query);
   statement.setString(1, parameter);
   System.out.println(statement);
   ResultSet result = statement.executeQuery();

   printResult(result);
}
```

In the example above, the input binds to the type String and therefore is part of the query code. This technique prevents the parameter input from interfering with the SQL code.

This was just 1 of 10 Java security best practices. Take a look at [the full 10](https://snyk.io/blog/10-java-security-best-practices/) and the easy [printable one-pager](https://snyk.io/blog/10-java-security-best-practices/) available.
