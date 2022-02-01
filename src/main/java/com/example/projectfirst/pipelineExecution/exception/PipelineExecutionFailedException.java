package com.example.projectfirst.pipelineExecution.exception;

public class PipelineExecutionFailedException extends RuntimeException {
    public PipelineExecutionFailedException(String message){
        super(message);
    }
}
