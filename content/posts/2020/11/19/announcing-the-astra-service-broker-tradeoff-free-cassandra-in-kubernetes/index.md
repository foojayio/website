---
title: "Astra Service Broker: Tradeoff-Free Cassandra in Kubernetes"
slug: "announcing-the-astra-service-broker-tradeoff-free-cassandra-in-kubernetes"
date: "2020-11-19T19:52:00+00:00"
lastmod: "2021-11-10T08:50:01+00:00"
description: "DataStax Astra Service Broker: seamlessly integrate Cassandra into your Kubernetes deployments and leave the operations to somebody else!"
canonical: "https://www.datastax.com/blog/2020/11/announcing-astra-service-broker-tradeoff-free-cassandra-kubernetes"
authors:
  - "christopher-bradford"
image: "astra-service-broker.png"
categories:
  - "Apache Cassandra"
  - "Databases"
  - "DevOps"
  - "Kubernetes"
tags:
related_posts:
enlighterjs: true
frozen: false
---

When you are designing and building a cloud-native application, you are probably thinking about deploying it with Kubernetes. What about the database? That can get a bit more complicated as you weigh out the tradeoffs of elasticity, scale, and self-healing requirements versus maintaining servers and the long term operations required. Apache Cassandra™ ticks the first three boxes easily, but what about the operational burden of managing Cassandra? That's where the cloud-native Cassandra service, [DataStax Astra](https://www.datastax.com/products/datastax-astra), helps both operators and developers. But what makes it easy for deployments on Kubernetes to access the Astra managed service?

Today, we are releasing the DataStax Astra Service Broker, so you can seamlessly integrate Cassandra into your Kubernetes deployments and leave the operations to somebody else. In this article, we'll show you exactly how easy it is to use Astra with Kubernetes, and make you wonder why anyone would do anything else.

For those unfamiliar with DataStax Astra, it is a Cassandra as a Service platform that lets you use Cassandra without the operations overhead. From the web interface, you can fill out a few fields, click a button and in a short time you will have a fully functioning database ready to scale on demand. When building cloud-native applications, combining services that scale in a similar fashion reduces the amount of tradeoffs initially and the dreaded technical debt later. Astra is cloud agnostic and gives you full portability between one or more cloud platforms. Just like how Kubernetes allows you to run where and how you want, Astra will be right there as a highly reliable data layer.

When you are building cloud-native applications with Kubernetes, choices still have to be made. When spinning up an instance of your application how can you provision that data layer? How is the application made aware of the data connection's information including endpoints, security certificates, and credentials? The [Open Service Broker API](https://www.openservicebrokerapi.org/) and [Service Catalog](https://svc-cat.io/) operator for Kubernetes define an interface for provisioning and binding of services like DataStax Astra. For DataStax this means standing up a Service Broker for translating requests from the Open Service Broker API specification to our [Astra DevOps API](https://docs.astra.datastax.com/docs/manage-database-with-service-account). The Service Catalog handles monitoring the Kubernetes API for lifecycle requests and forwarding them to the Astra Service Broker. Once an instance is provisioned it may then be bound, at which point service information is retrieved and stored in a Kubernetes secret.

In a continuous delivery environment, placing these custom resources alongside your code makes it trivial to push them to Kubernetes with each deployment of your application. Let's take a look at how easy it is to integrate the Astra Service Broker.

You'll need a few prerequisites to follow this walkthrough

