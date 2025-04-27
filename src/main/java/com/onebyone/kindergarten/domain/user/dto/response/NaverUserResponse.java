package com.onebyone.kindergarten.domain.user.dto.response;

import lombok.Data;

@Data
public class NaverUserResponse {
    private String resultcode;
    private String message;
    private Response response;

    @Data
    public static class Response {
        private String id;
        private String email;
        private String profile_image;
        private String nickname;
    }
}
