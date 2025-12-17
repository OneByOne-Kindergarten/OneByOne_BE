package com.onebyone.kindergarten.global.aop;

import com.onebyone.kindergarten.global.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect // AOP 명시
@Component
@Slf4j
public class ControllerExceptionLoggingAspect {
  // 메서드 실행 전 ~ 종료까지 제어
  @Around("execution(* com.onebyone.kindergarten.domain..*Controller.*(..))" // *Controller 밑 전체
  )
  public Object around(ProceedingJoinPoint pjp) throws Throwable {
    try {
      return pjp.proceed();
    } catch (Exception e) {

      if (e instanceof BusinessException be) {
        log.error("BusinessException 발생 에러코드: {} 메시지: {}", be.getMessage(), be.getErrorCode());
      } else {
        log.error("Exception 발생");
      }

      for (Object arg : pjp.getArgs()) {
        if (isLoggable(arg)) {
          log.error("RequestBody: {}", arg);
        }
      }

      throw e;
    }
  }

  private boolean isLoggable(Object arg) {
    return arg != null
        && !(arg instanceof jakarta.servlet.http.HttpServletRequest)
        && !(arg instanceof jakarta.servlet.http.HttpServletResponse)
        && !(arg instanceof org.springframework.validation.BindingResult)
        && !(arg instanceof org.springframework.web.multipart.MultipartFile);
  }
}
