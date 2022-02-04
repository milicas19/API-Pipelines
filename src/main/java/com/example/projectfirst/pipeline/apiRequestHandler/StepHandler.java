package com.example.projectfirst.pipeline.apiRequestHandler;

import com.example.projectfirst.connector.exception.APIPWrongYmlFileOfConnectorException;
import com.example.projectfirst.pipeline.model.StepParameters;
import com.example.projectfirst.pipelineExecution.StepExecution;
import com.example.projectfirst.pipelineExecution.exception.APIPStepExecutionFailedException;

import java.io.IOException;

public interface StepHandler {
    StepExecution execute(StepParameters stepParameter)
            throws APIPWrongYmlFileOfConnectorException, APIPStepExecutionFailedException;
}
