---
title: "foojay – a place for friends of OpenJDK"
description: "foojay is the place for all OpenJDK Update Release Information. Learn More."
canonical: "https://foojay.io/java-quick-start/install-java/install-java-on-windows/"
url: "/java-quick-start/install-java/install-java-on-windows/"
enlighterjs: true
aliases:
  - "/java-quick-start/install-java/install-java-on-windows/"
frozen: false
---

*** ** * ** ***

Is Java not available on your Windows computer yet?   

No problem, let's use an installer to make the process really easy.

This is a direct link to get the Azul Zulu build of OpenJDK distribution, version 25, as a Windows installer:   
<https://cdn.azul.com/zulu/bin/zulu25.34.17-ca-jdk25.0.3-win_x64.msi>

Many more distributions and versions of Java exist. You can [read more info here](https://foojay.io/java-quick-start/install-java/find-another-java-version/) on how to find another distribution or version.

Once the `.msi` file has completed downloading, double-click it, and follow the instructions.



<figure class="wp-block-gallery has-nested-images columns-4 is-cropped wp-block-gallery-1 is-layout-flex wp-block-gallery-is-layout-flex">
 <figure class="wp-block-image size-large">
  <img fetchpriority="high" decoding="async" width="616" height="481" data-id="61633" src="/images/pages/java-quick-start/install-java/install-java-on-windows/zulu-install-windows-1.png" alt="" class="wp-image-61633">
 </figure>
 <figure class="wp-block-image size-large">
  <img decoding="async" width="616" height="481" data-id="61632" src="/images/pages/java-quick-start/install-java/install-java-on-windows/zulu-install-windows-2.png" alt="" class="wp-image-61632">
 </figure>
 <figure class="wp-block-image size-large">
  <img decoding="async" width="616" height="481" data-id="61630" src="/images/pages/java-quick-start/install-java/install-java-on-windows/zulu-install-windows-3.png" alt="" class="wp-image-61630">
 </figure>
 <figure class="wp-block-image size-large">
  <img loading="lazy" decoding="async" width="616" height="481" data-id="61631" src="/images/pages/java-quick-start/install-java/install-java-on-windows/zulu-install-windows-4.png" alt="" class="wp-image-61631">
 </figure>
</figure>



After the installation has been completed, you can check the installed version by opening a terminal (click Start, type `cmd`, and hit Enter) and running the `java -version` command.

You should get the a result like this, with the version info depending on the one you installed:



<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">Microsoft Windows [Version 10.0.22621.963]
(c) Microsoft Corporation. All rights reserved.

C:\Windows\System32&gt; java -version
openjdk version "21.0.2" 2024-01-16 LTS
OpenJDK Runtime Environment Zulu21.32+17-CA (build 21.0.2+13-LTS)
OpenJDK 64-Bit Server VM Zulu21.32+17-CA (build 21.0.2+13-LTS, mixed mode, sharing)

C:\Windows\System32&gt; </pre>



That's it. You're now ready to run Java programs and, even better... create your own!
