---
title: "Separate Covid-19 Waves in Europe and South America"
date: "2020-12-11T09:47:59+00:00"
lastmod: "2020-12-11T09:58:50+00:00"
description: "Software-ECG is a free time series analysis tool which we will use this week to compare Covid-19 trends in Europe with South America."
authors:
  - "johannes-weigend"
image: "CW50-1-1.png"
categories:
  - "JavaFX"
related_posts:
frozen: false
---

In this series of blogposts ([see part 1 here](https://foojay.io/today/covid-19-time-series-analysis-with-software-ekg/)) we take look at the current figures of the Covid-19 pandemic with Software-ECG.

Software-ECG is a free time series analysis tool originally developed for time series analysis for system analysis of computer problems in distributed systems. With the Covid-19 edition, QAware has adapted the tool so that the current data from the data hub of the University of Oxford are automatically loaded and immediately available for analysis. More information about Software-ECG and download links [can be found here](https://foojay.io/today/covid-19-time-series-analysis-with-software-ekg/).

Software-ECG is build on OpenJDK and JavaFX. It leverages the power of a compiled language with a native rich client framework.

### Overview

[Last week, we compared the situation in Europe and in China,](https://foojay.io/today/where-is-the-2nd-wave-in-russia-and-china/) and discovered interesting anomalies in the data on Russia, Ukraine, Serbia, and China.

This week, we are going to compare Europe with South America.

### Europe

During the summer months of 2020, the pandemic paused throughout Europe. This applies to all countries, except for Russia. Russia had 100+ Covid-19 related deaths/day during the summer months July and August.  
[![Daily deaths in Europe (excluding Russia) related to Covid-19](https://foojay.io/?attachment_id=36582)](https://foojay.io/?attachment_id=36582)

*Daily deaths in Europe (excluding Russia) related to Covid-19*{#caption-attachment-36578}

The graph displays the daily number of deaths in European countries excluding Russia.

Hint: To filter out Russia, the expression -(Russia) is used in the country field. This syntax is taken from the Lucene Query Language. All ECG queries are Lucene queries against an Apache Solr NoSQL database. Therefore, in all editable Comboboxes, these kinds of queries can be used to narrow the search result.

The strong oscillation of the curves can be observed in a weekly rhythm for all countries. While the maximum is on Tuesdays, the minimum can be observed on Sundays. The reason for this might be that new cases from the weekend are usually reported late. This anomaly affects all countries.

Except for Russia, the number of deaths decreased and almost reached zero during summer. However, in autumn, the second wave started in Europe.

In Software-ECG COVID-19 Edition, you can create an aggregated view of all these values. The result will look as shown in the following graph:  
[![Total deaths (sum) in Europe related to Covid-19](https://foojay.io/?attachment_id=36583)](https://foojay.io/?attachment_id=36583)

*Total deaths (sum) in Europe related to Covid-19*{#caption-attachment-36581}

The seasonal component cannot be ignored as it is not surprising for a disease that has the same transmission as the seasonal flu.

### South America

Now, we would like to ask the following question—can the same behavior also be seen in South America?

Let us surprise you with the following graph:  
[![Total deaths in South America related to Covid-19](https://foojay.io/?attachment_id=36584)](https://foojay.io/?attachment_id=36584)

*Total deaths in South America related to Covid-19*{#caption-attachment-36580}

A wave is most likely to be seen in Brazil (brown line) during the winter months of the year. However, the number of cases and the number of deaths is rising there again although summer is just around the corner.

In some countries, there are significant outliers in the curves. Such data anomalies probably are due to adjustments and corrections that were combined into one corrected value later.

In sum, it looks less like a wave than a continuous event. For this purpose, you can add up all curves with the Combine Metrics -\> Add up (exact) function of the ECG:  
[![Total deaths (sum) in Europe related to Covid-19](https://foojay.io/?attachment_id=36585)](https://foojay.io/?attachment_id=36585)

*Total deaths (sum) in Europe related to Covid-19*{#caption-attachment-36579}

It would certainly be nice if the summer pause of the northern and southern hemisphere were the same. Then you would be able to flee from the virus by travelling to a different hemisphere.

However, the data does not support this. In Brazil, it is summer right now and, of course, it is more pleasant there than in Europe at the moment, though nonetheless you will still be confronted with COVID-19 there.

### Working with Software-ECG

By means of this series of articles, we're aiming to demonstrate that it is easy and interesting to analyze the current COVID-19 situation with the latest available data in Software-ECG COVID-19 Edition.

Feel free to download the tool and draw your own conclusions for your country, region or even all countries worldwide!

Further information, download links, and more [can be found here](https://info.qaware.de/software-ekg-covid-edition).
