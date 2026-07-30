---
title: "Backing up K8ssandra with MinIO"
slug: "backing-up-k8ssandra-with-minio"
date: "2021-11-30T17:00:00+00:00"
lastmod: "2021-12-03T14:30:09+00:00"
description: "K8ssandra includes Medusa for Apache Cassandra® to handle backup and restore for your Cassandra nodes. Recently Medusa was upgraded to introduce support - by Alexander Dejanovski"
canonical: "https://k8ssandra.io/blog/articles/backing-up-k8ssandra-with-minio/"
authors:
  - "alexander-dejanovski"
image: "https://foojay.io/wp-content/uploads/2021/07/minio-login.png"
categories:
  - "Apache Cassandra"
  - "Databases"
  - "DevOps"
tags:
related_posts:
enlighterjs: true
frozen: false
---

K8ssandra includes Medusa for Apache Cassandra® to handle backup and restore for your Cassandra nodes. Recently Medusa was upgraded to introduce support for all S3 compatible backends, including [MinIO](https://min.io/), the popular k8s-native object storage suite. Let's see how to set up K8ssandra and MinIO to backup Cassandra in just a few steps. If you don't want to self-manage Kubernetes and Cassandra, you can always use [Astra DB](https://dtsx.io/3nHk38t), where this is all set up for you.

Deploy MinIO {#h2-0-deploy-minio}
---------------------------------

Similar to K8ssandra, MinIO can be simply deployed through Helm.

First, add the MinIO repository to your local list:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">helm repo add minio https://helm.min.io/</pre>

The MinIO Helm charts allow you to do several things at once at install time:

* Set the credentials to access MinIO
* Create a bucket for your backups that can be set as default

You can create a `k8ssandra-medusa` bucket and use `minio_key/minio_secret` as the credentials, and deploy MinIO in a new namespace called `minio` by running the following command:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">helm install --set accessKey=minio_key,secretKey=minio_secret,defaultBucket.enabled=true,defaultBucket.name=k8ssandra-medusa minio minio/minio -n minio --create-namespace</pre>

**Note:** Creating the bucket is not mandatory at this stage and can be done through MinIO's UI.

After the `helm install` command has completed, you should see something similar to this in the `minio` namespace:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">% kubectl get all -n minio
NAME                        READY   STATUS    RESTARTS   AGE
pod/minio-5fd4dd687-gzr8j   1/1     Running   0          109s

NAME            TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)    AGE
service/minio   ClusterIP   10.96.144.61   &lt;none&gt;        9000/TCP   109s

NAME                    READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/minio   1/1     1            1           109s

NAME                              DESIRED   CURRENT   READY   AGE
replicaset.apps/minio-5fd4dd687   1         1         1       109s</pre>

Using port forwarding, you can expose access to the MinIO UI in the browser on port 9000:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">% kubectl port-forward service/minio 9000 -n minio
Forwarding from 127.0.0.1:9000 -&gt; 9000
Forwarding from [::1]:9000 -&gt; 9000</pre>

Now you can login to MinIO at [http://localhost:9000](http://localhost:9000/) using your install time defined credentials (if you used the same commands above they would be `minio_key` and `minio_secret`):
![](/images/posts/2021/11/backing-up-k8ssandra-with-minio/minio-login.png)

Once logged in, you can see that the `k8ssandra-medusa` bucket was created and is currently empty:
![](/images/posts/2021/11/backing-up-k8ssandra-with-minio/k8ssandra-medusa-bucket.png)

Deploy K8ssandra {#h2-1-deploy-k8ssandra}
-----------------------------------------

Now that MinIO is up and running, you can create a namespace for your K8ssandra installation and create a secret for Medusa to access the bucket. Create a `medusa_secret.yaml` file with the following content:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">apiVersion: v1
kind: Secret
metadata:
 name: medusa-bucket-key
type: Opaque
stringData:
 # Note that this currently has to be set to medusa_s3_credentials!
 medusa_s3_credentials: |-
   [default]
   aws_access_key_id = minio_key
   aws_secret_access_key = minio_secret
</pre>

Now create the `k8ssandra` namespace and the Medusa secret with the following commands:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">kubectl create namespace k8ssandra
kubectl apply -f medusa_secret.yaml -n k8ssandra</pre>

You should now see the `medusa-bucket-key` secret in the `k8ssandra` namespace:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">% kubectl get secrets -n k8ssandra
NAME                  TYPE                                  DATA   AGE
default-token-twk5w   kubernetes.io/service-account-token   3      4m49s
medusa-bucket-key     Opaque                                1      45s</pre>

You can then deploy K8ssandra with the following custom values file (all default values will be used if not customized here) :

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">medusa:
  enabled: true
  storage: s3_compatible
  storage_properties:
      host: minio.minio.svc.cluster.local
      port: 9000
      secure: "False"
  bucketName: k8ssandra-medusa
  storageSecret: medusa-bucket-key</pre>

Save the above file as `k8ssandra_medusa_minio.yaml` and then install K8ssandra with the following command:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">helm install k8ssandra k8ssandra/k8ssandra -f k8ssandra_medusa_minio.yaml -n k8ssandra</pre>

Now wait for the Cassandra cluster to be ready by using the following `wait` command:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">kubectl wait --for=condition=Ready cassandradatacenter/dc1 --timeout=900s -n k8ssandra</pre>

You should now see a list of pods similar to this:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">% kubectl get pods -n k8ssandra
NAME                                                  READY   STATUS      RESTARTS   AGE
k8ssandra-cass-operator-547845459-dwg68               1/1     Running     0          6m36s
k8ssandra-dc1-default-sts-0                           3/3     Running     0          5m56s
k8ssandra-dc1-stargate-776f88f945-p9twg               0/1     Running     0          6m36s
k8ssandra-grafana-75b9cb64cc-kndtc                    2/2     Running     0          6m36s
k8ssandra-kube-prometheus-operator-5bdd97c666-qz5vv   1/1     Running     0          6m36s
k8ssandra-medusa-operator-d766d5b66-wjt7j             1/1     Running     0          6m36s
k8ssandra-reaper-5f9bbfc989-j59xk                     1/1     Running     0          2m48s
k8ssandra-reaper-operator-858cd89bdd-7gfjj            1/1     Running     0          6m36s
k8ssandra-reaper-schema-4gshj                         0/1     Completed   0          3m3s
prometheus-k8ssandra-kube-prometheus-prometheus-0     2/2     Running     1          6m32s</pre>

Create some data and back it up {#h2-2-create-some-data-and-back-it-up}
-----------------------------------------------------------------------

Extract the username and password to access Cassandra (the password is different for each installation unless it is explicitly set at install time) into variables:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">% username=$(kubectl get secret k8ssandra-superuser -n k8ssandra -o jsonpath="{.data.username}" | base64 --decode)
% password=$(kubectl get secret k8ssandra-superuser -n k8ssandra -o jsonpath="{.data.password}" | base64 --decode)</pre>

Connect through CQLSH on one of the nodes:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">% kubectl exec -it k8ssandra-dc1-default-sts-0 -n k8ssandra -c cassandra -- cqlsh -u $username -p $password</pre>

Copy/paste the following statements into the CQLSH prompt and press enter:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">CREATE KEYSPACE medusa_test  WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
USE medusa_test;
CREATE TABLE users (email TEXT PRIMARY KEY, name TEXT, state TEXT);
INSERT INTO users (email, name, state) VALUES ('<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="bedfd2d7dddbfedbc6dfd3ced2db90ddd1d3">[email&nbsp;protected]</a>', 'Alice Smith', 'TX');
INSERT INTO users (email, name, state) VALUES ('<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="bbd9d4d9fbdec3dad6cbd7de95d8d4d6">[email&nbsp;protected]</a>', 'Bob Jones', 'VA');
INSERT INTO users (email, name, state) VALUES ('<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="432022312c2f03263b222e332f266d202c2e">[email&nbsp;protected]</a>', 'Carol Jackson', 'CA');
INSERT INTO users (email, name, state) VALUES ('<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="9efaffe8f7fadefbe6fff3eef2fbb0fdf1f3">[email&nbsp;protected]</a>', 'David Yang', 'NV');</pre>

Check that the rows were properly inserted:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">SELECT * FROM medusa_test.users;

 email             | name          | state
-------------------+---------------+-------
 <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="e6878a8f8583a6839e878b968a83c885898b">[email&nbsp;protected]</a> |   Alice Smith |    TX
   <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="6d0f020f2d08150c001d0108430e0200">[email&nbsp;protected]</a> |     Bob Jones |    VA
 <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="5b3f3a2d323f1b3e233a362b373e75383436">[email&nbsp;protected]</a> |    David Yang |    NV
 <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="2e4d4f5c41426e4b564f435e424b004d4143">[email&nbsp;protected]</a> | Carol Jackson |    CA

(4 rows)</pre>

Now backup this data, and check that files get created in your MinIO bucket.

To that end, use the following command:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">helm install my-backup k8ssandra/backup -n k8ssandra --set name=backup1,cassandraDatacenter.name=dc1</pre>

Since the backup operation is asynchronous, you can monitor its completion by running the following command:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">kubectl get cassandrabackup backup1 -n k8ssandra -o jsonpath={.status.finishTime}
</pre>

As long as this doesn't output a date and time, then the backup is still running. With the amount of data present and the fact that you're using a locally accessible backend, this should complete quickly.

Now refresh the MinIO UI and you should see some files in the `k8ssandra-medusa` bucket:
![](/images/posts/2021/11/backing-up-k8ssandra-with-minio/k8ssandra-medusa-backup.png)

An index folder should appear (it is Medusa's backup index) and then another folder that is specific to each Cassandra node in the cluster (in this case there is only one node).

Deleting the data and restoring the backup {#h2-3-deleting-the-data-and-restoring-the-backup}
---------------------------------------------------------------------------------------------

`TRUNCATE` the table and verify it is empty:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">% kubectl exec -it k8ssandra-dc1-default-sts-0 -n k8ssandra -c cassandra -- cqlsh -u $username -p $password

TRUNCATE medusa_test.users;

SELECT * FROM medusa_test.users;

 email | name | state
-------+------+-------

(0 rows)</pre>

Now restore the backup taken previously:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">helm install restore-test k8ssandra/restore --set name=restore-backup1,backup.name=backup1,cassandraDatacenter.name=dc1 -n k8ssandra
</pre>

This operation will take a little longer as it requires to stop the StatefulSet pod and perform the restore as part of the init containers, before the Cassandra container can start. You can monitor progress using this command:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">watch -d kubectl get cassandrarestore restore-backup1 -o jsonpath={.status} -n k8ssandra
</pre>

The restore operation is fully completed once the `finishTime` value appears in the output:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{"finishTime":"2021-03-23T13:58:36Z","restoreKey":"83977399-44dd-4752-b4c4-407273f0339e","startTime":"2021-03-23T13:55:35Z"}</pre>

Check that you can read the data from the previously truncated table:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">% kubectl exec -it k8ssandra-dc1-default-sts-0 -n k8ssandra -c cassandra -- cqlsh -u k8ssandra-superuser -p XHsZ943WBg5RPNhVAT8x -e "SELECT * FROM medusa_test.users"

 email             | name          | state
-------------------+---------------+-------
 <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="7514191c161035100d14180519105b161a18">[email&nbsp;protected]</a> |   Alice Smith |    TX
   <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="ec8e838eac89948d819c8089c28f8381">[email&nbsp;protected]</a> |     Bob Jones |    VA
 <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="c8aca9bea1ac88adb0a9a5b8a4ade6aba7a5">[email&nbsp;protected]</a> |    David Yang |    NV
 <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="dfbcbeadb0b39fbaa7beb2afb3baf1bcb0b2">[email&nbsp;protected]</a> | Carol Jackson |    CA

(4 rows)</pre>

You've successfully restored your lost data in just a few commands!

Many backends available {#h2-4-many-backends-available}
-------------------------------------------------------

MinIO, while being an obvious choice in the Kubernetes world, is not the only S3 compatible backend that K8ssandra can use. K8ssandra has supported AWS S3 and Google Cloud Storage as Medusa backends since 1.0.0. There are also a wide variety of solutions that can run on-prem (including CEPH, Cloudian, Riak S2, and Dell EMC ECS) or in cloud environments (including IBM Cloud Object Storage, and OVHcloud Object Storage). See the [K8ssandra backup/restore documentation](https://docs.k8ssandra.io/tasks/backup-restore/) for more detailed instructions and if you have questions, we love to help!
