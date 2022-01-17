package com.example.projectfirst.pipelineExecution;

import com.example.projectfirst.pipeline.PipelineNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class PipelineExecutionNotFoundAdvice {
    @ResponseBody
    @ExceptionHandler(PipelineExecutionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String pipelineExecutionNotFoundHandler(PipelineExecutionNotFoundException ex) {
        return ex.getMessage();
    }
}
