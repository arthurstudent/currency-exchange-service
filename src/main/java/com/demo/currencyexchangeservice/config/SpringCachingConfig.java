package com.demo.currencyexchangeservice.config;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@NoArgsConstructor
public class SpringCachingConfig {

    @Value("${data.cache.name}")
    private String cacheName;

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(cacheName);
    }
}
