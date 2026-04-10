# Frontend JavaScript Integration Guide

Add these snippets to your HTML files to connect them to the Spring Boot backend.

## 1. Global Configuration (Add to all pages)
Add this at the top of your scripts to handle the base URL and authentication.

```javascript
const API_BASE_URL = "http://localhost:8081/api";

// Helper to get headers with JWT token if available
function getHeaders() {
    const token = localStorage.getItem("token");
    const headers = {
        "Content-Type": "application/json"
    };
    if (token) {
        headers["Authorization"] = `Bearer ${token}`;
    }
    return headers;
}
```

## 2. User Registration (signup.html)
```javascript
async function handleRegister(event) {
    event.preventDefault();
    const formData = new FormData(event.target);
    const data = Object.fromEntries(formData.entries());

    try {
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        });
        const result = await response.json();
        if (result.status === "success") {
            alert("Registration successful! Please login.");
            window.location.href = "login.html";
        } else {
            alert("Error: " + result.message);
        }
    } catch (error) {
        console.error("Registration failed", error);
    }
}
```

## 3. User Login (login.html)
```javascript
async function handleLogin(event) {
    event.preventDefault();
    const formData = new FormData(event.target);
    const data = Object.fromEntries(formData.entries());

    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        });
        const result = await response.json();
        if (result.status === "success") {
            localStorage.setItem("token", result.data.token);
            localStorage.setItem("user", JSON.stringify(result.data.user));
            
            // Redirect based on role
            if (result.data.user.role === "ADMIN") {
                window.location.href = "admin-dashboard.html";
            } else {
                window.location.href = "index.html";
            }
        } else {
            alert("Login failed: " + result.message);
        }
    } catch (error) {
        console.error("Login failed", error);
    }
}
```

## 4. Contact Form (contact.html)
```javascript
async function handleContact(event) {
    event.preventDefault();
    const formData = new FormData(event.target);
    const data = Object.fromEntries(formData.entries());

    try {
        const response = await fetch(`${API_BASE_URL}/contact`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        });
        const result = await response.json();
        if (result.status === "success") {
            alert("Message sent successfully!");
            event.target.reset();
        }
    } catch (error) {
        console.error("Contact submission failed", error);
    }
}
```

## 5. Demo Request Form (product-details.html)
```javascript
async function handleDemoRequest(event) {
    event.preventDefault();
    const formData = new FormData(event.target);
    const data = Object.fromEntries(formData.entries());

    try {
        const response = await fetch(`${API_BASE_URL}/demo-requests`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        });
        const result = await response.json();
        if (result.status === "success") {
            alert("Demo request submitted! We will contact you soon.");
            event.target.reset();
        }
    } catch (error) {
        console.error("Demo request failed", error);
    }
}
```

## 6. Product List (products.html)
```javascript
async function fetchProducts() {
    try {
        const response = await fetch(`${API_BASE_URL}/products`);
        const result = await response.json();
        if (result.status === "success") {
            const container = document.getElementById("product-container");
            container.innerHTML = result.data.map(product => `
                <div class="product-card">
                    <img src="${product.imageUrl}" alt="${product.name}">
                    <h3>${product.name}</h3>
                    <p>${product.description}</p>
                    <button onclick="requestDemo(${product.id})">Request Demo</button>
                </div>
            `).join("");
        }
    } catch (error) {
        console.error("Failed to load products", error);
    }
}
window.onload = fetchProducts;
```

## 7. Service List (index.html or services.html)
```javascript
async function fetchServices() {
    try {
        const response = await fetch(`${API_BASE_URL}/services`);
        const result = await response.json();
        if (result.status === "success") {
            const container = document.getElementById("service-container");
            container.innerHTML = result.data.map(service => `
                <div class="service-item">
                    <i class="fa ${service.icon}"></i>
                    <h3>${service.title}</h3>
                    <p>${service.description}</p>
                </div>
            `).join("");
        }
    } catch (error) {
        console.error("Failed to load services", error);
    }
}
window.onload = fetchServices;
```

## 8. Admin Dashboard (admin-dashboard.html)
```javascript
async function loadDashboardStats() {
    try {
        const response = await fetch(`${API_BASE_URL}/admin/dashboard`, {
            headers: getHeaders()
        });
        const result = await response.json();
        if (result.status === "success") {
            const stats = result.data;
            document.getElementById("user-count").innerText = stats.totalUsers;
            document.getElementById("product-count").innerText = stats.totalProducts;
            document.getElementById("message-count").innerText = stats.totalContactMessages;
            
            // Render latest messages
            const msgList = document.getElementById("latest-messages");
            msgList.innerHTML = stats.latestMessages.map(msg => `
                <li><strong>${msg.name}</strong> (${msg.email}): ${msg.message}</li>
            `).join("");
        }
    } catch (error) {
        console.error("Dashboard load failed", error);
    }
}
window.onload = loadDashboardStats;
```
