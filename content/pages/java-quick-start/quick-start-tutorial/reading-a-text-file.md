---
title: "Reading a Text File"
description: "Read a CSV file from disk in Java, combining files, loops and objects into one small working program."
url: "/java-quick-start/quick-start-tutorial/reading-a-text-file/"
frozen: false
duration: "5:39"
weight: 9
---

{{< youtube cUmrGaGkTkU >}}

Let's read some data from an external source to combine all we learned before... With a [free online tool](http://www.convertcsv.com/generate-test-data.htm), we create a test CSV file with random data. This file is stored in a subdirectory `resources` as `testdata.csv`.

The file in the source code contains comma-separated values for counter, firstname, lastname, age, Street, City, State, ZIP:

```
1,Ada,Gomez,40,Mabvob Pike,Radafso,LA,60500
2,Bernard,Jordan,28,Dotcu Court,Cewbufbim,MS,17422
3,Mittie,Vaughn,64,Nandac Mill,Patunif,RI,81182
4,Miguel,Clarke,39,Liac Boulevard,Deguci,NH,32207
...
```

We can read this CSV-file line by line and convert each line to an object which is added to a list of persons:

{{< jdoodle files="testdata.csv" >}}
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class ReadTextFile {
    public static void main (String[] args) {
        List<Person> persons = loadPersons();
        System.out.println("Number of persons loaded from CSV file: " + persons.size());
        for (Person person : persons) {
            System.out.println(person.getFullName() + ", age: " + person.getAge());
        }
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

Just like in the UsingObjects-example, we use an object to store the data of each line in the CSV file, in this case, the object `Person`.

Within the loadPersons-method:

* the file is opened
* read line by line
* the text of each line is used to create a Person-object
* this object is added to the list
* the list is returned.

Within the main-method, the resulting list is used:

* to count the number of persons on the list
* to print each person's full name and age

```
$ java ReadTextFile.java
Number of persons loaded from CSV file: 100
Ada Gomez, age: 40
Bernard Jordan, age: 28
Mittie Vaughn, age: 64
Miguel Clarke, age: 39
...
```

Also in this example a `Record` can be used for the `Person` object:

```
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReadTextFile {
    public static void main(String[] args) {
        List<Person> persons = loadPersons();
        System.out.println("Number of persons loaded from CSV file: "
                + persons.size());
        for (Person person : persons) {
            System.out.println(person.fullName() + ", age: " + person.age());
        }
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
