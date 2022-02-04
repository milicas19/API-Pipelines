package com.example.projectfirst.pipeline.exception;

public class APIPPipelineAlreadyExistsException extends RuntimeException{
    public APIPPipelineAlreadyExistsException(String id){
        super("Pipeline with id " + id + " already exists!");
    }
}
