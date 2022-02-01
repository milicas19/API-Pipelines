package com.example.projectfirst.pipelineExecution.exception;

public class PipelineExecutionNotFoundException extends RuntimeException{
    public PipelineExecutionNotFoundException(String id){
        super("Could not find pipeline execution with id " + id + "!");
    }
}
