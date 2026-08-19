---
title: "How to Train ReadyNow to Achieve Optimal Java Performance"
date: "2025-06-20T19:31:00+00:00"
lastmod: "2025-06-27T11:17:17+00:00"
description: "This is the third blog post in a series on faster Java application warmup with ReadyNow. If you haven’t been following the series, go back to the first…"
canonical: "https://www.azul.com/blog/how-to-train-readynow-to-achieve-optimal-java-performance/"
authors:
  - "frankdelporte"
image: "Favicon-3-2.png"
categories:
  - "Java"
  - "Performance"
related_posts:
  - "analyzing-and-tuning-warm-up-of-the-jvm-with-azul-zulu-prime-builds-of-openjdk"
  - "faster-java-warmup-crac-versus-readynow"
  - "how-readynow-improves-java-warmup-time"
  - "superfast-application-startup-java-on-crac"
frozen: false
---

*This is the third blog post in a series on faster Java application warmup with ReadyNow. If you haven't been following the series, go back to the first blog post, [Faster Java Warmup: CRaC versus ReadyNow](https://foojay.io/today/faster-java-warmup-crac-versus-readynow/), and catch up. This post explains how you can train the ReadyNow feature in Azul Platform Prime to achieve optimal Java performance.*

Using a profile log made from a single run is only a first step in Java warmup. ReadyNow learns from previous executions and incrementally improves the warmup time at every new invocation. By using several training runs, you can improve your application's performance even further. ReadyNow gathers data from training runs and stores it in a profile log, which ensures better performance after the first and subsequent runs.

## Understanding ReadyNow generations

When using ReadyNow, you get the best results if you perform several training runs of your application to generate an optimal profile. You must do this for each new application build to ensure the profile log aligns with the modified or extended code.

When your application runs in production, it might have a lot of run-to-run variability. This is mainly caused by handling "real-world traffic," which is probably different from your test or training environment. By executing multiple training runs, you help to increase the profiling coverage to produce a final profile that is more representative of the expected application run in production.

For example, to generate a good profile for an application instance called MyApp-v1.5, you could perform three training runs of your application to record three generations of the ReadyNow profile log. You start the first run without an input file and use the output file for each subsequent training run.
![](readynow-generation-training-runs.avif)

You want to ensure that each training run's output meets the minimum criteria to be promoted as the input for the next level. These promotion criteria can be:

* The duration (time) of the training run
* The size of the candidate profile log

You can define these criteria based on the known properties of the expected application run to see if the collected ReadyNow profile is representative enough. You can use basic statistics like profile log duration, file size, the number of recorded compilations, the number of recorded classes, etc. If those numbers closely match the application run statistics, you can conclude that the profile is probably mature enough. For example, If your application gets restarted every 24 hours and loads 50k classes, you also want the same number of classes loaded in your ReadyNow profile.
>
> ## **TIP** When using [the ReadyNow Orchestrator feature of Optimizer Hub](https://docs.azul.com/optimizer-hub/connecting/readynow-orchestrator-generations#basic-profile-recording-with-default-generations), the creation of a promoted profile is handled automatically. This will be explained in the next blog post in this series.
>
## Possible approaches

Different approaches to training the ReadyNow profile can apply depending on your goals and constraints.
>
> ## **TIP**
> To reach an optimal profile, each run needs a minimum of 10,000 executions of all critical application methods.
>
### Optimal approach (pre-production)

Follow this approach to produce a profile that minimizes or eliminates learning in production.

Train the profile across two separate runs in the pre-production environment. Perform a first run of the application until it reaches its optimal performance. Restart the JVM for a second run using the profile you just generated, and again run the application until it reaches optimal performance.

```
# First run
-XX:ProfileLogOut=first-run.log

# Second run
-XX:ProfileLogIn=first-run.log -XX:ProfileLogOut=profile.log
```

The resulting profile log (`profile.log`) is the one you need to use in your production system.

### Basic approach (pre-production)

If the time for training ReadyNow is limited, perform one run of your application in the pre-production environment with the following command line option:

```
-XX:ProfileLogOut=profile.log
```

This will capture a very good profile but may feature a possible odd outlier on the first day of the production run. But this will improve with subsequent runs.

### No profile approach (production)

If you cannot create a profile in a pre-production environment, allow it to learn in production with the following command-line options:

```
-XX:ProfileLogIn=profile.log -XX:ProfileLogOut=profile.log
```

This results in a poor warm-up for the first run in production but gets better performance on subsequent runs as it reuses the same file as input.

## Understanding if training was enough

Check for outliers to validate the sufficiency of ReadyNow training. The absence of outliers denotes that the training went well. Reduced start-up latency also proves that enough training data is gathered in a profile log. Otherwise, more runs are needed to enhance the profile content and get a fully optimized profile log.

In one of the following posts in this series, we will examine examples of profile logs and garbage collector logs to identify common problems and configuration mistakes that can impact your application's performance or the warm-up duration with and/or without ReadyNow.

## Conclusion

Using multiple training runs, better profile logs can be generated, leading to better performance for your Java application. Even when no training run can be executed, storing the compiler decisions in a profile log from the first run in production will improve performance in any subsequent run.

**Next:** [The Role of Optimizer Hub with ReadyNow](https://foojay.io/today/improve-your-java-applications-startup-and-compilation-speed-with-optimizer-hub/)
