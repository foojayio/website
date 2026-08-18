---
title: "Namespace Shadowing (a.k.a. “Dependency Confusion”) Attack"
date: "2021-04-22T06:31:08+00:00"
lastmod: "2021-04-22T06:31:55+00:00"
description: "The npm Registry is vulnerable to supply chain namespace shadowing, also known as “Dependency Confusion” attacks!"
authors:
  - "jbaruch"
image: "https://media.jfrog.com/wp-content/uploads/2021/02/08172937/Common-Configuration-Include-and-Exclude-Patterns.png.webp"
categories:
  - "DevOps"
  - "Security"
tags:
related_posts:
  - "prevent-ldap-injection-in-java-with-springboot"
  - "foojay-podcast-7"
  - "the-lifecycle-of-a-security-vulnerability"
  - "build-secure-ai-chat-applications-with-boxlang-rag-ollama-and-amazon-bedrock-with-dan-card"
frozen: false
---

### TL;DR: Yet Another Case for Using Exclude Patterns in Remote Repositories

The npm Registry is vulnerable to supply chain namespace shadowing, also known as "Dependency Confusion" attacks. Make sure you create npm scoped packages and force exclude patterns.

### Long-time Obsession with Exclude Patterns

I remember the first JFrog customer training I delivered in February 2012. This slide was the one where I explained the importance of setting exclude patterns on your repositories (you can see it is 2012 by the slide design, right? Also, Ant was a thing.):

