package com.bys.sample.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@Slf4j
public class RestAPIJob implements Job {

    public RestAPIJob() {
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap map = context.getJobDetail().getJobDataMap();
        log.info("######################### Execute START #########################");
        log.info("Job-Firetime: " + context.getFireTime());
        log.info("Job-JobInstance: " + context.getJobInstance());
        log.info("Job-Organization: " + context.getJobDetail().getJobDataMap().get("orgCode"));
        log.info("Job-Key: " + context.getJobDetail().getKey());
        log.info("Job-Type: " + context.getJobDetail().getJobDataMap().get("jobType"));
        log.info("Job-Unique-Key: " + context.getJobDetail().getJobDataMap().get("uniqueKey"));
        log.info("######################### Execute END #########################");
    }
}