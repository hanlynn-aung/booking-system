-- Create database if not exists
CREATE DATABASE IF NOT EXISTS booking_system;
USE booking_system;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    status ENUM('PENDING_VERIFICATION', 'ACTIVE', 'SUSPENDED', 'INACTIVE') DEFAULT 'PENDING_VERIFICATION',
    verification_token VARCHAR(255),
    verification_token_expiry DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Packages table
CREATE TABLE IF NOT EXISTS packages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    credits INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    validity_days INT NOT NULL,
    country VARCHAR(20) NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- User packages table
CREATE TABLE IF NOT EXISTS user_packages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    package_id BIGINT NOT NULL,
    remaining_credits INT NOT NULL,
    purchase_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    expiry_date DATETIME NOT NULL,
    paid_amount DECIMAL(10,2) NOT NULL,
    status ENUM('ACTIVE', 'EXPIRED', 'USED_UP') DEFAULT 'ACTIVE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (package_id) REFERENCES packages(id)
);

-- Class schedules table
CREATE TABLE IF NOT EXISTS class_schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_name VARCHAR(100) NOT NULL,
    description TEXT,
    instructor VARCHAR(100) NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    max_capacity INT NOT NULL,
    required_credits INT NOT NULL,
    country VARCHAR(10) NOT NULL,
    location VARCHAR(100),
    class_type VARCHAR(50),
    status ENUM('SCHEDULED', 'ONGOING', 'COMPLETED', 'CANCELLED') DEFAULT 'SCHEDULED',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Class bookings table
CREATE TABLE IF NOT EXISTS class_bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    class_schedule_id BIGINT NOT NULL,
    user_package_id BIGINT NOT NULL,
    status ENUM('BOOKED', 'WAITLISTED', 'CANCELLED', 'CHECKED_IN', 'NO_SHOW') DEFAULT 'BOOKED',
    booking_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    cancellation_time DATETIME,
    check_in_time DATETIME,
    waitlist_position INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (class_schedule_id) REFERENCES class_schedules(id),
    FOREIGN KEY (user_package_id) REFERENCES user_packages(id)
);

-- Insert sample data

-- Sample users (password is 'password' encoded with BCrypt)
INSERT INTO users (username, email, password, first_name, last_name, phone_number, status, verification_token, verification_token_expiry) VALUES
('john_doe', 'john@example.com', '$2a$12$zPJZLFeORoCPbE.pVua7g.QwY64aiN1uLYnH4Iua6Yvv.INmAQLkG', 'John', 'Doe', '+1234567890', 'ACTIVE', NULL, NULL),
('jane_smith', 'jane@example.com', '$2a$12$zPJZLFeORoCPbE.pVua7g.QwY64aiN1uLYnH4Iua6Yvv.INmAQLkG', 'Jane', 'Smith', '+1234567891', 'ACTIVE', NULL, NULL),
('bob_wilson', 'bob@example.com', '$2a$12$zPJZLFeORoCPbE.pVua7g.QwY64aiN1uLYnH4Iua6Yvv.INmAQLkG', 'Bob', 'Wilson', '+1234567892', 'ACTIVE', NULL, NULL),
('alice_brown', 'alice@example.com', '$2a$12$zPJZLFeORoCPbE.pVua7g.QwY64aiN1uLYnH4Iua6Yvv.INmAQLkG', 'Alice', 'Brown', '+1234567893', 'PENDING_VERIFICATION', 'verification-token-123', DATE_ADD(NOW(), INTERVAL 1 HOUR)),
('charlie_davis', 'charlie@example.com', '$2a$12$zPJZLFeORoCPbE.pVua7g.QwY64aiN1uLYnH4Iua6Yvv.INmAQLkG', 'Charlie', 'Davis', '+1234567894', 'ACTIVE', NULL, NULL);

-- Sample packages for different countries
INSERT INTO packages (name, description, credits, price, validity_days, country, status) VALUES
('SG Basic Package', '5 classes for Singapore', 5, 50.00, 30, 'SG', 'ACTIVE'),
('SG Premium Package', '15 classes for Singapore', 15, 120.00, 60, 'SG', 'ACTIVE'),
('SG Elite Package', '25 classes for Singapore', 25, 180.00, 90, 'SG', 'ACTIVE'),
('MY Basic Package', '5 classes for Malaysia', 5, 35.00, 30, 'MY', 'ACTIVE'),
('MY Premium Package', '15 classes for Malaysia', 15, 85.00, 60, 'MY', 'ACTIVE'),
('MY Elite Package', '25 classes for Malaysia', 25, 125.00, 90, 'MY', 'ACTIVE');

