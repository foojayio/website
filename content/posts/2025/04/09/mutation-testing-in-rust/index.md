---
title: "Mutation Testing in Rust"
slug: "mutation-testing-in-rust"
date: "2025-04-09T12:39:50+00:00"
lastmod: "2025-04-09T12:39:52+00:00"
description: "I've been a big fan of Mutation Testing since I discovered PIT. As I dive deeper into Rust, I wanted to check the state of mutation testing in Rust."
canonical: "https://blog.frankel.ch/mutation-testing-rust/"
authors:
  - "nicolas-frankel"
image: "cover-2.jpg"
categories:
  - "Testing"
tags:
related_posts:
  - "azul-datadog-datastax-jfrog-payara-and-snyk-form-inaugural-foojay-advisory-board"
  - "azul-enhances-readynow-to-solve-javas-warmup-problem-simplify-operations-and-optimize-cloud-costs"
  - "become-a-better-java-developer-19-tips-for-staying-ahead-in-2024"
  - "beginning-javafx-with-intellij"
enlighterjs: true
frozen: false
---

I've been a big fan of [Mutation Testing](https://en.wikipedia.org/wiki/Mutation_testing) since I discovered [PIT](https://pitest.org/). As I dive deeper into Rust, I wanted to check the state of mutation testing in Rust.

Starting with `cargo-mutants` {#h2-0-starting-with-cargo-mutants}
-----------------------------------------------------------------

I found two crates for mutation testing in Rust:

* [cargo-mutants](https://mutants.rs/)
* and [mutagen](https://github.com/llogiq/mutagen)

`mutagen` hasn't been maintained for three years, while [cargo-mutants](https://github.com/sourcefrog/cargo-mutants) is still under active development.

I've ported the sample code from my previous Java code to Rust:

<pre class="EnlighterJSRAW" data-enlighter-language="rust">struct LowPassPredicate {
    threshold: i32,
}

impl LowPassPredicate {
    pub fn new(threshold: i32) -&gt; Self {
        LowPassPredicate { threshold }
    }

    pub fn test(&amp;self, value: i32) -&gt; bool {
        value &lt; self.threshold
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn should_return_true_when_under_limit() {
        let low_pass_predicate = LowPassPredicate::new(5);
        assert_eq!(low_pass_predicate.test(4), true);
    }

    #[test]
    fn should_return_false_when_above_limit() {
        let low_pass_predicate = LowPassPredicate::new(5);
        assert_eq!(low_pass_predicate.test(6), false);
    }
}</pre>

Using `cargo-mutants` is a two-step process:

1. Install it, `cargo install --locked cargo-mutants`
2. Use it, `cargo mutants`

```
Found 4 mutants to test
ok       Unmutated baseline in 0.1s build + 0.3s test
 INFO Auto-set test timeout to 20s
4 mutants tested in 1s: 4 caught
```

I expected a mutant to survive, as I didn't test the boundary when the test value equals the limit. Strangely enough, `cargo-mutants` didn't detect it.

Finding and fixing the issue {#h2-1-finding-and-fixing-the-issue}
-----------------------------------------------------------------

I investigated the source code and found the place where it mutates operators:

<pre class="EnlighterJSRAW" data-enlighter-language="rust">// We try replacing logical ops with == and !=, which are effectively
// XNOR and XOR when applied to booleans. However, they're often unviable
// because they require parenthesis for disambiguation in many expressions.
BinOp::Eq(_) =&gt; vec![quote! { != }],
BinOp::Ne(_) =&gt; vec![quote! { == }],
BinOp::And(_) =&gt; vec![quote! { || }],
BinOp::Or(_) =&gt; vec![quote! { &amp;&amp; }],
BinOp::Lt(_) =&gt; vec![quote! { == }, quote! {&gt;}],
BinOp::Gt(_) =&gt; vec![quote! { == }, quote! {&lt;}],
BinOp::Le(_) =&gt; vec![quote! {&gt;}],
BinOp::Ge(_) =&gt; vec![quote! {&lt;}],
BinOp::Add(_) =&gt; vec![quote! {-}, quote! {*}],</pre>

Indeed, `, but not to ``<=`. I forked the repo and updated the code accordingly:

<pre class="EnlighterJSRAW" data-enlighter-language="rust">BinOp::Lt(_) =&gt; vec![quote! { == }, quote! {&gt;}, quote!{ &lt;= }],
BinOp::Gt(_) =&gt; vec![quote! { == }, quote! {&lt;}, quote!{ =&gt; }],</pre>

I installed the new forked version:

<pre class="EnlighterJSRAW" data-enlighter-language="bash">cargo install --git https://github.com/nfrankel/cargo-mutants.git --locked</pre>

I reran the command:

<pre class="EnlighterJSRAW" data-enlighter-language="bash">cargo mutants</pre>

The output is the following:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Found 5 mutants to test
ok       Unmutated baseline in 0.1s build + 0.3s test
 INFO Auto-set test timeout to 20s
MISSED   src/lib.rs:11:15: replace &lt; with &lt;= in LowPassPredicate::test in 0.2s build + 0.2s test
5 mutants tested in 2s: 1 missed, 4 caught</pre>

You can find the same information in the `missed.txt` file. I thought I fixed it and was ready to make a Pull Request to the `cargo-mutants` repo. I just needed to add the test at the boundary:

<pre class="EnlighterJSRAW" data-enlighter-language="rust">#[test]
fn should_return_false_when_equals_limit() {
    let low_pass_predicate = LowPassPredicate::new(5);
    assert_eq!(low_pass_predicate.test(5), false);
}</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="bash">cargo test</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="generic">running 3 tests
test tests::should_return_false_when_above_limit ... ok
test tests::should_return_false_when_equals_limit ... ok
test tests::should_return_true_when_under_limit ... ok

test result: ok. 3 passed; 0 failed; 0 ignored; 0 measured; 0 filtered out; finished in 0.00s</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="bash">cargo mutants</pre>

And all mutants are killed!

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Found 5 mutants to test
ok       Unmutated baseline in 0.1s build + 0.2s test
 INFO Auto-set test timeout to 20s
5 mutants tested in 2s: 5 caught</pre>

Conclusion {#h2-2-conclusion}
-----------------------------

Not many blog posts end with a Pull Request, but this one [does](https://github.com/sourcefrog/cargo-mutants/pull/501). Unfortunately, I couldn't manage to make the tests pass; fortunately, the repository maintainer helped me--a lot. The Pull Request is merged: enjoy this slight improvement.

I learned more about `cargo-mutants` and could improve the code in the process.

**To go further:**

* [Mutation testing](https://en.wikipedia.org/wiki/Mutation_testing)
* [Welcome to cargo-mutants](https://mutants.rs)
* [GitHub cargo-mutants](https://github.com/sourcefrog/cargo-mutants)

*** ** * ** ***

*Originally published at [A Java Geek](https://blog.frankel.ch/mutation-testing-rust/) on March 30^th^, 2025*
