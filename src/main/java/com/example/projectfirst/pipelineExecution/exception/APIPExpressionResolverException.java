package com.example.projectfirst.pipelineExecution.exception;

import com.example.projectfirst.connector.exception.APIPGeneralException;

public class APIPExpressionResolverException extends APIPGeneralException {
    public APIPExpressionResolverException(String msg){
        super(msg);
    }
}
