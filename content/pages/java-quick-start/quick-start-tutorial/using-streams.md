---
title: "Using Streams"
description: "Streams, added in Java 8, let you chain steps over a collection. Extend the text-file example with filtering and mapping."
url: "/java-quick-start/quick-start-tutorial/using-streams/"
frozen: false
duration: "2:53"
weight: 10
---

{{< youtube v07_Z3ILyVU >}}

Streams were introduced as a new feature in Java 8. These allow you to perform a series of steps or actions on an object. Let's extend the ReadTextFile example with some extra functions...

First, we need to add extra imports:

```java
import java.util.IntSummaryStatistics;
import java.util.stream.Collectors;
```

And we change the main method to this:

```java
public static void main (String[] args) {
    List persons = loadPersons();
    System.out.println("Number of persons loaded from CSV file: " + persons.size());

    System.out.println("------------------------------------------------");
    System.out.println("Number of persons with first name");
    System.out.println("    * 'Matthew':     " 
        + countFirstName(persons, "Matthew"));
    System.out.println("    * 'Charlotte':   " 
        + countFirstName(persons, "Charlotte"));

    System.out.println("------------------------------------------------");
    IntSummaryStatistics stats = getAgeStats(persons);
    System.out.println("Minimum age: " + stats.getMin());
    System.out.println("Maximum age: " + stats.getMax());
    System.out.println("Average age: " + stats.getAverage());
}
```

Of course, we need to add the methods "countFirstName" and "getAgeStats" which are used in this extra code in the "main"-method:

```java
public static int countFirstName(List<Person> persons, String firstName) {
    return (int) persons.stream()
        .filter(p -> p.getFirstName().equals(firstName))
        .count();
}
public static IntSummaryStatistics getAgeStats(List<Person> persons) {
    return persons.stream()
        .mapToInt(Person::getAge)
        .summaryStatistics();
}
```

As you see, we start by converting the list into a stream, by adding ".stream()" behind the "persons"-list. We can further handle that stream step-by-step to obtain certain results.

* countFirstName
  * filter is used to get all the persons with the given first name
  * collect is used to make a list of all the matching persons
  * size returns the number of items in the collected list
* getAgeStats
  * mapToInt is used to only use the ages from all the persons
  * summaryStatistics returns an IntSummaryStatistics-object from the list from which we can get different values

{{< jdoodle files="testdata.csv" >}}
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.IntSummaryStatistics;
import java.util.stream.Collectors;
public class ReadTextFile {
    public static void main (String[] args) {
        List<Person> persons = loadPersons();
        System.out.println("Number of persons loaded from CSV file: " + persons.size());
        System.out.println("------------------------------------------------");
        System.out.println("Number of persons with first name");
        System.out.println(" * 'Matthew': " + countFirstName(persons, "Matthew"));
        System.out.println(" * 'Charlotte': " + countFirstName(persons, "Charlotte"));
        System.out.println("------------------------------------------------");
        IntSummaryStatistics stats = getAgeStats(persons);
        System.out.println("Minimum age: " + stats.getMin());
        System.out.println("Maximum age: " + stats.getMax());
        System.out.println("Average age: " + stats.getAverage());
    }
    public static int countFirstName(List<Person> persons, String firstName) {
        return (int) persons.stream()
                .filter(p -> p.getFirstName().equals(firstName))
                .count();
    }
    public static IntSummaryStatistics getAgeStats(List<Person> persons) {
        return persons.stream()
                .mapToInt(Person::getAge)
                .summaryStatistics();
    }
    public static List<Person> loadPersons() {
        List list = new ArrayList<>();
        File file = new File("/uploads/testdata.csv");
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                list.add(new Person(scanner.nextLine()));
            }
        } catch (FileNotFoundException ex) {
            System.err.println("Could not find the file to be loaded");
        }
        return list;
    }
    public static class Person {
        private int id;
        private String firstName;
        private String lastName;
        private int age;
        private String street;
        private String city;
        private String state;
        private int zip;
        public Person(String csvLine) {
            String[] data = csvLine.split(",");
            this.id = Integer.valueOf(data[0]);
            this.firstName = data[1];
            this.lastName = data[2];
            this.age = Integer.valueOf(data[3]);
            this.street = data[4];
            this.city = data[5];
            this.state = data[6];
            this.zip = Integer.valueOf(data[7]);
        }
        public String getFirstName() {
            return firstName;
        }
        public String getFullName() {
            return firstName + " " + lastName;
        }
        public int getAge() {
            return age;
        }
    }
}
{{< /jdoodle >}}

When we run this same application again now, we get this output:

```
$ java ReadTextFile.java
Number of persons loaded from CSV file: 100
------------------------------------------------
Number of persons with first name
    * 'Matthew':     2
    * 'Charlotte':   4
------------------------------------------------
Minimum age: 18
Maximum age: 65
Average age: 42.78
```

Java 25 version:

```
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import java.util.IntSummaryStatistics;

public class ReadTextFile {
    public static void main(String[] args) {
        List<Person> persons = loadPersons();
        System.out.println("Number of persons loaded from CSV file: "
                + persons.size());

        System.out.println("------------------------------------------------");
        System.out.println("Number of persons with first name");
        System.out.println("    * 'Matthew':     "
                + countFirstName(persons, "Matthew"));
        System.out.println("    * 'Charlotte':   "
                + countFirstName(persons, "Charlotte"));

        System.out.println("------------------------------------------------");
        IntSummaryStatistics stats = getAgeStats(persons);
        System.out.println("Minimum age: " + stats.getMin());
        System.out.println("Maximum age: " + stats.getMax());
        System.out.println("Average age: " + stats.getAverage());
    }

    public static int countFirstName(List<Person> persons, String firstName) {
        return (int) persons.stream()
                .filter(p -> p.firstName().equals(firstName))
                .count();
    }

    public static IntSummaryStatistics getAgeStats(List<Person> persons) {
        return persons.stream()
                .mapToInt(Person::age)
                .summaryStatistics();
    }

    public static List<Person> loadPersons() {
        List<Person> list = new ArrayList<>();
        File file = new File("/uploads/testdata.csv");
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                list.add(Person.fromCsv(scanner.nextLine()));
            }
        } catch (FileNotFoundException ex) {
            System.err.println("Could not find the file to be loaded");
        }
        return list;
    }

    public record Person(
            int id,
            String firstName,
            String lastName,
            int age,
            String street,
            String city,
            String state,
            int zip) {

        public static Person fromCsv(String csvLine) {
            String[] data = csvLine.split(",");
            return new Person(
                    Integer.parseInt(data[0]),
                    data[1],
                    data[2],
                    Integer.parseInt(data[3]),
                    data[4],
                    data[5],
                    data[6],
                    Integer.parseInt(data[7]));
        }

        public String fullName() {
            return firstName + " " + lastName;
        }
    }
}
```
