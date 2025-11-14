package com.example.assetservice.repository;

import com.example.assetservice.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    List<Asset> findByCategory(String category);

    List<Asset> findByNameContainingIgnoreCase(String name);

    List<Asset> findByFileType(String fileType);
}