package com.bys.sample.quartz.service;

import static java.time.ZoneId.systemDefault;
import static org.quartz.CronExpression.isValidExpression;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.springframework.util.StringUtils.isEmpty;

import java.time.format.DateTimeFormatter;
import java.util.*;

import com.bys.sample.quartz.bean.BackupJobDetailRequestBean;
import com.bys.sample.quartz.bean.SchedulerResponseBean;
import com.bys.sample.quartz.dto.ScheduleDTO;
import com.bys.sample.quartz.job.RestAPIJob;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Service
public class SchedulerService {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private SchedulerJobRegisterService schedulerJobRegisterService;

    /**
     * Create job scheduler response bean.
     *
     * @param jobGroup             the job group
     * @param body the job detail request bean
     * @return the scheduler response bean
     * @throws ClassNotFoundException
     */
    public SchedulerResponseBean createJob(String jobGroup, @RequestBody ScheduleDTO.CreateScheduleBody body) throws ClassNotFoundException {
        SchedulerResponseBean responseBean = new SchedulerResponseBean();
        log.info("bys:SchedulerService:Scheduler" + scheduler);

        body.setJobGroup(jobGroup);

        JobDetail jobDetail = schedulerJobRegisterService.buildJobDetail(body);
        log.info("bys:SchedulerService:backupJobDetailRequestBean.buildJobDetail()" + jobDetail.getKey());

        Trigger trigger = buildTrigger(body);
        log.info("bys:SchedulerService:backupJobDetailRequestBean.buildTrigger()" + trigger.getKey());

        Set<Trigger> triggers = new HashSet<Trigger>();
        triggers.add(trigger);
        try {
            scheduler.scheduleJob(jobDetail, triggers, false);

            log.info("Job with key - {} saved sucessfully", jobDetail.getKey());
            responseBean.setResult(ScheduleDTO.CreateScheduleBody.class);
            responseBean.setResultCode(HttpStatus.CREATED);
        } catch (SchedulerException e) {
            e.printStackTrace();
            log.error(
                    "Could not save job with key - {} due to error - {}",
                    jobDetail.getKey(),
                    e.getLocalizedMessage());
            throw new IllegalArgumentException(e.getLocalizedMessage());
        }
        return responseBean;
    }

    /**
     * Find job scheduler response bean.
     *
     * @param jobGroup the job group
     * @param jobName  the job name
     * @return the scheduler response bean
     */
    public SchedulerResponseBean findJob(String jobGroup, String jobName) {
        SchedulerResponseBean responseBean = new SchedulerResponseBean();
        try {
            JobDetail jobDetail = scheduler.getJobDetail(jobKey(jobName, jobGroup));
            if (Objects.nonNull(jobDetail)) {
                Trigger trigger = scheduler.getTriggersOfJob(jobKey(jobName, jobGroup)).get(0);
                TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                responseBean.setResult(
                        Optional.of(
                                BackupJobDetailRequestBean.buildJobDetail(
                                        jobDetail,trigger, triggerState)));
            }

            responseBean.setResultCode(HttpStatus.OK);

        } catch (SchedulerException e) {
            String errorMsg =
                    String.format(
                            "Could not find job with key - %s.%s  due to error -  %s",
                            jobGroup, jobName, e.getLocalizedMessage());
            log.error(errorMsg);
            responseBean.setResultCode(HttpStatus.INTERNAL_SERVER_ERROR);
            responseBean.setResult(errorMsg);
        }
        return responseBean;
    }

