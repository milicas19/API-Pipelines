package com.example.projectfirst.pipelineExecution.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class PipelineNotPausedAdvice {
    @ResponseBody
    @ExceptionHandler(APIPPipelineNotPausedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String pipelineNotPausedHandler(APIPPipelineNotPausedException ex) {
        return ex.getMessage();
    }
}
