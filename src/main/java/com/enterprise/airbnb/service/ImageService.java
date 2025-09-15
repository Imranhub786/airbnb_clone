package com.enterprise.airbnb.service;

import com.enterprise.airbnb.model.Image;
import com.enterprise.airbnb.model.ImageCategory;
import com.enterprise.airbnb.repository.ImageRepository;
import com.enterprise.airbnb.repository.PropertyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * Service for Image operations with AWS S3 integration
 */
@Service
@Transactional
public class ImageService {

    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    /**
     * Upload image to AWS S3
     */
    @CacheEvict(value = {"images", "properties"}, allEntries = true)
    public Image uploadImage(MultipartFile file, Long propertyId, ImageCategory category, String description) {
        logger.info("Uploading image for property ID: {}", propertyId);
        
        try {
            // Validate file
            validateImageFile(file);
            
            // Upload to S3
            String s3Url = uploadToS3(file);
            
            // Create image entity
            Image image = new Image();
            image.setProperty(propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new RuntimeException("Property not found")));
            image.setImageUrl(s3Url);
            image.setCategory(category != null ? category : ImageCategory.PROPERTY);
            image.setDescription(description);
            image.setFileName(file.getOriginalFilename());
            image.setFileSize(file.getSize());
            image.setContentType(file.getContentType());
            image.setIsMain(false);
            
            return imageRepository.save(image);
        } catch (Exception e) {
            logger.error("Error uploading image: {}", e.getMessage());
            throw new RuntimeException("Image upload failed");
        }
    }

    /**
     * Upload multiple images to AWS S3
     */
    @CacheEvict(value = {"images", "properties"}, allEntries = true)
    public List<Image> uploadMultipleImages(MultipartFile[] files, Long propertyId, ImageCategory category, String description) {
        logger.info("Uploading {} images for property ID: {}", files.length, propertyId);
        
        List<Image> uploadedImages = new java.util.ArrayList<>();
        
        for (MultipartFile file : files) {
            try {
                Image image = uploadImage(file, propertyId, category, description);
                uploadedImages.add(image);
            } catch (Exception e) {
                logger.error("Error uploading image {}: {}", file.getOriginalFilename(), e.getMessage());
                // Continue with other files
            }
        }
        
        return uploadedImages;
    }

    /**
     * Upload file to AWS S3
     */
    private String uploadToS3(MultipartFile file) {
        logger.info("Uploading file to S3: {}", file.getOriginalFilename());
        
        try {
            // Implement AWS S3 upload logic
            // This is a placeholder - actual implementation would use AWS SDK
            
            // Simulate S3 upload
            Thread.sleep(1000); // Simulate network delay
            
            // Generate S3 URL
            String s3Url = "https://airbnb-clone-images.s3.amazonaws.com/" + 
                          System.currentTimeMillis() + "_" + file.getOriginalFilename();
            
            return s3Url;
            
        } catch (Exception e) {
            logger.error("S3 upload failed: {}", e.getMessage());
            throw new RuntimeException("S3 upload failed");
        }
    }

    /**
     * Validate image file
     */
    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        
        if (file.getSize() > 10 * 1024 * 1024) { // 10MB limit
            throw new RuntimeException("File size exceeds 10MB limit");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("File is not an image");
        }
        
        // Check supported formats
        List<String> supportedFormats = getSupportedImageFormats();
        boolean isSupported = supportedFormats.stream()
                .anyMatch(format -> contentType.contains(format));
        
        if (!isSupported) {
            throw new RuntimeException("Unsupported image format");
        }
    }

    /**
     * Get all images with pagination
     */
    @Cacheable(value = "images", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Image> getAllImages(Pageable pageable) {
        logger.info("Fetching all images with pagination: {}", pageable);
        return imageRepository.findAll(pageable);
    }

    /**
     * Get image by ID
     */
    @Cacheable(value = "images", key = "#id")
    public Optional<Image> getImageById(Long id) {
        logger.info("Fetching image by ID: {}", id);
        return imageRepository.findById(id);
    }

    /**
     * Get images by property
     */
    @Cacheable(value = "images", key = "'property_' + #propertyId")
    public List<Image> getImagesByPropertyId(Long propertyId) {
        logger.info("Fetching images for property ID: {}", propertyId);
        return imageRepository.findByPropertyId(propertyId);
    }

    /**
     * Get images by category
     */
    @Cacheable(value = "images", key = "'category_' + #category + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Image> getImagesByCategory(ImageCategory category, Pageable pageable) {
        logger.info("Fetching images by category: {}", category);
        return imageRepository.findByCategory(category, pageable);
    }

    /**
     * Get main image for property
     */
    @Cacheable(value = "images", key = "'main_' + #propertyId")
    public Optional<Image> getMainImageForProperty(Long propertyId) {
        logger.info("Fetching main image for property ID: {}", propertyId);
        return imageRepository.findMainImageByPropertyId(propertyId);
    }

    /**
     * Set main image for property
     */
    @CacheEvict(value = {"images", "properties"}, allEntries = true)
    public void setMainImageForProperty(Long id) {
        logger.info("Setting main image for property with image ID: {}", id);
        
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        
        // Remove main flag from other images of the same property
        imageRepository.removeMainFlagFromPropertyImages(image.getProperty().getId());
        
        // Set this image as main
        image.setIsMain(true);
        imageRepository.save(image);
    }

    /**
     * Update image
     */
    @CacheEvict(value = {"images", "properties"}, allEntries = true)
    public Image updateImage(Image image) {
        logger.info("Updating image with ID: {}", image.getId());
        return imageRepository.save(image);
    }

    /**
     * Delete image
     */
    @CacheEvict(value = {"images", "properties"}, allEntries = true)
    public void deleteImage(Long id) {
        logger.info("Deleting image with ID: {}", id);
        imageRepository.deleteById(id);
    }

    /**
     * Delete image from AWS S3
     */
    @CacheEvict(value = {"images", "properties"}, allEntries = true)
    public void deleteImageFromS3(Long id) {
        logger.info("Deleting image from S3 with ID: {}", id);
        
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        
        try {
            // Delete from S3
            deleteFromS3(image.getImageUrl());
            
            // Delete from database
            imageRepository.deleteById(id);
            
        } catch (Exception e) {
            logger.error("Error deleting image from S3: {}", e.getMessage());
            throw new RuntimeException("S3 deletion failed");
        }
    }

    /**
     * Delete file from AWS S3
     */
    private void deleteFromS3(String s3Url) {
        logger.info("Deleting file from S3: {}", s3Url);
        
        try {
            // Implement AWS S3 delete logic
            // This is a placeholder - actual implementation would use AWS SDK
            
            // Simulate S3 delete
            Thread.sleep(500); // Simulate network delay
            
        } catch (Exception e) {
            logger.error("S3 delete failed: {}", e.getMessage());
            throw new RuntimeException("S3 delete failed");
        }
    }

    /**
     * Get image URL from S3
     */
    @Cacheable(value = "images", key = "'url_' + #id")
    public String getImageUrl(Long id) {
        logger.info("Getting image URL for ID: {}", id);
        
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        
        return image.getImageUrl();
    }

    /**
     * Get signed URL for image upload
     */
    public String getSignedUrlForUpload(String fileName, String contentType) {
        logger.info("Getting signed URL for upload: {}", fileName);
        
        try {
            // Implement AWS S3 signed URL generation
            // This is a placeholder - actual implementation would use AWS SDK
            
            // Simulate signed URL generation
            Thread.sleep(500); // Simulate network delay
            
            String signedUrl = "https://airbnb-clone-images.s3.amazonaws.com/" + 
                              System.currentTimeMillis() + "_" + fileName + 
                              "?AWSAccessKeyId=...&Signature=...&Expires=...";
            
            return signedUrl;
            
        } catch (Exception e) {
            logger.error("Error generating signed URL: {}", e.getMessage());
            throw new RuntimeException("Signed URL generation failed");
        }
    }

    /**
     * Resize image
     */
    @CacheEvict(value = {"images", "properties"}, allEntries = true)
    public Image resizeImage(Long id, Integer width, Integer height) {
        logger.info("Resizing image ID: {} to {}x{}", id, width, height);
        
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        
        try {
            // Implement image resizing logic
            // This is a placeholder - actual implementation would use image processing library
            
            // Simulate image resizing
            Thread.sleep(1000); // Simulate processing time
            
            // Update image metadata
            image.setWidth(width);
            image.setHeight(height);
            
            return imageRepository.save(image);
            
        } catch (Exception e) {
            logger.error("Error resizing image: {}", e.getMessage());
            throw new RuntimeException("Image resizing failed");
        }
    }

    /**
     * Generate thumbnail
     */
    @CacheEvict(value = {"images", "properties"}, allEntries = true)
    public Image generateThumbnail(Long id) {
        logger.info("Generating thumbnail for image ID: {}", id);
        
        Image originalImage = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        
        try {
            // Implement thumbnail generation logic
            // This is a placeholder - actual implementation would use image processing library
            
            // Simulate thumbnail generation
            Thread.sleep(1000); // Simulate processing time
            
            // Create thumbnail image
            Image thumbnail = new Image();
            thumbnail.setProperty(originalImage.getProperty());
            thumbnail.setCategory(ImageCategory.THUMBNAIL);
            thumbnail.setDescription("Thumbnail of " + originalImage.getDescription());
            thumbnail.setFileName("thumb_" + originalImage.getFileName());
            thumbnail.setFileSize(originalImage.getFileSize() / 4); // Assume 1/4 size
            thumbnail.setContentType(originalImage.getContentType());
            thumbnail.setWidth(150);
            thumbnail.setHeight(150);
            thumbnail.setIsMain(false);
            
            // Generate thumbnail URL
            String thumbnailUrl = originalImage.getImageUrl().replace(".jpg", "_thumb.jpg");
            thumbnail.setImageUrl(thumbnailUrl);
            
            return imageRepository.save(thumbnail);
            
        } catch (Exception e) {
            logger.error("Error generating thumbnail: {}", e.getMessage());
            throw new RuntimeException("Thumbnail generation failed");
        }
    }

    /**
     * Get image metadata
     */
    @Cacheable(value = "images", key = "'metadata_' + #id")
    public Object getImageMetadata(Long id) {
        logger.info("Getting metadata for image ID: {}", id);
        
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        
        // Return metadata object
        return new Object() {
            public final Long id = image.getId();
            public final String fileName = image.getFileName();
            public final Long fileSize = image.getFileSize();
            public final String contentType = image.getContentType();
            public final Integer width = image.getWidth();
            public final Integer height = image.getHeight();
            public final String imageUrl = image.getImageUrl();
            public final ImageCategory category = image.getCategory();
            public final String description = image.getDescription();
            public final Boolean isMain = image.getIsMain();
        };
    }

    /**
     * Search images by description
     */
    @Cacheable(value = "images", key = "'search_' + #description + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Image> searchImagesByDescription(String description, Pageable pageable) {
        logger.info("Searching images by description: {}", description);
        return imageRepository.findByDescriptionContainingIgnoreCase(description, pageable);
    }

    /**
     * Get images by date range
     */
    @Cacheable(value = "images", key = "'dateRange_' + #startDate + '_' + #endDate + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Image> getImagesByDateRange(String startDate, String endDate, Pageable pageable) {
        logger.info("Fetching images by date range: {} to {}", startDate, endDate);
        return imageRepository.findByDateRange(startDate, endDate, pageable);
    }

    /**
     * Get image statistics
     */
    @Cacheable(value = "images", key = "'statistics'")
    public Object getImageStatistics() {
        logger.info("Fetching image statistics");
        return imageRepository.getImageStatistics();
    }

    /**
     * Get storage usage
     */
    @Cacheable(value = "images", key = "'storageUsage'")
    public Object getStorageUsage() {
        logger.info("Fetching storage usage");
        return imageRepository.getStorageUsage();
    }

    /**
     * Clean up orphaned images
     */
    @CacheEvict(value = {"images", "properties"}, allEntries = true)
    public void cleanupOrphanedImages() {
        logger.info("Cleaning up orphaned images");
        imageRepository.cleanupOrphanedImages();
    }

    /**
     * Optimize images
     */
    @CacheEvict(value = {"images", "properties"}, allEntries = true)
    public void optimizeImages() {
        logger.info("Optimizing images");
        // Implement image optimization logic
    }

    /**
     * Get image processing queue
     */
    @Cacheable(value = "images", key = "'processingQueue'")
    public List<Object> getImageProcessingQueue() {
        logger.info("Fetching image processing queue");
        return imageRepository.getImageProcessingQueue();
    }

    /**
     * Retry failed image processing
     */
    @CacheEvict(value = {"images", "properties"}, allEntries = true)
    public void retryImageProcessing(Long id) {
        logger.info("Retrying image processing for ID: {}", id);
        // Implement retry logic
    }

    /**
     * Get supported image formats
     */
    @Cacheable(value = "images", key = "'supportedFormats'")
    public List<String> getSupportedImageFormats() {
        logger.info("Fetching supported image formats");
        return List.of("jpeg", "jpg", "png", "gif", "webp");
    }

    /**
     * Validate image file and return validation result
     */
    public Object validateImageFilePublic(MultipartFile file) {
        logger.info("Validating image file: {}", file.getOriginalFilename());
        
        try {
            validateImageFile(file);
            
            return new Object() {
                public final boolean valid = true;
                public final String message = "File is valid";
                public final String fileName = file.getOriginalFilename();
                public final Long fileSize = file.getSize();
                public final String contentType = file.getContentType();
            };
            
        } catch (Exception e) {
            return new Object() {
                public final boolean valid = false;
                public final String message = e.getMessage();
                public final String fileName = file.getOriginalFilename();
                public final Long fileSize = file.getSize();
                public final String contentType = file.getContentType();
            };
        }
    }

    /**
     * Get images by property ID
     */
    public List<Image> getImagesByProperty(Long propertyId) {
        logger.debug("Fetching images by property ID: {}", propertyId);
        return getImagesByPropertyId(propertyId);
    }

    /**
     * Get images by user ID
     */
    public List<Image> getImagesByUser(Long userId) {
        logger.debug("Fetching images by user ID: {}", userId);
        return getImagesByUserId(userId);
    }
    
    /**
     * Get images by user ID
     */
    public List<Image> getImagesByUserId(Long userId) {
        logger.debug("Fetching images by user ID: {}", userId);
        return imageRepository.findByUserId(userId);
    }

    /**
     * Update image with alt text and caption
     */
    public Image updateImage(Long id, String altText, String caption) {
        logger.info("Updating image ID: {} with alt text and caption", id);
        Image image = getImageById(id)
            .orElseThrow(() -> new RuntimeException("Image not found"));
        
        image.setAltText(altText);
        image.setCaption(caption);
        return updateImage(image);
    }

    /**
     * Set main image
     */
    public Image setMainImage(Long id) {
        logger.info("Setting main image ID: {}", id);
        Image image = getImageById(id)
            .orElseThrow(() -> new RuntimeException("Image not found"));
        
        setMainImageForProperty(id);
        return image;
    }
}
