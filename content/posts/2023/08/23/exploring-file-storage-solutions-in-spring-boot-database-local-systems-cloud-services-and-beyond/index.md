---
title: "Exploring File Storage Solutions in Spring Boot"
date: "2023-08-23T08:07:19+00:00"
lastmod: "2023-08-23T08:13:32+00:00"
description: "In this article, we will cover how to store files in a database using Spring Boot and discuss some alternatives."
authors:
  - "bazlur-rahman"
image: "cd03deb1-489d-4867-9b5b-2ffde99a3e20.jpeg"
categories:
  - "Spring"
  - "Tutorials"
tags:
related_posts:
  - "a-simple-service-with-spring-boot"
  - "annotation-free-spring"
  - "better-error-handling-for-your-spring-boot-rest-apis"
  - "minimize-costs-by-utilizing-cloud-storage-with-spring-data-eclipse-store"
frozen: false
---

**When building a web application, managing file uploads properly is a common requirement. After receiving, files can be stored in several places: in a file system, in a database or, more commonly, in a cloud storage service.**

**In this article, we will cover how to store files in a database using Spring Boot and discuss some alternatives.**

## **Introduction**

Recently, a fellow developer who I helped in past reached out to me on Slack seeking assistance with handling file uploads in a Spring Boot application. After providing some guidance, I decided to compile this article to assist others facing similar challenges.

When dealing with file data, storing these files in your database as Binary Large Objects, or BLOBs, is one option. However, it's important to understand that this approach comes with drawbacks, including potentially significant impacts on performance. Therefore, if your project deals with large files or a large number of files, using a dedicated file storage system is recommended.

That being said, if you're building a small-scale application or have specific requirements that warrant the use of a database for file storage, this approach can work.

## **Process Overview for Database File Storage**

### **Step 1: Entity Class**

To begin, we create an Entity class. This class represents the data that we will store in the database. An example Document entity with fields name, type, and data might look like this:

```

```java
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String type;

    @Lob
    @Column(length = Integer.MAX_VALUE)
    private byte[] data;

    // constructors
    public Document() {}

    public Document(String name, String type, byte[] data) {
        this.name = name;
        this.type = type;
        this.data = data;
    }

    // getters and setters
}
```

```

In this class, **@Lob** denotes that the data attribute should be stored as a **BLOB** in the database.  

The equivalent MySQL table for the Document entity class would look something like this:

```

```sql
create table files.document
(
    id   bigint auto_increment
        primary key,
    name varchar(255) null,
    type varchar(255) null,
    data longblob     null
);
```

```

**data LONGBLOB:**This creates a data column using the LONGBLOB datatype, which can hold a BLOB (Binary Large Object) of data of up to 4GB.

MySQL provides three main types of BLOB data types for storing binary data: **TINYBLOB** , **BLOB** , and **LONGBLOB** . **TINYBLOB** is suitable for small data, with a maximum size of 255 bytes. **BLOB** extends this to 64 KB, accommodating medium-sized binary data like images. **LONGBLOB**, with a maximum size of 4 GB, is used for large binary files such as videos.

Selecting the right BLOB type depends on the specific size requirements of the binary data in your application, balancing storage space, performance, and the nature of the data you are handling.  

In the Document class, I've added **@Column** annotation to the data variable and set its length to I**nteger.MAX_VALUE,** which is the maximum limit in Java for an integer and usually ample storage for a byte array field.

Please ensure that your MySQL database can indeed support that maximum size. If not, you may have to reconfigure your MySQL settings or consult with your DBA.

### **Step 2: Repository Class**

Next, we create a Repository interface extending JpaRepository. This gives us a variety of standard methods for CRUD operations that we can use with our Document entities.

```

```java
public interface DocumentRepository extends JpaRepository<Document, Long> {
}
```

```

### Step 3: Service Class

```

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final DocumentRepository documentRepository;

    public void saveFileInDatabase(MultipartFile file) throws IOException {
        Document doc = new Document(file.getOriginalFilename(), file.getContentType(), file.getBytes());
        documentRepository.save(doc);
    }

}
```

```

### Step 4: Controller Class

```

```java
@Slf4j
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileUploadController {
    private final FileUploadService fileUploadService;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("File name: {}", file.getOriginalFilename());

        fileUploadService.saveFileInDatabase(file);

        return "File uploaded successfully";
    }
}
```

```

When the user sends an HTTP POST request to upload a file, the uploadFile method will be triggered, which creates a new Document object with the file's details and data.

It then uses the fileUploadService.saveFileInDatabase(file) **;** to persist this object in the database.

## **Alternatives to Database File Storage**

While storing files in a database can work for some cases, it's not suitable for all. Here are a couple of alternatives:

### **Local or Network File Systems**

You can write your files to your server's local file system. While this can be viable for smaller applications, it does not scale well as your application grows.

Here's what saving a file to the local file system might look like

```

