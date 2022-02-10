package com.example.projectfirst.pipeline;

import com.example.projectfirst.connector.exception.APIPYamlParsingException;
import java.util.List;

public interface PipelineInterface {
    List<PipelineCollection> fetchAllPipelines();
    PipelineCollection fetchPipeline(String id);
    PipelineCollection savePipeline(String yaml) throws APIPYamlParsingException;
    PipelineCollection updatePipeline(String yaml, String id);
    void deletePipeline(String id);
    void deletePipelines();
}
