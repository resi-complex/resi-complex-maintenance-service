package com.resicomplex.maintenance.dto;

import com.resicomplex.maintenance.model.MaintenancePriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateMaintenanceRequest(
        @NotBlank(message = "Unit ID is required")
        @Size(max = 36, message = "Unit ID must not exceed 36 characters")
        String unitId,

        @NotBlank(message = "Title is required")
        @Size(max = 150, message = "Title must not exceed 150 characters")
        String title,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Priority is required (LOW, MEDIUM, HIGH or URGENT)")
        MaintenancePriority priority
) {}
