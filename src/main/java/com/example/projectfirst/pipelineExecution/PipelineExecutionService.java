package com.example.projectfirst.pipelineExecution;

import com.example.projectfirst.pipelineExecution.PipelineExecutionCollection;
import com.example.projectfirst.pipelineExecution.PipelineExecutionRepository;
import com.example.projectfirst.pipelineExecution.exception.PipelineExecutionNotFoundException;
import com.example.projectfirst.pipelineExecution.services.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public String executePipeline(String id){
        try {
            String pipelineExeId = workflowService.initiateExecution(id);
            return workflowService.executePipeline(pipelineExeId);
        }catch (IOException ex){
            return "Pipeline execution initiation failed!";
        }
    }

    public String resumeExecution(String id){
        Optional<PipelineExecutionCollection> pipelineExecution = pipelineExecutionRepository.findById(id);

        if(pipelineExecution.isEmpty())
            throw new PipelineExecutionNotFoundException(id);

        if(pipelineExecution.get().getState().equals("paused"))
            return "Pipeline with id: " + id + "is not paused";

        return workflowService.executePipeline(id);
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

}
