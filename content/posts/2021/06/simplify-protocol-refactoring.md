---
title: "Introduction to Simplify Protocol Refactoring"
slug: "simplify-protocol-refactoring"
date: "2021-06-28T13:22:24+00:00"
lastmod: "2021-09-03T09:23:36+00:00"
description: "Sometimes you need to make code more trivial to see the higher-level patterns that solve the issue at hand more elegantly."
canonical: "https://bmuskalla.github.io/blog/2021-06-28-simplify-protocol/"
authors:
  - "bmuskalla"
image: "/images/posts/2021/06/simplify-protocol-refactoring/Favicon-3-2.png"
categories:
  - "Uncategorized"
tags:
related_posts:
  - "git-archeology"
  - "java-syntax-puzzlers"
  - "3-ways-to-refactor-your-code-in-intellij-idea"
  - "building-ai-systems-with-mongodb-implementing-the-planning-pattern"
enlighterjs: true
frozen: false
---

The other day, I went grocery shopping. While waiting in line, I thought about some struggles I had in a test I wrote earlier that day. When it was my turn, the cashier scanned my items and said what I owe him. And I just gave him my whole wallet. He stared at me blankly and gave it back. A little confused for a second, I took out my card, paid, and left the store. And at that point, it hit me what was wrong with my test.

Let's have a look at the test and see what it has to do with grocery shopping:

<pre class="EnlighterJSRAW" data-enlighter-language="java">@Test
void givenAccountWithBalanceReporterShouldPrintSummary() {
    Account account = createTestAccount();
    EndOfYearReporter printer = new PlainTextEndOfYearReporter(account);

    String report = printer.produceReport();

    assertThat(report).isEqualTo("Benny: -39 EUR");
}</pre>

So far, so good. Overall, the idea of the test is pretty simple and well written. The problem is the part that I omitted.

How do we set up the account?

<pre class="EnlighterJSRAW" data-enlighter-language="java">private Account createTestAccount(String username, int balance) {
    Account account = mock(Account.class);

    HolderMetadata metadata = new HolderMetadata();
    when(metadata.getFullname()).thenReturn(username);
    when(account.getOwner()).thenReturn(metadata);

    Subaccount subaccount = new Subaccount();
    subaccount.setBalance(balance);
    when(account.getSubaccounts()).thenReturn(List.of(subaccount));

    return account;
}</pre>

Given our domain model got lost in between annotations, DI frameworks, and other funky technologies, we had to start mocking out parts of the model. In this case, we got away with only mocking a few things tightly related to what we do. More often than not, this usually turns into a nightmare of mocking (transitive) dependencies to get the object into the state you want it in. While generally, the advice is to keep your domain model independent of technology (and not mock stuff you don't own), it's often easier said than done. So if we can't easily change our domain model, how do we use this model in our report generator?

<pre class="EnlighterJSRAW" data-enlighter-language="java">String username = account.getOwner().getFullname();
int balance = account.getSubaccounts().stream().mapToInt(Subaccount::getBalance).sum();
String currency = account.getSubaccounts().get(0).getCurrency();
return String.format("%s %d %s", username, balance, currency);</pre>

To produce a simple report, we have to navigate our way through the object graph, collect all the data we need and do some processing (e.g. sum). While this is something we have to do anyway, the question becomes: is it really what the report generation should do? What if we add another report besides our plain text? That would need to replicate the same logic. What about changes to our domain model? We'll have to go and fiddle around with the PDF reporting, which broke due to those changes (usually referred to as [Shotgun Surgery](https://refactoring.guru/smells/shotgun-surgery)).

Let's try something different. Instead of giving the cashier our whole wallet, let's just give them what they need. Not more, not less.

Introducing the "**Simplify Protocol**" refactoring:
> Given a [unit under test](http://xunitpatterns.com/SUT.html), look at all the inputs (constructor arguments, method parameters) and try to replace them with the most trivial type available. Try to deliberately avoid parameter objects and use the most fundamental parameter types possible.

Then, just like you do in test-driven development, let's try the most simple thing that works and refactor later. What we need for the report right now are three things: The account holder, the balance, and the currency. Let's go with this first and see how far to get:

<pre class="EnlighterJSRAW" data-enlighter-language="java">@Test
void givenAccountWithBalanceReporterShouldPrintSummary() {
    // ...
    EndOfYearReporter printer = new PlainTextEndOfYearReporter("Benny", -39, "EUR");

    String report = printer.produceReport();

    assertThat(report).isEqualTo("Benny: -39 EUR");
}</pre>

Hm. That's a lot easier for our test. But that doesn't entirely solve the problem in our production code that needs to call our reporter. And most of you will think:
> "Hey Benny, a stringly-typed API is not great. You should have a strongly-typed API."

And this is what I love about the "Simplify Protocol" refactoring. It's not one of the refactorings you do in isolation. It highlights the shortcomings and helps you to work towards an appropriate pattern. What do I mean by that?

Given our initial setup, we had a single parameter for our reporter. At first sight, none of the apparent patterns (e.g., Adapter, Facade, ..) was applicable - at least not at first sight. But using the "Simplify Protocol" refactoring helped us to see our reporter's dependencies. Quite often, we pass arguments that are way larger than what we need. I've seen too much code where [God classes](https://en.wikipedia.org/wiki/God_object) (usually called something believable like "Context") are the inputs and require a whole mocking ceremony to be instantiated.

Given the exploded set of "trivially-typed" inputs, it's time to consider whether we want to combine them into more helpful abstractions. For example, raw amount and currency can be refactored into an "Amount" [Value Object](https://en.wikipedia.org/wiki/Value_object). Likewise, the 'Amount' and 'Username' form the inputs of our report and can be replaced by a parameter object. If you do, check whether it makes sense to make it specific for our unit under test. Too often, people strive for something reusable and, before they realize it, pass around a Map of objects called "Context".

Alternatively, as we still need to extract the values from our actual domain model, we can use the adapter as "a view" on the existing domain model.

For the inputs to our report, we define a parameter object/view/record/bean that helps us to capture only the necessary data we need for the reporter:

<pre class="EnlighterJSRAW" data-enlighter-language="java">public record EndOfYearReportInput(String username, MoneyAmount amount) {
}</pre>

This makes our test a lot simpler as we can now set up different report data for the various scenarios quickly:

<pre class="EnlighterJSRAW" data-enlighter-language="java">MoneyAmount amount = new MoneyAmount(-39, "EUR");
EndOfYearReportData reportData = new EndOfYearReportData("Benny", amount);
EndOfYearReporter printer = new EndOfYearReporter(reportData);</pre>

For the production code, we still need to adapt the domain model to our new record, either using an Adapter or (as shown here) a Factory method:

<pre class="EnlighterJSRAW" data-enlighter-language="java">public static EndOfYearReportData fromAccount(Account account) {
    String username = account.getOwner().getFullname();
    int balance = account.getSubaccounts().stream().mapToInt(Subaccount::getBalance).sum();
    String currency = account.getSubaccounts().get(0).getCurrency();
    MoneyAmount amount = new MoneyAmount(balance, currency);
    return new EndOfYearReportData(username, amount);
}</pre>

We already have all the patterns at hand to solve these kinds of problems. *Sometimes, you need to make code more trivial to see the higher-level patterns that solve the issue at hand more elegantly.*

And the moral of the story? Keep your wallet to yourself; only hand out what the other side really needs.
