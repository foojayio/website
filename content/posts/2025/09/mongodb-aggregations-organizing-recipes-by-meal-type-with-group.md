---
title: "MongoDB Aggregations: Organizing Recipes by Meal Type with $group"
slug: "mongodb-aggregations-organizing-recipes-by-meal-type-with-group"
date: "2025-09-04T05:31:22+00:00"
lastmod: "2025-09-04T05:31:24+00:00"
description: "In this post, we’ll walk through how to use `$group` to categorize recipes by type and explore grouping by tags to uncover popular recipe categories."
authors:
  - "justin-jenkins"
image: "/images/posts/2025/09/mongodb-aggregations-organizing-recipes-by-meal-type-with-group/mongologo.png"
categories:
  - "Databases"
  - "Mongo"
tags:
related_posts:
  - "building-a-spring-boot-crud-application-using-mongodbs-relational-migrator"
  - "data-modeling-for-java-developers-structuring-with-postgresql-and-mongodb"
  - "mongodb-acid-transactions-with-java"
  - "mongodb-aggregation-framework-a-beginners-guide"
enlighterjs: true
frozen: false
---

In this series, we're exploring different [MongoDB aggregation operators](https://www.mongodb.com/docs/manual/aggregation/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=recipe-foojay&utm_term=tony.kim) by applying them to a recipe collection. I hope you'll follow along with each post!

MongoDB Aggregations Series

