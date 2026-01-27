package com.onebyone.kindergarten.global.jwt;

import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.enums.UserStatus;
import com.onebyone.kindergarten.domain.user.repository.UserRepository;
import com.onebyone.kindergarten.global.exception.BusinessException;
import com.onebyone.kindergarten.global.exception.ErrorCodes;
import com.onebyone.kindergarten.global.exception.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  private final JwtProvider jwtProvider;
  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    if (isPreflight(request)) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = resolveToken(request);
    if (!StringUtils.hasText(token)) {
      filterChain.doFilter(request, response);
      return;
    }

    Map<String, Object> validation = jwtProvider.validateTokenWithError(token);
    if (!isValid(validation)) {
      sendError(response, (ErrorCodes) validation.get("errorCode"));
      return;
    }

    Map<String, Object> authResult = jwtProvider.getAuthentication(token);
    if (!isValid(authResult)) {
      sendError(response, (ErrorCodes) authResult.get("errorCode"));
      return;
    }

    Authentication authentication = (Authentication) authResult.get("authentication");
    Long userId = extractUserId(authentication);

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_USER));

    if (user.getStatus() != UserStatus.ACTIVE) {
      sendError(response, ErrorCodes.SUSPENDED_USER_EXCEPTION);
      return;
    }

    SecurityContextHolder.getContext().setAuthentication(authentication);
    filterChain.doFilter(request, response);
  }

  private boolean isPreflight(HttpServletRequest request) {
    return "OPTIONS".equalsIgnoreCase(request.getMethod());
  }

  private boolean isValid(Map<String, Object> result) {
    return Boolean.TRUE.equals(result.get("isValid"));
  }

  private Long extractUserId(Authentication authentication) {
    return Long.valueOf(((UserDetails) authentication.getPrincipal()).getUsername());
  }

  private void sendError(HttpServletResponse response, ErrorCodes errorCode) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json;charset=UTF-8");

    ErrorResponse errorResponse = ErrorResponse.buildError(errorCode);
    response
        .getWriter()
        .write(
            "{\"code\":\""
                + errorResponse.getCode()
                + "\",\"message\":\""
                + errorResponse.getMessage()
                + "\"}");
  }

  private String resolveToken(HttpServletRequest request) {
    String authorization = request.getHeader("Authorization");
    if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
      return authorization.substring(7);
    }
    return null;
  }
}
