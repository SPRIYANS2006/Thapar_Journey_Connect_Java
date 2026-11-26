# Thapar Journey Connect — Java Lab Viva & Oral Defense Cheatsheet
## Textbook-Grade Answers for University Faculty & Lab Evaluators

This guide prepares you for common oral defense (viva voce) questions asked during university evaluations for **Object-Oriented Programming (OOP)** and **Advanced Java Full-Stack Lab**.

---

### Section 1: Core OOP Concepts & Architecture

#### Q1: "Explain how you demonstrated Runtime Polymorphism in this project."
> **Answer**:  
> "We defined an abstract base class `TravelGroup` with an abstract method:  
> `public abstract double calculatePerRiderFare(double totalFare);`  
> We have three derived subclasses: `CabRideGroup`, `TrainTravelGroup`, and `BusTravelGroup`.  
> At runtime, when a user queries their estimated fare, Java's dynamic method dispatch invokes the overridden subclass implementation based on the concrete transit type:
> * In `CabRideGroup`, `calculatePerRiderFare` dynamically divides the total taxi meter fare equally among all active group members: `totalFare / Math.max(1, members.size())`. For example, a ₹1400 fare to Chandigarh Airport with 4 riders yields ₹350 per student.
> * In `TrainTravelGroup` and `BusTravelGroup`, we operate a zero ticket-liability coordination model where students book tickets directly with IRCTC/PRTC. It returns `0.0`, reflecting zero shared ticket liability.  
> This allows polymorphic handling of all travel entities through base `TravelGroup` references without brittle `if-else` or `instanceof` checks."

#### Q2: "Why did you use both an Abstract Class and Interfaces?"
> **Answer**:  
> "We followed the **Interface Segregation Principle (ISP)** and standard OOP design:  
> 1. **Interfaces (`Bookable`, `Cancellable`, `Notifiable`, `SearchableRoute`)** define orthogonal behavioral contracts. For example, `Bookable` dictates capacity checking (`isFull()`, `getRemainingSeats()`), while `Cancellable` defines late cancellation penalty formulas. Any class in the system (including future rental or carpool models) can implement these contracts independently.  
> 2. **Abstract Class (`TravelGroup`)** captures shared state and default identity attributes (`id`, `title`, `date`, `departureTime`, `origin`, `destination`, `members`) and provides foundational implementations of interface methods, while forcing concrete transit subclasses to define vehicle-specific fare logic via abstract methods. This provides code reuse while preserving contract flexibility."

#### Q3: "How does your code enforce Encapsulation and Data Integrity?"
> **Answer**:  
> "Encapsulation is enforced at three levels:  
> 1. **Strict Access Specifiers**: All class variables are `private`. Access is only granted via validated getters and business methods.  
> 2. **Defensive Copying**: When callers invoke `group.getMembers()`, instead of leaking a reference to the mutable internal `ArrayList`, we return `Collections.unmodifiableList(new ArrayList<>(this.members))`. This prevents callers from altering the group roster without going through the capacity guard and concurrency locks.  
> 3. **Constructor and Domain Invariants**: Constructors validate non-empty IDs, future dates, valid vehicle capacities ($\le 4$ for cabs), and `@thapar.edu` email domains."

---

### Section 2: Java Collections Framework & Algorithms

#### Q4: "Why did you choose `ConcurrentHashMap` and `TreeSet` for the 15-Minute Slot Engine?"
> **Answer**:  
> "The 15-minute slot engine partitions a 24-hour day into 96 distinct departure intervals (every 15 minutes from 00:00 to 23:45):  
> 1. **`ConcurrentHashMap<String, RideSlot>`**: Used for thread-safe $O(1)$ constant-time slot lookup using a composite key: `destinationId_date_time_direction`. It provides lock striping across hash segments, allowing simultaneous reads and concurrent writes across different departure intervals without global blocking.  
> 2. **`TreeSet<RideSlot>`**: `RideSlot` implements `Comparable<RideSlot>`, comparing the `LocalTime` of departure. Sorting in a `TreeSet` automatically guarantees $O(\log N)$ sorted insertion and produces an ordered chronological stream of available departure slots for the frontend UI."

#### Q5: "What is the difference between `Comparable` and `Comparator`, and how did you use them?"
> **Answer**:  
> "`Comparable` defines the **natural ordering** of an object via `compareTo(T other)` inside the class itself. We implemented `Comparable<RideSlot>` on `RideSlot` using departure time so any collection or `TreeSet` orders departures chronologically by default.  
> `Comparator` defines **external or alternative ordering strategies** via `compare(T o1, T o2)`. In our `GroupService` and `MyJourneysService`, we used `Comparator.comparing(GroupEntity::getDate).thenComparing(GroupEntity::getDepartureTime)` to sort joined trips chronologically for the student's personal itinerary."

---

### Section 3: Multithreading, Concurrency & Critical Sections

#### Q6: "Explain the race condition in seat booking and how you solved it."
> **Answer**:  
> "In a campus cab sharing system, maximum capacity is strictly 4 students. When a cab already has 3 enrolled students and only 1 seat remains, a race condition occurs if Student A and Student B click 'Join Slot' at the exact same millisecond:  
> 1. Thread A reads member count: 3.  
> 2. Context switch occurs before Thread A writes to the database.  
> 3. Thread B reads member count: 3.  
> 4. Both threads believe a seat is available and both enroll, resulting in 5 passengers in a 4-seater cab (**Overbooking Bug**).  
> 
> **Our Solution**:  
> We introduced critical section synchronization using Java's `ReentrantLock` in `SlotEngineService`:  
> ```java
> ReentrantLock lock = slotLocks.computeIfAbsent(slotKey, k -> new ReentrantLock());
> lock.lock();
> try {
>     List<GroupMemberEntity> current = groupMemberRepository.findByTargetIdAndGroupType(...);
>     if (current.size() >= targetGroup.getMaxMembers()) {
>         throw new OverbookingException(current.size(), targetGroup.getMaxMembers());
>     }
>     // Persist new member atomically...
> } finally {
>     lock.unlock();
> }
> ```  
> By keying locks per `slotKey` using `ConcurrentHashMap`, bookings for the 08:00 Delhi cab do not block bookings for the 09:15 Chandigarh cab, maximizing throughput while guaranteeing zero overbooking."

