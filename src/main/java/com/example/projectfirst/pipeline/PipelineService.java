package com.example.projectfirst.pipeline;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class PipelineService {
    @Autowired
    private PipelineRepository pipelineRepository;

    public List<PipelineCollection> fetchAllPipelines() {
        return pipelineRepository.findAll();
    }

    public PipelineCollection fetchPipeline(String id) {
        return pipelineRepository.findById(id)
                .orElseThrow(() -> new PipelineNotFoundException(id));
    }

    public String savePipeline(String yaml) {
        // YAML to POJO
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

        try {
            Map<String, Pipeline> pipelineMap = objectMapper.readValue(yaml,
                    new TypeReference<>(){});
            Pipeline pipe = pipelineMap.get("pipeline");
            if(pipelineRepository.existsById(pipe.getId())){
                return "Pipeline with that id already exists!";
            }
            PipelineCollection pipeline = new PipelineCollection(pipe.getId(),yaml, LocalDateTime.now(), LocalDateTime.now());
            pipelineRepository.save(pipeline);
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "Successfully saved!";
    }

    public String deletePipeline(String id) {
        if(pipelineRepository.existsById(id)){
            pipelineRepository.deleteById(id);
            return "Successfully deleted!";
        }
        throw new PipelineNotFoundException(id);
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
