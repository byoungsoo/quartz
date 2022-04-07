package com.bys.sample.quartz.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import io.swagger.annotations.ApiModel;

@Data
public class ScheduleDTO implements Serializable {


//    @ApiModel("BackupScheduleDTO.BackupScheduleParams")
//    public static class AlarmParams extends CommonDTO.defaultParams {
//        public AlarmParams(String domainId, String projectId) {
//            super(domainId, projectId);
//        }
//    }
    @ApiModel("ScheduleJobDTO.CreateScheduleBody")
    public static class CreateScheduleBody {

        private String domainId;
        private String projectId;

        // Job
        private String jobGroup;
        private String jobName;
        private String jobType;
        private String resourceId;

        // Trriger
        private String cron;

//        private long repeatInterval;
//        private Date lastFireTime;
//        private Date nextFireTime;
//        private String status;
//        private LocalDateTime fireTime;


        private Map<String, Object> data = new LinkedHashMap<>();

        public String getDomainId() {
            return domainId;
        }

        public void setDomainId(String domainId) {
            this.domainId = domainId;
        }

        public String getProjectId() {
            return projectId;
        }

        public void setProjectId(String projectId) {
            this.projectId = projectId;
        }

        public String getJobGroup() {
            return jobGroup;
        }

        public void setJobGroup(String jobGroup) {
            this.jobGroup = jobGroup;
        }

        public String getJobName() {
            return jobName;
        }

        public void setJobName(String jobName) {
            this.jobName = jobName;
        }

        public String getJobType() {
            return jobType;
        }

        public void setJobType(String jobType) {
            this.jobType = jobType;
        }

        public String getResourceId() {
            return resourceId;
        }

        public void setResourceId(String resourceId) {
            this.resourceId = resourceId;
        }

        public Map<String, Object> getData() {
            return data;
        }

        public void setData(Map<String, Object> data) {
            this.data = data;
        }

        public String getCron() {
            return cron;
        }

        public void setCron(String cron) {
            this.cron = cron;
        }

//        public long getRepeatInterval() {
//            return repeatInterval;
//        }
//
//        public void setRepeatInterval(long repeatInterval) {
//            this.repeatInterval = repeatInterval;
//        }
//
//        public LocalDateTime getFireTime() {
//                return fireTime;
//            }
//
//        public void setFireTime(LocalDateTime fireTime) {
//            this.fireTime = fireTime;
//        }
//
//        public Date getLastFireTime() {
//            return lastFireTime;
//        }
//
//        public void setLastFireTime(Date lastFireTime) {
//            this.lastFireTime = lastFireTime;
//        }
//
//        public Date getNextFireTime() {
//            return nextFireTime;
//        }
//
//        public void setNextFireTime(Date nextFireTime) {
//            this.nextFireTime = nextFireTime;
//        }
//
//        public String getStatus() {
//            return status;
//        }
//
//        public void setStatus(String status) {
//            this.status = status;
//        }
}



}

