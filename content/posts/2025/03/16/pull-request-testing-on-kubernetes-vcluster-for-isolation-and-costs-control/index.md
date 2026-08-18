---
title: "Pull request testing on Kubernetes: vCluster for isolation and costs control"
date: "2025-03-16T11:47:20+00:00"
lastmod: "2025-03-17T08:30:26+00:00"
description: "This week's article is the third and final in my series about running tests on Kubernetes for each pull request."
canonical: "https://www.loft.sh/blog/pull-request-testing-on-kubernetes"
authors:
  - "nicolas-frankel"
image: "Favicon-3-2.png"
categories:
  - "Developer Tools"
  - "DevOps"
related_posts:
  - "how-to-beautify-your-github-repo"
  - "is-it-time-to-go-back-to-the-monolith"
  - "langchain4j-musings"
  - "my-final-take-on-gradle-vs-maven"
frozen: false
---

This week's article is the third and final in my series about running tests on Kubernetes for each pull request. In the [first post](https://blog.frankel.ch/pr-testing-kubernetes/1/), I described the app and how to test locally using Testcontainers and in a GitHub workflow. The [second post](https://blog.frankel.ch/pr-testing-kubernetes/2/) focused on setting up the target environment and running end-to-end tests on Kubernetes.

I concluded the latter by mentioning a significant quandary. Creating a dedicated cluster for each workflow significantly impacts the time it takes to run. On , it took between 5 and 7 minutes to spin off a new cluster. If you create a GKE instance upstream, you face two issues:

* Since the instance is always up, it raises costs. While they are reasonable, they may become a decision factor if you are already struggling. In any case, we can leverage the built-in Cloud autoscaler. Also, note that the costs mainly come from the workloads; the control plane costs are marginal.
* Worse, some changes affect the whole cluster, *e.g.* , version changes. CRDs are cluster-wide resources. In this case, we need a dedicated cluster to avoid incompatible changes. From an engineering point of view, it requires identifying which PR can run on a shared cluster and which one needs a dedicated one. Such complexity hinders the delivery speed.

