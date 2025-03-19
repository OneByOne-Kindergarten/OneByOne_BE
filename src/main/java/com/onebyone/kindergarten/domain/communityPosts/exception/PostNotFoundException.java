package com.onebyone.kindergarten.domain.communityPosts.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)  // 404 상태 코드 반환
public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(Long id) {
        super(String.format("게시글(ID: %d)을 찾을 수 없습니다.", id));
    }
}
