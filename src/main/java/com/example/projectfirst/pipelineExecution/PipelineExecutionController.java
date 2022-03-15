package com.example.projectfirst.pipelineExecution;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@Slf4j
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
    public List<PipelineExecutionCollection> getPausedExecutions() {
        return pipelineExecutionService.fetchPausedExecutions();
    }

    @PostMapping("/execute/{id}")
    public ResponseEntity<String> executePipeline(@PathVariable(value="id") String id) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(pipelineExecutionService.executePipeline(id));
    }

    @PutMapping("/resume/{id}")
    public ResponseEntity<String> resumeExecution(@PathVariable(value="id") String id) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(pipelineExecutionService.resumeExecution(id));
    }

    @DeleteMapping("/executions/{id}")
    public void deleteExecution(@PathVariable(value="id") String id){
        pipelineExecutionService.deleteExecution(id);
    }

    @DeleteMapping("/executions")
    public void deleteExecutions(){
        pipelineExecutionService.deleteExecutions();
    }

}
