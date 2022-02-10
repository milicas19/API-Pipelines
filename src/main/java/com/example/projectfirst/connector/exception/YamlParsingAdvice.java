package com.example.projectfirst.connector.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class YamlParsingAdvice {
    @ResponseBody
    @ExceptionHandler(APIPYamlParsingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String APIPYamlParsingHandler(APIPYamlParsingException e) {
        return e.getMessage();
    }
}
