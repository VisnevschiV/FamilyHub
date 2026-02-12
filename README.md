# FamilyHub

**A secure, learning-focused Spring Boot backend for a family hub.**

FamilyHub is a backend API project built to practice Java, Spring Boot, and common backend patterns like authentication, DTOs, and persistence.

## Features

### Current
- **Authentication**: Register, login, refresh tokens (JWT access token + refresh token).
- **Cookie-based auth**: Access token stored in HttpOnly cookies.
- **Persona profile**: Create, read, and update the current user profile.
- **OpenAPI docs**: Swagger UI enabled.

### Roadmap
- **Shared to-do lists**
- **Secure chat**
- **Event tracking and shared calendar**
- **Finance manager**

## Tech Stack
- **Language**: Java 17
- **Framework**: Spring Boot 3.4
- **Security**: Spring Security (JWT resource server)
- **ORM**: Hibernate / Spring Data JPA
- **Database**: PostgreSQL
- **Build**: Gradle

## Getting Started

### Prerequisites
- [JDK 17](https://adoptium.net/)
- [PostgreSQL](https://www.postgresql.org/)

### Configure
Update [src/main/resources/application.properties](src/main/resources/application.properties) with your database credentials and a JWT secret.

### Run
```bash
./gradlew bootRun
```
On Windows:
```bash
gradlew.bat bootRun
```

### API Docs
Swagger UI: `http://localhost:8080/swagger-ui/index.html`

## Key Endpoints
- `POST /auth/register`
- `POST /auth/login`
- `POST /auth/refresh`
- `GET /personas/me`
- `POST /personas/me`
- `PATCH /personas/me`

## Notes
- This project is intentionally small and focused on learning.
- Tokens are sent via cookies by default; adjust cookie flags in properties for production.