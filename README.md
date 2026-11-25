# resi-complex-maintenance-service

Microservicio de **Gestión de Mantenimiento** para el ecosistema `resi-complex` (Sistema de Gestión Integral para Conjuntos Residenciales Mixtos). Gestiona el ciclo de vida de solicitudes de reparación y mantenimiento (`PENDING`, `ASSIGNED`, `IN_PROGRESS`, `RESOLVED`), clasificación por prioridad (`LOW`, `MEDIUM`, `HIGH`, `URGENT`) y asignación de personal técnico.

## Stack Tecnológico
- **Java:** 25
- **Framework:** Spring Boot 4.0.8
- **Base de Datos:** MySQL (`maintenance_db`)
- **Migraciones:** Flyway (`V1__init_maintenance.sql`)
- **Documentación API:** OpenAPI 3 / Swagger UI (`springdoc-openapi 3.0.3`)

## Configuración Local
- **Puerto por defecto:** `8086`
- **Swagger UI:** `http://localhost:8086/swagger-ui/index.html`
- **Variable de entorno requerida:** `DB_PASSWORD` (contraseña del usuario MySQL `resi_dev`).

## Endpoints Principales (`/api/v1/maintenance-requests`)
- `POST /api/v1/maintenance-requests` — Crea una nueva solicitud de mantenimiento en estado `PENDING`.
- `GET /api/v1/maintenance-requests` — Lista solicitudes (filtros opcionales `?status=...`, `?priority=...` o `?unitId=...`).
- `GET /api/v1/maintenance-requests/{id}` — Obtiene el detalle de una solicitud por su UUID.
- `GET /api/v1/maintenance-requests/unit/{unitId}` — Lista las solicitudes de una unidad específica.
- `PATCH /api/v1/maintenance-requests/{id}` — Actualiza el estado y/o asigna personal de mantenimiento.
- `PATCH /api/v1/maintenance-requests/{id}/resolve` — Marca directamente una solicitud como `RESOLVED`.
