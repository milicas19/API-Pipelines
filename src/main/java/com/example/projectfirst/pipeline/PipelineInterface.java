package com.example.projectfirst.pipeline;

import java.util.List;

public interface PipelineInterface {
    List<PipelineCollection> fetchAllPipelines();
    PipelineCollection fetchPipeline(String id);
    String savePipeline(String yaml);String deletePipeline(String id);
    PipelineCollection updatePipeline(String yaml, String id);
    void deletePipelines();
}
