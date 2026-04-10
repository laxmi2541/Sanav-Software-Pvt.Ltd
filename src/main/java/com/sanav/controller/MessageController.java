package com.sanav.controller;

import com.sanav.entity.ContactMessage;
import com.sanav.entity.MessageStatus;
import com.sanav.response.ApiResponse;
import com.sanav.service.ContactMessageService;
import com.sanav.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@PreAuthorize("hasRole('ADMIN')")
public class MessageController {

    @Autowired
    private ContactMessageService service;

    @Autowired
    private EmailService emailService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        Pageable pageable = PageRequest.of(page, size);
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success("Messages searched", service.searchMessages(search, pageable)));
        }
        return ResponseEntity.ok(ApiResponse.success("Messages retrieved", service.getAllMessages(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getMessage(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Message retrieved", service.getMessageById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteMessage(@PathVariable Long id) {
        service.deleteMessage(id);
        return ResponseEntity.ok(ApiResponse.success("Message deleted", null));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Message marked as read", service.markAsRead(id, MessageStatus.READ)));
    }

    @PutMapping("/{id}/unread")
    public ResponseEntity<ApiResponse> markAsUnread(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Message marked as unread", service.markAsRead(id, MessageStatus.UNREAD)));
    }

    @PostMapping("/{id}/reply")
    public ResponseEntity<ApiResponse> replyToMessage(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        ContactMessage message = service.getMessageById(id);
        String replyBody = payload.get("reply");
        emailService.sendReplyEmail(message.getEmail(), message.getSubject(), replyBody);
        
        service.markAsRead(id, MessageStatus.READ);
        
        return ResponseEntity.ok(ApiResponse.success("Reply sent to " + message.getEmail(), null));
    }
}
