---
title: "Deploying Spring Boot Application on Kubernetes"
slug: "deploying-spring-boot-applications-on-kubernetes"
date: "2021-02-26T17:51:44+00:00"
lastmod: "2021-07-05T20:03:09+00:00"
description: "Learn how you can create a pod, deploy a Spring Boot application, and manage the single node cluster with Lens IDE on Docker Desktop."
canonical: "https://ashishtechmill.com/deploying-spring-boot-application-on-kubernetes"
authors:
  - "yrashish"
image: "/images/posts/2021/02/deploying-spring-boot-applications-on-kubernetes/Screenshot-2020-12-07-at-7.29.06-PM-700x219.png"
categories:
  - "Kubernetes"
tags:
related_posts:
enlighterjs: true
frozen: false
---

In this article, I will explain how you can create a pod, deploy a Spring Boot application, and manage the single node cluster with Lens IDE on Docker Desktop.

### What you will learn. {#h3-0-what-you-will-learn}

* How to Enable Kubernetes With Docker Desktop for Mac
* How to Create a Pod
* How to Deploy a Working Spring Boot Application on Kubernetes
* How to Monitor and Manage Kubernetes Cluster with Lens IDE

Moving on, you may not know this, but you can have a functional single-node Kubernetes cluster running locally with your Docker Desktop by following the below steps. But wait, What is Docker Desktop?

### What is Docker Desktop? {#h3-1-what-is-docker-desktop}

