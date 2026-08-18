---
title: "Hot Class Reload in Java: A Webpack HMR-Like Experience"
date: "2024-11-19T15:57:59+00:00"
lastmod: "2024-11-19T15:58:02+00:00"
description: "Hot Class Reload in Java offers a powerful way to enhance development productivity by reducing the need for full application restarts."
authors:
  - "mohibul-chowdhury"
image: "image_2024-10-03_111134341_fwi3rh.png"
categories:
  - "Java"
  - "Java Core"
related_posts:
  - "5-tips-to-create-secure-docker-images-for-java-applications"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "9-outdated-ideas-about-java"
  - "build-secure-ai-chat-applications-with-boxlang-rag-ollama-and-amazon-bedrock-with-dan-card"
frozen: false
---

**In the world of software development, time is everything. Every developer knows the frustration of waiting for a full application restart just to see a small change take effect.**

Java developers, in particular, have long dealt with this issue. But what if you didn't have to stop and restart every time you updated a class? Enter **Hot Class Reload (HCR)** in Java---a technique that can keep you in the flow, reloading classes on the fly, much like **Hot Module Reload (HMR)** does in JavaScript.

In this guide, we'll walk through how to implement HCR and integrate it into your Java development workflow. By the end, you'll have a powerful new tool to cut down on those long, unproductive restart times.

## Understanding Hot Class Reload

Simply put, Hot Class Reload (HCR) allows Java applications to reload classes at runtime. This is incredibly useful in development environments where you're constantly iterating and tweaking code. Think of it as real-time editing: you change some code, the application picks it up right away---no restart required.

Hot Class Reload (HCR) is a technique that enables Java applications to reload classes at runtime. This approach is particularly useful in development environments where frequent code changes occur. By leveraging Java's `WatchService`, custom class loaders using the `javax.tools` api and `JavaCompiler` class we can monitor source files for changes, compile them on-the-fly, and reload the updated classes into the running application.

### How It Works

The core components of our HCR implementation include:

1. **File Watcher** : Utilizes Java's `WatchService` to monitor a directory for changes to `.java` files.
2. **Dynamic Compilation** : Uses `javax.tools.JavaCompiler` to compile modified Java source files.
3. **Custom Class Loader**: Loads the newly compiled classes into the JVM, replacing the old versions.

### System Flow Diagram

Below is a simplified flow diagram illustrating the HCR process:

