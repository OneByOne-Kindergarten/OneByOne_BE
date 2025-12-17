package com.onebyone.kindergarten.global.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
public class LoggerInterceptor implements HandlerInterceptor {
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    String uuid = UUID.randomUUID().toString().substring(0, 8);

    MDC.put("uuid", uuid);

    log.debug(
        "============================================ start ============================================");
    log.debug("request Uri : {}", request.getRequestURI());
    log.debug("method : {}", request.getMethod());
    log.debug("userAgent : {}", request.getHeader("User-Agent"));

    String query = request.getQueryString();
    if (query != null) {
      log.debug("query : {}", query);
    }

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
      log.debug("userId : {}", auth.getName());
    }

    log.debug("Client IP : {}", request.getRemoteAddr());
    request.setAttribute("startTime", System.currentTimeMillis());

    return true;
  }

  @Override
  public void postHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      ModelAndView modelAndView)
      throws Exception {
    log.debug(
        "execute time : {} ms",
        (System.currentTimeMillis() - (Long) request.getAttribute("startTime")));
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
      throws Exception {
    log.debug(
        "============================================ End ============================================");
    MDC.clear();
  }
}
