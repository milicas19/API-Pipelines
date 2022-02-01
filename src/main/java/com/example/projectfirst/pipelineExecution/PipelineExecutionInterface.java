package com.example.projectfirst.pipelineExecution;

import java.util.List;

public interface PipelineExecutionInterface {
    List<PipelineExecutionCollection> fetchAllExecutions();
    PipelineExecutionCollection fetchExecution(String id);
    List<PipelineExecutionCollection> fetchPausedExecutions();
    PipelineExecutionCollection executePipeline(String id);
    PipelineExecutionCollection resumeExecution(String id);
    PipelineExecutionCollection deleteExecution(String id);
    void deleteExecutions();
}
