package com.example.assetservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetDTO {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 200, message = "El nombre debe tener entre 3 y 200 caracteres")
    private String name;

    @Size(max = 2000, message = "La descripción no puede exceder 2000 caracteres")
    private String description;

    private String fileName;
    private String filePath;
    private String fileType;
    private Long fileSize;
    private LocalDateTime uploadDate;
    private LocalDateTime updatedDate;
    private String category;
    private String downloadUrl;
    private String ovaId;
    private String ovaName;
    private String thumbnailPath;
    private String previewUrl;
}