package com.sanav.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "certificates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String studentName;
    
    @Column(unique = true, nullable = false)
    private String certificateCode;
    
    @Column(nullable = false)
    private String courseName;
    
    private LocalDateTime issueDate;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
