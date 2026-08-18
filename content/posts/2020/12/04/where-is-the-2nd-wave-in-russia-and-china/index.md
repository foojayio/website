---
title: "Where is the 2nd Wave in Russia and China?"
slug: "where-is-the-2nd-wave-in-russia-and-china"
date: "2020-12-04T12:56:27+00:00"
lastmod: "2020-12-04T17:12:51+00:00"
description: "Last week, we took a look at the current situation in Europe. We saw that the second wave is already breaking in Europe. This trend continues."
authors:
  - "johannes-weigend"
image: "https://foojay.io/1-europe-2/"
categories:
  - "JavaFX"
tags:
related_posts:
frozen: false
---

In this series of blog posts ([see part 1 here](https://foojay.io/today/covid-19-time-series-analysis-with-software-ekg/)), we're taking look at the current figures of the Covid-19 pandemic with Software-ECG. Software-ECG is a free time series analysis tool originally developed for system analysis of computer problems in distributed systems.

With the Covid-19 edition, QAware has adapted the tool so that the current data sets from the data hub of the University of Oxford are automatically loaded and immediately available for analysis. More information about Software-ECG and its download links [can be found here](https://blog.qaware.de/posts/2020-11-11-software-ekg-covid-19-edition/). Note that the German translation for ECG is EKG (Elektrokardiogramm). We are German, therefore we use the names "Software ECG" and "Software EKG" as synonyms.

Software-ECG is built on OpenJDK and JavaFX, leveraging the power of a compiled language with a native rich client framework.

### The 2nd Wave in Russia, Ukraine, Serbia, and China

In [last week's blog post](https://foojay.io/today/the-second-wave-breaks-in-europe/), we took a look at the current situation in Europe and we saw that the second wave was already breaking. Over the past week, this trend has continued.

The number of people testing positive is decreasing significantly in France (black line), Italy (top green line), and Great Britain (top purple line).  
[![The 2nd Wave in Europe](https://foojay.io/1-europe-2/)](https://foojay.io/1-europe-2/)

*The 2nd Wave in Europe*{#caption-attachment-36491}

### Some Graphs Behave Differently

But there are some countries where the graphs are different. These are Russia, Serbia and Ukraine.

To analyze these countries exclusively, use the filter query "Russia\|Serbia\|Ukraine" and zoom into the period of time for the second wave only.

In these countries, the positive test numbers are growing linearly. Neither can we see the typical increase at the beginning nor any flattening at a later point in time. We might assume that the data is gathered in a different way than other EU countries do.  
[![Russia, Serbia and Ukraine show a almost linear growth](https://foojay.io/2-russia-2/)](https://foojay.io/2-russia-2/)

*Russia, Serbia and Ukraine show an almost linear growth*{#caption-attachment-36492}

A possible explanation for this could be that these countries are currently ramping up their testing capacities in a linear way while the infection rate is remaining constant.

If we had the same metrics as in the EU, we could easily demonstrate this by analyzing the number of tests and the infection rate. But these metrics are not published. The relevant fields in the CSV file from Our World in Data are empty for Russia, Serbia, and Ukraine.

### Where is the 2nd Wave in China?

Now, let's take a look at countries outside of Europe and analyze China. This country also has a very interesting chart because the second wave in China looks like this:  
[![2nd Wave in China](3-china-2nd-wave-2)](https://foojay.io/3-china-2nd-wave-2)

*2nd Wave in China*{#caption-attachment-36493}

The chart shows the number of new cases since September 2020. Make sure to look at the scale. Starting from 20 cases per day, it has now reached 100 cases per day.

People could get nervous when they see the number of people tested positive is rising again. Do not get nervous, though! If you take a closer look at the same chart and change the start date to 1/1/2020, you can display both waves in a single chart. Now you will not see any second wave at all.  
[![1st and 2nd Wave in China](https://foojay.io/4-china-both-waves-3/)](https://foojay.io/4-china-both-waves-3/)

*1st and 2nd Wave in China*{#caption-attachment-36494}

China does not report any test numbers. So, just looking at the isolated numbers of the reported positive cases is useless. You do not have any reference point if you do not know how many people are actually ill or have been tested.

It might seem that in China the pandemic is over. People go to parties, concerts and do normal activities again. It would be very interesting, however, if we were able to take a look at the same test metrics (number of tests, positive rate) which are available for Europe.

### Working with Software-ECG

We wanted to demonstrate that it is easy and fun to analyze the current COVID-19 situation with the latest available data in Software-ECG COVID-19 Edition.

Feel free to download the tool and draw your own conclusions for your country, region or even all countries worldwide!

Further information, download links, [and more can be found here](https://blog.qaware.de/posts/2020-11-11-software-ekg-covid-19-edition/).
