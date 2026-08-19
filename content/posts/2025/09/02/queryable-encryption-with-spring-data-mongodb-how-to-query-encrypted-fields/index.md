---
title: "Copy of Queryable Encryption with Spring Data MongoDB: How to Query Encrypted Fields"
date: "2025-09-02T19:00:24+00:00"
lastmod: "2025-12-12T21:22:43+00:00"
description: "Information is one of the most valuable assets in computing and keeping it protected is even more critical. When we talk about data protection, it’s not just about preventing breaches or leaks; it’s also about complying with privacy regulations and protecting user data. MongoDB provides strong encryption capabilities, including in transit, at rest, and in use. The Queryable Encryption feature falls into the in use category. It allows data to be encrypted on the client side, so that even with access to the database and its credentials, no one can read the protected fields without the proper encryption key. At the same time, it supports querying over encrypted fields, making it possible to filter, match, or retrieve data without compromising confidentiality. In this tutorial, we’ll implement this feature using Spring Data MongoDB, applying encryption to sensitive fields in a sample human resources (HR) system."
authors:
  - "ricardo-mello"
image: "Favicon-3-2.png"
categories:
  - "Databases"
  - "Java"
  - "Mongo"
  - "Spring"
related_posts:
  - "clean-and-modular-java-a-hexagonal-architecture-approach"
  - "building-a-spring-boot-crud-application-using-mongodbs-relational-migrator"
  - "domain-driven-design-in-java-a-practical-guide"
  - "how-to-identify-the-underlying-causes-of-connection-timeout-errors-for-mongodb-with-java"
frozen: false
---

Information is one of the most valuable assets in computing and keeping it protected is even more critical. When we talk about data protection, it's not just about preventing breaches or leaks; it's also about complying with privacy regulations and protecting user data.

MongoDB provides strong encryption capabilities, including in transit, at rest, and in use. The Queryable Encryption feature falls into the *in use* category. It allows data to be encrypted on the client side, so that even with access to the database and its credentials, no one can read the protected fields without the proper encryption key. At the same time, it supports querying over encrypted fields, making it possible to filter, match, or retrieve data without compromising confidentiality.

In this tutorial, we'll implement this feature using Spring Data MongoDB, applying encryption to sensitive fields in a sample human resources (HR) system.

## Why Queryable Encryption?

Imagine a common scenario in HR systems: You receive a regulatory requirement to protect employee data using encryption. At first, encrypting fields seems enough. But then comes a second requirement—you also need to search over that encrypted data.

That's exactly where [Queryable Encryption](https://www.mongodb.com/docs/v7.0/core/queryable-encryption/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=query-foojay&utm_term=tony.kim) comes in. MongoDB currently supports several query types over encrypted fields, including Equality, Range, Prefix, and Suffix.

To implement it, we typically follow four steps:

1. Specify which fields should be encrypted.  
2. Define whether each field should be queryable.  
3. Create the encrypted collection with the appropriate encryptedFields configuration.  
4. Perform regular operations: The client handles encryption and decryption transparently.

If you're interested in seeing more advanced details about Queryable Encryption, including how to use it directly with the MongoDB Java Driver and a deeper explanation of how it works, I recommend checking out my other article: [Java Meets Queryable Encryption](https://www.mongodb.com/developer/products/atlas/java-queryable-encryption/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=query-foojay&utm_term=tony.kim).

## A quick look at Spring Data MongoDB

Spring Data MongoDB is a module within the Spring ecosystem that makes it easier to work with MongoDB. It provides a familiar way to interact with MongoDB using the Spring programming model.

