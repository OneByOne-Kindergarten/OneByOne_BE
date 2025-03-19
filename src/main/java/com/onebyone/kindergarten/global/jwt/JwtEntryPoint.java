package com.onebyone.kindergarten.global.jwt;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        // 유효한 자격증명이 아닌 경우에는 401 에러 반환
        log.error("Unauthorized Error : {}", authException.getMessage());
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        try {
            String jsonResponse = new JSONObject()
                    .put("message", "인증에 실패했습니다")
                    .toString();
            response.getWriter().write(jsonResponse);
        } catch (org.json.JSONException e) {
            response.getWriter().write("알 수 없는 에러");
        }
    }
}