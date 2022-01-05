package com.example.projectfirst.pipeline;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PipelineService {
    @Autowired
    private PipelineRepository pipelineRepository;

    public List<PipelineCollection> fetchAllPipelines() {
        return pipelineRepository.findAll();
    }

    public String savePipeline(String yaml) {
        PipelineCollection pipeline = new PipelineCollection(yaml, LocalDateTime.now(), LocalDateTime.now());
        pipelineRepository.save(pipeline);
        return "Successefully saved!";
    }

    public PipelineCollection fetchPipeline(String id) {
        return pipelineRepository.findById(id)
                .orElseThrow(() -> new PipelineNotFoundException(id));
    }

    public String deletePipeline(String id) {
        if(pipelineRepository.existsById(id)){
            pipelineRepository.deleteById(id);
            return "Successefully deleted!";
        }
        throw new PipelineNotFoundException(id);
    }

    public PipelineCollection UpdatePipeline(String yaml, String id) {
        return pipelineRepository.findById(id)
                .map(pipelineCollection -> {
                    pipelineCollection.setYmlFile(yaml);
                    pipelineCollection.setModificationDate(LocalDateTime.now());
                    return pipelineRepository.save(pipelineCollection);
                })
                .orElseThrow(() -> new PipelineNotFoundException(id));
    }
}
