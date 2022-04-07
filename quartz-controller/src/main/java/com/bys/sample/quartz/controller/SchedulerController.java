package com.bys.sample.quartz.controller;

import com.bys.sample.quartz.bean.BackupJobDetailRequestBean;
import com.bys.sample.quartz.bean.SchedulerResponseBean;
import com.bys.sample.quartz.dto.ScheduleDTO;
import com.bys.sample.quartz.service.SchedulerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@CrossOrigin()
@RestController
@RequestMapping("/api/v1/backup")
@Tag(name = "BackupSchedulerController", description = "Quartz Scheduler Management API")
public class SchedulerController {
    public static final String JOBS = "/job-group/{jobGroup}/jobs";
    public static final String JOBS_BY_NAME = "/job-group/{jobGroup}/jobs/{jobName}";
    public static final String JOBS_PAUSE = "/job-group/{jobGroup}/jobs/{jobName}/pause";
    public static final String JOBS_RESUME = "/job-group/{jobGroup}/jobs/{jobName}/resume";

    @Autowired
    private SchedulerService schedulerService;

//1    @Operation(summary = "Scheduler create a new Job", description = "", tags = { "BackupSchedulerController" })
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = SchedulerResponseBean.class))),
//            @ApiResponse(responseCode = "404", description = "Scheduler Job Creation API not found", content = @Content(schema = @Schema(implementation = SchedulerResponseBean.class))),
//            @ApiResponse(responseCode = "400", description = "Bad Request,Scheduler Job Creation type not supported", content = @Content(schema = @Schema(implementation = SchedulerResponseBean.class))),
//            @ApiResponse(responseCode = "500", description = "Failure", content = @Content(schema = @Schema(implementation = SchedulerResponseBean.class))) })
//    @PostMapping(path = JOBS)
//    public ResponseEntity<SchedulerResponseBean> createJob(@PathVariable String jobGroup,
//                                                           @RequestBody BackupJobDetailRequestBean jobDetailRequestBean) throws ClassNotFoundException {
//        log.info("BackupSchedulerController#####Test: CreateJob#####");
//        return new ResponseEntity<SchedulerResponseBean>(schedulerService.createJob(jobGroup, jobDetailRequestBean),
//                CREATED);
//    }


    @Operation(summary = "Scheduler create a new Job", description = "", tags = { "BackupSchedulerController" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = SchedulerResponseBean.class))),
            @ApiResponse(responseCode = "404", description = "Scheduler Job Creation API not found", content = @Content(schema = @Schema(implementation = SchedulerResponseBean.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request,Scheduler Job Creation type not supported", content = @Content(schema = @Schema(implementation = SchedulerResponseBean.class))),
            @ApiResponse(responseCode = "500", description = "Failure", content = @Content(schema = @Schema(implementation = SchedulerResponseBean.class))) })
    @PostMapping(path = JOBS)
    public ResponseEntity<SchedulerResponseBean> createJob(@PathVariable String jobGroup,
                                                           @RequestBody ScheduleDTO.CreateScheduleBody body) throws ClassNotFoundException {
        log.info("BackupSchedulerController#####Test: CreateJob#####");
        return new ResponseEntity<SchedulerResponseBean>(schedulerService.createJob(jobGroup, body),
                CREATED);
    }




    @Operation(summary = "Scheduler find a Job", description = "", tags = { "BackupSchedulerController" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = SchedulerResponseBean.class))),
            @ApiResponse(responseCode = "404", description = "Scheduler Job find API not found", content = @Content(schema = @Schema(implementation = SchedulerResponseBean.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request,Scheduler Job find type not supported", content = @Content(schema = @Schema(implementation = SchedulerResponseBean.class))),
            @ApiResponse(responseCode = "500", description = "Failure", content = @Content(schema = @Schema(implementation = SchedulerResponseBean.class))) })
    @GetMapping(path = JOBS_BY_NAME)
    public ResponseEntity<SchedulerResponseBean> findJob(@PathVariable String jobGroup, @PathVariable String jobName) {
        return new ResponseEntity<>(schedulerService.findJob(jobGroup, jobName), OK);
    }

    @Operation(summary = "Scheduler list all Jobs", description = "", tags = { "BackupSchedulerController" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = SchedulerResponseBean.class))),
            @ApiResponse(responseCode = "404", description = "Scheduler Job find API not found", content = @Content(schema = @Schema(implementation = SchedulerResponseBean.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request,Scheduler Job find type not supported", content = @Content(schema = @Schema(implementation = SchedulerResponseBean.class))),
            @ApiResponse(responseCode = "500", description = "Failure", content = @Content(schema = @Schema(implementation = SchedulerResponseBean.class))) })
    @GetMapping(path = JOBS)
    public ResponseEntity<SchedulerResponseBean> allJobs(@PathVariable String jobGroup) {
        return new ResponseEntity<>(schedulerService.allJobs(jobGroup), OK);
    }

    @Operation(summary = "Scheduler update an existing Job", description = "", tags = {"BackupSchedulerController" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = SchedulerResponseBean.class))),
            @ApiResponse(responseCode = "404", description = "Scheduler Job update API not found", content = @Content(schema = @Schema(implementation = SchedulerResponseBean.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request,Scheduler Job update type not supported", content = @Content(schema = @Schema(implementation = SchedulerResponseBean.class))),
            @ApiResponse(responseCode = "500", description = "Failure", content = @Content(schema = @Schema(implementation = SchedulerResponseBean.class))) })
    @PutMapping(path = JOBS_BY_NAME)
    public ResponseEntity<SchedulerResponseBean> updateJob(@PathVariable String jobGroup, @PathVariable String jobName,
                                                           @RequestBody BackupJobDetailRequestBean jobDetailRequestBean) {
        return new ResponseEntity<>(schedulerService.updateJob(jobGroup, jobName, jobDetailRequestBean), OK);
    }

    @Operation(summary = "Scheduler delete Job", description = "", tags = { "BackupSchedulerController" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = SchedulerResponseBean.class))),
            @ApiResponse(responseCode = "404", description = "Scheduler Job delete API not found", content = @Content(schema = @Schema(implementation = SchedulerResponseBean.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request,Scheduler Job delete type not supported", content = @Content(schema = @Schema(implementation = SchedulerResponseBean.class))),
            @ApiResponse(responseCode = "500", description = "Failure", content = @Content(schema = @Schema(implementation = SchedulerResponseBean.class))) })
    @DeleteMapping(path = JOBS_BY_NAME)
    public ResponseEntity<SchedulerResponseBean> deleteJob(@PathVariable String jobGroup,
                                                           @PathVariable String jobName) {
        return new ResponseEntity<>(schedulerService.deleteJob(jobGroup, jobName), OK);
    }

    @Operation(summary = "Scheduler pause Job", description = "", tags = { "BackupSchedulerController" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = SchedulerResponseBean.class))),
            @ApiResponse(responseCode = "404", description = "Scheduler Job pause API not found", content = @Content(schema = @Schema(implementation = SchedulerResponseBean.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request,Scheduler Job pause type not supported", content = @Content(schema = @Schema(implementation = SchedulerResponseBean.class))),
            @ApiResponse(responseCode = "500", description = "Failure", content = @Content(schema = @Schema(implementation = SchedulerResponseBean.class))) })
    @PatchMapping(path = JOBS_PAUSE)
    public ResponseEntity<SchedulerResponseBean> pauseJob(@PathVariable String jobGroup, @PathVariable String jobName) {
        return new ResponseEntity<>(schedulerService.pauseJob(jobGroup, jobName), OK);
    }

    @Operation(summary = "Scheduler resume Job", description = "", tags = { "BackupSchedulerController" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = SchedulerResponseBean.class))),
            @ApiResponse(responseCode = "404", description = "Scheduler Job resume API not found", content = @Content(schema = @Schema(implementation = SchedulerResponseBean.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request,Scheduler Job resume type not supported", content = @Content(schema = @Schema(implementation = SchedulerResponseBean.class))),
            @ApiResponse(responseCode = "500", description = "Failure", content = @Content(schema = @Schema(implementation = SchedulerResponseBean.class))) })
    @PatchMapping(path = JOBS_RESUME)
    public ResponseEntity<SchedulerResponseBean> resumeJob(@PathVariable String jobGroup,
                                                           @PathVariable String jobName) {
        return new ResponseEntity<>(schedulerService.resumeJob(jobGroup, jobName), OK);
    }

}
