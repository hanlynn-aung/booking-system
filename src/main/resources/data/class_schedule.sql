INSERT INTO class_schedules (class_name, description, instructor, start_time, end_time, max_capacity, required_credits, country, location, class_type, status, created_at, updated_at)
VALUES
    ('Morning Yoga', 'A relaxing yoga session', 'Alice', '2025-06-25 07:00:00', '2025-06-25 08:00:00', 20, 1, 'MM', 'Room A', 'Yoga', 'SCHEDULED', NOW(), NOW()),
    ('Zumba Dance', 'Fun cardio dance class', 'Eva', '2025-06-25 09:00:00', '2025-06-25 10:00:00', 15, 1, 'MM', 'Room B', 'Zumba', 'SCHEDULED', NOW(), NOW()),
    ('Evening HIIT', 'High intensity interval training', 'Bob', '2025-06-25 18:00:00', '2025-06-25 19:00:00', 15, 2, 'MM', 'Room C', 'HIIT', 'SCHEDULED', NOW(), NOW());
