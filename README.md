# CAMS — Career Application Management System

A full-stack personal job application tracker built as a portfolio project demonstrating junior-to-intermediate Java and Spring Boot development practices.

---

## What It Does

CAMS lets a user register, log in, and manage their job applications in one place. Each application tracks the company, job title, status, source, applied date, a link to the job posting, and personal notes. A statistics dashboard visualises applications by status, source, and month.

---

## Tech Stack

### Backend
| Technology | Version                      | Role |
|---|------------------------------|---|
| Java | 25 (LTS)                     | Core language |
| Spring Boot | 4.0.6                        | Application framework |
| Spring Security + JWT (JJWT) | 0.12.6                       | Stateless authentication |
| Spring Data JPA + Hibernate | Managed by Spring Boot 4.0.6 | Database access and ORM |
| PostgreSQL | 18                           | Relational database |
| Flyway | 11.14.1                      | Versioned schema migrations |
| SpringDoc / OpenAPI | 2.8.8                        | Interactive API documentation |
| Maven | 3.9.15                       | Build and dependency management |

### Frontend
| Technology | Version | Role |
|---|---|---|
| React | 19.2.6 | UI library |
| Vite | 8.0.12 | Build tool and dev server |
| Tailwind CSS | 4.3.0 | Utility-first styling |
| React Router | 7.17.0  | Client-side routing |
| Axios | 1.17.0  | HTTP client with JWT interceptor |
| Recharts | 3.8.1 | Chart components for the dashboard |

---

## Features

- **Authentication** — register and login with BCrypt-hashed passwords; all sessions are stateless JWT-based
- **Job application CRUD** — create, view, edit, and delete applications; all data is scoped to the authenticated user
- **Filtering** — filter the applications list by status and/or source
- **Dashboard** — pie chart by status, bar chart by source, bar chart over time; clicking a chart segment navigates to the filtered list
- **Detail modal** — view all fields of an application inline without leaving the list page
- **API documentation** — full Swagger UI at `/swagger-ui/index.html` with JWT Bearer authentication wired in

---

## Project Structure

```
job-application-tracker/
├── src/                        # Spring Boot backend
│   └── main/java/.../cams/
│       ├── config/             # SecurityConfig, CorsConfig, OpenApiConfig
│       ├── controller/         # AuthController, JobApplicationController
│       ├── service/            # AuthService, JobApplicationService
│       ├── repository/         # UserRepository, JobApplicationRepository
│       ├── entity/             # User, JobApplication, enums
│       ├── dto/                # Request/Response DTOs
│       ├── mapper/             # JobApplicationMapper
│       ├── security/           # JwtUtil, JwtAuthFilter, CustomUserDetailsService
│       └── exception/          # Custom exceptions, GlobalExceptionHandler
├── src/main/resources/
│   ├── application.yml         # App configuration (secrets via environment variables)
│   └── db/migration/           # Flyway SQL migration scripts
└── frontend/                   # React frontend
    └── src/
        ├── api/                # Axios instance and API functions
        ├── context/            # AuthContext with localStorage persistence
        ├── components/         # Navbar, ProtectedRoute, PublicRoute
        └── pages/              # Login, Register, ApplicationsList, Dashboard, forms
```

---

## Running Locally

### Prerequisites

- Java 25
- Maven 3.9+
- PostgreSQL 15+ (running locally)
- Node.js 24+ (LTS)

### 1. Database setup

Create a PostgreSQL database and user:

```sql
CREATE DATABASE cams_db;
CREATE USER cams_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE cams_db TO cams_user;
ALTER DATABASE cams_db OWNER TO cams_user;
```

### 2. Environment variables

Two values are read from environment variables and are never stored in source control.
Set both before starting the backend:

```bash
export JWT_SECRET=your-base64-encoded-256-bit-secret
export DB_PASSWORD=your-database-password
```

Or add them to your IDE's run configuration under **Environment variables**.

### 3. Backend

Update `src/main/resources/application.yml` with your database credentials, then run:

```bash
mvn spring-boot:run
```

Flyway will run the migrations automatically on startup. The API will be available at `http://localhost:8080`.

Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### 4. Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend will be available at `http://localhost:5173`.

---

## API Overview

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Register a new user |
| POST | `/api/auth/login` | Public | Login and receive a JWT |
| GET | `/api/applications` | Required | Get all applications (optional `?status=` and `?source=` filters) |
| POST | `/api/applications` | Required | Create a new application |
| GET | `/api/applications/{id}` | Required | Get a single application |
| PUT | `/api/applications/{id}` | Required | Update an application |
| DELETE | `/api/applications/{id}` | Required | Delete an application |

Full interactive documentation is available via Swagger UI when the backend is running.

---

## Key Design Decisions

**Stateless JWT authentication.** There are no server-side sessions. Every request carries a signed JWT in the `Authorization: Bearer` header. The server validates the signature on each request using a secret key that never leaves the server. This scales horizontally and keeps the backend simple.

**User-scoped data with ownership enforcement.** Every operation on a job application verifies that the authenticated user owns that resource before proceeding. A mismatch returns `403 Forbidden`. This check lives in the service layer, not scattered across controllers.

**Request/Response DTO separation.** `JobApplicationRequest` carries validation annotations and omits server-generated fields. `JobApplicationResponse` carries server-generated fields (`id`, `createdAt`, `updatedAt`) and is never deserialized from client input. A dedicated mapper class is the only place that translates between DTOs and entities, keeping the database layer and API contract decoupled.

**Centralised exception handling.** Custom exceptions (`ResourceNotFoundException`, `UnauthorizedAccessException`, `EmailAlreadyExistsException`) are thrown from the service layer and caught by a single `@RestControllerAdvice` class that returns consistent JSON error responses. No controller contains error-handling logic.

**Client-side dashboard statistics.** The dashboard calls the existing `GET /api/applications` endpoint with no filters and computes all statistics in JavaScript. No additional backend endpoints were needed. A pure function (`computeStats`) handles the transformation outside the component, keeping data fetching, data shaping, and rendering in separate concerns.

---

## What This Project Demonstrates

- Implementing JWT-based stateless authentication from scratch in Spring Security 6
- Enforcing resource ownership at the service layer with proper `403 vs 404` distinction
- Applying the DTO pattern with clear separation between input validation and output contracts
- Centralised error handling with `@RestControllerAdvice` and consistent error response structure
- Schema-first database management with Flyway versioned migrations
- Interactive API documentation with SpringDoc/OpenAPI including a wired-in JWT auth scheme
- A React frontend with context-based auth state, JWT interceptors, and protected routing
- Client-side data transformation and Recharts visualisation without additional backend endpoints

---

## Author

Lyubo Petkov — [github.com/LyuboPetkov](https://github.com/LyuboPetkov)
