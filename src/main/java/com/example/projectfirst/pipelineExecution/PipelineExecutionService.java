package com.example.projectfirst.pipelineExecution;

import com.example.projectfirst.exceptions.APIPInitiateExecutionFailed;
import com.example.projectfirst.exceptions.APIPPipelineExecutionFailedException;
import com.example.projectfirst.exceptions.APIPPipelineExecutionNotFoundException;
import com.example.projectfirst.exceptions.APIPPipelineNotPausedException;
import com.example.projectfirst.pipelineExecution.services.WorkflowService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class PipelineExecutionService implements PipelineExecutionInterface{
    @Autowired
    private PipelineExecutionRepository pipelineExecutionRepository;
    @Autowired
    private WorkflowService workflowService;

    public List<PipelineExecutionCollection> fetchAllExecutions() {
        log.info("Fetching all pipeline executions!");
        return pipelineExecutionRepository.findAll();
    }

    public PipelineExecutionCollection fetchExecution(String id) {
        log.info("Fetching pipeline executions with id " + id + "!");
        return pipelineExecutionRepository.findById(id).
                orElseThrow(() -> {
                    log.error("Pipeline execution not found!");
                    throw new APIPPipelineExecutionNotFoundException("Could not find pipeline execution with id " + id + "!");
                });
    }

    public List<PipelineExecutionCollection> fetchPausedExecutions() {
        log.info("Fetching paused pipeline executions!");
        List<PipelineExecutionCollection> pipelineExecutionList = new ArrayList<>();
        for(PipelineExecutionCollection pipelineExecution: pipelineExecutionRepository.findAll()){
            if(pipelineExecution.getState().equals("paused")){
                pipelineExecutionList.add(pipelineExecution);
            }
        }
        return pipelineExecutionList;
    }

    public String executePipeline(String id) {
        log.info("Execution of pipeline with id " + id + " begins!");
        try {
            String pipelineExeId = workflowService.initiateExecution(id);

            Runnable task = () -> {
                workflowService.executePipelineSteps(pipelineExeId);
            };
            new Thread(task).start();

            return pipelineExeId;
        } catch (APIPInitiateExecutionFailed e) {
            throw new APIPPipelineExecutionFailedException("Pipeline execution initiation failed! " +
                    "Something wrong with the yml file of pipeline! Message: " + e.getMessage());
        }
    }

    public String resumeExecution(String pipelineExeId) {
        log.info("Resuming pipeline execution with id " + pipelineExeId + "!");
        Optional<PipelineExecutionCollection> pipelineExecution
                = pipelineExecutionRepository.findById(pipelineExeId);

        if(pipelineExecution.isEmpty()) {
            throw new APIPPipelineExecutionNotFoundException("Could not find pipeline execution with id " + pipelineExeId + "!");
        }
        if(!pipelineExecution.get().getState().equals("paused")) {
            throw new APIPPipelineNotPausedException("Pipeline execution with id " + pipelineExeId + " is not paused!");
        }

        Runnable task = () -> {
            workflowService.executePipelineSteps(pipelineExeId);
        };
        new Thread(task).start();

        return pipelineExeId;
    }

    public void deleteExecution(String id) {
        log.info("Deleting pipeline execution with id " + id +"!");
        pipelineExecutionRepository.deleteById(id);
    }

    public void deleteExecutions() {
        log.info("Deleting all pipeline executions!");
        pipelineExecutionRepository.deleteAll();
    }

}
