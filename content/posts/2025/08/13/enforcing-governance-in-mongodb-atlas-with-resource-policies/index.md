---
title: "Enforcing Governance in MongoDB Atlas with Resource Policies"
date: "2025-08-13T12:52:05+00:00"
lastmod: "2025-08-13T12:58:29+00:00"
description: "The growing adoption of MongoDB Atlas as a managed database platform increasingly demands mechanisms that align security, compliance, and governance."
authors:
  - "samuel-molling"
image: "mongologo.png"
categories:
  - "Databases"
  - "Java"
  - "Mongo"
tags:
related_posts:
  - "how-to-identify-the-underlying-causes-of-connection-timeout-errors-for-mongodb-with-java"
  - "java-on-azure-tooling-update-september-2022"
  - "java-virtual-threads-in-action-optimizing-mongodb-operation"
  - "run-an-atlas-cluster-locally-in-minutes"
frozen: false
---

The growing adoption of [MongoDB Atlas](https://www.mongodb.com/lp/cloud/atlas/try4-reg/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=samuel-foojay&utm_term=tony.kim) as a managed database platform increasingly demands mechanisms that align security, compliance, and governance. In April 2025, MongoDB introduced a critical feature for this purpose: [**Resource Policies**](https://www.mongodb.com/en-us/docs/atlas/atlas-resource-policies/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=samuel-foojay&utm_term=tony.kim).

This article presents the concept and capabilities of Resource Policies, highlights their importance in enterprise environments, and demonstrates how to use them effectively via Terraform --- ensuring that essential configurations rely not only on best practices but also on enforceable and auditable constraints.

## **What Are Resource Policies?**

**Resource Policies** are organization-level rules in Atlas that are automatically enforced across all projects and clusters under that organization. Their goal is straightforward yet powerful: to restrict specific actions or configurations that could compromise security, increase costs, or violate compliance standards.

These policies are defined using the AWS open-source [Cedar](https://docs.cedarpolicy.com/) policy language, adopted by MongoDB for its concise and expressive syntax. Its declarative structure allows for clear and precise specification of forbidden actions based on the resource context --- including cluster configuration, networking parameters, associated projects, and security requirements such as minimum TLS versions.

## **Why Use Them?**

Throughout the lifecycle of a MongoDB Atlas environment, it's common for different teams (developers, SREs, architects) to interact with the infrastructure. While this flexibility is desirable, it can introduce risk if well-defined *guardrails* are not in place.

**Resource Policies** help address this by:

* Enforcing minimum technical standards, such as TLS version or instance size.
* Preventing insecure practices, like exposing a cluster to the public IP 0.0.0.0/0.
* Avoiding inconsistencies across environments, such as usage of unauthorized regions or unbounded autoscaling.
* Supporting audits and regulatory frameworks with versioned, enforceable, and trackable rules.

In short, these policies elevate governance and operational control across any organization running MongoDB Atlas at scale.

## **Available Capabilities**

Among the constraints currently supported by **Resource Policies**, the following stand out:

* **Cloud provider restrictions**: allow or block cluster creation on AWS, GCP, or Azure
* **Region limitations**: restrict usage to specific geographic zones
* **Wildcard IP bans**: block 0.0.0.0/0 to prevent exposure to the internet
* **VPC peering or private endpoint enforcement**
* **Cluster size limits**: prevent instance sizes below M30 or above M60
* **Maintenance window requirements** at the project level
* **TLS-related rules**: enforce minimum TLS version or cipher suite configurations

These rules can be combined and fine-tuned, enabling targeted policies for critical clusters, production environments, or specific business units.

## **Applying Resource Policies with Terraform**

Support for **Resource Policies** is available in the [MongoDB Atlas Terraform Provider](https://registry.terraform.io/providers/mongodb/mongodbatlas/latest/docs/resources/resource_policy). This allows teams to declare policies as code, apply them through CI/CD pipelines, and enforce configuration compliance continuously and audibly.

Here are three practical examples of commonly used policies:

### **🔐 Example 1: Allow clusters only on AWS**

This policy blocks any cluster modifications outside AWS by using the unless clause. It's useful for organizations standardizing on a single cloud provider.

```
resource "mongodbatlas_resource_policy" "only_aws_clusters" {
  org_id = var.atlas_org_id

  name = "Allow Only AWS Clusters"

  policies = [
    {
      body = <<EOT
forbid(principal, action == ResourcePolicy::Action::"cluster.modify", resource)
unless { context.cluster.cloudProviders == [ResourcePolicy::CloudProvider::"aws"] };
EOT
    }
  ]
}
```

### **🌐 Example 2: Block public IPs (0.0.0.0/0)**

This policy denies any modification to the access list that includes a wildcard public IP. It's critical to improve access control and avoid accidental exposure.

```
resource "mongodbatlas_resource_policy" "block_public_ip" {
  org_id = var.atlas_org_id

  name = "Restrict Wildcard IP"

  policies = [
    {
      body = <<EOT
forbid(principal, action == ResourcePolicy::Action::"project.ipAccessList.modify", resource)
when { context.project.ipAccessList.contains(ip("0.0.0.0/0")) };
EOT
    }
  ]
}
```

### **🔒 Example 3: Enforce TLS 1.2 or higher**

This policy ensures that new or modified clusters support at least TLS 1.2, aligning with modern security standards.

```
resource "mongodbatlas_resource_policy" "require_tls_12" {
  org_id = var.atlas_org_id

  name = "Enforce Minimum TLS 1.2"

  policies = [
    {
      body = <<EOT
forbid(principal, action == ResourcePolicy::Action::"cluster.modify", resource)
unless { context.cluster.minTLSVersion == ResourcePolicy::TLSVersion::"tls1_2" };
EOT
    }
  ]
}
```

These policies can be organized in multiple .tf files and integrated into CI pipelines. By versioning and enforcing them through automation, teams can guarantee that all cluster configurations follow approved security and architecture standards.

## **Validation and Testing**

Once Resource Policies are applied via Terraform, it's possible to validate their enforcement using the Atlas UI or the administration API.

Below is an example of a Terraform plan applying several policies to the organization:
![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXeNb5LNgWiKsljTH10PIxaBJqNFg2eFq7m7LeRetHRfVW989P2iJRIuyXS5J9BQ1m7gthdAMoq73t_OxaIb8md6IRdT5yNZpwaYA6FqOB2Rj-EDx6rYSeOqfq9cpsQQBkQM53O1bw?key=FOpKAlLwLhYVlHz_DCq4KA)

Resource Policies plan

Once applied, the policies become visible in the MongoDB Atlas UI:
![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXd3-33_1s0eUd8ZiqnKDJIlw7m23iGaylNHo3LJRF7OaAPdalIVeVnhsbxNwHMXNknrF8cZxfQQS4efyQe-Anuab0MIFqGatv1cl7ULqLBifFKowSg2PrMFy0Dax-QVBfAjkEH-Qw?key=FOpKAlLwLhYVlHz_DCq4KA)

Resource Policies in Atlas UI

To detect **non-compliant resources**, MongoDB Atlas provides the following API endpoint:

```
curl --request GET \
  --user "<PUBLIC-KEY>:<PRIVATE-KEY>" \
  --digest \
  --header "Accept: application/vnd.atlas.2024-08-05+json" \
  "https://cloud.mongodb.com/api/atlas/v2/orgs/<ORG_ID>/nonCompliantResources?pretty=true"
```

This endpoint returns a list of projects, clusters, or networks that violate current policies.

*⚠️ Important: MongoDB Atlas does not automatically fix or block existing resources. If a resource is out of compliance, it will remain unchanged --- the system only reports the violation, enabling visibility without disruption.*

In the example below, a public IP was already configured in the project. After applying the policy that blocks 0.0.0.0/0, the resource was not removed, but it was flagged as non-compliant:
![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXeU1lUV71qq91DLAL39ZWmISnJR0xV7DClHUNbT-mnuKPLog1cbHEQpT79-UQnoS_V7oJxyhvqnNlGGhb8rUvteHC6mq48-rQoOr7VQngPk2y3Pr6ow-N_14XjF44wAbg4FL5Gx?key=FOpKAlLwLhYVlHz_DCq4KA)

Network Access List in Atlas UI
![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXdujOcrNYgocscIEtbLBPYjwE6VJ7Y4Ieb9Do_OBos74927nNq8djW64_Imo2HdyRAGdI34rJ7qeCfkWJmVbvPgEIbAdRU-KEk5ERrynohTDCs3qteJjD75sNmpGxNQYVJmphGl9A?key=FOpKAlLwLhYVlHz_DCq4KA)

Non-compliant resources in Atlas

Below are some visual examples of tests performed:

### **🚫 Attempt to create a cluster on Google Cloud Platform (GCP)**

The policy allowed only AWS clusters. The operation was blocked as expected:
![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXcq2s-go-k5tgvDUQ6HPSTLjtUNUEzQhYS0CGN_5fVErasvVtVEoFxyyBNZRJbKbYOo8_dHuux8qAifvsQtfqd-S03PVXPPqC0L95O3IkZBiyz9lSYVhiZUXV8V4r0fhjJQoxPbqA?key=FOpKAlLwLhYVlHz_DCq4KA)

Cluster creation blocked by policy

### **🔒 Attempt to add a public IP (0.0.0.0/0)**

The policy prevented the addition of a wildcard IP to the access list:
![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXcPPLQVyrfCrA2YBUDLgj_MDv7hQa1gpTpEvub9UTJf6-eBptrjoZaFKu4GHo2oUuwi1gnyXBzH516lr17-4HyqqUmlsOdL5Nu_AJ1yqlp4TRl2mb0AqLYoA88Q1clCfJ91fZSr?key=FOpKAlLwLhYVlHz_DCq4KA)

Blocked: 0.0.0.0/0 not allowed

### **Final Thoughts**

MongoDB Atlas Resource Policies represent a significant advancement for organizations looking to strengthen their security and governance posture. By combining declarative control, policy-as-code, and Terraform integration, these policies become a foundational component of any cloud data strategy.

Instead of relying solely on manual processes or human reviews, organizations can automate guardrails that prevent insecure practices, enforce compliance, and promote consistency across environments.

For teams using MongoDB Atlas in critical or regulated environments, gradual adoption is recommended --- starting with development environments and expanding as policies mature.
