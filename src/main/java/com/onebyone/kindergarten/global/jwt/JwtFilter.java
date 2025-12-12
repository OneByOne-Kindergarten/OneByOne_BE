package com.onebyone.kindergarten.global.jwt;

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
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  private final JwtProvider jwtProvider;

  //    private final RedisService redisService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
      filterChain.doFilter(request, response);
      return;
    }

    String jwt = resolveToken(request);

    if (StringUtils.hasText(jwt)) {
      Map<String, Object> map = jwtProvider.validateTokenWithError(jwt);

      if ((boolean) map.get("isValid")) {
        Map<String, Object> authenticationMap = jwtProvider.getAuthentication(jwt);

        if ((boolean) authenticationMap.get("isValid")) {
          Authentication authentication = (Authentication) authenticationMap.get("authentication");
          SecurityContextHolder.getContext().setAuthentication(authentication);
          filterChain.doFilter(request, response);
        } else {
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          response.setContentType("application/json;charset=UTF-8");

          ErrorResponse errorResponse =
              ErrorResponse.buildError((ErrorCodes) authenticationMap.get("errorCode"));
          response
              .getWriter()
              .write(
                  "{\"code\": \""
                      + errorResponse.getCode()
                      + "\", \"message\": \""
                      + errorResponse.getMessage()
                      + "\"}");
        }
      } else {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        ErrorResponse errorResponse = ErrorResponse.buildError((ErrorCodes) map.get("errorCode"));
        response
            .getWriter()
            .write(
                "{\"code\": \""
                    + errorResponse.getCode()
                    + "\", \"message\": \""
                    + errorResponse.getMessage()
                    + "\"}");
      }

      //            TODO: BlackList에 존재하는 토큰으로 요청이 온 경우.
      //            Optional<String> isBlackList = redisService.getBlackList(jwt);
      //            isBlackList.ifPresent(t -> {
      //                throw new RuntimeException("이미 로그아웃된 토큰입니다.");
      //            });
    } else {
      filterChain.doFilter(request, response);
    }
  }

  public String resolveToken(HttpServletRequest request) {
    String authorization = request.getHeader("Authorization");

    if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
      return authorization.substring(7);
    }
    return null;
  }
}
