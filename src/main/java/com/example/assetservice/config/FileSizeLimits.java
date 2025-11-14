package com.example.assetservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "file.max-size")
public class FileSizeLimits {

    private String pdf = "50MB";
    private String image = "10MB";
    private String video = "100MB";

    public long getPdfBytes() {
        return parseSize(pdf);
    }

    public long getImageBytes() {
        return parseSize(image);
    }

    public long getVideoBytes() {
        return parseSize(video);
    }

    public Map<String, Long> getAllLimits() {
        Map<String, Long> limits = new HashMap<>();
        limits.put("PDF", getPdfBytes());
        limits.put("IMAGE", getImageBytes());
        limits.put("VIDEO", getVideoBytes());
        return limits;
    }

    private long parseSize(String size) {
        size = size.toUpperCase().trim();
        if (size.endsWith("MB")) {
            return Long.parseLong(size.replace("MB", "").trim()) * 1024 * 1024;
        } else if (size.endsWith("GB")) {
            return Long.parseLong(size.replace("GB", "").trim()) * 1024 * 1024 * 1024;
        } else if (size.endsWith("KB")) {
            return Long.parseLong(size.replace("KB", "").trim()) * 1024;
        }
        return Long.parseLong(size);
    }

    // Getters y Setters
    public String getPdf() { return pdf; }
    public void setPdf(String pdf) { this.pdf = pdf; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getVideo() { return video; }
    public void setVideo(String video) { this.video = video; }
}