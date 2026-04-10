# 🛠️ Sanav Software - Full Stack Project Guide

This is a complete, beginner-friendly full-stack application using **HTML, CSS, JS, Spring Boot, and MySQL**.

## 📁 Project Structure (Copy-Paste Ready)
```text
Sanav Software/
├── src/main/java/com/sanav/app/
│   ├── controller/      
│   │   └── AuthController.java (POST /register, /login, GET /all)
│   ├── model/           
│   │   └── User.java (id, name, email, password)
│   ├── service/         
│   │   └── UserService.java (Business logic)
│   ├── repository/      
│   │   └── UserRepository.java (Database access)
│   └── config/          
│       └── SecurityConfig.java (Permit-all gateway)
├── src/main/resources/
│   ├── static/          
│   │   ├── index.html   (Home)
│   │   ├── contact.html (Registration form)
│   │   ├── login.html   (Admin login)
│   │   └── admin.html   (Dashboard)
│   └── application.properties (MySQL connection)
└── pom.xml (Dependencies)
```

## 🗄️ 1. Database Setup (MySQL)
Open your MySQL Command Line or Workbench and run:
```sql
CREATE DATABASE sanav_db;
```
The application will automatically create the `users` table for you.

## ⚙️ 2. Configuration
Check `src/main/resources/application.properties` and update your MySQL username and password:
```properties
spring.datasource.username=root
spring.datasource.password=your_password
```

## 🚀 3. How to Run
1. Open your terminal in the project root.
2. Run the command:
   ```powershell
   .\mvnw spring-boot:run
   ```
3. Wait for the message: `Started SanavApplication in ... seconds`.

## 🌐 4. How to Test
1. **Contact/Register**: Open `http://localhost:8081/contact.html`. Fill the form. The "Message" you type will be saved as your "Password".
2. **Login**: Open `http://localhost:8081/login.html`. Log in with the email and password (the message) you just used.
3. **Admin Dashboard**: Once logged in, you will be redirected to `admin.html` where you can see all users and their messages.

---

### 💡 Key Logic Note:
- **BCrypt**: Your messages are encrypted for security. In the Admin Dashboard, you will see the **hashed** version of the message.
- **Unified Flow**: This project uses the `User` entity to store contact submissions, making it simple to manage both registrations and messages in one table.
