---
title: "Clean Ways to Shutdown Spring Boot Applications"
slug: "clean-shutdown-of-spring-boot-applications"
date: "2022-11-04T14:11:00+00:00"
lastmod: "2023-08-28T12:48:39+00:00"
description: "Article discusses and presents five ways to shutdown Spring Boot apps cleanly. It provides inline code samples and link to GitHub."
authors:
  - "sumith-puri"
image: "1280px-Spring_Framework_Logo_2018.svg.png"
categories:
  - "Cloud"
  - "Spring"
tags:
related_posts:
  - "starting-docker-desktop-with-spring-boot"
  - "a-simple-service-with-spring-boot"
  - "annotation-free-spring"
  - "ask-a-lille-dev-what-java-developers-really-think-about-quality-frameworks-communities-and-careers"
frozen: false
---

For the last three to four years, I have been working on Spring Boot and its associations, such as Spring Cloud, Spring Data, and Spring Security.

I have always been tempted to use the combination of Ctrl+C and task kill for the purpose of killing Spring Boot applications or processes. It can be automated by a batch script, but usually I was quite lazy to do this myself, as the combination of actions used to serve the purpose!

Almost always on the command line, Ctrl+C kills the Spring Boot application process, although the outcome is very different on Eclipse IDE. The process outlives the Ctrl+C termination of Spring Boot applications, in the case of the IDE.

Anyways, this unreliable and inconsistent outcome across platforms requires us to find standard or clean ways to shutdown Spring Boot applications and processes.

Please note that this article is oriented toward the Windows operating system.

GitHub Link to Clone Repository / Fork Code  
<https://github.com/sumithpuri/skp-spring-boot-shutdown>

Remember this article will explain ways to shutdown a Spring Boot application 'cleanly'. This means that terminating a Spring Boot application will also lead to its process being terminated, immediately.

It is different from a graceful shutdown at the application level, where the application may need to perform certain actions or wait for a certain period of time to gracefully respond to current requests or to end current processing.

**1. Terminate the Application, Kill the Process (Command Line) - IDE**

You can verify this fact by issuing this command on the command-line

```
netstat -ao
```

