---
title: "What are Hidden Classes in Java 15 And How Are They Used?"
slug: "what-are-hidden-classes-in-java-15"
date: "2020-12-03T10:15:18+00:00"
lastmod: "2021-08-23T15:16:10+00:00"
description: "Hidden classes allow frameworks to define classes as non-discoverable implementation details, so that they cannot be linked against. Read on!"
canonical: "https://jfeatures.com/blog/HiddenClass"
authors:
  - "vipin-sharma"
image: "https://jfeatures.com/img/ebook_upd.png"
categories:
  - "JEPs"
  - "Tutorials"
tags:
related_posts:
enlighterjs: true
frozen: false
---

As we know, `sun.misc.Unsafe` APIs are not recommended to use outside the JDK, with a slight mistake you may cause the JVM to crash. In some cases, code may not be portable across different platforms and many other related problems may occur.

On the other hand, there are some useful features Unsafe APIs provide and we don't have an alternative for those as a standard language feature. To remove Unsafe API usages, JDK developers provide standard language features, the JDK 15 "hidden class" is one such feature. After the introduction of hidden classes, `sun.misc.Unsafe::defineAnonymousClass` is deprecated in Java 15, which will be removed in future releases.

Classes that cannot be used directly by the bytecode of other classes are hidden classes. Hidden classes allow frameworks/JVM languages to define classes as non-discoverable implementation details, so that they ***cannot*** be linked against by other classes.

The following properties of the hidden class help us understand it better.

1. cannot be named as a supertype
2. cannot be a declaring field type
3. cannot be the parameter type or the return type
4. cannot be found by classloader via `Class::forName, ClassLoader::loadClass,`  
   `Lookup::findClass` etc.

### Why We Need Hidden Classes {#h3-0-why-we-need-hidden-classes}

Framework/language implementors usually intend for a dynamically generated class to be logically part of the implementation of a statically generated class.

The following properties are desirable for dynamically generated classes.

