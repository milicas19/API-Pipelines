package com.example.projectfirst.exceptions;

import java.io.IOException;

public class APIPStepExecutionFailedException extends IOException {
    public APIPStepExecutionFailedException(Throwable e){
        super(e);
    }
}