* A running Kubernetes cluster you can access via command line.
* [Helm](https://helm.sh/docs/intro/install/) package manager for Kubernetes.
* [Service Catalog](https://svc-cat.io/docs/install/#installing-the-service-catalog-cli) command line interface.
* A [DataStax Astra](http://astra.datastax.com/) account with [service account credentials](https://docs.astra.datastax.com/docs/creating-a-new-service-account-for-your-database) created.

First, start by ensuring the Service Catalog operator is installed in your local cluster. This only needs to be done once per Kubernetes cluster.

```
helm repo add svc-cat https://svc-catalog-charts.storage.googleapis.com
helm repo update
helm install catalog svc-cat/catalog --namespace catalog --create-namespace
```


Next, create a Kubernetes secret with the service account information from Astra. For this, you will need to go to the service account area of Astra and copy the credentials ([instructions](https://docs.astra.datastax.com/docs/creating-a-new-service-account-for-your-database)). What you get is a small snippet of JSON with all the important info needed to create the secret in Kubernetes. It requires a little Command-line Fu but rest-assured, you only have to do this once. You just need to replace the part labeled `<service_account_creds>`

```
kubectl create secret generic astra-creds --from-literal=username=unused --from-literal=password=`echo '<service_account_creds>'| base64`
```


You then register the broker via a ServiceBroker custom resource. For brevity we will leverage the helpful svcat command-line tool.

```
$ svcat register astra --url https://broker.astra.datastax.com/ --basic-secret astra-creds
```


With this information, Service Catalog automatically queries for available services on Astra and displays all the plans or service tiers.

```
$ svcat marketplace
  	CLASS      	PLANS                  	DESCRIPTION               	 
+----------------+-----------+------------------------------------------------+
  astra-database   A10     	DataStax Astra, built on the               	 
                           	best distribution of Apache                	 
                           	Cassandra™, provides the                   	 
                           	ability to develop and deploy              	 
                           	data-driven applications                   	 
                           	with a cloud-native service,               	 
                           	without the hassles of                     	 
                           	database and infrastructure                	 
                           	administration.                            	 
               	A20                                                    	                              	 
               	developer                       	

$ svcat get plans
	NAME  	NAMESPACE   	CLASS               	DESCRIPTION         	 
+-----------+-----------+----------------+------------------------------------+
  A10     	default 	astra-database   6 vCPU, 24GB DRAM, 20GB        	 
                                       	Storage                        	 
  A20     	default 	astra-database   12 vCPU, 48GB DRAM, 40GB       	 
                                       	Storage                        	         	 
  developer   default 	astra-database   Free tier: Try Astra with      	 
                                       	no obligation. Get 5 GB of     	 
                                       	storage, free forever.
```


*Note the information here is a small subset of what is available. Listings have been reduced for space.*

With this information you may now provision your database instance using svcat or kubectl:

```
$ svcat provision devdb --class astra-database --plan developer --params-json '{
  "cloud_provider": "GCP",
  "region": "us-east1",
  "capacity_units": 1,
  "keyspace": "sample_keyspace"
}'
```


You should see the following output:

```
  Name:    	devdb    
  Namespace:   default  
  Status:          	 
  Class:           	 
  Plan:            	 

Parameters:
  capacity_units: 1
  cloud_provider: GCP
  keyspace: sample_keyspace
  region: us-east1
```


For `kubectl` create a file called `astra.yaml` to describe the type of instance you need:

```
apiVersion: servicecatalog.k8s.io/v1beta1
kind: ServiceInstance
metadata:
  name: devdb
  namespace: default
spec:
  parameters:
    capacity_units: 1
    cloud_provider: GCP
    keyspace: petclinic
    region: us-east1
  serviceClassExternalName: astra-database
  servicePlanExternalName: developer

kubectl apply -f astra.yaml
```


Service catalog handles the provisioning and communication with Astra. After a couple minutes you can check the instance status with svcat and kubectl:

```
$ svcat get instances
  NAME	NAMESPACE   CLASS   PLAN   STATUS  
+-------+-----------+-------+------+--------+
  devdb   default                	Ready   
$ kubectl get serviceinstances devdb
NAME	CLASS                                           	PLAN                               	STATUS   AGE
devdb   ServiceClass/26b3fbe6-0c18-5140-8ac6-87d03b5b4148   1c9bb5ac-6609-5af5-a747-ecf1d093cc7f   Ready	3m20s
```


The process of retrieving service credentials is known as binding. Here's how you bind the devdb instance:

```
$ svcat bind devdb
  Name:    	devdb    
  Namespace:   default  
  Status:          	 
  Secret:  	devdb    
  Instance:	devdb    

Parameters:
  No parameters defined
```


With kubectl this may be described with a ServiceBinding resource, such as: `astra-service-binding.yaml`:

```
apiVersion: servicecatalog.k8s.io/v1beta1
kind: ServiceBinding
metadata:
  name: devdb
spec:
  externalID: 9a412237-1c66-4d43-b5e6-cd92d7b61779
  instanceRef:
	name: devdb
  secretName: devdb

kubectl apply -f astra-service-binding.yaml
```


After receiving this request Service Catalog handles retrieving the credentials from Astra and placing them within a local kubernetes secret at the same name as our binding. In this example, this is called `devdb`:

```
$ kubectl get secrets devdb -o yaml
apiVersion: v1
data:
  cql_port: 9042
  external_endpoint: ...astra.datastax.com
  encoded_external_bundle: BASE64_ENCODED_CONNECT_BUNDLE_ZIP
  internal_endpoint: ...internal.astra.datastax.com
  encoded_internal_bundle: BASE64_ENCODED_CONNECT_BUNDLE_ZIP
  keyspace: sample_keyspace
  local_datacenter: dc-1
  password: REDACTED
  port: 1337
  tls_ca_cert: PEM_ENCODED_CA_CERT
  tls_cert: PEM_ENCODED_APPLICATION_CERT
  tls_key: PEM_ENCODED_APPLICATION_KEY
  username: REDACTED
kind: Secret
metadata:
  name: devdb
type: Opaque
```


This is all of the information required to configure the Cassandra driver for secure connectivity to Astra. Instead of manually spinning up nodes, wiring up monitoring, and sourcing infrastructure, Apache Cassandra is available on-demand through a simple GitOps interface. If you need to update the cluster to increase capacity, it is a simple YAML change which is checked into your repository and deployed with CD tools. The only "hard work" here is a call to `kubectl apply`. With a running database head over to the [Spring Reactive Pet Clinic](https://github.com/spring-petclinic/spring-petclinic-reactive/) for a reference Java application which is configured to use the Secret returned by Astra Service Broker.

For more learning check out the [Kubernetes and Cassandra](https://www.datastax.com/dev/kubernetes) and [cloud-native data](https://www.datastax.com/cloud-native) pages on our developer site [datastax.com/dev](https://www.datastax.com/dev). If you are interested in receiving updates on our new Cassandra on Kubernetes certification program, or becoming a Cassandra certified developer or administrator please visit [https://datastax.com/dev/certifications](https://www.datastax.com/dev/certifications).