1. **Non-discoverability.** Dynamically generated classes should not be discoverable by other classes in the JVM, e.g., using `Class::forName` and `ClassLoader::loadClass`.
2. **Lifecycle.**Dynamically generated classes may only be needed for a limited time, so retaining them for the lifetime of the statically generated class might unnecessarily increase memory footprint. Existing workarounds for this situation, such as per-class class loaders, are cumbersome and inefficient.
3. **Access Control.** It may be desirable to extend the [access control context](https://openjdk.java.net/jeps/181) of the statically generated class to include the dynamically generated class.

Existing standard APIs `ClassLoader::defineClass` and `Lookup::defineClass` always define a visible/discoverable class and in this way classes have a longer lifecycle than desired. Hidden classes are dynamically generated and have all the above 3 features desired by Framework/Language implementors.

### How to Write Hidden Classes {#h3-1-how-to-write-hidden-classes}

The hidden class is created by invoking `Lookup::defineHiddenClass`. It causes the JVM to derive a hidden class from the supplied bytes, links the hidden class, and returns a Lookup object that provides reflective access to the hidden class.

The following are 4 steps for creating and using hidden classes.

1. **Create Lookup object.** Get a lookup object, which will be used to create hidden class in the next steps.

   ```java
   MethodHandles.Lookup lookup = MethodHandles.lookup();
   ```

2. **Create class bytes using ASM.** We are using the byte code manipulation library [ASM](https://asm.ow2.io/). We create ClassWriter object using helper class [GenerateClass](https://github.com/Vipin-Sharma/JDK15Examples/blob/ec60c39c786ac93a77185f99dbcaf3f96e56bd7c/src/main/java/com/vip/jfeatures/jdk15/hiddenclass/GenerateClass.java#L16). If you look at the details in `GenerateClass`, this class implements interface `Test`, which we will use in further steps.

   ```java
   `ClassWriter cw = GenerateClass.getClassWriter(HiddenClassDemo.class);
   byte[] bytes = cw.toByteArray();` 
   ```

3. **Define hidden class.** In this step, we are creating a hidden class. It is important to note the invoking program should store the lookup object carefully since it is the only way to obtain the `Class` object of the hidden class.

   ```java
   `Class<?> c = lookup.defineHiddenClass(bytes, true, NESTMATE).lookupClass();`
   ```

4. **Use hidden class.** In this step, we are using reflection to access the hidden class. First, create the constructor, then create an object using this, typecast this object to interface `Test` and call method `test`. This method ignores the argument passed and prints "Hello test" to the console.

   ```java
   `Constructor<?> constructor = c.getConstructor(null);
   Object object = constructor.newInstance(null);
   Test test = (Test) object;
   test.test(new String[]{""});`
   ```

Below is the overall structure of the code we discussed above.

The complete code is available in my GitHub [repo](https://github.com/Vipin-Sharma/JDK15Examples).

```java
public class HiddenClassDemo {
   public static void main(String[] args) throws Throwable {
     MethodHandles.Lookup lookup = MethodHandles.lookup();
     ClassWriter cw =
          GenerateClass.getClassWriter(HiddenClassDemo.class);
     byte[] bytes = cw.toByteArray();

     Class<?> c = lookup.defineHiddenClass(bytes, true, NESTMATE).lookupClass();
     Constructor<?> constructor = c.getConstructor(null);
     Object object = constructor.newInstance(null);
     Test test = (Test) object;
     /* This way of creating instance is deprecated.
       Test test = (Test)c.newInstance();
     */
     test.test(new String[]{"sample"});

     System.out.println("End of main method in class " + HiddenClassDemo.class.getName());
   }
}
```

```java
public interface Test {
  void test(String[] args);
}
```

```java
public static ClassWriter getClassWriter(Class<HiddenClassDemo> ownerClassName) {
  ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

  cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, getHiddenClassName(ownerClassName),
      null, "java/lang/Object", new String[] {"com/vip/jfeatures/jdk15/hiddenclass/Test"});
  ...
  ...
  ...
     MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "test",
         "([Ljava/lang/String;)V", null, null);
     mv.visitCode();
     mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
     mv.visitLdcInsn("Hello test");
     mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");

     mv.visitInsn(RETURN);
     mv.visitMaxs(2, 1);
     mv.visitEnd(); 
}
```

### Hidden Classes as Unsafe Alternative {#h3-2-hidden-classes-as-unsafe-alternative}

Before Java15, non-standard API `Unsafe::defineAnonymousClass` was used to generate dynamic classes. We know Unsafe APIs are not recommended, hidden classes are the right approach to generate dynamic classes now.

Before migrating from `Unsafe::defineAnonymousClass` to `Lookup::defineHiddenClass` (hidden classes) we  

need to be aware of the following constraints:

1. Unlike `Unsafe::defineAnonymousClass,` hidden classes do not support constant-pool patching. [++This++](https://mail.openjdk.java.net/pipermail/valhalla-dev/2020-November/008251.html) is one recent thread showing progress on the enhancement.
2. VM-anonymous classes (created using `Unsafe::defineAnonymousClass`) can be optimized by Hotspot JVM by annotation @ForceInline. This optimization is not available for hidden classes.
3. A VM-anonymous class can access protected members of its host class even if the VM-anonymous class exists in a different run-time package and is not a subclass of the host class. A hidden class can only access protected members of another class if the hidden class is in the same run-time package as, or a subclass of, the other class. There is no special access for a hidden class to the protected members of the lookup class.

Due to these constraints in Java 15, hidden classes are not a complete replacement for `Unsafe::defineAnonymousClass.`

The best example of hidden class usages is lambda expressions in the JDK code. JDK developers don't want to expose classes generated by lambda expressions so `javac` is not translating lambda expressions into dedicated classes, it generates bytecode that dynamically generates and instantiates a class to yield an object corresponding to the lambda expression when needed. Before Java 15 `Unsafe::defineAnonymousClass` was used in the lambda expression implementation, now it is migrated to use hidden classes.

### Conclusion {#h3-3-conclusion}

Knowing language features like this helps you get the best java jobs, that's why to help you I wrote the e-book [5 steps to Best Java Jobs](https://jfeatures.com/).

Download this step by step guide for free!

[![](https://jfeatures.com/img/ebook_upd.png)](https://jfeatures.com/)

### Resources {#h3-4-resources}

1. [JEP for hidden classes in JDK15](https://openjdk.java.net/jeps/371).
2. [Code examples used in this post](https://github.com/Vipin-Sharma/JDK15Examples).
3. [Creating proxies using hidden class](https://github.com/forax/hidden_proxy).
