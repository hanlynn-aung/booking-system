-- User IDs from 1 to 5, alternating packages
INSERT INTO user_packages (user_id, package_id, remaining_credits, purchase_date, expiry_date, paid_amount, status, created_at, updated_at)
VALUES
    (1, 1, 5, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 29.99, 'ACTIVE', NOW(), NOW()),
    (2, 2, 15, NOW(), DATE_ADD(NOW(), INTERVAL 60 DAY), 69.99, 'ACTIVE', NOW(), NOW()),
    (3, 1, 5, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 29.99, 'ACTIVE', NOW(), NOW()),
    (4, 2, 15, NOW(), DATE_ADD(NOW(), INTERVAL 60 DAY), 69.99, 'ACTIVE', NOW(), NOW()),
    (5, 1, 5, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 29.99, 'ACTIVE', NOW(), NOW());
