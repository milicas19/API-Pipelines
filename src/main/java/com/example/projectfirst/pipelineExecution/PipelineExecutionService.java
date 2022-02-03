package com.example.projectfirst.pipelineExecution;

import com.example.projectfirst.pipelineExecution.exception.PipelineExecutionFailedException;
import com.example.projectfirst.pipelineExecution.exception.PipelineExecutionNotFoundException;
import com.example.projectfirst.pipelineExecution.exception.PipelineNotPausedException;
import com.example.projectfirst.pipelineExecution.services.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PipelineExecutionService implements PipelineExecutionInterface{
    @Autowired
    private PipelineExecutionRepository pipelineExecutionRepository;
    @Autowired
    private WorkflowService workflowService;


    public List<PipelineExecutionCollection> fetchAllExecutions() {
        return pipelineExecutionRepository.findAll();
    }

    public PipelineExecutionCollection fetchExecution(String id) {
        return pipelineExecutionRepository.findById(id).
                orElseThrow(() -> new PipelineExecutionNotFoundException(id));
    }

    public List<PipelineExecutionCollection> fetchPausedExecutions() {
        List<PipelineExecutionCollection> pipelineExecutionList = new ArrayList<>();
        for(PipelineExecutionCollection pipelineExecution: pipelineExecutionRepository.findAll()){
            if(pipelineExecution.getState().equals("paused")){
                pipelineExecutionList.add(pipelineExecution);
            }
        }
        return pipelineExecutionList;
    }

    public PipelineExecutionCollection executePipeline(String id){
        try {
            String pipelineExeId = workflowService.initiateExecution(id);
            return workflowService.executePipeline(pipelineExeId);
        }catch (IOException ex){
            throw new PipelineExecutionFailedException("Pipeline execution initiation failed!");
        }
    }

    public PipelineExecutionCollection resumeExecution(String id){
        return pipelineExecutionRepository.findById(id)
                .map(pipelineExecution -> {
                    if(!pipelineExecution.getState().equals("paused")) {
                        throw new PipelineNotPausedException(id);
                    }
                    return workflowService.executePipeline(id);
                })
                .orElseThrow(() -> new PipelineExecutionNotFoundException(id));
    }

    public PipelineExecutionCollection deleteExecution(String id) {
        return pipelineExecutionRepository.findById(id)
                .map(pipelineExecution -> {
                    pipelineExecutionRepository.deleteById(id);
                    return pipelineExecution;
                })
                .orElseThrow(() -> new PipelineExecutionNotFoundException(id));
    }

    public void deleteExecutions() {
        pipelineExecutionRepository.deleteAll();
    }

}
