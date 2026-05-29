# Product Management System

A full-stack product management and order processing system built with **Spring Boot 3.5** (backend) and **React** (frontend).

## Tech Stack

| Layer       | Technology                                     |
| ----------- | ---------------------------------------------- |
| Backend     | Java 21, Spring Boot 3.5, Spring Data JPA, Spring Security, Spring Validation |
| Database    | MySQL 8, Hibernate (auto-DDL)                  |
| Frontend    | React 18, Vite 8, React Router 7, Axios        |
| Build       | Maven, npm                                     |

---

## Setup

### Prerequisites

- **Java 21** or later
- **Maven 3.8+**
- **MySQL 8+**
- **Node.js 18+** and **npm**

### 1. Clone the Repository

```bash
git clone <repo-url>
cd productmanagment
```

### 2. Database Setup

Create a MySQL database (or let Hibernate auto-create it):

```sql
CREATE DATABASE IF NOT EXISTS productmanagment;
```

### 3. Backend Configuration

Edit `productmanagmentBackend/src/main/resources/application.properties`:

```properties
server.port=5000

spring.datasource.url=jdbc:mysql://localhost:3306/productmanagment?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root123

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

>

### 4. Run Backend

```bash
Start Backend in IntelliJ
```

The API starts at **http://localhost:5000**.

### 5. Run Frontend

```bash
cd productmanagmentFrontend
npm install
npm run dev
```

The frontend starts at **http://localhost:5173** (CORS is pre-configured).

---

## Seed Data

On first startup, the `DataSeeder` populates the database with:

### Users

| Username    | Password   | Role(s)      |
| ----------- | ---------- | ------------ |
| `user`      | `user123`  | USER         |
| `admin`     | `admin123` | ADMIN        |
| `superadmin`| `admin123` | SUPER_ADMIN  |

### Categories

| Name              |
| ----------------- |
| Electronics       |
| Clothing          |
| Home & Kitchen    |
| Sports            |

### Products (12 total)

Includes Bluetooth Speaker, Wireless Mouse, Cotton T-Shirt, Denim Jeans, Coffee Maker, Yoga Mat, Running Shoes, etc. Each product has 50 units in inventory.

---

## Database Schema



### Tables

#### `users`
| Column     | Type         | Constraints              |
| ---------- | ------------ | ------------------------ |
| id         | BIGINT       | PK, AUTO_INCREMENT       |
| username   | VARCHAR(255) | UNIQUE, NOT NULL         |
| email      | VARCHAR(255) | UNIQUE, NOT NULL         |
| password   | VARCHAR(255) | NOT NULL                 |
| created_at | DATETIME     |                          |

#### `roles`
| Column | Type         | Constraints        |
| ------ | ------------ | ------------------ |
| id     | BIGINT       | PK, AUTO_INCREMENT |
| name   | VARCHAR(255) | UNIQUE, NOT NULL   |

#### `user_roles` (Join Table)
| Column  | Type   | Constraints                              |
| ------- | ------ | ---------------------------------------- |
| user_id | BIGINT | PK (composite), FK → users(id)           |
| role_id | BIGINT | PK (composite), FK → roles(id)           |

#### `categories`
| Column      | Type         | Constraints        |
| ----------- | ------------ | ------------------ |
| id          | BIGINT       | PK, AUTO_INCREMENT |
| name        | VARCHAR(255) | UNIQUE, NOT NULL   |
| description | VARCHAR(500) |                    |

#### `products`
| Column      | Type         | Constraints              |
| ----------- | ------------ | ------------------------ |
| id          | BIGINT       | PK, AUTO_INCREMENT       |
| name        | VARCHAR(255) | NOT NULL                 |
| description | VARCHAR(1000)|                          |
| price       | DOUBLE       | NOT NULL                 |
| category_id | BIGINT       | FK → categories(id)      |
| image_url   | VARCHAR(255) |                          |
| enabled     | BOOLEAN      | NOT NULL, DEFAULT TRUE   |

#### `inventory`
| Column     | Type   | Constraints                           |
| ---------- | ------ | ------------------------------------- |
| id         | BIGINT | PK, AUTO_INCREMENT                    |
| product_id | BIGINT | FK → products(id), UNIQUE, NOT NULL   |
| quantity   | INT    | NOT NULL, DEFAULT 0                   |

#### `carts`
| Column     | Type         | Constraints            |
| ---------- | ------------ | ---------------------- |
| id         | BIGINT       | PK, AUTO_INCREMENT     |
| user_id    | BIGINT       | FK → users(id), UNIQUE |
| created_at | DATETIME     |                        |

#### `cart_items`
| Column     | Type   | Constraints              |
| ---------- | ------ | ------------------------ |
| id         | BIGINT | PK, AUTO_INCREMENT       |
| cart_id    | BIGINT | FK → carts(id)           |
| product_id | BIGINT | FK → products(id)        |
| quantity   | INT    | NOT NULL, DEFAULT 1      |

#### `orders`
| Column       | Type         | Constraints                         |
| ------------ | ------------ | ----------------------------------- |
| id           | BIGINT       | PK, AUTO_INCREMENT                  |
| user_id      | BIGINT       | FK → users(id)                      |
| address_id   | BIGINT       | FK → addresses(id), NULLABLE        |
| total_amount | DOUBLE       | NOT NULL                            |
| order_date   | DATETIME     | NOT NULL                            |
| status       | VARCHAR(255) | NOT NULL, ENUM (PROCESSING, SHIPPED, OUT_FOR_DELIVERY, DELIVERED, CANCELED) |

#### `order_items`
| Column           | Type   | Constraints              |
| ---------------- | ------ | ------------------------ |
| id               | BIGINT | PK, AUTO_INCREMENT       |
| order_id         | BIGINT | FK → orders(id)          |
| product_id       | BIGINT | FK → products(id)        |
| quantity         | INT    | NOT NULL                 |
| price_at_purchase| DOUBLE | NOT NULL                 |

#### `addresses`
| Column    | Type         | Constraints              |
| --------- | ------------ | ------------------------ |
| id        | BIGINT       | PK, AUTO_INCREMENT       |
| user_id   | BIGINT       | FK → users(id)           |
| street    | VARCHAR(255) | NOT NULL                 |
| city      | VARCHAR(255) | NOT NULL                 |
| state     | VARCHAR(255) | NOT NULL                 |
| zip_code  | VARCHAR(255) | NOT NULL                 |
| country   | VARCHAR(255) | NOT NULL                 |
| is_default| BOOLEAN      | DEFAULT FALSE            |

---

### Role Hierarchy

| Role          | Privileges                                                    |
| ------------- | ------------------------------------------------------------- |
| `USER`        | Browse products, manage own cart/addresses/orders             |
| `ADMIN`       | All `USER` privileges + create/update/disable products, update order status, manage inventory |
| `SUPER_ADMIN` | All `ADMIN` privileges + manage users/roles, manage categories |

---

## API Endpoints

### Auth

| Method | Path                     | Auth   | Description           |
| ------ | ------------------------ | ------ | --------------------- |
| POST   | `/api/auth/register`     | No     | Register a new user   |
| POST   | `/api/auth/login`        | No     | Login                 |
| GET    | `/api/auth/user/{id}`    | No     | Get user by ID        |

### Products

| Method | Path                        | Auth     | Description              |
| ------ | --------------------------- | -------- | ------------------------ |
| GET    | `/api/products`             | No       | List products (filterable) |
| GET    | `/api/products/{id}`        | No       | Get product by ID        |
| POST   | `/api/products`             | ADMIN+   | Create product           |
| PUT    | `/api/products/{id}`        | ADMIN+   | Update product           |
| PATCH  | `/api/products/{id}/enable` | ADMIN+   | Enable product           |
| PATCH  | `/api/products/{id}/disable`| ADMIN+   | Disable product          |
| DELETE | `/api/products/{id}`        | ADMIN+   | Delete product           |

**Query Parameters for `GET /api/products`:**

| Param    | Type   | Description                          |
| -------- | ------ | ------------------------------------ |
| category | String | Filter by category name              |
| search   | String | Search by product name (case-insensitive) |
| X-User-Id| Header | When provided, includes inventory qty |

### Categories

| Method | Path                       | Auth         | Description          |
| ------ | -------------------------- | ------------ | -------------------- |
| GET    | `/api/categories`          | No           | List all categories  |
| GET    | `/api/categories/{id}`     | No           | Get category by ID   |
| POST   | `/api/categories`          | SUPER_ADMIN  | Create category      |
| PUT    | `/api/categories/{id}`     | SUPER_ADMIN  | Update category      |
| DELETE | `/api/categories/{id}`     | SUPER_ADMIN  | Delete category      |

### Cart

| Method | Path                    | Auth | Description                |
| ------ | ----------------------- | ---- | -------------------------- |
| GET    | `/api/cart`             | Yes  | Get current user's cart    |
| POST   | `/api/cart`             | Yes  | Add item to cart           |
| PUT    | `/api/cart/items/{id}`  | Yes  | Update item quantity       |
| DELETE | `/api/cart/items/{id}`  | Yes  | Remove item from cart      |
| DELETE | `/api/cart`             | Yes  | Clear entire cart          |

### Orders

| Method | Path                          | Auth     | Description              |
| ------ | ----------------------------- | -------- | ------------------------ |
| POST   | `/api/orders/checkout`        | Yes      | Checkout (cart → order)  |
| GET    | `/api/orders/my`              | Yes      | Get current user's orders|
| GET    | `/api/orders`                 | SUPER_ADMIN | Get all orders        |
| PUT    | `/api/orders/{id}/status`     | ADMIN+   | Update order status      |
| DELETE | `/api/orders/{id}`            | SUPER_ADMIN | Delete order          |

**Order Status Values:** `PROCESSING`, `SHIPPED`, `OUT_FOR_DELIVERY`, `DELIVERED`, `CANCELED`

### Addresses

| Method | Path                     | Auth | Description               |
| ------ | ------------------------ | ---- | ------------------------- |
| GET    | `/api/addresses`         | Yes  | Get user's addresses      |
| POST   | `/api/addresses`         | Yes  | Add new address           |
| PUT    | `/api/addresses/{id}`    | Yes  | Update address            |
| DELETE | `/api/addresses/{id}`    | Yes  | Delete address            |

### Admin

| Method | Path                              | Auth         | Description                  |
| ------ | --------------------------------- | ------------ | ---------------------------- |
| GET    | `/api/admin/users`                | SUPER_ADMIN  | List all users               |
| GET    | `/api/admin/users/{id}`           | SUPER_ADMIN  | Get user by ID               |
| PUT    | `/api/admin/users/{id}/roles`     | SUPER_ADMIN  | Update user's roles          |
| GET    | `/api/admin/inventory`            | ADMIN+       | List all inventory           |
| PUT    | `/api/admin/inventory/{productId}`| ADMIN+       | Update product stock quantity|

---


