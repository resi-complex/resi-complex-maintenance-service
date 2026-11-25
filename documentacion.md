# Documentación de Desarrollo — `resi-complex-maintenance-service`

**Responsable:** Alexandra Garzón  
**Rol:** Backend Developer & UX Specialist (Inmuebles, Personas y Mantenimiento)  
**Microservicio:** `resi-complex-maintenance-service` (Puerto `8086` | Base de datos: `maintenance_db`)

---

## Fase 1: Estructura Base Maven (Scaffolding)

- **[pom.xml](file:///c:/Users/Alexa_Var/Documents/resi-complex-maintenance-service/pom.xml):**
  - Parent: `org.springframework.boot:spring-boot-starter-parent:4.0.8` con **Java 25**.
  - `groupId`: `com.resicomplex` | `artifactId`: `maintenance-service`.
  - Dependencias estándar de `resi-complex`: `spring-boot-starter-webmvc`, `spring-boot-starter-data-jpa`, `spring-boot-starter-flyway`, `spring-boot-starter-validation`, `flyway-mysql`, `mysql-connector-j`, `lombok`, `springdoc-openapi-starter-webmvc-ui:3.0.3` y starters de test.
- **[MaintenanceServiceApplication.java](file:///c:/Users/Alexa_Var/Documents/resi-complex-maintenance-service/src/main/java/com/resicomplex/maintenance/MaintenanceServiceApplication.java):**
  - Clase principal `@SpringBootApplication` en el paquete base `com.resicomplex.maintenance`.

---

## Fase 2: Configuración (`application.properties`) y Migración Flyway (`V1__init_maintenance.sql`)

- **[V1__init_maintenance.sql](file:///c:/Users/Alexa_Var/Documents/resi-complex-maintenance-service/src/main/resources/db/migration/V1__init_maintenance.sql):**
  - Crea la tabla `maintenance_requests` en `maintenance_db` con `id VARCHAR(36) PRIMARY KEY`, `unit_id VARCHAR(36) NOT NULL`, `title VARCHAR(150) NOT NULL`, `description TEXT NOT NULL`, `priority VARCHAR(20) NOT NULL` (`LOW`, `MEDIUM`, `HIGH`, `URGENT`), `status VARCHAR(20) NOT NULL DEFAULT 'PENDING'` (`PENDING`, `ASSIGNED`, `IN_PROGRESS`, `RESOLVED`), `assigned_staff_id VARCHAR(36) NULL` y columnas de auditoría (`created_at`, `updated_at`, `deleted_at`).
  - Crea índices de consulta (`idx_maintenance_requests_unit_id`, `idx_maintenance_requests_status`, `idx_maintenance_requests_priority`).
- **[application.properties](file:///c:/Users/Alexa_Var/Documents/resi-complex-maintenance-service/src/main/resources/application.properties):**
  - Puerto `8086` (`${SERVER_PORT:8086}`), conexión JDBC a `maintenance_db`, validación estricta `spring.jpa.hibernate.ddl-auto=validate`, `spring.jpa.show-sql=true`, `spring.jpa.open-in-view=false` y Flyway activo.

---

## Fase 3: Capas de Modelo, Repositorio, DTOs, Servicio y Controlador

- **Modelo (`model/`):**
  - [MaintenancePriority.java](file:///c:/Users/Alexa_Var/Documents/resi-complex-maintenance-service/src/main/java/com/resicomplex/maintenance/model/MaintenancePriority.java): Enum `LOW`, `MEDIUM`, `HIGH`, `URGENT`.
  - [MaintenanceStatus.java](file:///c:/Users/Alexa_Var/Documents/resi-complex-maintenance-service/src/main/java/com/resicomplex/maintenance/model/MaintenanceStatus.java): Enum `PENDING`, `ASSIGNED`, `IN_PROGRESS`, `RESOLVED`.
  - [MaintenanceRequest.java](file:///c:/Users/Alexa_Var/Documents/resi-complex-maintenance-service/src/main/java/com/resicomplex/maintenance/model/MaintenanceRequest.java): Entidad JPA mapeada a `maintenance_requests` (incluyendo `columnDefinition = "TEXT"` en `description` para superar la validación de Hibernate).
- **Repositorio (`repository/`):**
  - [MaintenanceRequestRepository.java](file:///c:/Users/Alexa_Var/Documents/resi-complex-maintenance-service/src/main/java/com/resicomplex/maintenance/repository/MaintenanceRequestRepository.java): Extiende `JpaRepository<MaintenanceRequest, String>` con `findByUnitId`, `findByStatus` y `findByPriority`.
- **DTOs (`dto/`):**
  - [CreateMaintenanceRequest.java](file:///c:/Users/Alexa_Var/Documents/resi-complex-maintenance-service/src/main/java/com/resicomplex/maintenance/dto/CreateMaintenanceRequest.java): Record con validaciones `@NotBlank`, `@NotNull` y `@Size`.
  - [UpdateMaintenanceRequest.java](file:///c:/Users/Alexa_Var/Documents/resi-complex-maintenance-service/src/main/java/com/resicomplex/maintenance/dto/UpdateMaintenanceRequest.java): Record para asignar técnico (`assignedStaffId`) y/o cambiar estado (`status`).
  - [MaintenanceRequestResponse.java](file:///c:/Users/Alexa_Var/Documents/resi-complex-maintenance-service/src/main/java/com/resicomplex/maintenance/dto/MaintenanceRequestResponse.java): Record de respuesta con todos los datos públicos del ticket.
- **Servicio (`service/`):**
  - [IMaintenanceRequestService.java](file:///c:/Users/Alexa_Var/Documents/resi-complex-maintenance-service/src/main/java/com/resicomplex/maintenance/service/IMaintenanceRequestService.java) y [MaintenanceRequestService.java](file:///c:/Users/Alexa_Var/Documents/resi-complex-maintenance-service/src/main/java/com/resicomplex/maintenance/service/MaintenanceRequestService.java): Crea solicitudes en estado `PENDING`, filtra por unidad/estado/prioridad, asigna personal técnico (cambiando automáticamente de `PENDING` a `ASSIGNED` si no se envía otro estado) y permite resolver tickets (`RESOLVED`).
- **Controlador (`controller/`):**
  - [MaintenanceRequestController.java](file:///c:/Users/Alexa_Var/Documents/resi-complex-maintenance-service/src/main/java/com/resicomplex/maintenance/controller/MaintenanceRequestController.java): Expone `/api/v1/maintenance-requests` (y `/api/v1/maintenance`, `/api/v1/requests` para compatibilidad con el API Gateway) con `POST`, `GET`, `GET /{id}`, `GET /unit/{unitId}`, `PATCH /{id}` y `PATCH /{id}/resolve`.

---

## Fase 4: Pruebas Unitarias e Integración

- **[MaintenanceRequestServiceTest.java](file:///c:/Users/Alexa_Var/Documents/resi-complex-maintenance-service/src/test/java/com/resicomplex/maintenance/service/MaintenanceRequestServiceTest.java):** 6 pruebas unitarias con JUnit 5 y Mockito.
- **[MaintenanceServiceApplicationTests.java](file:///c:/Users/Alexa_Var/Documents/resi-complex-maintenance-service/src/test/java/com/resicomplex/maintenance/MaintenanceServiceApplicationTests.java):** Prueba de integración que levanta el contexto de Spring Boot contra MySQL `maintenance_db`, ejecuta la migración Flyway `V1__init_maintenance.sql` y valida el esquema con Hibernate (`ddl-auto=validate`).
