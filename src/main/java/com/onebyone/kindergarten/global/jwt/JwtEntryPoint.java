package com.onebyone.kindergarten.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onebyone.kindergarten.global.exception.ErrorCodes;
import com.onebyone.kindergarten.global.exception.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper om;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            ErrorResponse body = ErrorResponse.buildError(ErrorCodes.INVALID_TOKEN_EXPIRED);
            om.writeValue(response.getWriter(), body);
        } catch (IOException e) {

        }
    }
}