# CAMS — Career Application Management System

> **Live demo:** [https://cams-tracker.online](https://cams-tracker.online)

A full-stack personal job application tracker built as a portfolio project demonstrating junior-to-intermediate Java and Spring Boot development practices. The full stack runs in Docker and is deployed live on Oracle Cloud with HTTPS.

---

## What It Does

CAMS lets a user register, log in, and manage their job applications in one place. Each application tracks the company, job title, status, source, applied date, a link to the job posting, and personal notes. A statistics dashboard visualises applications by status, source, and month.

---

## Tech Stack

### Backend
| Technology | Version | Role |
|---|---|---|
| Java | 25 | Core language |
| Spring Boot | 4.0.6 | Application framework |
| Spring Security + JWT (JJWT) | 0.12.6 | Stateless authentication |
| Spring Data JPA + Hibernate | Managed by Spring Boot 4.0.6 | Database access and ORM |
| PostgreSQL | 18 | Relational database |
| Flyway | 11.14.1 | Versioned schema migrations |
| SpringDoc / OpenAPI | 2.8.8 | Interactive API documentation |
| Maven | 3.9.15 | Build and dependency management |

### Frontend
| Technology | Version | Role |
|---|---|---|
| React | 19.2.6 | UI library |
| Vite | 8.0.12 | Build tool and dev server |
| Tailwind CSS | 4.3.0 | Utility-first styling |
| React Router | 7.17.0 | Client-side routing |
| Axios | 1.17.0 | HTTP client with JWT interceptor |
| Recharts | 3.8.1 | Chart components for the dashboard |

### Infrastructure
| Technology | Version       | Role |
|---|---------------|---|
| Docker | 29.5.3        | Container runtime |
| Docker Compose | v5.1.4         | Multi-container orchestration |
| Nginx | 1.31 (Alpine) | Static file server and HTTPS reverse proxy |
| Oracle Cloud (OCI) | Always Free   | Ubuntu 22.04 VPS hosting |
| Let's Encrypt / Certbot | Latest        | Free SSL certificate with auto-renewal |

---

## Features

- **Authentication** — register and login with BCrypt-hashed passwords; all sessions are stateless JWT-based
- **Job application CRUD** — create, view, edit, and delete applications; all data is scoped to the authenticated user
- **Filtering** — filter the applications list by status and/or source
- **Dashboard** — pie chart by status, bar chart by source, bar chart over time; clicking a chart segment navigates to the filtered list
- **Detail modal** — view all fields of an application inline without leaving the list page
- **API documentation** — full Swagger UI at `/swagger-ui/index.html` with JWT Bearer authentication wired in
- **Dockerised** — full stack runs with a single command locally or in production
- **Live deployment** — publicly accessible at [https://cams-tracker.online](https://cams-tracker.online)

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
├── frontend/                   # React frontend
│   ├── src/
│   │   ├── api/                # Axios instance and API functions
│   │   ├── context/            # AuthContext with localStorage persistence
│   │   ├── components/         # Navbar, ProtectedRoute, PublicRoute
│   │   └── pages/              # Login, Register, ApplicationsList, Dashboard, forms
│   ├── Dockerfile              # Multi-stage Node build → Nginx serve
│   ├── nginx.conf              # Production Nginx config (HTTPS + API reverse proxy)
│   └── nginx.local.conf        # Local development Nginx config (plain HTTP)
├── Dockerfile                  # Multi-stage Maven build → JRE runtime
├── docker-compose.yml          # Production stack (PostgreSQL + backend + frontend)
├── docker-compose.local.yml    # Local development stack
└── .env.example                # Required environment variables reference
```

---

## Running Locally with Docker (Recommended)

**Prerequisites:** Docker Desktop installed and running.

### 1. Clone the repository

```bash
git clone https://github.com/LyuboPetkov/job-application-tracker.git
cd job-application-tracker
```

### 2. Create a `.env` file in the project root

```
DB_PASSWORD=your_database_password
JWT_SECRET=your_base64_encoded_256_bit_secret
```

### 3. Start the full stack

```bash
docker compose -f docker-compose.local.yml up --build
```

The application will be available at `http://localhost`.

Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### To stop

```bash
docker compose -f docker-compose.local.yml down
```

---

## Running Locally without Docker

### Prerequisites

- Java 25
- Maven 3.9+
- PostgreSQL 18 running locally
- Node.js 22+ (LTS)

### 1. Database setup

```sql
CREATE DATABASE cams_db;
CREATE USER cams_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE cams_db TO cams_user;
ALTER DATABASE cams_db OWNER TO cams_user;
```

### 2. Environment variables

Set both before starting the backend, or add them to your IDE run configuration:

```bash
export JWT_SECRET=your-base64-encoded-256-bit-secret
export DB_PASSWORD=your-database-password
```

### 3. Backend

```bash
mvn spring-boot:run
```

Flyway will run the migrations automatically on startup. API available at `http://localhost:8080`.

Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### 4. Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend available at `http://localhost:5173`.

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

Full interactive documentation available via Swagger UI when the backend is running.

---

## Key Design Decisions

**Stateless JWT authentication.** There are no server-side sessions. Every request carries a signed JWT in the `Authorization: Bearer` header. The server validates the signature on each request using a secret key that never leaves the server. This scales horizontally and keeps the backend simple.

**User-scoped data with ownership enforcement.** Every operation on a job application verifies that the authenticated user owns that resource before proceeding. A mismatch returns `403 Forbidden`. This check lives in the service layer, not scattered across controllers.

**Request/Response DTO separation.** `JobApplicationRequest` carries validation annotations and omits server-generated fields. `JobApplicationResponse` carries server-generated fields (`id`, `createdAt`, `updatedAt`) and is never deserialized from client input. A dedicated mapper class is the only place that translates between DTOs and entities, keeping the database layer and API contract decoupled.

**Centralised exception handling.** Custom exceptions (`ResourceNotFoundException`, `UnauthorizedAccessException`, `EmailAlreadyExistsException`) are thrown from the service layer and caught by a single `@RestControllerAdvice` class that returns consistent JSON error responses. No controller contains error-handling logic.

**Client-side dashboard statistics.** The dashboard calls the existing `GET /api/applications` endpoint with no filters and computes all statistics in JavaScript. No additional backend endpoints were needed. A pure function (`computeStats`) handles the transformation outside the component, keeping data fetching, data shaping, and rendering in separate concerns.

**Nginx as HTTPS reverse proxy.** In production, the browser communicates exclusively with Nginx on port 443. API calls to `/api/` are proxied internally to the Spring Boot container on the Docker network. The backend is never exposed directly to the internet, and mixed-content blocking is avoided entirely.

**Multi-stage Docker builds.** Both Dockerfiles use multi-stage builds: a heavy build-stage image compiles the application, and a minimal runtime image runs it. Source code, build tools, and dependency caches are discarded from the final image, keeping image sizes small.

**Local vs production environment split.** Two docker-compose files separate local and production concerns. `docker-compose.local.yml` uses plain HTTP and `localhost:8080` as the API URL. `docker-compose.yml` uses HTTPS, the production domain, and the full Nginx reverse proxy configuration. The frontend Dockerfile accepts a build argument to select the correct Nginx config at build time.

---

## What This Project Demonstrates

- Implementing JWT-based stateless authentication from scratch in Spring Security
- Enforcing resource ownership at the service layer with proper `403 vs 404` distinction
- Applying the DTO pattern with clear separation between input validation and output contracts
- Centralised error handling with `@RestControllerAdvice` and consistent error response structure
- Schema-first database management with Flyway versioned migrations
- Interactive API documentation with SpringDoc/OpenAPI including a wired-in JWT auth scheme
- A React frontend with context-based auth state, JWT interceptors, and protected routing
- Client-side data transformation and Recharts visualisation without additional backend endpoints
- Docker containerisation with multi-stage builds and Docker Compose orchestration
- Production deployment to a Linux VPS with Nginx reverse proxying, HTTPS, and a custom domain

---

## Environment Variables

| Variable | Required | Description |
|---|---|---|
| `DB_PASSWORD` | Yes | PostgreSQL password for `cams_user` |
| `JWT_SECRET` | Yes | Base64-encoded 256-bit key for JWT signing |

See `.env.example` for a reference file. Never commit actual values to source control.

---

## Author

Lyubo Petkov — [github.com/LyuboPetkov](https://github.com/LyuboPetkov)