In this post, I'll show how to benefit from the best of both worlds with [vCluster](https://vcluster.com): a single cluster with testing from each PR in complete isolation from others.
> Virtual clusters are fully functional Kubernetes clusters nested inside a physical host cluster providing better isolation and flexibility to support multi-tenancy. Multiple teams can operate independently within the same physical infrastructure while minimizing conflicts, maximizing autonomy, and reducing costs.
>
> -- [What are virtual clusters?](https://www.vcluster.com/docs/)

With virtual clusters, we can have our cake---a single physical cluster for limited costs---and eat it with fully isolated virtual clusters.

## Weaving vCluster into the GitHub workflow

Weaving vCluster into the GitHub workflow is a three-step process:

1. Install vCluster
2. Create a virtual cluster
3. Connect to the virtual cluster

```yaml
- name: Install vCluster
  uses: loft-sh/setup-vcluster@main                                    #1
  with:
    kubectl-install: false
- name: Create a vCluster
  id: vcluster                                                         #2
  run: time vcluster create vcluster-pipeline-${{github.run_id}}       #3
- name: Connect to the vCluster
  run: vcluster connect vcluster-pipeline-${{github.run_id}}           #4
```

1. Install vCluster. By default, the action installs the latest available version. You can override it.
2. Step IDs are not necessary unless you want to reference them in later steps. We are going to need it
3. Create the virtual cluster. To avoid collisions, we name it with the workflow name suffixed with the GitHub run ID
4. Connect to the virtual cluster

The output is along the following lines:

```
> Run time vcluster create vcluster-pipeline-12632713145

12:44:13 info Creating namespace vcluster-vcluster-pipeline-12632713145
12:44:13 info Create vcluster vcluster-pipeline-12632713145...
12:44:13 info execute command: helm upgrade vcluster-pipeline-12632713145 /tmp/vcluster-0.22.0.tgz-2721862840 --create-namespace --kubeconfig /tmp/3273578530 --namespace vcluster-vcluster-pipeline-12632713145 --install --repository-config='' --values /tmp/3458157332
12:44:19 done Successfully created virtual cluster vcluster-pipeline-12632713145 in namespace vcluster-vcluster-pipeline-12632713145
12:44:23 info Waiting for vcluster to come up...
12:44:35 info vcluster is waiting, because vcluster pod vcluster-pipeline-12632713145-0 has status: Init:1/3
12:45:03 done vCluster is up and running
12:45:04 info Starting background proxy container...
12:45:11 done Switched active kube context to vcluster_vcluster-pipeline-12632713145_vcluster-vcluster-pipeline-12632713145_gke_vcluster-pipeline_europe-west9_minimal-cluster
- Use `vcluster disconnect` to return to your previous kube context
- Use `kubectl get namespaces` to access the vcluster

real	1m2.947s
user	0m0.828s
sys	0m0.187s

> Run vcluster connect vcluster-pipeline-12632713145

12:45:13 done vCluster is up and running
12:45:13 info Starting background proxy container...
12:45:16 done Switched active kube context to vcluster_vcluster-pipeline-12632713145_vcluster-vcluster-pipeline-12632713145_gke_vcluster-pipeline_europe-west9_minimal-cluster
- Use `vcluster disconnect` to return to your previous kube context
- Use `kubectl get namespaces` to access the vcluster
```

For fairness' sake, I used the time command to measure the creation time of a virtual cluster precisely. I measure other steps by looking at the GitHub workflow log.

Installing vCluster and connecting to the virtual cluster take around one second. The creation of a virtual cluster takes about one minute; the creation of a full-fledged GKE instance takes at least five times more.

## Changes to the workflow

Here comes the great news: there's absolutely no change to any of the workflow steps. We can keep using the same steps because a virtual cluster has the same interface as a regular Kubernetes cluster.

It includes:

* Installing the PostgreSQL Helm Chart
* Creating the PostgreSQL connection parameters `ConfigMap`
* Creating the GitHub registry `Secret`
* Applying the Kustomized manifest
* And retrieving the external IP from the `LoadBalancer`!

If you are already using Kubernetes, and you probably are because you read this post, introducing vCluster in our daily work does not require any breaking changes.

## Cleaning up

So far, we haven't cleaned up any objects we created. It means pods with our app and PostgreSQL keep piling up in the cluster, not to mention `Service` objects, making available ports a scarce resource. It was not an oversight: the reason was that it was a lot of overload to delete each object individually. I could have deployed all objects of a workflow run into a dedicated namespace and deleted that namespace. Unfortunately, I've been bitten by [namespaces stuck in the `Terminating` state](https://www.baeldung.com/ops/delete-namespace-terminating-state) before.

On the opposite, deleting a virtual cluster is a breeze. Let's add the last step to our workflow definition:

```yaml
- name: Delete the vCluster
  run: vcluster delete vcluster-pipeline-${{github.run_id}}
```

There still is one issue: if a step of a GitHub workflow fails, *i.e.* , returns a non-0 exit code, the job fails immediately, **and GitHub skips executing subsequent steps**. Hence, the above cleanup won't happen if the end-to-end tests fail. For example, it might be on purpose to keep the cluster's state if things go wrong. In this case, you should rely on observability instead for this purpose, like you do in production. I encourage you to delete your environment in every case.

GitHub provides an `if` attribute to run a step depending on conditions. For example, it offers a `if: always()`; GitHub runs the step regardless of the success or failure of previous steps. It would be redundant since we don't want to delete the virtual cluster unless it has been created in a prior step. We should delete it only if the creation is successful:

```yaml
- name: Delete the vCluster
  if: ${{ !cancelled() && steps.vcluster.conclusion == 'success' }}    #1
  run: vcluster delete vcluster-pipeline-${{github.run_id}}
```

1. Run if the job wasn't canceled **and** if the `vcluster` step (defined above) was successful. The job cancellation guard isn't necessary, but it allows you to keep the cluster up anyway.

The above setup allows each Pull Request to run in its sandbox, avoiding conflicts while controlling costs. By leveraging this approach, you can simplify your workflows, reduce risks, and focus on delivering features without worrying about breaking shared environments.

## Conclusion

This post concludes our series on testing Pull Requests on Kubernetes. In the first post, we ran unit tests with Testcontainers locally and set up the foundations of the GitHub workflow. We also leveraged GitHub Service Containers in our pipeline. In the second post, we created a GKE instance, deployed our app and its PostgreSQL database, got the `Service` URL, and ran the end-to-end tests. In this post, we used vCluster to isolate each PR and manage the costs.

While I couldn't cover every possible option, the series provides a solid foundation for starting your journey on end-to-end testing PRs on Kubernetes.

The complete source code for this post can be found on [GitHub](https://github.com/ajavageek/vcluster-pipeline).

**To go further:**

* [vCluster](https://vcluster.com)
* [Deploy vCluster on GKE](https://www.vcluster.com/docs/vcluster/deploy/environment/gke)
* [GitHub Action to install the vcluster CLI](https://github.com/loft-sh/setup-vcluster)
* [google-github-actions/get-gke-credentials failed with: required 'container.clusters.get' permission(s)](https://stackoverflow.com/questions/78670276/google-github-actions-get-gke-credentials-failed-with-required-container-clust)
* [GitHub workflow status check functions](https://docs.github.com/en/actions/writing-workflows/choosing-what-your-workflow-does/evaluate-expressions-in-workflows-and-actions#status-check-functions)

*[GKE]: Google Kubernetes Engine
*[CRD]: Custom Resource Definition
