package com.resicomplex.maintenance.controller;

import com.resicomplex.maintenance.dto.CreateMaintenanceRequest;
import com.resicomplex.maintenance.dto.MaintenanceRequestResponse;
import com.resicomplex.maintenance.dto.UpdateMaintenanceRequest;
import com.resicomplex.maintenance.model.MaintenancePriority;
import com.resicomplex.maintenance.model.MaintenanceStatus;
import com.resicomplex.maintenance.service.IMaintenanceRequestService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/v1/maintenance-requests", "/api/v1/maintenance", "/api/v1/requests"})
public class MaintenanceRequestController {

    private final IMaintenanceRequestService service;

    public MaintenanceRequestController(IMaintenanceRequestService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MaintenanceRequestResponse createRequest(@Valid @RequestBody CreateMaintenanceRequest request) {
        return service.createRequest(request);
    }

    @GetMapping
    public List<MaintenanceRequestResponse> getRequests(
            @RequestParam(name = "unitId", required = false) String unitId,
            @RequestParam(name = "status", required = false) MaintenanceStatus status,
            @RequestParam(name = "priority", required = false) MaintenancePriority priority
    ) {
        if (unitId != null && !unitId.isBlank()) {
            return service.getRequestsByUnitId(unitId.trim());
        }
        if (status != null) {
            return service.getRequestsByStatus(status);
        }
        if (priority != null) {
            return service.getRequestsByPriority(priority);
        }
        return service.getAllRequests();
    }

    @GetMapping("/{id}")
    public MaintenanceRequestResponse getRequestById(@PathVariable("id") String id) {
        return service.getRequestById(id);
    }

    @GetMapping("/unit/{unitId}")
    public List<MaintenanceRequestResponse> getRequestsByUnit(@PathVariable("unitId") String unitId) {
        return service.getRequestsByUnitId(unitId);
    }

    @PatchMapping("/{id}")
    public MaintenanceRequestResponse updateRequest(
            @PathVariable("id") String id,
            @Valid @RequestBody UpdateMaintenanceRequest request
    ) {
        return service.updateRequest(id, request);
    }

    @PatchMapping("/{id}/resolve")
    public MaintenanceRequestResponse resolveRequest(@PathVariable("id") String id) {
        return service.resolveRequest(id);
    }
}
