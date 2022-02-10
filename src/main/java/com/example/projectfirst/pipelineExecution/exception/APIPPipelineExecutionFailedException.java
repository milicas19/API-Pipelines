package com.example.projectfirst.pipelineExecution.exception;

import com.example.projectfirst.connector.exception.APIPGeneralException;

public class APIPPipelineExecutionFailedException extends APIPGeneralException {
    public APIPPipelineExecutionFailedException(String msg){
        super(msg);
    }
}
