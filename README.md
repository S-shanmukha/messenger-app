# messenger-app
A real-time messaging application supporting **one-to-one** and **group conversations** with **offline message delivery**, **message ordering**.

This project was built as part of an interview assignment.

---

## 🚀 Features

### ✅ Core Features
- User authentication using **JWT**
- Create **1-1 chats**
- Create and manage **group chats**
- Messages are persisted in database
- Offline message storage and delivery on reconnect
- Message ordering maintained per conversation

### ✅ Additional Features
- Fetch user chats
- Add / remove users from group chat

---
## 📦 Installation & Setup

### Prerequisites
Install the following before running the project:

- Java 17+
- Maven
- Node.js 18+
- npm
- MySQL / PostgreSQL

---

## 🛠 Backend Installation (Spring Boot)

### Step 1: Clone Repository
```bash
git clone https://github.com/S-shanmukha/messenger-app.git
cd messenger-app
```
## 🐳 Docker Setup (Optional)


## 🐘 PostgreSQL Setup using Docker

Run PostgreSQL locally using Docker:

```bash
docker pull postgres:15 && \
docker run -d --name messenger-postgres \
-e POSTGRES_USER=messenger_user \
-e POSTGRES_PASSWORD=messenger_pass \
-e POSTGRES_DB=messenger_db \
-p 5432:5432 \
-v messenger_pg_data:/var/lib/postgresql/data \
postgres:15
```

Verify Container is Running
```
docker ps
```
PostgreSQL will be available at:

```
localhost:5432
```

## ⚙️ Backend Configuration (`application.properties`)

Update the PostgreSQL database configuration in:

Example configuration:

```
properties
spring.application.name=backend

# Server
server.port=8080

# PostgreSQL Config
spring.datasource.url=jdbc:postgresql://localhost:5432/db
spring.datasource.username=admin_user
spring.datasource.password=password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA / Hibernate
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```
## 🗃️ Data Model & Relationships

The backend follows a relational database design using PostgreSQL with JPA/Hibernate.

---

### 👤 User Entity
Represents an application user.

**Fields**
- `id (UUID)`  
- `name (String)`
- `email (String)`
- `password (String)`

**Relationships**
- A user can participate in **multiple chats**
- A user can send **multiple messages**

---

### 💬 Chat Entity
Represents a conversation (either direct or group).

**Fields**
- `id (UUID)`
- `chatName (String)`
- `isGroup (Boolean)`
- `createdAt (Timestamp)`

**Relationships**
- A chat contains **multiple users** (participants)
- A chat contains **multiple messages**
- A chat can have an **admin** (only for group chats)

---

### ✉️ Message Entity
Represents a message sent in a chat.

**Fields**
- `id (UUID)`
- `content (String)`
- `createdAt (Timestamp)`
- `status (ENUM: SENT / DELIVERED / READ)`

**Relationships**
- A message belongs to **one chat**
- A message is sent by **one user**

---

## 🔗 Relationship Summary (ER View)

- **User ↔ Chat** : Many-to-Many  
  (One user can be in many chats, one chat can have many users)

- **Chat → Message** : One-to-Many  
  (One chat contains many messages)

- **User → Message** : One-to-Many  
  (One user can send many messages)

---

## 🧠 Database Relationship Diagram (Simplified)
```
User (id) <---- Many-to-Many ----> Chat (id)
|
| One-to-Many
v
Message (id) ---- Many-to-One ----> Chat (id)
```
## 🔐 Authentication & Security (JWT)

The backend uses **JWT (JSON Web Token)** based authentication implemented with **Spring Security**.

---

### ✅ Authentication Flow

1. User registers using:
   ```
   http
   POST /auth/signup
   ```
2.User logs in using:
  ```
  POST /auth/login
  ```
3.On successful login, the backend returns a JWT token.

4.For all protected APIs, the frontend sends the token in the request header:
  ```
A  uthorization: Bearer <JWT_TOKEN>
  ```
5.Backend validates the JWT using a custom security filter (JwtValidator) and loads the authenticated user into Spring Security context.

### 📌 Token Usage

Example request:
```
GET /api/chats/user
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```
### 🔒 Protected Endpoints

All chat and messaging APIs are protected and require authentication:
```
/api/chats/**
/api/messages/**
```
Only authentication APIs are public:
```
/auth/signup
/auth/login
```

### ⚙️ Security Implementation

- JWT validation is handled using Spring Security filter chain.
- Unauthorized requests return 401 Unauthorized. 
- Only participants of a chat can access its messages.
- Group chat modifications (add/remove users) require admin authorization.

### 🔑 Password Security

- Passwords are stored securely (hashed) in the database.
- Raw passwords are never stored.

