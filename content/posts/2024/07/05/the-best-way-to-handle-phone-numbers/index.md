---
title: "The Best Way to Handle Phone Numbers"
slug: "the-best-way-to-handle-phone-numbers"
date: "2024-07-05T07:00:23+00:00"
lastmod: "2024-09-18T06:31:21+00:00"
description: "Processing phone numbers seems complicated at first glance because of the many different formats."
authors:
  - "simon-martinelli"
image: "chemical-scientist-test-chemistry-chemist-student.jpg"
categories:
  - "Java"
  - "Java Core"
tags:
related_posts:
  - "foojay-podcast-36"
  - "foojay-podcast-46"
  - "foojay-podcast-53"
enlighterjs: true
frozen: false
---

Processing phone numbers seems complicated at first glance because of the many different formats. In this post, I'll show you that with libphonenumber, it becomes child's play. I'll also recommend how to store the phone number in the database.

## Introduction Google's libphonenumber

According to the GitHub repository <https://github.com/google/libphonenumber/> libphone number is:
> <br />
>
> Google's common Java, C++ and JavaScript library for parsing, formatting, and validating international phone numbers. The Java version is optimized for running on smartphones, and is used by the Android framework since 4.0 (Ice Cream Sandwich).

Let's see the library in action. The source code is available here: <https://github.com/simasch/libphonenumber-demo>

## Creating and Formatting Phone Numbers

The main class of libphonenumber is, no surprise, the class `Phonenumber`. You'll want to use a phone number in two ways to create a phone number.

First, from country code and national number:

```java
Phonenumber.PhoneNumber swissPhoneNumber = new Phonenumber.PhoneNumber();
swissPhoneNumber.setCountryCode(41);
swissPhoneNumber.setNationalNumber(324556677L);
```

This will print:

```
+41 32 455 66 77
032 455 66 77
```

## Parsing Phone Numbers

But what if we get the phone number as a string? No worries, the `PhoneNumberUtil` cannot only format but also parse phone numbers:

```java
Phonenumber.PhoneNumber parsedSwissPhoneNumber = 
    phoneNumberUtil.parse("032 455 66 77", "CH");
```

The first argument is the phone number as a string, and the second parameter is the region we expect the number to be from; this is only used if the number string is not an international number.

## Validating Phone Numbers

Now that we can parse strings to phone numbers, how can we check if the phone number is valid?  

Also, here, PhoneNumberUtil, has you covered it as it has metadata of the phone numbers of many regions?

```java
Phonenumber.PhoneNumber invalidSwissPhoneNumber = 
    phoneNumberUtil.parse("032 631 11 2", "CH");

System.out.println(phoneNumberUtil.isValidNumber(invalidSwissPhoneNumber));
```

The code above will print false as the phone number is too short.

## Conclusion and Recommendation

Google's libphonnumber is very powerful and has even more features, like finding phone numbers in text, getting the number's type, and so on. In my opinion, it's the go-to library for processing phone numbers.

Finally, to answer the question of how a phone number should be stored in the database, I recommend storing the country code and national number separately as numbers. That way, you can directly create a libphonenumber `Phonenumber` from the data and format it when needed.
