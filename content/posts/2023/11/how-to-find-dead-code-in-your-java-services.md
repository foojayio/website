---
title: "How to find dead code in your Java services"
slug: "how-to-find-dead-code-in-your-java-services"
date: "2023-11-07T10:40:48+00:00"
lastmod: "2023-11-07T14:53:08+00:00"
description: "There’s an interesting relation between the problem of finding dead code and another widespread practice: measuring code coverage for tests."
authors:
  - "jakob-loehnertz"
  - "sander-mak"
image: "/images/posts/2023/11/how-to-find-dead-code-in-your-java-services/1_zB4tm16eE9QotLZZAads7g.png"
categories:
  - "Java"
  - "Tools"
tags:
related_posts:
  - "why-picnic-picked-java"
  - "embracing-java-17-heres-what-we-learned-at-picnic"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
enlighterjs: true
frozen: false
---

**When building solutions, the code we write can last many years. While casually browsing legacy code, we might wonder; is this still used? The missing documentation or outdated tests do not help us answer this. When asking around, nobody really knows. Let's try to delete it, shall we? Then, chaos ensues: it turns out it is still used to support some legacy users, in case of emergency, or by that one forgotten integration everyone still uses.**{#2406}

When it is *actually* not used anymore, it is oftentimes because of historical reasons that code is still around. Use-cases might have been replaced by new functionalities or have become obsolete and code is forgotten to be deleted. In cases where it is not trivial to investigate this dead code, how can we increase our certainty?{#d565}

*This post was originally written by [Pieter Dirk Soels](https://medium.com/@p.d.soels?source=post_page-----d167c8f22838--------------------------------).*{#c03d}

There's an interesting relation between the problem of finding dead code and another widespread practice: measuring code coverage for tests. Let's find out how these two worlds may converge!{#7c33}

At Picnic, we use one of the tools that almost every Java developer has, directly or indirectly, interacted with: the [Java Code Coverage Library](https://www.jacoco.org/jacoco/) (JaCoCo). This tool is mostly used to report code coverage on automated test suites to gain more confidence that a test covers what it should. It collects coverage metrics by instrumenting the bytecode through a Java agent when the Java class loader loads the classes. In a future blog post, we will further dive into how it works under the hood.{#9155}

In principle, a Java agent can run in any environment, not just during development --- as is typically the case with JaCoCo. Why not run it also in production to see actual code coverage?{#71a0}

Carlos Becker^[1](#b5c1e9e5-5fce-4bfe-9cb7-6477372782a0){#b5c1e9e5-5fce-4bfe-9cb7-6477372782a0-link}^ and Markus Harrer^[2](#90d3d782-1949-4ee2-adf5-14f96c7f0444){#90d3d782-1949-4ee2-adf5-14f96c7f0444-link}^ also brought this up before. We wanted to follow in their footsteps but instead fetch the coverage at any time in an ephemeral context, that is, Kubernetes. So let's get started!{#a909}

First, we need to get the JaCoCo Java agent JAR. We can retrieve it from the latest [release](https://github.com/jacoco/jacoco/releases/latest) or the central Maven repository. There are several ways to get this JAR in your Kubernetes pod, such as copying it into the container's image or making it available through a mounted volume. Once it's available, it's time to start configuring the JVM. We can configure the agent using the `-javaagent` JVM argument. There are plenty of [configuration options](https://www.eclemma.org/jacoco/trunk/doc/agent.html). At Picnic, we run it as follows:{#b736}

`-javaagent:/path/to/jacocoagent.jar=includes=tech.picnic.*,output=tcpserver,address=*`{#b736}

This enables the JaCoCo Java agent and configures it to only instrument classes in our `tech.picnic.*` packages. The more specific we are here, the less performance overhead we will have, as fewer classes will be instrumented. We also configure JaCoCo to write to incoming TCP connections through `tcpserver`, which we will use to interact with the agent. Using the server, we can fetch the data anytime while the pod is alive.{#fe68}
> *Note: As we expose a server here, security is important. By default, the JaCoCo server listens on port 6300. By setting `address=*` we only allow connections from local addresses. We do not expose port 6300 in our containers and services. We will later show that we perform Kubernetes port-forwarding when interacting with the JaCoCo Java agent.*{#7355}

Now that we have JaCoCo running in production, it's time to gather data! As Kubernetes pods are ephemeral, we need to dump data to a persistent volume on termination or periodically fetch our data and accept the risk of data loss. As this data is not critical to us and only applies to that particular revision of the service anyway, we choose the latter.{#d67e}

To fetch the data, we need to use JaCoCo's CLI. This is also available as a JAR from JaCoCo's latest [release](https://github.com/jacoco/jacoco/releases/latest) on GitHub.{#b7f1}

For gathering JaCoCo's data, we use a script similar to the following (simplified) version, which is structured as follows:{#69ac}

* It loops over all pods, matching the specified Kubernetes cluster, namespace, and pod label.
* In a subshell, it then forwards connections to the local port 6300 to the pod's port 6300 in a background process. This background process will be interrupted once the subshell exits.
* After the port is forwarded, we use JaCoCo's CLI to dump the data to the local machine.
* Once we collected all the data from all the pods, we merge the dumped data into a single data file.

<pre class="EnlighterJSRAW" data-enlighter-language="bash">#!/usr/bin/env bash
set -e -u -o pipefail
if [ "${#}" -lt 4 ]; then
  echo "Usage: ${0} &lt;path-to-jacococli.jar&gt; &lt;kubernetes-context&gt; &lt;kubernetes-namespace&gt; &lt;kubernetes-pod-selector&gt;"
  echo "For selector syntax, see https://kubernetes.io/docs/concepts/overview/working-with-objects/labels."
  exit 1
fi
JACOCO_CLI_LOCAL="$(realpath "${1}")"
CONTEXT="${2}"
NAMESPACE="${3}"
POD_SELECTOR="${4}"
# All resources are stored in a temporary directory.
TMP_DIR='/tmp/jacoco-export'
mkdir -p "${TMP_DIR}"
pushd "${TMP_DIR}"
# Fetch coverage data from all pods.
kubectl get pods \
  --context="${CONTEXT}" \
  --namespace="${NAMESPACE}" \
  --selector="${POD_SELECTOR}" \
  --no-headers \
  -o custom-columns=":metadata.name" \
  | while read POD; do
    # One pod at a time, open a tunnel, connect to the JaCoCo agent to fetch
    # the coverage data and close the tunnel again.
    echo $CONTEXT - $POD
    (
      kubectl port-forward \
        --context="${CONTEXT}" \
        --namespace "${NAMESPACE}" \
        "${POD}" \
        "6300:6300" &amp;
      trap "kill ${!}" ERR EXIT HUP INT TERM
      java -jar "${JACOCO_CLI_LOCAL}" dump --destfile "jacoco-${POD}.exec"
    )
  done
# Merge the coverage data into a single file.
java -jar "${JACOCO_CLI_LOCAL}" merge --destfile jacoco.exec jacoco-*.exec
popd</pre>

Source: [Gist on GitHub](https://gist.github.com/Badbond/0f685d133763864a0760bbfc477e1f82)
> *Note: This script assumes that selected pods run the same software source-code revision. This is necessary as later we will combine coverage data with source code to have the coverage visualized, and JaCoCo can otherwise not distinguish between revisions. As such, as long as the pods run the same revision, it is also possible to run this multiple times and combine the data as the script does.*{#fd29}

Now that we have our binary format of the JaCoCo coverage data, we can generate an HTML or XML report out of it. For this, we need to pass JaCoCo the compiled `.class` files and source `.java` files matching the revision.

To get HTML reporting of source code line coverage, [JaCoCo requires](https://www.jacoco.org/jacoco/trunk/doc/counters.html#lines) that the classes are compiled with debug information such that it can relate the bytecode coverage to the source code. At Picnic, we use the Apache Maven Compiler Plugin to compile our code, which does this [by default](https://maven.apache.org/plugins/maven-compiler-plugin/compile-mojo.html#debug).{#f50c}

To generate the report and pass the class and source code files, we also provide a script below. Path matchers for these files are defaulted to the Maven directory structure, but can be overwritten.{#29ac}

<pre class="EnlighterJSRAW" data-enlighter-language="bash">#!/usr/bin/env bash
set -e -u -o pipefail
if [[ "${#}" -lt 2 ]]; then
  echo "Usage: ${0} &lt;path-to-jacococli.jar&gt; [--sourcefiles-matcher=&lt;string&gt;] [ --classfiles-miles-matcher&lt;string&gt;] &lt;source-roots&gt;..."
  exit 1
fi
JACOCO_CLI_LOCAL="$(realpath "${1}")"
shift
SOURCEFILES_MATCHER='*/src/main/java'
CLASSFILES_MATCHER='*/target/classes'
while [ "${#}" -gt 0 ]; do
  case "${1}" in
  --sourcefiles-matcher)
    shift
    SOURCEFILES_MATCHER="${1}"
    ;;
  --classfiles-matcher)
    shift
    CLASSFILES_MATCHER="${1}"
    ;;
  *)
    break
    ;;
  esac
done
if [[ "${#}" -ne 1 ]]; then
  echo "No source roots passed."
  exit 1
fi
pushd /tmp/jacoco-export
# Generate an HTML report based on the collected data. Note that this command
# assumes that the associated source code has been compiled (i.e. that the
# `target/classes` directories are present and populated).
java -jar "${JACOCO_CLI_LOCAL}" report jacoco.exec --html report \
  $(find "${@:1}" -path "${CLASSFILES_MATCHER}" | sed 's/^/--classfiles /') \
  $(find "${@:1}" -path "${SOURCEFILES_MATCHER}" | sed 's/^/--sourcefiles /')
popd</pre>

Source: [Gist on GitHub](https://gist.github.com/Badbond/0777680409ce28349c792416535940e0#file-jacoco_generate_report-sh)

Now that we have generated a report, we can inspect the generated `report/index.html` and look for coverage on some suspected legacy code. We are looking for red lines, which means the code is not covered. Let's dive into some examples.{#d3a0}

<figure class="wp-block-image is-resized">
 <img fetchpriority="high" decoding="async" src="https://miro.medium.com/v2/resize:fit:473/0*qYvVZekF-qruyoxt" alt="" style="width:546px;height:125px" width="546" height="125">
 <figcaption class="wp-element-caption">
  A service method marked for deletion which has not been executed.
 </figcaption>
</figure>

This bit of legacy code has been around for a few years already and has been marked as deprecated for a few months already. When searching for usages of this method across organization repositories, we find that it is indeed unused apart from tests!{#189a}

Time to drop it and leave our codebase cleaner. The planned migration for this service may have become easier now too, given that we have less functionality to take into account. It's time to have our developers revisit that migration.{#cd92}
![](https://miro.medium.com/v2/resize:fit:700/0*sWaFg5jfWYPtTZct) Part of a deserializer with support for legacy ArticleDeliveryIssue instances, still being dependent upon.

Another example: take this deserializer with some logic for handling legacy data. We wondered whether we could already drop support for these legacy objects, but apparently, it still needs to deserialize some of these instances! It's good that we didn't just ask around or do some code searches and just delete it; it could've been quite the *issue*!{#ae73}

As you can see, this analysis can help you get a clearer picture of what code is still used in your projects. It's important to note, however, that this does not mean we can now delete all code without coverage. Some logic might be used in seasonal cases, demos, or emergencies. A careful look is still desired, but this helps us get a little more confident in what we may delete.{#4a3d}

To see whether this is the case, we usually perform code searches to look at the age and commits for surrounding code. We identify whether code is exceptionally new --- maybe this is a feature in development or old and forgotten. Finding connected API endpoints and their documentation might also help to get an understanding of why this code is around. We also search our ticket and communication systems for any references and, of course, simply ask around.{#46c5}

Every time we introduce new tooling in production environments, we should understand its effect on application performance. This holds especially when instrumenting our code, as this can add quite some overhead in executing instructions. To understand its performance impact, we first ran it on staging environments to find any immediate problems with resource usage. This allowed us to tweak the settings accordingly.{#ad9a}

To determine the performance in production, we kept an eye out for the average duration of the request. We selected two 24-hour periods, covering different loads for this application. One period with running JaCoCo, and one without.{#71de}
![](https://miro.medium.com/v2/resize:fit:700/0*neVQTAom_1QPLD6V) Average request duration in service while running with JaCoCo (red) and without (grey).

From this, we observed an average overhead of 0.03%. As that is such a small overhead in the context of Picnic, we found this an acceptable price to pay for the insights we gain.{#1033}

With our codebase in a better place, we will continue to periodically scan it for code we can delete. However, this does not stop us from thinking about what more we can do with JaCoCo.{#c81e}

Lastly, I want to express my gratitude to [Stephan Schroevers](https://github.com/Stephan202) for driving this initiative and for his guidance, allowing me to pursue these forms of analyses.{#d457}

👉 Come [work with us](https://picnic.app/careers/jobs/986403/technology--amp--engineering/amsterdam-north-holland-netherlands/java-developer) to shape the future of Java at Picnic!
