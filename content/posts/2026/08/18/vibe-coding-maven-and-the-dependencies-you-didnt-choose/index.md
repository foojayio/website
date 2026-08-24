---
title: "Your AI Assistant Is Choosing Your Dependencies"
date: "2026-08-18T14:01:20+00:00"
lastmod: "2026-08-18T14:16:48+00:00"
description: "AI code generation doesn't just write your Java. It picks your suppliers, edits your build and pulls in ecosystems you don't know. What Maven developers should check."
authors:
  - "steve-poole"
image: "Gemini_Generated_Image_l6kj3gl6kj3gl6kj-scaled.jpeg"
categories:
  - "Java"
  - "Security"
related_posts:
  - "ai-found-the-bugs-whos-patching-your-eol-java-code"
  - "foojay-podcast-95"
  - "why-java-developers-over-trust-ai-dependency-suggestions"
  - "offline-crypto-address-validation-in-java"
frozen: false
---

When I write about bad actors, I sometimes try to imagine where they are and how they work. For this article, it's clearer than usual because there is some evidence around. You can use your imagination to fill in the gaps.

Imagine there's a job going in Pyongyang. Long hours, of course, with probably brutal OKRs. The job is creating malicious packages, measured by how many you can publish a week, how many credentials you can steal, etc. The successful candidate will never meet a customer face to face, but they will meet you occasionally via a dependency you add, or one your coding assistant adds for you.

This year the successful candidate probably had new targets. They certainly have new tools. In fact, the new tools are the same ones you're using. Theirs just have the safety features turned off, and they are applied at industrial speed and scale.

The AI assistant that you use to build a cool new app — a backend in Java, a frontend in Node, maybe an LLM connected via some Python — is not itself the target. The target is still you. What's changed is the way these tools alter how you build software and, in particular, how they choose and introduce dependencies.

Let's explore.

## So Vibe Coders?

Big money, lots of players, automation and dark times ahead. What's that got to do with Vibe Coding?

Strangely, it's not mainly to do with the code your tools *generate* . It's about the dependencies your assistant *selects*.

Ask an assistant to add JSON handling, JWT validation, an HTTP client, vector search or some clever PDF parsing and it will probably do more than write Java. It may change your `pom.xml`, add a Gradle plugin, select a version, import a BOM, or pull in a starter that brings another dozen dependencies with it.

In other words, your AI partner is choosing your suppliers, and that matters.

## Java isn't npm. Good. Don't relax.

Java developers have some theoretical advantages here.

Maven Central is the default repository for Maven, and publishing under a new Central namespace requires the publisher to demonstrate control of the corresponding domain. That puts a useful barrier in the way of somebody simply turning up and publishing arbitrary artifacts under somebody else's namespace.

And downloading an ordinary JAR is not like running an npm package with an install script. A Maven dependency doesn't normally get arbitrary code execution merely because Maven resolved it.

All good, but this can create a comforting mental model that says: *Maven dependency, Maven Central, therefore safe.*

That model is too simple.

Maven automatically resolves transitive dependencies. Add A and you can get B, C and D without ever typing their coordinates yourself. That's a core feature of Maven, and normally a very useful one, but it also means the dependency graph you run is larger than the handful of entries you consciously added to your POM.

Then there are plugins.

Java developers tend to think about supply-chain risk in terms of libraries, but an AI assistant can also modify the build itself. A Maven plugin is not just another application dependency: it can execute code as part of the build lifecycle, during phases such as `generate-sources`, `compile`, `test` or `package`.

In this context that makes Maven plugins potentially dangerous. They are fundamental to how Maven works and if your coding assistant adds a new plugin, repository, parent POM or other build configuration, that change really deserves at least as much scrutiny as a new library dependency.

There are also active profiles, plugin dependencies, repository declarations and whatever is hiding in your local or CI `settings.xml`.

The `pom.xml` you are looking at is not necessarily the POM Maven actually builds. Maven's `effective-pom` folds in inheritance and active profiles so you can see the resulting configuration.

The point is that AI doesn't just write your Java. It can modify the machinery that builds it.

## What Price Code AI Gen?

What Vibe coding ( in fact, any AI code gen) gives you is fast development.

