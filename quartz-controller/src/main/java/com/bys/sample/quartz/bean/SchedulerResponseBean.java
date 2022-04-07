package com.bys.sample.quartz.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SchedulerResponseBean {
    private Object result;
    private HttpStatus resultCode;
}

