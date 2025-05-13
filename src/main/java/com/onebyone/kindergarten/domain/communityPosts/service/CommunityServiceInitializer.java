package com.onebyone.kindergarten.domain.communityPosts.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommunityServiceInitializer implements CommandLineRunner {

    private final CommunityService communityService;

    @Override
    public void run(String... args) {
        communityService.getTopPosts();
        log.info("서버 시작 >> 인기 게시글 캐시 초기화가 완료되었습니다.");
    }
} 