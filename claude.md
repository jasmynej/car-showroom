# Car Showroom Management System — Spring Boot API

## Project Overview

REST API rewrite of a Java Swing desktop car showroom management system.
Demo project using an H2 in-memory database.

**Stack:** Spring Boot 3.x · Spring Data JPA · H2 · Java 17  
**No authentication required** (demo scope)

---

## Architecture

```
src/main/java/com/showroom/
├── model/          # JPA entities
├── repository/     # Spring Data JPA interfaces
├── service/        # Business logic
├── controller/     # REST controllers
├── dto/            # Request/Response bodies
└── exception/      # Custom exceptions + global handler
```

---

## Data Model

### Simplifications from the original

The original schema had structural issues that should be cleaned up in the rewrite:

- **`Inventory` table is redundant** — it was a separate table holding only VINs with a `LastUpdated` date, then joined back to `Car`. Collapse it: add `lastUpdated` directly to `Car`.
- **`Customer.ownedCars`** was a single `VARCHAR(17)` FK, which cannot represent multiple cars. Replace with a derived query (`SELECT * FROM Car WHERE owner = customerId`).
- **`PaymentDetails` table** (referenced in the Java code but absent from the SQL) — ignore it; treat `Payment` as the single payment record.
- **`Sales` and `Report`** tables exist in SQL but had no business logic implemented. Include the entities but keep endpoints read-only for now.
- **User inheritance** — the original used separate `Manager`, `Staff`, `Customer` tables joined to `Users`. Model this with a `role` enum on the `User` entity (single-table strategy is simplest for H2/demo).

---

## Entities

### `Car`
```
vin (PK, VARCHAR 17)
make
model
year
price (Double)
color
mileage (Double)
availabilityStatus  // "Available" | "Sold" | "Reserved"
lastServiceDate (LocalDate)
ownerId (Int, nullable)  // customerId when sold, else null
lastUpdated (LocalDate)
```

### `User`
```
userId (PK, auto-increment)
name
email (unique)
password
contactInfo
role  // Enum: CUSTOMER | STAFF | MANAGER
department  // nullable, Manager only
designation // nullable, Staff only
```

### `PurchaseOrder`
```
orderId (PK, auto-increment)
customerId (FK → User)
vin (FK → Car)
date (LocalDate)
comments
status  // Enum: PENDING | APPROVED | REJECTED | COMPLETED
```

### `Invoice`
```
invoiceId (PK, auto-increment)
orderId (FK → PurchaseOrder)
customerId (FK → User)
price (Double)
tax (Double)
totalAmount (Double)
date (LocalDate)
termsAndConditions
```

### `Payment`
```
transactionId (PK, auto-increment)
invoiceId (FK → Invoice)
customerId (FK → User)
vin
amount (Double)
status  // Enum: PAID | UNPAID
paymentDate (LocalDate)
paymentMethod  // Enum: CASH | CREDIT_CARD
// method-specific fields on the same table (single-table, demo simplicity):
accountNumber   // Cash
pin             // Cash
bank            // Cash
creditCardNumber // CreditCard
cvvCode         // CreditCard
```

### `TestDrive`
```
testDriveId (PK, auto-increment)
vin (FK → Car)
customerId (FK → User)
date (LocalDate)
time (String)
status  // Enum: SCHEDULED | COMPLETED | CANCELLED
comments
```

### `ServiceSchedule`
```
serviceId (PK, auto-increment)
vin (FK → Car)
serviceType
date (LocalDate)
status  // Enum: SCHEDULED | COMPLETED | CANCELLED
comments
staffId (FK → User, nullable)
```

### `Sale`
```
saleId (PK, auto-increment)
saleDate (LocalDate)
vin (FK → Car)
```

---

## Business Logic

### Car / Inventory

- **Add car:** Create `Car` record with `availabilityStatus = "Available"`, `ownerId = null`, `lastUpdated = today`.
- **Delete car:** Remove car record. Should be blocked (return 409) if the car has a `PENDING` or `APPROVED` purchase order.
- **Update car:** Patch allowed fields (`make`, `model`, `year`, `color`, `mileage`, `price`, `availabilityStatus`). `vin` is immutable.
- **List available cars:** Filter by `availabilityStatus = "Available"`.

### Users

- **Register user:** Accept `role` in the request. Set `department` if MANAGER, `designation` if STAFF.
- **Get user:** Return user info. For CUSTOMER role, also return list of cars they own (query Car by `ownerId`).
- **Change password:** Simple update by `userId`.
- **No login/session required** for demo scope.

