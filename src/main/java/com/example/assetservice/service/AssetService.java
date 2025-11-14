package com.example.assetservice.service;

import com.example.assetservice.config.FileSizeLimits;
import com.example.assetservice.exception.FileStorageException;
import com.example.assetservice.exception.ResourceNotFoundException;
import com.example.assetservice.model.Asset;
import com.example.assetservice.model.AssetDTO;
import com.example.assetservice.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private FileSizeLimits fileSizeLimits;

    // CREATE - Crear asset con archivo y OVA
    public AssetDTO createAsset(String name, String description, MultipartFile file, String ovaId, String ovaName) {

        // Validar tamaño del archivo según categoría
        String category = fileStorageService.getFileCategory(file.getContentType());
        validateFileSize(file, category);

        // Guardar archivo
        String fileName = fileStorageService.storeFile(file);

        // Crear entidad
        Asset asset = new Asset();
        asset.setName(name);
        asset.setDescription(description);
        asset.setFileName(file.getOriginalFilename());
        asset.setFilePath(fileName);
        asset.setFileType(file.getContentType());
        asset.setFileSize(file.getSize());
        asset.setCategory(category);
        asset.setOvaId(ovaId);
        asset.setOvaName(ovaName);

        Asset savedAsset = assetRepository.save(asset);

        return convertToDTO(savedAsset);
    }

    // Validar tamaño del archivo
    private void validateFileSize(MultipartFile file, String category) {
        long maxSize;

        switch (category) {
            case "PDF":
                maxSize = fileSizeLimits.getPdfBytes();
                break;
            case "IMAGE":
                maxSize = fileSizeLimits.getImageBytes();
                break;
            case "VIDEO":
                maxSize = fileSizeLimits.getVideoBytes();
                break;
            default:
                maxSize = 100 * 1024 * 1024; // 100MB por defecto
        }

        if (file.getSize() > maxSize) {
            throw new FileStorageException(
                    String.format("El archivo excede el tamaño máximo permitido para %s: %d MB",
                            category, maxSize / (1024 * 1024))
            );
        }
    }

    // READ - Obtener todos los assets
    public List<AssetDTO> getAllAssets() {
        return assetRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // READ - Obtener asset por ID
    public AssetDTO getAssetById(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset no encontrado con id: " + id));
        return convertToDTO(asset);
    }

    // READ - Obtener assets por categoría
    public List<AssetDTO> getAssetsByCategory(String category) {
        return assetRepository.findByCategory(category).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // UPDATE - Actualizar asset (sin archivo)
    public AssetDTO updateAsset(Long id, AssetDTO assetDTO) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset no encontrado con id: " + id));

        asset.setName(assetDTO.getName());
        asset.setDescription(assetDTO.getDescription());

        Asset updatedAsset = assetRepository.save(asset);
        return convertToDTO(updatedAsset);
    }

    // UPDATE - Actualizar asset con nuevo archivo
    public AssetDTO updateAssetWithFile(Long id, String name, String description, MultipartFile file, String ovaId, String ovaName) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset no encontrado con id: " + id));

        // Validar tamaño del archivo
        String category = fileStorageService.getFileCategory(file.getContentType());
        validateFileSize(file, category);

        // Eliminar archivo anterior
        fileStorageService.deleteFile(asset.getFilePath());

        // Guardar nuevo archivo
        String fileName = fileStorageService.storeFile(file);

        // Actualizar datos
        asset.setName(name);
        asset.setDescription(description);
        asset.setFileName(file.getOriginalFilename());
        asset.setFilePath(fileName);
        asset.setFileType(file.getContentType());
        asset.setFileSize(file.getSize());
        asset.setCategory(category);
        asset.setOvaId(ovaId);
        asset.setOvaName(ovaName);

        Asset updatedAsset = assetRepository.save(asset);
        return convertToDTO(updatedAsset);
    }

    // DELETE - Eliminar asset
    public void deleteAsset(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset no encontrado con id: " + id));

        // Eliminar archivo físico
        fileStorageService.deleteFile(asset.getFilePath());

        // Eliminar registro de BD
        assetRepository.delete(asset);
    }

    // Convertir entidad a DTO
    private AssetDTO convertToDTO(Asset asset) {
        AssetDTO dto = new AssetDTO();
        dto.setId(asset.getId());
        dto.setName(asset.getName());
        dto.setDescription(asset.getDescription());
        dto.setFileName(asset.getFileName());
        dto.setFilePath(asset.getFilePath());
        dto.setFileType(asset.getFileType());
        dto.setFileSize(asset.getFileSize());
        dto.setUploadDate(asset.getUploadDate());
        dto.setUpdatedDate(asset.getUpdatedDate());
        dto.setCategory(asset.getCategory());

        // Generar URL de descarga
        String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/assets/download/")
                .path(asset.getFilePath())
                .toUriString();
        dto.setDownloadUrl(downloadUrl);

        return dto;
    }
}