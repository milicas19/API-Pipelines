package com.example.projectfirst.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class GeneralAdvice {
    @ResponseBody
    @ExceptionHandler(APIPGeneralException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String generalHandler(APIPGeneralException ex) {
        log.error(ex.getMessage());
        return ex.getMessage();
    }
}
