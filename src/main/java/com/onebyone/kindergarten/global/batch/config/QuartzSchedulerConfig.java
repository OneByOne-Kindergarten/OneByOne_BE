//package com.onebyone.kindergarten.global.batch.config;
//
//import com.onebyone.kindergarten.global.batch.job.InstantJob;
//import org.quartz.*;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class QuartzSchedulerConfig {
//
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
//}
