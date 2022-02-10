package com.example.projectfirst.pipeline.exception;

import com.example.projectfirst.connector.exception.APIPGeneralException;

public class APIPPipelineNotFoundException extends APIPGeneralException {
    public APIPPipelineNotFoundException(String msg) {
        super(msg);
    }
}
