package com.coingecko.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        
        // Configurar caches específicos
        cacheManager.setCacheNames(java.util.Arrays.asList(
            "cryptoData",
            "marketData",
            "coinGeckoApi"
        ));
        
        // Permitir criação dinâmica de caches
        cacheManager.setAllowNullValues(false);
        
        return cacheManager;
    }
}
