package com.example.assetservice.controller;

import com.example.assetservice.config.FileSizeLimits;
import com.example.assetservice.model.AssetDTO;
import com.example.assetservice.service.AssetService;
import com.example.assetservice.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assets")
@Tag(name = "Assets", description = "API para gestión de assets (PDF, imágenes, videos)")
@CrossOrigin(origins = "*")
public class AssetController {

    @Autowired
    private AssetService assetService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private FileSizeLimits fileSizeLimits;

    // POST - Crear nuevo asset con archivo
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Crear nuevo asset", description = "Crea un asset con archivo (PDF, imagen o video)")
    public ResponseEntity<Map<String, Object>> createAsset(
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "ovaId", required = false) String ovaId,
            @RequestParam(value = "ovaName", required = false) String ovaName) {

        AssetDTO createdAsset = assetService.createAsset(name, description, file, ovaId, ovaName);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Asset creado exitosamente");
        response.put("data", createdAsset);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET - Obtener todos los assets
    @GetMapping
    @Operation(summary = "Listar todos los assets", description = "Obtiene la lista completa de assets")
    public ResponseEntity<Map<String, Object>> getAllAssets() {
        List<AssetDTO> assets = assetService.getAllAssets();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", assets.size());
        response.put("data", assets);

        return ResponseEntity.ok(response);
    }

    // GET - Obtener asset por ID
    @GetMapping("/{id}")
    @Operation(summary = "Obtener asset por ID", description = "Obtiene un asset específico por su ID")
    public ResponseEntity<Map<String, Object>> getAssetById(
            @Parameter(description = "ID del asset") @PathVariable Long id) {

        AssetDTO asset = assetService.getAssetById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", asset);

        return ResponseEntity.ok(response);
    }

    // GET - Obtener assets por categoría
    @GetMapping("/category/{category}")
    @Operation(summary = "Filtrar por categoría", description = "Obtiene assets filtrados por categoría (PDF, IMAGE, VIDEO)")
    public ResponseEntity<Map<String, Object>> getAssetsByCategory(
            @Parameter(description = "Categoría: PDF, IMAGE, VIDEO") @PathVariable String category) {

        List<AssetDTO> assets = assetService.getAssetsByCategory(category.toUpperCase());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("category", category);
        response.put("count", assets.size());
        response.put("data", assets);

        return ResponseEntity.ok(response);
    }

    // PUT - Actualizar asset (solo metadatos)
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar asset", description = "Actualiza los metadatos de un asset (sin cambiar el archivo)")
    public ResponseEntity<Map<String, Object>> updateAsset(
            @PathVariable Long id,
            @RequestBody AssetDTO assetDTO) {

        AssetDTO updatedAsset = assetService.updateAsset(id, assetDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Asset actualizado exitosamente");
        response.put("data", updatedAsset);

        return ResponseEntity.ok(response);
    }

    // PUT - Actualizar asset con nuevo archivo
    @PutMapping(value = "/{id}/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Actualizar asset con archivo", description = "Actualiza un asset incluyendo un nuevo archivo")
    public ResponseEntity<Map<String, Object>> updateAssetWithFile(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "ovaId", required = false) String ovaId,
            @RequestParam(value = "ovaName", required = false) String ovaName) {

        AssetDTO updatedAsset = assetService.updateAssetWithFile(id, name, description, file, ovaId, ovaName);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Asset y archivo actualizados exitosamente");
        response.put("data", updatedAsset);

        return ResponseEntity.ok(response);
    }

    // DELETE - Eliminar asset
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar asset", description = "Elimina un asset y su archivo asociado")
    public ResponseEntity<Map<String, Object>> deleteAsset(@PathVariable Long id) {
        assetService.deleteAsset(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Asset eliminado exitosamente");

        return ResponseEntity.ok(response);
    }

    // GET - Descargar archivo
    @GetMapping("/download/{fileName:.+}")
    @Operation(summary = "Descargar archivo", description = "Descarga el archivo de un asset")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String fileName,
            HttpServletRequest request) {

        Resource resource = fileStorageService.loadFileAsResource(fileName);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            contentType = "application/octet-stream";
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}