I've done my fair share of building large applications (and simple demos) with various CoPilots, LLMs, etc.

Get it right, and you have fantastic productivity. Get it wrong, and it's either procrastination-in-code — you keep tweaking the prompt but nothing ever seems to get you closer to the end goal — or, worst case, a messy, useless set of unreadable, unmaintainable code.

The security cost of this productivity is that we're not reviewing the generated changes anywhere near enough, and we're certainly not reviewing every dependency chosen. If we're out of our ecosystem comfort zone, then doubly so. For hosted vibe-coded apps, there may not even be a point where the human-in-the-loop gets to choose dependencies.

## Crossing ecosystems can be bad for your (app) health

If you *asked* for Java your backend might remain comfortingly Java and Spring Boot but the frontend is probably React. Add an AI integration request and that can introduce Python, and your CI configuration downloads tooling from somewhere else entirely. If you didnt ask for Java then guess what.

Regardless, Yo've gone from being a Java developer to being the consumer of several software ecosystems. The chances are you have some knowledge Node or Python but how much do you know the intricasies of the dependency or deploy procesess?

AI gives you more code, faster. It also recommends dependencies, and research has found substantial rates of non-existent, deprecated, unsafe or hallucinated dependency recommendations. That combination matters more than either problem on its own.

What happens when the Java developer does review the Maven dependencies but doesn't understand the npm install process in the frontend? What happens when the vibe coder isn't a developer at all? And what happens when the developer understands all of this but simply doesn't review what the assistant added?

There's a lot more to go wrong with AI generated code than performance , data security etc and the quantity of code being generated is making us cut corners on code review.

## The weakest link?

The net is that your project's security posture is now the posture of the weakest ecosystem and registry that *you don't know,* and you didn't necessarily pick which ones are in play anyway. Your AI partner did, and it shops at machine speed.

## Defences for Java Developers

There are some very simple questions worth asking before accepting an AI-generated build change. What did it actually add? What did Maven resolve? What build tooling did it introduce?

For Maven, start with:

```
mvn dependency:tree
mvn help:effective-pom
mvn help:effective-settings
mvn dependency:resolve-plugins
```

mvn dependency:tree shows your project's normal dependency tree, including transitive dependencies. It does not primarily show Maven build plugins.

mvn help:effective-pom shows the fully resolved POM after inheritance and active profiles are applied. That lets you see plugins, repositories, dependency management, parents, etc. that may not be obvious from the local pom.xml, but it's configuration rather than a plugin dependency tree.

mvn help:effective-settings shows the settings Maven is actually using after global/user settings are merged — useful for mirrors, repositories, proxies and profiles. It does not list build-tool dependencies.

mvn dependency:resolve-plugins is the one that explicitly resolves all project plugins and reports and their dependencies.

For **Gradle** users, the same principle applies. **Gradle** can display the dependency graph and explain why a particular dependency or version was selected. It also supports dependency locking and dependency verification.

Look particularly hard at changes that introduces Maven plugins, new parent, BOM, `SNAPSHOT` or dynamic version.

Review all the dependencies for vulnerabilities and keep a look at any config files that get created or modified.

None of that is exciting, which is rather the point - since now you have to do the same for Node , Python code as well..

## Your repository configuration is part of the attack surface too

Many organisations proxy Maven Central through Nexus, Artifactory or another repository manager. Maven can be configured with a mirror in `settings.xml`, which means repository policy is part of your defence. Something

If the AI adds a or block to your POM, that deserves more scrutiny than anything else.

Likewise, check what happened to the Maven Wrapper. If we're going to talk supply chains, the thing downloading the supply chain probably deserves some attention too.

## What if you can't just upgrade?

There's another awkward dependency problem that AI can introduce.

Sometimes the package isn't malicious. It isn't hallucinated. It isn't even particularly badly chosen. It's just old.

You apply due dilligence to the dependencies in your shiny new AI app and a scanner finds a CVE in a dependency, somebody says "just upgrade it", and suddenly you discover that your app is somewhat fixated on the older version.

Nowdays old is not stable. Old can mean lots of CVEs not discovered yet and unfortantely that changing as we speak.

