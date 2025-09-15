package com.enterprise.airbnb.audit;

import org.hibernate.envers.configuration.EnversSettings;
import org.hibernate.envers.strategy.ValidityAuditStrategy;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Configuration for Hibernate Envers auditing
 */
@Configuration
public class HibernateEnversConfig {
    
    /**
     * Customize Hibernate properties for Envers
     */
    @Bean
    public HibernatePropertiesCustomizer hibernateEnversCustomizer() {
        return new HibernatePropertiesCustomizer() {
            @Override
            public void customize(Map<String, Object> hibernateProperties) {
                // Enable Envers
                hibernateProperties.put(EnversSettings.AUDIT_TABLE_SUFFIX, "_audit");
                hibernateProperties.put(EnversSettings.REVISION_FIELD_NAME, "rev");
                hibernateProperties.put(EnversSettings.REVISION_TYPE_FIELD_NAME, "revtype");
                
                // Store data at delete
                hibernateProperties.put(EnversSettings.STORE_DATA_AT_DELETE, true);
                
                // Use validity audit strategy for better performance
                hibernateProperties.put(EnversSettings.AUDIT_STRATEGY, ValidityAuditStrategy.class.getName());
                
                // Enable audit for queries (deprecated in newer versions)
                // hibernateProperties.put(EnversSettings.AUDIT_QUERY_SUFFIX, "_audit");
                
                // Set default schema and catalog
                hibernateProperties.put(EnversSettings.DEFAULT_SCHEMA, "public");
                hibernateProperties.put(EnversSettings.DEFAULT_CATALOG, "public");
                
                // Enable revision on collection change
                hibernateProperties.put(EnversSettings.REVISION_ON_COLLECTION_CHANGE, true);
                
                // Track entity names
                hibernateProperties.put(EnversSettings.TRACK_ENTITIES_CHANGED_IN_REVISION, true);
            }
        };
    }
}
