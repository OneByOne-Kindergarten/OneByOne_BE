package com.onebyone.kindergarten.global.jwt;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        // 자격정보는 있으나 접근 권한이 없는 경우 403 에러 반환
        log.error("Forbidden Error : {}", accessDeniedException.getMessage());
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        try {
            String jsonResponse = new JSONObject()
                    .put("message", accessDeniedException.getMessage())
                    .toString();
            response.getWriter().write(jsonResponse);
        } catch (org.json.JSONException e) {
            log.error("Error while creating JSON response", e);
            response.getWriter().write("{\"message\": \"Unknown error occurred\"}");
        }
    }
}
