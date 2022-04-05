package com.bys.sample.quartzcontroller.job;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(HelloJob.class);

    public HelloJob() {
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap map = context.getJobDetail().getJobDataMap();
        log.info("#####Execute##### job-detail-key:{}, fired-time:{}, num:{}", new Object[]{context.getJobDetail().getKey(), context.getFireTime(), map.getInt("num")});
    }
}
