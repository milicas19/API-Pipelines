package com.example.projectfirst.pipeline.apiRequestHandler;

import com.example.projectfirst.exceptions.APIPYamlParsingException;
import com.example.projectfirst.pipeline.model.StepParameters;
import com.example.projectfirst.pipelineExecution.StepExecution;
import com.example.projectfirst.exceptions.APIPStepExecutionFailedException;

public interface StepHandler {
    StepExecution execute(StepParameters stepParameter)
            throws APIPYamlParsingException, APIPStepExecutionFailedException;
}
