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
    private String fileType;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private LocalDateTime uploadDate;

    private LocalDateTime updatedDate;

    @Column(nullable = false)
    private String category;

    @Column(name = "ova_id")
    private String ovaId; // ID del OVA asociado

    @Column(name = "ova_name")
    private String ovaName; // Nombre del OVA

    @Column(name = "thumbnail_path")
    private String thumbnailPath; // Ruta de la miniatura/preview

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