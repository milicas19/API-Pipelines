package com.example.projectfirst.pipeline;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class PipelineNotFoundAdvice {
    @ResponseBody
    @ExceptionHandler(PipelineNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String pipelineNotFoundHandler(PipelineNotFoundException ex) {
        return ex.getMessage();
    }
}
