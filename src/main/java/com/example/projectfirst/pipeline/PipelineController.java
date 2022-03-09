package com.example.projectfirst.pipeline;

import com.example.projectfirst.exceptions.APIPYamlParsingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class PipelineController {
    @Autowired
    private PipelineService pipelineService;

    @GetMapping("/pipelines")
    public List<PipelineCollection> getPipelines(){
        return pipelineService.fetchAllPipelines();
    }

    @GetMapping("/pipelines/{id}")
    public PipelineCollection getPipeline(@PathVariable(value="id") String id){
        return pipelineService.fetchPipeline(id);
    }

    @PostMapping("/pipelines")
    public ResponseEntity<PipelineCollection> savePipeline(@RequestBody String yaml) throws APIPYamlParsingException {
        return ResponseEntity.status(HttpStatus.CREATED).body(pipelineService.savePipeline(yaml));
    }

    @PutMapping("/pipelines/{id}")
    public PipelineCollection updatePipeline(@RequestBody String yaml, @PathVariable(value="id") String id){
        return pipelineService.updatePipeline(yaml,id);
    }

    @DeleteMapping("/pipelines/{id}")
    public void deletePipeline(@PathVariable(value="id") String id){
        pipelineService.deletePipeline(id);
    }

    @DeleteMapping("/pipelines")
    public void deletePipelines(){
        pipelineService.deletePipelines();
    }
}