Thats means as well as checking for CVEs you need to start checking for software thats end-of-life, out of support etc (I wanted to write 'ex-software' but only Monty Pythoners will get it)

### EOL discovery

As I may have mentioned, HeroDevs has a free [EOL dependency scanner](https://www.herodevs.com/eol-dataset?utm_source=devrel "EOL dependency scanner") that can identify unsupported open-source components in a codebase.

Given everything we've just said about AI choosing dependencies for you, running that against the applications you're building might produce some interesting results.

## Vibe coding take two

AI code generation tools have many strengths and weaknesses. The point is not to stop getting value from AI , its to understand the related weaknesses and deal with them.

You may not want to review all the generated code but you must take time to review the dependency management , build config, install process etc thats being created. You must start to take control of what the AI generates for you by being explicit up front about what it can or cannot use.

You can even give your coding assistant an MCP server backed by OSV and ask it to check the dependency tree for known vulnerabilities. That's useful, but it creates another trust decision: which MCP server did you just give access to your project?

## The factory bought the same tools

Which brings us back to those safety features being turned off.

Expel published research on a North Korean crew it tracks as *HexagonalRodent*. They used mainstream AI tools operationally to create infrastructure, generate malware and build fake companies used to lure developers.

More interestingly for this argument, Expel found evidence that the attackers prompted US-owned AI models to audit their malicious coding assessments for signs of malware. Targets had started using AI to inspect the code and find the backdoors, so the attacker used AI to check that their malware could survive your AI-assisted review.

Read that again. **The attacker is using AI to check that their malware evades your AI.**

This is the part of the AI security conversation that matters to me. Not magic autonomous super-hackers, but productivity. You can produce more software because of AI. So can they.

## And the old attacks didn't go away

The really awkward bit is that the bad guys don't need that sophistication in the first place.

Software supply-chain attacks continue to target the machinery developers already trust: source repositories, packages, editor configuration and development tooling.

Recent campaigns have used malicious editor tasks, compromised packages and manipulated repository history to make malicious projects look established. A Git history can be fake. A package name can look convincing. A dependency can be technically legitimate and still be a terrible choice. And an AI assistant can make all of those decisions faster than you used to.

Attitudes to software security by anyone other than security professionals have always been *complicated* : an abstract understanding of the *necessity*, but limited focus on the actuality.

Vibe coding widens that gap and brings more people into software creation without necessarily bringing more security awareness with them. Non-developers can perhaps be forgiven for assuming the tools are taking care of that. Professionals involved in software creation can't make the same assumption.

The AI driven world of software supply chain attacks is really one that sits more and more on the shoulders of developers to counter.

## The candidate is doing fine, by the way

Two groups have the same AI capability this year. One of them is you, building faster than you ever have. The other is the successful candidate from the start, somewhere in Pyongyang, hitting their targets.

**You both got more productive.**

Their work only has to become a dependency of yours or any one of the applications your non-developer colleagues just vibe-coded.

### What to do next - get clued up

There's enough evidence and helpful suggestions out there. Time to do some reading

<figure class="wp-block-embed is-type-wp-embed is-provider-cycode wp-block-embed-cycode">
 <div class="wp-block-embed__wrapper">
  <blockquote class="wp-embedded-content" data-secret="zf2ypUbhM9">
   <a target="_blank" href="https://cycode.com/blog/vibe-coding-security/">Vibe Coding Security: Risks and Vulnerabilities</a>
  </blockquote><iframe class="wp-embedded-content" sandbox="allow-scripts" security="restricted" style="position: absolute; visibility: hidden;" title="“Vibe Coding Security: Risks and Vulnerabilities” — Cycode" src="https://cycode.com/blog/vibe-coding-security/embed/#?secret=8ya7m5i9By#?secret=zf2ypUbhM9" data-secret="zf2ypUbhM9" width="500" height="282" frameborder="0" marginwidth="0" marginheight="0" scrolling="no"></iframe>
 </div>
</figure>

<https://snyk.io/articles/package-hallucinations>

<https://unit42.paloaltonetworks.com/phantom-squatting-hallucinated-web-domains>

{{< youtube NnGGYV0DwFI >}}
