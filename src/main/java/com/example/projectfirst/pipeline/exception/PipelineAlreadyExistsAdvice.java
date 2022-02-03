package com.example.projectfirst.pipeline.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class PipelineAlreadyExistsAdvice {
    @ResponseBody
    @ExceptionHandler(PipelineAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String pipelineAlreadyExistsHandler(PipelineAlreadyExistsException ex) {
        return ex.getMessage();
    }
}
