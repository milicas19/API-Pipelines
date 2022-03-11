package com.example.projectfirst.pipelineExecution.services;

import com.example.projectfirst.pipelineExecution.PipelineExecutionCollection;
import com.example.projectfirst.pipelineExecution.PipelineExecutionRepository;
import com.example.projectfirst.exceptions.APIPPipelineExecutionNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class StateService {
    @Autowired
    private PipelineExecutionRepository pipelineExecutionRepository;

    public String checkState(String pipelineExeId) {
        log.info("Checking state!");
        return pipelineExecutionRepository.findById(pipelineExeId)
                .map(PipelineExecutionCollection::getState)
                .orElseThrow(()-> new APIPPipelineExecutionNotFoundException("Could not find pipeline execution with id " + pipelineExeId + "!"));
    }

    public void setStateAndDescription(String pipelineExeId, String state, String description) {
        log.info("Setting state: " + state + "!");
        Optional<PipelineExecutionCollection> pipelineExecutionOptional
                = pipelineExecutionRepository.findById(pipelineExeId);

        if(pipelineExecutionOptional.isPresent()){
            PipelineExecutionCollection pipelineExecution = pipelineExecutionOptional.get();
            pipelineExecution.setState(state);
            pipelineExecution.setDescription(description);
            pipelineExecutionRepository.save(pipelineExecution);
        }else{
            throw new APIPPipelineExecutionNotFoundException("Could not find pipeline execution with id " + pipelineExeId + "!");
        }
    }
}
