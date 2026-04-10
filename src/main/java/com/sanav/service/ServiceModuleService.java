package com.sanav.service;

import com.sanav.entity.Service;
import java.util.List;

public interface ServiceModuleService {
    List<Service> getAllServices();
    Service getServiceById(Long id);
    Service createService(Service service);
    Service updateService(Long id, Service service);
    void deleteService(Long id);
}
