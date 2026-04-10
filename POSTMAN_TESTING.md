# Postman API Collection (Backend Integration)

## 1. Authentication
### Register
- **POST** `/api/auth/register`
- **Body**:
  ```json
  {
    "name": "Jane User",
    "email": "jane@example.com",
    "password": "password123",
    "role": "USER"
  }
  ```

### Login
- **POST** `/api/auth/login`
- **Body**:
  ```json
  {
    "email": "jane@example.com",
    "password": "password123"
  }
  ```

## 2. User Profile
### Get Profile
- **GET** `/api/user/profile`
- **Header**: `Authorization: Bearer <TOKEN>`

### Update Profile
- **PUT** `/api/user/profile`
- **Header**: `Authorization: Bearer <TOKEN>`
- **Body**:
  ```json
  {
    "name": "Jane Updated",
    "email": "jane@example.com",
    "password": "newpassword123"
  }
  ```

### Change Password
- **PUT** `/api/user/change-password`
- **Header**: `Authorization: Bearer <TOKEN>`
- **Body**:
  ```json
  {
    "oldPassword": "password123",
    "newPassword": "newpassword123"
  }
  ```

## 3. Services
### List Services
- **GET** `/api/services`

## 4. Products
### List Products
- **GET** `/api/products`

### Add Product (Admin Only)
- **POST** `/api/products`
- **Header**: `Authorization: Bearer <ADMIN_TOKEN>`
- **Body**:
  ```json
  {
    "name": "New Product",
    "description": "High-end retail software.",
    "imageUrl": "https://via.placeholder.com/150"
  }
  ```

## 5. Industrial Training
### List Course
- **GET** `/api/courses`

## 6. Clients
### List Clients
- **GET** `/api/clients`

## 7. Contact Us
### Send Message
- **POST** `/api/contact`
- **Body**:
  ```json
  {
    "name": "John Doe",
    "email": "john@example.com",
    "message": "Interested in GST software."
  }
  ```

## 8. Certificate Verification
### Verify Certificate
- **GET** `/api/certificate/SANAV-CERT-ABC12345`

## 9. Admin Dashboard
### Get Stats
- **GET** `/api/admin/dashboard`
- **Header**: `Authorization: Bearer <ADMIN_TOKEN>`
