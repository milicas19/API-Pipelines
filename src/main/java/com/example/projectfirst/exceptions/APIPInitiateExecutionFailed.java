package com.example.projectfirst.exceptions;

import java.io.IOException;

public class APIPInitiateExecutionFailed extends IOException {
    public APIPInitiateExecutionFailed(Throwable e){
        super(e);
    }
}
