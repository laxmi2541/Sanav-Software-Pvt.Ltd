package com.sanav.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "training_courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
    
    @Column(length = 1000)
    private String description;
    
    private String duration;
    private Double fees;
}