    /**
     * List all job scheduler response bean.
     *
     * @return the scheduler response bean
     */
    public SchedulerResponseBean allJobs(String jobGroup) {
        SchedulerResponseBean responseBean = new SchedulerResponseBean();
        try {
//        	GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
            GroupMatcher<JobKey> matcher = GroupMatcher.groupEquals(jobGroup);
            List<BackupJobDetailRequestBean> jobs = new ArrayList<BackupJobDetailRequestBean>();
            scheduler.getJobKeys(matcher).forEach((jobKey)->{
                JobDetail jobDetail;
                try {
                    jobDetail = scheduler.getJobDetail(jobKey);
                    Trigger trigger = scheduler.getTriggersOfJob(jobKey).get(0);
                    TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());

                    jobs.add(BackupJobDetailRequestBean.buildJobDetail(jobDetail, trigger, triggerState));
                } catch (SchedulerException e) {
                    String errorMsg =
                            String.format(
                                    "Could not any job,  due to error -  %s", e.getLocalizedMessage());
                    log.error(errorMsg);
                }

            });
            responseBean.setResult(jobs);
            responseBean.setResultCode(HttpStatus.OK);

        } catch (SchedulerException e) {
            String errorMsg =
                    String.format(
                            "Could not any job,  due to error -  %s", e.getLocalizedMessage());
            log.error(errorMsg);
            responseBean.setResultCode(HttpStatus.INTERNAL_SERVER_ERROR);
            responseBean.setResult(errorMsg);
        }
        return responseBean;
    }

    /**
     * Update job scheduler response bean.
     *
     * @param jobGroup             the job group
     * @param jobName              the job name
     * @param backupJobDetailRequestBean the job detail request bean
     * @return the scheduler response bean
     */
    public SchedulerResponseBean updateJob(
            String jobGroup, String jobName, BackupJobDetailRequestBean backupJobDetailRequestBean) {
        SchedulerResponseBean responseBean = new SchedulerResponseBean();
        try {
            JobDetail oldJobDetail = scheduler.getJobDetail(jobKey(jobName, jobGroup));
            if (Objects.nonNull(oldJobDetail)) {
                JobDataMap jobDataMap = oldJobDetail.getJobDataMap();
                jobDataMap.put("orgCode", backupJobDetailRequestBean.getOrgCode());
                jobDataMap.put("jobType", backupJobDetailRequestBean.getJobType());
                jobDataMap.put("uniqueKey", backupJobDetailRequestBean.getUniqueKey());
                jobDataMap.put("data", backupJobDetailRequestBean.getData());
                JobBuilder jb = oldJobDetail.getJobBuilder();
                JobDetail newJobDetail = jb.usingJobData(jobDataMap).storeDurably().build();
                scheduler.addJob(newJobDetail, true);
                log.info("Updated job with key - {}", newJobDetail.getKey());
                responseBean.setResult(backupJobDetailRequestBean);
                responseBean.setResultCode(HttpStatus.CREATED);
            }
            log.warn("Could not find job with key - {}.{} to update", jobGroup, jobName);
        } catch (SchedulerException e) {
            String errorMsg =
                    String.format(
                            "Could not find job with key - %s.%s to update due to error -  %s",
                            jobGroup, jobName, e.getLocalizedMessage());
            log.error(errorMsg);
            responseBean.setResultCode(HttpStatus.INTERNAL_SERVER_ERROR);
            responseBean.setResult(errorMsg);
        }
        return responseBean;
    }

    /**
     * Delete job scheduler response bean.
     *
     * @param jobGroup the job group
     * @param jobName  the job name
     * @return the scheduler response bean
     */
    public SchedulerResponseBean deleteJob(String jobGroup, String jobName) {
        SchedulerResponseBean responseBean = new SchedulerResponseBean();
        try {
            scheduler.deleteJob(jobKey(jobName, jobGroup));
            String msg = "Deleted job with key - " + jobGroup + "." + jobName;
            responseBean.setResult(msg);
            responseBean.setResultCode(HttpStatus.OK);
            log.info(msg);
        } catch (SchedulerException e) {
            String errorMsg =
                    String.format(
                            "Could not find job with key - %s.%s to Delete due to error -  %s",
                            jobGroup, jobName, e.getLocalizedMessage());
            log.error(errorMsg);
            responseBean.setResultCode(HttpStatus.INTERNAL_SERVER_ERROR);
            responseBean.setResult(errorMsg);
        }
        return responseBean;
    }

    /**
     * Pause job scheduler response bean.
     *
     * @param jobGroup the job group
     * @param jobName  the job name
     * @return the scheduler response bean
     */
    public SchedulerResponseBean pauseJob(String jobGroup, String jobName) {
        SchedulerResponseBean responseBean = new SchedulerResponseBean();
        try {
            scheduler.pauseJob(jobKey(jobName, jobGroup));
            String msg = "Paused job with key - " + jobGroup + "." + jobName;
            responseBean.setResult(msg);
            responseBean.setResultCode(HttpStatus.OK);
        } catch (SchedulerException e) {
            String errorMsg =
                    String.format(
                            "Could not find job with key - %s.%s  due to error -  %s",
                            jobGroup, jobName, e.getLocalizedMessage());
            log.error(errorMsg);
            responseBean.setResultCode(HttpStatus.INTERNAL_SERVER_ERROR);
            responseBean.setResult(errorMsg);
        }
        return responseBean;
    }

    /**
     * Resume job scheduler response bean.
     *
     * @param jobGroup the job group
     * @param jobName  the job name
     * @return the scheduler response bean
     */
    public SchedulerResponseBean resumeJob(String jobGroup, String jobName) {
        SchedulerResponseBean responseBean = new SchedulerResponseBean();
        try {
            scheduler.resumeJob(jobKey(jobName, jobGroup));
            String msg = "Resumed job with key - " + jobGroup + "." + jobName;
            responseBean.setResult(msg);
            responseBean.setResultCode(HttpStatus.OK);
        } catch (SchedulerException e) {
            String errorMsg =
                    String.format(
                            "Could not find job with key - %s.%s  due to error -  %s",
                            jobGroup, jobName, e.getLocalizedMessage());
            log.error(errorMsg);
            responseBean.setResultCode(HttpStatus.INTERNAL_SERVER_ERROR);
            responseBean.setResult(errorMsg);
        }
        return responseBean;
    }


    public Trigger buildTrigger(ScheduleDTO.CreateScheduleBody body) {
        if (!isEmpty(body.getCron())) {
            if (!isValidExpression(body.getCron()))
                throw new IllegalArgumentException("Provided expression " + body.getCron() + " is not a valid body.getCron() expression");
            Trigger trigger =  newTrigger()
                    .withIdentity(body.getJobName(), body.getJobGroup())
                    .withSchedule(
                            cronSchedule(body.getCron())
//                                    .withMisfireHandlingInstructionFireAndProceed()
                                    .withMisfireHandlingInstructionDoNothing()
                                    .inTimeZone(TimeZone.getTimeZone(systemDefault())))
                    .usingJobData("cron", body.getCron())
                    .build();
            trigger.getJobDataMap().remove("jobDetail");
            return trigger;
        }
//        else if (!isEmpty(body.getFireTime())) {
//            JobDataMap jobDataMap = new JobDataMap();
//            jobDataMap.put("fireTime", body.getFireTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//            jobDataMap.put("interval", body.getRepeatInterval() + "");
//            Trigger trigger =  newTrigger()
//                    .withIdentity(body.getJobName(), body.getJobGroup())
//                    .withSchedule(
//                            simpleSchedule()
//                                    .withMisfireHandlingInstructionNextWithExistingCount()
////                    		.withMisfireHandlingInstructionNowWithExistingCount()
//                                    .withIntervalInMilliseconds(body.getRepeatInterval())
//                                    .repeatForever()
//                    )
//                    .startAt(Date.from(body.getFireTime().atZone(systemDefault()).toInstant()))
//                    .usingJobData(jobDataMap)
//                    .build();
//            trigger.getJobDataMap().remove("jobDetail");
//            return trigger;
//        }
        throw new IllegalStateException("unsupported trigger details " + this);
    }


}