![HCR Flowchart](https://res.cloudinary.com/dlsxyts6o/image/upload/v1727932300/image_2024-10-03_111134341_fwi3rh.png)

1. **Source Changes**: Developers modify Java source files in the specified directory.
2. **File Watcher**: Detects changes and triggers the compilation process.
3. **Compile \& Reload**: Compiles the modified files and reloads the classes using a custom class loader.
4. **Custom Class Loader**: Loads the newly compiled class into the jvm.
5. **Invoke Main Method**: Exectues the main method by executing the bytecode of the reloaded class.

## Implementing Hot Class Reload

Here's a breakdown of the Java program implementing HCR:

#### Setting Up the Environment

The program initializes by setting up directories for source and compiled classes. It ensures these directories exist and are ready for use.

```java
public HotReload(String sourceDir, String classDir) {
    this.sourceDir = Paths.get(sourceDir).toAbsolutePath();
    this.classDir = Paths.get(classDir).toAbsolutePath();
    this.classLoader = new CustomClassLoader();
    this.compiler = ToolProvider.getSystemJavaCompiler();
    this.fileManager = compiler.getStandardFileManager(null, null, null);

    // Ensure directories exist
    try {
        Files.createDirectories(this.sourceDir);
        Files.createDirectories(this.classDir);
    } catch (IOException e) {
        throw new RuntimeException("Failed to create directories", e);
    }
}
```

The constructor begins by converting the provided sourceDir and classDir strings into absolute Path objects using `Paths.get().toAbsolutePath()`. This ensures that the paths are fully qualified and can be used reliably throughout the application.

Next, it initializes the classLoader as an instance of the `CustomClassLoader` class, which is responsible for dynamically loading compiled classes. The compiler is set to the system Java compiler obtained from `ToolProvider.getSystemJavaCompiler()`, and the fileManager is initialized using the standard file manager from the compiler.

This setup allows the class to compile Java source files programmatically. The constructor then attempts to create the directories specified by `sourceDir` and `classDir` if they do not already exist, using `Files.createDirectories()`.

If directory creation fails, it throws a RuntimeException to indicate a critical error that prevents the application from proceeding. This ensures that the necessary directory structure is in place for the class to function correctly.

#### Watching for File Changes

A dedicated thread runs a file watcher that listens for modifications to `.java` files in the source directory. The `watchForChanges()` method is responsible for monitoring the source directory for any modifications to `.java` files and triggering the compileAndReload method when changes are detected. Here is the code for the watchForChanges method:

```java
private void watchForChanges() {
    try (WatchService watchService = FileSystems.getDefault()
        .newWatchService()) {
        sourceDir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

        while (true) {
            WatchKey key = watchService.take();
            for (WatchEvent << ? > event : key.pollEvents()) {
                if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                    Path changed = sourceDir.resolve((Path) event.context());
                    String fileName = changed.getFileName()
                        .toString();
                    if (fileName.endsWith(".java")) {
                        compileAndReload(fileName);
                    }
                }
            }
            if (!key.reset()) {
                break;
            }
        }
    } catch (Exception e) {
        System.err.println("Error in file watcher: " + e.getMessage());
        e.printStackTrace();
    }
}
```

#### Compiling and Reloading Classes

The `compileAndReload` method is designed to handle the compilation of a Java source file and subsequently reload the resulting class if necessary.

Upon invocation, it accepts a `fileName` parameter, which should be a Java source file (e.g., "MyClass.java"). The method begins by extracting the class name by removing the ".java" extension from the provided file name.

It then constructs the paths for both the source file and the compiled class file using `sourceDir` and `classDir`, which are assumed to be defined in the broader context of the class.

This method checks if recompilation is required by comparing the modification times of the source file and the class file. It retrieves the last known modification time from a `lastModified` map and obtains the current modification time of the source file.

The method checks if the class file exists and whether its modification time is newer than or equal to that of the source file. If these conditions indicate that recompilation is unnecessary, the method exits early.

Conversely, if recompilation is deemed necessary, it invokes a separate `compile` method (details of which are not provided in this snippet) to compile the source file. Upon successful compilation, it calls a `reloadClass` method (also not shown) to reload the newly compiled class and updates the `lastModified` map with the current modification time.

The program prints a success message to confirm the operation's success. If compilation fails, it displays an error message. A try-catch block encapsulates the entire process to effectively handle any exceptions that may arise during compilation or file operations.

```java
private void compileAndReload(String fileName) {
    try {
        String className = fileName.replace(".java", "");
        Path sourceFile = sourceDir.resolve(fileName);
        Path classFile = classDir.resolve(className + ".class");

        // Check if the file has been modified since last compilation
        long lastModTime = lastModified.getOrDefault(className, 0L);
        long currentModTime = Files.getLastModifiedTime(sourceFile)
            .toMillis();
        if (Files.exists(classFile) && Files.getLastModifiedTime(classFile)
            .toMillis() >= currentModTime && lastModTime >= currentModTime) {
            return;
        }

        // Compile the Java file
        boolean compilationSuccessful = compile(sourceFile);

        if (compilationSuccessful) {
            // Compilation successful, reload the class
            reloadClass(className);
            lastModified.put(className, currentModTime);
            System.out.println("Reloaded: " + className);
        } else {
            System.err.println("Compilation failed for: " + className);
        }
    } catch (Exception e) {
        System.err.println("Error in compile and reload: " + e.getMessage());
        e.printStackTrace();
    }
}
```

The `compile` method is designed to compile a Java source file utilizing the Java Compiler API, providing a structured approach to dynamic compilation. The method begins by accepting a `Path` object that represents the source file intended for compilation. To facilitate error handling and reporting, it creates a `DiagnosticCollector`, which collects diagnostic information such as errors and warnings encountered during the compilation process.

Next, the method constructs a collection of `JavaFileObject`s from the source file using a `fileManager`, likely an instance of `StandardJavaFileManager`. This setup is crucial for the Java Compiler API to access the source files correctly. The method then specifies compilation options, particularly the output directory for the compiled classes, using the `-d` option.

A `CompilationTask` is created using the Java Compiler API, which is configured with several parameters: no custom writer for compiler output (set to null), the file manager, the diagnostic collector, the compilation options, no annotation processors (also null), and the compilation units (the source files). The compilation task is executed by calling `task.call()`, which returns a boolean indicating whether the compilation was successful.

In the event of a compilation failure, the method iterates through the collected diagnostics, printing detailed error information that includes the line number where the error occurred, the source file URI, and the specific error message. This detailed reporting aids in debugging compilation issues effectively. Finally, the method returns the success status of the compilation, providing a clear indication of the outcome.

This method provides a programmatic solution for compiling Java source files, making it particularly useful in scenarios requiring dynamic compilation. Its robust error reporting capabilities enhance the developer's ability to troubleshoot and resolve compilation errors efficiently.

```java
private boolean compile(Path sourceFile) {

    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

    Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Collections.singletonList(sourceFile.toFile()));

    List<String> options = Arrays.asList("-d", classDir.toString());

    JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits);

    boolean success = task.call();

    if (!success) {

        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {

            System.err.format("Error on line %d in %s%n", diagnostic.getLineNumber(), diagnostic.getSource()

                .toUri());

            System.err.println(diagnostic.getMessage(null));

        }

    }

    return success;

}
```

### Custom Class Loader

The program uses a custom class loader to load the compiled class files into the JVM. Reloading happens after the compilation succeeds, and it reloads the class. Implement a custom class loader to load the modified class and execute it.  

```java
private void reloadClass(String className) {

    try {

        Class << ? > loadedClass = classLoader.loadClass(className);

        Method mainMethod = loadedClass.getMethod("main", String[].class);

        System.out.println("Invoking main method for: " + className);

        mainMethod.invoke(null, (Object) new String[0]);

    } catch (Exception e) {

        System.err.println("Error reloading class " + className + ": " + e.getMessage());

        e.printStackTrace();

    }

}

private class CustomClassLoader extends ClassLoader {

@Override

public Class << ? > loadClass(String name) throws ClassNotFoundException {

    Path classFile = classDir.resolve(name + ".class");

        if (Files.exists(classFile)) {

            try {

                byte[] classBytes = Files.readAllBytes(classFile);

                return defineClass(name, classBytes, 0, classBytes.length);

             } catch (IOException e) {

                throw new ClassNotFoundException("Error loading class " + name, e);

            }

         }

        return super.loadClass(name);

    }

}
```

Now if we want to execute the Hot Reload program, we need to do something like this

```java
    public static void main(String[] args) {

        Day84 reloader = new Day84("C:\\Users\\mohib\\IdeaProjects\\date-time\\src\\main\\java", "C:\\Users\\mohib\\IdeaProjects\\date-time\\target\\classes");

        reloader.start();

        // Keep the main thread alive and handle user input

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Live reloader is running. Press Enter to exit.");

            reader.readLine();

        } catch (IOException e) {

            System.err.println("Error reading user input: " + e.getMessage());

        }

        System.out.println("Exiting live reloader.");

    }
```

In this main method, we can see that we need to provide the path for the src of the code that you want to hot reload and the classes of those java files; after that, it then watches for any changes and then compiles and reloads the class so that we can see the effect immediately.

Total code

```java
import javax.tools.*;

import java.io.*;

import java.lang.reflect.Method;

import java.nio.file.*;

import java.util.*;

import java.util.concurrent.ConcurrentHashMap;

public class Day84 {

    private final Path sourceDir;

    private final Path classDir;

    private final Map<String, Long> lastModified = new ConcurrentHashMap<>();

    private final CustomClassLoader classLoader;

    private final JavaCompiler compiler;

    private final StandardJavaFileManager fileManager;

    public Day84(String sourceDir, String classDir) {

        this.sourceDir = Paths.get(sourceDir)

            .toAbsolutePath();

        this.classDir = Paths.get(classDir)

            .toAbsolutePath();

        this.classLoader = new CustomClassLoader();

        this.compiler = ToolProvider.getSystemJavaCompiler();

        this.fileManager = compiler.getStandardFileManager(null, null, null);

        // Ensure directories exist

        try {

            Files.createDirectories(this.sourceDir);

            Files.createDirectories(this.classDir);

        } catch (IOException e) {

            throw new RuntimeException("Failed to create directories", e);

        }

    }

    public void start() {

        Thread watchThread = new Thread(this::watchForChanges, "FileWatcherThread");

        watchThread.setDaemon(true);

        watchThread.start();

        System.out.println("Live reloader started. Watching directory: " + sourceDir);

    }

    private void watchForChanges() {

        try (WatchService watchService = FileSystems.getDefault()

            .newWatchService()) {

            sourceDir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

            while (true) {

                WatchKey key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents()) {

                    if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {

                        Path changed = sourceDir.resolve((Path) event.context());

                        String fileName = changed.getFileName()

                            .toString();

                        if (fileName.endsWith(".java")) {

                            compileAndReload(fileName);

                        }

                    }

                }

                if (!key.reset()) {

                    break;

                }

            }

        } catch (Exception e) {

            System.err.println("Error in file watcher: " + e.getMessage());

            e.printStackTrace();

        }

    }

    private void compileAndReload(String fileName) {

        try {

            String className = fileName.replace(".java", "");

            Path sourceFile = sourceDir.resolve(fileName);

            Path classFile = classDir.resolve(className + ".class");

            // Check if the file has been modified since last compilation

            long lastModTime = lastModified.getOrDefault(className, 0L);

            long currentModTime = Files.getLastModifiedTime(sourceFile)

                .toMillis();

            if (Files.exists(classFile) && Files.getLastModifiedTime(classFile)

                .toMillis() >= currentModTime && lastModTime >= currentModTime) {

                return;

            }

            // Compile the Java file

            boolean compilationSuccessful = compile(sourceFile);

            if (compilationSuccessful) {

                // Compilation successful, reload the class

                reloadClass(className);

                lastModified.put(className, currentModTime);

                System.out.println("Reloaded: " + className);

            } else {

                System.err.println("Compilation failed for: " + className);

            }

        } catch (Exception e) {

            System.err.println("Error in compile and reload: " + e.getMessage());

            e.printStackTrace();

        }

    }

    private boolean compile(Path sourceFile) {

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Collections.singletonList(sourceFile.toFile()));

        List<String> options = Arrays.asList("-d", classDir.toString());

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits);

        boolean success = task.call();

        if (!success) {

            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {

                System.err.format("Error on line %d in %s%n", diagnostic.getLineNumber(), diagnostic.getSource()

                    .toUri());

                System.err.println(diagnostic.getMessage(null));

            }

        }

        return success;

    }

    private void reloadClass(String className) {

        try {

            Class<?> loadedClass = classLoader.loadClass(className);

            Method mainMethod = loadedClass.getMethod("main", String[].class);

            System.out.println("Invoking main method for: " + className);

            mainMethod.invoke(null, (Object) new String[0]);

        } catch (Exception e) {

            System.err.println("Error reloading class " + className + ": " + e.getMessage());

            e.printStackTrace();

        }

    }

    private class CustomClassLoader extends ClassLoader {

        @Override

        public Class<?> loadClass(String name) throws ClassNotFoundException {

            Path classFile = classDir.resolve(name + ".class");

            if (Files.exists(classFile)) {

                try {

                    byte[] classBytes = Files.readAllBytes(classFile);

                    return defineClass(name, classBytes, 0, classBytes.length);

                } catch (IOException e) {

                    throw new ClassNotFoundException("Error loading class " + name, e);

                }

            }

            return super.loadClass(name);

        }

    }

    public static void main(String[] args) {

        Day84 reloader = new Day84("C:\\Users\\mohib\\IdeaProjects\\date-time\\src\\main\\java", "C:\\Users\\mohib\\IdeaProjects\\date-time\\target\\classes");

        reloader.start();

        // Keep the main thread alive and handle user input

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Live reloader is running. Press Enter to exit.");

            reader.readLine();

        } catch (IOException e) {

            System.err.println("Error reading user input: " + e.getMessage());

        }

        System.out.println("Exiting live reloader.");

    }

}
```

## Conclusion

Hot Class Reload in Java offers a powerful way to enhance development productivity by reducing the need for full application restarts.

By integrating file watching, dynamic compilation, and custom class loading, developers can achieve a seamless development experience akin to JavaScript's Hot Module Reload.

Implementing HCR can significantly speed up the development cycle, allowing for rapid testing and iteration.

As you integrate this technique into your workflow, you'll find it an invaluable tool for efficient Java development.

There are some libraries, like JRebel, which provide this feature more on that in the next post!
