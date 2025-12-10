package com.onebyone.kindergarten.global.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Slf4j
public class LoggerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        request.setAttribute("UUID", uuid);

        log.debug("============================================ start ============================================");
        log.debug("[{}] request Uri : {}", uuid, request.getRequestURI());
        log.debug("[{}] method : {}", uuid, request.getMethod());
        log.debug("[{}] userAgent : {}", uuid, request.getHeader("User-Agent"));

        String query = request.getQueryString();
        if (query != null) {
            log.debug("[{}] query : {}", uuid, query);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            log.debug("[{}] email : {}", uuid, auth.getName());
        }

        log.debug("[{}] Client IP : {}", uuid, request.getRemoteAddr());
        request.setAttribute("startTime", System.currentTimeMillis());

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.debug("[{}] execute time : {} ms", request.getAttribute("UUID"), (System.currentTimeMillis() - (Long) request.getAttribute("startTime")));
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.debug("============================================ End ============================================");
    }
}
