package com.onebyone.kindergarten.global.facade;

import com.onebyone.kindergarten.domain.user.dto.UserDTO;
import com.onebyone.kindergarten.domain.user.enums.UserRole;
import com.onebyone.kindergarten.domain.user.service.UserService;
import com.onebyone.kindergarten.global.exception.BusinessException;
import com.onebyone.kindergarten.global.exception.ErrorCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BatchFacade {
    Logger logger = LoggerFactory.getLogger(BatchFacade.class);

    private final UserService userService;
    private final JobLauncher jobLauncher;
    private final Job parseAddressJob;

    public void kindergartenAddressBatch(String username){
        UserDTO user = userService.getUser(username);

        if (!UserRole.ADMIN.name().equals(user.getRole())) {
            throw new BusinessException(ErrorCodes.BATCH_NOT_ADMIN_CANNOT_USE);
        }

        try{
            JobParameters params = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis()) // 중복 실행 방지
                    .toJobParameters();

            jobLauncher.run(parseAddressJob, params);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new BusinessException(ErrorCodes.INTERNAL_SERVER_ERROR);
        }
    }
}
