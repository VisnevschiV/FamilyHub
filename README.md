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

For production or cloud deployments, you can set PostgreSQL values with environment variables instead of editing properties directly:
- `SPRING_DATASOURCE_URL` (example: `jdbc:postgresql://<server>.postgres.database.azure.com:5432/familyhub_db?sslmode=require`)
- `SPRING_DATASOURCE_USERNAME` (example: `familyhub_admin`)
- `SPRING_DATASOURCE_PASSWORD`

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

## Moving PostgreSQL to Azure

If you are fine starting fresh (no local data migration), use this clean setup path:

1. Create an **Azure Database for PostgreSQL Flexible Server** in the same region as your app.
2. Create database `familyhub_db` and an app user.
3. Allow network access:
	- either add your client/app IP in server firewall rules,
	- or use private networking if your app is deployed in Azure.
4. Set app environment variables:
	- `SPRING_DATASOURCE_URL=jdbc:postgresql://<server>.postgres.database.azure.com:5432/familyhub_db?sslmode=require`
	- `SPRING_DATASOURCE_USERNAME=<user>`
	- `SPRING_DATASOURCE_PASSWORD=<password>`
5. Start/restart the app. With `spring.jpa.hibernate.ddl-auto=update`, Hibernate creates missing tables automatically.
6. Verify `http://localhost:8080/swagger-ui/index.html` and run register/login once to validate writes.

If you do want existing local data later, follow the import/export path below.

1. Create an **Azure Database for PostgreSQL Flexible Server** in the same region as your app.
2. Create database `familyhub_db` and an admin/user with least required privileges.
3. Allow network access:
	- either add your client/app IP in server firewall rules,
	- or use private networking if your app is deployed in Azure.
4. Export local data:
	- `pg_dump -h localhost -U postgres -d familyhub_db -Fc -f familyhub_db.dump`
5. Import to Azure:
	- `pg_restore -h <server>.postgres.database.azure.com -U <user> -d familyhub_db --no-owner --no-privileges familyhub_db.dump`
6. Set app environment variables:
	- `SPRING_DATASOURCE_URL=jdbc:postgresql://<server>.postgres.database.azure.com:5432/familyhub_db?sslmode=require`
	- `SPRING_DATASOURCE_USERNAME=<user>`
	- `SPRING_DATASOURCE_PASSWORD=<password>`
7. Restart the application and verify the health/auth endpoints.

## Using Supabase PostgreSQL

If you switched to Supabase, you can keep the same Spring setup and just provide Supabase DB credentials via env vars.

1. In Supabase, open **Project Settings → Database**.
2. Copy the connection details for direct PostgreSQL access.
3. Set environment variables before starting the app:
	- `SPRING_DATASOURCE_URL=jdbc:postgresql://db.<project-ref>.supabase.co:5432/postgres?sslmode=require`
	- `SPRING_DATASOURCE_USERNAME=<supabase_db_user>`
	- `SPRING_DATASOURCE_PASSWORD=<supabase_db_password>`
4. Start the app with `./gradlew bootRun` (or `gradlew.bat bootRun` on Windows).
5. On first run, Hibernate creates missing tables automatically (`spring.jpa.hibernate.ddl-auto=update`).

Notes:
- If direct host access is blocked by your network, use Supabase pooler host/port from the same Database page and keep `sslmode=require`.
- For production, prefer a dedicated DB role for this app instead of the default admin role.