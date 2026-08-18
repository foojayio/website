---
title: "Shai-Hulud and the npm Worm: How Speed-Optimised Dev Ecosystems Made a Self-Propagating Supply Chain Attack Inevitable"
slug: "the-shai-hulud-cyber-worm-and-more-thoughts-on-supply-chain-attacks"
date: "2026-02-12T11:47:48+00:00"
lastmod: "2026-02-12T11:48:52+00:00"
description: "first, a word about ecosystems Before we dive into Shai-Hulud, before we label it “sophisticated” or “advanced” or “next generation,” we need to be honest - by Steve Poole"
authors:
  - "steve-poole"
image: "ChatGPT-Image-Sep-19-2025-11_39_44-AM.png"
categories:
  - "DevOps"
  - "Security"
tags:
related_posts:
frozen: false
---

first, a word about ecosystems
------------------------------

Before we dive into Shai-Hulud, before we label it "sophisticated" or "advanced" or "next generation," we need to be honest about something.

The worm didn't appear in a broken system. It appeared in the one we deliberately optimised.

In the book Dune, the worm is integral to the ecosystem. The planet, its environment, and the worms are deeply interconnected.

That's true for the cyber-worm equivalent. The worm is an entirely predictable outcome of the ecosystem in which we, as developers, are part

Speed first.
------------

Modern software development is not primarily about writing code. It is about assembling it. Most production systems today consist of orchestration layers that sit on top of hundreds or thousands of external components.

The majority of what runs in your application was written by someone else. Often years ago. Often maintained by very small teams. Sometimes no longer maintained at all.

And that model works extraordinarily well. It has given us leverage that would have been unimaginable twenty years ago.

Still optimised for speed.
--------------------------

Dependencies install automatically. Pipelines publish automatically. Credentials are injected automatically.

We removed friction wherever possible because it slows delivery. Over time, the practice of executing third-party code during installation became routine rather than risky.

In npm, lifecycle scripts run during dependency resolution. That is not considered unusual behaviour. It is normal build plumbing. Developers run `npm install` dozens of times a week without consciously registering that they are executing code pulled from the internet.

The same cultural shift happened everywhere. Whether it is `curl | bash`, `brew install`, or `apt install`, installing software has become so common that the execution aspect fades into the background.

Even security warnings feel ornamental. Of course, the file came from the internet. That was the intent.

Meanwhile, we centralised authority in automation. CI systems don't just build. They publish. They push. They modify repositories. They deploy infrastructure. They do this using tokens that are often long-lived and broadly scoped because tight scoping and aggressive expiry introduce inconvenience.

Automation is trusted precisely because it removes human delay.

This is the architecture we normalised: install-time execution, credential-rich automation, and frictionless publishing, all operating within the same trust boundary.

Once you see that, the worm stops looking exotic.

Open Source Security Doesn't Work the Way You Think It Does
-----------------------------------------------------------

There's a deeper problem running underneath this.

A significant portion of widely deployed open-source components sit on branches that no longer receive patches.

Some projects formally declare end of life. Others simply stop maintaining older versions. Security fixes are released in the current major versions without consistently documenting the full historical impact. In many modern components, it is a deliberate choice: older software, even though vulnerable, is often not included in CVE reports.

It's vital to understand that vulnerability databases reflect what is disclosed, not what can be inferred from patch diffs.

Software Composition Analysis tools mostly rely on those databases. If a vulnerability is not formally recorded against your exact version, the tool has no authoritative basis to flag it. "No CVEs found" becomes interpreted as "safe," even when the branch has effectively stopped receiving security attention.

Attackers can read patches. They can see what changed. They can infer the bug from the fix. Older branches don't become secure just because a CVE doesn't list them.

This matters more than people realise.
--------------------------------------

There is an entire commercial support industry built around keeping end-of-life open source secure, and most developers I speak to don't even realise that's an option. The broader reporting gap is even less understood. We assume visibility is complete because dashboards are green. *It isn't.*

