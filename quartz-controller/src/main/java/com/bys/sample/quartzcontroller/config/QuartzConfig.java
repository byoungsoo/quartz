package com.bys.sample.quartzcontroller.config;

import java.util.Collections;
import javax.annotation.PostConstruct;

import com.bys.sample.quartzcontroller.job.HelloJob;
import lombok.RequiredArgsConstructor;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
@RequiredArgsConstructor
public class QuartzConfig {

    private final SchedulerFactoryBean schedulerFactory;

    @PostConstruct
    public void scheduled() throws SchedulerException {
        JobDataMap map1 = new JobDataMap(Collections.singletonMap("num", "3"));
        JobDataMap map2 = new JobDataMap(Collections.singletonMap("num", "4"));
        JobDetail job1 = this.jobDetail("hello1", "hello-group", map1);
        JobDetail job2 = this.jobDetail("hello2", "hello-group", map2);
        SimpleTrigger trigger1 = this.trigger("trigger1", "trigger-group");
        SimpleTrigger trigger2 = this.trigger("trigger2", "trigger-group");
        this.schedulerFactory.getObject().scheduleJob(job1, trigger1);
        this.schedulerFactory.getObject().scheduleJob(job2, trigger2);
    }

    public JobDetail jobDetail(String name, String group, JobDataMap dataMap) {
        JobDetail job = JobBuilder.newJob(HelloJob.class).withIdentity(name, group).withDescription("simple hello job").usingJobData(dataMap).build();
        return job;
    }

    public SimpleTrigger trigger(String name, String group) {
        SimpleTrigger trigger = (SimpleTrigger)TriggerBuilder.newTrigger().withIdentity(name, group).withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(10)).withDescription("hello my trigger").build();
        return trigger;
    }

}
