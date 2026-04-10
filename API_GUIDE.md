# Postman API Collection Examples

## 1. Authentication
### Register
- **POST** `/api/auth/register`
- **Body (JSON)**:
  ```json
  {
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123",
    "role": "USER"
  }
  ```

### Login
- **POST** `/api/auth/login`
- **Body (JSON)**:
  ```json
  {
    "email": "john@example.com",
    "password": "password123"
  }
  ```
- **Response**: `{ "token": "eyJhbG...", "role": "USER", "userId": 1, "name": "John Doe" }`

### Forgot Password
- **POST** `/api/auth/forgot-password`
- **Body (JSON)**:
  ```json
  { "email": "john@example.com" }
  ```

## 2. Admin Dashboard
- **GET** `/api/admin/dashboard`
- **Headers**: `Authorization: Bearer <JWT_TOKEN>`

## 3. Products
### Get All
- **GET** `/api/products`

### Search
- **GET** `/api/products/search?keyword=Genius`

### Create (Admin Only)
- **POST** `/api/admin/products`
- **Headers**: `Authorization: Bearer <ADMIN_JWT_TOKEN>`
- **Body (JSON)**:
  ```json
  {
    "name": "Speed Plus 7.0",
    "description": "High-speed software solution",
    "category": "Software",
    "tags": "ERP, Speed",
    "status": "ACTIVE"
  }
  ```

## 4. Contact Us
- **POST** `/api/contact`
- **Body (JSON)**:
  ```json
  {
    "name": "Jane Doe",
    "email": "jane@example.com",
    "message": "Interested in your services"
  }
  ```

---

# Startup Instructions

1. **MySQL Setup**:
   - Ensure MySQL is running on port `3306`.
   - Create a database named `sanav_db`.
   - Update `spring.datasource.password` in `src/main/resources/application.properties` if your root password is not `root@123`.

2. **Run in IntelliJ/Eclipse**:
   - Open the project as a Maven project.
   - Wait for dependencies to download.
   - Run `SanavApplication.java` from `com.sanav`.

3. **Run from Terminal**:
   - Open terminal in the project root.
   - Run: `./mvnw spring-boot:run`

4. **Accessing API Documentation**:
   - The server runs on `http://localhost:8081`.
   - Protected routes require a `Bearer` token in the `Authorization` header.
