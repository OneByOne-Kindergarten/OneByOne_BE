package com.onebyone.kindergarten.global.batch.job;

import com.onebyone.kindergarten.domain.communityPosts.service.CommunityService;
import com.onebyone.kindergarten.global.exception.BusinessException;
import com.onebyone.kindergarten.global.exception.ErrorCodes;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TopPostsCacheRefreshJob extends QuartzJobBean {

  private final CommunityService communityService;

  @Override
  protected void executeInternal(JobExecutionContext context) {
    log.info("===== 인기 게시글 캐시 갱신 Job 실행 시작: {} =====", LocalDateTime.now());

    try {
      /// 캐시 갱신
      communityService.refreshTopPostsCache();

      /// 데이터 로드
      communityService.getTopPosts();

      log.info("인기 게시글 캐시가 성공적으로 갱신되었습니다.");
    } catch (Exception e) {
      throw new BusinessException(ErrorCodes.FAILED_TOP_POST_CACHE_EXCEPTION);
    }

    log.info("===== 인기 게시글 캐시 갱신 Job 실행 완료: {} =====", LocalDateTime.now());
  }
}
