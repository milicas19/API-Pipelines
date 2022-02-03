package com.example.projectfirst.pipeline;

import com.example.projectfirst.connector.exception.ObjectMapperException;
import java.util.List;

public interface PipelineInterface {
    List<PipelineCollection> fetchAllPipelines();
    PipelineCollection fetchPipeline(String id);
    PipelineCollection savePipeline(String yaml) throws ObjectMapperException;
    PipelineCollection updatePipeline(String yaml, String id);
    PipelineCollection deletePipeline(String id);
    void deletePipelines();
}
