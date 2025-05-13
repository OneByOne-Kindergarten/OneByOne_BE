package com.onebyone.kindergarten.global.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    /// TOP10 게시물 캐시
    public static final String TOP_POSTS_CACHE = "topPostsCache";

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(TOP_POSTS_CACHE);
    }
} 