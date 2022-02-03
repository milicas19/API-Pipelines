package com.example.projectfirst.pipeline.exception;

public class PipelineNotFoundException extends RuntimeException {
    public PipelineNotFoundException(String id) {
        super("Could not find pipeline with id: " + id + "!");
    }
}
