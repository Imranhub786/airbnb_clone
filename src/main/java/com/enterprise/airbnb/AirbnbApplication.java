package com.enterprise.airbnb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Airbnb Clone - Vacation Rental Platform
 * 
 * Features:
 * - Property search and booking
 * - Host admin panels
 * - Review system
 * - Multi-currency payments with PayPal
 * - Image uploads with AWS S3
 * - Internationalization (i18n)
 * - GraphQL API
 * - Caching with Caffeine and Redis
 * - Auditing and logging
 * 
 * @author Enterprise Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
public class AirbnbApplication {

    public static void main(String[] args) {
        SpringApplication.run(AirbnbApplication.class, args);
    }
}
