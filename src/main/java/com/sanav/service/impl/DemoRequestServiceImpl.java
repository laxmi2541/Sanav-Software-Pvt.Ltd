package com.sanav.service.impl;

import com.sanav.entity.DemoRequest;
import com.sanav.repository.DemoRequestRepository;
import com.sanav.service.DemoRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DemoRequestServiceImpl implements DemoRequestService {

    @Autowired
    private DemoRequestRepository repository;

    @Override
    public DemoRequest saveDemoRequest(DemoRequest request) {
        return repository.save(request);
    }

    @Override
    public List<DemoRequest> getAllDemoRequests() {
        return repository.findAll();
    }
}
