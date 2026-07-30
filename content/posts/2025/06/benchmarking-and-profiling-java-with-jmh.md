---
title: "Benchmark and profiling Java with JMH"
slug: "benchmarking-and-profiling-java-with-jmh"
date: "2025-06-24T21:49:25+00:00"
lastmod: "2025-06-24T21:51:12+00:00"
description: "Exploring how to profile and benchmark Java code using JMH and Async profiler."
canonical: "https://davidvlijmincx.com/posts/benchmark-and-profile-java/"
authors:
  - "david-vlijmincx"
image: "https://foojay.io/wp-content/uploads/2025/06/flamegraph-example-2.png"
categories:
  - "Java"
  - "Performance"
tags:
related_posts:
  - "7-functional-programming-techniques-in-java-a-primer"
  - "7-reasons-to-choose-apache-pulsar-over-apache-kafka"
  - "a-closer-look-at-jfr-streaming"
  - "async-file-io-with-java-and-io_uring"
enlighterjs: true
frozen: false
---

Introduction: Why JMH? {#h2-0-introduction-why-jmh}
---------------------------------------------------

Performance matters in Java applications, but measuring it accurately is harder than you might think. I've seen countless developers try to measure performance by wrapping code in System.currentTimeMillis() calls or using simple timing loops, only to get misleading results due to JVM optimizations, garbage collection, or just mistakes during measurement.

The JVM is incredibly good at optimizing code, sometimes so good that it optimizes away the very code you're trying to benchmark. Dead code elimination, constant folding, and just-in-time compilation can all skew your measurements in ways that don't reflect real-world performance.

That's where JMH (Java Microbenchmark Harness) comes in. In this post, I'll walk you through everything you need to know to start benchmarking your Java code, from basic setup to advanced profiling techniques that can help you identify performance bottlenecks.

Dependencies {#h2-1-dependencies}
---------------------------------

For this post, we will use the following dependencies:

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;dependency&gt;
  &lt;groupId&gt;org.openjdk.jmh&lt;/groupId&gt;
  &lt;artifactId&gt;jmh-core&lt;/artifactId&gt;
  &lt;version&gt;1.37&lt;/version&gt;
&lt;/dependency&gt;
&lt;dependency&gt;
  &lt;groupId&gt;org.openjdk.jmh&lt;/groupId&gt;
  &lt;artifactId&gt;jmh-generator-annprocess&lt;/artifactId&gt;
  &lt;version&gt;1.37&lt;/version&gt;
&lt;/dependency&gt;</pre>

These dependencies are needed to run the benchmarks and to use the annotations. You can find the latest version of [jmh-core here](https://mvnrepository.com/artifact/org.openjdk.jmh/jmh-core). The latest version of [jmh-generator-annprocess can be found here](https://mvnrepository.com/artifact/org.openjdk.jmh/jmh-generator-annprocess)

To use the async profiler, you need to download the async profiler from [here](https://github.com/jvm-profiling-tools/async-profiler/releases). Add the async profiler to the classpath. If you are using Linux you can also copy the async profiler to one of the following directories: `/usr/java/packages/lib`, `/usr/lib64`, `/lib64`, `/lib`, `/usr/lib`.

Creating your first benchmark {#h2-2-creating-your-first-benchmark}
-------------------------------------------------------------------

The easiest way to get started is to create a new class and give it a main method to start the benchmark. In the following example, you can see one way of doing this using the OptionsBuilder. It lets you configure everything from which benchmarks to run to how many iterations to perform.

<pre class="EnlighterJSRAW" data-enlighter-language="java">public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
            .include(Main.class.getSimpleName())
            .build();

    new Runner(opt).run();
}</pre>

In the previous example, you can see that we are using the `OptionsBuilder` to create the options. The `OptionsBuilder` has a lot of methods to configure the benchmark. Like if you want to enable garbage collection, how many threads you want to use, or if you want to use the async profiler and many more. For this example, we use `include` to specify which class to benchmark I want to run. In this example, we are running the `Main` class.

With that out of the way, we can start writing our first benchmark. Using annotations creating a benchmark is very straightforward. All you need to do is to add the `@Benchmark` annotation to the method you want to benchmark.

<pre class="EnlighterJSRAW" data-enlighter-language="java">@Benchmark
public void myFirstBenchmark() {

}</pre>

The `@Benchmark` annotation tells JMH that this method is a benchmark. The code inside the method will be executed during the benchmark. This is just an empty method for now, but this should be enough to get you started with your own code. The next section shows the different modes for running benchmarks. We will also add some code to this example later on.

Benchmark modes {#h2-3-benchmark-modes}
---------------------------------------

There are really only four modes you can use to run your benchmarks. These modes are:

* **Average time**: Continuously calls the Benchmark methods, counting the average time. The benchmark will run till
* **Single shot time**: Used to measure the time of a single call. This is handy for measuring a cold start.
* **Throughput**: Counts the total throughput of each worker thread till the iteration time expires.
* **Sample time**: Randomly samples the time needed for the call.

You can set the mode using this annotation `@BenchmarkMode(Mode.Throughput)`. The mode you should use depends on what you want to measure. For example, if you want to measure the time needed to execute a single method, you should use the `SingleShotTime` mode. If you want to measure the throughput of your code, you should use the `Throughput` mode. If you want to measure the average time needed to execute a method, you should use the `AverageTime` mode.

State management {#h2-4-state-management}
-----------------------------------------

When you write benchmarks, you will probably need some state at a point in time. For example, you might need to have some objects in place for your benchmark to run. If you create these objects during the benchmark, they will be timed as well. To avoid this, you can use the `@State` annotation and move the initialization of the objects outside the benchmark method to a `@Setup` method. You can use `@State` on the benchmark class or on a separate class.

I like to separate the state management from the benchmark class. This way I can reuse the state management for multiple benchmarks, and it makes the benchmark class method more readable. In the following example, you can see the state for a benchmark that is going to sort a given array.

<pre class="EnlighterJSRAW" data-enlighter-language="java">@State(Scope.Thread)
public class BenchState {
  private int[] unsorted;

  @Setup()
  public void setUp() {
    unsorted = new int[]{1,5,7,9,10,6,3,1,8,3,4,6};
  }

  public int[] getUnsorted() {
    return unsorted;
  }

}</pre>

The two annotations are `@State` and `@Setup`. The `@State` annotation tells JMH that this class is a state class. The `@Setup` annotation tells JMH that this method is a setup method. The setup method is called before the benchmark method is called. In this example, we are creating an array with some numbers and storing it in the `unsorted` variable.

To use this state in a benchmark, you need to add the state-annotated class as a parameter to the benchmark method. As you can see in the following example.

<pre class="EnlighterJSRAW" data-enlighter-language="java">@Benchmark
public void myFirstBenchmark(BenchState benchState) {
    int[] unsorted = benchState.getUnsorted();
    Arrays.sort(unsorted);
}</pre>

When you run this benchmark it will sort the array that is stored in the `unsorted` variable.

### Keeping the state correct {#h3-5-keeping-the-state-correct}

In the previous example, there is a bug hiding in plain sight. The unsorted array is only sorted once. The problem is that Arrays.sort() modifies the original array. After the first benchmark iteration, you're no longer sorting random data, but you're sorting an already sorted array, which is much faster and gives you misleading results. To fix this, you can use the `unsorted.clone();`. Now each benchmark will sort a new array. The downside is that the clone method will be counted towards the benchmark.

### Using state to create variants. {#h3-6-using-state-to-create-variants}

If you want to benchmark a lot of different parameters, you can use a @state annotated class to keep track of things. For example, you use a state object to test different inputs or to activate different behavior. In the following example, I use it to test different inputs.

In the following code, I have a @state annotated class with a single value "number". JMH will run a unique benchmark for each value in the param array.

<pre class="EnlighterJSRAW" data-enlighter-language="java">@State(Scope.Benchmark)
public class ExecutionPlan {

    @Param({"0", "1", "2", "3", "4", "5"})
    public int number;
}</pre>

The example will make JMH run six different benchmarks. If I add another value like `@Param({"true", "false"})` JMH will create 2 \* 6 = 12 benchmarks. One for each combination. This works great if you want to test lots of combinations, but the more combinations you have the longer the benchmark will take to run. That is something to keep in mind.

Understanding JMH output {#h2-7-understanding-jmh-output}
---------------------------------------------------------

After each benchmark run, JMH will print the results. The output will look something like you can see in the following example. It looks like a table with rows and columns. The first line shows you what each column means. In the first column, you see the name of the benchmark. If you are using `@Param` the second column will show you the value of the parameter. In the third column, you see the mode that was used. In the fourth column, you see the number of iterations. In the fifth column, you see the score of the benchmark. What this score means depends on the benchmark mode used. In the sixth column, you see the standard deviation of the benchmark.

<pre class="EnlighterJSRAW" data-enlighter-language="raw">Benchmark                             (readSize)   Mode  Cnt     Score     Error   Units
b.r.read.RandomReadBenchMark.libUring        512  thrpt    5  1332.440 ± 213.308  ops/ms
b.r.read.RandomReadBenchMark.libUring       4096  thrpt    5  1323.459 ±  93.749  ops/ms</pre>

This should help you to understand what the different columns mean and to interpret the results.

Prevent dead code optimizations {#h2-8-prevent-dead-code-optimizations}
-----------------------------------------------------------------------

To prevent optimizations of unused objects, you can use a black hole. The JVM is very good at optimizing code. If you are creating objects but don't use them, the JVM can optimize this. In your production code, you use all the objects you create so that is also what you want to do in your benchmark. One way to achieve this is to use a black hole. A black hole will fool the JVM into thinking that the object is actually used.

To use a black hole, all you have to do is to add it as a parameter.

<pre class="EnlighterJSRAW" data-enlighter-language="java">@Benchmark()
public void AddingToString(Blackhole blackhole, ExecutionPlan plan){
    var result = "test" + plan.number;
    blackhole.consume(result);
}</pre>

After adding it, you can use it to consume objects in your benchmark code.

Constant folding {#h2-9-constant-folding}
-----------------------------------------

Constant folding is one of the most common ways the JVM can make your benchmarks lie to you. The JVM is smart enough to evaluate constant expressions at compile time, which means your benchmark might be measuring almost nothing. Here's a simple example that demonstrates the problem:

<pre class="EnlighterJSRAW" data-enlighter-language="java">@Benchmark
public int badMath() {
    return 2 + 2 * 5;  // JVM calculates this as 12 at compile time
}</pre>

The JVM sees that this expression will always return 12, so it optimizes the entire method to just return 12. Your benchmark ends up measuring how fast the JVM can return a constant value which is very fast but tells you nothing about the performance of the operation. This becomes more subtle with string operations:

<pre class="EnlighterJSRAW" data-enlighter-language="java">@Benchmark
public String badStringConcat() {
    return "Hello" + " " + "World";  // Becomes "Hello World" at compile time
}

@Benchmark
public String badStringBuilder() {
    StringBuilder sb = new StringBuilder();
    sb.append("Hello");
    sb.append(" ");
    sb.append("World");
    return sb.toString();  // Still optimized because inputs are constants
}</pre>

Both of these methods will be heavily optimized because the JVM knows the result ahead of time. To get meaningful results, you need to use variable data:

<pre class="EnlighterJSRAW" data-enlighter-language="java">@State(Scope.Benchmark)
public class ExecutionPlan {
    public String firstWord = "Hello";
    public String secondWord = " ";
    public String thirdWord = "World";

    @Setup
    public void setUp() {
        // You could even randomize these values
        firstWord = "Hello" + System.nanoTime() % 2; // Prevents compile-time optimization
    }
}

@Benchmark
public String goodStringConcat(ExecutionPlan plan) {
    return plan.firstWord + plan.secondWord + plan.thirdWord;  // JVM can't pre-calculate this
}

@Benchmark
public String goodStringBuilder(ExecutionPlan plan) {
  StringBuilder sb = new StringBuilder();
  sb.append(plan.firstWord);
  sb.append(plan.secondWord);
  sb.append(plan.thirdWord);
  return sb.toString();  // Still optimized because inputs are constants
}</pre>

Running these examples, I got the following results:

<pre class="EnlighterJSRAW" data-enlighter-language="raw">Benchmark           Mode  Cnt           Score          Error  Units
badStringBuilder   thrpt    5   133744464.132 ±  1611284.483  ops/s
badStringConcat    thrpt    5  2848111332.093 ± 97370493.057  ops/s
goodStringBuilder  thrpt    5    45398739.848 ±  3852761.651  ops/s
goodStringConcat   thrpt    5    61711604.066 ±   567156.543  ops/s</pre>

As you can see, the scores differ a lot between the good and bad examples. This is because of the optimizations happening.

To detect constant folding check if your benchmark results are suspiciously fast or show unrealistic performance improvements, if so you're probably hitting constant folding. The fix is always the same use a variable from a state object that the JVM can't predict at compile time.

Using async profiler with JMH {#h2-10-using-async-profiler-with-jmh}
--------------------------------------------------------------------

JMH tells you what is slow, but it doesn't tell you why. That's where the async profiler comes in. Async profiler is a low-overhead sampling profiler that can show you exactly where your application spends its time, down to the method.

The beauty of combining JMH with async profiler is that you get both scoring (from JMH) and deep insights into the call stack (from the profiler). Instead of just knowing that "Method A is 20% slower than Method B," you can see exactly which parts of Method A are causing the slowdown.

Here's how you set it up. First, make sure you have the async profiler library available (see the Dependencies section). Then add the profiler to your JMH options:

<pre class="EnlighterJSRAW" data-enlighter-language="java">Options opt = new OptionsBuilder()
                .include(RandomReadBenchMark.class.getSimpleName())
                .forks(1)
                .addProfiler(AsyncProfiler.class, "lock=1ms simple=true output=flamegraph")
                .shouldDoGC(true)
                .build();</pre>

The key parameters I use most often:

* **output=flamegraph** Creates an interactive HTML flame graph
* **simple=true** Shows simple class names instead of fully qualified names
* **lock=1ms** Profiles lock contention (great for finding synchronization bottlenecks)

When you run the benchmark, the async profiler will generate an HTML file that looks like the flame graph in the following image. Let me explain how to read it:

<img fetchpriority="high" decoding="async" class="alignnone size-medium wp-image-116488" src="/images/posts/2025/06/benchmarking-and-profiling-java-with-jmh/flamegraph-example-700x258.png" alt="" width="700" height="258">

<br />

In this real flame graph, you can immediately see the problem: an enormous amount of time is being spent in close() operations. The width of each stack frame represents the percentage of time spent in that method. The wider the frame, the more time it's consuming. Looking at this graph, I can see:

* **The hotspot**: Most execution time is in file closing operations
* **The call path**: I can trace exactly how we got to these expensive close() calls
* **What to fix**: This is clearly where optimization efforts should focus

This is the kind of insight you can't get from JMH alone. JMH might tell you that your file processing benchmark is slow, but the flame graph shows you that the problem isn't reading or processing it's in cleanup operations that you might not have even considered measuring separately.

Bonus: Linux tools {#h2-11-bonus-linux-tools}
---------------------------------------------

Perf is another great tool if you are working on Linux especially if you are working with native calls using JNI or foreign function API. Like many other tools, it shows you where your application spends most of its time.

You can use Perf like so:

<pre class="EnlighterJSRAW" data-enlighter-language="shell">perf record The_Thing_You_Want_To_Record</pre>

To see what got recorded you can use `perf rapport` this will create an overview of where the application spends its time.

<pre class="EnlighterJSRAW" data-enlighter-language="raw">Samples: 5M of event 'cycles:P', Event count (approx.): 5804229894135
Overhead  Command          Shared Object         Symbol
  34,50%  bench.random.re  [kernel.kallsyms]     [k] native_queued_spin_lock_slowpath 
   4,20%  bench.random.re  [kernel.kallsyms]     [k] rep_movs_alternative             
   4,14%  bench.random.re  [kernel.kallsyms]     [k] filemap_get_read_batch           
   3,68%  bench.random.re  [kernel.kallsyms]     [k] _copy_to_iter                    
   1,77%  bench.random.re  [kernel.kallsyms]     [k] srso_return_thunk                
   1,62%  bench.random.re  [kernel.kallsyms]     [k] apparmor_file_alloc_security     
   1,48%  bench.random.re  [kernel.kallsyms]     [k] walk_component                   
   1,25%  bench.random.re  [kernel.kallsyms]     [k] srso_safe_ret                    
   1,09%  bench.random.re  [kernel.kallsyms]     [k] memset_orig                      
   1,04%  bench.random.re  [kernel.kallsyms]     [k] link_path_walk.part.0.constprop.0
   1,01%  bench.random.re  [kernel.kallsyms]     [k] filemap_read                     
   1,01%  bench.random.re  [kernel.kallsyms]     [k] locks_remove_posix               
   1,00%  bench.random.re  [kernel.kallsyms]     [k] atime_needs_update</pre>

I am working on a file IO tool, and the following tool also comes in quite handy during benchmarking. `iostat` shows you the utilization of the storage devices in your system. It gives you insight into what each device is doing and all kinds of different stats.

I normally run it like so `iostat -x 1` this will keep it printing the stats each second to the console. The output looks as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="raw">avg-cpu:  %user   %nice %system %iowait  %steal   %idle
           0,28    1,10    0,22    0,00    0,00   98,40

Device            r/s     rkB/s   rrqm/s  %rrqm r_await rareq-sz     w/s     wkB/s   wrqm/s  %wrqm w_await wareq-sz     d/s     dkB/s   drqm/s  %drqm d_await dareq-sz     f/s f_await  aqu-sz  %util
dm-0             0,00      0,00     0,00   0,00    0,00     0,00    0,00      0,00     0,00   0,00    0,00     0,00    0,00      0,00     0,00   0,00    0,00     0,00    0,00    0,00    0,00   0,00
nvme0n1          0,00      0,00     0,00   0,00    0,00     0,00   29,00    168,00     0,00   0,00    0,14     5,79    0,00      0,00     0,00   0,00    0,00     0,00    0,00    0,00    0,00   0,00
</pre>

As I said, it shows you a lot of stats about the devices and what it is doing, it also shows the CPU usage. All this is to help you get an insight into what the system is doing.

Conclusion {#h2-12-conclusion}
------------------------------

JMH makes performance measurement a lot more exact and less guessing. By handling JVM optimizations, providing scores, and integrating with profiling tools, JMH gives you a lot of reliable insights into your application.

The key takeaways from this post are: always use @State to manage your benchmark data, watch out for dead code elimination and constant folding, and remember to use tools like async profiler to understand where your application actually spends its time. The combination of JMH benchmarks and flame graphs will show you not just that something is slow, but exactly why it's slow.

Start small with a simple benchmark of the code you suspect might be a bottleneck. Once you see the power of JMH, you'll never go back to guessing about performance again. And remember premature optimization is the root of all evil.
