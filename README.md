# FamilyHub

**A secure Spring Boot backend for a family hub.**

FamilyHub is a backend API that centralizes family life: shared tasks, budgets, calendar events, real-time notifications, and health tracking — all behind secure JWT authentication.

## Features

### Authentication & Accounts
- Register, login, logout, and token refresh (JWT access token + refresh token)
- Email confirmation flow for new accounts
- HttpOnly cookie-based token transport
- Secure password storage

### Family Management
- Create a family group and invite members via time-limited join codes
- View all family members and their profiles

### Persona Profiles
- Create and update your personal profile (name, birthday, gender, avatar)

### Shared Task Lists
- Create task lists and assign participants from the family
- Add, update, complete, and delete tasks
- Daily scheduled cleanup: completed tasks are removed, recurring tasks reset automatically

### Family Budget & Finance Tracker
- Create family budgets with multi-currency support
- Record income/expense transactions
- Nested sub-budgets for granular tracking

### Shared Calendar
- Create, update, and delete family calendar events
- Participants receive notifications when events are created
- Automatic email reminders 10 minutes before events

### Real-time Notifications
- Server-Sent Events (SSE) stream for live push notifications
- Paginated notification history with read/unread tracking
- Async processing — notifications never block the main request

### Period Tracker
- Private menstrual cycle profiles per persona
- Log period start/end events
- Prediction algorithm that learns from historical records
- Month-view summaries; family members can optionally share visibility

### Observability
- Correlation ID injected on every request for end-to-end tracing
- Structured logging (WARN at root, INFO for app code)
- Global exception handler with standardized `ApiError` responses and error reference IDs
- OpenAPI / Swagger UI enabled

### Roadmap
- **Secure in-app chat**


## Tech Stack
- **Language**: Java 17
- **Framework**: Spring Boot 3.4
- **Security**: Spring Security (JWT resource server, HMAC SHA-256)
- **Databases**: PostgreSQL (auth, personas, families) + MongoDB (tasks, budgets, calendar, notifications, period data)
- **ORM**: Hibernate / Spring Data JPA + Spring Data MongoDB
- **Real-time**: Server-Sent Events (SSE)
- **Email**: Gmail SMTP (async)
- **API Docs**: Swagger / OpenAPI 3
- **Build**: Gradle


## Getting Started

### Prerequisites
- [JDK 17](https://adoptium.net/)
- [PostgreSQL](https://www.postgresql.org/)
- [MongoDB](https://www.mongodb.com/try/download/community)

### Environment Variables
Set the following before running:

| Variable | Example |
|----------|---------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/familyhub_db` |
| `SPRING_DATASOURCE_USERNAME` | `familyhub_admin` |
| `SPRING_DATASOURCE_PASSWORD` | `yourpassword` |
| `SPRING_DATA_MONGODB_URI` | `mongodb://localhost:27017/familyhub` |
| `JWT_SECRET` | `<base64-encoded-secret>` |
| `SPRING_MAIL_USERNAME` | `your@gmail.com` |
| `SPRING_MAIL_PASSWORD` | `<app-password>` |

For cloud deployments, see the [Moving PostgreSQL to Azure](#moving-postgresql-to-azure) section below.

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

### Auth
- `POST /auth/register`
- `POST /auth/confirm-email`
- `POST /auth/login`
- `POST /auth/refresh`
- `POST /auth/logout`

### Persona
- `GET /personas/me`
- `POST /personas/me`
- `PATCH /personas/me`
- `GET /personas/family-members`

### Family
- `POST /family/create`
- `POST /family/join`
- `GET /family`
- `POST /family/generate-code`

### Tasks
- `GET /task-lists`
- `POST /task-lists`
- `POST /task-lists/{id}/tasks`
- `PATCH /task-lists/{id}/tasks/{taskId}`
- `DELETE /task-lists/{id}/tasks/{taskId}`

### Budget
- `POST /budgets`
- `GET /budgets`
- `POST /budgets/{id}/transactions`

### Calendar
- `POST /calendar`
- `PATCH /calendar/{id}`
- `DELETE /calendar/{id}`

### Notifications
- `GET /notifications`
- `PATCH /notifications/{id}/read`
- `GET /notifications/stream` (SSE)

### Period Tracker
- `POST /period-profile`
- `GET /period-profile`
- `POST /period-profile/record`
- `GET /period-profile/month`

## Notes
- Tokens are sent via cookies by default; adjust cookie flags in `application.properties` for production.
- MongoDB auto-index creation is enabled (`spring.data.mongodb.auto-index-creation=true`); disable in production if managing indexes manually.
- Access token TTL: 1 hour. Refresh token TTL: 30 days.
- Family join codes expire after 15 minutes.
- Team/dev process is documented in [DEVELOPMENT_WORKFLOW.md](DEVELOPMENT_WORKFLOW.md).

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