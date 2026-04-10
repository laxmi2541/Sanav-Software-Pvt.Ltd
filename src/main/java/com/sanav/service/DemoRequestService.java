package com.sanav.service;

import com.sanav.entity.DemoRequest;
import java.util.List;

public interface DemoRequestService {
    DemoRequest saveDemoRequest(DemoRequest request);
    List<DemoRequest> getAllDemoRequests();
}
