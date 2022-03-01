package com.example.projectfirst.security.exceptions;

import com.example.projectfirst.connector.exception.APIPGeneralException;

public class APIPUserAlreadyExists extends APIPGeneralException {
    public APIPUserAlreadyExists(String msg){
        super(msg);
    }
}
