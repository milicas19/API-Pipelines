package com.example.projectfirst.pipelineExecution;

import com.example.projectfirst.connector.exception.APIPYamlParsingException;
import com.example.projectfirst.pipelineExecution.exception.APIPRetryMechanismException;

import java.util.List;

public interface PipelineExecutionInterface {
    List<PipelineExecutionCollection> fetchAllExecutions();
    PipelineExecutionCollection fetchExecution(String id);
    List<PipelineExecutionCollection> fetchPausedExecutions();
    PipelineExecutionCollection executePipeline(String id) throws APIPYamlParsingException, APIPRetryMechanismException;
    PipelineExecutionCollection resumeExecution(String id) throws APIPYamlParsingException, APIPRetryMechanismException;
    void deleteExecution(String id);
    void deleteExecutions();
}
