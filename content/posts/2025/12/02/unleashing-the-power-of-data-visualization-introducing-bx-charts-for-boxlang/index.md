---
title: "Unleashing the Power of Data Visualization: Introducing BX-Charts for BoxLang"
slug: "unleashing-the-power-of-data-visualization-introducing-bx-charts-for-boxlang"
date: "2025-12-02T09:21:00+00:00"
description: "In the world of modern web applications, data is king—but raw numbers rarely tell the full story. 📈 That's where visualization becomes crucial, - by Cristobal Escobar"
authors:
  - "cristobal-escobar"
image: "bx-chart-release.jpg"
categories:
  - "Uncategorized"
tags:
related_posts:
  - "boxlang-1-7-0-delivers-streaming-distributed-caching-and-enhanced-jvm-performance"
  - "boxlang-redis-has-landed-enterprise-grade-caching-pub-sub-and-distributed-locking"
  - "foojay-podcast-76"
  - "javafx-links-of-september-2025"
enlighterjs: true
frozen: false
---

![](bx-chart-release-700x467.jpg)

In the world of modern web applications, data is king---but raw numbers rarely tell the full story. 📈 That's where visualization becomes crucial, transforming complex datasets into intuitive, compelling narratives. Today, we're thrilled to introduce BX-Charts, a game-changing charting module that brings professional-grade data visualization directly into the BoxLang ecosystem. 🌟

