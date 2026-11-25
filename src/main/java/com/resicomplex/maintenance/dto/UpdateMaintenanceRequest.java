package com.resicomplex.maintenance.dto;

import com.resicomplex.maintenance.model.MaintenanceStatus;
import jakarta.validation.constraints.Size;

public record UpdateMaintenanceRequest(
        MaintenanceStatus status,

        @Size(max = 36, message = "Assigned staff ID must not exceed 36 characters")
        String assignedStaffId
) {}
