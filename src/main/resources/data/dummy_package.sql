INSERT INTO packages (name, description, credits, price, validity_days, country, status, created_at, updated_at)
VALUES
    ('Basic Package', '5 classes in 30 days', 5, 29.99, 30, 'SG', 'ACTIVE', NOW(), NOW()),
    ('Premium Package', '15 classes in 60 days', 15, 69.99, 60, 'MY', 'ACTIVE', NOW(), NOW()),
    ('Trial Package', '2 classes in 7 days', 2, 9.99, 7, 'SG', 'ACTIVE', NOW(), NOW()),
    ('Elite Package', '25 classes in 90 days', 25, 109.99, 90, 'TH', 'ACTIVE', NOW(), NOW()),
    ('Standard Package', '10 classes in 45 days', 10, 49.99, 45, 'MM', 'ACTIVE', NOW(), NOW());
