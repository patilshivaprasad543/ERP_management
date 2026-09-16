# ERP Management Platform

A modular enterprise resource planning platform built with Spring Boot, PostgreSQL, and a modern web frontend.

## Initial modules
- Authentication & role-based access
- Employee management
- Department management
- Attendance
- Leave management
- Expense management
- Procurement & vendors
- Inventory
- Finance-ready transaction layer
- Notifications and audit logging
- Dashboard/reporting foundation

## Architecture

```text
ERP_management/
├── backend/        Spring Boot REST API
├── frontend/       Web client
├── database/       SQL/schema documentation
└── docs/           Architecture and API documentation
```

## Roles
`SUPER_ADMIN`, `ADMIN`, `HR`, `MANAGER`, `FINANCE`, `PROCUREMENT`, `EMPLOYEE`

## Development principles
- REST APIs with validation
- DTO/service/repository separation
- PostgreSQL persistence
- BCrypt password hashing
- JWT-ready security boundary
- Auditability for business actions
- Pagination/filtering for enterprise lists
- Automated tests and CI

## Status
Phase 1 foundation is being implemented. Subsequent phases will add complete business workflows, UI screens, reporting, notifications, integrations, and production hardening.