#### Q7: "How did you verify thread safety in your automated test suite?"
> **Answer**:  
> "We wrote `ConcurrencyBookingTest` using JUnit 5, Java's `ExecutorService`, and `CountDownLatch`. We spawned 10 parallel worker threads attempting to concurrently book the final remaining seat on a cab capped at 4 members. The `CountDownLatch` held all threads until releasing them simultaneously. Our assertions verified that exactly 4 seats were filled, the remaining 6 threads threw `OverbookingException`, and the database state remained consistent with 0 overbooking."

---

### Section 4: Design Patterns & Architecture

#### Q8: "Where and why did you use the Factory Pattern?"
> **Answer**:  
> "We implemented `TravelGroupFactory` with enum `TravelGroupType { CAB, TRAIN, BUS }`.  
> When students create travel cohorts through the UI, the service layer passes a `GroupSpecification` to `TravelGroupFactory.createGroup(type, spec)`. The factory encapsulates instantiation logic:  
> * If `CAB`: instantiates `CabRideGroup` with 4-seater default capacity and taxi fare calculations.  
> * If `TRAIN`: instantiates `TrainTravelGroup` with IRCTC train number, class (CC, 3A, SL), and zero ticket-liability model.  
> * If `BUS`: instantiates `BusTravelGroup` with PRTC/PUNBUS operator details.  
> This isolates object creation from business logic, adhering to the **Open/Closed Principle**—adding a new transit mode (e.g., Flight or Metro) requires no modifications to calling services."

#### Q9: "How does the DTO (Data Transfer Object) Pattern protect your domain entities?"
> **Answer**:  
> "We created static DTO classes (`JoinSlotRequest`, `CreatePostRequest`, `CreateTravelGroupRequest`). Exposing JPA entities directly in `@RestController` request bodies causes **Mass Assignment Vulnerabilities** (where malicious clients inject arbitrary fields such as `adminId` or `reliability`). DTOs act as a clean boundary: incoming JSON is bound to strongly-typed DTOs, validated, and then mapped by services to persistent entities."

#### Q10: "Explain the Singleton Pattern in your Spring Boot application."
> **Answer**:  
> "In Spring Boot, classes annotated with `@Service`, `@Repository`, and `@Configuration` (`AppConfig`, `SlotEngineService`, `AuthService`) are managed as **Singletons** by default in the Spring ApplicationContext IoC container. Only one instance of each service exists in memory, sharing thread-safe dependencies (such as the database connection pool `HikariDataSource` and JWT provider) across all incoming HTTP requests."

---

### Section 5: Exception Handling & Domain Rules

#### Q11: "Explain your Custom Exception Hierarchy and how HTTP status codes are mapped."
> **Answer**:  
> "We created a clean academic exception hierarchy under `com.tiet.journeyconnect.exceptions`, all extending `RuntimeException`:  
> * `InvalidCampusDomainException`: Enforces the institutional boundary that only `@thapar.edu` accounts may register or log in. Mapped to **HTTP 400 Bad Request**.  
> * `OverbookingException`: Thrown when a cab exceeds 4 riders. Mapped to **HTTP 409 Conflict**.  
> * `SingleRidePolicyViolationException`: Thrown when a student attempts to join two overlapping active rides on the same slot. Mapped to **HTTP 409 Conflict**.  
> * `SingleCommunityRuleViolationException`: Enforces that a student belongs to exactly one hometown community hub. Mapped to **HTTP 409 Conflict**.  
> * `SlotNotFoundException`: Mapped to **HTTP 404 Not Found**.  
> * `UnauthorizedAccessException`: Mapped to **HTTP 401 / 403**.  
> 
> We centralize error interception using a Spring `@RestControllerAdvice` class (`GlobalExceptionHandler`). This separates error handling from business logic, returning structured JSON error payloads with timestamps and error messages."

---

### Section 6: Viva Summary Matrix (Quick Reference)

| Topic | Key Term to Say | Where in Code to Point Evaluator |
| :--- | :--- | :--- |
| **Encapsulation** | Defensive Copying (`Collections.unmodifiableList`) | `models.hierarchy.TravelGroup.java` (L65-75) |
| **Polymorphism** | Dynamic Method Dispatch (`calculatePerRiderFare`) | `CabRideGroup.java` vs `TrainTravelGroup.java` |
| **Interfaces** | Interface Segregation Principle (ISP) | `models.interfaces.Bookable.java`, `Cancellable.java` |
| **Collections** | $O(1)$ Hash Striping + Chronological `TreeSet` | `services.SlotEngineService.java` (L45-80) |
| **Concurrency** | `ReentrantLock` + Double-Checked Capacity Guard | `services.SlotEngineService.java` (L230-265) |
| **Unit Testing** | `CountDownLatch` Parallel Thread Stress Test | `src/test/java/com/tiet/journeyconnect/ConcurrencyBookingTest.java` |
| **Factory Pattern** | Creational Abstraction via Specification | `factory.TravelGroupFactory.java` |
| **Global Exceptions**| `@RestControllerAdvice` Error Interception | `controllers.GlobalExceptionHandler.java` |
