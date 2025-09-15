package com.enterprise.airbnb.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Arrays;
import java.util.Collection;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Caching configuration for Caffeine and Redis
 * Implements multi-level caching strategy for optimal performance
 */
@Configuration
@EnableCaching
public class CachingConfig {
    
    // Cache names
    public static final String PROPERTIES_CACHE = "properties";
    public static final String PROPERTY_SEARCH_CACHE = "propertySearch";
    public static final String USER_CACHE = "users";
    public static final String BOOKING_CACHE = "bookings";
    public static final String REVIEW_CACHE = "reviews";
    public static final String AVAILABILITY_CACHE = "availability";
    public static final String PAYMENT_CACHE = "payments";
    public static final String EXCHANGE_RATES_CACHE = "exchangeRates";
    public static final String STATS_CACHE = "stats";
    
    /**
     * Primary cache manager using Caffeine for local caching
     */
    @Bean
    @Primary
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeineCacheBuilder());
        cacheManager.setCacheNames(getCacheNames());
        return cacheManager;
    }
    
    /**
     * Redis cache manager for distributed caching
     */
    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // Property caches - longer TTL for relatively static data
        cacheConfigurations.put(PROPERTIES_CACHE, defaultConfig.entryTtl(Duration.ofHours(2)));
        cacheConfigurations.put(PROPERTY_SEARCH_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(30)));
        
        // User cache - medium TTL
        cacheConfigurations.put(USER_CACHE, defaultConfig.entryTtl(Duration.ofHours(1)));
        
        // Booking and payment caches - shorter TTL for frequently changing data
        cacheConfigurations.put(BOOKING_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(15)));
        cacheConfigurations.put(PAYMENT_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(10)));
        
        // Review cache - longer TTL for relatively static data
        cacheConfigurations.put(REVIEW_CACHE, defaultConfig.entryTtl(Duration.ofHours(4)));
        
        // Availability cache - very short TTL for real-time data
        cacheConfigurations.put(AVAILABILITY_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(5)));
        
        // Exchange rates cache - longer TTL for external API data
        cacheConfigurations.put(EXCHANGE_RATES_CACHE, defaultConfig.entryTtl(Duration.ofHours(6)));
        
        // Stats cache - medium TTL for aggregated data
        cacheConfigurations.put(STATS_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(30)));
        
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
    
    /**
     * Caffeine cache builder with optimized settings
     */
    private Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(Duration.ofHours(1))
                .expireAfterAccess(Duration.ofMinutes(30))
                .recordStats();
    }
    
    /**
     * Get all cache names
     */
    private Collection<String> getCacheNames() {
        return Arrays.asList(
                PROPERTIES_CACHE,
                PROPERTY_SEARCH_CACHE,
                USER_CACHE,
                BOOKING_CACHE,
                REVIEW_CACHE,
                AVAILABILITY_CACHE,
                PAYMENT_CACHE,
                EXCHANGE_RATES_CACHE,
                STATS_CACHE
        );
    }
}

