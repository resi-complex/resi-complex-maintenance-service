package com.resicomplex.maintenance.dto;

import com.resicomplex.maintenance.model.MaintenancePriority;
import com.resicomplex.maintenance.model.MaintenanceStatus;
import java.time.LocalDateTime;

public record MaintenanceRequestResponse(
        String id,
        String unitId,
        String title,
        String description,
        MaintenancePriority priority,
        MaintenanceStatus status,
        String assignedStaffId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
