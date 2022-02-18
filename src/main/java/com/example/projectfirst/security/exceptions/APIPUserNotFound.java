package com.example.projectfirst.security.exceptions;

import com.example.projectfirst.connector.exception.APIPGeneralException;

public class APIPUserNotFound extends APIPGeneralException {
    public APIPUserNotFound(String msg){
        super(msg);
    }
}
