package com.example.projectfirst.pipeline.exception;

public class APIPPipelineNotFoundException extends RuntimeException {
    public APIPPipelineNotFoundException(String id) {
        super("Could not find pipeline with id " + id + "!");
    }
}
