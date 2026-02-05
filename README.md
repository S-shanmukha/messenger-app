# messenger-app

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

### Pull Docker Image
Download the backend Docker image using:

```
docker pull postgres:15 && \
docker run -d --name messenger-postgres \
-e POSTGRES_USER=messenger_user \
-e POSTGRES_PASSWORD=messenger_pass \
-e POSTGRES_DB=messenger_db \
-p 5432:5432 \
-v messenger_pg_data:/var/lib/postgresql/data \
postgres:15
```
