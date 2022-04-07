package com.bys.sample.quartz.service;

import com.bys.sample.quartz.dto.JobTypeEnum;
import com.bys.sample.quartz.dto.ScheduleDTO;
import com.bys.sample.quartz.job.RestAPIJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.quartz.JobBuilder.newJob;

@Slf4j
@Service
public class SchedulerJobRegisterService {

    @Autowired
    private Scheduler scheduler;

    public JobDetail buildJobDetail(ScheduleDTO.CreateScheduleBody body) throws ClassNotFoundException {
        JobDataMap jobDataMap = new JobDataMap(body.getData());

        jobDataMap.put("domainId", body.getDomainId());
        jobDataMap.put("projectId", body.getProjectId());
        jobDataMap.put("jobType", body.getJobType());
        jobDataMap.put("resourceId", body.getResourceId());

        if (JobTypeEnum.VOLUME_BACKUP.equals(body.getJobType())) {
            Class<RestAPIJob> clazz = RestAPIJob.class;
            return newJob(clazz)
                    .withIdentity(body.getJobName(), body.getJobGroup())
                    .usingJobData(jobDataMap)
                    .build();
        }

        return null;
    }
}



