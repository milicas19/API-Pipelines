package com.example.projectfirst.pipelineExecution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PipelineExecutionService {
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

    public String executePipeline(String id) {
        String pipelineExeId = workflowService.initiateExecution(id);
        if(pipelineExeId == null) {
            return "Preparation error!";
        }
        else {
            return workflowService.executePipeline(pipelineExeId);
        }
    }

    public String deleteExecution(String id) {
        if(pipelineExecutionRepository.existsById(id)){
            pipelineExecutionRepository.deleteById(id);
            return "Successfully deleted!";
        }
        throw new PipelineExecutionNotFoundException(id);
    }

    public void deleteExecutions() {
        pipelineExecutionRepository.deleteAll();
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

    public String resumeExecution(String id) {
        if(pipelineExecutionRepository.existsById(id)){
            Optional<PipelineExecutionCollection> pipelineExecution = pipelineExecutionRepository.findById(id);
            if(pipelineExecution.get().getState().equals("paused"))
                return "Pipeline with id: " + id + "is not paused";
            return workflowService.executePipeline(id);
        }else
            throw new PipelineExecutionNotFoundException(id);
    }

}
