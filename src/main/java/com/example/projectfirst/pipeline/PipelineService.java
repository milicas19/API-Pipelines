package com.example.projectfirst.pipeline;

import com.example.projectfirst.connector.exception.ObjectMapperException;
import com.example.projectfirst.pipeline.exception.PipelineAlreadyExistsException;
import com.example.projectfirst.pipeline.exception.PipelineNotFoundException;
import com.example.projectfirst.pipeline.model.Pipeline;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class PipelineService implements PipelineInterface{
    @Autowired
    private PipelineRepository pipelineRepository;

    public List<PipelineCollection> fetchAllPipelines() {
        return pipelineRepository.findAll();
    }

    public PipelineCollection fetchPipeline(String id) {
        return pipelineRepository.findById(id)
                .orElseThrow(() -> new PipelineNotFoundException(id));
    }

    public PipelineCollection savePipeline(String yaml) throws ObjectMapperException {
        // YAML to POJO
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

        try {
            Map<String, Pipeline> pipelineMap = objectMapper.readValue(yaml,
                    new TypeReference<>(){});
            Pipeline pipe = pipelineMap.get("pipeline");
            String id = pipe.getId();

            if(pipelineRepository.existsById(id)){
                throw new PipelineAlreadyExistsException(id);
            }
            PipelineCollection pipeline = new PipelineCollection(id,yaml, LocalDateTime.now(), LocalDateTime.now());
            pipelineRepository.save(pipeline);
            return pipeline;
        } catch (IOException e) {
            throw new ObjectMapperException();
        }
    }

    public PipelineCollection deletePipeline(String id) {
        return pipelineRepository.findById(id)
                        .map(pipelineCollection -> {
                            pipelineRepository.deleteById(id);
                            return pipelineCollection;
                        })
                .orElseThrow(() -> new PipelineNotFoundException(id));
    }

    public PipelineCollection updatePipeline(String yaml, String id) {
        return pipelineRepository.findById(id)
                .map(pipelineCollection -> {
                    pipelineCollection.setYmlFile(yaml);
                    pipelineCollection.setModificationDate(LocalDateTime.now());
                    return pipelineRepository.save(pipelineCollection);
                })
                .orElseThrow(() -> new PipelineNotFoundException(id));
    }

    public void deletePipelines() {
        pipelineRepository.deleteAll();
    }
}
