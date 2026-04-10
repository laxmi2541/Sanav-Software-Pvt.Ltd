package com.sanav.controller;

import com.sanav.entity.Client;
import com.sanav.response.ApiResponse;
import com.sanav.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @GetMapping("/clients")
    public ResponseEntity<ApiResponse> getAllClients() {
        return ResponseEntity.ok(ApiResponse.success("Clients retrieved", clientService.getAllClients()));
    }
}
