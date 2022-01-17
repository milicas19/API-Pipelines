package com.example.projectfirst.pipelineExecution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PipelineExecutionController {
    @Autowired
    private PipelineExecutionService pipelineExecutionService;

    @GetMapping("executions")
    public List<PipelineExecutionCollection> getAllExecutions(){
        return pipelineExecutionService.fetchAllExecutions();
    }

    @GetMapping("executions/{id}")
    public PipelineExecutionCollection getExecution(@PathVariable(value="id") String id){
        return pipelineExecutionService.fetchExecution(id);
    }

    @GetMapping("executions/paused")
    public List<PipelineExecutionCollection> getPausedExecutions(){
        return pipelineExecutionService.fetchPausedExecutions();
    }

    @PostMapping("execute/{id}")
    public String executePipeline(@PathVariable(value="id") String id){
        return pipelineExecutionService.executePipeline(id);
    }

    @PutMapping("resume/{id}")
    public String resumeExecution(@PathVariable(value="id") String id){
        return pipelineExecutionService.resumeExecution(id);
    }

    @DeleteMapping("/executions/{id}")
    public String deleteExecution(@PathVariable(value="id") String id){
        return pipelineExecutionService.deleteExecution(id);
    }

    @DeleteMapping("executions")
    public void deleteExecutions(){
        pipelineExecutionService.deleteExecutions();
    }

}
