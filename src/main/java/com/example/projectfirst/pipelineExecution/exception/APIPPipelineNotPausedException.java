package com.example.projectfirst.pipelineExecution.exception;

import com.example.projectfirst.connector.exception.APIPGeneralException;

public class APIPPipelineNotPausedException extends APIPGeneralException {
    public APIPPipelineNotPausedException(String msg) {
        super(msg);
    }
}
