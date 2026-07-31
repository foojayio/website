---
title: "Domain-Driven Design in Java: A Practical Guide"
slug: "domain-driven-design-in-java-a-practical-guide"
date: "2025-08-15T09:30:31+00:00"
lastmod: "2025-08-15T09:42:11+00:00"
description: "While waiting for your flight in an airport, have you ever wondered how much behind-the-scenes planning it takes to keep an airport running smoothly? - by Rajesh Nair"
authors:
  - "rajesh-nair"
image: "mongologo.png"
categories:
  - "Databases"
  - "Java"
  - "Mongo"
tags:
related_posts:
  - "how-to-identify-the-underlying-causes-of-connection-timeout-errors-for-mongodb-with-java"
  - "java-on-azure-tooling-update-september-2022"
  - "java-virtual-threads-in-action-optimizing-mongodb-operation"
  - "run-an-atlas-cluster-locally-in-minutes"
enlighterjs: true
frozen: false
---

While waiting for your flight in an airport, have you ever wondered how much behind-the-scenes planning it takes to keep an airport running smoothly? Every day, thousands of flights take off and land, passengers rush through terminals, and staff work tirelessly to make sure everything unfolds without a hitch. What looks like chaos on the surface is actually the result of a highly coordinated system---a kind of "brain" that's always working in the background to manage logistics, adapt to surprises, and keep the airport humming along.  

In the world of software, domain-driven design (DDD) plays a similar role. DDD is an approach to building robust business applications by focusing on the core domain---the essential knowledge and operations at the heart of your business. Just like an airport's central system coordinates everything from flight schedules to security checks, DDD organizes your code around real-world processes and concepts. By using DDD, developers aren't just writing code---they're modeling the complex realities of a business, making their applications as reliable, adaptable, and well-orchestrated as a major international airport. They create a shared language which defines clear boundaries and responds flexibly to changing business needs.

Some of the key ideas in DDD include the domain itself (think of it as the "air traffic control" of your application, orchestrating all major operations), ubiquitous language (a shared, standardized vocabulary---much like the precise aviation terms used by every team in an airport), and bounded contexts (separate functional areas, just as an airport has terminals, security, and baggage claim, each with its own rules and workflows).

You'll encounter entities (unique, trackable items like airplanes or passengers), value objects (descriptions or details, such as a boarding pass), aggregates (clusters of related operations), repositories (the places where you store, find, or update your important information), and services (essential operations that coordinate activities across the system but don't belong to any single entity).

