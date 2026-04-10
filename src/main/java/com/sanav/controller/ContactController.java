package com.sanav.controller;

import com.sanav.entity.ContactMessage;
import com.sanav.response.ApiResponse;
import com.sanav.service.ContactMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Slf4j
public class ContactController {

    @Autowired
    private ContactMessageService service;

    @PostMapping("/contact")
    public ResponseEntity<ApiResponse> sendContactMessage(@RequestBody ContactMessage message) {
        log.info("Received contact form request: {}", message);
        try {
            ContactMessage savedMessage = service.saveMessage(message);
            log.info("Successfully saved message with ID: {}", savedMessage.getId());
            return ResponseEntity.ok(ApiResponse.success("Message sent successfully", savedMessage));
        } catch (Exception e) {
            log.error("Error saving contact message: ", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to save message: " + e.getMessage()));
        }
    }

}
