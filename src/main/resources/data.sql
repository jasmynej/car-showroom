-- =============================================================
-- Users: 1 manager, 2 staff, 3 customers
-- =============================================================
INSERT INTO "users" ("user_id","name","email","password","contact_info","role","department","designation") VALUES
  (1, 'Alice Johnson',  'alice@showroom.com',   'pass123', '555-0101', 'MANAGER',  'Sales',   NULL),
  (2, 'Bob Smith',      'bob@showroom.com',     'pass123', '555-0102', 'STAFF',    NULL,      'Sales Representative'),
  (3, 'Carol White',    'carol@showroom.com',   'pass123', '555-0103', 'STAFF',    NULL,      'Service Technician'),
  (4, 'David Brown',    'david@example.com',    'pass123', '555-0201', 'CUSTOMER', NULL,      NULL),
  (5, 'Emma Davis',     'emma@example.com',     'pass123', '555-0202', 'CUSTOMER', NULL,      NULL),
  (6, 'Frank Wilson',   'frank@example.com',    'pass123', '555-0203', 'CUSTOMER', NULL,      NULL);

ALTER TABLE "users" ALTER COLUMN "user_id" RESTART WITH 7;

-- =============================================================
-- Cars: mix of AVAILABLE, RESERVED, and SOLD
-- =============================================================
INSERT INTO "car" ("vin","make","model","year","price","color","mileage","availability_status","last_service_date","owner_id","last_updated") VALUES
  ('1HGCM82633A004352', 'Honda',      'Accord',  2023, 28500.00, 'Silver', 1200.0,  'AVAILABLE', '2024-12-01', NULL, '2025-01-10'),
  ('2T1BURHE0JC043821', 'Toyota',     'Corolla', 2022, 24900.00, 'White',  8500.0,  'AVAILABLE', '2024-11-15', NULL, '2025-01-10'),
  ('3VWFE21C04M000001', 'Volkswagen', 'Golf',    2021, 22000.00, 'Blue',   15000.0, 'RESERVED',  '2024-10-20', NULL, '2025-01-15'),
  ('4S3BMHB68B3286050', 'Subaru',     'Legacy',  2020, 18500.00, 'Red',    32000.0, 'SOLD',      '2024-09-05', 4,    '2025-01-20'),
  ('5NPE24AF8FH052952', 'Hyundai',    'Sonata',  2023, 26000.00, 'Black',  3000.0,  'AVAILABLE', '2025-01-05', NULL, '2025-01-10'),
  ('1FTFW1ET0EFA37167', 'Ford',       'F-150',   2022, 42000.00, 'Grey',   18000.0, 'AVAILABLE', '2024-08-30', NULL, '2025-01-10');

-- =============================================================
-- Purchase Orders
--   1: COMPLETED  (Subaru Legacy  → David Brown)
--   2: APPROVED   (VW Golf        → Emma Davis)  — car is RESERVED
--   3: PENDING    (Honda Accord   → Frank Wilson)
-- =============================================================
INSERT INTO "purchase_order" ("order_id","customer_id","vin","date","comments","status") VALUES
  (1, 4, '4S3BMHB68B3286050', '2025-01-10', 'First car purchase, very excited!',    'COMPLETED'),
  (2, 5, '3VWFE21C04M000001', '2025-01-14', 'Interested in the blue Golf.',          'APPROVED'),
  (3, 6, '1HGCM82633A004352', '2025-01-18', 'Would like to buy the silver Accord.',  'PENDING');

ALTER TABLE "purchase_order" ALTER COLUMN "order_id" RESTART WITH 4;

-- =============================================================
-- Invoices
--   1: for completed order (Subaru Legacy)
--   2: for approved order  (VW Golf) — awaiting payment
-- =============================================================
INSERT INTO "invoice" ("invoice_id","order_id","customer_id","price","tax","total_amount","date","terms_and_conditions") VALUES
  (1, 1, 4, 18500.00, 13.00, 20905.00, '2025-01-13', 'Payment due within 7 days. No refunds after delivery.'),
  (2, 2, 5, 22000.00, 13.00, 24860.00, '2025-01-16', 'Payment due within 7 days. No refunds after delivery.');

ALTER TABLE "invoice" ALTER COLUMN "invoice_id" RESTART WITH 3;

-- =============================================================
-- Payments
--   1: PAID (Subaru Legacy — credit card)
--   Invoice 2 (VW Golf) is intentionally unpaid so the app has
--   a live payment to demonstrate via POST /api/payments
-- =============================================================
INSERT INTO "payment" ("transaction_id","invoice_id","customer_id","vin","amount","status","payment_date","payment_method","account_number","pin","bank","credit_card_number","cvv_code") VALUES
  (1, 1, 4, '4S3BMHB68B3286050', 20905.00, 'PAID', '2025-01-14', 'CREDIT_CARD', NULL, NULL, NULL, '4111111111111111', '123');

ALTER TABLE "payment" ALTER COLUMN "transaction_id" RESTART WITH 2;

-- =============================================================
-- Sales
-- =============================================================
INSERT INTO "sale" ("sale_id","sale_date","vin") VALUES
  (1, '2025-01-14', '4S3BMHB68B3286050');

ALTER TABLE "sale" ALTER COLUMN "sale_id" RESTART WITH 2;

-- =============================================================
-- Test Drives
-- =============================================================
INSERT INTO "test_drive" ("test_drive_id","vin","customer_id","date","time","status","comments") VALUES
  (1, '1FTFW1ET0EFA37167', 4, '2025-01-08', '10:00 AM', 'COMPLETED', 'Customer loved the truck.'),
  (2, '5NPE24AF8FH052952', 5, '2025-01-20', '2:00 PM',  'SCHEDULED', 'First time test drive.'),
  (3, '2T1BURHE0JC043821', 6, '2025-01-12', '11:00 AM', 'CANCELLED', 'Customer cancelled due to scheduling conflict.');

ALTER TABLE "test_drive" ALTER COLUMN "test_drive_id" RESTART WITH 4;

-- =============================================================
-- Service Schedules
-- =============================================================
INSERT INTO "service_schedule" ("service_id","vin","service_type","date","status","comments","staff_id") VALUES
  (1, '4S3BMHB68B3286050', 'Oil Change',              '2024-09-05', 'COMPLETED', 'Full synthetic oil change completed.',  3),
  (2, '2T1BURHE0JC043821', 'Tire Rotation',           '2025-01-25', 'SCHEDULED', 'Scheduled for regular tire rotation.',  3),
  (3, '1HGCM82633A004352', 'Multi-Point Inspection',  '2025-01-22', 'COMPLETED', 'Pre-sale inspection passed.',           3);

ALTER TABLE "service_schedule" ALTER COLUMN "service_id" RESTART WITH 4;
