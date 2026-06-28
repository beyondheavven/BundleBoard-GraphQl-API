package com.source.bundleboard.refreshtoken.job;

import com.source.bundleboard.refreshtoken.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenCleanJob implements Job {

    private final RefreshTokenService refreshTokenService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            refreshTokenService.deleteAllExpired().block();
        }catch (Exception e){
            throw new JobExecutionException(e);
        }
    }
}
