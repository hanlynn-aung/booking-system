-- Create database if not exists
CREATE DATABASE IF NOT EXISTS booking_system;
USE booking_system;

-- Insert sample packages
INSERT INTO packages (name, description, credits, price, validity_days, country, status, created_at, updated_at) VALUES
('Singapore Starter Pack', '10 credits for fitness classes in Singapore', 10, 150.00, 30, 'SG', 'ACTIVE', NOW(), NOW()),
('Singapore Premium Pack', '25 credits for fitness classes in Singapore', 25, 350.00, 60, 'SG', 'ACTIVE', NOW(), NOW()),
('Singapore Unlimited', '50 credits for fitness classes in Singapore', 50, 650.00, 90, 'SG', 'ACTIVE', NOW(), NOW()),
('Malaysia Basic Pack', '8 credits for fitness classes in Malaysia', 8, 80.00, 30, 'MY', 'ACTIVE', NOW(), NOW()),
('Malaysia Standard Pack', '20 credits for fitness classes in Malaysia', 20, 180.00, 60, 'MY', 'ACTIVE', NOW(), NOW()),
('Malaysia Premium Pack', '40 credits for fitness classes in Malaysia', 40, 320.00, 90, 'MY', 'ACTIVE', NOW(), NOW());

-- Insert sample class schedules for Singapore
INSERT INTO class_schedules (class_name, description, instructor, start_time, end_time, max_capacity, required_credits, country, location, class_type, status, created_at, updated_at) VALUES
('Morning Yoga', 'Relaxing yoga session to start your day', 'Sarah Johnson', '2024-12-20 07:00:00', '2024-12-20 08:00:00', 20, 1, 'SG', 'Marina Bay Studio', 'Yoga', 'SCHEDULED', NOW(), NOW()),
('HIIT Workout', 'High-intensity interval training', 'Mike Chen', '2024-12-20 18:00:00', '2024-12-20 19:00:00', 15, 2, 'SG', 'Orchard Fitness Center', 'HIIT', 'SCHEDULED', NOW(), NOW()),
('Pilates Core', 'Core strengthening pilates class', 'Emma Wong', '2024-12-21 10:00:00', '2024-12-21 11:00:00', 12, 2, 'SG', 'Sentosa Wellness Hub', 'Pilates', 'SCHEDULED', NOW(), NOW()),
('Evening Zumba', 'Fun dance fitness class', 'Carlos Rodriguez', '2024-12-21 19:30:00', '2024-12-21 20:30:00', 25, 1, 'SG', 'Clarke Quay Studio', 'Dance', 'SCHEDULED', NOW(), NOW()),
('Power Lifting', 'Strength training with weights', 'David Tan', '2024-12-22 08:00:00', '2024-12-22 09:30:00', 10, 3, 'SG', 'Raffles Place Gym', 'Strength', 'SCHEDULED', NOW(), NOW());

-- Insert sample class schedules for Malaysia
INSERT INTO class_schedules (class_name, description, instructor, start_time, end_time, max_capacity, required_credits, country, location, class_type, status, created_at, updated_at) VALUES
('Sunrise Yoga', 'Peaceful morning yoga session', 'Aishah Rahman', '2024-12-20 06:30:00', '2024-12-20 07:30:00', 18, 1, 'MY', 'KLCC Wellness Center', 'Yoga', 'SCHEDULED', NOW(), NOW()),
('CrossFit Challenge', 'Intense functional fitness workout', 'Ahmad Zaki', '2024-12-20 17:00:00', '2024-12-20 18:00:00', 12, 2, 'MY', 'Bukit Bintang Gym', 'CrossFit', 'SCHEDULED', NOW(), NOW()),
('Aqua Aerobics', 'Low-impact water-based exercise', 'Lim Wei Ming', '2024-12-21 11:00:00', '2024-12-21 12:00:00', 15, 1, 'MY', 'Mont Kiara Pool', 'Aqua', 'SCHEDULED', NOW(), NOW()),
('Muay Thai Basics', 'Introduction to Muay Thai techniques', 'Somchai Jaidee', '2024-12-21 20:00:00', '2024-12-21 21:00:00', 16, 2, 'MY', 'Bangsar Combat Gym', 'Martial Arts', 'SCHEDULED', NOW(), NOW()),
('Spin Class', 'High-energy indoor cycling', 'Jennifer Lau', '2024-12-22 07:00:00', '2024-12-22 08:00:00', 20, 2, 'MY', 'Petaling Jaya Fitness', 'Cardio', 'SCHEDULED', NOW(), NOW());

-- Create indexes for better performance
CREATE INDEX idx_packages_country_status ON packages(country, status);
CREATE INDEX idx_class_schedules_country_status ON class_schedules(country, status);
CREATE INDEX idx_class_schedules_start_time ON class_schedules(start_time);
CREATE INDEX idx_user_packages_user_country ON user_packages(user_id, package_id);
CREATE INDEX idx_class_bookings_user ON class_bookings(user_id);
CREATE INDEX idx_class_bookings_schedule_status ON class_bookings(class_schedule_id, status);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_verification_token ON users(verification_token);