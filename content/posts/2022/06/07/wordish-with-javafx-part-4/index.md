---
title: "Wordish with JavaFX - Part 4 : Foojay"
date: "2022-06-07T09:23:47+00:00"
lastmod: "2022-06-07T09:23:49+00:00"
description: "Continue with Part 4, where we'll look at how we get our words and how we determine if a submitted word is valid!"
authors:
  - "gail-anderson"
image: "playgame5.png"
categories:
  - "Game Development"
  - "Gluon"
  - "Java Core"
  - "JavaFX"
  - "Maven"
  - "Tutorials"
related_posts:
  - "wordish-with-javafx-part-1"
  - "wordish-with-javafx-part-2"
  - "wordish-with-javafx-part-3"
  - "sheetmusic4j-0-0-3-abc-notation-guitar-pro-engraving-improvements"
frozen: false
---

Welcome to Part 4 of this five part series.

In [Part 1](https://foojay.io/today/wordish-with-javafx-part-1/), we introduced the Wordish game with JavaFX and discussed the main UI layout.

In [Part 2](https://foojay.io/today/wordish-with-javafx-part-2/), we discussed look and feel enhancements.

We introduced specialized Label and Button controls that use pseudo-classes for advanced CSS styling.

We covered incorporating third-party font libraries and customizing Scene Builder to leverage these features.

Next, in [Part 3](https://foojay.io/today/wordish-with-javafx-part-3/), we explored the controller code that maintains game state and responds to user input with appropriate updates to the UI.

Now in Part 4, we'll look at how we get our words and how we determine if a submitted word is valid, in a part entitled "What's in a Word, Anyway?"

Before we start, here's an example screenshot of Wordish.

{{< img src="playgame5-619x1024.png" class="size-large is-resized" alt="Wordish Game in progress" width="350" height="579" caption="Figure 1. Wordish Main View Layout" >}}

You can access the code on github here: <https://github.com/gailasgteach/Wordish>.

## Part 4: What's In a Word, Anyway?

Wordish depends on a word list. While the official Wordle game gives every player the same target word once per day, Wordish allows you to play as many times as you want. Wordish generates a word randomly from a list and uses the same list to check the validity of a submitted word. Thus, target words don't come from a carefully curated list. This makes Wordish more difficult, since you can be challenged with obscure words, such as:

* rauli (a large Chilean timber tree),
* sidhe (the fairy folk of Ireland in Gaelic folklore),
* sanga (African breed of cattle), or
* gusle (a musical instrument of the Balkans).

### **The Word List**

Our word list comes from **/usr/share/dict/words**, a linux text file (also available on MacOS) with one word per line that constitutes a dictionary. We use several shell commands to massage this list. We want to include only five-letter words and convert the words to all uppercase.

The following shell command uses the search command `grep` to find all five-letter alpha characters from **/usr/share/dict/words** , converts these results to uppercase characters with `tr`, and sends the output to file **wordlist.txt**.

```bash
$ grep -E '^[a-z]{5}$' /usr/share/dict/words | tr [:lower:] [:upper:] > wordlist.txt
```

The resulting file contains 8,497 words and we add **wordlist.txt** to our maven project under subdirectory **resource**.

### How We Get the Target Word

The singleton class WordData generates words and checks word validity. This class has a private constructor and a public static method `getInstance()`.

In the constructor, we read our dictionary text file using `.lines()` to read each line and collect the resulting lines into a list. The static `getInstance()` method returns a WordData object with the list of words initialized from our text file.

```java
public class WordData {

    private static WordData instance = null;
    private String theWord = "";
    private Set<Integer> usedIndices = new HashSet<>();
    private List<String> words;
    private Random rand = new Random();

    private WordData() {
          try ( InputStream is = WordishApp.class.getResourceAsStream(
                       "/com/asgteach/service/wordlist.txt")) {
            words = new BufferedReader(new InputStreamReader(is))
                    .lines()
                    .collect(Collectors.toList());
          } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
          }
    }

    public static WordData getInstance() {
          if (instance == null) {
            instance = new WordData();
          }
          return instance;
    }
    . . .
}
```

Once the WordData object is created, we set a new target word (the word to be guessed) with method `setNewWord()`, shown below. Instance variable `usedIndices` is a Set. The `do-while` loop terminates when a unique index is added to the Set. (The Set `add()` method rejects elements already in the Set, returning false.) We then use this index to set the next target word from our list of words. This lets users play multiple games of Wordish without getting duplicate words for the target.

```java
    private String theWord = "";

    public String getTheWord() {
        return theWord;
    } 

    private Set<Integer> usedIndices = new HashSet<>();
    int index;

    public void setNewWord() {   
          do {
            index = rand.nextInt(words.size());
          } while (!usedIndices.add(index));

          theWord = words.get(index);
    }
```

We call method `setNewWord()` when we start a new game. And we call method `getTheWord()` any time we need to access our current target word.

### Is Your Word Valid?

One of the baseline rules of Wordish (and Wordle) is that any submitted word must be a valid word, as defined in our word list. For Wordish, this list is the same source list we use to generate target words. A submitted word that is not in this list is rejected. In order to continue the game, the user must submit a valid word. Notably, a user is not penalized by submitting invalid words. However, there is also no feedback on matching, partial-matching, or non-matching status for letters submitted for an invalid word.

Here's boolean method `isAWord()` which returns true if the provided guess word is in our word list.

```java
public boolean isAWord(String guess) {
    return words.stream().anyMatch(w -> w.equals(guess));
}
```

We use `stream()` and `anyMatch()` which returns true if any stream element matches the provided guess word. Since this is a short-circuiting terminal operation, processing ends as soon as a match is found.

**Note** : See **[WordData.java](https://github.com/gailasgteach/Wordish/blob/master/src/main/java/com/asgteach/service/WordData.java)**.

**Next**: In Part 5 (the final installment of this series), we'll discuss customizing JavaFX charts with orientation and colors, adding nodes to the chart scene graph, and implementing a customized Popup control.
