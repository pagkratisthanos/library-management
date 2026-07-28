# Library Management API

A RESTful API for managing a library system, built with Spring Boot 3.5.11 and Java 21.

---

## Tech Stack

- **Java** 21 (Amazon Corretto)
- **Spring Boot** 3.5.11
- **Spring Security** + JWT (jjwt 0.12.6)
- **Spring Data JPA** + Hibernate
- **PostgreSQL** (production) / H2 (tests)
- **Flyway** — database migrations
- **Lombok**
- **Springdoc OpenAPI** 2.8.9 (Swagger UI)
- **Docker** + Docker Compose
- **JaCoCo** 0.8.12 — code coverage (99%)
- **JUnit 5** + AssertJ — 451 tests

---

## Prerequisites

- Java 21
- Maven 3.9+
- PostgreSQL 13+ (for local run)
- Docker & Docker Compose (for containerized run)

---

## Getting Started

### Run Locally

1. **Clone the repository**
```bash
git clone https://github.com/pagkratisthanos/library-management.git
cd library-management
```

2. **Create the database**
```sql
CREATE DATABASE library_db;
```

3. **Create a `.env` file** in the project root:
```
APP_SECURITY_SECRET_KEY=your_secret_key_here
APP_SECURITY_JWT_EXPIRATION=43200000
```

4. **Configure IntelliJ** to load the `.env` file using the EnvFile plugin:
    - Install the **EnvFile** plugin
    - Go to **Run → Edit Configurations**
    - Enable **EnvFile** and add the `.env` file

5. **Run the application**
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`.

Flyway will automatically run all migrations and create the database schema on startup.

---

### Run with Docker

1. **Create a `.env` file** in the project root:
```
APP_SECURITY_SECRET_KEY=your_secret_key_here
APP_SECURITY_JWT_EXPIRATION=43200000
```

2. **Start the containers**
```bash
docker-compose up --build
```

This will start:
- **PostgreSQL 17** on port `5433`
- **Application** on port `8080`

---

## Database

The schema is managed by **Flyway** migrations (V1–V14):

| Migration | Description |
|-----------|-------------|
| V1–V7 | Create tables (addresses, authors, members, books, copies, rentals, authors_books) |
| V8 | Add UUID columns |
| V9 | Add audit columns (created_at, updated_at, deleted, deleted_at) |
| V10 | Fix copies condition column |
| V11 | Refactor to UUID primary keys |
| V12 | Create roles, capabilities, users tables + insert ADMIN/LIBRARIAN roles + 15 capabilities |
| V13 | Insert default admin user (incorrect hash — fixed in V14) |
| V14 | Fix admin password hash |

---

## Authentication

The API uses **JWT Bearer Token** authentication.

### Default Admin Credentials
```
username: admin
password: admin123!
```

### Login
```http
POST /api/auth/authenticate
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123!"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

Use the token in subsequent requests:
```
Authorization: Bearer <token>
```

Token expires after **12 hours**.

---

## Authorization

The API has two roles with different capabilities:

### ADMIN
Full access to all endpoints including user management.

| Capability | Description |
|------------|-------------|
| VIEW_AUTHOR, EDIT_AUTHOR, DELETE_AUTHOR | Full author management |
| VIEW_BOOK, EDIT_BOOK, DELETE_BOOK | Full book management |
| VIEW_MEMBER, EDIT_MEMBER, DELETE_MEMBER | Full member management |
| VIEW_COPY, EDIT_COPY, DELETE_COPY | Full copy management |
| VIEW_RENTAL, MANAGE_RENTAL | Rental management |
| MANAGE_USERS | User management |

### LIBRARIAN
Limited access — can view authors and books, manage members, copies and rentals.

| Capability | Description |
|------------|-------------|
| VIEW_AUTHOR | View authors only |
| VIEW_BOOK | View books only |
| VIEW_MEMBER, EDIT_MEMBER, DELETE_MEMBER | Full member management |
| VIEW_COPY, EDIT_COPY, DELETE_COPY | Full copy management |
| VIEW_RENTAL, MANAGE_RENTAL | Rental management |

---

## API Endpoints

