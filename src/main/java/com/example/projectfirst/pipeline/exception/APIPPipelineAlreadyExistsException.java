package com.example.projectfirst.pipeline.exception;

import com.example.projectfirst.connector.exception.APIPGeneralException;

public class APIPPipelineAlreadyExistsException extends APIPGeneralException {
    public APIPPipelineAlreadyExistsException(String msg){
        super(msg);
    }
}
