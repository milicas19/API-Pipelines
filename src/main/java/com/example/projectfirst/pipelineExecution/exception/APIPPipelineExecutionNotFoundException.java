package com.example.projectfirst.pipelineExecution.exception;

import com.example.projectfirst.connector.exception.APIPGeneralException;

public class APIPPipelineExecutionNotFoundException extends APIPGeneralException {
    public APIPPipelineExecutionNotFoundException(String msg){
        super(msg);
    }
}
