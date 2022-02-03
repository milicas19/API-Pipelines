package com.example.projectfirst.pipelineExecution.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class PipelineExecutionFailedAdvice {
    @ResponseBody
    @ExceptionHandler(PipelineExecutionFailedException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    String pipelineExecutionFailedHandler(PipelineExecutionFailedException ex) {
        return ex.getMessage();
    }
}
