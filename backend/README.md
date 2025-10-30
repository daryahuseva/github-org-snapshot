# GitHub Org Snapshot - Backend

REST API that exposes GitHub organization repositories for the frontend.

---

## Repository structure (context)
```
github-org-snapshot/
├─ backend/
│  ├─ src/
│  ├─ pom.xml
│  └─ README.md
└─ frontend/
   └─ README.md
```

---

## Technology stack
- Java 17+
- Spring Boot 3.x (Spring Web / MVC)
- Maven
- RestTemplate, Jackson

---

## Configuration
Application properties (default values are provided):
- `github.api.base-url` — default: `https://api.github.com`
- `github.api.user-agent` — default: `GitHub-Org-Snapshot-App`
- `github.api.per-page` — default: `100`

Server port:
- Use `SERVER_PORT` env or `server.port` in `application.properties` (default: `8080`).

No GitHub token is required for public data. The `User-Agent` header is set automatically.

---

## How to run locally

Prerequisites:
- Java 17+
- Maven 3.9+

Run in dev mode:
```
mvn spring-boot:run
```
or
```
mvn clean package
java -jar target/backend-*.jar
```

---

## Endpoints
- `GET /api/health` — service health
- `GET /api/org/{org}/repos?limit={1..20}&sort={stars|updated}` — repositories

---

## Example requests (curl)

Health:

```
curl -s http://localhost:8080/api/health
```

Top 5 by stars (default sort):

```
curl -s "http://localhost:8080/api/org/vercel/repos?limit=5&sort=stars"
```

Top 10 by last updated:

```
curl -s "http://localhost:8080/api/org/spring-projects/repos?limit=10&sort=updated"
```

Org not found (returns empty array):

```
curl -s "http://localhost:8080/api/org/__no_such_org__/repos?limit=5&sort=stars"
```

