---
title: "Updating Data Files: Commits vs. Pull Requests?"
slug: "updating-data-files-commits-vs-pull-requests"
date: "2021-08-18T17:22:05+00:00"
lastmod: "2021-08-18T17:22:08+00:00"
description: "For once, I'm wondering a bit if this article can be helpful to somebody else since I believe my context is pretty specific!"
canonical: "https://blog.frankel.ch/data-files-commits-vs-pull-requests/"
authors:
  - "nicolas-frankel"
image: "chess-1438013-pxhere.com_.jpg"
categories:
  - "DevOps"
  - "Tools"
tags:
related_posts:
  - "whats-new-in-actions-setup-java-5-4-and-5-5-signature-verification-kona-jdk-and-a-better-maven-experience"
  - "lights-camera-action-github-actions-with-java-part-3"
  - "github-actions-with-java-part-2"
  - "github-actions-with-java-part-1"
enlighterjs: true
frozen: false
---

For once, I'm wondering a bit if this article can be helpful to somebody else since I believe my context is pretty specific. Anyway, just in case it might be the helpful case, here it is!

My [Jet Train](https://github.com/hazelcast/jet-train) project makes use of [GTFS](https://developers.google.com/transit/gtfs). GTFS stands for General Transit Feed Specification. It models public transportation schedules and their associated geographic information.

GTFS is based on two kinds of data, static data, and dynamic data. Static data may change but do so rarely, *e.g.*, transit agencies and bus stations. They are available as static files that you need to download now and then. Before, I had to download and overwrite them every time I run the demo.

As a developer, I'm lazy and wanted to automate this task. I used GitHub Actions for that:

```yaml
name: Refresh Dataset
on:
  schedule:
    - cron: '12 2 * * 1'                                                     # 1
jobs:
  build:
    name: Refresh Dataset
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v2                                            # 2
      - name: Fetch dataset archive
        run: curl -o archive.zip https://api.511.org/transit/datafeeds\?api_key\=${{secrets.FIVEONEONE_API_KEY}}\&operator_id\=RG  # 3
      - name: Extract archive
        run: unzip -o -d ./infrastructure/data/current/ archive.zip          # 4
      - name: Add & commit
        uses: stefanzweifel/git-auto-commit-action@v4                        # 5
        with:
          commit_message: Update to latest data files
          add_options: '-u'
```


1. Run the action weekly
2. Checkout the repository
3. Get the static data files archive
4. Extract files from the archive
5. Use the [git-auto-commit](https://github.com/marketplace/actions/git-auto-commit) action

It's not an issue to commit directly. Indeed, it's not code but data. The code should already have all built-in safeguards to prevent unexpected data from causing exceptions at runtime. I already had a couple of surprises previously and applied a lot of defensive programming techniques.

Yet, I was not happy with the above automation:

* Commits happen every week, regardless of whether I need to run the demo or not. It creates a lot of unnecessary commits. That's the reason I scheduled the action weekly and not more often.
* The action is scheduled on Mondays. If I run the demo on a Friday, I'll need to update the data files anyway.

Hence, I decided to switch to an alternative approach. Instead of committing, I updated the script to open a Pull Request. If I need to run the demo, I'll merge it (and pull locally); if not, it will stay open. If an opened PR already exists, the action will overwrite it. Now, I can schedule the action more frequently.

```yaml
name: Refresh Dataset
on:
  schedule:
    - cron: '12 2 * * *'                                                     # 1
jobs:
  build:
    name: Refresh Dataset
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v2                                            # 2
      - name: Fetch dataset archive
        run: curl -o archive.zip https://api.511.org/transit/datafeeds\?api_key\=${{secrets.FIVEONEONE_API_KEY}}\&operator_id\=RG  # 3
      - name: Extract files of interest from the archive
        run: unzip -o -j archive.zip agency.txt routes.txt stop_times.txt stops.txt trips.txt -d ./infrastructure/data/current  # 4
      - name: Remove archive
        run: rm archive.zip                                                  # 5
      - name: Create PR
        uses: peter-evans/create-pull-request@v3                             # 6
        with:
          commit-message: Update to latest data files
          branch: data/refresh
          delete-branch: true
          title: Refresh data files to latest version
          body: ""
```


1. Run the action *daily*
2. Checkout the repository
3. Get the static data files archive
4. Extract only required files from the archive
5. Remove the archive file for cleanup
6. Use the [create-pull-request](https://github.com/marketplace/actions/create-pull-request) action. The action creates a PR that automatically contains all new and updated files; that's the reason why I only extract some files and remove the archive.

As I mentioned in the introduction, I'm not sure this post can help many people. If it does, please don't hesitate to comment to let me know about your use case!

The complete source code for this post can be found on [Github](https://github.com/hazelcast/jet-train).

*Originally publish at [A Java Geek](https://blog.frankel.ch/data-files-commits-vs-pull-requests) on August 15^th^, 2021*
