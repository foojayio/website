---
title: "Abstracting Data Access in Java With the DAO Pattern"
slug: "abstracting-data-access-in-java-with-the-dao-pattern"
date: "2026-01-29T16:37:06+00:00"
lastmod: "2026-01-29T16:43:24+00:00"
description: "The Data Access Object (DAO) pattern is a structural pattern that isolates your application's business logic from persistence operations. By using an abstract API, the DAO pattern hides all the complexity of performing CRUD operations against your database—whether that's MongoDB, a relational database, or any other storage mechanism. The DAO pattern ensures both layers can evolve independently.In this tutorial written by Tim Kelly published on Friends of OpenJDK (Foojay.io), we'll implement the DAO pattern with MongoDB as our backend. We'll start with a simple in-memory example to understand the core concepts, then build a production-ready implementation using the MongoDB Java Driver. Along the way, you'll see how MongoDB's document model actually makes the DAO pattern more straightforward than with traditional ORMs—no complex entity mappings required."
authors:
  - "tim-kelly"
image: "546a7b8392ade39f6930ae5605b54327b1d73306_2_690x362.png"
categories:
  - "Databases"
  - "Java"
  - "Mongo"
tags:
related_posts:
  - "building-java-microservices-with-the-repository-pattern"
  - "building-rest-apis-in-java-with-spring-boot"
  - "building-systems-that-know-why-they-exist-when-data-logic-and-intent-finally-align"
  - "clean-and-modular-java-a-hexagonal-architecture-approach"
enlighterjs: true
frozen: false
---

The Data Access Object (DAO) pattern is a structural pattern that isolates your application's business logic from persistence operations. By using an abstract API, the DAO pattern hides all the complexity of performing CRUD operations against your database---whether that's MongoDB, a relational database, or any other storage mechanism.

This separation is crucial: Your business logic shouldn't care whether you're using MongoDB's flexible document model or a rigid SQL schema. The DAO pattern ensures both layers can evolve independently.

In this tutorial, we'll implement the DAO pattern with MongoDB as our backend. We'll start with a simple in-memory example to understand the core concepts, then build a production-ready implementation using the MongoDB Java Driver. Along the way, you'll see how MongoDB's document model actually makes the DAO pattern more straightforward than with traditional ORMs---no complex entity mappings required.