### Purchase Orders

- **Create order:** Customer submits a VIN + comments. Set `status = PENDING`, `date = today`. Validate: car must exist and have `availabilityStatus = "Available"`.
- **Approve order (Manager):** Set `status = APPROVED`. Optionally set car to `"Reserved"` to prevent double-orders.
- **Reject order:** Set `status = REJECTED`.
- **Complete order:** Triggered when payment is confirmed (see Payment logic). Sets `status = COMPLETED`.

### Invoices

- **Generate invoice:** Created by staff/manager after a purchase order is approved. Requires `orderId`. Compute `totalAmount = price + (price * tax / 100)`. `date = today`.
- **One invoice per order** — validate that an invoice doesn't already exist for the given `orderId`.

### Payments

- **Make payment:** Linked to an `invoiceId`. Accept `paymentMethod` (CASH or CREDIT_CARD) and the corresponding fields.
  - On success: set `Payment.status = PAID`.
  - Trigger: update `Car.availabilityStatus = "Sold"`, set `Car.ownerId = customerId`.
  - Trigger: update `PurchaseOrder.status = COMPLETED`.
  - Trigger: create a `Sale` record with `saleDate = today` and the VIN.
- **One payment per invoice** — validate no existing PAID payment for the same `invoiceId`.

### Test Drives

- **Schedule:** Customer requests a VIN + date + time. Validate car exists. Set `status = SCHEDULED`.
- **Cancel:** Set `status = CANCELLED`.
- **Complete:** Set `status = COMPLETED` (staff action).
- No double-booking rule required for demo scope.

### Service Schedules

- **Schedule service:** Provide VIN, serviceType, date, comments, optional staffId. Set `status = SCHEDULED`.
- **Complete:** Set `status = COMPLETED`.
- **Cancel:** Set `status = CANCELLED`.

---

## REST Endpoints

### Cars
```
GET    /api/cars                  # all cars
GET    /api/cars/available         # filter by status = Available
GET    /api/cars/{vin}
POST   /api/cars
PUT    /api/cars/{vin}
DELETE /api/cars/{vin}
```

### Users
```
GET    /api/users/{id}
POST   /api/users
PUT    /api/users/{id}/password
```

### Purchase Orders
```
GET    /api/orders
GET    /api/orders/{id}
POST   /api/orders
PATCH  /api/orders/{id}/status    # body: { "status": "APPROVED" }
```

### Invoices
```
GET    /api/invoices/{id}
POST   /api/invoices              # body includes orderId
```

### Payments
```
GET    /api/payments/{id}
POST   /api/payments              # body includes invoiceId, method, amount, method-specific fields
```

### Test Drives
```
GET    /api/test-drives
GET    /api/test-drives/{id}
POST   /api/test-drives
PATCH  /api/test-drives/{id}/status
```

### Service Schedules
```
GET    /api/services
GET    /api/services/{id}
POST   /api/services
PATCH  /api/services/{id}/status
```

### Sales
```
GET    /api/sales                  # read-only
```

---

## H2 Configuration (`application.yml`)

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:carshowroom;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
  sql:
    init:
      mode: always       # loads data.sql on startup if present
```

Seed data goes in `src/main/resources/data.sql`. Mirror the original SQL inserts, adapted for the simplified schema.

---

## Key Implementation Notes

1. **Drop the `Inventory` singleton pattern.** The original used a static in-memory singleton as a secondary cache alongside the DB. In the API version the DB is the single source of truth — no in-memory list.

2. **Use `@Transactional` on payment completion.** The payment POST triggers three side-effect writes (Car update, PurchaseOrder update, Sale insert). Wrap the service method so all three succeed or all roll back.

3. **Status transitions should be validated in the service layer**, not the controller. For example, you can't complete a payment on an already-PAID invoice — throw a custom exception and let the global handler return a 409.

4. **DTOs, not entities, cross the API boundary.** Keep JPA entities internal. Use request DTOs for input and response DTOs for output — this avoids accidentally exposing password fields or circular serialization issues (e.g. Car → ServiceSchedule → Car).

5. **VIN is a natural key, not auto-incremented.** Map it as `@Id` directly with no `@GeneratedValue`. Validate format (17 alphanumeric chars) in the request DTO.

6. **Single-table inheritance for Payment** is the right call for demo scope. Use a `paymentMethod` discriminator field and nullable columns for method-specific data rather than joined tables.
