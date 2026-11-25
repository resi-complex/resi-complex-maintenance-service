package com.resicomplex.maintenance.service;

import com.resicomplex.maintenance.dto.CreateMaintenanceRequest;
import com.resicomplex.maintenance.dto.MaintenanceRequestResponse;
import com.resicomplex.maintenance.dto.UpdateMaintenanceRequest;
import com.resicomplex.maintenance.model.MaintenancePriority;
import com.resicomplex.maintenance.model.MaintenanceStatus;
import java.util.List;

public interface IMaintenanceRequestService {

    MaintenanceRequestResponse createRequest(CreateMaintenanceRequest request);

    List<MaintenanceRequestResponse> getAllRequests();

    List<MaintenanceRequestResponse> getRequestsByUnitId(String unitId);

    List<MaintenanceRequestResponse> getRequestsByStatus(MaintenanceStatus status);

    List<MaintenanceRequestResponse> getRequestsByPriority(MaintenancePriority priority);

    MaintenanceRequestResponse getRequestById(String id);

    MaintenanceRequestResponse updateRequest(String id, UpdateMaintenanceRequest request);

    MaintenanceRequestResponse resolveRequest(String id);
}
