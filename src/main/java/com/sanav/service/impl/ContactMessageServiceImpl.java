package com.sanav.service.impl;

import com.sanav.entity.AuditLog;
import com.sanav.entity.ContactMessage;
import com.sanav.entity.MessageStatus;
import com.sanav.exception.ResourceNotFoundException;
import com.sanav.repository.AuditLogRepository;
import com.sanav.repository.ContactMessageRepository;
import com.sanav.service.ContactMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ContactMessageServiceImpl implements ContactMessageService {

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Override
    public Page<ContactMessage> getAllMessages(Pageable pageable) {
        return contactMessageRepository.findAll(pageable);
    }

    @Override
    public Page<ContactMessage> searchMessages(String query, Pageable pageable) {
        return contactMessageRepository.findByEmailContainingOrSubjectContaining(query, query, pageable);
    }

    @Override
    public ContactMessage getMessageById(Long id) {
        return contactMessageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));
    }

    @Override
    public ContactMessage saveMessage(ContactMessage message) {
        if (message.getStatus() == null) message.setStatus(MessageStatus.UNREAD);
        return contactMessageRepository.save(message);
    }

    @Override
    public ContactMessage markAsRead(Long id, MessageStatus status) {
        ContactMessage message = getMessageById(id);
        message.setStatus(status);
        return contactMessageRepository.save(message);
    }

    @Override
    public void deleteMessage(Long id) {
        ContactMessage message = getMessageById(id);
        auditLogRepository.save(AuditLog.builder()
                .action("DELETE_MESSAGE")
                .targetType("MESSAGE")
                .targetId(id)
                .details("Deleted message from: " + message.getEmail())
                .build());
        contactMessageRepository.delete(message);
    }

    @Override
    public List<ContactMessage> getRecentMessages() {
        return contactMessageRepository.findTop5ByOrderByCreatedAtDesc();
    }
}
