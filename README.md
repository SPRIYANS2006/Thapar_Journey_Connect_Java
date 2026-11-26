# Thapar Journey Connect — Java Full-Stack Lab Edition
> **Campus Transit & Travel Cohort Operating System**  
> *Thapar Institute of Engineering & Technology (TIET), Patiala*  
> **Course**: Object-Oriented Programming (OOP) & Java Full-Stack Lab Evaluation

---

## Quick Start (One-Click Localhost Execution)

### Prerequisites
* **Java 21 LTS** (or Java 17+)
* **Node.js 18+** with `npm`

### Launch the Application
Simply double-click the one-click startup script in the project root:
```cmd
run-local.bat
```
This script will:
1. Verify the Java 21 environment.
2. Launch the Spring Boot backend on port `8080` with embedded H2 in-memory database.
3. Launch the React 19 + Tailwind CSS frontend on port `5173`.

### Access URLs
| Service | URL | Notes |
| :--- | :--- | :--- |
| **Full-Stack Application** | [http://localhost:8080/](http://localhost:8080/) | Self-contained Spring Boot embedded frontend bundle |
| **Vite Dev Frontend** | [http://localhost:5173/](http://localhost:5173/) | Live hot-reloading dev server |
| **REST API Health Check** | [http://localhost:8080/api/health](http://localhost:8080/api/health) | Verifies database and server status |
| **H2 Database Console** | [http://localhost:8080/h2-console](http://localhost:8080/h2-console) | JDBC URL: `jdbc:h2:mem:thapar_journey`, User: `sa`, Password: *(empty)* |

---

## Pre-Seeded Academic Test Accounts
All test accounts are pre-loaded on database startup:

| Account Role | Email | Password | Hometown Hub | Hostel |
| :--- | :--- | :--- | :--- | :--- |
| **Student #1** | `student1@thapar.edu` | `Thapar@123` | Delhi NCR (`del`) | Tejas Hall (Hostel J) |
| **Student #2** | `student2@thapar.edu` | `Thapar@123` | Chandigarh & Tri-City (`chd`) | Mother Teresa Hall (Hostel N) |
| **Admin Desk** | `admin@thapar.edu` | `Thapar@123` | Punjab | Faculty Residences |

---

## Academic OOP & Lab Criteria Checklist

- [x] **Encapsulation**: Private fields, strict domain validation, defensive copies (`Collections.unmodifiableList`).
- [x] **Inheritance & Polymorphism**:
  - Abstract base class `TravelGroup`
  - Subclasses: `CabRideGroup`, `TrainTravelGroup`, `BusTravelGroup`
  - Polymorphic method: `calculatePerRiderFare(double totalFare)` (splits taxi fare vs 0-ticket-liability cohort).
- [x] **Interfaces & Abstraction**:
  - `Bookable` (capacity bounds and seat checks)
  - `Cancellable` (departure time-based penalty computation: -0.8 within 30 min, -0.3 within 4h)
  - `Notifiable` (member notifications)
  - `SearchableRoute` (chronological sorting & route matching)
- [x] **Collections Framework**:
  - `ConcurrentHashMap<String, RideSlot>` for $O(1)$ constant-time slot lookups across 96 daily 15-min intervals.
  - `TreeSet<RideSlot>` implementing `Comparable<RideSlot>` for natural chronological departure sorting.
- [x] **Concurrency & Multithreading**:
  - Critical section protection using `ReentrantLock` keyed by `slotKey`.
  - Double-checked capacity guard preventing race conditions when booking the 4th (final) seat.
  - Multi-threaded stress test verified with `CountDownLatch` in JUnit 5.
- [x] **Design Patterns**:
  - **Factory Pattern**: `TravelGroupFactory` for dynamic instantiation of Cab, Train, and Bus travel cohorts.
  - **Singleton Pattern**: Spring IoC container managed beans and database configuration.
  - **Repository Pattern**: Spring Data JPA repository layer.
  - **DTO Pattern**: Separation of internal JPA entities from external REST API contracts.
- [x] **Custom Exception Hierarchy**:
  - `InvalidCampusDomainException` (enforces `@thapar.edu` institutional boundary -> HTTP 400).
  - `OverbookingException` (enforces 4-rider vehicle capacity -> HTTP 409).
  - `SingleRidePolicyViolationException` (prevents double booking on the same slot -> HTTP 409).
  - `SingleCommunityRuleViolationException` (enforces single hometown hub membership -> HTTP 409).
  - `SlotNotFoundException` (HTTP 404).
  - `UnauthorizedAccessException` (HTTP 401 / 403).
  - Handled globally via `@RestControllerAdvice` (`GlobalExceptionHandler`).
- [x] **Automated JUnit 5 Test Suite**:
  - 11/11 tests passing (`mvn test`).

---

## Directory Structure
```text
Thapar_Journey_Connect_Java/
├── backend/
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/tiet/journeyconnect/
│       │   ├── config/              # Security, CORS, WebMvc, JWT configuration
│       │   ├── controllers/         # REST Controllers matching frontend contracts exactly
│       │   ├── dto/                 # Strongly-typed Data Transfer Objects (DTOs)
│       │   ├── exceptions/          # Custom Academic Exception Hierarchy
│       │   ├── factory/             # Creational Factory Pattern for Travel Groups
│       │   ├── models/
│       │   │   ├── entities/        # JPA Persistent Entities
│       │   │   ├── hierarchy/       # OOP Abstract Base Class & Subclasses (Polymorphism)
│       │   │   └── interfaces/      # Bookable, Cancellable, Notifiable, SearchableRoute
│       │   ├── repositories/        # 13 Spring Data JPA Repositories
│       │   └── services/            # Slot Engine, Concurrency, Security, Seeder, Serializers
│       ├── main/resources/
│       │   ├── application.properties # Embedded H2 configuration
│       │   ├── data.sql             # Static seed definitions
│       │   └── static/              # Embedded React 19 production bundle
│       └── test/java/com/tiet/journeyconnect/ # JUnit 5 test suite
├── frontend/                        # React 19 + Tailwind CSS frontend
├── run-local.bat                    # One-click startup script for professors & evaluators
└── README.md
```
