-- Assume ClassSchedule IDs: 1 = Yoga, 2 = Zumba, 3 = HIIT
-- Assume UserPackage IDs: 1–5 matching users

INSERT INTO class_bookings (user_id, class_schedule_id, user_package_id, status, booking_time, created_at, updated_at)
VALUES
(1, 1, 1, 'BOOKED', NOW(), NOW(), NOW()),
(2, 3, 2, 'BOOKED', NOW(), NOW(), NOW()),
(3, 2, 3, 'BOOKED', NOW(), NOW(), NOW()),
(4, 1, 4, 'BOOKED', NOW(), NOW(), NOW()),
(5, 2, 5, 'BOOKED', NOW(), NOW(), NOW());
