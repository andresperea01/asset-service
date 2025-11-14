package com.example.assetservice.service;

import com.example.assetservice.config.FileStorageProperties;
import com.example.assetservice.exception.FileStorageException;
import com.example.assetservice.exception.MyFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("No se pudo crear el directorio donde se almacenarán los archivos.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        // Normalizar nombre del archivo
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Verificar si el archivo contiene caracteres inválidos
            if (originalFileName.contains("..")) {
                throw new FileStorageException("El nombre del archivo contiene una secuencia de ruta inválida " + originalFileName);
            }

            // Generar un nombre único para el archivo
            String fileExtension = "";
            int dotIndex = originalFileName.lastIndexOf('.');
            if (dotIndex > 0) {
                fileExtension = originalFileName.substring(dotIndex);
            }

            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

            // Copiar archivo a la ubicación de destino
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return uniqueFileName;

        } catch (IOException ex) {
            throw new FileStorageException("No se pudo almacenar el archivo " + originalFileName + ". Intente de nuevo!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("Archivo no encontrado " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("Archivo no encontrado " + fileName, ex);
        }
    }

    public void deleteFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new FileStorageException("No se pudo eliminar el archivo " + fileName, ex);
        }
    }

    public String getFileCategory(String contentType) {
        if (contentType == null) {
            return "UNKNOWN";
        }

        if (contentType.startsWith("image/")) {
            return "IMAGE";
        } else if (contentType.startsWith("video/")) {
            return "VIDEO";
        } else if (contentType.equals("application/pdf")) {
            return "PDF";
        } else {
            return "OTHER";
        }
    }
}