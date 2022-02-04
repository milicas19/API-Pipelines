package com.example.projectfirst.pipeline.apiRequestHandler;

import com.example.projectfirst.connector.exception.APIPWrongYmlFileOfConnectorException;
import com.example.projectfirst.pipeline.model.StepParameters;
import com.example.projectfirst.pipelineExecution.StepExecution;
import com.example.projectfirst.pipelineExecution.exception.APIPStepExecutionFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;

public class PostHandler implements StepHandler{
    @Autowired
    private StepService stepService;

    @Override
    public StepExecution execute(StepParameters stepParameter)
            throws APIPWrongYmlFileOfConnectorException, APIPStepExecutionFailedException {

        return stepService.executePostRequest(stepParameter);
    }
}
