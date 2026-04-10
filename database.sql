-- MySQL Database Script for sanav_db
CREATE DATABASE IF NOT EXISTS sanav_db;
USE sanav_db;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    reset_token VARCHAR(255),
    reset_token_expiry TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Services
CREATE TABLE IF NOT EXISTS services (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    icon VARCHAR(255)
);

-- Products
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    image_url VARCHAR(255)
);

-- Training Courses
CREATE TABLE IF NOT EXISTS training_courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    duration VARCHAR(255),
    fees DOUBLE
);

-- Clients
CREATE TABLE IF NOT EXISTS clients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    logo_url VARCHAR(255),
    review TEXT
);

-- Contact Messages
CREATE TABLE IF NOT EXISTS contact_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    created_at DATETIME
);

-- Certificates
CREATE TABLE IF NOT EXISTS certificates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_name VARCHAR(255) NOT NULL,
    certificate_code VARCHAR(255) UNIQUE NOT NULL,
    course_name VARCHAR(255) NOT NULL,
    issue_date DATETIME,
    created_at DATETIME
);

-- Demo Requests table
CREATE TABLE IF NOT EXISTS demo_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    company VARCHAR(255),
    product_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Initial Admin (Password: admin123)
INSERT IGNORE INTO users (name, email, password, role) 
VALUES ('Admin', 'admin@sanav.com', '$2a$10$8.VAgWp.S4I9zVv9j/sHFO56.WjU05/01Z6W8z7Q5z1Q5z1Q5z1Q.', 'ADMIN');

-- Sample Data Inserts
INSERT INTO services (title, description, icon) VALUES 
('Web Development', 'High-quality web applications.', 'fa-code'),
('Mobile Apps', 'Modern mobile software solutions.', 'fa-mobile'),
('SEO Optimization', 'Get your site ranked higher.', 'fa-search');

INSERT INTO products (name, description, image_url) VALUES 
('Sanav Speed Plus', 'The ultimate ERP for retailers.', 'https://via.placeholder.com/150'),
('Genius GST', 'GST billing made easy.', 'https://via.placeholder.com/150'),
('Retail Master', 'Manage your stock with ease.', 'https://via.placeholder.com/150');

INSERT INTO training_courses (title, description, duration, fees) VALUES 
('Java Full Stack', 'Master Java, Spring and React.', '6 Months', 45000),
('Data Science', 'Learn Python and Machine Learning.', '4 Months', 35000),
('AWS Cloud', 'Become a certified AWS Architect.', '3 Months', 25000);

INSERT INTO clients (name, logo_url, review) VALUES 
('ABC Corp', 'https://via.placeholder.com/50', 'Great software and support!'),
('XYZ Retail', 'https://via.placeholder.com/50', 'Speed Plus changed our business.'),
('Global Tech', 'https://via.placeholder.com/50', 'Very professional team.');
