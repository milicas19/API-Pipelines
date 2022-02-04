package com.example.projectfirst.pipelineExecution.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class PipelineExecutionFailedAdvice {
    @ResponseBody
    @ExceptionHandler(APIPPipelineExecutionFailedException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    String pipelineExecutionFailedHandler(APIPPipelineExecutionFailedException ex) {
        return ex.getMessage();
    }
}
