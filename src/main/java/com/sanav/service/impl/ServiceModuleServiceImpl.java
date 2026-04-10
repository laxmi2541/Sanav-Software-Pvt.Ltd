package com.sanav.service.impl;

import com.sanav.entity.Service;
import com.sanav.exception.ResourceNotFoundException;
import com.sanav.repository.ServiceRepository;
import com.sanav.service.ServiceModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
@org.springframework.stereotype.Service
public class ServiceModuleServiceImpl implements ServiceModuleService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Override
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    @Override
    public Service getServiceById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));
    }

    @Override
    public Service createService(Service service) {
        return serviceRepository.save(service);
    }

    @Override
    public Service updateService(Long id, Service service) {
        Service existing = getServiceById(id);
        existing.setTitle(service.getTitle());
        existing.setDescription(service.getDescription());
        existing.setIcon(service.getIcon());
        return serviceRepository.save(existing);
    }

    @Override
    public void deleteService(Long id) {
        Service existing = getServiceById(id);
        serviceRepository.delete(existing);
    }
}
