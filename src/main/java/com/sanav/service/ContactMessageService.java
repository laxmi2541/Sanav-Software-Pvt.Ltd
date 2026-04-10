package com.sanav.service;

import com.sanav.entity.ContactMessage;
import com.sanav.entity.MessageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ContactMessageService {
    Page<ContactMessage> getAllMessages(Pageable pageable);
    Page<ContactMessage> searchMessages(String query, Pageable pageable);
    ContactMessage getMessageById(Long id);
    ContactMessage saveMessage(ContactMessage message);
    ContactMessage markAsRead(Long id, MessageStatus status);
    void deleteMessage(Long id);
    List<ContactMessage> getRecentMessages();
}
