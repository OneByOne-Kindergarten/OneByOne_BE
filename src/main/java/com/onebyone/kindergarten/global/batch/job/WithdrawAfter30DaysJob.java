package com.onebyone.kindergarten.global.batch.job;

import com.onebyone.kindergarten.domain.user.service.UserService;
import com.onebyone.kindergarten.global.exception.BusinessException;
import com.onebyone.kindergarten.global.exception.ErrorCodes;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WithdrawAfter30DaysJob extends QuartzJobBean {
  private final UserService userService;

  private static Logger log = LoggerFactory.getLogger(WithdrawAfter30DaysJob.class);

  @Override
  protected void executeInternal(JobExecutionContext context){
    try {
      log.info("===== 탈퇴 Job 실행 시작: {} =====", LocalDateTime.now());

      LocalDateTime minus30Days = LocalDateTime.now().minusDays(30);
      userService.withdrawAfter30Days(minus30Days);
    } catch (Exception e) {
      log.error("탈퇴 Job 실행 중 오류 발생: {}", e.getMessage(), e);
      throw new BusinessException(ErrorCodes.FAILED_WITHDRAW_EXCEPTION);
    }

    log.info("===== 탈퇴 Job 실행 종료: {} =====", LocalDateTime.now());
  }
}
