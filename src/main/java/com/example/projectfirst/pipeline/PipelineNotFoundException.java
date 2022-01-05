package com.example.projectfirst.pipeline;

public class PipelineNotFoundException extends RuntimeException {
    public PipelineNotFoundException(String id) {
        super("Could not find pipeline with id: " + id + "!");
    }
}
