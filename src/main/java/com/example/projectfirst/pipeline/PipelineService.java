package com.example.projectfirst.pipeline;

import com.example.projectfirst.pipeline.exception.APIPPipelineAlreadyExistsException;
import com.example.projectfirst.pipeline.exception.APIPPipelineNotFoundException;
import com.example.projectfirst.pipeline.exception.APIPWrongYmlFileOfPipelineException;
import com.example.projectfirst.pipeline.model.Pipeline;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class PipelineService implements PipelineInterface{
    @Autowired
    private PipelineRepository pipelineRepository;

    public List<PipelineCollection> fetchAllPipelines() {
        log.info("Fetching all pipelines!");
        return pipelineRepository.findAll();
    }

    public PipelineCollection fetchPipeline(String id) {
        log.info("Fetching pipeline with id " + id + "!");
        return pipelineRepository.findById(id)
                .orElseThrow(() -> new APIPPipelineNotFoundException(id));
    }

    public PipelineCollection savePipeline(String yaml) throws APIPWrongYmlFileOfPipelineException {
        log.info("Saving pipeline!");
        // YAML to POJO
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

        try {
            Map<String, Pipeline> pipelineMap = objectMapper.readValue(yaml,
                    new TypeReference<>(){});
            Pipeline pipe = pipelineMap.get("pipeline");
            String id = pipe.getId();

            if(pipelineRepository.existsById(id)){
                throw new APIPPipelineAlreadyExistsException(id);
            }
            PipelineCollection pipeline = new PipelineCollection(id,yaml, LocalDateTime.now(), LocalDateTime.now());
            pipelineRepository.save(pipeline);
            log.info("Pipeline with id " + id + " successfully saved!");
            return pipeline;
        } catch (IOException e) {
            log.error("Failed to save pipeline! Message: " + e.getMessage());
            throw new APIPWrongYmlFileOfPipelineException(e);
        }
    }

    public PipelineCollection updatePipeline(String yaml, String id) {
        log.info("Updating pipeline with id " + id + "!");
        return pipelineRepository.findById(id)
                .map(pipelineCollection -> {
                    pipelineCollection.setYmlFile(yaml);
                    pipelineCollection.setModificationDate(LocalDateTime.now());
                    log.info("Pipeline successfully updated!");
                    return pipelineRepository.save(pipelineCollection);
                })
                .orElseThrow(() -> new APIPPipelineNotFoundException(id));
    }

    public void deletePipeline(String id) {
        log.info("Deleting pipeline with id " + id + "!");
        pipelineRepository.deleteById(id);
    }

    public void deletePipelines() {
        log.info("Deleting all pipelines!");
        pipelineRepository.deleteAll();
    }
}
