package com.bys.sample.quartzworker.config;

import javax.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class QuartzConfig {

    private final SchedulerFactoryBean schedulerFactory;

    @PostConstruct
    public void scheduled() throws SchedulerException {
        Scheduler scheduler = this.schedulerFactory.getScheduler();
        scheduler.start();
        log.info("@@Worker@@:getSchedulerName: " + scheduler.getSchedulerName());
    }
}