As per the official [documentation](https://www.docker.com/products/docker-desktop):
> Docker Desktop is an application for macOS and Windows machines for the building and sharing of containerized applications and microservices.

You can follow the official Docker Desktop [installation](http://https://hub.docker.com/editions/community/docker-ce-desktop-mac "installation") guide if you have not installed it already.

### How to Enable Kubernetes With Docker Desktop for Mac {#h3-2-how-to-enable-kubernetes-with-docker-desktop-for-mac}

* Go to Preferences in the Docker menu bar. Then click the Kubernetes tab. Click the checkbox to enable Kubernetes and switch the default orchestrator to Kubernetes. It will take some time to enable Kubernetes.![](/images/posts/2021/02/deploying-spring-boot-applications-on-kubernetes/Screenshot-2020-12-07-at-7.29.06-PM-700x219.png)
* Once it is enabled you will see something like below on your Docker menu bar.![](/images/posts/2021/02/deploying-spring-boot-applications-on-kubernetes/Screenshot-2020-12-06-at-9.45.07-PM.png)
* Next, You should also set the Kubernetes context to docker-desktop if you have multiple clusters or environments running locally.![](/images/posts/2021/02/deploying-spring-boot-applications-on-kubernetes/Screenshot-2020-12-07-at-5.41.16-PM.png)

As per official [documentation](https://docs.docker.com/docker-for-mac/kubernetes/) the mac Kubernetes integration provides the Kubernetes CLI command at `/usr/local/bin/kubectl`. This location may not be in your shell's `PATH` variable, so you may need to type the full path of the command or add it to the `PATH`.

`kubectl` is a Kubernetes CLI tool for running commands against your cluster. Know more about kubectl [here](https://kubernetes.io/docs/reference/kubectl/overview/). Now we can verify the setup using the below kubectl command. The output should be something similar to the below.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">kubectl get nodes
NAME             STATUS   ROLES    AGE   VERSION
docker-desktop   Ready    master   19d   v1.19.3</pre>

### How to Create a Pod {#h3-3-how-to-create-a-pod}

A Pod in Kubernetes is the smallest possible execution unit. It can have one more container in it. However, we will test the setup by creating a simple pod with a single container image on the local Kubernetes cluster by following the below steps. This container runs an Nginx image.

1. Create a pod using the below YAML file: 

<pre class="EnlighterJSRAW" data-enlighter-language="generic">apiVersion: v1
kind: Pod
metadata:
name: nginx
labels:
app: nginx
spec:
containers:
- name: mycontainer
image: docker.io/nginx
ports:
- containerPort: 80</pre>

   Use `kubectl create` and the name of the YAML file to create a pod:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">kubectl create -f pod.yaml
pod/nginx created</pre>

2. Check the status of the pod we just created: 

<pre class="EnlighterJSRAW" data-enlighter-language="generic">kubectl get pods
NAME    READY   STATUS    RESTARTS   AGE
nginx   1/1     Running   0          2m38s</pre>

3. To debug the pod further and it's working we can get a shell to a running container using `kubectl exec` command. Once inside the container, we will just use curl to verify Nginx setup. Let's do that now:  

<pre class="EnlighterJSRAW" data-enlighter-language="generic">kubectl exec -it nginx -- /bin/sh
# curl 10.1.0.65
&lt;!DOCTYPE html&gt;
&lt;html&gt;
&lt;head&gt;
&lt;title&gt;Welcome to nginx!&lt;/title&gt;
&lt;style&gt;
body {
width: 35em;
margin: 0 auto;
font-family: Tahoma, Verdana, Arial, sans-serif;
}
&lt;/style&gt;
&lt;/head&gt;
&lt;body&gt;
&lt;h1&gt;Welcome to nginx!&lt;/h1&gt;
&lt;p&gt;If you see this page, the nginx web server is successfully installed and
working. Further configuration is required.&lt;/p&gt;
&lt;p&gt;For online documentation and support please refer to
&lt;a href="http://nginx.org/"&gt;nginx.org&lt;/a&gt;.&lt;br/&gt;
Commercial support is available at
&lt;a href="http://nginx.com/"&gt;nginx.com&lt;/a&gt;.&lt;/p&gt;
&lt;p&gt;&lt;em&gt;Thank you for using nginx.&lt;/em&gt;&lt;/p&gt;
&lt;/body&gt;
&lt;/html&gt;</pre>

### How to Deploy a Working Spring Boot Application on Kubernetes {#h3-4-how-to-deploy-a-working-spring-boot-application-on-kubernetes}

Let's deploy a working Spring Boot application on our local Kubernetes cluster. We will be using the Indian-states application for the demo. You can check out [this](https://github.com/yrashish/indian-states) GitHub repository to know more about this application.

In my previous [article](https://ashishtechmill.com/cicd-workflow-for-spring-boot-application-on-kubernetes-via-skaffold), I have already explained the service and deployment resources for this Kubernetes application.

Now we will just clone this repository and create Kubernetes manifests using the below command:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">git clone https://github.com/yrashish/indian-states
cd indian-states/k8s
kubectl create -f myservice.yaml
service/states created
kubectl create -f mydeployment.yaml
deployment.apps/states created
kubectl get all
NAME                          READY   STATUS    RESTARTS   AGE
pod/states-6664b9dbf6-l2tgj   1/1     Running   0          8s
NAME                 TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)          AGE
service/kubernetes   ClusterIP   10.96.0.1      &lt;none&gt;        443/TCP          55s
service/states       NodePort    10.96.78.225   &lt;none&gt;        8080:31238/TCP   15s
NAME                     READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/states   1/1     1            1           8s
NAME                                DESIRED   CURRENT   READY   AGE
replicaset.apps/states-6664b9dbf6   1         1         1       8s</pre>

Now just do curl and access the /states REST endpoint and see what happens.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">curl localhost:31238/states
[{“name”:”Andra Pradesh”,”capital”:”Hyderabad”},{“name”:”Arunachal Pradesh”,”capital”:”Itangar”},{“name”:”Assam”,”capital”:”Dispur”},{“name”:”Bihar”,”capital”:”Patna”},{“name”:”Chhattisgarh”,”capital”:”Raipur”},{“name”:”Goa”,”capital”:”Panaji”},{“name”:”Gujarat”,”capital”:”Gandhinagar”},{“name”:”Haryana”,”capital”:”Chandigarh”},{“name”:”Himachal Pradesh”,”capital”:”Shimla”},{“name”:”Jharkhand”,”capital”:”Ranchi”},{“name”:”Karnataka”,”capital”:”Bangalore”},{“name”:”Kerala”,”capital”:”Thiruvananthapuram”},{“name”:”Madhya Pradesh”,”capital”:”Bhopal”},{“name”:”Maharashtra”,”capital”:”Mumbai”},{“name”:”Manipur”,”capital”:”Imphal”},{“name”:”Meghalaya”,”capital”:”Shillong”},{“name”:”Mizoram”,”capital”:”Aizawi”},{“name”:”Nagaland”,”capital”:”Kohima”},{“name”:”Orissa”,”capital”:”Bhubaneshwar”},{“name”:”Rajasthan”,”capital”:”Jaipur”},{“name”:”Sikkim”,”capital”:”Gangtok”},{“name”:”Tamil Nadu”,”capital”:”Chennai”},{“name”:”Telangana”,”capital”:”Hyderabad”},{“name”:”Tripura”,”capital”:”Agartala”},{“name”:”Uttaranchal”,”capital”:”Dehradun”},{“name”:”Uttar Pradesh”,”capital”:”Lucknow”},{“name”:”West Bengal”,”capital”:”Kolkata”},{“name”:”Punjab”,”capital”:”Chandigarh”}]
</pre>

### How to Monitor and Manage Kubernetes Cluster with Lens IDE {#h3-5-how-to-monitor-and-manage-kubernetes-cluster-with-lens-ide}

Lens IDE for Kubernetes is a desktop application for macOS, Windows, and Linux to manage your cluster. It's completely free and open source. You can download it from [here](https://k8slens.dev/). Developers, DevOps engineers, SRE, and also people who are learning Kubernetes on their laptops can use this to view and manage their clusters. Following are some of the highlighted features that it provides.

* Manage multiple clusters with a single IDE.
* 360-degree view of your cluster.
* Prometheus integration
* Context-aware built-in terminal
* Built-in support for managing Helm charts

After installing it you can first add a cluster. Lens connects to your Kubernetes cluster using the kubeconfig file which is typically located in your `$HOME/.kube` directory. You have the option to paste it as a text file also. Now select the kubeconfig file, context, and click on Add cluster.

![Screenshot 2021-02-11 at 2.09.54 AM.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1612989621325/x13sR5GgY.png)

After adding the cluster you can see it on the left side navigation bar. For demo purposes, I have redeployed the Spring Boot application. Under events, you can see that our application image is getting pulled.

![Screenshot 2021-02-11 at 12.49.32 AM.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1612985782109/D9-yjMS5D.png)

We can also view the logs of the deployment we just completed. As you can see that application is deployed and running.

![Screenshot 2021-02-11 at 1.05.33 AM.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1612985998835/mDUZ2ngCw.png)

You can scale, restart, edit and remove Kubernetes manifests from Lens IDE itself.

![Screenshot 2021-02-11 at 1.12.58 AM.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1612986254526/gr3QKnsAa.png)

Under networks, you can also view Kubernetes services.

![Screenshot 2021-02-11 at 1.31.12 AM.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1612987519015/8sizkbmJ4.png)

Lens has a feature where it allows you to install Prometheus stack in your cluster so that you can gather metrics about the cluster and its nodes. For installation, right-click on the cluster icon in the Lens UI's top-left corner and select Settings.

![Screenshot 2021-02-11 at 1.49.32 AM.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1612988900910/iGVa450rS.png)

On the Settings page, under features, you can see a Metrics section and a button to install Prometheus. Click Install to deploy the Prometheus stack to your cluster.

![Screenshot 2021-02-11 at 1.50.13 AM.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1612989251651/U5NjQtWkT.png)

Lens will start displaying metrics after a minute or so.

![Screenshot 2021-02-11 at 2.08.04 AM.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1612989549853/4GltJdQil.png)

We have only covered a handful of features that Lends IDE for Kubernetes provides. It's a wrap for now.

### Conclusion {#h3-6-conclusion}

In this article, we have learned how to enable Kubernetes on Docker Desktop for Mac, created a basic pod, deployed a Spring Boot application, and managed our single-node Kubernetes cluster with the help of Lens IDE.

What are you waiting for?

Go containerize and share your applications!

### Support me {#h3-7-support-me}

If you like what you just read, then you can buy me a coffee by clicking the link in the image below:  
[![Buy Me A Coffee](https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png)](https://www.buymeacoffee.com/meashish)

### Further Reading {#h3-8-further-reading}

You can continue reading some of my previous [articles](https://ashishtechmill.com).