A simple implementation {#h2-0-a-simple-implementation}
-------------------------------------------------------

Let's build a basic example to understand how the DAO pattern works. We'll create an inventory management application that tracks products while keeping the domain model completely agnostic about persistence. You can read through, or follow along:

### Prerequisites {#h3-1-prerequisites}

Before starting this tutorial, ensure you have:

* **Java Development Kit (JDK) 17 or higher**: Verify with java -version.
* **Apache Maven 3.9.0 or higher**: Verify with mvn -version.
* **MongoDB Atlas account with a cluster** (M0 free tier works fine): Sign up for free at [MongoDB](https://account.mongodb.com/account/register?utm_campaign=devrel&utm_source=third-part-content&utm_medium=cta&utm_content=data+access+object&utm_term=tim.kelly).
  * **Database user credentials**: Create a user with read/write privileges in your Atlas cluster.
  * **Network access configured**: Allow your IP address to connect to the cluster.
  * **Connection string**: Available from your Atlas cluster's "Connect" button.

Now, you can create a simple maven application in the IDE of your choosing and follow along.

### The domain class {#h3-2-the-domain-class}

First, we need a simple domain class to represent our products:

```
import java.math.BigDecimal;

public class Product {
    private String id;
    private String name;
    private String category;
    private BigDecimal price;
    private int stockQuantity;
    public Product() {
    }

    public Product(String name, String category, BigDecimal price, int stockQuantity) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    // standard getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    @Override
    public String toString() {
        return "Product{id='" + id + "', name='" + name + 
               "', category='" + category + "', price=" + price + 
               ", stock=" + stockQuantity + "}";
    }
}
```


Notice that this is just a plain old Java object (POJO). No annotations, no framework dependencies---just a container for product data. This is exactly what we want: a clean domain model that knows nothing about how it will be persisted.

### The DAO API {#h3-3-the-dao-api}

Now, let's define the DAO interface that will abstract all persistence operations:

```
import java.util.List;
import java.util.Optional;
public interface Dao<T> {
    Optional<T> get(String id);
    List<T> getAll();
    void save(T t);
    void update(T t);
    void delete(String id);
}
```


This interface defines a generic API for CRUD operations on any type T. The beauty of this abstraction is that our application code can work with this interface without knowing anything about the underlying persistence mechanism.

### The ProductDao class {#h3-4-the-productdao-class}

Let's create an in-memory implementation to see the pattern in action:

```
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.math.BigDecimal;

public class ProductDao implements Dao<Product> {
    private List<Product> products = new ArrayList<>();
    public ProductDao() {
        Product laptop = new Product("ThinkPad X1", "Electronics", 
                                     new BigDecimal("1299.99"), 15);
        laptop.setId(UUID.randomUUID().toString());
        products.add(laptop);
        Product desk = new Product("Standing Desk", "Furniture", 
                                   new BigDecimal("499.99"), 8);
        desk.setId(UUID.randomUUID().toString());
        products.add(desk);
        Product chair = new Product("Ergonomic Chair", "Furniture", 
                                    new BigDecimal("349.99"), 12);
        chair.setId(UUID.randomUUID().toString());
        products.add(chair);
    }

    @Override
    public Optional<Product> get(String id) {
        return products.stream()
            .filter(product -> product.getId().equals(id))
            .findFirst();
    }

    @Override
    public List<Product> getAll() {
        return new ArrayList<>(products);
    }

    @Override
    public void save(Product product) {
        if (product.getId() == null) {
            product.setId(UUID.randomUUID().toString());
        }
        products.add(product);
    }

    @Override
    public void update(Product product) {
        products.stream()
            .filter(p -> p.getId().equals(product.getId()))
            .findFirst()
            .ifPresent(existingProduct -> {
                existingProduct.setName(product.getName());
                existingProduct.setCategory(product.getCategory());
                existingProduct.setPrice(product.getPrice());
                existingProduct.setStockQuantity(product.getStockQuantity());
            });
    }

    @Override
    public void delete(String id) {
        products.removeIf(product -> product.getId().equals(id));
    }
}
```


This in-memory implementation uses a simple ArrayList as our "database." In the constructor, we populate it with a few sample products. The key thing to notice here is that all the persistence logic---even if it's just managing a list---is encapsulated within the DAO.

### Using the DAO {#h3-5-using-the-dao}

Here's how an application would use our DAO:

```
import java.math.BigDecimal;
import java.util.List;

public class InventoryApplication {
    private static Dao<Product> productDao;
    public static void main(String[] args) {
        productDao = new ProductDao();

        // Get all products
        System.out.println("Current inventory:");
        List<Product> allProducts = productDao.getAll();
        allProducts.forEach(System.out::println);

        // Update a product's stock
        if (!allProducts.isEmpty()) {
            Product product = allProducts.get(0);
            System.out.println("\nUpdating stock for: " + product.getName());
            product.setStockQuantity(product.getStockQuantity() - 5);
            productDao.update(product);
        }

        // Add a new product
        Product keyboard = new Product("Mechanical Keyboard", "Electronics", 
                                       new BigDecimal("159.99"), 25);
        productDao.save(keyboard);
        System.out.println("\nAdded new product: " + keyboard.getName());

        // Delete a product
        if (allProducts.size() > 1) {
            String idToDelete = allProducts.get(1).getId();
            productDao.delete(idToDelete);
            System.out.println("\nDeleted product with ID: " + idToDelete);
        }

        // Show final inventory
        System.out.println("\nFinal inventory:");
        productDao.getAll().forEach(p -> 
            System.out.println("  " + p.getName() + " - $" + p.getPrice() + 
                             " (Stock: " + p.getStockQuantity() + ")")
        );
    }
}
```


The critical insight here is that InventoryApplication has no idea how products are being stored. It just knows that it can create, read, update, and delete them through the Dao interface. This means we could swap out the in-memory implementation for a database-backed one without changing a single line of application code.

Now, we can run our app:

```
mvn clean compile 

mvn exec:java -Dexec.mainClass="com.mongodb.InventoryApplication"
```


And we should see an output:

Current inventory:

```
Product{id='1d9bf23e-8b7d-49ae-ab2b-995dacec3571', name='ThinkPad X1', category='Electronics', price=1299.99, stock=15}

Product{id='cab93a44-c5c6-4d36-a0a7-73b8536d7748', name='Standing Desk', category='Furniture', price=499.99, stock=8}

Product{id='a5301b84-8104-40fc-912a-c1c710ee58db', name='Ergonomic Chair', category='Furniture', price=349.99, stock=12}

Updating stock for: ThinkPad X1

Added new product: Mechanical Keyboard

Deleted product with ID: cab93a44-c5c6-4d36-a0a7-73b8536d7748

Final inventory:

  ThinkPad X1 - $1299.99 (Stock: 10)

  Ergonomic Chair - $349.99 (Stock: 12)

  Mechanical Keyboard - $159.99 (Stock: 25)
```


Using MongoDB as the persistence layer {#h2-6-using-mongodb-as-the-persistence-layer}
-------------------------------------------------------------------------------------

Let's implement a production-ready DAO using MongoDB. You might wonder: Doesn't MongoDB already provide a clean API through its driver? Why add another layer?

Here's why the DAO pattern still makes sense with MongoDB:

1. **Domain-specific API**: You expose only the operations your application needs, not MongoDB's entire API.
2. **Business logic boundary**: Prevent MongoDB queries and document manipulation from leaking into your service layer.
3. **Testability**: It's easy to mock or swap implementations for testing.
4. **Future flexibility**: Changing databases or adding caching becomes trivial.

### Setting up MongoDB {#h3-7-setting-up-mongodb}

First, add the MongoDB Java Driver to your pom.xml:

```
<dependency>  
    <groupId>org.mongodb</groupId>  
    <artifactId>mongodb-driver-sync</artifactId>  
    <version>5.5.2</version>  
</dependency>
```


### The MongoDBProductDao class {#h3-8-the-mongodbproductdao-class}

Here's our MongoDB-backed implementation:

```
import com.mongodb.client.MongoClient;  
import com.mongodb.client.MongoCollection;  
import com.mongodb.client.MongoDatabase;  
import org.bson.Document;  
import org.bson.types.ObjectId;  
import java.math.BigDecimal;  
import java.util.ArrayList;  
import java.util.List;  
import java.util.Optional;  
import static com.mongodb.client.model.Filters.eq;  

public class MongoDBProductDao implements Dao<Product> {  
    private final MongoCollection<Document> collection;  
    public MongoDBProductDao(MongoClient mongoClient, String databaseName) {  
        MongoDatabase database = mongoClient.getDatabase(databaseName);  
        this.collection = database.getCollection("products");  
    }  

    @Override  
    public Optional<Product> get(String id) {  
        Document doc = collection.find(eq("_id", new ObjectId(id))).first();  
        return Optional.ofNullable(doc).map(this::documentToProduct);  
    }  

    @Override  
    public List<Product> getAll() {  
        List<Product> products = new ArrayList<>();  
        collection.find().forEach(doc -> products.add(documentToProduct(doc)));  
        return products;  
    }  

    @Override  
    public void save(Product product) {  
        Document doc = productToDocument(product);  
        collection.insertOne(doc);  
        // Set the generated ID back to the product object  
        product.setId(doc.getObjectId("_id").toHexString());  
    }  

    @Override  
    public void update(Product product) {  
        Document doc = productToDocument(product);  
        collection.replaceOne(  
                eq("_id", new ObjectId(product.getId())),  
                doc  
        );  
    }  

    @Override  
    public void delete(String id) {  
        collection.deleteOne(eq("_id", new ObjectId(id)));  
    }  

    // Helper methods for mapping between Product and Document  
    private Document productToDocument(Product product) {  
        Document doc = new Document()  
                .append("name", product.getName())  
                .append("category", product.getCategory())  
                .append("price", product.getPrice().doubleValue())  
                .append("stockQuantity", product.getStockQuantity());  

        if (product.getId() != null) {  
            doc.append("_id", new ObjectId(product.getId()));  
        }  
        return doc;  
    }  

    private Product documentToProduct(Document doc) {  
        Product product = new Product();  
        product.setId(doc.getObjectId("_id").toHexString());  
        product.setName(doc.getString("name"));  
        product.setCategory(doc.getString("category"));  
        product.setPrice(BigDecimal.valueOf(doc.getDouble("price")));  
        product.setStockQuantity(doc.getInteger("stockQuantity"));  
        return product;  
    }  
}
```


Let's break down what's happening here:

**Constructor**: We inject the MongoClient and database name, then get a reference to our "products" collection. This follows the dependency injection pattern, making testing easier.

**CRUD operations**: Each method uses the MongoDB Java Driver API but keeps all that complexity hidden behind our simple Dao interface. Notice we're using MongoDB's filter builders (eq) and working with BSON Document objects internally.

**ID handling**: MongoDB uses ObjectIds, so we convert between string IDs (what our domain model uses) and MongoDB's ObjectId type in the helper methods.

**Mapping helpers**: The productToDocument and documentToProduct methods handle the translation between our domain model and MongoDB's document format. This is much simpler than ORM mapping---we're just moving fields around, no complex entity lifecycle to manage.

### Connecting to MongoDB {#h3-9-connecting-to-mongodb}

Before we can use our DAO, we need to establish a connection to MongoDB. Here's a simple connection manager:

```
import com.mongodb.client.MongoClient;  
import com.mongodb.client.MongoClients;  

public class MongoDBConnection {  
    private static MongoClient mongoClient;  
    public static MongoClient getClient() {  
        if (mongoClient == null) {  
            // Add your connection string here  
            String uri = "mongodb+srv://<username>:<password>@<cluster>.mongodb.net/?retryWrites=true&w=majority";  
            MongoClientSettings settings = MongoClientSettings.builder()  
                    .applyConnectionString(new ConnectionString(uri))  
                    .applicationName("data-access-object-java")  
                    .build(); 

            mongoClient = MongoClients.create(settings);  
        }  
        return mongoClient;  
    }  

    public static void close() {  
        if (mongoClient != null) {  
            mongoClient.close();  
        }  
    }  
}
```


In a production application, you'd typically want to externalize the connection string to configuration files. For our purposes, this simple singleton approach works fine.

### The application class {#h3-10-the-application-class}

Now, let's see everything in action:

```
import com.mongodb.client.MongoClient;  
import java.math.BigDecimal;  
import java.util.List;  
import java.util.Optional;  

public class MongoDBInventoryApplication {  
    private static Dao<Product> productDao;  
    public static void main(String[] args) {  
        // Get MongoDB connection  
        MongoClient mongoClient = MongoDBConnection.getClient();  

        try {  
            // Create our DAO  
            productDao = new MongoDBProductDao(mongoClient, "inventory_db");  
            // Create a new product  
            Product mouse = new Product("Wireless Mouse", "Electronics",  
                    new BigDecimal("29.99"), 50);  
            saveProduct(mouse);  
            System.out.println("Saved product with ID: " + mouse.getId());  

            // Get the product we just created  
            Product retrievedProduct = getProduct(mouse.getId());  
            System.out.println("Retrieved: " + retrievedProduct);  

            // Update the product (simulate a sale)  
            retrievedProduct.setStockQuantity(retrievedProduct.getStockQuantity() - 3);  
            retrievedProduct.setPrice(new BigDecimal("24.99")); // Price drop!  
            updateProduct(retrievedProduct);  
            System.out.println("Updated product with new stock and price");  

            // Get all products  
            System.out.println("\nCurrent inventory:");  
            getAllProducts().forEach(product ->  
                    System.out.println("  " + product.getName() +  
                            " - $" + product.getPrice() +  
                            " (Stock: " + product.getStockQuantity() + ")")  
            );  

            // Delete the product  
            deleteProduct(mouse.getId());  
            System.out.println("\nDeleted product: " + mouse.getName());  
        } finally {  
            MongoDBConnection.close();  
        }  
    }  

    public static Product getProduct(String id) {  
        Optional<Product> product = productDao.get(id);  
        return product.orElseThrow(() ->  
                new RuntimeException("Product not found with id: " + id)  
        );  
    }  

    public static List<Product> getAllProducts() {  
        return productDao.getAll();  
    }  

    public static void updateProduct(Product product) {  
        productDao.update(product);  
    }  

    public static void saveProduct(Product product) {  
        productDao.save(product);  
    }  

    public static void deleteProduct(String id) {  
        productDao.delete(id);  
    }  
}
```


Notice how MongoDBInventoryApplication is almost identical to our earlier InventoryApplication---the only difference is how we initialize the DAO. The business logic remains completely unchanged.

This is the power of the DAO pattern: We've swapped from an in-memory implementation to a full-fledged MongoDB backend without touching our application code. Our domain model (Product) stayed clean, and our business logic stayed focused on what it should do, not how to persist data.

Now, we can run our new implementation with the following command:

```
mvn exec:java -Dexec.mainClass="com.mongodb.MongoDBInventoryApplication"
```


And we should see an output similar to:

```
...

Saved product with ID: 69121300b595be0bb189af76

Retrieved: Product{id='69121300b595be0bb189af76', name='Wireless Mouse', category='Electronics', price=29.99, stock=50}

Updated product with new stock and price

Current inventory:

  Wireless Mouse - $24.99 (Stock: 47)

Deleted product: Wireless Mouse

...
```


Advanced considerations {#h2-11-advanced-considerations}
--------------------------------------------------------

### Preventing business logic leakage {#h3-12-preventing-business-logic-leakage}

One of the most important benefits of the DAO pattern is keeping business logic out of your data access code. Here's what you should avoid:

**Bad practice**---business logic in the DAO:

```
// DON'T DO THIS

public void save(Product product) {
    // Business validation doesn't belong here
    if (product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalArgumentException("Price must be positive");
    }

    // Business rules don't belong here
    if (product.getStockQuantity() < 10) {
        sendLowStockAlert(product);
    }

    // Pricing logic doesn't belong here
    if (isHolidaySeason()) {
        product.setPrice(product.getPrice().multiply(new BigDecimal("1.2")));
    }

    Document doc = productToDocument(product);
    collection.insertOne(doc);
}
```


**Good practice**---clean separation:

```
// In your service layer

public class InventoryService {
    private final Dao<Product> productDao;
    private final PriceValidator priceValidator;
    private final AlertService alertService;
    public void addProduct(Product product) {
        // Business validation in the service layer
        priceValidator.validatePrice(product.getPrice());
        // Business pricing rules in the service layer
        if (isHolidaySeason()) {
            product.setPrice(calculateHolidayPrice(product.getPrice()));
        }
        // Persist through DAO
        productDao.save(product);
        // Business logic in the service layer
        if (product.getStockQuantity() < 10) {
            alertService.sendLowStockAlert(product);
        }
    }
}

// In your DAO
public void save(Product product) {
    // Pure persistence logic only
    Document doc = productToDocument(product);
    collection.insertOne(doc);
    product.setId(doc.getObjectId("_id").toHexString());
}
```


Your DAO should focus exclusively on translating between your domain model and MongoDB's document model. All business rules, validation, and workflow logic belong in your service layer.

### Adding custom query methods {#h3-13-adding-custom-query-methods}

While our basic DAO interface covers CRUD operations, real applications often need custom queries. Here's how to add them without polluting your DAO:

```
public interface ProductDao extends Dao<Product> {
    List<Product> findByCategory(String category);
    List<Product> findLowStockProducts(int threshold);
    List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
}
```


And the implementation:

```
import static com.mongodb.client.model.Filters.*;

public class MongoProductDao implements ProductDao {
    // ... existing methods ...
    @Override
    public List<Product> findByCategory(String category) {
        List<Product> products = new ArrayList<>();
        collection.find(eq("category", category))
                  .forEach(doc -> products.add(documentToProduct(doc)));
        return products;
    }

    @Override
    public List<Product> findLowStockProducts(int threshold) {
        List<Product> products = new ArrayList<>();
        collection.find(lte("stockQuantity", threshold))
                  .forEach(doc -> products.add(documentToProduct(doc)));
        return products;
    }

    @Override
    public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        List<Product> products = new ArrayList<>();
        collection.find(and(
            gte("price", minPrice.doubleValue()),
            lte("price", maxPrice.doubleValue())
        )).forEach(doc -> products.add(documentToProduct(doc)));
        return products;
    }
}
```


This approach keeps your query logic encapsulated in the DAO while still maintaining clean separation of concerns.

### Error handling {#h3-14-error-handling}

Production DAOs need robust error handling:

```
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.ErrorCategory;

@Override
public void save(Product product) {
    try {
        Document doc = productToDocument(product);
        collection.insertOne(doc);
        product.setId(doc.getObjectId("_id").toHexString());
    } catch (MongoWriteException e) {
        if (e.getError().getCategory() == ErrorCategory.DUPLICATE_KEY) {
            throw new DuplicateProductException("Product already exists", e);
        }
        throw new DataAccessException("Failed to save product", e);
    } catch (MongoException e) {
        throw new DataAccessException("Database error while saving product", e);
    }
}
```


Notice we're translating MongoDB-specific exceptions into application-specific exceptions. This prevents MongoDB dependencies from leaking into your business layer. We've made use of two custom exceptions, DuplicateProductException and DataAccessException, to help with debugging.

Conclusion {#h2-15-conclusion}
------------------------------

The DAO pattern remains valuable even with modern databases like MongoDB. It creates a clean separation between your business logic and persistence concerns, making your code more maintainable and testable.

We started with a simple in-memory implementation to understand the pattern's core concepts, then built a production-ready MongoDB implementation using a product inventory system. Along the way, we saw how MongoDB's document model actually makes the DAO pattern more straightforward than with traditional ORMs---no complex entity mappings or lazy loading to worry about.

The key takeaway: Keep your DAOs focused purely on persistence operations. All business logic, validation, and workflow concerns belong in your service layer. This separation will pay dividends as your application grows and evolves.

For more information on the MongoDB Java Driver and best practices, check out the [official MongoDB Java Driver documentation](https://www.mongodb.com/docs/drivers/java/sync/current/?utm_campaign=devrel&utm_source=third-part-content&utm_medium=cta&utm_content=data+access+object&utm_term=tim.kelly).
