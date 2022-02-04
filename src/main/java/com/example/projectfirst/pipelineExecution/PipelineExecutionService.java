package com.example.projectfirst.pipelineExecution;

import com.example.projectfirst.connector.exception.APIPWrongYmlFileOfConnectorException;
import com.example.projectfirst.pipelineExecution.exception.APIPInitiateExecutionFailed;
import com.example.projectfirst.pipelineExecution.exception.APIPPipelineExecutionFailedException;
import com.example.projectfirst.pipelineExecution.exception.APIPPipelineExecutionNotFoundException;
import com.example.projectfirst.pipelineExecution.exception.APIPPipelineNotPausedException;
import com.example.projectfirst.pipelineExecution.exception.APIPRetryMechanismException;
import com.example.projectfirst.pipelineExecution.services.WorkflowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
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
                    throw new APIPPipelineExecutionNotFoundException(id);
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

    public PipelineExecutionCollection executePipeline(String id)
            throws APIPWrongYmlFileOfConnectorException, APIPRetryMechanismException {

        log.info("Execution of pipeline with id " + id + " begins!");
        try {
            String pipelineExeId = workflowService.initiateExecution(id);
            return workflowService.executePipelineSteps(pipelineExeId);
        } catch (APIPInitiateExecutionFailed e) {
            log.error("Initiation of pipeline execution failed! Message: " + e.getMessage());
            throw new APIPPipelineExecutionFailedException("Pipeline execution initiation failed! " +
                    "Something wrong with the yml file of pipeline!");
        }
    }

    public PipelineExecutionCollection resumeExecution(String id)
            throws APIPWrongYmlFileOfConnectorException, APIPRetryMechanismException {

        log.info("Resuming pipeline execution with id " + id + "!");
        Optional<PipelineExecutionCollection> pipelineExecution
                = pipelineExecutionRepository.findById(id);

        if(pipelineExecution.isEmpty()) {
            log.error("Pipeline execution not found!");
            throw new APIPPipelineExecutionNotFoundException(id);
        }
        if(!pipelineExecution.get().getState().equals("paused")) {
            log.error("Pipeline execution not paused!");
            throw new APIPPipelineNotPausedException(id);
        }

        return workflowService.executePipelineSteps(id);
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
