package com.enterprise.airbnb.controller;

import com.enterprise.airbnb.model.Image;
import com.enterprise.airbnb.model.ImageCategory;
import com.enterprise.airbnb.service.ImageService;
import com.enterprise.airbnb.util.JwtRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Image Upload operations
 */
@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = "*")
public class ImageUploadController {

    private static final Logger logger = LoggerFactory.getLogger(ImageUploadController.class);

    @Autowired
    private ImageService imageService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    /**
     * Upload image to AWS S3
     */
    @PostMapping("/upload")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Image> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam Long propertyId,
            @RequestParam(required = false) ImageCategory category,
            @RequestParam(required = false) String description) {
        
        logger.info("Uploading image for property ID: {}", propertyId);
        try {
            Image uploadedImage = imageService.uploadImage(file, propertyId, category, description);
            return ResponseEntity.status(HttpStatus.CREATED).body(uploadedImage);
        } catch (Exception e) {
            logger.error("Error uploading image: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Upload multiple images to AWS S3
     */
    @PostMapping("/upload-multiple")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<List<Image>> uploadMultipleImages(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam Long propertyId,
            @RequestParam(required = false) ImageCategory category,
            @RequestParam(required = false) String description) {
        
        logger.info("Uploading {} images for property ID: {}", files.length, propertyId);
        try {
            List<Image> uploadedImages = imageService.uploadMultipleImages(files, propertyId, category, description);
            return ResponseEntity.status(HttpStatus.CREATED).body(uploadedImages);
        } catch (Exception e) {
            logger.error("Error uploading multiple images: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get all images with pagination
     */
    @GetMapping
    public ResponseEntity<Page<Image>> getAllImages(Pageable pageable) {
        logger.info("Fetching all images with pagination: {}", pageable);
        Page<Image> images = imageService.getAllImages(pageable);
        return ResponseEntity.ok(images);
    }

    /**
     * Get image by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Image> getImageById(@PathVariable Long id) {
        logger.info("Fetching image by ID: {}", id);
        Optional<Image> image = imageService.getImageById(id);
        return image.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get images by property
     */
    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<Image>> getImagesByProperty(@PathVariable Long propertyId) {
        logger.info("Fetching images for property ID: {}", propertyId);
        List<Image> images = imageService.getImagesByPropertyId(propertyId);
        return ResponseEntity.ok(images);
    }

    /**
     * Get images by category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<Image>> getImagesByCategory(@PathVariable ImageCategory category, Pageable pageable) {
        logger.info("Fetching images by category: {}", category);
        Page<Image> images = imageService.getImagesByCategory(category, pageable);
        return ResponseEntity.ok(images);
    }

    /**
     * Get main image for property
     */
    @GetMapping("/property/{propertyId}/main")
    public ResponseEntity<Image> getMainImageForProperty(@PathVariable Long propertyId) {
        logger.info("Fetching main image for property ID: {}", propertyId);
        Optional<Image> mainImage = imageService.getMainImageForProperty(propertyId);
        return mainImage.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Set main image for property
     */
    @PatchMapping("/{id}/set-main")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Void> setMainImageForProperty(@PathVariable Long id) {
        logger.info("Setting main image for property with image ID: {}", id);
        try {
            imageService.setMainImageForProperty(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error setting main image: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update image
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Image> updateImage(@PathVariable Long id, @RequestBody Image image) {
        logger.info("Updating image with ID: {}", id);
        try {
            image.setId(id);
            Image updatedImage = imageService.updateImage(image);
            return ResponseEntity.ok(updatedImage);
        } catch (Exception e) {
            logger.error("Error updating image: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete image
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        logger.info("Deleting image with ID: {}", id);
        try {
            imageService.deleteImage(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting image: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete image from AWS S3
     */
    @DeleteMapping("/s3/{id}")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteImageFromS3(@PathVariable Long id) {
        logger.info("Deleting image from S3 with ID: {}", id);
        try {
            imageService.deleteImageFromS3(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting image from S3: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get image URL from S3
     */
    @GetMapping("/{id}/url")
    public ResponseEntity<String> getImageUrl(@PathVariable Long id) {
        logger.info("Getting image URL for ID: {}", id);
        try {
            String imageUrl = imageService.getImageUrl(id);
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            logger.error("Error getting image URL: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get signed URL for image upload
     */
    @GetMapping("/signed-url")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<String> getSignedUrlForUpload(
            @RequestParam String fileName,
            @RequestParam String contentType) {
        
        logger.info("Getting signed URL for upload: {}", fileName);
        try {
            String signedUrl = imageService.getSignedUrlForUpload(fileName, contentType);
            return ResponseEntity.ok(signedUrl);
        } catch (Exception e) {
            logger.error("Error getting signed URL: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Resize image
     */
    @PostMapping("/{id}/resize")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Image> resizeImage(
            @PathVariable Long id,
            @RequestParam Integer width,
            @RequestParam Integer height) {
        
        logger.info("Resizing image ID: {} to {}x{}", id, width, height);
        try {
            Image resizedImage = imageService.resizeImage(id, width, height);
            return ResponseEntity.ok(resizedImage);
        } catch (Exception e) {
            logger.error("Error resizing image: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Generate thumbnail
     */
    @PostMapping("/{id}/thumbnail")
    @PreAuthorize("hasRole('HOST') or hasRole('SUPER_HOST') or hasRole('ADMIN')")
    public ResponseEntity<Image> generateThumbnail(@PathVariable Long id) {
        logger.info("Generating thumbnail for image ID: {}", id);
        try {
            Image thumbnail = imageService.generateThumbnail(id);
            return ResponseEntity.ok(thumbnail);
        } catch (Exception e) {
            logger.error("Error generating thumbnail: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get image metadata
     */
    @GetMapping("/{id}/metadata")
    public ResponseEntity<Object> getImageMetadata(@PathVariable Long id) {
        logger.info("Getting metadata for image ID: {}", id);
        try {
            Object metadata = imageService.getImageMetadata(id);
            return ResponseEntity.ok(metadata);
        } catch (Exception e) {
            logger.error("Error getting image metadata: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Search images by description
     */
    @GetMapping("/search")
    public ResponseEntity<Page<Image>> searchImages(@RequestParam String description, Pageable pageable) {
        logger.info("Searching images by description: {}", description);
        Page<Image> images = imageService.searchImagesByDescription(description, pageable);
        return ResponseEntity.ok(images);
    }

    /**
     * Get images by upload date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<Page<Image>> getImagesByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate,
            Pageable pageable) {
        
        logger.info("Fetching images by date range: {} to {}", startDate, endDate);
        Page<Image> images = imageService.getImagesByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(images);
    }

    /**
     * Get image statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Object> getImageStatistics() {
        logger.info("Fetching image statistics");
        Object statistics = imageService.getImageStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get storage usage
     */
    @GetMapping("/storage-usage")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> getStorageUsage() {
        logger.info("Fetching storage usage");
        Object storageUsage = imageService.getStorageUsage();
        return ResponseEntity.ok(storageUsage);
    }

    /**
     * Clean up orphaned images
     */
    @PostMapping("/cleanup-orphaned")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cleanupOrphanedImages() {
        logger.info("Cleaning up orphaned images");
        try {
            imageService.cleanupOrphanedImages();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error cleaning up orphaned images: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Optimize images
     */
    @PostMapping("/optimize")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> optimizeImages() {
        logger.info("Optimizing images");
        try {
            imageService.optimizeImages();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error optimizing images: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get image processing queue
     */
    @GetMapping("/processing-queue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Object>> getImageProcessingQueue() {
        logger.info("Fetching image processing queue");
        List<Object> queue = imageService.getImageProcessingQueue();
        return ResponseEntity.ok(queue);
    }

    /**
     * Retry failed image processing
     */
    @PostMapping("/{id}/retry-processing")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> retryImageProcessing(@PathVariable Long id) {
        logger.info("Retrying image processing for ID: {}", id);
        try {
            imageService.retryImageProcessing(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error retrying image processing: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get image formats
     */
    @GetMapping("/formats")
    public ResponseEntity<List<String>> getSupportedImageFormats() {
        logger.info("Fetching supported image formats");
        List<String> formats = imageService.getSupportedImageFormats();
        return ResponseEntity.ok(formats);
    }

    /**
     * Validate image file
     */
    @PostMapping("/validate")
    public ResponseEntity<Object> validateImageFile(@RequestParam("file") MultipartFile file) {
        logger.info("Validating image file: {}", file.getOriginalFilename());
        try {
            Object validation = imageService.validateImageFilePublic(file);
            return ResponseEntity.ok(validation);
        } catch (Exception e) {
            logger.error("Error validating image file: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
