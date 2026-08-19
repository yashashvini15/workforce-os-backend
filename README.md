# AI Workforce OS — Backend

Backend platform for **AI Workforce OS**, built using a microservice architecture with **Java, Spring Boot, PostgreSQL, JWT, OAuth2, and REST APIs**.

The backend currently provides **Authentication, User Management, Organization Management, and Attendance Management**, with additional workforce services planned for future development.

---

## 🏗️ Architecture

```text
                              AI Workforce OS
                                     │
                                     ▼
                          ┌─────────────────────┐
                          │     Frontend App    │
                          └──────────┬──────────┘
                                     │
                    ┌────────────────┴────────────────┐
                    │                                 │
                    ▼                                 ▼
           ┌─────────────────────┐          ┌─────────────────────┐
           │     Auth Service    │          │ Attendance Service  │
           │       ✅ Live       │          │       ✅ Live       │
           │                     │          │                     │
           │ • Authentication    │          │ • Check-in/out      │
           │ • User Management   │          │ • Geofencing        │
           │ • Organization Mgmt │          │ • Wi-Fi Validation  │
           │ • JWT / OTP / MFA   │          │ • Attendance History│
           │ • Google OAuth2     │          │ • Corrections       │
           │ • RBAC & Sessions   │          │ • Late Detection    │
           └──────────┬──────────┘          └──────────┬──────────┘
                      │                                │
                      └────────────── JWT ─────────────┘
                                     │
                                     ▼
                                PostgreSQL


                     🔜 FUTURE WORKFORCE SERVICES
                                     │
              ┌──────────────────────┼──────────────────────┐
              │                      │                      │
              ▼                      ▼                      ▼
     ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
     │ Leave Management│    │ Payroll Service │    │ Notification    │
     │    🔜 Planned   │    │    🔜 Planned   │    │    🔜 Planned   │
     └─────────────────┘    └─────────────────┘    └─────────────────┘
              │                      │                      │
              └──────────────────────┼──────────────────────┘
                                     │
                                     ▼
                           ┌─────────────────────┐
                           │ Reporting &         │
                           │ Analytics            │
                           │ 🔜 Planned           │
                           └─────────────────────┘
```

---

## 📦 Services

### Currently Available

| Service | Status | Description |
|---|---|---|
| **Auth Service** | ✅ Completed | Authentication, user management, organization management, JWT, OTP/MFA, Google OAuth2, RBAC and session management |
| **Attendance Service** | ✅ Completed | Check-in/out, geofencing, Wi-Fi validation, attendance history, late detection and correction workflows |

### Future Services

| Service | Status | Description |
|---|---|---|
| **Leave Management** | 🔜 Planned | Employee leave requests, approvals and leave tracking |
| **Payroll Management** | 🔜 Planned | Payroll processing and employee compensation management |
| **Notifications** | 🔜 Planned | Workforce-related email and system notifications |
| **Reporting & Analytics** | 🔜 Planned | Workforce reports, dashboards and analytics |

> **Legend:** ✅ Completed &nbsp;&nbsp; 🔄 In Progress &nbsp;&nbsp; 🔜 Planned for Future

---

# 🔐 Auth Service

The Auth Service is the central identity and access-management service for AI Workforce OS.

It handles **authentication, user management, organization management, authorization, and session management**. Organization functionality includes **departments, teams, team assignment, and manager team visibility**. :contentReference[oaicite:1]{index=1}

### Key Features

- Signup & Login
- JWT Authentication
- Email OTP / MFA
- Google OAuth2
- Password Reset
- User Management
- Role-Based Access Control
- Multi-device Session Management
- Logout / Logout All Devices
- Organization Management
- Department Management
- Team Management
- User-to-Team Assignment
- Manager Team Visibility

### Live URLs

| Purpose | URL |
|---|---|
| Base API | `https://workforce-os-backend-production.up.railway.app` |
| Swagger UI | `https://workforce-os-backend-production.up.railway.app/swagger-ui/index.html` |
| Google Login | `https://workforce-os-backend-production.up.railway.app/oauth2/authorization/google` |

### Authentication Flow

```text
Email + Password
      │
      ▼
    Login
      │
      ▼
  Email OTP
      │
      ▼
 Verify OTP
      │
      ▼
  JWT Token
```

Google OAuth2 can also be used to obtain a JWT token.

Use the token for protected APIs:

```http
Authorization: Bearer <token>
```

### User Management Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/users/profile` | Get current user profile |
| GET | `/api/users` | Get all users |
| PUT | `/api/users/{id}/role` | Update user role |
| GET | `/api/users/sessions` | Get active sessions |
| DELETE | `/api/users/sessions/logout` | Logout current device |
| DELETE | `/api/users/sessions/logout-all` | Logout all devices |

### Organization Management Endpoints

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/organization/departments` | HR_ADMIN / SUPER_ADMIN | Create department |
| GET | `/api/organization/departments` | HR_ADMIN / SUPER_ADMIN / MANAGER | Get all departments |
| POST | `/api/organization/teams` | HR_ADMIN / SUPER_ADMIN | Create team |
| GET | `/api/organization/teams` | HR_ADMIN / SUPER_ADMIN / MANAGER | Get all teams |
| PUT | `/api/organization/users/{userId}/assign-team/{teamId}` | HR_ADMIN / SUPER_ADMIN | Assign user to team |
| GET | `/api/organization/my-team` | MANAGER | Get manager's team members |

### Authentication Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/signup` | Create account |
| POST | `/api/auth/login` | Login & send OTP |
| POST | `/api/auth/verify-otp` | Verify OTP & receive JWT |
| GET | `/oauth2/authorization/google` | Google OAuth2 login |
| POST | `/api/auth/forgot-password` | Request password reset |
| POST | `/api/auth/reset-password` | Reset password |

