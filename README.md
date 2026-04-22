# Car Showroom Management API

A Spring Boot REST API that replaces a legacy Java Swing desktop application for managing a car dealership. It handles inventory, customers, purchase orders, invoices, payments, test drives, and service scheduling — all backed by an in-memory H2 database.

---

## Stack

| Layer | Technology |
|-------|-----------|
| Runtime | Java 17 |
| Framework | Spring Boot 3.4 |
| Persistence | Spring Data JPA + Hibernate |
| Database | H2 (in-memory) |
| Validation | Jakarta Bean Validation |
| API Docs | springdoc-openapi (Swagger UI) |

---

## Running the app

**Prerequisites:** JDK 17+

```bash
./mvnw spring-boot:run
```

The app starts on **`http://localhost:8080`**.

---

## Exploring the API

| URL | Description |
|-----|-------------|
| `http://localhost:8080/swagger-ui.html` | Interactive Swagger UI — browse and try every endpoint |
| `http://localhost:8080/v3/api-docs` | Raw OpenAPI 3.0 JSON spec |
| `http://localhost:8080/h2-console` | H2 database console (JDBC URL: `jdbc:h2:mem:carshowroom`) |

See [API.md](API.md) for the full endpoint reference with request/response examples.

---

## Seed data

The database is seeded automatically on every startup from [`src/main/resources/data.sql`](src/main/resources/data.sql). Because `ddl-auto` is set to `create-drop`, the schema is recreated fresh each time the app starts and the seed data is re-applied.

### What's seeded

**Users**

| ID | Name | Role |
|----|------|------|
| 1 | Alice Johnson | MANAGER (Sales dept.) |
| 2 | Bob Smith | STAFF (Sales Representative) |
| 3 | Carol White | STAFF (Service Technician) |
| 4 | David Brown | CUSTOMER |
| 5 | Emma Davis | CUSTOMER |
| 6 | Frank Wilson | CUSTOMER |

**Cars**

| VIN | Car | Status |
|-----|-----|--------|
| `1HGCM82633A004352` | 2023 Honda Accord | AVAILABLE |
| `2T1BURHE0JC043821` | 2022 Toyota Corolla | AVAILABLE |
| `3VWFE21C04M000001` | 2021 VW Golf | RESERVED |
| `4S3BMHB68B3286050` | 2020 Subaru Legacy | SOLD (owner: David Brown) |
| `5NPE24AF8FH052952` | 2023 Hyundai Sonata | AVAILABLE |
| `1FTFW1ET0EFA37167` | 2022 Ford F-150 | AVAILABLE |

**Pre-built scenarios**

- **Completed purchase** — David Brown bought the Subaru Legacy. The full chain exists: order → invoice → paid payment → sale record.
- **In-progress purchase** — Emma Davis has an APPROVED order and a generated invoice for the VW Golf. The payment (`POST /api/payments`) is intentionally left unpaid so the full payment flow can be demonstrated live.
- **Pending order** — Frank Wilson has a PENDING order on the Honda Accord. Attempting `DELETE /api/cars/1HGCM82633A004352` will return `409 Conflict`.
- **Test drives** — one COMPLETED, one SCHEDULED, one CANCELLED.
- **Service schedules** — two COMPLETED, one SCHEDULED.

### Re-seeding manually

Because `create-drop` drops and recreates the schema on each restart, simply stopping and restarting the app resets the database to its seeded state:

```bash
# Stop with Ctrl+C, then:
./mvnw spring-boot:run
```

To modify the seed data, edit [`src/main/resources/data.sql`](src/main/resources/data.sql) directly.

---

## Purchase workflow

The typical end-to-end flow:

```
POST /api/orders          →  order status: PENDING
PATCH /api/orders/{id}/status  →  status: APPROVED  (car → RESERVED)
POST /api/invoices        →  invoice generated
POST /api/payments        →  payment processed
                              car → SOLD, order → COMPLETED, sale record created
```

---

## Project structure

```
src/main/java/com/slingshot/carshowroom/
├── config/         OpenAPI configuration
├── controller/     REST controllers (one per resource)
├── dto/            Request and response records
├── exception/      ResourceNotFoundException, ConflictException, GlobalExceptionHandler
├── model/          JPA entities and enums
├── repository/     Spring Data JPA interfaces
└── service/        Business logic
src/main/resources/
├── application.yaml
└── data.sql        Seed data
```
