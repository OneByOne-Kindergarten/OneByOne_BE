package com.onebyone.kindergarten.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/// 푸시 알림과 같은 비동기 처리가 필요한 작업을 위한 스레드 풀 구성
@Configuration
@EnableAsync
public class AsyncConfig {

    /// 비동기 작업을 위한 스레드 풀 설정
    /// - corePoolSize: 기본적으로 유지하는 스레드 (5개)
    /// - maxPoolSize: 최대 스레드 수 (10개)
    /// - queueCapacity: 작업 큐 크기 (25개)
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("PushNotification-");
        executor.initialize();
        return executor;
    }
} 