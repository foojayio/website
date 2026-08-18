---
title: "Introducing the Foojay.io Calendar API"
date: "2023-06-22T06:40:22+00:00"
lastmod: "2023-07-25T14:22:55+00:00"
description: "We are excited to share that Foojay is growing and offering additional resources to bring the OpenJDK community together on a global scale."
authors:
  - "slava_yelk"
image: "calendar_preview.jpeg"
categories:
  - "Foojay"
  - "Tutorials"
related_posts:
  - "foojay-podcast-25"
  - "foojay-podcast-24"
  - "foojay-podcast-23"
  - "api-versioning"
frozen: false
---

We are excited to share that Foojay is growing and offering additional resources to bring the Java and OpenJDK community together on a global scale.

Several months ago, we launched the [Foojay Calendar](https://foojay.io/calendar), an interactive platform that allows individuals to propose Java-related and Kotlin-related and any other OpenJDK-related events and add them to the calendar.

All Foojay members are welcome to submit their events. Once reviewed, the event will be included in the calendar.
![](calendar_preview-1024x535.jpeg)

Although many users loved the idea, several organizations that provide their own event platforms reached out to us and asked if they could integrate their own events database into our calendar directly.

During our latest collaborations, we integrated events from [JUG Switzerland](https://www.jug.ch/), thanks to **Patrick Baumgartner** , and [Adoptium](https://adoptium.net/) with the Eclipse Foundation, thanks to **Carmen Delgado**.

We have expanded our API capabilities and now **allow anyone to send their own events**.

Our hope is that this change will keep the OpenJDK community engaged and informed about relevant events happening worldwide.

## How to add your events

We welcome inquiries from organizations interested in connecting with us.

You can reach us at [hello@foojay.io](mailto:hello@foojay.io) or via [Foojay Slack](https://foojay.slack.com/join/shared_invite/zt-tgefdcxv-SDwnqUqPH8peWujGNvC1ZQ#/shared-invite/email).

If you have events to share but lack development capabilities, our team is happy to assist with integration setup.  

### 1. Registering as an External Source and Getting an API Key

Other organizations that want to use our API directly and send events via an endpoint can follow the instructions below.

{{< img src="image-1.png" class="size-full is-resized" width="414" height="360" >}}

1. To get started, you'll need to retrieve your source and generate a unique API key. Please note that only **Contributors** and **Admins** currently have permission to complete this step. If you don't fall into either of these categories, please reach out to us so we can grant you the necessary permissions.
2. To obtain an **API key** and register your source, kindly send a request to [hello@foojay.io](mailto:hello@foojay.io) or via [Foojay Slack](https://foojay.slack.com/join/shared_invite/zt-tgefdcxv-SDwnqUqPH8peWujGNvC1ZQ#/shared-invite/email) with the subject line "API key". Our Foojay calendar administrator will then generate your API key and add you to our system as a trusted vendor.
3. Provide additional information about your organization, including its name, description, and details about the types of events it will host.
4. Once the Foojay Calendar admin generates your API key, you can use it to access the Foojay Calendar API.

### 2. Working with an API endpoint

Now you can work with our endpoint.

**Endpoint:** [`https://foojay.io/wp-json/foojay/v2/calendar/`](https://foojay.io/wp-json/foojay/v2/calendar/)

**Method:** `POST`

The endpoint is used to create new events on the Foojay Calendar. Any newly added events will be saved as drafts and will be published only after approval by the website administrator.

To send data to the endpoint, use the **FormData** format.

Below is an example of adding a new event in Postman:
![](image6-1024x470.png)

* Mandatory fields are marked with an asterisk (\*).
* The 'source' field is used along with a custom HTTP header to restrict the external sources from which events can be added.

To add your API key, use the "calendar-key" HTTP header. Make sure you have already generated an API key and source name, which should have been provided by the Foojay admin.
![](image4-1024x289.png)  

### 3. Responses

Once the event has been successfully added, you'll receive a **201 code and event ID**:
![](image3-1024x479.png)

If there is an issue, you will receive a response with a **4xx code**:
![](image2-1024x480.png)

## Example on the Frontend

Below is a description of the API fields and their corresponding elements on the front-end.
![](image7-1024x353.png)

## Example Based on an Existing Event

The following is a real imported event provided by <https://adoptium.net/:>  

{{< img src="image1-341x510.png" class="size-medium" width="341" height="510" >}}

![](image6-1-1024x470.png)

*If you encounter any problems or have any questions, please reach out to us at [hello@foojay.io](mailto:hello@foojay.io) or on the [Foojay Slack](https://foojay.slack.com/join/shared_invite/zt-tgefdcxv-SDwnqUqPH8peWujGNvC1ZQ#/shared-invite/email).*
