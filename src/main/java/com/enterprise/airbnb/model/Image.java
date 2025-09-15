package com.enterprise.airbnb.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Image entity for property and user images
 */
@Entity
@Table(name = "images")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class Image {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Image URL is required")
    @Size(max = 500)
    @Column(name = "image_url", nullable = false)
    private String imageUrl;
    
    @NotBlank(message = "Image name is required")
    @Size(max = 255)
    @Column(name = "image_name", nullable = false)
    private String imageName;
    
    @Size(max = 100)
    @Column(name = "image_type")
    private String imageType;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Size(max = 20)
    @Column(name = "file_extension")
    private String fileExtension;
    
    @Column(name = "width")
    private Integer width;
    
    @Column(name = "height")
    private Integer height;
    
    @Size(max = 1000)
    @Column(name = "alt_text")
    private String altText;
    
    @Size(max = 1000)
    @Column(name = "caption")
    private String caption;
    
    @Column(name = "is_main_image")
    private Boolean isMainImage = false;
    
    @Column(name = "display_order")
    private Integer displayOrder = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "image_category", nullable = false)
    private ImageCategory category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    private Property property;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;
    
    @Column(name = "is_verified")
    private Boolean isVerified = false;
    
    @Column(name = "is_public")
    private Boolean isPublic = true;
    
    @Column(name = "upload_source")
    private String uploadSource = "WEB";
    
    @Column(name = "storage_provider")
    private String storageProvider = "AWS_S3";
    
    @Column(name = "storage_bucket")
    private String storageBucket;
    
    @Column(name = "storage_key")
    private String storageKey;
    
    @Column(name = "cdn_url")
    private String cdnUrl;
    
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
    
    @Column(name = "medium_url")
    private String mediumUrl;
    
    @Column(name = "large_url")
    private String largeUrl;
    
    @Column(name = "original_url")
    private String originalUrl;
    
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @Column(name = "deleted_by")
    private String deletedBy;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public Image() {}
    
    public Image(String imageUrl, String imageName, ImageCategory category) {
        this.imageUrl = imageUrl;
        this.imageName = imageName;
        this.category = category;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }
    
    public String getImageType() { return imageType; }
    public void setImageType(String imageType) { this.imageType = imageType; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public String getFileExtension() { return fileExtension; }
    public void setFileExtension(String fileExtension) { this.fileExtension = fileExtension; }
    
    public Integer getWidth() { return width; }
    public void setWidth(Integer width) { this.width = width; }
    
    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }
    
    public String getAltText() { return altText; }
    public void setAltText(String altText) { this.altText = altText; }
    
    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }
    
    public Boolean getIsMainImage() { return isMainImage; }
    public void setIsMainImage(Boolean isMainImage) { this.isMainImage = isMainImage; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    
    public ImageCategory getCategory() { return category; }
    public void setCategory(ImageCategory category) { this.category = category; }
    
    public Property getProperty() { return property; }
    public void setProperty(Property property) { this.property = property; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }
    
    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }
    
    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
    
    public String getUploadSource() { return uploadSource; }
    public void setUploadSource(String uploadSource) { this.uploadSource = uploadSource; }
    
    public String getStorageProvider() { return storageProvider; }
    public void setStorageProvider(String storageProvider) { this.storageProvider = storageProvider; }
    
    public String getStorageBucket() { return storageBucket; }
    public void setStorageBucket(String storageBucket) { this.storageBucket = storageBucket; }
    
    public String getStorageKey() { return storageKey; }
    public void setStorageKey(String storageKey) { this.storageKey = storageKey; }
    
    public String getCdnUrl() { return cdnUrl; }
    public void setCdnUrl(String cdnUrl) { this.cdnUrl = cdnUrl; }
    
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    
    public String getMediumUrl() { return mediumUrl; }
    public void setMediumUrl(String mediumUrl) { this.mediumUrl = mediumUrl; }
    
    public String getLargeUrl() { return largeUrl; }
    public void setLargeUrl(String largeUrl) { this.largeUrl = largeUrl; }
    
    public String getOriginalUrl() { return originalUrl; }
    public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }
    
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
    
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    
    public String getDeletedBy() { return deletedBy; }
    public void setDeletedBy(String deletedBy) { this.deletedBy = deletedBy; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Utility methods
    public boolean isDeleted() {
        return isDeleted != null && isDeleted;
    }
    
    public boolean isMainImage() {
        return isMainImage != null && isMainImage;
    }
    
    public boolean isVerified() {
        return isVerified != null && isVerified;
    }
    
    public boolean isPublic() {
        return isPublic != null && isPublic;
    }
    
    public String getDisplayUrl() {
        if (cdnUrl != null && !cdnUrl.isEmpty()) {
            return cdnUrl;
        }
        return imageUrl;
    }
    
    public String getBestAvailableUrl() {
        if (largeUrl != null && !largeUrl.isEmpty()) {
            return largeUrl;
        }
        if (mediumUrl != null && !mediumUrl.isEmpty()) {
            return mediumUrl;
        }
        return getDisplayUrl();
    }
    
    public String getThumbnailDisplayUrl() {
        if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
            return thumbnailUrl;
        }
        return getDisplayUrl();
    }
    
    public void markAsDeleted(String deletedBy) {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }
    
    // Missing methods for compilation
    public void setDescription(String description) {
        this.caption = description;
    }
    
    public String getDescription() {
        return this.caption;
    }
    
    public void setFileName(String fileName) {
        this.imageName = fileName;
    }
    
    public String getFileName() {
        return this.imageName;
    }
    
    public void setContentType(String contentType) {
        this.imageType = contentType;
    }
    
    public String getContentType() {
        return this.imageType;
    }
    
    public void setIsMain(boolean isMain) {
        this.isMainImage = isMain;
    }
    
    public boolean getIsMain() {
        return this.isMainImage != null && this.isMainImage;
    }
}


