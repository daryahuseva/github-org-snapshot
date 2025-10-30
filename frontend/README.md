# GitHub Org Snapshot - Frontend

Single-page UI to browse top repositories of a GitHub organization.

---

## How to run locally

This is a static site. Use any static server (or open `index.html` directly):

- Quick (Python):
  ```
  # from frontend directory
  python -m http.server 5173
  # then open http://localhost:5173
  ```
- Or just open `index.html` in a browser.

Backend must run at `http://localhost:8080` by default.

---

## Configure backend URL

The API base URL is defined in `script.js` as a constant:

```
const API_BASE_URL = 'http://localhost:8080';
```

If your backend runs elsewhere, change this value accordingly (e.g. to `https://your-host`).

---

## Features
- Organization input (default: `vercel`)
- Sort selector: `stars` or `updated`
- Limit: 1–20 (default 5)
- Load button
- Cards: name (link), stars, forks, language, updatedAt, description
- Loading / Empty / Error states

---

## API contract
Calls the backend:
```
GET /api/org/{org}/repos?limit=1..20&sort=stars|updated
```
