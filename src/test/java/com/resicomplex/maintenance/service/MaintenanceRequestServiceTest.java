package com.resicomplex.maintenance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.resicomplex.maintenance.dto.CreateMaintenanceRequest;
import com.resicomplex.maintenance.dto.MaintenanceRequestResponse;
import com.resicomplex.maintenance.dto.UpdateMaintenanceRequest;
import com.resicomplex.maintenance.model.MaintenancePriority;
import com.resicomplex.maintenance.model.MaintenanceRequest;
import com.resicomplex.maintenance.model.MaintenanceStatus;
import com.resicomplex.maintenance.repository.MaintenanceRequestRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class MaintenanceRequestServiceTest {

    @Mock
    private MaintenanceRequestRepository repository;

    @InjectMocks
    private MaintenanceRequestService service;

    @Test
    void createRequest_shouldCreateWithPendingStatus() {
        CreateMaintenanceRequest request = new CreateMaintenanceRequest(
                "unit-uuid-101",
                "Fuga de agua en lavaplatos",
                "Se presenta filtración constante debajo del mueble de la cocina.",
                MaintenancePriority.URGENT
        );

        when(repository.save(any(MaintenanceRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MaintenanceRequestResponse response = service.createRequest(request);

        assertNotNull(response.id());
        assertEquals("unit-uuid-101", response.unitId());
        assertEquals("Fuga de agua en lavaplatos", response.title());
        assertEquals("Se presenta filtración constante debajo del mueble de la cocina.", response.description());
        assertEquals(MaintenancePriority.URGENT, response.priority());
        assertEquals(MaintenanceStatus.PENDING, response.status());
        assertNull(response.assignedStaffId());
        assertNotNull(response.createdAt());
        assertNotNull(response.updatedAt());
    }

    @Test
    void updateRequest_shouldAssignStaffAndAutoTransitionToAssigned() {
        MaintenanceRequest existing = new MaintenanceRequest();
        existing.setId("req-1");
        existing.setUnitId("unit-101");
        existing.setTitle("Fallo en citófono");
        existing.setDescription("No timbra desde portería.");
        existing.setPriority(MaintenancePriority.MEDIUM);
        existing.setStatus(MaintenanceStatus.PENDING);
        existing.setCreatedAt(LocalDateTime.now());
        existing.setUpdatedAt(LocalDateTime.now());

        when(repository.findById("req-1")).thenReturn(Optional.of(existing));
        when(repository.save(any(MaintenanceRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UpdateMaintenanceRequest update = new UpdateMaintenanceRequest(null, "staff-tech-01");
        MaintenanceRequestResponse response = service.updateRequest("req-1", update);

        assertEquals("staff-tech-01", response.assignedStaffId());
        assertEquals(MaintenanceStatus.ASSIGNED, response.status());
    }

    @Test
    void updateRequest_shouldThrowBadRequestWhenEmptyPayload() {
        MaintenanceRequest existing = new MaintenanceRequest();
        existing.setId("req-1");
        when(repository.findById("req-1")).thenReturn(Optional.of(existing));

        UpdateMaintenanceRequest emptyUpdate = new UpdateMaintenanceRequest(null, "   ");

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> service.updateRequest("req-1", emptyUpdate)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void resolveRequest_shouldTransitionStatusToResolved() {
        MaintenanceRequest existing = new MaintenanceRequest();
        existing.setId("req-2");
        existing.setUnitId("unit-202");
        existing.setTitle("Cambio de luminaria");
        existing.setDescription("Bombillo fundido en pasillo.");
        existing.setPriority(MaintenancePriority.LOW);
        existing.setStatus(MaintenanceStatus.IN_PROGRESS);
        existing.setAssignedStaffId("staff-tech-02");
        existing.setCreatedAt(LocalDateTime.now());
        existing.setUpdatedAt(LocalDateTime.now());

        when(repository.findById("req-2")).thenReturn(Optional.of(existing));
        when(repository.save(any(MaintenanceRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MaintenanceRequestResponse response = service.resolveRequest("req-2");

        assertEquals(MaintenanceStatus.RESOLVED, response.status());
        assertEquals("staff-tech-02", response.assignedStaffId());
    }

    @Test
    void getRequestsByStatus_shouldReturnFilteredList() {
        MaintenanceRequest existing = new MaintenanceRequest();
        existing.setId("req-3");
        existing.setUnitId("unit-303");
        existing.setTitle("Revisión puerta local");
        existing.setDescription("Cerradura trabada.");
        existing.setPriority(MaintenancePriority.HIGH);
        existing.setStatus(MaintenanceStatus.PENDING);
        existing.setCreatedAt(LocalDateTime.now());
        existing.setUpdatedAt(LocalDateTime.now());

        when(repository.findByStatus(MaintenanceStatus.PENDING)).thenReturn(List.of(existing));

        List<MaintenanceRequestResponse> results = service.getRequestsByStatus(MaintenanceStatus.PENDING);

        assertEquals(1, results.size());
        assertEquals(MaintenanceStatus.PENDING, results.get(0).status());
    }

    @Test
    void getRequestById_shouldThrowNotFoundWhenMissing() {
        when(repository.findById("missing-id")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> service.getRequestById("missing-id")
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}
