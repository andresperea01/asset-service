package com.example.assetservice;

import com.example.assetservice.config.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        FileStorageProperties.class
})
public class AssetServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssetServiceApplication.class, args);

        System.out.println("\n=========================================");
        System.out.println("🚀 ASSET-SERVICE INICIADO CORRECTAMENTE");
        System.out.println("=========================================");
        System.out.println("📄 Swagger UI: http://localhost:8080/swagger-ui.html");
        System.out.println("💾 H2 Console: http://localhost:8080/h2-console");
        System.out.println("📡 API Docs: http://localhost:8080/api-docs");
        System.out.println("📤 Upload Page: http://localhost:8080/upload.html");
        System.out.println("=========================================\n");
    }
}