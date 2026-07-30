---
title: "Managing MongoDB Database Changes Using Liquibase Pro"
slug: "managing-mongodb-database-changes-using-liquibase-pro"
date: "2025-08-20T14:21:51+00:00"
lastmod: "2025-08-20T14:34:12+00:00"
description: "This article explores how Liquibase and MongoDB can be integrated with Git actions to implement source control for database changes. What is Liquibase? - by Ravindar Karampuri"
authors:
  - "ravindar-karampuri"
image: "/images/posts/2025/08/managing-mongodb-database-changes-using-liquibase-pro/mongologo.png"
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
enlighterjs: true
frozen: false
---

This article explores how Liquibase and MongoDB can be integrated with Git actions to implement source control for database changes.

What is Liquibase? {#h2-0-what-is-liquibase}
--------------------------------------------

Liquibase is an open-source tool designed to manage version control for database schema changes. It allows developers to simplify and automate the process of applying database updates across various database platforms.

By using database-independent formats like XML, YAML, or JSON, Liquibase enables teams to define changes in a consistent and structured way. In a Java project, these updates are organized into files containing *changesets*, which outline the specific instructions needed to modify or refactor the database schema.

What is MongoDB? {#h2-1-what-is-mongodb}
----------------------------------------

MongoDB is an open-source NoSQL database management system that provides an alternative to traditional relational databases. It is designed to store and manage document-oriented data, making it ideal for scenarios that require flexible, scalable, and high-performance data storage and retrieval.

Setting up a local MongoDB instance on Windows {#h2-2-setting-up-a-local-mongodb-instance-on-windows}
-----------------------------------------------------------------------------------------------------

To test the integration of tools like Liquibase with MongoDB, we need a MongoDB server running locally. Install MongoDB directly on a Windows machine.

### Use Liquibase with MongoDB {#h3-3-use-liquibase-with-mongodb}

[MongoDB](https://www.mongodb.com/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=rav-foojay&utm_term=tony.kim) is a document-oriented NoSQL database. For more information, see the[MongoDB Documentation](https://www.mongodb.com/docs/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=rav-foojay&utm_term=tony.kim).

#### Supported database versions

Liquibase supports MongoDB versions starting from 5.0 onwards: 5.x, 6.x, 7.x, 8.x.

Prerequisites {#h2-4-prerequisites}
-----------------------------------

1. Download the latest version of the [Liquibase MongoDB Pro Extension](https://mvnrepository.com/artifact/org.liquibase.ext/liquibase-commercial-mongodb).
2. [Ensure Java is installed](https://docs.liquibase.com/start/install/liquibase-requirements.html): Liquibase requires Java to run. If you used the Liquibase Installer, Java is included automatically. Otherwise, you must[install Java](https://www.java.com/en/download/) manually.
3. If you use Liquibase Pro, or a Liquibase Pro extension, confirm that you have a valid license key. For more information, see[How to Apply Your Liquibase Pro License Key](https://docs.liquibase.com/liquibase-pro/license-key.html).

### Install supporting drivers {#h3-5-install-supporting-drivers}

1. Download the following four [JAR files](https://mvnrepository.com/artifact/org.liquibase.ext):

* [MongoDB Java Driver Core](https://mvnrepository.com/artifact/org.mongodb/mongodb-driver-core) (mongodb-driver-core-\<version\>.jar)
* [MongoDB Synchronous Driver](https://mvnrepository.com/artifact/org.mongodb/mongodb-driver-sync) (mongodb-driver-sync-\<version\>.jar)
* [MongoDB BSON](https://mvnrepository.com/artifact/org.mongodb/bson) (bson-\<version\>.jar)
* [Liquibase MongoDB extension](https://github.com/liquibase/liquibase-mongodb/releases/) (liquibase-mongodb-\<version\>.jar)

2. Place the above four JAR files in the specified path C:\\Program Files\\liquibase\\lib:

![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXdHZO7Y0SQdS3eA5oISfMG9kpkWJbaziO9O4s6cB34yOit-MbJY5i5sYcwkyz4GYUAqZ-U_sYTLvqDShXiVwx4xQHGpazKkYG1CZHwe8X-QKVpiwjjvz7sp-3yv0l_FY2wfViPA?key=kYj68YH2LkWKvX_ueHC8rQ)list of jarfiles required for this project

### Connection configuration {#h3-6-connection-configuration}

1. Ensure MongoDB is installed and configured as expected on your machine.

2. Ensure Liquibase Pro license key is specified in a **liquibase.properties** file.

3. Define the database URL in the defaults file, i.e., connection string and credentials to connect successfully.

4. Install Mongosh to run mongoshell commands though Liquibase.

### Connection test {#h3-7-connection-test}

1. Create a text file called[changelog](https://docs.liquibase.com/concepts/changelogs/home.html) (.js, .yaml, .json, or .xml) in your project directory and add a[changeset](https://docs.liquibase.com/concepts/changelogs/changeset.html).
2. Create files as per requirement.
3. Set up the git actions for the changes.

*** ** * ** ***

Hands-on execution steps detailed {#h2-8-hands-on-execution-steps-detailed}
---------------------------------------------------------------------------

### **1. Create project and add files** {#h3-9-1-create-project-and-add-files}

The liquibase-mdb-demo project demonstrates how to automate MongoDB database changes using Liquibase, a popular database change management tool. Here's a detailed explanation of the project structure, the purpose of each folder and file, and how they are utilized:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">PS E:\temp&gt; mkdir liquibase-mdb-demo

   Directory: E:\temp

Mode                 LastWriteTime         Length Name
----                 -------------         ------ ----
d----          22-06-2025    07:24                liquibase-mdb-demo

PS E:\temp&gt; cd .\liquibase-mdb-demo\
PS E:\temp\liquibase-mdb-demo&gt; git init
Initialized empty Git repository in E:/temp/liquibase-mdb-demo/.git/
PS E:\temp\liquibase-mdb-demo&gt;</pre>

<br />

![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXdSDi6L2En8Oa8a3OlIRlT9ZdzXYA6woZwVhuPNpNwt-NSaWS4hsTcCm3j0qxuc4VpkvWXFfOWO4ouHaly5DstFEBVwnMWvIAxwc06JQ4dWLX41P1kPOIVJknGJ8AtZvUASrawfhA?key=kYj68YH2LkWKvX_ueHC8rQ)

Figure: Git project structure and its files

### **2. Project structure in VS Code** {#h3-10-2-project-structure-in-vs-code}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">liquibase.properties
README.md
Update-report-22-Jun-2025-081340.html
Update-report-22-Jun-2025-083530.html
changelog/
   changelog.json
mongoscripts/
   create_person_collection.js
   delete_person_data.js
   drop_person_collection.js
   insert_person_data.js</pre>

<br />

### **3. Lifecycle of Liquibase** {#h3-11-3-lifecycle-of-liquibase}

**3.1 Configuration:**liquibase.properties tells Liquibase where to find the changelog and how to connect to MongoDB.

**3.2 Changelog execution:**changelog.json lists the database changes to apply, referencing scripts in mongoscripts.

**3.3 Script execution:**When you run liquibase update, Liquibase reads the changelog and executes the specified MongoDB scripts (e.g., inserting data, creating collections).

**3.4 Rollback support:**Each change set can specify a rollback script (e.g., delete_person_data.js) to undo changes, if needed.

**3.5 Reporting:**Update reports are generated for auditing.

### **4. Verify the Liquibase version by using the inbuilt command** {#h3-12-4-verify-the-liquibase-version-by-using-the-inbuilt-command}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">PS E:\temp\liquibase-mdb-demo&gt; liquibase --version
####################################################
##   _     _             _ _                      ##
##  | |   (_)           (_) |                     ##
##  | |    _  __ _ _   _ _| |__   __ _ ___  ___   ##
##  | |   | |/ _` | | | | | '_ \ / _` / __|/ _ \  ##
##  | |___| | (_| | |_| | | |_) | (_| \__ \  __/  ##
##  \_____/_|\__, |\__,_|_|_.__/ \__,_|___/\___|  ##
##              | |                               ##
##              |_|                               ##
##                                                ##
##  Get documentation at docs.liquibase.com       ##
##  Get certified courses at learn.liquibase.com  ##
##                                                ##
####################################################
Liquibase Home: C:\Program Files\liquibase
Java Home C:\Program Files\liquibase\jre (Version 21.0.7)
Liquibase Pro Version: 4.32.0
To renew Liquibase Pro or Liquibase Labs please contact <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="a2d1c3cec7d1e2cecbd3d7cbc0c3d1c78cc1cdcf">[email&nbsp;protected]</a> or go to https://www.liquibase.com/pricing
=============================================================================
PS E:\temp\liquibase-mdb-demo&gt; liquibase checks run
Starting Liquibase Pro at 08:05:46 using Java 21.0.7 (version 4.32.0 #53 built at 2025-05-22 20:36:18 UTC)
Liquibase Pro Version: 4.32.0
To renew Liquibase Pro or Liquibase Labs please contact <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="7c0f1d10190f3c10150d09151e1d0f19521f1311">[email&nbsp;protected]</a> or go to https://www.liquibase.com/pricing
INFO: Checks executed against changes generated by MongoDB at mongodb://localhost:27019/testdb.
Executing Policy Checks against changelog/changelog.json</pre>

<br />

Once the Liquibase check command succeeds with no errors, we can run the execution by using the Liquibase update command.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">PS E:\temp\liquibase-mdb-demo&gt; liquibase update
####################################################
##   _     _             _ _                      ##
##  | |   (_)           (_) |                     ##
##  | |    _  __ _ _   _ _| |__   __ _ ___  ___   ##
##  | |   | |/ _` | | | | | '_ \ / _` / __|/ _ \  ##
##  | |___| | (_| | |_| | | |_) | (_| \__ \  __/  ##
##  \_____/_|\__, |\__,_|_|_.__/ \__,_|___/\___|  ##
##              | |                               ##
##              |_|                               ##
##                                                ##
##  Get documentation at docs.liquibase.com       ##
##  Get certified courses at learn.liquibase.com  ##
##                                                ##
####################################################
Starting Liquibase Pro at 08:35:26 using Java 21.0.7 (version 4.32.0 #53 built at 2025-05-22 20:36:18 UTC)
Liquibase Pro Version: 4.32.0
To renew Liquibase Pro or Liquibase Labs please contact <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="d5a6b4b9b0a695b9bca4a0bcb7b4a6b0fbb6bab8">[email&nbsp;protected]</a> or go to https://www.liquibase.com/pricing

UPDATE SUMMARY
Run:                          2
Previously run:               0
Filtered out:                 0
Failed deployment:            0
-------------------------------
Total change sets:            2

Liquibase: Update has been successful. Rows affected: 0
Pro Update Report created!
* File '/E:/temp/liquibase-mdb-demo/Update-report-22-Jun-2025-083530.html' was created.
** To suppress Update reports add command arg 'liquibase update --report-enabled=false'
** To suppress all Pro Reports set liquibase.reports.enabled=false, or LIQUIBASE_REPORTS_ENABLED=false
Liquibase command 'update' was executed successfully.
</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">PS E:\temp\liquibase-mdb-demo</pre>

After successful execution, you will be redirected to HTML report generation for the same execution process for detailed audit process.

This screenshot shows a **Liquibase Update Report** , confirming a successful database update. The report indicates that **two change sets** were executed with **no previous runs, no filters, and no deployment failures**, meaning all intended changes were applied cleanly. The message "Rows affected: 0" suggests schema-level updates (e.g., table creation or modification) rather than data changes. This ensures the changelog is working as expected and keeps the database schema version-controlled and consistent.
![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXeVuZv0c29tZDdyYL4dNeMZ1vgSsm6DokhJB76uM8hguICmDqseOUPi-QaXdKZKm8cy42gGmHrU2usnIjAXUL9bGlBOuQVilelJnYXjY34QI-Hpv06dEgTEpIhZZ0JvR5BSOdvyew?key=kYj68YH2LkWKvX_ueHC8rQ)

Figure: Post deployment HTML report generated regarding deployed change.

This Liquibase report shows that **two changesets** were successfully executed with **zero failures or skips** . The changeset with ID 2025062202 inserted multiple documents into a MongoDB collection named person, adding sample data. These changes were defined in a JSON changelog file (changelog.json) and executed by user *Ravindar*. It confirms that Liquibase is being used to version and track NoSQL schema/data changes reliably.
![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXevi6FN-4B5bR-LTWGjefrehLaoUnhblU-blEyp2rlcb-fkcc9EiQvZHb52sxn3tVa6KH2CipygGwV3F2Rzd3EydHDUHYdu3fcvBk8IN6zomvn45G6rDbO58-kbbrosGYIt1Y8HBA?key=kYj68YH2LkWKvX_ueHC8rQ)

Figure: HTML report with Changelog information

It creates the collection named as **person**and inserts data as provided in the file mongoscripts\\insert_person_data.js.

Log in to the MongoDB database and check the collection and data inside the collection for newly created ones.

This screenshot from **MongoDB Compass** shows the successful insertion of four documents into the person collection in the testdb database. The data was pushed using **Liquibase**, as verified by the earlier changeset execution report, demonstrating how Liquibase can manage and apply structured NoSQL data changes to MongoDB collections in a controlled, versioned manner.
![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXezzrF-a6Yfbnrw0mIh1hRoZOyEh_35iyroRND48O2wbsrzq62KqhBTOUyY5lRo97Hn-vXD4_6bM5sczubd1aXSFj9XroFManhkzfDrsWXc0zRCaLU1RmiMFMDxkxwTXcWCTbPesA?key=kYj68YH2LkWKvX_ueHC8rQ)

Figure: Post deployment, documents reflected in the Person collection of testdb database.

After the successful execution of Liquibase, update the documents present in the collection   Person.

If those database changes need to roll back and keep it in previous consistent state, we can make it through the command:

**liquibase rollbackCount 1**

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">PS E:\temp\liquibase-mdb-demo&gt; liquibase rollbackCount 1
####################################################
##   _     _             _ _                      ##
##  | |   (_)           (_) |                     ##
##  | |    _  __ _ _   _ _| |__   __ _ ___  ___   ##
##  | |   | |/ _` | | | | | '_ \ / _` / __|/ _ \  ##
##  | |___| | (_| | |_| | | |_) | (_| \__ \  __/  ##
##  \_____/_|\__, |\__,_|_|_.__/ \__,_|___/\___|  ##
##              | |                               ##
##              |_|                               ##
##                                                ##
####################################################
To renew Liquibase Pro or Liquibase Labs please contact <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="6013010c0513200c09111509020113054e030f0d">[email&nbsp;protected]</a> or go to https://www.liquibase.com/pricing
Rolling Back Changeset: changelog/changelog.json::2025062202::Ravindar
Pro Rollback Count Report created!
* File '/E:/temp/liquibase-mdb-demo/Rollback-count-report-22-Jun-2025-105836.html' was created.
** To suppress Rollback Count reports add command arg 'liquibase rollback-count --report-enabled=false'
** To suppress all Pro Reports set liquibase.reports.enabled=false, or LIQUIBASE_REPORTS_ENABLED=false
Liquibase command 'rollbackCount' was executed successfully.</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">PS E:\temp\liquibase-mdb-demo&gt;</pre>

This screenshot demonstrates a successful **Liquibase rollback** operation and its effect on the **MongoDB** **person** **collection**. It confirms that one changeset (2025062202) was rolled back successfully using the rollbackCount command. This changeset originally inserted multiple documents into the person collection.

![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXcHBlrf0iwb_08sugNXRXYFkOBN3AAn5kYbaQV4YKDkRY9W4PVsDsZ5Ujcwu2HZFNWGAeO1lJgFgIRKGOZcMfICTRBbLtfQKx3FUv2eAz2Hl32Rv-ta1PcO953cDJRpDmKUY-Fb8g?key=kYj68YH2LkWKvX_ueHC8rQ)Figure: HTML report after rollback changes completed

This screenshot from **MongoDB Compass** shows that the person collection is now empty, verifying that the rollback removed the inserted documents. This highlights how Liquibase can manage and reverse schema or data changes in MongoDB with precision and traceability.

The database changes reflect in Compass for the same as shown below:
![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXfoiUUy1S50hNIUGC05dgOEn0cWFCiRNPTnPZbNN2Bm-akFJcfyWfVsLF0vm8yhrmOF22Pf5jKgkGv8VY0pe64d-TeChkOwT_FO5pMPjjSX5gL_s_Trc_35ZgzN8FG_TIT1P_kqUA?key=kYj68YH2LkWKvX_ueHC8rQ)

Figure: Post rollback, collection is empty.

In Liquibase with MongoDB, two special collections are automatically created and managed by Liquibase to track and control database changes:

This table explains two important internal collections used by **Liquibase in MongoDB** to manage database change tracking and ensure safe deployments. The **databasechangelog** collection keeps a record of all applied changesets, including details like the id, author, filename, and dateExecuted, so Liquibase knows what has already been executed. The **databasechangeloglock** collection is used to **prevent concurrent updates** by managing a locking mechanism. It includes fields like locked, lockGranted, and lockedBy, ensuring that only one process can apply changes at a time, avoiding conflicts or duplicate executions.
![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXcOUJFtdcQjSY7m6B_S5OZMFTnbkfiGE_aV9GOUbWfvdmVsxWyUIDYfmurFa8zSDFsUryBqv9z2BwoDwRmO-17tyBerjUVncma4G5y5QJZ3W1dKrk4Enf04484GvHCjDOFZ9GI1Ww?key=kYj68YH2LkWKvX_ueHC8rQ)

Figure: Liquibase generated collections inside the MongoDB database.

**In short:**

* databasechangelog = "What changes have been applied?"
* databasechangeloglock = "Is someone already running Liquibase right now?"

![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXfg_G5VO7eKsxhOgrBV1hY6PsfJCvs8wptKh3qnWsoFTpgdl1OuTubRQrE0GXKJP138ldkVylVp5jJkPPIbEjtlqgY44gSzTPLuhC9Vw5kgQgVww9hiFDNfBXhVV0S7hTewwgokgw?key=kYj68YH2LkWKvX_ueHC8rQ)Figure: Databasechangelog collection documents![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXcXSEPB_M1PoAwHIF8KAds4wjFry4fnyhG89JF_pNQcDwFAE-lG1NWAVu6Ix6Mp5Eu9uSv6YAWoAV-uxQQE17lL6B2KOUETl8-HXjFdMVAB0_SFFKOXvrBONuuJ3WVfF-SCmffryA?key=kYj68YH2LkWKvX_ueHC8rQ)Figure: Databasechangeloglock collection document

### **5. Scripts used for this project** {#h3-13-5-scripts-used-for-this-project}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">mongoscripts\create_person_collection.js
db.createCollection('person');

mongoscripts\drop_person_collection.js
db.person.drop();

mongoscripts\insert_person_data.js
db.person.insertMany([
 { name: "Alice ", year: 2018, major: "History", address: { city: "NYC", street: "123 3rd St" } },
 { name: "White Rabbit", year: 2017, major: "English", address: { city: "Rabbit", street: "9 Down St" } },
 { name: "Mad Hatter", year: 2024, major: "Math", address: { city: "Hattery", street: "5th Ave" } },
 { name: "Dutches", year: 2034, major: "English", address: { city: "The Manor", street: "123 Down st" } }
]);

mongoscripts\delete_person_data.js
db.person.deleteOne({ name: "Alice " });
db.person.deleteOne({ name: "White Rabbit" });
db.person.deleteOne({ name: "Mad Hatter" });
db.person.deleteOne({ name: "Dutches" });

changelog.json
{
 "databaseChangeLog": [

    {
     "changeSet": {
       "id": "2025062201",
       "author": "RK",
 "labels": ["git-1000", "version 1.0"],
       "runWith": "mongosh",
       "changes": [
         {
           "mongoFile": {
             "dbms": "mongodb",
             "path": "../mongoscripts/create_person_collection.js",
             "relativeToChangelogFile": true
           }

         }
       ],
       "rollback": [
         {
           "mongoFile": {
             "dbms": "mongodb",
             "path": "../mongoscripts/drop_person_collection.js",
             "relativeToChangelogFile": true
           }

         }
       ]
     }
   },
    {
     "changeSet": {
       "id": "2025062202",
       "author": "RK",
 "labels": ["git-1001", "version 1.0"],
       "runWith": "mongosh",
       "changes": [
         {
           "mongoFile": {
             "dbms": "mongodb",
             "path": "../mongoscripts/insert_person_data.js",
             "relativeToChangelogFile": true
           }

         }
       ],
       "rollback": [
         {
           "mongoFile": {
             "dbms": "mongodb",
             "path": "../mongoscripts/delete_person_data.js",
             "relativeToChangelogFile": true
           }

         }
       ]
     }
   }

 ]
}</pre>

<br />

### **6. Liquibase commands** {#h3-14-6-liquibase-commands}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">liquibase status -verbose

liquibase checks run

liquibase update

liquibase history

liquibase rollbackCount 1

liquibase update - log-level=debug // Detailed log view

liquibase --help // To explore more on Liquibase commands</pre>

<br />

### **7. Create a git project info** {#h3-15-7-create-a-git-project-info}

1. git add .
2. git commit -m "Initial MongoDB automation setup with Liquibase Pro"
3. git remote add origin \<your-repo-url\>
4. git push -u origin main

**References:**

* [**Using Liquibase MongoDB Pro with MongoDB platforms**](https://docs.liquibase.com/start/tutorials/mongodb-pro/home.html)
* [**MongoDB Java Driver**](https://www.mongodb.com/docs/drivers/java/sync/current/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=rav-foojay&utm_term=tony.kim)
* [**Liquibase Pro vs Open Source**](https://cdn.prod.website-files.com/65c3a844357606c1b08a1e74/67ed23ac369e4789677b26a1_Liquibase%20Pro%20vs%20OSS.pdf)
