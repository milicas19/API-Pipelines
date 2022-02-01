package com.example.projectfirst.pipelineExecution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class PipelineExecutionController {
    @Autowired
    private PipelineExecutionService pipelineExecutionService;

    @GetMapping("/executions")
    public List<PipelineExecutionCollection> getAllExecutions(){
        return pipelineExecutionService.fetchAllExecutions();
    }

    @GetMapping("/executions/{id}")
    public PipelineExecutionCollection getExecution(@PathVariable(value="id") String id){
        return pipelineExecutionService.fetchExecution(id);
    }

    @GetMapping("/executions/paused")
    public List<PipelineExecutionCollection> getPausedExecutions(){
        return pipelineExecutionService.fetchPausedExecutions();
    }

    @PostMapping("/execute/{id}")
    public PipelineExecutionCollection executePipeline(@PathVariable(value="id") String id){
        return pipelineExecutionService.executePipeline(id);
    }

    @PutMapping("/resume/{id}")
    public PipelineExecutionCollection resumeExecution(@PathVariable(value="id") String id){
        return pipelineExecutionService.resumeExecution(id);
    }

    @DeleteMapping("/executions/{id}")
    public PipelineExecutionCollection deleteExecution(@PathVariable(value="id") String id){
        return pipelineExecutionService.deleteExecution(id);
    }

    @DeleteMapping("/executions")
    public void deleteExecutions(){
        pipelineExecutionService.deleteExecutions();
    }

}
