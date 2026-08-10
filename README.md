# AI Workforce OS — Auth Service

Backend authentication microservice for AI Workforce OS. Built with **Java Spring Boot 4.1.0**, **PostgreSQL (Supabase)**, **JWT**, and **OAuth2 (Google)**. Deployed on **Railway**.

---

## 1. Live URLs

| Purpose | URL |
|---|---|
| Base API URL | `https://workforce-os-backend-production.up.railway.app` |
| Swagger UI (API docs + testing) | `https://workforce-os-backend-production.up.railway.app/swagger-ui/index.html` |
| Google Login (OAuth2) | `https://workforce-os-backend-production.up.railway.app/oauth2/authorization/google` |

---

## 2. Current Status

| Feature | Status |
|---|---|
| Signup / Login (JWT + Email OTP MFA) | ✅ Done |
| OAuth2 Google Login | ✅ Done |
| Password Reset (Email link) | ✅ Done |
| Role-Based Access Control (RBAC) | ✅ Done |
| Session Management (multi-device, logout) | ✅ Done |
| Swagger API Documentation | ✅ Done |
| Deployed to Railway | ✅ Done |
| Organization Management (Teams/Departments) | 🔄 In Progress |
| Attendance Service | ⏳ Not Started |

---

## 3. Authentication Flows

### Flow A — Email + Password (with OTP MFA)
```
1. POST /api/auth/signup            → create account
2. POST /api/auth/login              → validates email+password, sends OTP to email
3. POST /api/auth/verify-otp         → validates OTP, returns JWT token
```

### Flow B — Google OAuth2 (no OTP needed)
```
1. Open in browser: GET /oauth2/authorization/google
2. Login with Google account, click Allow
3. Page returns: {"token": "eyJhbGci..."}  (temporary — normally would redirect to frontend)
```

Use the token from either flow as:
```
Authorization: Bearer <token>
```

---

## 4. How to Test via Swagger (Step by Step)

1. Get a token using **either** Flow A or Flow B above.
2. Open Swagger UI: `https://workforce-os-backend-production.up.railway.app/swagger-ui/index.html`
3. Click the **"Authorize"** button (top right, lock icon).
4. Paste the raw token (no `Bearer ` prefix — Swagger adds it automatically).
5. Click **Authorize**, then **Close**.
6. Expand any protected endpoint (e.g. `GET /api/users/profile`) → **Try it out** → **Execute**.

⚠️ **Important**: Every login (OAuth or OTP) issues a **new token** and creates a **new session**. If you re-authorize with an old token after logging out or after a new login replaced it, you'll get `401 Unauthorized`. Always use the most recent token.

---

## 5. Error Response Format

```json
{ "message": "Human-readable error description", "status": 400 }
```

---

## 6. Roles

| Role | Description |
|---|---|
| `EMPLOYEE` | Default role on signup |
| `MANAGER` | Can view their team (once Org module is live) |
| `HR_ADMIN` | Can view/manage all users |
| `LEADERSHIP` | Organization-wide visibility (future use) |
| `SUPER_ADMIN` | Full access, can assign roles |

To make a user `SUPER_ADMIN` (first-time bootstrap), manually edit the `role` column in Supabase's Table Editor for the `users` table.

---

## 7. Key Endpoints

| Method | Endpoint | Auth Required | Notes |
|---|---|---|---|
| POST | `/api/auth/signup` | No | |
| POST | `/api/auth/login` | No | Sends OTP |
| POST | `/api/auth/verify-otp` | No | Returns JWT |
| GET | `/oauth2/authorization/google` | No | Browser only |
| POST | `/api/auth/forgot-password` | No | |
| POST | `/api/auth/reset-password` | No | |
| GET | `/api/users/profile` | Yes | Any authenticated user |
| GET | `/api/users` | Yes | HR_ADMIN / SUPER_ADMIN only |
| PUT | `/api/users/{id}/role` | Yes | SUPER_ADMIN only |
| GET | `/api/users/sessions` | Yes | List active sessions |
| DELETE | `/api/users/sessions/logout` | Yes | Logout current device |
| DELETE | `/api/users/sessions/logout-all` | Yes | Logout all devices |

Full request/response schemas: see Swagger UI.

---

## 8. Known Issues / Things to Watch

- **Token vs Session mismatch**: If a token passes JWT signature validation but session was deleted (via logout), API calls return `401`. This is expected — get a fresh token.
- **OAuth without a frontend**: Currently the OAuth success handler returns the JWT as plain text on-screen (`Google Login Successful. Token: ...`) instead of redirecting to a frontend app, since no frontend exists yet. This will change once frontend integration begins — the handler will redirect to `app.frontend-url` with the token as a query param instead.
- **Mixed content (HTTP vs HTTPS)**: Swagger's OpenAPI config explicitly sets the server URL to `https://` to avoid browser-blocked "mixed content" errors on Railway. If Swagger ever shows `http://` in the generated cURL command, check `OpenApiConfig.java`'s `.addServersItem(...)`.
- **Email sending**: Uses Gmail SMTP with an App Password (not the regular Gmail password). If OTP/password-reset emails stop arriving, check that 2-Step Verification and the App Password are still active on the sender Gmail account, and that `MAIL_USERNAME` / `MAIL_PASSWORD` env vars are correctly set on Railway.

---

## 9. Environment Variables (set in Railway → Variables)

```
DB_PASSWORD=<supabase-db-password>
MAIL_USERNAME=<system-gmail-address>
MAIL_PASSWORD=<gmail-app-password>
GOOGLE_CLIENT_ID=<google-oauth-client-id>
GOOGLE_CLIENT_SECRET=<google-oauth-client-secret>
app.frontend-url=<placeholder-until-frontend-exists>
```

---

## 10. Local Setup (For New Developers)

```bash
git clone <repo-url>
cd workforce-os-backend/auth-service
mvn clean install
mvn spring-boot:run
```

Server starts on port `8081` locally. Set the same environment variables listed above in your IntelliJ Run Configuration.