A vital part of modern DDD is the use of domain events---moments that signal significant changes, such as \`FlightDelayedEvent\` or \`BoardingStartedEvent\`, which help the whole system react and adapt, just like real-time announcements and operations in an actual airport.

By relating each of these building blocks back to airport operations, DDD transforms abstract software concepts into practical, real-world solutions---making your business logic as organized, predictable, and effective as the world's busiest travel hubs.

In this article, we'll take a hands-on approach to learning domain-driven design by actually building an airport operations system together---step by step, using Java and Spring Boot. You'll see how DDD concepts come to life as we model real-world airport activities, create domain classes, organize bounded contexts, and wire everything up with practical code.

Understanding the "Airport" domain {#h2-0-understanding-the-airport-domain}
---------------------------------------------------------------------------

As a first step, we brainstorm to understand the "Airport" domain: What's unique about airport operations? List out core business activities: scheduling flights, assigning gates, boarding passengers, tracking baggage, enforcing safety.

Result: We would create a glossary for your domain (ubiquitous language) with terms like \`Flight\`, \`Gate\`, \`Runway\`, and \`BoardingPass\`. We would then document this shared vocabulary in Markdown or a Google Doc. We will refer to these in our code comments and as class names for the objects we create in our project.  

Modeling the core Airport domain in Java {#h2-1-modeling-the-core-airport-domain-in-java}
-----------------------------------------------------------------------------------------

Open a new Spring Boot project (use Spring Initializr) in your IDE (like IntelliJ IDEA) and name it \`airport-domain-demo\` as a Maven project.  
![Creating new Java Project using Spring Initializr](https://lh7-rt.googleusercontent.com/docsz/AD_4nXdV6QfcM9LjeZjnl09UlO_xaD_oP1Mmei_aR_qUGhqGN1IAk0_vEAEDC_cOyh9LzE_In8_s4zZMBhTVt5m3-waXWiiOHwCWivEuFBYnmmqVW6RzqqXtLlcfsx3g96NJlno1hwLsXA?key=EKoTlsiGwMVY6pWycWj_Pg)  

For the purpose of this demo, we will curtail our domain objects to a minimum, but in a real-world scenario, you would have a broader glossary of domain objects, their fields, and implementation than what's showcased in the demo.

Identifying aggregates and entities {#h2-2-identifying-aggregates-and-entities}
-------------------------------------------------------------------------------

In DDD, the aggregate is the consistency boundary that defines which entities and value objects belong together when performing business operations. For our simplified airport domain:

* Entity: \`Flight\` represents a scheduled flight. Fields include flightNumber, origin, destination, and schedule. Each flight is uniquely identified by its flight number.
* Entity: \`Passenger\` represents a traveler. It contains an id, name, and a reference to their seat assignment (shows their place within the flight).
* Value object: \`SeatAssignment\` is a value object that represents descriptive attributes without identity. \`SeatAssignment\` describes a passenger's seat via seatNumber and class (e.g., economy, business) and doesn't exist standalone without a passenger.
* Aggregate root: \`Flight\`,  in this simplified model, acts as the aggregate root. It manages its passengers and their seat assignments, ensuring all operations on passengers occur via the flight to maintain consistency.

Implementing entities and value objects {#h2-3-implementing-entities-and-value-objects}
---------------------------------------------------------------------------------------

In the Spring Boot project, navigate to \`src/main/java/com/example/airport/domain\`. Here, we will define key classes that reflect important concepts from the airport domain. These classes become the building blocks of our application's business logic. Another core DDD practice is to allow entities to publish domain events as part of their business logic. In the airport context, when a passenger is added to a flight, you may want to raise an event (like PassengerAddedEvent) so the rest of the system can respond (updating manifests, sending notifications, etc.). A typical approach is using Spring's domain events support.

1. Flight

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class Flight {

    private String flightNumber;

    private String origin;

    private String destination;

    private LocalDateTime scheduledDeparture;

    private LocalDateTime scheduledArrival;

    private List&lt;Passenger&gt; passengers = new ArrayList&lt;&gt;();

    // Constructors, getters, setters

// Standard names for Domain Event publishing via Spring

@DomainEvents

Collection&lt;Object&gt; domainEvents() {

    return events;

}

@AfterDomainEventPublication

void clearDomainEvents() {

    events.clear();

}

    // Business method to add passenger

    public void addPassenger(Passenger passenger) {

        // e.g. validate seat availability before adding

        this.passengers.add(passenger);

    }

}</pre>

2. Passenger

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class Passenger {

    private Long id;

    private String name;

    private SeatAssignment seatAssignment;

    // Constructors, getters, setters

}</pre>

3. SeatAssignment

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class SeatAssignment {

    private String seatNumber;

    private String seatClass; // e.g. Economy, Business

    // Constructors, equals and hashCode for value semantics

}</pre>

The separation of entities and value objects helps maintain clarity. Entities have identity (Flight, Passenger), while value objects describe or detail entities without unique identities (SeatAssignment).

Bounded contexts and modularization {#h2-4-bounded-contexts-and-modularization}
-------------------------------------------------------------------------------

Next, we divide our application into bounded contexts reflecting airport departments, which helps manage complexity by isolating parts of the domain.

* Flight operations:  Handles flight scheduling, passenger management, and communications
* Passenger services:  Manages check-in, boarding, and seat assignments
* Ground services:  Could involve baggage handling and gate assignments, but omitted here for simplicity

In the Java project we created, we will map these contexts to separate packages/modules in our main base package. For example:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">com.example.airport.flightops

com.example.airport.passengerservices</pre>

Each context will own its models and business logic to avoid overlap and conflicts.

Repositories, domain services, and factories {#h2-5-repositories-domain-services-and-factories}
-----------------------------------------------------------------------------------------------

### Repositories {#h3-6-repositories}

Repositories abstract data persistence and retrieval, bridging your domain model with databases or external systems.

Example interfaces:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public interface FlightRepository {

    Flight findByFlightNumber(String flightNumber);

    void save(Flight flight);

}

public interface PassengerRepository {

    Passenger findById(Long id);

    void save(Passenger passenger);

}</pre>

Repositories expose aggregate roots and entities for retrieval and persistence, without exposing database details to domain logic.

### Domain services {#h3-7-domain-services}

Domain services encapsulate business logic that involves multiple domain objects or doesn't naturally fit in an entity or value object.

Example: \`FlightService\` that manages passenger assignments to flights ensuring no double seat bookings

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Service

public class FlightService {

    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {

        this.flightRepository = flightRepository;

    }

    public void addPassengerToFlight(String flightNumber, Passenger passenger) {

        Flight flight = flightRepository.findByFlightNumber(flightNumber);

        // Validate seat isn't already taken

        boolean seatTaken = flight.getPassengers().stream()

            .anyMatch(p -&gt; p.getSeatAssignment().equals(passenger.getSeatAssignment()));

        if (seatTaken) {

            throw new IllegalArgumentException("Seat already assigned");

        }

        flight.addPassenger(passenger);

        flightRepository.save(flight);

    }

}</pre>

### Factories {#h3-8-factories}

Factories create complex aggregate instances while encapsulating creation logic. Example:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Component

public class FlightFactory {

    public Flight createFlight(String flightNumber, String origin, String destination, LocalDateTime departure, LocalDateTime arrival) {

        Flight flight = new Flight();

        flight.setFlightNumber(flightNumber);

        flight.setOrigin(origin);

        flight.setDestination(destination);

        flight.setScheduledDeparture(departure);

        flight.setScheduledArrival(arrival);

        return flight;

    }

}</pre>

<br />

Application layer and integration {#h2-9-application-layer-and-integration}
---------------------------------------------------------------------------

Set up a simple REST controller integrated with your services, exposing the core functionality to clients.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@RestController

@RequestMapping("/api")

public class FlightController {

    private final FlightService flightService;

    private final FlightFactory flightFactory;

    private final FlightRepository flightRepository;

    public FlightController(FlightService flightService, FlightFactory flightFactory, FlightRepository flightRepository) {

        this.flightService = flightService;

        this.flightFactory = flightFactory;

        this.flightRepository = flightRepository;

    }

    @PostMapping("/flights")

    public ResponseEntity&lt;Flight&gt; createFlight(@RequestBody FlightRequest flightRequest) {

        Flight flight = flightFactory.createFlight(

            flightRequest.getFlightNumber(),

            flightRequest.getOrigin(),

            flightRequest.getDestination(),

            flightRequest.getScheduledDeparture(),

            flightRequest.getScheduledArrival()

        );

        flightRepository.save(flight);

        return ResponseEntity.ok(flight);

    }

    @PostMapping("/flights/{flightNumber}/passengers")

    public ResponseEntity&lt;String&gt; addPassenger(@PathVariable String flightNumber, @RequestBody Passenger passenger) {

        try {

            flightService.addPassengerToFlight(flightNumber, passenger);

            return ResponseEntity.ok("Passenger added");

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());

        }

    }

}</pre>

<br />

FlightRequestDTO

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class FlightRequest {

    private String flightNumber;

    private String origin;

    private String destination;

    private LocalDateTime scheduledDeparture;

    private LocalDateTime scheduledArrival;

    // getters and setters

}</pre>

The API exposes aggregate-root operations and domain behavior aligned with business processes.

Testing and evolving the model {#h2-10-testing-and-evolving-the-model}
----------------------------------------------------------------------

The next step is to write Junit tests for core scenarios, like flight creation, adding passengers, etc.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@SpringBootTest

public class AirportApplicationTests {

    @Autowired

    private FlightRepository flightRepository;

    @Autowired

    private PassengerRepository passengerRepository;

    @Test

    public void testCreateFlight() {

        Flight flight = new Flight();

        flight.setFlightNumber("AB123");

        flight.setOrigin("JFK");

        flight.setDestination("LAX");

        flightRepository.save(flight);

        assertNotNull(flightRepository.findByFlightNumber("AB123"));

    }

}</pre>

<br />

**Complete DDD implementation source code** {#h2-11-complete-ddd-implementation-source-code}
--------------------------------------------------------------------------------------------

You can find an elaborated codebase for the airport-domain-demo on my [GitHub repo](https://github.com/vemarahub/airport-domain-demo).

The project has domains segregated, keeping in mind the DDD approach, and uses MongoDB as its underlying database, as MongoDB's document-oriented model naturally supports DDD concepts, such as aggregates, bounded contexts, and repository abstraction.

The project has the below endpoints created for the demo:  

Base URL:  

http://localhost:8080/api/flights

**Flight management endpoints**

1. Create flight

• Method: POST

• URL: '/api/flight'

• Purpose: Create a new flight in the system

• Request body (JSON):

{

"flightNumber": "UA101",

"origin": "JFK",

"destination": "LAX",

"scheduledDeparture": "2024-12-25T10:00:00",

"scheduledArrival": "2024-12-25T13:00:00"

}

2. Get all flights

• Method: GET

• URL: \`/api/flights\`

• Purpose: Retrieve all flights in the system

• Response: Array of flight objects, including their passengers

• DDD concept: Implements the repository pattern, abstracting data retrieval from the core domain.

3. Get flight by number

• Method: GET

• URL: \`/api/flights/{flightNumber}\`

• Example: \`/api/flights/UA101\`

• Purpose: Get specific flight details, including the list of passengers

• Response: Flight object with passenger list

• DDD concept: Retrieves the aggregate root (\`Flight\`) and its related entities (\`Passenger\`)

4. Get flights by route

• Method: GET

• URL: \`/api/flights/route?origin={origin}\&destination={destination}\`

• Example: \`/api/flights/route?origin=JFK\&destination=LAX\`

• Purpose: Find flights between specified airports

• Response: Array of flights matching the criteria

• DDD concept: Domain service method that implements domain-specific querying

5. Get flights by departure time range

• Method: GET

• URL: \`/api/flights/departures?start={startTime}\&end={endTime}\`

• Example: \`/api/flights/departures?start=2024-12-25T00:00:00\&end=2024-12-25T23:59:59\`

• Purpose: List flights departing within a given time window

• Response: Array of flights departing in that range

• DDD concept: Repository with domain logic for time-based querying

6. Delete flight

• Method: DELETE

• URL: \`/api/flights/{flightNumber}\`

• Example: \`/api/flights/UA101\`

• Purpose: Remove a flight from the system

• Response: \`200 OK\` - "Flight deleted successfully"

• DDD concept: Aggregate root deletion to maintain system consistency

**Passenger management endpoints**

1. Add passenger to flight

• Method: POST

• URL: \`/api/flights/{flightNumber}/passengers\`

• Example: \`/api/flights/UA101/passengers\`

• Purpose: Add a passenger, with seat assignment, to a specific flight

• Request body (JSON):

{

"name": "John Doe",

"seatNumber": "12A",

"seatClass": "Economy"

}

• Response: \`200 OK\` - "Passenger added successfully"

• Error: \`409 Conflict\` - "Seat 12A is already assigned"

• DDD concept: Business rule enforcement to prevent duplicate seat assignments within the aggregate

2. Add passenger without seat

• Method: POST

• URL: \`/api/flights/{flightNumber}/passengers\`

• Purpose: Add passenger without pre-assigned seat

• Request body (JSON):

{

"name": "Jane Smith"

}

• Response: \`200 OK\` - "Passenger added successfully"

• DDD concept: Demonstrates optional value objects (\`SeatAssignment\`)

3. Remove passenger from flight

• Method: DELETE

• URL: \`/api/flights/{flightNumber}/passengers/{passengerId}\`

• Example: \`/api/flights/UA101/passengers/1\`

• Purpose: Remove a specific passenger from a flight

• Response: \`200 OK\` - "Passenger removed successfully"

• DDD concept: Aggregate manages its entities, ensuring encapsulated and consistent deletion

Testing scenarios for Postman {#h2-12-testing-scenarios-for-postman}
--------------------------------------------------------------------

Scenario 1: Complete flight booking flow

• Create a flight.

• Add multiple passengers with different seat classes.

• Attempt to add a passenger with a duplicate seat (expect failure).

• Retrieve flight details to verify passengers.

• Remove a passenger.

• Retrieve updated flight details.

Scenario 2: Business rule validation

• Attempt to create a flight where departure time is after arrival (should fail).

• Attempt to create a flight scheduled in the past (should fail).

• Add a passenger with a duplicate seat number (should fail).

Scenario 3: Query operations

• Create multiple flights on different routes.

• Search flights by route.

• Search flights by time range.

• Retrieve all flights for an overview.

**Sample data**   

The application initializes with three flights:

• UA101: JFK → LAX (with two passengers)

• UA102: LAX → JFK (with one passenger)

• AA203: ORD → MIA (with three passengers)

Real-world tips and common pitfalls {#h2-13-real-world-tips-and-common-pitfalls}
--------------------------------------------------------------------------------

It's important to pause after each section and encourage reflection of what we have achieved in each step of the domain-driven approach. For example, after modeling these aggregates, did we discover new business terms or rules we missed earlier? We would then update our ubiquitous language document in relation to the new discoveries.  

Also, it is significant in DDD to have pitfall identification and have preventive measures like mixing business logic with controllers, kind of like having a spaghetti runway. We would often have the requirement to refactor a confusing service or repository to inline with DDD approach.

Conclusion {#h2-14-conclusion}
------------------------------

By following these demo-driven steps, you'll experience DDD as a practical "control tower" for your Spring Boot project: organizing code, clarifying responsibilities, and enabling your team to grow and adapt the system---just as in a real-world airport operation.

We can enhance the capabilities of this demo application by adding new features, departments (contexts), or incorporating more real-life event correlations into the design. The main motive of DDD is to always try to have code that goes back to business understanding.
