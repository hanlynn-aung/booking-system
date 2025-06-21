-- Create database if not exists
CREATE DATABASE IF NOT EXISTS booking_system;
USE booking_system;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
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

-- Create packages table
CREATE TABLE IF NOT EXISTS packages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    credits INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    validity_days INT NOT NULL,
    country VARCHAR(10) NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create user_packages table
CREATE TABLE IF NOT EXISTS user_packages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    package_id BIGINT NOT NULL,
    remaining_credits INT NOT NULL,
    purchase_date DATETIME NOT NULL,
    expiry_date DATETIME NOT NULL,
    paid_amount DECIMAL(10,2) NOT NULL,
    status ENUM('ACTIVE', 'EXPIRED', 'USED_UP') DEFAULT 'ACTIVE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (package_id) REFERENCES packages(id)
);

-- Create class_schedules table
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

-- Create class_bookings table
CREATE TABLE IF NOT EXISTS class_bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    class_schedule_id BIGINT NOT NULL,
    user_package_id BIGINT NOT NULL,
    status ENUM('BOOKED', 'WAITLISTED', 'CANCELLED', 'CHECKED_IN', 'NO_SHOW') DEFAULT 'BOOKED',
    booking_time DATETIME NOT NULL,
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
INSERT INTO packages (name, description, credits, price, validity_days, country) VALUES
('Singapore Starter Pack', 'Perfect for beginners in Singapore', 5, 50.00, 30, 'SG'),
('Singapore Premium Pack', 'Best value for regular users in Singapore', 15, 120.00, 60, 'SG'),
('Singapore Unlimited', 'Unlimited classes for power users in Singapore', 50, 300.00, 90, 'SG'),
('Malaysia Basic Pack', 'Entry level package for Malaysia', 3, 30.00, 30, 'MY'),
('Malaysia Standard Pack', 'Standard package for Malaysia', 10, 80.00, 45, 'MY');

INSERT INTO class_schedules (class_name, description, instructor, start_time, end_time, max_capacity, required_credits, country, location, class_type) VALUES
('Morning Yoga', 'Relaxing morning yoga session', 'Sarah Johnson', '2024-01-15 08:00:00', '2024-01-15 09:00:00', 20, 1, 'SG', 'Studio A', 'Yoga'),
('HIIT Training', 'High intensity interval training', 'Mike Chen', '2024-01-15 18:00:00', '2024-01-15 19:00:00', 15, 2, 'SG', 'Studio B', 'Fitness'),
('Pilates Core', 'Core strengthening pilates class', 'Emma Wilson', '2024-01-16 10:00:00', '2024-01-16 11:00:00', 12, 1, 'SG', 'Studio A', 'Pilates'),
('Boxing Fundamentals', 'Learn basic boxing techniques', 'David Lee', '2024-01-16 19:00:00', '2024-01-16 20:00:00', 10, 2, 'MY', 'Studio C', 'Boxing'),
('Meditation & Mindfulness', 'Guided meditation session', 'Lisa Wong', '2024-01-17 07:00:00', '2024-01-17 08:00:00', 25, 1, 'MY', 'Studio D', 'Meditation');