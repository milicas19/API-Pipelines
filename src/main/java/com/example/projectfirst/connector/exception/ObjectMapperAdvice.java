package com.example.projectfirst.connector.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ObjectMapperAdvice {
    @ResponseBody
    @ExceptionHandler(ObjectMapperException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String objectMapperHandler(ObjectMapperException ex) {
        return ex.getMessage();
    }
}
