package com.example.projectfirst.pipelineExecution;

import com.example.projectfirst.connector.exception.APIPWrongYmlFileOfConnectorException;
import com.example.projectfirst.pipelineExecution.exception.APIPRetryMechanismException;

import java.util.List;

public interface PipelineExecutionInterface {
    List<PipelineExecutionCollection> fetchAllExecutions();
    PipelineExecutionCollection fetchExecution(String id);
    List<PipelineExecutionCollection> fetchPausedExecutions();
    PipelineExecutionCollection executePipeline(String id) throws APIPWrongYmlFileOfConnectorException, APIPRetryMechanismException;
    PipelineExecutionCollection resumeExecution(String id) throws APIPWrongYmlFileOfConnectorException, APIPRetryMechanismException;
    void deleteExecution(String id);
    void deleteExecutions();
}
