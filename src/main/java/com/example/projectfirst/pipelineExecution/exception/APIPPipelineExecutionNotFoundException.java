package com.example.projectfirst.pipelineExecution.exception;

public class APIPPipelineExecutionNotFoundException extends RuntimeException{
    public APIPPipelineExecutionNotFoundException(String id){
        super("Could not find pipeline execution with id " + id + "!");
    }
}