* [MongoDB Aggregations: Finding Cooking Times with $min and $max](https://learnmongo.com/mongodb-aggregations-finding-cooking-times-with-min-and-max/)
* MongoDB Aggregations: Organizing Recipes by Meal Type with $group

As part of this series, we are imagining you're building a recipe website. For this post, we are adding some new features to our site, including allowing users to browse recipes by meal type---breakfast, lunch, dinner, etc.

MongoDB's [$group](https://www.mongodb.com/docs/manual/reference/operator/aggregation/group/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=recipe-foojay&utm_term=tony.kim) operator is ideal for this, as it lets you categorize recipes based on a specific field. This is useful for developing features like meal plans or quick filters that help users find just the recipes they need.

In this post, we'll walk through how to use \`$group\` to categorize recipes by type and explore grouping by tags to uncover popular recipe categories.

Why Use $group for Organizing Recipes? {#h2-0-why-use-group-for-organizing-recipes}
-----------------------------------------------------------------------------------

Let's consider some possible [user stories](https://www.atlassian.com/agile/project-management/user-stories):

**"As a User, I want to filter recipes by meal type to quickly find breakfast options."**

For users seeking specific meal ideas (e.g., breakfast, dinner), meal type filters make browsing much easier.

**"As a Planner, I want to categorize recipes by type to create weekly meal plans."**

Meal plans become simpler when recipes are organized by type, making it easy to plan out balanced meals.

**"As a Content Creator, I want to know the distribution of recipe types to ensure variety on our site."**

Internal users can use these aggregations to analyze recipe distributions and maintain a good balance of different meal types.

By using MongoDB's \`$group\` operator, you can achieve all this directly within your aggregation pipeline.

Setting Up the Data {#h2-1-setting-up-the-data}
-----------------------------------------------

Here's a sample document in our recipe collection:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{

&nbsp;&nbsp;"_id": {

&nbsp;&nbsp;&nbsp;&nbsp;"$oid": "636aa9817dd21c28fda493a4"

&nbsp;&nbsp;},

&nbsp;&nbsp;"title": "Eggs Benedict",

&nbsp;&nbsp;"calories_per_serving": 400,

&nbsp;&nbsp;"prep_time": 4,

&nbsp;&nbsp;"cook_time": 6,

&nbsp;&nbsp;"ingredients": [

&nbsp;&nbsp;&nbsp;&nbsp;{

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"name": "eggs",

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"quantity": {

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"amount": 6

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"vegetarian": true

&nbsp;&nbsp;&nbsp;&nbsp;},

&nbsp;&nbsp;&nbsp;&nbsp;{

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"name": "Virginia ham",

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"quantity": {

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"amount": 6,

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"unit": "rounds"

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"vegetarian": false

&nbsp;&nbsp;&nbsp;&nbsp;},

&nbsp;&nbsp;&nbsp;&nbsp;{

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"name": "English muffins",

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"quantity": {

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"amount": 3

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"vegetarian": true

&nbsp;&nbsp;&nbsp;&nbsp;},

&nbsp;&nbsp;&nbsp;&nbsp;{

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"name": "Hollandaise sauce",

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"quantity": {

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"amount": 3,

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"unit": "oz"

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"vegetarian": true

&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;],

&nbsp;&nbsp;"directions": [

&nbsp;&nbsp;&nbsp;&nbsp;"Cut the ham in rounds, split, toast and butter the english muffins, to fit the muffins.",

&nbsp;&nbsp;&nbsp;&nbsp;"Poach the eggs and place them on the ham and pour over the hollandaise sauce."

&nbsp;&nbsp;],

&nbsp;&nbsp;"rating": [5,3,5],

&nbsp;&nbsp;"rating_avg": 4.35,

&nbsp;&nbsp;"servings": 3,

&nbsp;&nbsp;"tags": [

&nbsp;&nbsp;&nbsp;&nbsp;"ham"

&nbsp;&nbsp;],

&nbsp;&nbsp;"type": "Breakfast",

&nbsp;&nbsp;"vegetarian_option": false

}</pre>

Each recipe has a \`type\` field representing the meal type, such as "Dinner," "Lunch," or "Breakfast," and a tags field representing general recipe attributes (e.g., "vegetarian," "quick," or "soup").

We'll start by grouping recipes by \`type\` and later explore how to group them by tags.

Using $group to Organize Recipes by Type {#h2-2-using-group-to-organize-recipes-by-type}
----------------------------------------------------------------------------------------

To organize recipes, we'll set up an aggregation pipeline that groups documents by the type field.

### Step 1: Grouping Recipes by Type {#h3-3-step-1-grouping-recipes-by-type}

Here's how we can group all recipes by their type:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&gt; db.recipes.aggregate([{ $group: { _id: "$type", count: { $sum: 1 } } }]);

_id: "$type"</pre>

We're grouping by the type field, so each unique type (e.g., "Dinner") will have its own group.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">count: { $sum: 1 }</pre>

For each group, we add a count field that totals the number of recipes in that group.

Output example:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">[

&nbsp;&nbsp;{ _id: "Dinner", count: 45 },

&nbsp;&nbsp;{ _id: "Breakfast", count: 20 },

&nbsp;&nbsp;{ _id: "Lunch", count: 30 },

]</pre>

This output shows the count of recipes for each type. Now, we can see how many "Dinner," "Breakfast," and "Lunch" recipes are available in the collection.

### Step 2: Including Recipe Details in Each Group {#h3-4-step-2-including-recipe-details-in-each-group}

If you want to store additional details (such as an array of recipe titles for each meal type), use \`[$push](https://www.mongodb.com/docs/manual/reference/operator/update/push/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=recipe-foojay&utm_term=tony.kim)\` to add the titles to each group:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&gt; db.recipes.aggregate([

&nbsp;&nbsp;{ $group: { _id: "$type", recipeTitles: { $push: "$title" } } },

])

recipeTitles: { $push: "$title" }</pre>

Adds an array of recipe titles to each meal type group.

Output example:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">[
&nbsp;&nbsp;{

&nbsp;&nbsp;&nbsp;&nbsp;_id: "Dinner",

&nbsp;&nbsp;&nbsp;&nbsp;recipeTitles: ["Split Pea Soup", "Spaghetti Bolognese", "Grilled Salmon"],

&nbsp;&nbsp;},

&nbsp;&nbsp;{ _id: "Breakfast", recipeTitles: ["Pancakes", "Omelette", "Smoothie Bowl"] },
]</pre>

This output shows each meal type along with its recipe titles, which can make displaying the list in a frontend application easier.

Grouping Recipes by Tags {#h2-5-grouping-recipes-by-tags}
---------------------------------------------------------

Recipes often have multiple tags (e.g., "vegetarian," "quick," or "soup") to help users quickly find recipes with specific characteristics.

We can use [$unwind](https://www.mongodb.com/docs/manual/reference/operator/aggregation/unwind/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=recipe-foojay&utm_term=tony.kim) along with [$group](https://www.mongodb.com/docs/manual/reference/operator/aggregation/group/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=recipe-foojay&utm_term=tony.kim) to count the occurrences of each tag across all recipes.

### Step 1: Using $unwind to Break Down Tags {#h3-6-step-1-using-unwind-to-break-down-tags}

The tags field is an array, so we'll start by using $unwind to create a separate document for each tag.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&gt; db.recipes.aggregate([

&nbsp;&nbsp;{ $unwind: "$tags" },

&nbsp;&nbsp;{ $group: { _id: "$tags", count: { $sum: 1 } } },

])</pre>

[$unwind](https://www.mongodb.com/docs/manual/reference/operator/aggregation/unwind/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=recipe-foojay&utm_term=tony.kim):

This stage "explodes" the tags array, creating a new document for each tag.

[$group](https://www.mongodb.com/docs/manual/reference/operator/aggregation/group/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=recipe-foojay&utm_term=tony.kim):

We then group by each tag, counting its occurrences across the collection.

Output example:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">[

&nbsp;&nbsp;{ _id: "vegetarian", count: 25 },

&nbsp;&nbsp;{ _id: "quick", count: 18 },

&nbsp;&nbsp;{ _id: "soup", count: 12 },

]</pre>

Now, we can see which tags are most common in the collection, giving insights into popular recipe attributes. This data could help users quickly find recipes based on popular tags or dietary preferences.

Practical Applications for Grouped Recipes

By grouping recipes by type and tags, you can offer valuable features to users:

* **Meal type filters**
  * Quickly let users filter for specific types like breakfast, lunch, or dinner.
* **Meal planning**
  * Build a meal planner that recommends recipes across different meal types for a well-rounded weekly plan.
* **Recipe tag analysis**
  * Use tag counts to suggest recipes based on user interests or identify popular recipe tags, enhancing the user experience with relevant suggestions.

Advanced Tip: Using $match for Specific Filters {#h2-7-advanced-tip-using-match-for-specific-filters}
-----------------------------------------------------------------------------------------------------

You may want to group recipes of a specific type, such as only "Vegetarian" recipes. You can do this by adding a $match stage before $group to filter documents first.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&gt; db.recipes.aggregate([

&nbsp;&nbsp;{ $match: { vegetarian_option: true } },

&nbsp;&nbsp;{

&nbsp;&nbsp;&nbsp;&nbsp;$group: { _id: "$type", count: { $sum: 1 }, recipes: { $push: "$title" } },

&nbsp;&nbsp;},

])</pre>

In this example, only recipes with \`vegetarian_option: true\` are included in the grouping, allowing you to analyze vegetarian meal types specifically.

Conclusion {#h2-8-conclusion}
-----------------------------

Using MongoDB's $group operator, you can organize recipes by meal type and tags, making it easy for users to navigate your collection by category. By combining $group with other operators like $unwind and $match, you can create dynamic and user-friendly features that enrich the user experience on your recipe site.

Experiment with $group in your own collections, and see how categorizing data can simplify complex datasets and offer new ways to interact with your MongoDB collection!