---

# 🕐 Attendance Service

The Attendance Service manages employee attendance and attendance correction workflows.

### Key Features

- Employee Check-in / Check-out
- Office Geofencing
- Office Wi-Fi Validation
- Late Attendance Detection
- Attendance History
- Date-range Attendance History
- Attendance Correction Requests
- Manager/Admin Correction Approval & Rejection
- JWT Authentication & RBAC
- Optimistic Locking for Correction Workflow

### Live URLs

| Purpose | URL |
|---|---|
| Base API | `https://attendance-service-production.up.railway.app` |
| Swagger UI | `https://attendance-service-production-e73b.up.railway.app/swagger-ui/index.html` |

### Key Endpoints

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/attendance/check-in` | Authenticated | Mark check-in |
| POST | `/api/attendance/check-out` | Authenticated | Mark check-out |
| GET | `/api/attendance/my-history` | Authenticated | Get attendance history |
| GET | `/api/attendance/my-history/range` | Authenticated | Get history by date range |
| POST | `/api/attendance/corrections/{attendanceId}` | Authenticated | Submit correction |
| GET | `/api/attendance/corrections/my-requests` | Authenticated | Get own correction requests |
| GET | `/api/attendance/corrections/pending` | Manager/Admin | Get pending corrections |
| PUT | `/api/attendance/corrections/{requestId}/approve` | Manager/Admin | Approve correction |
| PUT | `/api/attendance/corrections/{requestId}/reject` | Manager/Admin | Reject correction |

Attendance APIs use the **same JWT token issued by the Auth Service**.

```http
Authorization: Bearer <token>
```

### Attendance Workflow

```text
Employee
   │
   ▼
Check-In
   │
   ├── Location Validation
   ├── Wi-Fi Validation
   └── Late Detection
   │
   ▼
Attendance Record
   │
   ▼
Check-Out
```

### Correction Workflow

```text
Employee
   │
   ▼
Submit Correction
   │
   ▼
Pending Request
   │
   ├───────────────┐
   ▼               ▼
Approve           Reject
   │
   ▼
Attendance Updated
```

---

# 👥 Roles

| Role | Description |
|---|---|
| `EMPLOYEE` | Standard employee access |
| `MANAGER` | Team visibility and attendance correction management |
| `HR_ADMIN` | User, organization and attendance administration |
| `LEADERSHIP` | Organization-wide visibility |
| `SUPER_ADMIN` | Full administrative access |

---

# 🛠️ Tech Stack

| Category | Technologies |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 4.1.0 |
| Security | Spring Security, JWT, OAuth2 |
| Database | PostgreSQL |
| Persistence | Spring Data JPA, Hibernate |
| Validation | Jakarta Validation |
| API | REST, OpenAPI / Swagger |
| Build | Maven |
| Deployment | Railway |
| Repository | GitHub |

---

# 🚀 Local Setup

## Prerequisites

- Java 17+
- Maven
- PostgreSQL
- Git

## Clone Repository

```bash
git clone https://github.com/yashashvini15/workforce-os-backend.git
cd workforce-os-backend
```

## Auth Service

```bash
cd auth-service
mvn clean install
mvn spring-boot:run
```

Runs on:

```text
http://localhost:8081
```

## Attendance Service

```bash
cd attendance-service
mvn clean install
mvn spring-boot:run
```

Runs on:

```text
http://localhost:8082
```

Configure database credentials, JWT/OAuth credentials and attendance configuration through environment variables.

---

# ☁️ Deployment

Both implemented services are independently deployed on **Railway**.

```text
                         GitHub
                            │
                 ┌──────────┴──────────┐
                 │                     │
                 ▼                     ▼
          Auth Service          Attendance Service
                 │                     │
                 ▼                     ▼
             Railway               Railway
                 │                     │
                 └──────────┬──────────┘
                            │
                            ▼
                       PostgreSQL
```

Each service can be developed and deployed independently while using the common JWT-based authentication mechanism.

---

# 📚 API Documentation

Interactive Swagger documentation is available for both services.

### Auth Service

`https://workforce-os-backend-production.up.railway.app/swagger-ui/index.html`

### Attendance Service

`https://attendance-service-production-e73b.up.railway.app/swagger-ui/index.html`

Swagger can be used to:

- View available endpoints
- View request/response schemas
- Authorize using JWT
- Execute API requests
- Test protected endpoints

---

# 📌 Project Status

### Completed

- ✅ Authentication & Authorization
- ✅ JWT Authentication
- ✅ Email OTP / MFA
- ✅ Google OAuth2
- ✅ Password Reset
- ✅ User Management
- ✅ Role-Based Access Control
- ✅ Session Management
- ✅ Organization Management
- ✅ Department Management
- ✅ Team Management
- ✅ Attendance Management
- ✅ Geofencing
- ✅ Wi-Fi Validation
- ✅ Attendance Corrections

### Planned for Future

- 🔜 Leave Management
- 🔜 Payroll Management
- 🔜 Notifications
- 🔜 Reporting & Analytics

---

## 👩‍💻 Author

**Yashashwi Soni**

Java • Spring Boot • Microservices • Backend Development

---

## 📄 License

This project is currently maintained as part of the **AI Workforce OS** development project.
