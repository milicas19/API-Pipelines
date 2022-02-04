package com.example.projectfirst.pipelineExecution.exception;

import java.io.IOException;

public class APIPInitiateExecutionFailed extends IOException {
    public APIPInitiateExecutionFailed(Throwable e){
        super(e);
    }
}