You will get a screen such as the below. Locate the port on which you had started the Tomcat Server.  
![](https://blogger.googleusercontent.com/img/a/AVvXsEgHTALnmpA7DEvZj6QxKQmwqKdjPPKLhKlZsw8ZnXz5r9ZiZqF6AIRO2IPSBmH5nhEmheRyCoJiiMOQJ0u9zehkl4XPav7aezTSiU8bQAoH8xu3lMzQQNQIIWvM6c-k7_owyzWB1D-KdnHA6467-79vdiNRC34ZKGf1zIlWj-7Pe9u7HiUOZoHt4ETlLw)

Go ahead and locate the PID and kill the task (Windows/MS-DOS)

```
taskkill /F /PID 14836
```

![](https://blogger.googleusercontent.com/img/a/AVvXsEiS0214rgPJ0H8qP9HK8Eciiy9RNiE3qtqL9wOIXAO4hDX9x98oXi-BwPqyA7E0H0d2HUJOEZbnpfFTv_ed6_kUl39TjmCgHnI_UK1ktyx_FSOIKoEtYoKQMRzLZFYJJicBhKg9MhyRIOxVWg2WPoxbqXsd9F1iktbdc2xwUVY-eK2LH1LTYaDvegG5ow)

Note that if you were running the application from the command-line, You can just press \[Ctrl+C\] to terminate the application. In most cases, it will also kill the process. But if it does not kill the process, you should proceed with the above mentioned steps.  
**\[1a\]** ++Automate the writing of Process Id to a File++

\[The above mentioned task of finding out the process id associated with the application can be automated or programmed using the 'ApplicationPidFileWriter' as shown below. In the below example, when the server starts it will write the PID to the file name 'sbshutdownwin.pid'. Later, the same steps as described above can be followed to kill the process. Note that the steps can also be automated via Batch Scripting in Windows/MS-DOS\]

```java
 SpringApplication springBootapplication = new SpringApplication(SpringBootDockerApplication.class);  
 springBootapplication.addListeners(new ApplicationPidFileWriter("sbshutdownwin.pid"));  
 springBootapplication.run();
```

**2. Terminate the Application, Kill the Process (TCPView)**

There is one another way to immediately kill the process via the TCPView tool that is made available by Microsoft. It is available at this [link](https://learn.microsoft.com/en-us/sysinternals/downloads/tcpview) to download.
![](https://blogger.googleusercontent.com/img/a/AVvXsEjL9aie5-WPDlJOWLxVGPRzWlekHbKfkrEscRetmCcaL3OUOLq-txmjYPHeuBsl3s1DO1o8Pr_th2NcpT4HNH0GQsb0Py2luN3WZAXwqxN6jcuLB0m6AMWC4JgxXPHvpLu1kooRWx7Mk15q_SHjXV8LGstOtEZJXvTxln1IAwqpuWUV_qruJpzI7HAgGA=w640-h306)

The next simple method is proposed by me. Beginby clicking on the \[Terminate - Red Square in Eclipse on the Console Tab\] button. You will observer that the application immediately shuts down. But infact at the background, the tomcat server will still be running on the port that it was started!

Use the above TCPView tool to locate the process based on the port on which you started the Tomcat Server/Spring Boot Application. Right Click on the process/row item and click on \[Kill Process...\] and then again Click on \[OK\] to confirm the killing of the operating system process.

This is a very convenient way especially if you are a Software Architect or an Engineer who has lots of Microservices to develop/manage/maintain. It will be useful when you are in depths of testing/debugging/fixing.

**3. Shutdown using Actuator Endpoint**

The most 'cleanest' way among the ones I described until now is the usage of Actuator to shutdown the spring boot applications. You will begin this by making sure to the include the following in your **pom.xml**

![](https://blogger.googleusercontent.com/img/a/AVvXsEjMGD_-_KVzJC1S-8yY9xlkHawwdDSmQOEGGyJqG5M4w4GCqe1Hb1loq3cJK5ha6_lWkAGbdx79hzEXe7anYhn8ZOQKGw34ag9UCEjs3lNevzhEun_OjV8ZXijPAEfe42HLjGdJbqXVuVM8s8xWGglp3xwshOIPWbhDRQ_Q-PidNZUQRNkMoO4dtwHWBA=w400-h55)

Next make sure that you have enabled the shutdown endpoint via actuator using the properties as shown below.

![](https://blogger.googleusercontent.com/img/a/AVvXsEg4yZyksyu3xqAbAj44fheHcemCgQZNjGKqEjyrUtOyupBEGwmK5iWkORjloBSMlRnvrzkR_F-67CzLy2G3WS1j3GrVM_iwNqfGDaLZuvAsFdATgQOKxDChPkcwY-M8atwhl6tBo8jLDJFNucVGR5IlLq5MP8LmGfdW29cpM-4LQUpnbXlY8roNWYV9Ag=w640-h144)

Once you have to shutdown the application, the actuator endpoint /shutdown will also kill the associated process. This is a very clean way to shutdown the Spring Boot applications. You have to make sure that you invoke the actuator endpoint /shutdown via a \[POST\] request only.
![](https://blogger.googleusercontent.com/img/a/AVvXsEjELC9xnk-jTPNkpKG4JHvEX64fM33ZNJPYh2K9uk0aF1nDfCpM1B_6TdYDehsaSdtuODU7eqBczYFciTj4IMj76oxYrnK2RGbNxDJ20a8BUxE9-XKxjYro056BO1jPeAXysyF75ydWPxLhcI-t3RJCps1j218jEq_avpiuI7bPElYFBPAT3Rl4xqdwRg=w640-h130)

If Spring Boot is the root context of your spring boot application, then the invocation will look like the following. The above diagram shows the response from the request sent via Postman.

```batch
 http://localhost:8080/springbootdocker/actuator/shutdown
```

**4. Close the Spring Boot ApplicationContext**

The application context needs to be stored when you start/run the spring boot application in your spring boot application class.

```java
public class SpringBootDockerApplication {  
      // Clean Shutdown Method 2 - Actuator Shutdown Endpoint - Store the Context  
      // Not a Recommended Way to Use public static - Only for the Demo Purposes..  
      public static ConfigurableApplicationContext ctx;  
      public static void main(String[] args) {  
           // Clean Shutdown Method 2 - Actuator Shutdown Endpoint - Store the Context  
           ctx = SpringApplication.run(SpringBootDockerApplication.class, args);  
      }  
 }
```

You may then use the following controller method to close the context asynchronously. Also, note that a PreDestroy method has been added to do any cleanup.

```java
      // Starting Threads using Runnable is Not Always the Best Way..  
      // Do Explore Other Integrated Approaches such as Async Servlet  
      @RequestMapping(value = "/shutdown2", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)  
      public ResponseEntity<Object> rule() {  
           System.out.println("Entry Thread Id (Debug): " + Thread.currentThread().getName());  
           Runnable runnable= () -> {  
                try {  
                     Thread.sleep(2000);  
                } catch (InterruptedException e) {  

                     System.out.println("Thread was Interrupted! Error in Thread Sleep (2 Seconds!)");  
                }  
                System.out.println("Callable Thread Id: " + Thread.currentThread().getName());  
                SpringBootDockerApplication.ctx.close();            
           };  

           new Thread(runnable).start();  
           System.out.println("Exit Thread Id (Debug): " + Thread.currentThread().getName());  
           return new ResponseEntity<>("Shutdown Requested - Will Shutdown in Next 2 Seconds!", HttpStatus.OK);  
      }  

      @PreDestroy  
      public void requestShutdown2PreDestroy() {  

           System.out.println("Requested Shutdown (via Context) of the Spring Boot Container");  
      }
```

Once the above has been done, built and deployed. You may then close the spring boot using the following browser request.

![](https://blogger.googleusercontent.com/img/a/AVvXsEh3qfActZG-2gu9LjZ3e73WemtzCttIziJ3f181kiRhpCEGrtCGqjofhAjRJ8g4kN66KT_R5Ds2YurtYvb4sIHfpht0oT0vvpNnwlRVoHAKTJIlTjuOCM3E25AP9D6U_DevgY88dkj6GHxSzVnmhGsb2MmOj0fiJNMbKqEyVjMNfkBz69D_t-3W4K5cCg=w640-h122)

You will notice that the application has been shutdown when you see the console and also the process has been removed from the operating system processes.

![](https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEjwooWQgGZkL8VyoA6LJ6Lzc51_Nq9KHvfveWJifqWK4Qm_24FzXOlfWVW0aqLHV8i7BB03IvsKmidWAnf83DUeyWP1wg1o5pG1o9eY7r7vUfLN8X4fRxzeSnD428cEak97SWoaUF0zxwOmSrFBfspKGx9GXVyhmK09NTG6H-RxtmkT548DPJV4ZiG3Qg/w640-h70/skp_ts_blog_001.png)

**5. Exit using SpringApplication.exit()**

The other way is use the exit() method of the SpringApplication class, it will also require to use System.exit() for ending the process.

```java
    // Clean Shutdown Method 2 - Actuator Shutdown Endpoint - Store the Context   
    // Not a Recommended Way to Use public static - Only for the Demo Purposes..   
    private static ConfigurableApplicationContext ctx;   

    public static void main(String[] args) {   

       // Clean Shutdown Method 2 - Actuator Shutdown Endpoint - Store the Context   
       ctx = SpringApplication.run(SpringBootDockerApplication.class, args);   
    }   

    public static void exitApplication() {   
       int staticExitCode = SpringApplication.exit(ctx, new ExitCodeGenerator() {   

        @Override   
        public int getExitCode() {   
        // no errors   
        return 0;   
        }   
       });   

       System.exit(staticExitCode );   
    }
```

The controller will look similar to what we had written previously. Once your application is running and you want to shutdown the application 'cleanly', then you may use the following:

![](https://blogger.googleusercontent.com/img/a/AVvXsEhj9fdgrxzUzjulgrIX5hosG0H-nC0SgPcICf29E5Ld4p-R7omRSHNol4bAVVpClQ_B2dNYyLuD1rrCvSgQqWRKJGF5SbzM3_8VMNcKnDb3rJuH5_tg1w4DwGBPVHyIq0DfVlGGH4iy-RWhB1hKuxw6jxHF8MCd7yHzAcq3yCPkgTkwULetJ_nNQzWM9g=w640-h96)

You will notice that the application has been shutdown when you see the console and also the process has been removed from the operating system processes.

![](https://blogger.googleusercontent.com/img/a/AVvXsEjEMWmzL03e0bjpDwxNE6sdx_-UnSfXugAZ1VNCNYucL1d35gmNJTZfCSQm1OUu8IOEAnkuvx4CHuuO4V7i6GzelBuq6AGzRqApL1OjwRlL5cj5HwYWig-OtAcXG2kceyPtMO9P5nd5sZTlkWZKtbTXl68n7bvBkdujThsxRsnSTTvBaDTNNwe81j5yXw=w640-h62)

**\[Reference\]**   
<https://www.javadevjournal.com/spring-boot/shutdown-spring-boot-application/>
