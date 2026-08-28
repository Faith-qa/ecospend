# EcoSpend API

A small backend service that tracks financial transactions against spending categories and computes a deterministic, **rule-based** environmental impact score per user.

> **Important:** The impact score is a simple heuristic (`spend × category weight`), not a scientific carbon-emissions calculation. It exists to let users compare their own spending categories relative to each other, not to estimate real-world emissions.

## Tech stack

- Kotlin + Spring Boot 4 (Spring Web, Spring Data JPA, Bean Validation)
- PostgreSQL (runtime/production datastore) + Flyway (schema migrations)
- H2 (in-memory database, tests only)
- Gradle (Kotlin DSL)
- JUnit 5 + MockMvc (tests)
- Docker / Docker Compose (local PostgreSQL)

## Architecture

```
HTTP request → Controller → Service → Repository → PostgreSQL
```

- **controller/** — HTTP concerns only: routing, request/response mapping, status codes.
- **service/** — business logic (entity↔DTO mapping, impact-score aggregation).
- **repository/** — Spring Data JPA interfaces; no hand-written SQL for CRUD.
- **entity/** — JPA-mapped classes, one-to-one with the database schema.
- **dto/** — request/response shapes exposed over the API; decoupled from entities.
- **exception/** — custom exceptions + a global `@RestControllerAdvice` handler.

The database schema is owned **entirely by Flyway** (`src/main/resources/db/migration/`). Hibernate is configured with `ddl-auto: validate` — it checks entities against the real schema at startup and fails loudly on any mismatch, but never creates or alters tables itself.

## Prerequisites

- **JDK 26** — the Gradle toolchain in `build.gradle.kts` is pinned to JDK 26 (the version installed in this project's development environment). If you're on a different JDK, either install JDK 26 or adjust `JavaLanguageVersion.of(26)` in `build.gradle.kts` to match what you have (JDK 17+ is the minimum Spring Boot 4 supports).
- **Docker** (with Docker Compose) — for running PostgreSQL locally.
- No local PostgreSQL, H2, or Gradle install needed — Gradle is invoked via the bundled `./gradlew` wrapper, and both databases are handled automatically (Postgres via Docker, H2 in-memory for tests).

## Getting started

### 1. Start PostgreSQL

```bash
docker compose up -d
```

This starts a `postgres:16-alpine` container named `ecospend-postgres`, seeded with a database/user/password of `ecospend`/`ecospend`/`ecospend` on port `5432`, with data persisted in a named Docker volume (`ecospend_postgres_data`) so it survives restarts.

Check it's healthy:

```bash
docker compose ps
```

### 2. (Optional) Configure environment variables

The app reads its datasource config from environment variables, each with a sensible local default baked in — so **no setup is required to run locally**. `.env.example` documents what's available:

| Variable | Default | Purpose |
|---|---|---|
| `DB_HOST` | `localhost` | PostgreSQL host |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_NAME` | `ecospend` | Database name |
| `DB_USERNAME` | `ecospend` | Database user |
| `DB_PASSWORD` | `ecospend` | Database password |

To override any of these, copy `.env.example` to `.env` and edit it — Docker Compose loads `.env` automatically, and you can `export` the same variables in your shell before running the app so both stay in sync. `.env` is gitignored; never commit real credentials.

### 3. Run the application

```bash
./gradlew bootRun
```

On startup, Flyway automatically applies all pending migrations (creating the schema and seeding 5 starter categories — see below), then Hibernate validates the entity mappings against that schema. The API is available at `http://localhost:8080`.

To stop: `Ctrl+C`, then `./gradlew --stop` to shut down the Gradle daemon if needed.

### 4. Run the tests

```bash
./gradlew test
```

Tests run against an in-memory **H2** database (configured in `src/test/resources/application.yml`, in PostgreSQL-compatibility mode) — no Docker or running Postgres required. Flyway runs the same real migrations against H2 first, so tests exercise the actual schema, not a mocked one.

## Seeded categories

`V2__seed_categories.sql` seeds these on first startup (IDs may shift if you insert/delete categories):

| id | name | impactTag | impactWeight |
|---|---|---|---|
| 1 | Public Transport | LOW | 1.00 |
| 2 | Groceries | MEDIUM | 2.00 |
| 3 | Clothing | MEDIUM | 3.00 |
| 4 | Flights | HIGH | 5.00 |
| 5 | Fuel | HIGH | 5.00 |

Check current IDs at any time via `GET /api/categories`.

## API reference

### `POST /api/categories`

Creates a category. Returns `201 Created` with a `Location` header.

```bash
curl -i -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{"name": "Home Energy", "impactTag": "HIGH", "impactWeight": 4}'
```

```json
{"id":6,"name":"Home Energy","impactTag":"HIGH","impactWeight":4.00}
```

Validation: `name` required, non-blank, max 100 chars; `impactTag` one of `LOW`/`MEDIUM`/`HIGH`; `impactWeight` required, `>= 0`. Invalid input returns `400 Bad Request`.

### `GET /api/categories`

Returns every category.

```bash
curl http://localhost:8080/api/categories
```

### `POST /api/transactions`

Creates a transaction against an existing category. Returns `201 Created`.

```bash
curl -i -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 101,
    "merchant": "Kenya Airways",
    "amount": 12000.00,
    "categoryId": 4,
    "occurredAt": "2026-08-20T09:15:00Z"
  }'
```

```json
{"id":1,"userId":101,"merchant":"Kenya Airways","amount":12000.00,"occurredAt":"2026-08-20T09:15:00Z","createdAt":"2026-08-26T07:49:44.919854Z","category":{"id":4,"name":"Flights","impactTag":"HIGH","impactWeight":5.00}}
```

Validation: `merchant` non-blank (max 200 chars), `amount` required and `> 0`, `categoryId` required, `occurredAt` required (ISO-8601 instant). A nonexistent `categoryId` returns `404 Not Found`:

```json
{"status":404,"message":"Category with id 999 not found","timestamp":"2026-08-26T07:54:59.446002Z"}
```

### `GET /api/transactions?userId=`

Returns every transaction for a given user (`userId` is a required query parameter).

```bash
curl "http://localhost:8080/api/transactions?userId=101"
```

### `GET /api/users/{userId}/impact-summary`

Returns aggregated spend and impact score for a user, grouped by category (sorted by `impactScore` descending). A user with no transactions returns zeroed totals and an empty `categoryBreakdown` — not an error, since there's no separate concept of a "registered user" in this system.

```bash
curl http://localhost:8080/api/users/101/impact-summary
```

```json
{
  "userId": 101,
  "totalSpend": 12000.00,
  "totalImpactScore": 60000.00,
  "categoryBreakdown": [
    {
      "categoryId": 4,
      "categoryName": "Flights",
      "impactTag": "HIGH",
      "totalSpend": 12000.00,
      "impactWeight": 5.00,
      "impactScore": 60000.00,
      "transactionCount": 1
    }
  ]
}
```

Each `categoryBreakdown` entry is self-verifying: `totalSpend × impactWeight == impactScore`.

## Useful Docker commands

```bash
docker compose up -d       # start Postgres in the background
docker compose down        # stop it (data persists in the volume)
docker compose down -v     # stop it AND wipe the data volume (full reset)
docker compose logs -f     # tail Postgres logs
docker exec -it ecospend-postgres psql -U ecospend -d ecospend   # open a psql shell
```

## Project layout

```
src/main/kotlin/com/ecospend/api/
├── EcospendApiApplication.kt
├── controller/     # CategoryController, TransactionController, ImpactSummaryController
├── service/        # CategoryService, TransactionService, ImpactSummaryService
├── repository/     # CategoryRepository, TransactionRepository
├── entity/         # Category, Transaction, ImpactTag
├── dto/            # request/response DTOs
└── exception/      # ResourceNotFoundException, GlobalExceptionHandler

src/main/resources/
├── application.yml            # datasource + JPA config (env-var driven)
└── db/migration/               # Flyway migrations (V1: schema, V2: seed data)

src/test/
├── kotlin/...                  # JUnit 5 + MockMvc tests
└── resources/application.yml   # H2 datasource config (test-only override)
```
