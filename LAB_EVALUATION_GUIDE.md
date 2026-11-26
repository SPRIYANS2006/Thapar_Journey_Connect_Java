# Thapar Journey Connect — Lab Evaluation & Viva Guide
**Department of Computer Science & Engineering**  
**Thapar Institute of Engineering & Technology (TIET), Patiala**  
*Course: Object-Oriented Programming (OOP) / Advanced Java Full-Stack Lab*

---

## 1. Executive Summary for Evaluators

**Thapar Journey Connect** is a full-stack campus transit and travel cohort operating system developed specifically for TIET students. It solves real-world student travel coordination (daily transit to Patiala Railway Station, Bus Stand, and Chandigarh/Delhi Airports) while demonstrating textbook-grade **Object-Oriented Programming (OOP)**, **Thread Safety & Concurrency Control**, and **Enterprise Design Patterns** in Java 21 LTS and Spring Boot 3.3.4.

---

## 2. Quick Examiner Verification (1-Click Run)

### Launching the Application
Run the one-click startup batch script located in the project root:
```cmd
run-local.bat
```
* **Frontend Application**: [http://localhost:5173/](http://localhost:5173/) *(React 19 + Tailwind CSS)*
* **Embedded Spring Boot UI**: [http://localhost:8080/](http://localhost:8080/) *(Self-contained bundle served by Tomcat)*
* **REST API Health Check**: [http://localhost:8080/api/health](http://localhost:8080/api/health)
* **Embedded H2 Database Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)  
  *(JDBC URL: `jdbc:h2:mem:thapar_journey`, Username: `sa`, Password: `<empty>`)*

### Running Automated Test Suite
Run the test runner batch script:
```cmd
run-tests.bat
```
* **Status**: **11/11 JUnit 5 Tests Passing** (Polymorphism, Concurrency, Domain Invariants, Single Ride Policy, Slot Generation).

---

## 3. Academic Criteria & Code Architecture Mapping

| Criteria Evaluated | Concrete Implementation in Code | Source File Path |
| :--- | :--- | :--- |
| **Encapsulation & Defensive Copying** | Private fields, domain setters, defensive copying using `Collections.unmodifiableList(new ArrayList<>(this.members))` | `backend/src/main/java/com/tiet/journeyconnect/models/hierarchy/TravelGroup.java` |
| **Inheritance & Polymorphism** | Abstract base class `TravelGroup` extended by `CabRideGroup`, `TrainTravelGroup`, `BusTravelGroup`. Polymorphic method `calculatePerRiderFare(double totalFare)` splits cab fare dynamically vs zero-liability IRCTC/PRTC model. | `backend/src/main/java/com/tiet/journeyconnect/models/hierarchy/` |
| **Interfaces & Abstraction** | `Bookable` (capacity checks), `Cancellable` (departure penalty calculation: -0.8 within 30 min, -0.3 within 4h), `Notifiable` (alerts), `SearchableRoute` (chronological departure matching). | `backend/src/main/java/com/tiet/journeyconnect/models/interfaces/` |
| **Collections Framework** | `ConcurrentHashMap<String, RideSlot>` for $O(1)$ composite-key lookup (`destination_date_time_direction`); `TreeSet<RideSlot>` implementing `Comparable<RideSlot>` for natural chronological sorting across 96 daily intervals. | `backend/src/main/java/com/tiet/journeyconnect/services/SlotEngineService.java` |
| **Multithreading & Concurrency** | Double-checked capacity guard and critical section protection using `ReentrantLock` keyed per `slotKey`. Prevents race conditions when booking the 4th (final) cab seat. Tested with 10 threads via `CountDownLatch`. | `backend/src/main/java/com/tiet/journeyconnect/services/SlotEngineService.java` & `src/test/java/.../ConcurrencyBookingTest.java` |
| **Custom Exception Hierarchy** | `InvalidCampusDomainException` (HTTP 400), `OverbookingException` (HTTP 409), `SingleRidePolicyViolationException` (HTTP 409), `SingleCommunityRuleViolationException` (HTTP 409), `SlotNotFoundException` (HTTP 404). Handled globally via `@RestControllerAdvice`. | `backend/src/main/java/com/tiet/journeyconnect/exceptions/` & `controllers/GlobalExceptionHandler.java` |
| **Creational Design Pattern** | **Factory Pattern**: `TravelGroupFactory.createGroup(TravelGroupType, GroupSpecification)` decouples transit cohort instantiation from business logic. | `backend/src/main/java/com/tiet/journeyconnect/factory/TravelGroupFactory.java` |
| **Structural / Architectural Patterns** | **DTO Pattern**: `AuthDtos`, `TravelDtos`, `CommunityDtos` isolate JPA entities from external REST contracts. **Singleton Pattern**: Spring IoC container-managed services and configuration. | `backend/src/main/java/com/tiet/journeyconnect/dto/` & `config/AppConfig.java` |

---

## 4. Pre-Seeded Academic Test Accounts

The in-memory H2 database is automatically populated on startup with TIET-specific entities:

| Role | Email | Password | Hometown Community | Hostel | Trips Completed |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Student 1** | `student1@thapar.edu` | `Thapar@123` | Delhi NCR (`del`) | Tejas Hall (Hostel J) | 4 (5.0 ★) |
| **Student 2** | `student2@thapar.edu` | `Thapar@123` | Chandigarh & Tri-City (`chd`) | Mother Teresa Hall (Hostel N) | 2 (4.8 ★) |
| **Admin Desk**| `admin@thapar.edu`    | `Thapar@123` | Punjab Hub | Faculty Residences | N/A |

### Pre-Seeded Master Data
* **Destinations (4 Core TIET Transit Routes)**:
  1. Patiala Railway Station (`railway` — 6.2 km, ₹140 cab fare)
  2. Patiala Bus Stand (`bus_stand` — 5.8 km, ₹120 cab fare)
  3. Chandigarh Airport (IXC) (`chandigarh_airport` — 64 km, ₹1400 cab fare)
  4. Delhi Airport (DEL) (`delhi_airport` — 245 km, ₹4200 cab fare)
* **Hostels**: 18 official TIET halls of residence (Hostels A, B, C, D, E, G, H, I, J, K, L, M, N, O, Q, PG Hostel, Frazer Hall).
* **Hometown Hubs**: 7 Regional City Communities (Delhi NCR, Chandigarh, Ludhiana, Amritsar, Jalandhar, Bathinda, Jaipur).

---

## 5. Key Questions Evaluators Ask & Quick Answers

1. **"How does your fare calculation demonstrate polymorphism?"**  
   * *Answer*: `TravelGroup` defines abstract `calculatePerRiderFare(double totalFare)`. `CabRideGroup` divides the taxi meter fare equally across occupied seats ($\text{totalFare} / \text{members.size()}$), while `TrainTravelGroup` and `BusTravelGroup` return `0.0` (self-booked IRCTC/PRTC model). Java's dynamic dispatch calls the correct algorithm at runtime without any `instanceof` branching.

2. **"Why use `ReentrantLock` instead of simply synchronizing the whole method?"**  
   * *Answer*: Synchronizing the entire `joinSlot()` method creates a global bottleneck across the whole university—booking a cab to Delhi at 08:00 would block students booking a cab to Patiala Railway Station at 15:30. By keying `ReentrantLock` per `slotKey` in a `ConcurrentHashMap`, we synchronize only the critical section of that specific cab, achieving maximum concurrent throughput with zero overbooking.

3. **"Why is defensive copying necessary in `getMembers()`?"**  
   * *Answer*: If `getMembers()` returns the internal mutable `ArrayList`, an external caller could invoke `.clear()` or `.add()` without triggering our 4-seater capacity checks or concurrency locks. By returning `Collections.unmodifiableList(new ArrayList<>(this.members))`, internal state is strictly protected.
