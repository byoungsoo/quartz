package com.bys.sample.quartz.bean;

import static org.quartz.JobBuilder.newJob;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import com.bys.sample.quartz.job.RestAPIJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class BackupJobDetailRequestBean implements Serializable {

    @NotEmpty
    private String group;

    @NotBlank
    private String name;

    @NotEmpty
    private String orgCode;

    @NotEmpty
    private String jobType;

    @NotEmpty
    private String uniqueKey;

    private Map<String, Object> data = new LinkedHashMap<>();

    private TriggerDetailsRequestBean triggerDetailsRequestBean;


    public static BackupJobDetailRequestBean buildJobDetail(JobDetail jobDetail, Trigger trigger) {
        return buildJobDetail(jobDetail, trigger, null);
    }

    public static BackupJobDetailRequestBean buildJobDetail(JobDetail jobDetail, Trigger trigger, TriggerState TriggerState) {
//        List<TriggerDetailsRequestBean> triggerDetailsRequestBeanList = triggersOfJob.stream()
//                .map(TriggerDetailsRequestBean::buildTriggerDetails)
//                .collect(Collectors.toList());

        TriggerDetailsRequestBean triggerDetail = TriggerDetailsRequestBean.buildTriggerDetails(trigger, TriggerState);

        return new BackupJobDetailRequestBean()
                .setName(jobDetail.getKey().getName())
                .setGroup(jobDetail.getKey().getGroup())
                .setOrgCode(jobDetail.getJobDataMap().getString("orgCode"))
                .setJobType(jobDetail.getJobDataMap().getString("jobType"))
                .setUniqueKey(jobDetail.getJobDataMap().getString("uniqueKey"))
                .setData((Map<String, Object>) jobDetail.getJobDataMap().get("data"))
                .setTriggerDetail(triggerDetail);
    }

    public BackupJobDetailRequestBean setTriggerDetail(final TriggerDetailsRequestBean triggerDetail) {
        this.triggerDetailsRequestBean = triggerDetail;
        return this;
    }

    public BackupJobDetailRequestBean setData(final Map<String, Object> data) {
        this.data = data;
        return this;
    }

    public BackupJobDetailRequestBean setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
        return this;
    }

    public BackupJobDetailRequestBean setJobType(String jobType) {
        this.jobType = jobType;
        return this;
    }

    public BackupJobDetailRequestBean setOrgCode(String orgCode) {
        this.orgCode = orgCode;
        return this;
    }

    public BackupJobDetailRequestBean setGroup(final String group) {
        this.group = group;
        return this;
    }

    public BackupJobDetailRequestBean setName(final String name) {
        this.name = name;
        return this;
    }

    public JobDetail buildJobDetail() throws ClassNotFoundException {
        JobDataMap jobDataMap = new JobDataMap(getData());
        jobDataMap.put("orgCode", orgCode);
        jobDataMap.put("jobType", jobType);
        jobDataMap.put("uniqueKey", uniqueKey);

        Class<RestAPIJob> clazz = RestAPIJob.class;
        return newJob(clazz)
                .withIdentity(getName(), getGroup())
                .usingJobData(jobDataMap)
                .build();
    }

    @JsonIgnore
    public Trigger buildTrigger() {
        return triggerDetailsRequestBean.buildTrigger();
    }
}

