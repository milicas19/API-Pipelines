package com.example.projectfirst.pipeline.apiRequestHandler;

import com.example.projectfirst.exceptions.APIPYamlParsingException;
import com.example.projectfirst.pipeline.model.StepParameters;
import com.example.projectfirst.pipelineExecution.StepExecution;
import com.example.projectfirst.exceptions.APIPStepExecutionFailedException;
import org.springframework.beans.factory.annotation.Autowired;

public class PostHandler implements StepHandler{
    @Autowired
    private StepService stepService;

    @Override
    public StepExecution execute(StepParameters stepParameter)
            throws APIPYamlParsingException, APIPStepExecutionFailedException {

        return stepService.executePostRequest(stepParameter);
    }
}
