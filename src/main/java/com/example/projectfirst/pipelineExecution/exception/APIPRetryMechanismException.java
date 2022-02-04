package com.example.projectfirst.pipelineExecution.exception;

import java.io.IOException;

public class APIPRetryMechanismException extends IOException {
    public APIPRetryMechanismException(Throwable e){
        super(e);
    }
}
