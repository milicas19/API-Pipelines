package com.example.projectfirst.connector;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ConnectorNotFoundAdvice {
    @ResponseBody
    @ExceptionHandler(ConnectorNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String connectorNotFoundHandler(ConnectorNotFoundException ex) {
        return ex.getMessage();
    }
}
