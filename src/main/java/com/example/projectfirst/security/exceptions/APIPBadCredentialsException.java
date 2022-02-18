package com.example.projectfirst.security.exceptions;

import com.example.projectfirst.connector.exception.APIPGeneralException;

public class APIPBadCredentialsException extends APIPGeneralException {
    public APIPBadCredentialsException(String msg){
        super(msg);
    }
}
