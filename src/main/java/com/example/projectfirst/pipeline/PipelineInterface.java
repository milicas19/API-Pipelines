package com.example.projectfirst.pipeline;
import com.example.projectfirst.pipeline.exception.APIPWrongYmlFileOfPipelineException;
import java.util.List;

public interface PipelineInterface {
    List<PipelineCollection> fetchAllPipelines();
    PipelineCollection fetchPipeline(String id);
    PipelineCollection savePipeline(String yaml) throws APIPWrongYmlFileOfPipelineException;
    PipelineCollection updatePipeline(String yaml, String id);
    void deletePipeline(String id);
    void deletePipelines();
}
