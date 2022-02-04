package com.example.projectfirst.pipeline.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class WrongYmlFileOfPipelineAdvice {
    @ResponseBody
    @ExceptionHandler(APIPWrongYmlFileOfPipelineException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String wrongYmlFileOfPipelineHandler(APIPWrongYmlFileOfPipelineException e) {
        return "Something wrong with yml string of pipeline!";
    }
}