-- Sample user packages
INSERT INTO user_packages (user_id, package_id, remaining_credits, purchase_date, expiry_date, paid_amount, status) VALUES
(1, 1, 3, NOW(), DATE_ADD(NOW(), INTERVAL 25 DAY), 50.00, 'ACTIVE'),
(1, 4, 5, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 35.00, 'ACTIVE'),
(2, 2, 12, NOW(), DATE_ADD(NOW(), INTERVAL 55 DAY), 120.00, 'ACTIVE'),
(3, 5, 8, NOW(), DATE_ADD(NOW(), INTERVAL 45 DAY), 85.00, 'ACTIVE'),
(5, 1, 4, NOW(), DATE_ADD(NOW(), INTERVAL 20 DAY), 50.00, 'ACTIVE');

-- Sample class schedules
INSERT INTO class_schedules (class_name, description, instructor, start_time, end_time, max_capacity, required_credits, country, location, class_type, status) VALUES
-- Singapore classes
('Morning Yoga SG', 'Relaxing morning yoga session', 'Sarah Lee', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 1 DAY), INTERVAL 1 HOUR), 15, 1, 'SG', 'Studio A', 'Yoga', 'SCHEDULED'),
('HIIT Training SG', 'High intensity interval training', 'Mike Chen', DATE_ADD(NOW(), INTERVAL 2 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 2 DAY), INTERVAL 1 HOUR), 10, 2, 'SG', 'Studio B', 'HIIT', 'SCHEDULED'),
('Evening Pilates SG', 'Core strengthening pilates', 'Lisa Wang', DATE_ADD(NOW(), INTERVAL 3 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 3 DAY), INTERVAL 1 HOUR), 12, 1, 'SG', 'Studio C', 'Pilates', 'SCHEDULED'),
('Zumba Dance SG', 'Fun cardio dance workout', 'Maria Santos', DATE_ADD(NOW(), INTERVAL 4 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 4 DAY), INTERVAL 1 HOUR), 20, 1, 'SG', 'Studio D', 'Dance', 'SCHEDULED'),
('Power Yoga SG', 'Dynamic power yoga flow', 'David Kim', DATE_ADD(NOW(), INTERVAL 5 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 5 DAY), INTERVAL 1.5 HOUR), 8, 2, 'SG', 'Studio A', 'Yoga', 'SCHEDULED'),

-- Malaysia classes
('Morning Yoga MY', 'Gentle morning yoga practice', 'Siti Rahman', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 1 DAY), INTERVAL 1 HOUR), 15, 1, 'MY', 'Room 1', 'Yoga', 'SCHEDULED'),
('CrossFit MY', 'Functional fitness training', 'Ahmad Hassan', DATE_ADD(NOW(), INTERVAL 2 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 2 DAY), INTERVAL 1 HOUR), 8, 2, 'MY', 'Room 2', 'CrossFit', 'SCHEDULED'),
('Meditation MY', 'Mindfulness meditation session', 'Priya Sharma', DATE_ADD(NOW(), INTERVAL 3 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 3 DAY), INTERVAL 45 MINUTE), 20, 1, 'MY', 'Room 3', 'Meditation', 'SCHEDULED'),
('Kickboxing MY', 'High energy kickboxing class', 'Tony Lim', DATE_ADD(NOW(), INTERVAL 4 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 4 DAY), INTERVAL 1 HOUR), 12, 2, 'MY', 'Room 2', 'Martial Arts', 'SCHEDULED'),
('Hatha Yoga MY', 'Traditional hatha yoga practice', 'Mei Ling', DATE_ADD(NOW(), INTERVAL 5 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 5 DAY), INTERVAL 1.5 HOUR), 10, 1, 'MY', 'Room 1', 'Yoga', 'SCHEDULED');

-- Sample bookings
INSERT INTO class_bookings (user_id, class_schedule_id, user_package_id, status, booking_time) VALUES
(1, 1, 1, 'BOOKED', NOW()),
(2, 1, 3, 'BOOKED', NOW()),
(3, 7, 4, 'BOOKED', NOW()),
(1, 6, 2, 'BOOKED', NOW()),
(5, 2, 5, 'WAITLISTED', NOW());

-- Update waitlist position for waitlisted booking
UPDATE class_bookings SET waitlist_position = 1 WHERE status = 'WAITLISTED';

-- Create indexes for better performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_packages_country_status ON packages(country, status);
CREATE INDEX idx_user_packages_user_status ON user_packages(user_id, status);
CREATE INDEX idx_user_packages_expiry ON user_packages(expiry_date);
CREATE INDEX idx_class_schedules_country_status ON class_schedules(country, status);
CREATE INDEX idx_class_schedules_start_time ON class_schedules(start_time);
CREATE INDEX idx_class_bookings_user_status ON class_bookings(user_id, status);
CREATE INDEX idx_class_bookings_class_status ON class_bookings(class_schedule_id, status);
CREATE INDEX idx_class_bookings_waitlist ON class_bookings(class_schedule_id, status, waitlist_position);