package com.resicomplex.maintenance.service;

import com.resicomplex.maintenance.dto.CreateMaintenanceRequest;
import com.resicomplex.maintenance.dto.MaintenanceRequestResponse;
import com.resicomplex.maintenance.dto.UpdateMaintenanceRequest;
import com.resicomplex.maintenance.model.MaintenancePriority;
import com.resicomplex.maintenance.model.MaintenanceRequest;
import com.resicomplex.maintenance.model.MaintenanceStatus;
import com.resicomplex.maintenance.repository.MaintenanceRequestRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MaintenanceRequestService implements IMaintenanceRequestService {

    private final MaintenanceRequestRepository repository;

    public MaintenanceRequestService(MaintenanceRequestRepository repository) {
        this.repository = repository;
    }

    @Override
    public MaintenanceRequestResponse createRequest(CreateMaintenanceRequest request) {
        LocalDateTime now = LocalDateTime.now();

        MaintenanceRequest entity = new MaintenanceRequest();
        entity.setId(UUID.randomUUID().toString());
        entity.setUnitId(request.unitId().trim());
        entity.setTitle(request.title().trim());
        entity.setDescription(request.description().trim());
        entity.setPriority(request.priority());
        entity.setStatus(MaintenanceStatus.PENDING);
        entity.setAssignedStaffId(null);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        MaintenanceRequest saved = repository.save(entity);
        return toResponse(saved);
    }

    @Override
    public List<MaintenanceRequestResponse> getAllRequests() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<MaintenanceRequestResponse> getRequestsByUnitId(String unitId) {
        return repository.findByUnitId(unitId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<MaintenanceRequestResponse> getRequestsByStatus(MaintenanceStatus status) {
        return repository.findByStatus(status).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<MaintenanceRequestResponse> getRequestsByPriority(MaintenancePriority priority) {
        return repository.findByPriority(priority).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public MaintenanceRequestResponse getRequestById(String id) {
        MaintenanceRequest entity = findEntityById(id);
        return toResponse(entity);
    }

    @Override
    public MaintenanceRequestResponse updateRequest(String id, UpdateMaintenanceRequest request) {
        MaintenanceRequest entity = findEntityById(id);

        if (request == null || (request.status() == null
                && (request.assignedStaffId() == null || request.assignedStaffId().isBlank()))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "At least one field (status or assignedStaffId) must be provided"
            );
        }

        if (request.assignedStaffId() != null && !request.assignedStaffId().isBlank()) {
            entity.setAssignedStaffId(request.assignedStaffId().trim());
            if (request.status() == null && entity.getStatus() == MaintenanceStatus.PENDING) {
                entity.setStatus(MaintenanceStatus.ASSIGNED);
            }
        }

        if (request.status() != null) {
            entity.setStatus(request.status());
        }

        entity.setUpdatedAt(LocalDateTime.now());
        MaintenanceRequest saved = repository.save(entity);
        return toResponse(saved);
    }

    @Override
    public MaintenanceRequestResponse resolveRequest(String id) {
        MaintenanceRequest entity = findEntityById(id);
        entity.setStatus(MaintenanceStatus.RESOLVED);
        entity.setUpdatedAt(LocalDateTime.now());
        MaintenanceRequest saved = repository.save(entity);
        return toResponse(saved);
    }

    private MaintenanceRequest findEntityById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Maintenance request not found with id: " + id
                ));
    }

    private MaintenanceRequestResponse toResponse(MaintenanceRequest entity) {
        return new MaintenanceRequestResponse(
                entity.getId(),
                entity.getUnitId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getPriority(),
                entity.getStatus(),
                entity.getAssignedStaffId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
