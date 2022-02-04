package com.example.projectfirst.connector.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class WrongYmlFileOfConnectorAdvice {
    @ResponseBody
    @ExceptionHandler(APIPWrongYmlFileOfConnectorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String wrongYmlFileOfConnectorHandler(APIPWrongYmlFileOfConnectorException e) {
        log.error("Failed to save connector! Message: " + e.getMessage());
        return "Something wrong with the yml string of connector!";
    }
}
