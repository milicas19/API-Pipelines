package com.example.projectfirst.pipelineExecution.exception;

public class APIPPipelineNotPausedException extends RuntimeException{
    public APIPPipelineNotPausedException(String id) {
        super("Pipeline with id " + id + " is not paused!");
    }
}
