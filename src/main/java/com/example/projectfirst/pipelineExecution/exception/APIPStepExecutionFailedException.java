package com.example.projectfirst.pipelineExecution.exception;

import java.io.IOException;

public class APIPStepExecutionFailedException extends IOException {
    public APIPStepExecutionFailedException(Throwable e){
        super(e);
    }
}
