package com.bys.sample.quartz.bean;

import static java.time.ZoneId.systemDefault;
import static java.util.UUID.randomUUID;
import static org.quartz.CronExpression.isValidExpression;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.springframework.util.StringUtils.isEmpty;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

import javax.validation.constraints.NotBlank;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;

import lombok.Data;

@Slf4j
@Data
public class TriggerDetailsRequestBean implements Serializable {

    @NotBlank
    private String name;
    private String group;
    private LocalDateTime fireTime;
    private long repeatInterval;
    private String cron;


    private Date lastFireTime;
    private Date nextFireTime;
    private String status;

    /**
     * Build trigger details trigger details request bean.
     *
     * @param trigger the trigger
     * @return the trigger details request bean
     */
    public static TriggerDetailsRequestBean buildTriggerDetails(Trigger trigger) {
        return TriggerDetailsRequestBean.buildTriggerDetails(trigger, null);
    }

    public static TriggerDetailsRequestBean buildTriggerDetails(Trigger trigger, TriggerState triggerState) {
        TriggerDetailsRequestBean triggerDetailsRequestBean =  new TriggerDetailsRequestBean()
                .setName(trigger.getKey().getName())
                .setGroup(trigger.getKey().getGroup())
                .setLastFireTime(trigger.getPreviousFireTime())
                .setNextFireTime(trigger.getNextFireTime())
                .setCron(trigger.getJobDataMap().getString("cron"));

        if (trigger.getJobDataMap().get("fireTime") != null){
            triggerDetailsRequestBean.setFireTime(LocalDateTime.parse((String)trigger.getJobDataMap().get("fireTime"),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (trigger.getJobDataMap().getString("interval") != null){
            triggerDetailsRequestBean.setRepeatInterval(Long.parseLong(trigger.getJobDataMap().getString("interval")));
        }
        if ( triggerState != null) {
            triggerDetailsRequestBean.setStatus(triggerState.name());
        }

        return triggerDetailsRequestBean;
    }

    /**
     * Sets lastFireTime.
     *
     * @param lastFireTime the lastFireTime
     * @return the cron
     */
    public TriggerDetailsRequestBean setLastFireTime(final Date lastFireTime) {
        this.lastFireTime = lastFireTime;
        return this;
    }

    /**
     * Sets nextFireTime.
     *
     * @param nextFireTime the nextFireTime
     * @return the cron
     */
    public TriggerDetailsRequestBean setNextFireTime(final  Date nextFireTime) {
        this.nextFireTime = nextFireTime;
        return this;
    }

    /**
     * Sets status.
     *
     * @param status the status
     * @return the status
     */
    public TriggerDetailsRequestBean setStatus(final String status) {
        this.status = status;
        return this;
    }

    /**
     * Sets interval.
     *
     * @param cron the cron
     * @return the cron
     */
    public TriggerDetailsRequestBean setRepeatInterval(final long interval) {
        this.repeatInterval = interval;
        return this;
    }

    /**
     * Sets cron.
     *
     * @param cron the cron
     * @return the cron
     */
    public TriggerDetailsRequestBean setCron(final String cron) {
        this.cron = cron;
        return this;
    }

    /**
     * Sets fire time.
     *
     * @param fireTime the fire time
     * @return the fire time
     */
    public TriggerDetailsRequestBean setFireTime(final LocalDateTime fireTime) {
        this.fireTime = fireTime;
        return this;
    }

    /**
     * Sets group.
     *
     * @param group the group
     * @return the group
     */
    public TriggerDetailsRequestBean setGroup(final String group) {
        this.group = group;
        return this;
    }

    /**
     * Sets name.
     *
     * @param name the name
     * @return the name
     */
    public TriggerDetailsRequestBean setName(final String name) {
        this.name = name;
        return this;
    }

    /**
     * Build trigger trigger.
     *
     * @return the trigger
     */
    public Trigger buildTrigger() {
        log.info("bys:buildTrigger():cron: " + cron);
        if (!isEmpty(cron)) {
            if (!isValidExpression(cron))
                throw new IllegalArgumentException("Provided expression " + cron + " is not a valid cron expression");
            Trigger trigger =  newTrigger()
                    .withIdentity(buildName(), group)
                    .withSchedule(
                            cronSchedule(cron)
//                                    .withMisfireHandlingInstructionFireAndProceed()
                                    .withMisfireHandlingInstructionDoNothing()
                                    .inTimeZone(TimeZone.getTimeZone(systemDefault())))
                    .usingJobData("cron", cron)
                    .build();
            trigger.getJobDataMap().remove("jobDetail");
            return trigger;
        } else if (!isEmpty(fireTime)) {
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("fireTime", fireTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            jobDataMap.put("interval", repeatInterval + "");
            Trigger trigger =  newTrigger()
                    .withIdentity(buildName(), group)
                    .withSchedule(
                            simpleSchedule()
                                    .withMisfireHandlingInstructionNextWithExistingCount()
//                    		.withMisfireHandlingInstructionNowWithExistingCount()
                                    .withIntervalInMilliseconds(repeatInterval)
                                    .repeatForever()
                    )
                    .startAt(Date.from(fireTime.atZone(systemDefault()).toInstant()))
                    .usingJobData(jobDataMap)
                    .build();
            trigger.getJobDataMap().remove("jobDetail");
            return trigger;
        }
        throw new IllegalStateException("unsupported trigger details " + this);
    }

    private String buildName() {
        return isEmpty(name) ? randomUUID().toString() : name;
    }
}