The Visualization Challenge 🧩 {#h2-0-the-visualization-challenge}
------------------------------------------------------------------

Developers have long struggled with creating meaningful charts:

* 🔧 Complex JavaScript libraries
* 🏗️ Extensive configuration
* 📚 Steep learning curves
* 🚧 Limited flexibility

**BX-Charts** demolishes these barriers, offering a seamless, powerful charting solution that's both developer-friendly and enterprise-ready. 💪

Fully Documented {#h2-1-fully-documented}
-----------------------------------------

We love ❤️ docs! We have fully documented our module: [https://boxlang.ortusbooks.com/boxlang-framework/modularity/charts](https://boxlang.ortusbooks.com/boxlang-framework/modularity/charts "https://boxlang.ortusbooks.com/boxlang-framework/modularity/charts")

Fully Supported {#h2-2-fully-supported}
---------------------------------------

If you have one of our +/++ licenses, you will get personalized support and implementation advice: [https://www.boxlang.io/plans](https://www.boxlang.io/plans "https://www.boxlang.io/plans")

Code Samples: Bringing Data to Life {#h2-3-code-samples-bringing-data-to-life}
------------------------------------------------------------------------------

1. Multi-Series Performance Analysis with Per-Series Border Styling  
   ![](bx-charts-1-700x324.png)

<pre class="EnlighterJSRAW" data-enlighter-language="java">&lt;bx:chart title="Quarterly Performance Analysis"
          chartwidth="700" chartheight="400"
          xaxistitle="Quarter" yaxistitle="Amount ($K)"
          showlegend="true"
          showygridlines="true"
          backgroundcolor="##ffffff"&gt;
    &lt;bx:chartseries type="bar"
                    colorlist="3498db"
                    serieslabel="Revenue"
                    bordercolor="##2980b9"
                    borderwidth="3"
                    borderradius="8"&gt;
        &lt;bx:chartdata item="Q1" value="125" /&gt;
        &lt;bx:chartdata item="Q2" value="158" /&gt;
        &lt;bx:chartdata item="Q3" value="142" /&gt;
        &lt;bx:chartdata item="Q4" value="189" /&gt;
    &lt;/bx:chartseries&gt;
    &lt;bx:chartseries type="bar"
                    colorlist="e74c3c"
                    serieslabel="Expenses"
                    bordercolor="##c0392b"
                    borderwidth="2"
                    borderradius="4"&gt;
        &lt;bx:chartdata item="Q1" value="95" /&gt;
        &lt;bx:chartdata item="Q2" value="102" /&gt;
        &lt;bx:chartdata item="Q3" value="98" /&gt;
        &lt;bx:chartdata item="Q4" value="115" /&gt;
    &lt;/bx:chartseries&gt;
&lt;/bx:chart&gt;</pre>

2. Line Chart with Variable Border Widths  
   ![](bx-charts-2-700x316.png)

<pre class="EnlighterJSRAW" data-enlighter-language="java">&lt;bx:chart title="Website Traffic Trends"
          chartwidth="700" chartheight="400"
          xaxistitle="Month" yaxistitle="Visits (K)"
          showlegend="true"
          showygridlines="true"
          showmarkers="true"
          backgroundcolor="##ffffff"&gt;
    &lt;bx:chartseries type="line"
                    colorlist="e74c3c"
                    serieslabel="Total Visitors (Primary)"
                    bordercolor="##c0392b"
                    borderwidth="5"&gt;
        &lt;bx:chartdata item="Jan" value="125" /&gt;
        &lt;bx:chartdata item="Feb" value="142" /&gt;
        &lt;bx:chartdata item="Mar" value="158" /&gt;
        &lt;bx:chartdata item="Apr" value="171" /&gt;
    &lt;/bx:chartseries&gt;
    &lt;bx:chartseries type="line"
                    colorlist="3498db"
                    serieslabel="Returning Visitors"
                    bordercolor="##2980b9"
                    borderwidth="2"&gt;
        &lt;bx:chartdata item="Jan" value="45" /&gt;
        &lt;bx:chartdata item="Feb" value="52" /&gt;
        &lt;bx:chartdata item="Mar" value="61" /&gt;
        &lt;bx:chartdata item="Apr" value="68" /&gt;
    &lt;/bx:chartseries&gt;
&lt;/bx:chart&gt;</pre>

3. Responsive Dashboard Layout  
   ![](bx-charts-3-700x449.png)

<pre class="EnlighterJSRAW" data-enlighter-language="java">&lt;div class="row g-3"&gt;
    &lt;div class="col-lg-6"&gt;
        &lt;bx:chart title="Sales"
                  chartwidth="400" chartheight="250"
                  responsive="true"
                  maintainAspectRatio="true"&gt;
            &lt;bx:chartseries type="pie" colorlist="FF6384,36A2EB" serieslabel="Sales"&gt;
                &lt;bx:chartdata item="Online" value="65" /&gt;
                &lt;bx:chartdata item="Retail" value="35" /&gt;
            &lt;/bx:chartseries&gt;
        &lt;/bx:chart&gt;
    &lt;/div&gt;
    &lt;div class="col-lg-6"&gt;
        &lt;bx:chart title="Traffic"
                  chartwidth="400" chartheight="250"
                  responsive="true"
                  maintainAspectRatio="true"
                  showygridlines="true"&gt;
            &lt;bx:chartseries type="bar" colorlist="4BC0C0" serieslabel="Visitors"&gt;
                &lt;bx:chartdata item="Mon" value="120" /&gt;
                &lt;bx:chartdata item="Tue" value="150" /&gt;
                &lt;bx:chartdata item="Wed" value="135" /&gt;
            &lt;/bx:chartseries&gt;
        &lt;/bx:chart&gt;
    &lt;/div&gt;
&lt;/div&gt;</pre>

4. Background and Font Styling  
   ![](bx-charts-4-700x296.png)

<pre class="EnlighterJSRAW" data-enlighter-language="java">&lt;bx:chart format="canvas" title="Font Styling Demo" 
          backgroundcolor="##ffffff"
          chartheight="300" chartwidth="600" 
          showlegend="true"
          fontbold="true" 
          fontitalic="true" 
          fontsize="14"
          foregroundcolor="##2E4057" 
          xaxistitle="Categories" 
          yaxistitle="Values"&gt;
    &lt;bx:chartseries type="bar" 
                    colorlist="FF6B6B,4ECDC4,45B7D1" 
                    serieslabel="Styled Data"&gt;
        &lt;bx:chartdata item="Item A" value="45" /&gt;
        &lt;bx:chartdata item="Item B" value="32" /&gt;
        &lt;bx:chartdata item="Item C" value="23" /&gt;
    &lt;/bx:chartseries&gt;
&lt;/bx:chart&gt;</pre>

5. Bubble Chart for Multi-Dimensional Analysis  
   ![](bx-charts-5-700x349.png)

<pre class="EnlighterJSRAW" data-enlighter-language="java">&lt;bx:chart title="Product Portfolio Analysis"
          chartwidth="600" chartheight="350"
          xaxistitle="Market Share (%)" 
          yaxistitle="Revenue (millions)"
          showxgridlines="true" 
          showygridlines="true"&gt;
    &lt;bx:chartseries type="bubble" 
                    colorlist="9966FF,36A2EB,FF6384,FFCE56"
                    serieslabel="Products"&gt;
        &lt;bx:chartdata item="Product A" x="20" y="85" r="15" /&gt;
        &lt;bx:chartdata item="Product B" x="35" y="120" r="20" /&gt;
        &lt;bx:chartdata item="Product C" x="15" y="65" r="10" /&gt;
        &lt;bx:chartdata item="Product D" x="25" y="95" r="12" /&gt;
    &lt;/bx:chartseries&gt;
&lt;/bx:chart&gt;</pre>

Exploring the Possibilities 🔍 {#h2-4-exploring-the-possibilities}
------------------------------------------------------------------

![](boxlang-charts-explorer-700x372.png)  

We've built an interactive Chart Explorer at **[charts.boxlang.io](https://charts.boxlang.io "charts.boxlang.io")**, where you can:

* Browse live chart examples
* View source code
* Experiment with different chart configurations
* Learn best practices for data visualization

### Installation 🔧 {#h3-5-installation}

Get started with BX-Charts

<pre class="EnlighterJSRAW" data-enlighter-language="java"># CommandBox Web Apps
box install bx-charts

# Core OS or Other Runtimes
install-bx-module bx-charts</pre>

### Key Features {#h3-6-key-features}

* 📊 10 Chart Types
* 📱 Fully Responsive
* 🎨 Customizable Styling
* 🔗 Easy Data Integration
* 🚀 Performance Optimized

### Developers, Unleash Your Data! 🌍 {#h3-7-developers-unleash-your-data}

Whether you're building business intelligence dashboards, scientific visualizations, or performance reports, BX-Charts provides the flexibility and power you need. 🔍

**Ready to transform your data into insights? BX-Charts is here. 🌟**

[Explore Full Documentation](https://boxlang.ortusbooks.com/modules/bx-charts "Explore Full Documentation")
