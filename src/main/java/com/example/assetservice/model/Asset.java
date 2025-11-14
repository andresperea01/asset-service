package com.example.assetservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "assets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private String fileType; // application/pdf, image/jpeg, video/mp4, etc.

    @Column(nullable = false)
    private Long fileSize; // en bytes

    @Column(nullable = false)
    private LocalDateTime uploadDate;

    private LocalDateTime updatedDate;

    @Column(nullable = false)
    private String category; // PDF, IMAGE, VIDEO

    @PrePersist
    protected void onCreate() {
        uploadDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }
}