```java
public void saveFileInFileSystem(MultipartFile file) throws IOException {
    log.info("Uploading file to local file system: {}", file.getOriginalFilename());

    if (!Files.exists(rootPath)) {
        Files.createDirectories(rootPath);
    }

    try (InputStream inputStream = file.getInputStream()) {
        String filenameWithExtension = Paths.get(file.getOriginalFilename()).getFileName().toString();
        Path path = rootPath.resolve(filenameWithExtension);
        Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
    }
}
```

```

Now run the application and do curl:

```

```bash
curl -X POST -H 'Content-Type: multipart/form-data' -F 'file=@/home/uses/uploads/_cd03deb1-489d-4867-9b5b-2ffde99a3e20.jpeg http://localhost:8080/files/upload
```

```

### **Cloud Storage Services**

Services like Amazon S3, Google Cloud Storage, and Azure Blob Storage are built for storing and retrieving any amount of data from anywhere.

These services provide durability, security, and performance for your applications. Here's an example of how you might upload a file to Amazon S3 using the AWS SDK for Java:

```

```java
public void uploadFileToS3(MultipartFile multipartFile) throws IOException {
    log.info("Uploading file to s3: {}", multipartFile.getOriginalFilename());
    var s3Client = getS3Client();

    var metadata = new ObjectMetadata();
    metadata.setContentLength(multipartFile.getSize());
    var keyName = buildKeyName(multipartFile);
    var results = s3Client.putObject(bucketName, keyName, multipartFile.getInputStream(), metadata);
    if (results != null && StringUtils.isNotBlank(results.getContentMd5())) {
        log.info("File uploaded successfully to s3: {}", multipartFile.getOriginalFilename());
    } else {
        log.error("Failed to upload file to s3: {}", multipartFile.getOriginalFilename());
        throw new RuntimeException("Failed to upload file to s3");
    }
}
```

```

### **Content Delivery Network (CDN) Storage**

CDNs are used to serve content to end-users with high availability and high performance. CDNs can also handle file uploads and storage.

An advantage of CDN storage is that files can automatically be distributed and cached close to the end user, which can greatly reduce download times if you have a geographically diverse user base.

### **Object Storage**

Object storage provides an optimized solution for storing large amounts of data, each as a discrete unit or "object". Amazon S3 and Google Cloud Storage are examples of object storage services.

Unlike files in a file system, there are no folders or hierarchy in object storage, and each object is accompanied by metadata that includes the object's unique identifier.

This design allows for great scalability and cost-effectiveness when dealing with massive amounts of unstructured data.

### **Block Storage**

Block storage divides data into standardized chunks called "blocks", each with its own address but without any additional metadata.

This method is often used for databases or file systems and is suitable for editable data scenarios because individual blocks can be read or written independently.

Popular Block Storage providers include Amazon EBS and Google Persistent Disk.

### **Distributed File Systems (e.g., Hadoop HDFS, GlusterFS)**

In a distributed file system, the data is stored across multiple servers yet appears to the user as a single cohesive file system.

Distributed file systems can handle huge amounts of data and are designed to be highly fault-tolerant. However, the setup and management of such systems can be relatively complex.

### **Managed File Storage Services (e.g., Google Drive API, Dropbox API)**

These services provide built-in file storage, organization, and security and can be accessed via APIs to integrate them into your application seamlessly.

The advantage of these services is that they offload much of the work of file management and allow you to leverage their well-designed interfaces and organizational structures.

## Pros and Cons

Storing files in a database offers consistency and simplicity but can lead to performance and scalability issues. Local and network file systems provide performance benefits but struggle with scalability and data integrity. Cloud storage services offer scalability and performance but might be costly for small applications. Content Delivery Networks (CDNs) enhance performance but at a cost.

Object storage offers scalability and affordability but may lack in performance, while block storage provides performance at a higher price. Distributed file systems bring scalability and fault tolerance but are complex to manage. Managed file storage services like Google Drive and Dropbox offer ease of use and security but come with dependence on third-party providers.

Ultimately, the choice of storage depends on the unique requirements of the application, such as volume, performance, budget, and use case.

## Conclusion

In this article, we showed you a simple setup for storing uploaded files in a database using Spring Boot and gave some alternatives.

I haven't included code examples for all the alternatives, as this article would become cumbersome and it very much depends on the API and SDKs of those services; it is recommended to read their documentations.

For a hands-on example demonstrating the database storage approach, you can find the complete code in the [fileupload-demo GitHub repository](https://github.com/rokon12/fileuplod-demo).

Note this setup is not taking into consideration real-world complexities like error handling, input validation, and file management (such as handling duplicate files), which you should implement before deploying your application.

Remember to choose the right storage option that suits not just your application needs now but also your future scalability demands.

Happy coding!
