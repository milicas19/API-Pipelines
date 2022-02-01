package com.example.projectfirst.pipeline.exception;

public class PipelineAlreadyExistsException extends RuntimeException{
    public PipelineAlreadyExistsException(String id){
        super("Pipeline with id: " + id + " already exists!");
    }
}