![Old training slide](https://media.jfrog.com/wp-content/uploads/2021/02/08172937/Common-Configuration-Include-and-Exclude-Patterns.png.webp)

The story went like this: Your company, Acme, has JFrog Artifactory installed. It has the usual set of remote repositories (which proxy various central repositories like Maven Central), local repositories (which host your company's internal artifacts), and a virtual repository (which aggregates locals and remotes under a single URL), simplifying the configuration of your build tool (in 2012, that would be Maven, duh).

Imagine, I said, you're working on a very secret project called Secret Almo. The artifact coordinates are org.acme:secret-almo:1.0, and you don't want your competitors to know about it. But what if one of your colleagues accidentally adds org.acme:secret-almo:1.1 as a dependency or any non-existing version of this library, and runs a build? This is what's going to happen:

1. The request arrives in [Artifactory's virtual repository](https://www.jfrog.com/confluence/display/JFROG/Virtual+Repositories), which (as it always does) **checks the [local repositories](https://www.jfrog.com/confluence/display/JFROG/Local+Repositories) first**. If your colleague didn't make the mistake and use 1.0 as a version, the resolution would stop there, and the correct artifact would be retrieved. But 1.1 is not found, so Artifactory keeps looking.
2. Artifactory looks in the remote repositories, which are the part of the virtual repo, one-by-one, sending the URL request containing your secret project name to external 3rd party repositories!

"Nix!" I shouted. "What a horrific neglect of privacy! What if someone watches the requests and discovers the Secret Almo!?!" Which was an appropriate way to motivate our users to use exclude patterns. Was it really that bad of a danger? I'm doubtful, but it helped.

We've been hammering this message into our customers and users since 2009:

* [Ariel Seftel in 2015](https://jfrog.com/blog/best-practices-repository-configuration/) -- "*Define an exclude pattern on remote repositories to prevent Artifactory from searching through them for packages that you know can not exist in them*"
* [Shani Levy in 2015 again](https://jfrog.com/blog/include-and-exclude-patterns/) -- this time specifically on security: "*Manage security and get a performance boost with exclude patterns*"
* [Rami Honing in 2016](https://jfrog.com/blog/push-the-limits-of-virtual-repositories-2/) -- "*modify... exclude patterns to enforce security policies.*"

The oldest user guide still around is Artifactory 2, written by the co-founders Yoav Landman and Fred Simon in 2009, and guess what? [It's right there](https://www.jfrog.com/confluence/display/RTF20/Local+and+Remote+Repositories):
> *It is **extremely** important to use include and exclude patterns for repositories. This is especially important for remote repositories in order to:*
>
> 1. *Avoid Looking up remote artifacts on repositories that will never contain those artifacts, or that contain only a limited range of group ids.*
> 2. *Not disclosing sensitive business information that can be derived from your artifact queries to whoever can intercept the queries, including the owners of the remote repository itself.*

Obviously, [the current documentation](https://www.jfrog.com/confluence/display/JFROG/Repository+Management#RepositoryManagement-AvoidingSecurityRiskswithanExcludePattern) highlights it as well:
> **Best practices using an excludes pattern for remote repositories to avoid security risks**
>
> To avoid exposing sensitive business information as described above, we strongly recommend the following best practices:
>
> * The list of remote repositories used in an organization should be managed under a single virtual repository to which all requests are directed
> * All internal artifacts should be specified in the **Exclude Pattern** field of the virtual repository (or alternatively, of each remote repository) using wildcard characters to encapsulate the widest possible specification of internal artifacts.

Well, you get the point; we are **big** on Exclude Patterns. But why? Is it really because we are afraid that someone in Sonatype (maintainers of Maven Central) or GitHub (maintainers of npm Registry) will scrutinize the logs and find out about Secret Almo?! Well, yes, but not only that. Enter the perfect storm of...

### The Caret, the Bazaar, and the Virtual Repository

Let's get back to the (now probably virtual) offices of Acme, where the work on the Secret Almo is still underway. Let's look at another component of the project, not secret at all, maybe a library, almo-common-utils. Its source might even publicly accessible, if, for instance, it is bundle as part of as Acme's publicly accessible products or web applications, it's written in Node and JFrog Artifactory now has a set of remote (proxying the official npm Registry), local (for sharing modules internally), and virtual npm repositories.

Consider the following:

1. npm Registry is a Bazaar ([in Raymond's terms](https://en.wikipedia.org/wiki/The_Cathedral_and_the_Bazaar)). **Anyone** can publish an unscoped npm library and call it **whatever they want**, i.e. "almo-common-utils" (unless there is a name conflict).
2. There is no package named "almo-common-utils" in npm registry (well, because it's an internal corporate library), so there is no name conflict.
3. Most of the npm dependencies declared using a **range** (the tilde or the caret) to request the **latest compatible version** , as defined in the Semantic Versioning standard and [its npm implementation](https://docs.npmjs.com/cli/v6/using-npm/semver#caret-ranges-123-025-004).

The only information an attacker needs to try to attack an unprotected organization is the existence of almo-common-utils, the major version of the library in use (let's say they know version 3 is used widely in the organization), and the content of the source code.

They can clone and modify the source, embedding any malware inside, but still maintain compatibility with the original code, and upload it to npm Registry as secret-almo:3.99.99 **because who is going to stop them?**...

Now let's look at Artifactory resolution at work **without the exclude pattern** when secret-almo:\^3.0.0 is requested :

1. Look for the latest compatible secret-almo in local repositories. Found 3.2.4.
2. Look for the latest compatible secret-almo in npm-registry proxy remote repository. Found 3.99.99.
3. The fake secret-almo from npm registry wins, and the **supply chain is hijacked**.

Remember I shouted "Nix!"? This is a double-nix. And an ouch.

But this is where an experienced Node.js developer would stand up and say...

### But scoped packages!

Remember how I told you anyone can upload anything to the npm Registry? Well, not quite. If you create an organization in the npm Registry, you'll be assigned a namespace to which only the members of your organization can upload (this is what npm calls [public scoped packages](https://docs.npmjs.com/about-organization-scopes-and-packages)). Does it save you from the Namespace Shadowing attack? Yes, when done right.

First, the company has to create the organization and reserve the namespace, because as everything in npm Registry, they are up for grabs. If your organization didn't reserve it, a more sophisticated attack could still succeed -- now the attacker has to hope some inexperienced developer will find a package with a familiar scope in npm Registry ("hey, look, there is an @acme/almo-common-utils here, it's ours for sure") and decide to use it. Also, even members of the official organization can still be neglectful and publish unscoped packages.

Solution? You know it by now...

### Two simple rules to save your butt from the Namespace Shadowing (a.k.a. "Dependency Confusion") Attack

1. **Only publish scoped packages!** Register an official organization for your company in npm Registry. Always publish only public scoped packages. BTW, it also simplifies the exclude patterns (see next rule), as you only need to exclude .npm/@acme/\* now to exclude all the packages from being searched in remote repositories.
2. **Use exclude patterns on your remote repositories!** You know for a fact almo-common-utils would never be found in npm Registry? Tell it to your repository manager! [Add your private dependencies in exclude patterns](https://www.jfrog.com/confluence/display/JFROG/Repository+Management#RepositoryManagement-AvoidingSecurityRiskswithanExcludePattern) and protect yourself from a serious (and quite clever) supply chain attack. It's so easy it's almost neglectful not to do so.

We'll finish up with...

### FAQ from Your Devil's Advocate

**Q: Are the Namespace Shadowing (a.k.a. "Dependency Confusion") attacks pose a security issue only if I use Artifactory?**

A: Any repository manager which allows combining local and proxied repositories under a single URL can be attacked in a similar manner, so no, this is not an Artifactory security issue. Those include Sonatype Nexus, and others. This isn't because all of them are faulted in some way -- they are working precisely as intended. And all of them have and strongly recommend the usage of best practices similar to exclude patterns (e.g. Routing Rules in Sonatype Nexus).

**Q: Why don't people use public scoped packages? Isn't it the way to go?**

A: It is for sure the way to go. But npm only introduced scoped packages in version 2 and it's not as common in the industry as it should be (at least by now). But you? You rock, so you use them, right?

**Q: Why doesn't npm Registry mandate scoped packages by verified owners to avoid namespace shadowing attacks?**

A: That's a very good question.