Starting with version [4.5.0](https://github.com/spring-projects/spring-data-mongodb/releases/tag/4.5.0), the framework introduced several important changes, including support for Queryable Encryption.

If you're new to using Spring Data with MongoDB or want to explore more advanced use cases, check the [Spring Data Unlocked](https://www.mongodb.com/developer/products/mongodb/springdata-getting-started-with-java-mongodb/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=query-foojay&utm_term=tony.kim) series that walks through these concepts in detail.

## Use case: HR system with encrypted fields

To better understand how Spring Data MongoDB works with Queryable Encryption, we'll build a simple Java application for an HR system. This application will use a document model like the one below:

```
{

   "name": "Ricardo",

   "pin": "001",

   "ssn": 223,

   "age": 36,

   "salary": 1000.50

}
```

In this scenario, fields such as pin, ssn, age, and salary will be encrypted using the new annotations introduced in Spring Data MongoDB 4.5, including @Encrypted, @Queryable, and @RangeEncrypted.

We're not only encrypting sensitive information, but also making it searchable, with support for both:

* Equality queries: find an employee by ssn  
* Range queries: filter employees by age or salary  

To keep things simple and practical, our application will expose four basic endpoints:

* Create a new employee  
* Retrieve all employees  
* Find an employee by SSN  
* Filter employees by age or salary range  

At the end, when we open a document in [MongoDB Compass](https://www.mongodb.com/products/tools/compass/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=query-foojay&utm_term=tony.kim), we'll see encrypted fields as unreadable binary blobs, only decryptable by the client configured with the proper keys:

{{< img src="Screenshot-2025-08-29-at-8.47.45-AM.png" class="size-full is-resized" alt="json-block-1" width="535" height="278" style="width:535px;height:auto" >}}

## Setting up the project

TL;DR

*If you just want to jump straight into the code, you can find the full project on* [*GitHub*](https://github.com/mongodb-developer/spring-data-queryable-encryption)*. It includes all the setup needed to run Queryable Encryption with Spring Data MongoDB.*

Let's start by creating our Spring Boot project using the official[Spring Initializr](https://start.spring.io/). This tool allows us to quickly generate a base project with the dependencies we need.

For this demo, we'll add the following dependencies:

* Spring Web, to expose our REST endpoints  
* Spring Data MongoDB, to interact with our MongoDB database  

![](Screenshot-2025-08-29-at-8.48.43-AM.png)

Configure the project properties as shown in the image, then generate and unzip the project to get started.

## Configuring dependencies and properties

### Adding mongodb-crypt

The first thing we need to do is include the mongodb-crypt library in our project. This library is essential when working with Queryable Encryption in Java applications, handling the low-level cryptographic operations required by the MongoDB driver. Open the pom.xml and include the following dependency:

```
<dependency>

   <groupId>org.mongodb</groupId>

   <artifactId>mongodb-crypt</artifactId>

   <version>5.5.1</version>

</dependency>
```

### Application.yml configuration

Now, let's open the application.yml file (or create one if it doesn't exist) and define the following values:

```
app:

 mongodb:

   uri: ${MONGODB_URI}

   cryptSharedLibPath: ${CRYPT_PATH}

   keyVaultNamespace: encryption.__keyVault

   encryptedDatabaseName: hrsystem

   encryptedCollectionName: employees

logging:

 level:

   org.springframework.data.mongodb: DEBUG
```

Alright, we can see some familiar settings here, like the uri, which defines the connection string to MongoDB, and the database/collection names where our encrypted data will live (hrsystem and employees).

But two properties stand out and deserve a quick explanation:

* keyVaultNamespace: This tells MongoDB where to store and retrieve the encryption keys. In our case, they'll be saved in the __keyVault collection inside the encryption database.  
* cryptSharedLibPath: This points to the native cryptographic library that handles encryption and decryption on the client side. Without this, the app can't use Queryable Encryption.

To resolve this library dependency, we need to [download](https://www.mongodb.com/docs/v6.0/core/queryable-encryption/reference/shared-library/#download-the-automatic-encryption-shared-library&utm_source=third-party-content%20&utm_medium=cta%20&utm_content=spring_data_with_queryable_encryption%20&utm_term=ricardo.mello) the automatic encryption shared libraryc and save it somewhere to use later.

### Accessing properties in the code

Now that we've defined our configuration values in the application.yaml file, we need a way to access them inside our application. To do this, create a simple class:

```
@Component

@ConfigurationProperties(prefix = "app.mongodb")

public class AppProperties {

   protected String uri;

   protected String cryptSharedLibPath;

   protected String keyVaultNamespace;

   protected String encryptedDatabaseName;

   protected String encryptedCollectionName;

   // getters and setters

}
```

Note: Don't forget to generate getters and setters for all the fields in the AppProperties class.

## Building the application layers

### The domain model

Now, let's define the data we'll be working with throughout the project. We'll use a simple Employee record to represent an employee in our HR system:

```
@Document(collection = "employees")

public record Employee(

       @Id

       String id,

       String name,

       @Encrypted

       String pin,

       @Queryable(queryType = "equality")

       @Encrypted

       int ssn,

       @RangeEncrypted(

               contentionFactor = 0L,

               rangeOptions = "{\"min\": 0, \"max\": 150}"

       )

       Integer age,

       @RangeEncrypted(contentionFactor = 0L,

               rangeOptions = "{\"min\": {\"$numberDouble\": \"1500\"}, \"max\": {\"$numberDouble\": \"100000\"}, \"precision\": 2 }")

       double salary

) {}
```

Here's a quick look at what each encrypted field does:

* pin is encrypted for confidentiality only.  
* ssn is encrypted but also queryable using equality.  
* age and salary use range-based encryption, allowing queries like "find all employees with salary above X" or "age below Y". Among other things, [**rangeOptions**](https://www.mongodb.com/docs/manual/core/queryable-encryption/fundamentals/encrypt-and-query/#configure-encrypted-fields-for-optimal-search-and-storage?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=spring_data_with_queryable_encryption&utm_term=ricardo.mello) define the minimum and maximum values allowed in range queries. Any value outside this range won't match during query execution.

### The repository

To query our encrypted fields, we'll define a few methods in our repository: findBySsn, findByAgeLessThan, and findBySalaryGreaterThan:

```
@Repository

public interface EmployeeRepository extends MongoRepository<Employee, String> {

   Optional<Employee> findBySsn(int ssn);

   List<Employee> findByAgeLessThan(int age);   

   List<Employee> findBySalaryGreaterThan(double salary);

}
```

### The service

Now, let's create a service layer to interact with the repository and handle the business logic of our application:

```
@Service

public class EmployeeService  {

   private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

   private final EmployeeRepository employeeRepository;

   public EmployeeService(EmployeeRepository employeeRepository) {

        this.employeeRepository = employeeRepository;

   }

   public Employee createEmployee(Employee employee) {

       logger.info("Creating employee {}", employee);

        return employeeRepository.save(employee);

    }

   public List<Employee> findAll() {

      logger.info("Finding all employees ");

      return employeeRepository.findAll();

   }

   public Optional<Employee> findBySsn(int ssn) {

       logger.info("Finding employee with ssn equals {}", ssn);

       return employeeRepository.findBySsn(ssn);

   }

   public List<Employee> findByAgeLessThan(int age) {

       Assert.isTrue(age > 0, "Age must be greater than 0");

       Assert.isTrue(age < 150, "Age must be less than 150");

       logger.info("Finding all employees where age is less than {} ", age);

       return employeeRepository.findByAgeLessThan(age);

   }

   public List<Employee> findBySalaryGreaterThan(double salary) {

       Assert.isTrue(salary >= 1500, "Salary must be at least 1500");

       Assert.isTrue(salary < 100000, "Salary must be less than 100000");

       logger.info("Finding all employees where salary is greater than {}", salary);

       return employeeRepository.findBySalaryGreaterThan(salary);

   }

}
```

### The controller

Finally, let's expose our service layer through a REST controller, allowing the application to receive a HTTP request and interact with the encrypted data:

```
@RestController

@RequestMapping("/employees")

public class EmployeeController {

   private final EmployeeService employeeService;

   public EmployeeController(EmployeeService employeeService) {

        this.employeeService = employeeService;

   }

   @PostMapping

   public ResponseEntity<Employee> create(@RequestBody Employee employee) {

       return ResponseEntity.ok(employeeService.createEmployee(employee));

   }

   @GetMapping

   public ResponseEntity<List<Employee>> findAll() {

       return ResponseEntity.ok(employeeService.findAll());

   }

   @GetMapping("/ssn/{ssn}")

   public ResponseEntity<Employee> findBySsn(@PathVariable int ssn) {

       return employeeService.findBySsn(ssn)

               .map(ResponseEntity::ok)

               .orElseGet(() -> ResponseEntity.notFound().build());

   }

   @GetMapping("/filter/salary-greater-than")

   public ResponseEntity<List<Employee>> findByAgeGreaterThan(@RequestParam double salary) {

       return ResponseEntity.ok(employeeService.findBySalaryGreaterThan(salary));

   }

   @GetMapping("/filter/age-less-than")

   public ResponseEntity<List<Employee>> findByAgeLessThan(@RequestParam int age) {

       return ResponseEntity.ok(employeeService.findByAgeLessThan(age));

   }

}
```

## Setting up encryption

Before we can start encrypting data, we need a [Customer Master Key (CMK)](https://www.mongodb.com/docs/manual/core/queryable-encryption/qe-create-cmk/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=query-foojay&utm_term=tony.kim), a secret key that serves as the base for encrypting other keys used by the database.

In real-world applications, this key is usually managed by a secure provider like AWS KMS, Azure Key Vault, or Google Cloud KMS, which offer strong protection and lifecycle management.

But to keep things simple in this example, we'll generate and store the CMK locally, in a file inside the project.

Let's create a utility class called LocalCMKService to handle this process. It checks if the key file exists, creates it if needed, and loads the key into memory so it can be used when configuring the MongoDB client:

```
@Service

public class LocalCMKService {

   private static final String CUSTOMER_KEY_PATH = "src/main/resources/my-key.txt";

   private static final int KEY_SIZE = 96;

   private boolean isCustomerMasterKeyFileExists() {

       return new File(CUSTOMER_KEY_PATH).isFile();

   }

   private void create() throws IOException {

       byte[] cmk = new byte[KEY_SIZE];

       new SecureRandom().nextBytes(cmk);

       try (FileOutputStream stream = new FileOutputStream(CUSTOMER_KEY_PATH)) {

           stream.write(cmk);

       } catch (IOException e) {

           throw new IOException("Unable to write Customer Master Key file: " + e.getMessage(), e);

       }

   }

  private byte[] read() throws IOException {

       byte[] cmk = new byte[KEY_SIZE];

       try (FileInputStream fis = new FileInputStream(CUSTOMER_KEY_PATH)) {

           int bytesRead = fis.read(cmk);

           if (bytesRead != KEY_SIZE) {

               throw new IOException("Expected the customer master key file to be " + KEY_SIZE + " bytes, but read " + bytesRead + " bytes.");

           }

       } catch (IOException e) {

           throw new IOException("Unable to read the Customer Master Key: " + e.getMessage(), e);

       }

       return cmk;

   }

  public Map<String, Map<String, Object>> getKmsProviderCredentials() throws IOException {

       try {

           if (!isCustomerMasterKeyFileExists()) {

               create();

           }

           byte[] localCustomerMasterKey = read();

           Map<String, Object> keyMap = new HashMap<>();

           keyMap.put("key", localCustomerMasterKey);

           Map<String, Map<String, Object>> kmsProviderCredentials = new HashMap<>();

           kmsProviderCredentials.put("local", keyMap);

           return kmsProviderCredentials;

       }catch (Exception e) {

           throw new IOException("Unable to read the Customer Master Key file: " + e.getMessage(), e);

       }

   }

}
```

## Configuring the MongoDB encryption layer

To bring everything together, let's now create the MongoEncryptionConfiguration class. This is where we configure our MongoDB client to support Queryable Encryption, define how keys are loaded, and ensure our encrypted collection is created at startup:

```
@Configuration

public class MongoEncryptionConfiguration implements ApplicationRunner {

   private final AppProperties appProperties;

   private final LocalCMKService localCMKService;

   MongoEncryptionConfiguration(LocalCMKService localCMKService, AppProperties appProperties) {

       this.localCMKService = localCMKService;

       this.appProperties = appProperties;

   }

   @Override

   public void run(ApplicationArguments args) throws Exception {

      // TODO 

   }

}
```

### Defining the encryption configuration

Great! Now, in the same **MongoEncryptionConfiguration** class, let's start adding a few methods. First, we configure the path to the native mongodb_crypt library we downloaded earlier:

```
private Map<String, Object> createExtraOptions() {

   Map<String, Object> extraOptions = new HashMap<>();

   extraOptions.put("cryptSharedLibPath", appProperties.cryptSharedLibPath);

   return extraOptions;

}
```

Next, we build the AutoEncryptionSettings, which defines the encryption behavior for the client:

```
private AutoEncryptionSettings getAutoEncryptionSettings() throws IOException {

   Map<String, Map<String, Object>> kmsProviderCredentials = localCMKService.getKmsProviderCredentials();

   return AutoEncryptionSettings.builder()

           .keyVaultNamespace(appProperties.keyVaultNamespace)

           .extraOptions(createExtraOptions())

           .kmsProviders(kmsProviderCredentials)

           .build();

}
```

We then use these settings to build the custom MongoClientSettings:

```
private MongoClientSettings getMongoClientSettings() throws IOException {

   return MongoClientSettings.builder()

           .applyConnectionString(new ConnectionString(appProperties.uri))

           .autoEncryptionSettings(getAutoEncryptionSettings())

           .uuidRepresentation(UuidRepresentation.STANDARD)

           .build();

}
```

### Defining Spring beans

Now that our encryption configuration is complete, we can register the two Spring beans:

* MongoClient: configured with the encryption support
* MongoTemplate: used to interact with the encrypted database

```
@Bean

public MongoClient mongoClient() throws IOException {

   return MongoClients.create(getMongoClientSettings());

}

@Bean

MongoOperations mongoTemplate(MongoClient mongoClient) {

   return new MongoTemplate(mongoClient, appProperties.encryptedDatabaseName);

}
```

### The encrypted collection

Instead of manually defining which fields are encrypted, we generate the schema based on our Employee class using Spring's built-in MongoJsonSchemaCreator:

```
private void createCollectionFromSchema(MongoOperations template, ClientEncryption clientEncryption) {

   MongoJsonSchema schema = MongoJsonSchemaCreator.create(new MongoMappingContext())

           .filter(MongoJsonSchemaCreator.encryptedOnly())

           .createSchemaFor(Employee.class);

   Document encryptedFields = CollectionOptions.encryptedCollection(schema)

           .getEncryptedFieldsOptions()

           .map(CollectionOptions.EncryptedFieldsOptions::toDocument)

           .orElseThrow();

   template.execute(db -> clientEncryption.createEncryptedCollection(

           db,

           template.getCollectionName(Employee.class),

           new CreateCollectionOptions().encryptedFields(encryptedFields),

           new CreateEncryptedCollectionParams("local")

   ));

}
```

### Creating the ClientEncryption

To interact with the key vault and create encrypted collections, we need a ClientEncryption instance:

```
private ClientEncryption createClientEncryption() throws IOException {

   var encryptionSettings = ClientEncryptionSettings.builder()

           .keyVaultMongoClientSettings(

                   MongoClientSettings.builder()

                           .applyConnectionString(new ConnectionString(appProperties.uri))

                           .build())

           .keyVaultNamespace(appProperties.keyVaultNamespace)

           .kmsProviders(localCMKService.getKmsProviderCredentials())

           .build();

   return ClientEncryptions.create(encryptionSettings);

}
```

### Initializing the collection on startup

Before the application starts, we want to ensure that the encrypted collection exists. That check happens inside the run() method, which is automatically executed after the Spring Boot application starts. You can now replace the //TODO in the run() method with the following logic:

```
@Override

public void run(ApplicationArguments args) throws Exception {

   var mongoTemplate = mongoTemplate(mongoClient());

   if (!mongoTemplate.collectionExists(appProperties.encryptedCollectionName)) {

       initializeEncryptedCollection(mongoTemplate);

   }

}

This code uses mongoTemplate to check if the collection already exists. If it doesn't, it calls the method below to create it:

private void initializeEncryptedCollection(MongoOperations template) throws IOException {

   try (ClientEncryption clientEncryption = createClientEncryption()) {

       createCollectionFromSchema(template, clientEncryption);

   }

}
```

### Inserting sample data for testing

To wrap things up, let's preload some sample employee data to help us test our encrypted queries. We'll use a simple CommandLineRunner to automatically insert records into the collection when the application starts:

```
@Configuration

public class SampleDataLoader {

   private static final Logger logger = LoggerFactory.getLogger(SampleDataLoader.class);

   @Bean

   public CommandLineRunner loadSampleEmployees(EmployeeRepository employeeRepository) {

       return args -> {

           if (employeeRepository.count() != 0) {

               logger.info("Sample data already exists. Skipping insert");

               return;

           }

           List<Employee> employees = List.of(

                   new Employee(null, "Ricardo", "001", 1, 36, 1501),

                   new Employee(null, "Maria",   "002", 2, 28, 4200),

                   new Employee(null, "Karen",   "003", 3, 42, 2800),

                   new Employee(null, "Mark",    "004", 4, 22, 2100),

                   new Employee(null, "Pedro",   "005", 5, 50, 4000),

                   new Employee(null, "Joana",   "006", 5, 50, 99000)

           );

           employeeRepository.saveAll(employees);

           logger.info("Saved {} employees", employees.size());

       };

   }

}
```

If the collection already contains data, the loader will skip the insertion to avoid duplicates.

## Running the application

Great! With everything in place, it's time to run the app.

Just make sure to pass the MongoDB URI and the path to the cryptographic shared library using environment variables:

```
MONGODB_URI='<YOUR_CONNECTION_STRING>' 
CRYPT_PATH='/path/to/mongo_crypt_shared/lib/mongo_crypt.dylib' mvn spring-boot:run
```

Note: The CRYPT_PATH should point to the full path of the native library you downloaded.

Once the application is running, if everything goes well, you can check the cluster specified in your connection string. Inside the hrsystem database, look for the employees collection—it should already contain six documents with encrypted fields, ready to be queried:
![](Screenshot-2025-08-29-at-9.00.08-AM-1024x414.png)

## Testing the endpoints

With the app running, we can interact with our encrypted data through simple HTTP requests. Here are some examples:

### Create a new employee

Send a new employee to the database. Fields like ssn, age, and salary will be encrypted automatically.

```
POST http://localhost:8080/employees

Content-Type: application/json

{

 "name": "Henrique Silva",

 "pin": "932",

 "ssn": 21,

 "age": 44,

 "salary": 32100

}
```

### Find by ssn

Perform an equality query on an encrypted field.

```
GET http://localhost:8080/employees/ssn/1
```

### Find by age (range)

Return all employees younger than a given age.

```
GET http://localhost:8080/employees/filter/age-less-than?age=50
```

### Find by salary (range)

Return all employees with a salary above a given value.

```
GET http://localhost:8080/employees/filter/salary-greater-than?salary=3500
```

## Conclusion

In this tutorial, we explored how to implement Queryable Encryption using Spring Data MongoDB, step by step, from setting up the project to making encrypted queries work in practice.

We saw how easily Spring and MongoDB fit together: The annotations in the domain model, the auto-generated schema, and the integration with client-side encryption all worked in harmony.

It's not just about encrypting sensitive fields. It's about being able to query them securely and efficiently, without giving up the developer experience we're used to with Spring.

If you have any questions, feel free to reach out to our community:[MongoDB Community Forum](https://www.mongodb.com/community/forums/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=query-foojay&utm_term=tony.kim).

For more on Spring Data MongoDB and the official docs: [Spring Data MongoDB Encryption](https://docs.spring.io/spring-data/mongodb/reference/mongodb/mongo-encryption.html).

To access the full source code of this project:[GitHub repository.](https://github.com/mongodb-developer/spring-data-queryable-encryption)
