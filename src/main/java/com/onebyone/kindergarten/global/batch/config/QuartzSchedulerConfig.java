package com.onebyone.kindergarten.global.batch.config;

import com.onebyone.kindergarten.global.batch.job.PushNotificationJob;
import com.onebyone.kindergarten.global.batch.job.TopPostsCacheRefreshJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzSchedulerConfig {
  //    // 테스트용 Job
  //    @Bean
  //    public JobDetail instantDetail() {
  //        return JobBuilder.newJob(
  //                InstantJob.class
  //        ).storeDurably()
  //                .build();
  //    }
  //
  //    @Bean
  //    public Trigger instantTrigger() {
  //        return TriggerBuilder.newTrigger()
  //                .forJob(instantDetail())
  //                .startNow()
  //                .withSchedule(instantCronScheduler())
  //                .build();
  //    }
  //
  //    public CronScheduleBuilder instantCronScheduler() {
  //        return CronScheduleBuilder.cronSchedule("0 * * * * ?");
  //    }

  /// 푸시 알림 Job 설정
  @Bean
  public JobDetail pushNotificationJobDetail() {
    return JobBuilder.newJob(PushNotificationJob.class)
        .storeDurably()
        .withIdentity("pushNotificationJob")
        .withDescription("푸시 알림 발송 작업")
        .build();
  }

  /// 푸시 알림 Trigger 설정
  @Bean
  public Trigger pushNotificationTrigger() {
    return TriggerBuilder.newTrigger()
        .forJob(pushNotificationJobDetail())
        .withIdentity("pushNotificationTrigger")
        .withDescription("15분마다 미발송 푸시 알림 발송")
        .startNow()
        .withSchedule(pushNotificationCronScheduler())
        .build();
  }

  /// 20분마다 실행되는 크론 표현식
  public CronScheduleBuilder pushNotificationCronScheduler() {
    return CronScheduleBuilder.cronSchedule("0 0/20 * * * ?");
  }

  /// 인기 게시글 캐시 갱신 Job 설정
  @Bean
  public JobDetail topPostsCacheRefreshJobDetail() {
    return JobBuilder.newJob(TopPostsCacheRefreshJob.class)
        .storeDurably()
        .withIdentity("topPostsCacheRefreshJob")
        .withDescription("인기 게시글 캐시 갱신 작업")
        .build();
  }

  /// 인기 게시글 캐시 갱신 Trigger 설정 (매일 새벽 6시에 실행)
  @Bean
  public Trigger topPostsCacheRefreshTrigger() {
    return TriggerBuilder.newTrigger()
        .forJob(topPostsCacheRefreshJobDetail())
        .withIdentity("topPostsCacheRefreshTrigger")
        .withDescription("매일 새벽 6시에 인기 게시글 캐시 갱신")
        .startNow()
        .withSchedule(topPostsCacheRefreshCronScheduler())
        .build();
  }

  /// 매일 새벽 6시에 실행되는 크론 표현식
  public CronScheduleBuilder topPostsCacheRefreshCronScheduler() {
    return CronScheduleBuilder.cronSchedule("0 0 6 * * ?");
  }
}
