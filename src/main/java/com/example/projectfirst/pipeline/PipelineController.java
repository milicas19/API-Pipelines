package com.example.projectfirst.pipeline;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
    public String postPipeline(@RequestBody String yaml) {
        return pipelineService.savePipeline(yaml);
    }

    @PutMapping("/pipelines/{id}")
    public PipelineCollection putPipeline(@RequestBody String yaml, @PathVariable(value="id") String id){
        return pipelineService.updatePipeline(yaml,id);
    }

    @DeleteMapping("/pipelines/{id}")
    public String deletePipeline(@PathVariable(value="id") String id){
        return pipelineService.deletePipeline(id);
    }
}
