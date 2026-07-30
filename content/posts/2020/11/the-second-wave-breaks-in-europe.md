---
title: "Using Java to Analyze The Second Wave Breaking in Europe"
slug: "the-second-wave-breaks-in-europe"
date: "2020-11-27T09:47:14+00:00"
lastmod: "2020-11-27T10:15:35+00:00"
description: "With the Software ECG Covid-19 Edition, you can evaluate the most important Covid-19 time series across countries. Read on for details!"
authors:
  - "johannes-weigend"
image: "https://foojay.io/wp-content/uploads/2020/11/ekg-2-1.png"
categories:
  - "JavaFX"
tags:
related_posts:
frozen: false
---

In this series of blog posts ([see part 1 here](https://foojay.io/today/covid-19-time-series-analysis-with-software-ekg/)), we're looking at the current figures of the Covid-19 pandemic with Software-ECG.{#a808}

Software-ECG is a free time series analysis tool originally developed for time series analysis for system analysis of computer problems in distributed systems. With the COVID-19 edition, QAware has adapted the tool so that the current data from the data hub of the University of Oxford are automatically loaded and immediately available for analysis. More information about the Software-ECG and download links [can be found here on foojay](https://foojay.io/today/covid-19-time-series-analysis-with-software-ekg/).{#a808}

*Note: The German translation for ECG is EKG (Elektrokardiogramm). We are German, therefore we use the names "Software ECG" and "Software EKG" as synonyms.*{#a0fd}

Software-ECG ist build on OpenJDK and JavaFX. It leverages the power of a compiled language with a native rich client framework.{#1887}

### CW 48: The Second Wave Breaks in Europe {#3a67}

In Europe, the number of people who tested positive is now declining. For this purpose, the respective countries can be entered in the selection box using the pipe operator.{#4d9c}
![](/images/posts/2020/11/the-second-wave-breaks-in-europe/ekg-2-1.png) Pipe operator for multiple country selection   

The input string in the editable dropdown has the form "country1 \| country2 \| countryN". The pipe operator is a logical OR which than selects all data from country1 and countryN.{#b7cd}

As a result, the ECG then shows all the countries selected. With the mouse, you can now zoom into the interesting parts. Here is the data from 07/07/2020 until today (11/25/2020).{#c38e}
[![](/images/posts/2020/11/the-second-wave-breaks-in-europe/ekg-2-2.png)](/images/posts/2020/11/the-second-wave-breaks-in-europe/ekg-2-2.png) #New Positive tests per day: Netherlands, Switzerland, France, Austria, Italy, Germany, Spain   

The number of positive tests correlates directly with the total number of tests. Dividing the number of positive tests by the total number gives the infection rate. In the Our World in Data Covid-19 data, this metric is called the "positive rate". In the ECG you can see it directly:{#ed55}
[![](/images/posts/2020/11/the-second-wave-breaks-in-europe/ekg-2-3.png)](/images/posts/2020/11/the-second-wave-breaks-in-europe/ekg-2-3.png) "positive_rate" = number of positive tests / total number of tests   

The data shows also here a flattening or breaking of the 2nd wave. Since at the present time, the data for the Netherlands (purple line) is available only up to 10/25/20, and the data for Germany (blue line) up to 11/15/20, one cannot make a clear statement here. (In next week's blog we will take another look at the curve and investigate whether the curve flattens or breaks.){#281d}

A very useful feature is the date selection. The date period can be set by zooming in or out or by using the Date Picker in the upper right corner.{#2b03}
![](/images/posts/2020/11/the-second-wave-breaks-in-europe/ekg-2-4.png) Selection of the positive rate in the period 7./1/2020 until today   

### Some Countries Already Have Constant Or Even Falling Mortality Rates {#c7f2}

The number of people who have died of or with Covid-19 is also flattening out in all countries except Italy and Germany.{#40c3}
[![](/images/posts/2020/11/the-second-wave-breaks-in-europe/ekg-2-5.png)](/images/posts/2020/11/the-second-wave-breaks-in-europe/ekg-2-5.png) Number of deceased people (2nd wave)   

It is interesting to note that Italy has almost three times as high a death rate as Germany.{#40c3}

The curves are smooth --- this smooth metric makes a lot of sense, because the curves fluctuate a lot on a daily basis, which has to do not only with statistical and medical reasons, but also with organizational reasons of data transfer from laboratories and authorities. The non flattend peek values are also available (new_deaths).{#40c3}

### Summary {#0039}

With the Software ECG Covid-19 Edition, you can evaluate the most important Covid-19 time series across countries.{#255e}

The ECG offers the possibility to display several metrics via logical expressions and to limit them to time periods. Currently you can observe very nicely the breaking of the 2nd wave in Europe.
