package com.example.projectfirst.pipelineExecution;

import java.util.List;

public interface PipelineExecutionInterface {
    List<PipelineExecutionCollection> fetchAllExecutions();
    PipelineExecutionCollection fetchExecution(String id);
    List<PipelineExecutionCollection> fetchPausedExecutions();
    String executePipeline(String id);
    String resumeExecution(String id);
    void deleteExecution(String id);
    void deleteExecutions();
}