### Authentication
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/authenticate` | Login and get JWT token | Public |

### Authors
| Method | Endpoint | Description | Required Capability |
|--------|----------|-------------|---------------------|
| GET | `/api/authors` | Get all authors (paginated + filtered) | VIEW_AUTHOR |
| GET | `/api/authors/{uuid}` | Get author by UUID | VIEW_AUTHOR |
| GET | `/api/authors/book/{bookUuid}` | Get authors by book | VIEW_AUTHOR |
| POST | `/api/authors` | Create author | EDIT_AUTHOR |
| PUT | `/api/authors/{uuid}` | Update author | EDIT_AUTHOR |
| DELETE | `/api/authors/{uuid}` | Delete author (soft) | DELETE_AUTHOR |

#### Author Filters
```
GET /api/authors?firstname=George&lastname=Orwell&birthPlace=India
```

### Books
| Method | Endpoint | Description | Required Capability |
|--------|----------|-------------|---------------------|
| GET | `/api/books` | Get all books (paginated + filtered) | VIEW_BOOK |
| GET | `/api/books/{uuid}` | Get book by UUID | VIEW_BOOK |
| POST | `/api/books` | Create book | EDIT_BOOK |
| PUT | `/api/books/{uuid}` | Update book | EDIT_BOOK |
| DELETE | `/api/books/{uuid}` | Delete book (soft) | DELETE_BOOK |

#### Book Filters
```
GET /api/books?title=Animal&isbn=978&language=English&description=political
```

### Members
| Method | Endpoint | Description | Required Capability |
|--------|----------|-------------|---------------------|
| GET | `/api/members` | Get all members (paginated + filtered) | VIEW_MEMBER |
| GET | `/api/members/{uuid}` | Get member by UUID | VIEW_MEMBER |
| POST | `/api/members` | Create member | EDIT_MEMBER |
| PUT | `/api/members/{uuid}` | Update member | EDIT_MEMBER |
| DELETE | `/api/members/{uuid}` | Delete member (soft) | DELETE_MEMBER |

#### Member Filters
```
GET /api/members?firstname=Thanos&lastname=Pagkratis&email=thanos&phoneNumber=691
```

### Copies
| Method | Endpoint | Description | Required Capability |
|--------|----------|-------------|---------------------|
| GET | `/api/copies` | Get all copies (paginated + filtered) | VIEW_COPY |
| GET | `/api/copies/{uuid}` | Get copy by UUID | VIEW_COPY |
| POST | `/api/copies` | Create copy | EDIT_COPY |
| PUT | `/api/copies/{uuid}` | Update copy | EDIT_COPY |
| DELETE | `/api/copies/{uuid}` | Delete copy (soft) | DELETE_COPY |

#### Copy Filters
```
GET /api/copies?available=true&condition=NEW
```

### Rentals
| Method | Endpoint | Description | Required Capability |
|--------|----------|-------------|---------------------|
| GET | `/api/rentals` | Get all rentals (paginated + filtered) | VIEW_RENTAL |
| GET | `/api/rentals/{uuid}` | Get rental by UUID | VIEW_RENTAL |
| GET | `/api/rentals/active` | Get active rentals (paginated) | VIEW_RENTAL |
| GET | `/api/rentals/member/{memberUuid}` | Get rentals by member | VIEW_RENTAL |
| POST | `/api/rentals` | Create rental | MANAGE_RENTAL |
| PUT | `/api/rentals/{uuid}/return` | Return rental | MANAGE_RENTAL |

#### Rental Filters
```
GET /api/rentals?memberUuid=&copyUuid=&active=true
```

### Users
| Method | Endpoint | Description | Required Capability |
|--------|----------|-------------|---------------------|
| GET | `/api/users` | Get all users (paginated) | MANAGE_USERS |
| GET | `/api/users/{uuid}` | Get user by UUID | MANAGE_USERS |
| POST | `/api/users` | Create user | MANAGE_USERS |
| DELETE | `/api/users/{uuid}` | Delete user (soft) | MANAGE_USERS |

### Roles
| Method | Endpoint | Description | Required Capability |
|--------|----------|-------------|---------------------|
| GET | `/api/roles` | Get all roles | MANAGE_USERS |

---

## Swagger UI

Access the API documentation at:

```
http://localhost:8080/swagger-ui/index.html
```

1. Use `POST /api/auth/authenticate` to get a token
2. Click **Authorize** and enter the token
3. Explore and test all endpoints

---

## Running Tests

```bash
mvn test
```

Tests use **H2 in-memory database** — no PostgreSQL required.

### Coverage Report

After running tests, open the JaCoCo report:
```
target/jacoco-report/index.html
```

Current coverage: **99%** (451 tests)

---

## Project Structure

```
src/main/java/com/library/management/
├── api/                    # REST Controllers
├── authentication/         # JWT Service, CustomUserDetailsService, AuthenticationService
├── core/                   # OpenApiConfig, ErrorHandler, MDCLoggingFilter
│   ├── exceptions/         # Custom exceptions
│   └── filters/            # AuthorFilters, BookFilters, MemberFilters, CopyFilters, RentalFilters
├── dto/                    # Data Transfer Objects
├── mapper/                 # Entity ↔ DTO mappers
├── model/                  # JPA Entities
├── repository/             # Spring Data JPA Repositories
├── security/               # Security configuration, JWT filter
├── service/                # Business logic
└── specification/          # JPA Specifications for dynamic filtering
```

---

## License

MIT License