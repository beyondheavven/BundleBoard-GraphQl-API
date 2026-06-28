package com.source.bundleboard.config;

import com.source.bundleboard.refreshtoken.job.RefreshTokenCleanJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail refreshTokenCleanJobDetail() {
        return JobBuilder.newJob(RefreshTokenCleanJob.class)
                .withIdentity("Refresh Token clean job")
                .withDescription("Delete expired refresh token from db")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger refreshTokenCleanJobTrigger(JobDetail refreshTokenCleanJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(refreshTokenCleanJobDetail)
                .withIdentity("Refresh Token clean job trigger")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInHours(1)
                        .repeatForever())
                .build();
    }
}