This situation is so bad, so misunderstood, that the company I work for, [HeroDevs](https://bit.ly/4av8c4G "HeroDevs"), a company that provides support for EOL open source for times like these. Pretty much no one I speak to knows that this is an option. That said, most aren't aware of the broader security reporting situation either.

The Inevitable AI in the Mix
----------------------------

AI Language models suggest dependencies based on prevalence and pattern similarity. They do not reason about support windows. They do not possess privileged vulnerability intelligence. If an older version appears frequently in training data and compiles successfully, it remains attractive. Humans behave in much the same way. If an installation works and triggers no alerts, it is accepted.

We built a system in which execution is invisible, authority is automated, lifecycle awareness is partial, and adoption is increasingly rapid.

In that environment, a registry-native worm is not surprising, other than how long it's taken to appear.

Enter Shai-Hulud
----------------

In September 2025, the npm ecosystem saw what is widely regarded as the first documented self-propagating registry-native worm.

Nobody hacked npm. Nobody broke TLS. There was no exotic zero-day. Attackers phished maintainers and harvested legitimate npm and GitHub tokens. That was enough.

Malicious versions of popular packages such as `@ctrl/tinycolor` and `ngx-bootstrap` were published. They contained a payload, typically named `bundle.js`, that executed via npm lifecycle scripts during installation.

When developers ran `npm install`, the worm ran automatically.  

The script harvested credentials from developer machines and CI environments. It searched for npm authentication tokens, GitHub Personal Access Tokens, and cloud provider keys. Some variants deployed TruffleHog to aggressively scrape secrets.

Then it pivoted.
----------------

If it found valid npm tokens, it enumerated other packages owned by the compromised maintainer, injected the malicious payload, incremented the version number, and republished them using authenticated `npm publish --force`.

The registry accepted those publications because they were authorised.

At that point, the attack moves from "malicious code in a package" and became something fundamentally different.

The stolen credentials were not used solely to push a single malicious version. They were used to infect other legitimate, already-trusted packages owned by the same maintainer.  

The worm enumerated the maintainer's portfolio, injected itself, incremented versions, and republished. The registry itself became the replication medium.

The defining shift.
-------------------

This was not typosquatting. It was not dependency confusion. It was not a one-off compromised release. It was automated, identity-driven propagation using a legitimate publishing authority. That is what makes Shai-Hulud a worm rather than simply malware in npm.

Across multiple waves, nearly 800 packages were implicated, representing potential exposure in excess of 20 million weekly downloads.

The attack evolved. A later wave shifted execution to the Bun runtime, likely to evade process-based monitoring, and briefly introduced destructive fallback behaviour before refining itself for stability.

This was not smash-and-grab. It was automated replication using legitimate, local authority.

This Is Also What Cyberwar Looks Like
-------------------------------------

There's another angle here that most coverage skipped.

Shai-Hulud had a theatrical quality. The name. The public GitHub repositories. The visible propagation. The slightly adolescent branding in later waves. It looked loud. Almost playful. That can be misleading.

In military terms, weapons are not simply built and deployed. They are tested. They are refined. They are evaluated in live environments. Visibility is not always a flaw.

*Sometimes it is telemetry.*

A registry-native worm that exercises install-time execution, credential harvesting, token replay, automated publishing, CI mutation, and cross-platform runtime switching is not just "malware." It is a systems test.

It measures detection latency. It measures registry response. It measures how quickly maintainers rotate credentials. It measures how effectively organisations distinguish authorised from malicious behaviour.

We tend to assume that if something is visible, has a name, and leaves public traces, it is unsophisticated. That assumption is dangerous.

The fact that Shai-Hulud was noisy does not mean it was trivial. It may simply mean that noise was part of the experiment.

History should make us cautious here.
-------------------------------------

When the Equifax breach was first disclosed, the focus was on data theft. Much later, it emerged that records had also been modified. False credit ratings were inserted. The integrity impact was subtler than the headline about exfiltration.

Exfiltration is easy to understand. Integrity manipulation is harder to detect and slower to surface. Shai-Hulud's public behaviour does not guarantee that its full impact is understood.

A worm capable of harvesting credentials and republishing packages is also capable of modifying code in ways that are far less obvious than a visible payload file.

If you wanted to test ecosystem response, this is exactly how you would do it. And if you wanted to insert something subtle, you would hide it behind the noise.

None of this requires conspiracy thinking.
------------------------------------------

It simply requires acknowledging that software supply chains now operate within geopolitical realities. Package registries are a global infrastructure. CI systems are critical infrastructure. They are soft targets compared to hardened networks.

Funny names and public repositories can lull us into treating something as mischievous rather than strategic.

*That would be a mistake.*

Why It Was So Effective
-----------------------

Shai-Hulud did not need to break the registry. It exploited the fact that install-time execution, credential-harvesting potential, and publish authority coexist within the same operational flow.

Dependency installation is routine and rarely scrutinised. Lifecycle scripts are accepted as build behaviour rather than treated as remote code execution. CI environments are credential-rich by design. Maintainer identities often span multiple packages. Publishing is frictionless for authenticated users.

When the worm harvested tokens, it did not need to escalate privileges. The privileges were already present. When it republished packages, it did not exploit npm. It exercised legitimate authority.

The truth is that we allowed the worm to behave like a developer. It authenticated, it published, it modified workflows, and it interacted with GitHub APIs. From the perspective of logs, it looked like a maintenance activity.

This is why detection is hard
-----------------------------

Traditional security models ask whether an action is authorised. In this case, it was. Once credentials were stolen, the worm operated entirely within permission boundaries.

The challenge lies in distinguishing malicious use of legitimate authority from routine automation. In environments already saturated with scripted releases and automated publishing, that distinction is not trivial.

The worm hides inside the behaviours we normalise.

Getting Practical: Without Pretending It's Easy
-----------------------------------------------

At this point, the obvious question is: what do we actually do?  

The industry response to incidents like this is predictable. We talk about dependency provenance checks. We talk about SBOMs and supply chain visibility. We talk about lockfile enforcement, short-lived credentials, least privilege, egress controls, and hardened CI.

All of those things are useful. None of them, on their own, is decisive. Dependency provenance is useful when verifying and when you are prepared to block builds on verification failure.

Generating an SBOM is useful only if it informs a system that changes behaviour. A lockfile protects you from unexpected version drift, but it does nothing if the version you locked was already compromised.

Short-lived credentials reduce the blast radius, but they do not prevent phishing.

Network controls make exfiltration harder, but they require treating your build environment as more than a convenience tool.

These measures are just friction.
---------------------------------

Friction works only if you accept that some workflows must slow down.

I expect that most organisations will implement visible controls while silently preserving the behaviours that made the worm effective. They will generate SBOMs but not act on them. They will enable security scanning but auto-merge updates. They will reduce token lifetimes while keeping publish authority within the same trust boundary as install-time execution.

The Shai-Hulud worm didn't succeed because we lacked tooling. It succeeded because install-time execution, credential concentration, and frictionless publishing coexisted without meaningful separation.

If you want practical mitigation, you have to separate those concerns.

Treat dependency installation as code execution. Treat CI as critical infrastructure rather than background plumbing. Treat the authority to publish as something that deserves ceremony. Treat unsupported software as a risk condition even when dashboards are green.

Provenance checks, SBOM-driven alerts, lockfiles, token rotation, and network segmentation can materially reduce impact. However, they only change outcomes when paired with a cultural shift away from "speed first, security later."

Without that shift, they are compliance theatre. With it, they become leverage in your favour.

The Mirror
----------

Shai-Hulud did not introduce a new class of risk. It exposed the one we had already optimised for.

We built a software factory designed for trust and speed. Attackers optimised for leverage and scale. When those incentives aligned, replication became trivial.

We didn't get unlucky. We got efficient.

The worm was not an anomaly. It was the mirror. It was a completely predictable product of our ecosystem's behaviour.

It won't be the last.
