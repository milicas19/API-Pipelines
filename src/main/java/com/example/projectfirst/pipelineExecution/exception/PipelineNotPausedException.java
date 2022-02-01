package com.example.projectfirst.pipelineExecution.exception;

public class PipelineNotPausedException extends RuntimeException{
    public PipelineNotPausedException(String id) {
        super("Pipeline with id " + id + " is not paused!");
    }
}
