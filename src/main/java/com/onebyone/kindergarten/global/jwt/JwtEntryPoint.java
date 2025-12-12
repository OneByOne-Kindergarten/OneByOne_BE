package com.onebyone.kindergarten.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onebyone.kindergarten.global.exception.ErrorCodes;
import com.onebyone.kindergarten.global.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtEntryPoint implements AuthenticationEntryPoint {
  private final ObjectMapper om;

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    try {
      ErrorResponse body = ErrorResponse.buildError(ErrorCodes.FAILED_AUTHORIZATION_EXCEPTION);
      om.writeValue(response.getWriter(), body);
    } catch (Exception e) {
      ErrorResponse body = ErrorResponse.buildError(ErrorCodes.INTERNAL_SERVER_ERROR);
      om.writeValue(response.getWriter(), body);
    }
  }
}
