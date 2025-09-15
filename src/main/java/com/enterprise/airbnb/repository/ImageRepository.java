package com.enterprise.airbnb.repository;

import com.enterprise.airbnb.model.Image;
import com.enterprise.airbnb.model.ImageCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Image operations
 */
@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    // Find images by property
    List<Image> findByPropertyId(Long propertyId);

    // Find images by category
    Page<Image> findByCategory(ImageCategory category, Pageable pageable);

    // Find main image for property
    @Query("SELECT i FROM Image i WHERE i.property.id = :propertyId AND i.isMain = true")
    Optional<Image> findMainImageByPropertyId(@Param("propertyId") Long propertyId);

    // Find images with photos
    @Query("SELECT i FROM Image i WHERE i.imageUrl IS NOT NULL AND i.imageUrl != ''")
    Page<Image> findImagesWithPhotos(Pageable pageable);

    // Find images without photos
    @Query("SELECT i FROM Image i WHERE i.imageUrl IS NULL OR i.imageUrl = ''")
    Page<Image> findImagesWithoutPhotos(Pageable pageable);

    // Search images by description
    @Query("SELECT i FROM Image i WHERE LOWER(i.description) LIKE LOWER(CONCAT('%', :description, '%'))")
    Page<Image> findByDescriptionContainingIgnoreCase(@Param("description") String description, Pageable pageable);

    // Find images by date range
    @Query("SELECT i FROM Image i WHERE DATE(i.createdAt) BETWEEN :startDate AND :endDate")
    Page<Image> findByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate, Pageable pageable);

    // Get image statistics
    @Query("SELECT COUNT(i) as totalImages, COUNT(DISTINCT i.property) as propertiesWithImages FROM Image i")
    Object getImageStatistics();

    // Get storage usage
    @Query("SELECT SUM(i.fileSize) as totalSize, AVG(i.fileSize) as averageSize FROM Image i")
    Object getStorageUsage();

    // Clean up orphaned images
    @Query("DELETE FROM Image i WHERE i.property IS NULL")
    void cleanupOrphanedImages();

    // Get image processing queue
    @Query("SELECT i FROM Image i WHERE i.status = 'PROCESSING' OR i.status = 'FAILED'")
    List<Object> getImageProcessingQueue();

    // Find images by property and category
    List<Image> findByPropertyIdAndCategory(Long propertyId, ImageCategory category);

    // Find images by property and main status
    List<Image> findByPropertyIdAndIsMain(Long propertyId, Boolean isMain);

    // Count images by property
    @Query("SELECT COUNT(i) FROM Image i WHERE i.property.id = :propertyId")
    Long countByPropertyId(@Param("propertyId") Long propertyId);

    // Find recent images
    @Query("SELECT i FROM Image i ORDER BY i.createdAt DESC")
    Page<Image> findRecentImages(Pageable pageable);
    
    // Missing method for compilation
    void removeMainFlagFromPropertyImages(Long propertyId);
}

