package com.sanav.repository;

import com.sanav.entity.ContactMessage;
import com.sanav.entity.MessageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
    Page<ContactMessage> findByEmailContainingOrSubjectContaining(String email, String subject, Pageable pageable);
    long countByStatus(MessageStatus status);
    List<ContactMessage> findTop5ByOrderByCreatedAtDesc();
    long count();
}
