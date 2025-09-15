package com.enterprise.airbnb.audit;

import jakarta.persistence.*;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import java.time.LocalDateTime;

/**
 * Custom audit trail entity for tracking data changes
 */
@Entity
@Table(name = "audit_trail")
@RevisionEntity(AuditTrailListener.class)
public class AuditTrail {
    
    @Id
    @RevisionNumber
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @RevisionTimestamp
    @Column(name = "revision_timestamp", nullable = false)
    private LocalDateTime revisionTimestamp;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "user_email")
    private String userEmail;
    
    @Column(name = "entity_name", nullable = false)
    private String entityName;
    
    @Column(name = "entity_id", nullable = false)
    private Long entityId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false)
    private OperationType operationType;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "user_agent")
    private String userAgent;
    
    @Column(name = "session_id")
    private String sessionId;
    
    @Column(name = "additional_info", columnDefinition = "TEXT")
    private String additionalInfo;
    
    // Constructors
    public AuditTrail() {}
    
    public AuditTrail(String entityName, Long entityId, OperationType operationType) {
        this.entityName = entityName;
        this.entityId = entityId;
        this.operationType = operationType;
        this.revisionTimestamp = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDateTime getRevisionTimestamp() {
        return revisionTimestamp;
    }
    
    public void setRevisionTimestamp(LocalDateTime revisionTimestamp) {
        this.revisionTimestamp = revisionTimestamp;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUserEmail() {
        return userEmail;
    }
    
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    
    public String getEntityName() {
        return entityName;
    }
    
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }
    
    public Long getEntityId() {
        return entityId;
    }
    
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
    
    public OperationType getOperationType() {
        return operationType;
    }
    
    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public String getAdditionalInfo() {
        return additionalInfo;
    }
    
    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
    
    @Override
    public String toString() {
        return "AuditTrail{" +
                "id=" + id +
                ", revisionTimestamp=" + revisionTimestamp +
                ", userId=" + userId +
                ", userEmail='" + userEmail + '\'' +
                ", entityName='" + entityName + '\'' +
                ", entityId=" + entityId +
                ", operationType=" + operationType +
                ", ipAddress='" + ipAddress + '\'' +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
    
    /**
     * Operation types for audit trail
     */
    public enum OperationType {
        INSERT,
        UPDATE,
        DELETE,
        SELECT
    }
}

