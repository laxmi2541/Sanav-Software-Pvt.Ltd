# Frontend Integration Guide (JavaScript Fetch)

Paste these snippets into your `<script>` tags on the respective pages.

## 1. Global Setup (Utility)
Ensure you have a way to handle the API URL:
```javascript
const API_BASE_URL = "http://localhost:8081/api";

function getHeaders() {
    const token = localStorage.getItem("token");
    return {
        "Content-Type": "application/json",
        "Authorization": token ? `Bearer ${token}` : ""
    };
}
```

## 2. Login Page
```javascript
async function login(email, password) {
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
    });
    const data = await response.json();
    if (data.status === "success") {
        localStorage.setItem("token", data.data.token);
        localStorage.setItem("user", JSON.stringify(data.data.user));
        window.location.href = "index.html";
    } else {
        alert("Login failed: " + data.message);
    }
}
```

## 3. Home Page (Load Services)
```javascript
fetch(`${API_BASE_URL}/services`)
    .then(res => res.json())
    .then(data => {
        const services = data.data;
        const container = document.getElementById("services-container");
        container.innerHTML = services.map(s => `
            <div class="service-card">
                <i class="fa ${s.icon}"></i>
                <h3>${s.title}</h3>
                <p>${s.description}</p>
            </div>
        `).join("");
    });
```

## 4. Product Page
```javascript
fetch(`${API_BASE_URL}/products`)
    .then(res => res.json())
    .then(data => {
        const products = data.data;
        const container = document.getElementById("products-grid");
        container.innerHTML = products.map(p => `
            <div class="product-item">
                <img src="${p.imageUrl}" alt="${p.name}">
                <h3>${p.name}</h3>
                <p>${p.description}</p>
            </div>
        `).join("");
    });
```

## 5. Industrial Training Page
```javascript
fetch(`${API_BASE_URL}/courses`)
    .then(res => res.json())
    .then(data => {
        const courses = data.data;
        const container = document.getElementById("courses-list");
        container.innerHTML = courses.map(c => `
            <div class="course-card">
                <h3>${c.title}</h3>
                <p>${c.description}</p>
                <span>Duration: ${c.duration} | Fees: ₹${c.fees}</span>
            </div>
        `).join("");
    });
```

## 6. Contact Us Page
```javascript
async function sendContact(name, email, message) {
    const response = await fetch(`${API_BASE_URL}/contact`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name, email, message })
    });
    const data = await response.json();
    if (data.status === "success") {
        alert("Thank you! Your message has been sent.");
    }
}
```

## 7. Certificate Verification
```javascript
async function verifyCert(code) {
    const response = await fetch(`${API_BASE_URL}/certificate/${code}`);
    const data = await response.json();
    if (data.status === "success") {
        const cert = data.data;
        document.getElementById("cert-result").innerHTML = `
            <div class="success">
                Certificate Valid! <br>
                Student: ${cert.studentName} <br>
                Course: ${cert.courseName} <br>
                Issued On: ${new Date(cert.issueDate).toLocaleDateString()}
            </div>
        `;
    } else {
        alert("Certificate NOT Found!");
    }
}
```

## 8. Admin Dashboard
```javascript
async function loadDashboard() {
    const response = await fetch(`${API_BASE_URL}/admin/dashboard`, {
        headers: getHeaders()
    });
    const data = await response.json();
    if (data.status === "success") {
        const stats = data.data;
        document.getElementById("total-users").innerText = stats.totalUsers;
        document.getElementById("total-products").innerText = stats.totalProducts;
        document.getElementById("total-messages").innerText = stats.totalContactMessages;
        
        const msgList = stats.latestMessages.map(m => `
            <li>${m.name}: ${m.message.substring(0, 30)}...</li>
        `).join("");
        document.getElementById("latest-messages-list").innerHTML = msgList;
    }
}
```
