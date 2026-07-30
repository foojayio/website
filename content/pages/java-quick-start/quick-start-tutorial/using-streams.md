---
title: "Getting Started with Java - Using Streams"
description: "foojay is the place for all OpenJDK Update Release Information. Learn More."
canonical: "https://foojay.io/java-quick-start/quick-start-tutorial/using-streams/"
url: "/java-quick-start/quick-start-tutorial/using-streams/"
jdoodle: true
enlighterjs: true
aliases:
  - "/java-quick-start/quick-start-tutorial/using-streams/"
frozen: false
---

*** ** * ** ***

[\<\< Reading a Text File](https://foojay.io/java-quick-start/quick-start-tutorial/reading-a-text-file/)  
[What's Next? \>\>](https://foojay.io/java-quick-start/quick-start-tutorial/whats-next)



{{< youtube v07_Z3ILyVU >}}



Streams were introduced as a new feature in Java 8. These allow you to perform a series of steps or actions on an object. Let's extend the ReadTextFile example with some extra functions...

First, we need to add extra imports:



<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import java.util.IntSummaryStatistics;
import java.util.stream.Collectors;</pre>



And we change the main method to this:



<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public static void main (String[] args) {
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
}</pre>



Of course, we need to add the methods "countFirstName" and "getAgeStats" which are used in this extra code in the "main"-method:



<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public static int countFirstName(List&lt;Person&gt; persons, String firstName) {
    return (int) persons.stream()
        .filter(p -&gt; p.getFirstName().equals(firstName))
        .count();
}
public static IntSummaryStatistics getAgeStats(List&lt;Person&gt; persons) {
    return persons.stream()
        .mapToInt(Person::getAge)
        .summaryStatistics();
}</pre>



As you see, we start by converting the list into a stream, by adding ".stream()" behind the "persons"-list. We can further handle that stream step-by-step to obtain certain results.

* countFirstName
  * filter is used to get all the persons with the given first name
  * collect is used to make a list of all the matching persons
  * size returns the number of items in the collected list
* getAgeStats
  * mapToInt is used to only use the ages from all the persons
  * summaryStatistics returns an IntSummaryStatistics-object from the list from which we can get different values



<div data-pym-src="https://www.jdoodle.com/plugin" data-version-index="6" data-language="java" data-client-id="34d6e81ae45d88cdb9fb98fed1415b81" data-has-files="true">
 <div data-type="file" data-file-name="testdata.csv">
  1,Ada,Gomez,40,Mabvob Pike,Radafso,LA,60500 2,Bernard,Jordan,28,Dotcu Court,Cewbufbim,MS,17422 3,Mittie,Vaughn,64,Nandac Mill,Patunif,RI,81182 4,Miguel,Clarke,39,Liac Boulevard,Deguci,NH,32207 5,Justin,Luna,37,Ucago Street,Loderuni,LA,21387 6,Adrian,Caldwell,35,Helfuz Manor,Pibvacgef,AZ,77766 7,Dale,Kelley,59,Viwo Square,Fepmoreb,AZ,80979 8,Bertha,Hernandez,34,Domo Path,Osipikuz,WV,50477 9,Roy,Perry,47,Ezokev Trail,Seruipe,MA,89600 10,Leona,Barber,41,Vaboz Key,Kovule,NE,45175 11,Ina,Washington,37,Fobug Terrace,Narhespi,MI,78272 12,Mitchell,Bowen,27,Jandob Plaza,Wuzgagsav,MT,62287 13,Leo,Romero,59,Leruk Mill,Debulot,VT,83857 14,Ricky,Welch,19,Uvujud Grove,Capbowji,WY,45847 15,Edna,Pierce,28,Biju Circle,Soucetum,KY,31995 16,Caroline,Chambers,62,Lordi Center,Goegdu,MI,59747 17,Cory,Howard,25,Gahu Way,Tewtilef,MN,03570 18,Frances,Vega,63,Ricvut Trail,Rogosiz,CO,13000 19,Charlotte,McCarthy,29,Egeta River,Uzejoriz,HI,71991 20,Janie,Mathis,43,Buhe Junction,Gedivu,MT,30475 21,Antonio,Shaw,64,Ihgih Place,Wopunum,OK,08525 22,Matilda,Henry,18,Fiwahu Road,Etudafuc,CT,48030 23,Lewis,Lyons,54,Bici Grove,Udzenva,RI,82279 24,Ida,Frazier,60,Vomo Road,Peddozuk,CO,43840 25,Lenora,Bowers,38,Zesido Pass,Ekivilis,AL,64218 26,Steven,Butler,51,Ipuf Terrace,Tonapu,NM,71572 27,Eliza,Wright,22,Haje Drive,Kublehef,LA,37560 28,Fanny,Robbins,31,Jiesa Grove,Bemdabju,VT,31330 29,Carrie,Wade,42,Fafi View,Buezren,MO,13874 30,Lottie,Fernandez,52,Gafe Point,Cakoetu,UT,62399 31,Lou,Elliott,47,Olunu Pike,Gujtoje,OR,01854 32,Irene,Williamson,56,Kesi Place,Upkohud,NC,16579 33,Jeremy,Roberson,44,Jaade Place,Luvsojo,AZ,79333 34,Violet,Mann,35,Berowa Circle,Rotehdi,TX,81721 35,Isaiah,Fowler,48,Deij Pike,Gajrikun,MD,34612 36,Johnny,Stone,23,Hiho View,Upzavsug,KY,54162 37,Hulda,Wong,60,Meok Grove,Eduzeulu,OR,58099 38,Jeffery,Mason,23,Zudho Circle,Udougiji,SC,47700 39,Gregory,Freeman,55,Nanci Trail,Ohhabza,SC,40015 40,Inez,Byrd,41,Jada Place,Livuulu,WV,65150 41,Charlotte,Weber,49,Domu Manor,Kanazijod,DC,86033 42,Johanna,Moreno,47,Zejku Grove,Gurfokub,CT,98543 43,Carlos,Colon,51,Agcah Trail,Jevasvan,IN,34068 44,Maurice,Norton,42,Gokta Way,Vidhonlo,OR,44786 45,Antonio,Bennett,37,Sewwiv Drive,Fuggifbo,TX,60290 46,Bill,Mendez,32,Havtad Trail,Ehoheig,AK,81866 47,Jorge,Alvarez,59,Hacgof Trail,Vufozbaj,IA,17941 48,Evan,Rodgers,50,Raphus Key,Hogbeteh,OR,74817 49,Johnny,Cunningham,34,Epejoc Avenue,Ugavamje,SD,07482 50,Delia,Fuller,27,Zogo Heights,Siosso,OK,45460 51,Luke,Guerrero,64,Foci Center,Sitwiscel,VT,18079 52,Jesse,Stevenson,22,Weip Terrace,Sizjoco,NV,94954 53,Joe,Conner,25,Vonod Pike,Jifonev,IA,53502 54,Nathan,Parker,20,Sedona Pass,Vazkowi,OK,60390 55,Charlotte,McGee,47,Sawha Circle,Ugegozvub,MD,47279 56,Matthew,Wade,65,Kussiz Way,Evkuham,KS,66566 57,Ola,Wade,48,Viwuc Junction,Tohigwo,MS,19691 58,Landon,Arnold,53,Pofo Circle,Nuonevid,OH,94843 59,Ronald,Rodriquez,55,Janu Turnpike,Soupce,TX,98285 60,Mitchell,Sanders,57,Cobzu Point,Ottumoj,NH,52145 61,Rosalie,McCoy,23,Vuihu Drive,Reppaltog,CT,38428 62,Harry,Harmon,64,Acufob Avenue,Cotjojnoh,WV,36065 63,Clifford,Waters,55,Aguak Grove,Bevjilel,HI,52017 64,Clarence,George,43,Fuzzes Pass,Ahmedmen,ID,66324 65,Dorothy,French,29,Hulav View,Kekomva,MA,45860 66,Jon,Ferguson,18,Kalcup Trail,Hudzawdap,IL,22215 67,Mario,Myers,42,Owohem Terrace,Dusmobdi,IA,23546 68,Allie,Andrews,49,Nicnat Park,Anuvalu,ID,72182 69,Dollie,Page,29,Cinit Lane,Ebniri,MA,22583 70,Carolyn,Marshall,30,Jozdav Loop,Sudkiisu,MT,10718 71,Ray,Fowler,51,Gadwe Square,Beneriz,DC,59681 72,Janie,Byrd,65,Bukaba Extension,Dajodgiv,WA,56046 73,Harold,Burns,21,Uslof Circle,Heuzju,WI,55665 74,Rhoda,Wade,38,Udri River,Wupegloj,TN,10147 75,Wayne,Sullivan,31,Tekfus Street,Siczobic,WI,52959 76,Gerald,Cook,23,Nujagu Street,Zozapwe,VT,93383 77,Edith,Crawford,35,Jujte Key,Sucditsu,MO,32067 78,Thomas,Reid,26,Sawcen Street,Narizfu,MD,44883 79,Vernon,Garrett,55,Ralro Square,Vatinmi,OH,02308 80,Charles,McBride,62,Bijiv Ridge,Kidanet,NE,66994 81,Callie,Ball,57,Ruvaja Avenue,Zohgacken,FL,15755 82,Minnie,Gibbs,62,Setu Square,Gadutna,GA,52897 83,Rena,Henry,43,Matban Pass,Ciijmoc,NJ,94606 84,Ethan,Park,30,Zowi Glen,Dufkofnof,NY,84610 85,Matthew,Harmon,43,Labot View,Amtojepi,NM,71077 86,Duane,Salazar,19,Bivoz Extension,Volovik,WA,86567 87,Hannah,Olson,47,Kizvo Pike,Paksonle,NC,80126 88,Henrietta,Sparks,37,Konaw Avenue,Hullikgu,AK,71124 89,Emma,Schultz,53,Tidnod Pike,Ununehas,IN,01641 90,Ophelia,Fleming,46,Umigeb Pike,Huzhewsez,DC,94926 91,Emilie,Henry,63,Zepet Manor,Sitgordez,RI,26612 92,Rachel,Conner,57,Muhot View,Obkivutu,OK,54753 93,Nelle,Caldwell,54,Vunvu Key,Zedteaco,WI,49044 94,Jose,Brooks,60,Givaz View,Conemido,TX,99776 95,Eula,Knight,60,Gafvel Terrace,Bucopop,AL,66582 96,Etta,McKenzie,43,Ucji Boulevard,Muizwir,VA,43886 97,David,Gutierrez,47,Pahmus Loop,Cuufca,MT,10341 98,Charlotte,Scott,26,Nanli Extension,Mucihuv,TN,24687 99,Alexander,McCoy,46,Huvahi Drive,Oljenbek,NH,19102 100,Callie,Fitzgerald,32,Maduv Circle,Socoeji,NJ,14602
 </div>
 <div data-type="script">
  <xmp>
   import java.io.File; import java.io.FileNotFoundException; import java.util.ArrayList; import java.util.List; import java.util.Scanner; import java.util.IntSummaryStatistics; import java.util.stream.Collectors; public class ReadTextFile { public static void main (String[] args) { List&lt;Person&gt; persons = loadPersons(); System.out.println("Number of persons loaded from CSV file: " + persons.size()); System.out.println("------------------------------------------------"); System.out.println("Number of persons with first name"); System.out.println(" * 'Matthew': " + countFirstName(persons, "Matthew")); System.out.println(" * 'Charlotte': " + countFirstName(persons, "Charlotte")); System.out.println("------------------------------------------------"); IntSummaryStatistics stats = getAgeStats(persons); System.out.println("Minimum age: " + stats.getMin()); System.out.println("Maximum age: " + stats.getMax()); System.out.println("Average age: " + stats.getAverage()); } public static int countFirstName(List&lt;Person&gt; persons, String firstName) { return (int) persons.stream() .filter(p -&gt; p.getFirstName().equals(firstName)) .count(); } public static IntSummaryStatistics getAgeStats(List&lt;Person&gt; persons) { return persons.stream() .mapToInt(Person::getAge) .summaryStatistics(); } public static List&lt;Person&gt; loadPersons() { List list = new ArrayList&lt;&gt;(); File file = new File("/uploads/testdata.csv"); try (Scanner scanner = new Scanner(file)) { while (scanner.hasNextLine()) { list.add(new Person(scanner.nextLine())); } } catch (FileNotFoundException ex) { System.err.println("Could not find the file to be loaded"); } return list; } public static class Person { private int id; private String firstName; private String lastName; private int age; private String street; private String city; private String state; private int zip; public Person(String csvLine) { String[] data = csvLine.split(","); this.id = Integer.valueOf(data[0]); this.firstName = data[1]; this.lastName = data[2]; this.age = Integer.valueOf(data[3]); this.street = data[4]; this.city = data[5]; this.state = data[6]; this.zip = Integer.valueOf(data[7]); } public String getFirstName() { return firstName; } public String getFullName() { return firstName + " " + lastName; } public int getAge() { return age; } } }
  </xmp>
 </div>
</div>



*** ** * ** ***

When we run this same application again now, we get this output:



<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="dracula" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ java ReadTextFile.java
Number of persons loaded from CSV file: 100
------------------------------------------------
Number of persons with first name
    * 'Matthew':     2
    * 'Charlotte':   4
------------------------------------------------
Minimum age: 18
Maximum age: 65
Average age: 42.78</pre>



*** ** * ** ***

Java 25 version:



<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import java.util.IntSummaryStatistics;

public class ReadTextFile {
    public static void main(String[] args) {
        List&lt;Person&gt; persons = loadPersons();
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

    public static int countFirstName(List&lt;Person&gt; persons, String firstName) {
        return (int) persons.stream()
                .filter(p -&gt; p.firstName().equals(firstName))
                .count();
    }

    public static IntSummaryStatistics getAgeStats(List&lt;Person&gt; persons) {
        return persons.stream()
                .mapToInt(Person::age)
                .summaryStatistics();
    }

    public static List&lt;Person&gt; loadPersons() {
        List&lt;Person&gt; list = new ArrayList&lt;&gt;();
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
}</pre>

  
[\<\< Reading a Text File](https://foojay.io/java-quick-start/quick-start-tutorial/reading-a-text-file/)  
[What's Next? \>\>](https://foojay.io/java-quick-start/quick-start-tutorial/whats-next)
